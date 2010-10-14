package com.code.aon.config;

import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;

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