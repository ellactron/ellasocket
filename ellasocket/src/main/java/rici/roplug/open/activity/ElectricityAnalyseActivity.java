package rici.roplug.open.activity;

import rici.roplug.open.R;
import rici.roplug.open.adapter.ElectricityAnalyseAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ElectricityAnalyseActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smart_energy_electricity_analyse_layout);
		Button titleReturnButton = (Button) findViewById(R.id.btn_title_return);
		titleReturnButton.setText(R.string.electricity_analyse);
		titleReturnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ElectricityAnalyseActivity.this.finish();
			}  
		});

		ListView electricityAnalyseListView = (ListView) findViewById(R.id.lv_electricity_analyse);
		ElectricityAnalyseAdapter electricityAnalyseAdapter = new ElectricityAnalyseAdapter(
				ElectricityAnalyseActivity.this, getSocketBLL()
						.getAllSocketInfos());
		electricityAnalyseListView.setAdapter(electricityAnalyseAdapter);
	}

}
