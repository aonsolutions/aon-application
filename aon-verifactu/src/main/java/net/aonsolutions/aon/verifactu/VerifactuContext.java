package net.aonsolutions.aon.verifactu;

import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class VerifactuContext {
	
	private VerifactuConfiguration config;
	private Company company;
	private List<EnterpriseActivity> activities;
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
	
	public List<EnterpriseActivity> getActivities() {
		return activities;
	}
	public VerifactuContext setActivities(List<EnterpriseActivity> activities) {
		this.activities = activities;
		return this;
	}
	public Optional<EnterpriseActivity> getActivity(Integer activity) {
		return AonCollectionUtils.stream(activities)
			.filter(a -> AonNumberUtils.equals(a.getId(), activity))
			.findAny();
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
