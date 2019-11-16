package com.kaiserpudding.novel2go

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val kindleEmailPreference = findPreference<EditTextPreference>("kindle_email")
        kindleEmailPreference?.summary = preferences.getString("kindle_email", "")
    }
}
