package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod390HFWriter.IMod390HFWriter;
import com.esferalia.aon.occam.server.fiscal.format.Mod390HFWriter.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod390HFWriter.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFWriterARABA2021 implements IMod390HFWriter {

	private static enum Mod390HFFile {
		ARABA_2019_RC ( mod -> true ,new IPropertyFiller[] {
				
				 // Habría que ver si aqui se pueden poner siempre los datos del contribuyente, o cuando
				 // la presentación la hace una asesoría o profesional, necesariamente deben ir los datos del profesional
				 // El programa de ayuda lo carga bien, pongas lo que pongas, así que por ahora, se ponen los mismo datos del contribuyente
				 (wr, mod) -> wr.append("C") // Tipo de registro = C
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9)
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),50)) // Apellidos y nombre o razón social del profesional X(50)
				,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40))                 // Libre a blancos X(40)
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(1,6,0))            // Número de declaraciones presentadas 9(6) (siempre se presenta 1 declaración) 
				,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(4894))               // Libre a blancos X(1994)
				,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
			// ARABA - Registro Detalle
			,ARABA_2019_RD ( mod -> true ,new IPropertyFiller[] {
	     		 (wr, mod) -> wr.append("D")                                           // Tipo de registro = D
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del profesional X(9) (El mismo que hayamos puesto en el registro de cabecera) 			
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))  // NIF del declarante X(9)
				
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getStartMonth()+1,2,0)) // Período inicial (AAAAMM) 9(6)
				
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getPeriod().getDueMonth()+1,2,0))   // Período final (AAAAMM) 9(6)
				
				,(wr, mod) -> wr.append("390")                                         		// Modelo de la declaración (390)
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getDeclarationResult(),18)) // Resultado de la declaración: Signo (blanco ó N) + Resultado 9(12) V99
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
				
				,(wr, mod) -> wr.append("145" + AonFiscalFileUtils.signedSpace((mod.isWithoutActivity()?1.0:0.0),18)) // [145] Sin actividad
				,(wr, mod) -> wr.append("901" + AonFiscalFileUtils.signedSpace((mod.isReplacement()?1.0:0.0),18))     // [901] Sustitutiva
					
				// En el numero anterior hay que poner año + numero anterior, se supone que en el programa 
				// ya se introducira el año primero y despues el numero, todo junto			
				,(wr, mod) -> wr.append(mod.isReplacement() 
						? "902"+AonStringUtils.SPACE+AonFiscalFileUtils.unsigned(mod.getReplacedNumber(),17,2) 
						: "000"+AonStringUtils.SPACE+AonFiscalFileUtils.zeros(17)) 
				// [902] Sustitutiva Numero declaracion anterior (año y numero) - solo si se ha presentado por internet
					
				,(wr, mod) -> wr.append("907" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C907),18))  // [907] Ha sido declarado en concurso de acreedores en el presente período de liquidación
				,(wr, mod) -> wr.append("918" + AonFiscalFileUtils.signedSpace((mod.isEnrolledInDevolutionRegistry()?1.0:0.0),18)) // [92] Está inscrito en el Registro de devolución mensual
				,(wr, mod) -> wr.append("910" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C910),18))  // [910] Ha optado por el Régimen especial del criterio de caja 
				,(wr, mod) -> wr.append("911" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C911),18))  // [911] Es destinatario de operaciones a las que se aplique el Régimen especial del criterio de caja
				// La casilla 908 va al final en las posiciones 2011 a 2018
				,(wr, mod) -> wr.append("909" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C909),18))  // [909] Si se ha dictado auto de declaración de concurso en este periodo indique el tipo de autoliquidación indicar si es Preconcursal o Postconcursal
					
				// IVA Devengado
				,(wr, mod) -> wr.append("001" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C001),18))  
				,(wr, mod) -> wr.append("002" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C002),18))
				,(wr, mod) -> wr.append("003" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C003),18))
				,(wr, mod) -> wr.append("804" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C804),18))
				,(wr, mod) -> wr.append("805" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C805),18))
				,(wr, mod) -> wr.append("806" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C806),18))
				,(wr, mod) -> wr.append("807" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C807),18))
				,(wr, mod) -> wr.append("808" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C808),18))
				,(wr, mod) -> wr.append("809" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C809),18))
				,(wr, mod) -> wr.append("351" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C351),18))
				,(wr, mod) -> wr.append("352" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C352),18))
				,(wr, mod) -> wr.append("019" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C019),18))
				,(wr, mod) -> wr.append("020" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C020),18))
				,(wr, mod) -> wr.append("223" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C223),18))
				,(wr, mod) -> wr.append("224" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C224),18))
				,(wr, mod) -> wr.append("025" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C025),18))
				,(wr, mod) -> wr.append("026" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C026),18))
				,(wr, mod) -> wr.append("027" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C027),18))
				,(wr, mod) -> wr.append("034" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C034),18))
				,(wr, mod) -> wr.append("035" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C035),18))
				,(wr, mod) -> wr.append("036" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C036),18))
				,(wr, mod) -> wr.append("828" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C828),18))
				,(wr, mod) -> wr.append("829" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C829),18))
				,(wr, mod) -> wr.append("830" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C830),18))
				,(wr, mod) -> wr.append("831" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C831),18))
				,(wr, mod) -> wr.append("832" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C832),18))
				,(wr, mod) -> wr.append("833" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C833),18))
				,(wr, mod) -> wr.append("037" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C037),18))
				,(wr, mod) -> wr.append("038" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C038),18))
				,(wr, mod) -> wr.append("239" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C239),18))
				,(wr, mod) -> wr.append("240" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C240),18))
				,(wr, mod) -> wr.append("010" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C010),18))
				,(wr, mod) -> wr.append("011" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C011),18))
				,(wr, mod) -> wr.append("012" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C012),18))
				,(wr, mod) -> wr.append("813" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C813),18))
				,(wr, mod) -> wr.append("814" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C814),18))
				,(wr, mod) -> wr.append("815" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C815),18))
				,(wr, mod) -> wr.append("816" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C816),18))
				,(wr, mod) -> wr.append("817" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C817),18))
				,(wr, mod) -> wr.append("818" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C818),18))
				,(wr, mod) -> wr.append("353" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C353),18))
				,(wr, mod) -> wr.append("354" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C354),18))
				,(wr, mod) -> wr.append("041" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C041),18))
				,(wr, mod) -> wr.append("042" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C042),18))
					
				// Deducciones
				,(wr, mod) -> wr.append("043" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C043),18))
				,(wr, mod) -> wr.append("044" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C044),18))
				,(wr, mod) -> wr.append("045" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C045),18))
				,(wr, mod) -> wr.append("846" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C846),18))
				,(wr, mod) -> wr.append("847" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C847),18))
				,(wr, mod) -> wr.append("848" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C848),18))
				,(wr, mod) -> wr.append("849" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C849),18))
				,(wr, mod) -> wr.append("850" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C850),18))
				,(wr, mod) -> wr.append("851" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C851),18))
				,(wr, mod) -> wr.append("076" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C076),18))
				,(wr, mod) -> wr.append("077" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C077),18))
				,(wr, mod) -> wr.append("078" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C078),18))
				,(wr, mod) -> wr.append("879" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C879),18))
				,(wr, mod) -> wr.append("880" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C880),18))
				,(wr, mod) -> wr.append("881" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C881),18))
				,(wr, mod) -> wr.append("882" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C882),18))
				,(wr, mod) -> wr.append("883" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C883),18))
				,(wr, mod) -> wr.append("884" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C884),18))
				,(wr, mod) -> wr.append("355" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C355),18))
				,(wr, mod) -> wr.append("356" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C356),18))
				,(wr, mod) -> wr.append("054" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C054),18))
				,(wr, mod) -> wr.append("055" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C055),18))
				,(wr, mod) -> wr.append("056" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C056),18))
				,(wr, mod) -> wr.append("857" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C857),18))
				,(wr, mod) -> wr.append("858" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C858),18))
				,(wr, mod) -> wr.append("859" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C859),18))
				,(wr, mod) -> wr.append("860" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C860),18))
				,(wr, mod) -> wr.append("861" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C861),18))
				,(wr, mod) -> wr.append("862" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C862),18))
				,(wr, mod) -> wr.append("087" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C087),18))
				,(wr, mod) -> wr.append("088" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C088),18))
				,(wr, mod) -> wr.append("089" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C089),18))
				,(wr, mod) -> wr.append("890" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C890),18))
				,(wr, mod) -> wr.append("891" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C891),18))
				,(wr, mod) -> wr.append("892" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C892),18))
				,(wr, mod) -> wr.append("893" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C893),18))
				,(wr, mod) -> wr.append("894" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C894),18))
				,(wr, mod) -> wr.append("895" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C895),18))
				,(wr, mod) -> wr.append("357" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C357),18))
				,(wr, mod) -> wr.append("358" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C358),18))
				,(wr, mod) -> wr.append("065" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C065),18))
				,(wr, mod) -> wr.append("066" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C066),18))
				,(wr, mod) -> wr.append("067" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C067),18))
				,(wr, mod) -> wr.append("868" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C868),18))
				,(wr, mod) -> wr.append("869" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C869),18))
				,(wr, mod) -> wr.append("870" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C870),18))
				,(wr, mod) -> wr.append("871" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C871),18))
				,(wr, mod) -> wr.append("872" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C872),18))
				,(wr, mod) -> wr.append("873" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C873),18))
				,(wr, mod) -> wr.append("098" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C098),18))
				,(wr, mod) -> wr.append("099" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C099),18))
				,(wr, mod) -> wr.append("100" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C100),18))
				,(wr, mod) -> wr.append("361" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C361),18))
				,(wr, mod) -> wr.append("362" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C362),18))
				,(wr, mod) -> wr.append("363" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C363),18))
				,(wr, mod) -> wr.append("364" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C364),18))
				,(wr, mod) -> wr.append("365" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C365),18))
				,(wr, mod) -> wr.append("366" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C366),18))
				,(wr, mod) -> wr.append("359" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C359),18))
				,(wr, mod) -> wr.append("360" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C360),18))
				,(wr, mod) -> wr.append("109" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C109),18))
				,(wr, mod) -> wr.append("110" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C110),18))
				,(wr, mod) -> wr.append("111" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C111),18))
				,(wr, mod) -> wr.append("112" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C112),18))
				,(wr, mod) -> wr.append("115" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C115),18))
				,(wr, mod) -> wr.append("113" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C113),18))
				,(wr, mod) -> wr.append("114" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C114),18))
					
				// Volumen de Operaciones, va con 5 decimales. Se pone todo el numero (incluido los decimales) en la parte entera
				,(wr, mod) -> wr.append("120" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C120),16,5)+"00" )
				,(wr, mod) -> wr.append("121" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C121),16,5)+"00" )
				,(wr, mod) -> wr.append("122" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C122),16,5)+"00" )
				,(wr, mod) -> wr.append("123" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C123),16,5)+"00" )
				,(wr, mod) -> wr.append("124" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C124),16,5)+"00" )

				// Cuotas atribuible / cuotas a compensar / Resultado autoliquidacion / recargos / total
				,(wr, mod) -> wr.append("125" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C125),18))
				,(wr, mod) -> wr.append("126" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C126),18))
				,(wr, mod) -> wr.append("127" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C127),18))
				,(wr, mod) -> wr.append("128" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C128),18))
				,(wr, mod) -> wr.append("129" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C129),18))
				,(wr, mod) -> wr.append("130" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C130),18))
				,(wr, mod) -> wr.append("131" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C131),18))
				,(wr, mod) -> wr.append("132" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C132),18))
				,(wr, mod) -> wr.append("133" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C133),18))
				,(wr, mod) -> wr.append("134" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C134),18))
				,(wr, mod) -> wr.append("135" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C135),18))
				,(wr, mod) -> wr.append("140" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C140),18))
				,(wr, mod) -> wr.append("141" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C141),18))
				,(wr, mod) -> wr.append("142" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C142),18))
					
				// Datos bancarios (CCC), Solo si es devolucion o es domiciliacion y está cumplimentada la cuenta bancaria			
				,(wr, mod) -> {
					if ((mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK || mod.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK) &&
						(mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20)) {
							wr.append("301" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 0, 4),17,2));
							wr.append("302" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 4, 8),17,2));
							wr.append("303" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring( 8,10),17,2));
							wr.append("304" + AonStringUtils.SPACE + AonFiscalFileUtils.unsigned(mod.getFinanceCCC().substring(10,20),17,2));						
					} else {
						for (int i=1;i<=4;i++)
							wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,18));				
					}
				}

				// Prorratas
				,(wr, mod) -> wr.append("150" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C150),18))
				,(wr, mod) -> wr.append("151" + AonFiscalFileUtils.signedSpace(mod.getAmount(mod.getProrateKey()),18))
				,(wr, mod) -> wr.append("152" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C152),18))
				,(wr, mod) -> wr.append("250" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C250),18))
				,(wr, mod) -> wr.append("251" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C251),18))

				// Informacion adicional
				,(wr, mod) -> wr.append("153" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C153),18))
				,(wr, mod) -> wr.append("252" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C252),18))
				,(wr, mod) -> wr.append("156" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C156),18))			
				,(wr, mod) -> wr.append("157" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C157),18))			
				,(wr, mod) -> wr.append("158" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C158),18))
				,(wr, mod) -> wr.append("214" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C214),18))
				,(wr, mod) -> wr.append("215" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C215),18))
				,(wr, mod) -> wr.append("216" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C216),18))
				,(wr, mod) -> wr.append("217" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C217),18))
				,(wr, mod) -> wr.append("218" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C218),18))
				,(wr, mod) -> wr.append("219" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C219),18))
				,(wr, mod) -> wr.append("154" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C154),18))
				,(wr, mod) -> wr.append("155" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C155),18))
				,(wr, mod) -> wr.append("220" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C220),18))
				,(wr, mod) -> wr.append("221" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C221),18))
				,(wr, mod) -> wr.append("212" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C212),18))
				,(wr, mod) -> wr.append("161" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C161),18))
				,(wr, mod) -> wr.append("162" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C162),18))
				,(wr, mod) -> wr.append("163" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C163),18))
				,(wr, mod) -> wr.append("167" + AonFiscalFileUtils.signedSpace(mod.getAmount(Mod390Key.AR_C167),18))
				
				// Resto de casillas hasta completar las 200 (total 31, ya que hasta ahora van 170)
				
				,(wr, mod) -> {
					for (int i=0;i<33;i++)
						wr.append("000" + AonFiscalFileUtils.signedSpace(0.0,18));
				}
					
				// Fecha en la que se dicto el concurso de acreedores
				,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDateZero(mod.getDescription(Mod390Key.AR_C908)))			
					
				// Libre a blancos X(82) y fin de registro
				,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(489))
				,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
					
		    })
		
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod390HFFile(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod390HF mod390HF) {
			return accepter.accept(mod390HF);
		}
		private void fillPage(Mod390HF mod390HF, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod390HF);
			}
		}
	}

	public void fillWriter(Mod390HF mod390HF, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod390HFFile format : Mod390HFFile.values()) {
			if (format.accept(mod390HF)) {
				format.fillPage(mod390HF, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
