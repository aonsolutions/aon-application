package com.esferalia.aon.salary;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.data.IData;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.Payments;

public interface ISalary {
	
	Integer getId();
	
	// DATOS DE EMPRESA
	String getEnterpriseName();
	String getEnterpriseAddress();
	String getEnterpriseDocument();
	String getCcc();

	// DATOS DEL EMPLEADO
	String getEmployeeName();
	String getEmployeeDocument();
	Integer getRegistration();
	String getSocialSecurityNumber();	
	String getCategory();
	String getQuoteGroup();
	Date getSeniorityDate();
	
	// DATOS DE LA NOMINA
	SalaryType getType();
	Date getChargeDate();
	Date getIssueDate();
	Date getStartDate();
	Date getEndDate();
	boolean isFullTime();	// Si es jornada completa
	Integer getTimeUnits(); // Si (fullTime)? dias : horas
	
	//DEVENGOS
	Payments getPayments() throws SalaryException;
	Double getTotalPayment();
	
	//DEDUCCIONES
	Deductions getDeductions() throws SalaryException;
	Double getSocialSecurityContributions();
	Double getTotalDeduction();
	
	//COSTES EMPRESA
	Costs getEnterpriseCosts() throws SalaryException;
	
	<T extends IPayment> Collection<T> getPaymentS() throws SalaryException ;
	<T extends IDeduction>  Collection<T> getDeductionS() throws SalaryException ;
	<T extends IDeduction>  Collection<T> getCostS() throws SalaryException ;
	<T extends IDeduction>  Collection<T> getEmbargoS() throws SalaryException ;
	<T extends IData>  Map<String, List<T>> getDataS() throws SalaryException ;
	
	Double getTotalIrpf();

	// TOTAL LIQUIDO
	Double getTotalLiquid();
	
	//CUOTA EMPRESA
	Double getTotalEnterprise();

	//BASES
	//Remuneración mensual
	Double getRemuneration();
	// Prorrata de pagas extraordinarias
	Double getExtraPayProration();
	//Base de cotización por contigencias comunes
	Double getCommonBase();
	Double getRawCommonBase();
	//Base de cotización por contigencias profesionales (A.T. y E.P.) y conceptos de recaudación conjunta (Desemp., F.P., F.G.S.)
	Double getProfessionalBase();
	//Base de cotización adicional por horas extraordinarias estructurales
	Double getOvertimeBase();
	//Base de cotización adicional por horas extraordinarias no estructurales
	Double getNonEstructuralOvertimeBase();
	//Base sujeta a retención del I.R.P.F.
	Double getIrpfBase();
	
	Double getInKindIrpfBase();
	Double getInMoneyIrpfBase();
	
}
