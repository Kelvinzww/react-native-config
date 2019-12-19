package com.lugg.ReactNativeConfig;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class ReactNativeConfigModule extends ReactContextBaseJavaModule {
  public ReactNativeConfigModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "ReactNativeConfig";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    try {
      Context context = getReactApplicationContext();
      int resId = context.getResources().getIdentifier("build_config_package", "string", context.getPackageName());
      String className;
      try {
        className = context.getString(resId);
      } catch (Resources.NotFoundException e) {
        className = getReactApplicationContext().getApplicationContext().getPackageName();
      }
      // because of BuildConfig is not in applicationId's directory when use productFlavors in rn0.60+
      boolean isExist = new File(className + ".BuildConfig").exists();
      if(!isExist){
      //TODO  
      //for the most part , we add dot into ending; such as xx.dev xx.test
      //so we get default applicationId  temporarily like this
        className = className.substring(0, className.lastIndexOf("."));
      }

      Class clazz = Class.forName(className + ".BuildConfig");
      Field[] fields = clazz.getDeclaredFields();
      for(Field f: fields) {
        try {
          constants.put(f.getName(), f.get(null));
        }
        catch (IllegalAccessException e) {
          Log.d("ReactNative", "ReactConfig: Could not access BuildConfig field " + f.getName());
        }
      }
    }
    catch (ClassNotFoundException e) {
      Log.d("ReactNative", "ReactConfig: Could not find BuildConfig class");
    }

    return constants;
  }
}
