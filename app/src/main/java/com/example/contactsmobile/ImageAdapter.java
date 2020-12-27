package com.example.contactsmobile;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<String> {
    public static class ViewHolder {
        ImageView photo;
    }

    public ImageAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final String photo = getItem(position);
        ViewHolder viewContact;

        if (convertView == null) {
            viewContact = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_image, parent, false);

            viewContact.photo = convertView.findViewById(R.id.ivPhoto);

            convertView.setTag(viewContact);
        } else {
            viewContact = (ViewHolder) convertView.getTag();
        }


        if (photo != null) {
            Picasso.get().load("http://192.168.43.71:8001/public/" + photo).into(viewContact.photo);
        }

        return convertView;
    }
}
