package net.aonsolutions.aon.in.adiss;


import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;

import org.jooq.DSLContext;

import com.esferalia.aon.jooq.tables.Geozone;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.adiss.querydsl.CENTROS;
import net.aonsolutions.adiss.querydsl.EMPRESAS;
import net.aonsolutions.adiss.querydsl.INCIDENCIAS;
import net.aonsolutions.adiss.querydsl.TRABAJADORES;

class Adiss2Contract {

	enum LeaveType {
		COMMON_DISEASE, 
		OCCUPATIONAL_DISEASE, 
		MATERNITY, 
		PATERNITY, 
		PREGNANCY_RISK, 
		BREASTFEEDING_RISK,
		NON_OCCUPATIONAL_DISEASE, 
		COMMON_DISEASE_AT_LACK, 
		COMMON_OCCUPATIONAL_DISEASE, 
		OBSERVATION_OCCUPATIONAL_DISEASE,
		MENSTRUATION, 
		PREGNANCY_STOP, 
		PREGNANCY_39_WEEK
	}

	private Adiss2Contract() {
	}

	public static ContractRecord insertContract(TRABAJADORES trabajador, EnterpriseCccRecord enterpriseCccRecord,
			PayrollWorkplaceRecord payrollWorkplaceRecord, DSLContext dslContext) {
		// re-entrant by domain & dni
		RegistryRecord registryRecord = dslContext.selectFrom(REGISTRY)
				.where(REGISTRY.DOMAIN.eq(payrollWorkplaceRecord.getDomain()))
				.and(REGISTRY.DOCUMENT.eq(trabajador.getDNI())).fetchOptional()
				.orElseGet(() -> dslContext.newRecord(REGISTRY));
		registryRecord.setDomain(payrollWorkplaceRecord.getDomain());
		registryRecord.setNationality("ES");
		registryRecord.setDocumentType((byte) 0); // NIF = 0,
		registryRecord.setDocumentCountry("ES");
		registryRecord.setDocument(trabajador.getDNI());
		registryRecord.setName(trabajador.getNOMTRABAJADOR());
		registryRecord.store();

		RaddressRecord raddressRecord = dslContext.selectFrom(RADDRESS)
				.where(RADDRESS.DOMAIN.eq(registryRecord.getDomain()).and(RADDRESS.REGISTRY.eq(registryRecord.getId()))
						.and(RADDRESS.TYPE.eq((byte) 0)))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(RADDRESS));
		raddressRecord.setDomain(registryRecord.getDomain());
		raddressRecord.setRegistry(registryRecord.getId());
		raddressRecord.setType((byte) 0); // MAIN = 0
		raddressRecord.setCity(trabajador.getMUNICIPIO());
		raddressRecord.setNumber(trabajador.getNUMERO());
		raddressRecord.setZip(trabajador.getCODIGOPOSTAL());
		raddressRecord.setAddress(trabajador.getDIRECCION());
		raddressRecord.setAddress2(
				Adiss2Aon.join(" ", trabajador.getPISO(), trabajador.getESCALERA(), trabajador.getPUERTA()));

		if (AonStringUtils.isNotBlank(trabajador.getPROVINCIA())) {
			// re-entrant by domain & name
			GeozoneRecord geozoneRecord = dslContext.selectFrom(Geozone.GEOZONE)
					.where(GEOZONE.DOMAIN.eq(registryRecord.getDomain())
							.and(GEOZONE.NAME.eq(trabajador.getPROVINCIA())))
					.fetchOptionalInto(GEOZONE).orElseGet(() -> dslContext.newRecord(GEOZONE));
			geozoneRecord.setDomain(registryRecord.getDomain());
			geozoneRecord.setName(trabajador.getPROVINCIA());
			geozoneRecord.setCode(AonStringUtils.substring(trabajador.getCODIGOPOSTAL(), 0, 2));
			geozoneRecord.store();
			raddressRecord.setGeozone(geozoneRecord.getId());
		}
		raddressRecord.store();

		// EMAIL = 4
		if (Adiss2Aon.anyNotBlank(trabajador.getEMAIL())) {
			// re-entrant by domain & registry & media ( email )
			RmediaRecord emailRecord = dslContext.selectFrom(RMEDIA)
					.where(RMEDIA.DOMAIN.eq(registryRecord.getDomain()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
							.and(RMEDIA.MEDIA.eq((byte) 4)))
					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
			emailRecord.setMedia((byte) 4);
			emailRecord.setValue(trabajador.getEMAIL());
			emailRecord.setDomain(registryRecord.getDomain());
			emailRecord.setRegistry(registryRecord.getId());
			emailRecord.setRaddress(raddressRecord.getId());
			emailRecord.store();
		}
		if (Adiss2Aon.anyNotBlank(trabajador.getTELEFONO(), trabajador.getTELEFONO2())) {
			// re-entrant by domain & registry & media ( fixed_phone )
			RmediaRecord phoneRecord = dslContext.selectFrom(RMEDIA)
					.where(RMEDIA.DOMAIN.eq(registryRecord.getDomain()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
							.and(RMEDIA.MEDIA.eq((byte) 1)))
					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
			phoneRecord.setMedia((byte) 1);
			phoneRecord.setDomain(registryRecord.getDomain());
			phoneRecord.setRegistry(registryRecord.getId());
			phoneRecord.setRaddress(raddressRecord.getId());

			String value = Adiss2Aon.join(" - ", trabajador.getTELEFONO(), trabajador.getTELEFONO2());
			phoneRecord.setValue(value);
			phoneRecord.store();
		}

		PersonRecord personRecord = dslContext.selectFrom(PERSON)
				.where(PERSON.DOMAIN.eq(payrollWorkplaceRecord.getDomain()))
				.and(PERSON.REGISTRY.eq(registryRecord.getId())).fetchOptional()
				.orElseGet(() -> dslContext.newRecord(PERSON));
		personRecord.setDomain(registryRecord.getDomain());
		personRecord.setRegistry(registryRecord.getId());
		personRecord.setName(trabajador.getNOMTRABAJADOR());
		personRecord.setSocialSecurityNum(trabajador.getCOTGSS());
		personRecord.setBirthDate(Adiss2Aon.getDate(trabajador.getFECHANACIMIENTO()));
		personRecord.setSocialSecurityNum(getSocialSecurityNum(trabajador));
		switch (trabajador.getSEXO()) {
		case "H", "h":
			// MALE = 0
			personRecord.setGender((byte) 0);
			break;
		case "M", "m":
			// FEMALE = 1
			personRecord.setGender((byte) 1);
			break;
		default:
			// UNKNOWN = 2
			personRecord.setGender((byte) 2);
		}

		personRecord.store();

		Date startDate = Adiss2Aon.getDate(trabajador.getFECHAINICONTRATO());
		Date endDate = Adiss2Aon.getDate(trabajador.getFECHAFINCONTRATO());

		ContractRecord contractRecord = dslContext.selectFrom(CONTRACT)
				.where(CONTRACT.WORKPLACE.eq(payrollWorkplaceRecord.getWorkplace())
						.and(CONTRACT.PERSON.eq(personRecord.getRegistry()).and(CONTRACT.START_DATE.eq(startDate))))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(CONTRACT));
		contractRecord.setDomain(personRecord.getDomain());
		contractRecord.setPerson(personRecord.getRegistry());
		contractRecord.setWorkplace(payrollWorkplaceRecord.getWorkplace());
		contractRecord.setStartDate(startDate);
		contractRecord.setEndDate(endDate);
		contractRecord.setCategoryDescription(trabajador.getCATEGORIADESC());
		contractRecord.setSeniorityDate(Adiss2Aon.getDate(trabajador.getFECHAANTIGUEDAD()));
		contractRecord.setEnterpriseCcc(enterpriseCccRecord.getId());
		contractRecord.setEnterpriseActivity(enterpriseCccRecord.getEnterpriseActivity());
		if (AonNumberUtils.toint(trabajador.getGRUPOTARIFA()) == 0) // � RETA ?
			contractRecord.setSsRegime((byte) 3);

		contractRecord.store();

		if (AonNumberUtils.between(AonNumberUtils.toint(trabajador.getTIPOCONTRATO()), 100, 990))
			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "TC2",
					"\"" + trabajador.getTIPOCONTRATO() + "\"");
		if (AonNumberUtils.between(AonNumberUtils.toint(trabajador.getGRUPOTARIFA()), 1, 10))
			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "GRUPO_COTIZACION",
					String.format("\"%s\"", AonStringUtils.leftPad(trabajador.getGRUPOTARIFA(), 2, '0')));
		if (trabajador.getPRCNTJORNADA() != null && trabajador.getPRCNTJORNADA().longValue() > 0.00)
			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "COEFICIENTE_PARCIALIDAD",
					trabajador.getPRCNTJORNADA().divide(BigDecimal.valueOf(100.00)).toPlainString());
		if (AonStringUtils.isNotBlank(trabajador.getCNO()))
			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "CNO",
					String.format("\"%s\"", trabajador.getCNO()));
		if (trabajador.getPORIT() != null && trabajador.getPORIT() >= 0.00)
			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "PORCENTAJE_IT",
					Double.toString(trabajador.getPORIT() / 100.00));
		if (trabajador.getPORIMS() != null && trabajador.getPORIMS() >= 0.00)
			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "PORCENTAJE_IMS",
					Double.toString(trabajador.getPORIMS() / 100.00));

		// System.out.println(trabajador.getPORIMS() + ", " + trabajador.getPORIT());

		return contractRecord;
	}

	static ContractLeaveRecord insertIncidencia(INCIDENCIAS incidencia, TRABAJADORES trabajador, CENTROS centro,
			EMPRESAS empresa, ContractRecord contractRecord, DSLContext dslContext) {
		// 1 CONCEPTOS VARIABLES

		// 5 RECAIDA ENFERMEDAD
		// 6 RECAIDA ACCIDENTE
		// 7 FIN I.T. (EXCEPTO PERMISO NACIMIENTO - PARCIAL-)
		// 9 INICIO VACACIONES
		// 10 FIN VACACIONES

		// 11 INICIO HUELGA
		// 12 FIN HUELGA

		// 15 FIN EXPEDIENTE REGULACI�N
		// 18 DIAS N (ACTUACIONES ARTISTAS)

		// 21 CONCEPTOS VARIABLES PAGAS EXTRAS
		// 32 INICIO I.T. RIESGO DURANTE EMBARAZO

		int codIncidencia = AonNumberUtils.toint(incidencia.getCODINCIDENCIA());
		switch (codIncidencia) {
		case 2: // 2 INICIO I.T. ENFERMEDAD
			break;
		case 3: // 3 INICIO I.T. ACCIDENTE
			break;
		default:
			break;
		}

		System.out.println(incidencia.getFECHA() + " -. " + incidencia.getCODINCIDENCIA() + "("
				+ incidencia.getNUMLINEA() + "," + incidencia.getAMPLIACION1() + ", " + incidencia.getAMPLIACION2()
				+ incidencia.getAMPLIACION3() + ")");
		return null;
	}

	static String getSocialSecurityNum(TRABAJADORES trabajador) {
		return Adiss2Aon.join("", trabajador.getNroAFILIACIONSS1(), trabajador.getNroAFILIACIONSS2(),
				AonStringUtils.leftPad(trabajador.getNroAFILIACIONSS3(), 2, '0'));
	}

	static void insertContractData(DSLContext dslContext, Date startDate, Date endDate, ContractRecord contractRecord,
			String name, String expression) {
		ContractDataRecord contractDataRecord = dslContext.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractRecord.getId()).and(CONTRACT_DATA.NAME.eq(name)))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(CONTRACT_DATA));
		contractDataRecord.setDomain(contractRecord.getDomain());
		contractDataRecord.setContract(contractRecord.getId());
		contractDataRecord.setName(name);
		contractDataRecord.setEndDate(endDate);
		contractDataRecord.setExpression(expression);

		contractDataRecord.store();
	}

}

//import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
//import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
//import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
//import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
//import static com.esferalia.aon.jooq.tables.Person.PERSON;
//import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
//import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
//import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
//
//import java.math.BigDecimal;
//import java.sql.Date;
//
//import org.jooq.DSLContext;
//
//import com.esferalia.aon.jooq.tables.ContractLeave;
//import com.esferalia.aon.jooq.tables.Geozone;
//import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
//import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
//import com.esferalia.aon.jooq.tables.records.ContractRecord;
//import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
//import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
//import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
//import com.esferalia.aon.jooq.tables.records.PersonRecord;
//import com.esferalia.aon.jooq.tables.records.RaddressRecord;
//import com.esferalia.aon.jooq.tables.records.RegistryRecord;
//import com.esferalia.aon.jooq.tables.records.RmediaRecord;
//import com.esferalia.aon.jooq.tables.records.SalaryRecord;
//import com.esferalia.aon.watson.util.AonNumberUtils;
//import com.esferalia.aon.watson.util.AonStringUtils;
//
//import net.aonsolutions.adiss.querydsl.CENTROS;
//import net.aonsolutions.adiss.querydsl.EMPRESAS;
//import net.aonsolutions.adiss.querydsl.INCIDENCIAS;
//import net.aonsolutions.adiss.querydsl.NOMINAS;
//import net.aonsolutions.adiss.querydsl.TRABAJADORES;
//
//class Adiss2Contract {
//
//	private Adiss2Contract() {
//	}
//
//	public static ContractRecord insertContract(TRABAJADORES trabajador, EnterpriseCccRecord enterpriseCccRecord,
//			PayrollWorkplaceRecord payrollWorkplaceRecord, DSLContext dslContext) {
//		// re-entrant by domain & dni
//		RegistryRecord registryRecord = dslContext.selectFrom(REGISTRY)
//				.where(REGISTRY.DOMAIN.eq(payrollWorkplaceRecord.getDomain()))
//				.and(REGISTRY.DOCUMENT.eq(trabajador.getDNI())).fetchOptional()
//				.orElseGet(() -> dslContext.newRecord(REGISTRY));
//		registryRecord.setDomain(payrollWorkplaceRecord.getDomain());
//		registryRecord.setNationality("ES");
//		registryRecord.setDocumentType((byte) 0); // NIF = 0,
//		registryRecord.setDocumentCountry("ES");
//		registryRecord.setDocument(trabajador.getDNI());
//		registryRecord.setName(trabajador.getNOMTRABAJADOR());
//		registryRecord.store();
//	
//		RaddressRecord raddressRecord = dslContext.selectFrom(RADDRESS)
//				.where(RADDRESS.DOMAIN.eq(registryRecord.getDomain()).and(RADDRESS.REGISTRY.eq(registryRecord.getId()))
//						.and(RADDRESS.TYPE.eq((byte) 0)))
//				.fetchOptional().orElseGet(() -> dslContext.newRecord(RADDRESS));
//		raddressRecord.setDomain(registryRecord.getDomain());
//		raddressRecord.setRegistry(registryRecord.getId());
//		raddressRecord.setType((byte) 0); // MAIN = 0
//		raddressRecord.setCity(trabajador.getMUNICIPIO());
//		raddressRecord.setNumber(trabajador.getNUMERO());
//		raddressRecord.setZip(trabajador.getCODIGOPOSTAL());
//		raddressRecord.setAddress(trabajador.getDIRECCION());
//		raddressRecord.setAddress2(Adiss2Aon.join(" ", trabajador.getPISO(), trabajador.getESCALERA(), trabajador.getPUERTA()));
//	
//		if (AonStringUtils.isNotBlank(trabajador.getPROVINCIA())) {
//			// re-entrant by domain & name
//			GeozoneRecord geozoneRecord = dslContext.selectFrom(Geozone.GEOZONE)
//					.where(GEOZONE.DOMAIN.eq(registryRecord.getDomain())
//							.and(GEOZONE.NAME.eq(trabajador.getPROVINCIA())))
//					.fetchOptionalInto(GEOZONE).orElseGet(() -> dslContext.newRecord(GEOZONE));
//			geozoneRecord.setDomain(registryRecord.getDomain());
//			geozoneRecord.setName(trabajador.getPROVINCIA());
//			geozoneRecord.setCode(AonStringUtils.substring(trabajador.getCODIGOPOSTAL(), 0, 2));
//			geozoneRecord.store();
//			raddressRecord.setGeozone(geozoneRecord.getId());
//		}
//		raddressRecord.store();
//	
//		// EMAIL = 4
//		if (Adiss2Aon.anyNotBlank(trabajador.getEMAIL())) {
//			// re-entrant by domain & registry & media ( email )
//			RmediaRecord emailRecord = dslContext.selectFrom(RMEDIA)
//					.where(RMEDIA.DOMAIN.eq(registryRecord.getDomain()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
//							.and(RMEDIA.MEDIA.eq((byte) 4)))
//					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
//			emailRecord.setMedia((byte) 4);
//			emailRecord.setValue(trabajador.getEMAIL());
//			emailRecord.setDomain(registryRecord.getDomain());
//			emailRecord.setRegistry(registryRecord.getId());
//			emailRecord.setRaddress(raddressRecord.getId());
//			emailRecord.store();
//		}
//		if (Adiss2Aon.anyNotBlank(trabajador.getTELEFONO(), trabajador.getTELEFONO2())) {
//			// re-entrant by domain & registry & media ( fixed_phone )
//			RmediaRecord phoneRecord = dslContext.selectFrom(RMEDIA)
//					.where(RMEDIA.DOMAIN.eq(registryRecord.getDomain()).and(RMEDIA.REGISTRY.eq(registryRecord.getId()))
//							.and(RMEDIA.MEDIA.eq((byte) 1)))
//					.fetchOptional().orElseGet(() -> dslContext.newRecord(RMEDIA));
//			phoneRecord.setMedia((byte) 1);
//			phoneRecord.setDomain(registryRecord.getDomain());
//			phoneRecord.setRegistry(registryRecord.getId());
//			phoneRecord.setRaddress(raddressRecord.getId());
//	
//			String value = Adiss2Aon.join(" - ", trabajador.getTELEFONO(), trabajador.getTELEFONO2());
//			phoneRecord.setValue(value);
//			phoneRecord.store();
//		}
//	
//		PersonRecord personRecord = dslContext.selectFrom(PERSON)
//				.where(PERSON.DOMAIN.eq(payrollWorkplaceRecord.getDomain()))
//				.and(PERSON.REGISTRY.eq(registryRecord.getId())).fetchOptional()
//				.orElseGet(() -> dslContext.newRecord(PERSON));
//		personRecord.setDomain(registryRecord.getDomain());
//		personRecord.setRegistry(registryRecord.getId());
//		personRecord.setName(trabajador.getNOMTRABAJADOR());
//		personRecord.setSocialSecurityNum(trabajador.getCOTGSS());
//		personRecord.setBirthDate(Adiss2Aon.getDate(trabajador.getFECHANACIMIENTO()));
//		personRecord.setSocialSecurityNum(getSocialSecurityNum(trabajador));
//		switch (trabajador.getSEXO()) {
//		case "H", "h":
//			// MALE = 0
//			personRecord.setGender((byte) 0);
//			break;
//		case "M", "m":
//			// FEMALE = 1
//			personRecord.setGender((byte) 1);
//			break;
//		default:
//			// UNKNOWN = 2
//			personRecord.setGender((byte) 2);
//		}
//	
//		personRecord.store();
//	
//		Date startDate = Adiss2Aon.getDate(trabajador.getFECHAINICONTRATO());
//		Date endDate = Adiss2Aon.getDate(trabajador.getFECHAFINCONTRATO());
//		
//		
//	
//		ContractRecord contractRecord = dslContext.selectFrom(CONTRACT)
//				.where(CONTRACT.WORKPLACE.eq(payrollWorkplaceRecord.getWorkplace())
//						.and(CONTRACT.PERSON.eq(personRecord.getRegistry()).and(CONTRACT.START_DATE.eq(startDate))))
//				.fetchOptional().orElseGet(() -> dslContext.newRecord(CONTRACT));
//		contractRecord.setDomain(personRecord.getDomain());
//		contractRecord.setPerson(personRecord.getRegistry());
//		contractRecord.setWorkplace(payrollWorkplaceRecord.getWorkplace());
//		contractRecord.setStartDate(startDate);
//		contractRecord.setEndDate(endDate);
//		contractRecord.setCategoryDescription(trabajador.getCATEGORIADESC());
//		contractRecord.setSeniorityDate(Adiss2Aon.getDate(trabajador.getFECHAANTIGUEDAD()));
//		contractRecord.setEnterpriseCcc(enterpriseCccRecord.getId());
//		contractRecord.setEnterpriseActivity(enterpriseCccRecord.getEnterpriseActivity());
//		if (AonNumberUtils.toint(trabajador.getGRUPOTARIFA()) == 0) // � RETA ?
//			contractRecord.setSsRegime((byte) 3);
//	
//		contractRecord.store();
//	
//		if (AonNumberUtils.between(AonNumberUtils.toint(trabajador.getTIPOCONTRATO()), 100, 990))
//			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "TC2",
//					"\"" + trabajador.getTIPOCONTRATO() + "\"");
//		if (AonNumberUtils.between(AonNumberUtils.toint(trabajador.getGRUPOTARIFA()), 1, 10))
//			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "GRUPO_COTIZACION",
//					String.format("\"%s\"", AonStringUtils.leftPad(trabajador.getGRUPOTARIFA(), 2, '0')));
//		if (trabajador.getPRCNTJORNADA() != null && trabajador.getPRCNTJORNADA().longValue() > 0.00)
//			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "COEFICIENTE_PARCIALIDAD",
//					trabajador.getPRCNTJORNADA().divide(BigDecimal.valueOf(100.00)).toPlainString());
//		if (AonStringUtils.isNotBlank(trabajador.getCNO()))
//			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "CNO",
//					String.format("\"%s\"", trabajador.getCNO()));
//		if (trabajador.getPORIT() != null && trabajador.getPORIT() >= 0.00)
//			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "PORCENTAJE_IT",
//					Double.toString(trabajador.getPORIT() / 100.00));
//		if (trabajador.getPORIMS() != null && trabajador.getPORIMS() >= 0.00)
//			Adiss2Contract.insertContractData(dslContext, startDate, endDate, contractRecord, "PORCENTAJE_IMS",
//					Double.toString(trabajador.getPORIMS() / 100.00));
//	
//		// System.out.println(trabajador.getPORIMS() + ", " + trabajador.getPORIT());
//	
//		return contractRecord;
//	}
//
//	static ContractLeaveRecord insertIncidencia(INCIDENCIAS incidencia, TRABAJADORES trabajador, CENTROS centro, EMPRESAS empresa,
//			ContractRecord contractRecord, DSLContext dslContext) {
//		//1 CONCEPTOS VARIABLES
//
//		//5 RECAIDA ENFERMEDAD
//		//6 RECAIDA ACCIDENTE
//		//7 FIN I.T. (EXCEPTO PERMISO NACIMIENTO - PARCIAL-)
//		//9 INICIO VACACIONES
//		//10 FIN VACACIONES
//		
//		//11 INICIO HUELGA
//		//12 FIN HUELGA
//		
//		//15 FIN EXPEDIENTE REGULACI�N
//		//18 DIAS N (ACTUACIONES ARTISTAS)
//		
//		//21 CONCEPTOS VARIABLES PAGAS EXTRAS
//		//32 INICIO I.T. RIESGO DURANTE EMBARAZO
//		
//		int codIncidencia =  AonNumberUtils.toint(incidencia.getCODINCIDENCIA());
//		switch (codIncidencia) {
//		case 2: //2 INICIO I.T. ENFERMEDAD
//			break;
//		case 3: //3 INICIO I.T. ACCIDENTE
//			break;
//		default:
//			break;
//		}
//		
//
//		System.out.println(incidencia.getFECHA() + " -. " + incidencia.getCODINCIDENCIA() + "("
//				+ incidencia.getNUMLINEA() + "," + incidencia.getAMPLIACION1() + ", " + incidencia.getAMPLIACION2()
//				+ incidencia.getAMPLIACION3() + ")");
//		return null;
//	}
//	
//
//	static String getSocialSecurityNum(TRABAJADORES trabajador) {
//		return Adiss2Aon.join("", trabajador.getNroAFILIACIONSS1(), trabajador.getNroAFILIACIONSS2(),
//				AonStringUtils.leftPad(trabajador.getNroAFILIACIONSS3(),2, '0'));
//	}
//
//	static void insertContractData(DSLContext dslContext, Date startDate, Date endDate,
//			ContractRecord contractRecord, String name, String expression) {
//		ContractDataRecord contractDataRecord = dslContext.selectFrom(CONTRACT_DATA)
//				.where(CONTRACT_DATA.CONTRACT.eq(contractRecord.getId()).and(CONTRACT_DATA.NAME.eq(name)))
//				.fetchOptional().orElseGet(() -> dslContext.newRecord(CONTRACT_DATA));
//		contractDataRecord.setDomain(contractRecord.getDomain());
//		contractDataRecord.setContract(contractRecord.getId());
//		contractDataRecord.setName(name);
//		contractDataRecord.setEndDate(endDate);
//--
//
