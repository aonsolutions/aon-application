package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPayroll;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDAO;

public class PayrollImpl implements IPayroll {

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

}
