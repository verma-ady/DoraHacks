package com.dorahacks.Helper;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mukesh on 1/20/2016.
 */
public class ContentCardPictures {
    public List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public void addItem(DummyItem item ) {
        ITEMS.add(item);
        ITEM_MAP.put(item.Name, item);
    }

    public void clear(){
        this.ITEMS.clear();
    }

    public static class DummyItem {
        public String Name;
        public Bitmap Image;
        public DummyItem(String vName, Bitmap vImage  ) {
            Name = vName ;
            Image = vImage;
        }

        @Override
        public String toString() {
            return Name;
        }
    }

}
