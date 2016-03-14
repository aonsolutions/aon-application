package com.esferalia.aon.occam.server.fiscal.calc;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
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
	 *  en la actividad. En particular, tendr?n la consideraci?n de personal 
	 *  asalariado el c?nyuge y los hijos menores del sujeto pasivo que convivan
	 *  con ?l, siempre que, existiendo el oportuno contrato laboral y la afiliaci?n 
	 *  al r?gimen general de la Seguridad Social, trabajen habitualmente y con 
	 *  continuidad en la actividad empresarial desarrollada por el contribuyente. 
	 *  No se computar?n como personas asalariadas los alumnos de formaci?n 
	 *  profesional espec?fica que realicen el m?dulo obligatorio de formaci?n 
	 *  en centros de trabajo. 
	 *  Se computar? como una persona asalariada la que  trabaje el n?mero de 
	 *  horas anuales por trabajador fijado en el convenio colectivo correspondiente 
	 *  o, en su defecto, mil ochocientas horas/a?o. Cuando el n?mero de horas de 
	 *  trabajo al a?o sea inferior o superior, se estimar? como cuant?a de la 
	 *  persona asalariada la proporci?n existente entre el n?mero de horas 
	 *  efectivamente trabajadas y las fijadas en el convenio colectivo o, en 
	 *  su defecto, mil ochocientas. 
	 *  Se computar? en un 60 por 100 al personal asalariado menor de diecinueve 
	 *  a?os y al que preste sus servicios bajo un contrato de aprendizaje o 
	 *  para la formaci?n. Cuando el personal asalariado sea una persona con 
	 *  discapacidad, con grado de minusval?a igual o superior al 33 %, se 
	 *  computar? en un 40 por 100. Estas reducciones ser?n incompatibles entre s?.
	 * @param ctx 
	 * @param fac
	 * @return
	 */
	private static FiscalActivity calculateM01(AONContext ctx, FiscalActivity fac) {
		double m01 = 0.0;
		//Mayores de 19 a?os
		double m011 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M011);
		//Menores de 19 a?os y trabajadores con contratos de aprendizaje o formaci?n, que no sean discapacitados.
		double m012 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M012);
		//Discapacitados con grado de minusval?a igual o superior al 33 por 100
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
	 * Personal no asalariado. Personal no asalariado es el empresario. Tambi?n
	 * tendr?n esta consideraci?n, su c?nyuge y los hijos menores que convivan
	 * con ?l, cuando, trabajando efectivamente en la actividad, no constituyan
	 * personal asalariado de acuerdo con lo establecido en la regla siguiente.
	 * 
	 * Se computar? como una persona no asalariada el empresario. En aquellos
	 * supuestos que pueda acreditarse una dedicaci?n inferior a 1.800 horas/a?o
	 * por causas objetivas, tales como jubilaci?n, incapacidad, pluralidad de
	 * actividades o cierre temporal de la explotaci?n, se computar? el tiempo
	 * efectivo dedicado a la actividad. En estos supuestos, para la
	 * cuantificaci?n de las tareas de direcci?n, organizaci?n y planificaci?n
	 * de la actividad y, en general, las inherentes a la titularidad de la
	 * misma, se computar? al empresario en 0,25 personas/a?o, salvo cuando se
	 * acredite una dedicaci?n efectiva superior o inferior.
	 * 
	 * Para el resto de personas no asalariadas se computar? como una persona no
	 * asalariada la que trabaje en la actividad al menos mil ochocientas
	 * horas/a?o. Cuando el n?mero de horas de trabajo al a?o sea inferior a mil
	 * ochocientas, se estimar? como cuant?a de la persona no asalariada la
	 * proporci?n existente entre n?mero de horas efectivamente trabajadas en el
	 * a?o y mil ochocientas.
	 * 
	 * El personal no asalariado con un grado de minusval?a igual o superior al
	 * 33% se computar? al 75 por 100. Cuando el c?nyuge o los hijos menores
	 * tengan la condici?n de no asalariados se computar?n al 50 por 100,
	 * siempre que el titular de la actividad se compute por entero, antes de
	 * aplicar, en su caso, la reducci?n prevista en el p?rrafo anterior, y no
	 * haya m?s de una persona asalariada. Esta reducci?n se practicar? despu?s
	 * de haber aplicado, en su caso, la correspondiente por grado de minusval?a
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
		//Horas anuales del c?nyuge.
		double m022 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M022);
		//Indique si el c?nyuge es discapacitado en grado igual o superior al 33%
		double m023 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M023);
		//Horas anuales de los hijos menores de 18 a?os.
		double m024 = fac.getModuleDetailDoubleValue(FiscalActivityInfoKey.M024);
		//Horas anuales de los hijos menores de 18 a?os con discapacidad en grado igual o superior al 33%
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
		//a) Minoraci?n por incentivos al empleo
		//	1.o) Si en el a?o que se liquida hubiese tenido lugar un incremento del 
		//		 n?mero de personas asalariadas, por comparaci?n al a?o inmediato 
		//		 anterior, se calcular?, en primer lugar, la diferencia entre el 
		//		 n?mero de unidades del m?dulo "personal asalariado" correspondientes 
		//		 al a?o y el n?mero de unidades de ese mismo m?dulo correspondientes al 
		//		 a?o inmediato anterior. A estos efectos, se tendr?n en cuenta exclusivamente 
		//		 las personas asalariadas que se hayan computado en la Fase 1a, de acuerdo 
		//		 con lo establecido en la Regla 2a.
		//		 Si en el a?o anterior no se hubiese estado acogido al r?gimen de 
		//		 estimaci?n objetiva, se tomar? como n?mero de unidades correspondientes 
		//		 a dicho a?o el que hubiese debido tomarse, de acuerdo a las normas contenidas
		//		 en la Regla 2a de la Fase anterior. Si la diferencia resultase positiva, a 
		//		 ?sta se aplicar? el coeficiente 0,40. El resultado es el coeficiente por 
		//		 incremento del n?mero de personas asalariadas. Si la diferencia hubiese 
		//		 resultado positiva y, por tanto, hubiese procedido la aplicaci?n del coeficiente 
		//		 0,40, a dicha diferencia no se le aplicar? la tabla de coeficientes por 
		//		 tramos que se se?ala a continuaci?n.
		//
		//2.o) Adem?s, a cada uno de los tramos del n?mero de unidades del m?dulo 
		//	 que a continuaci?n se indica se le aplicar?n los coeficientes que 
		//	 se expresan en la siguiente tabla:
		//		 Tramo				 Coeficiente
		//		 -------------------------------
		//		 Hasta 1,00					0,10
		//		 Entre 1,01 a 3,00			0,15
		//		 Entre 3,01 a 5,00			0,20
		//		 Entre 5,01 a 8,00			0,25
		//		 M?s de 8,00				0,30
		//
		//	Para cuantificar la minoraci?n por incentivos al empleo, se procede 
		//	de la siguiente forma:
		//		- Se suma el coeficiente por incremento del n?mero de personas 
		//		asalariadas, si procede, y el de la tabla anterior, obteni?ndose 
		//		el coeficiente de minoraci?n.
		//		- Este coeficiente de minoraci?n se multiplica por el "Rendimiento 
		//		anual por unidad antes de amortizaci?n" correspondiente al m?dulo 
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
		// 				?ndice corrector Especial
		// *****************************************************************
		
		// N?mero de veh?culos afectos de la actividad.
		double a07 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A07);
		// Municipio donde se ejerce la actividad.
		double a09 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A09);
		
		
		// Los ?ndices correctores especiales s?lo se aplicar?n en aquellas 
		// actividades concretas que se citan a continuaci?n:
		double i06 = 0.0;
		boolean indiceEmpresasPequenaDimensionAplicable = true; 
		if (Epigraph.E_659_4B.getEpigraph().equals(fac.getEpigraph())) {
			// Actividad de comercio al por menor de prensa, revistas y libros 
			// en quioscos situados en la v?a p?blica:
			//	Ubicaci?n de los quioscos					?ndice
			//  --------------------------------------------------
			//	Madrid y Barcelona							  1,00
			//	Municipios de m?s de 100.000 habitantes		  0,95
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
			// Se aplicar? el ?ndice 0,80 cuando el titular disponga de un ?nico veh?culo.
			if (AonMathUtils.round(a07) == 1.0) {
				i06 = 0.80;
				indiceEmpresasPequenaDimensionAplicable = false;	
			}
		} else if (Epigraph.E_721_2.getEpigraph().equals(fac.getEpigraph())) {
			//	Actividad de transporte por autotaxis.
			//		Poblaci?n del municipio				  ?ndice
			//		--------------------------------------------
			//		Hasta 2.000 habitantes					0,75
			//		De 2.001 hasta 10.000 habitantes		0,80
			//		De 10.001 hasta 50.000 habitantes		0,85
			//		De 50.001 hasta 100.000 habitantes		0,90
			//		M?s de 100.000 habitantes				1,00
			indiceEmpresasPequenaDimensionAplicable = false;
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
			//Actividades de transporte de mercanc?as por carretera y servicios de mudanzas:
			// Se aplicar? el ?ndice 0,80 cuando el titular disponga de un ?nico veh?culo.
			// Se aplicar? el ?ndice 0,90 cuando la actividad se realice con tractocamiones
			// y el titular carezca de semirremolques. Cuando la actividad se desarrolle con 
			// un ?nico tractocami?n y sin semirremolques, se aplicar?, exclusivamente, el ?ndice 0,75.

			if (AonMathUtils.round(a07) == 1.0) {
				i06 = 0.80;
				indiceEmpresasPequenaDimensionAplicable = false;
			}
			// Indique si la actividad se realiza con un ?nico tractocami?n y sin semirremolques.
			double c11 = fac.getInfoDoubleValue(FiscalActivityInfoKey.C11);
			if (AonMathUtils.round(c11) == 1.0) {
				i06 = 0.75;
				indiceEmpresasPequenaDimensionAplicable = false;
			}
			// Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.
			double c10 = fac.getInfoDoubleValue(FiscalActivityInfoKey.C10);
			if (AonMathUtils.round(c10) == 1.0) {
				i06 = 0.90;
				indiceEmpresasPequenaDimensionAplicable = false;
			}
		} else if (Epigraph.E____.getEpigraph().equals(fac.getEpigraph())) {
			// Actividad de producci?n de mejill?n en batea:
			//	- Empresa con una sola batea y sin barco auxiliar: 0,75.
			//	- Empresa con una sola batea y con un barco auxiliar de 
			//	  menos de 15 toneladas de registro bruto (T.R.B.): 0,85.
			//	- Empresa con una sola batea y con un barco auxiliar de 15 
			//	  a 30 T.R.B.; y empresa con dos bateas y sin barco auxiliar: 0,90.
			//	- Empresa con una sola batea y con un barco auxiliar de m?s 
			//	  de 30 T.R.B.; y empresa con dos bateas y un barco auxiliar 
			//    de menos de 15 T.R.B.: 0,95.
			indiceEmpresasPequenaDimensionAplicable = false;
			// N?mero de bateas y de barcos auxiliares de la empresa.
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
				indiceEmpresasPequenaDimensionAplicable = true;	
			}
		}
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I06,i06);

		
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participaci?n.
		double a02 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A02);
		// Ejerce la actividad en un s?lo local o sin ?l.
		double a06 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A06);
		// Capacidad de carga del veh?culo superior a 1000 Kg.
		double a08 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A08);
		
		
		// *****************************************************************
		// 			?ndice corrector de empresas de peque?a dimensi?n
		// *****************************************************************
		// Se aplicar? el ?ndice que corresponda, en funci?n de la poblaci?n en 
		// que se desarrolle la actividad, cuando concurran todas y cada una de 
		// las circunstancias siguientes:
		//	1.) Titular persona f?sica.
		//	2.) Ejercer la actividad en un solo local.
		//	3.) No disponer de m?s de un veh?culo afecto a la actividad y que 
		//		?ste no supere los 1.000 kilogramos de  capacidad de carga.
		//	4.o) Sin personal asalariado.
		//	Poblaci?n del municipio				?ndice
		//
		//  -------------------------------------------
		//	Hasta 2.000 habitantes				  0,70
		//	De 2.001 hasta 5.000 habitantes		  0,75
		//	M?s de 5.000 habitantes				  0,80
		//
		// Cuando, por ejercerse la actividad en varios municipios, exista la 
		// posibilidad de aplicar m?s de uno de los ?ndices anteriores, se 
		// aplicar? un ?nico ?ndice: el correspondiente al municipio de mayor 
		// poblaci?n.
		// Cuando concurran las circunstancias se?aladas en los n?meros 
		// 1.), 2.) y 3.) del primer p?rrafo y, adem?s, se ejerza la actividad 
		// con personal asalariado, hasta 2 trabajadores, se aplicar? el ?ndice 
		// 0,90, cualquiera que sea la poblaci?n del municipio en el que se 
		// desarrolla la actividad.
		
		double i07 = 0.0;
		if (indiceEmpresasPequenaDimensionAplicable) {
			// En ning?n caso ser? aplicable el ?ndice corrector para empresas de peque?a 
			// dimensi?n (b.1) a las actividades para las que est?n previstos los ?ndices 
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
		boolean indiceEmpresasPequenaDimensionAplicado = (i07!=0.0);
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I07,i07);
		
		// *****************************************************************
		// ?ndice corrector de temporada
		// *****************************************************************
		// Actividad de Temporada. n? de dias de ejercicio en el a?o anterior.
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
		// ?ndice corrector de exceso
		// *****************************************************************
		
		// Cuando resulte aplicable el ?ndice corrector para empresas de peque?a 
		// dimensi?n (b.1) no se aplicar? el ?ndice corrector de exceso
		double i09 = 0.0;
		if (!indiceEmpresasPequenaDimensionAplicado) {
			double tope = fac.getMaxImport();
			double baseIndice = i04;
			// Aplicamos los ?ndices correctores anteriores
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
		// ?ndice corrector de inicio de nueva actividad
		// *****************************************************************
		
		// ?ndice corrector por inicio de nuevas actividades.
		// El contribuyente que inicie nuevas actividades concurriendo las siguientes 
		// circunstancias:
		// - Que se trate de nuevas actividades cuyo ejercicio se inicie a partir 
		//   del 1 de enero de 2012.
		// - Que no se trate de actividades de temporada.
		// - Que no se hayan ejercido anteriormente bajo otra titularidad o calificaci?n.
		// - Que se realicen en local o establecimiento dedicados exclusivamente a 
		//   dicha actividad, con total separaci?n del resto de actividades empresariales 
		//	 o profesionales que, en su caso, pudiera realizar el contribuyente.
		// Tendr? derecho a aplicar los siguientes ?ndices correctores:
		//		Ejercicio	?ndice
		//  	------------------
		//		Primero		  0,80
		//		Segundo		  0,90
		
		// Cuando resulte aplicable el ?ndice corrector de temporada (b.2) no 
		// se aplicar? el ?ndice corrector por inicio de nuevas actividades
		
		double i10 = 0.0;
		if (!indiceTemporadaAplicado) {
			// Ejerce la actividad en un s?lo local o sin ?l.
			if (AonMathUtils.round(a06) == 1.0) {
				// Indique si el titular es discapacitado en grado igual o superior al 33%
				double a13 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A13);
				// Nuevas actividades iniciadas a partir del 1 de enero del a?o anterior. A?o de inicio.
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
		// Disposici?n adicional primera. 
		//	Reducci?n en 2013 del rendimiento neto calculado por el m?todo de estimaci?n objetiva.
		//	Los contribuyentes que determinen el rendimiento neto de sus actividades
		//	econ?micas por el m?todo de estimaci?n objetiva, podr?n reducir el rendimiento 
		//	neto de m?dulos obtenido en 2013 en un 5 por 100.
		i11 = i11 - (i11 * 5 / 100);
		
		
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participaci?n.
		if (AonMathUtils.round(a02) != 0.0) {
			i11 = i11 - (i11 * a02 / 100);
			
		}
		i11 = AonMathUtils.round(i11);
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I11,i11);
		
		// *****************************************************************
		// Reducci?n para actividades econ?micas realizadas en el t?rmino municipal de Lorca
		// *****************************************************************
		
		// Los contribuyentes del Impuesto sobre la Renta de las Personas F?sicas que
		// desarrollen actividades econ?micas incluidas en el anexo II de esta Orden en el t?rmino
		// municipal de Lorca y determinen el rendimiento neto por el m?todo de estimaci?n objetiva,
		// podr?n reducir el rendimiento neto de m?dulos de 2013 correspondiente a tales
		// actividades en un 20 por ciento.
		double i12 = 0.0;
		// Si en 2013 realiza la actividad en LORCA, seleccione:
		double a13 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A11);
		if (AonMathUtils.round(a13) != 0.0) {
			i12 = AonMathUtils.round(i11 * 20 / 100); 	
		}
		fac.setIRPFInfoValue(FiscalActivityInfoKey.I12,i12);		
		
		// *****************************************************************
		// Rendimientos a efectos de pagos fraccionados despu?s de la reducci?n
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
		// Reducci?n aplicable por actividades econ?micas realizadas en el t?rmino municipal de Lorca
		// *****************************************************************
		//	Los sujetos pasivos del Impuesto sobre el Valor A?adido que desarrollen
		//	actividades empresariales o profesionales incluidas en el anexo II de 
		//	esta Orden en el t?rmino municipal de Lorca y est?n acogidos al r?gimen 
		//	especial simplificado, podr?n reducir en un 20 por ciento el importe de 
		//	las cuotas devengadas por operaciones corrientes correspondiente a tales 
		//  actividades en el a?o 2013.
		double v03 = 0.0;
		// Si en 2013 realiza la actividad en LORCA, seleccione:
		double a13 = fac.getInfoDoubleValue(FiscalActivityInfoKey.A11);
		if (AonMathUtils.round(a13) != 0.0) {
			v03 = AonMathUtils.round(v02 * 20 / 100); 	
		}
		fac.setVATInfoValue(FiscalActivityInfoKey.V03,v03);
		
		// *****************************************************************
		// Cuota anual por operaciones corrientes despu?s de las reducciones anteriores
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