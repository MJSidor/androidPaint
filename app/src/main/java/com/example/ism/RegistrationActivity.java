package com.example.ism;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ism.ui.login.LoginActivity;

public class RegistrationActivity extends AppCompatActivity {

    DBHelper DBhelper;
    SQLiteDatabase DB;
    Button registerButton;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_registration);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        DBhelper = new DBHelper(this);
        DB = DBhelper.getWritableDatabase();

        registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });


    }

    private void register() {
        if (validate()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN1, username.getText().toString());
            values.put(DBHelper.COLUMN1, password.getText().toString());
            DB.insert(DBHelper.TABLE_NAME, null, values);
            Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);

        } else
            Toast.makeText(getApplicationContext(), "Please enter valid data", Toast.LENGTH_SHORT).show();

    }

    private boolean validate() {
        return username.length() > 4 && password.length() > 5;
    }

}
