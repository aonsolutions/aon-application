package com.code.aon.fiscal.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.esferalia.aon.watson.util.AonMathUtils;


public class Aeat2015ModuleCalculator implements IModuleCalculator, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Map<String, FiscalActivityInfoKey[]> SPECIAL_EPIGRAPHS = new HashMap<String, FiscalActivityInfoKey[]>();
	static {
		// Claves de Info para todos los epigrafes, menos los indicados a continuación.
		SPECIAL_EPIGRAPHS.put(null	 , new FiscalActivityInfoKey[] { 
				FiscalActivityInfoKey.A01
				,FiscalActivityInfoKey.A02
				,FiscalActivityInfoKey.A03 
				,FiscalActivityInfoKey.A04
				,FiscalActivityInfoKey.A05
				,FiscalActivityInfoKey.A06
				,FiscalActivityInfoKey.A07
				,FiscalActivityInfoKey.A08
				,FiscalActivityInfoKey.A09
				,FiscalActivityInfoKey.A10
				,FiscalActivityInfoKey.A11
				,FiscalActivityInfoKey.A13
		});
		// Transporte urbano colectivo y de viajeros por carretera 
		SPECIAL_EPIGRAPHS.put("721.1", new FiscalActivityInfoKey[] {
			FiscalActivityInfoKey.A01,FiscalActivityInfoKey.A02,FiscalActivityInfoKey.A03
			,FiscalActivityInfoKey.A04,FiscalActivityInfoKey.A05,FiscalActivityInfoKey.A07
			,FiscalActivityInfoKey.A10,FiscalActivityInfoKey.A11,FiscalActivityInfoKey.A13
		});
		// Transporte por autotaxis
		SPECIAL_EPIGRAPHS.put("721.2", new FiscalActivityInfoKey[] {
				FiscalActivityInfoKey.A01,FiscalActivityInfoKey.A02,FiscalActivityInfoKey.A03
				,FiscalActivityInfoKey.A04,FiscalActivityInfoKey.A05,FiscalActivityInfoKey.A09
				,FiscalActivityInfoKey.A10,FiscalActivityInfoKey.A11,FiscalActivityInfoKey.A13
			});
		// Transporte urbano colectivo y de viajeros por carretera 
		SPECIAL_EPIGRAPHS.put("721.3", new FiscalActivityInfoKey[] {
				FiscalActivityInfoKey.A01,FiscalActivityInfoKey.A02,FiscalActivityInfoKey.A03
				,FiscalActivityInfoKey.A04,FiscalActivityInfoKey.A05,FiscalActivityInfoKey.A07
				,FiscalActivityInfoKey.A10,FiscalActivityInfoKey.A11,FiscalActivityInfoKey.A13
			});
		// Transporte de mercancías por carretera, excepto residuos 
		SPECIAL_EPIGRAPHS.put("722"	 , new FiscalActivityInfoKey[] {
				FiscalActivityInfoKey.A01,FiscalActivityInfoKey.A02,FiscalActivityInfoKey.A03
				,FiscalActivityInfoKey.A04,FiscalActivityInfoKey.A05,FiscalActivityInfoKey.A07
				,FiscalActivityInfoKey.A10,FiscalActivityInfoKey.C10,FiscalActivityInfoKey.C11
				,FiscalActivityInfoKey.A11,FiscalActivityInfoKey.A13
			});
		// Servicios de mudanzas 
		SPECIAL_EPIGRAPHS.put("757"	 , new FiscalActivityInfoKey[] {
				FiscalActivityInfoKey.A01,FiscalActivityInfoKey.A02,FiscalActivityInfoKey.A03
				,FiscalActivityInfoKey.A04,FiscalActivityInfoKey.A05,FiscalActivityInfoKey.A07
				,FiscalActivityInfoKey.A10,FiscalActivityInfoKey.C10,FiscalActivityInfoKey.C11
				,FiscalActivityInfoKey.A11,FiscalActivityInfoKey.A13
		});
		// Producción de mejillón en batea.
		SPECIAL_EPIGRAPHS.put("---", new FiscalActivityInfoKey[] {
				FiscalActivityInfoKey.A01,FiscalActivityInfoKey.A02,FiscalActivityInfoKey.A03
				,FiscalActivityInfoKey.A04,FiscalActivityInfoKey.A05,FiscalActivityInfoKey.A10
				,FiscalActivityInfoKey.B06,FiscalActivityInfoKey.A11,FiscalActivityInfoKey.A13
		});			
	}
	private static final Map<FiscalActivityInfoKey, FiscalActivityInfoKey[]> DETAIL_MODULES = new HashMap<FiscalActivityInfoKey, FiscalActivityInfoKey[]>();
	static {
		// Claves de Info para todos los epigrafes, menos los indicados a continuación.
		DETAIL_MODULES.put(FiscalActivityInfoKey.M01, new FiscalActivityInfoKey[] { 
				 FiscalActivityInfoKey.M011
				,FiscalActivityInfoKey.M012
				,FiscalActivityInfoKey.M013
				,FiscalActivityInfoKey.M014
		});
		// Claves de Info para todos los epigrafes, menos los indicados a continuación.
		DETAIL_MODULES.put(FiscalActivityInfoKey.M02, new FiscalActivityInfoKey[] { 
				 FiscalActivityInfoKey.M021
				,FiscalActivityInfoKey.M022
				,FiscalActivityInfoKey.M023
				,FiscalActivityInfoKey.M024
				,FiscalActivityInfoKey.M025
		});
	}	
	
	private IFiscalActivityContainer fac;
	
	public Aeat2015ModuleCalculator(IFiscalActivityContainer fac) {
		this.fac = fac;
	}
	
	private IFiscalActivityContainer getFAC() {
		return fac;
	}
	private FiscalActivityInfo getKey(List<FiscalActivityInfo> list, FiscalActivityInfoKey key) {
		for (FiscalActivityInfo info : list) {
			if (info.getInfoKey() == key) {
				return info; 
			}
		}
		return null;
	}
	private FiscalActivityInfo getActivityInfoKey( FiscalActivityInfoKey key ) {
		return getKey(getFAC().getActivityInfoList(), key );
	}
	private FiscalActivityInfo getIrpfModulesKey( FiscalActivityInfoKey key ) {
		return getKey(getFAC().getIrpfModulesList(), key );
	}
	private FiscalActivityInfo getIrpfInfoKey( FiscalActivityInfoKey key ) {
		return getKey(getFAC().getIrpfInfoList(), key );
	}
	private FiscalActivityInfo getVatModulesKey( FiscalActivityInfoKey key ) {
		return getKey(getFAC().getVatModulesList(), key );
	}
	private FiscalActivityInfo getVatInfoKey( FiscalActivityInfoKey key ) {
		return getKey(getFAC().getVatInfoList(), key );
	}
	private FiscalActivityInfo getM311InfoKey( FiscalActivityInfoKey key ) {
		return getKey(getFAC().getM311List(), key );
	}

	@Override
	public void changeVatModule(FiscalActivityInfo info) throws AonException {
		for (FiscalActivityInfo irpfInfo : getFAC().getIrpfModulesList() ) {
			if ( info.getInfoKey() == irpfInfo.getInfoKey() ) {
				irpfInfo.setDoubleValue( info.getDoubleValue() );
			}
		}
		calculate();
	}
	
	@Override
	public void changeDetailModule(FiscalActivityInfo info) throws AonException {
		FiscalActivityInfo intoToDetail = getFAC().getInfoToDetail();
		calculateDetail(intoToDetail, getFAC().getModulesDetailList() );
		changeIrpfModule( intoToDetail );
	}

	private void calculateDetail(FiscalActivityInfo intoToDetail, List<FiscalActivityInfo> modulesDetailList) {
		if (intoToDetail.getInfoKey() == FiscalActivityInfoKey.M01) {
			intoToDetail.setDoubleValue( calculateDetailM01( modulesDetailList ) );
		}
		if (intoToDetail.getInfoKey() == FiscalActivityInfoKey.M02) {
			intoToDetail.setDoubleValue( calculateDetailM02( modulesDetailList ) );
		}
		
	}

	private Double calculateDetailM02(List<FiscalActivityInfo> modulesDetailList) {
		double m02 = 0.0;
		FiscalActivityInfo info01 = getIrpfModulesKey(FiscalActivityInfoKey.M01);
		double m01 = info01==null?0.0:info01.getDoubleValue();
		double a13 = getActivityInfoKey(FiscalActivityInfoKey.A13).getDoubleValue();
		//Horas anuales del titular.
		FiscalActivityInfo info021 = null;
		double m021 = 0.0;
		//Horas anuales del cónyuge.
		FiscalActivityInfo info022 = null;
		double m022 = 0.0;
		//Indique si el cónyuge es discapacitado en grado igual o superior al 33%
		double m023 = 0.0;
		//Horas anuales de los hijos menores de 18 años.
		double m024 = 0.0;
		//Horas anuales de los hijos menores de 18 años con discapacidad en grado igual o superior al 33%
		double m025 = 0.0;
		for (FiscalActivityInfo info : modulesDetailList) {
			if (info.getInfoKey() == FiscalActivityInfoKey.M021) {
				info021 = info;
				m021 = info.getDoubleValue();	
			} else if (info.getInfoKey() == FiscalActivityInfoKey.M022) {
				info022 = info;
				m022 = info.getDoubleValue();
			} else if (info.getInfoKey() == FiscalActivityInfoKey.M023) {
				m023 = info.getDoubleValue();
			} else if (info.getInfoKey() == FiscalActivityInfoKey.M024) {
				m024 = info.getDoubleValue();
			} else if (info.getInfoKey() == FiscalActivityInfoKey.M025) {
				m025 = info.getDoubleValue();
			}
		}
		
		// Personal no asalariado. Personal no asalariado es el empresario. 
		// También tendrán esta consideración, su cónyuge y los hijos menores 
		// que convivan con él, cuando, trabajando efectivamente en la actividad, 
		// no constituyan personal asalariado de acuerdo con lo establecido en 
		// la regla siguiente.

		// Se computará como una persona no asalariada el empresario. En aquellos 
		// supuestos que pueda acreditarse una dedicación inferior a 1.800 horas/año 
		// por causas objetivas, tales como jubilación, incapacidad, pluralidad de
		// actividades o cierre temporal de la explotación, se computará el tiempo 
		// efectivo dedicado a la actividad. En estos supuestos, para la cuantificación 
		// de las tareas de dirección, organización y planificación de la actividad y, en
		// general, las inherentes a la titularidad de la misma, se computará al 
		// empresario en 0,25 personas/año, salvo cuando se acredite una dedicación 
		// efectiva superior o inferior.

		// Para el resto de personas no asalariadas se computará como una persona 
		// no asalariada la que trabaje en la actividad al menos mil ochocientas horas/año.
		// Cuando el número de horas de trabajo al año sea inferior a mil ochocientas,  
		// se estimará como cuantía de la persona no asalariada la proporción existente  
		// entre número de horas efectivamente trabajadas en el año y mil ochocientas.
		
		// El personal no asalariado con un grado de minusvalía igual o superior al 33% 
		// se computará al 75 por 100.
		// Cuando el cónyuge o los hijos menores tengan la condición de no asalariados 
		// se computarán al 50 por 100, siempre que el titular de la actividad se compute 
		// por entero, antes de aplicar, en su caso, la reducción prevista en el párrafo anterior,
		// y no haya más de una persona asalariada. Esta reducción se practicará después de haber
		// aplicado, en su caso, la correspondiente por grado de minusvalía igual o superior al 33%.
		
		if (CommonUtil.round(m021) > 1800) {
			m021 = 1800;
			info021.setDoubleValue(m021);
		}
		boolean titularFullTime = CommonUtil.round(m021) == 1800;
		boolean moreThanOne = (CommonUtil.round(m01) > 1);
				
		if (CommonUtil.round(m021) != 0) {
			m02 = m021 / 1800;
			if (CommonUtil.round(a13) != 0) {
				m02 = m02 * 0.75;
			}
		}
		
		if (CommonUtil.round(m022) > 1800) {
			m022 = 1800;
			info022.setDoubleValue(m022);
		}
		if (CommonUtil.round(m022) != 0) {
			double d = (m022 / 1800);
			if (m023 == 1) {
				d = d * 0.75;
			}
			if (titularFullTime && !moreThanOne) {
				d = d / 2;
			}
			m02 = m02 + d;	
		}
		
		if (CommonUtil.round(m024) != 0) {
			double d = m024 / 1800;
			if (titularFullTime && !moreThanOne) {
				d = d/ 2;
			}
			m02 = m02 + d;	
		}
			
		if (CommonUtil.round(m025) != 0) {
			double d = ((m025 / 1800) * 0.75);
			if (titularFullTime && !moreThanOne) {
				d = d / 2;
			}
			m02 = m02 + d;	
		}
		return CommonUtil.floor(m02,2);
	}

	private Double calculateDetailM01(List<FiscalActivityInfo> modulesDetailList) {
		double m01 = 0.0;
		//Mayores de 19 años
		double m011 = 0.0;
		//Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
		double m012 = 0.0;
		//Discapacitados con grado de minusvalía igual o superior al 33 por 100
		double m013 = 0.0;
		//Horas anuales
		double m014 = 0.0;
		
		for (FiscalActivityInfo info : modulesDetailList) {
			if (info.getInfoKey() == FiscalActivityInfoKey.M011) {
				m011 = info.getDoubleValue();	
			} else if (info.getInfoKey() == FiscalActivityInfoKey.M012) {
				m012 = info.getDoubleValue();
			} else if (info.getInfoKey() == FiscalActivityInfoKey.M013) {
				m013 = info.getDoubleValue();
			} else if (info.getInfoKey() == FiscalActivityInfoKey.M014) {
				m014 = info.getDoubleValue();
			}
		}
		
		// Personal asalariado: Persona asalariada es cualquier otra que trabaje 
		// en la actividad. En particular, tendrán la consideración de personal 
		// asalariado el cónyuge y los hijos menores del sujeto pasivo que convivan
		// con él, siempre que, existiendo el oportuno contrato laboral y la afiliación 
		// al régimen general de la Seguridad Social, trabajen habitualmente y con 
		// continuidad en la actividad empresarial desarrollada por el contribuyente. 
		// No se computarán como personas asalariadas los alumnos de formación 
		// profesional específica que realicen el módulo obligatorio de formación 
		// en centros de trabajo. 
		// Se computará como una persona asalariada la que  trabaje el número de 
		// horas anuales por trabajador fijado en el convenio colectivo correspondiente 
		// o, en su defecto, mil ochocientas horas/año. Cuando el número de horas de 
		// trabajo al año sea inferior o superior, se estimará como cuantía de la 
		// persona asalariada la proporción existente entre el número de horas 
		// efectivamente trabajadas y las fijadas en el convenio colectivo o, en 
		// su defecto, mil ochocientas. 
		// Se computará en un 60 por 100 al personal asalariado menor de diecinueve 
		// años y al que preste sus servicios bajo un contrato de aprendizaje o 
		// para la formación. Cuando el personal asalariado sea una persona con 
		// discapacidad, con grado de minusvalía igual o superior al 33 %, se 
		// computará en un 40 por 100. Estas reducciones serán incompatibles entre sí.
		
		if (CommonUtil.round(m014) == 0) {
			m014 = 1800;
		}
		if (CommonUtil.round(m011) != 0) {
			double d = CommonUtil.round(m011 / m014);
			m01 = d;	
		}
		if (CommonUtil.round(m012) != 0) {
			double d = CommonUtil.round((m012 / m014) * 0.60);
			m01 = m01 + d;
		}
		if (CommonUtil.round(m013) != 0) {
			double d = CommonUtil.round((m013 / m014) * 0.40);
			m01 = m01 + d;
		}
		return CommonUtil.round(m01);
	}

	@Override
	public void changeIrpfModule(FiscalActivityInfo info) throws AonException {
		// Para el caso especial del personal asalariado.
		// Las claves M01 y M02 de IRPF se suman a la M26 de IVA.
		if (info.getInfoKey() == FiscalActivityInfoKey.M01 || info.getInfoKey() == FiscalActivityInfoKey.M02) {
			Double personal = info.getDoubleValue();
			FiscalActivityInfo info2 = getIrpfModulesKey(
					(info.getInfoKey() == FiscalActivityInfoKey.M01?FiscalActivityInfoKey.M02:FiscalActivityInfoKey.M01));
			personal = CommonUtil.round(personal + info2.getDoubleValue());
			FiscalActivityInfo vatInfo = getVatModulesKey(FiscalActivityInfoKey.M26);
			if (vatInfo != null) {
				vatInfo.setDoubleValue( personal );	
			}
		} else {
			// Para todos los demás módulos.
			for (FiscalActivityInfo vatInfo : getFAC().getVatModulesList() ) {
				if ( info.getInfoKey() == vatInfo.getInfoKey() ) {
					vatInfo.setDoubleValue( info.getDoubleValue() );
				}
			}
		}
		calculate();
	}
	
	@Override
	public void calculate( ) throws AonException {
		if (getFAC().getIrpfModulesList() != null && getFAC().getIrpfModulesList().size() > 0) {
			calculateIrpf( );
		}
		if (getFAC().getVatModulesList() != null && getFAC().getVatModulesList().size() > 0) {
			calculateVat( );
		}
		if (getFAC().getM311List() != null && getFAC().getM311List().size() > 0) {
			calculateM311();
		}
	}

	@Override
	public void calculateIrpf( ) {
		
		// *****************************************************************
		// 					RENDIMIENTO NETO PREVIO
		// *****************************************************************
		double i01= 0.0;
		for (FiscalActivityInfo vatInfo : getFAC().getIrpfModulesList() ) {
			i01 = CommonUtil.round(i01 + vatInfo.getBase() );
		}
		getIrpfInfoKey(FiscalActivityInfoKey.I01).setDoubleValue(i01);
		
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

		FiscalActivity fa = getFAC().getFiscalActivity();
		
		FiscalActivityInfoKey[] persoKeys = {FiscalActivityInfoKey.M01,FiscalActivityInfoKey.M15,FiscalActivityInfoKey.M16};
		double personalAsalariado = 0;
		for (FiscalActivityInfoKey key : persoKeys ){
			FiscalActivityInfo persoAsal = getIrpfModulesKey(key);
			if (persoAsal != null) {
				personalAsalariado = personalAsalariado + persoAsal.getDoubleValue();
			}
		}

		double i02 = 0;
		double coef = 0.0;
		double as = personalAsalariado;
		FiscalActivityInfo infoA10 = getActivityInfoKey(FiscalActivityInfoKey.A10);
		double previousAsalariados = infoA10 == null? 0 : infoA10.getDoubleValue();
		if( CommonUtil.round(previousAsalariados) > 0 && CommonUtil.round((personalAsalariado - previousAsalariados)) > 0 ) {
			as = CommonUtil.round(personalAsalariado - previousAsalariados);
			coef = CommonUtil.round( as * 0.40 );
		}
		if (CommonUtil.round(as) > 0.0) {
			coef = coef + AonMathUtils.round( (as>1?1:as) * 0.10 );
			as = CommonUtil.round(as - 1);
		}
		if (CommonUtil.round(as) > 0.0) {
			coef = coef + AonMathUtils.round( (as>2?2:as) * 0.15 );
			as = CommonUtil.round(as - 2);
		}
		if (CommonUtil.round(as) > 0.0) {
			coef = coef + AonMathUtils.round( (as>2?2:as) * 0.20 );
			as = CommonUtil.round(as - 2);
		}
		if (CommonUtil.round(as) > 0.0) {
			coef = coef + AonMathUtils.round( (as>3?3:as) * 0.25 );
			as = CommonUtil.round(as - 3);
		}
		if (CommonUtil.round(as) > 0.0) {
			coef = coef + AonMathUtils.round( as * 0.30 );
		}
		if (coef != 0 ) {
			for (FiscalActivityInfoKey key : persoKeys ){
				FiscalActivityInfo persoAsal = getIrpfModulesKey(key);
				if (persoAsal != null) {
					double m01 = persoAsal.getDoubleValue();
					double ratioPersonalAsalariado = 
							(personalAsalariado != 0 )?m01 / personalAsalariado:1;
					double f = persoAsal.getFactor();
					i02 = CommonUtil.round(i02 + (coef * ratioPersonalAsalariado * f)); 
				}
					
			}
		}
		getIrpfInfoKey(FiscalActivityInfoKey.I02).setDoubleValue(i02);		
	
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participación.
		FiscalActivityInfo info = getActivityInfoKey(FiscalActivityInfoKey.A02);
		double a02 = info!=null?info.getDoubleValue():0.0;
		// Ejerce la actividad en un sólo local o sin él.
		info = getActivityInfoKey(FiscalActivityInfoKey.A06);
		double a06 = info!=null?info.getDoubleValue():0.0;
		// Número de vehículos afectos de la actividad.
		info = getActivityInfoKey(FiscalActivityInfoKey.A07);
		double a07 = info!=null?info.getDoubleValue():0.0;
		// Capacidad de carga del vehículo superior a 1000 Kg.
		info = getActivityInfoKey(FiscalActivityInfoKey.A08);
		double a08 = info!=null?info.getDoubleValue():0.0;
		// Municipio donde se ejerce la actividad.
		info = getActivityInfoKey(FiscalActivityInfoKey.A09);
		double a09 = info!=null?info.getDoubleValue():0.0;
		
		// *****************************************************************
		// 				RENDIMIENTO NETO MINORADA
		// *****************************************************************
		double i03 = getIrpfInfoKey(FiscalActivityInfoKey.I03).getDoubleValue();
		double i04 = CommonUtil.round(i01 - i02 - i03 );
		getIrpfInfoKey(FiscalActivityInfoKey.I04).setDoubleValue(i04);
		
		// *****************************************************************
		// 				Índice corrector Especial
		// *****************************************************************
		// Los índices correctores especiales sólo se aplicarán en aquellas 
		// actividades concretas que se citan a continuación:
		double i06 = 0.0;
		boolean indiceEmpresasPequeñaDimensionAplicable = true; 
		if ("659.4".equals(fa.getEpigraph())) {
			// Actividad de comercio al por menor de prensa, revistas y libros 
			// en quioscos situados en la vía pública:
			//	Ubicación de los quioscos					Índice
			//  --------------------------------------------------
			//	Madrid y Barcelona							  1,00
			//	Municipios de más de 100.000 habitantes		  0,95
			//	Resto de municipios							  0,80
			if (CommonUtil.round(a09) == 7.0) {
				i06 = 1.0;
			} else if (CommonUtil.round(a09) == 6.0) {
				i06 = 0.95;
			} else {
				i06 = 0.80;
			}
		} else if ("721.1".equals(fa.getEpigraph()) || "721.3".equals(fa.getEpigraph())) {
			// Actividad de transporte urbano colectivo y de viajeros por carretera:
			// Se aplicará el índice 0,80 cuando el titular disponga de un único vehículo.
			if (CommonUtil.round(a07) == 1.0) {
				i06 = 0.80;
				indiceEmpresasPequeñaDimensionAplicable = false;	
			}
		} else if ("721.2".equals(fa.getEpigraph())) {
			//	Actividad de transporte por autotaxis.
			//		Población del municipio				  Índice
			//		--------------------------------------------
			//		Hasta 2.000 habitantes					0,75
			//		De 2.001 hasta 10.000 habitantes		0,80
			//		De 10.001 hasta 50.000 habitantes		0,85
			//		De 50.001 hasta 100.000 habitantes		0,90
			//		Más de 100.000 habitantes				1,00
			indiceEmpresasPequeñaDimensionAplicable = false;
			if (CommonUtil.round(a09) == 1.0) {
				i06 = 0.75;
			} else if (CommonUtil.round(a09) == 2.0 || CommonUtil.round(a09) == 3.0) {
				i06 = 0.80;
			} else if (CommonUtil.round(a09) == 4.0) {
				i06 = 0.85;
			} else if (CommonUtil.round(a09) == 5.0) {
				i06 = 0.90;
			} else {
				i06 = 1.00;
			}
		} else if ("722".equals(fa.getEpigraph()) || "757".equals(fa.getEpigraph())) {
			//Actividades de transporte de mercancías por carretera y servicios de mudanzas:
			// Se aplicará el índice 0,80 cuando el titular disponga de un único vehículo.
			// Se aplicará el índice 0,90 cuando la actividad se realice con tractocamiones
			// y el titular carezca de semirremolques. Cuando la actividad se desarrolle con 
			// un único tractocamión y sin semirremolques, se aplicará, exclusivamente, el índice 0,75.

			if (CommonUtil.round(a07) == 1.0) {
				i06 = 0.80;
				indiceEmpresasPequeñaDimensionAplicable = false;
			}
			// Indique si la actividad se realiza con un único tractocamión y sin semirremolques.
			// Cuando la actividad se desarrolle con un único tractocamión y sin
			// semirremolques, se aplicará, exclusivamente, el índice 0,75
			double c11 = getActivityInfoKey(FiscalActivityInfoKey.C11).getDoubleValue();
			if (CommonUtil.round(c11) == 1.0) {
				i06 = 0.75;
				indiceEmpresasPequeñaDimensionAplicable = false;
			} else {
				// Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.
				double c10 = getActivityInfoKey(FiscalActivityInfoKey.C10).getDoubleValue();
				if (CommonUtil.round(c10) == 1.0) {
					i06 = 0.90;
					indiceEmpresasPequeñaDimensionAplicable = false;
				}
			}
		} else if ("---".equals(fa.getEpigraph())) {
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
			double b06 = getActivityInfoKey(FiscalActivityInfoKey.B06).getDoubleValue();
			if (CommonUtil.round(b06) == 1.0) {
				i06 = 0.75;
			} else if (CommonUtil.round(b06) == 2.0) {
				i06 = 0.85;
			} else if (CommonUtil.round(b06) == 3.0 || CommonUtil.round(b06) == 5.0) {
				i06 = 0.90;
			} else if (CommonUtil.round(b06) == 4.0 || CommonUtil.round(b06) == 6.0) {
				i06 = 0.95;
			} else {
				// Otros: numero de bateas, barcos o TRB distintos de los anteriores.
				indiceEmpresasPequeñaDimensionAplicable = true;	
			}
		}
		getIrpfInfoKey(FiscalActivityInfoKey.I06).setDoubleValue(i06);
		
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
			if (CommonUtil.round(a06) == 1.0) {
				if (CommonUtil.round(a07) <= 1.0) {
					if (CommonUtil.round(a08) == 0.0) {
						i07 = 0.7; 
						if (CommonUtil.round(a09) == 2.0) {
							i07 = 0.75;
						} else if (CommonUtil.round(a09) >= 3.0) {
							i07 = 0.80;
						}
						double a10 = 0;
						if (infoA10 != null && infoA10.getDoubleValue() != 0) {
							a10 = infoA10.getDoubleValue();
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
		getIrpfInfoKey(FiscalActivityInfoKey.I07).setDoubleValue(i07);
		
		// *****************************************************************
		// Índice corrector de temporada
		// *****************************************************************
		// Actividad de Temporada. nº de dias de ejercicio en el año anterior.
		double a03 = getActivityInfoKey(FiscalActivityInfoKey.A03).getDoubleValue();
		double i08 = 0.0;
		if (CommonUtil.round(a03) > 0.0 && CommonUtil.round(a03) <=60.0) {
			i08 = 1.5;
		} else if (CommonUtil.round(a03) > 60.0 && CommonUtil.round(a03) <= 120.0) {
			i08 = 1.35;
		} else if (CommonUtil.round(a03) > 120.0 && CommonUtil.round(a03) <= 180.0) {
			i08 = 1.25;
		}
		boolean indiceTemporadaAplicado = (i08!=0.0);
		getIrpfInfoKey(FiscalActivityInfoKey.I08).setDoubleValue(i08);
		
		// *****************************************************************
		// Índice corrector de exceso
		// *****************************************************************
		
		// Cuando resulte aplicable el índice corrector para empresas de pequeña 
		// dimensión (b.1) no se aplicará el índice corrector de exceso
		double i09 = 0.0;
		if (!indiceEmpresasPequeñaDimensionAplicado) {
			double tope = fa.getMaxImport();
			double baseIndice = i04;
			// Aplicamos los índices correctores anteriores
			if (CommonUtil.round(i06) != 0.0) {
				baseIndice = CommonUtil.round(baseIndice * i06);	
			}
			if (CommonUtil.round(i07) != 0.0) {
				baseIndice = CommonUtil.round(baseIndice * i07);	
			}
			if (CommonUtil.round(i08) != 0.0) {
				baseIndice = CommonUtil.round(baseIndice * i08);	
			}
			if (baseIndice > tope) {
				 i09 = 1.3;
			}
		}
		getIrpfInfoKey(FiscalActivityInfoKey.I09).setDoubleValue(i09);
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
			if (CommonUtil.round(a06) == 1.0) {
				// Indique si el titular es discapacitado en grado igual o superior al 33%
				double a13 = getActivityInfoKey(FiscalActivityInfoKey.A13).getDoubleValue();
				// Nuevas actividades iniciadas a partir del 1 de enero del año anterior. Año de inicio.
				double a04 = getActivityInfoKey(FiscalActivityInfoKey.A04).getDoubleValue();
				if (CommonUtil.round(a04) != 0.0) {
					// Primero
					if (fa.getYear() == (int) a04) {
						i10 = (CommonUtil.round(a13)==0.0)?0.80:0.60; 
					}
					// Segundo
					if (( fa.getYear() - 1) == (int) a04) {
						i10 = (CommonUtil.round(a13)==0.0)?0.90:0.70;
					}
				}
			}
		}
		getIrpfInfoKey(FiscalActivityInfoKey.I10).setDoubleValue(i10);		
	
		// *****************************************************************
		// 		RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (i.R.P.F.)
		// *****************************************************************
		double i11 = 0.0;
		i11 = i04;
		if (CommonUtil.round(i06) != 0.0) {
			i11 = i04 * i06;	
		}
		if (CommonUtil.round(i07) != 0.0) {
			i11 = i11 * i07;	
		}
		if (CommonUtil.round(i08) != 0.0) {
			i11 = i11 * i08;	
		}
		if (CommonUtil.round(i09) != 0.0) {
			double baseExceso = i11 - fa.getMaxImport();
			baseExceso = baseExceso * i09; 
			i11 = baseExceso + fa.getMaxImport();	
		}
		if (CommonUtil.round(i10) != 0.0) {
			i11 = i11 * i10;	
		}
		// Disposición adicional primera. 
		//	Reducción en 2013 del rendimiento neto calculado por el método de estimación objetiva.
		//	Los contribuyentes que determinen el rendimiento neto de sus actividades
		//	económicas por el método de estimación objetiva, podrán reducir el rendimiento 
		//	neto de módulos obtenido en 2013 en un 5 por 100.
		i11 = i11 - (i11 * 5 / 100);
		
		
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participación.
		if (CommonUtil.round(a02) != 0.0) {
			i11 = i11 - (i11 * a02 / 100);
			
		}
		i11 = CommonUtil.round(i11);
		getIrpfInfoKey(FiscalActivityInfoKey.I11).setDoubleValue(i11);		
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
		double a13 = getActivityInfoKey(FiscalActivityInfoKey.A11).getDoubleValue();
		if (CommonUtil.round(a13) != 0.0) {
			i12 = CommonUtil.round(i11 * 20 / 100); 	
		}
		getIrpfInfoKey(FiscalActivityInfoKey.I12).setDoubleValue(i12);
		
		// *****************************************************************
		// Rendimientos a efectos de pagos fraccionados después de la reducción
		// *****************************************************************
		double i13 = CommonUtil.round(i11 - i12);
		getIrpfInfoKey(FiscalActivityInfoKey.I13).setDoubleValue(i13);
		
		
		// *****************************************************************
		// Resultado. Pago Trimestral.
		// *****************************************************************
		double a10 = personalAsalariado;
		if (infoA10 != null && infoA10.getDoubleValue() != 0) {
			a10 = infoA10.getDoubleValue();
		}
		double i14 = 4.0;
		if (CommonUtil.round(a10) <= 1.0) i14 = 3.0; 
		if (CommonUtil.round(a10) == 0.0) i14 = 2.0;
		double i15 = CommonUtil.round(i13 *  i14 / 100 );
		getIrpfInfoKey(FiscalActivityInfoKey.I14).setDoubleValue(i14);
		getIrpfInfoKey(FiscalActivityInfoKey.I15).setDoubleValue(i15);
	}

	@Override
	public void calculateVat() {
		// *****************************************
		// Indice corrector de Temporada
		// *****************************************
		double a03 = getActivityInfoKey(FiscalActivityInfoKey.A03).getDoubleValue();
		double v01 = 0.0;
		if (CommonUtil.round(a03) > 0.0 && CommonUtil.round(a03) <=60.0) {
			v01 = 1.5;
		} else if (CommonUtil.round(a03) > 60.0 && CommonUtil.round(a03) <= 120.0) {
			v01 = 1.35;
		} else if (CommonUtil.round(a03) > 120.0 && CommonUtil.round(a03) <= 180.0) {
			v01 = 1.25;
		}
		getVatInfoKey(FiscalActivityInfoKey.V01).setDoubleValue(v01);
		
		// *****************************************************************
		// Cuota anual devengada por operaciones corrientes
		// *****************************************************************
		double vatBase = 0.0;
		for (FiscalActivityInfo vatInfo : getFAC().getVatModulesList() ) {
			vatBase = CommonUtil.round(vatBase + vatInfo.getBase() );
		}
		double v02 = CommonUtil.round(v01) == 0.0? vatBase : CommonUtil.round(vatBase * v01); 
		getVatInfoKey(FiscalActivityInfoKey.V02).setDoubleValue(v02);
		
		
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
		double a13 = getActivityInfoKey(FiscalActivityInfoKey.A11).getDoubleValue();
		if (CommonUtil.round(a13) != 0.0) {
			v03 = CommonUtil.round(v02 * 20 / 100); 	
		}
		getVatInfoKey(FiscalActivityInfoKey.V03).setDoubleValue(v03);
		
		// *****************************************************************
		// Cuota anual por operaciones corrientes después de las reducciones anteriores
		// *****************************************************************
		double v04 = CommonUtil.round(v02 - v03);
		getVatInfoKey(FiscalActivityInfoKey.V04).setDoubleValue(v04);
		
		// *****************************************************************
		// Porcentaje aplicable
		// *****************************************************************

		double v05 = getVatInfoKey(FiscalActivityInfoKey.V05).getDoubleValue();
		
		// *****************************************************************
		// INGRESO A CUENTA POR OPERACIONES CORRIENTES PREVIO
		// *****************************************************************
		double v06 = CommonUtil.round(v04 *  v05 / 100); 
		getVatInfoKey(FiscalActivityInfoKey.V06).setDoubleValue(v06);
		
	}

	public boolean isSpecialEpigraph(String epigraph) {
		return SPECIAL_EPIGRAPHS.containsKey(epigraph);
	}

	@Override
	public FiscalActivityInfoKey[] getActivityKeys(int year, String epigraph) {
		boolean special = SPECIAL_EPIGRAPHS.containsKey(epigraph);
		return special?SPECIAL_EPIGRAPHS.get(epigraph):SPECIAL_EPIGRAPHS.get(null);
	}

	@Override
	public FiscalActivityInfoKey[] getDetailedKeys(FiscalActivityInfoKey key) {
		return DETAIL_MODULES.get(key);
	}

	@Override
	public void calculateFarmerM311() throws AonException {
		FiscalActivityInfo y01Info = getM311InfoKey(FiscalActivityInfoKey.Y01);
		double y01= y01Info==null?0:y01Info.getDoubleValue();
		
		FiscalActivity fa = getFAC().getFiscalActivity();
		double y02 = 0;
		if ("01".equals(fa.getEpigraph())) {
			y02 = 0.10; 
		} else if ("02".equals(fa.getEpigraph())) {
			y02 = 0.04;
		} else if ("03".equals(fa.getEpigraph())) {
			y02 = 0.10;
		} else if ("04".equals(fa.getEpigraph())) {
			y02 = 0.10;
		} else if ("05".equals(fa.getEpigraph())) {
			y02 = 0.10;
		} else if ("06".equals(fa.getEpigraph())) {
			y02 = 0.06625;
		} else if ("07".equals(fa.getEpigraph())) {
			y02 = 0.07;
		} else if ("08".equals(fa.getEpigraph())) {
			y02 = 0.10;
		} else if ("09".equals(fa.getEpigraph())) {
			y02 = 0.21;
		} else if ("10".equals(fa.getEpigraph())) {
			y02 = 0.04;
		} else if ("11".equals(fa.getEpigraph())) {
			y02 = 0.07625; 
		} else if ("12".equals(fa.getEpigraph())) {
			y02 = 0.21;
		} else if ("13".equals(fa.getEpigraph())) {
			y02 = 0.21;
		} else if ("14".equals(fa.getEpigraph())) {
			y02 = 0.07;
		} else if ("15".equals(fa.getEpigraph())) {
			y02 = 0.26750;
		} else if ("16".equals(fa.getEpigraph())) {
			y02 = 0.26750;
		} else if ("17".equals(fa.getEpigraph())) {
			y02 = 0.19625;
		}
		getM311InfoKey(FiscalActivityInfoKey.Y02).setDoubleValue(y02);
		
		double y03 = CommonUtil.round(y01 * y02);
		getM311InfoKey(FiscalActivityInfoKey.Y03).setDoubleValue(y03);
		
		FiscalActivityInfo y04Info = getM311InfoKey(FiscalActivityInfoKey.Y04);
		double y04= y04Info==null?0:y04Info.getDoubleValue();

		FiscalActivityInfo y05Info = getM311InfoKey(FiscalActivityInfoKey.Y05);
		double y05= y05Info==null?0:y05Info.getDoubleValue();

		double y06 = CommonUtil.round(y03 * 1 / 100);
		getM311InfoKey(FiscalActivityInfoKey.Y06).setDoubleValue(y06);
		
		double y07 = CommonUtil.round(y04 + y05 +y06);
		getM311InfoKey(FiscalActivityInfoKey.Y07).setDoubleValue(y07);
		
		double y08 = CommonUtil.round(y03 - y07);
		getM311InfoKey(FiscalActivityInfoKey.Y08).setDoubleValue(y08);
	}
	
	@Override
	public void calculateM311() throws AonException {
		FiscalActivityInfo v04 = getVatInfoKey(FiscalActivityInfoKey.V04);
		
		double x00 = v04==null?0:v04.getDoubleValue();

		getM311InfoKey(FiscalActivityInfoKey.X00).setDoubleValue(x00);
		
		double x01 = getM311InfoKey(FiscalActivityInfoKey.X01).getDoubleValue();
		double x02 = getM311InfoKey(FiscalActivityInfoKey.X02).getDoubleValue();
		
		double x03 = CommonUtil.round(x00 * 1 / 100);
		getM311InfoKey(FiscalActivityInfoKey.X03).setDoubleValue(x03);
		
		FiscalActivityInfo a11Info = getActivityInfoKey(FiscalActivityInfoKey.A11);
		double a11 = a11Info==null?0:a11Info.getDoubleValue();
		double x04 = 0; 
		if (CommonUtil.round(a11) != 0.0) {
			x04 = CommonUtil.round(x00 * 20 / 100); 	
		}
		getM311InfoKey(FiscalActivityInfoKey.X04).setDoubleValue(x04);

		
		double x05 = CommonUtil.round(x01 + x02 + x03);
		getM311InfoKey(FiscalActivityInfoKey.X05).setDoubleValue(x05);
		
		// *****************************************
		// Indice corrector de Temporada
		// *****************************************
		FiscalActivityInfo a03Info = getActivityInfoKey(FiscalActivityInfoKey.A03);
		double a03 = a03Info==null?0:a03Info.getDoubleValue();
		double x06 = 0.0;
		if (CommonUtil.round(a03) > 0.0 && CommonUtil.round(a03) <=60.0) {
			x06 = 1.5;
		} else if (CommonUtil.round(a03) > 60.0 && CommonUtil.round(a03) <= 120.0) {
			x06 = 1.35;
		} else if (CommonUtil.round(a03) > 120.0 && CommonUtil.round(a03) <= 180.0) {
			x06 = 1.25;
		}
		getM311InfoKey(FiscalActivityInfoKey.X06).setDoubleValue(x06);
		
		double x07 = CommonUtil.round(x00 - x05 - x04);
		if (x06 > 0) {
			x07 = CommonUtil.round(x07 * x06);	
		}
		getM311InfoKey(FiscalActivityInfoKey.X07).setDoubleValue(x07);
		
		
		FiscalActivity fa = getFAC().getFiscalActivity();
		Modules modules = new Modules();
		double x08 = modules.getCuotaMin(fa.getEpigraph());
		getM311InfoKey(FiscalActivityInfoKey.X08).setDoubleValue(x08);
		
		double x09 = getM311InfoKey(FiscalActivityInfoKey.X09).getDoubleValue();
		double x10 = CommonUtil.round(((x00 - x04)* x08 / 100));
		if (x06 > 0) {
			x10 = CommonUtil.round(x10 * x06);	
		}
		x10 = CommonUtil.round(x10 + x09);
		getM311InfoKey(FiscalActivityInfoKey.X10).setDoubleValue(x10);
		
		double x11 = (x10>x07)?x10:x07; 
		getM311InfoKey(FiscalActivityInfoKey.X11).setDoubleValue(x11);
		
	}
}
