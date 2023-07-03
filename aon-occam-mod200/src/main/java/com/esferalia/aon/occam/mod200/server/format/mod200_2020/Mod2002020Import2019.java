package com.esferalia.aon.occam.mod200.server.format.mod200_2020;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.DoubleVariable2019;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019CorrectionKey;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020KeyDC;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002020Import2019 {

	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(Mod2002019 mod200old, Mod2002020 mod200new);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Añadir casilla si no existe o asignar el valor a la casilla, al Modelo 200
	private static void setDoubleValue(Mod2002020 mod200, IMod200Key key, double value) {
		 
		DoubleVariableEx t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariableEx(key);
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
	private static double adjustDoubleTax(double value, double type, double type2020 ) {
		
		if (type2020 != 0 && type != 0 && type != type2020) {
			return AonMathUtils.round( value * type / type2020 );
	    }
		else return value;
		
	}

	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Pages2020 {
		
		 PAG1 ( new IPropertyFiller[] {
				 
			 (mod200old,mod200new) -> mod200new.setPeriodStart( addOneYear(mod200old.getPeriodStart()))   // Periodo Impositivo - Inicio					
			,(mod200old,mod200new) -> mod200new.setPeriodEnd( addOneYear(mod200old.getPeriodEnd()))       // Periodo Impositivo - Fin
			,(mod200old,mod200new) -> mod200new.setPeriodType( mod200old.getPeriodType())                 // Identificación - Tipo de ejercicio
			,(mod200old,mod200new) -> mod200new.setCnae( mod200old.getCnae() )  						  // Identificación - C.N.A.E.  Actividad principal (convertido a CNAE 2009)
			,(mod200old,mod200new) -> mod200new.setDocument( mod200old.getEnterpriseDocument()) // Identificación - NIF 
			,(mod200old,mod200new) -> mod200new.setName(mod200old.getEnterpriseName())          // Identificación - Apellidos y nombre o Razón Social
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone1(mod200old.getEnterprisePhone1())      // Identificación - Teléfono 1
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone2(mod200old.getEnterprisePhone2())      // Identificación - Teléfono 2
			,(mod200old,mod200new) -> mod200new.setYear( 2020 )                                           // Ejercicio
			,(mod200old,mod200new) -> mod200new.setAdministration( mod200old.getAdministration() )		  // Administracion                                           
			
			// CARACTERES DE LA DECLARACION 
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0001, mod200old.getDoubleValue(Mod2002019Key.C0001))                       
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0002, mod200old.getDoubleValue(Mod2002019Key.C0002))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0080, mod200old.getDoubleValue(Mod2002019Key.C0080))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0003, mod200old.getDoubleValue(Mod2002019Key.C0003))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0004, mod200old.getDoubleValue(Mod2002019Key.C0004))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0005, mod200old.getDoubleValue(Mod2002019Key.C0005))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0011, mod200old.getDoubleValue(Mod2002019Key.C0011))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0013, mod200old.getDoubleValue(Mod2002019Key.C0013))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0014, mod200old.getDoubleValue(Mod2002019Key.C0014))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0017, mod200old.getDoubleValue(Mod2002019Key.C0017))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0018, mod200old.getDoubleValue(Mod2002019Key.C0018))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0019, mod200old.getDoubleValue(Mod2002019Key.C0019))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0021, mod200old.getDoubleValue(Mod2002019Key.C0021))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0023, mod200old.getDoubleValue(Mod2002019Key.C0023))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0024, mod200old.getDoubleValue(Mod2002019Key.C0024))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0025, mod200old.getDoubleValue(Mod2002019Key.C0025))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0031, mod200old.getDoubleValue(Mod2002019Key.C0031))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0032, mod200old.getDoubleValue(Mod2002019Key.C0032))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0036, mod200old.getDoubleValue(Mod2002019Key.C0036))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0048, mod200old.getDoubleValue(Mod2002019Key.C0048))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0058, mod200old.getDoubleValue(Mod2002019Key.C0058))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0060, mod200old.getDoubleValue(Mod2002019Key.C0060))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0066, mod200old.getDoubleValue(Mod2002019Key.C0066))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0078, mod200old.getDoubleValue(Mod2002019Key.C0078))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0006, mod200old.getDoubleValue(Mod2002019Key.C0006))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0015, mod200old.getDoubleValue(Mod2002019Key.C0015))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0079, mod200old.getDoubleValue(Mod2002019Key.C0079))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0022, mod200old.getDoubleValue(Mod2002019Key.C0022))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0028, mod200old.getDoubleValue(Mod2002019Key.C0028))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0047, mod200old.getDoubleValue(Mod2002019Key.C0047))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0049, mod200old.getDoubleValue(Mod2002019Key.C0049))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0035, mod200old.getDoubleValue(Mod2002019Key.C0035))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0029, mod200old.getDoubleValue(Mod2002019Key.C0029))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0033, mod200old.getDoubleValue(Mod2002019Key.C0033))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0034, mod200old.getDoubleValue(Mod2002019Key.C0034))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0038, mod200old.getDoubleValue(Mod2002019Key.C0038))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0046, mod200old.getDoubleValue(Mod2002019Key.C0046))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0012, mod200old.getDoubleValue(Mod2002019Key.C0012))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0012R, mod200old.getDoubleValue(Mod2002019Key.C0012R))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0064, mod200old.getDoubleValue(Mod2002019Key.C0064))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0057, mod200old.getDoubleValue(Mod2002019Key.C0057))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0062, mod200old.getDoubleValue(Mod2002019Key.C0062))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0020, mod200old.getDoubleValue(Mod2002019Key.C0020))  

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0007, mod200old.getDoubleValue(Mod2002019Key.C0007))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0009, mod200old.getDoubleValue(Mod2002019Key.C0009))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0010, mod200old.getDoubleValue(Mod2002019Key.C0010))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0081, mod200old.getDoubleValue(Mod2002019Key.C0081))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0082, mod200old.getDoubleValue(Mod2002019Key.C0082))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0016, mod200old.getDoubleValue(Mod2002019Key.C0016))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0026, mod200old.getDoubleValue(Mod2002019Key.C0026))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0027, mod200old.getDoubleValue(Mod2002019Key.C0027))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0030, mod200old.getDoubleValue(Mod2002019Key.C0030))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0039, mod200old.getDoubleValue(Mod2002019Key.C0039))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0043, mod200old.getDoubleValue(Mod2002019Key.C0043))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0045, mod200old.getDoubleValue(Mod2002019Key.C0045))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0063, mod200old.getDoubleValue(Mod2002019Key.C0063))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0071, mod200old.getDoubleValue(Mod2002019Key.C0071))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0070, mod200old.getDoubleValue(Mod2002019Key.C0070))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0059, mod200old.getDoubleValue(Mod2002019Key.C0059))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0065, mod200old.getDoubleValue(Mod2002019Key.C0065))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0067, mod200old.getDoubleValue(Mod2002019Key.C0067))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0072, mod200old.getDoubleValue(Mod2002019Key.C0072))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0073, mod200old.getDoubleValue(Mod2002019Key.C0073))
			
			// ESTADOS DE CUENTAS 
			
			,(mod200old,mod200new) -> mod200new.setBalanceType( mod200old.getBalanceType().ordinal() )  // Balance
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0050, mod200old.getDoubleValue(Mod2002019Key.C0050))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0051, mod200old.getDoubleValue(Mod2002019Key.C0051))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0052, mod200old.getDoubleValue(Mod2002019Key.C0052))

			,(mod200old,mod200new) -> mod200new.setEcpnType( mod200old.getEcpnType().ordinal() )        // ECPN
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0075, mod200old.getDoubleValue(Mod2002019Key.C0075))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0076, mod200old.getDoubleValue(Mod2002019Key.C0076))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0077, mod200old.getDoubleValue(Mod2002019Key.C0077))

			,(mod200old,mod200new) -> mod200new.setPygType(mod200old.getPygType().ordinal() )           // Pérdidas y ganancias 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0053, mod200old.getDoubleValue(Mod2002019Key.C0053))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0054, mod200old.getDoubleValue(Mod2002019Key.C0054))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.C0055, mod200old.getDoubleValue(Mod2002019Key.C0055))
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002020Key.C0061, mod200old.getDoubleValue(Mod2002019Key.C0061))  // Estados de cuentas de Instituciones de inversión colectiva [061]
			
			// GRUPOS FISCALES 
			
			,(mod200old,mod200new) -> mod200new.setFiscalGroup( mod200old.getFiscalGroup() )              // Grupo Fiscal - Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040]
			,(mod200old,mod200new) -> mod200new.setDominantDocument( mod200old.getDominantDocument() )    // Grupo Fiscal - NIF de la entidad representante/dominante (incluida en el grupo fiscal)
			,(mod200old,mod200new) -> mod200new.setDominantIdentificationNumber( mod200old.getDominantIdentificationNumber() ) // Grupo Fiscal - Nº identificación de la entidad dominante (en el caso de grupos constituidos sólo por entidades dependientes)
			
			,(mod200old,mod200new) -> mod200new.setUltimateDocument( mod200old.getUltimateDocument() )    // Grupo Mercantil - Datos de la sociedad matriz última: NIF o equivalente
			,(mod200old,mod200new) -> mod200new.setUltimateDocumentCountry( mod200old.getUltimateDocumentCountry() )    // Grupo Mercantil - Datos de la sociedad matriz última: Código Pais (NIF)
			,(mod200old,mod200new) -> mod200new.setUltimateName( mod200old.getUltimateName() )            // Grupo Mercantil - Datos de la sociedad matriz última: Nombre o razón social
			,(mod200old,mod200new) -> mod200new.setUltimateCountry( mod200old.getUltimateCountry() )      // Grupo Mercantil - Datos de la sociedad matriz última: País o jurisdicción de residencia fiscal
			
			// PERSONAL ASALARIADO
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002020Key.C0041, mod200old.getDoubleValue(Mod2002019Key.C0041))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002020Key.C0042, mod200old.getDoubleValue(Mod2002019Key.C0042))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042]
			
			// SECRETARIO
			                  
            ,(mod200old,mod200new) -> mod200new.getSecretary().setName(mod200old.getSecretary().getName())              // Nombre o Razón social - Secretario del Consejo de Administración 
			,(mod200old,mod200new) -> mod200new.getSecretary().setDocument(mod200old.getSecretary().getDocument())      // N.I.F. - Secretario del Consejo de Administración
			,(mod200old,mod200new) -> mod200new.getSecretary().setIrnr( addOneYear(mod200old.getSecretary().getIrnr())) // Fecha - Contribuyentes por el I.R.N.R.
			
			// REPRESENTANTES LEGALES
						
			,(mod200old,mod200new) -> mod200new.getRepresentatives().addAll(mod200old.getRepresentatives())  // Declaración representantes legales entidad
			
		})

		,PAG2  ( new IPropertyFiller[] {
				
			 (mod200old,mod200new) -> mod200new.getAdministrators().addAll(mod200old.getAdministrators())       // A. Relación de administradores
			,(mod200old,mod200new) -> mod200new.getParticipationsOut().addAll(mod200old.getParticipationsOut()) // B1. Participaciones directas e indirectas de la declarante en otras sociedades a la fecha de cierre del período declarado
			
		})
		
		,PAG2B  ( new IPropertyFiller[] {
				
			(mod200old,mod200new) -> mod200new.getParticipationsIn().addAll(mod200old.getParticipationsIn())   // B2. Participaciones directas de otras personas o entidades en la declarante a la fecha de cierre del período declarado
				                                      
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002020Key.POR51, mod200old.getDoubleValue(Mod2002019Key.POR51))  // B2. Suma de porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002020Key.PORES, mod200old.getDoubleValue(Mod2002019Key.PORES))  // B2. Suma de porcentajes de participaciones en situaciones especiales
		    
		    ,(mod200old,mod200new) -> mod200new.getMinorEntities().addAll(mod200old.getMinorEntities())   // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
		    
		})
		
		,PAG14 ( new IPropertyFiller[] {
				
			// Se pone por defecto el tipo de gravamen del año pasado, cuando se calcule el modelo, ya se modificará si es necesario
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ558, mod200old.getDoubleValue(Mod2002019Key.LQ558))  // Tipo de Gravamen
			
		})

		,PAG15 ( new IPropertyFiller[] {
				
		    // Compensación bases imponibles negativas
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ640 , mod200old.getDoubleValue(Mod2002019Key.LQ548 )) // 1997
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ643 , mod200old.getDoubleValue(Mod2002019Key.LQ645 )) // 1998
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ646 , mod200old.getDoubleValue(Mod2002019Key.LQ648 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ649 , mod200old.getDoubleValue(Mod2002019Key.LQ651 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ652 , mod200old.getDoubleValue(Mod2002019Key.LQ654 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ655 , mod200old.getDoubleValue(Mod2002019Key.LQ657 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ658 , mod200old.getDoubleValue(Mod2002019Key.LQ660 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ661 , mod200old.getDoubleValue(Mod2002019Key.LQ663 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ664 , mod200old.getDoubleValue(Mod2002019Key.LQ666 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ667 , mod200old.getDoubleValue(Mod2002019Key.LQ669 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ743 , mod200old.getDoubleValue(Mod2002019Key.LQ748 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ275 , mod200old.getDoubleValue(Mod2002019Key.LQ277 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ608 , mod200old.getDoubleValue(Mod2002019Key.LQ610 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ704 , mod200old.getDoubleValue(Mod2002019Key.LQ706 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ013 , mod200old.getDoubleValue(Mod2002019Key.LQ015 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ725 , mod200old.getDoubleValue(Mod2002019Key.LQ727 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ534 , mod200old.getDoubleValue(Mod2002019Key.LQ536 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ607 , mod200old.getDoubleValue(Mod2002019Key.LQ699 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1045, mod200old.getDoubleValue(Mod2002019Key.LQ1047)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1519, mod200old.getDoubleValue(Mod2002019Key.LQ1521)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1592, mod200old.getDoubleValue(Mod2002019Key.LQ1594)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1825, mod200old.getDoubleValue(Mod2002019Key.LQ1827)) // 2018			
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ2193, mod200old.getDoubleValue(Mod2002019Key.LQ2195)+ // 2019
					                                                                   mod200old.getDoubleValue(Mod2002019Key.LQ1049)+
					                                                                  (mod200old.getDoubleValue(Mod2002019Key.C0017)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002019Key.C0018)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002019Key.C0019)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002019Key.LQ552)<0
                                                                                      && mod200old.getDoubleValue(Mod2002019Key.LQ1049)==0
                                                                                       ?Math.abs(mod200old.getDoubleValue(Mod2002019Key.LQ552)) : 0) )  
																						// Si la base imponible (casilla 552) del 2019 era negativa, tambien se suma a esta casilla (si no es cooperativa), si no estaba indicada en la 1049
			
			// Deducciones por doble imposición interna RDL 4/2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN104, adjustDoubleTax( // 2008
					mod200old.getDoubleValue(Mod2002019Key.BN848), mod200old.getDoubleValue(Mod2002019Key.BN105), mod200old.getDoubleValue(Mod2002019Key.BN103A) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN106, adjustDoubleTax( // 2009
					mod200old.getDoubleValue(Mod2002019Key.BN284), mod200old.getDoubleValue(Mod2002019Key.BN107), mod200old.getDoubleValue(Mod2002019Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN108, adjustDoubleTax( // 2010
					mod200old.getDoubleValue(Mod2002019Key.BN707), mod200old.getDoubleValue(Mod2002019Key.BN109), mod200old.getDoubleValue(Mod2002019Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN110, adjustDoubleTax( // 2011
					mod200old.getDoubleValue(Mod2002019Key.BN300), mod200old.getDoubleValue(Mod2002019Key.BN111), mod200old.getDoubleValue(Mod2002019Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN112, adjustDoubleTax( // 2012
					mod200old.getDoubleValue(Mod2002019Key.BN027), mod200old.getDoubleValue(Mod2002019Key.BN113), mod200old.getDoubleValue(Mod2002019Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN114, adjustDoubleTax( // 2013
					mod200old.getDoubleValue(Mod2002019Key.BN716), mod200old.getDoubleValue(Mod2002019Key.BN115), mod200old.getDoubleValue(Mod2002019Key.BN103A) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN735, adjustDoubleTax( // 2014
					mod200old.getDoubleValue(Mod2002019Key.BN738), mod200old.getDoubleValue(Mod2002019Key.BN920), mod200old.getDoubleValue(Mod2002019Key.BN103A) ) )

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN105 , mod200old.getDoubleValue(Mod2002019Key.BN105)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN107 , mod200old.getDoubleValue(Mod2002019Key.BN107)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN109 , mod200old.getDoubleValue(Mod2002019Key.BN109)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN111 , mod200old.getDoubleValue(Mod2002019Key.BN111)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN113 , mod200old.getDoubleValue(Mod2002019Key.BN113)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN115 , mod200old.getDoubleValue(Mod2002019Key.BN115)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN920 , mod200old.getDoubleValue(Mod2002019Key.BN920)) // 2014

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN846 , mod200old.getDoubleValue(Mod2002019Key.BN848)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN282 , mod200old.getDoubleValue(Mod2002019Key.BN284)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN702 , mod200old.getDoubleValue(Mod2002019Key.BN707)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN071 , mod200old.getDoubleValue(Mod2002019Key.BN300)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN025 , mod200old.getDoubleValue(Mod2002019Key.BN027)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN714 , mod200old.getDoubleValue(Mod2002019Key.BN716)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN736 , mod200old.getDoubleValue(Mod2002019Key.BN738)) // 2014
			                                                                          
			// Deducciones por doble imposición interna (DT 23ª.1 LIS)
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN101, adjustDoubleTax( // 2015
					mod200old.getDoubleValue(Mod2002019Key.BN121), mod200old.getDoubleValue(Mod2002019Key.BN102), mod200old.getDoubleValue(Mod2002019Key.BN103B) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN122, adjustDoubleTax( // 2016
					mod200old.getDoubleValue(Mod2002019Key.BN126), mod200old.getDoubleValue(Mod2002019Key.BN123), mod200old.getDoubleValue(Mod2002019Key.BN103B) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1595, adjustDoubleTax( // 2017
					mod200old.getDoubleValue(Mod2002019Key.BN1599), mod200old.getDoubleValue(Mod2002019Key.BN1596), mod200old.getDoubleValue(Mod2002019Key.BN103B) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1828, adjustDoubleTax( // 2018
					mod200old.getDoubleValue(Mod2002019Key.BN1832), mod200old.getDoubleValue(Mod2002019Key.BN1829), mod200old.getDoubleValue(Mod2002019Key.BN103B) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2196, adjustDoubleTax( // 2019
					mod200old.getDoubleValue(Mod2002019Key.BN2200)+mod200old.getDoubleValue(Mod2002019Key.BN129), mod200old.getDoubleValue(Mod2002019Key.BN2197), mod200old.getDoubleValue(Mod2002019Key.BN103B) ) )

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN102 , mod200old.getDoubleValue(Mod2002019Key.BN102)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN123 , mod200old.getDoubleValue(Mod2002019Key.BN123)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1596 , mod200old.getDoubleValue(Mod2002019Key.BN1596)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1829 , mod200old.getDoubleValue(Mod2002019Key.BN1829)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2197 , mod200old.getDoubleValue(Mod2002019Key.BN2197)) // 2019

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN119 , mod200old.getDoubleValue(Mod2002019Key.BN121)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN124 , mod200old.getDoubleValue(Mod2002019Key.BN126)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1597 , mod200old.getDoubleValue(Mod2002019Key.BN1599)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1830 , mod200old.getDoubleValue(Mod2002019Key.BN1832)) // 2018			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2198 , mod200old.getDoubleValue(Mod2002019Key.BN2200)+mod200old.getDoubleValue(Mod2002019Key.BN129)) // 2019
			                                                                    
		})                                                           
			                                                                    
		,PAG16 ( new IPropertyFiller[] {
			
			// Deducciones por doble imposición internacional RDL 4/2004
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN153, adjustDoubleTax(  // 2005
					mod200old.getDoubleValue(Mod2002019Key.BN639), mod200old.getDoubleValue(Mod2002019Key.BN728), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN154, adjustDoubleTax( // 2006
					mod200old.getDoubleValue(Mod2002019Key.BN197), mod200old.getDoubleValue(Mod2002019Key.BN729), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN155, adjustDoubleTax( // 2007
					mod200old.getDoubleValue(Mod2002019Key.BN287), mod200old.getDoubleValue(Mod2002019Key.BN730), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN156, adjustDoubleTax( // 2008
					mod200old.getDoubleValue(Mod2002019Key.BN827), mod200old.getDoubleValue(Mod2002019Key.BN731), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN157, adjustDoubleTax( // 2009
					mod200old.getDoubleValue(Mod2002019Key.BN003), mod200old.getDoubleValue(Mod2002019Key.BN732), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN158, adjustDoubleTax( // 2010
					mod200old.getDoubleValue(Mod2002019Key.BN030), mod200old.getDoubleValue(Mod2002019Key.BN733), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN159, adjustDoubleTax( // 2011
					mod200old.getDoubleValue(Mod2002019Key.BN719), mod200old.getDoubleValue(Mod2002019Key.BN734), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN720, adjustDoubleTax( // 2012
					mod200old.getDoubleValue(Mod2002019Key.BN724), mod200old.getDoubleValue(Mod2002019Key.BN721), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN739, adjustDoubleTax( // 2013
					mod200old.getDoubleValue(Mod2002019Key.BN742), mod200old.getDoubleValue(Mod2002019Key.BN921), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN134, adjustDoubleTax( // 2014
					mod200old.getDoubleValue(Mod2002019Key.BN137), mod200old.getDoubleValue(Mod2002019Key.BN926), mod200old.getDoubleValue(Mod2002019Key.BN103C) ))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN728 , mod200old.getDoubleValue(Mod2002019Key.BN728 )) // 2005 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN729 , mod200old.getDoubleValue(Mod2002019Key.BN729 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN730 , mod200old.getDoubleValue(Mod2002019Key.BN730 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN731 , mod200old.getDoubleValue(Mod2002019Key.BN731 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN732 , mod200old.getDoubleValue(Mod2002019Key.BN732 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN733 , mod200old.getDoubleValue(Mod2002019Key.BN733 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN734 , mod200old.getDoubleValue(Mod2002019Key.BN734 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN721 , mod200old.getDoubleValue(Mod2002019Key.BN721 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN921 , mod200old.getDoubleValue(Mod2002019Key.BN921 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN926 , mod200old.getDoubleValue(Mod2002019Key.BN926 )) // 2014

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN637 , mod200old.getDoubleValue(Mod2002019Key.BN639 )) // 2005 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN849 , mod200old.getDoubleValue(Mod2002019Key.BN197 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN285 , mod200old.getDoubleValue(Mod2002019Key.BN287 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN825 , mod200old.getDoubleValue(Mod2002019Key.BN827 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN001 , mod200old.getDoubleValue(Mod2002019Key.BN003 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN028 , mod200old.getDoubleValue(Mod2002019Key.BN030 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN717 , mod200old.getDoubleValue(Mod2002019Key.BN719 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN722 , mod200old.getDoubleValue(Mod2002019Key.BN724 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN740 , mod200old.getDoubleValue(Mod2002019Key.BN742 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN135 , mod200old.getDoubleValue(Mod2002019Key.BN137 )) // 2014
			
			// Deducciones por doble imposición internacional LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1054, adjustDoubleTax(mod200old.getDoubleValue(Mod2002019Key.BN1053), mod200old.getDoubleValue(Mod2002019Key.BN1050), mod200old.getDoubleValue(Mod2002019Key.BN103D) )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1348, adjustDoubleTax(mod200old.getDoubleValue(Mod2002019Key.BN1352), mod200old.getDoubleValue(Mod2002019Key.BN1349), mod200old.getDoubleValue(Mod2002019Key.BN103D) )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1770, adjustDoubleTax(mod200old.getDoubleValue(Mod2002019Key.BN1774), mod200old.getDoubleValue(Mod2002019Key.BN1771), mod200old.getDoubleValue(Mod2002019Key.BN103D) )) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1833, adjustDoubleTax(mod200old.getDoubleValue(Mod2002019Key.BN1837), mod200old.getDoubleValue(Mod2002019Key.BN1834), mod200old.getDoubleValue(Mod2002019Key.BN103D) )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2201, adjustDoubleTax(mod200old.getDoubleValue(Mod2002019Key.BN2205)+mod200old.getDoubleValue(Mod2002019Key.BN174), mod200old.getDoubleValue(Mod2002019Key.BN2202), mod200old.getDoubleValue(Mod2002019Key.BN103D) )) // 2019
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1050, mod200old.getDoubleValue(Mod2002019Key.BN1050)) // 2015 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1349, mod200old.getDoubleValue(Mod2002019Key.BN1349)) // 2016 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1771, mod200old.getDoubleValue(Mod2002019Key.BN1771)) // 2017 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1834, mod200old.getDoubleValue(Mod2002019Key.BN1834)) // 2018 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2202, mod200old.getDoubleValue(Mod2002019Key.BN2202)) // 2019 - Tipo de gravamen
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1051, mod200old.getDoubleValue(Mod2002019Key.BN1053)) // 2015 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1350, mod200old.getDoubleValue(Mod2002019Key.BN1352)) // 2016 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1772, mod200old.getDoubleValue(Mod2002019Key.BN1774)) // 2017 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1835, mod200old.getDoubleValue(Mod2002019Key.BN1837)) // 2018 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2203, mod200old.getDoubleValue(Mod2002019Key.BN2205)+mod200old.getDoubleValue(Mod2002019Key.BN174)) // 2019 - Deducción pendiente
										
			// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN297 , mod200old.getDoubleValue(Mod2002019Key.BN299 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN090 , mod200old.getDoubleValue(Mod2002019Key.BN092 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN004 , mod200old.getDoubleValue(Mod2002019Key.BN006 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN031 , mod200old.getDoubleValue(Mod2002019Key.BN033 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN022 , mod200old.getDoubleValue(Mod2002019Key.BN024 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN040 , mod200old.getDoubleValue(Mod2002019Key.BN042 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN138 , mod200old.getDoubleValue(Mod2002019Key.BN140 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN141 , mod200old.getDoubleValue(Mod2002019Key.BN143 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN188 , mod200old.getDoubleValue(Mod2002019Key.BN190 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN803 , mod200old.getDoubleValue(Mod2002019Key.BN805 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1055, mod200old.getDoubleValue(Mod2002019Key.BN1057)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN700 , mod200old.getDoubleValue(Mod2002019Key.BN709 )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1353, mod200old.getDoubleValue(Mod2002019Key.BN1355)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1775, mod200old.getDoubleValue(Mod2002019Key.BN1777)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1838, mod200old.getDoubleValue(Mod2002019Key.BN1840)+mod200old.getDoubleValue(Mod2002019Key.BN2208)) // 2019
			
		})
        
		,PAG16B ( new IPropertyFiller[] {
				
			// Deducciones disposición transitoria 24ª.1 LIS		
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN749 , mod200old.getDoubleValue(Mod2002019Key.BN754)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN752 , mod200old.getDoubleValue(Mod2002019Key.BN757)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN755 , mod200old.getDoubleValue(Mod2002019Key.BN760)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN758 , mod200old.getDoubleValue(Mod2002019Key.BN763)) // 2018			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN761 , mod200old.getDoubleValue(Mod2002019Key.BN746)+mod200old.getDoubleValue(Mod2002019Key.BN784)) // 2019
			
			// Deducciones inversión en Canarias
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN854 , mod200old.getDoubleValue(Mod2002019Key.BN1356)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN857 , mod200old.getDoubleValue(Mod2002019Key.BN859 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN860 , mod200old.getDoubleValue(Mod2002019Key.BN862 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN863 , mod200old.getDoubleValue(Mod2002019Key.BN865 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN883 , mod200old.getDoubleValue(Mod2002019Key.BN885 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN785 , mod200old.getDoubleValue(Mod2002019Key.BN790 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1357, mod200old.getDoubleValue(Mod2002019Key.BN1359)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1778, mod200old.getDoubleValue(Mod2002019Key.BN1780)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN852 , mod200old.getDoubleValue(Mod2002019Key.BN856 )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2116, mod200old.getDoubleValue(Mod2002019Key.BN2118)+mod200old.getDoubleValue(Mod2002019Key.BN2211)) // 2019
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN874 , mod200old.getDoubleValue(Mod2002019Key.BN876 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN877 , mod200old.getDoubleValue(Mod2002019Key.BN889 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN880 , mod200old.getDoubleValue(Mod2002019Key.BN882 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN866 , mod200old.getDoubleValue(Mod2002019Key.BN870 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN939 , mod200old.getDoubleValue(Mod2002019Key.BN941 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN191 , mod200old.getDoubleValue(Mod2002019Key.BN193 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN613 , mod200old.getDoubleValue(Mod2002019Key.BN701 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN200 , mod200old.getDoubleValue(Mod2002019Key.BN011 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN037 , mod200old.getDoubleValue(Mod2002019Key.BN039 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN044 , mod200old.getDoubleValue(Mod2002019Key.BN046 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN528 , mod200old.getDoubleValue(Mod2002019Key.BN530 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN144 , mod200old.getDoubleValue(Mod2002019Key.BN146 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN147 , mod200old.getDoubleValue(Mod2002019Key.BN149 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN240 , mod200old.getDoubleValue(Mod2002019Key.BN242 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1058, mod200old.getDoubleValue(Mod2002019Key.BN1060)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN791 , mod200old.getDoubleValue(Mod2002019Key.BN806 )) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1781, mod200old.getDoubleValue(Mod2002019Key.BN1783)) // 2018            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2122, mod200old.getDoubleValue(Mod2002019Key.BN2124)+mod200old.getDoubleValue(Mod2002019Key.BN2214)) // 2019
            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2119, mod200old.getDoubleValue(Mod2002019Key.BN2121)) // 2018: Inversiones en La Palma, La Gomera y El Hierro
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2125, mod200old.getDoubleValue(Mod2002019Key.BN2127)+mod200old.getDoubleValue(Mod2002019Key.BN2217)) // 2019: Inversiones en La Palma, La Gomera y El Hierro            
            
		})
			
		,PAG17_18 ( new IPropertyFiller[] {
				
		    // Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN766 , mod200old.getDoubleValue(Mod2002019Key.BN833 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN198 , mod200old.getDoubleValue(Mod2002019Key.BN897 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN288 , mod200old.getDoubleValue(Mod2002019Key.BN290 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN466 , mod200old.getDoubleValue(Mod2002019Key.BN468 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN061 , mod200old.getDoubleValue(Mod2002019Key.BN586 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN472 , mod200old.getDoubleValue(Mod2002019Key.BN478 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN180 , mod200old.getDoubleValue(Mod2002019Key.BN182 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN531 , mod200old.getDoubleValue(Mod2002019Key.BN533 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN945 , mod200old.getDoubleValue(Mod2002019Key.BN947 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN960 , mod200old.getDoubleValue(Mod2002019Key.BN962 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN183 , mod200old.getDoubleValue(Mod2002019Key.BN186 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN966 , mod200old.getDoubleValue(Mod2002019Key.BN968 )) // 2013 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN457 , mod200old.getDoubleValue(Mod2002019Key.BN459 )) // 2013 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN460 , mod200old.getDoubleValue(Mod2002019Key.BN462 )) // 2013 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1063, mod200old.getDoubleValue(Mod2002019Key.BN1065)) // 2014 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1066, mod200old.getDoubleValue(Mod2002019Key.BN1068)) // 2014 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1069, mod200old.getDoubleValue(Mod2002019Key.BN1071)) // 2014 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN813 , mod200old.getDoubleValue(Mod2002019Key.BN815 )) // 2015 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN986 , mod200old.getDoubleValue(Mod2002019Key.BN507 )) // 2015 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN557 , mod200old.getDoubleValue(Mod2002019Key.BN594 )) // 2015 IT			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1614, mod200old.getDoubleValue(Mod2002019Key.BN1616)) // 2016 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1617, mod200old.getDoubleValue(Mod2002019Key.BN1619)) // 2016 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1620, mod200old.getDoubleValue(Mod2002019Key.BN1622)) // 2016 IT			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1847, mod200old.getDoubleValue(Mod2002019Key.BN1849)) // 2017 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1850, mod200old.getDoubleValue(Mod2002019Key.BN1852)) // 2017 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1853, mod200old.getDoubleValue(Mod2002019Key.BN1855)) // 2017 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2218, mod200old.getDoubleValue(Mod2002019Key.BN2220)) // 2018 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2221, mod200old.getDoubleValue(Mod2002019Key.BN2223)) // 2018 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2224, mod200old.getDoubleValue(Mod2002019Key.BN2226)) // 2018 IT
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2353, mod200old.getDoubleValue(Mod2002019Key.BN1362)+ // 2019 excepto I+D+i
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN830)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN809)+																					   
																					   mod200old.getDoubleValue(Mod2002019Key.BN1077)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN965)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN751)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN797)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN889)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1371)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN2192)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1083)+				                                                                   
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1089)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1374)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1377)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1419)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1625)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1628)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1631)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1634)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1637)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1640)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1643)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1646)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1649)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1652)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1661)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1667)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1673)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1676)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1679)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1691)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1697)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN1700)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1709)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1802)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1858)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1861)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1864)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1867)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1870)+																					   
																					   mod200old.getDoubleValue(Mod2002019Key.BN1876)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1879)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1882)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1885)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1888)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1891)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1894)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1903)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1909)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1912)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1915)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1921)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1924)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1927)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1939)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1942)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1945)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1948)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1873)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1897)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1900)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1906)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1918)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1930)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN1936)+
																					   mod200old.getDoubleValue(Mod2002019Key.BN2286))	
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2356, mod200old.getDoubleValue(Mod2002019Key.BN1365)+  // 2019 CT
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN800 )) 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN2359, mod200old.getDoubleValue(Mod2002019Key.BN1368)+  // 2019 IT
					                                                                   mod200old.getDoubleValue(Mod2002019Key.BN713 ))

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1360, mod200old.getDoubleValue(Mod2002019Key.BN1685))  // 2020
			
		})
			
		,PAG18B ( new IPropertyFiller[] {

			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002			 
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN034 , mod200old.getDoubleValue(Mod2002019Key.BN036))  // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN201 , mod200old.getDoubleValue(Mod2002019Key.BN203))  // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN904 , mod200old.getDoubleValue(Mod2002019Key.BN906))  // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN990 , mod200old.getDoubleValue(Mod2002019Key.BN992))  // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN997 , mod200old.getDoubleValue(Mod2002019Key.BN999))  // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN246 , mod200old.getDoubleValue(Mod2002019Key.BN248))  // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN993 , mod200old.getDoubleValue(Mod2002019Key.BN995))  // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1434, mod200old.getDoubleValue(Mod2002019Key.BN1436)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1718, mod200old.getDoubleValue(Mod2002019Key.BN1720)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1950, mod200old.getDoubleValue(Mod2002019Key.BN1952)+mod200old.getDoubleValue(Mod2002019Key.BN2229)) // 2019
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.1 LIS)
			// Ejercicio 2015 = 2% [01167] = [01166] x 0,02
			// Ejercicio 2016 = 5% [01439] = [01438] x 0,05
			// Ejercicio 2017 = 5% [01443] = [01442] x 0,05
			// Ejercicio 2018 = 5% [01722] = [01721] x 0,05
			// Ejercicio 2019 = 5% [01954] = [01953] x 0,05
			
		    // Dado que el importe pendiente está en funcion del importe resultante de aplicar a la base de deduccion un porcentaje, 
			// para obtener ahora esa base de deducción, según el importe pendiente del año anterior, habra que hacer la operacion inversa
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1166, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1169)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1438, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1441)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1442, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1445)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1721, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1724)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1953, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1956)/0.05)+
					                                                                   AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN2233)/0.05)) // 2019
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)                                                                          
			// Ejercicio 2015 = 2%  [01179] = [01178] x 0,02
			// Ejercicio 2016 = 5%  [01448] = [01447] x 0,05
			// Ejercicio 2017 = 5%  [01452] = [01451] x 0,05
			// Ejercicio 2018 = 5%  [01726] = [01725] x 0,05
  			// Ejercicio 2019 = 5%  [01958] = [01957] x 0,05
  			                        
  		    // idem apartado anterior
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1178, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1181)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1447, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1450)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1451, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1454)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1725, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1728)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.BN1957, AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN1960)/0.05)+
			                                                              			   AonMathUtils.round(mod200old.getDoubleValue(Mod2002019Key.BN2237)/0.05)) // 2019
		})
		
		,PAG20 ( new IPropertyFiller[] {
				
			// Limitacion en la deducilidad de gastos financieros. Gastos financieros pendientes de deducir
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1188, mod200old.getDoubleValue(Mod2002019Key.LM1191)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1193, mod200old.getDoubleValue(Mod2002019Key.LM1196)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1198, mod200old.getDoubleValue(Mod2002019Key.LM1201)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1202, mod200old.getDoubleValue(Mod2002019Key.LM1205)) // 2015 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1203, mod200old.getDoubleValue(Mod2002019Key.LM1206)) // 2015 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1462, mod200old.getDoubleValue(Mod2002019Key.LM1210)) // 2016 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1463, mod200old.getDoubleValue(Mod2002019Key.LM1211)) // 2016 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1736, mod200old.getDoubleValue(Mod2002019Key.LM1465)) // 2017 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1737, mod200old.getDoubleValue(Mod2002019Key.LM1466)) // 2017 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1977, mod200old.getDoubleValue(Mod2002019Key.LM1739)) // 2018 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1978, mod200old.getDoubleValue(Mod2002019Key.LM1740)) // 2018 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM2253, mod200old.getDoubleValue(Mod2002019Key.LM1980)+mod200old.getDoubleValue(Mod2002019Key.LM2256)) // 2019 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM2254, mod200old.getDoubleValue(Mod2002019Key.LM1981)+mod200old.getDoubleValue(Mod2002019Key.LM2257)) // 2019 Resto
			
			// Pendiente de adición por límite beneficio operativo no aplicado			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM955 , mod200old.getDoubleValue(Mod2002019Key.LM957 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1217, mod200old.getDoubleValue(Mod2002019Key.LM1219)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1467, mod200old.getDoubleValue(Mod2002019Key.LM1469)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1741, mod200old.getDoubleValue(Mod2002019Key.LM1743)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1982, mod200old.getDoubleValue(Mod2002019Key.LM1984)+mod200old.getDoubleValue(Mod2002019Key.LM2260)) // 2019

		})
			
		,PAG20B ( new IPropertyFiller[] {
			
			// Reserva de capitalización
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1470, mod200old.getDoubleValue(Mod2002019Key.LQ1472)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1744, mod200old.getDoubleValue(Mod2002019Key.LQ1746)+mod200old.getDoubleValue(Mod2002019Key.LQ1987)) // 2019
			
			// Reserva de nivelación - Reducción en base imponible 	
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1141, mod200old.getDoubleValue(Mod2002019Key.LQ1143)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1144, mod200old.getDoubleValue(Mod2002019Key.LQ1146)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1455, mod200old.getDoubleValue(Mod2002019Key.LQ1457)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1961, mod200old.getDoubleValue(Mod2002019Key.LQ1963)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ2238, mod200old.getDoubleValue(Mod2002019Key.LQ2240)+mod200old.getDoubleValue(Mod2002019Key.LQ1731))  // 2019
			
		})
		
		,PAG20T ( new IPropertyFiller[] {
				
			// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS  
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1524, mod200old.getDoubleValue(Mod2002019Key.LM1528)) // 2007 y anteriores
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1529, mod200old.getDoubleValue(Mod2002019Key.LM1534)) // 2008 a 2015
				
			// Activos por impuesto diferido (AID). Art. 130 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1542, mod200old.getDoubleValue(Mod2002019Key.LM1549)+mod200old.getDoubleValue(Mod2002019Key.LM1550)+mod200old.getDoubleValue(Mod2002019Key.LM1551)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1552, mod200old.getDoubleValue(Mod2002019Key.LM1558)+mod200old.getDoubleValue(Mod2002019Key.LM1559)+mod200old.getDoubleValue(Mod2002019Key.LM1560)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1754, mod200old.getDoubleValue(Mod2002019Key.LM1760)+mod200old.getDoubleValue(Mod2002019Key.LM1761)+mod200old.getDoubleValue(Mod2002019Key.LM1762)) // 2018			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM2100, mod200old.getDoubleValue(Mod2002019Key.LM2106)+mod200old.getDoubleValue(Mod2002019Key.LM2107)+mod200old.getDoubleValue(Mod2002019Key.LM2108)+
					                                                                   mod200old.getDoubleValue(Mod2002019Key.LM2274)+mod200old.getDoubleValue(Mod2002019Key.LM2275)+mod200old.getDoubleValue(Mod2002019Key.LM2276)) // 2019
				
			// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1763, mod200old.getDoubleValue(Mod2002019Key.LM1766)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM2109, mod200old.getDoubleValue(Mod2002019Key.LM2112)+mod200old.getDoubleValue(Mod2002019Key.LM2280)) // 2019
			
		})
		
		,PAG20Q ( new IPropertyFiller[] {

            // Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
            // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible            
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1473, mod200old.getDoubleValue(Mod2002019Key.LM1476)) // 2007 y anteriores: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1408, mod200old.getDoubleValue(Mod2002019Key.LM1409)) // 2007 y anteriores: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1477, mod200old.getDoubleValue(Mod2002019Key.LM1483)) // 2008 a 2015: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1478, mod200old.getDoubleValue(Mod2002019Key.LM1484)) // 2008 a 2015: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1485, mod200old.getDoubleValue(Mod2002019Key.LM1489)) // 2016: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1486, mod200old.getDoubleValue(Mod2002019Key.LM1490)) // 2016: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1491, mod200old.getDoubleValue(Mod2002019Key.LM1493)) // 2017: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1747, mod200old.getDoubleValue(Mod2002019Key.LM1749)) // 2017: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1750, mod200old.getDoubleValue(Mod2002019Key.LM1752)) // 2018: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1988, mod200old.getDoubleValue(Mod2002019Key.LM1990)) // 2018: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM1991, mod200old.getDoubleValue(Mod2002019Key.LM1993)+mod200old.getDoubleValue(Mod2002019Key.LM2266)) // 2019: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LM2261, mod200old.getDoubleValue(Mod2002019Key.LM2263)) // 2019: Que han cumplido ...
				
		})
		
        ,PAG22 ( new IPropertyFiller[] {
        		
    		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)
        	 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.RC089 , mod200old.getDoubleValue(Mod2002019Key.RC048)) // 2016 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.RC097 , mod200old.getDoubleValue(Mod2002019Key.RC527)) // 2017				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.RC524 , mod200old.getDoubleValue(Mod2002019Key.RC925)) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.RC922 , mod200old.getDoubleValue(Mod2002019Key.RC996)) // 2019
					                                                                  
        	// Régimen de cooperativas - Detalle de compensación de cuotas	                                                                				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ673 , mod200old.getDoubleValue(Mod2002019Key.LQ1224)) // 2000 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ676 , mod200old.getDoubleValue(Mod2002019Key.LQ678 )) // 2001				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ679 , mod200old.getDoubleValue(Mod2002019Key.LQ681 )) // 2002				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ682 , mod200old.getDoubleValue(Mod2002019Key.LQ684 )) // 2003				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ685 , mod200old.getDoubleValue(Mod2002019Key.LQ687 )) // 2004				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ688 , mod200old.getDoubleValue(Mod2002019Key.LQ690 )) // 2005				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ691 , mod200old.getDoubleValue(Mod2002019Key.LQ693 )) // 2006				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ623 , mod200old.getDoubleValue(Mod2002019Key.LQ672 )) // 2007				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ279 , mod200old.getDoubleValue(Mod2002019Key.LQ281 )) // 2008				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ587 , mod200old.getDoubleValue(Mod2002019Key.LQ900 )) // 2009				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ059 , mod200old.getDoubleValue(Mod2002019Key.LQ100 )) // 2010				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ017 , mod200old.getDoubleValue(Mod2002019Key.LQ019 )) // 2011				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ772 , mod200old.getDoubleValue(Mod2002019Key.LQ777 )) // 2012				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ907 , mod200old.getDoubleValue(Mod2002019Key.LQ909 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ910 , mod200old.getDoubleValue(Mod2002019Key.LQ912 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ935 , mod200old.getDoubleValue(Mod2002019Key.LQ937 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1511, mod200old.getDoubleValue(Mod2002019Key.LQ1513)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ1767, mod200old.getDoubleValue(Mod2002019Key.LQ1769)) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ2113, mod200old.getDoubleValue(Mod2002019Key.LQ2115)) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002020Key.LQ2113, mod200old.getDoubleValue(Mod2002019Key.LQ2115)+ // 2019 (Tambien se añade la casilla 560 del ejercicio anterior si es negativa y es cooperativa y no está puesto ese importe en la 1226)
            		                                                                   mod200old.getDoubleValue(Mod2002019Key.LQ1226)+
            		                                                                  ((mod200old.getDoubleValue(Mod2002019Key.C0017)==1 || mod200old.getDoubleValue(Mod2002019Key.C0018)==1 || mod200old.getDoubleValue(Mod2002019Key.C0019)==1) && (mod200old.getDoubleValue(Mod2002019Key.LQ560)<0) && (mod200old.getDoubleValue(Mod2002019Key.LQ1226)==0) ? Math.abs(mod200old.getDoubleValue(Mod2002019Key.LQ560)) : 0) )  
			
		})
        
        ,PAG26B ( new IPropertyFiller[] {
        	
        	// Desglose de correcciones al resultado de la cuenta de perdidas y ganancias
       		// Se graban los importes pendientes (columnas 4 y 5 del año anterior) 
        	(mod200old,mod200new) -> {
        		
        		// Recorrer las correcciones del ejercicio anterior, y poner lo pendiente en la casilla correspondiente del ejercicio actual
        		for (Mod2002019CorrectionKey key : Mod2002019CorrectionKey.values()) {
        			// Aumentos
        			if (key.getIncrease() != null) {
        				Mod2002019KeyDC oldKey = key.getDetailIncrease()[3]; // La casilla que ocupa la columna 4 del año anterior, es la que este año lleva el importe pendiente de años anteriores				
        				try {
        					Mod2002020KeyDC newKey = Mod2002020KeyDC.valueOf(oldKey.toString());				
        					setDoubleValue(mod200new, newKey, mod200old.getDoubleValue(key.getDetailIncrease()[3])+mod200old.getDoubleValue(key.getDetailIncrease()[4]));
        				} catch (Exception e) {
        					// Posible excepción, si no existe la casilla en el nuevo ejercicio, no se hace nada
        				}
        			}			
        			// Disminuciones
        			if (key.getDecrease() != null) {
        				Mod2002019KeyDC oldKey = key.getDetailDecrease()[3]; // La casilla que ocupa la columna 4 del año anterior, es la que este año lleva el importe pendiente de años anteriores				
        				try {
        					Mod2002020KeyDC newKey = Mod2002020KeyDC.valueOf(oldKey.toString());				
        					setDoubleValue(mod200new, newKey, mod200old.getDoubleValue(key.getDetailDecrease()[3])+mod200old.getDoubleValue(key.getDetailDecrease()[4]));
        				} catch (Exception e) {
        					// Posible excepción, si no existe la casilla en el nuevo ejercicio, no se hace nada
        				}
        			}
        		}        		
        		
        	}        		
        		
        })
        
		;
		
		private IPropertyFiller[] propertyFillers;
		
		private Pages2020 (IPropertyFiller[] propertyFillers) {
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Mod2002019 mod200old, Mod2002020 mod200new) {
			for ( Pages2020 page : Pages2020.values()) {
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(mod200old, mod200new);
					}				
			}
		}
	}
	
	public static void import2019(Mod2002020 mod200new,Mod2002019 mod200old) {
		try {
			Pages2020.fill(mod200old, mod200new);
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
	}

	public static Mod2002020 import2019(Mod2002019 mod200old) {
		
		try {
			// Creamos el objeto Mod200 del ejercicio actual, donde importaremos los datos del ejercicio anterior
			Mod2002020 mod200new = new Mod2002020();
			import2019(mod200new, mod200old);
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
	
	private static void toString(Mod2002020 mod200) {
		
		System.out.println("PeriodStart...........: " + formatDate(mod200.getPeriodStart()));
		System.out.println("PeriodEnd.............: " + formatDate(mod200.getPeriodEnd()));
		System.out.println("PeriodType............: " + mod200.getPeriodType());
		System.out.println("Cnae .................: " + mod200.getCnae());
		System.out.println("Document..............: " + mod200.getDocument());
		System.out.println("Name..................: " + mod200.getName());
		System.out.println("Phone1................: " + mod200.getEnterprisePhone1());
		System.out.println("Phone2................: " + mod200.getEnterprisePhone2());
		System.out.println("Year..................: " + mod200.getYear());
		System.out.println("BalanceType...........: " + mod200.getBalanceType());
		System.out.println("PygType...............: " + mod200.getPygType());
		System.out.println("FiscalGroup...........: " + mod200.getFiscalGroup());
		System.out.println("DominantDocument......: " + mod200.getDominantDocument());
		System.out.println("Complementary.........: " + mod200.isComplementary());
		System.out.println("ComplementaryReceipt..: " + mod200.getReplacedNumber());
		System.out.println("Secretary_Name........: " + mod200.getSecretary().getName());
		System.out.println("Secretary_Document....: " + mod200.getSecretary().getDocument());
		System.out.println("Secretary_Irnr........: " + formatDate(mod200.getSecretary().getIrnr()));
		
		System.out.println();
		System.out.println("-- CLAVES --");
		for (Mod2002020Key key : Mod2002020Key.values()) {			
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
		List<Mod200CompanyAdministrator> l2 = mod200.getAdministrators();
		System.out.println("-- ADMINISTRADORES -- "+l2.size());		
		for (int i = 0; i < l2.size(); i++) {
			Mod200CompanyAdministrator ca = l2.get(i);
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
		List<Mod200CompanyParticipation> cpo = mod200.getParticipationsOut();
		System.out.println("-- PARTICIPATIONS OUT -- "+cpo.size());		
		for (int i = 0; i < cpo.size(); i++) {
			Mod200CompanyParticipation cp = cpo.get(i);
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
													,cp.getValueCorrection()
													,AonStringUtils.SPACE
													,cp.getLossReversion()
													,AonStringUtils.SPACE
													,cp.getCorrectionEffect()
													,AonStringUtils.SPACE
													,cp.getCorrectionsBalance()
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
		List<Mod200CompanyParticipation> cpi = mod200.getParticipationsIn();
		System.out.println("-- PARTICIPATIONS IN -- "+cpi.size());		
		for (int i = 0; i < cpi.size(); i++) {
			Mod200CompanyParticipation cp = cpi.get(i);
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
	
	private static void setDoubleValue2019(Mod2002019 mod200, Mod2002019Key key, double value) {
		 
		DoubleVariable2019 t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariable2019(key);
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
				Mod2002019 mod200old = new Mod2002019();
				if (mod200old!=null) {				
					
					// PRUEBA - Inicializamos todas las claves con sus numeros
					
					mod200old.setBalanceType(2); // PYMES
					mod200old.setPygType(2); // PYMES
					mod200old.setEcpnType(3); // No consta
					 
					for (Mod2002019Key key : Mod2002019Key.values()) {						
						try {
							setDoubleValue2019(mod200old, key, Double.parseDouble(key.name().substring(2)));
						} catch (NumberFormatException e) {							
							//e.printStackTrace();
						}
					}					
					
					// Prueba base imponible negativa (casilla 552)
					setDoubleValue2019(mod200old, Mod2002019Key.LQ552, -552 );
					
					// Cooperativas Casillas 17, 18 y 19. Cuota compensacion negativa (casilla 560) 
					setDoubleValue2019(mod200old, Mod2002019Key.C0017, 0 );
					setDoubleValue2019(mod200old, Mod2002019Key.C0018, 0 );
					setDoubleValue2019(mod200old, Mod2002019Key.C0019, 0 );
					setDoubleValue2019(mod200old, Mod2002019Key.LQ560, 0 );
					
					// FIN PRUEBA
					
					Mod2002020 mod200new = import2019(mod200old);					
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


