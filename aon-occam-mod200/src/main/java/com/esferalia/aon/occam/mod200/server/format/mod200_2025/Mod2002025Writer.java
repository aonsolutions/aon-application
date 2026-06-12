package com.esferalia.aon.occam.mod200.server.format.mod200_2025;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN082Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN1039Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN1040Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN1041Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN1280Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN1344Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN2314Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN2315Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN565Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN565_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN565_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN570Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN571Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN572Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN573Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN584Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN585Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN588Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN590Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025CorrectionKey;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1212Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1494Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1535Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1579Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM538Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LQ1032Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LQ1033_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LQ1033_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LQ243Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LQ547Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LQ554Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LQ561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025RIC_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025RIC_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025RIIB_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025RIIB_2Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002025Writer {	
	
	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************

	private static final SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static final int DS = 17; // Tamaño de digitos por defecto para los importes
	private static final int DD = 2;  // Decimales por defecto para los importes
	
	private static final Mod2002025KeyDC[][] CORRECTIONS_KEYS = {
			{ Mod2002025KeyDC.DC3646, Mod2002025KeyDC.DC2650 }, // Página 26B (from, to)
			{ Mod2002025KeyDC.DC2654, Mod2002025KeyDC.DC2831 }, // Página 26C (from, to)
			{ Mod2002025KeyDC.DC2574, Mod2002025KeyDC.DC2844 }, // Página 26D (from, to)
			{ Mod2002025KeyDC.DC2984, Mod2002025KeyDC.DC3170 }, // Página 26E (from, to)
			{ Mod2002025KeyDC.DC3174, Mod2002025KeyDC.DC2293 }, // Página 26F (from, to)
			{ Mod2002025KeyDC.DC3324, Mod2002025KeyDC.DC3400 }, // Página 26G (from, to)
	};	
	
	// Añade un importe de una casilla del modelo (con signo, longitud y decimales
	// por defecto, relleno con ceros por la izquierda)
	private static void addSignedKey(Writer line, Mod2002025 mod200, IMod200Key iMod200Key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(iMod200Key), DS, DD));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002025 mod200, Mod2002025Key key, boolean isComplementary) throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			if (key == Mod2002025Key.MILLON)
				// Caso especial de esta casilla, que es un check y está en un desglose
				line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key), 1, 0)); 
			else
				line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key), DS, DD));
		}
	}

	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por
	// la izquierda)
	private static void addUnSignedKey(Writer line, Mod2002025 mod200, IMod200Key key, int size, int dec) throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key), size, dec));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002025 mod200, Mod2002025Key key, int size, int dec, boolean isComplementary) throws IOException {
		if (isComplementary)
			line.append(AonFiscalFileUtils.zeros(size));
		else
			line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key), size, dec));
	}

	// Añade la etiqueta inicio de pagina
	private static void addStartLabel(Writer line, String label) throws IOException {
		line.append("<" + label + ">");
	}

	// Añade la etiqueta fin de pagina
	private static void addEndLabel(Writer line, String label) throws IOException {
		line.append("</" + label + ">");
	}

	// Declaración Representantes Legales de la Entidad
	private static void addLegalRepresentative(Writer line, Mod2002025 mod200, int index) throws IOException {
		String name = "";
		String document = "";
		Date notaryDate = null;
		String notary = "";
		if (index < mod200.getRepresentatives().size()) {
			name = mod200.getRepresentatives().get(index).getName();
			document = mod200.getRepresentatives().get(index).getDocument();
			notaryDate = mod200.getRepresentatives().get(index).getNotaryDate();
			notary = mod200.getRepresentatives().get(index).getNotary();
		}
		// Añadir los datos al Writer
		line.append(AonFiscalFileUtils.text(name, 36));
		line.append(AonFiscalFileUtils.text(document, 9));
		line.append(AonFiscalFileUtils.dateZero(notaryDate));
		line.append(AonFiscalFileUtils.text(notary, 12));
	}

	// A. Relación de administradores
	private static void addCompanyAdministrator(Writer line, Mod2002025 mod200, int index) throws IOException {
		String document = "";
		String fj = "";
		String rpte = "0";
		String name = "";
		String residence = "";
		String province = "";
		if (index < mod200.getAdministrators().size()) {
			document = mod200.getAdministrators().get(index).getDocument();
			fj = mod200.getAdministrators().get(index).getEntity();
			rpte = mod200.getAdministrators().get(index).getRepresenStr();
			name = mod200.getAdministrators().get(index).getName();
			residence = mod200.getAdministrators().get(index).getResidence();
			if (mod200.getDoubleValue(Mod2002025Key.C0021) == 1)
				province = mod200.getAdministrators().get(index).getProvinceStr(); // Provincia solo se pone si esta marcado Caracter 021
		}
		// Añadir los datos al Writer
		line.append(AonFiscalFileUtils.text(document, 9));    // N.I.F.
		line.append(AonFiscalFileUtils.text(fj, 1));          // F/J
		line.append(AonFiscalFileUtils.text(rpte, 1));        // RPTE.
		line.append(AonFiscalFileUtils.text(name, 40));       // Apellidos y nombre / Razón social
		line.append(AonFiscalFileUtils.text(residence, 17));  // Domicilio fiscal
		line.append(AonFiscalFileUtils.text(province, 2));    // Código Provincial

	}

	// Devuelve el codigo de provincia (por defecto) o el pais, según este cumplimentado
	// uno u otro campo (province o country) de participaciones
	private static String getProvinceCountry(int province, String country) {

		if (province == 0)
			return AonStringUtils.trimToEmpty(country); // Pais
		else
			return AonStringUtils.leftPad(Integer.toString(province), 2, "0"); // Provincia

	}

	// B.1. Participaciones declarante en otras entidades
	private static void addCompanyParticipationOut(Writer line, Mod2002025 mod200, int index) throws IOException {
		String document = "";
		String name = "";
		String province = "";
		double percent = 0;
		double nominalValue = 0;
		double bookValue = 0;
		double incomes = 0;
		double aValue = 0;
		double bValue = 0;
		double cValue = 0;
		double dValue = 0;
		double eValue = 0;
		double fValue = 0;
		double gValue = 0;
		double capital = 0;
		double reserve = 0;
		double otherAmounts = 0;
		double result = 0;
		if (index < mod200.getParticipationsOut().size()) {
			document = mod200.getParticipationsOut().get(index).getDocument();
			name = mod200.getParticipationsOut().get(index).getName();
			province = getProvinceCountry(mod200.getParticipationsOut().get(index).getProvince(),mod200.getParticipationsOut().get(index).getCountry());
			percent = mod200.getParticipationsOut().get(index).getPercent();
			nominalValue = mod200.getParticipationsOut().get(index).getNominalValue();
			bookValue = mod200.getParticipationsOut().get(index).getBookValue();
			incomes = mod200.getParticipationsOut().get(index).getIncomes();
			aValue = mod200.getParticipationsOut().get(index).getValueCorrection();        // a) Corrección de valor incluida en pérdidas y ganancias ejercicio
			bValue = mod200.getParticipationsOut().get(index).getLossReversion();          // b) Reversión de pérdidas por deterioro de valores (DT 16º LIS) 
			cValue = mod200.getParticipationsOut().get(index).getAccountingElimination();  // c) Eliminación del deterioro contable incluido en P y G (art.13.2b) LIS)			
			dValue = mod200.getParticipationsOut().get(index).getValuesElimination();      // d) Eliminación del deterioro de valores repr. de partic. en el capital o fondos propios (art. 15 k) LIS)
			eValue = mod200.getParticipationsOut().get(index).getAdjustmentDecrease();     // e) Ajuste por la disminución de valor originada por criterio de valor razonable (art. 15 l) LIS)
			fValue = mod200.getParticipationsOut().get(index).getCorrectionEffect();       // f) Efecto corrección valorativa en la BI del ejercicio (= a + b + c + d + e) 
			gValue = mod200.getParticipationsOut().get(index).getCorrectionsBalance();     // g) Saldo de correcciones fiscales (art. 12.3 RDL 4/2004) pendientes a fin de ejercicio [(+) = aumentos futuros; (-) = disminuciones futuras]
			capital = mod200.getParticipationsOut().get(index).getCapital();
			reserve = mod200.getParticipationsOut().get(index).getReserve();
			otherAmounts = mod200.getParticipationsOut().get(index).getOtherAmounts();
			result = mod200.getParticipationsOut().get(index).getResult();
		}
		line.append(AonFiscalFileUtils.text(document, 15));
		line.append(AonFiscalFileUtils.text(name, 30));
		line.append(AonFiscalFileUtils.text(province, 2));
		line.append(AonFiscalFileUtils.unsigned(percent, 5, 2));
		line.append(AonFiscalFileUtils.signedZero(nominalValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(bookValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(incomes, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(aValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(bValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(cValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(dValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(eValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(fValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(gValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(capital, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(reserve, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(otherAmounts, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(result, DS, DD));
	}

	// B.2. Participaciones de personas o entidades en la declarante
	private static void addCompanyParticipationIn(Writer line, Mod2002025 mod200, int index) throws IOException {
		String document = "";
		String rpte = "0";
		String fjo = "";
		String name = "";
		String province = "";
		double nominalValue = 0;
		double percent = 0;
		if (index < mod200.getParticipationsIn().size()) {
			document = mod200.getParticipationsIn().get(index).getDocument();
			rpte = mod200.getParticipationsIn().get(index).getRepresenStr();
			fjo = mod200.getParticipationsIn().get(index).getNotary(); // F/J/Otra
			name = mod200.getParticipationsIn().get(index).getName();
			province = getProvinceCountry(mod200.getParticipationsIn().get(index).getProvince(),
					mod200.getParticipationsIn().get(index).getCountry());
			nominalValue = mod200.getParticipationsIn().get(index).getNominalValue();
			percent = mod200.getParticipationsIn().get(index).getPercent();
		}
		line.append(AonFiscalFileUtils.text(document, 15));
		line.append(AonFiscalFileUtils.text(rpte, 1));
		line.append(AonFiscalFileUtils.text(fjo, 1));
		line.append(AonFiscalFileUtils.text(name, 37));
		line.append(AonFiscalFileUtils.text(province, 2));
		line.append(AonFiscalFileUtils.signedZero(nominalValue, DS, DD));
		line.append(AonFiscalFileUtils.unsigned(percent, 5, 2));
	}
	
	// C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
	private static void addMinorEntity(Writer line, Mod2002025 mod200, int index) throws IOException {
		String document = "";
		String name = "";
		if (index < mod200.getMinorEntities().size()) {
			document = mod200.getMinorEntities().get(index).getDocument();
			name = mod200.getMinorEntities().get(index).getName();
		}
		line.append(AonFiscalFileUtils.text(document, 9));
		line.append(AonFiscalFileUtils.text(name, 40));
	}

	// UTES - Deducción para evitar la doble imposicion
	private static void addUteBase(Writer line, Mod2002025 mod200, int index) throws IOException {
		double base = 0;
		double amount = 0;
		double percent = 0;
		if (index < mod200.getUteBases().size()) {
			base = mod200.getUteBases().get(index).getBase();
			amount = mod200.getUteBases().get(index).getAmount();
			percent = mod200.getUteBases().get(index).getPercent();
		}
		line.append(AonFiscalFileUtils.signedZero(base, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(amount, DS, DD));
		line.append(AonFiscalFileUtils.unsigned(percent, 5, 2));
	}

	// UTES - Relación de Participes
	private static void addUteParticipation(Writer line, Mod2002025 mod200, int index) throws IOException {
		String document = "";
		String rpte = "0";
		String fj = "";
		String rx = ""; // Según el PADIS este dato no debe cumplimentarse si esta marcada la casilla 013, pero si se puede si esta la 014 de caracteres (aqui no se marca nunca)
		String name = "";
		String province = "";
		double base = 0;
		double percent = 0;
		if (index < mod200.getUteParticipations().size()) {
			document = mod200.getUteParticipations().get(index).getDocument();
			rpte = mod200.getUteParticipations().get(index).getRepresenStr();
			fj = mod200.getUteParticipations().get(index).getEntity();
			name = mod200.getUteParticipations().get(index).getName();
			province = getProvinceCountry(mod200.getUteParticipations().get(index).getProvince(),mod200.getUteParticipations().get(index).getCountry());
			base = mod200.getUteParticipations().get(index).getBase();
			percent = mod200.getUteParticipations().get(index).getPercent();
		}
		line.append(AonFiscalFileUtils.text(document, 9));
		line.append(AonFiscalFileUtils.text(rpte, 1));
		line.append(AonFiscalFileUtils.text(fj, 1));
		line.append(AonFiscalFileUtils.text(rx, 1));
		line.append(AonFiscalFileUtils.text(name, 34));
		line.append(AonFiscalFileUtils.text(province, 2));
		line.append(AonFiscalFileUtils.signedZero(base, DS, DD));
		line.append(AonFiscalFileUtils.unsigned(percent, 7, 4));
	}

	// UTES - Información de detalle de EP 
	private static void addUteForeign(Writer line, Mod2002025 mod200, int index) throws IOException {
		String identification = "";
		String country = "";
		double volume = 0;
		double pyg = 0;
		double adjust = 0;
		double deduction = 0;
		if (index < mod200.getUteForeign().size()) {
			identification = mod200.getUteForeign().get(index).getIdentification();
			country = mod200.getUteForeign().get(index).getCountry();
			volume = mod200.getUteForeign().get(index).getVolume();
			pyg = mod200.getUteForeign().get(index).getPyg();
			adjust = mod200.getUteForeign().get(index).getAdjust();
			deduction = mod200.getUteForeign().get(index).getDeduction();
		}
		line.append(AonFiscalFileUtils.text(identification, 20));
		line.append(AonFiscalFileUtils.text(country, 2));
		line.append(AonFiscalFileUtils.signedZero(volume, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(pyg, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(adjust, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(deduction, DS, DD));
	}

	// Cifra de Negocios - Nif Entidades de grupo (y pais si no es ES)
	private static void addGroupNIF(Writer line, Mod2002025 mod200, int index) throws IOException {
		String document = "";
		String country = "";
		if (index < mod200.getGroupEntities().size()) {
			document = mod200.getGroupEntities().get(index).getDocument();
			country = mod200.getGroupEntities().get(index).getCountry();
		}
		line.append(AonFiscalFileUtils.text(document,15));                          // NIF
		line.append(AonFiscalFileUtils.text(("ES".equals(country) ? "" : country), 2));  // Pais (solo se pone pais si no es España)
	}

	// Cifra de Negocios - Nif establecimientos permanentes
	// Producciones cinematográficas
	private static void addNIF(Writer line, LinkedList<String> list, int index) throws IOException {
		String document = "";
		if (index < list.size()) {
			document = list.get(index);
		}
		line.append(AonFiscalFileUtils.text(document, 9));
	}
	
	// F. Identificación del titular real de la entidad  
	private static void addTitularReal(Writer line, Mod2002025 mod200, int index) throws IOException {
		int documentType = 0;          // Tipo documento identificativo (0-No es aplicable, 1-DNI, NIF o NIE, 2-TIN, 3-Pasaporte, 4-Otro)                       
		String document = "";          // NIF/código de identificación extranjero            
		String name = "";              // Apellidos y nombre                                 
		String documentCountry = "";   // País de expedición del documento de identificación  
		Date birthDate = null;         // Fecha de nacimiento                                
		String residenceCountry = "";  // País de residencia                                  
		String nationality = "";       // Nacionalidad
		if (index < mod200.getTitularReal().size()) {
			documentType = mod200.getTitularReal().get(index).getDocumentType();                                 
			document = mod200.getTitularReal().get(index).getDocument();                     
			name = mod200.getTitularReal().get(index).getName();                                              
			documentCountry = mod200.getTitularReal().get(index).getDocumentCountry();  
			birthDate = mod200.getTitularReal().get(index).getBirthDate();                                         
			residenceCountry = mod200.getTitularReal().get(index).getResidenceCountry();                                  
			nationality = mod200.getTitularReal().get(index).getNationality();              
		}
		line.append(AonFiscalFileUtils.text(documentType,1));                        
		line.append(AonFiscalFileUtils.text(document,18));                    
		line.append(AonFiscalFileUtils.text(name,40));                                             
		line.append(AonFiscalFileUtils.text((documentType == 1 ? "" : documentCountry), 2));   
		line.append(AonFiscalFileUtils.dateZero(birthDate));                                        
		line.append(AonFiscalFileUtils.text(residenceCountry,2));                                   
		line.append(AonFiscalFileUtils.text(nationality,2));            
	}
	
	// UTES - Relación de Participes
	private static void addUteParticipationBis(Writer line, Mod2002025 mod200, int index) throws IOException {
		String document = "";
		String name = "";
		String province = "";
		int entityType = 0;  // Datos de la participada: Tipo de entidad
		int imputationCriteria = 0; // Criterio de imputación art. 46.2 LIS		
		double c01279 = 0;  // Datos relativos a la participación: Valoración de la participación al comienzo del período impositivo                                                                                                       
		double c01455 = 0;  // Datos relativos a la participación: Valoración de la participación al final del período impositivo                                                                                                          
		double c01456 = 0;  // Datos relativos a la participación: Ingresos financieros de la participación                                                                                                                                
		double c01458 = 0;  // Importes imputados: Importe del resultado contable imputado                                                                                                                                                 
		double c01459 = 0;  // Importes imputados: Gastos financieros netos imputados                                                                                                                                                      
		double c01460 = 0;  // Importes imputados: Reserva de capitalización que no haya sido aplicada imputada                                                                                                                            
		double c01461 = 0;  // Importes imputados: Base imponible imputada                                                                                                                                                                 
		double c01467 = 0;  // Importes imputados: Importe de la deducción generada por bases de deducción para evitar la doble imposición imputadas                                                                                       
		double c01468 = 0;  // Importes imputados: Importe bonificación generada de las bases de bonificación imputadas                                                                                                                    
		double c01523 = 0;  // Importes imputados: Importe de la deducción generada por activos fijos por bases de deducción por inversión en Canarias imputadas                                                                           
		double c01601 = 0;  // Importes imputados: Importe de la deducción generada de investigación y desarrollo e innovación tecnológica por bases de deducción por inversión en Canarias imputadas                                      
		double c01638 = 0;  // Importes imputados: Importe de la deducción generada de producciones cinematográficas españolas y espectáculos en vivo de artes escénicas y musicales por deducciones por inversión en Canarias imputadas   
		double c01639 = 0;  // Importes imputados: Importe de la deducción generada del resto de deducciones por inversión en Canarias imputadas                                                                                           
		double c01640 = 0;  // Importes imputados: Importe de la deducción generada de investigación y desarrollo e innovación tecnológica por bases de deducción imputadas                                                                
		double c01743 = 0;  // Importes imputados: Importe de la deducción generada de producciones cinematográficas españolas y espectáculos en vivo de artes escénicas y musicales por bases de deducción imputadas                      
		double c01909 = 0;  // Importes imputados: Importe del resto de deducciones generadas para incentivar determinadas actividades por bases de deducción imputadas                                                                    
		double c01910 = 0;  // Importes imputados: Importe del resto de deducciones generadas por bases de deducción imputadas no mencionadas anteriormente                                                                                
		double c01911 = 0;  // Importes imputados: Retenciones e ingresos a cuenta imputados                                                                                                                                               
		double c01912 = 0;  // Importes imputados: Dividendos y participaciones en beneficios percibidos procedentes de ejercicios anteriores a la adquisición de la participación                                                         
		double c01934 = 0;  // Importes imputados: Dividendos y participaciones en beneficios percibidos procedentes de ejercicios posteriores a la adquisición de la participación                                                        
		
		if (index < mod200.getUteParticipationsBis().size()) {
			document = mod200.getUteParticipationsBis().get(index).getDocument();
			name = mod200.getUteParticipationsBis().get(index).getName();
			province = getProvinceCountry(mod200.getUteParticipationsBis().get(index).getProvince(),mod200.getUteParticipationsBis().get(index).getCountry());
			entityType = mod200.getUteParticipationsBis().get(index).getEntityType();          
			imputationCriteria = mod200.getUteParticipationsBis().get(index).getImputationCriteria();		
			c01279 = mod200.getUteParticipationsBis().get(index).getC01279();            
			c01455 = mod200.getUteParticipationsBis().get(index).getC01455();            
			c01456 = mod200.getUteParticipationsBis().get(index).getC01456();            
			c01458 = mod200.getUteParticipationsBis().get(index).getC01458();            
			c01459 = mod200.getUteParticipationsBis().get(index).getC01459();            
			c01460 = mod200.getUteParticipationsBis().get(index).getC01460();            
			c01461 = mod200.getUteParticipationsBis().get(index).getC01461();            
			c01467 = mod200.getUteParticipationsBis().get(index).getC01467();            
			c01468 = mod200.getUteParticipationsBis().get(index).getC01468();            
			c01523 = mod200.getUteParticipationsBis().get(index).getC01523();            
			c01601 = mod200.getUteParticipationsBis().get(index).getC01601();            
			c01638 = mod200.getUteParticipationsBis().get(index).getC01638();            
			c01639 = mod200.getUteParticipationsBis().get(index).getC01639();            
			c01640 = mod200.getUteParticipationsBis().get(index).getC01640();            
			c01743 = mod200.getUteParticipationsBis().get(index).getC01743();            
			c01909 = mod200.getUteParticipationsBis().get(index).getC01909();            
			c01910 = mod200.getUteParticipationsBis().get(index).getC01910();            
			c01911 = mod200.getUteParticipationsBis().get(index).getC01911();            
			c01912 = mod200.getUteParticipationsBis().get(index).getC01912();            
			c01934 = mod200.getUteParticipationsBis().get(index).getC01934();            
		}
		line.append(AonFiscalFileUtils.text(document,15));
		line.append(AonFiscalFileUtils.text(name,40));
		line.append(AonFiscalFileUtils.text(province,2));
		line.append(entityType==1 ? "1" : "0"); // Tipo de entidad: Agrupación de interés económico española
		line.append(entityType==2 ? "1" : "0"); // Tipo de entidad: Agrupación europea de interés económico
		line.append(entityType==3 ? "1" : "0"); // Tipo de entidad: Unión temporal de empresas
		line.append(entityType==4 ? "1" : "0"); // Tipo de entidad: Colaboraciones en el extranjero análogas a las uniones temporales
		line.append(imputationCriteria==1 ? "1" :"0"); // Partícipes de agrupaciones de interés económico y UTES - Entidad 1ª - Criterio de imputación art. 46.2 LIS: En la fecha de finalización del periodo impositivo de la entidad
		line.append(imputationCriteria==2 ? "1" :"0"); // Partícipes de agrupaciones de interés económico y UTES - Entidad 1ª - Criterio de imputación art. 46.2 LIS: En el siguiente periodo impositivo
		line.append(AonFiscalFileUtils.signedZero(c01279, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01455, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01456, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01458, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01459, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01460, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01461, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01467, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01468, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01523, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01601, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01638, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01639, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01640, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01743, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01909, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01910, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01911, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01912, DS, DD));            
		line.append(AonFiscalFileUtils.signedZero(c01934, DS, DD));            
	}
	
	// Añade al writer todas las casillas que contenga el desglose que se le pasa en keysProvider
	// Existe la posibilidad de que se le indique casillas desde/hasta (util para aquellos desgloses que van en 2 paginas, por ejemplo)
	private static void addBreakdown(Writer l, Mod2002025 m, IMod200KeysProvider[] keysProvider) throws IOException {
		addBreakdown(l, m, keysProvider, null, null);
	}
	
	private static void addBreakdown(Writer l, Mod2002025 m, IMod200KeysProvider[] keysProvider, Mod2002025Key fromKey, Mod2002025Key toKey) throws IOException {
		addBreakdown(l, m, keysProvider, fromKey, toKey, false);
	}
	
	private static void addBreakdown(Writer l, Mod2002025 m, IMod200KeysProvider[] keysProvider, Mod2002025Key fromKey, Mod2002025Key toKey, boolean isComplementary) throws IOException {
		
		boolean printKey = false;
		for (IMod200KeysProvider kp : keysProvider) {
			for (int i=0;i<kp.getKeys().length;i++) {
				Mod2002025Key key = (Mod2002025Key) kp.getKeys()[i];
				if (key != null) {
					if (fromKey == null || key == fromKey)
						printKey = true;
					if (printKey) {						
						addSignedKey(l, m, key, isComplementary);
					}
					if (toKey != null && key == toKey)
						return;				
				}
			}			
		}	
	}
	
	// Desgloses de las deducciones doble imposicion (hay casillas que llevan otro formato por que son porcentajes)
    private static void addBreakdownDoubleImposition(Writer l, Mod2002025 m, IMod200KeysProvider[] keysProvider) throws IOException {
		
		for (IMod200KeysProvider kp : keysProvider) {
			int pos = 1;
			for (IMod200Key key : kp.getKeys()) {
				if (key != null) {
					// Casillas BN103x van con formato 7,4
					if (key == Mod2002025Key.BN103A || key == Mod2002025Key.BN103B || key == Mod2002025Key.BN103C || key == Mod2002025Key.BN103D)
						addUnSignedKey(l, m, key, 7, 4); 
					// Elemento 2 de cada fila es un porcentaje (formato 4,2) 	
					else if (pos == 2) 
						addUnSignedKey(l, m, key, 4, 2);
					// Resto importe normal
					else addSignedKey(l, m, key);
				}
				pos++;
			}
		}
	}

    private static void addBreakdownFromConstants(Writer l, Mod2002025 m, Mod2002025Key[][] keys) throws IOException {
    	addBreakdownFromConstants(l, m, keys, false);
	}
    
    private static void addBreakdownFromConstants(Writer l, Mod2002025 m, Mod2002025Key[][] keys, boolean isComplementary) throws IOException {
		
    	for (int i=0; i<keys.length; i++) {
    		if (keys[i] != null)
    		 for (int j=0; j<keys[i].length; j++) {
    		    Mod2002025Key key = keys[i][j];
				if (key != null) {
					addSignedKey(l, m, key, isComplementary);
				}    		    
    		 }
    	}
    	
	}
    
    private static void addBreakdownFromConstants(Writer l, Mod2002025 m, Mod2002025Key[] keys) throws IOException {
    	addBreakdownFromConstants(l, m, keys, false);
	}
    
    private static void addBreakdownFromConstants(Writer l, Mod2002025 m, Mod2002025Key[] keys, boolean isComplementary) throws IOException {
		
    	for (Mod2002025Key key : keys) {
    		if (key != null) {
				addSignedKey(l, m, key, isComplementary);
    		 }
    	}
    	
	}
    
    private static boolean addBreakdownCorrectionKeys(Writer l, Mod2002025 m, int page) throws IOException {
    	
    	Mod2002025KeyDC fromKey = CORRECTIONS_KEYS[page][0];
    	Mod2002025KeyDC toKey = CORRECTIONS_KEYS[page][1];
    	
		boolean printKey = false;
		boolean res = false; // Indica si hay alguna casilla distinta de cero
		for (Mod2002025CorrectionKey ck : Mod2002025CorrectionKey.values()) {
			
			// Desglose casilla aumento
			if (ck.getDetailIncrease() != null) {
				for (Mod2002025KeyDC key : ck.getDetailIncrease()) {
					if (key != null) {
						if (fromKey == null || (fromKey != null && key == fromKey))
							printKey = true;
						if (printKey) {							
							res = res || (m.getDoubleValue(key) != 0.0);
							if (l!=null)
								addSignedKey(l, m, key);							
						}
						if (toKey != null && key == toKey)
							return res;				
					}					
				}
			}
			
			// Desglose casilla disminucion
			if (ck.getDetailDecrease() != null) {
				for (Mod2002025KeyDC key : ck.getDetailDecrease()) {
					if (key != null) {
						if (fromKey == null || (fromKey != null && key == fromKey))
							printKey = true;
						if (printKey) {
							res = res || (m.getDoubleValue(key) != 0.0);
							if (l!=null)
								addSignedKey(l, m, key);							
						}
						if (toKey != null && key == toKey)
							return res;				
					}					
				}
			}			
		}
		return res;
	}
    
	// **** FIN VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****

	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer line, Mod2002025 mod200, String tag) throws IOException;
	}

	private enum Pages2025 {

		  PAG00("AUX",new IPropertyFiller[] {
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(70))  // Reservado para la Administración. Rellenar con blancos
				,(line, mod200, label) -> line.append("2025")                         // Versión del programa                              
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(4))   // Reservado para la Administración. Rellenar con blancos
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(AonFiscalFileUtils.DEVELOPER_NIF, 9)) // NIF Empresa Desarrollo                            
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(213)) // Reservado para la Administración. Rellenar con blancos
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG01("T20001000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> { // Tipo de declaración 
						if ("N".equals(mod200.getResultType()) || AonStringUtils.isEmpty(mod200.getResultType())) {
							line.append("N");  // Cuota Cero
						} else if ("I".equals(mod200.getResultType())) {
							line.append(AonFiscalFileUtils.text(mod200.getPayType(), 1)); // Ingreso
						} else if ("D".equals(mod200.getResultType())) {
							line.append(AonFiscalFileUtils.text(mod200.getDevType(), 1)); // Devolución
						}
					}
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDocument(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getName(), 80))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getYear(), 4, 0)) // Ejercicio
				,(line, mod200, label) -> line.append("0A") // Periodo (Constante 0A)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.dateZero(mod200.getPeriodStart()))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.dateZero(mod200.getPeriodEnd()))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPeriodType(), 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getCnae() == null ? "" : mod200.getCnae().replace(".", ""), 4))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.X0001, 1, 0) // Realiza actividades agrícolas y/o ganaderas
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone1(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone2(), 9))
				,(line, mod200, label) -> line.append(mod200.isComplementary() ? "1" : "0")
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(AonNumberUtils.todouble(mod200.getReplacedNumber()), 13, 0))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.R0001, 1, 0)  // Autoliquidación rectificativa - Motivo de la rectificación - Rectificaciones (excepto incluidas en el motivo siguiente)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.R0002, 1, 0)  // Autoliquidación rectificativa - Motivo de la rectificación - Discrepancia criterio administrativo
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.R0003, 1, 0)  // Autoliquidación rectificativa - Como consecuencia de la presentación de la autoliquidación rectificativa solicito dar de baja la domiciliación efectuada
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0001, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0002, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0080, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0003, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0008, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0004, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0005, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0011, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0085, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0013, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0014, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0017, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0018, 1, 0)								
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0019, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0021, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0023, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0024, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0025, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0031, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0032, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0036, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0048, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0058, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0060, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0066, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0078, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0056, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0006, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0015, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0079, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0022, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0028, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0047, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0049, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0035, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0029, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0069, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0086, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0033, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0034, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0038, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0046, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0012, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0064, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0057, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0062, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0020, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0007, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0009, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0010, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0081, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0082, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0026, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0027, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0030, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0039, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0043, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0045, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0087, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0063, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0071, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0088, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0083, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0070, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0059, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0090, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0065, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0084, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0072, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0073, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0037, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0044, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0074, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0089, 1, 0)
				,(line, mod200, label) -> {  // SOCIMIS: Régimen fiscal de entrada-salida. Renta derivada de la transmisión de inmuebles poseídos con anterioridad a la aplicación de este régimen y otras transmisiones de participaciones y activos a las que se aplica un tipo impositivo distinto del general (Art. 12.1 c, Art. 12.1 y Art 12.2) (Sólo si están marcados caracteres 57 o 64)
					if (mod200.getDoubleValue(Mod2002025Key.C0057)==1 || mod200.getDoubleValue(Mod2002025Key.C0064)==1) 
						addUnSignedKey(line, mod200, Mod2002025Key.C0012R, 1, 0);
					else line.append("0"); 
				 }
				,(line, mod200, label) -> line.append(mod200.getDoubleValue(Mod2002025Key.VOLOPE) == 1.0 ? "1" : "0")  // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - inferior a 20 millones de euros
				,(line, mod200, label) -> line.append(mod200.getDoubleValue(Mod2002025Key.VOLOPE) == 2.0 ? "1" : "0")  // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 20 millones de euros pero inferior a 60 millones de euros
				,(line, mod200, label) -> line.append(mod200.getDoubleValue(Mod2002025Key.VOLOPE) == 3.0 ? "1" : "0")  // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 60 millones de euros
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(21))  // RESERVADO PARA LA A.E.A.T. (Dejar en blanco) Incluye Nº Referencia
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(20))  // Identificador cliente EEDD. RESERVADO PARA LAS EEDD.              
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(50))  // Nombre y Apellidos de la persona de contacto para incidencias     
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(9))    // Teléfono fijo de contacto para incidencias                        
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(9))    // Teléfono móvil de contacto para incidencias                       
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(50))  // Dirección de correo electrónico para incidencias                  
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(13))  // SELLO ELECTRONICO RESERVADO PARA LA A.E.A.T. (Dejar en blanco)    
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // RESERVADO PARA LA AEAT                                            
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG01B("T20001B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				
				,(line, mod200, label) -> line.append(AonStringUtils.isEmpty(mod200.getFiscalGroup()) ? AonFiscalFileUtils.spaces(7) : AonFiscalFileUtils.number(mod200.getFiscalGroup(), 7))  // Grupo fiscal - Claves 00009 ó 00010 - Nº de grupo fiscal [00040]
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantDocument(), 9))               // Grupo fiscal - Claves 00009 ó 00010 - N.I.F. de la sociedad representante/dominante (incluida en el grupo fiscal)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(), 15))  // Grupo fiscal - Clave 00010 - Nº identificación de la sociedad dominante (en el caso de grupos constituidos solo por entidades depend.)
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateDocument(), 15))       				      // Grupo mercantil - Datos de la sociedad matriz última: NIF
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateName(), 40))           				      // Grupo mercantil - Datos de la sociedad matriz última: Razón social
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateGroupName(), 40))                         // Grupo mercantil - Datos de la sociedad matriz última: Nombre de grupo
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(Country.safeIso2(mod200.getUltimateResidenceCountry()), 2)) // Grupo mercantil - Identificación fiscal del país de residencia - País de residencia
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateResidenceDocument(), 15))                 // Grupo mercantil - Identificación fiscal del país de residencia - NIF en el país de residencia (TIN)
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getBalanceType() == null ? 0 : mod200.getBalanceType().ordinal() + 1, 1, 0))                                             // Balance 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned((mod200.getEcpnType() == null || mod200.getEcpnType() == EcpnType.NO_CONSTA) ? 0 : mod200.getEcpnType().ordinal() + 1, 1, 0))   // ECPN 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPygType() == null || mod200.getDoubleValue(Mod2002025Key.C0026) == 1 ? 0 : mod200.getPygType().ordinal() + 1, 1, 0))  // Pérdidas y ganancias 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0061, 1, 0)   // Estados de cuentas de Instituciones de Inversión Colectiva: Entidades que utilicen los estados de cuentas aplicables a las IIC (excepto claves 00003, 00004 y 00008) [00061]
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0068, 1, 0)   // Estados de cuentas de Entidades de Crédito: Entidades que sin ser Entidades de Crédito utilicen los estados de cuentas aplicables a éstas [00068]				
				,(line, mod200, label) -> line.append("0")                                          // Modelo de estados contables que se va a cumplimentar (No se usa, es solo para estados contables entidades de credito, entidades aseguradoras, sociedades de garantía reciproca e IIC)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0041, 9, 2)   // Personal asalariado (cifra media del ejercicio) Personal fijo [00041]
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.C0042, 9, 2)   // Personal asalariado (cifra media del ejercicio) Personal no fijo [00042]				
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // RESERVADO PARA LA AEAT                                            
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG02("T20002000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0;  // Contador para Administradores
					int b1 = 0; // Contador para Participaciones B1
					int b2 = 0; // Contador para Participaciones B2					
					while (!isComplementary || a < mod200.getAdministrators().size() || b1 < mod200.getParticipationsOut().size() || b2 < mod200.getParticipationsIn().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
		
						for (int i = 1; i <= 5; i++) {
							addCompanyAdministrator(line, mod200, a++);
						}
		
						for (int i = 1; i <= 3; i++) {
							addCompanyParticipationOut(line, mod200, b1++);
						}
		
						addUnSignedKey(line, mod200, Mod2002025Key.P1501, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002025Key.P1502, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002025Key.P1503, DS, DD, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.P1504, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.P2376, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.P1506, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.P1809, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.P1810, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.P1507, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.P1508, isComplementary);
		
						for (int i = 1; i <= 6; i++) {
							addCompanyParticipationIn(line, mod200, b2++);
						}
		
						addUnSignedKey(line, mod200, Mod2002025Key.POR51, 5, 2, isComplementary);
						addUnSignedKey(line, mod200, Mod2002025Key.PORES, 5, 2, isComplementary);
						
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG02B("T20002B00", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int c = 0;  // C. Contador para Entidades menores
					int d = 0;  // D. Contador para Información de detalle de EP
					int s1 = 0; // E. Socios de SICAV en régimen especial de disolución y liquidación (DT 41ª LIS) - NIF de la sociedad/es disuelta/s
					int s2 = 0; // E. Socios de SICAV en régimen especial de disolución y liquidación (DT 41ª LIS) - NIF de la/las IIC donde reinvierte
					int t = 0;  // F. Identificación del titular real de la entidad 
					
					while (!isComplementary || c < mod200.getMinorEntities().size() || d < mod200.getUteForeign().size() || s1 < mod200.getSicav1().size() || s2 < mod200.getSicav2().size() || t < mod200.getTitularReal().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
						
						for (int i = 1; i <= 10; i++) {
							addMinorEntity(line, mod200, c++); // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
						}
						
						for (int i = 1; i <= 18; i++) {
							addUteForeign(line, mod200, d++);  // D. Información detalle de EP
						}
						
						for (int i = 1; i <= 5; i++) {
							addNIF(line, mod200.getSicav1(), s1++); // E. Socios de SICAV - NIF de la sociedad/es disuelta/s 
						}
						
						for (int i = 1; i <= 5; i++) {
							addNIF(line, mod200.getSicav2(), s2++); // E. Socios de SICAV - NIF de la/las IIC donde reinvierte
						}
						
						addUnSignedKey(line, mod200, Mod2002025Key.NOITR, 1, 0); // No obligado a identificar el titular real - Según el diseño del registro sale en todas las hojas, incluso en las complementarias
						addTitularReal(line, mod200, t++); // F. Identificación del titular real de la entidad (solo 1 por pagina)
						
						line.append(AonFiscalFileUtils.text(isComplementary ? "" : mod200.getSecretary().getName(), 21));    // Secretario - Apellidos y Nombre
						line.append(AonFiscalFileUtils.text(isComplementary ? "" : mod200.getSecretary().getDocument(), 9)); // Secretario - NIF
						addLegalRepresentative(line, mod200, isComplementary ? 5 : 0);  // No hay mas de 3 representantes legales, por lo tanto si es hoja complementaria se le pasa 5 para que se rellene con espacios o ceros
						addLegalRepresentative(line, mod200, isComplementary ? 5 : 1);
						addLegalRepresentative(line, mod200, isComplementary ? 5 : 2);
						line.append(AonFiscalFileUtils.spaces(200));   // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG03("T20003000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA101)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA102)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA103)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA104)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA105)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA106)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA107)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA108)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA700)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA109)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA110)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA111)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA112)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA113)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA114)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA115)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA116)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA117)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA118)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA119)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA120)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA121)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA122)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA123)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA124)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA125)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA126)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA127)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA128)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA129)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA130)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA131)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA132)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA133)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA134)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA135)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA136)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA137)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA138)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA139)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA140)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA141)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA142)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA143)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA144)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA145)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA146)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA147)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA148)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA701)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG04("T20004000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA149)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA151)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA152)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA153)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA154)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA155)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA156)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA157)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA158)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA159)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA160)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA161)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA162)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA163)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA164)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA165)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA166)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA167)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA168)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA169)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA170)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA171)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA172)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA173)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA174)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA175)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA176)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA177)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA178)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA179)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BA180)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG05("T20005000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP185)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP186)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP187)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP188)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP189)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP764)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP765)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP190)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP191)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP192)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP193)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP702)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP1001)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP1002)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP712)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP766)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP767)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP194)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP195)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP196)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP197)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP198)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP199)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP768)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP769)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP201)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP202)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP203)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP204)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP205)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP206)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP207)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP208)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP209)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP780)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP781)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP782)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP783)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP784)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP211)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP212)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP213)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP214)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP215)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP216)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP217)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP218)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP219)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP220)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP221)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP222)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP223)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP224)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP225)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP226)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP227)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
	
		, PAG06("T20006000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP228)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP785)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP786)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP787)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP788)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP789)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP229)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP703)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP704)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP231)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP232)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP233)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP234)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP235)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP236)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP237)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP238)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP239)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP240)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP241)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP242)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP243)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP244)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP245)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP246)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP247)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP248)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP249)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP250)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP251)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BP252)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG07("T20007000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG255)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG256)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG257)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG711)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG705)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG706)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG707)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG708)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG258)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG259)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG260)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG261)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG760)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG761)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG262)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG762)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG763)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG770)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG771)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG772)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG263)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG264)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG265)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG266)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG267)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG268)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG269)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG790)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG273)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG274)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG275)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG276)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG277)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG278)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG279)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG253)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG254)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG281)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG282)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG283)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG709)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG284)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG287)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG288)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG289)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG290)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG291)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG292)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG293)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG710)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG791)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG792)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG793)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG294)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG295)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG296)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG08("T20008000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG297)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG298)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG299)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG300)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG794)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG304)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG306)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG796)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG310)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG311)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG312)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG313)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG315)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG316)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG317)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG318)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG319)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG320)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG321)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG322)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG323)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG329)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG324)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG325)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG326)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG327)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG328)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.PG500)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)			
			})

		, PAG09("T20009000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0336)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0337)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0338)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0339)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0340)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0341)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0342)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0343)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0345)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0346)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0347)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0348)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0349)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0350)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0351)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0352)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0353)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0354)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.T0355)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG10("T20010000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC380)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC381)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC382)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC383)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC384)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC385)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC386)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC394)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC395)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC396)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC397)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC398)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC400)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC409)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC410)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC411)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC412)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC413)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC422)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC423)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC424)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC425)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC426)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC427)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC428)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC436)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC437)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC438)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC439)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC440)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC441)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC442)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC450)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC451)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC452)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC453)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC454)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC455)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC456)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC464)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC465)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC466)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC467)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC468)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC469)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC470)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC478)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC479)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC481)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC482)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC483)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC484)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC492)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC493)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC494)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC495)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC496)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC497)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC498)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC507)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC508)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC509)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC511)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC512)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC523)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC524)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC525)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC526)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC534)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC535)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC536)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC537)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC538)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC539)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC540)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC548)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC549)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC551)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC564)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC580)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC591)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC592)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC594)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC595)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC596)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC607)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC608)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC609)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC610)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC620)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC623)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC624)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC715)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC716)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC717)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC718)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC719)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC720)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC721)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC729)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC730)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC731)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC732)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC733)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC734)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC735)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC632)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC634)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC635)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC636)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC637)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC638)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG11("T20011000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC387)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC388)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC389)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC390)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC391)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC392)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC401)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC402)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC403)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC404)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC405)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC406)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC407)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC415)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC416)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC417)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC418)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC419)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC420)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC421)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC429)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC430)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC431)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC432)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC433)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC434)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC435)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC443)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC444)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC445)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC446)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC448)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC449)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC457)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC458)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC461)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC462)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC463)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC471)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC472)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC475)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC476)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC477)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC485)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC486)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC489)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC490)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC491)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC499)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC502)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC503)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC504)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC505)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC513)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC514)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC515)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC516)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC517)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC518)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC519)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC527)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC528)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC529)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC530)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC531)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC532)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC533)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC541)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC542)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC543)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC544)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC546)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC557)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC558)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC574)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC586)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC589)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC598)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC613)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC614)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC625)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC626)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC627)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC628)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC629)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC630)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC631)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC722)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC723)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC724)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC725)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC726)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC727)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC728)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC736)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC737)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC738)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC739)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC740)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC741)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC742)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC639)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC640)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC641)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC643)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC644)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TC645)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG12("T20012000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ004)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ501)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1231)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025CorrectionKey.values(), Mod2002025Key.I3401, Mod2002025Key.D1573)  // Correcciones al resultado contable
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG13("T20013000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")						        
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025CorrectionKey.values(), Mod2002025Key.I1574, Mod2002025Key.D0414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.I0417)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.D0418)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1029)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1030)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1031)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14("T20014000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ550TG) // Parte de la base imponible del período impositivo que tributa al tipo general (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ550T0) // Parte de la base imponible del período impositivo que tributa al tipo del 0% (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1032)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ541)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ564)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1887)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1890)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1033)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1034)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ559)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ925)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1509)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1577)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.LQ558, 4, 2) // FALTA - TIPO DE GRAVAMEN HAY QUE PONER LOS DOS POSIBLES TIPOS DE GRAVAMEN
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1037)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ932)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1038)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN815)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN573)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN582)
 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14B("T20014B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1039)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN2314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN2315)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN082)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1040)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1041)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN592)				
				,(line, mod200, label) -> line.append(" ") // Inoperatividad del orden de cumplimentación de las deducciones del Tramo 2				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1785)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1786)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1787)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1788)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1789)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1790)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1791)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1792)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1793)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1794)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1795)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1796)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1797)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1798)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1799)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1766)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1784)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN601)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN612)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1234A)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN083)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1892)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1042)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1333)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1319) // Liquidación IV - Resultado de la autoliquidación - Abono de deducciones por producciones cinematográficas extranjeras en Canarias (art. 39.3 LIS y DA 14ª Ley 19/1994) - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1893) // Liquidación IV - Resultado de la autoliquidación - Abono de deducciones por producciones cinematográficas extranjeras en Canarias (art. 39.3 LIS y DA 14ª Ley 19/1994) - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1881) // Liquidación IV - Resultado de la autoliquidación - Abono de deducciones por producciones cinematográficas extranjeras en Canarias (art. 39.3 LIS y DA 14ª Ley 19/1994) - D. Forales/Navarra
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ031)  // Liquidación IV - Resultado de la autoliquidación - Discrepancia de criterio administrativo para determinados supuestos de autoliquidación rectificativa que no deban incluirse en otras casillas - Total [00031]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ032)  // Liquidación IV - Resultado de la autoliquidación - Discrepancia de criterio administrativo para determinados supuestos de autoliquidación rectificativa que no deban incluirse en otras casillas - Estado [00032]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ466)  // Liquidación IV - Resultado de la autoliquidación - Discrepancia de criterio administrativo para determinados supuestos de autoliquidación rectificativa que no deban incluirse en otras casillas - D. Forales/Navarra [00466]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1586) // Liquidación IV - Resultado de la autoliquidación - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1587) // Liquidación IV - Resultado de la autoliquidación - D. Forales/Navarra
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1578) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Resultados a ingresar procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2025 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1583) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Resultados a ingresar procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2025 - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1584) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Devoluciones acordadas procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2025 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1585) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Devoluciones acordadas procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2025 - D. Forales/Navarra (Totales)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ866)  // Liquidación IV - Rectificación - Estado [00866]
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1588) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Importe integrado en la base imponible - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2480) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Importe integrado en la base imponible - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2481) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Deuda tributaria resultante del fraccionamiento art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2482) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Deuda tributaria resultante del fraccionamiento art. 19.1 LIS - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2483) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - 1er fraccionamiento - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2484) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - 1er fraccionamiento - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2485) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2486) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS - D. Forales/Navarra (Totales)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2487) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Complementaria: Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS procedente de autoliquidaciones anteriores correspondientes al período impositivo 2025 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2488) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Complementaria: Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS procedente de autoliquidaciones anteriores correspondientes al período impositivo 2025 - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2489) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3242) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS - D. Forales/Navarra (Totales)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.ING01) // A. Ingresos previos antes de la rectificación
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.ING02) // B. Ingresos anulados en la rectificación
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(166)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG15("T20015000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1020)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1043)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LM506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1021)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN1044)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3243) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Rectificativa: Devolución acordada/compensada - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3244) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Rectificativa: Devolución acordada/compensada - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3245) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Rectificativa: Devolución acordada/compensada - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3317) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: Abono - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3318) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: Abono - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3319) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: Abono - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ3320) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: Compensación - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2490) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: Compensación - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2491) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: Compensación - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2492) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: A ingresar - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2493) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: A ingresar - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2494) // Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Rectificativa - Resultado de conversión de AID tras regularización: A ingresar - D. Forales/Navarra (Totales) 

				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LQ547Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LQ243Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
			})

		, PAG15B("T20015B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002025BN570Key.values())  
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002025BN1344Key.values())  
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN1280Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002025BN572Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG16("T20016000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002025BN571Key.values()) 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN573Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN585Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN584Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG16B("T20016B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN590Key.values(), Mod2002025Key.BN854, Mod2002025Key.BN887)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG16C("T20016C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN590Key.values(), Mod2002025Key.BN2287, Mod2002025Key.BN2080)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		 
		, PAG17("T20017000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN588Key.values(), Mod2002025Key.BN472, Mod2002025Key.BN830)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG18("T20018000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int c = 0; // Contador para Producciones Cinematográficas					
					while (!isComplementary || c < mod200.getFilmProductions().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");						
						
						addBreakdown(line, mod200, Mod2002025BN588Key.values(), Mod2002025Key.BN798, Mod2002025Key.BN2192, isComplementary);
						
						for (int i = 1; i <= 6; i++) {
							addNIF(line, mod200.getFilmProductions(), c++);
						}
						
						addBreakdown(line, mod200, Mod2002025BN588Key.values(), Mod2002025Key.BN2371, Mod2002025Key.BN3522, isComplementary);
						
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})

		, PAG18B("T20018B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN588Key.values(), Mod2002025Key.BN3523, Mod2002025Key.BN832)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN2315Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN1039Key.values())				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN2314Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
		})

		, PAG18C("T20018C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN565_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN565_2Key.values())				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN565Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN974)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG19("T20019000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN1040Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN1041Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN082Key.values(), Mod2002025Key.BN918, Mod2002025Key.BN1234A)			
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG19B("T20019B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025BN082Key.values(), Mod2002025Key.BN814, Mod2002025Key.BN130)			
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
    
		, PAG20("T20020000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.DEDUCIBLE_LIMITATION_KEYS_1)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LM1212Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LM538Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20B("T20020B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")

				 // Detalle correcciones resultado pérdidas y ganancias (totales)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2305)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2306)					
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2301)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2302)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2303)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2304)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2307)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.I0417)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.D0418)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2309)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2310)	
				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LQ1032Key.values()) 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LQ1033_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LQ1033_2Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

		, PAG20C("T20020C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LM1535Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LM1561Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LM393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LM506)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LM1579Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})
		
		, PAG20D("T20020D00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")			
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.INCOME_DISTRIBUTION_KEYS_1) // Aplicación de resultados: Base de reparto
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.INCOME_DISTRIBUTION_KEYS_2) // Aplicación de resultados: Aplicación
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LM1494Key.values())
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.RV000, 2, 0 )  // Reversión de las pérdidas por deterioro de valores representativos - Número de período impositivo (*)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RV941 )  // Reversión por deterioro de valores representativos - Dotaciones pendientes de integración a principio del período [00941]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RV2810)  // Reversión por deterioro de valores representativos - Dotaciones integradas en esta liquidación DT 16ª.1 y 2 LIS [02810]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RV990 )  // Reversión por deterioro de valores representativos - Dotaciones integradas en esta liquidación DT 16ª.3 LIS [00990]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RV991 )  // Reversión por deterioro de valores representativos - Dotaciones pendientes de integración en períodos futuros [00991]
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})
		

		, PAG21("T20021000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int i1 = 0; // Contador para NIF entidades del grupo
					int i2 = 0; // Contador para NIF establecimientos permanentes
					while (!isComplementary || i1 < mod200.getGroupEntities().size() || i2 < mod200.getEstablishments().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
		
						addSignedKey(line, mod200, Mod2002025Key.CN987, isComplementary);
		
						for (int i = 1; i <= 12; i++) {
							addGroupNIF(line, mod200, i1++);
						}
						
						addSignedKey(line, mod200, Mod2002025Key.CN1897, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.CN1901, isComplementary);
		
						addSignedKey(line, mod200, Mod2002025Key.CN988, isComplementary);
						addUnSignedKey(line, mod200, Mod2002025Key.CNEST, 3, 0, isComplementary);
		
						for (int i = 1; i <= 5; i++) {
							addNIF(line, mod200.getEstablishments(), i2++);
						}
		
						addSignedKey(line, mod200, Mod2002025Key.CN989, isComplementary);
						
						addUnSignedKey(line, mod200, Mod2002025Key.LQ0N1, 4, 0, isComplementary); 
						addSignedKey(line, mod200, Mod2002025Key.LQ630, isComplementary); 
						addSignedKey(line, mod200, Mod2002025Key.LQ631, isComplementary); 
						addSignedKey(line, mod200, Mod2002025Key.LQ632, isComplementary); 
						addSignedKey(line, mod200, Mod2002025Key.LQ579, isComplementary); 
						
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoIII()), 22));   // Documentación presentada por el Anexo III (Ajustes y deducciones)
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoIV()), 22));    // Documentación presentada por el Anexo IV (Personal investigador)
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoVric()), 22));  // Documentación presentada por el Anexo V (RIC: Inversiones anticipadas)
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoVI()), 22));    // Documentación presentada por el Anexo VI (RIIB: Inversiones anticipadas)
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoV()), 22));     // Documento normalizado presentado por el Anexo V Orden HAP/871/2016 (Art. 16.4 RIS)
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getJustCanarias()), 13));  // Número de justificante identificativo de la declaración informativa de ayudas Régimen Económico y Fiscal de Canarias
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getJustBaleares()), 13));  // Número de justificante declaración informativa de ayudas Régimen Económico y Fiscal Illes Balears
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getJustActivos()), 13));   // Número de justificante identificativo autoliquidación de la prestación patrimonial por conversión de activos (DA 13ª LIS)
						
						addUnSignedKey(line, mod200, Mod2002025Key.IPCRG01, 5, 0, isComplementary);  // Inversiones en producciones cinematográficas o series audiovisuales. Régimen general: Producciones cinematográficas (excepto series audiovisuales)
						addUnSignedKey(line, mod200, Mod2002025Key.IPCRG02, 5, 0, isComplementary);  // Inversiones en producciones cinematográficas o series audiovisuales. Régimen general: Series audiovisuales
						addUnSignedKey(line, mod200, Mod2002025Key.IPCRG03, 5, 0, isComplementary);  // Inversiones en producciones cinematográficas o series audiovisuales. Régimen general: Número de episodios
						addUnSignedKey(line, mod200, Mod2002025Key.IPCRC01, 5, 0, isComplementary);  // Inversiones en producciones cinematográficas o series audiovisuales. Régimen fiscal Canarias: Producciones cinematográficas (excepto series audiovisuales)
						addUnSignedKey(line, mod200, Mod2002025Key.IPCRC02, 5, 0, isComplementary);  // Inversiones en producciones cinematográficas o series audiovisuales. Régimen fiscal Canarias: Series audiovisuales
						addUnSignedKey(line, mod200, Mod2002025Key.IPCRC03, 5, 0, isComplementary);  // Inversiones en producciones cinematográficas o series audiovisuales. Régimen fiscal Canarias: Número de episodios
						
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})

		, PAG22("T20022000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025RIC_1Key.values()) 	// Régimen especial de la reserva para inversiones en Canarias - RIC
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RC927)		  	// Importe de la dotación RIC con cargo a beneficios de 2025		
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025RIC_2Key.values(), Mod2002025Key.RC3629, null) 	// Régimen especial de la reserva para inversiones en Canarias - Inversiones anticipadas (LAS PRIMERAS CASILLAS DE ESTE DESGLOSE, VAN AL FINAL DE ESTA PAGINA)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LQ554Key.values()) 	// Régimen de cooperativas - Determinación de la base imponible
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025LQ561Key.values()) 	// Régimen de cooperativas - Detalle de compensación de cuotas
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RC3647)  		// Rég. especial reserva inversiones Canarias - Inversiones anticipadas 2022 - Pendiente de dotar RIC a principio de período [03647]        
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RC3648)  		// Rég. especial reserva inversiones Canarias - Inversiones anticipadas 2023 - Pendiente de dotar RIC a principio de período [03648]      
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RC3649)  		// Rég. especial reserva inversiones Canarias - Inversiones anticipadas 2023 - Pendiente de dotar RIC al final del período [03649]        
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(149)) 				// Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})
		
		, PAG22B("T20022B00", new IPropertyFiller[] {   
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025RIIB_1Key.values())  // Régimen especial de la reserva para inversiones en las Illes Balears - RIIB
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.RB2918)		    // Importe de la dotación RIIB con cargo a beneficios de 2025		
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002025RIIB_2Key.values())  // Régimen especial de la reserva para inversiones en las Illes Balears - Inversiones anticipadas				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

		// [...] NO ESTA EN EL MODELO - Página 23: Operaciones fusión, escisión, canje de valores.
		
		, PAG24("T20024000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int i1 = 0; // Contador para Deducción para Evitar la doble imposición
					while (!isComplementary || i1 < mod200.getUteBases().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
		
						addUnSignedKey(line, mod200, Mod2002025Key.UT060, 7, 4, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.UT500, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.UT1227, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.UT1228, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.UT552, isComplementary);
						addSignedKey(line, mod200, Mod2002025Key.UT1330, isComplementary);
		
						// B6.- Deducción para evitar la doble imposición
						for (int i = 1; i <= 6; i++) {      
							addUteBase(line, mod200, i1++);
						}						
						addSignedKey(line, mod200, Mod2002025Key.UT1277, isComplementary); // B6.- Deduc. evitar doble imposición: Total: Base de la deducción [01277]
						addSignedKey(line, mod200, Mod2002025Key.UT1278, isComplementary); // B6.- Deduc. evitar doble imposición: Total: Importe de la deducción [01278]
						
						addBreakdownFromConstants(line, mod200, Mod2002025Constants.UTE_KEYS_B7, isComplementary);  // B7.- Bonificaciones 
						addBreakdownFromConstants(line, mod200, Mod2002025Constants.UTE_KEYS_B8_1, isComplementary); // B8.- Deducciones generadas en el periodo impositivo
						addBreakdownFromConstants(line, mod200, Mod2002025Constants.UTE_KEYS_B8_2, isComplementary); // Información adicional para el cálculo del límite de deducciones
						
						addSignedKey(line, mod200, Mod2002025Key.UT062, isComplementary);  // B9.- Retenciones e ingresos a cuenta  [00062]
						addSignedKey(line, mod200, Mod2002025Key.UT070, isComplementary);  // B10.- Dividendos y participaciones. a) Ejercicios que no haya tributado en régimen especial [00070]
						addSignedKey(line, mod200, Mod2002025Key.UT072, isComplementary);  // B10.- Dividendos y participaciones. b) Ejercicios que haya tributado en régimen especial [00072]
								
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG24B("T20024B00", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int i1 = 0; // Contador para Relación de Partícipes
					int i2 = 0; // Contador para Partícipes de Agrupaciones de interés económico y UTES 
					
					while (!isComplementary || i1 < mod200.getUteParticipations().size() || i2 < mod200.getUteParticipationsBis().size() ) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
						
						// Relación de Partícipes
						for (int i = 1; i <= 10; i++) {
							addUteParticipation(line, mod200, i1++);
						}
						
						// Partícipes de Agrupaciones de interés económico y UTES
						for (int i = 1; i <= 3; i++) {
							addUteParticipationBis(line, mod200, i2++);
						}						
						
						addBreakdownFromConstants(line, mod200, Mod2002025Constants.UTE_PARTICIPATION_BIS_KEYS, isComplementary); // TOTALES Partícipes de Agrupaciones de interés económico y UTES
			
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})

		// [...] NO ESTA EN EL MODELO - Página 25: Régimen especial de transparencia fiscal internacional

		, PAG26("T20026000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TR050)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TR051)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TR052)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TR053)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TR054)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TR055)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.TR056)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.TR626, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.TR627, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.TR628, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.TR629, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002025Key.TR625, 5, 2)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.COMBINED_TAXATION_3_1)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.COMBINED_TAXATION_3_3)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.COMBINED_TAXATION_4)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.COMBINED_TAXATION_5)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002025Constants.COMBINED_TAXATION_3_2)  // CASILLAS [02378][02379][02407][02408][00466] VAN AL FINAL DE LA PAGINA Y NO ENTREMEDIAS DE COMBINED_TAXATION_3
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(115)) // Reservado para la AEAT 
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26B("T20026B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria				
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, 0)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26C("T20026C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, 1)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26D("T20026D00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, 2)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26E("T20026E00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, 3)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26F("T20026F00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, 4)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26G("T20026G00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, 5)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2306)					
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2302)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2304)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.DC2310)	
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})

		// [...] NO ESTAN EN EL MODELO - Páginas 27 a 54

		, DID("T200DID00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) // Etiqueta de inicio de pagina
				,(line, mod200, label) -> line.append(" ")           // Indicador de pagina complementaria
				,(line, mod200, label) -> line.append("0")           // Cuenta corriente tributaria "0" o "1" (no se usa)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getYear(), 4, 0))       // Identificación - Ejercicio
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPeriodType(), 1, 0)) // Tipo de ejercicio
				,(line, mod200, label) -> line.append("0A")                                                      // Período Impositivo "0A"
				,(line, mod200, label) -> line.append(mod200.getPeriodStart() == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodStart())) // Período Impositivo Inicio (ddmmaa)
				,(line, mod200, label) -> line.append(mod200.getPeriodEnd() == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodEnd()))     // Período Impositivo Fin (ddmmaa)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDocument(), 9))  // Identificación -  NIF				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getName(), 80))     // Identificación - Apellidos y nombre o Razón Social
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ552)                // Liquidación - Base imponible [552]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ562)                // Liquidación - Cuota íntegra [562]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ1586)               // Liquidación - Resultado de la autoliquidación [01586]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.BN621)                // Liquidación - Resultado: Estado [621]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002025Key.LQ2489)               // Liquidación - Opción de fraccionamiento art. 19.1 LIS - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS: Estado 
				
				,(line, mod200, label) -> {
					
					double importe = mod200.getDoubleValue(Mod2002025Key.BN621); // importe a ingresar o a devolver
					double ingreso = 0.0;
					double devolucion = 0.0;
					double rectificacion = mod200.getDoubleValue(Mod2002025Key.LQ866);
					
					if (importe > 0) {
						ingreso = importe;
					} else if (importe < 0) {
						devolucion = Math.abs(importe) - rectificacion;
					}

					line.append(AonFiscalFileUtils.text(devolucion > 0 ? ("V".equals(mod200.getDevType()) ? "" : mod200.getDevType()) : "", 1)); // Devolución - Renuncia o por Transferencia ("blanco","R","D")
					line.append(AonFiscalFileUtils.signedZero(devolucion, DS, DD)); 						                                     // Devolución - Importe a devolver
					
					line.append(AonFiscalFileUtils.signedZero(rectificacion, DS, DD));  // Rectificación - Solicito que el importe que, en su caso, pudiera resultar a devolver como consecuencia de la rectificación, me sea abonado mediante transferencia bancaria en la cuenta de la que soy titular [00866]
					
					boolean ponerCuentaDevolucion = (devolucion > 0 && "D".equals(mod200.getDevType())) || (rectificacion > 0); // Cuenta devolucion se indica si devolucion mayor que cero y solitita devolucion o rectificacion mayor que cero
					line.append(ponerCuentaDevolucion ? "1" : "0");  // Cuenta Bancaria - Marca SEPA (0 Vacía, 1 Cuenta España, 2 Unión Europea SEPA, 3 Resto Países) (Se asume cuenta de España)					
                    line.append(AonFiscalFileUtils.text(ponerCuentaDevolucion ? mod200.getIban() : "", 34));  // Cuenta Bancaria - Número de cuenta IBAN (si devolución por transferencia)
					line.append(AonFiscalFileUtils.spaces(11));  // Cuenta Bancaria - Código SWIFT-BIC (No pongo nada porque se supone que si es de España no debe indicarse nada, ya que si ponemos algo al cargar el archivo par la presentacion en la AEAT, lo pone por defecto en el apartado de cuenta extranjera UE)
					line.append(AonFiscalFileUtils.spaces(70));  // Cuenta Bancaria - Banco/Bank name (Cuenta bancaria abierta en el extranjero, fuera de la unión europea, se asume que la cuenta es de España)
					line.append(AonFiscalFileUtils.spaces(35));  // Cuenta Bancaria - Dirección del Banco/ Bank adress (Cuenta bancaria abierta en el extranjero, fuera de la unión europea, se asume que la cuenta es de España)
					line.append(AonFiscalFileUtils.spaces(30));  // Cuenta Bancaria - Ciudad/City (Cuenta bancaria abierta en el extranjero, fuera de la unión europea, se asume que la cuenta es de España)					
					line.append(AonFiscalFileUtils.spaces( 2));  // Cuenta Bancaria - Código País/Country code (Cuenta bancaria abierta en el extranjero, fuera de la unión europea, se asume que la cuenta es de España)
					
					line.append(AonFiscalFileUtils.text(ingreso > 0 ? mod200.getPayType() : "", 1));  // Ingreso - Modalidad de ingreso. Uno de los siguientes valores "blanco", "I" Ingreso, "U" Domiciliación
					if (mod200.getDoubleValue(Mod2002025Key.LQ2489) > 0) 
					   line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(Mod2002025Key.LQ2489), DS, DD));  // Ingreso - Importe a ingresar
					else
					   line.append(AonFiscalFileUtils.signedZero(ingreso, DS, DD));  // Ingreso - Importe a ingresar
					line.append(AonFiscalFileUtils.text(ingreso > 0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType()))	? mod200.getIban() : "", 34));  // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)
					
					addSignedKey(line, mod200, Mod2002025Key.BN1020); // Abono/Compensación - Abono por conversión de activos impuesto diferido - A
					addSignedKey(line, mod200, Mod2002025Key.BN1021); // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C
					addSignedKey(line, mod200, Mod2002025Key.LQ3318); // Abono/Compensación - Resultado de conversión de AID tras regularización: Abono 
					addSignedKey(line, mod200, Mod2002025Key.LQ2490); // Abono/Compensación - Resultado de conversión de AID tras regularización: Compensación 
					addSignedKey(line, mod200, Mod2002025Key.LQ2493); // Abono/Compensación - Resultado de conversión de AID tras regularización: A ingresar 

					line.append(ingreso == 0 && devolucion == 0 ? "1" : "0");  // Resultado Cero "0" o "1"
					
				}				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina

		});

		private String tag;
		private IPropertyFiller[] propertyFillers;
		
		private Pages2025(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002025 mod200, Writer line) throws IOException {

			// Controles determinadas páginas que solo se ponen si están marcados ciertos caracteres
			boolean addPage = true;

			// Páginas 9 a 11. ECPN. Si están marcadas las casillas 75, 76 o 77
			if (this == Pages2025.PAG09 || this == Pages2025.PAG10 || this == Pages2025.PAG11) {
				addPage = (mod200.getEcpnType() == EcpnType.NORMAL) || (mod200.getEcpnType() == EcpnType.ABREVIADO) || (mod200.getEcpnType() == EcpnType.PYMES);
			}

			// Página 22. Regimen especial de la reserva para inversiones en Canarias y Cooperativas
			if (this == Pages2025.PAG22) {
				addPage = ( mod200.isChecked(Mod2002025Key.C0029) || mod200.isCooperativa() );
			}			
			
			// Página 22 BIS. Regimen especial de la reserva para inversiones en Illes Balears y Cooperativas
			if (this == Pages2025.PAG22B) {
				addPage = ( mod200.isChecked(Mod2002025Key.C0086) || mod200.isCooperativa() );
			}

			// Página 24. Agrupaciones de interes económico y UTES (regimen especial). Caracteres 013, 085, 014 marcados
			if (this == Pages2025.PAG24) { 
				addPage = (mod200.isChecked(Mod2002025Key.C0013) || mod200.isChecked(Mod2002025Key.C0085) || mod200.isChecked(Mod2002025Key.C0014));
			}
			
			// Página 24 BIS. Agrupaciones de interes económico y UTES (regimen especial). Caracteres 013, 085, 014, 089 marcados
			if (this == Pages2025.PAG24B) { 
				addPage = (mod200.isChecked(Mod2002025Key.C0013) || mod200.isChecked(Mod2002025Key.C0085) || mod200.isChecked(Mod2002025Key.C0014) || mod200.isChecked(Mod2002025Key.C0089));
			}

			// Página 26. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2025.PAG26) {
				addPage = (mod200.isChecked(Mod2002025Key.C0028));
			}
			
			// Página 26B: Solo si hay algún importe en la pagina
			if (this == Pages2025.PAG26B) {				
				addPage = addBreakdownCorrectionKeys(null, mod200, 0);
			}
			
			// Página 26C: Solo si hay algún importe en la pagina			
			if (this == Pages2025.PAG26C) {
				addPage = addBreakdownCorrectionKeys(null, mod200, 1);
			}
			
			// Página 26D: Solo si hay algún importe en la pagina
			if (this == Pages2025.PAG26D) {
				addPage = addBreakdownCorrectionKeys(null, mod200, 2);
			}

			// Página 26E: Solo si hay algún importe en la pagina
			if (this == Pages2025.PAG26E) {
				addPage = addBreakdownCorrectionKeys(null, mod200, 3);
			}
			
			// Página 26F: Solo si hay algún importe en la pagina
			if (this == Pages2025.PAG26F) {
				addPage = addBreakdownCorrectionKeys(null, mod200, 4);
			}
			
			// Página 26G: Solo si hay algún importe en la pagina
			if (this == Pages2025.PAG26G) {
				addPage = addBreakdownCorrectionKeys(null, mod200, 5) ||
						  mod200.getDoubleValue(Mod2002025Key.DC2305) != 0 ||
						  mod200.getDoubleValue(Mod2002025Key.DC2301) != 0 ||
						  mod200.getDoubleValue(Mod2002025Key.DC2303) != 0 ||
						  mod200.getDoubleValue(Mod2002025Key.DC2307) != 0 ||
						  mod200.getDoubleValue(Mod2002025Key.DC2309) != 0 ||
						  mod200.getDoubleValue(Mod2002025Key.DC2306) != 0 ||					
						  mod200.getDoubleValue(Mod2002025Key.DC2302) != 0 ||	
						  mod200.getDoubleValue(Mod2002025Key.DC2304) != 0 ||	
						  mod200.getDoubleValue(Mod2002025Key.DC2308) != 0 ||
						  mod200.getDoubleValue(Mod2002025Key.DC2310) != 0;
			}
			
			// Añadir el contenido de la página
			if (addPage) {
				for (IPropertyFiller propertyFiller : this.propertyFillers) {
					propertyFiller.propertyFill(line, mod200, this.tag);
				}
			}
		}

	}

	public static void fillWriter(Mod2002025 mod200, Writer line) throws IOException {

		line.append("<T2000" + mod200.getYear() + "0A0000>"); // Etiqueta inicio de fichero
		for (Pages2025 page : Pages2025.values()) {
			page.fillPage(mod200, line);
		}
		line.append("</T2000" + mod200.getYear() + "0A0000>"); // Etiqueta fin de fichero
		line.close();

	}

}
