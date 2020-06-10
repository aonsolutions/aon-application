 package com.esferalia.aon.in.payroll.altai.jooq;

import static com.esferalia.aon.in.payroll.altai.jooq.JooqTSLABEMPHandler.insert;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.InsertOnDuplicateSetStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.altai.TSLABTRAHandler;
import com.esferalia.aon.in.payroll.utils.Utils;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqTSLABTRAHandlerII implements TSLABTRAHandler {
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final Pattern FULL_NAME_PATTERN = Pattern.compile("(?<firstsurname>[^\\s]+)\\s+(?<secondsurname>[^\\s,]+)[\\s,]+(?<name>.*)");
	private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("(?<firstsurname>[^\\s,]+)[\\s,]+(?<name>.*)");
	
	
	File file;
	java.util.Date fromdate;
	DSLContext dslContext;
	
	Map<String, Integer> aliasDomainMap;
	List<InsertFinalStep<?>> inserts;
	
	BiFunction<InsertSetMoreStep<?>, Function<InsertOnDuplicateSetStep<?>, InsertOnDuplicateSetMoreStep<?>>, InsertFinalStep<?> > onduplicateKey;
	
	
	public JooqTSLABTRAHandlerII(Connection connection, java.util.Date fromdate, File file) {
		Settings settings;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		this.file = file;
		this.fromdate = fromdate;
		this.inserts = new ArrayList<InsertFinalStep<?>>();
		this.aliasDomainMap = dslContext.select().from(REGISTRY)
		.where(REGISTRY.ALIAS.like(JooqTSLABEMPHandler.getAlias("%",file)))
		.fetchMap(REGISTRY.ALIAS, REGISTRY.DOMAIN);
		onduplicateKey = JooqTSLABTRAHandlerII::onDuplicateKeyIgnore;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void tra(Map<String, String> tra, Map<String, Double> amountsMap) {
		String em01000 = tra.get("em01.000");
		if ( AonStringUtils.isBlank(em01000)) 
			return;
		
		String document = AonStringUtils.upperCase(tra.get("document"));
		if ( AonStringUtils.isBlank(document) )
			return;
		
		
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

		
		
		Date date = null ;
		try {
			date  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("startdate")).getTime());
		} catch (Throwable t) {
//			System.err.printf(
//			"0,\"%s\",\"%s\",\"%s\",\"\",\"\",\"\",\"%s\",\"%s\",\"%s\",\"%s\",\"\"\r\n", 
//			alias,
//			document, 
//			fullname,
//			tc2,
//		    quoteGroup,
//			category,
//			AonStringUtils.defaultIfBlank(tra.get("startdate"), "")
//			);
//			return;
		}
		Date startdate= date;
		
		date = null ;
		try {
			date  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("enddate")).getTime());
			if ( date.compareTo(startdate) < 0 )
				date = null;
			else if ( date.compareTo(fromdate) < 0 )
				return;
			
		} catch (Throwable t) {
			
		}
		Date enddate = date;

		String ss = tra.get("ss");
		
		
		Integer domainId = 
		Optional.ofNullable(aliasDomainMap.get(alias)).orElseGet(() -> {
		try {
			return 
			dslContext
			.select()
			.from(REGISTRY)
			.where(REGISTRY.DOMAIN.ne(0))
			.and(REGISTRY.DOCUMENT.equalIgnoreCase( document ))
			.fetchOptional(REGISTRY.DOMAIN)
			.orElseThrow();
		} catch (NoSuchElementException | DataAccessException e) {
			return null;
		}
		}
		);
		
		
		String names [] = new String[3];
		Matcher fullNameMatcher = FULL_NAME_PATTERN.matcher(fullname);
		if  ( fullNameMatcher.matches() ) {
			names[0] = fullNameMatcher.group("name");
			names[1] = fullNameMatcher.group("firstsurname");
			names[2] = fullNameMatcher.group("secondsurname");
		} else { 
			Matcher firstNameMatcher = FIRST_NAME_PATTERN.matcher(fullname);
			if  ( firstNameMatcher.matches() ) {
				names[0]= firstNameMatcher.group("name");
				names[1] = firstNameMatcher.group("firstsurname");
			}
		}
		
		String name  = names[0];
		String firstSurName = names[1] ;
		String secondSurName = names[2];

		
		if ( domainId == null ) {
//			System.err.printf(
//			"-1,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%10$td/%10$tm/%10$ty\",\"\"\r\n", 
//			alias,
//			document, 
//			fullname,
//			AonStringUtils.defaultIfBlank(name,""),
//			AonStringUtils.defaultIfBlank(firstSurName,""),
//			AonStringUtils.defaultIfBlank(secondSurName,""),
//			AonStringUtils.defaultIfBlank(tc2, ""),
//			AonStringUtils.defaultIfBlank( quoteGroup, ""),
//			AonStringUtils.defaultIfBlank( category, ""),
//			startdate
//			);
			return;
		}
				
		SelectConditionStep<Record1<Integer>> registryId = 
		DSL
		.select(REGISTRY.ID)
		.from(REGISTRY)
		.where(REGISTRY.DOMAIN.eq(domainId))
		.and(REGISTRY.DOCUMENT.equalIgnoreCase( document ))
		;
		
		
		date = null ;
		try {
			date  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("senioritydate")).getTime());
		} catch (Throwable t) {
		}
		Date senioritydate = date;
		

		List<Integer> contractIds = 
		dslContext
		.select()
		.from(CONTRACT)
		.where(CONTRACT.DOMAIN.eq(domainId))
		.and(CONTRACT.PERSON.eq( registryId))
		.and(CONTRACT.START_DATE.eq( startdate )
		.or(CONTRACT.SENIORITY_DATE.eq(senioritydate))
		)
		.fetch(CONTRACT.ID)		
		;
		
		if ( contractIds.isEmpty() && enddate == null ) {
			
			contractIds = 
			dslContext
			.select()
			.from(CONTRACT)
			.where(CONTRACT.DOMAIN.eq(domainId))
			.and(CONTRACT.PERSON.eq( registryId))
			.and(CONTRACT.START_DATE.ge( startdate ))
			.fetch(CONTRACT.ID)		
			;
		}else if ( contractIds.isEmpty() && enddate != null ) {
			
			contractIds = 
			dslContext
			.select()
			.from(CONTRACT)
			.where(CONTRACT.DOMAIN.eq(domainId))
			.and(CONTRACT.PERSON.eq( registryId))
			.and(CONTRACT.START_DATE.le( enddate ))
			.and(CONTRACT.END_DATE.ge(startdate))
			.fetch(CONTRACT.ID)		
			;

		}
		
		if ( contractIds.isEmpty() ) {
			

			(contractIds.isEmpty() ? System.err : System.out).printf(
			"-2,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%10$td/%10$tm/%10$ty\",\"\"\r\n", 
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
		
		for (Integer contractId : contractIds) {
			insertContractData("TC2", tc2, domainId, startdate, contractId, enddate);
			insertContractData("CNO", cno, domainId, startdate, contractId, enddate);
			insertContractData("GRUPO_COTIZACION", quoteGroup, domainId, startdate, contractId, enddate);
		}
		

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
			Integer contractId, Date enddate) {
		
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
		onduplicateKey.apply(
		dslContext.insertInto(CONTRACT_DATA)
		.set(CONTRACT_DATA.ID, id )
		.set(CONTRACT_DATA.CONTRACT, contractId )
		.set(CONTRACT_DATA.DOMAIN, domainId )
		.set(CONTRACT_DATA.START_DATE, startdate)
		.set(CONTRACT_DATA.END_DATE, enddate)
		.set(CONTRACT_DATA.NAME, name)
		.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"", value)),
		insert -> insert
		.set(CONTRACT_DATA.CONTRACT, contractId )
		.set(CONTRACT_DATA.DOMAIN, domainId )
		.set(CONTRACT_DATA.START_DATE, startdate)
		.set(CONTRACT_DATA.END_DATE, enddate)
		.set(CONTRACT_DATA.NAME, name)
		.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"", value))
		));
	}
	
	
	public static InsertFinalStep<?>  onDuplicateKeyIgnore(InsertSetMoreStep<?> insert, Function<InsertOnDuplicateSetStep<?>, InsertOnDuplicateSetMoreStep<?>> update) {
		return insert.onDuplicateKeyIgnore();
	}
	
	private  static InsertFinalStep<?>  onDuplicateKeyUpdate(InsertSetMoreStep<?> insert, Function<InsertOnDuplicateSetStep<?>, InsertOnDuplicateSetMoreStep<?>> update) {
		return update.apply(insert.onDuplicateKeyUpdate());
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
