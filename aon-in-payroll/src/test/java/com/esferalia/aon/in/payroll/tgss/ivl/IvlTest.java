package com.esferalia.aon.in.payroll.tgss.ivl;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.jooq.Keys;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.github.javafaker.Faker;

public class IvlTest extends AbstractSQLTestCase {

    @Test
    public void testIvlcccI() throws IOException, UnknownPDFException {
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccI.pdf")) {
	    
	    IvlcccParser.parse(is, new IvlParserListener() {
		
		private int personCount = 0;
		private int contractCount = 0;
		private String employeeName = null ;
		
		@Override
		public void onEnterprise(String enterpriseName, String cccRegime, String cccProvince, String cccNumber,
			String docType, String docNumber, String enterpriseAddress, String enterpriseCity,
			String enterpriseCP, String enterpriseCNAENumber, String enterpriseCNAEDescription) {
		    assertEquals("RINKO INSTALACIONES INTERNACIONALES,SL", enterpriseName);
		    assertEquals("0111", cccRegime);
		    assertEquals("08", cccProvince);
		    assertEquals("189529639", cccNumber);
		    assertEquals("9", docType);
		    assertEquals("B66259516", docNumber);

		    assertEquals("CL TUSET 19 EN", enterpriseAddress);
		    assertEquals("BARCELONA", enterpriseCity);
		    assertEquals("08006", enterpriseCP);

		    assertEquals("4321", enterpriseCNAENumber);
		    assertEquals("Instalaciones el\u00e9ctricas", enterpriseCNAEDescription);
		}
		
		@Override
		public void onEmployee(String nafProvince, String nafNumber, String docType, String docNumber,
			String employeeName) {
		    personCount++;
		    assertFalse(docNumber.startsWith("0"), docNumber);
		    assertFalse(employeeName.contains("---"), employeeName);
		    assertNull(this.employeeName, "ERROR : " + this.employeeName + ", without contracts ");
		    this.employeeName = employeeName;
		}
		
		@Override
		public void onEmployeeContract(Date realStartDate, Date efectiveStartDate, Date realEndDate,
			Date efectiveEndDate, String quoteGroup, String monthly, String tc2, Double partialFactor, Double it, Double ims, Integer quoteDays) {
		    
		    if ( "100".equals(tc2) ) {
			assertNull(partialFactor, employeeName + " : 100- INDEFINIDO, TIEMPO COMPLETO, ORDINARIO");
		    }
		    
		    Map<String, Date> endDates = new HashMap<String, Date>();
		    endDates.put("ZAZA KHURTSIDZE", parseDate("23-10-2023"));
		    endDates.put("AFZAAL AKRAM", parseDate("16-10-2023"));
		    endDates.put("ISRAEL CARMONA REY", parseDate("23-10-2023"));
		    endDates.put("KHALID DAWOOD KHAN", parseDate("27-10-2023"));
		    endDates.put("KAMRAN SARWAR", parseDate("27-10-2023"));
		    
		    
		    endDates.forEach(( name, pdfDate ) -> {
			if ( name.equalsIgnoreCase(employeeName) ) {
			    assertEquals(pdfDate, realEndDate, name);
			    assertEquals(pdfDate, efectiveEndDate, name);
			}
		    });
			
		    contractCount++;
		    System.out.printf("persons : %d = contracts: %d\r\n", personCount , contractCount );
		    this.employeeName = null;
		}
	    });
	}
    }

    @Test
    public void testIvlcccII() throws IOException, UnknownPDFException {
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccII.pdf")) {
	    IvlcccParser.parse(is, new IvlParserListener() {
		
		private int personCount = 0;
		private int contractCount = 0;
		private String employeeName = null ;
		
		@Override
		public void onEnterprise(String enterpriseName, String cccRegime, String cccProvince, String cccNumber,
			String docType, String docNumber, String enterpriseAddress, String enterpriseCity,
			String enterpriseCP, String enterpriseCNAENumber, String enterpriseCNAEDescription) {
		    assertEquals("AYUDA T SOLUCIONES PROFESIONALES SL", enterpriseName);
		    assertEquals("0111", cccRegime);
		    assertEquals("11", cccProvince);
		    assertEquals("122534302", cccNumber);
		    assertEquals("9", docType);
		    assertEquals("B72384936", docNumber);

		    assertEquals("AV ISAAC NEWTON 287 1", enterpriseAddress);
		    assertEquals("PUERTO  DE  SANTA", enterpriseCity);
		    assertEquals("11500", enterpriseCP);

		    assertEquals("6920", enterpriseCNAENumber);
		    assertEquals("Actividades de contabilidad, tenedur\u00eda d", enterpriseCNAEDescription);
		}
		
		@Override
		public void onEmployee(String nafProvince, String nafNumber, String docType, String docNumber,
			String employeeName) {
		    
		    assertNull(this.employeeName, "ERROR : " + this.employeeName + ", without contracts ");
		    this.employeeName = employeeName;
		    personCount++;
		}
		
		@Override
		public void onEmployeeContract(Date realStartDate, Date efectiveStartDate, Date realEndDate,
			Date efectiveEndDate, String quoteGroup, String monthly, String tc2, Double partialFactor, Double it, Double ims, Integer quoteDays) {
		    if ( "100".equals(tc2) ) {
			assertNull(partialFactor, employeeName + " : 100 - INDEFINIDO, TIEMPO COMPLETO, ORDINARIO");
		    } else if ("501".equals(tc2) ) {
			//assertNotNull(employeeName + " : 501 - DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO", partialFactor);
		    }
		    this.employeeName = null;
		    contractCount++;
		    System.out.printf("persons : %d = contracts: %d\r\n", personCount , contractCount );
		}
	    });
	}
    }

    @Test
    public void testIvlcccIJooq() throws IOException, UnknownPDFException {
	String domainName = Faker.instance().internet().domainName();
	
	Integer domainId ;
	Integer registryId ;
	Integer workplaceId ;
	Integer payrollWorkplaceId ;
	Integer raddressId ;
	Integer enterpriseCccId ;
	Integer enterpriseActivityId ;
	
	Map<Integer, Record> contractMap;
	Map<Integer, Record> personRegistryMap;
	
	//Domain PARENT_DOMAIN = DOMAIN.as("parent_domain");
	ContractData tc2 = CONTRACT_DATA.as("tc2");
	ContractData quoteGroup = CONTRACT_DATA.as("quote_group");
	ContractData monthDays = CONTRACT_DATA.as("month_days");
	ContractData coeficienteParcialidad = CONTRACT_DATA.as("coeficiente_parcialidad");

	
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccI.pdf")) {
	    DSLContext dslContext = getDslContext();
	    JooqIvl2Contract jooqIvl2Contract =  
	    new JooqIvl2Contract(dslContext, domainName);
	    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    
	    org.jooq.Record record =
	    dslContext
	    .select()
	    .from(ENTERPRISE_CCC)
	    .innerJoin(ENTERPRISE_ACTIVITY).onKey(Keys.FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY)
	    .innerJoin(ENTERPRISE).onKey(Keys.FK_ENTERPRISE_ACTIVITY_ENTERPRISE)
	    .innerJoin(REGISTRY).onKey(Keys.FK_ENTERPRISE_REGISTRY)
	    .innerJoin(DOMAIN).onKey(Keys.FK_REGISTRY_DOMAIN)
	    .innerJoin(PAYROLL_WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY)
	    .innerJoin(WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_WORKPLACE)
	    .innerJoin(RADDRESS).onKey(Keys.FK_WORKPLACE_RADDRESS)
	    .where(DOMAIN.NAME.eq(/*"B66259516." +*/ domainName))
	    .fetchOne();
	    
	    assertEquals("B66259516", record.get(REGISTRY.DOCUMENT));
	    assertEquals("08189529639", record.get(ENTERPRISE_CCC.CCC));
	    assertEquals(4321,  (int) record.get(ENTERPRISE_ACTIVITY.CNAE2009));
	    assertEquals("RINKO INSTALACIONES INTERNACIONALES,SL", record.get(REGISTRY.NAME));
	    assertEquals("RINKO INSTALACIONES INTERNACIONALES,SL", record.get(DOMAIN.DESCRIPTION));
	    assertEquals("BARCELONA", record.get(RADDRESS.CITY));
	    assertEquals("08019", record.get(RADDRESS.MUNICIPALITY_CODE));
	    assertEquals("08006", record.get(RADDRESS.ZIP));
	    assertEquals("CL TUSET 19 EN", record.get(RADDRESS.ADDRESS));
	    
	    domainId = record.get(DOMAIN.ID);
	    registryId = record.get(REGISTRY.ID);
	    workplaceId = record.get(WORKPLACE.ID);
	    raddressId = record.get(RADDRESS.ID);
	    enterpriseCccId = record.get(ENTERPRISE_CCC.ID);
	    payrollWorkplaceId = record.get(PAYROLL_WORKPLACE.ID);
	    enterpriseActivityId = record.get(ENTERPRISE_ACTIVITY.ID);
	    
	    personRegistryMap  =
	    dslContext
	    .select()
	    .from(PERSON)
	    .innerJoin(REGISTRY).onKey()
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(PERSON.REGISTRY), r -> r));
	    
	    System.out.println(
		    dslContext
		    .select()
		    .from(CONTRACT)
		    .innerJoin(PERSON).onKey()
		    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
		    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
		    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
		    .where(CONTRACT.DOMAIN.eq(domainId))
		    .getSQL()
	  );
	    
	    contractMap  =
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(CONTRACT.ID), r -> r));
	    
	    
	    Integer contractCount =
	    dslContext
	    .select(DSL.count())
	    .from(CONTRACT)
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchOne(DSL.count());
	    
	    Integer personCount =
	    dslContext
	    .select(DSL.count())
	    .from(PERSON)
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchOne(DSL.count());
	    
	    assertTrue(contractCount >= personCount, "CONTRACTS : " + contractCount + ", PERSONS : " + personCount);
	    
	    
	    dslContext
	    .select()
	    .from(REGISTRY)
	    .where(REGISTRY.DOMAIN.eq(domainId))
	    .and(REGISTRY.NAME.eq("HARSH"))
	    .fetchOptionalInto(REGISTRY)
	    .ifPresentOrElse(
	    r -> {
	    } , 
	    () -> { 
		fail("HARSH not found"); 
	    });

	}
	
	// re-entrat 
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccI.pdf")) {
	    DSLContext dslContext = getDslContext();
	    JooqIvl2Contract jooqIvl2Contract =  
	    new JooqIvl2Contract(dslContext, domainName);
	    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    
	    org.jooq.Record record =
	    dslContext
	    .select()
	    .from(ENTERPRISE_CCC)
	    .innerJoin(ENTERPRISE_ACTIVITY).onKey(Keys.FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY)
	    .innerJoin(ENTERPRISE).onKey(Keys.FK_ENTERPRISE_ACTIVITY_ENTERPRISE)
	    .innerJoin(REGISTRY).onKey(Keys.FK_ENTERPRISE_REGISTRY)
	    .innerJoin(DOMAIN).onKey(Keys.FK_REGISTRY_DOMAIN)
	    .innerJoin(PAYROLL_WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY)
	    .innerJoin(WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_WORKPLACE)
	    .innerJoin(RADDRESS).onKey(Keys.FK_WORKPLACE_RADDRESS)
	    .where(DOMAIN.NAME.eq(/*"B66259516." +*/ domainName))
	    .fetchOne();
	    
	    assertEquals("B66259516", record.get(REGISTRY.DOCUMENT));
	    assertEquals("08189529639", record.get(ENTERPRISE_CCC.CCC));
	    assertEquals(4321,  (int) record.get(ENTERPRISE_ACTIVITY.CNAE2009));
	    assertEquals("RINKO INSTALACIONES INTERNACIONALES,SL", record.get(REGISTRY.NAME));
	    assertEquals("RINKO INSTALACIONES INTERNACIONALES,SL", record.get(DOMAIN.DESCRIPTION));
	    assertEquals("BARCELONA", record.get(RADDRESS.CITY));
	    assertEquals("08019", record.get(RADDRESS.MUNICIPALITY_CODE));
	    assertEquals("08006", record.get(RADDRESS.ZIP));
	    assertEquals("CL TUSET 19 EN", record.get(RADDRESS.ADDRESS));
	    
	    assertEquals(domainId, record.get(DOMAIN.ID) );
	    assertEquals(registryId, record.get(REGISTRY.ID) );
	    assertEquals(workplaceId, record.get(WORKPLACE.ID) );
	    assertEquals(raddressId, record.get(RADDRESS.ID) );
	    assertEquals(enterpriseCccId, record.get(ENTERPRISE_CCC.ID) );
	    assertEquals(payrollWorkplaceId, record.get(PAYROLL_WORKPLACE.ID) );
	    assertEquals(enterpriseActivityId, record.get(ENTERPRISE_ACTIVITY.ID) );
	    
	    dslContext
	    .select()
	    .from(PERSON)
	    .innerJoin(REGISTRY).onKey()
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchStream()
	    .forEach(r -> {
		Record mapR = personRegistryMap.get(r.get(PERSON.REGISTRY));
		assertEquals(mapR.get(PERSON.NAME), r.get(PERSON.NAME));
		assertEquals(mapR.get(PERSON.FIRST_SURNAME), r.get(PERSON.FIRST_SURNAME));
		assertEquals(mapR.get(PERSON.SECOND_SURNAME), r.get(PERSON.SECOND_SURNAME));
		assertEquals(mapR.get(REGISTRY.DOCUMENT), r.get(REGISTRY.DOCUMENT));
		assertEquals(mapR.get(PERSON.SOCIAL_SECURITY_NUM), r.get(PERSON.SOCIAL_SECURITY_NUM));
		
	    });
	    
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .leftJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(monthDays).on(CONTRACT.ID.eq(monthDays.CONTRACT).and(monthDays.NAME.eq("DIAS_MES")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .forEach(r -> {
		Record mapR = contractMap.get(r.get(CONTRACT.ID));
		assertEquals(mapR.get(CONTRACT.PERSON), r.get(CONTRACT.PERSON));
		assertEquals(mapR.get(CONTRACT.WORKPLACE), r.get(CONTRACT.WORKPLACE));
		assertEquals(mapR.get(CONTRACT.ENTERPRISE_CCC), r.get(CONTRACT.ENTERPRISE_CCC));
		assertEquals(mapR.get(CONTRACT.ENTERPRISE_ACTIVITY), r.get(CONTRACT.ENTERPRISE_ACTIVITY));
		assertEquals(mapR.get(CONTRACT.START_DATE), r.get(CONTRACT.START_DATE));
		assertEquals(mapR.get(CONTRACT.END_DATE), r.get(CONTRACT.END_DATE));

		assertEquals(mapR.get(tc2.EXPRESSION), r.get(tc2.EXPRESSION));
		assertEquals(mapR.get(quoteGroup.EXPRESSION), r.get(quoteGroup.EXPRESSION));
	    });
	    
	    
	}
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccI.pdf")) {
	    DSLContext dslContext = getDslContext();

	    contractMap
	    .entrySet()
	    .stream()
	    .skip(66)
	    .limit(200)
	    .map(Entry::getValue)
	    .forEach( r -> {
		dslContext
		.delete(CONTRACT_DATA)
		.where(CONTRACT_DATA.ID.in(
			r.get(quoteGroup.ID)
			, r.get(tc2.ID)
			, r.get(coeficienteParcialidad.ID)))
		.execute();
		dslContext
		.delete(CONTRACT)
		.where(CONTRACT.ID.in(r.get(CONTRACT.ID)))
		.execute();
		
	    });

	    JooqIvl2Contract jooqIvl2Contract =  
		    new JooqIvl2Contract(dslContext, domainName);
		    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    Map<String, Record > newContractMap  =
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(PERSON.SOCIAL_SECURITY_NUM)+"/"+r.get(CONTRACT.START_DATE), r -> r));

	    assertEquals(contractMap.size(), newContractMap.size());
	    
	    contractMap
	    .entrySet()
	    .stream()
	    .map(Entry::getValue)
	    .forEach( r -> {
		Record newMapR = newContractMap.get(r.get(PERSON.SOCIAL_SECURITY_NUM)+"/"+r.get(CONTRACT.START_DATE));
		
		//assertNotEquals(newMapR.get(CONTRACT.ID), r.get(CONTRACT.ID));

		assertEquals(newMapR.get(CONTRACT.PERSON), r.get(CONTRACT.PERSON));
		assertEquals(newMapR.get(CONTRACT.WORKPLACE), r.get(CONTRACT.WORKPLACE));
		assertEquals(newMapR.get(CONTRACT.ENTERPRISE_CCC), r.get(CONTRACT.ENTERPRISE_CCC));
		assertEquals(newMapR.get(CONTRACT.ENTERPRISE_ACTIVITY), r.get(CONTRACT.ENTERPRISE_ACTIVITY));
		assertEquals(newMapR.get(CONTRACT.START_DATE), r.get(CONTRACT.START_DATE));
		assertEquals(newMapR.get(CONTRACT.END_DATE), r.get(CONTRACT.END_DATE));

		assertEquals(newMapR.get(tc2.EXPRESSION), r.get(tc2.EXPRESSION));
		assertEquals(newMapR.get(quoteGroup.EXPRESSION), r.get(quoteGroup.EXPRESSION));
	    });
	    
	    
	}
	    
	    
	    
	    
    }
    
    @Test
    public void testIvlcccIIIJooq() throws IOException, UnknownPDFException {
	String domainName = Faker.instance().internet().domainName();
	
	Integer domainId ;
	Integer registryId ;
	Integer workplaceId ;
	Integer payrollWorkplaceId ;
	Integer raddressId ;
	Integer enterpriseCccId ;
	Integer enterpriseActivityId ;
	
	Map<Integer, Record> contractMap;
	Map<Integer, Record> personRegistryMap;
	
	//Domain PARENT_DOMAIN = DOMAIN.as("parent_domain");
	ContractData tc2 = CONTRACT_DATA.as("tc2");
	ContractData quoteGroup = CONTRACT_DATA.as("quote_group");
	ContractData monthDays = CONTRACT_DATA.as("month_days");
	ContractData coeficienteParcialidad = CONTRACT_DATA.as("coeficiente_parcialidad");

    DSLContext dslContext = getDslContext();
    AONContext aonContext = new AONContext(dslContext);
	
	DomainRecord domain = newDomain(aonContext, domainName, "CASTAÑO CARRASCO JOSE MIGUEL");
	ScopeRecord scope = newScope(aonContext, domain.getId());
	RegistryRecord enterprise = newEnterprise(aonContext, domain.getId(), "CASTAÑO CARRASCO JOSE MIGUEL", "77722690D", "ES", DocumentType.NIF, scope.getId());
	EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity4Enterprise(aonContext, domain.getId(), enterprise.getId(), SSRegimeType.GENERAL, "9329");
	newEnterpriseCcc(aonContext, domain.getId(), scope.getId(), enterpriseActivity.getId(), CCCType.PRINCIPAL, "30132614207");
	newEnterpriseCcc(aonContext, domain.getId(), scope.getId(), enterpriseActivity.getId(), CCCType.TRAINING, "30132614207");
	
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccIII.pdf")) {
	    JooqIvl2Contract jooqIvl2Contract =  
	    new JooqIvl2Contract(dslContext, domainName);
	    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    
	    org.jooq.Record record =
	    dslContext
	    .select()
	    .from(ENTERPRISE_CCC)
	    .innerJoin(ENTERPRISE_ACTIVITY).onKey(Keys.FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY)
	    .innerJoin(ENTERPRISE).onKey(Keys.FK_ENTERPRISE_ACTIVITY_ENTERPRISE)
	    .innerJoin(REGISTRY).onKey(Keys.FK_ENTERPRISE_REGISTRY)
	    .innerJoin(DOMAIN).onKey(Keys.FK_REGISTRY_DOMAIN)
	    .innerJoin(PAYROLL_WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY)
	    .innerJoin(WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_WORKPLACE)
	    .innerJoin(RADDRESS).onKey(Keys.FK_WORKPLACE_RADDRESS)
	    .where(DOMAIN.NAME.eq(/*"B66259516." +*/ domainName))
	    .fetchAny();
	    
	    assertEquals("77722690D", record.get(REGISTRY.DOCUMENT));
	    assertEquals("30132614207", record.get(ENTERPRISE_CCC.CCC));
	    assertEquals(9329,  (int) record.get(ENTERPRISE_ACTIVITY.CNAE2009));
	    assertEquals("CASTAÑO CARRASCO JOSE MIGUEL", record.get(REGISTRY.NAME));
	    assertEquals("CASTAÑO CARRASCO JOSE MIGUEL", record.get(DOMAIN.DESCRIPTION));
	    assertEquals("ABARAN", record.get(RADDRESS.CITY));
	    //assertEquals("08019", record.get(RADDRESS.MUNICIPALITY_CODE));
	    assertEquals("30550", record.get(RADDRESS.ZIP));
	    assertEquals("CL MANUEL AUSENSI 37 1 E", record.get(RADDRESS.ADDRESS));
	    
	    domainId = record.get(DOMAIN.ID);
	    registryId = record.get(REGISTRY.ID);
	    workplaceId = record.get(WORKPLACE.ID);
	    raddressId = record.get(RADDRESS.ID);
	    enterpriseCccId = record.get(ENTERPRISE_CCC.ID);
	    payrollWorkplaceId = record.get(PAYROLL_WORKPLACE.ID);
	    enterpriseActivityId = record.get(ENTERPRISE_ACTIVITY.ID);
	    
	    personRegistryMap  =
	    dslContext
	    .select()
	    .from(PERSON)
	    .innerJoin(REGISTRY).onKey()
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(PERSON.REGISTRY), r -> r));
	    
	    System.out.println(
		    dslContext
		    .select()
		    .from(CONTRACT)
		    .innerJoin(PERSON).onKey()
		    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
		    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
		    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
		    .where(CONTRACT.DOMAIN.eq(domainId))
		    .getSQL()
	  );
	    
	    contractMap  =
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(CONTRACT.ID), r -> r));
	    
	    
	    Integer contractCount =
	    dslContext
	    .select(DSL.count())
	    .from(CONTRACT)
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchOne(DSL.count());
	    
	    Integer personCount =
	    dslContext
	    .select(DSL.count())
	    .from(PERSON)
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchOne(DSL.count());
	    
	    System.out.printf("persons : %d = contracts: %d\r\n", personCount , contractCount );
	    
	    assertTrue(contractCount >= personCount, "CONTRACTS : " + contractCount + ", PERSONS : " + personCount);
	    
	    
	    
	    

	}
	
	// re-entrat 
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccIII.pdf")) {
	    JooqIvl2Contract jooqIvl2Contract =  
	    new JooqIvl2Contract(dslContext, domainName);
	    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    
	    org.jooq.Record record =
	    dslContext
	    .select()
	    .from(ENTERPRISE_CCC)
	    .innerJoin(ENTERPRISE_ACTIVITY).onKey(Keys.FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY)
	    .innerJoin(ENTERPRISE).onKey(Keys.FK_ENTERPRISE_ACTIVITY_ENTERPRISE)
	    .innerJoin(REGISTRY).onKey(Keys.FK_ENTERPRISE_REGISTRY)
	    .innerJoin(DOMAIN).onKey(Keys.FK_REGISTRY_DOMAIN)
	    .innerJoin(PAYROLL_WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY)
	    .innerJoin(WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_WORKPLACE)
	    .innerJoin(RADDRESS).onKey(Keys.FK_WORKPLACE_RADDRESS)
	    .where(DOMAIN.NAME.eq(/*"B66259516." +*/ domainName))
	    .fetchAny();
	    
	    assertEquals("77722690D", record.get(REGISTRY.DOCUMENT));
	    assertEquals("30132614207", record.get(ENTERPRISE_CCC.CCC));
	    assertEquals(9329,  (int) record.get(ENTERPRISE_ACTIVITY.CNAE2009));
	    assertEquals("CASTAÑO CARRASCO JOSE MIGUEL", record.get(REGISTRY.NAME));
	    assertEquals("CASTAÑO CARRASCO JOSE MIGUEL", record.get(DOMAIN.DESCRIPTION));
	    assertEquals("ABARAN", record.get(RADDRESS.CITY));
	    //assertEquals("08019", record.get(RADDRESS.MUNICIPALITY_CODE));
	    assertEquals("30550", record.get(RADDRESS.ZIP));
	    assertEquals("CL MANUEL AUSENSI 37 1 E", record.get(RADDRESS.ADDRESS));
	    
	    assertEquals(domainId, record.get(DOMAIN.ID) );
	    assertEquals(registryId, record.get(REGISTRY.ID) );
	    assertEquals(workplaceId, record.get(WORKPLACE.ID) );
	    assertEquals(raddressId, record.get(RADDRESS.ID) );
	    assertEquals(enterpriseCccId, record.get(ENTERPRISE_CCC.ID) );
	    assertEquals(payrollWorkplaceId, record.get(PAYROLL_WORKPLACE.ID) );
	    assertEquals(enterpriseActivityId, record.get(ENTERPRISE_ACTIVITY.ID) );
	    
	    dslContext
	    .select()
	    .from(PERSON)
	    .innerJoin(REGISTRY).onKey()
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchStream()
	    .forEach(r -> {
		Record mapR = personRegistryMap.get(r.get(PERSON.REGISTRY));
		System.out.println( personRegistryMap.containsKey(r.get(PERSON.REGISTRY)) + " person : " + r.get(PERSON.NAME) + " " + r.get(PERSON.FIRST_SURNAME) + " " + r.get(PERSON.SECOND_SURNAME) + ", registry : " + r.get(PERSON.REGISTRY) + ", document : " + r.get(REGISTRY.DOCUMENT));
		assertEquals(mapR.get(PERSON.NAME), r.get(PERSON.NAME));
		assertEquals(mapR.get(PERSON.FIRST_SURNAME), r.get(PERSON.FIRST_SURNAME));
		assertEquals(mapR.get(PERSON.SECOND_SURNAME), r.get(PERSON.SECOND_SURNAME));
		assertEquals(mapR.get(REGISTRY.DOCUMENT), r.get(REGISTRY.DOCUMENT));
		assertEquals(mapR.get(PERSON.SOCIAL_SECURITY_NUM), r.get(PERSON.SOCIAL_SECURITY_NUM));
		
	    });
	    
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .leftJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(monthDays).on(CONTRACT.ID.eq(monthDays.CONTRACT).and(monthDays.NAME.eq("DIAS_MES")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .forEach(r -> {
		Record mapR = contractMap.get(r.get(CONTRACT.ID));
		assertEquals(mapR.get(CONTRACT.PERSON), r.get(CONTRACT.PERSON));
		assertEquals(mapR.get(CONTRACT.WORKPLACE), r.get(CONTRACT.WORKPLACE));
		assertEquals(mapR.get(CONTRACT.ENTERPRISE_CCC), r.get(CONTRACT.ENTERPRISE_CCC));
		assertEquals(mapR.get(CONTRACT.ENTERPRISE_ACTIVITY), r.get(CONTRACT.ENTERPRISE_ACTIVITY));
		assertEquals(mapR.get(CONTRACT.START_DATE), r.get(CONTRACT.START_DATE));
		assertEquals(mapR.get(CONTRACT.END_DATE), r.get(CONTRACT.END_DATE));

		assertEquals(mapR.get(tc2.EXPRESSION), r.get(tc2.EXPRESSION));
		assertEquals(mapR.get(quoteGroup.EXPRESSION), r.get(quoteGroup.EXPRESSION));
	    });
	    
	    
	}
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccIII.pdf")) {

	    contractMap
	    .entrySet()
	    .stream()
	    .skip(66)
	    .limit(200)
	    .map(Entry::getValue)
	    .forEach( r -> {
		dslContext
		.delete(CONTRACT_DATA)
		.where(CONTRACT_DATA.ID.in(
			r.get(quoteGroup.ID)
			, r.get(tc2.ID)
			, r.get(coeficienteParcialidad.ID)))
		.execute();
		dslContext
		.delete(CONTRACT)
		.where(CONTRACT.ID.in(r.get(CONTRACT.ID)))
		.execute();
		
	    });

	    JooqIvl2Contract jooqIvl2Contract =  
		    new JooqIvl2Contract(dslContext, domainName);
		    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    Map<String, Record > newContractMap  =
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(PERSON.SOCIAL_SECURITY_NUM)+"/"+r.get(CONTRACT.START_DATE), r -> r));

	    assertEquals(contractMap.size(), newContractMap.size());
	    
	    contractMap
	    .entrySet()
	    .stream()
	    .map(Entry::getValue)
	    .forEach( r -> {
		Record newMapR = newContractMap.get(r.get(PERSON.SOCIAL_SECURITY_NUM)+"/"+r.get(CONTRACT.START_DATE));
		
		//assertNotEquals(newMapR.get(CONTRACT.ID), r.get(CONTRACT.ID));

		assertEquals(newMapR.get(CONTRACT.PERSON), r.get(CONTRACT.PERSON));
		assertEquals(newMapR.get(CONTRACT.WORKPLACE), r.get(CONTRACT.WORKPLACE));
		assertEquals(newMapR.get(CONTRACT.ENTERPRISE_CCC), r.get(CONTRACT.ENTERPRISE_CCC));
		assertEquals(newMapR.get(CONTRACT.ENTERPRISE_ACTIVITY), r.get(CONTRACT.ENTERPRISE_ACTIVITY));
		assertEquals(newMapR.get(CONTRACT.START_DATE), r.get(CONTRACT.START_DATE));
		assertEquals(newMapR.get(CONTRACT.END_DATE), r.get(CONTRACT.END_DATE));

		assertEquals(newMapR.get(tc2.EXPRESSION), r.get(tc2.EXPRESSION));
		assertEquals(newMapR.get(quoteGroup.EXPRESSION), r.get(quoteGroup.EXPRESSION));
	    });
	    
	    
	}
	    
	    
	    
	    
    }

    @Test
    public void testIvlcccIVJooq() throws IOException, UnknownPDFException {
	String domainName = Faker.instance().internet().domainName();
	
	Integer domainId ;
	Integer registryId ;
	Integer workplaceId ;
	Integer payrollWorkplaceId ;
	Integer raddressId ;
	Integer enterpriseCccId ;
	Integer enterpriseActivityId ;
	
	Map<Integer, Record> contractMap;
	Map<Integer, Record> personRegistryMap;
	
	//Domain PARENT_DOMAIN = DOMAIN.as("parent_domain");
	ContractData tc2 = CONTRACT_DATA.as("tc2");
	ContractData quoteGroup = CONTRACT_DATA.as("quote_group");
	ContractData monthDays = CONTRACT_DATA.as("month_days");
	ContractData coeficienteParcialidad = CONTRACT_DATA.as("coeficiente_parcialidad");

    DSLContext dslContext = getDslContext();
    AONContext aonContext = new AONContext(dslContext);
	
	DomainRecord domain = newDomain(aonContext, domainName, "FC812 STAFF S.L.");
	ScopeRecord scope = newScope(aonContext, domain.getId());
	RegistryRecord enterprise = newEnterprise(aonContext, domain.getId(), "FC812 STAFF S.L.", "B16931438", "ES", DocumentType.NIF, scope.getId());
	EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity4Enterprise(aonContext, domain.getId(), enterprise.getId(), SSRegimeType.GENERAL, "7810");
	newEnterpriseCcc(aonContext, domain.getId(), scope.getId(), enterpriseActivity.getId(), CCCType.PRINCIPAL, "28252599007");
	newEnterpriseCcc(aonContext, domain.getId(), scope.getId(), enterpriseActivity.getId(), CCCType.TRAINING, "28252599007");
	
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccIV.pdf")) {
	    JooqIvl2Contract jooqIvl2Contract =  
	    new JooqIvl2Contract(dslContext, domainName);
	    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    
	    org.jooq.Record record =
	    dslContext
	    .select()
	    .from(ENTERPRISE_CCC)
	    .innerJoin(ENTERPRISE_ACTIVITY).onKey(Keys.FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY)
	    .innerJoin(ENTERPRISE).onKey(Keys.FK_ENTERPRISE_ACTIVITY_ENTERPRISE)
	    .innerJoin(REGISTRY).onKey(Keys.FK_ENTERPRISE_REGISTRY)
	    .innerJoin(DOMAIN).onKey(Keys.FK_REGISTRY_DOMAIN)
	    .innerJoin(PAYROLL_WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY)
	    .innerJoin(WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_WORKPLACE)
	    .innerJoin(RADDRESS).onKey(Keys.FK_WORKPLACE_RADDRESS)
	    .where(DOMAIN.NAME.eq(/*"B66259516." +*/ domainName))
	    .fetchAny();
	    
	    assertEquals("B16931438", record.get(REGISTRY.DOCUMENT));
	    assertEquals("28252599007", record.get(ENTERPRISE_CCC.CCC));
	    assertEquals(7810,  (int) record.get(ENTERPRISE_ACTIVITY.CNAE2009));
	    assertEquals("FC812 STAFF S.L.", record.get(REGISTRY.NAME));
	    assertEquals("FC812 STAFF S.L.", record.get(DOMAIN.DESCRIPTION));
	    assertEquals("GRIÑON", record.get(RADDRESS.CITY));
	    //assertEquals("08019", record.get(RADDRESS.MUNICIPALITY_CODE));
	    assertEquals("28971", record.get(RADDRESS.ZIP));
	    assertEquals("CL ANDROMEDA 6", record.get(RADDRESS.ADDRESS));
	    
	    domainId = record.get(DOMAIN.ID);
	    registryId = record.get(REGISTRY.ID);
	    workplaceId = record.get(WORKPLACE.ID);
	    raddressId = record.get(RADDRESS.ID);
	    enterpriseCccId = record.get(ENTERPRISE_CCC.ID);
	    payrollWorkplaceId = record.get(PAYROLL_WORKPLACE.ID);
	    enterpriseActivityId = record.get(ENTERPRISE_ACTIVITY.ID);
	    
	    personRegistryMap  =
	    dslContext
	    .select()
	    .from(PERSON)
	    .innerJoin(REGISTRY).onKey()
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(PERSON.REGISTRY), r -> r));
	    
	    System.out.println(
		    dslContext
		    .select()
		    .from(CONTRACT)
		    .innerJoin(PERSON).onKey()
		    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
		    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
		    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
		    .where(CONTRACT.DOMAIN.eq(domainId))
		    .getSQL()
	  );
	    
	    contractMap  =
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(CONTRACT.ID), r -> r));
	    
	    
	    Integer contractCount =
	    dslContext
	    .select(DSL.count())
	    .from(CONTRACT)
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchOne(DSL.count());
	    
	    Integer personCount =
	    dslContext
	    .select(DSL.count())
	    .from(PERSON)
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchOne(DSL.count());
	    
	    System.out.printf("persons : %d = contracts: %d\r\n", personCount , contractCount );
	    
	    assertTrue(contractCount >= personCount, "CONTRACTS : " + contractCount + ", PERSONS : " + personCount);
	    
	    
	    
	    

	}
	
	// re-entrat 
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccIII.pdf")) {
	    JooqIvl2Contract jooqIvl2Contract =  
	    new JooqIvl2Contract(dslContext, domainName);
	    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    
	    org.jooq.Record record =
	    dslContext
	    .select()
	    .from(ENTERPRISE_CCC)
	    .innerJoin(ENTERPRISE_ACTIVITY).onKey(Keys.FK_ENTERPRISE_CCC_ENTERPRISE_ACTIVITY)
	    .innerJoin(ENTERPRISE).onKey(Keys.FK_ENTERPRISE_ACTIVITY_ENTERPRISE)
	    .innerJoin(REGISTRY).onKey(Keys.FK_ENTERPRISE_REGISTRY)
	    .innerJoin(DOMAIN).onKey(Keys.FK_REGISTRY_DOMAIN)
	    .innerJoin(PAYROLL_WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY)
	    .innerJoin(WORKPLACE).onKey(Keys.FK_PAYROLL_WORKPLACE_WORKPLACE)
	    .innerJoin(RADDRESS).onKey(Keys.FK_WORKPLACE_RADDRESS)
	    .where(DOMAIN.NAME.eq(/*"B66259516." +*/ domainName))
	    .fetchAny();
	    
	    assertEquals("77722690D", record.get(REGISTRY.DOCUMENT));
	    assertEquals("30132614207", record.get(ENTERPRISE_CCC.CCC));
	    assertEquals(9329,  (int) record.get(ENTERPRISE_ACTIVITY.CNAE2009));
	    assertEquals("CASTAÑO CARRASCO JOSE MIGUEL", record.get(REGISTRY.NAME));
	    assertEquals("CASTAÑO CARRASCO JOSE MIGUEL", record.get(DOMAIN.DESCRIPTION));
	    assertEquals("ABARAN", record.get(RADDRESS.CITY));
	    //assertEquals("08019", record.get(RADDRESS.MUNICIPALITY_CODE));
	    assertEquals("30550", record.get(RADDRESS.ZIP));
	    assertEquals("CL MANUEL AUSENSI 37 1 E", record.get(RADDRESS.ADDRESS));
	    
	    assertEquals(domainId, record.get(DOMAIN.ID) );
	    assertEquals(registryId, record.get(REGISTRY.ID) );
	    assertEquals(workplaceId, record.get(WORKPLACE.ID) );
	    assertEquals(raddressId, record.get(RADDRESS.ID) );
	    assertEquals(enterpriseCccId, record.get(ENTERPRISE_CCC.ID) );
	    assertEquals(payrollWorkplaceId, record.get(PAYROLL_WORKPLACE.ID) );
	    assertEquals(enterpriseActivityId, record.get(ENTERPRISE_ACTIVITY.ID) );
	    
	    dslContext
	    .select()
	    .from(PERSON)
	    .innerJoin(REGISTRY).onKey()
	    .where(PERSON.DOMAIN.eq(domainId))
	    .fetchStream()
	    .forEach(r -> {
		Record mapR = personRegistryMap.get(r.get(PERSON.REGISTRY));
		System.out.println( personRegistryMap.containsKey(r.get(PERSON.REGISTRY)) + " person : " + r.get(PERSON.NAME) + " " + r.get(PERSON.FIRST_SURNAME) + " " + r.get(PERSON.SECOND_SURNAME) + ", registry : " + r.get(PERSON.REGISTRY) + ", document : " + r.get(REGISTRY.DOCUMENT));
		assertEquals(mapR.get(PERSON.NAME), r.get(PERSON.NAME));
		assertEquals(mapR.get(PERSON.FIRST_SURNAME), r.get(PERSON.FIRST_SURNAME));
		assertEquals(mapR.get(PERSON.SECOND_SURNAME), r.get(PERSON.SECOND_SURNAME));
		assertEquals(mapR.get(REGISTRY.DOCUMENT), r.get(REGISTRY.DOCUMENT));
		assertEquals(mapR.get(PERSON.SOCIAL_SECURITY_NUM), r.get(PERSON.SOCIAL_SECURITY_NUM));
		
	    });
	    
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .leftJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(monthDays).on(CONTRACT.ID.eq(monthDays.CONTRACT).and(monthDays.NAME.eq("DIAS_MES")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .forEach(r -> {
		Record mapR = contractMap.get(r.get(CONTRACT.ID));
		assertEquals(mapR.get(CONTRACT.PERSON), r.get(CONTRACT.PERSON));
		assertEquals(mapR.get(CONTRACT.WORKPLACE), r.get(CONTRACT.WORKPLACE));
		assertEquals(mapR.get(CONTRACT.ENTERPRISE_CCC), r.get(CONTRACT.ENTERPRISE_CCC));
		assertEquals(mapR.get(CONTRACT.ENTERPRISE_ACTIVITY), r.get(CONTRACT.ENTERPRISE_ACTIVITY));
		assertEquals(mapR.get(CONTRACT.START_DATE), r.get(CONTRACT.START_DATE));
		assertEquals(mapR.get(CONTRACT.END_DATE), r.get(CONTRACT.END_DATE));

		assertEquals(mapR.get(tc2.EXPRESSION), r.get(tc2.EXPRESSION));
		assertEquals(mapR.get(quoteGroup.EXPRESSION), r.get(quoteGroup.EXPRESSION));
	    });
	    
	    
	}
	try (InputStream is = IvlTest.class.getResourceAsStream("ivlcccIII.pdf")) {

	    contractMap
	    .entrySet()
	    .stream()
	    .skip(66)
	    .limit(200)
	    .map(Entry::getValue)
	    .forEach( r -> {
		dslContext
		.delete(CONTRACT_DATA)
		.where(CONTRACT_DATA.ID.in(
			r.get(quoteGroup.ID)
			, r.get(tc2.ID)
			, r.get(coeficienteParcialidad.ID)))
		.execute();
		dslContext
		.delete(CONTRACT)
		.where(CONTRACT.ID.in(r.get(CONTRACT.ID)))
		.execute();
		
	    });

	    JooqIvl2Contract jooqIvl2Contract =  
		    new JooqIvl2Contract(dslContext, domainName);
		    
	    IvlcccParser.parse(is, jooqIvl2Contract);
	    
	    Map<String, Record > newContractMap  =
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).onKey()
	    .innerJoin(tc2).on(CONTRACT.ID.eq(tc2.CONTRACT).and(tc2.NAME.eq("TC2")))
	    .innerJoin(quoteGroup).on(CONTRACT.ID.eq(quoteGroup.CONTRACT).and(quoteGroup.NAME.eq("GRUPO_COTIZACION")))
	    .leftJoin(coeficienteParcialidad).on(CONTRACT.ID.eq(coeficienteParcialidad.CONTRACT).and(coeficienteParcialidad.NAME.eq("COEFICIENTE_PARCIALIDAD")))
	    .where(CONTRACT.DOMAIN.eq(domainId))
	    .fetchStream()
	    .collect(Collectors.toMap(r -> r.get(PERSON.SOCIAL_SECURITY_NUM)+"/"+r.get(CONTRACT.START_DATE), r -> r));

	    assertEquals(contractMap.size(), newContractMap.size());
	    
	    contractMap
	    .entrySet()
	    .stream()
	    .map(Entry::getValue)
	    .forEach( r -> {
		Record newMapR = newContractMap.get(r.get(PERSON.SOCIAL_SECURITY_NUM)+"/"+r.get(CONTRACT.START_DATE));
		
		//assertNotEquals(newMapR.get(CONTRACT.ID), r.get(CONTRACT.ID));

		assertEquals(newMapR.get(CONTRACT.PERSON), r.get(CONTRACT.PERSON));
		assertEquals(newMapR.get(CONTRACT.WORKPLACE), r.get(CONTRACT.WORKPLACE));
		assertEquals(newMapR.get(CONTRACT.ENTERPRISE_CCC), r.get(CONTRACT.ENTERPRISE_CCC));
		assertEquals(newMapR.get(CONTRACT.ENTERPRISE_ACTIVITY), r.get(CONTRACT.ENTERPRISE_ACTIVITY));
		assertEquals(newMapR.get(CONTRACT.START_DATE), r.get(CONTRACT.START_DATE));
		assertEquals(newMapR.get(CONTRACT.END_DATE), r.get(CONTRACT.END_DATE));

		assertEquals(newMapR.get(tc2.EXPRESSION), r.get(tc2.EXPRESSION));
		assertEquals(newMapR.get(quoteGroup.EXPRESSION), r.get(quoteGroup.EXPRESSION));
	    });
	    
	    
	}
	    
	    
	    
	    
    }

    private DSLContext getDslContext() {
	Settings settings = new Settings();
	settings.setRenderSchema(false);
	settings.setParamType(ParamType.INLINED);
	return DSL.using(getConnection(), settings);
    }
    
    private static Date parseDate(String str) {
	try {
	    return new SimpleDateFormat("dd-MM-yyyy").parse(str);
	} catch (ParseException e) {
	    fail(e.getMessage());
	    return null;
	}	
    }

	public static final EnterpriseActivityRecord newEnterpriseActivity4Enterprise(AONContext aonContext, int domainId, int enterpriseId,
			SSRegimeType ssRegimeType, String cnae2025) {

		EnterpriseActivityRecord enterpriseActivity = 
				aonContext.getDslContext().insertInto(ENTERPRISE_ACTIVITY)
				.set(ENTERPRISE_ACTIVITY.DOMAIN, domainId)
				.set(ENTERPRISE_ACTIVITY.ENTERPRISE, enterpriseId)
				.set(ENTERPRISE_ACTIVITY.DESCRIPTION, "")
				.set(ENTERPRISE_ACTIVITY.TYPE, (byte) ssRegimeType.ordinal())
				.set(ENTERPRISE_ACTIVITY.CNAE, cnae2025 != null ? Integer.parseInt(cnae2025) : null )
				.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2025 != null ? Integer.parseInt(cnae2025) : null )
				.returning().fetchOne();

		return enterpriseActivity;

	}

}
