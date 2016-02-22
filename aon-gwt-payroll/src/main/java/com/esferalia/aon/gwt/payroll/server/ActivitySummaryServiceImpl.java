package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Result;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.client.ActivitySummaryService;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.occam.api.AONContext;
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
	public List<ActivitySummaryObject> getActivitySummary(Date startDate, Date endDate) {
		
		initFacesContext();
		
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		String domainName = domainSwitcher.getDomainNameURL();
		Integer domainId = domainSwitcher.getDomainId();
		
		Map<Integer, ActivitySummaryObject> summaryMap = null;
		Map<Integer, ActivitySummaryObject> salaryMap = null;
		if(domainSwitcher.isParentDomain()){
			try {
				Integer[] childDomains = getChildDomainIDs(domainId);
				summaryMap = getSummaryEnterprise(childDomains, domainId, domainName, startDate, endDate);
				salaryMap = getSummaryEnterpriseSalary(childDomains, domainId, domainName, startDate, endDate);
			} catch (ManagerBeanException e) {
				throw new RuntimeException(e.getMessage());
			}
		} else {
			summaryMap = getSummaryEmployee(domainId, domainName, startDate, endDate);
			salaryMap = getSummaryEmployeeSalary(domainId, domainName, startDate, endDate);
		}
		fillMapData(summaryMap, salaryMap);
		return new ArrayList<>(summaryMap.values());
	}
	
	private void fillMapData(Map<Integer, ActivitySummaryObject> summaryMap, Map<Integer, ActivitySummaryObject> salaryMap){
		salaryMap.keySet().stream().forEach(
				key -> {
					ActivitySummaryObject obj = summaryMap.get(key);
					if(obj!=null){
						obj.setSalaryCount(salaryMap.get(key).getSalaryCount());
						obj.setSalaryExtraCount(salaryMap.get(key).getSalaryExtraCount());
						obj.setSalaryOtherCount(salaryMap.get(key).getSalaryOtherCount());
					}
				});
	}
	
	private Map<Integer, ActivitySummaryObject> getSummaryEmployee(Integer domainId, String domainName, Date startDate, Date endDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record6<String, String, String, java.sql.Date, java.sql.Date, Integer>> result = ctx.getDslContext()
					.select(PERSON.NAME, 
							PERSON.FIRST_SURNAME, 
							PERSON.SECOND_SURNAME,
							CONTRACT.START_DATE, 
							CONTRACT.END_DATE, 
							CONTRACT.ID
							)
					.from(CONTRACT.join(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
						.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(PERSON.REGISTRY)))
					.where(CONTRACT.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
					.and(CONTRACT.DOMAIN.eq(domainId))
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
				map.put(record.value6(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
	private Map<Integer, ActivitySummaryObject> getSummaryEnterprise(Integer[] childDomainIds, Integer domainId, String domainName, Date startDate, Date endDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, AonServletUtils.getLoggedUser());
			Result<Record4<Integer, String, Integer, Integer>> result = ctx.getDslContext()
					.select(DOMAIN.ID, 
							DOMAIN.DESCRIPTION,
							org.jooq.impl.DSL.count( org.jooq.impl.DSL.field(CONTRACT.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(startDate.getTime()))).coerce(Integer.class)),
							org.jooq.impl.DSL.count( org.jooq.impl.DSL.field(CONTRACT.END_DATE.between(new java.sql.Date(endDate.getTime()), new java.sql.Date(endDate.getTime()))).coerce(Integer.class))
							)
							.from(DOMAIN.join(CONTRACT).on(CONTRACT.DOMAIN.eq(DOMAIN.ID)))
									.where(CONTRACT.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
									.and(DOMAIN.ID.in(childDomainIds))
									.and(DOMAIN.ACTIVE.eq((byte)1))
									.groupBy(DOMAIN.ID)
									.orderBy(DOMAIN.ID.asc(), 
											CONTRACT.START_DATE.desc())
											.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
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
			Result<Record4<Integer, BigDecimal, BigDecimal, BigDecimal>> result = ctx.getDslContext()
					.select(CONTRACT.ID, 
							org.jooq.impl.DSL.sum( org.jooq.impl.DSL.field(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.eq((byte) SalaryType.SALARY.ordinal())).coerce(Integer.class)),
							org.jooq.impl.DSL.sum( org.jooq.impl.DSL.field(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.eq((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)),
							org.jooq.impl.DSL.sum( org.jooq.impl.DSL.field(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.gt((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)))
							.from(CONTRACT
								.leftOuterJoin(com.esferalia.aon.jooq.tables.Salary.SALARY)
								.on(com.esferalia.aon.jooq.tables.Salary.SALARY.CONTRACT.eq(CONTRACT.ID)))
							.where(CONTRACT.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
							.and(com.esferalia.aon.jooq.tables.Salary.SALARY.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
							.and(CONTRACT.DOMAIN.eq(domainId))
							.groupBy(CONTRACT.ID)
							.orderBy(CONTRACT.ID.desc(), CONTRACT.START_DATE.desc())
							.fetch();
			
			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream().forEach(record ->{
				ActivitySummaryObject obj = new ActivitySummaryObject();
				obj.setSalaryCount(record.value2()!=null?record.value2().intValue():0);
				obj.setSalaryExtraCount(record.value3()!=null?record.value3().intValue():0);
				obj.setSalaryOtherCount(record.value4()!=null?record.value4().intValue():0);
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
			Result<Record4<Integer, BigDecimal, BigDecimal, BigDecimal>> result = ctx.getDslContext()
					.select(DOMAIN.ID, 
							org.jooq.impl.DSL.sum( org.jooq.impl.DSL.field(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.eq((byte) SalaryType.SALARY.ordinal())).coerce(Integer.class)),
							org.jooq.impl.DSL.sum( org.jooq.impl.DSL.field(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.eq((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)),
							org.jooq.impl.DSL.sum( org.jooq.impl.DSL.field(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.gt((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)))
							.from(DOMAIN
									.leftOuterJoin(com.esferalia.aon.jooq.tables.Salary.SALARY)
									.on(com.esferalia.aon.jooq.tables.Salary.SALARY.DOMAIN.eq(DOMAIN.ID)))
									.where(com.esferalia.aon.jooq.tables.Salary.SALARY.START_DATE.between(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())))
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
				obj.setSalaryOtherCount(record.value4()!=null?record.value4().intValue():0);
				map.put(record.value1(), obj);
			});
			return map;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
		
	}
		

}

