package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.ui.ListBox;

public class AccountPeriodBox extends ListBox {
	
	private LinkedList<AccountPeriod> periods;
	
	public AccountPeriodBox() {
		setWidth("60px");
		periods = new LinkedList<AccountPeriod>();
		periods.add(new AccountPeriod());
		AccountPeriodBox.this.addItem( "----","-1" );
	}
	public void fill(LinkedList<AccountPeriod> result) {
		fill(result,false);	
	}
	
	public void fill(LinkedList<AccountPeriod> result,boolean enableAllPeriods) {
		int i = 0;
		for (AccountPeriod p : result) {
			AccountPeriodBox.this.addItem( p.getName() + (p.isDefaultPeriod()?"*":""), AonNumberUtils.toString(p.getId()) );
			i++;
			if ( !enableAllPeriods 
			 && (p.getStatus() == AccountPeriodStatus.CLOSED 
			 || p.getStatus() == AccountPeriodStatus.INACTIVE
			 || p.getStatus() == AccountPeriodStatus.OPERATING)) {
				getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled", "disabled");	
			} else {
				if (p.isDefaultPeriod()) {
					setSelectedIndex(i);
				}
			}
			periods.add(p);
		}
	}
	
	public void selectDefaultPeriod() {
		setSelectedIndex(0);
		for (int i = 0; i < periods.size(); i++) {
			if (periods.get(i).isDefaultPeriod()) {
				setSelectedIndex(i);
			}
		}
	}
	
	public Date getSelectedInitiationDate() {
		if (periods != null 
			&& periods.size() > 0 
			&& getSelectedIndex() < periods.size()) {
			AccountPeriod ap = periods.get(getSelectedIndex());
			if (ap != null ) return ap.getInitiationDate(); 
		}
		return null;
	}
	
	public Date getSelectedDeadline() {
		if (periods != null 
			&& periods.size() > 0 
			&& getSelectedIndex() < periods.size()) {
			AccountPeriod ap = periods.get(getSelectedIndex());
			if (ap != null ) return ap.getDeadline(); 
		}
		return null;
	}

	public boolean isOutOfRange(Date date) {
		if (date == null) return true;
		AccountPeriod ap = periods.get(getSelectedIndex());
		if (ap == null || ap.getId() == null) return true;
		return (
				ap.getInitiationDate().after(date)
				|| ap.getDeadline().before(date)
				);
	}

	public void select(Integer accountPeriod) {
		if (accountPeriod != null) {
			boolean found = false;
			for (int i = 0 ;  i < getItemCount(); i++) {
				int a = AonNumberUtils.toInteger( getValue(i) );
				int b = accountPeriod;
				if ( a == b) {
					setSelectedIndex(i);
					found = true;
					break;
				}
			}
			if (!found) {
				for (int i = 0 ;  i < getItemCount(); i++) {
					if ( periods.get(i).isDefaultPeriod()) {
						setSelectedIndex(i);
						break;
					}				
				}
			}
		} else {
			setSelectedIndex(0);			
		}
	}

	public Integer getValue() {
		if (getSelectedIndex() == 0) return null;
		return AonNumberUtils.toInteger( getSelectedValue() );
	}
}
