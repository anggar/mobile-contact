package com.example.contactsmobile;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public static class ViewHolder {
        TextView name;
        TextView phone;
        ImageView photo;
        ImageButton delete;
    }

    public ContactAdapter(@NonNull Context context, int resource, @NonNull List<Contact> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final Contact contact = getItem(position);
        ViewHolder viewContact;

        if (convertView == null) {
            viewContact = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            viewContact.name = convertView.findViewById(R.id.tvName);
            viewContact.phone = convertView.findViewById(R.id.tvPhone);
            viewContact.photo = convertView.findViewById(R.id.ivPhoto);
            viewContact.delete = convertView.findViewById(R.id.btnDeleteContact);

            convertView.setTag(viewContact);
        } else {
            viewContact = (ViewHolder) convertView.getTag();
        }


        if (contact != null) {
            String photo = contact.getPhoto();

            viewContact.name.setText(contact.getName());
            viewContact.phone.setText(contact.getPhone());

            if (photo != null) {
                viewContact.photo.setImageURI(Uri.parse(photo));
            }

            viewContact.delete.setOnClickListener(view -> deleteConfirmation(contact.getId(), contact, parent));

            convertView.setOnClickListener(view -> {
                Intent intent = new Intent(parent.getContext(), ContactDetailActivity.class);
                intent.putExtra("id", contact.getId());
                ((AppCompatActivity) getContext()).startActivityForResult(intent, 0);
            });
        }

        return convertView;
    }

    private void deleteConfirmation(final long id, final Contact contact, ViewGroup parent) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_confirmation, parent, false);

        DialogInterface.OnClickListener btnHandler = (dialogInterface, which) -> {
            if (which == -1) {
                ContactDbHelper.deleteOne(id);
                ContactAdapter.this.remove(contact);
            }
        };

        new AlertDialog.Builder(getContext())
                .setView(view)
                .setCancelable(true)
                .setPositiveButton("Confirm", btnHandler)
                .setNegativeButton("Cancel", btnHandler)
                .show();
    }
}
