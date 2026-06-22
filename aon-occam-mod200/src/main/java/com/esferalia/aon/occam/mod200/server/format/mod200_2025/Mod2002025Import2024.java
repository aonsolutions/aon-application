package com.esferalia.aon.occam.mod200.server.format.mod200_2025;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025KeyDC;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002025Import2024 {
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(Mod2002024 mod200old, Mod2002025 mod200new);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Añadir casilla si no existe o asignar el valor a la casilla, al Modelo 200
	private static void setDoubleValue(Mod2002025 mod200, IMod200Key key, double value) {
		 
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
		else 
			return com.esferalia.aon.watson.server.AonDateUtils.addYears(d,1);
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
	private static double adjustDoubleTax(double value, double type, double yearType) {
		
		if (yearType != 0 && type != 0 && type != yearType) {
			return AonMathUtils.round( value * type / yearType );
	    }
		else 
			return value;
		
	}
	
	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Pages2025 {
		
		 PAG1 ( new IPropertyFiller[] {
				 
			// IDENTIFICACION
				 
			 (mod200old,mod200new) -> mod200new.setPeriodStart(addOneYear(mod200old.getPeriodStart()))  // Periodo Impositivo - Inicio					
			,(mod200old,mod200new) -> mod200new.setPeriodEnd(addOneYear(mod200old.getPeriodEnd()))      // Periodo Impositivo - Fin
			,(mod200old,mod200new) -> mod200new.setPeriodType(mod200old.getPeriodType())                // Identificación - Tipo de ejercicio
			// NO COPIAR EL CNAE DEL 2024 PORQUE EN EL 2024 ERA EL CNAE2009 Y EN EL 2025 ES EL CNAE2025, ASI QUE SOLO SE COPIARA DE LA ACTIVIDAD PRINCIPAL
//			,(mod200old,mod200new) -> mod200new.setCnae(mod200old.getCnae()) 							// Identificación - C.N.A.E. Actividad principal
			,(mod200old,mod200new) -> mod200new.setDocument(mod200old.getDocument()) 					// Identificación - NIF 
			,(mod200old,mod200new) -> mod200new.setName(mod200old.getName())          					// Identificación - Apellidos y nombre o Razón Social
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone1(mod200old.getEnterprisePhone1())    // Identificación - Teléfono 1
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone2(mod200old.getEnterprisePhone2())    // Identificación - Teléfono 2
			,(mod200old,mod200new) -> mod200new.setYear(2025)             	                            // Ejercicio
			,(mod200old,mod200new) -> mod200new.setAdministration(mod200old.getAdministration())		// Administracion           
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.X0001, mod200old.getDoubleValue(Mod2002024Key.X0001)) // Realiza actividades agrícolas y/o ganaderas
			
			// CARACTERES DE LA DECLARACION 
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0001, mod200old.getDoubleValue(Mod2002024Key.C0001))                       
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0002, mod200old.getDoubleValue(Mod2002024Key.C0002))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0080, mod200old.getDoubleValue(Mod2002024Key.C0080))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0003, mod200old.getDoubleValue(Mod2002024Key.C0003))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0008, mod200old.getDoubleValue(Mod2002024Key.C0008))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0004, mod200old.getDoubleValue(Mod2002024Key.C0004))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0005, mod200old.getDoubleValue(Mod2002024Key.C0005))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0011, mod200old.getDoubleValue(Mod2002024Key.C0011))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0085, mod200old.getDoubleValue(Mod2002024Key.C0085))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0013, mod200old.getDoubleValue(Mod2002024Key.C0013))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0014, mod200old.getDoubleValue(Mod2002024Key.C0014))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0017, mod200old.getDoubleValue(Mod2002024Key.C0017))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0018, mod200old.getDoubleValue(Mod2002024Key.C0018))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0019, mod200old.getDoubleValue(Mod2002024Key.C0019))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0021, mod200old.getDoubleValue(Mod2002024Key.C0021))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0023, mod200old.getDoubleValue(Mod2002024Key.C0023))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0024, mod200old.getDoubleValue(Mod2002024Key.C0024))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0025, mod200old.getDoubleValue(Mod2002024Key.C0025))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0031, mod200old.getDoubleValue(Mod2002024Key.C0031))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0032, mod200old.getDoubleValue(Mod2002024Key.C0032))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0036, mod200old.getDoubleValue(Mod2002024Key.C0036))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0048, mod200old.getDoubleValue(Mod2002024Key.C0048))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0058, mod200old.getDoubleValue(Mod2002024Key.C0058))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0060, mod200old.getDoubleValue(Mod2002024Key.C0060))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0066, mod200old.getDoubleValue(Mod2002024Key.C0066))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0078, mod200old.getDoubleValue(Mod2002024Key.C0078))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0056, mod200old.getDoubleValue(Mod2002024Key.C0056))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0006, mod200old.getDoubleValue(Mod2002024Key.C0006))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0015, mod200old.getDoubleValue(Mod2002024Key.C0015))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0079, mod200old.getDoubleValue(Mod2002024Key.C0079))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0022, mod200old.getDoubleValue(Mod2002024Key.C0022))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0028, mod200old.getDoubleValue(Mod2002024Key.C0028))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0047, mod200old.getDoubleValue(Mod2002024Key.C0047))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0049, mod200old.getDoubleValue(Mod2002024Key.C0049))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0035, mod200old.getDoubleValue(Mod2002024Key.C0035))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0029, mod200old.getDoubleValue(Mod2002024Key.C0029))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0069, mod200old.getDoubleValue(Mod2002024Key.C0069))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0086, mod200old.getDoubleValue(Mod2002024Key.C0086))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0033, mod200old.getDoubleValue(Mod2002024Key.C0033))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0034, mod200old.getDoubleValue(Mod2002024Key.C0034))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0038, mod200old.getDoubleValue(Mod2002024Key.C0038))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0046, mod200old.getDoubleValue(Mod2002024Key.C0046))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0012, mod200old.getDoubleValue(Mod2002024Key.C0012))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0012R, mod200old.getDoubleValue(Mod2002024Key.C0012R))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0064, mod200old.getDoubleValue(Mod2002024Key.C0064))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0057, mod200old.getDoubleValue(Mod2002024Key.C0057))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0062, mod200old.getDoubleValue(Mod2002024Key.C0062))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0020, mod200old.getDoubleValue(Mod2002024Key.C0020))  

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0007, mod200old.getDoubleValue(Mod2002024Key.C0007))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0009, mod200old.getDoubleValue(Mod2002024Key.C0009))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0010, mod200old.getDoubleValue(Mod2002024Key.C0010))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0081, mod200old.getDoubleValue(Mod2002024Key.C0081))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0082, mod200old.getDoubleValue(Mod2002024Key.C0082))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0026, mod200old.getDoubleValue(Mod2002024Key.C0026))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0027, mod200old.getDoubleValue(Mod2002024Key.C0027))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0030, mod200old.getDoubleValue(Mod2002024Key.C0030))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0039, mod200old.getDoubleValue(Mod2002024Key.C0039))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0043, mod200old.getDoubleValue(Mod2002024Key.C0043))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0045, mod200old.getDoubleValue(Mod2002024Key.C0045))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0087, mod200old.getDoubleValue(Mod2002024Key.C0087))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0063, mod200old.getDoubleValue(Mod2002024Key.C0063))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0071, mod200old.getDoubleValue(Mod2002024Key.C0071))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0088, mod200old.getDoubleValue(Mod2002024Key.C0088))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0083, mod200old.getDoubleValue(Mod2002024Key.C0083))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0070, mod200old.getDoubleValue(Mod2002024Key.C0070))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0059, mod200old.getDoubleValue(Mod2002024Key.C0059))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0090, mod200old.getDoubleValue(Mod2002024Key.C0090))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0065, mod200old.getDoubleValue(Mod2002024Key.C0065))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0084, mod200old.getDoubleValue(Mod2002024Key.C0084))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0072, mod200old.getDoubleValue(Mod2002024Key.C0072))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0073, mod200old.getDoubleValue(Mod2002024Key.C0073))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0037, mod200old.getDoubleValue(Mod2002024Key.C0037))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0044, mod200old.getDoubleValue(Mod2002024Key.C0044))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0074, mod200old.getDoubleValue(Mod2002024Key.C0074))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0089, mod200old.getDoubleValue(Mod2002024Key.C0089))
			
			// IMPORTE NETO DE LA CIFRA DE NEGOCIOS
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002025Key.VOLOPE, mod200old.getDoubleValue(Mod2002024Key.VOLOPE))
			
			// GRUPOS FISCAL 
			
			,(mod200old,mod200new) -> mod200new.setFiscalGroup( mod200old.getFiscalGroup() )              						// Grupo Fiscal - Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040]
			,(mod200old,mod200new) -> mod200new.setDominantDocument( mod200old.getDominantDocument() )    						// Grupo Fiscal - NIF de la entidad representante/dominante (incluida en el grupo fiscal)
			,(mod200old,mod200new) -> mod200new.setDominantIdentificationNumber( mod200old.getDominantIdentificationNumber() ) 	// Grupo Fiscal - Nº identificación de la entidad dominante (en el caso de grupos constituidos sólo por entidades dependientes)
			
			// GRUPO MERCANTIL
			
			,(mod200old,mod200new) -> mod200new.setUltimateDocument( mod200old.getUltimateDocument() )          			// Grupo Mercantil - Datos de la sociedad matriz última: NIF
			,(mod200old,mod200new) -> mod200new.setUltimateName( mod200old.getUltimateName() )                  			// Grupo Mercantil - Datos de la sociedad matriz última: Razón social
			,(mod200old,mod200new) -> mod200new.setUltimateGroupName( mod200old.getUltimateGroupName() )        			// Grupo Mercantil - Datos de la sociedad matriz última: Nombre de grupo
			,(mod200old,mod200new) -> mod200new.setUltimateResidenceCountry( mod200old.getUltimateResidenceCountry() )  	// Grupo Mercantil - Identificación fiscal del país de residencia - País de residencia
			,(mod200old,mod200new) -> mod200new.setUltimateResidenceDocument( mod200old.getUltimateResidenceDocument() )	// Grupo Mercantil - Identificación fiscal del país de residencia - NIF en el país de residencia (TIN)
			
			// ESTADOS DE CUENTAS 
			
			,(mod200old,mod200new) -> mod200new.setBalanceType( mod200old.getBalanceType().ordinal() )  // Balance
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0050, mod200old.getDoubleValue(Mod2002024Key.C0050))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0051, mod200old.getDoubleValue(Mod2002024Key.C0051))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0052, mod200old.getDoubleValue(Mod2002024Key.C0052))

			,(mod200old,mod200new) -> mod200new.setEcpnType( mod200old.getEcpnType().ordinal() )        // ECPN
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0075, mod200old.getDoubleValue(Mod2002024Key.C0075))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0076, mod200old.getDoubleValue(Mod2002024Key.C0076))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0077, mod200old.getDoubleValue(Mod2002024Key.C0077))

			,(mod200old,mod200new) -> mod200new.setPygType(mod200old.getPygType().ordinal() )           // Pérdidas y ganancias 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0053, mod200old.getDoubleValue(Mod2002024Key.C0053))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0054, mod200old.getDoubleValue(Mod2002024Key.C0054))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.C0055, mod200old.getDoubleValue(Mod2002024Key.C0055))
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002025Key.C0061, mod200old.getDoubleValue(Mod2002024Key.C0061))  // Estados de cuentas de Instituciones de inversión colectiva [061]
			
			// PERSONAL ASALARIADO
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002025Key.C0041, mod200old.getDoubleValue(Mod2002024Key.C0041))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002025Key.C0042, mod200old.getDoubleValue(Mod2002024Key.C0042))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042]
			
		})

		,PAG2  ( new IPropertyFiller[] {
				
			 (mod200old,mod200new) -> mod200new.getAdministrators().addAll(mod200old.getAdministrators())       // A. Relación de administradores			 
			,(mod200old,mod200new) -> mod200new.getParticipationsOut().addAll(mod200old.getParticipationsOut()) // B1. Participaciones de la declarante en otras entidades
			,(mod200old,mod200new) -> mod200new.getParticipationsIn().addAll(mod200old.getParticipationsIn())   // B2. Participaciones de personas o entidades en la declarante
            
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002025Key.POR51, mod200old.getDoubleValue(Mod2002024Key.POR51))  // B2. Suma de porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002025Key.PORES, mod200old.getDoubleValue(Mod2002024Key.PORES))  // B2. Suma de porcentajes de participaciones en situaciones especiales
			
		})
		
		,PAG2BIS  ( new IPropertyFiller[] {
				
		     (mod200old,mod200new) -> mod200new.getMinorEntities().addAll(mod200old.getMinorEntities())   // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
		    ,(mod200old,mod200new) -> mod200new.getUteForeign().addAll(mod200old.getUteForeign())         // D. Información de detalle de EP que opere en el extranjero
		    
		    // E. Socios de SICAV en régimen especial de disolución y liquidación (DT 41ª LIS)
		    ,(mod200old,mod200new) -> mod200new.getSicav1().addAll(mod200old.getSicav1())  
		    ,(mod200old,mod200new) -> mod200new.getSicav2().addAll(mod200old.getSicav2())
		    
		    // F. Identificación del titular real de la entidad
		    ,(mod200old,mod200new) -> mod200new.getTitularReal().addAll(mod200old.getTitularReal())
		    
			// G. Secretario del Consejo de Administración y representantes legales de la entidad
            ,(mod200old,mod200new) -> mod200new.getSecretary().setName(mod200old.getSecretary().getName())              // Apellidos y Nombre - Secretario del Consejo de Administración 
			,(mod200old,mod200new) -> mod200new.getSecretary().setDocument(mod200old.getSecretary().getDocument())      // N.I.F. - Secretario del Consejo de Administración
			,(mod200old,mod200new) -> mod200new.getRepresentatives().addAll(mod200old.getRepresentatives())  			// Declaración representantes legales entidad
		    
		})		
		
		,PAG14 ( new IPropertyFiller[] {
				
			// Se pone por defecto el tipo de gravamen del año pasado, cuando se calcule el modelo, ya se modificará si es necesario
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ558, mod200old.getDoubleValue(Mod2002024Key.LQ558))  // Tipo de Gravamen
			
		})

		,PAG15 ( new IPropertyFiller[] {
				
		    // Compensación bases imponibles negativas
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ640 , mod200old.getDoubleValue(Mod2002024Key.LQ548 )) // 1997
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ643 , mod200old.getDoubleValue(Mod2002024Key.LQ645 )) // 1998
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ646 , mod200old.getDoubleValue(Mod2002024Key.LQ648 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ649 , mod200old.getDoubleValue(Mod2002024Key.LQ651 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ652 , mod200old.getDoubleValue(Mod2002024Key.LQ654 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ655 , mod200old.getDoubleValue(Mod2002024Key.LQ657 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ658 , mod200old.getDoubleValue(Mod2002024Key.LQ660 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ661 , mod200old.getDoubleValue(Mod2002024Key.LQ663 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ664 , mod200old.getDoubleValue(Mod2002024Key.LQ666 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ667 , mod200old.getDoubleValue(Mod2002024Key.LQ669 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ743 , mod200old.getDoubleValue(Mod2002024Key.LQ748 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ275 , mod200old.getDoubleValue(Mod2002024Key.LQ277 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ608 , mod200old.getDoubleValue(Mod2002024Key.LQ610 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ704 , mod200old.getDoubleValue(Mod2002024Key.LQ706 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ013 , mod200old.getDoubleValue(Mod2002024Key.LQ015 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ725 , mod200old.getDoubleValue(Mod2002024Key.LQ727 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ534 , mod200old.getDoubleValue(Mod2002024Key.LQ536 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ607 , mod200old.getDoubleValue(Mod2002024Key.LQ699 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1045, mod200old.getDoubleValue(Mod2002024Key.LQ1047)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1519, mod200old.getDoubleValue(Mod2002024Key.LQ1521)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1592, mod200old.getDoubleValue(Mod2002024Key.LQ1594)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1825, mod200old.getDoubleValue(Mod2002024Key.LQ1827)) // 2018			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2193, mod200old.getDoubleValue(Mod2002024Key.LQ2195)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ194 , mod200old.getDoubleValue(Mod2002024Key.LQ196 )) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ151 , mod200old.getDoubleValue(Mod2002024Key.LQ164 )) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ896 , mod200old.getDoubleValue(Mod2002024Key.LQ898 )) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ009 , mod200old.getDoubleValue(Mod2002024Key.LQ020 )) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ3402, mod200old.getDoubleValue(Mod2002024Key.LQ2318)+ // 2024
					                                                                   mod200old.getDoubleValue(Mod2002024Key.LQ1049)+
					                                                                  (mod200old.getDoubleValue(Mod2002024Key.C0017)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002024Key.C0018)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002024Key.C0019)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002024Key.LQ552)<0
                                                                                      && mod200old.getDoubleValue(Mod2002024Key.LQ1049)==0
                                                                                       ?Math.abs(mod200old.getDoubleValue(Mod2002024Key.LQ552)) : 0) )  
																						// Si la base imponible (casilla 552) del 2024 era negativa, tambien se suma a esta casilla (si no es cooperativa) y si no estaba indicada en la 1049
			
			// Régimen especial de buques y empresas navieras en Canarias: desglose de la compensación de bases imponibles negativas
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ168 , mod200old.getDoubleValue(Mod2002024Key.LQ173 )) // 2021 especial
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ175 , mod200old.getDoubleValue(Mod2002024Key.LQ177 )) // 2021 resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ178 , mod200old.getDoubleValue(Mod2002024Key.LQ198 )) // 2022 especial
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ202 , mod200old.getDoubleValue(Mod2002024Key.LQ215 )) // 2022 resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ987 , mod200old.getDoubleValue(Mod2002024Key.LQ989 )) // 2023 especial			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1010, mod200old.getDoubleValue(Mod2002024Key.LQ1200)) // 2023 resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ033 , mod200old.getDoubleValue(Mod2002024Key.LQ091)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.LQ267))  // 2024 especial			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ092 , mod200old.getDoubleValue(Mod2002024Key.LQ098)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.LQ344))  // 2024 resto
					
		})
		
		,PAG15BIS ( new IPropertyFiller[] {
				
				// Deducciones por doble imposición interna RDL 4/2004
				 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN104, adjustDoubleTax( // 2008
						mod200old.getDoubleValue(Mod2002024Key.BN848), mod200old.getDoubleValue(Mod2002024Key.BN105), mod200old.getDoubleValue(Mod2002024Key.BN103A) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN106, adjustDoubleTax( // 2009
						mod200old.getDoubleValue(Mod2002024Key.BN284), mod200old.getDoubleValue(Mod2002024Key.BN107), mod200old.getDoubleValue(Mod2002024Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN108, adjustDoubleTax( // 2010
						mod200old.getDoubleValue(Mod2002024Key.BN707), mod200old.getDoubleValue(Mod2002024Key.BN109), mod200old.getDoubleValue(Mod2002024Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN110, adjustDoubleTax( // 2011
						mod200old.getDoubleValue(Mod2002024Key.BN300), mod200old.getDoubleValue(Mod2002024Key.BN111), mod200old.getDoubleValue(Mod2002024Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN112, adjustDoubleTax( // 2012
						mod200old.getDoubleValue(Mod2002024Key.BN027), mod200old.getDoubleValue(Mod2002024Key.BN113), mod200old.getDoubleValue(Mod2002024Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN114, adjustDoubleTax( // 2013
						mod200old.getDoubleValue(Mod2002024Key.BN716), mod200old.getDoubleValue(Mod2002024Key.BN115), mod200old.getDoubleValue(Mod2002024Key.BN103A) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN735, adjustDoubleTax( // 2014
						mod200old.getDoubleValue(Mod2002024Key.BN738), mod200old.getDoubleValue(Mod2002024Key.BN920), mod200old.getDoubleValue(Mod2002024Key.BN103A) ) )

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN105 , mod200old.getDoubleValue(Mod2002024Key.BN105)) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN107 , mod200old.getDoubleValue(Mod2002024Key.BN107)) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN109 , mod200old.getDoubleValue(Mod2002024Key.BN109)) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN111 , mod200old.getDoubleValue(Mod2002024Key.BN111)) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN113 , mod200old.getDoubleValue(Mod2002024Key.BN113)) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN115 , mod200old.getDoubleValue(Mod2002024Key.BN115)) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN920 , mod200old.getDoubleValue(Mod2002024Key.BN920)) // 2014

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN846 , mod200old.getDoubleValue(Mod2002024Key.BN848)) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN282 , mod200old.getDoubleValue(Mod2002024Key.BN284)) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN702 , mod200old.getDoubleValue(Mod2002024Key.BN707)) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN071 , mod200old.getDoubleValue(Mod2002024Key.BN300)) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN025 , mod200old.getDoubleValue(Mod2002024Key.BN027)) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN714 , mod200old.getDoubleValue(Mod2002024Key.BN716)) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN736 , mod200old.getDoubleValue(Mod2002024Key.BN738)) // 2014
				                                                                          
				// Deducciones por doble imposición interna (DT 23ª.1 LIS)
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN101 , adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN121 ), mod200old.getDoubleValue(Mod2002024Key.BN102 ), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2015
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN122 , adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN126 ), mod200old.getDoubleValue(Mod2002024Key.BN123 ), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2016
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1595, adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN1599), mod200old.getDoubleValue(Mod2002024Key.BN1596), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2017
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1828, adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN1832), mod200old.getDoubleValue(Mod2002024Key.BN1829), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2018
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2196, adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN2200), mod200old.getDoubleValue(Mod2002024Key.BN2197), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2019
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2319, adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN2323), mod200old.getDoubleValue(Mod2002024Key.BN2320), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2020
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN199 , adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN206 ), mod200old.getDoubleValue(Mod2002024Key.BN203 ), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2021
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN394 , adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN2076), mod200old.getDoubleValue(Mod2002024Key.BN436 ), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2022
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1270, adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN1360), mod200old.getDoubleValue(Mod2002024Key.BN1271), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2023
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN467 , adjustDoubleTax( mod200old.getDoubleValue(Mod2002024Key.BN261 )+
						                                                                                    mod200old.getDoubleValue(Mod2002024Key.BN129 ), mod200old.getDoubleValue(Mod2002024Key.BN586 ), mod200old.getDoubleValue(Mod2002024Key.BN103B) ) ) // 2024
				
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN102 , mod200old.getDoubleValue(Mod2002024Key.BN102) ) // 2015
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN123 , mod200old.getDoubleValue(Mod2002024Key.BN123) ) // 2016
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1596, mod200old.getDoubleValue(Mod2002024Key.BN1596)) // 2017
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1829, mod200old.getDoubleValue(Mod2002024Key.BN1829)) // 2018
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2197, mod200old.getDoubleValue(Mod2002024Key.BN2197)) // 2019
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2320, mod200old.getDoubleValue(Mod2002024Key.BN2320)) // 2020
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN203 , mod200old.getDoubleValue(Mod2002024Key.BN203) ) // 2021
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN436 , mod200old.getDoubleValue(Mod2002024Key.BN436) ) // 2022
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1271, mod200old.getDoubleValue(Mod2002024Key.BN1271)) // 2023
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN586 , mod200old.getDoubleValue(Mod2002024Key.BN586 )) // 2024

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN119 , mod200old.getDoubleValue(Mod2002024Key.BN121) ) // 2015
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN124 , mod200old.getDoubleValue(Mod2002024Key.BN126) ) // 2016
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1597, mod200old.getDoubleValue(Mod2002024Key.BN1599)) // 2017
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1830, mod200old.getDoubleValue(Mod2002024Key.BN1832)) // 2018
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2198, mod200old.getDoubleValue(Mod2002024Key.BN2200)) // 2019
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2321, mod200old.getDoubleValue(Mod2002024Key.BN2323)) // 2020
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN204 , mod200old.getDoubleValue(Mod2002024Key.BN206) ) // 2021
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN437 , mod200old.getDoubleValue(Mod2002024Key.BN2076)) // 2022
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1299, mod200old.getDoubleValue(Mod2002024Key.BN1360)) // 2023
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN259 , mod200old.getDoubleValue(Mod2002024Key.BN261 )+mod200old.getDoubleValue(Mod2002024Key.BN129)) // 2024
				
				// Deducciones por doble imposición internacional RDLeg 4/2004
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN153, adjustDoubleTax(  // 2005
						mod200old.getDoubleValue(Mod2002024Key.BN639), mod200old.getDoubleValue(Mod2002024Key.BN728), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN154, adjustDoubleTax( // 2006
						mod200old.getDoubleValue(Mod2002024Key.BN197), mod200old.getDoubleValue(Mod2002024Key.BN729), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN155, adjustDoubleTax( // 2007
						mod200old.getDoubleValue(Mod2002024Key.BN287), mod200old.getDoubleValue(Mod2002024Key.BN730), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN156, adjustDoubleTax( // 2008
						mod200old.getDoubleValue(Mod2002024Key.BN827), mod200old.getDoubleValue(Mod2002024Key.BN731), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN157, adjustDoubleTax( // 2009
						mod200old.getDoubleValue(Mod2002024Key.BN003), mod200old.getDoubleValue(Mod2002024Key.BN732), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN158, adjustDoubleTax( // 2010
						mod200old.getDoubleValue(Mod2002024Key.BN030), mod200old.getDoubleValue(Mod2002024Key.BN733), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN159, adjustDoubleTax( // 2011
						mod200old.getDoubleValue(Mod2002024Key.BN719), mod200old.getDoubleValue(Mod2002024Key.BN734), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN720, adjustDoubleTax( // 2012
						mod200old.getDoubleValue(Mod2002024Key.BN724), mod200old.getDoubleValue(Mod2002024Key.BN721), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN739, adjustDoubleTax( // 2013
						mod200old.getDoubleValue(Mod2002024Key.BN742), mod200old.getDoubleValue(Mod2002024Key.BN921), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN134, adjustDoubleTax( // 2014
						mod200old.getDoubleValue(Mod2002024Key.BN137), mod200old.getDoubleValue(Mod2002024Key.BN926), mod200old.getDoubleValue(Mod2002024Key.BN103C) ))
				
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN728 , mod200old.getDoubleValue(Mod2002024Key.BN728 )) // 2005 - Tipo de gravamen
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN729 , mod200old.getDoubleValue(Mod2002024Key.BN729 )) // 2006
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN730 , mod200old.getDoubleValue(Mod2002024Key.BN730 )) // 2007
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN731 , mod200old.getDoubleValue(Mod2002024Key.BN731 )) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN732 , mod200old.getDoubleValue(Mod2002024Key.BN732 )) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN733 , mod200old.getDoubleValue(Mod2002024Key.BN733 )) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN734 , mod200old.getDoubleValue(Mod2002024Key.BN734 )) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN721 , mod200old.getDoubleValue(Mod2002024Key.BN721 )) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN921 , mod200old.getDoubleValue(Mod2002024Key.BN921 )) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN926 , mod200old.getDoubleValue(Mod2002024Key.BN926 )) // 2014

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN637 , mod200old.getDoubleValue(Mod2002024Key.BN639 )) // 2005 - Deducción pendiente
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN849 , mod200old.getDoubleValue(Mod2002024Key.BN197 )) // 2006
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN285 , mod200old.getDoubleValue(Mod2002024Key.BN287 )) // 2007
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN825 , mod200old.getDoubleValue(Mod2002024Key.BN827 )) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN001 , mod200old.getDoubleValue(Mod2002024Key.BN003 )) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN028 , mod200old.getDoubleValue(Mod2002024Key.BN030 )) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN717 , mod200old.getDoubleValue(Mod2002024Key.BN719 )) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN722 , mod200old.getDoubleValue(Mod2002024Key.BN724 )) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN740 , mod200old.getDoubleValue(Mod2002024Key.BN742 )) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN135 , mod200old.getDoubleValue(Mod2002024Key.BN137 )) // 2014
				
		})
		
		,PAG16 ( new IPropertyFiller[] {
			
			// Deducciones por doble imposición internacional LIS
 			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1054, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN1053), mod200old.getDoubleValue(Mod2002024Key.BN1050), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1348, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN1352), mod200old.getDoubleValue(Mod2002024Key.BN1349), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1770, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN1774), mod200old.getDoubleValue(Mod2002024Key.BN1771), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1833, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN1837), mod200old.getDoubleValue(Mod2002024Key.BN1834), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2201, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN2205), mod200old.getDoubleValue(Mod2002024Key.BN2202), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2324, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN2328), mod200old.getDoubleValue(Mod2002024Key.BN2325), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN207 , adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN213 ), mod200old.getDoubleValue(Mod2002024Key.BN208 ), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN490 , adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN620 ), mod200old.getDoubleValue(Mod2002024Key.BN491 ), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1361, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN1505), mod200old.getDoubleValue(Mod2002024Key.BN1362), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1013, adjustDoubleTax(mod200old.getDoubleValue(Mod2002024Key.BN288 )+
					                                                                                   mod200old.getDoubleValue(Mod2002024Key.BN174 ), mod200old.getDoubleValue(Mod2002024Key.BN254 ), mod200old.getDoubleValue(Mod2002024Key.BN103D) )) // 2024
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1050, mod200old.getDoubleValue(Mod2002024Key.BN1050)) // 2015 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1349, mod200old.getDoubleValue(Mod2002024Key.BN1349)) // 2016 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1771, mod200old.getDoubleValue(Mod2002024Key.BN1771)) // 2017 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1834, mod200old.getDoubleValue(Mod2002024Key.BN1834)) // 2018 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2202, mod200old.getDoubleValue(Mod2002024Key.BN2202)) // 2019 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2325, mod200old.getDoubleValue(Mod2002024Key.BN2325)) // 2020 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN208 , mod200old.getDoubleValue(Mod2002024Key.BN208 )) // 2021 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN491 , mod200old.getDoubleValue(Mod2002024Key.BN491 )) // 2022 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1362, mod200old.getDoubleValue(Mod2002024Key.BN1362)) // 2023 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN254 , mod200old.getDoubleValue(Mod2002024Key.BN254 )) // 2024 - Tipo de gravamen
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1051, mod200old.getDoubleValue(Mod2002024Key.BN1053)) // 2015 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1350, mod200old.getDoubleValue(Mod2002024Key.BN1352)) // 2016 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1772, mod200old.getDoubleValue(Mod2002024Key.BN1774)) // 2017 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1835, mod200old.getDoubleValue(Mod2002024Key.BN1837)) // 2018 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2203, mod200old.getDoubleValue(Mod2002024Key.BN2205)) // 2098 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2326, mod200old.getDoubleValue(Mod2002024Key.BN2328)) // 2020 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN209 , mod200old.getDoubleValue(Mod2002024Key.BN213 )) // 2021 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN492 , mod200old.getDoubleValue(Mod2002024Key.BN620 )) // 2022 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1457, mod200old.getDoubleValue(Mod2002024Key.BN1505)) // 2023 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN255 , mod200old.getDoubleValue(Mod2002024Key.BN288 )+mod200old.getDoubleValue(Mod2002024Key.BN174)) // 2024 - Deducción pendiente
										
			// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN040 , mod200old.getDoubleValue(Mod2002024Key.BN042 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN138 , mod200old.getDoubleValue(Mod2002024Key.BN140 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN141 , mod200old.getDoubleValue(Mod2002024Key.BN143 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN188 , mod200old.getDoubleValue(Mod2002024Key.BN190 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN803 , mod200old.getDoubleValue(Mod2002024Key.BN805 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1055, mod200old.getDoubleValue(Mod2002024Key.BN1057)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN700 , mod200old.getDoubleValue(Mod2002024Key.BN709 )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1353, mod200old.getDoubleValue(Mod2002024Key.BN1355)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1775, mod200old.getDoubleValue(Mod2002024Key.BN1777)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1838, mod200old.getDoubleValue(Mod2002024Key.BN1840)) // 2019			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2206, mod200old.getDoubleValue(Mod2002024Key.BN2208)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2329, mod200old.getDoubleValue(Mod2002024Key.BN2331)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN249 , mod200old.getDoubleValue(Mod2002024Key.BN253 )) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN696 , mod200old.getDoubleValue(Mod2002024Key.BN710 )) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1515, mod200old.getDoubleValue(Mod2002024Key.BN292)+mod200old.getDoubleValue(Mod2002024Key.BN1571)) // 2024
			
			// Deducciones disposición transitoria 24ª.1 LIS		
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN749 , mod200old.getDoubleValue(Mod2002024Key.BN754)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN752 , mod200old.getDoubleValue(Mod2002024Key.BN757)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN755 , mod200old.getDoubleValue(Mod2002024Key.BN760)) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN758 , mod200old.getDoubleValue(Mod2002024Key.BN763)) // 2023			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN761 , mod200old.getDoubleValue(Mod2002024Key.BN746)+mod200old.getDoubleValue(Mod2002024Key.BN784)) // 2024
			
		})
        
		,PAG16BIS ( new IPropertyFiller[] {
			
			// Deducciones inversión en Canarias - Activos fijos (Ley 20/1991)
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN854 , mod200old.getDoubleValue(Mod2002024Key.BN1356)) // 2010 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN857 , mod200old.getDoubleValue(Mod2002024Key.BN859 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN860 , mod200old.getDoubleValue(Mod2002024Key.BN862 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN863 , mod200old.getDoubleValue(Mod2002024Key.BN865 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN883 , mod200old.getDoubleValue(Mod2002024Key.BN885 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN785 , mod200old.getDoubleValue(Mod2002024Key.BN790 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1357, mod200old.getDoubleValue(Mod2002024Key.BN1359)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1778, mod200old.getDoubleValue(Mod2002024Key.BN1780)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN852 , mod200old.getDoubleValue(Mod2002024Key.BN856 )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2116, mod200old.getDoubleValue(Mod2002024Key.BN2118)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2209, mod200old.getDoubleValue(Mod2002024Key.BN2211)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2332, mod200old.getDoubleValue(Mod2002024Key.BN2334)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN237 , mod200old.getDoubleValue(Mod2002024Key.BN239 )) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN711 , mod200old.getDoubleValue(Mod2002024Key.BN2077)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1614, mod200old.getDoubleValue(Mod2002024Key.BN264)+mod200old.getDoubleValue(Mod2002024Key.BN1616)) // 2024
			
			// Deducciones inversión en Canarias - Activos fijos en La Palma, La Gomera y El Hierro
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2335, mod200old.getDoubleValue(Mod2002024Key.BN2337)) // 2018 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2338, mod200old.getDoubleValue(Mod2002024Key.BN2340)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2341, mod200old.getDoubleValue(Mod2002024Key.BN2343)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2344, mod200old.getDoubleValue(Mod2002024Key.BN2346)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN244 , mod200old.getDoubleValue(Mod2002024Key.BN2497)) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2078, mod200old.getDoubleValue(Mod2002024Key.BN766 )) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1763, mod200old.getDoubleValue(Mod2002024Key.BN270)+mod200old.getDoubleValue(Mod2002024Key.BN1801)) // 2024
			
			// Deducciones inversión en Canarias - Inversiones en Canarias (Ley 20/1991)			 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN191 , mod200old.getDoubleValue(Mod2002024Key.BN193 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN613 , mod200old.getDoubleValue(Mod2002024Key.BN701 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN200 , mod200old.getDoubleValue(Mod2002024Key.BN011 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN037 , mod200old.getDoubleValue(Mod2002024Key.BN039 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN044 , mod200old.getDoubleValue(Mod2002024Key.BN046 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN528 , mod200old.getDoubleValue(Mod2002024Key.BN530 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN144 , mod200old.getDoubleValue(Mod2002024Key.BN146 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN147 , mod200old.getDoubleValue(Mod2002024Key.BN149 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN240 , mod200old.getDoubleValue(Mod2002024Key.BN242 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1058, mod200old.getDoubleValue(Mod2002024Key.BN1060)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN791 , mod200old.getDoubleValue(Mod2002024Key.BN806 )) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1781, mod200old.getDoubleValue(Mod2002024Key.BN1783)) // 2018            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2122, mod200old.getDoubleValue(Mod2002024Key.BN2124)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2212, mod200old.getDoubleValue(Mod2002024Key.BN2214)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2347, mod200old.getDoubleValue(Mod2002024Key.BN2349)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN217 , mod200old.getDoubleValue(Mod2002024Key.BN219 )) // 2022
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN767 , mod200old.getDoubleValue(Mod2002024Key.BN769 )) // 2023
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1802, mod200old.getDoubleValue(Mod2002024Key.BN274)+mod200old.getDoubleValue(Mod2002024Key.BN1804)) // 2024
            
            // Deducciones inversión en Canarias - Inversiones en La Palma, La Gomera y El Hierro
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2119, mod200old.getDoubleValue(Mod2002024Key.BN2121)) // 2018 
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2125, mod200old.getDoubleValue(Mod2002024Key.BN2127)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2215, mod200old.getDoubleValue(Mod2002024Key.BN2217)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2350, mod200old.getDoubleValue(Mod2002024Key.BN2352)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN220 , mod200old.getDoubleValue(Mod2002024Key.BN222 )) // 2022
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN770 , mod200old.getDoubleValue(Mod2002024Key.BN774 )) // 2023
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1805, mod200old.getDoubleValue(Mod2002024Key.BN296)+mod200old.getDoubleValue(Mod2002024Key.BN1847)) // 2024
            
		})
// AQUI ESTAMOS			
		,PAG17_18_18BIS ( new IPropertyFiller[] {
				
		    // Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)		
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN472 , mod200old.getDoubleValue(Mod2002024Key.BN478 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN180 , mod200old.getDoubleValue(Mod2002024Key.BN182 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN531 , mod200old.getDoubleValue(Mod2002024Key.BN533 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN945 , mod200old.getDoubleValue(Mod2002024Key.BN947 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN960 , mod200old.getDoubleValue(Mod2002024Key.BN962 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN183 , mod200old.getDoubleValue(Mod2002024Key.BN186 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN966 , mod200old.getDoubleValue(Mod2002024Key.BN968 )) // 2013 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN457 , mod200old.getDoubleValue(Mod2002024Key.BN459 )) // 2013 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN460 , mod200old.getDoubleValue(Mod2002024Key.BN462 )) // 2013 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1063, mod200old.getDoubleValue(Mod2002024Key.BN1065)) // 2014 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1066, mod200old.getDoubleValue(Mod2002024Key.BN1068)) // 2014 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1069, mod200old.getDoubleValue(Mod2002024Key.BN1071)) // 2014 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2294, mod200old.getDoubleValue(Mod2002024Key.BN2296)) // 2015 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN986 , mod200old.getDoubleValue(Mod2002024Key.BN507 )) // 2015 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN557 , mod200old.getDoubleValue(Mod2002024Key.BN594 )) // 2015 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2081, mod200old.getDoubleValue(Mod2002024Key.BN2083)) // 2015 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2297, mod200old.getDoubleValue(Mod2002024Key.BN2299)) // 2016 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1617, mod200old.getDoubleValue(Mod2002024Key.BN1619)) // 2016 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1620, mod200old.getDoubleValue(Mod2002024Key.BN1622)) // 2016 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2084, mod200old.getDoubleValue(Mod2002024Key.BN2086)) // 2016 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2300, mod200old.getDoubleValue(Mod2002024Key.BN2087)) // 2017 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1850, mod200old.getDoubleValue(Mod2002024Key.BN1852)) // 2017 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1853, mod200old.getDoubleValue(Mod2002024Key.BN1855)) // 2017 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2088, mod200old.getDoubleValue(Mod2002024Key.BN2090)) // 2017 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2091, mod200old.getDoubleValue(Mod2002024Key.BN2093)) // 2018 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2221, mod200old.getDoubleValue(Mod2002024Key.BN2223)) // 2018 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2224, mod200old.getDoubleValue(Mod2002024Key.BN2226)) // 2018 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1916, mod200old.getDoubleValue(Mod2002024Key.BN1918)) // 2018 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2094, mod200old.getDoubleValue(Mod2002024Key.BN2096)) // 2019 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2356, mod200old.getDoubleValue(Mod2002024Key.BN2358)) // 2019 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2359, mod200old.getDoubleValue(Mod2002024Key.BN2361)) // 2019 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1919, mod200old.getDoubleValue(Mod2002024Key.BN1921)) // 2019 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2097, mod200old.getDoubleValue(Mod2002024Key.BN2099)) // 2020 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN228 , mod200old.getDoubleValue(Mod2002024Key.BN230) ) // 2020 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN234 , mod200old.getDoubleValue(Mod2002024Key.BN236) ) // 2020 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1922, mod200old.getDoubleValue(Mod2002024Key.BN1924)) // 2020 TAP			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2145, mod200old.getDoubleValue(Mod2002024Key.BN2448)) // 2021 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN780 , mod200old.getDoubleValue(Mod2002024Key.BN782 )) // 2021 CT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN786 , mod200old.getDoubleValue(Mod2002024Key.BN788 )) // 2021 IT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1925, mod200old.getDoubleValue(Mod2002024Key.BN1927)) // 2021 TAP			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1848, mod200old.getDoubleValue(Mod2002024Key.BN1873)) // 2022 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1874, mod200old.getDoubleValue(Mod2002024Key.BN1876)) // 2022 CT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1894, mod200old.getDoubleValue(Mod2002024Key.BN1896)) // 2022 IT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1897, mod200old.getDoubleValue(Mod2002024Key.BN1899)) // 2022 TAP
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN293 , mod200old.getDoubleValue(Mod2002024Key.BN407 )) // 2023 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN419 , mod200old.getDoubleValue(Mod2002024Key.BN527 )) // 2023 CT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN540 , mod200old.getDoubleValue(Mod2002024Key.BN595 )) // 2023 IT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN870 , mod200old.getDoubleValue(Mod2002024Key.BN881 )) // 2023 TAP
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN3436, mod200old.getDoubleValue(Mod2002024Key.BN2461)+ // 2024 excepto I+D+i y TAP
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN830)+					                                                                   
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN809)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN2464)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1077)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN2457)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN797)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN794)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN889)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1371)+					                                                                   
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN2367)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN2373)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN354)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN429)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN435)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN537)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN2460)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN877)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1870)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN956)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1088)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1141)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1145)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1151)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1154)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1157)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1195)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1208)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1222)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1232)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1236)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1239)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1263)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1266)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1269)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1274)+
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN1283)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN1902)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN1976)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN1999)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2028)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2031)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2034)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2037)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2040)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2043)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2054)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2280)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN2286)+
																					   mod200old.getDoubleValue(Mod2002024Key.BN090))	
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN3439, mod200old.getDoubleValue(Mod2002024Key.BN1365)+  // 2024 CT
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN800 ))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN3442, mod200old.getDoubleValue(Mod2002024Key.BN1368)+  // 2024 IT
					                                                                   mod200old.getDoubleValue(Mod2002024Key.BN713 ))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN3445, mod200old.getDoubleValue(Mod2002024Key.BN1930)+  // 2024 TAP
                                                                                       mod200old.getDoubleValue(Mod2002024Key.BN2192))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2449, mod200old.getDoubleValue(Mod2002024Key.BN1685))  // 2025
			
			// Deducción por inversiones y gastos realizados por las autoridades portuarias (art. 38 bis LIS)
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1284, mod200old.getDoubleValue(Mod2002024Key.BN1288))  // 2020 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1289, mod200old.getDoubleValue(Mod2002024Key.BN1291))  // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1292, mod200old.getDoubleValue(Mod2002024Key.BN1294))  // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1295, mod200old.getDoubleValue(Mod2002024Key.BN1297))  // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2312, mod200old.getDoubleValue(Mod2002024Key.BN299 )+  //  
                    																   mod200old.getDoubleValue(Mod2002024Key.BN2353))  // 2024
						
			// Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS)
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1931, mod200old.getDoubleValue(Mod2002024Key.BN1937))  // 2015 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1938, mod200old.getDoubleValue(Mod2002024Key.BN1941))  // 2016 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1942, mod200old.getDoubleValue(Mod2002024Key.BN1945))  // 2017 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1946, mod200old.getDoubleValue(Mod2002024Key.BN1949))  // 2018 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2109, mod200old.getDoubleValue(Mod2002024Key.BN2112))  // 2019 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2128, mod200old.getDoubleValue(Mod2002024Key.BN2131))  // 2020 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2132, mod200old.getDoubleValue(Mod2002024Key.BN2135))  // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2136, mod200old.getDoubleValue(Mod2002024Key.BN2139))  // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2140, mod200old.getDoubleValue(Mod2002024Key.BN2143))  // 2023 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2354, mod200old.getDoubleValue(Mod2002024Key.BN320 )+  //  
			                                                                           mod200old.getDoubleValue(Mod2002024Key.BN2443))  // 2024 

			// Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994)
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2148, mod200old.getDoubleValue(Mod2002024Key.BN2151))  // 2015 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2152, mod200old.getDoubleValue(Mod2002024Key.BN2155))  // 2016 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2156, mod200old.getDoubleValue(Mod2002024Key.BN2159))  // 2017 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2160, mod200old.getDoubleValue(Mod2002024Key.BN2163))  // 2018 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2164, mod200old.getDoubleValue(Mod2002024Key.BN2167))  // 2019 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2168, mod200old.getDoubleValue(Mod2002024Key.BN2171))  // 2020 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2172, mod200old.getDoubleValue(Mod2002024Key.BN2175))  // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1309, mod200old.getDoubleValue(Mod2002024Key.BN1312))  // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1313, mod200old.getDoubleValue(Mod2002024Key.BN1316))  // 2023 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2445, mod200old.getDoubleValue(Mod2002024Key.BN367 )+  //  
			                                                                           mod200old.getDoubleValue(Mod2002024Key.BN2468))  // 2024 
			
		})
			
		,PAG18TER ( new IPropertyFiller[] {
				
			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002 - Donaciones de carácter general

			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN246 , mod200old.getDoubleValue(Mod2002024Key.BN248) ) // 2015 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN818 , mod200old.getDoubleValue(Mod2002024Key.BN820) ) // 2015 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN993 , mod200old.getDoubleValue(Mod2002024Key.BN995) ) // 2016 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN821 , mod200old.getDoubleValue(Mod2002024Key.BN834) ) // 2016 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1434, mod200old.getDoubleValue(Mod2002024Key.BN1436)) // 2017 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN835 , mod200old.getDoubleValue(Mod2002024Key.BN837) ) // 2017 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1718, mod200old.getDoubleValue(Mod2002024Key.BN1720)) // 2018 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN838 , mod200old.getDoubleValue(Mod2002024Key.BN840) ) // 2018 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1950, mod200old.getDoubleValue(Mod2002024Key.BN1952)) // 2019 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN842 , mod200old.getDoubleValue(Mod2002024Key.BN845) ) // 2019 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2227, mod200old.getDoubleValue(Mod2002024Key.BN2229)) // 2020 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN868 , mod200old.getDoubleValue(Mod2002024Key.BN871) ) // 2020 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2380, mod200old.getDoubleValue(Mod2002024Key.BN2382)) // 2021 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN872 , mod200old.getDoubleValue(Mod2002024Key.BN2498)) // 2021 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2499, mod200old.getDoubleValue(Mod2002024Key.BN890) ) // 2022 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN891 , mod200old.getDoubleValue(Mod2002024Key.BN893) ) // 2022 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1323, mod200old.getDoubleValue(Mod2002024Key.BN1325)) // 2023 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1326, mod200old.getDoubleValue(Mod2002024Key.BN1328)) // 2023 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN369 , mod200old.getDoubleValue(Mod2002024Key.BN401)+mod200old.getDoubleValue(Mod2002024Key.BN2577)) // 2024 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN405 , mod200old.getDoubleValue(Mod2002024Key.BN423)+mod200old.getDoubleValue(Mod2002024Key.BN2693)) // 2024 Con			

			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002 - Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN933 , mod200old.getDoubleValue(Mod2002024Key.BN942) ) // 2015 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN943 , mod200old.getDoubleValue(Mod2002024Key.BN948) ) // 2015 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN949 , mod200old.getDoubleValue(Mod2002024Key.BN951) ) // 2016 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN952 , mod200old.getDoubleValue(Mod2002024Key.BN954) ) // 2016 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2472, mod200old.getDoubleValue(Mod2002024Key.BN2474)) // 2017 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN958 , mod200old.getDoubleValue(Mod2002024Key.BN963) ) // 2017 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN964 , mod200old.getDoubleValue(Mod2002024Key.BN969) ) // 2018 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN970 , mod200old.getDoubleValue(Mod2002024Key.BN972) ) // 2018 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN973 , mod200old.getDoubleValue(Mod2002024Key.BN979) ) // 2019 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN980 , mod200old.getDoubleValue(Mod2002024Key.BN982) ) // 2019 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN983 , mod200old.getDoubleValue(Mod2002024Key.BN985) ) // 2020 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1000, mod200old.getDoubleValue(Mod2002024Key.BN1007)) // 2020 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1008, mod200old.getDoubleValue(Mod2002024Key.BN1024)) // 2021 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1025, mod200old.getDoubleValue(Mod2002024Key.BN1036)) // 2021 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1061, mod200old.getDoubleValue(Mod2002024Key.BN1072)) // 2022 Sin    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1073, mod200old.getDoubleValue(Mod2002024Key.BN1078)) // 2022 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1329, mod200old.getDoubleValue(Mod2002024Key.BN1373)) // 2023 Sin    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1374, mod200old.getDoubleValue(Mod2002024Key.BN1376)) // 2023 Con	   
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN424, mod200old.getDoubleValue(Mod2002024Key.BN431)+mod200old.getDoubleValue(Mod2002024Key.BN2696)) // 2024 Sin    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN432, mod200old.getDoubleValue(Mod2002024Key.BN440)+mod200old.getDoubleValue(Mod2002024Key.BN2700)) // 2024 Con	   
			
		})
		
		,PAG19 ( new IPropertyFiller[] {
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.1 LIS)
			// Ejercicio 2015 = 2% [01167] = [01166] x 0,02
			// Ejercicio 2016 = 5% [01439] = [01438] x 0,05
			// Ejercicio 2017 = 5% [01443] = [01442] x 0,05
			// Ejercicio 2018 = 5% [01722] = [01721] x 0,05
			// Ejercicio 2019 = 5% [01954] = [01953] x 0,05
			// Ejercicio 2020 = 5% [02231] = [02230] x 0,05
			// Ejercicio 2021 = 5% [02384] = [02383] x 0,05
			// Ejercicio 2022 = 5% [01083] = [01082] x 0,05
			// Ejercicio 2023 = 5% [01378] = [01377] x 0,05 
			
		    // Dado que el importe pendiente está en funcion del importe resultante de aplicar a la base de deduccion un porcentaje, 
			// para obtener ahora esa base de deducción, según el importe pendiente del año anterior, habra que hacer la operacion inversa
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1166, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1169)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1438, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1441)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1442, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1445)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1721, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1724)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1953, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1956)/0.05)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2230, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2233)/0.05)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2383, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2386)/0.05)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1082, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1085)/0.05)) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1377, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1380)/0.05)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2701, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN905 )/0.05)+
					   																   AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2704)/0.05)) // 2024
			
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)                                                                          
			// Ejercicio 2015 = 2%  [01179] = [01178] x 0,02
			// Ejercicio 2016 = 5%  [01448] = [01447] x 0,05
			// Ejercicio 2017 = 5%  [01452] = [01451] x 0,05
			// Ejercicio 2018 = 5%  [01726] = [01725] x 0,05
			// Ejercicio 2019 = 5%  [01958] = [01957] x 0,05
  			// Ejercicio 2020 = 5%  [02235] = [02234] x 0,05
			// Ejercicio 2021 = 5%  [02388] = [02387] x 0,05
			// Ejercicio 2022 = 5%  [02477] = [01086] x 0,05
			// Ejercicio 2024 = 5%  [01382] = [01381] x 0,05
  			                        
  		    // idem apartado anterior
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1178, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1181)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1447, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1450)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1451, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1454)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1725, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1728)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1957, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1960)/0.05)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2234, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2237)/0.05)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2387, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2390)/0.05)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1086, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2479)/0.05)) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN1381, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN1384)/0.05)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.BN2705, AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2071)/0.05)+
       			   																	   AonMathUtils.round(mod200old.getDoubleValue(Mod2002024Key.BN2708)/0.05)) // 2024
			
		})

		,PAG20 ( new IPropertyFiller[] {
				
			// Limitacion en la deducilidad de gastos financieros. Gastos financieros pendientes de deducir
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1188, mod200old.getDoubleValue(Mod2002024Key.LM1191)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1193, mod200old.getDoubleValue(Mod2002024Key.LM1196)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1198, mod200old.getDoubleValue(Mod2002024Key.LM1201)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1202, mod200old.getDoubleValue(Mod2002024Key.LM1205)) // 2015 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1203, mod200old.getDoubleValue(Mod2002024Key.LM1206)) // 2015 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1462, mod200old.getDoubleValue(Mod2002024Key.LM1210)) // 2016 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1463, mod200old.getDoubleValue(Mod2002024Key.LM1211)) // 2016 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1736, mod200old.getDoubleValue(Mod2002024Key.LM1465)) // 2017 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1737, mod200old.getDoubleValue(Mod2002024Key.LM1466)) // 2017 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1977, mod200old.getDoubleValue(Mod2002024Key.LM1739)) // 2018 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1978, mod200old.getDoubleValue(Mod2002024Key.LM1740)) // 2018 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2253, mod200old.getDoubleValue(Mod2002024Key.LM1980)) // 2019 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2254, mod200old.getDoubleValue(Mod2002024Key.LM1981)) // 2019 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2399, mod200old.getDoubleValue(Mod2002024Key.LM2256)) // 2020 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2400, mod200old.getDoubleValue(Mod2002024Key.LM2257)) // 2020 Resto			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1098, mod200old.getDoubleValue(Mod2002024Key.LM2402)) // 2021 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1099, mod200old.getDoubleValue(Mod2002024Key.LM2403)) // 2021 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1393, mod200old.getDoubleValue(Mod2002024Key.LM1101)) // 2022 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1394, mod200old.getDoubleValue(Mod2002024Key.LM1102)) // 2022 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2764, mod200old.getDoubleValue(Mod2002024Key.LM1396)) // 2023 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2765, mod200old.getDoubleValue(Mod2002024Key.LM1397)) // 2023 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2370, mod200old.getDoubleValue(Mod2002024Key.LM2442)+mod200old.getDoubleValue(Mod2002024Key.LM2767)) // 2024 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2409, mod200old.getDoubleValue(Mod2002024Key.LM2444)+mod200old.getDoubleValue(Mod2002024Key.LM2768)) // 2024 Resto
			
			// Pendiente de adición por límite beneficio operativo no aplicado			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2258, mod200old.getDoubleValue(Mod2002024Key.LM2260)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2404, mod200old.getDoubleValue(Mod2002024Key.LM2406)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1103, mod200old.getDoubleValue(Mod2002024Key.LM1105)) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1398, mod200old.getDoubleValue(Mod2002024Key.LM1400)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2769, mod200old.getDoubleValue(Mod2002024Key.LM2972)+mod200old.getDoubleValue(Mod2002024Key.LM2772)) // 2024

		})
			
		,PAG20BIS ( new IPropertyFiller[] {
			
			// Reserva de capitalización
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1401, mod200old.getDoubleValue(Mod2002024Key.LQ1403)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2773, mod200old.getDoubleValue(Mod2002024Key.LQ453)+mod200old.getDoubleValue(Mod2002024Key.LQ2775)) // 2024
			
			// Reserva de nivelación - Reducción en base imponible 	
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2410, mod200old.getDoubleValue(Mod2002024Key.LQ2412)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1109, mod200old.getDoubleValue(Mod2002024Key.LQ1111)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1406, mod200old.getDoubleValue(Mod2002024Key.LQ1407)) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2776, mod200old.getDoubleValue(Mod2002024Key.LQ2779)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ454 , mod200old.getDoubleValue(Mod2002024Key.LQ463)+mod200old.getDoubleValue(Mod2002024Key.LQ1731))  // 2024
			
		})
		
		,PAG20TER ( new IPropertyFiller[] {
				
			// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS  
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1524, mod200old.getDoubleValue(Mod2002024Key.LM1528)) // 2007 y anteriores
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1529, mod200old.getDoubleValue(Mod2002024Key.LM1534)) // 2008 a 2015
				
			// Activos por impuesto diferido (AID). Art. 130 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1542, mod200old.getDoubleValue(Mod2002024Key.LM1549)+mod200old.getDoubleValue(Mod2002024Key.LM1550)+mod200old.getDoubleValue(Mod2002024Key.LM1551)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1552, mod200old.getDoubleValue(Mod2002024Key.LM1558)+mod200old.getDoubleValue(Mod2002024Key.LM1559)+mod200old.getDoubleValue(Mod2002024Key.LM1560)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1754, mod200old.getDoubleValue(Mod2002024Key.LM1760)+mod200old.getDoubleValue(Mod2002024Key.LM1761)+mod200old.getDoubleValue(Mod2002024Key.LM1762)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2100, mod200old.getDoubleValue(Mod2002024Key.LM2106)+mod200old.getDoubleValue(Mod2002024Key.LM2107)+mod200old.getDoubleValue(Mod2002024Key.LM2108)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2268, mod200old.getDoubleValue(Mod2002024Key.LM2274)+mod200old.getDoubleValue(Mod2002024Key.LM2275)+mod200old.getDoubleValue(Mod2002024Key.LM2276)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2418, mod200old.getDoubleValue(Mod2002024Key.LM2424)+mod200old.getDoubleValue(Mod2002024Key.LM2425)+mod200old.getDoubleValue(Mod2002024Key.LM2426)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1117, mod200old.getDoubleValue(Mod2002024Key.LM1131)+mod200old.getDoubleValue(Mod2002024Key.LM1132)+mod200old.getDoubleValue(Mod2002024Key.LM1133)) // 2022
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1414, mod200old.getDoubleValue(Mod2002024Key.LM1420)+mod200old.getDoubleValue(Mod2002024Key.LM1421)+mod200old.getDoubleValue(Mod2002024Key.LM1422)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2786, mod200old.getDoubleValue(Mod2002024Key.LM513 )+mod200old.getDoubleValue(Mod2002024Key.LM812 )+mod200old.getDoubleValue(Mod2002024Key.LM816 )+
                    																   mod200old.getDoubleValue(Mod2002024Key.LM2793)+mod200old.getDoubleValue(Mod2002024Key.LM2794)+mod200old.getDoubleValue(Mod2002024Key.LM2795)) // 2024
			
			// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1423, mod200old.getDoubleValue(Mod2002024Key.LM1469)) // 2023
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1423, mod200old.getDoubleValue(Mod2002024Key.LM1469)+mod200old.getDoubleValue(Mod2002024Key.LM2799)) // 2024
			
		})
		
		,PAG20QUA ( new IPropertyFiller[] {

            // Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
            // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible            
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1473, mod200old.getDoubleValue(Mod2002024Key.LM1476)) // 2007 y anteriores: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1408, mod200old.getDoubleValue(Mod2002024Key.LM1409)) // 2007 y anteriores: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1477, mod200old.getDoubleValue(Mod2002024Key.LM1483)) // 2008 a 2015: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1478, mod200old.getDoubleValue(Mod2002024Key.LM1484)) // 2008 a 2015: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1485, mod200old.getDoubleValue(Mod2002024Key.LM1489)) // 2016: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1486, mod200old.getDoubleValue(Mod2002024Key.LM1490)) // 2016: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1491, mod200old.getDoubleValue(Mod2002024Key.LM1493)) // 2017: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1747, mod200old.getDoubleValue(Mod2002024Key.LM1749)) // 2017: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1750, mod200old.getDoubleValue(Mod2002024Key.LM1752)) // 2018: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1988, mod200old.getDoubleValue(Mod2002024Key.LM1990)) // 2018: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1991, mod200old.getDoubleValue(Mod2002024Key.LM1993)) // 2019: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2261, mod200old.getDoubleValue(Mod2002024Key.LM2263)) // 2019: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2264, mod200old.getDoubleValue(Mod2002024Key.LM2266)) // 2020: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2431, mod200old.getDoubleValue(Mod2002024Key.LM2433)) // 2020: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2434, mod200old.getDoubleValue(Mod2002024Key.LM2436)) // 2021: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1143, mod200old.getDoubleValue(Mod2002024Key.LM1192)) // 2021: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1162, mod200old.getDoubleValue(Mod2002024Key.LM1164)) // 2022: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1470, mod200old.getDoubleValue(Mod2002024Key.LM1915)) // 2022: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1479, mod200old.getDoubleValue(Mod2002024Key.LM1500)) // 2023: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2800, mod200old.getDoubleValue(Mod2002024Key.LM2803)) // 2023: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM2804, mod200old.getDoubleValue(Mod2002024Key.LM1628)+mod200old.getDoubleValue(Mod2002024Key.LM2806)) // 2024: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LM1217, mod200old.getDoubleValue(Mod2002024Key.LM1219)) // 2024: Que han cumplido ...
			
		})
				
		,PAG21 ( new IPropertyFiller[] {
				  
			     (mod200old,mod200new) -> mod200new.getGroupEntities().addAll(mod200old.getGroupEntities())    // NIF de las entidades del grupo
			    ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.CNEST , mod200old.getDoubleValue(Mod2002024Key.CNEST)) // Número de establecimientos permanentes a través de los que opera, en caso de persona física titular
			    ,(mod200old,mod200new) -> mod200new.getEstablishments().addAll(mod200old.getEstablishments())  // NIF de los establecimientos permanentes		    
			    
		})
		
        ,PAG22 ( new IPropertyFiller[] {
        		
    		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC        	  			
             (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RC1165, mod200old.getDoubleValue(Mod2002024Key.RC996) ) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RC1744, mod200old.getDoubleValue(Mod2002024Key.RC1175)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RC2807, mod200old.getDoubleValue(Mod2002024Key.RC1821)) // 2022
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RC2975, mod200old.getDoubleValue(Mod2002024Key.RC2822)) // 2023
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RC3623, mod200old.getDoubleValue(Mod2002024Key.RC3313)) // 2024
            
            // Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RC3629, mod200old.getDoubleValue(Mod2002024Key.RC3354)) // 2024

        	// Régimen de cooperativas - Detalle de compensación de cuotas	                                                                				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ673 , mod200old.getDoubleValue(Mod2002024Key.LQ1224)) // 2000 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ676 , mod200old.getDoubleValue(Mod2002024Key.LQ678 )) // 2001				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ679 , mod200old.getDoubleValue(Mod2002024Key.LQ681 )) // 2002				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ682 , mod200old.getDoubleValue(Mod2002024Key.LQ684 )) // 2003				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ685 , mod200old.getDoubleValue(Mod2002024Key.LQ687 )) // 2004				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ688 , mod200old.getDoubleValue(Mod2002024Key.LQ690 )) // 2005				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ691 , mod200old.getDoubleValue(Mod2002024Key.LQ693 )) // 2006				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ623 , mod200old.getDoubleValue(Mod2002024Key.LQ672 )) // 2007				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ279 , mod200old.getDoubleValue(Mod2002024Key.LQ281 )) // 2008				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ587 , mod200old.getDoubleValue(Mod2002024Key.LQ900 )) // 2009				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ059 , mod200old.getDoubleValue(Mod2002024Key.LQ100 )) // 2010				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ017 , mod200old.getDoubleValue(Mod2002024Key.LQ019 )) // 2011				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ772 , mod200old.getDoubleValue(Mod2002024Key.LQ777 )) // 2012				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ907 , mod200old.getDoubleValue(Mod2002024Key.LQ909 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ910 , mod200old.getDoubleValue(Mod2002024Key.LQ912 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ935 , mod200old.getDoubleValue(Mod2002024Key.LQ937 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1511, mod200old.getDoubleValue(Mod2002024Key.LQ1513)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1767, mod200old.getDoubleValue(Mod2002024Key.LQ1769)) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2113, mod200old.getDoubleValue(Mod2002024Key.LQ2115)) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2281, mod200old.getDoubleValue(Mod2002024Key.LQ2283)) // 2019            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2452, mod200old.getDoubleValue(Mod2002024Key.LQ2454)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1186, mod200old.getDoubleValue(Mod2002024Key.LQ1190)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ1516, mod200old.getDoubleValue(Mod2002024Key.LQ1518)) // 2022
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ2850, mod200old.getDoubleValue(Mod2002024Key.LQ2913)) // 2023)  

            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.LQ3357, mod200old.getDoubleValue(Mod2002024Key.LQ024 )+ // 2024 (Tambien se añade la casilla 560 del ejercicio anterior si es negativa y es cooperativa y no está puesto ese importe en la 1226)
            		                                                                   mod200old.getDoubleValue(Mod2002024Key.LQ1226)+
            		                                                                  ((mod200old.getDoubleValue(Mod2002024Key.C0017)==1 || mod200old.getDoubleValue(Mod2002024Key.C0018)==1 || mod200old.getDoubleValue(Mod2002024Key.C0019)==1) && (mod200old.getDoubleValue(Mod2002024Key.LQ560)<0) && (mod200old.getDoubleValue(Mod2002024Key.LQ1226)==0) ? Math.abs(mod200old.getDoubleValue(Mod2002024Key.LQ560)) : 0) )  
			
		})
        
		,PAG22BIS  ( new IPropertyFiller[] {
				
	    		// Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022) - RIIB        	  			
				 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RB1707 , mod200old.getDoubleValue(Mod2002024Key.RB2917) ) // 2023
	            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RB3636 , mod200old.getDoubleValue(Mod2002024Key.RB1709) ) // 2024
	           
	            // Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022) - Inversiones anticipadas
	            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RB2362, mod200old.getDoubleValue(Mod2002024Key.RB2941)) // 2023
	            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025Key.RB3642, mod200old.getDoubleValue(Mod2002024Key.RB2374)) // 2024
			    
		})
        
		,PAG24BIS  ( new IPropertyFiller[] {
				
				// UTE - Relación de partícipes
			    (mod200old,mod200new) -> mod200new.getUteParticipations().addAll(mod200old.getUteParticipations())
			    
		})
        
        ,PAG26BIS ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias	
	      	  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2504, mod200old.getDoubleValue(Mod2002024KeyDC.DC2505))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2509, mod200old.getDoubleValue(Mod2002024KeyDC.DC2510))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2514, mod200old.getDoubleValue(Mod2002024KeyDC.DC2515))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2519, mod200old.getDoubleValue(Mod2002024KeyDC.DC2520))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2524, mod200old.getDoubleValue(Mod2002024KeyDC.DC2525))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2529, mod200old.getDoubleValue(Mod2002024KeyDC.DC2530))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2534, mod200old.getDoubleValue(Mod2002024KeyDC.DC2535))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2539, mod200old.getDoubleValue(Mod2002024KeyDC.DC2540))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2544, mod200old.getDoubleValue(Mod2002024KeyDC.DC2545))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2549, mod200old.getDoubleValue(Mod2002024KeyDC.DC2550))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2554, mod200old.getDoubleValue(Mod2002024KeyDC.DC2555))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2559, mod200old.getDoubleValue(Mod2002024KeyDC.DC2560))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2564, mod200old.getDoubleValue(Mod2002024KeyDC.DC2565))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2569, mod200old.getDoubleValue(Mod2002024KeyDC.DC2570))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2579, mod200old.getDoubleValue(Mod2002024KeyDC.DC2580))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2584, mod200old.getDoubleValue(Mod2002024KeyDC.DC2585))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2589, mod200old.getDoubleValue(Mod2002024KeyDC.DC2590))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2594, mod200old.getDoubleValue(Mod2002024KeyDC.DC2595))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2599, mod200old.getDoubleValue(Mod2002024KeyDC.DC2600))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2604, mod200old.getDoubleValue(Mod2002024KeyDC.DC2605))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2609, mod200old.getDoubleValue(Mod2002024KeyDC.DC2610))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2614, mod200old.getDoubleValue(Mod2002024KeyDC.DC2615))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2619, mod200old.getDoubleValue(Mod2002024KeyDC.DC2620))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2624, mod200old.getDoubleValue(Mod2002024KeyDC.DC2625))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2629, mod200old.getDoubleValue(Mod2002024KeyDC.DC2630))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC0075, mod200old.getDoubleValue(Mod2002024KeyDC.DC0079))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC0080, mod200old.getDoubleValue(Mod2002024KeyDC.DC0087))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1184, mod200old.getDoubleValue(Mod2002024KeyDC.DC1883))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1884, mod200old.getDoubleValue(Mod2002024KeyDC.DC1984))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2634, mod200old.getDoubleValue(Mod2002024KeyDC.DC2635))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2639, mod200old.getDoubleValue(Mod2002024KeyDC.DC2640))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2644, mod200old.getDoubleValue(Mod2002024KeyDC.DC2645))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2649, mod200old.getDoubleValue(Mod2002024KeyDC.DC2650))
    		  
        })
        
        ,PAG26TER ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
        		
	      	  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2654, mod200old.getDoubleValue(Mod2002024KeyDC.DC2655))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2659, mod200old.getDoubleValue(Mod2002024KeyDC.DC2660))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2664, mod200old.getDoubleValue(Mod2002024KeyDC.DC2665))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2669, mod200old.getDoubleValue(Mod2002024KeyDC.DC2670))
    	    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2674, mod200old.getDoubleValue(Mod2002024KeyDC.DC2675))
    	    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2679, mod200old.getDoubleValue(Mod2002024KeyDC.DC2680))
      		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2684, mod200old.getDoubleValue(Mod2002024KeyDC.DC2685))
      		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2689, mod200old.getDoubleValue(Mod2002024KeyDC.DC2690))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2714, mod200old.getDoubleValue(Mod2002024KeyDC.DC2715))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2719, mod200old.getDoubleValue(Mod2002024KeyDC.DC2720))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1860, mod200old.getDoubleValue(Mod2002024KeyDC.DC1864))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1865, mod200old.getDoubleValue(Mod2002024KeyDC.DC1869))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1995, mod200old.getDoubleValue(Mod2002024KeyDC.DC2375))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1009, mod200old.getDoubleValue(Mod2002024KeyDC.DC1735))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2724, mod200old.getDoubleValue(Mod2002024KeyDC.DC2725))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2729, mod200old.getDoubleValue(Mod2002024KeyDC.DC2730))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2734, mod200old.getDoubleValue(Mod2002024KeyDC.DC2735))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2739, mod200old.getDoubleValue(Mod2002024KeyDC.DC2740))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2744, mod200old.getDoubleValue(Mod2002024KeyDC.DC2745))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2749, mod200old.getDoubleValue(Mod2002024KeyDC.DC2750))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2814, mod200old.getDoubleValue(Mod2002024KeyDC.DC2815))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2819, mod200old.getDoubleValue(Mod2002024KeyDC.DC2820))
    		
        })
        
        ,PAG26QUA ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
        		
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2574, mod200old.getDoubleValue(Mod2002024KeyDC.DC2575))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2754, mod200old.getDoubleValue(Mod2002024KeyDC.DC2755))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1841, mod200old.getDoubleValue(Mod2002024KeyDC.DC1845))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1846, mod200old.getDoubleValue(Mod2002024KeyDC.DC1859))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2854, mod200old.getDoubleValue(Mod2002024KeyDC.DC2855))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2859, mod200old.getDoubleValue(Mod2002024KeyDC.DC2860))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2864, mod200old.getDoubleValue(Mod2002024KeyDC.DC2865))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2869, mod200old.getDoubleValue(Mod2002024KeyDC.DC2870))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2874, mod200old.getDoubleValue(Mod2002024KeyDC.DC2875))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2879, mod200old.getDoubleValue(Mod2002024KeyDC.DC2880))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2884, mod200old.getDoubleValue(Mod2002024KeyDC.DC2885))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2889, mod200old.getDoubleValue(Mod2002024KeyDC.DC2890))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2894, mod200old.getDoubleValue(Mod2002024KeyDC.DC2895))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2899, mod200old.getDoubleValue(Mod2002024KeyDC.DC2900))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2904, mod200old.getDoubleValue(Mod2002024KeyDC.DC2905))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2909, mod200old.getDoubleValue(Mod2002024KeyDC.DC2910))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2924, mod200old.getDoubleValue(Mod2002024KeyDC.DC2925))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2929, mod200old.getDoubleValue(Mod2002024KeyDC.DC2930))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2934, mod200old.getDoubleValue(Mod2002024KeyDC.DC2935))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2939, mod200old.getDoubleValue(Mod2002024KeyDC.DC2940))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1676, mod200old.getDoubleValue(Mod2002024KeyDC.DC1680))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC1681, mod200old.getDoubleValue(Mod2002024KeyDC.DC1688))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2954, mod200old.getDoubleValue(Mod2002024KeyDC.DC2955))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2959, mod200old.getDoubleValue(Mod2002024KeyDC.DC2960))
    		
        })
        
        ,PAG26QUI ( new IPropertyFiller[] {
        	
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
        		
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2984, mod200old.getDoubleValue(Mod2002024KeyDC.DC2985))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2989, mod200old.getDoubleValue(Mod2002024KeyDC.DC2990))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2994, mod200old.getDoubleValue(Mod2002024KeyDC.DC2995))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2999, mod200old.getDoubleValue(Mod2002024KeyDC.DC3000))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3004, mod200old.getDoubleValue(Mod2002024KeyDC.DC3005))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3009, mod200old.getDoubleValue(Mod2002024KeyDC.DC3010))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3014, mod200old.getDoubleValue(Mod2002024KeyDC.DC3015))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3019, mod200old.getDoubleValue(Mod2002024KeyDC.DC3020))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3024, mod200old.getDoubleValue(Mod2002024KeyDC.DC3025))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3029, mod200old.getDoubleValue(Mod2002024KeyDC.DC3030))
  		  	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3034, mod200old.getDoubleValue(Mod2002024KeyDC.DC3035))
  		  	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3039, mod200old.getDoubleValue(Mod2002024KeyDC.DC3040))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3044, mod200old.getDoubleValue(Mod2002024KeyDC.DC3045))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3049, mod200old.getDoubleValue(Mod2002024KeyDC.DC3050))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3054, mod200old.getDoubleValue(Mod2002024KeyDC.DC3055))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3059, mod200old.getDoubleValue(Mod2002024KeyDC.DC3060))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3074, mod200old.getDoubleValue(Mod2002024KeyDC.DC3075))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3079, mod200old.getDoubleValue(Mod2002024KeyDC.DC3080))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3084, mod200old.getDoubleValue(Mod2002024KeyDC.DC3085))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3089, mod200old.getDoubleValue(Mod2002024KeyDC.DC3090))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3094, mod200old.getDoubleValue(Mod2002024KeyDC.DC3095))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3099, mod200old.getDoubleValue(Mod2002024KeyDC.DC3100))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3104, mod200old.getDoubleValue(Mod2002024KeyDC.DC3105))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3109, mod200old.getDoubleValue(Mod2002024KeyDC.DC3110))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3114, mod200old.getDoubleValue(Mod2002024KeyDC.DC3115))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3119, mod200old.getDoubleValue(Mod2002024KeyDC.DC3120))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3124, mod200old.getDoubleValue(Mod2002024KeyDC.DC3125))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3129, mod200old.getDoubleValue(Mod2002024KeyDC.DC3130))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3134, mod200old.getDoubleValue(Mod2002024KeyDC.DC3135))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3139, mod200old.getDoubleValue(Mod2002024KeyDC.DC3140))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3144, mod200old.getDoubleValue(Mod2002024KeyDC.DC3145))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3149, mod200old.getDoubleValue(Mod2002024KeyDC.DC3150))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3154, mod200old.getDoubleValue(Mod2002024KeyDC.DC3155))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3159, mod200old.getDoubleValue(Mod2002024KeyDC.DC3160))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3164, mod200old.getDoubleValue(Mod2002024KeyDC.DC3165))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3169, mod200old.getDoubleValue(Mod2002024KeyDC.DC3170))
    		
        })
        
        ,PAG26SEX ( new IPropertyFiller[] {
        	
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
        		
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3174, mod200old.getDoubleValue(Mod2002024KeyDC.DC3175))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3179, mod200old.getDoubleValue(Mod2002024KeyDC.DC3180))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3184, mod200old.getDoubleValue(Mod2002024KeyDC.DC3185))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3189, mod200old.getDoubleValue(Mod2002024KeyDC.DC3190))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3194, mod200old.getDoubleValue(Mod2002024KeyDC.DC3195))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3199, mod200old.getDoubleValue(Mod2002024KeyDC.DC3200))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3204, mod200old.getDoubleValue(Mod2002024KeyDC.DC3205))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3209, mod200old.getDoubleValue(Mod2002024KeyDC.DC3210))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3214, mod200old.getDoubleValue(Mod2002024KeyDC.DC3215))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3219, mod200old.getDoubleValue(Mod2002024KeyDC.DC3220))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3224, mod200old.getDoubleValue(Mod2002024KeyDC.DC3225))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3229, mod200old.getDoubleValue(Mod2002024KeyDC.DC3230))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3234, mod200old.getDoubleValue(Mod2002024KeyDC.DC3235))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3239, mod200old.getDoubleValue(Mod2002024KeyDC.DC3240))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3254, mod200old.getDoubleValue(Mod2002024KeyDC.DC3255))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3259, mod200old.getDoubleValue(Mod2002024KeyDC.DC3260))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3264, mod200old.getDoubleValue(Mod2002024KeyDC.DC3265))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3269, mod200old.getDoubleValue(Mod2002024KeyDC.DC3270))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3274, mod200old.getDoubleValue(Mod2002024KeyDC.DC3275))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3279, mod200old.getDoubleValue(Mod2002024KeyDC.DC3280))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3294, mod200old.getDoubleValue(Mod2002024KeyDC.DC3295))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3299, mod200old.getDoubleValue(Mod2002024KeyDC.DC3300))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC0094, mod200old.getDoubleValue(Mod2002024KeyDC.DC0224))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC0227, mod200old.getDoubleValue(Mod2002024KeyDC.DC0811))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3304, mod200old.getDoubleValue(Mod2002024KeyDC.DC3305))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3309, mod200old.getDoubleValue(Mod2002024KeyDC.DC3310))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2176, mod200old.getDoubleValue(Mod2002024KeyDC.DC2180))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC2289, mod200old.getDoubleValue(Mod2002024KeyDC.DC2293))
        		
        })
        
        ,PAG26SEP ( new IPropertyFiller[] {
            	
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
    		
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3324, mod200old.getDoubleValue(Mod2002024KeyDC.DC3325))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3329, mod200old.getDoubleValue(Mod2002024KeyDC.DC3330))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3334, mod200old.getDoubleValue(Mod2002024KeyDC.DC3335))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3339, mod200old.getDoubleValue(Mod2002024KeyDC.DC3340))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3344, mod200old.getDoubleValue(Mod2002024KeyDC.DC3345))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3349, mod200old.getDoubleValue(Mod2002024KeyDC.DC3350))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3364, mod200old.getDoubleValue(Mod2002024KeyDC.DC3365))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3369, mod200old.getDoubleValue(Mod2002024KeyDC.DC3370))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3374, mod200old.getDoubleValue(Mod2002024KeyDC.DC3375))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3379, mod200old.getDoubleValue(Mod2002024KeyDC.DC3380))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3384, mod200old.getDoubleValue(Mod2002024KeyDC.DC3385))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3389, mod200old.getDoubleValue(Mod2002024KeyDC.DC3390))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3394, mod200old.getDoubleValue(Mod2002024KeyDC.DC3395))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002025KeyDC.DC3399, mod200old.getDoubleValue(Mod2002024KeyDC.DC3400))
        		
        })
		;
		
		private IPropertyFiller[] propertyFillers;
		
		private Pages2025 (IPropertyFiller[] propertyFillers) {
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Mod2002024 mod200old, Mod2002025 mod200new) {
			for ( Pages2025 page : Pages2025.values()) {
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(mod200old, mod200new);
					}				
			}
		}
	}
	
	public static void import2024(Mod2002025 mod200new, Mod2002024 mod200old) {
		try {
			Pages2025.fill(mod200old, mod200new);
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
	}
	
// -------------------- PRUEBAS --------------------
	
	public static void main(String[] argv) {
		
		// Esta prueba unicamente crea un objeto del año anterior e inicializa sus casillas con 
		// los códigos, posteriormente llama a la importacion para ver que se trasladan correctamente
		try {			
			Mod2002024 mod200old = new Mod2002024();

			// PRUEBA - Inicializamos todas las claves con sus numeros

			mod200old.setBalanceType(2); // PYMES
			mod200old.setPygType(2); // PYMES
			mod200old.setEcpnType(3); // No consta

			for (Mod2002024Key key : Mod2002024Key.values()) {
				try {
					setDoubleValue2024(mod200old, key, Double.parseDouble(key.name().substring(2)));
				} catch (NumberFormatException e) {
					// do nothing
				}
			}
			for (Mod2002024KeyDC key : Mod2002024KeyDC.values()) {
				try {
					setDoubleValue2024(mod200old, key, Double.parseDouble(key.name().substring(2)));
				} catch (NumberFormatException e) {
					// do nothing
				}
			}

			// Prueba base imponible negativa (casilla 552)
			setDoubleValue2024(mod200old, Mod2002024Key.LQ552, -552);

			// Cooperativas Casillas 17, 18 y 19. Cuota compensacion negativa (casilla 560)
			setDoubleValue2024(mod200old, Mod2002024Key.C0017, 0);
			setDoubleValue2024(mod200old, Mod2002024Key.C0018, 0);
			setDoubleValue2024(mod200old, Mod2002024Key.C0019, 0);
			setDoubleValue2024(mod200old, Mod2002024Key.LQ560, 0);

			// FIN PRUEBA

			Mod2002025 mod200new = import2024(mod200old);
			toString(mod200new);
				
		}
        catch (Exception e) {
		    e.printStackTrace();
        }
		finally {
			System.exit(0);
		}
	}
	
	private static Mod2002025 import2024(Mod2002024 mod200old) {
		
		try {
			// Creamos el objeto Mod200 del ejercicio actual, donde importaremos los datos del ejercicio anterior
			Mod2002025 mod200new = new Mod2002025();
			import2024(mod200new, mod200old);
			return mod200new;
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
	
	private static void toString(Mod2002025 mod200) {
		
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
		for (Mod2002025Key key : Mod2002025Key.values()) {			
			if (mod200.getKey(key) != null) {
				System.out.println(AonStringUtils.join(AonStringUtils.SPACE
						,mod200.getKey(key).getKey()
						," ...: "
						,mod200.getKey(key).getValue()));
			}
		}
		for (Mod2002025KeyDC key : Mod2002025KeyDC.values()) {			
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
				,formatDate(lr.getNotaryDate()) 				
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
	
	private static void setDoubleValue2024(Mod2002024 mod200, IMod200Key key, double value) {
		 
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
	
	
}


