package rici.roplug.open.adapter;

import java.util.ArrayList;

import com.rici.wifi.bean.DeviceInfo;

import rici.roplug.open.R;
import rici.roplug.open.bll.bean.SocketInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private OnDeviceItemClickListener mListener = null;
	private ArrayList<SocketInfo> deviceList = new ArrayList<SocketInfo>();

	public DeviceAdapter(Context context, ArrayList<SocketInfo> deviceList) {
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.deviceList = deviceList;
	}

	@Override
	public int getCount() {
		return deviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Drawable drawable;
		holder = new ViewHolder();
		int deviceState = -1;
		if(deviceList!=null && position <deviceList.size()){
		convertView = this.mInflater.inflate(R.layout.search_device_list_item,
				null);
		holder.deviceItem = (LinearLayout) convertView
				.findViewById(R.id.ll_searth_list_item);
		holder.deviceVersion = (ImageView)convertView.findViewById(R.id.iv_device_version);
		holder.deviceName = (TextView) convertView
				.findViewById(R.id.tv_device_name);
		holder.deviceType = (TextView) convertView
				.findViewById(R.id.tv_device_link_type);
		holder.button = (TextView) convertView
				.findViewById(R.id.tv_search_list);
		convertView.setTag(holder);
		holder.deviceName.setText(deviceList.get(position).getDeviceInfo()
				.getDeviceName());
		holder.deviceType.setText(deviceList.get(position).getDeviceInfo().getMacAddress());
		holder.deviceType.setCompoundDrawablePadding(mContext
				.getResources().getDimensionPixelOffset(
						R.dimen.search_button_margin_padding_bottom));
		deviceState = deviceList.get(position).getDeviceInfo().getDeviceState();
		
		switch (deviceState) {
		case DeviceInfo.DEVICE_TYPE_LOCATION_NEW:
			drawable = mContext.getResources().getDrawable(
					R.drawable.button_list_link_bg);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.button.setText("");
			holder.button.setCompoundDrawables(null, null, drawable, null);
			break;
		case DeviceInfo.DEVICE_TYPE_LOCATION_OFFLINE:
			holder.button.setText(R.string.device_list_offline);
			holder.button.setTextColor(mContext.getResources().getColor(
					R.color.search_list_offline_text_color));
			drawable = mContext.getResources().getDrawable(
					R.drawable.y335_button_list_offline);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.button.setCompoundDrawables(drawable, null, null, null);
			break;
		case DeviceInfo.DEVICE_TYPE_LOCATION_ONLINE:
			holder.button.setText(R.string.device_list_online);
			holder.button.setTextColor(mContext.getResources().getColor(
					R.color.search_list_online_text_color));
			drawable = mContext.getResources().getDrawable(
					R.drawable.y335_button_list_online);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			break;
		case DeviceInfo.DEVICE_TYPE_REMOTE_OFFLINE:
			holder.button.setText(R.string.device_list_offline);
			holder.button.setTextColor(mContext.getResources().getColor(
					R.color.search_list_offline_text_color));
			drawable = mContext.getResources().getDrawable(
					R.drawable.y335_button_list_offline);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.button.setCompoundDrawables(drawable, null, null, null);
			break;
		case DeviceInfo.DEVICE_TYPE_REMOTE_ONLINE:
			holder.button.setText(R.string.device_list_online);
			holder.button.setTextColor(mContext.getResources().getColor(
					R.color.search_list_online_text_color));
			drawable = mContext.getResources().getDrawable(
					R.drawable.y335_button_list_online);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			int currentVersion = deviceList.get(position).getDeviceInfo().getCurrentFirmwareVersion();
			if(deviceList.get(position).getDeviceInfo().isShouldUpdateFirmware(deviceList.get(position).getDeviceInfo().getFiremwareType(),currentVersion,deviceList.get(position).getDeviceInfo())){
				holder.deviceVersion.setVisibility(View.VISIBLE);
			}
			else{
				holder.deviceVersion.setVisibility(View.INVISIBLE);
			}
				holder.button.setCompoundDrawables(drawable, null, null, null);
			break;
		case DeviceInfo.DEVICE_TYPE_FIRMWARE_UPDATING:
			holder.button.setText(R.string.firmware_updating);
			holder.button.setCompoundDrawables(null, null, null, null);
			break;
		}
		holder.deviceItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onItemClick(position);
				}
			}
		});
		holder.deviceItem.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (mListener != null) {
					mListener.onItemLongClick(position);
				}
				return true;
			}
		});
		}
		return convertView;
	}

	private class ViewHolder {
		LinearLayout deviceItem;
		ImageView deviceVersion;
		TextView deviceName;
		TextView deviceType;
		TextView button;
	}

	public interface OnDeviceItemClickListener {
		public void onItemClick(int deviceIndex);
		public void onItemLongClick(int deviceIndex);
	}

	public void setOnDeviceItemClickListener(OnDeviceItemClickListener listener) {
		this.mListener = listener;
	}
}
