package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.type.Administration;

public class InvoiceCommunicationConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;

	private Administration administration;
	private boolean tbai;
	private boolean sii;
	private boolean verifactu;
	private boolean test;
	private Integer defaultCertificate;
	private Certificate certificate;
	private Date includeDate;
	private String registryDate;

	public Administration getAdministration() {
		return administration;
	}
	
	public InvoiceCommunicationConfiguration setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	public boolean isTbai() {
		return tbai;
	}
	public InvoiceCommunicationConfiguration setTbai(boolean tbai) {
		this.tbai = tbai;
		return this;
	}
	
	public boolean isVerifactu() {
		return verifactu;
	}
	public InvoiceCommunicationConfiguration setVerifactu(boolean verifactu) {
		this.verifactu = verifactu;
		return this;
	}
	
	public boolean isSii() {
		return sii;
	}
	public InvoiceCommunicationConfiguration setSii(boolean sii) {
		this.sii = sii;
		return this;
	}
	
	public boolean isTest() {
		return test;
	}
	public InvoiceCommunicationConfiguration setTest(boolean test) {
		this.test = test;
		return this;
	}
	
	public Integer getDefaultCertificate() {
		return defaultCertificate;
	}
	public InvoiceCommunicationConfiguration setDefaultCertificate(Integer defaultCertificate) {
		this.defaultCertificate = defaultCertificate;
		return this;
	}
	
	public Certificate getCertificate() {
		return certificate;
	}
	public InvoiceCommunicationConfiguration setCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}
	
	public Date getIncludeDate() {
		return includeDate;
	}
	public InvoiceCommunicationConfiguration setIncludeDate(Date includeDate) {
		this.includeDate = includeDate;
		return this;
	}
	
	public String getRegistryDate() {
		return registryDate;
	}
	public InvoiceCommunicationConfiguration setRegistryDate(String registryDate) {
		this.registryDate = registryDate;
		return this;
	}
	
	public boolean isRegistryTaxDate() {
		return "tax".equalsIgnoreCase(getRegistryDate());
	}
	
	public InvoiceCommunicationType getType() {
		if(isTbai() && (getAdministration().isAraba() || getAdministration().isGipuzkoa())) {
			return InvoiceCommunicationType.TBAI;
		} else if(isTbai() && getAdministration().isBizkaia()) {
			return InvoiceCommunicationType.LROE;
		} else if(isVerifactu() && (getAdministration().isAEAT() || getAdministration().isCanarias() || getAdministration().isUnknown())) {
			return InvoiceCommunicationType.VERIFACTU;
		} else if(isSii() && (getAdministration().isAEAT() || getAdministration().isCanarias() || getAdministration().isUnknown())) {
			return InvoiceCommunicationType.SII;
		}
		return null;
	}
	
	public boolean hasCommunication() {
		return getType() != null;
	}

	public boolean isBizkaia() 	{return administration == Administration.BIZKAIA;}
	public boolean isAraba() 	{return administration == Administration.ALAVA;}
	public boolean isGipuzkoa() {return administration == Administration.GIPUZKOA;}
	public boolean isNavarra() 	{return administration == Administration.NAVARRA;}
	public boolean isAEAT() 	{return administration == Administration.COMMON_TERRITORY;}
	public boolean isCanarias() {return administration == Administration.CANARIAS;}

}
