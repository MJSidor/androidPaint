package com.example.ism;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ism.ui.login.LoginActivity;

public class RegistrationActivity extends AppCompatActivity {

    DBHelper DBhelper;
    SQLiteDatabase DB;
    Button registerButton;
    EditText username;
    EditText password;
    TextView signInRedirect;

    private int VALIDATION_STATE_USERNAME_TOO_SHORT = 1;
    private int VALIDATION_STATE_PASSWORD_TOO_SHORT = 2;
    private int VALIDATION_STATE_USERNAME_AND_PASSWORD_TOO_SHORT = 3;
    private int VALIDATION_STATE_OK = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_registration);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        DBhelper = new DBHelper(this);
        DB = DBhelper.getWritableDatabase();

        registerButton = findViewById(R.id.register);

        signInRedirect = findViewById(R.id.textView2);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        signInRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void register() {
        if (validate() == this.VALIDATION_STATE_OK) {
            if (checkIfUserExists(username.getText().toString())) {
                Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN1, username.getText().toString());
            values.put(DBHelper.COLUMN2, password.getText().toString());
            DB.insert(DBHelper.TABLE_NAME, null, values);
            Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        } else
            if (validate() == this.VALIDATION_STATE_USERNAME_TOO_SHORT) {
                Toast.makeText(getApplicationContext(), "Username must be at least 4 characters long", Toast.LENGTH_SHORT).show();
            }
            if (validate() == this.VALIDATION_STATE_PASSWORD_TOO_SHORT) {
                Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            }
            if (validate() == this.VALIDATION_STATE_USERNAME_AND_PASSWORD_TOO_SHORT) {
                Toast.makeText(getApplicationContext(), "Username must be at least 4 characters long, password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            }
    }

    private int validate() {
        if (username.length() >= 4 && password.length() >= 6) return this.VALIDATION_STATE_OK;
        if (username.length() < 4 && password.length() >= 6) return this.VALIDATION_STATE_USERNAME_TOO_SHORT;
        if (username.length() < 4 && password.length() < 6) return this.VALIDATION_STATE_USERNAME_AND_PASSWORD_TOO_SHORT;
        if (username.length() >= 4 && password.length() < 6) return this.VALIDATION_STATE_PASSWORD_TOO_SHORT;

        return 0;
    }

    public static boolean checkIfUserExists(String username) {

        DBHelper DBhelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = DBhelper.getReadableDatabase();

        String[] columns = { DBhelper.COLUMN1  };
        String selection = DBhelper.COLUMN1 + " =?";
        String[] selectionArgs = { username };
        String limit = "1";

        Cursor cursor = db.query(DBhelper.TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}
