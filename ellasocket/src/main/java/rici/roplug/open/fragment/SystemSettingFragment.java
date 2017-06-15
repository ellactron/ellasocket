package rici.roplug.open.fragment;

import rici.roplug.open.R;
import rici.roplug.open.bll.SocketBLL;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class SystemSettingFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_system_setting_layout, container,
				false);
		TextView titleTextView = (TextView)v.findViewById(R.id.tv_title);
		titleTextView.setText(R.string.setting);
		
		final ImageButton overProtection = (ImageButton)v.findViewById(R.id.ib_overProtection);
		overProtection.setBackgroundResource(SocketBLL.getInstance().getOverProtection()?R.drawable.button_toggle_on:R.drawable.button_toggle_off);
		overProtection.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(SocketBLL.getInstance().setOverProtection(!SocketBLL.getInstance().getOverProtection())){
					overProtection.setBackgroundResource(SocketBLL.getInstance().getOverProtection()?R.drawable.button_toggle_on:R.drawable.button_toggle_off);
				}
				else{
					
				}
			}
		});
		
		return v;
	}
}
