package com.example.swli.myapplication20150519.common;

/**
 * Created by swli on 5/26/2015.
 */
public class SolarTerm
{
    private DateExt solarTermDate;
    private String name;


    public void setSolarTermDateTime(DateExt date)
    {
        this.solarTermDate = date;
    }

    public DateExt getSolarTermDate()
    {
        return  this.solarTermDate;
    }


    public String getName()
    {
        return  this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}