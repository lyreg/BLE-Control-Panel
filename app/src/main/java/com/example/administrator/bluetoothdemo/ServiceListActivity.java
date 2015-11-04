package com.example.administrator.bluetoothdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bluetoothdemo.adapter.ServiceListExpandableAdapter;
import com.example.administrator.bluetoothdemo.wrapper.BLEWrapperUICallBack;
import com.example.administrator.bluetoothdemo.wrapper.BLEWrapper;

import java.util.List;

public class ServiceListActivity extends AppCompatActivity implements BLEWrapperUICallBack {

    public static final String EXTRAS_DEVICE = "BLE_DEVICE";

    private Toolbar mToolbar;
    private BLEWrapper mBleWrapper = null;

    private ActionBar mAcitonBar;
    //private ServiceListAdapter mListAdapter;
    //private ListView mListView;
    private ServiceListExpandableAdapter mListAdapter;
    private ExpandableListView mListView;
    private View headerView;
    private ScanResult mScanResult;

    private TextView mDeviceBondState;
    private TextView mDeviceRssi;
    private TextView mDeviceAddress;
    private TextView mConnectStatus;

    private boolean mConnecting = false;
    private boolean onPause = false;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_layout);
        mToolbar = (Toolbar) findViewById(R.id.activity_service_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setTitle("ServiceList");

        mListView = (ExpandableListView) findViewById(R.id.serviceListView);

        headerView = getLayoutInflater().inflate(R.layout.activity_device_details_layout, null);
        mListView.addHeaderView(headerView);

//        mDeviceName = (TextView) headerView.findViewById(R.id.servicelist_deviceName);
        mDeviceAddress = (TextView) headerView.findViewById(R.id.servicelist_deviceAddress);
        mDeviceRssi = (TextView) headerView.findViewById(R.id.servicelist_deviceRssi);
        mConnectStatus = (TextView) headerView.findViewById(R.id.servicelist_deviceStatus);
        mDeviceBondState = (TextView) headerView.findViewById(R.id.servicelist_deviceBondState);

//        mDeviceName = (TextView) findViewById(R.id.servicelist_deviceName);
//        mDeviceAddress = (TextView) findViewById(R.id.servicelist_deviceAddress);
//        mDeviceRssi = (TextView) findViewById(R.id.servicelist_deviceRssi);
//        mConnectStatus = (TextView) findViewById(R.id.servicelist_deviceStatus);

        mScanResult = getIntent().getParcelableExtra(EXTRAS_DEVICE);

        BluetoothDevice device = mScanResult.getDevice();
        int rssi = mScanResult.getRssi();
        int bondState = device.getBondState();
        String name = device.getName();
        String address = device.getAddress();


        String rssiString = (rssi == 0) ? "N/A" : rssi + " db";
        String bondStateString = "Not Bond";
        if(bondState == BluetoothDevice.BOND_BONDED) bondStateString = "Bonded";
        if(bondState == BluetoothDevice.BOND_BONDING) bondStateString = "Bonding...";

        if(name == null || name.length() <= 0) name = "Unknown Device";

        //mDeviceName.setText(name);
        mDeviceRssi.setText(rssiString);
        mDeviceAddress.setText(address);
        mConnectStatus.setText("disconnected");
        mDeviceBondState.setText(bondStateString);
        getSupportActionBar().setTitle(name);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBondStatusReceiver, mFilter);

    }

    private BroadcastReceiver mBondStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mBluetoothDevice != null) {
                    int status = mBluetoothDevice.getBondState();
                    switch (status) {
                        case BluetoothDevice.BOND_NONE :
                            mDeviceBondState.setText("Not Bond");
                            menu.findItem(R.id.action_bond).setTitle("Bond");
                            break;
                        case BluetoothDevice.BOND_BONDING :
                            mDeviceBondState.setText("Bonding...");
                            menu.findItem(R.id.action_bond).setTitle("Bond");
                            break;
                        case BluetoothDevice.BOND_BONDED :
                            mDeviceBondState.setText("Bonded");
                            menu.findItem(R.id.action_bond).setTitle("Delete Bond");
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (onPause == true) {
            onPause = false;
            return;
        }

        Log.v("onResume", "onResume");
        if(mBleWrapper == null) mBleWrapper = new BLEWrapper(this, this);

        if(mBleWrapper.initialize() == false) {
            finish();
        }

//        if(mListAdapter == null) {
//            mListAdapter = new ServiceListAdapter(this, R.layout.service_list_item);
//        }
        if(mListAdapter == null) {
            mListAdapter = new ServiceListExpandableAdapter(this, mBleWrapper);
        }
        mListView.setAdapter(mListAdapter);
        mBleWrapper.connect(mScanResult);
        mConnectStatus.setText("connecting");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("onPause", "onPause");

        onPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBondStatusReceiver);
//        mListAdapter.clear();
        mBleWrapper.disconnect();
        mBleWrapper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_list, menu);
        this.menu = menu;
        menu.findItem(R.id.connect_indicator).setActionView(R.layout.progressbar_scanning);
        if(mBleWrapper.isConnected()) {
            menu.findItem(R.id.action_connect).setVisible(false);
            menu.findItem(R.id.action_disconnect).setVisible(true);
            menu.findItem(R.id.connect_indicator).setActionView(null);
        } else {
            menu.findItem(R.id.action_connect).setVisible(true);
            menu.findItem(R.id.action_disconnect).setVisible(false);
            menu.findItem(R.id.connect_indicator).setActionView(null);
        }

        switch (mBleWrapper.isBonded()) {
            case BluetoothDevice.BOND_NONE :
                menu.findItem(R.id.action_bond).setTitle("Bond");
                break;
            case BluetoothDevice.BOND_BONDING :
                menu.findItem(R.id.action_bond).setTitle("Bond");
                break;
            case BluetoothDevice.BOND_BONDED :
                menu.findItem(R.id.action_bond).setTitle("Delete Bond");
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        Log.v("servicelist", id + "");
        switch (id) {
            case android.R.id.home :
                mBleWrapper.disconnect();
                mBleWrapper.close();
                onBackPressed();
                break;
            case R.id.action_connect :
                mConnectStatus.setText("connecting");
                mBleWrapper.connect(mScanResult);
                menu.findItem(R.id.connect_indicator).setActionView(R.layout.progressbar_scanning);
                break;
            case R.id.action_disconnect :
//                mListAdapter.clear();
                mListAdapter.notifyDataSetChanged();
                mBleWrapper.disconnect();
                break;
            case R.id.action_bond :
                if (item.getTitle().equals("Bond")) {
                    mBleWrapper.createBond();
                } else if (item.getTitle().equals("Delete Bond")) {
                    mBleWrapper.removeBond();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uiDeviceFound(ScanResult scanResult) {

    }

    @Override
    public void uiDeviceConnected(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectStatus.setText("connected");
                invalidateOptionsMenu();
            }
        });

    }

    @Override
    public void uiDeviceDisconnected(final BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectStatus.setText("disconnected");
                invalidateOptionsMenu();
//                mListAdapter.clear();
            }
        });

    }

    @Override
    public void uiServicesDiscovered(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, final List<BluetoothGattService> services) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListAdapter.clear();
                for (BluetoothGattService service : services) {
                    //mListAdapter.add(service);
                    mListAdapter.addGroup(service);

                    List<BluetoothGattCharacteristic> childList = service.getCharacteristics();

                    mListAdapter.addChild(service, childList);
                }
                mListAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt bluetoothGatt, final BluetoothGattCharacteristic ch, final byte[] value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBleWrapper == null || mListAdapter == null) return;

                mListAdapter.newCharacteristicValueFomat(ch, value);
                mListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void uiOpenLoadingForReadOrWriteValue(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if(mSnackBar == null) {
//                    mSnackBar = new SnackBar(ServiceListActivity.this, title);
//                    mSnackBar.show();
//                } else {
//                    mSnackBar.setTitle(title);
//                   // mSnackBar.show();
//                }
                Toast.makeText(ServiceListActivity.this, title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void uiCloseLoadingForReadOrWriteValue() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if(mSnackBar != null) {
//                    mSnackBar.hide();
//                    mSnackBar.dismiss();
//                    mSnackBar = null;
//                }
            }
        });
    }

    @Override
    public void uiWriteValueSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, final String descriptor) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ServiceListActivity.this, "Writing to " + descriptor + " was finished successfully!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void uiWriteValueFailed(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, final String descriptor) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ServiceListActivity.this, "Writing to " + descriptor + " was failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
