package com.esferalia.aon.payroll.irpf;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.List;

import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente.Convivencia;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.CausaRegularizacion;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Contrato;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Discapacidad;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.SituacionFamiliar;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.SituacionLaboral;
import com.esferalia.aon.salary.expression.ExpressionException;

import es.aeat.pret.rw13.jaxb.AEATRetencionesEntrada2013;
import es.aeat.pret.rw13.jaxb.TipoDiscapacidad;
import es.aeat.pret.rw13.jaxb.TipoDiscapacidad.Grado1;
import es.aeat.pret.rw13.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida;
import es.aeat.pret.rw13.jaxb.TipoDiscapacidad.Grado2;
import es.aeat.pret.rw13.jaxb.TipoRetenciones;
import es.aeat.pret.rw13.jaxb.TipoRetenedorEntrada2013;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Ascendiente;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Descendiente;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Descendiente.ComputadoEntero;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.PagoPrestamosVivienda;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.RdtosObtenidosCeutaMelilla;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Reducciones;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Regularizacion;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.Regularizacion.ResidenciaInicialCeutaMelilla;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.ResidenciaCeutaMelilla;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionFamiliar.Situacion1;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionFamiliar.Situacion2;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionFamiliar.Situacion3;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral.Desempleado;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral.OtraSituacion;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral.Pensionista;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral.TrabajadorActivo;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral.TrabajadorActivo.MovilidadGeografica;
import es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral.TrabajadorActivo.ProlongacionLaboral;
import net.aonsolutions.core.aeat.v2020.jaxb.AEATRetencionesEntrada2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenedorEntrada2020;
import net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020;
import net.aonsolutions.core.aeat.v2021.jaxb.AEATRetencionesEntrada2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenedorEntrada2021;
import net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021;
import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesEntrada2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenedorEntrada2022;
import net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022;

public class AEATRetencionesEntradaFactory {

	private static final MathContext MATH_CONTEXT = new MathContext(3);

	public static AEATRetencionesEntrada2013 create2013(
			IIrpfCalculatorContext ctx) throws SQLException,
			ExpressionException {

		AEATRetencionesEntrada2013 entrada2013 = new AEATRetencionesEntrada2013();

		TipoRetenciones tipoRetenciones = new TipoRetenciones();
		tipoRetenciones.setEjercicio(2013);
		tipoRetenciones.setCodModelo("RET");
		entrada2013.setIdDoc(tipoRetenciones);

		List<TipoRetenedorEntrada2013> retenedores = entrada2013.getRetenedor();
		TipoRetenedorEntrada2013 retenedor = new TipoRetenedorEntrada2013();

		retenedor.setNif(ctx.getRetenedorNif());
		retenedor.setApellidosNombre(ctx.getRetenedorApellidosNombre());
		retenedores.add(retenedor);

		List<TipoRetenidoEntrada2013> retenidos = retenedor.getRetenido();
		TipoRetenidoEntrada2013 retenido = newTipoRetenidoEntrada2013(ctx);
		retenidos.add(retenido);

		return entrada2013;
	}

	public static AEATRetencionesEntrada2020 create2020(
			IIrpfCalculatorContext ctx) throws SQLException,
			ExpressionException {

		AEATRetencionesEntrada2020 entrada2020 = new AEATRetencionesEntrada2020();

		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenciones tipoRetenciones = new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenciones();
		tipoRetenciones.setEjercicio(2020);
		tipoRetenciones.setCodModelo("RET");
		entrada2020.setIdDoc(tipoRetenciones);

		List<TipoRetenedorEntrada2020> retenedores = entrada2020.getRetenedor();
		TipoRetenedorEntrada2020 retenedor = new TipoRetenedorEntrada2020();

		retenedor.setNif(ctx.getRetenedorNif());
		retenedor.setApellidosNombre(ctx.getRetenedorApellidosNombre());
		retenedores.add(retenedor);

		List<TipoRetenidoEntrada2020> retenidos = retenedor.getRetenido();
		TipoRetenidoEntrada2020 retenido = newTipoRetenidoEntrada2020(ctx);
		retenidos.add(retenido);

		return entrada2020;
	}

	public static AEATRetencionesEntrada2021 create2021(
			IIrpfCalculatorContext ctx) throws SQLException,
			ExpressionException {

		AEATRetencionesEntrada2021 entrada2021 = new AEATRetencionesEntrada2021();

		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenciones tipoRetenciones = new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenciones();
		tipoRetenciones.setEjercicio(2021);
		tipoRetenciones.setCodModelo("RET");
		entrada2021.setIdDoc(tipoRetenciones);

		List<TipoRetenedorEntrada2021> retenedores = entrada2021.getRetenedor();
		TipoRetenedorEntrada2021 retenedor = new TipoRetenedorEntrada2021();

		retenedor.setNif(ctx.getRetenedorNif());
		retenedor.setApellidosNombre(ctx.getRetenedorApellidosNombre());
		retenedores.add(retenedor);

		List<TipoRetenidoEntrada2021> retenidos = retenedor.getRetenido();
		TipoRetenidoEntrada2021 retenido = newTipoRetenidoEntrada2021(ctx);
		retenidos.add(retenido);

		return entrada2021;
	}

	public static AEATRetencionesEntrada2022 create2022(
			IIrpfCalculatorContext ctx) throws SQLException,
			ExpressionException {

		AEATRetencionesEntrada2022 entrada2022 = new AEATRetencionesEntrada2022();

		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenciones tipoRetenciones = new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenciones();
		tipoRetenciones.setEjercicio(2022);
		tipoRetenciones.setCodModelo("RET");
		entrada2022.setIdDoc(tipoRetenciones);

		List<TipoRetenedorEntrada2022> retenedores = entrada2022.getRetenedor();
		TipoRetenedorEntrada2022 retenedor = new TipoRetenedorEntrada2022();

		retenedor.setNif(ctx.getRetenedorNif());
		retenedor.setApellidosNombre(ctx.getRetenedorApellidosNombre());
		retenedores.add(retenedor);

		List<TipoRetenidoEntrada2022> retenidos = retenedor.getRetenido();
		TipoRetenidoEntrada2022 retenido = newTipoRetenidoEntrada2022(ctx);
		retenidos.add(retenido);

		return entrada2022;
	}
	// --------------------------------------------------------- Private methods

	private static TipoRetenidoEntrada2013 newTipoRetenidoEntrada2013(
			IIrpfCalculatorContext ctx) {

		TipoRetenidoEntrada2013 retenido = new TipoRetenidoEntrada2013();
		// ------------------------------------ Datos personales

		retenido.setNif(ctx.getNif());
		retenido.setApellidosNombre(ctx.getApellidosNombre());
		retenido.setAñoNacimiento(ctx.getAñoNacimiento());

		if (ctx.getResidenciaCeutaMelilla()) {
			retenido.setResidenciaCeutaMelilla(new ResidenciaCeutaMelilla());
		}
		retenido.setDiscapacidad(getAeat13Discapacidad(ctx.getDiscapacidad(),
				ctx.getMovilidadReducida()));
		retenido.setSituacionFamiliar(getAeat13Situacionfamiliar(
				ctx.getSituacionFamiliar(), ctx.getNifConyuge()));
		retenido.setSituacionLaboral(getAeat13SituacionLaboral(
				ctx.getSituacionLaboral(), ctx.getContrato(),
				ctx.getMovilidadGeografica(), ctx.getProlongacionLaboral()));
		// --------------------------------------- Descendientes
		List<Descendiente> descendientes = retenido.getDescendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente descendiente : ctx
				.getDescendientes()) {
			descendientes.add(getAeat13Descendiente(descendiente));
		}
		// ---------------------------------------- Ascendientes
		List<Ascendiente> ascendientes = retenido.getAscendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente ascendiente : ctx
				.getAscendientes()) {
			ascendientes.add(getAeat13Ascendiente(ascendiente));
		}

		// ------------------------------------ Datos económicos
		retenido.setRetribAnuales(ctx.getRetribAnuales());

		BigDecimal irregularidad1 = ctx.getIrregularidad1();
		BigDecimal irregularidad2 = ctx.getIrregularidad2();
		if (irregularidad1 != null || irregularidad2 != null) {
			Reducciones reducciones = new Reducciones();
			reducciones.setIrregularidad1(irregularidad1);
			reducciones.setIrregularidad2(irregularidad2);
			retenido.setReducciones(reducciones);
		}

		retenido.setGastosAnuales(ctx.getGastosAnuales());
		if (ctx.getRdtosObtenidosCeutaMelilla())
			retenido.setRdtosObtenidosCeutaMelilla(new RdtosObtenidosCeutaMelilla());
		retenido.setPensionCompensatoria(ctx.getPensionCompensatoria());
		retenido.setAnualidadesHijos(ctx.getAnualidadesHijos());
		if (ctx.getPagoPrestamosVivienda())
			retenido.setPagoPrestamosVivienda(new PagoPrestamosVivienda());

		// -------------------------------------- Regularización
		CausaRegularizacion causaRegularizacion = ctx.getCausaRegularizacion();
		if (causaRegularizacion != null) {
			Regularizacion regularizacion = new Regularizacion();
			List<Integer> causas = regularizacion.getCausa();
			causas.add(causaRegularizacion.getValue());

			regularizacion.setRetribSatisfechas(ctx.getRetribSatisfechas());
			regularizacion.setRetencionPracticada(ctx.getRetencionPracticada());
			regularizacion.setRetribAnualesIniciales(ctx
					.getRetribAnualesIniciales());
			regularizacion.setRetencionAnualInicial(ctx
					.getRetencionAnualInicial());
			if (ctx.getResidenciaInicialCeutaMelilla())
				regularizacion
						.setResidenciaInicialCeutaMelilla(new ResidenciaInicialCeutaMelilla());
			regularizacion.setBaseRetencion(ctx.getBaseRetencion());
			regularizacion.setMinimoPersonalFamiliarInicial(ctx
					.getMinimoPersonalFamiliarInicial());
			regularizacion.setTipoRetencion(ctx.getTipoRetencion());
			regularizacion.setMinoracionPrestamosVivienda(ctx
					.getMinoracionPrestamosVivienda());

			retenido.setRegularizacion(regularizacion);
		}

		return retenido;

	}

	private static TipoDiscapacidad getAeat13Discapacidad(
			Discapacidad discapacidad, boolean movilidadReducida) {
		if (discapacidad == Discapacidad.GRADO0)
			return null;
		TipoDiscapacidad tipoDiscapacidad = new TipoDiscapacidad();
		if (discapacidad == Discapacidad.GRADO1) {
			Grado1 grado1 = new Grado1();
			if (movilidadReducida) {
				grado1.setMovilidadReducida(new MovilidadReducida());
			}
			tipoDiscapacidad.setGrado1(grado1);
		} else if (discapacidad == Discapacidad.GRADO2) {
			tipoDiscapacidad.setGrado2(new Grado2());
		}
		return tipoDiscapacidad;
	}

	private static es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral getAeat13SituacionLaboral(
			SituacionLaboral _situacionLaboral, Contrato contrato,
			boolean movilidadGeografica, boolean prolongacionLaboral) {
		es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral situacionLaboral = new es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionLaboral();
		if (SituacionLaboral.DESEMPLEADO == _situacionLaboral) {
			situacionLaboral.setDesempleado(new Desempleado());
		} else if (SituacionLaboral.PENSIONISTA == _situacionLaboral) {
			situacionLaboral.setPensionista(new Pensionista());
		} else if (SituacionLaboral.TRABAJADOR_ACTIVO == _situacionLaboral) {
			TrabajadorActivo trabajadorActivo = new TrabajadorActivo();
			if (contrato == null)
				contrato = Contrato.UNO;
			trabajadorActivo.setContrato(contrato.getValue());
			if (movilidadGeografica)
				trabajadorActivo
						.setMovilidadGeografica(new MovilidadGeografica());
			if (prolongacionLaboral)
				trabajadorActivo
						.setProlongacionLaboral(new ProlongacionLaboral());
			situacionLaboral.setTrabajadorActivo(trabajadorActivo);
		} else {
			situacionLaboral.setOtraSituacion(new OtraSituacion());
		}
		return situacionLaboral;
	}

	private static es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionFamiliar getAeat13Situacionfamiliar(
			SituacionFamiliar _situacionFamiliar, String nifConyuge) {
		es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionFamiliar situacionFamiliar = new es.aeat.pret.rw13.jaxb.TipoRetenidoEntrada2013.SituacionFamiliar();
		if (SituacionFamiliar.UNO == _situacionFamiliar) {
			situacionFamiliar.setSituacion1(new Situacion1());
		} else if (SituacionFamiliar.DOS == _situacionFamiliar) {
			Situacion2 situacion2 = new Situacion2();
			situacion2.setNifConyuge(nifConyuge);
			situacionFamiliar.setSituacion2(situacion2);
		} else {
			situacionFamiliar.setSituacion3(new Situacion3());
		}
		return situacionFamiliar;
	}

	private static Descendiente getAeat13Descendiente(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente _descendiente) {
		Descendiente descendiente = new Descendiente();
		descendiente.setAñoNacimiento(_descendiente.getAñoNacimiento());
		descendiente.setAñoAdopcion(_descendiente.getAñoAdopcion());
		descendiente.setDiscapacidad(getAeat13Discapacidad(
				_descendiente.getDiscapacidad(),
				_descendiente.getMovilidadReducida()));
		if (_descendiente.getComputadoEntero())
			descendiente.setComputadoEntero(new ComputadoEntero());
		return descendiente;
	}

	private static Ascendiente getAeat13Ascendiente(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente _ascendiente) {
		Ascendiente ascendiente = new Ascendiente();
		ascendiente.setAñoNacimiento(_ascendiente.getAñoNacimiento());
		ascendiente.setDiscapacidad(getAeat13Discapacidad(
				_ascendiente.getDiscapacidad(),
				_ascendiente.getMovilidadReducida()));
		Convivencia convivencia = _ascendiente.getConvivecia();
		if (convivencia == null) {
			convivencia = Convivencia.UNO;
		}
		ascendiente.setConvivencia(convivencia.getValue());
		return ascendiente;
	}

	// ------------------------------------------------------------------- 2016

	private static TipoRetenidoEntrada2020 newTipoRetenidoEntrada2020(
			IIrpfCalculatorContext ctx) {

		TipoRetenidoEntrada2020 retenido = new TipoRetenidoEntrada2020();
		// ------------------------------------ Datos personales

		retenido.setNif(ctx.getNif());
		retenido.setApellidosNombre(ctx.getApellidosNombre());
		retenido.setAñoNacimiento(ctx.getAñoNacimiento());

		if (ctx.getResidenciaCeutaMelilla()) {

			retenido.setResidenciaCeutaMelilla(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.ResidenciaCeutaMelilla());
		}
		retenido.setDiscapacidad(getAeat15Discapacidad2020(ctx.getDiscapacidad(),
				ctx.getMovilidadReducida()));
		retenido.setSituacionFamiliar(getAeat15Situacionfamiliar2020(
				ctx.getSituacionFamiliar(), ctx.getNifConyuge()));
		retenido.setSituacionLaboral(getAeat15SituacionLaboral2020(
				ctx.getSituacionLaboral(), ctx.getContrato(),
				ctx.getMovilidadGeografica(), ctx.getProlongacionLaboral()));
		// --------------------------------------- Descendientes
		List<net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Descendiente> descendientes = retenido.getDescendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente descendiente : ctx
				.getDescendientes()) {
			descendientes.add(getAeat15Descendiente2020(descendiente));
		}
		// ---------------------------------------- Ascendientes
		List<net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Ascendiente> ascendientes = retenido.getAscendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente ascendiente : ctx
				.getAscendientes()) {
			ascendientes.add(getAeat15Ascendiente2020(ascendiente));
		}

		// ------------------------------------ Datos económicos
		retenido.setRetribAnuales(ctx.getRetribAnuales());

		BigDecimal irregularidad1 = ctx.getIrregularidad1();
		BigDecimal irregularidad2 = ctx.getIrregularidad2();
		if (irregularidad1 != null || irregularidad2 != null) {
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Reducciones reducciones = new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Reducciones();
			reducciones.setIrregularidad1(irregularidad1);
			reducciones.setIrregularidad2(irregularidad2);
			retenido.setReducciones(reducciones);
		}

		//retenido.setGastosAnuales(ctx.getGastosAnuales());
		retenido.setCotizaciones(ctx.getGastosAnuales());
		
		if (ctx.getRdtosObtenidosCeutaMelilla())
			retenido.setRdtosObtenidosCeutaMelilla(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.RdtosObtenidosCeutaMelilla());
		retenido.setPensionCompensatoria(ctx.getPensionCompensatoria());
		retenido.setAnualidadesHijos(ctx.getAnualidadesHijos());
		if (ctx.getPagoPrestamosVivienda())
			retenido.setPagoPrestamosVivienda(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.PagoPrestamosVivienda());

		// -------------------------------------- Regularización
		CausaRegularizacion causaRegularizacion = ctx.getCausaRegularizacion();
		if (causaRegularizacion != null) {
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Regularizacion regularizacion = 
					new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Regularizacion();
			List<Integer> causas = regularizacion.getCausa();
			causas.add(causaRegularizacion.getValue());

			regularizacion.setRetribSatisfechas(ctx.getRetribSatisfechas());
			regularizacion.setRetencionPracticada(ctx.getRetencionPracticada());
			regularizacion.setRetribAnualesIniciales(ctx
					.getRetribAnualesIniciales());
			regularizacion.setRetencionAnualInicial(ctx
					.getRetencionAnualInicial());
			if (ctx.getResidenciaInicialCeutaMelilla())
				regularizacion
						.setResidenciaInicialCeutaMelilla(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Regularizacion.ResidenciaInicialCeutaMelilla());
			regularizacion.setBaseRetencion(ctx.getBaseRetencion());
			regularizacion.setMinimoPersonalFamiliarInicial(ctx
					.getMinimoPersonalFamiliarInicial());
			regularizacion.setTipoRetencion(ctx.getTipoRetencion());
			regularizacion.setMinoracionPrestamosVivienda(ctx
					.getMinoracionPrestamosVivienda());

			retenido.setRegularizacion(regularizacion);
		}

		return retenido;

	}

	private static TipoRetenidoEntrada2021 newTipoRetenidoEntrada2021(
			IIrpfCalculatorContext ctx) {

		TipoRetenidoEntrada2021 retenido = new TipoRetenidoEntrada2021();
		// ------------------------------------ Datos personales

		retenido.setNif(ctx.getNif());
		retenido.setApellidosNombre(ctx.getApellidosNombre());
		retenido.setAñoNacimiento(ctx.getAñoNacimiento());

		if (ctx.getResidenciaCeutaMelilla()) {

			retenido.setResidenciaCeutaMelilla(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.ResidenciaCeutaMelilla());
		}
		retenido.setDiscapacidad(getAeat15Discapacidad2021(ctx.getDiscapacidad(),
				ctx.getMovilidadReducida()));
		retenido.setSituacionFamiliar(getAeat15Situacionfamiliar2021(
				ctx.getSituacionFamiliar(), ctx.getNifConyuge()));
		retenido.setSituacionLaboral(getAeat15SituacionLaboral2021(
				ctx.getSituacionLaboral(), ctx.getContrato(),
				ctx.getMovilidadGeografica(), ctx.getProlongacionLaboral()));
		// --------------------------------------- Descendientes
		List<net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Descendiente> descendientes = retenido.getDescendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente descendiente : ctx
				.getDescendientes()) {
			descendientes.add(getAeat15Descendiente2021(descendiente));
		}
		// ---------------------------------------- Ascendientes
		List<net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Ascendiente> ascendientes = retenido.getAscendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente ascendiente : ctx
				.getAscendientes()) {
			ascendientes.add(getAeat15Ascendiente2021(ascendiente));
		}

		// ------------------------------------ Datos económicos
		retenido.setRetribAnuales(ctx.getRetribAnuales());

		BigDecimal irregularidad1 = ctx.getIrregularidad1();
		BigDecimal irregularidad2 = ctx.getIrregularidad2();
		if (irregularidad1 != null || irregularidad2 != null) {
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Reducciones reducciones = new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Reducciones();
			reducciones.setIrregularidad1(irregularidad1);
			reducciones.setIrregularidad2(irregularidad2);
			retenido.setReducciones(reducciones);
		}

		//retenido.setGastosAnuales(ctx.getGastosAnuales());
		retenido.setCotizaciones(ctx.getGastosAnuales());
		
		if (ctx.getRdtosObtenidosCeutaMelilla())
			retenido.setRdtosObtenidosCeutaMelilla(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.RdtosObtenidosCeutaMelilla());
		retenido.setPensionCompensatoria(ctx.getPensionCompensatoria());
		retenido.setAnualidadesHijos(ctx.getAnualidadesHijos());
		if (ctx.getPagoPrestamosVivienda())
			retenido.setPagoPrestamosVivienda(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.PagoPrestamosVivienda());

		// -------------------------------------- Regularización
		CausaRegularizacion causaRegularizacion = ctx.getCausaRegularizacion();
		if (causaRegularizacion != null) {
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Regularizacion regularizacion = 
					new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Regularizacion();
			List<Integer> causas = regularizacion.getCausa();
			causas.add(causaRegularizacion.getValue());

			regularizacion.setRetribSatisfechas(ctx.getRetribSatisfechas());
			regularizacion.setRetencionPracticada(ctx.getRetencionPracticada());
			regularizacion.setRetribAnualesIniciales(ctx
					.getRetribAnualesIniciales());
			regularizacion.setRetencionAnualInicial(ctx
					.getRetencionAnualInicial());
			if (ctx.getResidenciaInicialCeutaMelilla())
				regularizacion
						.setResidenciaInicialCeutaMelilla(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Regularizacion.ResidenciaInicialCeutaMelilla());
			regularizacion.setBaseRetencion(ctx.getBaseRetencion());
			regularizacion.setMinimoPersonalFamiliarInicial(ctx
					.getMinimoPersonalFamiliarInicial());
			regularizacion.setTipoRetencion(ctx.getTipoRetencion());
			regularizacion.setMinoracionPrestamosVivienda(ctx
					.getMinoracionPrestamosVivienda());

			retenido.setRegularizacion(regularizacion);
		}

		return retenido;

	}

	private static TipoRetenidoEntrada2022 newTipoRetenidoEntrada2022(
			IIrpfCalculatorContext ctx) {

		TipoRetenidoEntrada2022 retenido = new TipoRetenidoEntrada2022();
		// ------------------------------------ Datos personales

		retenido.setNif(ctx.getNif());
		retenido.setApellidosNombre(ctx.getApellidosNombre());
		retenido.setAñoNacimiento(ctx.getAñoNacimiento());

		if (ctx.getResidenciaCeutaMelilla()) {

			retenido.setResidenciaCeutaMelilla(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.ResidenciaCeutaMelilla());
		}
		retenido.setDiscapacidad(getAeat15Discapacidad2022(ctx.getDiscapacidad(),
				ctx.getMovilidadReducida()));
		retenido.setSituacionFamiliar(getAeat15Situacionfamiliar2022(
				ctx.getSituacionFamiliar(), ctx.getNifConyuge()));
		retenido.setSituacionLaboral(getAeat15SituacionLaboral2022(
				ctx.getSituacionLaboral(), ctx.getContrato(),
				ctx.getMovilidadGeografica(), ctx.getProlongacionLaboral()));
		// --------------------------------------- Descendientes
		List<net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Descendiente> descendientes = retenido.getDescendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente descendiente : ctx
				.getDescendientes()) {
			descendientes.add(getAeat15Descendiente2022(descendiente));
		}
		// ---------------------------------------- Ascendientes
		List<net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Ascendiente> ascendientes = retenido.getAscendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente ascendiente : ctx
				.getAscendientes()) {
			ascendientes.add(getAeat15Ascendiente2022(ascendiente));
		}

		// ------------------------------------ Datos económicos
		retenido.setRetribAnuales(ctx.getRetribAnuales());

		BigDecimal irregularidad1 = ctx.getIrregularidad1();
		BigDecimal irregularidad2 = ctx.getIrregularidad2();
		if (irregularidad1 != null || irregularidad2 != null) {
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Reducciones reducciones = new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Reducciones();
			reducciones.setIrregularidad1(irregularidad1);
			reducciones.setIrregularidad2(irregularidad2);
			retenido.setReducciones(reducciones);
		}

		//retenido.setGastosAnuales(ctx.getGastosAnuales());
		retenido.setCotizaciones(ctx.getGastosAnuales());
		
		if (ctx.getRdtosObtenidosCeutaMelilla())
			retenido.setRdtosObtenidosCeutaMelilla(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.RdtosObtenidosCeutaMelilla());
		retenido.setPensionCompensatoria(ctx.getPensionCompensatoria());
		retenido.setAnualidadesHijos(ctx.getAnualidadesHijos());
		if (ctx.getPagoPrestamosVivienda())
			retenido.setPagoPrestamosVivienda(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.PagoPrestamosVivienda());

		// -------------------------------------- Regularización
		CausaRegularizacion causaRegularizacion = ctx.getCausaRegularizacion();
		if (causaRegularizacion != null) {
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Regularizacion regularizacion = 
					new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Regularizacion();
			List<Integer> causas = regularizacion.getCausa();
			causas.add(causaRegularizacion.getValue());

			regularizacion.setRetribSatisfechas(ctx.getRetribSatisfechas());
			regularizacion.setRetencionPracticada(ctx.getRetencionPracticada());
			regularizacion.setRetribAnualesIniciales(ctx
					.getRetribAnualesIniciales());
			regularizacion.setRetencionAnualInicial(ctx
					.getRetencionAnualInicial());
			if (ctx.getResidenciaInicialCeutaMelilla())
				regularizacion
						.setResidenciaInicialCeutaMelilla(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Regularizacion.ResidenciaInicialCeutaMelilla());
			regularizacion.setBaseRetencion(ctx.getBaseRetencion());
			regularizacion.setMinimoPersonalFamiliarInicial(ctx
					.getMinimoPersonalFamiliarInicial());
			regularizacion.setTipoRetencion(ctx.getTipoRetencion());
			regularizacion.setMinoracionPrestamosVivienda(ctx
					.getMinoracionPrestamosVivienda());

			retenido.setRegularizacion(regularizacion);
		}

		return retenido;

	}

	private static net.aonsolutions.core.aeat.v2020.jaxb.TipoDiscapacidad getAeat15Discapacidad2020(
			Discapacidad discapacidad, boolean movilidadReducida) {
		if (discapacidad == Discapacidad.GRADO0)
			return null;
		net.aonsolutions.core.aeat.v2020.jaxb.TipoDiscapacidad tipoDiscapacidad = new net.aonsolutions.core.aeat.v2020.jaxb.TipoDiscapacidad();
		if (discapacidad == Discapacidad.GRADO1) {
			net.aonsolutions.core.aeat.v2020.jaxb.TipoDiscapacidad.Grado1 grado1 = new net.aonsolutions.core.aeat.v2020.jaxb.TipoDiscapacidad.Grado1();
			if (movilidadReducida) {
				grado1.setMovilidadReducida(new net.aonsolutions.core.aeat.v2020.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida());
			}
			tipoDiscapacidad.setGrado1(grado1);
		} else if (discapacidad == Discapacidad.GRADO2) {
			tipoDiscapacidad.setGrado2(new net.aonsolutions.core.aeat.v2020.jaxb.TipoDiscapacidad.Grado2());
		}
		return tipoDiscapacidad;
	}
	private static net.aonsolutions.core.aeat.v2021.jaxb.TipoDiscapacidad getAeat15Discapacidad2021(
			Discapacidad discapacidad, boolean movilidadReducida) {
		if (discapacidad == Discapacidad.GRADO0)
			return null;
		net.aonsolutions.core.aeat.v2021.jaxb.TipoDiscapacidad tipoDiscapacidad = new net.aonsolutions.core.aeat.v2021.jaxb.TipoDiscapacidad();
		if (discapacidad == Discapacidad.GRADO1) {
			net.aonsolutions.core.aeat.v2021.jaxb.TipoDiscapacidad.Grado1 grado1 = new net.aonsolutions.core.aeat.v2021.jaxb.TipoDiscapacidad.Grado1();
			if (movilidadReducida) {
				grado1.setMovilidadReducida(new net.aonsolutions.core.aeat.v2021.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida());
			}
			tipoDiscapacidad.setGrado1(grado1);
		} else if (discapacidad == Discapacidad.GRADO2) {
			tipoDiscapacidad.setGrado2(new net.aonsolutions.core.aeat.v2021.jaxb.TipoDiscapacidad.Grado2());
		}
		return tipoDiscapacidad;
	}
	private static net.aonsolutions.core.aeat.v2022.jaxb.TipoDiscapacidad getAeat15Discapacidad2022(
			Discapacidad discapacidad, boolean movilidadReducida) {
		if (discapacidad == Discapacidad.GRADO0)
			return null;
		net.aonsolutions.core.aeat.v2022.jaxb.TipoDiscapacidad tipoDiscapacidad = new net.aonsolutions.core.aeat.v2022.jaxb.TipoDiscapacidad();
		if (discapacidad == Discapacidad.GRADO1) {
			net.aonsolutions.core.aeat.v2022.jaxb.TipoDiscapacidad.Grado1 grado1 = new net.aonsolutions.core.aeat.v2022.jaxb.TipoDiscapacidad.Grado1();
			if (movilidadReducida) {
				grado1.setMovilidadReducida(new net.aonsolutions.core.aeat.v2022.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida());
			}
			tipoDiscapacidad.setGrado1(grado1);
		} else if (discapacidad == Discapacidad.GRADO2) {
			tipoDiscapacidad.setGrado2(new net.aonsolutions.core.aeat.v2022.jaxb.TipoDiscapacidad.Grado2());
		}
		return tipoDiscapacidad;
	}

	private static net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionFamiliar getAeat15Situacionfamiliar2020(
			SituacionFamiliar _situacionFamiliar, String nifConyuge) {
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionFamiliar situacionFamiliar = new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionFamiliar();
		if (SituacionFamiliar.UNO == _situacionFamiliar) {
			situacionFamiliar.setSituacion1(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionFamiliar.Situacion1());
		} else if (SituacionFamiliar.DOS == _situacionFamiliar) {
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionFamiliar.Situacion2 situacion2 = 
					new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionFamiliar.Situacion2();
			situacion2.setNifConyuge(nifConyuge);
			situacionFamiliar.setSituacion2(situacion2);
		} else {
			situacionFamiliar.setSituacion3(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionFamiliar.Situacion3());
		}
		return situacionFamiliar;
	}
	private static net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionFamiliar getAeat15Situacionfamiliar2021(
			SituacionFamiliar _situacionFamiliar, String nifConyuge) {
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionFamiliar situacionFamiliar = new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionFamiliar();
		if (SituacionFamiliar.UNO == _situacionFamiliar) {
			situacionFamiliar.setSituacion1(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionFamiliar.Situacion1());
		} else if (SituacionFamiliar.DOS == _situacionFamiliar) {
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionFamiliar.Situacion2 situacion2 = 
					new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionFamiliar.Situacion2();
			situacion2.setNifConyuge(nifConyuge);
			situacionFamiliar.setSituacion2(situacion2);
		} else {
			situacionFamiliar.setSituacion3(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionFamiliar.Situacion3());
		}
		return situacionFamiliar;
	}
	private static net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionFamiliar getAeat15Situacionfamiliar2022(
			SituacionFamiliar _situacionFamiliar, String nifConyuge) {
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionFamiliar situacionFamiliar = new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionFamiliar();
		if (SituacionFamiliar.UNO == _situacionFamiliar) {
			situacionFamiliar.setSituacion1(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionFamiliar.Situacion1());
		} else if (SituacionFamiliar.DOS == _situacionFamiliar) {
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionFamiliar.Situacion2 situacion2 = 
					new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionFamiliar.Situacion2();
			situacion2.setNifConyuge(nifConyuge);
			situacionFamiliar.setSituacion2(situacion2);
		} else {
			situacionFamiliar.setSituacion3(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionFamiliar.Situacion3());
		}
		return situacionFamiliar;
	}

	private static net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Descendiente getAeat15Descendiente2020(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente _descendiente) {
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Descendiente descendiente = new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Descendiente();
		descendiente.setAñoNacimiento(_descendiente.getAñoNacimiento());
		descendiente.setAñoAdopcion(_descendiente.getAñoAdopcion());
		descendiente.setDiscapacidad(getAeat15Discapacidad2020(
				_descendiente.getDiscapacidad(),
				_descendiente.getMovilidadReducida()));
		if (_descendiente.getComputadoEntero())
			descendiente.setComputadoEntero(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Descendiente.ComputadoEntero());
		return descendiente;
	}
	private static net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Descendiente getAeat15Descendiente2021(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente _descendiente) {
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Descendiente descendiente = new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Descendiente();
		descendiente.setAñoNacimiento(_descendiente.getAñoNacimiento());
		descendiente.setAñoAdopcion(_descendiente.getAñoAdopcion());
		descendiente.setDiscapacidad(getAeat15Discapacidad2021(
				_descendiente.getDiscapacidad(),
				_descendiente.getMovilidadReducida()));
		if (_descendiente.getComputadoEntero())
			descendiente.setComputadoEntero(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Descendiente.ComputadoEntero());
		return descendiente;
	}
	private static net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Descendiente getAeat15Descendiente2022(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente _descendiente) {
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Descendiente descendiente = new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Descendiente();
		descendiente.setAñoNacimiento(_descendiente.getAñoNacimiento());
		descendiente.setAñoAdopcion(_descendiente.getAñoAdopcion());
		descendiente.setDiscapacidad(getAeat15Discapacidad2022(
				_descendiente.getDiscapacidad(),
				_descendiente.getMovilidadReducida()));
		if (_descendiente.getComputadoEntero())
			descendiente.setComputadoEntero(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Descendiente.ComputadoEntero());
		return descendiente;
	}

	private static net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Ascendiente getAeat15Ascendiente2020(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente _ascendiente) {
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Ascendiente ascendiente = new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.Ascendiente();
		ascendiente.setAñoNacimiento(_ascendiente.getAñoNacimiento());
		ascendiente.setDiscapacidad(getAeat15Discapacidad2020(
				_ascendiente.getDiscapacidad(),
				_ascendiente.getMovilidadReducida()));
		Convivencia convivencia = _ascendiente.getConvivecia();
		if (convivencia == null) {
			convivencia = Convivencia.UNO;
		}
		ascendiente.setConvivencia(convivencia.getValue());
		return ascendiente;
	}

	private static net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Ascendiente getAeat15Ascendiente2021(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente _ascendiente) {
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Ascendiente ascendiente = new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.Ascendiente();
		ascendiente.setAñoNacimiento(_ascendiente.getAñoNacimiento());
		ascendiente.setDiscapacidad(getAeat15Discapacidad2021(
				_ascendiente.getDiscapacidad(),
				_ascendiente.getMovilidadReducida()));
		Convivencia convivencia = _ascendiente.getConvivecia();
		if (convivencia == null) {
			convivencia = Convivencia.UNO;
		}
		ascendiente.setConvivencia(convivencia.getValue());
		return ascendiente;
	}

	private static net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Ascendiente getAeat15Ascendiente2022(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente _ascendiente) {
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Ascendiente ascendiente = new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.Ascendiente();
		ascendiente.setAñoNacimiento(_ascendiente.getAñoNacimiento());
		ascendiente.setDiscapacidad(getAeat15Discapacidad2022(
				_ascendiente.getDiscapacidad(),
				_ascendiente.getMovilidadReducida()));
		Convivencia convivencia = _ascendiente.getConvivecia();
		if (convivencia == null) {
			convivencia = Convivencia.UNO;
		}
		ascendiente.setConvivencia(convivencia.getValue());
		return ascendiente;
	}

	private static net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral getAeat15SituacionLaboral2020(
			SituacionLaboral _situacionLaboral, Contrato contrato,
			boolean movilidadGeografica, boolean prolongacionLaboral) {
		net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral situacionLaboral = new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral();
		if (SituacionLaboral.DESEMPLEADO == _situacionLaboral) {
			situacionLaboral.setDesempleado(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral.Desempleado());
		} else if (SituacionLaboral.PENSIONISTA == _situacionLaboral) {
			situacionLaboral.setPensionista(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral.Pensionista());
		} else if (SituacionLaboral.TRABAJADOR_ACTIVO == _situacionLaboral) {
			net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral.TrabajadorActivo trabajadorActivo = new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral.TrabajadorActivo();
			if (contrato == null)
				contrato = Contrato.UNO;
			trabajadorActivo.setContrato(contrato.getValue());
			if (movilidadGeografica)
				trabajadorActivo
						.setMovilidadGeografica(new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral.TrabajadorActivo.MovilidadGeografica());
			
			/* At 2016 NOT Found
			if (prolongacionLaboral)
				trabajadorActivo
						.setProlongacionLaboral(new ProlongacionLaboral());
			*/
			
			situacionLaboral.setTrabajadorActivo(trabajadorActivo);
		} else {
			situacionLaboral.setOtraSituacion( new net.aonsolutions.core.aeat.v2020.jaxb.TipoRetenidoEntrada2020.SituacionLaboral.OtraSituacion());
		}
		return situacionLaboral;
	}
	private static net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral getAeat15SituacionLaboral2021(
			SituacionLaboral _situacionLaboral, Contrato contrato,
			boolean movilidadGeografica, boolean prolongacionLaboral) {
		net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral situacionLaboral = new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral();
		if (SituacionLaboral.DESEMPLEADO == _situacionLaboral) {
			situacionLaboral.setDesempleado(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral.Desempleado());
		} else if (SituacionLaboral.PENSIONISTA == _situacionLaboral) {
			situacionLaboral.setPensionista(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral.Pensionista());
		} else if (SituacionLaboral.TRABAJADOR_ACTIVO == _situacionLaboral) {
			net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral.TrabajadorActivo trabajadorActivo = new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral.TrabajadorActivo();
			if (contrato == null)
				contrato = Contrato.UNO;
			trabajadorActivo.setContrato(contrato.getValue());
			if (movilidadGeografica)
				trabajadorActivo
						.setMovilidadGeografica(new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral.TrabajadorActivo.MovilidadGeografica());
			
			/* At 2016 NOT Found
			if (prolongacionLaboral)
				trabajadorActivo
						.setProlongacionLaboral(new ProlongacionLaboral());
			*/
			
			situacionLaboral.setTrabajadorActivo(trabajadorActivo);
		} else {
			situacionLaboral.setOtraSituacion( new net.aonsolutions.core.aeat.v2021.jaxb.TipoRetenidoEntrada2021.SituacionLaboral.OtraSituacion());
		}
		return situacionLaboral;
	}
	private static net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral getAeat15SituacionLaboral2022(
			SituacionLaboral _situacionLaboral, Contrato contrato,
			boolean movilidadGeografica, boolean prolongacionLaboral) {
		net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral situacionLaboral = new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral();
		if (SituacionLaboral.DESEMPLEADO == _situacionLaboral) {
			situacionLaboral.setDesempleado(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral.Desempleado());
		} else if (SituacionLaboral.PENSIONISTA == _situacionLaboral) {
			situacionLaboral.setPensionista(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral.Pensionista());
		} else if (SituacionLaboral.TRABAJADOR_ACTIVO == _situacionLaboral) {
			net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral.TrabajadorActivo trabajadorActivo = new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral.TrabajadorActivo();
			if (contrato == null)
				contrato = Contrato.UNO;
			trabajadorActivo.setContrato(contrato.getValue());
			if (movilidadGeografica)
				trabajadorActivo
						.setMovilidadGeografica(new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral.TrabajadorActivo.MovilidadGeografica());
			
			/* At 2016 NOT Found
			if (prolongacionLaboral)
				trabajadorActivo
						.setProlongacionLaboral(new ProlongacionLaboral());
			*/
			
			situacionLaboral.setTrabajadorActivo(trabajadorActivo);
		} else {
			situacionLaboral.setOtraSituacion( new net.aonsolutions.core.aeat.v2022.jaxb.TipoRetenidoEntrada2022.SituacionLaboral.OtraSituacion());
		}
		return situacionLaboral;
	}
}
