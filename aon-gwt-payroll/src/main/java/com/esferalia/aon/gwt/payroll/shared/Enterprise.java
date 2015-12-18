package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.HasDomain;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.common.shared.HasName;

public class Enterprise implements Serializable, HasId<Integer>, HasName<String>, HasDomain<Integer>{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2430564873016945740L;
	
	private int			id;
	
	private Integer		domain; 
	
	private String 			name;
	private List<Activity>	activities;
	private List<Workplace> workplaces;
	private List<BankAccount> bankAccounts;
	
	public Enterprise() {
		activities = new LinkedList<Activity>();
		workplaces = new LinkedList<Workplace>();
		bankAccounts = new LinkedList<BankAccount>();
	}

	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public List<Workplace> getWorkplaces() {
		return workplaces;
	}
	
	public void addWorkplace(Workplace workplace ) {
		workplaces.add(workplace);
	}
	
	public List<Activity> getActivities() {
		return activities;
	}
	
	
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	
	public void addActivity(Activity activity ) {
		activities.add(activity);
	}
	
	public List<BankAccount> getBankAccounts() {
		return bankAccounts;
	}
	
	public void addBankAccount(BankAccount bankAccount){
		bankAccounts.add(bankAccount);
	}
	
	public void setBankAccounts(List<BankAccount> bankAccounts){
		this.bankAccounts = bankAccounts;
	}
	// ------------------------------------------------------------------------

	public static boolean isGPS(Enterprise enterprise) {
		for (Activity activity : enterprise.getActivities()) {
			Integer cnae2009 = activity.getCnae2009();
			if ( cnae2009 != null  && cnae2009.toString().startsWith("551") )
				return true;
		}
		return false;
	}
	
	
	
}
