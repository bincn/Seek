package wb.app.seek.common.base;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import wb.app.library.MLog;
import wb.app.seek.BuildConfig;

/**
 * Created by W.b on 2016/12/29.
 */
public class BaseApplication extends Application {

  private static BaseApplication mInstance;
  private SeekHelper mHelper;

  @Override
  public void onCreate() {
    super.onCreate();

    mInstance = this;

    mHelper = SeekHelper.getInstance();

    MLog.init(BuildConfig.DEBUG);

    //初始化内存泄露检测
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    LeakCanary.install(this);
  }

  public static BaseApplication getInstance() {
    return mInstance;
  }

  public SeekHelper getHelper() {
    return mHelper;
  }
}
