package com.zhanghaitao.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.zhanghaitao.database.CoolWeatherDB;
import com.zhanghaitao.modle.City;
import com.zhanghaitao.modle.County;
import com.zhanghaitao.modle.Province;

import android.R.integer;
import android.content.Context;

public class Utility {
	
	public static void handleProviceSaveToDB(CoolWeatherDB db, String response) {
		String[] pvs,pv;
		if (response != null) {
			pvs = response.split(",");
			
			for (int i=0; i<pvs.length; i++) {
//				System.out.println(pvs[i]);
				pv = pvs[i].split("\\|");
//				System.out.println(pv[0]);
//				System.out.println(pv[1]);
				
				Province province = new Province();
				province.setProvinceCode(pv[0]);
				province.setProvinceName(pv[1]);
				db.saveProvivce(province);
			}
		}
	}
	
	public static void handleCitySaveToDB(CoolWeatherDB db, String response, Province province) {
		String[] citys,city;
		if (response != null) {
			citys = response.split(",");
			
			for (int i=0; i<citys.length; i++) {
				city = citys[i].split("\\|");
				
				City cityObject = new City();
				cityObject.setCityCode(city[0]);
				cityObject.setCityName(city[1]);
				cityObject.setProvinceId(province.getId());
				db.saveCity(cityObject);
				
			}
		}
	}
	
	public static void handleCountySaveToDB(CoolWeatherDB db, String response, City city) {
		String[] cys,cy;
		if (response != null) {
			cys = response.split(",");
			
			for (int i=0; i<cys.length; i++) {
				cy = cys[i].split("\\|");
				
				County county = new County();
				county.setCountyCode(cy[0]);
				county.setCountyName(cy[1]);
				county.setCityId(city.getId());
				db.saveCounty(county);
			}
		}
	}
	
	public static String objectToString(Object object) {
		String string = null;
		//���������л����ַ���������
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			
			string = byteArrayOutputStream.toString("ISO-8859-1");
			string = java.net.URLEncoder.encode(string, "UIF-8");
			objectOutputStream.close();
			byteArrayOutputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return string;
	}
	
	//����ʹ���˷��� ��Ϊ�����޷�ʵ���� ������Ҫ����һ����ת���Ķ������͵�ʵ��
	public static<T> T stringToObject (String string, T object) {
		String redStr = null;
//		T objec = new T();//������ݲ�ͬ�����޸�
		try {
			redStr = java.net.URLDecoder.decode(string, "UTF-8");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1")); 
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			
			object = (T) objectInputStream.readObject();
			
			objectInputStream.close();
			byteArrayInputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return object;
	}
}
