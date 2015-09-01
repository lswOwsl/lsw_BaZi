package weixin;

/**
 * Created by swli on 9/1/2015.
 */

import android.app.Activity;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WeiXinEntryActivity extends Activity implements IWXAPIEventHandler {
    // IWXAPI ????app??????openapi??
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, "wx4c9850d2ade4b2e9", false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //????
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //????
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //????
                break;
        }
    }
}

