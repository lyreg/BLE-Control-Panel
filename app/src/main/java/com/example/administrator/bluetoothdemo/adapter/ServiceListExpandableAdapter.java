package com.example.administrator.bluetoothdemo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bluetoothdemo.util.BleNamesResolver;
import com.example.administrator.bluetoothdemo.R;
import com.example.administrator.bluetoothdemo.util.ByteUtil;
import com.example.administrator.bluetoothdemo.util.CustomTextWatcher;
import com.example.administrator.bluetoothdemo.view.TextInputAlertDialogBuilder;
import com.example.administrator.bluetoothdemo.wrapper.BLEWrapper;

import org.apache.http.auth.MalformedChallengeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/7.
 */
public class ServiceListExpandableAdapter extends BaseExpandableListAdapter {

    private Activity mParent;
    private List<BluetoothGattService> mGattServicesGroups;
    private List<List<BluetoothGattCharacteristic>> mGattCharGroups;
    private BLEWrapper mBleWrapper = null;

    private LayoutInflater mLayoutInflater;

    // 用来记录switch按钮状态的Map
    private static List<Map<Integer, Boolean>> isCheckedList;

    private static Map<String, CharItemBean> mCharItemList;

    private int mSelectedGroupPosition;
    private int mSelectedChildPosition;
    private BluetoothGattCharacteristic mSelectedChar;
    private String mSelectedCharValue;

    public ServiceListExpandableAdapter(Activity parent, BLEWrapper bleWrapper) {
        mParent = parent;
        mBleWrapper = bleWrapper;
        mLayoutInflater = parent.getLayoutInflater();
        mGattServicesGroups = new ArrayList<BluetoothGattService>();
        mGattCharGroups = new ArrayList<List<BluetoothGattCharacteristic>>();

        isCheckedList = new ArrayList<Map<Integer, Boolean>>();
        mCharItemList = new HashMap<String, CharItemBean>();

        mSelectedGroupPosition = mSelectedChildPosition = -1;
        mSelectedCharValue = "";
        mSelectedChar = null;
    }

    public void addGroup(BluetoothGattService gattService) {
        mGattServicesGroups.add(gattService);
    }

    public void addChild(BluetoothGattService gattService, List<BluetoothGattCharacteristic> gattCharacteristics) {
        int index = mGattServicesGroups.indexOf(gattService);
        mGattCharGroups.add(gattCharacteristics);

        Map<Integer, Boolean> childChecked = new HashMap<Integer, Boolean>();
        for (int i = 0; i < gattCharacteristics.size(); i++) {
            childChecked.put(i, false);
            BluetoothGattCharacteristic ch = gattCharacteristics.get(i);
            String uuid = ch.getUuid().toString().toLowerCase(Locale.getDefault());
            mCharItemList.put(uuid, new CharItemBean(uuid));
        }
        isCheckedList.add(childChecked);
    }

    public void clear() {
        mGattServicesGroups.clear();
        mGattCharGroups.clear();

    }

    public void newCharacteristicValueFomat(BluetoothGattCharacteristic ch, byte[] rawValue) {
        Log.v("ListAdapter", "newCharacteristicValueFomat " + mSelectedGroupPosition+ " "+ mSelectedChildPosition);
        mSelectedChar = ch;

        String uuid = ch.getUuid().toString().toLowerCase(Locale.getDefault());

        if(rawValue != null && rawValue.length > 0) {
            StringBuilder str = new StringBuilder(rawValue.length);
            for(byte rawValueByte : rawValue) {
                str.append(String.format("%02X", rawValueByte));
            }
            mSelectedCharValue = "0x" + str.toString();

            mCharItemList.get(uuid).setValue(mSelectedCharValue);

        } else {
            mSelectedCharValue = "";

            mCharItemList.get(uuid).setValue(mSelectedCharValue);
        }
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return mGattServicesGroups.size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return mGattCharGroups.get(groupPosition).size();
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition) {
        return mGattCharGroups.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGattCharGroups.get(groupPosition).get(childPosition);
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     * @see Adapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder mGroupViewHolder = null;
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.service_list_item, null);
            mGroupViewHolder = new GroupViewHolder();
            mGroupViewHolder.servcieName = (TextView) convertView.findViewById(R.id.list_services_name);
            mGroupViewHolder.serviceType = (TextView) convertView.findViewById(R.id.list_service_type);
            mGroupViewHolder.serviceUUID = (TextView) convertView.findViewById(R.id.list_services_uuid);

            convertView.setTag(mGroupViewHolder);
        } else {
            mGroupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        Log.v("getGroupView", mGattServicesGroups.size() + " " + groupPosition);
        BluetoothGattService mGattService = mGattServicesGroups.get(groupPosition);
        String uuid = mGattService.getUuid().toString().toLowerCase(Locale.getDefault());
        String name = BleNamesResolver.resolveServiceName(uuid);
        String type = (mGattService.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) ? "Primary" : "Secondary";

        mGroupViewHolder.servcieName.setText(name);
        mGroupViewHolder.serviceType.setText(type);
        mGroupViewHolder.serviceUUID.setText(uuid);

        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder mChildViewHolder = null;

        final BluetoothGattCharacteristic gattCharacteristic = mGattCharGroups.get(groupPosition).get(childPosition);
        int props = gattCharacteristic.getProperties();
        final String uuid = gattCharacteristic.getUuid().toString().toLowerCase(Locale.getDefault());
        String name = BleNamesResolver.resolveCharacteristicName(uuid);
        String propertiesString = BleNamesResolver.resolveProperties(props);

        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.service_list_child_item, null);
            mChildViewHolder = new ChildViewHolder();
            mChildViewHolder.charName = (TextView) convertView.findViewById(R.id.list_char_name);
            mChildViewHolder.charUUID = (TextView) convertView.findViewById(R.id.list_char_uuid);

            mChildViewHolder.charProp = (TextView) convertView.findViewById(R.id.list_char_prop);
            mChildViewHolder.charVal = (TextView) convertView.findViewById(R.id.list_char_value);
            mChildViewHolder.charReadBtn = (Button) convertView.findViewById(R.id.list_char_btn_read);
            mChildViewHolder.charWriteBtn = (Button) convertView.findViewById(R.id.list_char_btn_write);
            mChildViewHolder.charNotifyBtn = (Switch) convertView.findViewById(R.id.list_char_btn_notify);
            mChildViewHolder.charIndicateBtn = (Switch) convertView.findViewById(R.id.list_char_btn_indicate);

            convertView.setTag(R.id.tag_first, mChildViewHolder);
            mChildViewHolder.charVal.setTag(uuid);

        } else {
            mChildViewHolder = (ChildViewHolder) convertView.getTag(R.id.tag_first);
            mChildViewHolder.charVal.setTag(uuid);
        }

//        final BluetoothGattCharacteristic gattCharacteristic = mGattCharGroups.get(groupPosition).get(childPosition);
//        int props = gattCharacteristic.getProperties();
//        String uuid = gattCharacteristic.getUuid().toString().toLowerCase(Locale.getDefault());
//        String name = BleNamesResolver.resolveCharacteristicName(uuid);
//        String propertiesString = BleNamesResolver.resolveProperties(props);

        mChildViewHolder.charName.setText(name);
        mChildViewHolder.charUUID.setText(uuid);
        mChildViewHolder.charProp.setText(propertiesString);

        if(BleNamesResolver.hasReadProperty(props)) {
            mChildViewHolder.charReadBtn.setEnabled(true);
        } else {
            mChildViewHolder.charReadBtn.setEnabled(false);
        }
        if(BleNamesResolver.hasWriteProperty(props) || BleNamesResolver.hasWriteNoRespProperty(props)) {
            mChildViewHolder.charWriteBtn.setEnabled(true);
        } else {
            mChildViewHolder.charWriteBtn.setEnabled(false);
        }
        if(BleNamesResolver.hasNotifyProperty(props)) {
            mChildViewHolder.charNotifyBtn.setEnabled(true);
            mChildViewHolder.charNotifyBtn.setAlpha(1f);
        } else {
            mChildViewHolder.charNotifyBtn.setEnabled(false);
            mChildViewHolder.charNotifyBtn.setAlpha(0.3f);
        }
        if(BleNamesResolver.hasIndicateProperty(props)) {
            mChildViewHolder.charIndicateBtn.setEnabled(true);
            mChildViewHolder.charIndicateBtn.setAlpha(1f);
        } else {
            mChildViewHolder.charIndicateBtn.setEnabled(false);
            mChildViewHolder.charIndicateBtn.setAlpha(0.3f);
        }

        mChildViewHolder.charReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("readBtn", "read" +" "+groupPosition+ " "+ childPosition);
                mSelectedGroupPosition = groupPosition;
                mSelectedChildPosition = childPosition;
                mBleWrapper.requestCharacteristicValue((BluetoothGattCharacteristic) getChild(groupPosition,childPosition));
            }
        });
        mChildViewHolder.charWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputAlertDialogBuilder mAlertDialogBuilder = new TextInputAlertDialogBuilder(mParent);

                final TextInputLayout mTextInputLayout = (TextInputLayout) LayoutInflater.from(mParent).inflate(R.layout.dialog_inputtext_layout,null);
                mTextInputLayout.setHint("请按16进制格式输入(0x开头):");
                mTextInputLayout.getEditText().addTextChangedListener(new CustomTextWatcher(mTextInputLayout.getEditText()));

                mAlertDialogBuilder.setTitle("Write New Value");
                mAlertDialogBuilder.setView(mTextInputLayout);

                mAlertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = mTextInputLayout.getEditText();
                        String newValue = editText.getText().toString().toLowerCase(Locale.getDefault());
                        if(newValue != null && !newValue.equals("")) {
                            byte[] dataToWrite = ByteUtil.parseHexStringToBytes(newValue);
                            mBleWrapper.writeDataToCharacteristic((BluetoothGattCharacteristic) getChild(groupPosition,childPosition),dataToWrite);
                        }

                    }
                });
                mAlertDialogBuilder.setNegativeButton("取消", null);

                mAlertDialogBuilder.show();
            }
        });

//        if(isCheckedList.get(groupPosition).get(childPosition)) {
//            mChildViewHolder.charNotifyBtn.setChecked(true);
//        } else {
//            mChildViewHolder.charNotifyBtn.setChecked(false);
//        }

//        mChildViewHolder.charNotifyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isCheckedList.get(groupPosition).get(childPosition)) {
//                    Log.v("checked", groupPosition + " " + childPosition + " true");
//                    isCheckedList.get(groupPosition).put(childPosition, false);
//                } else {
//                    Log.v("checked", groupPosition + " " + childPosition + " false");
//                    isCheckedList.get(groupPosition).put(childPosition, true);
//                }
//
//            }
//        });
        mChildViewHolder.charNotifyBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSelectedGroupPosition = groupPosition;
                mSelectedChildPosition = childPosition;

                mCharItemList.get(uuid).setIsCheckedN(isChecked);

                mBleWrapper.setNotificationForCharacteristic(gattCharacteristic, isChecked);
            }
        });

        mChildViewHolder.charIndicateBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mCharItemList.get(uuid).setIsCheckedI(isChecked);

            }
        });

//        if(mSelectedGroupPosition == groupPosition && mSelectedChildPosition == childPosition) {
//            if(mSelectedCharValue != null && mSelectedChar != null) {
//                if(getChild(groupPosition,childPosition).equals(mSelectedChar)) {
//                    mChildViewHolder.charVal.setText(mSelectedCharValue);
//                }
//            }
//        } else {
//            mChildViewHolder.charVal.setText("");
//        }
        String charVal = mCharItemList.get(uuid).getValue();
        boolean isCheckedN = mCharItemList.get(uuid).isCheckedN();
        boolean isCheckedI = mCharItemList.get(uuid).isCheckedI();
        mChildViewHolder.charVal.setText(charVal);
        mChildViewHolder.charNotifyBtn.setChecked(isCheckedN);
        mChildViewHolder.charIndicateBtn.setChecked(isCheckedI);

        return convertView;
    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {
        public TextView servcieName;
        public TextView serviceUUID;
        public TextView serviceType;
    }

    private class ChildViewHolder {
        public TextView charName;
        public TextView charUUID;
        public TextView charProp;
        public TextView charVal;

        public Button charReadBtn;
        public Button charWriteBtn;
        public Switch charNotifyBtn;
        public Switch charIndicateBtn;
    }
}
