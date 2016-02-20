package com.dorahacks.Fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.dorahacks.Helper.ContentCardPictures;
import com.dorahacks.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private DropboxAPI<AndroidAuthSession> mDBApi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wardrobe_pictures, container, false);

        sharedPreferences = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        contentCardPictures = new ContentCardPictures();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_card_pictures);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person",
                BitmapFactory.decodeResource(getResources(), R.drawable.dp)));

        contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person",
                BitmapFactory.decodeResource(getResources(), R.drawable.dp)));

        contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person",
                BitmapFactory.decodeResource(getResources(), R.drawable.dp)));

        contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person",
                BitmapFactory.decodeResource(getResources(), R.drawable.dp)));

        contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person",
                BitmapFactory.decodeResource(getResources(), R.drawable.dp)));

        rvAdapter = new RVAdapter( contentCardPictures.ITEMS );
        recyclerView.setAdapter(rvAdapter);

        return view;
    }

    private Bitmap DownloadDB(){
        Log.v("MyApp", "DownloadDB");
        File f = new File(Environment.getExternalStorageDirectory() + "/DoraHacks");
        if(!f.isDirectory()){
            f.mkdir();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/DoraHacks"+"/1455918138761.jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/1455918138761.jpg", null, outputStream, null);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            Log.v("MyApp", "DbExampleLog" +"The file's rev is: " + info.getMetadata().rev);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return null;
    }

//    contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person", bitmap));
//    contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person", bitmap));
//    contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person", bitmap));

//    rvAdapter = new RVAdapter(contentCardPictures.ITEMS);
//    recyclerView.setAdapter(rvAdapter);

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

        BGThread bgThread = new BGThread();
        bgThread.execute();
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

    private class BGThread extends AsyncTask<Void, Void, Bitmap> {

//        File File;
//        String Name;
//        public BGThread (File vFile, String vName ){
//            File = vFile;
//            Name = vName;
//        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            Log.v("MyApp", getClass().toString() + " AsyncTask doInBackground()");
            return DownloadDB();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            contentCardPictures.clear();
            contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person", bitmap));
            contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person", bitmap));
            contentCardPictures.addItem(new ContentCardPictures.DummyItem("ABC Person", bitmap));
            rvAdapter = new RVAdapter(contentCardPictures.ITEMS);
            recyclerView.setAdapter(rvAdapter);

        }
    }

}
