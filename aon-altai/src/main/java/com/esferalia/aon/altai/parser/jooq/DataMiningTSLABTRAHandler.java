package com.esferalia.aon.altai.parser.jooq;

import static com.esferalia.aon.altai.parser.jooq.JooqTSLABEMPHandler.getAlias;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.altai.parser.TSLABTRAHandler;
import com.esferalia.aon.jooq.tables.records.AgreementLevelDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DataMiningTSLABTRAHandler implements TSLABTRAHandler {
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");
	private static final Pattern FULL_NAME_PATTERN = Pattern.compile("(?<firstsurname>[^\\s]+)\\s+(?<secondsurname>[^\\s,]+)[\\s,]+(?<name>.*)");
	private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("(?<firstsurname>[^\\s]+)\\s+(?<name>[^\\s]+)");
	
	
	File file;
	DSLContext dslContext;
	
	Map<String,Map<String,Integer>> foundmap ; 



	public DataMiningTSLABTRAHandler(Connection connection, File file) {
		this.file = file;

		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		foundmap = new HashMap<String,Map<String,Integer>>();
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
		
		String alias = getAlias(em01000, file);

		Integer domainId = 
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(COMPANY).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.ALIAS.eq(alias))
		.fetchOne(DOMAIN.ID)
		;
		
		if ( domainId != null )
			return;
		
		Date startdate = null ;
		try {
			startdate  = new Date(SIMPLE_DATE_FORMAT.parse(tra.get("startdate")).getTime());
		} catch (Throwable t) {
			return;
		}

		List<ContractRecord> contracts =
		dslContext
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.DOCUMENT.eq(document))
		.and(CONTRACT.START_DATE.eq(startdate))
		.fetchInto(CONTRACT)
		;
		
		if ( contracts.isEmpty())
			return;
		
		if ( contracts.size() > 1)
			return;
		
		String fullname = tra.get("fullname"); 

//		System.out.printf("TRA: %s %s\r\n", document, fullname );

		ContractRecord contract = contracts.get(0);
		
//		List<ContractDataRecord> contractDatas =
//		dslContext
//		.select()
//		.from(CONTRACT_DATA)
//		.where(CONTRACT_DATA.CONTRACT.eq(contract.getId()))
//		.fetchInto(CONTRACT_DATA)
//		;
//		
//		contractDatas.stream()
//		.forEach(r -> {
//			System.out.println("\t" + r.getName() + " = " + r.getExpression());
//		});

		
		
		
		List<AgreementLevelDataRecord> 
		agreementPayments =
		dslContext
		.select()
		.from(AGREEMENT_LEVEL_DATA)
		.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(contract.getAgreementLevel()))
		.fetchInto(AGREEMENT_LEVEL_DATA)
		;
		
		
		agreementPayments.stream()
		.forEach(r -> {
			
			try {
				double amount = Double.parseDouble(r.getExpression());
				amountsMap.entrySet().stream()
				.filter( e -> Math.abs(e.getValue() - amount) < 0.1 )
				.forEach( e -> {
					System.out.printf("TRA: %s %s\r\n", document, fullname );
					System.out.println("\t" + r.getName() + " = " + r.getExpression());
					System.out.println("\t\t" + e.getKey() + " = " + e.getValue());
					Map<String, Integer> map = foundmap.getOrDefault(r.getName(), new HashMap<String,Integer>()); 
					map.put(e.getKey(), map.getOrDefault(e.getKey(), 0) + 1);
				} );
				;
			} catch ( Exception e ) {
			}
		});

//		List<AgreementPaymentRecord> 
//		agreementPayments =
//		dslContext
//		.select()
//		.from(AGREEMENT_LEVEL)
//		.innerJoin(AGREEMENT_PAYMENT)
//		.on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT_PAYMENT.AGREEMENT))
//		.where(AGREEMENT_LEVEL.ID.eq(contract.getAgreementLevel()))
//		.fetchInto(AGREEMENT_PAYMENT)
//		;
//		
//		agreementPayments.stream()
//		.forEach(r -> {
//			System.out.println("\t" + r.getDescription() + " = " + r.getExpression());
//		});
		
//		List<Record> dayHours = new ArrayList<Record>(7);  
//		dayHours.add(contractData.get(ContextVariable.MONDAY_HOURS.getName()));
//		dayHours.add(contractData.get(ContextVariable.TUESDAY_HOURS.getName()));
//		dayHours.add(contractData.get(ContextVariable.WEDNESDAY_HOURS.getName()));
//		dayHours.add(contractData.get(ContextVariable.THURSDAY_HOURS.getName()));
//		dayHours.add(contractData.get(ContextVariable.FRIDAY_HOURS.getName()));
//		dayHours.add(contractData.get(ContextVariable.SATURDAY_HOURS.getName()));
//		dayHours.add(contractData.get(ContextVariable.SUNDAY_HOURS.getName()));
//		
//		dayHours.stream()
//		.filter(r -> r != null )
//		.map(r->r.get(CONTRACT_DATA.EXPRESSION))
//		.collect(Collectors.summingDouble(s -> { try { return Double.parseDouble(s); } catch (Exception e) { return 0.00; }}))
//		;
			
		
		Optional.ofNullable(contract.getAgreementLevel())
		.ifPresentOrElse(
		(level) -> {
			
		}, 
		() -> {
		});
		
		
	}
	
}
