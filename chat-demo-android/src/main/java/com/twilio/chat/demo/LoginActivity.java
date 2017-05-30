package com.twilio.chat.demo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.twilio.chat.ChatClient;
import com.twilio.chat.demo.BasicChatClient.LoginListener;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements LoginListener {

    private static final Logger logger = Logger.getLogger(LoginActivity.class);
    private static final String TAG = "LoginActivity";

    private static String DEFAULT_CLIENT_NAME;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog progressDialog;
    private Button login;
    private EditText clientNameTextBox,inputPassword;
    // FCM
    private CheckBox fcmAvailable;

    TextView txt_reset, txt_register;
    private FirebaseAuth auth;
    private Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        progress = new Progress(this);
        if (auth.getCurrentUser() != null) {

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String userName = sharedPreferences.getString("userName", DEFAULT_CLIENT_NAME);
            Log.e(TAG, "onCreate: "+userName );
            if(userName != null){

                String url = Uri.parse(BuildConfig.ACCESS_TOKEN_SERVICE_URL)
                        .buildUpon()
                        .appendQueryParameter("clientname", userName)
                        .build()
                        .toString();
                logger.d("url string : " + url);
                TwilioApplication.get().getBasicClient().login(userName, url, LoginActivity.this);
                return;
            }

        }

        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();
        progress = new Progress(this);


        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("userName", DEFAULT_CLIENT_NAME);

        this.clientNameTextBox = (EditText) findViewById(R.id.et_email);
        inputPassword = (EditText)findViewById(R.id.et_pwd);
        this.clientNameTextBox.setText(userName);
        txt_register = (TextView)findViewById(R.id.txt_ragister);
        txt_reset = (TextView)findViewById(R.id.txt_forget);


        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Registration_Screen.class));
            }
        });

        txt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        this.login = (Button) findViewById(R.id.btn_login);
        this.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String idChosen = clientNameTextBox.getText().toString();
                final String password = inputPassword.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (TextUtils.isEmpty(idChosen)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isValidEmaillId(idChosen)){

                    clientNameTextBox.setError("Invalid Email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] parts = idChosen.split("@");
                final String nickname = parts[0];
                Log.e(TAG, "onClick: "+nickname);

                sharedPreferences.edit().putString("userName", nickname).apply();

                progress.createDialog(false);
                progress.DialogMessage("Login...");
                progress.showDialog();

                //authenticate user
                auth.signInWithEmailAndPassword(idChosen, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progress.hideDialog();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed) + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
//                                    Intent intent = new Intent(Login_Screen.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    finish();
                                    Log.e(TAG, "Suceess login " );
                                    String url = Uri.parse(BuildConfig.ACCESS_TOKEN_SERVICE_URL)
                                            .buildUpon()
                                            .appendQueryParameter("clientname", nickname)
                                            .build()
                                            .toString();
                                    logger.d("url string : " + url);
                                    TwilioApplication.get().getBasicClient().login(nickname, url, LoginActivity.this);
                                }
                            }
                        });



            }
        });


        fcmAvailable = (CheckBox) findViewById(R.id.fcmcxbx);

        if (checkPlayServices()) {
            fcmAvailable.setChecked(true);
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            showAboutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("About")
                .setMessage("Version: " + ChatClient.getSdkVersion())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog aboutDialog = builder.create();
        aboutDialog.show();
    }

    @Override
    public void onLoginStarted() {
        logger.d("Log in started");
        progressDialog = ProgressDialog.show(this, "", "Logging in. Please wait...", true);
    }

    @Override
    public void onLoginFinished() {
        progressDialog.dismiss();
        Intent intent = new Intent(this, ChannelActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginError(String errorMessage) {
        progressDialog.dismiss();
        TwilioApplication.get().showToast("Error logging in : " + errorMessage, Toast.LENGTH_LONG);
    }

    @Override
    public void onLogoutFinished() {
        TwilioApplication.get().showToast("Log out finished");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                logger.i("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }



}
