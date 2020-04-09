 package com.esferalia.aon.altai.parser.jooq;

import static com.esferalia.aon.altai.parser.jooq.JooqTSLABEMPHandler.insert;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.altai.parser.TSLABTRAHandler;
import com.esferalia.aon.altai.utils.Utils;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqTSLABTRAHandler implements TSLABTRAHandler {
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final Pattern FULL_NAME_PATTERN = Pattern.compile("(?<firstsurname>[^\\s]+)\\s+(?<secondsurname>[^\\s,]+)[\\s,]+(?<name>.*)");
	private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("(?<firstsurname>[^\\s,]+)[\\s,]+(?<name>.*)");
	
	
	File file;
	java.util.Date fromdate;
	DSLContext dslContext;
	
	Map<String, Integer> aliasDomainMap;
	List<InsertOnDuplicateSetMoreStep<?>> inserts;

	
	
	public JooqTSLABTRAHandler(Connection connection, java.util.Date fromdate, File file) {
		Settings settings;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		this.file = file;
		this.fromdate = fromdate;
		this.inserts = new ArrayList<InsertOnDuplicateSetMoreStep<?>>();
		this.aliasDomainMap = dslContext.select().from(REGISTRY)
		.where(REGISTRY.ALIAS.like(JooqTSLABEMPHandler.getAlias("%",file)))
		.fetchMap(REGISTRY.ALIAS, REGISTRY.DOMAIN);

	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void tra(Map<String, String> tra, Map<String, Double> amountsMap) {
		String em01000 = tra.get("em01.000");
		if ( AonStringUtils.isBlank(em01000)) 
			return;
		
		String document = tra.get("document");
		if ( AonStringUtils.isBlank(document) )
			return;
		
		document = AonStringUtils.upperCase(document);
		
		String fullname = tra.get("fullname"); 

		String alias = JooqTSLABEMPHandler.getAlias(em01000, file);
		String tc2 = tra.get("tc2");
		String cno = tra.get("cno");
		String category = tra.getOrDefault("category", tra.get("categoryy"));
		String quoteGroup = null;
		try {
			quoteGroup = String.format("%02d", Integer.parseInt(tra.get("quote_group")));
		} catch (Throwable t) {
			quoteGroup =tra.get("quote_group");
		}

		
		
		Date startdate = null ;
		try {
			startdate  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("startdate")).getTime());
		} catch (Throwable t) {
			System.out.printf(
			"0,\"%s\",\"%s\",\"%s\",\"\",\"\",\"\",\"%s\",\"%s\",\"%s\",\"%s\",\"\"\r\n", 
			alias,
			document, 
			fullname,
			tc2,
		    quoteGroup,
			category,
			AonStringUtils.defaultIfBlank(tra.get("startdate"), "")
			);
			return;
		}
		
		Date enddate = null ;
		try {
			enddate  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("enddate")).getTime());
			if ( enddate.compareTo(startdate) < 0 )
				enddate = null;
			else if ( enddate.compareTo(fromdate) < 0 )
				return;
			
		} catch (Throwable t) {
			
		}
		

		
		Integer domainId = 
		aliasDomainMap.get(alias)
//		dslContext
//		.select()
//		.from(REGISTRY)
//		.where(REGISTRY.ALIAS.equalIgnoreCase(alias))
//		.fetchOptional(REGISTRY.DOMAIN)
//		.orElseGet(()-> null)
		;
		
		
		String name  = null;
		String firstSurName = null ;
		String secondSurName = null;
		
		Matcher fullNameMatcher = FULL_NAME_PATTERN.matcher(fullname);
		if  ( fullNameMatcher.matches() ) {
			name = fullNameMatcher.group("name");
			firstSurName = fullNameMatcher.group("firstsurname");
			secondSurName = fullNameMatcher.group("secondsurname");
		} else { 
			Matcher firstNameMatcher = FIRST_NAME_PATTERN.matcher(fullname);
			if  ( firstNameMatcher.matches() ) {
				name = firstNameMatcher.group("name");
				firstSurName = firstNameMatcher.group("firstsurname");
			}
		}
		
		
		if ( domainId == null ) {
			System.out.printf(
			"0,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%10$td/%10$tm/%10$ty\",\"\"\r\n", 
			alias,
			document, 
			fullname,
			AonStringUtils.defaultIfBlank(name,""),
			AonStringUtils.defaultIfBlank(firstSurName,""),
			AonStringUtils.defaultIfBlank(secondSurName,""),
			AonStringUtils.defaultIfBlank(tc2, ""),
			AonStringUtils.defaultIfBlank( quoteGroup, ""),
			AonStringUtils.defaultIfBlank( category, ""),
			startdate
			);
			return;
		}
		
		SelectConditionStep<Record1<Integer>> registryId = 
		DSL
		.select(REGISTRY.ID)
		.from(DOMAIN)
		.innerJoin(REGISTRY).onKey()
		.where(DOMAIN.ID.eq(domainId))
		.and(REGISTRY.DOCUMENT.equalIgnoreCase( document ))
		;
		
		
		
		String nationality =  null;
		Byte documentType = Utils.getType(document);
		if ( documentType == Utils.DNI 
			||documentType == Utils.NIF
			||documentType == Utils.CIF)
			nationality = "ES";

		inserts.add(
		dslContext.insertInto(REGISTRY)
		.set(REGISTRY.ID, registryId)
		.set(REGISTRY.DOMAIN, domainId )
		.set(REGISTRY.TYPE, (byte) 0 ) 
		.set(REGISTRY.NAME, fullname)
		.set(REGISTRY.DOCUMENT, document)
		.set(REGISTRY.DOCUMENT_COUNTRY, "ES")
		.set(REGISTRY.DOCUMENT_TYPE, documentType)
		.set(REGISTRY.NATIONALITY, nationality)
		.onDuplicateKeyUpdate()
		.set(REGISTRY.DOMAIN, domainId )
		.set(REGISTRY.TYPE, (byte) 0 ) 
		.set(REGISTRY.NAME, fullname)
		.set(REGISTRY.DOCUMENT, document)
		.set(REGISTRY.DOCUMENT_COUNTRY, "ES")
		.set(REGISTRY.DOCUMENT_TYPE, documentType)
		.set(REGISTRY.NATIONALITY, nationality)
		)
		;
		
		String ss = tra.get("ss");
		Date birthdate = null ;
		try {
			birthdate  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("birthdate")).getTime());
		} catch (Throwable t) {
		}
		
		inserts.add(
		dslContext.insertInto(PERSON)
		.set(PERSON.REGISTRY, registryId)
		.set(PERSON.DOMAIN, domainId )
		.set(PERSON.BIRTH_DATE, birthdate )
		.set(PERSON.SOCIAL_SECURITY_NUM, ss )
		.set(PERSON.NAME, name )
		.set(PERSON.FIRST_SURNAME, firstSurName )
		.set(PERSON.SECOND_SURNAME, secondSurName )
		.onDuplicateKeyUpdate()
		.set(PERSON.DOMAIN, domainId )
		.set(PERSON.BIRTH_DATE, birthdate )
		.set(PERSON.SOCIAL_SECURITY_NUM, ss )
		.set(PERSON.NAME, name )
		.set(PERSON.FIRST_SURNAME, firstSurName )
		.set(PERSON.SECOND_SURNAME, secondSurName )
		)
		;
		
		
		
		PayrollWorkplaceRecord workplace = dslContext
		.select()
		.from(PAYROLL_WORKPLACE)
		.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
		.fetchOptionalInto(PAYROLL_WORKPLACE)
		.orElseGet(()-> null)
		;

		
		SelectConditionStep<Record1<Integer>> contractId = 
		DSL
		.select(CONTRACT.ID)
		.from(CONTRACT)
		.where(CONTRACT.DOMAIN.eq(domainId))
		.and(CONTRACT.PERSON.eq( registryId))
		.and(CONTRACT.WORKPLACE.eq( workplace.getWorkplace()))
		.and(CONTRACT.START_DATE.eq( startdate ))
		;

		Date senioritydate = null ;
		try {
			senioritydate  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("senioritydate")).getTime());
		} catch (Throwable t) {
		}
		
		
		SelectConditionStep<Record1<Integer>> enterpriseActivityId = 
		DSL
		.select(ENTERPRISE_ACTIVITY.ID)
		.from(ENTERPRISE_ACTIVITY)
		.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domainId))
		;

		SelectConditionStep<Record1<Integer>> enterpriseCccId = 
		DSL
		.select(ENTERPRISE_CCC.ID)
		.from(ENTERPRISE_CCC)
		.where(ENTERPRISE_CCC.DOMAIN.eq(domainId))
		;
		

		inserts.add(
		dslContext.insertInto(CONTRACT)
		.set(CONTRACT.ID, contractId )
		.set(CONTRACT.PERSON, registryId )
		.set(CONTRACT.DOMAIN, domainId )
		.set(CONTRACT.WORKPLACE, workplace.getWorkplace() )
		.set(CONTRACT.START_DATE, startdate)
		.set(CONTRACT.END_DATE, enddate)
		.set(CONTRACT.SENIORITY_DATE, senioritydate)
		.set(CONTRACT.ENTERPRISE_ACTIVITY, enterpriseActivityId)
		.set(CONTRACT.ENTERPRISE_CCC, enterpriseCccId)
		.set(CONTRACT.CATEGORY_DESCRIPTION, category)
		.onDuplicateKeyUpdate()
		.set(CONTRACT.PERSON, registryId )
		.set(CONTRACT.DOMAIN, domainId )
		.set(CONTRACT.WORKPLACE, workplace.getWorkplace() )
		.set(CONTRACT.START_DATE, startdate)
		.set(CONTRACT.END_DATE, enddate)
		.set(CONTRACT.SENIORITY_DATE, senioritydate)
		.set(CONTRACT.ENTERPRISE_ACTIVITY, enterpriseActivityId)
		.set(CONTRACT.ENTERPRISE_CCC, enterpriseCccId)
		.set(CONTRACT.CATEGORY_DESCRIPTION, category)
		)
		;

		insertContractData("TC2", tc2, domainId, startdate, contractId, enddate);
		insertContractData("CNO", cno, domainId, startdate, contractId, enddate);
		insertContractData("GRUPO_COTIZACION", quoteGroup, domainId, startdate, contractId, enddate);
		

		System.out.printf(
		"%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%11$td/%11$tm/%11$ty\",", 
		domainId,
		alias,
		document, 
		fullname,
		AonStringUtils.defaultIfBlank(name,""),
		AonStringUtils.defaultIfBlank(firstSurName,""),
		AonStringUtils.defaultIfBlank(secondSurName,""),
		AonStringUtils.defaultIfBlank(tc2, ""),
		AonStringUtils.defaultIfBlank( quoteGroup, ""),
		AonStringUtils.defaultIfBlank( category, ""),
		startdate
		);
		
		if ( enddate != null )
			System.out.printf(
					"\"%1$td/%1$tm/%1$ty\"\r\n", 
					enddate
					);
		else 
			System.out.printf(
					"\"%s\"\r\n",
					AonStringUtils.defaultIfBlank(tra.get("enddate"), "")
					);
			
	}
	
	
	private void insertContractData(String name, String value, Integer domainId, Date startdate,
			SelectConditionStep<Record1<Integer>> contractId, Date enddate) {
		
		if ( AonStringUtils.isBlank(value))
			return;

		SelectConditionStep<Record1<Integer>> id = 
		DSL
		.select(CONTRACT_DATA.ID)
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(contractId))
		.and(CONTRACT_DATA.NAME.eq(name))
		;

		inserts.add(
		dslContext.insertInto(CONTRACT_DATA)
		.set(CONTRACT_DATA.ID, id )
		.set(CONTRACT_DATA.CONTRACT, contractId )
		.set(CONTRACT_DATA.DOMAIN, domainId )
		.set(CONTRACT_DATA.START_DATE, startdate)
		.set(CONTRACT_DATA.END_DATE, enddate)
		.set(CONTRACT_DATA.NAME, name)
		.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"", value))
		.onDuplicateKeyUpdate()
		.set(CONTRACT_DATA.CONTRACT, contractId )
		.set(CONTRACT_DATA.DOMAIN, domainId )
		.set(CONTRACT_DATA.START_DATE, startdate)
		.set(CONTRACT_DATA.END_DATE, enddate)
		.set(CONTRACT_DATA.NAME, name)
		.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"", value))
		)
		;
	}
	
	


	
	// ------------------------------------------------------------------------

	public void toSQL(PrintStream os) {
		insert(inserts, i -> os.println(i.getSQL()));
	}
	
	public void execute() {
		dslContext.transaction((configuration) -> {
			
			insert(inserts, InsertFinalStep::execute);
			
//			throw new RollbackException();
		});
	}
	
	public static void main(String[] args) throws ParseException {
		System.out.println(SIMPLE_DATE_FORMAT.parse("2019-10-01"));
	}
	
	

}
