package com.dorahacks.Fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dorahacks.Activity.Login;
import com.dorahacks.Activity.Navigation;
import com.dorahacks.Helper.ContentCardPictures;
import com.dorahacks.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
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
    private	ContentCardPictures contentCardPictures;
    private RecyclerView recyclerView;
    private RVAdapter rvAdapter;
    String urlS;
    private DropboxAPI<AndroidAuthSession> mDBApi;

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

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_card_pictures);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


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

                        contentCardPictures = new ContentCardPictures();

                        contentCardPictures.addItem(new ContentCardPictures.DummyItem("Feroz Ahmed",
                                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.t1)));

                        contentCardPictures.addItem(new ContentCardPictures.DummyItem("Feroz Ahmed",
                                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.b1)));


                        contentCardPictures.addItem(new ContentCardPictures.DummyItem("Feroz Ahmed",
                                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.f1)));

                        contentCardPictures.addItem(new ContentCardPictures.DummyItem("Aditya Verma",
                                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.t2)));

                        contentCardPictures.addItem(new ContentCardPictures.DummyItem("Aditya Verma",
                                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.b3)));


                        contentCardPictures.addItem(new ContentCardPictures.DummyItem("Aditya Verma",
                                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.f2)));

                        rvAdapter = new RVAdapter( contentCardPictures.ITEMS );
                        recyclerView.setAdapter(rvAdapter);


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();

    }

    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder>{
        ContentCardPictures contentCardPictures = new ContentCardPictures();

        public RVAdapter(List<ContentCardPictures.DummyItem> vITEMS ){
            contentCardPictures.ITEMS = vITEMS;
        }

        @Override
        public RVAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pictures, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RVAdapter.CardViewHolder holder, int position) {
            holder.name.setText(contentCardPictures.ITEMS.get(position).Name);

            holder.image.setAdjustViewBounds(true);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.image.setImageBitmap(contentCardPictures.ITEMS.get(position).Image);
        }

        @Override
        public int getItemCount() {
            return contentCardPictures.ITEMS.size();
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            CardView card;
            TextView name;
            ImageView image;
            public CardViewHolder(View itemView) {
                super(itemView);
                card = (CardView) itemView.findViewById(R.id.cardView_pictures);
                name = (TextView) itemView.findViewById(R.id.cardPicturesName);
                image = (ImageView) itemView.findViewById(R.id.cardPicturesImage);
            }
        }
    }

}
