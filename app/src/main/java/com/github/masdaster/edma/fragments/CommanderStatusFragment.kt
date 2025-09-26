package com.github.masdaster.edma.fragments

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.masdaster.edma.R
import com.github.masdaster.edma.activities.LoginActivity
import com.github.masdaster.edma.activities.SettingsActivity
import com.github.masdaster.edma.databinding.FragmentCommanderStatusBinding
import com.github.masdaster.edma.models.CommanderCredits
import com.github.masdaster.edma.models.CommanderFleet
import com.github.masdaster.edma.models.CommanderLoadout
import com.github.masdaster.edma.models.CommanderLoadoutWeapon
import com.github.masdaster.edma.models.CommanderPosition
import com.github.masdaster.edma.models.CommanderRanks
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.Ship
import com.github.masdaster.edma.models.exceptions.DataNotInitializedException
import com.github.masdaster.edma.models.exceptions.FrontierAuthNeededException
import com.github.masdaster.edma.utils.CommanderUtils
import com.github.masdaster.edma.utils.InternalNamingUtils
import com.github.masdaster.edma.utils.MathUtils
import com.github.masdaster.edma.utils.MiscUtils
import com.github.masdaster.edma.utils.NotificationsUtils
import com.github.masdaster.edma.utils.RankUtils
import com.github.masdaster.edma.utils.SettingsUtils
import com.github.masdaster.edma.view_models.CommanderViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

class CommanderStatusFragment : Fragment() {

    companion object {
        const val COMMANDER_STATUS_FRAGMENT = "commander_status_fragment"
    }

    private var frontierLoginNeeded: Boolean = false
    private val frontierLoginNeededLock = Any()

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
            this.refreshCommanderStatus()
        }
        binding.swipeContainer.setOnRefreshListener(listener)

        // Set temporary text
        binding.creditsTextView.text = resources.getString(R.string.credits, "?")
        binding.locationTextView.text = resources.getString(R.string.unknown)
        RankUtils.setTempContent(
            context, binding.federationRankLayout.root,
            getString(R.string.rank_federation)
        )
        RankUtils.setTempContent(
            context,
            binding.empireRankLayout.root,
            getString(R.string.rank_empire)
        )
        RankUtils.setTempContent(
            context,
            binding.combatRankLayout.root,
            getString(R.string.rank_combat)
        )
        RankUtils.setTempContent(
            context,
            binding.tradeRankLayout.root,
            getString(R.string.rank_trading)
        )
        RankUtils.setTempContent(
            context, binding.explorationRankLayout.root,
            getString(R.string.rank_exploration)
        )
        RankUtils.setTempContent(
            context,
            binding.arenaRankLayout.root,
            getString(R.string.rank_arena)
        )
        RankUtils.setTempContent(
            context,
            binding.exobiologistRankLayout.root,
            getString(R.string.rank_exobiologist)
        )
        RankUtils.setTempContent(
            context,
            binding.mercenaryRankLayout.root,
            getString(R.string.rank_mercenary)
        )

        // Hide views according to supported content from source
        val currentContext = context
        if (currentContext != null) {
            if (!CommanderUtils.hasCreditsData(currentContext)) {
                binding.creditsContainer.visibility = View.GONE
            } else {
                binding.creditsContainer.visibility = View.VISIBLE
            }

            if (!CommanderUtils.hasPositionData(currentContext)) {
                binding.locationContainer.visibility = View.GONE
            } else {
                binding.locationContainer.visibility = View.VISIBLE
            }

            if (!CommanderUtils.hasCurrentShipData(currentContext)) {
                binding.currentShipCardView.visibility = View.GONE
            }
        }

        // Setup observers
        viewModel.getRanks().observe(viewLifecycleOwner) {
            onRanksChange(it)
        }
        viewModel.getCredits().observe(viewLifecycleOwner) {
            onCreditsChange(it)
        }
        viewModel.getPosition().observe(viewLifecycleOwner) {
            onPositionChange(it)
        }
        viewModel.getCurrentShip().observe(viewLifecycleOwner) {
            onCurrentShipChange(it)
        }
        viewModel.getFleet().observe(viewLifecycleOwner) {
            onFleetChange(it)
        }
        viewModel.getLoadout().observe(viewLifecycleOwner) {
            onLoadoutChange(it)
        }

        // Display message if no source, else fetch content
        if (currentContext != null) {
            if (CommanderUtils.hasCommanderStatus(currentContext)) {
                startLoading()
                this.refreshCommanderStatus()
            } else {

                val dialog = MaterialAlertDialogBuilder(currentContext)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_commander_accounts_linked_error_message)
                    .setPositiveButton(R.string.no_commander_accounts_linked_error_settings_button) { d: DialogInterface, _: Int ->
                        d.dismiss()
                        val i = Intent(context, SettingsActivity::class.java)
                        startActivity(i)
                    }
                    .setNegativeButton(
                        android.R.string.cancel
                    ) { d: DialogInterface, _: Int -> d.dismiss() }
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
        synchronized(frontierLoginNeededLock) {
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
                .setNegativeButton(
                    android.R.string.cancel
                ) { d: DialogInterface, _: Int -> d.dismiss() }
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

    private fun refreshCommanderStatus() {
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
        viewModel.fetchCurrentLoadout()

        // We do not really need fleet/loadouts except to preload for other tab and to display auth popup if needed
        viewModel.fetchFleet()
        viewModel.fetchAllLoadouts()
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

    private fun onFleetChange(result: ProxyResult<CommanderFleet>) {
        handleResult(result) {}
    }

    private fun setLoadoutWeaponDisplay(
        weapon: CommanderLoadoutWeapon?,
        labelTextView: TextView,
        textView: TextView
    ) {
        if (weapon != null) {
            labelTextView.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE

            if (weapon.magazineName != null) {
                textView.text = getString(
                    R.string.weapon_display,
                    weapon.name,
                    weapon.magazineName
                )
            } else {
                textView.text = getString(
                    R.string.weapon_display_without_magazine,
                    weapon.name
                )
            }

        } else {
            labelTextView.visibility = View.GONE
            textView.visibility = View.GONE
        }
    }

    private fun onLoadoutChange(result: ProxyResult<CommanderLoadout>) {
        // Don't display if not enabled
        if (!SettingsUtils.getBoolean(
                context,
                getString(R.string.settings_cmdr_loadout_display_enable),
                true
            )
        ) {
            binding.currentLoadoutLayout.loadoutContainer.visibility = View.GONE
            return
        }

        handleResult(result) {
            if (result.data == null) {
                return@handleResult
            }

            if (result.data.hasLoadout) {
                binding.currentLoadoutLayout.loadoutContainer.visibility = View.VISIBLE
                binding.currentLoadoutLayout.suitTextView.text = result.data.suitName
                binding.currentLoadoutLayout.suitGradeRatingBar.progress = result.data.suitGrade

                // Weapons
                setLoadoutWeaponDisplay(
                    result.data.firstPrimaryWeapon,
                    binding.currentLoadoutLayout.firstPrimaryWeaponLabelTextView,
                    binding.currentLoadoutLayout.firstPrimaryWeaponTextView
                )
                setLoadoutWeaponDisplay(
                    result.data.secondPrimaryWeapon,
                    binding.currentLoadoutLayout.secondaryPrimaryWeaponLabelTextView,
                    binding.currentLoadoutLayout.secondaryPrimaryWeaponTextView
                )
                setLoadoutWeaponDisplay(
                    result.data.secondaryWeapon,
                    binding.currentLoadoutLayout.secondaryWeaponLabelTextView,
                    binding.currentLoadoutLayout.secondarWeaponTextView
                )

            } else {
                binding.currentLoadoutLayout.loadoutContainer.visibility = View.GONE
            }
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
                context, binding.federationRankLayout.root, R.drawable.elite_federation,
                ranks.federation, getString(R.string.rank_federation)
            )
            RankUtils.setContent(
                context, binding.empireRankLayout.root, R.drawable.elite_empire,
                ranks.empire, getString(R.string.rank_empire)
            )
            RankUtils.setContent(
                context, binding.combatRankLayout.root,
                InternalNamingUtils.getCombatLogoId(ranks.combat.value), ranks.combat,
                getString(R.string.rank_combat)
            )
            RankUtils.setContent(
                context, binding.tradeRankLayout.root,
                InternalNamingUtils.getTradeLogoId(ranks.trade.value), ranks.trade,
                getString(R.string.rank_trading)
            )
            RankUtils.setContent(
                context, binding.explorationRankLayout.root,
                InternalNamingUtils.getExplorationLogoId(ranks.explore.value),
                ranks.explore, getString(R.string.rank_exploration)
            )
            RankUtils.setContent(
                context, binding.arenaRankLayout.root,
                InternalNamingUtils.getCqcLogoId(ranks.cqc.value), ranks.cqc,
                getString(R.string.rank_arena)
            )

            if (ranks.exobiologist != null) {
                RankUtils.setContent(
                    context, binding.exobiologistRankLayout.root,
                    R.drawable.rank_placeholder, ranks.exobiologist,
                    getString(R.string.rank_exobiologist)
                )
            }
            if (ranks.mercenary != null) {
                RankUtils.setContent(
                    context, binding.mercenaryRankLayout.root,
                    R.drawable.rank_placeholder, ranks.mercenary,
                    getString(R.string.rank_mercenary)
                )
            }
        }
    }

    private fun onCurrentShipChange(result: ProxyResult<Ship>) {
        // Don't display if not enabled
        if (!SettingsUtils.getBoolean(
                context,
                getString(R.string.settings_ship_state_display_enable),
                true
            )
        ) {
            binding.currentShipStateLayout.shipStateContainer.visibility = View.GONE
            return
        }

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
            binding.currentShipStateLayout.shipCockpitBreachedTextView.text = if(shipState.cockpitBreached) "yes" else "no"
            binding.currentShipStateLayout.shipShieldUpTextView.text = if(shipState.shieldUp) "yes" else "no"
            binding.currentShipStateLayout.shipHullHealthTextView.text = "${MathUtils.divAndRound(shipState.hullHealth,10000)}%"
            binding.currentShipStateLayout.shipIntegrityHealthTextView.text = "${MathUtils.divAndRound(1000000-shipState.integrityHealth,10000)}%"
            binding.currentShipStateLayout.shipPaintworkHealthTextView.text = "${MathUtils.divAndRound(1000000-shipState.paintworkHealth,10000)}%"
            binding.currentShipStateLayout.shipShieldHealthTextView.text = "${MathUtils.divAndRound(shipState.shieldHealth, 10000)}%"
            binding.currentShipStateLayout.shipOxygenRemainingTextView.text = "${MathUtils.divAndRound(shipState.oxygenRemaining, 1000)}s"
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
}