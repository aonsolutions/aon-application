package com.esferalia.aon.in.payroll.tgss.creta;

import static com.esferalia.aon.in.payroll.tgss.creta.Calculos2Aon.insertSDLAgreement;
import static com.esferalia.aon.in.payroll.tgss.creta.Utils.fecha2Date;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

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
import java.util.Map;
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

import com.code.aon.ql.Criteria;
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
import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Bases;
import net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Dato;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo;

public class Bases2Aon extends Abstract2Aon {
	
	private static interface TramoCallback  {
		void tramo(String ccc, String naf, Tramo tramo);
	}
	
	
	int sdlAgreementLevel ;
	
	public Bases2Aon(Connection connection, Condition where) {
		super(connection, where);
	}
	
	public void check(File file) throws IOException {
		dslContext.transaction((configuration)->{			
		this.sdlAgreementLevel = insertSDLAgreement(dslContext, 0);
		parse(file, this::checkTramo);
		});
	}
	
	public void fix(File file) throws IOException {
		dslContext.transaction((configuration)->{			
		this.sdlAgreementLevel = insertSDLAgreement(dslContext, 0);
		parse(file, this::fixTramo);
		});
	}

	public void parse(File file, TramoCallback cb) throws IOException {
		InputStream is = null; 
		try {
			is = new FileInputStream(file);
			parse(is, cb);
		} catch ( JAXBException e ) {
			System.err.printf("Warnning: '%s' is not an valid 'SLD-Basis File'\r\n", file.getPath() );
		}
		finally {
			if ( is != null )
				is.close();
		}
		
	}
	
	public void parse(InputStream is, TramoCallback cb) throws JAXBException {
		Bases bases = Utils.unmarshal(Bases.class, is);
		bases.getLiquidacion().stream()
		.filter(l -> "L00".equals(l.getTipo()))
		.forEach(l -> liquidacion2Aon(l, cb ) );
	}
	
	// ------------------------------------------------------------------------
	
	private void liquidacion2Aon(Liquidacion liquidacion, TramoCallback cb) {
		CtaCot ctaCot = liquidacion.getCcc();
		String ccc = String.format("%s%s", ctaCot.getProvincia() ,ctaCot.getNumero());
		
		liquidacion.getLiquidacionMes().stream()
		.flatMap(liquidacionMes -> liquidacionMes.getTrabajadores().getTrabajador().stream())
		.forEach(trabajador -> trabajador2Aon(ccc, trabajador, cb) );
		;
	}
	

	private void trabajador2Aon(String ccc, Trabajador trabajador, TramoCallback cb) {
		String naf = trabajador.getNaf();
		trabajador.getTramos().getTramo().stream()
		.filter(tramo -> tramo.getDatosTramo() != null )
		.sorted( Bases2Aon::compare  )
		.forEach(tramo ->cb.tramo(ccc, naf, tramo));	}
	
	private void fixTramo ( String ccc, String naf, Tramo tramo) {
		Fecha fechaDesde = tramo.getFechaDesde();
		Fecha fechaHasta = tramo.getFechaHasta();
		
		Date fromDate = fecha2Date(fechaDesde);
		Date toDate = fecha2Date(fechaHasta);

		findContract(ccc, naf, fromDate, toDate, r -> { 

			System.out.printf(
					"\"%s\",\"%s\",\"%s\"",
					r.get(PERSON.SOCIAL_SECURITY_NUM),
					r.get(PERSON.FIRST_SURNAME),
					r.get(PERSON.NAME)
					);

			fixDatoCalculados(tramo, fromDate, toDate, r);
			fixAgreementLevel(r, sdlAgreementLevel);
			
			System.out.printf("\r\n");
		});
		
		
	}

	private void checkTramo ( String ccc, String naf, Tramo tramo) {

		Fecha fechaDesde = tramo.getFechaDesde();
		Fecha fechaHasta = tramo.getFechaHasta();
		
		Date startDate = fecha2Date(fechaDesde);
		Date endDate = fecha2Date(fechaHasta);
		
		findContract(ccc, naf, startDate, endDate, r ->{
		
			tramo.getDatosTramo().getDato()
			.stream().filter(d -> d.getCodigo().equals("500"))
			.findFirst().ifPresentOrElse(
			d -> {
				try {
					Salary salary = calculate(r, startDate, endDate);
					double commonBase = salary.getCommonBase();
					double valorBase = Double.parseDouble(d.getValor())/ 100.0;
					
					(( Math.round(commonBase) == Math.round(valorBase)) ? System.out : System.err )
					.printf(
							"\"%s\",\"%s\",\"%s\",\"%s\",\"%5$td/%5$tm/%5$tY\",\"%6$td/%6$tm/%6$tY\",\"%7$.2f\",\"%8$.2f\"\r\n",
							salary.getSocialSecurityNumber(),
							salary.getEmployeeName(),
							salary.getCcc(),
							salary.getEnterpriseName(),
							salary.getStartDate(),
							salary.getEndDate(),
							commonBase,
							valorBase);				
					if ( Math.round(commonBase) != Math.round(valorBase)) {
						fixBaseCgcMin(r, valorBase, startDate, endDate);
					} else {
						try {
							calculateAndsave(r, startDate, endDate);
						} catch ( Exception e ) {
							
						}
					}
					
				} catch (NullPointerException e) {
				} catch (ExpressionException | SalaryException | SQLException e) {
				}
			}
			, () -> {});
		
		});
		
		
		
		
	}
		
	private void fixDatoCalculados(Tramo tramo, Date fromDate, Date toDate, Record r) {
		
		List<ContractDataRecord> datas = 
		dslContext
		.select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
		.fetchStreamInto(CONTRACT_DATA)
		.filter(d -> d.getName().matches("^C[0-9]{3}$") )
		.collect(Collectors.toList() )
		;		
		
		Period  period = new Period(fromDate, toDate);
		
		tramo.getDatosTramo().getDato().forEach(d -> {
			switch (d.getTipoDato()) {
			case "C":
				String name = "C" + d.getCodigo();
				double valor = Double.parseDouble(d.getValor())/100.00;
				if ( valor == 0.00 )
					return;
				fix(name, valor, period, r, datas)
				.ifPresent(var -> {});		
				return;

			default:
				return;
			}
		});
		
	}	
	
	private static int compare(Tramo t1, Tramo t2) {
		return fecha2Date(t1.getFechaDesde()).compareTo(fecha2Date(t2.getFechaDesde()));
	}	
}	
