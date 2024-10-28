package net.aonsolutions.occam.api.model;

import java.util.List;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;

public class Configuration {
	
	private Integer domainId;
	private Domain domain;
	private User user;
	private CompanyFull company;
	private boolean defaultScopeResolved;
	private Scope defaultScope;
	private Integer[] userScopes;
	private List<Activity> activities;
	private List<InvestAsset> investAssets;
	private List<Workplace> workplaces;
	private TbaiConfiguration tbaiConfiguration;
	private ApplicationParameters appParams;	
	
	public Integer getDomainId() {
		if (domainId == null) 
			throw new IllegalStateException("No Domain, no configuration :(");
		return domainId;
	}
	public Configuration setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}
	
	public Optional<Domain> getDomain() {
		return Optional.ofNullable(domain);
	}
	public Configuration setDomain(Domain domain) {
		this.domain = domain; 
		return this;
	}
	
	public Optional<User> getUser() {
		return Optional.ofNullable(user);
	}
	public Configuration setUser(User user) {
		this.user = user;
		return this;
	}

	public Optional<Scope> getDefaultScope() {
		return Optional.ofNullable(defaultScope);
	}
	public Configuration setDefaultScope(Scope defaultScope) {
		this.defaultScope = defaultScope;
		return this;
	}
	
	public boolean isDefaultScopeResolved() {
		return defaultScopeResolved;
	}
	public Configuration setDefaultScopeResolved(boolean defaultScopeResolved) {
		this.defaultScopeResolved = defaultScopeResolved;
		return this;
	}
	
	public Optional<Integer[]> getUserScopes() {
		return Optional.ofNullable(userScopes);
	}
	public Configuration setUserScopes(Integer[] userScopes) {
		this.userScopes = userScopes;
		return this;
	}
	
	public Optional<List<Activity>> getActivities() {
		return Optional.ofNullable(activities);
	}
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	
	public Optional<List<InvestAsset>> getInvestAssets() {
		return Optional.ofNullable(investAssets);
	}
	public void setInvestAssets(List<InvestAsset> investAssets) {
		this.investAssets = investAssets;
	}
	
	public Optional<ApplicationParameters> getApplicationParameters() {
		return Optional.ofNullable(appParams);
	}
	public void setApplicationParameters(ApplicationParameters appParams) {
		this.appParams = appParams;
	}
	
	public Optional<CompanyFull> getCompany() {
		return Optional.ofNullable(company);
	}
	public Configuration setCompany(CompanyFull company) {
		this.company = company;
		return this;
	}
	public Optional<Workplace> getDefaultWorkplace() {
		return AonCollectionUtils.size(workplaces) == 1
			? Optional.of(workplaces.get(0))
			: Optional.empty();
	}
	public Optional<List<Workplace>> getWorkplaces() {
		return Optional.ofNullable(workplaces);
	}
	public void setWorkplaces(List<Workplace> workplaces) {
		this.workplaces = workplaces;
	}
	public Optional<TbaiConfiguration> getTbaiConfiguration() {
		return Optional.ofNullable(tbaiConfiguration);
	}
	public Configuration setTbaiConfiguration(TbaiConfiguration tbaiConfiguration) {
		this.tbaiConfiguration = tbaiConfiguration;
		return this;
	}
	
	
}
