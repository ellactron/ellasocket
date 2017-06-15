package rici.roplug.open.util.autoUpdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rici.wifi.util.L;
import com.rici.wifi.util.WifiUtil;

import rici.roplug.open.R;
import rici.roplug.open.view.PopupDialogView.OnPopupDialogButtonClick;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateAsyncTask extends AsyncTask<Integer, Integer, String> {

	private final static String SETTING_UPDATE_APK_INFO = "setting_updateapkinfo";
	private final static String UPDATE_DATE = "updatedate";
	private final static String APK_VERSION = "apkversion";
	private final static String APK_VERCODE = "apkvercode";
	
	private final static String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RiCi";
	
	private String fileName;
	
	private Context mContext;
	private ProgressBar progressView;      //进度条
	private TextView textView;
	private AlertDialog downloadDialog;    //下载弹出框
	
	private UpdateApkInfo apkInfo;   //APK更新的详细信息
	
	private boolean interceptFlag = false;  //是否取消下载
	private boolean sdExists = false;   //是否存在SD卡
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public UpdateAsyncTask(Context mContext,UpdateApkInfo apkInfo) {
		this.mContext = mContext;
		this.apkInfo = apkInfo;
		if(apkInfo!=null){
			fileName = savePath + "/" + apkInfo.getApkName();
		}
	}
	
	/**
	 * 升级成功，更新升级日期和版本号，和版本code
	 */
	private void alearyUpdateSuccess(){
		SharedPreferences sharedPreference = mContext.getSharedPreferences(SETTING_UPDATE_APK_INFO, 0);
		sharedPreference.edit().putString(UPDATE_DATE, sdf.format(new Date()))
		.putString(APK_VERSION, apkInfo.getApkVersion()).putInt(APK_VERCODE, apkInfo.getAplVerCode()).commit();
	}
	
	/**
	 * 安装apk
	 */
	private void installApk(){ 
		File file = new File(fileName);
		if(!file.exists()){
			L.i("找不到下载的软件");
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
	/**
	 * 检测手机是否存在SD卡
	 */
	private boolean checkSoftStage(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //判断是否存在SD卡
			File file = new File(savePath);
			if(!file.exists()){
				file.mkdir();
			}
			sdExists = true;
			return true;
		}else{
			Toast.makeText(mContext, mContext.getResources().getString(R.string.no_sdcard), Toast.LENGTH_LONG).show();
			return false;
		}
	}
	@Override
	protected void onPreExecute() {
		
		if(apkInfo!=null && checkSoftStage()){
			showDownloadDialog();
		}
		super.onPreExecute();
	}
	
	private OnPopupDialogButtonClick downloadButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			interceptFlag = true; 
		}
	};
	/**
	 * 弹出下载进度对话框
	 */
	private void showDownloadDialog(){	
		try {
			final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
			dlg.show();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dialgView = inflater.inflate(
					R.layout.update_progress_layout, null);
			TextView titleView = (TextView) dialgView
					.findViewById(R.id.tv_popupwindow_title);
			titleView.setText(R.string.downloading);

			textView = (TextView)dialgView.findViewById(R.id.progressCount_text);
			textView.setText(mContext.getResources().getString(R.string.progress)+" 0");
			progressView = (ProgressBar)dialgView.findViewById(R.id.progressbar);
			// 为确认按钮添加事件,执行退出应用操作
			Button rightButton = (Button) dialgView.findViewById(R.id.btn_popupwindow_one);
			rightButton.setVisibility(View.VISIBLE);
			rightButton.setBackgroundResource(R.drawable.popupwindow_one_button_bg);
			rightButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.dismiss();
					interceptFlag = true; 
				}
			});
			Window window = dlg.getWindow();
			window.setContentView(dialgView);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected String doInBackground(Integer... params) {		
		String result = "";
		if(apkInfo==null){
			result = "fail";
		}else if(!WifiUtil.getInstance().checkURL(apkInfo.getApkDownloadUrl())){   //检查apk的下载地址是否可用
			result = "netfail";
		}else if(apkInfo!=null && sdExists){
			InputStream is = null;
			FileOutputStream fos = null;
			File file = new File(savePath);
			if(!file.exists()){
				file.mkdirs();
			}
			try {
				URL url = new URL(apkInfo.getApkDownloadUrl());
				URLConnection urlConn = url.openConnection();
				is = urlConn.getInputStream();
				int length = urlConn.getContentLength();   //文件大小
				fos = new FileOutputStream(fileName);
				
				int count = 0,numread = 0;
				byte buf[] = new byte[1024];
				
				while(!interceptFlag && (numread = is.read(buf))!=-1){
					count+=numread;
					int progressCount =(int)(((float)count / length) * 100);
					publishProgress(progressCount);
					fos.write(buf, 0, numread);
				}
				fos.flush();
				result = "success";
			} catch (Exception e) {
				e.printStackTrace();
				result = "fail";
			}finally{
				try {
					if(fos!=null)
						fos.close();
					if(is!=null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
					result = "fail";
				}
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		if(downloadDialog!=null){
			downloadDialog.dismiss();
		}
		if(!interceptFlag && "success".equals(result)){
			alearyUpdateSuccess();
			installApk();
		}else if("netfail".equals(result)){
			Toast.makeText(mContext, mContext.getResources().getString(R.string.link_falith), Toast.LENGTH_LONG).show();
		}
		super.onPostExecute(result);
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		int count = values[0];
		progressView.setProgress(count);   //设置下载进度
		textView.setText(mContext.getResources().getString(R.string.progress)+count+"%");
		super.onProgressUpdate(values);
	}
}
