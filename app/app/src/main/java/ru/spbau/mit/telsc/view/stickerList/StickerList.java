package ru.spbau.mit.telsc.view.stickerList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.spbau.mit.telsc.R;

public class StickerList extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> names;
    private final ArrayList<Bitmap> imageId;
    public StickerList(Activity context,
                      ArrayList<String> web, ArrayList<Bitmap> imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.names = web;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = rowView.findViewById(R.id.txt);

        ImageView imageView = rowView.findViewById(R.id.img);
        txtTitle.setText(names.get(position));

        imageView.setImageBitmap(imageId.get(position));
        return rowView;
    }
}
