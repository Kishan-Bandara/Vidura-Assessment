package com.example.vidura_assessment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {
    TextView tRegister;
    Button LogInBtn;
    FirebaseAuth fAuth;
    EditText nEmail, nPassword;
    CallbackManager callbackManager;
    Button faceBookLogIn;
    FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());

        tRegister = findViewById(R.id.registerText);
        LogInBtn = findViewById(R.id.logInBtn);
        fAuth = FirebaseAuth.getInstance();
        nPassword = findViewById(R.id.password);
        nEmail = findViewById(R.id.Email);

        final String TAG = "FacebookAuthentication";

        callbackManager = CallbackManager.Factory.create();
        LoginButton faceBookLogIn = findViewById(R.id.loginButtonFb);
        faceBookLogIn.setReadPermissions("email", "public_profile");
        faceBookLogIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            public void onSuccess(LoginResult loginResult){
                Log.d(TAG, "OnSuccess" +loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            public void onCancel(){
                Log.d(TAG, "OnCancel" );
            }

            public void onError(FacebookException error){
                Log.d(TAG, "OnError" +error);
            }
        });



        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = nEmail.getText().toString().trim();
                String password = nPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    nEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    nPassword.setError("Password is required");
                    return;
                }

                if(password.length()<6){
                    nPassword.setError("Password must be less than 6 Characters");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful()) {
                            Toast.makeText(LogIn.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else
                        {
                            Toast.makeText(LogIn.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                tRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),Register.class));
                        finish();
                    }
                });

            }

            public void onStart() {
                super.onStart();
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = fAuth.getCurrentUser();
                updateUI(currentUser);
            }

            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                // Pass the activity result back to the Facebook SDK
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }

            private void handleFacebookAccessToken(AccessToken token) {
                Log.d(TAG, "handleFacebookAccessToken:" + token);

                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                fAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = fAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    Toast.makeText(LogIn.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }

                        });

            }

        });

}




}