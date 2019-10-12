package com.example.ism.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.ism.DBHelper;
import com.example.ism.MainActivity;
import com.example.ism.MyApplication;
import com.example.ism.data.model.LoggedInUser;
import com.example.ism.ui.login.LoginActivity;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    public Result<LoggedInUser> login(String username, String password) {

        if (checkIfUserExists(username, password)) {
            LoggedInUser admin =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "admin");
            return new Result.Success<>(admin);
        } else {
            return new Result.Error(new IOException("Invalid credentials"));
        }

    }

    public void logout() {
        // TODO: revoke authentication
    }

    public static boolean checkIfUserExists(String username, String password) {

        DBHelper DBhelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = DBhelper.getReadableDatabase();

        String Query = "Select * from " + DBhelper.TABLE_NAME + " where " + DBhelper.COLUMN1 + " = " + username + " and " + DBhelper.COLUMN2 + " = " + password;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
