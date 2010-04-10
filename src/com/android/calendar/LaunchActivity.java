/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar;

import com.google.android.googlelogin.GoogleLoginServiceConstants;
import com.google.android.googlelogin.GoogleLoginServiceHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Gmail;
import android.provider.Calendar.Calendars;
import android.content.ContentResolver;
import android.database.Cursor;
import android.content.ContentValues;
import android.text.format.Time;

public class LaunchActivity extends Activity {
    
    // An arbitrary constant to pass to the GoogleLoginHelperService
    private static final int GET_ACCOUNT_REQUEST = 1;

    private static final String[] PROJECTION= new String[]{Calendars._ID,};
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        // Our UI is not something intended for the user to see.  We just
        // stick around until we can figure out what to do next based on
        // the current state of the system.
        setVisible(false);
        
    	final ContentResolver cr = getContentResolver();
    	Cursor c = cr.query(Calendars.CONTENT_URI, PROJECTION,
                          null, null, Calendars.DEFAULT_SORT_ORDER);
    	if ((c == null) || c.getCount()== 0) {
    		ContentValues cv = new ContentValues();
  		cv.put(Calendars.HIDDEN, 0);
  		cv.put(Calendars.DISPLAY_NAME, "default");
  		cv.put(Calendars.NAME, "default");
  		cv.put(Calendars.ACCESS_LEVEL, 700);
  		cv.put(Calendars.SELECTED, 1);
  		cv.put(Calendars.TIMEZONE, Time.getCurrentTimezone());
  		cv.put(Calendars.SYNC_EVENTS, 1);

		cr.insert(Calendars.CONTENT_URI, cv);
		c.close();
	}
	final String account = "Hi";
	onAccountsLoaded(account);
    }
    
    private void onAccountsLoaded(String account) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String startActivity = prefs.getString(CalendarPreferenceActivity.KEY_START_VIEW,
                CalendarPreferenceActivity.DEFAULT_START_VIEW);
            
        // Get the data for from this intent, if any
        Intent myIntent = getIntent();
        Uri myData = myIntent.getData();
            
        // Set up the intent for the start activity
        Intent intent = new Intent();
        if (myData != null) {
            intent.setData(myData);
        }
        intent.setClassName(this, startActivity);
        startActivity(intent);
        finish();
    }
    
}
