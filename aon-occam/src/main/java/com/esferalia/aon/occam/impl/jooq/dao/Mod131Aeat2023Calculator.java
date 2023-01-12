package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod131Aeat2023Calculator  {
	
	private static final double DEFAULT_YEAR_HOURS = 1800;
	
	public static Mod131Activity calculate(AONContext ctx, Mod131Activity act) {
		calculateModules(ctx,act);
		calcRendimientoNetoPrevio(ctx,act);
		calcIncentivosAlEmpleo(ctx,act);
		calcRendimientoNetoMinorado(ctx,act);
		calcIndiceCorrectorEspecial(ctx,act);
		calcIndiceCorrectorEmpresaPequenaDimension(ctx,act);
		calcIndiceCorrectorTemporada(ctx,act);
		calcIndiceCorrectorExceso(ctx,act);
		calcIndiceCorrectorNuevaActividad(ctx,act);
		calcRendimientoEfectosPagoFraccionado(ctx,act);
		calcReduccionLorca(ctx,act);
		calcRendimientoEfectosPagoFraccionadoDespuesReduccion(ctx,act);
		calcResultadoPagoTrimestral(ctx,act);
		return act;
	}
	
	private static void calculateModules(AONContext ctx, Mod131Activity act) {
		for (Mod131ActivityModule mod : act.getModules()) {
			if (mod.isSalariedStaff() || AonStringUtils.contains(mod.getDescription(),"PERSONAL ASALARIADO")) {
				calculateSalariedStaff(ctx,mod);
			}
			if (mod.isNoSalariedStaff() || AonStringUtils.contains(mod.getDescription(),"PERSONAL NO ASALARIADO")) {
				calculateNoSalariedStaff(ctx,act,mod);		
			}
			mod.setResult( AonMathUtils.round(mod.getValue() * mod.getFactor() ) );
		}
		
	}

	private static void calculateSalariedStaff(AONContext ctx, Mod131ActivityModule act) {
		double salariedStaff = 0.0;
		// Mayores de 19 años
		double may19Hours = act.getMay19Hours();
		// Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
		double men19Hours = act.getMen19Hours();
		// Discapacitados con grado de minusvalía igual o superior al 33 por 100
		double disHours = act.getDisHours();
		// Horas anuales
		double yearHours = act.getYearHours();
		
		if (AonMathUtils.isZero(yearHours)) {
			yearHours = DEFAULT_YEAR_HOURS;
		}
		if (AonMathUtils.isNotZero(may19Hours)) {
			salariedStaff = AonMathUtils.round(may19Hours / yearHours);	
		}
		if (AonMathUtils.isNotZero(men19Hours)) {
			salariedStaff = salariedStaff + AonMathUtils.round((men19Hours / yearHours) * 0.60);
		}
		if (AonMathUtils.isNotZero(disHours)) {
			salariedStaff = salariedStaff + AonMathUtils.round((disHours / yearHours) * 0.40);
		}
	}
	
	private static void calculateNoSalariedStaff(AONContext ctx, Mod131Activity act, Mod131ActivityModule mod) {
		double noSalariedStaff = 0.0;
		// Horas anuales del titular.
		double ownerHours = mod.getOwnerHours();
		// Horas anuales del cónyuge.
		double spouseHours = mod.getSpouseHours();
		// Horas anuales de los hijos menores de 18 años.
		double childMen18Hours = mod.getChildMen18Hours();
		// Horas anuales de los hijos menores de 18 años con discapacidad en grado igual o superior al 33%
		double childDisHours = mod.getChildDisHours();
		
		boolean titularFullTime = AonMathUtils.round(ownerHours) >= DEFAULT_YEAR_HOURS;
		boolean moreThanOne = (AonMathUtils.round(spouseHours + childMen18Hours + childDisHours) > DEFAULT_YEAR_HOURS);
		if (AonMathUtils.isNotZero(ownerHours)) {
			noSalariedStaff = (ownerHours>=DEFAULT_YEAR_HOURS?DEFAULT_YEAR_HOURS:ownerHours) / DEFAULT_YEAR_HOURS;
		}
		if (AonMathUtils.isNotZero(spouseHours)) {
			double d = ((spouseHours>=DEFAULT_YEAR_HOURS?DEFAULT_YEAR_HOURS:spouseHours) / DEFAULT_YEAR_HOURS);
			// Indique si el cónyuge es discapacitado en grado igual o superior al 33%
			if ( mod.isSpouseDis() ) {
				d = AonMathUtils.round(d * 0.75);
			}
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d / 2);
			}
			noSalariedStaff = noSalariedStaff + d;	
		}
		if (AonMathUtils.isNotZero(childMen18Hours)) {
			double d = AonMathUtils.round(childMen18Hours / 1800);
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d/ 2);
			}
			noSalariedStaff = noSalariedStaff + d;	
		}
		if (AonMathUtils.isNotZero(childDisHours)) {
			double d = ((childDisHours / 1800) * 0.75);
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d / 2);
			}
			noSalariedStaff = noSalariedStaff + d;	
		}
	}

	// **************************************************************************
	
	private static void calcRendimientoNetoPrevio(AONContext ctx, Mod131Activity act) {
		double rnp= 0.0;
		for (Mod131ActivityModule mod : act.getModules()) {
			rnp = AonMathUtils.round(rnp + mod.getResult());
		}
		act.setRnp(rnp);
	}
	
	private static void calcIncentivosAlEmpleo(AONContext ctx, Mod131Activity act) {
		double salariedStaff = 0.0;
		for (Mod131ActivityModule mod : act.getModules()) {
			if (mod.isSalariedStaff() || AonStringUtils.contains(mod.getDescription(),"PERSONAL ASALARIADO")) {
				salariedStaff = AonMathUtils.round(salariedStaff + mod.getValue());
			}
		}

		double coef = 0.0;
		double iem = 0;
		double as = salariedStaff;
//		if (act.getEmp() != 0 && salariedStaff >= act.getEmp() ) {
//			as = AonMathUtils.round(as - act.getEmp());
//			coef = AonMathUtils.round( as * 0.40 );
//		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>1?1:as) * 0.10 );	
			as = AonMathUtils.round(as - 1);	
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>2?2:as) * 0.15 );
			as = AonMathUtils.round(as - 2);
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>2?2:as) * 0.20 );
			as = AonMathUtils.round(as - 2);
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>3?3:as) * 0.25 );
			as = AonMathUtils.round(as - 3);
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( as * 0.30 );
			as = 0;
		}
		
		if (coef != 0 ) {
			for (Mod131ActivityModule mod : act.getModules()) {
				if (mod.isSalariedStaff() || AonStringUtils.contains(mod.getDescription(),"PERSONAL ASALARIADO")) {
					double m01 = mod.getValue();
					double ratioPersonalAsalariado = (salariedStaff != 0 )?m01 / salariedStaff:1;
					iem = AonMathUtils.round(iem + (coef * ratioPersonalAsalariado * mod.getFactor())); 
				}
			}
		}
		act.setIem(iem);
	}
	
	private static void calcRendimientoNetoMinorado(AONContext ctx, Mod131Activity act) {
		act.setRnm(AonMathUtils.round(act.getRnp() - act.getIem() - act.getIin()));
	}
	
	private static void calcIndiceCorrectorEspecial(AONContext ctx, Mod131Activity act) {
		// N?mero de vehículos afectos de la actividad.
		double veh = act.getVeh();
		// Municipio donde se ejerce la actividad.
		double mun = act.getMun();
		
		
		// Los índices correctores especiales sólo se aplicarán en aquellas 
		// actividades concretas que se citan a continuación:
		double ic1 = 0.0;
		act.setIndiceEmpresasPequenaDimensionAplicable(true); 
		if (Epigraph.E_659_4B.getEpigraph().equals(act.getEpigraph())) {

			// PREGUNTA para diferenciar de Epigraph.E_659_4A, puesto que el epigrafe es el mismo 
			if (act.getModules() != null && act.getModules().size() == 4) {
				
				// Actividad de comercio al por menor de prensa, revistas y libros 
				// en quioscos situados en la v?a p?blica:
				//	Ubicaci?n de los quioscos					?ndice
				//  --------------------------------------------------
				//	Madrid y Barcelona							  1,00
				//	Municipios de m?s de 100.000 habitantes		  0,95
				//	Resto de municipios							  0,80
				if (AonMathUtils.round(mun) == 6.0) {
					ic1 = 1.0;
				} else if (AonMathUtils.round(mun) == 5.0) {
					ic1 = 0.95;
				} else {
					ic1 = 0.80;
				}
			}
		} else if (Epigraph.E_721_1.getEpigraph().equals(act.getEpigraph()) 
				|| Epigraph.E_721_3.getEpigraph().equals(act.getEpigraph())) {
			// Actividad de transporte urbano colectivo y de viajeros por carretera:
			// Se aplicar? el ?ndice 0,80 cuando el titular disponga de un ?nico veh?culo.
			if (AonMathUtils.round(veh) == 1.0) {
				ic1 = 0.80;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);	
			}
		} else if (Epigraph.E_721_2.getEpigraph().equals(act.getEpigraph())) {
			//	Actividad de transporte por autotaxis.
			//		Poblaci?n del municipio				  ?ndice
			//		--------------------------------------------
			//		Hasta 2.000 habitantes					0,75
			//		De 2.001 hasta 10.000 habitantes		0,80
			//		De 10.001 hasta 50.000 habitantes		0,85
			//		De 50.001 hasta 100.000 habitantes		0,90
			//		M?s de 100.000 habitantes				1,00
			act.setIndiceEmpresasPequenaDimensionAplicable(false);
			if (AonMathUtils.round(mun) == 0.0) {
				ic1 = 0.75;
			} else if (AonMathUtils.round(mun) == 1.0 || AonMathUtils.round(mun) == 2.0) {
				ic1 = 0.80;
			} else if (AonMathUtils.round(mun) == 3.0) {
				ic1 = 0.85;
			} else if (AonMathUtils.round(mun) == 4.0) {
				ic1 = 0.90;
			} else {
				ic1 = 1.00;
			}
		} else if (Epigraph.E_722A.getEpigraph().equals(act.getEpigraph())) {
			//Actividades de transporte de mercanc?as por carretera y servicios de mudanzas:
			// Se aplicar? el ?ndice 0,80 cuando el titular disponga de un ?nico veh?culo.
			// Se aplicar? el ?ndice 0,90 cuando la actividad se realice con tractocamiones
			// y el titular carezca de semirremolques. Cuando la actividad se desarrolle con 
			// un ?nico tractocami?n y sin semirremolques, se aplicar?, exclusivamente, el ?ndice 0,75.

			if (AonMathUtils.round(veh) == 1.0) {
				ic1 = 0.80;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);
			}
			// Indique si la actividad se realiza con un ?nico tractocami?n y sin semirremolques.
			if (act.isTss()) {
				ic1 = 0.75;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);
			}
			// Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.
			if (act.isTns()) {
				ic1 = 0.90;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);
			}
		} else if (Epigraph.E____.getEpigraph().equals(act.getEpigraph())) {
			// Actividad de producci?n de mejill?n en batea:
			//	- Empresa con una sola batea y sin barco auxiliar: 0,75.
			//	- Empresa con una sola batea y con un barco auxiliar de 
			//	  menos de 15 toneladas de registro bruto (T.R.B.): 0,85.
			//	- Empresa con una sola batea y con un barco auxiliar de 15 
			//	  a 30 T.R.B.; y empresa con dos bateas y sin barco auxiliar: 0,90.
			//	- Empresa con una sola batea y con un barco auxiliar de m?s 
			//	  de 30 T.R.B.; y empresa con dos bateas y un barco auxiliar 
			//    de menos de 15 T.R.B.: 0,95.
			act.setIndiceEmpresasPequenaDimensionAplicable(false);
			// N?mero de bateas y de barcos auxiliares de la empresa.
			double bat = act.getBat();
			if (AonMathUtils.round(bat) == 1.0) {
				ic1 = 0.75;
			} else if (AonMathUtils.round(bat) == 2.0) {
				ic1 = 0.85;
			} else if (AonMathUtils.round(bat) == 3.0 || AonMathUtils.round(bat) == 5.0) {
				ic1 = 0.90;
			} else if (AonMathUtils.round(bat) == 4.0 || AonMathUtils.round(bat) == 6.0) {
				ic1 = 0.95;
			} else {
				// Otros: numero de bateas, barcos o TRB distintos de los anteriores.
				act.setIndiceEmpresasPequenaDimensionAplicable(false);	
			}
		}
		act.setIc1(ic1);
	}
	
	private static void calcIndiceCorrectorEmpresaPequenaDimension(AONContext ctx, Mod131Activity act) {
		double ic2 = 0.0;
		if (act.isIndiceEmpresasPequenaDimensionAplicable() 
			&& AonMathUtils.round(act.getCom()) > 0) {
			act.setIndiceEmpresasPequenaDimensionAplicable(false);
		}
		if (act.isIndiceEmpresasPequenaDimensionAplicable()) {
			// En ning?n caso ser? aplicable el ?ndice corrector para empresas de peque?a 
			// dimensi?n (b.1) a las actividades para las que est?n previstos los ?ndices 
			// correctores especiales enumerados en las letras a.2), a.3), a.4) y a.5).
			if (act.isLoc() && act.getRnm() > 0) {
				if (AonMathUtils.round(act.getVeh()) <= 1.0) {
					if (!act.isCap()) {
						ic2 = 0.7; 
						if (AonMathUtils.round(act.getMun()) == 1.0) {
							ic2 = 0.75;
						} else if (AonMathUtils.round(act.getMun()) >= 2.0) {
							ic2 = 0.80;
						}
						if (act.getEmp() > 0.0 && act.getEmp() <= 2.0) {
							ic2 = 0.90;
						}
						if (act.getEmp() > 2) {
							ic2 = 0.0;
						}
					}
				}
			}
		}
		act.setIc2(ic2);
	}
	
	private static void calcIndiceCorrectorTemporada(AONContext ctx, Mod131Activity act) {
		act.setIc3(0);
		if (AonMathUtils.round(act.getTem()) > 0.0 && AonMathUtils.round(act.getTem()) <=60.0) {
			act.setIc3(1.5);
		} else if (AonMathUtils.round(act.getTem()) > 60.0 && AonMathUtils.round(act.getTem()) <= 120.0) {
			act.setIc3(1.35);
		} else if (AonMathUtils.round(act.getTem()) > 120.0 && AonMathUtils.round(act.getTem()) <= 180.0) {
			act.setIc3(1.25);
		}
	}
	
	private static void calcIndiceCorrectorExceso(AONContext ctx, Mod131Activity act) {
		double ic4 = 0.0;
		if (AonMathUtils.isZero(act.getIc2())) {
			double tope = AonMathUtils.round(act.getMaxImport());
			if (AonMathUtils.isZero(tope)) {
				Epigraph epi = Modules2016.Epigraph.getEpigraph(act.getEpigraph());
				if (epi != null) {
					tope = epi.getLimExceso();
				}
			}
			double baseIndice = act.getRnm();
			if (AonMathUtils.round(act.getIc1()) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * act.getIc1());	
			}
			if (AonMathUtils.round(act.getIc2()) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * act.getIc2());	
			}
			if (AonMathUtils.round(act.getIc3()) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * act.getIc3());	
			}
			if (baseIndice > tope) {
				 ic4 = 1.3;
			}
		}
		act.setIc4(ic4);
	}
	
	private static void calcIndiceCorrectorNuevaActividad(AONContext ctx, Mod131Activity act) {
		act.setIc5(0);
		if (AonMathUtils.isZero(act.getIc3())) {
			if (act.isLoc()) {
				if (AonMathUtils.round(act.getNue()) != 0.0) {
					
					if (!act.isDis()) { 	// No discapacitado
						if (act.getYear() == (int) act.getNue()) { // Primer año
							act.setIc5( 0.80 ); 
						}
						if (( act.getYear() - 1) == (int) act.getNue()) { // Segundo año
							act.setIc5( 0.90 ); 
						}
					} else {	// Discapacitado
						if (act.getYear() == (int) act.getNue()) {	// Primer año
							act.setIc5( 0.60 ); 
						}
						if (( act.getYear() - 1) == (int) act.getNue()) {	// Segundo año
							act.setIc5( 0.70 ); 
						}
					}
//					if (act.getYear() == (int) act.getNue()) {
//						act.setIc5( act.isDis()?0.60:0.70 ); 
//					}
//					// Segundo
//					if (( act.getYear() - 1) == (int) act.getNue()) {
//						act.setIc5( act.isDis()?0.80:0.90);
//					}
				}
			}
		}
	}
	
	private static void calcRendimientoEfectosPagoFraccionado(AONContext ctx, Mod131Activity act) {
		// *****************************************************************
		// 		RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (i.R.P.F.)
		// *****************************************************************
		double rpf = 0.0;
		rpf = act.getRnm();
		if (AonMathUtils.isNotZero(act.getIc1())) {
			rpf = rpf * act.getIc1();	
		}
		if (AonMathUtils.isNotZero(act.getIc2())) {
			rpf = rpf * act.getIc2();	
		}
		if (AonMathUtils.isNotZero(act.getIc3())) {
			rpf = rpf * act.getIc3();	
		}
		if (AonMathUtils.isNotZero(act.getIc4())) {
			double baseExceso = rpf - act.getMaxImport();
			baseExceso = baseExceso * act.getIc4(); 
			rpf = baseExceso + act.getMaxImport();	
		}
		if (AonMathUtils.isNotZero(act.getIc5())) {
			rpf = rpf * act.getIc5();	
		}
		// Disposici?n adicional primera. 
		//	Reducci?n en 2013 del rendimiento neto calculado por el m?todo de estimaci?n objetiva.
		//	Los contribuyentes que determinen el rendimiento neto de sus actividades
		//	econ?micas por el m?todo de estimaci?n objetiva, podr?n reducir el rendimiento 
		//	neto de m?dulos obtenido en 2013 en un 5 por 100.
		rpf = rpf - (rpf * 10 / 100);
		
		
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participaci?n.
		if (AonMathUtils.isNotZero(act.getCom())) {
			rpf = (rpf * act.getCom() / 100);
			
		}
		rpf = AonMathUtils.round(rpf);
		act.setRpf(rpf);
	}

	private static void calcReduccionLorca(AONContext ctx, Mod131Activity act) {
		// *****************************************************************
		// Reducci?n para actividades econ?micas realizadas en el t?rmino municipal de Lorca
		// *****************************************************************
		
		// Los contribuyentes del Impuesto sobre la Renta de las Personas F?sicas que
		// desarrollen actividades econ?micas incluidas en el anexo II de esta Orden en el t?rmino
		// municipal de Lorca y determinen el rendimiento neto por el m?todo de estimaci?n objetiva,
		// podr?n reducir el rendimiento neto de m?dulos de 2013 correspondiente a tales
		// actividades en un 20 por ciento.
		double rlo = 0.0;
		if (AonMathUtils.isNotZero(act.getLor())) {
			rlo = AonMathUtils.round(act.getRpf() * 20 / 100); 	
		}
		act.setRlo(rlo);		
	}
	
	private static void calcRendimientoEfectosPagoFraccionadoDespuesReduccion(AONContext ctx, Mod131Activity act) {
		act.setRdr(AonMathUtils.round(act.getRpf() - act.getRlo()));
	}
	
	private static void calcResultadoPagoTrimestral(AONContext ctx, Mod131Activity act) {
		double salariedStaff = act.getEmp();
		if (AonMathUtils.isZero(salariedStaff)) {
			for (Mod131ActivityModule mod : act.getModules()) {
				if (mod.isSalariedStaff()) {
					salariedStaff = AonMathUtils.round(salariedStaff + mod.getValue());
				}
			}
		}
		
		int periodDays = (int) AonDateUtils.getDaysBetweenDates(
				 FiscalUtils.getPeriodStart(act.getYear(),act.getPeriod())
				,FiscalUtils.getPeriodEnd(act.getYear(),act.getPeriod()));
		periodDays = periodDays + 1;
		
		double daysFactor = 1;
		if (act.getDia() < periodDays ) {
			daysFactor = ((double)act.getDia()) / ((double) periodDays);
		}
		double net = AonMathUtils.round(act.getRdr() * daysFactor);
		act.setNet(net);
		
		double por = 4.0;
		if (AonMathUtils.round(salariedStaff) <= 1.0) por = 3.0; 
		if (AonMathUtils.round(salariedStaff) == 0.0) por = 2.0;
		
		if (act.getPrc() > por) {
			por = act.getPrc();
		}
		if ( net < 0) {
			por = 0.0;
		}
		act.setPor(por);
		
		double res = AonMathUtils.round(net *  por / 100 );
		act.setRes(res);

	}
	
}