package rici.roplug.open.util.autoUpdate;

import java.io.Serializable;

public class UpdateApkInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String apkVersion;     //apk版本号
	private String apkName;      //apk名字
	private String apkDownloadUrl;  //下载地址
	private int aplVerCode;    //apk升级标示
	
	public UpdateApkInfo(String apkVersion,String apkName,String apkDownloadUrl,int aplVerCode){
		this.apkVersion = apkVersion;
		this.apkName = apkName;
		this.apkDownloadUrl = apkDownloadUrl;
		this.aplVerCode = aplVerCode;
	}
	public String getApkVersion() {
		return apkVersion;
	}
	public void setApkVersion(String apkVersion) {
		this.apkVersion = apkVersion;
	}
	public String getApkName() {
		return apkName;
	}
	public void setApkName(String apkName) {
		this.apkName = apkName;
	}
	public String getApkDownloadUrl() {
		return apkDownloadUrl;
	}
	public void setApkDownloadUrl(String apkDownloadUrl) {
		this.apkDownloadUrl = apkDownloadUrl;
	}
	public int getAplVerCode() {
		return aplVerCode;
	}
	public void setAplVerCode(int aplVerCode) {
		this.aplVerCode = aplVerCode;
	}

    
}
