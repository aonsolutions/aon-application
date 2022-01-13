package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;

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
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.ContractLeaveDetailRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.Filter.ContractLeaveFilter;
import com.esferalia.aon.occam.api.model.payroll.TooManyEmployeesException;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractLeavePropertiesDAO;

public class EmployeeITDAO {
	
//	private static String CONTRACT_DATA_NAMES [] = {"INICIO_PAGO_DIRECTO", "TIPO_SOLICITANTE_MAT_PAT", "MOTIVO_MAT_PAT", "BASE_REGULADORA", "COEFICIENTE_MATERNIDAD", "COEFICIENTE_PATERNIDAD"};
	
	public static Optional<EmployeeIT> get(AONContext aonContext, ContractLeaveFilter filter ) {
		List<EmployeeIT> employeeIts = getStream(aonContext, filter).collect(Collectors.toList());
		if ( employeeIts.isEmpty() )
			return Optional.empty();
		else if ( employeeIts.size() == 1 )
			return Optional.of(employeeIts.get(0));
		else 
			throw new TooManyEmployeesException();
	}

	public static Stream<EmployeeIT> getStream(AONContext aonContext, ContractLeaveFilter filter) {
		DSLContext dslContext = aonContext.getDslContext();
		
		Map<EmployeeIT, List<EmployeeITPart>> employeeITMap = 
		dslContext
		.select()
		.from(CONTRACT_LEAVE)
		.innerJoin(CONTRACT_LEAVE_DETAIL).onKey()
		.where(new ContractLeavePropertiesDAO().getConditions(filter))
		.fetchGroups( r -> 
			new EmployeeIT()
			.setId(r.get(CONTRACT_LEAVE.ID))
			.setDomain(r.get(CONTRACT_LEAVE.DOMAIN))
			.setType( ContractLeaveType.safeValueOf(r.get(CONTRACT_LEAVE.TYPE)) )
			.setContract(r.get(CONTRACT_LEAVE.CONTRACT))
			.setDescription(r.get(CONTRACT_LEAVE.DESCRIPTION))
			.setStartDate(r.get(CONTRACT_LEAVE.START_DATE))
			.setEndDate(r.get(CONTRACT_LEAVE.END_DATE))
			.setDailyCgcBase(r.get(CONTRACT_LEAVE.DAILY_CGC_BASE))
			.setDailyCgpBase(r.get(CONTRACT_LEAVE.DAILY_CGP_BASE))
			.setParent(r.get(CONTRACT_LEAVE.PARENT))
			.setDailyRegBase(r.get(CONTRACT_LEAVE.DAILY_REG_BASE))
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

	public static EmployeeIT[] setEmployeeIT(AONContext aonContext, EmployeeIT... employeeITs) {
		return setEmloyeeITImpl(aonContext.getDslContext(), employeeITs);
	}

	private static EmployeeIT [] setEmloyeeITImpl(DSLContext dslContext, EmployeeIT ...employeeIts) {
		if ( employeeIts == null )
			return new EmployeeIT[0];
		if ( employeeIts.length == 0 )
			return new EmployeeIT[0];
		
		for (EmployeeIT employeeIT : employeeIts) {

			ContractLeaveRecord contractLeaveRecord = setContractLeave(dslContext, employeeIT);
			
			setContractLeaveDetail(dslContext, contractLeaveRecord, employeeIT.getITsParts());
		}
		
		return employeeIts;
	}
	
	private static ContractLeaveRecord setContractLeave(DSLContext dslContext, EmployeeIT employeeIt) {
			SelectConditionStep<Record> condition = dslContext
			.select()
			.from(CONTRACT_LEAVE)
			.where(CONTRACT_LEAVE.DOMAIN.eq(employeeIt.getDomain()));
			
			if(null!=employeeIt.getId()) {
				condition.and(CONTRACT_LEAVE.ID.eq(employeeIt.getId()));
			} else {
				condition.and(CONTRACT_LEAVE.START_DATE.eq(toSql(employeeIt.getStartDate())))
				.and(CONTRACT_LEAVE.CONTRACT.eq(employeeIt.getContract()));
			}

			employeeIt.getEndDate().ifPresent(end-> condition.and(CONTRACT_LEAVE.END_DATE.le(toSql(end)) ) );
		
			ContractLeaveRecord contractLeaveRecord  = condition.fetchOptionalInto(CONTRACT_LEAVE)
			.orElseGet( () ->{
				InsertSetMoreStep<ContractLeaveRecord> sets = dslContext
				.insertInto(CONTRACT_LEAVE)
				.set(CONTRACT_LEAVE.DOMAIN, employeeIt.getDomain())
				.set(CONTRACT_LEAVE.TYPE, employeeIt.getType().value())
				.set(CONTRACT_LEAVE.CONTRACT, employeeIt.getContract())
				.set(CONTRACT_LEAVE.START_DATE, toSql(employeeIt.getStartDate()))
				;
			
				employeeIt.getDescription().ifPresent(d-> sets.set(CONTRACT_LEAVE.DESCRIPTION, d) );

				employeeIt.getEndDate().ifPresent(d-> sets.set(CONTRACT_LEAVE.END_DATE, toSql(d)) );
				
				employeeIt.getParent().ifPresent(d-> sets.set(CONTRACT_LEAVE.PARENT, d) );
				
				employeeIt.getDailyCgcBase().ifPresent(d-> sets.set(CONTRACT_LEAVE.DAILY_CGC_BASE, d) );

				employeeIt.getDailyCgpBase().ifPresent(d-> sets.set(CONTRACT_LEAVE.DAILY_CGP_BASE, d) );

				employeeIt.getDailyRegBase().ifPresent(d-> sets.set(CONTRACT_LEAVE.DAILY_REG_BASE, d) );
				
				return sets.returning().fetchOne();
			} );
		 
		 if(null!=employeeIt.getDischargeCause()) {
				contractLeaveRecord.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, employeeIt.getDischargeCause().value());
				contractLeaveRecord.update();
		 }
		 return contractLeaveRecord;
	}
	
	private static void setContractLeaveDetail(DSLContext dslContext, ContractLeaveRecord contractLeaveRecord, List<EmployeeITPart> its) {


		InsertSetMoreStep<ContractLeaveDetailRecord> insertContractLeaveDetail = null;
		
		List<EmployeeITPart> sort = its.stream().sorted((o1, o2)-> o1.getDate().compareTo(o2.getDate())).collect(Collectors.toList());
		
		for(EmployeeITPart it: sort) {
			
			SelectConditionStep<Record> contextDetail = dslContext.select()
			.from(CONTRACT_LEAVE_DETAIL)
			.where(CONTRACT_LEAVE_DETAIL.DOMAIN.eq(contractLeaveRecord.getDomain()));
			
			if(null!=it.getId()) {
				contextDetail.and(CONTRACT_LEAVE_DETAIL.ID.eq(it.getId()));
			} else {
				contextDetail.and(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(contractLeaveRecord.getId()))
				.and(CONTRACT_LEAVE_DETAIL.DATE.eq(toSql(it.getDate())))
				.and(CONTRACT_LEAVE_DETAIL.TYPE.eq(it.getType().value()));
			}
			
			Optional<ContractLeaveDetailRecord> detailLeave = contextDetail.fetchStreamInto(CONTRACT_LEAVE_DETAIL).findFirst();
			
			if(detailLeave.isPresent()) {
				ContractLeaveDetailRecord contractLeaveDetailRecord = detailLeave.get();
				it.getCollegeNumber().ifPresent(c-> contractLeaveDetailRecord.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, c) );
				
				it.getConfirmOrder().ifPresent(c-> contractLeaveDetailRecord.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, c) );
			
				it.getCias().ifPresent(c-> contractLeaveDetailRecord.set(CONTRACT_LEAVE_DETAIL.CIAS, c) );
				
				contractLeaveDetailRecord.update();
			} else {
				InsertSetStep<ContractLeaveDetailRecord> insert = 
						null != insertContractLeaveDetail ? insertContractLeaveDetail.newRecord() : dslContext.insertInto(CONTRACT_LEAVE_DETAIL);
						
				InsertSetMoreStep<ContractLeaveDetailRecord> recordSets = insert
				.set(CONTRACT_LEAVE_DETAIL.DOMAIN, contractLeaveRecord.getDomain())
				.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, contractLeaveRecord.getId())
				.set(CONTRACT_LEAVE_DETAIL.TYPE, it.getType().value())
				.set(CONTRACT_LEAVE_DETAIL.DATE, toSql(it.getDate()))
				.set(CONTRACT_LEAVE_DETAIL.STATUS, it.getStatus().value())
				;
				
				it.getCollegeNumber().ifPresent(c-> recordSets.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, c) );
				
				it.getConfirmOrder().ifPresent(c-> recordSets.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, c) );
			
				it.getCias().ifPresent(c-> recordSets.set(CONTRACT_LEAVE_DETAIL.CIAS, c) );
				
				insertContractLeaveDetail = recordSets;
			}
	
		}	
		
		if(null!=insertContractLeaveDetail) insertContractLeaveDetail.execute();
	}
	
	
	private static java.sql.Date toSql(Date date) {
		return null!= date ? new java.sql.Date(date.getTime()) : null;
	}

}
