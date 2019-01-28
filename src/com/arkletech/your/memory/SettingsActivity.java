package com.arkletech.your.memory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	static final String TAG="MyAndroidDebugTAG";
	static final int SELECT_IMAGE=1;
	
	static public final String PREF_NAME="AppPreferences";
//	static public final String PREF_USE_CUSTOM_PICTURE=getResources().getString(R.string.pref_use_custom_picture);
//	static public final String PREF_BACKGROUND_IMAGE=getResources().getString(R.string.pref_background_image);
//	static public final String PREF_GAME_TYPE=getResources().getString(R.string.pref_game_type);

	ListPreference lp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Settings - onCreate()");
	
		PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(PREF_NAME);
        addPreferencesFromResource(R.xml.preferences);

		lp = (ListPreference) getPreferenceScreen().findPreference(getResources().getString(R.string.pref_game_type));
		lp.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
		Log.d(TAG, "onPreferenceTreeClick("+screen+", "+preference+")");
		if (preference.getKey().equals(getResources().getString(R.string.pref_background_image)))	{
			Log.d(TAG, "Get custom picture from user's images library ...");
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_IMAGE);
			
			return true;
		}
		else if (preference.getKey().equals(getResources().getString(R.string.pref_use_custom_picture))) {
			Log.d(TAG, "Enable/Disable Use Custom Picture ...");
			
		}
		// TODO Auto-generated method stub
		return super.onPreferenceTreeClick(screen, preference);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult("+requestCode+", "+resultCode+", "+data+")");
		
		if (requestCode == SELECT_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String imgPath = getPath(selectedImage);
//				String imgPath = selectedImage.toString();
				Log.d(TAG, "The Uri for selected image is:"+selectedImage);
				Log.d(TAG, "The path for selected image is:"+imgPath);
//				SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
//				Log.d(TAG, "Save the imgPath ("+imgPath+") to preferences ("+s+") ...");
//				s.edit().putString("BackgroundImgPref", imgPath);
//				s.edit().commit();
				SharedPreferences.Editor ed = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
				Log.d(TAG, "Save the imgPath ("+imgPath+") to preferences ("+ed+") ...");
				ed.putString(getResources().getString(R.string.pref_background_image), imgPath);
				ed.commit();
			} 
		}
	}

	// Convert the path from URI to real file path
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		// TODO Auto-generated method stub
		Log.d(TAG, "SettingsActivity - onPreferenceChange("+pref+","+newValue+")");

		if (pref == lp) {
			if (!newValue.toString().equals(Integer.toString(YourMemoryActivity.gameType))) {
				YourMemoryActivity.gameType = -1;
			}
			return true;
		}
		
		return false;
	}

}
