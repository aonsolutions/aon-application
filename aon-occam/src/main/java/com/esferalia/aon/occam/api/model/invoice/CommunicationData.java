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
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CommunicationData extends EnterpriseData implements Serializable {

	public enum ExemptType {
		NO_SOFTWARE		("No utiliza sistema inform\u00E1tico de facturaci\u00F3n"),
		NO_OBLIGATION	("Operaciones sin obligaci\u00F3n de emitir factura"),
	    REAGP			("REAGP sin emisi\u00F3n de factura propia"),
	    AUTHORIZATION	("Exenci\u00F3n autorizada"),
	    ;

	    private final String description;

	    private ExemptType(String description) {
	        this.description = description;
	    }

	    public String getDescription() {
	        return description;
	    }

		public static Optional<ExemptType> safeValueOf(String cause) {
			if (AonStringUtils.isBlank(cause)) return Optional.empty();
			String c = AonStringUtils.trimToNull(cause);
			return AonCollectionUtils.stream(values())
				.filter(et -> AonStringUtils.equalsIgnoreCase(et.name(), c))
				.findFirst();
		}
	}

	private static final long serialVersionUID = 1260028550928443004L;
	
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
		this.test = test;
		return this;
	}
	
	public boolean isExempt() {
		return getExemptType() != null;
	}
	public ExemptType getExemptType() {
		return exemptType;
	}
	public CommunicationData setExemptType(ExemptType exemptType) {
		this.exemptType = exemptType;
		return this;
	}
	
	public Optional<Administration> getAdministration() {
		if (!isAdministration()) return Optional.empty();
		return Optional.ofNullable(Administration.safeValueOf(getExpression()));
	}
	public CommunicationData setAdministration(Administration admon) {
		if (!isAdministration()) throw new AonCoreException("Only administration data can be set with this method");
		setExpression(admon == null ? null : admon.name());
		return this;
	}
	
	public Optional<InvoiceCommunicationType> getCommunicationType() {
		return InvoiceCommunicationType.get(getDataName());
	}

	public boolean isAdministration() { return getDataName() == ICC_ADMINISTRATION; }
	public boolean isTBai() { return getDataName() == ICC_TBAI; }
	public boolean isLroe() { return getDataName() == ICC_LROE; }
	public boolean isSii() { return getDataName() == ICC_SII; }
	public boolean isVerifactu() { return getDataName() == ICC_VERIFACTU; }
	public boolean isNoVerifactu() { return getDataName() == ICC_NO_VERIFACTU; }
	public boolean isSif() { return getDataName() == ICC_SIF; }
	public boolean isNoSif() { return getDataName() == ICC_NO_SIF; }

}
