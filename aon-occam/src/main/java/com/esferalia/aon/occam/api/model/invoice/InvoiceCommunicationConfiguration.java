package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceCommunicationConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;

	private Administration administration;

	// TBAI
	private boolean tbai;
	private boolean tbaiTest;
	private Date tbaiIncludeDate;
	private String tbaiRegistryDate;
	
	// SII
	private boolean sii;
	private boolean siiTest;
	private Date siiIncludeDate;
	private String siiRegistryDate;
	private boolean prepareNewSii;
	private boolean siiAutosend;
	
	// VERIFACTU
	private boolean verifactu;
	private boolean verifactuTest;
	private Date verifactuIncludeDate;
	private String verifactuRegistryDate;
	
	private Integer defaultCertificate;
	private Certificate certificate;

	


	
	// TO FIX || DELETE
	private boolean skipTracking;

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
	
	public boolean isTbaiTest() {
		return tbaiTest;
	}
	public InvoiceCommunicationConfiguration setTbaiTest(boolean tbaiTest) {
		this.tbaiTest = tbaiTest;
		return this;
	}
	
	public boolean hasVerifactu() {
		return isVerifactu() && getVerifactuIncludeDate() != null && getVerifactuIncludeDate().before(new Date());
	}
	
	public boolean isVerifactu() {
		return verifactu;
	}
	
	public InvoiceCommunicationConfiguration setVerifactu(boolean verifactu) {
		this.verifactu = verifactu;
		return this;
	}
	
	public boolean isVerifactuTest() {
		return verifactuTest;
	}
	
	public InvoiceCommunicationConfiguration setVerifactuTest(boolean verifactuTest) {
		this.verifactuTest = verifactuTest;
		return this;
	}
	
	public boolean isSii() {
		return sii;
	}
	public InvoiceCommunicationConfiguration setSii(boolean sii) {
		this.sii = sii;
		return this;
	}
	public boolean isSiiTest() {
		return siiTest;
	}
	public InvoiceCommunicationConfiguration setSiiTest(boolean siiTest) {
		this.siiTest = siiTest;
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

	public Date getTbaiIncludeDate() {
		return tbaiIncludeDate;
	}
	public InvoiceCommunicationConfiguration setTbaiIncludeDate(Date tbaiIncludeDate) {
		this.tbaiIncludeDate = tbaiIncludeDate;
		return this;
	}
	
	public String getTbaiRegistryDate() {
		return tbaiRegistryDate;
	}
	public InvoiceCommunicationConfiguration setTbaiRegistryDate(String tbaiRegistryDate) {
		this.tbaiRegistryDate = tbaiRegistryDate;
		return this;
	}
	
	public Date getSiiIncludeDate() {
		return siiIncludeDate;
	}
	public InvoiceCommunicationConfiguration setSiiIncludeDate(Date siiIncludeDate) {
		this.siiIncludeDate = siiIncludeDate;
		return this;
	}
	
	public String getSiiRegistryDate() {
		return siiRegistryDate;
	}
	public InvoiceCommunicationConfiguration setSiiRegistryDate(String siiRegistryDate) {
		this.siiRegistryDate = siiRegistryDate;
		return this;
	}
	
	public Date getVerifactuIncludeDate() {
		return verifactuIncludeDate;
	}
	
	public InvoiceCommunicationConfiguration setVerifactuIncludeDate(Date verifactuIncludeDate) {
		this.verifactuIncludeDate = verifactuIncludeDate;
		return this;
	}
	
	public String getVerifactuRegistryDate() {
		return verifactuRegistryDate;
	}
	
	public InvoiceCommunicationConfiguration setVerifactuRegistryDate(String verifactuRegistryDate) {
		this.verifactuRegistryDate = verifactuRegistryDate;
		return this;
	}
	
	public boolean isRegistryTaxDate() {
		return "tax".equalsIgnoreCase(getSiiRegistryDate());
	}
	
	public boolean isSkipTracking() {
		return skipTracking;
	}
	public InvoiceCommunicationConfiguration setSkipTracking(boolean skipTracking) {
		this.skipTracking = skipTracking;
		return this;
	}
	
	public boolean isSiiAutosend() {
		return siiAutosend;
	}
	public InvoiceCommunicationConfiguration setSiiAutosend(boolean siiAutosend) {
		this.siiAutosend = siiAutosend;
		return this;
	}
	
	public boolean isPrepareNewSii() {
		return prepareNewSii;
	}
	public InvoiceCommunicationConfiguration setPrepareNewSii(boolean prepareNewSii) {
		this.prepareNewSii = prepareNewSii;
		return this;
	}

	public List<InvoiceCommunicationType> getTypes() {
		LinkedList<InvoiceCommunicationType> types = new LinkedList<>(); 
		if(isTbai() && (getAdministration().isAraba() || getAdministration().isGipuzkoa())) {
			types.add(InvoiceCommunicationType.TBAI);
		} else if(isTbai() && getAdministration().isBizkaia()) {
			types.add(InvoiceCommunicationType.LROE);
		} else if(hasVerifactu() && (getAdministration().isAEAT() || getAdministration().isCanarias() || getAdministration().isUnknown())) {
			types.add(InvoiceCommunicationType.VERIFACTU);
		} else if(isSii() && (getAdministration().isAEAT() || getAdministration().isCanarias() || getAdministration().isUnknown())) {
			types.add(InvoiceCommunicationType.SII);
		}
		return types;
	}
	
	public boolean hasCommunication() {
		return AonCollectionUtils.isNotEmpty(getTypes());
	}

	public boolean isBizkaia() 	{return administration == Administration.BIZKAIA;}
	public boolean isAraba() 	{return administration == Administration.ALAVA;}
	public boolean isGipuzkoa() {return administration == Administration.GIPUZKOA;}
	public boolean isNavarra() 	{return administration == Administration.NAVARRA;}
	public boolean isAEAT() 	{return administration == Administration.COMMON_TERRITORY;}
	public boolean isCanarias() {return administration == Administration.CANARIAS;}

}
