package com.example.swli.myapplication20150519.common;

import android.content.Context;

/**
 * Created by swli on 6/5/2015.
 */
public abstract class XmlParser<T> {

    protected Context context;

    public XmlParser(Context context)
    {
        this.context = context;
    }

    public abstract void parse(T t);

}


