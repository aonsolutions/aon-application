package com.esferalia.aon.occam.server.fiscal.format.mod200;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.DoubleVariable2014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002014Writer {
	
	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************
	
	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	
	private static int DEFAULT_SIZE = 17;  // Tamaño de digitos por defecto para los importes 
	private static int DEFAULT_DEC  =  2;  // Decimales por defecto para los importes
	
	// Añade un importe de una casilla del modelo (con signo, longitud y decimales por defecto, relleno con ceros por la izquierda) 
	private static void addSignedKey(Writer line, Mod2002014 mod200, Mod2002014Key key) throws IOException {
		
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DEFAULT_SIZE, DEFAULT_DEC ));
		
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002014 mod200, Mod2002014Key key, boolean isComplementary) throws IOException {
		
		if (isComplementary)
			line.append(AonFiscalFileUtils.zeros(DEFAULT_SIZE));
		else line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DEFAULT_SIZE, DEFAULT_DEC ));
		
	}
	
	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por la izquierda) 
	private static void addUnSignedKey(Writer line, Mod2002014 mod200, Mod2002014Key key, int size, int dec) throws IOException {
		
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key) , size, dec ));
		
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002014 mod200, Mod2002014Key key, int size, int dec, boolean isComplementary) throws IOException {
		
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
	private static void addLegalRepresentative(Writer line, Mod2002014 mod200, int index) throws IOException {
		
		// Inicializar variables
		String name = "";
		String document = "";
		Date notaryDate = null;
		String notary = "";
		
		// Asignar variables, si aun no hemos terminado de poner todos los elementos de la lista		
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
	private static void addCompanyAdministrator(Writer line, Mod2002014 mod200, int index) throws IOException {

		// Inicializar variables
		String document = "";
		String fj = "";
		String rpte = "0";
		String name = "";
		String residence = "";
		String province = "";
		
		// Asignar variables, si aun no hemos terminado de poner todos los elementos de la lista
		if (index < mod200.getAdministrators().size()) {
			document = mod200.getAdministrators().get(index).getDocument();			
			fj = mod200.getAdministrators().get(index).getEntity();	
			rpte = mod200.getAdministrators().get(index).getRepresenStr();                             
			name = mod200.getAdministrators().get(index).getName();
			residence = mod200.getAdministrators().get(index).getResidence();                 
            if (mod200.getDoubleValue(Mod2002014Key.C0021)==1)
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
	private static String getProvinceCountry(CompanyParticipation cp) {
		
		int province = cp.getProvince();
		if (province==0)
			return AonStringUtils.trimToEmpty(cp.getCountry()); // Pais
		else return AonStringUtils.leftPad(Integer.toString(province), 2, "0");  // Provincia
		
	}
	
	// B.1. Participaciones declarante en otras entidades	
	private static void addCompanyParticipationOut(Writer line, Mod2002014 mod200, int index) throws IOException {

		// Inicializar variables
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
		double capital = 0; 
		double reserve = 0; 
		double otherAmounts = 0; 
		double result = 0;
		
		// Asignar variables, si aun no hemos terminado de poner todos los elementos de la lista		
		if (index < mod200.getParticipationsOut().size()) {			
            document     = mod200.getParticipationsOut().get(index).getDocument();	
            name         = mod200.getParticipationsOut().get(index).getName();	    
            province     = getProvinceCountry(mod200.getParticipationsOut().get(index));	
            percent      = mod200.getParticipationsOut().get(index).getPercent();
            nominalValue = mod200.getParticipationsOut().get(index).getNominalValue();  
            bookValue    = mod200.getParticipationsOut().get(index).getBookValue();  	
            incomes 	 = mod200.getParticipationsOut().get(index).getIncomes();       
            aValue       = mod200.getParticipationsOut().get(index).getaValue();        
            bValue       = mod200.getParticipationsOut().get(index).getbValue();        
            cValue       = mod200.getParticipationsOut().get(index).getcValue();        
            dValue       = mod200.getParticipationsOut().get(index).getdValue();        
            capital      = mod200.getParticipationsOut().get(index).getCapital();       
            reserve      = mod200.getParticipationsOut().get(index).getReserve();       
            otherAmounts = mod200.getParticipationsOut().get(index).getOtherAmounts();  
            result       = mod200.getParticipationsOut().get(index).getResult();        
		}		
		
		// Añadir los datos al Writer
        line.append( AonFiscalFileUtils.text(document, 15 ) );	// Datos participada - N.I.F.
        line.append( AonFiscalFileUtils.text(name,     30 ) );	    // Datos participada - Nombre o razón social
        line.append( AonFiscalFileUtils.text(province,  2 ) );	    // Datos participada - Código provincia / país 
        line.append( AonFiscalFileUtils.unsigned(percent,5,2) );	// Datos de la declarante - Porcentaje de participación 
        line.append( AonFiscalFileUtils.signedZero(nominalValue, DEFAULT_SIZE, DEFAULT_DEC) );   // Datos de la declarante - Valor nominal total de la participación
        line.append( AonFiscalFileUtils.signedZero(bookValue   , DEFAULT_SIZE, DEFAULT_DEC) );  	// Datos de la declarante - Valor en libros (en el activo de la declarante) de la participación
        line.append( AonFiscalFileUtils.signedZero(incomes 	   , DEFAULT_SIZE, DEFAULT_DEC) );   // Datos de la declarante - Ingresos por Dividendos recibidos en el ejercicio declarado
        line.append( AonFiscalFileUtils.signedZero(aValue      , DEFAULT_SIZE, DEFAULT_DEC) );   // Correcciones valorativas - Corrección de valor pérdidas y ganancias ejercicio
        line.append( AonFiscalFileUtils.signedZero(bValue      , DEFAULT_SIZE, DEFAULT_DEC) );   // Correcciones valorativas - Reversión de pérdidas por deterioro de valores
        line.append( AonFiscalFileUtils.signedZero(cValue      , DEFAULT_SIZE, DEFAULT_DEC) );   // Correcciones valorativas - Efecto corrección valorativa en la BI del ejercicio
        line.append( AonFiscalFileUtils.signedZero(dValue      , DEFAULT_SIZE, DEFAULT_DEC) );   // Corrrecciones valorativas - Saldo de correcciones fiscales (art. 12.3 LIS) pendientes a fin de ejercicio
        line.append( AonFiscalFileUtils.signedZero(capital     , DEFAULT_SIZE, DEFAULT_DEC) );   // Datos adicionales participada - Capital 
        line.append( AonFiscalFileUtils.signedZero(reserve     , DEFAULT_SIZE, DEFAULT_DEC) );   // Datos adicionales participada - Reservas
        line.append( AonFiscalFileUtils.signedZero(otherAmounts, DEFAULT_SIZE, DEFAULT_DEC) );   // Datos adicionales participada - Otras partidas del patrimonio neto
        line.append( AonFiscalFileUtils.signedZero(result      , DEFAULT_SIZE, DEFAULT_DEC) );   // Datos adicionales participada - Resultado del último ejercicio
		
	}
	
	// B.2. Participaciones de personas o entidades en la declarante
	private static void addCompanyParticipationIn(Writer line, Mod2002014 mod200, int index) throws IOException {
		
		// Inicializar variables
		String document = "";
		String rpte = "0";
		String fj = "";
		String name = "";
		String province = "";
		double nominalValue = 0;
		double percent = 0;
		
		// Asignar variables, si aun no hemos terminado de poner todos los elementos de la lista
		if (index < mod200.getParticipationsIn().size()) {
			document = mod200.getParticipationsIn().get(index).getDocument();   
			rpte = mod200.getParticipationsIn().get(index).getRepresenStr();
			fj = mod200.getParticipationsIn().get(index).getEntity();
			name = mod200.getParticipationsIn().get(index).getName();
			province = getProvinceCountry(mod200.getParticipationsIn().get(index));   
			nominalValue = mod200.getParticipationsIn().get(index).getNominalValue();
			percent = mod200.getParticipationsIn().get(index).getPercent();
		}
		
		// Añadir los datos al Writer 
		line.append( AonFiscalFileUtils.text(document, 15)); // N.I.F.
		line.append( AonFiscalFileUtils.text(rpte,      1)); // RPTE.
		line.append( AonFiscalFileUtils.text(fj,        1)); // F/J					                                                      
		line.append( AonFiscalFileUtils.text(name,     37)); // Apellidos y nombre / Razón social 
		line.append( AonFiscalFileUtils.text(province,  2)); // Código provincia / país
		line.append( AonFiscalFileUtils.signedZero(nominalValue, DEFAULT_SIZE, DEFAULT_DEC) ); // Nominal 
		line.append( AonFiscalFileUtils.unsigned(percent, 5,2) ); // % Particip.
		
	}
	
	// **** FIN VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	
	@FunctionalInterface
	private interface IPropertyFiller {		
		public void propertyFill(Writer line, Mod2002014 mod200, String tag) throws IOException;
	}
    
	// Paginas del modelo 
	private enum Pages2014 {
		
		// String relleno con ceros por la derecha -> AonFiscalFileUtils.text(str, longitud)
		// Fecha formato yyyyMMdd o ceros si es null -> AonFiscalFileUtils.dateZero(date)
		// Numero con signo relleno con ceros por la izquierda -> AonFiscalFileUtils.signedZero( num , lon, dec)
		// Numero con signo relleno con ceros por la izquierda -> AonFiscalFileUtils.signedZero( num , lon ) -> se asume 2 decimales
		
		 PAG00 ("AUX", new IPropertyFiller[] {				 
             
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
 			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces( 70)) // Reservado para la Administración. Rellenar con blancos BLANCOS
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  4)) // Versión del programa (**)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  4)) // Reservado para la Administración. Rellenar con blancos
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  9)) // NIF Empresa Desarrollo (**)
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(213)) // Reservado para la Administración. Rellenar con blancos
			,(line,mod200, label) -> addEndLabel(line,label)    // Etiqueta de fin de pagina
						
		 })
		 
		 ,PAG01 ("T200010", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getPeriodStart()) )        // Periodo Impositivo - Inicio					
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getPeriodEnd())   )    	// Periodo Impositivo - Fin
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPeriodType(),1,0) )     // Identificación - Tipo de ejercicio
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getCnae()==null?"":mod200.getCnae().replace(".",""), 4) )  // Identificación - C.N.A.E.  Actividad principal
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseDocument(),9) )   // Identificación - NIF 
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseName(),40) )      // Identificación - Apellidos y nombre o Razón Social
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterprisePhone1(),9) )     // Identificación - Teléfono 1
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterprisePhone2(),9))      // Identificación - Teléfono 2
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getYear(), 4,0))           // Ejercicio
				 
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0001, 1, 0 )  // Entidad sin ánimo de lucro acogida régimen fiscal Título II Ley 49/2002 [001] 
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0002, 1, 0 )  // Entidad parcialmente exenta [002]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0003, 1, 0 )  // Sociedad de inversión de capital variable o fondo de inversión de carácter financiero [003]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0004, 1, 0 )  // Sociedad de inversión inmobiliaria o fondo de inversión inmobiliaria [004]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0005, 1, 0 )  // Comunidades titulares de montes vecinales en mano común [005]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0011, 1, 0 )  // Entidad de tenencia de valores extranjeros [011]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0013, 1, 0 )  // Agrupación de interés económico española o U.T.E. [013]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0014, 1, 0 )  // Agrupación europea de  interés económico [014]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0017, 1, 0 )  // Cooperativa protegida [017]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0018, 1, 0 )  // Cooperativa especialmente protegida [018]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0019, 1, 0 )  // Resto cooperativas [019]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0021, 1, 0 )  // Establecimiento permanente [021]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0023, 1, 0 )  // Gran empresa [023]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0024, 1, 0 )  // Entidad de crédito [024]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0025, 1, 0 )  // Entidad aseguradora [025]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0031, 1, 0 )  // Entidades de  capital-riesgo [031]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0032, 1, 0 )  // Sociedades desarrollo industrial regional [032]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0036, 1, 0 )  // Sociedad de garantía recíproca o de reafianzamiento [036]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0048, 1, 0 )  // Fondo de Pensiones Real Decreto Legislativo 1/2002 de 29 de noviembre [048]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0058, 1, 0 )  // Mutua de seguros o Mutualidad de previsión social [058]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0060, 1, 0 )  // Fondos o activos de titulización [060]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0006, 1, 0 )  // Incentivos empresa de reducida dimensión ( cap XII, tít VII L.I.S )  [006]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0015, 1, 0 )  // Entidad ZEC [015]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0022, 1, 0 )  // Régimen entidades navieras en función del tonelaje [022]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0028, 1, 0 )  // Tributación conjunta Estado/Diput.Cdad.Forales [028]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0047, 1, 0 )  // Entidades sometidas a normativa foral [047]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0049, 1, 0 )  // Regímenes especiales de normativa foral [049]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0029, 1, 0 )  // Régimen especial Canarias [029]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0033, 1, 0 )  // Régimen especial minería [033]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0034, 1, 0 )  // Régimen especial hidrocarburos [034]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0038, 1, 0 )  // Entidad dedicada al arrend.viviendas [038]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0046, 1, 0 )  // Entidad en rég. atribución de rentas constituida en el extranjero con presencia en territorio español [046]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0012, 1, 0 )  // SOCIMI [012]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0064, 1, 0 )  // Régimen fiscal entrada SOCIMI [064]			
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0057, 1, 0 )  // Régimen fiscal salida SOCIMI [057]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0020, 1, 0 )  // Otros regímenes especiales [020]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0056, 1, 0 )  // Tipo gravamen reducido mant.o creación empleo [056]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0007, 1, 0 )  // Inclusión en base imponible rentas positivas art. 107 L.I.S. [007]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0008, 1, 0 )  // Opción art. 107.6 L.I.S. [008]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0009, 1, 0 )  // Sociedad dominante de grupo fiscal [009]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0010, 1, 0 )  // Sociedad dependiente de grupo fiscal [010]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0016, 1, 0 )  // Opción  art.51.2.b)  L.I.S. [016]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0026, 1, 0 )  // Entidad  inactiva [026]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0027, 1, 0 )  // Base imponible negativa o cero [027]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0030, 1, 0 )  // Transmisión elementos patrimoniales arts. 26.2.d) y 84.1 L.I.S. [030]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0035, 1, 0 )  // Opción art. 43.1 R.I.S. [035] 
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0037, 1, 0 )  // Opción art. 43.3 R.I.S. [037]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0039, 1, 0 )  // Entidad que forma parte de un grupo mercantil (art. 42 del Cód. Comercio) [039]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0043, 1, 0 )  // Obligación información art. 15 R.I.S. [043]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0044, 1, 0 )  // Obligación información art. 45 R.I.S. [044] 
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0045, 1, 0 )  // Inversiones anticipadas - reserva inversiones en Canarias (art. 27.11 Ley 19/1994) [045]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0062, 1, 0 )  // Régimen fiscal de operaciones de aportación de activos a sociedades para la gestión de activos (Ley 8/2012) [062]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0063, 1, 0 )  // Tipo de gravamen reducido para entidades de nueva creción (D.A. 19ª LIS) [063]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0059, 1, 0 )  // Opción  art.44.2 LIS [059]
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0065, 1, 0 )  // Bonificación personal investigador (RD 475/2014) [065]
			 
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned( mod200.getBalanceType()==null?0:mod200.getBalanceType().ordinal()+1, 1,0))  // Balance y ECPN 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES                
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned( mod200.getPygType()==null||mod200.getDoubleValue(Mod2002014Key.C0026)==1?0:mod200.getPygType().ordinal()+1, 1,0) )          // Pérdidas y ganancias 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES (si marcado caracter 026 entidad inactiva, este campo no se cumplimenta)
			
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0061, 1, 0)      // Estados de cuentas de Instituciones de inversión colectiva [061]
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getFiscalGroup(), 7) )             // Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040] 
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getDominantDocument(), 9) )        // N.I.F. de la sociedad dominante para entidades que hayan marcado la clave 010 
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0041, 9, 2 )     // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002014Key.C0042, 9, 2 )     // Personal asalariado (cifra media del ejercicio) Personal no fijo [042] 
			,(line,mod200, label) -> line.append( mod200.isComplementary()?"1":"0" )        // Declaración complementaria
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(AonNumberUtils.todouble(mod200.getComplementaryReceipt()),13,0) )    // Nº de justificante de la declaración anterior
			
            ,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getSecretary().getName(),21) )     // Nombre o Razón social - Secretario del Consejo de Administración 
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getSecretary().getDocument(),9) )  // N.I.F. - Secretario del Consejo de Administración
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getSecretary().getIrnr()) )    // Fecha - Contribuyentes por el I.R.N.R. 
			
			,(line,mod200, label) -> addLegalRepresentative(line, mod200, 0) // Representantes legales de la entidad (1)
			,(line,mod200, label) -> addLegalRepresentative(line, mod200, 1) // Representantes legales de la entidad (2)
			,(line,mod200, label) -> addLegalRepresentative(line, mod200, 2) // Representantes legales de la entidad (3)
			
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(21) ) // RESERVADO PARA LA A E A T (Dejar en blanco) A.E.A.T. Incluye Nº Referencia
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(20) ) // Identificador cliente EEDD. RESERVADO PARA LAS EEDD.
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(50) ) // Nombre y Apellidos de la persona de contacto para incidencias
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.zeros(9) )   // Teléfono fijo de contacto para incidencias
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.zeros(9) )   // Teléfono móvil de contacto para incidencias
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(50) ) // Dirección de correo electrónico para incidencias
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(13) ) // SELLO ELECTRONICO RESERVADO PARA LA A.E.A.T. (Dejar en blanco)			
				
			,(line,mod200, label) -> addEndLabel(line,label)
						
			})
			
		,PAG02 ("T200020", new IPropertyFiller[] {
				
			(line,mod200, label) -> {
				
				boolean isComplementary = false; // Indicador de pagina complementaria
				
				int a  = 0;  // Contador para Administradores
				int b1 = 0;  // Contador para Participaciones B1
				int b2 = 0;  // Contador para Participaciones B2 
				
				while ( a  < mod200.getAdministrators().size() ||
					    b1 < mod200.getParticipationsOut().size() ||
						b2 < mod200.getParticipationsIn().size() ) {
					
					addStartLabel(line,label);             // Etiqueta inicio de pagina					 
					line.append(isComplementary?"C":" ");  // Indicador de pagina complementaria
					
					addCompanyAdministrator(line, mod200, a++);     // A. Relación de administradores (1)
					addCompanyAdministrator(line, mod200, a++);     // A. Relación de administradores (2)
					addCompanyAdministrator(line, mod200, a++);     // A. Relación de administradores (3)
					addCompanyAdministrator(line, mod200, a++);     // A. Relación de administradores (4)
					addCompanyAdministrator(line, mod200, a++);     // A. Relación de administradores (5)
					addCompanyAdministrator(line, mod200, a++);     // A. Relación de administradores (6)
					
					addCompanyParticipationOut(line, mod200, b1++); // B1. Participaciones declarante en otras entidades (1)
					addCompanyParticipationOut(line, mod200, b1++); // B1. Participaciones declarante en otras entidades (2)
					addCompanyParticipationOut(line, mod200, b1++); // B1. Participaciones declarante en otras entidades (3)
					addCompanyParticipationOut(line, mod200, b1++); // B1. Participaciones declarante en otras entidades (4)
					
					addCompanyParticipationIn(line, mod200, b2++);  // B.2. Participaciones de personas o entidades en la declarante (1)
					addCompanyParticipationIn(line, mod200, b2++);  // B.2. Participaciones de personas o entidades en la declarante (2)
					addCompanyParticipationIn(line, mod200, b2++);  // B.2. Participaciones de personas o entidades en la declarante (3)
					addCompanyParticipationIn(line, mod200, b2++);  // B.2. Participaciones de personas o entidades en la declarante (4)
					addCompanyParticipationIn(line, mod200, b2++);  // B.2. Participaciones de personas o entidades en la declarante (5)
					addCompanyParticipationIn(line, mod200, b2++);  // B.2. Participaciones de personas o entidades en la declarante (6)
					
					// Estas dos casillas van a cero en las paginas complementarias
					addUnSignedKey(line, mod200, Mod2002014Key.POR51, 5, 2, isComplementary ); // Suma de porcentajes de participación de personas o entidades en el capital de la declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado
					addUnSignedKey(line, mod200, Mod2002014Key.PORES, 5, 2, isComplementary ); // Suma de porcentajes de participaciones en situaciones especiales

					isComplementary = true; // La siguiente pagina (si la hay), será complementaria
					
					addEndLabel(line,label); // Etiqueta fin de pagina
				}
			}
			
			})
				
		,PAG03 ("T200030", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label) // Etiqueta inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA101 )  // Balance: Activo (I) - Activo - ACTIVO NO CORRIENTE [101]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA102 )  // Balance: Activo (I) - Activo - Inmovilizado intangible  [102]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA103 )  // Balance: Activo (I) - Activo - Desarrollo  [103]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA104 )  // Balance: Activo (I) - Activo - Concesiones  [104]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA105 )  // Balance: Activo (I) - Activo - Patentes, licencias, marcas y similares  [105]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA106 )  // Balance: Activo (I) - Activo - Fondo de comercio  [106]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA107 )  // Balance: Activo (I) - Activo - Aplicaciones informáticas  [107]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA108 )  // Balance: Activo (I) - Activo - Investigación  [108]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA700 )  // Balance: Activo (I) - Activo - Propiedad intelectual  [700]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA701 )  // Balance: Activo (I) - Activo - Derechos de emisión de gases de efecto invernadero [701]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA109 )  // Balance: Activo (I) - Activo - Otro inmovilizado intangible  [109]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA110 )  // Balance: Activo (I) - Activo - Resto  [110]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA111 )  // Balance: Activo (I) - Activo - Inmovilizado material  [111]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA112 )  // Balance: Activo (I) - Activo - Terrenos y construcciones  [112]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA113 )  // Balance: Activo (I) - Activo - Instalaciones técnicas y otro inmovilizado material  [113]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA114 )  // Balance: Activo (I) - Activo - Inmovilizado en curso y anticipos [114]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA115 )  // Balance: Activo (I) - Activo - Inversiones inmobiliarias [115]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA116 )  // Balance: Activo (I) - Activo - Terrenos [116]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA117 )  // Balance: Activo (I) - Activo - Construcciones [117]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA118 )  // Balance: Activo (I) - Activo - Inversiones en empresas del grupo y asociadas [118]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA119 )  // Balance: Activo (I) - Activo - Instrumentos de patrimonio [119]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA120 )  // Balance: Activo (I) - Activo - Créditos a empresas [120]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA121 )  // Balance: Activo (I) - Activo - Valores representativos de deuda [121]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA122 )  // Balance: Activo (I) - Activo - Derivados [122]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA123 )  // Balance: Activo (I) - Activo - Otros activos financieros [123]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA124 )  // Balance: Activo (I) - Activo - Otras inversiones [124]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA125 )  // Balance: Activo (I) - Activo - Resto [125]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA126 )  // Balance: Activo (I) - Activo - Inversiones financieras a largo plazo [126]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA127 )  // Balance: Activo (I) - Activo - Instrumentos de patrimonio [127]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA128 )  // Balance: Activo (I) - Activo - Créditos a terceros [128]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA129 )  // Balance: Activo (I) - Activo - Valores representativos de deuda [129]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA130 )  // Balance: Activo (I) - Activo - Derivados [130]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA131 )  // Balance: Activo (I) - Activo - Otros activos financieros [131]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA132 )  // Balance: Activo (I) - Activo - Otras inversiones [132]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA133 )  // Balance: Activo (I) - Activo - Resto [133]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA134 )  // Balance: Activo (I) - Activo - Activos por impuesto diferido [134]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA135 )  // Balance: Activo (I) - Activo - Deudores comerciales no corrientes [135]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA136 )  // Balance: Activo (I) - Activo - ACTIVO CORRIENTE [136]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA137 )  // Balance: Activo (I) - Activo - Activos no corrientes mantenidos para la venta [137]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA138 )  // Balance: Activo (I) - Activo - Existencias [138]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA139 )  // Balance: Activo (I) - Activo - Comerciales  [139]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA140 )  // Balance: Activo (I) - Activo - Materias primas y otros aprovisionamientos [140]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA141 )  // Balance: Activo (I) - Activo - Productos en curso [141]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA142 )  // Balance: Activo (I) - Activo - Productos en curso - De ciclo largo de producción  [142]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA143 )  // Balance: Activo (I) - Activo - Productos en curso - De ciclo corto de producción  [143]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA144 )  // Balance: Activo (I) - Activo - Productos terminados [144]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA145 )  // Balance: Activo (I) - Activo - Productos terminados - De ciclo largo de producción  [145]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA146 )  // Balance: Activo (I) - Activo - Productos terminados - De ciclo corto de producción  [146]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA147 )  // Balance: Activo (I) - Activo - Subproductos, residuos y materiales recuperados [147]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA148 )  // Balance: Activo (I) - Activo - Anticipos a proveedores [148]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina			
			
			})
				
		,PAG04 ("T200040", new IPropertyFiller[] {  
             (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
            ,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA149 )  // Balance: Activo (II) - Activo - Deudores comerciales y otras cuentas a cobrar [149]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA150 )  // Balance: Activo (II) - Activo - Clientes por ventas y prestaciones de servicios [150]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA151 )  // Balance: Activo (II) - Activo - Clientes por ventas y prestaciones de servicios - Clientes por ventas y prestaciones de servicios a largo plazo [151]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA152 )  // Balance: Activo (II) - Activo - Clientes por ventas y prestaciones de servicios - Clientes por ventas y prestaciones de servicios a corto plazo [152]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA153 )  // Balance: Activo (II) - Activo - Clientes empresas del grupo y asociadas [153]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA154 )  // Balance: Activo (II) - Activo - Deudores varios [154]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA155 )  // Balance: Activo (II) - Activo - Personal [155]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA156 )  // Balance: Activo (II) - Activo - Activos por impuesto corriente [156]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA157 )  // Balance: Activo (II) - Activo - Otros créditos con las Administraciones Públicas [157]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA158 )  // Balance: Activo (II) - Activo - Accionistas (socios) por desembolsos exigidos [158]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA159 )  // Balance: Activo (II) - Activo - Otros deudores [159]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA160 )  // Balance: Activo (II) - Activo - Inversiones en empresas del grupo y asociadas a corto plazo [160]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA161 )  // Balance: Activo (II) - Activo - Instrumentos de patrimonio  [161]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA162 )  // Balance: Activo (II) - Activo - Créditos a empresas  [162]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA163 )  // Balance: Activo (II) - Activo - Valores representativos de deuda  [163]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA164 )  // Balance: Activo (II) - Activo - Derivados  [164]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA165 )  // Balance: Activo (II) - Activo - Otros activos financieros  [165]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA166 )  // Balance: Activo (II) - Activo - Otras inversiones  [166]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA167 )  // Balance: Activo (II) - Activo - Resto  [167]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA168 )  // Balance: Activo (II) - Activo - Inversiones financieras a corto plazo  [168]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA169 )  // Balance: Activo (II) - Activo - Instrumentos de patrimonio  [169]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA170 )  // Balance: Activo (II) - Activo - Créditos a empresas  [170]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA171 )  // Balance: Activo (II) - Activo - Valores representativos de deuda [171]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA172 )  // Balance: Activo (II) - Activo - Derivados [172]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA173 )  // Balance: Activo (II) - Activo - Otros activos financieros [173]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA174 )  // Balance: Activo (II) - Activo - Otras inversiones [174]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA175 )  // Balance: Activo (II) - Activo - Resto [175]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA176 )  // Balance: Activo (II) - Activo - Periodificaciones a corto plazo [176]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA177 )  // Balance: Activo (II) - Activo - Efectivo y otros activos líquidos equivalentes [177]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA178 )  // Balance: Activo (II) - Activo - Tesorería [178]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA179 )  // Balance: Activo (II) - Activo - Otros activos líquidos equivalentes [179]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BA180 )  // Balance: Activo (II) - Activo - TOTAL ACTIVO [180]				
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})

		,PAG05 ("T200050", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP185 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - PATRIMONIO NETO [185]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP186 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Fondos propios [186]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP187 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Capital [187]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP188 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Capital escriturado [188]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP189 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Capital no exigido [189]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP190 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Prima de emisión [190]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP191 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Reservas [191]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP192 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Legal y estatutarias [192]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP193 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras reservas [193]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP702 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Reserva de revalorización [702]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP194 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Acciones y participaciones en patrimonio propias [194]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP195 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Resultados de ejercicios anteriores [195]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP196 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Remanente [196]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP197 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Resultados negativos de ejercicios anteriores [197]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP198 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras aportaciones de socios [198]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP199 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Resultado del ejercicio [199]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP200 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Dividendo a cuenta [200]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP201 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otros instrumentos de patrimonio neto [201]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP202 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Ajustes por cambios de valor [202]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP203 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Activos financieros disponibles para la venta [203]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP204 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Operaciones de cobertura [204]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP205 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Activos no corrientes y pasivos vinculados [205]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP206 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Diferencia de conversión [206]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP207 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otros  [207]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP208 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Ajustes en patrimonio neto [208]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP209 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Subvenciones, donaciones y legados recibidos [209]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP210 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - PASIVO NO CORRIENTE [210]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP211 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Provisiones a largo plazo  [211]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP212 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Obligaciones por prestaciones a largo plazo al personal  [212]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP213 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Actuaciones medioambientales  [213]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP214 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Provisiones por reestructuración  [214]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP215 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras provisiones  [215]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP216 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deudas a largo plazo  [216]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP217 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Obligaciones y otros valores negociables  [217]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP218 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deudas con entidades de crédito  [218]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP219 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Acreedores por arrendamiento financiero  [219]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP220 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Derivados  [220]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP221 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otros pasivos financieros [221]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP222 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras deudas a largo plazo [222]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP223 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deudas con empresas del grupo y asociadas a largo plazo [223]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP224 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Pasivos por impuesto diferido [224]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP225 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Periodificaciones a largo plazo [225]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP226 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Acreedores comerciales no corrientes [226]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP227 )  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deuda con características especiales a largo plazo [227]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})

		,PAG06 ("T200060", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
            ,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria			 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP228 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - PASIVO CORRIENTE [228]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP229 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Pasivos vinculados con activos no corrientes [229]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP230 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Provisiones a corto plazo [230]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP703 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Provisiones por derechos emisión de gases de efecto invernadero [703]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP704 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otras provisiones [704]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP231 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deudas a corto plazo [231]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP232 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Obligaciones y otros valores negociables [232]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP233 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deudas con entidades de crédito [233]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP234 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Acreedores por arrendamiento financiero [234]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP235 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Derivados [235]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP236 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otros pasivos financieros [236]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP237 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otras deudas a corto plazo [237]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP238 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deudas con empresas del grupo y asociadas a corto plazo [238]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP239 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Acreedores comerciales y otras cuentas a pagar [239]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP240 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores [240]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP241 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores - Proveedores a largo plazo [241]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP242 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores - Proveedores a corto plazo [242]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP243 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores, empresas del grupo y asociadas [243]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP244 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Acreedores varios [244]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP245 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Personal (remuneraciones pendientes de pago) [245]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP246 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Pasivos por impuesto corriente [246]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP247 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otras deudas con las Administraciones Públicas [247]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP248 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Anticipos de clientes [248]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP249 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otros acreedores [249]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP250 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Periodificaciones a corto plazo [250]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP251 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deuda con características especiales a corto plazo  [251]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BP252 )  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - TOTAL PATRIMONIO NETO Y PASIVO [252]				
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
				
		,PAG07 ("T200070", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG255 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Importe neto de la cifra de negocios [255]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG256 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ventas [256]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG257 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Prestaciones de servicios [257]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG705 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding [705]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG706 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding  - De participaciones en instrumentos patrimonio [706]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG707 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding  - De valores negociables y otros instrumentos financieros [707]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG708 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding  - Resto [708]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG258 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Variación de existencias [258]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG259 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Trabajos realizados por la empresa para su activo [259]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG260 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Aprovisionamientos [260]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG261 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Consumo de mercaderías [261]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG262 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Consumo de materias primas y otras materias consumibles [262]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG263 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Trabajos realizados por otras empresas [263]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG264 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro de mercaderías, materias primas [264]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG265 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros ingresos de explotación [265]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG266 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos accesorios y otros de gestión corriente [266]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG267 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos accesorios y otros de gestión corriente - Ingresos arrendamientos [267]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG268 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos accesorios y otros de gestión corriente - Resto [268]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG269 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Subvenciones de explotación [269]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG270 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Gastos de personal  [270]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG271 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Sueldos, salarios y asimilados [271]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG273 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Indemnizaciones [273]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG274 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Seguridad Social a cargo de la empresa [274]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG275 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Retribuciones a largo plazo por sistemas de aportación [275]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG276 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Retribuciones mediante instrumentos de patrimonio [276]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG277 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros gastos sociales [277]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG278 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Provisiones [278]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG279 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros gastos de explotación [279]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG280 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Servicios exteriores [280]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG281 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Tributos [281]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG282 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Pérdidas, deterioro y variación de provisiones por operaciones comerciales [282]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG283 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros gastos de gestión corriente [283]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG709 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Gastos por emisión de gases de efecto invernadero [709]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG284 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Amortización del inmovilizado [284]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG285 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Imputación de subvenciones de inmovilizado no financiero y otras [285]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG286 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Excesos de provisiones [286]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG287 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y resultado por enajenaciones del inmovilizado [287]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG288 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y pérdidas [288]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG289 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y pérdidas - Deterioros [289]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG290 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y pérdidas - Reversión de deterioros [290]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG291 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Resultados por enajenaciones y otras [291]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG292 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Resultados por enajenaciones y otras - Beneficios [292]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG293 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Resultados por enajenaciones y otras - Pérdidas [293]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG710 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y resultados por enajenaciones del inmovilizado de las sociedades holding [710]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG294 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Diferencia negativa de combinaciones de negocio [294]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG295 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros resultados [295]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG296 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - RESULTADO DE EXPLOTACION [296]				
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})

		,PAG08 ("T200080", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
		    ,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG297 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos financieros [297]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG298 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De participaciones en instrumentos de patrimonio [298]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG299 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De participaciones en instrumentos de patrimonio - En empresas del grupo y asociadas [299]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG300 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De participaciones en instrumentos de patrimonio - En terceros [300]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG301 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De valores negociables y otros instrumentos financieros  [301]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG302 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De valores negociables y otros instrumentos financieros  - De empresas del grupo y asociadas  [302]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG303 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De valores negociables y otros instrumentos financieros  - De terceros  [303]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG304 )  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Imputación de subvenciones, donaciones y legados  [304]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG305 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Gastos financieros [305]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG306 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Por deudas con empresas del grupo y asociadas [306]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG307 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Por deudas con terceros [307]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG308 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Por actualización de provisiones [308]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG309 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Variación de valor razonable en instrumentos financieros [309]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG310 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Cartera de negociación y otros [310]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG311 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Imputación por activos financieros disponibles para la venta  [311]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG312 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Diferencias de cambio [312]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG313 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioro y resultado por enajenaciones de instrumentos financieros   [313]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG314 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas [314]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG315 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Deterioros, empresas del grupo, asociadas y vinculadas [315]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG316 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Deterioros, otras empresas [316]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG317 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Reversión de deterioros, empresas del grupo, asociadas y vinculadas [317]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG318 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Reversión de deterioros, otras empresas [318]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG319 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras [319]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG320 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Beneficios, empresas del grupo, asociadas y vinculadas [320]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG321 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Beneficios, otras empresas [321]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG322 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Pérdidas, empresas del grupo, asociadas y vinculadas  [322]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG323 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Pérdidas, otras empresas  [323]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG329 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Otros ingresos y gastos de carácter financiero [329]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG330 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Incorporación al activo de gastos financieros [330]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG331 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Ingresos financieros derivados de convenios de acreedores [331]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG332 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resto de ingresos y gastos [332] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG324 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - RESULTADO FINANCIERO [324]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG325 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - RESULTADO ANTES DE IMPUESTOS [325]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG326 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Impuestos sobre beneficios  [326]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG327 )  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES CONTINUADAS [327]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG328 )  // Cuenta de pérdidas y ganancias (II) - Operaciones interrumpidas - RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES INTERRUMPIDAS NETO DE IMPUESTOS [328]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.PG500 )  // Cuenta de pérdidas y ganancias (II) - Operaciones interrumpidas - RESULTADO DE LA CUENTA DE PÉRDIDAS Y GANANCIAS [500]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
			
		,PAG09 ("T200090", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0500 )  // Estado de cambios patrimonio neto (I) - Resultado de la cuenta de pérdidas y ganancias  [500]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0336 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por valoración de instrumentos financieros  [336]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0337 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Activos financieros disponibles para la venta [337]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0338 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Otros ingresos/gastos [338]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0339 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por coberturas de flujos de efectivo [339]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0340 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Subvenciones, donaciones y legados recibidos [340]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0341 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por ganancias y pérdidas actuariales [341]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0342 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por activos no corrientes y pasivos vinculados  [342]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0343 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Diferencias de conversión [343]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0344 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Efecto impositivo [344]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0345 )  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Total ingresos y gastos imputados en el patrimonio neto [345]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0346 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Por valoración de instrumentos financieros [346]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0347 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Activos financieros disponibles para la venta [347]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0348 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Otros ingresos/gastos [348]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0349 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Por coberturas de flujos de efectivo [349]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0350 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Subvenciones, donaciones y legados recibidos [350]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0351 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Por activos no corrientes y pasivos vinculados [351]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0352 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Diferencias de conversión [352]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0353 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Efecto impositivo [353]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0354 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Total transferencia a la cuenta de pérdidas y ganancias [354]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.T0355 )  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - TOTAL DE INGRESOS Y GASTOS RECONOCIDOS [355]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
			
		,PAG10 ("T200100", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC380 )  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Capital - Escriturado [380]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC381 )  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Capital - No exigido  [381]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC382 )  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Prima de emisión  [382]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC383 )  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Reservas  [383]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC384 )  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Acciones y participaciones propias  [384]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC385 )  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Resultados ejercicios anteriores  [385]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC386 )  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Otras aportaciones socios  [386]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC394 )  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Capital - Escriturado  [394]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC395 )  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Capital - No exigido [395]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC396 )  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Prima de emisión [396]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC397 )  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Reservas [397]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC398 )  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Acciones y participaciones propias [398]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC399 )  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Resultados ejercicios anteriores [399]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC400 )  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Otras aportaciones socios [400]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC408 )  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Capital - Escriturado [408]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC409 )  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Capital - No exigido [409]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC410 )  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Prima de emisión [410]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC411 )  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Reservas [411]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC412 )  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Acciones y participaciones propias [412]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC413 )  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Resultados ejercicios anteriores [413]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC414 )  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Otras aportaciones socios [414]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC422 )  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Capital - Escriturado [422]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC423 )  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Capital - No exigido [423]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC424 )  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Prima de emisión [424]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC425 )  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Reservas [425]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC426 )  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Acciones y participaciones propias [426]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC427 )  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Resultados ejercicios anteriores [427]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC428 )  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Otras aportaciones socios [428]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC436 )  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Capital - Escriturado [436]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC437 )  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Capital - No exigido [437]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC438 )  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Prima de emisión [438]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC439 )  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Reservas [439]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC440 )  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Acciones y participaciones propias [440]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC441 )  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Resultados ejercicios anteriores [441]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC442 )  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Otras aportaciones socios [442]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC450 )  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Capital - Escriturado [450]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC451 )  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Capital - No exigido [451]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC452 )  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Prima de emisión [452]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC453 )  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Reservas [453]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC454 )  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Acciones y participaciones propias [454]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC455 )  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Resultados ejercicios anteriores [455]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC456 )  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Otras aportaciones socios [456]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC464 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Capital - Escriturado [464]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC465 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Capital - No exigido [465]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC466 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Prima de emisión [466]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC467 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Reservas [467]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC468 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Acciones y participaciones propias [468]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC469 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Resultados ejercicios anteriores [469]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC470 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otras aportaciones socios [470]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC478 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Capital - Escriturado [478]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC479 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Capital - No exigido [479]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC480 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Prima de emisión [480]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC481 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Reservas [481]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC482 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Acciones y participaciones propias [482]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC483 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Resultados ejercicios anteriores [483]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC484 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Otras aportaciones socios [484]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC492 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Capital - Escriturado [492]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC493 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Capital - No exigido [493]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC494 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Prima de emisión [494]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC495 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Reservas [495]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC496 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Acciones y participaciones propias [496]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC497 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Resultados ejercicios anteriores [497]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC498 )  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Otras aportaciones socios [498]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC506 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Capital - Escriturado [506]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC507 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Capital - No exigido [507]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC508 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Prima de emisión [508]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC509 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Reservas [509]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC510 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Acciones y participaciones propias [510]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC511 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Resultados ejercicios anteriores [511]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC512 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras aportaciones socios [512]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC520 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Capital - Escriturado [520]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC521 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Capital - No exigido [521]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC522 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Prima de emisión [522]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC523 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Reservas [523]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC524 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Acciones y participaciones propias [524]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC525 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Resultados ejercicios anteriores [525]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC526 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Otras aportaciones socios [526]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC534 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Capital - Escriturado [534]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC535 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Capital - No exigido [535]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC536 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Prima de emisión [536]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC537 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Reservas [537]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC538 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Acciones y participaciones propias [538]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC539 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Resultados ejercicios anteriores [539]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC540 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Otras aportaciones socios [540]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC548 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Capital - Escriturado [548]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC549 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Capital - No exigido [549]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC550 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Prima de emisión [550]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC551 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Reservas [551]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC552 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Acciones y participaciones propias [552]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC553 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Resultados ejercicios anteriores [553]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC554 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Otras aportaciones socios [554]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC562 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Capital - Escriturado [562]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC563 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Capital - No exigido [563]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC564 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Prima de emisión [564]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC565 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Reservas [565]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC566 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Acciones y participaciones propias [566]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC567 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Resultados ejercicios anteriores [567]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC568 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Otras aportaciones socios [568]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC576 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Capital - Escriturado [576]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC577 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Capital - No exigido [577]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC578 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Prima de emisión [578]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC579 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Reservas [579]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC580 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Acciones y participaciones propias [580]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC581 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Resultados ejercicios anteriores [581]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC582 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Otras aportaciones socios [582]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC590 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Capital  - Escriturado [590]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC591 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Capital  - No exigido [591]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC592 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Prima de emisión [592]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC593 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Reservas [593]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC594 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Acciones y participaciones propias [594]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC595 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Resultados ejercicios anteriores [595]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC596 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Otras aportaciones socios [596]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC604 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Capital -  Escriturado [604]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC605 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Capital - No exigido [605]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC606 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Prima de emisión [606]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC607 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Reservas [607]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC608 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Acciones y participaciones propias [608]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC609 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Resultados ejercicios anteriores [609]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC610 )  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Otras aportaciones socios [610]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC618 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Capital - Escriturado [618]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC619 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Capital - No exigido [619]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC620 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Prima de emisión [620]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC621 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Reservas [621]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC622 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Acciones y participaciones propias [622]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC623 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Resultados ejercicios anteriores [623]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC624 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras aportaciones socios [624]  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC715 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Capital - Escriturado [715]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC716 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización -  Capital - No exigido [716]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC717 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Prima de emisión [717]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC718 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización -  Reservas [718]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC719 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Acciones y participaciones propias [719]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC720 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Resultados ejercicios anteriores [720]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC721 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Otras aportaciones socios [721]  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC729 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Capital - Escriturado [729]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC730 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones -  Capital - No exigido [730]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC731 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Prima de emisión [731]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC732 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones -  Reservas [732]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC733 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Acciones y participaciones propias [733]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC734 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Resultados ejercicios anteriores [734]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC735 )  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Otras aportaciones socios [735]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC632 )  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Capital - Escriturado [632]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC633 )  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Capital - No exigido [633]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC634 )  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Prima de emisión [634]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC635 )  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Reservas [635]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC636 )  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Acciones y participaciones propias [636]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC637 )  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Resultados ejercicios anteriores [637]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC638 )  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Otras aportaciones socios [638]				
		    ,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
			
		,PAG11 ("T200110", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC387 )  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Resultado del ejercicio [387]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC388 )  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Dividendo a cuenta [388]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC389 )  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Otros instrumentos patrimonio neto [389]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC390 )  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Ajustes por cambios de valor [390]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC391 )  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Ajustes en patrimonio neto [391]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC392 )  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Subvenciones, donaciones y legados recibidos [392]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC393 )  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Total [393]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC401 )  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Resultado del ejercicio [401]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC402 )  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Dividendo a cuenta [402]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC403 )  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Otros instrumentos patrimonio neto [403]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC404 )  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Ajustes por cambios de valor [404]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC405 )  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Ajustes en patrimonio neto [405]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC406 )  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Subvenciones, donaciones y legados recibidos [406]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC407 )  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Total [407]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC415 )  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Resultado del ejercicio [415]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC416 )  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Dividendo a cuenta [416]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC417 )  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Otros instrumentos patrimonio neto [417]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC418 )  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Ajustes por cambios de valor [418]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC419 )  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Ajustes en patrimonio neto [419]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC420 )  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Subvenciones, donaciones y legados recibidos [420]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC421 )  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Total [421]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC429 )  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Resultado del ejercicio [429]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC430 )  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Dividendo a cuenta [430]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC431 )  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Otros instrumentos patrimonio neto [431]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC432 )  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Ajustes por cambios de valor [432]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC433 )  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Ajustes en patrimonio neto [433]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC434 )  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Subvenciones, donaciones y legados recibidos [434]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC435 )  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Total [435]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC443 )  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Resultado del ejercicio [443]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC444 )  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Dividendo a cuenta [444]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC445 )  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Otros instrumentos patrimonio neto [445]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC446 )  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Ajustes por cambios de valor [446]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC448 )  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Subvenciones, donaciones y legados recibidos [448]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC449 )  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Total [449]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC457 )  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Resultado del ejercicio [457]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC458 )  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Dividendo a cuenta [458]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC461 )  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Ajustes en patrimonio neto [461]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC462 )  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Subvenciones, donaciones y legados recibidos [462]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC463 )  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Total [463]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC471 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Resultado del ejercicio [471]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC472 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Dividendo a cuenta [472]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC475 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ajustes en patrimonio neto [475]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC476 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Subvenciones, donaciones y legados recibidos [476]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC477 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Total [477]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC485 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Resultado del ejercicio [485]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC486 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Dividendo a cuenta [486]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC489 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Ajustes en patrimonio neto [489]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC490 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Subvenciones, donaciones y legados recibidos [490]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC491 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Total [491]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC499 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Resultado del ejercicio [499]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC502 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Dividendo a cuenta [502]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC503 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Ajustes en patrimonio neto [503]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC504 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Subvenciones, donaciones y legados recibidos [504]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC505 )  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Total [505]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC513 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Resultado del ejercicio [513]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC514 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Dividendo a cuenta [514]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC515 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otros instrumentos patrimonio neto [515]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC516 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Ajustes por cambios de valor [516]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC517 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Ajustes en patrimonio neto [517]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC518 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Subvenciones, donaciones y legados recibidos [518]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC519 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Total [519]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC527 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Resultado del ejercicio [527]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC528 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Dividendo a cuenta [528]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC529 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Otros instrumentos patrimonio neto [529]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC530 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Ajustes por cambios de valor [530]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC531 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Ajustes en patrimonio neto [531]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC532 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Subvenciones, donaciones y legados recibidos [532]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC533 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Total [533]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC541 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Resultado del ejercicio [541]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC542 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Dividendo a cuenta [542]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC543 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Otros instrumentos patrimonio neto [543]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC544 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Ajustes por cambios de valor [544]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC545 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Ajustes en patrimonio neto [545]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC546 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Subvenciones, donaciones y legados recibidos [546]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC547 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Total [547]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC555 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Resultado del ejercicio [555]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC556 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Dividendo a cuenta [556]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC557 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Otros instrumentos patrimonio neto [557]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC558 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Ajustes por cambios de valor [558]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC560 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Subvenciones, donaciones y legados recibidos [560]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC561 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Total [561]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC569 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Resultado del ejercicio [569]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC570 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Dividendo a cuenta [570]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC571 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Otros instrumentos patrimonio neto [571]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC572 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Ajustes por cambio de valor [572]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC574 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Subvenciones, donaciones y legados recibidos [574]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC575 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Total [575]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC583 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Resultado del ejercicio [583]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC584 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Dividendo a cuenta [584]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC585 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Otros instrumentos patrimonio neto [585]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC586 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Ajustes por cambio de valor [586]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC588 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Subvenciones, donaciones y legados recibidos [588]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC589 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Total [589]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC597 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Resultado del ejercicio [597]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC598 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Dividendo a cuenta [598]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC599 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Otros instrumentos patrimonio neto [599]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC600 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Ajustes por cambios de valor [600]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC602 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Subvenciones, donaciones y legados recibidos [602]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC603 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Total [603]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC611 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Resultado del ejercicio  [611]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC612 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Dividendo a cuenta  [612]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC613 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Otros instrumentos patrimonio neto [ [613]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC614 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Ajustes por cambios de valor [614]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC615 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Ajustes en patrimonio neto [615]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC616 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Subvenciones, donaciones y legados recibidos  [616]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC617 )  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Total [617]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC625 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Resultado del ejercicio [625]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC626 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Dividendo a cuenta  [626]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC627 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otros instrumentos patrimonio neto [ [627]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC628 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Ajustes por cambios de valor [628]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC629 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Ajustes en patrimonio neto [629]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC630 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Subvenciones, donaciones y legados recibidos  [630]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC631 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Total [631]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC722 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Resultado del ejercicio [722]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC723 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Dividendo a cuenta  [723]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC724 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Otros instrumentos patrimonio neto [ [724]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC725 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Ajustes por cambios de valor [725]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC726 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Ajustes en patrimonio neto [726]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC727 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Subvenciones, donaciones y legados recibidos  [727]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC728 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Total [728]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC736 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Resultado del ejercicio [736]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC737 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Dividendo a cuenta  [737]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC738 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Otros instrumentos patrimonio neto [ [738]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC739 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Ajustes por cambios de valor [739]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC740 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Ajustes en patrimonio neto [740]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC741 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Subvenciones, donaciones y legados recibidos  [741]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC742 )  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Total [742]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC639 )  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Resultado del ejercicio [639]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC640 )  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Dividendo a cuenta [640]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC641 )  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Otros instrumentos patrimonio neto [641]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC642 )  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Ajustes por cambios de valor [642]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC643 )  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Ajustes en patrimonio neto [643]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC644 )  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Subvenciones, donaciones y legados recibidos [644]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TC645 )  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Total [645]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})

		,PAG12 ("T200120", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ500 )  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Resultado de la cuenta de pérdidas y ganancias [500]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ301 )  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Correcciones por Impuesto Sociedades - Aumentos [301]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ302 )  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Correcciones por Impuesto Sociedades - Disminuciones [302]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ501 )  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Resultado cuenta pérdidas y ganancias antes de Impuesto Sociedades [501]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0303 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Diferencias entre amortización contable y fiscal - Aumentos [303]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0304 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Diferencias entre amortización contable y fiscal - Disminuciones [304]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0504 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - 30% importe gastos amortiz. contable - Aumentos [504]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0505 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - 30% importe gastos amortiz. contable - Disminuciones [505]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0305 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Amortización inmovilizado afecto investigación y desarrollo - Aumentos [305]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0306 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Amortización inmovilizado afecto investigación y desarrollo - Disminuciones [306]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0307 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización de gastos de investigación y desarrollo - Aumentos [307]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0308 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización de gastos de investigación y desarrollo - Disminuciones [308]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0514 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización con mantenimiento de empleo - Aumentos [514]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0509 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización con mantenimiento de empleo - Disminuciones [509]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0516 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización sin mantenimiento de empleo - Aumentos [516]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0551 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización sin mantenimiento de empleo - Disminuciones [551]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0309 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otros supuestos de libertad de amortización - Aumentos [309]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0310 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otros supuestos de libertad de amortización - Disminuciones [310]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0311 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:libertad amortización - Aumentos [311]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0312 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:libertad amortización - Disminuciones [312]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0313 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:amortización acelerada - Aumentos [313]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0314 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:amortización acelerada - Disminuciones [314]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0315 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Cesión de bienes con opción de compra - Aumentos [315]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0316 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Cesión de bienes con opción de compra - Disminuciones [316]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0317 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Arrendamiento financiero: régimen especial - Aumentos [317]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0318 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Arrendamiento financiero: régimen especial - Disminuciones [318]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0319 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro no justificadas valor de fondos editoriales, fonográficos y audiovisuales - Aumentos [319]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0320 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro no justificadas valor de fondos editoriales, fonográficos y audiovisuales - Disminuciones [320]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0321 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro del art.12.2 LIS no afectada por el art.19.13 LIS - Aumentos [321]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0322 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro del art.12.2 LIS no afectada por el art.19.13 LIS - Disminuciones [322]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0415 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro del art.12.2 LIS y provisiones y gastos - Aumentos [415]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0211 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro del art.12.2 LIS y provisiones y gastos - Disminuciones [211]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0416 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Aplicación del límite del art.19.13 LIS - Aumentos [416]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0543 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Aplicación del límite del art.19.13 - Disminuciones [543]			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0323 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión: pérdidas por deterioro créditos insolvencias - Aumentos [323]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0324 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión: pérdidas por deterioro creditos insolvencias - Disminuciones [324]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0325 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Ajustes por deterioro valores representativos partic.capital o fondos propios - Aumentos [325]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0326 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Ajustes por deterioro valores representativos partic.capital o fondos propios - Disminuciones [326]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0327 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro de valores representativos de deuda - Aumentos [327]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0328 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro de valores representativos de deuda - Disminuciones [328]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0329 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Adquisición de participaciones en entidades no residentes - Aumentos [329]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0330 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Adquisición de participaciones en entidades no residentes - Disminuciones [330]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0331 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del fondo de comercio - Aumentos [331]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0332 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del fondo de comercio - Disminuciones [332]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0333 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del intangible de vida útil indefinida - Aumentos [333]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0334 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del intangible de vida útil indefinida - Disminuciones [334]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0335 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Provisiones y gastos por pensiones - Aumentos [335]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0336 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Provisiones y gastos por pensiones - Disminuciones [336]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0337 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otras provisiones no deducibles fiscalmente - Aumentos [337]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0338 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otras provisiones no deducibles fiscalmente - Disminuciones [338]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0339 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos por donativos y liberalidades - Aumentos [339]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0341 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones realizadas con paraísos fiscales - Aumentos [341]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0342 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones realizadas con paraísos fiscales - Disminuciones [342]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0508 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos financieros derivados de deudas con entidades del grupo - Aumentos [508]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0510 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro valores representativos partic. capital o fondos propios - Aumentos [510] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0511 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro valores representativos partic. capital o fondos propios - Disminuciones [511] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0512 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Rentas negativas obtenidas en el extranjero a través de E.P - Aumentos [512] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0513 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Rentas negativas obtenidas en el extranjero a través de E.P - Disminuciones  [513] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0343 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Otros gastos no deducibles - Aumentos [343]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0184 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Rentas negativas obtenidas por miembros de una UTE en el extranjero - Aumentos [184]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0544 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Rentas negativas obtenidas por miembros de una UTE en el extranjero - Disminuciones [544]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0345 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Revalorizaciones contables - Aumentos [345]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0346 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Revalorizaciones contables - Disminuciones [346]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0347 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Aplicación del valor normal de mercado - Aumentos [347]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0348 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Aplicación del valor normal de mercado - Disminuciones [348]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0349 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ingresos por donaciones y legados otorgados por terceros - Aumentos [349]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0350 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ingresos por donaciones y legados otorgados por terceros - Disminuciones [350]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0352 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Correción de rentas por depreciación monetaria - Disminuciones [352]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0354 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos por operaciones con acciones propias - Disminuciones [354]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0355 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Errores contables - Aumentos [355]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0356 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Errores contables - Disminuciones [356]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0357 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones a plazos - Aumentos [357]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0358 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones a plazos - Disminuciones [358]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0359 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reversión del deterioro del valor de elementos patrimoniales - Aumentos [359]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0360 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reversión del deterioro del valor de elementos patrimoniales - Disminuciones [360]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0225 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Rentas negativas - Aumentos [225]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0226 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Rentas negativas - Disminuciones [226]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0545 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Ajuste por rentas derivadas de operaciones con quita o espera - Aumentos [545]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0272 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Ajuste por rentas derivadas de operaciones con quita o espera - Disminuciones [272]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0361 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Otras diferencias de imputación temporal de ingresos y gastos - Aumentos [361]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0362 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Otras diferencias de imputación temporal de ingresos y gastos - Disminuciones [362]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0363 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ajustes por limitación en deducibilidad en gastos financieros - Aumentos [363]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0364 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ajustes por limitación en deducibilidad en gastos financieros - Disminuciones [364]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0365 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reinversión de beneficios extraordinarios - Aumentos  [365]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0367 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos no deducibles por incompatibilidad con la deducción por reinversión - Aumentos [367]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0369 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Exención por doble imposición internacional (art.21 LIS) - Aumentos [369]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0370 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Exención por doble imposición internacional (art.21 LIS) - Disminuciones [370]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0256 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -   Exención por doble imposición internacional (art.22 LIS y DT 41) - Aumentos [256]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0278 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -   Exención por doble imposición internacional (art.22 LIS y DT 41) - Disminuciones [278]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0372 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reducción de ingresos de activos intangibles - Disminuciones [372]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0373 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Obra benéfico-social cajas de ahorro y fundaciones bancarias - Aumentos [373]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0374 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Obra benéfico-social cajas de ahorro y fundaciones bancarias - Disminuciones [374]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0375 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Agrupaciones interés económico y UTE's - Aumentos [375] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0376 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Agrupaciones interés económico y UTE's - Disminuciones [376] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0377 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Soc. y fondos de capital-riesgo y soc. desarrollo industrial regional - Aumentos [377]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0378 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Soc. y fondos de capital-riesgo y soc. desarrollo industrial regional - Disminuciones [378]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0379 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Valoración bienes y derechos. Régimen especial operaciones reestructuración - Aumentos [379]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0380 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Valoración bienes y derechos. Régimen especial operaciones reestructuración - Disminuciones [380]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0381 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Minería e hidrocarburos : factor agotamiento - Aumentos [381]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0382 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Minería e hidrocarburos : factor agotamiento - Disminuciones [382]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0383 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Hidrocarburos: Amortización inversiones intangibles  y gastos de investigación - Aumentos [383]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0384 )  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Hidrocarburos: Amortización inversiones intangibles y gastos de investigación - Disminuciones [384]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
			
		,PAG13 ("T200130", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0385 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades de tenencia valores extranjeros - Aumentos [385]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0386 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades de tenencia valores extranjeros - Disminuciones [386]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0387 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Transparencia fiscal internacional - Aumentos [387]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0388 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Transparencia fiscal internacional - Disminuciones [388]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0389 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen de entidades parcialmente exentas - Aumentos [389]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0390 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen de entidades parcialmente exentas - Disminuciones [390]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0250 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Aportaciones a favor entidades sin fines lucrativos - Aumentos [250]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0251 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Aportaciones a favor entidades sin fines lucrativos - Disminuciones [251]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0391 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades sin fines lucrativos - Aumentos [391]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0392 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades sin fines lucrativos - Disminuciones [392]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0396 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Montes vecinales en mano común - Disminuciones [396]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0397 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen entidades navieras - Aumentos [397] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0398 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen entidades navieras - Disminuciones [398] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0400 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Cooperativas: Fondo de reserva obligatorio - Disminuciones [400]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0403 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Reservas inversiones en Canarias - Aumentos [403]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0404 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Reservas inversiones en Canarias - Disminuciones [404]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0405 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Diferimiento plusvalías - Aumentos [405] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0406 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Diferimiento plusvalías - Disminuciones [406] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0409 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Entidades rég. atribución rentas constituidas extranjero, presencia territorio español - Aumentos [409]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0410 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias -  Entidades rég. atribución rentas constituidas extranjero, presencia territorio español - Disminuciones [410]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0411 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias -  Correcciones específicas entidades sometidas normativa foral - Aumentos [411]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0412 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias -  Correcciones específicas entidades sometidas normativa foral - Disminuciones [412]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0518 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Exención transmisión bienes inmuebles - Aumentos [518]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0519 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Exención transmisión bienes inmuebles - Disminuciones [519]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0340 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Impuesto extranjero beneficios con cargo a los cuales se pagan dividendos objeto deducción por doble imposición internacional - Aumentos [340]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0351 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Impuesto extranjero soportado por sujeto pasivo, no deducible por afectar rentas con deducción doble imposición - Aumentos [351]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0368 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Subvenciones públicas en el resultado del ejercicio, no integrables en la base imponible - Disminuciones [368]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0371 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - SICAV: reducciones de capital y distribución prima de emisión - Aumentos [371]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0413 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Otras correcciones al resultado cta. pérdidas y ganancias  - Aumentos [413]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0414 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Otras correcciones al resultado cta. pérdidas y ganancias  - Disminuciones [414]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.I0417 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Total correcciones al resultado cta. pérdidas y ganancias - Aumentos [417]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.D0418 )  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Total correcciones al resultado cta. pérdidas y ganancias - Disminuciones [418]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ578 )  // Liquidación II -  Entidades navieras en función del tonelaje - B.I. actividades o rentas en régimen general [578]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ579 )  // Liquidación II -  Entidades navieras en función del tonelaje - B.I. derivada del régimen especial [579]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ550 )  // Liquidación II -  Base imponible - B.I. antes de la compensación de bases imponibles negativas [550]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ547 )  // Liquidación II -  Base imponible - Compensación de bases imponibles negativas períodos anteriores [547]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ552 )  // Liquidación II -  Base imponible - Base imponible  [552]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ553 )  // Liquidación II -  Base imponible - Sólo cooperativas - Resultados cooperativos [553]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ554 )  // Liquidación II -  Base imponible - Sólo cooperativas - Resultados extracooperativos [554]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ555 )  // Liquidación II -  Base imponible - Sólo agrupaciones interés económico y UTE's - Socios residentes [555]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ556 )  // Liquidación II -  Base imponible - Sólo agrupaciones interés económico y UTE's - Socios no residentes [556]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ559 )  // Liquidación II -  Base imponible - Sólo entidades ZEC - B.I. a tipo de gravamen especial [559]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ520 )  // Liquidación II -  Base imponible - Sólo SOCIMIS - Parte B.I. del periodo impositivo que tributa al tipo general [520]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ521 )  // Liquidación II -  Base imponible - Sólo SOCIMIS - Parte B.I. del periodo impositivo que tributa al tipo del 0% [521]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ545 )  // Liquidación II - Base imponible - Rentas correspondientes a quitas por acuerdo con acreedores no vinculados [545]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ593 )  // Liquidación II - Base imponible - Rentas correspondientes a quitas por acuerdo con acreedores no vinculados de cooperativas [593]			
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.LQ558, 4, 2 )  // Liquidación II -  Tipo de gravamen - Tipo de gravamen [558]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ560 )  // Liquidación II - Sólo sociedades cooperativas - Cuota íntegra previa [560]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ210 )  // Liquidación II - Sólo sociedades cooperativas - Pérdidas por deterioro del art.12.2 LIS y provisiones y gastos - Aumentos [210]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ480 )  // Liquidación II - Sólo sociedades cooperativas - Pérdidas por deterioro del art.12.2 LIS y provisiones y gastos - Disminuciones [480]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ408 )  // Liquidación II - Sólo sociedades cooperativas - Aplicación del límite del art.19.13 LIS [408]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ561 )  // Liquidación II - Sólo sociedades cooperativas - Compensación de cuotas por pérdidas de cooperativas [561]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ562 )  // Liquidación II - Cuota íntegra -  Cuota íntegra [562]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
		    
		,PAG14 ("T200140", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN567 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificación por rentas obtenidas en Ceuta y Melilla [567]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN568 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificación por prestación de servicios [568]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN563 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificación rendimientos por venta de bienes corporales producidos en Canarias  [563]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN566 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificaciones sociedades cooperativas  [566]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN576 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificaciones entidades dedicadas al arrendamiento de viviendas [576]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN569 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Otras bonificaciones  [569]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN570 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición - D.I. interna de periodos anteriores aplicada en el ejercicio [570]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN571 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. interna generada y aplicada en el ejercicio actual [571]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN572 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. internacional periodos anteriores aplicada en el ejercicio [572]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN573 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. internacional generada y aplicada ejercicio actual [573]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN575 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - Transparencia fiscal internacional [575]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN577 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. interna intersocietaria al 5/10 % (cooperativas) [577]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN581 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificaciones empresas navieras en Canarias  [581]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN582 )  // Liquidación III - Bonificaciones/Deducciones doble imposición - Cuota íntegra ajustada positiva [582]    
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN583 )  // Liquidación III - Otras deducciones - Apoyo fiscal a la inversión y otras deducciones  [583] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN585 )  // Liquidación III - Otras deducciones - Deducción art.42 L.I.S. y art. 36 ter Ley 43/95  [585]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN584 )  // Liquidación III - Otras deducciones - Deducciones disposición transitoria octava L.I.S. [584]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN588 )  // Liquidación III - Otras deducciones - Deducciones con límite del Capítulo IV Título VI L.I.S. [588]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN082 )  // Liquidación III - Otras deducciones - Deducciones sin límite I+D+i [082]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN565 )  // Liquidación III - Otras deducciones - Deducción donaciones a entidades sin fines de lucro [565]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN590 )  // Liquidación III - Otras deducciones - Deducciones inversión Canarias (Ley 20/1991) [590]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN399 )  // Liquidación III - Otras deducciones - Deducciones especifícas de las entidades sometidas a normativa foral [399]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN592 )  // Liquidación III - Otras deducciones - Cuota líquida positiva [592]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN595 )  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Retenciones e ingresos a cuenta/pagos a cuenta participaciones I.I.C. [595]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN596 )  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Ret. e ingr. a cuenta/pagos a cuenta participaciones I.I.C. imputadas por agrup. de interés economico y UTES [596]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN597 )  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Retenciones sobre premios loterías y apuestas [597]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN083 )  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Abono deducciones I+D+i por insuficiencia de cuota [083]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN599 )  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Cuota del ejercicio a ingresar o a devolver - Estado [599]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN600 )  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Cuota del ejercicio a ingresar o a devolver - D. Forales/Navarra (Totales) [600]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN601 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 1 - Estado [601]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN602 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 1 - D. Forales/Navarra (Totales) [602]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN603 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 2 - Estado [603]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN604 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 2 - D. Forales/Navarra (Totales) [604]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN605 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 3 - Estado [605]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN606 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 3 - D. Forales/Navarra (Totales) [606]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN611 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Cuota diferencial  - Estado [611]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN612 )  // Liquidación III - Pagos fraccionados/Cuota diferencial - Cuota diferencial  - D. Forales/Navarra (Totales) [612]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN615 )  // Liquidación III - Líquido a ingresar o a devolver - Incremento por pérdida beneficios fiscales períodos anteriores  - Estado [615]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN616 )  // Liquidación III - Líquido a ingresar o a devolver - Incremento por pérdida beneficios fiscales períodos anteriores  - D. Forales/Navarra (Totales) [616]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN633 )  // Liquidación III - Líquido a ingresar o a devolver - Incremento por incumplimiento de requisitos SOCIMI  -  Estado [633]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN642 )  // Liquidación III - Líquido a ingresar o a devolver - Incremento por incumplimiento de requisitos SOCIMI  -  D. Forales/Navarra (Totales) [642]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN617 )  // Liquidación III - Líquido a ingresar o a devolver - Intereses de demora  - Estado [617]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN618 )  // Liquidación III - Líquido a ingresar o a devolver - Intereses de demora  - D. Forales/Navarra (Totales) [618]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN619 )  // Liquidación III - Líquido a ingresar o a devolver - Importe ingreso/devolución efectuada de la declaración originaria  - Estado [619]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN620 )  // Liquidación III - Líquido a ingresar o a devolver - Importe ingreso/devolución efectuada de la declaración originaria  - D. Forales/Navarra (Totales) [620]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN621 )  // Liquidación III - Líquido a ingresar o a devolver  - Estado [621]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN622 )  // Liquidación III - Líquido a ingresar o a devolver  - D. Forales/Navarra (Totales) [622]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN150 )  // Liquidación III - Abono por conversión de activos [150]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN506 )  // Liquidación III - Compensación por conversión de activos [506]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})

		,PAG15 ("T200150", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ640 )  // Detalle compensación bases imponibles negativas - 1997 - Pendiente aplicación a principio periodo [640]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ641 )  // Detalle compensación bases imponibles negativas - 1997 - Aplicado en esta liquidación [641]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ548 )  // Detalle compensación bases imponibles negativas - 1997 - Pendiente aplicación en periodos futuros [548]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ643 )  // Detalle compensación bases imponibles negativas - 1998 - Pendiente aplicación a principio periodo [643]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ644 )  // Detalle compensación bases imponibles negativas - 1998 - Aplicado en esta liquidación [644]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ645 )  // Detalle compensación bases imponibles negativas - 1998 - Pendiente aplicación en periodos futuros [645]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ646 )  // Detalle compensación bases imponibles negativas - 1999 - Pendiente aplicación a principio periodo [646]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ647 )  // Detalle compensación bases imponibles negativas - 1999 - Aplicado en esta liquidación [647]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ648 )  // Detalle compensación bases imponibles negativas - 1999 - Pendiente aplicación en periodos futuros [648]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ649 )  // Detalle compensación bases imponibles negativas - 2000 - Pendiente aplicación a principio periodo [649]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ650 )  // Detalle compensación bases imponibles negativas - 2000 - Aplicado en esta liquidación [650]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ651 )  // Detalle compensación bases imponibles negativas - 2000 - Pendiente aplicación en periodos futuros [651]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ652 )  // Detalle compensación bases imponibles negativas - 2001 - Pendiente aplicación a principio periodo [652]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ653 )  // Detalle compensación bases imponibles negativas - 2001 - Aplicado en esta liquidación [653]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ654 )  // Detalle compensación bases imponibles negativas - 2001 - Pendiente aplicación en periodos futuros [654]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ655 )  // Detalle compensación bases imponibles negativas - 2002 - Pendiente aplicación a principio periodo [655]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ656 )  // Detalle compensación bases imponibles negativas - 2002 - Aplicado en esta liquidación [656]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ657 )  // Detalle compensación bases imponibles negativas - 2002 - Pendiente aplicación en periodos futuros [657]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ658 )  // Detalle compensación bases imponibles negativas - 2003 - Pendiente aplicación a principio periodo [658]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ659 )  // Detalle compensación bases imponibles negativas - 2003 - Aplicado en esta liquidación [659]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ660 )  // Detalle compensación bases imponibles negativas - 2003 - Pendiente aplicación en periodos futuros [660]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ661 )  // Detalle compensación bases imponibles negativas - 2004 - Pendiente aplicación a principio periodo [661]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ662 )  // Detalle compensación bases imponibles negativas - 2004 - Aplicado en esta liquidación [662]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ663 )  // Detalle compensación bases imponibles negativas - 2004 - Pendiente aplicación en periodos futuros [663]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ664 )  // Detalle compensación bases imponibles negativas - 2005 - Pendiente aplicación a principio periodo [664]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ665 )  // Detalle compensación bases imponibles negativas - 2005 - Aplicado en esta liquidación [665]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ666 )  // Detalle compensación bases imponibles negativas - 2005 - Pendiente aplicación en periodos futuros [666]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ667 )  // Detalle compensación bases imponibles negativas - 2006 - Pendiente aplicación a principio periodo [667]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ668 )  // Detalle compensación bases imponibles negativas - 2006 - Aplicado en esta liquidación [668]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ669 )  // Detalle compensación bases imponibles negativas - 2006 - Pendiente aplicación en periodos futuros [669]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ743 )  // Detalle compensación bases imponibles negativas - 2007 - Pendiente aplicación a principio periodo [743]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ747 )  // Detalle compensación bases imponibles negativas - 2007 - Aplicado en esta liquidación [747]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ748 )  // Detalle compensación bases imponibles negativas - 2007 - Pendiente aplicación en periodos futuros [748]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ275 )  // Detalle compensación bases imponibles negativas - 2008 - Pendiente aplicación a principio periodo [275]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ276 )  // Detalle compensación bases imponibles negativas - 2008 - Aplicado en esta liquidación [276]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ277 )  // Detalle compensación bases imponibles negativas - 2008 - Pendiente aplicación en periodos futuros [277]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ608 )  // Detalle compensación bases imponibles negativas - 2009 - Pendiente de aplicación a principio del periodo [608]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ609 )  // Detalle compensación bases imponibles negativas - 2009 - Aplicado en esta liquidación [609]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ610 )  // Detalle compensación bases imponibles negativas - 2009 - Pendiente aplicación en periodos futuros [610]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ704 )  // Detalle compensación bases imponibles negativas - 2010 - Pendiente aplicación a principio periodo [704]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ705 )  // Detalle compensación bases imponibles negativas - 2010 - Aplicado en esta liquidación [705]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ706 )  // Detalle compensación bases imponibles negativas - 2010 - Pendiente aplicación en periodos futuros [706]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ013 )  // Detalle compensación bases imponibles negativas - 2011 - Pendiente aplicación a principio periodo [013]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ014 )  // Detalle compensación bases imponibles negativas - 2011 - Aplicado en esta liquidación [014]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ015 )  // Detalle compensación bases imponibles negativas - 2011 - Pendiente aplicación en periodos futuros [015]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ725 )  // Detalle compensación bases imponibles negativas - 2012 - Pendiente aplicación a principio periodo  [725]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ726 )  // Detalle compensación bases imponibles negativas - 2012 - Aplicado en esta liquidación [726]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ727 )  // Detalle compensación bases imponibles negativas - 2012 - Pendiente aplicación en periodos futuros [727]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ534 )  // Detalle compensación bases imponibles negativas - 2013 - Pendiente aplicación a principio periodo  [534]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ535 )  // Detalle compensación bases imponibles negativas - 2013 - Aplicado en esta liquidación [535]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ536 )  // Detalle compensación bases imponibles negativas - 2013 - Pendiente aplicación en periodos futuros [536]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ607 )  // Detalle compensación bases imponibles negativas - 2014 (*) - Pendiente aplicación a principio periodo  [607]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ675 )  // Detalle compensación bases imponibles negativas - 2014 (*) - Aplicado en esta liquidación [675]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ699 )  // Detalle compensación bases imponibles negativas - 2014 (*) - Pendiente aplicación en periodos futuros [699]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ670 )  // Detalle compensación bases imponibles negativas - TOTAL - Pendiente aplicación a principio periodo [670]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ547 )  // Detalle compensación bases imponibles negativas - TOTAL - Aplicado en esta liquidación [547]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ671 )  // Detalle compensación bases imponibles negativas - TOTAL - Pendiente de aplicación en periodos futuros [671]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN101 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2007 - Deducción pendiente/generada [101]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN102, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2007 - Tipo de gravamen periodo generación [102]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN696 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2007 - 2014 Deducción pendiente [696]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN697 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2007 - Deducción aplicada en esta liquidación [697]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN104 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2008 - Deducción pendiente/generada [104]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN105, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2008 - Tipo de gravamen periodo generación [105]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN846 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2008 - 2014 Deducción pendiente [846]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN847 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2008 - Deducción aplicada en esta liquidación [847]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN848 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2008 - Deducción pendiente períodos futuros [848]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN106 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2009 - Deducción pendiente/generada [106]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN107, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2009 - Tipo de gravamen periodo generación [107]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN282 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2009 - 2014 Deducción pendiente [282]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN283 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2009 - Deducción aplicada en esta liquidación [283]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN284 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2009 - Deducción pendiente períodos futuros [284]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN108 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2010 - Deducción pendiente/generada [108]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN109, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2010 - Tipo de gravamen periodo generación [109]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN702 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2010 - 2014 Deducción pendiente [702]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN703 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2010 - Deducción aplicada en esta liquidación [703]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN707 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2010 - Deducción pendiente períodos futuros [707]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN110 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2011 - Deducción pendiente/generada [110]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN111, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2011 - Tipo de gravamen periodo generación [111]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN071 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2011 - 2014 Deducción pendiente [071]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN187 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2011 - Deducción aplicada en esta liquidación [187]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN300 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2011 - Deducción pendiente períodos futuros [300]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN112 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2012 - Deducción pendiente/generada [112]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN113, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2012 - Tipo de gravamen periodo generación [113]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN025 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2012 - 2014 Deducción pendiente [025]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN026 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2012 - Deducción aplicada en esta liquidación [026]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN027 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2012 - Deducción pendiente períodos futuros [027]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN114 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2013 - Deducción pendiente/generada [114]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN115, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2013 - Tipo de gravamen periodo generación [115]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN714 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2013 - 2014 Deducción pendiente [714]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN715 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2013 - Deducción aplicada en esta liquidación [715]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN716 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2013 - Deducción pendiente períodos futuros [716]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN735 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2014 (*) - Deducción pendiente/generada [735]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN920, 4, 2 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2014 (*) - Tipo de gravamen periodo generación [920]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN736 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2014 (*) - 2014 Deducción pendiente [736]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN737 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2014 (*) - Deducción aplicada en esta liquidación [737]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN738 )  // Deducciones doble imposición interna 2007-2014 - DI interna 2014 (*) - Deducción pendiente períodos futuros [738]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN116 )  // Deducciones doble imposición interna - Total 2007-2014 - Deducción pendiente/generada [116]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN117 )  // Deducciones doble imposición interna - Total 2007-2014 - 2014 Deducción pendiente [117]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN570 )  // Deducciones doble imposición interna - Total 2007-2014 - Deducción aplicada en esta liquidación [570]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN118 )  // Deducciones doble imposición interna - Total 2007-2014 - Deducción pendiente períodos futuros [118]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN103, 7, 2 )  // Deducciones doble imposición interna - Tipo de gravamen 2014 [103]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN119 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 50% - Deducción pendiente/generada  [119]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN120 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 50% - 2014 Deducción pendiente [120]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN121 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 50% -  Deducción aplicada en esta liquidación [121]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN122 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 50% -  Deducción pendiente períodos futuros [122]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN123 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 100% - Deducción pendiente/generada [123]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN124 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 100% -  2014 Deducción pendiente [124]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN125 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 100% -  Deducción aplicada en esta liquidación [125]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN126 )  // Deducciones doble imposición interna - DI interna 2014 - Intersoc.al 100% -  Deducción pendiente períodos futuros [126]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN127 )  // Deducciones doble imposición interna - DI interna 2014 - Plusvalías fuente interna - Deducción pendiente/generada [127]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN128 )  // Deducciones doble imposición interna - DI interna 2014 - Plusvalías fuente interna - 2014 Deducción pendiente [128]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN129 )  // Deducciones doble imposición interna - DI interna 2014 - Plusvalías fuente interna -  Deducción aplicada en esta liquidación [129]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN130 )  // Deducciones doble imposición interna - DI interna 2014 - Plusvalías fuente interna -  Deducción pendiente períodos futuros [130]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN131 )  // Deducciones doble imposición interna - Total 2014 - Deducción pendiente/generada [131]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN132 )  // Deducciones doble imposición interna - Total 2014 - 2014 Deducción pendiente [132]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN571 )  // Deducciones doble imposición interna - Total 2014 - Deducción aplicada en esta liquidación [571]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN133 )  // Deducciones doble imposición interna - Total 2014 - Deducción pendiente períodos futuros [133]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN151 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2004 - Deducción pendiente/generada [151]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN152, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2004 - Tipo de gravamen periodo generación [152]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN711 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2004 - 2014 Deducción pendiente [711]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN712 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2004 - Deducción aplicada en esta liquidación [712]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN153 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2005 - Deducción pendiente/generada [153]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN728, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2005 - Tipo de gravamen periodo generación [728]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN637 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2005 - 2014 Deducción pendiente [637]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN638 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2005 - Deducción aplicada en esta liquidación [638]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN639 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2005 - Deducción pendiente períodos futuros [639]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN154 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2006 - Deducción pendiente/generada [154]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN729, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2006 - Tipo de gravamen periodo generación [729]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN849 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2006 - 2014 Deducción pendiente [849]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN894 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2006 - Deducción aplicada en esta liquidación [894]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN197 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2006 - Deducción pendiente períodos futuros [197]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN155 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2007 - Deducción pendiente/generada [155]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN730, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2007 - Tipo de gravamen periodo generación [730] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN285 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2007 - 2014 Deducción pendiente [285]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN286 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2007 - Deducción aplicada en esta liquidación [286]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN287 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2007 - Deducción pendiente períodos futuros [287]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN156 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2008 - Deducción pendiente/generada [156]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN731, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2008 - Tipo de gravamen periodo generación [731]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN825 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2008 - 2014 Deducción pendiente [825]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN826 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2008 - Deducción aplicada en esta liquidación [826]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN827 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2008 - Deducción pendiente períodos futuros [827]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN157 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2009 - Deducción pendiente/generada [157]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN732, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2009 - Tipo de gravamen periodo generación [732]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN001 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2009 - 2014 Deducción pendiente [001]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN002 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2009 - Deducción aplicada en esta liquidación [002]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN003 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2009- Deducción pendiente períodos futuros [003]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN158 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2010 - Deducción pendiente/generada [158]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN733, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2010 - Tipo de gravamen periodo generación [733]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN028 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2010 - 2014 Deducción pendiente [028]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN029 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2010 - Deducción aplicada en esta liquidación [029]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN030 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2010 - Deducción pendiente períodos futuros [030]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN159 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2011 - Deducción pendiente/generada [159]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN734, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2011 - Tipo de gravamen periodo generación [734]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN717 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2011 - 2014 Deducción pendiente [717]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN718 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2011 - Deducción aplicada en esta liquidación [718]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN719 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2011 - Deducción pendiente períodos futuros [719]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN720 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2012 - Deducción pendiente/generada [720]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN721, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2012 - Tipo de gravamen periodo generación [721]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN722 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2012 - 2014 Deducción pendiente [722]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN723 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2012 - Deducción aplicada en esta liquidación [723]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN724 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2012 - Deducción pendiente períodos futuros [724]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN739 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2013 - Deducción pendiente/generada [739]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN921, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2013 - Tipo de gravamen periodo generación [921]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN740 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2013 - 2014 Deducción pendiente [740]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN741 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2013 - Deducción aplicada en esta liquidación [741]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN742 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2013 - Deducción pendiente períodos futuros [742]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN134 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 (*) - Deducción pendiente/generada [134]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN926, 4, 2 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 (*) - Tipo de gravamen periodo generación [926]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN135 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 (*) - 2014 Deducción pendiente [135]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN136 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 (*) - Deducción aplicada en esta liquidación [136]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN137 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 (*) - Deducción pendiente períodos futuros [137]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN160 )  // Deducciones doble imposición internacional - Total 2004-2014 - Deducción pendiente/generada [160]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN161 )  // Deducciones doble imposición internacional - Total 2004-2014 - 2014 Deducción pendiente [161]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN572 )  // Deducciones doble imposición internacional - Total 2004-2014 - Deducción aplicada en esta liquidación [572]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN162 )  // Deducciones doble imposición internacional - Total 2004-2014 - Deducción pendiente ejercicios futuros [162]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.BN103, 7, 2 )  // Deducciones doble imposición internacional - Tipo de gravamen 2014 [103]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN163 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Impuesto soportado sujeto pasivo - Deducción pendiente/generada [163]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN164 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Impuesto soportado sujeto pasivo - 2014 Deducción pendiente [164]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN165 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Impuesto soportado sujeto pasivo -  Deducción aplicada en esta liquidación [165]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN166 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Impuesto soportado sujeto pasivo -  Deducción pendiente períodos futuros [166]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN167 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Dividendos y participaciones en beneficios  - Deducción pendiente/generada [167]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN168 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Dividendos y participacionesen beneficios  - 2014 Deducción pendiente [168]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN169 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Dividendos y participaciones en beneficios -  Deducción aplicada en esta liquidación [169]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN170 )  // Deducciones doble imposición internacional 2004-2014 - DI internacional 2014 - Dividendos y participaciones en beneficios  -  Deducción pendiente períodos futuros [170]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN171 )  // Deducciones doble imposición internacional 2004-2014 - Total 2014 - Deducción pendiente/generada [171]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN172 )  // Deducciones doble imposición internacional 2004-2014 - Total 2014 - 2014 Deducción pendiente [172]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN573 )  // Deducciones doble imposición internacional 2004-2014 - Total 2014 - Deducción aplicada en esta liquidación [573]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN174 )  // Deducciones doble imposición internacional 2004-2014 - Total 2014 - Deducción pendiente períodos futuros [174]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
			
		,PAG16 ("T200160", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN835 )  // Deducc. Art. 36 ter Ley 43 / 1995.  2002 - Deducción pendiente/generada [835]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN836 )  // Deducc. Art. 36 ter Ley 43 / 1995.  2002 - Aplicado en esta liquidación [836]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN837 )  // Deducc. Art. 36 ter Ley 43 / 1995.  2002 - Pendiente aplicación en periodos futuros [837]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN838 )  // Deducc. Art. 36 ter Ley 43 / 1995.  2003 - Deducción pendiente/generada [838]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN839 )  // Deducc. Art. 36 ter Ley 43 / 1995.  2003 - Aplicado en esta liquidación [839]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN840 )  // Deducc. Art. 36 ter Ley 43 / 1995.  2003 - Pendiente aplicación en periodos futuros [840]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN932 )  // Deducc. Art. 42 L.I.S. 2004 - Deducción pendiente/generada [932]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN933 )  // Deducc. Art. 42 L.I.S. 2004 - Aplicado en esta liquidación [933]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN934 )  // Deducc. Art. 42 L.I.S. 2004 - Pendiente aplicación en periodos futuros [934]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN297 )  // Deducc. Art. 42 L.I.S. 2005 - Deducción pendiente/generada [297]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN298 )  // Deducc. Art. 42 L.I.S. 2005 - Aplicado en esta liquidación [298]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN299)  // Deducc. Art. 42 L.I.S. 2005 - Pendiente aplicación en periodos futuros [299]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN090)  // Deducc. Art. 42 L.I.S. 2006 - Deducción pendiente/generada [090]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN091)  // Deducc. Art. 42 L.I.S. 2006 - Aplicado en esta liquidación [091]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN092)  // Deducc. Art. 42 L.I.S. 2006 - Pendiente aplicación en periodos futuros [092]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN004)  // Deducc. Art. 42 L.I.S. 2007 - Deducción pendiente/generada [004]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN005)  // Deducc. Art. 42 L.I.S. 2007 - Aplicado en esta liquidación [005]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN006)  // Deducc. Art. 42 L.I.S. 2007 - Pendiente aplicación en periodos futuros [006]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN031)  // Deducc. Art. 42 L.I.S. 2008 - Deducción pendiente/generada [031]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN032)  // Deducc. Art. 42 L.I.S. 2008 - Aplicado en esta liquidación [032]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN033)  // Deducc. Art. 42 L.I.S. 2008 - Pendiente aplicación en periodos futuros [033]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN022)  // Deducc. Art. 42 L.I.S. 2009 - Deducción pendiente/generada [022]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN023)  // Deducc. Art. 42 L.I.S. 2009 - Aplicado en esta liquidación [023]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN024)  // Deducc. Art. 42 L.I.S. 2009 - Pendiente aplicación en periodos futuros [024]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN040)  // Deducc. Art. 42 L.I.S. 2010 - Deducción pendiente/generada [040]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN041)  // Deducc. Art. 42 L.I.S. 2010 - Aplicado en esta liquidación [041]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN042)  // Deducc. Art. 42 L.I.S. 2010 - Pendiente aplicación en periodos futuros [042]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN138)  // Deducc. Art. 42 L.I.S. 2011 - Deducción pendiente/generada [138]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN139)  // Deducc. Art. 42 L.I.S. 2011 - Aplicado en esta liquidación [139]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN140)  // Deducc. Art. 42 L.I.S. 2011 - Pendiente aplicación en periodos futuros [140]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN141)  // Deducc. Art. 42 L.I.S. 2012 - Deducción pendiente/generada [141]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN142)  // Deducc. Art. 42 L.I.S. 2012 - Aplicado en esta liquidación [142]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN143)  // Deducc. Art. 42 L.I.S. 2012 - Pendiente aplicación en periodos futuros [143]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN188)  // Deducc. Art. 42 L.I.S. 2013 - Deducción pendiente/generada [188]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN189)  // Deducc. Art. 42 L.I.S. 2013 - Aplicado en esta liquidación [189]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN190)  // Deducc. Art. 42 L.I.S. 2013 - Pendiente aplicación en periodos futuros [190]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN803)  // Deducc. Art. 42 L.I.S. 2014 - Deducción pendiente/generada [803]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN804)  // Deducc. Art. 42 L.I.S. 2014 - Aplicado en esta liquidación [804]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN805)  // Deducc. Art. 42 L.I.S. 2014 - Pendiente aplicación en periodos futuros [805]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN700)  // Deducc. Art. 42 L.I.S. 2014 (*) - Deducción pendiente/generada [700]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN708)  // Deducc. Art. 42 L.I.S. 2014 (*) - Aplicado en esta liquidación [708]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN709)  // Deducc. Art. 42 L.I.S. 2014 (*) - Pendiente aplicación en periodos futuros [709]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN841)  // Deducc. Art. 36 ter Ley 43 / 1995 y 42 L.I.S. Total Deducc. - Deducción pendiente/generada [841]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN585)  // Deducc. Art. 36 ter Ley 43 / 1995 y 42 L.I.S. Total Deducc. - Aplicado en esta liquidación [585]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN843)  // Deducc. Art. 36 ter Ley 43 / 1995 y 42 L.I.S. Total Deducc. - Pendiente aplicación en periodos futuros [843]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN749)  // Deducciones DT octava L.I.S. - 2009 Periodificación - Deducción pendiente/generada [749]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN750)  // Deducciones DT octava L.I.S. - 2009 Periodificación - Aplicado en esta liquidación [750]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN752)  // Deducciones DT octava L.I.S. - 2010 Periodificación - Deducción pendiente/generada [752]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN753)  // Deducciones DT octava L.I.S. - 2010 Periodificación - Aplicado en esta liquidación [753]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN754)  // Deducciones DT octava L.I.S. - 2010 Periodificación - Pendiente de aplicación en periodos futuros [754]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN755)  // Deducciones DT octava L.I.S. - 2011 Periodificación - Deducción pendiente/generada [755]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN756)  // Deducciones DT octava L.I.S. - 2011 Periodificación - Aplicado en esta liquidación [756]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN757)  // Deducciones DT octava L.I.S. - 2011 Periodificación - Pendiente de aplicación en periodos futuros [757]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN758)  // Deducciones DT octava L.I.S. - 2012 Periodificación - Deducción pendiente/generada [758]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN759)  // Deducciones DT octava L.I.S. - 2012 Periodificación - Aplicado en esta liquidación [759]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN760)  // Deducciones DT octava L.I.S. - 2012 Periodificación - Pendiente de aplicación en periodos futuros [760]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN761)  // Deducciones DT octava L.I.S. - 2013 Periodificación - Deducción pendiente/generada [761]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN762)  // Deducciones DT octava L.I.S. - 2013 Periodificación - Aplicado en esta liquidación [762]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN763)  // Deducciones DT octava L.I.S. - 2013 Periodificación - Pendiente de aplicación en periodos futuros [763]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN744)  // Deducciones DT octava L.I.S. - 2014 Periodificación - Deducción pendiente/generada [744]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN745)  // Deducciones DT octava L.I.S. - 2014 Periodificación - Aplicado en esta liquidación [745]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN746)  // Deducciones DT octava L.I.S. - 2014 Periodificación - Pendiente de aplicación en periodos futuros [746]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN779)  // Deducciones DT octava L.I.S. - 2014 (*) Periodificación - Deducción pendiente/generada [779]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN783)  // Deducciones DT octava L.I.S. - 2014 (*) Periodificación - Aplicado en esta liquidación [783]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN784)  // Deducciones DT octava L.I.S. - 2014 (*) Periodificación - Pendiente de aplicación en periodos futuros [784]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN764)  // Deducciones DT octava L.I.S. Total deducciones - Deducción pendiente/generada [764]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN584)  // Deducciones DT octava L.I.S. Total deducciones - Aplicado en esta liquidación [584]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN765)  // Deducciones DT octava L.I.S. Total deducciones - Pendiente de aplicación en periodos futuros [765]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN854)  // Deducciones inversión Canarias - Activos fijos 2009 - Deducción pendiente/generada [854]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN855)  // Deducciones inversión Canarias - Activos fijos 2009 - Aplicado en esta liquidación [855]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN857)  // Deducciones inversión Canarias - Activos fijos 2010 - Deducción pendiente/generada [857]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN858)  // Deducciones inversión Canarias - Activos fijos 2010 - Aplicado en esta liquidación [858]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN859)  // Deducciones inversión Canarias - Activos fijos 2010 - Pendiente de aplicación en periodos futuros [859]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN860)  // Deducciones inversión Canarias - Activos fijos 2011 - Deducción pendiente/generada [860]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN861)  // Deducciones inversión Canarias - Activos fijos 2011 - Aplicado en esta liquidación [861]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN862)  // Deducciones inversión Canarias - Activos fijos 2011 - Pendiente de aplicación en periodos futuros [862]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN863)  // Deducciones inversión Canarias - Activos fijos 2012 - Deducción pendiente/generada [863]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN864)  // Deducciones inversión Canarias - Activos fijos 2012 - Aplicado en esta liquidación [864]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN865)  // Deducciones inversión Canarias - Activos fijos 2012 - Pendiente de aplicación en periodos futuros [865]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN883)  // Deducciones inversión Canarias - Activos fijos 2013 - Deducción pendiente/generada [883]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN884)  // Deducciones inversión Canarias - Activos fijos 2013 - Aplicado en esta liquidación [884]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN885)  // Deducciones inversión Canarias - Activos fijos 2013 - Pendiente de aplicación en periodos futuros [885]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN785)  // Deducciones inversión Canarias - Activos fijos 2014 - Deducción pendiente/generada [785]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN789)  // Deducciones inversión Canarias - Activos fijos 2014 - Aplicado en esta liquidación [789]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN790)  // Deducciones inversión Canarias - Activos fijos 2014 - Pendiente de aplicación en periodos futuros [790]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN088)  // Deducciones inversión Canarias - Inversiones Canarias 1997 - Deducción pendiente/generada [088]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN564)  // Deducciones inversión Canarias - Inversiones Canarias 1997 - Aplicado en esta liquidación [564]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN801)  // Deducciones inversión Canarias - Inversiones Canarias 1997 - Pendiente de aplicación en periodos futuros [801]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN194)  // Deducciones inversión Canarias - Inversiones Canarias 1998 - Deducción pendiente/generada [194]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN195)  // Deducciones inversión Canarias - Inversiones Canarias 1998 - Aplicado en esta liquidación [195]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN196)  // Deducciones inversión Canarias - Inversiones Canarias 1998 - Pendiente de aplicación en periodos futuros [196]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN868)  // Deducciones inversión Canarias - Inversiones Canarias 1999 - Deducción pendiente/generada  [868]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN869)  // Deducciones inversión Canarias - Inversiones Canarias 1999 - Aplicado en esta liquidación [869]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN834)  // Deducciones inversión Canarias - Inversiones Canarias 1999 - Pendiente de aplicación en periodos futuros [834]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN871)  // Deducciones inversión Canarias - Inversiones Canarias 2000 - Deducción pendiente/generada [871] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN872)  // Deducciones inversión Canarias - Inversiones Canarias 2000 - Aplicado en esta liquidación [872]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN873)  // Deducciones inversión Canarias - Inversiones Canarias 2000 - Pendiente de aplicación en periodos futuros [873]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN874)  // Deducciones inversión Canarias - Inversiones Canarias 2001 - Deducción pendiente/generada [874]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN875)  // Deducciones inversión Canarias - Inversiones Canarias 2001 - Aplicado en esta liquidación [875]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN876)  // Deducciones inversión Canarias - Inversiones Canarias 2001 - Pendiente de aplicación en periodos futuros [876]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN877)  // Deducciones inversión Canarias - Inversiones Canarias 2002 - Deducción pendiente/generada [877]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN878)  // Deducciones inversión Canarias - Inversiones Canarias 2002 - Aplicado en esta liquidación [878]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN879)  // Deducciones inversión Canarias - Inversiones Canarias 2002 - Pendiente de aplicación en periodos futuros [879]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN880)  // Deducciones inversión Canarias - Inversiones Canarias 2003 - Deducción pendiente/generada [880]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN881)  // Deducciones inversión Canarias - Inversiones Canarias 2003 - Aplicado en esta liquidación [881]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN882)  // Deducciones inversión Canarias - Inversiones Canarias 2003 - Pendiente de aplicación en periodos futuros [882]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN866)  // Deducciones inversión Canarias - Inversiones Canarias 2004 - Deducción pendiente/generada [866]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN867)  // Deducciones inversión Canarias - Inversiones Canarias 2004 - Aplicado en esta liquidación [867]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN870)  // Deducciones inversión Canarias - Inversiones Canarias 2004 - Pendiente de aplicación en periodos futuros [870]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN939)  // Deducciones inversión Canarias - Inversiones Canarias 2005 - Deducción pendiente/generada [939]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN940)  // Deducciones inversión Canarias - Inversiones Canarias 2005 - Aplicado en esta liquidación [940]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN941)  // Deducciones inversión Canarias - Inversiones Canarias 2005 - Pendiente de aplicación en periodos futuros [941]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN191)  // Deducciones inversión Canarias - Inversiones Canarias 2006 - Deducción pendiente/generada [191]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN192)  // Deducciones inversión Canarias - Inversiones Canarias 2006 - Aplicado en esta liquidación [192]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN193)  // Deducciones inversión Canarias - Inversiones Canarias 2006 - Pendiente de aplicación en periodos futuros [193]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN613)  // Deducciones inversión Canarias - Inversiones Canarias 2007 - Deducción pendiente/generada  [613]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN614)  // Deducciones inversión Canarias - Inversiones Canarias 2007 - Aplicado en esta liquidación [614]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN701)  // Deducciones inversión Canarias - Inversiones Canarias 2007 - Pendiente de aplicación en periodos futuros [701]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN200)  // Deducciones inversión Canarias - Inversiones Canarias 2008 - Deducción pendiente/generada [200]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN257)  // Deducciones inversión Canarias - Inversiones Canarias 2008 - Aplicado en esta liquidación [257]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN011)  // Deducciones inversión Canarias - Inversiones Canarias 2008 - Pendiente de aplicación en periodos futuros [011]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN037)  // Deducciones inversión Canarias - Inversiones Canarias 2009 - Deducción pendiente/generada [037]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN038)  // Deducciones inversión Canarias - Inversiones Canarias 2009 - Aplicado en esta liquidación [038]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN039)  // Deducciones inversión Canarias - Inversiones Canarias 2009 - Pendiente de aplicación en periodos futuros [039]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN044)  // Deducciones inversión Canarias - Inversiones Canarias 2010 - Deducción pendiente/generada [044]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN045)  // Deducciones inversión Canarias - Inversiones Canarias 2010 - Aplicado en esta liquidación [045]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN046)  // Deducciones inversión Canarias - Inversiones Canarias 2010 - Pendiente de aplicación en periodos futuros [046]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN528)  // Deducciones inversión Canarias - Inversiones Canarias 2011 - Deducción pendiente/generada [528]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN529)  // Deducciones inversión Canarias - Inversiones Canarias 2011 - Aplicado en esta liquidación [529]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN530)  // Deducciones inversión Canarias - Inversiones Canarias 2011 - Pendiente de aplicación en periodos futuros [530]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN144)  // Deducciones inversión Canarias - Inversiones Canarias 2012 - Deducción pendiente/generada [144]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN145)  // Deducciones inversión Canarias - Inversiones Canarias 2012 - Aplicado en esta liquidación [145]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN146)  // Deducciones inversión Canarias - Inversiones Canarias 2012 - Pendiente de aplicación en periodos futuros [146]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN147)  // Deducciones inversión Canarias - Inversiones Canarias 2013 - Deducción pendiente/generada [147]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN148)  // Deducciones inversión Canarias - Inversiones Canarias 2013 - Aplicado en esta liquidación [148]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN149)  // Deducciones inversión Canarias - Inversiones Canarias 2013 - Pendiente de aplicación en periodos futuros [149]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN240)  // Deducciones inversión Canarias - Inversiones Canarias 2014 (**) - Deducción pendiente/generada [240]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN241)  // Deducciones inversión Canarias - Inversiones Canarias 2014 (**) - Aplicado en esta liquidación [241]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN242)  // Deducciones inversión Canarias - Inversiones Canarias 2014 (**) - Pendiente de aplicación en periodos futuros [242]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN791)  // Deducciones inversión Canarias - Inversiones Canarias 2014 - Deducción pendiente/generada [791]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN802)  // Deducciones inversión Canarias - Inversiones Canarias 2014 - Aplicado en esta liquidación [802]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN806)  // Deducciones inversión Canarias - Inversiones Canarias 2014 - Pendiente de aplicación en periodos futuros [806]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN852)  // Deducciones inversión Canarias - Activos fijos 2013 - Deducción pendiente/generada [852]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN853)  // Deducciones inversión Canarias - Activos fijos 2013 - Aplicado en esta liquidación [853]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN856)  // Deducciones inversión Canarias - Activos fijos 2013 - Pendiente de aplicación en periodos futuros [856]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN886)  // Deducciones inversión Canarias - Total deducciones - Deducción pendiente/generada [886]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN590)  // Deducciones inversión Canarias - Total deducciones - Aplicado en esta liquidación [590]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN887)  // Deducciones inversión Canarias - Total deducciones - Pendiente de aplicación en periodos futuros [887]			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})

		,PAG17 ("T200170", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN768)  // Deducc. para  incentivar determ.actividades - 1998 Suma deducciones - Deducción pendiente/generada [768]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN769)  // Deducc. para  incentivar determ.actividades - 1998 Suma deducciones - Aplicado en esta liquidación [769]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN770)  // Deducc. para  incentivar determ.actividades - 1998 Suma deducciones - Pendiente de aplicación en periodos futuros [770]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN774)  // Deducc. para  incentivar determ.actividades - 1999 Suma deducciones - Deducción pendiente/generada [774]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN775)  // Deducc. para  incentivar determ.actividades - 1999 Suma deducciones - Aplicado en esta liquidación [775]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN776)  // Deducc. para  incentivar determ.actividades - 1999 Suma deducciones - Pendiente de aplicación en periodos futuros [776]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN780)  // Deducc. para  incentivar determ.actividades - 2000 Suma deducciones - Deducción pendiente/generada [780]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN781)  // Deducc. para  incentivar determ.actividades - 2000 Suma deducciones - Aplicado en esta liquidación [781]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN782)  // Deducc. para  incentivar determ.actividades - 2000 Suma deducciones - Pendiente de aplicación en periodos futuros [782]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN786)  // Deducc. para  incentivar determ.actividades - 2001 Suma deducciones - Deducción pendiente/generada [786]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN787)  // Deducc. para  incentivar determ.actividades - 2001 Suma deducciones - Aplicado en esta liquidación [787]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN788)  // Deducc. para  incentivar determ.actividades - 2001 Suma deducciones - Pendiente de aplicación en periodos futuros [788]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN766)  // Deducc. para  incentivar determ.actividades - 2002 Suma deducciones - Deducción pendiente/generada [766]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN767)  // Deducc. para  incentivar determ.actividades - 2002 Suma deducciones - Aplicado en esta liquidación [767]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN833)  // Deducc. para  incentivar determ.actividades - 2002 Suma deducciones - Pendiente de aplicación en periodos futuros [833]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN198)  // Deducc. para  incentivar determ.actividades - 2003 Suma deducciones - Deducción pendiente/generada [198]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN896)  // Deducc. para  incentivar determ.actividades - 2003 Suma deducciones - Aplicado en esta liquidación [896]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN897)  // Deducc. para  incentivar determ.actividades - 2003 Suma deducciones - Pendiente de aplicación en periodos futuros [897]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN288)  // Deducc. para  incentivar determ.actividades - 2004 Suma deducciones - Deducción pendiente/generada [288]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN289)  // Deducc. para  incentivar determ.actividades - 2004 Suma deducciones - Aplicado en esta liquidación [289]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN290)  // Deducc. para  incentivar determ.actividades - 2004 Suma deducciones - Pendiente de aplicación en periodos futuros [290]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN466)  // Deducc. para  incentivar determ.actividades - 2005 Suma deducciones - Deducción pendiente/generada [466]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN467)  // Deducc. para  incentivar determ.actividades - 2005 Suma deducciones - Aplicado en esta liquidación [467]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN468)  // Deducc. para  incentivar determ.actividades - 2005 Suma deducciones - Pendiente de aplicación en periodos futuros [468]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN061)  // Deducc. para  incentivar determ.actividades - 2006 Suma deducciones - Deducción pendiente/generada [061]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN498)  // Deducc. para  incentivar determ.actividades - 2006 Suma deducciones - Aplicado en esta liquidación [498]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN586)  // Deducc. para  incentivar determ.actividades - 2006 Suma deducciones - Pendiente de aplicación en periodos futuros [586]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN472)  // Deducc. para  incentivar determ.actividades - 2007 Suma deducciones - Deducción pendiente/generada [472]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN473)  // Deducc. para  incentivar determ.actividades - 2007 Suma deducciones - Aplicado en esta liquidación [473]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN478)  // Deducc. para  incentivar determ.actividades - 2007 Suma deducciones - Pendiente de aplicación en periodos futuros [478]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN180)  // Deducc. para  incentivar determ.actividades - 2008 Suma deducciones - Deducción pendiente/generada [180]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN181)  // Deducc. para  incentivar determ.actividades - 2008 Suma deducciones - Aplicado en esta liquidación [181]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN182)  // Deducc. para  incentivar determ.actividades - 2008 Suma deducciones - Pendiente de aplicación en periodos futuros [182]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN531)  // Deducc. para  incentivar determ.actividades - 2009 Suma deducciones - Deducción pendiente/generada [531]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN532)  // Deducc. para  incentivar determ.actividades - 2009 Suma deducciones - Aplicado en esta liquidación [532]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN533)  // Deducc. para  incentivar determ.actividades - 2009 Suma deducciones - Pendiente de aplicación en periodos futuros [533]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN945)  // Deducc. para  incentivar determ.actividades - 2010 Suma deducciones - Deducción pendiente/generada [945]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN946)  // Deducc. para  incentivar determ.actividades - 2010 Suma deducciones - Aplicado en esta liquidación [946]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN947)  // Deducc. para  incentivar determ.actividades - 2010 Suma deducciones - Pendiente de aplicación en periodos futuros [947]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN960)  // Deducc. para incentivar determ.actividades - 2011 Suma deducciones - Deducción pendiente/generada [960]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN961)  // Deducc. para incentivar determ.actividades - 2011 Suma deducciones - Aplicado en esta liquidación [961]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN962)  // Deducc. para incentivar determ.actividades - 2011 Suma deducciones - Pendiente de aplicación en periodos futuros [962]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN183)  // Deducc. para incentivar determ.actividades - 2012 Suma deducciones - Deducción pendiente/generada [183]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN185)  // Deducc. para incentivar determ.actividades - 2012 Suma deducciones - Aplicado en esta liquidación [185]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN186)  // Deducc. para incentivar determ.actividades - 2012 Suma deducciones - Pendiente de aplicación en periodos futuros [186]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN966)  // Deducc. para incentivar determ.actividades - 2013 Suma deducciones - Deducción pendiente/generada [966]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN967)  // Deducc. para incentivar determ.actividades - 2013 Suma deducciones - Aplicado en esta liquidación [967]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN968)  // Deducc. para incentivar determ.actividades - 2013 Suma deducciones - Pendiente de aplicación en periodos futuros [968]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN457)  // Deducc. para incentivar determ.actividades - 2013 Investigación y desarrollo - Deducción pendiente/generada [457]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN458)  // Deducc. para incentivar determ.actividades - 2013 Investigación y desarrollo - Aplicado en esta liquidación [458]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN459)  // Deducc. para incentivar determ.actividades - 2013 Investigación y desarrollo - Pendiente de aplicación en periodos futuros [459]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN460)  // Deducc. para incentivar determ.actividades - 2013 Innovación tecnológica - Deducción pendiente/generada [460]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN461)  // Deducc. para incentivar determ.actividades - 2013 Innovación tecnológica - Aplicado en esta liquidación [461]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN462)  // Deducc. para incentivar determ.actividades - 2013 Innovación tecnológica - Pendiente de aplicación en periodos futuros [462]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN813)  // Deducc. para incentivar determ.actividades - 2014 (**) Suma deducciones - Deducción pendiente/generada [813]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN814)  // Deducc. para incentivar determ.actividades - 2014 (**) Suma deducciones - Aplicado en esta liquidación [814]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN815)  // Deducc. para incentivar determ.actividades - 2014 (**)Suma deducciones - Pendiente de aplicación en periodos futuros [815]			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN792)  // Deducc. para incentivar determ.actividades - 2014 Inv.protección medio ambiente - Deducción pendiente/generada [792]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN793)  // Deducc. para incentivar determ.actividades - 2014 Inv. protección medio ambiente - Aplicado en esta liquidación [793]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN794)  // Deducc. para incentivar determ.actividades - 2014 Inv. protección medio ambiente - Pendiente de aplicación en periodos futuros [794]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN795)  // Deducc. para incentivar determ.actividades - 2014 Deducción creación empleo trabajadores discapacidad - Deducción pendiente/generada [795]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN796)  // Deducc. para incentivar determ.actividades - 2014 Deducción creación empleo trabajadores discapacidad - Deducción pendiente/generada [796]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN797)  // Deducc. para incentivar determ.actividades - 2014 Deducción creación empleo trabajadores discapacidad - Deducción pendiente/generada [797]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN798)  // Deducc. para incentivar determ.actividades - 2014 Gastos investigación y desarrollo - Deducción pendiente/generada [798]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN799)  // Deducc. para incentivar determ.actividades - 2014 Gastos investigación y desarrollo - Aplicado en esta liquidación [799]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN800)  // Deducc. para incentivar determ.actividades - 2014 Gastos investigación y desarrollo - Pendiente de aplicación en periodos futuros [800]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN096)  // Deducc. para incentivar determ.actividades - 2014 Gastos innovación tecnológica - Deducción pendiente/generada [096]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN698)  // Deducc. para incentivar determ.actividades - 2014 Gastos innovación tecnológica - Aplicado en esta liquidación [698]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN713)  // Deducc. para incentivar determ.actividades - 2014 Gastos innovación tecnológica - Pendiente de aplicación en periodos futuros [713]
            ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN986)  // Deducc. para incentivar determ.actividades - 2014 (**) Gastos investigación y desarrollo - Aplicado en esta liquidación [986]
            ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN810)  // Deducc. para incentivar determ.actividades - 2014 (**) Gastos investigación y desarrollo - Pendiente de aplicación en periodos futuros [810]
            ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN507)  // Deducc. para incentivar determ.actividades - 2014 (**) Gastos innovación tecnológica - Deducción pendiente/generada [507]
            ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN557)  // Deducc. para incentivar determ.actividades - 2014 (**) Gastos innovación tecnológica - Deducción pendiente/generada [557]
            ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN591)  // Deducc. para incentivar determ.actividades - 2014 (**) Gastos innovación tecnológica - Aplicado en esta liquidación [591]
            ,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN594)  // Deducc. para incentivar determ.actividades - 2014 (**) Gastos innovación tecnológica - Pendiente de aplicación en periodos futuros [594]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN549)  // Deducc. para incentivar determ.actividades - 2014 Deducción inversión beneficios - Deducción pendiente/generada [549]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN888)  // Deducc. para incentivar determ.actividades - 2014 Deducción inversión beneficios - Aplicado en esta liquidación [888]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN889)  // Deducc. para incentivar determ.actividades - 2014 Deducción inversión beneficios - Pendiente de aplicación en periodos futuros [889]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN807)  // Deducc. para incentivar determ.actividades - 2014 Produc. cinematográficas - Deducción pendiente/generada [807]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN808)  // Deducc. para incentivar determ.actividades - 2014 Produc. cinematográficas - Aplicado en esta liquidación [808]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN809)  // Deducc. para incentivar determ.actividades - 2014 Produc. cinematográficas - Pendiente de aplicación en periodos futuros [809]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN816)  // Deducc. para incentivar determ.actividades - 2014 Gastos formación profesional - Deducción pendiente/generada [816]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN817)  // Deducc. para incentivar determ.actividades - 2014 Gastos formación profesional - Aplicado en esta liquidación [817]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN818)  // Deducc. para incentivar determ.actividades - 2014 Gastos formación profesional - Pendiente de aplicación en periodos futuros [818]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN963)  // Deducc. para incentivar determ.actividades - 2014 Creación empleo menores 30 años - Deducción pendiente/generada [963]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN964)  // Deducc. para incentivar determ.actividades - 2014 Creación empleo menores 30 años - Aplicado en esta liquidación [964]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN965)  // Deducc. para incentivar determ.actividades - 2014 Creación empleo menores 30 años - Pendiente de aplicación en periodos futuros [965]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN931)  // Deducc. para incentivar determ.actividades - 2014 Creación empleo contratación desempleados con prestación desempleo - Deducción pendiente/generada [931]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN502)  // Deducc. para incentivar determ.actividades - 2014 Creación empleo contratación desempleados con prestación desempleo - Aplicado en esta liquidación [502]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN751)  // Deducc. para incentivar determ.actividades - 2014 Creación empleo contratación desempleados con prestación desempleo - Pendiente de aplicación en periodos futuros [751]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN972)  // Deducc. para incentivar determ.actividades - 2014 Alicante 2011 - Deducción pendiente/generada [972]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN973)  // Deducc. para incentivar determ.actividades - 2014 Alicante 2011 - Aplicado en esta liquidación [973]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN975)  // Deducc. para incentivar determ.actividades - 2014 Alicante 2011 - Pendiente de aplicación en periodos futuros [975]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN540)  // Deducc. para incentivar determ.actividades - 2014 Mundobasket 2014 - Deducción pendiente/generada [540]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN541)  // Deducc. para incentivar determ.actividades - 2014 Mundobasket 2014 - Aplicado en esta liquidación [541]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN542)  // Deducc. para incentivar determ.actividades - 2014 Mundobasket 2014 - Pendiente de aplicación en periodos futuros [542]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN901)  // Deducc. para incentivar determ.actividades - 2014 IV Centenario del fallecimiento de El Greco - Deducción pendiente/generada [901]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN902)  // Deducc. para incentivar determ.actividades - 2014 IV Centenario del fallecimiento de El Greco - Aplicado en esta liquidación [902]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN903)  // Deducc. para incentivar determ.actividades - 2014 IV Centenario del fallecimiento de El Greco - Pendiente de aplicación en periodos futuros [903]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN063)  // Deducc. para incentivar determ.actividades - 2014 Vitoria-Gasteiz Capital Verde Europea 2012 - Deducción pendiente/generada [063]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN064)  // Deducc. para incentivar determ.actividades - 2014 Vitoria-Gasteiz Capital Verde Europea 2012 - Aplicado en esta liquidación [064]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN065)  // Deducc. para incentivar determ.actividades - 2014 Vitoria-Gasteiz Capital Verde Europea 2012 - Pendiente de aplicación en periodos futuros [065]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN067)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Vela Santander 2014 - Deducción pendiente/generada [067]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN068)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Vela Santander 2014 - Aplicado en esta liquidación [068]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN069)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Vela Santander 2014  - Pendiente de aplicación en periodos futuros [069]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN070)  // Deducc. para incentivar determ.actividades - 2014 Programa "El árbol es vida" - Deducción pendiente/generada [070]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN072)  // Deducc. para incentivar determ.actividades - 2014 Programa "El árbol es vida" - Aplicado en esta liquidación [072]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN073)  // Deducc. para incentivar determ.actividades - 2014 Programa "El árbol es vida" - Pendiente de aplicación en periodos futuros [073]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN075)  // Deducc. para incentivar determ.actividades - 2014 Año de España en Japón - Deducción pendiente/generada [075]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN076)  // Deducc. para incentivar determ.actividades - 2014 Año de España en Japón - Aplicado en esta liquidación [076]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN077)  // Deducc. para incentivar determ.actividades - 2014 Año de España en Japón - Pendiente de aplicación en periodos futuros [077]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN078)  // Deducc. para incentivar determ.actividades - 2014 Plan Director recuperación Patimonio Cultural Lorca - Deducción pendiente/generada [078]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN079)  // Deducc. para incentivar determ.actividades - 2014 Plan Director recuperación Patimonio Cultural Lorca - Aplicado en esta liquidación [079]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN080)  // Deducc. para incentivar determ.actividades - 2014 Plan Director recuperación Patimonio Cultural Lorca - Pendiente de aplicación en periodos futuros [080]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN085)  // Deducc. para incentivar determ.actividades - 2014 Universiada de Invierno Granada 2015 - Deducción pendiente/generada [085]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN086)  // Deducc. para incentivar determ.actividades - 2014 Universiada de Invierno Granada 2015 - Aplicado en esta liquidación [086]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN087)  // Deducc. para incentivar determ.actividades - 2014 Universiada de Invierno Granada 2015 - Pendiente de aplicación en periodos futuros [087]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN093)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Ciclismo en Carretera Ponferrada 2014 - Deducción pendiente/generada [093]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN057)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Ciclismo en Carretera Ponferrada 2014 - Aplicado en esta liquidación [057]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN058)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Ciclismo en Carretera Ponferrada 2014 - Pendiente de aplicación en periodos futuros [058]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN207)  // Deducc. para incentivar determ.actividades - 2014 Barcelona World Jumping Challenge - Deducción pendiente/generada [207]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN208)  // Deducc. para incentivar determ.actividades - 2014 Barcelona World Jumping Challenge - Aplicado en esta liquidación [208]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN209)  // Deducc. para incentivar determ.actividades - 2014 Barcelona World Jumping Challenge - Pendiente de aplicación en periodos futuros [209]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN213)  // Deducc. para incentivar determ.actividades - 2014 Barcelona Mobile World Capital - Deducción pendiente/generada [213]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN214)  // Deducc. para incentivar determ.actividades - 2014 Barcelona Mobile World Capital - Aplicado en esta liquidación [214]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN215)  // Deducc. para incentivar determ.actividades - 2014 Barcelona Mobile World Capital - Pendiente de aplicación en periodos futuros [215]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN216)  // Deducc. para incentivar determ.actividades - 2014 3ª Edición Barcelona World Race - Deducción pendiente/generada [216]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN217)  // Deducc. para incentivar determ.actividades - 2014 3ª Edición Barcelona World Race - Aplicado en esta liquidación [217]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN218)  // Deducc. para incentivar determ.actividades - 2014 3ª Edición Barcelona World Race - Pendiente de aplicación en periodos futuros [218]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN222)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo Tiro Olímpico "Las Gabias" - Deducción pendiente/generada [222]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN223)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo Tiro Olímpico "Las Gabias" - Aplicado en esta liquidación [223]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN224)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo Tiro Olímpico "Las Gabias" - Pendiente de aplicación en periodos futuros [224]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN243)  // Deducc. para incentivar determ.actividades - 2014 2014 Año Internacional Dieta Mediterránea - Deducción pendiente/generada [243]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN244)  // Deducc. para incentivar determ.actividades - 2014 2014 Año Internacional Dieta Mediterránea  - Aplicado en esta liquidación [244]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN245)  // Deducc. para incentivar determ.actividades - 2014 2014 Año Internacional Dieta Mediterránea  - Pendiente de aplicación en periodos futuros [245]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN204)  // Deducc. para incentivar determ.actividades - 2014 Programa preparación deportistas españoles juegos "Río de Janeiro 2016"  - Deducción pendiente/generada [204]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN205)  // Deducc. para incentivar determ.actividades - 2014 Programa preparación deportistas españoles juegos "Río de Janeiro 2016"  - Aplicado en esta liquidación [205]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN206)  // Deducc. para incentivar determ.actividades - 2014 Programa preparación deportistas españoles juegos "Río de Janeiro 2016"  - Pendiente de aplicación en periodos futuros [206]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN219)  // Deducc. para incentivar determ.actividades - 2014 VIII Centenario Peregrinación San Francisco de Asís a Santiago de Compostela  - Deducción pendiente/generada [219]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN220)  // Deducc. para incentivar determ.actividades - 2014  VIII Centenario Peregrinación San Francisco de Asís a Santiago de Compostela  - Aplicado en esta liquidación [220]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN221)  // Deducc. para incentivar determ.actividades - 2014  VIII Centenario Peregrinación San Francisco de Asís a Santiago de Compostela  - Pendiente de aplicación en periodos futuros [221]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN228)  // Deducc. para incentivar determ.actividades - 2014 V Centenario del Nacimiento Santa Teresa Avila 2015  - Deducción pendiente/generada [228] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN229)  // Deducc. para incentivar determ.actividades - 2014  V Centenario del Nacimiento Santa Teresa Avila 2015  - Aplicado en esta liquidación [229]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN230)  // Deducc. para incentivar determ.actividades - 2014  V Centenario del Nacimiento Santa Teresa Avila 2015  - Pendiente de aplicación en periodos futuros [230]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN234)  // Deducc. para incentivar determ.actividades - 2014 Año Santo Jubilar Mariano a celebrar ciudad de Sevilla - Deducción pendiente/generada [234]  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN235)  // Deducc. para incentivar determ.actividades - 2014 Año Santo Jubilar Mariano a celebrar ciudad de Sevilla - Aplicado en esta liquidación [235]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN236)  // Deducc. para incentivar determ.actividades - 2014 Año Santo Jubilar Mariano a celebrar ciudad de Sevilla - Pendiente de aplicación en periodos futuros [236]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN237)  // Deducc. para incentivar determ.actividades - 2014 Vuelta al mundo a vela Alicante 2014 - Deducción pendiente/generada [237]  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN238)  // Deducc. para incentivar determ.actividades - 2014 Vuelta al mundo a vela Alicante 2014 - Aplicado en esta liquidación [238]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN239)  // Deducc. para incentivar determ.actividades - 2014 Vuelta al mundo a vela Alicante 2014 - Pendiente de aplicación en periodos futuros [239]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN007)  // Deducc. para incentivar determ.actividades - 2014 Donostia/San Sebastián, Capital Europea de la Cultura - Deducción pendiente/generada [007]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN012)  // Deducc. para incentivar determ.actividades - 2014 Donostia/San Sebastián, Capital Europea de la Cultura - Aplicado en esta liquidación [012]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN016)  // Deducc. para incentivar determ.actividades - 2014 Donostia/San Sebastián, Capital Europea de la Cultura - Pendiente de aplicación en periodos futuros [016]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN199)  // Deducc. para incentivar determ.actividades - 2014 Expo Milán 2015 - Deducción pendiente/generada [199]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN292)  // Deducc. para incentivar determ.actividades - 2014 Expo Milán 2015 - Aplicado en esta liquidación [292]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN293)  // Deducc. para incentivar determ.actividades - 2014 Expo Milán 2015 - Pendiente de aplicación en periodos futuros [293]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN353)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Escalada 2014 - Deducción pendiente/generada [353]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN366)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Escalada 2014 - Aplicado en esta liquidación [366]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN394)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Escalada 2014 - Pendiente de aplicación en periodos futuros [394]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN395)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Patinaje Artístico 2014 - Deducción pendiente/generada [395]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN401)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Patinaje Artístico 2014 - Aplicado en esta liquidación [401]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN407)  // Deducc. para incentivar determ.actividades - 2014 Campeonato del Mundo de Patinaje Artístico 2014 - Pendiente de aplicación en periodos futuros [407]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN419)  // Deducc. para incentivar determ.actividades - 2014 Madrid Horse Week - Deducción pendiente/generada [419]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN422)  // Deducc. para incentivar determ.actividades - 2014 Madrid Horse Week - Aplicado en esta liquidación [422]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN423)  // Deducc. para incentivar determ.actividades - 2014 Madrid Horse Week - Pendiente de aplicación en periodos futuros [423]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN424)  // Deducc. para incentivar determ.actividades - 2014 III Centenario de la Real Academia Española - Deducción pendiente/generada [424]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN425)  // Deducc. para incentivar determ.actividades - 2014 III Centenario de la Real Academia Española - Aplicado en esta liquidación [425]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN428)  // Deducc. para incentivar determ.actividades - 2014 III Centenario de la Real Academia Española - Pendiente de aplicación en periodos futuros [428]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN429)  // Deducc. para incentivar determ.actividades - 2014 A Coruña 2015-120 años después - Deducción pendiente/generada [429]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN430)  // Deducc. para incentivar determ.actividades - 2014 A Coruña 2015-120 años después - Aplicado en esta liquidación [430]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN431)  // Deducc. para incentivar determ.actividades - 2014 A Coruña 2015-120 años después - Pendiente de aplicación en periodos futuros [431]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
			
		,PAG17B ("T20017B", new IPropertyFiller[] {				
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN432)  // Deducc. para incentivar determ.actividades - 2014 IV Centenario de la segunda parte de El Quijote - Deducción pendiente/generada [432]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN433)  // Deducc. para incentivar determ.actividades - 2014 IV Centenario de la segunda parte de El Quijote - Aplicado en esta liquidación [433]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN434)  // Deducc. para incentivar determ.actividades - 2014 IV Centenario de la segunda parte de El Quijote - Pendiente de aplicación en periodos futuros [434]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN435)  // Deducc. para incentivar determ.actividades - 2014 World Challenge LFP/ 85 Aniversario de la Liga - Deducción pendiente/generada [435]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN436)  // Deducc. para incentivar determ.actividades - 2014 World Challenge LFP/ 85 Aniversario de la Liga - Aplicado en esta liquidación [436]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN437)  // Deducc. para incentivar determ.actividades - 2014 World Challenge LFP/ 85 Aniversario de la Liga - Pendiente de aplicación en periodos futuros [437]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN438)  // Deducc. para incentivar determ.actividades - 2014 Juegos del Mediterráneo de 2017 - Deducción pendiente/generada [438]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN439)  // Deducc. para incentivar determ.actividades - 2014 Juegos del Mediterráneo de 2017 - Aplicado en esta liquidación [439]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN440)  // Deducc. para incentivar determ.actividades - 2014 Juegos del Mediterráneo de 2017 - Pendiente de aplicación en periodos futuros [440]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN441)  // Deducc. para incentivar determ.actividades - 2014 Sesenta Edición Festival Internacional Teatro Clásico de Mérida - Deducción pendiente/generada [441]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN452)  // Deducc. para incentivar determ.actividades - 2014 Sesenta Edición Festival Internacional Teatro Clásico de Mérida - Aplicado en esta liquidación [452]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN453)  // Deducc. para incentivar determ.actividades - 2014 Sesenta Edición Festival Internacional Teatro Clásico de Mérida - Pendiente de aplicación en periodos futuros [453]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN454)  // Deducc. para incentivar determ.actividades - 2014 Año de la Biotecnología en España - Deducción pendiente/generada [454]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN455)  // Deducc. para incentivar determ.actividades - 2014 Año de la Biotecnología en España - Aplicado en esta liquidación [455]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN456)  // Deducc. para incentivar determ.actividades - 2014 Año de la Biotecnología en España - Pendiente de aplicación en periodos futuros [456]			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN828)  // Deducc. para incentivar determ.actividades - 2014 Diferimiento Deducciones - Deducción pendiente/generada [828]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN829)  // Deducc. para incentivar determ.actividades - 2014 Diferimiento deducciones - Aplicado en esta liquidación [829]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN830)  // Deducc. para incentivar determ.actividades - 2014 Diferimiento deducciones - Pendiente de aplicación en periodos futuros [830]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN634)  // Deducc. para incentivar determ.actividades - Total deducciones programas apoyo acontecimientos de excepcional interés público - Deducción pendiente/generada [634]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN635)  // Deducc. para incentivar determ.actividades - Total deducciones programas apoyo acontecimientos de excepcional interés público - Aplicado en esta liquidación [635]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN636)  // Deducc. para incentivar determ.actividades - Total deducciones programas apoyo acontecimientos de excepcional interés público - Pendiente de aplicación en periodos futuros [636]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN831)  // Deducc. para incentivar determ.actividades - Total deducciones Cap.IV Tít.VI - Deducción pendiente/generada [831]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN588)  // Deducc. para incentivar determ.actividades - Total deducciones Cap.IV Tít.VI - Aplicado en esta liquidación [588]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN832)  // Deducc. para incentivar determ.actividades - Total deducciones Cap.IV Tít.VI - Pendiente de aplicación en periodos futuros [832]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN918)  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Deducción generada [918]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN919)  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Deducción reducida [919]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN574)  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Importe deducible en cuota [574]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN580)  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Pendiente insuficiencia cuota [580]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN589)  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Deducción generada [589]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN976)  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Deducción reducida [976]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN977)  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Importe deducible en cuota [977]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN978)  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Pendiente insuficiencia cuota [978]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN822)  // Deducciones I+D+i excluidas de límite - 2014 Investigación y desarrollo - Deducción pendiente/generada [822]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN823)  // Deducciones I+D+i excluidas de límite - 2014 Investigación y desarrollo - Deducción reducida [823]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN824)  // Deducciones I+D+i excluidas de límite - 2014 Investigación y desarrollo - Aplicado en esta liquidación [824]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN231)  // Deducciones I+D+i excluidas de límite - 2014 Investigación y desarrollo - Importe abonado por insuficiencia de cuota [231]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN232)  // Deducciones I+D+i excluidas de límite - 2014 Innovación tecnológica - Deducción pendiente/generada [232]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN233)  // Deducciones I+D+i excluidas de límite - 2014 Innovación tecnológica - Deducción reducida [233]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN850)  // Deducciones I+D+i excluidas de límite - 2014 Innovación tecnológica - Aplicado en esta liquidación [850]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN851)  // Deducciones I+D+i excluidas de límite - 2014 Innovación tecnológica - Importe abonado por insuficiencia de cuota [851]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN517)  // Deducciones I+D+i excluidas de límite - Total - Deducción generada [517]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN081)  // Deducciones I+D+i excluidas de límite - Total - Deducción reducida [081]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN082)  // Deducciones I+D+i excluidas de límite - Total - Importe deducible en cuota [082]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN083)  // Deducciones I+D+i excluidas de límite - Total - Pendiente insuficiencia cuota [083]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina	
		    })
			
		,PAG18 ("T200180", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN929)  // Deducción donativos entidades sin fines lucro - 2004 - Deducción pendiente/generada [929]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN930)  // Deducción donativos entidades sin fines lucro - 2004 - Aplicado en esta liquidación [930]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN942)  // Deducción donativos entidades sin fines lucro - 2005 - Deducción pendiente/generada [942]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN943)  // Deducción donativos entidades sin fines lucro - 2005 - Aplicado en esta liquidación [943]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN944)  // Deducción donativos entidades sin fines lucro - 2005 - Pendiente de aplicación en periodos futuros [944]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN294)  // Deducción donativos entidades sin fines lucro - 2006 - Deducción pendiente/generada [294]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN295)  // Deducción donativos entidades sin fines lucro - 2006 - Aplicado en esta liquidación [295]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN296)  // Deducción donativos entidades sin fines lucro - 2006 - Pendiente de aplicación en periodos futuros [296]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN066)  // Deducción donativos entidades sin fines lucro - 2007 - Deducción pendiente/generada [066]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN074)  // Deducción donativos entidades sin fines lucro - 2007 - Aplicado en esta liquidación [074]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN084)  // Deducción donativos entidades sin fines lucro - 2007 - Pendiente de aplicación en periodos futuros [084]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN008)  // Deducción donativos entidades sin fines lucro - 2008 - Deducción pendiente/generada [008]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN009)  // Deducción donativos entidades sin fines lucro - 2008 - Aplicado en esta liquidación [009]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN010)  // Deducción donativos entidades sin fines lucro - 2008 - Pendiente de aplicación en periodos futuros [010]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN034)  // Deducción donativos entidades sin fines lucro - 2009 - Deducción pendiente/generada [034]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN035)  // Deducción donativos entidades sin fines lucro - 2009 - Aplicado en esta liquidación [035]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN036)  // Deducción donativos entidades sin fines lucro - 2009 - Pendiente de aplicación en periodos futuros [036]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN201)  // Deducción donativos entidades sin fines lucro - 2010 - Deducción pendiente/generada [201]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN202)  // Deducción donativos entidades sin fines lucro - 2010 - Aplicado en esta liquidación [202]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN203)  // Deducción donativos entidades sin fines lucro - 2010 - Pendiente de aplicación en periodos futuros [203]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN904)  // Deducción donativos entidades sin fines lucro - 2011 - Deducción pendiente/generada [904]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN905)  // Deducción donativos entidades sin fines lucro - 2011 - Aplicado en esta liquidación [905]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN906)  // Deducción donativos entidades sin fines lucro - 2011 - Pendiente de aplicación en periodos futuros [906]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN990)  // Deducción donativos entidades sin fines lucro - 2012 - Deducción pendiente/generada [990]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN991)  // Deducción donativos entidades sin fines lucro - 2012 - Aplicado en esta liquidación [991]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN992)  // Deducción donativos entidades sin fines lucro - 2012 - Pendiente de aplicación en periodos futuros [992]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN997)  // Deducción donativos entidades sin fines lucro - 2013 - Deducción pendiente/generada [997]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN998)  // Deducción donativos entidades sin fines lucro - 2013 - Aplicado en esta liquidación [998]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN999)  // Deducción donativos entidades sin fines lucro - 2013 - Pendiente de aplicación en periodos futuros [999]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN246)  // Deducción donativos entidades sin fines lucro - 2014 (*) - Deducción pendiente/generada [246]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN247)  // Deducción donativos entidades sin fines lucro - 2014 (*) - Aplicado en esta liquidación [247]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN248)  // Deducción donativos entidades sin fines lucro - 2014 (*) - Pendiente de aplicación en periodos futuros [248]		
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN993)  // Deducción donativos entidades sin fines lucro - 2013 - Deducción pendiente/generada [993]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN994)  // Deducción donativos entidades sin fines lucro - 2013 - Aplicado en esta liquidación [994]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN995)  // Deducción donativos entidades sin fines lucro - 2013 - Pendiente de aplicación en periodos futuros [995]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN598)  // Deducción donativos entidades sin fines lucro - Total deducciones donaciones a entidades sin fines lucro - Deducción pendiente/generada [598]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN565)  // Deducción donativos entidades sin fines lucro - Total deducciones donaciones a entidades sin fines lucro - Aplicado en esta liquidación [565]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN895)  // Deducción donativos entidades sin fines lucro - Total deducciones donaciones a entidades sin fines lucro - Pendiente de aplicación en periodos futuros [895]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN974)  // Deducción donativos entidades sin fines lucro - Donaciones del período impositivo efectuadas a entidades sin fines de lucro [974]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID650)  // Aplicación de resultados - Base de reparto - Pérdidas y ganancias [650]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID651)  // Aplicación de resultados - Base de reparto - Remanente [651]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID652)  // Aplicación de resultados - Base de reparto - Reservas [652]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID653)  // Aplicación de resultados - Base de reparto - Total [653]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID654)  // Aplicación de resultados - Aplicación - A reservas [654]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID655)  // Aplicación de resultados - Aplicación - Intereses aportaciones al capital (Cooperativas) [655]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID656)  // Aplicación de resultados - Aplicación - A dividendos [656]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID658)  // Aplicación de resultados - Aplicación - A dotación O.S. (Cajas de ahorro y fundaciones bancarias) [658]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID659)  // Aplicación de resultados - Aplicación - A F.R.O y dotaciones voluntarias al F.E.P (Cooperativas) [659]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID660)  // Aplicación de resultados - Aplicación - A retornos cooperativos (Cooperativas) [660]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID662)  // Aplicación de resultados - Aplicación - Partícipes (IIC) [662]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID664)  // Aplicación de resultados - Aplicación - A remanente y otros [664]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID665)  // Aplicación de resultados - Aplicación - A compensación de pérdidas de ejercicios anteriores [665]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.ID666)  // Aplicación de resultados - Aplicación - Total [666]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC001)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correcciones permanentes - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC002)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correcciones permanentes - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC003)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC004)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC005)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Saldo pendiente - Aumentos futuros 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC006)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC007)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Amortizaciones - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC008)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Amortizaciones - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC009)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Amortizaciones - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC010)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Amortizaciones - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC011)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Deterioros valor - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC012)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Deterioros valor - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC013)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Deterioros valor - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC014)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Deterioros valor - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC015)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Pensiones -  Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC016)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Pensiones -  Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC017)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Pensiones -  Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC018)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Pensiones -  Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC019)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Fondo de comercio - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC020)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Fondo de comercio - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC021)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Fondo de comercio - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC022)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Fondo de comercio - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC023)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Resto - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC024)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Resto - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC025)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Resto - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC026)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejercicio - Resto - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC027)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC028)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC029)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC030)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC031)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Amortizaciones - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC032)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Amortizaciones - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC033)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Amortizaciones - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC034)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Amortizaciones - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC035)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Deterioros valor - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC036)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Deterioros valor - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC037)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Deterioros valor - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC038)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Deterioros valor - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC039)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Pensiones - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC040)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Pensiones - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC041)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Pensiones - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC042)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Pensiones - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC043)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Fondo de comercio - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC044)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Fondo de comercio - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC045)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Fondo de comercio - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC046)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Fondo de comercio - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC047)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Resto - Del ejercicio - Aumentos
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC048)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Resto - Del ejercicio - Disminuciones
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC049)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Resto - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC050)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Correc. temporarias origen ejerc. anteriores - Resto - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC051)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Del ejercicio - Aumentos  [417]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC052)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Del ejercicio - Disminuciones [418]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC053)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Saldo pendiente - Aumentos futuros
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.DC054)	//	Detalle correcciones resultado pérdidas y ganancias - Correcciones fiscales - Total correcciones resultado de pérdidas y ganancias - Saldo pendiente - Disminuciones futuras
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(22)) // [...] NO ESTA EN EL MODELO - Presentación de documentación previa en la sede electrónica. NRS1
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(22)) // [...] NO ESTA EN EL MODELO - Presentación de documentación previa en la sede electrónica. NRS2			
			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})
			
		,PAG18B ("T20018B", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM175)  // Limitación deducibilidad gastos financieros - a) Resultado explotación [175]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM176)  // Limitación deducibilidad gastos financieros - b) Amortización del inmovilizado [176]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM177)  // Limitación deducibilidad gastos financieros - c) Imputación subvenciones inmovilizado no financiero y otras [177]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM178)  // Limitación deducibilidad gastos financieros - d) Deterioro y resultado enajenaciones inmovilizado [178]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM179)  // Limitación deducibilidad gastos financieros - e) Ingresos financieros participaciones instrumentos de patrimonio [179]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM043)  // Limitación deducibilidad gastos financieros - f) Límite deducción gastos financieros netos [043]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM049)  // Limitación deducibilidad gastos financieros - g) Adición por límite beneficio operativo no aplicado en cinco ejercicios anteriores [049]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM249)  // Limitación deducibilidad gastos financieros - h) Gastos financieros periodo impositivo excluidos art. 14.1.h) LIS [249]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM252)  // Limitación deducibilidad gastos financieros - i) Ingresos financieros periodo impositivo derivados cesión terceros de capitales propios [252]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM253)  // Limitación deducibilidad gastos financieros - j) Gastos financieros netos del periodo [253]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM254)  // Limitación deducibilidad gastos financieros - k) Gastos financieros netos del periodo deducibles [254]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM255)  // Limitación deducibilidad gastos financieros - l) Gastos financieros netos del periodo no deducibles [255]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM258)  // Limitación deducibilidad gastos financieros - m) Gastos financieros netos pendientes deducir de periodos anteriores aplicados [258]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM259)  // Limitación deducibilidad gastos financieros - n) Total gastos financieros netos deducibles en el periodo [259]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM260)  // Limitación deducibilidad gastos financieros - ñ) Total gastos financieros deducibles en el periodo [260]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM212)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2012 - Pendiente aplicación a principio del período [212]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM270)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2012 - Aplicado en esta liquidación [270]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM271)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2012 - Pendiente aplicación períodos futuros [271]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM969)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013 - Pendiente aplicación a principio del período [969]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM970)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013 - Aplicado en esta liquidación [970]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM971)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013 - Pendiente aplicación períodos futuros  [971]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM261)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2014(*) - Pendiente aplicación a principio del período [261]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM262)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2014(*) - Aplicado en esta liquidación [262]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM263)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2014(*) - Pendiente aplicación períodos futuros  [263]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM264)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2014(**) - Pendiente aplicación a principio del período [264]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM265)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2014(**) - Aplicado en esta liquidación [265]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM266)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2014(**) - Pendiente aplicación períodos futuros  [266]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM267)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Total - Pendiente aplicación a principio del período [267]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM268)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Total - Aplicado en esta liquidación [268]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM269)  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Total - Pendiente aplicación períodos futuros  [269]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM890)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2012 - Pendiente aplicación a principio del período [890]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM891)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2012 - Aplicado en esta liquidación [891]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM892)  // Num Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2012 - Pendiente aplicación períodos futuros [892]			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM503)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013 - Pendiente aplicación a principio del período [503]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM522)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013 - Aplicado en esta liquidación [522]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM523)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013 - Pendiente aplicación períodos futuros  [523]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM273)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2014(*) - Pendiente aplicación a principio del período [273]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM274)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2014(*) - Aplicado en esta liquidación [274]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM537)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2014(*) - Pendiente aplicación períodos futuros  [537]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM955)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2014(**) - Pendiente aplicación a principio del período [955]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM956)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2014(**) - Aplicado en esta liquidación [956]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM957)  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2014(**) - Pendiente aplicación períodos futuros [957]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM538)  // Pendiente adición por límite beneficio operativo no aplicado - Total - Pendiente aplicación a principio del período [538]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM539)  // Pendiente adición por límite beneficio operativo no aplicado - Total - Aplicado en esta liquidación [539]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM546)  // Pendiente adición por límite beneficio operativo no aplicado - Total - Pendiente aplicación períodos futuros  [546]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM893)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2011 - Pendiente aplicación a principio del período [893]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM173)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2011 - Ingresado en esta liquidación [173] 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM958)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2011 - Conversión [958]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM898)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2011 - Pendiente de integración [898]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM899)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2012 - Pendiente aplicación a principio del período [899]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM227)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2012 - Ingresado en esta liquidación [227]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM959)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2012 - Conversión [959]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM917)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2012 - Pendiente de integración [917]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM948)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2013 - Pendiente aplicación a principio del período [948]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM291)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2013 - Ingresado en esta liquidación [291]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM979)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2013 - Conversión [979]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM949)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2013 - Pendiente de integración [949]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM950)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 (*) - Pendiente aplicación a principio del período [950]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM951)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 (*) - Ingresado en esta liquidación [951]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM980)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 (*) - Conversión [980]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM952)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 (*) - Pendiente de integración [952]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM981)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 - Pendiente aplicación a principio del período [981]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM982)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 - Ingresado en esta liquidación [982]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM983)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 - Conversión [983]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM984)  // Dotaciones deterioro créditos u otros activos - Ejercicio generación 2014 - Pendiente de integración [984]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM953)  // Dotaciones deterioro créditos u otros activos - Total - Pendiente aplicación a principio del período [953]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM344)  // Dotaciones deterioro créditos u otros activos - Total - Ingresado en esta liquidación [344]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM985)  // Dotaciones deterioro créditos u otros activos - Total - Conversión [985]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM954)  // Dotaciones deterioro créditos u otros activos - Total - Pendiente de integración [954]			
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM393)  // Dotaciones deterioro créditos u otros activos - Conversión activos impuesto diferido - Importe crédito exigible [393]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM150)  // Dotaciones deterioro créditos u otros activos - Conversión activos impuesto diferido - Opciones: Abono [150]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LM506)  // Dotaciones deterioro créditos u otros activos - Conversión activos impuesto diferido - Opciones: Compensación [506]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})		
			
		,PAG19 ("T200190", new IPropertyFiller[] {

			(line,mod200, label) -> {
				    
				    addStartLabel(line,label);  // Etiqueta de inicio de pagina
				    
				    // Esta página puede tener complementarias en los apartados que ahora no están en el modelo
				    // Si se añaden dichos apartados hay que tenerlo en cuenta, para hacerlo de forma similar a la pagina 2
				    boolean isComplementary = false;
					line.append(isComplementary?"C":" ");  // Indicador de pagina complementaria
				
					// [...] NO ESTA EN EL MODELO - Operaciones relacionadas con paises o territorios considerados como paraisos fiscales (hasta 6)
					for (int i=0;i<6;i++) {
						line.append(AonFiscalFileUtils.spaces(20));    // Descripción de la operación
						line.append(AonFiscalFileUtils.spaces(20));    // Persona o entidad
						line.append(AonFiscalFileUtils.spaces(1));     // F/J 
						line.append(AonFiscalFileUtils.spaces(2));     // Clave país/territorio
						line.append(AonFiscalFileUtils.zeros(DEFAULT_SIZE));  // Importe
					}
					
					// [...] NO ESTA EN EL MODELO - Tenencia de valores con paraisos fiscales (hasta 6)
					for (int i=0;i<6;i++) {
						line.append(AonFiscalFileUtils.spaces( 1)); // Tipo A - B - C
						line.append(AonFiscalFileUtils.spaces(23)); // Entidad participada
						line.append(AonFiscalFileUtils.spaces( 2)); // Clave país/territorio
						line.append(AonFiscalFileUtils.zeros(DEFAULT_SIZE)); // Valor adquisición
						line.append(AonFiscalFileUtils.zeros(5));            // % participación
					}
					
					// [...] NO ESTA EN EL MODELO - Comunicación del importe neto de la cifra de negocios
					line.append(AonFiscalFileUtils.zeros(DEFAULT_SIZE)); // Grupos de sociedades. Importe neto cifra negocios [987] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [1]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [2]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [3]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [4]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [5]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [6]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [7]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [8]
					line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [9]
					line.append(AonFiscalFileUtils.zeros(DEFAULT_SIZE)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. Importe neto [988] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
					line.append(AonFiscalFileUtils.zeros(3)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. Nº establecimientos (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
					line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [1]
					line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [2]
					line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [3]
					line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [4]
					line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [5]
					line.append(AonFiscalFileUtils.zeros(DEFAULT_SIZE)); // Comunicación importe neto cifra negocios - Entidades de crédito, aseguradoras, I.I.C. y sociedades de garantíarecíproca - Importe neto [989] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
					
					// Las siguientes 5 casillas solo van con importes en la primera pagina, en las complementarias van a cero
					addUnSignedKey(line, mod200, Mod2002014Key.LQ0N1, 4, 0, isComplementary); // Rég. Entidades navieras en función del tonelaje. Nº de buques  [N1]
					addSignedKey(line, mod200, Mod2002014Key.LQ630,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de  aplicar la escala [630]
					addSignedKey(line, mod200, Mod2002014Key.LQ631,isComplementary); // Rég. Entidades navieras en función del tonelaje. Importe rentas generadas en trasmisiones de buques [631]
					addSignedKey(line, mod200, Mod2002014Key.LQ632,isComplementary); // Rég. Entidades navieras en función del tonelaje. Compensación bases imponibles negativas períodos anteriores [632]
					addSignedKey(line, mod200, Mod2002014Key.LQ579,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de la aplicación del régimen [579]
					
					addEndLabel(line,label); // Etiqueta fin de pagina
				}
			})		
		
		,PAG20 ("T200200", new IPropertyFiller[] {
				
				(line,mod200, label) -> {
					
					addStartLabel(line,label);  // Etiqueta de inicio de pagina
					
				    // Esta página puede tener complementarias en algunos apartados que ahora no están en el modelo
				    // Si se añaden dichos apartados hay que tenerlo en cuenta, para hacerlo de forma similar a la pagina 2
				    boolean isComplementary = false;			        
				    line.append(isComplementary?"C":" ");  // Indicador de pagina complementaria
					
					// [...] NO ESTA EN EL MODELO - Regimen Especial de la reserva para inversiones en Canarias (21 campos, estos van a cero en las complementarias)
					line.append(AonFiscalFileUtils.zeros(21*DEFAULT_SIZE));
					
					// [...] NO ESTA EN EL MODELO - Operaciones con personas o entidades vinculadas (hasta 6)
					for (int i=0;i<6;i++) {
						line.append(AonFiscalFileUtils.spaces(15)); // NIF
						line.append(AonFiscalFileUtils.spaces( 1)); // F/J
						line.append(AonFiscalFileUtils.spaces(40)); // Apellidos y nombre
						line.append(AonFiscalFileUtils.spaces( 1)); // Tipo vinculación A a L
						line.append(AonFiscalFileUtils.spaces( 2)); // Código provincia/país
						line.append(AonFiscalFileUtils.zeros ( 2)); // Tipo operación 1 a 11
						line.append(AonFiscalFileUtils.spaces( 1)); // Ingreso/Pago "I" "P"
						line.append(AonFiscalFileUtils.spaces( 2)); // Método valoración 1a 1b 1c 2a 2b
						line.append(AonFiscalFileUtils.zeros (DEFAULT_SIZE)); // Importe operación
					}
					
					addSignedKey(line, mod200, Mod2002014Key.CP0C1, isComplementary); // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados cooperativos [C1]
					addSignedKey(line, mod200, Mod2002014Key.CP0E1, isComplementary); // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados extracooperativos [E1]
					addSignedKey(line, mod200, Mod2002014Key.CP0C2, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados cooperativos [C2]
					addSignedKey(line, mod200, Mod2002014Key.CP0E2, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados extracooperativos [E2]
					addSignedKey(line, mod200, Mod2002014Key.CP0C3, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados cooperativos [C3]
					addSignedKey(line, mod200, Mod2002014Key.CP0E3, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados extracooperativos [E3]
					addSignedKey(line, mod200, Mod2002014Key.CP0C4, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados cooperativos [C4]
					addSignedKey(line, mod200, Mod2002014Key.CP0E4, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados extracooperativos [E4]
					addSignedKey(line, mod200, Mod2002014Key.CP0E5, isComplementary); // Rég. cooperativas - Determ. base imponible - Incrementos y disminuciones patrimoniales - Resultados extracooperativos [E5]
					addSignedKey(line, mod200, Mod2002014Key.CP0C6, isComplementary); // Rég. cooperativas - Determ. base imponible - resultado - Resultados cooperativos [C6]
					addSignedKey(line, mod200, Mod2002014Key.CP0E6, isComplementary); // Rég. cooperativas - Determ. base imponible - resultado - Resultados extracooperativos [E6]
					addSignedKey(line, mod200, Mod2002014Key.CP0C7, isComplementary); // Rég. cooperativas - Determ. base imponible - aumentos - Resultados cooperativos [C7]
					addSignedKey(line, mod200, Mod2002014Key.CP0E7, isComplementary); // Rég. cooperativas - Determ. base imponible - aumentos - Resultados extracooperativos [E7]
					addSignedKey(line, mod200, Mod2002014Key.CP0C8, isComplementary); // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados cooperativos [C8]
					addSignedKey(line, mod200, Mod2002014Key.CP0E8, isComplementary); // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados extracooperativos [E8]
					addSignedKey(line, mod200, Mod2002014Key.CP0C9, isComplementary); // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados cooperativos [C9]
					addSignedKey(line, mod200, Mod2002014Key.CP0E9, isComplementary); // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados extracooperativos [E9]
					addSignedKey(line, mod200, Mod2002014Key.CPC10, isComplementary); // Rég. cooperativas - Determ. base imponible - Reserva inversiones Canarias - Resultados cooperativos [C10]
					addSignedKey(line, mod200, Mod2002014Key.CPC11, isComplementary); // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados cooperativos [C11]
					addSignedKey(line, mod200, Mod2002014Key.CPE11, isComplementary); // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados extracooperativos [E11]
					addSignedKey(line, mod200, Mod2002014Key.CPC12, isComplementary); // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados cooperativos [553]
					addSignedKey(line, mod200, Mod2002014Key.CPE12, isComplementary); // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados extracooperativos [554]
					addSignedKey(line, mod200, Mod2002014Key.LQ673, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 1999 Pendiente aplicación al principio del periodo [673]
					addSignedKey(line, mod200, Mod2002014Key.LQ674, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 1999 Aplicado en esta liquidación [674]
					addSignedKey(line, mod200, Mod2002014Key.LQ676, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2000 Pendiente aplicación al principio del periodo [676]
					addSignedKey(line, mod200, Mod2002014Key.LQ677, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2000 Aplicado en esta liquidación [677]
					addSignedKey(line, mod200, Mod2002014Key.LQ678, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2000 Pendiente aplicación en ejercicios futuros [678]
					addSignedKey(line, mod200, Mod2002014Key.LQ679, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2001 Pendiente aplicación al principio del periodo [679]
					addSignedKey(line, mod200, Mod2002014Key.LQ680, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2001 Aplicado en esta liquidación [680]
					addSignedKey(line, mod200, Mod2002014Key.LQ681, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2001 Pendiente aplicación en ejercicios futuros [681]
					addSignedKey(line, mod200, Mod2002014Key.LQ682, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2002 Pendiente aplicación al principio del periodo [682]
					addSignedKey(line, mod200, Mod2002014Key.LQ683, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2002 Aplicado en esta liquidación [683]
					addSignedKey(line, mod200, Mod2002014Key.LQ684, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2002 Pendiente aplicación en ejercicios futuros [684]
					addSignedKey(line, mod200, Mod2002014Key.LQ685, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2003 Pendiente aplicación al principio del periodo [685]
					addSignedKey(line, mod200, Mod2002014Key.LQ686, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2003 Aplicado en esta liquidación [686]
					addSignedKey(line, mod200, Mod2002014Key.LQ687, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2003 Pendiente aplicación en ejercicios futuros [687]
					addSignedKey(line, mod200, Mod2002014Key.LQ688, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2004 Pendiente aplicación al principio del periodo [688]
					addSignedKey(line, mod200, Mod2002014Key.LQ689, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2004 Aplicado en esta liquidación [689]
					addSignedKey(line, mod200, Mod2002014Key.LQ690, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2004 Pendiente aplicación en ejercicios futuros [690]
					addSignedKey(line, mod200, Mod2002014Key.LQ691, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2005 Pendiente aplicación al principio del periodo [691]
					addSignedKey(line, mod200, Mod2002014Key.LQ692, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2005 Aplicado en esta liquidación [692]
					addSignedKey(line, mod200, Mod2002014Key.LQ693, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2005 Pendiente aplicación en ejercicios futuros [693]
					addSignedKey(line, mod200, Mod2002014Key.LQ623, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2006 Pendiente aplicación al principio del periodo [623]
					addSignedKey(line, mod200, Mod2002014Key.LQ624, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2006 Aplicado en esta liquidación [624]
					addSignedKey(line, mod200, Mod2002014Key.LQ672, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2006 Pendiente aplicación en ejercicios futuros [672]
					addSignedKey(line, mod200, Mod2002014Key.LQ279, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2007 Pendiente aplicación al principio del periodo [279]
					addSignedKey(line, mod200, Mod2002014Key.LQ280, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2007 Aplicado en esta liquidación [280]
					addSignedKey(line, mod200, Mod2002014Key.LQ281, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2007 Pendiente aplicación en ejercicios futuros [281]
					addSignedKey(line, mod200, Mod2002014Key.LQ587, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2008 Pendiente aplicación al principio del periodo [587]
					addSignedKey(line, mod200, Mod2002014Key.LQ515, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2008 Aplicado en esta liquidación [515]
					addSignedKey(line, mod200, Mod2002014Key.LQ900, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2008 Pendiente aplicación en ejercicios futuros [900]
					addSignedKey(line, mod200, Mod2002014Key.LQ059, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2009 Pendiente aplicación al principio del periodo [059]
					addSignedKey(line, mod200, Mod2002014Key.LQ099, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2009 Aplicado en esta liquidación [099]
					addSignedKey(line, mod200, Mod2002014Key.LQ100, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2009 Pendiente aplicación en ejercicios futuros [100]
					addSignedKey(line, mod200, Mod2002014Key.LQ017, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2010 Pendiente aplicación al principio del periodo [017]
					addSignedKey(line, mod200, Mod2002014Key.LQ018, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2010 Aplicado en esta liquidación [018]
					addSignedKey(line, mod200, Mod2002014Key.LQ019, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2010 Pendiente aplicación en ejercicios futuros [019]
					addSignedKey(line, mod200, Mod2002014Key.LQ772, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2011 Pendiente aplicación al principio del periodo [772]
					addSignedKey(line, mod200, Mod2002014Key.LQ773, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2011 Aplicado en esta liquidación [773]
					addSignedKey(line, mod200, Mod2002014Key.LQ777, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2011 Pendiente aplicación en ejercicios futuros [777]
					addSignedKey(line, mod200, Mod2002014Key.LQ907, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2012 Pendiente aplicación al principio del periodo [907]
					addSignedKey(line, mod200, Mod2002014Key.LQ908, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2012 Aplicado en esta liquidación [908]
					addSignedKey(line, mod200, Mod2002014Key.LQ909, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2012 Pendiente aplicación en ejercicios futuros [909]
					addSignedKey(line, mod200, Mod2002014Key.LQ910, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2013 Pendiente aplicación al principio del periodo [910]
					addSignedKey(line, mod200, Mod2002014Key.LQ911, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2013 Aplicado en esta liquidación [911]
					addSignedKey(line, mod200, Mod2002014Key.LQ912, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2013 Pendiente aplicación en ejercicios futuros [912]
					addSignedKey(line, mod200, Mod2002014Key.LQ935, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2014 (*) Pendiente aplicación al principio del periodo [935]
					addSignedKey(line, mod200, Mod2002014Key.LQ936, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2014 (*) Aplicado en esta liquidación [936]
					addSignedKey(line, mod200, Mod2002014Key.LQ937, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. 2014 (*) Pendiente aplicación en ejercicios futuros [937]
					addSignedKey(line, mod200, Mod2002014Key.LQ694, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. Total. Pendiente aplicación al principio del periodo [694]
					addSignedKey(line, mod200, Mod2002014Key.LQ561, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. Total. Aplicado en esta liquidación [561]
					addSignedKey(line, mod200, Mod2002014Key.LQ695, isComplementary); // Rég. cooperativas - Detalle compensación cuotas. Total. Pendiente aplicación en ejercicios futuros [695]
					addEndLabel(line,label); // Etiqueta fin de pagina
				}
			})                                                                     
			                                                                       
		// [...] NO ESTA EN EL MODELO - Página 21: Operaciones fusión, escisión, canje valores... 
		// [...] NO ESTA EN EL MODELO - Página 22: Agrup. interés económico y UTES 
		// [...] NO ESTA EN EL MODELO - Página 23: Régimen especial de transparencia fiscal internacional
                                                                                   
		,PAG24 ("T200240", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR050) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen total de operaciones  [050]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR051) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en el extranjero [051]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR052) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Araba [052]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR053) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Gipuzkoa [053]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR054) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Bizkaia [054]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR055) // Tributación conjunta Estado y Adm.Forales - Convenio económico - Volumen operaciones en Navarra [055]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR056) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Territorio común [056]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.TR626, 5, 2) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Araba [626]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.TR627, 5, 2) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Gipuzkoa [627]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.TR628, 5, 2) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Bizkaia [628]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.TR629, 5, 2) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Navarra [629]
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002014Key.TR625, 5, 2) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Admón.del Estado [625]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR420) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Araba [420]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR421) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Gipuzkoa [421]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR426) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Bizkaia [426]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR427) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Navarra [427]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR600) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Total [600]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR402) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Araba [402]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR442) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Gipuzkoa [442]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR443) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Bizkaia [443]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR444) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Navarra [444]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR602) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Total [602]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR445) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Araba [445]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR446) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Gipuzkoa [446]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR447) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Bizkaia [447]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR448) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Navarra [448]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR604) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Total [604]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR449) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Araba [449]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR450) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Gipuzkoa [450]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR451) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Bizkaia [451]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR465) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Navarra [465]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR606) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Total [606]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR474) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Araba [474]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR475) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Gipuzkoa [475]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR476) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Bizkaia [476]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR477) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Navarra [477]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR612) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Total [612]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR482) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Araba [482]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR483) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Gipuzkoa [483]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR484) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Bizkaia [484]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR485) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Navarra [485]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR616) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Total [616]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR913) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Araba [913]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR914) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Gipuzkoa [914
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR915) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Bizkaia [915]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR916) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Navarra [916]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR642) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Total [642]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR486) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Araba [486]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR487) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Gipuzkoa [487]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR488) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Bizkaia [488]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR489) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Navarra [489]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR618) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Total [618]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR490) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Araba [490]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR491) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Gipuzkoa [491]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR492) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Bizkaia [492]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR493) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Navarra  [493]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR620) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Total [620]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR494) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Araba [494]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR495) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Gipuzkoa [495]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR496) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Bizkaia [496]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR497) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Navarra [497]
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.TR622) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Total [622]
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
			})	
			
		// [...] NO ESTAN EN EL MODELO - Páginas 25 a 52
			
		,DID ("T200DID", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)   // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  			 // Indicador de pagina complementaria
			,(line,mod200, label) -> line.append("0")            // Cuenta corriente tributaria "0" o "1" (no se usa)
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getYear(), 4, 0) )      // Identificación - Ejercicio
	        ,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPeriodType(),1,0) )  //	Tipo de ejercicio
	        ,(line,mod200, label) -> line.append( "0A" )                                                     // Período Impositivo "0A"
			,(line,mod200, label) -> line.append( mod200.getPeriodStart() == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodStart()) ) // Período Impositivo Inicio (ddmmaa)
			,(line,mod200, label) -> line.append( mod200.getPeriodEnd()   == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodEnd()) )   // Período Impositivo Fin (ddmmaa)
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseDocument(),9) )  // Identificación - NIF 
			,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseName(),40) )     // Identificación - Apellidos y nombre o Razón Social
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ552)   // Liquidación - Base imponible [552]                                    
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.LQ562)   // Liquidación - Cuota íntegra [562]                          
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002014Key.BN621)   // Liquidación - Líquido a ingresar o a devolver Estado [621]
			
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
			,(line,mod200, label) -> {
					double importe = mod200.getDoubleValue(Mod2002014Key.BN621); // importe a ingresar o a devolver
					
					line.append( AonFiscalFileUtils.text(importe<0 ? mod200.getDevType() : "",1) );                                   // Devolución - Renuncia o por Transferencia "blanco" "R","D"				
					line.append( AonFiscalFileUtils.signedZero(importe<0 ? Math.abs(importe) : 0.0, DEFAULT_SIZE, DEFAULT_DEC) );     // Devolución - Importe a devolver
					line.append( AonFiscalFileUtils.text(importe<0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "",34) );  // Devolución - Número de cuenta IBAN (si devolución por transferencia)			
				
					line.append( AonFiscalFileUtils.text(importe>0 ? mod200.getPayType() : "",1) );  // Ingreso - Modalidad de ingreso. Uno de los siguientes valores	"blanco", "I" Adeudo en	cuenta, "H" Efectivo, "U" Domiciliación
					line.append(" ");   // RESERVADO AEAT
					line.append(" ");   // RESERVADO AEAT
					line.append( AonFiscalFileUtils.signedZero(importe>0 ? importe : 0.0, DEFAULT_SIZE, DEFAULT_DEC) );               // Ingreso - Importe a ingresar
					line.append( AonFiscalFileUtils.text(importe>0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType())) ? mod200.getIban() : "",34) ); // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)			
	
					addSignedKey(line, mod200, Mod2002014Key.BN150);  // Abono/Compensación - Abono por conversión de activos impuesto diferido - A       
					addSignedKey(line, mod200, Mod2002014Key.BN506);  // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C
					
					line.append(importe == 0 ? "1" : "0"); // Cuota Cero "0" o "1"
			}
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
				
			})
		
		;
		 
		private String tag;
		private IPropertyFiller[] propertyFillers;
		
		private Pages2014(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002014 mod200, Writer line) throws IOException {
			
			// Controles determinadas páginas que solo se ponen si están marcados ciertos caracteres
			boolean addPage = true;
			
			// Página 9. Estado de Ingresos y Gastos Reconocidos. Solo si Balance Normal o Abreviado
			if (this == Pages2014.PAG09) {  
				addPage = (mod200.getBalanceType() == com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014.BalanceType.NORMAL) ||
				          (mod200.getBalanceType() == com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014.BalanceType.ABREVIADO);					
			}
			
			// Página 22. Agrupaciones de interes económico y UTES (regimen especial). Caracteres 013 o 014 marcados
			// La página 22 actualmente no está en el Modelo 200, luego por ahora nunca se pone, ni siquiera está definida en el enumerado
//			if (this == Pages2014.PAG22) {
//				addPage = (mod200.getDoubleValue(Mod2002014Key.C0013)==1) ||
//			              (mod200.getDoubleValue(Mod2002014Key.C0014)==1);
//			}
			
			// Página 24. Tributación Conjunta. Caracter 028 marcado
			if (this == Pages2014.PAG24) {
				addPage = (mod200.getDoubleValue(Mod2002014Key.C0028)==1);
			}			
			
			// Añadir el contenido de la página
			if (addPage) {
	 			for (IPropertyFiller propertyFiller : this.propertyFillers) {
	 				propertyFiller.propertyFill(line, mod200, this.tag);
				}     		
			}
		}
	}
	
	public static void fillWriter(Mod2002014 mod200, Writer line) throws IOException {
		
		line.append("<T2000"+mod200.getYear()+"0A0000>");  // Etiqueta inicio de fichero
		for (Pages2014 page : Pages2014.values()) {     			
			page.fillPage(mod200, line);			
		}	     			
		line.append("</T2000"+mod200.getYear()+"0A0000>");  // Etiqueta fin de fichero
		line.append("\r\n"); // Fin de registro. Constante CRLF
		line.close(); 
		
	}
	
	
	public static void main(String argv[]) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
		
		try {
			String filename = "/AEAT/200/2013";  // directorio por defecto
			
			// Mostrar una ventana de dialogo para seleccionar ficheros
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(filename));
            int res = fc.showOpenDialog(new JFrame());
		    
	        if (res == JFileChooser.APPROVE_OPTION) {
	        	filename = fc.getSelectedFile().getAbsolutePath();
        		System.out.println("***** Inicio Fichero : "+filename);
				System.out.println("");
				InputStream input = new FileInputStream(filename);
				
				// Convertir el fichero a mod200
				Mod2002014 mod2002014 = new Mod2002014();
				Mod2002013 mod2002013 = Mod2002013Reader.getMod2002013(input);
				Mod2002014Import2013.import2013(mod2002014, mod2002013);
				input.close();

				// prueba - dejar todas las claves a cero
				mod2002014.setKeysMap(new EnumMap<Mod2002014Key,DoubleVariable2014>(Mod2002014Key.class));
				
				//Writer line = new StringWriter();
				filename = "c:\\tmp\\prueba.txt";
				BufferedWriter line = new BufferedWriter(new FileWriter(filename));
				fillWriter(mod2002014, line);
				 
				// Mostramos la longitud de cada pagina 
			    input = new FileInputStream(filename);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();							
				Document doc = db.parse(new InputSource(new InputStreamReader(input,"ISO-8859-1")));
				
				for ( Pages2014 page : Pages2014.values()) {
					NodeList nodeList = doc.getElementsByTagName(page.tag);
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node node = nodeList.item(i);
						String lin = "<" + page.tag + ">" + node.getTextContent() + "</" + page.tag + ">";
						System.out.println(node.getNodeName()+" - "+lin.length());
					}
				}
				
				System.out.println("");
				System.out.println("***** Fin Fichero : "+filename);
				System.out.println("");
				
				// Mostramos el archivo creado, con el bloc de notas
				Runtime.getRuntime().exec("notepad.exe "+filename);
				
	        }	        
		} 
//	        catch (Exception e) {
//			//e.printStackTrace();			
//		}
		finally {
			System.exit(0);
		}
	}
	
}
