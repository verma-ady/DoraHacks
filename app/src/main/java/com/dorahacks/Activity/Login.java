package com.dorahacks.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String urlS;
    ProgressDialog progressDialog;
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
//    Map<String, String> params = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        urlS = getResources().getString(R.string.website)+"login/";
        sharedPreferences = getApplicationContext().getSharedPreferences("Login", getApplicationContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

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
                        Log.v("MyApp", getClass().toString() + object.toString() + "name " + object.getString("first_name"));

                        editor.putString("fname", object.getString("first_name"));
                        editor.putString("lname", object.getString("last_name"));
                        editor.putString("gender", object.getString("gender"));
                        editor.putString("fbid", object.getString("id"));
                        editor.putString("email", object.getString("email"));

                    } catch (JSONException e) {
                        Log.v("MyApp", getClass().toString() + "LoginJSON");
//                        Toast.makeText(getApplicationContext(), "Unable to get your EMail-ID", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
//                    editor.commit();
                    editor.apply();
                    progressDialog.show();
                    SaveUser saveUser = new SaveUser();
                    saveUser.execute();
//
//                    volleyPOST(getResources().getString(R.string.website)+"login/");
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

    public class SaveUser extends AsyncTask<Void, Void, String > {

        //        String LOG_CAT = "MyApp";
        @Override
        protected String doInBackground(Void... params) {
            String error=null;

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            URL url = null;
            try {
                url= new URL(urlS);
//                params.put("fbid", sharedPreferences.getString("fbid", null) );
//                    params.put("fname",sharedPreferences.getString("fname", null) );
//                    params.put("lname", sharedPreferences.getString("lname", null));
//                    params.put("email", sharedPreferences.getString("email", null));
//                    params.put("gender", sharedPreferences.getString("gender", null));
                StringBuilder postDataString = new StringBuilder();
                postDataString.append(URLEncoder.encode("fbid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("fbid", null)));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("fname"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("fname", null)));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("lname"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("lname", null)));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("email"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("email", null)));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("gender"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("gender", null)));

                Log.v("MyApp", getClass().toString() + "post data " + postDataString);
                byte[] postData = postDataString.toString().getBytes("UTF-8");

                int postDataLength = postData.length;

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(postDataLength));
                urlConnection.setRequestProperty("Content-Language", "en-US");
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream==null){
                    return "null_inputstream";
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line ;

                while ( (line=bufferedReader.readLine())!=null ){
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {
                    return "null_inputstream";
                }

                String stringJSON = buffer.toString();
//                Log.v(LOG_CAT, stringJSON );
                return stringJSON;
            } catch (UnknownHostException | ConnectException e) {
                error = "null_internet" ;
                e.printStackTrace();
            } catch (IOException e) {
                error= "null_file";
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
//                        Log.e(LOG_CAT, "ErrorClosingStream", e);
                    }
                }
            }
            return error;
        }//doinbackground

        @Override
        protected void onPostExecute(String strJSON) {
            Log.v("MyApp", "AsyncResponse: " + strJSON );
            if( strJSON=="null_inputstream" || strJSON=="null_file" ){
                Toast.makeText(getApplicationContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                return  ;
            }

            if ( strJSON=="null_internet" ){
                Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return ;
            }

            try {
                JSONObject jsonObject = new JSONObject(strJSON);
                if(jsonObject.getString("success").equals("1")){
                    Toast.makeText(getApplicationContext(),"User Saved Successfully", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("login", true);
                    editor.apply();
                    progressDialog.dismiss();
                    Intent intent = new Intent(Login.this, Navigation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(),"Unable to Login", Toast.LENGTH_SHORT).show();
                }
            progressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }//getrepo

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
