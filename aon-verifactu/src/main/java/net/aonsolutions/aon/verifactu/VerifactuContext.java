package net.aonsolutions.aon.verifactu;

import java.util.List;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;

public class VerifactuContext {
	
	private VerifactuConfiguration config;
	private Company company;
	private List<Invoice> invoices;
	private VerifactuBlockchain blockchain;
	
	public VerifactuConfiguration getConfig() {
		return config;
	}
	public VerifactuContext setConfig(VerifactuConfiguration config) {
		this.config = config;
		return this;
	}
	
	public Company getCompany() {
		return company;
	}
	public VerifactuContext setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public List<Invoice> getInvoices() {
		return invoices;
	}
	public VerifactuContext setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
		return this;
	}
	
	public VerifactuBlockchain getBlockchain() {
		return blockchain;
	}
	public VerifactuContext setBlockchain(VerifactuBlockchain blockchain) {
		this.blockchain = blockchain;
		return this;
	}
	
	
};
