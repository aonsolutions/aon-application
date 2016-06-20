package com.esferalia.aon.occam.server.fiscal.format.mod200;

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
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.DoubleVariable2014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.DoubleVariable2015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002015Import2014 {

	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(Mod2002014 mod200old, Mod2002015 mod200new);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Añadir casilla si no existe o asignar el valor a la casilla, al Modelo 200 2014
	private static void setDoubleValue(Mod2002015 mod200, Mod2002015Key key, double value) {	
		 
		DoubleVariable2015 t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariable2015(key);
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
	private static double adjustDoubleTax(double value, double type, double type2014 ) {
		
		if (type2014 != 0 && type != 0 && type != type2014) {
			return AonMathUtils.round( value * type / type2014 );
	    }
		else return value;
		
	}

	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Pages2014 {
		
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
			,(mod200old,mod200new) -> mod200new.setYear( 2015 )                                           // Ejercicio
			,(mod200old,mod200new) -> mod200new.setAdministration( mod200old.getAdministration() )		  // Administracion                                           // Ejercicio
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0001, mod200old.getDoubleValue(Mod2002014Key.C0001))  // Entidad sin ánimo de lucro acogida régimen fiscal Título II Ley 49/2002 [001]			                                                    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0002, mod200old.getDoubleValue(Mod2002014Key.C0002))  // Entidad parcialmente exenta [002]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0003, mod200old.getDoubleValue(Mod2002014Key.C0003))  // Sociedad de inversión de capital variable o fondo de inversión de carácter financiero [003]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0004, mod200old.getDoubleValue(Mod2002014Key.C0004))  // Sociedad de inversión inmobiliaria o fondo de inversión inmobiliaria [004]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0005, mod200old.getDoubleValue(Mod2002014Key.C0005))  // Comunidades titulares de montes vecinales en mano común [005]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0011, mod200old.getDoubleValue(Mod2002014Key.C0011))  // Entidad de tenencia de valores extranjeros [011]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0013, mod200old.getDoubleValue(Mod2002014Key.C0013))  // Agrupación de interés económico española o U.T.E. [013]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0014, mod200old.getDoubleValue(Mod2002014Key.C0014))  // Agrupación europea de  interés económico [014]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0017, mod200old.getDoubleValue(Mod2002014Key.C0017))  // Cooperativa protegida [017]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0018, mod200old.getDoubleValue(Mod2002014Key.C0018))  // Cooperativa especialmente protegida [018]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0019, mod200old.getDoubleValue(Mod2002014Key.C0019))  // Resto cooperativas [019]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0021, mod200old.getDoubleValue(Mod2002014Key.C0021))  // Establecimiento permanente [021]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0023, mod200old.getDoubleValue(Mod2002014Key.C0023))  // Gran empresa [023]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0024, mod200old.getDoubleValue(Mod2002014Key.C0024))  // Entidad de crédito [024]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0025, mod200old.getDoubleValue(Mod2002014Key.C0025))  // Entidad aseguradora [025]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0031, mod200old.getDoubleValue(Mod2002014Key.C0031))  // Entidades de  capital-riesgo [031]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0032, mod200old.getDoubleValue(Mod2002014Key.C0032))  // Sociedades desarrollo industrial regional [032]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0036, mod200old.getDoubleValue(Mod2002014Key.C0036))  // Sociedad de garantía recíproca o de reafianzamiento [036]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0048, mod200old.getDoubleValue(Mod2002014Key.C0048))  // Fondo de Pensiones Real Decreto Legislativo 1/2002 de 29 de noviembre [048]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0058, mod200old.getDoubleValue(Mod2002014Key.C0058))  // Mutua de seguros o Mutualidad de previsión social [058]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0060, mod200old.getDoubleValue(Mod2002014Key.C0060))  // Fondos o activos de titulización [060]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0006, mod200old.getDoubleValue(Mod2002014Key.C0006))  // Incentivos empresa de reducida dimensión ( cap XII, tít VII L.I.S )  [006]			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0015, mod200old.getDoubleValue(Mod2002014Key.C0015))  // Entidad ZEC [015]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0022, mod200old.getDoubleValue(Mod2002014Key.C0022))  // Régimen entidades navieras en función del tonelaje [022]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0028, mod200old.getDoubleValue(Mod2002014Key.C0028))  // Tributación conjunta Estado/Diput.Cdad.Forales [028]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0047, mod200old.getDoubleValue(Mod2002014Key.C0047))  // Entidades sometidas a normativa foral [047]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0049, mod200old.getDoubleValue(Mod2002014Key.C0049))  // Regímenes especiales de normativa foral [049]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0029, mod200old.getDoubleValue(Mod2002014Key.C0029))  // Régimen especial Canarias [029]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0033, mod200old.getDoubleValue(Mod2002014Key.C0033))  // Régimen especial minería [033]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0034, mod200old.getDoubleValue(Mod2002014Key.C0034))  // Régimen especial hidrocarburos [034]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0038, mod200old.getDoubleValue(Mod2002014Key.C0038))  // Entidad dedicada al arrend.viviendas [038]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0046, mod200old.getDoubleValue(Mod2002014Key.C0046))  // Entidad en rég. atribución de rentas constituida en el extranjero con presencia en territorio español [046]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0012, mod200old.getDoubleValue(Mod2002014Key.C0012))  // SOCIMI [012]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0057, mod200old.getDoubleValue(Mod2002014Key.C0057))  // Entidades que aplican el régimen especial Ley 11/2009 (excepto SOCIMI) [057]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0020, mod200old.getDoubleValue(Mod2002014Key.C0020))  // Otros regímenes especiales [020]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0056, mod200old.getDoubleValue(Mod2002014Key.C0056))  // Tipo gravamen reducido mant.o creación empleo [056]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0007, mod200old.getDoubleValue(Mod2002014Key.C0007))  // Inclusión en base imponible rentas positivas art. 107 L.I.S. [007]
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0008, mod200old.getDoubleValue(Mod2002014Key.C0008))  // Opción art. 107.6 L.I.S. [008]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0009, mod200old.getDoubleValue(Mod2002014Key.C0009))  // Sociedad dominante de grupo fiscal [009]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0010, mod200old.getDoubleValue(Mod2002014Key.C0010))  // Sociedad dependiente de grupo fiscal [010]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0016, mod200old.getDoubleValue(Mod2002014Key.C0016))  // Opción  art.51.2.b)  L.I.S. [016]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0026, mod200old.getDoubleValue(Mod2002014Key.C0026))  // Entidad  inactiva [026]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0027, mod200old.getDoubleValue(Mod2002014Key.C0027))  // Base imponible negativa o cero [027]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0030, mod200old.getDoubleValue(Mod2002014Key.C0030))  // Transmisión elementos patrimoniales arts. 26.2.d) y 84.1 L.I.S. [030]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0035, mod200old.getDoubleValue(Mod2002014Key.C0035))  // Opción art. 43.1 R.I.S. [035] 
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0037, mod200old.getDoubleValue(Mod2002014Key.C0037))  // Opción art. 43.3 R.I.S. [037]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0039, mod200old.getDoubleValue(Mod2002014Key.C0039))  // Entidad que forma parte de un grupo mercantil (art. 42 del Cód. Comercio) [039]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0043, mod200old.getDoubleValue(Mod2002014Key.C0043))  // Obligación información art. 15 R.I.S. [043]
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0044, mod200old.getDoubleValue(Mod2002014Key.C0044))  // Obligación información art. 45 R.I.S. [044] 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0045, mod200old.getDoubleValue(Mod2002014Key.C0045))  // Inversiones anticipadas - reserva inversiones en Canarias (art. 27.11 Ley 19/1994) [045]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0062, mod200old.getDoubleValue(Mod2002014Key.C0062))  // Régimen fiscal de operaciones de aportación de activos a sociedades para la gestión de activos (Ley 8/2012) [062]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0063, mod200old.getDoubleValue(Mod2002014Key.C0063))  // Tipo de gravamen reducido para entidades de nueva creción (D.A. 19ª LIS) [063]
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.C0059, mod200old.getDoubleValue(Mod2002014Key.C0059))  // Opción  art.44.2 LIS [059]
			
			,(mod200old,mod200new) -> mod200new.setBalanceType( mod200old.getBalanceType().ordinal() )  // Balance y ECPN 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES                
			,(mod200old,mod200new) -> mod200new.setPygType(mod200old.getPygType().ordinal() )           // Pérdidas y ganancias 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002015Key.C0061, mod200old.getDoubleValue(Mod2002014Key.C0061))  // Estados de cuentas de Instituciones de inversión colectiva [061]
			,(mod200old,mod200new) -> mod200new.setFiscalGroup( mod200old.getFiscalGroup() )              // Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040] 
			,(mod200old,mod200new) -> mod200new.setDominantDocument( mod200old.getDominantDocument() )    // N.I.F. de la sociedad dominante para entidades que hayan marcado la clave 010 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002015Key.C0041, mod200old.getDoubleValue(Mod2002014Key.C0041))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002015Key.C0042, mod200old.getDoubleValue(Mod2002014Key.C0042))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042] 
			                  
            ,(mod200old,mod200new) -> mod200new.getSecretary().setName(mod200old.getSecretary().getName())              // Nombre o Razón social - Secretario del Consejo de Administración 
			,(mod200old,mod200new) -> mod200new.getSecretary().setDocument(mod200old.getSecretary().getDocument())      // N.I.F. - Secretario del Consejo de Administración
			,(mod200old,mod200new) -> mod200new.getSecretary().setIrnr( addOneYear(mod200old.getSecretary().getIrnr())) // Fecha - Contribuyentes por el I.R.N.R. 
						
			,(mod200old,mod200new) -> mod200new.getRepresentatives().addAll(mod200old.getRepresentatives())  // Declaración representantes legales entidad
			})
			
		,PAG2 ( new IPropertyFiller[] {				
				
			 (mod200old,mod200new) -> mod200new.getAdministrators().addAll(mod200old.getAdministrators())       // A. Relación de administradores
			,(mod200old,mod200new) -> mod200new.getParticipationsOut().addAll(mod200old.getParticipationsOut()) // B. Participaciones directas - B.1. Participaciones declarante en otras entidades
			,(mod200old,mod200new) -> mod200new.getParticipationsIn().addAll(mod200old.getParticipationsIn())   // B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante
			                                      
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002015Key.POR51, mod200old.getDoubleValue(Mod2002014Key.POR51))  // B .Participaciones directas - B.2. Suma de  porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002015Key.PORES, mod200old.getDoubleValue(Mod2002014Key.PORES))  // B. Participaciones directas - B.2. Suma de porcentajes de participaciones en situaciones especiales
			})
			
		,PAG13 ( new IPropertyFiller[] {
				
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ558, mod200old.getDoubleValue(Mod2002014Key.LQ558))  // Tipo de Gravamen
				
			})			
			
		,PAG15 ( new IPropertyFiller[] {
				
		    // Compensación bases imponibles negativas
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ640, mod200old.getDoubleValue(Mod2002014Key.LQ548))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ643, mod200old.getDoubleValue(Mod2002014Key.LQ645))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ646, mod200old.getDoubleValue(Mod2002014Key.LQ648))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ649, mod200old.getDoubleValue(Mod2002014Key.LQ651))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ652, mod200old.getDoubleValue(Mod2002014Key.LQ654))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ655, mod200old.getDoubleValue(Mod2002014Key.LQ657))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ658, mod200old.getDoubleValue(Mod2002014Key.LQ660))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ661, mod200old.getDoubleValue(Mod2002014Key.LQ663))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ664, mod200old.getDoubleValue(Mod2002014Key.LQ666))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ667, mod200old.getDoubleValue(Mod2002014Key.LQ669))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ743, mod200old.getDoubleValue(Mod2002014Key.LQ748))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ275, mod200old.getDoubleValue(Mod2002014Key.LQ277))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ608, mod200old.getDoubleValue(Mod2002014Key.LQ610))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ704, mod200old.getDoubleValue(Mod2002014Key.LQ706))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ013, mod200old.getDoubleValue(Mod2002014Key.LQ015))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ725, mod200old.getDoubleValue(Mod2002014Key.LQ727))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ534, mod200old.getDoubleValue(Mod2002014Key.LQ536))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ607, mod200old.getDoubleValue(Mod2002014Key.LQ699)+
					             (mod200old.getDoubleValue(Mod2002014Key.C0017)==0 
                               && mod200old.getDoubleValue(Mod2002014Key.C0018)==0 
                               && mod200old.getDoubleValue(Mod2002014Key.C0019)==0 
                               && mod200old.getDoubleValue(Mod2002014Key.LQ552)<0 
                               ?Math.abs(mod200old.getDoubleValue(Mod2002014Key.LQ552)) 
                               : 0) )  // Si la base imponible (casilla 552) del 2014 era negativa, tambien se suma a la 
									   // casilla 534 (si no es cooperativa)
						
			// Deducciones por doble imposición interna
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN101, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN848), mod200old.getDoubleValue(Mod2002014Key.BN105), mod200old.getDoubleValue(Mod2002014Key.BN103) ) )  // pendiente/generada
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN104, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN284), mod200old.getDoubleValue(Mod2002014Key.BN107), mod200old.getDoubleValue(Mod2002014Key.BN103) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN106, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN707), mod200old.getDoubleValue(Mod2002014Key.BN109), mod200old.getDoubleValue(Mod2002014Key.BN103) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN108, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN300), mod200old.getDoubleValue(Mod2002014Key.BN111), mod200old.getDoubleValue(Mod2002014Key.BN103) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN110, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN027), mod200old.getDoubleValue(Mod2002014Key.BN113), mod200old.getDoubleValue(Mod2002014Key.BN103) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN112, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN716), mod200old.getDoubleValue(Mod2002014Key.BN115), mod200old.getDoubleValue(Mod2002014Key.BN103) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN114, mod200old.getDoubleValue(Mod2002014Key.BN738)+
			                                                                          mod200old.getDoubleValue(Mod2002014Key.BN133))
                                                                                  
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN102, mod200old.getDoubleValue(Mod2002014Key.BN105))  // tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN105, mod200old.getDoubleValue(Mod2002014Key.BN107))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN107, mod200old.getDoubleValue(Mod2002014Key.BN109))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN109, mod200old.getDoubleValue(Mod2002014Key.BN111))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN111, mod200old.getDoubleValue(Mod2002014Key.BN113))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN113, mod200old.getDoubleValue(Mod2002014Key.BN115))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN115, mod200old.getDoubleValue(Mod2002014Key.BN103))
                                                                                  
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN696, mod200old.getDoubleValue(Mod2002014Key.BN848))  // pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN846, mod200old.getDoubleValue(Mod2002014Key.BN284))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN282, mod200old.getDoubleValue(Mod2002014Key.BN707))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN702, mod200old.getDoubleValue(Mod2002014Key.BN300))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN071, mod200old.getDoubleValue(Mod2002014Key.BN027))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN025, mod200old.getDoubleValue(Mod2002014Key.BN716))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN714, mod200old.getDoubleValue(Mod2002014Key.BN738)+
			                                                                          mod200old.getDoubleValue(Mod2002014Key.BN133))
            
			// Deducciones por doble imposición internacional
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN151, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN639), mod200old.getDoubleValue(Mod2002014Key.BN728), mod200old.getDoubleValue(Mod2002014Key.BN103) ))  // pendiente/generada
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN153, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN197), mod200old.getDoubleValue(Mod2002014Key.BN729), mod200old.getDoubleValue(Mod2002014Key.BN103) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN154, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN287), mod200old.getDoubleValue(Mod2002014Key.BN730), mod200old.getDoubleValue(Mod2002014Key.BN103) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN155, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN827), mod200old.getDoubleValue(Mod2002014Key.BN731), mod200old.getDoubleValue(Mod2002014Key.BN103) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN156, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN003), mod200old.getDoubleValue(Mod2002014Key.BN732), mod200old.getDoubleValue(Mod2002014Key.BN103) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN157, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN030), mod200old.getDoubleValue(Mod2002014Key.BN733), mod200old.getDoubleValue(Mod2002014Key.BN103) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN158, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN719), mod200old.getDoubleValue(Mod2002014Key.BN734), mod200old.getDoubleValue(Mod2002014Key.BN103) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN159, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN724), mod200old.getDoubleValue(Mod2002014Key.BN721), mod200old.getDoubleValue(Mod2002014Key.BN103) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN720, adjustDoubleTax( mod200old.getDoubleValue(Mod2002014Key.BN742), mod200old.getDoubleValue(Mod2002014Key.BN921), mod200old.getDoubleValue(Mod2002014Key.BN103) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN739, mod200old.getDoubleValue(Mod2002014Key.BN137)+
			                                                                      mod200old.getDoubleValue(Mod2002014Key.BN174))
            
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN152, mod200old.getDoubleValue(Mod2002014Key.BN728))  // tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN728, mod200old.getDoubleValue(Mod2002014Key.BN729))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN729, mod200old.getDoubleValue(Mod2002014Key.BN730))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN730, mod200old.getDoubleValue(Mod2002014Key.BN731))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN731, mod200old.getDoubleValue(Mod2002014Key.BN732))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN732, mod200old.getDoubleValue(Mod2002014Key.BN733))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN733, mod200old.getDoubleValue(Mod2002014Key.BN734))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN734, mod200old.getDoubleValue(Mod2002014Key.BN721))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN721, mod200old.getDoubleValue(Mod2002014Key.BN921))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN921, mod200old.getDoubleValue(Mod2002014Key.BN103))
            
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN711, mod200old.getDoubleValue(Mod2002014Key.BN639))  // pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN637, mod200old.getDoubleValue(Mod2002014Key.BN197))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN849, mod200old.getDoubleValue(Mod2002014Key.BN287))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN285, mod200old.getDoubleValue(Mod2002014Key.BN827))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN825, mod200old.getDoubleValue(Mod2002014Key.BN003))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN001, mod200old.getDoubleValue(Mod2002014Key.BN030))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN028, mod200old.getDoubleValue(Mod2002014Key.BN719))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN717, mod200old.getDoubleValue(Mod2002014Key.BN724))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN722, mod200old.getDoubleValue(Mod2002014Key.BN742))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN740, mod200old.getDoubleValue(Mod2002014Key.BN137)+
			                                                            		  mod200old.getDoubleValue(Mod2002014Key.BN174))
			                                                                    
			})                                                                  
			                                                                    
		,PAG16 ( new IPropertyFiller[] {
						
			// Deducción Art. 42 LIS y Art. 36 Ter Ley 43/95
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN835, mod200old.getDoubleValue(Mod2002014Key.BN837))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN838, mod200old.getDoubleValue(Mod2002014Key.BN840))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN932, mod200old.getDoubleValue(Mod2002014Key.BN934))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN297, mod200old.getDoubleValue(Mod2002014Key.BN299))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN090, mod200old.getDoubleValue(Mod2002014Key.BN092))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN004, mod200old.getDoubleValue(Mod2002014Key.BN006))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN031, mod200old.getDoubleValue(Mod2002014Key.BN033))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN022, mod200old.getDoubleValue(Mod2002014Key.BN024))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN040, mod200old.getDoubleValue(Mod2002014Key.BN042))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN138, mod200old.getDoubleValue(Mod2002014Key.BN140))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN141, mod200old.getDoubleValue(Mod2002014Key.BN143))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN188, mod200old.getDoubleValue(Mod2002014Key.BN190))
				
			// Deducciones disposición transitoria octava LIS			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN749, mod200old.getDoubleValue(Mod2002014Key.BN754))				
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN752, mod200old.getDoubleValue(Mod2002014Key.BN757))				
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN755, mod200old.getDoubleValue(Mod2002014Key.BN760))			 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN758, mod200old.getDoubleValue(Mod2002014Key.BN763))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN761, mod200old.getDoubleValue(Mod2002014Key.BN746))
			
			// Deducciones inversión en Canarias Ley 20/1991
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN854, mod200old.getDoubleValue(Mod2002014Key.BN859))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN857, mod200old.getDoubleValue(Mod2002014Key.BN862))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN860, mod200old.getDoubleValue(Mod2002014Key.BN865))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN863, mod200old.getDoubleValue(Mod2002014Key.BN885))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN883, mod200old.getDoubleValue(Mod2002014Key.BN856))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN088, mod200old.getDoubleValue(Mod2002014Key.BN801))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN194, mod200old.getDoubleValue(Mod2002014Key.BN196))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN868, mod200old.getDoubleValue(Mod2002014Key.BN834))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN871, mod200old.getDoubleValue(Mod2002014Key.BN873))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN874, mod200old.getDoubleValue(Mod2002014Key.BN876))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN877, mod200old.getDoubleValue(Mod2002014Key.BN879))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN880, mod200old.getDoubleValue(Mod2002014Key.BN882))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN866, mod200old.getDoubleValue(Mod2002014Key.BN870))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN939, mod200old.getDoubleValue(Mod2002014Key.BN941))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN191, mod200old.getDoubleValue(Mod2002014Key.BN193))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN613, mod200old.getDoubleValue(Mod2002014Key.BN701))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN200, mod200old.getDoubleValue(Mod2002014Key.BN011))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN037, mod200old.getDoubleValue(Mod2002014Key.BN039))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN044, mod200old.getDoubleValue(Mod2002014Key.BN046))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN528, mod200old.getDoubleValue(Mod2002014Key.BN530))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN144, mod200old.getDoubleValue(Mod2002014Key.BN146))
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN147, mod200old.getDoubleValue(Mod2002014Key.BN149))			
			
			})
			
		,PAG17 ( new IPropertyFiller[] {
				
		    // Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI Ley 43/95 y LIS)
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN768, mod200old.getDoubleValue(Mod2002014Key.BN770)) // 1998
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN774, mod200old.getDoubleValue(Mod2002014Key.BN776)) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN780, mod200old.getDoubleValue(Mod2002014Key.BN782)) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN786, mod200old.getDoubleValue(Mod2002014Key.BN788)) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN766, mod200old.getDoubleValue(Mod2002014Key.BN833)) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN198, mod200old.getDoubleValue(Mod2002014Key.BN897)) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN288, mod200old.getDoubleValue(Mod2002014Key.BN290)) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN466, mod200old.getDoubleValue(Mod2002014Key.BN468)) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN061, mod200old.getDoubleValue(Mod2002014Key.BN586)) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN472, mod200old.getDoubleValue(Mod2002014Key.BN478)) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN180, mod200old.getDoubleValue(Mod2002014Key.BN182)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN531, mod200old.getDoubleValue(Mod2002014Key.BN533)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN945, mod200old.getDoubleValue(Mod2002014Key.BN947)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN960, mod200old.getDoubleValue(Mod2002014Key.BN962)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN183, mod200old.getDoubleValue(Mod2002014Key.BN186)) // 2012			  
	        ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN966, mod200old.getDoubleValue(Mod2002014Key.BN794)+ // 2013: Suma de deducciones Cap.IV Tit.VI Ley 43/95 y LIS (excepto I+D+i)			  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN797)+
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN889)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN809)+				  
//                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN812)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN818)+				  
//                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN821)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN965)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN751)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN968)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN975)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN542)+				  
//                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN545)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN903)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN065)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN069)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN073)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN077)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN080)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN087)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN058)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN209)+				  
//                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN212)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN215)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN218)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN224)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN242)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN245)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN248)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN206)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN221)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN230)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN233)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN236)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN239)+				  
                                                                                      mod200old.getDoubleValue(Mod2002014Key.BN830))  
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN457, mod200old.getDoubleValue(Mod2002014Key.BN800)) // 2013: Investigación y desarrollo (CT)
		    ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN460, mod200old.getDoubleValue(Mod2002014Key.BN713)) // 2013: Innovación tecnológica (IT)   
							 
			})
			
		,PAG17B ( new IPropertyFiller[] {
				
			// El año pasado la casilla 919 era el 80% de la casilla 918 y en la casilla 580 quedaba lo pendiente, por lo tanto para pasar 
			// lo pendiente del 2013 (casilla 580) a la casilla 918, habria que tener en cuenta que esa 918 esta reducida en un 20%, es decir hacer la 
			// operacion inversa
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN918, AonMathUtils.round(mod200old.getDoubleValue(Mod2002014Key.BN580)*100/80))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN589, AonMathUtils.round(mod200old.getDoubleValue(Mod2002014Key.BN978)*100/80))
				
			})
			
		,PAG18 ( new IPropertyFiller[] {

			// Deducción donativos a entidades sin fines de lucro
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN929, mod200old.getDoubleValue(Mod2002014Key.BN944))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN942, mod200old.getDoubleValue(Mod2002014Key.BN296))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN294, mod200old.getDoubleValue(Mod2002014Key.BN084))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN066, mod200old.getDoubleValue(Mod2002014Key.BN010))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN008, mod200old.getDoubleValue(Mod2002014Key.BN036))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN034, mod200old.getDoubleValue(Mod2002014Key.BN203))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN201, mod200old.getDoubleValue(Mod2002014Key.BN906))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN904, mod200old.getDoubleValue(Mod2002014Key.BN992))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN990, mod200old.getDoubleValue(Mod2002014Key.BN999))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.BN997, mod200old.getDoubleValue(Mod2002014Key.BN995))

			})
			
		,PAG18B ( new IPropertyFiller[] {
			
			// Limitacion de la deducilidad de gastos financieros
//			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LM212, mod200old.getDoubleValue(Mod2002014Key.LM971))
//			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LM969, mod200old.getDoubleValue(Mod2002014Key.LM263)+
//					                                                                  mod200old.getDoubleValue(Mod2002014Key.LM266))
			
			// Pendiente de adición por límite beneficio operativo no aplicado
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LM890, mod200old.getDoubleValue(Mod2002014Key.LM523))		                                                              
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LM503, 
//																					  mod200old.getDoubleValue(Mod2002014Key.LM272)+
					                                                                  mod200old.getDoubleValue(Mod2002014Key.LM537))
			
			})		
			
		,PAG20 ( new IPropertyFiller[] {
				
		    // [...] ESTE APARTADO NO ESTABA EN 2013 - Régimen especial de la reserva para inversiones en Canarias
		    // new.089->old.048
		    // new.097->old.527
		    // new.524->old.925
 		    // new.922->old.996

            // Detalle de la compensación de cuotas por pérdidas de cooperativas				
             (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ673, mod200old.getDoubleValue(Mod2002014Key.LQ678))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ676, mod200old.getDoubleValue(Mod2002014Key.LQ681))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ679, mod200old.getDoubleValue(Mod2002014Key.LQ684))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ682, mod200old.getDoubleValue(Mod2002014Key.LQ687))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ685, mod200old.getDoubleValue(Mod2002014Key.LQ690))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ688, mod200old.getDoubleValue(Mod2002014Key.LQ693))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ691, mod200old.getDoubleValue(Mod2002014Key.LQ672))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ623, mod200old.getDoubleValue(Mod2002014Key.LQ281))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ279, mod200old.getDoubleValue(Mod2002014Key.LQ900))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ587, mod200old.getDoubleValue(Mod2002014Key.LQ100))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ059, mod200old.getDoubleValue(Mod2002014Key.LQ019))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ017, mod200old.getDoubleValue(Mod2002014Key.LQ777))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ772, mod200old.getDoubleValue(Mod2002014Key.LQ909))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ907, mod200old.getDoubleValue(Mod2002014Key.LQ912))				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002015Key.LQ910, mod200old.getDoubleValue(Mod2002014Key.LQ937)+            		
            		                                                                  ((mod200old.getDoubleValue(Mod2002014Key.C0017)==1 || mod200old.getDoubleValue(Mod2002014Key.C0018)==1 || mod200old.getDoubleValue(Mod2002014Key.C0019)==1) && (mod200old.getDoubleValue(Mod2002014Key.LQ560)<0) ? Math.abs(mod200old.getDoubleValue(Mod2002014Key.LQ560)) : 0)   )  // Cooperativas: Si la casilla 560 del 2013 es negtiva, tambien se añade a la casilla 910
			
			})
		
		;
		
		private IPropertyFiller[] propertyFillers;
		
		private Pages2014 (IPropertyFiller[] propertyFillers) {		
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Mod2002014 mod200old, Mod2002015 mod200new) {
			for ( Pages2014 page : Pages2014.values()) {				
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(mod200old, mod200new);
					}				
			}
		}
	}
	
	public static void import2014(Mod2002015 mod2002015,Mod2002014 mod2002014) {
		try {
			Pages2014.fill(mod2002014, mod2002015);
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
	}

	public static Mod2002015 import2014(Mod2002014 mod2002014) {
		
		try {
			// Creamos el objeto Mod200 donde importaremos todos los datos del fichero
			Mod2002015 mod2002015 = new Mod2002015();
			import2014(mod2002015, mod2002014);
			return mod2002015;
		} catch (Exception e) {
			throw new AonCoreException(e);
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
	
	private static void toString(Mod2002015 mod200) {
		
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
		for (Mod2002015Key key : Mod2002015Key.values()) {			
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
	
	private static void setDoubleValue2014(Mod2002014 mod200, Mod2002014Key key, double value) {	
		 
		DoubleVariable2014 t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariable2014(key);
			t.setValue(value);
			mod200.addVariable(t);		
		}
		else {
			t.setValue(value);
		}
	}
	
	public static void main(String argv[]) throws UnsupportedEncodingException, FileNotFoundException {
		
		try {
			String filename = "/AEAT/LOTES/ENVIAR/200/2013";  // directorio por defecto
			
			// Mostrar una ventana de dialogo para seleccionar ficheros
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(filename));
            int res = fc.showOpenDialog(new JFrame()); 
		    
	        if (res == JFileChooser.APPROVE_OPTION) {
	        	filename = fc.getSelectedFile().getAbsolutePath();
        		System.out.println("***** Inicio Fichero : "+filename);
				System.out.println("");
				
				File file = new File(filename);
				FileInputStream in = new FileInputStream(file);
				
				Mod2002014 mod200old = Mod2002014Reader.getMod2002014(in);
				if (mod200old!=null) {
					
					// PRUEBA - Inicializamos todas las claves con sus numeros
					 
					for (Mod2002014Key key : Mod2002014Key.values()) {						
						try {
							setDoubleValue2014(mod200old, key, Double.parseDouble(key.name().substring(2)));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}					
					
					// Ademas le ponemos negativo en la 552 y 560 para bases imponibles negativas y cuota compensacion cooperativa negativa
					setDoubleValue2014(mod200old, Mod2002014Key.LQ552, -552 );
					setDoubleValue2014(mod200old, Mod2002014Key.LQ560, -560 );
					
					// Casillas 17, 18 y 19 cooperativas (marcamos o desmarcamos para probar)
					setDoubleValue2014(mod200old, Mod2002014Key.C0017, 0 );
					setDoubleValue2014(mod200old, Mod2002014Key.C0018, 0 );
					setDoubleValue2014(mod200old, Mod2002014Key.C0019, 0 );
					
					// FIN PRUEBA
					
					Mod2002015 mod200new = import2014(mod200old);					
					toString(mod200new);
				}
			
        		System.out.println("");
				System.out.println("***** Fin Fichero : "+filename);
				System.out.println("");
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
