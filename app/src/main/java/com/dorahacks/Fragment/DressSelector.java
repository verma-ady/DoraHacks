package com.dorahacks.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorahacks.Helper.ContentDressSelector;
import com.dorahacks.R;

import org.solovyev.android.views.llm.DividerItemDecoration;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DressSelector extends Fragment {


    public DressSelector() {
        // Required empty public constructor
    }

    private RecyclerView recyclerViewTop, recyclerViewBottum, recyclerViewFootwear;
    private RVAdapter rvAdapter;
    private ContentDressSelector content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dress_selector, container, false);
        recyclerViewTop = (RecyclerView) view.findViewById(R.id.recyclerViewTop);
        recyclerViewBottum = (RecyclerView) view.findViewById(R.id.recyclerViewBottum);
        recyclerViewFootwear = (RecyclerView) view.findViewById(R.id.recyclerViewFootwear);

        recyclerViewTop.setHasFixedSize(false);
        recyclerViewBottum.setHasFixedSize(false);
        recyclerViewFootwear.setHasFixedSize(false);

        recyclerViewTop.addItemDecoration(new DividerItemDecoration(getContext(), null));

        LinearLayoutManager linearLayoutManagerTop = new org.solovyev.android.views.llm.LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerBottom = new org.solovyev.android.views.llm.LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerFootwear = new org.solovyev.android.views.llm.LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false);

        recyclerViewTop.setLayoutManager(linearLayoutManagerTop);
        recyclerViewBottum.setLayoutManager(linearLayoutManagerBottom);
        recyclerViewFootwear.setLayoutManager(linearLayoutManagerFootwear);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dp);

        content = new ContentDressSelector();

        content.addItem(new ContentDressSelector.DummyItem("Name", bitmap ));
        content.addItem(new ContentDressSelector.DummyItem("Name", bitmap ));
        content.addItem(new ContentDressSelector.DummyItem("Name", bitmap ));
        content.addItem(new ContentDressSelector.DummyItem("Name", bitmap ));

        rvAdapter = new RVAdapter( content.ITEMS );

        recyclerViewTop.setAdapter(rvAdapter);
        recyclerViewBottum.setAdapter(rvAdapter);
        recyclerViewFootwear.setAdapter(rvAdapter);

        return view;
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

}
