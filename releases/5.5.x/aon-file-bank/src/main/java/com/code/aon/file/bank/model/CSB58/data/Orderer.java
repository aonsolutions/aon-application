package com.code.aon.file.bank.model.CSB58.data;

import java.util.ArrayList;
import java.util.Iterator;

import com.code.aon.file.format.core.Account;

/**
 * 
 * @author Consulting & Development. Iñigo GAyarre - 26/02/2007
 * @since 1.0
 *
 */
public class Orderer {

	/**
	 * Code 
	 */
	private String code;
	/**
	 * Sufix
	 */
	private String sufix;
	/**
	 * Name
	 */
	private String name;
	/**
	 * Account
	 */
	private Account account;
	/**
	 * INE
	 */
	private Integer codeINE;

	public int numRegs = 0;
	
	/**
	 * Individuals
	 */
	private ArrayList<Individual> individuals = new ArrayList<Individual>();

	
	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account the acount to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the sufix
	 */
	public String getSufix() {
		return sufix;
	}

	/**
	 * @param sufix the sufix to set
	 */
	public void setSufix(String sufix) {
		this.sufix = sufix;
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


	/**
	 * @return the amount
	 */
	public Double getAmount() {
		Iterator<Individual> iter = this.getIndividualsIterator();
		double total = 0.0;
		while (iter.hasNext()){
			total += iter.next().getAmount().doubleValue();
		}
		return total;
	}

	
	/**
	 * @return the codeINE
	 */
	public Integer getCodeINE() {
		return codeINE;
	}

	/**
	 * @param codeINE the codeINE to set
	 */
	public void setCodeINE(Integer codeINE) {
		this.codeINE = codeINE;
	}

	/**
	 * @return the numRegs
	 */
	public int getNumRegs() {
		return numRegs;
	}

	/**
	 * @param numRegs the numRegs to set
	 */
	public void setNumRegs(int numRegs) {
		this.numRegs = numRegs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "ORDERER ";
		description += "CODE "+code+"; ";
		description += "SUFIX "+sufix+"; ";
		description += "NAME "+name+"; ";
		description += "ACCOUNT "+account.getCcc()+"; ";
		description += "CODE_INE "+codeINE+"; ";
		return description;
	}
	
}
