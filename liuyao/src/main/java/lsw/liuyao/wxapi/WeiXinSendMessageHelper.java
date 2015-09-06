package lsw.liuyao.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

import lsw.liuyao.R;

/**
 * Created by swli on 9/2/2015.
 */
public class WeiXinSendMessageHelper {

    final static int THUMB_SIZE = 150;

    public static void sendAppMessage(Context context, IWXAPI api, String title, String descpription) {
        WXAppExtendObject appdata = new WXAppExtendObject();
        //String path = SDCARD_ROOT + "/test.png";
        //appdata.fileData = Util.readFromFile(path, 0, -1);
         appdata.extInfo = title;
//        appdata.fileData = object;

//        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//        bmp.recycle();

        WXMediaMessage msg = new WXMediaMessage();
        //msg.setThumbImage(thumbBmp);
        msg.title = title;
        msg.description = descpription;
        msg.mediaObject = appdata;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    public static void sendMessage(IWXAPI api)
    {
        WXTextObject textObject = new WXTextObject();
        textObject.text = "??,??????????!";

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.description = "??,??????????!";

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = mediaMessage;
        //req.scene = SendMessageToWX.Req.WXSceneSession;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }
}
