package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;


@SuppressWarnings("serial")
public class RetentionCertificate implements Serializable {

	private Integer id;
	private int domain;
	private int year;
	private String name;
	
	private String enterpriseName;
	private String enterpriseDocument;
	private String employeeName;
	private String employeeDocument;
	
	/**
	 * *************************************************************************
	 * Rendimientos del trabajo, dietas exceptuadas de gravamen y rentas exentas (Modelo 190) y Rendimientos del capital mobiliario y determinadas rentas (Modelo 193)
	 * *************************************************************************
	 */
	// Rendimientos de trabajo
	private double perception;
	private double retention;
	private double inKindPerception;
	private double inKindDeposit;
	private double inKindOutputDeposit;
	
	//
	private double forecastPlanContributions;
	private double dependencyContributions;
	
	//
	private double applicableReduction;
	private double deducibleExpense;
	
	// Rendimientos satisfechos en el ejercicio correspondientes a ejercicios
	// anteriores (atrasos)
	private RetentionCertificate delay1;
	private RetentionCertificate delay2;
	private RetentionCertificate delay3;
	private RetentionCertificate delay4;
	
	// Cantidades reintegradas por el perceptor en el ejercicio por haber sido
	// indebida o excesivamente percibidas en ejercicios anteriores (reintegros)
	private double refundAmount;     // Certificado Retenciones Modelo 193, se pone aqui el importe de las percepciones
	private double refundReduction;  // Certificado Retenciones Modelo 193, se pone aqui el importe de las reducciones
	private RetentionCertificate refund1;
	private RetentionCertificate refund2;
	private RetentionCertificate refund3;
	
	// Dietas exceptuadas de gravamen y rentas exentas del impueto
	private double journeyDiet;
	private double incomeExemption;
	
	/** 
	 * ****************************************
	 * Rendimientos de actividades economicas (Modelo 190) y Rendimientos del capital mobiliario y determinadas rentas (Modelo 193)
	 * ****************************************
	 */
	// Rendimientos de actividades agricolas o ganaderas
	// clave H, subclaves 01 y 02
	private RetentionCertificate prof1;  // Certificado Retenciones Modelo 193: Clave A
	// Rendimientos de actividades forestales
	// clave H, subclave 03
	private RetentionCertificate prof2;  // Certificado Retenciones Modelo 193: Clave B
	// Rendimientos de actividades empresariales en estimacion objetiva
	// previstas en el art. 95.6 del Reglamento del IRPF
	// clave H, subclave 04
	private RetentionCertificate prof3;  // Certificado Retenciones Modelo 193: Clave C
	// Rendimientos a que se refiere el articulo 75.2.b) del Reglamento del
	// IRPF, que deban calificarse como rendimientos de actividades economicas
	// clave I
	private RetentionCertificate prof4;  // Certificado Retenciones Modelo 193: Clave D
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeDocument() {
		return employeeDocument;
	}
	public void setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
	}
	public double getPerception() {
		return perception;
	}
	public void setPerception(double perception) {
		this.perception = perception;
	}
	public double getRetention() {
		return retention;
	}
	public void setRetention(double retention) {
		this.retention = retention;
	}
	public double getInKindPerception() {
		return inKindPerception;
	}
	public void setInKindPerception(double inKindPerception) {
		this.inKindPerception = inKindPerception;
	}
	public double getInKindDeposit() {
		return inKindDeposit;
	}
	public void setInKindDeposit(double inKindDeposit) {
		this.inKindDeposit = inKindDeposit;
	}
	public double getInKindOutputDeposit() {
		return inKindOutputDeposit;
	}
	public void setInKindOutputDeposit(double inKindOutputDeposit) {
		this.inKindOutputDeposit = inKindOutputDeposit;
	}
	public double getForecastPlanContributions() {
		return forecastPlanContributions;
	}
	public void setForecastPlanContributions(double forecastPlanContributions) {
		this.forecastPlanContributions = forecastPlanContributions;
	}
	public double getDependencyContributions() {
		return dependencyContributions;
	}
	public void setDependencyContributions(double dependencyContributions) {
		this.dependencyContributions = dependencyContributions;
	}
	public double getApplicableReduction() {
		return applicableReduction;
	}
	public void setApplicableReduction(double applicableReduction) {
		this.applicableReduction = applicableReduction;
	}
	public double getDeducibleExpense() {
		return deducibleExpense;
	}
	public void setDeducibleExpense(double deducibleExpense) {
		this.deducibleExpense = deducibleExpense;
	}
	public RetentionCertificate getDelay1() {
		return delay1;
	}
	public void setDelay1(RetentionCertificate delay1) {
		this.delay1 = delay1;
	}
	public RetentionCertificate getDelay2() {
		return delay2;
	}
	public void setDelay2(RetentionCertificate delay2) {
		this.delay2 = delay2;
	}
	public RetentionCertificate getDelay3() {
		return delay3;
	}
	public void setDelay3(RetentionCertificate delay3) {
		this.delay3 = delay3;
	}
	public RetentionCertificate getDelay4() {
		return delay4;
	}
	public void setDelay4(RetentionCertificate delay4) {
		this.delay4 = delay4;
	}
	public double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}
	public double getRefundReduction() {
		return refundReduction;
	}
	public void setRefundReduction(double refundReduction) {
		this.refundReduction = refundReduction;
	}
	public RetentionCertificate getRefund1() {
		return refund1;
	}
	public void setRefund1(RetentionCertificate refund1) {
		this.refund1 = refund1;
	}
	public RetentionCertificate getRefund2() {
		return refund2;
	}
	public void setRefund2(RetentionCertificate refund2) {
		this.refund2 = refund2;
	}
	public RetentionCertificate getRefund3() {
		return refund3;
	}
	public void setRefund3(RetentionCertificate refund3) {
		this.refund3 = refund3;
	}
	public double getJourneyDiet() {
		return journeyDiet;
	}
	public void setJourneyDiet(double journeyDiet) {
		this.journeyDiet = journeyDiet;
	}
	public double getIncomeExemption() {
		return incomeExemption;
	}
	public void setIncomeExemption(double incomeExemption) {
		this.incomeExemption = incomeExemption;
	}
	public RetentionCertificate getProf1() {
		return prof1;
	}
	public void setProf1(RetentionCertificate prof1) {
		this.prof1 = prof1;
	}
	public RetentionCertificate getProf2() {
		return prof2;
	}
	public void setProf2(RetentionCertificate prof2) {
		this.prof2 = prof2;
	}
	public RetentionCertificate getProf3() {
		return prof3;
	}
	public void setProf3(RetentionCertificate prof3) {
		this.prof3 = prof3;
	}
	public RetentionCertificate getProf4() {
		return prof4;
	}
	public void setProf4(RetentionCertificate prof4) {
		this.prof4 = prof4;
	}
	
}
