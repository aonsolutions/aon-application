package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019.EcpnType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN082Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN1040Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN1041Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN1280Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN1344Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN565Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN570Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN571Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN572Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN573Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN584Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN585Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN588Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN590Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019KeyDC;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019LQ1032Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019LQ1033_1Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019LQ1033_2Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019LQ547Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019LQ554Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019LQ561Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002019Writer {

	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************

	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static int DS = 17; // Tamaño de digitos por defecto para los importes
	private static int DD = 2; // Decimales por defecto para los importes

	// Añade un importe de una casilla del modelo (con signo, longitud y decimales
	// por defecto, relleno con ceros por la izquierda)
	private static void addSignedKey(Writer line, Mod2002019 mod200, IMod200Key iMod200Key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(iMod200Key), DS, DD));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002019 mod200, Mod2002019Key key, boolean isComplementary)
			throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key), DS, DD));
		}
	}

	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por
	// la izquierda)
	private static void addUnSignedKey(Writer line, Mod2002019 mod200, IMod200Key key, int size, int dec)
			throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key), size, dec));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002019 mod200, Mod2002019Key key, int size, int dec,
			boolean isComplementary) throws IOException {

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
	private static void addLegalRepresentative(Writer line, Mod2002019 mod200, int index) throws IOException {
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
	private static void addCompanyAdministrator(Writer line, Mod2002019 mod200, int index) throws IOException {
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
			if (mod200.getDoubleValue(Mod2002019Key.C0021) == 1)
				province = mod200.getAdministrators().get(index).getProvinceStr(); // Provincia solo se pone si esta marcado Caracter 021
		}
		// Añadir los datos al Writer
		line.append(AonFiscalFileUtils.text(document, 9)); // N.I.F.
		line.append(AonFiscalFileUtils.text(fj, 1)); // F/J
		line.append(AonFiscalFileUtils.text(rpte, 1)); // RPTE.
		line.append(AonFiscalFileUtils.text(name, 40)); // Apellidos y nombre / Razón social
		line.append(AonFiscalFileUtils.text(residence, 17)); // Domicilio fiscal
		line.append(AonFiscalFileUtils.text(province, 2)); // Código Provincial

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
	private static void addCompanyParticipationOut(Writer line, Mod2002019 mod200, int index) throws IOException {
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
		double ccValue = 0;
		double dValue = 0;
		double ddValue = 0;
		double eValue = 0;
		double capital = 0;
		double reserve = 0;
		double otherAmounts = 0;
		double result = 0;
		if (index < mod200.getParticipationsOut().size()) {
			document = mod200.getParticipationsOut().get(index).getDocument();
			name = mod200.getParticipationsOut().get(index).getName();
			province = getProvinceCountry(mod200.getParticipationsOut().get(index).getProvince(),
					mod200.getParticipationsOut().get(index).getCountry());
			percent = mod200.getParticipationsOut().get(index).getPercent();
			nominalValue = mod200.getParticipationsOut().get(index).getNominalValue();
			bookValue = mod200.getParticipationsOut().get(index).getBookValue();
			incomes = mod200.getParticipationsOut().get(index).getIncomes();
			aValue = mod200.getParticipationsOut().get(index).getaValue();
			bValue = mod200.getParticipationsOut().get(index).getbValue();
			cValue = mod200.getParticipationsOut().get(index).getcValue();
			ccValue = mod200.getParticipationsOut().get(index).getccValue();
			dValue = mod200.getParticipationsOut().get(index).getdValue();
			ddValue = mod200.getParticipationsOut().get(index).getddValue();
			eValue = mod200.getParticipationsOut().get(index).geteValue();
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
		line.append(AonFiscalFileUtils.signedZero(ccValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(ddValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(eValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(cValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(dValue, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(capital, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(reserve, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(otherAmounts, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(result, DS, DD));
	}

	// B.2. Participaciones de personas o entidades en la declarante
	private static void addCompanyParticipationIn(Writer line, Mod2002019 mod200, int index) throws IOException {
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
	private static void addMinorEntity(Writer line, Mod2002019 mod200, int index) throws IOException {
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
	private static void addUteBase(Writer line, Mod2002019 mod200, int index) throws IOException {
		double base = 0;
		double percent = 0;
		if (index < mod200.getUteBases().size()) {
			base = mod200.getUteBases().get(index).getBase();
			percent = mod200.getUteBases().get(index).getPercent();
		}
		line.append(AonFiscalFileUtils.signedZero(base, DS, DD));
		line.append(AonFiscalFileUtils.unsigned(percent, 5, 2));
	}

	// UTES - Relación de Socios
	private static void addUteParticipation(Writer line, Mod2002019 mod200, int index) throws IOException {
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

	// UTES - Información de detalle de EP o UTE que operen en el extranjero...
	private static void addUteForeign(Writer line, Mod2002019 mod200, int index) throws IOException {
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
	private static void addGroupNIF(Writer line, Mod2002019 mod200, int index) throws IOException {
		String document = "";
		String country = "";
		if (index < mod200.getGroupEntities().size()) {
			document = mod200.getGroupEntities().get(index).getDocument();
			country = mod200.getGroupEntities().get(index).getCountry();
		}
		line.append(AonFiscalFileUtils.text(document,15));                        // NIF 
		//line.append(AonFiscalFileUtils.spaces(5));                                // Reservado para la AEAT
		line.append(AonFiscalFileUtils.text((country == "ES" ? "" : country), 2));  // Pais (solo se pone pais si no es España)
	}

	// Cifra de Negocios - Nif establecimientos permanentes
	private static void addNIF(Writer line, LinkedList<String> list, int index) throws IOException {
		String document = "";
		if (index < list.size()) {
			document = list.get(index);
		}
		line.append(AonFiscalFileUtils.text(document, 9));
	}
	
	// Añade al writer todas las casillas que contenga el desglose que se le pasa en keysProvider
	// Existe la posibilidad de que se le indique casillas desde/hasta (util para aquellos desgloses que van en 2 paginas, por ejemplo)
	private static void addBreakdown(Writer l, Mod2002019 m, IMod200KeysProvider[] keysProvider) throws IOException {
		addBreakdown(l, m, keysProvider, null, null);
	}
	
	private static void addBreakdown(Writer l, Mod2002019 m, IMod200KeysProvider[] keysProvider, Mod2002019Key fromKey, Mod2002019Key toKey) throws IOException {
		
		boolean printKey = false;
		for (IMod200KeysProvider kp : keysProvider) {
			for (int i=0;i<kp.getKeys().length;i++) {
				Mod2002019Key key = (Mod2002019Key) kp.getKeys()[i];
				if (key != null) {
					if (fromKey == null || (fromKey != null && key == fromKey))
						printKey = true;
					if (printKey) {						
						addSignedKey(l, m, key);
					}
					if (toKey != null && key == toKey)
						return;				
				}
			}			
		}	
	}
	
	// Desgloses de las deducciones doble imposicion (hay casillas que llevan otro formato por que son porcentajes)
    private static void addBreakdownDoubleImposition(Writer l, Mod2002019 m, IMod200KeysProvider[] keysProvider) throws IOException {
		
		for (IMod200KeysProvider kp : keysProvider) {
			int pos = 1;
			for (IMod200Key key : kp.getKeys()) {
				if (key != null) {
					// Casillas BN103x van con formato 7,2
					if (key == Mod2002019Key.BN103A || key == Mod2002019Key.BN103B || key == Mod2002019Key.BN103C || key == Mod2002019Key.BN103D)
						addUnSignedKey(l, m, key, 7, 4); // Según los errores que da el fichero, se está esperando 4 decimales
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

    private static void addBreakdownFromConstants(Writer l, Mod2002019 m, Mod2002019Key[][] keys) throws IOException {
		
    	for (int i=0; i<keys.length; i++) {
    		if (keys[i] != null)
    		 for (int j=0; j<keys[i].length; j++) {
    		    Mod2002019Key key = keys[i][j];
    		    // En el desglose de la Tributación Conjunta, las casillas 2313, 2314, 2315, 2316 y 2312 que en el modelo están en el centro del desglose, en el fichero las ponen al final, por eso aqui no se ponen
				if (key != null && key != Mod2002019Key.TR2313 && key != Mod2002019Key.TR2314 && key != Mod2002019Key.TR2315 && key != Mod2002019Key.TR2316 && key != Mod2002019Key.TR2312 ) {
					addSignedKey(l, m, key);
				}
    		 }
    	}
    	
	}
    
	private static boolean addBreakdownCorrectionKeys(Writer l, Mod2002019 m, Mod2002019KeyDC fromKey, Mod2002019KeyDC toKey) throws IOException {
		
		boolean printKey = false;
		boolean res = false; // Indica si hay alguna casilla distinta de cero
		for (Mod2002019CorrectionKey ck : Mod2002019CorrectionKey.values()) {
			
			// Desglose casilla aumento
			if (ck.getDetailIncrease() != null) {
				for (Mod2002019KeyDC key : ck.getDetailIncrease()) {
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
				for (Mod2002019KeyDC key : ck.getDetailDecrease()) {
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
		public void propertyFill(Writer line, Mod2002019 mod200, String tag) throws IOException;
	}

	private enum Pages2019 {

		  PAG00("AUX",new IPropertyFiller[] {
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(70))  // Reservado para la Administración. Rellenar con blancos
				,(line, mod200, label) -> line.append("2020")                         // Versión del programa                              
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
							line.append("N");
						} else if ("I".equals(mod200.getResultType())) {
							line.append(AonFiscalFileUtils.text(mod200.getPayType(), 1)); // Tipo de declaración
						} else if ("D".equals(mod200.getResultType())) {
							line.append(AonFiscalFileUtils.text(mod200.getDevType(), 1)); // Tipo de declaración
						}
					}
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterpriseDocument(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterpriseName(), 80))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getYear(), 4, 0)) // Ejercicio
				,(line, mod200, label) -> line.append("0A") // Periodo (Constante 0A)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.dateZero(mod200.getPeriodStart()))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.dateZero(mod200.getPeriodEnd()))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPeriodType(), 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getCnae() == null ? "" : mod200.getCnae().replace(".", ""), 4))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone1(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone2(), 9))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0001, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0002, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0080, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0003, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0004, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0005, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0011, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0013, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0014, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0017, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0018, 1, 0)								
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0019, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0021, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0023, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0024, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0025, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0031, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0032, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0036, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0048, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0058, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0060, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0066, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0078, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0006, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0015, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0079, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0022, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0028, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0047, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0049, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0035, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0029, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0033, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0034, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0038, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0046, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0012, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0064, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0057, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0062, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0020, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0007, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0009, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0010, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0081, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0082, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0016, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0026, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0027, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0030, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0039, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0043, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0045, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0063, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0071, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0070, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0059, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0065, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0067, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0072, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0073, 1, 0)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getBalanceType() == null ? 0 : mod200.getBalanceType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned((mod200.getEcpnType() == null || mod200.getEcpnType() == EcpnType.NO_CONSTA) ? 0 : mod200.getEcpnType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPygType() == null || mod200.getDoubleValue(Mod2002019Key.C0026) == 1 ? 0 : mod200.getPygType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0061, 1, 0)				
				,(line, mod200, label) -> line.append("0") // Modelo de estados contables que se va a cumplimentar (No se usa, es solo para estados contables entidades de credito, entidades aseguradoras, sociedades de garantía reciproca e IIC)
				,(line, mod200, label) -> {  // SOCIMIS: Régimen fiscal de entrada-salida. Renta derivada de la transmisión de inmuebles poseídos con anterioridad a la aplicación de este régimen y otras transmisiones de participaciones y activos a las que se aplica un tipo impositivo distinto del general (Art. 12.1 c, Art. 12.1 y Art 12.2) (Sólo si están marcados caracteres 57 o 64)
					if (mod200.getDoubleValue(Mod2002019Key.C0057)==1 || mod200.getDoubleValue(Mod2002019Key.C0064)==1) 
						addUnSignedKey(line, mod200, Mod2002019Key.C0012R, 1, 0);
					else line.append("0"); 
				 }
				,(line, mod200, label) -> line.append(AonStringUtils.isEmpty(mod200.getFiscalGroup()) ? AonFiscalFileUtils.spaces(7) : AonFiscalFileUtils.unsigned(mod200.getFiscalGroup(), 7, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantDocument(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(), 15))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateDocument(), 15))       // Grupo - Clave 00081 - Datos de la sociedad matriz última: NIF o equivalente.
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateDocumentCountry(), 2)) // Grupo - Clave 00081 - Datos de la sociedad matriz última: Código país
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateName(), 40))           // Grupo - Clave 00081 - Datos de la sociedad matriz última: Nombre o razón social
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateCountry(), 2))         // Grupo - Clave 00081 - Datos de la sociedad matriz última: País o jurisdicción
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0041, 9, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.C0042, 9, 2)
				,(line, mod200, label) -> line.append(mod200.isComplementary() ? "1" : "0")
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(AonNumberUtils.todouble(mod200.getComplementaryReceipt()), 13, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getSecretary().getName(), 21))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getSecretary().getDocument(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.dateZero(mod200.getSecretary().getIrnr()))
				,(line, mod200, label) -> addLegalRepresentative(line, mod200, 0)
				,(line, mod200, label) -> addLegalRepresentative(line, mod200, 1)
				,(line, mod200, label) -> addLegalRepresentative(line, mod200, 2)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(21))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(20))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(50))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(50))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(13))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.X0001, 1, 0) // Declaración correspondiente al art. 124.1 LIS sin aprobar cuentas anuales
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.X0002, 1, 0) // Nueva declaración art. 12.2 RDLey 19/2020
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(198)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG02("T20002000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para Administradores
					int b1 = 0; // Contador para Participaciones B1
					int b2 = 0; // Contador para Participaciones B2
					int c = 0; // Contador para Entidades menores
					while (!isComplementary || a < mod200.getAdministrators().size() || b1 < mod200.getParticipationsOut().size() || b2 < mod200.getParticipationsIn().size() || c < mod200.getMinorEntities().size() ) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
		
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						//addCompanyAdministrator(line, mod200, a++);
		
						addCompanyParticipationOut(line, mod200, b1++);
						addCompanyParticipationOut(line, mod200, b1++);
						addCompanyParticipationOut(line, mod200, b1++);
		
						addUnSignedKey(line, mod200, Mod2002019Key.P1501, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002019Key.P1502, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002019Key.P1503, DS, DD, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.P1504, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.P1505, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.P1506, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.P1809, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.P1810, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.P1507, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.P1508, isComplementary);
		
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
		
						addUnSignedKey(line, mod200, Mod2002019Key.POR51, 5, 2, isComplementary);
						addUnSignedKey(line, mod200, Mod2002019Key.PORES, 5, 2, isComplementary);
						
						addMinorEntity(line, mod200, c++);
						addMinorEntity(line, mod200, c++);
						addMinorEntity(line, mod200, c++);
						addMinorEntity(line, mod200, c++);
		
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG03("T20003000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA101)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA102)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA103)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA104)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA105)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA106)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA107)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA108)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA700)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA109)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA110)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA111)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA112)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA113)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA114)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA115)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA116)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA117)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA118)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA119)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA120)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA121)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA122)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA123)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA124)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA125)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA126)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA127)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA128)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA129)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA130)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA131)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA132)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA133)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA134)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA135)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA136)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA137)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA138)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA139)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA140)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA141)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA142)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA143)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA144)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA145)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA146)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA147)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA148)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA701)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG04("T20004000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA149)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA151)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA152)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA153)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA154)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA155)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA156)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA157)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA158)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA159)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA160)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA161)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA162)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA163)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA164)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA165)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA166)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA167)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA168)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA169)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA170)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA171)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA172)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA173)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA174)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA175)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA176)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA177)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA178)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA179)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BA180)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG05("T20005000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP185)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP186)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP187)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP188)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP189)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP190)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP191)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP192)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP193)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP702)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP1001)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP1002)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP194)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP195)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP196)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP197)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP198)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP199)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP201)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP202)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP203)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP204)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP205)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP206)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP207)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP208)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP209)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP211)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP212)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP213)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP214)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP215)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP216)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP217)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP218)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP219)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP220)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP221)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP222)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP223)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP224)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP225)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP226)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP227)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
	
		, PAG06("T20006000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP228)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP229)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP703)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP704)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP231)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP232)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP233)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP234)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP235)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP236)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP237)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP238)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP239)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP240)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP241)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP242)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP243)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP244)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP245)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP246)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP247)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP248)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP249)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP250)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP251)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BP252)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG07("T20007000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG255)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG256)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG257)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG711)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG705)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG706)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG707)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG708)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG258)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG259)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG260)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG261)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG760)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG761)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG262)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG762)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG763)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG263)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG264)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG265)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG266)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG267)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG268)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG269)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG273)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG274)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG275)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG276)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG277)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG278)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG279)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG281)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG282)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG283)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG709)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG284)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG287)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG288)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG289)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG290)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG291)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG292)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG293)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG710)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG294)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG295)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG296)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG08("T20008000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG297)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG298)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG299)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG300)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG304)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG306)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG310)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG311)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG312)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG313)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG315)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG316)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG317)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG318)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG319)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG320)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG321)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG322)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG323)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG329)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG324)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG325)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG326)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG327)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG328)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.PG500)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)			
			})

		, PAG09("T20009000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0336)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0337)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0338)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0339)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0340)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0341)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0342)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0343)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0345)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0346)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0347)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0348)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0349)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0350)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0351)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0352)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0353)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0354)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.T0355)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG10("T20010000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC380)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC381)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC382)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC383)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC384)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC385)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC386)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC394)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC395)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC396)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC397)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC398)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC400)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC409)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC410)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC411)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC412)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC413)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC422)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC423)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC424)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC425)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC426)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC427)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC428)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC436)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC437)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC438)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC439)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC440)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC441)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC442)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC450)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC451)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC452)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC453)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC454)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC455)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC456)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC464)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC465)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC466)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC467)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC468)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC469)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC470)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC478)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC479)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC481)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC482)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC483)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC484)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC492)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC493)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC494)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC495)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC496)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC497)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC498)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC507)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC508)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC509)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC511)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC512)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC523)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC524)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC525)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC526)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC534)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC535)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC536)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC537)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC538)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC539)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC540)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC548)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC549)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC551)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC564)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC580)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC591)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC592)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC594)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC595)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC596)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC607)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC608)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC609)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC610)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC620)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC623)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC624)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC715)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC716)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC717)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC718)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC719)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC720)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC721)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC729)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC730)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC731)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC732)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC733)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC734)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC735)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC632)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC634)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC635)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC636)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC637)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC638)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG11("T20011000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC387)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC388)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC389)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC390)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC391)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC392)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC401)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC402)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC403)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC404)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC405)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC406)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC407)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC415)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC416)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC417)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC418)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC419)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC420)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC421)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC429)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC430)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC431)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC432)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC433)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC434)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC435)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC443)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC444)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC445)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC446)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC448)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC449)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC457)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC458)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC461)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC462)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC463)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC471)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC472)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC475)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC476)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC477)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC485)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC486)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC489)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC490)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC491)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC499)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC502)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC503)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC504)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC505)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC513)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC514)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC515)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC516)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC517)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC518)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC519)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC527)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC528)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC529)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC530)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC531)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC532)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC533)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC541)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC542)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC543)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC544)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC546)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC557)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC558)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC574)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC586)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC589)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC598)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC613)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC614)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC625)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC626)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC627)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC628)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC629)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC630)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC631)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC722)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC723)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC724)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC725)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC726)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC727)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC728)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC736)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC737)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC738)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC739)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC740)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC741)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC742)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC639)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC640)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC641)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC643)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC644)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TC645)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG12("T20012000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ501)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1231)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.VOLOPE, 1, 0) // Volumen Operaciones
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019CorrectionKey.values(), Mod2002019Key.I0355, Mod2002019Key.D2185)  // Correcciones al resultado contable
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG13("T20013000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
						        
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019CorrectionKey.values(), Mod2002019Key.I2186, Mod2002019Key.D0414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.I0417)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.D0418)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1029)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1030)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1031)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ550TG) // Parte de la base imponible del período impositivo que tributa al tipo general (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ550T0) // Parte de la base imponible del período impositivo que tributa al tipo del 0% (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1032)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1033)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1034)
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14("T20014000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ559)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1509)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.LQ558, 4, 2)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1037)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ1038)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN573)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1039)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN082)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1040)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1041)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN592)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14B("T20014B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1785)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1786)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1787)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1788)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1789)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1790)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1791)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1792)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1793)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1794)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1795)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1796)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1797)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1798)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1799)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN601)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN620)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1234A)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN083)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1042)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1333)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1020)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1043)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1021)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN1044)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN2311) // Nueva declaración art. 12.2.b) RDLey 19/2020 no complementaria: importe que resultó a ingresar / devolución solicitada en la declaración originaria - Estado        
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN2312) // Nueva declaración art. 12.2.b) RDLey 19/2020 no complementaria: importe que resultó a ingresar / devolución solicitada en la declaración originaria - D. Forales/Navarra (Totales)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces( 34)) // Reservado para la AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(166)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG15("T20015000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019LQ547Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002019BN570Key.values())  
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002019BN1344Key.values()) 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN1280Key.values()) 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
			})

		, PAG16("T20016000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002019BN572Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002019BN571Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN573Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN585Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG16B("T20016B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN584Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN590Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG17("T20017000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN588Key.values(), null, Mod2002019Key.BN2192)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG18("T20018000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN588Key.values(), Mod2002019Key.BN1081, null)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
		})

		, PAG18B("T20018B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				
				,(line, mod200, label) -> {
					// Si la casilla 565 tiene contenido, se ponen los checks que se indican, si no, se dejan en blanco
					if (mod200.getDoubleValue(Mod2002019Key.BN565) > 0 ) {
						addUnSignedKey(line, mod200, Mod2002019Key.BN565A, 1, 0); // Deducción donativos entidades sin fines de lucro. Ley 49/2002. Indique si entre las deducciones que se van a aplicar hay actividades prioritarias de mecenazgo 
						addUnSignedKey(line, mod200, Mod2002019Key.BN565B, 1, 0); // Deducción donativos entidades sin fines de lucro. Ley 49/2002. Indique si en los dos períodos impositivos inmediatos anteriores hubieran realizado donaciones o aportaciones con derecho a deducción a favor de una misma entidad por importe igual o superior en cada uno de ellos, al del período impositivo anterior
					}
					else {
						line.append("0");
						line.append("0");
					}
				}				
				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN565Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN1040Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN1041Key.values())
			
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG19("T20019000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019BN082Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID650)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID651)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID652)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID653)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID654)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID1270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID1271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID1522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID655)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID656)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID658)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID659)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID660)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID662)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID664)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID665)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.ID666)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2301)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección permanente (excluida corrección I. Sociedades) - Del ejercicio - Aumentos [02301]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2302)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección permanente (excluida corrección I. Sociedades) - Del ejercicio - Disminuciones [02302]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2303)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en el ejercicio - Del ejercicio - Aumentos [02303]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2304)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en el ejercicio - Del ejercicio - Disminuciones [02304]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2305)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en el ejercicio - Saldo pendiente - Aumentos futuros [02305]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2306)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en el ejercicio - Saldo pendiente - Disminuciones futuras [02306]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2307)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en ejerc. anteriores - Del ejercicio - Aumentos [02307]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2308)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en ejerc. anteriores - Del ejercicio - Disminuciones [02308]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2309)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en ejerc. anteriores - Saldo pendiente - Aumentos futuros [02309]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2310)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Corrección temporaria con origen en ejerc. anteriores - Saldo pendiente - Disminuciones futuras [02310]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.I0417)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Del ejercicio - Aumentos  [00417]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.D0418)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Del ejercicio - Disminuciones [00418]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2295)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Saldo pendiente - Aumentos futuros [02295]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.DC2296)	// Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Saldo pendiente - Disminuciones futuras [02296]
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20("T20020000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_1)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_2)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_3)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20B("T20020B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019LQ1032Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019LQ1033_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019LQ1033_2Key.values())
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.DOTACION_KEYS_1)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.NUMPER, 1, 0)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM1515)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM1516)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM1585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM1517)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

		, PAG20C("T20020C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.DOTACION_KEYS_3)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.DOTACION_KEYS_4)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LM506)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.DOTACION_KEYS_6)				
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
		
						addSignedKey(line, mod200, Mod2002019Key.CN987, isComplementary);
		
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
						addGroupNIF(line, mod200, i1++);
		
						addSignedKey(line, mod200, Mod2002019Key.CN988, isComplementary);
						addUnSignedKey(line, mod200, Mod2002019Key.CNEST, 3, 0, isComplementary);
		
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
		
						addSignedKey(line, mod200, Mod2002019Key.CN989, isComplementary);
						
						line.append(!isComplementary && mod200.getDoubleValue(Mod2002019Key.VOLOPE) == 1.0?"1":"0"); // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - inferior a 20 millones de euros
						line.append(!isComplementary && mod200.getDoubleValue(Mod2002019Key.VOLOPE) == 2.0?"1":"0"); // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 20 millones de euros pero inferior a 60 millones de euros
						line.append(!isComplementary && mod200.getDoubleValue(Mod2002019Key.VOLOPE) == 3.0?"1":"0"); // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 60 millones de euros
		
						addUnSignedKey(line, mod200, Mod2002019Key.LQ0N1, 4, 0, isComplementary); 
						addSignedKey(line, mod200, Mod2002019Key.LQ630, isComplementary); 
						addSignedKey(line, mod200, Mod2002019Key.LQ631, isComplementary); 
						addSignedKey(line, mod200, Mod2002019Key.LQ632, isComplementary); 
						addSignedKey(line, mod200, Mod2002019Key.LQ579, isComplementary); 
						
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoIII()), 22));
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoIV()), 22));
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoV()), 22));
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getJustCanarias()), 13));
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getJustActivos()), 13));
		                
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})

		, PAG22("T20022000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.CANARIAS_KEYS)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019LQ554Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002019LQ561Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

//		, PAG23("T20023000", new IPropertyFiller[] { 
//				 (line, mod200, label) -> addStartLabel(line, label)
//				,(line, mod200, label) -> line.append(" ")
//				,(line, mod200, label) -> {
//					// [...] NO ESTA EN EL MODELO - Operaciones fusión, escisión, canje de valores.
//					for (int i = 0; i < 5; i++) {
//						line.append(AonFiscalFileUtils.spaces(1));
//						line.append(AonFiscalFileUtils.spaces(9));
//						line.append(AonFiscalFileUtils.spaces(40));
//						line.append(AonFiscalFileUtils.spaces(9));
//						line.append(AonFiscalFileUtils.spaces(40));
//						line.append(AonFiscalFileUtils.zeros(8));
//						line.append(AonFiscalFileUtils.zeros(DS));
//						line.append(AonFiscalFileUtils.zeros(DS));
//						line.append(AonFiscalFileUtils.zeros(DS));
//					}
//					line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
//				}
//				, (line, mod200, label) -> addEndLabel(line, label) 
//			})

		, PAG24("T20024000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int i1 = 0; // Contador para Deducción para Evitar la doble imposición
					int i2 = 0; // Contador para Relación de Socios
					int i3 = 0; // Contador para Información de detalle de EP o UTE
					while (!isComplementary || i1 < mod200.getUteBases().size() || i2 < mod200.getUteParticipations().size() || i3 < mod200.getUteForeign().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
		
						addUnSignedKey(line, mod200, Mod2002019Key.UT060, 7, 4, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UT500, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UT1227, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UT1228, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UT552, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UT1330, isComplementary);
		
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
		
						addSignedKey(line, mod200, Mod2002019Key.UTC01, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UTC02, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UTC03, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UT062, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UTC04, isComplementary);
						addSignedKey(line, mod200, Mod2002019Key.UTC05, isComplementary);
		
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						addUteParticipation(line, mod200, i2++);
						
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
						addUteForeign(line, mod200, i3++);
								
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
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR050)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR051)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR052)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR053)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR054)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR055)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR056)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.TR626, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.TR627, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.TR628, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.TR629, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002019Key.TR625, 5, 2)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002019Constants.COMBINED_TAXATION_3)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR2313)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR2314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR2315)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR2316)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.TR2312)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(115)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26B("T20026B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria				
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002019KeyDC.DC2501, Mod2002019KeyDC.DC2680)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26C("T20026C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002019KeyDC.DC2681, Mod2002019KeyDC.DC2860)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26D("T20026D00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002019KeyDC.DC2861, Mod2002019KeyDC.DC3040)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26E("T20026E00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002019KeyDC.DC3041, Mod2002019KeyDC.DC3220)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26F("T20026F00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002019KeyDC.DC3221, Mod2002019KeyDC.DC3400)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})

		// [...] NO ESTAN EN EL MODELO - Páginas 27 a 54

		, DID("T200DID00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) // Etiqueta de inicio de pagina
				,(line, mod200, label) -> line.append(" ") // Indicador de pagina complementaria
				,(line, mod200, label) -> line.append("0") // Cuenta corriente tributaria "0" o "1" (no se usa)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getYear(), 4, 0)) // Identificación - Ejercicio
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPeriodType(), 1, 0)) // Tipo de ejercicio
				,(line, mod200, label) -> line.append("0A") // Período Impositivo "0A"
				,(line, mod200, label) -> line.append(mod200.getPeriodStart() == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodStart())) // Período Impositivo Inicio (ddmmaa)
				,(line, mod200, label) -> line.append(mod200.getPeriodEnd() == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodEnd())) // Período Impositivo Fin (ddmmaa)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterpriseDocument(), 9)) // Identificación -  NIF				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterpriseName(), 80)) // Identificación - Apellidos y nombre o Razón Social
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ552) // Liquidación - Base imponible [552]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.LQ562) // Liquidación - Cuota íntegra [562]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002019Key.BN621) // Liquidación - Líquido a ingresar o a devolver Estado [621]

				,(line, mod200, label) -> {
					
					double importe = mod200.getDoubleValue(Mod2002019Key.BN621); // importe a ingresar o a devolver

					line.append(AonFiscalFileUtils.text(importe < 0 ? ("V".equals(mod200.getDevType()) ? "" : mod200.getDevType()) : "", 1)); // Devolución - Renuncia o por Transferencia ("blanco","R","D")
					line.append(AonFiscalFileUtils.signedZero(importe < 0 ? Math.abs(importe) : 0.0, DS, DD)); 						          // Devolución - Importe a devolver
					line.append(importe < 0 && "D".equals(mod200.getDevType()) ? "1" : "0");                                                  // Devolución - Marca SEPA (0 Vacía, 1 Cuenta España, 2 Unión Europea SEPA, 3 Resto Países) (Se asume cuenta de España)					
                    line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "", 34));         // Devolución - Número de cuenta IBAN (si devolución por transferencia)
					line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getBic() : "", 11));          // Devolución - Código SWIFT-BIC
					line.append(AonFiscalFileUtils.text(importe > 0 ? mod200.getPayType() : "", 1));    // Ingreso - Modalidad de ingreso. Uno de los siguientes valores "blanco", "I" Adeudo en cuenta, "U" Domiciliación
					line.append(AonFiscalFileUtils.signedZero(importe > 0 ? importe : 0.0, DS, DD));    // Ingreso - Importe a ingresar
					line.append(AonFiscalFileUtils.text(importe > 0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType()))	? mod200.getIban() : "", 34));  // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)

					addSignedKey(line, mod200, Mod2002019Key.LM150); // Abono/Compensación - Abono por conversión de activos impuesto diferido - A
					addSignedKey(line, mod200, Mod2002019Key.LM506); // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C

					line.append(importe == 0 ? "1" : "0");   // Cuota Cero "0" o "1"
					
					// Datos bancarios devolucion por transferencia a cuenta bancaria abierta en el extranjero (fuera de la unión europea) (Se asume que la cuenta es de España)
					line.append(AonFiscalFileUtils.spaces(70)); // Devolución - Banco/Bank name
					line.append(AonFiscalFileUtils.spaces(35)); // Devolución - Dirección del Banco/ Bank adress
					line.append(AonFiscalFileUtils.spaces(30)); // Devolución - Ciudad/City					
					line.append(AonFiscalFileUtils.spaces( 2)); // Devolución - Código País/Country code
					
				}
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina

		});

		private String tag;
		private IPropertyFiller[] propertyFillers;

		private Pages2019(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002019 mod200, Writer line) throws IOException {

			// Controles determinadas páginas que solo se ponen si están marcados ciertos
			// caracteres
			boolean addPage = true;

			// Página 9 a 11. ECPN. Si están marcadas las casillas 75, 76 o 77
			if (this == Pages2019.PAG09 || this == Pages2019.PAG10 || this == Pages2019.PAG11) {
				addPage = (mod200.getEcpnType() == EcpnType.NORMAL) || (mod200.getEcpnType() == EcpnType.ABREVIADO) || (mod200.getEcpnType() == EcpnType.PYMES);
			}

			// Página 22. Regimen especial de la reserva para inversiones en Canarias y
			// Cooperativas
			if (this == Pages2019.PAG22) {
				addPage = (mod200.getDoubleValue(Mod2002019Key.C0029) == 1)
						|| (mod200.getDoubleValue(Mod2002019Key.C0017) == 1)
						|| (mod200.getDoubleValue(Mod2002019Key.C0018) == 1)
						|| (mod200.getDoubleValue(Mod2002019Key.C0019) == 1);
			}

			// Página 24. Agrupaciones de interes económico y UTES (regimen especial).
			// Caracteres 013 o 014 marcados
			if (this == Pages2019.PAG24) {
				addPage = (mod200.isChecked(Mod2002019Key.C0013) || mod200.isChecked(Mod2002019Key.C0014));
			}

			// Página 26. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2019.PAG26) {
				addPage = (mod200.getDoubleValue(Mod2002019Key.C0028) == 1);
			}
			
			// Páginas 26B: Solo si hay algún importe en la pagina
			if (this == Pages2019.PAG26B) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002019KeyDC.DC2501, Mod2002019KeyDC.DC2680);
			}
			
			// Páginas 26C: Solo si hay algún importe en la pagina			
			if (this == Pages2019.PAG26C) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002019KeyDC.DC2681, Mod2002019KeyDC.DC2860);
			}
			
			// Páginas 26D: Solo si hay algún importe en la pagina
			if (this == Pages2019.PAG26D) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002019KeyDC.DC2861, Mod2002019KeyDC.DC3040);
			}

			// Páginas 26E: Solo si hay algún importe en la pagina
			if (this == Pages2019.PAG26E) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002019KeyDC.DC3041, Mod2002019KeyDC.DC3220);
			}
			
			// Páginas 26F: Solo si hay algún importe en la pagina
			if (this == Pages2019.PAG26F) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002019KeyDC.DC3221, Mod2002019KeyDC.DC3400);
			}
			
			// Añadir el contenido de la página
			if (addPage) {
				for (IPropertyFiller propertyFiller : this.propertyFillers) {
					propertyFiller.propertyFill(line, mod200, this.tag);
				}
			}
		}
	}

	public static void fillWriter(Mod2002019 mod200, Writer line) throws IOException {

		line.append("<T2000" + mod200.getYear() + "0A0000>"); // Etiqueta inicio de fichero
		for (Pages2019 page : Pages2019.values()) {
			page.fillPage(mod200, line);
		}
		line.append("</T2000" + mod200.getYear() + "0A0000>"); // Etiqueta fin de fichero
		line.close();

	}

//	private static void setDoubleValue2019(Mod2002019 mod200, Mod2002019Key key, double value) {
//
//		DoubleVariable2019 t = mod200.getVariable(key);
//		if (t == null) {
//			t = new DoubleVariable2019(key);
//			t.setValue(value);
//			mod200.addVariable(t);
//		} else {
//			t.setValue(value);
//		}
//	}
	
//	public static void main(String argv[])
//			throws IOException, InterruptedException, ParserConfigurationException, SAXException {
//
//		// Prueba para ver si la longitud de todas las páginas es correcta
//		try {
//
//			// Creamos un nuevo modelo 200 2019
//			Mod2002019 mod200 = new Mod2002019();
//
//			// Inicializamos las casillas con un valor aleatorio
//			for (Mod2002019Key key : Mod2002019Key.values()) {
//				double value = AonNumberUtils.todouble(AonRandomStringUtils.randomNumeric(4));
//				value = (AonMathUtils.isZero(value)) ? value : AonMathUtils.round(value / 100);
//				setDoubleValue2019(mod200, key, value);
//			}
//
//			// Periodo inicio y fin
//			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//			mod200.setPeriodStart(sdf.parse("01-01-2019"));
//			mod200.setPeriodEnd(sdf.parse("31-12-2019"));
//			mod200.setYear(2019);
//
//			// Caracteres todos a cero excepto 9, 13, 28 y 29 para que salgan todas las
//			// páginas
//			for (Mod2002019Key key : Mod2002019Character.CHARACTERS_KEYS) {
//				setDoubleValue2019(mod200, key, 0.0);
//			}
//			setDoubleValue2019(mod200, Mod2002019Key.C0009, 1.0);
//			setDoubleValue2019(mod200, Mod2002019Key.C0028, 1.0);
//			setDoubleValue2019(mod200, Mod2002019Key.C0029, 1.0);
//			setDoubleValue2019(mod200, Mod2002019Key.C0013, 1.0);
//
//			// Estados de cuentas de IIC
//			setDoubleValue2019(mod200, Mod2002019Key.C0061, 0.0);
//
//			// Numero de periodo impositivo
//			setDoubleValue2019(mod200, Mod2002019Key.NUMPER, 0.0);
//
//			// Balance, ECPN y Cuenta PyG Normal
//			mod200.setBalanceType(BalanceType.NORMAL);
//			mod200.setEcpnType(EcpnType.NORMAL);			
//			mod200.setPygType(BalanceType.NORMAL);
//			
//			// Volumen de operaciones
//			setDoubleValue2019(mod200, Mod2002019Key.VOLOPE, 0.0);			
//
//			// Añadir un administrador para que salga la pagina 2
////			if (mod200.getAdministrators() == null ) {
////				mod200.setAdministrators( new LinkedList<CompanyAdministrator>());
////			}
////
////		    // Crear un objeto y asignar los datos		
////			CompanyAdministrator ca = new CompanyAdministrator();
////			ca.setDocument("12345678Z");
////			ca.setRepresentative(true);
////			ca.setName("ADMINISTRADOR DE PRUEBA");
////			ca.setResidence("DOMICILIO FISCAL");
////			ca.setProvince(50);
////			
////			// Añadirlo a la lista
////			mod200.getAdministrators().add(ca);
//
//			// Generamos el fichero
//			String filename = "c:\\tmp\\prueba_M200_2019.txt";
//			BufferedWriter line = new BufferedWriter(new FileWriter(filename));
//			fillWriter(mod200, line);
//
//			// Mostramos la longitud de cada pagina
//			FileInputStream input = new FileInputStream(filename);
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			Document doc = db.parse(new InputSource(new InputStreamReader(input, "ISO-8859-1")));
//
//			// Longitudes de todas las páginas
//			HashMap<String, Integer> m = new HashMap<String, Integer>();
//			m.put(Pages2019.PAG00.tag,    0);
//			m.put(Pages2019.PAG01.tag,  954);
//			m.put(Pages2019.PAG02.tag, 2288);
//			m.put(Pages2019.PAG03.tag, 1074);
//			m.put(Pages2019.PAG04.tag,  768);
//			m.put(Pages2019.PAG05.tag, 1006);
//			m.put(Pages2019.PAG06.tag,  683);
//			m.put(Pages2019.PAG07.tag, 1108);
//			m.put(Pages2019.PAG08.tag,  853);
//			m.put(Pages2019.PAG09.tag, 581);
//			m.put(Pages2019.PAG10.tag, 2723);
//			m.put(Pages2019.PAG11.tag, 2502);
//			m.put(Pages2019.PAG12.tag, 1823);
//			m.put(Pages2019.PAG13.tag, 1771);
//			m.put(Pages2019.PAG14.tag, 1095);
//			m.put(Pages2019.PAG14B.tag,1074); 
//			m.put(Pages2019.PAG15.tag, 2598);
//			m.put(Pages2019.PAG16.tag, 2508);
//			m.put(Pages2019.PAG16B.tag,2417);
//			m.put(Pages2019.PAG17.tag, 2451);
//			m.put(Pages2019.PAG18.tag, 3233);
//			m.put(Pages2019.PAG18B.tag,1841);
//			m.put(Pages2019.PAG19.tag, 2009); 
//			m.put(Pages2019.PAG20.tag, 1686);
//			m.put(Pages2019.PAG20B.tag,2146);
//			m.put(Pages2019.PAG20C.tag,1941);
//			m.put(Pages2019.PAG21.tag,  694);
//			m.put(Pages2019.PAG22.tag, 2077);
//			//m.put(Pages2019.PAG23.tag, 1054);
//			m.put(Pages2019.PAG24.tag, 2126);
//			// m.put(Pages2019.PAG25.tag, 3685);
//			m.put(Pages2019.PAG26.tag, 1558);			
//			m.put(Pages2019.PAG26B.tag, 3199);
//			m.put(Pages2019.PAG26C.tag, 2604);
//			m.put(Pages2019.PAG26D.tag, 3029);
//			m.put(Pages2019.PAG26E.tag, 3199);
//			m.put(Pages2019.PAG26F.tag, 3029);
//			m.put(Pages2019.DID.tag,    672);
//
//			for (Pages2019 page : Pages2019.values()) {
//				NodeList nodeList = doc.getElementsByTagName(page.tag);
//				for (int i = 0; i < nodeList.getLength(); i++) {
//					Node node = nodeList.item(i);
//					String lin = "<" + page.tag + ">" + node.getTextContent() + "</" + page.tag + ">";
//					System.out.println(AonStringUtils.rightPad(node.getNodeName(), 9) + " - "
//							+ AonStringUtils.leftPad(AonNumberUtils.toString(lin.length()), 4) + " - "
//							+ AonStringUtils.leftPad(m.get(page.tag).toString(), 4)
//							+ (lin.length() != m.get(page.tag) ? " **" : ""));
//				}
//			}
//
//			System.out.println("");
//			System.out.println("***** Fin Fichero : " + filename);
//			System.out.println("");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			System.exit(0);
//		}
//	}
	

}
