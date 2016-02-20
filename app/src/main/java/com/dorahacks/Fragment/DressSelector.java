package com.dorahacks.Fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
import com.dorahacks.Helper.ContentDressSelector;
import com.dorahacks.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.DividerItemDecoration;

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
public class DressSelector extends Fragment {


    public DressSelector() {
        // Required empty public constructor
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    String urlS, dressType;
    Boolean doneTop, doneBottom, done;

    private RecyclerView recyclerViewTop, recyclerViewBottum, recyclerViewFootwear;
    private RVAdapter rvAdapterTop, rvAdapterBottom, rvAdapterFootwear;
    private ContentDressSelector contentTop, contentBottom, contentFoot;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dress_selector, container, false);

        doneBottom = false;
        doneTop = false;
        done = false;

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        recyclerViewTop = (RecyclerView) view.findViewById(R.id.recyclerViewTop);
        recyclerViewBottum = (RecyclerView) view.findViewById(R.id.recyclerViewBottum);
        recyclerViewFootwear = (RecyclerView) view.findViewById(R.id.recyclerViewFootwear);

        recyclerViewTop.setHasFixedSize(false);
        recyclerViewBottum.setHasFixedSize(false);
        recyclerViewFootwear.setHasFixedSize(false);

        recyclerViewTop.addItemDecoration(new DividerItemDecoration(getContext(), null));

        LinearLayoutManager linearLayoutManagerTop = new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerBottom = new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerFootwear = new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false);

        recyclerViewTop.setLayoutManager(linearLayoutManagerTop);
        recyclerViewBottum.setLayoutManager(linearLayoutManagerBottom);
        recyclerViewFootwear.setLayoutManager(linearLayoutManagerFootwear);

        contentTop = new ContentDressSelector();
        contentBottom = new ContentDressSelector();
        contentFoot = new ContentDressSelector();

        return view;
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

        dressType = "top";
        progressDialog.setMessage("Loading Wardrobe");
        progressDialog.show();
        GetImage getImage = new GetImage();
        getImage.execute();
    }

    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder> {

        ContentDressSelector content = new ContentDressSelector();

        public RVAdapter ( List<ContentDressSelector.DummyItem> list_dummy ){
            content.ITEMS = list_dummy;
        }

        @Override
        public RVAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dress_selector, parent, false);
            CardViewHolder cardViewHolder = new CardViewHolder(view);
            return cardViewHolder;
        }

        @Override
        public void onBindViewHolder(RVAdapter.CardViewHolder holder, int position) {
           holder.imageView.setImageBitmap(content.ITEMS.get(position).bitmap);
        }

        @Override
        public int getItemCount() {
            return content.ITEMS.size();
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            CardView cardView;
            public CardViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.cardViewDressSelector);
                imageView = (ImageView) itemView.findViewById(R.id.imageViewDressSelector);
            }
        }
    }

    private Bitmap DownloadDB(String s){
        Log.v("MyApp", "DownloadDB" + dressType);
        File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");
        if(!f.isDirectory()){
            f.mkdir();
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/DoraHacks/" + s + ".jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/"+ s + ".jpg", null, outputStream, null);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//            content.addItem(new ContentDressSelector.DummyItem("Dress",bitmap));

            if(dressType.equals("top") ){
                contentTop.addItem(new ContentDressSelector.DummyItem(s,bitmap));
            } else if (dressType.equals("bottom")){
                contentBottom.addItem(new ContentDressSelector.DummyItem(s,bitmap));
            } else if (dressType.equals("foot")){
                contentFoot.addItem(new ContentDressSelector.DummyItem(s,bitmap));
            }
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
            Log.v("MyApp", getClass().toString() + " AsyncTask doInBackground()" + dressType);
//            content.clear();
            Log.v("MyApp", dressType + "Size: " + params.length);
            for(int i=0; i<params.length ; i++ ) {
                DownloadDB(params[i]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            rvAdapter = new RVAdapter(content.ITEMS);
            if(dressType.equals("top") ){
                rvAdapterTop = new RVAdapter(contentTop.ITEMS);
                recyclerViewTop.setAdapter(rvAdapterTop);
                dressType = "bottom";
                GetImage getImage = new GetImage();
                getImage.execute();
            } else if (dressType.equals("bottom")){
                rvAdapterBottom = new RVAdapter(contentBottom.ITEMS);
                recyclerViewBottum.setAdapter(rvAdapterBottom);
                dressType = "foot";
                GetImage getImage = new GetImage();
                getImage.execute();
            } else if (dressType.equals("foot")){
                rvAdapterFootwear = new RVAdapter(contentFoot.ITEMS);
                recyclerViewFootwear.setAdapter(rvAdapterFootwear);
                progressDialog.dismiss();
            }
            Toast.makeText(getContext(), "Wardrobe Loaded Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public class GetImage extends AsyncTask<Void, Void, String > {

        //        String LOG_CAT = "MyApp";
        @Override
        protected String doInBackground(Void... params) {
            String error=null;
//            while(!done);
            Log.v("MyApp", getClass().toString() + " AsyncTask Get Image doInBackground()"  + dressType);
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            URL url = null;
            urlS = getResources().getString(R.string.website) + "closet/" + dressType + "/";
            try {
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
                    JSONArray type = jsonObject.getJSONArray(dressType);
                    String image[] = new String[type.length()];
                    for(int i=0 ; i<type.length(); i++){
                        image[i] = type.getJSONObject(i).getString("image");
                        Log.v("MyApp", dressType + " Image: " + image[i]);
                    }
                    BGThread bgThread = new BGThread();
                    bgThread.execute(image);
                } else {
                    Toast.makeText(getContext(),"Unable to Login", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }//getrepo


}
