package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceCommunicationConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;

	// ADMINISTRATION
	
	/**
	 * Listado hist�rico de administraciones.
	 */
	private List<EnterpriseData>  administrationHistory;
	
	/**
	 * Administraci�n actual.
	 */
	private Administration administration;

	// TBAI
	
	/**
	 * Listado hist�rico de configuraciones de TICKET BAI.
	 */
	private List<EnterpriseData> tbaiDataHistory;
	
	/**
	 * Estado actual de la configuraci�n de TICKET BAI.
	 */
	private EnterpriseData tbaiData;
	
	// LROE
	
	/**
	 * Listado hist�rico de configuraciones de LROE.
	 */
	private List<EnterpriseData> lroeDataHistory;
	
	/**
	 * Estado actual de la configuraci�n de LROE.
	 */
	private EnterpriseData lroeData;

	@Deprecated	private String lroeRegistryDate;
	
	// SII
	
	/**
	 * Listado hist�rico de configuraciones de SII.
	 */
	private List<EnterpriseData> siiDataHistory;
	
	/**
	 * Estado actual de la configuraci�n de SII.
	 */
	private EnterpriseData siiData;

	@Deprecated	private String siiRegistryDate;
	@Deprecated	private boolean prepareNewSii;
	
	// VERIFACTU

	/**
	 * Listado hist�rico de configuraciones de Verifactu.
	 */
	private List<EnterpriseData> verifactuDataHistory;

	/**
	 * Estado actual de la configuraci�n de Verifactu.
	 */
	private EnterpriseData verifactuData;
	
	// NO VERIFACTU

	/**
	 * Listado hist�rico de configuraciones de No Verifactu.
	 */
	private List<EnterpriseData> noVerifactuDataHistory;
	
	/**
	 * Estado actual de la configuraci�n de No Verifactu.
	 */
	private EnterpriseData noVerifactuData;
	
	// SIF || LEY ANTIFRAUDE
	
	/**
	 * Listado hist�rico de configuraciones de SIF (Sistema Inform�tico de Facturaci�n).
	 */
	private List<EnterpriseData> sifDataHistory; 

	/**
	 * Estado actual de la configuraci�n de SIF (Sistema Inform�tico de Facturaci�n).
	 */
	private EnterpriseData sifData;
	
	// NO SIF
	
	/**
	 * Listado hist�rico de configuraciones de SIF (Sistema Inform�tico de Facturaci�n).
	 */
	private List<EnterpriseData> noSifDataHistory; 

	/**
	 * Estado actual de la configuraci�n de SIF (Sistema Inform�tico de Facturaci�n).
	 */
	private EnterpriseData noSifData;
	
	// CERTIFICADO
	
	/**
	 * Identificador del certificado por defecto para comunicaciones de facturas.
	 */
	private Integer defaultCertificate;
	
	/**
	 * Certificado para la comunicaci�n de facturas.
	 */
	private Certificate certificate;
	
	/**
	 * Obtiene el historial de administraciones.
	 * @return Historial de administraciones.
	 */
	public List<EnterpriseData> getAdministrationHistory() {
		return administrationHistory;
	}
	
	/**
	 * Establece el historial de administraciones.
	 * @param administrationHistory Historial de administraciones.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setAdministrationHistory(List<EnterpriseData> administrationHistory) {
		this.administrationHistory = administrationHistory;
		return this;
	}
	
	/**
	 * Obtiene la administraci�n actual.
	 * @return Administraci�n actual.
	 */
	public Administration getAdministration() {
		return administration;
	}
	
	/**
	 * Establece la administraci�n actual.
	 * @param administration Administraci�n actual.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	// COMUNICATION FUNCTIONS
	
	private boolean is(EnterpriseData data) {
		return data != null && data.getId() != null && (data.getEndDate() == null || data.getEndDate().after(new Date()));
	}
	
	private boolean isTest(EnterpriseData data) {
		return is(data) && AonStringUtils.containsIgnoreCase(data.getExpression(), "test");
	}
	
	private boolean was(List<EnterpriseData> history) {
		return history != null && !history.isEmpty()
			&& history.stream().anyMatch(d -> 
				d.getStartDate() != null && d.getStartDate().before(new Date()));
	}
	
	private boolean willBe(List<EnterpriseData> history) {
		return history != null && !history.isEmpty()
			&& history.stream().anyMatch(d -> 
				d.getStartDate() != null && d.getStartDate().after(new Date()));
	}
	
	// TICKET BAI	

	/**
	 * Obtiene el historial de configuraciones de Ticket BAI.
	 * @return Historial de configuraciones de Ticket BAI.
	 */
	public List<EnterpriseData> getTbaiDataHistory() {
		return tbaiDataHistory;
	}

	/**
	 * Establece el historial de configuraciones de Ticket BAI.
	 * @param tbaiDataHistory Historial de configuraciones de Ticket BAI.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setTbaiDataHistory(List<EnterpriseData> tbaiDataHistory) {
		this.tbaiDataHistory = tbaiDataHistory;
		return this;
	}
	
	/**
	 * Obtiene la configuraci�n actual de Ticket BAI.
	 * @return Configuraci�n actual de Ticket BAI.
	 */
	public EnterpriseData getTbaiData() {
		return tbaiData;
	}
	
	/**
	 * Establece la configuraci�n actual de Ticket BAI.
	 * @param tbaiData Configuraci�n actual de Ticket BAI.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setTbaiData(EnterpriseData tbaiData) {
		this.tbaiData = tbaiData;
		return this;
	}
	
	/**
	 * Determina si la empresa est� dada de alta en Ticket Bai.
	 * @return true si la empresa est� dada de alta en Ticket Bai actualmente, false en caso contrario.
	 */
	public boolean isTbai() {
		return is(getTbaiData());
	}

	/**
	 * Determina si la empresa est� dada de alta en Ticket Bai en modo test.
	 * @return true si la empresa est� dada de alta en Ticket Bai en modo test, false en caso contrario.
	 */
	public boolean isTbaiTest() {
		return isTest(getTbaiData());
	}
	
	/**
	 * Determina si la empresa ha estado dada de alta en Ticket Bai en alg�n momento.
	 * @return true si la empresa ha estado dada de alta en Ticket Bai en alg�n momento, false en caso contrario.
	 */
	public boolean wasTbai() {
		return was(getTbaiDataHistory());
	}

	/**
	 * Determina si la empresa estar� dada de alta en Ticket Bai en alg�n momento.
	 * @return true si la empresa estar� dada de alta en Ticket Bai en alg�n momento, false en caso contrario.
	 */
	public boolean willBeTbai() {
		return willBe(getTbaiDataHistory());
	}
	
	// LROE
	
	/**
	 * Obtiene el historial de configuraciones de LROE.
	 * @return Historial de configuraciones de LROE.
	 */
	public List<EnterpriseData> getLroeDataHistory() {
		return lroeDataHistory;
	}
	
	/**
	 * Establece el historial de configuraciones de LROE.
	 * @param lroeDataHistory Historial de configuraciones de LROE.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setLroeDataHistory(List<EnterpriseData> lroeDataHistory) {
		this.lroeDataHistory = lroeDataHistory;
		return this;
	}
	
	/**
	 * Obtiene la configuraci�n actual de LROE.
	 * @return Configuraci�n actual de LROE.
	 */
	public EnterpriseData getLroeData() {
		return lroeData;
	}
	
	/**
	 * Establece la configuraci�n actual de LROE.
	 * @param lroeData Configuraci�n actual de LROE.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setLroeData(EnterpriseData lroeData) {
		this.lroeData = lroeData;
		return this;
	}
	
	/**
	 * Determina si la empresa est� dada de alta en LROE.
	 * @return true si la empresa est� dada de alta en LROE actualmente, false en caso contrario.
	 */
	public boolean isLroe() {
		return is(getLroeData());
	}
	
	/**
	 * Determina si la empresa est� dada de alta en LROE en modo test.
	 * @return true si la empresa est� dada de alta en LROE en modo test, false en caso contrario.
	 */
	public boolean isLroeTest() {
		return isTest(getLroeData());
	}
	
	/**
	 * Determina si la empresa ha estado dada de alta en LROE en alg�n momento.
	 * @return true si la empresa ha estado dada de alta en LROE en alg�n momento, false en caso contrario.
	 */
	public boolean wasLroe() {
		return was(getLroeDataHistory());
	}
	
	/**
	 * Determina si la empresa estar� dada de alta en LROE en alg�n momento.
	 * @return true si la empresa estar� dada de alta en LROE en alg�n momento, false en caso contrario.
	 */
	public boolean willBeLroe() {
		return willBe(getLroeDataHistory());
	}
	
	@Deprecated
	public String getLroeRegistryDate() {
		return lroeRegistryDate;
	}
	
	@Deprecated
	public InvoiceCommunicationConfiguration setLroeRegistryDate(String lroeRegistryDate) {
		this.lroeRegistryDate = lroeRegistryDate;
		return this;
	}
	
	// SII
	
	/**
	 * Obtiene el historial de configuraciones de SII.
	 * @return Historial de configuraciones de SII.
	 */
	public List<EnterpriseData> getSiiDataHistory() {
		return siiDataHistory;
	}
	
	/**
	 * Establece el historial de configuraciones de SII.
	 * @param siiDataHistory Historial de configuraciones de SII.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setSiiDataHistory(List<EnterpriseData> siiDataHistory) {
		this.siiDataHistory = siiDataHistory;
		return this;
	}
	
	/**
	 * Obtiene la configuraci�n actual de SII.
	 * @return Configuraci�n actual de SII.
	 */
	public EnterpriseData getSiiData() {
		return siiData;
	}
	
	/**
	 * Establece la configuraci�n actual de SII.
	 * @param siiData Configuraci�n actual de SII.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setSiiData(EnterpriseData siiData) {
		this.siiData = siiData;
		return this;
	}
	
	/**
	 * Determina si la empresa est� dada de alta en SII.
	 * @return true si la empresa est� dada de alta en SII actualmente, false en caso contrario.
	 */
	public boolean isSii() {
		return is(getSiiData());
	}
	
	/**
	 * Determina si la empresa est� dada de alta en SII en modo test.
	 * @return true si la empresa est� dada de alta en SII en modo test, false en caso contrario.
	 */
	public boolean isSiiTest() {
		return isTest(getSiiData());
	}
	
	/**
	 * Determina si la empresa ha estado dada de alta en SII en alg�n momento.
	 * @return true si la empresa ha estado dada de alta en SII en alg�n momento, false en caso contrario.
	 */
	public boolean wasSii() {
		return was(getSiiDataHistory());
	}
	
	/**
	 * Determina si la empresa estar� dada de alta en SII en alg�n momento.
	 * @return true si la empresa estar� dada de alta en SII en alg�n momento, false en caso contrario.
	 */
	public boolean willBeSii() {
		return willBe(getSiiDataHistory());
	}
	
	@Deprecated
	public String getSiiRegistryDate() {
		return siiRegistryDate;
	}
	
	@Deprecated
	public InvoiceCommunicationConfiguration setSiiRegistryDate(String siiRegistryDate) {
		this.siiRegistryDate = siiRegistryDate;
		return this;
	}
	
	@Deprecated
	public boolean isRegistryTaxDate() {
		return "tax".equalsIgnoreCase(getSiiRegistryDate());
	}
	
	@Deprecated
	public boolean isPrepareNewSii() {
		return prepareNewSii;
	}
	
	@Deprecated
	public InvoiceCommunicationConfiguration setPrepareNewSii(boolean prepareNewSii) {
		this.prepareNewSii = prepareNewSii;
		return this;
	}
	
	// VERIFACTU
	
	/**
	 * Obtiene el historial de configuraciones de Verifactu.
	 * @return Historial de configuraciones de Verifactu.
	 */
	public List<EnterpriseData> getVerifactuDataHistory() {
		return verifactuDataHistory;
	}
	
	/**
	 * Establece el historial de configuraciones de Verifactu.
	 * @param verifactuDataHistory Historial de configuraciones de Verifactu.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setVerifactuDataHistory(List<EnterpriseData> verifactuDataHistory) {
		this.verifactuDataHistory = verifactuDataHistory;
		return this;
	}
	
	/**
	 * Obtiene la configuraci�n actual de Verifactu.
	 * @return Configuraci�n actual de Verifactu.
	 */
	public EnterpriseData getVerifactuData() {
		return verifactuData;
	}
	
	/**
	 * Establece la configuraci�n actual de Verifactu.
	 * @param verifactuData Configuraci�n actual de Verifactu.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setVerifactuData(EnterpriseData verifactuData) {
		this.verifactuData = verifactuData;
		return this;
	}
	
	/**
	 * Determina si la empresa est� dada de alta en Verifactu.
	 * @return true si la empresa est� dada de alta en Verifactu actualmente, false en caso contrario.
	 */
	public boolean isVerifactu() {
		return is(getVerifactuData());
	}
	
	/**
	 * Determina si la empresa est� dada de alta en Verifactu en modo test.
	 * @return true si la empresa est� dada de alta en Verifactu en modo test, false en caso contrario.
	 */
	public boolean isVerifactuTest() {
		return isTest(getVerifactuData());
	}

	/**
	 * Determina si la empresa ha estado dada de alta en Verifactu en alg�n momento.
	 * @return true si la empresa ha estado dada de alta en Verifactu en alg�n momento, false en caso contrario.
	 */
	public boolean wasVerifactu() {
		return was(getVerifactuDataHistory());
	}
	
	/**
	 * Determina si la empresa estar� dada de alta en Verifactu en alg�n momento.
	 * @return true si la empresa estar� dada de alta en Verifactu en alg�n momento, false en caso contrario.
	 */
	public boolean willBeVerifactu() {
		return willBe(getVerifactuDataHistory());
	}

	// NO VERIFACTU
	
	/**
	 * Obtiene el historial de configuraciones de No Verifactu.
	 * @return Historial de configuraciones de No Verifactu.
	 */
	public List<EnterpriseData> getNoVerifactuDataHistory() {
		return noVerifactuDataHistory;
	}
	
	/**
	 * Establece el historial de configuraciones de No Verifactu.
	 * @param noVerifactuDataHistory Historial de configuraciones de No Verifactu.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setNoVerifactuDataHistory(List<EnterpriseData> noVerifactuDataHistory) {
		this.noVerifactuDataHistory = noVerifactuDataHistory;
		return this;
	}
	
	/**
	 * Obtiene la configuraci�n actual de No Verifactu.
	 * @return Configuraci�n actual de No Verifactu.
	 */
	public EnterpriseData getNoVerifactuData() {
		return noVerifactuData;
	}
	
	/**
	 * Establece la configuraci�n actual de No Verifactu.
	 * @param noVerifactuData Configuraci�n actual de No Verifactu.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setNoVerifactuData(EnterpriseData noVerifactuData) {
		this.noVerifactuData = noVerifactuData;
		return this;
	}
	
	/**
	 * Determina si la empresa est� dada de alta en No Verifactu.
	 * @return true si la empresa est� dada de alta en No Verifactu actualmente, false en caso contrario.
	 */
	public boolean isNoVerifactu() {
		return is(getNoVerifactuData());
	}
	
	/**
	 * Determina si la empresa ha estado dada de alta en No Verifactu en alg�n momento.
	 * @return true si la empresa ha estado dada de alta en No Verifactu en alg�n momento, false en caso contrario.
	 */
	public boolean wasNoVerifactu() {
		return was(getNoVerifactuDataHistory());
	}
	
	/**
	 * Determina si la empresa estar� dada de alta en No Verifactu en alg�n momento.
	 * @return true si la empresa estar� dada de alta en No Verifactu en alg�n momento, false en caso contrario.
	 */
	public boolean willBeNoVerifactu() {
		return willBe(getNoVerifactuDataHistory());
	}
	
	// SIF || LEY ANTIFRAUDE
	
	/**
	 * Obtiene el historial de configuraciones de SIF.
	 * @return Historial de configuraciones de SIF.
	 */
	public List<EnterpriseData> getSifDataHistory() {
		return sifDataHistory;
	}
	
	/**
	 * Establece el historial de configuraciones de SIF.
	 * @param sifDataHistory Historial de configuraciones de SIF.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setSifDataHistory(List<EnterpriseData> sifDataHistory) {
		this.sifDataHistory = sifDataHistory;
		return this;
	}
	
	/**
	 * Obtiene la configuraci�n actual de SIF.
	 * @return Configuraci�n actual de SIF.
	 */
	public EnterpriseData getSifData() {
		return sifData;
	}
	
	/**
	 * Establece la configuraci�n actual de SIF.
	 * @param sifData Configuraci�n actual de SIF.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setSifData(EnterpriseData sifData) {
		this.sifData = sifData;
		return this;
	}
	
	/**
	 * Determina si la empresa est� dada de alta en SIF.
	 * @return true si la empresa est� dada de alta en SIF actualmente, false en caso contrario.
	 */
	public boolean isSif() {
		return is(getSifData());
	}
	
	/**
	 * Determina si la empresa ha estado dada de alta en SIF en alg�n momento.
	 * @return true si la empresa ha estado dada de alta en SIF en alg�n momento, false en caso contrario.
	 */
	public boolean wasSif() {
		return was(getSifDataHistory());
	}
	
	/**
	 * Determina si la empresa estar� dada de alta en SIF en alg�n momento.
	 * @return true si la empresa estar� dada de alta en SIF en alg�n momento, false en caso contrario.
	 */
	public boolean willBeSif() {
		return willBe(getSifDataHistory());
	}

	// NO SIF 
	
	/**
	 * Obtiene el historial de configuraciones de NO SIF.
	 * @return Historial de configuraciones de NO SIF.
	 */
	public List<EnterpriseData> getNoSifDataHistory() {
		return noSifDataHistory;
	}
	
	/**
	 * Establece el historial de configuraciones de NO SIF.
	 * @param sifDataHistory Historial de configuraciones de NO SIF.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setNoSifDataHistory(List<EnterpriseData> noSifDataHistory) {
		this.noSifDataHistory = noSifDataHistory;
		return this;
	}
	
	/**
	 * Obtiene la configuraci�n actual de NO SIF.
	 * @return Configuraci�n actual de NO SIF.
	 */
	public EnterpriseData getNoSifData() {
		return noSifData;
	}
	
	/**
	 * Establece la configuraci�n actual de NO SIF.
	 * @param sifData Configuraci�n actual de NO SIF.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setNoSifData(EnterpriseData noSifData) {
		this.noSifData = noSifData;
		return this;
	}
	
	/**
	 * Determina si la empresa est� dada de alta en NO SIF.
	 * @return true si la empresa est� dada de alta en NO SIF actualmente, false en caso contrario.
	 */
	public boolean isNoSif() {
		return is(getNoSifData());
	}
	
	/**
	 * Determina si la empresa ha estado dada de alta en NO SIF en alg�n momento.
	 * @return true si la empresa ha estado dada de alta en NO SIF en alg�n momento, false en caso contrario.
	 */
	public boolean wasNoSif() {
		return was(getNoSifDataHistory());
	}
	
	/**
	 * Determina si la empresa estar� dada de alta en NO SIF en alg�n momento.
	 * @return true si la empresa estar� dada de alta en NO SIF en alg�n momento, false en caso contrario.
	 */
	public boolean willBeNoSif() {
		return willBe(getNoSifDataHistory());
	}
	
	// CERTIFICADO
	
	/**
	 * Obtiene el identificador del certificado por defecto para comunicaciones de facturas.
	 * @return Identificador del certificado por defecto para comunicaciones de facturas.
	 */
	public Integer getDefaultCertificate() {
		return defaultCertificate;
	}
	
	/**
	 * Establece el identificador del certificado por defecto para comunicaciones de facturas.
	 * @param defaultCertificate Identificador del certificado por defecto para comunicaciones de facturas.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setDefaultCertificate(Integer defaultCertificate) {
		this.defaultCertificate = defaultCertificate;
		return this;
	}
	
	/**
	 * Obtiene el certificado para la comunicaci�n de facturas.
	 * @return Certificado para la comunicaci�n de facturas.
	 */
	public Certificate getCertificate() {
		return certificate;
	}
	
	/**
	 * Establece el certificado para la comunicaci�n de facturas.
	 * @param certificate Certificado para la comunicaci�n de facturas.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}

	/**
	 * Obtiene los tipos de comunicaci�n de facturas activos para Facturas Emitidas.
	 * @return Listado de tipos de comunicaci�n de facturas activos para Facturas Emitidas.
	 */
	public List<InvoiceCommunicationType> getTypes() {
		return getTypes(InvoiceType.SALES);
	}
	
	/**
	 * Obtiene los tipos de comunicaci�n de facturas activos para el tipo de factura indicado.
	 * @param invoiceType Tipo de factura.
	 * @return Listado de tipos de comunicaci�n de facturas activos para el tipo de factura indicado.
	 */
	public List<InvoiceCommunicationType> getTypes(InvoiceType invoiceType) {
		LinkedList<InvoiceCommunicationType> types = new LinkedList<>();

		if(invoiceType.isSales() && isTbai() && (isAraba() || isGipuzkoa())) {
			types.add(InvoiceCommunicationType.TBAI);
		} 
		
		if(isLroe() && isBizkaia()) {
			types.add(InvoiceCommunicationType.LROE);
		} 
		
		if(invoiceType.isSales() && isVerifactu() && (isAEAT() || isCanarias() || isUnknown())) {
			types.add(InvoiceCommunicationType.VERIFACTU);
		} 
		
		if(isSii() && (isAEAT() || isCanarias() || isUnknown() || isNavarra() 
				|| (!invoiceType.isSales() && (isAraba() || isGipuzkoa())))) {
			types.add(InvoiceCommunicationType.SII);
		} 
		
		if(invoiceType.isSales() && isNoVerifactu() && (isAEAT() || isCanarias() || isUnknown())) {
			types.add(InvoiceCommunicationType.NO_VERIFACTU);
		} 
		
		if(isSif()) {
			types.add(InvoiceCommunicationType.SIF);
		}
		
		return types;
	}
	
	/**
	 * Determina si existen tipos de comunicaci�n de facturas emitadas activos.
	 * @return true si existen tipos de comunicaci�n de facturas emitidas activos, false en caso contrario.
	 */
	public boolean hasCommunication() {
		return AonCollectionUtils.isNotEmpty(getTypes());
	}
	
	/**
	 * Determina si existen tipos de comunicaci�n de facturas activos para el tipo de factura indicado.
	 * @param invoiceType Tipo de factura.
	 * @return true si existen tipos de comunicaci�n de facturas activos para el tipo de factura indicado, false en caso contrario.
	 */
	public boolean hasCommunication(InvoiceType invoiceType) {
		return AonCollectionUtils.isNotEmpty(getTypes(invoiceType));
	}

	public boolean isBizkaia() 	{return administration == Administration.BIZKAIA;}
	public boolean isAraba() 	{return administration == Administration.ALAVA;}
	public boolean isGipuzkoa() {return administration == Administration.GIPUZKOA;}
	public boolean isNavarra() 	{return administration == Administration.NAVARRA;}
	public boolean isAEAT() 	{return administration == Administration.COMMON_TERRITORY;}
	public boolean isCanarias() {return administration == Administration.CANARIAS;}
	public boolean isUnknown() {return administration == null || administration == Administration.UNKNOWN;}

}
