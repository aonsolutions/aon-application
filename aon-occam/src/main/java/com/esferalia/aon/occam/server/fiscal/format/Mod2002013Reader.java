package com.esferalia.aon.occam.server.fiscal.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013.BalanceType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002013Reader {
	
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat( );
	
	static {
		DECIMAL_FORMAT.setGroupingUsed(false);	
		DECIMAL_FORMAT.setNegativePrefix("N");		
	}
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(String line, Mod2002013 mod200);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Devuelve un String leyendo una cadena de la linea que se le pasa
	private static String getString(String line, int pos, int lon) {
		String c = AonStringUtils.substring(line,pos-1,pos+lon-1);
		if (c!=null) c = c.trim();
		return c;
	}
	
	// Devuelve un boolean leyendo una cadena de la linea que se le pasa
	private static boolean getBoolean(String line, int pos, int lon) {
		return "1".equals( getString(line,pos,lon) );
	}
	
	// Devuelve un integer leyendo una cadena de la linea que se le pasa
	private static int getInt(String line, int pos, int lon) {
		Integer i = AonNumberUtils.toInteger( getString(line,pos,lon)); 
		return i==null?0:i;
	}
	
	// Devuelve una fecha leyendo una cadena de la linea que se le pasa
	private static Date getDate(String line, int pos, int lon) {
		String c = AonStringUtils.substring(line,pos-1,pos+lon-1);
		if (AonStringUtils.isNotBlank(c) && !AonStringUtils.equals(c, "00000000")) {
			try {
				return DATE_FORMAT.parse(c.substring(0,4)+c.substring(4,6)+c.substring(6,8));
			} catch (ParseException e) {
				// En el caso (improbable) de que la fecha sea incorrecta, no lanzamos la excepcisn porque nos 
				// interesa mas la carga de datos que la validez de la fecha.  
			}
		}
		return null;
	}
	
	// Devuelve un double leyendo de la linea que se le pasa, en el texto que se lee los 2 ultimos digitos son decimales
	private static double getDouble(String line, int pos, int lon) {
		String s = AonStringUtils.substring(line,pos-1,pos+lon-1);
		double number = 0;
		if (AonStringUtils.isNotBlank(s)) {
			try {
				number = DECIMAL_FORMAT.parse(s).doubleValue();				
				number = AonMathUtils.round(number / 100);  // Los dos ultimos digitos son decimales
			} catch (ParseException e) {
				// Nada. Continuamos con la carga.
			}
		}
		return number; 
	}
	
	// Añade <casilla,valor> a las casillas del modelo 200 
	private static void setCasilla(Mod2002013 mod200, Mod2002013Key key, double valor) {
		DoubleVariable2013 t = new DoubleVariable2013(key);
		t.setValue(valor);
		mod200.addVariable(t);		
	}
	
    // Añade un representante a la lista de representantes
	// Se asume que si el nombre no está cumplimentado el representante está vacio, en ese caso no se añade
	private static void addLegalRepresentative(Mod2002013 mod200,String name,String document, Date notaryDate, String notary) {

		// Si el nombre está en blanco no se hace nada
		if (AonStringUtils.isBlank(name)) {
			return;
		}		
		
		// Controlar si la lista está creada en el modelo 200		
		if (mod200.getRepresentatives() == null ) {
			mod200.setRepresentatives( new LinkedList<LegalRepresentative>());
		}
		
		// Asignamos los datos al nuevo objeto
		LegalRepresentative lr = new LegalRepresentative();		
		lr.setName(name); 
		lr.setDocument(document);
		lr.setNotaryDate(notaryDate); 
		lr.setNotary(notary);
		
		// Añadimos elemento a la lista
		mod200.getRepresentatives().add(lr); 

	}
	
    // Añade un administrador a la lista de administradores
	// Se asume que si el nombre no está cumplimentado el administrador está vacio, en ese caso no se añade
	private static void addCompanyAdministrator(Mod2002013 mod200, String document, boolean isRepresentative, String name, String residence, int province) {

		// Si el nombre está en blanco no se hace nada
		if (AonStringUtils.isBlank(name)) {
			return;
		}		
				
		// Controlar si la lista está creada en el modelo 200		
		if (mod200.getAdministrators() == null ) {
			mod200.setAdministrators( new LinkedList<CompanyAdministrator>());
		}

	    // Crear un objeto y asignar los datos		
		CompanyAdministrator ca = new CompanyAdministrator();
		ca.setDocument(document);
		ca.setRepresentative(isRepresentative);
		ca.setName(name);
		ca.setResidence(residence);
		ca.setProvince(province);
		
		// Añadirlo a la lista
		mod200.getAdministrators().add(ca);
		
	}	
	
    // Añade una participacion (apartado B1) a la lista de participaciones (B1)
	// Se asume que si el nombre no está cumplimentado la participacion está vacia, en ese caso no se añade
	private static void addCompanyParticipationOut(Mod2002013 mod200, String document,	String name, String provinceCountry, double percent, double nominalValue,	double bookValue, double incomes, double aValue, double bValue, double cValue, double dValue, double capital, double reserve, double otherAmounts, double result) {
		
		// Si el nombre está en blanco no se hace nada
		if (AonStringUtils.isBlank(name)) {
			return;
		}		
		
		// Controlar si la lista está creada en el modelo 200
		if (mod200.getParticipationsOut() == null ) {
			mod200.setParticipationsOut(new LinkedList<CompanyParticipation>());
		}

		Country c = null;
		int province = 0; 
		if (AonStringUtils.isNumeric(provinceCountry)) {
			province = Integer.parseInt(provinceCountry);
		} else {
			c = Country.safeValueOf(provinceCountry);
		}

	    // Crear un objeto y asignar los datos		
		CompanyParticipation cp = new CompanyParticipation();
		cp.setDocument(document);
		cp.setName(name);
		cp.setProvince(province);		
		cp.setCountry(c==null?null:c.getIso2());		
		cp.setPercent(percent);
		cp.setNominalValue(nominalValue);
		cp.setBookValue(bookValue);
		cp.setIncomes(incomes);
		cp.setaValue(aValue);
		cp.setbValue(bValue);
		cp.setcValue(cValue);
		cp.setdValue(dValue);
		cp.setCapital(capital);
		cp.setReserve(reserve);
		cp.setOtherAmounts(otherAmounts);
		cp.setResult(result);
				
		// Añadirlo a la lista		
		mod200.getParticipationsOut().add(cp);		
		
	}
	
    // Añade una participacion (apartado B2) a la lista de participaciones (B2)
	// Se asume que si el nombre no está cumplimentado la participacion está vacia, en ese caso no se añade
	private static void addCompanyParticipationIn(Mod2002013 mod200, String document, boolean isRepresentative, String name, String provinceCountry, double nominalValue, double percent) {
		
		// Si el nombre está en blanco no se hace nada
		if (AonStringUtils.isBlank(name)) {
			return;
		}		
		
		// Controlar si la lista está creada en el modelo 200
		if (mod200.getParticipationsIn() == null ) {
			mod200.setParticipationsIn(new LinkedList<CompanyParticipation>());
		}
		Country c = null;
		int province = 0; 
		if (AonStringUtils.isNumeric(provinceCountry)) {
			province = Integer.parseInt(provinceCountry);
		} else {
			c = Country.safeValueOf(provinceCountry);
		}

	    // Crear un objeto y asignar los datos		
		CompanyParticipation cp = new CompanyParticipation();
		cp.setDocument(document);
		cp.setRepresentative(isRepresentative);
		cp.setName(name);
		cp.setProvince(province);		
		cp.setCountry(c==null?null:c.getIso2());		
		cp.setNominalValue(nominalValue);
		cp.setPercent(percent);		
				
		// Añadirlo a la lista
		mod200.getParticipationsIn().add(cp);
		
	}
	
	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Paginas {
		
		 PAG1 ("T200010", new IPropertyFiller[] {
			 (line,mod200) ->  mod200.setPeriodStart(getDate(line,11,8))            // Periodo Impositivo - Inicio					
			,(line,mod200) ->  mod200.setPeriodEnd(getDate(line,19,8))    			// Periodo Impositivo - Fin
			,(line,mod200) ->  mod200.setPeriodType(getInt(line,27,1))              // Identificación - Tipo de ejercicio
			,(line,mod200) ->  { 
				        // Controlar la posibilidad de que pueda venir el CNAE en blanco
				        // solo se inserta el punto si vienen los 4 digitos 
						String c=getString(line,28,4);
						if (c.length()==4)
							mod200.setCnae(c.substring(0,2)+"."+c.substring(2));
						else mod200.setCnae(c);
						}                // Identificación - C.N.A.E.  Actividad principal
			,(line,mod200) ->  mod200.setEnterpriseDocument(getString(line,32,9))   // Identificación - NIF 
			,(line,mod200) ->  mod200.setEnterpriseName(getString(line,41,40))      // Identificación - Apellidos y nombre o Razón Social
			,(line,mod200) ->  mod200.setEnterprisePhone1(getString(line,81,9))     // Identificación - Teléfono 1
			,(line,mod200) ->  mod200.setEnterprisePhone2(getString(line,90,9))     // Identificación - Teléfono 2
			,(line,mod200) ->  mod200.setYear(getInt(line,99,4))                    // Ejercicio
				 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0001,getInt(line,103,1))  // Entidad sin ánimo de lucro acogida régimen fiscal Título II Ley 49/2002 [001] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0002,getInt(line,104,1))  // Entidad parcialmente exenta [002]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0003,getInt(line,105,1))  // Sociedad de inversión de capital variable o fondo de inversión de carácter financiero [003]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0004,getInt(line,106,1))  // Sociedad de inversión inmobiliaria o fondo de inversión inmobiliaria [004]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0005,getInt(line,107,1))  // Comunidades titulares de montes vecinales en mano común [005]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0011,getInt(line,108,1))  // Entidad de tenencia de valores extranjeros [011]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0013,getInt(line,109,1))  // Agrupación de interés económico española o U.T.E. [013]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0014,getInt(line,110,1))  // Agrupación europea de  interés económico [014]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0017,getInt(line,111,1))  // Cooperativa protegida [017]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0018,getInt(line,112,1))  // Cooperativa especialmente protegida [018]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0019,getInt(line,113,1))  // Resto cooperativas [019]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0021,getInt(line,114,1))  // Establecimiento permanente [021]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0023,getInt(line,115,1))  // Gran empresa [023]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0024,getInt(line,116,1))  // Entidad de crédito [024]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0025,getInt(line,117,1))  // Entidad aseguradora [025]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0031,getInt(line,118,1))  // Entidades de  capital-riesgo [031]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0032,getInt(line,119,1))  // Sociedades desarrollo industrial regional [032]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0036,getInt(line,120,1))  // Sociedad de garantía recíproca o de reafianzamiento [036]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0048,getInt(line,121,1))  // Fondo de Pensiones Real Decreto Legislativo 1/2002 de 29 de noviembre [048]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0058,getInt(line,122,1))  // Mutua de seguros o Mutualidad de previsión social [058]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0060,getInt(line,123,1))  // Fondos o activos de titulización [060]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0006,getInt(line,124,1))  // Incentivos empresa de reducida dimensión ( cap XII, tít VII L.I.S )  [006]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0015,getInt(line,125,1))  // Entidad ZEC [015]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0022,getInt(line,126,1))  // Régimen entidades navieras en función del tonelaje [022]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0028,getInt(line,127,1))  // Tributación conjunta Estado/Diput.Cdad.Forales [028]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0047,getInt(line,128,1))  // Entidades sometidas a normativa foral [047]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0049,getInt(line,129,1))  // Regímenes especiales de normativa foral [049]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0029,getInt(line,130,1))  // Régimen especial Canarias [029]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0033,getInt(line,131,1))  // Régimen especial minería [033]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0034,getInt(line,132,1))  // Régimen especial hidrocarburos [034]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0038,getInt(line,133,1))  // Entidad dedicada al arrend.viviendas [038]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0046,getInt(line,134,1))  // Entidad en rég. atribución de rentas constituida en el extranjero con presencia en territorio español [046]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0012,getInt(line,135,1))  // SOCIMI [012]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0057,getInt(line,136,1))  // Entidades que aplican el régimen especial Ley 11/2009 (excepto SOCIMI) [057]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0020,getInt(line,137,1))  // Otros regímenes especiales [020]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0056,getInt(line,138,1))  // Tipo gravamen reducido mant.o creación empleo [056]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0007,getInt(line,139,1))  // Inclusión en base imponible rentas positivas art. 107 L.I.S. [007]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0008,getInt(line,140,1))  // Opción art. 107.6 L.I.S. [008]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0009,getInt(line,141,1))  // Sociedad dominante de grupo fiscal [009]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0010,getInt(line,142,1))  // Sociedad dependiente de grupo fiscal [010]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0016,getInt(line,143,1))  // Opción  art.51.2.b)  L.I.S. [016]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0026,getInt(line,144,1))  // Entidad  inactiva [026]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0027,getInt(line,145,1))  // Base imponible negativa o cero [027]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0030,getInt(line,146,1))  // Transmisión elementos patrimoniales arts. 26.2.d) y 84.1 L.I.S. [030]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0035,getInt(line,147,1))  // Opción art. 43.1 R.I.S. [035] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0037,getInt(line,148,1))  // Opción art. 43.3 R.I.S. [037]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0039,getInt(line,149,1))  // Entidad que forma parte de un grupo mercantil (art. 42 del Cód. Comercio) [039]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0043,getInt(line,150,1))  // Obligación información art. 15 R.I.S. [043]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0044,getInt(line,151,1))  // Obligación información art. 45 R.I.S. [044] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0045,getInt(line,152,1))  // Inversiones anticipadas - reserva inversiones en Canarias (art. 27.11 Ley 19/1994) [045]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0062,getInt(line,153,1))  // Régimen fiscal de operaciones de aportación de activos a sociedades para la gestión de activos (Ley 8/2012) [062]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0063,getInt(line,154,1))  // Tipo de gravamen reducido para entidades de nueva creción (D.A. 19ª LIS) [063]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0059,getInt(line,155,1))  // Opción  art.44.2 LIS [059]
			 
			,(line,mod200) ->  { int t = getInt(line,156,1); if (t>0) mod200.setBalanceType(t-1); }  // Balance y ECPN 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES                
			,(line,mod200) ->  { int t = getInt(line,157,1); if (t>0) mod200.setPygType(t-1);     }  // Pérdidas y ganancias 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
			
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0061,getInt(line,158,1))     // Estados de cuentas de Instituciones de inversión colectiva [061]
			,(line,mod200) ->  mod200.setFiscalGroup(getString(line,159,7))              // Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040] 
			,(line,mod200) ->  mod200.setDominantDocument(getString(line,166,9))         // N.I.F. de la sociedad dominante para entidades que hayan marcado la clave 010 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0041,getDouble(line,175,9))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.C0042,getDouble(line,184,9))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042] 
			,(line,mod200) ->  mod200.setComplementary(getBoolean(line,93,1))            // Declaración complementaria
			,(line,mod200) ->  mod200.setComplementaryReceipt(getString(line,194,13))    // Nº de justificante de la declaración anterior
			
            ,(line,mod200) ->  mod200.getSecretary().setName(getString(line,207,21))     // Nombre o Razón social - Secretario del Consejo de Administración 
			,(line,mod200) ->  mod200.getSecretary().setDocument(getString(line,228,9))  // N.I.F. - Secretario del Consejo de Administración
			,(line,mod200) ->  mod200.getSecretary().setIrnr(getDate(line,237,8))        // Fecha - Contribuyentes por el I.R.N.R. 
						
			,(line,mod200) ->  addLegalRepresentative(mod200, getString(line,245,36),  // Declaración representantes legales entidad - 1 - Nombre y apellidos
			                                                  getString(line,281, 9),  // Declaración representantes legales entidad - 1 - N.I.F
			                                                  getDate(  line,290, 8),  // Declaración representantes legales entidad - 1 - Fecha Poder
			                                                  getString(line,298,12))  // Declaración representantes legales entidad - 1 - Notaría			
			
			,(line,mod200) ->  addLegalRepresentative(mod200, getString(line,310,36),  // Declaración representantes legales entidad - 2 - Nombre y apellidos  
								                              getString(line,346, 9),  // Declaración representantes legales entidad - 2 - N.I.F              
								                              getDate(  line,355, 8),  // Declaración representantes legales entidad - 2 - Fecha Poder        
								                              getString(line,363,12))  // Declaración representantes legales entidad - 2 - Notaría			  
											
			,(line,mod200) ->  addLegalRepresentative(mod200, getString(line,375,36),  // Declaración representantes legales entidad - 3 - Nombre y apellidos 
								                  			  getString(line,411, 9),  // Declaración representantes legales entidad - 3 - N.I.F              
								                  			  getDate(  line,420, 8),  // Declaración representantes legales entidad - 3 - Fecha Poder        
								                  			  getString(line,428,12))  // Declaración representantes legales entidad - 3 - Notaría			  								                              
			
			})
			
		,PAG2 ("T200020", new IPropertyFiller[] {				
				
			 (line,mod200) ->  addCompanyAdministrator(mod200, getString( line, 11, 9),  // A. Relación de administradores .1 - N.I.F.      
															   getBoolean(line, 21, 1),  // A. Relación de administradores. 1 - RPTE.
															   getString( line, 22,40),  // A. Relación de administradores. 1 - Apellidos y nombre / Razón social 
															   getString( line, 62,17),  // A. Relación de administradores. 1 - Domicilio fiscal
															   getInt(    line, 79, 2))  // A. Relación de administradores .1 - Código Provincial
															   
			,(line,mod200) ->  addCompanyAdministrator(mod200, getString( line, 81, 9),  // A. Relación de administradores .2 - N.I.F.                           
			                                                   getBoolean(line, 91, 1),  // A. Relación de administradores. 2 - RPTE.                            
			                                                   getString( line, 92,40),  // A. Relación de administradores. 2 - Apellidos y nombre / Razón social
			                                                   getString( line,132,17),  // A. Relación de administradores. 2 - Domicilio fiscal                 
			                                                   getInt(    line,149, 2))  // A. Relación de administradores .2 - Código Provincial
			                                                   
			,(line,mod200) ->  addCompanyAdministrator(mod200, getString( line,151, 9),  // A. Relación de administradores .3 - N.I.F.                           
			                                                   getBoolean(line,161, 1),  // A. Relación de administradores. 3 - RPTE.                            
			                                                   getString( line,162,40),  // A. Relación de administradores. 3 - Apellidos y nombre / Razón social
			                                                   getString( line,202,17),  // A. Relación de administradores. 3 - Domicilio fiscal                 
			                                                   getInt(    line,219, 2))  // A. Relación de administradores .3 - Código Provincial                
			                                                   
			,(line,mod200) ->  addCompanyAdministrator(mod200, getString( line,221, 9),  // A. Relación de administradores .4 - N.I.F.                           
			                                                   getBoolean(line,231, 1),  // A. Relación de administradores. 4 - RPTE.                            
			                                                   getString( line,232,40),  // A. Relación de administradores. 4 - Apellidos y nombre / Razón social
			                                                   getString( line,272,17),  // A. Relación de administradores. 4 - Domicilio fiscal                 
			                                                   getInt(    line,289, 2))  // A. Relación de administradores .4 - Código Provincial                
			                                                   
			,(line,mod200) ->  addCompanyAdministrator(mod200, getString( line,291, 9),  // A. Relación de administradores .5 - N.I.F.                           
			                                                   getBoolean(line,301, 1),  // A. Relación de administradores. 5 - RPTE.                            
			                                                   getString( line,302,40),  // A. Relación de administradores. 5 - Apellidos y nombre / Razón social
			                                                   getString( line,342,17),  // A. Relación de administradores. 5 - Domicilio fiscal                 
			                                                   getInt(    line,359, 2))  // A. Relación de administradores .5 - Código Provincial                
			                                                   
			,(line,mod200) ->  addCompanyAdministrator(mod200, getString( line,361, 9),  // A. Relación de administradores .6 - N.I.F.                           
			                                                   getBoolean(line,371, 1),  // A. Relación de administradores. 6 - RPTE.                            
			                                                   getString( line,372,40),  // A. Relación de administradores. 6 - Apellidos y nombre / Razón social
			                                                   getString( line,412,17),  // A. Relación de administradores. 6 - Domicilio fiscal                 
			                                                   getInt(    line,429, 2))  // A. Relación de administradores .6 - Código Provincial                
				
			,(line,mod200) ->  addCompanyParticipationOut(mod200, getString(line, 431,15),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos participada - N.I.F.
			                                                      getString(line, 446,30),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos participada - Nombre o razón social
			                                                      getString(line, 476, 2),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos participada - Código provincia / país 
			                                                      getDouble(line, 478, 5),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos de la declarante - Porcentaje de participación 
			                                                      getDouble(line, 483,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos de la declarante - Valor nominal total de la participación
			                                                      getDouble(line, 500,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos de la declarante - Valor en libros (en el activo de la declarante) de la participación
			                                                      getDouble(line, 517,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos de la declarante - Ingresos por Dividendos recibidos en el ejercicio declarado
			                                                      getDouble(line, 534,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Correcciones valorativas - Corrección de valor pérdidas y ganancias ejercicio
			                                                      getDouble(line, 551,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Correcciones valorativas - Reversión de pérdidas por deterioro de valores
			                                                      getDouble(line, 568,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Correcciones valorativas - Efecto corrección valorativa en la BI del ejercicio
			                                                      getDouble(line, 585,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Corrrecciones valorativas - Saldo de correcciones fiscales (art. 12.3 LIS) pendientes a fin de ejercicio
			                                                      getDouble(line, 602,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos adicionales participada - Capital 
			                                                      getDouble(line, 619,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos adicionales participada - Reservas
			                                                      getDouble(line, 636,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos adicionales participada - Otras partidas del patrimonio neto
			                                                      getDouble(line, 653,17))	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 1 - Datos adicionales participada - Resultado del último ejercicio
			                                                                                 
            ,(line,mod200) ->  addCompanyParticipationOut(mod200, getString(line, 670,15),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos participada - N.I.F.
			                                                      getString(line, 685,30),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos participada - Nombre o razón social 
			                                                      getString(line, 715, 2),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos participada - Código provincia / país 
			                                                      getDouble(line, 717, 5),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos de la declarante - Porcentaje de participación 
			                                                      getDouble(line, 722,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos de la declarante - Valor nominal total de la participación
			                                                      getDouble(line, 739,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos de la declarante - Valor en libros (en el activo de la declarante) de la participación
			                                                      getDouble(line, 756,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos de la declarante - Ingresos por Dividendos recibidos en el ejercicio declarado
			                                                      getDouble(line, 773,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Correcciones valorativas - Corrección de valor pérdidas y ganancias ejercicio
			                                                      getDouble(line, 790,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Correcciones valorativas - Reversión de pérdidas por deterioro de valores
			                                                      getDouble(line, 807,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Correcciones valorativas - Efecto corrección valorativa en la BI del ejercicio
			                                                      getDouble(line, 824,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Corrrecciones valorativas - Saldo de correcciones fiscales (art. 12.3 LIS) pendientes a fin de ejercicio
			                                                      getDouble(line, 841,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos adicionales participada - Capital 
			                                                      getDouble(line, 858,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos adicionales participada - Reservas
			                                                      getDouble(line, 875,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos adicionales participada - Otras partidas del patrimonio neto
			                                                      getDouble(line, 892,17))	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 2 - Datos adicionales participada - Resultado del último ejercicio
			                                                                                 
			,(line,mod200) ->  addCompanyParticipationOut(mod200, getString(line, 909,15),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos participada - N.I.F.
			                                                      getString(line, 924,30),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos participada - Nombre o razón social
			                                                      getString(line, 954, 2),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos participada - Código provincia / país
			                                                      getDouble(line, 956, 5),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos de la declarante - Porcentaje de participación 
			                                                      getDouble(line, 961,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos de la declarante - Valor nominal total de la participación
			                                                      getDouble(line, 978,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos de la declarante - Valor en libros (en el activo de la declarante) de la participación
			                                                      getDouble(line, 995,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos de la declarante - Ingresos por Dividendos recibidos en el ejercicio declarado
			                                                      getDouble(line,1012,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Correcciones valorativas - Corrección de valor pérdidas y ganancias ejercicio
			                                                      getDouble(line,1029,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Correcciones valorativas - Reversión de pérdidas por deterioro de valores
			                                                      getDouble(line,1046,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Correcciones valorativas - Efecto corrección valorativa en la BI del ejercicio
			                                                      getDouble(line,1063,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Corrrecciones valorativas - Saldo de correcciones fiscales (art. 12.3 LIS) pendientes a fin de ejercicio
			                                                      getDouble(line,1080,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos adicionales participada - Capital 
			                                                      getDouble(line,1097,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos adicionales participada - Reservas
			                                                      getDouble(line,1114,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos adicionales participada - Otras partidas del patrimonio neto
			                                                      getDouble(line,1131,17))	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 3 - Datos adicionales participada - Resultado del último ejercicio
			                                                                                 
			,(line,mod200) ->  addCompanyParticipationOut(mod200, getString(line,1148,15),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos participada - N.I.F.
			                                                      getString(line,1163,30),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos participada - Nombre o razón social 
			                                                      getString(line,1193, 2),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos participada - Código provincia / país
			                                                      getDouble(line,1195, 5),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos de la declarante - Porcentaje de participación 
			                                                      getDouble(line,1200,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos de la declarante - Valor nominal total de la participación
			                                                      getDouble(line,1217,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos de la declarante - Valor en libros (en el activo de la declarante) de la participación
			                                                      getDouble(line,1234,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos de la declarante - Ingresos por Dividendos recibidos en el ejercicio declarado
			                                                      getDouble(line,1251,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Correcciones valorativas - Corrección de valor pérdidas y ganancias ejercicio
			                                                      getDouble(line,1268,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Correcciones valorativas - Reversión de pérdidas por deterioro de valores
			                                                      getDouble(line,1285,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Correcciones valorativas - Efecto corrección valorativa en la BI del ejercicio
			                                                      getDouble(line,1302,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Correcciones valorativas - Saldo de correcciones fiscales (art. 12.3 LIS) pendientes a fin de ejercicio
			                                                      getDouble(line,1319,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos adicionales participada - Capital 
			                                                      getDouble(line,1336,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos adicionales participada - Reservas
			                                                      getDouble(line,1353,17),	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos adicionales participada - Otras partidas del patrimonio neto
			                                                      getDouble(line,1370,17))	// B. Participaciones directas - B.1. Participaciones declarante en otras entidades - Entidad 4 - Datos adicionales participada - Resultado del último ejercicio
			                                                   
			,(line,mod200) ->  addCompanyParticipationIn(mod200, getString( line,1387,15),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 1 - N.I.F. 
			                                                     getBoolean(line,1402, 1),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 1 - RPTE.			                                                      
			                                                     getString( line,1404,37),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 1 - Apellidos y nombre / Razón social 
			                                                     getString( line,1441, 2),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 1 - Código provincia / país
			                                                     getDouble( line,1443,17),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 1 - Nominal 
			                                                     getDouble( line,1460, 5))	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 1 - % Particip.
			                                                 	  	                         
			,(line,mod200) ->  addCompanyParticipationIn(mod200, getString( line,1465,15),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 2 - N.I.F. 
			                                                     getBoolean(line,1480, 1),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 2 - RPTE. 
			                                                     getString( line,1482,37),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 2 - Apellidos y nombre / Razón social 
			                                                     getString( line,1519, 2),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 2 - Código provincia / país 
			                                                     getDouble( line,1521,17),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 2 - Nominal 
			                                                     getDouble( line,1538, 5))	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 2 - % Particip.
			                                                                                 
		    ,(line,mod200) ->  addCompanyParticipationIn(mod200, getString( line,1543,15),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 3 - N.I.F. 
			                                                     getBoolean(line,1558, 1), 	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 3 - RPTE.
			                                                     getString( line,1560,37),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 3 - Apellidos y nombre / Razón social 
			                                                     getString( line,1597, 2),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 3 - Código provincia / país 
			                                                     getDouble( line,1599,17),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 3 - Nominal 
			                                                     getDouble( line,1616, 5))	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 3 - % Particip.
			                                                                                 
			,(line,mod200) ->  addCompanyParticipationIn(mod200, getString( line,1621,15),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 4 - N.I.F. 
			                                                     getBoolean(line,1636, 1),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 4 - RPTE. 
			                                                     getString( line,1638,37),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 4 - Apellidos y nombre / Razón social 
			                                                     getString( line,1675, 2),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 4 - Código provincia / país 
			                                                     getDouble( line,1677,17),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 4 - Nominal 
			                                                     getDouble( line,1694, 5)) 	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 4 - % Particip.
			                                                                                 
			,(line,mod200) ->  addCompanyParticipationIn(mod200, getString( line,1699,15),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 5 - N.I.F. 
			                                                     getBoolean(line,1714, 1),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 5 - RPTE.
			                                                     getString( line,1716,37),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 5 - Apellidos y nombre / Razón social. 
			                                                     getString( line,1753, 2),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 5 - Código provincia / país 
			                                                     getDouble( line,1755,17),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 5 - Nominal
			                                                     getDouble( line,1772, 5))	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 5 - % Particip.
			                                                                                 
			,(line,mod200) ->  addCompanyParticipationIn(mod200, getString( line,1777,15),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 6 - N.I.F. 
			                                                     getBoolean(line,1792, 1),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 6 - RPTE.
			                                                     getString( line,1794,37),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 6 - Apellidos y nombre / Razón social 
			                                                     getString( line,1831, 2),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 6 - Código provincia / país 
			                                                     getDouble( line,1833,17),	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 6 - Nominal
			                                                     getDouble( line,1850, 5))	// B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante - 6 - % Particip.
			
		    // Estas casillas solo deben asignarse con los valores de la primera pagina, pues las paginas complementarias los pueden llevar a cero
			// La posicion 10 de la linea indica si es pagina complementaria, si esta vacia no lo es y si lleva una "C" si que lo es                                                     
		    ,(line,mod200) -> { if (AonStringUtils.isBlank(getString(line,10,1))) setCasilla(mod200,Mod2002013Key.POR51,getDouble(line,1855,5)); } // B .Participaciones directas - B.2. Suma de  porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(line,mod200) -> { if (AonStringUtils.isBlank(getString(line,10,1))) setCasilla(mod200,Mod2002013Key.PORES,getDouble(line,1860,5)); } // B. Participaciones directas - B.2. Suma de porcentajes de participaciones en situaciones especiales
			})
				
		,PAG3 ("T200030", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA101,getDouble(line, 11,17))  // Balance: Activo (I) - Activo - ACTIVO NO CORRIENTE [101]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA102,getDouble(line, 28,17))  // Balance: Activo (I) - Activo - Inmovilizado intangible  [102]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA103,getDouble(line, 45,17))  // Balance: Activo (I) - Activo - Desarrollo  [103]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA104,getDouble(line, 62,17))  // Balance: Activo (I) - Activo - Concesiones  [104]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA105,getDouble(line, 79,17))  // Balance: Activo (I) - Activo - Patentes, licencias, marcas y similares  [105]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA106,getDouble(line, 96,17))  // Balance: Activo (I) - Activo - Fondo de comercio  [106]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA107,getDouble(line,113,17))  // Balance: Activo (I) - Activo - Aplicaciones informáticas  [107]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA108,getDouble(line,130,17))  // Balance: Activo (I) - Activo - Investigación  [108]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA700,getDouble(line,147,17))  // Balance: Activo (I) - Activo - Propiedad intelectual  [700]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA701,getDouble(line,164,17))  // Balance: Activo (I) - Activo - Derechos de emisión de gases de efecto invernadero [701]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA109,getDouble(line,181,17))  // Balance: Activo (I) - Activo - Otro inmovilizado intangible  [109]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA110,getDouble(line,198,17))  // Balance: Activo (I) - Activo - Resto  [110]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA111,getDouble(line,215,17))  // Balance: Activo (I) - Activo - Inmovilizado material  [111]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA112,getDouble(line,232,17))  // Balance: Activo (I) - Activo - Terrenos y construcciones  [112]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA113,getDouble(line,249,17))  // Balance: Activo (I) - Activo - Instalaciones técnicas y otro inmovilizado material  [113]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA114,getDouble(line,266,17))  // Balance: Activo (I) - Activo - Inmovilizado en curso y anticipos [114]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA115,getDouble(line,283,17))  // Balance: Activo (I) - Activo - Inversiones inmobiliarias [115]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA116,getDouble(line,300,17))  // Balance: Activo (I) - Activo - Terrenos [116]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA117,getDouble(line,317,17))  // Balance: Activo (I) - Activo - Construcciones [117]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA118,getDouble(line,334,17))  // Balance: Activo (I) - Activo - Inversiones en empresas del grupo y asociadas [118]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA119,getDouble(line,351,17))  // Balance: Activo (I) - Activo - Instrumentos de patrimonio [119]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA120,getDouble(line,368,17))  // Balance: Activo (I) - Activo - Créditos a empresas [120]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA121,getDouble(line,385,17))  // Balance: Activo (I) - Activo - Valores representativos de deuda [121]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA122,getDouble(line,402,17))  // Balance: Activo (I) - Activo - Derivados [122]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA123,getDouble(line,419,17))  // Balance: Activo (I) - Activo - Otros activos financieros [123]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA124,getDouble(line,436,17))  // Balance: Activo (I) - Activo - Otras inversiones [124]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA125,getDouble(line,453,17))  // Balance: Activo (I) - Activo - Resto [125]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA126,getDouble(line,470,17))  // Balance: Activo (I) - Activo - Inversiones financieras a largo plazo [126]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA127,getDouble(line,487,17))  // Balance: Activo (I) - Activo - Instrumentos de patrimonio [127]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA128,getDouble(line,504,17))  // Balance: Activo (I) - Activo - Créditos a terceros [128]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA129,getDouble(line,521,17))  // Balance: Activo (I) - Activo - Valores representativos de deuda [129]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA130,getDouble(line,538,17))  // Balance: Activo (I) - Activo - Derivados [130]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA131,getDouble(line,555,17))  // Balance: Activo (I) - Activo - Otros activos financieros [131]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA132,getDouble(line,572,17))  // Balance: Activo (I) - Activo - Otras inversiones [132]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA133,getDouble(line,589,17))  // Balance: Activo (I) - Activo - Resto [133]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA134,getDouble(line,606,17))  // Balance: Activo (I) - Activo - Activos por impuesto diferido [134]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA135,getDouble(line,623,17))  // Balance: Activo (I) - Activo - Deudores comerciales no corrientes [135]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA136,getDouble(line,640,17))  // Balance: Activo (I) - Activo - ACTIVO CORRIENTE [136]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA137,getDouble(line,657,17))  // Balance: Activo (I) - Activo - Activos no corrientes mantenidos para la venta [137]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA138,getDouble(line,674,17))  // Balance: Activo (I) - Activo - Existencias [138]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA139,getDouble(line,691,17))  // Balance: Activo (I) - Activo - Comerciales  [139]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA140,getDouble(line,708,17))  // Balance: Activo (I) - Activo - Materias primas y otros aprovisionamientos [140]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA141,getDouble(line,725,17))  // Balance: Activo (I) - Activo - Productos en curso [141]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA142,getDouble(line,742,17))  // Balance: Activo (I) - Activo - Productos en curso - De ciclo largo de producción  [142]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA143,getDouble(line,759,17))  // Balance: Activo (I) - Activo - Productos en curso - De ciclo corto de producción  [143]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA144,getDouble(line,776,17))  // Balance: Activo (I) - Activo - Productos terminados [144]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA145,getDouble(line,793,17))  // Balance: Activo (I) - Activo - Productos terminados - De ciclo largo de producción  [145]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA146,getDouble(line,810,17))  // Balance: Activo (I) - Activo - Productos terminados - De ciclo corto de producción  [146]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA147,getDouble(line,827,17))  // Balance: Activo (I) - Activo - Subproductos, residuos y materiales recuperados [147]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA148,getDouble(line,844,17))  // Balance: Activo (I) - Activo - Anticipos a proveedores [148]
			})
				
		,PAG4 ("T200040", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA149,getDouble(line,11	,17))  // Balance: Activo (II) - Activo - Deudores comerciales y otras cuentas a cobrar [149]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA150,getDouble(line,28	,17))  // Balance: Activo (II) - Activo - Clientes por ventas y prestaciones de servicios [150]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA151,getDouble(line,45	,17))  // Balance: Activo (II) - Activo - Clientes por ventas y prestaciones de servicios - Clientes por ventas y prestaciones de servicios a largo plazo [151]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA152,getDouble(line,62	,17))  // Balance: Activo (II) - Activo - Clientes por ventas y prestaciones de servicios - Clientes por ventas y prestaciones de servicios a corto plazo [152]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA153,getDouble(line,79	,17))  // Balance: Activo (II) - Activo - Clientes empresas del grupo y asociadas [153]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA154,getDouble(line,96	,17))  // Balance: Activo (II) - Activo - Deudores varios [154]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA155,getDouble(line,113	,17))  // Balance: Activo (II) - Activo - Personal [155]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA156,getDouble(line,130	,17))  // Balance: Activo (II) - Activo - Activos por impuesto corriente [156]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA157,getDouble(line,147	,17))  // Balance: Activo (II) - Activo - Otros créditos con las Administraciones Públicas [157]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA158,getDouble(line,164	,17))  // Balance: Activo (II) - Activo - Accionistas (socios) por desembolsos exigidos [158]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA159,getDouble(line,181	,17))  // Balance: Activo (II) - Activo - Otros deudores [159]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA160,getDouble(line,198	,17))  // Balance: Activo (II) - Activo - Inversiones en empresas del grupo y asociadas a corto plazo [160]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA161,getDouble(line,215	,17))  // Balance: Activo (II) - Activo - Instrumentos de patrimonio  [161]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA162,getDouble(line,232	,17))  // Balance: Activo (II) - Activo - Créditos a empresas  [162]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA163,getDouble(line,249	,17))  // Balance: Activo (II) - Activo - Valores representativos de deuda  [163]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA164,getDouble(line,266	,17))  // Balance: Activo (II) - Activo - Derivados  [164]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA165,getDouble(line,283	,17))  // Balance: Activo (II) - Activo - Otros activos financieros  [165]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA166,getDouble(line,300	,17))  // Balance: Activo (II) - Activo - Otras inversiones  [166]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA167,getDouble(line,317	,17))  // Balance: Activo (II) - Activo - Resto  [167]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA168,getDouble(line,334	,17))  // Balance: Activo (II) - Activo - Inversiones financieras a corto plazo  [168]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA169,getDouble(line,351	,17))  // Balance: Activo (II) - Activo - Instrumentos de patrimonio  [169]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA170,getDouble(line,368	,17))  // Balance: Activo (II) - Activo - Créditos a empresas  [170]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA171,getDouble(line,385	,17))  // Balance: Activo (II) - Activo - Valores representativos de deuda [171]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA172,getDouble(line,402	,17))  // Balance: Activo (II) - Activo - Derivados [172]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA173,getDouble(line,419	,17))  // Balance: Activo (II) - Activo - Otros activos financieros [173]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA174,getDouble(line,436	,17))  // Balance: Activo (II) - Activo - Otras inversiones [174]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA175,getDouble(line,453	,17))  // Balance: Activo (II) - Activo - Resto [175]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA176,getDouble(line,470	,17))  // Balance: Activo (II) - Activo - Periodificaciones a corto plazo [176]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA177,getDouble(line,487	,17))  // Balance: Activo (II) - Activo - Efectivo y otros activos líquidos equivalentes [177]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA178,getDouble(line,504	,17))  // Balance: Activo (II) - Activo - Tesorería [178]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA179,getDouble(line,521	,17))  // Balance: Activo (II) - Activo - Otros activos líquidos equivalentes [179]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BA180,getDouble(line,538	,17))  // Balance: Activo (II) - Activo - TOTAL ACTIVO [180]				
			})

		,PAG5 ("T200050", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP185,getDouble(line, 11,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - PATRIMONIO NETO [185]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP186,getDouble(line, 28,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Fondos propios [186]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP187,getDouble(line, 45,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Capital [187]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP188,getDouble(line, 62,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Capital escriturado [188]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP189,getDouble(line, 79,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Capital no exigido [189]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP190,getDouble(line, 96,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Prima de emisión [190]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP191,getDouble(line,113,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Reservas [191]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP192,getDouble(line,130,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Legal y estatutarias [192]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP193,getDouble(line,147,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras reservas [193]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP702,getDouble(line,164,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Reserva de revalorización [702]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP194,getDouble(line,181,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Acciones y participaciones en patrimonio propias [194]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP195,getDouble(line,198,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Resultados de ejercicios anteriores [195]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP196,getDouble(line,215,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Remanente [196]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP197,getDouble(line,232,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Resultados negativos de ejercicios anteriores [197]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP198,getDouble(line,249,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras aportaciones de socios [198]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP199,getDouble(line,266,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Resultado del ejercicio [199]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP200,getDouble(line,283,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Dividendo a cuenta [200]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP201,getDouble(line,300,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otros instrumentos de patrimonio neto [201]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP202,getDouble(line,317,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Ajustes por cambios de valor [202]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP203,getDouble(line,334,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Activos financieros disponibles para la venta [203]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP204,getDouble(line,351,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Operaciones de cobertura [204]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP205,getDouble(line,368,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Activos no corrientes y pasivos vinculados [205]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP206,getDouble(line,385,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Diferencia de conversión [206]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP207,getDouble(line,402,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otros  [207]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP208,getDouble(line,419,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Ajustes en patrimonio neto [208]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP209,getDouble(line,436,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Subvenciones, donaciones y legados recibidos [209]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP210,getDouble(line,453,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - PASIVO NO CORRIENTE [210]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP211,getDouble(line,470,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Provisiones a largo plazo  [211]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP212,getDouble(line,487,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Obligaciones por prestaciones a largo plazo al personal  [212]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP213,getDouble(line,504,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Actuaciones medioambientales  [213]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP214,getDouble(line,521,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Provisiones por reestructuración  [214]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP215,getDouble(line,538,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras provisiones  [215]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP216,getDouble(line,555,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deudas a largo plazo  [216]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP217,getDouble(line,572,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Obligaciones y otros valores negociables  [217]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP218,getDouble(line,589,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deudas con entidades de crédito  [218]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP219,getDouble(line,606,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Acreedores por arrendamiento financiero  [219]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP220,getDouble(line,623,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Derivados  [220]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP221,getDouble(line,640,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otros pasivos financieros [221]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP222,getDouble(line,657,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Otras deudas a largo plazo [222]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP223,getDouble(line,674,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deudas con empresas del grupo y asociadas a largo plazo [223]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP224,getDouble(line,691,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Pasivos por impuesto diferido [224]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP225,getDouble(line,708,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Periodificaciones a largo plazo [225]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP226,getDouble(line,725,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Acreedores comerciales no corrientes [226]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP227,getDouble(line,742,17))  // Balance: Patrimonio neto y pasivo (I) - Patrimonio neto y pasivo - Deuda con características especiales a largo plazo [227]
			})

		,PAG6 ("T200060", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP228,getDouble(line, 11,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - PASIVO CORRIENTE [228]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP229,getDouble(line, 28,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Pasivos vinculados con activos no corrientes [229]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP230,getDouble(line, 45,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Provisiones a corto plazo [230]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP703,getDouble(line, 62,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Provisiones por derechos emisión de gases de efecto invernadero [703]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP704,getDouble(line, 79,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otras provisiones [704]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP231,getDouble(line, 96,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deudas a corto plazo [231]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP232,getDouble(line,113,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Obligaciones y otros valores negociables [232]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP233,getDouble(line,130,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deudas con entidades de crédito [233]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP234,getDouble(line,147,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Acreedores por arrendamiento financiero [234]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP235,getDouble(line,164,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Derivados [235]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP236,getDouble(line,181,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otros pasivos financieros [236]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP237,getDouble(line,198,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otras deudas a corto plazo [237]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP238,getDouble(line,215,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deudas con empresas del grupo y asociadas a corto plazo [238]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP239,getDouble(line,232,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Acreedores comerciales y otras cuentas a pagar [239]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP240,getDouble(line,249,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores [240]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP241,getDouble(line,266,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores - Proveedores a largo plazo [241]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP242,getDouble(line,283,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores - Proveedores a corto plazo [242]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP243,getDouble(line,300,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Proveedores, empresas del grupo y asociadas [243]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP244,getDouble(line,317,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Acreedores varios [244]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP245,getDouble(line,334,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Personal (remuneraciones pendientes de pago) [245]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP246,getDouble(line,351,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Pasivos por impuesto corriente [246]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP247,getDouble(line,368,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otras deudas con las Administraciones Públicas [247]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP248,getDouble(line,385,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Anticipos de clientes [248]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP249,getDouble(line,402,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Otros acreedores [249]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP250,getDouble(line,419,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Periodificaciones a corto plazo [250]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP251,getDouble(line,436,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - Deuda con características especiales a corto plazo  [251]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BP252,getDouble(line,453,17))  // Balance: Patrimonio neto y pasivo (II) - Patrimonio neto y pasivo - TOTAL PATRIMONIO NETO Y PASIVO [252]				
			})
				
		,PAG7 ("T200070", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG255,getDouble(line, 11,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Importe neto de la cifra de negocios [255]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG256,getDouble(line, 28,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ventas [256]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG257,getDouble(line, 45,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Prestaciones de servicios [257]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG705,getDouble(line, 62,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding [705]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG706,getDouble(line, 79,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding  - De participaciones en instrumentos patrimonio [706]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG707,getDouble(line, 96,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding  - De valores negociables y otros instrumentos financieros [707]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG708,getDouble(line,113,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas -  Ingresos carácter financiero sociedades holding  - Resto [708]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG258,getDouble(line,130,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Variación de existencias [258]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG259,getDouble(line,147,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Trabajos realizados por la empresa para su activo [259]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG260,getDouble(line,164,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Aprovisionamientos [260]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG261,getDouble(line,181,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Consumo de mercaderías [261]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG262,getDouble(line,198,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Consumo de materias primas y otras materias consumibles [262]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG263,getDouble(line,215,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Trabajos realizados por otras empresas [263]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG264,getDouble(line,232,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro de mercaderías, materias primas [264]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG265,getDouble(line,249,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros ingresos de explotación [265]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG266,getDouble(line,266,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos accesorios y otros de gestión corriente [266]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG267,getDouble(line,283,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos accesorios y otros de gestión corriente - Ingresos arrendamientos [267]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG268,getDouble(line,300,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos accesorios y otros de gestión corriente - Resto [268]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG269,getDouble(line,317,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Subvenciones de explotación [269]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG270,getDouble(line,334,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Gastos de personal  [270]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG271,getDouble(line,351,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Sueldos, salarios y asimilados [271]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG273,getDouble(line,368,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Indemnizaciones [273]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG274,getDouble(line,385,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Seguridad Social a cargo de la empresa [274]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG275,getDouble(line,402,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Retribuciones a largo plazo por sistemas de aportación [275]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG276,getDouble(line,419,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Retribuciones mediante instrumentos de patrimonio [276]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG277,getDouble(line,436,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros gastos sociales [277]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG278,getDouble(line,453,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Provisiones [278]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG279,getDouble(line,470,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros gastos de explotación [279]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG280,getDouble(line,487,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Servicios exteriores [280]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG281,getDouble(line,504,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Tributos [281]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG282,getDouble(line,521,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Pérdidas, deterioro y variación de provisiones por operaciones comerciales [282]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG283,getDouble(line,538,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros gastos de gestión corriente [283]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG709,getDouble(line,555,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Gastos por emisión de gases de efecto invernadero [709]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG284,getDouble(line,572,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Amortización del inmovilizado [284]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG285,getDouble(line,589,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Imputación de subvenciones de inmovilizado no financiero y otras [285]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG286,getDouble(line,606,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Excesos de provisiones [286]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG287,getDouble(line,623,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y resultado por enajenaciones del inmovilizado [287]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG288,getDouble(line,640,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y pérdidas [288]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG289,getDouble(line,657,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y pérdidas - Deterioros [289]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG290,getDouble(line,674,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y pérdidas - Reversión de deterioros [290]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG291,getDouble(line,691,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Resultados por enajenaciones y otras [291]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG292,getDouble(line,708,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Resultados por enajenaciones y otras - Beneficios [292]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG293,getDouble(line,725,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Resultados por enajenaciones y otras - Pérdidas [293]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG710,getDouble(line,742,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Deterioro y resultados por enajenaciones del inmovilizado de las sociedades holding [710]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG294,getDouble(line,759,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Diferencia negativa de combinaciones de negocio [294]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG295,getDouble(line,776,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Otros resultados [295]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG296,getDouble(line,793,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - RESULTADO DE EXPLOTACION [296]				
			})

		,PAG8 ("T200080", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG297,getDouble(line, 11,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Ingresos financieros [297]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG298,getDouble(line, 28,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De participaciones en instrumentos de patrimonio [298]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG299,getDouble(line, 45,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De participaciones en instrumentos de patrimonio - En empresas del grupo y asociadas [299]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG300,getDouble(line, 62,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De participaciones en instrumentos de patrimonio - En terceros [300]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG301,getDouble(line, 79,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De valores negociables y otros instrumentos financieros  [301]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG302,getDouble(line, 96,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De valores negociables y otros instrumentos financieros  - De empresas del grupo y asociadas  [302]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG303,getDouble(line,113,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - De valores negociables y otros instrumentos financieros  - De terceros  [303]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG304,getDouble(line,130,17))  // Cuenta de pérdidas y ganancias (I) - Operaciones continuadas - Imputación de subvenciones, donaciones y legados  [304]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG305,getDouble(line,147,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Gastos financieros [305]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG306,getDouble(line,164,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Por deudas con empresas del grupo y asociadas [306]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG307,getDouble(line,181,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Por deudas con terceros [307]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG308,getDouble(line,198,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Por actualización de provisiones [308]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG309,getDouble(line,215,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Variación de valor razonable en instrumentos financieros [309]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG310,getDouble(line,232,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Cartera de negociación y otros [310]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG311,getDouble(line,249,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Imputación por activos financieros disponibles para la venta  [311]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG312,getDouble(line,266,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Diferencias de cambio [312]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG313,getDouble(line,283,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioro y resultado por enajenaciones de instrumentos financieros   [313]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG314,getDouble(line,300,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas [314]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG315,getDouble(line,317,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Deterioros, empresas del grupo, asociadas y vinculadas [315]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG316,getDouble(line,334,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Deterioros, otras empresas [316]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG317,getDouble(line,351,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Reversión de deterioros, empresas del grupo, asociadas y vinculadas [317]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG318,getDouble(line,368,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Deterioros y pérdidas - Reversión de deterioros, otras empresas [318]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG319,getDouble(line,385,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras [319]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG320,getDouble(line,402,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Beneficios, empresas del grupo, asociadas y vinculadas [320]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG321,getDouble(line,419,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Beneficios, otras empresas [321]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG322,getDouble(line,436,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Pérdidas, empresas del grupo, asociadas y vinculadas  [322]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG323,getDouble(line,453,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resultados por enajenaciones y otras - Pérdidas, otras empresas  [323]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG329,getDouble(line,470,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Otros ingresos y gastos de carácter financiero [329]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG330,getDouble(line,487,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Incorporación al activo de gastos financieros [330]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG331,getDouble(line,504,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Ingresos financieros derivados de convenios de acreedores [331]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG332,getDouble(line,521,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Resto de ingresos y gastos [332] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG324,getDouble(line,538,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - RESULTADO FINANCIERO [324]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG325,getDouble(line,555,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - RESULTADO ANTES DE IMPUESTOS [325]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG326,getDouble(line,572,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - Impuestos sobre beneficios  [326]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG327,getDouble(line,589,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones continuadas - RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES CONTINUADAS [327]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG328,getDouble(line,606,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones interrumpidas - RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES INTERRUMPIDAS NETO DE IMPUESTOS [328]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.PG500,getDouble(line,623,17))  // Cuenta de pérdidas y ganancias (II) - Operaciones interrumpidas - RESULTADO DE LA CUENTA DE PÉRDIDAS Y GANANCIAS [500]
			})
			
		,PAG9 ("T200090", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0500,getDouble(line, 11,17))  // Estado de cambios patrimonio neto (I) - Resultado de la cuenta de pérdidas y ganancias  [500]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0336,getDouble(line, 28,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por valoración de instrumentos financieros  [336]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0337,getDouble(line, 45,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Activos financieros disponibles para la venta [337]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0338,getDouble(line, 62,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Otros ingresos/gastos [338]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0339,getDouble(line, 79,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por coberturas de flujos de efectivo [339]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0340,getDouble(line, 96,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Subvenciones, donaciones y legados recibidos [340]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0341,getDouble(line,113,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por ganancias y pérdidas actuariales [341]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0342,getDouble(line,130,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Por activos no corrientes y pasivos vinculados  [342]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0343,getDouble(line,147,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Diferencias de conversión [343]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0344,getDouble(line,164,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Efecto impositivo [344]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0345,getDouble(line,181,17))  // Estado de cambios patrimonio neto (I) - Ingresos y gastos imputados al patrimonio neto - Total ingresos y gastos imputados en el patrimonio neto [345]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0346,getDouble(line,198,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Por valoración de instrumentos financieros [346]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0347,getDouble(line,215,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Activos financieros disponibles para la venta [347]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0348,getDouble(line,232,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Otros ingresos/gastos [348]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0349,getDouble(line,249,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Por coberturas de flujos de efectivo [349]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0350,getDouble(line,266,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Subvenciones, donaciones y legados recibidos [350]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0351,getDouble(line,283,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Por activos no corrientes y pasivos vinculados [351]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0352,getDouble(line,300,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Diferencias de conversión [352]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0353,getDouble(line,317,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Efecto impositivo [353]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0354,getDouble(line,334,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - Total transferencia a la cuenta de pérdidas y ganancias [354]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.T0355,getDouble(line,351,17))  // Estado de cambios patrimonio neto (I) - Transferencias a la cta. pérdidas y ganancias - TOTAL DE INGRESOS Y GASTOS RECONOCIDOS [355]
			})
			
		,PAG10 ("T200100", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC380,getDouble(line,  11,17))  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Capital - Escriturado [380]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC381,getDouble(line,  28,17))  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Capital - No exigido  [381]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC382,getDouble(line,  45,17))  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Prima de emisión  [382]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC383,getDouble(line,  62,17))  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Reservas  [383]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC384,getDouble(line,  79,17))  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Acciones y participaciones propias  [384]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC385,getDouble(line,  96,17))  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Resultados ejercicios anteriores  [385]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC386,getDouble(line, 113,17))  // Estado de cambios patrimonio neto (II) - Saldo,  final del ejercicio anterior - Otras aportaciones socios  [386]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC394,getDouble(line, 130,17))  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Capital - Escriturado  [394]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC395,getDouble(line, 147,17))  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Capital - No exigido [395]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC396,getDouble(line, 164,17))  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Prima de emisión [396]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC397,getDouble(line, 181,17))  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Reservas [397]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC398,getDouble(line, 198,17))  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Acciones y participaciones propias [398]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC399,getDouble(line, 215,17))  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Resultados ejercicios anteriores [399]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC400,getDouble(line, 232,17))  // Estado de cambios patrimonio neto (II) - Ajustes por cambio de criterio de ejercicios anteriores - Otras aportaciones socios [400]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC408,getDouble(line, 249,17))  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Capital - Escriturado [408]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC409,getDouble(line, 266,17))  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Capital - No exigido [409]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC410,getDouble(line, 283,17))  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Prima de emisión [410]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC411,getDouble(line, 300,17))  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Reservas [411]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC412,getDouble(line, 317,17))  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Acciones y participaciones propias [412]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC413,getDouble(line, 334,17))  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Resultados ejercicios anteriores [413]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC414,getDouble(line, 351,17))  // Estado de cambios patrimonio neto (II) - Ajustes por errores de ejercicios anteriores - Otras aportaciones socios [414]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC422,getDouble(line, 368,17))  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Capital - Escriturado [422]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC423,getDouble(line, 385,17))  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Capital - No exigido [423]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC424,getDouble(line, 402,17))  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Prima de emisión [424]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC425,getDouble(line, 419,17))  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Reservas [425]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC426,getDouble(line, 436,17))  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Acciones y participaciones propias [426]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC427,getDouble(line, 453,17))  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Resultados ejercicios anteriores [427]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC428,getDouble(line, 470,17))  // Estado de cambios patrimonio neto (II) - Saldo ajustado, inicio del ejercicio - Otras aportaciones socios [428]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC436,getDouble(line, 487,17))  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Capital - Escriturado [436]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC437,getDouble(line, 504,17))  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Capital - No exigido [437]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC438,getDouble(line, 521,17))  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Prima de emisión [438]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC439,getDouble(line, 538,17))  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Reservas [439]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC440,getDouble(line, 555,17))  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Acciones y participaciones propias [440]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC441,getDouble(line, 572,17))  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Resultados ejercicios anteriores [441]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC442,getDouble(line, 589,17))  // Estado de cambios patrimonio neto (II) - Total ingresos y gastos reconocidos - Otras aportaciones socios [442]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC450,getDouble(line, 606,17))  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Capital - Escriturado [450]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC451,getDouble(line, 623,17))  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Capital - No exigido [451]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC452,getDouble(line, 640,17))  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Prima de emisión [452]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC453,getDouble(line, 657,17))  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Reservas [453]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC454,getDouble(line, 674,17))  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Acciones y participaciones propias [454]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC455,getDouble(line, 691,17))  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Resultados ejercicios anteriores [455]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC456,getDouble(line, 708,17))  // Estado de cambios patrimonio neto (II) - Resultado cuenta pérdidas y ganancias - Otras aportaciones socios [456]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC464,getDouble(line, 725,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Capital - Escriturado [464]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC465,getDouble(line, 742,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Capital - No exigido [465]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC466,getDouble(line, 759,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Prima de emisión [466]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC467,getDouble(line, 776,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Reservas [467]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC468,getDouble(line, 793,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Acciones y participaciones propias [468]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC469,getDouble(line, 810,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Resultados ejercicios anteriores [469]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC470,getDouble(line, 827,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otras aportaciones socios [470]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC478,getDouble(line, 844,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Capital - Escriturado [478]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC479,getDouble(line, 861,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Capital - No exigido [479]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC480,getDouble(line, 878,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Prima de emisión [480]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC481,getDouble(line, 895,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Reservas [481]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC482,getDouble(line, 912,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Acciones y participaciones propias [482]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC483,getDouble(line, 929,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Resultados ejercicios anteriores [483]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC484,getDouble(line, 946,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Otras aportaciones socios [484]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC492,getDouble(line, 963,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Capital - Escriturado [492]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC493,getDouble(line, 980,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Capital - No exigido [493]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC494,getDouble(line, 997,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Prima de emisión [494]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC495,getDouble(line,1014,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Reservas [495]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC496,getDouble(line,1031,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Acciones y participaciones propias [496]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC497,getDouble(line,1048,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Resultados ejercicios anteriores [497]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC498,getDouble(line,1065,17))  // Estado de cambios patrimonio neto (II) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Otras aportaciones socios [498]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC506,getDouble(line,1082,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Capital - Escriturado [506]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC507,getDouble(line,1099,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Capital - No exigido [507]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC508,getDouble(line,1116,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Prima de emisión [508]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC509,getDouble(line,1133,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Reservas [509]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC510,getDouble(line,1150,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Acciones y participaciones propias [510]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC511,getDouble(line,1167,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Resultados ejercicios anteriores [511]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC512,getDouble(line,1184,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras aportaciones socios [512]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC520,getDouble(line,1201,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Capital - Escriturado [520]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC521,getDouble(line,1218,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Capital - No exigido [521]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC522,getDouble(line,1235,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Prima de emisión [522]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC523,getDouble(line,1252,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Reservas [523]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC524,getDouble(line,1269,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Acciones y participaciones propias [524]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC525,getDouble(line,1286,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Resultados ejercicios anteriores [525]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC526,getDouble(line,1303,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Aumentos de capital - Otras aportaciones socios [526]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC534,getDouble(line,1320,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Capital - Escriturado [534]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC535,getDouble(line,1337,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Capital - No exigido [535]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC536,getDouble(line,1354,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Prima de emisión [536]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC537,getDouble(line,1371,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Reservas [537]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC538,getDouble(line,1388,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Acciones y participaciones propias [538]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC539,getDouble(line,1405,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Resultados ejercicios anteriores [539]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC540,getDouble(line,1422,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Reducciones de capital - Otras aportaciones socios [540]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC548,getDouble(line,1439,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Capital - Escriturado [548]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC549,getDouble(line,1456,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Capital - No exigido [549]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC550,getDouble(line,1473,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Prima de emisión [550]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC551,getDouble(line,1490,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Reservas [551]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC552,getDouble(line,1507,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Acciones y participaciones propias [552]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC553,getDouble(line,1524,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Resultados ejercicios anteriores [553]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC554,getDouble(line,1541,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Otras aportaciones socios [554]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC562,getDouble(line,1558,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Capital - Escriturado [562]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC563,getDouble(line,1575,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Capital - No exigido [563]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC564,getDouble(line,1592,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Prima de emisión [564]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC565,getDouble(line,1609,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Reservas [565]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC566,getDouble(line,1626,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Acciones y participaciones propias [566]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC567,getDouble(line,1643,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Resultados ejercicios anteriores [567]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC568,getDouble(line,1660,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Otras aportaciones socios [568]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC576,getDouble(line,1677,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Capital - Escriturado [576]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC577,getDouble(line,1694,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Capital - No exigido [577]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC578,getDouble(line,1711,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Prima de emisión [578]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC579,getDouble(line,1728,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Reservas [579]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC580,getDouble(line,1745,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Acciones y participaciones propias [580]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC581,getDouble(line,1762,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Resultados ejercicios anteriores [581]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC582,getDouble(line,1779,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Otras aportaciones socios [582]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC590,getDouble(line,1796,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Capital  - Escriturado [590]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC591,getDouble(line,1813,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Capital  - No exigido [591]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC592,getDouble(line,1830,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Prima de emisión [592]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC593,getDouble(line,1847,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Reservas [593]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC594,getDouble(line,1864,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Acciones y participaciones propias [594]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC595,getDouble(line,1881,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Resultados ejercicios anteriores [595]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC596,getDouble(line,1898,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Otras aportaciones socios [596]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC604,getDouble(line,1915,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Capital -  Escriturado [604]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC605,getDouble(line,1932,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Capital - No exigido [605]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC606,getDouble(line,1949,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Prima de emisión [606]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC607,getDouble(line,1966,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Reservas [607]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC608,getDouble(line,1983,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Acciones y participaciones propias [608]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC609,getDouble(line,2000,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Resultados ejercicios anteriores [609]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC610,getDouble(line,2017,17))  // Estado de cambios patrimonio neto (II) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Otras aportaciones socios [610]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC618,getDouble(line,2034,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Capital - Escriturado [618]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC619,getDouble(line,2051,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Capital - No exigido [619]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC620,getDouble(line,2068,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Prima de emisión [620]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC621,getDouble(line,2085,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Reservas [621]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC622,getDouble(line,2102,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Acciones y participaciones propias [622]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC623,getDouble(line,2119,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Resultados ejercicios anteriores [623]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC624,getDouble(line,2136,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras aportaciones socios [624]  
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC715,getDouble(line,2153,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Capital - Escriturado [715]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC716,getDouble(line,2170,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización -  Capital - No exigido [716]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC717,getDouble(line,2187,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Prima de emisión [717]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC718,getDouble(line,2204,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización -  Reservas [718]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC719,getDouble(line,2221,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Acciones y participaciones propias [719]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC720,getDouble(line,2238,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Resultados ejercicios anteriores [720]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC721,getDouble(line,2255,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Otras aportaciones socios [721]  
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC729,getDouble(line,2272,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Capital - Escriturado [729]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC730,getDouble(line,2289,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones -  Capital - No exigido [730]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC731,getDouble(line,2306,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Prima de emisión [731]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC732,getDouble(line,2323,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones -  Reservas [732]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC733,getDouble(line,2340,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Acciones y participaciones propias [733]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC734,getDouble(line,2357,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Resultados ejercicios anteriores [734]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC735,getDouble(line,2374,17))  // Estado de cambios patrimonio neto (II) - Otras variaciones del patrimonio neto - Otras variaciones - Otras aportaciones socios [735]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC632,getDouble(line,2391,17))  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Capital - Escriturado [632]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC633,getDouble(line,2408,17))  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Capital - No exigido [633]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC634,getDouble(line,2425,17))  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Prima de emisión [634]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC635,getDouble(line,2442,17))  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Reservas [635]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC636,getDouble(line,2459,17))  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Acciones y participaciones propias [636]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC637,getDouble(line,2476,17))  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Resultados ejercicios anteriores [637]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC638,getDouble(line,2493,17))  // Estado de cambios patrimonio neto (II) - Saldo, final ejercicio - Otras aportaciones socios [638]				
		    })
			
		,PAG11 ("T200110", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC387,getDouble(line,  11,17))  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Resultado del ejercicio [387]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC388,getDouble(line,  28,17))  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Dividendo a cuenta [388]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC389,getDouble(line,  45,17))  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Otros instrumentos patrimonio neto [389]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC390,getDouble(line,  62,17))  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Ajustes por cambios de valor [390]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC391,getDouble(line,  79,17))  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Ajustes en patrimonio neto [391]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC392,getDouble(line,  96,17))  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Subvenciones, donaciones y legados recibidos [392]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC393,getDouble(line, 113,17))  // Estado de cambios patrimonio neto (III) - Saldo final del ejercicio anterior - Total [393]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC401,getDouble(line, 130,17))  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Resultado del ejercicio [401]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC402,getDouble(line, 147,17))  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Dividendo a cuenta [402]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC403,getDouble(line, 164,17))  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Otros instrumentos patrimonio neto [403]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC404,getDouble(line, 181,17))  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Ajustes por cambios de valor [404]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC405,getDouble(line, 198,17))  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Ajustes en patrimonio neto [405]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC406,getDouble(line, 215,17))  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Subvenciones, donaciones y legados recibidos [406]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC407,getDouble(line, 232,17))  // Estado de cambios patrimonio neto (III) - Ajustes por cambio de criterio de ejercicios anteriores - Total [407]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC415,getDouble(line, 249,17))  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Resultado del ejercicio [415]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC416,getDouble(line, 266,17))  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Dividendo a cuenta [416]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC417,getDouble(line, 283,17))  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Otros instrumentos patrimonio neto [417]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC418,getDouble(line, 300,17))  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Ajustes por cambios de valor [418]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC419,getDouble(line, 317,17))  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Ajustes en patrimonio neto [419]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC420,getDouble(line, 334,17))  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Subvenciones, donaciones y legados recibidos [420]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC421,getDouble(line, 351,17))  // Estado de cambios patrimonio neto (III) - Ajustes por errores de ejercicios anteriores - Total [421]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC429,getDouble(line, 368,17))  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Resultado del ejercicio [429]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC430,getDouble(line, 385,17))  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Dividendo a cuenta [430]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC431,getDouble(line, 402,17))  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Otros instrumentos patrimonio neto [431]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC432,getDouble(line, 419,17))  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Ajustes por cambios de valor [432]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC433,getDouble(line, 436,17))  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Ajustes en patrimonio neto [433]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC434,getDouble(line, 453,17))  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Subvenciones, donaciones y legados recibidos [434]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC435,getDouble(line, 470,17))  // Estado de cambios patrimonio neto (III) - Saldo ajustado, inicio del ejercicio - Total [435]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC443,getDouble(line, 487,17))  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Resultado del ejercicio [443]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC444,getDouble(line, 504,17))  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Dividendo a cuenta [444]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC445,getDouble(line, 521,17))  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Otros instrumentos patrimonio neto [445]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC446,getDouble(line, 538,17))  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Ajustes por cambios de valor [446]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC448,getDouble(line, 555,17))  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Subvenciones, donaciones y legados recibidos [448]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC449,getDouble(line, 572,17))  // Estado de cambios patrimonio neto (III) - Total ingresos y gastos reconocidos - Total [449]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC457,getDouble(line, 589,17))  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Resultado del ejercicio [457]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC458,getDouble(line, 606,17))  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Dividendo a cuenta [458]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC461,getDouble(line, 623,17))  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Ajustes en patrimonio neto [461]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC462,getDouble(line, 640,17))  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Subvenciones, donaciones y legados recibidos [462]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC463,getDouble(line, 657,17))  // Estado de cambios patrimonio neto (III) - Resultado cuenta pérdidas y ganancias - Total [463]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC471,getDouble(line, 674,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Resultado del ejercicio [471]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC472,getDouble(line, 691,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Dividendo a cuenta [472]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC475,getDouble(line, 708,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ajustes en patrimonio neto [475]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC476,getDouble(line, 725,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Subvenciones, donaciones y legados recibidos [476]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC477,getDouble(line, 742,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Total [477]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC485,getDouble(line, 759,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Resultado del ejercicio [485]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC486,getDouble(line, 776,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Dividendo a cuenta [486]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC489,getDouble(line, 793,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Ajustes en patrimonio neto [489]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC490,getDouble(line, 810,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Subvenciones, donaciones y legados recibidos [490]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC491,getDouble(line, 827,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Ingresos fiscales a distribuir en varios ejercicios - Total [491]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC499,getDouble(line, 844,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Resultado del ejercicio [499]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC502,getDouble(line, 861,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Dividendo a cuenta [502]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC503,getDouble(line, 878,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Ajustes en patrimonio neto [503]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC504,getDouble(line, 895,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Subvenciones, donaciones y legados recibidos [504]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC505,getDouble(line, 912,17))  // Estado de cambios patrimonio neto (III) - Ingresos y gastos reconocidos en patrimonio neto - Otros ingresos y gastos reconocidos en patrimonio neto - Total [505]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC513,getDouble(line, 929,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Resultado del ejercicio [513]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC514,getDouble(line, 946,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Dividendo a cuenta [514]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC515,getDouble(line, 963,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otros instrumentos patrimonio neto [515]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC516,getDouble(line, 980,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Ajustes por cambios de valor [516]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC517,getDouble(line, 997,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Ajustes en patrimonio neto [517]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC518,getDouble(line,1014,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Subvenciones, donaciones y legados recibidos [518]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC519,getDouble(line,1031,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Total [519]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC527,getDouble(line,1048,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Resultado del ejercicio [527]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC528,getDouble(line,1065,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Dividendo a cuenta [528]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC529,getDouble(line,1082,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Otros instrumentos patrimonio neto [529]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC530,getDouble(line,1099,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Ajustes por cambios de valor [530]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC531,getDouble(line,1116,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Ajustes en patrimonio neto [531]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC532,getDouble(line,1133,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Subvenciones, donaciones y legados recibidos [532]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC533,getDouble(line,1150,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Aumentos de capital - Total [533]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC541,getDouble(line,1167,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Resultado del ejercicio [541]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC542,getDouble(line,1184,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Dividendo a cuenta [542]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC543,getDouble(line,1201,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Otros instrumentos patrimonio neto [543]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC544,getDouble(line,1218,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Ajustes por cambios de valor [544]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC545,getDouble(line,1235,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Ajustes en patrimonio neto [545]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC546,getDouble(line,1252,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Subvenciones, donaciones y legados recibidos [546]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC547,getDouble(line,1269,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Reducciones de capital - Total [547]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC555,getDouble(line,1286,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Resultado del ejercicio [555]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC556,getDouble(line,1303,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Dividendo a cuenta [556]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC557,getDouble(line,1320,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Otros instrumentos patrimonio neto [557]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC558,getDouble(line,1337,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Ajustes por cambios de valor [558]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC560,getDouble(line,1354,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Subvenciones, donaciones y legados recibidos [560]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC561,getDouble(line,1371,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Conversión de pasivos en patrim. neto - Total [561]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC569,getDouble(line,1388,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Resultado del ejercicio [569]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC570,getDouble(line,1405,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Dividendo a cuenta [570]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC571,getDouble(line,1422,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Otros instrumentos patrimonio neto [571]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC572,getDouble(line,1439,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Ajustes por cambio de valor [572]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC574,getDouble(line,1456,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Subvenciones, donaciones y legados recibidos [574]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC575,getDouble(line,1473,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - (-) Distribución de dividendos - Total [575]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC583,getDouble(line,1490,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Resultado del ejercicio [583]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC584,getDouble(line,1507,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Dividendo a cuenta [584]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC585,getDouble(line,1524,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Otros instrumentos patrimonio neto [585]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC586,getDouble(line,1541,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Ajustes por cambio de valor [586]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC588,getDouble(line,1558,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Subvenciones, donaciones y legados recibidos [588]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC589,getDouble(line,1575,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Operaciones con acciones o participaciones propias - Total [589]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC597,getDouble(line,1592,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Resultado del ejercicio [597]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC598,getDouble(line,1609,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Dividendo a cuenta [598]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC599,getDouble(line,1626,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Otros instrumentos patrimonio neto [599]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC600,getDouble(line,1643,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Ajustes por cambios de valor [600]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC602,getDouble(line,1660,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Subvenciones, donaciones y legados recibidos [602]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC603,getDouble(line,1677,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Incremento (reducción) de patr. neto de combinación de negocios - Total [603]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC611,getDouble(line,1694,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Resultado del ejercicio  [611]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC612,getDouble(line,1711,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Dividendo a cuenta  [612]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC613,getDouble(line,1728,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Otros instrumentos patrimonio neto [ [613]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC614,getDouble(line,1745,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Ajustes por cambios de valor [614]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC615,getDouble(line,1762,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Ajustes en patrimonio neto [615]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC616,getDouble(line,1779,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Subvenciones, donaciones y legados recibidos  [616]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC617,getDouble(line,1796,17))  // Estado de cambios patrimonio neto (III) - Operaciones con socios o propietarios - Otras operaciones con socios o propietarios - Total [617]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC625,getDouble(line,1813,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Resultado del ejercicio [625]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC626,getDouble(line,1830,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Dividendo a cuenta  [626]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC627,getDouble(line,1847,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otros instrumentos patrimonio neto [ [627]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC628,getDouble(line,1864,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Ajustes por cambios de valor [628]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC629,getDouble(line,1881,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Ajustes en patrimonio neto [629]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC630,getDouble(line,1898,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Subvenciones, donaciones y legados recibidos  [630]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC631,getDouble(line,1915,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Total [631]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC722,getDouble(line,1932,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Resultado del ejercicio [722]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC723,getDouble(line,1949,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Dividendo a cuenta  [723]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC724,getDouble(line,1966,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Otros instrumentos patrimonio neto [ [724]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC725,getDouble(line,1983,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Ajustes por cambios de valor [725]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC726,getDouble(line,2000,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Ajustes en patrimonio neto [726]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC727,getDouble(line,2017,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Subvenciones, donaciones y legados recibidos  [727]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC728,getDouble(line,2034,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Movimiento reserva revalorización - Total [728]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC736,getDouble(line,2051,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Resultado del ejercicio [736]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC737,getDouble(line,2068,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Dividendo a cuenta  [737]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC738,getDouble(line,2085,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Otros instrumentos patrimonio neto [ [738]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC739,getDouble(line,2102,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Ajustes por cambios de valor [739]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC740,getDouble(line,2119,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Ajustes en patrimonio neto [740]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC741,getDouble(line,2136,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Subvenciones, donaciones y legados recibidos  [741]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC742,getDouble(line,2153,17))  // Estado de cambios patrimonio neto (III) - Otras variaciones del patrimonio neto - Otras variaciones - Total [742]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC639,getDouble(line,2170,17))  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Resultado del ejercicio [639]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC640,getDouble(line,2187,17))  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Dividendo a cuenta [640]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC641,getDouble(line,2204,17))  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Otros instrumentos patrimonio neto [641]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC642,getDouble(line,2221,17))  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Ajustes por cambios de valor [642]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC643,getDouble(line,2238,17))  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Ajustes en patrimonio neto [643]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC644,getDouble(line,2255,17))  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Subvenciones, donaciones y legados recibidos [644]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TC645,getDouble(line,2272,17))  // Estado de cambios patrimonio neto (III) - Saldo, final ejercicio  - Total [645]
			})

		,PAG12 ("T200120", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ500,getDouble(line,  11,17))  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Resultado de la cuenta de pérdidas y ganancias [500]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ301,getDouble(line,  28,17))  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Correcciones por Impuesto Sociedades - Aumentos [301]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ302,getDouble(line,  45,17))  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Correcciones por Impuesto Sociedades - Disminuciones [302]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ501,getDouble(line,  62,17))  // Liquidación I - Resultado de la cuenta de pérdidas y ganancias - Resultado cuenta pérdidas y ganancias antes de Impuesto Sociedades [501]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0303,getDouble(line,  79,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Diferencias entre amortización contable y fiscal - Aumentos [303]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0304,getDouble(line,  96,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Diferencias entre amortización contable y fiscal - Disminuciones [304]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0504,getDouble(line, 113,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - 30% importe gastos amortiz. contable - Aumentos [504]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0505,getDouble(line, 130,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - 30% importe gastos amortiz. contable - Disminuciones [505]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0305,getDouble(line, 147,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Amortización inmovilizado afecto investigación y desarrollo - Aumentos [305]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0306,getDouble(line, 164,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Amortización inmovilizado afecto investigación y desarrollo - Disminuciones [306]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0307,getDouble(line, 181,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización de gastos de investigación y desarrollo - Aumentos [307]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0308,getDouble(line, 198,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización de gastos de investigación y desarrollo - Disminuciones [308]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0514,getDouble(line, 215,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización con mantenimiento de empleo - Aumentos [514]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0509,getDouble(line, 232,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización con mantenimiento de empleo - Disminuciones [509]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0516,getDouble(line, 249,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización sin mantenimiento de empleo - Aumentos [516]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0551,getDouble(line, 266,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Libertad de amortización sin mantenimiento de empleo - Disminuciones [551]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0309,getDouble(line, 283,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otros supuestos de libertad de amortización - Aumentos [309]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0310,getDouble(line, 300,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otros supuestos de libertad de amortización - Disminuciones [310]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0311,getDouble(line, 317,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:libertad amortización - Aumentos [311]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0312,getDouble(line, 334,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:libertad amortización - Disminuciones [312]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0313,getDouble(line, 351,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:amortización acelerada - Aumentos [313]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0314,getDouble(line, 368,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión:amortización acelerada - Disminuciones [314]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0315,getDouble(line, 385,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Cesión de bienes con opción de compra - Aumentos [315]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0316,getDouble(line, 402,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Cesión de bienes con opción de compra - Disminuciones [316]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0317,getDouble(line, 419,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Arrendamiento financiero: régimen especial - Aumentos [317]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0318,getDouble(line, 436,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Arrendamiento financiero: régimen especial - Disminuciones [318]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0319,getDouble(line, 453,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro no justificadas valor de fondos editoriales, fonográficos y audiovisuales - Aumentos [319]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0320,getDouble(line, 470,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro no justificadas valor de fondos editoriales, fonográficos y audiovisuales - Disminuciones [320]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0321,getDouble(line, 487,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro de valor de créditos derivadas de insolvencia deudores - Aumentos [321]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0322,getDouble(line, 504,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro de valor de créditos derivadas de insolvencia deudores - Disminuciones [322]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0323,getDouble(line, 521,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión: pérdidas por deterioro créditos insolvencias - Aumentos [323]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0324,getDouble(line, 538,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Empresas reducida dimensión: pérdidas por deterioro creditos insolvencias - Disminuciones [324]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0325,getDouble(line, 555,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Ajustes por deterioro valores representativos partic.capital o fondos propios - Aumentos [325]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0326,getDouble(line, 572,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Ajustes por deterioro valores representativos partic.capital o fondos propios - Disminuciones [326]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0327,getDouble(line, 589,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro de valores representativos de deuda - Aumentos [327]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0328,getDouble(line, 606,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro de valores representativos de deuda - Disminuciones [328]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0329,getDouble(line, 623,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Adquisición de participaciones en entidades no residentes - Aumentos [329]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0330,getDouble(line, 640,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Adquisición de participaciones en entidades no residentes - Disminuciones [330]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0331,getDouble(line, 657,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del fondo de comercio - Aumentos [331]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0332,getDouble(line, 674,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del fondo de comercio - Disminuciones [332]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0333,getDouble(line, 691,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del intangible de vida útil indefinida - Aumentos [333]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0334,getDouble(line, 708,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Deducción del intangible de vida útil indefinida - Disminuciones [334]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0335,getDouble(line, 725,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Provisiones y gastos por pensiones - Aumentos [335]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0336,getDouble(line, 742,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Provisiones y gastos por pensiones - Disminuciones [336]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0337,getDouble(line, 759,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otras provisiones no deducibles fiscalmente - Aumentos [337]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0338,getDouble(line, 776,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Otras provisiones no deducibles fiscalmente - Disminuciones [338]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0339,getDouble(line, 793,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos por donativos y liberalidades - Aumentos [339]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0341,getDouble(line, 810,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones realizadas con paraísos fiscales - Aumentos [341]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0342,getDouble(line, 827,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones realizadas con paraísos fiscales - Disminuciones [342]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0508,getDouble(line, 844,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos financieros derivados de deudas con entidades del grupo - Aumentos [508]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0510,getDouble(line, 861,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro valores representativos partic. capital o fondos propios - Aumentos [510] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0511,getDouble(line, 878,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Pérdidas por deterioro valores representativos partic. capital o fondos propios - Disminuciones [511] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0512,getDouble(line, 895,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Rentas negativas obtenidas en el extranjero a través de E.P - Aumentos [512] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0513,getDouble(line, 912,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Rentas negativas obtenidas en el extranjero a través de E.P - Disminuciones  [513] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0343,getDouble(line, 929,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Otros gastos no deducibles - Aumentos [343]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0184,getDouble(line, 946,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Rentas negativas obtenidas por miembros de una UTE en el extranjero - Aumentos [184]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0345,getDouble(line, 963,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Revalorizaciones contables - Aumentos [345]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0346,getDouble(line, 980,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Revalorizaciones contables - Disminuciones [346]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0347,getDouble(line, 997,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Aplicación del valor normal de mercado - Aumentos [347]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0348,getDouble(line,1014,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Aplicación del valor normal de mercado - Disminuciones [348]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0349,getDouble(line,1031,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ingresos por donaciones y legados otorgados por terceros - Aumentos [349]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0350,getDouble(line,1048,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ingresos por donaciones y legados otorgados por terceros - Disminuciones [350]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0352,getDouble(line,1065,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Correción de rentas por depreciación monetaria - Disminuciones [352]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0354,getDouble(line,1082,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos por operaciones con acciones propias - Disminuciones [354]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0355,getDouble(line,1099,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Errores contables - Aumentos [355]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0356,getDouble(line,1116,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Errores contables - Disminuciones [356]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0357,getDouble(line,1133,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones a plazos - Aumentos [357]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0358,getDouble(line,1150,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Operaciones a plazos - Disminuciones [358]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0359,getDouble(line,1167,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reversión del deterioro del valor de elementos patrimoniales - Aumentos [359]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0360,getDouble(line,1184,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reversión del deterioro del valor de elementos patrimoniales - Disminuciones [360]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0225,getDouble(line,1201,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Rentas negativas - Aumentos [225]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0226,getDouble(line,1218,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Rentas negativas - Disminuciones [226]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0415,getDouble(line,1235,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ajustes - Aumentos [415]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0416,getDouble(line,1252,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ajustes - Disminuciones [416]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0361,getDouble(line,1269,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Otras diferencias de imputación temporal de ingresos y gastos - Aumentos [361]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0362,getDouble(line,1286,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Otras diferencias de imputación temporal de ingresos y gastos - Disminuciones [362]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0363,getDouble(line,1303,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ajustes por limitación en deducibilidad en gastos financieros - Aumentos [363]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0364,getDouble(line,1320,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Ajustes por limitación en deducibilidad en gastos financieros - Disminuciones [364]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0365,getDouble(line,1337,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reinversión de beneficios extraordinarios - Aumentos  [365]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0367,getDouble(line,1354,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Gastos no deducibles por incompatibilidad con la deducción por reinversión - Aumentos [367]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0369,getDouble(line,1371,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Exención por doble imposición internacional (art.21 LIS) - Aumentos [369]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0370,getDouble(line,1388,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Exención por doble imposición internacional (art.21 LIS) - Disminuciones [370]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0256,getDouble(line,1405,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -   Exención por doble imposición internacional (art.22 LIS y DT 41) - Aumentos [256]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0278,getDouble(line,1422,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -   Exención por doble imposición internacional (art.22 LIS y DT 41) - Disminuciones [278]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0372,getDouble(line,1439,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias -  Reducción de ingresos de activos intangibles - Disminuciones [372]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0373,getDouble(line,1456,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Obra benéfico-social cajas de ahorro y fundaciones bancarias - Aumentos [373]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0374,getDouble(line,1473,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Obra benéfico-social cajas de ahorro y fundaciones bancarias - Disminuciones [374]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0375,getDouble(line,1490,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Agrupaciones interés económico y UTE's - Aumentos [375] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0376,getDouble(line,1507,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Agrupaciones interés económico y UTE's - Disminuciones [376] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0377,getDouble(line,1524,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Soc. y fondos de capital-riesgo y soc. desarrollo industrial regional - Aumentos [377]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0378,getDouble(line,1541,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Soc. y fondos de capital-riesgo y soc. desarrollo industrial regional - Disminuciones [378]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0379,getDouble(line,1558,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Valoración bienes y derechos. Régimen especial operaciones reestructuración - Aumentos [379]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0380,getDouble(line,1575,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Valoración bienes y derechos. Régimen especial operaciones reestructuración - Disminuciones [380]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0381,getDouble(line,1592,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Minería e hidrocarburos : factor agotamiento - Aumentos [381]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0382,getDouble(line,1609,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Minería e hidrocarburos : factor agotamiento - Disminuciones [382]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0383,getDouble(line,1626,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Hidrocarburos: Amortización inversiones intangibles  y gastos de investigación - Aumentos [383]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0384,getDouble(line,1643,17))  // Liquidación I - Detalle correcciones resultado cta. pérdidas y ganancias - Hidrocarburos: Amortización inversiones intangibles y gastos de investigación - Disminuciones [384]
			})
			
		,PAG13 ("T200130", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0385,getDouble(line, 11,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades de tenencia valores extranjeros - Aumentos [385]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0386,getDouble(line, 28,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades de tenencia valores extranjeros - Disminuciones [386]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0387,getDouble(line, 45,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Transparencia fiscal internacional - Aumentos [387]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0388,getDouble(line, 62,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Transparencia fiscal internacional - Disminuciones [388]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0389,getDouble(line, 79,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen de entidades parcialmente exentas - Aumentos [389]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0390,getDouble(line, 96,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen de entidades parcialmente exentas - Disminuciones [390]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0250,getDouble(line,113,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Aportaciones a favor entidades sin fines lucrativos - Aumentos [250]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0251,getDouble(line,130,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Aportaciones a favor entidades sin fines lucrativos - Disminuciones [251]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0391,getDouble(line,147,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades sin fines lucrativos - Aumentos [391]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0392,getDouble(line,164,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen fiscal entidades sin fines lucrativos - Disminuciones [392]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0396,getDouble(line,181,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Montes vecinales en mano común - Disminuciones [396]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0397,getDouble(line,198,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen entidades navieras - Aumentos [397] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0398,getDouble(line,215,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Régimen entidades navieras - Disminuciones [398] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0400,getDouble(line,232,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Cooperativas: Fondo de reserva obligatorio - Disminuciones [400]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0403,getDouble(line,249,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Reservas inversiones en Canarias - Aumentos [403]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0404,getDouble(line,266,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Reservas inversiones en Canarias - Disminuciones [404]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0405,getDouble(line,283,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Diferimiento plusvalías - Aumentos [405] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0406,getDouble(line,300,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Diferimiento plusvalías - Disminuciones [406] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0409,getDouble(line,317,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Entidades rég. atribución rentas constituidas extranjero, presencia territorio español - Aumentos [409]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0410,getDouble(line,334,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias -  Entidades rég. atribución rentas constituidas extranjero, presencia territorio español - Disminuciones [410]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0411,getDouble(line,351,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias -  Correcciones específicas entidades sometidas normativa foral - Aumentos [411]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0412,getDouble(line,368,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias -  Correcciones específicas entidades sometidas normativa foral - Disminuciones [412]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0518,getDouble(line,385,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Exención transmisión bienes inmuebles - Aumentos [518]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0519,getDouble(line,402,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Exención transmisión bienes inmuebles - Disminuciones [519]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0340,getDouble(line,419,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Impuesto extranjero beneficios con cargo a los cuales se pagan dividendos objeto deducción por doble imposición internacional - Aumentos [340]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0351,getDouble(line,436,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Impuesto extranjero soportado por sujeto pasivo, no deducible por afectar rentas con deducción doble imposición - Aumentos [351]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0368,getDouble(line,453,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Subvenciones públicas en el resultado del ejercicio, no integrables en la base imponible - Disminuciones [368]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0371,getDouble(line,470,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - SICAV: reducciones de capital y distribución prima de emisión - Aumentos [371]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0413,getDouble(line,487,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Otras correcciones al resultado cta. pérdidas y ganancias  - Aumentos [413]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0414,getDouble(line,504,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Otras correcciones al resultado cta. pérdidas y ganancias  - Disminuciones [414]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.I0417,getDouble(line,521,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Total correcciones al resultado cta. pérdidas y ganancias - Aumentos [417]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.D0418,getDouble(line,538,17))  // Liquidación II - Detalle correcciones resultado cta. pérdidas y ganancias - Total correcciones al resultado cta. pérdidas y ganancias - Disminuciones [418]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ578,getDouble(line,555,17))  // Liquidación II -  Entidades navieras en función del tonelaje - B.I. actividades o rentas en régimen general [578]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ579,getDouble(line,572,17))  // Liquidación II -  Entidades navieras en función del tonelaje - B.I. derivada del régimen especial [579]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ550,getDouble(line,589,17))  // Liquidación II -  Base imponible - B.I. antes de la compensación de bases imponibles negativas [550]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ547,getDouble(line,606,17))  // Liquidación II -  Base imponible - Compensación de bases imponibles negativas períodos anteriores [547]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ552,getDouble(line,623,17))  // Liquidación II -  Base imponible - Base imponible  [552]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ553,getDouble(line,640,17))  // Liquidación II -  Base imponible - Sólo cooperativas - Resultados cooperativos [553]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ554,getDouble(line,657,17))  // Liquidación II -  Base imponible - Sólo cooperativas - Resultados extracooperativos [554]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ555,getDouble(line,674,17))  // Liquidación II -  Base imponible - Sólo agrupaciones interés económico y UTE's - Socios residentes [555]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ556,getDouble(line,691,17))  // Liquidación II -  Base imponible - Sólo agrupaciones interés económico y UTE's - Socios no residentes [556]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ559,getDouble(line,708,17))  // Liquidación II -  Base imponible - Sólo entidades ZEC - B.I. a tipo de gravamen especial [559]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ520,getDouble(line,725,17))  // Liquidación II -  Base imponible - Sólo SOCIMIS - Parte B.I. del periodo impositivo que tributa al tipo general [520]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ521,getDouble(line,742,17))  // Liquidación II -  Base imponible - Sólo SOCIMIS - Parte B.I. del periodo impositivo que tributa al tipo del 0% [521]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ558,getDouble(line,759, 4))  // Liquidación II -  Tipo de gravamen - Tipo de gravamen [558]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ560,getDouble(line,763,17))  // Liquidación II -  Sólo sociedades cooperativas - Cuota íntegra previa [560]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ561,getDouble(line,780,17))  // Liquidación II -  Sólo sociedades cooperativas - Compensación de cuotas por pérdidas de cooperativas [561]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ562,getDouble(line,797,17))  // Liquidación II -  Cuota íntegra -  Cuota íntegra [562]
			})
		    
		,PAG14 ("T200140", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN567,getDouble(line, 11,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificación por rentas obtenidas en Ceuta y Melilla [567]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN568,getDouble(line, 28,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificación actividades exportadoras y de prestación de servicios [568]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN563,getDouble(line, 45,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificación rendimientos por venta de bienes corporales producidos en Canarias  [563]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN566,getDouble(line, 62,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificaciones sociedades cooperativas  [566]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN576,getDouble(line, 79,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificaciones entidades dedicadas al arrendamiento de viviendas [576]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN569,getDouble(line, 96,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Otras bonificaciones  [569]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN570,getDouble(line,113,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición - D.I. interna de periodos anteriores aplicada en el ejercicio [570]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN571,getDouble(line,130,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. interna generada y aplicada en el ejercicio actual [571]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN572,getDouble(line,147,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. internacional periodos anteriores aplicada en el ejercicio [572]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN573,getDouble(line,164,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. internacional generada y aplicada ejercicio actual [573]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN575,getDouble(line,181,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - Transparencia fiscal internacional [575]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN577,getDouble(line,198,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Deducciones por doble imposición  - D.I. interna intersocietaria al 5/10 % (cooperativas) [577]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN581,getDouble(line,215,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Bonificaciones empresas navieras en Canarias  [581]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN582,getDouble(line,232,17))  // Liquidación III - Bonificaciones/Deducciones doble imposición - Cuota íntegra ajustada positiva [582]    
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN583,getDouble(line,249,17))  // Liquidación III - Otras deducciones - Apoyo fiscal a la inversión y otras deducciones  [583] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN585,getDouble(line,266,17))  // Liquidación III - Otras deducciones - Deducción art.42 L.I.S. y art. 36 ter Ley 43/95  [585]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN584,getDouble(line,283,17))  // Liquidación III - Otras deducciones - Deducciones disposición transitoria octava L.I.S. [584]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN588,getDouble(line,300,17))  // Liquidación III - Otras deducciones - Deducciones con límite del Capítulo IV Título VI L.I.S. [588]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN082,getDouble(line,317,17))  // Liquidación III - Otras deducciones - Deducciones sin límite I+D+i [082]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN565,getDouble(line,334,17))  // Liquidación III - Otras deducciones - Deducción donaciones a entidades sin fines de lucro [565]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN590,getDouble(line,351,17))  // Liquidación III - Otras deducciones - Deducciones inversión Canarias (Ley 20/1991) [590]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN399,getDouble(line,368,17))  // Liquidación III - Otras deducciones - Deducciones especifícas de las entidades sometidas a normativa foral [399]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN592,getDouble(line,385,17))  // Liquidación III - Otras deducciones - Cuota líquida positiva [592]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN595,getDouble(line,402,17))  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Retenciones e ingresos a cuenta/pagos a cuenta participaciones I.I.C. [595]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN596,getDouble(line,419,17))  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Ret. e ingr. a cuenta/pagos a cuenta participaciones I.I.C. imputadas por agrup. de interés economico y UTES [596]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN597,getDouble(line,436,17))  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Retenciones sobre premios loterías y apuestas [597]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN408,getDouble(line,453,17))  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Abono deducciones I+D+i por insuficiencia de cuota [408]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN150,getDouble(line,470,17))  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Abono por conversión de activos por impuesto diferido en crédito exigible [150]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN599,getDouble(line,487,17))  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Cuota del ejercicio a ingresar o a devolver - Estado [599]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN600,getDouble(line,504,17))  // Liquidación III - Cuota del ejercicio a ingresar o a devolver - Cuota del ejercicio a ingresar o a devolver - D. Forales/Navarra (Totales) [600]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN601,getDouble(line,521,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 1 - Estado [601]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN602,getDouble(line,538,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 1 - D. Forales/Navarra (Totales) [602]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN603,getDouble(line,555,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 2 - Estado [603]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN604,getDouble(line,572,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 2 - D. Forales/Navarra (Totales) [604]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN605,getDouble(line,589,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 3 - Estado [605]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN606,getDouble(line,606,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Pagos fraccionados - 3 - D. Forales/Navarra (Totales) [606]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN611,getDouble(line,623,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Cuota diferencial  - Estado [611]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN612,getDouble(line,640,17))  // Liquidación III - Pagos fraccionados/Cuota diferencial - Cuota diferencial  - D. Forales/Navarra (Totales) [612]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN615,getDouble(line,657,17))  // Liquidación III - Líquido a ingresar o a devolver - Incremento por pérdida beneficios fiscales períodos anteriores  - Estado [615]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN616,getDouble(line,674,17))  // Liquidación III - Líquido a ingresar o a devolver - Incremento por pérdida beneficios fiscales períodos anteriores  - D. Forales/Navarra (Totales) [616]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN633,getDouble(line,691,17))  // Liquidación III - Líquido a ingresar o a devolver - Incremento por incumplimiento de requisitos SOCIMI  -  Estado [633]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN642,getDouble(line,708,17))  // Liquidación III - Líquido a ingresar o a devolver - Incremento por incumplimiento de requisitos SOCIMI  -  D. Forales/Navarra (Totales) [642]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN617,getDouble(line,725,17))  // Liquidación III - Líquido a ingresar o a devolver - Intereses de demora  - Estado [617]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN618,getDouble(line,742,17))  // Liquidación III - Líquido a ingresar o a devolver - Intereses de demora  - D. Forales/Navarra (Totales) [618]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN619,getDouble(line,759,17))  // Liquidación III - Líquido a ingresar o a devolver - Importe ingreso/devolución efectuada de la declaración originaria  - Estado [619]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN620,getDouble(line,776,17))  // Liquidación III - Líquido a ingresar o a devolver - Importe ingreso/devolución efectuada de la declaración originaria  - D. Forales/Navarra (Totales) [620]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN621,getDouble(line,793,17))  // Liquidación III - Líquido a ingresar o a devolver  - Estado [621]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN622,getDouble(line,810,17))  // Liquidación III - Líquido a ingresar o a devolver  - D. Forales/Navarra (Totales) [622]
			})

		,PAG15 ("T200150", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ640,getDouble(line,  11,17))  // Detalle compensación bases imponibles negativas - 1997 - Pendiente aplicación a principio periodo [640]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ641,getDouble(line,  28,17))  // Detalle compensación bases imponibles negativas - 1997 - Aplicado en esta liquidación [641]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ548,getDouble(line,  45,17))  // Detalle compensación bases imponibles negativas - 1997 - Pendiente aplicación en periodos futuros [548]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ643,getDouble(line,  62,17))  // Detalle compensación bases imponibles negativas - 1998 - Pendiente aplicación a principio periodo [643]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ644,getDouble(line,  79,17))  // Detalle compensación bases imponibles negativas - 1998 - Aplicado en esta liquidación [644]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ645,getDouble(line,  96,17))  // Detalle compensación bases imponibles negativas - 1998 - Pendiente aplicación en periodos futuros [645]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ646,getDouble(line, 113,17))  // Detalle compensación bases imponibles negativas - 1999 - Pendiente aplicación a principio periodo [646]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ647,getDouble(line, 130,17))  // Detalle compensación bases imponibles negativas - 1999 - Aplicado en esta liquidación [647]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ648,getDouble(line, 147,17))  // Detalle compensación bases imponibles negativas - 1999 - Pendiente aplicación en periodos futuros [648]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ649,getDouble(line, 164,17))  // Detalle compensación bases imponibles negativas - 2000 - Pendiente aplicación a principio periodo [649]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ650,getDouble(line, 181,17))  // Detalle compensación bases imponibles negativas - 2000 - Aplicado en esta liquidación [650]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ651,getDouble(line, 198,17))  // Detalle compensación bases imponibles negativas - 2000 - Pendiente aplicación en periodos futuros [651]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ652,getDouble(line, 215,17))  // Detalle compensación bases imponibles negativas - 2001 - Pendiente aplicación a principio periodo [652]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ653,getDouble(line, 232,17))  // Detalle compensación bases imponibles negativas - 2001 - Aplicado en esta liquidación [653]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ654,getDouble(line, 249,17))  // Detalle compensación bases imponibles negativas - 2001 - Pendiente aplicación en periodos futuros [654]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ655,getDouble(line, 266,17))  // Detalle compensación bases imponibles negativas - 2002 - Pendiente aplicación a principio periodo [655]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ656,getDouble(line, 283,17))  // Detalle compensación bases imponibles negativas - 2002 - Aplicado en esta liquidación [656]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ657,getDouble(line, 300,17))  // Detalle compensación bases imponibles negativas - 2002 - Pendiente aplicación en periodos futuros [657]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ658,getDouble(line, 317,17))  // Detalle compensación bases imponibles negativas - 2003 - Pendiente aplicación a principio periodo [658]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ659,getDouble(line, 334,17))  // Detalle compensación bases imponibles negativas - 2003 - Aplicado en esta liquidación [659]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ660,getDouble(line, 351,17))  // Detalle compensación bases imponibles negativas - 2003 - Pendiente aplicación en periodos futuros [660]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ661,getDouble(line, 368,17))  // Detalle compensación bases imponibles negativas - 2004 - Pendiente aplicación a principio periodo [661]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ662,getDouble(line, 385,17))  // Detalle compensación bases imponibles negativas - 2004 - Aplicado en esta liquidación [662]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ663,getDouble(line, 402,17))  // Detalle compensación bases imponibles negativas - 2004 - Pendiente aplicación en periodos futuros [663]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ664,getDouble(line, 419,17))  // Detalle compensación bases imponibles negativas - 2005 - Pendiente aplicación a principio periodo [664]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ665,getDouble(line, 436,17))  // Detalle compensación bases imponibles negativas - 2005 - Aplicado en esta liquidación [665]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ666,getDouble(line, 453,17))  // Detalle compensación bases imponibles negativas - 2005 - Pendiente aplicación en periodos futuros [666]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ667,getDouble(line, 470,17))  // Detalle compensación bases imponibles negativas - 2006 - Pendiente aplicación a principio periodo [667]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ668,getDouble(line, 487,17))  // Detalle compensación bases imponibles negativas - 2006 - Aplicado en esta liquidación [668]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ669,getDouble(line, 504,17))  // Detalle compensación bases imponibles negativas - 2006 - Pendiente aplicación en periodos futuros [669]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ743,getDouble(line, 521,17))  // Detalle compensación bases imponibles negativas - 2007 - Pendiente aplicación a principio periodo [743]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ747,getDouble(line, 538,17))  // Detalle compensación bases imponibles negativas - 2007 - Aplicado en esta liquidación [747]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ748,getDouble(line, 555,17))  // Detalle compensación bases imponibles negativas - 2007 - Pendiente aplicación en periodos futuros [748]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ275,getDouble(line, 572,17))  // Detalle compensación bases imponibles negativas - 2008 - Pendiente aplicación a principio periodo [275]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ276,getDouble(line, 589,17))  // Detalle compensación bases imponibles negativas - 2008 - Aplicado en esta liquidación [276]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ277,getDouble(line, 606,17))  // Detalle compensación bases imponibles negativas - 2008 - Pendiente aplicación en periodos futuros [277]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ608,getDouble(line, 623,17))  // Detalle compensación bases imponibles negativas - 2009 - Pendiente de aplicación a principio del periodo [608]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ609,getDouble(line, 640,17))  // Detalle compensación bases imponibles negativas - 2009 - Aplicado en esta liquidación [609]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ610,getDouble(line, 657,17))  // Detalle compensación bases imponibles negativas - 2009 - Pendiente aplicación en periodos futuros [610]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ704,getDouble(line, 674,17))  // Detalle compensación bases imponibles negativas - 2010 - Pendiente aplicación a principio periodo [704]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ705,getDouble(line, 691,17))  // Detalle compensación bases imponibles negativas - 2010 - Aplicado en esta liquidación [705]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ706,getDouble(line, 708,17))  // Detalle compensación bases imponibles negativas - 2010 - Pendiente aplicación en periodos futuros [706]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ013,getDouble(line, 725,17))  // Detalle compensación bases imponibles negativas - 2011 - Pendiente aplicación a principio periodo [013]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ014,getDouble(line, 742,17))  // Detalle compensación bases imponibles negativas - 2011 - Aplicado en esta liquidación [014]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ015,getDouble(line, 759,17))  // Detalle compensación bases imponibles negativas - 2011 - Pendiente aplicación en periodos futuros [015]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ725,getDouble(line, 776,17))  // Detalle compensación bases imponibles negativas - 2012 - Pendiente aplicación a principio periodo  [725]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ726,getDouble(line, 793,17))  // Detalle compensación bases imponibles negativas - 2012 - Aplicado en esta liquidación [726]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ727,getDouble(line, 810,17))  // Detalle compensación bases imponibles negativas - 2012 - Pendiente aplicación en periodos futuros [727]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ534,getDouble(line, 827,17))  // Detalle compensación bases imponibles negativas - 2013 (*) - Pendiente aplicación a principio periodo  [534]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ535,getDouble(line, 844,17))  // Detalle compensación bases imponibles negativas - 2013  (*) - Aplicado en esta liquidación [535]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ536,getDouble(line, 861,17))  // Detalle compensación bases imponibles negativas - 2013 (*) - Pendiente aplicación en periodos futuros [536]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ670,getDouble(line, 878,17))  // Detalle compensación bases imponibles negativas - TOTAL - Pendiente aplicación a principio periodo [670]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ547,getDouble(line, 895,17))  // Detalle compensación bases imponibles negativas - TOTAL - Aplicado en esta liquidación [547]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ671,getDouble(line, 912,17))  // Detalle compensación bases imponibles negativas - TOTAL - Pendiente de aplicación en periodos futuros [671]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN101,getDouble(line, 929,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2006- Deducción pendiente/generada [101]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN102,getDouble(line, 946,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2006 - Tipo de gravamen periodo generación [102]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN696,getDouble(line, 950,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2006 - 2013 Deducción pendiente [696]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN697,getDouble(line, 967,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2006 - Deducción aplicada en esta liquidación [697]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN104,getDouble(line, 984,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2007 - Deducción pendiente/generada [104]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN105,getDouble(line,1001,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2007 - Tipo de gravamen periodo generación [105]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN846,getDouble(line,1005,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2007 - 2013 Deducción pendiente [846]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN847,getDouble(line,1022,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2007 - Deducción aplicada en esta liquidación [847]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN848,getDouble(line,1039,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2007 - Deducción pendiente períodos futuros [848]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN106,getDouble(line,1056,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2008 - Deducción pendiente/generada [106]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN107,getDouble(line,1073,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2008 - Tipo de gravamen periodo generación [107]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN282,getDouble(line,1077,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2008 - 2013 Deducción pendiente [282]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN283,getDouble(line,1094,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2008 - Deducción aplicada en esta liquidación [283]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN284,getDouble(line,1111,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2008 - Deducción pendiente períodos futuros [284]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN108,getDouble(line,1128,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2009 - Deducción pendiente/generada [108]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN109,getDouble(line,1145,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2009 - Tipo de gravamen periodo generación [109]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN702,getDouble(line,1149,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2009 - 2013 Deducción pendiente [702]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN703,getDouble(line,1166,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2009 - Deducción aplicada en esta liquidación [703]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN707,getDouble(line,1183,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2009 - Deducción pendiente períodos futuros [707]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN110,getDouble(line,1200,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2010 - Deducción pendiente/generada [110]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN111,getDouble(line,1217,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2010 - Tipo de gravamen periodo generación [111]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN071,getDouble(line,1221,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2010 - 2013 Deducción pendiente [071]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN187,getDouble(line,1238,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2010 - Deducción aplicada en esta liquidación [187]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN300,getDouble(line,1255,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2010 - Deducción pendiente períodos futuros [300]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN112,getDouble(line,1272,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2011 - Deducción pendiente/generada [112]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN113,getDouble(line,1289,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2011 - Tipo de gravamen periodo generación [113]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN025,getDouble(line,1293,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2011 - 2013 Deducción pendiente [025]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN026,getDouble(line,1310,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2011 - Deducción aplicada en esta liquidación [026]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN027,getDouble(line,1327,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2011 - Deducción pendiente períodos futuros [027]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN114,getDouble(line,1344,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2012 - Deducción pendiente/generada [114]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN115,getDouble(line,1361,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2012 - Tipo de gravamen periodo generación [115]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN714,getDouble(line,1365,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2012 - 2013 Deducción pendiente [714]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN715,getDouble(line,1382,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2012 - Deducción aplicada en esta liquidación [715]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN716,getDouble(line,1399,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2012 - Deducción pendiente períodos futuros [716]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN735,getDouble(line,1416,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2013 (*) - Deducción pendiente/generada [735]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN920,getDouble(line,1433,4 ))  // Deducciones doble imposición interna 2006-2013 - DI interna 2013 (*) - Tipo de gravamen periodo generación [920]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN736,getDouble(line,1437,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2013 (*) - 2013 Deducción pendiente [736]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN737,getDouble(line,1454,17))  // Deducciones doble imposición interna 2006-2013- DI interna 2013 (*) - Deducción aplicada en esta liquidación [737]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN738,getDouble(line,1471,17))  // Deducciones doble imposición interna 2006-2013 - DI interna 2013 (*) - Deducción pendiente períodos futuros [738]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN116,getDouble(line,1488,17))  // Deducciones doble imposición interna - Total 2006-2013 - Deducción pendiente/generada [116]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN117,getDouble(line,1505,17))  // Deducciones doble imposición interna - Total 2006-2013 -  2013 Deducción pendiente [117]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN570,getDouble(line,1522,17))  // Deducciones doble imposición interna - Total 2006-2013 -  Deducción aplicada en esta liquidación [570]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN118,getDouble(line,1539,17))  // Deducciones doble imposición interna - Total 2006-2013 -  Deducción pendiente períodos futuros [118]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN103,getDouble(line,1556,7 ))  // Deducciones doble imposición interna - Tipo de gravamen 2013 [103]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN119,getDouble(line,1563,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 50% - Deducción pendiente/generada  [119]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN120,getDouble(line,1580,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 50% - 2013 Deducción pendiente [120]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN121,getDouble(line,1597,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 50% -  Deducción aplicada en esta liquidación [121]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN122,getDouble(line,1614,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 50% -  Deducción pendiente períodos futuros [122]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN123,getDouble(line,1631,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 100% - Deducción pendiente/generada [123]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN124,getDouble(line,1648,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 100% -  2013 Deducción pendiente [124]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN125,getDouble(line,1665,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 100% -  Deducción aplicada en esta liquidación [125]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN126,getDouble(line,1682,17))  // Deducciones doble imposición interna - DI interna 2013 - Intersoc.al 100% -  Deducción pendiente períodos futuros [126]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN127,getDouble(line,1699,17))  // Deducciones doble imposición interna - DI interna 2013 - Plusvalías fuente interna - Deducción pendiente/generada [127]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN128,getDouble(line,1716,17))  // Deducciones doble imposición interna - DI interna 2013 - Plusvalías fuente interna - 2013 Deducción pendiente [128]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN129,getDouble(line,1733,17))  // Deducciones doble imposición interna - DI interna 2013 - Plusvalías fuente interna -  Deducción aplicada en esta liquidación [129]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN130,getDouble(line,1750,17))  // Deducciones doble imposición interna - DI interna 2013 - Plusvalías fuente interna -  Deducción pendiente períodos futuros [130]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN131,getDouble(line,1767,17))  // Deducciones doble imposición interna - Total 2013 - Deducción pendiente/generada [131]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN132,getDouble(line,1784,17))  // Deducciones doble imposición interna - Total 2013 - 2013 Deducción pendiente [132]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN571,getDouble(line,1801,17))  // Deducciones doble imposición interna - Total 2013 - Deducción aplicada en esta liquidación [571]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN133,getDouble(line,1818,17))  // Deducciones doble imposición interna - Total 2013 - Deducción pendiente períodos futuros [133]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN151,getDouble(line,1835,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2003 - Deducción pendiente/generada [151]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN152,getDouble(line,1852,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2003 - Tipo de gravamen periodo generación [152]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN711,getDouble(line,1856,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2003 - 2013 Deducción pendiente [711]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN712,getDouble(line,1873,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2003 - Deducción aplicada en esta liquidación [712]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN153,getDouble(line,1890,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2004 - Deducción pendiente/generada [153]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN728,getDouble(line,1907,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2004 - Tipo de gravamen periodo generación [728]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN637,getDouble(line,1911,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2004 - 2013 Deducción pendiente [637]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN638,getDouble(line,1928,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2004 - Deducción aplicada en esta liquidación [638]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN639,getDouble(line,1945,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2004 - Deducción pendiente períodos futuros [639]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN154,getDouble(line,1962,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2005 - Deducción pendiente/generada [154]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN729,getDouble(line,1979,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2005 - Tipo de gravamen periodo generación [729]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN849,getDouble(line,1983,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2005 - 2013 Deducción pendiente [849]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN894,getDouble(line,2000,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2005 - Deducción aplicada en esta liquidación [894]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN197,getDouble(line,2017,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2005 - Deducción pendiente períodos futuros [197]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN155,getDouble(line,2034,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2006 - Deducción pendiente/generada [155]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN730,getDouble(line,2051,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2006 - Tipo de gravamen periodo generación [730] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN285,getDouble(line,2055,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2006 - 2013 Deducción pendiente [285]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN286,getDouble(line,2072,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2006 - Deducción aplicada en esta liquidación [286]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN287,getDouble(line,2089,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2006 - Deducción pendiente períodos futuros [287]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN156,getDouble(line,2106,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2007 - Deducción pendiente/generada [156]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN731,getDouble(line,2123,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2007 - Tipo de gravamen periodo generación [731]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN825,getDouble(line,2127,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2007 - 2013 Deducción pendiente [825]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN826,getDouble(line,2144,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2007 - Deducción aplicada en esta liquidación [826]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN827,getDouble(line,2161,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2007 - Deducción pendiente períodos futuros [827]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN157,getDouble(line,2178,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2008 - Deducción pendiente/generada [157]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN732,getDouble(line,2195,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2008 - Tipo de gravamen periodo generación [732]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN001,getDouble(line,2199,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2008 - 2013 Deducción pendiente [001]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN002,getDouble(line,2216,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2008 - Deducción aplicada en esta liquidación [002]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN003,getDouble(line,2233,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2008- Deducción pendiente períodos futuros [003]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN158,getDouble(line,2250,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2009 - Deducción pendiente/generada [158]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN733,getDouble(line,2267,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2009 - Tipo de gravamen periodo generación [733]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN028,getDouble(line,2271,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2009 - 2013 Deducción pendiente [028]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN029,getDouble(line,2288,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2009 - Deducción aplicada en esta liquidación [029]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN030,getDouble(line,2305,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2009 - Deducción pendiente períodos futuros [030]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN159,getDouble(line,2322,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2010 - Deducción pendiente/generada [159]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN734,getDouble(line,2339,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2010 - Tipo de gravamen periodo generación [734]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN717,getDouble(line,2343,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2010 - 2013 Deducción pendiente [717]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN718,getDouble(line,2360,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2010 - Deducción aplicada en esta liquidación [718]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN719,getDouble(line,2377,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2010 - Deducción pendiente períodos futuros [719]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN720,getDouble(line,2394,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2011 - Deducción pendiente/generada [720]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN721,getDouble(line,2411,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2011 - Tipo de gravamen periodo generación [721]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN722,getDouble(line,2415,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2011 - 2013 Deducción pendiente [722]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN723,getDouble(line,2432,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2011 - Deducción aplicada en esta liquidación [723]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN724,getDouble(line,2449,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2011 - Deducción pendiente períodos futuros [724]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN739,getDouble(line,2466,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2012 - Deducción pendiente/generada [739]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN921,getDouble(line,2483,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2012 - Tipo de gravamen periodo generación [921]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN740,getDouble(line,2487,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2012 - 2013 Deducción pendiente [740]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN741,getDouble(line,2504,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2012 - Deducción aplicada en esta liquidación [741]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN742,getDouble(line,2521,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2012 - Deducción pendiente períodos futuros [742]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN134,getDouble(line,2538,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 (*) - Deducción pendiente/generada [134]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN926,getDouble(line,2555,4 ))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 (*) - Tipo de gravamen periodo generación [926]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN135,getDouble(line,2559,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 (*) - 2013 Deducción pendiente [135]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN136,getDouble(line,2576,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 (*) - Deducción aplicada en esta liquidación [136]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN137,getDouble(line,2593,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 (*) - Deducción pendiente períodos futuros [137]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN160,getDouble(line,2610,17))  // Deducciones doble imposición internacional - Total 2003-2013 - Deducción pendiente/generada [160]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN161,getDouble(line,2627,17))  // Deducciones doble imposición internacional - Total 2003-2013 - 2013 Deducción pendiente [161]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN572,getDouble(line,2644,17))  // Deducciones doble imposición internacional - Total 2003-2013 - Deducción aplicada en esta liquidación [572]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN162,getDouble(line,2661,17))  // Deducciones doble imposición internacional - Total 2003-2013 - Deducción pendiente ejercicios futuros [162]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN103,getDouble(line,2678,7 ))  // Deducciones doble imposición internacional - Tipo de gravamen 2013 [103]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN163,getDouble(line,2685,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Impuesto soportado sujeto pasivo - Deducción pendiente/generada [163]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN164,getDouble(line,2702,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Impuesto soportado sujeto pasivo - 2013 Deducción pendiente [164]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN165,getDouble(line,2719,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Impuesto soportado sujeto pasivo -  Deducción aplicada en esta liquidación [165]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN166,getDouble(line,2736,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Impuesto soportado sujeto pasivo -  Deducción pendiente períodos futuros [166]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN167,getDouble(line,2753,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Dividendos y participaciones en beneficios  - Deducción pendiente/generada [167]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN168,getDouble(line,2770,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Dividendos y participacionesen beneficios  - 2013 Deducción pendiente [168]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN169,getDouble(line,2787,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Dividendos y participaciones en beneficios -  Deducción aplicada en esta liquidación [169]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN170,getDouble(line,2804,17))  // Deducciones doble imposición internacional 2003-2013 - DI internacional 2013 - Dividendos y participaciones en beneficios  -  Deducción pendiente períodos futuros [170]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN171,getDouble(line,2821,17))  // Deducciones doble imposición internacional 2003-2013 - Total 2013 - Deducción pendiente/generada [171]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN172,getDouble(line,2838,17))  // Deducciones doble imposición internacional 2003-2013 - Total 2013 - 2013 Deducción pendiente [172]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN573,getDouble(line,2855,17))  // Deducciones doble imposición internacional 2003-2013 - Total 2013 - Deducción aplicada en esta liquidación [573]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN174,getDouble(line,2872,17))  // Deducciones doble imposición internacional 2003-2013 - Total 2013 - Deducción pendiente períodos futuros [174]
			})
			
		,PAG16 ("T200160", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN835,getDouble(line, 11,17))  // Deducc. Art. 36 ter Ley 43 / 1995.  2002 - Deducción pendiente/generada [835]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN836,getDouble(line, 28,17))  // Deducc. Art. 36 ter Ley 43 / 1995.  2002 - Aplicado en esta liquidación [836]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN837,getDouble(line, 45,17))  // Deducc. Art. 36 ter Ley 43 / 1995.  2002 - Pendiente aplicación en periodos futuros [837]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN838,getDouble(line, 62,17))  // Deducc. Art. 36 ter Ley 43 / 1995.  2003 - Deducción pendiente/generada [838]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN839,getDouble(line, 79,17))  // Deducc. Art. 36 ter Ley 43 / 1995.  2003 - Aplicado en esta liquidación [839]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN840,getDouble(line, 96,17))  // Deducc. Art. 36 ter Ley 43 / 1995.  2003 - Pendiente aplicación en periodos futuros [840]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN932,getDouble(line,113,17))  // Deducc. Art. 42 L.I.S. 2004 - Deducción pendiente/generada [932]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN933,getDouble(line,130,17))  // Deducc. Art. 42 L.I.S. 2004 - Aplicado en esta liquidación [933]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN934,getDouble(line,147,17))  // Deducc. Art. 42 L.I.S. 2004 - Pendiente aplicación en periodos futuros [934]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN297,getDouble(line,164,17))  // Deducc. Art. 42 L.I.S. 2005 - Deducción pendiente/generada [297]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN298,getDouble(line,181,17))  // Deducc. Art. 42 L.I.S. 2005 - Aplicado en esta liquidación [298]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN299,getDouble(line,198,17))  // Deducc. Art. 42 L.I.S. 2005 - Pendiente aplicación en periodos futuros [299]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN090,getDouble(line,215,17))  // Deducc. Art. 42 L.I.S. 2006 - Deducción pendiente/generada [090]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN091,getDouble(line,232,17))  // Deducc. Art. 42 L.I.S. 2006 - Aplicado en esta liquidación [091]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN092,getDouble(line,249,17))  // Deducc. Art. 42 L.I.S. 2006 - Pendiente aplicación en periodos futuros [092]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN004,getDouble(line,266,17))  // Deducc. Art. 42 L.I.S. 2007 - Deducción pendiente/generada [004]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN005,getDouble(line,283,17))  // Deducc. Art. 42 L.I.S. 2007 - Aplicado en esta liquidación [005]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN006,getDouble(line,300,17))  // Deducc. Art. 42 L.I.S. 2007 - Pendiente aplicación en periodos futuros [006]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN031,getDouble(line,317,17))  // Deducc. Art. 42 L.I.S. 2008 - Deducción pendiente/generada [031]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN032,getDouble(line,334,17))  // Deducc. Art. 42 L.I.S. 2008 - Aplicado en esta liquidación [032]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN033,getDouble(line,351,17))  // Deducc. Art. 42 L.I.S. 2008 - Pendiente aplicación en periodos futuros [033]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN022,getDouble(line,368,17))  // Deducc. Art. 42 L.I.S. 2009 - Deducción pendiente/generada [022]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN023,getDouble(line,385,17))  // Deducc. Art. 42 L.I.S. 2009 - Aplicado en esta liquidación [023]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN024,getDouble(line,402,17))  // Deducc. Art. 42 L.I.S. 2009 - Pendiente aplicación en periodos futuros [024]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN040,getDouble(line,419,17))  // Deducc. Art. 42 L.I.S. 2010 - Deducción pendiente/generada [040]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN041,getDouble(line,436,17))  // Deducc. Art. 42 L.I.S. 2010 - Aplicado en esta liquidación [041]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN042,getDouble(line,453,17))  // Deducc. Art. 42 L.I.S. 2010 - Pendiente aplicación en periodos futuros [042]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN138,getDouble(line,470,17))  // Deducc. Art. 42 L.I.S. 2011 - Deducción pendiente/generada [138]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN139,getDouble(line,487,17))  // Deducc. Art. 42 L.I.S. 2011 - Aplicado en esta liquidación [139]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN140,getDouble(line,504,17))  // Deducc. Art. 42 L.I.S. 2011 - Pendiente aplicación en periodos futuros [140]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN141,getDouble(line,521,17))  // Deducc. Art. 42 L.I.S. 2012 - Deducción pendiente/generada [141]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN142,getDouble(line,538,17))  // Deducc. Art. 42 L.I.S. 2012 - Aplicado en esta liquidación [142]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN143,getDouble(line,555,17))  // Deducc. Art. 42 L.I.S. 2012 - Pendiente aplicación en periodos futuros [143]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN188,getDouble(line,572,17))  // Deducc. Art. 42 L.I.S. 2013 - Deducción pendiente/generada [188]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN189,getDouble(line,589,17))  // Deducc. Art. 42 L.I.S. 2013 - Aplicado en esta liquidación [189]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN190,getDouble(line,606,17))  // Deducc. Art. 42 L.I.S. 2013 - Pendiente aplicación en periodos futuros [190]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN841,getDouble(line,623,17))  // Deducc. Art. 36 ter Ley 43 / 1995 y 42 L.I.S. Total Deducc. - Deducción pendiente/generada [841]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN585,getDouble(line,640,17))  // Deducc. Art. 36 ter Ley 43 / 1995 y 42 L.I.S. Total Deducc. - Aplicado en esta liquidación [585]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN843,getDouble(line,657,17))  // Deducc. Art. 36 ter Ley 43 / 1995 y 42 L.I.S. Total Deducc. - Pendiente aplicación en periodos futuros [843]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN749,getDouble(line,674,17))  // Deducciones DT octava L.I.S. - 2008 Periodificación - Deducción pendiente/generada [749]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN750,getDouble(line,691,17))  // Deducciones DT octava L.I.S. - 2008 Periodificación - Aplicado en esta liquidación [750]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN752,getDouble(line,708,17))  // Deducciones DT octava L.I.S. - 2009 Periodificación - Deducción pendiente/generada [752]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN753,getDouble(line,725,17))  // Deducciones DT octava L.I.S. - 2009 Periodificación - Aplicado en esta liquidación [753]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN754,getDouble(line,742,17))  // Deducciones DT octava L.I.S. - 2009 Periodificación - Pendiente de aplicación en periodos futuros [754]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN755,getDouble(line,759,17))  // Deducciones DT octava L.I.S. - 2010 Periodificación - Deducción pendiente/generada [755]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN756,getDouble(line,776,17))  // Deducciones DT octava L.I.S. - 2010 Periodificación - Aplicado en esta liquidación [756]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN757,getDouble(line,793,17))  // Deducciones DT octava L.I.S. - 2010 Periodificación - Pendiente de aplicación en periodos futuros [757]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN758,getDouble(line,810,17))  // Deducciones DT octava L.I.S. - 2011 Periodificación - Deducción pendiente/generada [758]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN759,getDouble(line,827,17))  // Deducciones DT octava L.I.S. - 2011 Periodificación - Aplicado en esta liquidación [759]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN760,getDouble(line,844,17))  // Deducciones DT octava L.I.S. - 2011 Periodificación - Pendiente de aplicación en periodos futuros [760]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN761,getDouble(line,861,17))  // Deducciones DT octava L.I.S. - 2012 Periodificación - Deducción pendiente/generada [761]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN762,getDouble(line,878,17))  // Deducciones DT octava L.I.S. - 2012 Periodificación - Aplicado en esta liquidación [762]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN763,getDouble(line,895,17))  // Deducciones DT octava L.I.S. - 2012 Periodificación - Pendiente de aplicación en periodos futuros [763]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN744,getDouble(line,912,17))  // Deducciones DT octava L.I.S. - 2013 Periodificación - Deducción pendiente/generada [744]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN745,getDouble(line,929,17))  // Deducciones DT octava L.I.S. - 2013 Periodificación - Aplicado en esta liquidación [745]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN746,getDouble(line,946,17))  // Deducciones DT octava L.I.S. - 2013 Periodificación - Pendiente de aplicación en periodos futuros [746]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN764,getDouble(line,963,17))  // Deducciones DT octava L.I.S. Total deducciones - Deducción pendiente/generada [764]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN584,getDouble(line,980,17))  // Deducciones DT octava L.I.S. Total deducciones - Aplicado en esta liquidación [584]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN765,getDouble(line,997,17))  // Deducciones DT octava L.I.S. Total deducciones - Pendiente de aplicación en periodos futuros [765]
			
			// [...] NO ESTA EN EL MODELO - Regimen Especial de la reserva para inversiones en Canarias 			
					
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN854,getDouble(line,1371,17))  // Deducciones inversión Canarias - Activos fijos 2008 - Deducción pendiente/generada [854]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN855,getDouble(line,1388,17))  // Deducciones inversión Canarias - Activos fijos 2008 - Aplicado en esta liquidación [855]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN857,getDouble(line,1405,17))  // Deducciones inversión Canarias - Activos fijos 2009 - Deducción pendiente/generada [857]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN858,getDouble(line,1422,17))  // Deducciones inversión Canarias - Activos fijos 2009 - Aplicado en esta liquidación [858]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN859,getDouble(line,1439,17))  // Deducciones inversión Canarias - Activos fijos 2009 - Pendiente de aplicación en periodos futuros [859]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN860,getDouble(line,1456,17))  // Deducciones inversión Canarias - Activos fijos 2010 - Deducción pendiente/generada [860]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN861,getDouble(line,1473,17))  // Deducciones inversión Canarias - Activos fijos 2010 - Aplicado en esta liquidación [861]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN862,getDouble(line,1490,17))  // Deducciones inversión Canarias - Activos fijos 2010 - Pendiente de aplicación en periodos futuros [862]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN863,getDouble(line,1507,17))  // Deducciones inversión Canarias - Activos fijos 2011 - Deducción pendiente/generada [863]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN864,getDouble(line,1524,17))  // Deducciones inversión Canarias - Activos fijos 2011 - Aplicado en esta liquidación [864]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN865,getDouble(line,1541,17))  // Deducciones inversión Canarias - Activos fijos 2011 - Pendiente de aplicación en periodos futuros [865]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN883,getDouble(line,1558,17))  // Deducciones inversión Canarias - Activos fijos 2012 - Deducción pendiente/generada [883]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN884,getDouble(line,1575,17))  // Deducciones inversión Canarias - Activos fijos 2012 - Aplicado en esta liquidación [884]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN885,getDouble(line,1592,17))  // Deducciones inversión Canarias - Activos fijos 2012 - Pendiente de aplicación en periodos futuros [885]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN088,getDouble(line,1609,17))  // Deducciones inversión Canarias - Inversiones Canarias 1997 - Deducción pendiente/generada [088]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN564,getDouble(line,1626,17))  // Deducciones inversión Canarias - Inversiones Canarias 1997 - Aplicado en esta liquidación [564]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN801,getDouble(line,1643,17))  // Deducciones inversión Canarias - Inversiones Canarias 1997 - Pendiente de aplicación en periodos futuros [801]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN194,getDouble(line,1660,17))  // Deducciones inversión Canarias - Inversiones Canarias 1998 - Deducción pendiente/generada [194]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN195,getDouble(line,1677,17))  // Deducciones inversión Canarias - Inversiones Canarias 1998 - Aplicado en esta liquidación [195]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN196,getDouble(line,1694,17))  // Deducciones inversión Canarias - Inversiones Canarias 1998 - Pendiente de aplicación en periodos futuros [196]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN868,getDouble(line,1711,17))  // Deducciones inversión Canarias - Inversiones Canarias 1999 - Deducción pendiente/generada  [868]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN869,getDouble(line,1728,17))  // Deducciones inversión Canarias - Inversiones Canarias 1999 - Aplicado en esta liquidación [869]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN834,getDouble(line,1745,17))  // Deducciones inversión Canarias - Inversiones Canarias 1999 - Pendiente de aplicación en periodos futuros [834]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN871,getDouble(line,1762,17))  // Deducciones inversión Canarias - Inversiones Canarias 2000 - Deducción pendiente/generada [871] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN872,getDouble(line,1779,17))  // Deducciones inversión Canarias - Inversiones Canarias 2000 - Aplicado en esta liquidación [872]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN873,getDouble(line,1796,17))  // Deducciones inversión Canarias - Inversiones Canarias 2000 - Pendiente de aplicación en periodos futuros [873]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN874,getDouble(line,1813,17))  // Deducciones inversión Canarias - Inversiones Canarias 2001 - Deducción pendiente/generada [874]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN875,getDouble(line,1830,17))  // Deducciones inversión Canarias - Inversiones Canarias 2001 - Aplicado en esta liquidación [875]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN876,getDouble(line,1847,17))  // Deducciones inversión Canarias - Inversiones Canarias 2001 - Pendiente de aplicación en periodos futuros [876]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN877,getDouble(line,1864,17))  // Deducciones inversión Canarias - Inversiones Canarias 2002 - Deducción pendiente/generada [877]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN878,getDouble(line,1881,17))  // Deducciones inversión Canarias - Inversiones Canarias 2002 - Aplicado en esta liquidación [878]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN879,getDouble(line,1898,17))  // Deducciones inversión Canarias - Inversiones Canarias 2002 - Pendiente de aplicación en periodos futuros [879]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN880,getDouble(line,1915,17))  // Deducciones inversión Canarias - Inversiones Canarias 2003 - Deducción pendiente/generada [880]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN881,getDouble(line,1932,17))  // Deducciones inversión Canarias - Inversiones Canarias 2003 - Aplicado en esta liquidación [881]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN882,getDouble(line,1949,17))  // Deducciones inversión Canarias - Inversiones Canarias 2003 - Pendiente de aplicación en periodos futuros [882]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN866,getDouble(line,1966,17))  // Deducciones inversión Canarias - Inversiones Canarias 2004 - Deducción pendiente/generada [866]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN867,getDouble(line,1983,17))  // Deducciones inversión Canarias - Inversiones Canarias 2004 - Aplicado en esta liquidación [867]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN870,getDouble(line,2000,17))  // Deducciones inversión Canarias - Inversiones Canarias 2004 - Pendiente de aplicación en periodos futuros [870]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN939,getDouble(line,2017,17))  // Deducciones inversión Canarias - Inversiones Canarias 2005 - Deducción pendiente/generada [939]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN940,getDouble(line,2034,17))  // Deducciones inversión Canarias - Inversiones Canarias 2005 - Aplicado en esta liquidación [940]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN941,getDouble(line,2051,17))  // Deducciones inversión Canarias - Inversiones Canarias 2005 - Pendiente de aplicación en periodos futuros [941]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN191,getDouble(line,2068,17))  // Deducciones inversión Canarias - Inversiones Canarias 2006 - Deducción pendiente/generada [191]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN192,getDouble(line,2085,17))  // Deducciones inversión Canarias - Inversiones Canarias 2006 - Aplicado en esta liquidación [192]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN193,getDouble(line,2102,17))  // Deducciones inversión Canarias - Inversiones Canarias 2006 - Pendiente de aplicación en periodos futuros [193]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN613,getDouble(line,2119,17))  // Deducciones inversión Canarias - Inversiones Canarias 2007 - Deducción pendiente/generada  [613]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN614,getDouble(line,2136,17))  // Deducciones inversión Canarias - Inversiones Canarias 2007 - Aplicado en esta liquidación [614]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN701,getDouble(line,2153,17))  // Deducciones inversión Canarias - Inversiones Canarias 2007 - Pendiente de aplicación en periodos futuros [701]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN200,getDouble(line,2170,17))  // Deducciones inversión Canarias - Inversiones Canarias 2008 - Deducción pendiente/generada [200]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN257,getDouble(line,2187,17))  // Deducciones inversión Canarias - Inversiones Canarias 2008 - Aplicado en esta liquidación [257]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN011,getDouble(line,2204,17))  // Deducciones inversión Canarias - Inversiones Canarias 2008 - Pendiente de aplicación en periodos futuros [011]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN037,getDouble(line,2221,17))  // Deducciones inversión Canarias - Inversiones Canarias 2009 - Deducción pendiente/generada [037]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN038,getDouble(line,2238,17))  // Deducciones inversión Canarias - Inversiones Canarias 2009 - Aplicado en esta liquidación [038]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN039,getDouble(line,2255,17))  // Deducciones inversión Canarias - Inversiones Canarias 2009 - Pendiente de aplicación en periodos futuros [039]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN044,getDouble(line,2272,17))  // Deducciones inversión Canarias - Inversiones Canarias 2010 - Deducción pendiente/generada [044]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN045,getDouble(line,2289,17))  // Deducciones inversión Canarias - Inversiones Canarias 2010 - Aplicado en esta liquidación [045]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN046,getDouble(line,2306,17))  // Deducciones inversión Canarias - Inversiones Canarias 2010 - Pendiente de aplicación en periodos futuros [046]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN528,getDouble(line,2323,17))  // Deducciones inversión Canarias - Inversiones Canarias 2011 - Deducción pendiente/generada [528]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN529,getDouble(line,2340,17))  // Deducciones inversión Canarias - Inversiones Canarias 2011 - Aplicado en esta liquidación [529]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN530,getDouble(line,2357,17))  // Deducciones inversión Canarias - Inversiones Canarias 2011 - Pendiente de aplicación en periodos futuros [530]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN144,getDouble(line,2374,17))  // Deducciones inversión Canarias - Inversiones Canarias 2012 - Deducción pendiente/generada [144]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN145,getDouble(line,2391,17))  // Deducciones inversión Canarias - Inversiones Canarias 2012 - Aplicado en esta liquidación [145]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN146,getDouble(line,2408,17))  // Deducciones inversión Canarias - Inversiones Canarias 2012 - Pendiente de aplicación en periodos futuros [146]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN147,getDouble(line,2425,17))  // Deducciones inversión Canarias - Inversiones Canarias 2013 - Deducción pendiente/generada [147]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN148,getDouble(line,2442,17))  // Deducciones inversión Canarias - Inversiones Canarias 2013 - Aplicado en esta liquidación [148]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN149,getDouble(line,2459,17))  // Deducciones inversión Canarias - Inversiones Canarias 2013 - Pendiente de aplicación en periodos futuros [149]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN852,getDouble(line,2476,17))  // Deducciones inversión Canarias - Activos fijos 2013 - Deducción pendiente/generada [852]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN853,getDouble(line,2493,17))  // Deducciones inversión Canarias - Activos fijos 2013 - Aplicado en esta liquidación [853]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN856,getDouble(line,2510,17))  // Deducciones inversión Canarias - Activos fijos 2013 - Pendiente de aplicación en periodos futuros [856]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN886,getDouble(line,2527,17))  // Deducciones inversión Canarias - Total deducciones - Deducción pendiente/generada [886]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN590,getDouble(line,2544,17))  // Deducciones inversión Canarias - Total deducciones - Aplicado en esta liquidación [590]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN887,getDouble(line,2561,17))  // Deducciones inversión Canarias - Total deducciones - Pendiente de aplicación en periodos futuros [887]			
			})

		,PAG17 ("T200170", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN842,getDouble(line,  11,17))  // Deducc. para  incentivar determ.actividades - 1997 Suma deducciones - Deducción pendiente/generada [842] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN844,getDouble(line,  28,17))  // Deducc. para  incentivar determ.actividades - 1997 Suma deducciones - Aplicado en esta liquidación [844]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN845,getDouble(line,  45,17))  // Deducc. para  incentivar determ.actividades - 1997 Suma deducciones - Pendiente de aplicación en periodos futuros [845]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN768,getDouble(line,  62,17))  // Deducc. para  incentivar determ.actividades - 1998 Suma deducciones - Deducción pendiente/generada [768]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN769,getDouble(line,  79,17))  // Deducc. para  incentivar determ.actividades - 1998 Suma deducciones - Aplicado en esta liquidación [769]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN770,getDouble(line,  96,17))  // Deducc. para  incentivar determ.actividades - 1998 Suma deducciones - Pendiente de aplicación en periodos futuros [770]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN774,getDouble(line, 113,17))  // Deducc. para  incentivar determ.actividades - 1999 Suma deducciones - Deducción pendiente/generada [774]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN775,getDouble(line, 130,17))  // Deducc. para  incentivar determ.actividades - 1999 Suma deducciones - Aplicado en esta liquidación [775]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN776,getDouble(line, 147,17))  // Deducc. para  incentivar determ.actividades - 1999 Suma deducciones - Pendiente de aplicación en periodos futuros [776]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN780,getDouble(line, 164,17))  // Deducc. para  incentivar determ.actividades - 2000 Suma deducciones - Deducción pendiente/generada [780]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN781,getDouble(line, 181,17))  // Deducc. para  incentivar determ.actividades - 2000 Suma deducciones - Aplicado en esta liquidación [781]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN782,getDouble(line, 198,17))  // Deducc. para  incentivar determ.actividades - 2000 Suma deducciones - Pendiente de aplicación en periodos futuros [782]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN786,getDouble(line, 215,17))  // Deducc. para  incentivar determ.actividades - 2001 Suma deducciones - Deducción pendiente/generada [786]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN787,getDouble(line, 232,17))  // Deducc. para  incentivar determ.actividades - 2001 Suma deducciones - Aplicado en esta liquidación [787]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN788,getDouble(line, 249,17))  // Deducc. para  incentivar determ.actividades - 2001 Suma deducciones - Pendiente de aplicación en periodos futuros [788]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN766,getDouble(line, 266,17))  // Deducc. para  incentivar determ.actividades - 2002 Suma deducciones - Deducción pendiente/generada [766]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN767,getDouble(line, 283,17))  // Deducc. para  incentivar determ.actividades - 2002 Suma deducciones - Aplicado en esta liquidación [767]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN833,getDouble(line, 300,17))  // Deducc. para  incentivar determ.actividades - 2002 Suma deducciones - Pendiente de aplicación en periodos futuros [833]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN198,getDouble(line, 317,17))  // Deducc. para  incentivar determ.actividades - 2003 Suma deducciones - Deducción pendiente/generada [198]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN896,getDouble(line, 334,17))  // Deducc. para  incentivar determ.actividades - 2003 Suma deducciones - Aplicado en esta liquidación [896]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN897,getDouble(line, 351,17))  // Deducc. para  incentivar determ.actividades - 2003 Suma deducciones - Pendiente de aplicación en periodos futuros [897]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN288,getDouble(line, 368,17))  // Deducc. para  incentivar determ.actividades - 2004 Suma deducciones - Deducción pendiente/generada [288]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN289,getDouble(line, 385,17))  // Deducc. para  incentivar determ.actividades - 2004 Suma deducciones - Aplicado en esta liquidación [289]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN290,getDouble(line, 402,17))  // Deducc. para  incentivar determ.actividades - 2004 Suma deducciones - Pendiente de aplicación en periodos futuros [290]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN466,getDouble(line, 419,17))  // Deducc. para  incentivar determ.actividades - 2005 Suma deducciones - Deducción pendiente/generada [466]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN467,getDouble(line, 436,17))  // Deducc. para  incentivar determ.actividades - 2005 Suma deducciones - Aplicado en esta liquidación [467]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN468,getDouble(line, 453,17))  // Deducc. para  incentivar determ.actividades - 2005 Suma deducciones - Pendiente de aplicación en periodos futuros [468]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN061,getDouble(line, 470,17))  // Deducc. para  incentivar determ.actividades - 2006 Suma deducciones - Deducción pendiente/generada [061]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN498,getDouble(line, 487,17))  // Deducc. para  incentivar determ.actividades - 2006 Suma deducciones - Aplicado en esta liquidación [498]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN586,getDouble(line, 504,17))  // Deducc. para  incentivar determ.actividades - 2006 Suma deducciones - Pendiente de aplicación en periodos futuros [586]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN472,getDouble(line, 521,17))  // Deducc. para  incentivar determ.actividades - 2007 Suma deducciones - Deducción pendiente/generada [472]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN473,getDouble(line, 538,17))  // Deducc. para  incentivar determ.actividades - 2007 Suma deducciones - Aplicado en esta liquidación [473]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN478,getDouble(line, 555,17))  // Deducc. para  incentivar determ.actividades - 2007 Suma deducciones - Pendiente de aplicación en periodos futuros [478]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN180,getDouble(line, 572,17))  // Deducc. para  incentivar determ.actividades - 2008 Suma deducciones - Deducción pendiente/generada [180]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN181,getDouble(line, 589,17))  // Deducc. para  incentivar determ.actividades - 2008 Suma deducciones - Aplicado en esta liquidación [181]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN182,getDouble(line, 606,17))  // Deducc. para  incentivar determ.actividades - 2008 Suma deducciones - Pendiente de aplicación en periodos futuros [182]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN531,getDouble(line, 623,17))  // Deducc. para  incentivar determ.actividades - 2009 Suma deducciones - Deducción pendiente/generada [531]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN532,getDouble(line, 640,17))  // Deducc. para  incentivar determ.actividades - 2009 Suma deducciones - Aplicado en esta liquidación [532]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN533,getDouble(line, 657,17))  // Deducc. para  incentivar determ.actividades - 2009 Suma deducciones - Pendiente de aplicación en periodos futuros [533]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN945,getDouble(line, 674,17))  // Deducc. para  incentivar determ.actividades - 2010 Suma deducciones - Deducción pendiente/generada [945]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN946,getDouble(line, 691,17))  // Deducc. para  incentivar determ.actividades - 2010 Suma deducciones - Aplicado en esta liquidación [946]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN947,getDouble(line, 708,17))  // Deducc. para  incentivar determ.actividades - 2010 Suma deducciones - Pendiente de aplicación en periodos futuros [947]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN960,getDouble(line, 725,17))  // Deducc. para incentivar determ.actividades - 2011 Suma deducciones - Deducción pendiente/generada [960]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN961,getDouble(line, 742,17))  // Deducc. para incentivar determ.actividades - 2011 Suma deducciones - Aplicado en esta liquidación [961]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN962,getDouble(line, 759,17))  // Deducc. para incentivar determ.actividades - 2011 Suma deducciones - Pendiente de aplicación en periodos futuros [962]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN183,getDouble(line, 776,17))  // Deducc. para incentivar determ.actividades - 2012 Suma deducciones - Deducción pendiente/generada [183]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN185,getDouble(line, 793,17))  // Deducc. para incentivar determ.actividades - 2012 Suma deducciones - Aplicado en esta liquidación [185]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN186,getDouble(line, 810,17))  // Deducc. para incentivar determ.actividades - 2012 Suma deducciones - Pendiente de aplicación en periodos futuros [186]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN792,getDouble(line, 827,17))  // Deducc. para incentivar determ.actividades - 2013 Inv.protección medio ambiente - Deducción pendiente/generada [792]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN793,getDouble(line, 844,17))  // Deducc. para incentivar determ.actividades - 2013 Inv. protección medio ambiente - Aplicado en esta liquidación [793]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN794,getDouble(line, 861,17))  // Deducc. para incentivar determ.actividades - 2013 Inv. protección medio ambiente - Pendiente de aplicación en periodos futuros [794]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN795,getDouble(line, 878,17))  // Deducc. para incentivar determ.actividades - 2013 Deducción creación empleo trabajadores discapacidad - Deducción pendiente/generada [795]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN796,getDouble(line, 895,17))  // Deducc. para incentivar determ.actividades - 2013 Deducción creación empleo trabajadores discapacidad - Deducción pendiente/generada [796]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN797,getDouble(line, 912,17))  // Deducc. para incentivar determ.actividades - 2013 Deducción creación empleo trabajadores discapacidad - Deducción pendiente/generada [797]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN798,getDouble(line, 929,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos investigación y desarrollo - Deducción pendiente/generada [798]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN799,getDouble(line, 946,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos investigación y desarrollo - Aplicado en esta liquidación [799]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN800,getDouble(line, 963,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos investigación y desarrollo - Pendiente de aplicación en periodos futuros [800]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN096,getDouble(line, 980,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos innovación tecnológica - Deducción pendiente/generada [096]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN698,getDouble(line, 997,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos innovación tecnológica - Aplicado en esta liquidación [698]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN713,getDouble(line,1014,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos innovación tecnológica - Pendiente de aplicación en periodos futuros [713]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN549,getDouble(line,1031,17))  // Deducc. para incentivar determ.actividades - 2013 Deducción inversión beneficios - Deducción pendiente/generada [549]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN888,getDouble(line,1048,17))  // Deducc. para incentivar determ.actividades - 2013 Deducción inversión beneficios - Aplicado en esta liquidación [888]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN889,getDouble(line,1065,17))  // Deducc. para incentivar determ.actividades - 2013 Deducción inversión beneficios - Pendiente de aplicación en periodos futuros [889]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN807,getDouble(line,1082,17))  // Deducc. para incentivar determ.actividades - 2013 Produc. cinematográficas - Deducción pendiente/generada [807]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN808,getDouble(line,1099,17))  // Deducc. para incentivar determ.actividades - 2013 Produc. cinematográficas - Aplicado en esta liquidación [808]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN809,getDouble(line,1116,17))  // Deducc. para incentivar determ.actividades - 2013 Produc. cinematográficas - Pendiente de aplicación en periodos futuros [809]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN810,getDouble(line,1133,17))  // Deducc. para incentivar determ.actividades - 2013 Bienes interés cultural - Deducción pendiente/generada [810]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN811,getDouble(line,1150,17))  // Deducc. para incentivar determ.actividades - 2013 Bienes interés cultural - Aplicado en esta liquidación [811]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN812,getDouble(line,1167,17))  // Deducc. para incentivar determ.actividades - 2013 Bienes interés cultural - Pendiente de aplicación en periodos futuros [812]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN816,getDouble(line,1184,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos formación profesional - Deducción pendiente/generada [816]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN817,getDouble(line,1201,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos formación profesional - Aplicado en esta liquidación [817]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN818,getDouble(line,1218,17))  // Deducc. para incentivar determ.actividades - 2013 Gastos formación profesional - Pendiente de aplicación en periodos futuros [818]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN819,getDouble(line,1235,17))  // Deducc. para incentivar determ.actividades - 2013 Edición  libros -  Deducción pendiente/generada [819]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN820,getDouble(line,1252,17))  // Deducc. para incentivar determ.actividades - 2013 Edición  libros -  Aplicado en esta liquidación [820]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN821,getDouble(line,1269,17))  // Deducc. para incentivar determ.actividades - 2013 Edición  libros -  Pendiente de aplicación en periodos futuros [821]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN963,getDouble(line,1286,17))  // Deducc. para incentivar determ.actividades - 2013 Creación empleo menores 30 años - Deducción pendiente/generada [963]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN964,getDouble(line,1303,17))  // Deducc. para incentivar determ.actividades - 2013 Creación empleo menores 30 años - Aplicado en esta liquidación [964]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN965,getDouble(line,1320,17))  // Deducc. para incentivar determ.actividades - 2013 Creación empleo menores 30 años - Pendiente de aplicación en periodos futuros [965]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN931,getDouble(line,1337,17))  // Deducc. para incentivar determ.actividades - 2013 Creación empleo contratación desempleados con prestación desempleo - Deducción pendiente/generada [931]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN502,getDouble(line,1354,17))  // Deducc. para incentivar determ.actividades - 2013 Creación empleo contratación desempleados con prestación desempleo - Aplicado en esta liquidación [502]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN751,getDouble(line,1371,17))  // Deducc. para incentivar determ.actividades - 2013 Creación empleo contratación desempleados con prestación desempleo - Pendiente de aplicación en periodos futuros [751]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN966,getDouble(line,1388,17))  // Deducc. para incentivar determ.actividades - 2013 Conmemoración Milenio fundación Reino de Granada - Deducción pendiente/generada [966]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN967,getDouble(line,1405,17))  // Deducc. para incentivar determ.actividades - 2013 Conmemoración Milenio fundación Reino de Granada - Aplicado en esta liquidación  [967]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN968,getDouble(line,1422,17))  // Deducc. para incentivar determ.actividades - 2013 Conmemoración Milenio fundación Reino de Granada - Pendiente de aplicación en periodos futuros [968]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN972,getDouble(line,1439,17))  // Deducc. para incentivar determ.actividades - 2013 Alicante 2011 - Deducción pendiente/generada [972]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN973,getDouble(line,1456,17))  // Deducc. para incentivar determ.actividades - 2013 Alicante 2011 - Aplicado en esta liquidación [973]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN975,getDouble(line,1473,17))  // Deducc. para incentivar determ.actividades - 2013 Alicante 2011 - Pendiente de aplicación en periodos futuros [975]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN540,getDouble(line,1490,17))  // Deducc. para incentivar determ.actividades - 2013 Mundobasket 2014 - Deducción pendiente/generada [540]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN541,getDouble(line,1507,17))  // Deducc. para incentivar determ.actividades - 2013 Mundobasket 2014 - Aplicado en esta liquidación [541]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN542,getDouble(line,1524,17))  // Deducc. para incentivar determ.actividades - 2013 Mundobasket 2014 - Pendiente de aplicación en periodos futuros [542]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN543,getDouble(line,1541,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato Mundo Balonmano Masculino 2013 - Deducción pendiente/generada [543]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN544,getDouble(line,1558,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato Mundo Balonmano Masculino 2013 - Aplicado en esta liquidación [544]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN545,getDouble(line,1575,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato Mundo Balonmano Masculino 2013 - Pendiente de aplicación en periodos futuros [545]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN901,getDouble(line,1592,17))  // Deducc. para incentivar determ.actividades - 2013 IV Centenario del fallecimiento de El Greco - Deducción pendiente/generada [901]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN902,getDouble(line,1609,17))  // Deducc. para incentivar determ.actividades - 2013 IV Centenario del fallecimiento de El Greco - Aplicado en esta liquidación [902]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN903,getDouble(line,1626,17))  // Deducc. para incentivar determ.actividades - 2013 IV Centenario del fallecimiento de El Greco - Pendiente de aplicación en periodos futuros [903]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN063,getDouble(line,1643,17))  // Deducc. para  incentivar determ.actividades - 2013 Vitoria-Gasteiz Capital Verde Europea 2012 - Deducción pendiente/generada [063]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN064,getDouble(line,1660,17))  // Deducc. para incentivar determ.actividades - 2013 Vitoria-Gasteiz Capital Verde Europea 2012 - Aplicado en esta liquidación [064]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN065,getDouble(line,1677,17))  // Deducc. para incentivar determ.actividades - 2013 Vitoria-Gasteiz Capital Verde Europea 2012 - Pendiente de aplicación en periodos futuros [065]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN067,getDouble(line,1694,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo de Vela Santander 2014 - Deducción pendiente/generada [067]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN068,getDouble(line,1711,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo de Vela Santander 2014 - Aplicado en esta liquidación [068]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN069,getDouble(line,1728,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo de Vela Santander 2014  - Pendiente de aplicación en periodos futuros [069]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN070,getDouble(line,1745,17))  // Deducc. para incentivar determ.actividades - 2013 Programa "El árbol es vida" - Deducción pendiente/generada [070]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN072,getDouble(line,1762,17))  // Deducc. para incentivar determ.actividades - 2013 Programa "El árbol es vida" - Aplicado en esta liquidación [072]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN073,getDouble(line,1779,17))  // Deducc. para incentivar determ.actividades - 2013 Programa "El árbol es vida" - Pendiente de aplicación en periodos futuros [073]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN075,getDouble(line,1796,17))  // Deducc. para incentivar determ.actividades - 2013 Año de España en Japón - Deducción pendiente/generada [075]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN076,getDouble(line,1813,17))  // Deducc. para incentivar determ.actividades - 2013 Año de España en Japón - Aplicado en esta liquidación [076]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN077,getDouble(line,1830,17))  // Deducc. para incentivar determ.actividades - 2013 Año de España en Japón - Pendiente de aplicación en periodos futuros [077]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN078,getDouble(line,1847,17))  // Deducc. para incentivar determ.actividades - 2013 Plan Director recuperación Patimonio Cultural Lorca - Deducción pendiente/generada [078]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN079,getDouble(line,1864,17))  // Deducc. para incentivar determ.actividades - 2013 Plan Director recuperación Patimonio Cultural Lorca - Aplicado en esta liquidación [079]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN080,getDouble(line,1881,17))  // Deducc. para incentivar determ.actividades - 2013 Plan Director recuperación Patimonio Cultural Lorca - Pendiente de aplicación en periodos futuros [080]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN085,getDouble(line,1898,17))  // Deducc. para incentivar determ.actividades - 2013 Universiada de Invierno Granada 2015 - Deducción pendiente/generada [085]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN086,getDouble(line,1915,17))  // Deducc. para incentivar determ.actividades - 2013 Universiada de Invierno Granada 2015 - Aplicado en esta liquidación [086]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN087,getDouble(line,1932,17))  // Deducc. para incentivar determ.actividades - 2013 Universiada de Invierno Granada 2015 - Pendiente de aplicación en periodos futuros [087]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN093,getDouble(line,1949,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo de Ciclismo en Carretera Ponferrada 2014 - Deducción pendiente/generada [093]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN057,getDouble(line,1966,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo de Ciclismo en Carretera Ponferrada 2014 - Aplicado en esta liquidación [057]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN058,getDouble(line,1983,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo de Ciclismo en Carretera Ponferrada 2014 - Pendiente de aplicación en periodos futuros [058]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN207,getDouble(line,2000,17))  // Deducc. para incentivar determ.actividades - 2013 Barcelona World Jumping Challenge - Deducción pendiente/generada [207]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN208,getDouble(line,2017,17))  // Deducc. para incentivar determ.actividades - 2013 Barcelona World Jumping Challenge - Aplicado en esta liquidación [208]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN209,getDouble(line,2034,17))  // Deducc. para incentivar determ.actividades - 2013 Barcelona World Jumping Challenge - Pendiente de aplicación en periodos futuros [209]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN210,getDouble(line,2051,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo Natación Barcelona 2013 - Deducción pendiente/generada [210]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN211,getDouble(line,2068,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo Natación Barcelona 2013 - Aplicado en esta liquidación [211]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN212,getDouble(line,2085,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo Natación Barcelona 2013 - Pendiente de aplicación en periodos futuros [212]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN213,getDouble(line,2102,17))  // Deducc. para incentivar determ.actividades - 2013 Barcelona Mobile World Capital - Deducción pendiente/generada [213]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN214,getDouble(line,2119,17))  // Deducc. para incentivar determ.actividades - 2013 Barcelona Mobile World Capital - Aplicado en esta liquidación [214]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN215,getDouble(line,2136,17))  // Deducc. para incentivar determ.actividades - 2013 Barcelona Mobile World Capital - Pendiente de aplicación en periodos futuros [215]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN216,getDouble(line,2153,17))  // Deducc. para incentivar determ.actividades - 2013 3ª Edición Barcelona World Race - Deducción pendiente/generada [216]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN217,getDouble(line,2170,17))  // Deducc. para incentivar determ.actividades - 2013 3ª Edición Barcelona World Race - Aplicado en esta liquidación [217]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN218,getDouble(line,2187,17))  // Deducc. para incentivar determ.actividades - 2013 3ª Edición Barcelona World Race - Pendiente de aplicación en periodos futuros [218]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN222,getDouble(line,2204,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo Tiro Olímpico "Las Gabias" - Deducción pendiente/generada [222]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN223,getDouble(line,2221,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo Tiro Olímpico "Las Gabias" - Aplicado en esta liquidación [223]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN224,getDouble(line,2238,17))  // Deducc. para incentivar determ.actividades - 2013 Campeonato del Mundo Tiro Olímpico "Las Gabias" - Pendiente de aplicación en periodos futuros [224]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN240,getDouble(line,2255,17))  // Deducc. para incentivar determ.actividades - 2013 Año Santo Jubilar Mariano 2012-2013 en Almonte - Deducción pendiente/generada [240]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN241,getDouble(line,2272,17))  // Deducc. para incentivar determ.actividades - 2013 Año Santo Jubilar Mariano 2012-2013 en Almonte  - Aplicado en esta liquidación [241]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN242,getDouble(line,2289,17))  // Deducc. para incentivar determ.actividades - 2013 Año Santo Jubilar Mariano 2012-2013 en Almonte  - Pendiente de aplicación en periodos futuros [242]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN243,getDouble(line,2306,17))  // Deducc. para incentivar determ.actividades - 2013 2014 Año Internacional Dieta Mediterránea - Deducción pendiente/generada [243]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN244,getDouble(line,2323,17))  // Deducc. para incentivar determ.actividades - 2013 2014 Año Internacional Dieta Mediterránea  - Aplicado en esta liquidación [244]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN245,getDouble(line,2340,17))  // Deducc. para incentivar determ.actividades - 2013 2014 Año Internacional Dieta Mediterránea  - Pendiente de aplicación en periodos futuros [245]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN246,getDouble(line,2357,17))  // Deducc. para incentivar determ.actividades - 2013 Candidatura Madrid 2020 - Deducción pendiente/generada [246]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN247,getDouble(line,2374,17))  // Deducc. para incentivar determ.actividades - 2013 Candidatura Madrid 2020  - Aplicado en esta liquidación [247]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN248,getDouble(line,2391,17))  // Deducc. para incentivar determ.actividades - 2013 Candidatura Madrid 2020  - Pendiente de aplicación en periodos futuros [248]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN204,getDouble(line,2408,17))  // Deducc. para incentivar determ.actividades - 2013 Programa preparación deportistas españoles juegos "Río de Janeiro 2016"  - Deducción pendiente/generada [204]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN205,getDouble(line,2425,17))  // Deducc. para  incentivar determ.actividades - 2013 Programa preparación deportistas españoles juegos "Río de Janeiro 2016"  - Aplicado en esta liquidación [205]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN206,getDouble(line,2442,17))  // Deducc. para incentivar determ.actividades - 2013 Programa preparación deportistas españoles juegos "Río de Janeiro 2016"  - Pendiente de aplicación en periodos futuros [206]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN219,getDouble(line,2459,17))  // Deducc. para incentivar determ.actividades - 2013 VIII Centenario Peregrinación San Francisco de Asís a Santiago de Compostela  - Deducción pendiente/generada [219]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN220,getDouble(line,2476,17))  // Deducc. para incentivar determ.actividades - 2013  VIII Centenario Peregrinación San Francisco de Asís a Santiago de Compostela  - Aplicado en esta liquidación [220]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN221,getDouble(line,2493,17))  // Deducc. para incentivar determ.actividades - 2013  VIII Centenario Peregrinación San Francisco de Asís a Santiago de Compostela  - Pendiente de aplicación en periodos futuros [221]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN228,getDouble(line,2510,17))  // Deducc. para incentivar determ.actividades - 2013 V Centenario del Nacimiento Santa Teresa Avila 2015  - Deducción pendiente/generada [228] 
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN229,getDouble(line,2527,17))  // Deducc. para incentivar determ.actividades - 2013  V Centenario del Nacimiento Santa Teresa Avila 2015  - Aplicado en esta liquidación [229]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN230,getDouble(line,2544,17))  // Deducc. para incentivar determ.actividades - 2013  V Centenario del Nacimiento Santa Teresa Avila 2015  - Pendiente de aplicación en periodos futuros [230]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN231,getDouble(line,2561,17))  // Deducc. para incentivar determ.actividades - 2013 Año Junipero Serra 2013 - Deducción pendiente/generada [231]  
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN232,getDouble(line,2578,17))  // Deducc. para incentivar determ.actividades - 2013 Año Junipero Serra 2013 - Aplicado en esta liquidación [232]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN233,getDouble(line,2595,17))  // Deducc. para incentivar determ.actividades - 2013 Año Junipero Serra 2013   - Pendiente de aplicación en periodos futuros [233]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN234,getDouble(line,2612,17))  // Deducc. para  incentivar determ.actividades - 2013 Año Santo Jubilar Mariano a celebrar ciudad de Sevilla - Deducción pendiente/generada [234]  
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN235,getDouble(line,2629,17))  // Deducc. para  incentivar determ.actividades - 2013 Año Santo Jubilar Mariano a celebrar ciudad de Sevilla - Aplicado en esta liquidación [235]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN236,getDouble(line,2646,17))  // Deducc. para  incentivar determ.actividades - 2013 Año Santo Jubilar Mariano a celebrar ciudad de Sevilla - Pendiente de aplicación en periodos futuros [236]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN237,getDouble(line,2663,17))  // Deducc. para incentivar determ.actividades - 2013 Vuelta al mundo a vela Alicante 2014 - Deducción pendiente/generada [237]  
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN238,getDouble(line,2680,17))  // Deducc. para incentivar determ.actividades - 2013 Vuelta al mundo a vela Alicante 2014 - Aplicado en esta liquidación [238]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN239,getDouble(line,2697,17))  // Deducc. para incentivar determ.actividades - 2013 Vuelta al mundo a vela Alicante 2014 - Pendiente de aplicación en periodos futuros [239]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN828,getDouble(line,2714,17))  // Deducc. para incentivar determ.actividades - 2013 Diferimiento Deducciones - Deducción pendiente/generada [828]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN829,getDouble(line,2731,17))  // Deducc. para incentivar determ.actividades - 2013 Diferimiento deducciones - Aplicado en esta liquidación [829]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN830,getDouble(line,2748,17))  // Deducc. para incentivar determ.actividades - 2013 Diferimiento deducciones - Pendiente de aplicación en periodos futuros [830]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN634,getDouble(line,2765,17))  // Deducc. para incentivar determ.actividades - Total deducciones programas apoyo acontecimientos de excepcional interés público - Deducción pendiente/generada [634]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN635,getDouble(line,2782,17))  // Deducc. para incentivar determ.actividades - Total deducciones programas apoyo acontecimientos de excepcional interés público - Aplicado en esta liquidación [635]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN636,getDouble(line,2799,17))  // Deducc. para incentivar determ.actividades - Total deducciones programas apoyo acontecimientos de excepcional interés público - Pendiente de aplicación en periodos futuros [636]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN831,getDouble(line,2816,17))  // Deducc. para incentivar determ.actividades - Total deducciones Cap.IV Tít.VI - Deducción pendiente/generada [831]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN588,getDouble(line,2833,17))  // Deducc. para incentivar determ.actividades - Total deducciones Cap.IV Tít.VI - Aplicado en esta liquidación [588]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN832,getDouble(line,2850,17))  // Deducc. para incentivar determ.actividades - Total deducciones Cap.IV Tít.VI - Pendiente de aplicación en periodos futuros [832]
			})
			
		,PAG18 ("T200180", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN918,getDouble(line,  11,17))  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Deducción generada [918]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN919,getDouble(line,  28,17))  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Deducción reducida [919]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN574,getDouble(line,  45,17))  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Importe deducible en cuota [574]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN580,getDouble(line,  62,17))  // Deducciones I+D+i excluidas de límite - 2013 Investigación y desarrollo - Pendiente insuficiencia cuota [580]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN589,getDouble(line,  79,17))  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Deducción generada [589]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN976,getDouble(line,  96,17))  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Deducción reducida [976]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN977,getDouble(line, 113,17))  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Importe deducible en cuota [977]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN978,getDouble(line, 130,17))  // Deducciones I+D+i excluidas de límite - 2013 Innovación tecnológica - Pendiente insuficiencia cuota [978]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN517,getDouble(line, 147,17))  // Deducciones I+D+i excluidas de límite - Total - Deducción generada [517]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN081,getDouble(line, 164,17))  // Deducciones I+D+i excluidas de límite - Total - Deducción reducida [081]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN082,getDouble(line, 181,17))  // Deducciones I+D+i excluidas de límite - Total - Importe deducible en cuota [082]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN083,getDouble(line, 198,17))  // Deducciones I+D+i excluidas de límite - Total - Pendiente insuficiencia cuota [083]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN929,getDouble(line, 215,17))  // Deducción donativos entidades sin fines lucro - 2003 - Deducción pendiente/generada [929]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN930,getDouble(line, 232,17))  // Deducción donativos entidades sin fines lucro - 2003 - Aplicado en esta liquidación [930]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN942,getDouble(line, 249,17))  // Deducción donativos entidades sin fines lucro - 2004 - Deducción pendiente/generada [942]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN943,getDouble(line, 266,17))  // Deducción donativos entidades sin fines lucro - 2004 - Aplicado en esta liquidación [943]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN944,getDouble(line, 283,17))  // Deducción donativos entidades sin fines lucro - 2004 - Pendiente de aplicación en periodos futuros [944]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN294,getDouble(line, 300,17))  // Deducción donativos entidades sin fines lucro - 2005 - Deducción pendiente/generada [294]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN295,getDouble(line, 317,17))  // Deducción donativos entidades sin fines lucro - 2005 - Aplicado en esta liquidación [295]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN296,getDouble(line, 334,17))  // Deducción donativos entidades sin fines lucro - 2005 - Pendiente de aplicación en periodos futuros [296]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN066,getDouble(line, 351,17))  // Deducción donativos entidades sin fines lucro - 2006 - Deducción pendiente/generada [066]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN074,getDouble(line, 368,17))  // Deducción donativos entidades sin fines lucro - 2006 - Aplicado en esta liquidación [074]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN084,getDouble(line, 385,17))  // Deducción donativos entidades sin fines lucro - 2006 - Pendiente de aplicación en periodos futuros [084]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN008,getDouble(line, 402,17))  // Deducción donativos entidades sin fines lucro - 2007 - Deducción pendiente/generada [008]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN009,getDouble(line, 419,17))  // Deducción donativos entidades sin fines lucro - 2007 - Aplicado en esta liquidación [009]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN010,getDouble(line, 436,17))  // Deducción donativos entidades sin fines lucro - 2007 - Pendiente de aplicación en periodos futuros [010]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN034,getDouble(line, 453,17))  // Deducción donativos entidades sin fines lucro - 2008 - Deducción pendiente/generada [034]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN035,getDouble(line, 470,17))  // Deducción donativos entidades sin fines lucro - 2008 - Aplicado en esta liquidación [035]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN036,getDouble(line, 487,17))  // Deducción donativos entidades sin fines lucro - 2008 - Pendiente de aplicación en periodos futuros [036]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN201,getDouble(line, 504,17))  // Deducción donativos entidades sin fines lucro - 2009 - Deducción pendiente/generada [201]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN202,getDouble(line, 521,17))  // Deducción donativos entidades sin fines lucro - 2009 - Aplicado en esta liquidación [202]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN203,getDouble(line, 538,17))  // Deducción donativos entidades sin fines lucro - 2009 - Pendiente de aplicación en periodos futuros [203]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN904,getDouble(line, 555,17))  // Deducción donativos entidades sin fines lucro - 2010 - Deducción pendiente/generada [904]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN905,getDouble(line, 572,17))  // Deducción donativos entidades sin fines lucro - 2010 - Aplicado en esta liquidación [905]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN906,getDouble(line, 589,17))  // Deducción donativos entidades sin fines lucro - 2010 - Pendiente de aplicación en periodos futuros [906]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN990,getDouble(line, 606,17))  // Deducción donativos entidades sin fines lucro - 2011 - Deducción pendiente/generada [990]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN991,getDouble(line, 623,17))  // Deducción donativos entidades sin fines lucro - 2011 - Aplicado en esta liquidación [991]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN992,getDouble(line, 640,17))  // Deducción donativos entidades sin fines lucro - 2011 - Pendiente de aplicación en periodos futuros [992]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN997,getDouble(line, 657,17))  // Deducción donativos entidades sin fines lucro - 2012 - Deducción pendiente/generada [997]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN998,getDouble(line, 674,17))  // Deducción donativos entidades sin fines lucro - 2012 - Aplicado en esta liquidación [998]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN999,getDouble(line, 691,17))  // Deducción donativos entidades sin fines lucro - 2012 - Pendiente de aplicación en periodos futuros [999]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN993,getDouble(line, 708,17))  // Deducción donativos entidades sin fines lucro - 2013 - Deducción pendiente/generada [993]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN994,getDouble(line, 725,17))  // Deducción donativos entidades sin fines lucro - 2013 - Aplicado en esta liquidación [994]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN995,getDouble(line, 742,17))  // Deducción donativos entidades sin fines lucro - 2013 - Pendiente de aplicación en periodos futuros [995]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN598,getDouble(line, 759,17))  // Deducción donativos entidades sin fines lucro - Total deducciones donaciones a entidades sin fines lucro - Deducción pendiente/generada [598]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN565,getDouble(line, 776,17))  // Deducción donativos entidades sin fines lucro - Total deducciones donaciones a entidades sin fines lucro - Aplicado en esta liquidación [565]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN895,getDouble(line, 793,17))  // Deducción donativos entidades sin fines lucro - Total deducciones donaciones a entidades sin fines lucro - Pendiente de aplicación en periodos futuros [895]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.BN974,getDouble(line, 810,17))  // Deducción donativos entidades sin fines lucro - Donaciones del período impositivo efectuadas a entidades sin fines de lucro [974]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID650,getDouble(line, 827,17))  // Aplicación de resultados - Base de reparto - Pérdidas y ganancias [650]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID651,getDouble(line, 844,17))  // Aplicación de resultados - Base de reparto - Remanente [651]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID652,getDouble(line, 861,17))  // Aplicación de resultados - Base de reparto - Reservas [652]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID653,getDouble(line, 878,17))  // Aplicación de resultados - Base de reparto - Total [653]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID654,getDouble(line, 895,17))  // Aplicación de resultados - Aplicación - A reservas [654]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID655,getDouble(line, 912,17))  // Aplicación de resultados - Aplicación - Intereses aportaciones al capital (Cooperativas) [655]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID656,getDouble(line, 929,17))  // Aplicación de resultados - Aplicación - A dividendos [656]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID658,getDouble(line, 946,17))  // Aplicación de resultados - Aplicación - A dotación O.S. (Cajas de ahorro) [658]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID659,getDouble(line, 963,17))  // Aplicación de resultados - Aplicación - A F.R.O y dotaciones voluntarias al F.E.P (Cooperativas) [659]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID660,getDouble(line, 980,17))  // Aplicación de resultados - Aplicación - A retornos cooperativos (Cooperativas) [660]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID662,getDouble(line, 997,17))  // Aplicación de resultados - Aplicación - Partícipes (IIC) [662]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID664,getDouble(line,1014,17))  // Aplicación de resultados - Aplicación - A remanente y otros [664]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID665,getDouble(line,1031,17))  // Aplicación de resultados - Aplicación - A compensación de pérdidas de ejercicios anteriores [665]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.ID666,getDouble(line,1048,17))  // Aplicación de resultados - Aplicación - Total [666]
			
			// [...] NO ESTA EN EL MODELO - Detalle de las correcciones al resultado de la cuenta de perdidas y ganancias
			
			})
			
		,PAG18B ("T20018B", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM175,getDouble(line, 11,17))  // Limitación deducibilidad gastos financieros - a) Resultado explotación [175]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM176,getDouble(line, 28,17))  // Limitación deducibilidad gastos financieros - b) Amortización del inmovilizado [176]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM177,getDouble(line, 45,17))  // Limitación deducibilidad gastos financieros - c) Imputación subvenciones inmovilizado no financiero y otras [177]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM178,getDouble(line, 62,17))  // Limitación deducibilidad gastos financieros - d) Deterioro y resultado enajenaciones inmovilizado [178]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM179,getDouble(line, 79,17))  // Limitación deducibilidad gastos financieros - e) Ingresos financieros participaciones instrumentos de patrimonio [179]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM043,getDouble(line, 96,17))  // Limitación deducibilidad gastos financieros - f) Límite deducción gastos financieros netos [043]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM049,getDouble(line,113,17))  // Limitación deducibilidad gastos financieros - g) Adición por límite beneficio operativo no aplicado en cinco ejercicios anteriores [049]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM249,getDouble(line,130,17))  // Limitación deducibilidad gastos financieros - h) Gastos financieros periodo impositivo excluidos art. 14.1.h) LIS [249]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM252,getDouble(line,147,17))  // Limitación deducibilidad gastos financieros - i) Ingresos financieros periodo impositivo derivados cesión terceros de capitales propios [252]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM253,getDouble(line,164,17))  // Limitación deducibilidad gastos financieros - j) Gastos financieros netos del periodo [253]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM254,getDouble(line,181,17))  // Limitación deducibilidad gastos financieros - k) Gastos financieros netos del periodo deducibles [254]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM255,getDouble(line,198,17))  // Limitación deducibilidad gastos financieros - l) Gastos financieros netos del periodo no deducibles [255]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM258,getDouble(line,215,17))  // Limitación deducibilidad gastos financieros - m) Gastos financieros netos pendientes deducir de periodos anteriores aplicados [258]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM259,getDouble(line,232,17))  // Limitación deducibilidad gastos financieros - n) Total gastos financieros netos deducibles en el periodo [259]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM260,getDouble(line,249,17))  // Limitación deducibilidad gastos financieros - ñ) Total gastos financieros deducibles en el periodo [260]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM969,getDouble(line,266,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2012 - Pendiente aplicación a principio del período [969]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM970,getDouble(line,283,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2012 - Aplicado en esta liquidación [970]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM971,getDouble(line,300,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2012 - Pendiente aplicación períodos futuros  [971]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM261,getDouble(line,317,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013(*) - Pendiente aplicación a principio del período [261]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM262,getDouble(line,334,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013(*) - Aplicado en esta liquidación [262]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM263,getDouble(line,351,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013(*) - Pendiente aplicación períodos futuros  [263]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM264,getDouble(line,368,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013(**) - Pendiente aplicación a principio del período [264]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM265,getDouble(line,385,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013(**) - Aplicado en esta liquidación [265]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM266,getDouble(line,402,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Ejercicio generación 2013(**) - Pendiente aplicación períodos futuros  [266]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM267,getDouble(line,419,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Total - Pendiente aplicación a principio del período [267]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM268,getDouble(line,436,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Total - Aplicado en esta liquidación [268]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM269,getDouble(line,453,17))  // Limitación deducibilidad gastos financieros, gastos financieros pendientes deducir - Total - Pendiente aplicación períodos futuros  [269]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM503,getDouble(line,470,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2012 - Pendiente aplicación a principio del período [503]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM522,getDouble(line,487,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2012 - Aplicado en esta liquidación [522]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM523,getDouble(line,504,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2012 - Pendiente aplicación períodos futuros  [523]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM270,getDouble(line,521,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013(*) - Pendiente aplicación a principio del período [270]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM271,getDouble(line,538,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013(*) - Aplicado en esta liquidación [271]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM272,getDouble(line,555,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013(*) - Pendiente aplicación períodos futuros  [272]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM273,getDouble(line,572,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013(**) - Pendiente aplicación a principio del período [273]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM274,getDouble(line,589,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013(**) - Aplicado en esta liquidación [274]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM537,getDouble(line,606,17))  // Pendiente adición por límite beneficio operativo no aplicado - Ejercicio generación 2013(**) - Pendiente aplicación períodos futuros  [537]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM538,getDouble(line,623,17))  // Pendiente adición por límite beneficio operativo no aplicado - Total - Pendiente aplicación a principio del período [538]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM539,getDouble(line,640,17))  // Pendiente adición por límite beneficio operativo no aplicado - Total - Aplicado en esta liquidación [539]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM546,getDouble(line,657,17))  // Pendiente adición por límite beneficio operativo no aplicado - Total - Pendiente aplicación períodos futuros  [546]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM173,getDouble(line,674,17))  // Dotaciones deterioro créditos u otros activos - Ejercicio generarción 2011 - Ingresado en esta liquidación [173]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM227,getDouble(line,691,17))  // Dotaciones deterioro créditos u otros activos - Ejercicio generarción 2012 - Ingresado en esta liquidación [227]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM291,getDouble(line,708,17))  // Dotaciones deterioro créditos u otros activos - Ejercicio generarción 2013 (*) - Ingresado en esta liquidación [291]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM344,getDouble(line,725,17))  // Dotaciones deterioro créditos u otros activos - Total - Ingresado en esta liquidación [344]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LM393,getDouble(line,742,17))  // Dotaciones deterioro créditos u otros activos - Conversión activos impuesto diferido - Importe crédito exigible [393]
			
			// [...] NO ESTA EN EL MODELO - Opciones Conversión de activos por imputesto diferido... 
			
			})		
			
		,PAG19 ("T200190", new IPropertyFiller[] {
				
			// [...] NO ESTA EN EL MODELO - Operaciones y situaciones relacionadas con paises y territorios fiscales 
				
			// [...] NO ESTA EN EL MODELO - Comunicación del importe neto de la cifra de negocios
				
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ0N1,getInt(line,839, 4))    // Rég. Entidades navieras en función del tonelaje. Nº de buques  [N1]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ630,getDouble(line,843,17)) // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de  aplicar la escala [630]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ631,getDouble(line,860,17)) // Rég. Entidades navieras en función del tonelaje. Importe rentas generadas en trasmisiones de buques [631]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ632,getDouble(line,877,17)) // Rég. Entidades navieras en función del tonelaje. Compensación bases imponibles negativas períodos anteriores [632]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ579,getDouble(line,894,17)) // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de la aplicación del régimen [579]				
			})		
		
		,PAG20 ("T200200", new IPropertyFiller[] {
				
			// [...] NO ESTA EN EL MODELO - Operaciones con personas o entidades vinculadas 
			
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C1,getDouble(line, 497,17)) // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados cooperativos [C1]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E1,getDouble(line, 514,17)) // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados extracooperativos [E1]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C2,getDouble(line, 531,17)) // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados cooperativos [C2]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E2,getDouble(line, 548,17)) // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados extracooperativos [E2]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C3,getDouble(line, 565,17)) // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados cooperativos [C3]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E3,getDouble(line, 582,17)) // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados extracooperativos [E3]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C4,getDouble(line, 599,17)) // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados cooperativos [C4]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E4,getDouble(line, 616,17)) // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados extracooperativos [E4]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E5,getDouble(line, 633,17)) // Rég. cooperativas - Determ. base imponible - Incrementos y disminuciones patrimoniales - Resultados extracooperativos [E5]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C6,getDouble(line, 650,17)) // Rég. cooperativas - Determ. base imponible - resultado - Resultados cooperativos [C6]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E6,getDouble(line, 667,17)) // Rég. cooperativas - Determ. base imponible - resultado - Resultados extracooperativos [E6]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C7,getDouble(line, 684,17)) // Rég. cooperativas - Determ. base imponible - aumentos - Resultados cooperativos [C7]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E7,getDouble(line, 701,17)) // Rég. cooperativas - Determ. base imponible - aumentos - Resultados extracooperativos [E7]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C8,getDouble(line, 718,17)) // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados cooperativos [C8]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E8,getDouble(line, 735,17)) // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados extracooperativos [E8]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0C9,getDouble(line, 752,17)) // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados cooperativos [C9]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CP0E9,getDouble(line, 769,17)) // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados extracooperativos [E9]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CPC10,getDouble(line, 786,17)) // Rég. cooperativas - Determ. base imponible - Reserva inversiones Canarias - Resultados cooperativos [C10]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CPC11,getDouble(line, 803,17)) // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados cooperativos [C11]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CPE11,getDouble(line, 820,17)) // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados extracooperativos [E11]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CPC12,getDouble(line, 837,17)) // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados cooperativos [553]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.CPE12,getDouble(line, 854,17)) // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados extracooperativos [554]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ673,getDouble(line, 871,17)) // Rég. cooperativas - Detalle compensación cuotas. 1998 Pendiente aplicación al principio del periodo [673]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ674,getDouble(line, 888,17)) // Rég. cooperativas - Detalle compensación cuotas. 1998 Aplicado en esta liquidación [674]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ676,getDouble(line, 905,17)) // Rég. cooperativas - Detalle compensación cuotas. 1999 Pendiente aplicación al principio del periodo [676]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ677,getDouble(line, 922,17)) // Rég. cooperativas - Detalle compensación cuotas. 1999 Aplicado en esta liquidación [677]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ678,getDouble(line, 939,17)) // Rég. cooperativas - Detalle compensación cuotas. 1999 Pendiente aplicación en ejercicios futuros [678]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ679,getDouble(line, 956,17)) // Rég. cooperativas - Detalle compensación cuotas. 2000 Pendiente aplicación al principio del periodo [679]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ680,getDouble(line, 973,17)) // Rég. cooperativas - Detalle compensación cuotas. 2000 Aplicado en esta liquidación [680]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ681,getDouble(line, 990,17)) // Rég. cooperativas - Detalle compensación cuotas. 2000 Pendiente aplicación en ejercicios futuros [681]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ682,getDouble(line,1007,17)) // Rég. cooperativas - Detalle compensación cuotas. 2001 Pendiente aplicación al principio del periodo [682]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ683,getDouble(line,1024,17)) // Rég. cooperativas - Detalle compensación cuotas. 2001 Aplicado en esta liquidación [683]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ684,getDouble(line,1041,17)) // Rég. cooperativas - Detalle compensación cuotas. 2001 Pendiente aplicación en ejercicios futuros [684]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ685,getDouble(line,1058,17)) // Rég. cooperativas - Detalle compensación cuotas. 2002 Pendiente aplicación al principio del periodo [685]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ686,getDouble(line,1075,17)) // Rég. cooperativas - Detalle compensación cuotas. 2002 Aplicado en esta liquidación [686]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ687,getDouble(line,1092,17)) // Rég. cooperativas - Detalle compensación cuotas. 2002 Pendiente aplicación en ejercicios futuros [687]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ688,getDouble(line,1109,17)) // Rég. cooperativas - Detalle compensación cuotas. 2003 Pendiente aplicación al principio del periodo [688]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ689,getDouble(line,1126,17)) // Rég. cooperativas - Detalle compensación cuotas. 2003 Aplicado en esta liquidación [689]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ690,getDouble(line,1143,17)) // Rég. cooperativas - Detalle compensación cuotas. 2003 Pendiente aplicación en ejercicios futuros [690]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ691,getDouble(line,1160,17)) // Rég. cooperativas - Detalle compensación cuotas. 2004 Pendiente aplicación al principio del periodo [691]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ692,getDouble(line,1177,17)) // Rég. cooperativas - Detalle compensación cuotas. 2004 Aplicado en esta liquidación [692]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ693,getDouble(line,1194,17)) // Rég. cooperativas - Detalle compensación cuotas. 2004 Pendiente aplicación en ejercicios futuros [693]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ623,getDouble(line,1211,17)) // Rég. cooperativas - Detalle compensación cuotas. 2005 Pendiente aplicación al principio del periodo [623]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ624,getDouble(line,1228,17)) // Rég. cooperativas - Detalle compensación cuotas. 2005 Aplicado en esta liquidación [624]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ672,getDouble(line,1245,17)) // Rég. cooperativas - Detalle compensación cuotas. 2005 Pendiente aplicación en ejercicios futuros [672]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ279,getDouble(line,1262,17)) // Rég. cooperativas - Detalle compensación cuotas. 2006 Pendiente aplicación al principio del periodo [279]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ280,getDouble(line,1279,17)) // Rég. cooperativas - Detalle compensación cuotas. 2006 Aplicado en esta liquidación [280]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ281,getDouble(line,1296,17)) // Rég. cooperativas - Detalle compensación cuotas. 2006 Pendiente aplicación en ejercicios futuros [281]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ587,getDouble(line,1313,17)) // Rég. cooperativas - Detalle compensación cuotas. 2007 Pendiente aplicación al principio del periodo [587]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ515,getDouble(line,1330,17)) // Rég. cooperativas - Detalle compensación cuotas. 2007 Aplicado en esta liquidación [515]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ900,getDouble(line,1347,17)) // Rég. cooperativas - Detalle compensación cuotas. 2007 Pendiente aplicación en ejercicios futuros [900]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ059,getDouble(line,1364,17)) // Rég. cooperativas - Detalle compensación cuotas. 2008 Pendiente aplicación al principio del periodo [059]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ099,getDouble(line,1381,17)) // Rég. cooperativas - Detalle compensación cuotas. 2008 Aplicado en esta liquidación [099]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ100,getDouble(line,1398,17)) // Rég. cooperativas - Detalle compensación cuotas. 2008 Pendiente aplicación en ejercicios futuros [100]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ017,getDouble(line,1415,17)) // Rég. cooperativas - Detalle compensación cuotas. 2009 Pendiente aplicación al principio del periodo [017]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ018,getDouble(line,1432,17)) // Rég. cooperativas - Detalle compensación cuotas. 2009 Aplicado en esta liquidación [018]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ019,getDouble(line,1449,17)) // Rég. cooperativas - Detalle compensación cuotas. 2009 Pendiente aplicación en ejercicios futuros [019]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ772,getDouble(line,1466,17)) // Rég. cooperativas - Detalle compensación cuotas. 2010 Pendiente aplicación al principio del periodo [772]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ773,getDouble(line,1483,17)) // Rég. cooperativas - Detalle compensación cuotas. 2010 Aplicado en esta liquidación [773]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ777,getDouble(line,1500,17)) // Rég. cooperativas - Detalle compensación cuotas. 2010 Pendiente aplicación en ejercicios futuros [777]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ907,getDouble(line,1517,17)) // Rég. cooperativas - Detalle compensación cuotas. 2011 Pendiente aplicación al principio del periodo [907]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ908,getDouble(line,1534,17)) // Rég. cooperativas - Detalle compensación cuotas. 2011 Aplicado en esta liquidación [908]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ909,getDouble(line,1551,17)) // Rég. cooperativas - Detalle compensación cuotas. 2011 Pendiente aplicación en ejercicios futuros [909]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ910,getDouble(line,1568,17)) // Rég. cooperativas - Detalle compensación cuotas. 2012 Pendiente aplicación al principio del periodo [910]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ911,getDouble(line,1585,17)) // Rég. cooperativas - Detalle compensación cuotas. 2012 Aplicado en esta liquidación [911]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ912,getDouble(line,1602,17)) // Rég. cooperativas - Detalle compensación cuotas. 2012 Pendiente aplicación en ejercicios futuros [912]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ935,getDouble(line,1619,17)) // Rég. cooperativas - Detalle compensación cuotas. 2013 (*) Pendiente aplicación al principio del periodo [935]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ936,getDouble(line,1636,17)) // Rég. cooperativas - Detalle compensación cuotas. 2013 (*) Aplicado en esta liquidación [936]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ937,getDouble(line,1653,17)) // Rég. cooperativas - Detalle compensación cuotas. 2013 (*) Pendiente aplicación en ejercicios futuros [937]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ694,getDouble(line,1670,17)) // Rég. cooperativas - Detalle compensación cuotas. Total. Pendiente aplicación al principio del periodo [694]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ561,getDouble(line,1687,17)) // Rég. cooperativas - Detalle compensación cuotas. Total. Aplicado en esta liquidación [561]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.LQ695,getDouble(line,1704,17)) // Rég. cooperativas - Detalle compensación cuotas. Total. Pendiente aplicación en ejercicios futuros [695]
			})
			
		// [...] NO ESTA EN EL MODELO - Página 21: Operaciones fusión, escisión, canje valores... 
		// [...] NO ESTA EN EL MODELO - Página 22: Agrup. interés económico y UTES 
		// [...] NO ESTA EN EL MODELO - Página 23: Régimen especial de transparencia fiscal internacional

		,PAG24 ("T200240", new IPropertyFiller[] {
			 (line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR050,getDouble(line, 11,17)) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen total de operaciones  [050]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR051,getDouble(line, 28,17)) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en el extranjero [051]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR052,getDouble(line, 45,17)) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Araba [052]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR053,getDouble(line, 62,17)) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Gipuzkoa [053]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR054,getDouble(line, 79,17)) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Bizkaia [054]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR055,getDouble(line, 96,17)) // Tributación conjunta Estado y Adm.Forales - Convenio económico - Volumen operaciones en Navarra [055]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR056,getDouble(line,113,17)) // Tributación conjunta Estado y Adm.Forales - Concierto económico - Volumen operaciones en Territorio común [056]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR626,getDouble(line,130, 5)) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Araba [626]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR627,getDouble(line,135, 5)) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Gipuzkoa [627]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR628,getDouble(line,140, 5)) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Bizkaia [628]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR629,getDouble(line,145, 5)) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Navarra [629]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR625,getDouble(line,150, 5)) // Tributación conjunta Estado y Adm.Forales - Cálculo porcentajes tributación - Admón.del Estado [625]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR420,getDouble(line,155,17)) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Araba [420]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR421,getDouble(line,172,17)) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Gipuzkoa [421]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR426,getDouble(line,189,17)) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Bizkaia [426]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR427,getDouble(line,206,17)) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Navarra [427]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR600,getDouble(line,223,17)) // Tributación conjunta Estado y Adm.Forales - Cuota del ejercicio a ingresar/devolver - Total [600]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR402,getDouble(line,240,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Araba [402]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR442,getDouble(line,257,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Gipuzkoa [442]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR443,getDouble(line,274,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Bizkaia [443]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR444,getDouble(line,291,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Navarra [444]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR602,getDouble(line,308,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 1 - Total [602]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR445,getDouble(line,325,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Araba [445]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR446,getDouble(line,342,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Gipuzkoa [446]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR447,getDouble(line,359,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Bizkaia [447]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR448,getDouble(line,376,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Navarra [448]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR604,getDouble(line,393,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 2 - Total [604]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR449,getDouble(line,410,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Araba [449]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR450,getDouble(line,427,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Gipuzkoa [450]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR451,getDouble(line,444,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Bizkaia [451]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR465,getDouble(line,461,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Navarra [465]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR606,getDouble(line,478,17)) // Tributación conjunta Estado y Adm.Forales - Pagos fraccionados 3 - Total [606]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR474,getDouble(line,495,17)) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Araba [474]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR475,getDouble(line,512,17)) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Gipuzkoa [475]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR476,getDouble(line,529,17)) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Bizkaia [476]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR477,getDouble(line,546,17)) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Navarra [477]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR612,getDouble(line,563,17)) // Tributación conjunta Estado y Adm.Forales - Cuota diferencial - Total [612]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR482,getDouble(line,580,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Araba [482]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR483,getDouble(line,597,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Gipuzkoa [483]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR484,getDouble(line,614,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Bizkaia [484]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR485,getDouble(line,631,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Navarra [485]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR616,getDouble(line,648,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por pérdida beneficios fiscales - Total [616]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR913,getDouble(line,665,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Araba [913]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR914,getDouble(line,682,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Gipuzkoa [914
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR915,getDouble(line,699,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Bizkaia [915]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR916,getDouble(line,716,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Navarra [916]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR642,getDouble(line,733,17)) // Tributación conjunta Estado y Adm.Forales - Incremento por incumplimiento requisitos SOCIMI - Total [642]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR486,getDouble(line,750,17)) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Araba [486]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR487,getDouble(line,767,17)) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Gipuzkoa [487]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR488,getDouble(line,784,17)) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Bizkaia [488]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR489,getDouble(line,801,17)) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Navarra [489]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR618,getDouble(line,818,17)) // Tributación conjunta Estado y Adm.Forales - Intereses demora - Total [618]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR490,getDouble(line,835,17)) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Araba [490]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR491,getDouble(line,852,17)) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Gipuzkoa [491]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR492,getDouble(line,869,17)) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Bizkaia [492]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR493,getDouble(line,886,17)) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Navarra  [493]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR620,getDouble(line,903,17)) // Tributación conjunta Estado y Adm.Forales - Importe  ingreso/devolución declaración originaria - Total [620]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR494,getDouble(line,920,17)) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Araba [494]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR495,getDouble(line,937,17)) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Gipuzkoa [495]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR496,getDouble(line,954,17)) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Bizkaia [496]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR497,getDouble(line,971,17)) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Navarra [497]
			,(line,mod200) ->  setCasilla(mod200,Mod2002013Key.TR622,getDouble(line,988,17)) // Tributación conjunta Estado y Adm.Forales -  Líquido a ingresar o a devolver - Total [622]
			})	
			
		// [...] NO ESTAN EN EL MODELO - Páginas 25 a 52
		
		;
		 
		private String tag;
		private IPropertyFiller[] propertyFillers;
		
		private Paginas (String tag,IPropertyFiller[] propertyFillers) {
			this.tag = tag;
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Document doc, Mod2002013 mod200) {
			// Se recorren todas las páginas obteniendo los datos del doc que se le pasa
			for ( Paginas page : Paginas.values()) {
				NodeList nodeList = doc.getElementsByTagName(page.tag);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					String line = "<" + page.tag + ">" + node.getTextContent();
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(line, mod200);
					}
				}
			}
		}
	}
	
	private static String formatDate(Date d) {
		if (d==null)
			return null;
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(d);			
		}
	}
	
	private static void toString(Mod2002013 mod200) {
		
		System.out.println("PeriodStart...........: " + formatDate(mod200.getPeriodStart()));
		System.out.println("PeriodEnd.............: " + formatDate(mod200.getPeriodEnd()));
		System.out.println("PeriodType............: " + mod200.getPeriodType());
		System.out.println("Cnae .................: " + mod200.getCnae());
		System.out.println("Document..............: " + mod200.getEnterpriseDocument());
		System.out.println("Name..................: " + mod200.getEnterpriseName());
		System.out.println("Phone1................: " + mod200.getEnterprisePhone1());
		System.out.println("Phone2................: " + mod200.getEnterprisePhone2());
		System.out.println("Year..................: " + mod200.getYear());
		System.out.println("BalanceType...........: " + mod200.getBalanceType());
		System.out.println("PygType...............: " + mod200.getPygType());
		System.out.println("FiscalGroup...........: " + mod200.getFiscalGroup());
		System.out.println("DominantDocument......: " + mod200.getDominantDocument());
		System.out.println("Complementary.........: " + mod200.isComplementary());
		System.out.println("ComplementaryReceipt..: " + mod200.getComplementaryReceipt());
		System.out.println("Secretary_Name........: " + mod200.getSecretary().getName());
		System.out.println("Secretary_Document....: " + mod200.getSecretary().getDocument());
		System.out.println("Secretary_Irnr........: " + formatDate(mod200.getSecretary().getIrnr()));
		
		System.out.println();
		System.out.println("-- CLAVES --");
		for (Mod2002013Key key : Mod2002013Key.values()) {			
			if (mod200.getKey(key) != null) {
				System.out.println(AonStringUtils.join(AonStringUtils.SPACE
						,mod200.getKey(key).getKey()
						," ...: "
						,mod200.getKey(key).getValue()));
			}
		}

		System.out.println();
		List<LegalRepresentative> l1 = mod200.getRepresentatives();
		System.out.println("-- REPRESENTANTES -- "+l1.size());		
		for (int i = 0; i < l1.size(); i++) {
			LegalRepresentative lr = l1.get(i);
			System.out.println( AonStringUtils.join(lr.getDocument()
				,AonStringUtils.SPACE
				,lr.getName()
				,AonStringUtils.SPACE
				,formatDate(lr.getNotaryDate()).toString()				
				,AonStringUtils.SPACE
				,lr.getNotary()));
		}
		
		System.out.println();
		List<CompanyAdministrator> l2 = mod200.getAdministrators();
		System.out.println("-- ADMINISTRADORES -- "+l2.size());		
		for (int i = 0; i < l2.size(); i++) {
			CompanyAdministrator ca = l2.get(i);
			System.out.println( AonStringUtils.join(ca.getDocument()
				,AonStringUtils.SPACE
				,ca.isRepresentative()
				,AonStringUtils.SPACE
				,ca.getName()
				,AonStringUtils.SPACE
				,ca.getResidence()
				,AonStringUtils.SPACE
				,ca.getProvince()));
		}
		
		System.out.println();
		List<CompanyParticipation> cpo = mod200.getParticipationsOut();
		System.out.println("-- PARTICIPATIONS OUT -- "+cpo.size());		
		for (int i = 0; i < cpo.size(); i++) {
			CompanyParticipation cp = cpo.get(i);
			System.out.println( AonStringUtils.join( cp.getDocument()
													,AonStringUtils.SPACE
													,cp.getName()
													,AonStringUtils.SPACE
													,cp.getProvince()
													,AonStringUtils.SPACE
													,cp.getPercent()
													,AonStringUtils.SPACE
													,cp.getNominalValue()
													,AonStringUtils.SPACE
													,cp.getBookValue()
													,AonStringUtils.SPACE
													,cp.getIncomes()
													,AonStringUtils.SPACE
													,cp.getaValue()
													,AonStringUtils.SPACE
													,cp.getbValue()
													,AonStringUtils.SPACE
													,cp.getcValue()
													,AonStringUtils.SPACE
													,cp.getdValue()
													,AonStringUtils.SPACE
													,cp.getCapital()
													,AonStringUtils.SPACE
													,cp.getReserve()
													,AonStringUtils.SPACE
													,cp.getOtherAmounts()
													,AonStringUtils.SPACE
													,cp.getResult()));
		}

		System.out.println();
		List<CompanyParticipation> cpi = mod200.getParticipationsIn();
		System.out.println("-- PARTICIPATIONS IN -- "+cpi.size());		
		for (int i = 0; i < cpi.size(); i++) {
			CompanyParticipation cp = cpi.get(i);
			System.out.println( AonStringUtils.join( cp.getDocument()
													,AonStringUtils.SPACE
													,cp.isRepresentative()
													,AonStringUtils.SPACE
													,cp.getName()
													,AonStringUtils.SPACE
													,cp.getProvince()
													,AonStringUtils.SPACE
													,cp.getNominalValue()
													,AonStringUtils.SPACE
													,cp.getPercent()));
		}
		
	}
	
	public static Mod2002013 getMod2002013(InputStream input) {
		try {			
			// Leemos el fichero especificando codificacion ISO-8859-1 pues si no da error si el fichero contiene datos con eñes
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();							
			Document doc = db.parse(new InputSource(new InputStreamReader(input,"ISO-8859-1")));				
			
			// Creamos el objeto Mod200 donde importaremos todos los datos del fichero
			Mod2002013 mod200 = new Mod2002013();
			Paginas.fill(doc, mod200);

			DoubleVariable2013 bv = new DoubleVariable2013( Mod2002013Key.C0050 );
			bv.setValue((mod200.getBalanceType() == BalanceType.NORMAL));
			mod200.addVariable(bv);
			
			bv = new DoubleVariable2013( Mod2002013Key.C0051 );
			bv.setValue((mod200.getBalanceType() == BalanceType.ABREVIADO));
			mod200.addVariable(bv);
			
			bv = new DoubleVariable2013( Mod2002013Key.C0052 );
			bv.setValue((mod200.getBalanceType() == BalanceType.PYMES));
			mod200.addVariable(bv);
				
			bv = new DoubleVariable2013( Mod2002013Key.C0053 );
			bv.setValue((mod200.getBalanceType() == BalanceType.NORMAL));
			mod200.addVariable(bv);
			
			bv = new DoubleVariable2013( Mod2002013Key.C0054 );
			bv.setValue((mod200.getBalanceType() == BalanceType.ABREVIADO));
			mod200.addVariable(bv);
			
			bv = new DoubleVariable2013( Mod2002013Key.C0055 );
			bv.setValue((mod200.getBalanceType() == BalanceType.PYMES));
			mod200.addVariable(bv);
			
			return mod200;
		
		} catch (IOException | ParserConfigurationException | SAXException e) {
			throw new AonCoreException(e);
		}
	}

	public static void main(String argv[]) throws FileNotFoundException {
		
		try {
			String filename = "/AEAT/200/2013";  // directorio por defecto
			
			// Mostrar una ventana de dialogo para seleccionar ficheros
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(filename));
			fc.setMultiSelectionEnabled(true);
            int res = fc.showOpenDialog(new JFrame());
		    
	        if (res == JFileChooser.APPROVE_OPTION) {	        	
	        	for (File file : fc.getSelectedFiles()) {	        	
		        	//filename = fc.getSelectedFile().getAbsolutePath();
	        		filename = file.getAbsolutePath();
	        		System.out.println("***** Inicio Fichero : "+filename);
					System.out.println("");
					InputStream input = new FileInputStream(filename);
					Mod2002013 mod200 = getMod2002013(input);
					if (mod200!=null) toString(mod200);
					if (mod200!=null) System.out.println("OK");
	        		System.out.println("");
					System.out.println("***** Fin Fichero : "+filename);
					System.out.println("");
	        	}
	        }	        
		} catch (Exception e) {
			e.printStackTrace();			
		}
		finally {
			System.exit(0);
		}
	}
	
}
