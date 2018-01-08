package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFWriter {

	@FunctionalInterface
	private interface IModelAccepter {
		public boolean accept(Mod390HF mod390);
	}
	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod390HF mod390) throws IOException;
	}
	
	private enum Mod390File {
		// **************************************************************** // 
		//                           A R A B A                              //
		// **************************************************************** //
		
		// ARABA - Registro Cabecera
		 ARABA_2017_RC ( mod -> (mod.isAraba() && mod.getYear() >= 2017) ,new IPropertyFiller[] {
				
			 // Habría que ver si aqui se pueden poner siempre los datos del contribuyente, o cuando
			 // la presentación la hace una asesoría o profesional, necesariamente deben ir los datos del profesional
			 // El programa de ayuda lo carga bien, pongas lo que pongas, así que por ahora, se ponen los mismo datos del contribuyente
			 (wr, mod) -> wr.append("C") // Tipo de registro = C
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) // Apellidos y nombre o razón social del profesional X(50)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40))                 // Libre a blancos X(40)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(1,6,0))            // Número de declaraciones presentadas 9(6) (siempre se presenta 1 declaración) 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(3894))               // Libre a blancos X(1994)
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		// ARABA - Registro Detalle
		,ARABA_2017_RD ( mod -> (mod.isAraba() && mod.getYear() >= 2017) ,new IPropertyFiller[] {
     		 (wr, mod) -> wr.append("D")                                           // Tipo de registro = D
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9) (El mismo que hayamos puesto en el registro de cabecera) 			
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del declarante X(9)
			
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getStartMonth()+1,2,0)) // Período inicial (AAAAMM) 9(6)
			
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getDueMonth()+1,2,0))   // Período final (AAAAMM) 9(6)
			
			,(wr, mod) -> wr.append("390")                                         		// Modelo de la declaración (390)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getResult(),15)) // Resultado de la declaración: Signo (blanco ó N) + Resultado 9(12) V99
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
			
			// CLAVES-IMPORTES declaración (200 ocurrencias.) - Las claves posibles del modelo 390
			// Todas llevan el importe con signo (blanco o N) + importe (12 enteros y 2 decimales), 
			// excepto determinadas claves que solo usan la parte entera por que llevan 5 decimales 
			// o por que son casilla Si/No
			
			,(wr, mod) -> wr.append("145" + AonFiscalFileUtils.signedSpace((mod.isWithoutActivity()?1.0:0.0),15)) // [145] Sin actividad
			,(wr, mod) -> wr.append("901" + AonFiscalFileUtils.signedSpace((mod.isReplacement()?1.0:0.0),15))     // [901] Sustitutiva
			
			// En el numero anterior hay que poner año + numero anterior, se supone que en el programa 
			// ya se introducira el año primero y despues el numero, todo junto			
			,(wr, mod) -> wr.append(mod.isReplacement() ? "902"+AonStringUtils.SPACE+AonFiscalFileUtils.unsigned(mod.getReplacedNumber(),14,2) : "000"+AonStringUtils.SPACE+AonFiscalFileUtils.zeros(14)) 
			// [902] Sustitutiva Numero declaracion anterior (año y numero) - solo si se ha presentado por internet
			
			,(wr, mod) -> wr.append("907" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C907),15))  // [907] Ha sido declarado en concurso de acreedores en el presente período de liquidación
			,(wr, mod) -> wr.append("918" + AonFiscalFileUtils.signedSpace((mod.isEnrolledInDevolutionRegistry()?1.0:0.0),15)) // [92] Está inscrito en el Registro de devolución mensual
			,(wr, mod) -> wr.append("910" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C910),15))  // [910] Ha optado por el Régimen especial del criterio de caja 
			,(wr, mod) -> wr.append("911" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C911),15))  // [911] Es destinatario de operaciones a las que se aplique el Régimen especial del criterio de caja
			// La casilla 908 va al final en las posiciones 2011 a 2018
			,(wr, mod) -> wr.append("909" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C909),15))  // [909] Si se ha dictado auto de declaración de concurso en este periodo indique el tipo de autoliquidación indicar si es Preconcursal o Postconcursal
			
			// IVA Devengado
			,(wr, mod) -> wr.append("001" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C001),15))  
			,(wr, mod) -> wr.append("002" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C002),15))
			,(wr, mod) -> wr.append("003" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C003),15))
			,(wr, mod) -> wr.append("804" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C804),15))
			,(wr, mod) -> wr.append("805" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C805),15))
			,(wr, mod) -> wr.append("806" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C806),15))
			,(wr, mod) -> wr.append("807" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C807),15))
			,(wr, mod) -> wr.append("808" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C808),15))
			,(wr, mod) -> wr.append("809" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C809),15))
			,(wr, mod) -> wr.append("351" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C351),15))
			,(wr, mod) -> wr.append("352" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C352),15))
			,(wr, mod) -> wr.append("019" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C019),15))
			,(wr, mod) -> wr.append("020" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C020),15))
			,(wr, mod) -> wr.append("223" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C223),15))
			,(wr, mod) -> wr.append("224" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C224),15))
			,(wr, mod) -> wr.append("025" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C025),15))
			,(wr, mod) -> wr.append("026" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C026),15))
			,(wr, mod) -> wr.append("027" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C027),15))
			,(wr, mod) -> wr.append("034" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C034),15))
			,(wr, mod) -> wr.append("035" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C035),15))
			,(wr, mod) -> wr.append("036" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C036),15))
			,(wr, mod) -> wr.append("828" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C828),15))
			,(wr, mod) -> wr.append("829" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C829),15))
			,(wr, mod) -> wr.append("830" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C830),15))
			,(wr, mod) -> wr.append("831" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C831),15))
			,(wr, mod) -> wr.append("832" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C832),15))
			,(wr, mod) -> wr.append("833" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C833),15))
			,(wr, mod) -> wr.append("037" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C037),15))
			,(wr, mod) -> wr.append("038" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C038),15))
			,(wr, mod) -> wr.append("239" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C239),15))
			,(wr, mod) -> wr.append("240" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C240),15))
			,(wr, mod) -> wr.append("010" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C010),15))
			,(wr, mod) -> wr.append("011" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C011),15))
			,(wr, mod) -> wr.append("012" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C012),15))
			,(wr, mod) -> wr.append("813" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C813),15))
			,(wr, mod) -> wr.append("814" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C814),15))
			,(wr, mod) -> wr.append("815" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C815),15))
			,(wr, mod) -> wr.append("816" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C816),15))
			,(wr, mod) -> wr.append("817" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C817),15))
			,(wr, mod) -> wr.append("818" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C818),15))
			,(wr, mod) -> wr.append("353" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C353),15))
			,(wr, mod) -> wr.append("354" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C354),15))
			,(wr, mod) -> wr.append("041" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C041),15))
			,(wr, mod) -> wr.append("042" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C042),15))
			
			// Deducciones
			,(wr, mod) -> wr.append("043" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C043),15))
			,(wr, mod) -> wr.append("044" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C044),15))
			,(wr, mod) -> wr.append("045" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C045),15))
			,(wr, mod) -> wr.append("846" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C846),15))
			,(wr, mod) -> wr.append("847" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C847),15))
			,(wr, mod) -> wr.append("848" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C848),15))
			,(wr, mod) -> wr.append("849" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C849),15))
			,(wr, mod) -> wr.append("850" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C850),15))
			,(wr, mod) -> wr.append("851" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C851),15))
			,(wr, mod) -> wr.append("076" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C076),15))
			,(wr, mod) -> wr.append("077" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C077),15))
			,(wr, mod) -> wr.append("078" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C078),15))
			,(wr, mod) -> wr.append("879" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C879),15))
			,(wr, mod) -> wr.append("880" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C880),15))
			,(wr, mod) -> wr.append("881" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C881),15))
			,(wr, mod) -> wr.append("882" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C882),15))
			,(wr, mod) -> wr.append("883" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C883),15))
			,(wr, mod) -> wr.append("884" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C884),15))
			,(wr, mod) -> wr.append("355" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C355),15))
			,(wr, mod) -> wr.append("356" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C356),15))
			,(wr, mod) -> wr.append("054" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C054),15))
			,(wr, mod) -> wr.append("055" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C055),15))
			,(wr, mod) -> wr.append("056" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C056),15))
			,(wr, mod) -> wr.append("857" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C857),15))
			,(wr, mod) -> wr.append("858" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C858),15))
			,(wr, mod) -> wr.append("859" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C859),15))
			,(wr, mod) -> wr.append("860" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C860),15))
			,(wr, mod) -> wr.append("861" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C861),15))
			,(wr, mod) -> wr.append("862" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C862),15))
			,(wr, mod) -> wr.append("087" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C087),15))
			,(wr, mod) -> wr.append("088" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C088),15))
			,(wr, mod) -> wr.append("089" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C089),15))
			,(wr, mod) -> wr.append("890" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C890),15))
			,(wr, mod) -> wr.append("891" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C891),15))
			,(wr, mod) -> wr.append("892" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C892),15))
			,(wr, mod) -> wr.append("893" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C893),15))
			,(wr, mod) -> wr.append("894" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C894),15))
			,(wr, mod) -> wr.append("895" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C895),15))
			,(wr, mod) -> wr.append("357" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C357),15))
			,(wr, mod) -> wr.append("358" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C358),15))
			,(wr, mod) -> wr.append("065" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C065),15))
			,(wr, mod) -> wr.append("066" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C066),15))
			,(wr, mod) -> wr.append("067" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C067),15))
			,(wr, mod) -> wr.append("868" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C868),15))
			,(wr, mod) -> wr.append("869" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C869),15))
			,(wr, mod) -> wr.append("870" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C870),15))
			,(wr, mod) -> wr.append("871" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C871),15))
			,(wr, mod) -> wr.append("872" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C872),15))
			,(wr, mod) -> wr.append("873" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C873),15))
			,(wr, mod) -> wr.append("098" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C098),15))
			,(wr, mod) -> wr.append("099" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C099),15))
			,(wr, mod) -> wr.append("100" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C100),15))
			,(wr, mod) -> wr.append("361" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C361),15))
			,(wr, mod) -> wr.append("362" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C362),15))
			,(wr, mod) -> wr.append("363" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C363),15))
			,(wr, mod) -> wr.append("364" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C364),15))
			,(wr, mod) -> wr.append("365" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C365),15))
			,(wr, mod) -> wr.append("366" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C366),15))
			,(wr, mod) -> wr.append("359" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C359),15))
			,(wr, mod) -> wr.append("360" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C360),15))
			,(wr, mod) -> wr.append("109" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C109),15))
			,(wr, mod) -> wr.append("110" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C110),15))
			,(wr, mod) -> wr.append("111" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C111),15))
			,(wr, mod) -> wr.append("112" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C112),15))
			,(wr, mod) -> wr.append("115" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C115),15))
			,(wr, mod) -> wr.append("113" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C113),15))
			,(wr, mod) -> wr.append("114" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C114),15))
			
			// Volumen de Operaciones, va con 5 decimales. Se pone todo el numero (incluido los decimales) en la parte entera
			,(wr, mod) -> wr.append("120" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C120),13,5)+"00" )
			,(wr, mod) -> wr.append("121" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C121),13,5)+"00" )
			,(wr, mod) -> wr.append("122" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C122),13,5)+"00" )
			,(wr, mod) -> wr.append("123" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C123),13,5)+"00" )
			,(wr, mod) -> wr.append("124" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C124),13,5)+"00" )

			// Cuotas atribuible / cuotas a compensar / Resultado autoliquidacion / recargos / total
			,(wr, mod) -> wr.append("125" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C125),15))
			,(wr, mod) -> wr.append("126" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C126),15))
			,(wr, mod) -> wr.append("127" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C127),15))
			,(wr, mod) -> wr.append("128" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C128),15))
			,(wr, mod) -> wr.append("129" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C129),15))
			,(wr, mod) -> wr.append("130" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C130),15))
			,(wr, mod) -> wr.append("131" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C131),15))
			,(wr, mod) -> wr.append("132" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C132),15))
			,(wr, mod) -> wr.append("133" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C133),15))
			,(wr, mod) -> wr.append("134" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C134),15))
			,(wr, mod) -> wr.append("135" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C135),15))
			,(wr, mod) -> wr.append("140" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C140),15))
			,(wr, mod) -> wr.append("141" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C141),15))
			,(wr, mod) -> wr.append("142" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C142),15))
			
			// Datos bancarios (CCC), Solo si es devolucion o es domiciliacion y está cumplimentada la cuenta bancaria			
			,(wr, mod) -> {
				if ((mod.getDeclarationType() == FiscalModelDeclarationType.BANK || mod.getDeclarationType() == FiscalModelDeclarationType.PAYBACK) &&
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

			// Prorratas
			,(wr, mod) -> wr.append("150" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C150),15))
			,(wr, mod) -> wr.append("151" + AonFiscalFileUtils.signedSpace(mod.getAmount(mod.getProrateKey()),15))
			,(wr, mod) -> wr.append("152" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C152),15))
			,(wr, mod) -> wr.append("250" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C250),15))
			,(wr, mod) -> wr.append("251" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C251),15))

			// Informacion adicional
			,(wr, mod) -> wr.append("153" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C153),15))
			,(wr, mod) -> wr.append("252" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C252),15))
			,(wr, mod) -> wr.append("154" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C154),15))			
			,(wr, mod) -> wr.append("155" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C155),15))			
			,(wr, mod) -> wr.append("156" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C156),15))			
			,(wr, mod) -> wr.append("157" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C157),15))			
			,(wr, mod) -> wr.append("158" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C158),15))			
			,(wr, mod) -> wr.append("210" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C210),15))			
			,(wr, mod) -> wr.append("211" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C211),15))			
			,(wr, mod) -> wr.append("212" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C212),15))
			,(wr, mod) -> wr.append("161" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C161),15))
			,(wr, mod) -> wr.append("162" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C162),15))
			,(wr, mod) -> wr.append("163" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C163),15))
			,(wr, mod) -> wr.append("167" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C167),15))


			// Resto de casillas hasta completar las 200 (total 39, ya que hasta ahora van 162)
			
			,(wr, mod) -> {
				for (int i=0;i<39;i++)
					wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,15));
			}
			
			// Libre a blancos X(34)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(34))
			// Fecha en la que se dicto el concurso de acreedores
			,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDateZero(mod.getDescription(Mod390Key.AR_C908)))			
			
			// Libre a blancos X(82) y fin de registro
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(58))
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			
	    })

/*		
		// **************************************************************** BIZKAIA		
		 BIZKAIA_2017_R01 ( mod -> (mod.isBizkaia() && mod.getYear() > 2016) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("R01")			
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 25))
		   ,(wr, mod) -> wr.append("C")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getTown(), 15))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getDay(new Date()),2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(df.format(new Date()),10))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getYear(new Date()),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDate(mod.getDescription(Mod303Key.BZ_C185_1)))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDate(mod.getDescription(Mod303Key.BZ_C185_2)))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_2017_RA3 ( mod -> (mod.isBizkaia() && mod.getYear() > 2016) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("RA3")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceBankAlias(),25))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),24))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_2017_R05 ( mod -> (mod.isBizkaia() && mod.getYear() > 2016) ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("R05")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getName()
					   					+ AonStringUtils.SPACE
					   					+ AonStringUtils.trimToEmpty(mod.getSurname()),40))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 33))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 6))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPhone(), 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 3))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 12))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 5))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 5))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactEmail(), 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
		
		,BIZKAIA_P00_2017 ( mod -> (mod.isBizkaia() && mod.getYear() >= 2017) ,new IPropertyFiller[] {
				
				(wr, mod) -> wr.append( mod.isComplementary() ? "P00TX0001"+AonFiscalFileUtils.text("S",40)+AonStringUtils.CR_LF : "")
			   ,(wr, mod) -> wr.append( mod.isEnrolledInDevolutionRegistry() ? "P00TX0002"+AonFiscalFileUtils.text("S",40)+AonStringUtils.CR_LF : "")			   
			   ,(wr, mod) -> wr.append("P00IM0003"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C003),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0004"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C004),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0005"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C005),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0006"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C006),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0007"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C007),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0008"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C008),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0009"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C009),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0010"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C010),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0011"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C011),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0012"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C012),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0013"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C013),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0014"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C014),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0015"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C015),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0016"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C016),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0017"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C017),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0018"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C018),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0019"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C019),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0020"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C020),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0021"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C021),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0022"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C022),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0023"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C023),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0024"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C024),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0025"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C025),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0026"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C026),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0027"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C027),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0028"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C028),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   // FALTA - [29] Regularización por aplicación porcentaje definitivo de prorrata (sólo en el mes 12)
			   // En el PDF que saca el programa de ayuda (tambien en la orden publicada), si que está esta casilla, pero en el documento disponible en la Web no lo está
			   // de cualquier forma en la plataforma de AON, aún no existe este campo
			   //,(wr, mod) -> wr.append("P00IM0029"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C029),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append("P00IM0030"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C030),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0031"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C031),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)			   
			   ,(wr, mod) -> wr.append("P00P30032"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getAmount(Mod303Key.BZ_C032))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)			   
			   ,(wr, mod) -> wr.append("P00IM0033"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C033),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0034"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C034),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0035"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C035),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0036"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C036),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append( mod.isWithoutActivity() ? "P00MR0037" + "X" + AonStringUtils.CR_LF : "" ) 
			   ,(wr, mod) -> wr.append("P00IM0038"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C038),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0039"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C039),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0040"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C040),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0041"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C041),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0042"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C042),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0043"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C043),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0045"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C045),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0046"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C046),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0047"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C047),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0050"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C050),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0051"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C051),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0052"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C052),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0053"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C053),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0054"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C054),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0055"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C055),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0056"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C056),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0057"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C057),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0058"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C058),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0059"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C059),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0060"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C060),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0061"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C061),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0062"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C062),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0063"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C063),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0064"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C064),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0065"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C065),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0066"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C066),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0067"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C067),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0068"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C068),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0069"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C069),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0070"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C070),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0071"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C071),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0072"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C072),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0073"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C073),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0074"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C074),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0075"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C075),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0076"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C076),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0077"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C077),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0078"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C078),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0079"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C079),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0080"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C080),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0081"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C081),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0082"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C082),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0083"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C083),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0084"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C084),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0085"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C085),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0086"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C086),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0087"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C087),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0088"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C088),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0089"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C089),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0090"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C090),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0091"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C091),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0092"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C092),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0093"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C093),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0094"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C094),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0095"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C095),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0096"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C096),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0097"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C097),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0098"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C098),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0099"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C099),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0100"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C100),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append( mod.getProratePercent() != 0.0 && mod.getProratePercent() != 100.0 ? "P00TX0101" + AonFiscalFileUtils.text("S",40) + AonStringUtils.CR_LF : "")  

			   // FALTA - [102] - Prorrata especial - Existe la clave, pero no aparece en el formulario en pantalla
			   //,(wr, mod) -> wr.append("P00TX0102"),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   
			   ,(wr, mod) -> wr.append("P00P30103"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getProratePercent())),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0104"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C104),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0105"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C105),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0106"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C106),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0107"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C107),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0108"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C108),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append( mod.getDescription(Mod303Key.BZ_C185_1) == null && mod.getDescription(Mod303Key.BZ_C185_2) == null ? "" : "P00TX0185" + AonFiscalFileUtils.text("S",40) + AonStringUtils.CR_LF )
			   ,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C186) == 1 ? "P00MR0186" + "X" + AonStringUtils.CR_LF : "")
			   ,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C187) == 1 ? "P00MR0187" + "X" + AonStringUtils.CR_LF : "")
			   
			   // FALTA - No estan los campos
			   // [188] - Opción por la aplicación de la prorrata especial
			   //,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C188) == 1 ? "P00MR0188" + "X" + AonStringUtils.CR_LF : "")
			   // [189] - Revocación de la opción por la aplicación de la prorrata especial
			   //,(wr, mod) -> wr.append( mod.getAmount(Mod303Key.BZ_C189) == 1 ? "P00MR0189" + "X" + AonStringUtils.CR_LF : "")

			   ,(wr, mod) -> wr.append("P00IM0200"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C200),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0201"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C201),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0202"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C202),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0203"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C203),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})		
		
		// Dejo este solo para el 2016, pues hay cosas que han cambiado para el 2017
		,BIZKAIA_P00_2016 ( mod -> (mod.isBizkaia() && mod.getYear() == 2016) ,new IPropertyFiller[] {
		    (wr, mod) -> wr.append("P00TX0001"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.isComplementary())),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces( 39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0002"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.CM_002))),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0003"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C003),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0004"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C004),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0005"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C005),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0006"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C006),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0007"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C007),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0008"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C008),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0009"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C009),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0010"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C010),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0011"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C011),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0012"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C012),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0013"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C013),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0014"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C014),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0015"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C015),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0016"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C016),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0017"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C017),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0018"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C018),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0019"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C019),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0020"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C020),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0021"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C021),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0022"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C022),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0023"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C023),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0024"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C024),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0025"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C025),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0026"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C026),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0027"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C027),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0028"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C028),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
//		   ,(wr, mod) -> wr.append("P00IM0029"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C029),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0030"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C030),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0031"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C031),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00P30103"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getAmount(Mod303Key.BZ_C032))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0033"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C033),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0034"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C034),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0035"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C035),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0036"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C036),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0187"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.isWithoutActivity() )),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0038"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C038),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0039"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C039),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0040"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C040),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0041"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C041),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0042"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C042),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0043"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C043),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0045"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C045),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0046"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C046),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0047"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C047),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0050"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C050),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0051"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C051),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0052"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C052),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0053"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C053),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0054"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C054),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0055"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C055),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0056"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C056),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0057"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C057),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0058"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C058),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0059"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C059),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0060"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C060),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0061"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C061),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0062"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C062),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0063"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C063),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0064"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C064),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0065"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C065),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0066"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C066),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0067"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C067),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0068"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C068),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0069"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C069),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0070"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C070),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0071"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C071),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0072"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C072),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0073"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C073),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0074"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C074),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0075"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C075),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0076"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C076),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0077"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C077),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0078"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C078),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0079"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C079),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0080"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C080),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0081"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C081),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0082"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C082),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0083"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C083),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0084"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C084),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0085"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C085),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0086"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C086),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0087"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C087),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0088"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C088),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0089"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C089),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0090"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C090),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0091"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C091),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0092"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C092),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0093"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C093),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0094"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C094),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0095"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C095),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0096"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C096),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0097"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C097),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0098"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C098),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0099"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C099),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0100"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C100),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0101"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getProratePercent() != 0.0 && mod.getProratePercent() != 100.0)),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0102"),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00P30103"),(wr, mod) -> wr.append(AonFiscalFileUtils.p3(mod.getProratePercent())),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0104"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C104),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0105"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C105),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0106"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C106),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0107"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C107),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0108"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C108),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0185"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getDescription(Mod303Key.BZ_C185_1) == null && mod.getDescription(Mod303Key.BZ_C185_2) == null)),(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(39)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0186"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C186))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00TX0187"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C187))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
//		   ,(wr, mod) -> wr.append("P00TX0188"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C188))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
//		   ,(wr, mod) -> wr.append("P00TX0189"),(wr, mod) -> wr.append(AonFiscalFileUtils.mark( mod.getAmount(Mod303Key.BZ_C189))),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0200"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C200),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0201"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C201),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0202"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C202),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		   ,(wr, mod) -> wr.append("P00IM0203"),(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod303Key.BZ_C203),16,2)),(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		
		
		// **************************************************************** GIPUZKOA		
		,GIPUZKOA_2015_R01 ( mod -> (mod.isGipuzkoa() && mod.getYear() >= 2015) ,new IPropertyFiller[] {
			
// 			Los campos de Importes con signo, serán: Signo (0 o -) + X enteros + 2 decimales, excepto
//			el Porcentaje de Gipuzkoa, que se compone de 3 posiciones enteras y 4 posiciones decimales			

			 (wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))      // Nif presentador AN9
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))      // Nif declarante AN9
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))   // Ejercicio N4
			,(wr, mod) -> wr.append(mod.getPeriod().isQuarterPeriod() ? "300" : "320") // Modelo 300 (si presentacion trimestral) o 320 (si presentacion mensual) AN3
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 2)) // Periodo AN2
			
			// FALTA - Que hay que poner ?? en los que genera el programa de ayuda, y en los que genera hasta ahora AON, siempre pone 01
			,(wr, mod) -> wr.append("01") // Código de registro N2
			
			,(wr, mod) -> wr.append((mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20)?AonFiscalFileUtils.text(mod.getFinanceCCC(),20):AonFiscalFileUtils.zeros(20)) // Código cuenta cliente N20
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),4))	                // Cuatro primeras posiciones del IBAN (ESXX) AN4
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(3)) 							            // Libre blancos AN3 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.isWithoutActivity()))                   // [01] Sin actividad AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.GP_A001)))          // [PRE] Autoliquidacion Concursal PRE AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.GP_A002)))          // [POST] Autoliquidacion Concursal POST AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C002),15))     //	[02] Base al 21 Signo+N14 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C003),14))     //	[03] Cuota al 21 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C004),15))     // [04] Base al 10 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C005),14))     // [05] Cuota al 10 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C006),15))     // [06] Base al 4 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C007),14))     // [07] Cuota al 4 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C039),15))     // [39] Modificación de base régimen general Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C040),14))     // [40] Modificación de cuota régimen general Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C008),15))     // [08] Base recargo al 5,2 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C009),14))     // [09] Cuota recargo al 5,2 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C010),15))     // [10] Base recargo al 1,4 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C011),14))     // [11] Cuota recargo al 1,4 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C012),15))     // [12] Base recargo al 0,5 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C013),14))     // [13] Cuota recargo al 0,5 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C041),15))     // [41] Modificación de base recargo equivalencia Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C042),14))     // [42] Modificación de cuota recargo equivalencia Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C014),15))     // [14] Base adquisiciones intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C015),14))     // [15] Cuota adquisiciones intracomunitarias Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C043),15))     // [43] Base otras operaciones inversión sujeto pasivo (compras) Signo+N14	
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C044),14))     // [44] Cuota otras operaciones inversión sujeto pasivo (compras) Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C016),15))     // [16] Total cuota devengada Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C017),15))     // [17] Base Iva deducible operaciones interiores Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C018),14))     // [18] Cuota Iva deducible operaciones interiores Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C019),15))     // [19] Base Iva deducible importaciones Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C020),14))     // [20] Cuota Iva deducible importaciones Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C021),15))     // [21] Base Iva deducible adquisiciones intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C022),14))     // [22] Cuota Iva deducible adquisiciones intracomunitarias Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C045),15))     // [45] Rectificación de deducciones. Base. Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C046),14))     // [46] Rectificación de deducciones. Cuota. Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C023),14))     // [23] Cuota compensaciones Reg A.G. y P. Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C024),14))     // [24] Cuota Iva deducible Regularización Inversiones Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C025),14))     // [25] Cuota total a deducir Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C026),14))     // [26] Diferencia Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C027),7,4))  // [27] Porcentaje Gipuzkoa N7 (3 enteros + 4 decimales)
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C028),12))     // [28] Atribuible T. H. Gipuzkoa Signo+N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C029),11))   // [29] Cuotas a compensar de periodos anteriores en T.H. Gipuzkoa N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C030),15))     // [30] Entregas intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C031),15))     // [31] Exportaciones y operaciones asimiladas Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C032),15))     // [32] Op. No sujetas o con inversión del sujeto pasivo Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C047),15))     // [47] Base imponible Entregas criterio de caja Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C048),14))     // [48] Cuota Entregas criterio de caja Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C049),15))     // [49] Base imponible Adquisiciones criterio de caja Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C050),14))     // [50] Cuota Adquisiciones criterio de caja Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C035),12))     // [35] Resultado Signo+N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)>=0?mod.getAmount(Mod303Key.GP_C035):0.0,11))  // [36] A ingresar N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)<0 && mod.getDeclarationType() == FiscalModelDeclarationType.COMPENSATE?mod.getAmount(Mod303Key.GP_C035)*(-1):0.0,11))  // [37] A compensar N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)<0 && mod.getDeclarationType() == FiscalModelDeclarationType.PAYBACK?mod.getAmount(Mod303Key.GP_C035)*(-1):0.0,11))     // [38] A devolver N11
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
*/
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod390File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod390HF mod390) {
			return accepter.accept(mod390);
		}
		private void fillPage(Mod390HF mod390, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod390);
			}
		}
	}

	public static void fillWriter(Mod390HF mod390, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod390File format : Mod390File.values()) {
			if (format.accept(mod390)) {
				format.fillPage(mod390, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
