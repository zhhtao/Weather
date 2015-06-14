package com.zhanghaitao.weather;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.zhanghaitao.base.AppManager;
import com.zhanghaitao.database.CoolWeatherDB;
import com.zhanghaitao.modle.City;
import com.zhanghaitao.modle.County;
import com.zhanghaitao.modle.Province;
import com.zhanghaitao.util.CustomProgress;
import com.zhanghaitao.util.MyConst;
import com.zhanghaitao.util.Utility;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.SuperscriptSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ChoseAreaActivity extends FinalActivity {

	@ViewInject(id=R.id.title_main) private TextView titleTextView;
	@ViewInject(id=R.id.listview_main) private ListView listView;
	@ViewInject(id=R.id.show_test)TextView showTextView;
	
	private static final int ProvinceLevel = 1, CityLevel = 2, CountyLevel = 3,WeatherInfoLevel = 4;
	private static int currentLevel = 0; 
	private Context mContext;
	private CustomProgress myProgressDialog;
	
	private List<Province> provinceList = new ArrayList<Province>();
	private List<City> cityList = new ArrayList<City>();
	private List<County> countyList = new ArrayList<County>();
	
	private static Province selectedProvince = new Province();
	private static City selectedCity = new City();
	private  static County selectedCounty = null;
	
	private List<String> dataList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private FinalHttp finalHttp;
	private CoolWeatherDB db;
	
	String[] ss = {"d","a"};
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			myProgressDialog.dismiss();
	    }
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppManager.getAppManager().addActivity(this);
        mContext = this;
        
        db = CoolWeatherDB.getInstance(mContext);
        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        
        //先判断是否从天气活动的切换城市按键跳转回来
        selectedCounty = getSavedSelecteCounty();//读取sharePreferences 
        String get = getIntent().getStringExtra("fromShowWeatherActivity");
        if ("switchCityButtonClick".equals(get)) {
        	myProgressDialog = CustomProgress.show(mContext, "加载中……", false, null);
        	showProvince();
        	
        	//如果之前已经选中了某个县  则直接显示该县的天气
        } else
        if (selectedCounty != null) {
        	Intent intent = new Intent(mContext, ShowWeatherActivity.class);
			intent.putExtra("selectedCounty", selectedCounty);
			startActivity(intent);
			finish();
			currentLevel = WeatherInfoLevel;
        } else {
        	
        	myProgressDialog = CustomProgress.show(mContext, "加载中……", false, null);
        	showProvince();
        }
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (currentLevel == ProvinceLevel) {
					selectedProvince = provinceList.get(position);
//					System.out.println(selectedProvince.getProvinceName());
					myProgressDialog = CustomProgress.show(mContext, "加载中……", false, null);
					showCity(selectedProvince);
					
					System.out.println("test1 " + selectedProvince.getProvinceName());
					String string = Utility.objectToString(selectedProvince);
					System.out.println(string);
					Province province = (Province) Utility.stringToObject(string, new Province());
					System.out.println("test2 "+ province.getProvinceName());
					
				} else if (currentLevel == CityLevel) {
					
					selectedCity = cityList.get(position);
//					System.out.println(selectedCity.getCityName());
					myProgressDialog = CustomProgress.show(mContext, "加载中……", false, null);
					showCounty(selectedCity);
					
					System.out.println("test3 " + selectedCity.getCityName());
					String string = Utility.objectToString(selectedCity);
					System.out.println(string);
					City province =  Utility.stringToObject(string, new City());
					System.out.println("test4 "+ province.getCityName());
					
				} else if (currentLevel == CountyLevel) {
					
					selectedCounty = countyList.get(position);
//					System.out.println(selectedCounty.getCountyName());
					Intent intent = new Intent(mContext, ShowWeatherActivity.class);
					intent.putExtra("selectedCounty", selectedCounty);
					startActivity(intent);
					finish();
				}
				
			}
		});
        
    }
    
//    //该活动运行模式为singleTask，当从天气活动跳转回来后进入城市选择界面
//    @Override
//    public void onResume() {
//    	super.onResume();
//    	if (currentLevel == WeatherInfoLevel) {
//    		showProvince();
//    	} 
//    }
    
    private void showProvince() {
    	provinceList = db.loadProvince();
    	//如果数据库有 则从数据库提取
    	if (provinceList.size() != 0) {
    		dataList.clear();
            for (int i=0; i<provinceList.size(); i++) {
            	String name  = provinceList.get(i).getProvinceName();
//            			+" id:"+provinceList.get(i).getId();
            	dataList.add(name);
            }
            
            mHandler.sendEmptyMessage(0);
            currentLevel = ProvinceLevel;
            titleTextView.setText("中国");
            //如果数据库没有 则从服务器查询
    	} else {
    		queryProvinceFromServer();
    	}
    }
    
    private void queryProvinceFromServer() {
    	
    	finalHttp = new FinalHttp();
        finalHttp.get(MyConst.ServerURLHeader+MyConst.ServerURLTail, new AjaxCallBack<String>() {
        	
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
                
                showTextView.setText(t);
                System.out.println(t);
                Utility.handleProviceSaveToDB(db, t);
                
                showProvince();

            }  
		});
    }
    
    private void showCity(Province province) {
    	cityList = db.loadCities(province.getId());
    	
    	if (cityList.size() != 0) {
    		dataList.clear();
            for (int i=0; i<cityList.size(); i++) {
            	String name  = cityList.get(i).getCityName();
//            			+" id:"+cityList.get(i).getId();
            	dataList.add(name);
            }
            mHandler.sendEmptyMessage(0);
            
            currentLevel = CityLevel;
            titleTextView.setText("省："+province.getProvinceName());
    		
    	} else {
    		queryCityFromServer(province);
    	}
    }

    private void queryCityFromServer(final Province province) {
    	finalHttp = new FinalHttp();
        finalHttp.get(MyConst.ServerURLHeader+province.getProvinceCode() + MyConst.ServerURLTail, new AjaxCallBack<String>() {
        	
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
                
//                showTextView.setText(t);
                System.out.println(t);
                Utility.handleCitySaveToDB(db, t, province);
                showCity(province);

            }  
		});
    }
    
    
    private void showCounty(City city) {
    	System.out.println(city.getId());
    	countyList = db.loadCounty(city.getId());
    	if (countyList.size() != 0) {
    		dataList.clear();
    		for (int i=0; i<countyList.size(); i++) {
    			String name = countyList.get(i).getCountyName();
//    					+ " id:" + countyList.get(i).getId();
    			dataList.add(name);
    		}
    		mHandler.sendEmptyMessage(0);
    		currentLevel = CountyLevel;
    		titleTextView.setText("市："+city.getCityName());
    	} else {
    		queryCountyFromServer(city);
    	}
    }
    
    private void queryCountyFromServer(final City city) {
    	finalHttp = new FinalHttp();
        finalHttp.get(MyConst.ServerURLHeader+city.getCityCode() + MyConst.ServerURLTail, new AjaxCallBack<String>() {
        	
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
                
//                showTextView.setText(t);
                System.out.println(t);
                Utility.handleCountySaveToDB(db, t, city);
                showCounty(city);

            }  
		});
    }
    
    private void saveSelectedCounty(County selectedCounty) {
    	SharedPreferences.Editor editor = getSharedPreferences("savedData", MODE_PRIVATE).edit();
    	String serStr = null;
    	try {
    		//将对象序列化成字符串：编码
        	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(  
                    byteArrayOutputStream);  
            objectOutputStream.writeObject(selectedCounty);  
            serStr = byteArrayOutputStream.toString("ISO-8859-1");  
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");  
            objectOutputStream.close();  
            byteArrayOutputStream.close();
            
		} catch (Exception e) {
			// TODO: handle exception
		}
    	editor.putString("selectedCounty", serStr);
    	editor.commit();
    }
    
    private County getSavedSelecteCounty() {
    	County county = null ;
    	SharedPreferences sharedPreferences = getSharedPreferences("savedData", MODE_PRIVATE);
    	String str = sharedPreferences.getString("selectedCounty", "");
    	
    	//如果没有保存过 则返回空
    	if (str.equals("")) {
    		return null;
    	}
    	
    	//将字符串转化为对象：解码
    	try {
    		String redStr = java.net.URLDecoder.decode(str, "UTF-8");  
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(  
                    redStr.getBytes("ISO-8859-1"));  
            ObjectInputStream objectInputStream = new ObjectInputStream(  
                    byteArrayInputStream);  
            county = (County) objectInputStream.readObject();  
            objectInputStream.close();  
            byteArrayInputStream.close(); 
            
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
        return county;
    }
    
    @Override 
    public void onDestroy() {
    	
    	saveSelectedCounty(selectedCounty);
    	super.onDestroy();
    	
    }
    
    @Override 
    public void onBackPressed() {
    	if (currentLevel == CityLevel) {
    		showProvince();
    	} else if (currentLevel == CountyLevel) {
    		showCity(selectedProvince);
    	} else {
    		finish();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
