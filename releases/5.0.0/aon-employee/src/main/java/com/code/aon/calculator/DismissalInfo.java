package com.code.aon.calculator;

import java.util.Date;

import com.code.aon.calculator.enumeration.DismissalType;

public class DismissalInfo {

	private double grossSalary;
	private Date seniorityDate;

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Date getSeniorityDate() {
        return seniorityDate;
    }

    public void setSeniorityDate(Date seniorityDate) {
        this.seniorityDate = seniorityDate;
    }

    public double getCompensation(DismissalType dismissalType) {
        double years = daysBetween(getSeniorityDate(), new Date()) / 365;
        double limit = (grossSalary * getMaximumMonths(dismissalType)) / 12;
        double compensation = (grossSalary * getDaysPerWorkedYear(dismissalType) * years) / 365; 
        return (compensation > limit) ? limit : compensation;
    }

    private double daysBetween(Date dateFrom, Date dateTo){
        long diff = dateTo.getTime() - dateFrom.getTime();
        return ((diff) / (1000 * 60 * 60 * 24)); 
    }

    private int getDaysPerWorkedYear(DismissalType dismissalType) {
        if (dismissalType == DismissalType.UNFAIR) {
            return 45;
        } else if (dismissalType == DismissalType.LAYOFF_AS_UNFAIR) {
            return 33;
        } else {
            return 20;
        }
    }

    private int getMaximumMonths(DismissalType dismissalType) {
        if (dismissalType == DismissalType.UNFAIR) {
            return 42;
        } else if (dismissalType == DismissalType.LAYOFF_AS_UNFAIR) {
            return 24;
        } else {
            return 12;
        }
    }

}
