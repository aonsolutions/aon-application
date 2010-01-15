package com.code.aon.common.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean Configuration.
 * 
 * @author Consulting & Development. ecastellano - 12-dic-2005
 * 
 */
public class BeanConfig {

	private String pojo;
	
	private Class pojoClass;
	
	private String daoFactory;

	private String daoMethod;

	private List<String> listeners;

	private List<String> vetoListeners;

	/**
	 * Listener registration method.
	 * 
	 * @param listenerClass
	 */
	public void addListener(String listenerClass) {
		if (listeners == null) {
			listeners = new ArrayList<String>();
		}
		listeners.add(listenerClass);
	}

	/**
	 * Veto Listener registration method.
	 * 
	 * @param vetoListenerClass
	 */
	public void addVetoListener(String vetoListenerClass) {
		if (vetoListeners == null) {
			vetoListeners = new ArrayList<String>();
		}
		vetoListeners.add(vetoListenerClass);
	}

	/**
	 * Return the listeners.
	 * 
	 * @return The list of listeners.
	 */
	public List<String> getListeners() {
		return listeners;
	}

	/**
	 * Return the veto listeners.
	 * 
	 * @return The list of the veto listeners.
	 */
	public List<String> getVetoListeners() {
		return vetoListeners;
	}

	/**
	 * Return the DAO factory.
	 * 
	 * @return The DAO factory.
	 */
	public String getDaoFactory() {
		return daoFactory;
	}

	/**
	 * Assign the DAO factory.
	 * 
	 * @param daoFactory
	 */
	public void setDaoFactory(String daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * Return the DAO method.
	 * 
	 * @return The DAO method.
	 */
	public String getDaoMethod() {
		return daoMethod;
	}

	/**
	 * Assign the DAO factory.
	 * 
	 * @param daoMethod
	 */
	public void setDaoMethod(String daoMethod) {
		this.daoMethod = daoMethod;
	}

	/**
	 * Return POJO Class name.
	 * 
	 * @return The POJO Class name.
	 */
	public String getPojo() {
		return pojo;
	}

	/**
	 * Return the POJO Class.
	 * 
	 * @return The POJO Class.
	 */
	public Class getPojoClass() {
		return this.pojoClass;
	}

	/**
	 * Assign the POJO Class.
	 * 
	 * @param pojoClass
	 */
	public void setPojoClass(Class pojoClass) {
		this.pojoClass = pojoClass;
		this.pojo = this.pojoClass.getName();
	}

	/**
	 * Assign the POJO Class name.
	 * 
	 * @param pojo
	 * @throws ClassNotFoundException
	 */
	public void setPojo(String pojo) throws ClassNotFoundException {
		this.pojo = pojo;
		this.pojoClass = Class.forName( pojo );		
	}

}
