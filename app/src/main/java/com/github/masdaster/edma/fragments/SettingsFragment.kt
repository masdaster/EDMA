package com.github.masdaster.edma.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.github.masdaster.edma.R
import com.github.masdaster.edma.activities.LoginActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)
        initCmdrPreferences()
    }

    private fun initCmdrPreferences() {
        // Bind help preference
        val helpPreference = findPreference<Preference>(getString(R.string.settings_cmdr_help))
        if (helpPreference != null) {
            helpPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(getString(R.string.commander_help_url))
                startActivity(browserIntent)
                true
            }
        }

        // Bind preferences summary to values
        val pref: Preference? = findPreference(getString(R.string.settings_cmdr_edsm_username))
        if (pref != null) {
            bindPreferenceSummaryToValue(pref)
        }

        val frontierPreference =
            findPreference<Preference>(getString(R.string.settings_cmdr_frontier_oauth))
        frontierPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(context, LoginActivity::class.java)
            activity?.startActivityForResult(i, FRONTIER_LOGIN_REQUEST_CODE)
            true
        }
    }

    companion object {
        private const val FRONTIER_LOGIN_REQUEST_CODE = 999

        private val preferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference, value: Any ->
                val stringValue = value.toString()
                when (preference) {
                    // For list preferences, look up the correct display value in the preference's 'entries' list.
                    is ListPreference -> {
                        val index = preference.findIndexOfValue(stringValue)

                        // Set the summary to reflect the new value.
                        preference.setSummary(if (index >= 0) preference.entries[index] else preference.summary)
                    }
                    // For all other preferences, set the summary to the value's simple string representation.
                    else -> {
                        preference.summary = stringValue
                    }
                }
                true
            }

        private fun bindPreferenceSummaryToValue(preference: Preference) { // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = preferenceChangeListener

            // Trigger the listener immediately with the preference's current value.
            preferenceChangeListener.onPreferenceChange(
                preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }
    }
}