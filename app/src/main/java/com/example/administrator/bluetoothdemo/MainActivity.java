package com.example.administrator.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.administrator.bluetoothdemo.adapter.DeviceListAdapter;
import com.example.administrator.bluetoothdemo.wrapper.BLEWrapper;
import com.example.administrator.bluetoothdemo.wrapper.BLEWrapperUICallBack;

public class MainActivity extends Activity {

    private boolean mScanning = false;
    private ListView mDeviceListView;
    private BLEWrapper mBLEWrapper;
    private DeviceListAdapter mDeviceListAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle("BluetoothDemo");
        mDeviceListView = (ListView) findViewById(R.id.deviceListView);

        mHandler = new Handler();

        mBLEWrapper = new BLEWrapper(this, new BLEWrapperUICallBack.CallBack() {
            @Override
            public void uiDeviceFound(ScanResult scanResult) {
                handleScanResult(scanResult);
            }
        });

        /*检查设备是否支持蓝牙，以及蓝牙BLE*/
        if(mBLEWrapper.checkBleHardwareAvailable() == false) {
            Toast.makeText(this, "设备不支持BLE！", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*每次resume，都检查蓝牙是否打开*/
        if (!mBLEWrapper.isBluetoothEnable()) {
            Toast.makeText(this, "蓝牙为打开，请先打开蓝牙功能！", Toast.LENGTH_LONG).show();
            Log.v("isBluetoothEnable", "false");
            finish();
        }

        mBLEWrapper.initialize();

        mDeviceListAdapter = new DeviceListAdapter(this, null);
        //setListAdapter(mDeviceListAdapter);
        mDeviceListView.setAdapter(mDeviceListAdapter);
        mDeviceListView.setOnItemClickListener(onItemClickListener);

        mScanning = true;
        setScanningTimeOut();
        mBLEWrapper.startScanning();


        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanning = false;
        mBLEWrapper.stopScanning();

        invalidateOptionsMenu();
        mDeviceListAdapter.clearList();
    }

    private void handleScanResult(ScanResult result) {
        mDeviceListAdapter.addDevice(result);
        mDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(mScanning) {
            menu.findItem(R.id.scanning_start).setVisible(false);
            menu.findItem(R.id.scanning_stop).setVisible(true);
            menu.findItem(R.id.scanning_indicator)
                    .setActionView(R.layout.progressbar_scanning);
        } else {
            menu.findItem(R.id.scanning_start).setVisible(true);
            menu.findItem(R.id.scanning_stop).setVisible(false);
            menu.findItem(R.id.scanning_indicator).setActionView(null);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.scanning_start) {
            mScanning = true;
            //setScanningTimeOut();
            //mBLEWrapper.startScanning();
            mBLEWrapper.startScanning(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEWrapper.stopScanning();
                    invalidateOptionsMenu();
                }
            }, 10000);
        } else if (item.getItemId() == R.id.scanning_stop) {
            mScanning = false;
            mBLEWrapper.stopScanning();
        }

        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }


    private void setScanningTimeOut() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                mBLEWrapper.stopScanning();
                invalidateOptionsMenu();
            }
        };
        mHandler.postDelayed(runnable, 10000);
    }


    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Intent intent = new Intent(MainActivity.this, ServiceListActivity.class);


            if (mScanning) {
                mScanning = false;
                invalidateOptionsMenu();
                mBLEWrapper.stopScanning();
            }

            intent.putExtra(ServiceListActivity.EXTRAS_DEVICE, (ScanResult) mDeviceListAdapter.getItem(position));

            startActivity(intent);
        }
    };
}
