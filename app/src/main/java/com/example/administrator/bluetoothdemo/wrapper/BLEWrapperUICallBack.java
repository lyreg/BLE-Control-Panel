package com.example.administrator.bluetoothdemo.wrapper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;

import java.util.List;

/**
 * Created by Administrator on 2015/8/31.
 */
public interface BLEWrapperUICallBack {

    public void uiDeviceFound(final ScanResult scanResult);
    public void uiDeviceConnected(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice);
    public void uiDeviceDisconnected(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice);
    public void uiServicesDiscovered(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, List<BluetoothGattService> services);
    public void uiNewValueForCharacteristic(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic ch, byte[] value);
    public void uiOpenLoadingForReadOrWriteValue(String title);
    public void uiCloseLoadingForReadOrWriteValue();
    public void uiWriteValueSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, String descriptor);
    public void uiWriteValueFailed(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, String descriptor);

    public static class CallBack implements BLEWrapperUICallBack {
        @Override
        public void uiDeviceFound(ScanResult result) {
        }

        @Override
        public void uiDeviceConnected(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void uiDeviceDisconnected(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void uiServicesDiscovered(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, List<BluetoothGattService> services) {

        }

        @Override
        public void uiNewValueForCharacteristic(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic ch, byte[] value) {

        }

        @Override
        public void uiOpenLoadingForReadOrWriteValue(String title) {

        }

        @Override
        public void uiCloseLoadingForReadOrWriteValue() {

        }

        @Override
        public void uiWriteValueSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, String descriptor) {

        }

        @Override
        public void uiWriteValueFailed(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, String descriptor) {

        }
    }

}
