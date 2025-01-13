package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;

public class Configuration implements Serializable {
	
	private static final long serialVersionUID = -7310540483421917688L;
	
	private Integer domainId;
	private Domain domain;
	private User user;
	private CompanyFull company;
	private boolean defaultScopeResolved;
	private Scope defaultScope;
	private boolean defaultAccountPeriodResolved;
	private AccountPeriod defaultAccountPeriod;
	private Integer[] userScopes;
	private boolean activitiesResolved;
	private LinkedList<Activity> activities;
	private boolean investAssetsResolved;
	private LinkedList<InvestAsset> investAssets;
	private LinkedList<Workplace> workplaces;
	private LinkedList<AccountPeriod> periods;
	private LinkedList<String> autoConcepts;
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
	
	public Optional<AccountPeriod> getDefaultAccountPeriod() {
		return Optional.ofNullable(defaultAccountPeriod);
	}
	public Configuration setDefaultAccountPeriod(AccountPeriod defaultAccountPeriod) {
		this.defaultAccountPeriod = defaultAccountPeriod;
		return this;
	}

	public boolean isDefaultAccountPeriodResolved() {
		return defaultAccountPeriodResolved;
	}
	public Configuration setDefaultAccountPeriodResolved(boolean defaultAccountPeriodResolved) {
		this.defaultAccountPeriodResolved = defaultAccountPeriodResolved;
		return this;
	}
	
	public Optional<Integer[]> getUserScopes() {
		return Optional.ofNullable(userScopes);
	}
	public Configuration setUserScopes(Integer[] userScopes) {
		this.userScopes = userScopes;
		return this;
	}
	
	public Stream<Activity> activityStream() {
		return AonCollectionUtils.stream(activities);
	}
	public boolean isActivitiesResolved() {
		return activitiesResolved;
	}
	public void setActivitiesResolved(boolean activitiesResolved) {
		this.activitiesResolved = activitiesResolved;
	}
	public boolean hasActivities() {
		return activityStream().count() > 0; 
	}
	public Activity addActivity(Activity activity) {
		if (this.activities == null) this.activities = new LinkedList<>(); 
		this.activities.add(activity);
		return activity;
	}
	
	public Stream<InvestAsset> investAssetStream() {
		return AonCollectionUtils.stream(investAssets);
	}
	public boolean isInvestAssetsResolved() {
		return this.investAssetsResolved; 
	}
	public void setInvestAssetsResolved(boolean investAssetsResolved) {
		this.investAssetsResolved = investAssetsResolved;
	}
	public InvestAsset addInvestAsset(InvestAsset investAsset) {
		if (this.investAssets == null) this.investAssets = new LinkedList<>(); 
		this.investAssets.add( investAsset );
		return investAsset;
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
	public Stream<Workplace> workplacesStream() {
		return AonCollectionUtils.stream(workplaces);
	}
	public boolean isWorkplacesResolved() {
		return (this.workplaces != null); 
	}
	public Workplace addWorkplace(Workplace workplace) {
		if (this.workplaces == null) this.workplaces = new LinkedList<>(); 
		this.workplaces.add(workplace);
		return workplace;
	}
	
	public Stream<AccountPeriod> periodsStream() {
		return AonCollectionUtils.stream(periods);
	}
	public boolean isPeriodsResolved() {
		return (this.periods != null); 
	}
	public AccountPeriod addPeriod(AccountPeriod period) {
		if (this.periods == null) this.periods = new LinkedList<>(); 
		this.periods.add(period);
		return period;
	}

	public Stream<String> autoConceptsStream() {
		return AonCollectionUtils.stream(autoConcepts);
	}
	public boolean isAutoConceptsResolved() {
		return (this.autoConcepts != null); 
	}
	public String addAutoConcept(String autoConcept) {
		if (this.autoConcepts == null) this.autoConcepts= new LinkedList<>();
		this.autoConcepts.add(autoConcept);
		return autoConcept;
	}

	public Optional<TbaiConfiguration> getTbaiConfiguration() {
		return Optional.ofNullable(tbaiConfiguration);
	}
	public Configuration setTbaiConfiguration(TbaiConfiguration tbaiConfiguration) {
		this.tbaiConfiguration = tbaiConfiguration;
		return this;
	}
	
	
	/* TODO Implement! */ public boolean hasGuestRole() 			{return false;}
	/* TODO Implement! */ public boolean isAccountingGuest() 		{return false;}
	/* TODO Implement! */ public boolean hasConfidentialityRole() 	{return true;}
	/* TODO Implement! */ public boolean hasAccountingRole() 		{return true;}
	
	
}
