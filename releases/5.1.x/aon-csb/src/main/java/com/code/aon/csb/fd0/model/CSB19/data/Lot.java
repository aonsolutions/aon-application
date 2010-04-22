package com.code.aon.csb.fd0.model.CSB19.data;

import java.util.ArrayList;
import java.util.Iterator;

public class Lot {

	private int type = 0;
	
	private Presenter presenter;
	
	private int numRegs = 0;
	
	/**
	 * Orderers
	 */
	private ArrayList<Orderer> orderers = new ArrayList<Orderer>();
	
	
	
	/**
	 * @return the presenter
	 */
	public Presenter getPresenter() {
		return presenter;
	}

	/**
	 * @param presenter the presenter to set
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Adds a Orderer
	 * 
	 * @param orderer
	 */
	public void addOrderer(Orderer orderer) {
		this.orderers.add(orderer);
	}

	/**
	 * @return orderers iterator
	 */
	public Iterator<Orderer> getOrderersIterator() {
		return this.orderers.iterator();
	}

	/**
	 * @return the orderers size
	 */
	public Integer getNumOrderers() {
		return orderers.size();
	}


	/**
	 * @return the amount
	 */
	public Double getAmount() {
		Iterator<Orderer> iter = this.getOrderersIterator();
		double total = 0.0;
		while (iter.hasNext()){
			total += iter.next().getAmount().doubleValue();
		}
		return total;
	}

	/**
	 * @return the number of individuals
	 */
	public int getNumIndividuals() {
		Iterator<Orderer> iter = this.getOrderersIterator();
		int total = 0;
		while (iter.hasNext()){
            total += iter.next().getNumIndividuals();
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
	
	public static int RESUMED = 0;
	
	public static int EXTENDED = 1;
	
}
