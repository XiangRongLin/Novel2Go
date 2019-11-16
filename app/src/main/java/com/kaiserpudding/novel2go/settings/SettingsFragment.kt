package com.kaiserpudding.novel2go.settings

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.kaiserpudding.novel2go.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val kindleEmailPreference = findPreference<EditTextPreference>("kindle_email")
        kindleEmailPreference?.summary = preferences.getString("kindle_email", "")
        kindleEmailPreference?.setOnPreferenceChangeListener { preference, newValue ->
            if (Patterns.EMAIL_ADDRESS.matcher(newValue as String).matches()) {
                kindleEmailPreference.summary = newValue
                true
            } else {
                val toast = Toast.makeText(
                    context,
                    getString(R.string.toast_invalid_email),
                    Toast.LENGTH_SHORT
                )
                toast.show()
                false
            }
        }
    }
}
