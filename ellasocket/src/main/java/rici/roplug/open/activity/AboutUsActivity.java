package rici.roplug.open.activity;

import rici.roplug.open.R;
import rici.roplug.open.util.autoUpdate.CheckUpdateAsyncTask;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutUsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_setting_aboutme_layout);
		Button titleReturnButton = (Button) findViewById(R.id.btn_title_return);
		titleReturnButton.setText(R.string.about_us);
		titleReturnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
			}
		});

		TextView textView = (TextView) findViewById(R.id.tv_current_version);
		textView.setText(getVerName());
	}

	private String getVerName() {
		String verName = "V";
		try {
			verName += getPackageManager().getPackageInfo("rici.roplug.open", 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return verName;
	}

	public void systemSettingButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btn_aboutme_version:
			new Thread() {
				public void run() {
					CheckUpdateAsyncTask checkAsyncTask = new CheckUpdateAsyncTask(
							AboutUsActivity.this);
					checkAsyncTask.setManual(true);
					checkAsyncTask.execute(10);
				}
			}.start();
			break;
		}
	}
}
