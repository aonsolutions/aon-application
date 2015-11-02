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
		AccountPeriodBox.this.addItem( "----","0" );
		setSelectedIndex(0);
	}
	
	public void fill(LinkedList<AccountPeriod> result) {
		int i = 0;
		for (AccountPeriod p : result) {
			AccountPeriodBox.this.addItem( p.getName(), AonNumberUtils.toString(p.getId()) );
			i++;
			if (p.getStatus() == AccountPeriodStatus.CLOSED 
			 || p.getStatus() == AccountPeriodStatus.INACTIVE
			 || p.getStatus() == AccountPeriodStatus.OPERATING) {
				getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled", "disabled");	
			}
			periods.add(p);
		}
	}
	
	public boolean isOutOfRange(Date date) {
		AccountPeriod ap = periods.get(getSelectedIndex());
		return (ap.getId() != null &&  
			(ap.getInitiationDate().after(date) 
		  || ap.getDeadline().before(date)));
	}

	public void select(Integer accountPeriod) {
		if (accountPeriod != null) {
			for (int i = 0 ;  i < getItemCount(); i++) {
				int a = AonNumberUtils.toInteger( getValue(i) );
				int b = accountPeriod;
				if ( a == b) {
					setSelectedIndex(i);
					break;
				}
			}
		}
	}
}
