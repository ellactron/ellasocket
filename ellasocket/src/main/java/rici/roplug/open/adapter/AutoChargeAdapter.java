package rici.roplug.open.adapter;

import java.util.List;

import rici.roplug.open.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AutoChargeAdapter extends BaseAdapter {
	private List<String> mList;
	private Context mContext;
	public AutoChargeAdapter(Context context,
			List<String> list) {
		mContext = context;		
		mList = list;
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
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.auto_charge_item_layout, null);

			appItem = new AppItem();
			appItem.mAppName = (TextView) v.findViewById(R.id.deviceName);
			appItem.mAppIcon = (TextView)v.findViewById(R.id.deviceIcon);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem) convertView.getTag();
		}
		
		// set the app name
		appItem.mAppName.setText(mList.get(position));
		// set the icon
		if(position==0){
			appItem.mAppIcon.setBackgroundResource(R.drawable.socket_electric_analyse_first);
			//appItem.mAppIcon.setBackground(mContext.getResources().getDrawable(R.drawable.socket_electric_analyse_first));			
		}
		else if(position == 1){
			appItem.mAppIcon.setBackgroundResource(R.drawable.socket_electric_analyse_second);
			//appItem.mAppIcon.setBackground(mContext.getResources().getDrawable(R.drawable.socket_electric_analyse_second));			
		}
		else if(position == 2){
			appItem.mAppIcon.setBackgroundResource(R.drawable.socket_electric_analyse_third);
			//appItem.mAppIcon.setBackground(mContext.getResources().getDrawable(R.drawable.socket_electric_analyse_third));			
		}
		else{
			//appItem.mAppIcon.setBackground(mContext.getResources().getDrawable(R.drawable.socket_electric_analyse_third));	
			appItem.mAppIcon.setVisibility(View.GONE);
		}
		appItem.mAppIcon.setText(String.valueOf(position+1));
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
