package com.esferalia.aon.occam.server.fiscal.format;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.DoubleVariable2018;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018.EcpnType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Character;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018LQ547Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN570Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN1344Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN1280Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN572Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN571Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN573Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN585Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN584Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN590Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN588Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN565Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN1040Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN1041Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018LQ1033_1Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018LQ1033_2Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN082Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018LQ1032Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018LQ554Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018LQ561Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018CorrectionKey;

import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002018Writer {

	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************

	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static int DS = 17; // Tamaño de digitos por defecto para los importes
	private static int DD = 2; // Decimales por defecto para los importes

	// Añade un importe de una casilla del modelo (con signo, longitud y decimales
	// por defecto, relleno con ceros por la izquierda)
	private static void addSignedKey(Writer line, Mod2002018 mod200, Mod2002018Key key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key), DS, DD));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002018 mod200, Mod2002018Key key, boolean isComplementary)
			throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key), DS, DD));
		}
	}

	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por
	// la izquierda)
	private static void addUnSignedKey(Writer line, Mod2002018 mod200, Mod2002018Key key, int size, int dec)
			throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key), size, dec));
	}

	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso
	// no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002018 mod200, Mod2002018Key key, int size, int dec,
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
	private static void addLegalRepresentative(Writer line, Mod2002018 mod200, int index) throws IOException {
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
	private static void addCompanyAdministrator(Writer line, Mod2002018 mod200, int index) throws IOException {
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
			if (mod200.getDoubleValue(Mod2002018Key.C0021) == 1)
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
	private static void addCompanyParticipationOut(Writer line, Mod2002018 mod200, int index) throws IOException {
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
	private static void addCompanyParticipationIn(Writer line, Mod2002018 mod200, int index) throws IOException {
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

	// UTES - Deducción para evitar la doble imposicion
	private static void addUteBase(Writer line, Mod2002018 mod200, int index) throws IOException {
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
	private static void addUteParticipation(Writer line, Mod2002018 mod200, int index) throws IOException {
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
	private static void addUteForeign(Writer line, Mod2002018 mod200, int index) throws IOException {
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
	private static void addGroupNIF(Writer line, Mod2002018 mod200, int index) throws IOException {
		String document = "";
		String country = "";
		if (index < mod200.getGroupEntities().size()) {
			document = mod200.getGroupEntities().get(index).getDocument();
			country = mod200.getGroupEntities().get(index).getCountry();
		}
		line.append(AonFiscalFileUtils.text(document,15));                        // NIF 
		line.append(AonFiscalFileUtils.spaces(5));                                // Reservado para la AEAT
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
	private static void addBreakdown(Writer l, Mod2002018 m, IMod200KeysProvider[] keysProvider) throws IOException {
		addBreakdown(l, m, keysProvider, null, null);
	}
	
	private static void addBreakdown(Writer l, Mod2002018 m, IMod200KeysProvider[] keysProvider, Mod2002018Key fromKey, Mod2002018Key toKey) throws IOException {
		
		boolean printKey = false;
		for (IMod200KeysProvider kp : keysProvider) {
			for (int i=0;i<kp.getKeys().length;i++) {
				Mod2002018Key key = kp.getKeys()[i];
				if (key != null) {
					if (fromKey == null || (fromKey != null && key == fromKey))
						printKey = true;
					if (printKey) {						
						addSignedKey(l, m, key);
						// Correcciones al Resultado Contable: Algunas casillas llevan tambien desglose 
						if (kp instanceof Mod2002018CorrectionKey) 
						 if (((Mod2002018CorrectionKey)kp).getDetail() != null)
						  for (IMod200KeysProvider kpc : ((Mod2002018CorrectionKey)kp).getDetail()) {
							  addSignedKey(l, m, kpc.getKeys()[i]); 
						  }					
					}
					if (toKey != null && key == toKey)
						return;				
				}
			}			
		}	
	}
	
	// Desgloses de las deducciones doble imposicion (hay casillas que llevan otro formato por que son porcentajes)
    private static void addBreakdownDoubleImposition(Writer l, Mod2002018 m, IMod200KeysProvider[] keysProvider) throws IOException {
		
		for (IMod200KeysProvider kp : keysProvider) {
			int pos = 1;
			for (Mod2002018Key key : kp.getKeys()) {
				if (key != null) {
					// Casillas BN103x van con formato 7,2
					if (key == Mod2002018Key.BN103A || key == Mod2002018Key.BN103B || key == Mod2002018Key.BN103C || key == Mod2002018Key.BN103D)
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

    private static void addBreakdownFromConstants(Writer l, Mod2002018 m, Mod2002018Key[][] keys) throws IOException {
		
    	for (int i=0; i<keys.length; i++) {
    		if (keys[i] != null)
    		 for (int j=0; j<keys[i].length; j++) {
    		    Mod2002018Key key = keys[i][j];
				if (key != null) {
					addSignedKey(l, m, key);
				}
    		 }
    	}
    	
	}
    

	// **** FIN VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****

	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer line, Mod2002018 mod200, String tag) throws IOException;
	}

	private enum Pages2018 {

		  PAG00("AUX",new IPropertyFiller[] {
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(70))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(4))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(4))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(213))
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
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0001, 1, 0)				
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0002, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0003, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0004, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0005, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0011, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0013, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0014, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0017, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0018, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0019, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0021, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0023, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0024, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0025, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0031, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0032, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0036, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0048, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0058, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0060, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0066, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0006, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0015, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0022, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0028, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0047, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0049, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0035, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0029, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0033, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0034, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0038, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0046, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0012, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0064, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0057, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0062, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0020, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0007, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0009, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0010, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0016, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0026, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0027, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0030, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0039, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0043, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0045, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0063, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0071, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0070, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0059, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0065, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0067, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0072, 1, 0)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0073, 1, 0)				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getBalanceType() == null ? 0 : mod200.getBalanceType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned((mod200.getEcpnType() == null || mod200.getEcpnType() == EcpnType.NO_CONSTA) ? 0 : mod200.getEcpnType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.unsigned(mod200.getPygType() == null || mod200.getDoubleValue(Mod2002018Key.C0026) == 1 ? 0 : mod200.getPygType().ordinal() + 1, 1, 0))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0061, 1, 0)
				,(line, mod200, label) -> line.append(AonStringUtils.isEmpty(mod200.getFiscalGroup()) ? AonFiscalFileUtils.spaces(7) : AonFiscalFileUtils.unsigned(mod200.getFiscalGroup(), 7, 0))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantDocument(), 9))
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(), 15))
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0041, 9, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.C0042, 9, 2)
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
				,(line, mod200, label) -> line.append(mod200.getDoubleValue(Mod2002018Key.C0015)==1?(mod200.getDoubleValue(Mod2002018Key.C0015G)==1?"1":"2"):" ")   // Entidad ZEC - Pertenencia a grupo fiscal (valores blanco-1-2)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(199)) // Reservado para la AEAT
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
		
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
						addCompanyAdministrator(line, mod200, a++);
		
						addCompanyParticipationOut(line, mod200, b1++);
						addCompanyParticipationOut(line, mod200, b1++);
						addCompanyParticipationOut(line, mod200, b1++);
		
						addUnSignedKey(line, mod200, Mod2002018Key.P1501, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002018Key.P1502, DS, DD, isComplementary);
						addUnSignedKey(line, mod200, Mod2002018Key.P1503, DS, DD, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.P1504, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.P1505, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.P1506, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.P1809, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.P1810, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.P1507, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.P1508, isComplementary);
		
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
						addCompanyParticipationIn(line, mod200, b2++);
		
						addUnSignedKey(line, mod200, Mod2002018Key.POR51, 5, 2, isComplementary);
						addUnSignedKey(line, mod200, Mod2002018Key.PORES, 5, 2, isComplementary);
		
						line.append(AonFiscalFileUtils.spaces(200)); // Reservado para la AEAT
		
						isComplementary = true;
						addEndLabel(line, label);
					}
				} 
			})
		
		, PAG03("T20003000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA101)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA102)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA103)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA104)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA105)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA106)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA107)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA108)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA700)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA109)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA110)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA111)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA112)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA113)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA114)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA115)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA116)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA117)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA118)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA119)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA120)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA121)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA122)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA123)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA124)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA125)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA126)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA127)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA128)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA129)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA130)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA131)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA132)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA133)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA134)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA135)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA136)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA137)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA138)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA139)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA140)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA141)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA142)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA143)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA144)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA145)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA146)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA147)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA148)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA701)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG04("T20004000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA149)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA151)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA152)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA153)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA154)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA155)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA156)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA157)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA158)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA159)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA160)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA161)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA162)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA163)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA164)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA165)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA166)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA167)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA168)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA169)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA170)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA171)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA172)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA173)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA174)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA175)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA176)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA177)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA178)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA179)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BA180)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG05("T20005000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP185)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP186)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP187)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP188)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP189)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP190)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP191)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP192)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP193)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP702)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP1001)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP1002)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP194)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP195)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP196)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP197)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP198)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP199)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP201)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP202)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP203)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP204)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP205)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP206)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP207)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP208)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP209)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP211)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP212)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP213)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP214)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP215)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP216)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP217)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP218)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP219)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP220)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP221)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP222)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP223)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP224)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP225)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP226)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP227)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
	
		, PAG06("T20006000", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP228)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP229)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP703)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP704)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP231)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP232)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP233)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP234)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP235)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP236)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP237)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP238)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP239)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP240)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP241)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP242)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP243)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP244)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP245)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP246)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP247)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP248)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP249)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP250)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP251)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BP252)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG07("T20007000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG255)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG256)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG257)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG711)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG705)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG706)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG707)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG708)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG258)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG259)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG260)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG261)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG760)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG761)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG262)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG762)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG763)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG263)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG264)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG265)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG266)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG267)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG268)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG269)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG273)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG274)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG275)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG276)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG277)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG278)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG279)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG281)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG282)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG283)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG709)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG284)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG287)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG288)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG289)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG290)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG291)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG292)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG293)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG710)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG294)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG295)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG296)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG08("T20008000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG297)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG298)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG299)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG300)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG303)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG304)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG305)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG306)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG307)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG308)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG309)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG310)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG311)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG312)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG313)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG314)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG315)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG316)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG317)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG318)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG319)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG320)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG321)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG322)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG323)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG329)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG324)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG325)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG326)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG327)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG328)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.PG500)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)			
			})

		, PAG09("T20009000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0336)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0337)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0338)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0339)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0340)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0341)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0342)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0343)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0345)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0346)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0347)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0348)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0349)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0350)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0351)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0352)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0353)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0354)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.T0355)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG10("T20010000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC380)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC381)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC382)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC383)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC384)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC385)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC386)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC394)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC395)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC396)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC397)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC398)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC400)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC409)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC410)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC411)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC412)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC413)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC422)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC423)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC424)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC425)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC426)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC427)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC428)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC436)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC437)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC438)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC439)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC440)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC441)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC442)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC450)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC451)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC452)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC453)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC454)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC455)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC456)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC464)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC465)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC466)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC467)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC468)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC469)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC470)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC478)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC479)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC481)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC482)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC483)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC484)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC492)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC493)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC494)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC495)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC496)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC497)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC498)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC507)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC508)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC509)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC511)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC512)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC523)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC524)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC525)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC526)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC534)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC535)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC536)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC537)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC538)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC539)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC540)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC548)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC549)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC551)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC564)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC580)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC591)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC592)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC594)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC595)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC596)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC607)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC608)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC609)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC610)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC620)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC623)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC624)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC715)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC716)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC717)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC718)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC719)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC720)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC721)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC729)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC730)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC731)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC732)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC733)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC734)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC735)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC632)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC634)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC635)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC636)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC637)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC638)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG11("T20011000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC387)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC388)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC389)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC390)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC391)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC392)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC401)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC402)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC403)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC404)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC405)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC406)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC407)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC415)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC416)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC417)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC418)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC419)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC420)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC421)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC429)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC430)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC431)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC432)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC433)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC434)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC435)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC443)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC444)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC445)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC446)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC448)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC449)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC457)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC458)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC461)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC462)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC463)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC471)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC472)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC475)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC476)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC477)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC485)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC486)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC489)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC490)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC491)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC499)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC502)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC503)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC504)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC505)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC513)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC514)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC515)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC516)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC517)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC518)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC519)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC527)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC528)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC529)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC530)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC531)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC532)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC533)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC541)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC542)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC543)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC544)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC546)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC557)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC558)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC574)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC586)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC589)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC598)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC613)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC614)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC625)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC626)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC627)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC628)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC629)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC630)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC631)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC722)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC723)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC724)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC725)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC726)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC727)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC728)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC736)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC737)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC738)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC739)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC740)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC741)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC742)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC639)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC640)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC641)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC643)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC644)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TC645)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG12("T20012000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ500)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ301)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ302)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ501)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1230)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1231)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.VOLOPE, 1, 0) // Volumen Operaciones
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018CorrectionKey.values(), Mod2002018Key.I0355, Mod2002018Key.D2185)  // Correcciones al resultado contable
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})
		
		, PAG13("T20013000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				
		        //,(line, mod200, label) -> addBreakdownCorrectionKey(line, mod200, Mod2002018Key.I2186, Mod2002018Key.D0414)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018CorrectionKey.values(), Mod2002018Key.I2186, Mod2002018Key.D0414)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.I0417)				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.D0418)
				
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ578)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ579)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1029)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1030)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1031)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ550)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ550TG) // Parte de la base imponible del período impositivo que tributa al tipo general (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ550T0) // Parte de la base imponible del período impositivo que tributa al tipo del 0% (antes de compensación de bases imponibles negativas) 
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1032)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ547)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ552)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1033)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1034)
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14("T20014000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1330)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ553)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ554)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ555)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ556)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ559)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ520)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ521)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ545)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1509)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.LQ558, 4, 2)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ560)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ210)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ480)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ408)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1037)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ593)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1510)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ561)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1285)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1286)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1331)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ562)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ1038)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN567)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN568)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN563)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN566)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN576)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN569)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN570)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1344)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1280)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN572)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN571)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN573)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN575)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN577)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN581)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN582)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN583)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN584)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN588)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1039)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN565)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN590)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN399)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN082)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1040)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1041)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN592)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG14B("T20014B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1785)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1786)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1787)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1788)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1789)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1790)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1791)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1792)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1793)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1794)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1795)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1796)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN597)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1797)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1798)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1799)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN599)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN600)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN601)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN602)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN603)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN604)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN605)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN606)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN611)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN612)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN615)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN616)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN633)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN642)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN617)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN618)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN619)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN620)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1234A)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN083)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1332)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1200)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1042)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1333)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN621)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN622)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1020)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1043)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM506)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1021)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN1044)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG15("T20015000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018LQ547Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002018BN570Key.values())  
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002018BN1344Key.values()) 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN1280Key.values()) 
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina
			})

		, PAG16("T20016000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002018BN572Key.values())
				,(line, mod200, label) -> addBreakdownDoubleImposition(line, mod200, Mod2002018BN571Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN573Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN585Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG16B("T20016B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN584Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN590Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG17("T20017000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN588Key.values(), null, Mod2002018Key.BN1425)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG18("T20018000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label) 
				,(line, mod200, label) -> line.append(" ") 
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN588Key.values(), Mod2002018Key.BN1623, null)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
		})

		, PAG18B("T20018B00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN565Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN1040Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN1041Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018LQ1033_1Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018LQ1033_2Key.values())
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG19("T20019000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018BN082Key.values())
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID650)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID651)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID652)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID653)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID654)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID1270)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID1271)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID1522)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID655)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID656)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID658)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID659)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID660)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID662)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID664)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID665)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.ID666)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC001)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC002)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC003)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC004)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC005)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC006)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC007)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC008)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC009)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC010)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC011)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC012)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC013)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC014)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC015)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC016)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC017)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC018)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC019)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC020)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC021)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC022)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC023)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC024)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC025)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC026)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC027)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC028)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC029)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC030)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC031)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC032)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC033)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC034)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC035)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC036)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC037)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC038)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC039)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC040)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC041)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC042)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC043)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC044)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC045)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC046)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC047)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC048)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC049)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC050)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC051)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC052)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC053)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.DC054)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20("T20020000", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.DEDUCIBLE_LIMITATION_KEYS_1)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.DEDUCIBLE_LIMITATION_KEYS_2)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.DEDUCIBLE_LIMITATION_KEYS_3)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) 
			})

		, PAG20B("T20020B00", new IPropertyFiller[] { 
				(line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018LQ1032Key.values())
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.DOTACION_KEYS_1)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.NUMPER, 1, 0)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM1515)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM1516)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM1585)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM1517)
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(200)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label)
			})

		, PAG20C("T20020C00", new IPropertyFiller[] { 
				 (line, mod200, label) -> addStartLabel(line, label)
				,(line, mod200, label) -> line.append(" ")
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.DOTACION_KEYS_3)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.DOTACION_KEYS_4)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM393)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM150)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LM506)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.DOTACION_KEYS_6)				
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
		
						addSignedKey(line, mod200, Mod2002018Key.CN987, isComplementary);
		
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
		
						addSignedKey(line, mod200, Mod2002018Key.CN988, isComplementary);
						addUnSignedKey(line, mod200, Mod2002018Key.CNEST, 3, 0, isComplementary);
		
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
		
						addSignedKey(line, mod200, Mod2002018Key.CN989, isComplementary);
		
						addUnSignedKey(line, mod200, Mod2002018Key.LQ0N1, 4, 0, isComplementary); 
						addSignedKey(line, mod200, Mod2002018Key.LQ630, isComplementary); 
						addSignedKey(line, mod200, Mod2002018Key.LQ631, isComplementary); 
						addSignedKey(line, mod200, Mod2002018Key.LQ632, isComplementary); 
						addSignedKey(line, mod200, Mod2002018Key.LQ579, isComplementary); 
						
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
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.CANARIAS_KEYS)
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018LQ554Key.values())
				,(line, mod200, label) -> addBreakdown(line, mod200, Mod2002018LQ561Key.values())
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
		
						addUnSignedKey(line, mod200, Mod2002018Key.UT060, 7, 4, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UT500, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UT1227, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UT1228, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UT552, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UT1330, isComplementary);
		
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
		
						addSignedKey(line, mod200, Mod2002018Key.UTC01, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UTC02, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UTC03, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UT062, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UTC04, isComplementary);
						addSignedKey(line, mod200, Mod2002018Key.UTC05, isComplementary);
		
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
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TR050)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TR051)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TR052)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TR053)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TR054)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TR055)
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.TR056)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.TR626, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.TR627, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.TR628, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.TR629, 5, 2)
				,(line, mod200, label) -> addUnSignedKey(line, mod200, Mod2002018Key.TR625, 5, 2)
				,(line, mod200, label) -> addBreakdownFromConstants(line, mod200, Mod2002018Constants.COMBINED_TAXATION_3)
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
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ552) // Liquidación - Base imponible [552]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.LQ562) // Liquidación - Cuota íntegra [562]
				,(line, mod200, label) -> addSignedKey(line, mod200, Mod2002018Key.BN621) // Liquidación - Líquido a ingresar o a devolver Estado [621]

				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(17)) // RESERVADO AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(17)) // RESERVADO AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(17)) // RESERVADO AEAT
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.zeros(17)) // RESERVADO AEAT
				,(line, mod200, label) -> {
					
					double importe = mod200.getDoubleValue(Mod2002018Key.BN621); // importe a ingresar o a devolver

					line.append(AonFiscalFileUtils.text(importe < 0 ? ("V".equals(mod200.getDevType()) ? "" : mod200.getDevType()) : "", 1)); // Devolución - Renuncia o por Transferencia ("blanco","R","D")
					line.append(AonFiscalFileUtils.signedZero(importe < 0 ? Math.abs(importe) : 0.0, DS, DD)); 						          // Devolución - Importe a devolver
					line.append(importe < 0 && "D".equals(mod200.getDevType()) ? "1" : "0");                                                  // Devolución - Marca SEPA (0 Vacía, 1 Cuenta España, 2 Unión Europea SEPA, 3 Resto Países) (Se asume cuenta de España)					
                    line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "", 34));         // Devolución - Número de cuenta IBAN (si devolución por transferencia)
					line.append(AonFiscalFileUtils.text(importe < 0 && "D".equals(mod200.getDevType()) ? mod200.getBic() : "", 11));          // Devolución - Código SWIFT-BIC
					line.append(AonFiscalFileUtils.text(importe > 0 ? mod200.getPayType() : "", 1));    // Ingreso - Modalidad de ingreso. Uno de los siguientes valores "blanco", "I" Adeudo en cuenta, "H" Efectivo, "U"	Domiciliación
					line.append(" "); // RESERVADO AEAT
					line.append(" "); // RESERVADO AEAT
					line.append(AonFiscalFileUtils.signedZero(importe > 0 ? importe : 0.0, DS, DD));    // Ingreso - Importe a ingresar
					line.append(AonFiscalFileUtils.text(importe > 0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType()))	? mod200.getIban() : "", 34));  // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)

					addSignedKey(line, mod200, Mod2002018Key.LM150); // Abono/Compensación - Abono por conversión de activos impuesto diferido - A
					addSignedKey(line, mod200, Mod2002018Key.LM506); // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C

					line.append(importe == 0 ? "1" : "0");   // Cuota Cero "0" o "1"
					
					// Datos bancarios devolucion por transferencia a cuenta bancaria abierta en el extranjero (fuera de la unión europea) (Se asume que la cuenta es de España)
					line.append(AonFiscalFileUtils.spaces(70)); // Devolución - Banco/Bank name
					line.append(AonFiscalFileUtils.spaces(35)); // Devolución - Dirección del Banco/ Bank adress
					line.append(AonFiscalFileUtils.spaces(30)); // Devolución - Ciudad/City
					line.append(AonFiscalFileUtils.spaces(40)); // Devolución - País/Country
					line.append(AonFiscalFileUtils.spaces( 2)); // Devolución - Código País/Country code
					
				}
				
				,(line, mod200, label) -> line.append(AonFiscalFileUtils.spaces(23)) // Reservado para la AEAT
				,(line, mod200, label) -> addEndLabel(line, label) // Etiqueta fin de pagina

		});

		private String tag;
		private IPropertyFiller[] propertyFillers;

		private Pages2018(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002018 mod200, Writer line) throws IOException {

			// Controles determinadas páginas que solo se ponen si están marcados ciertos
			// caracteres
			boolean addPage = true;

			// Página 9 a 11. ECPN. Si están marcadas las casillas 75, 76 o 77
			if (this == Pages2018.PAG09 || this == Pages2018.PAG10 || this == Pages2018.PAG11) {
				addPage = (mod200.getEcpnType() == EcpnType.NORMAL) || (mod200.getEcpnType() == EcpnType.ABREVIADO) || (mod200.getEcpnType() == EcpnType.PYMES);
			}

			// Página 22. Regimen especial de la reserva para inversiones en Canarias y
			// Cooperativas
			if (this == Pages2018.PAG22) {
				addPage = (mod200.getDoubleValue(Mod2002018Key.C0029) == 1)
						|| (mod200.getDoubleValue(Mod2002018Key.C0017) == 1)
						|| (mod200.getDoubleValue(Mod2002018Key.C0018) == 1)
						|| (mod200.getDoubleValue(Mod2002018Key.C0019) == 1);
			}

			// Página 24. Agrupaciones de interes económico y UTES (regimen especial).
			// Caracteres 013 o 014 marcados
			if (this == Pages2018.PAG24) {
				addPage = (mod200.isChecked(Mod2002018Key.C0013) || mod200.isChecked(Mod2002018Key.C0014));
			}

			// Página 26. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2018.PAG26) {
				addPage = (mod200.getDoubleValue(Mod2002018Key.C0028) == 1);
			}

			// Añadir el contenido de la página
			if (addPage) {
				for (IPropertyFiller propertyFiller : this.propertyFillers) {
					propertyFiller.propertyFill(line, mod200, this.tag);
				}
			}
		}
	}

	public static void fillWriter(Mod2002018 mod200, Writer line) throws IOException {

		line.append("<T2000" + mod200.getYear() + "0A0000>"); // Etiqueta inicio de fichero
		for (Pages2018 page : Pages2018.values()) {
			page.fillPage(mod200, line);
		}
		line.append("</T2000" + mod200.getYear() + "0A0000>"); // Etiqueta fin de fichero
		line.close();

	}

	private static void setDoubleValue2018(Mod2002018 mod200, Mod2002018Key key, double value) {

		DoubleVariable2018 t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariable2018(key);
			t.setValue(value);
			mod200.addVariable(t);
		} else {
			t.setValue(value);
		}
	}

	public static void main(String argv[])
			throws IOException, InterruptedException, ParserConfigurationException, SAXException {

		// Prueba para ver si la longitud de todas las páginas es correcta
		try {

			// Creamos un nuevo modelo 200 2018
			Mod2002018 mod200 = new Mod2002018();

			// Inicializamos las casillas con un valor aleatorio
			for (Mod2002018Key key : Mod2002018Key.values()) {
				double value = AonNumberUtils.todouble(AonRandomStringUtils.randomNumeric(4));
				value = (AonMathUtils.isZero(value)) ? value : AonMathUtils.round(value / 100);
				setDoubleValue2018(mod200, key, value);
			}

			// Periodo inicio y fin
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			mod200.setPeriodStart(sdf.parse("01-01-2018"));
			mod200.setPeriodEnd(sdf.parse("31-12-2018"));
			mod200.setYear(2018);

			// Caracteres todos a cero excepto 9, 13, 28 y 29 para que salgan todas las
			// páginas
			for (Mod2002018Key key : Mod2002018Character.CHARACTERS_KEYS) {
				setDoubleValue2018(mod200, key, 0.0);
			}
			setDoubleValue2018(mod200, Mod2002018Key.C0009, 1.0);
			setDoubleValue2018(mod200, Mod2002018Key.C0028, 1.0);
			setDoubleValue2018(mod200, Mod2002018Key.C0029, 1.0);
			setDoubleValue2018(mod200, Mod2002018Key.C0013, 1.0);

			// Estados de cuentas de IIC
			setDoubleValue2018(mod200, Mod2002018Key.C0061, 0.0);

			// Numero de periodo impositivo
			setDoubleValue2018(mod200, Mod2002018Key.NUMPER, 0.0);

			// Balance, ECPN y Cuenta PyG Normal
			mod200.setBalanceType(BalanceType.NORMAL);
			mod200.setEcpnType(EcpnType.NORMAL);			
			mod200.setPygType(BalanceType.NORMAL);
			
			// Volumen de operaciones
			setDoubleValue2018(mod200, Mod2002018Key.VOLOPE, 0.0);			

			// Añadir un administrador para que salga la pagina 2
//			if (mod200.getAdministrators() == null ) {
//				mod200.setAdministrators( new LinkedList<CompanyAdministrator>());
//			}
//
//		    // Crear un objeto y asignar los datos		
//			CompanyAdministrator ca = new CompanyAdministrator();
//			ca.setDocument("12345678Z");
//			ca.setRepresentative(true);
//			ca.setName("ADMINISTRADOR DE PRUEBA");
//			ca.setResidence("DOMICILIO FISCAL");
//			ca.setProvince(50);
//			
//			// Añadirlo a la lista
//			mod200.getAdministrators().add(ca);

			// Generamos el fichero
			String filename = "c:\\tmp\\prueba_M200_2018.txt";
			BufferedWriter line = new BufferedWriter(new FileWriter(filename));
			fillWriter(mod200, line);

			// Mostramos la longitud de cada pagina
			FileInputStream input = new FileInputStream(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new InputStreamReader(input, "ISO-8859-1")));

			// Longitudes de todas las páginas
			HashMap<String, Integer> m = new HashMap<String, Integer>();
			m.put(Pages2018.PAG00.tag,  311);
			m.put(Pages2018.PAG01.tag,  888);
			m.put(Pages2018.PAG02.tag, 2162);
			m.put(Pages2018.PAG03.tag, 1074);
			m.put(Pages2018.PAG04.tag,  768);
			m.put(Pages2018.PAG05.tag, 1006);
			m.put(Pages2018.PAG06.tag,  683);
			m.put(Pages2018.PAG07.tag, 1108);
			m.put(Pages2018.PAG08.tag,  853);
			m.put(Pages2018.PAG09.tag, 581);
			m.put(Pages2018.PAG10.tag, 2723);
			m.put(Pages2018.PAG11.tag, 2502);
			m.put(Pages2018.PAG12.tag, 3591);
			m.put(Pages2018.PAG13.tag, 4423);
			m.put(Pages2018.PAG14.tag, 1095);
			m.put(Pages2018.PAG14B.tag,1040); 
			m.put(Pages2018.PAG15.tag, 2475);
			m.put(Pages2018.PAG16.tag, 2436);
			m.put(Pages2018.PAG16B.tag,2281);
			m.put(Pages2018.PAG17.tag, 3318);
			m.put(Pages2018.PAG18.tag, 3386);
			m.put(Pages2018.PAG18B.tag,2417);
			m.put(Pages2018.PAG19.tag, 2519); 
			m.put(Pages2018.PAG20.tag, 1601);
			m.put(Pages2018.PAG20B.tag,1211);
			m.put(Pages2018.PAG20C.tag,1771);
			m.put(Pages2018.PAG21.tag,  751);
			m.put(Pages2018.PAG22.tag, 2009);
			//m.put(Pages2018.PAG23.tag, 1014);
			m.put(Pages2018.PAG24.tag, 2126);
			// m.put(Pages2018.PAG25.tag, 3685);
			m.put(Pages2018.PAG26.tag, 1558);
			m.put(Pages2018.DID.tag,    605);

			for (Pages2018 page : Pages2018.values()) {
				NodeList nodeList = doc.getElementsByTagName(page.tag);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					String lin = "<" + page.tag + ">" + node.getTextContent() + "</" + page.tag + ">";
					System.out.println(AonStringUtils.rightPad(node.getNodeName(), 9) + " - "
							+ AonStringUtils.leftPad(AonNumberUtils.toString(lin.length()), 4) + " - "
							+ AonStringUtils.leftPad(m.get(page.tag).toString(), 4)
							+ (lin.length() != m.get(page.tag) ? " **" : ""));
				}
			}

			System.out.println("");
			System.out.println("***** Fin Fichero : " + filename);
			System.out.println("");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

}
