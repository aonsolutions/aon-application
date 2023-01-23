package com.esferalia.aon.occam.api.model.payroll;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;

public class Employee implements Serializable{
	
	public static final String TC2 = "TC2";
	public static final String RLCE = "RLCE";
	public static final String OCUPACION = "OCUPACION";
	public static final String GRUPO_COTIZACION = "GRUPO_COTIZACION";
	public static final String COEFICIENTE_PARCIALIDAD = "COEFICIENTE_PARCIALIDAD";
	public static final String COLECTIVO_TRABAJADORES = "COLECTIVO_TRABAJADORES";
	public static final String CNO = "CNO";
	public static final String MODELO_COTIZACION_AGRARIO = "MODELO_COTIZACION_AGRARIO";

	public static class Data<T> {
		private T value;
		private LocalDate endDate;
		private LocalDate startDate;
		
		private Data() {
			
		}
		
		public T getValue() {
			return value;
		}
		
		public LocalDate getEndDate() {
			return endDate;
		}
		
		public LocalDate getStartDate() {
			return startDate;
		}
		
	}

	public static class ExpressionData {
		private String expression;
		private LocalDate endDate;
		private LocalDate startDate;
		
		public String getExpression() {
			return expression;
		}

		public LocalDate getEndDate() {
			return endDate;
		}
		
		public LocalDate getStartDate() {
			return startDate;
		}
		
	}
	
	
	private String naf;
	private String dni;
	private String name;
	private LocalDate birthDate;
	private String phone;
	private String sex; 
	
	private String cif;
	private String ccc;
	
	private String category;
	private String regime;
	private Date startDate;
	private Date endDate;
	
	private Date insertDate;
	private Date deleteDate;
	private String workplaceName;
	
	private Integer employeeId;
	private Integer workplaceId;
	
	private Integer registration;
	
	private Map<String, Collection<ExpressionData>> dataMap;
	
	private Map<String, Collection<ExpressionData>> infoMap;
	
	public Employee() {
		this.sex = "U";
		this.dataMap = new HashMap<String, Collection<ExpressionData>>();
		this.infoMap = new HashMap<>();
	}
	
	public String getNaf() {
		return naf;
	}
	
	public Employee setNaf(String naf) {
		this.naf = naf;
		return this;
	}
	
	public String getDni() {
		return dni;
	}
	
	public Employee setDni(String dni) {
		this.dni = dni;
		return this;
	}
	
	public String getCcc() {
		return ccc;
	}
	
	public Employee setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public String getCif() {
		return cif;
	}
	
	public Employee setCif(String cif) {
		this.cif = cif;
		return this;
	}

	public String getRegime() {
		return regime;
	}
	
	public Employee setRegime(String regime) {
		this.regime = regime;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Employee setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Integer getEmployeeId() {
		return employeeId;
	}
	
	public Employee setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
		return this;
	}
	
	public Optional<Integer> getRegistration() {
		return Optional.ofNullable(registration);
	}
	
	public Employee setRegistration(Integer registration) {
        System.out.println("REGISTRATION-> "+ registration);
		this.registration = registration;
		return this;
	}
	
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	
	
	public Employee setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
		return this;
	}

	
	public Employee setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
		return this;
	}

	// ------------------------------------------------------------------------
	
	public Optional<String> getWorkplaceName() {
		return Optional.ofNullable(workplaceName);
	}
	
	public Optional<String> getSex() {
		return Optional.ofNullable(sex);
	}
	
	public Employee setSex(String sex) {
		this.sex = sex;
		return this;
	}
	
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	
	public Employee setName(String name) {
		this.name = name;
		return this;
	}
	
	public Optional<String> getPhone() {
		return Optional.ofNullable(phone);
	}
	
	public Employee setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public Optional<Date> getBirthDate() {
		return Optional.ofNullable(toDate(birthDate));
	}
	
	public Employee setBirthDate(Date birthDate) {
		return setBirthDate(toLocalDate(birthDate));
	}
	
	public Optional<Date> getEndDate() {
		return Optional.ofNullable(endDate);
	}
	
	public Employee setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Optional<Date> getInsertDate() {
		return Optional.ofNullable(insertDate);
	}
	
	public Employee setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
		return this;
	}

	public Optional<Date> getDeleteDate() {
		return Optional.ofNullable(deleteDate);
	}
	
	public Employee setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
		return this;
	}

	public Optional<String> getCategory() {
		return Optional.ofNullable(category);
	}
	
	public Employee setCategory(String category) {
		this.category = category;
		return this;
	}
	
	public Optional<String> getContractType() {
		return getContractType(toLocalDate(startDate));
	}
	
	public Collection<Data<String>> getContractTypes() {
		return getDatas(TC2, String.class);
	}

	public Employee setContractType(String contractType ) {
		return setString(TC2, contractType);
	}

	public Optional<String> getContractType(LocalDate date) {
		return getData(TC2, date, String.class);
	}

	public Employee addContractType(String contractType, Date startDate, Date endDate) {
		return addString(TC2, contractType, toLocalDate(startDate), toLocalDate(endDate));
	}

	public Optional<String> getRlce() {
		return getRlce(toLocalDate(startDate));
	}
	
	public Employee setRlce(String rlec ) {
		return setString(RLCE, rlec);
	}

	public Collection<Data<String>> getRlces() {
		return getDatas(RLCE, String.class);
	}

	public Optional<String> getRlce(LocalDate date) {
		return getData(RLCE, date, String.class);
	}

	public Employee addRlce(String rlce, Date startDate, Date endDate) {
		return addString(RLCE, rlce, toLocalDate(startDate), toLocalDate(endDate));
	}

	public Optional<String> getOccupation() {
		return getOccupation(toLocalDate(startDate));
	}

	public Employee setOccupation(String ocupation) {
		return setString(OCUPACION, ocupation);
	}

	public Collection<Data<String>> getOccupations() {
		return getDatas(OCUPACION, String.class);
	}

	public Optional<String> getOccupation(LocalDate date) {
		return getData(OCUPACION, date, String.class);
	}

	public Employee addOccupation(String occupation, Date startDate, Date endDate) {
		return addString(OCUPACION, occupation, toLocalDate(startDate), toLocalDate(endDate));
	}

	public Optional<String> getQuoteGroup() {
		return getQuoteGroup(toLocalDate(startDate));
	}
	
	public Employee setQuoteGroup(String quoteGroup) {
		return setString(GRUPO_COTIZACION, quoteGroup);
	}

	public Optional<String> getCollective(LocalDate date) {
		return getData(COLECTIVO_TRABAJADORES, date, String.class);
	}
	
	public Optional<String> getCno(LocalDate date) {
		return getData(CNO, date, String.class);
	}
	
	public Employee setCollective(String collective) {
		return setString(COLECTIVO_TRABAJADORES, collective);
	}
	
	public Employee setCno(String cno) {
		return setString(CNO, cno);
	}
	
	public Collection<Data<String>> getQuoteGroups() {
		return getDatas(GRUPO_COTIZACION, String.class);
	}

	public Optional<String> getQuoteGroup(LocalDate date) {
		return getData(GRUPO_COTIZACION, date, String.class);
	}

	public Employee addQuoteGroup(String quoteGroup, Date startDate, Date endDate) {
		return addString(GRUPO_COTIZACION, quoteGroup, toLocalDate(startDate), toLocalDate(endDate));
	}

	public Optional<Double> getFactor() {
		return getFactor(toLocalDate(startDate));
	}

	public Employee setFactor(Double factor) {
		return setNumber(COEFICIENTE_PARCIALIDAD, factor);
	}

	public Collection<Data<Double>> getFactors() {
		return getDatas(COEFICIENTE_PARCIALIDAD, Double.class);
	}

	public Optional<Double> getFactor(LocalDate date) {
		return getData(COEFICIENTE_PARCIALIDAD, date, Double.class );
	}
	
	public Employee addFactor(Double factor, Date startDate, Date endDate) {
		return addNumber(COEFICIENTE_PARCIALIDAD, factor, toLocalDate(startDate), toLocalDate(endDate));
	}
	
	public Optional<String> getMdCtz(LocalDate date) {
		return getData(MODELO_COTIZACION_AGRARIO, date, String.class );
	}
	
	public Employee setMdCtz(String modelCtz) {
		addData(MODELO_COTIZACION_AGRARIO, modelCtz, this.startDate, this.endDate);
		return this;
	}

	public Map<String, Collection<ExpressionData>> getDatas() {
		return Collections.unmodifiableMap(dataMap);
	}
	
	public Map<String, Collection<ExpressionData>> getInfos() {
		return Collections.unmodifiableMap(infoMap);
	}
	
	public void addData(String name, String expression, Date startDate, Date endDate) {
		addData(name, expression, toLocalDate(startDate), toLocalDate(endDate));
	}
	
	public void addData(String name, String expression, java.sql.Date startDate, java.sql.Date endDate) {
		addData(name, expression, startDate.toLocalDate(), endDate == null ? null : endDate.toLocalDate());
	}
	
	public void addInfo(String name, String expression, Date startDate, Date endDate) {
		addInfo(name, expression, toLocalDate(startDate), toLocalDate(endDate));
	}
	
	// ----------------------------------------------------------------- Object
	public Employee setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
		return this;
	}
	
	@Override
	public int hashCode() {
		if ( employeeId != null )
			return Objects.hashCode(employeeId);
		else
			return Objects.hash(naf, ccc, startDate, endDate);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Employee ) )
			return false;
		
		Employee employee = (Employee) obj;
		
		if ( employeeId != null ) 
			return Objects.equals(employeeId, employee.employeeId);
		else 
			return Objects.equals(naf, employee.naf)
					&& Objects.equals(ccc, employee.ccc)
					&& Objects.equals(startDate, employee.startDate)
					&& Objects.equals(endDate, employee.endDate);
	}

	// -------------------------------------------------------------- protected
	
	protected Employee setString(String name, String str) {
		String expression = str != null ? String.format("\"%s\"", str): null;
		return setData(name, expression );
	}

	protected <T extends Number> Employee setNumber(String name, T t) {
		String expression = t != null ? Double.toString(t.doubleValue()): null;
		return setData(name, expression );
	}

	protected Employee addString(String name, String str, LocalDate startDate, LocalDate endDate) {
		String expression = str != null ? String.format("\"%s\"", str): null;
		return addData(name, expression, startDate, endDate );
	}

	protected <T extends Number> Employee addNumber(String name, T t, LocalDate startDate, LocalDate endDate) {
		String expression = t != null ? Double.toString( t.doubleValue()): null;
		return addData(name, expression, startDate, endDate );
	}
	
	protected ExpressionData setExpressionData(String expression) {
		ExpressionData expressionData = new ExpressionData();
		expressionData.expression = expression;
		expressionData.startDate = toLocalDate(this.startDate);
		expressionData.endDate = toLocalDate(this.endDate);
		return expressionData;
	}

	protected Employee setData(String name, String expression) {
		dataMap.remove(name);
		ExpressionData expressionData = setExpressionData(expression);
		dataMap.put(name, Arrays.asList(expressionData));
		return this;
	}
	
	public Employee setInfo(String name, String expression) {
		infoMap.remove(name);
		ExpressionData expressionData = setExpressionData(expression);
		infoMap.put(name, Arrays.asList(expressionData));
		return this;
	}
	
	protected Employee addData(String name, String expression, LocalDate startDate, LocalDate endDate) {
		ExpressionData expressionData = new ExpressionData();
		expressionData.endDate = endDate;
		expressionData.startDate = startDate;
		expressionData.expression = expression;

		Collection<ExpressionData> expressionDatas = dataMap.computeIfAbsent(name, key -> new LinkedList<>());
		
		expressionDatas.add(expressionData);
		
		return this;
	}
	
	protected Employee addInfo(String name, String expression, LocalDate startDate, LocalDate endDate) {
		ExpressionData expressionData = new ExpressionData();
		expressionData.endDate = endDate;
		expressionData.startDate = startDate;
		expressionData.expression = expression;

		Collection<ExpressionData> expressionDatas = infoMap.computeIfAbsent(name, key -> new LinkedList<>());
		
		expressionDatas.add(expressionData);
		
		return this;
	}
	
	protected <T> Collection<Data<T>> getDatas(String name, Class<T> type ) {
		return
		getSortedDatas(name)
		.map(d -> {
			Data<T> data = new Data<>();
			data.endDate = d.endDate;
			data.startDate = d.startDate;
			data.value = MVEL.eval(d.expression, type);
			return data;
		})
		.collect(Collectors.toList())
		;
	}
	
	// ---------------------------------------------------------------- private
	
	private Stream<ExpressionData> getSortedDatas(String name ) {
		return dataMap .getOrDefault(name, Collections.emptyList()).stream().sorted((d1,d2) -> d1.startDate.compareTo(d2.startDate) );
	}

	private <T> Optional<T> getData(String name, LocalDate date, Class<T> type) {
		return dataMap.getOrDefault(name, Collections.emptySortedSet())
		.stream()
		.filter( data -> contains(data, date))
		.filter( data -> Objects.nonNull(data.expression))
		.map( data -> MVEL.eval(data.expression, type))
		.findFirst();
	}
	
	private static boolean contains( ExpressionData expressionData, LocalDate date) {
		return contains(expressionData.startDate, expressionData.endDate, date);
	}

	private static boolean contains( LocalDate startDate, LocalDate endDate, LocalDate date) {
		return ( date.compareTo(startDate) >= 0 ) 
				&& ( endDate == null || (date.compareTo(endDate) <= 0) ); 
	}
	
	@SuppressWarnings("deprecation")
    public static Date toDate(LocalDate date) {
		return isNull(date) ? null : new Date(date.getYear() - 1900, date.getMonthValue() -1, date.getDayOfMonth()); 
    }

    public static LocalDate toLocalDate(Date date) {
		return isNull(date) ? null : new Timestamp(date.getTime()).toLocalDateTime().toLocalDate();
    }
}
