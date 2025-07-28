package net.aonsolutions.aon.verifactu;

import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class VerifactuContext<T> {
	
	private VerifactuConfiguration config;
	private Company company;
	private List<EnterpriseActivity> activities;
	private List<Invoice> invoices;
	private VerifactuBlockchain blockchain;
	private String user;
	
	private T request;
	private VerifactuResponse response;
	
	public VerifactuConfiguration getConfig() {
		return config;
	}
	public VerifactuContext<T> setConfig(VerifactuConfiguration config) {
		this.config = config;
		return this;
	}
	
	public Company getCompany() {
		return company;
	}
	public VerifactuContext<T> setCompany(Company company) {
		this.company = company;
		return this;
	}
	public Domain getDomain() {
		return company.getDomain();
	}
	public Occam getOccam() {
		return new Occam()
			.setDomain( getDomain().getId() )
			.setDomainName( getDomain().getName() )
			.setUser(getUser());
	}
	
	
	public List<EnterpriseActivity> getActivities() {
		return activities;
	}
	public VerifactuContext<T> setActivities(List<EnterpriseActivity> activities) {
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
	public VerifactuContext<T> setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
		return this;
	}
	
	public VerifactuBlockchain getBlockchain() {
		return blockchain;
	}
	public VerifactuContext<T> setBlockchain(VerifactuBlockchain blockchain) {
		this.blockchain = blockchain;
		return this;
	}

	public String getUser() {
		return user;
	}
	public VerifactuContext<T> setUser(String user) {
		this.user = user;
		return this;
	}
	
	public T getRequest() {
		return request;
	}
	public VerifactuContext<T> setRequest(T request) {
		this.request = request;
		return this;
	}
	
	public VerifactuResponse getResponse() {
		return response;
	}
	public VerifactuContext<T> setResponse(VerifactuResponse response) {
		this.response = response;
		return this;
	}
	
}
