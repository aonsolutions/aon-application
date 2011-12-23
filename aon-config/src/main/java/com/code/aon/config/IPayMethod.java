package com.code.aon.config;


public interface IPayMethod {

    public PayMethod getPayment();
	public Bank getBank();
	public BankAccount getBankAccount();
	public int getNumberOfPayments();
	public int getDaysToFirstPayment();
	public int getDaysBetweenPayments();
	public String getPaymentDays();
	public int[] getPaymentDaysArray();
   
}