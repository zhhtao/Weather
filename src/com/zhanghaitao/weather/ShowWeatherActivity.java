package com.zhanghaitao.weather;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zhanghaitao.base.AppManager;
import com.zhanghaitao.modle.County;
import com.zhanghaitao.modle.WeatherInfo;
import com.zhanghaitao.service.AutoUpdateService;
import com.zhanghaitao.util.MyConst;
import com.zhanghaitao.util.Utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

public class ShowWeatherActivity extends FinalActivity{

	@ViewInject(id=R.id.show_weather) TextView showWeatherTextView;
	@ViewInject(id=R.id.title_weather) TextView titleTextView;
	@ViewInject(id=R.id.public_time_weather) private TextView publicTimeTextView;
	@ViewInject(id=R.id.current_time_weather)private TextView currentTimeTextView;
	@ViewInject(id=R.id.weather_info_weather)private TextView weatherInfoTextView;
	@ViewInject(id=R.id.temp_low_weather)private TextView tempLowTextView;
	@ViewInject(id=R.id.temp_high_weather)private TextView tempHighTextView;
	@ViewInject(id=R.id.progress_bar_layout)private RelativeLayout progressBarLayout;
	@ViewInject(id=R.id.show_weather_layout)private RelativeLayout showWeatherLayout;
	@ViewInject(id=R.id.refresh_btn)private Button refreshButton;
	@ViewInject(id=R.id.switch_city_btn)private Button  switchCityButton;
	
	private Context mContext;
	private County selectedCounty;
	private FinalHttp finalHttp;
	private String weatherCode;
	private WeatherInfo weatherInfo;
	private String currentDate;
	private int backPressTimes = 0;
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case 0:
				//更新界面数据
				showWeatherInfoLayout();
				refreshWeatherInfoForUI(weatherInfo);
				break;
			case 1:
				//显示进度条
				showProgressBarLayout();
				break;
			default:
				break;
			}
			
	    }
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        AppManager.getAppManager().addActivity(this);
        selectedCounty = (County) getIntent().getSerializableExtra("selectedCounty");
        mContext = this;
        titleTextView.setText("天气："+selectedCounty.getCountyName());
        
        refreshButton.setOnClickListener(new MyOnClickListener());
        switchCityButton.setOnClickListener(new MyOnClickListener());
        
        //查询天气 并更新UI
        queryWeatherInfo(selectedCounty);
        
        Intent intent2 = new Intent(mContext, AutoUpdateService.class);
		startService(intent2);
	}
	
	@Override 
	public void onBackPressed() {
		backPressTimes++;
		if (backPressTimes == 1) {
			Toast.makeText(mContext, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
		} else if (backPressTimes == 2) {
			super.onBackPressed();
		}
	}
	
	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.refresh_btn:
				queryWeatherInfo(selectedCounty);
//				finish();
//				AppManager.getAppManager().AppExit(mContext);
				break;
				
			case R.id.switch_city_btn:
				
				Intent intent = new Intent(mContext, ChoseAreaActivity.class);
				intent.putExtra("fromShowWeatherActivity", "switchCityButtonClick");
				startActivity(intent);
				finish();
				break;

			default:
				break;
			}
		}
		
	}
	
	//查询天气信息 并更新UI
	private void queryWeatherInfo(County county) {
		String address = "http://www.weather.com.cn/data/list3/city" + selectedCounty.getCountyCode() + ".xml";
		//查询县级代号所对应的天气代号。
//		showProgressBarLayout();
		mHandler.sendEmptyMessage(1);
		queryFromServer(address, "WeatherCode");
	}
	
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
	 */
	private void queryFromServer(final String address, final String type) {
		finalHttp = new FinalHttp();
		finalHttp.get(address, new AjaxCallBack<String>() {
        	
        	//当我们请求失败的时候会被调用，errorNo是请求失败之后，服务器的错误码,StrMsg则是错误信息  
    		@Override
    	    public void onFailure(Throwable t,int errorNO, String strMsg) {
    	        //加载失败的时候回调
    			super.onFailure(t,errorNO, strMsg);
    			Toast.makeText(mContext, "读取失败", 0).show();
    	    }
    		
            //如果请求成功，则调用这个回调函数，t就是服务器返回的字符串信息  
            @Override  
            public void onSuccess(String t) {  
            	
                super.onSuccess(t);
                
                if ("WeatherCode".equals(type)) {
                	String[] tempStrings = t.split("\\|");
                	weatherCode = tempStrings[1];
                	
                	//查询天气代号所对应的天气。
                	String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
                	queryFromServer(address, "WeatherInfo");
                	
                } else if ("WeatherInfo".equals(type)) {
                	showWeatherTextView.setText(t);
                    System.out.println(t);
                    weatherInfo = parseWeatherInfo(t);
                    //更新UI
                    mHandler.sendEmptyMessage(0);
                  
                }

            }  
		});
	}
	
	private WeatherInfo parseWeatherInfo(String jsonDatas) {
		
		WeatherInfo weatherInfo = null;
		try {
			System.out.println("parseWeatherInfo");
			JSONObject jsonObject = new JSONObject(jsonDatas);
			JSONObject weatherJson = jsonObject.getJSONObject("weatherinfo");
			
			weatherInfo = new WeatherInfo();
			weatherInfo = new WeatherInfo();
			weatherInfo.setCity(weatherJson.getString("city"));
			weatherInfo.setTempHigh(weatherJson.getString("temp1"));
			weatherInfo.setTempLow(weatherJson.getString("temp2"));
			weatherInfo.setWeather(weatherJson.getString("weather"));
			weatherInfo.setpTime(weatherJson.getString("ptime"));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return weatherInfo;
	}
	
	
	private void refreshWeatherInfoForUI(WeatherInfo weatherInfo) {
		
		publicTimeTextView.setText("发布时间："+ weatherInfo.getpTime());
		weatherInfoTextView.setText(weatherInfo.getWeather());
		tempLowTextView.setText(weatherInfo.getTempLow());
		tempHighTextView.setText(weatherInfo.getTempHigh());
		
		currentTimeTextView.setText(getCurrentTime());
		
	}
	
	private String getCurrentTime() {
		String currentDate;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 ");
        currentDate = dateFormat.format(new Date());
        
        return currentDate;
	}
	
	private void showProgressBarLayout() {
		progressBarLayout.setVisibility(View.VISIBLE);
		showWeatherLayout.setVisibility(View.INVISIBLE);
	}
	
	private void showWeatherInfoLayout() {
		progressBarLayout.setVisibility(View.GONE);
		showWeatherLayout.setVisibility(View.VISIBLE);
	}
	
}
