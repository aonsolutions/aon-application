package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPayroll;
import com.esferalia.aon.occam.api.model.AuxSalaryInfo;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Cost;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractExtendedDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseActivityFilter;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.Filter.Mod145Filter;
import com.esferalia.aon.occam.api.model.Salary;
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
import com.esferalia.aon.occam.impl.jooq.dao.ActivityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CCCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ContractAttachDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDAO;
import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EmployeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod145.Mod145DAO;

public class PayrollImpl implements IPayroll {
	
	
	@Override
	public ContractData[] getData(AONContext ctx, String domainName, Integer contractId) {
		return EmployeeDAO.getData(ctx, domainName, contractId);
	}

	@Override
	public ContractData[] setData(AONContext ctx, String domainName, String ccc, String naf, 
			Date startDate, Date endDate,  ContractData... contractDatas) {
		return EmployeeDAO.setData(ctx, domainName, ccc, naf, startDate, endDate, contractDatas);
	}


	@Override
	public ContractData[] setContractData(AONContext ctx, String domainName, ContractFilter filter,
			ContractData... contractDatas) {
		return EmployeeDAO.setContractData(ctx, domainName, filter, contractDatas);
	}
	

	@Override
	public Deduction[] getDeductions(AONContext ctx, String domainName, Integer contractId) {
		return EmployeeDAO.getDeductions(ctx, domainName, contractId);
	}
	
	@Override
	public Deduction[] setDeductions(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Deduction... deductions) {
		return EmployeeDAO.setDeductions(ctx, domainName, ccc, naf, startDate, endDate, deductions);
	}
	
	@Override
	public Cost[] getCosts(AONContext ctx, String domainName, Integer contractId) {
		return EmployeeDAO.getCosts(ctx, domainName, contractId);
	}
	
	@Override
	public Cost[] setCosts(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate,
			Cost... costs) {
		return EmployeeDAO.setCosts(ctx, domainName, ccc, naf, startDate, endDate, costs);
	}
	
	// -------------------- EMPLOYEE
	
	@Override
	public Bonus[] getBonuses(AONContext ctx, String domainName, Integer contractId) {
		return EmployeeDAO.getBonuses(ctx, domainName, contractId);
	}
	
	@Override
	public Bonus[] setBonuses(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Bonus... bonuses) {
		return EmployeeDAO.setBonuses(ctx, domainName, ccc, naf, startDate, endDate, bonuses);
	}
	
	// -------------------- EMPLOYEE
	
	@Override
	public Optional<Employee> getEmployee(AONContext ctx, EmployeeFilter filter) {
		return EmployeeDAO.getEmployee(ctx, filter);
	}
	
	@Override
	public Stream<Employee> getEmployees(AONContext ctx, EmployeeFilter filter) {
		return EmployeeDAO.getEmployees(ctx, filter);
	}

	@Override
	public Employee addEmployee(AONContext ctx, String domainName, Employee employee) {
		return EmployeeDAO.addEmployee(ctx, domainName, employee);
	}

	// -------------------- CONTRACT
	
	public Stream<Contract> getContractStream(AONContext ctx, ContractFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ContractDAO.getContractStream(ctx, filter));
	}
	
	public Stream<ContractExtendedData> getContractExtendedDataStream(AONContext ctx, ContractExtendedDataFilter filter, Integer page, Integer perPage){
		return ctx.getDslContext().transactionResult(configuration ->
		ContractDAO.getContractExtendedDataStream(ctx, filter, page, perPage));
	}
	
	public Stream<ContractExtendedData> getContractSimplifiedDataStream(AONContext ctx, ContractExtendedDataFilter filter, Integer page, Integer perPage){
		return ctx.getDslContext().transactionResult(configuration ->
		ContractDAO.getContractSimplifiedData(ctx, filter, page, perPage));
	}
	
	public long getContractCount(AONContext ctx, ContractExtendedDataFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
		ContractDAO.getContractCount(ctx, filter));
	}
	
	public List<AuxSalaryInfo> getEmployeeSalary(AONContext ctx, ContractExtendedDataFilter filter, Integer page, Integer per_page) {
		return ctx.getDslContext().transactionResult(configuration ->
		ContractDAO.getEmployeeSalary(ctx, filter, page, per_page));
	}
	
	public long getEmployeeSalaryCount(AONContext ctx, ContractExtendedDataFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
		ContractDAO.getEmployeeSalaryCount(ctx, filter));
	}

	public void deleteContracts(AONContext ctx, Integer ...contractIds) {
		ctx.getDslContext().transaction(configuration -> ContractDAO.delete(ctx, contractIds));
	}

	// -------------------- CONTRACT DATA
	
	public Stream<ContractData> getContractDataStream(AONContext ctx, ContractDataFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ContractDataDAO.getStream(ctx, filter));
	}
	
	// -------------------- CONTRACT DATA
	
	public void saveContractDatas(AONContext ctx, List<ContractData> contractData){
		ctx.getDslContext().transaction(configuration -> ContractDataDAO.save(ctx, contractData));
	}	
	
	public LinkedList<ContractData> saveContractData(AONContext ctx, ContractData ...contractData){
		return ctx.getDslContext().transactionResult(configuration -> ContractDataDAO.insert(ctx, contractData));
	}	

	// -------------------- IRPF DATA
	
	@Override
	public Stream<IrpfData> getIrpfDataStream(AONContext ctx, IrpfDataFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			ContractDAO.getIrpfDataStream(ctx, filter));
	}

	// -------------------- AGREEMENT LEVEL CATEGORY

	@Override
	public Stream<AgreementLevelCategory> getAgreementLevelCategoryStream(AONContext ctx, AgreementLevelCategoryFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			ContractDAO.getAgreementLevelCategoryStream(ctx, filter));
	}

	// -------------------- CCC
	@Override
	public Stream<CCCInfo> getCCCStream(AONContext ctx){
		return ctx.getDslContext().transactionResult((configuration) ->
			CCCDAO.getCCCStream(ctx));
	}
	
	
	// -------------------- CONTRACT ATTACH
	
	@Override
	public Stream<ContractAttach> getContractAttachStream(AONContext ctx, ContractAttachFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> ContractAttachDAO.getStream(ctx, filter));
	}

	@Override
	public ContractAttach getContractAttach(AONContext ctx, ContractAttachFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> ContractAttachDAO.get(ctx, filter));
	}

	@Override
	public ContractAttach saveContractAttach(AONContext ctx, ContractAttach attach) {
		return ctx.getDslContext().transactionResult(configuration -> ContractAttachDAO.save(ctx, attach));
	}

	@Override
	public void deleteContractAttach(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> ContractAttachDAO.delete(ctx, id));
	}
	
	// -------------------- ENTERPRISE
	
	@Override
	public Enterprise getEnterprise(AONContext ctx, EnterpriseFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> EnterpriseDAO.get(ctx, filter));
	}

	@Override
	public void saveEnterprise(AONContext ctx, Enterprise enterprise) {
		ctx.getDslContext().transaction(configuration -> EnterpriseDAO.save(ctx, enterprise));
	}
	
	// -------------------- ACTIVITY
	
	@Override
	public Activity getActivity(AONContext ctx, EnterpriseActivityFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> ActivityDAO.get(ctx, filter));
	}

	@Override
	public void saveActivity(AONContext ctx, Activity activity) {
		ctx.getDslContext().transaction(configuration -> ActivityDAO.save(ctx, activity));
	}
	
	@Override
	public List<Activity> getActivities(AONContext ctx, EnterpriseActivityFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> ActivityDAO.getList(ctx, filter));
	}

	@Override
	public void saveActivities(AONContext ctx, List<Activity> activities) {
		ctx.getDslContext().transaction(configuration -> ActivityDAO.saveList(ctx, activities));
	}
	
	// -------------------- MOD 145
	
	@Override
	public List<Mod145> getMod145List(AONContext ctx, Mod145Filter filter) {
		return ctx.getDslContext().transactionResult(configuration -> Mod145DAO.getList(ctx, filter));
	}
	
	@Override
	public Mod145 getMod145(AONContext ctx, Mod145Filter filter) {
		return ctx.getDslContext().transactionResult(configuration -> Mod145DAO.get(ctx, filter));
	}

	@Override
	public void saveMod145(AONContext ctx, Mod145 mod145) {
		ctx.getDslContext().transaction(configuration -> Mod145DAO.save(ctx, mod145));
	}

}
