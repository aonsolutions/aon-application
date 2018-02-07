package com.esferalia.aon.payroll.irpf;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.code.aon.config.enumeration.Administration;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.IrpfRegularization;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.calculator.jooq.JooqGeozoneIrpf;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Discapacidad;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.SituacionFamiliar;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import es.aeat.pret.rw13.jaxb.TipoComputo;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Regularizacion;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Ascendientes;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Ascendientes.Mayores75;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Ascendientes.Menores75;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Descendientes;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Descendientes.ComputoDescendientes;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Descendientes.ComputoDescendientes.CuartoySucesivos;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Descendientes.ConDiscapacidad;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Descendientes.Menores3Años;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Descendientes.Resto;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.MinimoPersonalFamiliar;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013.Reduccion;
import es.aeat.pret.rw13.util.ValidaNif;
import net.aonsolutions.core.aeat.jaxb.AEATRetencionesEntrada2016;
import net.aonsolutions.core.aeat.jaxb.AEATRetencionesError2016;
import net.aonsolutions.core.aeat.jaxb.AEATRetencionesSalida2016;
import net.aonsolutions.core.aeat.jaxb.TipoRetenedorError2016;
import net.aonsolutions.core.aeat.jaxb.TipoRetenedorSalida2016;
import net.aonsolutions.core.aeat.jaxb.TipoRetenidoError2016;
import net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016;
import net.aonsolutions.core.aeat.v2017.jaxb.AEATRetencionesEntrada2017;
import net.aonsolutions.core.aeat.v2017.jaxb.AEATRetencionesError2017;
import net.aonsolutions.core.aeat.v2017.jaxb.AEATRetencionesSalida2017;
import net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenedorError2017;
import net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenedorSalida2017;
import net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoError2017;
import net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017;
import net.aonsolutions.core.aeat.v2018.jaxb.AEATRetencionesEntrada2018;
import net.aonsolutions.core.aeat.v2018.jaxb.AEATRetencionesError2018;
import net.aonsolutions.core.aeat.v2018.jaxb.AEATRetencionesSalida2018;
import net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenedorError2018;
import net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenedorSalida2018;
import net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoError2018;
import net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018;

public class IrpfCalculator {

	public static String DEFAULT_NIF = "87449445H";
	public static String DEFAULT_CIF = "Z7896423E";

	public static boolean isValidNif(String nif) {
		ValidaNif validaNif = new ValidaNif();
		validaNif.checkNif(nif);
		return validaNif.isOk();
	}

	public static IrpfOutcome calculateIrpf(IIrpfCalculatorContext ctx, Date date) {
		int year = AonDateUtils.get(date, Calendar.YEAR);
		if ( year <= 2016 )
			return calculateIrpf2016(ctx);
		if ( year == 2017 )
			return calculateIrpf2017(ctx);
		else 
			return calculateIrpf2018(ctx);
	}
	
	
	// ------------------------------------------------------------------- 2016

	public static double calculate(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2016 aeatRetencionesSalida2016 = calculate2016(ctx);
		List<TipoRetenedorSalida2016> retenedores = aeatRetencionesSalida2016
				.getRetenedor();
		TipoRetenedorSalida2016 retenedorSalida2016 = retenedores.get(0);
		List<TipoRetenidoSalida2016> retenidos = retenedorSalida2016
				.getRetenido();
		TipoRetenidoSalida2016 retenidoSalida2016 = retenidos.get(0);
		BigDecimal tipoRetencion = retenidoSalida2016.getTipoRetencion();
		return tipoRetencion != null ? tipoRetencion.doubleValue() : 0.00;
	}

	public static IrpfOutcome calculateIrpf2016(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2016 aeatRetencionesSalida2016 = calculate2016(ctx);
		List<TipoRetenedorSalida2016> retenedores = aeatRetencionesSalida2016
				.getRetenedor();
		TipoRetenedorSalida2016 retenedorSalida2016 = retenedores.get(0);
		List<TipoRetenidoSalida2016> retenidos = retenedorSalida2016
				.getRetenido();
		TipoRetenidoSalida2016 retenidoSalida2016 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2016);
	}

	// ------------------------------------------------------------------- 2017

	public static IrpfOutcome calculateIrpf2017(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2017 aeatRetencionesSalida2017 = calculate2017(ctx);
		List<TipoRetenedorSalida2017> retenedores = aeatRetencionesSalida2017
				.getRetenedor();
		TipoRetenedorSalida2017 retenedorSalida2017 = retenedores.get(0);
		List<TipoRetenidoSalida2017> retenidos = retenedorSalida2017
				.getRetenido();
		TipoRetenidoSalida2017 retenidoSalida2017 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2017);
	}

	// ------------------------------------------------------------------- 2018

	public static IrpfOutcome calculateIrpf2018(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2018 aeatRetencionesSalida2018 = calculate2018(ctx);
		List<TipoRetenedorSalida2018> retenedores = aeatRetencionesSalida2018
				.getRetenedor();
		TipoRetenedorSalida2018 retenedorSalida2018 = retenedores.get(0);
		List<TipoRetenidoSalida2018> retenidos = retenedorSalida2018
				.getRetenido();
		TipoRetenidoSalida2018 retenidoSalida2018 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2018);
	}
	// -------------------------------------------------------------------------

	protected static IrpfOutcome transfom(IIrpfCalculatorContext ctx,
			TipoRetenidoSalida2013 retenidoSalida2013) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2013
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2013.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2013.getBaseRetencion()));
		MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2013
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(retenidoSalida2013
				.getDeduccion80Bis()));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2013
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2013
				.getImpAnualRetencionesIngresosCuenta()));

		// DATOS PERSONALES DEL PERCEPTOR
		IrpfData irpfData = new IrpfData();
		irpfData.setFamilySituation(toFamilySituation(ctx
				.getSituacionFamiliar()));
		irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
		irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad()));
		irpfOutcome.setIrpfData(irpfData);
		// DATOS ECONOMICOS
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2013
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(retenidoSalida2013
				.getDeduccion80Bis()));
		Reduccion reduccion = retenidoSalida2013.getReduccion();
		irpfResult
				.setIrregular18_3Reduction(reduccion != null ? toDouble(reduccion
						.getTotal()) : 0.00);
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2013
				.getGastosAnuales()));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2013
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2013
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		Descendientes descendientes = retenidoSalida2013.getDescendientes();
		if (descendientes != null) {
			Menores3Años menores3Años = descendientes.getMenores3Años();
			if (menores3Años != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años
						.getPorEntero()));
			}
			Resto resto = descendientes.getResto();
			if (resto != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto
						.getPorEntero()));
			}
			ComputoDescendientes computoDescendientes = descendientes
					.getComputoDescendientes();
			if (computoDescendientes != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes
						.getHijo3()));
				CuartoySucesivos cuartoySucesivos = computoDescendientes
						.getCuartoySucesivos();
				if (cuartoySucesivos != null) {
					irpfResult
							.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos
									.getTotal()));
					irpfResult
							.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos
									.getPorEntero()));
				}
			}
			ConDiscapacidad conDiscapacidad = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {
				ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					ConMovilidadReducida conMovilidadReducida = grado1
							.getConMovilidadReducida();
					if (conMovilidadReducida != null) {
						irpfResult
								.setDescendentsMovingTotal(toInteger(conMovilidadReducida
										.getTotal()));
						irpfResult
								.setDescendentsMovingEntirely(toInteger(conMovilidadReducida
										.getPorEntero()));
					}
				}
				ConDiscapacidad.EnGrado2 grado2 = conDiscapacidad.getEnGrado2();
				if (grado2 != null) {
					irpfResult.setDescendents65Total(toInteger(grado2
							.getTotal()));
					irpfResult.setDescendents65Entirely(toInteger(grado2
							.getPorEntero()));
				}

			}

		}

		// ASCENCIENTES COMPUTADOS
		Ascendientes ascendientes = retenidoSalida2013.getAscendientes();
		if (ascendientes != null) {
			Menores75 menores75 = ascendientes.getMenores75();
			if (menores75 != null) {
			}
			Mayores75 mayores75 = ascendientes.getMayores75();
			if (mayores75 != null) {
			}
			Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		Regularizacion regularizacion = retenidoSalida2013.getRegularizacion();
		if (regularizacion != null) {
			IrpfRegularization irpfRegularization = new IrpfRegularization();
			List<Integer> causas = regularizacion.getCausa();
			irpfRegularization
					.setReason(causas.isEmpty() ? IrpfRegularizationReason.OTHER
							: toIrpfRegularizationReason(causas.get(0)));
			irpfRegularization.setPaidRemuneration(toDouble(regularizacion
					.getRetribSatisfechas()));
			irpfRegularization.setPaidIrpf(toDouble(regularizacion
					.getRetencionPracticada()));
			irpfRegularization
					.setPriorAnnualRemuneration(toDouble(regularizacion
							.getRetribAnualesIniciales()));
			irpfRegularization.setPriorAnnualIrpf(toDouble(regularizacion
					.getRetencionAnualInicial()));
			irpfRegularization.setPriorBaseIrpf(toDouble(regularizacion
					.getBaseRetencion()));
			irpfRegularization
					.setPriorMinimunPersonalFamily(toDouble(regularizacion
							.getMinimoPersonalFamiliarInicial()));
			irpfRegularization.setPriorIrpf(toDouble(regularizacion
					.getTipoRetencion()));
			irpfRegularization
					.setPriorDeductHomeLoanAmount(toDouble(regularizacion
							.getMinoracionPrestamosVivienda()));
			irpfOutcome.setIrpfRegularization(irpfRegularization);
		}

		return irpfOutcome;
	}

	protected static IrpfOutcome transfom(IIrpfCalculatorContext ctx,
			TipoRetenidoSalida2016 retenidoSalida2016) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2016
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2016.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2016.getBaseRetencion()));
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2016
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(null /*
												 * retenidoSalida2016.
												 * getDeduccion80Bis()
												 */));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2016
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2016
				.getImpAnualRetencionesIngresosCuenta()));

		// DATOS PERSONALES DEL PERCEPTOR
		IrpfData irpfData = new IrpfData();
		irpfData.setFamilySituation(toFamilySituation(ctx
				.getSituacionFamiliar()));
		irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
		irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad()));
		irpfOutcome.setIrpfData(irpfData);

		// DATOS ECONOMICOS
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2016
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(null /*
															 * retenidoSalida2016.
															 * getDeduccion80Bis
															 * ()
															 */));
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Reduccion reduccion = retenidoSalida2016
				.getReduccion();
		irpfResult
				.setIrregular18_3Reduction(reduccion != null ? toDouble(null/*
																			 * reduccion
																			 * .
																			 * getTotal
																			 * (
																			 * )
																			 */)
						: 0.00);
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2016
				.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2016
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2016
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes descendientes = retenidoSalida2016
				.getDescendientes();
		if (descendientes != null) {
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.Menores3Años menores3Años = descendientes
					.getMenores3Años();
			if (menores3Años != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.Resto resto = descendientes
					.getResto();
			if (resto != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ComputoDescendientes computoDescendientes = descendientes
					.getComputoDescendientes();
			if (computoDescendientes != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes
						.getHijo3()));
				net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos = computoDescendientes
						.getCuartoySucesivos();
				if (cuartoySucesivos != null) {
					irpfResult
							.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos
									.getTotal()));
					irpfResult
							.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos
									.getPorEntero()));
				}
			}
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ConDiscapacidad conDiscapacidad = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {
				net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad
						.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida = grado1
							.getConMovilidadReducida();
					if (conMovilidadReducida != null) {
						irpfResult
								.setDescendentsMovingTotal(toInteger(conMovilidadReducida
										.getTotal()));
						irpfResult
								.setDescendentsMovingEntirely(toInteger(conMovilidadReducida
										.getPorEntero()));
					}
				}
				net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ConDiscapacidad.EnGrado2 grado2 = conDiscapacidad
						.getEnGrado2();
				if (grado2 != null) {
					irpfResult.setDescendents65Total(toInteger(grado2
							.getTotal()));
					irpfResult.setDescendents65Entirely(toInteger(grado2
							.getPorEntero()));
				}

			}

		}

		// ASCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Ascendientes ascendientes = retenidoSalida2016
				.getAscendientes();
		if (ascendientes != null) {
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Ascendientes.Menores75 menores75 = ascendientes
					.getMenores75();
			if (menores75 != null) {
			}
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Ascendientes.Mayores75 mayores75 = ascendientes
					.getMayores75();
			if (mayores75 != null) {
			}
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Regularizacion regularizacion = retenidoSalida2016
				.getRegularizacion();
		if (regularizacion != null) {
			IrpfRegularization irpfRegularization = new IrpfRegularization();
			List<Integer> causas = regularizacion.getCausa();
			irpfRegularization
					.setReason(causas.isEmpty() ? IrpfRegularizationReason.OTHER
							: toIrpfRegularizationReason(causas.get(0)));
			irpfRegularization.setPaidRemuneration(toDouble(regularizacion
					.getRetribSatisfechas()));
			irpfRegularization.setPaidIrpf(toDouble(regularizacion
					.getRetencionPracticada()));
			irpfRegularization
					.setPriorAnnualRemuneration(toDouble(regularizacion
							.getRetribAnualesIniciales()));
			irpfRegularization.setPriorAnnualIrpf(toDouble(regularizacion
					.getRetencionAnualInicial()));
			irpfRegularization.setPriorBaseIrpf(toDouble(regularizacion
					.getBaseRetencion()));
			irpfRegularization
					.setPriorMinimunPersonalFamily(toDouble(regularizacion
							.getMinimoPersonalFamiliarInicial()));
			irpfRegularization.setPriorIrpf(toDouble(regularizacion
					.getTipoRetencion()));
			irpfRegularization
					.setPriorDeductHomeLoanAmount(toDouble(regularizacion
							.getMinoracionPrestamosVivienda()));
			irpfOutcome.setIrpfRegularization(irpfRegularization);
		}

		return irpfOutcome;
	}

	protected static IrpfOutcome transfom(IIrpfCalculatorContext ctx,
			TipoRetenidoSalida2017 retenidoSalida2017) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2017
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2017.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2017.getBaseRetencion()));
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2017
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(null /*
												 * retenidoSalida2016.
												 * getDeduccion80Bis()
												 */));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2017
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2017
				.getImpAnualRetencionesIngresosCuenta()));

		// DATOS PERSONALES DEL PERCEPTOR
		IrpfData irpfData = new IrpfData();
		irpfData.setFamilySituation(toFamilySituation(ctx
				.getSituacionFamiliar()));
		irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
		irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad()));
		irpfOutcome.setIrpfData(irpfData);

		// DATOS ECONOMICOS
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2017
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(null /*
															 * retenidoSalida2016.
															 * getDeduccion80Bis
															 * ()
															 */));
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Reduccion reduccion = retenidoSalida2017
				.getReduccion();
		irpfResult
				.setIrregular18_3Reduction(reduccion != null ? toDouble(null/*
																			 * reduccion
																			 * .
																			 * getTotal
																			 * (
																			 * )
																			 */)
						: 0.00);
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2017
				.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2017
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2017
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes descendientes = retenidoSalida2017
				.getDescendientes();
		if (descendientes != null) {
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.Menores3Años menores3Años2017 = descendientes
					.getMenores3Años();
			if (menores3Años2017 != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años2017
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años2017
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.Resto resto2017 = descendientes
					.getResto();
			if (resto2017 != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto2017
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto2017
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ComputoDescendientes computoDescendientes2017 = descendientes
					.getComputoDescendientes();
			if (computoDescendientes2017 != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes2017
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes2017
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes2017
						.getHijo3()));
				net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2017 = computoDescendientes2017
						.getCuartoySucesivos();
				if (cuartoySucesivos2017 != null) {
					irpfResult
							.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos2017
									.getTotal()));
					irpfResult
							.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos2017
									.getPorEntero()));
				}
			}
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ConDiscapacidad conDiscapacidad2017 = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad2017 != null) {
				net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad2017
						.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida2017 = grado1
							.getConMovilidadReducida();
					if (conMovilidadReducida2017 != null) {
						irpfResult
								.setDescendentsMovingTotal(toInteger(conMovilidadReducida2017
										.getTotal()));
						irpfResult
								.setDescendentsMovingEntirely(toInteger(conMovilidadReducida2017
										.getPorEntero()));
					}
				}
				net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ConDiscapacidad.EnGrado2 grado22017 = conDiscapacidad2017
						.getEnGrado2();
				if (grado22017 != null) {
					irpfResult.setDescendents65Total(toInteger(grado22017
							.getTotal()));
					irpfResult.setDescendents65Entirely(toInteger(grado22017
							.getPorEntero()));
				}

			}

		}

		
		
		// ASCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Ascendientes ascendientes = retenidoSalida2017
				.getAscendientes();
		if (ascendientes != null) {
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Ascendientes.Menores75 menores75 = ascendientes
					.getMenores75();
			if (menores75 != null) {
			}
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Ascendientes.Mayores75 mayores75 = ascendientes
					.getMayores75();
			if (mayores75 != null) {
			}
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Regularizacion regularizacion = retenidoSalida2017
				.getRegularizacion();
		if (regularizacion != null) {
			IrpfRegularization irpfRegularization = new IrpfRegularization();
			List<Integer> causas = regularizacion.getCausa();
			irpfRegularization
					.setReason(causas.isEmpty() ? IrpfRegularizationReason.OTHER
							: toIrpfRegularizationReason(causas.get(0)));
			irpfRegularization.setPaidRemuneration(toDouble(regularizacion
					.getRetribSatisfechas()));
			irpfRegularization.setPaidIrpf(toDouble(regularizacion
					.getRetencionPracticada()));
			irpfRegularization
					.setPriorAnnualRemuneration(toDouble(regularizacion
							.getRetribAnualesIniciales()));
			irpfRegularization.setPriorAnnualIrpf(toDouble(regularizacion
					.getRetencionAnualInicial()));
			irpfRegularization.setPriorBaseIrpf(toDouble(regularizacion
					.getBaseRetencion()));
			irpfRegularization
					.setPriorMinimunPersonalFamily(toDouble(regularizacion
							.getMinimoPersonalFamiliarInicial()));
			irpfRegularization.setPriorIrpf(toDouble(regularizacion
					.getTipoRetencion()));
			irpfRegularization
					.setPriorDeductHomeLoanAmount(toDouble(regularizacion
							.getMinoracionPrestamosVivienda()));
			irpfOutcome.setIrpfRegularization(irpfRegularization);
		}

		return irpfOutcome;
	}

	protected static IrpfOutcome transfom(IIrpfCalculatorContext ctx,
			TipoRetenidoSalida2018 retenidoSalida2018) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2018
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2018.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2018.getBaseRetencion()));
		net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2018
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(null /*
												 * retenidoSalida2016.
												 * getDeduccion80Bis()
												 */));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2018
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2018
				.getImpAnualRetencionesIngresosCuenta()));

		// DATOS PERSONALES DEL PERCEPTOR
		IrpfData irpfData = new IrpfData();
		irpfData.setFamilySituation(toFamilySituation(ctx
				.getSituacionFamiliar()));
		irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
		irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad()));
		irpfOutcome.setIrpfData(irpfData);

		// DATOS ECONOMICOS
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2018
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(null /*
															 * retenidoSalida2016.
															 * getDeduccion80Bis
															 * ()
															 */));
		net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Reduccion reduccion = retenidoSalida2018
				.getReduccion();
		irpfResult
				.setIrregular18_3Reduction(reduccion != null ? toDouble(null/*
																			 * reduccion
																			 * .
																			 * getTotal
																			 * (
																			 * )
																			 */)
						: 0.00);
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2018
				.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2018
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2018
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes descendientes = retenidoSalida2018
				.getDescendientes();
		if (descendientes != null) {
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.Menores3Años menores3Años2018 = descendientes
					.getMenores3Años();
			if (menores3Años2018 != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años2018
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años2018
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.Resto resto2018 = descendientes
					.getResto();
			if (resto2018 != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto2018
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto2018
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ComputoDescendientes computoDescendientes2018 = descendientes
					.getComputoDescendientes();
			if (computoDescendientes2018 != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes2018
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes2018
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes2018
						.getHijo3()));
				net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2018 = computoDescendientes2018
						.getCuartoySucesivos();
				if (cuartoySucesivos2018 != null) {
					irpfResult
							.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos2018
									.getTotal()));
					irpfResult
							.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos2018
									.getPorEntero()));
				}
			}
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ConDiscapacidad conDiscapacidad2018 = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad2018 != null) {
				net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad2018
						.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida2018 = grado1
							.getConMovilidadReducida();
					if (conMovilidadReducida2018 != null) {
						irpfResult
								.setDescendentsMovingTotal(toInteger(conMovilidadReducida2018
										.getTotal()));
						irpfResult
								.setDescendentsMovingEntirely(toInteger(conMovilidadReducida2018
										.getPorEntero()));
					}
				}
				net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ConDiscapacidad.EnGrado2 grado22018 = conDiscapacidad2018
						.getEnGrado2();
				if (grado22018 != null) {
					irpfResult.setDescendents65Total(toInteger(grado22018
							.getTotal()));
					irpfResult.setDescendents65Entirely(toInteger(grado22018
							.getPorEntero()));
				}

			}

		}

		
		
		// ASCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Ascendientes ascendientes = retenidoSalida2018
				.getAscendientes();
		if (ascendientes != null) {
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Ascendientes.Menores75 menores75 = ascendientes
					.getMenores75();
			if (menores75 != null) {
			}
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Ascendientes.Mayores75 mayores75 = ascendientes
					.getMayores75();
			if (mayores75 != null) {
			}
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoEntrada2018.Regularizacion regularizacion = retenidoSalida2018
				.getRegularizacion();
		if (regularizacion != null) {
			IrpfRegularization irpfRegularization = new IrpfRegularization();
			List<Integer> causas = regularizacion.getCausa();
			irpfRegularization
					.setReason(causas.isEmpty() ? IrpfRegularizationReason.OTHER
							: toIrpfRegularizationReason(causas.get(0)));
			irpfRegularization.setPaidRemuneration(toDouble(regularizacion
					.getRetribSatisfechas()));
			irpfRegularization.setPaidIrpf(toDouble(regularizacion
					.getRetencionPracticada()));
			irpfRegularization
					.setPriorAnnualRemuneration(toDouble(regularizacion
							.getRetribAnualesIniciales()));
			irpfRegularization.setPriorAnnualIrpf(toDouble(regularizacion
					.getRetencionAnualInicial()));
			irpfRegularization.setPriorBaseIrpf(toDouble(regularizacion
					.getBaseRetencion()));
			irpfRegularization
					.setPriorMinimunPersonalFamily(toDouble(regularizacion
							.getMinimoPersonalFamiliarInicial()));
			irpfRegularization.setPriorIrpf(toDouble(regularizacion
					.getTipoRetencion()));
			irpfRegularization
					.setPriorDeductHomeLoanAmount(toDouble(regularizacion
							.getMinoracionPrestamosVivienda()));
			irpfOutcome.setIrpfRegularization(irpfRegularization);
		}

		return irpfOutcome;
	}


	protected static AEATRetencionesSalida2016 calculate2016(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2016(ctx);

		return AEATCalculate.INSTANCE.calculate2016(ctx);
	}

	protected static AEATRetencionesSalida2017 calculate2017(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2017(ctx);

		return AEATCalculate.INSTANCE.calculate2017(ctx);
	}

	protected static AEATRetencionesSalida2018 calculate2018(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2018(ctx);

		return AEATCalculate.INSTANCE.calculate2018(ctx);
	}

	private static Integer toInteger(Byte b) {
		return b == null ? 0 : b.intValue();
	}

	private static Integer toInteger(TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
			net.aonsolutions.core.aeat.jaxb.TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
			net.aonsolutions.core.aeat.v2017.jaxb.TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
			net.aonsolutions.core.aeat.v2018.jaxb.TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Double toDouble(BigDecimal bigDecimal) {
		return bigDecimal == null ? 0.00 : bigDecimal.doubleValue();
	}

	private static FamilySituation toFamilySituation(
			SituacionFamiliar situacionFamiliar) {
		switch (situacionFamiliar) {
		case UNO:
			return FamilySituation.NO_MARRIED_WITH_SONS;
		case DOS:
			return FamilySituation.MARRIED;

		default:
			return FamilySituation.OTHER;
		}

	}

	private static DisabilityLevel toDisabilityLevel(Discapacidad discapacidad) {
		switch (discapacidad) {
		case GRADO1:
			return DisabilityLevel.GT_EQ_33_LT_65;
		case GRADO2:
			return DisabilityLevel.GT_EQ_65;
		default:
			return null;
		}

	}

	private static IrpfRegularizationReason toIrpfRegularizationReason(int causa) {
		return IrpfRegularizationReason.valueof(causa);
	}

	private static interface Calculate {
		boolean accept(IIrpfCalculatorContext ctx);

		AEATRetencionesSalida2016 calculate2016(IIrpfCalculatorContext ctx);

		AEATRetencionesSalida2017 calculate2017(IIrpfCalculatorContext ctx);

		AEATRetencionesSalida2018 calculate2018(IIrpfCalculatorContext ctx);
	}

	private static class AEATCalculate implements Calculate {

		private static AEATCalculate INSTANCE = new AEATCalculate();

		@Override
		public boolean accept(IIrpfCalculatorContext ctx) {
			return true;
		}


		@Override
		public AEATRetencionesSalida2016 calculate2016(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2016 aeatRetencionesEntrada2016 = AEATRetencionesEntradaFactory
						.create2016(ctx);
				return calculate(aeatRetencionesEntrada2016);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2016 error = e
						.getAEATRetencionesError2016();

				for (net.aonsolutions.core.aeat.jaxb.TipoErrorGeneral tipoErrorGeneral : error
						.getErrorGeneral())
					throw new ExpressionExceptionWrapper(new CheckException(
							tipoErrorGeneral.getDescripcion()));

				List<TipoRetenedorError2016> retenedores = error.getRetenedor();

				String message = null;
				TipoRetenedorError2016 retenedor = retenedores.get(0);
				List<TipoRetenidoError2016> retenidos = retenedor.getRetenido();
				if (retenidos.size() > 0) {
					TipoRetenidoError2016 retenido = retenidos.get(0);
					List<net.aonsolutions.core.aeat.jaxb.TipoError> tipoErrores = retenido
							.getError();
					if (tipoErrores.size() > 0) {
						net.aonsolutions.core.aeat.jaxb.TipoError tipoError = tipoErrores
								.get(0);
						message = tipoError.getDescripcion();
					}
				}
				if (message == null) {
					List<net.aonsolutions.core.aeat.jaxb.TipoErrorGeneral> errores = error
							.getErrorGeneral();
					if (errores.size() > 0) {
						message = errores.get(0).getDescripcion();
					}

				}
				ExpressionException expressionException = new CheckException(
						message);
				throw new ExpressionExceptionWrapper(expressionException);
			}
		}

		@Override
		public AEATRetencionesSalida2017 calculate2017(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2017 aeatRetencionesEntrada2017 = AEATRetencionesEntradaFactory
						.create2017(ctx);
				return calculate(aeatRetencionesEntrada2017);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2017 error = e
						.getAEATRetencionesError2017();

				for (net.aonsolutions.core.aeat.v2017.jaxb.TipoErrorGeneral tipoErrorGeneral : error
						.getErrorGeneral())
					throw new ExpressionExceptionWrapper(new CheckException(
							tipoErrorGeneral.getDescripcion()));

				List<TipoRetenedorError2017> retenedores = error.getRetenedor();

				String message = null;
				TipoRetenedorError2017 retenedor = retenedores.get(0);
				List<TipoRetenidoError2017> retenidos = retenedor.getRetenido();
				if (retenidos.size() > 0) {
					TipoRetenidoError2017 retenido = retenidos.get(0);
					List<net.aonsolutions.core.aeat.v2017.jaxb.TipoError> tipoErrores = retenido
							.getError();
					if (tipoErrores.size() > 0) {
						net.aonsolutions.core.aeat.v2017.jaxb.TipoError tipoError = tipoErrores
								.get(0);
						message = tipoError.getDescripcion();
					}
				}
				if (message == null) {
					List<net.aonsolutions.core.aeat.v2017.jaxb.TipoErrorGeneral> errores = error
							.getErrorGeneral();
					if (errores.size() > 0) {
						message = errores.get(0).getDescripcion();
					}

				}
				ExpressionException expressionException = new CheckException(
						message);
				throw new ExpressionExceptionWrapper(expressionException);
			}
		}

		
		@Override
		public AEATRetencionesSalida2018 calculate2018(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2018 aeatRetencionesEntrada2018 = AEATRetencionesEntradaFactory
						.create2018(ctx);
				return calculate(aeatRetencionesEntrada2018);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2018 error = e
						.getAEATRetencionesError2018();

				for (net.aonsolutions.core.aeat.v2018.jaxb.TipoErrorGeneral tipoErrorGeneral : error
						.getErrorGeneral())
					throw new ExpressionExceptionWrapper(new CheckException(
							tipoErrorGeneral.getDescripcion()));

				List<TipoRetenedorError2018> retenedores = error.getRetenedor();

				String message = null;
				TipoRetenedorError2018 retenedor = retenedores.get(0);
				List<TipoRetenidoError2018> retenidos = retenedor.getRetenido();
				if (retenidos.size() > 0) {
					TipoRetenidoError2018 retenido = retenidos.get(0);
					List<net.aonsolutions.core.aeat.v2018.jaxb.TipoError> tipoErrores = retenido
							.getError();
					if (tipoErrores.size() > 0) {
						net.aonsolutions.core.aeat.v2018.jaxb.TipoError tipoError = tipoErrores
								.get(0);
						message = tipoError.getDescripcion();
					}
				}
				if (message == null) {
					List<net.aonsolutions.core.aeat.v2018.jaxb.TipoErrorGeneral> errores = error
							.getErrorGeneral();
					if (errores.size() > 0) {
						message = errores.get(0).getDescripcion();
					}

				}
				ExpressionException expressionException = new CheckException(
						message);
				throw new ExpressionExceptionWrapper(expressionException);
			}
		}
		// --------------------------------------------------------------------

		private AEATRetencionesSalida2016 calculate(
				AEATRetencionesEntrada2016 entrada2016)
				throws IrpfCalculateException, JAXBException, IOException {

			Marshaller marshaller = JAXBContext.newInstance(
					AEATRetencionesEntrada2016.class).createMarshaller();
			File entrada2016File = File.createTempFile(
					AEATRetencionesEntrada2016.class.getSimpleName(), null);
			marshaller.marshal(entrada2016, entrada2016File);

			File salida2016File = File.createTempFile(
					AEATRetencionesSalida2016.class.getSimpleName(), null);
			File error2016File = File.createTempFile(
					AEATRetencionesError2016.class.getSimpleName(), null);

			es.aeat.pret.c160.mc.ModuloCalculo.procesarFicheroXml(entrada2016File.getAbsolutePath(),
					error2016File.getAbsolutePath(), null,
					salida2016File.getAbsolutePath());
			entrada2016File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
					AEATRetencionesError2016.class).createUnmarshaller();
			try {
				AEATRetencionesError2016 error2016 = (AEATRetencionesError2016) unMarshaller
						.unmarshal(error2016File);
				error2016File.delete();
				throw new IrpfCalculateException(error2016);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2016File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2016.class).createUnmarshaller();
			AEATRetencionesSalida2016 salida2016 = (AEATRetencionesSalida2016) unMarshaller
					.unmarshal(salida2016File);
			salida2016File.delete();
			return salida2016;
		}

		private AEATRetencionesSalida2017 calculate(
				AEATRetencionesEntrada2017 entrada2017)
				throws IrpfCalculateException, JAXBException, IOException {

			Marshaller marshaller = JAXBContext.newInstance(
					AEATRetencionesEntrada2017.class).createMarshaller();
			File entrada2017File = File.createTempFile(
					AEATRetencionesEntrada2017.class.getSimpleName(), null);
			marshaller.marshal(entrada2017, entrada2017File);

			File salida2017File = File.createTempFile(
					AEATRetencionesSalida2017.class.getSimpleName(), null);
			File error2017File = File.createTempFile(
					AEATRetencionesError2017.class.getSimpleName(), null);

			es.aeat.pret.c170.mc.ModuloCalculo.procesarFicheroXml(entrada2017File.getAbsolutePath(),
					error2017File.getAbsolutePath(), null,
					salida2017File.getAbsolutePath());
			entrada2017File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
					AEATRetencionesError2017.class).createUnmarshaller();
			try {
				AEATRetencionesError2017 error2017 = (AEATRetencionesError2017) unMarshaller
						.unmarshal(error2017File);
				error2017File.delete();
				throw new IrpfCalculateException(error2017);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2017File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2017.class).createUnmarshaller();
			AEATRetencionesSalida2017 salida2017 = (AEATRetencionesSalida2017) unMarshaller
					.unmarshal(salida2017File);
			salida2017File.delete();
			return salida2017;
		}

		private AEATRetencionesSalida2018 calculate(
				AEATRetencionesEntrada2018 entrada2018)
				throws IrpfCalculateException, JAXBException, IOException {

			Marshaller marshaller = JAXBContext.newInstance(
					AEATRetencionesEntrada2018.class).createMarshaller();
			File entrada2018File = File.createTempFile(
					AEATRetencionesEntrada2018.class.getSimpleName(), null);
			marshaller.marshal(entrada2018, entrada2018File);

			File salida2018File = File.createTempFile(
					AEATRetencionesSalida2018.class.getSimpleName(), null);
			File error2018File = File.createTempFile(
					AEATRetencionesError2018.class.getSimpleName(), null);

			es.aeat.pret.c180.mc.ModuloCalculo.procesarFicheroXml(entrada2018File.getAbsolutePath(),
					error2018File.getAbsolutePath(), null,
					salida2018File.getAbsolutePath());
			entrada2018File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
					AEATRetencionesError2018.class).createUnmarshaller();
			try {
				AEATRetencionesError2018 error2018 = (AEATRetencionesError2018) unMarshaller
						.unmarshal(error2018File);
				error2018File.delete();
				throw new IrpfCalculateException(error2018);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2018File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2018.class).createUnmarshaller();
			AEATRetencionesSalida2018 salida2018 = (AEATRetencionesSalida2018) unMarshaller
					.unmarshal(salida2018File);
			salida2018File.delete();
			return salida2018;
		}
	}

	
	
	private static class ForalCalculate implements Calculate {

		public static ForalCalculate[] INSTANCES = {
				new ForalCalculate("01", Administration.ALAVA),
				new ForalCalculate("48", Administration.BIZKAIA),
				new ForalCalculate("20", Administration.GIPUZKOA),
				new ForalCalculate("31", Administration.NAVARRA) };

		private String geozone;
		private Administration administration;

		ForalCalculate(String geozone, Administration administration) {
			this.geozone = geozone;
			this.administration = administration;
		}

		@Override
		public boolean accept(IIrpfCalculatorContext ctx) {
			return ctx instanceof SQLIrpfCalculatorContext
					&& ((SQLIrpfCalculatorContext) ctx).getEconomicAgreement() == administration;
		}


		@Override
		public AEATRetencionesSalida2016 calculate2016(
				IIrpfCalculatorContext ctx) {
			// TODO Auto-generated method stub
			byte descendants = 0;
			Iterable<Descendiente> descendientes = ctx.getDescendientes();
			if (descendientes != null)
				for (Descendiente descendiente : descendientes)
					descendants++;

			byte handicap = 0;
			Discapacidad discapacidad = ctx.getDiscapacidad();
			if (discapacidad != null)
				handicap = (byte) discapacidad.ordinal();

			double amount = 0.00;
			BigDecimal retribAnuales = ctx.getRetribAnuales();
			if (retribAnuales != null)
				amount = retribAnuales.doubleValue();

			SQLIrpfCalculatorContext sqlCtx = (SQLIrpfCalculatorContext) ctx;

			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							sqlCtx.getChargeDate().getTime()));
			if (percent == null)
				throw new ExpressionExceptionWrapper(new CheckException(
						String.format("Imposible encontrar un porcentaje de retenci\u00f3n en las tablas de  %s ( %s descendientes, %s )",
								administration.getName(new Locale("es")), 
								descendants > 0 ? String.valueOf(descendants) : "sin",
								new String [] {
										"sin discapacidad", 
										"discapacidad >= 33% y < 65%",
										"discapacidad >= 33% y < 65%, movilidad reducida",
										"discapacidad > 65%"}[handicap])) );

			TipoRetenidoSalida2016 retenidoSalida = new TipoRetenidoSalida2016();
			retenidoSalida.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida.setComunidadAutonoma(geozone);

			// Mainly DEBUG INFO
			retenidoSalida.setRetribAnuales(retribAnuales);
			retenidoSalida.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ComputoDescendientes computoDescendientes = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes
						.setHijo1(net.aonsolutions.core.aeat.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes
						.setHijo2(net.aonsolutions.core.aeat.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes
						.setHijo3(net.aonsolutions.core.aeat.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos.setTotal((byte) (descendants - 3));
				cuartoySucesivos.setPorEntero((byte) (descendants - 3));
				computoDescendientes.setCuartoySucesivos(cuartoySucesivos);
			}
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes descendientes15 = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoSalida2016.Descendientes();
			descendientes15.setComputoDescendientes(computoDescendientes);
			retenidoSalida.setDescendientes(descendientes15);

			TipoRetenedorSalida2016 retenedorSalida2016 = new TipoRetenedorSalida2016();
			List<TipoRetenidoSalida2016> retenidosSalida = retenedorSalida2016
					.getRetenido();
			retenidosSalida.add(retenidoSalida);

			AEATRetencionesSalida2016 aeatRetencionesSalida = new AEATRetencionesSalida2016();
			List<TipoRetenedorSalida2016> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2016);

			return aeatRetencionesSalida;
		}

	
		@Override
		public AEATRetencionesSalida2017 calculate2017(
				IIrpfCalculatorContext ctx) {
			// TODO Auto-generated method stub
			byte descendants = 0;
			Iterable<Descendiente> descendientes = ctx.getDescendientes();
			if (descendientes != null)
				for (Descendiente descendiente : descendientes)
					descendants++;

			byte handicap = 0;
			Discapacidad discapacidad = ctx.getDiscapacidad();
			if (discapacidad != null)
				handicap = (byte) discapacidad.ordinal();

			double amount = 0.00;
			BigDecimal retribAnuales = ctx.getRetribAnuales();
			if (retribAnuales != null)
				amount = retribAnuales.doubleValue();

			SQLIrpfCalculatorContext sqlCtx = (SQLIrpfCalculatorContext) ctx;

			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							sqlCtx.getChargeDate().getTime()));
			if (percent == null)
				throw new ExpressionExceptionWrapper(new CheckException(
						String.format("Imposible encontrar un porcentaje de retenci\u00f3n en las tablas de  %s ( %s descendientes, %s )",
								administration.getName(new Locale("es")), 
								descendants > 0 ? String.valueOf(descendants) : "sin",
								new String [] {
										"sin discapacidad", 
										"discapacidad >= 33% y < 65%",
										"discapacidad >= 33% y < 65%, movilidad reducida",
										"discapacidad > 65%"}[handicap])) );

			TipoRetenidoSalida2017 retenidoSalida2017 = new TipoRetenidoSalida2017();
			retenidoSalida2017.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida2017.setComunidadAutonoma(geozone);

			// Mainly DEBUG INFO
			retenidoSalida2017.setRetribAnuales(retribAnuales);
			retenidoSalida2017.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida2017.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ComputoDescendientes computoDescendientes2017 = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes2017
						.setHijo1(net.aonsolutions.core.aeat.v2017.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes2017
						.setHijo2(net.aonsolutions.core.aeat.v2017.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes2017
						.setHijo3(net.aonsolutions.core.aeat.v2017.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2017 = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos2017.setTotal((byte) (descendants - 3));
				cuartoySucesivos2017.setPorEntero((byte) (descendants - 3));
				computoDescendientes2017.setCuartoySucesivos(cuartoySucesivos2017);
			}
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes descendientes152017 = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoSalida2017.Descendientes();
			descendientes152017.setComputoDescendientes(computoDescendientes2017);
			retenidoSalida2017.setDescendientes(descendientes152017);

			TipoRetenedorSalida2017 retenedorSalida2017 = new TipoRetenedorSalida2017();
			List<TipoRetenidoSalida2017> retenidosSalida = retenedorSalida2017
					.getRetenido();
			retenidosSalida.add(retenidoSalida2017);

			AEATRetencionesSalida2017 aeatRetencionesSalida = new AEATRetencionesSalida2017();
			List<TipoRetenedorSalida2017> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2017);

			return aeatRetencionesSalida;
		}

		@Override
		public AEATRetencionesSalida2018 calculate2018(
				IIrpfCalculatorContext ctx) {
			// TODO Auto-generated method stub
			byte descendants = 0;
			Iterable<Descendiente> descendientes = ctx.getDescendientes();
			if (descendientes != null)
				for (Descendiente descendiente : descendientes)
					descendants++;

			byte handicap = 0;
			Discapacidad discapacidad = ctx.getDiscapacidad();
			if (discapacidad != null)
				handicap = (byte) discapacidad.ordinal();

			double amount = 0.00;
			BigDecimal retribAnuales = ctx.getRetribAnuales();
			if (retribAnuales != null)
				amount = retribAnuales.doubleValue();

			SQLIrpfCalculatorContext sqlCtx = (SQLIrpfCalculatorContext) ctx;

			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							sqlCtx.getChargeDate().getTime()));
			if (percent == null)
				throw new ExpressionExceptionWrapper(new CheckException(
						String.format("Imposible encontrar un porcentaje de retenci\u00f3n en las tablas de  %s ( %s descendientes, %s )",
								administration.getName(new Locale("es")), 
								descendants > 0 ? String.valueOf(descendants) : "sin",
								new String [] {
										"sin discapacidad", 
										"discapacidad >= 33% y < 65%",
										"discapacidad >= 33% y < 65%, movilidad reducida",
										"discapacidad > 65%"}[handicap])) );

			TipoRetenidoSalida2018 retenidoSalida2018 = new TipoRetenidoSalida2018();
			retenidoSalida2018.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida2018.setComunidadAutonoma(geozone);

			// Mainly DEBUG INFO
			retenidoSalida2018.setRetribAnuales(retribAnuales);
			retenidoSalida2018.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida2018.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ComputoDescendientes computoDescendientes2018 = new net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes2018
						.setHijo1(net.aonsolutions.core.aeat.v2018.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes2018
						.setHijo2(net.aonsolutions.core.aeat.v2018.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes2018
						.setHijo3(net.aonsolutions.core.aeat.v2018.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2018 = new net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos2018.setTotal((byte) (descendants - 3));
				cuartoySucesivos2018.setPorEntero((byte) (descendants - 3));
				computoDescendientes2018.setCuartoySucesivos(cuartoySucesivos2018);
			}
			net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes descendientes152018 = new net.aonsolutions.core.aeat.v2018.jaxb.TipoRetenidoSalida2018.Descendientes();
			descendientes152018.setComputoDescendientes(computoDescendientes2018);
			retenidoSalida2018.setDescendientes(descendientes152018);

			TipoRetenedorSalida2018 retenedorSalida2018 = new TipoRetenedorSalida2018();
			List<TipoRetenidoSalida2018> retenidosSalida = retenedorSalida2018
					.getRetenido();
			retenidosSalida.add(retenidoSalida2018);

			AEATRetencionesSalida2018 aeatRetencionesSalida = new AEATRetencionesSalida2018();
			List<TipoRetenedorSalida2018> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2018);

			return aeatRetencionesSalida;
		}
	}

}
