package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_LACK_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_DAYS_FORCE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_DAYS_FORCE_OFF;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR_FORCE_OFF;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.LeaveType.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.LeaveType.OCCUPATIONAL_DISEASE;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;
import static java.util.Calendar.YEAR;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.calculator.CollectSalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.tgss.creta.Bases;
import com.esferalia.aon.payroll.tgss.creta.Bases.EmptyBasesException;
import com.esferalia.aon.payroll.tgss.creta.TrabajadoresTramos;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.mchange.util.AssertException;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Dato;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Error;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Errores;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.FechaHoraRecaudacion;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Periodo;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatosTramo;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.InformacionAfiliacion;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajadores;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.DatoSolicitadoBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.LiquidacionMesBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder.TipoIpf;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadoresTramosBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TramoBuilder;

public class SQLCretaTestCase extends AbstractSQLTestCase {

    private static final double DELTA = 0.006;

    private static class Sucessfull extends RuntimeException {

    }

    // -------------------------------------------------------------------------
    @Test
    public void testCretaStandardActiveFullTime() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
				null);
		//@formatter:on

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(1, datas.size());
	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()));

	    // 501 Base de Horas Extras Fuerza Mayor
	    datas = salary.getContextData().get(STRUCTURAL_OVERTIME_BASE.getName());
	    Assert.assertEquals(1, datas.size());
	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

	    // 502 Base de Horas Extras
	    datas = salary.getContextData().get(NON_STRUCTURAL_OVERTIME_BASE.getName());
	    Assert.assertEquals(1, datas.size());
	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

	    // 601 o 611 Base de Accidentes de Trabajo.
	    datas = salary.getContextData().get(CGP_BASE.getName());
	    Assert.assertEquals(1, datas.size());
	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()));

	});
	;

    }

    @Test
    public void testCretaFormacionEnAlternanciaNormal() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);
	Tramo tramo = trabajador.getTramos().getTramo().get(0);
	assertTramoActivoNormalFormacionEnAlternancia(tramo);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);

	org.junit.Assert.assertEquals(1, bases.size());
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "500");
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "601");
	assertNoDato(bases.get(0).getDatosTramo().getDato(), "I", "51");
    }

    @Test
    public void testCretaFormacionEnAlternanaciaNormalFormacionContinua()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contracts[] = new ContractRecord[10];
	for (int i = 0; i < contracts.length; i++)
	    contracts[i] = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	double c763 = 0.00;
	for (int i = 0; i < contracts.length; i += 3) {
	    c763 += 30.00;
	    addData(aonContext, contracts[i], startDate, endDate, ContextVariable.SLD_C763, 30.00);
	}

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contracts);
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	liquidacionMes.getTrabajadores().getTrabajador().forEach(trabajador -> {
	    Tramo tramo = trabajador.getTramos().getTramo().get(0);
	    assertTramoActivoNormalFormacionEnAlternancia(tramo);
	});

	net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion = getLiquidacion(connection, startDate,
		endDate, ccc, contracts);

	List<Dato> datos = liquidacion.getDatosLiquidacion().getDato();
	org.junit.Assert.assertEquals(1, datos.size());
	org.junit.Assert.assertEquals(c763 * 100.00, Double.parseDouble(datos.get(0).getValor()), DELTA);

    }

    @Test
    public void testCretaActivoNormalFormacionContinua() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_C763, 333.00);

	net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion = getLiquidacion(connection, startDate,
		endDate, ccc, contract);

	List<Dato> datos = liquidacion.getDatosLiquidacion().getDato();
	org.junit.Assert.assertEquals(1, datos.size());
	org.junit.Assert.assertEquals(33300.00, Double.parseDouble(datos.get(0).getValor()), DELTA);

	datos = liquidacion.getLiquidacionMes().get(0).getTrabajadores().getTrabajador().get(0).getTramos().getTramo()
		.get(0).getDatosTramo().getDato();

    }

    @Test
    public void testCretaBecariosNormal() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C990, "10", CCCType.FELLOWS);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	try {
	    List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate,
		    endDate, ccc, contract);
	    org.junit.Assert.fail("Bases must be empty");
	} catch (EmptyBasesException e) {
	    return;
	}

    }

    @Test
    public void testCretaFormacionEnAlternanciaNormalTutoria()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_C737, "737.66");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H06, "6");

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);
	Tramo tramo = trabajador.getTramos().getTramo().get(0);
	assertTramoActivoNormalFormacionEnAlternancia(tramo);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	org.junit.Assert.assertEquals(1, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(4, datos.size());
	// 500 , 601 , 737 , 06

	double c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000, c500, DELTA);

	double c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000, c601, DELTA);

	double c737 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(73766, c737, DELTA);

	double h6 = datos.stream().filter(d -> d.getCodigo().equals("06")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(6, h6, DELTA);

    }

    @Test
    public void testCretaFormacionEnAlternanciaNormalTutoriaTramosI()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);

	Date endDate = getLastDayOfMonth(startDate);

	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	int monthDays = get(endDate, Calendar.DAY_OF_MONTH);

	// Only for second contract
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_C737,
		Integer.toString(monthDays) + " * 10.00");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H06, Integer.toString(monthDays));

	addPayment(aonContext, contract, String.format("TRAMO(FECHA(%d,%d,15));0.00", get(endDate, Calendar.YEAR),
		get(endDate, Calendar.MONTH) + 1));

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);
	trabajador.getTramos().getTramo().forEach(SQLCretaTestCase::assertTramoActivoNormalFormacionEnAlternancia);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);

	org.junit.Assert.assertEquals(2, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(4, datos.size());

	double c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000 * 15 / monthDays, c500, DELTA);

	double c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000 * 15 / monthDays, c601, DELTA);

	double c737 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(15.00 * 1000.00, c737, DELTA);

	double h6 = datos.stream().filter(d -> d.getCodigo().equals("06")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(15.00, h6, DELTA);

	datos = bases.get(1).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(4, datos.size());

	c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000 * (monthDays - 15) / monthDays, c500, 1);

	c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000 * (monthDays - 15) / monthDays, c601, 1);

	c737 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals((monthDays - 15) * 1000.00, c737, DELTA);

	h6 = datos.stream().filter(d -> d.getCodigo().equals("06")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals((monthDays - 15), h6, DELTA);

    }

    @Test
    public void testCretaFormacionEnAlternanciaNormalTutoriaTramosII()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	Date startDateFirst = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDateFirst = add(startDateFirst, DAY_OF_MONTH, 14);

	Date endDateSecond = getLastDayOfMonth(startDateFirst);
	Date startDateSecond = endDateSecond; // (endDateSecond, DAY_OF_MONTH, -0);

	String dni = Long.toString(Math.abs(new Random().nextLong()), 10).substring(0, 10);
	String nss = Long.toString(Math.abs(new Random().nextLong()), 10).substring(0, 12);
	ContractRecord firstContract = newContract(aonContext, ccc, ContractCode.C421, "10", CCCType.PRINCIPAL,
		startDateFirst, endDateFirst, null, dni, nss);
	ContractRecord secondContract = newContract(aonContext, ccc, ContractCode.C421, "10", CCCType.PRINCIPAL,
		startDateSecond, endDateSecond, null, dni, nss);

	// Only for second contract
	addData(aonContext, secondContract, startDateSecond, endDateSecond, ContextVariable.SLD_C737, "10.00");
	addData(aonContext, secondContract, startDateSecond, endDateSecond, ContextVariable.SLD_H06, "1");

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, startDateFirst, endDateSecond, ccc, firstContract, secondContract);
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);
	trabajador.getTramos().getTramo().forEach(SQLCretaTestCase::assertTramoActivoNormalFormacionEnAlternancia);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);

	org.junit.Assert.assertEquals(2, bases.size());

	List<Dato> datos = bases.get(1).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(4, datos.size());

	double c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000 * 1 / 30, c500, DELTA);

	double c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000 * 1 / 30, c601, DELTA);

	double c737 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(1.00 * 1000.00, c737, DELTA);

	double h6 = datos.stream().filter(d -> d.getCodigo().equals("06")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(1.00, h6, DELTA);

    }

    @Test
    public void testCretaFormacionEnAlternanciaNormalFormacion()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H04, "44");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H03, "33");

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);
	trabajador.getTramos().getTramo().forEach(SQLCretaTestCase::assertTramoActivoNormalFormacionEnAlternancia);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);

	org.junit.Assert.assertEquals(1, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(4, datos.size());

	double c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000, c500, DELTA);

	double c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(175000, c601, DELTA);

	double h4 = datos.stream().filter(d -> d.getCodigo().equals("04")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(44, h4, DELTA);

	double h3 = datos.stream().filter(d -> d.getCodigo().equals("03")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(33, h3, DELTA);

    }

    @Test
    public void testCretaFormacionEnAlternanciaNormalFormacionNoSalary()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);
	trabajador.getTramos().getTramo().forEach(SQLCretaTestCase::assertTramoActivoNormalFormacionEnAlternancia);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H04, "44");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H03, "33");
	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);

	org.junit.Assert.assertEquals(1, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(4, datos.size());

	double h4 = datos.stream().filter(d -> d.getCodigo().equals("04")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(44, h4, DELTA);

	double h3 = datos.stream().filter(d -> d.getCodigo().equals("03")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(33, h3, DELTA);

    }

    @Test
    public void testCretaFormacionEnAlternanciaERETotalI() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H04, "44");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H03, "33");
	addData(aonContext, contract, startDate, endDate, ContextVariable.ERE_FACTOR, "1.0");

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);

	trabajador.getTramos().getTramo().forEach(SQLCretaTestCase::assertTramoExpedienteRegulacionEmpleoTotal);
	trabajador.getTramos().getTramo()
		.forEach(tramo -> assertNoDatoSolicitado(tramo.getDatosTramo().getDatoSolicitado(), "I", "51"));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "509",
		Integer.toString((int) Math.round(1750.00 * 100)));
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "603",
		Integer.toString((int) Math.round(1750.00 * 100)));

    }

    @Test
    public void testCretaFormacionEnAlternanciaEREParcialITI()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H04, "44");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H03, "33");
	addData(aonContext, contract, startDate, endDate, ContextVariable.ERE_FACTOR, "0.60");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	org.junit.Assert.assertEquals(1, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(7, datos.size());

	double c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(1750.00 * 0.40 * 100.00, c500, DELTA);

	double c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(1750.00 * 0.40 * 100.00, c601, DELTA);

	double c536 = datos.stream().filter(d -> d.getCodigo().equals("536")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(1750.00 * 0.60 * 100.00, c536, DELTA);

	double c636 = datos.stream().filter(d -> d.getCodigo().equals("636")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(1750.00 * 0.60 * 100.00, c636, DELTA);

	double h4 = datos.stream().filter(d -> d.getCodigo().equals("04")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(44, h4, DELTA);

	double h3 = datos.stream().filter(d -> d.getCodigo().equals("03")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(33, h3, DELTA);

	double h5 = datos.stream().filter(d -> d.getCodigo().equals("05")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(400, h5, DELTA);

    }

    @Test
    public void testCretaBecariosEREParcialI() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C990, "10", CCCType.FELLOWS);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H04, "44");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H03, "33");
	addData(aonContext, contract, startDate, endDate, ContextVariable.ERE_FACTOR, "0.60");

	try {
	    List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate,
		    endDate, ccc, contract);
	    org.junit.Assert.fail("Bases must be empty");
	} catch (EmptyBasesException e) {
	    // Nothing to comunicate .
	    return;
	}

    }

    @Test
    public void testCretaFormacionEnAlternanciaMaternidadTotalI()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H04, "44");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H03, "33");

	addIT(aonContext, contract, LeaveType.MATERNITY, startDate, endDate, null);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);

	trabajador.getTramos().getTramo().forEach(SQLCretaTestCase::assertTramoMaternidadTiempoCompleto);
	trabajador.getTramos().getTramo()
		.forEach(tramo -> assertNoDatoSolicitado(tramo.getDatosTramo().getDatoSolicitado(), "I", "51"));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "509",
		Integer.toString((int) Math.round(1750.00 * 100)));
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "603",
		Integer.toString((int) Math.round(1750.00 * 100)));

    }

    @Test
    public void testCretaFormacionEnAlternanciaITI() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startItDate = add(startDate, Calendar.DAY_OF_MONTH, 4);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startItDate, null, 100.00);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");

	Trabajador trabajador = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores()
		.getTrabajador().get(0);

	// 01-04
	assertTramoActivoNormalFormacionEnAlternancia(trabajador.getTramos().getTramo().get(0));
	// 05-19
	assertTramoIT15PrimerosDias(trabajador.getTramos().getTramo().get(1));
	// 20-24
	assertTramoITPagoDelegado(trabajador.getTramos().getTramo().get(2));
	// 25...
	assertTramoITPagoDelegado(trabajador.getTramos().getTramo().get(3));

	for (int i = 0; i < 4; i++)
	    assertNoDatoSolicitado(trabajador.getTramos().getTramo().get(i).getDatosTramo().getDatoSolicitado(), "I",
		    "51");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);

    }

    @Test
    public void testCretaBecariosITI() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C990, "10", CCCType.FELLOWS);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate, endDate, 100.00);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	org.junit.Assert.assertEquals(2, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(1, datos.size());

	double c563 = datos.stream().filter(d -> d.getCodigo().equals("563")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(Math.ceil(100.00 * 0.75 * 5 * 100), c563, DELTA);

	datos = bases.get(1).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(1, datos.size());

	c563 = datos.stream().filter(d -> d.getCodigo().equals("563")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	int days = AonDateUtils.getMax(endDate, DAY_OF_MONTH) - 21 + 1;
	org.junit.Assert.assertEquals(Math.ceil(100.00 * 0.75 * (days) * 100), c563, DELTA);

    }

    @Test
    public void testCretaFormacionEnAlternanciaERETotalII() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startEre = add(startDate, Calendar.DAY_OF_MONTH, 9);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H04, "44");
	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_H03, "33");
	addData(aonContext, contract, startEre, endDate, ContextVariable.ERE_FACTOR, "1.0");

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresYTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");
	LiquidacionMes liquidacionMes = trabajadoresYTramos.getLiquidacion().getLiquidacionMes().get(0);
	Trabajador trabajador = liquidacionMes.getTrabajadores().getTrabajador().get(0);

	assertTramoActivoNormalFormacionEnAlternancia(trabajador.getTramos().getTramo().get(0));
	assertTramoExpedienteRegulacionEmpleoTotal(trabajador.getTramos().getTramo().get(1));

	for (int i = 0; i < 2; i++)
	    assertNoDatoSolicitado(trabajador.getTramos().getTramo().get(i).getDatosTramo().getDatoSolicitado(), "I",
		    "51");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresYTramos);

	org.junit.Assert.assertEquals(2, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(4, datos.size());

	double h4 = datos.stream().filter(d -> d.getCodigo().equals("04")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(44, h4, DELTA);

	double h3 = datos.stream().filter(d -> d.getCodigo().equals("03")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(33, h3, DELTA);

	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 9 / 30 * 100)));
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 9 / 30 * 100)));
	assertDato(bases.get(1).getDatosTramo().getDato(), "C", "509",
		Integer.toString((int) Math.round(1750.00 * 21 / 30 * 100)));
	assertDato(bases.get(1).getDatosTramo().getDato(), "C", "603",
		Integer.toString((int) Math.round(1750.00 * 21 / 30 * 100)));

    }

    @Test
    public void testCretaFormacionEnAlternanciaERETotalIII() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startEre = add(startDate, Calendar.DAY_OF_MONTH, 10);
	Date endEre = add(startEre, Calendar.DAY_OF_MONTH, 9);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_C737, "100.00");
	addData(aonContext, contract, startEre, endEre, ContextVariable.ERE_FACTOR, "1.0");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	org.junit.Assert.assertEquals(3, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(3, datos.size());

	double c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * 10 / 30 * 100.00), c500, DELTA);

	double c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * 10 / 30 * 100.00), c601, DELTA);

	double h4 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	int workedDays = AonDateUtils.getMax(endDate, DAY_OF_MONTH) - 10;
	org.junit.Assert.assertEquals(Math.round(100.00 / workedDays * 10.00 * 100.00), h4, DELTA);

	datos = bases.get(1).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(2, datos.size());

	double c509 = datos.stream().filter(d -> d.getCodigo().equals("509")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * (30 - workedDays) / 30 * 100.00), c509, DELTA);

	double c603 = datos.stream().filter(d -> d.getCodigo().equals("603")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * (30 - workedDays) / 30 * 100.00), c603, DELTA);

	datos = bases.get(2).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(3, datos.size());

	c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * (workedDays - 10) / 30 * 100.00), c500, DELTA);

	c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * (workedDays - 10) / 30 * 100.00), c601, DELTA);

	h4 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(Math.round(100.00 / workedDays * (workedDays - 10) * 100.00), h4, DELTA);

    }

    @Test
    public void testCretaFormacionEnAlternanciaERETotalIV() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	DomainRecord domain = newDomain(aonContext);

	ScopeRecord scope = newScope(aonContext, domain.getId());

	EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(aonContext, domain.getId(), scope.getId(),
		SSRegimeType.GENERAL);

	EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, domain.getId(), scope.getId(),
		enterpriseActivity.getId(), CCCType.TRAINING, ccc);

	WorkplaceRecord workplace = newWorkplace(aonContext, domain.getId(), scope.getId(),
		enterpriseActivity.getEnterprise());

	RegistryRecord person = newPerson(aonContext, domain.getId(), "00000000A");

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL, CCCType.TRAINING,
		getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
		    {
			put(QUOTE_GROUP.getName(), String.format("'%s'", "10"));
			put(TC2.getName(), String.format("\"%s\"", "421"));
			put(MONTH_DAYS.getName(), String.format("{'%s':30}[%s]", "10", QUOTE_GROUP.getName()));

		    }
		}, new String[] { "250.00", "1500.00", },
		new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05", "BASE_IRPF * PORCENTAJE_IRPF/100", }, null,
		domain.getId(), // domainId,
		person.getId(), // personId,
		workplace.getId(), // workplaceId,
		enterpriseCcc.getId(), // enterpriseCccId,
		enterpriseActivity.getId() // enterpriseActivityId
	);

	PaymentConceptRecord ere = addConcept(aonContext, "ERE");

	addPayment(aonContext, contract, ere, String.format("/*read-only*/%s * 0.00/**/", ERE_DAYS),
		String.format("%s * BASE_REGULADORA", ERE_DAYS));

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startEre = add(startDate, Calendar.DAY_OF_MONTH, 10);
	Date endEre = add(startEre, Calendar.DAY_OF_MONTH, 9);

	addData(aonContext, contract, startDate, endDate, ContextVariable.SLD_C737, "100.00");
	addData(aonContext, contract, startEre, endEre, ContextVariable.ERE_FACTOR, "1.0");

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);
	Trabajador trabajador = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores()
		.getTrabajador().get(0);
	assertTramoActivoNormalFormacionEnAlternancia(trabajador.getTramos().getTramo().get(0));
	assertTramoExpedienteRegulacionEmpleoTotal(trabajador.getTramos().getTramo().get(1));
	assertTramoActivoNormalFormacionEnAlternancia(trabajador.getTramos().getTramo().get(2));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);

	org.junit.Assert.assertEquals(3, bases.size());

	List<Dato> datos = bases.get(0).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(3, datos.size());

	double c500 = datos.stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * 10 / 30 * 100.00), c500, DELTA);

	double c601 = datos.stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * 10 / 30 * 100.00), c601, DELTA);

	double h4 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

	int workedDays = AonDateUtils.getMax(endDate, DAY_OF_MONTH) - 10;
	org.junit.Assert.assertEquals(Math.round(100.00 / workedDays * 10.00 * 100.00), h4, DELTA);

	datos = bases.get(1).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(2, datos.size());

	double c509 = datos.stream().filter(d -> d.getCodigo().equals("509")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * (30 - workedDays) / 30 * 100.00), c509, DELTA);

	double c603 = datos.stream().filter(d -> d.getCodigo().equals("603")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(Math.round(1750.00 * (30 - workedDays) / 30 * 100.00), c603, DELTA);

	datos = bases.get(2).getDatosTramo().getDato();

	org.junit.Assert.assertEquals(3, datos.size());

	h4 = datos.stream().filter(d -> d.getCodigo().equals("737")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));

    }

    // -------------------------------------------------------------------------
    @Test
    public void testCretaITDailyI() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "10");
	addData(aonContext, contract, getFirstDayOfYear(getToday()), null, MONTH_DAYS.getName(),
		NATURAL_MONTH_DAYS.getName());

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				getFirstDayOfYear(getToday()), 
				null, 
				null);
		//@formatter:on

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	org.junit.Assert.assertEquals(1, tramosBases.size());

	tramosBases.get(0).getDatosTramo().getDato().forEach(d -> {
	    org.junit.Assert.assertNotEquals("51", d.getCodigo());

	});
	double _500 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));

	double _563 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("563"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(_500 * 0.75, _563, DELTA);

    }

    // -------------------------------------------------------------------------
    @Test
    public void testCretaITMaternityFullTimeI() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);
	addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), PaymentType.CRA_0004,
		"TRACE('DIAS_COTIZADOS=%f\r\n',DIAS_COTIZADOS);0.00", "DIAS_COTIZADOS * BASE_REGULADORA", "0.00");
	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), String.format("%f", 30.00));
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
				},
				null);
		//@formatter:on

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				getFirstDayOfYear(startDate), 
				null, 
				null/*1750.00/30*/);
		//@formatter:on

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
	new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
	int salaries = jooqSalaryBuilder.execute();
	// int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(1, datas.size());

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	});
	;

    }

    // -------------------------------------------------------------------------
    @Test
    public void testCretaITMaternityFullTimeII() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);
	addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), PaymentType.CRA_0004,
		"TRACE('DIAS_MATERNIDAD=%f\r\n',DIAS_MATERNIDAD);0.00", "DIAS_MATERNIDAD * BASE_REGULADORA",
		"TRACE('BASE_REGULADORA=%f\r\n',BASE_REGULADORA);0.00");
	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
				},
				null);
		//@formatter:on

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

	double br = Math.round((1755.00 / monthDays * 1000.00)) / 1000.00; // DB only has three decimals

	//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				getFirstDayOfYear(startDate), 
				null, 
				br);
		//@formatter:on

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
	new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
	int salaries = jooqSalaryBuilder.execute();
//		int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(1, datas.size());

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(br * monthDays, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	});
	;

    }

    // -------------------------------------------------------------------------
    @Test
    public void testCretaITMaternityFullTimeIII() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);
	addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), PaymentType.CRA_0004,
		"TRACE('DIAS_MATERNIDAD=%f\r\n',DIAS_MATERNIDAD);0.00", "DIAS_MATERNIDAD * BASE_REGULADORA", "0.00");
	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				"TRACE('BASE_CGC=%f\r\n', BASE_CGC); 0.00;",
				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
				},
				null);
		//@formatter:on

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);
	Date startIt = add(startDate, DAY_OF_MONTH, 13);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				startIt, 
				endDate, 
				null/*1750.00/30*/);
		//@formatter:on

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
	new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
	int salaries = jooqSalaryBuilder.execute();
//		int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    Date endActive = add(startIt, DAY_OF_MONTH, -1);
	    int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(2, datas.size());
	    Collections.sort(datas, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endActive, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00 * 13 / monthDays, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	    Assert.assertEquals(startIt, datas.get(1).getStartDate());
	    Assert.assertEquals(endDate, datas.get(1).getEndDate());
	    Assert.assertEquals(1750.00 * (monthDays - 13) / monthDays,
		    Double.parseDouble(datas.get(1).getExpression()), DELTA);

	    // 601 o 611 Base de Accidentes de Trabajo.
	    datas = salary.getContextData().get(CGP_BASE.getName());
	    Assert.assertEquals(2, datas.size());
	    Collections.sort(datas, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endActive, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00 * 13 / monthDays, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	    Assert.assertEquals(startIt, datas.get(1).getStartDate());
	    Assert.assertEquals(endDate, datas.get(1).getEndDate());
	    Assert.assertEquals(1750.00 * (monthDays - 13) / monthDays,
		    Double.parseDouble(datas.get(1).getExpression()), DELTA);

	});
	;

    }

    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    @Test
    public void testCustomPeriod() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemData(aonContext);
	cleanSystemCosts(aonContext);

	//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getToday(), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

	addSSRegimeData(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), null,
		new HashMap<String, String>() {
		    {
			put("PORCENTAJE_CGC_E", "23.60");
		    }
		});

	addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), "CGC_E",
		DeductionType.COMMON_CONTINGENCY, "( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

	//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"FIN_BONIF=AÑO(INICIO_CONTRATO,2);"+
				"TRAMO(FIN_BONIF);"+
				"0.00"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

	// After two years.
	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection,
		getFirstDayOfMonth(add(getToday(), YEAR, 2)), getLastDayOfMonth(add(getToday(), YEAR, 2)),
		getLastDayOfMonth(add(getToday(), YEAR, 2)), contract);

	int salaries = calculateAndSave(connection, ctx);
	Assert.assertEquals(1, salaries);

	// Only one salary saved to DB.
	Map<String, List<ContextData>> datas = AON
		.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).findFirst()
		.map(salary -> salary.getContextData()).get();

	Date finBonif = add(contract.getStartDate(), YEAR, 2);
	Date endDate = getLastDayOfMonth(add(getToday(), YEAR, 2));

	List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());
	Assert.assertEquals(finBonif.before(endDate) ? 2 : 1, cgcBases.size());

	Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgcBases.get(0).getStartDate(), getFirstDayOfMonth(add(getToday(), YEAR, 2)));
	Assert.assertEquals(cgcBases.get(0).getEndDate(), add(getToday(), YEAR, 2));
	if (cgcBases.size() > 1) {
	    Assert.assertEquals(cgcBases.get(1).getStartDate(), add(add(getToday(), YEAR, 2), DAY_OF_MONTH, 1));
	    Assert.assertEquals(cgcBases.get(1).getEndDate(), getLastDayOfMonth(add(getToday(), YEAR, 2)));
	}

	List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());
	Assert.assertEquals(finBonif.before(endDate) ? 2 : 1, cgpBases.size());
	Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgpBases.get(0).getStartDate(), getFirstDayOfMonth(add(getToday(), YEAR, 2)));
	Assert.assertEquals(cgpBases.get(0).getEndDate(), add(getToday(), YEAR, 2));
	if (cgcBases.size() > 1) {
	    Assert.assertEquals(cgpBases.get(1).getStartDate(), add(add(getToday(), YEAR, 2), DAY_OF_MONTH, 1));
	    Assert.assertEquals(cgpBases.get(1).getEndDate(), getLastDayOfMonth(add(getToday(), YEAR, 2)));
	}

//		List<ContextData> structuralBases = datas.get(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, structuralBases.size());
//
//		List<ContextData> nonStructuralBases = datas.get(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, nonStructuralBases.size());

    }

    @Test
    public void testCustomPeriodWithIT() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemData(aonContext);
	cleanSystemCosts(aonContext);

	//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00 * DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

	addSSRegimeData(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), null,
		new HashMap<String, String>() {
		    {
			put("PORCENTAJE_CGC_E", "23.60");
		    }
		});

	addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), "CGC_E",
		DeductionType.COMMON_CONTINGENCY, "( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

	//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"FIN_BONIF=DIA(AÑO(INICIO_CONTRATO,2),9);"+
				"TRAMO(FIN_BONIF);"+
				"0.00"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, getFirstDayOfMonth(add(getToday(), YEAR, 2)),
		add(getFirstDayOfMonth(add(getToday(), YEAR, 2)), DAY_OF_MONTH, 14), 100.00);

	//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

	// After two years.
	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection,
		getFirstDayOfMonth(add(getToday(), YEAR, 2)), getLastDayOfMonth(add(getToday(), YEAR, 2)),
		getLastDayOfMonth(add(getToday(), YEAR, 2)), contract);

	int salaries = calculateAndSave(connection, ctx);
	Assert.assertEquals(1, salaries);

	// Only one salary saved to DB.
	Map<String, List<ContextData>> datas = AON
		.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).findFirst()
		.map(salary -> salary.getContextData()).get();

	Date startDate = getFirstDayOfMonth(add(getToday(), YEAR, 2));
	Date sectionDate = add(startDate, DAY_OF_MONTH, 9);
	Date next2SectionDate = add(sectionDate, DAY_OF_MONTH, 1);
	Date itEndDate = add(startDate, DAY_OF_MONTH, 14);
	Date workStartDate = add(itEndDate, DAY_OF_MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);

	List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());
	Assert.assertEquals(4, cgcBases.size());
	Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgcBases.get(0).getStartDate(), startDate);
	Assert.assertEquals(cgcBases.get(1).getEndDate(), sectionDate);
	Assert.assertEquals(cgcBases.get(2).getStartDate(), next2SectionDate);
	Assert.assertEquals(cgcBases.get(2).getEndDate(), itEndDate);
	Assert.assertEquals(cgcBases.get(3).getStartDate(), workStartDate);
	Assert.assertEquals(cgcBases.get(3).getEndDate(), endDate);

	Assert.assertEquals(100.00 * 3, Double.parseDouble(cgcBases.get(0).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 7, Double.parseDouble(cgcBases.get(1).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 5, Double.parseDouble(cgcBases.get(2).getExpression()), DELTA);
	Assert.assertEquals(1500.00 * (monthDays - 15) / monthDays, Double.parseDouble(cgcBases.get(3).getExpression()),
		DELTA);

	List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());
	Assert.assertEquals(4, cgpBases.size());
	Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgpBases.get(0).getStartDate(), startDate);
	Assert.assertEquals(cgpBases.get(1).getEndDate(), sectionDate);
	Assert.assertEquals(cgpBases.get(2).getStartDate(), next2SectionDate);
	Assert.assertEquals(cgpBases.get(2).getEndDate(), itEndDate);
	Assert.assertEquals(cgpBases.get(3).getStartDate(), workStartDate);
	Assert.assertEquals(cgpBases.get(3).getEndDate(), endDate);

	Assert.assertEquals(100.00 * 3, Double.parseDouble(cgpBases.get(0).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 7, Double.parseDouble(cgpBases.get(1).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 5, Double.parseDouble(cgpBases.get(2).getExpression()), DELTA);
	Assert.assertEquals(1500.00 * (monthDays - 15) / monthDays, Double.parseDouble(cgpBases.get(3).getExpression()),
		DELTA);

	List<ContextData> prestIts = datas.get(ContextVariable.PREST_IT);
	Assert.assertEquals(4, prestIts.size());
	Collections.sort(prestIts, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(prestIts.get(0).getStartDate(), startDate);
	Assert.assertEquals(prestIts.get(1).getEndDate(), sectionDate);
	Assert.assertEquals(prestIts.get(2).getStartDate(), next2SectionDate);
	Assert.assertEquals(prestIts.get(2).getEndDate(), itEndDate);
	Assert.assertEquals(prestIts.get(3).getStartDate(), workStartDate);
	Assert.assertEquals(prestIts.get(3).getEndDate(), endDate);

	Assert.assertEquals(0.00, Double.parseDouble(prestIts.get(0).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 7 * 0.60, Double.parseDouble(prestIts.get(1).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 5 * 0.60, Double.parseDouble(prestIts.get(2).getExpression()), DELTA);
	Assert.assertEquals(0.00, Double.parseDouble(prestIts.get(3).getExpression()), DELTA);

//		List<ContextData> structuralBases = datas.get(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, structuralBases.size());
//		Assert.assertEquals(startDate, structuralBases.get(0).getStartDate());
//		Assert.assertEquals(endDate, structuralBases.get(0).getEndDate());
//
//		List<ContextData> nonStructuralBases = datas.get(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, nonStructuralBases.size());
//		Assert.assertEquals(startDate, nonStructuralBases.get(0).getStartDate());
//		Assert.assertEquals(endDate, nonStructuralBases.get(0).getEndDate());

    }

    @Test
    public void testCustomPeriodHextraBaseIT() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemData(aonContext);
	cleanSystemCosts(aonContext);

	//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00 * DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, getFirstDayOfMonth(getToday()),
		add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 14), 100.00);

	//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

	//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"FIN_BONIF=DIA(INICIO_NOMINA,9);"+
				"TRAMO(FIN_BONIF);"+
				"0.00"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

	addPayment(aonContext, contract, "", "100.00", "_P", "_P", PaymentType.CRA_0002);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection,
		getFirstDayOfMonth(getToday()), getLastDayOfMonth(getToday()), getLastDayOfMonth(getToday()), contract);

	int salaries = calculateAndSave(connection, ctx);
	Assert.assertEquals(1, salaries);

	// Only one salary saved to DB.
	Map<String, List<ContextData>> datas = AON
		.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).findFirst()
		.map(salary -> salary.getContextData()).get();
	Date startDate = getFirstDayOfMonth(getToday());
	Date sectionDate = add(startDate, DAY_OF_MONTH, 9);
	Date next2SectionDate = add(sectionDate, DAY_OF_MONTH, 1);
	Date itEndDate = add(startDate, DAY_OF_MONTH, 14);
	Date workStartDate = add(itEndDate, DAY_OF_MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);

	List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());
	Assert.assertEquals(4, cgcBases.size());
	Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgcBases.get(0).getStartDate(), startDate);
	Assert.assertEquals(cgcBases.get(1).getEndDate(), sectionDate);
	Assert.assertEquals(cgcBases.get(2).getStartDate(), next2SectionDate);
	Assert.assertEquals(cgcBases.get(2).getEndDate(), itEndDate);
	Assert.assertEquals(cgcBases.get(3).getStartDate(), workStartDate);
	Assert.assertEquals(cgcBases.get(3).getEndDate(), endDate);

	Assert.assertEquals(100.00 * 3, Double.parseDouble(cgcBases.get(0).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 7, Double.parseDouble(cgcBases.get(1).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 5, Double.parseDouble(cgcBases.get(2).getExpression()), DELTA);

	Assert.assertEquals(1500.00 * (monthDays - 15) / monthDays, Double.parseDouble(cgcBases.get(3).getExpression()),
		DELTA);

	List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());
	Assert.assertEquals(4, cgpBases.size());
	Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgpBases.get(0).getStartDate(), startDate);
	Assert.assertEquals(cgpBases.get(1).getEndDate(), sectionDate);
	Assert.assertEquals(cgpBases.get(2).getStartDate(), next2SectionDate);
	Assert.assertEquals(cgpBases.get(2).getEndDate(), itEndDate);
	Assert.assertEquals(cgpBases.get(3).getStartDate(), workStartDate);
	Assert.assertEquals(cgpBases.get(3).getEndDate(), endDate);

	Assert.assertEquals(100.00 * 3, Double.parseDouble(cgpBases.get(0).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 7, Double.parseDouble(cgpBases.get(1).getExpression()), DELTA);
	Assert.assertEquals(100.00 * 5, Double.parseDouble(cgpBases.get(2).getExpression()), DELTA);

	Assert.assertEquals(100.00 + (1500.00 * (monthDays - 15) / monthDays),
		Double.parseDouble(cgpBases.get(3).getExpression()), DELTA);

	List<ContextData> prestIts = datas.get(ContextVariable.PREST_IT);
	Assert.assertEquals(4, prestIts.size());
	Collections.sort(prestIts, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(prestIts.get(0).getStartDate(), startDate);
	Assert.assertEquals(prestIts.get(1).getEndDate(), sectionDate);
	Assert.assertEquals(prestIts.get(2).getStartDate(), next2SectionDate);
	Assert.assertEquals(prestIts.get(2).getEndDate(), itEndDate);
	Assert.assertEquals(prestIts.get(3).getStartDate(), workStartDate);
	Assert.assertEquals(prestIts.get(3).getEndDate(), endDate);

	List<ContextData> structuralBases = datas.get(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName());
	Assert.assertEquals(1, structuralBases.size());
	Assert.assertEquals(structuralBases.get(0).getStartDate(), startDate);
	Assert.assertEquals(structuralBases.get(0).getEndDate(), endDate);
	Assert.assertEquals(0.00, Double.parseDouble(structuralBases.get(0).getExpression()), DELTA);

	List<ContextData> nonStructuralBases = datas.get(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName());
	Assert.assertEquals(1, nonStructuralBases.size());
	Assert.assertEquals(nonStructuralBases.get(0).getStartDate(), workStartDate);
	Assert.assertEquals(nonStructuralBases.get(0).getEndDate(), endDate);
	Assert.assertEquals(100.00, Double.parseDouble(nonStructuralBases.get(0).getExpression()), DELTA);
    }

    @Test
    public void test31QuoteIT() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemData(aonContext);
	cleanSystemCosts(aonContext);

	Date startDate = getFirstDayOfYear(getToday());

	//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
				put(ContextVariable.MONTH_DAYS.getName(), "30");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00 * DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

	Date itDate = startDate;

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, itDate, itDate, 100.00);

	//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

	// After two years.
	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate,
		getLastDayOfMonth(startDate), getLastDayOfMonth(startDate), contract);

	int salaries = calculateAndSave(connection, ctx);
	Assert.assertEquals(1, salaries);

	// Only one salary saved to DB.
	Map<String, List<ContextData>> datas = AON
		.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).findFirst()
		.map(salary -> salary.getContextData()).get();

	Date workStartDate = add(itDate, DAY_OF_MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());

	Assert.assertEquals(2, cgcBases.size());
	Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgcBases.get(0).getStartDate(), startDate);
	Assert.assertEquals(cgcBases.get(0).getEndDate(), itDate);
	Assert.assertEquals(cgcBases.get(1).getStartDate(), workStartDate);
	Assert.assertEquals(cgcBases.get(1).getEndDate(), endDate);

	Assert.assertEquals(0.00, Double.parseDouble(cgcBases.get(0).getExpression()));
	Assert.assertEquals(1500.00, Double.parseDouble(cgcBases.get(1).getExpression()), DELTA);

	List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());

	Assert.assertEquals(2, cgpBases.size());
	Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	Assert.assertEquals(cgpBases.get(0).getStartDate(), startDate);
	Assert.assertEquals(cgpBases.get(0).getEndDate(), itDate);
	Assert.assertEquals(cgpBases.get(1).getStartDate(), workStartDate);
	Assert.assertEquals(cgpBases.get(1).getEndDate(), endDate);

	Assert.assertEquals(0.00, Double.parseDouble(cgpBases.get(0).getExpression()));
	Assert.assertEquals(1500.00, Double.parseDouble(cgpBases.get(1).getExpression()), DELTA);
    }

    // -------------------------------------------------------------------------
    @Test
    public void testCretaChangeTime() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
				null);
		//@formatter:on

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, add(startDate, DAY_OF_MONTH, 10), null, new HashMap<String, String>() {
	    {
		put(ContextVariable.OCCUPATION.getName(), "\"g\"");
	    }
	});

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {
	    Date noOcupationEnd = add(startDate, DAY_OF_MONTH, 9);
	    Date ocupationStart = add(startDate, DAY_OF_MONTH, 10);

	    int monthDays = AonDateUtils.get(endDate, DAY_OF_MONTH);

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(2, datas.size());
	    Collections.sort(datas, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(noOcupationEnd, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00 * 10 / monthDays, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	    Assert.assertEquals(ocupationStart, datas.get(1).getStartDate());
	    Assert.assertEquals(endDate, datas.get(1).getEndDate());
	    Assert.assertEquals(1750.00 * (monthDays - 10) / monthDays,
		    Double.parseDouble(datas.get(1).getExpression()), DELTA);

	    // 601 o 611 Base de Accidentes de Trabajo.
	    datas = salary.getContextData().get(CGP_BASE.getName());
	    Assert.assertEquals(2, datas.size());
	    Collections.sort(datas, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(noOcupationEnd, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00 * 10 / monthDays, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	    Assert.assertEquals(ocupationStart, datas.get(1).getStartDate());
	    Assert.assertEquals(endDate, datas.get(1).getEndDate());
	    Assert.assertEquals(1750.00 * (monthDays - 10) / monthDays,
		    Double.parseDouble(datas.get(1).getExpression()), DELTA);

	});
	;
    }

    @Test
    public void testSalaryDAOI() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);
	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	DomainRecord domain = newDomain(aonContext);

	ScopeRecord scope = newScope(aonContext, domain.getId());

	EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(aonContext, domain.getId(), scope.getId(),
		SSRegimeType.GENERAL);

	EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, domain.getId(), scope.getId(),
		enterpriseActivity.getId(), CCCType.PRINCIPAL, ccc);

	WorkplaceRecord workplace = newWorkplace(aonContext, domain.getId(), scope.getId(),
		enterpriseActivity.getEnterprise());

	RegistryRecord person = newPerson(aonContext, domain.getId(), "00000000A");

	String deductions[] = new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05", "BASE_IRPF * PORCENTAJE_IRPF/100" };

	Map<String, String> data = new HashMap<String, String>() {
	    {
		put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
	    }
	};

	Date startDate = getFirstDayOfMonth(getToday());
	Date endDate = AonDateUtils.add(startDate, DAY_OF_MONTH, 1);

	List<ContractRecord> contracts = new ArrayList<ContractRecord>();
	for (int i = 0; i < 10; i++) {
	    //@formatter:off
			contracts.add(newContract(aonContext, 
					SSRegimeType.GENERAL, 
					CCCType.PRINCIPAL, 
					startDate, 
					endDate, 
					data, 
					new String[] {
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES"
					}, 
					deductions,
					null, 
					domain.getId(), 
					person.getId(), 
					workplace.getId(), 
					enterpriseCcc.getId(), 
					enterpriseActivity.getId()));
			//@formatter:on

	    startDate = add(endDate, DAY_OF_MONTH, 1);
	    endDate = AonDateUtils.add(startDate, DAY_OF_MONTH, 2);
	}

	for (int i = 1; i < 10; i++) {
	    person = newPerson(aonContext, domain.getId(),
		    String.format("%sA", new String(new char[8]).replace("\0", Integer.toString(i))));
	    contracts
		    .add(newContract(aonContext, SSRegimeType.GENERAL, CCCType.PRINCIPAL, getFirstDayOfYear(getToday()),
			    null, data, new String[] { String.format("%f * DIAS_TRABAJADOS / DIAS_MES", 1000.00 * i) },
			    deductions, null, domain.getId(), person.getId(), workplace.getId(), enterpriseCcc.getId(),
			    enterpriseActivity.getId()));
	}

	startDate = getFirstDayOfMonth(getToday());
	endDate = getLastDayOfMonth(startDate);

	for (ContractRecord contract : contracts) {
	    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		    endDate, contract);
	    calculateAndSave(connection, ctx);
	}

	Map<String, Long> employeDocuments = new HashMap<String, Long>();
	//@formatter:off
		AON.getSalaryData(aonContext, 
				p-> p.getCCCProperty().eq(ccc)
		)
		.forEach(salary-> { 
			System.out.println(salary.getEmployeeDocument() );
			for ( ContextData ctxData : salary.getContextData().get(ContextVariable.CGC_BASE.getName()) )
				System.out.println(ContextVariable.CGC_BASE.getName() + " = " + ctxData.getExpression() + "[" + ctxData.getStartDate() + ".." + ctxData.getEndDate() + "]" );
			employeDocuments.put(salary.getEmployeeDocument(),salary.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.counting()));
			
		})
		;
		//@formatter:on

	for (String employeeDocument : employeDocuments.keySet())
	    System.out.println(employeeDocument);

	Assert.assertEquals(10, employeDocuments.size());

	Assert.assertEquals((Long) 10L, employeDocuments.get("00000000A"));

	for (int i = 1; i < 10; i++) {
	    Assert.assertEquals((Long) 1L, employeDocuments
		    .get(String.format("%sA", new String(new char[8]).replace("\0", Integer.toString(i)))));
	}
    }

    // -------------------------------------------------------------------------

    @Test
    public void testCretaTrabajadoresYTramosArtistas()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalArtistas(tramo);

    }

    @Test
    public void testCretaTrabajadoresYTramosArtistasII()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	Date startDateI = getFirstDayOfMonth(getToday());
	Date endDateI = add(startDateI, Calendar.DAY_OF_MONTH, 5);

	Date startDateII = add(endDateI, Calendar.DAY_OF_MONTH, 5);
	Date endDateII = add(startDateII, Calendar.DAY_OF_MONTH, 5);

	String dni = Integer.toString((int) (Math.random() * 1000000000.00));

	@SuppressWarnings("serial")
	ContractRecord contractI = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateI,
		endDateI, dni);
	ContractRecord contractII = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateII,
		endDateII, dni);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, startDateI, endDateII, ccc, contractI, contractII);

	Assert.assertEquals(2, tramos.size());

	Tramo tramoI = tramos.get(0);
	Assert.assertEquals("01", tramoI.getFechaDesde().getDia());
	Assert.assertEquals("06", tramoI.getFechaHasta().getDia());
	assertTramoActivoNormalArtistas(tramoI);

	Tramo tramoII = tramos.get(1);
	Assert.assertEquals(Integer.toString(get(startDateII, DAY_OF_MONTH)), tramoII.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDateII, DAY_OF_MONTH)), tramoII.getFechaHasta().getDia());
	assertTramoActivoNormalArtistas(tramoII);

    }

    @Test
    public void testCretaArtistasII() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	Date startDateI = getFirstDayOfMonth(getToday());
	Date endDateI = add(startDateI, Calendar.DAY_OF_MONTH, 5);

	Date startDateII = add(endDateI, Calendar.DAY_OF_MONTH, 5);
	Date endDateII = add(startDateII, Calendar.DAY_OF_MONTH, 6);

	String dni = Integer.toString((int) (Math.random() * 1000000000.00));

	@SuppressWarnings("serial")
	ContractRecord contractI = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateI,
		endDateI, dni);
	ContractRecord contractII = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateII,
		endDateII, dni);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDateI, endDateII, ccc, contractI, contractII);
	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getBases(connection, trabajadoresTramos);

	Assert.assertEquals(2, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoI = tramos.get(0);
	Assert.assertEquals("01", tramoI.getFechaDesde().getDia());
	Assert.assertEquals("06", tramoI.getFechaHasta().getDia());
	double _300 = tramoI.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("300"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_300, (1750.00 * 6 / 30) * 100.00, DELTA);

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoII = tramos.get(1);
	Assert.assertEquals(Integer.toString(get(startDateII, DAY_OF_MONTH)), tramoII.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDateII, DAY_OF_MONTH)), tramoII.getFechaHasta().getDia());
	_300 = tramoII.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("300"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_300, (int) ((1750.00 * 7.00 / 30.00) * 100.00), DELTA);

    }

    @Test
    public void testCretaArtistas0DNIV() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	Date startDateI = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 4);
	Date endDateI = startDateI;

	Date startDateII = add(endDateI, Calendar.DAY_OF_MONTH, 5);
	Date endDateII = startDateII;

	Date startDateIII = add(endDateII, Calendar.DAY_OF_MONTH, 5);
	Date endDateIII = startDateIII;

	Date startDateIV = add(endDateIII, Calendar.DAY_OF_MONTH, 5);
	Date endDateIV = startDateIV;

	Date startDateV = add(endDateIV, Calendar.DAY_OF_MONTH, 5);
	Date endDateV = startDateV;

	String dni = Integer.toString((int) (Math.random() * 1000000000.00));
	String zeroDni = "0" + dni;
	String zeroZeroDni = "00" + dni;
	String whiteSpaceDni = " " + dni;
	String whiteSpaceDniWhiteSapce = " " + dni + " ";

	@SuppressWarnings("serial")
	ContractRecord contractI = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateI,
		endDateI, dni);
	ContractRecord contractII = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateII,
		endDateII, zeroZeroDni);
	ContractRecord contractIII = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateIII,
		endDateIII, zeroDni);
	ContractRecord contractIV = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateIV,
		endDateIV, whiteSpaceDni);
	ContractRecord contractV = newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.ARTIST, startDateV,
		endDateV, whiteSpaceDniWhiteSapce);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, getFirstDayOfMonth(startDateI), getLastDayOfMonth(startDateI), ccc, contractI, contractII,
		contractIII, contractIV, contractV);
	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getBases(connection, trabajadoresTramos);

	Assert.assertEquals(5, tramos.size());

	{
	    net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoI = tramos.get(0);
	    Assert.assertEquals(get(startDateI, DAY_OF_MONTH), Integer.parseInt(tramoI.getFechaDesde().getDia()));
	    Assert.assertEquals(get(endDateI, DAY_OF_MONTH), Integer.parseInt(tramoI.getFechaHasta().getDia()));
	    double _300 = tramoI.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("300"))
		    .map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	    org.junit.Assert.assertEquals(_300, (int) ((1750.00 / 30.00) * 100.00), DELTA);
	}
	{
	    net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoII = tramos.get(1);
	    Assert.assertEquals(get(startDateII, DAY_OF_MONTH), Integer.parseInt(tramoII.getFechaDesde().getDia()));
	    Assert.assertEquals(get(endDateII, DAY_OF_MONTH), Integer.parseInt(tramoII.getFechaHasta().getDia()));
	    double _300 = tramoII.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("300"))
		    .map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	    org.junit.Assert.assertEquals(_300, (int) ((1750.00 / 30.00) * 100.00), DELTA);
	}

	{
	    net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoIII = tramos.get(2);
	    Assert.assertEquals(get(startDateIII, DAY_OF_MONTH), Integer.parseInt(tramoIII.getFechaDesde().getDia()));
	    Assert.assertEquals(get(endDateIII, DAY_OF_MONTH), Integer.parseInt(tramoIII.getFechaHasta().getDia()));
	    double _300 = tramoIII.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("300"))
		    .map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	    org.junit.Assert.assertEquals(_300, (int) ((1750.00 / 30.00) * 100.00), DELTA);
	}

	{
	    net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoIV = tramos.get(3);
	    Assert.assertEquals(get(startDateIV, DAY_OF_MONTH), Integer.parseInt(tramoIV.getFechaDesde().getDia()));
	    Assert.assertEquals(get(endDateIV, DAY_OF_MONTH), Integer.parseInt(tramoIV.getFechaHasta().getDia()));
	    double _300 = tramoIV.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("300"))
		    .map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	    org.junit.Assert.assertEquals(_300, (int) ((1750.00 / 30.00) * 100.00), DELTA);
	}

	{
	    net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramoV = tramos.get(4);
	    Assert.assertEquals(get(startDateV, DAY_OF_MONTH), Integer.parseInt(tramoV.getFechaDesde().getDia()));
	    Assert.assertEquals(get(endDateV, DAY_OF_MONTH), Integer.parseInt(tramoV.getFechaHasta().getDia()));
	    double _300 = tramoV.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("300"))
		    .map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	    org.junit.Assert.assertEquals(_300, (int) ((1750.00 / 30.00) * 100.00), DELTA);
	}
    }

    @Test
    public void testCretaTrabajadoresYTramosNormal()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo);

    }

    @Test
    public void testCretaTrabajadoresYTramosNormalFormacionContinua()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Liquidacion liquidacion = getLiquidacion(connection, contract, startDate, endDate, ccc);
	assertLiquidacionFormacionContinua(liquidacion);

	List<Tramo> tramos = liquidacion.getLiquidacionMes().get(0).getTrabajadores().getTrabajador().get(0).getTramos()
		.getTramo();

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo);

    }

    @Test
    public void testCretaTrabajadoresYTramosFormacionEnAlternanciaNormal()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C421, "03", CCCType.TRAINING);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalFormacionEnAlternancia(tramo);

    }

    @Test
    public void testCretaTrabajadoresYTramosGrupoDiario()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompletoDiario(tramo);

    }

    @Test
    public void testCretaTrabajadoresYTramosTiempoCompletoNo()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100);

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.FULL_TIME,
		"FALSO()");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoParcial(tramo);

    }

    @Test
    public void testCretaTrabajadoresYTramosTiempoParcial()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoParcial(tramo);

    }

//	@Test
//	public void testCretaTrabajadoresYTramosTiempoParcialH02()
//			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
//		Connection connection = getConnection();
//		AONContext aonContext = new AONContext(connection);
//
//		cleanSalaries(aonContext);
//		cleanSystemPayments(aonContext);
//
//		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
//		
//		@SuppressWarnings("serial")
//		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200);
//
//		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
//		Date endDate = getLastDayOfMonth(startDate);
//		
//		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
//		
//		Assert.assertEquals(1, tramos.size());
//		
//		Tramo tramo = tramos.get(0); 
//		Assert.assertEquals("01", tramo.getFechaDesde().getDia());
//		Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
//		assertTramoActivoNormalTiempoParcial(tramo);
//
//	}

    @Test
    public void testCretaTrabajadoresYTramosTiempoParcialAjuste()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, add(endDate, DAY_OF_MONTH, -1), ContextVariable.PARTIAL_FACTOR, "0.5");
	addData(aonContext, contract, endDate, null, ContextVariable.PARTIAL_FACTOR, "0.25");

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("30", tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoParcial(tramo);

    }

    @Test
    public void testCretaTrabajadoresYTramosTiempoParcialSinAjuste()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 3);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, add(endDate, DAY_OF_MONTH, -1), ContextVariable.PARTIAL_FACTOR, "0.5");
	addData(aonContext, contract, endDate, null, ContextVariable.PARTIAL_FACTOR, "0.25");

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("29", tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoParcial(tramo);

	tramo = tramos.get(1);
	Assert.assertEquals("30", tramo.getFechaDesde().getDia());
	Assert.assertEquals("30", tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoParcial(tramo);
    }

    @Test
    public void testCretaTrabajadoresYTramosITTiempoParcial()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");

	addData(aonContext, contract, contract.getStartDate(), null, ContextVariable.PARTIAL_FACTOR, "0.5");

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);
	Date endDate = getLastDayOfMonth(startDate);

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, endDate, null, null);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("30", tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoParcial(tramo);

	tramo = tramos.get(1);
	Assert.assertEquals("31", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo);
    }

    @Test
    public void testCretaTITTiempoParcial() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.PARTIAL_FACTOR.getName(), "0.25");

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);
	Date endDate = getLastDayOfMonth(startDate);

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, endDate, null, null);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("30", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (1750.00 * 0.25) * 100, DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, (1750.00 * 0.25) * 100, DELTA);

	tramo = tramos.get(1);
	Assert.assertEquals("31", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	_500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (int) (1750.00 * 0.25 / 30.00 * 100.00), DELTA);
	double _603 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("603"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_603, (int) (1750.00 * 0.25 / 30.00 * 100.00), DELTA);
    }

    @Test
    public void testCretaTiempoParcialAjusteMensual() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.PARTIAL_FACTOR.getName(), "0.25");

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, add(endDate, DAY_OF_MONTH, -1), ContextVariable.PARTIAL_FACTOR, "0.5");
	addData(aonContext, contract, endDate, null, ContextVariable.PARTIAL_FACTOR, "0.5");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("30", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (1750.00 * 0.25) * 100, DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, (1750.00 * 0.25) * 100, DELTA);

    }

    @Test
    public void testCretaTiempoParcialH02() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.PARTIAL_FACTOR.getName(), "0.25");

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (1750.00 * 0.25) * 100, DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, (1750.00 * 0.25) * 100, DELTA);
	double _01 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));

	cleanSalaries(aonContext);

	PaymentConceptRecord h02Concept = addConcept(aonContext, "HORAS_COMPL", PaymentType.CRA_0057);
	addPayment(aonContext, contract, h02Concept,
		String.format("%s * 66.66", ContextVariable.ADDITIONAL_HOURS.getName()));
	addData(aonContext, contract, startDate, endDate, ContextVariable.ADDITIONAL_HOURS.getName(), "11");

	tramos = getTramosBases(connection, startDate, endDate, ccc, contract);
	tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());

	double __02 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(11, __02, 0.00);
	double __537 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(11 * 66.66 * 100.00, __537, DELTA);

	double __01 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_01 + __02, __01, 0.00);
	double __500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500 + __537, __500, DELTA);
	double __601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601 + __537, __601, DELTA);

    }

    @Test
    public void testCretaTiempoParcialH02WithoutCode() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.PARTIAL_FACTOR.getName(), "0.25");

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (1750.00 * 0.25) * 100, DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, (1750.00 * 0.25) * 100, DELTA);
	double _01 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));

	cleanSalaries(aonContext);

	addPayment(aonContext, contract, "Horas Complementarias",
		String.format("%s * 66.66", ContextVariable.ADDITIONAL_HOURS.getName()), "_P", "_P",
		PaymentType.CRA_0057);
	addData(aonContext, contract, startDate, endDate, ContextVariable.ADDITIONAL_HOURS.getName(), "11");

	tramos = getTramosBases(connection, startDate, endDate, ccc, contract);
	tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());

	double __02 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(11, __02, 0.00);
	double __537 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(11 * 66.66 * 100.00, __537, DELTA);

	double __01 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_01 + __02, __01, 0.00);
	double __500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500 + __537, __500, DELTA);
	double __601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601 + __537, __601, DELTA);

    }

    @Test
    public void testCretaTiempoParcialWithoutH01() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C289, "01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.PARTIAL_FACTOR.getName(), "1.00");

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (1750.00) * 100, DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, (1750.00) * 100, DELTA);

	tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01")).findAny()
		.ifPresent(d -> org.junit.Assert.fail("H 01 Dato solicitado proporcionado no requerido"));
	;

    }

    @Test
    public void testCretaTiempoParcialZeroWorkingHours() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C289, "01", getToday(), getToday());
	addPayment(aonContext, contract, "HORAS_TRABAJADAS * 0.00");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.MONDAY_DAYS.getName(), "30.00");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.MONDAY_HOURS.getName(), "0.25");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.TUESDAY_HOURS.getName(), "0.25");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.WEDNESDAY_HOURS.getName(), "0.25");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.THURSDAY_HOURS.getName(), "0.25");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.FRIDAY_HOURS.getName(), "0.25");

	// addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
	// ContextVariable.PARTIAL_FACTOR.getName(), "0.01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.SALARY_HOURS.getName(), "1.00");

	Date startDate = getToday();
	Date endDate = getToday();

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	Integer today = get(getToday(), Calendar.DAY_OF_MONTH);

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals((int) today, (int) Integer.parseInt(tramo.getFechaDesde().getDia()));
	Assert.assertEquals((int) today, (int) Integer.parseInt(tramo.getFechaHasta().getDia()));
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, Math.round((1750.00 / 30 * 0.03125) * 100), DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, Math.round((1750.00 / 30 * 0.03125) * 100), DELTA);

	double _01 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));

	org.junit.Assert.assertEquals(_01, 1.00, DELTA);
    }

    @Test
    public void testCretaTiempoCompletoWithH01() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.PARTIAL_FACTOR.getName(), "0.75");

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (1750.00) * 75, DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, (1750.00) * 75, DELTA);

	tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01")).findAny()
		.orElseThrow(() -> new AssertionError("H 01 Dato solicitado no proporcionado"));
	;

    }

    @Test
    public void testCretaTiempoParcialWithoutH01I() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C289, "01");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.AGREEMENT_HOURS.getName(), "40.00");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.MONDAY_HOURS.getName(), "8.00");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.TUESDAY_HOURS.getName(), "8.00");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.WEDNESDAY_HOURS.getName(), "8.00");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.THURSDAY_HOURS.getName(), "8.00");
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		ContextVariable.FRIDAY_HOURS.getName(), "8.00");

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500, (1750.00) * 100, DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601, (1750.00) * 100, DELTA);

	tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01")).findAny()
		.ifPresent(d -> org.junit.Assert.fail("H 01 Dato solicitado proporcionado no requerido"));
	;

    }

    @Test
    public void testCretaTiempoParcialAjusteMensualII() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "01");
	// addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
	// ContextVariable.PARTIAL_FACTOR.getName(), "0.25");

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, add(endDate, DAY_OF_MONTH, -2), ContextVariable.PARTIAL_FACTOR,
		"0.25");
	addData(aonContext, contract, add(endDate, DAY_OF_MONTH, -1), null, ContextVariable.PARTIAL_FACTOR, "0.50");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals("29", tramo.getFechaHasta().getDia());
	double _500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500 / 100.00, (1750.00 * 0.25 / 30.00 * 29), DELTA);
	double _601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601"))
		.map(d -> d.getValor()).collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601 / 100.00, (1750.00 * 0.25 / 30.00 * 29), DELTA);

	tramo = tramos.get(1);
	Assert.assertEquals("30", tramo.getFechaDesde().getDia());
	Assert.assertEquals("31", tramo.getFechaHasta().getDia());
	_500 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("500")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_500 / 100.00, (1750.00 * 0.50 / 30.00), DELTA * 1000);
	_601 = tramo.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("601")).map(d -> d.getValor())
		.collect(Collectors.summingDouble(Double::parseDouble));
	org.junit.Assert.assertEquals(_601 / 100.00, (1750.00 * 0.50 / 30.00), DELTA * 1000);

    }

    @Test
    public void testCretaTrabajadoresYTramosIT15Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 14);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(3, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("25", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("26", tramo2.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo2);

    }

    @Test
    public void testCretaTrabajadoresYTramosDosIT15Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 4);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	startIT = add(endIT, DAY_OF_MONTH, 1);
	endIT = add(startIT, DAY_OF_MONTH, 4);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(3, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("20", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("21", tramo2.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo2);

    }

    @Test
    public void testCretaTrabajadoresYTramosIT4Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 3);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(3, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("14", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("15", tramo2.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo2);

    }

    @Test
    public void testCretaTrabajadoresYTramosIT25Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 2);
	Date endIT = add(startIT, DAY_OF_MONTH, 24);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(5, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("02", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("03", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("17", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("18", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("22", tramo2.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo2);

	Tramo tramo3 = tramos.get(3);
	Assert.assertEquals("23", tramo3.getFechaDesde().getDia());
	Assert.assertEquals("27", tramo3.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo3);

	Tramo tramo4 = tramos.get(4);
	Assert.assertEquals("28", tramo4.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo4.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo4);

    }

    @Test
    public void testCretaTrabajadoresYTramosIT99Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 5);
	Date endIT = null; // add(startIT, DAY_OF_MONTH, 24);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(4, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("05", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("06", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("20", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("21", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("25", tramo2.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo2);

	Tramo tramo3 = tramos.get(3);
	Assert.assertEquals("26", tramo3.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo3.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo3);

    }

    @Test
    public void testCretaTrabajadoresYTramosIT3Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 2);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(3, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("13", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("14", tramo2.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo2);

    }

    @Test
    public void testCretaTrabajadoresYTramosIT16Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 15);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	// Assert.assertEquals(4, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("25", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("26", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("26", tramo2.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo2);

	Tramo tramo3 = tramos.get(3);
	Assert.assertEquals("27", tramo3.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo3.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo3);

    }

    @Test
    public void testCretaTrabajadoresYTramosCommonATEP()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 4);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_OCCUPATIONAL_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(3, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("11", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("12", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("15", tramo1.getFechaHasta().getDia());
	assertTramoITATEPPagoDelegado(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("16", tramo2.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo2);

    }

    @Test
    public void testCretaTrabajadoresYTramosATEP()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 4);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.OCCUPATIONAL_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(3, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("15", tramo1.getFechaHasta().getDia());
	assertTramoITATEPPagoDelegado(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("16", tramo2.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo2);

    }

    @Test
    public void testCretaTrabajadoresYTramosMaternidad()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.MATERNITY, 
				startIT, 
				null, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	// Maternidad
	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo1.getFechaHasta().getDia());
	assertTramoMaternidadTiempoCompleto(tramo1);

    }

    @Test
    public void testCretaTrabajadoresYTramosRiesgoYMaternidad()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 4);
	Date endIT = add(startIT, DAY_OF_MONTH, 5);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null);
		//@formatter:on

	Date startRisk = add(endIT, DAY_OF_MONTH, 1);
	Date endRisk = add(startRisk, DAY_OF_MONTH, 9);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.PREGNANCY_RISK, 
				startRisk, 
				endRisk, 
				null);
		//@formatter:on

	Date startMtndad = add(endRisk, DAY_OF_MONTH, 1);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.MATERNITY, 
				startMtndad, 
				null, 
				null);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	for (Tramo t : tramos)
	    System.out.println("Tramo : " + t.getFechaDesde().getMes() + "/" + t.getFechaDesde().getDia() + "..."
		    + t.getFechaHasta().getMes() + "/" + t.getFechaHasta().getDia());

	Assert.assertEquals(4, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("04", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	// IT
	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("05", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);

	// Risk
	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("11", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("20", tramo2.getFechaHasta().getDia());
	assertTramoMaternidadTiempoCompleto(tramo2);

	// Mtndad
	Tramo tramo3 = tramos.get(3);
	Assert.assertEquals("21", tramo3.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo3.getFechaHasta().getDia());
	assertTramoMaternidadTiempoCompleto(tramo3);

    }

    @Test
    public void testCretaTrabajadoresYTramosMaternidadTiempoParcial()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.PATERNITY, 
				startIT, 
				null, 
				null/*1750.00/30*/);
		//@formatter:on

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.PATERNITY_FACTOR,
		"0.33");

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	// Paternidad
	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo1.getFechaHasta().getDia());
	assertTramoMaternidadTiempoParcial(tramo1);

    }

    @Test
    public void testCretaTrabajadoresYTramosERETotal()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR, 1.00);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertTramoExpedienteRegulacionEmpleoTotal(tramo1);

    }

    @Test
    public void testCretaTrabajadoresYTramosEREFZATotal()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR_FORCE, 1.00);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertTramoExpedienteRegulacionEmpleoTotal(tramo1);

    }

    @Test
    public void testCretaTrabajadoresYTramosEREFZATotalAndIT()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, -1);
	Date endDate = getLastDayOfMonth(startDate);

	Date endERE = add(startDate, DAY_OF_MONTH, 4);

	addData(aonContext, contract, startDate, endERE, ContextVariable.ERE_FACTOR_FORCE_OFF, 1.00);

	Date startIT = add(endERE, DAY_OF_MONTH, 2);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, null, null);
	//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		PaymentConceptRecord ereFzaExonerado = addConcept(aonContext, "ERE_FZA_EXONERADO");

		addPayment(aonContext, contract, ereFzaExonerado, null, 
				String.format("%s * BASE_REGULADORA",  ERE_DAYS_FORCE_OFF)
				);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(5, tramos.size());

	// ERE
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("05", tramo0.getFechaHasta().getDia());
	assertTramoExpedienteRegulacionEmpleoTotal(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("06", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("06", tramo1.getFechaHasta().getDia());
	assertTramoActivoNormal(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("07", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("21", tramo2.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo2);

	Tramo tramo3 = tramos.get(3);
	Assert.assertEquals("22", tramo3.getFechaDesde().getDia());
	Assert.assertEquals("26", tramo3.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo3);

	Tramo tramo4 = tramos.get(4);
	Assert.assertEquals("27", tramo4.getFechaDesde().getDia());
	// Assert.assertEquals("28", tramo4.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo4);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(5, bases.size());
    }

    @Test
    public void testCretaTrabajadoresYTramosEREFZAParcialAndIT()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = getFirstDayOfMonth(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, null, ContextVariable.ERE_FACTOR_FORCE_OFF, 0.15);

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate, null, null);
	//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		PaymentConceptRecord ereFzaExonerado = addConcept(aonContext, "ERE_FZA_EXONERADO");

		addPayment(aonContext, contract, ereFzaExonerado, null, 
				String.format("%s; %s * BASE_REGULADORA",  ERE_FACTOR_FORCE_OFF, ERE_DAYS_FORCE_OFF)
				);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	for (Tramo tramo : tramos) {
	    System.out.println(tramo.getFechaDesde().getDia() + ".." + tramo.getFechaHasta().getDia());
	}

	Assert.assertEquals(3, tramos.size());

	// ERE
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("15", tramo0.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo0);
	assertTramoExpedienteRegulacionEmpleoParcial(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("16", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("20", tramo1.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo1);
	assertTramoExpedienteRegulacionEmpleoParcial(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("21", tramo2.getFechaDesde().getDia());
	assertTramoITPagoDelegado(tramo1);
	assertTramoExpedienteRegulacionEmpleoParcial(tramo2);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(3, bases.size());
    }

    @Test
    public void testCretaTrabajadoresYTramosEREFZAParcialAndITII()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = getFirstDayOfMonth(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, null, ContextVariable.ERE_FACTOR_FORCE_OFF, 0.15);

	Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 6);

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, null, null);
	//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		PaymentConceptRecord ereFzaExonerado = addConcept(aonContext, "ERE_FZA_EXONERADO");

		addPayment(aonContext, contract, ereFzaExonerado, null, 
				String.format("%s; %s * BASE_REGULADORA",  ERE_FACTOR_FORCE_OFF, ERE_DAYS_FORCE_OFF)
				);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	for (Tramo tramo : tramos) {
	    System.out.println(tramo.getFechaDesde().getDia() + ".." + tramo.getFechaHasta().getDia());
	}

	Assert.assertEquals(4, tramos.size());

	// ERE
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("06", tramo0.getFechaHasta().getDia());
	assertTramoExpedienteRegulacionEmpleoParcialActivo(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("07", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("21", tramo1.getFechaHasta().getDia());
	assertTramoIT15PrimerosDias(tramo1);
	assertTramoExpedienteRegulacionEmpleoParcial(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("22", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("26", tramo2.getFechaHasta().getDia());
	assertTramoITPagoDelegado(tramo2);
	assertTramoExpedienteRegulacionEmpleoParcial(tramo2);

	Tramo tramo3 = tramos.get(3);
	Assert.assertEquals("27", tramo3.getFechaDesde().getDia());
	assertTramoITPagoDelegado(tramo3);
	assertTramoExpedienteRegulacionEmpleoParcial(tramo3);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(4, bases.size());
    }

    @Test
    public void testCretaTrabajadoresYTramosEREFZAParcialAndITIII()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = getFirstDayOfMonth(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, endDate, ContextVariable.ERE_FACTOR_FORCE, 0.50);

	Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 14);
	Date endIT = add(startDate, Calendar.DAY_OF_MONTH, 24);

	addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startIt, endIT, null);
	addData(aonContext, contract, startDate, endDate, ContextVariable.REGULATORY_BASE, 35.00);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	for (Tramo tramo : tramos) {
	    System.out.println(tramo.getFechaDesde().getDia() + ".." + tramo.getFechaHasta().getDia());
	}

	Assert.assertEquals(3, tramos.size());

	// ERE
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("14", tramo0.getFechaHasta().getDia());
	assertTramoExpedienteRegulacionEmpleoParcialActivo(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("15", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("25", tramo1.getFechaHasta().getDia());
	assertTramoITATEPPagoDelegado(tramo1);
	;
	assertTramoExpedienteRegulacionEmpleoParcial(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("26", tramo2.getFechaDesde().getDia());
	assertTramoExpedienteRegulacionEmpleoParcialActivo(tramo2);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(3, bases.size());
    }

    @Test
    public void testCretaTrabajadoresYTramosEREFZAOFFTotal()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR_FORCE_OFF, 1.00);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertTramoExpedienteRegulacionEmpleoTotal(tramo1);

    }

    @Test
    public void testCretaTrabajadoresYTramosEREParcial()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR_FORCE_OFF, 0.50);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertTramoExpedienteRegulacionEmpleoParcialActivo(tramo1);

    }

    @Test
    public void testCretaERETotal() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR, 1.00);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	// Activo
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	// assertDato(tramo0.getDatosTramo().getDato(), "I", "51", "M");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "509",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "603",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 100)));

    }

    @Test
    public void testCretaERETotalWithZeroBaseCgp() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { new Extra() {
	    {
		this.expression = "0.00";
		this.month = Month.DECEMBER;
		this.start = "01/07";
		this.end = "31/12";
		this.issue = "15/12";
	    }
	}, new Extra() {
	    {
		this.expression = "0.00";
		this.month = Month.JULY;
		this.start = "01/01";
		this.end = "30/06";
		this.issue = "01/07";
	    }
	}, new Extra() {
	    {
		this.expression = "0.00";
		this.month = Month.MARCH;
		this.start = "01/01 -1";
		this.end = "31/12 -1";
		this.issue = "01/3";
	    }
	} });

	contract.setAgreementLevel(category.getAgreementLevel());
	contract.update();
//		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
//		addPayment(aonContext, contract, pagaExtra, "ROUND(INPUT(\"/*user*/P_0 + P_1/**/\",\"...\"),2)");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 0);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR, 1.00);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(1, tramos.size());

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(0);
	Assert.assertEquals("01", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "509", Integer.toString((int) Math.round(1750.00 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "603", Integer.toString((int) Math.round(1750.00 * 100)));

    }

    @Test
    public void testCretaEREFZATotal() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR_FORCE, 1.00);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	// Activo
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	// assertDato(tramo0.getDatosTramo().getDato(), "I", "51", "M");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "509",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "603",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 100)));

    }

    @Test
    public void testCretaEREFZAOFFTotal() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR_FORCE_OFF, 1.00);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	// Activo
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	// assertDato(tramo0.getDatosTramo().getDato(), "I", "51", "M");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "509",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "603",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 100)));

    }

    @Test
    public void testCretaEREParcial() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR, 0.25);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	// Activo
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	// assertDato(tramo0.getDatosTramo().getDato(), "I", "51", "M");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.75 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.75 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "536",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.25 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "636",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.25 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "H", "05", "750");

    }

    @Test
    public void testCretaEREFZAParcial() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR_FORCE, 0.25);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	// Activo
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	// assertDato(tramo0.getDatosTramo().getDato(), "I", "51", "M");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.75 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.75 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "536",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.25 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "636",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.25 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "H", "05", "750");

    }

    @Test
    public void testCretaEREFZOFFParcial() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR_FORCE_OFF, 0.25);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	// Activo
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	// assertDato(tramo0.getDatosTramo().getDato(), "I", "51", "M");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 10.00 / 30.00 * 100)));
	tramo0.getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01")).findAny()
		.ifPresent(d -> org.junit.Assert.fail("H 01 Dato solicitado proporcionado no requerido"));
	;

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.75 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.75 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "536",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.25 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "636",
		Integer.toString((int) Math.round(1750.00 * 20.00 / 30.00 * 0.25 * 100)));
	assertDato(tramo1.getDatosTramo().getDato(), "H", "05", "750");

    }

    @Test
    @Ignore("Not fixed yet BRUTO...:-(")
    public void testCretaEREFZOFFParcialII() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);
	addPayment(aonContext, contract, "BRUTO(1750.00)");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startERE = startDate;
	Date endERE = add(startDate, DAY_OF_MONTH, 24);

	addData(aonContext, contract, startERE, endERE, ContextVariable.ERE_FACTOR_FORCE_OFF, 0.25);

	int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
	int activeDays = monthDays - 25;

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);
	Utils.marshal(trabajadoresTramos, System.out);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	Assert.assertEquals(2, tramos.size());

	// Activo
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("25", tramo0.getFechaHasta().getDia());
	// assertDato(tramo0.getDatosTramo().getDato(), "I", "51", "M");
	// assertDato(tramo0.getDatosTramo().getDato(), "C", "500",
	// Integer.toString((int)Math.round(1750.00 * 25.00 /30.00 * 0.75 * 100)));
	// assertDato(tramo0.getDatosTramo().getDato(), "C", "601",
	// Integer.toString((int)Math.round(1750.00 * 25.00 /30.00 * 0.75 * 100)));
	// assertDato(tramo0.getDatosTramo().getDato(), "C", "536",
	// Integer.toString((int)Math.round(1750.00 * (30 - activeDays) /30.00 * 0.25 *
	// 100)));
	// assertDato(tramo0.getDatosTramo().getDato(), "C", "636",
	// Integer.toString((int)Math.round(1750.00 * (30 - activeDays ) /30.00 * 0.25 *
	// 100)));
	assertDato(tramo0.getDatosTramo().getDato(), "H", "05", "750");

	String diaHasta = Integer.toString(get(endDate, Calendar.DAY_OF_MONTH));
	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("26", tramo1.getFechaDesde().getDia());
	Assert.assertEquals(diaHasta, tramo1.getFechaHasta().getDia());
	// assertDato(tramo1.getDatosTramo().getDato(), "C", "500",
	// Integer.toString((int)Math.round(1750.00 * activeDays /30.00 * 100)));
	// assertDato(tramo1.getDatosTramo().getDato(), "C", "601",
	// Integer.toString((int)Math.round(1750.00 * activeDays /30.00 * 100)));
	tramo1.getDatosTramo().getDato().stream().filter(d -> !d.getCodigo().equals("500"))
		.filter(d -> !d.getCodigo().equals("601")).findAny()
		.ifPresent(d -> org.junit.Assert.fail("Dato solicitado proporcionado no requerido"));
	;

    }

    @Test
    public void testCretaAdditionalHours() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	PaymentConceptRecord concept = addConcept(aonContext, "HORAS_COMPL");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0057);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("HORAS_COMPLEMENTARIAS", "10.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	Dato _537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals("69000", _537.getValor());

	Dato _2 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals("10", _2.getValor());

    }

    @Test
    public void testCretaMultipleAdditionalHours() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	PaymentConceptRecord concept = addConcept(aonContext, "HORAS_COMPL");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0057);

	addData(aonContext, contract, startDate, endDate, "IMPORTE_HORA_COMPLEMENTARIA", "69.00");

	for (int i = 0; i < 10; i++)
	    addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, i), endDate, "HORAS_COMPLEMENTARIAS",
		    "1.00");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	org.junit.Assert.assertEquals(1, tramosBases.size());

	Dato _537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals("69000", _537.getValor());

	Dato _2 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals("10", _2.getValor());

    }

    @Test
    public void testCretaAdditionalHoursWithPeriods() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);

	Date endDate = getLastDayOfMonth(startDate);

	Date endDateI = add(startDate, Calendar.DAY_OF_MONTH, 10);
	Date startDateI = add(startDate, Calendar.DAY_OF_MONTH, 11);

	addData(aonContext, contract, startDate, endDateI, ContextVariable.MONDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDate, endDateI, ContextVariable.TUESDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDate, endDateI, ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDate, endDateI, ContextVariable.THURSDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDate, endDateI, ContextVariable.FRIDAY_HOURS.getName(), "2.00");

	addData(aonContext, contract, startDateI, endDate, ContextVariable.MONDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDateI, endDate, ContextVariable.TUESDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDateI, endDate, ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDateI, endDate, ContextVariable.THURSDAY_HOURS.getName(), "2.00");
	addData(aonContext, contract, startDateI, endDate, ContextVariable.FRIDAY_HOURS.getName(), "2.00");

	PaymentConceptRecord concept = addConcept(aonContext, "HORAS_COMPL");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0057);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("HORAS_COMPLEMENTARIAS", "10.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	Dato _0537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Dato _1537 = tramosBases.get(1).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;

	int end = get(endDate, Calendar.DAY_OF_MONTH);
	int endI = get(endDateI, Calendar.DAY_OF_MONTH);
	int expected = (int) (10.00 * endI / end * 69.00 * 100.00);
	Assert.assertEquals(69000.00, Double.valueOf(_0537.getValor()) + Double.valueOf(_1537.getValor()));

	Dato _02 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Dato _12 = tramosBases.get(1).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));

	expected = (int) (10.00 * endI / end);
	Assert.assertEquals(9.00, Double.valueOf(_02.getValor()) + Double.valueOf(_12.getValor()));

    }

    @Test
    public void testCretaAdditionalHoursWithOtherConcept() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	PaymentConceptRecord concept = addConcept(aonContext, "OTHER_CONCEPT");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0058);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("HORAS_COMPLEMENTARIAS", "10.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	Dato _537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals("69000", _537.getValor());

	Dato _2 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals("10", _2.getValor());

    }

    @Test
    public void testCretaAdditionalHoursWithoutConcept() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0058);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("HORAS_COMPLEMENTARIAS", "10.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	Dato _537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals("69000", _537.getValor());

	Dato _2 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals("10", _2.getValor());

    }

    @Test
    public void testCretaAdditionalHoursWithoutPayment() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("HORAS_NOMINA", "66.00");
		put("HORAS_COMPLEMENTARIAS", "10.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02")).forEach(d -> {
	    throw new AssertionError();
	});

	tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("01")).forEach(d -> {
	    Assert.assertEquals("66", d.getValor());
	});
    }

    @Test
    public void testCretaAdditionalHoursTooSmall() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("HORAS_COMPLEMENTARIAS", "0.01");
		put("IMPORTE_HORA_COMPLEMENTARIA", "57.00");
	    }
	});

	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0058);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02")).forEach(d -> {
	    throw new AssertionError();
	});

	tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537")).forEach(d -> {
	    throw new AssertionError();
	});
    }

    @Test
    public void testCretaAdditionalHoursIT() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	PaymentConceptRecord concept = addConcept(aonContext, "HORAS_COMPL");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0057);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("DIAS_MES", "30.00");
		put("HORAS_COMPLEMENTARIAS", "10.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 9);
	Date endIt = add(startIt, Calendar.DAY_OF_MONTH, 9);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);

	List<Tramo> tramosTrabajadores = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0)
		.getTrabajadores().getTrabajador().get(0).getTramos().getTramo();

	assertEquals(3, tramosTrabajadores.size());

	assertTramoActivoNormal(tramosTrabajadores.get(0));
	assertTramoIT15PrimerosDiasDiario(tramosTrabajadores.get(1));
	assertTramoActivoNormal(tramosTrabajadores.get(0));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	assertEquals(3, tramosBases.size());

	int activeDays1 = 9;
	int activeDays2 = get(endDate, Calendar.DAY_OF_MONTH) - get(endIt, Calendar.DAY_OF_MONTH);

	int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
	int activeDays = 9 + activeDays2;

	Dato _2 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals(Integer.toString((int) (10.00 / activeDays * 9)), _2.getValor());

	Dato _537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals(Integer.toString((int) (6900 * 10.00 / activeDays * 9)), _537.getValor());

	_2 = tramosBases.get(2).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02")).findFirst()
		.orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals(Integer.toString((int) (10.00 / activeDays * (activeDays2))), _2.getValor());

	_537 = tramosBases.get(2).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals(Integer.toString((int) Math.round((6900 * 10.00 / activeDays * (activeDays2)))),
		_537.getValor());

    }

    @Test
    @Ignore
    public void testCretaAdditionalHoursITOK() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	PaymentConceptRecord concept = addConcept(aonContext, "HORAS_COMPL");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0057);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("DIAS_MES", "30.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 9);
	Date endIt = add(startIt, Calendar.DAY_OF_MONTH, 9);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);

	addData(aonContext, contract, startDate, add(startIt, DAY_OF_MONTH, -1), ContextVariable.ADDITIONAL_HOURS,
		"3.00");
	addData(aonContext, contract, add(endIt, DAY_OF_MONTH, +1), endDate, ContextVariable.ADDITIONAL_HOURS, "7.00");

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);

	List<Tramo> tramosTrabajadores = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0)
		.getTrabajadores().getTrabajador().get(0).getTramos().getTramo();

	assertEquals(3, tramosTrabajadores.size());

	assertTramoActivoNormal(tramosTrabajadores.get(0));
	assertTramoIT15PrimerosDiasDiario(tramosTrabajadores.get(1));
	assertTramoActivoNormal(tramosTrabajadores.get(0));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	assertEquals(3, tramosBases.size());

	int activeDays1 = 9;
	int activeDays2 = get(endDate, Calendar.DAY_OF_MONTH) - get(endIt, Calendar.DAY_OF_MONTH);

	int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
	int activeDays = 9 + activeDays2;

	Dato _2 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals(Integer.toString(3), _2.getValor());

	Dato _537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals(Integer.toString((int) (6900 * 3)), _537.getValor());

	_2 = tramosBases.get(2).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02")).findFirst()
		.orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals(Integer.toString(7), _2.getValor());

	_537 = tramosBases.get(2).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;
	Assert.assertEquals(Integer.toString((int) Math.round((6900 * 7))), _537.getValor());

    }

    @Test
    public void testCretaZeroAdditionalHoursIT() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	PaymentConceptRecord concept = addConcept(aonContext, "HORAS_COMPL");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/", "_P", "_P",
		PaymentType.CRA_0057);

	concept = addConcept(aonContext, "HORAS_NOMINA_FIX");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept, "HORAS NOMINA",
		"/*read-only*/HORAS_NOMINA=HORAS_TRABAJADAS/**/", "_P", "_P", PaymentType.CRA_0001);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("DIAS_MES", "30.00");
		put("HORAS_COMPLEMENTARIAS", "0.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

	Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 20);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, null, null);

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, contract);

	List<Tramo> tramosTrabajadores = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0)
		.getTrabajadores().getTrabajador().get(0).getTramos().getTramo();

	assertEquals(2, tramosTrabajadores.size());

	assertTramoActivoNormalTiempoParcial(tramosTrabajadores.get(0));
	assertTramoIT15PrimerosDiasDiario(tramosTrabajadores.get(1));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	assertEquals(2, tramosBases.size());

	assertDato(tramosBases.get(0).getDatosTramo().getDato(), "H", "01");

    }

    @Test
    public void testCretaAdditionalHoursSection() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemData(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>() {
	    {
		put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
		put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
		put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
		put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	PaymentConceptRecord concept = addConcept(aonContext, "HORAS_COMPL");
	addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), concept,
		"HORAS COMPLEMENTARIAS PACTADAS",
		"/*read-only*/(HORAS_COMPLEMENTARIAS=FRACCIONAR(HORAS_COMPLEMENTARIAS)) * IMPORTE_HORA_COMPLEMENTARIA/**/",
		"_P", "_P", PaymentType.CRA_0057);

	addData(aonContext, contract, startDate, endDate, new HashMap<String, String>() {
	    {
		put("HORAS_COMPLEMENTARIAS", "10.00");
		put("IMPORTE_HORA_COMPLEMENTARIA", "69.00");
	    }
	});

//		List<Tramo> tramos = 
//		getTramos(connection, contract, startDate, endDate, ccc);
//		for ( Tramo tramo: tramos ) {
//			assertTramoActivoNormalTiempoParcial(tramo);
//		}

	addBonus(aonContext, contract, startDate, endDate, String.format("TRAMO(FECHA(%d,%d,%d));0.00",
		get(startDate, Calendar.YEAR), get(startDate, Calendar.MONTH) + 1, 12), "AJUSTE");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	assertEquals(2, tramosBases.size());

	int monthDays = get(endDate, Calendar.DAY_OF_MONTH);

	Dato _2 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("02"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	Assert.assertEquals(Integer.toString((int) (10.00 / monthDays * 12)), _2.getValor());

	Dato _537 = tramosBases.get(0).getDatosTramo().getDato().stream().filter(d -> d.getCodigo().equals("537"))
		.findFirst().orElseThrow(() -> new AssertionFailedError(""));
	;

	Assert.assertEquals(Long.toString(Math.round(6900.00 * 10.00 / monthDays * 12)), _537.getValor());

    }

    @Test
    public void testCretaBasesFromSalariesMaternidad() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.PATERNITY, 
				startDate, 
				null, 
				null);
		//@formatter:on

	getTramosBases(connection, startDate, endDate, ccc, contract);

    }

    @Test
    public void testCretaContratosFormacion()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo);

    }

    @Test
    public void testManualPeriodsI() throws ExpressionException, SQLException, SalaryException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemData(aonContext);
	cleanSystemCosts(aonContext);

	//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
				put(ContextVariable.MONTH_DAYS.getName(), "30.00");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

	Date startDateI = contract.getStartDate(); // add(getFirstDayOfMonth(getToday()), MONTH, 1)
	;
	Date endDateI = add(startDateI, DAY_OF_MONTH, 9);
	Date startDateII = add(endDateI, DAY_OF_MONTH, 1);
	Date endDateII = add(startDateII, DAY_OF_MONTH, 9);
	Date startDateIII = add(endDateII, DAY_OF_MONTH, 1);
	Date endDateIII = getLastDayOfMonth(startDateI);

	ISQLContractSalaryCalculatorContext ctxI = getContractSalaryCalculatorContext(connection, startDateI, endDateI,
		endDateI, contract);
	ISQLContractSalaryCalculatorContext ctxII = getContractSalaryCalculatorContext(connection, startDateII,
		endDateII, endDateII, contract);
	ISQLContractSalaryCalculatorContext ctxIII = getContractSalaryCalculatorContext(connection, startDateIII,
		endDateIII, endDateIII, contract);

	CollectSalaryBuilder<ISalary> collectSalaryBuilder = new CollectSalaryBuilder<ISalary>();

	new ContractSalaryCalculator<ISalary>(collectSalaryBuilder).calculate(ctxI);
	new ContractSalaryCalculator<ISalary>(collectSalaryBuilder).calculate(ctxII);

	ctxIII.getExpressionContext().putVariable(ContextVariable.ACTIVE_DAYS,
		collectSalaryBuilder.getAllActiveDaysVar());
	new ContractSalaryCalculator<ISalary>(collectSalaryBuilder).calculate(ctxIII);

	JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
	collectSalaryBuilder.collect(jooqSalaryBuilder);

	int salaries = jooqSalaryBuilder.execute();

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(3, datas.size());
	    Collections.sort(datas, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
	    // I
	    Assert.assertEquals(startDateI, datas.get(0).getStartDate());
	    Assert.assertEquals(endDateI, datas.get(0).getEndDate());
	    Assert.assertEquals(1500.00 * 10 / 30.00, Double.parseDouble(datas.get(0).getExpression()), DELTA);
	    // II
	    Assert.assertEquals(startDateII, datas.get(1).getStartDate());
	    Assert.assertEquals(endDateII, datas.get(1).getEndDate());
	    Assert.assertEquals(1500.00 * 10 / 30.00, Double.parseDouble(datas.get(1).getExpression()), DELTA);

	    // III
	    Assert.assertEquals(startDateIII, datas.get(2).getStartDate());
	    Assert.assertEquals(endDateIII, datas.get(2).getEndDate());
	    Assert.assertEquals(1500.00 * 10 / 30.00, Double.parseDouble(datas.get(2).getExpression()), DELTA);

	});
	;
    }

    @Test
    public void testStrike() throws ExpressionException, SQLException, SalaryException, IOException, JAXBException {

	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	Date contractStart = getFirstDayOfYear(getToday());

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03");

	addData(aonContext, contract, contractStart, contract.getStartDate(), ContextVariable.MONTH_DAYS, "30.00");

	Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);

	Date strikeDay = add(startDate, DAY_OF_MONTH, 10);

	addData(aonContext, contract, strikeDay, strikeDay, new HashMap<String, String>() {
	    {
		put(STRIKE_DAYS.getName(), "1.00");
		put(STRIKE_FACTOR.getName(), "1.00");
	    }
	});

	addPayment(aonContext, contract, "1200.00 * DIAS_TRABAJADOS / DIAS_MES");

	Date endDate = getLastDayOfMonth(startDate);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(2, tramos.size());
	Assert.assertEquals(1, Integer.parseInt(tramos.get(0).getFechaDesde().getDia()));
	Assert.assertEquals(10, Integer.parseInt(tramos.get(0).getFechaHasta().getDia()));
	Assert.assertEquals(10, Integer.parseInt(tramos.get(0).getDiasCotizados()));

	Assert.assertEquals(12, Integer.parseInt(tramos.get(1).getFechaDesde().getDia()));
	Assert.assertEquals(31, Integer.parseInt(tramos.get(1).getFechaHasta().getDia()));
	Assert.assertEquals(19, Integer.parseInt(tramos.get(1).getDiasCotizados()));

    }

    @Test
    public void testCretaITAdjust1DayDaily() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "09");

	Date startIt = getLastDayOfMonth(add(getToday(), MONTH, Calendar.JULY - get(getToday(), MONTH)));

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, null, null);

	Date startDate = getFirstDayOfMonth(startIt);
	Date endDate = getLastDayOfMonth(startDate);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	// Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    Date endActive = add(startIt, DATE, -1);

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		    // 500 Base de contingencias comunes.
		    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());

		    Assert.assertEquals(2, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()));
		    Assert.assertEquals(endDate, datas.get(1).getStartDate());
		    Assert.assertEquals(endDate, datas.get(1).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(1).getExpression()));

		    // 601 o 611 Base de Accidentes de Trabajo.
		    datas = salary.getContextData().get(CGP_BASE.getName());
		    Assert.assertEquals(2, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()));
		    Assert.assertEquals(endDate, datas.get(1).getStartDate());
		    Assert.assertEquals(endDate, datas.get(1).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(1).getExpression()));

		    // 501 Base de Horas Extras Fuerza Mayor
		    datas = salary.getContextData().get(STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		    // 502 Base de Horas Extras
		    datas = salary.getContextData().get(NON_STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
	Assert.assertEquals(2, tramos.size());

	Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaDesde().getMes());
	Assert.assertEquals("30", tramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaHasta().getMes());
	Assert.assertEquals("30", tramos.get(0).getDiasCotizados());
	assertTramoActivoNormalTiempoCompletoDiario(tramos.get(0));

	Assert.assertEquals("31", tramos.get(1).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(1).getFechaDesde().getMes());
	Assert.assertEquals("31", tramos.get(1).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(1).getFechaHasta().getMes());
	Assert.assertEquals("0", tramos.get(1).getDiasCotizados());
	assertTramoIT15PrimerosDiasDiario(tramos.get(1));

	cleanSalaries(aonContext);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);
	Assert.assertEquals(2, bases.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = bases.get(0);
	Assert.assertEquals("01", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo1.getFechaDesde().getMes());
	Assert.assertEquals("30", tramo1.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo1.getFechaHasta().getMes());
	assertDato(tramo1.getDatosTramo().getDato(), "I", "51", "M");
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500", "175000");
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601", "175000");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo2 = bases.get(1);
	Assert.assertEquals("31", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo2.getFechaDesde().getMes());
	Assert.assertEquals("31", tramo2.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo2.getFechaHasta().getMes());
	assertDato(tramo2.getDatosTramo().getDato(), "I", "51", "M");

    }

    @Test
    public void testCretaITAdjust1DayMonthly() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "06");

	Date startIt = getLastDayOfMonth(add(getToday(), MONTH, Calendar.JULY - get(getToday(), MONTH)));

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, null, null);

	Date startDate = getFirstDayOfMonth(startIt);
	Date endDate = getLastDayOfMonth(startDate);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	// Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    Date endActive = add(startIt, DATE, -1);

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		    // 500 Base de contingencias comunes.
		    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());

		    Assert.assertEquals(2, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()));
		    Assert.assertEquals(endDate, datas.get(1).getStartDate());
		    Assert.assertEquals(endDate, datas.get(1).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(1).getExpression()));

		    // 601 o 611 Base de Accidentes de Trabajo.
		    datas = salary.getContextData().get(CGP_BASE.getName());
		    Assert.assertEquals(2, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()));
		    Assert.assertEquals(endDate, datas.get(1).getStartDate());
		    Assert.assertEquals(endDate, datas.get(1).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(1).getExpression()));

		    // 501 Base de Horas Extras Fuerza Mayor
		    datas = salary.getContextData().get(STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		    // 502 Base de Horas Extras
		    datas = salary.getContextData().get(NON_STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
	Assert.assertEquals(1, tramos.size());

	Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaDesde().getMes());
	Assert.assertEquals("30", tramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaHasta().getMes());
	Assert.assertEquals("30", tramos.get(0).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tramos.get(0));

	cleanSalaries(aonContext);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);
	Assert.assertEquals(1, bases.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = bases.get(0);
	Assert.assertEquals("01", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo1.getFechaDesde().getMes());
	Assert.assertEquals("30", tramo1.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo1.getFechaHasta().getMes());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500", "175000");
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601", "175000");

    }

    @Test
    public void testCretaITNotAdjust300Partial() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C300, "05");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.SATURDAY_HOURS,
		2.0);
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.FRIDAY_HOURS,
		2.0);
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.WEDNESDAY_HOURS,
		4.0);
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.MONDAY_HOURS,
		4.0);

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 9);
	Date endIt = add(startIt, Calendar.DAY_OF_MONTH, 1);

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	// Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		    // 500 Base de contingencias comunes.
		    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());

		    Assert.assertEquals(3, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(add(startIt, DATE, -1), datas.get(0).getEndDate());

		    Assert.assertEquals(startIt, datas.get(1).getStartDate());
		    Assert.assertEquals(endIt, datas.get(1).getEndDate());

		    Assert.assertEquals(add(endIt, Calendar.DAY_OF_MONTH, 1), datas.get(2).getStartDate());
		    Assert.assertEquals(endDate, datas.get(2).getEndDate());
		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
	Assert.assertEquals(3, tramos.size());

	Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("09", tramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("9", tramos.get(0).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tramos.get(0));

	Assert.assertEquals("10", tramos.get(1).getFechaDesde().getDia());
	Assert.assertEquals("11", tramos.get(1).getFechaHasta().getDia());
	Assert.assertEquals("2", tramos.get(1).getDiasCotizados());
	assertTramoIT15PrimerosDias(tramos.get(1));

	Assert.assertEquals("12", tramos.get(2).getFechaDesde().getDia());
	Assert.assertEquals("31", tramos.get(2).getFechaHasta().getDia());
	Assert.assertEquals("20", tramos.get(2).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tramos.get(2));

    }

    @Test
    public void testCretaITNotAdjust502Partial() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C502, "06");

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.MONDAY_HOURS,
		6.0);
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.TUESDAY_HOURS,
		6.0);
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.WEDNESDAY_HOURS,
		6.0);
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.THURSDAY_HOURS,
		6.0);
	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.FRIDAY_HOURS,
		6.0);

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 6);
	Date endIt = startIt;

	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	// Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		    // 500 Base de contingencias comunes.
		    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());

		    Assert.assertEquals(3, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(add(startIt, DATE, -1), datas.get(0).getEndDate());

		    Assert.assertEquals(startIt, datas.get(1).getStartDate());
		    Assert.assertEquals(endIt, datas.get(1).getEndDate());
		    org.junit.Assert.assertTrue(Double.parseDouble(datas.get(1).getExpression()) > 0.00);

		    Assert.assertEquals(add(endIt, Calendar.DAY_OF_MONTH, 1), datas.get(2).getStartDate());
		    Assert.assertEquals(endDate, datas.get(2).getEndDate());
		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
	Assert.assertEquals(3, tramos.size());

	Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("06", tramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("6", tramos.get(0).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tramos.get(0));

	Assert.assertEquals("07", tramos.get(1).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(1).getFechaHasta().getDia());
	Assert.assertEquals("1", tramos.get(1).getDiasCotizados());
	assertTramoIT15PrimerosDias(tramos.get(1));

	Assert.assertEquals("08", tramos.get(2).getFechaDesde().getDia());
	Assert.assertEquals("31", tramos.get(2).getFechaHasta().getDia());
	Assert.assertEquals("24", tramos.get(2).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tramos.get(2));

    }

    @Test
    public void testCretaITPagoDelegadoMonthly() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemData(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C401, "08");
	setData(aonContext, contract, ContextVariable.MONTH_DAYS.getName(), "30.00");

	setData(aonContext, contract, ContextVariable.CGC_BASE_MIN.getName(), "[\"08\":(100.00)][GRUPO_COTIZACION]");

	Date startDate = getFirstDayOfMonth(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	Date startIt = add(startDate, DAY_OF_MONTH, -16);
	Date endIt = add(startDate, DAY_OF_MONTH, 20);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	// Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    Date endActive = add(startIt, DATE, -1);

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	for (Tramo tramo : tramos) {
	    // Utils.marshal(tramo, System.out);
	    assertDatosSolicitado(tramo.getDatosTramo().getDatoSolicitado(), "I", "51", "P");
	}

	cleanSalaries(aonContext);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);

	for (net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo : bases) {
	    assertDato(tramo.getDatosTramo().getDato(), "I", "51", "M");
	}

    }

    @Test
    public void testCretaTrabajadoresYTramosITLack15Days()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc);

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIT = add(startDate, DAY_OF_MONTH, 10);
	Date endIT = add(startIT, DAY_OF_MONTH, 14);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE_AT_LACK, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(3, tramos.size());

	// Activo
	Tramo tramo0 = tramos.get(0);
	Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
	Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo0);

	Tramo tramo1 = tramos.get(1);
	Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("25", tramo1.getFechaHasta().getDia());
	assertTramoITPagoDirecto(tramo1);

	Tramo tramo2 = tramos.get(2);
	Assert.assertEquals("26", tramo2.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
	assertTramoActivoNormalTiempoCompleto(tramo2);

    }

    @Test
    public void testCretaOneDayPeriod() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "08");

	Date firstDayOfMonth = getFirstDayOfMonth(getToday());
	Date secondDayOfMonth = add(firstDayOfMonth, DAY_OF_MONTH, 1);

	addData(aonContext, contract, firstDayOfMonth, firstDayOfMonth, ContextVariable.PARTIAL_FACTOR, "0.675");

	addData(aonContext, contract, secondDayOfMonth, null, ContextVariable.PARTIAL_FACTOR, "0.750");

	Date startDate = firstDayOfMonth;
	Date endDate = getLastDayOfMonth(startDate);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramosBases = getTramosBases(connection, startDate,
		endDate, ccc, contract);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(2, datas.size());
	    Assert.assertEquals(firstDayOfMonth, datas.get(0).getStartDate());
	    Assert.assertEquals(firstDayOfMonth, datas.get(0).getEndDate());
	    Assert.assertEquals(secondDayOfMonth, datas.get(1).getStartDate());
	    Assert.assertEquals(endDate, datas.get(1).getEndDate());

	});
	;

	Assert.assertEquals(2, tramosBases.size());
	org.junit.Assert.assertEquals("01", tramosBases.get(0).getFechaDesde().getDia());
	org.junit.Assert.assertEquals("01", tramosBases.get(0).getFechaHasta().getDia());

	org.junit.Assert.assertEquals("02", tramosBases.get(1).getFechaDesde().getDia());
    }

    @Test
    public void testCretaL13() throws ExpressionException, SQLException, SalaryException, JAXBException, IOException,
	    EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "01");

	Date firstDayOfMonth = getFirstDayOfMonth(getToday());

	Date startDate = firstDayOfMonth;
	Date endDate = add(startDate, Calendar.DAY_OF_MONTH, 25);
	Date lastDayOfMonth = getLastDayOfMonth(startDate);
	int lastDay = get(lastDayOfMonth, Calendar.DAY_OF_MONTH);

	addData(aonContext, contract, add(endDate, Calendar.DAY_OF_MONTH, 1), lastDayOfMonth,
		new HashMap<String, String>() {
		    {
			put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", lastDay - 25));
		    }
		});

	addData(aonContext, contract, add(firstDayOfMonth, Calendar.MONTH, 1), null, new HashMap<String, String>() {
	    {
		put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 19));
	    }
	});
	// VACACIONES RETRIBUIDAS NO DISFRUTADAS
	addPayment(aonContext, contract, "DIAS VACACIONES NO DISFRUTADOS",
		"DIAS_VACACIONES_NO_DISFRUTADOS * ( 100.00 )", "_P", "_P", PaymentType.CRA_0006, SalaryType.SETTLE);

	ISQLContractSalaryCalculatorContext ctx = getSmartSQLContractSettleContext(connection, contract.getStartDate(),
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {
	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(2, datas.size());
	    Assert.assertEquals(add(endDate, Calendar.DAY_OF_MONTH, 1), datas.get(0).getStartDate());
	    Assert.assertEquals(lastDayOfMonth, datas.get(0).getEndDate());
	    Assert.assertEquals(add(lastDayOfMonth, Calendar.DAY_OF_MONTH, 1), datas.get(1).getStartDate());
	    Assert.assertEquals(add(lastDayOfMonth, Calendar.DAY_OF_MONTH, 19), datas.get(1).getEndDate());

	});
	;

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(endDate);
	String mes = Integer.toString(calendar.get(MONTH) + 1);
	String anho = Integer.toString(calendar.get(YEAR));

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	TrabajadoresTramos.generate(connection, "0000", // autorizado,
		mes, // desdeAnhoMes,
		anho, // desdeAnho,
		mes, // hastaMes,
		anho, // hastaAnho,
		mes, // ctrlMes,
		anho, // ctrlAnho,
		"L13", // tipo,
		new String[] { "0111" + "" + ccc }, // cccs
		trabajadoresTramosOs);

	trabajadoresTramosOs.flush();

	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
			trabajadoresTramosIs);

	trabajadoresTramosOs.close();
	trabajadoresTramosIs.close();

	Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacion.getPeriodoDesde().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(liquidacion.getPeriodoDesde().getMes()));
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacion.getPeriodoHasta().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(liquidacion.getPeriodoHasta().getMes()));

	org.junit.Assert.assertEquals(2, liquidacion.getLiquidacionMes().size());

	LiquidacionMes liquidacionMes = liquidacion.getLiquidacionMes().get(0);
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacionMes.getMesLiquidativo().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes),
		Integer.parseInt(liquidacionMes.getMesLiquidativo().getMes()));

	Trabajadores trabajadores = liquidacionMes.getTrabajadores();
	Trabajador trabajador = trabajadores.getTrabajador().get(0);
	org.junit.Assert.assertEquals(1, trabajador.getTramos().getTramo().size());

	Tramo tramo0 = trabajador.getTramos().getTramo().get(0);
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(tramo0.getFechaDesde().getMes()));
	org.junit.Assert.assertEquals(Integer.parseInt(anho), Integer.parseInt(tramo0.getFechaDesde().getAnho()));

	calendar.setTime(add(firstDayOfMonth, Calendar.MONTH, 1));
	String mes1 = Integer.toString(calendar.get(Calendar.MONTH) + 1);
	String anho1 = Integer.toString(calendar.get(Calendar.YEAR));

	liquidacionMes = liquidacion.getLiquidacionMes().get(1);
	org.junit.Assert.assertEquals(Integer.parseInt(anho1),
		Integer.parseInt(liquidacionMes.getMesLiquidativo().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes1),
		Integer.parseInt(liquidacionMes.getMesLiquidativo().getMes()));

	trabajadores = liquidacionMes.getTrabajadores();
	trabajador = trabajadores.getTrabajador().get(0);
	org.junit.Assert.assertEquals(1, trabajador.getTramos().getTramo().size());

	tramo0 = trabajador.getTramos().getTramo().get(0);
	org.junit.Assert.assertEquals(calendar.get(Calendar.MONTH) + 1,
		Integer.parseInt(tramo0.getFechaDesde().getMes()));
	org.junit.Assert.assertEquals(calendar.get(Calendar.YEAR), Integer.parseInt(tramo0.getFechaDesde().getAnho()));

	net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMes liquidacionMes1 = getLiquidacion(connection,
		trabajadoresTramos).getLiquidacionMes().get(1);

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo10 = liquidacionMes1.getTrabajadores().getTrabajador()
		.get(0).getTramos().getTramo().get(0);
	assertDato(tramo10.getDatosTramo().getDato(), "C", "500", "190000");
	assertDato(tramo10.getDatosTramo().getDato(), "C", "601", "190000");

    }

    @Test
    @Ignore("Not real")
    public void testCretaL13I() throws ExpressionException, SQLException, SalaryException, JAXBException, IOException,
	    EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	Date firstDayOfMonth = getFirstDayOfMonth(getToday());
	Date endDate = add(firstDayOfMonth, DAY_OF_MONTH, 18);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { new Extra() {
	    {
		this.expression = "1000.00";
		this.month = Month.DECEMBER;
		this.start = "01/01";
		this.end = "31/12";
		this.issue = "31/12";
	    }
	} });

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "01", CCCType.PRINCIPAL,
		getFirstDayOfYear(getToday()), null, category);

	addData(aonContext, contract, add(endDate, Calendar.DAY_OF_MONTH, 1), add(endDate, Calendar.DAY_OF_MONTH, 50),
		new HashMap<String, String>() {
		    {
			put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 50));
		    }
		});

	// VACACIONES RETRIBUIDAS NO DISFRUTADAS
	addPayment(aonContext, contract, "DIAS VACACIONES NO DISFRUTADOS",
		"DIAS_VACACIONES_NO_DISFRUTADOS * ( 100.00 )", "_P", "_P", PaymentType.CRA_0006, SalaryType.SETTLE);

	ISQLContractSalaryCalculatorContext ctx = getSmartSQLContractSettleContext(connection, contract.getStartDate(),
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {
	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(1, datas.size());

	    // Assert.assertEquals(getFirstDayOfYear(getToday()),
	    // datas.get(0).getStartDate());
	    // Assert.assertEquals(endDate, datas.get(0).getEndDate());

	    Assert.assertEquals(add(endDate, Calendar.DAY_OF_MONTH, 1), datas.get(0).getStartDate());
	    Assert.assertEquals(add(endDate, Calendar.DAY_OF_MONTH, 50), datas.get(0).getEndDate());

	});
	;

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(endDate);
	String mes = Integer.toString(calendar.get(MONTH) + 1);
	String anho = Integer.toString(calendar.get(YEAR));

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	TrabajadoresTramos.generate(connection, "0000", // autorizado,
		mes, // desdeAnhoMes,
		anho, // desdeAnho,
		mes, // hastaMes,
		anho, // hastaAnho,
		mes, // ctrlMes,
		anho, // ctrlAnho,
		"L13", // tipo,
		new String[] { "0111" + "" + ccc }, // cccs
		trabajadoresTramosOs);

	trabajadoresTramosOs.flush();

	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
			trabajadoresTramosIs);

	trabajadoresTramosOs.close();
	trabajadoresTramosIs.close();

	Utils.marshal(trabajadoresTramos, System.out);

	Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacion.getPeriodoDesde().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(liquidacion.getPeriodoDesde().getMes()));
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacion.getPeriodoHasta().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(liquidacion.getPeriodoHasta().getMes()));

	org.junit.Assert.assertEquals(1, liquidacion.getLiquidacionMes().size());

	LiquidacionMes liquidacionMes = liquidacion.getLiquidacionMes().get(0);
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacionMes.getMesLiquidativo().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes),
		Integer.parseInt(liquidacionMes.getMesLiquidativo().getMes()));

	Trabajadores trabajadores = liquidacionMes.getTrabajadores();
	Trabajador trabajador = trabajadores.getTrabajador().get(0);
	org.junit.Assert.assertEquals(1, trabajador.getTramos().getTramo().size());

	Tramo tramo0 = trabajador.getTramos().getTramo().get(0);
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(tramo0.getFechaDesde().getMes()));
	org.junit.Assert.assertEquals(Integer.parseInt(anho), Integer.parseInt(tramo0.getFechaDesde().getAnho()));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo bases1 = bases.get(0);
	assertDato(bases1.getDatosTramo().getDato(), "C", "500", "500000");
	assertDato(bases1.getDatosTramo().getDato(), "C", "601", "500000");

    }

    @Test
    public void testCretaL13II() throws ExpressionException, SQLException, SalaryException, JAXBException, IOException,
	    EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "01");

	Date firstDayOfMonth = getFirstDayOfMonth(getToday());

	Date startDate = firstDayOfMonth;
	// Date endDate = add(startDate, Calendar.DAY_OF_MONTH, 25 );
	Date lastDayOfMonth = getLastDayOfMonth(startDate);
	int lastDay = get(lastDayOfMonth, Calendar.DAY_OF_MONTH);

	addData(aonContext, contract, add(firstDayOfMonth, Calendar.MONTH, 1), null, new HashMap<String, String>() {
	    {
		put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 19));
	    }
	});
	// VACACIONES RETRIBUIDAS NO DISFRUTADAS
	addPayment(aonContext, contract, "DIAS VACACIONES NO DISFRUTADOS",
		"DIAS_VACACIONES_NO_DISFRUTADOS * ( 100.00 )", "_P", "_P", PaymentType.CRA_0006, SalaryType.SETTLE);

	ISQLContractSalaryCalculatorContext ctx = getSmartSQLContractSettleContext(connection, contract.getStartDate(),
		lastDayOfMonth, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {
	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(1, datas.size());
	    Assert.assertEquals(add(lastDayOfMonth, Calendar.DAY_OF_MONTH, 1), datas.get(0).getStartDate());
	    Assert.assertEquals(add(lastDayOfMonth, Calendar.DAY_OF_MONTH, 19), datas.get(0).getEndDate());

	});
	;

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(lastDayOfMonth);
	String mes = Integer.toString(calendar.get(MONTH) + 1);
	String anho = Integer.toString(calendar.get(YEAR));

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();

	TrabajadoresTramos.generate(connection, "0000", // autorizado,
		mes, // desdeAnhoMes,
		anho, // desdeAnho,
		mes, // hastaMes,
		anho, // hastaAnho,
		mes, // ctrlMes,
		anho, // ctrlAnho,
		"L13", // tipo,
		new String[] { "0111" + "" + ccc }, // cccs
		trabajadoresTramosOs);

	trabajadoresTramosOs.flush();

	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
			trabajadoresTramosIs);

	trabajadoresTramosIs.close();
	trabajadoresTramosOs.close();

	Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacion.getPeriodoDesde().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(liquidacion.getPeriodoDesde().getMes()));
	org.junit.Assert.assertEquals(Integer.parseInt(anho),
		Integer.parseInt(liquidacion.getPeriodoHasta().getAnho()));
	org.junit.Assert.assertEquals(Integer.parseInt(mes), Integer.parseInt(liquidacion.getPeriodoHasta().getMes()));

	org.junit.Assert.assertEquals(1, liquidacion.getLiquidacionMes().size());

	LiquidacionMes liquidacionMes = liquidacion.getLiquidacionMes().get(0);
	int year = Integer.parseInt(anho);
	int month = (Integer.parseInt(mes) + 1);
	if (month > 12) {
	    year++;
	    month = month % 12;
	}

	org.junit.Assert.assertEquals(year, Integer.parseInt(liquidacionMes.getMesLiquidativo().getAnho()));
	org.junit.Assert.assertEquals(month, Integer.parseInt(liquidacionMes.getMesLiquidativo().getMes()));

	Trabajadores trabajadores = liquidacionMes.getTrabajadores();
	Trabajador trabajador = trabajadores.getTrabajador().get(0);
	org.junit.Assert.assertEquals(1, trabajador.getTramos().getTramo().size());

	Tramo tramo1 = trabajador.getTramos().getTramo().get(0);
	calendar.setTime(add(firstDayOfMonth, Calendar.MONTH, 1));
	org.junit.Assert.assertEquals(calendar.get(Calendar.MONTH) + 1,
		Integer.parseInt(tramo1.getFechaDesde().getMes()));
	org.junit.Assert.assertEquals(calendar.get(Calendar.YEAR), Integer.parseInt(tramo1.getFechaDesde().getAnho()));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo bases2 = bases.get(0);
	assertDato(bases2.getDatosTramo().getDato(), "C", "500", "190000");
	assertDato(bases2.getDatosTramo().getDato(), "C", "601", "190000");

    }

    @Test
    public void testCretaITMaternityWithBonusPeriod() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "04");
		//@formatter:on

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				getFirstDayOfYear(startDate), 
				null, 
				null/*1750.00/30*/);
		//@formatter:on

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(1, datas.size());

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	});
	;

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);
	org.junit.Assert.assertEquals(1, bases.size());
	org.junit.Assert.assertEquals(1, Integer.parseInt(bases.get(0).getFechaDesde().getDia()));
	org.junit.Assert.assertEquals(get(endDate, DAY_OF_MONTH),
		Integer.parseInt(bases.get(0).getFechaHasta().getDia()));
	org.junit.Assert.assertEquals(175000,
		Integer.parseInt(bases.get(0).getDatosTramo().getDato().get(0).getValor()));

	Trabajador trabajador = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores()
		.getTrabajador().get(0);
	List<Tramo> tramos = trabajador.getTramos().getTramo();
	Tramo tramo = tramos.get(0);
	Tramo bonusTramo = clone(tramo);
	tramo.getFechaHasta().setDia("09");
	bonusTramo.getFechaDesde().setDia("10");
	tramos.add(bonusTramo);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bonusBases = getBases(connection, trabajadoresTramos);
	org.junit.Assert.assertEquals(2, bonusBases.size());

	org.junit.Assert.assertEquals(1, Integer.parseInt(bonusBases.get(0).getFechaDesde().getDia()));
	org.junit.Assert.assertEquals(9, Integer.parseInt(bonusBases.get(0).getFechaHasta().getDia()));

	org.junit.Assert.assertEquals(10, Integer.parseInt(bonusBases.get(1).getFechaDesde().getDia()));
	org.junit.Assert.assertEquals(get(endDate, DAY_OF_MONTH),
		Integer.parseInt(bonusBases.get(1).getFechaHasta().getDia()));

	org.junit.Assert.assertEquals(Math.round(175000.00 * 9 / get(endDate, DAY_OF_MONTH)),
		Integer.parseInt(bonusBases.get(0).getDatosTramo().getDato().get(0).getValor()));

	org.junit.Assert.assertEquals(
		Math.round(175000.00 * (get(endDate, DAY_OF_MONTH) - 9) / get(endDate, DAY_OF_MONTH)),
		Integer.parseInt(bonusBases.get(1).getDatosTramo().getDato().get(1).getValor()));
    }

    @Test
    public void testCretaITMaternityPartialI() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "04");
		//@formatter:on

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				getFirstDayOfYear(startDate), 
				null, 
				null/*1750.00/30*/);
		addData(aonContext, 
				contract, 
				getFirstDayOfYear(startDate), 
				null, 
				ContextVariable.MATERNITY_FACTOR,
				"0.50");
		//@formatter:on

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(1, datas.size());

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(endDate, datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00 * 0.50, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	});
	;

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);
	org.junit.Assert.assertEquals(1, bases.size());
	org.junit.Assert.assertEquals(1, Integer.parseInt(bases.get(0).getFechaDesde().getDia()));
	org.junit.Assert.assertEquals(get(endDate, DAY_OF_MONTH),
		Integer.parseInt(bases.get(0).getFechaHasta().getDia()));

	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "500", "87500");
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "601", "87500");
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "535", "87500");
	assertDato(bases.get(0).getDatosTramo().getDato(), "C", "635", "87500");

	assertDato(bases.get(0).getDatosTramo().getDato(), "H", "01", (valor) -> Integer.parseInt(valor) > 0.00);

    }

    @Test
    public void testCretaITMaternityPartialII() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "04");
		//@formatter:on

	addSystemData(aonContext, contract.getStartDate(), null, new HashMap<String, String>() {
	    {
		put("POR_HORAS", "def () { isdef CONTEXT ? UTILIZADA('HORAS_TRABAJADAS') : FALSO() }");
		put("HORAS_NOMINA",
			"MAX(1,FLOOR([\"04\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) / 6.33 * COEFICIENTE_TRABAJADO)][GRUPO_COTIZACION]))");
	    }
	});

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date startIDate = add(startDate, Calendar.DAY_OF_MONTH, 10);

	//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				startIDate, 
				null, 
				null/*1750.00/30*/);
		addData(aonContext, 
				contract, 
				startIDate, 
				null, 
				ContextVariable.MATERNITY_FACTOR,
				"0.50");
		//@formatter:on

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(2, datas.size());

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(add(startIDate, Calendar.DAY_OF_MONTH, -1), datas.get(0).getEndDate());
	    Assert.assertEquals(1750.00 * 10 / 30, Double.parseDouble(datas.get(0).getExpression()), DELTA);

	});
	;

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");

	Trabajador trabajador = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores()
		.getTrabajador().get(0);
	assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));
	assertTramoMaternidadTiempoParcial(trabajador.getTramos().getTramo().get(1));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);
	org.junit.Assert.assertEquals(2, bases.size());
	org.junit.Assert.assertEquals(1, Integer.parseInt(bases.get(0).getFechaDesde().getDia()));
	// org.junit.Assert.assertEquals( get(endDate, DAY_OF_MONTH),
	// Integer.parseInt(bases.get(0).getFechaHasta().getDia()));

	// assertDato(bases.get(0).getDatosTramo().getDato(), "C", "500", 1750.00 * 10
	// /30 * 100.00 );
	// assertDato(bases.get(0).getDatosTramo().getDato(), "C", "601", 1750.00 * 10
	// /30 * 100.00);
	// assertDato(bases.get(0).getDatosTramo().getDato(), "C", "535", 1750.00 * 10
	// /30 * 100.00);
	// assertDato(bases.get(0).getDatosTramo().getDato(), "C", "635", 1750.00 * 10
	// /30 * 100.00);

	assertDato(bases.get(1).getDatosTramo().getDato(), "H", "01", (valor) -> Integer.parseInt(valor) > 0.00);

    }

    @Test
    public void testCretaITPartialI() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200, "04", startDate, add(endDate, DAY_OF_MONTH,-5));
		//@formatter:on

	addSystemData(aonContext, contract.getStartDate(), null, new HashMap<String, String>() {
	    {
		put("POR_HORAS", "def () { isdef CONTEXT ? UTILIZADA('HORAS_TRABAJADAS') : FALSO() }");
		put("HORAS_NOMINA",
			"MAX(1,FLOOR([\"04\":(POR_HORAS() ? HORAS_TRABAJADAS : 1125.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) / 6.78 * COEFICIENTE_TRABAJADO)][GRUPO_COTIZACION]))");
		put("BASE_CGC_MIN",
			"[ \"04\":(MAX(6.78, (POR_HORAS() ? 6.78 * HORAS_NOMINA : 1125.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD)))] [GRUPO_COTIZACION]");
	    }
	});

	int dayOfWeek = AonDateUtils.get(startDate, Calendar.DAY_OF_WEEK);

	addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
		WEEK_HOURS_VARIABLES.get(dayOfWeek), 8.00);

	Date startITDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
	Date endITDate = contract.getEndDate();

	//@formatter:off
		addIT(aonContext, 
				contract, 
				OCCUPATIONAL_DISEASE, 
				startITDate, 
				endITDate, 
				null/*1750.00/30*/);
		//@formatter:on

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId())).forEach(salary -> {

	    int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

	    // 500 Base de contingencias comunes.
	    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
	    Assert.assertEquals(2, datas.size());

	    Assert.assertEquals(startDate, datas.get(0).getStartDate());
	    Assert.assertEquals(add(startITDate, Calendar.DAY_OF_MONTH, -1), datas.get(0).getEndDate());

	});
	;

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");

	Trabajador trabajador = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores()
		.getTrabajador().get(0);
	assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));
	assertTramoITATEPPagoDelegado(trabajador.getTramos().getTramo().get(1));

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, trabajadoresTramos);
	org.junit.Assert.assertEquals(2, bases.size());
	org.junit.Assert.assertEquals(1, Integer.parseInt(bases.get(0).getFechaDesde().getDia()));
	// org.junit.Assert.assertEquals( get(endDate, DAY_OF_MONTH),
	// Integer.parseInt(bases.get(0).getFechaHasta().getDia()));

	// assertDato(bases.get(0).getDatosTramo().getDato(), "C", "500", (valor) ->
	// Integer.parseInt(valor) == (1750.00 * (8 / 40) /30) );

	assertDato(bases.get(0).getDatosTramo().getDato(), "H", "01", (valor) -> Integer.parseInt(valor) == 1.00);

    }

    @Test
    public void testCretaRegimenArtistasNormal() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "01", CCCType.ARTIST);

	Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, Calendar.JULY - get(getToday(), MONTH)));
	Date endDate = getLastDayOfMonth(startDate);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		    // 300 Percepciones íntegras.
		    List<ContextData> datas = salary.getContextData().get(TOTAL_PAYMENT.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00, Double.parseDouble(datas.get(0).getExpression()));

		    // 501 Base de Horas Extras Fuerza Mayor
		    datas = salary.getContextData().get(STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		    // 502 Base de Horas Extras
		    datas = salary.getContextData().get(NON_STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
	Assert.assertEquals(1, tramos.size());

	Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaDesde().getMes());
	Assert.assertEquals("31", tramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaHasta().getMes());
	Assert.assertEquals("30", tramos.get(0).getDiasCotizados());
	assertTramoActivoNormalArtistas(tramos.get(0));

	cleanSalaries(aonContext);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);
	Assert.assertEquals(1, bases.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = bases.get(0);
	Assert.assertEquals("01", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo1.getFechaDesde().getMes());
	Assert.assertEquals("31", tramo1.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo1.getFechaHasta().getMes());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "300", "175000");

    }

    @Test
    public void testCretaERTEAndITNoAdjust1DayMonthly() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C501, "04");
//		for ( ContextVariable var : new ContextVariable[] {
//				ContextVariable.MONDAY_HOURS,
//				ContextVariable.TUESDAY_HOURS,
//				ContextVariable.WEDNESDAY_HOURS,
//				ContextVariable.THURSDAY_HOURS,
//				ContextVariable.FRIDAY_HOURS,
//				ContextVariable.SATURDAY_HOURS,
//		} ) {
//				setData(aonContext, contract, var.getName(), "3.33");
//		}
	setData(aonContext, contract, ContextVariable.PARTIAL_FACTOR.getName(), "0.5");

	Date startERE = getLastDayOfMonth(add(getToday(), MONTH, Calendar.JULY - get(getToday(), MONTH)));

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR, "1.0");

	Date startIT = add(startERE, Calendar.DAY_OF_MONTH, -1);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, startIT, null);

	Date startDate = getFirstDayOfMonth(startERE);
	Date endDate = getLastDayOfMonth(startDate);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    Date endActive = add(startIT, DATE, -1);

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		    // 500 Base de contingencias comunes.
		    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());

		    Assert.assertEquals(2, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30 * 29, Double.parseDouble(datas.get(0).getExpression()), DELTA);
		    Assert.assertEquals(startIT, datas.get(1).getStartDate());
		    Assert.assertEquals(startIT, datas.get(1).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30.00, Double.parseDouble(datas.get(1).getExpression()), DELTA);

		    // 601 o 611 Base de Accidentes de Trabajo.
		    datas = salary.getContextData().get(CGP_BASE.getName());
		    Assert.assertEquals(2, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30 * 29, Double.parseDouble(datas.get(0).getExpression()), DELTA);
		    Assert.assertEquals(startIT, datas.get(1).getStartDate());
		    Assert.assertEquals(startIT, datas.get(1).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30.00, Double.parseDouble(datas.get(1).getExpression()), DELTA);

		    //
		    datas = salary.getContextData().get(ERE_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(endDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    // Assert.assertEquals(( 1750/2.00) / 30.00,
		    // Double.parseDouble(datas.get(0).getExpression()), DELTA);

		    // 501 Base de Horas Extras Fuerza Mayor
		    datas = salary.getContextData().get(STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		    // 502 Base de Horas Extras
		    datas = salary.getContextData().get(NON_STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
	Assert.assertEquals(3, tramos.size());

	Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaDesde().getMes());
	Assert.assertEquals("29", tramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaHasta().getMes());
	Assert.assertEquals("29", tramos.get(0).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tramos.get(0));

	Assert.assertEquals("30", tramos.get(1).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(1).getFechaDesde().getMes());
	Assert.assertEquals("30", tramos.get(1).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(1).getFechaHasta().getMes());
	Assert.assertEquals("1", tramos.get(1).getDiasCotizados());
	assertTramoIT15PrimerosDias(tramos.get(1));

	Assert.assertEquals("31", tramos.get(2).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(2).getFechaDesde().getMes());
	Assert.assertEquals("31", tramos.get(2).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(2).getFechaHasta().getMes());
	Assert.assertEquals("1", tramos.get(2).getDiasCotizados());
	assertTramoExpedienteRegulacionEmpleoTotal(tramos.get(2));

	cleanSalaries(aonContext);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);
	Assert.assertEquals(3, bases.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = bases.get(0);
	Assert.assertEquals("01", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo1.getFechaDesde().getMes());
	Assert.assertEquals("29", tramo1.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo1.getFechaHasta().getMes());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500", "84583");
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601", "84583");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo2 = bases.get(1);
	Assert.assertEquals("30", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo2.getFechaDesde().getMes());
	Assert.assertEquals("30", tramo2.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo2.getFechaHasta().getMes());
	assertDato(tramo2.getDatosTramo().getDato(), "C", "500", "2917");
	assertDato(tramo2.getDatosTramo().getDato(), "C", "603", "2917");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo3 = bases.get(2);
	Assert.assertEquals("31", tramo3.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo3.getFechaDesde().getMes());
	Assert.assertEquals("31", tramo3.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo3.getFechaHasta().getMes());
	assertDato(tramo3.getDatosTramo().getDato(), "C", "509", "2917");
	assertDato(tramo3.getDatosTramo().getDato(), "C", "603", "2917");

    }

    @Test
    public void testCretaERTEAndITNoAdjust1DayMonthlyII() throws ExpressionException, SQLException, SalaryException,
	    JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C501, "04");
//		for ( ContextVariable var : new ContextVariable[] {
//				ContextVariable.MONDAY_HOURS,
//				ContextVariable.TUESDAY_HOURS,
//				ContextVariable.WEDNESDAY_HOURS,
//				ContextVariable.THURSDAY_HOURS,
//				ContextVariable.FRIDAY_HOURS,
//				ContextVariable.SATURDAY_HOURS,
//		} ) {
//				setData(aonContext, contract, var.getName(), "3.33");
//		}
	setData(aonContext, contract, ContextVariable.PARTIAL_FACTOR.getName(), "0.5");

	Date startERE = getLastDayOfMonth(add(getToday(), MONTH, Calendar.JULY - get(getToday(), MONTH)));

	addData(aonContext, contract, startERE, null, ContextVariable.ERE_FACTOR, "0.5");

	Date startIT = add(startERE, Calendar.DAY_OF_MONTH, -1);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, startIT, null);

	Date startDate = getFirstDayOfMonth(startERE);
	Date endDate = getLastDayOfMonth(startDate);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	AON.getSalaryData(aonContext,
		props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
		.forEach(salary -> {

		    Date endActive = add(startIT, DATE, -1);

		    for (Entry<String, List<ContextData>> entry : salary.getContextData().entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (ContextData data : entry.getValue())
			    System.out.print(
				    data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate() + "),");
			System.out.println();
		    }

		    // 500 Base de contingencias comunes.
		    List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());

		    Assert.assertEquals(3, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30 * 29, Double.parseDouble(datas.get(0).getExpression()), DELTA);
		    Assert.assertEquals(startIT, datas.get(1).getStartDate());
		    Assert.assertEquals(startIT, datas.get(1).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30.00, Double.parseDouble(datas.get(1).getExpression()), DELTA);
		    Assert.assertEquals(startERE, datas.get(2).getStartDate());
		    Assert.assertEquals(startERE, datas.get(2).getEndDate());
		    Assert.assertEquals(1750.00 / 4 / 30.00, Double.parseDouble(datas.get(2).getExpression()), DELTA);

		    // 601 o 611 Base de Accidentes de Trabajo.
		    datas = salary.getContextData().get(CGP_BASE.getName());
		    Assert.assertEquals(3, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endActive, datas.get(0).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30 * 29, Double.parseDouble(datas.get(0).getExpression()), DELTA);
		    Assert.assertEquals(startIT, datas.get(1).getStartDate());
		    Assert.assertEquals(startIT, datas.get(1).getEndDate());
		    Assert.assertEquals(1750.00 / 2 / 30.00, Double.parseDouble(datas.get(1).getExpression()), DELTA);
		    Assert.assertEquals(startERE, datas.get(2).getStartDate());
		    Assert.assertEquals(startERE, datas.get(2).getEndDate());
		    Assert.assertEquals(1750.00 / 4 / 30.00, Double.parseDouble(datas.get(2).getExpression()), DELTA);

		    //
		    datas = salary.getContextData().get(ERE_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(endDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals((1750 / 4.00) / 30.00, Double.parseDouble(datas.get(0).getExpression()), DELTA);

		    // 501 Base de Horas Extras Fuerza Mayor
		    datas = salary.getContextData().get(STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		    // 502 Base de Horas Extras
		    datas = salary.getContextData().get(NON_STRUCTURAL_OVERTIME_BASE.getName());
		    Assert.assertEquals(1, datas.size());
		    Assert.assertEquals(startDate, datas.get(0).getStartDate());
		    Assert.assertEquals(endDate, datas.get(0).getEndDate());
		    Assert.assertEquals(0.00, Double.parseDouble(datas.get(0).getExpression()));

		});
	;

	cleanSalaries(aonContext);

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
	Assert.assertEquals(3, tramos.size());

	Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaDesde().getMes());
	Assert.assertEquals("29", tramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(0).getFechaHasta().getMes());
	Assert.assertEquals("29", tramos.get(0).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tramos.get(0));

	Assert.assertEquals("30", tramos.get(1).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(1).getFechaDesde().getMes());
	Assert.assertEquals("30", tramos.get(1).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(1).getFechaHasta().getMes());
	Assert.assertEquals("1", tramos.get(1).getDiasCotizados());
	assertTramoIT15PrimerosDias(tramos.get(1));

	Assert.assertEquals("31", tramos.get(2).getFechaDesde().getDia());
	Assert.assertEquals("07", tramos.get(2).getFechaDesde().getMes());
	Assert.assertEquals("31", tramos.get(2).getFechaHasta().getDia());
	Assert.assertEquals("07", tramos.get(2).getFechaHasta().getMes());
	Assert.assertEquals("1", tramos.get(2).getDiasCotizados());
	assertTramoExpedienteRegulacionEmpleoParcial(tramos.get(2));

	cleanSalaries(aonContext);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getTramosBases(connection, startDate, endDate,
		ccc, contract);
	Assert.assertEquals(3, bases.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = bases.get(0);
	Assert.assertEquals("01", tramo1.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo1.getFechaDesde().getMes());
	Assert.assertEquals("29", tramo1.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo1.getFechaHasta().getMes());
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500", "84583");
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601", "84583");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo2 = bases.get(1);
	Assert.assertEquals("30", tramo2.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo2.getFechaDesde().getMes());
	Assert.assertEquals("30", tramo2.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo2.getFechaHasta().getMes());
	assertDato(tramo2.getDatosTramo().getDato(), "C", "500", "2917");
	assertDato(tramo2.getDatosTramo().getDato(), "C", "603", "2917");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo3 = bases.get(2);
	Assert.assertEquals("31", tramo3.getFechaDesde().getDia());
	Assert.assertEquals("07", tramo3.getFechaDesde().getMes());
	Assert.assertEquals("31", tramo3.getFechaHasta().getDia());
	Assert.assertEquals("07", tramo3.getFechaHasta().getMes());
	assertDato(tramo3.getDatosTramo().getDato(), "C", "500", "1458");
	assertDato(tramo3.getDatosTramo().getDato(), "C", "601", "1458");
	assertDato(tramo3.getDatosTramo().getDato(), "C", "536", "1458");
	assertDato(tramo3.getDatosTramo().getDato(), "C", "636", "1458");
	assertDato(tramo3.getDatosTramo().getDato(), "H", "05", "500");

    }

    @Test
    public void testCretaDelayIT() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = UUID.randomUUID().toString().substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "04");

	Date startDate = getFirstDayOfYear(getToday());
	Date endDate = getLastDayOfMonth(startDate);

	Date startITDate = add(startDate, DAY_OF_MONTH, 8);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	calculateAndSave(connection, ctx);

	addPayment(aonContext, contract, "30.00*DIAS_TRABAJADOS/DIAS_MES");

	Criteria criteria = new Criteria();
	criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
	SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, startDate,
		endDate, endDate, criteria);
	delayCtx.next();

	int salaries = calculateAndSave(connection, delayCtx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(startDate);
	String mes = Integer.toString(calendar.get(MONTH) + 1);
	String anho = Integer.toString(calendar.get(YEAR));

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	TrabajadoresTramos.generate(connection, "0000", // autorizado,
		mes, // desdeAnhoMes,
		anho, // desdeAnho,
		mes, // hastaMes,
		anho, // hastaAnho,
		mes, // ctrlMes,
		anho, // ctrlAnho,
		"L90", // tipo,
		new String[] { "0111" + "" + ccc }, // cccs
		trabajadoresTramosOs);
	trabajadoresTramosOs.flush();

	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
			trabajadoresTramosIs);

	Utils.marshal(trabajadoresTramos, System.out);

	trabajadoresTramosIs.close();
	trabajadoresTramosOs.close();

	List<Tramo> tTramos = trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores()
		.getTrabajador().get(0).getTramos().getTramo();

	Assert.assertEquals(4, tTramos.size());

	Assert.assertEquals("01", tTramos.get(0).getFechaDesde().getDia());
	Assert.assertEquals("08", tTramos.get(0).getFechaHasta().getDia());
	Assert.assertEquals("8", tTramos.get(0).getDiasCotizados());
	assertTramoActivoNormalTiempoCompleto(tTramos.get(0));

	Assert.assertEquals("09", tTramos.get(1).getFechaDesde().getDia());
	Assert.assertEquals("23", tTramos.get(1).getFechaHasta().getDia());
	// Assert.assertEquals("15", tramos.get(1).getDiasCotizados());
	assertTramoIT15PrimerosDias(tTramos.get(1));

	Assert.assertEquals("24", tTramos.get(2).getFechaDesde().getDia());
	Assert.assertEquals("28", tTramos.get(2).getFechaHasta().getDia());
	// Assert.assertEquals("5", tramos.get(2).getDiasCotizados());
	assertTramoITPagoDelegado(tTramos.get(2));

	Assert.assertEquals("29", tTramos.get(3).getFechaDesde().getDia());
	assertTramoITPagoDelegado(tTramos.get(3));

	trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	ByteArrayOutputStream basesOs = new ByteArrayOutputStream();

	Bases.generate(connection, true, // comments,
		false, // skipExisting,
		false, // acceptPrevBases,
		null, // nafs,
		new String[] {}, // defaultsValues,
		trabajadoresTramosIs, null, // respuestaIs,
		basesOs, // os,
		new Bases.BasesCallback[] { Bases.L03BASESCALLBACK } // cbs
	);

	System.out.println(basesOs.toString());

	ByteArrayInputStream basesIs = new ByteArrayInputStream(basesOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases.class, basesIs);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bTramos = bases.getLiquidacion().get(0)
		.getLiquidacionMes().get(0).getTrabajadores().getTrabajador().get(0).getTramos().getTramo();

	Assert.assertEquals(4, bTramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = bTramos.get(0);
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500", "800");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601", "800");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = bTramos.get(1);
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500", "1500");
	assertDato(tramo1.getDatosTramo().getDato(), "C", "603", "1500");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo2 = bTramos.get(2);
	assertDato(tramo2.getDatosTramo().getDato(), "C", "500", "500");
	assertDato(tramo2.getDatosTramo().getDato(), "C", "603", "500");
//		assertDato(tramo2.getDatosTramo().getDato(), "C", "563", "375");

//		net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo3 = bTramos.get(3);

    }

    @Test
    public void testDelaysOverrideIT() throws ExpressionException, SQLException, SalaryException, JAXBException,
	    IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	String ccc = "20" + Long.toString(Math.abs(new Random().nextLong()), 10).substring(0, 9);
	//@formatter:off
		Date startContractDate = add(getFirstDayOfYear(getToday()),Calendar.YEAR, -1);
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "04",CCCType.PRINCIPAL, startContractDate, null, null, Long.toString(Math.abs(new Random().nextLong()), 10).substring(0,10), Long.toString(Math.abs(new Random().nextLong()), 10).substring(0,12));
		//@formatter:on
	PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
	addPayment(aonContext, contract, salarioBase, String.format("1250.00 * %s / %s", WORKED_DAYS, MONTH_DAYS),
		"_P");

	PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
	addPayment(aonContext, contract, prestIT, String.format("BASE_REGULADORA * 0.00 * %s_1_3", COMMON_DISEASE_DAYS),
		String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS));

	Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, -5);
	Date endDate = getLastDayOfMonth(startDate);

	Date itStartDate = add(startDate, DAY_OF_MONTH, 4);
	Date itEndDate = add(itStartDate, DAY_OF_MONTH, 1);
	addIT(aonContext, contract, LeaveType.COMMON_DISEASE, itStartDate, itEndDate, null);

	{
	    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		    endDate, contract);
	    SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
	    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
	    calculator.setSalaryBuilder(jooqSalaryBuilder);
	    calculator.calculate(ctx);
	    jooqSalaryBuilder.execute();
	}

	addData(aonContext, contract, itStartDate, itEndDate, "ATRASO", "10.00");
	{
	    Criteria criteria = new Criteria();
	    criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
	    SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, startDate,
		    endDate, endDate, criteria);
	    delayCtx.next();
	    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
	    SmartContractSalaryCalculator<ISalary> delayCalculator = new SmartContractSalaryCalculator<ISalary>();
	    delayCalculator.setSalaryBuilder(jooqSalaryBuilder);
	    delayCalculator.calculate(delayCtx);
	    jooqSalaryBuilder.execute();
	}

	addData(aonContext, contract, startDate, endDate, "ATRASO",
		Integer.toString(get(endDate, Calendar.DAY_OF_MONTH) - 2));
	{
	    Criteria criteria = new Criteria();
	    criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
	    SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, startDate,
		    endDate, getLastDayOfMonth(getToday()), criteria);
	    delayCtx.next();
	    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
	    SmartContractSalaryCalculator<ISalary> delayCalculator = new SmartContractSalaryCalculator<ISalary>();
	    delayCalculator.setSalaryBuilder(jooqSalaryBuilder);
	    delayCalculator.calculate(delayCtx);
	    jooqSalaryBuilder.execute();
	}

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(startDate);
	int anho = calendar.get(YEAR);
	int mes = calendar.get(MONTH);
	java.time.Month month = java.time.Month.values()[mes];

	Salary salary = AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId())).findAny().get();

	TrabajadorBuilder trabajadorBuilder = new TrabajadorBuilder();
	trabajadorBuilder.setNaf(salary.getEmployeeSSNumber());
	trabajadorBuilder.setTipoIpf(TipoIpf.DNI);
	trabajadorBuilder.setNumeroIpf(salary.getEmployeeDocument());
	trabajadorBuilder.setName(salary.getEmployeeName());
	trabajadorBuilder.addTramo(new TramoBuilder().setAnhoDesde(anho).setMesDesde(month).setDiaDesde(1)
		.setAnhoHasta(anho).setMesHasta(month).setDiaHasta(4).setDiasCotizados(4).setGrupoCotizacion(4)
		.setTipoDeContrato("100")
		.addDato(new DatoSolicitadoBuilder().setTipo("I").setCodigo("51").setObligatorio(true).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("500").setObligatorio(false).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("501").setObligatorio(false).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("502").setObligatorio(false).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("601").setObligatorio(false).create())
		.create());
	trabajadorBuilder.addTramo(new TramoBuilder().setAnhoDesde(anho).setMesDesde(month).setDiaDesde(5)
		.setAnhoHasta(anho).setMesHasta(month).setDiaHasta(6).setDiasCotizados(2).setGrupoCotizacion(6)
		.setTipoDeContrato("100")
		.addDato(new DatoSolicitadoBuilder().setTipo("I").setCodigo("51").setObligatorio(true).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("500").setObligatorio(false).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("603").setObligatorio(false).create())
		.create());
	trabajadorBuilder.addTramo(new TramoBuilder().setAnhoDesde(anho).setMesDesde(month).setDiaDesde(7)
		.setAnhoHasta(anho).setMesHasta(month).setDiaHasta(get(endDate, DAY_OF_MONTH))
		.setDiasCotizados(get(endDate, DAY_OF_MONTH) - 7).setGrupoCotizacion(4).setTipoDeContrato("100")
		.addDato(new DatoSolicitadoBuilder().setTipo("I").setCodigo("51").setObligatorio(true).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("500").setObligatorio(false).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("501").setObligatorio(false).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("502").setObligatorio(false).create())
		.addDato(new DatoSolicitadoBuilder().setTipo("C").setCodigo("601").setObligatorio(false).create())
		.create());

	LiquidacionMesBuilder liquidacionMesBuilder = new LiquidacionMesBuilder();
	liquidacionMesBuilder.setAnho(anho);
	liquidacionMesBuilder.setMes(month);
	liquidacionMesBuilder.add(trabajadorBuilder.create());

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = new TrabajadoresTramosBuilder()
		.setCCC("0111" + ccc).setTipo("L03").setAnhoDesde(anho).setMesDesde(month).setAnhoHasta(anho)
		.setMesHasta(month).setAnhoControl(anho).setMesControl(month).setAutorizado(277229)
		.addLiquidacionMes(liquidacionMesBuilder.create()).create();

	// Utils.marshal(trabajadoresTramos, System.out);

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	Utils.marshal(trabajadoresTramos, trabajadoresTramosOs);
	trabajadoresTramosOs.flush();
	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	ByteArrayOutputStream basesOs = new ByteArrayOutputStream();

	Bases.generate(connection, true, // comments,
		false, // skipExisting,
		false, // acceptPrevBases,
		null, // nafs,
		new String[] {}, // defaultsValues,
		trabajadoresTramosIs, null, // respuestaIs,
		basesOs, // os,
		new Bases.BasesCallback[] { // cbs
			Bases.L03BASESCALLBACK });
	basesOs.flush();

	ByteArrayInputStream basesIs = new ByteArrayInputStream(basesOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases.class, basesIs);

	// Utils.marshal(bases, System.out);

	List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> tramos = bases.getLiquidacion().get(0)
		.getLiquidacionMes().get(0).getTrabajadores().getTrabajador().get(0).getTramos().getTramo();

	Assert.assertEquals(2, tramos.size());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo0 = tramos.get(0);
	assertDato(tramo0.getDatosTramo().getDato(), "C", "500", "400");
	assertDato(tramo0.getDatosTramo().getDato(), "C", "601", "400");

	net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = tramos.get(1);
	assertDato(tramo1.getDatosTramo().getDato(), "C", "500",
		Integer.toString(100 * (get(endDate, Calendar.DAY_OF_MONTH) - 6)));
	assertDato(tramo1.getDatosTramo().getDato(), "C", "601",
		Integer.toString(100 * (get(endDate, Calendar.DAY_OF_MONTH) - 6)));

    }

    @Test
    public void testCretaA999() throws ExpressionException, SQLException, SalaryException, JAXBException, IOException,
	    EmptyBasesException, XMLStreamException, FactoryConfigurationError, SAXException {

	Locale.setDefault(new Locale("es", "ES"));
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Respuesta respuesta = new Respuesta();
	respuesta.setAutorizado("163103333");
	respuesta.setReferenciaExterna("66666666");

	net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion liquidacion = new net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion();
	liquidacion.setTipo("L00");
	liquidacion.setCcc(getCtaCot(ccc));
	liquidacion.setPeriodoDesde(getPerido(startDate));
	liquidacion.setPeriodoHasta(getPerido(endDate));
	liquidacion.setFechaHoraRecaudacion(getFechaHoraRecaudacion(startDate));
	Errores errores = getErrores("A9999");
	liquidacion.setErrores(errores);
	respuesta.getLiquidacion().add(liquidacion);

	ByteArrayOutputStream respuestaOs = new ByteArrayOutputStream();
	Utils.marshal(respuesta, respuestaOs);
	Utils.marshal(respuesta, System.out);

	ByteArrayInputStream respuestaIs = new ByteArrayInputStream(respuestaOs.toByteArray());

	ByteArrayOutputStream basesOs = new ByteArrayOutputStream();
	try {
	    Bases.generate(connection, true, // comments,
		    false, // skipExisting,
		    false, // acceptPrevBases,
		    null, // nafs,
		    new String[] {}, // defaultsValues,
		    null, // trabajadoresTramosIs
		    respuestaIs, basesOs, // os,
		    new Bases.BasesCallback[] { new Bases.BasesCallback() {
			public void invalidLiquidacion(
				net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion, Exception e) {
			    System.out.println(e.getLocalizedMessage());
			    try {
				Utils.marshal(liquidacion, System.out);
			    } catch (JAXBException e1) {
			    }
			};
		    } });
	} catch (EmptyBasesException e) {
	    return;
	}

	org.junit.Assert.fail("EmptyBasesException expected");

    }

    @Test
    public void testCretaTrabajadoresYTramosJornadasReales()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C300, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date doDaysStartDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
	Date doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 1);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	doDaysStartDate = add(doDaysEndDate, Calendar.DAY_OF_MONTH, 5);
	doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 4);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	doDaysStartDate = add(doDaysEndDate, Calendar.DAY_OF_MONTH, 5);
	doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 1);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	doDaysStartDate = add(doDaysEndDate, Calendar.DAY_OF_MONTH, 2);
	doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 5);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	addPayment(aonContext, contract, String.format("100.00 * %s ", ContextVariable.DO_DAYS.getName()));

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());
	
	tramos.forEach ( tramo -> {
	    try {
		Utils.marshal(tramo, System.out);
	    } catch (JAXBException e) {
		throw new RuntimeException(e);
	    }
	} );

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	
	
	org.junit.Assert.assertEquals(2, tramo.getDatosTramo().getDatoSolicitado().size());
	
	tramo.getDatosTramo().getDatoSolicitado().forEach( d -> {
	    if ( d.getCodigo().equals("500")) 
		return ;
	    else if (d.getCodigo().equals("601") )
		return ;
	    org.junit.Assert.fail(d.getCodigo());
	});

    }

    @Test
    public void testCretaTrabajadoresYTramosJornadasRealesConstant()
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
	Connection connection = getConnection();
	AONContext aonContext = new AONContext(connection);

	cleanSalaries(aonContext);
	cleanSystemPayments(aonContext);

	String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, ccc, ContractCode.C300, "10");

	Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
	Date endDate = getLastDayOfMonth(startDate);

	Date doDaysStartDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
	Date doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 1);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	doDaysStartDate = add(doDaysEndDate, Calendar.DAY_OF_MONTH, 5);
	doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 4);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	doDaysStartDate = add(doDaysEndDate, Calendar.DAY_OF_MONTH, 5);
	doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 1);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	doDaysStartDate = add(doDaysEndDate, Calendar.DAY_OF_MONTH, 2);
	doDaysEndDate = add(doDaysStartDate, Calendar.DAY_OF_MONTH, 5);
	addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS.getName(),
		Long.toString(new Period(doDaysStartDate, doDaysEndDate).getDays()));

	addPayment(aonContext, contract, String.format("100.00"));
	addPayment(aonContext, contract, String.format("100.00 * %s ", ContextVariable.DO_DAYS.getName()));

	List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);

	Assert.assertEquals(1, tramos.size());
	
	tramos.forEach ( tramo -> {
	    try {
		Utils.marshal(tramo, System.out);
	    } catch (JAXBException e) {
		throw new RuntimeException(e);
	    }
	} );

	Tramo tramo = tramos.get(0);
	Assert.assertEquals("01", tramo.getFechaDesde().getDia());
	Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
	
	
	org.junit.Assert.assertEquals(2, tramo.getDatosTramo().getDatoSolicitado().size());
	
	tramo.getDatosTramo().getDatoSolicitado().forEach( d -> {
	    if ( d.getCodigo().equals("500")) 
		return ;
	    else if (d.getCodigo().equals("601") )
		return ;
	    org.junit.Assert.fail(d.getCodigo());
	});

    }

    private <T> void validate(Class<T> clazz, T t) throws JAXBException, SAXException {
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	Utils.marshal(t, os);
	ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
	Utils.unmarshal(clazz, is, Utils.BASES);
    }

    private Errores getErrores(String... codes) {
	Errores errores = new Errores();
	for (String code : codes) {
	    errores.getError().add(getError(code));
	}
	return errores;
    }

    private Error getError(String code) {
	Error error = new Error();
	error.setCodigoErr(code);
	error.setDescripcion(code);
	return error;
    }

    private CtaCot getCtaCot(String ccc) {
	CtaCot ctaCot = new CtaCot();
	ctaCot.setRegimen("0111");
	ctaCot.setProvincia(ccc.substring(0, 2));
	ctaCot.setNumero(ccc.substring(2));
	return ctaCot;
    }

    protected static Periodo getPerido(Date date) {
	Periodo periodo = new Periodo();
	int year = get(date, Calendar.YEAR);
	int month = get(date, Calendar.MONTH);
	periodo.setAnho(String.format("%04d", year));
	periodo.setMes(String.format("%02d", month + 1));
	return periodo;
    }

    protected static FechaHoraRecaudacion getFechaHoraRecaudacion(Date date) {
	FechaHoraRecaudacion fechaHora = new FechaHoraRecaudacion();
	int year = get(date, Calendar.YEAR);
	int month = get(date, Calendar.MONTH);
	fechaHora.setHoraRecaudacion("000000");
	net.aonsolutions.core.tgss.creta.jaxb.respuesta.Fecha fecha = new net.aonsolutions.core.tgss.creta.jaxb.respuesta.Fecha();
	fecha.setAnho(String.format("%04d", year));
	fecha.setMes(String.format("%02d", month + 1));
	fechaHora.setFechaRecaudacion(fecha);
	return fechaHora;
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc) {
	return newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.PRINCIPAL);
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode) {
	return newContract(aonContext, ccc, contractCode, "03", CCCType.PRINCIPAL);
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode,
	    String quoteGroup) {
	return newContract(aonContext, ccc, contractCode, quoteGroup, CCCType.PRINCIPAL);
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode,
	    String quoteGroup, Date startDate, Date endDate) {
	return newContract(aonContext, ccc, contractCode, quoteGroup, CCCType.PRINCIPAL, startDate, endDate, null,
		Integer.toString((int) (Math.random() * 1000000000.00)), UUID.randomUUID().toString().substring(0, 12));
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode,
	    String quoteGroup, CCCType cccType) {
	return newContract(aonContext, ccc, contractCode, quoteGroup, cccType,
		add(getFirstDayOfYear(getToday()), Calendar.MONTH, -1), null, null,
		Integer.toString((int) (Math.random() * 1000000000.00)), UUID.randomUUID().toString().substring(0, 12));
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode,
	    String quoteGroup, CCCType cccType, Date startDate, Date endDate, String dni) {
	return newContract(aonContext, ccc, contractCode, quoteGroup, cccType, startDate, endDate, null, dni,
		UUID.randomUUID().toString().substring(0, 12));
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode,
	    String quoteGroup, CCCType cccType, Date startDate, Date endDate, AgreementLevelCategoryRecord category) {
	return newContract(aonContext, ccc, contractCode, quoteGroup, cccType, startDate, endDate, category,
		Integer.toString((int) (Math.random() * 1000000000.00)), UUID.randomUUID().toString().substring(0, 12));
    }

    protected static ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode,
	    String quoteGroup, CCCType cccType, Date startDate, Date endDate, AgreementLevelCategoryRecord category,
	    String dni, String nss) {
	DomainRecord domain = newDomain(aonContext);

	ScopeRecord scope = newScope(aonContext, domain.getId());

	EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(aonContext, domain.getId(), scope.getId(),
		SSRegimeType.GENERAL);

	EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, domain.getId(), scope.getId(),
		enterpriseActivity.getId(), cccType, ccc);

	WorkplaceRecord workplace = newWorkplace(aonContext, domain.getId(), scope.getId(),
		enterpriseActivity.getEnterprise());

	RegistryRecord person = newPerson(aonContext, domain.getId(), dni, // "00000000A"
		nss// "123456789012"
	);

	@SuppressWarnings("serial")
	ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL, cccType, startDate, // getFirstDayOfYear(getToday()),
		endDate, new HashMap<String, String>() {
		    {
			// put(MONTH_DAYS.getName(), String.format("%f", 30.00));
			put(QUOTE_GROUP.getName(), String.format("'%s'", quoteGroup));
			put(TC2.getName(), String.format("\"%s\"", contractCode.getValue()));
			put(MONTH_DAYS.getName(), String.format("{'%s':30}[%s]", quoteGroup, QUOTE_GROUP.getName()));

		    }
		}, new String[] { "250.00 * DIAS_TRABAJADOS / DIAS_MES", "1500.00 * DIAS_TRABAJADOS / DIAS_MES",
//				"TRACE('GRUPO_COTIZACION=%s\r\n', GRUPO_COTIZACION); 0.00;"
		}, new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05", "BASE_IRPF * PORCENTAJE_IRPF/100",
//				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
		}, category, domain.getId(), // domainId,
		person.getId(), // personId,
		workplace.getId(), // workplaceId,
		enterpriseCcc.getId(), // enterpriseCccId,
		enterpriseActivity.getId() // enterpriseActivityId
	);

	PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");

	addPayment(aonContext, contract, prestIT, String.format("BASE_REGULADORA * 0.00 * %s_1_3", COMMON_DISEASE_DAYS),
		String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS));
	addPayment(aonContext, contract, prestIT,
		String.format("BASE_REGULADORA * 0.60 * %s_4_15", COMMON_DISEASE_DAYS),
		String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS));
	addPayment(aonContext, contract, prestIT,
		String.format("BASE_REGULADORA * 0.75 * %s_16_20", COMMON_DISEASE_DAYS),
		String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS));
	addPayment(aonContext, contract, prestIT, String.format("BASE_REGULADORA * 0.75 * %s_21", COMMON_DISEASE_DAYS),
		String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS));
	addPayment(aonContext, contract, prestIT,
		String.format("BASE_REGULADORA * 0.75 * %s", OCCUPATIONAL_DISEASE_DAYS),
		String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS));

	addPayment(aonContext, contract, prestIT,
		String.format("BASE_REGULADORA * 0.00 * %s", COMMON_DISEASE_LACK_DAYS),
		String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS));

	PaymentConceptRecord mtnad = addConcept(aonContext, "MTNAD");

	addPayment(aonContext, contract, mtnad, String.format("0.00 * %s", MATERNITY_DAYS), String.format(
		"%s * (isdef COEFICIENTE_MATERNIDAD ? COEFICIENTE_MATERNIDAD : 1.00) * BASE_REGULADORA", QUOTE_DAYS));

	addPayment(aonContext, contract, mtnad, String.format("0.00 * %s", PATERNITY_DAYS), String.format(
		"%s * (isdef COEFICIENTE_PATERNIDAD ? COEFICIENTE_PATERNIDAD : 1.00) * BASE_REGULADORA", QUOTE_DAYS) //
	);

	PaymentConceptRecord ere = addConcept(aonContext, "ERE");

	addPayment(aonContext, contract, ere, String.format("/*read-only*/%s * 0.00/**/", ERE_DAYS),
		String.format("%s * BASE_REGULADORA", ERE_DAYS));

	PaymentConceptRecord ereFza = addConcept(aonContext, "ERE_FZA");

	addPayment(aonContext, contract, ereFza, String.format("/*read-only*/%s * 0.00/**/", ERE_DAYS_FORCE),
		String.format("%s * BASE_REGULADORA", ERE_DAYS_FORCE));

	PaymentConceptRecord ereFzaExonerado = addConcept(aonContext, "ERE_FZA_EXONERADO");

	addPayment(aonContext, contract, ereFzaExonerado,
		String.format("/*read-only*/%s * 0.00/**/", ERE_DAYS_FORCE_OFF),
		String.format("%s * BASE_REGULADORA", ERE_DAYS_FORCE_OFF));

	//@formatter:on
	return contract;
    }

    // -------------------------------------------------------------------------
    protected net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos getTrabajadoresTramos(
	    Connection connection, ContractRecord contract, Date startDate, Date endDate, String ccc, String tipo)
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {

	ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		endDate, contract);

	int salaries = calculateAndSave(connection, ctx);

	// Only one salary saved to DB.
	Assert.assertEquals(1, salaries);

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(startDate);
	String mes = Integer.toString(calendar.get(MONTH) + 1);
	String anho = Integer.toString(calendar.get(YEAR));

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	TrabajadoresTramos.generate(connection, "0000", // autorizado,
		mes, // desdeAnhoMes,
		anho, // desdeAnho,
		mes, // hastaMes,
		anho, // hastaAnho,
		mes, // ctrlMes,
		anho, // ctrlAnho,
		tipo, // tipo,
		new String[] { "0111" + "" + ccc }, // cccs
		trabajadoresTramosOs);
	trabajadoresTramosOs.flush();

	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
			trabajadoresTramosIs);

	trabajadoresTramosIs.close();
	trabajadoresTramosOs.close();

	return trabajadoresTramos;

    }

    protected net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos getTrabajadoresTramos(
	    Connection connection, Date startDate, Date endDate, String ccc, String tipo, ContractRecord... contracts)
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {

	for (ContractRecord contract : contracts) {
	    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		    endDate, contract);

	    int salaries = calculateAndSave(connection, ctx);
	    // Only one salary saved to DB.
	    Assert.assertEquals(1, salaries);

	}

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(startDate);
	String mes = Integer.toString(calendar.get(MONTH) + 1);
	String anho = Integer.toString(calendar.get(YEAR));

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	TrabajadoresTramos.generate(connection, "0000", // autorizado,
		mes, // desdeAnhoMes,
		anho, // desdeAnho,
		mes, // hastaMes,
		anho, // hastaAnho,
		mes, // ctrlMes,
		anho, // ctrlAnho,
		tipo, // tipo,
		new String[] { "0111" + "" + ccc }, // cccs
		trabajadoresTramosOs);
	trabajadoresTramosOs.flush();

	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
			trabajadoresTramosIs);

	trabajadoresTramosOs.close();
	trabajadoresTramosIs.close();

	return trabajadoresTramos;

    }

    private List<Tramo> getTramos(Connection connection, ContractRecord contract, Date startDate, Date endDate,
	    String ccc) throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");

	return trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores().getTrabajador().get(0)
		.getTramos().getTramo();

    }

    private List<Tramo> getTramos(Connection connection, Date startDate, Date endDate, String ccc,
	    ContractRecord... contracts)
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, "L00", contracts);

	return trabajadoresTramos.getLiquidacion().getLiquidacionMes().get(0).getTrabajadores().getTrabajador().get(0)
		.getTramos().getTramo();

    }

    private net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos getTrabajadoresTramos(
	    Connection connection, Date startDate, Date endDate, String ccc, ContractRecord... contracts)
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, startDate, endDate, ccc, "L00", contracts);

	return trabajadoresTramos;

    }

    private Liquidacion getLiquidacion(Connection connection, ContractRecord contract, Date startDate, Date endDate,
	    String ccc) throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {

	net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = getTrabajadoresTramos(
		connection, contract, startDate, endDate, ccc, "L00");

	return trabajadoresTramos.getLiquidacion();

    }

    protected ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection,
	    Date contractStart, Date endDate, ContractRecord contract) throws SQLException, ExpressionException {
	Criteria criteria = new Criteria();
	criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
	ISQLContractSalaryCalculatorContext ctx = new SmartSQLContractSettleCalculatorContext(connection, contractStart,
		endDate, endDate, criteria);
	ctx.next();
	return ctx;
    }

    protected List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> getTramosBases(Connection connection,
	    Date startDate, Date endDate, String ccc, ContractRecord... contracts)
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {

	net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion liquidacion = getLiquidacion(connection, startDate,
		endDate, ccc, contracts);

	return liquidacion.getLiquidacionMes().get(0).getTrabajadores().getTrabajador().get(0).getTramos().getTramo();
    }

    private net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion getLiquidacion(Connection connection,
	    Date startDate, Date endDate, String ccc, ContractRecord... contracts)
	    throws ExpressionException, SQLException, SalaryException, EmptyBasesException, JAXBException,
	    XMLStreamException, FactoryConfigurationError, IOException {
	return getBases(connection, startDate, endDate, ccc, contracts).getLiquidacion().get(0);
    }

    private net.aonsolutions.core.tgss.creta.jaxb.bases.Bases getBases(Connection connection, Date startDate,
	    Date endDate, String ccc, ContractRecord... contracts)
	    throws ExpressionException, SQLException, SalaryException, EmptyBasesException, JAXBException,
	    XMLStreamException, FactoryConfigurationError, IOException {
	for (ContractRecord contract : contracts) {
	    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		    endDate, contract);

	    int salaries = calculateAndSave(connection, ctx);

	    // Only one salary saved to DB.
	    Assert.assertEquals(1, salaries);
	}

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(startDate);
	String mes = Integer.toString(calendar.get(MONTH) + 1);
	String anho = Integer.toString(calendar.get(YEAR));

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	TrabajadoresTramos.generate(connection, "0000", // autorizado,
		mes, // desdeAnhoMes,
		anho, // desdeAnho,
		mes, // hastaMes,
		anho, // hastaAnho,
		mes, // ctrlMes,
		anho, // ctrlAnho,
		"L00", // tipo,
		new String[] { "0111" + "" + ccc }, // cccs
		trabajadoresTramosOs);

	trabajadoresTramosOs.flush();
	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	ByteArrayOutputStream basesOs = new ByteArrayOutputStream();

	Bases.generate(connection, true, // comments,
		false, // skipExisting,
		false, // acceptPrevBases,
		null, // nafs,
		new String[] {}, // defaultsValues,
		trabajadoresTramosIs, null, // respuestaIs,
		basesOs, // os,
		new Bases.BasesCallback[] {} // cbs
	);

	System.out.println(basesOs.toString());

	ByteArrayInputStream basesIs = new ByteArrayInputStream(basesOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases.class, basesIs);

	basesIs.close();
	basesOs.close();
	trabajadoresTramosIs.close();
	trabajadoresTramosOs.close();
	return bases;
    }

    // -------------------------------------------------------------------------
    private net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion getLiquidacion(Connection connection,
	    net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos)
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {

	ByteArrayOutputStream trabajadoresTramosOs = new ByteArrayOutputStream();
	Utils.marshal(trabajadoresTramos, trabajadoresTramosOs);
	trabajadoresTramosOs.flush();

	ByteArrayInputStream trabajadoresTramosIs = new ByteArrayInputStream(trabajadoresTramosOs.toByteArray());

	ByteArrayOutputStream basesOs = new ByteArrayOutputStream();

	Bases.generate(connection, true, // comments,
		false, // skipExisting,
		false, // acceptPrevBases,
		null, // nafs,
		new String[] {}, // defaultsValues,
		trabajadoresTramosIs, null, // respuestaIs,
		basesOs, // os,
		new Bases.BasesCallback[] {} // cbs
	);

	System.out.println(basesOs.toString());

	ByteArrayInputStream basesIs = new ByteArrayInputStream(basesOs.toByteArray());

	net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases = Utils
		.unmarshal(net.aonsolutions.core.tgss.creta.jaxb.bases.Bases.class, basesIs);

	basesIs.close();
	basesOs.close();
	trabajadoresTramosIs.close();
	trabajadoresTramosOs.close();

	return bases.getLiquidacion().get(0);
    }

    private List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> getBases(Connection connection,
	    net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos)
	    throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException,
	    XMLStreamException, FactoryConfigurationError {

	return getLiquidacion(connection, trabajadoresTramos).getLiquidacionMes().get(0).getTrabajadores()
		.getTrabajador().get(0).getTramos().getTramo();
    }
    // -------------------------------------------------------------------------

    private static void assertTramoTutoria(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

	assertDatosSolicitado(datoSolicitados, "C", "737", "P");
	assertDatosSolicitado(datoSolicitados, "H", "06", "P");
    }

    private static void assertTramoHorasFormacion(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

	assertDatosSolicitado(datoSolicitados, "H", "03", "P");
	assertDatosSolicitado(datoSolicitados, "H", "04", "P");
    }

    private static void assertLiquidacionFormacionContinua(Liquidacion liquidacion) {
	List<DatoSolicitado> datoSolicitados = liquidacion.getDatosLiquidacion().getDato();

	assertDatosSolicitado(datoSolicitados, "C", "763", "P");
    }

    private static void assertTramoExpedienteRegulacionEmpleoTotal(Tramo tramo) {
	assertTramoITPagoDirecto(tramo);
    }

    private static void assertTramoITPagoDirecto(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

	assertDatosSolicitado(datoSolicitados, "C", "509", "B");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "603", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "613", "B");
	}
    }

    private static void assertTramoIT15PrimerosDias(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

	assertDatosSolicitado(datoSolicitados, "C", "500", "B");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "603", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "613", "B");
	}
    }

    private static void assertTramoIT15PrimerosDiasDiario(Tramo tramo) {
	assertTramoIT15PrimerosDias(tramo);
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertDatosSolicitado(datoSolicitados, "I", "51", "P");
    }

    private static void assertTramoITPagoDelegado(Tramo tramo) {
	assertTramoIT15PrimerosDias(tramo);
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertDatosSolicitado(datoSolicitados, "C", "563", "B");
    }

    private static void assertTramoITATEPPagoDelegado(Tramo tramo) {
	assertTramoIT15PrimerosDias(tramo);
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertDatosSolicitado(datoSolicitados, "C", "663", "B");
    }

    private static void assertTramoActivoNormal(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

	assertDatosSolicitado(datoSolicitados, "C", "500", "B");
	assertDatosSolicitado(datoSolicitados, "C", "501", "P");
	assertDatosSolicitado(datoSolicitados, "C", "502", "P");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "601", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "611", "B");
	}
    }

    private static void assertTramoActivoNormalFormacion(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertDatosSolicitado(datoSolicitados, "C", "501", "P");
	assertDatosSolicitado(datoSolicitados, "C", "502", "P");
	assertDatosSolicitado(datoSolicitados, "C", "737", "P");
	assertDatosSolicitado(datoSolicitados, "H", "03", "P");
	assertDatosSolicitado(datoSolicitados, "H", "04", "P");
	assertDatosSolicitado(datoSolicitados, "H", "06", "P");

    }

    private static void assertTramoActivoNormalFormacionEnAlternancia(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertNoDatoSolicitado(datoSolicitados, "I", "51");
	assertDatosSolicitado(datoSolicitados, "C", "500", "B");
	assertDatosSolicitado(datoSolicitados, "C", "301", "P");
	assertDatosSolicitado(datoSolicitados, "C", "601", "B");
	assertDatosSolicitado(datoSolicitados, "H", "03", "P");
	assertDatosSolicitado(datoSolicitados, "H", "04", "P");
	assertDatosSolicitado(datoSolicitados, "H", "06", "P");
	assertDatosSolicitado(datoSolicitados, "C", "737", "P");
	assertDatosSolicitado(datoSolicitados, "C", "501", "P");

    }

    private static void assertTramoActivoNormalArtistas(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertDatosSolicitado(datoSolicitados, "C", "501", "P");
	assertDatosSolicitado(datoSolicitados, "C", "502", "P");
	assertDatosSolicitado(datoSolicitados, "C", "300", "B");
    }

    private static void assertTramoActivoNormalTiempoCompleto(Tramo tramo) {
	assertTramoActivoNormal(tramo);
    }

    private static void assertTramoActivoNormalTiempoParcial(Tramo tramo) {
	assertTramoActivoNormal(tramo);
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertDatosSolicitado(datoSolicitados, "H", "01", "B");
	assertDatosSolicitado(datoSolicitados, "C", "537", "P");
	assertDatosSolicitado(datoSolicitados, "H", "02", "P");
    }

    private static void assertTramoActivoNormalTiempoCompletoDiario(Tramo tramo) {
	assertTramoActivoNormal(tramo);
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	assertDatosSolicitado(datoSolicitados, "I", "51", "P");
    }

    private static void assertTramoMaternidadTiempoCompleto(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

	assertDatosSolicitado(datoSolicitados, "C", "509", "B");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "603", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "613", "B");
	}
    }

    private static void assertTramoMaternidadTiempoParcial(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	// PARTE JORNADA TRABAJADA
	assertDatosSolicitado(datoSolicitados, "C", "500", "B");
	assertDatosSolicitado(datoSolicitados, "C", "501", "P");
	assertDatosSolicitado(datoSolicitados, "C", "537", "P");
	assertDatosSolicitado(datoSolicitados, "H", "01", "B");
	assertDatosSolicitado(datoSolicitados, "H", "02", "P");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "601", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "611", "B");
	}

	// PARTE JORNADA EN SITUACIÓN DE DESCANSO
	assertDatosSolicitado(datoSolicitados, "C", "535", "B");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "635", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "634", "B");
	}
    }

    private static void assertTramoExpedienteRegulacionEmpleoParcialActivo(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
	// PARTE JORNADA TRABAJADA
	assertDatosSolicitado(datoSolicitados, "C", "500", "B");
	assertDatosSolicitado(datoSolicitados, "C", "501", "P");
	assertDatosSolicitado(datoSolicitados, "C", "537", "P");
	assertDatosSolicitado(datoSolicitados, "H", "01", "B");
	assertDatosSolicitado(datoSolicitados, "H", "02", "P");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "601", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "611", "B");
	}

	// PARTE JORNADA EN SITUACIÓN DE DESCANSO
	assertTramoExpedienteRegulacionEmpleoParcial(tramo);
    }

    private static void assertTramoExpedienteRegulacionEmpleoParcial(Tramo tramo) {
	List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

	// PARTE JORNADA EN SITUACIÓN DE DESCANSO
	assertDatosSolicitado(datoSolicitados, "H", "05", "B");
	assertDatosSolicitado(datoSolicitados, "C", "536", "B");
	try {
	    assertDatosSolicitado(datoSolicitados, "C", "636", "B");
	} catch (AssertException e) {
	    assertDatosSolicitado(datoSolicitados, "C", "637", "B");
	}

    }


    private static void assertNoDatoSolicitado(Tramo tramo, String tipoDato, String codigo) {
	List<DatoSolicitado> datos = tramo.getDatosTramo().getDatoSolicitado();
	for (DatoSolicitado dato : datos) {
	    if (dato.getCodigo().equals(codigo)) {
		throw new AssertException("DatoSOlictado" + codigo + " Found");
	    }
	}
	return;

    }

    private static void assertDatosSolicitado(List<DatoSolicitado> datoSolicitados, String tipoDato, String codigo,
	    String indicadorObligatoriedad) {
	for (DatoSolicitado datoSolicitado : datoSolicitados) {
	    if (datoSolicitado.getCodigo().equals(codigo)) {
		Assert.assertEquals(tipoDato, datoSolicitado.getTipoDato());
		Assert.assertEquals(indicadorObligatoriedad, datoSolicitado.getIndicadorObligatoriedad());
		return;
	    }
	}

	throw new AssertException("Dato Solicitado " + codigo + " Not Found");
    }

    private static void assertDato(List<Dato> datos, String tipoDato, String codigo) {
	for (Dato dato : datos) {
	    if (dato.getCodigo().equals(codigo)) {
		Assert.assertEquals(tipoDato, dato.getTipoDato());
		return;
	    }
	}

	throw new AssertException("Dato " + codigo + " Not Found");
    }

    private static void assertDato(List<Dato> datos, String tipoDato, String codigo, String valor) {
	for (Dato dato : datos) {
	    if (dato.getCodigo().equals(codigo)) {
		Assert.assertEquals(tipoDato, dato.getTipoDato());
		Assert.assertEquals(valor, dato.getValor());
		return;
	    }
	}

	throw new AssertException("Dato " + codigo + " Not Found");
    }

    private static void assertDato(List<Dato> datos, String tipoDato, String codigo, Double valor) {
	assertDato(datos, tipoDato, codigo, Integer.toString(valor.intValue()));
    }

    private static void assertDato(List<Dato> datos, String tipoDato, String codigo, Predicate<String> predicate) {
	for (Dato dato : datos) {
	    if (dato.getCodigo().equals(codigo)) {
		Assert.assertEquals(tipoDato, dato.getTipoDato());
		Assert.assertEquals(true, predicate.test(dato.getValor()));
		return;
	    }
	}

	throw new AssertException("Dato " + codigo + " Not Found");
    }

    private static void assertNoDato(List<Dato> datos, String tipoDato, String codigo) {
	for (Dato dato : datos) {
	    if (dato.getCodigo().equals(codigo)) {
		throw new AssertException("Dato " + codigo + " Found");
	    }
	}
	return;

    }

    private static void assertNoDatoSolicitado(List<DatoSolicitado> datos, String tipoDato, String codigo) {
	for (DatoSolicitado dato : datos) {
	    if (dato.getCodigo().equals(codigo)) {
		throw new AssertException("DatoSOlictado" + codigo + " Found");
	    }
	}
	return;

    }

    private static Tramo clone(Tramo tramo) {
	Tramo clone = new Tramo();

	clone.setDiasCotizados(tramo.getDiasCotizados());
	clone.setFechaDesde(clone(tramo.getFechaDesde()));
	clone.setFechaHasta(clone(tramo.getFechaHasta()));
	clone.setDatosTramo(clone(tramo.getDatosTramo()));
	clone.setInformacionAfiliacion(clone(tramo.getInformacionAfiliacion()));

	return clone;
    }

    private static Fecha clone(Fecha fecha) {
	Fecha clone = new Fecha();
	clone.setDia(fecha.getDia());
	clone.setAnho(fecha.getAnho());
	clone.setMes(fecha.getMes());
	return clone;
    }

    private static DatosTramo clone(DatosTramo datosTramo) {
	DatosTramo clone = new DatosTramo();

	List<DatoSolicitado> datoSolicitado = new ArrayList<DatoSolicitado>();
	datosTramo.getDatoSolicitado().forEach(d -> datoSolicitado.add(clone(d)));
	clone.setDatoSolicitado(datoSolicitado);

	return clone;
    }

    private static DatoSolicitado clone(DatoSolicitado datoSolicitado) {
	DatoSolicitado clone = new DatoSolicitado();
//		clone.getValor(datoSolicitado.getValor());
	clone.setCodigo(datoSolicitado.getCodigo());
	clone.setTipoDato(datoSolicitado.getTipoDato());
	clone.setIndicadorObligatoriedad(datoSolicitado.getIndicadorObligatoriedad());
	return clone;
    }

    private static InformacionAfiliacion clone(InformacionAfiliacion informacionAfiliacion) {
	InformacionAfiliacion clone = new InformacionAfiliacion();
	clone.setCNAE(informacionAfiliacion.getCNAE());
	clone.setCoeficienteTiempoParcial(informacionAfiliacion.getCoeficienteTiempoParcial());
//		clone.setColectivoEspecial(informacionAfiliacion.getColectivoEspecial());
	clone.setCatProfesional(informacionAfiliacion.getCatProfesional());
//		clone.setEpigrafe(informacionAfiliacion.getEpigrafe());
	clone.setGrupoCotizacion(informacionAfiliacion.getGrupoCotizacion());
//		clone.setTRL(informacionAfiliacion.getTRL());
//		clone.setModalidadCotizSEA(informacionAfiliacion.getGrupoCotizacion());
	clone.setTipoContrato(informacionAfiliacion.getGrupoCotizacion());

	return clone;
    }

    private static final Map<Integer, ContextVariable> WEEK_HOURS_VARIABLES = new HashMap<Integer, ContextVariable>() {
	{
	    put(SUNDAY, SUNDAY_HOURS);
	    put(MONDAY, MONDAY_HOURS);
	    put(TUESDAY, TUESDAY_HOURS);
	    put(WEDNESDAY, WEDNESDAY_HOURS);
	    put(THURSDAY, THURSDAY_HOURS);
	    put(FRIDAY, FRIDAY_HOURS);
	    put(SATURDAY, SATURDAY_HOURS);
	}
    };
}
