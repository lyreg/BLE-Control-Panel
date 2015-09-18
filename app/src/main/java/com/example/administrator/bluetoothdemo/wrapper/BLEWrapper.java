package com.example.administrator.bluetoothdemo.wrapper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2015/8/31.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEWrapper {

    private Activity mActivity = null;
    private BLEWrapperUICallBack mCallBack;
    private static BLEWrapperUICallBack DEFOUT_CALLBACK = new BLEWrapperUICallBack.CallBack(){};

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothManager mBluetoothManager = null;
    private BluetoothLeScanner mBluetoothLeSanner;
    Handler mHandler;
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothGatt mBluetoothGatt = null;
    private String mBluetoothAddress = null;
    private List<BluetoothGattService> mBluetoothGattServices = null;
    private boolean mConnected = false;

    public BLEWrapper(Activity parent, BLEWrapperUICallBack callBack) {
        mActivity = parent;
        mCallBack = callBack;
        mHandler = new Handler();
        if(callBack == null) mCallBack = DEFOUT_CALLBACK;
    }

    /**
     *  检查设备是否有蓝牙功能,以及是否支持BLE
     */
    public boolean checkBleHardwareAvailable() {
        /*先检查是否有蓝牙功能*/
        final BluetoothManager manager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) return false;
        /*再获取蓝牙adapter*/
        final BluetoothAdapter adapter = manager.getAdapter();
        if(adapter == null) return false;

        /*接着检查是否支持BLE*/
        boolean hasBle = mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        return hasBle;
    }

    /**
     *  检查蓝牙是否打开
     */
    public boolean isBluetoothEnable() {
        final BluetoothManager manager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if(manager == null) return false;

        final BluetoothAdapter adapter = manager.getAdapter();
        if(adapter == null) return false;

        return adapter.isEnabled();
    }

    /**
     *  初始化，并获取BluetoothManager和BluetoothAdapter
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBluetoothManager == null) {
                return false;
            }
        }
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if(mBluetoothAdapter == null) {
                return false;
            }
        }
        return true;
    }
    /**
     * 搜索蓝牙设备
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startScanning() {
        mBluetoothLeSanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeSanner != null)
            mBluetoothLeSanner.startScan(mScanCallback);
    }
    public void startScanning(Runnable run, long delayMillis) {
        mBluetoothLeSanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeSanner != null) {
            mHandler.postDelayed(run, delayMillis);
            mBluetoothLeSanner.startScan(mScanCallback);
        }
    }
    /**
     * 停止搜索蓝牙设备
     */
    public void stopScanning() {
        mBluetoothLeSanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeSanner != null) {
            mBluetoothLeSanner.stopScan(mScanCallback);
        }
    }
    /*搜索ble设备的回调*/
    private ScanCallback mScanCallback = new ScanCallback() {
        /**
         * Callback when a BLE advertisement has been found.
         *
         * @param callbackType Determines how this callback was triggered. Currently could only be
         *                     {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES}.
         * @param result       A Bluetooth LE scan result.
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            mCallBack.uiDeviceFound(result);
        }
    };
    /*连接蓝牙设备*/
    public boolean connect(ScanResult scanResult) {
        if(mBluetoothAdapter == null || scanResult == null) return false;

        mBluetoothAddress = scanResult.getDevice().getAddress();

        if(mBluetoothGatt != null && mBluetoothGatt.getDevice().getAddress().equals(mBluetoothAddress)) {
            return mBluetoothGatt.connect();
        }

        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mBluetoothAddress);
        if(mBluetoothDevice == null) {
            return false;
        } else {
            mBluetoothGatt = mBluetoothDevice.connectGatt(mActivity, false, mBluetoothGattCallback);
        }
        return true;
    }

    public void disconnect() {
        if (mBluetoothGatt != null) mBluetoothGatt.disconnect();
        mCallBack.uiDeviceDisconnected(mBluetoothGatt, mBluetoothDevice);
    }
    public void close() {
        if (mBluetoothGatt != null) mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public boolean isConnected() {
        return mConnected;
    }
    /**
     * 请求读取characteristic的value，然后在onCharacteristicRead回调中处理读取到的value
     */
    public void requestCharacteristicValue(BluetoothGattCharacteristic gattChara) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;
        mBluetoothGatt.readCharacteristic(gattChara);
        Log.v("BLEWrapper", "requestChar");
    }

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        /**
         * Callback indicating when GATT client has connected/disconnected to/from a remote
         * GATT server.
         *
         * @param gatt     GATT client
         * @param status   Status of the connect or disconnect operation.
         *                 {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         * @param newState Returns the new connection state. Can be one of
         *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
         *                 {@link BluetoothProfile#STATE_CONNECTED}
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                mConnected = true;
                mCallBack.uiDeviceConnected(gatt, mBluetoothDevice);

                if(gatt != null) gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnected = false;
                mCallBack.uiDeviceDisconnected(gatt, mBluetoothDevice);
            }
        }

        /**
         * Callback invoked when the list of remote services, characteristics and descriptors
         * for the remote device have been updated, ie new services have been discovered.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if(status == BluetoothGatt.GATT_SUCCESS) {
                if(mBluetoothGattServices != null && mBluetoothGattServices.size() > 0) mBluetoothGattServices.clear();

                if(mBluetoothGatt != null) {
                    mBluetoothGattServices = mBluetoothGatt.getServices();
                    mCallBack.uiServicesDiscovered(gatt, mBluetoothDevice, mBluetoothGattServices);
                }
            }
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         *
         * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
         * @param characteristic Characteristic that was read from the associated
         *                       remote device.
         * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if( status == BluetoothGatt.GATT_SUCCESS) {
                //getCharacteristicValueFormat(characteristic);
                if (gatt == null || characteristic == null || mBluetoothAdapter == null) return;
                byte[] val = characteristic.getValue();
                mCallBack.uiNewValueForCharacteristic(gatt, characteristic, val);
                Log.v("BluetoothGattCallback", "onCharacteristicRead " + val.toString());
            }
        }

        /**
         * Callback indicating the result of a characteristic write operation.
         * <p/>
         * <p>If this callback is invoked while a reliable write transaction is
         * in progress, the value of the characteristic represents the value
         * reported by the remote device. An application should compare this
         * value to the desired value to be written. If the values don't match,
         * the application must abort the reliable write transaction.
         *
         * @param gatt           GATT client invoked {@link BluetoothGatt#writeCharacteristic}
         * @param characteristic Characteristic that was written to the associated
         *                       remote device.
         * @param status         The result of the write operation
         *                       {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        /**
         * Callback triggered as a result of a remote characteristic notification.
         *
         * @param gatt           GATT client the characteristic is associated with
         * @param characteristic Characteristic that has been updated as a result
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        /**
         * Callback reporting the result of a descriptor read operation.
         *
         * @param gatt       GATT client invoked {@link BluetoothGatt#readDescriptor}
         * @param descriptor Descriptor that was read from the associated
         *                   remote device.
         * @param status     {@link BluetoothGatt#GATT_SUCCESS} if the read operation
         */
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        /**
         * Callback indicating the result of a descriptor write operation.
         *
         * @param gatt       GATT client invoked {@link BluetoothGatt#writeDescriptor}
         * @param descriptor Descriptor that was writte to the associated
         *                   remote device.
         * @param status     The result of the write operation
         *                   {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        /**
         * Callback invoked when a reliable write transaction has been completed.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#executeReliableWrite}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the reliable write
         */
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        /**
         * Callback reporting the RSSI for a remote device connection.
         * <p/>
         * This callback is triggered in response to the
         * {@link BluetoothGatt#readRemoteRssi} function.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#readRemoteRssi}
         * @param rssi   The RSSI value for the remote device
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the RSSI was read successfully
         */
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    public static boolean isContainsScanResult(List<ScanResult> list, ScanResult result) {

        for(ScanResult tmpResult : list) {
            if(tmpResult.getDevice().getAddress().equals(result.getDevice().getAddress())) {
                return true;
            }
        }
        return false;
    }

    public static boolean removeScanResult(List<ScanResult> list, String address) {
        for(int i = 0; i < list.size(); i++) {
            ScanResult tmpResult = list.get(i);
            if(tmpResult.getDevice().getAddress().equals(address)) {
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothManager getmBluetoothManager() {
        return mBluetoothManager;
    }
}
