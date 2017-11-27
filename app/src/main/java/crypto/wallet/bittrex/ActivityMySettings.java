package crypto.wallet.bittrex;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Set;


public class ActivityMySettings extends AppCompatPreferenceActivity {
    private static final String TAG = ActivityMySettings.class.getSimpleName();
    public static final String LOCATION_INTERVAL_KEY="lp_location_interval";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences); depricated
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            addPreferencesFromResource(R.xml.preferences); //TODO if this test and implement other logic like fragment has
//        }else {
           // PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
            getFragmentManager().beginTransaction().replace(android.R.id.content,  new PrefsFragment()).commit();
//        }


    }


    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        private static final String TAG = PrefsFragment.class.getSimpleName();
       // ActivityMySettings activityMySetting;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.preferences, false);
            addPreferencesFromResource(R.xml.preferences);
            updateALLSummary(getPreferenceManager().getSharedPreferences());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        //For nicer summary info
        private void updateALLSummary(SharedPreferences sharedPreferences) {
           Set<String> keys= sharedPreferences.getAll().keySet();
            Preference connectionPref;
            for (String key:keys) {
                connectionPref= findPreference(key);
                setSummary(sharedPreferences,connectionPref,key);
            }
        }

        //Helper for updating settings summary
        private void setSummary(SharedPreferences sharedPreferences, Preference connectionPref, String key) {
            if (connectionPref==null) return;
            Log.i(TAG, "sharedPreferences key:"+" "+key);
            if (connectionPref instanceof EditTextPreference) {
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
            } else {
                if (connectionPref instanceof CheckBoxPreference) {
                    if (sharedPreferences.getBoolean(key, true))
                        connectionPref.setSummary("True");
                    else
                        connectionPref.setSummary("False");
                }
            }
        }

        //Settings has changed! What to do?
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference connectionPref = findPreference(key);
            if (connectionPref == null) {
                Log.e(TAG, "connectionPref is null");
                return;
            }
            setSummary(sharedPreferences,connectionPref,key);

        }


    }
}
