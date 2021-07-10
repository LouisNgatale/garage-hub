package com.louisngatale.garagehub.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.louisngatale.garagehub.R;
import com.louisngatale.garagehub.data.Requests;

public class RequestsRecView extends FirestoreRecyclerAdapter<Requests, RequestsRecView.ViewHolder> {
    private static final String TAG = "Dash";
    Context mContext;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RequestsRecView(@NonNull  FirestoreRecyclerOptions<Requests> options, Context mContext) {
        super(options);
        this.mContext = mContext;
        Log.d(TAG, "RequestsRecView: Initialized rec view");
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestsRecView.ViewHolder holder, int position, @NonNull Requests model) {
        Log.d(TAG, "onBindViewHolder: ");
        holder.phone.setText(model.getPhone());
        holder.service_name.setText(model.getCustomer());
        holder.service_type.setVisibility(View.VISIBLE);
        holder.service_type.setText(model.getService());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,parent,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView service_name,phone,service_type;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: ");
            service_name = itemView.findViewById(R.id.service_name);
            phone = itemView.findViewById(R.id.service_price);
            service_type = itemView.findViewById(R.id.service_type);
        }
    }
}
