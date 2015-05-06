package com.litijun.photochooser.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

public class FileMgr {
	private static FileMgr	instance			= null;
	/** 如果没有sdcard，文件保存路径 */
	private static String			CACHE_APP_ROOT_DIR	= null;
	/** 文件在sdcard中保存路径 */
	private static String			SDCARD_APP_ROOT_DIR	= null;

	public static FileMgr getInstance(Context context) {
		if (instance == null) {
			synchronized (FileMgr.class) {
				if (instance == null) {
					instance = new FileMgr(context);
				}
			}
		}

		return instance;
	}

	private FileMgr(Context context) {
		String appPkg = context.getPackageName();

		SDCARD_APP_ROOT_DIR = Environment.getExternalStorageDirectory() + File.separator //
				+ "Android" + File.separator + "data" + File.separator + appPkg + File.separator;

		CACHE_APP_ROOT_DIR = Environment.getDataDirectory() + File.separator //
				+ "data" + File.separator + appPkg + File.separator;
	}

	/**
	 * 判断sdcard是否可用
	 */
	private boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	/**
	 * 创建项目文件放置的根路径
	 */
	private File createFileDir() {
		File goalFile = null;
		if (hasSDCard()) {
			goalFile = new File(SDCARD_APP_ROOT_DIR);
		}
		else {
			goalFile = new File(CACHE_APP_ROOT_DIR);
		}

		if (!goalFile.exists()) {
			goalFile.mkdirs();
		}
		return goalFile;
	}

	public File createFileDir(String fileDir) {
		File file = new File(createFileDir(), fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 创建具体的文件
	 * 
	 * @param fileDir
	 *            文件放置路径
	 * @param fileName
	 *            文件名
	 * @return 目标文件
	 */
	public File createFile(String fileDir, String fileName) {
		if (null == fileDir || TextUtils.isEmpty(fileDir)) {
			return new File(createFileDir(), fileName);
		}
		else {
			return new File(createFileDir(fileDir), fileName);
		}
	}

	public File createFile(String fileName) {
		return createFile(null, fileName);
	}

	public File getAppDir() {
		return createFileDir();
	}
}
