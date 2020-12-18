package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPayroll;
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
import com.esferalia.aon.occam.impl.jooq.dao.CCCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EmployeeDAO;

public class PayrollImpl implements IPayroll {
	
	
	@Override
	public ContractData[] setContractData(AONContext ctx, String domainName, ContractFilter filter,
			ContractData... contractDatas) {
		return EmployeeDAO.setContractData(ctx, domainName, filter, contractDatas);
	}
	
	@Override
	public Deduction[] setDeductions(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Deduction... deductions) {
		return EmployeeDAO.setDeductions(ctx, domainName, ccc, naf, startDate, endDate, deductions);
	}
	
	// -------------------- EMPLOYEE
	
	@Override
	public Bonus[] setBonuses(AONContext ctx, String domainName, String ccc, String naf, Date startDate, Date endDate, Bonus... bonuses) {
		return EmployeeDAO.setBonuses(ctx, domainName, ccc, naf, startDate, endDate, bonuses);
	}
	
	// -------------------- EMPLOYEE
	
	@Override
	public Employee addEmployee(AONContext ctx, String domainName, Employee employee) {
		return EmployeeDAO.addEmployee(ctx, domainName, employee);
	}

	// -------------------- CONTRACT
	
	public Stream<Contract> getContractStream(AONContext ctx, ContractFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ContractDAO.getContractStream(ctx, filter));
	}

	// -------------------- CONTRACT DATA
	
	public Stream<ContractData> getContractDataStream(AONContext ctx, ContractDataFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ContractDAO.getContractDataStream(ctx, filter));
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
	
	

}
