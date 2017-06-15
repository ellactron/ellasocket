package rici.roplug.open.fragment;

import rici.roplug.open.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SmartSavingFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_smart_energy_saving_layout, container,
				false);
		TextView titleTextView = (TextView)v.findViewById(R.id.tv_title);
		titleTextView.setText(R.string.smart_energy_saving);
		
		return v;
	}
	
	
}
