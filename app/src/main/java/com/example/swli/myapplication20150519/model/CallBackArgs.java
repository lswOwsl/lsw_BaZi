package com.example.swli.myapplication20150519.model;

/**
 * Created by swli on 6/15/2015.
 */
public class CallBackArgs {
    private Integer daYunEraIndex;
    private Integer flowYearEraIndex;
    private Integer currentAge;
    private Integer flowMonthEraIndex;
    private boolean isFlowMonthClick;

    public boolean isFlowMonthClick() {
        return isFlowMonthClick;
    }

    public void setIsFlowMonthClick(boolean isFlowMonthClick) {
        this.isFlowMonthClick = isFlowMonthClick;
    }

    public Integer getFlowMonthEraIndex() {
        return flowMonthEraIndex;
    }

    public void setFlowMonthEraIndex(Integer flowMonthEraIndex) {
        this.flowMonthEraIndex = flowMonthEraIndex;
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
