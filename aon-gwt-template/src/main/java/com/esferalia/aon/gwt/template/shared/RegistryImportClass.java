package com.esferalia.aon.gwt.template.shared;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RegistryImportClass implements IsSerializable {

	private Registry registry;
	private Account account;
	private String iban;
	private String ccc;
	private String bic;
	private String type;
	private Integer line;
	private LinkedList<RegistryMedia> rmediaList;
	private PayMethod paymethod;

	
	public RegistryImportClass() {
		this.registry = new Registry()
			.setMainAddress(new RAddress());
		this.account = new Account();
		this.paymethod = new PayMethod();
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}
	
	public String getCcc( ) {
		return ccc;
	}
	
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public LinkedList<RegistryMedia> getRmediaList() {
		if(rmediaList == null) {
			this.rmediaList = new LinkedList<RegistryMedia>();
		}
		return rmediaList;
	}

	public void setRmediaList(LinkedList<RegistryMedia> rmediaList) {
		this.rmediaList = rmediaList;
	}

	public PayMethod getPaymethod() {
		return paymethod;
	}

	public void setPaymethod(PayMethod paymethod) {
		this.paymethod = paymethod;
	}

	public Boolean isCustomer() {
		return (this.type != null && (this.type.equalsIgnoreCase("C") || this.type.equalsIgnoreCase("CUSTOMER") || this.type.equalsIgnoreCase("CLIENTE")))
				|| (this.account != null && this.account.getCode() != null
					&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("430"));
	}

	public Boolean isSupplier() {
		return (this.type != null && (this.type.equalsIgnoreCase("P") || this.type.equalsIgnoreCase("PROVEEDOR") || this.type.equalsIgnoreCase("SUPPLIER")))
				|| (this.account != null && this.account.getCode() != null
					&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("400"));
	}

	public Boolean isCreditor() {
		return (this.type != null && (this.type.equalsIgnoreCase("A") || this.type.equalsIgnoreCase("ACREEDOR") || this.type.equalsIgnoreCase("CREDITOR")))
				|| (this.account != null && this.account.getCode() != null
					&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("410"));
	}

	public String getAccountPrefix() {
		if(isCustomer()) return "430";
		if(isSupplier()) return "400";
		if(isCreditor()) return "410";
		return null;
	}
}