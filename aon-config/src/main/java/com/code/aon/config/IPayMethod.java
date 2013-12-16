package com.code.aon.config;


public interface IPayMethod {

    public PayMethod getPayment();
	public BankAccount getBankAccount();
	public String getBankAlias();
	public String getBic();
	public int getNumberOfPayments();
	public int getDaysToFirstPayment();
	public int getDaysBetweenPayments();
	public String getPaymentDays();
	public int[] getPaymentDaysArray();
   
}