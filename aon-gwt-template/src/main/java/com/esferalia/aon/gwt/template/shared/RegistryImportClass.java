package com.esferalia.aon.gwt.template.shared;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.google.gwt.user.client.rpc.IsSerializable;

public class RegistryImportClass implements IsSerializable {

	private Registry registry;
	private Account account;
	private String iban;
	private String ccc;
	private String bic;
	private String type;
	private Integer line;
	private List<RegistryMedia> rmediaList;
	private PayMethod paymethod;
	private InvoiceTransactionType transaction;
	
	public RegistryImportClass() {
		this.registry = new Registry()
			.setMainAddress(new RAddress());
		this.account = new Account();
		this.paymethod = new PayMethod();
	}

	public Registry getRegistry() {
		return registry;
	}

	public RegistryImportClass setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}

	public Account getAccount() {
		return account;
	}

	public RegistryImportClass setAccount(Account account) {
		this.account = account;
		return this;
	}

	public String getIban() {
		return iban;
	}

	public RegistryImportClass setIban(String iban) {
		this.iban = iban;
		return this;
	}

	public String getBic() {
		return bic;
	}

	public RegistryImportClass setBic(String bic) {
		this.bic = bic;
		return this;
	}
	
	public String getCcc( ) {
		return ccc;
	}
	
	public RegistryImportClass setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public String getType() {
		return type;
	}

	public RegistryImportClass setType(String type) {
		this.type = type;
		return this;
	}

	public Integer getLine() {
		return line;
	}

	public RegistryImportClass setLine(Integer line) {
		this.line = line;
		return this;
	}

	public List<RegistryMedia> getRmediaList() {
		if(rmediaList == null) {
			this.rmediaList = new LinkedList<>();
		}
		return rmediaList;
	}

	public RegistryImportClass setRmediaList(List<RegistryMedia> rmediaList) {
		this.rmediaList = rmediaList;
		return this;
	}

	public PayMethod getPaymethod() {
		return paymethod;
	}

	public RegistryImportClass setPaymethod(PayMethod paymethod) {
		this.paymethod = paymethod;
		return this;
	}
	
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	
	public RegistryImportClass setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}

	public boolean isCustomer() {
		return (this.type != null && (this.type.equalsIgnoreCase("C") || this.type.equalsIgnoreCase("CUSTOMER") || this.type.equalsIgnoreCase("CLIENTE")))
				|| (this.account != null && this.account.getCode() != null
					&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("430"));
	}

	public boolean isSupplier() {
		return (this.type != null && (this.type.equalsIgnoreCase("P") || this.type.equalsIgnoreCase("PROVEEDOR") || this.type.equalsIgnoreCase("SUPPLIER")))
				|| (this.account != null && this.account.getCode() != null
					&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("400"));
	}

	public boolean isCreditor() {
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