package lsw.liuyao.wxapi;

/**
 * Created by swli on 9/1/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import lsw.liuyao.HexagramBuilderActivity;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    // IWXAPI ????app??????openapi??
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, "wx4c9850d2ade4b2e9");
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                Toast.makeText(this,"test go from weixin", Toast.LENGTH_SHORT);
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                Intent intent = new Intent();
                intent.setClass(WXEntryActivity.this, HexagramBuilderActivity.class);
                startActivityForResult(intent, 0);
                Toast.makeText(this,"test show from weixin", Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
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

