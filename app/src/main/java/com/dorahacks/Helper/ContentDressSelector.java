package com.dorahacks.Helper;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mukesh on 2/20/2016.
 */
public class ContentDressSelector {
    public List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public void addItem(DummyItem item ) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    public void clear(){
        this.ITEMS.clear();
    }

    public static class DummyItem {
        public Bitmap bitmap;
        public String name;
        public DummyItem(String vName, Bitmap vBitmap) {
            bitmap = vBitmap;
            name = vName;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
