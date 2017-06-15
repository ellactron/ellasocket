package rici.roplug.open.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import rici.roplug.open.R;
import rici.roplug.open.bll.bean.SocketInfo;
import rici.roplug.open.common.util.SortComparator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ElectricityAnalyseAdapter extends BaseAdapter {
	private ArrayList<SocketInfo> mList= new ArrayList<SocketInfo>();
	private Context mContext;
	private DecimalFormat decimalFormat = new DecimalFormat("00");
	public ElectricityAnalyseAdapter(Context context,
			ArrayList<SocketInfo> socketInfos) {
		mContext = context;
		mList.addAll(socketInfos);
		Collections.sort(mList, new SortComparator());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		AppItem appItem;
		Drawable drawable;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.electric_analyse_item_layout, null);

			appItem = new AppItem();
			appItem.mAppName = (TextView) v
					.findViewById(R.id.tv_electricAnalyseName);
			appItem.mAppIcon = (TextView) v
					.findViewById(R.id.tv_electricAnalyseElectric);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem) convertView.getTag();
		}
		SocketInfo socketInfo = mList.get(position);
		appItem.mAppName.setText(socketInfo.getDeviceInfo().getDeviceName());
		if (socketInfo.getSavedElectric() > 0) {
			appItem.mAppIcon.setTextColor(mContext.getResources().getColor(
					R.color.electricity_analyse_down_color));
			drawable = mContext.getResources().getDrawable(
					R.drawable.y335_electricity_analyse_down_icon);
		} else {
			appItem.mAppIcon.setTextColor(mContext.getResources().getColor(
					R.color.electricity_analyse_up_color));
			drawable = mContext.getResources().getDrawable(
					R.drawable.y335_electricity_analyse_up_icon);
		}
		if (socketInfo.getSavedElectric() != 0) {
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			appItem.mAppIcon.setCompoundDrawables(drawable, null, null, null);
		} else {
			appItem.mAppIcon.setCompoundDrawables(null, null, null, null);
			appItem.mAppIcon.setTextColor(mContext.getResources().getColor(
					R.color.system_color_black));
		}
		
		appItem.mAppIcon.setText(decimalFormat.format(Math.abs(socketInfo.getSavedElectric()))
				+ "W");
		return convertView;
	}

	/**
	 * 每个应用显示的内容，包括图标和名称
	 * 
	 * @author Yao.GUET
	 * 
	 */
	class AppItem {
		TextView mAppIcon;
		TextView mAppName;
	}

}
