package com.example.backupapk;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	 List appPakageList ;
     Intent intent ;
     BackApkStructure backStr;
     ListView listView;
     ArrayList<BackApkStructure> appList = new ArrayList<BackApkStructure>();
     ProgressBar progressBar;
     Button backUpAllBtn;
     private HashMap<Integer, Integer> myChecked = new HashMap<Integer, Integer>();
     
     int selectedPosition;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myactivity);
		
		
		 intent = new Intent(Intent.ACTION_MAIN, null);
		 intent.addCategory(Intent.CATEGORY_LAUNCHER);
		 appPakageList = getPackageManager().queryIntentActivities(intent, 0);
		
		 listView = (ListView)findViewById(R.id.appList);
		 backUpAllBtn = (Button )findViewById(R.id.backUpBtn);
		 progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		 ListAppAsyncTask listApp = new ListAppAsyncTask();
		 listApp.execute();
		 backUpAllBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				BackUpAllAppAsyncTask backupAllApk = new BackUpAllAppAsyncTask();
				backupAllApk.execute();
				
			}
		});
		
		
	}

	public class ListAppAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			
			
		}

		@Override
		protected String doInBackground(String... params) {
			for (Object objevt : appPakageList) {
				ResolveInfo resolveInFo = (ResolveInfo) objevt;
				backStr = new BackApkStructure();
				backStr.apkSourceDir = resolveInFo.activityInfo.applicationInfo.publicSourceDir;
				backStr.apkName = resolveInFo.loadLabel(getPackageManager()).toString();
			    backStr.icons  =resolveInFo.loadIcon(getPackageManager());
			    appList.add(backStr);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			
			InstalledAppAdapter adapter = new InstalledAppAdapter(MainActivity.this,
					appList);
			listView.setAdapter(adapter);
			progressBar.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}
	}
	
	

public class InstalledAppAdapter extends  BaseAdapter {

	Activity activity;
	ArrayList<BackApkStructure> appStrucutreList;
	
	public InstalledAppAdapter(Activity act, ArrayList<BackApkStructure> appStructure) {
		
		this.activity = act;
		this.appStrucutreList = appStructure;
		
		
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appStrucutreList.size();
	}

	@Override
	public Object getItem(int arg0) {
		
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
	
		LayoutInflater inflator =(LayoutInflater)activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		view = inflator.inflate(R.layout.adapter_layout, null);
		
		TextView appName = (TextView)view.findViewById(R.id.applicationName);
		ImageView appIcon = (ImageView)view.findViewById(R.id.appIcon);
		Button backBtn = (Button)view.findViewById(R.id.back_btn);
		appName.setText(appStrucutreList.get(position).apkName);
		appIcon.setBackgroundDrawable(appStrucutreList.get(position).icons);
		
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectedPosition = position;
				BackUpSingleAppAsyncTask backApp = new BackUpSingleAppAsyncTask();
				backApp.execute();
			}
		});
		return view;
	}

}

public class BackUpSingleAppAsyncTask extends AsyncTask<String, Void, String> {
	ProgressDialog progress;
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress = new ProgressDialog(MainActivity.this);;
		progress.setMessage("Please wait...App backup...");
		progress.setCancelable(false);
		progress.show();
		
	}

	@Override
	protected String doInBackground(String... params) {
		String result = null;
		backupSingleApp(selectedPosition);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		progress.dismiss();
		Toast.makeText(MainActivity.this, "Application successfully Backup.", Toast.LENGTH_LONG).show();
		super.onPostExecute(result);
	}
}

public class BackUpAllAppAsyncTask extends AsyncTask<String, Void, String> {
	ProgressDialog progress;
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress = new ProgressDialog(MainActivity.this);;
		progress.setMessage("Please wait...App backup...");
		progress.setCancelable(false);
		progress.show();
		
	}

	@Override
	protected String doInBackground(String... params) {
		String result = null;
	     backupAllApp();
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		progress.dismiss();
		Toast.makeText(MainActivity.this, "Application successfully Backup.", Toast.LENGTH_LONG).show();
		super.onPostExecute(result);
	}
}


private void backupSingleApp(int position){
		File apkSourceDirec = new File(appList.get(position).apkSourceDir);
		try {
			String file_name =appList.get(position).apkName;
			File fileDirectry = new File(Environment.getExternalStorageDirectory()
					.toString() + "/iam_VJ_backUp_APK");
			fileDirectry.mkdirs();
			fileDirectry = new File(fileDirectry.getPath() + "/" + file_name + ".apk");
			fileDirectry.createNewFile();
			InputStream in = new FileInputStream(apkSourceDirec);
			OutputStream out = new FileOutputStream(fileDirectry);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			Log.e("file_name--", "File copied. ");

		} catch (FileNotFoundException ex) {
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	
}


private void backupAllApp(){
	
	for(int i = 0; i<appList.size(); i++){
	File apkSourceDirec = new File(appList.get(i).apkSourceDir);
	try {
		String file_name =appList.get(i).apkName;
		File fileDirectry = new File(Environment.getExternalStorageDirectory()
				.toString() + "/iam_VJ_backUp_APK");
		fileDirectry.mkdirs();
		fileDirectry = new File(fileDirectry.getPath() + "/" + file_name + ".apk");
		fileDirectry.createNewFile();
		InputStream in = new FileInputStream(apkSourceDirec);
		OutputStream out = new FileOutputStream(fileDirectry);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
		Log.e("file_name--", "File copied. ");

	} catch (FileNotFoundException ex) {
		
	} catch (IOException e) {
		System.out.println(e.getMessage());
	}

}}

}
