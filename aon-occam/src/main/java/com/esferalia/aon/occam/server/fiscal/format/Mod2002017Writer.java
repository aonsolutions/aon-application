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

import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.DoubleVariable2017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Character;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002017Writer {
	
	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************
	
	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static int DS = 17;  // Tamaño de digitos por defecto para los importes 
	private static int DD =  2;  // Decimales por defecto para los importes
	
	// Añade un importe de una casilla del modelo (con signo, longitud y decimales por defecto, relleno con ceros por la izquierda) 
	private static void addSignedKey(Writer line, Mod2002017 mod200, Mod2002017Key key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DS, DD ));
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002017 mod200, Mod2002017Key key, boolean isComplementary) throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DS, DD ));
		}
	}
	
	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por la izquierda) 
	private static void addUnSignedKey(Writer line, Mod2002017 mod200, Mod2002017Key key, int size, int dec) throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key) , size, dec ));
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002017 mod200, Mod2002017Key key, int size, int dec, boolean isComplementary) throws IOException {
		
		if (isComplementary)
			line.append(AonFiscalFileUtils.zeros(size));
		else line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key) , size, dec ));
		
	}
	
	// Añade la etiqueta inicio de pagina
	private static void addStartLabel(Writer line, String label) throws IOException {
		
		line.append("<"+label+">");
		
	}
	
	// Añade la etiqueta fin de pagina
	private static void addEndLabel(Writer line, String label) throws IOException {
		
		line.append("</"+label+">");
		
	}
	
    // Declaración Representantes Legales de la Entidad
	private static void addLegalRepresentative(Writer line, Mod2002017 mod200, int index) throws IOException {
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
		line.append( AonFiscalFileUtils.text(name, 36) );
		line.append( AonFiscalFileUtils.text(document, 9) );
		line.append( AonFiscalFileUtils.dateZero(notaryDate) );
		line.append( AonFiscalFileUtils.text(notary,12));
	}
	
	// A. Relación de administradores
	private static void addCompanyAdministrator(Writer line, Mod2002017 mod200, int index) throws IOException {
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
            if (mod200.getDoubleValue(Mod2002017Key.C0021)==1)
            	province = mod200.getAdministrators().get(index).getProvinceStr(); // Provincia solo se pone si esta marcado Caracter 021
		}
		// Añadir los datos al Writer
		line.append( AonFiscalFileUtils.text(document,  9) );  // N.I.F.			
		line.append( AonFiscalFileUtils.text(fj,        1) );  // F/J	
		line.append( AonFiscalFileUtils.text(rpte,      1) );  // RPTE.                            
		line.append( AonFiscalFileUtils.text(name,     40) );  // Apellidos y nombre / Razón social
		line.append( AonFiscalFileUtils.text(residence,17) );  // Domicilio fiscal                 
		line.append( AonFiscalFileUtils.text(province,  2) );  // Código Provincial
		
	}	
	
	// Devuelve el codigo de provincia (por defecto) o el pais, según este cumplimentado 
	// uno u otro campo (province o country) de participaciones
	//private static String getProvinceCountry(CompanyParticipation cp) {
//	int province = cp.getProvince();
//	if (province==0)
//		return AonStringUtils.trimToEmpty(cp.getCountry()); // Pais
//	else return AonStringUtils.leftPad(Integer.toString(province), 2, "0");  // Provincia
	
	private static String getProvinceCountry(int province, String country) {

		if (province==0)
			return AonStringUtils.trimToEmpty(country); // Pais
		else return AonStringUtils.leftPad(Integer.toString(province), 2, "0");  // Provincia
		
	}
	
	// B.1. Participaciones declarante en otras entidades	
	private static void addCompanyParticipationOut(Writer line, Mod2002017 mod200, int index) throws IOException {
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
		double capital = 0; 
		double reserve = 0; 
		double otherAmounts = 0; 
		double result = 0;
		if (index < mod200.getParticipationsOut().size()) {			
            document     = mod200.getParticipationsOut().get(index).getDocument();	
            name         = mod200.getParticipationsOut().get(index).getName();	    
            province     = getProvinceCountry(mod200.getParticipationsOut().get(index).getProvince(),mod200.getParticipationsOut().get(index).getCountry());	
            percent      = mod200.getParticipationsOut().get(index).getPercent();
            nominalValue = mod200.getParticipationsOut().get(index).getNominalValue();  
            bookValue    = mod200.getParticipationsOut().get(index).getBookValue();  	
            incomes 	 = mod200.getParticipationsOut().get(index).getIncomes();       
            aValue       = mod200.getParticipationsOut().get(index).getaValue();        
            bValue       = mod200.getParticipationsOut().get(index).getbValue();        
            cValue       = mod200.getParticipationsOut().get(index).getcValue();        
            dValue       = mod200.getParticipationsOut().get(index).getdValue();        
            ccValue      = mod200.getParticipationsOut().get(index).getccValue();
            capital      = mod200.getParticipationsOut().get(index).getCapital();       
            reserve      = mod200.getParticipationsOut().get(index).getReserve();       
            otherAmounts = mod200.getParticipationsOut().get(index).getOtherAmounts();  
            result       = mod200.getParticipationsOut().get(index).getResult();        
		}
        line.append( AonFiscalFileUtils.text(document, 15 ));
        line.append( AonFiscalFileUtils.text(name,     30 ));
        line.append( AonFiscalFileUtils.text(province,  2 )); 
        line.append( AonFiscalFileUtils.unsigned(percent,5,2)); 
        line.append( AonFiscalFileUtils.signedZero(nominalValue,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(bookValue   ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(incomes 	   ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(aValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(bValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(cValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(ccValue     ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(dValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(capital     ,DS, DD)); 
        line.append( AonFiscalFileUtils.signedZero(reserve     ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(otherAmounts,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(result      ,DS, DD));
	}
	
	// B.2. Participaciones de personas o entidades en la declarante
	private static void addCompanyParticipationIn(Writer line, Mod2002017 mod200, int index) throws IOException {
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
//			if (AonStringUtils.isNotEmpty(document)) {
//				if (document == null || document.length() != 9) {
//					fj = "";
//				} else {
//					if (document.matches("^(A|B|C|D|F|G|J|P|Q|R|S).{8}")) {
//						fj = "J";
//					} else if (document.matches("^(E|H|U|V|N|W).{8}")) {
//						fj = "O";
//					} else {
//						fj = "F";
//					}
//				}
//			}
			name = mod200.getParticipationsIn().get(index).getName();
			province = getProvinceCountry(mod200.getParticipationsIn().get(index).getProvince(),mod200.getParticipationsIn().get(index).getCountry());   
			nominalValue = mod200.getParticipationsIn().get(index).getNominalValue();
			percent = mod200.getParticipationsIn().get(index).getPercent();
		}
		line.append( AonFiscalFileUtils.text(document, 15));
		line.append( AonFiscalFileUtils.text(rpte,      1));
		line.append( AonFiscalFileUtils.text(fjo,       1));					                                                      
		line.append( AonFiscalFileUtils.text(name,     37)); 
		line.append( AonFiscalFileUtils.text(province,  2));
		line.append( AonFiscalFileUtils.signedZero(nominalValue, DS, DD) ); 
		line.append( AonFiscalFileUtils.unsigned(percent, 5,2));
	}
	
	// UTES - Deducción para evitar la doble imposicion
	private static void addUteBase(Writer line, Mod2002017 mod200, int index) throws IOException {			
		double base = 0;
		double percent = 0;
		if (index < mod200.getUteBases().size()) {			   
			base = mod200.getUteBases().get(index).getBase();
			percent = mod200.getUteBases().get(index).getPercent();
		}
		line.append( AonFiscalFileUtils.signedZero(base, DS, DD) ); 
		line.append( AonFiscalFileUtils.unsigned(percent, 5,2));
	}
	
	// UTES - Relación de Socios
	private static void addUteParticipation(Writer line, Mod2002017 mod200, int index) throws IOException {
		String document = "";
		String rpte = "0";
		String fj = "";
		String rx = "";  // Según el PADIS este dato no debe cumplimentarse si esta marcada la casilla 013, pero si se puede si esta la 014 de caracteres
		String name = "";
		String province = "";
		double base = 0;
		double percent = 0;
		if (index < mod200.getUteParticipations().size()) {
			document = mod200.getUteParticipations().get(index).getDocument();   
			rpte = mod200.getUteParticipations().get(index).getRepresenStr();
			fj = mod200.getUteParticipations().get(index).getEntity();
			
			// FALTA - Campo para R/X Residente/No residente, que se utiliza cuando se marca la casilla 013
			//rx = mod200.getDoubleValue(Mod2002017Key.C0014)==1?mod200.getUteParticipations().get(index).get
			
			name = mod200.getUteParticipations().get(index).getName();			
			province = getProvinceCountry(mod200.getUteParticipations().get(index).getProvince(),mod200.getUteParticipations().get(index).getCountry());
			base = mod200.getUteParticipations().get(index).getBase();
			percent = mod200.getUteParticipations().get(index).getPercent();
		}
		line.append( AonFiscalFileUtils.text(document,9));
		line.append( AonFiscalFileUtils.text(rpte,1));
		line.append( AonFiscalFileUtils.text(fj,1));
		line.append( AonFiscalFileUtils.text(rx,1));
		line.append( AonFiscalFileUtils.text(name,34)); 
		line.append( AonFiscalFileUtils.text(province,2));
		line.append( AonFiscalFileUtils.signedZero(base,DS, DD) ); 
		line.append( AonFiscalFileUtils.unsigned(percent,7,4));
	}
	
	// UTES - Información de detalle de EP o UTE que operen en el extranjero...
	private static void addUteForeign(Writer line, Mod2002017 mod200, int index) throws IOException {
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
		line.append( AonFiscalFileUtils.text(identification,20));
		line.append( AonFiscalFileUtils.text(country,2));		
		line.append( AonFiscalFileUtils.signedZero(volume,DS,DD) );
		line.append( AonFiscalFileUtils.signedZero(pyg,DS,DD) );
		line.append( AonFiscalFileUtils.signedZero(adjust,DS,DD) );
		line.append( AonFiscalFileUtils.signedZero(deduction,DS,DD) );		
	}
	
	// Cifra de Negocios - Nif Entidades y Nif establecimientos permanentes
	private static void addNIF(Writer line, LinkedList<String> list, int index) throws IOException {
		String document = "";		
		if (index < list.size()) {
			document = list.get(index);
		}
		line.append( AonFiscalFileUtils.text(document,9) );		
	}
	
	// **** FIN VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	
	@FunctionalInterface
	private interface IPropertyFiller {		
		public void propertyFill(Writer line, Mod2002017 mod200, String tag) throws IOException;
	}
    
	private enum Pages2017 {
		
		 PAG00 ("AUX", new IPropertyFiller[] {				 
			 (line,mod200, label) -> addStartLabel(line,label)
 			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces( 70))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  4))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  4))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  9))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(213))
			,(line,mod200, label) -> addEndLabel(line,label)
		 })
		 
		,PAG01 ("T20001000", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")  				
				,(line,mod200, label) -> { // Tipo de declaración
					if ("N".equals( mod200.getResultType()) || AonStringUtils.isEmpty(mod200.getResultType())) {
						line.append( "N" );  
					} else if ("I".equals( mod200.getResultType()) ) {
						line.append( AonFiscalFileUtils.text(mod200.getPayType(),1) );  // Tipo de declaración
					} else if ("D".equals( mod200.getResultType()) ) {
						line.append( AonFiscalFileUtils.text(mod200.getDevType(),1) );  // Tipo de declaración
					}
				}
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseDocument(),9)) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseName(),80))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getYear(), 4,0))  // Ejercicio
				,(line,mod200, label) -> line.append( "0A" )                                               // Periodo (Constante 0A)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getPeriodStart()))					
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getPeriodEnd()))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPeriodType(),1,0))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getCnae()==null?"":mod200.getCnae().replace(".",""), 4))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterprisePhone1(),9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterprisePhone2(),9))
				
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0001, 1, 0 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0002, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0003, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0004, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0005, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0011, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0013, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0014, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0017, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0018, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0019, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0021, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0023, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0024, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0025, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0031, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0032, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0036, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0048, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0058, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0060, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0066, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0006, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0015, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0022, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0028, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0047, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0035, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0049, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0029, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0033, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0034, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0038, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0046, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0012, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0064, 1, 0 )			
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0057, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0020, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0062, 1, 0 )				
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0007, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0009, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0010, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0016, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0026, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0027, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0030, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0039, 1, 0 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0043, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0045, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0063, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0071, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0070, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0059, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0065, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0067, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0072, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0073, 1, 0 )
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getBalanceType()==null?0:mod200.getBalanceType().ordinal()+1, 1,0))                
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPygType()==null || mod200.getDoubleValue(Mod2002017Key.C0026)==1?0:mod200.getPygType().ordinal()+1, 1,0) )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0061, 1, 0)
				
				//,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getFiscalGroup(), 7))
				,(line,mod200, label) -> line.append( AonStringUtils.isEmpty(mod200.getFiscalGroup()) ? AonFiscalFileUtils.spaces(7) : AonFiscalFileUtils.unsigned(mod200.getFiscalGroup(), 7, 0) )
				
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getDominantDocument(), 9) ) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(),15) ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0041, 9, 2 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002017Key.C0042, 9, 2 ) 
				,(line,mod200, label) -> line.append( mod200.isComplementary()?"1":"0" )
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(AonNumberUtils.todouble(mod200.getComplementaryReceipt()),13,0))
	            ,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getSecretary().getName(),21)) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getSecretary().getDocument(),9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getSecretary().getIrnr())) 
				,(line,mod200, label) -> addLegalRepresentative(line, mod200, 0)
				,(line,mod200, label) -> addLegalRepresentative(line, mod200, 1)
				,(line,mod200, label) -> addLegalRepresentative(line, mod200, 2)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(21))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(20))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(50))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.zeros(9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.zeros(9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(50))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(13))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
				,(line,mod200, label) -> addEndLabel(line,label)
		 })

		,PAG02 ("T20002000", new IPropertyFiller[] {
			(line,mod200, label) -> {
				boolean isComplementary = false; // Indicador de pagina complementaria
				int a  = 0;  // Contador para Administradores
				int b1 = 0;  // Contador para Participaciones B1
				int b2 = 0;  // Contador para Participaciones B2 
				while ( !isComplementary ||
						a  < mod200.getAdministrators().size() || 
						b1 < mod200.getParticipationsOut().size() ||
						b2 < mod200.getParticipationsIn().size()) {
					addStartLabel(line,label);					 
					line.append(isComplementary?"C":" ");
					
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					
					addCompanyParticipationOut(line, mod200, b1++);
					addCompanyParticipationOut(line, mod200, b1++);
					addCompanyParticipationOut(line, mod200, b1++);
					
					addUnSignedKey(line, mod200, Mod2002017Key.P1501, DS, DD, isComplementary );
					addUnSignedKey(line, mod200, Mod2002017Key.P1502, DS, DD, isComplementary );
					addUnSignedKey(line, mod200, Mod2002017Key.P1503, DS, DD, isComplementary );
					addSignedKey(line, mod200, Mod2002017Key.P1504, isComplementary );
					addSignedKey(line, mod200, Mod2002017Key.P1505, isComplementary );
					addSignedKey(line, mod200, Mod2002017Key.P1506, isComplementary );
					addSignedKey(line, mod200, Mod2002017Key.P1507, isComplementary );
					addSignedKey(line, mod200, Mod2002017Key.P1508, isComplementary );
					line.append( AonFiscalFileUtils.spaces(68));  // Reservado para la AEAT
					
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					
					addUnSignedKey(line, mod200, Mod2002017Key.POR51, 5, 2, isComplementary );
					addUnSignedKey(line, mod200, Mod2002017Key.PORES, 5, 2, isComplementary );
					
					line.append( AonFiscalFileUtils.spaces(200));  // Reservado para la AEAT
					
					isComplementary = true;
					addEndLabel(line,label);
				}
			}
		})
				
		,PAG03 ("T20003000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA101)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA102)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA103)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA104)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA105)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA106)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA107)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA108)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA700)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA109)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA110)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA111)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA112)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA113)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA114)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA115)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA118)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA120)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA121)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA122)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA123)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA130)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA133)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA134)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA138)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA140)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA141)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA142)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA143)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA144)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA145)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA146)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA147)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA148)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA701)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)			
		})
		
		,PAG04 ("T20004000", new IPropertyFiller[] {  
             (line,mod200, label) -> addStartLabel(line,label)
            ,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA149)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA150)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA151)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA152)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA153)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA154)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA155)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA156)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA157)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA158)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA159)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA160)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA161)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA162)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA163)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA164)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA165)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA168)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA172)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA173)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA174)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA175)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA176)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA177)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA178)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA179)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BA180)				
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG05 ("T20005000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP185)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP186)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP187)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP188)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP189)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP190)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP191)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP192)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP193)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP702)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP1001)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP1002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP194)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP195)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP196)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP197)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP198)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP199)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP201)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP202)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP203)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP204)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP205)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP206)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP207)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP208)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP209)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP210)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP211)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP212)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP213)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP214)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP215)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP216)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP217)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP218)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP219)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP220)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP221)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP222)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP223)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP224)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP225)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP226)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP227)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG06 ("T20006000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
            ,(line,mod200, label) -> line.append(" ")			 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP228)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP229)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP230)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP703)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP704)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP232)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP233)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP234)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP235)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP236)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP237)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP238)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP239)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP240)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP241)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP242)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP243)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP244)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP245)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP246)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP247)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP248)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP249)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP250)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP251)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BP252)	
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
			})
				
		,PAG07 ("T20007000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG255)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG256)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG257)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG711)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG705)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG706)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG707)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG708)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG258)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG259)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG260)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG261)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG760)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG761)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG262)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG762)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG763)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG263)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG264)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG265)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG266)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG267)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG268)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG269)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG270)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG271)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG273)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG274)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG277)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG278)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG279)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG281)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG282)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG283)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG709)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG284)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG287)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG288)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG289)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG290)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG291)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG292)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG293)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG710)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG294)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG295)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG296)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
			})

		,PAG08 ("T20008000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
		    ,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG297)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG298)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG299)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG304)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG309)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG310)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG311)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG312)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG313)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG314)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG315)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG316)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG317)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG318)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG319)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG320)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG322)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG323)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG329)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG331)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG332) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG324)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG325)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG326)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG327)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG328)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.PG500)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG09 ("T20009000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0500)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0344)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0345)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0347)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0348)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0349)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0350)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0352)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0353)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0354)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.T0355)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG10 ("T20010000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ") 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC380)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC381)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC382)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC383)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC384)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC385)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC386)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC394)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC395)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC396)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC397)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC398)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC399)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC400)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC408)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC409)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC411)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC412)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC414)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC422)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC423)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC424)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC425)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC426)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC427)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC428)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC436)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC437)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC441)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC442)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC450)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC451)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC452)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC453)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC454)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC455)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC456)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC464)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC465)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC466)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC467)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC468)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC469)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC470)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC478)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC479)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC480)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC481)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC482)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC483)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC484)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC492)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC493)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC494)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC495)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC496)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC497)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC498)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC506)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC507)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC508)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC510)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC511)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC512)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC522)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC523)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC524)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC525)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC526)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC534)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC535)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC536)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC537)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC538)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC539)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC540)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC548)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC549)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC550)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC551)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC552)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC553)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC554)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC562)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC563)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC564)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC566)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC567)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC568)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC576)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC577)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC578)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC579)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC580)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC581)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC582)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC591)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC592)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC593)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC594)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC595)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC596)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC604)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC605)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC606)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC607)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC608)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC609)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC610)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC618)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC619)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC620)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC621)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC623)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC624)  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC715)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC716)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC717)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC718)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC719)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC720)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC721)  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC729)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC730)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC731)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC732)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC733)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC734)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC735)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC632)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC633)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC634)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC635)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC636)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC637)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC638)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
		    ,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG11 ("T20011000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC387)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC388)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC389)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC390)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC391)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC393)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC401)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC402)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC403)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC404)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC405)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC406)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC407)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC415)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC417)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC418)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC419)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC420)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC421)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC429)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC430)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC431)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC432)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC433)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC434)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC435)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC443)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC444)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC445)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC446)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC448)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC449)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC457)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC458)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC461)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC462)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC463)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC471)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC472)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC475)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC476)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC477)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC485)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC486)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC489)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC490)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC491)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC499)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC502)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC503)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC504)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC505)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC513)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC515)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC516)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC517)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC527)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC528)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC529)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC530)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC531)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC532)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC533)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC541)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC542)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC543)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC544)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC545)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC546)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC555)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC556)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC557)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC558)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC560)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC561)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC569)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC574)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC575)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC583)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC586)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC589)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC597)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC598)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC599)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC600)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC602)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC603)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC611)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC612)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC613)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC614)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC615)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC616)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC617)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC625)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC626)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC627)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC628)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC629)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC630)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC631)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC722)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC723)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC724)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC725)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC726)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC727)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC728)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC736)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC737)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC738)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC739)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC740)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC741)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC742)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC639)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC640)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC641)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC642)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC643)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC644)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TC645)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG12 ("T20012000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ500)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ501)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1230)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0355)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0356)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0357)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0358)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0359)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0360)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0225)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0226)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0272)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0361)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0362)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0304)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0505)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1005)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1006)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1003)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1004)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0309)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0310)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0516)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0551)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0322)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0415)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0211)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0331)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0325)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0326)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0394)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0334)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0327)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0328)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0543)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0335)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0368)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0508)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1009)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1010)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1807)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1808)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0363)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0364)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0345)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0371)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0347)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0348)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1011)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1012)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1013)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1014)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1015)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1016)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0369)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0370)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0256)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0278)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0372)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0373)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0374)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1589)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0375)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0376)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1320)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0184)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0544)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1022)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1023)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1018)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1019)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0377)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0378)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT			
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG13 ("T20013000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0379)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0380)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0381)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0382)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0383)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0384)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0387)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0388)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0311)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0312)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0313)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0314)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0323)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0324)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0317)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0318)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0385)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0386)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0389)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0390)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0396)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0397) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0398) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0250)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0251)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0391)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0400)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0403)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0404)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0510)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0512)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0329)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0365)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1026)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0409)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0411)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0412)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I1027)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D1028)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0414)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.I0417)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.D0418)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ578)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ579)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1029)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1030)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1031)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ550)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ552)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1033)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ553)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ554)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ555)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ556)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ559)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ545)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ593)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1510)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG14 ("T20014000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.LQ558, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ560)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ210)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ480)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ408)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1037)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ561)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1331)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ562)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1038)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN567)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN568)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN563)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN566)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN576)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN569)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1344)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN573)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN575)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN577)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN581)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN582)    
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN583) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1039)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN399)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN592)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG14B("T20014B00", new IPropertyFiller[] {
			(line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1785)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1786)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1787)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1788)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1789)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1790)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1791)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1792)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1793)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1794)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1795)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1796)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN597)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1797)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1798)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1799)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN599)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN600)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN601)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN602)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN603)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN604)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN605)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN606)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN611)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN612)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN615)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN616)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN633)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN642)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN617)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN618)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN619)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN620)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1234A)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN083)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1042)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN621)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM150)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1020)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1043)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM506)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1021)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1044)
		})
		
		,PAG15 ("T20015000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ640)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ641)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ548)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ643)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ644)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ645)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ646)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ647)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ648)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ649)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ650)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ651)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ652)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ653)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ654)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ655)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ656)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ657)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ658)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ659)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ660)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ661)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ662)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ663)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ664)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ665)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ666)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ667)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ668)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ669)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ743)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ747)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ748)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ277)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ608)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ609)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ610)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ704)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ705)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ706)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ013)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ014)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ015)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ725)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ726)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ727)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ534)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ535)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ536)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ607)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ675)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ699)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1045)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1046)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1047)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1592)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1593)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1594)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ670)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ671)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1048)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1049)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN104)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN105, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN846)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN847)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN848)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN106)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN107, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN282)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN283)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN284)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN108)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN109, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN702)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN703)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN707)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN110)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN111, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN071)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN187)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN112)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN113, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN025)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN026)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN027)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN114)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN115, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN714)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN715)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN716)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN735)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN920, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN736)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN737)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN738)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN118)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN103A, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN101)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN102, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN120)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN121)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN122)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN123, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1595)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN1596, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1597)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1598)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1599)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1344)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1345)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN103B, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1347)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})

		,PAG16 ("T20016000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN153)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN728, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN637)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN638)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN639)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN154)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN729, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN849)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN894)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN197)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN155)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN730, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN287)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN156)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN731, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN825)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN826)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN827)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN157)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN732, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN001)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN003)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN158)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN733, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN028)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN029)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN030)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN159)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN734, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN717)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN718)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN719)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN720)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN721, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN722)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN723)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN724)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN739)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN921, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN740)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN741)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN742)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN134)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN926, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN160)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN161)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN162)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN103C, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1054)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN1050, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1051)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1052)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1053)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1348)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN1349, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1350)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1352)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1770)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN1771, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1772)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1773)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1774)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN133)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.BN103D, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN163)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN165)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN573)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN174)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN835)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN836)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN838)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN839)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN840)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN932)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN933)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN934)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN297)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN298)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN299)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN090)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN091)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN092)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN004)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN005)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN006)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN031)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN033)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN022)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN023)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN024)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN042)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN138)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN140)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN141)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN142)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN143)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN188)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN189)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN190)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN803)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN804)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN805)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1055)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1056)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1057)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN700)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN708)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN709)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1353)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1354)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1355)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1775)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1776)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1777)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN841)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN843)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG16B ("T20016B00", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN749)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN750)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN752)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN753)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN754)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN755)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN756)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN757)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN758)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN759)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN760)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN761)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN762)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN763)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN744)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN745)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN746)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN779)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN783)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN784)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN764)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN765)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN854)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN855)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1356)		
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN857)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN858)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN859)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN860)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN861)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN862)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN863)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN864)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN865)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN883)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN884)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN885)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN785)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN789)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN790)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1357)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1358)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1359)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1778)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1779)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1780)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN852)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN853)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN856)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN852)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN853)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN856)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN194)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN195)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN868)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN869)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN834)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN871) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN872)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN873)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN874)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN875)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN876)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN877)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN878)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN879)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN880)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN881)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN882)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN866)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN867)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN870)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN939)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN940)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN941)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN191)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN192)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN193)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN613)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN614)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN701)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN257)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN011)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN037)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN038)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN039)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN044)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN045)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN046)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN528)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN529)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN530)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN144)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN145)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN146)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN147)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN148)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN149)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN240)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN241)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN242)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1058)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1059)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1060)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN791)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN802)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN806)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1781)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1782)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1783)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN886)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN887)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG17 ("T20017000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN774)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN775)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN780)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN781)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN782)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN786)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN787)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN788)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN766)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN767)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN833)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN198)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN896)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN897)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN288)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN289)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN290)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN466)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN467)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN468)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN061)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN498)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN586)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN472)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN473)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN478)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN180)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN181)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN182)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN531)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN532)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN533)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN945)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN946)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN947)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN960)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN961)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN962)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN183)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN185)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN186)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN966)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN967)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN968)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN457)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN458)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN459)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN460)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN461)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN462)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1063)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1064)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1065)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1066)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1067)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1068)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1069)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1070)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1071)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN813)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN814)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN815)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN986)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN810)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN507)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN557)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN591)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN594)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1614)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1615)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1616)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1617)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1618)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1619)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1620)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1621)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1360)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1361)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1362)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1363)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1364)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1365)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1366)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1367)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1368)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN798)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN799)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN800)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN096)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN698)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN713)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN807)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN808)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN809)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1075)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1076)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1077)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN963)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN964)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN965)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN931)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN502)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN751)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN795)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN796)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN797)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN549)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN888)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN889)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1369)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1370)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1371)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1081)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1083)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1084)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1085)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1086)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1087)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1088)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1089)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1090)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1091)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1092)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1093)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1094)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1095)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1096)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1097)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1098)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1099)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1100)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1101)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1102)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1103)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1104)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1105)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1106)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1107)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1114)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1115)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1118)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1372)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1373)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1374)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1375)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1376)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1377)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1378)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1379)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1380)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1381)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1382)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1383)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1384)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1385)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1386)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1387)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1388)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1389)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1390)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1391)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1393)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1394)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1395)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1396)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1397)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1398)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1399)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1400)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1401)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1402)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1403)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1404)
   			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG18 ("T20018000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1405)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1406)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1407)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1408)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1409)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1411)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1412)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1414)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1415)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1417)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1418)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1419)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1420)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1421)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1422)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1423)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1424)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1425)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1623)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1624)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1625)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1626)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1627)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1628)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1629)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1630)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1631)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1632)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1633)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1634)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1635)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1636)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1637)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1638)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1639)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1640)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1641)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1642)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1643)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1644)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1645)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1646)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1647)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1648)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1649)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1650)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1651)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1652)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1653)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1654)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1655)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1656)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1657)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1658)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1659)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1660)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1661)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1662)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1663)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1664)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1665)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1666)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1667)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1668)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1669)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1670)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1671)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1672)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1673)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1674)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1675)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1676)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1677)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1678)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1679)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1680)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1681)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1682)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1689)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1690)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1691)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1692)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1693)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1694)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1695)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1696)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1697)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1698)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1699)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1700)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1701)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1702)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1703)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1704)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1705)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1706)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1707)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1708)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1709)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1800)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1801)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1802)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1683)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1684)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1685)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN634)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN635)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN636)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN828)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN829)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN830)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN831)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN832)
			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN918)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN919)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN574)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN580)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN589)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN976)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN977)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN978)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN822)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN823)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN824)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN232)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN233)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN850)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN851)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1123)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1130)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1426)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1427)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1428)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1429)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1430)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1431)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1432)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1433)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1710)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1711)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1712)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1713)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1714)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1715)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1716)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1717)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN517)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN081)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1234B)

   			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})
		
		,PAG18B("T20018B00", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN294)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN295)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN066)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN074)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN084)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN008)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN009)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN010)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN035)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN036)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN201)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN202)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN203)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN904)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN905)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN906)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN990)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN991)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN992)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN997)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN998)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN999)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN246)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN247)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN248)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN993)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN994)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN995)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1434)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1435)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1436)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1718)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1719)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1720)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN598)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN895)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN974)
			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1437)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1441)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1442)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1443)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1444)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1445)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1721)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1722)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1723)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1724)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1173)
			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1178)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1179)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1446)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1181)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1447)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1448)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1449)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1450)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1451)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1452)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1453)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1454)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1725)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1726)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1727)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1728)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1182)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1183)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN1185)
		
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1141)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1142)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1143)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1144)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1145)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1146)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1455)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1456)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1457)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1730)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1731)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1147)
			//,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1148)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1149)
				
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1150)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1151)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1152)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1153)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1154)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1155)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1156)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1157)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1458)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1459)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1460)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1461)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1732)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1733)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1734)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1735)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1158)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1159)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1160)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1161)
				
   			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})
		
		,PAG19 ("T20019000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID650)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID651)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID652)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID653)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID654)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID1270)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID1271)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID1522)				
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID655)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID656)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID658)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID659)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID660)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID662)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID664)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID665)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.ID666)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC001)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC003)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC004)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC005)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC006)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC007)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC008)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC009)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC010)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC011)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC012)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC013)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC014)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC015)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC016)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC017)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC018)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC019)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC020)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC021)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC022)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC023)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC024)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC025)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC026)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC027)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC028)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC029)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC030)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC031)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC033)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC035)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC036)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC037)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC038)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC039)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC042)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC043)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC044)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC045)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC046)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC047)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC048)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC049)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC050)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC051)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC052)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC053)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.DC054)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getNrsAnexoIII(), 22))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getNrsAnexoIV(), 22))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getNrsAnexoV(), 22))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getJustCanarias(), 13))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getJustActivos(), 13))
 			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT				
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG20 ("T20020000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1240)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1241)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1242)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1243)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1244)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1245)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1246)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1247)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1248)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1249)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1250)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1251)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1252)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1253)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1254)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1255)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1256)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1257)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1258)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1259)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1260)
			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1188)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1189)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1191)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1193)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1194)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1196)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1198)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1199)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1201)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1202)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1203)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1204)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1205)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1206)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1462)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1463)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1209)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1210)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1211)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1736)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1737)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1464)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1465)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1466)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1738)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1739)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1740)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1212)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1213)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1214)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1215)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1216)
			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM890)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM891)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM503)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM522)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM523)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM273)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM274)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM537)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM955)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM956)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM957)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1217)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1218)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1219)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1467)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1468)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1469)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1741)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1742)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1743)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM538)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM539)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM546)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG20B("T20020B00", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1134)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1470)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1471)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1472)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1744)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1745)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1746)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1140)

			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1473)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1408)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1474)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1475)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1476)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1409)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1477)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1478)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1481)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1482)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1483)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1484)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1485)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1486)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1487)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1488)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1489)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1490)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1491)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1747)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1748)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1492)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1493)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1749)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1750)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1751)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1752)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1494)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1495)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1496)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1497)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1498)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1499)
			
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.NUMPER,1,0)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1515)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1516)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1585)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1517)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
			
		})

		,PAG20T("T20020C00", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")				
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1524)         
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1525) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1526)         
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1527)         
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1528)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1529) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1530) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1590) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1591) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1531) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1532) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1533) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1534)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1535)         
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1536) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1537) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1538) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1539) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1540) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1541)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1542) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1543) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1544) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1545) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1546) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1547) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1548) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1549) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1550) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1551)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1552) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1553) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1554) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1555) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1556) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1753)        
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1557) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1558) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1559) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1560)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1754)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1755)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1756)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1757)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1758)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1759)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1760)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1761)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1762)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1561) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1562) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1563) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1564) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1565) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1566) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1567) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1568) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1569) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM393)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM150)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM506)
			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1573)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1574)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1575)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1576)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1577)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1578)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1763)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1764)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1765)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1766)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1579)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1580) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1581) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LM1582)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)				
				
		})
		
//		,PAG21 ("T20021000", new IPropertyFiller[] {
//
//				(line,mod200, label) -> {
//					    
//					    addStartLabel(line,label);  // Etiqueta de inicio de pagina
//					    
//					    // Esta página puede tener complementarias en los apartados que ahora no están en el modelo
//					    // Si se añaden dichos apartados hay que tenerlo en cuenta, para hacerlo de forma similar a la pagina 2
//					    boolean isComplementary = false;
//						line.append(isComplementary?"C":" ");  // Indicador de pagina complementaria
//					
//						// [...] NO ESTA EN EL MODELO - Comunicación del importe neto de la cifra de negocios
//						line.append(AonFiscalFileUtils.zeros(DS)); // Grupos de sociedades. Importe neto cifra negocios [987] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [1]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [2]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [3]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [4]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [5]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [6]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [7]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [8]
//						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [9]
//						line.append(AonFiscalFileUtils.zeros(DS)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. Importe neto [988] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
//						line.append(AonFiscalFileUtils.zeros(3)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. Nº establecimientos (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
//						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [1]
//						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [2]
//						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [3]
//						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [4]
//						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [5]
//						line.append(AonFiscalFileUtils.zeros(DS)); // Comunicación importe neto cifra negocios - Entidades de crédito, aseguradoras, I.I.C. y sociedades de garantíarecíproca - Importe neto [989] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
//						
//						// Las siguientes 5 casillas solo van con importes en la primera pagina, en las complementarias van a cero
//						addUnSignedKey(line, mod200, Mod2002017Key.LQ0N1, 4, 0, isComplementary); // Rég. Entidades navieras en función del tonelaje. Nº de buques  [N1]
//						addSignedKey(line, mod200, Mod2002017Key.LQ630,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de  aplicar la escala [630]
//						addSignedKey(line, mod200, Mod2002017Key.LQ631,isComplementary); // Rég. Entidades navieras en función del tonelaje. Importe rentas generadas en trasmisiones de buques [631]
//						addSignedKey(line, mod200, Mod2002017Key.LQ632,isComplementary); // Rég. Entidades navieras en función del tonelaje. Compensación bases imponibles negativas períodos anteriores [632]
//						addSignedKey(line, mod200, Mod2002017Key.LQ579,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de la aplicación del régimen [579]
//						
//						line.append(AonFiscalFileUtils.spaces(200));  // Reservado para la AEAT
//						
//						addEndLabel(line,label); // Etiqueta fin de pagina
//				}
//		})
		
		,PAG21 ("T20021000", new IPropertyFiller[] {
				(line,mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int i1 = 0;  // Contador para NIF entidades del grupo
					int i2 = 0;  // Contador para NIF establecimientos permanentes					 
					while ( !isComplementary ||
							i1 < mod200.getGroupEntities().size() ||							
							i2 < mod200.getEstablishments().size()) {
						addStartLabel(line,label);					 
						line.append(isComplementary?"C":" ");
						
						addSignedKey(line, mod200, Mod2002017Key.CN987, isComplementary); 
						
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						
						addSignedKey(line, mod200, Mod2002017Key.CN988, isComplementary);
						addUnSignedKey(line, mod200, Mod2002017Key.CNEST, 3, 0, isComplementary);
						
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						
						addSignedKey(line, mod200, Mod2002017Key.CN989, isComplementary);
						
						addUnSignedKey(line, mod200, Mod2002017Key.LQ0N1, 4, 0, isComplementary); // Rég. Entidades navieras en función del tonelaje. Nº de buques  [N1]
						addSignedKey(line, mod200, Mod2002017Key.LQ630,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de  aplicar la escala [630]
						addSignedKey(line, mod200, Mod2002017Key.LQ631,isComplementary); // Rég. Entidades navieras en función del tonelaje. Importe rentas generadas en trasmisiones de buques [631]
						addSignedKey(line, mod200, Mod2002017Key.LQ632,isComplementary); // Rég. Entidades navieras en función del tonelaje. Compensación bases imponibles negativas períodos anteriores [632]
						addSignedKey(line, mod200, Mod2002017Key.LQ579,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de la aplicación del régimen [579]
						
						line.append( AonFiscalFileUtils.spaces(200));  // Reservado para la AEAT
						
						isComplementary = true;
						addEndLabel(line,label);
					}
				}
			})
		
		,PAG22 ("T20022000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")				
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC089)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC094)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC095)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC097)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC098)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC047)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC048)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC524)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC525)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC526)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC527)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC922)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC923)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC924)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC925)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC927)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC928)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC938)
    		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC996)
	        ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC020)
	        ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.RC021)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C1) // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados cooperativos [C1]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E1) // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados extracooperativos [E1]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C2) // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados cooperativos [C2]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E2) // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados extracooperativos [E2]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C3) // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados cooperativos [C3]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E3) // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados extracooperativos [E3]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C4) // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados cooperativos [C4]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E4) // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados extracooperativos [E4]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E5) // Rég. cooperativas - Determ. base imponible - Incrementos y disminuciones patrimoniales - Resultados extracooperativos [E5]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C6) // Rég. cooperativas - Determ. base imponible - resultado - Resultados cooperativos [C6]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E6) // Rég. cooperativas - Determ. base imponible - resultado - Resultados extracooperativos [E6]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C7) // Rég. cooperativas - Determ. base imponible - aumentos - Resultados cooperativos [C7]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E7) // Rég. cooperativas - Determ. base imponible - aumentos - Resultados extracooperativos [E7]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C8) // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados cooperativos [C8]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E8) // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados extracooperativos [E8]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0C9) // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados cooperativos [C9]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CP0E9) // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados extracooperativos [E9]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CPC10) // Rég. cooperativas - Determ. base imponible - Reserva inversiones Canarias - Resultados cooperativos [C10]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CPC11) // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados cooperativos [C11]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CPE11) // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados extracooperativos [E11]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CPC12) // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados cooperativos [553]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.CPE12) // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados extracooperativos [554]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ673 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ674 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1224)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ676 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ677 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ678 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ679 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ680 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ681 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ682 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ683 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ684 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ685 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ686 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ687 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ688 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ689 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ690 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ691 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ692 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ693 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ623 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ624 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ672 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ279 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ280 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ281 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ587 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ515 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ900 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ059 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ099 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ100 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ017 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ018 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ019 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ772 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ773 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ777 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ907 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ908 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ909 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ910 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ911 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ912 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ935 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ936 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ937 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1511 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1512 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1513 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1767 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1768 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1769 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ694 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ561 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ695 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1225)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ1226)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
				
		})

		,PAG23 ("T20023000", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")
				,(line,mod200, label) -> {
					
					// [...] FALTA - NO ESTA EN EL MODELO - Operaciones fusión, escisión, canje de valores.
					for (int i=0;i<5;i++) {
						line.append(AonFiscalFileUtils.spaces( 1)); 
						line.append(AonFiscalFileUtils.spaces( 9)); 
						line.append(AonFiscalFileUtils.spaces(40)); 
						line.append(AonFiscalFileUtils.spaces( 9)); 
						line.append(AonFiscalFileUtils.spaces(40)); 
						line.append(AonFiscalFileUtils.zeros ( 8));
						line.append(AonFiscalFileUtils.zeros (DS));
						line.append(AonFiscalFileUtils.zeros (DS));
						line.append(AonFiscalFileUtils.zeros (DS));
					}
					line.append(AonFiscalFileUtils.spaces(200));  // Reservado para la AEAT
					
				}				
				,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG24 ("T20024000", new IPropertyFiller[] {
				(line,mod200, label) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int i1 = 0;  // Contador para Deducción para Evitar la doble imposición
					int i2 = 0;  // Contador para Relación de Socios
					int i3 = 0;  // Contador para Información de detalle de EP o UTE 
					while ( !isComplementary ||
							i1 < mod200.getUteBases().size() || 
							i2 < mod200.getUteParticipations().size() ||
							i3 < mod200.getUteForeign().size()) {
						addStartLabel(line,label);					 
						line.append(isComplementary?"C":" ");
						
						addUnSignedKey(line, mod200, Mod2002017Key.UT060, 7, 4, isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UT500, isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UT1227, isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UT1228, isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UT552, isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UT1330, isComplementary );
												
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						
						addSignedKey(line, mod200, Mod2002017Key.UTC01 , isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UTC02 , isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UTC03 , isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UT062 , isComplementary );						
						addSignedKey(line, mod200, Mod2002017Key.UTC04 , isComplementary );
						addSignedKey(line, mod200, Mod2002017Key.UTC05 , isComplementary );
						
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
						addUteForeign(line, mod200, i3++);
						
						line.append( AonFiscalFileUtils.spaces(200));  // Reservado para la AEAT
						
						isComplementary = true;
						addEndLabel(line,label);
					}
				}
			})
		
		// [...] FALTA - NO ESTA EN EL MODELO - Página 25: Régimen especial de transparencia fiscal internacional
		
		,PAG26 ("T20026000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR050) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR051) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR052) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR053) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR054) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR055) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR056) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.TR626, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.TR627, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.TR628, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.TR629, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002017Key.TR625, 5, 2) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR420) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR421) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR426) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR427) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR600) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR402) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR442) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR443) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR444) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR602) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR445) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR446) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR447) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR448) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR604) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR449) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR450) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR451) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR465) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR606) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR474) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR475) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR476) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR477) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR612) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR482) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR483) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR484) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR485) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR616) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR913) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR914) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR915) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR916) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR642) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR486) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR487) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR488) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR489) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR618) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR490) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR491) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR492) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR493) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR620) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1334)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1335)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR494) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR495) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR496) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR497) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1043)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.TR1044)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})	

		// [...] NO ESTAN EN EL MODELO - Páginas 27 a 54

		,DID ("T200DID00", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)   // Etiqueta de inicio de pagina
				,(line,mod200, label) -> line.append(" ")  			 // Indicador de pagina complementaria
				,(line,mod200, label) -> line.append("0")            // Cuenta corriente tributaria "0" o "1" (no se usa)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getYear(), 4, 0) )      // Identificación - Ejercicio
		        ,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPeriodType(),1,0) )  //	Tipo de ejercicio
		        ,(line,mod200, label) -> line.append( "0A" )                                                     // Período Impositivo "0A"
				,(line,mod200, label) -> line.append( mod200.getPeriodStart() == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodStart()) ) // Período Impositivo Inicio (ddmmaa)
				,(line,mod200, label) -> line.append( mod200.getPeriodEnd()   == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodEnd()) )   // Período Impositivo Fin (ddmmaa)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseDocument(),9) )  // Identificación - NIF 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseName(),80) )     // Identificación - Apellidos y nombre o Razón Social
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ552)   // Liquidación - Base imponible [552]                                    
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.LQ562)   // Liquidación - Cuota íntegra [562]                          
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002017Key.BN621)   // Liquidación - Líquido a ingresar o a devolver Estado [621]
				
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> {
						double importe = mod200.getDoubleValue(Mod2002017Key.BN621); // importe a ingresar o a devolver
						
						line.append( AonFiscalFileUtils.text(importe<0 ? ("V".equals(mod200.getDevType())?"":mod200.getDevType()) : "",1) ); // Devolución - Renuncia o por Transferencia "blanco" "R","D"				
						line.append( AonFiscalFileUtils.signedZero(importe<0 ? Math.abs(importe) : 0.0, DS, DD) );     // Devolución - Importe a devolver
						line.append( AonFiscalFileUtils.text(importe<0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "",34) );  // Devolución - Número de cuenta IBAN (si devolución por transferencia)
						line.append( AonFiscalFileUtils.text(importe<0 && "D".equals(mod200.getDevType()) ? mod200.getBic() : "",11)); // Devolución - Código SWIFT-BIC
						line.append( AonFiscalFileUtils.text(importe>0 ? mod200.getPayType() : "",1) );  // Ingreso - Modalidad de ingreso. Uno de los siguientes valores	"blanco", "I" Adeudo en	cuenta, "H" Efectivo, "U" Domiciliación
						line.append(" ");   // RESERVADO AEAT
						line.append(" ");   // RESERVADO AEAT
						line.append( AonFiscalFileUtils.signedZero(importe>0 ? importe : 0.0, DS, DD) );               // Ingreso - Importe a ingresar
						line.append( AonFiscalFileUtils.text(importe>0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType())) ? mod200.getIban() : "",34) ); // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)			
		
						addSignedKey(line, mod200, Mod2002017Key.LM150);  // Abono/Compensación - Abono por conversión de activos impuesto diferido - A       
						addSignedKey(line, mod200, Mod2002017Key.LM506);  // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C
						
						line.append(importe == 0 ? "1" : "0"); // Cuota Cero "0" o "1"
					}
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
				,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
					
				})
		;
		 
		private String tag;
		private IPropertyFiller[] propertyFillers;
		
		private Pages2017(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002017 mod200, Writer line) throws IOException {
			
			// Controles determinadas páginas que solo se ponen si están marcados ciertos caracteres
			boolean addPage = true;
			
			// Página 7.
			// Página 8. La cuenta de pérdidas y ganancias no debe aparecer si se ha marcado el caracter 26 (entidad inactiva).
//			if (this == Pages2017.PAG07 || this == Pages2017.PAG08) {
//				addPage = (mod200.getDoubleValue(Mod2002017Key.C0026)==0);
//			}
			
			// Página 9. Estado de Ingresos y Gastos Reconocidos. Solo si Balance Normal o Abreviado
			if (this == Pages2017.PAG09) {  
				addPage = (mod200.getBalanceType() == BalanceType.NORMAL) ||
				          (mod200.getBalanceType() == BalanceType.ABREVIADO);					
			}
			
			// Página 22. Regimen especial de la reserva para inversiones en Canarias y Cooperativas
			if (this == Pages2017.PAG22) {
				addPage = (mod200.getDoubleValue(Mod2002017Key.C0029)==1) ||
			              (mod200.getDoubleValue(Mod2002017Key.C0017)==1) ||
			              (mod200.getDoubleValue(Mod2002017Key.C0018)==1) ||
			              (mod200.getDoubleValue(Mod2002017Key.C0019)==1);
			}
			
			// Página 24. Agrupaciones de interes económico y UTES (regimen especial). Caracteres 013 o 014 marcados
			if (this == Pages2017.PAG24) {
				addPage = (mod200.isChecked( Mod2002017Key.C0013) 
					    || mod200.isChecked( Mod2002017Key.C0014));			              
			}
			
			// Página 26. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2017.PAG26) {
				addPage = (mod200.getDoubleValue(Mod2002017Key.C0028)==1);
			}
			
			// Añadir el contenido de la página
			if (addPage) {
	 			for (IPropertyFiller propertyFiller : this.propertyFillers) {
	 				propertyFiller.propertyFill(line, mod200, this.tag);
				}     		
			}
		}
	}
	
	public static void fillWriter(Mod2002017 mod200, Writer line) throws IOException {
		
		// FALTA - Hay una incoherencia en el formato del fichero, la etiqueta la pone
		// de longitud 17, pero la etiqueta que especifica solo tiene 16, el año pasado
		// llevaba un cero detras del año y este año no lo lleva, pero no han actualizado
		// la longitud del campo
		//line.append("<T2000"+mod200.getYear()+"0A0000>");  // Etiqueta inicio de fichero
		line.append("<T2000"+mod200.getYear()+"0A0000>");  // Etiqueta inicio de fichero
		for (Pages2017 page : Pages2017.values()) {     			
			page.fillPage(mod200, line);			
		}
		// FALTA - IDEM etiqueta de inicio
		//line.append("</T2000"+mod200.getYear()+"0A0000>");  // Etiqueta fin de fichero
		line.append("</T2000"+mod200.getYear()+"0A0000>");  // Etiqueta fin de fichero
		line.append(AonStringUtils.CR_LF); // Fin de registro. Constante CRLF
		line.close(); 
		
	}
	
	private static void setDoubleValue2017(Mod2002017 mod200, Mod2002017Key key, double value) {	
		 
		DoubleVariable2017 t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariable2017(key);
			t.setValue(value);
			mod200.addVariable(t);		
		}
		else {
			t.setValue(value);
		}
	}

	public static void main(String argv[]) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
		
		// Prueba para ver si la longitud de todas las páginas es correcta
		try {
			
			// Creamos un nuevo modelo 200 2017
			Mod2002017 mod200 = new Mod2002017();
			
			// Inicializamos las casillas con un valor aleatorio			
			for (Mod2002017Key key : Mod2002017Key.values()) {
				double value = AonNumberUtils.todouble( AonRandomStringUtils.randomNumeric(4));
				value = (AonMathUtils.isZero(value))?value:AonMathUtils.round( value  / 100 );
				setDoubleValue2017(mod200, key, value);						
			}
			
			// Periodo inicio y fin 
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			mod200.setPeriodStart( sdf.parse("01-01-2017") );
			mod200.setPeriodEnd( sdf.parse("31-12-2017") );
			mod200.setYear(2017);
			
			// Caracteres todos a cero excepto 9, 13, 28 y 29 para que salgan todas las páginas
			for (Mod2002017Key key : Mod2002017Character.CHARACTERS_KEYS) {				
				setDoubleValue2017(mod200, key, 0.0);						
			}
			setDoubleValue2017(mod200, Mod2002017Key.C0009, 1.0);
			setDoubleValue2017(mod200, Mod2002017Key.C0028, 1.0);
			setDoubleValue2017(mod200, Mod2002017Key.C0029, 1.0);
			setDoubleValue2017(mod200, Mod2002017Key.C0013, 1.0);
			
			// Estados de cuentas de IIC
			setDoubleValue2017(mod200, Mod2002017Key.C0061, 0.0);
			
			// Numero de periodo impositivo
			setDoubleValue2017(mod200, Mod2002017Key.NUMPER, 0.0);
			
			// Balance y Cuenta PyG Normal
			mod200.setBalanceType(BalanceType.NORMAL);
			mod200.setPygType(BalanceType.NORMAL);
			
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
			String filename = "c:\\tmp\\prueba_M200_2017.txt";
			BufferedWriter line = new BufferedWriter(new FileWriter(filename));
			fillWriter(mod200, line);
			
			// Mostramos la longitud de cada pagina 
			FileInputStream input = new FileInputStream(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();							
			Document doc = db.parse(new InputSource(new InputStreamReader(input,"ISO-8859-1")));
			
			// Longitudes de todas las páginas
			HashMap<String, Integer> m = new HashMap<String, Integer>();
			m.put(Pages2017.PAG00.tag, 311);
			m.put(Pages2017.PAG01.tag, 884);
			m.put(Pages2017.PAG02.tag, 2094);
			m.put(Pages2017.PAG03.tag, 1074);
			m.put(Pages2017.PAG04.tag, 768);
			m.put(Pages2017.PAG05.tag, 1006);
			m.put(Pages2017.PAG06.tag, 683);
			m.put(Pages2017.PAG07.tag, 1091);
			m.put(Pages2017.PAG08.tag, 853);
			m.put(Pages2017.PAG09.tag, 581);
			m.put(Pages2017.PAG10.tag, 2723);
			m.put(Pages2017.PAG11.tag, 2502);
			m.put(Pages2017.PAG12.tag, 2026);
			m.put(Pages2017.PAG13.tag, 1503);
			m.put(Pages2017.PAG14.tag, 1329);
			m.put(Pages2017.PAG14B.tag, 1329); //sdf
			m.put(Pages2017.PAG15.tag, 3024);
			m.put(Pages2017.PAG16.tag, 1854);
			m.put(Pages2017.PAG16B.tag, 1686);
			m.put(Pages2017.PAG17.tag, 3369);
			m.put(Pages2017.PAG18.tag, 2808);
			m.put(Pages2017.PAG19.tag, 1999);
			m.put(Pages2017.PAG20.tag, 1397);
			m.put(Pages2017.PAG20B.tag, 939);
			m.put(Pages2017.PAG20T.tag, 1312);
			m.put(Pages2017.PAG21.tag, 476);
			m.put(Pages2017.PAG22.tag, 1907);
			m.put(Pages2017.PAG23.tag, 1014);
			m.put(Pages2017.PAG24.tag, 2288);
			//m.put(Pages2017.PAG25.tag, 3685);
			m.put(Pages2017.PAG26.tag, 1558);
			m.put(Pages2017.DID.tag, 604);
			
			for ( Pages2017 page : Pages2017.values()) {
				NodeList nodeList = doc.getElementsByTagName(page.tag);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					String lin = "<" + page.tag + ">" + node.getTextContent() + "</" + page.tag + ">";
					System.out.println( AonStringUtils.rightPad(node.getNodeName(),9)+" - "+
							AonStringUtils.leftPad(AonNumberUtils.toString(lin.length()),4)+ " - "+ 
							AonStringUtils.leftPad(m.get(page.tag).toString(),4)
							+ (lin.length()!=m.get(page.tag)?" **":"") );
				}
			}
			
			System.out.println("");
			System.out.println("***** Fin Fichero : "+filename);
			System.out.println("");
				        
		} 
        catch (Exception e) {
			e.printStackTrace();			
		}
		finally {
			System.exit(0);
		}
	}
	
}
