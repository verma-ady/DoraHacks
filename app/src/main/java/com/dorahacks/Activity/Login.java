package com.dorahacks.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.dorahacks.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    LoginButton loginButton;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    Button buttonLogInFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());

        sharedPreferences = getApplicationContext().getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean("login", false)){
            Intent intent = new Intent(Login.this, Navigation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        buttonLogInFB = (Button) findViewById(R.id.button_login_fb);
        buttonLogInFB.setOnClickListener(this);

    }

    private void updateWithToken( AccessToken currentAccessToken ){
        if(currentAccessToken!=null){
            Log.v("MyApp", getClass().toString() + "updateWithToken:If(Token NonNull): " + currentAccessToken.toString() );

            GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.v("MyApp",getClass().toString() + response.toString());
                    // Get facebook data from login
//                    Bundle bFacebookData = getFacebookData(object);
                    try {
                        Log.v("MyApp", getClass().toString() + object.toString()+ "name " + object.getString("first_name"));

//                        editor.putString("fname", object.getString("first_name"));
//                        editor.putString("lname", object.getString("last_name"));
//                        editor.putString("gender", CapitalizeWord(object.getString("gender")));
//                        editor.putString("email", object.getString("email"));
                        editor.putString("fbid", object.getString("id"));
                    } catch (JSONException e) {
                        Log.v("MyApp", getClass().toString() + "LoginJSON");
//                        Toast.makeText(getApplicationContext(), "Unable to get your EMail-ID", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
//                    editor.commit();
                    editor.putBoolean("login", true);
                    editor.apply();
                    Intent intent = new Intent(Login.this, Navigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            Bundle parameters = new Bundle();
            // Parámetros que pedimos a facebook
            parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();

        } else {
            Log.v("MyApp", getClass().toString() + "updateWithToken:Else(Token Null)");
//            dialog.dismiss();
        }
    }//updatewithtoken

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==64206) {
            Log.v("MyApp", getClass().toString() + " onActivityResult:Facebook");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
                case R.id.button_login_fb:
                    Log.v("MyApp", "Button Login Click");
                    loginButton = new LoginButton(Login.this);
                    callbackManager = CallbackManager.Factory.create();
                    loginButton.setReadPermissions(Arrays.asList("public_profile", "email","user_friends"));
                    loginButton.registerCallback(callbackManager, callback);
                    accessTokenTracker = new AccessTokenTracker() {
                        @Override
                        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                            updateWithToken(newAccessToken);
                        }
                    };
                    loginButton.performClick();
                    break;
        }
    }
}
