package com.code.aon.csb.fd0.model.CSB19.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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
	 * The disk making date
	 */
	private Date makeDate;
	/**
	 * Start date
	 */
	private Date startDate;
	/**
	 * Name
	 */
	private String name;
	/**
	 * Account
	 */
	private Account account;
	/**
	 * Procedure
	 */
	private Integer procedure;

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
	 * @return the makeDate
	 */
	public Date getMakeDate() {
		return makeDate;
	}

	/**
	 * @param makeDate the makeDate to set
	 */
	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
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
	 * @return the procedure
	 */
	public Integer getProcedure() {
		return procedure;
	}

	/**
	 * @param procedure the procedure to set
	 */
	public void setProcedure(Integer procedure) {
		this.procedure = procedure;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
		description += "MAKE_DATE "+makeDate+"; ";
		description += "START_DATE "+startDate+"; ";
		description += "NAME "+name+"; ";
		description += "ACCOUNT "+account.getCcc()+"; ";
		description += "PROC "+procedure+"; ";
		return description;
	}
	
}
