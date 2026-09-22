package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
	 * Listado histórico de administraciones.
	 */
	private List<EnterpriseData>  administrationHistory;
	
	/**
	 * Administración actual.
	 */
	private Administration administration;

	// TBAI
	
	/**
	 * Listado histórico de configuraciones de TICKET BAI.
	 */
	private List<EnterpriseData> tbaiDataHistory;
	
	/**
	 * Estado actual de la configuración de TICKET BAI.
	 */
	private EnterpriseData tbaiData;
	
	/**
	 * Indica si la empresa ha enviado facturas mediante TBAI en el ejercicio actual.
	 */
	private boolean tbaiInvoice;
	
	// LROE
	
	/**
	 * Listado histórico de configuraciones de LROE.
	 */
	private List<EnterpriseData> lroeDataHistory;
	
	/**
	 * Estado actual de la configuración de LROE.
	 */
	private EnterpriseData lroeData;
	
	/**
	 * Indica si la empresa ha enviado facturas mediante LROE en el ejercicio actual.
	 */
	private boolean lroeInvoice;

	@Deprecated	private String lroeRegistryDate;
	
	// SII
	
	/**
	 * Listado histórico de configuraciones de SII.
	 */
	private List<EnterpriseData> siiDataHistory;
	
	/**
	 * Estado actual de la configuración de SII.
	 */
	private EnterpriseData siiData;

	/**
	 * Indica si la empresa ha enviado facturas mediante SII en el ejercicio actual.
	 */
	private boolean siiInvoice;
	
	@Deprecated	private String siiRegistryDate;
	@Deprecated	private boolean prepareNewSii;
	
	// VERIFACTU

	/**
	 * Listado histórico de configuraciones de Verifactu.
	 */
	private List<EnterpriseData> verifactuDataHistory;

	/**
	 * Estado actual de la configuración de Verifactu.
	 */
	private EnterpriseData verifactuData;
	
	/**
	 * Indica si la empresa ha enviado facturas mediante VERIFACTU en el ejercicio actual.
	 */
	private boolean verifactuInvoice;
	
	// NO VERIFACTU

	/**
	 * Listado histórico de configuraciones de No Verifactu.
	 */
	private List<EnterpriseData> noVerifactuDataHistory;
	
	/**
	 * Estado actual de la configuración de No Verifactu.
	 */
	private EnterpriseData noVerifactuData;
	
	/**
	 * Indica si la empresa ha enviado facturas mediante VERIFACTU en el ejercicio actual.
	 */
	private boolean noVerifactuInvoice;
	
	// SIF || LEY ANTIFRAUDE
	
	/**
	 * Listado histórico de configuraciones de SIF (Sistema Informï¿½tico de Facturaciï¿½n).
	 */
	private List<EnterpriseData> sifDataHistory; 

	/**
	 * Estado actual de la configuración de SIF (Sistema Informï¿½tico de Facturaciï¿½n).
	 */
	private EnterpriseData sifData;
	
	/**
	 * Indica si la empresa ha enviado facturas mediante SIF en el ejercicio actual.
	 */
	private boolean sifInvoice;
	
	// NO SIF
	
	/**
	 * Listado histórico de configuraciones de SIF (Sistema Informï¿½tico de Facturaciï¿½n).
	 */
	private List<EnterpriseData> noSifDataHistory; 

	/**
	 * Estado actual de la configuración de SIF (Sistema Informï¿½tico de Facturaciï¿½n).
	 */
	private EnterpriseData noSifData;
	
	// CERTIFICADO
	
	/**
	 * Identificador del certificado por defecto para comunicaciones de facturas.
	 */
	private Integer defaultCertificate;
	
	/**
	 * Certificado para la comunicación de facturas.
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
	 * Obtiene la Administración actual.
	 * @return Administración actual.
	 */
	public Administration getAdministration() {
		return administration;
	}
	
	/**
	 * Establece la Administración actual.
	 * @param administration Administración actual.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	// COMUNICATION FUNCTIONS

	/**
	 * Determina si una configuración de empresa está en rango para una fecha dada.
	 * La fecha de inicio debe ser anterior o igual a la fecha dada y la fecha de fin debe ser posterior a la fecha dada o nula.
	 */
	private boolean inRange(EnterpriseData d, Date atDate) {
		if (d == null || atDate == null) return false;
		return d.getStartDate() != null 
			&& !atDate.before(d.getStartDate())
			&& (d.getEndDate() == null || d.getEndDate().after(atDate))
		;
	}
	private boolean is(List<EnterpriseData> history, Date atDate) {
		return history != null && !history.isEmpty()
			&& history.stream().anyMatch(d -> inRange(d,atDate)); 
	}
	
	private boolean is(EnterpriseData data) {
		return data != null && data.getId() != null && (data.getEndDate() == null || data.getEndDate().after(new Date()));
	}

	public Administration getAdministration(Date atDate) {
		return AonCollectionUtils.stream(getAdministrationHistory())
			.filter(d -> inRange(d,atDate))
			.map(d -> Administration.safeValueOf(d.getExpression()))
			.filter(a -> a != null)
			.findFirst()
			.orElse(Administration.UNKNOWN);
	}
	
	private boolean isTest(EnterpriseData data) {
		return is(data) && AonStringUtils.containsIgnoreCase(data.getExpression(), "test");
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
	 * Obtiene la configuración actual de Ticket BAI.
	 * @return configuración actual de Ticket BAI.
	 */
	public EnterpriseData getTbaiData() {
		return tbaiData;
	}
	
	/**
	 * Establece la configuración actual de Ticket BAI.
	 * @param tbaiData configuración actual de Ticket BAI.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setTbaiData(EnterpriseData tbaiData) {
		this.tbaiData = tbaiData;
		return this;
	}
	
	/**
	 * Determina si la empresa está dada de alta en TBAI en la fecha y tipo de factura indicadas.
	 * @param date Fecha a comprobar.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en TBAI en la fecha y tipo de factura indicadas, false en caso contrario.
	 */
	public boolean isTbai(Date date, InvoiceType type) {
		return type.isSales() && isTbai(date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en TBAI actualmente con el tipo de factura indicado.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en TBAI actualmente con el tipo de factura indicado, false en caso contrario.
	 */
	public boolean isTbai(InvoiceType type) {
		return type.isSales() && isTbai();
	}
	
	/**
	 * Determina si la empresa está dada de alta en TBAI en la fecha indicada.
	 * @param date Fecha a comprobar.
	 * @return true si la empresa está dada de alta en TBAI en la fecha indicada, false en caso contrario.
	 */
	public boolean isTbai(Date date) {
		return is(getTbaiDataHistory(), date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en Ticket Bai.
	 * @return true si la empresa está dada de alta en Ticket Bai actualmente, false en caso contrario.
	 */
	public boolean isTbai() {
		return is(getTbaiData());
	}

	/**
	 * Determina si la empresa está dada de alta en Ticket Bai en modo test.
	 * @return true si la empresa está dada de alta en Ticket Bai en modo test, false en caso contrario.
	 */
	public boolean isTbaiTest() {
		return isTest(getTbaiData());
	}
	
	/**
	 * Indica si la empresa ha enviado facturas mediante TBAI en el ejercicio actual.
	 * @return true si la empresa ha enviado facturas mediante TBAI en el ejercicio actual, false en caso contrario.
	 */
	public boolean hasTbaiInvoice() {
		return tbaiInvoice;
	}
	
	public InvoiceCommunicationConfiguration setTbaiInvoice(boolean tbaiInvoice) {
		this.tbaiInvoice = tbaiInvoice;
		return this;
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
	 * Obtiene la configuración actual de LROE.
	 * @return configuración actual de LROE.
	 */
	public EnterpriseData getLroeData() {
		return lroeData;
	}
	
	/**
	 * Establece la configuración actual de LROE.
	 * @param lroeData configuración actual de LROE.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setLroeData(EnterpriseData lroeData) {
		this.lroeData = lroeData;
		return this;
	}
	
	/**
	 * Determina si la empresa está dada de alta en LROE en la fecha y tipo de factura indicadas.
	 * @param date Fecha a comprobar.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en LROE en la fecha y tipo de factura indicadas, false en caso contrario.
	 */
	public boolean isLroe(Date date, InvoiceType type) {
		return ((type.isSales() && !isNoSif(date)) || !type.isSales()) && isLroe(date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en LROE actualmente con el tipo de factura indicado.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en LROE actualmente con el tipo de factura indicado, false en caso contrario.
	 */
	public boolean isLroe(InvoiceType type) {
		return ((type.isSales() && !isNoSif()) || !type.isSales()) && isLroe();
	}
	
	/**
	 * Determina si la empresa está dada de alta en LROE en la fecha indicada.
	 * @param date Fecha a comprobar.
	 * @return true si la empresa está dada de alta en LROE en la fecha indicada, false en caso contrario.
	 */
	public boolean isLroe(Date date) {
		return is(getLroeDataHistory(), date);
	}
	
	
	/**
	 * Determina si la empresa está dada de alta en LROE.
	 * @return true si la empresa está dada de alta en LROE actualmente, false en caso contrario.
	 */
	public boolean isLroe() {
		return is(getLroeData());
	}
	
	/**
	 * Determina si la empresa está dada de alta en LROE en modo test.
	 * @return true si la empresa está dada de alta en LROE en modo test, false en caso contrario.
	 */
	public boolean isLroeTest() {
		return isTest(getLroeData());
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
	
	/**
	 * Indica si la empresa ha enviado facturas mediante LROE en el ejercicio actual.
	 * @return true si la empresa ha enviado facturas mediante LROE en el ejercicio actual, false en caso contrario.
	 */
	public boolean hasLroeInvoice() {
		return lroeInvoice;
	}
	
	public InvoiceCommunicationConfiguration setLroeInvoice(boolean lroeInvoice) {
		this.lroeInvoice = lroeInvoice;
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
	 * Obtiene la configuración actual de SII.
	 * @return configuración actual de SII.
	 */
	public EnterpriseData getSiiData() {
		return siiData;
	}
	
	/**
	 * Establece la configuración actual de SII.
	 * @param siiData configuración actual de SII.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setSiiData(EnterpriseData siiData) {
		this.siiData = siiData;
		return this;
	}
	
	/**
	 * Determina si la empresa está dada de alta en Sii en la fecha y tipo de factura indicadas.
	 * @param date Fecha a comprobar.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en SII en la fecha y tipo de factura indicadas, false en caso contrario.
	 */
	public boolean isSii(Date date, InvoiceType type) {
		return ((type.isSales() && !isNoSif(date)) || !type.isSales()) && isSii(date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en LROE actualmente con el tipo de factura indicado.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en LROE actualmente con el tipo de factura indicado, false en caso contrario.
	 */
	public boolean isSii(InvoiceType type) {
		return ((type.isSales() && !isNoSif()) || !type.isSales()) && isSii();
	}

	/**
	 * Determina si la empresa está dada de alta en SII en la fecha indicada.
	 * @param date Fecha a comprobar.
	 * @return true si la empresa está dada de alta en SII actualmente, false en caso contrario.
	 */
	public boolean isSii(Date date) {
		return is(getSiiDataHistory(), date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en SII.
	 * @return true si la empresa está dada de alta en SII actualmente, false en caso contrario.
	 */
	public boolean isSii() {
		return is(getSiiData());
	}
	
	/**
	 * Determina si la empresa está dada de alta en SII en modo test.
	 * @return true si la empresa está dada de alta en SII en modo test, false en caso contrario.
	 */
	public boolean isSiiTest() {
		return isTest(getSiiData());
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
	
	/**
	 * Indica si la empresa ha enviado facturas mediante SII en el ejercicio actual.
	 * @return true si la empresa ha enviado facturas mediante SII en el ejercicio actual, false en caso contrario.
	 */
	public boolean hasSiiInvoice() {
		return siiInvoice;
	}
	
	public InvoiceCommunicationConfiguration setSiiInvoice(boolean siiInvoice) {
		this.siiInvoice = siiInvoice;
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
	 * Obtiene la configuración actual de Verifactu.
	 * @return configuración actual de Verifactu.
	 */
	public EnterpriseData getVerifactuData() {
		return verifactuData;
	}
	
	/**
	 * Establece la configuración actual de Verifactu.
	 * @param verifactuData configuración actual de Verifactu.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setVerifactuData(EnterpriseData verifactuData) {
		this.verifactuData = verifactuData;
		return this;
	}
	
	/**
	 * Determina si la empresa está dada de alta en VERIFACTU en la fecha y tipo de factura indicadas.
	 * @param date Fecha a comprobar.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en VERIFACTU en la fecha y tipo de factura indicadas, false en caso contrario.
	 */
	public boolean isVerifactu(Date date, InvoiceType type) {
		return type.isSales() && isVerifactu(date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en VERIFACTU actualmente con el tipo de factura indicado.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en VERIFACTU actualmente con el tipo de factura indicado, false en caso contrario.
	 */
	public boolean isVerifactu(InvoiceType type) {
		return type.isSales() && isVerifactu();
	}
	
	/**
	 * Determina si la empresa está dada de alta en VERIFACTU en la fecha indicada.
	 * @param date Fecha a comprobar.
	 * @return true si la empresa está dada de alta en VERIFACTU actualmente, false en caso contrario.
	 */
	public boolean isVerifactu(Date date) {
		return is(getVerifactuDataHistory(), date);
	}
	
	
	/**
	 * Determina si la empresa está dada de alta en Verifactu.
	 * @return true si la empresa está dada de alta en Verifactu actualmente, false en caso contrario.
	 */
	public boolean isVerifactu() {
		return is(getVerifactuData());
	}
	
	/**
	 * Determina si la empresa está dada de alta en Verifactu en modo test.
	 * @return true si la empresa está dada de alta en Verifactu en modo test, false en caso contrario.
	 */
	public boolean isVerifactuTest() {
		return isTest(getVerifactuData());
	}

	/**
	 * Indica si la empresa ha enviado facturas mediante VERIFACTU en el ejercicio actual.
	 * @return true si la empresa ha enviado facturas mediante VERIFACTU en el ejercicio actual, false en caso contrario.
	 */
	public boolean hasVerifactuInvoice() {
		return verifactuInvoice;
	}

	public InvoiceCommunicationConfiguration setVerifactuInvoice(boolean verifactuInvoice) {
		this.verifactuInvoice = verifactuInvoice;
		return this;
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
	 * Obtiene la configuración actual de No Verifactu.
	 * @return configuración actual de No Verifactu.
	 */
	public EnterpriseData getNoVerifactuData() {
		return noVerifactuData;
	}
	
	/**
	 * Establece la configuración actual de No Verifactu.
	 * @param noVerifactuData configuración actual de No Verifactu.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setNoVerifactuData(EnterpriseData noVerifactuData) {
		this.noVerifactuData = noVerifactuData;
		return this;
	}
	
	/**
	 * Determina si la empresa está dada de alta en NO VERIFACTU en la fecha y tipo de factura indicadas.
	 * @param date Fecha a comprobar.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en NO VERIFACTU en la fecha y tipo de factura indicadas, false en caso contrario.
	 */
	public boolean isNoVerifactu(Date date, InvoiceType type) {
		return type.isSales() && isNoVerifactu(date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en NO VERIFACTU actualmente con el tipo de factura indicado.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en NO VERIFACTU actualmente con el tipo de factura indicado, false en caso contrario.
	 */
	public boolean isNoVerifactu(InvoiceType type) {
		return type.isSales() && isNoVerifactu();
	}
	
	/**
	 * Determina si la empresa está dada de alta en NO VERIFACTU en la fecha indicada.
	 * @param date Fecha a comprobar.
	 * @return true si la empresa esta dada de alta en NO VERIFACTU en la fecha indicada, false en caso contrario.
	 */
	public boolean isNoVerifactu(Date date) {
		return is(getNoVerifactuDataHistory(), date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en No Verifactu.
	 * @return true si la empresa está dada de alta en No Verifactu actualmente, false en caso contrario.
	 */
	public boolean isNoVerifactu() {
		return is(getNoVerifactuData());
	}
	
	/**
	 * Determina si la empresa estará dada de alta en No Verifactu en algún momento.
	 * @return true si la empresa estará dada de alta en No Verifactu en algún momento, false en caso contrario.
	 */
	public boolean willBeNoVerifactu() {
		return willBe(getNoVerifactuDataHistory());
	}
	
	/**
	 * Indica si la empresa ha enviado facturas mediante NO VERIFACTU en el ejercicio actual.
	 * @return true si la empresa ha enviado facturas mediante  NO VERIFACTU en el ejercicio actual, false en caso contrario.
	 */
	public boolean hasNoVerifactuInvoice() {
		return noVerifactuInvoice;
	}

	public InvoiceCommunicationConfiguration setNoVerifactuInvoice(boolean noVerifactuInvoice) {
		this.noVerifactuInvoice = noVerifactuInvoice;
		return this;
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
	 * Obtiene la configuración actual de SIF.
	 * @return configuración actual de SIF.
	 */
	public EnterpriseData getSifData() {
		return sifData;
	}
	
	/**
	 * Establece la configuración actual de SIF.
	 * @param sifData configuración actual de SIF.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setSifData(EnterpriseData sifData) {
		this.sifData = sifData;
		return this;
	}
	
	/**
	 * Determina si la empresa está dada de alta en SIF en la fecha y tipo de factura indicadas.
	 * @param date Fecha a comprobar.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en SIF en la fecha y tipo de factura indicadas, false en caso contrario.
	 */
	public boolean isSif(Date date, InvoiceType type) {
		return type.isSales() && isSif(date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en SIF actualmente con el tipo de factura indicado.
	 * @param type Tipo de factura.
	 * @return true si la empresa está dada de alta en SIF actualmente con el tipo de factura indicado, false en caso contrario.
	 */
	public boolean isSif(InvoiceType type) {
		return type.isSales() && isSif();
	}
	
	/**
	 * Determina si la empresa está dada de alta en SIF en la fecha indicada.
	 * @param date Fecha a comprobar.
	 * @return true si la empresa está dada de alta en SIF en la fecha indicada, false en caso contrario.
	 */
	public boolean isSif(Date date) {
		return is(getSifDataHistory(), date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en SIF.
	 * @return true si la empresa está dada de alta en SIF actualmente, false en caso contrario.
	 */
	public boolean isSif() {
		return is(getSifData());
	}
	
	/**
	 * Determina si la empresa está dada de alta en SIF en modo test.
	 * @return true si la empresa está dada de alta en SIF en modo test, false en caso contrario.
	 */
	public boolean isSifTest() {
		return isTest(getSifData());
	}
	
	/**
	 * Indica si la empresa ha enviado facturas mediante SIF en el ejercicio actual.
	 * @return true si la empresa ha enviado facturas mediante SIF en el ejercicio actual, false en caso contrario.
	 */
	public boolean hasSifInvoice() {
		return sifInvoice;
	}

	public InvoiceCommunicationConfiguration setSifInvoice(boolean sifInvoice) {
		this.sifInvoice = sifInvoice;
		return this;
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
	 * Obtiene la configuración actual de NO SIF.
	 * @return configuración actual de NO SIF.
	 */
	public EnterpriseData getNoSifData() {
		return noSifData;
	}
	
	/**
	 * Establece la configuración actual de NO SIF.
	 * @param sifData configuración actual de NO SIF.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setNoSifData(EnterpriseData noSifData) {
		this.noSifData = noSifData;
		return this;
	}
	
	/**
	 * Determina si la empresa está dada de alta en NO SIF en la fecha indicada.
	 * @param date Fecha a comprobar.
	 * @return true si la empresa está dada de alta en NO SIF en la fecha indicada, false en caso contrario.
	 */
	public boolean isNoSif(Date date) {
		return is(getNoSifDataHistory(), date);
	}
	
	/**
	 * Determina si la empresa está dada de alta en NO SIF.
	 * @return true si la empresa está dada de alta en NO SIF actualmente, false en caso contrario.
	 */
	public boolean isNoSif() {
		return is(getNoSifData());
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
	 * Obtiene el certificado para la comunicación de facturas.
	 * @return Certificado para la comunicación de facturas.
	 */
	public Certificate getCertificate() {
		return certificate;
	}
	
	/**
	 * Establece el certificado para la comunicación de facturas.
	 * @param certificate Certificado para la comunicación de facturas.
	 * @return Instancia actualizada de InvoiceCommunicationConfiguration.
	 */
	public InvoiceCommunicationConfiguration setCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}

	/**
	 * Obtiene los tipos de comunicación de facturas activos para Facturas Emitidas.
	 * @return Listado de tipos de comunicación de facturas activos para Facturas Emitidas.
	 */
	public List<InvoiceCommunicationType> getTypes() {
		return getTypes(InvoiceType.SALES);
	}
	
	/**
	 * Obtiene los tipos de comunicación de facturas activos para el tipo de factura indicado.
	 * @param invoiceType Tipo de factura.
	 * @return Listado de tipos de comunicación de facturas activos para el tipo de factura indicado.
	 */
	public List<InvoiceCommunicationType> getTypes(InvoiceType invoiceType) {
		LinkedList<InvoiceCommunicationType> types = new LinkedList<>();

		if(invoiceType.isSales() && isNoSif()) return types;
		
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
	
	public boolean hasCommunication(InvoiceType invoiceType, Date expDate) {
		return AonCollectionUtils.isNotEmpty(getTypes(invoiceType, expDate));
	}
	public List<InvoiceCommunicationType> getTypes(Date atDate) {
		return getTypes(InvoiceType.SALES, atDate);
	}
	public List<InvoiceCommunicationType> getTypes(InvoiceType invoiceType ,Date atDate) {
		LinkedList<InvoiceCommunicationType> types = new LinkedList<>();

		if(invoiceType.isSales() && isNoSif(atDate)) return types;
		
		if(invoiceType.isSales() && isTbai(atDate) && (isAraba(atDate) || isGipuzkoa(atDate))) {
			types.add(InvoiceCommunicationType.TBAI);
		} 
		
		if(isLroe(atDate) && isBizkaia(atDate)) {
			types.add(InvoiceCommunicationType.LROE);
		} 
		
		if(invoiceType.isSales() && isVerifactu(atDate) && (isAEAT(atDate) || isCanarias(atDate) || isUnknown(atDate))) {
			types.add(InvoiceCommunicationType.VERIFACTU);
		} 
		
		if(isSii(atDate) && (isAEAT(atDate) || isCanarias(atDate) || isUnknown(atDate) || isNavarra(atDate) 
			|| (!invoiceType.isSales() && (isAraba(atDate) || isGipuzkoa(atDate))))) {
			types.add(InvoiceCommunicationType.SII);
		} 
		
		if(invoiceType.isSales() && isNoVerifactu(atDate) && (isAEAT(atDate) || isCanarias(atDate) || isUnknown(atDate))) {
			types.add(InvoiceCommunicationType.NO_VERIFACTU);
		} 
		
		if(isSif(atDate)) {
			types.add(InvoiceCommunicationType.SIF);
		}
		
		return types;
	}
	/**
	 * Determina si existen tipos de comunicación de facturas emitadas activos.
	 * @return true si existen tipos de comunicación de facturas emitidas activos, false en caso contrario.
	 */
	public boolean hasCommunication() {
		return AonCollectionUtils.isNotEmpty(getTypes());
	}
	
	/**
	 * Determina si existen tipos de comunicación de facturas activos para el tipo de factura indicado.
	 * @param invoiceType Tipo de factura.
	 * @return true si existen tipos de comunicación de facturas activos para el tipo de factura indicado, false en caso contrario.
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

	private List<EnterpriseData> getAdministrationHistory(Administration admon) {
		return AonCollectionUtils.stream(getAdministrationHistory())
			.filter(d -> admon == Administration.safeValueOf(d.getExpression()))
			.collect(Collectors.toList());
		
	}
	
	public boolean isBizkaia(Date atDate) 	{return is( getAdministrationHistory(Administration.BIZKAIA), atDate);}
	public boolean isAraba(Date atDate) 	{return is( getAdministrationHistory(Administration.ALAVA), atDate);}
	public boolean isGipuzkoa(Date atDate) 	{return is( getAdministrationHistory(Administration.GIPUZKOA), atDate);}
	public boolean isNavarra(Date atDate) 	{return is( getAdministrationHistory(Administration.NAVARRA), atDate);}
	public boolean isAEAT(Date atDate) 		{return is( getAdministrationHistory(Administration.COMMON_TERRITORY), atDate);}
	public boolean isCanarias(Date atDate) 	{return is( getAdministrationHistory(Administration.CANARIAS), atDate);}
	public boolean isUnknown(Date atDate) 	{return is( getAdministrationHistory(Administration.UNKNOWN), atDate);}
	
}
