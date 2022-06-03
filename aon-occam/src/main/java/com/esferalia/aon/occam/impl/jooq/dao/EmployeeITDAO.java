package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.LeaveBatch.LEAVE_BATCH;
import static com.esferalia.aon.jooq.tables.LeaveBatchDetail.LEAVE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.ContractLeaveDetailRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.Filter.ContractLeaveFilter;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.EmployeeNotFoundexception;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractLeavePropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class EmployeeITDAO {
	
	private static String[] CONTRACT_DATA_NAMES = {"INICIO_PAGO_DIRECTO", "BASE_REGULADORA", "TIPO_SOLICITANTE_MAT_PAT", "MOTIVO_MAT_PAT", "COEFICIENTE_MATERNIDAD", "COEFICIENTE_PATERNIDAD"};
	
	public static Optional<EmployeeIT> get(AONContext ctx, ContractLeaveFilter filter ) {
		ctx.checkRead();
		List<EmployeeIT> employeeIts = getStream(ctx, filter).collect(Collectors.toList());
		if ( employeeIts.isEmpty() )
			return Optional.empty();
		else if ( employeeIts.size() == 1 )
			return Optional.of(employeeIts.get(0));
		else 
			return Optional.of(employeeIts.get(employeeIts.size()-1));
	}

	public static Stream<EmployeeIT> getStream(AONContext ctx, ContractLeaveFilter filter) {
		ctx.checkRead();
		
		Map<EmployeeIT, List<EmployeeITPart>> employeeITMap = ctx.getDslContext()
		.select()
		.from(CONTRACT_LEAVE)
		.innerJoin(CONTRACT_LEAVE_DETAIL).onKey()
		.innerJoin(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_LEAVE.CONTRACT))
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(REGISTRY).on(PERSON.REGISTRY.eq(REGISTRY.ID))
		.innerJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
		.where(new ContractLeavePropertiesDAO().getConditions(filter)).fetchGroups( r -> 
			new EmployeeIT()
			.setId(r.get(CONTRACT_LEAVE.ID))
			.setDomain(r.get(CONTRACT_LEAVE.DOMAIN))
			.setType( ContractLeaveType.safeValueOf(r.get(CONTRACT_LEAVE.TYPE)) )
			.setContract(r.get(CONTRACT_LEAVE.CONTRACT))
			.setDescription(r.get(CONTRACT_LEAVE.DESCRIPTION))
			.setStartDate(r.get(CONTRACT_LEAVE.START_DATE))
			.setEndDate(r.get(CONTRACT_LEAVE.END_DATE))
			.setDailyCgcBase(r.get(CONTRACT_LEAVE.DAILY_CGC_BASE))
			.setParent(r.get(CONTRACT_LEAVE.PARENT))
			
			.setRegime(getSSRegimeCode(r.get(CONTRACT.SS_REGIME)))
			.setCcc(r.get(ENTERPRISE_CCC.CCC))
			
			.setNss(r.get(PERSON.SOCIAL_SECURITY_NUM))
			.setName(r.get(REGISTRY.NAME))
			.setDni(r.get(REGISTRY.DOCUMENT))
			
			.setDischargeCause( ContractLeaveDischargeCause.safeValueOf(r.get(CONTRACT_LEAVE.DISCHARGE_CAUSE)) )
			, r -> new EmployeeITPart()
				.setId(r.get(CONTRACT_LEAVE_DETAIL.ID))
				.setType(ContractLeaveDetailType.safeValueOf(r.get(CONTRACT_LEAVE_DETAIL.TYPE)))
				.setContractLeave(r.get(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE))
				.setCollegeNumber(r.get(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER))
				.setConfirmOrder(r.get(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER))
				.setCias(r.get(CONTRACT_LEAVE_DETAIL.CIAS))
				.setDate(r.get(CONTRACT_LEAVE_DETAIL.DATE))
				.setStatus(ContractLeaveDetailStatus.safeValueOf(r.get(CONTRACT_LEAVE_DETAIL.STATUS)))
		);
		employeeITMap.forEach((it, details) -> details.forEach( detail -> it.addITPart(detail)) );
		return employeeITMap.keySet().stream();
	}

	public static EmployeeIT [] setEmployeeIT(AONContext ctx, EmployeeIT ...employeeIts) {
		ctx.checkWrite();
		if ( employeeIts == null )
			return new EmployeeIT[0];
		if ( employeeIts.length == 0 )
			return new EmployeeIT[0];
		
		for (EmployeeIT employeeIT : employeeIts) {
			ContractLeaveRecord contractLeaveRecord = setContractLeave(ctx, employeeIT);
			setContractLeaveDetail(ctx, contractLeaveRecord, employeeIT.getITsParts());
			setContractData(ctx, contractLeaveRecord, employeeIT.getContractDatas());
		}
		
		return employeeIts;
	}
	
	public static void removeEmployeeIT(AONContext ctx, Integer contractLeaveId, Integer ...partIds) {
		ctx.checkWrite();
		DSLContext dslContext = ctx.getDslContext();
	
		get(ctx, f->f.getIdProperty().eq(contractLeaveId)).ifPresent(employeeIT->{

			List<EmployeeITPart> parts = employeeIT.getITsParts().stream().filter(part-> Arrays.asList(partIds).contains(part.getId())).collect(Collectors.toList());
			
			Optional<EmployeeITPart> baja =	 parts.stream().filter(x->x.getType().equals(ContractLeaveDetailType.BAJA)).findFirst();
			
			if(baja.isPresent()) {
				removeLeaveBatch(dslContext, employeeIT.getITsParts());
				
				dslContext.batch(	
						dslContext.delete(CONTRACT_LEAVE_DETAIL).where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(employeeIT.getId())),
						dslContext.delete(CONTRACT_LEAVE).where(CONTRACT_LEAVE.ID.eq(employeeIT.getId()))
				).execute();

				removeContractData(dslContext, employeeIT.getContract(), employeeIT.getStartDate());
			} else {
		
				removeLeaveBatch(dslContext, parts);	
				
				dslContext.delete(CONTRACT_LEAVE_DETAIL).where(CONTRACT_LEAVE_DETAIL.ID.in(partIds)).execute();
				
				Optional<EmployeeITPart> alta = parts.stream().filter(x->x.getType().equals(ContractLeaveDetailType.ALTA)).findFirst();
				if(alta.isPresent()) {
					getContractLeave(dslContext, employeeIT).ifPresent(contractLeave->{
						contractLeave.set(CONTRACT_LEAVE.END_DATE, null);
						contractLeave.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, null);
						contractLeave.update();
					});
				}
			}
		});
	}
	
	private static void removeLeaveBatch(DSLContext dslContext, List<EmployeeITPart> parts) {
		Integer[] partIds = parts.stream().map(EmployeeITPart::getId).toArray(Integer[]::new);
		
		Result<Record1<Integer>> leaveBatchIds = dslContext
				.select(LEAVE_BATCH_DETAIL.ID).from(LEAVE_BATCH_DETAIL).where(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL.in(partIds)).fetch();
		
		dslContext.batch(	
			dslContext.delete(LEAVE_BATCH).where(LEAVE_BATCH.ID.in(leaveBatchIds)),
			dslContext.delete(LEAVE_BATCH_DETAIL).where(LEAVE_BATCH_DETAIL.ID.in(partIds))
		).execute();
	}
	
	private static void removeContractData(DSLContext dslContext, Integer contractId, Date startDate) {
		dslContext.delete(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(contractId))
		.and(CONTRACT_DATA.START_DATE.eq(toSql(startDate)))
		.and(CONTRACT_DATA.NAME.in(CONTRACT_DATA_NAMES))
		.execute();
	}
	

	private static ContractLeaveRecord setContractLeave(AONContext ctx, EmployeeIT employeeIt) {
			DSLContext dslContext = ctx.getDslContext();
			Optional<ContractLeaveRecord> exists = getContractLeave(dslContext, employeeIt);
			
			ContractLeaveRecord contractLeaveRecord;
	
			if(exists.isPresent()) {
				contractLeaveRecord = exists.get();
				employeeIt.getDescription().ifPresent(d-> contractLeaveRecord.set(CONTRACT_LEAVE.DESCRIPTION, d) );

				employeeIt.getEndDate().ifPresent(d-> contractLeaveRecord.set(CONTRACT_LEAVE.END_DATE, toSql(d)) );
				
				employeeIt.getParent().ifPresent(d-> contractLeaveRecord.set(CONTRACT_LEAVE.PARENT, d) );
				
				employeeIt.getDailyCgcBase().ifPresent(d-> contractLeaveRecord.set(CONTRACT_LEAVE.DAILY_CGC_BASE, d) );

				 if(null!=employeeIt.getDischargeCause()) 
					 contractLeaveRecord.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, employeeIt.getDischargeCause().value());
				 
				 contractLeaveRecord.update();
			} else {
				java.sql.Date itStartDate = normalizeStartDateToSave(employeeIt.getType(), employeeIt.getStartDate());
				InsertSetMoreStep<ContractLeaveRecord> sets = dslContext
				.insertInto(CONTRACT_LEAVE)
				.set(CONTRACT_LEAVE.DOMAIN, employeeIt.getDomain())
				.set(CONTRACT_LEAVE.TYPE, employeeIt.getType().value())
				.set(CONTRACT_LEAVE.CONTRACT, employeeIt.getContract())
				.set(CONTRACT_LEAVE.START_DATE, itStartDate)
				;
			
				employeeIt.getDescription().ifPresent(d-> sets.set(CONTRACT_LEAVE.DESCRIPTION, d) );

				employeeIt.getEndDate().ifPresent(d-> sets.set(CONTRACT_LEAVE.END_DATE, toSql(d)) );
				
				employeeIt.getParent().ifPresent(d-> sets.set(CONTRACT_LEAVE.PARENT, d) );
				
				employeeIt.getDailyCgcBase().ifPresent(d-> sets.set(CONTRACT_LEAVE.DAILY_CGC_BASE, d) );
				
				 if(null!=employeeIt.getDischargeCause()) {					 
					 sets.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, employeeIt.getDischargeCause().value());
				 }
		
				contractLeaveRecord = sets.returning().fetchOne();
			}		 

		 return contractLeaveRecord;
	}
	
	private static void setContractLeaveDetail(AONContext ctx, ContractLeaveRecord contractLeaveRecord, List<EmployeeITPart> parts) {
		DSLContext dslContext = ctx.getDslContext();
		InsertSetMoreStep<ContractLeaveDetailRecord> insertContractLeaveDetail = null;
		
		List<EmployeeITPart> partsSort= parts.stream().sorted((o1, o2)-> o1.getDate().compareTo(o2.getDate())).collect(Collectors.toList());
		
		for(EmployeeITPart part: partsSort) {

			Optional<ContractLeaveDetailRecord> detailLeave = getContractLeaveDetail(dslContext, contractLeaveRecord, part);
			
			if(detailLeave.isPresent()) {
	
				ContractLeaveDetailRecord contractLeaveDetailRecord = detailLeave.get();
				
				part.getCollegeNumber().ifPresent(c-> contractLeaveDetailRecord.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, c) );
				
				part.getConfirmOrder().ifPresent(c-> contractLeaveDetailRecord.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, c) );
			
				part.getCias().ifPresent(c-> contractLeaveDetailRecord.set(CONTRACT_LEAVE_DETAIL.CIAS, c) );
				
				if(part.getStatus()!=null) 
					contractLeaveDetailRecord.set(CONTRACT_LEAVE_DETAIL.STATUS, part.getStatus().value());
				
				contractLeaveDetailRecord.update();
			} else {
				InsertSetStep<ContractLeaveDetailRecord> insert = 
						null != insertContractLeaveDetail ? insertContractLeaveDetail.newRecord() : dslContext.insertInto(CONTRACT_LEAVE_DETAIL);
						
				InsertSetMoreStep<ContractLeaveDetailRecord> recordSets = insert
				.set(CONTRACT_LEAVE_DETAIL.DOMAIN, contractLeaveRecord.getDomain())
				.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, contractLeaveRecord.getId())
				.set(CONTRACT_LEAVE_DETAIL.TYPE, part.getType().value())
				.set(CONTRACT_LEAVE_DETAIL.DATE, toSql(part.getDate()))
				.set(CONTRACT_LEAVE_DETAIL.STATUS, part.getStatus().value())
				;
				
				part.getCollegeNumber().ifPresent(c-> recordSets.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, c) );
				
				part.getConfirmOrder().ifPresent(c-> recordSets.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, c) );
			
				part.getCias().ifPresent(c-> recordSets.set(CONTRACT_LEAVE_DETAIL.CIAS, c) );
				
				insertContractLeaveDetail = recordSets;
			}
	
		}	
		
		if(null!=insertContractLeaveDetail) insertContractLeaveDetail.execute();
	}
	
	/**
	 * SAVE AND UPDATE CONTRACT_DATA
	 * @param ctx AONContext
	 * @param contractLeaveRecord ContractLeaveRecord
	 * @param contractDatas  List contractDatas(name, expression, startDate, endDate)
	 */
	private static void setContractData(AONContext ctx, ContractLeaveRecord contractLeaveRecord, List<ContractData> contractDatas) {
		if(!contractDatas.isEmpty()) {
			EmployeeDAO.setContractData(ctx, 
				ctx.getDomainName(), 
				f->f.getDomainProperty().eq(contractLeaveRecord.getDomain()).and(f.getIdProperty().eq(contractLeaveRecord.getContract())), 
				contractDatas.toArray(ContractData[]::new)
			);
		}
	}
	
	private static Optional<ContractLeaveRecord> getContractLeave(DSLContext dslContext, EmployeeIT employeeIt) {
		SelectConditionStep<Record> condition = dslContext
		.select()
		.from(CONTRACT_LEAVE)
		.where(CONTRACT_LEAVE.DOMAIN.eq(employeeIt.getDomain()));
		
		if(null!=employeeIt.getId()) {
			condition.and(CONTRACT_LEAVE.ID.eq(employeeIt.getId()));
		} else {
			if(employeeIt.getContract()==null) {
				java.sql.Date itStartDate = normalizeStartDateToSave(employeeIt.getType(), employeeIt.getStartDate());
				ContractRecord contract = getContract(dslContext, employeeIt);
				employeeIt.setContract(contract.getId());
				condition.and(CONTRACT_LEAVE.START_DATE.eq(itStartDate).and(CONTRACT_LEAVE.CONTRACT.eq(employeeIt.getContract())));
			}
//			employeeIt.getEndDate().ifPresent(end-> condition.and(CONTRACT_LEAVE.END_DATE.eq(toSql(end)) ) );
		} 
	    return condition.fetchStreamInto(CONTRACT_LEAVE).findFirst();
	}
	
	private static Optional<ContractLeaveDetailRecord> getContractLeaveDetail(DSLContext dslContext, ContractLeaveRecord contractLeaveRecord, EmployeeITPart part) {
		
		SelectConditionStep<Record> contextDetail = dslContext.select()
		.from(CONTRACT_LEAVE_DETAIL)
		.where(CONTRACT_LEAVE_DETAIL.DOMAIN.eq(contractLeaveRecord.getDomain()));
		
		if(null!=part.getId()) {
			contextDetail.and(CONTRACT_LEAVE_DETAIL.ID.eq(part.getId()));
		} else {
			contextDetail.and(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(contractLeaveRecord.getId()))
			.and(CONTRACT_LEAVE_DETAIL.DATE.eq(toSql(part.getDate())))
			.and(CONTRACT_LEAVE_DETAIL.TYPE.eq(part.getType().value()));
		}
		return contextDetail.fetchStreamInto(CONTRACT_LEAVE_DETAIL).findFirst();
	}
	
	private static ContractRecord getContract(DSLContext dslContext, EmployeeIT employeeIt) {
		java.sql.Date itStartDate = toSql(employeeIt.getStartDate());
		return dslContext
		.select()
		.from(REGISTRY)
		.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
		.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(ENTERPRISE_CCC).onKey()
		.where(ENTERPRISE_CCC.DOMAIN.eq(employeeIt.getDomain()))
		.and(ENTERPRISE_CCC.CCC.eq(employeeIt.getCcc()))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(employeeIt.getNss()))
		.and(CONTRACT.START_DATE.le(itStartDate))
		.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(itStartDate)))
		.orderBy(CONTRACT.START_DATE.asc())
		.fetchStreamInto(CONTRACT).findFirst().orElseThrow(EmployeeNotFoundexception::new);
	}
	
	private static String getSSRegimeCode(Byte ordinal) {
		if ( ordinal == null )
			return null;
		if ( ordinal < 0 )
			return null;
		SSRegimeType types [] = SSRegimeType.values();
		if ( ordinal >= types.length )
			return null;
		
		return types[ordinal].getCode();
	}
	
	private static java.sql.Date normalizeStartDateToSave(ContractLeaveType contractLeaveType, Date date) {
		if(contractLeaveType!=null && contractLeaveType.equals(ContractLeaveType.ACCIDENTE_LABORAL)) 
			date = AonDateUtils.addDays(date, 1);
		
		return toSql(date);
	}	
	
	private static java.sql.Date toSql(Date date) {
		return null!= date ? new java.sql.Date(date.getTime()) : null;
	}
	
}
