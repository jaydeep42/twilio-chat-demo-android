//package com.twilio.chat.demo;
//
//import android.accounts.Account;
//import android.accounts.AccountManager;
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.LinkedList;
//import java.util.List;
//
//public class Login_Screen extends Activity {
//
//    TextView btnReset, btnSignup;
//    private EditText inputEmail, inputPassword;
//    private FirebaseAuth auth;
//    private Progress progress;
//    private Button btnLogin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //Get Firebase auth instance
//        auth = FirebaseAuth.getInstance();
//
//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(Login_Screen.this, LoginActivity.class));
//            finish();
//        }
//        setContentView(R.layout.activity_login__screen);
//
//        inputEmail = (EditText) findViewById(R.id.et_email);
//        inputPassword = (EditText) findViewById(R.id.et_pwd);
//        btnLogin = (Button) findViewById(R.id.btn_login);
//        btnSignup = (TextView) findViewById(R.id.txt_ragister);
//        btnReset = (TextView) findViewById(R.id.txt_forget);
//        progress = new Progress(this);
//
//        auth = FirebaseAuth.getInstance();
//
//
//        btnSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Login_Screen.this, Registration_Screen.class));
//            }
//        });
//
//        btnReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Login_Screen.this, ResetPasswordActivity.class));
//            }
//        });
//
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = inputEmail.getText().toString();
//                final String password = inputPassword.getText().toString();
//
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                progress.createDialog(false);
//                progress.DialogMessage("Login...");
//                progress.showDialog();
//
//                //authenticate user
//                auth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(Login_Screen.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                // If sign in fails, display a message to the user. If sign in succeeds
//                                // the auth state listener will be notified and logic to handle the
//                                // signed in user can be handled in the listener.
//                                progress.hideDialog();
//                                if (!task.isSuccessful()) {
//                                    // there was an error
//                                    if (password.length() < 6) {
//                                        inputPassword.setError(getString(R.string.minimum_password));
//                                    } else {
//                                        Toast.makeText(Login_Screen.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//                                    }
//                                } else {
//                                    Intent intent = new Intent(Login_Screen.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }
//                        });
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        progress.hideDialog();
//    }
//
//
//    }
