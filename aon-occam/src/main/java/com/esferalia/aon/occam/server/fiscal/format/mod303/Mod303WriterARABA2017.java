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

public class Mod303WriterARABA2017 implements IMod303Writer{

	private static enum Mod303File {
		// ARABA - Registro Cabecera
		 ARABA_2016_RC ( mod -> true ,new IPropertyFiller[] {
				
			 // Habría que ver si aqui se pueden poner siempre los datos del contribuyente, o cuando
			 // la presentación la hace una asesoría o profesional, necesariamente deben ir los datos del profesional
			 // El programa de ayuda lo carga bien, pongas lo que pongas, así que por ahora, se ponen los mismo datos del contribuyente
			 (wr, mod) -> wr.append("C") // Tipo de registro = C
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) // Apellidos y nombre o razón social del profesional X(50)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40))                 // Libre a blancos X(40)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(1,6,0))            // Número de declaraciones presentadas 9(6) (siempre se presenta 1 declaración) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(1994))               // Libre a blancos X(1994)
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		
		// ARABA - Registro Detalle
		,ARABA_2016_RD ( mod -> true ,new IPropertyFiller[] {
     		 (wr, mod) -> wr.append("D")                                           // Tipo de registro = D
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9) (El mismo que hayamos puesto en el registro de cabecera) 			
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del declarante X(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)), (wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getStartMonth()+1,2,0)) // Período inicial (AAAAMM) 9(6)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)), (wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getDueMonth()+1,2,0))   // Período final (AAAAMM) 9(6)
			,(wr, mod) -> wr.append("303")                                         // Modelo de la declaración (303)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C080),15))  // Resultado de la declaración: Signo (blanco ó N) + Resultado 9(12) V99
			,(wr, mod) -> wr.append("E")                                           // Moneda (blanco ó E)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) // Apellidos y nombre o razón social X(50)
			
		    ,(wr, mod) -> {
		    	String streetName = (mod.getStreetInitial()+" "+mod.getStreetName()).trim();
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
			
			// CLAVES-IMPORTES declaración (95 ocurrencias.) - Las claves posibles del modelo 303
			// Todas llevan el importe con signo (blanco o N) + importe (12 enteros y 2 decimales), 
			// excepto determinadas claves que solo usan la parte entera por que llevan 5 decimales 
			// o por que son casilla Si/No
			
			,(wr, mod) -> wr.append("090" + AonFiscalFileUtils.signedSpace((mod.isWithoutActivity()?1.0:0.0),15)) // [90] Sin actividad
			,(wr, mod) -> wr.append("091" + AonFiscalFileUtils.signedSpace((mod.isReplacement()?1.0:0.0),15))     // [91] Sustitutiva
			
			// En el numero anterior hay que poner año + numero anterior, se supone que en el programa 
			// ya se introducira el año primero y despues el numero, todo junto			
			,(wr, mod) -> wr.append(mod.isReplacement() ? "902"+AonStringUtils.SPACE+AonFiscalFileUtils.unsigned(mod.getReplacedNumber(),14,2) : "000"+AonStringUtils.SPACE+AonFiscalFileUtils.zeros(14)) // [902] Sustitutiva Numero declaracion anterior (año y numero) - solo si se ha presentado por internet
			
			,(wr, mod) -> wr.append("907" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C907),15))  // [907] Ha sido declarado en concurso de acreedores en el presente período de liquidación
			,(wr, mod) -> wr.append("092" + AonFiscalFileUtils.signedSpace((mod.isEnrolledInDevolutionRegistry()?1.0:0.0),15)) // [92] Está inscrito en el Registro de devolución mensual
			,(wr, mod) -> wr.append("910" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C910),15))  // [910] Ha optado por el Régimen especial del criterio de caja 
			,(wr, mod) -> wr.append("911" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C911),15))  // [911] Es destinatario de operaciones a las que se aplique el Régimen especial del criterio de caja
			// La casilla 908 va al final en las posiciones 2011 a 2018
			,(wr, mod) -> wr.append("909" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C909),15))  // [909] Si se ha dictado auto de declaración de concurso en este periodo indique el tipo de autoliquidación indicar si es Preconcursal o Postconcursal
			
			// IVA Devengado
			,(wr, mod) -> wr.append("001" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C001),15))  
			,(wr, mod) -> wr.append("002" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C002),15))
			,(wr, mod) -> wr.append("003" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C003),15))
			,(wr, mod) -> wr.append("204" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C204),15))
			,(wr, mod) -> wr.append("205" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C205),15))
			,(wr, mod) -> wr.append("206" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C206),15))
			,(wr, mod) -> wr.append("207" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C207),15))
			,(wr, mod) -> wr.append("208" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C208),15))
			,(wr, mod) -> wr.append("209" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C209),15))
			,(wr, mod) -> wr.append("370" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C370),15))
			,(wr, mod) -> wr.append("371" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C371),15))
			,(wr, mod) -> wr.append("372" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C372),15))
			,(wr, mod) -> wr.append("373" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C373),15))
			,(wr, mod) -> wr.append("010" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C010),15))
			,(wr, mod) -> wr.append("011" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C011),15))
			,(wr, mod) -> wr.append("012" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C012),15))
			,(wr, mod) -> wr.append("213" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C213),15))
			,(wr, mod) -> wr.append("214" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C214),15))
			,(wr, mod) -> wr.append("215" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C215),15))
			,(wr, mod) -> wr.append("216" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C216),15))
			,(wr, mod) -> wr.append("217" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C217),15))
			,(wr, mod) -> wr.append("218" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C218),15))
			,(wr, mod) -> wr.append("374" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C374),15))
			,(wr, mod) -> wr.append("375" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C375),15))
			,(wr, mod) -> wr.append("019" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C019),15))
			,(wr, mod) -> wr.append("020" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C020),15))
			,(wr, mod) -> wr.append("021" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C021),15))
			,(wr, mod) -> wr.append("222" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C222),15))
			,(wr, mod) -> wr.append("223" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C223),15))
			,(wr, mod) -> wr.append("224" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C224),15))
			,(wr, mod) -> wr.append("225" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C225),15))
			,(wr, mod) -> wr.append("226" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C226),15))
			,(wr, mod) -> wr.append("227" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C227),15))
			,(wr, mod) -> wr.append("376" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C376),15))
			,(wr, mod) -> wr.append("377" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C377),15))
			,(wr, mod) -> wr.append("028" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C028),15))
			
			// Deducciones
			,(wr, mod) -> wr.append("030" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C030),15))
			,(wr, mod) -> wr.append("031" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C031),15))
			,(wr, mod) -> wr.append("032" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C032),15))
			,(wr, mod) -> wr.append("033" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C033),15))
			,(wr, mod) -> wr.append("034" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C034),15))
			,(wr, mod) -> wr.append("035" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C035),15))
			,(wr, mod) -> wr.append("036" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C036),15))
			,(wr, mod) -> wr.append("037" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C037),15))
			
			// Casilla [46] no existe en el modelo
			,(wr, mod) -> wr.append("046" + AonFiscalFileUtils.signedSpace(0.0,15))
			
			,(wr, mod) -> wr.append("038" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C038),15))
			
			// Diferencia
			,(wr, mod) -> wr.append("039" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C039),15))
			
			// Volumen de Operaciones, va con 5 decimales. Se pone todo el numero (incluido los decimales) en la parte entera
			,(wr, mod) -> wr.append("040" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C040),13,5)+"00" )
			,(wr, mod) -> wr.append("041" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C041),13,5)+"00" )
			,(wr, mod) -> wr.append("042" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C042),13,5)+"00" )
			,(wr, mod) -> wr.append("043" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C043),13,5)+"00" )
			
			// Cuotas atribuible / cuotas a compensar / Resultado autoliquidacion / recargos / total
			,(wr, mod) -> wr.append("044" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C044),15))
			,(wr, mod) -> wr.append("045" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C045),15))
			,(wr, mod) -> wr.append("060" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C060),15))
			,(wr, mod) -> wr.append("061" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C061),15))
			,(wr, mod) -> wr.append("062" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C062),15))
			,(wr, mod) -> wr.append("063" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C063),15))
			,(wr, mod) -> wr.append("080" + AonFiscalFileUtils.signedSpace((mod.getAmount(Mod303Key.AR_C080)>=0?mod.getAmount(Mod303Key.AR_C080):0.0),15))
			,(wr, mod) -> wr.append("081" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C081),15))
			,(wr, mod) -> wr.append("082" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C082),15))
			
			// Informacion adicional
			,(wr, mod) -> wr.append("050" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C050),15))
			,(wr, mod) -> wr.append("051" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C051),15))
			,(wr, mod) -> wr.append("052" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C052),15))
			,(wr, mod) -> wr.append("180" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C180),15))
			,(wr, mod) -> wr.append("181" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C181),15))
			,(wr, mod) -> wr.append("182" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C182),15))
			,(wr, mod) -> wr.append("183" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod303Key.AR_C183),15))
			
			// Datos bancarios (CCC), Solo si es devolucion o es domiciliacion y está cumplimentada la cuenta bancaria			
			,(wr, mod) -> {
				if ((mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK || mod.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK) &&
					(mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20))
					{
						wr.append("301" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 0, 4),14,2));
						wr.append("302" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 4, 8),14,2));
						wr.append("303" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 8,10),14,2));
						wr.append("304" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring(10,20),14,2));						
					}
				else					
				{
					for (int i=1;i<=4;i++)
						wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,15));				
				}
			}
			
			// Resto de casillas hasta completar las 95 (total 16, ya que hasta ahora van 79)			
			,(wr, mod) -> {
				for (int i=1;i<=16;i++)
					wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,15));
			}
			
			// Fecha en la que se dicto el concurso de acreedores
			,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDateZero(mod.getDescription(Mod303Key.AR_C908)))			
			
			// Libre a blancos X(82) y fin de registro
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(82))
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
	
}
