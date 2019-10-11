package com.example.ism.data;

import com.example.ism.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        if (username.equals("admin" ) && password.equals("123456")) {
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
}
