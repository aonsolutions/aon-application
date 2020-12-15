package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.Employee;


public interface IPayroll {
	
	// -------------------- EMPLOYEE 
	
	public Employee addEmployee(AONContext ctx, String domainName, Employee employee);

	// -------------------- CONTRACT
	
	public Stream<Contract> getContractStream(AONContext ctx, ContractFilter filter);

	// -------------------- CONTRACT DATA
	
	public Stream<ContractData> getContractDataStream(AONContext ctx, ContractDataFilter filter);

	// -------------------- IRPF DATA

	public Stream<IrpfData> getIrpfDataStream(AONContext ctx, IrpfDataFilter filter);
	
	// -------------------- AgreementLevelCategory
	
	public Stream<AgreementLevelCategory> getAgreementLevelCategoryStream(AONContext ctx, AgreementLevelCategoryFilter filter);
	
	// -------------------- BONUS 
	
	public Bonus [] setBonuses(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Bonus ...bonuses);

	// -------------------- DEDUCTIONS 
	
	public Deduction [] setDeductions(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Deduction ...deductions);

	// -------------------- CCC
	
	public Stream<CCCInfo> getCCCStream(AONContext ctx);

}
