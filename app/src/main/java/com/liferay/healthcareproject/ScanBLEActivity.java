package com.liferay.healthcareproject;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.liferay.healthcareproject.bluetooth.BlueToothDeviceManager;
import com.liferay.healthcareproject.bluetooth.OnBlueToothDeviceListener;

import java.util.ArrayList;
import java.util.List;

public class ScanBLEActivity extends BaseActivity implements OnBlueToothDeviceListener {

    private RecyclerView deviceRecyclerView;
    private BlueToothDeviceManager blueToothDeviceManager;
    private List<ScanResult> scanResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ble);

        scanResultList = new ArrayList<ScanResult>();

        blueToothDeviceManager = new BlueToothDeviceManager(this, this);

        setDeviceRecyclerView();

        setToolbar();
    }

    private void setDeviceRecyclerView() {

        deviceRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        deviceRecyclerView.setHasFixedSize(true);

        deviceRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,

                        new RecyclerItemClickListener.OnItemClickListener() {

                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override

                            public void onItemClick(View view, int position) {

                                String name = scanResultList.get(position)
                                        .getDevice().getAddress();

                                Intent intent = new Intent(ScanBLEActivity.this,
                                        BLEDeviceActivity.class);

                                intent.putExtra(ADDRESS_INTENT,name);

                                startActivity(intent);

                            }
                        })

        );

    }

    private void setValueToDeviceRecyclerView(){
        deviceRecyclerView.setAdapter(new DeviceRecyclerViewAdapter(this, scanResultList));
    }

    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        toolbar.setNavigationIcon(android.R.drawable.ic_popup_sync);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBLEDevice();
            }
        });

    }

    private void scanBLEDevice(){

        scanResultList.clear();

        setValueToDeviceRecyclerView();

        blueToothDeviceManager.startScan();

    }

    @Override
    public void onResult(int callbackType, ScanResult result) {

        if (!isOverLap(result)){
            scanResultList.add(result);
            setValueToDeviceRecyclerView();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isOverLap(ScanResult result){

        for (ScanResult scanResult:scanResultList){

            if (scanResult.getDevice().
                    getAddress().equals(result.getDevice().getAddress())){
                return true;
            }

        }

        return false;

    }

    @Override
    public void onFinish() {

    }
}
