package com.example.contactsmobile;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public static class ViewHolder {
        TextView name;
        TextView phone;
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
            viewContact.delete = convertView.findViewById(R.id.btnDeleteContact);

            convertView.setTag(viewContact);
        } else {
            viewContact = (ViewHolder) convertView.getTag();
        }

        if (contact != null) {
            viewContact.name.setText(contact.getName());
            viewContact.phone.setText(contact.getPhone());

            viewContact.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteConfirmation(contact.getId(), contact, parent);
                }
            });
        }

        return convertView;
    }

    private void deleteConfirmation(final long id, final Contact contact, ViewGroup parent) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_confirmation, parent, false);

        DialogInterface.OnClickListener btnHandler = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    ContactDbHelper.deleteOne(id);
                    ContactAdapter.this.remove(contact);
                }
            }
        };

        new AlertDialog.Builder(getContext())
                .setView(view)
                .setCancelable(true)
                .setPositiveButton("Konfirmasi", btnHandler)
                .setNegativeButton("Batal", btnHandler)
                .show();
    }
}
