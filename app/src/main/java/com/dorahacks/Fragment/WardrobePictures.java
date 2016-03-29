package com.dorahacks.Fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dorahacks.Helper.ContentCardPictures;
import com.dorahacks.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
public class WardrobePictures extends Fragment {


    public WardrobePictures() {
        // Required empty public constructor
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private	ContentCardPictures contentCardPictures;
    private RecyclerView recyclerView;
    private RVAdapter rvAdapter;
    private boolean loaded;
    private int cnt, size;
    private ProgressDialog progressDialog;
    String dressType, urlS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wardrobe_pictures, container, false);
        loaded = false;
        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle bundle = this.getArguments();
        dressType = bundle.getString("type", null);
        urlS = getResources().getString(R.string.website) + "closet/" + dressType + "/";

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        Log.v("MyApp", "Dress Type " + dressType);

        contentCardPictures = new ContentCardPictures();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_card_pictures);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        rvAdapter = new RVAdapter( contentCardPictures.ITEMS );
        recyclerView.setAdapter(rvAdapter);

        GetImage getImage = new GetImage();
        getImage.execute();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private Bitmap DownloadDB(String s){
        File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");
        if(!f.isDirectory()){
            f.mkdir();
        }

        Log.v("MyApp", "DownloadDB" + s);
        File file = new File(Environment.getExternalStorageDirectory() + "/DoraHacks/" + s + ".jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            //DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/"+ s+ ".jpg", null, outputStream, null);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            contentCardPictures.addItem(new ContentCardPictures.DummyItem("Dress", bitmap));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void RunPicaso (String url[], String name[]) {
        contentCardPictures.clear();
        cnt = 0;
        for(int i=0; i<url.length ; i++ ) {
            //Log.v("MyApp", getClass().toString() + "RunPicaso " + url[i] + " " + name[i] );
            Picasso.with(getContext()).load(url[i]).into(getTarget(url[i], name[i] ));
        }
        //while(!loaded);
        Log.v("MyApp", getClass().toString() + "RunPicaso finished " );
    }

    private class BGThread extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            rvAdapter = new RVAdapter(contentCardPictures.ITEMS);
            recyclerView.setAdapter(rvAdapter);
            progressDialog.dismiss();
            Toast.makeText(getContext(),"Wardrobe Loaded Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void useBitmap(Bitmap bitmap ){
        contentCardPictures.addItem(new ContentCardPictures.DummyItem("Dress", bitmap));

        cnt++;
        if(cnt==size){
            Log.v("MyApp", "DoneLoading");
            DoneLoading();
        }

    }

    private Target getTarget(final String url, final String Filename){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.i("PRODUTOS_FOLDER", CreateAppFolder.getProdutosFolder());
                        File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");
                        if(!f.isDirectory()){
                            f.mkdir();
                        }
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File destination = new File(Environment.getExternalStorageDirectory(),
                                "/DoraHacks/" + Filename + ".jpg");
                        FileOutputStream fo;

//                        File file = new File(Environment.getExternalStorageDirectory() + url);
                        try {

                            //Log.v("MyApp", getClass().toString() + " Camera " + destination.getPath());
                            fo = new FileOutputStream(destination);
                            fo.write(bytes.toByteArray());
                            fo.close();
                            boolean fill = destination.createNewFile();
                            if(fill){
                                Log.v("MyApp", Filename + " Created ");
                            } else {
                                Log.v("MyApp", Filename + " Not Created ");
                            }
//                            file.createNewFile();
//                            FileOutputStream ostream = new FileOutputStream(file);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
//                            ostream.flush();
//                            ostream.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Log.v("MyApp", "onBitmapLoaded" + url);
                useBitmap(bitmap);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
            }
        };
        return target;
    }

    public class GetImage extends AsyncTask<Void, Void, String > {

        @Override
        protected String doInBackground(Void... params) {
            String error=null;
//            while(!done);
            Log.v("MyApp", getClass().toString() + " AsyncTask Get Image doInBackground()");
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            URL url = null;
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
                Toast.makeText(getContext(), "Invalid Request", Toast.LENGTH_SHORT).show();
                return  ;
            }

            if ( strJSON=="null_internet" ){
                Toast.makeText(getContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return ;
            }

            try {
                JSONObject jsonObject = new JSONObject(strJSON);
                if(jsonObject.getString("success").equals("1")){
                    JSONArray type = jsonObject.getJSONArray(dressType);
                    size = type.length();
                    String image[] = new String[type.length()];
                    String filename[] = new String[type.length()];
                    for(int i=0 ; i<type.length(); i++){
                        image[i] = type.getJSONObject(i).getString("url");
                        filename[i] = type.getJSONObject(i).getString("image");
                        Log.v("MyApp", dressType + " ImageURL: " + image[i]);
                    }
                    RunPicaso(image, filename);
//                    BGThread bgThread = new BGThread();
//                    bgThread.execute(image);
                } else {
                    Toast.makeText(getContext(),"Unable to Login", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            //progressDialog.dismiss();
        }
    }//getrepo

    public void DoneLoading (){
        rvAdapter = new RVAdapter(contentCardPictures.ITEMS);
        recyclerView.setAdapter(rvAdapter);
        progressDialog.dismiss();
        Toast.makeText(getContext(),"Wardrobe Loaded Successfully", Toast.LENGTH_SHORT).show();
    }

}
