package com.liferay.healthcareproject;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by flatfisher on 9/21/15.
 */
public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<ScanResult> scanResultList;

    public DeviceRecyclerViewAdapter(Context context,List<ScanResult> scanResultList) {
        super();
        layoutInflater = LayoutInflater.from(context);
        this.scanResultList = scanResultList;
    }

    @Override
    public DeviceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.device_item, parent, false);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final DeviceRecyclerViewAdapter.ViewHolder holder, int position) {
        String name = scanResultList.get(position).getDevice().getName();
        if (name==null){
            name = "Unknown";
        }
        final String address = scanResultList.get(position).getDevice().getAddress();

        holder.name.setText(name);
        holder.address.setText(address);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return scanResultList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;
        CardView cardView;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.device_name);
            address = (TextView) v.findViewById(R.id.device_address);
            cardView = (CardView) v.findViewById(R.id.cardView);
        }
    }
}
