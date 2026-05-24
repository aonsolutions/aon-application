package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractBatch.CONTRACT_BATCH;
import static com.esferalia.aon.jooq.tables.ContractBatchDetail.CONTRACT_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCalendarEvent.CONTRACT_CALENDAR_EVENT;
import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.IrpfRegularization.IRPF_REGULARIZATION;
import static com.esferalia.aon.jooq.tables.IrpfResult.IRPF_RESULT;
import static com.esferalia.aon.jooq.tables.LeaveBatch.LEAVE_BATCH;
import static com.esferalia.aon.jooq.tables.LeaveBatchDetail.LEAVE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Timecontrol;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractExtendedDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.AgreementLevelCategoryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ContractExtendedDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ContractFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.IrpfDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.AgreementLevelCategoryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractExtendedDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.IrpfDataPropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ContractDAO {
	
	public static final ContractPropertiesDAO CONTRACT_PROPERTIES = new ContractPropertiesDAO();
	public static final ContractExtendedDataPropertiesDAO CONTRACT_EXTENDED_DATA_PROPERTIES = new ContractExtendedDataPropertiesDAO();
	public static final ContractDataPropertiesDAO CONTRACT_DATA_PROPERTIES = new ContractDataPropertiesDAO();
	public static final IrpfDataPropertiesDAO IRPF_DATA_PROPERTIES = new IrpfDataPropertiesDAO();
	public static final AgreementLevelCategoryPropertiesDAO AGREEMENT_LEVEL_CATEGORY_PROPERTIES = new AgreementLevelCategoryPropertiesDAO();
	public static final Field<Double> SALARY_CGC_BASE = DSL.field("salary", Double.class);
	public static final Field<Double> MARK_TOTAL_TIME = DSL.field("mark", Double.class);
	public static final Field<String> CONTRACT_TYPE = DSL.field("contract_type", String.class);
	public static final Field<String> PERSON_FULL_NAME = DSL.field("person_full_name", String.class);
	
	// -------------------- CONTRACT
	
	public static Stream<Contract> getContractStream(AONContext ctx, ContractFilter filter){
		ctx.checkRead();
		return CONTRACT_PROPERTIES.build(
			ctx.getDslContext()
			.select()
			.from(CONTRACT)
			.innerJoin(PERSON).onKey()
			.innerJoin(REGISTRY).onKey()
			.leftOuterJoin(ENTERPRISE_CCC).onKey()
			, filter).fetch().stream().map(new ContractFiller());		
	}

	public static Stream<ContractExtendedData> getContractExtendedDataStream(AONContext ctx, byte[] auth, ContractExtendedDataFilter filter, Integer limit){
		Integer[] userScopes = AuthDAO.getAuthScopes(ctx, auth);
		Integer[] domains = AuthDAO.getAuthDomains(ctx, auth);
		Condition domainsCondition = DOMAIN.ID.in(domains).or(DOMAIN.PARENT.in(domains));
		Condition userScopesDomain = DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.in(userScopes));
		
		// Skip child domains with user's roles redefined & doesn't have PAYROLL_MANAGER role.
		try {
			Integer[] skipDomains  =
			Arrays.stream(AuthDAO.getAuthDomainsUserRoles(ctx, auth))
			.filter(d -> !d.getDomainUserRoles().contains(AonRole.PAYROLL_MANAGER) 
					&& !d.getDomainUserRoles().contains(AonRole.PAYROLL_PORTAL))
			.map(DomainUserRoles::getDomain).map(Domain::getId).toArray(Integer[]::new);
			if (skipDomains.length > 0)
				domainsCondition = domainsCondition.and(DOMAIN.ID.notIn(skipDomains));
		} catch (Exception e) {
			// In case of any error getting user roles, skip filtering by them.
		}
		
		Condition[] filterConditions = CONTRACT_EXTENDED_DATA_PROPERTIES.getConditions(filter);
		
		Condition[] allConditions = Stream
				.concat(Arrays.stream(filterConditions),
						Arrays.stream(new Condition[] { domainsCondition, userScopesDomain }))
				.toArray(Condition[]::new);
		
		return ctx.getDslContext()
				.select(DOMAIN.fields())
				.select(CONTRACT.fields())
				.select(PERSON.fields())
				.select(REGISTRY.fields())
				.select(WORKPLACE.DESCRIPTION)
				.select(REGISTRY.NAME.as(PERSON_FULL_NAME))
				.select(DSL.inline(0d).as(SALARY_CGC_BASE))
				.select(DSL.inline(0d).as(MARK_TOTAL_TIME)
						)
				.select(DSL.inline("000").as(CONTRACT_TYPE)
						)
				.from(CONTRACT)
				.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
				.innerJoin(REGISTRY).on(CONTRACT.PERSON.eq(REGISTRY.ID))
				.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))
				.where(allConditions)
				.limit(limit).offset(0)
				.fetch().stream().map(new ContractExtendedDataFiller());
		
		//return getContractExtendedDataStream(ctx, filter, 1, limit, domainsCondition, userScopesDomain);
	}
	

	public static Stream<ContractExtendedData> getContractExtendedDataStream(AONContext ctx, ContractExtendedDataFilter filter, Integer page, Integer perPage, Condition ...customConditions){
		ctx.checkRead();
		
		Condition[] filterConditions = CONTRACT_EXTENDED_DATA_PROPERTIES.getConditions(filter);
		Condition[] allConditions = Stream.concat(Arrays.stream(filterConditions), Arrays.stream(customConditions))
				.toArray(Condition[]::new);
		
		Field<Integer> dateMiliseconds = DSL.field("UNIX_TIMESTAMP(date)", Integer.class);		
		Table<?> registryTable = REGISTRY.as("registryTable");
		return ctx.getDslContext()
				.select(DOMAIN.fields())
				.select(CONTRACT.fields())
				.select(PERSON.fields())
				.select(REGISTRY.fields())
				.select(WORKPLACE.DESCRIPTION)
				.select(REGISTRY.NAME.as(PERSON_FULL_NAME))
				.select(DSL.select(DSL.sum(SALARY.CGC_BASE).cast(Double.class))
						.from(SALARY)
						.where(SALARY.CONTRACT.eq(CONTRACT.ID))
						.and(SALARY.TYPE.eq(SalaryType.SALARY.value()))
						.groupBy(SALARY.END_DATE)
						.orderBy(SALARY.END_DATE.desc())
						.limit(1).asField().as(SALARY_CGC_BASE)
						)
				.select(DSL.select(DSL.coalesce(DSL.sum(DSL.if_(Timecontrol.TIMECONTROL.STATUS.eq((byte) 1), dateMiliseconds, dateMiliseconds.neg())).cast(Double.class), 0).cast(Double.class))
						.from(Timecontrol.TIMECONTROL)
						.innerJoin(registryTable).on(registryTable.field(REGISTRY.ID).eq(Timecontrol.TIMECONTROL.TASK_HOLDER))
						.where("date BETWEEN DATE_FORMAT(NOW() ,'%Y-%m-01') AND LAST_DAY(NOW())")
						.and(registryTable.field(REGISTRY.DOCUMENT).eq(REGISTRY.DOCUMENT))
						.asField().as(MARK_TOTAL_TIME)
						)
				.select(DSL.select(CONTRACT_DATA.EXPRESSION)
						.from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(CONTRACT.ID))
						.and(CONTRACT_DATA.NAME.like("TC2"))
						.orderBy(CONTRACT_DATA.START_DATE.desc())
						.limit(1).asField().as(CONTRACT_TYPE)
						)
//				.select(CONTRACT_DATA.EXPRESSION.as(CONTRACT_TYPE))
				.from(CONTRACT)
				.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
				.innerJoin(REGISTRY).on(CONTRACT.PERSON.eq(REGISTRY.ID))
				.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))
				.having(allConditions)
				.orderBy(PERSON_FULL_NAME.asc())
				.limit(perPage).offset(perPage * (page -1))
				.fetch().stream().map(new ContractExtendedDataFiller());
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
		return ctx.getDslContext().select()
				.from(AGREEMENT_LEVEL_CATEGORY)
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL_CATEGORY_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.map(new AgreementLevelCategoryFiller());
	}
	
	public static Stream<AgreementLevelCategory> getAgreementLevelCategoryStream(AONContext ctx, Integer domainId, Integer parentDomain, Integer year){
		ctx.checkRead();
		
		Date startDate = AonDateUtils.getYearFirstDay(year);
		Date endDate = AonDateUtils.getYearLastDay(year);
		
		Result<Record> select = ctx.getDslContext().select()
			.from(AGREEMENT_LEVEL_CATEGORY)
			.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
			.join(CONTRACT).on(
					CONTRACT.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL)
					.and(CONTRACT.START_DATE.le(AonDateUtils.toSql(endDate)))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(AonDateUtils.toSql(startDate))))
					.and(CONTRACT.DOMAIN.eq(domainId))
			)
			.and(AGREEMENT_LEVEL_CATEGORY.DOMAIN.eq(domainId).or(AGREEMENT_LEVEL_CATEGORY.DOMAIN.eq(parentDomain)))
			.fetch();
		
		return select
			.stream()
			.map(new AgreementLevelCategoryFiller());
	}
	
	public static void delete(AONContext ctx, Integer ...contractIds){
		ctx.checkWrite();
		DSLContext dslContext = ctx.getDslContext();

		Result<Record1<Integer>> certifica2DetailSelect = dslContext
				.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
				.from(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(contractIds)).fetch();
		
		Result<Record1<Integer>> batchDetailSelect = dslContext
				.select(CONTRACT_BATCH_DETAIL.CONTRACT_BATCH)
				.from(CONTRACT_BATCH_DETAIL)
				.where(CONTRACT_BATCH_DETAIL.CONTRACT.in(contractIds)).fetch();
		
		Result<Record1<Integer>> leaveBatchDetail = dslContext
				.select(LEAVE_BATCH_DETAIL.LEAVE_BATCH)
				.from(LEAVE_BATCH_DETAIL
						.join(CONTRACT_LEAVE_DETAIL)
						.on(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL
								.eq(CONTRACT_LEAVE_DETAIL.ID))
						.join(CONTRACT_LEAVE)
						.on(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.in(contractIds)))
				.fetch();


		dslContext.batch(	
				// ----------------------------------IRPF-------------------------------------------
				dslContext.delete(IRPF_DATA_ASCENDANTS).using(IRPF_DATA_ASCENDANTS.innerJoin(IRPF_DATA).onKey()).where(IRPF_DATA.CONTRACT.in(contractIds)),
				dslContext.delete(IRPF_DATA_DESCENDIENTS).using(IRPF_DATA_DESCENDIENTS.innerJoin(IRPF_DATA).onKey()).where(IRPF_DATA.CONTRACT.in(contractIds)),
				dslContext.delete(IRPF_REGULARIZATION).where(IRPF_REGULARIZATION.CONTRACT.in(contractIds)),
				dslContext.delete(IRPF_RESULT).where(IRPF_RESULT.CONTRACT.in(contractIds)),
				dslContext.delete(IRPF_DATA).where(IRPF_DATA.CONTRACT.in(contractIds)),
				// ----------------------------------Salary------------------------------------------
				dslContext.delete(SALARY_DATA).using(SALARY_DATA.innerJoin(SALARY).onKey()).where(SALARY.CONTRACT.in(contractIds)),
				dslContext.delete(SALARY_COST).using(SALARY_COST.innerJoin(SALARY).onKey()).where(SALARY.CONTRACT.in(contractIds)),
				dslContext.delete(SALARY_BONUS).using(SALARY_BONUS.innerJoin(SALARY).onKey()).where(SALARY.CONTRACT.in(contractIds)),
				dslContext.delete(SALARY_EMBARGO).using(SALARY_EMBARGO.innerJoin(SALARY).onKey()).where(SALARY.CONTRACT.in(contractIds)),
				dslContext.delete(SALARY_PAYMENT).using(SALARY_PAYMENT.innerJoin(SALARY).onKey()).where(SALARY.CONTRACT.in(contractIds)),
				dslContext.delete(SALARY_DEDUCTION).using(SALARY_DEDUCTION.innerJoin(SALARY).onKey()).where(SALARY.CONTRACT.in(contractIds)),
				dslContext.delete(SALARY).where(SALARY.CONTRACT.in(contractIds)),				
				// --------------------------------CERTIFICA2_BATCH------------------------------------
				dslContext.delete(CERTIFICA2_BATCH_DETAIL).where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(contractIds)),
				dslContext.delete(CERTIFICA2_BATCH).where(CERTIFICA2_BATCH_DETAIL.ID.in(certifica2DetailSelect)),
				 // ----------------------------------LEAVE_BATCH--------------------------------------
				dslContext.delete(LEAVE_BATCH_DETAIL).where(LEAVE_BATCH_DETAIL.LEAVE_BATCH.in(leaveBatchDetail)),
				dslContext.delete(LEAVE_BATCH).where(LEAVE_BATCH.ID.in(leaveBatchDetail)),
				// ----------------------------------CONTRACT------------------------------------------
				dslContext.delete(CONTRACT_BATCH).where(CONTRACT_BATCH.ID.in(batchDetailSelect)),
				dslContext.delete(CONTRACT_LEAVE_DETAIL).using(CONTRACT_LEAVE_DETAIL.innerJoin(CONTRACT_LEAVE).onKey()).where(CONTRACT_LEAVE.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_ATTACH).where(CONTRACT_ATTACH.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_BATCH_DETAIL).where(CONTRACT_BATCH_DETAIL.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_BONUS).where(CONTRACT_BONUS.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_CALENDAR_EVENT).where(CONTRACT_CALENDAR_EVENT.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_CLAUSE).where(CONTRACT_CLAUSE.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_DEDUCTION).where(CONTRACT_DEDUCTION.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_EMBARGO).where(CONTRACT_EMBARGO.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_INFO).where(CONTRACT_INFO.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_INFO).where(CONTRACT_INFO.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT_PAYMENT).where(CONTRACT_PAYMENT.CONTRACT.in(contractIds)),
				dslContext.delete(CONTRACT).where(CONTRACT.ID.in(contractIds))
		).execute();
	}
}




