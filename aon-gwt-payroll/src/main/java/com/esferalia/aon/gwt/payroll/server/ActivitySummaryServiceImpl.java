package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.client.ActivitySummaryService;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.enumeration.SalaryType;


@SuppressWarnings("serial")
public class ActivitySummaryServiceImpl extends AonRemoteServiceServlet implements ActivitySummaryService {
	
	@Override
	public Integer getParentDomain() {		
		try {
			initFacesContext();			
			return getParentDomainID();
		} finally {
			releaseFacesContext();
		}
	}
	
	@Override
	public List<ActivitySummaryObject> getActivitySummary(Integer domainId,
			Date startDate, Date endDate, Boolean onlyStarts, Boolean onlyEnds) {
		
		initFacesContext();

		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		String domainName = domainSwitcher.getDomainNameURL();
		
		Map<Integer, ActivitySummaryObject> summaryMap = null;
		Map<Integer, ActivitySummaryObject> salaryMap = null;
		Map<Integer, ActivitySummaryObject> itMap = null;
		if(domainSwitcher.isParentDomain() && domainId==null){
			domainId = domainSwitcher.getDomainId();
			try {
				Integer[] childDomains = getChildDomainIDs(domainId);
				summaryMap = getSummaryEnterprise(childDomains, domainId, domainName, startDate, endDate, onlyStarts, onlyEnds);
				salaryMap = getSummaryEnterpriseSalary(childDomains, domainId, domainName, startDate, endDate);
				itMap = getSummaryEnterpriseIT(childDomains, domainId, domainName, startDate, endDate);
			} catch (ManagerBeanException e) {
				throw new RuntimeException(e.getMessage());
			}
		} else {
			if(domainId==null){
				domainId = domainSwitcher.getDomainId();
			}
			summaryMap = getSummaryEmployee(domainId, domainName, startDate, endDate, onlyStarts, onlyEnds);
			salaryMap = getSummaryEmployeeSalary(domainId, domainName, startDate, endDate);
			itMap = getSummaryEmployeeIT(domainId, domainName, startDate, endDate);
		}
		fillMapData(summaryMap, salaryMap);
		fillMapData(summaryMap, itMap);
		return new ArrayList<>(summaryMap.values());
	}
	
	private void fillMapData(Map<Integer, ActivitySummaryObject> summaryMap, Map<Integer, ActivitySummaryObject> dataMap){
		dataMap.keySet().stream().forEach(
				key -> {
					ActivitySummaryObject obj = summaryMap.get(key);
					if(obj!=null){
						if(dataMap.get(key).getSalaryCount()!=null){
							obj.setSalaryCount(dataMap.get(key).getSalaryCount());
						}
						if(dataMap.get(key).getSalaryExtraCount()!=null){
							obj.setSalaryExtraCount(dataMap.get(key).getSalaryExtraCount());
						}
						if(dataMap.get(key).getSalarySettleCount()!=null){
							obj.setSalarySettleCount(dataMap.get(key).getSalarySettleCount());
						}
						if(dataMap.get(key).getSalaryOtherCount()!=null){
							obj.setSalaryOtherCount(dataMap.get(key).getSalaryOtherCount());
						}
						if(dataMap.get(key).getItCommonDiseaseCount()!=null){
							obj.setItCommonDiseaseCount(dataMap.get(key).getItCommonDiseaseCount());
						}
						if(dataMap.get(key).getItOccupationalDiseaseCount()!=null){
							obj.setItOccupationalDiseaseCount(dataMap.get(key).getItOccupationalDiseaseCount());
						}
						if(dataMap.get(key).getItMaternityCount()!=null){
							obj.setItMaternityCount(dataMap.get(key).getItMaternityCount());
						}
						if(dataMap.get(key).getItOtherCount()!=null){
							obj.setItOtherCount(dataMap.get(key).getItOtherCount());
						}
					}
				});
	}
	
	private Map<Integer, ActivitySummaryObject> getSummaryEmployee(Integer domainId, String domainName, Date startDate, Date endDate, Boolean onlyStarts, Boolean onlyEnds) {
		AONContext ctx = null;
		try {			
			Condition startCond = onlyStarts?
					CONTRACT.START_DATE.ge(new java.sql.Date(startDate.getTime())).and(CONTRACT.START_DATE.le(new java.sql.Date(endDate.getTime())))
					:
					CONTRACT.START_DATE.le(new java.sql.Date(endDate.getTime()));
			Condition endCond = onlyEnds?
					CONTRACT.END_DATE.isNotNull()
						.and(CONTRACT.END_DATE.ge(new java.sql.Date(startDate.getTime()))
								.and(CONTRACT.END_DATE.le(new java.sql.Date(endDate.getTime()))))
					:
					CONTRACT.END_DATE.ge(new java.sql.Date(startDate.getTime())).or(CONTRACT.END_DATE.isNull());
			
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record6<String, String, String, java.sql.Date, java.sql.Date, Integer>> result = ctx.getDslContext()
					.select(PERSON.NAME, 
							PERSON.FIRST_SURNAME, 
							PERSON.SECOND_SURNAME,
							CONTRACT.START_DATE, 
							CONTRACT.END_DATE, 
							CONTRACT.ID
							)
					.from(CONTRACT.leftOuterJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
							.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(PERSON.REGISTRY)))
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(startCond)
					.and(endCond)
					.groupBy(CONTRACT.ID)
					.orderBy(PERSON.FIRST_SURNAME.asc(), 
							PERSON.SECOND_SURNAME.asc(), 
							PERSON.NAME.asc(), 
							CONTRACT.START_DATE.desc())
					.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
				obj.setName(record.value1());
				obj.setFirstSurname(record.value2());
				obj.setSecondSurname(record.value3());
				obj.setStartDate(record.value4()!=null?new Date(record.value4().getTime()):null);
				obj.setEndDate(record.value5()!=null?new Date(record.value5().getTime()):null);
				obj.setId(record.value6());
				map.put(record.value6(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
	private Map<Integer, ActivitySummaryObject> getSummaryEnterprise(Integer[] childDomainIds, Integer domainId, String domainName, Date startDate, Date endDate, Boolean onlyStarts, Boolean onlyEnds) {
		AONContext ctx = null;
		try {			
			Condition startCond = onlyStarts?
					CONTRACT.START_DATE.ge(new java.sql.Date(startDate.getTime())).and(CONTRACT.START_DATE.le(new java.sql.Date(endDate.getTime())))
					:
					CONTRACT.START_DATE.le(new java.sql.Date(endDate.getTime()));
			Condition endCond = onlyEnds?
					CONTRACT.END_DATE.isNotNull()
						.and(CONTRACT.END_DATE.ge(new java.sql.Date(startDate.getTime()))
								.and(CONTRACT.END_DATE.le(new java.sql.Date(endDate.getTime()))))
					:
					CONTRACT.END_DATE.ge(new java.sql.Date(startDate.getTime())).or(CONTRACT.END_DATE.isNull());
			
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record4<Integer, String, BigDecimal, BigDecimal>> result = ctx.getDslContext()
					.select(DOMAIN.ID, 
							DOMAIN.DESCRIPTION,
							DSL.sum( DSL.field(CONTRACT.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()))).coerce(Integer.class)),
							DSL.sum( DSL.field(CONTRACT.END_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()))).coerce(Integer.class))
							)
					.from(DOMAIN
							.leftOuterJoin(CONTRACT).on(CONTRACT.DOMAIN.eq(DOMAIN.ID)))
					.where(DOMAIN.ID.in(childDomainIds))
					.and(DOMAIN.ACTIVE.eq((byte)1))
					.and(startCond)
					.and(endCond)
					.groupBy(DOMAIN.ID)
					.orderBy(DOMAIN.ID.asc(),
							CONTRACT.START_DATE.desc())
					.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
				obj.setId(record.value1());
				obj.setName(record.value2());
				obj.setStartCount(record.value3()!=null?record.value3().intValue():0);
				obj.setEndCount(record.value4()!=null?record.value4().intValue():0);
				map.put(record.value1(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
	private Map<Integer, ActivitySummaryObject> getSummaryEmployeeSalary(Integer domainId, String domainName, Date startDate, Date endDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx.getDslContext()
					.select(CONTRACT.ID, 
							DSL.sum( DSL.field(SALARY.TYPE.eq((byte) SalaryType.SALARY.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(SALARY.TYPE.eq((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(SALARY.TYPE.eq((byte) SalaryType.SETTLE.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(SALARY.TYPE.gt((byte) SalaryType.SETTLE.ordinal())).coerce(Integer.class)))
					.from(CONTRACT
							.leftOuterJoin(SALARY).on(CONTRACT.ID.eq(SALARY.CONTRACT)))
					.where(CONTRACT.START_DATE.lt(new java.sql.Date(endDate.getTime()))
							.or(CONTRACT.START_DATE.gt(new java.sql.Date(endDate.getTime()))))
					.and(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
					.and(CONTRACT.DOMAIN.eq(domainId))
					.groupBy(CONTRACT.ID)
					.orderBy(CONTRACT.ID.desc(), 
							CONTRACT.START_DATE.desc())
					.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
				obj.setSalaryCount(record.value2()!=null?record.value2().intValue():0);
				obj.setSalaryExtraCount(record.value3()!=null?record.value3().intValue():0);
				obj.setSalarySettleCount(record.value4()!=null?record.value4().intValue():0);
				obj.setSalaryOtherCount(record.value5()!=null?record.value5().intValue():0);
				map.put(record.value1(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
	
	private Map<Integer, ActivitySummaryObject> getSummaryEnterpriseSalary(Integer[] childDomainIds, Integer domainId, String domainName, Date startDate, Date endDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx.getDslContext()
					.select(DOMAIN.ID, 
							DSL.sum( DSL.field(SALARY.TYPE.eq((byte) SalaryType.SALARY.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(SALARY.TYPE.eq((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(SALARY.TYPE.eq((byte) SalaryType.SETTLE.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(SALARY.TYPE.gt((byte) SalaryType.SETTLE.ordinal())).coerce(Integer.class)))
					.from(DOMAIN
							.leftOuterJoin(SALARY).on(SALARY.DOMAIN.eq(DOMAIN.ID)))
					.where(SALARY.ISSUE_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
					.and(DOMAIN.ID.in(childDomainIds))
					.and(DOMAIN.ACTIVE.eq((byte)1))
					.groupBy(DOMAIN.ID)
					.orderBy(DOMAIN.ID.asc())
					.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
				obj.setSalaryCount(record.value2()!=null?record.value2().intValue():0);
				obj.setSalaryExtraCount(record.value3()!=null?record.value3().intValue():0);
				obj.setSalarySettleCount(record.value4()!=null?record.value4().intValue():0);
				obj.setSalaryOtherCount(record.value5()!=null?record.value5().intValue():0);
				map.put(record.value1(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
	private Map<Integer, ActivitySummaryObject> getSummaryEmployeeIT(Integer domainId, String domainName, Date startDate, Date endDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx.getDslContext()
					.select(CONTRACT.ID, 
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.eq((byte) LeaveType.COMMON_DISEASE.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.eq((byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.in((byte) LeaveType.MATERNITY.ordinal(), (byte) LeaveType.PATERNITY.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.notIn((byte) LeaveType.COMMON_DISEASE.ordinal(), (byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal(), (byte) LeaveType.MATERNITY.ordinal(), (byte) LeaveType.PATERNITY.ordinal())).coerce(Integer.class)))
					.from(CONTRACT
							.leftOuterJoin(CONTRACT_LEAVE).on(CONTRACT_LEAVE.CONTRACT.eq(CONTRACT.ID)))
					.where(CONTRACT.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
					.and(CONTRACT_LEAVE.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
					.and(CONTRACT.DOMAIN.eq(domainId))
					.groupBy(CONTRACT.ID)
					.orderBy(CONTRACT.ID.desc(), CONTRACT.START_DATE.desc())
					.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
				obj.setItCommonDiseaseCount(record.value2()!=null?record.value2().intValue():0);
				obj.setItOccupationalDiseaseCount(record.value3()!=null?record.value3().intValue():0);
				obj.setItMaternityCount(record.value4()!=null?record.value4().intValue():0);
				obj.setItOtherCount(record.value5()!=null?record.value5().intValue():0);
				map.put(record.value1(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
	
	private Map<Integer, ActivitySummaryObject> getSummaryEnterpriseIT(Integer[] childDomainIds, Integer domainId, String domainName, Date startDate, Date endDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx.getDslContext()
					.select(DOMAIN.ID,
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.eq((byte) LeaveType.COMMON_DISEASE.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.eq((byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.in((byte) LeaveType.MATERNITY.ordinal(), (byte) LeaveType.PATERNITY.ordinal())).coerce(Integer.class)),
							DSL.sum( DSL.field(CONTRACT_LEAVE.TYPE.notIn((byte) LeaveType.COMMON_DISEASE.ordinal(), (byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal(), (byte) LeaveType.MATERNITY.ordinal(), (byte) LeaveType.PATERNITY.ordinal())).coerce(Integer.class)))
					.from(DOMAIN
							.leftOuterJoin(CONTRACT_LEAVE).on(CONTRACT_LEAVE.DOMAIN.eq(DOMAIN.ID)))
					.where(CONTRACT_LEAVE.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
					.and(DOMAIN.ID.in(childDomainIds))
					.and(DOMAIN.ACTIVE.eq((byte)1))
					.groupBy(DOMAIN.ID)
					.orderBy(DOMAIN.ID.asc())
					.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
				obj.setItCommonDiseaseCount(record.value2()!=null?record.value2().intValue():0);
				obj.setItOccupationalDiseaseCount(record.value3()!=null?record.value3().intValue():0);
				obj.setItMaternityCount(record.value4()!=null?record.value4().intValue():0);
				obj.setItOtherCount(record.value5()!=null?record.value5().intValue():0);
				map.put(record.value1(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
		

}

