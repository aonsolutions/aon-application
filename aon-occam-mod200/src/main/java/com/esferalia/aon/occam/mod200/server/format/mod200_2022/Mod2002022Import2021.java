package com.esferalia.aon.occam.mod200.server.format.mod200_2022;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022KeyDC;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002022Import2021 {

	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(Mod2002021 mod200old, Mod2002022 mod200new);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Añadir casilla si no existe o asignar el valor a la casilla, al Modelo 200
	private static void setDoubleValue(Mod2002022 mod200, IMod200Key key, double value) {
		 
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
	private static double adjustDoubleTax(double value, double type, double type2022 ) {
		
		if (type2022 != 0 && type != 0 && type != type2022) {
			return AonMathUtils.round( value * type / type2022 );
	    }
		else return value;
		
	}

	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Pages2022 {
		
		 PAG1 ( new IPropertyFiller[] {
				 
			 (mod200old,mod200new) -> mod200new.setPeriodStart(addOneYear(mod200old.getPeriodStart()))  // Periodo Impositivo - Inicio					
			,(mod200old,mod200new) -> mod200new.setPeriodEnd(addOneYear(mod200old.getPeriodEnd()))      // Periodo Impositivo - Fin
			,(mod200old,mod200new) -> mod200new.setPeriodType(mod200old.getPeriodType())                // Identificación - Tipo de ejercicio
			,(mod200old,mod200new) -> mod200new.setCnae(mod200old.getCnae())  			 			  	// Identificación - C.N.A.E.  Actividad principal (convertido a CNAE 2009)
			,(mod200old,mod200new) -> mod200new.setDocument(mod200old.getDocument()) 					// Identificación - NIF 
			,(mod200old,mod200new) -> mod200new.setName(mod200old.getName())          					// Identificación - Apellidos y nombre o Razón Social
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone1(mod200old.getEnterprisePhone1())    // Identificación - Teléfono 1
			,(mod200old,mod200new) -> mod200new.setEnterprisePhone2(mod200old.getEnterprisePhone2())    // Identificación - Teléfono 2
			,(mod200old,mod200new) -> mod200new.setYear(2022)             	                            // Ejercicio
			,(mod200old,mod200new) -> mod200new.setAdministration(mod200old.getAdministration())		// Administracion                                           
			
			// CARACTERES DE LA DECLARACION 
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0001, mod200old.getDoubleValue(Mod2002021Key.C0001))                       
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0002, mod200old.getDoubleValue(Mod2002021Key.C0002))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0080, mod200old.getDoubleValue(Mod2002021Key.C0080))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0003, mod200old.getDoubleValue(Mod2002021Key.C0003))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0004, mod200old.getDoubleValue(Mod2002021Key.C0004))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0005, mod200old.getDoubleValue(Mod2002021Key.C0005))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0011, mod200old.getDoubleValue(Mod2002021Key.C0011))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0013, mod200old.getDoubleValue(Mod2002021Key.C0013))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0014, mod200old.getDoubleValue(Mod2002021Key.C0014))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0017, mod200old.getDoubleValue(Mod2002021Key.C0017))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0018, mod200old.getDoubleValue(Mod2002021Key.C0018))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0019, mod200old.getDoubleValue(Mod2002021Key.C0019))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0021, mod200old.getDoubleValue(Mod2002021Key.C0021))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0023, mod200old.getDoubleValue(Mod2002021Key.C0023))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0024, mod200old.getDoubleValue(Mod2002021Key.C0024))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0025, mod200old.getDoubleValue(Mod2002021Key.C0025))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0031, mod200old.getDoubleValue(Mod2002021Key.C0031))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0032, mod200old.getDoubleValue(Mod2002021Key.C0032))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0036, mod200old.getDoubleValue(Mod2002021Key.C0036))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0048, mod200old.getDoubleValue(Mod2002021Key.C0048))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0058, mod200old.getDoubleValue(Mod2002021Key.C0058))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0060, mod200old.getDoubleValue(Mod2002021Key.C0060))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0066, mod200old.getDoubleValue(Mod2002021Key.C0066))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0078, mod200old.getDoubleValue(Mod2002021Key.C0078))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0006, mod200old.getDoubleValue(Mod2002021Key.C0006))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0015, mod200old.getDoubleValue(Mod2002021Key.C0015))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0079, mod200old.getDoubleValue(Mod2002021Key.C0079))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0022, mod200old.getDoubleValue(Mod2002021Key.C0022))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0028, mod200old.getDoubleValue(Mod2002021Key.C0028))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0047, mod200old.getDoubleValue(Mod2002021Key.C0047))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0049, mod200old.getDoubleValue(Mod2002021Key.C0049))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0035, mod200old.getDoubleValue(Mod2002021Key.C0035))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0029, mod200old.getDoubleValue(Mod2002021Key.C0029))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0033, mod200old.getDoubleValue(Mod2002021Key.C0033))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0034, mod200old.getDoubleValue(Mod2002021Key.C0034))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0038, mod200old.getDoubleValue(Mod2002021Key.C0038))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0046, mod200old.getDoubleValue(Mod2002021Key.C0046))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0012, mod200old.getDoubleValue(Mod2002021Key.C0012))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0012R, mod200old.getDoubleValue(Mod2002021Key.C0012R))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0064, mod200old.getDoubleValue(Mod2002021Key.C0064))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0057, mod200old.getDoubleValue(Mod2002021Key.C0057))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0062, mod200old.getDoubleValue(Mod2002021Key.C0062))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0020, mod200old.getDoubleValue(Mod2002021Key.C0020))  

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0007, mod200old.getDoubleValue(Mod2002021Key.C0007))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0009, mod200old.getDoubleValue(Mod2002021Key.C0009))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0010, mod200old.getDoubleValue(Mod2002021Key.C0010))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0081, mod200old.getDoubleValue(Mod2002021Key.C0081))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0082, mod200old.getDoubleValue(Mod2002021Key.C0082))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0016, mod200old.getDoubleValue(Mod2002021Key.C0016))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0026, mod200old.getDoubleValue(Mod2002021Key.C0026))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0027, mod200old.getDoubleValue(Mod2002021Key.C0027))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0030, mod200old.getDoubleValue(Mod2002021Key.C0030))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0039, mod200old.getDoubleValue(Mod2002021Key.C0039))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0043, mod200old.getDoubleValue(Mod2002021Key.C0043))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0045, mod200old.getDoubleValue(Mod2002021Key.C0045))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0063, mod200old.getDoubleValue(Mod2002021Key.C0063))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0071, mod200old.getDoubleValue(Mod2002021Key.C0071))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0070, mod200old.getDoubleValue(Mod2002021Key.C0070))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0059, mod200old.getDoubleValue(Mod2002021Key.C0059))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0065, mod200old.getDoubleValue(Mod2002021Key.C0065))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0072, mod200old.getDoubleValue(Mod2002021Key.C0072))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0073, mod200old.getDoubleValue(Mod2002021Key.C0073))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0044, mod200old.getDoubleValue(Mod2002021Key.C0044))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0074, mod200old.getDoubleValue(Mod2002021Key.C0074))
			
			// IMPORTE NETO DE LA CIFRA DE NEGOCIOS
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002022Key.VOLOPE, mod200old.getDoubleValue(Mod2002021Key.VOLOPE))
			
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
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0050, mod200old.getDoubleValue(Mod2002021Key.C0050))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0051, mod200old.getDoubleValue(Mod2002021Key.C0051))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0052, mod200old.getDoubleValue(Mod2002021Key.C0052))

			,(mod200old,mod200new) -> mod200new.setEcpnType( mod200old.getEcpnType().ordinal() )        // ECPN
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0075, mod200old.getDoubleValue(Mod2002021Key.C0075))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0076, mod200old.getDoubleValue(Mod2002021Key.C0076))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0077, mod200old.getDoubleValue(Mod2002021Key.C0077))

			,(mod200old,mod200new) -> mod200new.setPygType(mod200old.getPygType().ordinal() )           // Pérdidas y ganancias 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0053, mod200old.getDoubleValue(Mod2002021Key.C0053))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0054, mod200old.getDoubleValue(Mod2002021Key.C0054))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.C0055, mod200old.getDoubleValue(Mod2002021Key.C0055))
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002022Key.C0061, mod200old.getDoubleValue(Mod2002021Key.C0061))  // Estados de cuentas de Instituciones de inversión colectiva [061]
			
			// PERSONAL ASALARIADO
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002022Key.C0041, mod200old.getDoubleValue(Mod2002021Key.C0041))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002022Key.C0042, mod200old.getDoubleValue(Mod2002021Key.C0042))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042]
			
		})

		,PAG2  ( new IPropertyFiller[] {
				
			 (mod200old,mod200new) -> mod200new.getAdministrators().addAll(mod200old.getAdministrators())       // A. Relación de administradores			 
			,(mod200old,mod200new) -> mod200new.getParticipationsOut().addAll(mod200old.getParticipationsOut()) // B1. Participaciones de la declarante en otras entidades
			,(mod200old,mod200new) -> mod200new.getParticipationsIn().addAll(mod200old.getParticipationsIn())   // B2. Participaciones de personas o entidades en la declarante
            
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002022Key.POR51, mod200old.getDoubleValue(Mod2002021Key.POR51))  // B2. Suma de porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002022Key.PORES, mod200old.getDoubleValue(Mod2002021Key.PORES))  // B2. Suma de porcentajes de participaciones en situaciones especiales
			
		})
		
		,PAG2B  ( new IPropertyFiller[] {
				
		     (mod200old,mod200new) -> mod200new.getMinorEntities().addAll(mod200old.getMinorEntities())   // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
		    ,(mod200old,mod200new) -> mod200new.getUteForeign().addAll(mod200old.getUteForeign())         // D. Información de detalle de EP o UTE que operen en el extranjero y por participación en fórmula de colaboración análoga a UTE
		    
			// SECRETARIO
            
            ,(mod200old,mod200new) -> mod200new.getSecretary().setName(mod200old.getSecretary().getName())              // Nombre o Razón social - Secretario del Consejo de Administración 
			,(mod200old,mod200new) -> mod200new.getSecretary().setDocument(mod200old.getSecretary().getDocument())      // N.I.F. - Secretario del Consejo de Administración
			,(mod200old,mod200new) -> mod200new.getSecretary().setIrnr(addOneYear(mod200old.getSecretary().getIrnr()))  // Fecha - Contribuyentes por el I.R.N.R.
			
			// REPRESENTANTES LEGALES
						
			,(mod200old,mod200new) -> mod200new.getRepresentatives().addAll(mod200old.getRepresentatives())  // Declaración representantes legales entidad
		    
		})		
		
		,PAG14 ( new IPropertyFiller[] {
				
			// Se pone por defecto el tipo de gravamen del año pasado, cuando se calcule el modelo, ya se modificará si es necesario
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ558, mod200old.getDoubleValue(Mod2002021Key.LQ558))  // Tipo de Gravamen
			
		})

		,PAG15 ( new IPropertyFiller[] {
				
		    // Compensación bases imponibles negativas
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ640 , mod200old.getDoubleValue(Mod2002021Key.LQ548 )) // 1997
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ643 , mod200old.getDoubleValue(Mod2002021Key.LQ645 )) // 1998
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ646 , mod200old.getDoubleValue(Mod2002021Key.LQ648 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ649 , mod200old.getDoubleValue(Mod2002021Key.LQ651 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ652 , mod200old.getDoubleValue(Mod2002021Key.LQ654 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ655 , mod200old.getDoubleValue(Mod2002021Key.LQ657 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ658 , mod200old.getDoubleValue(Mod2002021Key.LQ660 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ661 , mod200old.getDoubleValue(Mod2002021Key.LQ663 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ664 , mod200old.getDoubleValue(Mod2002021Key.LQ666 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ667 , mod200old.getDoubleValue(Mod2002021Key.LQ669 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ743 , mod200old.getDoubleValue(Mod2002021Key.LQ748 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ275 , mod200old.getDoubleValue(Mod2002021Key.LQ277 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ608 , mod200old.getDoubleValue(Mod2002021Key.LQ610 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ704 , mod200old.getDoubleValue(Mod2002021Key.LQ706 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ013 , mod200old.getDoubleValue(Mod2002021Key.LQ015 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ725 , mod200old.getDoubleValue(Mod2002021Key.LQ727 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ534 , mod200old.getDoubleValue(Mod2002021Key.LQ536 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ607 , mod200old.getDoubleValue(Mod2002021Key.LQ699 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1045, mod200old.getDoubleValue(Mod2002021Key.LQ1047)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1519, mod200old.getDoubleValue(Mod2002021Key.LQ1521)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1592, mod200old.getDoubleValue(Mod2002021Key.LQ1594)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1825, mod200old.getDoubleValue(Mod2002021Key.LQ1827)) // 2018			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ2193, mod200old.getDoubleValue(Mod2002021Key.LQ2195)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ194 , mod200old.getDoubleValue(Mod2002021Key.LQ196 )) // 2020			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ151,  mod200old.getDoubleValue(Mod2002021Key.LQ2318)+ // 2021
					                                                                   mod200old.getDoubleValue(Mod2002021Key.LQ1049)+
					                                                                  (mod200old.getDoubleValue(Mod2002021Key.C0017)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002021Key.C0018)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002021Key.C0019)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002021Key.LQ552)<0
                                                                                      && mod200old.getDoubleValue(Mod2002021Key.LQ1049)==0
                                                                                       ?Math.abs(mod200old.getDoubleValue(Mod2002021Key.LQ552)) : 0) )  
																						// Si la base imponible (casilla 552) del 2021 era negativa, tambien se suma a esta casilla (si no es cooperativa) y si no estaba indicada en la 1049
			                                                                    
		})
		
		,PAG15B ( new IPropertyFiller[] {
				
				// Deducciones por doble imposición interna RDL 4/2004
				 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN104, adjustDoubleTax( // 2008
						mod200old.getDoubleValue(Mod2002021Key.BN848), mod200old.getDoubleValue(Mod2002021Key.BN105), mod200old.getDoubleValue(Mod2002021Key.BN103A) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN106, adjustDoubleTax( // 2009
						mod200old.getDoubleValue(Mod2002021Key.BN284), mod200old.getDoubleValue(Mod2002021Key.BN107), mod200old.getDoubleValue(Mod2002021Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN108, adjustDoubleTax( // 2010
						mod200old.getDoubleValue(Mod2002021Key.BN707), mod200old.getDoubleValue(Mod2002021Key.BN109), mod200old.getDoubleValue(Mod2002021Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN110, adjustDoubleTax( // 2011
						mod200old.getDoubleValue(Mod2002021Key.BN300), mod200old.getDoubleValue(Mod2002021Key.BN111), mod200old.getDoubleValue(Mod2002021Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN112, adjustDoubleTax( // 2012
						mod200old.getDoubleValue(Mod2002021Key.BN027), mod200old.getDoubleValue(Mod2002021Key.BN113), mod200old.getDoubleValue(Mod2002021Key.BN103A) ) )  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN114, adjustDoubleTax( // 2013
						mod200old.getDoubleValue(Mod2002021Key.BN716), mod200old.getDoubleValue(Mod2002021Key.BN115), mod200old.getDoubleValue(Mod2002021Key.BN103A) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN735, adjustDoubleTax( // 2014
						mod200old.getDoubleValue(Mod2002021Key.BN738), mod200old.getDoubleValue(Mod2002021Key.BN920), mod200old.getDoubleValue(Mod2002021Key.BN103A) ) )

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN105 , mod200old.getDoubleValue(Mod2002021Key.BN105)) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN107 , mod200old.getDoubleValue(Mod2002021Key.BN107)) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN109 , mod200old.getDoubleValue(Mod2002021Key.BN109)) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN111 , mod200old.getDoubleValue(Mod2002021Key.BN111)) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN113 , mod200old.getDoubleValue(Mod2002021Key.BN113)) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN115 , mod200old.getDoubleValue(Mod2002021Key.BN115)) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN920 , mod200old.getDoubleValue(Mod2002021Key.BN920)) // 2014

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN846 , mod200old.getDoubleValue(Mod2002021Key.BN848)) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN282 , mod200old.getDoubleValue(Mod2002021Key.BN284)) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN702 , mod200old.getDoubleValue(Mod2002021Key.BN707)) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN071 , mod200old.getDoubleValue(Mod2002021Key.BN300)) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN025 , mod200old.getDoubleValue(Mod2002021Key.BN027)) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN714 , mod200old.getDoubleValue(Mod2002021Key.BN716)) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN736 , mod200old.getDoubleValue(Mod2002021Key.BN738)) // 2014
				                                                                          
				// Deducciones por doble imposición interna (DT 23ª.1 LIS)
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN101, adjustDoubleTax( // 2015
						mod200old.getDoubleValue(Mod2002021Key.BN121), mod200old.getDoubleValue(Mod2002021Key.BN102), mod200old.getDoubleValue(Mod2002021Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN122, adjustDoubleTax( // 2016
						mod200old.getDoubleValue(Mod2002021Key.BN126), mod200old.getDoubleValue(Mod2002021Key.BN123), mod200old.getDoubleValue(Mod2002021Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1595, adjustDoubleTax( // 2017
						mod200old.getDoubleValue(Mod2002021Key.BN1599), mod200old.getDoubleValue(Mod2002021Key.BN1596), mod200old.getDoubleValue(Mod2002021Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1828, adjustDoubleTax( // 2018
						mod200old.getDoubleValue(Mod2002021Key.BN1832), mod200old.getDoubleValue(Mod2002021Key.BN1829), mod200old.getDoubleValue(Mod2002021Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2196, adjustDoubleTax( // 2019
						mod200old.getDoubleValue(Mod2002021Key.BN2200), mod200old.getDoubleValue(Mod2002021Key.BN2197), mod200old.getDoubleValue(Mod2002021Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2319, adjustDoubleTax( // 2020
						mod200old.getDoubleValue(Mod2002021Key.BN2323), mod200old.getDoubleValue(Mod2002021Key.BN2320), mod200old.getDoubleValue(Mod2002021Key.BN103B) ) )
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN199 , adjustDoubleTax( // 2021
						mod200old.getDoubleValue(Mod2002021Key.BN206)+mod200old.getDoubleValue(Mod2002021Key.BN129), mod200old.getDoubleValue(Mod2002021Key.BN203), mod200old.getDoubleValue(Mod2002021Key.BN103B) ) )

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN102 , mod200old.getDoubleValue(Mod2002021Key.BN102) ) // 2015
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN123 , mod200old.getDoubleValue(Mod2002021Key.BN123) ) // 2016
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1596, mod200old.getDoubleValue(Mod2002021Key.BN1596)) // 2017
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1829, mod200old.getDoubleValue(Mod2002021Key.BN1829)) // 2018
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2197, mod200old.getDoubleValue(Mod2002021Key.BN2197)) // 2019
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2320, mod200old.getDoubleValue(Mod2002021Key.BN2320)) // 2020
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN203 , mod200old.getDoubleValue(Mod2002021Key.BN203) ) // 2021

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN119 , mod200old.getDoubleValue(Mod2002021Key.BN121) ) // 2015
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN124 , mod200old.getDoubleValue(Mod2002021Key.BN126) ) // 2016
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1597, mod200old.getDoubleValue(Mod2002021Key.BN1599)) // 2017
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1830, mod200old.getDoubleValue(Mod2002021Key.BN1832)) // 2018
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2198, mod200old.getDoubleValue(Mod2002021Key.BN2200)) // 2019
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2321, mod200old.getDoubleValue(Mod2002021Key.BN2323)) // 2020
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN204 , mod200old.getDoubleValue(Mod2002021Key.BN206)+mod200old.getDoubleValue(Mod2002021Key.BN129)) // 2021
				
				// Deducciones por doble imposición internacional RDL 4/2004
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN153, adjustDoubleTax(  // 2005
						mod200old.getDoubleValue(Mod2002021Key.BN639), mod200old.getDoubleValue(Mod2002021Key.BN728), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN154, adjustDoubleTax( // 2006
						mod200old.getDoubleValue(Mod2002021Key.BN197), mod200old.getDoubleValue(Mod2002021Key.BN729), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN155, adjustDoubleTax( // 2007
						mod200old.getDoubleValue(Mod2002021Key.BN287), mod200old.getDoubleValue(Mod2002021Key.BN730), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN156, adjustDoubleTax( // 2008
						mod200old.getDoubleValue(Mod2002021Key.BN827), mod200old.getDoubleValue(Mod2002021Key.BN731), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN157, adjustDoubleTax( // 2009
						mod200old.getDoubleValue(Mod2002021Key.BN003), mod200old.getDoubleValue(Mod2002021Key.BN732), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN158, adjustDoubleTax( // 2010
						mod200old.getDoubleValue(Mod2002021Key.BN030), mod200old.getDoubleValue(Mod2002021Key.BN733), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))  
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN159, adjustDoubleTax( // 2011
						mod200old.getDoubleValue(Mod2002021Key.BN719), mod200old.getDoubleValue(Mod2002021Key.BN734), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN720, adjustDoubleTax( // 2012
						mod200old.getDoubleValue(Mod2002021Key.BN724), mod200old.getDoubleValue(Mod2002021Key.BN721), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN739, adjustDoubleTax( // 2013
						mod200old.getDoubleValue(Mod2002021Key.BN742), mod200old.getDoubleValue(Mod2002021Key.BN921), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN134, adjustDoubleTax( // 2014
						mod200old.getDoubleValue(Mod2002021Key.BN137), mod200old.getDoubleValue(Mod2002021Key.BN926), mod200old.getDoubleValue(Mod2002021Key.BN103C) ))
				
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN728 , mod200old.getDoubleValue(Mod2002021Key.BN728 )) // 2005 - Tipo de gravamen
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN729 , mod200old.getDoubleValue(Mod2002021Key.BN729 )) // 2006
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN730 , mod200old.getDoubleValue(Mod2002021Key.BN730 )) // 2007
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN731 , mod200old.getDoubleValue(Mod2002021Key.BN731 )) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN732 , mod200old.getDoubleValue(Mod2002021Key.BN732 )) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN733 , mod200old.getDoubleValue(Mod2002021Key.BN733 )) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN734 , mod200old.getDoubleValue(Mod2002021Key.BN734 )) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN721 , mod200old.getDoubleValue(Mod2002021Key.BN721 )) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN921 , mod200old.getDoubleValue(Mod2002021Key.BN921 )) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN926 , mod200old.getDoubleValue(Mod2002021Key.BN926 )) // 2014

				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN637 , mod200old.getDoubleValue(Mod2002021Key.BN639 )) // 2005 - Deducción pendiente
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN849 , mod200old.getDoubleValue(Mod2002021Key.BN197 )) // 2006
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN285 , mod200old.getDoubleValue(Mod2002021Key.BN287 )) // 2007
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN825 , mod200old.getDoubleValue(Mod2002021Key.BN827 )) // 2008
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN001 , mod200old.getDoubleValue(Mod2002021Key.BN003 )) // 2009
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN028 , mod200old.getDoubleValue(Mod2002021Key.BN030 )) // 2010
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN717 , mod200old.getDoubleValue(Mod2002021Key.BN719 )) // 2011
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN722 , mod200old.getDoubleValue(Mod2002021Key.BN724 )) // 2012
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN740 , mod200old.getDoubleValue(Mod2002021Key.BN742 )) // 2013
				,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN135 , mod200old.getDoubleValue(Mod2002021Key.BN137 )) // 2014
				
		})
		
		,PAG16 ( new IPropertyFiller[] {
			
			// Deducciones por doble imposición internacional LIS
 			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1054, adjustDoubleTax(mod200old.getDoubleValue(Mod2002021Key.BN1053), mod200old.getDoubleValue(Mod2002021Key.BN1050), mod200old.getDoubleValue(Mod2002021Key.BN103D) )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1348, adjustDoubleTax(mod200old.getDoubleValue(Mod2002021Key.BN1352), mod200old.getDoubleValue(Mod2002021Key.BN1349), mod200old.getDoubleValue(Mod2002021Key.BN103D) )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1770, adjustDoubleTax(mod200old.getDoubleValue(Mod2002021Key.BN1774), mod200old.getDoubleValue(Mod2002021Key.BN1771), mod200old.getDoubleValue(Mod2002021Key.BN103D) )) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1833, adjustDoubleTax(mod200old.getDoubleValue(Mod2002021Key.BN1837), mod200old.getDoubleValue(Mod2002021Key.BN1834), mod200old.getDoubleValue(Mod2002021Key.BN103D) )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2201, adjustDoubleTax(mod200old.getDoubleValue(Mod2002021Key.BN2205), mod200old.getDoubleValue(Mod2002021Key.BN2202), mod200old.getDoubleValue(Mod2002021Key.BN103D) )) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2324, adjustDoubleTax(mod200old.getDoubleValue(Mod2002021Key.BN2328), mod200old.getDoubleValue(Mod2002021Key.BN2325), mod200old.getDoubleValue(Mod2002021Key.BN103D) )) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN207 , adjustDoubleTax(mod200old.getDoubleValue(Mod2002021Key.BN213)+mod200old.getDoubleValue(Mod2002021Key.BN174), mod200old.getDoubleValue(Mod2002021Key.BN208), mod200old.getDoubleValue(Mod2002021Key.BN103D) )) // 2021
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1050, mod200old.getDoubleValue(Mod2002021Key.BN1050)) // 2015 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1349, mod200old.getDoubleValue(Mod2002021Key.BN1349)) // 2016 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1771, mod200old.getDoubleValue(Mod2002021Key.BN1771)) // 2017 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1834, mod200old.getDoubleValue(Mod2002021Key.BN1834)) // 2018 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2202, mod200old.getDoubleValue(Mod2002021Key.BN2202)) // 2019 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2325, mod200old.getDoubleValue(Mod2002021Key.BN2325)) // 2020 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN208 , mod200old.getDoubleValue(Mod2002021Key.BN208) ) // 2021 - Tipo de gravamen
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1051, mod200old.getDoubleValue(Mod2002021Key.BN1053)) // 2015 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1350, mod200old.getDoubleValue(Mod2002021Key.BN1352)) // 2016 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1772, mod200old.getDoubleValue(Mod2002021Key.BN1774)) // 2017 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1835, mod200old.getDoubleValue(Mod2002021Key.BN1837)) // 2018 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2203, mod200old.getDoubleValue(Mod2002021Key.BN2205)) // 2098 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2326, mod200old.getDoubleValue(Mod2002021Key.BN2328)) // 2020 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN209 , mod200old.getDoubleValue(Mod2002021Key.BN213)+mod200old.getDoubleValue(Mod2002021Key.BN174)) // 2021 - Deducción pendiente
										
			// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN004 , mod200old.getDoubleValue(Mod2002021Key.BN006 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN031 , mod200old.getDoubleValue(Mod2002021Key.BN033 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN022 , mod200old.getDoubleValue(Mod2002021Key.BN024 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN040 , mod200old.getDoubleValue(Mod2002021Key.BN042 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN138 , mod200old.getDoubleValue(Mod2002021Key.BN140 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN141 , mod200old.getDoubleValue(Mod2002021Key.BN143 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN188 , mod200old.getDoubleValue(Mod2002021Key.BN190 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN803 , mod200old.getDoubleValue(Mod2002021Key.BN805 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1055, mod200old.getDoubleValue(Mod2002021Key.BN1057)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN700 , mod200old.getDoubleValue(Mod2002021Key.BN709 )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1353, mod200old.getDoubleValue(Mod2002021Key.BN1355)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1775, mod200old.getDoubleValue(Mod2002021Key.BN1777)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1838, mod200old.getDoubleValue(Mod2002021Key.BN1840)) // 2019			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2206, mod200old.getDoubleValue(Mod2002021Key.BN2208)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2329, mod200old.getDoubleValue(Mod2002021Key.BN2331)+mod200old.getDoubleValue(Mod2002021Key.BN253)) // 2021
			
			// Deducciones disposición transitoria 24ª.1 LIS		
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN749 , mod200old.getDoubleValue(Mod2002021Key.BN754)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN752 , mod200old.getDoubleValue(Mod2002021Key.BN757)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN755 , mod200old.getDoubleValue(Mod2002021Key.BN760)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN758 , mod200old.getDoubleValue(Mod2002021Key.BN763)) // 2020			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN761 , mod200old.getDoubleValue(Mod2002021Key.BN746)+mod200old.getDoubleValue(Mod2002021Key.BN784)) // 2021
			
		})
        
		,PAG16B ( new IPropertyFiller[] {
			
			// Deducciones inversión en Canarias - Activos fijos (Ley 20/1991)
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN854 , mod200old.getDoubleValue(Mod2002021Key.BN1356)) // 2010 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN857 , mod200old.getDoubleValue(Mod2002021Key.BN859 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN860 , mod200old.getDoubleValue(Mod2002021Key.BN862 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN863 , mod200old.getDoubleValue(Mod2002021Key.BN865 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN883 , mod200old.getDoubleValue(Mod2002021Key.BN885 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN785 , mod200old.getDoubleValue(Mod2002021Key.BN790 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1357, mod200old.getDoubleValue(Mod2002021Key.BN1359)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1778, mod200old.getDoubleValue(Mod2002021Key.BN1780)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN852 , mod200old.getDoubleValue(Mod2002021Key.BN856 )) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2116, mod200old.getDoubleValue(Mod2002021Key.BN2118)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2209, mod200old.getDoubleValue(Mod2002021Key.BN2211)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2332, mod200old.getDoubleValue(Mod2002021Key.BN2334)+mod200old.getDoubleValue(Mod2002021Key.BN239)) // 2021
			
			// Deducciones inversión en Canarias - Activos fijos en La Palma, La Gomera y El Hierro
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2335, mod200old.getDoubleValue(Mod2002021Key.BN2337)) // 2018 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2338, mod200old.getDoubleValue(Mod2002021Key.BN2340)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2341, mod200old.getDoubleValue(Mod2002021Key.BN2343)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2344, mod200old.getDoubleValue(Mod2002021Key.BN2346)+mod200old.getDoubleValue(Mod2002021Key.BN2497)) // 2021
			
			// Deducciones inversión en Canarias - Inversiones en Canarias (Ley 20/1991)			 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN880 , mod200old.getDoubleValue(Mod2002021Key.BN882 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN866 , mod200old.getDoubleValue(Mod2002021Key.BN870 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN939 , mod200old.getDoubleValue(Mod2002021Key.BN941 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN191 , mod200old.getDoubleValue(Mod2002021Key.BN193 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN613 , mod200old.getDoubleValue(Mod2002021Key.BN701 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN200 , mod200old.getDoubleValue(Mod2002021Key.BN011 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN037 , mod200old.getDoubleValue(Mod2002021Key.BN039 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN044 , mod200old.getDoubleValue(Mod2002021Key.BN046 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN528 , mod200old.getDoubleValue(Mod2002021Key.BN530 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN144 , mod200old.getDoubleValue(Mod2002021Key.BN146 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN147 , mod200old.getDoubleValue(Mod2002021Key.BN149 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN240 , mod200old.getDoubleValue(Mod2002021Key.BN242 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1058, mod200old.getDoubleValue(Mod2002021Key.BN1060)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN791 , mod200old.getDoubleValue(Mod2002021Key.BN806 )) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1781, mod200old.getDoubleValue(Mod2002021Key.BN1783)) // 2018            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2122, mod200old.getDoubleValue(Mod2002021Key.BN2124)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2212, mod200old.getDoubleValue(Mod2002021Key.BN2214)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2347, mod200old.getDoubleValue(Mod2002021Key.BN2349)+mod200old.getDoubleValue(Mod2002021Key.BN219)) // 2021
            
            // Deducciones inversión en Canarias - Inversiones en La Palma, La Gomera y El Hierro
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2119, mod200old.getDoubleValue(Mod2002021Key.BN2121)) // 2018 
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2125, mod200old.getDoubleValue(Mod2002021Key.BN2127)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2215, mod200old.getDoubleValue(Mod2002021Key.BN2217)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2350, mod200old.getDoubleValue(Mod2002021Key.BN2352)+mod200old.getDoubleValue(Mod2002021Key.BN222)) // 2021
            
		})
			
		,PAG17_18 ( new IPropertyFiller[] {
				
		    // Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)			
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN288 , mod200old.getDoubleValue(Mod2002021Key.BN290 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN466 , mod200old.getDoubleValue(Mod2002021Key.BN468 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN061 , mod200old.getDoubleValue(Mod2002021Key.BN586 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN472 , mod200old.getDoubleValue(Mod2002021Key.BN478 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN180 , mod200old.getDoubleValue(Mod2002021Key.BN182 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN531 , mod200old.getDoubleValue(Mod2002021Key.BN533 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN945 , mod200old.getDoubleValue(Mod2002021Key.BN947 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN960 , mod200old.getDoubleValue(Mod2002021Key.BN962 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN183 , mod200old.getDoubleValue(Mod2002021Key.BN186 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN966 , mod200old.getDoubleValue(Mod2002021Key.BN968 )) // 2013 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN457 , mod200old.getDoubleValue(Mod2002021Key.BN459 )) // 2013 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN460 , mod200old.getDoubleValue(Mod2002021Key.BN462 )) // 2013 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1063, mod200old.getDoubleValue(Mod2002021Key.BN1065)) // 2014 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1066, mod200old.getDoubleValue(Mod2002021Key.BN1068)) // 2014 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1069, mod200old.getDoubleValue(Mod2002021Key.BN1071)) // 2014 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2294, mod200old.getDoubleValue(Mod2002021Key.BN815 )) // 2015 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN986 , mod200old.getDoubleValue(Mod2002021Key.BN507 )) // 2015 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN557 , mod200old.getDoubleValue(Mod2002021Key.BN594 )) // 2015 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2297, mod200old.getDoubleValue(Mod2002021Key.BN1616)) // 2016 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1617, mod200old.getDoubleValue(Mod2002021Key.BN1619)) // 2016 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1620, mod200old.getDoubleValue(Mod2002021Key.BN1622)) // 2016 IT			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2300, mod200old.getDoubleValue(Mod2002021Key.BN1849)) // 2017 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1850, mod200old.getDoubleValue(Mod2002021Key.BN1852)) // 2017 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1853, mod200old.getDoubleValue(Mod2002021Key.BN1855)) // 2017 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2091, mod200old.getDoubleValue(Mod2002021Key.BN2220)) // 2018 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2221, mod200old.getDoubleValue(Mod2002021Key.BN2223)) // 2018 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2224, mod200old.getDoubleValue(Mod2002021Key.BN2226)) // 2018 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2094, mod200old.getDoubleValue(Mod2002021Key.BN2355)) // 2019 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2356, mod200old.getDoubleValue(Mod2002021Key.BN2358)) // 2019 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2359, mod200old.getDoubleValue(Mod2002021Key.BN2361)) // 2019 IT			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2097, mod200old.getDoubleValue(Mod2002021Key.BN227) ) // 2020 excepto I+D+i y TAP
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN228 , mod200old.getDoubleValue(Mod2002021Key.BN230) ) // 2020 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN234 , mod200old.getDoubleValue(Mod2002021Key.BN236) ) // 2020 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2145, mod200old.getDoubleValue(Mod2002021Key.BN1362)+ // 2021 excepto I+D+i y TAP
					                                                                   mod200old.getDoubleValue(Mod2002021Key.BN830)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN809)+																					   
																					   mod200old.getDoubleValue(Mod2002021Key.BN1077)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN797)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN889)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1371)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN2192)+																					   
																					   mod200old.getDoubleValue(Mod2002021Key.BN1628)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1640)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1709)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1802)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1876)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1891)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1894)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1903)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1909)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1912)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1915)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1948)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1873)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1897)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1900)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1906)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1918)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1930)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN1936)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN2364)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN2367)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN2370)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN2373)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN2376)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN2379)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN258)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN261)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN264)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN267)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN270)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN274)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN293)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN296)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN299)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN319)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN350)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN354)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN369)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN405)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN419)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN424)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN429)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN432)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN435)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN438)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN441)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN454)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN463)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN470)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN481)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN503)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN513)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN537)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN595)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN811)+
																					   mod200old.getDoubleValue(Mod2002021Key.BN817))				
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN780,  mod200old.getDoubleValue(Mod2002021Key.BN1365)+  // 2021 CT
					                                                                   mod200old.getDoubleValue(Mod2002021Key.BN800 )) 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN786,  mod200old.getDoubleValue(Mod2002021Key.BN1368)+  // 2021 IT
					                                                                   mod200old.getDoubleValue(Mod2002021Key.BN713 ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2449, mod200old.getDoubleValue(Mod2002021Key.BN1685))  // 2022
			
		})
			
		,PAG18T ( new IPropertyFiller[] {
				
			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002 - Donaciones de carácter general

  			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN904 , mod200old.getDoubleValue(Mod2002021Key.BN906) ) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN990 , mod200old.getDoubleValue(Mod2002021Key.BN992) ) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN997 , mod200old.getDoubleValue(Mod2002021Key.BN999) ) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN246 , mod200old.getDoubleValue(Mod2002021Key.BN248) ) // 2015 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN818 , mod200old.getDoubleValue(Mod2002021Key.BN820) ) // 2015 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN993 , mod200old.getDoubleValue(Mod2002021Key.BN995) ) // 2016 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN821 , mod200old.getDoubleValue(Mod2002021Key.BN834) ) // 2016 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1434, mod200old.getDoubleValue(Mod2002021Key.BN1436)) // 2017 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN835 , mod200old.getDoubleValue(Mod2002021Key.BN837) ) // 2017 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1718, mod200old.getDoubleValue(Mod2002021Key.BN1720)) // 2018 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN838 , mod200old.getDoubleValue(Mod2002021Key.BN840) ) // 2018 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1950, mod200old.getDoubleValue(Mod2002021Key.BN1952)) // 2019 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN842 , mod200old.getDoubleValue(Mod2002021Key.BN845) ) // 2019 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2227, mod200old.getDoubleValue(Mod2002021Key.BN2229)) // 2020 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN868 , mod200old.getDoubleValue(Mod2002021Key.BN871) ) // 2020 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2380, mod200old.getDoubleValue(Mod2002021Key.BN2382)+mod200old.getDoubleValue(Mod2002021Key.BN890)) // 2021 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN872 , mod200old.getDoubleValue(Mod2002021Key.BN2498)+mod200old.getDoubleValue(Mod2002021Key.BN893)) // 2021 Con			

			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002 - Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN899 , mod200old.getDoubleValue(Mod2002021Key.BN902) ) // 2012    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN903 , mod200old.getDoubleValue(Mod2002021Key.BN929) ) // 2013    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN930 , mod200old.getDoubleValue(Mod2002021Key.BN932) ) // 2014    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN933 , mod200old.getDoubleValue(Mod2002021Key.BN942) ) // 2015 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN943 , mod200old.getDoubleValue(Mod2002021Key.BN948) ) // 2015 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN949 , mod200old.getDoubleValue(Mod2002021Key.BN951) ) // 2016 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN952 , mod200old.getDoubleValue(Mod2002021Key.BN954) ) // 2016 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2472, mod200old.getDoubleValue(Mod2002021Key.BN2474)) // 2017 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN958 , mod200old.getDoubleValue(Mod2002021Key.BN963) ) // 2017 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN964 , mod200old.getDoubleValue(Mod2002021Key.BN969) ) // 2018 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN970 , mod200old.getDoubleValue(Mod2002021Key.BN972) ) // 2018 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN973 , mod200old.getDoubleValue(Mod2002021Key.BN979) ) // 2019 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN980 , mod200old.getDoubleValue(Mod2002021Key.BN982) ) // 2019 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN983 , mod200old.getDoubleValue(Mod2002021Key.BN985) ) // 2020 Sin
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1000, mod200old.getDoubleValue(Mod2002021Key.BN1007)) // 2020 Con
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1008, mod200old.getDoubleValue(Mod2002021Key.BN1024)+mod200old.getDoubleValue(Mod2002021Key.BN1072)) // 2021 Sin    
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1025, mod200old.getDoubleValue(Mod2002021Key.BN1036)+mod200old.getDoubleValue(Mod2002021Key.BN1078)) // 2021 Con	   
			
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
			
		    // Dado que el importe pendiente está en funcion del importe resultante de aplicar a la base de deduccion un porcentaje, 
			// para obtener ahora esa base de deducción, según el importe pendiente del año anterior, habra que hacer la operacion inversa
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1166, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1169)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1438, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1441)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1442, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1445)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1721, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1724)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1953, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1956)/0.05)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2230, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN2233)/0.05)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2383, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN2386)/0.05)+
                    																   AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1085)/0.05)) // 2021
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)                                                                          
			// Ejercicio 2015 = 2%  [01179] = [01178] x 0,02
			// Ejercicio 2016 = 5%  [01448] = [01447] x 0,05
			// Ejercicio 2017 = 5%  [01452] = [01451] x 0,05
			// Ejercicio 2018 = 5%  [01726] = [01725] x 0,05
			// Ejercicio 2019 = 5%  [01958] = [01957] x 0,05
  			// Ejercicio 2020 = 5%  [02235] = [02234] x 0,05
			// Ejercicio 2021 = 5%  [02388] = [02387] x 0,05
  			                        
  		    // idem apartado anterior
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1178, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1181)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1447, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1450)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1451, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1454)/0.05)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1725, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1728)/0.05)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN1957, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1960)/0.05)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2234, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN2237)/0.05)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.BN2387, AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN2390)/0.05)+
       			   																	   AonMathUtils.round(mod200old.getDoubleValue(Mod2002021Key.BN1089)/0.05)) // 2021

		})

		,PAG20 ( new IPropertyFiller[] {
				
			// Limitacion en la deducilidad de gastos financieros. Gastos financieros pendientes de deducir
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1188, mod200old.getDoubleValue(Mod2002021Key.LM1191)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1193, mod200old.getDoubleValue(Mod2002021Key.LM1196)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1198, mod200old.getDoubleValue(Mod2002021Key.LM1201)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1202, mod200old.getDoubleValue(Mod2002021Key.LM1205)) // 2015 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1203, mod200old.getDoubleValue(Mod2002021Key.LM1206)) // 2015 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1462, mod200old.getDoubleValue(Mod2002021Key.LM1210)) // 2016 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1463, mod200old.getDoubleValue(Mod2002021Key.LM1211)) // 2016 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1736, mod200old.getDoubleValue(Mod2002021Key.LM1465)) // 2017 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1737, mod200old.getDoubleValue(Mod2002021Key.LM1466)) // 2017 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1977, mod200old.getDoubleValue(Mod2002021Key.LM1739)) // 2018 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1978, mod200old.getDoubleValue(Mod2002021Key.LM1740)) // 2018 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2253, mod200old.getDoubleValue(Mod2002021Key.LM1980)) // 2019 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2254, mod200old.getDoubleValue(Mod2002021Key.LM1981)) // 2019 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2399, mod200old.getDoubleValue(Mod2002021Key.LM2256)) // 2020 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2400, mod200old.getDoubleValue(Mod2002021Key.LM2257)) // 2020 Resto			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1098, mod200old.getDoubleValue(Mod2002021Key.LM2402)+mod200old.getDoubleValue(Mod2002021Key.LM1101)) // 2021 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1099, mod200old.getDoubleValue(Mod2002021Key.LM2403)+mod200old.getDoubleValue(Mod2002021Key.LM1102)) // 2021 Resto
			
			// Pendiente de adición por límite beneficio operativo no aplicado			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1467, mod200old.getDoubleValue(Mod2002021Key.LM1469)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1741, mod200old.getDoubleValue(Mod2002021Key.LM1743)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1982, mod200old.getDoubleValue(Mod2002021Key.LM1984)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2258, mod200old.getDoubleValue(Mod2002021Key.LM2260)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2404, mod200old.getDoubleValue(Mod2002021Key.LM2406)+mod200old.getDoubleValue(Mod2002021Key.LM1105)) // 2021

		})
			
		,PAG20B ( new IPropertyFiller[] {
			
			// Reserva de capitalización
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1985, mod200old.getDoubleValue(Mod2002021Key.LQ1987)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ2407, mod200old.getDoubleValue(Mod2002021Key.LQ2409)+mod200old.getDoubleValue(Mod2002021Key.LQ1108)) // 2021
			
			// Reserva de nivelación - Reducción en base imponible 	
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1455, mod200old.getDoubleValue(Mod2002021Key.LQ1457)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1961, mod200old.getDoubleValue(Mod2002021Key.LQ1963)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ2238, mod200old.getDoubleValue(Mod2002021Key.LQ2240)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ2410, mod200old.getDoubleValue(Mod2002021Key.LQ2412)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1109, mod200old.getDoubleValue(Mod2002021Key.LQ1111)+mod200old.getDoubleValue(Mod2002021Key.LQ1731))  // 2021
			
		})
		
		,PAG20T ( new IPropertyFiller[] {
				
			// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS  
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1524, mod200old.getDoubleValue(Mod2002021Key.LM1528)) // 2007 y anteriores
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1529, mod200old.getDoubleValue(Mod2002021Key.LM1534)) // 2008 a 2015
				
			// Activos por impuesto diferido (AID). Art. 130 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1542, mod200old.getDoubleValue(Mod2002021Key.LM1549)+mod200old.getDoubleValue(Mod2002021Key.LM1550)+mod200old.getDoubleValue(Mod2002021Key.LM1551)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1552, mod200old.getDoubleValue(Mod2002021Key.LM1558)+mod200old.getDoubleValue(Mod2002021Key.LM1559)+mod200old.getDoubleValue(Mod2002021Key.LM1560)) // 2017
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1754, mod200old.getDoubleValue(Mod2002021Key.LM1760)+mod200old.getDoubleValue(Mod2002021Key.LM1761)+mod200old.getDoubleValue(Mod2002021Key.LM1762)) // 2018
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2100, mod200old.getDoubleValue(Mod2002021Key.LM2106)+mod200old.getDoubleValue(Mod2002021Key.LM2107)+mod200old.getDoubleValue(Mod2002021Key.LM2108)) // 2019
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2268, mod200old.getDoubleValue(Mod2002021Key.LM2274)+mod200old.getDoubleValue(Mod2002021Key.LM2275)+mod200old.getDoubleValue(Mod2002021Key.LM2276)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2418, mod200old.getDoubleValue(Mod2002021Key.LM2424)+mod200old.getDoubleValue(Mod2002021Key.LM2425)+mod200old.getDoubleValue(Mod2002021Key.LM2426)+
                    																   mod200old.getDoubleValue(Mod2002021Key.LM1131)+mod200old.getDoubleValue(Mod2002021Key.LM1132)+mod200old.getDoubleValue(Mod2002021Key.LM1133)) // 2021
			
			// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2277, mod200old.getDoubleValue(Mod2002021Key.LM2280)) // 2020
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2427, mod200old.getDoubleValue(Mod2002021Key.LM2430)+mod200old.getDoubleValue(Mod2002021Key.LM1138)) // 2021
			
		})
		
		,PAG20Q ( new IPropertyFiller[] {

            // Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
            // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible            
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1473, mod200old.getDoubleValue(Mod2002021Key.LM1476)) // 2007 y anteriores: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1408, mod200old.getDoubleValue(Mod2002021Key.LM1409)) // 2007 y anteriores: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1477, mod200old.getDoubleValue(Mod2002021Key.LM1483)) // 2008 a 2015: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1478, mod200old.getDoubleValue(Mod2002021Key.LM1484)) // 2008 a 2015: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1485, mod200old.getDoubleValue(Mod2002021Key.LM1489)) // 2016: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1486, mod200old.getDoubleValue(Mod2002021Key.LM1490)) // 2016: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1491, mod200old.getDoubleValue(Mod2002021Key.LM1493)) // 2017: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1747, mod200old.getDoubleValue(Mod2002021Key.LM1749)) // 2017: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1750, mod200old.getDoubleValue(Mod2002021Key.LM1752)) // 2018: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1988, mod200old.getDoubleValue(Mod2002021Key.LM1990)) // 2018: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1991, mod200old.getDoubleValue(Mod2002021Key.LM1993)) // 2019: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2261, mod200old.getDoubleValue(Mod2002021Key.LM2263)) // 2019: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2264, mod200old.getDoubleValue(Mod2002021Key.LM2266)) // 2020: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2431, mod200old.getDoubleValue(Mod2002021Key.LM2433)) // 2020: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM2434, mod200old.getDoubleValue(Mod2002021Key.LM2436)+mod200old.getDoubleValue(Mod2002021Key.LM1164)) // 2021: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LM1143, mod200old.getDoubleValue(Mod2002021Key.LM1192)) // 2021: Que han cumplido ...
			
		})
				
		,PAG21 ( new IPropertyFiller[] {
				  
			     (mod200old,mod200new) -> mod200new.getGroupEntities().addAll(mod200old.getGroupEntities())    // NIF de las entidades del grupo
			    ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.CNEST , mod200old.getDoubleValue(Mod2002021Key.CNEST)) // Número de establecimientos permanentes a través de los que opera, en caso de persona física titular
			    ,(mod200old,mod200new) -> mod200new.getEstablishments().addAll(mod200old.getEstablishments())  // NIF de los establecimientos permanentes		    
			    
		})
		
        ,PAG22 ( new IPropertyFiller[] {
        		
    		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC        	  			
             (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC097 , mod200old.getDoubleValue(Mod2002021Key.RC048) ) // 2017				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC524 , mod200old.getDoubleValue(Mod2002021Key.RC527) ) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC922 , mod200old.getDoubleValue(Mod2002021Key.RC925) ) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC1165, mod200old.getDoubleValue(Mod2002021Key.RC996) ) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC1744, mod200old.getDoubleValue(Mod2002021Key.RC1175)) // 2021
            
            // Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC2442, mod200old.getDoubleValue(Mod2002021Key.RC2443)) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC2444, mod200old.getDoubleValue(Mod2002021Key.RC2445)) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC2446, mod200old.getDoubleValue(Mod2002021Key.RC2447)) // 2019
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC1176, mod200old.getDoubleValue(Mod2002021Key.RC2451)) // 2020
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.RC1823, mod200old.getDoubleValue(Mod2002021Key.RC1184)) // 2021

        	// Régimen de cooperativas - Detalle de compensación de cuotas	                                                                				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ673 , mod200old.getDoubleValue(Mod2002021Key.LQ1224)) // 2000 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ676 , mod200old.getDoubleValue(Mod2002021Key.LQ678 )) // 2001				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ679 , mod200old.getDoubleValue(Mod2002021Key.LQ681 )) // 2002				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ682 , mod200old.getDoubleValue(Mod2002021Key.LQ684 )) // 2003				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ685 , mod200old.getDoubleValue(Mod2002021Key.LQ687 )) // 2004				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ688 , mod200old.getDoubleValue(Mod2002021Key.LQ690 )) // 2005				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ691 , mod200old.getDoubleValue(Mod2002021Key.LQ693 )) // 2006				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ623 , mod200old.getDoubleValue(Mod2002021Key.LQ672 )) // 2007				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ279 , mod200old.getDoubleValue(Mod2002021Key.LQ281 )) // 2008				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ587 , mod200old.getDoubleValue(Mod2002021Key.LQ900 )) // 2009				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ059 , mod200old.getDoubleValue(Mod2002021Key.LQ100 )) // 2010				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ017 , mod200old.getDoubleValue(Mod2002021Key.LQ019 )) // 2011				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ772 , mod200old.getDoubleValue(Mod2002021Key.LQ777 )) // 2012				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ907 , mod200old.getDoubleValue(Mod2002021Key.LQ909 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ910 , mod200old.getDoubleValue(Mod2002021Key.LQ912 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ935 , mod200old.getDoubleValue(Mod2002021Key.LQ937 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1511, mod200old.getDoubleValue(Mod2002021Key.LQ1513)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1767, mod200old.getDoubleValue(Mod2002021Key.LQ1769)) // 2017
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ2113, mod200old.getDoubleValue(Mod2002021Key.LQ2115)) // 2018
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ2281, mod200old.getDoubleValue(Mod2002021Key.LQ2283)) // 2019            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ2452, mod200old.getDoubleValue(Mod2002021Key.LQ2454)) // 2020            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022Key.LQ1186, mod200old.getDoubleValue(Mod2002021Key.LQ1190)+ // 2021 (Tambien se añade la casilla 560 del ejercicio anterior si es negativa y es cooperativa y no está puesto ese importe en la 1226)
            		                                                                   mod200old.getDoubleValue(Mod2002021Key.LQ1226)+
            		                                                                  ((mod200old.getDoubleValue(Mod2002021Key.C0017)==1 || mod200old.getDoubleValue(Mod2002021Key.C0018)==1 || mod200old.getDoubleValue(Mod2002021Key.C0019)==1) && (mod200old.getDoubleValue(Mod2002021Key.LQ560)<0) && (mod200old.getDoubleValue(Mod2002021Key.LQ1226)==0) ? Math.abs(mod200old.getDoubleValue(Mod2002021Key.LQ560)) : 0) )  
			
		})
        
		,PAG24  ( new IPropertyFiller[] {
				
				// UTE - Relación de socios existentes a la fecha de cierre del período impositivo...
			    (mod200old,mod200new) -> mod200new.getUteParticipations().addAll(mod200old.getUteParticipations()) 
			    
		})
        
        ,PAG26B ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias	
	      	  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2504, mod200old.getDoubleValue(Mod2002021KeyDC.DC2505))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2509, mod200old.getDoubleValue(Mod2002021KeyDC.DC2510))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2514, mod200old.getDoubleValue(Mod2002021KeyDC.DC2515))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2519, mod200old.getDoubleValue(Mod2002021KeyDC.DC2520))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2524, mod200old.getDoubleValue(Mod2002021KeyDC.DC2525))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2529, mod200old.getDoubleValue(Mod2002021KeyDC.DC2530))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2534, mod200old.getDoubleValue(Mod2002021KeyDC.DC2535))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2539, mod200old.getDoubleValue(Mod2002021KeyDC.DC2540))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2544, mod200old.getDoubleValue(Mod2002021KeyDC.DC2545))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2549, mod200old.getDoubleValue(Mod2002021KeyDC.DC2550))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2554, mod200old.getDoubleValue(Mod2002021KeyDC.DC2555))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2559, mod200old.getDoubleValue(Mod2002021KeyDC.DC2560))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2564, mod200old.getDoubleValue(Mod2002021KeyDC.DC2565))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2569, mod200old.getDoubleValue(Mod2002021KeyDC.DC2570))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2579, mod200old.getDoubleValue(Mod2002021KeyDC.DC2580))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2584, mod200old.getDoubleValue(Mod2002021KeyDC.DC2585))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2589, mod200old.getDoubleValue(Mod2002021KeyDC.DC2590))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2594, mod200old.getDoubleValue(Mod2002021KeyDC.DC2595))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2599, mod200old.getDoubleValue(Mod2002021KeyDC.DC2600))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2604, mod200old.getDoubleValue(Mod2002021KeyDC.DC2605))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2609, mod200old.getDoubleValue(Mod2002021KeyDC.DC2610))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2614, mod200old.getDoubleValue(Mod2002021KeyDC.DC2615))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2619, mod200old.getDoubleValue(Mod2002021KeyDC.DC2620))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2624, mod200old.getDoubleValue(Mod2002021KeyDC.DC2625))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2629, mod200old.getDoubleValue(Mod2002021KeyDC.DC2630))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2634, mod200old.getDoubleValue(Mod2002021KeyDC.DC2635))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2639, mod200old.getDoubleValue(Mod2002021KeyDC.DC2640))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2644, mod200old.getDoubleValue(Mod2002021KeyDC.DC2645))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2649, mod200old.getDoubleValue(Mod2002021KeyDC.DC2650))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2654, mod200old.getDoubleValue(Mod2002021KeyDC.DC2655))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2659, mod200old.getDoubleValue(Mod2002021KeyDC.DC2660))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2664, mod200old.getDoubleValue(Mod2002021KeyDC.DC2665))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2669, mod200old.getDoubleValue(Mod2002021KeyDC.DC2670))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2674, mod200old.getDoubleValue(Mod2002021KeyDC.DC2675))
	      	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2679, mod200old.getDoubleValue(Mod2002021KeyDC.DC2680))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2684, mod200old.getDoubleValue(Mod2002021KeyDC.DC2685))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2689, mod200old.getDoubleValue(Mod2002021KeyDC.DC2690))
    		  
        })
        
        ,PAG26T ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)	
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2714, mod200old.getDoubleValue(Mod2002021KeyDC.DC2715))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2719, mod200old.getDoubleValue(Mod2002021KeyDC.DC2720))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2724, mod200old.getDoubleValue(Mod2002021KeyDC.DC2725))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2729, mod200old.getDoubleValue(Mod2002021KeyDC.DC2730))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2734, mod200old.getDoubleValue(Mod2002021KeyDC.DC2735))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2739, mod200old.getDoubleValue(Mod2002021KeyDC.DC2740))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2744, mod200old.getDoubleValue(Mod2002021KeyDC.DC2745))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2749, mod200old.getDoubleValue(Mod2002021KeyDC.DC2750))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2814, mod200old.getDoubleValue(Mod2002021KeyDC.DC2815))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2819, mod200old.getDoubleValue(Mod2002021KeyDC.DC2820))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2574, mod200old.getDoubleValue(Mod2002021KeyDC.DC2575))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2754, mod200old.getDoubleValue(Mod2002021KeyDC.DC2755))	
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2854, mod200old.getDoubleValue(Mod2002021KeyDC.DC2855))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2859, mod200old.getDoubleValue(Mod2002021KeyDC.DC2860))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2864, mod200old.getDoubleValue(Mod2002021KeyDC.DC2865))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2869, mod200old.getDoubleValue(Mod2002021KeyDC.DC2870))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2874, mod200old.getDoubleValue(Mod2002021KeyDC.DC2875))
  		    , (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2879, mod200old.getDoubleValue(Mod2002021KeyDC.DC2880))
    		
        })
        
        ,PAG26QUA ( new IPropertyFiller[] {
        		
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)        		
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2884, mod200old.getDoubleValue(Mod2002021KeyDC.DC2885))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2889, mod200old.getDoubleValue(Mod2002021KeyDC.DC2890))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2894, mod200old.getDoubleValue(Mod2002021KeyDC.DC2895))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2899, mod200old.getDoubleValue(Mod2002021KeyDC.DC2900))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2904, mod200old.getDoubleValue(Mod2002021KeyDC.DC2905))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2909, mod200old.getDoubleValue(Mod2002021KeyDC.DC2910))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2924, mod200old.getDoubleValue(Mod2002021KeyDC.DC2925))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2929, mod200old.getDoubleValue(Mod2002021KeyDC.DC2930))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2934, mod200old.getDoubleValue(Mod2002021KeyDC.DC2935))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2939, mod200old.getDoubleValue(Mod2002021KeyDC.DC2940))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC1676, mod200old.getDoubleValue(Mod2002021KeyDC.DC1680))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC1681, mod200old.getDoubleValue(Mod2002021KeyDC.DC1688))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2954, mod200old.getDoubleValue(Mod2002021KeyDC.DC2955))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2959, mod200old.getDoubleValue(Mod2002021KeyDC.DC2960))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2984, mod200old.getDoubleValue(Mod2002021KeyDC.DC2985))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2989, mod200old.getDoubleValue(Mod2002021KeyDC.DC2990))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2994, mod200old.getDoubleValue(Mod2002021KeyDC.DC2995))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC2999, mod200old.getDoubleValue(Mod2002021KeyDC.DC3000))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3004, mod200old.getDoubleValue(Mod2002021KeyDC.DC3005))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3009, mod200old.getDoubleValue(Mod2002021KeyDC.DC3010))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3014, mod200old.getDoubleValue(Mod2002021KeyDC.DC3015))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3019, mod200old.getDoubleValue(Mod2002021KeyDC.DC3020))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3024, mod200old.getDoubleValue(Mod2002021KeyDC.DC3025))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3029, mod200old.getDoubleValue(Mod2002021KeyDC.DC3030))
  		  	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3034, mod200old.getDoubleValue(Mod2002021KeyDC.DC3035))
  		  	, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3039, mod200old.getDoubleValue(Mod2002021KeyDC.DC3040))
    		
        })
        
        ,PAG26QUI ( new IPropertyFiller[] {
        	
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3044, mod200old.getDoubleValue(Mod2002021KeyDC.DC3045))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3049, mod200old.getDoubleValue(Mod2002021KeyDC.DC3050))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3054, mod200old.getDoubleValue(Mod2002021KeyDC.DC3055))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3059, mod200old.getDoubleValue(Mod2002021KeyDC.DC3060))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3074, mod200old.getDoubleValue(Mod2002021KeyDC.DC3075))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3079, mod200old.getDoubleValue(Mod2002021KeyDC.DC3080))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3084, mod200old.getDoubleValue(Mod2002021KeyDC.DC3085))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3089, mod200old.getDoubleValue(Mod2002021KeyDC.DC3090))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3094, mod200old.getDoubleValue(Mod2002021KeyDC.DC3095))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3099, mod200old.getDoubleValue(Mod2002021KeyDC.DC3100))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3104, mod200old.getDoubleValue(Mod2002021KeyDC.DC3105))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3109, mod200old.getDoubleValue(Mod2002021KeyDC.DC3110))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3114, mod200old.getDoubleValue(Mod2002021KeyDC.DC3115))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3119, mod200old.getDoubleValue(Mod2002021KeyDC.DC3120))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3124, mod200old.getDoubleValue(Mod2002021KeyDC.DC3125))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3129, mod200old.getDoubleValue(Mod2002021KeyDC.DC3130))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3134, mod200old.getDoubleValue(Mod2002021KeyDC.DC3135))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3139, mod200old.getDoubleValue(Mod2002021KeyDC.DC3140))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3144, mod200old.getDoubleValue(Mod2002021KeyDC.DC3145))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3149, mod200old.getDoubleValue(Mod2002021KeyDC.DC3150))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3154, mod200old.getDoubleValue(Mod2002021KeyDC.DC3155))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3159, mod200old.getDoubleValue(Mod2002021KeyDC.DC3160))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3164, mod200old.getDoubleValue(Mod2002021KeyDC.DC3165))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3169, mod200old.getDoubleValue(Mod2002021KeyDC.DC3170))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3174, mod200old.getDoubleValue(Mod2002021KeyDC.DC3175))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3179, mod200old.getDoubleValue(Mod2002021KeyDC.DC3180))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3184, mod200old.getDoubleValue(Mod2002021KeyDC.DC3185))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3189, mod200old.getDoubleValue(Mod2002021KeyDC.DC3190))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3194, mod200old.getDoubleValue(Mod2002021KeyDC.DC3195))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3199, mod200old.getDoubleValue(Mod2002021KeyDC.DC3200))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3204, mod200old.getDoubleValue(Mod2002021KeyDC.DC3205))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3209, mod200old.getDoubleValue(Mod2002021KeyDC.DC3210))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3214, mod200old.getDoubleValue(Mod2002021KeyDC.DC3215))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3219, mod200old.getDoubleValue(Mod2002021KeyDC.DC3220))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3224, mod200old.getDoubleValue(Mod2002021KeyDC.DC3225))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3229, mod200old.getDoubleValue(Mod2002021KeyDC.DC3230))
        		
        })
        
        ,PAG26S ( new IPropertyFiller[] {
        	
        	// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias (cont.)
    		  (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3234, mod200old.getDoubleValue(Mod2002021KeyDC.DC3235))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3239, mod200old.getDoubleValue(Mod2002021KeyDC.DC3240))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3254, mod200old.getDoubleValue(Mod2002021KeyDC.DC3255))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3259, mod200old.getDoubleValue(Mod2002021KeyDC.DC3260))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3264, mod200old.getDoubleValue(Mod2002021KeyDC.DC3265))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3269, mod200old.getDoubleValue(Mod2002021KeyDC.DC3270))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3274, mod200old.getDoubleValue(Mod2002021KeyDC.DC3275))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3279, mod200old.getDoubleValue(Mod2002021KeyDC.DC3280))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3294, mod200old.getDoubleValue(Mod2002021KeyDC.DC3295))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3299, mod200old.getDoubleValue(Mod2002021KeyDC.DC3300))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3304, mod200old.getDoubleValue(Mod2002021KeyDC.DC3305))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3309, mod200old.getDoubleValue(Mod2002021KeyDC.DC3310))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3324, mod200old.getDoubleValue(Mod2002021KeyDC.DC3325))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3329, mod200old.getDoubleValue(Mod2002021KeyDC.DC3330))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3334, mod200old.getDoubleValue(Mod2002021KeyDC.DC3335))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3339, mod200old.getDoubleValue(Mod2002021KeyDC.DC3340))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3344, mod200old.getDoubleValue(Mod2002021KeyDC.DC3345))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3349, mod200old.getDoubleValue(Mod2002021KeyDC.DC3350))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3364, mod200old.getDoubleValue(Mod2002021KeyDC.DC3365))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3369, mod200old.getDoubleValue(Mod2002021KeyDC.DC3370))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3374, mod200old.getDoubleValue(Mod2002021KeyDC.DC3375))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3379, mod200old.getDoubleValue(Mod2002021KeyDC.DC3380))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3384, mod200old.getDoubleValue(Mod2002021KeyDC.DC3385))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3389, mod200old.getDoubleValue(Mod2002021KeyDC.DC3390))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3394, mod200old.getDoubleValue(Mod2002021KeyDC.DC3395))
    		, (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002022KeyDC.DC3399, mod200old.getDoubleValue(Mod2002021KeyDC.DC3400))
        		
        })
		;
		
		private IPropertyFiller[] propertyFillers;
		
		private Pages2022 (IPropertyFiller[] propertyFillers) {
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Mod2002021 mod200old, Mod2002022 mod200new) {
			for ( Pages2022 page : Pages2022.values()) {
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(mod200old, mod200new);
					}				
			}
		}
	}
	
	public static void import2021(Mod2002022 mod200new, Mod2002021 mod200old) {
		try {
			Pages2022.fill(mod200old, mod200new);
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
	}

//	public static Mod2002022 import2021(Mod2002021 mod200old) {
//		
//		try {
//			// Creamos el objeto Mod200 del ejercicio actual, donde importaremos los datos del ejercicio anterior
//			Mod2002022 mod200new = new Mod2002022();
//			import2021(mod200new, mod200old);
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
	
	private static void toString(Mod2002022 mod200) {
		
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
		for (Mod2002022Key key : Mod2002022Key.values()) {			
			if (mod200.getKey(key) != null) {
				System.out.println(AonStringUtils.join(AonStringUtils.SPACE
						,mod200.getKey(key).getKey()
						," ...: "
						,mod200.getKey(key).getValue()));
			}
		}
		for (Mod2002022KeyDC key : Mod2002022KeyDC.values()) {			
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
	
	private static void setDoubleValue2021(Mod2002021 mod200, IMod200Key key, double value) {
		 
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
//			Mod2002021 mod200old = new Mod2002021();
//
//			// PRUEBA - Inicializamos todas las claves con sus numeros
//
//			mod200old.setBalanceType(2); // PYMES
//			mod200old.setPygType(2); // PYMES
//			mod200old.setEcpnType(3); // No consta
//
//			for (Mod2002021Key key : Mod2002021Key.values()) {
//				try {
//					setDoubleValue2021(mod200old, key, Double.parseDouble(key.name().substring(2)));
//				} catch (NumberFormatException e) {
//					// nothing
//				}
//			}
//			for (Mod2002021KeyDC key : Mod2002021KeyDC.values()) {
//				try {
//					setDoubleValue2021(mod200old, key, Double.parseDouble(key.name().substring(2)));
//				} catch (NumberFormatException e) {
//					//// nothing
//				}
//			}
//
//			// Prueba base imponible negativa (casilla 552)
//			setDoubleValue2021(mod200old, Mod2002021Key.LQ552, -552);
//
//			// Cooperativas Casillas 17, 18 y 19. Cuota compensacion negativa (casilla 560)
//			setDoubleValue2021(mod200old, Mod2002021Key.C0017, 0);
//			setDoubleValue2021(mod200old, Mod2002021Key.C0018, 0);
//			setDoubleValue2021(mod200old, Mod2002021Key.C0019, 0);
//			setDoubleValue2021(mod200old, Mod2002021Key.LQ560, 0);
//
//			// FIN PRUEBA
//
//			Mod2002022 mod200new = import2021(mod200old);
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


