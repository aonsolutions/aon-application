package com.esferalia.aon.occam.mod200.server.format.mod200_2022;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN082Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1039Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1040Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1041Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1280Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1344Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN2314Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN2315Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN565Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN565_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN565_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN570Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN571Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN572Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN573Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN584Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN585Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN588Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN590Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022CorrectionKey;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1212Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1494Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1535Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1579Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM538Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1032Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1033_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1033_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ243Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ547Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ554Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022RIC_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022RIC_2Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002022Writer {
	
	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************

	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static int DS = 17; // Tamaño de digitos por defecto para los importes
	private static int DD = 2; // Decimales por defecto para los importes
	
	// Añade un importe de una casilla del modelo (con signo, longitud y decimales
	// por defecto, relleno con ceros por la izquierda)
	private static void addSignedKey(Writer line, Mod2002022 mod200, IMod200Key iMod200Key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(iMod200Key), DS, DD));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002022 mod200, Mod2002022Key key, boolean isComplementary)
			throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key), DS, DD));
		}
	}

	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por
	// la izquierda)
	private static void addUnSignedKey(Writer line, Mod2002022 mod200, IMod200Key key, int size, int dec)
			throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key), size, dec));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002022 mod200, Mod2002022Key key, int size, int dec,
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
	private static void addLegalRepresentative(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addCompanyAdministrator(Writer line, Mod2002022 mod200, int index) throws IOException {
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
			if (mod200.getDoubleValue(Mod2002022Key.C0021) == 1)
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
	private static void addCompanyParticipationOut(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addCompanyParticipationIn(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addMinorEntity(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addUteBase(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addUteParticipation(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addUteForeign(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addGroupNIF(Writer line, Mod2002022 mod200, int index) throws IOException {
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
	private static void addBreakdown(Writer l, Mod2002022 m, IMod200KeysProvider[] keysProvider) throws IOException {
		addBreakdown(l, m, keysProvider, null, null);
	}
	
	private static void addBreakdown(Writer l, Mod2002022 m, IMod200KeysProvider[] keysProvider, Mod2002022Key fromKey, Mod2002022Key toKey) throws IOException {
		addBreakdown(l, m, keysProvider, fromKey, toKey, false);
	}
	
	private static void addBreakdown(Writer l, Mod2002022 m, IMod200KeysProvider[] keysProvider, Mod2002022Key fromKey, Mod2002022Key toKey, boolean isComplementary) throws IOException {
		
		boolean printKey = false;
		for (IMod200KeysProvider kp : keysProvider) {
			for (int i=0;i<kp.getKeys().length;i++) {
				Mod2002022Key key = (Mod2002022Key) kp.getKeys()[i];
				if (key != null) {
					//if (fromKey == null || (fromKey != null && key == fromKey))
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
    private static void addBreakdownDoubleImposition(Writer l, Mod2002022 m, IMod200KeysProvider[] keysProvider) throws IOException {
		
		for (IMod200KeysProvider kp : keysProvider) {
			int pos = 1;
			for (IMod200Key key : kp.getKeys()) {
				if (key != null) {
					// Casillas BN103x van con formato 7,2
					if (key == Mod2002022Key.BN103A || key == Mod2002022Key.BN103B || key == Mod2002022Key.BN103C || key == Mod2002022Key.BN103D)
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

    private static void addBreakdownFromConstants(Writer l, Mod2002022 m, Mod2002022Key[][] keys) throws IOException {
		
    	for (int i=0; i<keys.length; i++) {
    		if (keys[i] != null)
    		 for (int j=0; j<keys[i].length; j++) {
    		    Mod2002022Key key = keys[i][j];
				if (key != null) {
					addSignedKey(l, m, key);
				}    		    
    		 }
    	}
    	
	}
    
	private static boolean addBreakdownCorrectionKeys(Writer l, Mod2002022 m, Mod2002022KeyDC fromKey, Mod2002022KeyDC toKey) throws IOException {
		
		boolean printKey = false;
		boolean res = false; // Indica si hay alguna casilla distinta de cero
		for (Mod2002022CorrectionKey ck : Mod2002022CorrectionKey.values()) {
			
			// Desglose casilla aumento
			if (ck.getDetailIncrease() != null) {
				for (Mod2002022KeyDC key : ck.getDetailIncrease()) {
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
				for (Mod2002022KeyDC key : ck.getDetailDecrease()) {
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
		public void propertyFill(Writer line, Mod2002022 mod200, String tag) throws IOException;
	}

	private enum Pages2022 {

		  PAG00("AUX",new IPropertyFiller[] {
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(70))  // Reservado para la Administración. Rellenar con blancos
				,(line, mod200, label) -> line.append("2022")                         // Versión del programa                              
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
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.X0001, 1, 0) // Realiza actividades agrícolas y/o ganaderas
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone1(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getEnterprisePhone2(), 9))
				,(line, mod200, label) -> line.append(mod200.isComplementary() ? "1" : "0")
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(AonNumberUtils.todouble(mod200.getReplacedNumber()), 13, 0))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0001, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0002, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0080, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0003, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0008, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0004, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0005, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0011, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0013, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0014, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0017, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0018, 1, 0)								
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0019, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0021, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0023, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0024, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0025, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0031, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0032, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0036, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0048, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0058, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0060, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0066, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0078, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0056, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0083, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0006, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0015, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0079, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0022, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0028, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0047, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0049, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0035, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0029, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0069, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0033, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0034, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0038, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0046, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0012, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0064, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0057, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0062, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0020, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0007, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0009, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0010, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0081, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0082, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0016, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0026, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0027, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0030, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0039, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0043, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0045, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0063, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0071, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0070, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0059, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0065, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0084, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0072, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0073, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0037, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0044, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0074, 1, 0)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getBalanceType() == null ? 0 : mod200.getBalanceType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned((mod200.getEcpnType() == null || mod200.getEcpnType() == EcpnType.NO_CONSTA) ? 0 : mod200.getEcpnType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPygType() == null || mod200.getDoubleValue(Mod2002022Key.C0026) == 1 ? 0 : mod200.getPygType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0061, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0068, 1, 0)				
				,(line, mod200, label) -> line.append("0") // Modelo de estados contables que se va a cumplimentar (No se usa, es solo para estados contables entidades de credito, entidades aseguradoras, sociedades de garantía reciproca e IIC) (No se usa en AON)
				,(line, mod200, label) -> {  // SOCIMIS: Régimen fiscal de entrada-salida. Renta derivada de la transmisión de inmuebles poseídos con anterioridad a la aplicación de este régimen y otras transmisiones de participaciones y activos a las que se aplica un tipo impositivo distinto del general (Art. 12.1 c, Art. 12.1 y Art 12.2) (Sólo si están marcados caracteres 57 o 64)
					if (mod200.getDoubleValue(Mod2002022Key.C0057)==1 || mod200.getDoubleValue(Mod2002022Key.C0064)==1) 
						addUnSignedKey(line, mod200, Mod2002022Key.C0012R, 1, 0);
					else line.append("0"); 
				 }
				,(line, mod200, label) -> line.append(mod200.getDoubleValue(Mod2002022Key.VOLOPE) == 1.0?"1":"0")  // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - inferior a 20 millones de euros
				,(line, mod200, label) -> line.append(mod200.getDoubleValue(Mod2002022Key.VOLOPE) == 2.0?"1":"0")  // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 20 millones de euros pero inferior a 60 millones de euros
				,(line, mod200, label) -> line.append(mod200.getDoubleValue(Mod2002022Key.VOLOPE) == 3.0?"1":"0")  // Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo - de al menos 60 millones de euros				
				,(line, mod200, label) -> line.append(AonStringUtils.isEmpty(mod200.getFiscalGroup()) ? AonFiscalFileUtils.spaces(7) : AonFiscalFileUtils.number(mod200.getFiscalGroup(), 7))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantDocument(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(), 15))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateDocument(), 15))       				// Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: NIF o equivalente.
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text((Country.safeIso2(mod200.getUltimateDocumentCountry())=="ES"?"":Country.safeIso2(mod200.getUltimateDocumentCountry())), 2)) // Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: Código país
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getUltimateName(), 40))           				// Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: Nombre o razón social
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(Country.safeIso2(mod200.getUltimateCountry()), 2))  	// Grupo mercantil - Clave 00081 - Datos de la sociedad matriz última: País o jurisdicción
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0041, 9, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.C0042, 9, 2)				
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
		
						addUnSignedKey(line, mod200, Mod2002022Key.P1501, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002022Key.P1502, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002022Key.P1503, DS, DD, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.P1504, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.P1506, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.P1809, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.P1810, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.P1507, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.P1508, isComplementary);
		
						for (int i = 1; i <= 6; i++) {
							addCompanyParticipationIn(line, mod200, b2++);
						}
		
						addUnSignedKey(line, mod200, Mod2002022Key.POR51, 5, 2, isComplementary);
						addUnSignedKey(line, mod200, Mod2002022Key.PORES, 5, 2, isComplementary);
						
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG02B("T20002B00", new IPropertyFiller[] { 
				(line, mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int c = 0;  // Contador para Entidades menores
					int d = 0;  // Contador para Información de detalle de EP o UTE
					int s1 = 0; // E. Socios de SICAV en régimen especial de disolución y liquidación (DT 41ª LIS) - NIF de la sociedad/es disuelta/s
					int s2 = 0; // E. Socios de SICAV en régimen especial de disolución y liquidación (DT 41ª LIS) - NIF de la/las IIC donde reinvierte
					while (!isComplementary || c < mod200.getMinorEntities().size() || d < mod200.getUteForeign().size() || s1 < mod200.getSicav1().size() || s2 < mod200.getSicav2().size()) {
						addStartLabel(line, label);
						line.append(isComplementary ? "C" : " ");
						
						for (int i = 1; i <= 10; i++) {
							addMinorEntity(line, mod200, c++); // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
						}
						
						for (int i = 1; i <= 18; i++) {
							addUteForeign(line, mod200, d++); // Agrup. interés económico y UTES - Información detalle de EP o UTE
						}
						
						for (int i = 1; i <= 5; i++) {
							addNIF(line, mod200.getSicav1(), s1++);
						}
						
						for (int i = 1; i <= 5; i++) {
							addNIF(line, mod200.getSicav2(), s2++);
						}						
						
						line.append(AonFiscalFileUtils.text(isComplementary ? "" : mod200.getSecretary().getName(), 21));
						line.append(AonFiscalFileUtils.text(isComplementary ? "" : mod200.getSecretary().getDocument(), 9));
						line.append(AonFiscalFileUtils.dateZero(isComplementary ? null : mod200.getSecretary().getIrnr()));
						addLegalRepresentative(line, mod200, isComplementary ? 5 : 0);  // No hay mas de 3 representantes legales, por lo tanto si es hoja complementaria se le pasa 5 para que se rellene con espacios o ceros
						addLegalRepresentative(line, mod200, isComplementary ? 5 : 1);
						addLegalRepresentative(line, mod200, isComplementary ? 5 : 2);
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG03("T20003000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA101)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA102)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA103)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA104)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA105)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA106)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA107)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA108)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA700)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA109)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA110)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA111)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA112)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA113)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA114)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA115)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA116)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA117)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA118)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA119)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA120)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA121)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA122)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA123)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA124)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA125)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA126)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA127)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA128)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA129)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA130)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA131)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA132)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA133)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA134)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA135)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA136)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA137)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA138)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA139)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA140)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA141)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA142)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA143)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA144)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA145)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA146)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA147)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA148)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA701)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG04("T20004000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA149)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA151)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA152)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA153)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA154)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA155)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA156)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA157)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA158)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA159)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA160)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA161)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA162)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA163)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA164)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA165)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA166)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA167)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA168)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA169)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA170)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA171)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA172)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA173)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA174)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA175)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA176)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA177)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA178)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA179)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BA180)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG05("T20005000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP185)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP186)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP187)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP188)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP189)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP190)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP191)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP192)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP193)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP702)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP1001)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP1002)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP712)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP194)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP195)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP196)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP197)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP198)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP199)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP201)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP202)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP203)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP204)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP205)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP206)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP207)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP208)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP209)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP211)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP212)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP213)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP214)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP215)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP216)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP217)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP218)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP219)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP220)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP221)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP222)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP223)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP224)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP225)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP226)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP227)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
	
		, PAG06("T20006000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP228)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP229)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP703)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP704)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP231)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP232)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP233)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP234)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP235)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP236)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP237)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP238)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP239)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP240)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP241)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP242)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP243)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP244)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP245)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP246)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP247)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP248)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP249)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP250)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP251)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BP252)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG07("T20007000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG255)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG256)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG257)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG711)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG705)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG706)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG707)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG708)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG258)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG259)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG260)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG261)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG760)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG761)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG262)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG762)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG763)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG263)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG264)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG265)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG266)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG267)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG268)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG269)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG273)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG274)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG275)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG276)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG277)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG278)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG279)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG253)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG254)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG281)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG282)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG283)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG709)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG284)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG287)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG288)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG289)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG290)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG291)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG292)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG293)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG710)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG294)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG295)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG296)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG08("T20008000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG297)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG298)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG299)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG300)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG304)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG306)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG310)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG311)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG312)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG313)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG315)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG316)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG317)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG318)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG319)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG320)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG321)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG322)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG323)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG329)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG324)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG325)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG326)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG327)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG328)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.PG500)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)			
			})

		, PAG09("T20009000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0336)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0337)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0338)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0339)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0340)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0341)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0342)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0343)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0345)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0346)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0347)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0348)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0349)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0350)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0351)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0352)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0353)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0354)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.T0355)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG10("T20010000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC380)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC381)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC382)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC383)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC384)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC385)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC386)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC394)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC395)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC396)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC397)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC398)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC400)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC409)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC410)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC411)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC412)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC413)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC422)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC423)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC424)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC425)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC426)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC427)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC428)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC436)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC437)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC438)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC439)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC440)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC441)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC442)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC450)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC451)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC452)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC453)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC454)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC455)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC456)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC464)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC465)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC466)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC467)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC468)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC469)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC470)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC478)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC479)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC481)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC482)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC483)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC484)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC492)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC493)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC494)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC495)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC496)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC497)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC498)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC507)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC508)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC509)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC511)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC512)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC523)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC524)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC525)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC526)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC534)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC535)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC536)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC537)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC538)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC539)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC540)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC548)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC549)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC551)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC564)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC580)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC591)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC592)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC594)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC595)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC596)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC607)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC608)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC609)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC610)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC620)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC623)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC624)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC715)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC716)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC717)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC718)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC719)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC720)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC721)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC729)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC730)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC731)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC732)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC733)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC734)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC735)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC632)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC634)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC635)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC636)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC637)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC638)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG11("T20011000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC387)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC388)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC389)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC390)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC391)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC392)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC401)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC402)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC403)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC404)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC405)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC406)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC407)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC415)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC416)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC417)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC418)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC419)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC420)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC421)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC429)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC430)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC431)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC432)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC433)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC434)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC435)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC443)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC444)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC445)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC446)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC448)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC449)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC457)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC458)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC461)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC462)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC463)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC471)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC472)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC475)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC476)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC477)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC485)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC486)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC489)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC490)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC491)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC499)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC502)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC503)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC504)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC505)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC513)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC514)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC515)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC516)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC517)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC518)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC519)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC527)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC528)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC529)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC530)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC531)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC532)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC533)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC541)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC542)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC543)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC544)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC546)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC557)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC558)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC574)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC586)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC589)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC598)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC613)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC614)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC625)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC626)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC627)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC628)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC629)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC630)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC631)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC722)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC723)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC724)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC725)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC726)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC727)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC728)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC736)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC737)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC738)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC739)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC740)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC741)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC742)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC639)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC640)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC641)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC643)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC644)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TC645)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG12("T20012000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ501)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1231)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022CorrectionKey.values(), Mod2002022Key.I0355, Mod2002022Key.D2185)  // Correcciones al resultado contable
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG13("T20013000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
						        
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022CorrectionKey.values(), Mod2002022Key.I2186, Mod2002022Key.D0414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.I0417)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.D0418)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1029)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1030)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1031)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ550TG) // Parte de la base imponible del período impositivo que tributa al tipo general (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ550T0) // Parte de la base imponible del período impositivo que tributa al tipo del 0% (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1032)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ541)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ564)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1887)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1890)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1033)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1034)
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(153)) // Reservado para la AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(47)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14("T20014000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ559)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1509)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1577)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.LQ558, 4, 2)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1037)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1038)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN573)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1039)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN2314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN2315)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN082)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1040)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1041)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN592)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(17)) // Reservado para la AEAT (antes Mod2002022Key.BN1039M) 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(17)) // Reservado para la AEAT (antes Mod2002022Key.BN2314M)
				,(line, mod200, label) -> line.append(" ") // Inoperatividad del orden de cumplimentación de las deducciones del Tramo 2 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(165)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14B("T20014B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1785)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1786)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1787)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1788)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1789)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1790)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1791)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1792)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1793)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1794)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1795)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1796)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1797)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1798)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1799)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1766)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1784)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN601)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1234A)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN083)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1892)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1042)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1333)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1319) // Liquidación IV - Resultado de la autoliquidación - Abono de deducciones por producciones cinematográficas extranjeras en Canarias (art. 39.3 LIS y DA 14ª Ley 19/1994) - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1893) // Liquidación IV - Resultado de la autoliquidación - Abono de deducciones por producciones cinematográficas extranjeras en Canarias (art. 39.3 LIS y DA 14ª Ley 19/1994) - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1881) // Liquidación IV - Resultado de la autoliquidación - Abono de deducciones por producciones cinematográficas extranjeras en Canarias (art. 39.3 LIS y DA 14ª Ley 19/1994) - D. Forales/Navarra 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1586) // Liquidación IV - Resultado de la autoliquidación - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1587) // Liquidación IV - Resultado de la autoliquidación - D. Forales/Navarra 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1578) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Resultados a ingresar procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2022 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1583) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Resultados a ingresar procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2022 - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1584) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Devoluciones acordadas procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2022 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1585) // Liquidación IV - Líquido a ingresar o a devolver - Complementaria: Devoluciones acordadas procedentes de autoliquidaciones anteriores correspondientes al período impositivo 2022 - D. Forales/Navarra (Totales)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ1588) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Importe integrado en la base imponible - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2480) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Importe integrado en la base imponible - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2481) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Deuda tributaria resultante del fraccionamiento art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2482) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Deuda tributaria resultante del fraccionamiento art. 19.1 LIS - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2483) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - 1er fraccionamiento - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2484) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - 1er fraccionamiento - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2485) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2486) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2487) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Complementaria: Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS procedente de autoliquidaciones anteriores correspondientes al período impositivo 2022 - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2488) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Complementaria: Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS procedente de autoliquidaciones anteriores correspondientes al período impositivo 2022 - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2489) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3242) // Liquidación IV - Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS) - Líquido a ingresar - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1020)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1043)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LM506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1021)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN1044)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3243) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Complementaria: Devolución acordada/compensada - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3244) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Complementaria: Devolución acordada/compensada - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3245) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Complementaria: Devolución acordada/compensada - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3317) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Abono - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3318) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Abono - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3319) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Abono - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ3320) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Compensación - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2490) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Compensación - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2491) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: Compensación - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2492) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: A ingresar - Total 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2493) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: A ingresar - Estado 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2494) // Liquidación IV - Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS) - Líquido a ingresar o a devolver - Resultado de conversión de AID tras regularización: A ingresar - D. Forales/Navarra (Totales) 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces( 34)) // Reservado para la AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(166)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG15("T20015000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LQ547Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LQ243Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
			})

		, PAG15B("T20015B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002022BN570Key.values())  
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002022BN1344Key.values()) 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN1280Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002022BN572Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG16("T20016000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002022BN571Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN573Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN585Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN584Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG16B("T20016B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN590Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG17("T20017000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN588Key.values(), Mod2002022Key.BN288, Mod2002022Key.BN2192)
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
						
						for (int i = 1; i <= 6; i++) {
							addNIF(line, mod200.getFilmProductions(), c++);
						}
						
						addBreakdown(line, mod200, Mod2002022BN588Key.values(), Mod2002022Key.BN1626, Mod2002022Key.BN1195, isComplementary);
						
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})

		, PAG18B("T20018B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN588Key.values(), Mod2002022Key.BN1197, null)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN2315Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN1039Key.values())				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN2314Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
		})

		, PAG18C("T20018C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN565_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN565_2Key.values())				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN565Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN974)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG19("T20019000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN1040Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN1041Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022BN082Key.values())
			
				// Detalle correcciones resultado pérdidas y ganancias (totales)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2305)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2306)					
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2301)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2302)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2303)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2304)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2307)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.I0417)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.D0418)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2309)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2310)	
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20("T20020000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002022Constants.DEDUCIBLE_LIMITATION_KEYS_1)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LM1212Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LM538Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20B("T20020B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LQ1032Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LQ1033_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LQ1033_2Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

		, PAG20C("T20020C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LM1535Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LM1561Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LM393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LM506)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LM1579Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})
		
		, PAG20D("T20020D00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")			
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID650)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID651)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID652)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID653)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID654)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID1270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID1271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID1522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID655)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID656)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID658)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID659)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID660)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID662)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID664)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID665)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.ID666)				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LM1494Key.values())
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
		
						addSignedKey(line, mod200, Mod2002022Key.CN987, isComplementary);
		
						for (int i = 1; i <= 12; i++) {
							addGroupNIF(line, mod200, i1++);
						}
						
						addSignedKey(line, mod200, Mod2002022Key.CN1897, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.CN1901, isComplementary);
		
						addSignedKey(line, mod200, Mod2002022Key.CN988, isComplementary);
						addUnSignedKey(line, mod200, Mod2002022Key.CNEST, 3, 0, isComplementary);
		
						for (int i = 1; i <= 5; i++) {
							addNIF(line, mod200.getEstablishments(), i2++);
						}
		
						addSignedKey(line, mod200, Mod2002022Key.CN989, isComplementary);
						
						addUnSignedKey(line, mod200, Mod2002022Key.LQ0N1, 4, 0, isComplementary); 
						addSignedKey(line, mod200, Mod2002022Key.LQ630, isComplementary); 
						addSignedKey(line, mod200, Mod2002022Key.LQ631, isComplementary); 
						addSignedKey(line, mod200, Mod2002022Key.LQ632, isComplementary); 
						addSignedKey(line, mod200, Mod2002022Key.LQ579, isComplementary); 
						
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
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022RIC_1Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.RC927)				
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022RIC_2Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LQ554Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002022LQ561Key.values())
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
		
						addUnSignedKey(line, mod200, Mod2002022Key.UT060, 7, 4, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UT500, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UT1227, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UT1228, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UT552, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UT1330, isComplementary);
		
						for (int i = 1; i <= 4; i++) {
							addUteBase(line, mod200, i1++);
						}
		
						addSignedKey(line, mod200, Mod2002022Key.UTC01, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UTC02, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UTC03, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UT062, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UTC04, isComplementary);
						addSignedKey(line, mod200, Mod2002022Key.UTC05, isComplementary);
		
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
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TR050)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TR051)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TR052)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TR053)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TR054)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TR055)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.TR056)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.TR626, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.TR627, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.TR628, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.TR629, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002022Key.TR625, 5, 2)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002022Constants.COMBINED_TAXATION_3)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002022Constants.COMBINED_TAXATION_4)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002022Constants.COMBINED_TAXATION_5)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26B("T20026B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria				
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002022KeyDC.DC2504, Mod2002022KeyDC.DC2690)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26C("T20026C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002022KeyDC.DC2714, Mod2002022KeyDC.DC3241)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26D("T20026D00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002022KeyDC.DC2884, Mod2002022KeyDC.DC3040)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26E("T20026E00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002022KeyDC.DC3044, Mod2002022KeyDC.DC3230)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
		})
		
		, PAG26F("T20026F00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")  // Indicador de página complementaria
				,(line, mod200, label) -> addBreakdownCorrectionKeys(line, mod200, Mod2002022KeyDC.DC3234, Mod2002022KeyDC.DC3400)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2306)					
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2302)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2304)	
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.DC2310)	
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
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ552) // Liquidación - Base imponible [552]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ562) // Liquidación - Cuota íntegra [562]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.BN621) // Liquidación - Líquido a ingresar o a devolver Estado [621]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002022Key.LQ2489) // Liquidación - Opción de fraccionamiento art. 19.1 LIS - Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS: Estado 
				
				,(line, mod200, label) -> {
					
					double importe = mod200.getDoubleValue(Mod2002022Key.BN621); // importe a ingresar o a devolver
					
					line.append(AonFiscalFileUtils.text(importe < 0 ? ("V".equals(mod200.getDevType()) ? "" : mod200.getDevType()) : "", 1)); // Devolución - Renuncia o por Transferencia ("blanco","R","D")
					line.append(AonFiscalFileUtils.signedZero(importe < 0 ? Math.abs(importe) : 0.0, DS, DD)); 						          // Devolución - Importe a devolver
					line.append(importe < 0 && "D".equals(mod200.getDevType()) ? "1" : "0");                                                  // Devolución - Marca SEPA (0 Vacía, 1 Cuenta España, 2 Unión Europea SEPA, 3 Resto Países) (Se asume cuenta de España)					
                    line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "", 34));         // Devolución - Número de cuenta IBAN (si devolución por transferencia)
					line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getBic() : "", 11));          // Devolución - Código SWIFT-BIC
					line.append(AonFiscalFileUtils.text(importe > 0 ? mod200.getPayType() : "", 1));    // Ingreso - Modalidad de ingreso. Uno de los siguientes valores "blanco", "I" Adeudo en cuenta, "U" Domiciliación
					if (mod200.getDoubleValue(Mod2002022Key.LQ2489) > 0) 
					   line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(Mod2002022Key.LQ2489), DS, DD));    // Ingreso - Importe a ingresar
					else
					   line.append(AonFiscalFileUtils.signedZero(importe > 0 ? importe : 0.0, DS, DD));    // Ingreso - Importe a ingresar
					line.append(AonFiscalFileUtils.text(importe > 0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType()))	? mod200.getIban() : "", 34));  // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)

					addSignedKey(line, mod200, Mod2002022Key.BN1020); // Abono/Compensación - Abono por conversión de activos impuesto diferido - A
					addSignedKey(line, mod200, Mod2002022Key.BN1021); // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C
					addSignedKey(line, mod200, Mod2002022Key.LQ3318); // Abono/Compensación - Resultado de conversión de AID tras regularización: Abono 
					addSignedKey(line, mod200, Mod2002022Key.LQ2490); // Abono/Compensación - Resultado de conversión de AID tras regularización: Compensación 
					addSignedKey(line, mod200, Mod2002022Key.LQ2493); // Abono/Compensación - Resultado de conversión de AID tras regularización: A ingresar 

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

		private Pages2022(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002022 mod200, Writer line) throws IOException {

			// Controles determinadas páginas que solo se ponen si están marcados ciertos caracteres
			boolean addPage = true;

			// Página 9 a 11. ECPN. Si están marcadas las casillas 75, 76 o 77
			if (this == Pages2022.PAG09 || this == Pages2022.PAG10 || this == Pages2022.PAG11) {
				addPage = (mod200.getEcpnType() == EcpnType.NORMAL) || (mod200.getEcpnType() == EcpnType.ABREVIADO) || (mod200.getEcpnType() == EcpnType.PYMES);
			}

			// Página 22. Regimen especial de la reserva para inversiones en Canarias y Cooperativas
			if (this == Pages2022.PAG22) {
				addPage = (mod200.getDoubleValue(Mod2002022Key.C0029) == 1)
						|| (mod200.getDoubleValue(Mod2002022Key.C0017) == 1)
						|| (mod200.getDoubleValue(Mod2002022Key.C0018) == 1)
						|| (mod200.getDoubleValue(Mod2002022Key.C0019) == 1);
			}

			// Página 24. Agrupaciones de interes económico y UTES (regimen especial).
			// Caracteres 013 o 014 marcados
			if (this == Pages2022.PAG24) {
				addPage = (mod200.isChecked(Mod2002022Key.C0013) || mod200.isChecked(Mod2002022Key.C0014));
			}

			// Página 26. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2022.PAG26) {
				addPage = (mod200.getDoubleValue(Mod2002022Key.C0028) == 1);
			}
			
			// Páginas 26B: Solo si hay algún importe en la pagina
			if (this == Pages2022.PAG26B) {				
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002022KeyDC.DC2504, Mod2002022KeyDC.DC2690);
			}
			
			// Páginas 26C: Solo si hay algún importe en la pagina			
			if (this == Pages2022.PAG26C) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002022KeyDC.DC2714, Mod2002022KeyDC.DC3241);
			}
			
			// Páginas 26D: Solo si hay algún importe en la pagina
			if (this == Pages2022.PAG26D) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002022KeyDC.DC2884, Mod2002022KeyDC.DC3040);
			}

			// Páginas 26E: Solo si hay algún importe en la pagina
			if (this == Pages2022.PAG26E) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002022KeyDC.DC3044, Mod2002022KeyDC.DC3230);
			}
			
			// Páginas 26F: Solo si hay algún importe en la pagina
			if (this == Pages2022.PAG26F) {
				addPage = addBreakdownCorrectionKeys(null, mod200, Mod2002022KeyDC.DC3234, Mod2002022KeyDC.DC3400) ||
						  mod200.getDoubleValue(Mod2002022Key.DC2305) != 0 ||
						  mod200.getDoubleValue(Mod2002022Key.DC2301) != 0 ||
						  mod200.getDoubleValue(Mod2002022Key.DC2303) != 0 ||
						  mod200.getDoubleValue(Mod2002022Key.DC2307) != 0 ||
						  mod200.getDoubleValue(Mod2002022Key.DC2309) != 0 ||
						  mod200.getDoubleValue(Mod2002022Key.DC2306) != 0 ||					
						  mod200.getDoubleValue(Mod2002022Key.DC2302) != 0 ||	
						  mod200.getDoubleValue(Mod2002022Key.DC2304) != 0 ||	
						  mod200.getDoubleValue(Mod2002022Key.DC2308) != 0 ||
						  mod200.getDoubleValue(Mod2002022Key.DC2310) != 0;
			}
			
			// Añadir el contenido de la página
			if (addPage) {
				for (IPropertyFiller propertyFiller : this.propertyFillers) {
					propertyFiller.propertyFill(line, mod200, this.tag);
				}
			}
		}
	}

	public static void fillWriter(Mod2002022 mod200, Writer line) throws IOException {

		line.append("<T2000" + mod200.getYear() + "0A0000>"); // Etiqueta inicio de fichero
		for (Pages2022 page : Pages2022.values()) {
			page.fillPage(mod200, line);
		}
		line.append("</T2000" + mod200.getYear() + "0A0000>"); // Etiqueta fin de fichero
		line.close();

	}

}
