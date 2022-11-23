package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPayroll;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Cost;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractAttach;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.impl.jooq.dao.CCCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ContractAttachDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EmployeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;

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

	public void deleteContracts(AONContext ctx, Integer ...contractIds) {
		ctx.getDslContext().transaction(configuration -> ContractDAO.delete(ctx, contractIds));
	}

	// -------------------- CONTRACT DATA
	
	public Stream<ContractData> getContractDataStream(AONContext ctx, ContractDataFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ContractDataDAO.getStream(ctx, filter));
	}
	
	// -------------------- CONTRACT DATA
	
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

}
