package com.esferalia.aon.in.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.xalan.xsltc.compiler.sym;
import org.hibernate.annotations.Where;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectJoinStep;
import org.jooq.TableField;
import org.jooq.Transaction;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.Province;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.in.payroll.ivl.IvlCccParser;
import com.esferalia.aon.in.payroll.ivl.IvlParserListener;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.utils.Utils;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;
import com.esferalia.aon.watson.util.AonStringUtils;

import antlr.collections.List;

import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;



import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;



public class VidaLaboral2AON implements IvlParserListener{
	
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
	static InputStream is = VidaLaboral2AON.class.getResourceAsStream("tmp/document-31.pdf");		
	static Connection connection = null;
	AONContext aonContext = new AONContext(connection);
	DSLContext dslContext = aonContext.getDslContext();
	static Date startDate;
	static Date endDate;
	static String sDate;
	static String eDate;
	static String situationn;
	static String socialReason;
	String fullname;
	String naf;
	String docNum;
	String docType;
	String address;
	static String ccc;
	static String nif;
	String economicActivityCode;
	String domainNamePreffix = "payroll";
	String parentDomainName = "assimilated-payroll-test.aonsolutions.org";
	String creationUser;
	String regime; 
	String economicActivityDescription;
	String pdf;
	String fullName;
	int zone;
	byte enableHeredity;
	PDDocument doc;
	static EnterpriseRecord enterprise;
	static EnterpriseCccRecord enterpriseCcc;
	static PayrollWorkplaceRecord payRollWorkPlace;
	static RegistryRecord person;
	static DomainRecord parentDomain;
	static int domainId = 8776;
		 
		
		

	@SuppressWarnings("static-access")
	public VidaLaboral2AON(DSLContext dslContext, String parentDomainName) {
			super();
			this.dslContext = dslContext;
			this.domainNamePreffix = "payroll";
			this.enableHeredity = 1;
			this.parentDomainName = parentDomainName;
			this.parentDomain = getParentDomain(parentDomainName);
					
			
		}
	public VidaLaboral2AON(DSLContext dslContext) {
		super();
		this.dslContext = dslContext;
		this.enableHeredity = 1;
	}
	
	

	public static void main(String[] args) {
	
		
		Map<Byte, String> regimeMap = new HashMap<Byte, String>(){
			{
				put((byte)6,"0138");
				put((byte)7,"0163");
			}
		};
		
		Option hostOption = Option.builder("h")
				.hasArg()
				.longOpt("host")
				.argName("name")
				.desc("Connect to host.")
				.build();
				
		Option portOption = Option.builder("P")
				.hasArg()
				.longOpt("port")
				.argName("name")
				.desc("Port number to use for connection, default (3306).")
				.build();
		
		Option userOption = Option.builder("u")
				.hasArg()
				.longOpt("user")
				.argName("name")
				.desc("User for login if not current user.")
				.build();
		
		Option passwordOption = Option.builder("p")
				.hasArg()
				.longOpt("password")
				.argName("name")
				.desc("Password to use when connecting to server.")
				.build();
		
		Option databaseOption = Option.builder("d")
				.hasArg()
				.longOpt("database")
				.argName("name")
				.desc("Database to use")
				.build();
		
		Option domainOption = Option.builder("D")
				.hasArg()
				.longOpt("domain")
				.argName("name")
				.desc("Parent domain")
				.build();
		
		Option ivlOption = Option.builder("i")
				.hasArg()
				.longOpt("ivl")
				.argName("name")
				.desc("Informe de vida laboral")
				.build();
		
		Option pdfOption = Option.builder("pdf")
				.hasArg()
				.longOpt("pdf")
				.argName("name")
				.desc("PDF IVL AON")
				.build();
				
		
		Options options = new Options();
		
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(passwordOption);
		options.addOption(databaseOption);
		options.addOption(domainOption);
		options.addOption(ivlOption);
		options.addOption(pdfOption);
		
//		CommandLine commandLine = null;
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine commandLine = parser.parse(options, args);
            String host = commandLine.getOptionValue(hostOption.getLongOpt(),"127.0.0.1");
            String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
            String user = commandLine.getOptionValue(userOption.getLongOpt(),"dbuser");
            String password = commandLine.getOptionValue(passwordOption.getLongOpt(),"serubd2000");
            String database = commandLine.getOptionValue(databaseOption.getLongOpt(),"test-aonsolutions-org");
            String domain = commandLine.getOptionValue(domainOption.getLongOpt(),"payroll.aonsolutions.org");
            String pdf = commandLine.getOptionValue(pdfOption.getLongOpt(),"/tmp/ivl-aon.pdf");
                Properties properties = new Properties();
                properties.setProperty("user", user);
                properties.setProperty("password", password);
                properties.setProperty("useSSL", "false");
                properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
                properties.setProperty("domain", domain);
                String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database,pdf);
                Settings settings;
                settings = new Settings();
                settings.setRenderSchema(false);
                settings.setParamType(ParamType.INLINED);
			try (Connection connection = DriverManager.getConnection(url,properties)){
				
	            DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
                dslContext.select().from(DOMAIN).fetchInto(DOMAIN).forEach( r -> System.out.println(r.getDescription()));
				 VidaLaboral2AON vidaLaboral2AON = new VidaLaboral2AON(dslContext);
					vidaLaboral2AON.import2AON(is, vidaLaboral2AON, pdf);		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(VidaLaboral2AON.class.getSimpleName(), options);

		}
	}
	
	
	

	
	public void import2AON(InputStream is, IvlParserListener ivl, String pdf) throws IOException, UnknownPDFException {
		dslContext.transaction(ctx ->{
				InputStream iis = new FileInputStream(pdf);
				IvlCccParser.parse(iis, ivl);
				});
	}
	
	private Optional <RegistryRecord> getPerson(int domainId, String naf) {
		
		return getDSLContext(dslContext)
				.select()
				.from(REGISTRY)
				.innerJoin(PERSON).onKey()
				.where(REGISTRY.DOMAIN.eq(domainId))
				.and(REGISTRY.DOCUMENT.eq(naf))
				.and(PERSON.SOCIAL_SECURITY_NUM.eq(naf))
				.limit(1)
				.fetchOptionalInto(REGISTRY);
		
	}
	
	
	
	
	
	private RegistryRecord newPerson(int domainId, String ccc, String fullname, String naf) {
		
		RegistryRecord registry = getDSLContext(dslContext)
				.select()
				.from(REGISTRY)
				.where(REGISTRY.DOMAIN.eq(domainId))
				.and(REGISTRY.DOCUMENT.eq(ccc))
				.limit(1).fetchOptionalInto(REGISTRY)
				.orElseGet(() ->{
					String nationality = "ES";
					//nif de empresa, cambiar x trabajador
					Byte documentType = Utils.getType(naf);
					
					
					RegistryRecord r = 
							getDSLContext(dslContext).newRecord(REGISTRY);
					r.setDomain(domainId);
					r.setNationality(nationality);
					r.setDocument(naf);
					r.setDocumentCountry(nationality);
					r.setDocumentType(documentType);
					r.setName(fullname);
					r.setType(type(RegistryType.NATURAL));
					r.setAlias("test-alias");
					r.insert();
					return r;

				});
		//Separar nombres y apellidos
		
		String names [] = Utils.split(fullname);
//		try {
			PersonRecord person = getDSLContext(dslContext).newRecord(PERSON);
			person.setDomain(domainId);
			person.setRegistry(registry.getId());
			person.setName(names[0]);
			person.setFirstSurname(names[1]);
			person.setSecondSurname(names[2]);
			person.setSocialSecurityNum(naf);
			getDSLContext(dslContext).insertInto(PERSON)
			.set(person)
			.execute()
			;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
				
		
		return registry;
		
	}
	
	
	
	private Optional<EnterpriseCccRecord> getEnterpriseCCC(String ccc, String nif){
		return getDSLContext(dslContext)
				.select()
				.from(ENTERPRISE_CCC)
				.innerJoin(ENTERPRISE_ACTIVITY).onKey()
				.innerJoin(ENTERPRISE).onKey()
				.innerJoin(REGISTRY).onKey()
				.where(ENTERPRISE_CCC.CCC.eq(ccc.replaceAll("\\s","").substring(0, 11)))
				.and(REGISTRY.DOCUMENT.eq(nif))
				.orderBy(ENTERPRISE_CCC.ID).limit(1).fetchOptionalInto(ENTERPRISE_CCC)
				;
		
	}
	
	private EnterpriseCccRecord newEnterpriseCCC(  EnterpriseRecord enterprise, String socialReason, String ccc, String nif, String economicActivityDescription, int zone) {
		EnterpriseActivityRecord enterpriseActivity =
				getDSLContext(dslContext)
				.select()
				.from(ENTERPRISE_ACTIVITY)
				.innerJoin(ENTERPRISE).onKey()
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(enterprise.getRegistry()))
				.orderBy(ENTERPRISE_ACTIVITY.ID).limit(1).fetchOptionalInto(ENTERPRISE_ACTIVITY)
				.orElseGet(() -> {
					EnterpriseActivityRecord activity =
					getDSLContext(dslContext)
					.newRecord(ENTERPRISE_ACTIVITY);
					activity.setPrincipal((byte) 1);
					activity.setDomain(enterprise.getDomain());
					activity.setEnterprise(enterprise.getRegistry());
					activity.setDescription(socialReason);
					activity.setType(type(EnterpriseActivityType.PRINCIPAL));
					activity.insert();
					
					return activity;
					
				});		
		
	
		
		EnterpriseCccRecord enterpriseCcc =
				getDSLContext(dslContext)
				.newRecord(ENTERPRISE_CCC);		
		
				enterpriseCcc.setType(type(CCCType.PRINCIPAL)); 
				enterpriseCcc.setDomain(enterpriseActivity.getDomain());
				enterpriseCcc.setCcc(ccc.replaceAll("\\s","").substring(0, 11));
				enterpriseCcc.setEnterpriseActivity(enterpriseActivity.getId());
				enterpriseCcc.setGeozone(zone);
				enterpriseCcc.insert();
//				vl.newWorkPlace(enterprise, enterpriseCcc, nif, economicActivityDescription);
//		}
				return enterpriseCcc;
	}
	
	
	
	
	
	
	private Optional<EnterpriseRecord> getEnterprise(String ccc, String socialReason, String nif){		
		return getDSLContext(dslContext)
					.select()
			        .from(ENTERPRISE)
			        .innerJoin(REGISTRY).onKey()
			        .innerJoin(DOMAIN).onKey(REGISTRY.DOMAIN)
			        .where(REGISTRY.DOCUMENT.eq(nif))
			        .and(DOMAIN.PARENT.eq(domainId))
			        .fetchOptionalInto(ENTERPRISE)
			        ;
		     
	}
	
	
	//Cambiar a private otra vez
	private EnterpriseRecord newEnterprise(String nif, String socialReason, String economicActivityDescription) {	
		VidaLaboral2AON vl = new VidaLaboral2AON(dslContext);

		try {
					return 	 
					getDSLContext(dslContext)
					.selectDistinct()
					.from(ENTERPRISE_CCC)
					.innerJoin(ENTERPRISE_ACTIVITY).onKey()
					.innerJoin(ENTERPRISE).onKey()
					.where(ENTERPRISE_CCC.CCC.eq(ccc))
					.orderBy(ENTERPRISE_CCC.ID).limit(1)
					.fetchOptionalInto(ENTERPRISE)
					.orElseGet(() -> {
						
						DomainRecord domain =
								getDSLContext(dslContext).newRecord(DOMAIN);
						
								DomainRecord parentDomain = vl.getParentDomain(parentDomainName);
								domain.setCreationUser(parentDomain.getCreationUser());

								domain.setParent(parentDomain.getParent());
				                domain.setOwner(parentDomain.getOwner());

				                domain.setMaxdocumentsize(1);
				                domain.setMaxtotaldocumentsize(100);
				                domain.setMaxdefinedusers(0);
				                domain.setLastaccessUser(parentDomain.getLastaccessUser());
				                domain.setLastaccessDate(parentDomain.getLastaccessDate());
								domain.setEnableheredity(enableHeredity);
								domain.setActive((byte) 0);
								domain.setScope(parentDomain.getScope());
								domain.setDescription(parentDomain.getDescription());
								domain.setCreationDate(new Timestamp(System.currentTimeMillis()));
//								domain.setName(String.format("%s-%s-%s", domainNamePreffix, contract.getCif(), parentDomainName));
								domain.setName(String.format("%s-%s-%s", domainNamePreffix, "test.aonsolutions", "-.org"));

								domain.insert();
			
								String nationality = "ES";
								Byte documentType = Utils.getType(nif);
				
				
								RegistryRecord r3gistry = 
										getDSLContext(dslContext).newRecord(REGISTRY);
								r3gistry.setType(type(RegistryType.LEGAL));
								r3gistry.setDomain(domain.getId());
								r3gistry.setNationality(nationality);
								r3gistry.setDocument(nif);
								r3gistry.setDocumentCountry(nationality);
								r3gistry.setDocumentType(documentType);
								r3gistry.setName(socialReason);
								r3gistry.setAlias("");
								r3gistry.insert();
					
								EnterpriseRecord enterprise =
								getDSLContext(dslContext).newRecord(ENTERPRISE);
									enterprise.setDomain(domain.getId());
									enterprise.setRegistry(r3gistry.getId());
									enterprise.setScope(parentDomain.getScope());
									enterprise.insert();
	

								
								
								return enterprise;
						
				
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return enterprise;
	

	}
	
	private Optional <PayrollWorkplaceRecord> getWorkplace(int domainId, String nif){		

		return getDSLContext(dslContext)
				.select()
				.from(PAYROLL_WORKPLACE)
				.innerJoin(WORKPLACE).onKey()
				.innerJoin(ENTERPRISE).onKey()
				.innerJoin(REGISTRY).onKey()
				.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
				.orderBy(PAYROLL_WORKPLACE.ID)
				.limit(1)
				.fetchOptionalInto(PAYROLL_WORKPLACE);
	}
	
	private PayrollWorkplaceRecord newWorkPlace(EnterpriseRecord enterprise, EnterpriseCccRecord enterpriseCcc, String economicActivityCode) {
		
		RaddressRecord raddress = getDSLContext(dslContext)
				.select()
				.from(RADDRESS)
				.innerJoin(ENTERPRISE).on()
				.where(RADDRESS.REGISTRY.eq(ENTERPRISE.REGISTRY))
				.orderBy(RADDRESS.ID).limit(1).fetchOptionalInto(RADDRESS)
				.orElseGet(() -> {
					
					RaddressRecord raddres = getDSLContext(dslContext).newRecord(RADDRESS);
					raddres.setType(type(AddressType.MAIN));
					raddres.setRegistry(enterprise.getRegistry());
					raddres.setDomain(enterprise.getDomain());
					raddres.setAddress(address);
					raddres.insert();
					return raddres;
				} );
		
		WorkplaceRecord workplace = getDSLContext(dslContext)
				.select()
				.from(WORKPLACE)
				.innerJoin(ENTERPRISE).on()
				.innerJoin(ENTERPRISE_CCC).on()
				.where(WORKPLACE.ENTERPRISE.eq(ENTERPRISE.REGISTRY))
				.orderBy(WORKPLACE.ID).limit(1).fetchOptionalInto(WORKPLACE)
				.orElseGet(() ->{
					
					WorkplaceRecord workplac3 =
					getDSLContext(dslContext).newRecord(WORKPLACE);
					workplac3.setDescription("PRINCIPAL");
					workplac3.setDomain(enterprise.getDomain());
					workplac3.setEnterprise(enterprise.getRegistry());
					workplac3.setScope(enterprise.getScope());
					workplac3.setAddress(raddress.getId());
					workplac3.insert();
					
					return workplac3;

				});

		PayrollWorkplaceRecord payrollWorkplace = 
				getDSLContext(dslContext).newRecord(PAYROLL_WORKPLACE);
				payrollWorkplace.setWorkplace(workplace.getId());
				payrollWorkplace.setDomain(workplace.getDomain());
				payrollWorkplace.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());				
				payrollWorkplace.insert();
		return payrollWorkplace;

	}
	
	
	//TENER EN CUENTA CASOS DONDE G:C/M = 10/M  	 (VALOR DE NAME EN CONTRACT_DATA DIAS_MES)
	private ContractRecord newContract(EnterpriseCccRecord enterpriseCcc, RegistryRecord person, PayrollWorkplaceRecord workplace, 
			String start, String end,String startSit ,String effectSit, String gc, String tc, String ctp, String cotDays)    {
		System.out.println(person.getId());
		System.out.println(workplace.getWorkplace());
		 Record existingContract = getDSLContext(dslContext)
		            .select().from(CONTRACT).innerJoin(CONTRACT_DATA)
		            .onKey()
		            .where(CONTRACT.PERSON.eq(person.getId())
		                    .and(CONTRACT.WORKPLACE.eq(workplace.getWorkplace()))
		                    .and(CONTRACT.START_DATE.eq(CONTRACT_DATA.START_DATE))).limit(1)
		            .fetchOne();
		 
		 ContractRecord contract = 
					getDSLContext(dslContext).newRecord(CONTRACT);
			
			ContractDataRecord quoteGroupData =
					getDSLContext(dslContext).newRecord(CONTRACT_DATA);
		 if (existingContract != null) {
			 System.out.println("el contrato ya existe");

		 }else {
				

				String situacion = getSituation(situationn);
				java.util.Date utilDate;
				java.sql.Date sqlDate;
				//CASO DONDE EL TRABAJADOR ESTA DE BAJA
				if (situacion.equals("BAJA ")) {
					 contract = 
							getDSLContext(dslContext).newRecord(CONTRACT);
					contract.setDomain(enterpriseCcc.getDomain());
					contract.setPerson(person.getId());
					contract.setWorkplace(workplace.getWorkplace());
					contract.setEnterpriseCcc(enterpriseCcc.getId());
					contract.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());
					String startDate = getStartDate(start);
					String endDate = getEndDate(startSit);
					
					try {
						utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
						sqlDate = new java.sql.Date(utilDate.getTime());
						contract.setStartDate(sqlDate);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(endDate);
						sqlDate = new java.sql.Date(utilDate.getTime());
						contract.setEndDate(sqlDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
					byte baja = 0;
					contract.setSsStatus(baja);
					contract.setRegistration(null);
					contract.setCategoryDescription("prueba-BAJA");
					contract.setSsRegime(type(SSRegimeType.GENERAL));
					contract.insert();
					
					//INSERT PARA GRUPO DE COTIZACION
					quoteGroupData =
							getDSLContext(dslContext).newRecord(CONTRACT_DATA);	
					quoteGroupData.setDomain(contract.getDomain());
					quoteGroupData.setContract(contract.getId());
					quoteGroupData.setName(ContextVariable.QUOTE_GROUP.getName());
					quoteGroupData.setStartDate(contract.getStartDate());
					quoteGroupData.setEndDate(contract.getEndDate());
					quoteGroupData.setExpression(String.format("\"%s\"", gc));
					quoteGroupData.insert();
					
					//INSERT PARA TC2
					 quoteGroupData =
							getDSLContext(dslContext).newRecord(CONTRACT_DATA);	
					quoteGroupData.setDomain(contract.getDomain());
					quoteGroupData.setContract(contract.getId());
					quoteGroupData.setName(ContextVariable.TC2.getName());
					quoteGroupData.setStartDate(contract.getStartDate());
					quoteGroupData.setEndDate(contract.getEndDate());
					quoteGroupData.setExpression(String.format("\"%s\"", tc));
					quoteGroupData.insert();
					//INSERT PARA COEFICIENTE DE PARCIALIDAD
					quoteGroupData =
							getDSLContext(dslContext).newRecord(CONTRACT_DATA);	
					quoteGroupData.setDomain(contract.getDomain());
					quoteGroupData.setContract(contract.getId());
					quoteGroupData.setName(ContextVariable.PARTIAL_FACTOR.getName());
					quoteGroupData.setStartDate(contract.getStartDate());
					quoteGroupData.setEndDate(contract.getEndDate());
					if (ctp == null) {
						quoteGroupData.setExpression(ctp);

					}else {
						quoteGroupData.setExpression(ctp.replaceAll(",", "."));

					}
					quoteGroupData.insert();
					

					//CASO DONDE EL TRABAJADOR ESTA EN ALTA
				}else if(situacion.equals("ALTA ")){
					 contract = 
							getDSLContext(dslContext).newRecord(CONTRACT);
					contract.setDomain(enterpriseCcc.getDomain());
					contract.setPerson(person.getId());
					contract.setWorkplace(workplace.getWorkplace());
					contract.setEnterpriseCcc(enterpriseCcc.getId());
					contract.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());
					String startDate = getStartDate(start);
			
					try {
						utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
						sqlDate = new java.sql.Date(utilDate.getTime());
						contract.setStartDate(sqlDate);	
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
					byte alta = 1;
					contract.setSsStatus(alta);
					contract.setRegistration(null);
					contract.setCategoryDescription("prueba-ALTA");
					contract.setSsRegime(type(SSRegimeType.GENERAL));
					contract.insert();
					//INSERT PARA GRUPO DE COTIZACION

					quoteGroupData =
							getDSLContext(dslContext).newRecord(CONTRACT_DATA);
					quoteGroupData.setDomain(contract.getDomain());
					quoteGroupData.setContract(contract.getId());
					quoteGroupData.setName(ContextVariable.QUOTE_GROUP.getName());
					quoteGroupData.setStartDate(contract.getStartDate());
					quoteGroupData.setEndDate(contract.getEndDate());
					quoteGroupData.setExpression(String.format("\"%s\"", gc));
					quoteGroupData.insert();
					//INSERT PARA TC2

					quoteGroupData =
							getDSLContext(dslContext).newRecord(CONTRACT_DATA);	
					quoteGroupData.setDomain(contract.getDomain());
					quoteGroupData.setContract(contract.getId());
					quoteGroupData.setName(ContextVariable.TC2.getName());
					quoteGroupData.setStartDate(contract.getStartDate());
					quoteGroupData.setEndDate(contract.getEndDate());
					quoteGroupData.setExpression(String.format("\"%s\"", tc));
					quoteGroupData.insert();
					//INSERT PARA COEFICIENTE DE PARCIALIDAD

					quoteGroupData =
							getDSLContext(dslContext).newRecord(CONTRACT_DATA);	
					quoteGroupData.setDomain(contract.getDomain());
					quoteGroupData.setContract(contract.getId());
					quoteGroupData.setName(ContextVariable.PARTIAL_FACTOR.getName());
					quoteGroupData.setStartDate(contract.getStartDate());
					quoteGroupData.setEndDate(contract.getEndDate());
					if (ctp == null) {
						quoteGroupData.setExpression(ctp);

					}else {
						quoteGroupData.setExpression(ctp.replaceAll(",", "."));

					}
					quoteGroupData.insert();
					
					
					
					
				//CASO PARA LOS DIAS DE VACACIONES
				}else if(situacion.equals("VAC.RETRIB.NO ")) {
					
					 	contract = 
								getDSLContext(dslContext).newRecord(CONTRACT);
					 
						contract.setDomain(enterpriseCcc.getDomain());
						contract.setPerson(person.getId());
						contract.setWorkplace(workplace.getWorkplace());
						contract.setEnterpriseCcc(enterpriseCcc.getId());
						contract.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());
						String startDate = getStartDate(start);
						try {
							utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
							sqlDate = new java.sql.Date(utilDate.getTime());
							contract.setStartDate(sqlDate);
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
									
						contract.setSsStatus(null);
						contract.setRegistration(null);
						contract.setCategoryDescription("prueba-VACACIONES");
						contract.setSsRegime(type(SSRegimeType.GENERAL));
						contract.insert();
					
					quoteGroupData =
							getDSLContext(dslContext).newRecord(CONTRACT_DATA);
					quoteGroupData.setDomain(contract.getDomain());
					quoteGroupData.setContract(contract.getId());
					quoteGroupData.setName(ContextVariable.NO_HOLIDAYS.getName());
					quoteGroupData.setExpression(cotDays);

					String effectDate = getStartDate(start);
					try {
						utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(effectDate);
						sqlDate = new java.sql.Date(utilDate.getTime());
						quoteGroupData.setStartDate(sqlDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					try {
						start = start.substring(7,10);
						end = end + start;
						effectDate = getStartDate(end);
						utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(effectDate);
						sqlDate = new java.sql.Date(utilDate.getTime());
						quoteGroupData.setEndDate(sqlDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					quoteGroupData.insert();
				}
		 }
		
	
		 
		return contract;
		}
	
		private Optional<ContractRecord> getContract(RegistryRecord person, PayrollWorkplaceRecord workplace, EnterpriseCccRecord enterprise){
			//contract_data.contract
			System.out.println(person.getId() +" id");
			 return getDSLContext(dslContext)
			            .select()
			            .from(CONTRACT)
			            .where(CONTRACT.PERSON.eq(person.getId()))
			            .and(CONTRACT.WORKPLACE.eq(workplace.getWorkplace()))
			            .and(CONTRACT.ENTERPRISE_CCC.eq(enterprise.getId()))
			            .fetchOptionalInto(CONTRACT);
				
		}
		
		
	public String getSituation(String situation) {
		return situation;
	}
	
	public String getStartDate(String start) {
		return start;
	}
	public String getEndDate(String end) {
		return end;
	}	
	

	
	private DomainRecord getParentDomain(String parentDomainName){
		return getDSLContext(dslContext).select().from(DOMAIN).where(DOMAIN.NAME.eq(parentDomainName)).fetchOneInto(DOMAIN);	
		
	}
	
	private GeozoneRecord getGeoZone(String provCode){
		return getDSLContext(dslContext).select().from(GEOZONE).where(GEOZONE.CODE.eq(provCode)).and(GEOZONE.DOMAIN.eq(domainId)).fetchOneInto(GEOZONE);
	}
	
	
	private DSLContext getDSLContext(DSLContext ds) {
		return ds;
		
	}
	
	protected static <E extends Enum<?>> byte type(E constant) {
		return (byte) constant.ordinal();
	}
	
	public static Province safeValueOf( Integer i ) {
		if (i == null) {
			return null;
		}
		if (i < 0 || i >= Province.values().length) {
			return null;
		}
		return Province.values()[i];
		
	}
	
	public static Province safeValueOf(String provCode) {
		if (AonStringUtils.isBlank(provCode)) {
			return null;
		}
		provCode = AonStringUtils.trim(provCode);
		if (!AonStringUtils.isNumeric(provCode)) {
			return null;
		}
		int i = Integer.parseInt(provCode);
		return safeValueOf(i);
		
	}
	
	
	
	@Override
	public void onEnterprise(String socialReason, String ccc, String regime, String nif, String economicActivityCode,
			String economicActivityDescription, String fullCCC) {
		
		VidaLaboral2AON on = new VidaLaboral2AON(dslContext);
		
		enterprise = getEnterprise(ccc, socialReason, nif)
				.orElseGet(()-> newEnterprise(nif, socialReason, economicActivityDescription));

		String provCode = ccc.substring(0,2);
		VidaLaboral2AON.safeValueOf(provCode);
		GeozoneRecord zoneCode =on.getGeoZone(provCode);
		zone = zoneCode.getId();
		
		enterpriseCcc = getEnterpriseCCC(ccc, nif)
				.orElseGet(()-> newEnterpriseCCC(enterprise, socialReason, ccc, nif, economicActivityDescription, zone));
		
		payRollWorkPlace = getWorkplace(domainId, nif)
				.orElseGet(()-> newWorkPlace(enterprise, enterpriseCcc, economicActivityCode));
		
	}
	@Override
	public void onEnterpriseAddress(String city, String address, String cp, String economicActivityCode ) {

	}
	@Override
	public void onEnterprisePeriod(String startDate, String endDate) {
		
	}
	@Override
	public void onAtTypes(String it, String ims, String total) {

	}
	@Override
	public void onEmployee(String naf, String docType, String docNum, String fullName) {
		 person = getPerson(domainId, naf).orElseGet(()-> newPerson(domainId, docNum, fullName, naf));
		
	}
	
	//MANTENER EL ORDEN EN EL QUE ENTRAN LOS DATOS
	@Override
	public void onEmployeeIdent(String nss, String ident) {
		
	}
	@Override
	public void onEmployeeSituation(String situation, String start, String effect, String startSit, String effectSit,
			String gc, String tc,String ctp, String ep, String it, String ims, String total, String cotDays, String clv) {
		situationn = getSituation(situation);
		System.out.println(situationn);
//		ContractRecord contract = getContract(person, payRollWorkPlace, enterpriseCcc)
//				.orElseGet(() -> newContract(enterpriseCcc, person, payRollWorkPlace, start, effect, startSit, effectSit, gc, tc, ctp, cotDays));
//		
//		
		
		System.out.println(person.getId());
		ContractRecord contract = newContract(enterpriseCcc, person, payRollWorkPlace, start,effect, startSit,effectSit,gc, tc, ctp, cotDays);
		
	}
	
	
}


