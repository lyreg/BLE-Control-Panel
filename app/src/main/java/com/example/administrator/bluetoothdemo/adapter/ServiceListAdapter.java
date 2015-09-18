package com.example.administrator.bluetoothdemo.adapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.administrator.bluetoothdemo.util.BleNamesResolver;
import com.example.administrator.bluetoothdemo.R;

import java.util.Locale;

/**
 * Created by Administrator on 2015/9/6.
 */
public class ServiceListAdapter extends ArrayAdapter<BluetoothGattService> {

    private Context mContext;
    private LayoutInflater mInflater;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public ServiceListAdapter(Context context, int resource) {
        super(context, resource);
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void add(BluetoothGattService object) {
        super.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.service_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.servcieName = (TextView) convertView.findViewById(R.id.list_services_name);
            viewHolder.serviceType = (TextView) convertView.findViewById(R.id.list_service_type);
            viewHolder.serviceUUID = (TextView) convertView.findViewById(R.id.list_services_uuid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothGattService mGattService = getItem(position);
        String uuid = mGattService.getUuid().toString().toLowerCase(Locale.getDefault());
        String name = BleNamesResolver.resolveServiceName(uuid);
        String type = (mGattService.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) ? "Primary" : "Secondary";

        viewHolder.servcieName.setText(name);
        viewHolder.serviceType.setText(type);
        viewHolder.serviceUUID.setText(uuid);

        return convertView;
    }

    class ViewHolder {
        public TextView servcieName;
        public TextView serviceUUID;
        public TextView serviceType;
    }
}
