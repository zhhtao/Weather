package com.zhanghaitao.database;
import java.util.ArrayList;
import java.util.List;

import com.zhanghaitao.modle.City;
import com.zhanghaitao.modle.County;
import com.zhanghaitao.modle.Province;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	public static final String DB_NAME = "cool_weather";
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public synchronized static CoolWeatherDB getInstance (Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		
		return coolWeatherDB;
	}
	
	public void saveProvivce(Province province) {
		if(province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			
			db.insert("Province", null, values);
	
		}
	}
		
	public List<Province> loadProvince() {
		List<Province> list = new ArrayList<Province>();
		
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				
				list.add(province);
			} while (cursor.moveToNext());
			
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public void deleteProvivce() {
		db.delete("Province", null, null);
	}
	
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
		
	public List<City> loadCities(int proviceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(proviceId)}, null, null, null);
		
		if(cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		return list;
	}
	
	public void deleteCities() {
		db.delete("City", null, null);
	}
	
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	
	public List<County> loadCounty(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		
		if(cursor != null) {
			cursor.close();
		}
		
		return list;
	}
	
	public void deleteCounty() {
		db.delete("County", null, null);
	}
}