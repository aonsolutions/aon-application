package com.esferalia.aon.payroll.irpf;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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
import com.esferalia.aon.payroll.enumeration.DeductHomeLoan;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Contrato;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Discapacidad;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.SituacionFamiliar;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.server.AonDateUtils;

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
import net.aonsolutions.core.aeat.v2020.jaxb.AEATRetencionesEntrada2020;
import net.aonsolutions.core.aeat.v2020.jaxb.AEATRetencionesError2020;
import net.aonsolutions.core.aeat.v2020.jaxb.AEATRetencionesSalida2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenedorEntrada2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenedorError2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenedorSalida2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoError2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020;
import net.aonsolutions.core.aeat.v2021.jaxb.AEATRetencionesEntrada2021;
import net.aonsolutions.core.aeat.v2021.jaxb.AEATRetencionesError2021;
import net.aonsolutions.core.aeat.v2021.jaxb.AEATRetencionesSalida2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenedorEntrada2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenedorError2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenedorSalida2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoError2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021;
import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesEntrada2022;
import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesError2022;
import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesSalida2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoError;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoErrorGeneral;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenedorEntrada2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenedorError2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenedorSalida2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoError2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022;
import net.aonsolutions.core.aeat.v2023.jaxb.AEATRetencionesEntrada2023;
import net.aonsolutions.core.aeat.v2023.jaxb.AEATRetencionesError2023;
import net.aonsolutions.core.aeat.v2023.jaxb.AEATRetencionesSalida2023;
import net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenedorEntrada2023;
import net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenedorError2023;
import net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenedorSalida2023;
import net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoEntrada2023;
import net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoError2023;
import net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023;
import net.aonsolutions.core.aeat.v2024.jaxb.AEATRetencionesEntrada2024;
import net.aonsolutions.core.aeat.v2024.jaxb.AEATRetencionesError2024;
import net.aonsolutions.core.aeat.v2024.jaxb.AEATRetencionesSalida2024;
import net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenedorEntrada2024;
import net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenedorError2024;
import net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenedorSalida2024;
import net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoEntrada2024;
import net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoError2024;
import net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024;

public class IrpfCalculator {

	public static String DEFAULT_NIF = "87449445H";
	public static String DEFAULT_CIF = "Z7896423E";

	public static boolean isValidNif(String nif) {
		ValidaNif validaNif = new ValidaNif();
		validaNif.checkNif(nif);
		return validaNif.isOk();
	}

	public static IrpfOutcome calculateIrpf(IIrpfCalculatorContext ctx, Date date) {
		int year = AonDateUtils.getYear(date);
		IrpfOutcome irpfOutcome;
		if ( year == 2020 ) {
		    irpfOutcome = calculateIrpf2020(ctx);
		} else if ( year == 2021 ) {
		    irpfOutcome = calculateIrpf2021(ctx);
		} else if ( year == 2022 ) {
		    irpfOutcome = calculateIrpf2022(ctx);
		} else if ( year == 2023 ){		
		    irpfOutcome = calculateIrpf2023(ctx, date);
		} else {
		    irpfOutcome = calculateIrpf2024(ctx, date);
		}
		
		irpfOutcome.getIrpfResult()
		.setEffectiveDate(date);
		
		return irpfOutcome;
	}
	
	
	// ------------------------------------------------------------------- 2020

	public static IrpfOutcome calculateIrpf2020(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2020 aeatRetencionesSalida2020 = calculate2020(ctx);
		List<TipoRetenedorSalida2020> retenedores = aeatRetencionesSalida2020
				.getRetenedor();
		TipoRetenedorSalida2020 retenedorSalida2020 = retenedores.get(0);
		List<TipoRetenidoSalida2020> retenidos = retenedorSalida2020
				.getRetenido();
		TipoRetenidoSalida2020 retenidoSalida2020 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2020);
	}
	// ------------------------------------------------------------------- 2021

	public static IrpfOutcome calculateIrpf2021(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2021 aeatRetencionesSalida2021 = calculate2021(ctx);
		List<TipoRetenedorSalida2021> retenedores = aeatRetencionesSalida2021
				.getRetenedor();
		TipoRetenedorSalida2021 retenedorSalida2021 = retenedores.get(0);
		List<TipoRetenidoSalida2021> retenidos = retenedorSalida2021
				.getRetenido();
		TipoRetenidoSalida2021 retenidoSalida2021 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2021);
	}

	// ------------------------------------------------------------------- 2022

	public static IrpfOutcome calculateIrpf2022(IIrpfCalculatorContext ctx) {
		AEATRetencionesSalida2022 aeatRetencionesSalida2022 = calculate2022(ctx);
		List<TipoRetenedorSalida2022> retenedores = aeatRetencionesSalida2022
				.getRetenedor();
		TipoRetenedorSalida2022 retenedorSalida2022 = retenedores.get(0);
		List<TipoRetenidoSalida2022> retenidos = retenedorSalida2022
				.getRetenido();
		TipoRetenidoSalida2022 retenidoSalida2022 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2022);
	}

	// ------------------------------------------------------------------- 2023

	public static IrpfOutcome calculateIrpf2023(IIrpfCalculatorContext ctx, Date date) {
		AEATRetencionesSalida2023 aeatRetencionesSalida2023 = calculate2023(ctx, date);
		List<TipoRetenedorSalida2023> retenedores = aeatRetencionesSalida2023
				.getRetenedor();
		TipoRetenedorSalida2023 retenedorSalida2023 = retenedores.get(0);
		List<TipoRetenidoSalida2023> retenidos = retenedorSalida2023
				.getRetenido();
		TipoRetenidoSalida2023 retenidoSalida2023 = retenidos.get(0);
		return transfom(ctx, retenidoSalida2023);
	}

	// ------------------------------------------------------------------- 2024

	public static IrpfOutcome calculateIrpf2024(IIrpfCalculatorContext ctx, Date date) {
		AEATRetencionesSalida2024 aeatRetencionesSalida2024 = calculate2024(ctx, date);
		List<TipoRetenedorSalida2024> retenedores = aeatRetencionesSalida2024
				.getRetenedor();
		TipoRetenedorSalida2024 retenedorSalida2024 = retenedores.get(0);
		List<TipoRetenidoSalida2024> retenidos = retenedorSalida2024
				.getRetenido();
		TipoRetenidoSalida2024 retenidoSalida2024 = retenidos.get(0);		
		return transfom(ctx, retenidoSalida2024);
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
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad(), ctx.getMovilidadReducida()));
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
			TipoRetenidoSalida2020 retenidoSalida2020) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2020
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2020.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2020.getBaseRetencion()));
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2020
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(null /*
												 * retenidoSalida2016.
												 * getDeduccion80Bis()
												 */));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2020
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2020
				.getImpAnualRetencionesIngresosCuenta()));

		// DATOS PERSONALES DEL PERCEPTOR
		IrpfData irpfData = new IrpfData();
		irpfData.setFamilySituation(toFamilySituation(ctx
				.getSituacionFamiliar()));
		irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
		irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad(), ctx.getMovilidadReducida()));
		irpfOutcome.setIrpfData(irpfData);

		// DATOS ECONOMICOS
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2020
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(null /*
															 * retenidoSalida2016.
															 * getDeduccion80Bis
															 * ()
															 */));
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Reduccion reduccion = retenidoSalida2020
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
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2020
				.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2020
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2020
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes descendientes = retenidoSalida2020
				.getDescendientes();
		if (descendientes != null) {
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.Menores3Años menores3Años2020 = descendientes
					.getMenores3Años();
			if (menores3Años2020 != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años2020
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años2020
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.Resto resto2020 = descendientes
					.getResto();
			if (resto2020 != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto2020
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto2020
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ComputoDescendientes computoDescendientes2020 = descendientes
					.getComputoDescendientes();
			if (computoDescendientes2020 != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes2020
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes2020
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes2020
						.getHijo3()));
				net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2020 = computoDescendientes2020
						.getCuartoySucesivos();
				if (cuartoySucesivos2020 != null) {
					irpfResult
							.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos2020
									.getTotal()));
					irpfResult
							.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos2020
									.getPorEntero()));
				}
			}
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ConDiscapacidad conDiscapacidad2020 = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad2020 != null) {
				net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad2020
						.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida2020 = grado1
							.getConMovilidadReducida();
					if (conMovilidadReducida2020 != null) {
						irpfResult
								.setDescendentsMovingTotal(toInteger(conMovilidadReducida2020
										.getTotal()));
						irpfResult
								.setDescendentsMovingEntirely(toInteger(conMovilidadReducida2020
										.getPorEntero()));
					}
				}
				net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ConDiscapacidad.EnGrado2 grado22020 = conDiscapacidad2020
						.getEnGrado2();
				if (grado22020 != null) {
					irpfResult.setDescendents65Total(toInteger(grado22020
							.getTotal()));
					irpfResult.setDescendents65Entirely(toInteger(grado22020
							.getPorEntero()));
				}

			}

		}

		
		
		// ASCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Ascendientes ascendientes = retenidoSalida2020
				.getAscendientes();
		if (ascendientes != null) {
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Ascendientes.Menores75 menores75 = ascendientes
					.getMenores75();
			if (menores75 != null) {
			}
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Ascendientes.Mayores75 mayores75 = ascendientes
					.getMayores75();
			if (mayores75 != null) {
			}
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Regularizacion regularizacion = retenidoSalida2020
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
			TipoRetenidoSalida2021 retenidoSalida2021) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2021
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2021.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2021.getBaseRetencion()));
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2021
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(null /*
												 * retenidoSalida2016.
												 * getDeduccion80Bis()
												 */));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2021
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2021
				.getImpAnualRetencionesIngresosCuenta()));

		// DATOS PERSONALES DEL PERCEPTOR
		IrpfData irpfData = new IrpfData();
		irpfData.setFamilySituation(toFamilySituation(ctx
				.getSituacionFamiliar()));
		irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
		irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad(), ctx.getMovilidadReducida()));
		irpfOutcome.setIrpfData(irpfData);

		// DATOS ECONOMICOS
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2021
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(null /*
															 * retenidoSalida2016.
															 * getDeduccion80Bis
															 * ()
															 */));
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Reduccion reduccion = retenidoSalida2021
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
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2021
				.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2021
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2021
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes descendientes = retenidoSalida2021
				.getDescendientes();
		if (descendientes != null) {
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.Menores3Años menores3Años2021 = descendientes
					.getMenores3Años();
			if (menores3Años2021 != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años2021
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años2021
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.Resto resto2021 = descendientes
					.getResto();
			if (resto2021 != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto2021
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto2021
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ComputoDescendientes computoDescendientes2021 = descendientes
					.getComputoDescendientes();
			if (computoDescendientes2021 != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes2021
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes2021
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes2021
						.getHijo3()));
				net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2021 = computoDescendientes2021
						.getCuartoySucesivos();
				if (cuartoySucesivos2021 != null) {
					irpfResult
							.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos2021
									.getTotal()));
					irpfResult
							.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos2021
									.getPorEntero()));
				}
			}
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ConDiscapacidad conDiscapacidad2021 = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad2021 != null) {
				net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad2021
						.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida2021 = grado1
							.getConMovilidadReducida();
					if (conMovilidadReducida2021 != null) {
						irpfResult
								.setDescendentsMovingTotal(toInteger(conMovilidadReducida2021
										.getTotal()));
						irpfResult
								.setDescendentsMovingEntirely(toInteger(conMovilidadReducida2021
										.getPorEntero()));
					}
				}
				net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ConDiscapacidad.EnGrado2 grado22021 = conDiscapacidad2021
						.getEnGrado2();
				if (grado22021 != null) {
					irpfResult.setDescendents65Total(toInteger(grado22021
							.getTotal()));
					irpfResult.setDescendents65Entirely(toInteger(grado22021
							.getPorEntero()));
				}

			}

		}

		
		
		// ASCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Ascendientes ascendientes = retenidoSalida2021
				.getAscendientes();
		if (ascendientes != null) {
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Ascendientes.Menores75 menores75 = ascendientes
					.getMenores75();
			if (menores75 != null) {
			}
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Ascendientes.Mayores75 mayores75 = ascendientes
					.getMayores75();
			if (mayores75 != null) {
			}
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Regularizacion regularizacion = retenidoSalida2021
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
			TipoRetenidoSalida2022 retenidoSalida2022) {

		IrpfOutcome irpfOutcome = new IrpfOutcome();

		irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
		irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
		irpfOutcome.setComunidadAutonoma(retenidoSalida2022
				.getComunidadAutonoma());

		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setEffectiveDate(new Date()); // TODO: Now ???

		irpfResult.setIrpf(toDouble(retenidoSalida2022.getTipoRetencion()));

		irpfResult.setBaseIrpf(toDouble(retenidoSalida2022.getBaseRetencion()));
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2022
				.getMinimoPersonalFamiliar();
		irpfResult
				.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
						.getTotal()) : 0.00);
		irpfResult.setDeduct80Bis(toDouble(null /*
												 * retenidoSalida2022.
												 * getDeduccion80Bis()
												 */));
		irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2022
				.getMinoracionPrestamo()));
		irpfResult.setAnnualIrpf(toDouble(retenidoSalida2022
				.getImpAnualRetencionesIngresosCuenta()));

		// DATOS PERSONALES DEL PERCEPTOR
		IrpfData irpfData = new IrpfData();
		irpfData.setFamilySituation(toFamilySituation(ctx
				.getSituacionFamiliar()));
		irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
		irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
		irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad(), ctx.getMovilidadReducida()));
		irpfData.setDeductHomeLoan(toDeductHomeLoan(retenidoSalida2022.getPagoPrestamosVivienda()));
		irpfOutcome.setIrpfData(irpfData);

		// DATOS ECONOMICOS
		irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2022
				.getRetribAnuales()));
		irpfResult.setIrregular18_2Reduction(toDouble(null /*
															 * retenidoSalida2016.
															 * getDeduccion80Bis
															 * ()
															 */));
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Reduccion reduccion = retenidoSalida2022
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
		irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2022
				.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
		irpfResult.setSpousalSupport(toDouble(retenidoSalida2022
				.getPensionCompensatoria()));
		irpfResult.setFoodAnnuity(toDouble(retenidoSalida2022
				.getAnualidadesHijos()));

		// DESCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes descendientes = retenidoSalida2022
				.getDescendientes();
		if (descendientes != null) {
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.Menores3Años menores3Años2022 = descendientes
					.getMenores3Años();
			if (menores3Años2022 != null) {
				irpfResult.setDescendentsMinor3Total(toInteger(menores3Años2022
						.getTotal()));
				irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años2022
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.Resto resto2022 = descendientes
					.getResto();
			if (resto2022 != null) {
				irpfResult.setDescendentsRemainderTotal(toInteger(resto2022
						.getTotal()));
				irpfResult.setDescendentsRemainderEntirely(toInteger(resto2022
						.getPorEntero()));
			}
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ComputoDescendientes computoDescendientes2022 = descendientes
					.getComputoDescendientes();
			if (computoDescendientes2022 != null) {
				irpfResult.setDescendentsFirst(toInteger(computoDescendientes2022
						.getHijo1()));
				irpfResult.setDescendentsSecond(toInteger(computoDescendientes2022
						.getHijo2()));
				irpfResult.setDescendentsThird(toInteger(computoDescendientes2022
						.getHijo3()));
				net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2022 = computoDescendientes2022
						.getCuartoySucesivos();
				if (cuartoySucesivos2022 != null) {
					irpfResult
							.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos2022
									.getTotal()));
					irpfResult
							.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos2022
									.getPorEntero()));
				}
			}
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ConDiscapacidad conDiscapacidad2022 = descendientes
					.getConDiscapacidad();
			if (conDiscapacidad2022 != null) {
				net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad2022
						.getEnGrado1();
				if (grado1 != null) {
					irpfResult.setDescendents33_65Total(toInteger(grado1
							.getTotal()));
					irpfResult.setDescendents33_65Entirely(toInteger(grado1
							.getPorEntero()));
					net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida2022 = grado1
							.getConMovilidadReducida();
					if (conMovilidadReducida2022 != null) {
						irpfResult
								.setDescendentsMovingTotal(toInteger(conMovilidadReducida2022
										.getTotal()));
						irpfResult
								.setDescendentsMovingEntirely(toInteger(conMovilidadReducida2022
										.getPorEntero()));
					}
				}
				net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ConDiscapacidad.EnGrado2 grado22022 = conDiscapacidad2022
						.getEnGrado2();
				if (grado22022 != null) {
					irpfResult.setDescendents65Total(toInteger(grado22022
							.getTotal()));
					irpfResult.setDescendents65Entirely(toInteger(grado22022
							.getPorEntero()));
				}

			}

		}

		
		
		// ASCENCIENTES COMPUTADOS
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Ascendientes ascendientes = retenidoSalida2022
				.getAscendientes();
		if (ascendientes != null) {
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Ascendientes.Menores75 menores75 = ascendientes
					.getMenores75();
			if (menores75 != null) {
			}
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Ascendientes.Mayores75 mayores75 = ascendientes
					.getMayores75();
			if (mayores75 != null) {
			}
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
					.getConDiscapacidad();
			if (conDiscapacidad != null) {

			}
		}

		irpfOutcome.setIrpfResult(irpfResult);

		// REEGULARIZACION
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Regularizacion regularizacion = retenidoSalida2022
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
		TipoRetenidoSalida2023 retenidoSalida2023) {

        	IrpfOutcome irpfOutcome = new IrpfOutcome();
        
        	irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
        	irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
        	irpfOutcome.setComunidadAutonoma(retenidoSalida2023
        			.getComunidadAutonoma());
        
        	IrpfResult irpfResult = new IrpfResult();
        	irpfResult.setEffectiveDate(new Date()); // TODO: Now ???
        
        	irpfResult.setIrpf(toDouble(retenidoSalida2023.getTipoRetencion()));
        
        	irpfResult.setBaseIrpf(toDouble(retenidoSalida2023.getBaseRetencion()));
        	net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2023
        			.getMinimoPersonalFamiliar();
        	irpfResult
        			.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
        					.getTotal()) : 0.00);
        	irpfResult.setDeduct80Bis(toDouble(null /*
        											 * retenidoSalida2023.
        											 * getDeduccion80Bis()
        											 */));
        	irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2023
        			.getMinoracionPrestamo()));
        	irpfResult.setAnnualIrpf(toDouble(retenidoSalida2023
        			.getImpAnualRetencionesIngresosCuenta()));
        
        	// DATOS PERSONALES DEL PERCEPTOR
        	IrpfData irpfData = new IrpfData();
        	irpfData.setFamilySituation(toFamilySituation(ctx
        			.getSituacionFamiliar()));
        	irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
        	irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
        	irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad(), ctx.getMovilidadReducida()));
        	irpfData.setDeductHomeLoan(toDeductHomeLoan(retenidoSalida2023.getPagoPrestamosVivienda()));
        	irpfOutcome.setIrpfData(irpfData);
        
        	// DATOS ECONOMICOS
        	irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2023
        			.getRetribAnuales()));
        	irpfResult.setIrregular18_2Reduction(toDouble(null /*
        														 * retenidoSalida2016.
        														 * getDeduccion80Bis
        														 * ()
        														 */));
        	net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Reduccion reduccion = retenidoSalida2023
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
        	irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2023
        			.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
        	irpfResult.setSpousalSupport(toDouble(retenidoSalida2023
        			.getPensionCompensatoria()));
        	irpfResult.setFoodAnnuity(toDouble(retenidoSalida2023
        			.getAnualidadesHijos()));
        
        	// DESCENCIENTES COMPUTADOS
        	net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes descendientes = retenidoSalida2023
        			.getDescendientes();
        	if (descendientes != null) {
        		net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.Menores3Años menores3Años2023 = descendientes
        				.getMenores3Años();
        		if (menores3Años2023 != null) {
        			irpfResult.setDescendentsMinor3Total(toInteger(menores3Años2023
        					.getTotal()));
        			irpfResult.setDescendentsMinor3Entirely(toInteger(menores3Años2023
        					.getPorEntero()));
        		}
        		net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.Resto resto2023 = descendientes
        				.getResto();
        		if (resto2023 != null) {
        			irpfResult.setDescendentsRemainderTotal(toInteger(resto2023
        					.getTotal()));
        			irpfResult.setDescendentsRemainderEntirely(toInteger(resto2023
        					.getPorEntero()));
        		}
        		net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ComputoDescendientes computoDescendientes2023 = descendientes
        				.getComputoDescendientes();
        		if (computoDescendientes2023 != null) {
        			irpfResult.setDescendentsFirst(toInteger(computoDescendientes2023
        					.getHijo1()));
        			irpfResult.setDescendentsSecond(toInteger(computoDescendientes2023
        					.getHijo2()));
        			irpfResult.setDescendentsThird(toInteger(computoDescendientes2023
        					.getHijo3()));
        			net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2023 = computoDescendientes2023
        					.getCuartoySucesivos();
        			if (cuartoySucesivos2023 != null) {
        				irpfResult
        						.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos2023
        								.getTotal()));
        				irpfResult
        						.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos2023
        								.getPorEntero()));
        			}
        		}
        		net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ConDiscapacidad conDiscapacidad2023 = descendientes
        				.getConDiscapacidad();
        		if (conDiscapacidad2023 != null) {
        			net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad2023
        					.getEnGrado1();
        			if (grado1 != null) {
        				irpfResult.setDescendents33_65Total(toInteger(grado1
        						.getTotal()));
        				irpfResult.setDescendents33_65Entirely(toInteger(grado1
        						.getPorEntero()));
        				net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida2023 = grado1
        						.getConMovilidadReducida();
        				if (conMovilidadReducida2023 != null) {
        					irpfResult
        							.setDescendentsMovingTotal(toInteger(conMovilidadReducida2023
        									.getTotal()));
        					irpfResult
        							.setDescendentsMovingEntirely(toInteger(conMovilidadReducida2023
        									.getPorEntero()));
        				}
        			}
        			net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ConDiscapacidad.EnGrado2 grado22023 = conDiscapacidad2023
        					.getEnGrado2();
        			if (grado22023 != null) {
        				irpfResult.setDescendents65Total(toInteger(grado22023
        						.getTotal()));
        				irpfResult.setDescendents65Entirely(toInteger(grado22023
        						.getPorEntero()));
        			}
        
        		}
        
        	}
        
        	
        	
        	// ASCENCIENTES COMPUTADOS
        	net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Ascendientes ascendientes = retenidoSalida2023
        			.getAscendientes();
        	if (ascendientes != null) {
        		net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Ascendientes.Menores75 menores75 = ascendientes
        				.getMenores75();
        		if (menores75 != null) {
        		}
        		net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Ascendientes.Mayores75 mayores75 = ascendientes
        				.getMayores75();
        		if (mayores75 != null) {
        		}
        		net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
        				.getConDiscapacidad();
        		if (conDiscapacidad != null) {
        
        		}
        	}
        
        	irpfOutcome.setIrpfResult(irpfResult);
        
        	// REEGULARIZACION
        	net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoEntrada2023.Regularizacion regularizacion = retenidoSalida2023
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
		TipoRetenidoSalida2024 retenidoSalida2024) {

        	IrpfOutcome irpfOutcome = new IrpfOutcome();
        
        	irpfOutcome.setNif(ctx.getNif() == DEFAULT_NIF ? null : ctx.getNif());
        	irpfOutcome.setBirthYear(ctx.getAñoNacimiento());
        	irpfOutcome.setComunidadAutonoma(ctx.getComunidadAutonoma());
        	
        
        	IrpfResult irpfResult = new IrpfResult();
        	irpfResult.setEffectiveDate(new Date()); // TODO: Now ???
        
        	irpfResult.setIrpf(toDouble(retenidoSalida2024.getTipoRetencion()));
        
        	irpfResult.setBaseIrpf(toDouble(retenidoSalida2024.getBaseRetencion()));
        	net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.MinimoPersonalFamiliar minimoPersonalFamiliar = retenidoSalida2024
        			.getMinimoPersonalFamiliar();
        	irpfResult
        			.setMinimunPersonalFamily(minimoPersonalFamiliar != null ? toDouble(minimoPersonalFamiliar
        					.getTotal()) : 0.00);
        	irpfResult.setDeduct80Bis(toDouble(null /*
        											 * retenidoSalida2024.
        											 * getDeduccion80Bis()
        											 */));
        	irpfResult.setDeductHomeLoanAmount(toDouble(retenidoSalida2024
        			.getMinoracionPrestamo()));
        	irpfResult.setAnnualIrpf(toDouble(retenidoSalida2024
        			.getImpAnualRetencionesIngresosCuenta()));
        
        	// DATOS PERSONALES DEL PERCEPTOR
        	IrpfData irpfData = new IrpfData();
        	irpfData.setFamilySituation(toFamilySituation(ctx
        			.getSituacionFamiliar()));
        	irpfData.setLabourProlongation(ctx.getProlongacionLaboral());
        	irpfData.setMovingDate(ctx.getMovilidadGeografica() ? new Date() : null);
        	irpfData.setDisabilityLevel(toDisabilityLevel(ctx.getDiscapacidad(), ctx.getMovilidadReducida()));
        	irpfData.setDeductHomeLoan(toDeductHomeLoan(retenidoSalida2024.getPagoPrestamosVivienda()));
        	irpfOutcome.setIrpfData(irpfData);
        
        	// DATOS ECONOMICOS
        	irpfResult.setAnnualRemuneration(toDouble(retenidoSalida2024
        			.getRetribAnuales()));
        	irpfResult.setIrregular18_2Reduction(toDouble(null /*
        														 * retenidoSalida2016.
        														 * getDeduccion80Bis
        														 * ()
        														 */));
        	net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Reduccion reduccion = retenidoSalida2024
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
        	irpfResult.setDeducciblesExpenses(toDouble(retenidoSalida2024
        			.getCotizaciones()/* retenidoSalida2016.getGastosAnuales() */));
        	irpfResult.setSpousalSupport(toDouble(retenidoSalida2024
        			.getPensionCompensatoria()));
        	irpfResult.setFoodAnnuity(toDouble(retenidoSalida2024
        			.getAnualidadesHijos()));
        
        	// DESCENCIENTES COMPUTADOS
        	net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes descendientes = retenidoSalida2024
        			.getDescendientes();
        	if (descendientes != null) {
        		net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.Menores3 menores32024 = descendientes
        				.getMenores3();
        		if (menores32024 != null) {
        			irpfResult.setDescendentsMinor3Total(toInteger(menores32024
        					.getTotal()));
        			irpfResult.setDescendentsMinor3Entirely(toInteger(menores32024
        					.getPorEntero()));
        		}
        		net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.Resto resto2024 = descendientes
        				.getResto();
        		if (resto2024 != null) {
        			irpfResult.setDescendentsRemainderTotal(toInteger(resto2024
        					.getTotal()));
        			irpfResult.setDescendentsRemainderEntirely(toInteger(resto2024
        					.getPorEntero()));
        		}
        		net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ComputoDescendientes computoDescendientes2024 = descendientes
        				.getComputoDescendientes();
        		if (computoDescendientes2024 != null) {
        			irpfResult.setDescendentsFirst(toInteger(computoDescendientes2024
        					.getHijo1()));
        			irpfResult.setDescendentsSecond(toInteger(computoDescendientes2024
        					.getHijo2()));
        			irpfResult.setDescendentsThird(toInteger(computoDescendientes2024
        					.getHijo3()));
        			net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2024 = computoDescendientes2024
        					.getCuartoySucesivos();
        			if (cuartoySucesivos2024 != null) {
        				irpfResult
        						.setDescendentsFourthSubsequentTotal(toInteger(cuartoySucesivos2024
        								.getTotal()));
        				irpfResult
        						.setDescendentsFourthSubsequentEntirely(toInteger(cuartoySucesivos2024
        								.getPorEntero()));
        			}
        		}
        		net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ConDiscapacidad conDiscapacidad2024 = descendientes
        				.getConDiscapacidad();
        		if (conDiscapacidad2024 != null) {
        			net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ConDiscapacidad.EnGrado1 grado1 = conDiscapacidad2024
        					.getEnGrado1();
        			if (grado1 != null) {
        				irpfResult.setDescendents33_65Total(toInteger(grado1
        						.getTotal()));
        				irpfResult.setDescendents33_65Entirely(toInteger(grado1
        						.getPorEntero()));
        				net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ConDiscapacidad.EnGrado1.ConMovilidadReducida conMovilidadReducida2024 = grado1
        						.getConMovilidadReducida();
        				if (conMovilidadReducida2024 != null) {
        					irpfResult
        							.setDescendentsMovingTotal(toInteger(conMovilidadReducida2024
        									.getTotal()));
        					irpfResult
        							.setDescendentsMovingEntirely(toInteger(conMovilidadReducida2024
        									.getPorEntero()));
        				}
        			}
        			net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ConDiscapacidad.EnGrado2 grado22024 = conDiscapacidad2024
        					.getEnGrado2();
        			if (grado22024 != null) {
        				irpfResult.setDescendents65Total(toInteger(grado22024
        						.getTotal()));
        				irpfResult.setDescendents65Entirely(toInteger(grado22024
        						.getPorEntero()));
        			}
        
        		}
        
        	}
        
        	
        	
        	// ASCENCIENTES COMPUTADOS
        	net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Ascendientes ascendientes = retenidoSalida2024
        			.getAscendientes();
        	if (ascendientes != null) {
        		net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Ascendientes.Menores75 menores75 = ascendientes
        				.getMenores75();
        		if (menores75 != null) {
        		}
        		net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Ascendientes.Mayores75 mayores75 = ascendientes
        				.getMayores75();
        		if (mayores75 != null) {
        		}
        		net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Ascendientes.ConDiscapacidad conDiscapacidad = ascendientes
        				.getConDiscapacidad();
        		if (conDiscapacidad != null) {
        
        		}
        	}
        
        	irpfOutcome.setIrpfResult(irpfResult);
        
        	// REEGULARIZACION
        	net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoEntrada2024.Regularizacion regularizacion = retenidoSalida2024
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

	protected static AEATRetencionesSalida2020 calculate2020(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2020(ctx);

		return AEATCalculate.INSTANCE.calculate2020(ctx);
	}

	protected static AEATRetencionesSalida2021 calculate2021(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2021(ctx);

		return AEATCalculate.INSTANCE.calculate2021(ctx);
	}

	protected static AEATRetencionesSalida2022 calculate2022(
			IIrpfCalculatorContext ctx) {
		ctx.next();

		for (Calculate calculate : ForalCalculate.INSTANCES)
			if (calculate.accept(ctx))
				return calculate.calculate2022(ctx);

		return AEATCalculate.INSTANCE.calculate2022(ctx);
	}

	protected static AEATRetencionesSalida2023 calculate2023(
		IIrpfCalculatorContext ctx, Date date) {
        	ctx.next();
        
        	for (Calculate calculate : ForalCalculate.INSTANCES)
        		if (calculate.accept(ctx))
        			return calculate.calculate2023(ctx, date);
        
        	return AEATCalculate.INSTANCE.calculate2023(ctx, date);
	}

	protected static AEATRetencionesSalida2024 calculate2024(
		IIrpfCalculatorContext ctx, Date date) {
        	ctx.next();
        
        	for (Calculate calculate : ForalCalculate.INSTANCES)
        		if (calculate.accept(ctx))
        			return calculate.calculate2024(ctx, date);
        
        	return AEATCalculate.INSTANCE.calculate2024(ctx, date);
	}

	private static Integer toInteger(Byte b) {
		return b == null ? 0 : b.intValue();
	}

	private static Integer toInteger(TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
			net.aonsolutions.core.aeat.v2020.jaxb.TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
			net.aonsolutions.core.aeat.v2021.jaxb.TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
			net.aonsolutions.core.aeat.v2022.jaxb.TipoComputo tipoComputo) {
		return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
		net.aonsolutions.core.aeat.v2023.jaxb.TipoComputo tipoComputo) {
	    	return tipoComputo != null ? tipoComputo.ordinal() : null;
	}

	private static Integer toInteger(
		net.aonsolutions.core.aeat.v2024.jaxb.TipoComputo tipoComputo) {
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

	private static DisabilityLevel toDisabilityLevel(Discapacidad discapacidad, boolean movilidadReducida) {
		switch (discapacidad) {
		case GRADO1:
			return movilidadReducida ? 
					DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE 
					:  DisabilityLevel.GT_EQ_33_LT_65;
		case GRADO2:
			return DisabilityLevel.GT_EQ_65;
		default:
			return null;
		}

	}

	private static <T> DeductHomeLoan toDeductHomeLoan(T pagoPrestamosdeVivienda) {
		return pagoPrestamosdeVivienda == null ? null : DeductHomeLoan.AFTER_01_01_2001; 
	}

	private static IrpfRegularizationReason toIrpfRegularizationReason(int causa) {
		return IrpfRegularizationReason.valueof(causa);
	}

	private static interface Calculate {
		boolean accept(IIrpfCalculatorContext ctx);

		AEATRetencionesSalida2020 calculate2020(IIrpfCalculatorContext ctx);
		
		AEATRetencionesSalida2021 calculate2021(IIrpfCalculatorContext ctx);

		AEATRetencionesSalida2022 calculate2022(IIrpfCalculatorContext ctx);

		AEATRetencionesSalida2023 calculate2023(IIrpfCalculatorContext ctx, Date date);

		AEATRetencionesSalida2024 calculate2024(IIrpfCalculatorContext ctx, Date date);
	}

	private static class AEATCalculate implements Calculate {

		private static AEATCalculate INSTANCE = new AEATCalculate();

		@Override
		public boolean accept(IIrpfCalculatorContext ctx) {
			return true;
		}


		@Override
		public AEATRetencionesSalida2020 calculate2020(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2020 aeatRetencionesEntrada2020 = AEATRetencionesEntradaFactory
						.create2020(ctx);
				
				return calculate(aeatRetencionesEntrada2020);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2020 error = e
						.getAEATRetencionesError2020();

				for (net.aonsolutions.core.aeat.v2020.jaxb.TipoErrorGeneral tipoErrorGeneral : error
						.getErrorGeneral())
					throw new ExpressionExceptionWrapper(new CheckException(
							tipoErrorGeneral.getDescripcion()));

				List<TipoRetenedorError2020> retenedores = error.getRetenedor();

				String message = null;
				TipoRetenedorError2020 retenedor = retenedores.get(0);
				List<TipoRetenidoError2020> retenidos = retenedor.getRetenido();
				if (retenidos.size() > 0) {
					TipoRetenidoError2020 retenido = retenidos.get(0);
					List<net.aonsolutions.core.aeat.v2020.jaxb.TipoError> tipoErrores = retenido
							.getError();
					if (tipoErrores.size() > 0) {
						net.aonsolutions.core.aeat.v2020.jaxb.TipoError tipoError = tipoErrores
								.get(0);
						message = tipoError.getDescripcion();
					}
				}
				if (message == null) {
					List<net.aonsolutions.core.aeat.v2020.jaxb.TipoErrorGeneral> errores = error
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
		public AEATRetencionesSalida2021 calculate2021(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2021 aeatRetencionesEntrada2021 = AEATRetencionesEntradaFactory
						.create2021(ctx);
				
				return calculate(aeatRetencionesEntrada2021);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2021 error = e
						.getAEATRetencionesError2021();

				for (net.aonsolutions.core.aeat.v2021.jaxb.TipoErrorGeneral tipoErrorGeneral : error
						.getErrorGeneral())
					throw new ExpressionExceptionWrapper(new CheckException(
							tipoErrorGeneral.getDescripcion()));

				List<TipoRetenedorError2021> retenedores = error.getRetenedor();

				String message = null;
				TipoRetenedorError2021 retenedor = retenedores.get(0);
				List<TipoRetenidoError2021> retenidos = retenedor.getRetenido();
				if (retenidos.size() > 0) {
					TipoRetenidoError2021 retenido = retenidos.get(0);
					List<net.aonsolutions.core.aeat.v2021.jaxb.TipoError> tipoErrores = retenido
							.getError();
					if (tipoErrores.size() > 0) {
						net.aonsolutions.core.aeat.v2021.jaxb.TipoError tipoError = tipoErrores
								.get(0);
						message = tipoError.getDescripcion();
					}
				}
				if (message == null) {
					List<net.aonsolutions.core.aeat.v2021.jaxb.TipoErrorGeneral> errores = error
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
		public AEATRetencionesSalida2022 calculate2022(
				IIrpfCalculatorContext ctx) {
			try {
				AEATRetencionesEntrada2022 aeatRetencionesEntrada2022 = AEATRetencionesEntradaFactory
						.create2022(ctx);
				
				return calculate(aeatRetencionesEntrada2022);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2022 error = e
						.getAEATRetencionesError2022();

				String message = null;
				
				List<String> messages = new LinkedList<>();
				
				error.getErrorGeneral().stream()
				.map(TipoErrorGeneral::getDescripcion )
				.forEach( messages::add );

				List<TipoRetenedorError2022> retenedores = 
				error.getRetenedor();

				retenedores.stream()
				.map(TipoRetenedorError2022::getError)
				.filter(Objects::nonNull)
				.map( TipoError::getDescripcion)
				.forEach(messages::add);

				List<TipoRetenidoError2022> retenidos =
				retenedores.stream()
				.map(TipoRetenedorError2022::getRetenido)
				.flatMap(List::stream)
				.collect(Collectors.toList());
				
				// Errores 
				retenidos.stream()
				.map(TipoRetenidoError2022::getError)
				.flatMap(List::stream)
				.map(TipoError::getDescripcion )
				.forEach( messages::add );

				// Descendientes
				retenidos.stream()
				.map(TipoRetenidoError2022::getDescendiente)
				.flatMap(List::stream)
				.map(TipoRetenidoError2022.Descendiente::getError)
				.flatMap(List::stream)
				.map(TipoError::getDescripcion )
				.forEach( messages::add );
				
				// Ascendientes
				retenidos.stream()
				.map(TipoRetenidoError2022::getAscendiente)
				.flatMap(List::stream)
				.map(TipoRetenidoError2022.Ascendiente::getError)
				.flatMap(List::stream)
				.map(TipoError::getDescripcion )
				.forEach( messages::add )
				;

				message = messages.stream().findFirst().orElse("Error al calcular el IRPF");
				

				ExpressionException expressionException = new CheckException(
						message);
				throw new ExpressionExceptionWrapper(expressionException);
			}
		}
		@Override
		public AEATRetencionesSalida2023 calculate2023(
				IIrpfCalculatorContext ctx, Date date) {
			try {
				AEATRetencionesEntrada2023 aeatRetencionesEntrada2023 = AEATRetencionesEntradaFactory
						.create2023(ctx);
				
				return calculate(aeatRetencionesEntrada2023, date);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2023 error = e
						.getAEATRetencionesError2023();

				String message = null;
				
				List<String> messages = new LinkedList<>();
				
				error.getErrorGeneral().stream()
				.map(net.aonsolutions.core.aeat.v2023.jaxb.TipoErrorGeneral::getDescripcion)
				.forEach( messages::add );

				List<TipoRetenedorError2023> retenedores = 
				error.getRetenedor();

				retenedores.stream()
				.map(TipoRetenedorError2023::getError)
				.filter(Objects::nonNull)
				.map( net.aonsolutions.core.aeat.v2023.jaxb.TipoError::getDescripcion)
				.forEach(messages::add);

				List<TipoRetenidoError2023> retenidos =
				retenedores.stream()
				.map(TipoRetenedorError2023::getRetenido)
				.flatMap(List::stream)
				.collect(Collectors.toList());
				
				// Errores 
				retenidos.stream()
				.map(TipoRetenidoError2023::getError)
				.flatMap(List::stream)
				.map(net.aonsolutions.core.aeat.v2023.jaxb.TipoError::getDescripcion )
				.forEach( messages::add );

				// Descendientes
				retenidos.stream()
				.map(TipoRetenidoError2023::getDescendiente)
				.flatMap(List::stream)
				.map(TipoRetenidoError2023.Descendiente::getError)
				.flatMap(List::stream)
				.map(net.aonsolutions.core.aeat.v2023.jaxb.TipoError::getDescripcion )
				.forEach( messages::add );
				
				// Ascendientes
				retenidos.stream()
				.map(TipoRetenidoError2023::getAscendiente)
				.flatMap(List::stream)
				.map(TipoRetenidoError2023.Ascendiente::getError)
				.flatMap(List::stream)
				.map(net.aonsolutions.core.aeat.v2023.jaxb.TipoError::getDescripcion )
				.forEach( messages::add )
				;

				message = messages.stream().findFirst().orElse("Error al calcular el IRPF");
				

				ExpressionException expressionException = new CheckException(
						message);
				throw new ExpressionExceptionWrapper(expressionException);
			}
		}

		@Override
		public AEATRetencionesSalida2024 calculate2024(
				IIrpfCalculatorContext ctx, Date date) {
			try {
			    	AEATRetencionesEntrada2024 aeatRetencionesEntrada2024 = AEATRetencionesEntradaFactory.create2024(ctx);
				
				return calculate(aeatRetencionesEntrada2024, date);
			} catch (IOException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (JAXBException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			} catch (IrpfCalculateException e) {
				AEATRetencionesError2024 error = e
						.getAEATRetencionesError2024();

				String message = null;
				
				List<String> messages = new LinkedList<>();
				
				error.getErrorGeneral().stream()
				.map(net.aonsolutions.core.aeat.v2024.jaxb.TipoErrorGeneral::getDescripcion)
				.forEach( messages::add );

				List<TipoRetenedorError2024> retenedores = 
				error.getRetenedor();

				retenedores.stream()
				.map(TipoRetenedorError2024::getError)
				.filter(Objects::nonNull)
				.map( net.aonsolutions.core.aeat.v2024.jaxb.TipoError::getDescripcion)
				.forEach(messages::add);

				List<TipoRetenidoError2024> retenidos =
				retenedores.stream()
				.map(TipoRetenedorError2024::getRetenido)
				.flatMap(List::stream)
				.collect(Collectors.toList());
				
				// Errores 
				retenidos.stream()
				.map(TipoRetenidoError2024::getError)
				.flatMap(List::stream)
				.map(net.aonsolutions.core.aeat.v2024.jaxb.TipoError::getDescripcion )
				.forEach( messages::add );

				// Descendientes
				retenidos.stream()
				.map(TipoRetenidoError2024::getDescendiente)
				.flatMap(List::stream)
				.map(TipoRetenidoError2024.Descendiente::getError)
				.flatMap(List::stream)
				.map(net.aonsolutions.core.aeat.v2024.jaxb.TipoError::getDescripcion )
				.forEach( messages::add );
				
				// Ascendientes
				retenidos.stream()
				.map(TipoRetenidoError2024::getAscendiente)
				.flatMap(List::stream)
				.map(TipoRetenidoError2024.Ascendiente::getError)
				.flatMap(List::stream)
				.map(net.aonsolutions.core.aeat.v2024.jaxb.TipoError::getDescripcion )
				.forEach( messages::add )
				;

				message = messages.stream().findFirst().orElse("Error al calcular el IRPF");
				

				ExpressionException expressionException = new CheckException(
						message);
				throw new ExpressionExceptionWrapper(expressionException);
			}
		}

		// --------------------------------------------------------------------



		private AEATRetencionesSalida2020 calculate(
				AEATRetencionesEntrada2020 entrada2020)
				throws IrpfCalculateException, JAXBException, IOException {
			try {
				checkNullZeroRetribAnuales(entrada2020);
			} catch (NullPointerException e) {
				return newZeroAEATRetencionesSalida2020(entrada2020);
			} 

			Marshaller marshaller = JAXBContext.newInstance(
					AEATRetencionesEntrada2020.class).createMarshaller();
			File entrada2020File = File.createTempFile(
					AEATRetencionesEntrada2020.class.getSimpleName(), null);
			marshaller.marshal(entrada2020, entrada2020File);

			File salida2020File = File.createTempFile(
					AEATRetencionesSalida2020.class.getSimpleName(), null);
			File error2020File = File.createTempFile(
					AEATRetencionesError2020.class.getSimpleName(), null);

			es.aeat.pret.c200.mc.ModuloCalculo.procesarFicheroXml(entrada2020File.getAbsolutePath(),
					error2020File.getAbsolutePath(), null,
					salida2020File.getAbsolutePath());
			entrada2020File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
					AEATRetencionesError2020.class).createUnmarshaller();
			try {
				AEATRetencionesError2020 error2020 = (AEATRetencionesError2020) unMarshaller
						.unmarshal(error2020File);
				error2020File.delete();
				throw new IrpfCalculateException(error2020);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2020File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2020.class).createUnmarshaller();
			AEATRetencionesSalida2020 salida2020 = (AEATRetencionesSalida2020) unMarshaller
					.unmarshal(salida2020File);
			salida2020File.delete();
			return salida2020;
		}

		private AEATRetencionesSalida2021 calculate(
				AEATRetencionesEntrada2021 entrada2021)
				throws IrpfCalculateException, JAXBException, IOException {
			try {
				checkNullZeroRetribAnuales(entrada2021);
			} catch (NullPointerException e) {
				return newZeroAEATRetencionesSalida2021(entrada2021);
			} 

			Marshaller marshaller = JAXBContext.newInstance(
					AEATRetencionesEntrada2021.class).createMarshaller();
			File entrada2021File = File.createTempFile(
					AEATRetencionesEntrada2021.class.getSimpleName(), null);
			marshaller.marshal(entrada2021, entrada2021File);

			File salida2021File = File.createTempFile(
					AEATRetencionesSalida2021.class.getSimpleName(), null);
			File error2021File = File.createTempFile(
					AEATRetencionesError2021.class.getSimpleName(), null);

			es.aeat.pret.c200.mc.c210.ModuloCalculo.procesarFicheroXml(entrada2021File.getAbsolutePath(),
					error2021File.getAbsolutePath(), null,
					salida2021File.getAbsolutePath());
			entrada2021File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
					AEATRetencionesError2021.class).createUnmarshaller();
			try {
				AEATRetencionesError2021 error2021 = (AEATRetencionesError2021) unMarshaller
						.unmarshal(error2021File);
				error2021File.delete();
				throw new IrpfCalculateException(error2021);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2021File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2021.class).createUnmarshaller();
			AEATRetencionesSalida2021 salida2021 = (AEATRetencionesSalida2021) unMarshaller
					.unmarshal(salida2021File);
			salida2021File.delete();
			return salida2021;
		}

		private AEATRetencionesSalida2022 calculate(
				AEATRetencionesEntrada2022 entrada2022)
				throws IrpfCalculateException, JAXBException, IOException {
			try {
				checkNullZeroRetribAnuales(entrada2022);
			} catch (NullPointerException e) {
				return newZeroAEATRetencionesSalida2022(entrada2022);
			} 

			try {
			    return ServicioCalculo.procesarFicheroXML(entrada2022);
			} catch ( IrpfCalculateException e) {
			    throw e;
			} catch ( Exception e ) {
			    
			} 
			
			Marshaller marshaller = JAXBContext.newInstance(
				AEATRetencionesEntrada2022.class).createMarshaller();
			File entrada2022File = File.createTempFile(
					AEATRetencionesEntrada2022.class.getSimpleName(), null);
			marshaller.marshal(entrada2022, entrada2022File);

			File salida2022File = File.createTempFile(
					AEATRetencionesSalida2022.class.getSimpleName(), null);
			File error2022File = File.createTempFile(
					AEATRetencionesError2022.class.getSimpleName(), null);

			es.aeat.pret.c200.mc.c220.ModuloCalculo.procesarFicheroXml(entrada2022File.getAbsolutePath(),
					error2022File.getAbsolutePath(), null,
					salida2022File.getAbsolutePath());
			entrada2022File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
				AEATRetencionesError2022.class).createUnmarshaller();
			
			try {
				AEATRetencionesError2022 error2022 = (AEATRetencionesError2022) unMarshaller
						.unmarshal(error2022File);
				error2022File.delete();
				throw new IrpfCalculateException(error2022);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2022File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2022.class).createUnmarshaller();
			AEATRetencionesSalida2022 salida2022 = (AEATRetencionesSalida2022) unMarshaller
					.unmarshal(salida2022File);
			salida2022File.delete();
			return salida2022;
		}

		private AEATRetencionesSalida2023 calculate(
			AEATRetencionesEntrada2023 entrada2023, Date fecha)
			throws IrpfCalculateException, JAXBException, IOException {
        		try {
        			checkNullZeroRetribAnuales(entrada2023);
        		} catch (NullPointerException e) {
        			return newZeroAEATRetencionesSalida2023(entrada2023);
        		} 
        
				try {
				    return ServicioCalculo.procesarFicheroXML(entrada2023, fecha);
				} catch ( IrpfCalculateException e) {
				    throw e;
				} catch ( Exception e ) {
				} 
			
        		Marshaller marshaller = JAXBContext.newInstance(
        				AEATRetencionesEntrada2023.class).createMarshaller();
        		File entrada2023File = File.createTempFile(
        				AEATRetencionesEntrada2023.class.getSimpleName(), null);
        		marshaller.marshal(entrada2023, entrada2023File);
        
        		File salida2023File = File.createTempFile(
        				AEATRetencionesSalida2023.class.getSimpleName(), null);
        		File error2023File = File.createTempFile(
        				AEATRetencionesError2023.class.getSimpleName(), null);
        		
        		if ( fecha.before(ServicioCalculo.FEBRUARY_2023) ) {
	        		es.aeat.pret.c200.mc.c230.ModuloCalculo.procesarFicheroXml(entrada2023File.getAbsolutePath(),
	        				error2023File.getAbsolutePath(), null,
	        				salida2023File.getAbsolutePath());
        		} else {
	        		es.aeat.pret.c200.mc.c231.ModuloCalculo.procesarFicheroXml(entrada2023File.getAbsolutePath(),
	        				error2023File.getAbsolutePath(), null,
	        				salida2023File.getAbsolutePath());
        		}
        		

        		entrada2023File.delete();
        		Unmarshaller unMarshaller = JAXBContext.newInstance(
        				AEATRetencionesError2023.class).createUnmarshaller();
        		try {
        			AEATRetencionesError2023 error2023 = (AEATRetencionesError2023) unMarshaller
        					.unmarshal(error2023File);
        			error2023File.delete();
        			throw new IrpfCalculateException(error2023);
        		} catch (JAXBException e) {
        		} catch (IllegalArgumentException e) {
        		}
        
        		error2023File.delete();
        		unMarshaller = JAXBContext.newInstance(
        				AEATRetencionesSalida2023.class).createUnmarshaller();
        		AEATRetencionesSalida2023 salida2023 = (AEATRetencionesSalida2023) unMarshaller
        				.unmarshal(salida2023File);
        		salida2023File.delete();
        		return salida2023;
		}
		
		private AEATRetencionesSalida2024 calculate(
			AEATRetencionesEntrada2024 entrada2024, Date fecha)
			throws IrpfCalculateException, JAXBException, IOException {
			try {
				checkNullZeroRetribAnuales(entrada2024);
			} catch (NullPointerException e) {
				return newZeroAEATRetencionesSalida2024(entrada2024);
			} 

			try {
			    return ServicioCalculo.procesarFicheroXML(entrada2024, fecha);
			} catch ( IrpfCalculateException e) {
			    throw e;
			} catch ( Exception e ) {
			} 
			
			Marshaller marshaller = JAXBContext.newInstance(
					AEATRetencionesEntrada2024.class).createMarshaller();
			File entrada2024File = File.createTempFile(
					AEATRetencionesEntrada2024.class.getSimpleName(), null);
			marshaller.marshal(entrada2024, entrada2024File);

			File salida2024File = File.createTempFile(
					AEATRetencionesSalida2024.class.getSimpleName(), null);
			File error2024File = File.createTempFile(
					AEATRetencionesError2024.class.getSimpleName(), null);

    		if ( fecha.before(ServicioCalculo.FEBRUARY_2023) ) {
				es.aeat.pret.c200.mc.c240.ModuloCalculo.procesarFicheroXml(entrada2024File.getAbsolutePath(),
						error2024File.getAbsolutePath(), null,
						salida2024File.getAbsolutePath());
    		} else {
				es.aeat.pret.c200.mc.c241.ModuloCalculo.procesarFicheroXml(entrada2024File.getAbsolutePath(),
						error2024File.getAbsolutePath(), null,
						salida2024File.getAbsolutePath());
					
			}
			entrada2024File.delete();
			Unmarshaller unMarshaller = JAXBContext.newInstance(
					AEATRetencionesError2024.class).createUnmarshaller();
			try {
				AEATRetencionesError2024 error2024 = (AEATRetencionesError2024) unMarshaller
						.unmarshal(error2024File);
				error2024File.delete();
				throw new IrpfCalculateException(error2024);
			} catch (JAXBException e) {
			} catch (IllegalArgumentException e) {
			}

			error2024File.delete();
			unMarshaller = JAXBContext.newInstance(
					AEATRetencionesSalida2023.class).createUnmarshaller();
			AEATRetencionesSalida2024 salida2024 = (AEATRetencionesSalida2024) unMarshaller
					.unmarshal(salida2024File);
			salida2024File.delete();
			return salida2024;
		}
	}


	private static void checkNullZeroRetribAnuales(AEATRetencionesEntrada2020 entrada2020) {
		
		double retribAnulaes = 0.00;
		for ( TipoRetenedorEntrada2020 retenedor : entrada2020.getRetenedor() )
			for ( TipoRetenidoEntrada2020 retenido: retenedor.getRetenido() )
				if ( retenido.getRetribAnuales() != null )
					retribAnulaes += retenido.getRetribAnuales().doubleValue(); 

		if ( retribAnulaes == 0.00 ) 
			throw new NullPointerException();
		
	}

	private static void checkNullZeroRetribAnuales(AEATRetencionesEntrada2021 entrada2021) {
		
		double retribAnulaes = 0.00;
		for ( TipoRetenedorEntrada2021 retenedor : entrada2021.getRetenedor() )
			for ( TipoRetenidoEntrada2021 retenido: retenedor.getRetenido() )
				if ( retenido.getRetribAnuales() != null )
					retribAnulaes += retenido.getRetribAnuales().doubleValue(); 

		if ( retribAnulaes == 0.00 ) 
			throw new NullPointerException();
		
	}

	private static void checkNullZeroRetribAnuales(AEATRetencionesEntrada2022 entrada2022) {
		
		double retribAnulaes = 0.00;
		for ( TipoRetenedorEntrada2022 retenedor : entrada2022.getRetenedor() )
			for ( TipoRetenidoEntrada2022 retenido: retenedor.getRetenido() )
				if ( retenido.getRetribAnuales() != null )
					retribAnulaes += retenido.getRetribAnuales().doubleValue(); 

		if ( retribAnulaes == 0.00 ) 
			throw new NullPointerException();
		
	}

	private static void checkNullZeroRetribAnuales(AEATRetencionesEntrada2023 entrada2023) {
		
		double retribAnulaes = 0.00;
		for ( TipoRetenedorEntrada2023 retenedor : entrada2023.getRetenedor() )
			for ( TipoRetenidoEntrada2023 retenido: retenedor.getRetenido() )
				if ( retenido.getRetribAnuales() != null )
					retribAnulaes += retenido.getRetribAnuales().doubleValue(); 

		if ( retribAnulaes == 0.00 ) 
			throw new NullPointerException();
		
	}

	private static void checkNullZeroRetribAnuales(AEATRetencionesEntrada2024 entrada2024) {
		
		double retribAnulaes = 0.00;
		for ( TipoRetenedorEntrada2024 retenedor : entrada2024.getRetenedor() )
			for ( TipoRetenidoEntrada2024 retenido: retenedor.getRetenido() )
				if ( retenido.getRetribAnuales() != null )
					retribAnulaes += retenido.getRetribAnuales().doubleValue(); 

		if ( retribAnulaes == 0.00 ) 
			throw new NullPointerException();
		
	}

	private static AEATRetencionesSalida2020 newZeroAEATRetencionesSalida2020(AEATRetencionesEntrada2020 entrada2020) {
		AEATRetencionesSalida2020 aeatRetencionesSalida2020 = new AEATRetencionesSalida2020();
		
		aeatRetencionesSalida2020.setIdDoc(entrada2020.getIdDoc());
		for ( TipoRetenedorEntrada2020 retenedorEntrada : entrada2020.getRetenedor() ) {
			TipoRetenedorSalida2020 retenedorSalida = new TipoRetenedorSalida2020();
			retenedorSalida.setNif(retenedorEntrada.getNif());
			retenedorSalida.setApellidosNombre(retenedorEntrada.getApellidosNombre());
			
			for ( TipoRetenidoEntrada2020 retenidoEntrada: retenedorEntrada.getRetenido() ) {
				TipoRetenidoSalida2020 retenidoSalida = new  TipoRetenidoSalida2020();
				retenidoSalida.setNif(retenidoEntrada.getNif());
				retenidoSalida.setApellidosNombre(retenidoEntrada.getApellidosNombre());
				retenidoSalida.setAñoNacimiento(retenidoEntrada.getAñoNacimiento());
				retenidoSalida.setDiscapacidad(retenidoEntrada.getDiscapacidad());
				retenidoSalida.setComunidadAutonoma(retenidoEntrada.getComunidadAutonoma());
				retenidoSalida.setSituacionLaboral(retenidoEntrada.getSituacionLaboral());
				retenidoSalida.setSituacionFamiliar(retenidoEntrada.getSituacionFamiliar());
				

				retenidoSalida.setReducciones(retenidoEntrada.getReducciones());
				retenidoSalida.setCotizaciones(retenidoEntrada.getCotizaciones());
				retenidoSalida.setRegularizacion(retenidoEntrada.getRegularizacion());
				retenidoSalida.setPensionCompensatoria(retenidoEntrada.getPensionCompensatoria());
				retenidoSalida.setPagoPrestamosVivienda(retenidoEntrada.getPagoPrestamosVivienda());
				retenidoSalida.setRdtosObtenidosCeutaMelilla(retenidoEntrada.getRdtosObtenidosCeutaMelilla());
				retenidoSalida.setAnualidadesHijos(retenidoEntrada.getAnualidadesHijos());

				net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Ascendientes ascendientes = 
						new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Ascendientes();
				retenidoSalida.setAscendientes(ascendientes);
				
				net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes descendientes = 
						new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes();
				retenidoSalida.setDescendientes(descendientes);
				
				net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Gastos gastos = 
				new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Gastos();
				retenidoSalida.setGastos(gastos);

				retenidoSalida.setTipoRetencion(BigDecimal.ZERO);
				
				retenidoSalida.setBaseRetencion(BigDecimal.ZERO);
				retenidoSalida.setRetribAnuales(BigDecimal.ZERO);
				//retenidoSalida.setMinimoPersonalFamiliar(BigDecimal.ZERO);
				retenidoSalida.setMinoracionPrestamo(BigDecimal.ZERO);
				retenidoSalida.setImpAnualRetencionesIngresosCuenta(BigDecimal.ZERO);
				
				retenedorSalida.getRetenido().add(retenidoSalida);
				
			}
			aeatRetencionesSalida2020.getRetenedor().add(retenedorSalida);
		}
		
		
		return aeatRetencionesSalida2020;
		
	}

	private static AEATRetencionesSalida2021 newZeroAEATRetencionesSalida2021(AEATRetencionesEntrada2021 entrada2021) {
		AEATRetencionesSalida2021 aeatRetencionesSalida2021 = new AEATRetencionesSalida2021();
		
		aeatRetencionesSalida2021.setIdDoc(entrada2021.getIdDoc());
		for ( TipoRetenedorEntrada2021 retenedorEntrada : entrada2021.getRetenedor() ) {
			TipoRetenedorSalida2021 retenedorSalida = new TipoRetenedorSalida2021();
			retenedorSalida.setNif(retenedorEntrada.getNif());
			retenedorSalida.setApellidosNombre(retenedorEntrada.getApellidosNombre());
			
			for ( TipoRetenidoEntrada2021 retenidoEntrada: retenedorEntrada.getRetenido() ) {
				TipoRetenidoSalida2021 retenidoSalida = new  TipoRetenidoSalida2021();
				retenidoSalida.setNif(retenidoEntrada.getNif());
				retenidoSalida.setApellidosNombre(retenidoEntrada.getApellidosNombre());
				retenidoSalida.setAñoNacimiento(retenidoEntrada.getAñoNacimiento());
				retenidoSalida.setDiscapacidad(retenidoEntrada.getDiscapacidad());
				retenidoSalida.setComunidadAutonoma(retenidoEntrada.getComunidadAutonoma());
				retenidoSalida.setSituacionLaboral(retenidoEntrada.getSituacionLaboral());
				retenidoSalida.setSituacionFamiliar(retenidoEntrada.getSituacionFamiliar());
				

				retenidoSalida.setReducciones(retenidoEntrada.getReducciones());
				retenidoSalida.setCotizaciones(retenidoEntrada.getCotizaciones());
				retenidoSalida.setRegularizacion(retenidoEntrada.getRegularizacion());
				retenidoSalida.setPensionCompensatoria(retenidoEntrada.getPensionCompensatoria());
				retenidoSalida.setPagoPrestamosVivienda(retenidoEntrada.getPagoPrestamosVivienda());
				retenidoSalida.setRdtosObtenidosCeutaMelilla(retenidoEntrada.getRdtosObtenidosCeutaMelilla());
				retenidoSalida.setAnualidadesHijos(retenidoEntrada.getAnualidadesHijos());

				net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Ascendientes ascendientes = 
						new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Ascendientes();
				retenidoSalida.setAscendientes(ascendientes);
				
				net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes descendientes = 
						new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes();
				retenidoSalida.setDescendientes(descendientes);
				
				net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Gastos gastos = 
				new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Gastos();
				retenidoSalida.setGastos(gastos);

				retenidoSalida.setTipoRetencion(BigDecimal.ZERO);
				
				retenidoSalida.setBaseRetencion(BigDecimal.ZERO);
				retenidoSalida.setRetribAnuales(BigDecimal.ZERO);
				//retenidoSalida.setMinimoPersonalFamiliar(BigDecimal.ZERO);
				retenidoSalida.setMinoracionPrestamo(BigDecimal.ZERO);
				retenidoSalida.setImpAnualRetencionesIngresosCuenta(BigDecimal.ZERO);
				
				retenedorSalida.getRetenido().add(retenidoSalida);
				
			}
			aeatRetencionesSalida2021.getRetenedor().add(retenedorSalida);
		}
		
		
		return aeatRetencionesSalida2021;
		
	}

	private static AEATRetencionesSalida2022 newZeroAEATRetencionesSalida2022(AEATRetencionesEntrada2022 entrada2022) {
		AEATRetencionesSalida2022 aeatRetencionesSalida2022 = new AEATRetencionesSalida2022();
		
		aeatRetencionesSalida2022.setIdDoc(entrada2022.getIdDoc());
		for ( TipoRetenedorEntrada2022 retenedorEntrada : entrada2022.getRetenedor() ) {
			TipoRetenedorSalida2022 retenedorSalida = new TipoRetenedorSalida2022();
			retenedorSalida.setNif(retenedorEntrada.getNif());
			retenedorSalida.setApellidosNombre(retenedorEntrada.getApellidosNombre());
			
			for ( TipoRetenidoEntrada2022 retenidoEntrada: retenedorEntrada.getRetenido() ) {
				TipoRetenidoSalida2022 retenidoSalida = new  TipoRetenidoSalida2022();
				retenidoSalida.setNif(retenidoEntrada.getNif());
				retenidoSalida.setApellidosNombre(retenidoEntrada.getApellidosNombre());
				retenidoSalida.setAñoNacimiento(retenidoEntrada.getAñoNacimiento());
				retenidoSalida.setDiscapacidad(retenidoEntrada.getDiscapacidad());
				retenidoSalida.setComunidadAutonoma(retenidoEntrada.getComunidadAutonoma());
				retenidoSalida.setSituacionLaboral(retenidoEntrada.getSituacionLaboral());
				retenidoSalida.setSituacionFamiliar(retenidoEntrada.getSituacionFamiliar());
				

				retenidoSalida.setReducciones(retenidoEntrada.getReducciones());
				retenidoSalida.setCotizaciones(retenidoEntrada.getCotizaciones());
				retenidoSalida.setRegularizacion(retenidoEntrada.getRegularizacion());
				retenidoSalida.setPensionCompensatoria(retenidoEntrada.getPensionCompensatoria());
				retenidoSalida.setPagoPrestamosVivienda(retenidoEntrada.getPagoPrestamosVivienda());
				retenidoSalida.setRdtosObtenidosCeutaMelilla(retenidoEntrada.getRdtosObtenidosCeutaMelilla());
				retenidoSalida.setAnualidadesHijos(retenidoEntrada.getAnualidadesHijos());

				net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Ascendientes ascendientes = 
						new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Ascendientes();
				retenidoSalida.setAscendientes(ascendientes);
				
				net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes descendientes = 
						new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes();
				retenidoSalida.setDescendientes(descendientes);
				
				net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Gastos gastos = 
				new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Gastos();
				retenidoSalida.setGastos(gastos);

				retenidoSalida.setTipoRetencion(BigDecimal.ZERO);
				
				retenidoSalida.setBaseRetencion(BigDecimal.ZERO);
				retenidoSalida.setRetribAnuales(BigDecimal.ZERO);
				//retenidoSalida.setMinimoPersonalFamiliar(BigDecimal.ZERO);
				retenidoSalida.setMinoracionPrestamo(BigDecimal.ZERO);
				retenidoSalida.setImpAnualRetencionesIngresosCuenta(BigDecimal.ZERO);
				
				retenedorSalida.getRetenido().add(retenidoSalida);
				
			}
			aeatRetencionesSalida2022.getRetenedor().add(retenedorSalida);
		}
		
		
		return aeatRetencionesSalida2022;
		
	}

	private static AEATRetencionesSalida2023 newZeroAEATRetencionesSalida2023(AEATRetencionesEntrada2023 entrada2023) {
		AEATRetencionesSalida2023 aeatRetencionesSalida2023 = new AEATRetencionesSalida2023();
		
		aeatRetencionesSalida2023.setIdDoc(entrada2023.getIdDoc());
		for ( TipoRetenedorEntrada2023 retenedorEntrada : entrada2023.getRetenedor() ) {
			TipoRetenedorSalida2023 retenedorSalida = new TipoRetenedorSalida2023();
			retenedorSalida.setNif(retenedorEntrada.getNif());
			retenedorSalida.setApellidosNombre(retenedorEntrada.getApellidosNombre());
			
			for ( TipoRetenidoEntrada2023 retenidoEntrada: retenedorEntrada.getRetenido() ) {
				TipoRetenidoSalida2023 retenidoSalida = new  TipoRetenidoSalida2023();
				retenidoSalida.setNif(retenidoEntrada.getNif());
				retenidoSalida.setApellidosNombre(retenidoEntrada.getApellidosNombre());
				retenidoSalida.setAñoNacimiento(retenidoEntrada.getAñoNacimiento());
				retenidoSalida.setDiscapacidad(retenidoEntrada.getDiscapacidad());
				retenidoSalida.setComunidadAutonoma(retenidoEntrada.getComunidadAutonoma());
				retenidoSalida.setSituacionLaboral(retenidoEntrada.getSituacionLaboral());
				retenidoSalida.setSituacionFamiliar(retenidoEntrada.getSituacionFamiliar());
				

				retenidoSalida.setReducciones(retenidoEntrada.getReducciones());
				retenidoSalida.setCotizaciones(retenidoEntrada.getCotizaciones());
				retenidoSalida.setRegularizacion(retenidoEntrada.getRegularizacion());
				retenidoSalida.setPensionCompensatoria(retenidoEntrada.getPensionCompensatoria());
				retenidoSalida.setPagoPrestamosVivienda(retenidoEntrada.getPagoPrestamosVivienda());
				retenidoSalida.setRdtosObtenidosCeutaMelilla(retenidoEntrada.getRdtosObtenidosCeutaMelilla());
				retenidoSalida.setAnualidadesHijos(retenidoEntrada.getAnualidadesHijos());

				net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Ascendientes ascendientes = 
						new net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Ascendientes();
				retenidoSalida.setAscendientes(ascendientes);
				
				net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes descendientes = 
						new net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes();
				retenidoSalida.setDescendientes(descendientes);
				
				net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Gastos gastos = 
				new net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Gastos();
				retenidoSalida.setGastos(gastos);

				retenidoSalida.setTipoRetencion(BigDecimal.ZERO);
				
				retenidoSalida.setBaseRetencion(BigDecimal.ZERO);
				retenidoSalida.setRetribAnuales(BigDecimal.ZERO);
				//retenidoSalida.setMinimoPersonalFamiliar(BigDecimal.ZERO);
				retenidoSalida.setMinoracionPrestamo(BigDecimal.ZERO);
				retenidoSalida.setImpAnualRetencionesIngresosCuenta(BigDecimal.ZERO);
				
				retenedorSalida.getRetenido().add(retenidoSalida);
				
			}
			aeatRetencionesSalida2023.getRetenedor().add(retenedorSalida);
		}
		
		
		return aeatRetencionesSalida2023;
		
	}

	private static AEATRetencionesSalida2024 newZeroAEATRetencionesSalida2024(AEATRetencionesEntrada2024 entrada2024) {
		AEATRetencionesSalida2024 aeatRetencionesSalida2024 = new AEATRetencionesSalida2024();
		
		aeatRetencionesSalida2024.setIdDoc(entrada2024.getIdDoc());
		for ( TipoRetenedorEntrada2024 retenedorEntrada : entrada2024.getRetenedor() ) {
			TipoRetenedorSalida2024 retenedorSalida = new TipoRetenedorSalida2024();
			retenedorSalida.setNif(retenedorEntrada.getNif());
			retenedorSalida.setApellidosNombre(retenedorEntrada.getApellidosNombre());
			
			for ( TipoRetenidoEntrada2024 retenidoEntrada: retenedorEntrada.getRetenido() ) {
				TipoRetenidoSalida2024 retenidoSalida = new  TipoRetenidoSalida2024();
				retenidoSalida.setNif(retenidoEntrada.getNif());
				retenidoSalida.setApellidosNombre(retenidoEntrada.getApellidosNombre());
				retenidoSalida.setNacimiento(retenidoEntrada.getNacimiento());
				retenidoSalida.setDiscapacidad(retenidoEntrada.getDiscapacidad());
				retenidoSalida.setResidenciaCeutaMelilla(retenidoEntrada.getResidenciaCeutaMelilla());
				retenidoSalida.setSituacionLaboral(retenidoEntrada.getSituacionLaboral());
				retenidoSalida.setSituacionFamiliar(retenidoEntrada.getSituacionFamiliar());
				

				retenidoSalida.setReducciones(retenidoEntrada.getReducciones());
				retenidoSalida.setCotizaciones(retenidoEntrada.getCotizaciones());
				retenidoSalida.setRegularizacion(retenidoEntrada.getRegularizacion());
				retenidoSalida.setPensionCompensatoria(retenidoEntrada.getPensionCompensatoria());
				retenidoSalida.setPagoPrestamosVivienda(retenidoEntrada.getPagoPrestamosVivienda());
				retenidoSalida.setRdtosObtenidosCeutaMelilla(retenidoEntrada.getRdtosObtenidosCeutaMelilla());
				retenidoSalida.setAnualidadesHijos(retenidoEntrada.getAnualidadesHijos());

				net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Ascendientes ascendientes = 
						new net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Ascendientes();
				retenidoSalida.setAscendientes(ascendientes);
				
				net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes descendientes = 
						new net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes();
				retenidoSalida.setDescendientes(descendientes);
				
				net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Gastos gastos = 
				new net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Gastos();
				retenidoSalida.setGastos(gastos);

				retenidoSalida.setTipoRetencion(BigDecimal.ZERO);
				
				retenidoSalida.setBaseRetencion(BigDecimal.ZERO);
				retenidoSalida.setRetribAnuales(BigDecimal.ZERO);
				//retenidoSalida.setMinimoPersonalFamiliar(BigDecimal.ZERO);
				retenidoSalida.setMinoracionPrestamo(BigDecimal.ZERO);
				retenidoSalida.setImpAnualRetencionesIngresosCuenta(BigDecimal.ZERO);
				
				retenedorSalida.getRetenido().add(retenidoSalida);
				
			}
			aeatRetencionesSalida2024.getRetenedor().add(retenedorSalida);
		}
		
		
		return aeatRetencionesSalida2024;
		
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
		public AEATRetencionesSalida2020 calculate2020(
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
			
			Date chargeDate = sqlCtx.getChargeDate();
//			chargeDate = AonDateUtils.add(chargeDate, Calendar.YEAR, -1) ;
			
			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							chargeDate.getTime()));
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

			TipoRetenidoSalida2020 retenidoSalida2020 = new TipoRetenidoSalida2020();
			retenidoSalida2020.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida2020.setComunidadAutonoma(geozone);

			// Mainly DEBUG INFO
			retenidoSalida2020.setRetribAnuales(retribAnuales);
			retenidoSalida2020.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida2020.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ComputoDescendientes computoDescendientes2020 = 
			new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes2020
						.setHijo1(net.aonsolutions.core.aeat.v2020.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes2020
						.setHijo2(net.aonsolutions.core.aeat.v2020.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes2020
						.setHijo3(net.aonsolutions.core.aeat.v2020.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2020 = 
				new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos2020.setTotal((byte) (descendants - 3));
				cuartoySucesivos2020.setPorEntero((byte) (descendants - 3));
				computoDescendientes2020.setCuartoySucesivos(cuartoySucesivos2020);
			}
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes descendientes2020 = 
			new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoSalida2020.Descendientes();
			descendientes2020.setComputoDescendientes(computoDescendientes2020);
			retenidoSalida2020.setDescendientes(descendientes2020);

			TipoRetenedorSalida2020 retenedorSalida2020 = new TipoRetenedorSalida2020();
			List<TipoRetenidoSalida2020> retenidosSalida = retenedorSalida2020
					.getRetenido();
			retenidosSalida.add(retenidoSalida2020);

			AEATRetencionesSalida2020 aeatRetencionesSalida = new AEATRetencionesSalida2020();
			List<TipoRetenedorSalida2020> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2020);

			return aeatRetencionesSalida;
		}

		@Override
		public AEATRetencionesSalida2021 calculate2021(
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
			
			Date chargeDate = sqlCtx.getChargeDate();
//			chargeDate = AonDateUtils.add(chargeDate, Calendar.YEAR, -1) ;
			
			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							chargeDate.getTime()));
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

			TipoRetenidoSalida2021 retenidoSalida2021 = new TipoRetenidoSalida2021();
			retenidoSalida2021.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida2021.setComunidadAutonoma(geozone);

			// Mainly DEBUG INFO
			retenidoSalida2021.setRetribAnuales(retribAnuales);
			retenidoSalida2021.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida2021.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ComputoDescendientes computoDescendientes2021 = 
			new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes2021
						.setHijo1(net.aonsolutions.core.aeat.v2021.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes2021
						.setHijo2(net.aonsolutions.core.aeat.v2021.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes2021
						.setHijo3(net.aonsolutions.core.aeat.v2021.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2021 = 
				new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos2021.setTotal((byte) (descendants - 3));
				cuartoySucesivos2021.setPorEntero((byte) (descendants - 3));
				computoDescendientes2021.setCuartoySucesivos(cuartoySucesivos2021);
			}
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes descendientes2021 = 
			new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoSalida2021.Descendientes();
			descendientes2021.setComputoDescendientes(computoDescendientes2021);
			retenidoSalida2021.setDescendientes(descendientes2021);

			TipoRetenedorSalida2021 retenedorSalida2021 = new TipoRetenedorSalida2021();
			List<TipoRetenidoSalida2021> retenidosSalida = retenedorSalida2021
					.getRetenido();
			retenidosSalida.add(retenidoSalida2021);

			AEATRetencionesSalida2021 aeatRetencionesSalida = new AEATRetencionesSalida2021();
			List<TipoRetenedorSalida2021> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2021);

			return aeatRetencionesSalida;
		}

		@Override
		public AEATRetencionesSalida2022 calculate2022(
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
			
			Date chargeDate = sqlCtx.getChargeDate();
//			chargeDate = AonDateUtils.add(chargeDate, Calendar.YEAR, -1) ;
			
			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							chargeDate.getTime()));
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

			TipoRetenidoSalida2022 retenidoSalida2022 = new TipoRetenidoSalida2022();
			retenidoSalida2022.setTipoRetencion(BigDecimal.valueOf(percent));
			retenidoSalida2022.setComunidadAutonoma(geozone);

			// Mainly DEBUG INFO
			retenidoSalida2022.setRetribAnuales(retribAnuales);
			retenidoSalida2022.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida2022.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ComputoDescendientes computoDescendientes2022 = 
			new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes2022
						.setHijo1(net.aonsolutions.core.aeat.v2022.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes2022
						.setHijo2(net.aonsolutions.core.aeat.v2022.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes2022
						.setHijo3(net.aonsolutions.core.aeat.v2022.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2022 = 
				new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos2022.setTotal((byte) (descendants - 3));
				cuartoySucesivos2022.setPorEntero((byte) (descendants - 3));
				computoDescendientes2022.setCuartoySucesivos(cuartoySucesivos2022);
			}
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes descendientes2022 = 
			new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoSalida2022.Descendientes();
			descendientes2022.setComputoDescendientes(computoDescendientes2022);
			retenidoSalida2022.setDescendientes(descendientes2022);

			TipoRetenedorSalida2022 retenedorSalida2022 = new TipoRetenedorSalida2022();
			List<TipoRetenidoSalida2022> retenidosSalida = retenedorSalida2022
					.getRetenido();
			retenidosSalida.add(retenidoSalida2022);

			AEATRetencionesSalida2022 aeatRetencionesSalida = new AEATRetencionesSalida2022();
			List<TipoRetenedorSalida2022> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2022);

			return aeatRetencionesSalida;
		}

		@Override
		public AEATRetencionesSalida2023 calculate2023(
				IIrpfCalculatorContext ctx, Date date) {
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
			
			Date chargeDate = sqlCtx.getChargeDate();
//			chargeDate = AonDateUtils.add(chargeDate, Calendar.YEAR, -1) ;
			
			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							chargeDate.getTime()));
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

			TipoRetenidoSalida2023 retenidoSalida2023 = new TipoRetenidoSalida2023();
			retenidoSalida2023.setTipoRetencion(BigDecimal.valueOf(percent));

			// Mainly DEBUG INFO
			retenidoSalida2023.setRetribAnuales(retribAnuales);
			retenidoSalida2023.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida2023.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ComputoDescendientes computoDescendientes2023 = 
			new net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes2023
						.setHijo1(net.aonsolutions.core.aeat.v2023.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes2023
						.setHijo2(net.aonsolutions.core.aeat.v2023.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes2023
						.setHijo3(net.aonsolutions.core.aeat.v2023.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2023 = 
				new net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos2023.setTotal((byte) (descendants - 3));
				cuartoySucesivos2023.setPorEntero((byte) (descendants - 3));
				computoDescendientes2023.setCuartoySucesivos(cuartoySucesivos2023);
			}
			net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes descendientes2023 = 
			new net.aonsolutions.core.aeat.v2023.jaxb.TipoRetenidoSalida2023.Descendientes();
			descendientes2023.setComputoDescendientes(computoDescendientes2023);
			retenidoSalida2023.setDescendientes(descendientes2023);

			TipoRetenedorSalida2023 retenedorSalida2023 = new TipoRetenedorSalida2023();
			List<TipoRetenidoSalida2023> retenidosSalida = retenedorSalida2023
					.getRetenido();
			retenidosSalida.add(retenidoSalida2023);

			AEATRetencionesSalida2023 aeatRetencionesSalida = new AEATRetencionesSalida2023();
			List<TipoRetenedorSalida2023> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2023);

			return aeatRetencionesSalida;
		}
		
		@Override
		public AEATRetencionesSalida2024 calculate2024(IIrpfCalculatorContext ctx, Date date) {
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
			
			Date chargeDate = sqlCtx.getChargeDate();
//			chargeDate = AonDateUtils.add(chargeDate, Calendar.YEAR, -1) ;
			
			Double percent = JooqGeozoneIrpf.getPercent(sqlCtx.getConnection(),
					geozone, amount, descendants, handicap, new java.sql.Date(
							chargeDate.getTime()));
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

			if ( ctx.getContrato() == Contrato.DOS ) {
				percent = Math.max(2.00, percent);
			}
			
			TipoRetenidoSalida2024 retenidoSalida2024 = new TipoRetenidoSalida2024();
			retenidoSalida2024.setTipoRetencion(BigDecimal.valueOf(percent));
			//retenidoSalida2024.setResidenciaCeutaMelilla(new ResidenciaCeutaMelilla());

			// Mainly DEBUG INFO
			retenidoSalida2024.setRetribAnuales(retribAnuales);
			retenidoSalida2024.setCotizaciones(ctx.getGastosAnuales());
			retenidoSalida2024.setBaseRetencion(retribAnuales);
			net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ComputoDescendientes computoDescendientes2024 = 
			new net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ComputoDescendientes();
			if (descendants >= 1)
				computoDescendientes2024
						.setHijo1(net.aonsolutions.core.aeat.v2024.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 2)
				computoDescendientes2024
						.setHijo2(net.aonsolutions.core.aeat.v2024.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 3)
				computoDescendientes2024
						.setHijo3(net.aonsolutions.core.aeat.v2024.jaxb.TipoComputo.POR_ENTERO);
			if (descendants >= 4) {
				net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ComputoDescendientes.CuartoySucesivos cuartoySucesivos2024 = 
				new net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes.ComputoDescendientes.CuartoySucesivos();
				cuartoySucesivos2024.setTotal((byte) (descendants - 3));
				cuartoySucesivos2024.setPorEntero((byte) (descendants - 3));
				computoDescendientes2024.setCuartoySucesivos(cuartoySucesivos2024);
			}
			net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes descendientes2024 = 
			new net.aonsolutions.core.aeat.v2024.jaxb.TipoRetenidoSalida2024.Descendientes();
			descendientes2024.setComputoDescendientes(computoDescendientes2024);
			retenidoSalida2024.setDescendientes(descendientes2024);

			TipoRetenedorSalida2024 retenedorSalida2024 = new TipoRetenedorSalida2024();
			List<TipoRetenidoSalida2024> retenidosSalida = retenedorSalida2024
					.getRetenido();
			retenidosSalida.add(retenidoSalida2024);

			AEATRetencionesSalida2024 aeatRetencionesSalida = new AEATRetencionesSalida2024();
			List<TipoRetenedorSalida2024> retenedoresSalida = aeatRetencionesSalida
					.getRetenedor();
			retenedoresSalida.add(retenedorSalida2024);

			return aeatRetencionesSalida;
		}
		
	}

}
