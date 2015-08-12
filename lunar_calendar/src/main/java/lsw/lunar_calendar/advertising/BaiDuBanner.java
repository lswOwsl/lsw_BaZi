package lsw.lunar_calendar.advertising;

import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.baidu.appx.BDBannerAd;

import lsw.lunar_calendar.R;

/**
 * Created by lsw_wsl on 7/18/15.
 */
public class BaiDuBanner {

    private RelativeLayout appxBannerContainer;
    private static BDBannerAd bannerAdView;
    private static String TAG = "AppX_BannerAd";


    private Activity activity;

    public BaiDuBanner(Activity context)
    {
        this.activity = context;
    }

    private boolean isActive()
    {
        return activity.getResources().getBoolean(R.bool.includeAD);
    }

    public void create(){

        if (!isActive())
            return;

        // 创建广告视图
        // 发布时请使用正确的ApiKey和广告位ID
        // 此处ApiKey和推广位ID均是测试用的
        // 您在正式提交应用的时候，请确认代码中已经更换为您应用对应的Key和ID
        // 具体获取方法请查阅《百度开发者中心交叉换量产品介绍.pdf》
//        bannerAdView = new BDBannerAd(activity, "CRqGC0MMbzpSLT2EYgDKk58d6ymsHylt",
//                "TRwQxo62D74ULcY9TDRCjvno");
        bannerAdView = new BDBannerAd(activity, "3Qh1lG1mNW65Wx155M3WV48c",
                "fNEy9eUvTdAzKzMKqFxZvh7B");

        // 设置横幅广告展示尺寸，如不设置，默认为SIZE_FLEXIBLE;
        //bannerAdView.setAdSize(BDBannerAd.SIZE_FLEXIBLE);

        // 设置横幅广告行为监听器
        bannerAdView.setAdListener(new BDBannerAd.BannerAdListener() {

            @Override
            public void onAdvertisementDataDidLoadFailure() {
                Log.e(TAG, "load failure");
            }

            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                Log.e(TAG, "load success");
            }

            @Override
            public void onAdvertisementViewDidClick() {
                Log.e(TAG, "on click");
            }

            @Override
            public void onAdvertisementViewDidShow() {
                Log.e(TAG, "on show");
            }

            @Override
            public void onAdvertisementViewWillStartNewIntent() {
                Log.e(TAG, "leave app");
            }
        });

        // 创建广告容器
        appxBannerContainer = (RelativeLayout) activity.findViewById(R.id.appx_banner_container);

        // 显示广告视图
        appxBannerContainer.addView(bannerAdView);
    }
}
