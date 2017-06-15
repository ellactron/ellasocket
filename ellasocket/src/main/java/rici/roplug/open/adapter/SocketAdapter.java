package rici.roplug.open.adapter;

import java.util.ArrayList;

import rici.roplug.open.R;
import rici.roplug.open.bll.bean.SocketInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SocketAdapter extends BaseAdapter {
	private ArrayList<SocketInfo> socketInfos;
	private Context mContext;
	private SocketInfo socketInfo = null;
	private int currentIndex;

	public SocketAdapter(Context context, ArrayList<SocketInfo> list,
			int currentIndex) {
		mContext = context;
		this.currentIndex = 0;
		socketInfos = list;
		this.currentIndex = currentIndex;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return socketInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return socketInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		socketInfo = socketInfos.get(position);
		AppItem appItem;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.socket_item_layout, null);
			appItem = new AppItem();
			appItem.mAppIcon = (ImageView) v.findViewById(R.id.iv_socket_icon);
			appItem.mAppName = (TextView) v.findViewById(R.id.socketName);
			appItem.currentView = (View) v.findViewById(R.id.iv_current);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem) convertView.getTag();
		}
		// set the icon
		appItem.mAppIcon
				.setBackgroundResource(socketInfo.getPowerOn() ? R.drawable.y335_socket_item_poweron
						: R.drawable.y335_socket_item_poweroff);
		appItem.mAppName.setText(socketInfo.getDeviceInfo().getDeviceName());
		if (position == currentIndex) {
			appItem.currentView.setVisibility(View.VISIBLE);
		} else {
			appItem.currentView.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * 每个应用显示的内容，包括图标和名称
	 * 
	 * @author Yao.GUET
	 * 
	 */
	class AppItem {
		ImageView mAppIcon;
		TextView mAppName;
		View currentView;
	}

	public ArrayList<SocketInfo> getSocketInfos() {
		return socketInfos;
	}

	public void setSocketInfos(ArrayList<SocketInfo> socketInfos) {
		this.socketInfos = socketInfos;
	}

}
