package com.esferalia.aon.in.payroll.tgss.fie;

import java.util.Date;

public interface FieListener {

	public void onCCC(String ccc);

	public void onNaf(String naf);

	public void onIPF(String ipf);
	
	// DIT Datos de Incapacidad Temporal


	public void onDitItStartDate(Date itStartDate);

	public void onDitRelapse(Boolean relapse);

	public void onDitInitialProcessDate(Date initialProcessDate);

	public void onDitLastProcessDate(Date lastProcessDate);

	public void onDitAcumulatedDays(Integer acumulatedDays);

	public void onDitContingency(Integer contingency);

	public void onDitDeficiencyIndicator(String deficiencyIndicator);

	public void onDitDelegatePaymendEndDate(Date delegatePaymentEndDate);

	public void onDitDelegatePaymendEndCause(String delegatePaymentEndCause);

	public void onDitItEndDate(Date itEndDate);

	public void onDitItEndCause(String itEndCause);

	public void onDitItPartCancel(Boolean itPartCancel);
	
	default public void onDitProcessType(Integer processType) {};

	default public void onDitResponsibleEntity(String responsibleEntity) {};

	default public void onDitEstimatedDuration(Integer estimatedDuration) {};

	default public void onDitNonExistantProcessDate(Date nonExistantProcessDate) {};

	default public void onDitNonExistantProcessCause(String nonExistantProcessCause) {};

	// ITP IT Pago Directo

	public void onItdDirectPaymentStartDate(Date directPaymentStartDate);

	public void onItdRegulatoryBase(Float regulatoryBase);

	default public void onItdDirectPaymentStartEntity(String directPaymentStartEntity) {};

	default public void onItdActualResponsibleEntity(String actualResponsibleEntity) {};

	default public void onItdResponsibleEntityInitialPaymentDate(Date responsibleEntityInitialPaymentDate) {};

	default public void onItdResponsibleEntityFinalPaymentDate(Date responsibleEntityFinalPaymentDate) {};

	

	
	// OIT Otros Datos IT

	default public void onOitMcssProcessRevisionStartDate(Date mcssProcessRevisionStartDate) {};

	default public void onOitCause89Date(Date cause89Date) {};

	default public void onOitCause90Date(Date cause90Date) {};

	default public void onOitCause91Date(Date cause91Date) {};

	default public void onOitCause92Date(Date cause92Date) {};

	default public void onOitProcess170_2(Boolean process170_2) {};

	default public void onOitCause42Date(Date cause42Date) {};

	default public void onOitCause35Date(Date cause35Date) {};

	default public void onOitMedicalCertificateDate(Date MedicalCertificateDate) {};
	

	// ------------------------------------------------------------------------


	default public void onName(String name) {};

	default public void onFirstSurname(String firstSurname) {};

	default public void onSecondSurname(String secondSurname) {};

	default public void onRegime(String regime) {};
	
	default  public void onEntepriseName(String enterpriseName) {};

	// ------------------------------------------------------------------------

	default public void startEnterprise() {};
	
	default public void endEnterprise() {};
	
	default public void startRZS() {};

	default public void endRZS() {};
	
	default public void startEmployee() {};

	default public void endEmployee() {};

	default public void startNameData() {};

	default public void endNameData() {};

	default public void startDIT() {};

	default public void endDIT() {};

	default public void startITD() {};
	
	default public void endITD() {};

	default public void startOIT() {};

	default public void endOIT() {};


}