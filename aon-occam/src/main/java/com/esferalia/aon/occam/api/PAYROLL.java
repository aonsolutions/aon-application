package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Cost;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Domain;
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
import com.esferalia.aon.occam.impl.jooq.PayrollImpl;

public class PAYROLL {

	private static IPayroll getPayroll() {
		return new PayrollImpl();
	}

	// ********************************************
	// ********************************* PAYROLL **
	// ********************************************

	// -------------------- CONTRACT DATA
	
	public static ContractData[] setContractData(String domainName, Integer domainId, String login, ContractFilter filter, ContractData... contractDatas) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setContractData(ctx, domainName, filter, contractDatas);
		}
	}

	public static ContractData[] setData(String domainName, Integer domainId, String login, String ccc, String naf, Date startDate, Date endDate, ContractData... contractDatas) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setData(ctx, domainName, ccc, naf, startDate, endDate, contractDatas);
		}
	}

	public static ContractData[] getData(String domainName, Integer domainId, String login, Integer contractId) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().getData(ctx, domainName, contractId);
		}
	}

	// -------------------- DEDUCTIONS
	
	public static Deduction[] getDeductions(String domainName, Integer domainId, String login, Integer contractId) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().getDeductions(ctx, domainName, contractId);
		}
	}

	public static Deduction[] setDeductions(String domainName, Integer domainId, String login, String ccc, String naf, Date startDate, Date endDate, Deduction... deductions) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setDeductions(ctx, domainName, ccc, naf, startDate, endDate, deductions);
		}
	}

	// -------------------- COSTS
	
	public static Cost[] getCosts(String domainName, Integer domainId, String login, Integer contractId) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().getCosts(ctx, domainName, contractId);
		}
	}

	public static Cost[] setCosts(String domainName, Integer domainId, String login, String ccc, String naf, Date startDate, Date endDate, Cost... costs) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setCosts(ctx, domainName, ccc, naf, startDate, endDate, costs);
		}
	}

	// -------------------- BONUS
	
	public static Bonus[] getBonuses(String domainName, Integer domainId, String login, Integer contractId) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().getBonuses(ctx, domainName, contractId);
		}
	}

	public static Bonus[] setBonuses(String domainName, Integer domainId, String login, String ccc, String naf, Date startDate, Date endDate, Bonus... bonuses) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setBonuses(ctx, domainName, ccc, naf, startDate, endDate, bonuses);
		}
	}
	

	// -------------------- EMPLOYEE
	
	public static Employee addEmployee(String domainName, Integer domainId, String login, Employee employee) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().addEmployee(ctx, domainName, employee);
		}
	}
	
	public static Optional<Employee> getEmployee(String domainName, Integer domainId, String login, EmployeeFilter filter ) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getEmployee(ctx, filter);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static Stream<Employee> getEmployees(String domainName, Integer domainId, String login, EmployeeFilter filter ) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getEmployees(ctx, filter);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}

	// -------------------- CONTRACT
	
	public static Stream<Contract> getContractStream(String domainName, Integer domainId, String login, ContractFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractStream(ctx, filter);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static LinkedList<Contract> getContractList(String domainName, Integer domainId, String login, ContractFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static Optional<Contract> getContract(String domainName, Integer domainId, String login, ContractFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	// -------------------- CONTRACT DATA
	
	public static Stream<ContractData> getContractDataStream(String domainName, Integer domainId, String login, ContractDataFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractDataStream(ctx, filter);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static LinkedList<ContractData> saveContractData(String domainName, Integer domainId, String login, ContractData ...contractData) {
		try( CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login) ){
			return getPayroll().saveContractData(ctx, contractData);
		} 
	}
	
	public static LinkedList<ContractData> getContractDataList(String domainName, Integer domainId, String login, ContractDataFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractDataStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static Optional<ContractData> getContractData(String domainName, Integer domainId, String login, ContractDataFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractDataStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static void deleteContracts(Domain domain, String login, Integer ...contractIds) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getPayroll().deleteContracts(ctx, contractIds);
		}
	}
	
	
	
	// -------------------- CONTRACT ATTACH
	
	public static Stream<ContractAttach> getContractAttachStream(Domain domain, String login, ContractAttachFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getPayroll().getContractAttachStream(ctx, filter);
		}
	}
	
	public static ContractAttach getContractAttach(Domain domain, String login, ContractAttachFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getPayroll().getContractAttach(ctx, filter);
		}
	}
	public static ContractAttach saveContractAttach(String domainName, Integer domainId, String login, ContractAttach attach) {
		try( CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login) ){
			return getPayroll().saveContractAttach(ctx, attach);
		} 
	}
	
	public static void deleteContractAttach(Domain domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getPayroll().deleteContractAttach(ctx, id);
		}
	}
	
	// -------------------- IRPF DATA
	
	public static Stream<IrpfData> getIrpfDataStream(String domainName, Integer domainId, String login, IrpfDataFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getIrpfDataStream(ctx, filter);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static LinkedList<IrpfData> getIrpfDataList(String domainName, Integer domainId, String login, IrpfDataFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getIrpfDataStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static Optional<IrpfData> getIrpfData(String domainName, Integer domainId, String login, IrpfDataFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getIrpfDataStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static Stream<AgreementLevelCategory> getAgreementLevelCategoryStream(String domainName, Integer domainId, String login, AgreementLevelCategoryFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getAgreementLevelCategoryStream(ctx, filter);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	public static Optional<AgreementLevelCategory> getAgreementLevelCategory(String domainName, Integer domainId, String login, AgreementLevelCategoryFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getAgreementLevelCategoryStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	//CCC
	public static Stream<CCCInfo> getCCCStream(String domainName, Integer domainId, String login) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getCCCStream(ctx);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	// -------------------- ENTERPRISE

	public static Enterprise getEnterprise(String domainName, Integer domainId, String login, EnterpriseFilter filter) {
		try( CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login) ){
			return getPayroll().getEnterprise(ctx, filter);
		}
	}
	
	public static void saveEnterprise(String domainName, Integer domainId, String login, Enterprise enterprise) {
		try( CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login) ){
			getPayroll().saveEnterprise(ctx, enterprise);
		}
	}
	
}
