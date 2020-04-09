package com.esferalia.aon.altai.tgss.creta;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0001;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0004;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static java.util.Calendar.DAY_OF_MONTH;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Converter;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.altai.tgss.creta.CRAParser.ConceptoRetributivo;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.ContractPayment;
import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.jooq.tables.SystemPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.dba.DatosBancarios;


public class CRA2Aon {

	private static interface TrabajadorCallback  {
		void trabajador(String ccc, CRAParser.Periodo periodo, CRAParser.Trabajador trabajador);
	}
	
	Date fromDate;
	
	Condition where;
	DSLContext dslContext;
	Connection connection;
	
	Map<String, Integer> paymentConceptsByCode;
	Map<PaymentType, Integer> paymentConceptsByType;
	

	public CRA2Aon(Connection connection, Condition where ) {
		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		
		this.where = where;
		this.connection = connection;
		this.dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	}
	
	public void fix(File file) throws IOException {
		setup();
		dslContext.transaction((configuration)->{			
		parse(file, this::fixTrabajador);
		});
	}	
	
	// ------------------------------------------------------------------------
	
	private void setup() {
//		fromDate =
//		dslContext
//		.select(DSL.min(CONTRACT.START_DATE))
//		.from(CONTRACT)
//		.innerJoin(PERSON).onKey()
//		.innerJoin(ENTERPRISE_CCC).onKey()
//		.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))	
//		.and(where)
//		.fetchOne(DSL.min(CONTRACT.START_DATE))
//		;		
		fromDate = new Date(2020-1900, 0, 1);
		
		SelectConditionStep<Record1<Integer>> systemConcepts = 
		DSL
		.select(SYSTEM_PAYMENT.PAYMENT_CONCEPT)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.isNotNull());
		
		paymentConceptsByType =
		dslContext.select(
		PAYMENT_CONCEPT.TYPE,
		DSL.min(PAYMENT_CONCEPT.ID),
		DSL.count())
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.ID.ge(0))
		.and(PAYMENT_CONCEPT.TYPE.isNotNull())
		.and(PAYMENT_CONCEPT.ID.notIn(systemConcepts))
		.groupBy(PAYMENT_CONCEPT.TYPE)
		.having(DSL.count().eq(1))
		.fetchStream()
		.collect(Collectors.toMap(r -> typeOf( r.get(PAYMENT_CONCEPT.TYPE)), r -> r.value2()))
		;
		
		paymentConceptsByCode =
		dslContext.select(
		PAYMENT_CONCEPT.CODE,
		DSL.min(PAYMENT_CONCEPT.ID)
		)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.ID.ge(0))
		.and(PAYMENT_CONCEPT.TYPE.isNotNull())
		.and(PAYMENT_CONCEPT.ID.notIn(systemConcepts))
		.groupBy(PAYMENT_CONCEPT.CODE)
		.fetchStream()
		.collect(Collectors.toMap(r -> r.get(PAYMENT_CONCEPT.CODE), r -> r.value2()))
		;
	}
	
	private void parse(File file, TrabajadorCallback cb) throws IOException {
		Reader reader = null; 
		try {
			reader = new FileReader(file);
			parse(reader, cb);
		} 
		catch ( CRAException e ) {
			System.out.println("Not CRA file " + file );
		}
		finally {
			if ( reader != null )
				reader.close();
		}
	}
	
	private void parse(Reader reader, TrabajadorCallback cb) throws IOException {
		CRAParser.CRA cra = CRAParser.parse(reader);
		cra.getLiquidaciones().forEach(l -> liquidacion2Aon(l, cb));
	}	
	
	private void liquidacion2Aon(CRAParser.Liquidacion liquidacion, TrabajadorCallback cb) {
		CRAParser.CtaCot ctaCot = liquidacion.getCcc();
		CRAParser.Periodo periodo = liquidacion.getPeriodoLiquidacion();

		Date startDate = getFirstDayOfMonth(periodo);
		if( startDate.before(fromDate ) )
			return;
		
		String ccc = String.format("%s%s", ctaCot.getProvincia() ,ctaCot.getNumero());

		liquidacion.getTrabajadores()
		.forEach(trabajador -> cb.trabajador(ccc, periodo, trabajador));
		;
	}

	private Date getFirstDayOfMonth(CRAParser.Periodo periodo) {
		int year = Integer.parseInt(periodo.getAnho()) - 1900;
		int month = Integer.parseInt(periodo.getMes()) - 1;		
		return new Date(year, month, 1);
	}
	
	private void fixTrabajador(String ccc, CRAParser.Periodo periodo, CRAParser.Trabajador trabajador) {
		
		
		Date firstDayOfMonth = getFirstDayOfMonth(periodo);
		Date lastDayOfMonth = AonDateUtils.getLastDayOfMonth(firstDayOfMonth);
		int monthdays = get(lastDayOfMonth, DAY_OF_MONTH);
		
		String naf = trabajador.getNaf();
		
		try {
			ContractData GRUPO_COTIZACION = CONTRACT_DATA.as("G");
			ContractData COEFICIENTE_PARCIALIDAD = CONTRACT_DATA.as("C");
			
			dslContext
			.select()
			.from(CONTRACT)
			.innerJoin(PERSON).onKey()
			.innerJoin(ENTERPRISE_CCC).onKey()
			.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))

			// COEFICIENTE_PARCIALIDAD
			.leftJoin(CONTRACT_DATA.as(COEFICIENTE_PARCIALIDAD))
			.on(CONTRACT.ID.eq(COEFICIENTE_PARCIALIDAD.CONTRACT)
			.and(COEFICIENTE_PARCIALIDAD.NAME.eq(PARTIAL_FACTOR.getName())))
			
			.where(ENTERPRISE_CCC.CCC.eq(ccc))
			.and(PERSON.SOCIAL_SECURITY_NUM.eq(naf))
			.and(CONTRACT.START_DATE.le(lastDayOfMonth))
			.and(CONTRACT.END_DATE.isNull()
			.or(CONTRACT.END_DATE.ge(firstDayOfMonth)))
			.and(where)
			.fetchOptional()
			.ifPresent( record -> {
				
				// not intermiate
				
				Map<PaymentType, ConceptoRetributivo> conceptos = 
				trabajador.getConceptosRetributivos().stream()
				.collect(Collectors.toMap(c -> typeOf(c.getCodigo()), c -> c));
				
				// conceptos are grouped by codigo
				

				java.util.Date startDate = Period.max(firstDayOfMonth, record.get(CONTRACT.START_DATE));
				java.util.Date endDate = Period.min(lastDayOfMonth, record.get(CONTRACT.END_DATE));				
				int days = get(endDate, DAY_OF_MONTH) - get(startDate, DAY_OF_MONTH) + 1;	
				
				ConceptoRetributivo concepto1 = conceptos.get(CRA_0001);
				ConceptoRetributivo concepto4 = conceptos.get(CRA_0004);
				if ( concepto4 == null) 
					System.err.println("No 0004" );
				
				double importe1 = Integer.parseInt(concepto1.getImporte())/100.00;
				double importe4 = Integer.parseInt(concepto4.getImporte())/100.00;
								
				
				int pagas = (int) Math.ceil((importe4 * 12 )/ importe1);
				if ( pagas  != 2 && pagas  != 3 ) 
					return;
				
				dslContext
				.delete(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.CONTRACT.eq(record.get(CONTRACT.ID)))
				.execute();

				Byte months [] = {
						(byte) Month.DECEMBER.ordinal(), 
						(byte) Month.JULY.ordinal(), 
						(byte) Month.MARCH.ordinal(), 
						null, null, null, null, null, 

						};
				
				double coeficenteParcialidad = 1.00;
				try {
					String expression = Optional.ofNullable(record.get(COEFICIENTE_PARCIALIDAD.EXPRESSION)).orElse("1.00");
					coeficenteParcialidad = MVEL.eval(expression, Number.class ).doubleValue();
				} catch ( Exception e ) {
					
				}
				
				// pagas = import4 * 12 / importe1 
				// importe1 * pagas = importe4 * 12 
				// importe1 = importe4 * 12  / pagas 
				
				double importeSalarioBase = (importe4 * 12 / pagas ) ;
				double importePlusSalarial = importe1 - importeSalarioBase ;
				
				if ( importePlusSalarial < 1.00 ) {
					importePlusSalarial = 0.00;
					importeSalarioBase = importe1;
				}
				
				importeSalarioBase /= coeficenteParcialidad;
				importeSalarioBase *=  30 / (days == monthdays ? 30.00 : days );
				
				importePlusSalarial /= coeficenteParcialidad;
				importePlusSalarial *=  30 / (days == monthdays ? 30.00 : days );

				
				double importePaga = importe4/pagas ;
				importePaga /= coeficenteParcialidad;
				importePaga *=  30 / (days == monthdays ? 30.00 : days );
				
				Locale es = Locale.forLanguageTag("es");
				// PAGAS EXTRAORDINARIAS
				for ( int i = 0 ; i < pagas; i++ ) {
					Month month = Month.getMonthByValue(months[i]);
					String mes = AonStringUtils.upperCase(month.getName(es));
					dslContext
					.insertInto(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.DOMAIN, record.get(CONTRACT.DOMAIN))
					.set(CONTRACT_PAYMENT.CONTRACT, record.get(CONTRACT.ID))
					.set(CONTRACT_PAYMENT.START_DATE, record.get(CONTRACT.START_DATE))
					.set(CONTRACT_PAYMENT.END_DATE, record.get(CONTRACT.END_DATE))
					.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) 0)
					.set(CONTRACT_PAYMENT.MONTH, months[i])
					.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConceptsByType.get(CRA_0004))
					.set(CONTRACT_PAYMENT.MONTH, months[i])
					.set(CONTRACT_PAYMENT.EXPRESSION, 
					String.format(
					Locale.ROOT,
					"/*read-only*/%.2f * %s / %s /**/", 
					importePaga * 12.00, 
					ContextVariable.WORKED_DAYS, 
					ContextVariable.MONTH_DAYS 
					))
					.set(CONTRACT_PAYMENT.DESCRIPTION, 
					String.format(
					"PAGA EXTRA %s",
					mes
					))
					.execute()
					;					
					System.out.printf("PAGA EXTRAORDINARIA %s: %.2f ", mes, importePaga );
				}
				
				// SALARIO BASE
				dslContext
				.insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, record.get(CONTRACT.DOMAIN))
				.set(CONTRACT_PAYMENT.CONTRACT, record.get(CONTRACT.ID))
				.set(CONTRACT_PAYMENT.START_DATE, record.get(CONTRACT.START_DATE))
				.set(CONTRACT_PAYMENT.END_DATE, record.get(CONTRACT.END_DATE))
				.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) 0)
				.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConceptsByCode.get("SALARIO_BASE"))
				.set(CONTRACT_PAYMENT.DESCRIPTION, "SALARIO BASE")
				.set(CONTRACT_PAYMENT.EXPRESSION, 
				String.format(
				Locale.ROOT,
				"/*read-only*/%.2f * %s / %s /**/", 
				importeSalarioBase, 
				ContextVariable.WORKED_DAYS, 
				ContextVariable.MONTH_DAYS 
				))
				.execute()
				;
				System.out.printf("SALARIO BASE : %.2f", importeSalarioBase );
				
				if ( importePlusSalarial > 0.00 ) {
					dslContext
					.insertInto(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.DOMAIN, record.get(CONTRACT.DOMAIN))
					.set(CONTRACT_PAYMENT.CONTRACT, record.get(CONTRACT.ID))
					.set(CONTRACT_PAYMENT.START_DATE, record.get(CONTRACT.START_DATE))
					.set(CONTRACT_PAYMENT.END_DATE, record.get(CONTRACT.END_DATE))
					.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) 0)
					.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConceptsByCode.get("PLUS_SALARIAL"))
					.set(CONTRACT_PAYMENT.DESCRIPTION, "PLUS SALARIAL")
					.set(CONTRACT_PAYMENT.EXPRESSION, 
					String.format(
					Locale.ROOT,
					"/*read-only*/%.2f * %s / %s /**/", 
					importePlusSalarial, 
					ContextVariable.WORKED_DAYS, 
					ContextVariable.MONTH_DAYS 
					))
					.execute()
					;
					System.out.printf(" PLUS SAlARIAL : %.2f", importePlusSalarial );
				}
				
				double finalCoeficenteParcialidad = coeficenteParcialidad;
				conceptos.values().stream()
				.filter(c -> typeOf(c.getCodigo()) != CRA_0001)
				.filter(c -> typeOf(c.getCodigo()) != CRA_0004)
				.filter(c -> paymentConceptsByType.containsKey(typeOf(c.getCodigo())))
				.forEach(c -> {
					double importe = Integer.parseInt(concepto4.getImporte())/100.00;
					importe /= finalCoeficenteParcialidad;
					importe *=  30 / (days == monthdays ? 30.00 : days );					
					
					dslContext
					.insertInto(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.DOMAIN, record.get(CONTRACT.DOMAIN))
					.set(CONTRACT_PAYMENT.CONTRACT, record.get(CONTRACT.ID))
					.set(CONTRACT_PAYMENT.START_DATE, record.get(CONTRACT.START_DATE))
					.set(CONTRACT_PAYMENT.END_DATE, record.get(CONTRACT.END_DATE))
					.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) 0)
					.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConceptsByType.get(typeOf(c.getCodigo())))
					.set(CONTRACT_PAYMENT.EXPRESSION, 
					String.format(
					Locale.ROOT,
					"/*read-only*/%.2f * %s / %s /**/", 
					importe, 
					ContextVariable.WORKED_DAYS, 
					ContextVariable.MONTH_DAYS 
					))
					.execute();				
					System.out.printf(" %s : %.2f ", typeOf(c.getCodigo()).getName(es), importe );
				});
				
				System.out.println();
				
//				System.out.println(ccc + ", " + trabajador.getNaf() 
//				+ ", Importe1: " + importe1 
//				+ ", Importe4: " + importe4 
//				+ ", Salario Base: " + importeSalarioBase 
//				+ ", Plus Salarial: " + importePlusSalarial 
//				+ ", Paga Extraordinaria: " + importePaga + " (" + pagas +")" 
//				);
				
				
			});
		}catch ( NullPointerException e ) {
			
		}catch ( TooManyRowsException e ) {
			
		} catch ( IllegalStateException e ) {
			
		}


	}
	
	private static PaymentType typeOf(Byte b) {
		return PaymentType.values()[b];
	}	
	private static PaymentType typeOf(String codigo) {
		return PaymentType.values()[Integer.parseInt(codigo)];
	}
	
	

}
