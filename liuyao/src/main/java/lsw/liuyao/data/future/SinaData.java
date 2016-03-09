package lsw.liuyao.data.future;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import lsw.library.StringHelper;

/**
 * Created by swli on 8/28/2015.
 */
public class SinaData {

    RequestQueue mQueue;

    String url;

    public SinaData(Context context)
    {
        mQueue = Volley.newRequestQueue(context);
    }

    public interface IResult<T>
    {
        void invoke(T t);
    }

    public void  getResponeFromURL(String url, final IResult<ArrayList<DailyData>> result)
    {
        this.url = url;
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        ArrayList<DailyData> dailyDatas = parseDataByString(response);
                        result.invoke(dailyDatas);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }


    public static final String Sina_Url = "http://stock2.finance.sina.com.cn/futures/api/json.php/IndexService.";
    public static final String Sina_OneHour_Method = "getInnerFuturesMiniKLine60m?symbol=";
    public static final String Sina_Day_Method = "getInnerFuturesDailyKLine?symbol=";

    public ArrayList<DailyData> parseDataByString(String result)
    {
        ArrayList<DailyData> list = new ArrayList<DailyData>();

        result = result.substring(1, result.length() - 1);
        String[] rArray = result.split("]");
        ArrayList<String> arrayList = new ArrayList<String>();
        for(String str: rArray)
        {
            if(!StringHelper.isNullOrEmpty(str))
                arrayList.add(str);
        }

        for (String r : arrayList) {
            String obj = "";
            String[] objProperties = null;
            if(url.indexOf(Sina_OneHour_Method) > 0) {
                obj = r.substring(1);
                objProperties = obj.split(",");
            }
            else
            {
                obj = r.substring(1, r.length() - 1);
                objProperties = obj.split(",");
            }

            String p1 = ReBuildPropertyString(objProperties[0]);
            String p2 = ReBuildPropertyString(objProperties[1]);
            String p3 = ReBuildPropertyString(objProperties[2]);
            String p4 = ReBuildPropertyString(objProperties[3]);
            String p5 = ReBuildPropertyString(objProperties[4]);
            String p6 = ReBuildPropertyString(objProperties[5],0);

            DailyData ds = new DailyData();

            ds.DateTime = p1;
            ds.OpeningPrice = Double.valueOf(p2);
            ds.HighestPrice = Double.valueOf(p3);
            ds.LowestPrice = Double.valueOf(p4);
            ds.ClosingPrice = Double.valueOf(p5);
            ds.Volume = Integer.valueOf(p6);
            list.add(ds);
        }

        return list;
    }

    String ReBuildPropertyString(String p)
    {
        return ReBuildPropertyString(p,1);
    }

    String ReBuildPropertyString(String p, int offSet)
    {
        return p.substring(1, p.length() - offSet);
    }

}
