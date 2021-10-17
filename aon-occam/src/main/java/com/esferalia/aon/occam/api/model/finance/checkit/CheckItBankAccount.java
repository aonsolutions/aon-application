package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.BankStatement;

public class CheckItBankAccount implements Serializable {

	private static final long serialVersionUID = 1175684863512830855L;

	private String ccc; // "ccc"
	private Date atDate; // "fecha_saldo"
	private Integer bankId; // "banco_id"
	private String bank; // "entidad"
	private String logo; // "logo"
	private Integer bankAccountId; // "id_cuentabancaria"
	private double balance; // "saldo"
	private double remainder; // "disponible"
	private Integer bankAccountType; // "tipo_cuenta_bancaria_id"
	private Integer bankLoginType; // "tipo_login_banco_id"
	private List<BankStatement> pending;
	private List<CheckItLog> logs;

	public String getCcc() {
		return ccc;
	}
	public CheckItBankAccount setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}

	public Date getAtDate() {
		return atDate;
	}
	public CheckItBankAccount setAtDate(Date atDate) {
		this.atDate = atDate;
		return this;
	}

	public Integer getBankId() {
		return bankId;
	}
	public CheckItBankAccount setBankId(Integer bankId) {
		this.bankId = bankId;
		return this;
	}

	public String getBank() {
		return bank;
	}
	public CheckItBankAccount setBank(String bank) {
		this.bank = bank;
		return this;
	}

	public Integer getBankAccountId() {
		return bankAccountId;
	}
	public CheckItBankAccount setBankAccountId(Integer bankAccountId) {
		this.bankAccountId = bankAccountId;
		return this;
	}

	public double getBalance() {
		return balance;
	}
	public CheckItBankAccount setBalance(double balance) {
		this.balance = balance;
		return this;
	}

	public double getRemainder() {
		return remainder;
	}
	public CheckItBankAccount setRemainder(double remainder) {
		this.remainder = remainder;
		return this;
	}

	public Integer getBankAccountType() {
		return bankAccountType;
	}
	public CheckItBankAccount setBankAccountType(Integer bankAccountType) {
		this.bankAccountType = bankAccountType;
		return this;
	}

	public Integer getBankLoginType() {
		return bankLoginType;
	}
	public CheckItBankAccount setBankLoginType(Integer bankLoginType) {
		this.bankLoginType = bankLoginType;
		return this;
	}
	public List<BankStatement> getPending() {
		return pending;
	}
	public CheckItBankAccount setPending(List<BankStatement> pending) {
		this.pending = pending;
		return this;
	}
	public List<CheckItLog> getLogs() {
		return logs;
	}
	public CheckItBankAccount setLogs(List<CheckItLog> logs) {
		this.logs = logs;
		return this;
	}
	public String getLogo() {
		return logo;
	}
	public CheckItBankAccount setLogo(String logo) {
		this.logo = logo;
		return this;
	}

}
