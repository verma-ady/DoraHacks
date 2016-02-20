package com.dorahacks.Fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
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

import com.dorahacks.Helper.ContentCardPictures;
import com.dorahacks.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favourite extends Fragment {


    public Favourite() {
        // Required empty public constructor
    }

    private ContentCardPictures contentCardPictures;
    private RecyclerView recyclerView;
    private RVAdapter rvAdapter;
    String urlS;
    ProgressDialog progressDialog;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_card_pictures);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        contentCardPictures = new ContentCardPictures();
        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        return view;
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
//            holder.name.setText(contentCardPictures.ITEMS.get(position).Name);

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


    @Override
    public void onStart() {
        super.onStart();
        Log.v("MyApp", "DbAuthLog" + " onStart");
        if(!sharedPreferences.getBoolean("dropboxWR", false ) ){
            Log.v("MyApp", "DbAuthLog"+  " onStart if");
            Drobbox();
        }
    }

    private void Drobbox(){
        // In the class declaration section:

        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(getResources().getString(R.string.dbappkey),
                getResources().getString(R.string.dbappsecret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        mDBApi.getSession().startOAuth2Authentication(getActivity());
        editor.putBoolean("dropboxWR", true);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("MyApp", "DbAuthLog" + " onResume");
        AppKeyPair appKeys = new AppKeyPair(getResources().getString(R.string.dbappkey),
                getResources().getString(R.string.dbappsecret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        if (mDBApi.getSession().authenticationSuccessful()) {
            Log.v("MyApp", "DbAuthLog"+  " onResume if");
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();

            } catch (IllegalStateException e) {
                Log.v("MyApp", "DbAuthLog"+  " Error authenticating:" + e);
            }
        } else {
            Log.v("MyApp", "DbAuthLog"+  " onResume else");
            mDBApi.getSession().startOAuth2Authentication(getActivity());
            editor.putBoolean("dropboxWRP", true);
            editor.apply();
        }

        progressDialog.setMessage("Loading Favourite");
        progressDialog.show();
        GetImage getImage = new GetImage();
        getImage.execute();
    }


    private Bitmap DownloadDB(String s){
        File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");
        if(!f.isDirectory()){
            f.mkdir();
        }

        Log.v("MyApp", "DownloadDB " + s );
        File file = new File(Environment.getExternalStorageDirectory() + "/DoraHacks/" + s + ".jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/"+ s+ ".jpg", null, outputStream, null);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            contentCardPictures.addItem(new ContentCardPictures.DummyItem("Dress",bitmap));
            Log.v("MyApp", "DbExampleLog" + "The file's rev is: " + info.getMetadata().rev);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class BGThread extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.v("MyApp", getClass().toString() + " AsyncTask  bgthread doInBackground()");
            contentCardPictures.clear();
            Log.v("MyApp", "Size: " + params.length);
            for(int i=0; i<params.length ; i++ ) {
                DownloadDB(params[i]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            rvAdapter = new RVAdapter(contentCardPictures.ITEMS);
            recyclerView.setAdapter(rvAdapter);
            progressDialog.dismiss();
            if(bundle==null){
                Toast.makeText(getActivity(), "Wardrobe Loaded Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Expert Approves!!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class GetImage extends AsyncTask<Void, Void, String > {

        //        String LOG_CAT = "MyApp";
        @Override
        protected String doInBackground(Void... params) {
            String error=null;
//            while(!done);
            Log.v("MyApp", getClass().toString() + " AsyncTask Get Image doInBackground()");
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            URL url = null;
            try {
                urlS = getResources().getString(R.string.website) + "closet/getFav/";
                url= new URL(urlS);
                Log.v("MyApp", getClass().toString() + " URL : " + urlS);
                StringBuilder postDataString = new StringBuilder();
                postDataString.append(URLEncoder.encode("fbid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("fbid", null)));

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
            Log.v("MyApp", "AsyncResponse: " + strJSON);
            if( strJSON=="null_inputstream" || strJSON=="null_file" ){
//                Toast.makeText(getContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                return  ;
            }

            if ( strJSON=="null_internet" ){
//                Toast.makeText(getContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return ;
            }

            try {
                JSONObject jsonObject = new JSONObject(strJSON);
                if(jsonObject.getString("success").equals("1")){
                    Log.v("MyApp", "in if success 1");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    String image[] = new String[ (jsonArray.length())*3 ];
                    Log.v("MyApp", "size " + jsonArray.length());
                    for(int i=0 ; i<jsonArray.length() ;  ){
                        image[i] = jsonArray.getJSONObject(i).getString("top");
                        image[i+1] = jsonArray.getJSONObject(i).getString("bottom");
                        image[i+2] = jsonArray.getJSONObject(i).getString("foot");
                        Log.v("MyApp", image[i] +" "+image[i+1] +" "+image[i+2]);
                        i=i+3;
                    }

                    BGThread bgThread = new BGThread();
                    bgThread.execute(image);

                } else {
                    Toast.makeText(getContext(), "Unable to Get Favourite", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }//getrepo


}
