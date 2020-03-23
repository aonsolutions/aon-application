package com.esferalia.aon.altai.tgss.creta;

import static com.esferalia.aon.altai.tgss.creta.Utils.fecha2Date;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Calculos;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.DatoCalculado;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Tramo;

public class Calculos2Aon {
	
	private static final String C500_VALUE = "__C500";
	private static final String C500_CHECK = "C500_CHECK";
	private static final String C500_WARN = "C500_WARN";

	private static interface TramoCallback  {
		void tramo(String ccc, String naf, Tramo tramo);
	}
	
	Condition where;
	DSLContext dslContext;
	Connection connection;
	


	public Calculos2Aon(Connection connection, Condition where ) {
		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		
		this.where = where;
		this.connection = connection;
		this.dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	}
	
	public void fix(File file) throws IOException {
		setUpFix();
		dslContext.transaction((configuration)->{			
		parse(file, this::fixTramo);
		});
	}
	
	public void check(File file) throws IOException {
		parse(file, this::checkTramo);
	}

	// ------------------------------------------------------------------------
	
	private void parse(File file, TramoCallback cb) throws IOException {
		InputStream is = null; 
		try {
			is = new FileInputStream(file);
			parse(is, cb);
		} catch ( JAXBException e ) {
		}
		finally {
			if ( is != null )
				is.close();
		}
	}
	
	private void parse(InputStream is, TramoCallback cb) throws JAXBException {
		Calculos calculos = Utils.unmarshal(Calculos.class, is);
		
		liquidacion2Aon(calculos.getLiquidacion(),cb);
	}
	
	// ------------------------------------------------------------------------
	
	private void liquidacion2Aon(Liquidacion liquidacion, TramoCallback cb) {
		CtaCot ctaCot = liquidacion.getCcc();
		String ccc = String.format("%s%s", ctaCot.getProvincia() ,ctaCot.getNumero());

		liquidacion.getTrabajadores().getTrabajador().stream()
		.forEach(trabajador -> trabajador2Aon(ccc, trabajador,cb) );
		;
	}
	private void trabajador2Aon(String ccc, Trabajador trabajador, TramoCallback cb) {
		String naf = trabajador.getNaf();
		trabajador.getTramos().getTramo().stream()
		.filter(tramo -> tramo.getCalculosTramo() != null )
		.sorted( Calculos2Aon::compare  )
		.forEach(tramo ->cb.tramo(ccc, naf, tramo));
	}
	
	private void fixTramo ( String ccc, String naf, Tramo tramo) {
		Fecha fechaDesde = tramo.getFechaDesde();
		Fecha fechaHasta = tramo.getFechaHasta();
		
		Date fromDate = fecha2Date(fechaDesde);
		Date toDate = fecha2Date(fechaHasta);

		dslContext
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))
		.leftJoin(ENTERPRISE_ACTIVITY).on(CONTRACT.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
		.leftJoin(CNAE2009).onKey()
		.where(ENTERPRISE_CCC.CCC.eq(ccc))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(naf))
		.and(CONTRACT.START_DATE.le(toDate))
		.and(CONTRACT.END_DATE.isNull()
		.or(CONTRACT.END_DATE.ge(fromDate)))
		.and(where)
		.fetchStream()
		.forEach(r -> { 
			//logAvisos(tramo);
			//fixITs(tramo, fromDate, toDate, r);
			
			fixInformacionAfiliacion(tramo, fromDate, toDate,r);
			fixBaseCgc(tramo, fromDate, toDate, r);
		}
		);
		
		
	}
	
	private void logAvisos(Tramo tramo ) {
		if ( tramo.getAvisos() == null )
			return;
		tramo.getAvisos().getAviso().stream()
		.forEach(aviso -> System.out.printf("AVISO [%s]: %s\r\n", aviso.getCodigoAviso(), aviso.getDescripcion()));
		;
		
	}
	
	private void fixBaseCgc(Tramo tramo, Date fromDate, Date toDate, Record r) {
		if ( !isWholeMonth(fromDate, toDate))
			return;
		
		Map<String,DatoCalculado> datos = 
		tramo.getCalculosTramo()
		.getDatoCalculado().stream()
		.collect(Collectors.toMap(d->d.getCodigo(), d ->d));
		
		if ( !datos.containsKey("500"))
			return;
		
		dslContext
		.delete(CONTRACT_DEDUCTION)
		.where(CONTRACT_DEDUCTION.CONTRACT.eq(r.get(CONTRACT.ID)))
		.and(CONTRACT_DEDUCTION.START_DATE.eq(fromDate))
		.and(CONTRACT_DEDUCTION.END_DATE.eq(toDate))
		.and(CONTRACT_DEDUCTION.DESCRIPTION.eq(C500_CHECK))
		.execute();
		
		dslContext
		.delete(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
		.and(CONTRACT_DATA.START_DATE.eq(fromDate))
		.and(CONTRACT_DATA.END_DATE.eq(toDate))
		.and(CONTRACT_DATA.NAME.eq(C500_VALUE))
		.execute();

		DatoCalculado dato500 =datos.get("500");
		double valor500 = Integer.parseInt(dato500.getValorBase())/100.00;
		
		dslContext
		.insertInto(CONTRACT_DATA)
		.set(CONTRACT_DATA.DOMAIN, r.get(CONTRACT.DOMAIN))
		.set(CONTRACT_DATA.CONTRACT, r.get(CONTRACT.ID))
		.set(CONTRACT_DATA.START_DATE, fromDate)
		.set(CONTRACT_DATA.END_DATE, toDate)
		.set(CONTRACT_DATA.NAME, C500_VALUE)
		.set(CONTRACT_DATA.EXPRESSION, String.format(Locale.ROOT,"%.2f", valor500))
		.execute();

		dslContext
		.insertInto(CONTRACT_DEDUCTION)
		.set(CONTRACT_DEDUCTION.DOMAIN, r.get(CONTRACT.DOMAIN))
		.set(CONTRACT_DEDUCTION.CONTRACT, r.get(CONTRACT.ID))
		.set(CONTRACT_DEDUCTION.START_DATE, fromDate)
		.set(CONTRACT_DEDUCTION.END_DATE, toDate)
		.set(CONTRACT_DEDUCTION.DESCRIPTION, C500_CHECK)
		.set(CONTRACT_DEDUCTION.EXPRESSION, String.format(Locale.ROOT,"(BASE_CGC == %s ) ? HIDE() : HIDE(%s(%s))", C500_VALUE, C500_WARN, C500_VALUE ))
		.execute();
		
		
		
	}
	
	private void fixITs(Tramo tramo, Date fromDate, Date toDate, Record r) {
		Map<String,DatoCalculado> datos = 
		tramo.getCalculosTramo()
		.getDatoCalculado().stream()
		.collect(Collectors.toMap(d->d.getCodigo(), d ->d));
		
		if ( !datos.containsKey("603") )
			return;
		
		
		if ( datos.containsKey("563") )
			System.out.printf(
			"%s %s: Incapacidad Temporal contingencias comunes pago delegado\r\n",
			r.get(PERSON.FIRST_SURNAME),
			r.get(PERSON.NAME));
		else if ( datos.containsKey("663") )
			System.out.printf(
			"%s %s: Incapacidad Temporal derivada de AT y EP pago delegado\r\n",
			r.get(PERSON.FIRST_SURNAME),
			r.get(PERSON.NAME));
		else if (datos.containsKey("509") )
			System.out.printf(
			"%s %s: "
			+ "Incapacidad Temporal Pago Directo, "
			+ "Maternidad/Paternidad Tiempo Completo, "
			+ "Situación de Riesgo Durante el embarazo/lactancia\r\n",
			r.get(PERSON.FIRST_SURNAME),
			r.get(PERSON.NAME));
		else
			System.out.printf(
			"%s %s: Incapacidad Temporal empresas colaboradoras, excluidas, o 15 primeros días contingencias comunes AT y EP.\r\n",
			r.get(PERSON.FIRST_SURNAME),
			r.get(PERSON.NAME));			
		
		
	}
	
	private void addsItCommon15(Tramo tramo, Date fromDate, Date toDate, Record r) {
	}
	

	private void fixInformacionAfiliacion(Tramo tramo, Date fromDate, Date toDate, Record r) {
		Period period = new Period(fromDate, toDate);
		// Fix InformacionAfiliacion
		Set<ContextVariable> expand = 
		new HashSet<ContextVariable>();
		
		List<ContractDataRecord> datas = 
		dslContext
		.select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
		.fetchStreamInto(CONTRACT_DATA)
		.filter(d -> ContextVariable.isContextVariable(d.getName()) )
		.collect(Collectors.toList() )
		;
		
		deleteErrors(datas);
		
		int domain = r.get(CONTRACT.DOMAIN);
		int contract = r.get(CONTRACT.ID);

		String tgssQuoteGroup = tramo.getInformacionAfiliacion().getGrupoCotizacion();

		fix(QUOTE_GROUP, tgssQuoteGroup, period, r, datas)
		.ifPresent(var -> expand.add(var));
		
		String tgssTC2 = tramo.getInformacionAfiliacion().getTipoContrato();
		fix(TC2, tgssTC2, period, r, datas)
		.ifPresent(var -> expand.add(var));
		
		String tgssOcupacion = tramo.getInformacionAfiliacion().getOcupacion();
		fix(OCCUPATION, lowerCase(tgssOcupacion), period, r, datas)
		.ifPresent(var -> expand.add(var));
		
		String aonCNAE = r.get(CNAE2009.CODE);
		try {
			String tgssCNAE = tramo.getInformacionAfiliacion().getCNAE();
			if ( !AonUtils.equals(aonCNAE, tgssCNAE) ) {
				System.err.printf(
				"%s %s: "
				+ "CNAE '%s' <> '%s' \r\n",
				r.get(PERSON.FIRST_SURNAME),
				r.get(PERSON.NAME),
				aonCNAE, 
				tgssCNAE
				);	
				
				SelectConditionStep<Record1<Integer>> cnae2009Id  = 
				DSL.select(CNAE2009.ID).from(CNAE2009).where(CNAE2009.CODE.eq(tgssCNAE));
				
				dslContext
				.update(ENTERPRISE_ACTIVITY)
				.set(ENTERPRISE_ACTIVITY.CNAE2009,cnae2009Id )
				.where(ENTERPRISE_ACTIVITY.ID.eq(r.get(ENTERPRISE_ACTIVITY.ID)))
				.execute();
			}					
		}
		catch ( Exception e ) {
		}
		
		String tgssCoeficienteTiempoParcial = tramo.getInformacionAfiliacion().getCoeficienteTiempoParcial();
		try {

			double tgssPartialFactor = Double.parseDouble(tgssCoeficienteTiempoParcial)/1000.00;

			if ( tgssPartialFactor > 0.00 &&  tgssPartialFactor < 1.00 && !allMatch(PARTIAL_FACTOR, tgssPartialFactor, period, datas) ) {
				System.err.printf(
				"%s %s: "
				+ ", COEFICIENTE '%s' = '%s'"
				+ " \r\n",
				r.get(PERSON.FIRST_SURNAME),
				r.get(PERSON.NAME),
				findNoMatch(PARTIAL_FACTOR, tgssPartialFactor, period, datas).orElse(0.00),
				tgssPartialFactor
				);					
				fixNoMatch(domain, contract, PARTIAL_FACTOR, tgssPartialFactor, period, datas);
				join(datas, PARTIAL_FACTOR);
				expand.add(PARTIAL_FACTOR);
			}
		} catch ( NumberFormatException | NullPointerException e ) {
			
		}
					
		if ( isLastDayOfMonth(toDate)) 
			expand(contract, toDate, expand);
	}

	private Optional<ContextVariable> fix( ContextVariable var, String tgssStr, Period period, Record r, List<ContractDataRecord> datas) {
		if ( allMatch(var, tgssStr, period, datas))
			return Optional.empty();
		
		System.err.printf(
		"%s %s: "
		+ "%s '%s' <> '%s'"
		+ " \r\n",
		r.get(PERSON.FIRST_SURNAME),
		r.get(PERSON.NAME),
		var.getName(),
		findNoMatch(var, tgssStr, period, datas).orElse("-"),
		tgssStr
		);
		
		fixNoMatch(r.get(CONTRACT.DOMAIN), r.get(CONTRACT.ID), var, tgssStr, period, datas);
		join(datas, var);
		
		return Optional.of(var);
	}
	
	private void checkTramo ( String ccc, String naf, Tramo tramo) {
		Fecha fechaDesde = tramo.getFechaDesde();
		Fecha fechaHasta = tramo.getFechaHasta();
		
		Date fromDate = fecha2Date(fechaDesde);
		Date toDate = fecha2Date(fechaHasta);
		
		String datos = tramo.getCalculosTramo().getDatoCalculado().stream()
		.map(d -> String.format("%s = %s", d.getCodigo(), d.getValorBase()))
		.collect(Collectors.joining(","))
		;
		
		Map<String, DatoCalculado> datosMap = 
				tramo.getCalculosTramo().getDatoCalculado().stream().collect(Collectors.toMap(d -> d.getCodigo() , d -> d ));
		
		DatoCalculado dato500 = datosMap.get("500");
		if ( dato500 == null ) 
			return;

		dslContext
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.where(ENTERPRISE_CCC.CCC.eq(ccc))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(naf))
		.and(CONTRACT.START_DATE.le(toDate))
		.and(CONTRACT.END_DATE.isNull()
			.or(CONTRACT.END_DATE.ge(fromDate)))
		.and(CONTRACT.AGREEMENT_LEVEL.isNotNull())
		.fetchStreamInto(CONTRACT)
		.forEach(contract -> { 
			try {

				Date startDate = getStartDate(fromDate); 
				Date endDate = getEndDate(toDate); 
				Salary salary = calculate(contract, startDate, endDate);

				double _500 = Double.parseDouble(dato500.getValorBase()) / 100.00;
				
				System.out.printf(
				"%1$td-%1$tm %2$td-%2$tm  %3$s, %4$s %5$f = %6$s \r\n", 
				salary.getStartDate(), 
				salary.getEndDate(), 
				salary.getEnterpriseName(), 
				salary.getEmployeeName(), 
				salary.getCommonBase(), _500 );
				;
				
				
				
				
			} catch (ExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SalaryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		);
			
	}

	private Salary calculate (ContractRecord contract, Date startDate, Date endDate) throws ExpressionException, SQLException, SalaryException {
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		
		SQLContractSalaryCalculatorContext ctx = 
		new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		return new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(ctx);
	}
	
	private void setString (ContextVariable key, String str, Map<ContextVariable,Optional<ContractDataRecord>> dataMap, Record contract, Date tramoStartDate, Date tramoEndDate) {
		set(key, String.format("\"%s\"", str), dataMap, contract, tramoStartDate, tramoEndDate);
	}
	
	private void set (ContextVariable key, String expression, Map<ContextVariable,Optional<ContractDataRecord>> dataMap, Record contract, Date tramoStartDate, Date tramoEndDate) {
		
		
		ContractDataRecord contractData = 
		dataMap
		.getOrDefault(key, Optional.empty())
		.orElseGet(() ->{

		ContractDataRecord contractDataRecord = dslContext.newRecord(CONTRACT_DATA);
		contractDataRecord.setContract(contract.get(CONTRACT.ID));
		contractDataRecord.setDomain(contract.get(CONTRACT.DOMAIN)); 
		contractDataRecord.setName(key.getName());
		contractDataRecord.setStartDate(contract.get(CONTRACT.START_DATE)); 
		contractDataRecord.setEndDate(contract.get(CONTRACT.END_DATE));
		
		return contractDataRecord;
		
		}
		)
		;
					
		contractData.setExpression(expression);
		contractData.store();
	}
	
	private void fixNoMatch(int domain, int contract, ContextVariable var, double n, Period p, List<ContractDataRecord> datas) {
		fixNoMatch(domain, contract, var.getName(), Double.toString(n), p, datas);
	}
		
	private void fixNoMatch(int domain, int contract, ContextVariable var, String s, Period p, List<ContractDataRecord> datas) {
		fixNoMatch(domain, contract, var.getName(), String.format("\"%s\"", s), p, datas);
	}
	
	private void fixNoMatch(int domain, int contract, String name, String s, Period p, List<ContractDataRecord> datas) {

		datas
		.stream()
		.sorted(Calculos2Aon::compare)
		.filter(d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.filter(d -> !AonStringUtils.equals(d.getExpression(), s))
		.forEach(d -> {
			new Period(d.getStartDate(),d.getEndDate()).sub(p).forEach( sub -> {
				ContractDataRecord contractData = 
				dslContext.newRecord(CONTRACT_DATA);
				contractData.setDomain(domain);
				contractData.setContract(contract);
				contractData.setName(name);
				contractData.setExpression(d.getExpression());
				contractData.setStartDate(toSQL(sub.getStart()));
				contractData.setEndDate(toSQL(sub.getEnd()));
				contractData.store();
			});
			
			d.delete();
		});
		
		ContractDataRecord newContractData = 
		dslContext.newRecord(CONTRACT_DATA);
		newContractData.setDomain(domain);
		newContractData.setContract(contract);
		newContractData.setName(name);
		newContractData.setExpression(s);
		newContractData.setStartDate(toSQL(p.getStart()));
		newContractData.setEndDate(toSQL(p.getEnd()));
		newContractData.store();

	
	}	
	
	private void expand(int contract, Date date, Set<ContextVariable> vars) {
		expand(contract, date, vars.stream().map(v->v.getName()).toArray(String[]::new));
	}
	
	private void expand(int contract, Date date, String ...names) {	
		dslContext
		.delete(CONTRACT_DATA)
		.where(CONTRACT_DATA.NAME.in(names))
		.and(CONTRACT_DATA.START_DATE.gt(date))
		.execute();

		dslContext
		.update(CONTRACT_DATA)
		.set(CONTRACT_DATA.END_DATE, DSL.castNull(Date.class))
		.where(CONTRACT_DATA.NAME.in(names))
		.and(CONTRACT_DATA.END_DATE.eq(date))
		.execute();
	}
	
	private void setUpFix() {
		dslContext.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.NAME.eq(C500_WARN))
		.and(SYSTEM_DATA.DOMAIN.eq(0))
		.execute()
		;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date _2010StartDate = new Date(calendar.getTimeInMillis());
		
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, C500_WARN)
		.set(SYSTEM_DATA.START_DATE, _2010StartDate )
		.set(SYSTEM_DATA.EXPRESSION, "def ( base ) { "
				+ "(\"<div>La base de contingencias comunes (\" + base + \") remitida a la Seguridad Social en la liquidaci&oacute;n ordinaria (L00) es diferente de la actual.</div>"
				+ "<div>&nbsp;</div></div><div class='aon-text-right'> <span class='aon-icon aon-icon-logo' />aon Solutions</div>\") "
				+ "}")
		.execute()
		;
	}
	
	// ----------------------------------------------------------------- static
	
	private static void join(List<ContractDataRecord> datas, ContextVariable var) {
		join(datas, var.getName());
	}
	
	private static void join(List<ContractDataRecord> datas, String name) {
		ContractDataRecord array [] =
		datas
		.stream()
		.filter(d -> AonStringUtils.equals(d.getName(), name ))		
		.sorted(Calculos2Aon::compare)
		.collect(Collectors.toList())
		.toArray(ContractDataRecord[]::new)
		;
		
		for (int i = 1; i < array.length; i++) {
			ContractDataRecord d1 = array[i-1];
			ContractDataRecord d2 = array[i];
			
			if ( notEquals(d1, d2) )
				continue;

			d2.setStartDate(d1.getStartDate());
			d2.store();
			d1.delete();
			
		} 
		
		
		
	}
	
	
	private static boolean unbound(ContractDataRecord d1, ContractDataRecord d2) {
		Date nextEndDate1 = AonDateUtils.add(d1.getEndDate(), DAY_OF_MONTH,1);
		return nextEndDate1.before(d2.getStartDate());
	}
	
	private static boolean notEquals(ContractDataRecord d1, ContractDataRecord d2) {
		if ( AonStringUtils.equals(d1.getExpression(), d2.getExpression()) )
			return false;
		return true;
	}
	

	private static boolean isLastDayOfMonth(Date date) {
		return getMax(date, DAY_OF_MONTH) == get(date, DAY_OF_MONTH);
	}

	private static boolean isWholeMonth(Date startDate, Date endDate) {
		return  ( get(startDate, DAY_OF_MONTH) == 1) &&
				getMax(endDate, DAY_OF_MONTH) == get(endDate, DAY_OF_MONTH);
	}

	private static Date getEndDate(Date toDate) {
		return getLastDayOfMonth(toDate);
	}
	
	private static Date getStartDate(Date fromDate) {
		return getFirstDayOfMonth(fromDate);
	}
	
	private static java.sql.Date toSQL(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}
	
	private static int compare(Tramo t1, Tramo t2) {
		return fecha2Date(t1.getFechaDesde()).compareTo(fecha2Date(t2.getFechaDesde()));
	}
	
	private static int compare(ContractDataRecord d1, ContractDataRecord d2) {
		return (d1.getStartDate().compareTo(d2.getStartDate()));
	}	

	private static boolean isNumber (String s ) {
		if ( AonStringUtils.isBlank(s)) {
			return false;
		}
		try {
			Double.parseDouble(s);
			return true;			
		} catch ( Exception e) {
			return false;
		}
	}

	private static boolean intersects ( ContractDataRecord d, Period p ) {
		return p.intersects(new Period(d.getStartDate(), d.getEndDate()));
	}
	
	private static void deleteErrors (List<ContractDataRecord> datas) {
		datas
		.stream()
		.filter( d -> Period.compare(d.getStartDate(), d.getEndDate()) > 0 )
		.forEach(d -> {
			d.delete();
			System.out.printf(
			"%1$td-%1$tm %2$td-%2$tm  %3$s = %4$s\r\n", 
			d.getStartDate(), 
			d.getEndDate(), 
			d.getName(),
			d.getExpression()
			);
		});
		;
	}
	
	private static boolean allMatch (ContextVariable var, String str, Period p, List<ContractDataRecord> datas) {
		return allMatch(var.getName(), str, p, datas);
	}
	
	private static boolean allMatch (ContextVariable var, Number number, Period p, List<ContractDataRecord> datas) {
		return allMatch(var.getName(), number, p, datas);
	}	
	
	private static List<ContractDataRecord> filter(String name,Period p, List<ContractDataRecord> datas) {
		return 	
		datas
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.collect(Collectors.toList());
	}
	
	
	private static boolean allMatch (String name, String str, Period p, List<ContractDataRecord> datas) {
		List<ContractDataRecord> list = 
		filter(name, p, datas);
		
		if ( list.isEmpty() )
			return AonStringUtils.isBlank(str);

		return 
		list
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.map(d -> MVEL.evalToString(d.getExpression()))
		.allMatch( s -> AonStringUtils.equals(s, str))
		;
	}
	
	

	private static boolean allMatch (String name, Number number, Period p, List<ContractDataRecord> datas) {
		List<ContractDataRecord> list = 
				filter(name, p, datas);
		
		if ( list.isEmpty() )
			return false;

		return 
		list
		.stream()
		.map(Calculos2Aon::evalNumber)
		.allMatch( n -> n.doubleValue() == number.doubleValue())
		;
	}

	private static Number evalNumber(ContractDataRecord d) {
		try {
			return MVEL.eval(d.getExpression(), Number.class);
		} catch ( Exception e) {
			return Double.NaN;
		}
	}
	
	
	private static Optional<String> findNoMatch (ContextVariable var, String str, Period p, List<ContractDataRecord> datas) {
		return findNoMatch(var.getName(), str, p, datas);
	}

	private static Optional<Number> findNoMatch (ContextVariable var, Number number, Period p, List<ContractDataRecord> datas) {
		return findNoMatch(var.getName(), number, p, datas);
	}
	
	private static Optional<String> findNoMatch (String name, String str, Period p, List<ContractDataRecord> datas) {
		return 
		datas
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.map(d -> MVEL.evalToString(d.getExpression()))
		.filter( s -> !AonStringUtils.equals(s, str))
		.findAny()
		;
	}
	
	private static Optional<Number> findNoMatch (String name, Number number, Period p, List<ContractDataRecord> datas) {
		return 
		datas
		.stream()
		.filter( d -> intersects(d,p) )
		.filter(d -> AonStringUtils.equals(d.getName(), name ))
		.map(Calculos2Aon::evalNumber)
		.filter( n -> n.doubleValue() != number.doubleValue())
		.findAny()
		;
	}	
	
	private static String getString(ContextVariable key, Map<ContextVariable,Optional<ContractDataRecord>> dataMap) {
		return dataMap.getOrDefault(key, Optional.empty()).map(r -> MVEL.evalToString(r.getExpression())).orElse(null);
	}
	
	private static Number getNumber(ContextVariable key, Map<ContextVariable,Optional<ContractDataRecord>> dataMap) {
		return dataMap.getOrDefault(key, Optional.empty()).map(r -> MVEL.eval(r.getExpression(), Number.class)).orElse(0.00);
	}

	private static String lowerCase(final String str) {
		if (str == null) {
			return null;
		}
		return str.toLowerCase();
	}
	
	private static boolean isSituacionActivoNormal(Tramo tramo) {
		
		Set<String> codigos = new HashSet<String>();
		codigos.add("500");  
		codigos.add("501");  
		codigos.add("502");  
		codigos.add("600");  
		codigos.add("611");  
		
		return
		codigos.containsAll(
		tramo.getCalculosTramo()
		.getDatoCalculado()
		.stream()
		.map(d->d.getCodigo())
		.collect(Collectors.toList())
		)
		;
	}
}	
