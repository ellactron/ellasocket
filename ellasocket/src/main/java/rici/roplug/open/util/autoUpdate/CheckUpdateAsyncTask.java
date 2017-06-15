package rici.roplug.open.util.autoUpdate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import rici.roplug.open.R;
import rici.roplug.open.util.Util;
import rici.roplug.open.view.PopupDialogView;
import rici.roplug.open.view.PopupDialogView.OnPopupDialogButtonClick;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

import com.rici.wifi.util.L;
import com.rici.wifi.util.WifiUtil;

public class CheckUpdateAsyncTask extends AsyncTask<Integer, Integer, String> {

	private Context mContext;
	private final static String SETTING_UPDATE_APK_INFO = "setting_updateapkinfo";
	private final static String CHECK_DATE = "checkdate";
	private final static String UPDATE_DATE = "updatedate";
	private final static String APK_VERSION = "apkversion";
	private final static String APK_VERCODE = "apkvercode";
	private final static String FIRMWARE_VERSION="firmwareVersion";
	
	private boolean isManual = false;
	private AlertDialog noticeDialog; // 提示弹出框
	private UpdateApkInfo apkInfo;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public CheckUpdateAsyncTask(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	protected String doInBackground(Integer... params) {
		String result = "";
		// 检查是否能够连接网络,根据日期判断是否需要进行更新
		if (isManual) {
			// 检查是否能够连接网络
			if (WifiUtil.getInstance().checkNetWorkStatus(mContext)) {
				getUpateApkInfo();
				if (apkInfo != null && checkApkVersion()) { // 检查版本号
					result = "success";
				} else {
					result = "fail";
				}
			}
		} else {
			if (WifiUtil.getInstance().checkNetWorkStatus(mContext)) {
				getUpateApkInfo();
				if (apkInfo != null && checkApkVersion()) { // 检查版本号
					alreayCheckTodayUpdate(); // 设置今天已经检查过更新
					result = "success";
				} else {
					L.i("从服务器获取下载数据失败或者该版本code不需要升级");
					result = "fail";
				}
			} else {
				L.i("无法连接网络或者根据日期判断不需要更新软件");
				result = "fail";
			}
		}
		return result;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		if ("success".equals(result)) {
			showNoticeDialog();
		} else if (isManual) {
			if ("fail".equals(result)) {
				notNewVersionShow();
			}
		}
		super.onPostExecute(result);
	}
	private OnPopupDialogButtonClick downloadButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			Intent intent = new Intent();
			intent.setAction(UpdateReceiver.ACTION_PROCRESS);
			intent.addCategory(Intent.CATEGORY_DEFAULT); // 一定要添加这个属性，不然onReceive(Context,Intent)中的Context参数不等于mContext，并且报错
			intent.putExtra(UpdateReceiver.PARAM_IN, apkInfo);
			mContext.sendBroadcast(intent);
		}
	};
	/**
	 * 弹出软件更新提示对话框
	 */
	private void showNoticeDialog() {
		final PopupDialogView downloaDialogView = new PopupDialogView(mContext);
		downloaDialogView
				.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
		downloaDialogView.setTitleText(R.string.version_update_title);
		downloaDialogView.setRightButtonClick(downloadButtonClick);
		downloaDialogView.setMessageContent(mContext.getResources().getString(R.string.new_version_info));
		downloaDialogView.show();
		/*final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
		dlg.show();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialgView = inflater.inflate(
				R.layout.dialog_text_layout, null);
		TextView titleView = (TextView) dialgView
				.findViewById(R.id.tv_popupwindow_title);
		titleView.setText(R.string.version_update_title);
		TextView contentView = (TextView) dialgView
				.findViewById(R.id.tv_popupwindow_text);
		contentView.setText(R.string.new_version_info);
		// 为确认按钮添加事件,执行退出应用操作
		Button leftButton = (Button) dialgView.findViewById(R.id.btn_popupwindow_left);
		leftButton.setText(R.string.download_next);
		leftButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		Button rightButton = (Button) dialgView.findViewById(R.id.btn_popupwindow_right);
		rightButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setAction(UpdateReceiver.ACTION_PROCRESS);
				intent.addCategory(Intent.CATEGORY_DEFAULT); // 一定要添加这个属性，不然onReceive(Context,Intent)中的Context参数不等于mContext，并且报错
				intent.putExtra(UpdateReceiver.PARAM_IN, apkInfo);
				dlg.dismiss();
				mContext.sendBroadcast(intent);
			}
		});
		Window window = dlg.getWindow();
		window.setContentView(dialgView);*/
	}

	/**
	 * 没有最新版本需要更新
	 */
	private void notNewVersionShow() {
		String verName = "";
		if (apkInfo != null) {
			verName = apkInfo.getApkVersion();
		}
		StringBuffer sb = new StringBuffer();
		sb.append(mContext.getResources().getString(R.string.current_version));
		sb.append(verName);
		sb.append(mContext.getResources().getString(R.string.new_version));	
		
		final PopupDialogView downloaDialogView = new PopupDialogView(mContext);
		downloaDialogView
				.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
		downloaDialogView.setTitleText(R.string.version_update_title);
		downloaDialogView.setRightButtonClick(downloadButtonClick);
		downloaDialogView.setMessageContent(sb.toString());
		downloaDialogView.show();
		
/*		final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
		dlg.show();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialgView = inflater.inflate(
				R.layout.dialog_text_layout, null);
		TextView titleView = (TextView) dialgView
				.findViewById(R.id.tv_popupwindow_title);
		titleView.setText(R.string.version_update_title);
		TextView contentView = (TextView) dialgView
				.findViewById(R.id.tv_popupwindow_text);
		contentView.setText(sb.toString());
		// 为确认按钮添加事件,执行退出应用操作
		Button leftButton = (Button) dialgView.findViewById(R.id.btn_popupwindow_left);
		leftButton.setVisibility(View.GONE);
		Button rightButton = (Button) dialgView.findViewById(R.id.btn_popupwindow_right);
		rightButton.setBackgroundResource(R.drawable.popupwindow_one_button_bg);
		rightButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		Window window = dlg.getWindow();
		window.setContentView(dialgView);*/
	}

	/**
	 * 获取升级APK详细信息
	 * 
	 * @return
	 */
	private void getUpateApkInfo() {
		try {
			String updateApkJson = getServerContent("http://"+
					Util.SERVER_IP+"/apk/Roplug/ver.json");
//			String updateApkJson = getServerContent("http://115.28.216.177/apk/Roplug/ver.json");
			JSONObject obj = new JSONObject(updateApkJson);
			String apkVersion = obj.getString("apkVersion");
			int apkVerCode = obj.getInt("apkVerCode");
			String apkName = obj.getString("apkName");
			String apkDownloadUrl = obj.getString("apkDownloadUrl");
			apkInfo = new UpdateApkInfo(apkVersion, apkName, apkDownloadUrl,
					apkVerCode);
			//int firmwareVersion = obj.getInt(FIRMWARE_VERSION);
			//SocketBLL.getInstance().setCurrentFrimwareVersion(firmwareVersion);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据日期检查是否需要进行软件升级
	 * 
	 * @throws Exception
	 */
	private boolean checkTodayUpdate() {
		SharedPreferences sharedPreference = mContext.getSharedPreferences(
				SETTING_UPDATE_APK_INFO, 0);
		String checkDate = sharedPreference.getString(CHECK_DATE, "");
		String updateDate = sharedPreference.getString(UPDATE_DATE, "");
		L.i("检查时间：" + checkDate);
		L.i("最近更新软件时间：" + updateDate);
		if ("".equals(checkDate)) { // 刚安装的新版本，设置详细信息
			int verCode = 0;
			String versionName = "";
			try {
				verCode = mContext.getPackageManager().getPackageInfo(
						"rici.roplug.open", 0).versionCode;
				versionName = mContext.getPackageManager().getPackageInfo(
						"rici.roplug.open", 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			String dateStr = sdf.format(new Date());
			sharedPreference.edit().putString(CHECK_DATE, dateStr)
					.putString(APK_VERSION, versionName)
					.putInt(APK_VERCODE, verCode).commit();
			return false;
		}
		if ("".equals(updateDate)) {
			String dateStr = sdf.format(new Date());
			sharedPreference.edit().putString(UPDATE_DATE, dateStr).commit();
			return false;
		}
		try {
			// 判断defaultMinUpdateDay天内不检查升级
			long currentTime = new Date().getTime();
			long updateTime = sdf.parse(updateDate).getTime();
			if ((currentTime - updateTime) / 1000 / 3600 / 24 < 1) {
				return false;
			} else if (checkDate.equalsIgnoreCase(sdf.format(new Date()))) {// 判断今天是否检查过升级
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 检查版本是否需要更新
	 * 
	 * @return
	 */
	private boolean checkApkVersion() {
		SharedPreferences sharedPreference = mContext.getSharedPreferences(
				SETTING_UPDATE_APK_INFO, 0);
		int verCode = 0;
		try {
			verCode = mContext.getPackageManager().getPackageInfo(
					"rici.roplug.open", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (apkInfo.getAplVerCode() > verCode) { // 如果新版本Code大于系统更新后的Code，则升级
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置今天已经检查过升级
	 * 
	 * @return
	 */
	private void alreayCheckTodayUpdate() {
		String date = sdf.format(new Date());
		SharedPreferences sharedPreference = mContext.getSharedPreferences(
				SETTING_UPDATE_APK_INFO, 0);
		sharedPreference.edit().putString(CHECK_DATE, date).commit();
	}

	private String getServerContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		// 设置网络超时参数
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		try {
			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						entity.getContent(), "UTF-8"), 8192);
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");   
				}
				reader.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public void setManual(boolean isManual) {
		this.isManual = isManual;
	}

	/**
	 * 手动检查更新
	 * 
	 * @return
	 */
	public boolean checkUpdateManual() {
		boolean result = false;
		// 检查是否能够连接网络
		if (WifiUtil.getInstance().checkNetWorkStatus(mContext)) {
			getUpateApkInfo();
			if (apkInfo != null && checkApkVersion()) { // 检查版本号

			} else {

			}
		}
		return result;
	}
}
