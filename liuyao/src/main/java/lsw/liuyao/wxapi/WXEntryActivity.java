package lsw.liuyao.wxapi;

/**
 * Created by swli on 9/1/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Objects;

import lsw.Util;
import lsw.liuyao.HexagramAnalyzerActivity;
import lsw.liuyao.HexagramBuilderActivity;
import lsw.liuyao.common.IntentKeys;


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
                //Toast.makeText(this,"test go from weixin", Toast.LENGTH_SHORT);
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:

                WXMediaMessage mediaMessage = ((ShowMessageFromWX.Req) req).message;
//                if(mediaMessage.mediaObject instanceof WXAppExtendObject)
//                {
//                    WXAppExtendObject extendObject = (WXAppExtendObject)mediaMessage.mediaObject;
//                    String exInfo = extendObject.extInfo;
//                    byte[] bytes = extendObject.fileData;
//
//                    try {
                //Intent intent = (Intent)((Object) Util.unmarshall(bytes));
                Intent mIntent = new Intent(this, HexagramAnalyzerActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(IntentKeys.FormatDate, mediaMessage.title);
                mBundle.putString(IntentKeys.OriginalName, mediaMessage.description.substring(0, mediaMessage.description.indexOf("-")));
                String changedName = "";
                if (mediaMessage.description.length() >= 3) {
                    changedName = mediaMessage.description.substring(mediaMessage.description.indexOf("-")+1);
                }
                mBundle.putString(IntentKeys.ChangedName, changedName);
                mIntent.putExtras(mBundle);

                startActivity(mIntent);

//                    }
//                    catch (Exception ex)
//                    {
//                        Log.e("convert byte to object", ex.getMessage());
//                    }
                //}
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

