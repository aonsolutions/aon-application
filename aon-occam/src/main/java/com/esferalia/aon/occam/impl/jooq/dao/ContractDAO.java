package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import java.sql.Date;
import java.util.stream.Stream;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.AgreementLevelCategoryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ContractFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.IrpfDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.AgreementLevelCategoryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.IrpfDataPropertiesDAO;

public class ContractDAO {
	
	public static final ContractPropertiesDAO CONTRACT_PROPERTIES = new ContractPropertiesDAO();
	public static final ContractDataPropertiesDAO CONTRACT_DATA_PROPERTIES = new ContractDataPropertiesDAO();
	public static final IrpfDataPropertiesDAO IRPF_DATA_PROPERTIES = new IrpfDataPropertiesDAO();
	public static final AgreementLevelCategoryPropertiesDAO AGREEMENT_LEVEL_CATEGORY_PROPERTIES = new AgreementLevelCategoryPropertiesDAO();

	
	// -------------------- CONTRACT
	
	public static Stream<Contract> getContractStream(AONContext ctx, ContractFilter filter){
		ctx.checkRead();
		return CONTRACT_PROPERTIES.build(
			ctx.getDslContext()
			.select()
			.from(CONTRACT)
			.innerJoin(PERSON).onKey()
			.innerJoin(REGISTRY).onKey()
			.innerJoin(ENTERPRISE_CCC).onKey()
			, filter).fetch().stream().map(new ContractFiller());		
	}
	
	// -------------------- IRPF DATA
	
	public static Stream<IrpfData> getIrpfDataStream(AONContext ctx, IrpfDataFilter filter){
		ctx.checkRead();
		return IRPF_DATA_PROPERTIES.build(ctx.getDslContext().select()
			.from(IRPF_DATA), filter).fetch().stream().map(new IrpfDataFiller());		
	}
	
	// -------------------- AGREEMENT LEVEL CATEGORY
	
	public static Stream<AgreementLevelCategory> getAgreementLevelCategoryStream(AONContext ctx, AgreementLevelCategoryFilter filter){
		ctx.checkRead();
		return AGREEMENT_LEVEL_CATEGORY_PROPERTIES.build(ctx.getDslContext().select()
			.from(AGREEMENT_LEVEL_CATEGORY), filter).fetch().stream().map(new AgreementLevelCategoryFiller());		
	}
	
	public static Contract save(AONContext ctx, Contract contract) {
		ctx.checkWrite();
		return contract.getId()!=null ? update(ctx, contract) : insert(ctx, contract);
	}
	
	
	public static Contract insert(AONContext ctx, Contract contract) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext().insertInto(CONTRACT)
		.set(CONTRACT.DOMAIN, contract.getDomain())
		.set(CONTRACT.PERSON, contract.getPerson())
		.set(CONTRACT.START_DATE, converDateSql(contract.getStartDate()) ) 
		.set(CONTRACT.END_DATE, contract.getEndDate() !=null ? converDateSql(contract.getEndDate()) : null)
		.set(CONTRACT.SENIORITY_DATE, contract.getSeniorityDate() !=null ? converDateSql(contract.getSeniorityDate()) : null)
		.set(CONTRACT.CATEGORY_DESCRIPTION, contract.getCategoryDescription())
		.set(CONTRACT.AGREEMENT_LEVEL,  contract.getAgreementLevel())
		.returning(CONTRACT.ID).fetchOne().getId();
		contract.setId(id);
		ctx.log().debug("INSERT CONTRACT id "+ contract.getId());		
		return contract;
	}
	
	public static Contract update(AONContext ctx, Contract contract) {
		ctx.checkWrite();
		ctx.getDslContext().update(CONTRACT)
			.set(CONTRACT.DOMAIN, contract.getDomain())
			.set(CONTRACT.PERSON, contract.getPerson())
			.set(CONTRACT.START_DATE, converDateSql(contract.getStartDate()) ) 
			.set(CONTRACT.END_DATE, contract.getEndDate() !=null ? converDateSql(contract.getEndDate()) : null)
			.set(CONTRACT.SENIORITY_DATE, contract.getSeniorityDate() !=null ? converDateSql(contract.getSeniorityDate()) : null)
			.set(CONTRACT.CATEGORY_DESCRIPTION, contract.getCategoryDescription())
			.set(CONTRACT.AGREEMENT_LEVEL,  contract.getAgreementLevel())
			.where(CONTRACT.ID.eq(contract.getId()))
			.execute();
		ctx.log().debug("UPDATE CIBTRACT id "+ contract.getId());		
		return contract;
	}
	
	private static Date converDateSql(java.util.Date date) {
	    return date != null ? new Date(date.getTime()) : null;
	}

}




