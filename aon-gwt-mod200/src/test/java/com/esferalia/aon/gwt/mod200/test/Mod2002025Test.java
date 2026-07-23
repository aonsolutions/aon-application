package com.esferalia.aon.gwt.mod200.test;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.esferalia.aon.gwt.mod200.server.Model200AdmonUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.MinorEntity;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.Secretary;
import com.esferalia.aon.occam.mod200.api.model.TitularReal;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Character;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025KeyDC;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2025.Mod2002025DAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002025Test {
	
	private static final double amount = 55000.0;
	private static double utePercent = 0.0; 

	private static Mod2002025 createMod200base(Mod2002025Key... characterKeys) {
		
		System.out.println("Caracteres de la declaración: " + Arrays.toString(characterKeys));
		
		Mod2002025 mod200 = new Mod2002025();
//		mod200.setDomain(9253);
		mod200.setYear(2025);
		mod200.setStatus(FiscalStatus.PENDING);
		mod200.setPeriodType(1);
		mod200.setPeriodStart(AonDateUtils.getYearFirstDay(mod200.getYear()));
		mod200.setPeriodEnd(AonDateUtils.getYearLastDay(mod200.getYear()));
		mod200.setBalanceType(BalanceType.ABREVIADO);
		mod200.setEcpnType(EcpnType.NO_CONSTA);
		mod200.setPygType(BalanceType.ABREVIADO);
//		mod200.setEnterprise(conf.getCompany().getId());
		mod200.setDocument("B50111111");
		mod200.setName("EMPRESA DE PRUEBA, S.L.");
//		mod200.setEnterprisePhone1(conf.fiscal().getContactPhone());
//		mod200.setEnterprisePhone2(conf.fiscal().getContactCellular());
		mod200.setAdministration(Administration.COMMON_TERRITORY);
		mod200.setInitializedFromLastYear(false);
		mod200.setCnae("0111");
		
		// Secretario
		Secretary secretary = new Secretary();
		secretary.setDocument("12345678Z");
		secretary.setName("SECRETARIO SECRETARIO, PRUEBA");
		mod200.setSecretary(secretary);
		
		// Representantes legales de la entidad
		mod200.getRepresentatives().add(new LegalRepresentative()
											 .setDocument("12345678Z")
											 .setName("REPRESENTANTE LEGAL, PRUEBA")
											 .setNotary("NOTARIA UNO")
											 .setNotaryDate(AonDateUtils.getDate(2020, 1, 1)));
		
		// A. Relación de administradores
		mod200.getAdministrators().add(new Mod200CompanyAdministrator()
										    .setDocument("99999018D")
											.setName("FICTICIO ACTIVO, CIUDADANO")
											.setProvince(50)); // Necesario si carácter 21 marcado
		
		// B2. Participaciones personas o entidades en la declarante a la fecha de cierre del período declarado
		mod200.getParticipationsIn().add(new Mod200CompanyParticipation()
											  .setDocument("12345678Z")
											  .setName("PARTICIPACION, PRUEBA")
											  .setNotary("F")
											  .setProvince(50)
											  .setPercent(100.0)
											  .setNominalValue(3000.0));
		
		// F. Identificación del titular real de la entidad
		mod200.getTitularReal().add(new TitularReal()
										 .setDocumentType(1)
										 .setDocument("12345678Z")
										 .setName("TITULAR REAL, PRUEBA")
										 .setDocumentCountry("ES")
										 .setBirthDate(AonDateUtils.getDate(1980, 1, 1))
										 .setResidenceCountry("ES")
										 .setNationality("ES"));
		
		// Inicializar todas las casillas del modelo 200
		for (Mod2002025Key key : Mod2002025Key.values()) {
			mod200.setDoubleValue(key, 0.0); 
		}
		for (Mod2002025KeyDC key : Mod2002025KeyDC.values()) {
			mod200.setDoubleValue(key, 0.0);
		}	
		
		// Casillas estados contables (Balance, PYG y ECPN)
		mod200.setBooleanValue(Mod2002025Key.C0050, (mod200.getBalanceType() == BalanceType.NORMAL));
		mod200.setBooleanValue(Mod2002025Key.C0051, (mod200.getBalanceType() == BalanceType.ABREVIADO));
		mod200.setBooleanValue(Mod2002025Key.C0052, (mod200.getBalanceType() == BalanceType.PYMES));
		
		mod200.setBooleanValue(Mod2002025Key.C0053, (mod200.getPygType() == BalanceType.NORMAL));
		mod200.setBooleanValue(Mod2002025Key.C0054, (mod200.getPygType() == BalanceType.ABREVIADO));
		mod200.setBooleanValue(Mod2002025Key.C0055, (mod200.getPygType() == BalanceType.PYMES));
		
		mod200.setBooleanValue(Mod2002025Key.C0075, (mod200.getEcpnType() == EcpnType.NORMAL));
		mod200.setBooleanValue(Mod2002025Key.C0076, (mod200.getEcpnType() == EcpnType.ABREVIADO));
		mod200.setBooleanValue(Mod2002025Key.C0077, (mod200.getEcpnType() == EcpnType.PYMES));
			
		//  Importe	neto de la cifra de negocios (casilla VOLOPE)
		mod200.setDoubleValue(Mod2002025Key.VOLOPE, 1.0); // 1 - Inferior a 20 millones de euros
		
		// Importes en balance (BA177, BP199), pyg (PG255) y aplicación de resultados otras reservas (ID1522)
		mod200.setDoubleValue(Mod2002025Key.BA177, amount);
		mod200.setDoubleValue(Mod2002025Key.BP199, amount);
		mod200.setDoubleValue(Mod2002025Key.PG255, amount);
		mod200.setDoubleValue(Mod2002025Key.ID1522, amount);
		
		// Marcar caracteres de la declaración
		check(mod200, characterKeys);
		
		// Si la combinación de caracteres contiene el caracter 78, hay que crear al menos una entidad menor
		if (mod200.isChecked(Mod2002025Key.C0078)) {
			mod200.getMinorEntities().add(new MinorEntity()
											  .setDocument("A50111111")
											  .setName("ENTIDAD MENOR PRUEBA"));
		}
		
		// Cuando el declarante sea una AIE o UTE (caracteres 00013, 00014 y 00085) se debe indicar la relación de participes y el porcentaje de la casilla 060
		if (mod200.isChecked(Mod2002025Key.C0013) || mod200.isChecked(Mod2002025Key.C0085)) {
			mod200.setDoubleValue(Mod2002025Key.UT060, utePercent);
			if (mod200.getDoubleValue(Mod2002025Key.UT060) > 0.0) {
				mod200.getUteParticipations().add(new UteParticipation()
													  .setDocument("99999018D")
													  .setName("FICTICIO ACTIVO, CIUDADANO")
													  .setProvince(50)
													  .setPercent(utePercent));
			}
		}
		
		if (mod200.isChecked(Mod2002025Key.C0014)) {
			mod200.getUteParticipations().add(new UteParticipation()
												  .setDocument("99999018D")
												  .setName("FICTICIO ACTIVO, CIUDADANO")
												  .setProvince(50)
												  .setPercent(100));
		}
		
		return mod200;
		
	}
	
	private static void check(Mod2002025 mod200, Mod2002025Key... keys) {
		for (Mod2002025Key key : keys) {
			mod200.setBooleanValue(key, true);
		}
	}
	
	private static void calculate(Mod2002025 mod200) {
		
		mod200.getDraftMap().clear();
		
		// Calcular el modelo
//		System.out.println("Calculando modelo 200...");
		Mod2002025DAO.calculate(mod200);
		
		// Pasar los valores modificados en draftMap a keysMap
		for (IMod200Key key : mod200.getDraftMap().keySet()) {
			DoubleVariableEx dv = mod200.getDraftMap().get(key);
			if (dv != null && dv.getValue() != 0.0)
				mod200.setDoubleValue(key, dv.getValue());
		}
		
	}
	
	private static boolean validate(Mod2002025 mod200, boolean showPdf) {
		
		// Calcular el modelo		
		System.out.println("Calculando modelo 200...");
		calculate(mod200);
		
		// SOCIMIS caracter 12, 64 o 57 marcado, hay que rellenar tambien las casillas 550TG y 550T0
		if (mod200.isChecked(Mod2002025Key.C0012) || mod200.isChecked(Mod2002025Key.C0064) || mod200.isChecked(Mod2002025Key.C0057)) {
			// Rellenar las casillas 550TG y 550T0 a partir de la casilla 550, repartiendolo al 70% y 30% entre ambas
			double casilla550 = mod200.getDoubleValue(Mod2002025Key.LQ550);
			double casilla550TG = AonMathUtils.round(casilla550 * 0.70);
			double casilla550T0 = casilla550 - casilla550TG;
			mod200.setDoubleValue(Mod2002025Key.LQ550TG, casilla550TG);
			mod200.setDoubleValue(Mod2002025Key.LQ550T0, casilla550T0);
			// Volver a calcular el modelo para que se recalculen el resto de casillas
			calculate(mod200);
		}
		
		// Cooperativas, caracteres 17, 18 o 19 marcadas, hay que rellenar las casillas 2827 y 2828 (resultados cooperativos y extracooperativos) con el importe de la casilla 1330
		if (mod200.isChecked(Mod2002025Key.C0017) || mod200.isChecked(Mod2002025Key.C0018) || mod200.isChecked(Mod2002025Key.C0019)) {
			// Rellenar las casillas CP2827 y CP2828 a partir de la casilla LQ1330, repartiendolo al 70% y 30% entre ambas
			double casilla1330 = mod200.getDoubleValue(Mod2002025Key.LQ1330);
			double casilla2827 = AonMathUtils.round(casilla1330 * 0.70);
			double casilla2828 = casilla1330 - casilla2827;
			mod200.setDoubleValue(Mod2002025Key.CP2827, casilla2827);
			mod200.setDoubleValue(Mod2002025Key.CP2828, casilla2828);
			// Tambien el tipo de gravamen (casilla LQ558) de los tipos posibles, por ejemplo el 20%, si le corresponde otro ya lo pondrá el recalculo del modelo
			mod200.setDoubleValue(Mod2002025Key.LQ558, mod200.isChecked(Mod2002025Key.C0046) ? 25.0 : 20.0);
			// Si está marcado el caracter 57 las casillas 550TG y 550T0 se reparten al 80% y 20% en lugar de 70% y 30%
			if (mod200.isChecked(Mod2002025Key.C0057)) {
				double casilla550 = mod200.getDoubleValue(Mod2002025Key.LQ550);
				double casilla550TG = AonMathUtils.round(casilla550 * 0.80);
				double casilla550T0 = casilla550 - casilla550TG;
				mod200.setDoubleValue(Mod2002025Key.LQ550TG, casilla550TG);
				mod200.setDoubleValue(Mod2002025Key.LQ550T0, casilla550T0);
			}
			// Volver a calcular el modelo para que se recalculen el resto de casillas
			calculate(mod200);
		}
		
		// Claves 13 o 85 marcadas, hay que rellenar las casillas 555 y 556 con el importe de la casilla 1330
		// 00013 Agrupación de interés económico española o unión temporal de empresas
		// 00085 Unión temporal de empresas
		// Clave 00060 = 100 entonces 00555 = 01330
		// Clave 00060 = 0 entonces 00556 = 01330
		// Clave 00060 > 0 y Clave 00060 < 100 entonces Casilla 01330 = Casilla 00555 + Casilla 00556
		if (mod200.isChecked(Mod2002025Key.C0013) || mod200.isChecked(Mod2002025Key.C0085)) {
			double casilla1330 = mod200.getDoubleValue(Mod2002025Key.LQ1330);
			double casilla060 =  mod200.getDoubleValue(Mod2002025Key.UT060);
			double casilla555 = 0.0;
			double casilla556 = 0.0;
			if (casilla060 == 100.0) {
				casilla555 =casilla1330;
			} else if (casilla060 == 0.0) {
				casilla556 = casilla1330;
			} else {
				casilla555 = AonMathUtils.round(casilla1330 * casilla060 / 100.0);
				casilla556 = casilla1330 - casilla555;
			}
			mod200.setDoubleValue(Mod2002025Key.LQ555, casilla555);
			mod200.setDoubleValue(Mod2002025Key.LQ556, casilla556);
			// Volver a calcular el modelo para que se recalculen el resto de casillas
			calculate(mod200);
		}
		
		// Mostrar porcentaje de participación de la UTE (casilla 060) e importe usado en el calculo
		System.out.println("Importe usado en el cálculo     : " + amount);
		System.out.println("Porcentaje de participación UTE : " + mod200.getDoubleValue(Mod2002025Key.UT060));
		
		// Mostrar tipo de gravamen calculado (casilla LQ558)
		System.out.println("Tipo de gravamen calculado [LQ558]: " + mod200.getDoubleValue(Mod2002025Key.LQ558));

		// Enviar el modelo 200 al servicio de validación de la AEAT
		System.out.println("Enviando modelo 200 al servicio de validación de la AEAT...");
		AEATParams aeatParams = new AEATParams();
		Model200AdmonUtils.serValiDos(null, aeatParams, mod200);
		
		// Mostrar PDF si la validación es correcta, o los errores en caso contrario
		boolean result = true;
		if (aeatParams.getErrores() == null || aeatParams.getErrores().isEmpty()) {			
			System.out.println("No se han encontrado errores.");
			// Guardar el PDF en un archivo y mostrarlo
			if (showPdf) {
				if (savePdf(aeatParams)) {
					showPdf(aeatParams.getNrc());
				}
			}
		} else {
			result = false;
			System.err.println("Se han encontrado los siguientes errores:");
			for (String error : aeatParams.getErrores()) {
				System.err.println(new String(error.getBytes(), StandardCharsets.UTF_8));
			}
		}
		try {
			Thread.sleep(5000); // Delay de 5 segundos
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		System.out.println("----------------------------------------------------------------------------------------------------");
		return result;
		
	}

	private static boolean savePdf(AEATParams aeatParams) {
		// La propiedad nrc de aeatParams contiene el PDF en base64, hay que decodificarlo, guardarlo en un archivo y mostrarlo
		byte[] pdfBytes = Base64.getDecoder().decode(aeatParams.getNrc());
		// Guardar el PDF en un archivo
		String fileName = "c:\\tmp\\modelo200prueba.pdf";
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(pdfBytes);
			System.out.println("PDF guardado correctamente.");
			aeatParams.setNrc(fileName);
			return true;
		} catch (IOException e) {
			System.err.println("Error al guardar el PDF: " + e.getMessage());
		}
		return false;
	}
	
	private static void showPdf(String fileName) {
		// Abrir el PDF con el visor de PDF predeterminado del sistema operativo
		try {
			File pdfFile = new File(fileName);
			if (pdfFile.exists()) {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(pdfFile);
				} else {
					System.err.println("No se puede abrir el PDF, el escritorio no es compatible.");
				}
			} else {
				System.err.println("No se puede abrir el PDF, el archivo no existe.");
			}
		} catch (IOException e) {
			System.err.println("Error al abrir el PDF: " + e.getMessage());
		}
	}

    public static List<List<Mod2002025Key>> generarCombinaciones(Mod2002025Key[] arr) {
        List<List<Mod2002025Key>> resultado = new ArrayList<>();
        int n = arr.length;
        
        // 1 << n es equivalente a 2 elevado a la potencia n (2^12 = 4096)
        int totalCombinaciones = 1 << n; 

        // Iterar desde 0 hasta 4095
        for (int i = 0; i < totalCombinaciones; i++) {
            List<Mod2002025Key> combinacionActual = new ArrayList<>();
            
            // Revisar qué bits están encendidos en el número 'i'
            for (int j = 0; j < n; j++) {
                // Si el bit j está encendido en 'i', incluimos el elemento j
                if ((i & (1 << j)) != 0) {
                    combinacionActual.add(arr[j]);
                }
            }
            resultado.add(combinacionActual);
        }
        return resultado;
    }
    
	private static boolean pruebaSimple(boolean showPdf, Mod2002025Key... characterKeys) {
		
		Mod2002025 mod200 = createMod200base(characterKeys);
		return validate(mod200, showPdf);
		
	}
    
    // Prueba todas las combinaciones de los caracteres que se le pasan, si la combinación es compatible
	private static void pruebaTodas(Mod2002025Key onlyKey, Mod2002025Key... characterKeys) {
		
		// Generar todas las combinaciones
        List<List<Mod2002025Key>> todasLasCombinaciones = generarCombinaciones(characterKeys);
        
        // Para todas las combinaciones generadas, llamar a la funcion prueba si la combinación es compatible, es decir, si no hay caracteres incompatibles en la combinación
        int contador = 0;
        int erroneas = 0;
        for (List<Mod2002025Key> combinacion : todasLasCombinaciones) {
			boolean compatible = true;
			// La combinación vacia se ignora
			if (combinacion.isEmpty()) 
				continue;
			// Si la combinación contiene la clave 15 o 79, también debe marcarse la clave 29
			if (combinacion.contains(Mod2002025Key.C0015) || combinacion.contains(Mod2002025Key.C0079)) {
				if (!combinacion.contains(Mod2002025Key.C0029)) {
					combinacion.add(Mod2002025Key.C0029);
				}
			}
			// Si la combinación contiene la clave 64, también debe marcarse la clave 12
			if (combinacion.contains(Mod2002025Key.C0064)) {
				if (!combinacion.contains(Mod2002025Key.C0012)) {
					combinacion.add(Mod2002025Key.C0012);
				}
			}
			// Si la combinación contiene la clave 00078, tambien debe marcarse la clave 1 o la 2
			if (combinacion.contains(Mod2002025Key.C0078)) {
				if (!combinacion.contains(Mod2002025Key.C0001) && !combinacion.contains(Mod2002025Key.C0002)) {
					combinacion.add(Mod2002025Key.C0001);
				}
			}
			// Comprobar compatibilidad de los caracteres de la combinación
			for (int i = 0; i < combinacion.size(); i++) {
				for (int j = i + 1; j < combinacion.size(); j++) {
					if (Arrays.asList(Mod2002025Character.CHARACTER_INCOMPATIBILITY_MAP.get(combinacion.get(i))).contains(combinacion.get(j))) {
						compatible = false;
						break;
					}
				}
				if (!compatible) break;
			}
			if (compatible) {
//				contador++;
				// Convertir la lista de combinacion a un array de Mod2002025Key, para llamar a la función validate con el array de combinacion
				Mod2002025Key[] combinacionArray = new Mod2002025Key[combinacion.size()];
				combinacion.toArray(combinacionArray);
				// Imprimir la combinación de caracteres que se va a probar
//				System.out.println(Arrays.toString(combinacionArray));
				// Probar solo las que llevan onlyKey, si no se pasa ninguna, probar todas
				if (onlyKey == null || combinacion.contains(onlyKey)) {
					contador++;
					utePercent = 0.0;
					if (!pruebaSimple(false, combinacionArray)) 
						erroneas++;
					// SI ES EL CARACTER 13 O 85 SE PRUEBEN LOS PORCENTAJES 0, 100 Y 80
					if (combinacion.contains(Mod2002025Key.C0013) || combinacion.contains(Mod2002025Key.C0085)) {
						// Probar con porcentaje 100
						utePercent = 100.0;
						contador++;
						if (!pruebaSimple(false, combinacionArray)) 
							erroneas++;
						// Probar con porcentaje 80
						utePercent = 80.0;
						contador++;
						if (!pruebaSimple(false, combinacionArray)) 
							erroneas++;
					}
				}
			}
        }
        System.out.println("Param characterKeys : " + Arrays.toString(characterKeys));
        System.out.println("Param onlyKey       : " + onlyKey);
        System.out.println("Total de combinaciones generadas   : " + todasLasCombinaciones.size());
        System.out.println("Total de combinaciones comprobadas : " + contador);
        System.out.println("Total de combinaciones erroneas    : " + erroneas);
		
	}
	
	public static void main(String[] args) {
		
		// CARACTERES IMPLICADOS EN EL CALCULO DE LA CASILLA 00558 (TIPO DE GRAVAMEN)
		// C0001, C0006, C0012, C0013, C0014, C0015, C0017, C0018, C0021, C0034, C0046, C0048, C0063, C0064, C0071, C0079, C0083, C0085, C0088

		// CARACTERES IMPLICADOS EN EL CALCULO DE LA CASILLA 00562 (CUOTA INTEGRA)
		// C0006, C0012, C0015, C0034, C0046, C0057, C0063, C0064, C0071, C0079, C0083, C0088,
		
		// CARACTERES IMPLICADOS EN EL CALCULO DE LA CASILLA 00560 (COOPERATIVAS CUOTA INTEGRA PREVIA)
		// C0006, C0017, C0018, C0019, C0057, C0063, C0071, C0083, C0088,
		
		Mod2002025Key[] characterKeys = {
			Mod2002025Key.C0001, 
			Mod2002025Key.C0006, 
			Mod2002025Key.C0012, 
			Mod2002025Key.C0013, 
			Mod2002025Key.C0014, 
			Mod2002025Key.C0015, 
			Mod2002025Key.C0017,  
			Mod2002025Key.C0018, 
			Mod2002025Key.C0019, 
			Mod2002025Key.C0021, 
			Mod2002025Key.C0034, 
			Mod2002025Key.C0046, 
			Mod2002025Key.C0048,
			Mod2002025Key.C0057,
			Mod2002025Key.C0063, 
			Mod2002025Key.C0064, 
			Mod2002025Key.C0071, 
			Mod2002025Key.C0079, 
			Mod2002025Key.C0083, 
			Mod2002025Key.C0085, 
		 	Mod2002025Key.C0088
		};
		
//		pruebaSimple(false, Mod2002025Key.C0072, Mod2002025Key.C0006);
		pruebaTodas(null, characterKeys);
//		pruebaTodas(Mod2002025Key.C0088, characterKeys);
		
	}

}
