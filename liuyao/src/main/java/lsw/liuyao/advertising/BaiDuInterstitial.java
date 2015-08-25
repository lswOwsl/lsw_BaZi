package lsw.liuyao.advertising;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.appx.BDInterstitialAd;

import lsw.liuyao.R;

/**
 * Created by swli on 8/25/2015.
 */
public class BaiDuInterstitial {


    private BDInterstitialAd appxInterstitialAdView;
	private Button appxInterstitialBtn;

    private static String TAG = "AppX_Interstitial";
    private Activity activity;

    public BaiDuInterstitial(Activity context)
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

        appxInterstitialAdView = new BDInterstitialAd(activity,
                "N5Qtkxs3hNF0bLVmGgwM6pmo", "CWsAoGkwxoBlZzaN9Wq2zls8");

        appxInterstitialAdView.setAdListener(new BDInterstitialAd.InterstitialAdListener() {

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
            public void onAdvertisementViewDidHide() {
                Log.e(TAG, "on hide");
            }

            @Override
            public void onAdvertisementViewDidShow() {
                Log.e(TAG, "on show");
            }

            @Override
            public void onAdvertisementViewWillStartNewIntent() {
                Log.e(TAG, "leave");
            }
        });

        appxInterstitialAdView.loadAd();

    }

    public void loadInterstitialAdOnButton()
    {
        if (appxInterstitialAdView.isLoaded()) {
            appxInterstitialAdView.showAd();
        } else {
            Log.i(TAG, "AppX BaiDuInterstitial Ad is not ready");
            appxInterstitialAdView.loadAd();
        }
    }
}

