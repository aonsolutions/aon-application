package com.esferalia.aon.occam.mod200.server.format.mod200_2023;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023KeyDC;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002023Import2022 {

	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(Mod2002022 mod200old, Mod2002023 mod200new);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Añadir casilla si no existe o asignar el valor a la casilla, al Modelo 200
	private static void setDoubleValue(Mod2002023 mod200, IMod200Key key, double value) {
		 
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
	private static double adjustDoubleTax(double value, double type, double type2023 ) {
		
		if (type2023 != 0 && type != 0 && type != type2023) {
			return AonMathUtils.round( value * type / type2023 );
	    }
		else return value;
		
	}

	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Pages2023 {
		
		 PAG1 ( new IPropertyFiller[] {
				 
			 (mod200old,mod200new) -> mod200new.setPeriodStart(addOneYear(mod200old.getPeriodStart()))  // Periodo Impositivo - Inicio					
			,(mod200old,mod200new) -> mod200new.setPeriodEnd(addOneYear(mod200old.getPeriodEnd()))      // Periodo Impositivo - Fin
			,(mod200old,mod200new) -> mod200new.setPeriodType(mod200old.getPeriodType())                // Identificación - Tipo de ejercicio
			,(mod200old,mod200new) -> mod200new.setCnae(mod200old.getCnae())  			 			  	// Identificación - C.N.A.E.  Actividad principal (convertido a CNAE 2009)
			,(mod200old,mod200new) -> mod200new.setDocument(mod200old.getDocument()) 					// Identificación - NIF 
			,(mod200old,mod200new) -> mod200new.setName(mod200old.getName())          					// Identificación - Apellidos y nombre o Razón Social
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone1(mod200old.getEnterprisePhone1())    // Identificación - Teléfono 1
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone2(mod200old.getEnterprisePhone2())    // Identificación - Teléfono 2
			,(mod200old,mod200new) -> mod200new.setYear(2023)             	                            // Ejercicio
			,(mod200old,mod200new) -> mod200new.setAdministration(mod200old.getAdministration())		// Administracion                                           
			
			// CARACTERES DE LA DECLARACION 
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0001, mod200old.getDoubleValue(Mod2002022Key.C0001))                       
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0002, mod200old.getDoubleValue(Mod2002022Key.C0002))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0080, mod200old.getDoubleValue(Mod2002022Key.C0080))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0003, mod200old.getDoubleValue(Mod2002022Key.C0003))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0008, mod200old.getDoubleValue(Mod2002022Key.C0008))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0004, mod200old.getDoubleValue(Mod2002022Key.C0004))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0005, mod200old.getDoubleValue(Mod2002022Key.C0005))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0011, mod200old.getDoubleValue(Mod2002022Key.C0011))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0013, mod200old.getDoubleValue(Mod2002022Key.C0013))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0014, mod200old.getDoubleValue(Mod2002022Key.C0014))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0017, mod200old.getDoubleValue(Mod2002022Key.C0017))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0018, mod200old.getDoubleValue(Mod2002022Key.C0018))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0019, mod200old.getDoubleValue(Mod2002022Key.C0019))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0021, mod200old.getDoubleValue(Mod2002022Key.C0021))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0023, mod200old.getDoubleValue(Mod2002022Key.C0023))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0024, mod200old.getDoubleValue(Mod2002022Key.C0024))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0025, mod200old.getDoubleValue(Mod2002022Key.C0025))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0031, mod200old.getDoubleValue(Mod2002022Key.C0031))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0032, mod200old.getDoubleValue(Mod2002022Key.C0032))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0036, mod200old.getDoubleValue(Mod2002022Key.C0036))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0048, mod200old.getDoubleValue(Mod2002022Key.C0048))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0058, mod200old.getDoubleValue(Mod2002022Key.C0058))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0060, mod200old.getDoubleValue(Mod2002022Key.C0060))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0066, mod200old.getDoubleValue(Mod2002022Key.C0066))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0078, mod200old.getDoubleValue(Mod2002022Key.C0078))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0056, mod200old.getDoubleValue(Mod2002022Key.C0056))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0006, mod200old.getDoubleValue(Mod2002022Key.C0006))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0015, mod200old.getDoubleValue(Mod2002022Key.C0015))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0079, mod200old.getDoubleValue(Mod2002022Key.C0079))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0022, mod200old.getDoubleValue(Mod2002022Key.C0022))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0028, mod200old.getDoubleValue(Mod2002022Key.C0028))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0047, mod200old.getDoubleValue(Mod2002022Key.C0047))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0049, mod200old.getDoubleValue(Mod2002022Key.C0049))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0035, mod200old.getDoubleValue(Mod2002022Key.C0035))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0029, mod200old.getDoubleValue(Mod2002022Key.C0029))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0069, mod200old.getDoubleValue(Mod2002022Key.C0069))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0033, mod200old.getDoubleValue(Mod2002022Key.C0033))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0034, mod200old.getDoubleValue(Mod2002022Key.C0034))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0038, mod200old.getDoubleValue(Mod2002022Key.C0038))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0046, mod200old.getDoubleValue(Mod2002022Key.C0046))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0012, mod200old.getDoubleValue(Mod2002022Key.C0012))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0012R, mod200old.getDoubleValue(Mod2002022Key.C0012R))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0064, mod200old.getDoubleValue(Mod2002022Key.C0064))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0057, mod200old.getDoubleValue(Mod2002022Key.C0057))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0062, mod200old.getDoubleValue(Mod2002022Key.C0062))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0020, mod200old.getDoubleValue(Mod2002022Key.C0020))  

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0007, mod200old.getDoubleValue(Mod2002022Key.C0007))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0009, mod200old.getDoubleValue(Mod2002022Key.C0009))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0010, mod200old.getDoubleValue(Mod2002022Key.C0010))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0081, mod200old.getDoubleValue(Mod2002022Key.C0081))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0082, mod200old.getDoubleValue(Mod2002022Key.C0082))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0026, mod200old.getDoubleValue(Mod2002022Key.C0026))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0027, mod200old.getDoubleValue(Mod2002022Key.C0027))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0030, mod200old.getDoubleValue(Mod2002022Key.C0030))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0039, mod200old.getDoubleValue(Mod2002022Key.C0039))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0043, mod200old.getDoubleValue(Mod2002022Key.C0043))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0045, mod200old.getDoubleValue(Mod2002022Key.C0045))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0063, mod200old.getDoubleValue(Mod2002022Key.C0063))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0071, mod200old.getDoubleValue(Mod2002022Key.C0071))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0083, mod200old.getDoubleValue(Mod2002022Key.C0083))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0070, mod200old.getDoubleValue(Mod2002022Key.C0070))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0059, mod200old.getDoubleValue(Mod2002022Key.C0059))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0065, mod200old.getDoubleValue(Mod2002022Key.C0065))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0084, mod200old.getDoubleValue(Mod2002022Key.C0084))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0072, mod200old.getDoubleValue(Mod2002022Key.C0072))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0073, mod200old.getDoubleValue(Mod2002022Key.C0073))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0037, mod200old.getDoubleValue(Mod2002022Key.C0037))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0044, mod200old.getDoubleValue(Mod2002022Key.C0044))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0074, mod200old.getDoubleValue(Mod2002022Key.C0074))
			
			// IMPORTE NETO DE LA CIFRA DE NEGOCIOS
			// FALTA - Este año solo hay valores 0, 1 y 2, luego si tenía un 3 se le pone un 2, excepto cooperativas (caracteres 17, 18, 19) que siguen teniendo todos los valores
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002023Key.VOLOPE, 
					mod200old.getDoubleValue(Mod2002022Key.VOLOPE) == 3.0 && mod200old.getDoubleValue(Mod2002022Key.C0017) == 0.0 && mod200old.getDoubleValue(Mod2002022Key.C0018) == 0.0 && mod200old.getDoubleValue(Mod2002022Key.C0019) == 0.0 ? 2.0 : mod200old.getDoubleValue(Mod2002022Key.VOLOPE) )
			
			// GRUPOS FISCALES 
			
			,(mod200old,mod200new) -> mod200new.setFiscalGroup( mod200old.getFiscalGroup() )              // Grupo Fiscal - Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040]
			,(mod200old,mod200new) -> mod200new.setDominantDocument( mod200old.getDominantDocument() )    // Grupo Fiscal - NIF de la entidad representante/dominante (incluida en el grupo fiscal)
			,(mod200old,mod200new) -> mod200new.setDominantIdentificationNumber( mod200old.getDominantIdentificationNumber() ) // Grupo Fiscal - Nº identificación de la entidad dominante (en el caso de grupos constituidos sólo por entidades dependientes)
			
			,(mod200old,mod200new) -> mod200new.setUltimateDocument( mod200old.getUltimateDocument() )    // Grupo Mercantil - Datos de la sociedad matriz última: NIF o equivalente
			,(mod200old,mod200new) -> mod200new.setUltimateDocumentCountry( mod200old.getUltimateDocumentCountry() )    // Grupo Mercantil - Datos de la sociedad matriz última: Código Pais (NIF)
			,(mod200old,mod200new) -> mod200new.setUltimateName( mod200old.getUltimateName() )            // Grupo Mercantil - Datos de la sociedad matriz última: Nombre o razón social
			,(mod200old,mod200new) -> mod200new.setUltimateCountry( mod200old.getUltimateCountry() )      // Grupo Mercantil - Datos de la sociedad matriz última: País o jurisdicción de residencia fiscal
			
			// ESTADOS DE CUENTAS 
			
			,(mod200old,mod200new) -> mod200new.setBalanceType( mod200old.getBalanceType().ordinal() )  // Balance
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0050, mod200old.getDoubleValue(Mod2002022Key.C0050))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0051, mod200old.getDoubleValue(Mod2002022Key.C0051))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0052, mod200old.getDoubleValue(Mod2002022Key.C0052))

			,(mod200old,mod200new) -> mod200new.setEcpnType( mod200old.getEcpnType().ordinal() )        // ECPN
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0075, mod200old.getDoubleValue(Mod2002022Key.C0075))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0076, mod200old.getDoubleValue(Mod2002022Key.C0076))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0077, mod200old.getDoubleValue(Mod2002022Key.C0077))

			,(mod200old,mod200new) -> mod200new.setPygType(mod200old.getPygType().ordinal() )           // Pérdidas y ganancias 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0053, mod200old.getDoubleValue(Mod2002022Key.C0053))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0054, mod200old.getDoubleValue(Mod2002022Key.C0054))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.C0055, mod200old.getDoubleValue(Mod2002022Key.C0055))
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002023Key.C0061, mod200old.getDoubleValue(Mod2002022Key.C0061))  // Estados de cuentas de Instituciones de inversión colectiva [061]
			
			// PERSONAL ASALARIADO
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002023Key.C0041, mod200old.getDoubleValue(Mod2002022Key.C0041))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002023Key.C0042, mod200old.getDoubleValue(Mod2002022Key.C0042))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042]
			
		})

		,PAG2  ( new IPropertyFiller[] {
				
			 (mod200old,mod200new) -> mod200new.getAdministrators().addAll(mod200old.getAdministrators())       // A. Relación de administradores			 
			,(mod200old,mod200new) -> mod200new.getParticipationsOut().addAll(mod200old.getParticipationsOut()) // B1. Participaciones de la declarante en otras entidades
			,(mod200old,mod200new) -> mod200new.getParticipationsIn().addAll(mod200old.getParticipationsIn())   // B2. Participaciones de personas o entidades en la declarante
            
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002023Key.POR51, mod200old.getDoubleValue(Mod2002022Key.POR51))  // B2. Suma de porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002023Key.PORES, mod200old.getDoubleValue(Mod2002022Key.PORES))  // B2. Suma de porcentajes de participaciones en situaciones especiales
			
		})
		
		,PAG2BIS  ( new IPropertyFiller[] {
				
		     (mod200old,mod200new) -> mod200new.getMinorEntities().addAll(mod200old.getMinorEntities())   // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
		    ,(mod200old,mod200new) -> mod200new.getUteForeign().addAll(mod200old.getUteForeign())         // D. Información de detalle de EP que opere en el extranjero
		    
			// SECRETARIO
            
            ,(mod200old,mod200new) -> mod200new.getSecretary().setName(mod200old.getSecretary().getName())              // Apellidos y Nombre - Secretario del Consejo de Administración 
			,(mod200old,mod200new) -> mod200new.getSecretary().setDocument(mod200old.getSecretary().getDocument())      // N.I.F. - Secretario del Consejo de Administración
//			,(mod200old,mod200new) -> mod200new.getSecretary().setIrnr(addOneYear(mod200old.getSecretary().getIrnr()))  // Fecha - Contribuyentes por el I.R.N.R. - DESAPARECE ESTE AÑO
			
			// REPRESENTANTES LEGALES
						
			,(mod200old,mod200new) -> mod200new.getRepresentatives().addAll(mod200old.getRepresentatives())  // Declaración representantes legales entidad
		    
		})		
		
		,PAG14 ( new IPropertyFiller[] {
				
			// Se pone por defecto el tipo de gravamen del año pasado, cuando se calcule el modelo, ya se modificará si es necesario
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ558, mod200old.getDoubleValue(Mod2002022Key.LQ558))  // Tipo de Gravamen
			
		})

		,PAG15 ( new IPropertyFiller[] {
				
		    // Compensación bases imponibles negativas
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ640 , mod200old.getDoubleValue(Mod2002022Key.LQ548 )) // 1997
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ643 , mod200old.getDoubleValue(Mod2002022Key.LQ645 )) // 1998
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ646 , mod200old.getDoubleValue(Mod2002022Key.LQ648 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ649 , mod200old.getDoubleValue(Mod2002022Key.LQ651 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ652 , mod200old.getDoubleValue(Mod2002022Key.LQ654 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ655 , mod200old.getDoubleValue(Mod2002022Key.LQ657 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ658 , mod200old.getDoubleValue(Mod2002022Key.LQ660 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ661 , mod200old.getDoubleValue(Mod2002022Key.LQ663 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ664 , mod200old.getDoubleValue(Mod2002022Key.LQ666 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ667 , mod200old.getDoubleValue(Mod2002022Key.LQ669 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ743 , mod200old.getDoubleValue(Mod2002022Key.LQ748 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ275 , mod200old.getDoubleValue(Mod2002022Key.LQ277 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ608 , mod200old.getDoubleValue(Mod2002022Key.LQ610 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ704 , mod200old.getDoubleValue(Mod2002022Key.LQ706 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ013 , mod200old.getDoubleValue(Mod2002022Key.LQ015 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ725 , mod200old.getDoubleValue(Mod2002022Key.LQ727 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ534 , mod200old.getDoubleValue(Mod2002022Key.LQ536 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ607 , mod200old.getDoubleValue(Mod2002022Key.LQ699 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1045, mod200old.getDoubleValue(Mod2002022Key.LQ1047)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1519, mod200old.getDoubleValue(Mod2002022Key.LQ1521)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1592, mod200old.getDoubleValue(Mod2002022Key.LQ1594)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1825, mod200old.getDoubleValue(Mod2002022Key.LQ1827)) // 2018			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ2193, mod200old.getDoubleValue(Mod2002022Key.LQ2195)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ194 , mod200old.getDoubleValue(Mod2002022Key.LQ196 )) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ151 , mod200old.getDoubleValue(Mod2002022Key.LQ164 )) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ896,  mod200old.getDoubleValue(Mod2002022Key.LQ2318)+ // 2022
					                                                                   mod200old.getDoubleValue(Mod2002022Key.LQ1049)+
					                                                                  (mod200old.getDoubleValue(Mod2002022Key.C0017)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002022Key.C0018)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002022Key.C0019)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002022Key.LQ552)<0
                                                                                      && mod200old.getDoubleValue(Mod2002022Key.LQ1049)==0
                                                                                       ?Math.abs(mod200old.getDoubleValue(Mod2002022Key.LQ552)) : 0) )  
																						// Si la base imponible (casilla 552) del 2022 era negativa, tambien se suma a esta casilla (si no es cooperativa) y si no estaba indicada en la 1049
			                                                                    
			// Régimen especial de buques y empresas navieras en Canarias: desglose de la compensación de bases imponibles negativas
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ168 , mod200old.getDoubleValue(Mod2002022Key.LQ173 )) // 2021 especial
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ175 , mod200old.getDoubleValue(Mod2002022Key.LQ177 )) // 2021 resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ178 , mod200old.getDoubleValue(Mod2002022Key.LQ198)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.LQ267))  // 2022 especial			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ202 , mod200old.getDoubleValue(Mod2002022Key.LQ215)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.LQ344))  // 2022 resto
					
		})
		
		,PAG15BIS ( new IPropertyFiller[] {
				
				// Deducciones por doble imposición interna RDL 4/2004
				 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN104, adjustDoubleTax( // 2008
						mod200old.getDoubleValue(Mod2002022Key.BN848), mod200old.getDoubleValue(Mod2002022Key.BN105), mod200old.getDoubleValue(Mod2002022Key.BN103A) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN106, adjustDoubleTax( // 2009
						mod200old.getDoubleValue(Mod2002022Key.BN284), mod200old.getDoubleValue(Mod2002022Key.BN107), mod200old.getDoubleValue(Mod2002022Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN108, adjustDoubleTax( // 2010
						mod200old.getDoubleValue(Mod2002022Key.BN707), mod200old.getDoubleValue(Mod2002022Key.BN109), mod200old.getDoubleValue(Mod2002022Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN110, adjustDoubleTax( // 2011
						mod200old.getDoubleValue(Mod2002022Key.BN300), mod200old.getDoubleValue(Mod2002022Key.BN111), mod200old.getDoubleValue(Mod2002022Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN112, adjustDoubleTax( // 2012
						mod200old.getDoubleValue(Mod2002022Key.BN027), mod200old.getDoubleValue(Mod2002022Key.BN113), mod200old.getDoubleValue(Mod2002022Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN114, adjustDoubleTax( // 2013
						mod200old.getDoubleValue(Mod2002022Key.BN716), mod200old.getDoubleValue(Mod2002022Key.BN115), mod200old.getDoubleValue(Mod2002022Key.BN103A) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN735, adjustDoubleTax( // 2014
						mod200old.getDoubleValue(Mod2002022Key.BN738), mod200old.getDoubleValue(Mod2002022Key.BN920), mod200old.getDoubleValue(Mod2002022Key.BN103A) ) )

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN105 , mod200old.getDoubleValue(Mod2002022Key.BN105)) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN107 , mod200old.getDoubleValue(Mod2002022Key.BN107)) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN109 , mod200old.getDoubleValue(Mod2002022Key.BN109)) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN111 , mod200old.getDoubleValue(Mod2002022Key.BN111)) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN113 , mod200old.getDoubleValue(Mod2002022Key.BN113)) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN115 , mod200old.getDoubleValue(Mod2002022Key.BN115)) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN920 , mod200old.getDoubleValue(Mod2002022Key.BN920)) // 2014

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN846 , mod200old.getDoubleValue(Mod2002022Key.BN848)) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN282 , mod200old.getDoubleValue(Mod2002022Key.BN284)) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN702 , mod200old.getDoubleValue(Mod2002022Key.BN707)) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN071 , mod200old.getDoubleValue(Mod2002022Key.BN300)) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN025 , mod200old.getDoubleValue(Mod2002022Key.BN027)) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN714 , mod200old.getDoubleValue(Mod2002022Key.BN716)) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN736 , mod200old.getDoubleValue(Mod2002022Key.BN738)) // 2014
				                                                                          
				// Deducciones por doble imposición interna (DT 23ª.1 LIS)
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN101, adjustDoubleTax( // 2015
						mod200old.getDoubleValue(Mod2002022Key.BN121), mod200old.getDoubleValue(Mod2002022Key.BN102), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN122, adjustDoubleTax( // 2016
						mod200old.getDoubleValue(Mod2002022Key.BN126), mod200old.getDoubleValue(Mod2002022Key.BN123), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1595, adjustDoubleTax( // 2017
						mod200old.getDoubleValue(Mod2002022Key.BN1599), mod200old.getDoubleValue(Mod2002022Key.BN1596), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1828, adjustDoubleTax( // 2018
						mod200old.getDoubleValue(Mod2002022Key.BN1832), mod200old.getDoubleValue(Mod2002022Key.BN1829), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2196, adjustDoubleTax( // 2019
						mod200old.getDoubleValue(Mod2002022Key.BN2200), mod200old.getDoubleValue(Mod2002022Key.BN2197), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2319, adjustDoubleTax( // 2020
						mod200old.getDoubleValue(Mod2002022Key.BN2323), mod200old.getDoubleValue(Mod2002022Key.BN2320), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN199 , adjustDoubleTax( // 2021
						mod200old.getDoubleValue(Mod2002022Key.BN206), mod200old.getDoubleValue(Mod2002022Key.BN203), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN394 , adjustDoubleTax( // 2022
						mod200old.getDoubleValue(Mod2002022Key.BN2076)+mod200old.getDoubleValue(Mod2002022Key.BN129), mod200old.getDoubleValue(Mod2002022Key.BN436), mod200old.getDoubleValue(Mod2002022Key.BN103B) ) )

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN102 , mod200old.getDoubleValue(Mod2002022Key.BN102) ) // 2015
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN123 , mod200old.getDoubleValue(Mod2002022Key.BN123) ) // 2016
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1596, mod200old.getDoubleValue(Mod2002022Key.BN1596)) // 2017
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1829, mod200old.getDoubleValue(Mod2002022Key.BN1829)) // 2018
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2197, mod200old.getDoubleValue(Mod2002022Key.BN2197)) // 2019
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2320, mod200old.getDoubleValue(Mod2002022Key.BN2320)) // 2020
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN203 , mod200old.getDoubleValue(Mod2002022Key.BN203) ) // 2021
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN436 , mod200old.getDoubleValue(Mod2002022Key.BN436) ) // 2022

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN119 , mod200old.getDoubleValue(Mod2002022Key.BN121) ) // 2015
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN124 , mod200old.getDoubleValue(Mod2002022Key.BN126) ) // 2016
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1597, mod200old.getDoubleValue(Mod2002022Key.BN1599)) // 2017
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1830, mod200old.getDoubleValue(Mod2002022Key.BN1832)) // 2018
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2198, mod200old.getDoubleValue(Mod2002022Key.BN2200)) // 2019
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2321, mod200old.getDoubleValue(Mod2002022Key.BN2323)) // 2020
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN204 , mod200old.getDoubleValue(Mod2002022Key.BN206) ) // 2021
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN437 , mod200old.getDoubleValue(Mod2002022Key.BN2076)+mod200old.getDoubleValue(Mod2002022Key.BN129)) // 2022
				
				// Deducciones por doble imposición internacional RDLeg 4/2004
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN153, adjustDoubleTax(  // 2005
						mod200old.getDoubleValue(Mod2002022Key.BN639), mod200old.getDoubleValue(Mod2002022Key.BN728), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN154, adjustDoubleTax( // 2006
						mod200old.getDoubleValue(Mod2002022Key.BN197), mod200old.getDoubleValue(Mod2002022Key.BN729), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN155, adjustDoubleTax( // 2007
						mod200old.getDoubleValue(Mod2002022Key.BN287), mod200old.getDoubleValue(Mod2002022Key.BN730), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN156, adjustDoubleTax( // 2008
						mod200old.getDoubleValue(Mod2002022Key.BN827), mod200old.getDoubleValue(Mod2002022Key.BN731), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN157, adjustDoubleTax( // 2009
						mod200old.getDoubleValue(Mod2002022Key.BN003), mod200old.getDoubleValue(Mod2002022Key.BN732), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN158, adjustDoubleTax( // 2010
						mod200old.getDoubleValue(Mod2002022Key.BN030), mod200old.getDoubleValue(Mod2002022Key.BN733), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN159, adjustDoubleTax( // 2011
						mod200old.getDoubleValue(Mod2002022Key.BN719), mod200old.getDoubleValue(Mod2002022Key.BN734), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN720, adjustDoubleTax( // 2012
						mod200old.getDoubleValue(Mod2002022Key.BN724), mod200old.getDoubleValue(Mod2002022Key.BN721), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN739, adjustDoubleTax( // 2013
						mod200old.getDoubleValue(Mod2002022Key.BN742), mod200old.getDoubleValue(Mod2002022Key.BN921), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN134, adjustDoubleTax( // 2014
						mod200old.getDoubleValue(Mod2002022Key.BN137), mod200old.getDoubleValue(Mod2002022Key.BN926), mod200old.getDoubleValue(Mod2002022Key.BN103C) ))
				
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN728 , mod200old.getDoubleValue(Mod2002022Key.BN728 )) // 2005 - Tipo de gravamen
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN729 , mod200old.getDoubleValue(Mod2002022Key.BN729 )) // 2006
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN730 , mod200old.getDoubleValue(Mod2002022Key.BN730 )) // 2007
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN731 , mod200old.getDoubleValue(Mod2002022Key.BN731 )) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN732 , mod200old.getDoubleValue(Mod2002022Key.BN732 )) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN733 , mod200old.getDoubleValue(Mod2002022Key.BN733 )) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN734 , mod200old.getDoubleValue(Mod2002022Key.BN734 )) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN721 , mod200old.getDoubleValue(Mod2002022Key.BN721 )) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN921 , mod200old.getDoubleValue(Mod2002022Key.BN921 )) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN926 , mod200old.getDoubleValue(Mod2002022Key.BN926 )) // 2014

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN637 , mod200old.getDoubleValue(Mod2002022Key.BN639 )) // 2005 - Deducción pendiente
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN849 , mod200old.getDoubleValue(Mod2002022Key.BN197 )) // 2006
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN285 , mod200old.getDoubleValue(Mod2002022Key.BN287 )) // 2007
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN825 , mod200old.getDoubleValue(Mod2002022Key.BN827 )) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN001 , mod200old.getDoubleValue(Mod2002022Key.BN003 )) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN028 , mod200old.getDoubleValue(Mod2002022Key.BN030 )) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN717 , mod200old.getDoubleValue(Mod2002022Key.BN719 )) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN722 , mod200old.getDoubleValue(Mod2002022Key.BN724 )) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN740 , mod200old.getDoubleValue(Mod2002022Key.BN742 )) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN135 , mod200old.getDoubleValue(Mod2002022Key.BN137 )) // 2014
				
		})
		
		,PAG16 ( new IPropertyFiller[] {
			
			// Deducciones por doble imposición internacional LIS
 			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1054, adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN1053), mod200old.getDoubleValue(Mod2002022Key.BN1050), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1348, adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN1352), mod200old.getDoubleValue(Mod2002022Key.BN1349), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1770, adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN1774), mod200old.getDoubleValue(Mod2002022Key.BN1771), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1833, adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN1837), mod200old.getDoubleValue(Mod2002022Key.BN1834), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2201, adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN2205), mod200old.getDoubleValue(Mod2002022Key.BN2202), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2324, adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN2328), mod200old.getDoubleValue(Mod2002022Key.BN2325), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN207 , adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN213 ), mod200old.getDoubleValue(Mod2002022Key.BN208 ), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN490 , adjustDoubleTax(mod200old.getDoubleValue(Mod2002022Key.BN620)+mod200old.getDoubleValue(Mod2002022Key.BN174), mod200old.getDoubleValue(Mod2002022Key.BN491), mod200old.getDoubleValue(Mod2002022Key.BN103D) )) // 2022
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1050, mod200old.getDoubleValue(Mod2002022Key.BN1050)) // 2015 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1349, mod200old.getDoubleValue(Mod2002022Key.BN1349)) // 2016 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1771, mod200old.getDoubleValue(Mod2002022Key.BN1771)) // 2017 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1834, mod200old.getDoubleValue(Mod2002022Key.BN1834)) // 2018 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2202, mod200old.getDoubleValue(Mod2002022Key.BN2202)) // 2019 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2325, mod200old.getDoubleValue(Mod2002022Key.BN2325)) // 2020 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN208 , mod200old.getDoubleValue(Mod2002022Key.BN208) ) // 2021 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN491 , mod200old.getDoubleValue(Mod2002022Key.BN491) ) // 2022 - Tipo de gravamen
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1051, mod200old.getDoubleValue(Mod2002022Key.BN1053)) // 2015 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1350, mod200old.getDoubleValue(Mod2002022Key.BN1352)) // 2016 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1772, mod200old.getDoubleValue(Mod2002022Key.BN1774)) // 2017 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1835, mod200old.getDoubleValue(Mod2002022Key.BN1837)) // 2018 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2203, mod200old.getDoubleValue(Mod2002022Key.BN2205)) // 2098 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2326, mod200old.getDoubleValue(Mod2002022Key.BN2328)) // 2020 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN209 , mod200old.getDoubleValue(Mod2002022Key.BN213) ) // 2021 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN492 , mod200old.getDoubleValue(Mod2002022Key.BN620)+mod200old.getDoubleValue(Mod2002022Key.BN174)) // 2022 - Deducción pendiente
										
			// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN031 , mod200old.getDoubleValue(Mod2002022Key.BN033 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN022 , mod200old.getDoubleValue(Mod2002022Key.BN024 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN040 , mod200old.getDoubleValue(Mod2002022Key.BN042 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN138 , mod200old.getDoubleValue(Mod2002022Key.BN140 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN141 , mod200old.getDoubleValue(Mod2002022Key.BN143 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN188 , mod200old.getDoubleValue(Mod2002022Key.BN190 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN803 , mod200old.getDoubleValue(Mod2002022Key.BN805 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1055, mod200old.getDoubleValue(Mod2002022Key.BN1057)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN700 , mod200old.getDoubleValue(Mod2002022Key.BN709 )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1353, mod200old.getDoubleValue(Mod2002022Key.BN1355)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1775, mod200old.getDoubleValue(Mod2002022Key.BN1777)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1838, mod200old.getDoubleValue(Mod2002022Key.BN1840)) // 2019			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2206, mod200old.getDoubleValue(Mod2002022Key.BN2208)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2329, mod200old.getDoubleValue(Mod2002022Key.BN2331)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN249 , mod200old.getDoubleValue(Mod2002022Key.BN253)+mod200old.getDoubleValue(Mod2002022Key.BN710)) // 2022
			
			// Deducciones disposición transitoria 24ª.1 LIS		
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN749 , mod200old.getDoubleValue(Mod2002022Key.BN754)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN752 , mod200old.getDoubleValue(Mod2002022Key.BN757)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN755 , mod200old.getDoubleValue(Mod2002022Key.BN760)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN758 , mod200old.getDoubleValue(Mod2002022Key.BN763)) // 2021			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN761 , mod200old.getDoubleValue(Mod2002022Key.BN746)+mod200old.getDoubleValue(Mod2002022Key.BN784)) // 2022
			
		})
        
		,PAG16BIS ( new IPropertyFiller[] {
			
			// Deducciones inversión en Canarias - Activos fijos (Ley 20/1991)
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN854 , mod200old.getDoubleValue(Mod2002022Key.BN1356)) // 2010 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN857 , mod200old.getDoubleValue(Mod2002022Key.BN859 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN860 , mod200old.getDoubleValue(Mod2002022Key.BN862 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN863 , mod200old.getDoubleValue(Mod2002022Key.BN865 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN883 , mod200old.getDoubleValue(Mod2002022Key.BN885 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN785 , mod200old.getDoubleValue(Mod2002022Key.BN790 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1357, mod200old.getDoubleValue(Mod2002022Key.BN1359)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1778, mod200old.getDoubleValue(Mod2002022Key.BN1780)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN852 , mod200old.getDoubleValue(Mod2002022Key.BN856 )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2116, mod200old.getDoubleValue(Mod2002022Key.BN2118)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2209, mod200old.getDoubleValue(Mod2002022Key.BN2211)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2332, mod200old.getDoubleValue(Mod2002022Key.BN2334)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN237 , mod200old.getDoubleValue(Mod2002022Key.BN239)+mod200old.getDoubleValue(Mod2002022Key.BN2077)) // 2022
			
			// Deducciones inversión en Canarias - Activos fijos en La Palma, La Gomera y El Hierro
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2335, mod200old.getDoubleValue(Mod2002022Key.BN2337)) // 2018 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2338, mod200old.getDoubleValue(Mod2002022Key.BN2340)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2341, mod200old.getDoubleValue(Mod2002022Key.BN2343)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2344, mod200old.getDoubleValue(Mod2002022Key.BN2346)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN244 , mod200old.getDoubleValue(Mod2002022Key.BN2497)+mod200old.getDoubleValue(Mod2002022Key.BN766)) // 2022
			
			// Deducciones inversión en Canarias - Inversiones en Canarias (Ley 20/1991)			 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN866 , mod200old.getDoubleValue(Mod2002022Key.BN870 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN939 , mod200old.getDoubleValue(Mod2002022Key.BN941 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN191 , mod200old.getDoubleValue(Mod2002022Key.BN193 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN613 , mod200old.getDoubleValue(Mod2002022Key.BN701 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN200 , mod200old.getDoubleValue(Mod2002022Key.BN011 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN037 , mod200old.getDoubleValue(Mod2002022Key.BN039 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN044 , mod200old.getDoubleValue(Mod2002022Key.BN046 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN528 , mod200old.getDoubleValue(Mod2002022Key.BN530 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN144 , mod200old.getDoubleValue(Mod2002022Key.BN146 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN147 , mod200old.getDoubleValue(Mod2002022Key.BN149 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN240 , mod200old.getDoubleValue(Mod2002022Key.BN242 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1058, mod200old.getDoubleValue(Mod2002022Key.BN1060)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN791 , mod200old.getDoubleValue(Mod2002022Key.BN806 )) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1781, mod200old.getDoubleValue(Mod2002022Key.BN1783)) // 2018            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2122, mod200old.getDoubleValue(Mod2002022Key.BN2124)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2212, mod200old.getDoubleValue(Mod2002022Key.BN2214)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2347, mod200old.getDoubleValue(Mod2002022Key.BN2349)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN217 , mod200old.getDoubleValue(Mod2002022Key.BN219)+mod200old.getDoubleValue(Mod2002022Key.BN769)) // 2022
            
            // Deducciones inversión en Canarias - Inversiones en La Palma, La Gomera y El Hierro
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2119, mod200old.getDoubleValue(Mod2002022Key.BN2121)) // 2018 
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2125, mod200old.getDoubleValue(Mod2002022Key.BN2127)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2215, mod200old.getDoubleValue(Mod2002022Key.BN2217)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2350, mod200old.getDoubleValue(Mod2002022Key.BN2352)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN220 , mod200old.getDoubleValue(Mod2002022Key.BN222)+mod200old.getDoubleValue(Mod2002022Key.BN774)) // 2022
            
		})
			
		,PAG17_18_18BIS ( new IPropertyFiller[] {
				
		    // Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)		
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN466 , mod200old.getDoubleValue(Mod2002022Key.BN468 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN061 , mod200old.getDoubleValue(Mod2002022Key.BN586 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN472 , mod200old.getDoubleValue(Mod2002022Key.BN478 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN180 , mod200old.getDoubleValue(Mod2002022Key.BN182 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN531 , mod200old.getDoubleValue(Mod2002022Key.BN533 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN945 , mod200old.getDoubleValue(Mod2002022Key.BN947 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN960 , mod200old.getDoubleValue(Mod2002022Key.BN962 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN183 , mod200old.getDoubleValue(Mod2002022Key.BN186 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN966 , mod200old.getDoubleValue(Mod2002022Key.BN968 )) // 2013 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN457 , mod200old.getDoubleValue(Mod2002022Key.BN459 )) // 2013 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN460 , mod200old.getDoubleValue(Mod2002022Key.BN462 )) // 2013 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1063, mod200old.getDoubleValue(Mod2002022Key.BN1065)) // 2014 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1066, mod200old.getDoubleValue(Mod2002022Key.BN1068)) // 2014 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1069, mod200old.getDoubleValue(Mod2002022Key.BN1071)) // 2014 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2294, mod200old.getDoubleValue(Mod2002022Key.BN2296)) // 2015 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN986 , mod200old.getDoubleValue(Mod2002022Key.BN507 )) // 2015 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN557 , mod200old.getDoubleValue(Mod2002022Key.BN594 )) // 2015 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2081, mod200old.getDoubleValue(Mod2002022Key.BN2083)) // 2015 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2297, mod200old.getDoubleValue(Mod2002022Key.BN2299)) // 2016 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1617, mod200old.getDoubleValue(Mod2002022Key.BN1619)) // 2016 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1620, mod200old.getDoubleValue(Mod2002022Key.BN1622)) // 2016 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2084, mod200old.getDoubleValue(Mod2002022Key.BN2086)) // 2016 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2300, mod200old.getDoubleValue(Mod2002022Key.BN2087)) // 2017 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1850, mod200old.getDoubleValue(Mod2002022Key.BN1852)) // 2017 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1853, mod200old.getDoubleValue(Mod2002022Key.BN1855)) // 2017 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2088, mod200old.getDoubleValue(Mod2002022Key.BN2090)) // 2017 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2091, mod200old.getDoubleValue(Mod2002022Key.BN2093)) // 2018 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2221, mod200old.getDoubleValue(Mod2002022Key.BN2223)) // 2018 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2224, mod200old.getDoubleValue(Mod2002022Key.BN2226)) // 2018 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1916, mod200old.getDoubleValue(Mod2002022Key.BN1918)) // 2018 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2094, mod200old.getDoubleValue(Mod2002022Key.BN2096)) // 2019 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2356, mod200old.getDoubleValue(Mod2002022Key.BN2358)) // 2019 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2359, mod200old.getDoubleValue(Mod2002022Key.BN2361)) // 2019 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1919, mod200old.getDoubleValue(Mod2002022Key.BN1921)) // 2019 TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2097, mod200old.getDoubleValue(Mod2002022Key.BN2099)) // 2020 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN228 , mod200old.getDoubleValue(Mod2002022Key.BN230) ) // 2020 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN234 , mod200old.getDoubleValue(Mod2002022Key.BN236) ) // 2020 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1922, mod200old.getDoubleValue(Mod2002022Key.BN1924)) // 2020 TAP			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2145, mod200old.getDoubleValue(Mod2002022Key.BN2448)) // 2021 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN780 , mod200old.getDoubleValue(Mod2002022Key.BN782 )) // 2021 CT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN786 , mod200old.getDoubleValue(Mod2002022Key.BN788 )) // 2021 IT                 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1925, mod200old.getDoubleValue(Mod2002022Key.BN1927)) // 2021 TAP			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1848, mod200old.getDoubleValue(Mod2002022Key.BN2461)+ // 2022 excepto I+D+i y TAP
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN830)+					                                                                   
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN809)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2464)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1077)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2457)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN797)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN794)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN889)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1371)+					                                                                   
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1628)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1640)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1709)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1909)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1912)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1936)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2364)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2367)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2370)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2373)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2376)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2379)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN258)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN261)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN264)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN270)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN274)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN293)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN296)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN299)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN319)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN350)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN354)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN369)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN405)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN419)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN424)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN429)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN432)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN435)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN441)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN454)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN463)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN470)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN481)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN503)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN513)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN537)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN595)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN811)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN817)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN2460)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN877)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN882)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1870)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN956)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1088)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1141)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1145)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1151)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1154)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1157)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1195)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1208)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1219)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1222)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1232)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1236)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1239)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1263)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1266)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1269)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1274)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1279)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1283)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN1885))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1874, mod200old.getDoubleValue(Mod2002022Key.BN1365)+  // 2022 CT
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN800 ))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1894, mod200old.getDoubleValue(Mod2002022Key.BN1368)+  // 2022 IT
					                                                                   mod200old.getDoubleValue(Mod2002022Key.BN713 ))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1897, mod200old.getDoubleValue(Mod2002022Key.BN1930)+  // 2022 TAP
                                                                                       mod200old.getDoubleValue(Mod2002022Key.BN2192))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2449, mod200old.getDoubleValue(Mod2002022Key.BN1685))  // 2023
			
			// Deducción por inversiones y gastos realizados por las autoridades portuarias (art. 38 bis LIS)
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1284, mod200old.getDoubleValue(Mod2002022Key.BN1288))  // 2020 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1289, mod200old.getDoubleValue(Mod2002022Key.BN1291))  // 2021 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1292, mod200old.getDoubleValue(Mod2002022Key.BN1294)+  //  
			                                                                           mod200old.getDoubleValue(Mod2002022Key.BN1297))  // 2022
						
			// Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS)
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1931, mod200old.getDoubleValue(Mod2002022Key.BN1937))  // 2015 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1938, mod200old.getDoubleValue(Mod2002022Key.BN1941))  // 2016 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1942, mod200old.getDoubleValue(Mod2002022Key.BN1945))  // 2017 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1946, mod200old.getDoubleValue(Mod2002022Key.BN1949))  // 2018 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2109, mod200old.getDoubleValue(Mod2002022Key.BN2112))  // 2019 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2128, mod200old.getDoubleValue(Mod2002022Key.BN2131))  // 2020 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2132, mod200old.getDoubleValue(Mod2002022Key.BN2135))  // 2021 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2136, mod200old.getDoubleValue(Mod2002022Key.BN2139)+  //  
			                                                                           mod200old.getDoubleValue(Mod2002022Key.BN2143))  // 2022 

			// Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994)
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2148, mod200old.getDoubleValue(Mod2002022Key.BN2151))  // 2015 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2152, mod200old.getDoubleValue(Mod2002022Key.BN2155))  // 2016 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2156, mod200old.getDoubleValue(Mod2002022Key.BN2159))  // 2017 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2160, mod200old.getDoubleValue(Mod2002022Key.BN2163))  // 2018 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2164, mod200old.getDoubleValue(Mod2002022Key.BN2167))  // 2019 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2168, mod200old.getDoubleValue(Mod2002022Key.BN2171))  // 2020 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2172, mod200old.getDoubleValue(Mod2002022Key.BN2175))  // 2021 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1309, mod200old.getDoubleValue(Mod2002022Key.BN1312)+  //  
			                                                                           mod200old.getDoubleValue(Mod2002022Key.BN1316))  // 2022 
			
		})
			
		,PAG18TER ( new IPropertyFiller[] {
				
			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002 - Donaciones de carácter general

			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN990 , mod200old.getDoubleValue(Mod2002022Key.BN992) ) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN997 , mod200old.getDoubleValue(Mod2002022Key.BN999) ) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN246 , mod200old.getDoubleValue(Mod2002022Key.BN248) ) // 2015 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN818 , mod200old.getDoubleValue(Mod2002022Key.BN820) ) // 2015 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN993 , mod200old.getDoubleValue(Mod2002022Key.BN995) ) // 2016 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN821 , mod200old.getDoubleValue(Mod2002022Key.BN834) ) // 2016 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1434, mod200old.getDoubleValue(Mod2002022Key.BN1436)) // 2017 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN835 , mod200old.getDoubleValue(Mod2002022Key.BN837) ) // 2017 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1718, mod200old.getDoubleValue(Mod2002022Key.BN1720)) // 2018 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN838 , mod200old.getDoubleValue(Mod2002022Key.BN840) ) // 2018 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1950, mod200old.getDoubleValue(Mod2002022Key.BN1952)) // 2019 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN842 , mod200old.getDoubleValue(Mod2002022Key.BN845) ) // 2019 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2227, mod200old.getDoubleValue(Mod2002022Key.BN2229)) // 2020 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN868 , mod200old.getDoubleValue(Mod2002022Key.BN871) ) // 2020 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2380, mod200old.getDoubleValue(Mod2002022Key.BN2382)) // 2021 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN872 , mod200old.getDoubleValue(Mod2002022Key.BN2498)) // 2021 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2499, mod200old.getDoubleValue(Mod2002022Key.BN890)+mod200old.getDoubleValue(Mod2002022Key.BN1325)) // 2022 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN891 , mod200old.getDoubleValue(Mod2002022Key.BN893)+mod200old.getDoubleValue(Mod2002022Key.BN1328)) // 2022 Con			

			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002 - Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN903 , mod200old.getDoubleValue(Mod2002022Key.BN929) ) // 2013    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN930 , mod200old.getDoubleValue(Mod2002022Key.BN932) ) // 2014    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN933 , mod200old.getDoubleValue(Mod2002022Key.BN942) ) // 2015 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN943 , mod200old.getDoubleValue(Mod2002022Key.BN948) ) // 2015 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN949 , mod200old.getDoubleValue(Mod2002022Key.BN951) ) // 2016 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN952 , mod200old.getDoubleValue(Mod2002022Key.BN954) ) // 2016 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2472, mod200old.getDoubleValue(Mod2002022Key.BN2474)) // 2017 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN958 , mod200old.getDoubleValue(Mod2002022Key.BN963) ) // 2017 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN964 , mod200old.getDoubleValue(Mod2002022Key.BN969) ) // 2018 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN970 , mod200old.getDoubleValue(Mod2002022Key.BN972) ) // 2018 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN973 , mod200old.getDoubleValue(Mod2002022Key.BN979) ) // 2019 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN980 , mod200old.getDoubleValue(Mod2002022Key.BN982) ) // 2019 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN983 , mod200old.getDoubleValue(Mod2002022Key.BN985) ) // 2020 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1000, mod200old.getDoubleValue(Mod2002022Key.BN1007)) // 2020 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1008, mod200old.getDoubleValue(Mod2002022Key.BN1024)) // 2021 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1025, mod200old.getDoubleValue(Mod2002022Key.BN1036)) // 2021 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1061, mod200old.getDoubleValue(Mod2002022Key.BN1072)+mod200old.getDoubleValue(Mod2002022Key.BN1373)) // 2022 Sin    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1073, mod200old.getDoubleValue(Mod2002022Key.BN1078)+mod200old.getDoubleValue(Mod2002022Key.BN1376)) // 2022 Con	   
			
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
			
		    // Dado que el importe pendiente está en funcion del importe resultante de aplicar a la base de deduccion un porcentaje, 
			// para obtener ahora esa base de deducción, según el importe pendiente del año anterior, habra que hacer la operacion inversa
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1166, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1169)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1438, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1441)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1442, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1445)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1721, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1724)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1953, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1956)/0.05)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2230, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN2233)/0.05)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2383, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN2386)/0.05)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1082, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1085)/0.05)+
                    																   AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1380)/0.05)) // 2022
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)                                                                          
			// Ejercicio 2015 = 2%  [01179] = [01178] x 0,02
			// Ejercicio 2016 = 5%  [01448] = [01447] x 0,05
			// Ejercicio 2017 = 5%  [01452] = [01451] x 0,05
			// Ejercicio 2018 = 5%  [01726] = [01725] x 0,05
			// Ejercicio 2019 = 5%  [01958] = [01957] x 0,05
  			// Ejercicio 2020 = 5%  [02235] = [02234] x 0,05
			// Ejercicio 2021 = 5%  [02388] = [02387] x 0,05
			// Ejercicio 2022 = 5%  [02477] = [01086] x 0,05
  			                        
  		    // idem apartado anterior
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1178, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1181)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1447, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1450)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1451, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1454)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1725, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1728)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1957, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1960)/0.05)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2234, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN2237)/0.05)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN2387, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN2390)/0.05)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.BN1086, AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN2479)/0.05)+
       			   																	   AonMathUtils.round(mod200old.getDoubleValue(Mod2002022Key.BN1384)/0.05)) // 2022
			
			// FALTA - APARTADO DEDUCCIONES I+D+i EXCLUIDAS DEL LIMITE (DESGLOSE 00082) NO ESTA PUESTO EN AÑOS ANTERIORES
			// CREO QUE ES PORQUE NO HAY COLUMNA DE PENDIENTE, IGUAL SE PODRIA SACAR POR LA DIFERENCIA DE REDUCIDA - APLICADO - ABONADO
			// TENIENDO EN CUENTA QUE LA REDUCIDA ES UN 0.8 DE LA PENDIENTE ??
			
		})

		,PAG20 ( new IPropertyFiller[] {
				
			// Limitacion en la deducilidad de gastos financieros. Gastos financieros pendientes de deducir
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1188, mod200old.getDoubleValue(Mod2002022Key.LM1191)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1193, mod200old.getDoubleValue(Mod2002022Key.LM1196)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1198, mod200old.getDoubleValue(Mod2002022Key.LM1201)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1202, mod200old.getDoubleValue(Mod2002022Key.LM1205)) // 2015 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1203, mod200old.getDoubleValue(Mod2002022Key.LM1206)) // 2015 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1462, mod200old.getDoubleValue(Mod2002022Key.LM1210)) // 2016 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1463, mod200old.getDoubleValue(Mod2002022Key.LM1211)) // 2016 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1736, mod200old.getDoubleValue(Mod2002022Key.LM1465)) // 2017 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1737, mod200old.getDoubleValue(Mod2002022Key.LM1466)) // 2017 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1977, mod200old.getDoubleValue(Mod2002022Key.LM1739)) // 2018 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1978, mod200old.getDoubleValue(Mod2002022Key.LM1740)) // 2018 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2253, mod200old.getDoubleValue(Mod2002022Key.LM1980)) // 2019 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2254, mod200old.getDoubleValue(Mod2002022Key.LM1981)) // 2019 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2399, mod200old.getDoubleValue(Mod2002022Key.LM2256)) // 2020 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2400, mod200old.getDoubleValue(Mod2002022Key.LM2257)) // 2020 Resto			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1098, mod200old.getDoubleValue(Mod2002022Key.LM2402)) // 2021 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1099, mod200old.getDoubleValue(Mod2002022Key.LM2403)) // 2021 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1393, mod200old.getDoubleValue(Mod2002022Key.LM1101)+mod200old.getDoubleValue(Mod2002022Key.LM1396)) // 2022 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1394, mod200old.getDoubleValue(Mod2002022Key.LM1102)+mod200old.getDoubleValue(Mod2002022Key.LM1397)) // 2022 Resto
			
			// Pendiente de adición por límite beneficio operativo no aplicado			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1741, mod200old.getDoubleValue(Mod2002022Key.LM1743)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1982, mod200old.getDoubleValue(Mod2002022Key.LM1984)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2258, mod200old.getDoubleValue(Mod2002022Key.LM2260)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2404, mod200old.getDoubleValue(Mod2002022Key.LM2406)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1103, mod200old.getDoubleValue(Mod2002022Key.LM1105)+mod200old.getDoubleValue(Mod2002022Key.LM1400)) // 2022

		})
			
		,PAG20BIS ( new IPropertyFiller[] {
			
			// Reserva de capitalización
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ2407, mod200old.getDoubleValue(Mod2002022Key.LQ2409)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1106, mod200old.getDoubleValue(Mod2002022Key.LQ1108)+mod200old.getDoubleValue(Mod2002022Key.LQ1403)) // 2022
			
			// Reserva de nivelación - Reducción en base imponible 	
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1961, mod200old.getDoubleValue(Mod2002022Key.LQ1963)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ2238, mod200old.getDoubleValue(Mod2002022Key.LQ2240)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ2410, mod200old.getDoubleValue(Mod2002022Key.LQ2412)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1109, mod200old.getDoubleValue(Mod2002022Key.LQ1111)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1406, mod200old.getDoubleValue(Mod2002022Key.LQ1407)+mod200old.getDoubleValue(Mod2002022Key.LQ1731))  // 2022
			
			// FALTA - DOTACION DE LA RESERVA NO LO HE COPIADO NINGUN AÑO VER SI ESTE AÑO SE PUEDE COPIAR ALGO DEL AÑO PASADO
			
		})
		
		,PAG20TER ( new IPropertyFiller[] {
				
			// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS  
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1524, mod200old.getDoubleValue(Mod2002022Key.LM1528)) // 2007 y anteriores
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1529, mod200old.getDoubleValue(Mod2002022Key.LM1534)) // 2008 a 2015
				
			// Activos por impuesto diferido (AID). Art. 130 LIS
			// FALTA - ESTO NO SE SI ESTA BIEN, PUES DE PENDIENTE HAY 3 COLUMNAS QUE TAMBIEN ESTAN CON LA MISMA DESCRIPCION, PERO LO ESTOY LLEVANDO A LA PRIMERA COLUMNA QUE TIENE OTRA DESCRIPCION
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1542, mod200old.getDoubleValue(Mod2002022Key.LM1549)+mod200old.getDoubleValue(Mod2002022Key.LM1550)+mod200old.getDoubleValue(Mod2002022Key.LM1551)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1552, mod200old.getDoubleValue(Mod2002022Key.LM1558)+mod200old.getDoubleValue(Mod2002022Key.LM1559)+mod200old.getDoubleValue(Mod2002022Key.LM1560)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1754, mod200old.getDoubleValue(Mod2002022Key.LM1760)+mod200old.getDoubleValue(Mod2002022Key.LM1761)+mod200old.getDoubleValue(Mod2002022Key.LM1762)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2100, mod200old.getDoubleValue(Mod2002022Key.LM2106)+mod200old.getDoubleValue(Mod2002022Key.LM2107)+mod200old.getDoubleValue(Mod2002022Key.LM2108)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2268, mod200old.getDoubleValue(Mod2002022Key.LM2274)+mod200old.getDoubleValue(Mod2002022Key.LM2275)+mod200old.getDoubleValue(Mod2002022Key.LM2276)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2418, mod200old.getDoubleValue(Mod2002022Key.LM2424)+mod200old.getDoubleValue(Mod2002022Key.LM2425)+mod200old.getDoubleValue(Mod2002022Key.LM2426)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1117, mod200old.getDoubleValue(Mod2002022Key.LM1131)+mod200old.getDoubleValue(Mod2002022Key.LM1132)+mod200old.getDoubleValue(Mod2002022Key.LM1133)+
					                                                                   mod200old.getDoubleValue(Mod2002022Key.LM1420)+mod200old.getDoubleValue(Mod2002022Key.LM1421)+mod200old.getDoubleValue(Mod2002022Key.LM1422)) // 2022
			
			// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2427, mod200old.getDoubleValue(Mod2002022Key.LM2430)) // 2021
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1134, mod200old.getDoubleValue(Mod2002022Key.LM1138)+mod200old.getDoubleValue(Mod2002022Key.LM1469)) // 2022
			
		})
		
		,PAG20QUA ( new IPropertyFiller[] {

            // Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
            // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible            
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1473, mod200old.getDoubleValue(Mod2002022Key.LM1476)) // 2007 y anteriores: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1408, mod200old.getDoubleValue(Mod2002022Key.LM1409)) // 2007 y anteriores: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1477, mod200old.getDoubleValue(Mod2002022Key.LM1483)) // 2008 a 2015: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1478, mod200old.getDoubleValue(Mod2002022Key.LM1484)) // 2008 a 2015: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1485, mod200old.getDoubleValue(Mod2002022Key.LM1489)) // 2016: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1486, mod200old.getDoubleValue(Mod2002022Key.LM1490)) // 2016: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1491, mod200old.getDoubleValue(Mod2002022Key.LM1493)) // 2017: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1747, mod200old.getDoubleValue(Mod2002022Key.LM1749)) // 2017: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1750, mod200old.getDoubleValue(Mod2002022Key.LM1752)) // 2018: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1988, mod200old.getDoubleValue(Mod2002022Key.LM1990)) // 2018: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1991, mod200old.getDoubleValue(Mod2002022Key.LM1993)) // 2019: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2261, mod200old.getDoubleValue(Mod2002022Key.LM2263)) // 2019: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2264, mod200old.getDoubleValue(Mod2002022Key.LM2266)) // 2020: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2431, mod200old.getDoubleValue(Mod2002022Key.LM2433)) // 2020: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM2434, mod200old.getDoubleValue(Mod2002022Key.LM2436)) // 2021: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1143, mod200old.getDoubleValue(Mod2002022Key.LM1192)) // 2021: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1162, mod200old.getDoubleValue(Mod2002022Key.LM1164)+mod200old.getDoubleValue(Mod2002022Key.LM1500)) // 2022: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LM1470, mod200old.getDoubleValue(Mod2002022Key.LM1915)) // 2022: Que han cumplido ...
			
		})
				
		,PAG21 ( new IPropertyFiller[] {
				  
			     (mod200old,mod200new) -> mod200new.getGroupEntities().addAll(mod200old.getGroupEntities())    // NIF de las entidades del grupo
			    ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.CNEST , mod200old.getDoubleValue(Mod2002022Key.CNEST)) // Número de establecimientos permanentes a través de los que opera, en caso de persona física titular
			    ,(mod200old,mod200new) -> mod200new.getEstablishments().addAll(mod200old.getEstablishments())  // NIF de los establecimientos permanentes		    
			    
		})
		
        ,PAG22 ( new IPropertyFiller[] {
        		
    		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC        	  			
             (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC524 , mod200old.getDoubleValue(Mod2002022Key.RC527) ) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC922 , mod200old.getDoubleValue(Mod2002022Key.RC925) ) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC1165, mod200old.getDoubleValue(Mod2002022Key.RC996) ) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC1744, mod200old.getDoubleValue(Mod2002022Key.RC1175)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC2807, mod200old.getDoubleValue(Mod2002022Key.RC1821)) // 2022
            
            // Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC2446, mod200old.getDoubleValue(Mod2002022Key.RC2447)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC1176, mod200old.getDoubleValue(Mod2002022Key.RC2451)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC1823, mod200old.getDoubleValue(Mod2002022Key.RC1184)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.RC2823, mod200old.getDoubleValue(Mod2002022Key.RC1600)) // 2022

        	// Régimen de cooperativas - Detalle de compensación de cuotas	                                                                				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ673 , mod200old.getDoubleValue(Mod2002022Key.LQ1224)) // 2000 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ676 , mod200old.getDoubleValue(Mod2002022Key.LQ678 )) // 2001				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ679 , mod200old.getDoubleValue(Mod2002022Key.LQ681 )) // 2002				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ682 , mod200old.getDoubleValue(Mod2002022Key.LQ684 )) // 2003				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ685 , mod200old.getDoubleValue(Mod2002022Key.LQ687 )) // 2004				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ688 , mod200old.getDoubleValue(Mod2002022Key.LQ690 )) // 2005				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ691 , mod200old.getDoubleValue(Mod2002022Key.LQ693 )) // 2006				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ623 , mod200old.getDoubleValue(Mod2002022Key.LQ672 )) // 2007				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ279 , mod200old.getDoubleValue(Mod2002022Key.LQ281 )) // 2008				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ587 , mod200old.getDoubleValue(Mod2002022Key.LQ900 )) // 2009				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ059 , mod200old.getDoubleValue(Mod2002022Key.LQ100 )) // 2010				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ017 , mod200old.getDoubleValue(Mod2002022Key.LQ019 )) // 2011				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ772 , mod200old.getDoubleValue(Mod2002022Key.LQ777 )) // 2012				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ907 , mod200old.getDoubleValue(Mod2002022Key.LQ909 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ910 , mod200old.getDoubleValue(Mod2002022Key.LQ912 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ935 , mod200old.getDoubleValue(Mod2002022Key.LQ937 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1511, mod200old.getDoubleValue(Mod2002022Key.LQ1513)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1767, mod200old.getDoubleValue(Mod2002022Key.LQ1769)) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ2113, mod200old.getDoubleValue(Mod2002022Key.LQ2115)) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ2281, mod200old.getDoubleValue(Mod2002022Key.LQ2283)) // 2019            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ2452, mod200old.getDoubleValue(Mod2002022Key.LQ2454)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1186, mod200old.getDoubleValue(Mod2002022Key.LQ1190)) // 2021
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023Key.LQ1516, mod200old.getDoubleValue(Mod2002022Key.LQ1518)+ // 2022 (Tambien se añade la casilla 560 del ejercicio anterior si es negativa y es cooperativa y no está puesto ese importe en la 1226)
            		                                                                   mod200old.getDoubleValue(Mod2002022Key.LQ1226)+
            		                                                                  ((mod200old.getDoubleValue(Mod2002022Key.C0017)==1 || mod200old.getDoubleValue(Mod2002022Key.C0018)==1 || mod200old.getDoubleValue(Mod2002022Key.C0019)==1) && (mod200old.getDoubleValue(Mod2002022Key.LQ560)<0) && (mod200old.getDoubleValue(Mod2002022Key.LQ1226)==0) ? Math.abs(mod200old.getDoubleValue(Mod2002022Key.LQ560)) : 0) )  
			
		})
        
		,PAG24BIS  ( new IPropertyFiller[] {
				
				// UTE - Relación de partícipes
			    (mod200old,mod200new) -> mod200new.getUteParticipations().addAll(mod200old.getUteParticipations()) 
			    
		})
        
        ,PAG26BIS ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias	
	      	  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2504, mod200old.getDoubleValue(Mod2002022KeyDC.DC2505))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2509, mod200old.getDoubleValue(Mod2002022KeyDC.DC2510))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2514, mod200old.getDoubleValue(Mod2002022KeyDC.DC2515))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2519, mod200old.getDoubleValue(Mod2002022KeyDC.DC2520))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2524, mod200old.getDoubleValue(Mod2002022KeyDC.DC2525))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2529, mod200old.getDoubleValue(Mod2002022KeyDC.DC2530))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2534, mod200old.getDoubleValue(Mod2002022KeyDC.DC2535))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2539, mod200old.getDoubleValue(Mod2002022KeyDC.DC2540))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2544, mod200old.getDoubleValue(Mod2002022KeyDC.DC2545))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2549, mod200old.getDoubleValue(Mod2002022KeyDC.DC2550))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2554, mod200old.getDoubleValue(Mod2002022KeyDC.DC2555))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2559, mod200old.getDoubleValue(Mod2002022KeyDC.DC2560))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2564, mod200old.getDoubleValue(Mod2002022KeyDC.DC2565))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2569, mod200old.getDoubleValue(Mod2002022KeyDC.DC2570))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2579, mod200old.getDoubleValue(Mod2002022KeyDC.DC2580))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2584, mod200old.getDoubleValue(Mod2002022KeyDC.DC2585))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2589, mod200old.getDoubleValue(Mod2002022KeyDC.DC2590))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2594, mod200old.getDoubleValue(Mod2002022KeyDC.DC2595))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2599, mod200old.getDoubleValue(Mod2002022KeyDC.DC2600))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2604, mod200old.getDoubleValue(Mod2002022KeyDC.DC2605))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2609, mod200old.getDoubleValue(Mod2002022KeyDC.DC2610))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2614, mod200old.getDoubleValue(Mod2002022KeyDC.DC2615))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2619, mod200old.getDoubleValue(Mod2002022KeyDC.DC2620))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2624, mod200old.getDoubleValue(Mod2002022KeyDC.DC2625))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2629, mod200old.getDoubleValue(Mod2002022KeyDC.DC2630))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2634, mod200old.getDoubleValue(Mod2002022KeyDC.DC2635))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2639, mod200old.getDoubleValue(Mod2002022KeyDC.DC2640))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2644, mod200old.getDoubleValue(Mod2002022KeyDC.DC2645))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2649, mod200old.getDoubleValue(Mod2002022KeyDC.DC2650))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2654, mod200old.getDoubleValue(Mod2002022KeyDC.DC2655))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2659, mod200old.getDoubleValue(Mod2002022KeyDC.DC2660))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2664, mod200old.getDoubleValue(Mod2002022KeyDC.DC2665))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2669, mod200old.getDoubleValue(Mod2002022KeyDC.DC2670))
    		  
        })
        
        ,PAG26TER ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
    	      	
    	      (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2674, mod200old.getDoubleValue(Mod2002022KeyDC.DC2675))
    	    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2679, mod200old.getDoubleValue(Mod2002022KeyDC.DC2680))
      		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2684, mod200old.getDoubleValue(Mod2002022KeyDC.DC2685))
      		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2689, mod200old.getDoubleValue(Mod2002022KeyDC.DC2690))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2714, mod200old.getDoubleValue(Mod2002022KeyDC.DC2715))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2719, mod200old.getDoubleValue(Mod2002022KeyDC.DC2720))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2724, mod200old.getDoubleValue(Mod2002022KeyDC.DC2725))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2729, mod200old.getDoubleValue(Mod2002022KeyDC.DC2730))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2734, mod200old.getDoubleValue(Mod2002022KeyDC.DC2735))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2739, mod200old.getDoubleValue(Mod2002022KeyDC.DC2740))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2744, mod200old.getDoubleValue(Mod2002022KeyDC.DC2745))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2749, mod200old.getDoubleValue(Mod2002022KeyDC.DC2750))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2814, mod200old.getDoubleValue(Mod2002022KeyDC.DC2815))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2819, mod200old.getDoubleValue(Mod2002022KeyDC.DC2820))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2574, mod200old.getDoubleValue(Mod2002022KeyDC.DC2575))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2754, mod200old.getDoubleValue(Mod2002022KeyDC.DC2755))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC1841, mod200old.getDoubleValue(Mod2002022KeyDC.DC1845))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC1846, mod200old.getDoubleValue(Mod2002022KeyDC.DC1859))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2854, mod200old.getDoubleValue(Mod2002022KeyDC.DC2855))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2859, mod200old.getDoubleValue(Mod2002022KeyDC.DC2860))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2864, mod200old.getDoubleValue(Mod2002022KeyDC.DC2865))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2869, mod200old.getDoubleValue(Mod2002022KeyDC.DC2870))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2874, mod200old.getDoubleValue(Mod2002022KeyDC.DC2875))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2879, mod200old.getDoubleValue(Mod2002022KeyDC.DC2880))
    		
        })
        
        ,PAG26QUA ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)        		
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2884, mod200old.getDoubleValue(Mod2002022KeyDC.DC2885))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2889, mod200old.getDoubleValue(Mod2002022KeyDC.DC2890))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2894, mod200old.getDoubleValue(Mod2002022KeyDC.DC2895))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2899, mod200old.getDoubleValue(Mod2002022KeyDC.DC2900))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2904, mod200old.getDoubleValue(Mod2002022KeyDC.DC2905))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2909, mod200old.getDoubleValue(Mod2002022KeyDC.DC2910))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2924, mod200old.getDoubleValue(Mod2002022KeyDC.DC2925))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2929, mod200old.getDoubleValue(Mod2002022KeyDC.DC2930))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2934, mod200old.getDoubleValue(Mod2002022KeyDC.DC2935))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2939, mod200old.getDoubleValue(Mod2002022KeyDC.DC2940))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC1676, mod200old.getDoubleValue(Mod2002022KeyDC.DC1680))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC1681, mod200old.getDoubleValue(Mod2002022KeyDC.DC1688))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2954, mod200old.getDoubleValue(Mod2002022KeyDC.DC2955))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2959, mod200old.getDoubleValue(Mod2002022KeyDC.DC2960))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2984, mod200old.getDoubleValue(Mod2002022KeyDC.DC2985))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2989, mod200old.getDoubleValue(Mod2002022KeyDC.DC2990))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2994, mod200old.getDoubleValue(Mod2002022KeyDC.DC2995))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2999, mod200old.getDoubleValue(Mod2002022KeyDC.DC3000))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3004, mod200old.getDoubleValue(Mod2002022KeyDC.DC3005))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3009, mod200old.getDoubleValue(Mod2002022KeyDC.DC3010))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3014, mod200old.getDoubleValue(Mod2002022KeyDC.DC3015))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3019, mod200old.getDoubleValue(Mod2002022KeyDC.DC3020))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3024, mod200old.getDoubleValue(Mod2002022KeyDC.DC3025))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3029, mod200old.getDoubleValue(Mod2002022KeyDC.DC3030))
  		  	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3034, mod200old.getDoubleValue(Mod2002022KeyDC.DC3035))
  		  	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3039, mod200old.getDoubleValue(Mod2002022KeyDC.DC3040))
    		
        })
        
        ,PAG26QUI ( new IPropertyFiller[] {
        	
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3044, mod200old.getDoubleValue(Mod2002022KeyDC.DC3045))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3049, mod200old.getDoubleValue(Mod2002022KeyDC.DC3050))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3054, mod200old.getDoubleValue(Mod2002022KeyDC.DC3055))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3059, mod200old.getDoubleValue(Mod2002022KeyDC.DC3060))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3074, mod200old.getDoubleValue(Mod2002022KeyDC.DC3075))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3079, mod200old.getDoubleValue(Mod2002022KeyDC.DC3080))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3084, mod200old.getDoubleValue(Mod2002022KeyDC.DC3085))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3089, mod200old.getDoubleValue(Mod2002022KeyDC.DC3090))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3094, mod200old.getDoubleValue(Mod2002022KeyDC.DC3095))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3099, mod200old.getDoubleValue(Mod2002022KeyDC.DC3100))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3104, mod200old.getDoubleValue(Mod2002022KeyDC.DC3105))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3109, mod200old.getDoubleValue(Mod2002022KeyDC.DC3110))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3114, mod200old.getDoubleValue(Mod2002022KeyDC.DC3115))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3119, mod200old.getDoubleValue(Mod2002022KeyDC.DC3120))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3124, mod200old.getDoubleValue(Mod2002022KeyDC.DC3125))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3129, mod200old.getDoubleValue(Mod2002022KeyDC.DC3130))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3134, mod200old.getDoubleValue(Mod2002022KeyDC.DC3135))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3139, mod200old.getDoubleValue(Mod2002022KeyDC.DC3140))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3144, mod200old.getDoubleValue(Mod2002022KeyDC.DC3145))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3149, mod200old.getDoubleValue(Mod2002022KeyDC.DC3150))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3154, mod200old.getDoubleValue(Mod2002022KeyDC.DC3155))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3159, mod200old.getDoubleValue(Mod2002022KeyDC.DC3160))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3164, mod200old.getDoubleValue(Mod2002022KeyDC.DC3165))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3169, mod200old.getDoubleValue(Mod2002022KeyDC.DC3170))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3174, mod200old.getDoubleValue(Mod2002022KeyDC.DC3175))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3179, mod200old.getDoubleValue(Mod2002022KeyDC.DC3180))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3184, mod200old.getDoubleValue(Mod2002022KeyDC.DC3185))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3189, mod200old.getDoubleValue(Mod2002022KeyDC.DC3190))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3194, mod200old.getDoubleValue(Mod2002022KeyDC.DC3195))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3199, mod200old.getDoubleValue(Mod2002022KeyDC.DC3200))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3204, mod200old.getDoubleValue(Mod2002022KeyDC.DC3205))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3209, mod200old.getDoubleValue(Mod2002022KeyDC.DC3210))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3214, mod200old.getDoubleValue(Mod2002022KeyDC.DC3215))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3219, mod200old.getDoubleValue(Mod2002022KeyDC.DC3220))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3224, mod200old.getDoubleValue(Mod2002022KeyDC.DC3225))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3229, mod200old.getDoubleValue(Mod2002022KeyDC.DC3230))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3234, mod200old.getDoubleValue(Mod2002022KeyDC.DC3235))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3239, mod200old.getDoubleValue(Mod2002022KeyDC.DC3240))
        		
        })
        
        ,PAG26SEX ( new IPropertyFiller[] {
        	
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3254, mod200old.getDoubleValue(Mod2002022KeyDC.DC3255))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3259, mod200old.getDoubleValue(Mod2002022KeyDC.DC3260))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3264, mod200old.getDoubleValue(Mod2002022KeyDC.DC3265))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3269, mod200old.getDoubleValue(Mod2002022KeyDC.DC3270))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3274, mod200old.getDoubleValue(Mod2002022KeyDC.DC3275))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3279, mod200old.getDoubleValue(Mod2002022KeyDC.DC3280))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3294, mod200old.getDoubleValue(Mod2002022KeyDC.DC3295))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3299, mod200old.getDoubleValue(Mod2002022KeyDC.DC3300))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3304, mod200old.getDoubleValue(Mod2002022KeyDC.DC3305))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3309, mod200old.getDoubleValue(Mod2002022KeyDC.DC3310))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC1860, mod200old.getDoubleValue(Mod2002022KeyDC.DC1864))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC1865, mod200old.getDoubleValue(Mod2002022KeyDC.DC1869))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2176, mod200old.getDoubleValue(Mod2002022KeyDC.DC2180))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC2289, mod200old.getDoubleValue(Mod2002022KeyDC.DC2293))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3324, mod200old.getDoubleValue(Mod2002022KeyDC.DC3325))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3329, mod200old.getDoubleValue(Mod2002022KeyDC.DC3330))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3334, mod200old.getDoubleValue(Mod2002022KeyDC.DC3335))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3339, mod200old.getDoubleValue(Mod2002022KeyDC.DC3340))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3344, mod200old.getDoubleValue(Mod2002022KeyDC.DC3345))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3349, mod200old.getDoubleValue(Mod2002022KeyDC.DC3350))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3364, mod200old.getDoubleValue(Mod2002022KeyDC.DC3365))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3369, mod200old.getDoubleValue(Mod2002022KeyDC.DC3370))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3374, mod200old.getDoubleValue(Mod2002022KeyDC.DC3375))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3379, mod200old.getDoubleValue(Mod2002022KeyDC.DC3380))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3384, mod200old.getDoubleValue(Mod2002022KeyDC.DC3385))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3389, mod200old.getDoubleValue(Mod2002022KeyDC.DC3390))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3394, mod200old.getDoubleValue(Mod2002022KeyDC.DC3395))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002023KeyDC.DC3399, mod200old.getDoubleValue(Mod2002022KeyDC.DC3400))
        		
        })
		;
		
		private IPropertyFiller[] propertyFillers;
		
		private Pages2023 (IPropertyFiller[] propertyFillers) {
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Mod2002022 mod200old, Mod2002023 mod200new) {
			for ( Pages2023 page : Pages2023.values()) {
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(mod200old, mod200new);
					}				
			}
		}
	}
	
	public static void import2022(Mod2002023 mod200new, Mod2002022 mod200old) {
		try {
			Pages2023.fill(mod200old, mod200new);
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
	}

//	public static Mod2002023 import2022(Mod2002022 mod200old) {
//		
//		try {
//			// Creamos el objeto Mod200 del ejercicio actual, donde importaremos los datos del ejercicio anterior
//			Mod2002023 mod200new = new Mod2002023();
//			import2022(mod200new, mod200old);
//			return mod200new;
//		} catch (Exception e) {
//			throw new AonCoreException(e);
//		}
//		
//	}
	
	// ---- PRUEBAS ---- 
	
	private static String formatDate(Date d) {
		if (d==null)
			return null;
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(d);			
		}
	}
	
	private static void toString(Mod2002023 mod200) {
		
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
		for (Mod2002023Key key : Mod2002023Key.values()) {			
			if (mod200.getKey(key) != null) {
				System.out.println(AonStringUtils.join(AonStringUtils.SPACE
						,mod200.getKey(key).getKey()
						," ...: "
						,mod200.getKey(key).getValue()));
			}
		}
		for (Mod2002023KeyDC key : Mod2002023KeyDC.values()) {			
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
	
	private static void setDoubleValue2022(Mod2002022 mod200, IMod200Key key, double value) {
		 
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
	
//	public static void main(String[] argv) {
//		
//		// Esta prueba unicamente crea un objeto del año anterior e inicializa sus casillas con 
//		// los códigos, posteriormente llama a la importacion para ver que se trasladan correctamente
//		try {			
//			Mod2002022 mod200old = new Mod2002022();
//
//			// PRUEBA - Inicializamos todas las claves con sus numeros
//
//			mod200old.setBalanceType(2); // PYMES
//			mod200old.setPygType(2); // PYMES
//			mod200old.setEcpnType(3); // No consta
//
//			for (Mod2002022Key key : Mod2002022Key.values()) {
//				try {
//					setDoubleValue2022(mod200old, key, Double.parseDouble(key.name().substring(2)));
//				} catch (NumberFormatException e) {
//					// nothing
//				}
//			}
//			for (Mod2002022KeyDC key : Mod2002022KeyDC.values()) {
//				try {
//					setDoubleValue2022(mod200old, key, Double.parseDouble(key.name().substring(2)));
//				} catch (NumberFormatException e) {
//					//// nothing
//				}
//			}
//
//			// Prueba base imponible negativa (casilla 552)
//			setDoubleValue2022(mod200old, Mod2002022Key.LQ552, -552);
//
//			// Cooperativas Casillas 17, 18 y 19. Cuota compensacion negativa (casilla 560)
//			setDoubleValue2022(mod200old, Mod2002022Key.C0017, 0);
//			setDoubleValue2022(mod200old, Mod2002022Key.C0018, 0);
//			setDoubleValue2022(mod200old, Mod2002022Key.C0019, 0);
//			setDoubleValue2022(mod200old, Mod2002022Key.LQ560, 0);
//
//			// FIN PRUEBA
//
//			Mod2002023 mod200new = import2022(mod200old);
//			toString(mod200new);
//				
//		}
//        catch (Exception e) {
//		    e.printStackTrace();
//        }
//		finally {
//			System.exit(0);
//		}
//	}
	
}


