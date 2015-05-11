package com.esferalia.aon.occam.server.fiscal.calc;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Aeat2015ModuleCalculator  {
	
	public static FiscalActivity calculate(AONContext ctx, FiscalActivity fac) {
		try {
			if (fac.hasIRPFModules()) {
				calculateIRPF(ctx,fac);
			}
			if (fac.hasVATModules()) {
				calculateIVA(ctx,fac);
			}
			return fac;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	public static FiscalActivity calculateIRPF(AONContext ctx, FiscalActivity fac) {
		fac = calculateM01(ctx,fac);
		fac = calculateM02(ctx,fac);
		fac = calculateInfoIRPF(ctx,fac);
		return fac;
	}
	
	/**
	 *  Personal asalariado: Persona asalariada es cualquier otra que trabaje 
	 *  en la actividad. En particular, tendrán la consideración de personal 
	 *  asalariado el cónyuge y los hijos menores del sujeto pasivo que convivan
	 *  con él, siempre que, existiendo el oportuno contrato laboral y la afiliación 
	 *  al régimen general de la Seguridad Social, trabajen habitualmente y con 
	 *  continuidad en la actividad empresarial desarrollada por el contribuyente. 
	 *  No se computarán como personas asalariadas los alumnos de formación 
	 *  profesional específica que realicen el módulo obligatorio de formación 
	 *  en centros de trabajo. 
	 *  Se computará como una persona asalariada la que  trabaje el número de 
	 *  horas anuales por trabajador fijado en el convenio colectivo correspondiente 
	 *  o, en su defecto, mil ochocientas horas/año. Cuando el número de horas de 
	 *  trabajo al año sea inferior o superior, se estimará como cuantía de la 
	 *  persona asalariada la proporción existente entre el número de horas 
	 *  efectivamente trabajadas y las fijadas en el convenio colectivo o, en 
	 *  su defecto, mil ochocientas. 
	 *  Se computará en un 60 por 100 al personal asalariado menor de diecinueve 
	 *  años y al que preste sus servicios bajo un contrato de aprendizaje o 
	 *  para la formación. Cuando el personal asalariado sea una persona con 
	 *  discapacidad, con grado de minusvalía igual o superior al 33 %, se 
	 *  computará en un 40 por 100. Estas reducciones serán incompatibles entre sí.
	 * @param ctx 
	 * @param fac
	 * @return
	 */
	private static FiscalActivity calculateM01(AONContext ctx, FiscalActivity fac) {
		double m01 = 0.0;
		//Mayores de 19 años
		double m011 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M011);
		//Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
		double m012 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M012);
		//Discapacitados con grado de minusvalía igual o superior al 33 por 100
		double m013 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M013);
		//Horas anuales
		double m014 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M014);
		
		if (AonMathUtils.round(m014) == 0) {
			m014 = 1800;
		}
		if (AonMathUtils.round(m011) != 0) {
			double d = AonMathUtils.round(m011 / m014);
			m01 = d;	
		}
		if (AonMathUtils.round(m012) != 0) {
			double d = AonMathUtils.round((m012 / m014) * 0.60);
			m01 = m01 + d;
		}
		if (AonMathUtils.round(m013) != 0) {
			double d = AonMathUtils.round((m013 / m014) * 0.40);
			m01 = m01 + d;
		}
		fac.setIRPFModuleValue(FiscalActivityInfoKey.M01, AonMathUtils.round(m01));
		return fac;
	}
	
	/**
	 * Personal no asalariado. Personal no asalariado es el empresario. También
	 * tendrán esta consideración, su cónyuge y los hijos menores que convivan
	 * con él, cuando, trabajando efectivamente en la actividad, no constituyan
	 * personal asalariado de acuerdo con lo establecido en la regla siguiente.
	 * 
	 * Se computará como una persona no asalariada el empresario. En aquellos
	 * supuestos que pueda acreditarse una dedicación inferior a 1.800 horas/año
	 * por causas objetivas, tales como jubilación, incapacidad, pluralidad de
	 * actividades o cierre temporal de la explotación, se computará el tiempo
	 * efectivo dedicado a la actividad. En estos supuestos, para la
	 * cuantificación de las tareas de dirección, organización y planificación
	 * de la actividad y, en general, las inherentes a la titularidad de la
	 * misma, se computará al empresario en 0,25 personas/año, salvo cuando se
	 * acredite una dedicación efectiva superior o inferior.
	 * 
	 * Para el resto de personas no asalariadas se computará como una persona no
	 * asalariada la que trabaje en la actividad al menos mil ochocientas
	 * horas/año. Cuando el número de horas de trabajo al año sea inferior a mil
	 * ochocientas, se estimará como cuantía de la persona no asalariada la
	 * proporción existente entre número de horas efectivamente trabajadas en el
	 * año y mil ochocientas.
	 * 
	 * El personal no asalariado con un grado de minusvalía igual o superior al
	 * 33% se computará al 75 por 100. Cuando el cónyuge o los hijos menores
	 * tengan la condición de no asalariados se computarán al 50 por 100,
	 * siempre que el titular de la actividad se compute por entero, antes de
	 * aplicar, en su caso, la reducción prevista en el párrafo anterior, y no
	 * haya más de una persona asalariada. Esta reducción se practicará después
	 * de haber aplicado, en su caso, la correspondiente por grado de minusvalía
	 * igual o superior al 33%.
	 * @param ctx 
	 * 
	 * @param fac
	 * @return
	 */
	private static FiscalActivity calculateM02(AONContext ctx, FiscalActivity fac) {
		double m02 = 0.0;
		//Horas anuales del titular.
		double m021 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M021);
		//Horas anuales del cónyuge.
		double m022 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M022);
		//Indique si el cónyuge es discapacitado en grado igual o superior al 33%
		double m023 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M023);
		//Horas anuales de los hijos menores de 18 años.
		double m024 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M024);
		//Horas anuales de los hijos menores de 18 años con discapacidad en grado igual o superior al 33%
		double m025 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M025);
		
		boolean titularFullTime = AonMathUtils.round(m021) >= 1800;
		boolean moreThanOne = (AonMathUtils.round(m022 + m024 + m025) > 1800);
		if (AonMathUtils.round(m021) != 0) {
			m02 = (m021>=1800?1800:m021) / 1800;
		}
		if (AonMathUtils.round(m022) != 0) {
			double d = ((m022>=1800?1800:m022) / 1800);
			if (m023 == 1) {
				d = AonMathUtils.round(d * 0.75);
			}
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d / 2);
			}
			m02 = m02 + d;	
		}
		
		if (AonMathUtils.round(m024) != 0) {
			double d = AonMathUtils.round(m024 / 1800);
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d/ 2);
			}
			m02 = m02 + d;	
		}
		
		if (AonMathUtils.round(m025) != 0) {
			double d = ((m025 / 1800) * 0.75);
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d / 2);
			}
			m02 = m02 + d;	
		}
		fac.setIRPFModuleValue(FiscalActivityInfoKey.M02, AonMathUtils.round(m02));
		return fac;
	}

	/**
	 * Calculo RENDIMIENTO NETO PREVIO. Suma rendimiento de los modulos.
	 * @param ctx 
	 * @param fac
	 * @return
	 */
	private static FiscalActivity calculateInfoIRPF(AONContext ctx, FiscalActivity fac) {
		// *****************************************************************
		// 					RENDIMIENTO NETO PREVIO
		// *****************************************************************
		double i01= 0.0;
		if (fac.getMap().get(FiscalActivityInfoKeyType.IRPF_MODULE.ordinal()) != null 
			&& fac.getMap().get(FiscalActivityInfoKeyType.IRPF_MODULE.ordinal()).values() != null ) {
			for (FiscalActivityInfo mod : fac.getMap().get(FiscalActivityInfoKeyType.IRPF_MODULE.ordinal()).values() ) {
				double base = AonMathUtils.round(mod.getDoubleValue() * mod.getFactor() );
				mod.setBase(base);
				i01 = AonMathUtils.round(i01 + base );
			}	
		}
		
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I01, AonMathUtils.round(i01));

		// *****************************************************************
		// 					Incentivos al empleo
		// *****************************************************************
		//a) Minoración por incentivos al empleo
		//	1.o) Si en el año que se liquida hubiese tenido lugar un incremento del 
		//		 número de personas asalariadas, por comparación al año inmediato 
		//		 anterior, se calculará, en primer lugar, la diferencia entre el 
		//		 número de unidades del módulo "personal asalariado" correspondientes 
		//		 al año y el número de unidades de ese mismo módulo correspondientes al 
		//		 año inmediato anterior. A estos efectos, se tendrán en cuenta exclusivamente 
		//		 las personas asalariadas que se hayan computado en la Fase 1a, de acuerdo 
		//		 con lo establecido en la Regla 2a.
		//		 Si en el año anterior no se hubiese estado acogido al régimen de 
		//		 estimación objetiva, se tomará como número de unidades correspondientes 
		//		 a dicho año el que hubiese debido tomarse, de acuerdo a las normas contenidas
		//		 en la Regla 2a de la Fase anterior. Si la diferencia resultase positiva, a 
		//		 ésta se aplicará el coeficiente 0,40. El resultado es el coeficiente por 
		//		 incremento del número de personas asalariadas. Si la diferencia hubiese 
		//		 resultado positiva y, por tanto, hubiese procedido la aplicación del coeficiente 
		//		 0,40, a dicha diferencia no se le aplicará la tabla de coeficientes por 
		//		 tramos que se señala a continuación.
		//
		//2.o) Además, a cada uno de los tramos del número de unidades del módulo 
		//	 que a continuación se indica se le aplicarán los coeficientes que 
		//	 se expresan en la siguiente tabla:
		//		 Tramo				 Coeficiente
		//		 -------------------------------
		//		 Hasta 1,00					0,10
		//		 Entre 1,01 a 3,00			0,15
		//		 Entre 3,01 a 5,00			0,20
		//		 Entre 5,01 a 8,00			0,25
		//		 Más de 8,00				0,30
		//
		//	Para cuantificar la minoración por incentivos al empleo, se procede 
		//	de la siguiente forma:
		//		- Se suma el coeficiente por incremento del número de personas 
		//		asalariadas, si procede, y el de la tabla anterior, obteniéndose 
		//		el coeficiente de minoración.
		//		- Este coeficiente de minoración se multiplica por el "Rendimiento 
		//		anual por unidad antes de amortización" correspondiente al módulo 
		//		"personal asalariado". La cantidad anterior se minora del rendimiento neto previo.

		double personalAsalariado = fac.getIRPFModuleDoubleValue(FiscalActivityInfoKey.M01);
		if (fac.getIRPFModule(FiscalActivityInfoKey.M15) != null) {
			personalAsalariado += fac.getIRPFModuleDoubleValue(FiscalActivityInfoKey.M15);
		}
		if (fac.getIRPFModule(FiscalActivityInfoKey.M16) != null) {
			personalAsalariado += fac.getIRPFModuleDoubleValue(FiscalActivityInfoKey.M16);
		}
		double coef = 0.0;
//		double previousAsalariados = getPreviousAsalariados(ctx,fac);

		// Empleados al inicio de ejercicio (o al inicio de la actividad
		double a10 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A10);
		
		double i02 = 0;
		double as = personalAsalariado;
		if (a10 != 0 && personalAsalariado >= a10 ) {
			if( AonMathUtils.round((personalAsalariado - a10)) > 0 ) {
				coef = 0.40;
			} else {
				if (AonMathUtils.round(as) > 0.0) {
					coef = coef + 0.10; 
					as = AonMathUtils.round(as - 1);
				}
				if (AonMathUtils.round(as) > 0.0) {
					coef = coef + 0.15;
					as = AonMathUtils.round(as - 2);
				}
				if (AonMathUtils.round(as) > 0.0) {
					coef = coef + 0.20;
					as = AonMathUtils.round(as - 2);
				}
				if (AonMathUtils.round(as) > 0.0) {
					coef = coef + 0.25;
					as = AonMathUtils.round(as - 3);
				}
				if (AonMathUtils.round(as) > 0.0) {
					coef = coef + 0.30;
				}
			}
			if (coef != 0 ) {
				FiscalActivityInfoKey[] persoKeys = {FiscalActivityInfoKey.M01,FiscalActivityInfoKey.M15,FiscalActivityInfoKey.M16};
				for (FiscalActivityInfoKey key : persoKeys ){
					FiscalActivityInfo persoAsal = fac.getIRPFModule(key);
					if (persoAsal != null) {
						double m01 = persoAsal.getDoubleValue();
						double ratioPersonalAsalariado = 
								(personalAsalariado != 0 )?m01 / personalAsalariado:1;
						double f = persoAsal.getFactor();
						i02 = AonMathUtils.round(i02 + (coef * ratioPersonalAsalariado * f)); 
					}
				}
			}
		}
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I02, AonMathUtils.round(i02));
		
		
		// *****************************************************************
		// 				RENDIMIENTO NETO MINORADA
		// *****************************************************************
		double i03 = fac.getIRPFInfoDoubleValue(FiscalActivityInfoKey.I03);
		double i04 = AonMathUtils.round(i01 - i02 - i03 );
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I04, i04);
		
		// *****************************************************************
		// 				Índice corrector Especial
		// *****************************************************************
		
		// Número de vehículos afectos de la actividad.
		double a07 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A07);
		// Municipio donde se ejerce la actividad.
		double a09 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A09);
		
		
		// Los índices correctores especiales sólo se aplicarán en aquellas 
		// actividades concretas que se citan a continuación:
		double i06 = 0.0;
		boolean indiceEmpresasPequeñaDimensionAplicable = true; 
		if (Epigraph.E_659_4B.getEpigraph().equals(fac.getEpigraph())) {
			// Actividad de comercio al por menor de prensa, revistas y libros 
			// en quioscos situados en la vía pública:
			//	Ubicación de los quioscos					Índice
			//  --------------------------------------------------
			//	Madrid y Barcelona							  1,00
			//	Municipios de más de 100.000 habitantes		  0,95
			//	Resto de municipios							  0,80
			if (AonMathUtils.round(a09) == 7.0) {
				i06 = 1.0;
			} else if (AonMathUtils.round(a09) == 6.0) {
				i06 = 0.95;
			} else {
				i06 = 0.80;
			}
		} else if (Epigraph.E_721_1.getEpigraph().equals(fac.getEpigraph()) 
				|| Epigraph.E_721_3.getEpigraph().equals(fac.getEpigraph())) {
			// Actividad de transporte urbano colectivo y de viajeros por carretera:
			// Se aplicará el índice 0,80 cuando el titular disponga de un único vehículo.
			if (AonMathUtils.round(a07) == 1.0) {
				i06 = 0.80;
				indiceEmpresasPequeñaDimensionAplicable = false;	
			}
		} else if (Epigraph.E_721_2.getEpigraph().equals(fac.getEpigraph())) {
			//	Actividad de transporte por autotaxis.
			//		Población del municipio				  Índice
			//		--------------------------------------------
			//		Hasta 2.000 habitantes					0,75
			//		De 2.001 hasta 10.000 habitantes		0,80
			//		De 10.001 hasta 50.000 habitantes		0,85
			//		De 50.001 hasta 100.000 habitantes		0,90
			//		Más de 100.000 habitantes				1,00
			indiceEmpresasPequeñaDimensionAplicable = false;
			if (AonMathUtils.round(a09) == 1.0) {
				i06 = 0.75;
			} else if (AonMathUtils.round(a09) == 2.0 || AonMathUtils.round(a09) == 3.0) {
				i06 = 0.80;
			} else if (AonMathUtils.round(a09) == 4.0) {
				i06 = 0.85;
			} else if (AonMathUtils.round(a09) == 5.0) {
				i06 = 0.90;
			} else {
				i06 = 1.00;
			}
		} else if (Epigraph.E_722A.getEpigraph().equals(fac.getEpigraph())) {
			//Actividades de transporte de mercancías por carretera y servicios de mudanzas:
			// Se aplicará el índice 0,80 cuando el titular disponga de un único vehículo.
			// Se aplicará el índice 0,90 cuando la actividad se realice con tractocamiones
			// y el titular carezca de semirremolques. Cuando la actividad se desarrolle con 
			// un único tractocamión y sin semirremolques, se aplicará, exclusivamente, el índice 0,75.

			if (AonMathUtils.round(a07) == 1.0) {
				i06 = 0.80;
				indiceEmpresasPequeñaDimensionAplicable = false;
			}
			// Indique si la actividad se realiza con un único tractocamión y sin semirremolques.
			double c11 = fac.getInfoDoubleValue(FiscalActivityInfoKey.C11);
			if (AonMathUtils.round(c11) == 1.0) {
				i06 = 0.75;
				indiceEmpresasPequeñaDimensionAplicable = false;
			}
			// Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.
			double c10 = fac.getInfoDoubleValue(FiscalActivityInfoKey.C10);
			if (AonMathUtils.round(c10) == 1.0) {
				i06 = 0.90;
				indiceEmpresasPequeñaDimensionAplicable = false;
			}
		} else if (Epigraph.E____.getEpigraph().equals(fac.getEpigraph())) {
			// Actividad de producción de mejillón en batea:
			//	- Empresa con una sola batea y sin barco auxiliar: 0,75.
			//	- Empresa con una sola batea y con un barco auxiliar de 
			//	  menos de 15 toneladas de registro bruto (T.R.B.): 0,85.
			//	- Empresa con una sola batea y con un barco auxiliar de 15 
			//	  a 30 T.R.B.; y empresa con dos bateas y sin barco auxiliar: 0,90.
			//	- Empresa con una sola batea y con un barco auxiliar de más 
			//	  de 30 T.R.B.; y empresa con dos bateas y un barco auxiliar 
			//    de menos de 15 T.R.B.: 0,95.
			indiceEmpresasPequeñaDimensionAplicable = false;
			// Número de bateas y de barcos auxiliares de la empresa.
			double b06 = fac.getInfoDoubleValue(FiscalActivityInfoKey.B06);
			if (AonMathUtils.round(b06) == 1.0) {
				i06 = 0.75;
			} else if (AonMathUtils.round(b06) == 2.0) {
				i06 = 0.85;
			} else if (AonMathUtils.round(b06) == 3.0 || AonMathUtils.round(b06) == 5.0) {
				i06 = 0.90;
			} else if (AonMathUtils.round(b06) == 4.0 || AonMathUtils.round(b06) == 6.0) {
				i06 = 0.95;
			} else {
				// Otros: numero de bateas, barcos o TRB distintos de los anteriores.
				indiceEmpresasPequeñaDimensionAplicable = true;	
			}
		}
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I06,i06);

		
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participación.
		double a02 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A02);
		// Ejerce la actividad en un sólo local o sin él.
		double a06 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A06);
		// Capacidad de carga del vehículo superior a 1000 Kg.
		double a08 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A08);
		
		
		// *****************************************************************
		// 			Índice corrector de empresas de pequeña dimensión
		// *****************************************************************
		// Se aplicará el índice que corresponda, en función de la población en 
		// que se desarrolle la actividad, cuando concurran todas y cada una de 
		// las circunstancias siguientes:
		//	1.) Titular persona física.
		//	2.) Ejercer la actividad en un solo local.
		//	3.) No disponer de más de un vehículo afecto a la actividad y que 
		//		éste no supere los 1.000 kilogramos de  capacidad de carga.
		//	4.o) Sin personal asalariado.
		//	Población del municipio				Índice
		//
		//  -------------------------------------------
		//	Hasta 2.000 habitantes				  0,70
		//	De 2.001 hasta 5.000 habitantes		  0,75
		//	Más de 5.000 habitantes				  0,80
		//
		// Cuando, por ejercerse la actividad en varios municipios, exista la 
		// posibilidad de aplicar más de uno de los índices anteriores, se 
		// aplicará un único índice: el correspondiente al municipio de mayor 
		// población.
		// Cuando concurran las circunstancias señaladas en los números 
		// 1.), 2.) y 3.) del primer párrafo y, además, se ejerza la actividad 
		// con personal asalariado, hasta 2 trabajadores, se aplicará el índice 
		// 0,90, cualquiera que sea la población del municipio en el que se 
		// desarrolla la actividad.
		
		double i07 = 0.0;
		if (indiceEmpresasPequeñaDimensionAplicable) {
			// En ningún caso será aplicable el índice corrector para empresas de pequeña 
			// dimensión (b.1) a las actividades para las que están previstos los índices 
			// correctores especiales enumerados en las letras a.2), a.3), a.4) y a.5).
			if (AonMathUtils.round(a06) == 1.0) {
				if (AonMathUtils.round(a07) <= 1.0) {
					if (AonMathUtils.round(a08) == 0.0) {
						i07 = 0.7; 
						if (AonMathUtils.round(a09) == 2.0) {
							i07 = 0.75;
						} else if (AonMathUtils.round(a09) >= 3.0) {
							i07 = 0.80;
						}
						if (a10 > 0.0 && a10 <= 2.0) {
							i07 = 0.90;
						}
						if (a10 > 2) {
							i07 = 0.0;
						}
					}
				}
			}
		}
		boolean indiceEmpresasPequeñaDimensionAplicado = (i07!=0.0);
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I07,i07);
		
		// *****************************************************************
		// Índice corrector de temporada
		// *****************************************************************
		// Actividad de Temporada. nº de dias de ejercicio en el año anterior.
		double a03 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A03);
		double i08 = 0.0;
		if (AonMathUtils.round(a03) > 0.0 && AonMathUtils.round(a03) <=60.0) {
			i08 = 1.5;
		} else if (AonMathUtils.round(a03) > 60.0 && AonMathUtils.round(a03) <= 120.0) {
			i08 = 1.35;
		} else if (AonMathUtils.round(a03) > 120.0 && AonMathUtils.round(a03) <= 180.0) {
			i08 = 1.25;
		}
		boolean indiceTemporadaAplicado = (i08!=0.0);
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I08,i08);

		// *****************************************************************
		// Índice corrector de exceso
		// *****************************************************************
		
		// Cuando resulte aplicable el índice corrector para empresas de pequeña 
		// dimensión (b.1) no se aplicará el índice corrector de exceso
		double i09 = 0.0;
		if (!indiceEmpresasPequeñaDimensionAplicado) {
			double tope = fac.getMaxImport();
			double baseIndice = i04;
			// Aplicamos los índices correctores anteriores
			if (AonMathUtils.round(i06) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * i06);	
			}
			if (AonMathUtils.round(i07) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * i07);	
			}
			if (AonMathUtils.round(i08) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * i08);	
			}
			if (baseIndice > tope) {
				 i09 = 1.3;
			}
		}
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I09,i09);
		
		// *****************************************************************
		// Índice corrector de inicio de nueva actividad
		// *****************************************************************
		
		// Índice corrector por inicio de nuevas actividades.
		// El contribuyente que inicie nuevas actividades concurriendo las siguientes 
		// circunstancias:
		// - Que se trate de nuevas actividades cuyo ejercicio se inicie a partir 
		//   del 1 de enero de 2012.
		// - Que no se trate de actividades de temporada.
		// - Que no se hayan ejercido anteriormente bajo otra titularidad o calificación.
		// - Que se realicen en local o establecimiento dedicados exclusivamente a 
		//   dicha actividad, con total separación del resto de actividades empresariales 
		//	 o profesionales que, en su caso, pudiera realizar el contribuyente.
		// Tendrá derecho a aplicar los siguientes índices correctores:
		//		Ejercicio	Índice
		//  	------------------
		//		Primero		  0,80
		//		Segundo		  0,90
		
		// Cuando resulte aplicable el índice corrector de temporada (b.2) no 
		// se aplicará el índice corrector por inicio de nuevas actividades
		
		double i10 = 0.0;
		if (!indiceTemporadaAplicado) {
			// Ejerce la actividad en un sólo local o sin él.
			if (AonMathUtils.round(a06) == 1.0) {
				// Indique si el titular es discapacitado en grado igual o superior al 33%
				double a13 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A13);
				// Nuevas actividades iniciadas a partir del 1 de enero del año anterior. Año de inicio.
				double a04 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A04);
				if (AonMathUtils.round(a04) != 0.0) {
					// Primero
					if (fac.getYear() == (int) a04) {
						i10 = (AonMathUtils.round(a13)==0.0)?0.80:0.60; 
					}
					// Segundo
					if (( fac.getYear() - 1) == (int) a04) {
						i10 = (AonMathUtils.round(a13)==0.0)?0.90:0.70;
					}
				}
			}
		}
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I10,i10);
		
		// *****************************************************************
		// 		RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (i.R.P.F.)
		// *****************************************************************
		double i11 = 0.0;
		i11 = i04;
		if (AonMathUtils.round(i06) != 0.0) {
			i11 = i04 * i06;	
		}
		if (AonMathUtils.round(i07) != 0.0) {
			i11 = i11 * i07;	
		}
		if (AonMathUtils.round(i08) != 0.0) {
			i11 = i11 * i08;	
		}
		if (AonMathUtils.round(i09) != 0.0) {
			double baseExceso = i11 - fac.getMaxImport();
			baseExceso = baseExceso * i09; 
			i11 = baseExceso + fac.getMaxImport();	
		}
		if (AonMathUtils.round(i10) != 0.0) {
			i11 = i11 * i10;	
		}
		// Disposición adicional primera. 
		//	Reducción en 2013 del rendimiento neto calculado por el método de estimación objetiva.
		//	Los contribuyentes que determinen el rendimiento neto de sus actividades
		//	económicas por el método de estimación objetiva, podrán reducir el rendimiento 
		//	neto de módulos obtenido en 2013 en un 5 por 100.
		i11 = i11 - (i11 * 5 / 100);
		
		
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participación.
		if (AonMathUtils.round(a02) != 0.0) {
			i11 = i11 - (i11 * a02 / 100);
			
		}
		i11 = AonMathUtils.round(i11);
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I11,i11);
		
		// *****************************************************************
		// Reducción para actividades económicas realizadas en el término municipal de Lorca
		// *****************************************************************
		
		// Los contribuyentes del Impuesto sobre la Renta de las Personas Físicas que
		// desarrollen actividades económicas incluidas en el anexo II de esta Orden en el término
		// municipal de Lorca y determinen el rendimiento neto por el método de estimación objetiva,
		// podrán reducir el rendimiento neto de módulos de 2013 correspondiente a tales
		// actividades en un 20 por ciento.
		double i12 = 0.0;
		// Si en 2013 realiza la actividad en LORCA, seleccione:
		double a13 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A11);
		if (AonMathUtils.round(a13) != 0.0) {
			i12 = AonMathUtils.round(i11 * 20 / 100); 	
		}
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I12,i12);		
		
		// *****************************************************************
		// Rendimientos a efectos de pagos fraccionados después de la reducción
		// *****************************************************************
		double i13 = AonMathUtils.round(i11 - i12);
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I13,i13);
		
		
		
		// *****************************************************************
		// Resultado. Pago Trimestral.
		// *****************************************************************
		 
		if (a10 == 0) {
			a10 = personalAsalariado;
		}
		double i14 = 4.0;
		if (AonMathUtils.round(a10) <= 1.0) i14 = 3.0; 
		if (AonMathUtils.round(a10) == 0.0) i14 = 2.0;
		double i15 = AonMathUtils.round(i13 *  i14 / 100 );
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I14,i14);
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I15,i15);

		return fac;
	}
	
	public static FiscalActivity calculateIVA(AONContext ctx, FiscalActivity fac) {
		// *****************************************
		// Indice corrector de Temporada
		// *****************************************
		double a03 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A03);
		double v01 = 0.0;
		if (AonMathUtils.round(a03) > 0.0 && AonMathUtils.round(a03) <=60.0) {
			v01 = 1.5;
		} else if (AonMathUtils.round(a03) > 60.0 && AonMathUtils.round(a03) <= 120.0) {
			v01 = 1.35;
		} else if (AonMathUtils.round(a03) > 120.0 && AonMathUtils.round(a03) <= 180.0) {
			v01 = 1.25;
		}
		fac.setVATInfoValue(FiscalActivityInfoKey.V01,v01);
		
		// *****************************************************************
		// Cuota anual devengada por operaciones corrientes
		// *****************************************************************
		double vatBase = 0.0;
		if (fac.getMap().get(FiscalActivityInfoKeyType.VAT_MODULE.ordinal()) != null 
			&& fac.getMap().get(FiscalActivityInfoKeyType.VAT_MODULE.ordinal()).values() != null ) {
			for (FiscalActivityInfo mod : fac.getMap().get(FiscalActivityInfoKeyType.VAT_MODULE.ordinal()).values() ) {
				double base = AonMathUtils.round(mod.getDoubleValue() * mod.getFactor() );
				mod.setBase(base);
				vatBase = AonMathUtils.round(vatBase + base );
			}
		}
		double v02 = AonMathUtils.round(v01) == 0.0? vatBase : AonMathUtils.round(vatBase * v01); 
		fac.setVATInfoValue(FiscalActivityInfoKey.V02,v02);
		
		// *****************************************************************
		// Reducción aplicable por actividades económicas realizadas en el término municipal de Lorca
		// *****************************************************************
		//	Los sujetos pasivos del Impuesto sobre el Valor Añadido que desarrollen
		//	actividades empresariales o profesionales incluidas en el anexo II de 
		//	esta Orden en el término municipal de Lorca y estén acogidos al régimen 
		//	especial simplificado, podrán reducir en un 20 por ciento el importe de 
		//	las cuotas devengadas por operaciones corrientes correspondiente a tales 
		//  actividades en el año 2013.
		double v03 = 0.0;
		// Si en 2013 realiza la actividad en LORCA, seleccione:
		double a13 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A11);
		if (AonMathUtils.round(a13) != 0.0) {
			v03 = AonMathUtils.round(v02 * 20 / 100); 	
		}
		fac.setVATInfoValue(FiscalActivityInfoKey.V03,v03);
		
		// *****************************************************************
		// Cuota anual por operaciones corrientes después de las reducciones anteriores
		// *****************************************************************
		double v04 = AonMathUtils.round(v02 - v03);
		fac.setVATInfoValue(FiscalActivityInfoKey.V04,v04);
		
		// *****************************************************************
		// Porcentaje aplicable
		// *****************************************************************
		double v05 = fac.getVatPercent(); 
		fac.setVATInfoValue(FiscalActivityInfoKey.V05,v05);
		
		// *****************************************************************
		// INGRESO A CUENTA POR OPERACIONES CORRIENTES PREVIO
		// *****************************************************************
		double v06 = AonMathUtils.round(v04 *  v05 / 100); 
		fac.setVATInfoValue(FiscalActivityInfoKey.V06,v06);
		return fac;
	}
	
}