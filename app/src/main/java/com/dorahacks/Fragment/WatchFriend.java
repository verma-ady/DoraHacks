package com.dorahacks.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dorahacks.Activity.Login;
import com.dorahacks.Activity.Navigation;
import com.dorahacks.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchFriend extends Fragment {

    public WatchFriend() {
        // Required empty public constructor
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CharSequence[][] items;// = {"Easy "," Medium "," Hard "," Very Hard "};
    // arraylist to keep the selected items
    ArrayList seletedItems=new ArrayList();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_friend, container, false);

        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String fbid = sharedPreferences.getString("fbid", null);
        Log.v("MyApp", getClass().toString() + " fbid" + fbid);
        new GraphRequest( AccessToken.getCurrentAccessToken(), "/"+ fbid +"/friends", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Log.v("MyApp", AccessToken.getCurrentAccessToken().toString() );
                        Log.v("MyApp", "Response: " + response.toString());
                        try {
                            JSONObject responseObject = response.getJSONObject();
                            Log.v("MyApp", "responseObject" + responseObject.toString());
                            JSONArray data = responseObject.getJSONArray("data");
                            JSONObject friend;
                            items = new CharSequence[2][data.length()];
                            for(int i=0 ; i<data.length() ; i++ ){
                                friend = data.getJSONObject(i);
                                items[0][i] = friend.getString("name") ;
                                items[1][i] = friend.getString("id");
//                                Log.v("MyApp", "items " + items[i] + i  );
                            }
                            Alert();
                        } catch (JSONException e) {
                            Log.v("MyApp", "responseObjectError" + e.toString());
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        return view;
    }

    private void Alert(){
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Select Your Favourite Friends")
                .setMultiChoiceItems(items[0], null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here

                        for (int i = 0; i < seletedItems.size(); i++) {
                            Log.v("MyApp", getClass().toString() + " Selected " +
                                    items[0][(int)seletedItems.get(i)] + " " + items[1][(int)seletedItems.get(i)]);
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();

    }

}
