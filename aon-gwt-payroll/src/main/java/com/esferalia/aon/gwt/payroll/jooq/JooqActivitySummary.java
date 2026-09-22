package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rrelationship.RRELATIONSHIP;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record5;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryParams;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqActivitySummary {

	private static Settings SETTINGS = null;

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static List<ActivitySummaryObject> getActivitySummary(Connection connection, Integer domainId,
	        Integer parentDomainId, Integer userId, ActivitySummaryParams params) {
	    DSLContext dslContext = DSL.using(connection, getDefaultSettings());
	    return buildActivitySummary(dslContext, domainId, parentDomainId, userId, params);
	}

	private static List<ActivitySummaryObject> buildActivitySummary(DSLContext dslContext, Integer domainId,
	        Integer parentDomainId, Integer userId, ActivitySummaryParams params) {

	    if (params.getStart() == null || params.getEnd() == null)
	        return new ArrayList<>();

	    Map<Integer, ActivitySummaryObject> summaryMap = null;
	    Map<Integer, ActivitySummaryObject> salaryMap = null;
	    Map<Integer, ActivitySummaryObject> itMap = null;

	    if (params.getChildomain() == null && (parentDomainId == null || params.isOffice())) {

	        Integer[] childDomains = null;
	        try {
	            childDomains = params.isOffice()
	                    ? getOfficeDomainIDs(dslContext, domainId, userId)
	                    : getChildDomainIDs(dslContext, domainId, userId);
	        } catch (SQLException e) {
	            throw new IllegalArgumentException(e.getMessage());
	        }

	        if (params.isOffice() && (childDomains == null || childDomains.length == 0))
	            return new ArrayList<>();

	        summaryMap = getSummaryEnterprise(dslContext, params, childDomains);
	        salaryMap = getSummaryEnterpriseSalary(dslContext, params, childDomains);
	        itMap = getSummaryEnterpriseIT(dslContext, params, childDomains);

	    } else {

	        if (params.getChildomain() == null)
	            params.setChildomain(domainId);
	        summaryMap = getSummaryEmployee(dslContext, params, params.getChildomain());
	        salaryMap = getSummaryEmployeeSalary(dslContext, params, params.getChildomain());
	        itMap = getSummaryEmployeeIT(dslContext, params, params.getChildomain());
	    }

	    fillMapData(summaryMap, salaryMap);
	    fillMapData(summaryMap, itMap);

	    return sortedList(summaryMap, params);
	}

//	public static List<ActivitySummaryObject> getActivitySummary(Connection connection, Integer domainId,
//			Integer parentDomainId, Integer userId, ActivitySummaryParams params) {
//
//		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
//
//		if (params.getStart() != null && params.getEnd() != null) {
//			Map<Integer, ActivitySummaryObject> summaryMap = null;
//			Map<Integer, ActivitySummaryObject> salaryMap = null;
//			Map<Integer, ActivitySummaryObject> itMap = null;
//
//			if (parentDomainId == null && params.getChildomain() == null) {
//				try {
//					Integer[] childDomains = null;
//
//					if (null != userId)
//						childDomains = getChildDomainIDs(dslContext, domainId, userId);
//					else
//						childDomains = getChildDomainIDs(dslContext, domainId, null);
//
//					summaryMap = getSummaryEnterprise(dslContext, params, childDomains);
//					salaryMap = getSummaryEnterpriseSalary(dslContext, params, childDomains);
//					itMap = getSummaryEnterpriseIT(dslContext, params, childDomains);
//
//				} catch (SQLException e) {
//					throw new IllegalArgumentException(e.getMessage());
//				}
//			} else {
//
//				if (params.getChildomain() == null)
//					params.setChildomain(domainId);
//				summaryMap = getSummaryEmployee(dslContext, params, params.getChildomain());
//				salaryMap = getSummaryEmployeeSalary(dslContext, params, params.getChildomain());
//				itMap = getSummaryEmployeeIT(dslContext, params, params.getChildomain());
//
//			}
//
//			fillMapData(summaryMap, salaryMap);
//			fillMapData(summaryMap, itMap);
//
//			return sortedList(summaryMap, params);
//
//		}
//
//		return new ArrayList<>();
//	}

	private static List<ActivitySummaryObject> sortedList(Map<Integer, ActivitySummaryObject> summaryMap,
			ActivitySummaryParams params) {
		if (params.isAsc()) {

			if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "name")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getFullname);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "start")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getStartDate);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "end")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getEndDate);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "salary")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getSalaryCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "extra")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getSalaryExtraCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "settle")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getSalarySettleCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "delay")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getSalaryOtherCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_ecan")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getItCommonDiseaseCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_atep")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getItOccupationalDiseaseCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_mp")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getItMaternityCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_other")) {
				return sortListByAttribute(summaryMap.values(), ActivitySummaryObject::getItOtherCount);
			}

		} else {

			if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "name")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getFullname);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "start")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getStartDate);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "end")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getEndDate);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "salary")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getSalaryCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "extra")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getSalaryExtraCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "settle")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getSalarySettleCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "delay")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getSalaryOtherCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_ecan")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getItCommonDiseaseCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_atep")) {
				return sortListDescByAttribute(summaryMap.values(),
						ActivitySummaryObject::getItOccupationalDiseaseCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_mp")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getItMaternityCount);
			} else if (AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "it_other")) {
				return sortListDescByAttribute(summaryMap.values(), ActivitySummaryObject::getItOtherCount);
			}

		}

		return new ArrayList<>(summaryMap.values().stream().sorted(Comparator.comparing(o -> o.getFullname()))
				.collect(Collectors.toList()));
	}

	private static <T, R extends Comparable<R>> List<T> sortListByAttribute(Collection<T> collection,
			Function<T, R> attributeExtractor) {
		return collection.stream()
				.sorted(Comparator.comparing(attributeExtractor, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());
	}

	private static <T, R extends Comparable<R>> List<T> sortListDescByAttribute(Collection<T> collection,
			Function<T, R> attributeExtractor) {
		return collection.stream()
				.sorted(Comparator.comparing(attributeExtractor, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());
	}

	public static Integer[] getChildDomainIDs(DSLContext dslContext, Integer domain, Integer userId)
			throws SQLException {
		List<Integer> userScopes = dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE)
				.where(USER_SCOPE.USER_ID.eq(userId)).fetch(USER_SCOPE.SCOPE);

		if (userScopes.isEmpty())
			return dslContext.select().from(DOMAIN).where(DOMAIN.PARENT.eq(domain)).fetchArray(DOMAIN.ID);

		else
			return dslContext.select().from(DOMAIN).where(DOMAIN.PARENT.eq(domain))
					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.in(userScopes))).fetchArray(DOMAIN.ID);
	}
	
	public static Integer[] getOfficeDomainIDs(DSLContext dslContext, Integer domainId, Integer userId)
	        throws SQLException {
	    List<Integer> userScopes = dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE)
	            .where(USER_SCOPE.USER_ID.eq(userId)).fetch(USER_SCOPE.SCOPE);

	    Condition condition = CUSTOMER.DOMAIN.eq(domainId)
	            .and(CUSTOMER.STATUS.eq((byte) 0))
	            .and(RRELATIONSHIP.RELATIONSHIP.eq(-1))
	            .and(ENTERPRISE.DOMAIN.isNotNull());

	    if (!userScopes.isEmpty())
	        condition = condition.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.in(userScopes)));
	    
	    return dslContext.selectDistinct(DOMAIN.ID)
	            .from(CUSTOMER
	                    .join(RRELATIONSHIP).on(RRELATIONSHIP.REGISTRY.eq(CUSTOMER.REGISTRY))
	                    .join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(RRELATIONSHIP.RELATED_REGISTRY))
	                    .join(DOMAIN).on(DOMAIN.ID.eq(ENTERPRISE.DOMAIN)))
	            .where(condition)
	            .fetchArray(DOMAIN.ID);
	}

	private static void fillMapData(Map<Integer, ActivitySummaryObject> summaryMap,
			Map<Integer, ActivitySummaryObject> dataMap) {
		dataMap.keySet().stream().forEach(key -> {
			ActivitySummaryObject obj = summaryMap.get(key);
			if (obj != null) {
				if (dataMap.get(key).getSalaryCount() != null) {
					obj.setSalaryCount(dataMap.get(key).getSalaryCount());
				}
				if (dataMap.get(key).getSalaryExtraCount() != null) {
					obj.setSalaryExtraCount(dataMap.get(key).getSalaryExtraCount());
				}
				if (dataMap.get(key).getSalarySettleCount() != null) {
					obj.setSalarySettleCount(dataMap.get(key).getSalarySettleCount());
				}
				if (dataMap.get(key).getSalaryOtherCount() != null) {
					obj.setSalaryOtherCount(dataMap.get(key).getSalaryOtherCount());
				}
				if (dataMap.get(key).getItCommonDiseaseCount() != null) {
					obj.setItCommonDiseaseCount(dataMap.get(key).getItCommonDiseaseCount());
				}
				if (dataMap.get(key).getItOccupationalDiseaseCount() != null) {
					obj.setItOccupationalDiseaseCount(dataMap.get(key).getItOccupationalDiseaseCount());
				}
				if (dataMap.get(key).getItMaternityCount() != null) {
					obj.setItMaternityCount(dataMap.get(key).getItMaternityCount());
				}
				if (dataMap.get(key).getItOtherCount() != null) {
					obj.setItOtherCount(dataMap.get(key).getItOtherCount());
				}
			}
		});
	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEmployee(DSLContext dslContext,
			ActivitySummaryParams params, Integer domainId) {
		SelectConditionStep<Record7<String, String, String, java.sql.Date, java.sql.Date, Integer, String>> select = dslContext
				.select(PERSON.NAME, PERSON.FIRST_SURNAME, PERSON.SECOND_SURNAME, CONTRACT.START_DATE,
						CONTRACT.END_DATE, CONTRACT.ID, REGISTRY.NAME)
				.from(CONTRACT.leftOuterJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY)).leftOuterJoin(REGISTRY)
						.on(REGISTRY.ID.eq(PERSON.REGISTRY)))
				.where(CONTRACT.DOMAIN.eq(domainId));

		if (params.isStartContract() && params.isEndContract())
			select.and(getStartCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
					params.isStartContract())
					.or(getEndCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
							params.isEndContract())));
		else if (params.isStartContract())
			select.and(getStartCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
					params.isStartContract()));
		else if (params.isEndContract())
			select.and(getEndCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
					params.isEndContract()));
		else
			select.and(CONTRACT.START_DATE.le(AonDateUtils.toSql(params.getEnd()))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(AonDateUtils.toSql(params.getStart())))));

		if (AonStringUtils.isNotBlank(params.getDescription()))
			select.and(REGISTRY.NAME.like("%" + params.getDescription() + "%"));

		Result<Record7<String, String, String, java.sql.Date, java.sql.Date, Integer, String>> result = select
				.groupBy(CONTRACT.ID).orderBy(PERSON.FIRST_SURNAME.asc(), PERSON.SECOND_SURNAME.asc(),
						PERSON.NAME.asc(), CONTRACT.START_DATE.desc())
				.fetch();

		Map<Integer, ActivitySummaryObject> map = new HashMap<>();
		result.stream().forEach(record -> {
			ActivitySummaryObject obj = new ActivitySummaryObject();
			obj.setName(record.value1());
			obj.setFirstSurname(record.value2());
			obj.setSecondSurname(record.value3());
			obj.setStartDate(record.value4() != null ? new Date(record.value4().getTime()) : null);
			obj.setEndDate(record.value5() != null ? new Date(record.value5().getTime()) : null);
			obj.setId(record.value6());
			map.put(record.value6(), obj);
		});
		return map;
	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEnterprise(DSLContext dslContext,
			ActivitySummaryParams params, Integer[] childDomainIds) {

		SelectConditionStep<Record5<Integer, String, String, BigDecimal, BigDecimal>> select = dslContext
				.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION,
						DSL.sum(DSL.field(CONTRACT.START_DATE.between(AonDateUtils.toSql(params.getStart()),
								AonDateUtils.toSql(params.getEnd()))).coerce(Integer.class)),
						DSL.sum(DSL.field(CONTRACT.END_DATE.between(AonDateUtils.toSql(params.getStart()),
								AonDateUtils.toSql(params.getEnd()))).coerce(Integer.class)))
				.from(DOMAIN.leftOuterJoin(CONTRACT).on(CONTRACT.DOMAIN.eq(DOMAIN.ID)))
				.where(DOMAIN.ID.in(childDomainIds)).and(DOMAIN.ACTIVE.eq((byte) 1));

		if (params.isStartContract() && params.isEndContract())
			select.and(getStartCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
					params.isStartContract())
					.or(getEndCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
							params.isEndContract())));
		else if (params.isStartContract())
			select.and(getStartCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
					params.isStartContract()));
		else if (params.isEndContract())
			select.and(getEndCondition(AonDateUtils.toSql(params.getStart()), AonDateUtils.toSql(params.getEnd()),
					params.isEndContract()));

		if (AonStringUtils.isNotBlank(params.getDescription()))
			select.and(DOMAIN.DESCRIPTION.like("%" + params.getDescription() + "%"));

		Result<Record5<Integer, String, String, BigDecimal, BigDecimal>> result = select.groupBy(DOMAIN.ID)
				.orderBy(DOMAIN.DESCRIPTION.asc()).fetch();

		Map<Integer, ActivitySummaryObject> map = new HashMap<>();
		result.stream().forEach(record -> {
			ActivitySummaryObject obj = new ActivitySummaryObject();
			obj.setId(record.value1());
			obj.setNameUrl(record.value2());
			obj.setName(record.value3());
			obj.setStartCount(record.value4() != null ? record.value4().intValue() : 0);
			obj.setEndCount(record.value5() != null ? record.value5().intValue() : 0);
			map.put(record.value1(), obj);
		});
		return map;

	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEmployeeSalary(DSLContext dslContext,
			ActivitySummaryParams params, Integer domainId) {
		Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = dslContext
				.select(CONTRACT.ID,
						DSL.sum(DSL.field(SALARY.TYPE.eq((byte) SalaryType.SALARY.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(SALARY.TYPE.eq((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(SALARY.TYPE.eq((byte) SalaryType.SETTLE.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(SALARY.TYPE.gt((byte) SalaryType.SETTLE.ordinal())).coerce(Integer.class)))
				.from(CONTRACT.leftOuterJoin(SALARY).on(CONTRACT.ID.eq(SALARY.CONTRACT)))
				.where(CONTRACT.START_DATE.lt(AonDateUtils.toSql(params.getEnd()))
						.or(CONTRACT.START_DATE.gt(AonDateUtils.toSql(params.getEnd()))))
				.and(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(params.getStart()),
						AonDateUtils.toSql(params.getEnd())))
				.and(CONTRACT.DOMAIN.eq(domainId)).and(getSalaryCondition(params)).groupBy(CONTRACT.ID)
				.orderBy(CONTRACT.ID.desc(), CONTRACT.START_DATE.desc()).fetch();

		Map<Integer, ActivitySummaryObject> map = new HashMap<>();
		result.stream().forEach(record -> {
			ActivitySummaryObject obj = new ActivitySummaryObject();
			obj.setSalaryCount(record.value2() != null ? record.value2().intValue() : 0);
			obj.setSalaryExtraCount(record.value3() != null ? record.value3().intValue() : 0);
			obj.setSalarySettleCount(record.value4() != null ? record.value4().intValue() : 0);
			obj.setSalaryOtherCount(record.value5() != null ? record.value5().intValue() : 0);
			map.put(record.value1(), obj);
		});
		return map;
	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEnterpriseSalary(DSLContext dslContext,
			ActivitySummaryParams params, Integer[] childDomainIds) {

		Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = dslContext
				.select(DOMAIN.ID,
						DSL.sum(DSL.field(SALARY.TYPE.eq((byte) SalaryType.SALARY.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(SALARY.TYPE.eq((byte) SalaryType.EXTRA.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(SALARY.TYPE.eq((byte) SalaryType.SETTLE.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(SALARY.TYPE.eq((byte) SalaryType.DELAY.ordinal())).coerce(Integer.class)))
				.from(DOMAIN.leftOuterJoin(SALARY).on(SALARY.DOMAIN.eq(DOMAIN.ID)))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(params.getStart()),
						AonDateUtils.toSql(params.getEnd())))
				.and(DOMAIN.ID.in(childDomainIds)).and(DOMAIN.ACTIVE.eq((byte) 1)).and(SALARY.TYPE.lt((byte) 4)) // Nomina,
																													// extra,
																													// finiquito,
																													// atraso
				.and(getSalaryCondition(params)).groupBy(DOMAIN.ID).orderBy(DOMAIN.ID.asc()).fetch();

		Map<Integer, ActivitySummaryObject> map = new HashMap<>();
		result.stream().forEach(record -> {
			ActivitySummaryObject obj = new ActivitySummaryObject();
			obj.setSalaryCount(record.value2() != null ? record.value2().intValue() : 0);
			obj.setSalaryExtraCount(record.value3() != null ? record.value3().intValue() : 0);
			obj.setSalarySettleCount(record.value4() != null ? record.value4().intValue() : 0);
			obj.setSalaryOtherCount(record.value5() != null ? record.value5().intValue() : 0);
			map.put(record.value1(), obj);
		});
		return map;

	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEmployeeIT(DSLContext dslContext,
			ActivitySummaryParams params, Integer domainId) {
		Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = dslContext
				.select(CONTRACT.ID,
						DSL.sum(DSL.field(CONTRACT_LEAVE.TYPE.in((byte) LeaveType.COMMON_DISEASE.ordinal(),
								(byte) LeaveType.COMMON_DISEASE_AT_LACK.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(CONTRACT_LEAVE.TYPE.eq((byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal()))
								.coerce(Integer.class)),
						DSL.sum(DSL.field(CONTRACT_LEAVE.TYPE.in((byte) LeaveType.MATERNITY.ordinal(),
								(byte) LeaveType.PATERNITY.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL
								.field(CONTRACT_LEAVE.TYPE.notIn((byte) LeaveType.COMMON_DISEASE.ordinal(),
										(byte) LeaveType.COMMON_DISEASE_AT_LACK.ordinal(),
										(byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal(),
										(byte) LeaveType.MATERNITY.ordinal(), (byte) LeaveType.PATERNITY.ordinal()))
								.coerce(Integer.class)))
				.from(CONTRACT.leftOuterJoin(CONTRACT_LEAVE).on(CONTRACT_LEAVE.CONTRACT.eq(CONTRACT.ID)))
				.where(CONTRACT_LEAVE.START_DATE.le(AonDateUtils.toSql(params.getEnd())))
				.and((CONTRACT_LEAVE.END_DATE.isNull()
						.or(CONTRACT_LEAVE.END_DATE.ge(AonDateUtils.toSql(params.getStart())))))
				.and(CONTRACT.DOMAIN.eq(domainId)).and(getItCondition(params)).groupBy(CONTRACT.ID)
				.orderBy(CONTRACT.ID.desc(), CONTRACT.START_DATE.desc()).fetch();

		Map<Integer, ActivitySummaryObject> map = new HashMap<>();
		result.stream().forEach(record -> {
			ActivitySummaryObject obj = new ActivitySummaryObject();
			obj.setItCommonDiseaseCount(record.value2() != null ? record.value2().intValue() : 0);
			obj.setItOccupationalDiseaseCount(record.value3() != null ? record.value3().intValue() : 0);
			obj.setItMaternityCount(record.value4() != null ? record.value4().intValue() : 0);
			obj.setItOtherCount(record.value5() != null ? record.value5().intValue() : 0);
			map.put(record.value1(), obj);
		});
		return map;
	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEnterpriseIT(DSLContext dslContext,
			ActivitySummaryParams params, Integer[] childDomainIds) {
		Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = dslContext
				.select(DOMAIN.ID,
						DSL.sum(DSL.field(CONTRACT_LEAVE.TYPE.in((byte) LeaveType.COMMON_DISEASE.ordinal(),
								(byte) LeaveType.COMMON_DISEASE_AT_LACK.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL.field(CONTRACT_LEAVE.TYPE.eq((byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal()))
								.coerce(Integer.class)),
						DSL.sum(DSL.field(CONTRACT_LEAVE.TYPE.in((byte) LeaveType.MATERNITY.ordinal(),
								(byte) LeaveType.PATERNITY.ordinal())).coerce(Integer.class)),
						DSL.sum(DSL
								.field(CONTRACT_LEAVE.TYPE.notIn((byte) LeaveType.COMMON_DISEASE.ordinal(),
										(byte) LeaveType.COMMON_DISEASE_AT_LACK.ordinal(),
										(byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal(),
										(byte) LeaveType.MATERNITY.ordinal(), (byte) LeaveType.PATERNITY.ordinal()))
								.coerce(Integer.class)))
				.from(DOMAIN.leftOuterJoin(CONTRACT_LEAVE).on(CONTRACT_LEAVE.DOMAIN.eq(DOMAIN.ID)))
				.where(CONTRACT_LEAVE.START_DATE.le(AonDateUtils.toSql(params.getEnd())))
				.and((CONTRACT_LEAVE.END_DATE.isNull()
						.or(CONTRACT_LEAVE.END_DATE.ge(AonDateUtils.toSql(params.getStart())))))
				.and(DOMAIN.ID.in(childDomainIds)).and(DOMAIN.ACTIVE.eq((byte) 1)).and(getItCondition(params))
				.groupBy(DOMAIN.ID).orderBy(DOMAIN.ID.asc()).fetch();

		Map<Integer, ActivitySummaryObject> map = new HashMap<>();
		result.stream().forEach(record -> {
			ActivitySummaryObject obj = new ActivitySummaryObject();
			obj.setItCommonDiseaseCount(record.value2() != null ? record.value2().intValue() : 0);
			obj.setItOccupationalDiseaseCount(record.value3() != null ? record.value3().intValue() : 0);
			obj.setItMaternityCount(record.value4() != null ? record.value4().intValue() : 0);
			obj.setItOtherCount(record.value5() != null ? record.value5().intValue() : 0);
			map.put(record.value1(), obj);
		});
		return map;
	}

	/*
	 * CONTRACT CONDITIONS
	 */
	private static Condition getStartCondition(java.sql.Date startDate, java.sql.Date endDate,
			boolean isContractStart) {
		return isContractStart ? CONTRACT.START_DATE.ge(startDate).and(CONTRACT.START_DATE.le(endDate))
				: CONTRACT.START_DATE.le(endDate);
	}

	private static Condition getEndCondition(java.sql.Date startDate, java.sql.Date endDate, boolean isContractEnd) {
		return isContractEnd
				? CONTRACT.END_DATE.isNotNull().and(CONTRACT.END_DATE.ge(startDate).and(CONTRACT.END_DATE.le(endDate)))
				: CONTRACT.END_DATE.ge(startDate).or(CONTRACT.END_DATE.isNull());
	}

	/*
	 * SALARY CONDITIONS
	 */
	private static Condition getSalaryCondition(ActivitySummaryParams params) {
		byte salaryType = (byte) SalaryType.SALARY.ordinal();
		byte extraType = (byte) SalaryType.EXTRA.ordinal();
		byte settleType = (byte) SalaryType.SETTLE.ordinal();
		byte delayype = (byte) SalaryType.DELAY.ordinal();

		Condition cond = null;
		cond = params.isSalary() ? SALARY.TYPE.eq(salaryType) : SALARY.TYPE.ne(salaryType);
		cond = params.isExtra() ? cond.or(SALARY.TYPE.eq(extraType)) : cond.and(SALARY.TYPE.ne(extraType));
		cond = params.isSettle() ? cond.or(SALARY.TYPE.eq(settleType)) : cond.and(SALARY.TYPE.ne(settleType));
		cond = params.isDelay() ? cond.or(SALARY.TYPE.eq(delayype)) : cond.and(SALARY.TYPE.ne(delayype));
		return cond;
	}

	/*
	 * IT CONDITIONS
	 */
	private static Condition getItCondition(ActivitySummaryParams params) {
		byte itCommonDiseaseType = (byte) LeaveType.COMMON_DISEASE.ordinal();
		byte itCommonDiseaseAtLackType = (byte) LeaveType.COMMON_DISEASE_AT_LACK.ordinal();
		byte itOccupationalDiseaseType = (byte) LeaveType.OCCUPATIONAL_DISEASE.ordinal();
		byte itMaternityType = (byte) LeaveType.MATERNITY.ordinal();
		byte itPaternityType = (byte) LeaveType.PATERNITY.ordinal();

		Condition cond = null;
		cond = params.isItCD() ? CONTRACT_LEAVE.TYPE.in(itCommonDiseaseType, itCommonDiseaseAtLackType)
				: CONTRACT_LEAVE.TYPE.notIn(itCommonDiseaseType, itCommonDiseaseAtLackType);
		cond = params.isItOD() ? cond.or(CONTRACT_LEAVE.TYPE.eq(itOccupationalDiseaseType))
				: cond.and(CONTRACT_LEAVE.TYPE.ne(itOccupationalDiseaseType));
		cond = params.isItMP()
				? cond.or(CONTRACT_LEAVE.TYPE.eq(itMaternityType)).or(CONTRACT_LEAVE.TYPE.eq(itPaternityType))
				: cond.and(CONTRACT_LEAVE.TYPE.ne(itMaternityType)).and(CONTRACT_LEAVE.TYPE.ne(itPaternityType));
		cond = params.isItOT() ? cond.or(CONTRACT_LEAVE.TYPE.gt(itPaternityType))
				: cond.and(CONTRACT_LEAVE.TYPE.le(itPaternityType));
		return cond;
	}

}
