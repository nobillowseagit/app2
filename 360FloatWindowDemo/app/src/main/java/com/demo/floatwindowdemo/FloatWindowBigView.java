package com.demo.floatwindowdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FloatWindowBigView extends LinearLayout {

	private final MyBaiduTts mMyBaiduTts;
	private GridView gridview;
	private PackageManager pManager = null;
	private List<PakageMod> datas = null;

	private List<AppInfo> mlistAppInfo = null;
	MyBaidu mBaidu = null;

	/**
	 * 记录大悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录大悬浮窗的高度
	 */
	public static int viewHeight;

	List<PackageInfo> mAppList = null;

	public FloatWindowBigView(final Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button close = (Button) findViewById(R.id.close);
		Button back = (Button) findViewById(R.id.back);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.removeSmallWindow(context);

				mBaidu.close();
				mBaidu.setAudio(false);

				Intent intent = new Intent(getContext(), FloatWindowService.class);
				context.stopService(intent);

			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击返回的时候，移除大悬浮窗，创建小悬浮窗
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context);
				mBaidu.close();
				mBaidu.setAudio(false);
			}
		});


		mMyBaiduTts = new MyBaiduTts(context);

		mBaidu = new MyBaidu(context, mHandler);
		mBaidu.init();




		gridview = (GridView) findViewById(R.id.gridview);
		pManager = context.getPackageManager();


		//Thread thread=new Thread(this);
		//thread.start();
		//mHandler.sendEmptyMessageDelayed(0, 1000);
		DataChange.getInstance().addObserver(watcher);



/*
		if (datas == null) {
			List<PackageInfo> appList = getAllApps(context);
			datas = new ArrayList<PakageMod>();
			for (int i = 0; i < appList.size(); i++) {
				PackageInfo pinfo = appList.get(i);
				PakageMod shareItem = new PakageMod();
				// 设置图片
				shareItem.icon = pManager.getApplicationIcon(pinfo.applicationInfo);
				// 设置应用程序名字
				shareItem.appName = pManager.getApplicationLabel(
						pinfo.applicationInfo).toString();
				// 设置应用程序的包名
				shareItem.pakageName = pinfo.applicationInfo.packageName;

				datas.add(shareItem);
			}
		}
		gridview.setAdapter(new DemoGridAdapter(context, datas));
*/


		mlistAppInfo = new ArrayList<AppInfo>();
		queryAppInfo(); // 查询所有应用程序信息
		gridview.setAdapter(new BrowseApplicationInfoAdapter(context, mlistAppInfo));
		gridview.setOnItemClickListener(new ClickListener2());


		// 点击应用图标时，做出响应
		//gridview.setOnItemClickListener(new ClickListener());
	}


	public static List<PackageInfo> getAllApps(Context context) {

		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> packlist = pManager.getInstalledPackages(0);
		for (int i = 0; i < packlist.size(); i++) {
			PackageInfo pak = (PackageInfo) packlist.get(i);
			// if()里的值如果<=0则为自己装的程序，否则为系统工程自带
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 添加自己已经安装的应用程序
				// apps.add(pak);
			}
			apps.add(pak);
		}
		return apps;
	}

	private class ClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
			Intent intent = new Intent();


			intent = pManager.getLaunchIntentForPackage(datas.get(position).pakageName);

			//startActivity(intent);
			Context context = getContext();
			//intent.setClass(context, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}


	private class ClickListener2 implements AdapterView.OnItemClickListener {

		@Override

		// 点击跳转至该应用程序
		public void onItemClick(AdapterView<?> arg0, View view, int position,
								long arg3) {
			// TODO Auto-generated method stub
			Intent intent = mlistAppInfo.get(position).getIntent();

			//如果context是Application或者Application的context则要加上intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);
		}
	}


	// 获得所有启动Activity的信息，类似于Launch界面
	public void queryAppInfo() {
		PackageManager pm = getContext().getPackageManager(); // 获得PackageManager对象
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm
				.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
		if (mlistAppInfo != null) {
			mlistAppInfo.clear();
			for (ResolveInfo reInfo : resolveInfos) {
				String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
				String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
				String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
				Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
				// 为应用程序的启动Activity 准备Intent
				Intent launchIntent = new Intent();
				launchIntent.setComponent(new ComponentName(pkgName,
						activityName));
				// 创建一个AppInfo对象，并赋值
				AppInfo appInfo = new AppInfo();
				appInfo.setAppLabel(appLabel);
				appInfo.setPkgName(pkgName);
				appInfo.setAppIcon(icon);
				appInfo.setIntent(launchIntent);
				mlistAppInfo.add(appInfo); // 添加至列表中
				System.out.println(appLabel + " activityName---" + activityName
						+ " pkgName---" + pkgName);
			}
		}
	}


	void baidu() {


	}

	class WorkThread extends Thread {
		public Handler mHandler;

		public void run() {
			Looper.prepare();

			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					// 处理收到的消息
				}
			};

			Looper.loop();
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					//更新你相应的UI
					mHandler.sendEmptyMessageDelayed(0, 1000);
					break;
				case 1:
					break;
			}
		}
	};


	public void run() {
		Log.e("ok", "111111111");
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 1;
		String str = mBaidu.get();
		if (str != null) {
			mBaidu.set();
		}
		mHandler.sendMessage(message);
	}



	private DataWatcher watcher = new DataWatcher() {

		@Override
		public void update(Observable observable, Object data) {
			super.update(observable, data);
			//观察者接受到被观察者的通知，来更新自己的数据操作。
			Data mData = (Data)data;
			Log.i("Test", "mData---->>"+mData.getDataChange());

			int code = mData.getDataChange();

			if (code == 0) {
				String str = mBaidu.get();
				mBaidu.set();
				int cnt = mlistAppInfo.size();
				int i;
				for (i = 0; i < cnt; i++) {
					if (mlistAppInfo.get(i).getAppLabel().equals(str)) {
						Intent intent = mlistAppInfo.get(i).getIntent();

						//如果context是Application或者Application的context则要加上intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getContext().startActivity(intent);
						break;

					}
				}
			} else if (code == 1) {

				String str_p = (String)mData.getParam();

				//获取系统当前日历时间
				SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
				Date curDate =  new Date(System.currentTimeMillis());
				String   str   =   formatter.format(curDate);
				mMyBaiduTts.speakText("北京时间" + str);
			}


		}

	};

}