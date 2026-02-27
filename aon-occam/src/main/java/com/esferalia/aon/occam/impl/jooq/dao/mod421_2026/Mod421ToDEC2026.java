package com.esferalia.aon.occam.impl.jooq.dao.mod421_2026;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.Mod421Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod421ToDEC2026 {
	
	private Mod421ToDEC2026() {
	}
	
	public static String getDeclaration(Mod421 mod421) throws JAXBException {
		DEC dec = Mod421ToDEC2026.getDEC(mod421);
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(DEC.class);
		Marshaller um = context.createMarshaller();
		um.setProperty("jaxb.encoding", "ISO-8859-1");
		um.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		um.marshal(dec, writer);
		return writer.toString();		
	}
	
	private static DEC getDEC(Mod421 mod) {
		
		DEC dec = new DEC();
		
		dec.setMOD("421"); 											  // Identificador del modelo                                  
		dec.setANY(AonNumberUtils.toString(mod.getYear()));           // Ejercicio al que se refiere la autoliquidación
		dec.setPER(mod.getPeriod().getName());                        // Período al que se refiere la autoliquidación: 1T, 2T, 3T, 4T
		dec.setADM(mod.getAdmonAeat()); 							  // Código de la administración tributaria
		dec.setACR(mod.getAmount(Mod421Key.X03) == 1.0 ? "X" : "");   // Concurso de acreedores
		
		if (mod.isComplementary()) {
			dec.setCOM("X");                     // Se establece con "X" cuando esta autoliquidación sea complementaria
			dec.setNJA(mod.getReplacedNumber()); // Para una declaración complementaria se declara el número anterior de justificante
		}
		
		addOTP(dec, mod); // Datos identificativos
		
		if (mod.isWithoutActivity()) {
			dec.setAUT(new TAUTOLIQUIDACION());
		} else {
			dec.setAUT(getAut(dec, mod)); // Autoliquidación
		}
		
		dec.setRESULTADOLIQUIDACION(getRes(mod));  // Resultado de la declaración
		
		return dec;
		
	}

	private static void addOTP(DEC dec, Mod421 mod) {
		
		DATOSPERSONALES dp = new DATOSPERSONALES();
		dp.setNIF(mod.getDocument());                          // Nif de la persona
		dp.setNRS(changeCharacters(mod.getFullName()));        // Nombre o razón social
		dp.setSVP(changeCharacters(mod.getStreetInitial()));   // Siglas vía pública
		dp.setNVP(changeCharacters(mod.getStreetName()));      // Nombre de la vía pública
		dp.setNPK(changeCharacters(mod.getStreetNumber()));    // Número de edificio/pto kilométrico
		dp.setESC(changeCharacters(mod.getStreetStair()));     // Escalera
		dp.setPIS(changeCharacters(mod.getStreetFloor()));     // Piso
		dp.setPUE(changeCharacters(mod.getStreetDoor()));      // Puerta
		dp.setLOC(changeCharacters(mod.getTown()));            // Localidad
		dp.setTEL(mod.getPhone());       	                   // Teléfono
		dp.setMOV(mod.getContactCellular());                   // Teléfono móvil
		dp.setEMA(mod.getContactEmail());                      // Email
		dp.setPOP(AonFiscalFileUtils.unsigned(Province.getByName(mod.getProvince()).ordinal(), 2)); // Código de provincia
		dp.setCMU(mod.getTownCode());                          // Código de municipio
		dp.setCP(mod.getZip());                                // Código Postal
		
		dec.getOTP().add(dp);
		
	}
	
	private static TAUTOLIQUIDACION getAut(DEC dec, Mod421 mod) {
		
		TAUTOLIQUIDACION aut = new TAUTOLIQUIDACION();
		TAUTOLIQUIDACION.EPIGRAFES epigrafes = new TAUTOLIQUIDACION.EPIGRAFES();

		for (Mod421Activity activity : mod.getActivityList()) {
			if (activity.isNotEmpty()) {
				TAUTOLIQUIDACION.EPIGRAFES.EPIGRAFE ep = new TAUTOLIQUIDACION.EPIGRAFES.EPIGRAFE();
				
				ep.setEPI(activity.getEpigraph());
				ep.setSEC(Integer.toString(activity.getSpecialEpigraph()));
				
				ep.setMOD1(getAmount(activity.getModules().get(0).getValue()));
				ep.setMOD2(getAmount(activity.getModules().get(1).getValue()));
				ep.setMOD3(getAmount(activity.getModules().get(2).getValue()));
				ep.setMOD4(getAmount(activity.getModules().get(3).getValue()));
				ep.setMOD5(getAmount(activity.getModules().get(4).getValue()));
				ep.setMOD6(getAmount(activity.getModules().get(5).getValue()));
				ep.setMOD7(getAmount(activity.getModules().get(6).getValue()));
				ep.setTOT(getAmount(activity.getDev()));
				
				ep.setINDCOR(getAmount(activity.getIct()));
				
				if (mod.isLastPeriod()) {
					ep.setCUOSOP(getAmount(activity.getSop()));
					ep.setDIASEJECUR(Integer.toString(activity.getDia()));
					ep.setCUORESULTTRIM(getAmount(activity.getCad()));				
				} else {
					ep.setDIASEJEANT(Integer.toString(activity.getTem()));
					ep.setDIASTRICUR(Integer.toString(activity.getDia()));
					ep.setCUORESTRIM(getAmount(activity.getIng()));
				}
				
				epigrafes.getEPIGRAFE().add(ep);
			}
		}
			
		aut.setEPIGRAFES(epigrafes);

		if (mod.isLastPeriod()) {
			aut.setC07(getAmount(mod, Mod421Key.C07));
			aut.setC08(getAmount(mod, Mod421Key.C08));
			aut.setC09(getAmount(mod, Mod421Key.C09));
			aut.setC10(getAmount(mod, Mod421Key.C10));
			aut.setC11(getAmount(mod, Mod421Key.C11));
			aut.setTRIM1(getAmount(mod, Mod421Key.C10T1));
			aut.setTRIM2(getAmount(mod, Mod421Key.C10T2));
			aut.setTRIM3(getAmount(mod, Mod421Key.C10T3));
		} else {
			aut.setC06(getAmount(mod, Mod421Key.C06));	
		}
		
		aut.setC12(getAmount(mod, Mod421Key.C12));
		aut.setC13(getAmount(mod, Mod421Key.C13));
		aut.setC14(getAmount(mod, Mod421Key.C14));
		aut.setC15(getAmount(mod, Mod421Key.C15));
		aut.setC16(getAmount(mod, Mod421Key.C16));
		aut.setC17(getAmount(mod, Mod421Key.C17));
		aut.setC18(getAmount(mod, Mod421Key.C18));
		aut.setC19(getAmount(mod, Mod421Key.C19));
		
		return aut;
	}
	
	// Resultado de la declaración
	private static RESULTADOLIQUIDACION getRes(Mod421 mod) {
		
		RESULTADOLIQUIDACION res = new RESULTADOLIQUIDACION();
		
		// Tipo de resultado de la liquidación
		//  	I = Ingreso
		//  	D = Devolución
		//  	C = A Compensar,
		//  	S = Sin Actividad
		// En caso de resultado de la liquidación 0, cuando
		// se ha producido actividad en el período, el tipo
		// de resultado será "C"		
		String resultType = "";
		if (mod.isWithoutActivity()) {
			resultType = "S"; // Sin actividad 
		} else if (mod.getDeclarationResult() == 0 || mod.getDeclarationResultType() == FiscalModelDeclarationType.COMPENSATE) {
			resultType = "C"; // Resultado cero o A compensar
		} else if (mod.isToDeposit()) {
			resultType = "I"; // Ingreso
		} else {
			resultType = "D"; // Devolución
		}
		res.setTIP(resultType); // Tipo de resultado de la liquidación
		
		// Importe a Ingresar, Compensar o Devolver
		if (!mod.isWithoutActivity()) {
			res.setIMP(AonFiscalFileUtils.unsigned(mod.getDeclarationResult(),3));	
		}
		
		// SI LE PONGO FORMA DE PAGO 2 ADEUDO EN CUENTA EN INGRESO, ME DA EL SIGUIENTE ERROR (AUNQUE LE PONGA IBAN), POR ESO LE PONGO PAGO TELEMATICO:
		// [forma de pago] erróneo (incoherente con [opción de presentación elegida en los parámetros de entrada])
		
		// Forma de pago ingresos
		// 1 - Efectivo
		// 2 - Adeudo en cuenta 
		// 3 - Pago fraccionado
		// 4 - Domiciliación bancaria
		// 5 - Pago telemático
		// 6 - Aplazamiento 6 meses medidas Covid
		if (mod.getDeclarationResult() > 0) {
//			String paymentMethod = "2"; // Adeudo en cuenta
			String paymentMethod = "5"; // Pago telemático
			if (mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK) {
				paymentMethod = "4"; // Domiciliación
			}
			res.setFPA(paymentMethod); // Forma de pago ingresos
		}
		
		// Código internacional de cuenta bancaria (IBAN) (Ingreso o Devolución)
		if (mod.getDeclarationResult() > 0 || mod.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK) {
			if (AonStringUtils.isNotEmpty(mod.getFinanceIban())) {
				res.setIBAN(mod.getFinanceIban()); 
			}
		}
		
		return res;
	}
	
	// --- METODOS AUXILIARES ---------------------------------------------------------------------------------------------------
	
	private static String getAmount(Mod421 mod, Mod421Key key) {
		return getAmount(mod.getAmount(key));
	}
	
	private static String getAmount(double amount) {
		return AonFiscalFileUtils.signed(amount, ' ', '-', 4).trim();
	}
	
	// CAMBIAR CARACTERES NO PERMITIDOS (ACENTOS, &, ', ETC.) Y PONER EN MAYUSCULAS
	private static String changeCharacters(String fileString) {
		fileString = AonStringUtils.trimToEmpty(fileString);
		fileString = AonStringUtils.upperCase(fileString);
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", "Y");	
		fileString = fileString.replace("Á", "A");
		fileString = fileString.replace("É", "E");
		fileString = fileString.replace("Í", "I");
		fileString = fileString.replace("Ó", "O");
		fileString = fileString.replace("Ú", "U");
		fileString = fileString.replace("Ü", "U");		
		return fileString;
	}
	
	

}
