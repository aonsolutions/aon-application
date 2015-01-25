package com.code.aon.file.tax.model.MOD193;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.file.tax.FileTaxUtil;

public class Deponent {

	private int year;
	private String document;
	private String name;
	private String contactPhone;
	private String contactPerson;
	private String receipt;
	private String complementary;
	private String replacement;
	private String replacedReceipt;
	private int c001;
	private double c002;
	private double c003;
	private double c004;
	private double c005;
	
	private List<Receiver> receivers;
	private List<Expenses> expenses;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = FileTaxUtil.changeInvalidCharacters(document);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = FileTaxUtil.changeInvalidCharacters(name);
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = FileTaxUtil.changeInvalidCharacters(contactPhone);
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = FileTaxUtil.changeInvalidCharacters(contactPerson);
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = FileTaxUtil.changeInvalidCharacters(receipt);
	}

	public String getComplementary() {
		return complementary;
	}

	public void setComplementary(String complementary) {
		this.complementary = FileTaxUtil.changeInvalidCharacters(complementary);
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = FileTaxUtil.changeInvalidCharacters(replacement);
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}

	public void setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = FileTaxUtil.changeInvalidCharacters(replacedReceipt);
	}

	public int getC001() {
		return c001;
	}

	public void setC001(int c001) {
		this.c001 = c001;
	}

	public double getC002() {
		return c002;
	}

	public void setC002(double c002) {
		this.c002 = c002;
	}

	public double getC003() {
		return c003;
	}

	public void setC003(double c003) {
		this.c003 = c003;
	}

	public double getC004() {
		return c004;
	}

	public void setC004(double c004) {
		this.c004 = c004;
	}

	public double getC005() {
		return c005;
	}

	public void setC005(double c005) {
		this.c005 = c005;
	}

	public List<Receiver> getReceivers() {
		if (receivers == null) {
			receivers = new LinkedList<Receiver>();
		}
		return receivers;
	}

	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}

	public List<Expenses> getExpenses() {
		if (expenses == null) {
			expenses = new LinkedList<Expenses>();
		}
		return expenses;
	}

	public void setExpenses(List<Expenses> expenses) {
		this.expenses = expenses;
	}
	
	
}
