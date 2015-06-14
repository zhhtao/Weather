package com.zhanghaitao.base;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
	}
}
