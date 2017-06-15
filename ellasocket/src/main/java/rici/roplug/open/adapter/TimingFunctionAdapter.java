package rici.roplug.open.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rici.roplug.open.R;
import rici.roplug.open.bll.bean.TimingFunctionInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimingFunctionAdapter extends BaseAdapter {
	private List<TimingFunctionInfo> mList;
	private Context mContext;
	private TimingFunctionInfo timingFunctionInfo = null;
	private OnItemClickListener onItemClickListener = null;
	
	public TimingFunctionAdapter(Context context, ArrayList<TimingFunctionInfo> allTimingFunctionInfos) {
		mContext = context;
		mList = allTimingFunctionInfos;
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
		timingFunctionInfo = mList.get(position);
		final int socketIndex = position;
		AppItem appItem;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.timing_function_list_item_layout, null);

			appItem = new AppItem();
			appItem.llLayout = (LinearLayout) v.findViewById(R.id.ll_timingfunction_item);			
			appItem.tvTime = (TextView) v.findViewById(R.id.tv_time);
			appItem.tvInfo = (TextView) v.findViewById(R.id.tv_info);
			appItem.imageButton = (ImageButton) v.findViewById(R.id.ib_timing_function);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem) convertView.getTag();
		}
		int powon = timingFunctionInfo.getPoweron() ? R.string.power_on
				: R.string.power_off;
		String powonString = mContext.getResources().getString(powon);
		appItem.tvTime.setText(createTimeString(timingFunctionInfo.getHour(),
				timingFunctionInfo.getMinute()));
		appItem.tvInfo.setText(powonString + ", "
				+ timingFunctionInfo.getCirculationModeString(mContext));
		appItem.imageButton.setBackgroundResource(timingFunctionInfo.getStatus()?R.drawable.button_toggle_on:R.drawable.button_toggle_off);
		appItem.imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(socketIndex);
				}
			}
		});
		
		appItem.llLayout.setOnLongClickListener(new OnLongClickListener() {				
			@Override
			public boolean onLongClick(View v) {
				if (onItemClickListener != null) {
					onItemClickListener.onItemLongClick(socketIndex);
				}
				return false;
			}
		});
		return convertView;
	}

	/**
	 * 每个应用显示的内容，包括图标和名称
	 * 
	 * @author Yao.GUET
	 * 
	 */
	class AppItem {
		LinearLayout llLayout;
		TextView tvTime;
		TextView tvInfo;
		ImageButton imageButton;
	}

	private String createTimeString(int hour, int minute) {		
		SimpleDateFormat ss = new SimpleDateFormat("a  hh:mm");// 12小时制
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		String timeStr = ss.format(calendar.getTime());
		
		return timeStr;
	}

	public interface OnItemClickListener {
		public void onItemClick(int deviceIndex);
		public void onItemLongClick(int deviceIndex);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.onItemClickListener = listener;
	}
}
