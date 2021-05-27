package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Mod184Income implements Serializable {

	private Integer id;
	private int domain;
	private int mod184;
	
	private String key;
	private String subKey;
	private String country;
	private byte regime;
	private byte activityType;
	private Integer epigraph;
	private String granteeDocument;
	private String granteeName;
	private Date adqDate;
	private double increase;
	private double decrease;
	private double accountingResult;
	private double expenses;
	private double netYield;
	private double reductionPercent;
	private double deductionRightRent;
	private double result;
	private double deductionBase;
	private double retention;
	private String location;
	private String cadasdralReference;
	
	
	private double assetAcquisition;
	
	// Detalle de gastos / Rendimientos de actividades económicas
	// Gastos Personal
	private double staffExpenses;
	// Consumos de explotación
	private double consumosExplotacion;
	// Tributos Fiscalmente deducibles
	private double taxDeduction;
	// Arrendamientos y cánones
	private double arrendamientosCanones;
	// Reparaciones y conservación
	private double reparacionConservacion;
	// Servicios profesionales independientes
	private double servProfIndependientes;
	// Suministros
	private double suministros;
	// Gastos financieros
	private double gastosFinancieros;
	// Amortizaciones
	private double amortizaciones;
	// Provisiones
	private double provisiones;
	// Otros Gastos Fiscalmente Deducibles
	private double otherTaxDeduction;
	// Criterio de cobros y pagos
	private boolean vatAccrualPayment; 
	 
	// Detalle de gastos / Rendimientos de capital inmobiliario
	// Intereses y demás gastos de financiación
	private double inmInteresFinanciacion;
	// Conservación y reparación
	private double inmReparacionConservacion;
	// Intereses / Gastos de reparación y conservación pendientes
	private double inmGastosReparacionConservacionPendientes;
	// Tributos y recargos
	private double inmTributosRecargos;
	// Saldos de dudoso cobro
	private double inmSaldoDudosoCobro;
	// Cantidades devengadas por terceros
	private double inmCantidadesDevengadas;
	// Primas de seguros
	private double inmPrimasSeguro;
	// Amortización del inmueble
	private double inmAmortizacionInmueble;	
	// Amortización de bienes muebles
	private double inmAmortizacionMueble;	
	// Otros gastos deducibles
	private double inmOtrosGastosDeducible;	
	// Número de días de arrendamiento o cesión de uso y disfrute	
	private int inmNumeroDiasArrendamiento;

	private boolean dirty;
	private boolean deleted;
	private int tempId;	

	public Integer getId() {
		return id;
	}

	public Mod184Income setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod184Income setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod184() {
		return mod184;
	}

	public Mod184Income setMod184(int mod184) {
		this.mod184 = mod184;
		return this;
	}
	
	public String getKey() {
		return key;
	}

	public Mod184Income setKey(String key) {
		this.key = key;
		return this;
	}

	public String getSubKey() {
		return subKey;
	}

	public Mod184Income setSubKey(String subKey) {
		this.subKey = subKey;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public Mod184Income setCountry(String country) {
		this.country = country;
		return this;
	}

	public byte getRegime() {
		return regime;
	}

	public Mod184Income setRegime(byte regime) {
		this.regime = regime;
		return this;
	}

	public byte getActivityType() {
		return activityType;
	}

	public Mod184Income setActivityType(byte activityType) {
		this.activityType = activityType;
		return this;
	}

	public Integer getEpigraph() {
		return epigraph;
	}

	public Mod184Income setEpigraph(Integer epigraph) {
		this.epigraph = epigraph;
		return this;
	}

	public String getGranteeDocument() {
		return granteeDocument;
	}

	public Mod184Income setGranteeDocument(String granteeDocument) {
		this.granteeDocument = granteeDocument;
		return this;
	}

	public String getGranteeName() {
		return granteeName;
	}

	public Mod184Income setGranteeName(String granteeName) {
		this.granteeName = granteeName;
		return this;
	}

	public Date getAdqDate() {
		return adqDate;
	}

	public Mod184Income setAdqDate(Date adqDate) {
		this.adqDate = adqDate;
		return this;
	}

	public double getIncrease() {
		return increase;
	}

	public Mod184Income setIncrease(double increase) {
		this.increase = increase;
		return this;
	}

	public double getDecrease() {
		return decrease;
	}

	public Mod184Income setDecrease(double decrease) {
		this.decrease = decrease;
		return this;
	}

	public double getAccountingResult() {
		return accountingResult;
	}

	public Mod184Income setAccountingResult(double accountingResult) {
		this.accountingResult = accountingResult;
		return this;
	}

	public double getExpenses() {
		return expenses;
	}

	public Mod184Income setExpenses(double expenses) {
		this.expenses = expenses;
		return this;
	}

	public double getNetYield() {
		return netYield;
	}

	public Mod184Income setNetYield(double netYield) {
		this.netYield = netYield;
		return this;
	}

	public double getReductionPercent() {
		return reductionPercent;
	}

	public Mod184Income setReductionPercent(double reductionPercent) {
		this.reductionPercent = reductionPercent;
		return this;
	}

	public double getDeductionRightRent() {
		return deductionRightRent;
	}

	public Mod184Income setDeductionRightRent(double deductionRightRent) {
		this.deductionRightRent = deductionRightRent;
		return this;
	}

	public double getResult() {
		return result;
	}

	public Mod184Income setResult(double result) {
		this.result = result;
		return this;
	}

	public double getDeductionBase() {
		return deductionBase;
	}

	public Mod184Income setDeductionBase(double deductionBase) {
		this.deductionBase = deductionBase;
		return this;
	}

	public double getRetention() {
		return retention;
	}

	public Mod184Income setRetention(double retention) {
		this.retention = retention;
		return this;
	}
	public String getLocation() {
		return location;
	}

	public Mod184Income setLocation(String location) {
		this.location = location;
		return this;
	}

	public String getCadasdralReference() {
		return cadasdralReference;
	}

	public Mod184Income setCadasdralReference(String cadasdralReference) {
		this.cadasdralReference = cadasdralReference;
		return this;
	}
	
	public double getStaffExpenses() {
		return staffExpenses;
	}

	public Mod184Income setStaffExpenses(double staffExpenses) {
		this.staffExpenses = staffExpenses;
		return this;
	}

	public double getAssetAcquisition() {
		return assetAcquisition;
	}

	public Mod184Income setAssetAcquisition(double assetAcquisition) {
		this.assetAcquisition = assetAcquisition;
		return this;
	}

	public double getTaxDeduction() {
		return taxDeduction;
	}

	public Mod184Income setTaxDeduction(double taxDeduction) {
		this.taxDeduction = taxDeduction;
		return this;
	}

	public double getOtherTaxDeduction() {
		return otherTaxDeduction;
	}

	public Mod184Income setOtherTaxDeduction(double otherTaxDeduction) {
		this.otherTaxDeduction = otherTaxDeduction;
		return this;
	}
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Mod184Income setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	public double getConsumosExplotacion() {
		return consumosExplotacion;
	}

	public Mod184Income setConsumosExplotacion(double consumosExplotacion) {
		this.consumosExplotacion = consumosExplotacion;
		return this;
	}

	public double getArrendamientosCanones() {
		return arrendamientosCanones;
	}

	public Mod184Income setArrendamientosCanones(double arrendamientosCanones) {
		this.arrendamientosCanones = arrendamientosCanones;
		return this;
	}

	public double getReparacionConservacion() {
		return reparacionConservacion;
	}

	public Mod184Income setReparacionConservacion(double reparacionConservacion) {
		this.reparacionConservacion = reparacionConservacion;
		return this;
	}

	public double getServProfIndependientes() {
		return servProfIndependientes;
	}

	public Mod184Income setServProfIndependientes(double servProfIndependientes) {
		this.servProfIndependientes = servProfIndependientes;
		return this;
	}

	public double getSuministros() {
		return suministros;
	}

	public Mod184Income setSuministros(double suministros) {
		this.suministros = suministros;
		return this;
	}

	public double getGastosFinancieros() {
		return gastosFinancieros;
	}

	public Mod184Income setGastosFinancieros(double gastosFinancieros) {
		this.gastosFinancieros = gastosFinancieros;
		return this;
	}

	public double getAmortizaciones() {
		return amortizaciones;
	}

	public Mod184Income setAmortizaciones(double amortizaciones) {
		this.amortizaciones = amortizaciones;
		return this;
	}

	public double getProvisiones() {
		return provisiones;
	}

	public Mod184Income setProvisiones(double provisiones) {
		this.provisiones = provisiones;
		return this;
	}

	public double getInmInteresFinanciacion() {
		return inmInteresFinanciacion;
	}

	public Mod184Income setInmInteresFinanciacion(double inmInteresFinanciacion) {
		this.inmInteresFinanciacion = inmInteresFinanciacion;
		return this;
	}

	public double getInmReparacionConservacion() {
		return inmReparacionConservacion;
	}

	public Mod184Income setInmReparacionConservacion(double inmReparacionConservacion) {
		this.inmReparacionConservacion = inmReparacionConservacion;
		return this;
	}

	public double getInmGastosReparacionConservacionPendientes() {
		return inmGastosReparacionConservacionPendientes;
	}

	public Mod184Income setInmGastosReparacionConservacionPendientes(double inmGastosReparacionConservacionPendientes) {
		this.inmGastosReparacionConservacionPendientes = inmGastosReparacionConservacionPendientes;
		return this;
	}

	public double getInmTributosRecargos() {
		return inmTributosRecargos;
	}

	public Mod184Income setInmTributosRecargos(double inmTributosRecargos) {
		this.inmTributosRecargos = inmTributosRecargos;
		return this;
	}

	public double getInmSaldoDudosoCobro() {
		return inmSaldoDudosoCobro;
	}

	public Mod184Income setInmSaldoDudosoCobro(double inmSaldoDudosoCobro) {
		this.inmSaldoDudosoCobro = inmSaldoDudosoCobro;
		return this;
	}

	public double getInmCantidadesDevengadas() {
		return inmCantidadesDevengadas;
	}

	public Mod184Income setInmCantidadesDevengadas(double inmCantidadesDevengadas) {
		this.inmCantidadesDevengadas = inmCantidadesDevengadas;
		return this;
	}

	public double getInmPrimasSeguro() {
		return inmPrimasSeguro;
	}

	public Mod184Income setInmPrimasSeguro(double inmPrimasSeguro) {
		this.inmPrimasSeguro = inmPrimasSeguro;
		return this;
	}

	public double getInmAmortizacionInmueble() {
		return inmAmortizacionInmueble;
	}

	public Mod184Income setInmAmortizacionInmueble(double inmAmortizacionInmueble) {
		this.inmAmortizacionInmueble = inmAmortizacionInmueble;
		return this;
	}

	public double getInmAmortizacionMueble() {
		return inmAmortizacionMueble;
	}

	public Mod184Income setInmAmortizacionMueble(double inmAmortizacionMueble) {
		this.inmAmortizacionMueble = inmAmortizacionMueble;
		return this;
	}

	public double getInmOtrosGastosDeducible() {
		return inmOtrosGastosDeducible;
	}

	public Mod184Income setInmOtrosGastosDeducible(double inmOtrosGastosDeducible) {
		this.inmOtrosGastosDeducible = inmOtrosGastosDeducible;
		return this;
	}

	public int getInmNumeroDiasArrendamiento() {
		return inmNumeroDiasArrendamiento;
	}

	public Mod184Income setInmNumeroDiasArrendamiento(int inmNumeroDiasArrendamiento) {
		this.inmNumeroDiasArrendamiento = inmNumeroDiasArrendamiento;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod184Income setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod184Income setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod184Income setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}

	
}
