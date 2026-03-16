package com.esferalia.aon.occam.api.model.invoice;

import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_ADMINISTRATION;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_LROE;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_NO_VERIFACTU;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SIF;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SII;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_TBAI;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_VERIFACTU;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.ExemptType;
import com.esferalia.aon.watson.util.AonUtils;

public class CommunicationData extends EnterpriseData implements Serializable {
	
	private static final long serialVersionUID = 1260028550928443004L;
	
	private Administration administration;
	private boolean test;
	private ExemptType exemptType;
	
	@Override 
	public CommunicationData setId(Integer id) {
		super.setId(id); 
		return this; 
	}
	@Override 
	public CommunicationData setDomain(Integer domain) {
		super.setDomain(domain); 
		return this; 
	}
	@Override 
	public CommunicationData setEnterprise(Integer enterprise) {
		super.setEnterprise(enterprise); 
		return this; 
	}
	@Override 
	public CommunicationData setName(String name) {
		super.setName(name); 
		return this; 
	}
	@Override 
	public CommunicationData setDataName( EnterpriseDataNames name) {
		super.setDataName(name);
		return this;
	}
	@Override 
	public CommunicationData setExpression(String expr) {
		super.setExpression(expr); 
		return this; 
	}
	@Override 
	public CommunicationData setStartDate(Date date) {
		super.setStartDate(date); 
		return this; 
	}
	@Override 
	public CommunicationData setEndDate(Date date) {
		super.setEndDate(date); 
		return this; 
	}		
	@Override 
	public CommunicationData setDeleted(boolean deleted) { 
		super.setDeleted(deleted); 
		return this; 
	}

	public boolean isTest() {
		return test;
	}
	public boolean isNotTest() {
		return !test;
	}
	public CommunicationData setTest(boolean test) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.test, test) );
		this.test = test;
		return this;
	}
	
	public boolean isExempt() {
		return getExemptType().isPresent();
	}
	public Optional<ExemptType> getExemptType() {
		return Optional.ofNullable(exemptType);
	}
	public CommunicationData setExemptType(ExemptType exemptType) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.exemptType, exemptType) );
		this.exemptType = exemptType;
		return this;
	}
	
	public Optional<Administration> getAdministration() {
		return Optional.ofNullable(this.administration);
	}
	public CommunicationData setAdministration(Administration administration) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.administration, administration) );
		this.administration = administration;
		return this;
	}
	
	public Optional<InvoiceCommunicationType> getCommunicationType() {
		return InvoiceCommunicationType.get(getDataName());
	}

	public boolean isAdministration() { return getDataName() == ICC_ADMINISTRATION; }
	public boolean isTBai() { return getDataName() == ICC_TBAI; }
	public boolean isNotTBai() { return getDataName() != ICC_TBAI; }
	public boolean isLroe() { return getDataName() == ICC_LROE; }
	public boolean isNotLroe() { return getDataName() != ICC_LROE; }
	public boolean isSii() { return getDataName() == ICC_SII; }
	public boolean isNotSii() { return getDataName() != ICC_SII; }
	public boolean isVerifactu() { return getDataName() == ICC_VERIFACTU; }
	public boolean isNotVerifactu() { return getDataName() != ICC_VERIFACTU; }
	public boolean isNoVerifactu() { return getDataName() == ICC_NO_VERIFACTU; }
	public boolean isNotNoVerifactu() { return getDataName() != ICC_NO_VERIFACTU; }
	public boolean isSif() { return getDataName() == ICC_SIF; }
	public boolean isNotSif() { return getDataName() != ICC_SIF; }
	public boolean isNoSif() { return getDataName() == ICC_NO_SIF; }
	public boolean isNotNoSif() { return getDataName() != ICC_NO_SIF; }
	
	public boolean isAraba() {return getAdministration().map(a -> a.isAraba()).orElse(false);}
	public boolean isGipuzkoa() {return getAdministration().map(a -> a.isGipuzkoa()).orElse(false);}
	public boolean isBizkaia() {return getAdministration().map(a -> a.isBizkaia()).orElse(false);}
	public boolean isNavarra() {return getAdministration().map(a -> a.isNavarra()).orElse(false);}
	public boolean isAEAT() {return getAdministration().map(a -> a.isAEAT()).orElse(false);}
	public boolean isCanarias() {return getAdministration().map(a -> a.isCanarias()).orElse(false);}
	

}