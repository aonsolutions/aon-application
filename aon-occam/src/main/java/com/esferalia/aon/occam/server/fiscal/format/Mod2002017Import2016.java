package com.esferalia.aon.occam.server.fiscal.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.DoubleVariable2017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002017Import2016 {

	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(Mod2002016 mod200old, Mod2002017 mod200new);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Añadir casilla si no existe o asignar el valor a la casilla, al Modelo 200
	private static void setDoubleValue(Mod2002017 mod200, Mod2002017Key key, double value) {
		 
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
	
	// El metodo addYears de AonDateUtils, devuelve una excepcion si la fecha es null
	private static Date addOneYear(Date d) {
		if (d==null)
			return null;
		else return com.esferalia.aon.watson.server.AonDateUtils.addYears(d,1);
	}
	
	// Ajuste de las casillas de la columna "deducción pendiente/generada" de los 
	// apartados "Deducciones por doble imposicion interna e internacional"
	// Se recalcula la columna deduccion pendiente/generada de las deducciones
	// por doble imposicion interna/internacional, si en la linea esta el tipo de
	// gravamen, y el tipo de gravamen del ejercicio anterior es distinto al del
	// año de generacion, en ese caso se hace la proporcion, pues habria que poner
	// aqui el importe pendiente, en relacion al tipo de gravamen en que se genero
	// hay que tener en cuenta que a estas casillas, primero se pasa simplemente
	// el importe pendiente del año anterior, que es en relacion al tipo de gravamen
	// que se genero y al tipo de gravamen aplicable el año anterior
	// Mirar el manual practico, para ver un ejemplo
	private static double adjustDoubleTax(double value, double type, double type2016 ) {
		
		if (type2016 != 0 && type != 0 && type != type2016) {
			return AonMathUtils.round( value * type / type2016 );
	    }
		else return value;
		
	}

	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Pages2017 {
		
		 PAG1 ( new IPropertyFiller[] {
			 (mod200old,mod200new) -> mod200new.setPeriodStart( addOneYear(mod200old.getPeriodStart()))   // Periodo Impositivo - Inicio					
			,(mod200old,mod200new) -> mod200new.setPeriodEnd( addOneYear(mod200old.getPeriodEnd()))       // Periodo Impositivo - Fin
			,(mod200old,mod200new) -> mod200new.setPeriodType( mod200old.getPeriodType())                 // Identificación - Tipo de ejercicio
			,(mod200old,mod200new) -> mod200new.setCnae( mod200old.getCnae() )  						  // Identificación - C.N.A.E.  Actividad principal (convertido a CNAE 2009)
			// No podemos asegurar que el id de enterprise viene bien cumplimentado en mod200old
			// respetamos los id que vienen con mod200new
			//,(mod200old,mod200new) -> mod200new.setEnterprise( mod200old.getEnterprise()) 				  // Identificación - Empresa 
			,(mod200old,mod200new) -> mod200new.setEnterpriseDocument( mod200old.getEnterpriseDocument()) // Identificación - NIF 
			,(mod200old,mod200new) -> mod200new.setEnterpriseName(mod200old.getEnterpriseName())          // Identificación - Apellidos y nombre o Razón Social
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone1(mod200old.getEnterprisePhone1())      // Identificación - Teléfono 1
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone2(mod200old.getEnterprisePhone2())      // Identificación - Teléfono 2
			,(mod200old,mod200new) -> mod200new.setYear( 2017 )                                           // Ejercicio
			,(mod200old,mod200new) -> mod200new.setAdministration( mod200old.getAdministration() )		  // Administracion                                           
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0001, mod200old.getDoubleValue(Mod2002016Key.C0001))  // Entidad sin ánimo de lucro acogida régimen fiscal Título II Ley 49/2002 [001]			                                                    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0002, mod200old.getDoubleValue(Mod2002016Key.C0002))  // Entidad parcialmente exenta [002]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0003, mod200old.getDoubleValue(Mod2002016Key.C0003))  // Sociedad de inversión de capital variable o fondo de inversión de carácter financiero [003]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0004, mod200old.getDoubleValue(Mod2002016Key.C0004))  // Sociedad de inversión inmobiliaria o fondo de inversión inmobiliaria [004]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0005, mod200old.getDoubleValue(Mod2002016Key.C0005))  // Comunidades titulares de montes vecinales en mano común [005]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0011, mod200old.getDoubleValue(Mod2002016Key.C0011))  // Entidad de tenencia de valores extranjeros [011]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0013, mod200old.getDoubleValue(Mod2002016Key.C0013))  // Agrupación de interés económico española o U.T.E. [013]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0014, mod200old.getDoubleValue(Mod2002016Key.C0014))  // Agrupación europea de  interés económico [014]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0017, mod200old.getDoubleValue(Mod2002016Key.C0017))  // Cooperativa protegida [017]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0018, mod200old.getDoubleValue(Mod2002016Key.C0018))  // Cooperativa especialmente protegida [018]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0019, mod200old.getDoubleValue(Mod2002016Key.C0019))  // Resto cooperativas [019]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0021, mod200old.getDoubleValue(Mod2002016Key.C0021))  // Establecimiento permanente [021]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0023, mod200old.getDoubleValue(Mod2002016Key.C0023))  // Gran empresa [023]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0024, mod200old.getDoubleValue(Mod2002016Key.C0024))  // Entidad de crédito [024]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0025, mod200old.getDoubleValue(Mod2002016Key.C0025))  // Entidad aseguradora [025]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0031, mod200old.getDoubleValue(Mod2002016Key.C0031))  // Entidades de capital-riesgo [031]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0032, mod200old.getDoubleValue(Mod2002016Key.C0032))  // Sociedades desarrollo industrial regional [032]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0036, mod200old.getDoubleValue(Mod2002016Key.C0036))  // Sociedad de garantía recíproca o de reafianzamiento [036]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0048, mod200old.getDoubleValue(Mod2002016Key.C0048))  // Fondo de Pensiones Real Decreto Legislativo 1/2002 de 29 de noviembre [048]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0058, mod200old.getDoubleValue(Mod2002016Key.C0058))  // Mutua de seguros o Mutualidad de previsión social [058]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0060, mod200old.getDoubleValue(Mod2002016Key.C0060))  // Fondos o activos de titulización [060]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0066, mod200old.getDoubleValue(Mod2002016Key.C0066))  // Entidad patrimonial
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0006, mod200old.getDoubleValue(Mod2002016Key.C0006))  // Incentivos empresa de reducida dimensión (cap XI, tít VII LIS)  [006]			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0015, mod200old.getDoubleValue(Mod2002016Key.C0015))  // Entidad ZEC [015]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0022, mod200old.getDoubleValue(Mod2002016Key.C0022))  // Régimen entidades navieras en función del tonelaje [022]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0028, mod200old.getDoubleValue(Mod2002016Key.C0028))  // Tributación conjunta Estado/Diput.Cdad.Forales [028]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0047, mod200old.getDoubleValue(Mod2002016Key.C0047))  // Entidades sometidas a normativa foral [047]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0049, mod200old.getDoubleValue(Mod2002016Key.C0049))  // Regímenes especiales de normativa foral [049]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0035, mod200old.getDoubleValue(Mod2002016Key.C0035))  // Aplicación rég. especial fusiones, escisiones, aportaciones activos y canjes valores (Cap. VII, Tít VII) 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0029, mod200old.getDoubleValue(Mod2002016Key.C0029))  // Régimen especial Canarias [029]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0033, mod200old.getDoubleValue(Mod2002016Key.C0033))  // Régimen especial minería [033]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0034, mod200old.getDoubleValue(Mod2002016Key.C0034))  // Régimen especial hidrocarburos [034]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0038, mod200old.getDoubleValue(Mod2002016Key.C0038))  // Entidad dedicada al arrend.viviendas [038]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0046, mod200old.getDoubleValue(Mod2002016Key.C0046))  // Entidad en rég. atribución de rentas constituida en el extranjero con presencia en territorio español [046]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0012, mod200old.getDoubleValue(Mod2002016Key.C0012))  // SOCIMI [012]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0064, mod200old.getDoubleValue(Mod2002016Key.C0064))  // Régimen fiscal entrada SOCIMI
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0057, mod200old.getDoubleValue(Mod2002016Key.C0057))  // Régimen fiscal salida SOCIMI
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0062, mod200old.getDoubleValue(Mod2002016Key.C0062))  // Rég. fiscal de operaciones de aportación de activos a sociedades para la gestión de activos (Ley 8/2012)
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0020, mod200old.getDoubleValue(Mod2002016Key.C0020))  // Otros regímenes especiales [020]

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0007, mod200old.getDoubleValue(Mod2002016Key.C0007))  // Imputación en base imp. rentas positivas art. 100 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0009, mod200old.getDoubleValue(Mod2002016Key.C0009))  // Entidad dominante de grupo fiscal [009]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0010, mod200old.getDoubleValue(Mod2002016Key.C0010))  // Entidad dependiente de grupo fiscal [010]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0016, mod200old.getDoubleValue(Mod2002016Key.C0016))  // Opción art. 46.2 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0026, mod200old.getDoubleValue(Mod2002016Key.C0026))  // Entidad  inactiva [026]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0027, mod200old.getDoubleValue(Mod2002016Key.C0027))  // Base imponible negativa o cero [027]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0030, mod200old.getDoubleValue(Mod2002016Key.C0030))  // Transmisión elementos patrimoniales arts. 27.2.d) y 77.1 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0039, mod200old.getDoubleValue(Mod2002016Key.C0039))  // Entidad que forma parte de un grupo mercantil (art. 42 del Cód. Comercio) [039]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0043, mod200old.getDoubleValue(Mod2002016Key.C0043))  // Obligación información DT 5ª RIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0045, mod200old.getDoubleValue(Mod2002016Key.C0045))  // Inversiones anticipadas - reserva inversiones en Canarias (art. 27.11 Ley 19/1994) [045]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0063, mod200old.getDoubleValue(Mod2002016Key.C0063))  // Tipo de gravamen reducido para entidades de nueva creción (DT 22ª LIS)
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0071, mod200old.getDoubleValue(Mod2002016Key.C0071))  // Tipo de gravamen reducido para entidades de nueva creción (art. 29.1 LIS)
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0059, mod200old.getDoubleValue(Mod2002016Key.C0059))  // Opciones arts. 39.2 y 39.3 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0065, mod200old.getDoubleValue(Mod2002016Key.C0065))  // Bonificación personal investigador (RD 475/2014)
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.C0067, mod200old.getDoubleValue(Mod2002016Key.C0067))  // Opción régimen transitorio de la reducción de ingresos procedentes de determinados activos intangibles (DT 20ª LIS)
			
			,(mod200old,mod200new) -> mod200new.setBalanceType( mod200old.getBalanceType().ordinal() )  // Balance y ECPN 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES                
			,(mod200old,mod200new) -> mod200new.setPygType(mod200old.getPygType().ordinal() )           // Pérdidas y ganancias 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002017Key.C0061, mod200old.getDoubleValue(Mod2002016Key.C0061))  // Estados de cuentas de Instituciones de inversión colectiva [061]
			
			,(mod200old,mod200new) -> mod200new.setFiscalGroup( mod200old.getFiscalGroup() )              // Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040]
			,(mod200old,mod200new) -> mod200new.setDominantDocument( mod200old.getDominantDocument() )    // NIF de la entidad representante/dominante (incluida en el grupo fiscal)
			,(mod200old,mod200new) -> mod200new.setDominantIdentificationNumber( mod200old.getDominantIdentificationNumber() ) // Nº identificación de la entidad dominante (en el caso de grupos constituidos sólo por entidades dependientes)
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002017Key.C0041, mod200old.getDoubleValue(Mod2002016Key.C0041))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002017Key.C0042, mod200old.getDoubleValue(Mod2002016Key.C0042))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042] 
			                  
            ,(mod200old,mod200new) -> mod200new.getSecretary().setName(mod200old.getSecretary().getName())              // Nombre o Razón social - Secretario del Consejo de Administración 
			,(mod200old,mod200new) -> mod200new.getSecretary().setDocument(mod200old.getSecretary().getDocument())      // N.I.F. - Secretario del Consejo de Administración
			,(mod200old,mod200new) -> mod200new.getSecretary().setIrnr( addOneYear(mod200old.getSecretary().getIrnr())) // Fecha - Contribuyentes por el I.R.N.R. 
						
			,(mod200old,mod200new) -> mod200new.getRepresentatives().addAll(mod200old.getRepresentatives())  // Declaración representantes legales entidad
		})

		,PAG2  ( new IPropertyFiller[] {
			 (mod200old,mod200new) -> mod200new.getAdministrators().addAll(mod200old.getAdministrators())       // A. Relación de administradores
			,(mod200old,mod200new) -> mod200new.getParticipationsOut().addAll(mod200old.getParticipationsOut()) // B. Participaciones directas - B.1. Participaciones declarante en otras entidades
			,(mod200old,mod200new) -> mod200new.getParticipationsIn().addAll(mod200old.getParticipationsIn())   // B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante
			                                      
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002017Key.POR51, mod200old.getDoubleValue(Mod2002016Key.POR51))  // B .Participaciones directas - B.2. Suma de  porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002017Key.PORES, mod200old.getDoubleValue(Mod2002016Key.PORES))  // B. Participaciones directas - B.2. Suma de porcentajes de participaciones en situaciones especiales
		})

		,PAG14 ( new IPropertyFiller[] {
			// FALTA - Revisar que el tipo de gravamen sea el mismo para el 2017, con respecto al 2016
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ558, mod200old.getDoubleValue(Mod2002016Key.LQ558))  // Tipo de Gravamen				
		})

		,PAG15 ( new IPropertyFiller[] {
				
		    // Compensación bases imponibles negativas
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ640 , mod200old.getDoubleValue(Mod2002016Key.LQ548 )) // 1997
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ643 , mod200old.getDoubleValue(Mod2002016Key.LQ645 )) // 1998
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ646 , mod200old.getDoubleValue(Mod2002016Key.LQ648 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ649 , mod200old.getDoubleValue(Mod2002016Key.LQ651 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ652 , mod200old.getDoubleValue(Mod2002016Key.LQ654 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ655 , mod200old.getDoubleValue(Mod2002016Key.LQ657 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ658 , mod200old.getDoubleValue(Mod2002016Key.LQ660 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ661 , mod200old.getDoubleValue(Mod2002016Key.LQ663 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ664 , mod200old.getDoubleValue(Mod2002016Key.LQ666 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ667 , mod200old.getDoubleValue(Mod2002016Key.LQ669 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ743 , mod200old.getDoubleValue(Mod2002016Key.LQ748 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ275 , mod200old.getDoubleValue(Mod2002016Key.LQ277 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ608 , mod200old.getDoubleValue(Mod2002016Key.LQ610 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ704 , mod200old.getDoubleValue(Mod2002016Key.LQ706 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ013 , mod200old.getDoubleValue(Mod2002016Key.LQ015 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ725 , mod200old.getDoubleValue(Mod2002016Key.LQ727 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ534 , mod200old.getDoubleValue(Mod2002016Key.LQ536 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ607 , mod200old.getDoubleValue(Mod2002016Key.LQ699 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ1045, mod200old.getDoubleValue(Mod2002016Key.LQ1047)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ1519, mod200old.getDoubleValue(Mod2002016Key.LQ1521)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.LQ1049)+
					                                                                  (mod200old.getDoubleValue(Mod2002016Key.C0017)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002016Key.C0018)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002016Key.C0019)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002016Key.LQ552)<0 
                                                                                       ?Math.abs(mod200old.getDoubleValue(Mod2002016Key.LQ552)) : 0) )  
																						// Si la base imponible (casilla 552) del 2016 era negativa,
																						// tambien se suma a esta casilla (si no es cooperativa)

			// Deducciones por doble imposición interna RDL 4/2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN104, adjustDoubleTax( // 2008
					mod200old.getDoubleValue(Mod2002016Key.BN848), mod200old.getDoubleValue(Mod2002016Key.BN105), mod200old.getDoubleValue(Mod2002016Key.BN103A) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN106, adjustDoubleTax( // 2009
					mod200old.getDoubleValue(Mod2002016Key.BN284), mod200old.getDoubleValue(Mod2002016Key.BN107), mod200old.getDoubleValue(Mod2002016Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN108, adjustDoubleTax( // 2010
					mod200old.getDoubleValue(Mod2002016Key.BN707), mod200old.getDoubleValue(Mod2002016Key.BN109), mod200old.getDoubleValue(Mod2002016Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN110, adjustDoubleTax( // 2011
					mod200old.getDoubleValue(Mod2002016Key.BN300), mod200old.getDoubleValue(Mod2002016Key.BN111), mod200old.getDoubleValue(Mod2002016Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN112, adjustDoubleTax( // 2012
					mod200old.getDoubleValue(Mod2002016Key.BN027), mod200old.getDoubleValue(Mod2002016Key.BN113), mod200old.getDoubleValue(Mod2002016Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN114, adjustDoubleTax( // 2013
					mod200old.getDoubleValue(Mod2002016Key.BN716), mod200old.getDoubleValue(Mod2002016Key.BN115), mod200old.getDoubleValue(Mod2002016Key.BN103A) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN735, adjustDoubleTax( // 2014
					mod200old.getDoubleValue(Mod2002016Key.BN738), mod200old.getDoubleValue(Mod2002016Key.BN920), mod200old.getDoubleValue(Mod2002016Key.BN103A) ) )

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN105 , mod200old.getDoubleValue(Mod2002016Key.BN105)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN107 , mod200old.getDoubleValue(Mod2002016Key.BN107)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN109 , mod200old.getDoubleValue(Mod2002016Key.BN109)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN111 , mod200old.getDoubleValue(Mod2002016Key.BN111)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN113 , mod200old.getDoubleValue(Mod2002016Key.BN113)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN115 , mod200old.getDoubleValue(Mod2002016Key.BN115)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN920 , mod200old.getDoubleValue(Mod2002016Key.BN920)) // 2014

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN846 , mod200old.getDoubleValue(Mod2002016Key.BN848)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN282 , mod200old.getDoubleValue(Mod2002016Key.BN284)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN702 , mod200old.getDoubleValue(Mod2002016Key.BN707)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN071 , mod200old.getDoubleValue(Mod2002016Key.BN300)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN025 , mod200old.getDoubleValue(Mod2002016Key.BN027)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN714 , mod200old.getDoubleValue(Mod2002016Key.BN716)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN736 , mod200old.getDoubleValue(Mod2002016Key.BN738)) // 2014
			                                                                          
			// Deducciones por doble imposición interna (DT 23ª.1 LIS)
			// En el ejercicio anterior (2016) no había casillas para indicar 
			// el importe que queda pendiente para periodos futuros
			                                                                    
		})                                                           
			                                                                    
		,PAG16 ( new IPropertyFiller[] {
			
			// Deducciones por doble imposición internacional RDL 4/2004
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN153, adjustDoubleTax( // 2005
					//mod200old.getDoubleValue(Mod2002016Key.BN637)-mod200old.getDoubleValue(Mod2002016Key.BN638), // Se vuelve a permitir poner el pendiente del 2005 aunque en el ejercicio anterior no habia casilla para ello 
					mod200old.getDoubleValue(Mod2002016Key.BN639), mod200old.getDoubleValue(Mod2002016Key.BN728), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN154, adjustDoubleTax( // 2006
					mod200old.getDoubleValue(Mod2002016Key.BN197), mod200old.getDoubleValue(Mod2002016Key.BN729), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN155, adjustDoubleTax( // 2007
					mod200old.getDoubleValue(Mod2002016Key.BN287), mod200old.getDoubleValue(Mod2002016Key.BN730), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN156, adjustDoubleTax( // 2008
					mod200old.getDoubleValue(Mod2002016Key.BN827), mod200old.getDoubleValue(Mod2002016Key.BN731), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN157, adjustDoubleTax( // 2009
					mod200old.getDoubleValue(Mod2002016Key.BN003), mod200old.getDoubleValue(Mod2002016Key.BN732), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN158, adjustDoubleTax( // 2010
					mod200old.getDoubleValue(Mod2002016Key.BN030), mod200old.getDoubleValue(Mod2002016Key.BN733), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN159, adjustDoubleTax( // 2011
					mod200old.getDoubleValue(Mod2002016Key.BN719), mod200old.getDoubleValue(Mod2002016Key.BN734), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN720, adjustDoubleTax( // 2012
					mod200old.getDoubleValue(Mod2002016Key.BN724), mod200old.getDoubleValue(Mod2002016Key.BN721), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN739, adjustDoubleTax( // 2013
					mod200old.getDoubleValue(Mod2002016Key.BN742), mod200old.getDoubleValue(Mod2002016Key.BN921), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN134, adjustDoubleTax( // 2014
					mod200old.getDoubleValue(Mod2002016Key.BN137), mod200old.getDoubleValue(Mod2002016Key.BN926), mod200old.getDoubleValue(Mod2002016Key.BN103C) ))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN728 , mod200old.getDoubleValue(Mod2002016Key.BN728 )) // 2005 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN729 , mod200old.getDoubleValue(Mod2002016Key.BN729 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN730 , mod200old.getDoubleValue(Mod2002016Key.BN730 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN731 , mod200old.getDoubleValue(Mod2002016Key.BN731 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN732 , mod200old.getDoubleValue(Mod2002016Key.BN732 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN733 , mod200old.getDoubleValue(Mod2002016Key.BN733 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN734 , mod200old.getDoubleValue(Mod2002016Key.BN734 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN721 , mod200old.getDoubleValue(Mod2002016Key.BN721 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN921 , mod200old.getDoubleValue(Mod2002016Key.BN921 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN926 , mod200old.getDoubleValue(Mod2002016Key.BN926 )) // 2014

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN637 , mod200old.getDoubleValue(Mod2002016Key.BN639 )) // 2005 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN849 , mod200old.getDoubleValue(Mod2002016Key.BN197 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN285 , mod200old.getDoubleValue(Mod2002016Key.BN287 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN825 , mod200old.getDoubleValue(Mod2002016Key.BN827 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN001 , mod200old.getDoubleValue(Mod2002016Key.BN003 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN028 , mod200old.getDoubleValue(Mod2002016Key.BN030 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN717 , mod200old.getDoubleValue(Mod2002016Key.BN719 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN722 , mod200old.getDoubleValue(Mod2002016Key.BN724 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN740 , mod200old.getDoubleValue(Mod2002016Key.BN742 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN135 , mod200old.getDoubleValue(Mod2002016Key.BN137 )) // 2014
			
			// Deducciones por doble imposición internacional LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1054, adjustDoubleTax(
					mod200old.getDoubleValue(Mod2002016Key.BN1053), mod200old.getDoubleValue(Mod2002016Key.BN1050), mod200old.getDoubleValue(Mod2002016Key.BN103D) )) // 2015
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1050, mod200old.getDoubleValue(Mod2002016Key.BN1050)) // 2015 - Tipo de gravamen
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1051, mod200old.getDoubleValue(Mod2002016Key.BN1053)) // 2015 - Deducción pendiente
			
			
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1348, mod200old.getDoubleValue(Mod2002016Key.BN1352)+mod200old.getDoubleValue(Mod2002016Key.BN174)) // 2016
				
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1349, mod200old.getDoubleValue(Mod2002016Key.BN103D)) // 2016 - Tipo de gravamen
		
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1350, mod200old.getDoubleValue(Mod2002016Key.BN1352)+mod200old.getDoubleValue(Mod2002016Key.BN174)) // 2016 - Deducción pendiente
										
			// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004 y art. 36 ter Ley 43/95
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN835 ,  mod200old.getDoubleValue(Mod2002016Key.BN837 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN838 ,  mod200old.getDoubleValue(Mod2002016Key.BN840 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN932 ,  mod200old.getDoubleValue(Mod2002016Key.BN934 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN297 ,  mod200old.getDoubleValue(Mod2002016Key.BN299 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN090 ,  mod200old.getDoubleValue(Mod2002016Key.BN092 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN004 ,  mod200old.getDoubleValue(Mod2002016Key.BN006 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN031 ,  mod200old.getDoubleValue(Mod2002016Key.BN033 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN022 ,  mod200old.getDoubleValue(Mod2002016Key.BN024 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN040 ,  mod200old.getDoubleValue(Mod2002016Key.BN042 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN138 ,  mod200old.getDoubleValue(Mod2002016Key.BN140 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN141 ,  mod200old.getDoubleValue(Mod2002016Key.BN143 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN188 ,  mod200old.getDoubleValue(Mod2002016Key.BN190 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN803 ,  mod200old.getDoubleValue(Mod2002016Key.BN805 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1055, mod200old.getDoubleValue(Mod2002016Key.BN1057)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN700 ,  mod200old.getDoubleValue(Mod2002016Key.BN709 )+mod200old.getDoubleValue(Mod2002016Key.BN1355)) // 2016
			
		})
        
		,PAG16B ( new IPropertyFiller[] {
				
			// Deducciones disposición transitoria 24ª.1 LIS		
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN749 , mod200old.getDoubleValue(Mod2002016Key.BN754)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN752 , mod200old.getDoubleValue(Mod2002016Key.BN757)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN755 , mod200old.getDoubleValue(Mod2002016Key.BN760)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN758 , mod200old.getDoubleValue(Mod2002016Key.BN763)) // 2015			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN761 , mod200old.getDoubleValue(Mod2002016Key.BN746)+mod200old.getDoubleValue(Mod2002016Key.BN784)) // 2016
			
			// Deducciones inversión en Canarias
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN854 , mod200old.getDoubleValue(Mod2002016Key.BN1356)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN857 , mod200old.getDoubleValue(Mod2002016Key.BN859 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN860 , mod200old.getDoubleValue(Mod2002016Key.BN862 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN863 , mod200old.getDoubleValue(Mod2002016Key.BN865 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN883 , mod200old.getDoubleValue(Mod2002016Key.BN885 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN785 , mod200old.getDoubleValue(Mod2002016Key.BN790 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1357, mod200old.getDoubleValue(Mod2002016Key.BN1359)+mod200old.getDoubleValue(Mod2002016Key.BN856 )) // 2016
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN194 , mod200old.getDoubleValue(Mod2002016Key.BN196 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN868 , mod200old.getDoubleValue(Mod2002016Key.BN834 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN871 , mod200old.getDoubleValue(Mod2002016Key.BN873 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN874 , mod200old.getDoubleValue(Mod2002016Key.BN876 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN877 , mod200old.getDoubleValue(Mod2002016Key.BN889 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN880 , mod200old.getDoubleValue(Mod2002016Key.BN882 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN866 , mod200old.getDoubleValue(Mod2002016Key.BN870 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN939 , mod200old.getDoubleValue(Mod2002016Key.BN941 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN191 , mod200old.getDoubleValue(Mod2002016Key.BN193 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN613 , mod200old.getDoubleValue(Mod2002016Key.BN701 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN200 , mod200old.getDoubleValue(Mod2002016Key.BN011 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN037 , mod200old.getDoubleValue(Mod2002016Key.BN039 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN044 , mod200old.getDoubleValue(Mod2002016Key.BN046 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN528 , mod200old.getDoubleValue(Mod2002016Key.BN530 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN144 , mod200old.getDoubleValue(Mod2002016Key.BN146 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN147 , mod200old.getDoubleValue(Mod2002016Key.BN149 )) // 2014
            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN240 , mod200old.getDoubleValue(Mod2002016Key.BN242 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1058, mod200old.getDoubleValue(Mod2002016Key.BN1060 )+mod200old.getDoubleValue(Mod2002016Key.BN806)) // 2016
		})
			
		,PAG17 ( new IPropertyFiller[] {
				
		    // Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN774 , mod200old.getDoubleValue(Mod2002016Key.BN776 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN780 , mod200old.getDoubleValue(Mod2002016Key.BN782 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN786 , mod200old.getDoubleValue(Mod2002016Key.BN788 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN766 , mod200old.getDoubleValue(Mod2002016Key.BN833 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN198 , mod200old.getDoubleValue(Mod2002016Key.BN897 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN288 , mod200old.getDoubleValue(Mod2002016Key.BN290 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN466 , mod200old.getDoubleValue(Mod2002016Key.BN468 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN061 , mod200old.getDoubleValue(Mod2002016Key.BN586 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN472 , mod200old.getDoubleValue(Mod2002016Key.BN478 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN180 , mod200old.getDoubleValue(Mod2002016Key.BN182 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN531 , mod200old.getDoubleValue(Mod2002016Key.BN533 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN945 , mod200old.getDoubleValue(Mod2002016Key.BN947 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN960 , mod200old.getDoubleValue(Mod2002016Key.BN962 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN183 , mod200old.getDoubleValue(Mod2002016Key.BN186 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN966 , mod200old.getDoubleValue(Mod2002016Key.BN968 )) // 2013 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN457 , mod200old.getDoubleValue(Mod2002016Key.BN459 )) // 2013 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN460 , mod200old.getDoubleValue(Mod2002016Key.BN462 )) // 2013 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1063, mod200old.getDoubleValue(Mod2002016Key.BN1065)) // 2014 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1066, mod200old.getDoubleValue(Mod2002016Key.BN1068)) // 2014 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1069, mod200old.getDoubleValue(Mod2002016Key.BN1071)) // 2014 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN813 , mod200old.getDoubleValue(Mod2002016Key.BN815 )) // 2015 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN986 , mod200old.getDoubleValue(Mod2002016Key.BN507 )) // 2015 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN557 , mod200old.getDoubleValue(Mod2002016Key.BN594 )) // 2015 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN813 , mod200old.getDoubleValue(Mod2002016Key.BN1362)+ // 2016 excepto I+D+i
																					   mod200old.getDoubleValue(Mod2002016Key.BN809 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1077)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN965 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN751 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN797 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN889 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1371)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN080 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN087 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN206 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN016 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN293 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN423 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN434 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN437 )+
																					   mod200old.getDoubleValue(Mod2002016Key.BN440 )+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1083)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1086)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1089)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1092)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1095)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1098)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1101)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1104)+		
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1107)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1116)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN1119)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1374)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1377)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1380)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1383)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1386)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1389)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1392)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1395)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1398)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1401)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1404)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1407)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1410)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1413)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1416)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1419)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1422)+
																					   mod200old.getDoubleValue(Mod2002016Key.BN1425)+
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN830 ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1617, mod200old.getDoubleValue(Mod2002016Key.BN1365)+  // 2016 CT
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN800 )) 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1620, mod200old.getDoubleValue(Mod2002016Key.BN1368)+  // 2016 IT
					                                                                   mod200old.getDoubleValue(Mod2002016Key.BN713 ))
			
		})
			
		,PAG18 ( new IPropertyFiller[] {
				
			// El año pasado la casilla 919 era el 80% de la casilla 918 y en la casilla 580 quedaba lo pendiente, por lo tanto 
			// para pasar lo pendiente del 2013 (casilla 580) a la casilla 918, habria que tener en cuenta que esa 918 esta reducida 
			// en un 20%, es decir hacer la operacion inversa. Se asume lo mismo para el resto de ejercicios
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN918 , AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN580 )*100/80)) // 2013 CTE
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN589 , AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN978 )*100/80)) // 2013 ITE
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN822 , AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN231 )*100/80)) // 2014 CTE
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN232 , AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN851 )*100/80)) // 2014 ITE
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1123, AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1126)*100/80)) // 2015 CTE
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1127, AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1130)*100/80)) // 2015 ITE
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1426, AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1429)*100/80)) // 2016 CTE
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1430, AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1433)*100/80)) // 2016 ITE

			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN294 , mod200old.getDoubleValue(Mod2002016Key.BN296 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN066 , mod200old.getDoubleValue(Mod2002016Key.BN084 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN008 , mod200old.getDoubleValue(Mod2002016Key.BN010 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN034 , mod200old.getDoubleValue(Mod2002016Key.BN036 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN201 , mod200old.getDoubleValue(Mod2002016Key.BN203 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN904 , mod200old.getDoubleValue(Mod2002016Key.BN906 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN990 , mod200old.getDoubleValue(Mod2002016Key.BN992 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN997 , mod200old.getDoubleValue(Mod2002016Key.BN999 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN246 , mod200old.getDoubleValue(Mod2002016Key.BN248 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN993 , mod200old.getDoubleValue(Mod2002016Key.BN995 )+mod200old.getDoubleValue(Mod2002016Key.BN1436)) // 2016
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.1 LIS)
		    // El año pasado las casillas 1163 y 1167 eran el 2% de las casillas 1162 y 1166 respectivamente, por lo tanto para
	        // obtener en la casilla 1166 la base pendiente del 2016, habrá que hacer la operacion inversa, pues en el importe
	        // pendiente del año anterior lo tendremos despues de aplicar el 2% a la base 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1166, mod200old.getDoubleValue(Mod2002016Key.BN1169)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1438, //AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1165)/0.02)+ 
			                                                                           AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1441)/0.02)) // 2016
			                                                                          
  			// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)
  		    // El año pasado las casillas 1175 y 1179 eran el 2% de las casillas 1174 y 1178 respectivamente, por lo tanto para
  	        // obtener en la casilla 1178 la base pendiente del 2016, habrá que hacer la operacion inversa, pues en el importe
  	        // pendiente del año anterior lo tendremos despues de aplicar el 2% a la base 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1178, mod200old.getDoubleValue(Mod2002016Key.BN1181)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.BN1447, //AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1177)/0.02)+
			                                                              			   AonMathUtils.round(mod200old.getDoubleValue(Mod2002016Key.BN1450)/0.02)) // 2016
			
			// Reserva de nivelación - Reducción en base imponible 	
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ1141, mod200old.getDoubleValue(Mod2002016Key.LQ1143)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ1144, mod200old.getDoubleValue(Mod2002016Key.LQ1146)+mod200old.getDoubleValue(Mod2002016Key.LQ1457))  // 2016
				
		})		
			
		,PAG20 ( new IPropertyFiller[] {
				
			// Limitacion en la deducilidad de gastos financieros. Gastos financieros pendientes de deducir
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1188, mod200old.getDoubleValue(Mod2002016Key.LM1191)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1193, mod200old.getDoubleValue(Mod2002016Key.LM1196)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1198, mod200old.getDoubleValue(Mod2002016Key.LM1201)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1202, mod200old.getDoubleValue(Mod2002016Key.LM1205)) // 2015 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1203, mod200old.getDoubleValue(Mod2002016Key.LM1206)) // 2015 Resto			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1462, mod200old.getDoubleValue(Mod2002016Key.LM1210)+mod200old.getDoubleValue(Mod2002016Key.LM1465)) // 2016 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1463, mod200old.getDoubleValue(Mod2002016Key.LM1211)+mod200old.getDoubleValue(Mod2002016Key.LM1466)) // 2016 Resto
			
			// Pendiente de adición por límite beneficio operativo no aplicado
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM890 , mod200old.getDoubleValue(Mod2002016Key.LM892 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM503 , mod200old.getDoubleValue(Mod2002016Key.LM523 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM273 , mod200old.getDoubleValue(Mod2002016Key.LM537 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM955 , mod200old.getDoubleValue(Mod2002016Key.LM957 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LM1217, mod200old.getDoubleValue(Mod2002016Key.LM1219)+mod200old.getDoubleValue(Mod2002016Key.LM1469)) // 2016
		})
			
		,PAG20B ( new IPropertyFiller[] {
			
			// Reserva de capitalización
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ1131, mod200old.getDoubleValue(Mod2002016Key.LQ1133))  // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ1134, mod200old.getDoubleValue(Mod2002016Key.LQ1136)+mod200old.getDoubleValue(Mod2002016Key.LQ1472)) // 2016
			
            // Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
            // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible            
               // Este apartado ha cambiado todas sus casillas en 2016, desglosando más aún las existentes en 2015, por lo que 
               // no veo forma de pasar lo pendiente de forma automática, ya que no existe equivalencia entre unas y otras.
               // Se deja sin traspasar del 2015 y si el usuario tenía algo pendiente, que lo introduzca manualmente en las
               // casillas que corresponda, según la nueva distribución del 2016.
					                                                                  
		})
		
        ,PAG22 ( new IPropertyFiller[] {
        		
    		// [...] Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)
    		   // Este apartado no existía en AON Sociedades en 2016
					                                                                  
        	// Régimen de cooperativas - Detalle de compensación de cuotas	                                                                				
             (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ673 , mod200old.getDoubleValue(Mod2002016Key.LQ1224)) // 2000 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ676 , mod200old.getDoubleValue(Mod2002016Key.LQ678 )) // 2001				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ679 , mod200old.getDoubleValue(Mod2002016Key.LQ681 )) // 2002				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ682 , mod200old.getDoubleValue(Mod2002016Key.LQ684 )) // 2003				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ685 , mod200old.getDoubleValue(Mod2002016Key.LQ687 )) // 2004				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ688 , mod200old.getDoubleValue(Mod2002016Key.LQ690 )) // 2005				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ691 , mod200old.getDoubleValue(Mod2002016Key.LQ693 )) // 2006				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ623 , mod200old.getDoubleValue(Mod2002016Key.LQ672 )) // 2007				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ279 , mod200old.getDoubleValue(Mod2002016Key.LQ281 )) // 2008				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ587 , mod200old.getDoubleValue(Mod2002016Key.LQ900 )) // 2009				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ059 , mod200old.getDoubleValue(Mod2002016Key.LQ100 )) // 2010				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ017 , mod200old.getDoubleValue(Mod2002016Key.LQ019 )) // 2011				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ772 , mod200old.getDoubleValue(Mod2002016Key.LQ777 )) // 2012				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ907 , mod200old.getDoubleValue(Mod2002016Key.LQ909 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ910 , mod200old.getDoubleValue(Mod2002016Key.LQ912 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ935 , mod200old.getDoubleValue(Mod2002016Key.LQ937 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002017Key.LQ1511, mod200old.getDoubleValue(Mod2002016Key.LQ1513)+ // 2016 (Tambien se añade la casilla 560 del ejercicio anterior si es negativa y es cooperativa)
            		                                                                   mod200old.getDoubleValue(Mod2002016Key.LQ1226)+
            		                                                                  ((mod200old.getDoubleValue(Mod2002016Key.C0017)==1 || mod200old.getDoubleValue(Mod2002016Key.C0018)==1 || mod200old.getDoubleValue(Mod2002016Key.C0019)==1) && (mod200old.getDoubleValue(Mod2002016Key.LQ560)<0) ? Math.abs(mod200old.getDoubleValue(Mod2002016Key.LQ560)) : 0)  )  
			
		})
		
		;
		
		private IPropertyFiller[] propertyFillers;
		
		private Pages2017 (IPropertyFiller[] propertyFillers) {
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Mod2002016 mod200old, Mod2002017 mod200new) {
			for ( Pages2017 page : Pages2017.values()) {
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(mod200old, mod200new);
					}				
			}
		}
	}
	
	public static void import2016(Mod2002017 mod200new,Mod2002016 mod200old) {
		try {
			Pages2017.fill(mod200old, mod200new);
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
	}

	public static Mod2002017 import2016(Mod2002016 mod200old) {
		
		try {
			// Creamos el objeto Mod200 del ejercicio actual, donde importaremos los datos del ejercicio anterior
			Mod2002017 mod200new = new Mod2002017();
			import2016(mod200new, mod200old);
			return mod200new;
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
		
	}
	
	// ---- PRUEBAS ---- 
	
	private static String formatDate(Date d) {
		if (d==null)
			return null;
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(d);			
		}
	}
	
	private static void toString(Mod2002017 mod200) {
		
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
		for (Mod2002017Key key : Mod2002017Key.values()) {			
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
	
	public static void main(String argv[]) throws UnsupportedEncodingException, FileNotFoundException {
		
		// Esta prueba unicamente crea un objeto del año anterior e inicializa sus casillas con 
		// los códigos, posteriormente llama a la importacion para ver que se trasladan correctamente
		try {			
				Mod2002016 mod200old = new Mod2002016();
				if (mod200old!=null) {				
					
					// PRUEBA - Inicializamos todas las claves con sus numeros
					
					mod200old.setBalanceType(2); // PYMES
					mod200old.setPygType(2); // PYMES
					 
					for (Mod2002016Key key : Mod2002016Key.values()) {						
						try {
							setDoubleValue2016(mod200old, key, Double.parseDouble(key.name().substring(2)));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}					
					
					// Prueba base imponible negativa (casilla 552)
					setDoubleValue2016(mod200old, Mod2002016Key.LQ552, -552 );
					
					// Cooperativas Casillas 17, 18 y 19. Cuota compensacion negativa (casilla 560) 
					setDoubleValue2016(mod200old, Mod2002016Key.C0017, 0 );
					setDoubleValue2016(mod200old, Mod2002016Key.C0018, 0 );
					setDoubleValue2016(mod200old, Mod2002016Key.C0019, 0 );
					setDoubleValue2016(mod200old, Mod2002016Key.LQ560, 0 );
					
					// FIN PRUEBA
					
					Mod2002017 mod200new = import2016(mod200old);					
					toString(mod200new);
				}
		}
        catch (Exception e) {
		    e.printStackTrace();
        }
		finally {
			System.exit(0);
		}
	}
	
}


