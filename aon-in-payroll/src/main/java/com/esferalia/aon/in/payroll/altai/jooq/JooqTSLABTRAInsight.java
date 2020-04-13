package com.esferalia.aon.in.payroll.altai.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.PrintStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqTSLABTRAInsight {
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");
	
	Condition where;
	DSLContext dslContext;
	
	List<UpdateConditionStep<?>> updates; 
	
	
	
	public JooqTSLABTRAInsight(Connection connection, Condition where) {
		
		this.where = where;

		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		this.dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		updates = new ArrayList<UpdateConditionStep<?>>();
		
		
	}
	
	
	
	// ------------------------------------------------------------------------
	
	public void insight(Integer contractId, Integer workplaceId, String ...categories ) {
		
		
		

		Integer agreementLevel = getAgreementLevel(dslContext, workplaceId, categories);
		
		if ( agreementLevel == null )
			return;
		
		System.out.println(String.format("Sets agreement '%d' for '%s' (%S)", 
		agreementLevel, contractId ,Arrays.stream(categories).collect(Collectors.joining(","))));
		
		updates.add(
		dslContext.update(CONTRACT)
		.set(CONTRACT.AGREEMENT_LEVEL, agreementLevel)
		.where(CONTRACT.ID.eq(contractId))
		)
		;
		
		
	}
	
	private Integer getAgreementLevel(DSLContext dslContext, Integer workplace, String document ) {
		List<Integer> agreementLevels = 
		dslContext
		.selectDistinct(CONTRACT.AGREEMENT_LEVEL)
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.DOCUMENT.eq(document))
		.fetch(CONTRACT.AGREEMENT_LEVEL)
		;
		
		if ( agreementLevels.isEmpty() )
			return null;
		
		if ( agreementLevels.size() == 1 )
			return agreementLevels.get(0);

		agreementLevels = 
		dslContext
		.selectDistinct(CONTRACT.AGREEMENT_LEVEL)
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.DOCUMENT.eq(document))
		.and(CONTRACT.WORKPLACE.eq(workplace))
		.fetch(CONTRACT.AGREEMENT_LEVEL)
		;

		if ( agreementLevels.size() == 1 )
			return agreementLevels.get(0);
		
		return null;
	}

	private Integer getAgreementLevel(DSLContext dslContext, Integer workplace,  String ...categories) {
		
		RaddressRecord raddress =
		dslContext.select()
		.from(WORKPLACE)
		.join(RADDRESS).onKey()
		.where(WORKPLACE.ID.eq(workplace))
		.fetchOneInto(RADDRESS)
		;

		if ( raddress == null ) {
			return null;
		}

		String categoriesPattern =
				Arrays.stream(categories)
				.filter(c -> AonStringUtils.isNotBlank(c))
				.map(c -> c.toUpperCase().trim()).distinct()
				.map(c -> c.replace("?", "\\?"))
				.collect(Collectors.joining("|"))
				;
		
		if ( AonStringUtils.isBlank(categoriesPattern)) { 
			return null;
		}
		
		String exactPattern = "^[[:space:]]*(" + categoriesPattern + ")[[:space:]]*$"; 
		
		List<Integer> availAgreements = new ArrayList<Integer>();
		List<Integer> availAgreementLevels = new ArrayList<Integer>();
		
		dslContext
		.selectDistinct()
		.from(AGREEMENT_LEVEL)
		.innerJoin(CONTRACT).onKey()
		.where(DSL.upper(CONTRACT.CATEGORY_DESCRIPTION).likeRegex(exactPattern))
		.fetchStreamInto(AGREEMENT_LEVEL)
		.forEach((agreementLevel) -> {
			availAgreementLevels.add(agreementLevel.getId());
			availAgreements.add(agreementLevel.getAgreement());
		});
		
		if ( availAgreementLevels.isEmpty() ) {
			
			dslContext
			.selectDistinct()
			.from(AGREEMENT_LEVEL)
			.innerJoin(AGREEMENT_LEVEL_CATEGORY).onKey()
			.where(AGREEMENT_LEVEL_CATEGORY.ID.ge(0))
			.and(DSL.upper(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION).likeRegex(exactPattern))
			.fetchStreamInto(AGREEMENT_LEVEL)
			.forEach((agreementLevel) -> {
				availAgreementLevels.add(agreementLevel.getId());
				availAgreements.add(agreementLevel.getAgreement());
			});
		}
		
		if ( availAgreementLevels.isEmpty() ) {
			System.err.println("Unknow category : " + Arrays.stream(categories).collect(Collectors.joining(",")) );	
			return null;
		}
		
		// Are there any near contract with any of these levels ?
		List<Integer> candidateAgreementLevels =
				dslContext
				.selectDistinct(CONTRACT.AGREEMENT_LEVEL)
				.from(CONTRACT)
				.innerJoin(WORKPLACE).onKey()
				.innerJoin(RADDRESS).onKey()
				.where(RADDRESS.GEOZONE.eq(raddress.getGeozone()))
				.and(CONTRACT.AGREEMENT_LEVEL.in(availAgreementLevels))
				.fetch(CONTRACT.AGREEMENT_LEVEL)
				;
		
		if ( candidateAgreementLevels.isEmpty() ) {
			
			// None with leves. 
			// Are there any near contract with any these agreements ?
			SelectConditionStep<Record1<Integer>> allAvailAgreementLevels = 
					DSL
					.selectDistinct(AGREEMENT_LEVEL.ID)
					.from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.AGREEMENT.in(availAgreements));
			
			candidateAgreementLevels =
					dslContext
					.selectDistinct(CONTRACT.AGREEMENT_LEVEL)
					.from(CONTRACT)
					.innerJoin(WORKPLACE).onKey()
					.innerJoin(RADDRESS).onKey()
					.where(RADDRESS.GEOZONE.eq(raddress.getGeozone()))
					.and(CONTRACT.AGREEMENT_LEVEL.in(allAvailAgreementLevels))
					.fetch(CONTRACT.AGREEMENT_LEVEL)
					;
		}
		
		// Are there any 'near' agreement with any of these levels ?
		if ( candidateAgreementLevels.isEmpty() ) {
			
			candidateAgreementLevels =
					dslContext
					.selectDistinct(AGREEMENT_LEVEL.ID)
					.from(AGREEMENT)
					.innerJoin(AGREEMENT_LEVEL).onKey()
					.where(AGREEMENT_LEVEL.ID.in(availAgreementLevels))
					.and(AGREEMENT.DESCRIPTION.containsIgnoreCase(DSL.field(DSL.select(GEOZONE.NAME).from(GEOZONE).where(GEOZONE.ID.eq(raddress.getGeozone())))))
					.fetch(AGREEMENT_LEVEL.ID)
					;
			
		}

		if ( candidateAgreementLevels.isEmpty() ) {
			//System.out.println("Unknow category : " + exactPattern + " at " + dslContext.select().from(GEOZONE).where(GEOZONE.ID.eq(raddress.getGeozone())).fetch(GEOZONE.NAME));	
			return null; 
		}
		
		if ( candidateAgreementLevels.size() == 1)
			return candidateAgreementLevels.get(0);
		
		List<Integer> srcCandidateAgreementLevels = 
				candidateAgreementLevels;

		// Filter 'near' agreement's levels
		candidateAgreementLevels =
		dslContext
		.selectDistinct(AGREEMENT_LEVEL.ID)
		.from(AGREEMENT)
		.innerJoin(AGREEMENT_LEVEL).onKey()
		.where(AGREEMENT_LEVEL.ID.in(srcCandidateAgreementLevels))
		.and(AGREEMENT.DESCRIPTION.containsIgnoreCase(DSL.field(DSL.select(GEOZONE.NAME).from(GEOZONE).where(GEOZONE.ID.eq(raddress.getGeozone())))))
		.fetch(AGREEMENT_LEVEL.ID)
		;

		if ( candidateAgreementLevels.isEmpty() ) {
			return null; 
		}
		
		if ( candidateAgreementLevels.size() == 1)
			return candidateAgreementLevels.get(0);
		

//		candidateAgreementLevels =
//		dslContext
//		.selectDistinct(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL)
//		.from(AGREEMENT_LEVEL_CATEGORY)
//		.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(srcCandidateAgreementLevels))
//		.and(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION.likeRegex(exactPattern))
//		.fetch(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL)
//		;
//
//		if ( candidateAgreementLevels.size() == 1)
//			return candidateAgreementLevels.get(0);

//		candidateAgreementLevels =
//		dslContext
//		.selectDistinct(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL)
//		.from(AGREEMENT_LEVEL_CATEGORY)
//		.innerJoin(AGREEMENT_LEVEL).onKey()
//		.where(AGREEMENT_LEVEL.AGREEMENT.in(
//				DSL.select(AGREEMENT_LEVEL.AGREEMENT)
//				.from(AGREEMENT_LEVEL)
//				.where( AGREEMENT_LEVEL.ID.in(srcCandidateAgreementLevels))))
//		.and(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION.likeRegex(exactPattern))
//		.fetch(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL)
//		;
//		
//		if ( candidateAgreementLevels.size() == 1)
//			return candidateAgreementLevels.get(0);

		String message = 
		dslContext.select(AGREEMENT.ID, AGREEMENT.DESCRIPTION, AGREEMENT_LEVEL.DESCRIPTION)
		.from(AGREEMENT).innerJoin(AGREEMENT_LEVEL).onKey().where(AGREEMENT_LEVEL.ID.in(srcCandidateAgreementLevels))
		.fetchStream().map(r -> String.format("%d - '%s'", r.value1(), r.value2())).distinct().collect(Collectors.joining(","));
		;
		
		
		System.err.println(
				"Too many agreement levels for " 
//				+ " (" + srcCandidateAgreementLevels.stream().map(i -> i.toString()).collect(Collectors.joining(",")) +")"
				+ " " + Arrays.stream(categories).filter(c -> AonStringUtils.isNotBlank(c)).distinct().collect(Collectors.joining(",")) + " " + message  
		);

//		candidateAgreementLevels =
//			dslContext.selectDistinct(CONTRACT.AGREEMENT_LEVEL)
//			.from(CONTRACT)
//			.innerJoin(PAYROLL_WORKPLACE).on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
//			.innerJoin(ENTERPRISE_ACTIVITY).on(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
//			.innerJoin(CNAE2009).on(ENTERPRISE_ACTIVITY.CNAE2009.eq(CNAE2009.ID))
//			.where(CONTRACT.AGREEMENT_LEVEL.in(candidateAgreementLevels))
//			.and(CNAE2009.CODE.eq(
//					DSL.select(CNAE2009.CODE)
//					.from(PAYROLL_WORKPLACE)
//					.innerJoin(ENTERPRISE_ACTIVITY).onKey()
//					.innerJoin(CNAE2009).onKey()
//					.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplace))
//			))
//			.fetch(CONTRACT.AGREEMENT_LEVEL);
//			;
//		if ( candidateAgreementLevels.isEmpty() ) {
//			return null; 
//		}
//		if ( candidateAgreementLevels.size() == 1) {
//			System.out.println("One and only one agreement with same CNAE");
//			return candidateAgreementLevels.get(0);
//		}
		
		
		return null;
	}
	
	
	protected static void update(List<UpdateConditionStep<? extends Record>> inserts, Consumer<UpdateConditionStep<? extends Record>> consumer) {
		 inserts.stream().forEach(consumer);
	}

	// ------------------------------------------------------------------------

	public void toSQL(PrintStream os) {
		update(updates, i -> os.println(i.getSQL()));
	}
	
	public void execute() {
		
		dslContext
		.select()
		.from(CONTRACT)
		.innerJoin(DOMAIN).onKey()
		.where(where)
		.and(CONTRACT.CATEGORY_DESCRIPTION.likeRegex("[^[:space:]]"))
		.fetchStreamInto(CONTRACT)
		.forEach((contract) -> insight(contract.getId() , contract.getWorkplace(), contract.getCategoryDescription() ) );
		
		dslContext.transaction((configuration) -> {
			
			update(updates, UpdateConditionStep::execute);
			
//			throw new RollbackException();
		});
	}
	
	

}
