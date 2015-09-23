package com.example.administrator.bluetoothdemo.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.bluetoothdemo.MainActivity;
import com.example.administrator.bluetoothdemo.ServiceListActivity;
import com.example.administrator.bluetoothdemo.wrapper.BLEWrapper;
import com.example.administrator.bluetoothdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/31.
 */
public class DeviceListAdapter extends BaseAdapter {

    private List<ScanResult> scanResultList;
    private LayoutInflater mLayoutInflater;
    private Activity mParent;
    private BLEWrapper mBleWrapper;

    public DeviceListAdapter(Activity parent, BLEWrapper bleWrapper, List<ScanResult> list) {
        mParent = parent;
        mLayoutInflater = parent.getLayoutInflater();
        mBleWrapper = bleWrapper;
        if(list != null) {
            scanResultList = list;
        } else {
            scanResultList = new ArrayList<ScanResult>();
        }
    }

    public void addDevice(ScanResult scanResult) {
//        if(scanResultList.contains(scanResult) == false) {
//            scanResultList.add(scanResult);
//        }
        if(!BLEWrapper.isContainsScanResult(scanResultList, scanResult)) {
            scanResultList.add(scanResult);
        } else {
            BLEWrapper.removeScanResult(scanResultList, scanResult.getDevice().getAddress());
            scanResultList.add(scanResult);
        }
    }

    public void clearList() {
        scanResultList.clear();
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return scanResultList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return scanResultList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.device_scan_item, null);

            mViewHolder = new ViewHolder();
            mViewHolder.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            mViewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.deviceAddress);
            mViewHolder.deviceRssi = (TextView) convertView.findViewById(R.id.deviceRssi);
            mViewHolder.connectBtn = (Button) convertView.findViewById(R.id.connect_button);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        ScanResult result = scanResultList.get(position);
        BluetoothDevice device = result.getDevice();
        int rssi = result.getRssi();

        String name = device.getName();
        String address = device.getAddress();
        String rssiString = (rssi == 0) ? "N/A" : rssi + " db";

        if(name == null || name.length() <= 0) name = "Unknown Device";

        mViewHolder.deviceName.setText(name);
        mViewHolder.deviceAddress.setText(address);
        mViewHolder.deviceRssi.setText(rssiString);

        mViewHolder.connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(mParent, ServiceListActivity.class);


                if (mBleWrapper.isScanning()) {
                    mParent.invalidateOptionsMenu();
                    mBleWrapper.stopScanning();
                }

                intent.putExtra(ServiceListActivity.EXTRAS_DEVICE, (ScanResult) getItem(position));

                mParent.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        public TextView deviceName;
        public TextView deviceAddress;
        public TextView deviceRssi;
        public Button connectBtn;
    }

}
