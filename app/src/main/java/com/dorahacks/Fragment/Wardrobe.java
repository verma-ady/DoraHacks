package com.dorahacks.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;
import com.dorahacks.Activity.Navigation;
import com.dorahacks.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class Wardrobe extends Fragment implements View.OnClickListener {


    public Wardrobe() {
        // Required empty public constructor
    }
    Boolean done = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String Filename, type, urlS;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Cloudinary cloudinary;

    Bundle bundle;
    WardrobePictures wardrobePictures;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        cloudinary = new Cloudinary("cloudinary://457197387596169:7gGfhTwHqIob3E1FKxyTZDIZePA@dressup");
        cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(getContext()));

        urlS = getResources().getString(R.string.website) + "closet/update/";

        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        view.findViewById(R.id.cardView_top).setOnClickListener(this);
        view.findViewById(R.id.cardView_bottum).setOnClickListener(this);
        view.findViewById(R.id.cardView_Footwear).setOnClickListener(this);
//        view.findViewById(R.id.cardView_accessories).setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectType();
            }
        });

        return view;
    }

    private void SelectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void SelectType(){
        final CharSequence[] options = {"Top", "Bottom", "Footwear", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Wardrobe Type");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Top")) {
                    type = "top";
                    SelectImage();
                } else if (options[item].equals("Bottom")) {
                    type = "bottom";
                    SelectImage();
                } else if (options[item].equals("Footwear")) {
                    type = "footwear";
                    SelectImage();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        WardrobePictures wardrobePictures = new WardrobePictures();
        bundle = new Bundle();
        switch (v.getId()){
//            case R.id.cardView_accessories:
//                Toast.makeText(getContext(), "Accessories", Toast.LENGTH_SHORT).show();
////                wardrobePictures = new WardrobePictures();
//                fragmentTransaction = getFragmentManager().beginTransaction();
////                bundle = new Bundle();
//                bundle.putString("type", "acc");
//                wardrobePictures.setArguments(bundle);
//                break;
            case R.id.cardView_top:
                Toast.makeText(getContext(), "Top", Toast.LENGTH_SHORT).show();
//                wardrobePictures = new WardrobePictures();
                fragmentTransaction = getFragmentManager().beginTransaction();
//                bundle = new Bundle();
                bundle.putString("type", "top");
                wardrobePictures.setArguments(bundle);
                break;
            case R.id.cardView_bottum:
                Toast.makeText(getContext(), "Bottom", Toast.LENGTH_SHORT).show();
//                wardrobePictures = new WardrobePictures();
                fragmentTransaction = getFragmentManager().beginTransaction();
//                bundle = new Bundle();
                bundle.putString("type", "bottom");
                wardrobePictures.setArguments(bundle);
                break;
            case R.id.cardView_Footwear:
//                wardrobePictures = new WardrobePictures();
                Toast.makeText(getContext(), "Footwear", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
//                bundle = new Bundle();
                bundle.putString("type", "foot");
                wardrobePictures.setArguments(bundle);
                break;
        }
        fragmentTransaction.replace(R.id.framelayout_navigation, wardrobePictures);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) { // camera
            if (requestCode == 1) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");

                if(!f.isDirectory()){
                    if(!f.mkdir()){
                        Log.v("MyApp", "Unable to create directory" + f.toString() );
                        Toast.makeText(getContext(), "Unable to Create New Directory", Toast.LENGTH_LONG );
                    }
                }
                Filename = Long.toString(System.currentTimeMillis() );
                File destination = new File(Environment.getExternalStorageDirectory() +
                        "/DoraHacks/" + Filename + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    Log.v("MyApp", getClass().toString() + " Camera " + destination.getPath());
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    progressDialog.setMessage("Uploading Image");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    FileInputStream inputStream = new FileInputStream(destination);
                    BGThread bgThread = new BGThread(inputStream, Filename);
                    bgThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) { // gallery
                Uri selectedImageUri = data.getData();
                String[] projection = { MediaStore.MediaColumns.DATA };
                CursorLoader cursorLoader = new CursorLoader(getContext(),selectedImageUri, projection, null, null, null);
                Cursor cursor =cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                Log.v("MyApp", getClass().toString() + " Select " + selectedImagePath);

                File file = new File(selectedImagePath);
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Filename = Long.toString(System.currentTimeMillis());
                progressDialog.setMessage("Uploading Image");
                progressDialog.show();
                BGThread bgThread = new BGThread(fileInputStream, Filename);
                bgThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }
/*
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

    private void UploadDB(File file, String Name){
        Log.v("MyApp", getClass().toString() + " UploadDB()");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            DropboxAPI.Entry response = null;
            response = mDBApi.putFile(Name+".jpg", inputStream, file.length(), null, null);
            SaveImage saveImage = new SaveImage();
            saveImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Log.v("MyApp", "DbExampleLog" + "The uploaded file's rev is: " + response.rev);
        } catch (DropboxException e) {
            Log.v("MyApp", "DbExampleLog" + "DBException");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.v("MyApp", "DbExampleLog" + "FileNotFoundException");
            e.printStackTrace();
        }
    }
*/
    @Override
    public void onResume() {
        super.onResume();
    }

    private class BGThread extends AsyncTask<Void, Void, Void>{

        FileInputStream inputStream;
        String Name;
        Map<String, String> upload;
        public BGThread (FileInputStream vFile, String vName ){
            inputStream = vFile;
            Name = vName;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.v("MyApp", getClass().toString() + " AsyncTask doInBackground()");

            try {
                upload = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap("public_id", Name));
                Log.v( "MyApp", getClass().toString() + " " + upload.toString() );
            } catch (IOException e) {
                e.printStackTrace();
            }
            //UploadDB(File, Name);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.v("MyApp", getClass().toString() + " AsyncTask BGThread onPost");
            SaveImage saveImage = new SaveImage(upload.get("public_id"), upload.get("secure_url"));
            saveImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            done = true;
        }
    }

    public class SaveImage extends AsyncTask<Void, Void, String > { //

        String PID, URL;
        public SaveImage(String vPID, String vURL) {
            PID = vPID;
            URL = vURL;
        }

        @Override
        protected String doInBackground(Void... params) {
            String error=null;
            while(!done);
            Log.v("MyApp", getClass().toString() + " AsyncTask Save Image doInBackground()");
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            URL url = null;
            try {
                url= new URL(urlS);

                StringBuilder postDataString = new StringBuilder();
                postDataString.append(URLEncoder.encode("fbid"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(sharedPreferences.getString("fbid", null)));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("dressType"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(type));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("url"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(URL));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("image"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode(Filename));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("access"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode("0"));
                postDataString.append("&");

                postDataString.append(URLEncoder.encode("dressName"));
                postDataString.append("=");
                postDataString.append(URLEncoder.encode("mydress"));

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

                    Toast.makeText(getContext(),"Wardrobe Saved Successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Fashion Tip: A Black Bottom might suit your selection", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getContext(),"Unable to Login", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }//getrepo

}
