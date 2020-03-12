package com.esferalia.aon.altai.tgss.creta;

import static com.esferalia.aon.altai.tgss.creta.Utils.fecha2Date;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Calculos;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.DatoCalculado;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Tramo;

public class Calculos2Aon {
	
	Optional<Date> date;
	DSLContext dslContext;
	Connection connection;
	
	public Calculos2Aon(Connection connection) {
		this(connection, Optional.empty());
	}

	public Calculos2Aon(Connection connection, Date date) {
		this(connection, Optional.ofNullable(date));
	}

	public Calculos2Aon(Connection connection, java.util.Date date) {
		this(connection, Optional.ofNullable(date).map( d -> new java.sql.Date(date.getTime())));
	}

	private Calculos2Aon(Connection connection, Optional<Date> date) {
		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		this.date = date;
		this.connection = connection;
		this.dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	}
	
	
	public void parse(File file) throws IOException {
		InputStream is = null; 
		try {
			is = new FileInputStream(file);
			parse(is);
//			System.err.printf("Info: '%s' is not a valid 'SLD-Calculos File'\r\n", file.getPath() );
		} catch ( JAXBException e ) {
			System.err.printf("Warnning: '%s' is not an valid 'SLD-Calculos File' %s \r\n", file.getPath(), e.getMessage() );
		}
		finally {
			if ( is != null )
				is.close();
		}
		
	}
	
	public void parse(InputStream is) throws JAXBException {
		Calculos calculos = Utils.unmarshal(Calculos.class, is);
		
		liquidacion2Aon(calculos.getLiquidacion());
	}
	
	// ------------------------------------------------------------------------
	
	private void liquidacion2Aon(Liquidacion liquidacion) {
		CtaCot ctaCot = liquidacion.getCcc();
		String ccc = String.format("%s%s", ctaCot.getProvincia() ,ctaCot.getNumero());

		liquidacion.getTrabajadores().getTrabajador().stream()
		.forEach(trabajador -> trabajador2Aon(ccc, trabajador) );
		;
	}
	private void trabajador2Aon(String ccc, Trabajador trabajador) {
		String naf = trabajador.getNaf();
		trabajador.getTramos().getTramo().stream()
		.filter(tramo -> tramo.getCalculosTramo() != null )
		.forEach(tramo ->tramo2Aon(ccc, naf, tramo));
	}
	
	private void tramo2Aon ( String ccc, String naf, Tramo tramo) {
		Fecha fechaDesde = tramo.getFechaDesde();
		Fecha fechaHasta = tramo.getFechaHasta();
		
		Date fromDate = fecha2Date(fechaDesde);
		Date toDate = fecha2Date(fechaHasta);
		
//		dslContext
//		.select()
//		.from(SALARY)
//		.where(SALARY.CCC.eq(ccc))
//		.and(SALARY.SOCIAL_SECURITY_NUMBER.eq(naf))
//		.and(SALARY.START_DATE.le(toDate))
//		.and(SALARY.END_DATE.ge(fromDate))
//		.fetchOptionalInto(SALARY)
//		.ifPresent(s -> System.out.printf("Salary: %s \r\n", s.getEmployeeName() ));
//		;

//		String datos = tramo.getCalculosTramo().getDatoCalculado().stream()
//		.map(d -> String.format("%s = %s", d.getCodigo(), d.getValorBase()))
//		.collect(Collectors.joining(","))
//		;
//		
//		Map<String, DatoCalculado> datosMap = 
//				tramo.getCalculosTramo().getDatoCalculado().stream().collect(Collectors.toMap(d -> d.getCodigo() , d -> d ));
//		
//		DatoCalculado dato500 = datosMap.get("500");
//		if ( dato500 == null ) 
//			return;

//		dslContext
//		.select()
//		.from(CONTRACT)
//		.innerJoin(PERSON).onKey()
//		.innerJoin(ENTERPRISE_CCC).onKey()
//		.where(ENTERPRISE_CCC.CCC.eq(ccc))
//		.and(PERSON.SOCIAL_SECURITY_NUM.eq(naf))
//		.and(CONTRACT.START_DATE.le(toDate))
//		.and(CONTRACT.END_DATE.isNull()
//			.or(CONTRACT.END_DATE.ge(fromDate)))
//		.and(CONTRACT.AGREEMENT_LEVEL.isNotNull())
//		.fetchStreamInto(CONTRACT)
//		.forEach(contract -> { 
//			try {
//
//				Date startDate = getStartDate(fromDate); 
//				Date endDate = getEndDate(toDate); 
//				Salary salary = calculate(contract, startDate, endDate);
//
//				double _500 = Double.parseDouble(dato500.getValorBase()) / 100.00;
//				
//				System.out.printf(
//				"%1$td-%1$tm %2$td-%2$tm  %3$s, %4$s %5$f = %6$s \r\n", 
//				salary.getStartDate(), 
//				salary.getEndDate(), 
//				salary.getEnterpriseName(), 
//				salary.getEmployeeName(), 
//				salary.getCommonBase(), _500 );
//				;
//				
//				
//				
//				
//			} catch (ExpressionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SalaryException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		);

		dslContext
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))
		.where(ENTERPRISE_CCC.CCC.eq(ccc))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(naf))
		.and(CONTRACT.START_DATE.le(toDate))
		.and(CONTRACT.END_DATE.isNull()
			.or(CONTRACT.END_DATE.ge(fromDate)))
		.and(DOMAIN.NAME.startsWith("altai-"))
		.fetchStream()
		.forEach(r -> { 
			Map<String,String> data =
			dslContext
			.select()
			.from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
			.groupBy(CONTRACT_DATA.NAME)
			.fetchStreamInto(CONTRACT_DATA)
			.collect(Collectors.toMap(d -> d.getName(), d->d.getExpression()))
			;
			
			String aonQuoteGroup = MVEL.evalToString(data.get(ContextVariable.QUOTE_GROUP.getName()));
			String tgssQuoteGroup = tramo.getInformacionAfiliacion().getGrupoCotizacion();

			String aonTC2 = MVEL.evalToString(data.get(ContextVariable.TC2.getName()));
			String tgssTC2 = tramo.getInformacionAfiliacion().getTipoContrato();
			
			String aonCategory = r.get(CONTRACT.CATEGORY_DESCRIPTION);
			String tgssCategory = tramo.getInformacionAfiliacion().getCatProfesional();
			
			String tgssPartialFactor = tramo.getInformacionAfiliacion().getCoeficienteTiempoParcial();
			
			String tgssCNAE = tramo.getInformacionAfiliacion().getCNAE();

			if ( !AonStringUtils.equals(aonQuoteGroup, tgssQuoteGroup))
				System.err.printf(
				"%s %s: "
				+ "GRUPO_COTIZACION '%s' = '%s'"
				+ " \r\n",
				r.get(PERSON.FIRST_SURNAME),
				r.get(PERSON.NAME),
				aonQuoteGroup,
				tgssQuoteGroup
				);
			else if ( !AonStringUtils.equals(aonTC2, tgssTC2) )
				System.err.printf(
				"%s %s: "
				+ "TC2 '%s' = '%s' \r\n",
				r.get(PERSON.FIRST_SURNAME),
				r.get(PERSON.NAME),
				aonTC2, 
				tgssTC2
				);
			else 
				System.out.printf(
				"%s %s: "
//				+ "CATEGORIA '%s' = '%s'"
				+ ", COEFICIENTE '%s'"
				+ " \r\n",
				r.get(PERSON.FIRST_SURNAME),
				r.get(PERSON.NAME),
//				aonCategory,
//				tgssCategory,
				tgssPartialFactor
				);
			
		}
		);
	}
	
	private Date getEndDate(Date toDate) {
		return getLastDayOfMonth(date.orElse(toDate));
	}
	
	private Date getStartDate(Date fromDate) {
		return getFirstDayOfMonth(date.orElse(fromDate));
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
	
}	
