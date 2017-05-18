package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.impl.jooq.PayrollImpl;

public class PAYROLL {

	private static IPayroll getPayroll() {
		return new PayrollImpl();
	}

	// ********************************************
	// ********************************* PAYROLL **
	// ********************************************

	
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
	
}
