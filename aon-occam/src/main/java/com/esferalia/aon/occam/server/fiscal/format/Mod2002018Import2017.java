package com.esferalia.aon.occam.server.fiscal.format;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.DoubleVariable2017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.DoubleVariable2018;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018.EcpnType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002018Import2017 {

	@FunctionalInterface
	private static interface IPropertyFiller {
		public void propertyFill(Mod2002017 mod200old, Mod2002018 mod200new);
	}
	
	// ***************************************
	// **** METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************
	
	// Añadir casilla si no existe o asignar el valor a la casilla, al Modelo 200
	private static void setDoubleValue(Mod2002018 mod200, Mod2002018Key key, double value) {
		 
		DoubleVariable2018 t = mod200.getVariable(key);
		if (t == null) {
			t = new DoubleVariable2018(key);
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
	private static double adjustDoubleTax(double value, double type, double type2018 ) {
		
		if (type2018 != 0 && type != 0 && type != type2018) {
			return AonMathUtils.round( value * type / type2018 );
	    }
		else return value;
		
	}

	// **** FIN METODOS ESTATICOS DE UTILIDAD ****
	
	// Paginas del modelo 
	private enum Pages2018 {
		
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
			,(mod200old,mod200new) -> mod200new.setYear( 2018 )                                           // Ejercicio
			,(mod200old,mod200new) -> mod200new.setAdministration( mod200old.getAdministration() )		  // Administracion                                           
			
			// CARACTERES DE LA DECLARACION 
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0001, mod200old.getDoubleValue(Mod2002017Key.C0001))                       
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0002, mod200old.getDoubleValue(Mod2002017Key.C0002))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0003, mod200old.getDoubleValue(Mod2002017Key.C0003))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0004, mod200old.getDoubleValue(Mod2002017Key.C0004))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0005, mod200old.getDoubleValue(Mod2002017Key.C0005))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0011, mod200old.getDoubleValue(Mod2002017Key.C0011))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0013, mod200old.getDoubleValue(Mod2002017Key.C0013))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0014, mod200old.getDoubleValue(Mod2002017Key.C0014))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0017, mod200old.getDoubleValue(Mod2002017Key.C0017))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0018, mod200old.getDoubleValue(Mod2002017Key.C0018))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0019, mod200old.getDoubleValue(Mod2002017Key.C0019))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0021, mod200old.getDoubleValue(Mod2002017Key.C0021))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0023, mod200old.getDoubleValue(Mod2002017Key.C0023))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0024, mod200old.getDoubleValue(Mod2002017Key.C0024))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0025, mod200old.getDoubleValue(Mod2002017Key.C0025))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0031, mod200old.getDoubleValue(Mod2002017Key.C0031))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0032, mod200old.getDoubleValue(Mod2002017Key.C0032))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0036, mod200old.getDoubleValue(Mod2002017Key.C0036))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0048, mod200old.getDoubleValue(Mod2002017Key.C0048))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0058, mod200old.getDoubleValue(Mod2002017Key.C0058))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0060, mod200old.getDoubleValue(Mod2002017Key.C0060))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0066, mod200old.getDoubleValue(Mod2002017Key.C0066))  
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0006, mod200old.getDoubleValue(Mod2002017Key.C0006))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0015, mod200old.getDoubleValue(Mod2002017Key.C0015))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0022, mod200old.getDoubleValue(Mod2002017Key.C0022))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0028, mod200old.getDoubleValue(Mod2002017Key.C0028))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0047, mod200old.getDoubleValue(Mod2002017Key.C0047))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0049, mod200old.getDoubleValue(Mod2002017Key.C0049))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0035, mod200old.getDoubleValue(Mod2002017Key.C0035))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0029, mod200old.getDoubleValue(Mod2002017Key.C0029))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0033, mod200old.getDoubleValue(Mod2002017Key.C0033))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0034, mod200old.getDoubleValue(Mod2002017Key.C0034))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0038, mod200old.getDoubleValue(Mod2002017Key.C0038))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0046, mod200old.getDoubleValue(Mod2002017Key.C0046))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0012, mod200old.getDoubleValue(Mod2002017Key.C0012))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0064, mod200old.getDoubleValue(Mod2002017Key.C0064))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0057, mod200old.getDoubleValue(Mod2002017Key.C0057))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0062, mod200old.getDoubleValue(Mod2002017Key.C0062))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0020, mod200old.getDoubleValue(Mod2002017Key.C0020))  

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0007, mod200old.getDoubleValue(Mod2002017Key.C0007))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0009, mod200old.getDoubleValue(Mod2002017Key.C0009))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0010, mod200old.getDoubleValue(Mod2002017Key.C0010))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0016, mod200old.getDoubleValue(Mod2002017Key.C0016))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0026, mod200old.getDoubleValue(Mod2002017Key.C0026))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0027, mod200old.getDoubleValue(Mod2002017Key.C0027))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0030, mod200old.getDoubleValue(Mod2002017Key.C0030))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0039, mod200old.getDoubleValue(Mod2002017Key.C0039))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0043, mod200old.getDoubleValue(Mod2002017Key.C0043))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0045, mod200old.getDoubleValue(Mod2002017Key.C0045))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0063, mod200old.getDoubleValue(Mod2002017Key.C0063))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0071, mod200old.getDoubleValue(Mod2002017Key.C0071))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0059, mod200old.getDoubleValue(Mod2002017Key.C0059))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0065, mod200old.getDoubleValue(Mod2002017Key.C0065))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0067, mod200old.getDoubleValue(Mod2002017Key.C0067))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.C0072, mod200old.getDoubleValue(Mod2002017Key.C0072))  
			
			// ESTADOS DE CUENTAS 
			
			,(mod200old,mod200new) -> mod200new.setBalanceType( mod200old.getBalanceType().ordinal() )  // Balance. No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
			
			// ECPN campo nuevo para el 2018, se inicializa con el del balance del 2017, si era normal, en caso contrario se pone como que "No consta"
			,(mod200old,mod200new) -> mod200new.setEcpnType( (mod200old.getBalanceType() == BalanceType.NORMAL ? EcpnType.NORMAL : EcpnType.NO_CONSTA) )  //
			
			,(mod200old,mod200new) -> mod200new.setPygType(mod200old.getPygType().ordinal() )           // Pérdidas y ganancias 0.No consta 1.Mod.normal 2.Mod.abreviado 3. Mod.PYMES
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002018Key.C0061, mod200old.getDoubleValue(Mod2002017Key.C0061))  // Estados de cuentas de Instituciones de inversión colectiva [061]
			
			// GRUPOS FISCALES 
			
			,(mod200old,mod200new) -> mod200new.setFiscalGroup( mod200old.getFiscalGroup() )              // Nº de grupo fiscal al que pertenecen las entidades  que hayan marcado las claves 009 ó 010  [040]
			,(mod200old,mod200new) -> mod200new.setDominantDocument( mod200old.getDominantDocument() )    // NIF de la entidad representante/dominante (incluida en el grupo fiscal)
			,(mod200old,mod200new) -> mod200new.setDominantIdentificationNumber( mod200old.getDominantIdentificationNumber() ) // Nº identificación de la entidad dominante (en el caso de grupos constituidos sólo por entidades dependientes)
			
			// PERSONAL ASALARIADO
			
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002018Key.C0041, mod200old.getDoubleValue(Mod2002017Key.C0041))  // Personal asalariado (cifra media del ejercicio) Personal fijo [041] 
			,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002018Key.C0042, mod200old.getDoubleValue(Mod2002017Key.C0042))  // Personal asalariado (cifra media del ejercicio) Personal no fijo [042]
			
			// SECRETARIO
			                  
            ,(mod200old,mod200new) -> mod200new.getSecretary().setName(mod200old.getSecretary().getName())              // Nombre o Razón social - Secretario del Consejo de Administración 
			,(mod200old,mod200new) -> mod200new.getSecretary().setDocument(mod200old.getSecretary().getDocument())      // N.I.F. - Secretario del Consejo de Administración
			,(mod200old,mod200new) -> mod200new.getSecretary().setIrnr( addOneYear(mod200old.getSecretary().getIrnr())) // Fecha - Contribuyentes por el I.R.N.R.
			
			// REPRESENTANTES LEGALES
						
			,(mod200old,mod200new) -> mod200new.getRepresentatives().addAll(mod200old.getRepresentatives())  // Declaración representantes legales entidad
		})

		,PAG2  ( new IPropertyFiller[] {
			 (mod200old,mod200new) -> mod200new.getAdministrators().addAll(mod200old.getAdministrators())       // A. Relación de administradores
			,(mod200old,mod200new) -> mod200new.getParticipationsOut().addAll(mod200old.getParticipationsOut()) // B. Participaciones directas - B.1. Participaciones declarante en otras entidades
			,(mod200old,mod200new) -> mod200new.getParticipationsIn().addAll(mod200old.getParticipationsIn())   // B. Participaciones directas - B.2. Participaciones de personas o entidades en la declarante
			                                      
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002018Key.POR51, mod200old.getDoubleValue(Mod2002017Key.POR51))  // B .Participaciones directas - B.2. Suma de  porcentajes de participación de personas o entidades en el capital de la  declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado 
		    ,(mod200old,mod200new) -> setDoubleValue(mod200new, Mod2002018Key.PORES, mod200old.getDoubleValue(Mod2002017Key.PORES))  // B. Participaciones directas - B.2. Suma de porcentajes de participaciones en situaciones especiales
		})

		,PAG14 ( new IPropertyFiller[] {
			// Se pone por defecto el tipo de gravamen del año pasado
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ558, mod200old.getDoubleValue(Mod2002017Key.LQ558))  // Tipo de Gravamen				
		})

		,PAG15 ( new IPropertyFiller[] {
				
		    // Compensación bases imponibles negativas
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ640 , mod200old.getDoubleValue(Mod2002017Key.LQ548 )) // 1997
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ643 , mod200old.getDoubleValue(Mod2002017Key.LQ645 )) // 1998
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ646 , mod200old.getDoubleValue(Mod2002017Key.LQ648 )) // 1999
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ649 , mod200old.getDoubleValue(Mod2002017Key.LQ651 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ652 , mod200old.getDoubleValue(Mod2002017Key.LQ654 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ655 , mod200old.getDoubleValue(Mod2002017Key.LQ657 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ658 , mod200old.getDoubleValue(Mod2002017Key.LQ660 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ661 , mod200old.getDoubleValue(Mod2002017Key.LQ663 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ664 , mod200old.getDoubleValue(Mod2002017Key.LQ666 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ667 , mod200old.getDoubleValue(Mod2002017Key.LQ669 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ743 , mod200old.getDoubleValue(Mod2002017Key.LQ748 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ275 , mod200old.getDoubleValue(Mod2002017Key.LQ277 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ608 , mod200old.getDoubleValue(Mod2002017Key.LQ610 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ704 , mod200old.getDoubleValue(Mod2002017Key.LQ706 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ013 , mod200old.getDoubleValue(Mod2002017Key.LQ015 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ725 , mod200old.getDoubleValue(Mod2002017Key.LQ727 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ534 , mod200old.getDoubleValue(Mod2002017Key.LQ536 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ607 , mod200old.getDoubleValue(Mod2002017Key.LQ699 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1045, mod200old.getDoubleValue(Mod2002017Key.LQ1047)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1519, mod200old.getDoubleValue(Mod2002017Key.LQ1521)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1592, mod200old.getDoubleValue(Mod2002017Key.LQ1594)+ // 2017
					                                                                   mod200old.getDoubleValue(Mod2002017Key.LQ1049)+
					                                                                  (mod200old.getDoubleValue(Mod2002017Key.C0017)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002017Key.C0018)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002017Key.C0019)==0 
                                                                                      && mod200old.getDoubleValue(Mod2002017Key.LQ552)<0 
                                                                                       ?Math.abs(mod200old.getDoubleValue(Mod2002017Key.LQ552)) : 0) )  
																						// Si la base imponible (casilla 552) del 2017 era negativa, tambien se suma a esta casilla (si no es cooperativa)

			// Deducciones por doble imposición interna RDL 4/2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN104, adjustDoubleTax( // 2008
					mod200old.getDoubleValue(Mod2002017Key.BN848), mod200old.getDoubleValue(Mod2002017Key.BN105), mod200old.getDoubleValue(Mod2002017Key.BN103A) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN106, adjustDoubleTax( // 2009
					mod200old.getDoubleValue(Mod2002017Key.BN284), mod200old.getDoubleValue(Mod2002017Key.BN107), mod200old.getDoubleValue(Mod2002017Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN108, adjustDoubleTax( // 2010
					mod200old.getDoubleValue(Mod2002017Key.BN707), mod200old.getDoubleValue(Mod2002017Key.BN109), mod200old.getDoubleValue(Mod2002017Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN110, adjustDoubleTax( // 2011
					mod200old.getDoubleValue(Mod2002017Key.BN300), mod200old.getDoubleValue(Mod2002017Key.BN111), mod200old.getDoubleValue(Mod2002017Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN112, adjustDoubleTax( // 2012
					mod200old.getDoubleValue(Mod2002017Key.BN027), mod200old.getDoubleValue(Mod2002017Key.BN113), mod200old.getDoubleValue(Mod2002017Key.BN103A) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN114, adjustDoubleTax( // 2013
					mod200old.getDoubleValue(Mod2002017Key.BN716), mod200old.getDoubleValue(Mod2002017Key.BN115), mod200old.getDoubleValue(Mod2002017Key.BN103A) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN735, adjustDoubleTax( // 2014
					mod200old.getDoubleValue(Mod2002017Key.BN738), mod200old.getDoubleValue(Mod2002017Key.BN920), mod200old.getDoubleValue(Mod2002017Key.BN103A) ) )

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN105 , mod200old.getDoubleValue(Mod2002017Key.BN105)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN107 , mod200old.getDoubleValue(Mod2002017Key.BN107)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN109 , mod200old.getDoubleValue(Mod2002017Key.BN109)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN111 , mod200old.getDoubleValue(Mod2002017Key.BN111)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN113 , mod200old.getDoubleValue(Mod2002017Key.BN113)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN115 , mod200old.getDoubleValue(Mod2002017Key.BN115)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN920 , mod200old.getDoubleValue(Mod2002017Key.BN920)) // 2014

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN846 , mod200old.getDoubleValue(Mod2002017Key.BN848)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN282 , mod200old.getDoubleValue(Mod2002017Key.BN284)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN702 , mod200old.getDoubleValue(Mod2002017Key.BN707)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN071 , mod200old.getDoubleValue(Mod2002017Key.BN300)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN025 , mod200old.getDoubleValue(Mod2002017Key.BN027)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN714 , mod200old.getDoubleValue(Mod2002017Key.BN716)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN736 , mod200old.getDoubleValue(Mod2002017Key.BN738)) // 2014
			                                                                          
			// Deducciones por doble imposición interna (DT 23ª.1 LIS)
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN101, adjustDoubleTax( // 2015
					mod200old.getDoubleValue(Mod2002017Key.BN121), mod200old.getDoubleValue(Mod2002017Key.BN102), mod200old.getDoubleValue(Mod2002017Key.BN103B) ) )
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN122, adjustDoubleTax( // 2016
					mod200old.getDoubleValue(Mod2002017Key.BN126), mod200old.getDoubleValue(Mod2002017Key.BN123), mod200old.getDoubleValue(Mod2002017Key.BN103B) ) )  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1595, adjustDoubleTax( // 2017
					mod200old.getDoubleValue(Mod2002017Key.BN1599)+mod200old.getDoubleValue(Mod2002017Key.BN129), mod200old.getDoubleValue(Mod2002017Key.BN1596), mod200old.getDoubleValue(Mod2002017Key.BN103B) ) )  

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN102 , mod200old.getDoubleValue(Mod2002017Key.BN102)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN123 , mod200old.getDoubleValue(Mod2002017Key.BN123)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1596 , mod200old.getDoubleValue(Mod2002017Key.BN1596)) // 2017

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN119 , mod200old.getDoubleValue(Mod2002017Key.BN121)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN124 , mod200old.getDoubleValue(Mod2002017Key.BN126)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1597 , mod200old.getDoubleValue(Mod2002017Key.BN1599)+mod200old.getDoubleValue(Mod2002017Key.BN129)) // 2017
			                                                                    
		})                                                           
			                                                                    
		,PAG16 ( new IPropertyFiller[] {
			
			// Deducciones por doble imposición internacional RDL 4/2004
			(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN153, adjustDoubleTax(  // 2005
					mod200old.getDoubleValue(Mod2002017Key.BN639), mod200old.getDoubleValue(Mod2002017Key.BN728), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN154, adjustDoubleTax( // 2006
					mod200old.getDoubleValue(Mod2002017Key.BN197), mod200old.getDoubleValue(Mod2002017Key.BN729), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN155, adjustDoubleTax( // 2007
					mod200old.getDoubleValue(Mod2002017Key.BN287), mod200old.getDoubleValue(Mod2002017Key.BN730), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN156, adjustDoubleTax( // 2008
					mod200old.getDoubleValue(Mod2002017Key.BN827), mod200old.getDoubleValue(Mod2002017Key.BN731), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN157, adjustDoubleTax( // 2009
					mod200old.getDoubleValue(Mod2002017Key.BN003), mod200old.getDoubleValue(Mod2002017Key.BN732), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN158, adjustDoubleTax( // 2010
					mod200old.getDoubleValue(Mod2002017Key.BN030), mod200old.getDoubleValue(Mod2002017Key.BN733), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))  
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN159, adjustDoubleTax( // 2011
					mod200old.getDoubleValue(Mod2002017Key.BN719), mod200old.getDoubleValue(Mod2002017Key.BN734), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN720, adjustDoubleTax( // 2012
					mod200old.getDoubleValue(Mod2002017Key.BN724), mod200old.getDoubleValue(Mod2002017Key.BN721), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN739, adjustDoubleTax( // 2013
					mod200old.getDoubleValue(Mod2002017Key.BN742), mod200old.getDoubleValue(Mod2002017Key.BN921), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN134, adjustDoubleTax( // 2014
					mod200old.getDoubleValue(Mod2002017Key.BN137), mod200old.getDoubleValue(Mod2002017Key.BN926), mod200old.getDoubleValue(Mod2002017Key.BN103C) ))
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN728 , mod200old.getDoubleValue(Mod2002017Key.BN728 )) // 2005 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN729 , mod200old.getDoubleValue(Mod2002017Key.BN729 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN730 , mod200old.getDoubleValue(Mod2002017Key.BN730 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN731 , mod200old.getDoubleValue(Mod2002017Key.BN731 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN732 , mod200old.getDoubleValue(Mod2002017Key.BN732 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN733 , mod200old.getDoubleValue(Mod2002017Key.BN733 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN734 , mod200old.getDoubleValue(Mod2002017Key.BN734 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN721 , mod200old.getDoubleValue(Mod2002017Key.BN721 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN921 , mod200old.getDoubleValue(Mod2002017Key.BN921 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN926 , mod200old.getDoubleValue(Mod2002017Key.BN926 )) // 2014

			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN637 , mod200old.getDoubleValue(Mod2002017Key.BN639 )) // 2005 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN849 , mod200old.getDoubleValue(Mod2002017Key.BN197 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN285 , mod200old.getDoubleValue(Mod2002017Key.BN287 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN825 , mod200old.getDoubleValue(Mod2002017Key.BN827 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN001 , mod200old.getDoubleValue(Mod2002017Key.BN003 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN028 , mod200old.getDoubleValue(Mod2002017Key.BN030 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN717 , mod200old.getDoubleValue(Mod2002017Key.BN719 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN722 , mod200old.getDoubleValue(Mod2002017Key.BN724 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN740 , mod200old.getDoubleValue(Mod2002017Key.BN742 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN135 , mod200old.getDoubleValue(Mod2002017Key.BN137 )) // 2014
			
			// Deducciones por doble imposición internacional LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1054, adjustDoubleTax(mod200old.getDoubleValue(Mod2002017Key.BN1053), mod200old.getDoubleValue(Mod2002017Key.BN1050), mod200old.getDoubleValue(Mod2002017Key.BN103D) )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1348, adjustDoubleTax(mod200old.getDoubleValue(Mod2002017Key.BN1352), mod200old.getDoubleValue(Mod2002017Key.BN1349), mod200old.getDoubleValue(Mod2002017Key.BN103D) )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1770, adjustDoubleTax(mod200old.getDoubleValue(Mod2002017Key.BN1774)+mod200old.getDoubleValue(Mod2002017Key.BN174), mod200old.getDoubleValue(Mod2002017Key.BN1771), mod200old.getDoubleValue(Mod2002017Key.BN103D) )) // 2017
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1050, mod200old.getDoubleValue(Mod2002017Key.BN1050)) // 2015 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1349, mod200old.getDoubleValue(Mod2002017Key.BN1349)) // 2016 - Tipo de gravamen
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1771, mod200old.getDoubleValue(Mod2002017Key.BN1771)) // 2017 - Tipo de gravamen
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1051, mod200old.getDoubleValue(Mod2002017Key.BN1053)) // 2015 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1350, mod200old.getDoubleValue(Mod2002017Key.BN1352)) // 2016 - Deducción pendiente
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1772, mod200old.getDoubleValue(Mod2002017Key.BN1774)+mod200old.getDoubleValue(Mod2002017Key.BN174)) // 2017 - Deducción pendiente
										
			// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004 y art. 36 ter Ley 43/95
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN838 , mod200old.getDoubleValue(Mod2002017Key.BN840 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN932 , mod200old.getDoubleValue(Mod2002017Key.BN934 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN297 , mod200old.getDoubleValue(Mod2002017Key.BN299 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN090 , mod200old.getDoubleValue(Mod2002017Key.BN092 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN004 , mod200old.getDoubleValue(Mod2002017Key.BN006 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN031 , mod200old.getDoubleValue(Mod2002017Key.BN033 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN022 , mod200old.getDoubleValue(Mod2002017Key.BN024 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN040 , mod200old.getDoubleValue(Mod2002017Key.BN042 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN138 , mod200old.getDoubleValue(Mod2002017Key.BN140 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN141 , mod200old.getDoubleValue(Mod2002017Key.BN143 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN188 , mod200old.getDoubleValue(Mod2002017Key.BN190 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN803 , mod200old.getDoubleValue(Mod2002017Key.BN805 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1055, mod200old.getDoubleValue(Mod2002017Key.BN1057)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN700 , mod200old.getDoubleValue(Mod2002017Key.BN709 )) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1353, mod200old.getDoubleValue(Mod2002017Key.BN1355)+mod200old.getDoubleValue(Mod2002017Key.BN1777)) // 2017
			
		})
        
		,PAG16B ( new IPropertyFiller[] {
				
			// Deducciones disposición transitoria 24ª.1 LIS		
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN749 , mod200old.getDoubleValue(Mod2002017Key.BN754)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN752 , mod200old.getDoubleValue(Mod2002017Key.BN757)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN755 , mod200old.getDoubleValue(Mod2002017Key.BN760)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN758 , mod200old.getDoubleValue(Mod2002017Key.BN763)) // 2016			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN761 , mod200old.getDoubleValue(Mod2002017Key.BN746)+mod200old.getDoubleValue(Mod2002017Key.BN784)) // 2017
			
			// Deducciones inversión en Canarias
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN854 , mod200old.getDoubleValue(Mod2002017Key.BN1356)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN857 , mod200old.getDoubleValue(Mod2002017Key.BN859 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN860 , mod200old.getDoubleValue(Mod2002017Key.BN862 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN863 , mod200old.getDoubleValue(Mod2002017Key.BN865 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN883 , mod200old.getDoubleValue(Mod2002017Key.BN885 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN785 , mod200old.getDoubleValue(Mod2002017Key.BN790 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1357, mod200old.getDoubleValue(Mod2002017Key.BN1359)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1778, mod200old.getDoubleValue(Mod2002017Key.BN1780)+mod200old.getDoubleValue(Mod2002017Key.BN856)) // 2017
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN868 , mod200old.getDoubleValue(Mod2002017Key.BN834 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN871 , mod200old.getDoubleValue(Mod2002017Key.BN873 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN874 , mod200old.getDoubleValue(Mod2002017Key.BN876 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN877 , mod200old.getDoubleValue(Mod2002017Key.BN889 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN880 , mod200old.getDoubleValue(Mod2002017Key.BN882 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN866 , mod200old.getDoubleValue(Mod2002017Key.BN870 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN939 , mod200old.getDoubleValue(Mod2002017Key.BN941 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN191 , mod200old.getDoubleValue(Mod2002017Key.BN193 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN613 , mod200old.getDoubleValue(Mod2002017Key.BN701 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN200 , mod200old.getDoubleValue(Mod2002017Key.BN011 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN037 , mod200old.getDoubleValue(Mod2002017Key.BN039 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN044 , mod200old.getDoubleValue(Mod2002017Key.BN046 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN528 , mod200old.getDoubleValue(Mod2002017Key.BN530 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN144 , mod200old.getDoubleValue(Mod2002017Key.BN146 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN147 , mod200old.getDoubleValue(Mod2002017Key.BN149 )) // 2014
            
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN240 , mod200old.getDoubleValue(Mod2002017Key.BN242 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1058, mod200old.getDoubleValue(Mod2002017Key.BN1060)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN791, mod200old.getDoubleValue(Mod2002017Key.BN806)+mod200old.getDoubleValue(Mod2002017Key.BN1783)) // 2017
		})
			
		,PAG17_18 ( new IPropertyFiller[] {
				
		    // Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN780 , mod200old.getDoubleValue(Mod2002017Key.BN782 )) // 2000
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN786 , mod200old.getDoubleValue(Mod2002017Key.BN788 )) // 2001
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN766 , mod200old.getDoubleValue(Mod2002017Key.BN833 )) // 2002
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN198 , mod200old.getDoubleValue(Mod2002017Key.BN897 )) // 2003
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN288 , mod200old.getDoubleValue(Mod2002017Key.BN290 )) // 2004
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN466 , mod200old.getDoubleValue(Mod2002017Key.BN468 )) // 2005
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN061 , mod200old.getDoubleValue(Mod2002017Key.BN586 )) // 2006
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN472 , mod200old.getDoubleValue(Mod2002017Key.BN478 )) // 2007
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN180 , mod200old.getDoubleValue(Mod2002017Key.BN182 )) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN531 , mod200old.getDoubleValue(Mod2002017Key.BN533 )) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN945 , mod200old.getDoubleValue(Mod2002017Key.BN947 )) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN960 , mod200old.getDoubleValue(Mod2002017Key.BN962 )) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN183 , mod200old.getDoubleValue(Mod2002017Key.BN186 )) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN966 , mod200old.getDoubleValue(Mod2002017Key.BN968 )) // 2013 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN457 , mod200old.getDoubleValue(Mod2002017Key.BN459 )) // 2013 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN460 , mod200old.getDoubleValue(Mod2002017Key.BN462 )) // 2013 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1063, mod200old.getDoubleValue(Mod2002017Key.BN1065)) // 2014 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1066, mod200old.getDoubleValue(Mod2002017Key.BN1068)) // 2014 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1069, mod200old.getDoubleValue(Mod2002017Key.BN1071)) // 2014 IT
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN813 , mod200old.getDoubleValue(Mod2002017Key.BN815 )) // 2015 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN986 , mod200old.getDoubleValue(Mod2002017Key.BN507 )) // 2015 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN557 , mod200old.getDoubleValue(Mod2002017Key.BN594 )) // 2015 IT
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1641, mod200old.getDoubleValue(Mod2002017Key.BN1616)) // 2016 excepto I+D+i
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1617, mod200old.getDoubleValue(Mod2002017Key.BN1619)) // 2016 CT 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1620, mod200old.getDoubleValue(Mod2002017Key.BN1622)) // 2016 IT
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1847, mod200old.getDoubleValue(Mod2002017Key.BN1362)+ // 2017 excepto I+D+i
																					   mod200old.getDoubleValue(Mod2002017Key.BN809)+																					   
																					   mod200old.getDoubleValue(Mod2002017Key.BN1077)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN965)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN751)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN797)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN889)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1371)+																					   
																					   mod200old.getDoubleValue(Mod2002017Key.BN440)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1083)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1086)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1089)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1092)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1095)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1098)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1101)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1104)+		
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1107)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1116)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1119)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1374)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1377)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1380)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1383)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1386)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1389)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1392)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1395)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1398)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1401)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1404)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1407)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1416)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1419)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1422)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1425)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1625)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1628)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1631)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1634)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1637)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1640)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1643)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1646)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1649)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1652)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1655)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1658)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1661)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1664)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1667)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1670)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1673)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1676)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1679)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1682)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1691)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1694)+		
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1697)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1700)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN1703)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1706)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1709)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1802)+
																					   mod200old.getDoubleValue(Mod2002017Key.BN1685)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN830 ))			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1850, mod200old.getDoubleValue(Mod2002017Key.BN1365)+  // 2017 CT
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN800 )) 
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1853, mod200old.getDoubleValue(Mod2002017Key.BN1368)+  // 2017 IT
					                                                                   mod200old.getDoubleValue(Mod2002017Key.BN713 ))
			
		})
			
		,PAG18B ( new IPropertyFiller[] {

			// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN066 , mod200old.getDoubleValue(Mod2002017Key.BN084)) // 2008
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN008 , mod200old.getDoubleValue(Mod2002017Key.BN010)) // 2009
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN034 , mod200old.getDoubleValue(Mod2002017Key.BN036)) // 2010
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN201 , mod200old.getDoubleValue(Mod2002017Key.BN203)) // 2011
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN904 , mod200old.getDoubleValue(Mod2002017Key.BN906)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN990 , mod200old.getDoubleValue(Mod2002017Key.BN992)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN997 , mod200old.getDoubleValue(Mod2002017Key.BN999)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN246 , mod200old.getDoubleValue(Mod2002017Key.BN248)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN993 , mod200old.getDoubleValue(Mod2002017Key.BN995)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1434, mod200old.getDoubleValue(Mod2002017Key.BN1436)+mod200old.getDoubleValue(Mod2002017Key.BN1720)) // 2017
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.1 LIS)
			// Según PADIS del 2017:
			// Ejercicio 2015 = 2% [01167] = [01166] x 0,02
			// Ejercicio 2016 = 5% [01439] = [01438] x 0,05
			// Ejercicio 2017 = 5% [01443] = [01442] x 0,05
			//                     [01722] = [01721] x 0,05
			
		    // Dado que el importe pendiente está en funcion del importe resultante de aplicar a la base de deduccion un porcentaje, 
			// para obtener ahora esa base de deducción, según el importe pendiente del año anterior, habra que hacer la operacion inversa
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1166, AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1169)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1438, AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1441)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1442, AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1445)/0.05)+
					                                                                   AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1724)/0.05)) // 2017
			
			// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)                                                                          
			// Según PADIS del 2017:
			// Ejercicio 2015 = 2%  [01179] = [01178] x 0,02
			// Ejercicio 2016 = 5%  [01448] = [01447] x 0,05
			// Ejercicio 2017 = 5%  [01452] = [01451] x 0,05
  			//                      [01726] = [01725] x 0,05
  			                        
  		    // idem apartado anterior
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1178, AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1181)/0.02)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1447, AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1450)/0.05)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.BN1451, AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1454)/0.05)+
			                                                              			   AonMathUtils.round(mod200old.getDoubleValue(Mod2002017Key.BN1728)/0.05)) // 2017

			// Reserva de nivelación - Reducción en base imponible 	
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1141, mod200old.getDoubleValue(Mod2002017Key.LQ1143)) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1144, mod200old.getDoubleValue(Mod2002017Key.LQ1146)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1455, mod200old.getDoubleValue(Mod2002017Key.LQ1457)+mod200old.getDoubleValue(Mod2002017Key.LQ1731))  // 2017
				
		})

		,PAG20 ( new IPropertyFiller[] {
				
			// Limitacion en la deducilidad de gastos financieros. Gastos financieros pendientes de deducir
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1188, mod200old.getDoubleValue(Mod2002017Key.LM1191)) // 2012
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1193, mod200old.getDoubleValue(Mod2002017Key.LM1196)) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1198, mod200old.getDoubleValue(Mod2002017Key.LM1201)) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1202, mod200old.getDoubleValue(Mod2002017Key.LM1205)) // 2015 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1203, mod200old.getDoubleValue(Mod2002017Key.LM1206)) // 2015 Resto
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1462, mod200old.getDoubleValue(Mod2002017Key.LM1210)) // 2016 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1463, mod200old.getDoubleValue(Mod2002017Key.LM1211)) // 2016 Resto
			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1736, mod200old.getDoubleValue(Mod2002017Key.LM1465)+mod200old.getDoubleValue(Mod2002017Key.LM1739)) // 2017 Por límite 16.5 y 83 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1737, mod200old.getDoubleValue(Mod2002017Key.LM1466)+mod200old.getDoubleValue(Mod2002017Key.LM1740)) // 2017 Resto
			
			// Pendiente de adición por límite beneficio operativo no aplicado			
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM503 , mod200old.getDoubleValue(Mod2002017Key.LM523 )) // 2013
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM273 , mod200old.getDoubleValue(Mod2002017Key.LM537 )) // 2014
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM955 , mod200old.getDoubleValue(Mod2002017Key.LM957 )) // 2015
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1217, mod200old.getDoubleValue(Mod2002017Key.LM1219)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1467, mod200old.getDoubleValue(Mod2002017Key.LM1469)+mod200old.getDoubleValue(Mod2002017Key.LM1743)) // 2017
		})
			
		,PAG20B ( new IPropertyFiller[] {
			
			// Reserva de capitalización
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1134, mod200old.getDoubleValue(Mod2002017Key.LQ1136)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1470, mod200old.getDoubleValue(Mod2002017Key.LQ1472)+mod200old.getDoubleValue(Mod2002017Key.LQ1746)) // 2017
			
            // Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
            // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible            
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1473, mod200old.getDoubleValue(Mod2002017Key.LM1476)) // 2007 y anteriores: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1408, mod200old.getDoubleValue(Mod2002017Key.LM1409)) // 2007 y anteriores: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1477, mod200old.getDoubleValue(Mod2002017Key.LM1483)) // 2008 a 2015: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1478, mod200old.getDoubleValue(Mod2002017Key.LM1484)) // 2008 a 2015: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1485, mod200old.getDoubleValue(Mod2002017Key.LM1489)) // 2016: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1486, mod200old.getDoubleValue(Mod2002017Key.LM1490)) // 2016: Que han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1491, mod200old.getDoubleValue(Mod2002017Key.LM1493)+mod200old.getDoubleValue(Mod2002017Key.LM1752)) // 2017: Que no han cumplido ...
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1747, mod200old.getDoubleValue(Mod2002017Key.LM1749)) // 2017: Que han cumplido ...
					                                                                  
		})
		
		,PAG20T ( new IPropertyFiller[] {
				
			// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS  
			 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1524, mod200old.getDoubleValue(Mod2002017Key.LM1528)) // 2007 y anteriores
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1529, mod200old.getDoubleValue(Mod2002017Key.LM1534)) // 2008 a 2015
				
			// Activos por impuesto diferido (AID). Art. 130 LIS
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1542, mod200old.getDoubleValue(Mod2002017Key.LM1549)+mod200old.getDoubleValue(Mod2002017Key.LM1550)+mod200old.getDoubleValue(Mod2002017Key.LM1551)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1552, mod200old.getDoubleValue(Mod2002017Key.LM1558)+mod200old.getDoubleValue(Mod2002017Key.LM1559)+mod200old.getDoubleValue(Mod2002017Key.LM1560)+
					                                                                   mod200old.getDoubleValue(Mod2002017Key.LM1760)+mod200old.getDoubleValue(Mod2002017Key.LM1761)+mod200old.getDoubleValue(Mod2002017Key.LM1762)) // 2017
				
			// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1571, mod200old.getDoubleValue(Mod2002017Key.LM1574)) // 2016
			,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LM1575, mod200old.getDoubleValue(Mod2002017Key.LM1578)+mod200old.getDoubleValue(Mod2002017Key.LM1766)) // 2018
			
		})
		
        ,PAG22 ( new IPropertyFiller[] {
        		
    		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)
        	 (mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.RC089 , mod200old.getDoubleValue(Mod2002017Key.RC048)) // 2014 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.RC097 , mod200old.getDoubleValue(Mod2002017Key.RC527)) // 2015				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.RC524 , mod200old.getDoubleValue(Mod2002017Key.RC925)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.RC922 , mod200old.getDoubleValue(Mod2002017Key.RC996)) // 2017
					                                                                  
        	// Régimen de cooperativas - Detalle de compensación de cuotas	                                                                				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ673 , mod200old.getDoubleValue(Mod2002017Key.LQ1224)) // 2000 			
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ676 , mod200old.getDoubleValue(Mod2002017Key.LQ678 )) // 2001				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ679 , mod200old.getDoubleValue(Mod2002017Key.LQ681 )) // 2002				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ682 , mod200old.getDoubleValue(Mod2002017Key.LQ684 )) // 2003				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ685 , mod200old.getDoubleValue(Mod2002017Key.LQ687 )) // 2004				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ688 , mod200old.getDoubleValue(Mod2002017Key.LQ690 )) // 2005				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ691 , mod200old.getDoubleValue(Mod2002017Key.LQ693 )) // 2006				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ623 , mod200old.getDoubleValue(Mod2002017Key.LQ672 )) // 2007				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ279 , mod200old.getDoubleValue(Mod2002017Key.LQ281 )) // 2008				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ587 , mod200old.getDoubleValue(Mod2002017Key.LQ900 )) // 2009				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ059 , mod200old.getDoubleValue(Mod2002017Key.LQ100 )) // 2010				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ017 , mod200old.getDoubleValue(Mod2002017Key.LQ019 )) // 2011				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ772 , mod200old.getDoubleValue(Mod2002017Key.LQ777 )) // 2012				
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ907 , mod200old.getDoubleValue(Mod2002017Key.LQ909 )) // 2013
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ910 , mod200old.getDoubleValue(Mod2002017Key.LQ912 )) // 2014
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ935 , mod200old.getDoubleValue(Mod2002017Key.LQ937 )) // 2015
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1511, mod200old.getDoubleValue(Mod2002017Key.LQ1513)) // 2016
            ,(mod200old,mod200new) -> setDoubleValue( mod200new, Mod2002018Key.LQ1767, mod200old.getDoubleValue(Mod2002017Key.LQ1769)+ // 2017 (Tambien se añade la casilla 560 del ejercicio anterior si es negativa y es cooperativa)
            		                                                                   mod200old.getDoubleValue(Mod2002017Key.LQ1226)+
            		                                                                  ((mod200old.getDoubleValue(Mod2002017Key.C0017)==1 || mod200old.getDoubleValue(Mod2002017Key.C0018)==1 || mod200old.getDoubleValue(Mod2002017Key.C0019)==1) && (mod200old.getDoubleValue(Mod2002017Key.LQ560)<0) ? Math.abs(mod200old.getDoubleValue(Mod2002017Key.LQ560)) : 0)  )  
			
		})
		
		;
		
		private IPropertyFiller[] propertyFillers;
		
		private Pages2018 (IPropertyFiller[] propertyFillers) {
			this.propertyFillers = propertyFillers;
		}

		public static void fill(Mod2002017 mod200old, Mod2002018 mod200new) {
			for ( Pages2018 page : Pages2018.values()) {
					for (IPropertyFiller propertyFiller : page.propertyFillers) {
						propertyFiller.propertyFill(mod200old, mod200new);
					}				
			}
		}
	}
	
	public static void import2017(Mod2002018 mod200new,Mod2002017 mod200old) {
		try {
			Pages2018.fill(mod200old, mod200new);
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
	}

	public static Mod2002018 import2017(Mod2002017 mod200old) {
		
		try {
			// Creamos el objeto Mod200 del ejercicio actual, donde importaremos los datos del ejercicio anterior
			Mod2002018 mod200new = new Mod2002018();
			import2017(mod200new, mod200old);
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
	
	private static void toString(Mod2002018 mod200) {
		
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
		for (Mod2002018Key key : Mod2002018Key.values()) {			
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
	
	public static void main(String argv[]) throws UnsupportedEncodingException, FileNotFoundException {
		
		// Esta prueba unicamente crea un objeto del año anterior e inicializa sus casillas con 
		// los códigos, posteriormente llama a la importacion para ver que se trasladan correctamente
		try {			
				Mod2002017 mod200old = new Mod2002017();
				if (mod200old!=null) {				
					
					// PRUEBA - Inicializamos todas las claves con sus numeros
					
					mod200old.setBalanceType(2); // PYMES
					mod200old.setPygType(2); // PYMES
					 
					for (Mod2002017Key key : Mod2002017Key.values()) {						
						try {
							setDoubleValue2017(mod200old, key, Double.parseDouble(key.name().substring(2)));
						} catch (NumberFormatException e) {							
							//e.printStackTrace();
						}
					}					
					
					// Prueba base imponible negativa (casilla 552)
					setDoubleValue2017(mod200old, Mod2002017Key.LQ552, -552 );
					
					// Cooperativas Casillas 17, 18 y 19. Cuota compensacion negativa (casilla 560) 
					setDoubleValue2017(mod200old, Mod2002017Key.C0017, 0 );
					setDoubleValue2017(mod200old, Mod2002017Key.C0018, 0 );
					setDoubleValue2017(mod200old, Mod2002017Key.C0019, 0 );
					setDoubleValue2017(mod200old, Mod2002017Key.LQ560, 0 );
					
					// FIN PRUEBA
					
					Mod2002018 mod200new = import2017(mod200old);					
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


