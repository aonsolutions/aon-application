package com.esferalia.aon.altai.tgss.creta;

import static com.esferalia.aon.altai.tgss.creta.Utils.fecha2Date;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.jooq.tables.EnterpriseCcc;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Calculos;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.calculos.Tramo;

public class Calculos2Aon extends Abstract2Aon{
	

	private static interface TramoCallback  {
		void tramo(String ccc, String naf, Tramo tramo);
	}
	

	int sdlAgreementLevel ;

	public Calculos2Aon(Connection connection, Condition where ) {
		super(connection, where);
		System.out.printf(
				"ccc,naf,trabajador,empresa,inicio_nomina,fin_nomina,base,c500,incio_contrato,fin_contrato\r\n"
				);	
		
	}
	
	public void fix(File file) throws IOException {
		dslContext.transaction((configuration)->{			
		this.sdlAgreementLevel = insertSDLAgreement(dslContext, 0);
		parse(file, this::fixTramo);
		});
	}
	
	public void check(File file) throws IOException {
		dslContext.transaction((configuration)->{			
		this.sdlAgreementLevel = insertSDLAgreement(dslContext, 0);
		parse(file, this::checkTramo);
		});
	}

	// ------------------------------------------------------------------------
	
	private void parse(File file, TramoCallback cb) throws IOException {
		InputStream is = null; 
		try {
			is = new FileInputStream(file);
			parse(is, cb);
		} catch ( Exception e ) {
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
	
	private void checkTramo ( String ccc, String naf, Tramo tramo) {

		Fecha fechaDesde = tramo.getFechaDesde();
		Fecha fechaHasta = tramo.getFechaHasta();
		
		Date startDate = fecha2Date(fechaDesde);
		Date endDate = fecha2Date(fechaHasta);
		
		findContract(ccc, naf, startDate, endDate, r ->{
		
			tramo.getCalculosTramo().getDatoCalculado()
			.stream().filter(d -> d.getCodigo().equals("500"))
			.findFirst().ifPresentOrElse(
			d -> {
				try {
					Salary salary = calculate(r, startDate, endDate);
					double commonBase = salary.getCommonBase();
					double valorBase = Double.parseDouble(d.getValorBase())/ 100.0;
					
					(( Math.round(commonBase) == Math.round(valorBase)) ? System.out : System.err )
					.printf(
							"\"%s\",\"%s\",\"%s\",\"%s\",\"%5$td/%5$tm/%5$tY\",\"%6$td/%6$tm/%6$tY\",\"%7$.2f\",\"%8$.2f\",\"%9$td/%9$tm/%9$tY\",\"%10$s\"\r\n",
							AonStringUtils.leftPad(salary.getCcc(), 11, "0"),
							AonStringUtils.leftPad(salary.getSocialSecurityNumber(), 12, "0"),
							salary.getEmployeeName(),
							salary.getEnterpriseName(),
							salary.getStartDate(),
							salary.getEndDate(),
							commonBase,
							valorBase,
							r.get(CONTRACT.START_DATE),
							r.get(CONTRACT.END_DATE) == null ? "" : String.format("%1$td/%1$tm/%1$tY", r.get(CONTRACT.END_DATE))
 							);	
					if ( Math.round(commonBase) != Math.round(valorBase)) {
						fixBaseCgcMin(r, valorBase, startDate, endDate);
					} else {
						calculateAndsave(r, startDate, endDate);
					}
					
				}  catch (NullPointerException e) {
				} catch (ExpressionException | SalaryException | SQLException e) {
				} catch (ConcurrentModificationException e) {
					System.out.printf(e.getMessage());
				}
			}
			, () -> {});
		
		});
		
		
		
		
	}
	
	private void fixTramo ( String ccc, String naf, Tramo tramo) {
		Fecha fechaDesde = tramo.getFechaDesde();
		Fecha fechaHasta = tramo.getFechaHasta();
		
		Date fromDate = fecha2Date(fechaDesde);
		Date toDate = fecha2Date(fechaHasta);
		
		findContract(ccc, naf, fromDate, toDate, r -> { 

			System.out.printf(
					"\"%s\",\"%s\",\"%s\",\"%s\"",
					r.get(ENTERPRISE_CCC.CCC),
					r.get(PERSON.SOCIAL_SECURITY_NUM),
					r.get(PERSON.FIRST_SURNAME),
					r.get(PERSON.NAME)
					);

			fixInformacionAfiliacion(tramo, fromDate, toDate,r);
			fixDatoCalculados(tramo, fromDate, toDate, r);
			fixAgreementLevel(r, sdlAgreementLevel);
			
			System.out.printf("\r\n");
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
		
		tramo.getCalculosTramo().getDatoCalculado().forEach(d -> {
			String name = "C" + d.getCodigo();
			double base = Double.parseDouble(d.getValorBase())/100.00;
			if ( base == 0.00 )
				return;
			fix(name, base, period, r, datas)
			.ifPresent(var -> {});		
		});
		
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
				System.out.printf(
				",\"%s(%s)\"",
				tgssCNAE,
				aonCNAE
				);
								
				SelectConditionStep<Record1<Integer>> cnae2009Id  = 
				DSL.select(CNAE2009.ID).from(CNAE2009).where(CNAE2009.CODE.eq(tgssCNAE));
				
				dslContext
				.update(ENTERPRISE_ACTIVITY)
				.set(ENTERPRISE_ACTIVITY.CNAE2009,cnae2009Id )
				.where(ENTERPRISE_ACTIVITY.ID.eq(r.get(ENTERPRISE_ACTIVITY.ID)))
				.execute();
			} else {
				System.out.printf(
				",\"%s\"",tgssCNAE);
			}
		}
		catch ( Exception e ) {
		}
		
		String tgssCoeficienteTiempoParcial = tramo.getInformacionAfiliacion().getCoeficienteTiempoParcial();
		try {

			double tgssPartialFactor = Double.parseDouble(tgssCoeficienteTiempoParcial)/1000.00;

			if ( tgssPartialFactor > 0.00 &&  tgssPartialFactor < 1.00 && !allMatch(PARTIAL_FACTOR, tgssPartialFactor, period, datas) ) {

				System.out.printf(",\"%.2f(%.2f)\"",
						tgssPartialFactor, 
						findNoMatch(PARTIAL_FACTOR, tgssPartialFactor, period, datas).orElse(0.00));
				
				fixNoMatch(domain, contract, PARTIAL_FACTOR, tgssPartialFactor, period, datas);
				join(datas, PARTIAL_FACTOR);
				expand.add(PARTIAL_FACTOR);
			} else if (tgssPartialFactor > 0.00 &&  tgssPartialFactor < 1.00 ){
				System.out.printf(",\"%.2f\"", tgssPartialFactor );
			} else {
				System.out.printf(",\"%.2f\"", 1.00 );
			}
		} catch ( NumberFormatException | NullPointerException e ) {
			System.out.printf(",\"%.2f\"", 1.00 );
		}

		
		
		if ( isLastDayOfMonth(toDate)) 
			expand(contract, toDate, expand);
	}
	
	
	

	// ----------------------------------------------------------------- static


	protected static int insertSDLAgreement(DSLContext dslContext, int domainId) {
		
		Date EPOCH = new Date(0);
		String SISTEMA_DE_LIQUIDACION_DIRECTA = "SISTEMA DE LIQUIDACION DIRECTA (TGSS)";
		
		int agreementLevelId =  	
			dslContext
			.select()
			.from(AGREEMENT)
			.innerJoin(AGREEMENT_LEVEL).onKey()
			.where(AGREEMENT.DOMAIN.eq(domainId))
			.and(AGREEMENT.DESCRIPTION.eq(SISTEMA_DE_LIQUIDACION_DIRECTA))
			.fetchOptional(AGREEMENT_LEVEL.ID)
			.orElse(Integer.MIN_VALUE);
		
		boolean upgraded = agreementLevelId != Integer.MIN_VALUE;
		
		if ( upgraded ) 
			return agreementLevelId;
		

		int sdlAgreementId =
		dslContext
		.insertInto(AGREEMENT)
		.set(AGREEMENT.DOMAIN, domainId)
		.set(AGREEMENT.DESCRIPTION, SISTEMA_DE_LIQUIDACION_DIRECTA)
		.returning()
		.fetchOne()
		.getId()
		;
		
		
		agreementLevelId =
		dslContext
		.insertInto(AGREEMENT_LEVEL)
		.set(AGREEMENT_LEVEL.DOMAIN, domainId)
		.set(AGREEMENT_LEVEL.AGREEMENT, sdlAgreementId)
		.set(AGREEMENT_LEVEL.DESCRIPTION, "I")
		.returning()
		.fetchOne()
		.getId();
		
		dslContext
		.insertInto(AGREEMENT_LEVEL_CATEGORY)
		.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, domainId)
		.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelId)
		.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "I")
		.execute();
		
		dslContext
		.insertInto(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.DOMAIN, domainId)
		.set(AGREEMENT_PAYMENT.AGREEMENT, sdlAgreementId)
		.set(AGREEMENT_PAYMENT.TYPE, (byte) 1)
		.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
		.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
		.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
		.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
		.set(AGREEMENT_PAYMENT.EXPRESSION, "/*read-only*/C500/**/")
		.set(AGREEMENT_PAYMENT.DESCRIPTION, "SDL CONCEPTO 500 BASE DE CONTINGENCIAS COMUNES")
		.execute();
		
		return agreementLevelId;
	}
	
	private static int compare(Tramo t1, Tramo t2) {
		return fecha2Date(t1.getFechaDesde()).compareTo(fecha2Date(t2.getFechaDesde()));
	}
	

	public static void main(String[] args) {
		System.out.printf("%120s", 6666);
	}
	
	
}	
