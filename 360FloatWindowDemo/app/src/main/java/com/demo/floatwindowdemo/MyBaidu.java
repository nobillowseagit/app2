package com.demo.floatwindowdemo;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.speech.VoiceRecognitionService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.baidu.speech.VoiceRecognitionService;
//import com.baidu.speech.recognizerdemo.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
/**
 * Created by lyt on 2016/12/31.
 */

public class MyBaidu implements RecognitionListener {
    private static final String TAG = "Sdk2Api";
    private static final int REQUEST_UI = 1;
    //private TextView txtLog;
    //private Button btn;
    //private Button setting;

    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    private SpeechRecognizer speechRecognizer;
    private int status = STATUS_None;
    private TextView txtResult;
    private long speechEndTime = -1;
    private static final int EVENT_ERROR = 11;
    private Context mContext = null;
    String mString = null;
    private Handler mHandler = null;


    public MyBaidu(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, new ComponentName(context, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        //start();
    }


    public void bindParams(Intent intent) {
        Context context = mContext;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.getBoolean(Constant.EXTRA_GRAMMAR, false)) {
            intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    private static boolean mAudio = true;

    private void restart() {
        if (mAudio) {
            start();
        }
    }

    public void setAudio(boolean bool) {
        mAudio = bool;
    }

    public void init() {
        mAudio = true;
        start();
    }

    public void close() {
        cancel();
        stop();
    }

    public String get() {
        return mString;
    }

    public void set() {
        mString = null;
    }

    private void start() {
        //txtLog.setText("");
        print("点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        {

            String args = sp.getString("args", "");
            if (null != args) {
                print("参数集：" + args);
                intent.putExtra("args", args);
            }
        }
        boolean api = sp.getBoolean("api", false);
        if (true) {
            speechEndTime = -1;
            speechRecognizer.startListening(intent);
        } else {
            //intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
            //startActivityForResult(intent, REQUEST_UI);
        }

        //
        // txtResult.setText("");
    }

    private void stop() {
        speechRecognizer.stopListening();
        print("点击了“说完了”");
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
        print("点击了“取消”");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
        print("准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        time = System.currentTimeMillis();
        status = STATUS_Speaking;
        //btn.setText("说完了");
        print("检测到用户的已经开始说话");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;
        print("检测到用户的已经停止说话");
        //btn.setText("识别中");
    }

    @Override
    public void onError(int error) {
        time = 0;
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
        //btn.setText("开始");
        restart();
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            print("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            print("origin_result=[warning: bad json]\n" + json_res);
        }
        //btn.setText("开始");
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        //txtResult.setText(nbest.get(0) + strEnd2Finish);
        time = 0;

        String str = null;
        String str2 = "打开";
        String str3 = "播报";
        try {
            //str = new JSONObject(json_res).toString();
            JSONObject jo = new JSONObject(json_res);
            JSONObject jcontent = jo.getJSONObject("content");
            JSONArray jitem = jcontent.getJSONArray("item");
            str = jitem.get(0).toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (str2.regionMatches(0, str, 0, 2)) {
            int a = 1;
            mString = str.substring(2);
            //mHandler.sendEmptyMessageDelayed(0, 1000);

            //模拟被观察者数据改变，更新数据。
            Data mData = new Data();
            mData.setDataChange(0);
            DataChange.getInstance().notifyDataChange(mData);
        } else if (str3.regionMatches(0, str, 0, 2)){
            mString = str.substring(2);
            //模拟被观察者数据改变，更新数据。
            Data mData = new Data();
            mData.setDataChange(1);
            mData.setParam(mString);
            DataChange.getInstance().notifyDataChange(mData);
        }

        restart();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            //txtResult.setText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    long time;
    private void print(String msg) {
        long t = System.currentTimeMillis() - time;
        if (t > 0 && t < 100000) {
            //txtLog.append(t + "ms, " + msg + "\n");
        } else {
            //txtLog.append("" + msg + "\n");
        }
        //ScrollView sv = (ScrollView) txtLog.getParent();
        //sv.smoothScrollTo(0, 1000000);
        Log.d(TAG, "----" + msg);
    }


}
