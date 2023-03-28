package com.github.masdaster.edma.fragments

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.masdaster.edma.R
import com.github.masdaster.edma.activities.LoginActivity
import com.github.masdaster.edma.activities.SettingsActivity
import com.github.masdaster.edma.databinding.FragmentCommanderStatusBinding
import com.github.masdaster.edma.models.*
import com.github.masdaster.edma.models.exceptions.DataNotInitializedException
import com.github.masdaster.edma.models.exceptions.FrontierAuthNeededException
import com.github.masdaster.edma.utils.*
import com.github.masdaster.edma.view_models.CommanderViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream
import kotlin.math.round

class CommanderStatusFragment : Fragment() {

    companion object {
        const val COMMANDER_STATUS_FRAGMENT = "commander_status_fragment"
    }

    private var frontierLoginNeeded: Boolean = false

    private var _binding: FragmentCommanderStatusBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommanderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommanderStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Click to system details on location name
        binding.locationContainer.setOnClickListener { onSystemNameClick() }

        //Swipe to refresh setup
        val listener = OnRefreshListener {
            binding.swipeContainer.isRefreshing = true
            this.refreshCommanderInformations()
        }
        binding.swipeContainer.setOnRefreshListener(listener)

        // Set temporary text
        binding.creditsTextView.text = resources.getString(R.string.credits, "?")
        binding.locationTextView.text = resources.getString(R.string.unknown)
        RankUtils.setTempContent(context, binding.federationRankLayout.root, getString(R.string.rank_federation))
        RankUtils.setTempContent(context, binding.empireRankLayout.root, getString(R.string.rank_empire))
        RankUtils.setTempContent(context, binding.combatRankLayout.root, getString(R.string.rank_combat))
        RankUtils.setTempContent(context, binding.tradeRankLayout.root, getString(R.string.rank_trading))
        RankUtils.setTempContent(context, binding.explorationRankLayout.root, getString(R.string.rank_exploration))
        RankUtils.setTempContent(context, binding.arenaRankLayout.root, getString(R.string.rank_arena))
        RankUtils.setTempContent(context, binding.mercenaryRankLayout.root, getString(R.string.rank_mercenary))
        RankUtils.setTempContent(context, binding.exobiologistRankLayout.root, getString(R.string.rank_exobiologist))

        // Hide views according to supported information from source
        val currentContext = context
        if (currentContext != null) {
            if (!CommanderUtils.hasCreditsData(currentContext)) {
                binding.creditsContainer.visibility = View.GONE
            }
            if (!CommanderUtils.hasPositionData(currentContext)) {
                binding.locationContainer.visibility = View.GONE
            }
            if (!CommanderUtils.hasCurrentShipData(currentContext)) {
                binding.currentShipCardView.visibility = View.GONE
            }
        }

        // Setup observers
        viewModel.getRanks().observe(viewLifecycleOwner, ::onRanksChange)
        viewModel.getCredits().observe(viewLifecycleOwner, ::onCreditsChange)
        viewModel.getPosition().observe(viewLifecycleOwner, ::onPositionChange)
        viewModel.getCurrentShip().observe(viewLifecycleOwner, ::onCurrentShipChange)
        viewModel.getCurrentLoadOut().observe(viewLifecycleOwner, ::onCurrentLoadOutChange)

        // Display message if no source, else fetch informations
        if (currentContext != null) {
            if (CommanderUtils.hasCommanderInformations(currentContext)) {
                startLoading()
                this.refreshCommanderInformations()
            } else {

                val dialog = MaterialAlertDialogBuilder(currentContext)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_commander_accounts_linked_error_message)
                    .setPositiveButton(R.string.no_commander_accounts_linked_error_settings_button) { d: DialogInterface, _: Int ->
                        d.dismiss()
                        val i = Intent(context, SettingsActivity::class.java)
                        startActivity(i)
                    }
                    .setNegativeButton(android.R.string.cancel) { d: DialogInterface, _: Int -> d.dismiss() }
                    .create()
                dialog.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onFrontierLoginNeeded() {
        // End loading and clear the data on the viewmodel has it's not valid without login
        viewModel.clearCachedData()
        endLoading()

        // Show dialog but only once
        synchronized(frontierLoginNeeded) {
            if (frontierLoginNeeded) {
                return
            }

            frontierLoginNeeded = true
            val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.login_again_dialog_title)
                .setMessage(R.string.login_again_dialog_text)
                .setPositiveButton(android.R.string.ok) { d: DialogInterface, _: Int ->
                    d.dismiss()
                    val i = Intent(context, LoginActivity::class.java)
                    startActivity(i)
                }
                .setNegativeButton(android.R.string.cancel) { d: DialogInterface, _: Int -> d.dismiss() }
                .setOnDismissListener {
                    it.dismiss()
                    frontierLoginNeeded = false
                }

            val myDialog = dialogBuilder.create()
            myDialog.show()
        }
    }

    private fun onSystemNameClick() {
        val text = binding.locationTextView.text.toString()
        MiscUtils.startIntentToSystemDetails(context, text)
    }

    private fun refreshCommanderInformations() {
        val currentContext = context
        if (currentContext != null) {
            val cmdrName = CommanderUtils.getCommanderName(currentContext)
            if (cmdrName.isNotEmpty()) {
                binding.commanderNameTextView.text = cmdrName
            }
        }

        viewModel.fetchCredits()
        viewModel.fetchPosition()
        viewModel.fetchRanks()
        viewModel.fetchCurrentShip()
        viewModel.fetchCurrentLoadOut()
    }

    private fun <T> handleResult(result: ProxyResult<T>, onSuccess: (ProxyResult<T>) -> Unit) {
        endLoading()

        if (result.error is FrontierAuthNeededException) {
            onFrontierLoginNeeded()
            return
        }

        if (result.data == null || result.error != null) {
            if (result.error !is DataNotInitializedException) {
                NotificationsUtils.displayGenericDownloadErrorSnackbar(activity)
            }
        } else {
            onSuccess(result)
        }
    }

    private fun onPositionChange(result: ProxyResult<CommanderPosition>) {
        handleResult(result) {
            if (result.data == null) {
                return@handleResult
            }
            binding.locationTextView.text = result.data.systemName
        }
    }

    private fun onCreditsChange(result: ProxyResult<CommanderCredits>) {
        handleResult(result) {
            if (result.data == null) {
                return@handleResult
            }

            val numberFormat = MathUtils.getNumberFormat(context)
            val amount: String = numberFormat.format(result.data.balance)
            if (result.data.loan > 0) {
                val loan: String = numberFormat.format(result.data.loan)
                binding.creditsTextView.text = resources.getString(
                    R.string.credits_with_loan,
                    amount, loan
                )
            } else {
                binding.creditsTextView.text = resources.getString(R.string.credits, amount)
            }
        }
    }

    private fun onRanksChange(result: ProxyResult<CommanderRanks>) {
        handleResult(result) {
            if (result.data == null) {
                return@handleResult
            }

            val ranks = result.data
            RankUtils.setContent(
                context,
                binding.federationRankLayout.root,
                getInaraRankLogo(13, 0),
                ranks.federation,
                getString(R.string.rank_federation)
            )
            RankUtils.setContent(
                context,
                binding.empireRankLayout.root,
                getInaraRankLogo(12, 0),
                ranks.empire,
                getString(R.string.rank_empire)
            )
            RankUtils.setContent(
                context,
                binding.combatRankLayout.root,
                getInaraRankLogo(1, ranks.combat.value),
                ranks.combat,
                getString(R.string.rank_combat)
            )
            RankUtils.setContent(
                context,
                binding.tradeRankLayout.root,
                getInaraRankLogo(2, ranks.trade.value),
                ranks.trade,
                getString(R.string.rank_trading)
            )
            RankUtils.setContent(
                context,
                binding.explorationRankLayout.root,
                getInaraRankLogo(3, ranks.explore.value),
                ranks.explore,
                getString(R.string.rank_exploration)
            )
            RankUtils.setContent(
                context,
                binding.arenaRankLayout.root,
                getInaraRankLogo(4, ranks.cqc.value),
                ranks.cqc,
                getString(R.string.rank_arena)
            )
            if(ranks.mercenary == null) {
                binding.mercenaryRankLayout.root.visibility = View.GONE
            } else{
                binding.mercenaryRankLayout.root.visibility = View.VISIBLE
                RankUtils.setContent(
                    context,
                    binding.mercenaryRankLayout.root,
                    getInaraRankLogo(5, ranks.mercenary.value),
                    ranks.mercenary,
                    getString(R.string.rank_mercenary)
                )
            }
            if(ranks.exobiologist == null) {
                binding.exobiologistRankLayout.root.visibility = View.GONE
            } else{
                binding.exobiologistRankLayout.root.visibility = View.VISIBLE
                RankUtils.setContent(
                    context,
                    binding.exobiologistRankLayout.root,
                    getInaraRankLogo(6, ranks.exobiologist.value),
                    ranks.exobiologist,
                    getString(R.string.rank_exobiologist)
                )
            }
        }
    }

    private fun onCurrentShipChange(result: ProxyResult<Ship>) {
        handleResult(result) {
            if (result.data == null) {
                return@handleResult
            }

            val shipInformation = result.data.information
            val shipState = result.data.state

            MiscUtils.loadShipImage(binding.currentShipImageView, shipInformation)
            binding.currentShipTitleTextView.text = shipInformation.model
            if (shipInformation.name != null && shipInformation.name != shipInformation.model) {
                binding.currentShipSubtitleTextView.visibility = View.VISIBLE
                binding.currentShipSubtitleTextView.text = shipInformation.name
            } else {
                binding.currentShipSubtitleTextView.visibility = View.GONE
            }
            binding.currentShipCockpitBreachedTextView.text = if(shipState.cockpitBreached) "yes" else "no"
            binding.currentShipShieldUpTextView.text = if(shipState.shieldUp) "yes" else "no"
            binding.currentShipHullHealthTextView.text = "${MathUtils.divAndRound(shipState.hullHealth,10000)}%"
            binding.currentShipIntegrityHealthTextView.text = "${MathUtils.divAndRound(1000000-shipState.integrityHealth,10000)}%"
            binding.currentShipPaintworkHealthTextView.text = "${MathUtils.divAndRound(1000000-shipState.paintworkHealth,10000)}%"
            binding.currentShipShieldHealthTextView.text = "${MathUtils.divAndRound(shipState.shieldHealth, 10000)}%"
            binding.currentShipOxygenRemainingTextView.text = "${MathUtils.divAndRound(shipState.oxygenRemaining, 1000)}s"
            binding.currentShipCardView.setOnClickListener {
                ByteArrayOutputStream().use { out ->
                    GZIPOutputStream(out).use {
                        it.write(result.data.raw.toString().encodeToByteArray())
                    }
                    val encodedShip = Base64.encode(out.toByteArray(), Base64.URL_SAFE + Base64.NO_PADDING + Base64.NO_WRAP).decodeToString().replace("=", "%3D")
                    startActivity(Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://edsy.org/#/I=$encodedShip")
                    ))
                }
            }
        }
    }

    private fun onCurrentLoadOutChange(result: ProxyResult<CommanderLoadOut>) {
        handleResult(result) {
            if (result.data == null) {
                return@handleResult
            }

            val loadOutInformation = result.data.information
            val loadOutState = result.data.state
            val suit = loadOutInformation.suit

            MiscUtils.loadLoadOutImage(binding.currentLoadOutImageView, suit)
            binding.currentLoadOutTitleTextView.text = suit.name
            if (loadOutInformation.name != null && loadOutInformation.name != suit.name) {
                binding.currentLoadOutSubtitleTextView.visibility = View.VISIBLE
                binding.currentLoadOutSubtitleTextView.text = loadOutInformation.name
            } else {
                binding.currentLoadOutSubtitleTextView.visibility = View.GONE
            }
            binding.currentLoadOutHullHealthTextView.text = "${MathUtils.divAndRound(loadOutState.hullHealth,10000)}%"
            binding.currentLoadOutOxygenRemainingTextView.text = "${MathUtils.divAndRound(loadOutState.oxygenRemaining, 1000)}s"
            binding.currentLoadOutEnergyTextView.text = "${round(loadOutState.energy*100).toInt()}%"
        }
    }

    fun endLoading() {
        binding.swipeContainer.post {
            binding.swipeContainer.isRefreshing = false
        }
    }

    private fun startLoading() {
        binding.swipeContainer.post {
            binding.swipeContainer.isRefreshing = true
        }
    }

    private fun getInaraRankLogo(type: Int, value: Int): String = context?.getString(R.string.inara_base)+"/images/ranks/$type/$value.png?v=2"
}