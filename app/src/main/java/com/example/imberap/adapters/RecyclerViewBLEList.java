package com.example.imberap.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imberap.Clasesdata.BLEDevices;
import com.example.imberap.Fragment.ListaBLEFragment;
import com.example.imberap.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewBLEList extends RecyclerView.Adapter<RecyclerViewBLEList.BLEListViewHolder> implements View.OnClickListener {
    ArrayList<BLEDevices> listaDevices;
    private View.OnClickListener listener;
    private List<ImageView> imageView;
    private ImageView im;

    public RecyclerViewBLEList(ArrayList<BLEDevices> devices){
        this.listaDevices = devices;
    }

    @NonNull
    @Override
    public BLEListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_device,null,false);
        view.setOnClickListener(this);
        return new BLEListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BLEListViewHolder holder, int position) {
        holder.txtNameDevice.setText(listaDevices.get(position).getNombre());
        holder.txtMac.setText(listaDevices.get(position).getMac());

    }

    @Override
    public int getItemCount() {
        return listaDevices.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }

    public class BLEListViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameDevice,txtMac;
        Button btnConnect;
        public BLEListViewHolder(View view) {
            super(view);
            txtNameDevice= (TextView) itemView.findViewById(R.id.tvdevice_name);
            txtMac= (TextView) itemView.findViewById(R.id.tvdevice_address);
        }
    }


}
