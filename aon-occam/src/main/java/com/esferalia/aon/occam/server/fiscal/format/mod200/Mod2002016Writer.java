package com.esferalia.aon.occam.server.fiscal.format.mod200;

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

import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Character;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002016Writer {
	
	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************
	
	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static int DS = 17;  // Tamaño de digitos por defecto para los importes 
	private static int DD =  2;  // Decimales por defecto para los importes
	
	// Añade un importe de una casilla del modelo (con signo, longitud y decimales por defecto, relleno con ceros por la izquierda) 
	private static void addSignedKey(Writer line, Mod2002016 mod200, Mod2002016Key key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DS, DD ));
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002016 mod200, Mod2002016Key key, boolean isComplementary) throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DS, DD ));
		}
	}
	
	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por la izquierda) 
	private static void addUnSignedKey(Writer line, Mod2002016 mod200, Mod2002016Key key, int size, int dec) throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key) , size, dec ));
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002016 mod200, Mod2002016Key key, int size, int dec, boolean isComplementary) throws IOException {
		
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
	private static void addLegalRepresentative(Writer line, Mod2002016 mod200, int index) throws IOException {
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
	private static void addCompanyAdministrator(Writer line, Mod2002016 mod200, int index) throws IOException {
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
            if (mod200.getDoubleValue(Mod2002016Key.C0021)==1)
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
	private static void addCompanyParticipationOut(Writer line, Mod2002016 mod200, int index) throws IOException {
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
	private static void addCompanyParticipationIn(Writer line, Mod2002016 mod200, int index) throws IOException {
		String document = "";
		String rpte = "0";
		String fj = "";
		String name = "";
		String province = "";
		double nominalValue = 0;
		double percent = 0;
		if (index < mod200.getParticipationsIn().size()) {
			document = mod200.getParticipationsIn().get(index).getDocument();   
			rpte = mod200.getParticipationsIn().get(index).getRepresenStr();
			fj = mod200.getParticipationsIn().get(index).getEntity();
			name = mod200.getParticipationsIn().get(index).getName();
			province = getProvinceCountry(mod200.getParticipationsIn().get(index).getProvince(),mod200.getParticipationsIn().get(index).getCountry());   
			nominalValue = mod200.getParticipationsIn().get(index).getNominalValue();
			percent = mod200.getParticipationsIn().get(index).getPercent();
		}
		line.append( AonFiscalFileUtils.text(document, 15));
		line.append( AonFiscalFileUtils.text(rpte,      1));
		line.append( AonFiscalFileUtils.text(fj,        1));					                                                      
		line.append( AonFiscalFileUtils.text(name,     37)); 
		line.append( AonFiscalFileUtils.text(province,  2));
		line.append( AonFiscalFileUtils.signedZero(nominalValue, DS, DD) ); 
		line.append( AonFiscalFileUtils.unsigned(percent, 5,2));
	}
	
	// UTES - Deducción para evitar la doble imposicion
	private static void addUteBase(Writer line, Mod2002016 mod200, int index) throws IOException {			
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
	private static void addUteParticipation(Writer line, Mod2002016 mod200, int index) throws IOException {
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
			//rx = mod200.getDoubleValue(Mod2002016Key.C0014)==1?mod200.getUteParticipations().get(index).get
			
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
	private static void addUteForeign(Writer line, Mod2002016 mod200, int index) throws IOException {
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
		public void propertyFill(Writer line, Mod2002016 mod200, String tag) throws IOException;
	}
    
	private enum Pages2016 {
		
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
				
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0001, 1, 0 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0002, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0003, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0004, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0005, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0011, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0013, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0014, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0017, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0018, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0019, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0021, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0023, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0024, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0025, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0031, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0032, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0036, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0048, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0058, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0060, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0066, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0006, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0015, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0022, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0028, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0047, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0035, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0049, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0029, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0033, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0034, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0038, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0046, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0012, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0064, 1, 0 )			
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0057, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0020, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0062, 1, 0 )				
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0007, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0009, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0010, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0016, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0026, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0027, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0030, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0039, 1, 0 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0043, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0045, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0063, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0071, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0059, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0065, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0067, 1, 0 )
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getBalanceType()==null?0:mod200.getBalanceType().ordinal()+1, 1,0))                
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPygType()==null || mod200.getDoubleValue(Mod2002016Key.C0026)==1?0:mod200.getPygType().ordinal()+1, 1,0) )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0061, 1, 0)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getFiscalGroup(), 7)) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getDominantDocument(), 9) ) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(),15) ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0041, 9, 2 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002016Key.C0042, 9, 2 ) 
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
					
					addUnSignedKey(line, mod200, Mod2002016Key.P1501, DS, DD, isComplementary );
					addUnSignedKey(line, mod200, Mod2002016Key.P1502, DS, DD, isComplementary );
					addUnSignedKey(line, mod200, Mod2002016Key.P1503, DS, DD, isComplementary );
					addSignedKey(line, mod200, Mod2002016Key.P1504, isComplementary );
					addSignedKey(line, mod200, Mod2002016Key.P1505, isComplementary );
					addSignedKey(line, mod200, Mod2002016Key.P1506, isComplementary );
					addSignedKey(line, mod200, Mod2002016Key.P1507, isComplementary );
					addSignedKey(line, mod200, Mod2002016Key.P1508, isComplementary );
					line.append( AonFiscalFileUtils.spaces(68));  // Reservado para la AEAT
					
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					
					addUnSignedKey(line, mod200, Mod2002016Key.POR51, 5, 2, isComplementary );
					addUnSignedKey(line, mod200, Mod2002016Key.PORES, 5, 2, isComplementary );
					
					line.append( AonFiscalFileUtils.spaces(200));  // Reservado para la AEAT
					
					isComplementary = true;
					addEndLabel(line,label);
				}
			}
		})
				
		,PAG03 ("T20003000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA101)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA102)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA103)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA104)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA105)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA106)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA107)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA108)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA700)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA109)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA110)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA111)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA112)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA113)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA114)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA115)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA118)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA120)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA121)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA122)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA123)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA130)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA133)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA134)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA138)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA140)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA141)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA142)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA143)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA144)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA145)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA146)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA147)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA148)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA701)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)			
		})
		
		,PAG04 ("T20004000", new IPropertyFiller[] {  
             (line,mod200, label) -> addStartLabel(line,label)
            ,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA149)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA150)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA151)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA152)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA153)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA154)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA155)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA156)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA157)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA158)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA159)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA160)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA161)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA162)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA163)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA164)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA165)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA168)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA172)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA173)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA174)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA175)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA176)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA177)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA178)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA179)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BA180)				
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG05 ("T20005000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP185)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP186)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP187)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP188)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP189)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP190)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP191)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP192)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP193)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP702)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP1001)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP1002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP194)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP195)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP196)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP197)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP198)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP199)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP201)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP202)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP203)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP204)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP205)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP206)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP207)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP208)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP209)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP210)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP211)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP212)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP213)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP214)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP215)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP216)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP217)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP218)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP219)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP220)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP221)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP222)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP223)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP224)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP225)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP226)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP227)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG06 ("T20006000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
            ,(line,mod200, label) -> line.append(" ")			 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP228)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP229)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP230)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP703)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP704)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP232)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP233)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP234)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP235)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP236)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP237)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP238)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP239)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP240)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP241)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP242)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP243)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP244)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP245)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP246)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP247)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP248)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP249)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP250)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP251)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BP252)	
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
			})
				
		,PAG07 ("T20007000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG255)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG256)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG257)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG705)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG706)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG707)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG708)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG258)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG259)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG260)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG261)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG760)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG761)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG262)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG762)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG763)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG263)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG264)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG265)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG266)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG267)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG268)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG269)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG270)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG271)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG273)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG274)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG277)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG278)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG279)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG281)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG282)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG283)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG709)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG284)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG287)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG288)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG289)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG290)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG291)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG292)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG293)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG710)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG294)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG295)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG296)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
			})

		,PAG08 ("T20008000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
		    ,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG297)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG298)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG299)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG304)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG309)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG310)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG311)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG312)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG313)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG314)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG315)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG316)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG317)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG318)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG319)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG320)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG322)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG323)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG329)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG331)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG332) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG324)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG325)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG326)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG327)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG328)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.PG500)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG09 ("T20009000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0500)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0344)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0345)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0347)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0348)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0349)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0350)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0352)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0353)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0354)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.T0355)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG10 ("T20010000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ") 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC380)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC381)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC382)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC383)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC384)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC385)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC386)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC394)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC395)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC396)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC397)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC398)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC399)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC400)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC408)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC409)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC411)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC412)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC414)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC422)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC423)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC424)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC425)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC426)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC427)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC428)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC436)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC437)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC441)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC442)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC450)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC451)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC452)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC453)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC454)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC455)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC456)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC464)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC465)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC466)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC467)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC468)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC469)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC470)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC478)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC479)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC480)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC481)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC482)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC483)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC484)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC492)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC493)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC494)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC495)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC496)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC497)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC498)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC506)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC507)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC508)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC510)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC511)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC512)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC522)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC523)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC524)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC525)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC526)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC534)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC535)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC536)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC537)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC538)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC539)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC540)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC548)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC549)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC550)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC551)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC552)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC553)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC554)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC562)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC563)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC564)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC566)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC567)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC568)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC576)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC577)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC578)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC579)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC580)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC581)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC582)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC591)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC592)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC593)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC594)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC595)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC596)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC604)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC605)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC606)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC607)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC608)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC609)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC610)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC618)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC619)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC620)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC621)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC623)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC624)  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC715)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC716)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC717)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC718)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC719)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC720)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC721)  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC729)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC730)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC731)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC732)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC733)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC734)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC735)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC632)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC633)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC634)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC635)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC636)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC637)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC638)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
		    ,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG11 ("T20011000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC387)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC388)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC389)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC390)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC391)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC393)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC401)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC402)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC403)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC404)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC405)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC406)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC407)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC415)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC417)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC418)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC419)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC420)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC421)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC429)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC430)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC431)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC432)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC433)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC434)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC435)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC443)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC444)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC445)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC446)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC448)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC449)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC457)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC458)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC461)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC462)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC463)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC471)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC472)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC475)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC476)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC477)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC485)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC486)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC489)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC490)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC491)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC499)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC502)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC503)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC504)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC505)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC513)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC515)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC516)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC517)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC527)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC528)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC529)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC530)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC531)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC532)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC533)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC541)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC542)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC543)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC544)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC545)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC546)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC555)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC556)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC557)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC558)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC560)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC561)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC569)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC574)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC575)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC583)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC586)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC589)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC597)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC598)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC599)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC600)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC602)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC603)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC611)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC612)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC613)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC614)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC615)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC616)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC617)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC625)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC626)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC627)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC628)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC629)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC630)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC631)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC722)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC723)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC724)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC725)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC726)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC727)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC728)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC736)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC737)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC738)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC739)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC740)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC741)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC742)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC639)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC640)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC641)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC642)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC643)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC644)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TC645)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG12 ("T20012000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ500)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ501)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1230)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0355)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0356)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0357)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0358)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0359)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0360)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0225)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0226)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0272)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0361)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0362)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0304)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0505)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1005)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1006)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1003)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1004)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0309)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0310)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0516)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0551)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0322)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0415)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0211)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0331)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0325)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0326)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0394)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0334)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0327)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0328)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0543)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0335)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0368)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0508)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1009)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1010)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0363)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0364)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0345)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0371)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0347)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0348)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1011)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1012)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1013)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1014)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1015)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1016)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0369)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0370)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0256)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0278)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0372)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0373)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0374)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1589)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0375)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0376)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1320)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0184)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0544)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1022)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1023)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1018)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1019)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0377)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0378)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0379)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0380)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0381)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0382)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0383)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0384)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT			
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG13 ("T20013000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0387)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0388)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0311)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0312)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0313)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0314)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0323)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0324)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0317)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0318)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0385)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0386)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0389)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0390)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0396)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0397) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0398) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0250)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0251)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0391)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0400)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0403)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0404)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0510)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0512)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0329)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0365)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1026)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0409)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0411)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0412)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I1027)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D1028)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0414)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.I0417)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.D0418)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ578)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ579)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1029)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1030)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1031)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ550)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ552)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1033)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ553)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ554)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ555)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ556)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ559)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1035)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ545)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ593)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1510)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.LQ558, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ560)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ210)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ480)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ408)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1037)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ561)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1331)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG14 ("T20014000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ562)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1038)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN567)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN568)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN563)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN566)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN576)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN569)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1344)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN573)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN575)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN577)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN581)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN582)    
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN583) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1039)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN399)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN592)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN595)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN596)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN597)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN599)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN600)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN601)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN602)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN603)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN604)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN605)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN606)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN611)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN612)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN615)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN616)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN633)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN642)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN617)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN618)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN619)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN620)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1234)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN083)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1042)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN621)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM150)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1020)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1043)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM506)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1021)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1044)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG15 ("T20015000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ640)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ641)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ548)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ643)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ644)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ645)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ646)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ647)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ648)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ649)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ650)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ651)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ652)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ653)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ654)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ655)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ656)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ657)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ658)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ659)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ660)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ661)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ662)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ663)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ664)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ665)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ666)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ667)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ668)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ669)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ743)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ747)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ748)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ277)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ608)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ609)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ610)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ704)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ705)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ706)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ013)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ014)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ015)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ725)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ726)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ727)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ534)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ535)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ536)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ607)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ675)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ699)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1045)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1046)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1047)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ670)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ671)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1048)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1049)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN104)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN105, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN846)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN847)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN848)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN106)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN107, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN282)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN283)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN284)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN108)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN109, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN702)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN703)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN707)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN110)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN111, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN071)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN187)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN112)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN113, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN025)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN026)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN027)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN114)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN115, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN714)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN715)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN716)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN735)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN920, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN736)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN737)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN738)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN118)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN103, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN101)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN102, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN120)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN121)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN122)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN123, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1344)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1345)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN103, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1347)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN153)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN728, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN637)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN638)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN639)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN154)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN729, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN849)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN894)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN197)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN155)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN730, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN287)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN156)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN731, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN825)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN826)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN827)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN157)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN732, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN001)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN003)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN158)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN733, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN028)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN029)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN030)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN159)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN734, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN717)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN718)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN719)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN720)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN721, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN722)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN723)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN724)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN739)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN921, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN740)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN741)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN742)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN134)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN926, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN160)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN161)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN162)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN103, 7, 2 )
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})

		,PAG16 ("T20016000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1054)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN1050, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1051)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1052)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1053)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1348)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN1349, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1350)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1352)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN133)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.BN103, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN163)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN165)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN573)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN174)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN835)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN836)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN837)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN838)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN839)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN840)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN932)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN933)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN934)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN297)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN298)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN299)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN090)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN091)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN092)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN004)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN005)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN006)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN031)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN033)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN022)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN023)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN024)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN042)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN138)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN140)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN141)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN142)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN143)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN188)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN189)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN190)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN803)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN804)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN805)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1055)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1056)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1057)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN700)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN708)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN709)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1353)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1354)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1355)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN841)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN843)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN749)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN750)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN752)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN753)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN754)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN755)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN756)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN757)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN758)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN759)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN760)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN761)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN762)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN763)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN744)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN745)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN746)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN779)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN783)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN784)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN764)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN765)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG16B ("T20016B00", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN854)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN855)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1356)		
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN857)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN858)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN859)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN860)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN861)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN862)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN863)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN864)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN865)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN883)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN884)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN885)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN785)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN789)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN790)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1357)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1358)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1359)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN088)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN564)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN194)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN195)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN196)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN868)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN869)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN834)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN871) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN872)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN873)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN874)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN875)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN876)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN877)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN878)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN879)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN880)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN881)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN882)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN866)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN867)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN870)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN939)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN940)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN941)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN191)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN192)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN193)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN613)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN614)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN701)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN257)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN011)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN037)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN038)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN039)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN044)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN045)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN046)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN528)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN529)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN530)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN144)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN145)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN146)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN147)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN148)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN149)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN240)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN241)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN242)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1058)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1059)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1060)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN791)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN802)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN806)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN852)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN853)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN856)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN886)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN887)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG17 ("T20017000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN768)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN769)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN774)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN775)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN776)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN780)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN781)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN782)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN786)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN787)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN788)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN766)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN767)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN833)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN198)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN896)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN897)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN288)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN289)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN290)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN466)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN467)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN468)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN061)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN498)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN586)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN472)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN473)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN478)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN180)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN181)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN182)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN531)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN532)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN533)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN945)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN946)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN947)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN960)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN961)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN962)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN183)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN185)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN186)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN966)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN967)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN968)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN457)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN458)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN459)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN460)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN461)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN462)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1063)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1064)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1065)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1066)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1067)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1068)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1069)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1070)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1071)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN813)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN814)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN815)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN986)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN810)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN507)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN557)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN591)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN594)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1360)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1361)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1362)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1363)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1364)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1365)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1366)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1367)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1368)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN798)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN799)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN800)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN096)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN698)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN713)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN807)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN808)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN809)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1075)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1076)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1077)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN963)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN964)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN965)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN931)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN502)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN751)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN795)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN796)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN797)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN549)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN888)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN889)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1369)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1370)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1371)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN078)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN079)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN080)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN085)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN086)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN087)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN204)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN205)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN206)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN007)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN012)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN016)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN199)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN292)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN293)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN419)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN422)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN423)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN432)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN433)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN434)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN435)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN436)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN437)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1081)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1083)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1084)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1085)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1086)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1087)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1088)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1089)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1090)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1091)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1092)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1093)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1094)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1095)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1096)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1097)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1098)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1099)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1100)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1101)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1102)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1103)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1104)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1105)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1106)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1107)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1114)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1115)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1118)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1372)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1373)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1374)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1375)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1376)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1377)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1378)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1379)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1380)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1381)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1382)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1383)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1384)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1385)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1386)
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1387)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1388)	
		    ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1389)
   			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG18 ("T20018000", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1390)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1391)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1393)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1394)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1395)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1396)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1397)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1398)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1399)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1400)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1401)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1402)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1403)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1404)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1405)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1406)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1407)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1408)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1409)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1411)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1412)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1414)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1415)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1417)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1418)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1419)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1420)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1421)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1422)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1423)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1424)	
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1425)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN634)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN635)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN636)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN828)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN829)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN830)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN831)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN832)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN918)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN919)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN574)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN580)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN589)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN976)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN977)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN978)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN822)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN823)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN824)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN232)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN233)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN850)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN851)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1123)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1130)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1426)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1427)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1428)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1429)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1430)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1431)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1432)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1433)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN517)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN081)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1234)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN942)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN943)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN294)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN295)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN296)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN066)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN074)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN084)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN008)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN009)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN010)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN035)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN036)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN201)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN202)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN203)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN904)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN905)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN906)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN990)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN991)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN992)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN997)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN998)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN999)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN246)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN247)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN248)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN993)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN994)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN995)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1434)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1435)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1436)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN598)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN895)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN974)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1437)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1441)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1442)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1443)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1444)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1445)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1173)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1178)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1179)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1446)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1181)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1447)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1448)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1449)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1450)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1451)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1452)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1453)			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1454)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1182)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1183)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN1185)
   			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})
		
		,PAG19 ("T20019000", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1141)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1142)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1143)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1144)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1145)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1146)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1455)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1456)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1457)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1147)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1148)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1149)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1150)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1151)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1152)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1153)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1154)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1155)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1156)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1157)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1458)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1459)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1460)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1461)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1158)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1159)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1160)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1161)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID650)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID651)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID652)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID653)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID654)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID1270)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID1271)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID1522)				
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID655)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID656)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID658)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID659)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID660)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID662)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID664)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID665)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.ID666)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC001)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC002)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC003)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC004)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC005)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC006)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC007)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC008)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC009)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC010)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC011)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC012)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC013)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC014)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC015)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC016)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC017)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC018)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC019)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC020)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC021)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC022)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC023)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC024)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC025)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC026)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC027)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC028)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC029)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC030)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC031)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC032)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC033)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC034)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC035)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC036)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC037)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC038)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC039)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC040)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC041)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC042)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC043)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC044)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC045)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC046)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC047)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC048)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC049)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC050)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC051)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC052)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC053)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.DC054)
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
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1240)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1241)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1242)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1243)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1244)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1245)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1246)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1247)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1248)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1249)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1250)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1251)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1252)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1253)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1254)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1255)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1256)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1257)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1258)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1259)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1260)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1188)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1189)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1191)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1193)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1194)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1196)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1198)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1199)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1201)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1202)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1203)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1204)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1205)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1206)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1462)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1463)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1209)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1210)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1211)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1464)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1465)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1466)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1212)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1213)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1214)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1215)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1216)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM890)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM891)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM892)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM503)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM522)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM523)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM273)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM274)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM537)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM955)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM956)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM957)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1217)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1218)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1219)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1467)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1468)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1469)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM538)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM539)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM546)
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
				,(line,mod200, label) -> addEndLabel(line,label)				
		})

		,PAG20B ("T20020B00", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1131)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1132)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1133)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1134)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1135)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1136)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1470)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1471)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1472)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1137)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1032)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1139)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1140)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1473)			
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1474)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1475)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1476)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1477)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1478)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1481)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1482)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1483)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1484)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1485)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1486)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1487)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1488)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1489)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1490)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1491)					
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1492)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1493)	       
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1494)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1495)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1496)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1497)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1498)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1499)
				,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.NUMPER,1,0)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1515)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1516)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1585)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1517)
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
				,(line,mod200, label) -> addEndLabel(line,label)				
			
		})

		,PAG20T ("T20020C00", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")				
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1524)         
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1525) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1526)         
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1527)         
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1528)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1529) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1530) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1590) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1591) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1531) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1532) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1533) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1534)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1535)         
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1536) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1537) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1538) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1539) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1540) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1541)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1542) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1543) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1544) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1545) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1546) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1547) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1548) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1549) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1550) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1551)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1552) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1553) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1554) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1555) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1556)         
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1557) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1558) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1559) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1560)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1561) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1562) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1563) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1564) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1565) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1566) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1567) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1568) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1569) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1570)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM393)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM150)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM506)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1571) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1572) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1573) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1574)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1575) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1576) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1577) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1578)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1579) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1580) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1581) 
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LM1582)
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
//						addUnSignedKey(line, mod200, Mod2002016Key.LQ0N1, 4, 0, isComplementary); // Rég. Entidades navieras en función del tonelaje. Nº de buques  [N1]
//						addSignedKey(line, mod200, Mod2002016Key.LQ630,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de  aplicar la escala [630]
//						addSignedKey(line, mod200, Mod2002016Key.LQ631,isComplementary); // Rég. Entidades navieras en función del tonelaje. Importe rentas generadas en trasmisiones de buques [631]
//						addSignedKey(line, mod200, Mod2002016Key.LQ632,isComplementary); // Rég. Entidades navieras en función del tonelaje. Compensación bases imponibles negativas períodos anteriores [632]
//						addSignedKey(line, mod200, Mod2002016Key.LQ579,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de la aplicación del régimen [579]
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
						
						addSignedKey(line, mod200, Mod2002016Key.CN987, isComplementary); 
						
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						addNIF(line, mod200.getGroupEntities(), i1++);
						
						addSignedKey(line, mod200, Mod2002016Key.CN988, isComplementary);
						addUnSignedKey(line, mod200, Mod2002016Key.CNEST, 3, 0, isComplementary);
						
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						addNIF(line, mod200.getEstablishments(), i2++);
						
						addSignedKey(line, mod200, Mod2002016Key.CN989, isComplementary);
						
						addUnSignedKey(line, mod200, Mod2002016Key.LQ0N1, 4, 0, isComplementary); // Rég. Entidades navieras en función del tonelaje. Nº de buques  [N1]
						addSignedKey(line, mod200, Mod2002016Key.LQ630,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de  aplicar la escala [630]
						addSignedKey(line, mod200, Mod2002016Key.LQ631,isComplementary); // Rég. Entidades navieras en función del tonelaje. Importe rentas generadas en trasmisiones de buques [631]
						addSignedKey(line, mod200, Mod2002016Key.LQ632,isComplementary); // Rég. Entidades navieras en función del tonelaje. Compensación bases imponibles negativas períodos anteriores [632]
						addSignedKey(line, mod200, Mod2002016Key.LQ579,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de la aplicación del régimen [579]
						
						line.append( AonFiscalFileUtils.spaces(200));  // Reservado para la AEAT
						
						isComplementary = true;
						addEndLabel(line,label);
					}
				}
			})
		
		,PAG22 ("T20022000", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")				
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC089)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC094)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC095)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC097)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC098)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC047)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC048)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC524)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC525)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC526)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC527)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC922)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC923)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC924)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC925)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC927)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC928)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC938)
        		,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC996)
		        ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC020)
		        ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.RC021)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C1) // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados cooperativos [C1]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E1) // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados extracooperativos [E1]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C2) // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados cooperativos [C2]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E2) // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados extracooperativos [E2]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C3) // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados cooperativos [C3]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E3) // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados extracooperativos [E3]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C4) // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados cooperativos [C4]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E4) // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados extracooperativos [E4]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E5) // Rég. cooperativas - Determ. base imponible - Incrementos y disminuciones patrimoniales - Resultados extracooperativos [E5]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C6) // Rég. cooperativas - Determ. base imponible - resultado - Resultados cooperativos [C6]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E6) // Rég. cooperativas - Determ. base imponible - resultado - Resultados extracooperativos [E6]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C7) // Rég. cooperativas - Determ. base imponible - aumentos - Resultados cooperativos [C7]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E7) // Rég. cooperativas - Determ. base imponible - aumentos - Resultados extracooperativos [E7]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C8) // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados cooperativos [C8]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E8) // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados extracooperativos [E8]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0C9) // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados cooperativos [C9]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CP0E9) // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados extracooperativos [E9]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CPC10) // Rég. cooperativas - Determ. base imponible - Reserva inversiones Canarias - Resultados cooperativos [C10]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CPC11) // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados cooperativos [C11]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CPE11) // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados extracooperativos [E11]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CPC12) // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados cooperativos [553]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.CPE12) // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados extracooperativos [554]
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ673 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ674 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1224)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ676 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ677 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ678 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ679 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ680 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ681 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ682 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ683 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ684 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ685 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ686 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ687 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ688 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ689 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ690 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ691 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ692 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ693 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ623 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ624 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ672 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ279 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ280 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ281 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ587 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ515 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ900 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ059 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ099 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ100 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ017 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ018 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ019 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ772 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ773 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ777 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ907 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ908 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ909 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ910 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ911 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ912 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ935 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ936 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ937 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1511 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1512 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1513 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ694 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ561 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ695 )
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1225)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ1226)
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
						
						addUnSignedKey(line, mod200, Mod2002016Key.UT060, 7, 4, isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UT500, isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UT1227, isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UT1228, isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UT552, isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UT1330, isComplementary );
												
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						addUteBase(line, mod200, i1++);
						
						addSignedKey(line, mod200, Mod2002016Key.UTC01 , isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UTC02 , isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UTC03 , isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UT062 , isComplementary );						
						addSignedKey(line, mod200, Mod2002016Key.UTC04 , isComplementary );
						addSignedKey(line, mod200, Mod2002016Key.UTC05 , isComplementary );
						
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
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR050) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR051) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR052) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR053) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR054) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR055) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR056) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.TR626, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.TR627, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.TR628, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.TR629, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002016Key.TR625, 5, 2) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR420) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR421) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR426) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR427) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR600) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR402) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR442) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR443) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR444) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR602) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR445) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR446) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR447) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR448) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR604) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR449) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR450) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR451) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR465) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR606) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR474) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR475) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR476) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR477) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR612) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR482) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR483) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR484) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR485) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR616) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR913) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR914) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR915) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR916) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR642) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR486) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR487) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR488) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR489) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR618) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR490) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR491) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR492) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR493) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR620) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1334)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1335)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR494) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR495) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR496) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR497) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1043)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.TR1044)
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
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ552)   // Liquidación - Base imponible [552]                                    
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.LQ562)   // Liquidación - Cuota íntegra [562]                          
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002016Key.BN621)   // Liquidación - Líquido a ingresar o a devolver Estado [621]
				
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> {
						double importe = mod200.getDoubleValue(Mod2002016Key.BN621); // importe a ingresar o a devolver
						
						line.append( AonFiscalFileUtils.text(importe<0 ? ("V".equals(mod200.getDevType())?"":mod200.getDevType()) : "",1) ); // Devolución - Renuncia o por Transferencia "blanco" "R","D"				
						line.append( AonFiscalFileUtils.signedZero(importe<0 ? Math.abs(importe) : 0.0, DS, DD) );     // Devolución - Importe a devolver
						line.append( AonFiscalFileUtils.text(importe<0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "",34) );  // Devolución - Número de cuenta IBAN (si devolución por transferencia)
						line.append( AonFiscalFileUtils.text(importe<0 && "D".equals(mod200.getDevType()) ? mod200.getBic() : "",11)); // Devolución - Código SWIFT-BIC
						line.append( AonFiscalFileUtils.text(importe>0 ? mod200.getPayType() : "",1) );  // Ingreso - Modalidad de ingreso. Uno de los siguientes valores	"blanco", "I" Adeudo en	cuenta, "H" Efectivo, "U" Domiciliación
						line.append(" ");   // RESERVADO AEAT
						line.append(" ");   // RESERVADO AEAT
						line.append( AonFiscalFileUtils.signedZero(importe>0 ? importe : 0.0, DS, DD) );               // Ingreso - Importe a ingresar
						line.append( AonFiscalFileUtils.text(importe>0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType())) ? mod200.getIban() : "",34) ); // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)			
		
						addSignedKey(line, mod200, Mod2002016Key.LM150);  // Abono/Compensación - Abono por conversión de activos impuesto diferido - A       
						addSignedKey(line, mod200, Mod2002016Key.LM506);  // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C
						
						line.append(importe == 0 ? "1" : "0"); // Cuota Cero "0" o "1"
					}
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(200))  // Reservado para la AEAT
				,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
					
				})
		;
		 
		private String tag;
		private IPropertyFiller[] propertyFillers;
		
		private Pages2016(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002016 mod200, Writer line) throws IOException {
			
			// Controles determinadas páginas que solo se ponen si están marcados ciertos caracteres
			boolean addPage = true;
			
			// Página 7.
			// Página 8. La cuenta de pérdidas y ganancias no debe aparecer si se ha marcado el caracter 26 (entidad inactiva).
//			if (this == Pages2016.PAG07 || this == Pages2016.PAG08) {
//				addPage = (mod200.getDoubleValue(Mod2002016Key.C0026)==0);
//			}
			
			// Página 9. Estado de Ingresos y Gastos Reconocidos. Solo si Balance Normal o Abreviado
			if (this == Pages2016.PAG09) {  
				addPage = (mod200.getBalanceType() == BalanceType.NORMAL) ||
				          (mod200.getBalanceType() == BalanceType.ABREVIADO);					
			}
			
			// Página 22. Regimen especial de la reserva para inversiones en Canarias y Cooperativas
			if (this == Pages2016.PAG22) {
				addPage = (mod200.getDoubleValue(Mod2002016Key.C0029)==1) ||
			              (mod200.getDoubleValue(Mod2002016Key.C0017)==1) ||
			              (mod200.getDoubleValue(Mod2002016Key.C0018)==1) ||
			              (mod200.getDoubleValue(Mod2002016Key.C0019)==1);
			}
			
			// Página 24. Agrupaciones de interes económico y UTES (regimen especial). Caracteres 013 o 014 marcados
			if (this == Pages2016.PAG24) {
				addPage = (mod200.isChecked( Mod2002016Key.C0013) 
					    || mod200.isChecked( Mod2002016Key.C0014));			              
			}
			
			// Página 26. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2016.PAG26) {
				addPage = (mod200.getDoubleValue(Mod2002016Key.C0028)==1);
			}
			
			// Añadir el contenido de la página
			if (addPage) {
	 			for (IPropertyFiller propertyFiller : this.propertyFillers) {
	 				propertyFiller.propertyFill(line, mod200, this.tag);
				}     		
			}
		}
	}
	
	public static void fillWriter(Mod2002016 mod200, Writer line) throws IOException {
		
		// FALTA - Hay una incoherencia en el formato del fichero, la etiqueta la pone
		// de longitud 17, pero la etiqueta que especifica solo tiene 16, el año pasado
		// llevaba un cero detras del año y este año no lo lleva, pero no han actualizado
		// la longitud del campo
		//line.append("<T2000"+mod200.getYear()+"0A0000>");  // Etiqueta inicio de fichero
		line.append("<T2000"+mod200.getYear()+"0A0000>");  // Etiqueta inicio de fichero
		for (Pages2016 page : Pages2016.values()) {     			
			page.fillPage(mod200, line);			
		}
		// FALTA - IDEM etiqueta de inicio
		//line.append("</T2000"+mod200.getYear()+"0A0000>");  // Etiqueta fin de fichero
		line.append("</T2000"+mod200.getYear()+"0A0000>");  // Etiqueta fin de fichero
		line.append(AonStringUtils.CR_LF); // Fin de registro. Constante CRLF
		line.close(); 
		
	}
	
	private static void setDoubleValue2016(Mod2002016 mod200, Mod2002016Key key, double value) {	
		 
		DoubleVariable2016 t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariable2016(key);
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
			
			// Creamos un nuevo modelo 200 2016
			Mod2002016 mod200 = new Mod2002016();
			
			// Inicializamos las casillas con un valor aleatorio			
			for (Mod2002016Key key : Mod2002016Key.values()) {
				double value = AonNumberUtils.todouble( AonRandomStringUtils.randomNumeric(4));
				value = (AonMathUtils.isZero(value))?value:AonMathUtils.round( value  / 100 );
				setDoubleValue2016(mod200, key, value);						
			}
			
			// Periodo inicio y fin 
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			mod200.setPeriodStart( sdf.parse("01-01-2016") );
			mod200.setPeriodEnd( sdf.parse("31-12-2016") );
			mod200.setYear(2016);
			
			// Caracteres todos a cero excepto 9, 13, 28 y 29 para que salgan todas las páginas
			for (Mod2002016Key key : Mod2002016Character.CHARACTERS_KEYS) {				
				setDoubleValue2016(mod200, key, 0.0);						
			}
			setDoubleValue2016(mod200, Mod2002016Key.C0009, 1.0);
			setDoubleValue2016(mod200, Mod2002016Key.C0028, 1.0);
			setDoubleValue2016(mod200, Mod2002016Key.C0029, 1.0);
			setDoubleValue2016(mod200, Mod2002016Key.C0013, 1.0);
			
			// Estados de cuentas de IIC
			setDoubleValue2016(mod200, Mod2002016Key.C0061, 0.0);
			
			// Numero de periodo impositivo
			setDoubleValue2016(mod200, Mod2002016Key.NUMPER, 0.0);
			
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
			String filename = "c:\\tmp\\prueba_M200_2016.txt";
			BufferedWriter line = new BufferedWriter(new FileWriter(filename));
			fillWriter(mod200, line);
			
			// Mostramos la longitud de cada pagina 
			FileInputStream input = new FileInputStream(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();							
			Document doc = db.parse(new InputSource(new InputStreamReader(input,"ISO-8859-1")));
			
			// Longitudes de todas las páginas
			HashMap<String, Integer> m = new HashMap<String, Integer>();
			m.put(Pages2016.PAG00.tag, 311);
			m.put(Pages2016.PAG01.tag, 884);
			m.put(Pages2016.PAG02.tag, 2094);
			m.put(Pages2016.PAG03.tag, 1074);
			m.put(Pages2016.PAG04.tag, 768);
			m.put(Pages2016.PAG05.tag, 1006);
			m.put(Pages2016.PAG06.tag, 683);
			m.put(Pages2016.PAG07.tag, 1091);
			m.put(Pages2016.PAG08.tag, 853);
			m.put(Pages2016.PAG09.tag, 581);
			m.put(Pages2016.PAG10.tag, 2723);
			m.put(Pages2016.PAG11.tag, 2502);
			m.put(Pages2016.PAG12.tag, 2026);
			m.put(Pages2016.PAG13.tag, 1503);
			m.put(Pages2016.PAG14.tag, 1329);
			m.put(Pages2016.PAG15.tag, 3024);
			m.put(Pages2016.PAG16.tag, 1854);
			m.put(Pages2016.PAG16B.tag, 1686);
			m.put(Pages2016.PAG17.tag, 3369);
			m.put(Pages2016.PAG18.tag, 2808);
			m.put(Pages2016.PAG19.tag, 1999);
			m.put(Pages2016.PAG20.tag, 1397);
			m.put(Pages2016.PAG20B.tag, 939);
			m.put(Pages2016.PAG20T.tag, 1312);
			m.put(Pages2016.PAG21.tag, 476);
			m.put(Pages2016.PAG22.tag, 1907);
			m.put(Pages2016.PAG23.tag, 1014);
			m.put(Pages2016.PAG24.tag, 2288);
			//m.put(Pages2016.PAG25.tag, 3685);
			m.put(Pages2016.PAG26.tag, 1558);
			m.put(Pages2016.DID.tag, 604);
			
			for ( Pages2016 page : Pages2016.values()) {
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
