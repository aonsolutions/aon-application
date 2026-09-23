package com.esferalia.aon.in.payroll.tgss.sld;


import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Period;
import solutions.aon.seg.social.object.WorkerLiquidation;

public class SLDSalaries {
	
	
	private static final String LIQUIDO_DE_TOTALES = "LIQUIDO DE TOTALES";
	private static final String BONIF_Y_SUBVENC_CON_CARGO_AL_INEM = "BONIF.Y SUBVENC.CON CARGO AL INEM";
	
	public static Collection<Salary> getSLDSalaries(Map<String, Map<String, WorkerLiquidation>> costs, String ccc, Date from, Date to) {
		
		List<Salary> salaries = new LinkedList<Salary>();

		costs.forEach((type, cccCosts ) -> cccCosts.forEach( (naf, liq) -> salaries.add(getSalary(type, ccc, liq, from, to))));
		// same as above, perhaps this is must more clear 
		//for ( Entry<String, Map<String, WorkerLiquidation>> typeEntry : costs.entrySet() )
		//	for ( Entry<String, WorkerLiquidation > cccEntry: typeEntry.getValue().entrySet()  )
		//		salaries.add(getSalary(typeEntry.getKey(), cccEntry.getKey(), cccEntry.getValue()));
		
		return salaries;
		
	}
	
	public static Salary getSalary(String type, String ccc, String naf, Map<Period, Map<String, Calc>> periodCalcs) {
		
		Salary salary = new Salary();
		
		List<Period> periods = 
		periodCalcs.keySet().stream()
		.filter(p -> p != null )
		.collect(Collectors.toList());

		periods.sort((p1,p2) -> p1.getStartDate().compareTo(p2.getStartDate()) );
		
		Date startDate = periods.get(0).getStartDate();
		Date endDate = periods.get(periods.size()-1).getEndDate();
		
		salary.setEndDate(endDate);
		salary.setIssueDate(endDate);
		salary.setStartDate(startDate);
		salary.setSalaryDays(getDaysBetween(endDate, startDate));
		
		salary.setEnterpriseCCC(ccc);
		salary.setEmployeeSSNumber(naf);
		salary.setSalaryType(SalaryType.valueOf(type));
		
		
		// Data
		periodCalcs.forEach((period, calcs) -> {
			Optional.ofNullable(period).ifPresent( p -> { 
				
				getContextVariables(p).forEach((var, value) -> 
					salary.addContextData(var.getName(), Double.toString(value), p.getStartDate(), p.getEndDate()));
				
				getContextVariables(calcs).forEach((var, value) -> 
					salary.addContextData(var.getName(), Double.toString(value), p.getStartDate(), p.getEndDate()));
			});
		});
		
		fillSalary(salary, periodCalcs.get(null));
		
		return salary;
	}
	
	public static Salary getSalary(String type, String ccc, String naf, Map<Period, Map<String, Calc>> calcs, Period period) {
		
		Map<Period, Map<String, Calc>> periodCalcs = 
		calcs.entrySet().stream()
		.filter(e -> e.getKey() != null )
		.filter(e -> contains(period, e.getKey()))
		.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		if ( periodCalcs.isEmpty() && "L13".equals(type)) {
			periodCalcs = 
			calcs.entrySet().stream()
			.filter(e -> e.getKey() != null )
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		} else if ( periodCalcs.isEmpty() ) {
			periodCalcs = 
			calcs.entrySet().stream()
			.filter(e -> e.getKey() != null )
			.filter(e -> intersects(period, e.getKey()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}
		
		return getPeriodSalary(type, ccc, naf, periodCalcs);
	}

	public static Collection<Salary> getSalaries(String type, String ccc, String naf, Map<Period, Map<String, Calc>> calcs, Period period) {
		
		Map<Period, Map<String, Calc>> periodCalcs = 
		calcs.entrySet().stream()
		.filter(e -> e.getKey() != null )
		.filter(e -> contains(period, e.getKey()))
		.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		if ( periodCalcs.isEmpty() && "L13".equals(type)) {
			periodCalcs = 
			calcs.entrySet().stream()
			.filter(e -> e.getKey() != null )
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		} else if ( periodCalcs.isEmpty() ) {
			periodCalcs = 
			calcs.entrySet().stream()
			.filter(e -> e.getKey() != null )
			.filter(e -> intersects(period, e.getKey()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		} 
		
		if ("L02".equals(type) ) {
			return periodCalcs.entrySet().stream()
			.map( entry -> getPeriodSalary(type, ccc, naf, Collections.singletonMap(entry.getKey(), entry.getValue()))).toList();
		} else {
			Salary salary =  getPeriodSalary(type, ccc, naf, periodCalcs);
			return Collections.singleton(salary);
		}
		
	}

	private static Salary getPeriodSalary(String type, String ccc, String naf, Map<Period, Map<String, Calc>> periodCalcs) {
		
		Salary salary = new Salary();
		
		List<Period> periods = 
		periodCalcs.keySet().stream()
		.collect(Collectors.toList());

		periods.sort((p1,p2) -> p1.getStartDate().compareTo(p2.getStartDate()) );
		
		Date startDate = periods.get(0).getStartDate();
		Date endDate = periods.get(periods.size()-1).getEndDate();
		
		salary.setEndDate(endDate);
		salary.setIssueDate(endDate);
		salary.setStartDate(startDate);
		salary.setSalaryDays(getDaysBetween(endDate, startDate));
		
		salary.setEnterpriseCCC(ccc);
		salary.setEmployeeSSNumber(naf);
		salary.setSalaryType(SalaryType.valueOf(type));
		
		
		// Data
		periodCalcs.forEach((period, calcs) -> {
			Optional.ofNullable(period).ifPresent( p -> { 
				getContextVariables(p).forEach((var, value) -> 
					salary.addContextData(var.getName(), Double.toString(value), p.getStartDate(), p.getEndDate()));
				getContextVariables(calcs).forEach((var, value) -> 
					salary.addContextData(var.getName(), Double.toString(value), p.getStartDate(), p.getEndDate()));
			});
		});
		
		Map<String, Calc> calcs = 
		periodCalcs.values().stream()
		.collect(Collectors.reducing(Collections.emptyMap(), (m1,m2) -> union(m1, m2, (c1,c2) -> union(c1,c2))));
		
		fillSalary(salary, calcs );
		
		
		Double baseCC = periodCalcs.keySet().stream().collect(Collectors.summingDouble(p -> Optional.ofNullable(p.getBaseCC()).orElse(0.00)));
		salary.setCommonContingenciesBase(baseCC);
		Double baseAT = periodCalcs.keySet().stream().collect(Collectors.summingDouble(p -> Optional.ofNullable(p.getBaseAT()).orElse(0.00)));
		salary.setProfessionalContingenciesBase(baseAT);
		Double quoteDays = periodCalcs.keySet().stream().collect(Collectors.summingDouble(p -> Optional.ofNullable(p.getQuoteDays()).orElse(0.00)));
		salary.setSalaryDays(quoteDays.intValue());
		
		Double baseOtherHours = periodCalcs.values().stream().map(map -> map.get("OTRAS HORAS EXTRAS"))
		.filter(calc -> calc != null ).collect(Collectors.summingDouble(calc -> Optional.ofNullable(calc.getBase()).orElse(0.00)));
		salary.setNonEstructuralOvertimeBase(baseOtherHours);

		return salary;
	}

	private static Salary fillSalary(Salary salary, Map<String, Calc> calcs) {
		
		
		// Deductions
		calcs.forEach((description, calc) -> {
			Optional.ofNullable(calc.getEmployee()).ifPresent(amount -> {
				getDeductionType(description).ifPresent(deductionType -> 
					salary.addDeduction(deductionType, amount, getEmployeeContextVariable(description).map(v -> v.getName()).orElse(""), description));
			});
		});

		// Deductions 'Reductions'
		calcs.forEach((description, calc) -> {
			Optional.ofNullable(calc.getEmployee()).ifPresent(amount -> {
				getReductionType(description).ifPresent(deductionType -> 
					salary.addDeduction(deductionType, -amount, "REDUCCION_TGSS", description));
			});
		});

		// Costs 
		calcs.forEach((description, calc) -> {
			Optional.ofNullable(calc.getEnterprise()).ifPresent(amount -> {
				getDeductionType(description).ifPresent(deductionType -> 
					salary.addCost(deductionType,  getEnterpriseContextVariable(description).map(v -> v.getName()).orElse(""), amount, description));
			});
		});
		
		// Costs 'Compensations' 
		calcs.forEach((description, calc) -> {
			Optional.ofNullable(calc.getEnterprise()).ifPresent(amount -> {
				getCompensationType(description).ifPresent(deductionType ->
					salary.addCost(deductionType,  ( deductionType == DeductionType.PROFESSIONAL_CONTINGENCY ? "ATEP_E" : "ECSS_E" ), -amount, description));
			});
		});
		
		// Costs 'Reductions' 
		calcs.forEach((description, calc) -> {
			Optional.ofNullable(calc.getEnterprise()).ifPresent(amount -> {
				getReductionType(description).ifPresent(deductionType -> 
					salary.addCost(deductionType,  getReductionName(description), -amount, description));
			});
		});

		// Bonus
		Optional.ofNullable(calcs.get(BONIF_Y_SUBVENC_CON_CARGO_AL_INEM))
		.ifPresent(calc -> {
			Optional.ofNullable(calc.getEmployee())
			.ifPresent( employee -> 
				salary.addDeduction(DeductionType.BONUS, BONIF_Y_SUBVENC_CON_CARGO_AL_INEM, -employee));
			Optional.ofNullable(calc.getEnterprise())
			.ifPresent( enterprise -> 
				salary.addBonus(null, BONIF_Y_SUBVENC_CON_CARGO_AL_INEM, enterprise));
		});
		
		// Totals
		Optional.ofNullable(calcs.get(LIQUIDO_DE_TOTALES))
		.ifPresent(calc -> {
			salary.setTotalEnterprise(calc.getEnterprise());
			salary.setTotalSSContributions(calc.getEmployee());
		});
		
		// Bases
		{
			double cgcEmployeeBase =
			calcs.entrySet().stream()
			.filter( e -> e.getValue().getBase() != null  )
			.filter( e -> e.getValue().getEmployee() != null  )
			.filter( e -> getBaseDeductionType(e.getKey()) == DeductionType.COMMON_CONTINGENCY )
			.collect(Collectors.summingDouble(e -> e.getValue().getBase()));
			
			double cgpEmployeeBase = 0.00;
			for (DeductionType cgpType : new DeductionType[] { 
							DeductionType.JOB_TRAINING
							,DeductionType.UNEMPLOYMENT
							,DeductionType.COMMON_CONTINGENCY
			}) {
				cgpEmployeeBase =
						calcs.entrySet().stream()
						.filter( e -> e.getValue().getBase() != null  )
						.filter( e -> e.getValue().getEmployee() != null  )
						.filter( e -> getBaseDeductionType(e.getKey()) == cgpType )
						.collect(Collectors.summingDouble(e -> e.getValue().getBase()));
				if ( cgpEmployeeBase > 0.00 ) 
					break;
			}
			
			if ( cgpEmployeeBase == 0.00 )
				cgpEmployeeBase = cgcEmployeeBase;

			salary.setCommonContingenciesBase(cgcEmployeeBase);
			salary.setProfessionalContingenciesBase(cgpEmployeeBase);
			
		}

		return salary;

	}
	
	private static Calc union(Calc c1, Calc c2) {
		
		Calc union = new Calc()
		.setBase(c1.getBase())
		.setEmployee(c1.getEmployee())
		.setEnterprise(c1.getEnterprise())
		.setEmployeePercent(c1.getEmployeePercent())
		.setEnterprisePercent(c1.getEnterprisePercent());
		
		if ( c2.getBase() != null )
			union.setBase(orZero(union.getBase()) + c2.getBase());
		if ( c2.getEmployee() != null )
			union.setEmployee(orZero(union.getEmployee()) + c2.getEmployee());
		if ( c2.getEnterprise() != null )
			union.setEnterprise(orZero(union.getEnterprise()) + c2.getEnterprise());
		if ( c2.getEmployeePercent() != null )
			union.setEmployeePercent(orZero(union.getEmployeePercent()) + c2.getEmployeePercent());
		if ( c2.getEnterprisePercent() != null )
			union.setEnterprisePercent(orZero(union.getEnterprisePercent()) + c2.getEnterprisePercent());
		
		return union;
	}
	
	private static double orZero(Double d) {
		return Optional.ofNullable(d).orElse(0.00);
	}
		
	private static <K,V> Map<K,V> union(Map<K,V> m1, Map<K,V> m2, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		Map<K,V> map = new HashMap<K, V>();
		m1.forEach((k,v) -> map.put(k, v) );
		m2.forEach((k,v) -> map.merge(k, v, remappingFunction ));
		return map;
	}
	
	private static Date max(Date d1, Date d2) {
		return compare(d1, d2) > 0 ? d1 : d2;
	}

	private static Date min(Date d1, Date d2) {
		return compare(d1, d2) < 0 ? d1 : d2;
	}

	private static int compare(Date d1, Date d2) {
		if ( d1 == d2 )
			return 0;
		if ( d1 == null )
			return 1;
		if ( d2 == null )
			return -1;
		return d1.compareTo(d2);
	}
	
	private static boolean intersects ( Period p1, Period p2) {
		Date start = max(p1.getStartDate(), p2.getStartDate());
		Date end = min(p1.getEndDate(), p2.getEndDate());
		return compare(start, end) <= 0 ;
	}

	private static boolean contains ( Period p1, Period p2) {
		return p1.getStartDate().compareTo(p2.getStartDate())  <= 0 
				&& ( p1.getEndDate() == null || ( p2.getEndDate() != null && p1.getEndDate().compareTo(p2.getEndDate()) >= 0) 
				);
	}
	

	private static int getDaysBetween(Date endDate, Date startDate ) {
		return (int) Math.floor( ( endDate.getTime()-startDate.getTime() ) / (3600.00*24*1000) ) + 1;		
	}
	
	private static Optional<DeductionType>  getCompensationType(String description) {
		switch (description) {
		case "COMPENSACION IT ENFERMEDAD COMUN" : 
			return Optional.of(DeductionType.IN_KIND);
		case "COMP.IT POR ACCIDENTE DE TRABAJO" : 
			return Optional.of(DeductionType.IN_KIND);
		default:
			return Optional.empty();
		}
		
	}

	private static Optional<DeductionType>  getReductionType(String description) {
		switch (description) {
		case "REDUCCIONES A CARGO DE LA TGSS":
		case "REDUCCION APORTACI\u00D3N PLAN PENSIONES":
		case "REDUCCI\u00D3N PR\u00C1CTICAS FORMATIVAS":
			return Optional.of(DeductionType.COMMON_CONTINGENCY);
		case "REDUCCIONES SEA A CARGO TGSS":
		case "REDUCCIONES SEA EN IT A CARGO DEL SPEE":
			return Optional.of(DeductionType.SEA);
		default:
			return Optional.empty();
		}
	}

	private static String  getReductionName(String description) {
		switch (description) {
		case "REDUCCIONES SEA A CARGO TGSS":
			return ContextVariable.SEA_ENTERPRISE.getName();
		case "REDUCCIONES SEA EN IT A CARGO DEL SPEE":
			return "RED_SEA_E";
		case "REDUCCION APORTACI\u00D3N PLAN PENSIONES":
			return "RED_PPE_E";
		default:
			return "RED_CGC_E";
		}
	}

	private static Optional<DeductionType>  getDeductionType(String description) {
		switch (description) {
		case "FOGASA":
		case "FOGASA COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(DeductionType.FOGASA);
		case "DESEMPLEO":
		case "DESEMPLEO COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(DeductionType.UNEMPLOYMENT);
		case "CONTINGENCIAS COMUNES":
		case "CONTING.COM.COTIZ.EMPRESARIAL" :
		case "INCREMENTO COTI.CONTRATO TEMP.MENOR 6-7D":
		case "COTIZ.ADICIONAL CONTRATOS TEMP.CORTA DUR":
			return Optional.of(DeductionType.COMMON_CONTINGENCY);
		case "MEI COTIZACI\u00D3N EMPRESARIAL":
		case "MECANISMO EQUIDAD INTERGENERACIONAL":
			return Optional.of(DeductionType.MEI);
		case "IT DE ACCIDENTES DE TRABAJO":
		case "CUOTA IT DE AT Y EP DE SITUACIONES ESPEC":
			return Optional.of(DeductionType.PROFESSIONAL_CONTINGENCY);
		case "IMS DE ACCIDENTES DE TRABAJO":
		case "CUOTA IMS DE AT Y EP DE SITUACIONES ESPE":
			return Optional.of(DeductionType.PROFESSIONAL_CONTINGENCY);
		case "FORMACI\u00D3N PROFESIONAL":
		case "FORMACI\u00D3N PROFESIONAL COTIZ. EMPRESA.":
			return Optional.of(DeductionType.JOB_TRAINING);
		case "OTRAS HORAS EXTRAS":
			return Optional.of(DeductionType.NON_STRUCTURAL_OVERTIME);
		case "COTIZACION ESPECIAL SOLIDARIDAD":
		case "1º TRAMO COTIZACION ADIC. SOLIDARIDAD":
		case "2º TRAMO COTIZACION ADIC. SOLIDARIDAD":
		case "3º TRAMO COTIZACION ADIC. SOLIDARIDAD":
			return Optional.of(DeductionType.SOLIDARITY);
		case "LIQUIDO DE TOTALES":
		case "LIQUIDO CONTINGENCIAS COMUNES":
		case "LIQUIDO DE OTRAS COTIZACIONES":
		case "LIQUIDO DE ACCIDENTES DE TRABAJO" :
		case "COMPENSACION IT ENFERMEDAD COMUN" : 
		case "COMP.IT POR ACCIDENTE DE TRABAJO" :
		case "BONIF.Y SUBVENC.CON CARGO AL INEM":
		case "REDUCCIONES SEA A CARGO TGSS":
		case "REDUCCIONES SEA EN IT A CARGO DEL SPEE":
		case "REDUCCIONES A CARGO DE LA TGSS":
		case "REDUCCION APORTACI\u00D3N PLAN PENSIONES":
		case "REDUCCI\u00D3N PR\u00C1CTICAS FORMATIVAS":
			return Optional.empty();
		default:
			return Optional.of(DeductionType.OTHER);
		}
	}
	
	
	private static Map<ContextVariable, Double>  getContextVariables(Period period ) {
		Map<ContextVariable, Double> contextVaribles = new HashMap<ContextVariable, Double>();
		
		Optional.ofNullable(period.getQuoteDays()).ifPresent(value -> contextVaribles.putIfAbsent(QUOTE_DAYS, value));
		Optional.ofNullable(period.getQuoteDays()).ifPresent(value -> contextVaribles.putIfAbsent(SALARY_HOURS, value));

		return contextVaribles;
		
	}

	private static Map<ContextVariable, Double>  getContextVariables(Map<String, Calc> calcs ) {
		
		Map<ContextVariable, Double> contextVaribles = new HashMap<ContextVariable, Double>();
		
		calcs.forEach((description, calc) -> {
			
			Optional.ofNullable(calc.getEmployeePercent())
			.ifPresent(percent -> 
			getEmployeePercentContextVariable(description)
			.ifPresent(var -> contextVaribles.putIfAbsent(var, percent)));

			Optional.ofNullable(calc.getEnterprisePercent())
			.ifPresent(percent -> 
			getEnterprisePercentContextVariable(description)
			.ifPresent(var -> contextVaribles.putIfAbsent(var, percent)));

			Optional.ofNullable(calc.getBase())
			.ifPresent(base ->
			Optional.ofNullable(calc.getEmployee())
			.ifPresent( employee ->
			getEmployeeBaseContextVariable(description)
			.ifPresent(var -> contextVaribles.putIfAbsent(var, calc.getBase()))));
			
			Optional.ofNullable(calc.getBase())
			.ifPresent(base ->
			Optional.ofNullable(calc.getEnterprise())
			.ifPresent( enterprise ->
			getEnterpriseBaseContextVariable(description)
			.ifPresent(var -> contextVaribles.putIfAbsent(var, calc.getBase()))));

			Optional.ofNullable(contextVaribles.get(ContextVariable.CGC_BASE_ENTERPRISE))
			.ifPresent( cgcBase -> contextVaribles.putIfAbsent(ContextVariable.CGP_BASE_ENTERPRISE, cgcBase) );

			Optional.ofNullable(calc.getEmployee())
			.ifPresent(employee -> 
			getEmployeeContextVariable(description)
			.ifPresent(var -> contextVaribles.merge(var, employee, (d1,d2) -> d1 + d2 )));
			
			Optional.ofNullable(calc.getEnterprise())
			.ifPresent(enterprise -> 
			getEnterpriseContextVariable(description)
			.ifPresent(var -> contextVaribles.merge(var, enterprise, (d1,d2) -> d1 + d2 )));
			
		});
		
		Optional.ofNullable(contextVaribles.get(ContextVariable.CGC_BASE))
		.ifPresent( cgcBase -> contextVaribles.putIfAbsent(ContextVariable.CGP_BASE, cgcBase) );

		return contextVaribles;
	}
	
	private static Optional<ContextVariable> getEmployeeContextVariable (String description) {
		switch (description) {
		case "CONTINGENCIAS COMUNES":
			return Optional.of(ContextVariable.CGC_EMPLOYEE);
		case "MECANISMO EQUIDAD INTERGENERACIONAL":
			return Optional.of(ContextVariable.MEI_EMPLOYEE);
		case "FORMACI\u00D3N PROFESIONAL":
			return Optional.of(ContextVariable.FP_EMPLOYEE);
		case "DESEMPLEO":
			return Optional.of(ContextVariable.UNEMPLOY_EMPLOYEE);
		case "OTRAS HORAS EXTRAS":
			return Optional.of(ContextVariable.NON_STRUCTURAL_OVERTIME_EMPLOYEE);
		case "COTIZACION ESPECIAL SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_EMPLOYEE);
		case "1º TRAMO COTIZACION ADIC. SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_FIRST_EMPLOYEE);
		case "2º TRAMO COTIZACION ADIC. SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_SECOND_EMPLOYEE);
		case "3º TRAMO COTIZACION ADIC. SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_THIRD_EMPLOYEE);
		default:
			return Optional.empty();
		}
	}

	private static Optional<ContextVariable> getEmployeePercentContextVariable (String description) {
		switch (description) {
		case "CONTINGENCIAS COMUNES":
			return Optional.of(ContextVariable.CGC_EMPLOYEE_PERCENT);
		case "MECANISMO EQUIDAD INTERGENERACIONAL":
			return Optional.of(ContextVariable.MEI_EMPLOYEE_PERCENT);
		case "FORMACI\u00D3N PROFESIONAL":
			return Optional.of(ContextVariable.FP_EMPLOYEE_PERCENT);
		case "DESEMPLEO":
			return Optional.of(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT);
		case "OTRAS HORAS EXTRAS":
			return Optional.of(ContextVariable.NON_STRUCTURAL_OVERTIME_EMPLOYEE_PERCENT);
		default:
			return Optional.empty();
		}
	}
	

	private static Optional<ContextVariable> getEnterpriseContextVariable (String description) {
		switch (description) {
		case "FOGASA":
		case "FOGASA COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.FOGASA_ENTERPRISE);
		case "DESEMPLEO":
		case "DESEMPLEO COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.UNEMPLOY_ENTERPRISE);
		case "CONTINGENCIAS COMUNES":
		case "CONTING.COM.COTIZ.EMPRESARIAL":
			return Optional.of(ContextVariable.CGC_ENTERPRISE);
		case "MEI COTIZACI\u00D3N EMPRESARIAL":
		case "MECANISMO EQUIDAD INTERGENERACIONAL":
			return Optional.of(ContextVariable.MEI_ENTERPRISE);
		case "IT DE ACCIDENTES DE TRABAJO":
		case "CUOTA IT DE AT Y EP DE SITUACIONES ESPEC":
			return Optional.of(ContextVariable.IT_ENTERPRISE);
		case "IMS DE ACCIDENTES DE TRABAJO":
		case "CUOTA IMS DE AT Y EP DE SITUACIONES ESPE":
			return Optional.of(ContextVariable.IMS_ENTERPRISE);
		case "FORMACI\u00D3N PROFESIONAL":
		case "FORMACI\u00D3N PROFESIONAL COTIZ. EMPRESA.":
			return Optional.of(ContextVariable.FP_ENTERPRISE);
		case "OTRAS HORAS EXTRAS":
			return Optional.of(ContextVariable.NON_STRUCTURAL_OVERTIME_ENTERPRISE);
		case "COTIZ.ADICIONAL CONTRATOS TEMP.CORTA DUR":
			return Optional.of(ContextVariable.CGC_ENTERPRISE_TEMP);
		case "COTIZACION ESPECIAL SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_ENTERPRISE);
		case "1º TRAMO COTIZACION ADIC. SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_FIRST_ENTERPRISE);
		case "2º TRAMO COTIZACION ADIC. SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_SECOND_ENTERPRISE);
		case "3º TRAMO COTIZACION ADIC. SOLIDARIDAD":
			return Optional.of(ContextVariable.SOLIDARITY_THIRD_ENTERPRISE);
			
		default:
			return Optional.empty();
		}
	}

	private static Optional<ContextVariable> getEnterprisePercentContextVariable (String description) {
		switch (description) {
		case "FOGASA":
		case "FOGASA COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.FOGASA_ENTERPRISE_PERCENT);
		case "DESEMPLEO":
		case "DESEMPLEO COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT);
		case "CONTINGENCIAS COMUNES":
		case "CONTING.COM.COTIZ.EMPRESARIAL":	
			return Optional.of(ContextVariable.CGC_ENTERPRISE_PERCENT);
		case "MEI COTIZACI\u00D3N EMPRESARIAL":
		case "MECANISMO EQUIDAD INTERGENERACIONAL":
			return Optional.of(ContextVariable.MEI_ENTERPRISE_PERCENT);
		case "IT DE ACCIDENTES DE TRABAJO":
		case "CUOTA IT DE AT Y EP DE SITUACIONES ESPEC":
			return Optional.of(ContextVariable.IT_ENTERPRISE_PERCENT);
		case "IMS DE ACCIDENTES DE TRABAJO":
		case "CUOTA IMS DE AT Y EP DE SITUACIONES ESPE":
			return Optional.of(ContextVariable.IMS_ENTERPRISE_PERCENT);
		case "FORMACI\u00D3N PROFESIONAL":
		case "FORMACI\u00D3N PROFESIONAL COTIZ. EMPRESA.":
			return Optional.of(ContextVariable.FP_ENTERPRISE_PERCENT);
		case "OTRAS HORAS EXTRAS":
			return Optional.of(ContextVariable.NON_STRUCTURAL_OVERTIME_ENTERPRISE_PERCENT);
		default:
			return Optional.empty();
		}
	}
	
	private static Optional<ContextVariable> getEmployeeBaseContextVariable (String description) {
		switch (description) {
		case "FOGASA":
		case "FOGASA COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.CGP_BASE);
		case "DESEMPLEO":
		case "DESEMPLEO COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.CGP_BASE);
		case "CONTINGENCIAS COMUNES":
		case "CONTING.COM.COTIZ.EMPRESARIAL":	
			return Optional.of(ContextVariable.CGC_BASE);
		case "IT DE ACCIDENTES DE TRABAJO":
		case "CUOTA IT DE AT Y EP DE SITUACIONES ESPEC":
			return Optional.of(ContextVariable.CGP_BASE);
		case "IMS DE ACCIDENTES DE TRABAJO":
		case "CUOTA IMS DE AT Y EP DE SITUACIONES ESPE":
			return Optional.of(ContextVariable.CGP_BASE);
		case "FORMACI\u00D3N PROFESIONAL":
		case "FORMACI\u00D3N PROFESIONAL COTIZ. EMPRESA.":
			return Optional.of(ContextVariable.CGP_BASE);
//		case "COMP.IT POR ACCIDENTE DE TRABAJO" : 
//			return Optional.of(ContextVariable.IT_BASE);
		default:
			return Optional.empty();
		}
	}
	
	private static Optional<ContextVariable> getEnterpriseBaseContextVariable (String description) {
		switch (description) {
		case "FOGASA":
		case "FOGASA COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.CGP_BASE_ENTERPRISE);
		case "DESEMPLEO":
		case "DESEMPLEO COTIZACI\u00D3N EMPRESARIAL":
			return Optional.of(ContextVariable.CGP_BASE_ENTERPRISE);
		case "CONTINGENCIAS COMUNES":
		case "CONTING.COM.COTIZ.EMPRESARIAL":	
			return Optional.of(ContextVariable.CGC_BASE_ENTERPRISE);
		case "IT DE ACCIDENTES DE TRABAJO":
		case "CUOTA IT DE AT Y EP DE SITUACIONES ESPEC":
			return Optional.of(ContextVariable.CGP_BASE_ENTERPRISE);
		case "IMS DE ACCIDENTES DE TRABAJO":
		case "CUOTA IMS DE AT Y EP DE SITUACIONES ESPE":
			return Optional.of(ContextVariable.CGP_BASE_ENTERPRISE);
		case "FORMACI\u00D3N PROFESIONAL":
		case "FORMACI\u00D3N PROFESIONAL COTIZ. EMPRESA.":
			return Optional.of(ContextVariable.CGP_BASE_ENTERPRISE);
		default:
			return Optional.empty();
		}
	}

	private static DeductionType  getBaseDeductionType(String description) {
		switch (description) {
		case "FOGASA":
		case "FOGASA COTIZACI\u00D3N EMPRESARIAL":
			return DeductionType.FOGASA;
		case "DESEMPLEO":
		case "DESEMPLEO COTIZACI\u00D3N EMPRESARIAL":
			return DeductionType.UNEMPLOYMENT;
		case "CONTINGENCIAS COMUNES":
		case "CONTING.COM.COTIZ.EMPRESARIAL":	
			return DeductionType.COMMON_CONTINGENCY;
		case "IT DE ACCIDENTES DE TRABAJO":
		case "CUOTA IT DE AT Y EP DE SITUACIONES ESPEC":
			return DeductionType.IT;
		case "IMS DE ACCIDENTES DE TRABAJO":
		case "CUOTA IMS DE AT Y EP DE SITUACIONES ESPE":
			return DeductionType.IMS;
		case "FORMACI\u00D3N PROFESIONAL":
		case "FORMACI\u00D3N PROFESIONAL COTIZ. EMPRESA.":
			return DeductionType.JOB_TRAINING;
		default:
			return null;
		}
	}

	private static Salary getSalary(String type, String ccc, WorkerLiquidation liquidation, Date from, Date to ) {
		
		Salary salary = new Salary();
		
		salary.setEndDate(to);
		salary.setIssueDate(to);
		salary.setStartDate(from);
		salary.setEnterpriseCCC(ccc);
		salary.setSalaryType(SalaryType.valueOf(type));
		
		liquidation.accept(new WorkerLiquidation.NoopVisitor() {

			@Override
			public void visitNss(String nss) {
				salary.setEmployeeSSNumber(nss);
			}

			@Override
			public void visitCaf(String caf) {
				salary.setEmployeeName(caf);
			}
			
			@Override
			public void visitCcBase(Float ccBase) {
				salary.setCommonContingenciesBase(ccBase.doubleValue());
				salary.setContextData(ContextVariable.CGC_BASE.getName(), ccBase.toString(), from, to);
			}
			
			@Override
			public void visitCcBusinessFee(Float ccLiquidBusinessFee) {
				salary.addCost(DeductionType.COMMON_CONTINGENCY, ContextVariable.CGC_ENTERPRISE.getName(), ccLiquidBusinessFee.doubleValue(), "CONTINGENCIAS COMUNES");
				salary.setContextData(ContextVariable.CGC_ENTERPRISE.getName(), ccLiquidBusinessFee.toString(), from, to);
			}

			@Override
			public void visitCcWorkerFee(Float ccLiquidWorkerFee) {
				salary.addDeduction(DeductionType.COMMON_CONTINGENCY, ccLiquidWorkerFee.doubleValue(), ContextVariable.CGC_EMPLOYEE.getName(),"CONTINGENCIAS COMUNES");
				salary.setContextData(ContextVariable.CGC_EMPLOYEE.getName(), ccLiquidWorkerFee.toString(), from, to);
			}

			@Override
			public void visitItWorkAccidentBusinessFee(Float itWorkAccidentBusinessFee) {
				salary.addCost(DeductionType.IT, ContextVariable.IT_ENTERPRISE.getName() , itWorkAccidentBusinessFee.doubleValue(), "IT DE ACCIDENTES DE TRABAJO");
				salary.setContextData(ContextVariable.IT_ENTERPRISE.getName(), itWorkAccidentBusinessFee.toString(), from, to);
			}

			@Override
			public void visitItWorkAccidentWorkerFee(Float itWorkAccidentWorkerFee) {
				//salary.addDeduction(DeductionType.IT, itWorkAccidentWorkerFee.doubleValue(), "IT DE ACCIDENTES DE TRABAJO");
			}

			@Override
			public void visitImsWorkAccidentBusinessFee(Float imsWorkAccidentBusinessFee) {
				salary.addCost(DeductionType.IMS, ContextVariable.IMS_ENTERPRISE.getName() , imsWorkAccidentBusinessFee.doubleValue(), "IMS DE ACCIDENTES DE TRABAJO");
				salary.setContextData(ContextVariable.IMS_ENTERPRISE.getName(), imsWorkAccidentBusinessFee.toString(), from, to);
			}

			@Override
			public void visitImsWorkAccidentWorkerFee(Float imsWorkAccidentWorkerFee) {
				//salary.addDeduction(DeductionType.IMS, imsWorkAccidentWorkerFee.doubleValue(), "IMS DE ACCIDENTES DE TRABAJO");
			}

			@Override
			public void visitWorkAccidentLiquidBase(Float workAccidentLiquidBase) {
				salary.setProfessionalContingenciesBase(workAccidentLiquidBase.doubleValue());
				salary.setContextData(ContextVariable.CGP_BASE.getName(), workAccidentLiquidBase.toString(), from, to);
			}


			@Override
			public void visitUnemploymentBusinessFee(Float unemploymentBusinessFee) {
				salary.addCost(DeductionType.UNEMPLOYMENT, ContextVariable.UNEMPLOY_ENTERPRISE.getName() , unemploymentBusinessFee.doubleValue(), "DESEMPLEO");
				salary.setContextData(ContextVariable.UNEMPLOY_ENTERPRISE.getName(), unemploymentBusinessFee.toString(), from, to);
			}

			@Override
			public void visitUnemploymentWorkerFee(Float unemploymentWorkerFee) {
				salary.addDeduction(DeductionType.UNEMPLOYMENT, unemploymentWorkerFee.doubleValue(), ContextVariable.UNEMPLOY_EMPLOYEE.getName(),"DESEMPLEO");
				salary.setContextData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(), unemploymentWorkerFee.toString(), from, to);
			}

			@Override
			public void visitFogasaBase(Float fogasaBase) {
				salary.setProfessionalContingenciesBase(fogasaBase.doubleValue());
			}
			
			@Override
			public void visitFogasaBusinessFee(Float fogasaBusinessFee) {
				salary.addCost(DeductionType.FOGASA, ContextVariable.FOGASA_ENTERPRISE.getName() , fogasaBusinessFee.doubleValue(), "FOGASA");
				salary.setContextData(ContextVariable.FOGASA_ENTERPRISE.getName(), fogasaBusinessFee.toString(), from, to);
			}

			@Override
			public void visitFogasaWorkerFee(Float fogasaWorkerFee) {
				//salary.addDeduction(DeductionType.FOGASA, fogasaWorkerFee.doubleValue(), "FOGASA");
			}

			@Override
			public void visitJobTrainingBusinessFee(Float jobTrainingBusinessFee) {
				salary.addCost(DeductionType.JOB_TRAINING, ContextVariable.FP_ENTERPRISE.getName() , jobTrainingBusinessFee.doubleValue(), "FORMACIÓN PROFESIONAL");
				salary.setContextData(ContextVariable.FP_ENTERPRISE.getName(), jobTrainingBusinessFee.toString(), from, to);
			}

			@Override
			public void visitJobTrainingWorkerFee(Float jobTrainingWorkerFee) {
				salary.addDeduction(DeductionType.JOB_TRAINING, jobTrainingWorkerFee.doubleValue(), ContextVariable.FP_EMPLOYEE.getName(), "FORMACIÓN PROFESIONAL");
				salary.setContextData(ContextVariable.FP_EMPLOYEE.getName(), jobTrainingWorkerFee.toString(), from, to);
			}

			@Override
			public void visitGrantsAndBonusesBusinessFee(Float grantsAndBonusesBusinessFee) {
				salary.addBonus(null, "BONIFICACIONES", grantsAndBonusesBusinessFee.doubleValue());
			}

			@Override
			public void visitGrantsAndBonusesWorkerFee(Float grantsAndBonusesWorkerFee) {
				salary.addDeduction(DeductionType.BONUS, "BONIFICACIONES", grantsAndBonusesWorkerFee.doubleValue());
			}

			@Override
			public void visitTotalLiquidBusinessFee(Float totalLiquidBusinessFee) {
				salary.setTotalEnterprise(totalLiquidBusinessFee.doubleValue());
				salary.setContextData(ContextVariable.ENTERPRISE_QUOTA.getName(), totalLiquidBusinessFee.toString(), from, to);
			}

			@Override
			public void visitTotalLiquidWorkerFee(Float totalLiquidWorkerFee) {
				salary.setTotalSSContributions(totalLiquidWorkerFee.doubleValue());
				salary.setContextData(ContextVariable.EMPLOYEE_QUOTA.getName(), totalLiquidWorkerFee.toString(), from, to);
			}

			
		});
		
		
		return salary;
	}
	
	

	

}
