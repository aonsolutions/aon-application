package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
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
		try (AONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setContractData(ctx, domainName, filter, contractDatas);
		}
	}

	// -------------------- DEDUCTIONS
	
	public static Deduction[] setDeductions(String domainName, Integer domainId, String login, String ccc, String naf, Date startDate, Date endDate, Deduction... deductions) {
		try (AONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setDeductions(ctx, domainName, ccc, naf, startDate, endDate, deductions);
		}
	}

	// -------------------- BONUS
	
	public static Bonus[] setBonuses(String domainName, Integer domainId, String login, String ccc, String naf, Date startDate, Date endDate, Bonus... bonuses) {
		try (AONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().setBonuses(ctx, domainName, ccc, naf, startDate, endDate, bonuses);
		}
	}
	

	// -------------------- EMPLOYEE
	
	public static Employee addEmployee(String domainName, Integer domainId, String login, Employee employee) {
		try (AONContext ctx  = AONContext.getAONContext(domainName, domainId, login) ) {
			return getPayroll().addEmployee(ctx, domainName, employee);
		}
	}
	
	
	// -------------------- CONTRACT
	
	public static Stream<Contract> getContractStream(String domainName, Integer domainId, String login, ContractFilter filter) {
		AONContext ctx = null;
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
		AONContext ctx = null;
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
		AONContext ctx = null;
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
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractDataStream(ctx, filter);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	public static LinkedList<ContractData> getContractDataList(String domainName, Integer domainId, String login, ContractDataFilter filter) {
		AONContext ctx = null;
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
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getContractDataStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
	// -------------------- IRPF DATA
	
	public static Stream<IrpfData> getIrpfDataStream(String domainName, Integer domainId, String login, IrpfDataFilter filter) {
		AONContext ctx = null;
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
		AONContext ctx = null;
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
		AONContext ctx = null;
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
		AONContext ctx = null;
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
		AONContext ctx = null;
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
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPayroll().getCCCStream(ctx);
		} finally {
			if (ctx != null){
				ctx.close();
			}
		}
	}
	
}
