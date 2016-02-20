package com.dorahacks.Fragment;


import android.app.AlertDialog;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dorahacks.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Wardrobe extends Fragment implements View.OnClickListener {


    public Wardrobe() {
        // Required empty public constructor
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String Filename, type;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        view.findViewById(R.id.cardView_top).setOnClickListener(this);
        view.findViewById(R.id.cardView_bottum).setOnClickListener(this);
        view.findViewById(R.id.cardView_Footwear).setOnClickListener(this);
        view.findViewById(R.id.cardView_accessories).setOnClickListener(this);

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
                } else if (options[item].equals("Bottum")) {
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
        switch (v.getId()){
            case R.id.cardView_accessories:
                Toast.makeText(getContext(), "Accessories", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout_navigation, new WardrobePictures());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.cardView_top:
                Toast.makeText(getContext(), "Top", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout_navigation, new WardrobePictures());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.cardView_bottum:
                Toast.makeText(getContext(), "Bottum", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout_navigation, new WardrobePictures());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.cardView_Footwear:
                Toast.makeText(getContext(), "Footwear", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout_navigation, new WardrobePictures());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!sharedPreferences.getBoolean("dropboxWR", false ) ){
            Drobbox();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 1) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");
                if(!f.isDirectory()){
                    f.mkdir();
                }
                Filename = Long.toString(System.currentTimeMillis() );
                File destination = new File(Environment.getExternalStorageDirectory(),
                        "/DoraHacks/" + Filename + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    Log.v("MyApp", getClass().toString() + " Camera " + destination.getPath());
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    BGThread bgThread = new BGThread(destination, Filename);
                    bgThread.execute();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {
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
                Filename = Long.toString(System.currentTimeMillis());
                BGThread bgThread = new BGThread(file, Filename);
                bgThread.execute();
            }
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

    private void UploadDB(File file, String Name){
        Log.v("MyApp", getClass().toString() + " UploadDB()");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            DropboxAPI.Entry response = null;
            response = mDBApi.putFile(Name+".jpg", inputStream, file.length(), null, null);
            Log.v("MyApp", "DbExampleLog" + "The uploaded file's rev is: " + response.rev);
        } catch (DropboxException e) {
            Log.v("MyApp", "DbExampleLog" + "DBException");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.v("MyApp", "DbExampleLog" + "FileNotFoundException");
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        AppKeyPair appKeys = new AppKeyPair(getResources().getString(R.string.dbappkey),
                getResources().getString(R.string.dbappsecret));
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.v("MyApp", "DbAuthLog"+  " Error authenticating:" + e);
            }
        }
    }

    private class BGThread extends AsyncTask<Void, Void, Void>{

        File File;
        String Name;
        public BGThread (File vFile, String vName ){
            File = vFile;
            Name = vName;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.v("MyApp", getClass().toString() + " AsyncTask doInBackground()");
            UploadDB(File, Name);
            return null;
        }
    }

}
