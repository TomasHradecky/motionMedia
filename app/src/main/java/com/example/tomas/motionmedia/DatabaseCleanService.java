package com.example.tomas.motionmedia;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Tomas Hradecky on 24.12.2016.
 */
public class DatabaseCleanService extends IntentService {

    public DatabaseCleanService() {
        super("DatabaseCleanService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Database db = new Database(this);
            db.clearSkippedWeek();
            Log.i("TAG", "Week skipped songs were cleared");
        } catch (Exception e){
            e.printStackTrace();
            Log.e("TAG", "No week skipped songs were cleared");
        }
    }
}
