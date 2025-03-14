package net.aonsolutions.aon.in.adiss;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Cnae.CNAE;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static net.aonsolutions.aon.in.adiss.Adiss2Salary.insertExtra;
import static net.aonsolutions.aon.in.adiss.Adiss2Salary.insertSalary;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.CompanyRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.SalaryCostRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDeductionRecord;
import com.esferalia.aon.jooq.tables.records.SalaryPaymentRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.querydsl.sql.mssql.SQLServerQueryFactory;

import net.aonsolutions.adiss.querydsl.CENTROS;
import net.aonsolutions.adiss.querydsl.CONCEPTOSREGISTROS;
import net.aonsolutions.adiss.querydsl.EMPRESAS;
import net.aonsolutions.adiss.querydsl.NOMINASCONCEPTOS;
import net.aonsolutions.adiss.querydsl.QCENTROS;
import net.aonsolutions.adiss.querydsl.QCONCEPTOSREGISTROS;
import net.aonsolutions.adiss.querydsl.QEMPRESAS;
import net.aonsolutions.adiss.querydsl.QINCIDENCIAS;
import net.aonsolutions.adiss.querydsl.QNOMINAS;
import net.aonsolutions.adiss.querydsl.QNOMINASCONCEPTOS;
import net.aonsolutions.adiss.querydsl.QNOMINASEXT;
import net.aonsolutions.adiss.querydsl.QTRABAJADORES;

public class Adiss2Aon {

	private static final String SUFFIX = ".aonsolutions.org";

	public static void main(String[] args) throws SQLException, IOException {
		Connection sqlServerConnection = DriverManager.getConnection(
				"jdbc:sqlserver://aon.adisscloud.es;databaseName=3e891185-f826-4f06-9b53-614a7d69628c;nullCatalogMeansCurrent=true;encrypt=false",
				"dataloop", "Data123$%");
		Connection mySqlConnection = DriverManager.getConnection(
				"jdbc:mysql://172.17.0.1:3306/adiss-aonsolutions-org?useSSL=false", "dbuser", "serubd2000");

		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext mysqlDslContext = DSL.using(mySqlConnection, SQLDialect.MYSQL, settings);

		SQLServerQueryFactory sqlServerQueryFactory = new SQLServerQueryFactory(() -> sqlServerConnection);

		mysqlDslContext.transaction(configurartion -> {
			DSLContext dslContext = configurartion.dsl();
			transfer(sqlServerQueryFactory, dslContext);
		});

	}

	public static void transfer(SQLServerQueryFactory sqlServerQueryFactory, DSLContext mysqlDslContext) {
		sqlServerQueryFactory.selectFrom(QEMPRESAS.EMPRESAS).stream().forEach(empresa -> {
			EnterpriseRecord enterpriseRecord = insertEmpresa(empresa, mysqlDslContext);

			sqlServerQueryFactory.selectFrom(QCENTROS.CENTROS)
					.where(QCENTROS.CENTROS.CODEMPRESA.eq(empresa.getCODEMPRESA())).stream().forEach(centro -> {
						EnterpriseCccRecord enterpriseCccRecord = insertEnterpriseActivity(centro, enterpriseRecord,
								mysqlDslContext);
						PayrollWorkplaceRecord payrollWorkplaceRecord = insertWorkplace(centro, enterpriseRecord,
								mysqlDslContext);

						sqlServerQueryFactory.selectFrom(QTRABAJADORES.TRABAJADORES)
								.where(QTRABAJADORES.TRABAJADORES.CODEMPRESA.eq(empresa.getCODEMPRESA())
										.and(QTRABAJADORES.TRABAJADORES.CODCENTRO.eq(centro.getCODIGO())))
								.stream().forEach(trabajador -> {
									ContractRecord contractRecord = Adiss2Contract.insertContract(trabajador,
											enterpriseCccRecord, payrollWorkplaceRecord, mysqlDslContext);

									sqlServerQueryFactory.selectFrom(QINCIDENCIAS.INCIDENCIAS)
											.where(QINCIDENCIAS.INCIDENCIAS.CODEMPRESA.eq(empresa.getCODEMPRESA())
													.and(QINCIDENCIAS.INCIDENCIAS.CODTRABAJADOR
															.eq(trabajador.getCODTRABAJADOR()))
													.and(QINCIDENCIAS.INCIDENCIAS.CODCENTRO.eq(centro.getCODIGO())))
											.stream().forEach(incidencia -> {
//												Adiss2Contract.insertIT(incidencia, trabajador, centro,
//														empresa, contractRecord, mysqlDslContext);
											});

									sqlServerQueryFactory.select(QNOMINAS.NOMINAS.getProjection())
											.from(QNOMINAS.NOMINAS)
											.where(QNOMINAS.NOMINAS.CODEMPRESA.eq(empresa.getCODEMPRESA()).and(
													QNOMINAS.NOMINAS.CODTRABAJADOR.eq(trabajador.getCODTRABAJADOR())))
											.stream().forEach(nomina -> {
												SalaryRecord salaryRecord = insertSalary(nomina, trabajador, centro,
														empresa, contractRecord, mysqlDslContext);

												List<SalaryDataRecord> salaryDataRecords = new ArrayList<>();
												List<SalaryCostRecord> salaryCostRecords = new ArrayList<>();
												List<SalaryPaymentRecord> salaryPaymentsRecords = new ArrayList<>();
												List<SalaryDeductionRecord> salaryDeductionsRecords = new ArrayList<>();

												Adiss2Salary
														.insertSalaryDatas(nomina, trabajador, centro, empresa,
																salaryRecord, mysqlDslContext)
														.forEach(salaryDataRecords::add);
												Adiss2Salary
														.insertSalaryCosts(nomina, trabajador, centro, empresa,
																salaryRecord, mysqlDslContext)
														.forEach(salaryCostRecords::add);
												Adiss2Salary
														.insertSalaryDeductions(nomina, trabajador, centro, empresa,
																salaryRecord, mysqlDslContext)
														.forEach(salaryDeductionsRecords::add);

												sqlServerQueryFactory
														.select(QNOMINASCONCEPTOS.NOMINASCONCEPTOS.getProjection(),
																QCONCEPTOSREGISTROS.CONCEPTOSREGISTROS.getProjection())
														.from(QNOMINASCONCEPTOS.NOMINASCONCEPTOS)
														.leftJoin(QCONCEPTOSREGISTROS.CONCEPTOSREGISTROS)
														.on(QCONCEPTOSREGISTROS.CONCEPTOSREGISTROS.CODIGOTABLA
																.eq(empresa.getNOMTABLACONCEPTOS())
																.and(QCONCEPTOSREGISTROS.CONCEPTOSREGISTROS.CODIGO.eq(
																		QNOMINASCONCEPTOS.NOMINASCONCEPTOS.CODCONCEPTO)))
														.where(QNOMINASCONCEPTOS.NOMINASCONCEPTOS.CODEMPRESA
																.eq(empresa.getCODEMPRESA())
																.and(QNOMINASCONCEPTOS.NOMINASCONCEPTOS.CODTRABAJADOR
																		.eq(trabajador.getCODTRABAJADOR()))
																.and(QNOMINASCONCEPTOS.NOMINASCONCEPTOS.TIPOPAGA
																		.eq("01"))
																.and(QNOMINASCONCEPTOS.NOMINASCONCEPTOS.FECHA
																		.eq(nomina.getFECHA())))
														.stream().forEach(conceptos -> {
															NOMINASCONCEPTOS concepto = conceptos.get(
																	QNOMINASCONCEPTOS.NOMINASCONCEPTOS.getProjection());

															Optional<CONCEPTOSREGISTROS> registro = Optional.ofNullable(
																	conceptos.get(QCONCEPTOSREGISTROS.CONCEPTOSREGISTROS
																			.getProjection()));

															Adiss2Salary
																	.insertSalaryPayment(registro, concepto, nomina,
																			trabajador, centro, empresa, salaryRecord,
																			mysqlDslContext)
																	.ifPresentOrElse(salaryPaymentsRecords::add,
																			() -> Adiss2Salary.insertSalaryDeduction(
																					registro, concepto, nomina,
																					trabajador, centro, empresa,
																					salaryRecord, mysqlDslContext)
																					.ifPresent(
																							salaryDeductionsRecords::add));
														});
												try {
													Adiss2Salary.checkSalary(salaryRecord, salaryDataRecords,
															salaryPaymentsRecords, salaryDeductionsRecords,
															salaryCostRecords);
												} catch (AssertionError e) {
													System.err.println(e.getMessage());
												}

											});
									sqlServerQueryFactory.select(QNOMINASEXT.NOMINASEXT.getProjection())
											.from(QNOMINASEXT.NOMINASEXT)
											.where(QNOMINASEXT.NOMINASEXT.CODEMPRESA.eq(empresa.getCODEMPRESA())
													.and(QNOMINASEXT.NOMINASEXT.CODTRABAJADOR
															.eq(trabajador.getCODTRABAJADOR())))
											.stream().forEach(extra -> {
												SalaryRecord salaryRecord = insertExtra(extra, trabajador, centro,
														empresa, contractRecord, mysqlDslContext);

											});
								});
					});
		});
	}

	public static EnterpriseRecord insertEmpresa(EMPRESAS empresa, DSLContext dslContext) {
		// ENTERPRISE = 0
		// CONSULTANCY = 1
		// GARAGE = 2
		// ACADEMY = 3
		// HOTEL = 4
		// ADMIN = 5
		// OFFICE = 6
		// GENERIC = 7
		// COMMERCE = 8
		// KIT_DIGITAL = 9
		String name = AonStringUtils.lowerCase(empresa.getCODEMPRESA() + "-" + empresa.getCIF() + SUFFIX);
		// re-entrant by name
		DomainRecord domainRecord = dslContext.selectFrom(DOMAIN).where(Domain.DOMAIN.NAME.eq(name)).fetchOptional()
				.orElseGet(() -> dslContext.newRecord(Domain.DOMAIN));
		domainRecord.setName(name);
		domainRecord.setType((byte) 0);
		domainRecord.setActive((byte) 1);
		domainRecord.setOwner(empresa.getCODRESPONSABLE());
		domainRecord.setDescription(empresa.getNOMEMPRESA());
		domainRecord.store();

		// re-entrant by domain & cif
		RegistryRecord registryRecord = dslContext.selectFrom(REGISTRY)
				.where(REGISTRY.DOMAIN.eq(domainRecord.getId()).and(REGISTRY.DOCUMENT.eq(empresa.getCIF())))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(Registry.REGISTRY));
		registryRecord.setDomain(domainRecord.getId());
		// NIF = 0,
		// CIF = 1,
		// NIE = 2,
		// PASSPORT = 3,
		// ..
		// empresa.getPAIS();
		registryRecord.setNationality("ES");
		registryRecord.setDocumentType((byte) 1);
		registryRecord.setDocumentCountry("ES");
		registryRecord.setDocument(empresa.getCIF());
		registryRecord.setName(empresa.getNOMEMPRESA());
		registryRecord.store();

		// re-entrant by domain & registry
		CompanyRecord companyRecord = dslContext.selectFrom(COMPANY)
				.where(COMPANY.DOMAIN.eq(domainRecord.getId()).and(COMPANY.REGISTRY.eq(registryRecord.getId())))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(COMPANY));
		companyRecord.setDomain(domainRecord.getId());
		companyRecord.setRegistry(registryRecord.getId());
		companyRecord.store();

		// re-entrant by domain & description ( GENERAL )
		ScopeRecord scopeRecord = dslContext.selectFrom(SCOPE)
				.where(SCOPE.DOMAIN.eq(domainRecord.getId()).and(SCOPE.DESCRIPTION.eq("GENERAL"))).fetchOptional()
				.orElseGet(() -> dslContext.newRecord(SCOPE));
		scopeRecord.setDomain(domainRecord.getId());
		scopeRecord.setDescription("GENERAL");
		scopeRecord.store();

		// re-entrant by domain & registry
		EnterpriseRecord enterpriseRecord = dslContext.selectFrom(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainRecord.getId()).and(ENTERPRISE.REGISTRY.eq(registryRecord.getId())))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(ENTERPRISE));
		enterpriseRecord.setDomain(domainRecord.getId());
		enterpriseRecord.setRegistry(registryRecord.getId());
		enterpriseRecord.setScope(scopeRecord.getId());
		enterpriseRecord.store();

		// re-entrant by domain & registry & type ( main )
		RaddressRecord raddressRecord = dslContext.selectFrom(RADDRESS)
				.where(RADDRESS.DOMAIN.eq(domainRecord.getId()).and(RADDRESS.REGISTRY.eq(registryRecord.getId()))
						.and(RADDRESS.TYPE.eq((byte) 0)))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(RADDRESS));
		raddressRecord.setDomain(domainRecord.getId());
		raddressRecord.setRegistry(registryRecord.getId());
		// MAIN = 0
		// DELEGATION = 1
		// ...
		// BILLING = 4
		// SHIPPING = 5
		raddressRecord.setType((byte) 0);
		raddressRecord.setCity(empresa.getMUNICIPIO());
		raddressRecord.setNumber(empresa.getNUMERO());
		raddressRecord.setZip(empresa.getCPMUNICIPIO());
		raddressRecord.setAddress(empresa.getVIA());
		raddressRecord.setAddress2(join(" ", empresa.getPORTAL(), empresa.getBLOQUE(), empresa.getPISO()));
		raddressRecord.store();
		// UNKNOWN = 0
		// FIXED_PHONE = 1
		// CELLULAR = 2
		if (anyNotBlank(empresa.getTLF(), empresa.getTLF2(), empresa.getTLF3())) {
			// re-entrant by domain & registry & media ( fixed_phone )
			RmediaRecord phoneRecord = dslContext.selectFrom(RMEDIA)
					.where(RMEDIA.DOMAIN.eq(domainRecord.getId()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
							.and(RMEDIA.MEDIA.eq((byte) 1)))
					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
			phoneRecord.setMedia((byte) 1);
			phoneRecord.setDomain(domainRecord.getId());
			phoneRecord.setRegistry(registryRecord.getId());
			phoneRecord.setRaddress(raddressRecord.getId());

			// 555667788, Ext. 12 - 999553311
			String tlf = join(", Ext. ", empresa.getTLF(), empresa.getTLFEXT());
			String tlf2 = join(", Ext. ", empresa.getTLF2(), empresa.getTLFEXT2());
			String tlf3 = join("", empresa.getTLF3());
			String value = join(" - ", tlf, tlf2, tlf3);
			phoneRecord.setValue(value);
			phoneRecord.store();
		}
		// FAX = 3
		if (anyNotBlank(empresa.getFAX(), empresa.getFAX2())) {
			// re-entrant by domain & registry & media ( fax )
			RmediaRecord faxRecord = dslContext.selectFrom(RMEDIA)
					.where(RMEDIA.DOMAIN.eq(domainRecord.getId()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
							.and(RMEDIA.MEDIA.eq((byte) 3)))
					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
			faxRecord.setMedia((byte) 3);
			faxRecord.setDomain(domainRecord.getId());
			faxRecord.setRegistry(registryRecord.getId());
			faxRecord.setRaddress(raddressRecord.getId());
			faxRecord.setValue(join(" - ", empresa.getFAX(), empresa.getFAX2()));
			faxRecord.store();
		}

		// EMAIL = 4
		if (anyNotBlank(empresa.getEMAIL())) {
			// re-entrant by domain & registry & media ( email )
			RmediaRecord emailRecord = dslContext.selectFrom(RMEDIA)
					.where(RMEDIA.DOMAIN.eq(domainRecord.getId()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
							.and(RMEDIA.MEDIA.eq((byte) 4)))
					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
			emailRecord.setMedia((byte) 4);
			emailRecord.setValue(empresa.getEMAIL());
			emailRecord.setDomain(domainRecord.getId());
			emailRecord.setRegistry(registryRecord.getId());
			emailRecord.setRaddress(raddressRecord.getId());
			emailRecord.store();
		}
		// WEB = 5
		if (anyNotBlank(empresa.getPAGWEB())) {
			RmediaRecord webRecord = dslContext.selectFrom(RMEDIA)
					.where(RMEDIA.DOMAIN.eq(domainRecord.getId()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
							.and(RMEDIA.MEDIA.eq((byte) 5)))
					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
			webRecord.setMedia((byte) 5);
			webRecord.setValue(empresa.getPAGWEB());
			webRecord.setDomain(domainRecord.getId());
			webRecord.setRegistry(registryRecord.getId());
			webRecord.setRaddress(raddressRecord.getId());
			webRecord.store();
		}

		return enterpriseRecord;
	}

	public static PayrollWorkplaceRecord insertWorkplace(CENTROS centro, EnterpriseRecord enterpriseRecord,
			DSLContext dslContext) {
		// re-entrant by domain & registry & worlplace.description
		RaddressRecord raddressRecord = dslContext
				.selectFrom(RADDRESS.innerJoin(WORKPLACE).on(WORKPLACE.ADDRESS.eq(RADDRESS.ID)))
				.where(RADDRESS.TYPE.eq((byte) 1).and(RADDRESS.DOMAIN.eq(enterpriseRecord.getDomain()))
						.and(RADDRESS.REGISTRY.eq(enterpriseRecord.getRegistry()))
						.and(WORKPLACE.DESCRIPTION.eq(centro.getDESCRIPCION())))
				.fetchOptionalInto(RADDRESS).orElseGet(() -> dslContext.newRecord(RADDRESS));
		// DELEGATION = 1
		raddressRecord.setType((byte) 1);
		raddressRecord.setDomain(enterpriseRecord.getDomain());
		raddressRecord.setRegistry(enterpriseRecord.getRegistry());
		raddressRecord.setCity(centro.getMUNICIPIO());
		raddressRecord.setZip(centro.getCODPOSTAL());
		raddressRecord.setAddress(centro.getDOMICILIO());
		raddressRecord.store();

		// re-entrant by domain & registry & type ( delegation ) & Recipient ( CODIGO )
		WorkplaceRecord workplaceRecord = dslContext.selectFrom(WORKPLACE)
				.where(WORKPLACE.DOMAIN.eq(enterpriseRecord.getDomain())
						.and(WORKPLACE.ENTERPRISE.eq(enterpriseRecord.getRegistry()))
						.and(WORKPLACE.DESCRIPTION.eq(centro.getDESCRIPCION())))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(WORKPLACE));
		workplaceRecord.setActive((byte) 1);
		workplaceRecord.setDomain(enterpriseRecord.getDomain());
		workplaceRecord.setEnterprise(enterpriseRecord.getRegistry());
		workplaceRecord.setAddress(raddressRecord.getId());
		workplaceRecord.setScope(enterpriseRecord.getScope());
		workplaceRecord.setDescription(centro.getDESCRIPCION());
		workplaceRecord.store();

		// re-entrant by domain & workplace
		PayrollWorkplaceRecord payrollWorkplaceRecord = dslContext.selectFrom(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.DOMAIN.eq(enterpriseRecord.getDomain())
						.and(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceRecord.getId())))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(PAYROLL_WORKPLACE));
		payrollWorkplaceRecord.setDomain(workplaceRecord.getDomain());
		payrollWorkplaceRecord.setWorkplace(workplaceRecord.getId());

		payrollWorkplaceRecord.store();

		return payrollWorkplaceRecord;
	}

	public static EnterpriseCccRecord insertEnterpriseActivity(CENTROS centro, EnterpriseRecord enterpriseRecord,
			DSLContext dslContext) {

		Optional<Integer> cnaeId = dslContext.selectFrom(CNAE).where(CNAE.ID.eq(centro.getCNAE93()))
				.fetchOptional(CNAE.ID);
		Optional<Integer> cnae2009Id = dslContext.selectFrom(CNAE2009).where(CNAE2009.ID.eq(centro.getCNAE09()))
				.fetchOptional(CNAE2009.ID);
		Optional<Integer> iaeId = dslContext.selectFrom(IAE).where(IAE.EPIGRAPH.eq(centro.getCODACTIVIDAD()))
				.fetchOptional(IAE.ID);

		// re-entrat enterprise & CNAE09 & CNAE93
		EnterpriseActivityRecord enterpriseActivityRecord = dslContext.selectFrom(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(enterpriseRecord.getRegistry())
						.and(ENTERPRISE_ACTIVITY.CNAE.eq(cnaeId.orElse(Integer.MIN_VALUE))
								.or(ENTERPRISE_ACTIVITY.CNAE2009.eq(cnae2009Id.orElse(Integer.MIN_VALUE)))
								.or(ENTERPRISE_ACTIVITY.IAE.eq(iaeId.orElse(Integer.MIN_VALUE)))))
				.fetchOptionalInto(ENTERPRISE_ACTIVITY).orElseGet(() -> dslContext.newRecord(ENTERPRISE_ACTIVITY));

		enterpriseActivityRecord.setDomain(enterpriseRecord.getDomain());
		enterpriseActivityRecord.setEnterprise(enterpriseRecord.getRegistry());
		iaeId.ifPresent(enterpriseActivityRecord::setIae);
		cnaeId.ifPresent(enterpriseActivityRecord::setCnae);
		cnae2009Id.ifPresent(enterpriseActivityRecord::setCnae2009);
		enterpriseActivityRecord.setDescription(centro.getDESCACTIVIDAD());
		enterpriseActivityRecord.setType((byte) 0);
		enterpriseActivityRecord.setPrincipal((byte) 1);
		enterpriseActivityRecord.store();

		String ccc = getCcc(centro);

		Optional<Integer> geozoneId = dslContext.selectFrom(GEOZONE).where(
				GEOZONE.DOMAIN.eq(enterpriseRecord.getDomain()).and(GEOZONE.CODE.eq(centro.getNroINSCRIPCION1())))
				.fetchOptional(GEOZONE.ID);
		// re-enterat y activity & ccc code
		EnterpriseCccRecord enterpriseCccRecord = dslContext.selectFrom(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpriseActivityRecord.getId())
						.and(ENTERPRISE_CCC.CCC.eq(ccc)))
				.fetchOptionalInto(ENTERPRISE_CCC).orElseGet(() -> dslContext.newRecord(ENTERPRISE_CCC));
		enterpriseCccRecord.setDomain(enterpriseRecord.getDomain());
		enterpriseCccRecord.setEnterpriseActivity(enterpriseActivityRecord.getId());
		enterpriseCccRecord.setCcc(ccc);
		geozoneId.ifPresent(enterpriseCccRecord::setGeozone);
		enterpriseCccRecord.store();

		return enterpriseCccRecord;
	}

	static String getCcc(CENTROS centro) {
		return join("", centro.getNroINSCRIPCION1(), centro.getNroINSCRIPCION2(),
				AonStringUtils.leftPad(centro.getNroINSCRIPCION3(), 2, '0'));
	}

	static Date getDate(String str) {
		try {
			if (AonStringUtils.isNotBlank(str)) {
				java.util.Date date = new SimpleDateFormat("dd/mm/yyyy").parse(str);
				return AonDateUtils.get(date, Calendar.YEAR) >= 2099 ? null : new Date(date.getTime());
			}
		} catch (ParseException e) {
		}
		return null;
	}

	static Date getDate(Timestamp timestamp) {
		return timestamp == null ? null : new Date(timestamp.getTime());
	}

	static String join(String sep, String... strs) {
		return Stream.of(strs).filter(AonStringUtils::isNotBlank).collect(Collectors.joining(sep));
	}

	static boolean anyNotBlank(String... strs) {
		return Stream.of(strs).anyMatch(AonStringUtils::isNotBlank);
	}

	static boolean allNull(Object... objs) {
		return Stream.of(objs).allMatch(Objects::isNull);
	}

	static boolean allZero(Number... numbers) {
		return Stream.of(numbers).mapToDouble(AonNumberUtils::todouble).allMatch(value -> value == 0.00);
	}

	static boolean anyNonZero(Number... numbers) {
		return Stream.of(numbers).mapToDouble(AonNumberUtils::todouble).anyMatch(value -> value != 0.00);
	}

	static String firstNotBlank(String... strs) {
		return Arrays.stream(strs).filter(AonStringUtils::isNotBlank).findFirst().orElse(null);
	}

	static int toInt(BigDecimal... bigDecimals) {
		for (BigDecimal bigDecimal : bigDecimals) {
			if (bigDecimal != null) {
				return bigDecimal.intValue();
			}
		}
		return 0;
	}

	static double toDouble(BigDecimal... bigDecimals) {
		for (BigDecimal bigDecimal : bigDecimals) {
			if (bigDecimal != null) {
				return bigDecimal.doubleValue();
			}
		}
		return 0.00;
	}

}
