package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class MarketingCompaignParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	private Integer scope;
	private Byte active;
	
	private Double budget;
	private boolean betweenBudgetNumbers = false;
	private Double gtbudget;
	private Double ltbudget;
	
	private Double expense;
	private boolean betweenExpenseNumbers = false;
	private Double gtexpense;
	private Double ltexpense;
	
	private int limit;
	private int offset;

	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public MarketingCompaignParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public MarketingCompaignParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public MarketingCompaignParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public MarketingCompaignParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public MarketingCompaignParams setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Byte getActive() {
		return active;
	}
	public MarketingCompaignParams setActive(Byte active) {
		this.active = active;
		return this;
	}
	public Double getBudget() {
		return budget;
	}
	public MarketingCompaignParams setBudget(Double budget) {
		this.budget = budget;
		return this;
	}
	public boolean isBetweenBudgetNumbers() {
		return betweenBudgetNumbers;
	}
	public MarketingCompaignParams setBetweenBudgetNumbers(boolean betweenBudgetNumbers) {
		this.betweenBudgetNumbers = betweenBudgetNumbers;
		return this;
	}
	public Double getGTBudget() {
		return gtbudget;
	}
	public MarketingCompaignParams setGTBudget(Double gtbudget) {
		this.gtbudget = gtbudget;
		return this;
	}
	public Double getLTBudget() {
		return ltbudget;
	}
	public MarketingCompaignParams setLTBudget(Double ltbudget) {
		this.ltbudget = ltbudget;
		return this;
	}
	public boolean isBetweenExpenseNumbers() {
		return betweenExpenseNumbers;
	}
	public MarketingCompaignParams setBetweenExpenseNumbers(boolean betweenExpensetNumbers) {
		this.betweenExpenseNumbers = betweenExpensetNumbers;
		return this;
	}
	public Double getGTExpense() {
		return gtexpense;
	}
	public MarketingCompaignParams setGTExpense(Double gtexpense) {
		this.gtexpense = gtexpense;
		return this;
	}
	public Double getLTExpense() {
		return ltexpense;
	}
	public MarketingCompaignParams setLTExpense(Double ltexpense) {
		this.ltexpense = ltexpense;
		return this;
	}
	public Double getExpense() {
		return expense;
	}
	public MarketingCompaignParams setExpense(Double expense) {
		this.expense = expense;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public MarketingCompaignParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public MarketingCompaignParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public MarketingCompaignParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public MarketingCompaignParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
	
	
}
