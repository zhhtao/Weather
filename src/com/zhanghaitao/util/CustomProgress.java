package com.zhanghaitao.util;

import com.zhanghaitao.weather.R;
import com.zhanghaitao.weather.R.id;
import com.zhanghaitao.weather.R.layout;
import com.zhanghaitao.weather.R.style;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomProgress extends Dialog{

	public CustomProgress(Context context) {
		
		super(context, R.layout.progress_custom);
		// TODO Auto-generated constructor stub
	}

	public CustomProgress(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}
	
	public CustomProgress(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	/** 
     * ��Dialog������ʾ��Ϣ 
     *  
     * @param message 
     */  
    public void setMessage(CharSequence message) {  
        if (message != null && message.length() > 0) {  
            findViewById(R.id.message).setVisibility(View.VISIBLE);  
            TextView txt = (TextView) findViewById(R.id.message);  
            txt.setText(message);  
            txt.invalidate();  
        }  
    }  
  
    /** 
     * �����Զ���ProgressDialog 
     *  
     * @param context 
     *            ������ 
     * @param message 
     *            ��ʾ 
     * @param cancelable 
     *            �Ƿ񰴷��ؼ�ȡ�� 
     * @param cancelListener 
     *            ���·��ؼ����� 
     * @return 
     */  
    
    /*
     * 
     * <!-- �Զ���Dialog -->  
    <style name="Custom_Progress" parent="@android:style/Theme.Dialog">
        <item name="android:windowFrame">@null</item>
        <item name="android:windowIsFloating">true</item>
        <item name="android:windowContentOverlay">@null</item>
        <item name="android:windowAnimationStyle">@android:style/Animation.Dialog</item>
        <item name="android:windowSoftInputMode">stateUnspecified|adjustPan</item>
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowNoTitle">true</item>
    </style>
     */
    public static CustomProgress show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {  
        CustomProgress dialog = new CustomProgress(context, R.style.Custom_Progress);  
        dialog.setTitle("");  
        dialog.setContentView(R.layout.progress_custom);  
        if (message == null || message.length() == 0) {  
            dialog.findViewById(R.id.message).setVisibility(View.GONE);  
        } else {  
            TextView txt = (TextView) dialog.findViewById(R.id.message);  
            txt.setText(message);  
        }  
        // �����ؼ��Ƿ�ȡ��  
        dialog.setCancelable(cancelable);  
        // �������ؼ�����  
        dialog.setOnCancelListener(cancelListener);  
        // ���þ���  
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
        // ���ñ�����͸����  
        lp.dimAmount = 0.6f;  
        dialog.getWindow().setAttributes(lp);  
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);  
        dialog.show();  
        return dialog;  
    }  
}  


