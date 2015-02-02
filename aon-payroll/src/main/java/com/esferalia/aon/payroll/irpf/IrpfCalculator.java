package com.esferalia.aon.payroll.irpf;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.code.aon.config.enumeration.Administration;
import com.esferalia.aon.aeat.jaxb.AEATRetencionesEntrada2015;
import com.esferalia.aon.aeat.jaxb.AEATRetencionesError2015;
import com.esferalia.aon.aeat.jaxb.AEATRetencionesSalida2015;
import com.esferalia.aon.aeat.jaxb.TipoRetenedorError2015;
import com.esferalia.aon.aeat.jaxb.TipoRetenedorSalida2015;
import com.esferalia.aon.aeat.jaxb.TipoRetenidoError2015;
import com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015;
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

import es.aeat.pret.rd13.ModeloRetencionesXMLJaxb;
import es.aeat.pret.rd13.XMLProgressListener;
import es.aeat.pret.rd15.modulo.retenciones.ModuloCalculo;
import es.aeat.pret.rw13.jaxb.AEATRetencionesEntrada2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesSalida2013;
import es.aeat.pret.rw13.jaxb.TipoComputo;
import es.aeat.pret.rw13.jaxb.TipoError;
import es.aeat.pret.rw13.jaxb.TipoErrorGeneral;
import es.aeat.pret.rw13.jaxb.TipoRetenedorError2013;
import es.aeat.pret.rw13.jaxb.TipoRetenedorSalida2013;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Regularizacion;
import es.aeat.pret.rw13.jaxb.TipoRetenidoError2013;
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

public class IrpfCalculator {

	public static String DEFAULT_NIF = "87449445H";
	public static String DEFAULT_CIF = "Z7896423E";

	public static boolean isValidNif(String nif) {
		ValidaNif validaNif = new ValidaNif();
		validaNif.checkNif(nif);
		return validaNif.isOk();
	}

	public static double calculate13(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2013 aeatRetencionesSalida2013 = calculate2013(ctx);
		List<TipoRetenedorSalida2013> retenedores = aeatRetencionesSalida2013
				.getRetenedor();
		TipoRetenedorSalida2013 retenedorSalida2013 = retenedores.get(0);
		List<TipoRetenidoSalida2013> retenidos = retenedorSalida2013
				.getRetenido();
		TipoRetenidoSalida2013 retenidoSalida2013 = retenidos.get(0);
		BigDecimal tipoRetencion = retenidoSalida2013.getTipoRetencion();
		return tipoRetencion != null ? tipoRetencion.doubleValue() : 0.00;
	}

	public static IrpfOutcome calculateIrpf13(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2013 aeatRetencionesSalida2013 = calculate2013(ctx);
		List<TipoRetenedorSalida2013> retenedores = aeatRetencionesSalida2013
				.getRetenedor();
		TipoRetenedorSalida2013 retenedorSalida2013 = retenedores.get(0);
		List<TipoRetenidoSalida2013> retenidos = retenedorSalida2013
				.getRetenido();
		TipoRetenidoSalida2013 retenidoSalida2013 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2013);
	}

	// ------------------------------------------------------------------- 2015

	public static double calculate(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2015 aeatRetencionesSalida2015 = calculate2015(ctx);
		List<TipoRetenedorSalida2015> retenedores = aeatRetencionesSalida2015
				.getRetenedor();
		TipoRetenedorSalida2015 retenedorSalida2015 = retenedores.get(0);
		List<TipoRetenidoSalida2015> retenidos = retenedorSalida2015
				.getRetenido();
		TipoRetenidoSalida2015 retenidoSalida2015 = retenidos.get(0);
		BigDecimal tipoRetencion = retenidoSalida2015.getTipoRetencion();
		return tipoRetencion != null ? tipoRetencion.doubleValue() : 0.00;
	}

	public static IrpfOutcome calculateIrpf(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2015 aeatRetencionesSalida2015 = calculate2015(ctx);
		List<TipoRetenedorSalida2015> retenedores = aeatRetencionesSalida2015
				.getRetenedor();
		TipoRetenedorSalida2015 retenedorSalida2015 = retenedores.get(0);
		List<TipoRetenidoSalida2015> retenidos = retenedorSalida2015
				.getRetenido();
		TipoRetenidoSalida2015 retenidoSalida2015 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2015);
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
			TipoRetenidoSalida2015 retenidoSalida2015) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2015
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2015.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2015.getBaseRetencion()));
		com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2015
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(null /*
												 * retenidoSalida2015.
												 * getDeduccion80Bis()
												 */));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2015
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2015
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
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2015
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(null /*
															 * retenidoSalida2015.
															 * getDeduccion80Bis
															 * ()
															 */));
		com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Reduccion reduccion = retenidoSalida2015
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
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2015
				.getCotizaciones()/* retenidoSalida2015.getGastosAnuales() */));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2015
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2015
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes descendientes = retenidoSalida2015
				.getDescendientes();
		if (descendientes != null) {
			com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.Menores3Años menores3Años = descendientes
					.getMenores3Años();
			if (menores3Años != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años
						.getPorEntero()));
			}
			com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.Resto resto = descendientes
					.getResto();
			if (resto != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto
						.getPorEntero()));
			}
			com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.ComputoDescendientes computoDescendientes = descendientes
					.getComputoDescendientes();
			if (computoDescendientes != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes
						.getHijo3()));
				com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos = computoDescendientes
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
			com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.ConDiscapacidad conDiscapacidad = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {
				com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad
						.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida = grado1
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
				com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Descendientes.ConDiscapacidad.EnGrado2 grado2 = conDiscapacidad
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
		com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Ascendientes ascendientes = retenidoSalida2015
				.getAscendientes();
		if (ascendientes != null) {
			com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Ascendientes.Menores75 menores75 = ascendientes
					.getMenores75();
			if (menores75 != null) {
			}
			com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Ascendientes.Mayores75 mayores75 = ascendientes
					.getMayores75();
			if (mayores75 != null) {
			}
			com.esferalia.aon.aeat.jaxb.TipoRetenidoSalida2015.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		com.esferalia.aon.aeat.jaxb.TipoRetenidoEntrada2015.Regularizacion regularizacion = retenidoSalida2015
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

	protected static AEATRetencionesSalida2013 calculate2013(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2013(ctx);

		return AEATCalculate.INSTANCE.calculate2013(ctx);
	}

	protected static AEATRetencionesSalida2015 calculate2015(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2015(ctx);

		return AEATCalculate.INSTANCE.calculate2015(ctx);
	}

	private static Integer toInteger(Byte b) {
		return b == null ? 0 : b.intValue();
	}

	private static Integer toInteger(TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
			com.esferalia.aon.aeat.jaxb.TipoComputo tipoComputo) {
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

		AEATRetencionesSalida2013 calculate2013(IIrpfCalculatorContext ctx);

		AEATRetencionesSalida2015 calculate2015(IIrpfCalculatorContext ctx);

	}

	private static class AEATCalculate implements Calculate {

		private static AEATCalculate INSTANCE = new AEATCalculate();

		@Override
		public boolean accept(IIrpfCalculatorContext ctx) {
			return true;
		}

		@Override
		public AEATRetencionesSalida2013 calculate2013(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2013 aeatRetencionesEntrada2013 = AEATRetencionesEntradaFactory
						.create2013(ctx);
				return calculate(aeatRetencionesEntrada2013);
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2013 error = e
						.getAEATRetencionesError2013();
				List<TipoRetenedorError2013> retenedores = error.getRetenedor();
				String message = null;
				TipoRetenedorError2013 retenedor = retenedores.get(0);
				List<TipoRetenidoError2013> retenidos = retenedor.getRetenido();
				if (retenidos.size() > 0) {
					TipoRetenidoError2013 retenido = retenidos.get(0);
					List<TipoError> tipoErrores = retenido.getError();
					if (tipoErrores.size() > 0) {
						TipoError tipoError = tipoErrores.get(0);
						message = tipoError.getDescripcion();
					}
				}
				if (message == null) {
					List<TipoErrorGeneral> errores = error.getErrorGeneral();
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
		public AEATRetencionesSalida2015 calculate2015(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2015 aeatRetencionesEntrada2015 = AEATRetencionesEntradaFactory
						.create2015(ctx);
				return calculate(aeatRetencionesEntrada2015);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2015 error = e
						.getAEATRetencionesError2015();
				
				for (com.esferalia.aon.aeat.jaxb.TipoErrorGeneral tipoErrorGeneral : error
						.getErrorGeneral())
					throw new ExpressionExceptionWrapper(new CheckException(
							tipoErrorGeneral.getDescripcion()));

				
				List<TipoRetenedorError2015> retenedores = error.getRetenedor();

				String message = null;
				TipoRetenedorError2015 retenedor = retenedores.get(0);
				List<TipoRetenidoError2015> retenidos = retenedor.getRetenido();
				if (retenidos.size() > 0) {
					TipoRetenidoError2015 retenido = retenidos.get(0);
					List<com.esferalia.aon.aeat.jaxb.TipoError> tipoErrores = retenido
							.getError();
					if (tipoErrores.size() > 0) {
						com.esferalia.aon.aeat.jaxb.TipoError tipoError = tipoErrores
								.get(0);
						message = tipoError.getDescripcion();
					}
				}
				if (message == null) {
					List<com.esferalia.aon.aeat.jaxb.TipoErrorGeneral> errores = error
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
		private AEATRetencionesSalida2013 calculate(
				AEATRetencionesEntrada2013 entrada2013)
				throws IrpfCalculateException {
			ModeloRetencionesXMLJaxb modeloRetencionesXMLJaxb = new ModeloRetencionesXMLJaxb();

			modeloRetencionesXMLJaxb.setEntradaRetenciones(entrada2013);
			modeloRetencionesXMLJaxb
					.setXMLProgressListener(new XMLProgressListener() {
						@Override
						public void avanzarBarraProgreso() {
						}
					});

			try {
				modeloRetencionesXMLJaxb.calcularXML();
			} catch (NullPointerException ignore) {
			}

			AEATRetencionesError2013 error2013 = modeloRetencionesXMLJaxb
					.getSalidaXMLError();
			if (error2013 != null)
				throw new IrpfCalculateException(error2013);

			AEATRetencionesSalida2013 salida2013 = modeloRetencionesXMLJaxb
					.getSalidaRetenciones();

			return salida2013;
		}

		private AEATRetencionesSalida2015 calculate(
				AEATRetencionesEntrada2015 entrada2015)
				throws IrpfCalculateException, JAXBException, IOException {

			Marshaller marshaller = JAXBContext.newInstance(
					AEATRetencionesEntrada2015.class).createMarshaller();
			File entrada2015File = File.createTempFile(
					AEATRetencionesEntrada2015.class.getSimpleName(), null);
			marshaller.marshal(entrada2015, entrada2015File);

			File salida2015File = File.createTempFile(
					AEATRetencionesSalida2015.class.getSimpleName(), null);
			File error2015File = File.createTempFile(
					AEATRetencionesError2015.class.getSimpleName(), null);

			ModuloCalculo.procesarFicheroXml(entrada2015File.getAbsolutePath(),
					error2015File.getAbsolutePath(), null,
					salida2015File.getAbsolutePath());
			entrada2015File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
					AEATRetencionesError2015.class).createUnmarshaller();
			try {
				AEATRetencionesError2015 error2015 = (AEATRetencionesError2015) unMarshaller
						.unmarshal(error2015File);
				error2015File.delete();
				throw new IrpfCalculateException(error2015);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2015File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2015.class).createUnmarshaller();
			AEATRetencionesSalida2015 salida2015 = (AEATRetencionesSalida2015) unMarshaller
					.unmarshal(salida2015File);
			salida2015File.delete();
			return salida2015;
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
		public AEATRetencionesSalida2013 calculate2013(
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

			double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							sqlCtx.getChargeDate().getTime()));

			TipoRetenidoSalida2013 retenidoSalida = new TipoRetenidoSalida2013();
			retenidoSalida.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida.setComunidadAutonoma(geozone);

			ArrayList<TipoRetenidoSalida2013> retenidosSalida = new ArrayList<TipoRetenidoSalida2013>(
					1);
			retenidosSalida.add(retenidoSalida);
			TipoRetenedorSalida2013 retenedorSalida2013 = new TipoRetenedorSalida2013();
			retenedorSalida2013.setRetenido(retenidosSalida);
			ArrayList<TipoRetenedorSalida2013> retenedoresSalida = new ArrayList<TipoRetenedorSalida2013>(
					1);
			retenedoresSalida.add(retenedorSalida2013);
			AEATRetencionesSalida2013 aeatRetencionesSalida = new AEATRetencionesSalida2013();
			aeatRetencionesSalida.setRetenedor(retenedoresSalida);

			return aeatRetencionesSalida;
		}

		@Override
		public AEATRetencionesSalida2015 calculate2015(
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

			double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							sqlCtx.getChargeDate().getTime()));

			TipoRetenidoSalida2015 retenidoSalida = new TipoRetenidoSalida2015();
			retenidoSalida.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida.setComunidadAutonoma(geozone);

			TipoRetenedorSalida2015 retenedorSalida2015 = new TipoRetenedorSalida2015();
			List<TipoRetenidoSalida2015> retenidosSalida = retenedorSalida2015
					.getRetenido();
			retenidosSalida.add(retenidoSalida);

			AEATRetencionesSalida2015 aeatRetencionesSalida = new AEATRetencionesSalida2015();
			List<TipoRetenedorSalida2015> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2015);

			return aeatRetencionesSalida;
		}

	}

}
