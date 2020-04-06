package com.esferalia.aon.altai.parser.jooq;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSelectStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.altai.parser.TSLABEMPHandler;
import com.esferalia.aon.altai.utils.Municipalities;
import com.esferalia.aon.altai.utils.Utils;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqTSLABEMPHandler implements TSLABEMPHandler {
	
	{
		println(System.out);
	}
	
	private static class ByOtherUser extends Exception {
		
		private Record records [];
		
		public ByOtherUser(Record records []) {
			this.records = records;
		}
		
		public void println() {
			Arrays
			.stream(records)
			.forEach( r -> 
					System.out.printf("\"%d\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\r\n", 
					r.get(DOMAIN.ID), 
					r.get(REGISTRY.ID),
					r.get(DOMAIN.NAME), 
					r.get(REGISTRY.DOCUMENT), 
					AonStringUtils.defaultIfBlank(r.get(REGISTRY.NAME),""), 
					AonStringUtils.defaultIfBlank(r.get(DOMAIN.CREATION_USER), ""),
					AonStringUtils.defaultIfBlank(r.get(DOMAIN.MODIFICATION_USER), "") ,
					AonStringUtils.defaultIfBlank(r.get(REGISTRY.ALIAS) , "")
					)
			);
		}
		
		
		
	}
	
	private static class InsertedByOtherUser extends ByOtherUser {

		public InsertedByOtherUser(Record[] records) {
			super(records);
		}
		
	}
	
	private static class UpdatedByOtherUser extends ByOtherUser {

		public UpdatedByOtherUser(Record[] records) {
			super(records);
		}
		

	}
	
	
	private static void println(PrintStream os) {
		{
			os.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\r\n", 
			"domain.id", 
			"registry.id",
			"domain.name", 
			"registry.document", 
			"registry.name", 
			"domain.creation_user",
			"domain.modification_user" ,
			"registry.alias"
			);			
		}		
	}
	
	File file;
	Integer scopeId;
	String creationUser;
	byte enableHeredity;

	DSLContext dslContext;
	String parentDomainName;
	String domainNamePreffix; 
	
	DomainRecord parentDomainRecord;
	
	List<InsertOnDuplicateSetMoreStep<?>> inserts;
	
	static final byte RADDRESS_MAIN = 0;
	static final byte RADDRESS_DELEGATION = 1;
	
	public JooqTSLABEMPHandler(Connection connection, String parentDomainName, String domainNamePreffix, File file) {
		Settings settings;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		this.file = file;
		this.enableHeredity = 1;
		this.creationUser = "altai2aon";
		
		this.parentDomainName = parentDomainName;
		this.domainNamePreffix = domainNamePreffix;
		
		inserts = new ArrayList<InsertOnDuplicateSetMoreStep<?>>();

		parentDomainRecord = dslContext.select().from(DOMAIN).where(DOMAIN.NAME.eq(parentDomainName)).fetchOneInto(DOMAIN);
		
		this.scopeId = parentDomainRecord.getScope(); 
		
		

	}

	@Override
	public void emp01(Map<String, String> emp01) {
		
		String doc = emp01.get("document");
		if ( AonStringUtils.isBlank(doc))
			return; 
		
		//016004390R
		String document = doc
		.trim()
		.replaceAll("^0([0-9a-zA-Z]{9})$", "$1")
		.toUpperCase();
		
		RegistryRecord registry =
		dslContext
		.select()
		.from(REGISTRY)
		.where(REGISTRY.DOMAIN.eq(0))
		.and(REGISTRY.DOCUMENT.equalIgnoreCase(document))
		.fetchOptionalInto(REGISTRY)
		.orElseGet(() -> 
		new RegistryRecord(
				null, 
				0, 
				document, 
				Utils.getType(document), 
				"ES", 
				emp01.get("name"), 
				null, 
				null, 
				"ES", 
				null)
		)
		;
		registry.setAlias(getAlias(emp01.get("em01.000"), file));
		

		if ( AonStringUtils.isBlank(registry.getName()))
			return; 
		try {
			CheckIfInsertedByOtherUser(registry.getDocument());
		} catch (InsertedByOtherUser e) {
			e.println();
			return; 
		}

		try {
			CheckIfUpdatedByOtherUser(registry.getDocument());
		} catch (UpdatedByOtherUser e) {
			e.println();
			return; 
		}

		StringBuffer domainNameBuffer = new StringBuffer();
		if ( AonStringUtils.isNotBlank(domainNamePreffix) ) {
			domainNameBuffer.append(domainNamePreffix);
			domainNameBuffer.append('-');
		}
		domainNameBuffer.append(registry.getDocument());
		domainNameBuffer.append('-');
		domainNameBuffer.append(parentDomainName);
		
		String domainName = domainNameBuffer.toString();
		

		inserts.add( 
		dslContext.insertInto(DOMAIN) 
		.set(DOMAIN.NAME, domainName )
		.set(DOMAIN.DESCRIPTION, registry.getName() )
		
		.set(DOMAIN.CREATION_DATE, DSL.now() )
		.set(DOMAIN.CREATION_USER, creationUser )

		.set(DOMAIN.ENABLEHEREDITY, enableHeredity )
		
		.set(DOMAIN.PARENT, parentDomainRecord.getId() )
		.set(DOMAIN.OWNER,  parentDomainRecord.getOwner() )
		
		.onDuplicateKeyUpdate()
		.set(DOMAIN.DESCRIPTION, registry.getName() )
		
		.set(DOMAIN.MODIFICATION_DATE, DSL.now() )
		.set(DOMAIN.MODIFICATION_USER, creationUser )
		
		.set(DOMAIN.ENABLEHEREDITY, enableHeredity )
		
		.set(DOMAIN.PARENT, parentDomainRecord.getId() )
		.set(DOMAIN.OWNER,  parentDomainRecord.getOwner() )
		)
		;
		
		SelectConditionStep<Record1<Integer>> domainId = 
		DSL
		.select(DOMAIN.ID)
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq(domainName))
		;
		
		SelectConditionStep<Record1<Integer>> registryId = 
		DSL
		.select(REGISTRY.ID)
		.from(DOMAIN)
		.innerJoin(REGISTRY).onKey()
		.where(DOMAIN.NAME.eq(domainName))
		.and(REGISTRY.DOCUMENT.eq(registry.getDocument()))
		;
		

		inserts.add(
		dslContext.insertInto(REGISTRY)
		.set(REGISTRY.ID, registryId)
		.set(REGISTRY.DOMAIN, domainId )
		.set(REGISTRY.TYPE, (byte) 1 )
		.set(REGISTRY.NAME, registry.getName())
		.set(REGISTRY.ALIAS, registry.getAlias())
		.set(REGISTRY.DOCUMENT, registry.getDocument())
		.set(REGISTRY.DOCUMENT_COUNTRY, registry.getDocumentCountry())
		.set(REGISTRY.DOCUMENT_TYPE, Utils.getType(registry.getDocument()))
		.onDuplicateKeyUpdate()
		.set(REGISTRY.DOMAIN, domainId )
		.set(REGISTRY.TYPE, (byte) 1 )
		.set(REGISTRY.NAME, registry.getName())
		.set(REGISTRY.ALIAS, registry.getAlias())
		.set(REGISTRY.DOCUMENT, registry.getDocument())
		.set(REGISTRY.DOCUMENT_COUNTRY, registry.getDocumentCountry())
		.set(REGISTRY.DOCUMENT_TYPE, Utils.getType(registry.getDocument()))
		)
		;
		
//		if ( !AonStringUtils.equalsIgnoreCase(emp01.get("name"), registry.getName()) )
//			System.out.printf("Aviso [%s] : '%s' realmente es '%s'.\r\n", 
//					registry.getDocument(), emp01.get("name"), registry.getName());
		
		inserts.add(
		dslContext.insertInto(COMPANY)
		.set(COMPANY.DOMAIN, domainId)
		.set(COMPANY.REGISTRY, registryId)

		.set(COMPANY.ACTIVE, (byte) 1)

		.onDuplicateKeyUpdate()

		.set(COMPANY.ACTIVE, (byte) 1)
		)
		;
		
		inserts.add(
		dslContext.insertInto(ENTERPRISE)
		
		.set(ENTERPRISE.DOMAIN, domainId)
		.set(ENTERPRISE.REGISTRY, registryId)
		
		.set(ENTERPRISE.SCOPE, scopeId)
		
		.onDuplicateKeyUpdate()
		.set(ENTERPRISE.SCOPE, scopeId)
		)
		;
		
		String cnae2009 = emp01.get("cnae2009"); 
		
		SelectConditionStep<Record1<Integer>> cnae2009Id = 
		DSL
		.select(CNAE2009.ID)
		.from(CNAE2009)
		.where(CNAE2009.CODE.eq(cnae2009))
		;
		
		SelectConditionStep<Record1<Integer>> enterpriseActivityId = 
		DSL.select(ENTERPRISE_ACTIVITY.ID)
		.from(ENTERPRISE_ACTIVITY)
		.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domainId))
		.and(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(registryId))
		;
		
		SelectSelectStep<Record1<String>> enterpriseActivityDescription = 
		DSL.select(
		DSL.cast(
		DSL.ifnull(
		DSL.select(
		DSL.ifnull(
		DSL
		.select(DSL.substring(RADDINFO.VALUE,1,64))
		.from(RADDINFO)
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.DOMAIN.eq(0))
		.and(REGISTRY.DOCUMENT.eq(document))
		.and(RADDINFO.ATTRIBUTE.eq("CNAE 2009"))
		,
		DSL
		.select(DSL.substring(CNAE2009.TITLE,1,64))
		.from(CNAE2009)
		.where(CNAE2009.CODE.eq(cnae2009))
		)
		)
		,
		registry.getName()
		)
		,
		String.class
		)
		)
		;

		inserts.add(
		dslContext.insertInto(ENTERPRISE_ACTIVITY)
		.set(ENTERPRISE_ACTIVITY.ID, enterpriseActivityId)
		
		.set(ENTERPRISE_ACTIVITY.DOMAIN, domainId)
		.set(ENTERPRISE_ACTIVITY.ENTERPRISE, registryId)
		.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
		.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte)1)
		.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009Id)
		.set(ENTERPRISE_ACTIVITY.DESCRIPTION, enterpriseActivityDescription)
		
		.onDuplicateKeyUpdate()
		.set(ENTERPRISE_ACTIVITY.DOMAIN, domainId)
		.set(ENTERPRISE_ACTIVITY.ENTERPRISE, registryId)
		.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
		.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte)1)
		.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009Id)
		.set(ENTERPRISE_ACTIVITY.DESCRIPTION, enterpriseActivityDescription)
		)
		;

		SelectConditionStep<Record1<Integer>> enterpriseCCCId = 
		DSL.select(ENTERPRISE_CCC.ID)
		.from(ENTERPRISE_CCC)
		.where(ENTERPRISE_CCC.DOMAIN.eq(domainId))
		.and(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpriseActivityId))
		;
		String ccc = emp01.get("ccc");
		
		SelectConditionStep<Record1<Integer>> cccGeozoneId = 
		DSL
		.select(GEOZONE.ID)
		.from(GEOZONE)
		.where(GEOZONE.DOMAIN.eq(parentDomainRecord.getId()))
		.and(GEOZONE.CODE.eq(AonStringUtils.substring(ccc,0,2)))
		;

		inserts.add(
		dslContext.insertInto(ENTERPRISE_CCC)
		.set(ENTERPRISE_CCC.ID, enterpriseCCCId)
		
		.set(ENTERPRISE_CCC.DOMAIN, domainId)
		.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseActivityId)
		.set(ENTERPRISE_CCC.CCC, ccc)
		.set(ENTERPRISE_CCC.GEOZONE, cccGeozoneId)
		
		.onDuplicateKeyUpdate()
		.set(ENTERPRISE_CCC.DOMAIN, domainId)
		.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseActivityId)
		.set(ENTERPRISE_CCC.CCC, ccc)
		.set(ENTERPRISE_CCC.GEOZONE, cccGeozoneId)
		)
		;

		SelectConditionStep<Record1<Integer>> rAddressId = 
		DSL.select(RADDRESS.ID)
		.from(RADDRESS)
		.where(RADDRESS.DOMAIN.eq(domainId))
		.and(RADDRESS.REGISTRY.eq(registryId))
		.and(RADDRESS.TYPE.eq(RADDRESS_MAIN))
		;
		
		String geozone = emp01.get("geozone");

		RaddressRecord raddress =
		dslContext
		.select()
		.from(RADDRESS)
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.DOMAIN.eq(0))
		.and(REGISTRY.DOCUMENT.eq(document))
		.and(RADDRESS.ZIP.eq(emp01.get("zip")))
		.fetchOptionalInto(RADDRESS)
		.orElseGet(() -> 
		new RaddressRecord(
				null, 						//id, 
				null,						//domain, 
				null,						//registry, 
				RADDRESS_MAIN,				//type, 
				null,						//recipient, 
				emp01.get("street_type"), 	//streetType, 
				emp01.get("address"),		//address, 
				emp01.get("number"), 		//number, 
				null,						//address2, 
				null,						//address3, 
				emp01.get("zip"), 			//zip, 
				null,						//city, 
				null,						//geozone,					 
				null,						//alias, 
				null						//municipalityCode
				)
		);
		
		
		if ( AonStringUtils.isBlank(raddress.getCity() ))
			raddress.setCity(emp01.get("city"));

		if ( AonStringUtils.isNotBlank(raddress.getZip()) 
				&& raddress.getZip().matches("[1-9][0-9]{3}")) {
			raddress.setZip("0" + raddress.getZip());
		}
		
		Municipalities municipalities = new Municipalities();
		if ( AonStringUtils.isBlank(raddress.getMunicipalityCode() ))
			raddress.setMunicipalityCode( municipalities.getCodeByMunicipalityName(raddress.getCity()));

		if ( AonStringUtils.isBlank(raddress.getMunicipalityCode() ))
			raddress.setMunicipalityCode( municipalities.getCodeByMunicipalityNameAndZip(raddress.getZip(), raddress.getCity()));
		
		
		SelectConditionStep<Record1<Integer>> geozoneId = 
		DSL
		.select(GEOZONE.ID)
		.from(GEOZONE)
		.where(GEOZONE.DOMAIN.eq(parentDomainRecord.getId()))
		.and(GEOZONE.CODE.eq(AonStringUtils.substring(raddress.getZip(),0,2)))
		;

		inserts.add(
		dslContext.insertInto(RADDRESS)
		.set(RADDRESS.ID, rAddressId)
		
		.set( RADDRESS.DOMAIN, domainId)
		.set( RADDRESS.REGISTRY, registryId)
		.set( RADDRESS.GEOZONE, geozoneId)
		.set( RADDRESS.TYPE, raddress.getType() )
		.set( RADDRESS.ZIP, raddress.getZip())
		.set( RADDRESS.CITY, raddress.getCity())
		.set( RADDRESS.STREET_TYPE, raddress.getStreetType())
		.set( RADDRESS.ADDRESS, raddress.getAddress())
		.set( RADDRESS.ADDRESS2, raddress.getAddress2())
		.set( RADDRESS.ADDRESS3, raddress.getAddress3())
		.set( RADDRESS.NUMBER, raddress.getNumber())
		.set( RADDRESS.MUNICIPALITY_CODE, raddress.getMunicipalityCode())

		.onDuplicateKeyUpdate()
		.set( RADDRESS.DOMAIN, domainId)
		.set( RADDRESS.REGISTRY, registryId)
		.set( RADDRESS.GEOZONE, geozoneId)
		.set( RADDRESS.TYPE, raddress.getType() )
		.set( RADDRESS.ZIP, raddress.getZip())
		.set( RADDRESS.CITY, raddress.getCity())
		.set( RADDRESS.STREET_TYPE, raddress.getStreetType())
		.set( RADDRESS.ADDRESS, raddress.getAddress())
		.set( RADDRESS.ADDRESS2, raddress.getAddress2())
		.set( RADDRESS.ADDRESS3, raddress.getAddress3())
		.set( RADDRESS.NUMBER, raddress.getNumber())
		.set( RADDRESS.MUNICIPALITY_CODE, raddress.getMunicipalityCode())
		);
		
		SelectConditionStep<Record1<Integer>> workplaceId = 
		DSL.select(WORKPLACE.ID)
		.from(WORKPLACE)
		.where(WORKPLACE.DOMAIN.eq(domainId))
		.and(WORKPLACE.ENTERPRISE.eq(registryId))
		.and(WORKPLACE.ADDRESS.eq(rAddressId))
		;

		inserts.add(
		dslContext.insertInto(WORKPLACE)
		.set(WORKPLACE.ID, workplaceId)
		
		.set(WORKPLACE.DOMAIN, domainId)
		.set(WORKPLACE.ENTERPRISE, registryId)
		.set(WORKPLACE.ADDRESS, rAddressId)
		
		.set(WORKPLACE.SCOPE, scopeId)
		.set(WORKPLACE.ACTIVE, (byte)1)
		.set(WORKPLACE.DESCRIPTION, "PRINCIPAL")
		
		.onDuplicateKeyUpdate()
		.set(WORKPLACE.SCOPE, scopeId)
		.set(WORKPLACE.ACTIVE, (byte)1)
		.set(WORKPLACE.DESCRIPTION, registry.getName())

		)
		;

		SelectConditionStep<Record1<Integer>> payrollWorkplaceId = 
		DSL.select(PAYROLL_WORKPLACE.ID)
		.from(PAYROLL_WORKPLACE)
		.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
		.and(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
		;

		inserts.add(
		dslContext.insertInto(PAYROLL_WORKPLACE)
		.set(PAYROLL_WORKPLACE.ID, payrollWorkplaceId)
		
		.set(PAYROLL_WORKPLACE.DOMAIN, domainId)
		.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceId)
		.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, enterpriseActivityId)
		
		.onDuplicateKeyUpdate()
		.set(PAYROLL_WORKPLACE.DOMAIN, domainId)
		.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceId)
		.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, enterpriseActivityId)
		)
		;
		
		System.out.printf("0,0,\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"%s\"\r\n", 
		domainName, 
		registry.get(REGISTRY.DOCUMENT), 
		registry.get(REGISTRY.NAME), 
		creationUser,
		registry.get(REGISTRY.ALIAS) 
		);
		
	}

	@Override
	public void emp10(Map<String, String> emp10) {
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
	


	private void CheckIfInsertedByOtherUser(String document) 
	throws InsertedByOtherUser {
		Record records [] =
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(COMPANY).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.DOCUMENT.equalIgnoreCase(document))
		.and(DOMAIN.CREATION_USER.ne(creationUser))
		.fetchArray()
		;
		
		if ( records.length > 0 )
			throw new InsertedByOtherUser(records);
	
	}

	private void CheckIfUpdatedByOtherUser(String document)
	throws UpdatedByOtherUser {
		Record records [] =
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(COMPANY).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(REGISTRY.DOCUMENT.equalIgnoreCase(document))
		.and(DOMAIN.MODIFICATION_USER.isNotNull())
		.and(DOMAIN.MODIFICATION_USER.ne(creationUser))
		.fetchArray()
		; 	
		if ( records.length > 0 )
			throw new UpdatedByOtherUser(records);
	}
	
	protected static String getAlias(String em01000, File file) {
		if ( file == null )
			return em01000;
		return String.format("%s-%s",getAlias(file), em01000);
	}
	
	protected static String getAlias(File file) {
		return file.getName()
			   .toUpperCase()
			   .replaceAll("\\.ASC", "")
			   .replaceAll("TSLABTRA", "")
			   .replaceAll("TSLABEMP", "")
			   .trim()
			   .replaceAll("\\s", "_")
			   ;
	}

	protected static void insert(List<InsertOnDuplicateSetMoreStep<? extends Record>> inserts, Consumer<InsertFinalStep<? extends Record>> consumer) {
		 inserts.stream().forEach(consumer);
	}


	public static void main(String[] args) {
//		println(System.out);
	}
}
