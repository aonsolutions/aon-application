package com.esferalia.aon.occam.api.model.accounting.utilities;

import java.io.Serializable;

public class AccUtilitiesParams implements Serializable {

	private static final long serialVersionUID = 7026079399878678313L;
	
	private String query;

	private boolean showCustomers;
	private boolean showCreditors;
	private boolean showSuppliers;
	
	private boolean showInactives;
	private boolean showWihtoutAccount;
	private boolean showSynchronizables;
	
	public String getQuery() {
		return query;
	}
	public AccUtilitiesParams setQuery(String query) {
		this.query = query;
		return this;
	}
	public boolean isShowCustomers() {
		return showCustomers;
	}
	public AccUtilitiesParams setShowCustomers(boolean showCustomers) {
		this.showCustomers = showCustomers;
		return this;
	}
	public boolean isShowCreditors() {
		return showCreditors;
	}
	public AccUtilitiesParams setShowCreditors(boolean showCreditors) {
		this.showCreditors = showCreditors;
		return this;
	}
	public boolean isShowSuppliers() {
		return showSuppliers;
	}
	public AccUtilitiesParams setShowSuppliers(boolean showSuppliers) {
		this.showSuppliers = showSuppliers;
		return this;
	}
	
	public boolean isShowInactives() {
		return showInactives;
	}
	public AccUtilitiesParams setShowInactives(boolean showInactives) {
		this.showInactives = showInactives;
		return this;
	}
	public boolean isShowWihtoutAccount() {
		return showWihtoutAccount;
	}
	public AccUtilitiesParams setShowWihtoutAccount(boolean showWihtoutAccount) {
		this.showWihtoutAccount = showWihtoutAccount;
		return this;
	}
	public boolean isShowSynchronizables() {
		return showSynchronizables;
	}
	public AccUtilitiesParams setShowSynchronizables(boolean showSynchronizables) {
		this.showSynchronizables = showSynchronizables;
		return this;
	}
	

}

