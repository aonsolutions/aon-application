package com.esferalia.aon.occam.impl.jooq.dao;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.*;

import com.esferalia.aon.jooq.tables.Timecontrol;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AuxSalaryInfo;
import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractExtendedDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.Filter.SalaryNewPortalFilter;
import com.esferalia.aon.occam.api.model.Properties.SalaryNewPortalProperties;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.AgreementLevelCategoryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ContractExtendedDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ContractFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ContractSimplifiedDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.IrpfDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.AgreementLevelCategoryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractExtendedDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.IrpfDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SalaryNewPortalPropertiesDAO;

public class ContractDAO {
	
	public static final ContractPropertiesDAO CONTRACT_PROPERTIES = new ContractPropertiesDAO();
	public static final ContractExtendedDataPropertiesDAO CONTRACT_EXTENDED_DATA_PROPERTIES = new ContractExtendedDataPropertiesDAO();
	public static final SalaryNewPortalPropertiesDAO SALARY_NEW_PORTAL_PROPERTIES = new SalaryNewPortalPropertiesDAO();
	public static final ContractDataPropertiesDAO CONTRACT_DATA_PROPERTIES = new ContractDataPropertiesDAO();
	public static final IrpfDataPropertiesDAO IRPF_DATA_PROPERTIES = new IrpfDataPropertiesDAO();
	public static final AgreementLevelCategoryPropertiesDAO AGREEMENT_LEVEL_CATEGORY_PROPERTIES = new AgreementLevelCategoryPropertiesDAO();
	public static final Field<Double> SALARY_CGC_BASE = DSL.field("salary", Double.class);
	public static final Field<Double> MARK_TOTAL_TIME = DSL.field("mark", Double.class);
	public static final Field<String> CONTRACT_TYPE = DSL.field("contract_type", String.class);
	public static final Field<String> PERSON_FULL_NAME = DSL.field("'person_full_name'", String.class);
	
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
	
	public static Stream<ContractExtendedData> getContractSimplifiedData(AONContext ctx, ContractExtendedDataFilter filter,  Integer page, Integer perPage){
		ctx.checkRead();
		return ctx.getDslContext()
				.select(CONTRACT.ID)
				.select(REGISTRY.NAME.as(PERSON_FULL_NAME))
				.select(REGISTRY.DOCUMENT)
				.from(CONTRACT)
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.where(CONTRACT_EXTENDED_DATA_PROPERTIES.getConditions(filter))
				.orderBy(REGISTRY.NAME.asc())
				.limit(perPage).offset(perPage * (page -1))
				.fetch()
				.stream()
				.map(new ContractSimplifiedDataFiller());
	} 
	
	public static long getContractCount(AONContext ctx, ContractExtendedDataFilter filter) {
		return ctx.getDslContext()
				.select(CONTRACT.ID)
				.from(CONTRACT)
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.where(CONTRACT_EXTENDED_DATA_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.count();		
	}
	
	private static Record getWorkplaceRecord(DSLContext dslContext, Integer contractId) {
		return dslContext.select()
				.from(WORKPLACE)
				.where(WORKPLACE.ID.eq(
						dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
						.where(CONTRACT.ID.eq(contractId))))
				.fetchOne();
	}
	
	private static Integer getEnterpriseId(DSLContext dslContext, Integer contractId) {
		return dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
				.where(WORKPLACE.ID.eq(
						dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
				)).fetchOne()
				.get(WORKPLACE.ENTERPRISE);
	}
	
	public static List<AuxSalaryInfo> getEmployeeSalary(AONContext ctx , SalaryNewPortalFilter filter, Integer page, Integer perPage) {
		List<AuxSalaryInfo> salaryList = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		Result<Record> results = ctx.getDslContext().select()
		        .from(SALARY)
		        .where(SALARY_NEW_PORTAL_PROPERTIES.getConditions(filter))
		        .limit(perPage)
				.offset(perPage * (page -1))
		        .fetch();
		 
		 for (Record salaryRecord : results) {
		        AuxSalaryInfo salaryInfo = new AuxSalaryInfo();
		        salaryInfo.setId(salaryRecord.get(SALARY.ID));
				salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
				salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
				Date startDate = salaryRecord.get(SALARY.START_DATE);
		        Date endDate = salaryRecord.get(SALARY.END_DATE);
		        Date issueDate = salaryRecord.get(SALARY.ISSUE_DATE);
		        salaryInfo.setStartDate(dateFormat.format(startDate));
		        salaryInfo.setEndDate(dateFormat.format(endDate));
		        salaryInfo.setIssueDate(dateFormat.format(issueDate));
				salaryInfo.setType(salaryRecord.get(SALARY.TYPE));
				salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
				salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
				salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
				salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
				salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
				
				Integer contractId = salaryRecord.get(SALARY.CONTRACT);
				Integer enterpriseId = getEnterpriseId(ctx.getDslContext(), contractId);
				Record workplaceRecord = getWorkplaceRecord(ctx.getDslContext(), contractId);
				
				String workplaceName = workplaceRecord.get(WORKPLACE.DESCRIPTION);
				Integer workplaceId =  workplaceRecord.get(WORKPLACE.ID);
				
				salaryInfo.setWorkplaceName(workplaceName);
				salaryInfo.setWorkplaceId(workplaceId);
				salaryInfo.setEnterpriseId(enterpriseId);
		    
				salaryList.add(salaryInfo);
		    }

		return salaryList;
	}
	
	public static long getEmployeeSalaryCount(AONContext ctx , SalaryNewPortalFilter filter) {
		return ctx.getDslContext().select()
		        .from(SALARY)
		        .innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
		        .innerJoin(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
		        .where(SALARY_NEW_PORTAL_PROPERTIES.getConditions(filter))
		        .fetch().stream().count();
	}
	
	public static Stream<ContractExtendedData> getContractExtendedDataStream(AONContext ctx, ContractExtendedDataFilter filter, Integer page, Integer perPage){
		ctx.checkRead();
		Field<Integer> dateMiliseconds = DSL.field("UNIX_TIMESTAMP(date)", Integer.class);
		Field<Integer> totalTime = DSL.field("total_time", Integer.class);
		Field<String> rDocument = DSL.field("rDocument", String.class);
		Field<Integer> rnSalary = DSL.field("rnSalary", Integer.class);
		Field<Integer> idSalary = DSL.field("idSalary", Integer.class);
		Field<Double> salaryAmount = DSL.field("salaryAmount", Double.class);
		Field<Integer> rnContractData = DSL.field("rnContractData", Integer.class);
		Field<Integer> idContractData = DSL.field("idContractData", Integer.class);
		Field<String> expressionCD = DSL.field("expressionCD", String.class);
		Table<?> registryTable = REGISTRY.as("registryTable");
		Table<?> salary = SALARY.as("s");
		Table<?> contractData = CONTRACT_DATA.as("cd");
		Table<?> tm = Timecontrol.TIMECONTROL.as("tm");
		
		Select<?> subQ1 = DSL.select(SALARY.CONTRACT.as(idSalary))
				.select(SALARY.CGC_BASE.as(salaryAmount))
				.select(DSL.rowNumber().over(DSL.partitionBy(SALARY.CONTRACT).orderBy(SALARY.END_DATE.desc())).as(rnSalary))
				.from(SALARY);
		
		Select<?> subQ2 = DSL.select(CONTRACT_DATA.CONTRACT.as(idContractData))
				.select(CONTRACT_DATA.EXPRESSION.as(expressionCD))
				.select(DSL.rowNumber().over(DSL.partitionBy(CONTRACT_DATA.CONTRACT).orderBy(CONTRACT_DATA.START_DATE.desc())).as(rnContractData))
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("TC2"));
		
		Select<?> subQ3 = DSL.select(
					DSL.coalesce(
							DSL.sum(DSL.if_(Timecontrol.TIMECONTROL.STATUS.eq((byte) 0), dateMiliseconds.neg(), dateMiliseconds)
						).cast(Double.class), 0).cast(Double.class).as(totalTime)
					)
				.select(registryTable.field(REGISTRY.DOCUMENT).as(rDocument))
				.from(Timecontrol.TIMECONTROL)
				.join(REGISTRY.as(registryTable)).on(registryTable.field(REGISTRY.ID).eq(Timecontrol.TIMECONTROL.TASK_HOLDER))
				.where(
						Timecontrol.TIMECONTROL.DOMAIN.eq(ctx.getDomainId())
						.and(Timecontrol.TIMECONTROL.MODIFICATED_TIMECONTROL.isNull())
						.and(DSL.sql("date >= DATE_FORMAT(NOW() ,'%Y-%m-01') AND date < DATE(NOW())"))
						)
				.groupBy(registryTable.field(REGISTRY.DOCUMENT));
		
		return ctx.getDslContext()
				.select(CONTRACT.ID)
				.select(REGISTRY.NAME.as(PERSON_FULL_NAME))
				.select(CONTRACT.START_DATE)
				.select(CONTRACT.END_DATE)
				.select(REGISTRY.DOCUMENT)
				.select(subQ2.asTable().as(contractData).field(expressionCD).as(CONTRACT_TYPE))
				.select(CONTRACT.DOMAIN)
				.select(CONTRACT.PERSON)
				.select(CONTRACT.WORKPLACE)
				.select(WORKPLACE.DESCRIPTION)
				.select(subQ1.asTable().as(salary).field(salaryAmount).as(SALARY_CGC_BASE))
				.select(subQ3.asTable().as(tm).field(totalTime).as(MARK_TOTAL_TIME))
				.from(CONTRACT)
				.join(WORKPLACE).onKey()
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.leftJoin(subQ1.asTable().as(salary))
					.on(CONTRACT.ID.eq(subQ1.asTable().as(salary).field(idSalary)).and(subQ1.asTable().as(salary).field(rnSalary).eq(1)))
				.leftJoin(subQ2.asTable().as(contractData))
					.on(CONTRACT.ID.eq(subQ2.asTable().as(contractData).field(idContractData)).and(subQ2.asTable().as(contractData).field(rnContractData).eq(1)))
				.leftJoin(subQ3.asTable().as(tm)).on(subQ3.asTable().as(tm).field(rDocument).eq(REGISTRY.DOCUMENT))
				.groupBy(CONTRACT.ID)
				.having(CONTRACT_EXTENDED_DATA_PROPERTIES.getConditions(filter))
				.orderBy(REGISTRY.NAME.asc())
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
		return AGREEMENT_LEVEL_CATEGORY_PROPERTIES.build(ctx.getDslContext().select()
			.from(AGREEMENT_LEVEL_CATEGORY), filter).fetch().stream().map(new AgreementLevelCategoryFiller());		
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




