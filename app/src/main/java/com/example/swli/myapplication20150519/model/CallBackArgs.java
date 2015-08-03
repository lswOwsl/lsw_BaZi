package com.example.swli.myapplication20150519.model;


import lsw.library.SolarTerm;

public class CallBackArgs {
    private Integer daYunEraIndex;
    private Integer flowYearEraIndex;
    private Integer flowMonthEraIndex;
    private Integer currentAge;

    public Integer getFlowMonthEraIndex() {
        return flowMonthEraIndex;
    }

    public void setFlowMonthEraIndex(Integer flowMonthEraIndex) {
        this.flowMonthEraIndex = flowMonthEraIndex;
    }

    private SolarTerm flowMonthSolarTerm;
    private boolean isFlowMonthClick;

    public boolean isFlowMonthClick() {
        return isFlowMonthClick;
    }

    public void setIsFlowMonthClick(boolean isFlowMonthClick) {
        this.isFlowMonthClick = isFlowMonthClick;
    }

    public SolarTerm getFlowMonthSolarTerm() {
        return flowMonthSolarTerm;
    }

    public void setFlowMonthSolarTerm(SolarTerm flowMonthSolarTerm) {
        this.flowMonthSolarTerm = flowMonthSolarTerm;
    }

    public Integer getDaYunEraIndex() {
        return daYunEraIndex;
    }

    public void setDaYunEraIndex(int daYunEraIndex) {
        this.daYunEraIndex = daYunEraIndex;
    }

    public Integer getFlowYearEraIndex() {
        return flowYearEraIndex;
    }

    public void setFlowYearEraIndex(int flowYearEraIndex) {
        this.flowYearEraIndex = flowYearEraIndex;
    }

    public Integer getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }
}
