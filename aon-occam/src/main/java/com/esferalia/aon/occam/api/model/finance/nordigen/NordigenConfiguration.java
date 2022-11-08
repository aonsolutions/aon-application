package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;

public class NordigenConfiguration implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private AonConfiguration configuration;
	private NordigenAccessToken token;
	private List<NordigenBankAccount> accounts;
	private List<NordigenInstitution> institutions;
	private List<RegistryAddInfo> raddinfos;

	public AonConfiguration getConfiguration() {
		return configuration;
	}
	public NordigenConfiguration setConfiguration(AonConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public NordigenAccessToken getToken() {
		return token;
	}
	public NordigenConfiguration setToken(NordigenAccessToken token) {
		this.token = token;
		return this;
	}
	public List<NordigenBankAccount> getAccounts() {
		return accounts;
	}
	public NordigenConfiguration setAccounts(List<NordigenBankAccount> accounts) {
		this.accounts = accounts;
		return this;
	}
	public List<NordigenInstitution> getInstitutions() {
		return institutions;
	}
	public NordigenConfiguration setInstitutions(List<NordigenInstitution> institutions) {
		this.institutions = institutions;
		return this;
	}
	
	public List<NordigenBankAccount> getLinkedAccounts() {
		if (accounts != null) {
			return accounts.stream().filter(acc -> acc != null && acc.isLinked()).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	public List<NordigenBankAccount> getUnlinkedAccounts() {
		if (accounts != null) {
			return accounts.stream().filter(acc -> acc != null && !acc.isLinked()).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	public List<RegistryAddInfo> getRaddinfos() {
		return raddinfos;
	}
	public NordigenConfiguration setRaddinfos(List<RegistryAddInfo> raddinfos) {
		this.raddinfos = raddinfos;
		return this;
	}
	
}
