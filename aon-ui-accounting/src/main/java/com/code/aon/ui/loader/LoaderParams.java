package com.code.aon.ui.loader;

import java.lang.ref.WeakReference;

import com.code.aon.accounting.Period;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.product.ProductCategory;

public class LoaderParams {
	
	private Scope scope;
	private SecurityLevel securityLevel;
	private WorkPlace workPlace;
	private ProductCategory category;
	private WeakReference<LoaderUtils> loaderUtils;
	private Period accountPeriod;
	
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	public ProductCategory getCategory() {
		return category;
	}
	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public LoaderUtils getLoaderUtils() {
		if (loaderUtils == null) {
			loaderUtils = new WeakReference<LoaderUtils>(new LoaderUtils( this ));	
		}
		return loaderUtils.get();
	}
	public Period getAccountPeriod() {
		return accountPeriod;
	}
	public void setAccountPeriod(Period accountPeriod) {
		this.accountPeriod = accountPeriod;
	}
	
}
