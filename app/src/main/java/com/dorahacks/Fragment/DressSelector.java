package com.dorahacks.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.dorahacks.Helper.ContentCardPictures;
import com.dorahacks.Helper.ContentDressSelector;
import com.dorahacks.Helper.RecyclerViewTouchListener;
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
import org.solovyev.android.views.llm.DividerItemDecoration;

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
public class DressSelector extends Fragment implements View.OnClickListener {


    public DressSelector() {
        // Required empty public constructor
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    String topSelect, bottomSelect, footSelect;
    int top, bottom, foot;
    String urlS, dressType;
    Boolean doneTop, doneBottom, done;
    private int cnt, size;
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

        topSelect = null;
        bottomSelect = null;
        footSelect = null;

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        recyclerViewTop = (RecyclerView) view.findViewById(R.id.recyclerViewTop);
        recyclerViewBottum = (RecyclerView) view.findViewById(R.id.recyclerViewBottum);
        recyclerViewFootwear = (RecyclerView) view.findViewById(R.id.recyclerViewFootwear);

        view.findViewById(R.id.dressSelector_button_add).setOnClickListener(this);
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

        contentTop.clear();
        contentBottom.clear();
        contentFoot.clear();

        dressType = "top";
        progressDialog.setMessage("Loading Wardrobe");
        progressDialog.show();

        GetImage getImage = new GetImage();
        getImage.execute();

        RecyclerListener();

        return view;
    }

    private void RecyclerListener(){
        recyclerViewTop.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(),
                new RecyclerViewTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.v("MyApp", getClass().toString() + " CardListener " + position);
                        if(topSelect!=null){
                            recyclerViewTop.getChildAt(top).setBackgroundColor(getResources().getColor(R.color.white));
                        }
                        recyclerViewTop.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.shade));
                        top = position;
                        topSelect = contentTop.ITEMS.get(position).name;
                    }
                }));

        recyclerViewBottum.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(),
                new RecyclerViewTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.v("MyApp", getClass().toString()+" CardListener " + position );
                        if(bottomSelect!=null){
                            recyclerViewBottum.getChildAt(bottom).setBackgroundColor(getResources().getColor(R.color.white));
                        }
                        recyclerViewBottum.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.shade));
                        bottom = position;
                        bottomSelect = contentBottom.ITEMS.get(position).name;
                    }
                }));

        recyclerViewFootwear.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(),
                new RecyclerViewTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.v("MyApp", getClass().toString() + " CardListener " + position);
                        if (footSelect != null) {
                            recyclerViewFootwear.getChildAt(foot).setBackgroundColor(getResources().getColor(R.color.white));
                        }
                        recyclerViewFootwear.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.shade));
                        foot = position;
                        footSelect = contentFoot.ITEMS.get(position).name;
                    }
                }));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dressSelector_button_add:
                SaveDress saveDress = new SaveDress();
                saveDress.execute();
                break;

        }
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

            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

            if(dressType.equals("top") ){
                contentTop.addItem(new ContentDressSelector.DummyItem(s,bitmap));
            } else if (dressType.equals("bottom")){
                contentBottom.addItem(new ContentDressSelector.DummyItem(s,bitmap));
            } else if (dressType.equals("foot")){
                contentFoot.addItem(new ContentDressSelector.DummyItem(s,bitmap));
            }
            return bitmap;
        } catch (FileNotFoundException e) {
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
//                    BGThread bgThread = new BGThread();
//                    bgThread.execute(image);
                    RunPicaso(image, filename);
                } else {
                    Toast.makeText(getContext(),"Unable to Login", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }//getrepo

    private void RunPicaso (String url[], String name[]) {
        cnt = 0;
        for(int i=0; i<url.length ; i++ ) {
            Picasso.with(getContext()).load(url[i]).into(getTarget(url[i], name[i] ));
        }
        Log.v("MyApp", getClass().toString() + "RunPicaso finished " );
    }

    private Target getTarget(final String url, final String Filename){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");
                        if(!f.isDirectory()){
                            f.mkdir();
                        }
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File destination = new File(Environment.getExternalStorageDirectory(),
                                "/DoraHacks/" + Filename + ".jpg");
                        FileOutputStream fo;

                        try {
                            fo = new FileOutputStream(destination);
                            fo.write(bytes.toByteArray());
                            fo.close();
                            boolean fill = destination.createNewFile();
                            if(fill){
                                Log.v("MyApp", Filename + " Created ");
                            } else {
                                Log.v("MyApp", Filename + " Not Created ");
                            }
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

    private void useBitmap(Bitmap bitmap ){
        if(dressType.equals("top") ){
            contentTop.addItem(new ContentDressSelector.DummyItem(dressType,bitmap));
        } else if (dressType.equals("bottom")){
            contentBottom.addItem(new ContentDressSelector.DummyItem(dressType,bitmap));
        } else if (dressType.equals("foot")){
            contentFoot.addItem(new ContentDressSelector.DummyItem(dressType,bitmap));
        }

        cnt++;
        if(cnt==size){
            DoneLoading();
        }

    }

    public void DoneLoading (){
        if(dressType.equals("top") ){
            rvAdapterTop = new RVAdapter(contentTop.ITEMS);
            recyclerViewTop.setAdapter(rvAdapterTop);
            dressType = "bottom";
            cnt=0;
            GetImage getImage = new GetImage();
            getImage.execute();
        } else if (dressType.equals("bottom")){
            rvAdapterBottom = new RVAdapter(contentBottom.ITEMS);
            recyclerViewBottum.setAdapter(rvAdapterBottom);
            dressType = "foot";
            cnt=0;
            GetImage getImage = new GetImage();
            getImage.execute();
        } else if (dressType.equals("foot")){
            rvAdapterFootwear = new RVAdapter(contentFoot.ITEMS);
            recyclerViewFootwear.setAdapter(rvAdapterFootwear);
            progressDialog.dismiss();
        }
        Toast.makeText(getContext(),"Wardrobe Loaded Successfully", Toast.LENGTH_SHORT).show();
    }


    public class SaveDress extends AsyncTask<Void, Void, String > {

        //        String LOG_CAT = "MyApp";
        @Override
        protected String doInBackground(Void... params) {
            String error=null;
//            while(!done);
            Log.v("MyApp", getClass().toString() + " AsyncTask Get Image doInBackground()"  + dressType);
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            URL url = null;
            urlS = getResources().getString(R.string.website) + "closet/addFav/";
            try {
                url= new URL(urlS);
                Log.v("MyApp", getClass().toString() + " URL : " + urlS);

                StringBuilder postDataString = new StringBuilder();
                postDataString.append(URLEncoder.encode("fbid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("fbid", null)));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("dressName"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode("MyDress"));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("trend"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode("0"));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("topid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(topSelect));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("bottomid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(bottomSelect));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("footid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(footSelect));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("accid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode("Acc"));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("access"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode("0"));

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
                    Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                    android.support.v4.app.FragmentTransaction fragmentTransaction;
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout_navigation, new Expert());
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(),"Unable to Add", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }//getrepo


}
