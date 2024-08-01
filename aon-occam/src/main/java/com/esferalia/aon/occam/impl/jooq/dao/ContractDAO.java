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

import com.esferalia.aon.jooq.tables.Auth;
import com.esferalia.aon.jooq.tables.TaskHolder;
import com.esferalia.aon.jooq.tables.Timecontrol;
import com.esferalia.aon.jooq.tables.User;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AuxSalaryInfo;
import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractExtendedDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.Filter.SalaryNewPortalFilter;
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
		DSLContext dslContext = ctx.getDslContext();
		dslContext.settings().withRenderGroupConcatMaxLenSessionVariable(false);
		return dslContext
				.select(CONTRACT.ID)
				.select(CONTRACT.PERSON)
				.select(REGISTRY.ID)
				.select(REGISTRY.NAME.as(PERSON_FULL_NAME))
				.select(REGISTRY.DOCUMENT)
				.select(DSL.groupConcat(CONTRACT.ID).as("contract_ids"))
				.from(CONTRACT)
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.where(CONTRACT_EXTENDED_DATA_PROPERTIES.getConditions(filter))
				.groupBy(CONTRACT.PERSON)
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
				.join(WORKPLACE).onKey()
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
		SelectSeekStep2<Record, java.sql.Date, Integer> results = ctx.getDslContext().select()
		        .from(SALARY)
		        .where(SALARY_NEW_PORTAL_PROPERTIES.getConditions(filter))
		        .orderBy(SALARY.START_DATE.desc(), SALARY.ID.desc());
		
		if(page != null && perPage != null)
			results.limit(perPage)
			.offset(perPage * (page -1));
		results.fetch();
		 
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
		Field<Long> totalTime = DSL.field("total_time", Long.class);
		Field<Integer> rnSalary = DSL.field("rnSalary", Integer.class);
		Field<Integer> idSalary = DSL.field("idSalary", Integer.class);
		Field<Double> salaryAmount = DSL.field("salaryAmount", Double.class);
		Field<Integer> rnContractData = DSL.field("rnContractData", Integer.class);
		Field<Integer> idContractData = DSL.field("idContractData", Integer.class);
		Field<String> expressionCD = DSL.field("expressionCD", String.class);
		List<ContractExtendedData> arrayContracts = new ArrayList<>();
		List<Integer> ids = new ArrayList<>();
		List<String> documents = new ArrayList<>();
		
		ctx.getDslContext()
			.select(CONTRACT.ID)
			.select(REGISTRY.NAME.as(PERSON_FULL_NAME))
			.select(CONTRACT.START_DATE)
			.select(CONTRACT.END_DATE)
			.select(REGISTRY.DOCUMENT)
			.select(CONTRACT.DOMAIN)
			.select(CONTRACT.PERSON)
			.select(CONTRACT.WORKPLACE)
			.select(WORKPLACE.DESCRIPTION)
			.from(CONTRACT)
			.join(WORKPLACE).onKey()
			.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
			.where(CONTRACT_EXTENDED_DATA_PROPERTIES.getConditions(filter))
			.orderBy(REGISTRY.NAME.asc())
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().forEach( (r) -> {
				arrayContracts.add(new ContractExtendedDataFiller().apply(r));
				ids.add(r.get(CONTRACT.ID));
				documents.add(r.get(REGISTRY.DOCUMENT));
			});
			
		
		CommonTableExpression<Record> cteSalary = DSL.name("cte").as(DSL.select(SALARY.CONTRACT.as(idSalary))
				.select(SALARY.TOTAL_PAYMENT.as(salaryAmount))
				.select(DSL.rowNumber().over(DSL.partitionBy(SALARY.CONTRACT).orderBy(SALARY.END_DATE.desc())).as(rnSalary))
				.from(SALARY).where(SALARY.CONTRACT.in(ids)));
		
		ctx.getDslContext().with(cteSalary)
			.select(cteSalary.field(idSalary),cteSalary.field(salaryAmount))
			.from(cteSalary)
			.where(cteSalary.field(rnSalary).eq(1)).fetch().stream().forEach(r -> {
				arrayContracts.forEach(e -> {
					if(r.getValue(cteSalary.field(idSalary)).intValue() == e.getId().intValue())
						e.setGrossSalaryLastMonth(r.getValue(cteSalary.field(salaryAmount)));
				});
			});
		
		CommonTableExpression<Record> cteType = DSL.name("cte").as(DSL.select(CONTRACT_DATA.CONTRACT.as(idContractData))
				.select(CONTRACT_DATA.EXPRESSION.as(expressionCD))
				.select(DSL.rowNumber().over(DSL.partitionBy(CONTRACT_DATA.CONTRACT).orderBy(CONTRACT_DATA.START_DATE.desc())).as(rnContractData))
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("TC2").and(CONTRACT_DATA.CONTRACT.in(ids))));
		
		ctx.getDslContext().with(cteType)
			.select(cteType.field(expressionCD), cteType.field(rnContractData), cteType.field(idContractData))
			.from(cteType)
			.where(cteType.field(rnContractData).eq(1))
			.fetch().stream().forEach(r -> {
				arrayContracts.forEach(e -> {
					if(r.getValue(cteType.field(idContractData)).intValue() == e.getId().intValue()) {
						e.setContractType(r.getValue(cteType.field(expressionCD)));
					}
				});				
			});
			
		ctx.getDslContext().select(
				DSL.coalesce(
						DSL.sum(DSL.if_(Timecontrol.TIMECONTROL.STATUS.eq((byte) 0), dateMiliseconds.neg(), dateMiliseconds)
								).cast(Long.class), 0).cast(Long.class).as(totalTime)
				)
		.select(Auth.AUTH.DOCUMENT)
		.from(Timecontrol.TIMECONTROL)
		.join(TaskHolder.TASK_HOLDER).on(TaskHolder.TASK_HOLDER.REGISTRY.eq(Timecontrol.TIMECONTROL.TASK_HOLDER))
		.join(User.USER).on(User.USER.ID.eq(TaskHolder.TASK_HOLDER.USER_ID))
		.join(Auth.AUTH).on(Auth.AUTH.ID.eq(User.USER.AUTH))
		.where(
				Timecontrol.TIMECONTROL.DOMAIN.eq(ctx.getDomainId())
				.and(Timecontrol.TIMECONTROL.MODIFICATED_TIMECONTROL.isNull())
				.and(DSL.sql("date >= DATE_FORMAT(NOW() ,'%Y-%m-01') AND date < DATE(NOW())"))
				.and(Auth.AUTH.DOCUMENT.in(documents))
				)
		.groupBy(Auth.AUTH.DOCUMENT).fetch().stream().forEach(r -> {
			arrayContracts.forEach(e -> {
				if(r.getValue(Auth.AUTH.DOCUMENT).equals(e.getPersonDocument())) {
					e.setTotalMarksLastMonth(r.getValue(totalTime).doubleValue());
				}				
			});
		});
		
		return arrayContracts.stream();
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
	
	
	public static ContractExtendedData getContractById(AONContext ctx, ContractExtendedDataFilter filter, Integer contractId) {
	   ContractExtendedData contract = new ContractExtendedData();
	   ctx.getDslContext()
			   .select().from(CONTRACT)
			   .join(CONTRACT_DATA)
			   .on(CONTRACT.ID.eq(CONTRACT_DATA.CONTRACT))
			   .join(PERSON)
			   .on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
			   .join(REGISTRY)
			   .on(CONTRACT.PERSON.eq(REGISTRY.ID))
			   .where(CONTRACT.DOMAIN.eq(ctx.getDomainId()).and(CONTRACT.ID.eq(contractId))).groupBy(CONTRACT.ID).fetch().stream().forEach(r ->{
				   	Record mainRecord = r;
				   	contract.setId(mainRecord.getValue(CONTRACT.ID));
			        contract.setPersonDocument(mainRecord.getValue(REGISTRY.DOCUMENT));
			        contract.setPersonName(mainRecord.getValue(PERSON.NAME));
			        contract.setPersonFirstName(mainRecord.getValue(PERSON.FIRST_SURNAME));
			        contract.setPersonSecondName(mainRecord.getValue(PERSON.SECOND_SURNAME));
			        contract.setPersonSsNumber(mainRecord.getValue(PERSON.SOCIAL_SECURITY_NUM));
			        contract.setWorkplace(mainRecord.getValue(CONTRACT.WORKPLACE));
			        contract.setEnterpriseCCC(Integer.toString(mainRecord.getValue(CONTRACT.ENTERPRISE_CCC)));
			        contract.setStartDate(mainRecord.getValue(CONTRACT.START_DATE));
			        contract.setEndDate(mainRecord.getValue(CONTRACT.END_DATE));
			        contract.setCategoryDescription((mainRecord.getValue(CONTRACT.CATEGORY_DESCRIPTION)));
			   });
	    ctx.getDslContext()
	    		.select(CONTRACT_DATA.NAME)
	    		.select(CONTRACT_DATA.EXPRESSION)
	    		.from(CONTRACT_DATA)
	    		.where(CONTRACT_DATA.DOMAIN.eq(ctx.getDomainId())
	    		.and(CONTRACT_DATA.CONTRACT.eq(contractId))
	    		.and(CONTRACT_DATA.NAME.in("TC2","GRUPO_COTIZACION","OCUPACION","RLCE","COLECTIVO_TRABAJADORES","CNO"))
	    		).orderBy(CONTRACT_DATA.START_DATE.asc()).fetch().stream().forEach(r -> {
	    			if(r.getValue(CONTRACT_DATA.NAME).equals("TC2")) {
	    				contract.setContractType(r.getValue(CONTRACT_DATA.EXPRESSION));
	    			}else if(r.getValue(CONTRACT_DATA.NAME).equals("GRUPO_COTIZACION")) {
	    				contract.setQuoteGroup(r.getValue(CONTRACT_DATA.EXPRESSION));
	    			}else if(r.getValue(CONTRACT_DATA.NAME).equals("OCUPACION")) {
	    				contract.setOccupation(r.getValue(CONTRACT_DATA.EXPRESSION));
	    			}else if(r.getValue(CONTRACT_DATA.NAME).equals("RLCE")) {
	    				contract.setRlce(r.getValue(CONTRACT_DATA.EXPRESSION));
	    			}else if(r.getValue(CONTRACT_DATA.NAME).equals("COLECTIVO_TRABAJADORES")) {
	    				contract.setWorkerCollective(r.getValue(CONTRACT_DATA.EXPRESSION));
	    			}else if(r.getValue(CONTRACT_DATA.NAME).equals("CNO")) {
	    				contract.setCno(r.getValue(CONTRACT_DATA.EXPRESSION));
	    			}
	    		});
	    return contract;
		}
	}




