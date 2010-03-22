package com.code.aon.file.bank.model.CSB32.data;

import java.util.ArrayList;
import java.util.Iterator;

import com.code.aon.file.format.core.Account;

/**
 * The withholder
 * 
 * @author Consulting & Development. Iñigo GAyarre - 03/05/2007
 * @since 1.0
 * 
 */
public class Delivery {

	/**
	 * The year
	 */
	private Integer deliveyNumber;

	/**
	 * The code
	 */
	private String giverCode;

	/**
	 * The truncated effects
	 */
	private Integer truncatedEffects;
	
	/**
	 * The payment account
	 */
	private Account paymentAccount;

	/**
	 * The owe account
	 */
	private Account oweAccount;

	/**
	 * The not payed account
	 */
	private Account notPayedAccount;

	/**
	 * Individuals
	 */
	private ArrayList<Individual> individuals = new ArrayList<Individual>();

	/**
	 * @return the deliveyNumber
	 */
	public Integer getDeliveyNumber() {
		return deliveyNumber;
	}

	/**
	 * @param deliveyNumber the deliveyNumber to set
	 */
	public void setDeliveyNumber(Integer deliveyNumber) {
		this.deliveyNumber = deliveyNumber;
	}

	/**
	 * @return the giverCode
	 */
	public String getGiverCode() {
		return giverCode;
	}

	/**
	 * @param giverCode the giverCode to set
	 */
	public void setGiverCode(String giverCode) {
		this.giverCode = giverCode;
	}

	/**
	 * @return the notPayedAccount
	 */
	public Account getNotPayedAccount() {
		return notPayedAccount;
	}

	/**
	 * @param notPayedAccount the notPayedAccount to set
	 */
	public void setNotPayedAccount(Account notPayedAccount) {
		this.notPayedAccount = notPayedAccount;
	}

	/**
	 * @return the oweAccount
	 */
	public Account getOweAccount() {
		return oweAccount;
	}

	/**
	 * @param oweAccount the oweAccount to set
	 */
	public void setOweAccount(Account oweAccount) {
		this.oweAccount = oweAccount;
	}

	/**
	 * @return the paymentAccount
	 */
	public Account getPaymentAccount() {
		return paymentAccount;
	}

	/**
	 * @param paymentAccount the paymentAccount to set
	 */
	public void setPaymentAccount(Account paymentAccount) {
		this.paymentAccount = paymentAccount;
	}


	/**
	 * Adds a Individual 
	 * 
	 * @param individual
	 */
	public void addIndividual(Individual individual) {
		this.individuals.add(individual);
	}

	/**
	 * @return individuals iterator
	 */
	public Iterator<Individual> getIndividualsIterator() {
		return this.individuals.iterator();
	}

	/**
	 * @return the individuals size
	 */
	public Integer getNumIndividuals() {
		return individuals.size();
	}

	public double getAmount() {
		Iterator<Individual> iter = this.getIndividualsIterator();
		double total = 0.0;
		while (iter.hasNext()){
			total += iter.next().getAmount().doubleValue();
		}
		return total;
	}

	/**
	 * @return the truncatedEffects
	 */
	public Integer getTruncatedEffects() {
		return truncatedEffects;
	}

	/**
	 * @param truncatedEffects the truncatedEffects to set
	 */
	public void setTruncatedEffects(Integer truncatedEffects) {
		this.truncatedEffects = truncatedEffects;
	}

	public int getNumRegs() {
		Iterator<Individual> iter = this.getIndividualsIterator();
		int total = 2;
		while (iter.hasNext()){
			iter.next();
			total += 3;
		}
		return total;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "DELIVERY ";
		description += "NUMBER "+deliveyNumber+"; ";
		description += "GIVER CODE "+giverCode+"; ";
		description += "TRUNCATED  "+truncatedEffects+"; ";
		description += "PAYMENT ACCOUNT "+paymentAccount.getCcc()+"; ";
		description += "OWNER ACCOUNT "+oweAccount.getCcc()+"; ";
		description += "NOT PAYED ACCOUNT "+notPayedAccount.getCcc()+"; ";
		return description;
	}

}
