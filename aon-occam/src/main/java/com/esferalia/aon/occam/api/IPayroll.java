package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Cost;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseActivityFilter;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.Filter.Mod145Filter;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractAttach;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;


public interface IPayroll {
	
	// -------------------- EMPLOYEE 
	
	public Employee addEmployee(AONContext ctx, String domainName, Employee employee);
	
	public Stream<Employee> getEmployees(AONContext ctx, EmployeeFilter filter);

	public Optional<Employee> getEmployee(AONContext ctx, EmployeeFilter filter);

	// -------------------- CONTRACT
	
	public Stream<Contract> getContractStream(AONContext ctx, ContractFilter filter);
	public void deleteContracts(AONContext ctx, Integer ...contractsId);
	// -------------------- CONTRACT DATA
	
	public Stream<ContractData> getContractDataStream(AONContext ctx, ContractDataFilter filter);
	public LinkedList<ContractData> saveContractData(AONContext ctx, ContractData ...contractData);

	// -------------------- IRPF DATA

	public Stream<IrpfData> getIrpfDataStream(AONContext ctx, IrpfDataFilter filter);
	
	// -------------------- AgreementLevelCategory
	
	public Stream<AgreementLevelCategory> getAgreementLevelCategoryStream(AONContext ctx, AgreementLevelCategoryFilter filter);
	
	// -------------------- BONUS 
	
	public Bonus [] getBonuses(AONContext ctx, String domainName, Integer contractId);

	public Bonus [] setBonuses(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Bonus ...bonuses);

	// -------------------- DEDUCTIONS 
	
	public Deduction [] getDeductions(AONContext ctx, String domainName, Integer contractId);

	public Deduction [] setDeductions(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Deduction ...deductions);

	// -------------------- COSTS 
	
	public Cost [] getCosts(AONContext ctx, String domainName, Integer contractId);

	public Cost [] setCosts(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Cost ...costs);

	// -------------------- BONUS 
	
	public ContractData [] getData(AONContext ctx, String domainName, Integer contractId);

	public ContractData [] setData(AONContext ctx, String domainName,String ccc, String naf, Date startDate, Date endDate, ContractData...contractDatas);

	public ContractData [] setContractData(AONContext ctx, String domainName, ContractFilter filter, ContractData...contractDatas);

	// -------------------- CCC
	
	public Stream<CCCInfo> getCCCStream(AONContext ctx);
	
	// ----------CONTRACT ATTACH
	
	public Stream<ContractAttach> getContractAttachStream(AONContext ctx, ContractAttachFilter filter);
	public ContractAttach getContractAttach(AONContext ctx, ContractAttachFilter filter);
	public ContractAttach saveContractAttach(AONContext ctx, ContractAttach attach);
	public void deleteContractAttach(AONContext ctx, Integer id);
	
	// ----------ENTERPRISE
	
	public Enterprise getEnterprise(AONContext ctx, EnterpriseFilter filter);

	public void saveEnterprise(AONContext ctx, Enterprise enterprise);
	
	// ----------ACTIVITY
	
	public Activity getActivity(AONContext ctx, EnterpriseActivityFilter filter);

	public void saveActivity(AONContext ctx, Activity Activity);
	
	public List<Activity> getActivities(AONContext ctx, EnterpriseActivityFilter filter);

	public void saveActivities(AONContext ctx, List<Activity> Activity);
	
	// ----------MOD145
	
	public List<Mod145> getMod145List(AONContext ctx, Mod145Filter filter);
	
	public Mod145 getMod145(AONContext ctx, Mod145Filter filter);
	
	public void saveMod145(AONContext ctx, Mod145 mod145);
}
