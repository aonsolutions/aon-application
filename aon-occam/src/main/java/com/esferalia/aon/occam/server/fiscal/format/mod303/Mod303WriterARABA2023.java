package com.esferalia.aon.occam.server.fiscal.format.mod303;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IMod303Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303WriterARABA2023 implements IMod303Writer {

	private enum Mod303File {
		// ARABA - Registro Cabecera
		 ARABA_RC ( mod -> true ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("C") 											// Tipo de registro = C
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  	// NIF del profesional X(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) 	// Apellidos y nombre o razón social del profesional X(50)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40))                 	// Libre a blancos X(40)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(1,6,0))            	// Número de declaraciones presentadas 9(6) (siempre se presenta 1 declaración) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(4894))               // Libre a blancos X(1994)
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		
		// ARABA - Registro Detalle
		,ARABA_RD ( mod -> true ,new IPropertyFiller[] {
     		 (wr, mod) -> wr.append("D")                                           // Tipo de registro = D
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9) (El mismo que hayamos puesto en el registro de cabecera) 			
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del declarante X(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)), (wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getStartMonth()+1,2,0)) // Período inicial (AAAAMM) 9(6)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)), (wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getDueMonth()+1,2,0))   // Período final (AAAAMM) 9(6)
			,(wr, mod) -> wr.append("303")                                         // Modelo de la declaración (303)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getDeclarationResult(),18))  // Resultado de la declaración: Signo (blanco ó N) + Resultado 9(12) V99
			,(wr, mod) -> wr.append("E")                                           // Moneda (blanco ó E)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) // Apellidos y nombre o razón social X(50)
			
		    ,(wr, mod) -> {
		    	String streetName = 
	    			(AonStringUtils.isNotBlank(mod.getStreetInitial())?
		    			AonStringUtils.trim(mod.getStreetInitial()) + " "
		    			:"")
	    			+ AonStringUtils.trim(mod.getStreetName());
				String streetNumber = mod.getStreetNumber();
				
				// Como el formato del numero debe ser 3 dígitos, si lleva letras o es mayor de 3 entonces se pone junto con la direccion
				if (streetNumber != null && (!AonStringUtils.isNumericSpace(streetNumber) || streetNumber.length() > 3)) {					
					streetName = streetName + ", " + streetNumber;
					streetNumber = "";
				}
				
				wr.append(AonFiscalFileUtils.text(streetName, 25));         // Domicilio - Texto calle Domicilio del declarante X(25)
				wr.append(AonFiscalFileUtils.unsigned(streetNumber, 3, 0)); // Domicilio - Número portal 9(3)		    
		    }
		    
			,(wr, mod) -> wr.append(AonStringUtils.SPACE)                               // Domicilio - Letra portal X(1)    
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getStreetStair(),2))    // Domicilio - Escalera portal X(2) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getStreetFloor(),2))    // Domicilio - Piso X(2) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getStreetDoor(),3))     // Domicilio - Mano X(3) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(25))                      // Texto entidad X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getTown(), 25))         // Texto municipio X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getProvince(), 25))		// Texto provincia X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getZip(), 5, 0))	// Código postal 9(5)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPhone(),9,0))    // Teléfono del declarante 9(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(25))                      // Anagrama X(25)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(17)) 						// Clave de actividad: 1 (Modelo 310)  X(17)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(17)) 						// Clave de actividad: 11 (Modelo 310) X(17)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(16)) 						// Clave de actividad: 21 (Modelo 310) X(16)
			
			// CLAVES-IMPORTES declaración (200 ocurrencias.) - Las claves posibles del modelo 303
			// Todas llevan el importe con signo (blanco o N) + importe (12 enteros y 2 decimales), 
			// excepto determinadas claves que solo usan la parte entera por que llevan 5 decimales 
			// o por que son casilla Si/No
			
			,(wr, mod) -> wr.append("090" + AonFiscalFileUtils.signedSpace((mod.isWithoutActivity()?1.0:0.0),18)) // [90] Sin actividad
			,(wr, mod) -> wr.append("091" + AonFiscalFileUtils.signedSpace((mod.isReplacement()?1.0:0.0),18))     // [91] Sustitutiva
			
			,(wr, mod) -> wr.append(mod.isReplacement() ? 
					 "902"+AonStringUtils.SPACE+AonFiscalFileUtils.unsigned(mod.getReplacedNumber(),15,2) 
					:"902"+AonStringUtils.SPACE+AonFiscalFileUtils.zeros(17)) // [902] Sustitutiva Numero declaracion anterior (año y numero) - solo si se ha presentado por internet
			
			,(wr, mod) -> wr.append("907" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C907),18))  // [907] Ha sido declarado en concurso de acreedores en el presente período de liquidación
			,(wr, mod) -> wr.append("930" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C930),18))  // [9307]
			,(wr, mod) -> wr.append("092" + AonFiscalFileUtils.signedSpace((mod.isEnrolledInDevolutionRegistry()?1.0:0.0),18)) // [92] Está inscrito en el Registro de devolución mensual
			,(wr, mod) -> wr.append("910" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C910),18))  // [910] Ha optado por el Régimen especial del criterio de caja 
			,(wr, mod) -> wr.append("911" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C911),18))  // [911] Es destinatario de operaciones a las que se aplique el Régimen especial del criterio de caja
			// La casilla 908 va al final en las posiciones 2011 a 2018
			,(wr, mod) -> wr.append("909" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C909),18))  // [909] Si se ha dictado auto de declaración de concurso en este periodo indique el tipo de autoliquidación indicar si es Preconcursal o Postconcursal
			
			// IVA Devengado
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C210))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C211))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C212))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C001))  
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C002))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C003))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C201))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C202))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C203))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C204))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C205))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C206))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C207))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C208))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C209))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C370))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C371))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C372))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C373))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C010))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C011))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C012))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C213))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C214))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C215))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C219))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C220))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C221))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C216))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C217))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C218))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C374))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C375))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C019))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C020))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C021))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C231))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C232))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C233))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C222))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C223))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C224))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C225))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C226))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C227))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C376))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C377))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C028))
			
			// Deducciones
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C030))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C031))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C032))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C033))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C034))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C035))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C036))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C037))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C046))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C038))
			
			// Diferencia
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C039))
			
			// Volumen de Operaciones, va con 5 decimales. Se pone el numero completo (incluido los decimales) en la parte entera
			,(wr, mod) -> wr.append(Mod303Key.AR_C040.getBoxCode() + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C040),16,5)+"00" )
			,(wr, mod) -> wr.append(Mod303Key.AR_C041.getBoxCode() + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C041),16,5)+"00" )
			,(wr, mod) -> wr.append(Mod303Key.AR_C042.getBoxCode() + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C042),16,5)+"00" )
			,(wr, mod) -> wr.append(Mod303Key.AR_C043.getBoxCode() + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C043),16,5)+"00" )
			
			// Cuotas atribuible / cuotas a compensar / Resultado autoliquidacion / recargos / total
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C044))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C045))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C060))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C061))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C062))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C063))
			,(wr, mod) -> wr.append(Mod303Key.AR_C080.getBoxCode() +	 
				AonFiscalFileUtils.signedSpace((mod.getAmount(Mod303Key.AR_C080)>=0?mod.getAmount(Mod303Key.AR_C080):0.0),18))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C081))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C082))
			
			// Informacion adicional
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C050))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C051))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C054))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C055))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C056))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C058))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C180))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C181))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C182))
			,(wr, mod) -> wr.append(print(mod, Mod303Key.AR_C183))
			
			// Datos bancarios (CCC), Solo si es devolucion o es domiciliacion y está cumplimentada la cuenta bancaria			
			,(wr, mod) -> {
				if ((mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK || mod.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK) &&
					(mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20))
					{
						wr.append("301" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 0, 4),17,2));
						wr.append("302" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 4, 8),17,2));
						wr.append("303" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 8,10),17,2));
						wr.append("304" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring(10,20),17,2));						
					}
				else					
				{
					for (int i=1;i<=4;i++) {
						wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,18));
					}
				}
			}
			
			// Resto de casillas hasta completar las 200 (hasta ahora van 95)			
			,(wr, mod) -> {
				for (int i=0; i<(200-95); i++) {
					wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,18));
				}
			}
			
			// Fecha en la que se dicto el concurso de acreedores
			,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDateZero(mod.getDescription(Mod303Key.AR_C908)))			
			
			
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(489))
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			
	    })
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod303File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod303 mod303) {
			return accepter.accept(mod303);
		}
		private void fillPage(Mod303 mod303, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod303);
			}
		}
	}

	public void fillWriter(Mod303 mod303, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod303File format : Mod303File.values()) {
			if (format.accept(mod303)) {
				format.fillPage(mod303, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}

	private static String print(Mod303 mod, Mod303Key key) {
		return key.getBoxCode() + AonFiscalFileUtils.signedSpace(mod.getAmount(key),18);
	}
	
}
