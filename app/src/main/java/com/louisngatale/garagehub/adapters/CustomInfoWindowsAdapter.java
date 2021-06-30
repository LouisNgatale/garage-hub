package com.louisngatale.garagehub.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.textclassifier.TextClassification;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.louisngatale.garagehub.R;

public class CustomInfoWindowsAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    String TAG = "Adapter";
    private Context mContext;

    public CustomInfoWindowsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        TextView phone = view.findViewById(R.id.phone_number);
        TextView tvSnippet = view.findViewById(R.id.desc);
        Button view_item = view.findViewById(R.id.view_item);

        DocumentSnapshot doc = (DocumentSnapshot) marker.getTag();

        if (!title.equals("")){
            tvTitle.setText((String) doc.get("company"));
        }

        if (!tvSnippet.equals("")){
            tvSnippet.setText((String) doc.get("description"));
        }
        phone.setText((String) doc.get("phone"));

        view_item.setOnClickListener(v -> {
            Toast.makeText(mContext, doc.getId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        rendowWindowText(marker,mWindow);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull  Marker marker) {
        rendowWindowText(marker,mWindow);
        return mWindow;
    }
}
