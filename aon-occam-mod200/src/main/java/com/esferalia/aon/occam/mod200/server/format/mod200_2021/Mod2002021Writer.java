package com.esferalia.aon.occam.mod200.server.format.mod200_2021;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN082Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1040Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1041Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1280Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1344Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN570Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN571Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN572Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN573Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN584Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN585Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN588Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN590Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021CorrectionKey;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1212Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1494Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1535Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1579Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM538Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LQ1032Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LQ1033_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LQ1033_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LQ547Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LQ554Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LQ561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021RIC_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021RIC_2Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002021Writer {

	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************

	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static int DS = 17; // Tamaño de digitos por defecto para los importes
	private static int DD = 2; // Decimales por defecto para los importes
	
	// Añade un importe de una casilla del modelo (con signo, longitud y decimales
	// por defecto, relleno con ceros por la izquierda)
	private static void addSignedKey(Writer line, Mod2002021 mod200, IMod200Key iMod200Key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(iMod200Key), DS, DD));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002021 mod200, Mod2002021Key key, boolean isComplementary)
			throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key), DS, DD));
		}
	}

	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por
	// la izquierda)
	private static void addUnSignedKey(Writer line, Mod2002021 mod200, IMod200Key key, int size, int dec)
			throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key), size, dec));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002021 mod200, Mod2002021Key key, int size, int dec,
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
	private static void addLegalRepresentative(Writer line, Mod2002021 mod200, int index) throws IOException {
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
	private static void addCompanyAdministrator(Writer line, Mod2002021 mod200, int index) throws IOException {
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
			if (mod200.getDoubleValue(Mod2002021Key.C0021) == 1)
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
	private static void addCompanyParticipationOut(Writer line, Mod2002021 mod200, int index) throws IOException {
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
			aValue = mod200.getParticipationsOut().get(index).getValueCorrection();			
			bValue = mod200.getParticipationsOut().get(index).getAccountingElimination();			
			cValue = mod200.getParticipationsOut().get(index).getValuesElimination();
			dValue = mod200.getParticipationsOut().get(index).getAdjustmentDecrease();
			eValue = mod200.getParticipationsOut().get(index).getCorrectionEffect();
			fValue = mod200.getParticipationsOut().get(index).getCorrectionsBalance();
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
		line.append(AonFiscalFileUtils.signedZero(capital, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(reserve, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(otherAmounts, DS, DD));
		line.append(AonFiscalFileUtils.signedZero(result, DS, DD));
	}

	// B.2. Participaciones de personas o entidades en la declarante
	private static void addCompanyParticipationIn(Writer line, Mod2002021 mod200, int index) throws IOException {
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
	private static void addMinorEntity(Writer line, Mod2002021 mod200, int index) throws IOException {
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
	private static void addUteBase(Writer line, Mod2002021 mod200, int index) throws IOException {
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
	private static void addUteParticipation(Writer line, Mod2002021 mod200, int index) throws IOException {
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
	private static void addUteForeign(Writer line, Mod2002021 mod200, int index) throws IOException {
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
	private static void addGroupNIF(Writer line, Mod2002021 mod200, int index) throws IOException {
		String document = "";
		String country = "";
		if (index < mod200.getGroupEntities().size()) {
			document = mod200.getGroupEntities().get(index).getDocument();
			country = mod200.getGroupEntities().get(index).getCountry();
		}
		line.append(AonFiscalFileUtils.text(document,15));                          // NIF 
		line.append(AonFiscalFileUtils.text((country == "ES" ? "" : country), 2));  // Pais (solo se pone pais si no es España)
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
	
	// Añade al writer todas las casillas que contenga el desglose que se le pasa en keysProvider
	// Existe la posibilidad de que se le indique casillas desde/hasta (util para aquellos desgloses que van en 2 paginas, por ejemplo)
	private static void addBreakdown(Writer l, Mod2002021 m, IMod200KeysProvider[] keysProvider) throws IOException {
		addBreakdown(l, m, keysProvider, null, null);
	}
	
	private static void addBreakdown(Writer l, Mod2002021 m, IMod200KeysProvider[] keysProvider, Mod2002021Key fromKey, Mod2002021Key toKey) throws IOException {
		addBreakdown(l, m, keysProvider, fromKey, toKey, false);
	}
	
	private static void addBreakdown(Writer l, Mod2002021 m, IMod200KeysProvider[] keysProvider, Mod2002021Key fromKey, Mod2002021Key toKey, boolean isComplementary) throws IOException {
		
		boolean printKey = false;
		for (IMod200KeysProvider kp : keysProvider) {
			for (int i=0;i<kp.getKeys().length;i++) {
				Mod2002021Key key = (Mod2002021Key) kp.getKeys()[i];
				if (key != null) {
					if (fromKey == null || (fromKey != null && key == fromKey))
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
    private static void addBreakdownDoubleImposition(Writer l, Mod2002021 m, IMod200KeysProvider[] keysProvider) throws IOException {
		
		for (IMod200KeysProvider kp : keysProvider) {
			int pos = 1;
			for (IMod200Key key : kp.getKeys()) {
				if (key != null) {
					// Casillas BN103x van con formato 7,2
					if (key == Mod2002021Key.BN103A || key == Mod2002021Key.BN103B || key == Mod2002021Key.BN103C || key == Mod2002021Key.BN103D)
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

    private static void addBreakdownFromConstants(Writer l, Mod2002021 m, Mod2002021Key[][] keys) throws IOException {
		
    	for (int i=0; i<keys.length; i++) {
    		if (keys[i] != null)
    		 for (int j=0; j<keys[i].length; j++) {
    		    Mod2002021Key key = keys[i][j];
				if (key != null) {
					addSignedKey(l, m, key);
				}    		    
    		 }
    	}
    	
	}
    
	private static boolean addBreakdownCorrectionKeys(Writer l, Mod2002021 m, Mod2002021KeyDC fromKey, Mod2002021KeyDC toKey) throws IOException {
		
		boolean printKey = false;
		boolean res = false; // Indica si hay alguna casilla distinta de cero
		for (Mod2002021CorrectionKey ck : Mod2002021CorrectionKey.values()) {
			
			// Desglose casilla aumento
			if (ck.getDetailIncrease() != null) {
				for (Mod2002021KeyDC key : ck.getDetailIncrease()) {
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
				for (Mod2002021KeyDC key : ck.getDetailDecrease()) {
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
		public void propertyFill(Writer line, Mod2002021 mod200, String tag) throws IOException;
	}

	private enum Pages2021 {

		  PAG00("AUX",new IPropertyFiller[] {
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(70))  // Reservado para la Administración. Rellenar con blancos
				,(line, mod200, label) -> line.append("2021")                         // Versión del programa                              
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
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone1(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone2(), 9))
				,(line, mod200, label) -> line.append(mod200.isComplementary() ? "1" : "0")
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(AonNumberUtils.todouble(mod200.getReplacedNumber()), 13, 0))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0001, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0002, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0080, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0003, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0004, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0005, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0011, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0013, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0014, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0017, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0018, 1, 0)								
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0019, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0021, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0023, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0024, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0025, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0031, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0032, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0036, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0048, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0058, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0060, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0066, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0078, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0006, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0015, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0079, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0022, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0028, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0047, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0049, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0035, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0029, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0033, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0034, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0038, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0046, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0012, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0064, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0057, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0062, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0020, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0007, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0009, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0010, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0081, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0082, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0016, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0026, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0027, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0030, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0039, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0043, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0045, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0063, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0071, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0070, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0059, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0065, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0067, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0072, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0073, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0037, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0044, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0074, 1, 0)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getBalanceType() == null ? 0 : mod200.getBalanceType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned((mod200.getEcpnType() == null || mod200.getEcpnType() == EcpnType.NO_CONSTA) ? 0 : mod200.getEcpnType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPygType() == null || mod200.getDoubleValue(Mod2002021Key.C0026) == 1 ? 0 : mod200.getPygType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0061, 1, 0)				
				,(line, mod200, label) -> line.append("0") // Modelo de estados contables que se va a cumplimentar (No se usa, es solo para estados contables entidades de credito, entidades aseguradoras, sociedades de garantía reciproca e IIC)
				,(line, mod200, label) -> {  // SOCIMIS: Régimen fiscal de entrada-salida. Renta derivada de la transmisión de inmuebles poseídos con anterioridad a la aplicación de este régimen y otras transmisiones de participaciones y activos a las que se aplica un tipo impositivo distinto del general (Art. 12.1 c, Art. 12.1 y Art 12.2) (Sólo si están marcados caracteres 57 o 64)
					if (mod200.getDoubleValue(Mod2002021Key.C0057)==1 || mod200.getDoubleValue(Mod2002021Key.C0064)==1) 
						addUnSignedKey(line, mod200, Mod2002021Key.C0012R, 1, 0);
					else line.append("0"); 
				 }
				,(line, mod200, label) -> line.append(AonStringUtils.isEmpty(mod200.getFiscalGroup()) ? AonFiscalFileUtils.spaces(7) : AonFiscalFileUtils.unsigned(mod200.getFiscalGroup(), 7, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantDocument(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(), 15))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateDocument(), 15))       				// Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: NIF o equivalente.
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text((Country.safeIso2(mod200.getUltimateDocumentCountry())=="ES"?"":Country.safeIso2(mod200.getUltimateDocumentCountry())), 2)) // Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: Código país
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateName(), 40))           				// Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: Nombre o razón social
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(Country.safeIso2(mod200.getUltimateCountry()), 2))  	// Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: País o jurisdicción
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0041, 9, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.C0042, 9, 2)
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
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG02("T20002000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para Administradores
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
		
						addUnSignedKey(line, mod200, Mod2002021Key.P1501, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002021Key.P1502, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002021Key.P1503, DS, DD, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.P1504, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.P1506, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.P1809, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.P1810, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.P1507, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.P1508, isComplementary);
		
						for (int i = 1; i <= 6; i++) {
							addCompanyParticipationIn(line, mod200, b2++);
						}
		
						addUnSignedKey(line, mod200, Mod2002021Key.POR51, 5, 2, isComplementary);
						addUnSignedKey(line, mod200, Mod2002021Key.PORES, 5, 2, isComplementary);
						
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG02B("T20002B00", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int c = 0; // Contador para Entidades menores
					int d = 0; // Contador para Información de detalle de EP o UTE
					while (!isComplementary || c < mod200.getMinorEntities().size() || d < mod200.getUteForeign().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
						
						for (int i = 1; i <= 10; i++) {
							addMinorEntity(line, mod200, c++); // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
						}
						
						for (int i = 1; i <= 19; i++) {
							addUteForeign(line, mod200, d++); // Agrup. interés económico y UTES - Información detalle de EP o UTE
						}
		
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG03("T20003000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA101)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA102)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA103)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA104)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA105)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA106)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA107)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA108)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA700)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA109)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA110)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA111)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA112)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA113)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA114)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA115)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA116)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA117)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA118)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA119)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA120)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA121)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA122)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA123)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA124)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA125)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA126)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA127)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA128)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA129)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA130)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA131)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA132)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA133)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA134)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA135)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA136)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA137)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA138)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA139)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA140)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA141)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA142)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA143)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA144)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA145)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA146)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA147)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA148)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA701)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG04("T20004000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA149)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA151)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA152)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA153)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA154)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA155)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA156)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA157)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA158)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA159)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA160)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA161)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA162)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA163)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA164)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA165)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA166)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA167)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA168)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA169)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA170)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA171)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA172)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA173)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA174)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA175)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA176)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA177)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA178)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA179)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BA180)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG05("T20005000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP185)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP186)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP187)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP188)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP189)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP190)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP191)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP192)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP193)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP702)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP1001)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP1002)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP712)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP194)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP195)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP196)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP197)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP198)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP199)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP201)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP202)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP203)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP204)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP205)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP206)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP207)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP208)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP209)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP211)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP212)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP213)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP214)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP215)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP216)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP217)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP218)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP219)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP220)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP221)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP222)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP223)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP224)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP225)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP226)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP227)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
	
		, PAG06("T20006000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP228)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP229)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP703)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP704)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP231)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP232)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP233)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP234)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP235)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP236)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP237)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP238)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP239)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP240)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP241)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP242)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP243)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP244)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP245)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP246)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP247)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP248)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP249)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP250)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP251)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BP252)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG07("T20007000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG255)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG256)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG257)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG711)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG705)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG706)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG707)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG708)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG258)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG259)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG260)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG261)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG760)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG761)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG262)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG762)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG763)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG263)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG264)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG265)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG266)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG267)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG268)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG269)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG273)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG274)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG275)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG276)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG277)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG278)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG279)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG253)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG254)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG281)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG282)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG283)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG709)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG284)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG287)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG288)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG289)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG290)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG291)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG292)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG293)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG710)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG294)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG295)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG296)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG08("T20008000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG297)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG298)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG299)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG300)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG304)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG306)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG310)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG311)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG312)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG313)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG315)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG316)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG317)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG318)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG319)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG320)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG321)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG322)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG323)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG329)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG324)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG325)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG326)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG327)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG328)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.PG500)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)			
			})

		, PAG09("T20009000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0336)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0337)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0338)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0339)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0340)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0341)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0342)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0343)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0345)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0346)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0347)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0348)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0349)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0350)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0351)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0352)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0353)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0354)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.T0355)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG10("T20010000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC380)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC381)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC382)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC383)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC384)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC385)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC386)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC394)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC395)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC396)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC397)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC398)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC400)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC409)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC410)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC411)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC412)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC413)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC422)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC423)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC424)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC425)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC426)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC427)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC428)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC436)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC437)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC438)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC439)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC440)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC441)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC442)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC450)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC451)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC452)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC453)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC454)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC455)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC456)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC464)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC465)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC466)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC467)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC468)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC469)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC470)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC478)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC479)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC481)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC482)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC483)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC484)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC492)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC493)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC494)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC495)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC496)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC497)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC498)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC507)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC508)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC509)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC511)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC512)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC523)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC524)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC525)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC526)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC534)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC535)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC536)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC537)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC538)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC539)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC540)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC548)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC549)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC551)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC564)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC580)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC591)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC592)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC594)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC595)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC596)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC607)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC608)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC609)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC610)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC620)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC623)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC624)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC715)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC716)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC717)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC718)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC719)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC720)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC721)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC729)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC730)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC731)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC732)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC733)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC734)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC735)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC632)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC634)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC635)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC636)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC637)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC638)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG11("T20011000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC387)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC388)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC389)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC390)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC391)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC392)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC401)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC402)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC403)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC404)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC405)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC406)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC407)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC415)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC416)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC417)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC418)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC419)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC420)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC421)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC429)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC430)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC431)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC432)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC433)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC434)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC435)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC443)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC444)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC445)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC446)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC448)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC449)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC457)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC458)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC461)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC462)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC463)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC471)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC472)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC475)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC476)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC477)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC485)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC486)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC489)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC490)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC491)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC499)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC502)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC503)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC504)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC505)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC513)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC514)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC515)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC516)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC517)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC518)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC519)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC527)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC528)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC529)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC530)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC531)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC532)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC533)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC541)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC542)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC543)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC544)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC546)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC557)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC558)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC574)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC586)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC589)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC598)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC613)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC614)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC625)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC626)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC627)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC628)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC629)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC630)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC631)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC722)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC723)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC724)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC725)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC726)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC727)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC728)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC736)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC737)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC738)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC739)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC740)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC741)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC742)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC639)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC640)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC641)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC643)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC644)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TC645)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG12("T20012000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ501)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1231)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.VOLOPE, 1, 0) // Volumen Operaciones
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021CorrectionKey.values(), Mod2002021Key.I0355, Mod2002021Key.D2185)  // Correcciones al resultado contable
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG13("T20013000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
						        
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021CorrectionKey.values(), Mod2002021Key.I2186, Mod2002021Key.D0414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.I0417)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.D0418)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1029)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1030)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1031)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ550TG) // Parte de la base imponible del período impositivo que tributa al tipo general (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ550T0) // Parte de la base imponible del período impositivo que tributa al tipo del 0% (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1032)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1033)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1034)
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(153)) // Reservado para la AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(47)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14("T20014000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ559)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1509)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1577)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.LQ558, 4, 2)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1037)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1038)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN573)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1039)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN2314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN2315)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN082)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1040)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1041)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN592)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1039M)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN2314M)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(166)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14B("T20014B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1785)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1786)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1787)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1788)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1789)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1790)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1791)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1792)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1793)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1794)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1795)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1796)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1797)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1798)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1799)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1766)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1784)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN601)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1234A)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN083)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1042)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1333)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1586) // Liquidación IV - Resultado de la autoliquidación - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1587) // Liquidación IV - Resultado de la autoliquidación - D. Forales/Navarra 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1578) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Resultados a ingresar procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2021 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1583) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Resultados a ingresar procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2021 - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1584) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Devoluciones acordadas procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2021 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1585) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Devoluciones acordadas procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2021 - D. Forales/Navarra (Totales)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ1588) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Importe integrado en la base imponible - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2480) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Importe integrado en la base imponible - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2481) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Deuda tributaria resultante del fraccionamiento art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2482) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Deuda tributaria resultante del fraccionamiento art. 19.1 LIS - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2483) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - 1er fraccionamiento - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2484) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - 1er fraccionamiento - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2485) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2486) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2487) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Complementaria: Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS procedente de autoliquidaciones anteriores correspondientes al período impositivo 2021 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2488) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Complementaria: Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS procedente de autoliquidaciones anteriores correspondientes al período impositivo 2022 - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2489) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3242) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1020)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1043)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LM506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1021)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN1044)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3243) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Complementaria: Devolución acordada/compensada - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3244) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Complementaria: Devolución acordada/compensada - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3245) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Complementaria: Devolución acordada/compensada - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3317) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Abono - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3318) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Abono - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3319) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Abono - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ3320) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Compensación - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2490) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Compensación - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2491) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Compensación - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2492) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: A ingresar - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2493) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: A ingresar - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2494) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: A ingresar - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces( 34)) // Reservado para la AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(166)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG15("T20015000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LQ547Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002021BN570Key.values())  
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002021BN1344Key.values()) 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN1280Key.values()) 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
			})

		, PAG16("T20016000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002021BN572Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002021BN571Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN573Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN585Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG16B("T20016B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN584Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN590Key.values(), null, Mod2002021Key.BN887)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG17("T20017000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int c = 0; // Contador para Producciones Cinematográficas					
					while (!isComplementary || c < mod200.getFilmProductions().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
						addBreakdown(line, mod200, Mod2002021BN590Key.values(), Mod2002021Key.BN2287, null, isComplementary);
						addBreakdown(line, mod200, Mod2002021BN588Key.values(), null, Mod2002021Key.BN2192, isComplementary);
						
						for (int i = 1; i <= 6; i++) {
							addNIF(line, mod200.getFilmProductions(), c++);
						}
						
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})

		, PAG18("T20018000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN588Key.values(), Mod2002021Key.BN1626, null)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
		})

		, PAG18B("T20018B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN565_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN565_2Key.values())				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN565Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN974)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG19("T20019000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN1040Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN1041Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021BN082Key.values())
			
				// Detalle correcciones resultado pérdidas y ganancias (totales)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2305)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2306)					
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2301)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2302)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2303)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2304)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2307)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.I0417)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.D0418)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2309)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2310)	
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20("T20020000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002021Constants.DEDUCIBLE_LIMITATION_KEYS_1)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LM1212Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LM538Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20B("T20020B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LQ1032Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LQ1033_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LQ1033_2Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

		, PAG20C("T20020C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LM1535Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LM1561Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LM393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LM506)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LM1579Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})
		
		, PAG20D("T20020D00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")			
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID650)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID651)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID652)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID653)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID654)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID1270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID1271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID1522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID655)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID656)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID658)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID659)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID660)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID662)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID664)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID665)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.ID666)				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LM1494Key.values())
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
		
						addSignedKey(line, mod200, Mod2002021Key.CN987, isComplementary);
		
						for (int i = 1; i <= 12; i++) {
							addGroupNIF(line, mod200, i1++);
						}
		
						addSignedKey(line, mod200, Mod2002021Key.CN988, isComplementary);
						addUnSignedKey(line, mod200, Mod2002021Key.CNEST, 3, 0, isComplementary);
		
						for (int i = 1; i <= 5; i++) {
							addNIF(line, mod200.getEstablishments(), i2++);
						}
		
						addSignedKey(line, mod200, Mod2002021Key.CN989, isComplementary);
						
						line.append(!isComplementary && mod200.getDoubleValue(Mod2002021Key.VOLOPE) == 1.0?"1":"0"); // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - inferior a 20 millones de euros
						line.append(!isComplementary && mod200.getDoubleValue(Mod2002021Key.VOLOPE) == 2.0?"1":"0"); // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 20 millones de euros pero inferior a 60 millones de euros
						line.append(!isComplementary && mod200.getDoubleValue(Mod2002021Key.VOLOPE) == 3.0?"1":"0"); // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 60 millones de euros
		
						addUnSignedKey(line, mod200, Mod2002021Key.LQ0N1, 4, 0, isComplementary); 
						addSignedKey(line, mod200, Mod2002021Key.LQ630, isComplementary); 
						addSignedKey(line, mod200, Mod2002021Key.LQ631, isComplementary); 
						addSignedKey(line, mod200, Mod2002021Key.LQ632, isComplementary); 
						addSignedKey(line, mod200, Mod2002021Key.LQ579, isComplementary); 
						
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoIII()), 22));
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoIV()), 22));
						line.append(AonFiscalFileUtils.text((isComplementary?"":mod200.getNrsAnexoVric()), 22));
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
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021RIC_1Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.RC927)				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021RIC_2Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LQ554Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002021LQ561Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

		// [...] NO ESTA EN EL MODELO - Página 23: Operaciones fusión, escisión, canje de valores.

		, PAG24("T20024000", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int i1 = 0; // Contador para Deducción para Evitar la doble imposición
					int i2 = 0; // Contador para Relación de Socios
					while (!isComplementary || i1 < mod200.getUteBases().size() || i2 < mod200.getUteParticipations().size() ) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
		
						addUnSignedKey(line, mod200, Mod2002021Key.UT060, 7, 4, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UT500, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UT1227, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UT1228, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UT552, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UT1330, isComplementary);
		
						for (int i = 1; i <= 4; i++) {
							addUteBase(line, mod200, i1++);
						}
		
						addSignedKey(line, mod200, Mod2002021Key.UTC01, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UTC02, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UTC03, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UT062, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UTC04, isComplementary);
						addSignedKey(line, mod200, Mod2002021Key.UTC05, isComplementary);
		
						for (int i = 1; i <= 10; i++) {
							addUteParticipation(line, mod200, i2++);
						}
								
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
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TR050)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TR051)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TR052)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TR053)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TR054)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TR055)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.TR056)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.TR626, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.TR627, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.TR628, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.TR629, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002021Key.TR625, 5, 2)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002021Constants.COMBINED_TAXATION_3)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002021Constants.COMBINED_TAXATION_4)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002021Constants.COMBINED_TAXATION_5)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26B("T20026B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria				
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002021KeyDC.DC2504, Mod2002021KeyDC.DC2680)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26C("T20026C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002021KeyDC.DC2684, Mod2002021KeyDC.DC2880)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26D("T20026D00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002021KeyDC.DC3241, Mod2002021KeyDC.DC3030)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26E("T20026E00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002021KeyDC.DC3034, Mod2002021KeyDC.DC3220)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26F("T20026F00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002021KeyDC.DC3224, Mod2002021KeyDC.DC3400)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2306)					
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2302)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2304)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.DC2310)	
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
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDocument(), 9)) // Identificación -  NIF				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getName(), 80)) // Identificación - Apellidos y nombre o Razón Social
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ552) // Liquidación - Base imponible [552]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ562) // Liquidación - Cuota íntegra [562]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.BN621) // Liquidación - Líquido a ingresar o a devolver Estado [621]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002021Key.LQ2489) // Liquidación - Opción de fraccionamiento art. 19.1 LIS - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS: Estado 
				
				,(line, mod200, label) -> {
					
					double importe = mod200.getDoubleValue(Mod2002021Key.BN621); // importe a ingresar o a devolver

					line.append(AonFiscalFileUtils.text(importe < 0 ? ("V".equals(mod200.getDevType()) ? "" : mod200.getDevType()) : "", 1)); // Devolución - Renuncia o por Transferencia ("blanco","R","D")
					line.append(AonFiscalFileUtils.signedZero(importe < 0 ? Math.abs(importe) : 0.0, DS, DD)); 						          // Devolución - Importe a devolver
					line.append(importe < 0 && "D".equals(mod200.getDevType()) ? "1" : "0");                                                  // Devolución - Marca SEPA (0 Vacía, 1 Cuenta España, 2 Unión Europea SEPA, 3 Resto Países) (Se asume cuenta de España)					
                    line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "", 34));         // Devolución - Número de cuenta IBAN (si devolución por transferencia)
					line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getBic() : "", 11));          // Devolución - Código SWIFT-BIC
					line.append(AonFiscalFileUtils.text(importe > 0 ? mod200.getPayType() : "", 1));    // Ingreso - Modalidad de ingreso. Uno de los siguientes valores "blanco", "I" Adeudo en cuenta, "U" Domiciliación
					if (mod200.getDoubleValue(Mod2002021Key.LQ2489) > 0) 
					   line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(Mod2002021Key.LQ2489), DS, DD));    // Ingreso - Importe a ingresar
					else
					   line.append(AonFiscalFileUtils.signedZero(importe > 0 ? importe : 0.0, DS, DD));    // Ingreso - Importe a ingresar
					line.append(AonFiscalFileUtils.text(importe > 0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType()))	? mod200.getIban() : "", 34));  // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)

					addSignedKey(line, mod200, Mod2002021Key.BN1020); // Abono/Compensación - Abono por conversión de activos impuesto diferido - A
					addSignedKey(line, mod200, Mod2002021Key.BN1021); // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C
					addSignedKey(line, mod200, Mod2002021Key.LQ3318); // Abono/Compensación - Resultado de conversión de AID tras regularización: Abono 
					addSignedKey(line, mod200, Mod2002021Key.LQ2490); // Abono/Compensación - Resultado de conversión de AID tras regularización: Compensación 
					addSignedKey(line, mod200, Mod2002021Key.LQ2493); // Abono/Compensación - Resultado de conversión de AID tras regularización: A ingresar 

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

		private Pages2021(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002021 mod200, Writer line) throws IOException {

			// Controles determinadas páginas que solo se ponen si están marcados ciertos
			// caracteres
			boolean addPage = true;

			// Página 9 a 11. ECPN. Si están marcadas las casillas 75, 76 o 77
			if (this == Pages2021.PAG09 || this == Pages2021.PAG10 || this == Pages2021.PAG11) {
				addPage = (mod200.getEcpnType() == EcpnType.NORMAL) || (mod200.getEcpnType() == EcpnType.ABREVIADO) || (mod200.getEcpnType() == EcpnType.PYMES);
			}

			// Página 22. Regimen especial de la reserva para inversiones en Canarias y
			// Cooperativas
			if (this == Pages2021.PAG22) {
				addPage = (mod200.getDoubleValue(Mod2002021Key.C0029) == 1)
						|| (mod200.getDoubleValue(Mod2002021Key.C0017) == 1)
						|| (mod200.getDoubleValue(Mod2002021Key.C0018) == 1)
						|| (mod200.getDoubleValue(Mod2002021Key.C0019) == 1);
			}

			// Página 24. Agrupaciones de interes económico y UTES (regimen especial).
			// Caracteres 013 o 014 marcados
			if (this == Pages2021.PAG24) {
				addPage = (mod200.isChecked(Mod2002021Key.C0013) || mod200.isChecked(Mod2002021Key.C0014));
			}

			// Página 26. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2021.PAG26) {
				addPage = (mod200.getDoubleValue(Mod2002021Key.C0028) == 1);
			}
			
			// Páginas 26B: Solo si hay algún importe en la pagina
			if (this == Pages2021.PAG26B) {				
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002021KeyDC.DC2504, Mod2002021KeyDC.DC2680);
			}
			
			// Páginas 26C: Solo si hay algún importe en la pagina			
			if (this == Pages2021.PAG26C) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002021KeyDC.DC2684, Mod2002021KeyDC.DC2880);
			}
			
			// Páginas 26D: Solo si hay algún importe en la pagina
			if (this == Pages2021.PAG26D) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002021KeyDC.DC3241, Mod2002021KeyDC.DC3030);
			}

			// Páginas 26E: Solo si hay algún importe en la pagina
			if (this == Pages2021.PAG26E) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002021KeyDC.DC3034, Mod2002021KeyDC.DC3220);
			}
			
			// Páginas 26F: Solo si hay algún importe en la pagina
			if (this == Pages2021.PAG26F) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002021KeyDC.DC3224, Mod2002021KeyDC.DC3400) ||
						  mod200.getDoubleValue(Mod2002021Key.DC2305) != 0 ||
						  mod200.getDoubleValue(Mod2002021Key.DC2301) != 0 ||
						  mod200.getDoubleValue(Mod2002021Key.DC2303) != 0 ||
						  mod200.getDoubleValue(Mod2002021Key.DC2307) != 0 ||
						  mod200.getDoubleValue(Mod2002021Key.DC2309) != 0 ||
						  mod200.getDoubleValue(Mod2002021Key.DC2306) != 0 ||					
						  mod200.getDoubleValue(Mod2002021Key.DC2302) != 0 ||	
						  mod200.getDoubleValue(Mod2002021Key.DC2304) != 0 ||	
						  mod200.getDoubleValue(Mod2002021Key.DC2308) != 0 ||
						  mod200.getDoubleValue(Mod2002021Key.DC2310) != 0;
			}
			
			// Añadir el contenido de la página
			if (addPage) {
				for (IPropertyFiller propertyFiller : this.propertyFillers) {
					propertyFiller.propertyFill(line, mod200, this.tag);
				}
			}
		}
	}

	public static void fillWriter(Mod2002021 mod200, Writer line) throws IOException {

		line.append("<T2000" + mod200.getYear() + "0A0000>"); // Etiqueta inicio de fichero
		for (Pages2021 page : Pages2021.values()) {
			page.fillPage(mod200, line);
		}
		line.append("</T2000" + mod200.getYear() + "0A0000>"); // Etiqueta fin de fichero
		line.close();

	}

}
