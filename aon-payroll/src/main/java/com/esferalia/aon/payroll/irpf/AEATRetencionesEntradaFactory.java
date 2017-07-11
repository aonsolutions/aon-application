package com.esferalia.aon.payroll.irpf;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.List;

import net.aonsolutions.core.aeat.jaxb.AEATRetencionesEntrada2016;
import net.aonsolutions.core.aeat.jaxb.TipoRetenedorEntrada2016;
import net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016;
import net.aonsolutions.core.aeat.v2017.jaxb.AEATRetencionesEntrada2017;
import net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenedorEntrada2017;
import net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017;
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

	public static AEATRetencionesEntrada2016 create2016(
			IIrpfCalculatorContext ctx) throws SQLException,
			ExpressionException {

		AEATRetencionesEntrada2016 entrada2016 = new AEATRetencionesEntrada2016();

		net.aonsolutions.core.aeat.jaxb.TipoRetenciones tipoRetenciones = new net.aonsolutions.core.aeat.jaxb.TipoRetenciones();
		tipoRetenciones.setEjercicio(2016);
		tipoRetenciones.setCodModelo("RET");
		entrada2016.setIdDoc(tipoRetenciones);

		List<TipoRetenedorEntrada2016> retenedores = entrada2016.getRetenedor();
		TipoRetenedorEntrada2016 retenedor = new TipoRetenedorEntrada2016();

		retenedor.setNif(ctx.getRetenedorNif());
		retenedor.setApellidosNombre(ctx.getRetenedorApellidosNombre());
		retenedores.add(retenedor);

		List<TipoRetenidoEntrada2016> retenidos = retenedor.getRetenido();
		TipoRetenidoEntrada2016 retenido = newTipoRetenidoEntrada2016(ctx);
		retenidos.add(retenido);

		return entrada2016;
	}

	public static AEATRetencionesEntrada2017 create2017(
			IIrpfCalculatorContext ctx) throws SQLException,
			ExpressionException {

		AEATRetencionesEntrada2017 entrada2017 = new AEATRetencionesEntrada2017();

		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenciones tipoRetenciones = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenciones();
		tipoRetenciones.setEjercicio(2017);
		tipoRetenciones.setCodModelo("RET");
		entrada2017.setIdDoc(tipoRetenciones);

		List<TipoRetenedorEntrada2017> retenedores = entrada2017.getRetenedor();
		TipoRetenedorEntrada2017 retenedor = new TipoRetenedorEntrada2017();

		retenedor.setNif(ctx.getRetenedorNif());
		retenedor.setApellidosNombre(ctx.getRetenedorApellidosNombre());
		retenedores.add(retenedor);

		List<TipoRetenidoEntrada2017> retenidos = retenedor.getRetenido();
		TipoRetenidoEntrada2017 retenido = newTipoRetenidoEntrada2017(ctx);
		retenidos.add(retenido);

		return entrada2017;
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

	private static TipoRetenidoEntrada2016 newTipoRetenidoEntrada2016(
			IIrpfCalculatorContext ctx) {

		TipoRetenidoEntrada2016 retenido = new TipoRetenidoEntrada2016();
		// ------------------------------------ Datos personales

		retenido.setNif(ctx.getNif());
		retenido.setApellidosNombre(ctx.getApellidosNombre());
		retenido.setAñoNacimiento(ctx.getAñoNacimiento());

		if (ctx.getResidenciaCeutaMelilla()) {

			retenido.setResidenciaCeutaMelilla(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.ResidenciaCeutaMelilla());
		}
		retenido.setDiscapacidad(getAeat15Discapacidad2016(ctx.getDiscapacidad(),
				ctx.getMovilidadReducida()));
		retenido.setSituacionFamiliar(getAeat15Situacionfamiliar2016(
				ctx.getSituacionFamiliar(), ctx.getNifConyuge()));
		retenido.setSituacionLaboral(getAeat15SituacionLaboral2016(
				ctx.getSituacionLaboral(), ctx.getContrato(),
				ctx.getMovilidadGeografica(), ctx.getProlongacionLaboral()));
		// --------------------------------------- Descendientes
		List<net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Descendiente> descendientes = retenido.getDescendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente descendiente : ctx
				.getDescendientes()) {
			descendientes.add(getAeat15Descendiente2016(descendiente));
		}
		// ---------------------------------------- Ascendientes
		List<net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Ascendiente> ascendientes = retenido.getAscendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente ascendiente : ctx
				.getAscendientes()) {
			ascendientes.add(getAeat15Ascendiente2016(ascendiente));
		}

		// ------------------------------------ Datos económicos
		retenido.setRetribAnuales(ctx.getRetribAnuales());

		BigDecimal irregularidad1 = ctx.getIrregularidad1();
		BigDecimal irregularidad2 = ctx.getIrregularidad2();
		if (irregularidad1 != null || irregularidad2 != null) {
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Reducciones reducciones = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Reducciones();
			reducciones.setIrregularidad1(irregularidad1);
			reducciones.setIrregularidad2(irregularidad2);
			retenido.setReducciones(reducciones);
		}

		//retenido.setGastosAnuales(ctx.getGastosAnuales());
		retenido.setCotizaciones(ctx.getGastosAnuales());
		
		if (ctx.getRdtosObtenidosCeutaMelilla())
			retenido.setRdtosObtenidosCeutaMelilla(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.RdtosObtenidosCeutaMelilla());
		retenido.setPensionCompensatoria(ctx.getPensionCompensatoria());
		retenido.setAnualidadesHijos(ctx.getAnualidadesHijos());
		if (ctx.getPagoPrestamosVivienda())
			retenido.setPagoPrestamosVivienda(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.PagoPrestamosVivienda());

		// -------------------------------------- Regularización
		CausaRegularizacion causaRegularizacion = ctx.getCausaRegularizacion();
		if (causaRegularizacion != null) {
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Regularizacion regularizacion = 
					new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Regularizacion();
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
						.setResidenciaInicialCeutaMelilla(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Regularizacion.ResidenciaInicialCeutaMelilla());
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
	
	private static TipoRetenidoEntrada2017 newTipoRetenidoEntrada2017(
			IIrpfCalculatorContext ctx) {

		TipoRetenidoEntrada2017 retenido = new TipoRetenidoEntrada2017();
		// ------------------------------------ Datos personales

		retenido.setNif(ctx.getNif());
		retenido.setApellidosNombre(ctx.getApellidosNombre());
		retenido.setAñoNacimiento(ctx.getAñoNacimiento());

		if (ctx.getResidenciaCeutaMelilla()) {

			retenido.setResidenciaCeutaMelilla(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.ResidenciaCeutaMelilla());
		}
		retenido.setDiscapacidad(getAeat15Discapacidad2017(ctx.getDiscapacidad(),
				ctx.getMovilidadReducida()));
		retenido.setSituacionFamiliar(getAeat15Situacionfamiliar2017(
				ctx.getSituacionFamiliar(), ctx.getNifConyuge()));
		retenido.setSituacionLaboral(getAeat15SituacionLaboral2017(
				ctx.getSituacionLaboral(), ctx.getContrato(),
				ctx.getMovilidadGeografica(), ctx.getProlongacionLaboral()));
		// --------------------------------------- Descendientes
		List<net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Descendiente> descendientes = retenido.getDescendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente descendiente : ctx
				.getDescendientes()) {
			descendientes.add(getAeat15Descendiente2017(descendiente));
		}
		// ---------------------------------------- Ascendientes
		List<net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Ascendiente> ascendientes = retenido.getAscendiente();
		for (com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente ascendiente : ctx
				.getAscendientes()) {
			ascendientes.add(getAeat15Ascendiente2017(ascendiente));
		}

		// ------------------------------------ Datos económicos
		retenido.setRetribAnuales(ctx.getRetribAnuales());

		BigDecimal irregularidad1 = ctx.getIrregularidad1();
		BigDecimal irregularidad2 = ctx.getIrregularidad2();
		if (irregularidad1 != null || irregularidad2 != null) {
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Reducciones reducciones = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Reducciones();
			reducciones.setIrregularidad1(irregularidad1);
			reducciones.setIrregularidad2(irregularidad2);
			retenido.setReducciones(reducciones);
		}

		//retenido.setGastosAnuales(ctx.getGastosAnuales());
		retenido.setCotizaciones(ctx.getGastosAnuales());
		
		if (ctx.getRdtosObtenidosCeutaMelilla())
			retenido.setRdtosObtenidosCeutaMelilla(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.RdtosObtenidosCeutaMelilla());
		retenido.setPensionCompensatoria(ctx.getPensionCompensatoria());
		retenido.setAnualidadesHijos(ctx.getAnualidadesHijos());
		if (ctx.getPagoPrestamosVivienda())
			retenido.setPagoPrestamosVivienda(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.PagoPrestamosVivienda());

		// -------------------------------------- Regularización
		CausaRegularizacion causaRegularizacion = ctx.getCausaRegularizacion();
		if (causaRegularizacion != null) {
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Regularizacion regularizacion = 
					new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Regularizacion();
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
						.setResidenciaInicialCeutaMelilla(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Regularizacion.ResidenciaInicialCeutaMelilla());
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

	private static net.aonsolutions.core.aeat.jaxb.TipoDiscapacidad getAeat15Discapacidad2016(
			Discapacidad discapacidad, boolean movilidadReducida) {
		if (discapacidad == Discapacidad.GRADO0)
			return null;
		net.aonsolutions.core.aeat.jaxb.TipoDiscapacidad tipoDiscapacidad = new net.aonsolutions.core.aeat.jaxb.TipoDiscapacidad();
		if (discapacidad == Discapacidad.GRADO1) {
			net.aonsolutions.core.aeat.jaxb.TipoDiscapacidad.Grado1 grado1 = new net.aonsolutions.core.aeat.jaxb.TipoDiscapacidad.Grado1();
			if (movilidadReducida) {
				grado1.setMovilidadReducida(new net.aonsolutions.core.aeat.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida());
			}
			tipoDiscapacidad.setGrado1(grado1);
		} else if (discapacidad == Discapacidad.GRADO2) {
			tipoDiscapacidad.setGrado2(new net.aonsolutions.core.aeat.jaxb.TipoDiscapacidad.Grado2());
		}
		return tipoDiscapacidad;
	}

	private static net.aonsolutions.core.aeat.v2017.jaxb.TipoDiscapacidad getAeat15Discapacidad2017(
			Discapacidad discapacidad, boolean movilidadReducida) {
		if (discapacidad == Discapacidad.GRADO0)
			return null;
		net.aonsolutions.core.aeat.v2017.jaxb.TipoDiscapacidad tipoDiscapacidad = new net.aonsolutions.core.aeat.v2017.jaxb.TipoDiscapacidad();
		if (discapacidad == Discapacidad.GRADO1) {
			net.aonsolutions.core.aeat.v2017.jaxb.TipoDiscapacidad.Grado1 grado1 = new net.aonsolutions.core.aeat.v2017.jaxb.TipoDiscapacidad.Grado1();
			if (movilidadReducida) {
				grado1.setMovilidadReducida(new net.aonsolutions.core.aeat.v2017.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida());
			}
			tipoDiscapacidad.setGrado1(grado1);
		} else if (discapacidad == Discapacidad.GRADO2) {
			tipoDiscapacidad.setGrado2(new net.aonsolutions.core.aeat.v2017.jaxb.TipoDiscapacidad.Grado2());
		}
		return tipoDiscapacidad;
	}

	private static net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionFamiliar getAeat15Situacionfamiliar2016(
			SituacionFamiliar _situacionFamiliar, String nifConyuge) {
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionFamiliar situacionFamiliar = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionFamiliar();
		if (SituacionFamiliar.UNO == _situacionFamiliar) {
			situacionFamiliar.setSituacion1(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionFamiliar.Situacion1());
		} else if (SituacionFamiliar.DOS == _situacionFamiliar) {
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionFamiliar.Situacion2 situacion2 = 
					new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionFamiliar.Situacion2();
			situacion2.setNifConyuge(nifConyuge);
			situacionFamiliar.setSituacion2(situacion2);
		} else {
			situacionFamiliar.setSituacion3(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionFamiliar.Situacion3());
		}
		return situacionFamiliar;
	}

	private static net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionFamiliar getAeat15Situacionfamiliar2017(
			SituacionFamiliar _situacionFamiliar, String nifConyuge) {
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionFamiliar situacionFamiliar = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionFamiliar();
		if (SituacionFamiliar.UNO == _situacionFamiliar) {
			situacionFamiliar.setSituacion1(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionFamiliar.Situacion1());
		} else if (SituacionFamiliar.DOS == _situacionFamiliar) {
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionFamiliar.Situacion2 situacion2 = 
					new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionFamiliar.Situacion2();
			situacion2.setNifConyuge(nifConyuge);
			situacionFamiliar.setSituacion2(situacion2);
		} else {
			situacionFamiliar.setSituacion3(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionFamiliar.Situacion3());
		}
		return situacionFamiliar;
	}

	private static net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Descendiente getAeat15Descendiente2016(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente _descendiente) {
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Descendiente descendiente = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Descendiente();
		descendiente.setAñoNacimiento(_descendiente.getAñoNacimiento());
		descendiente.setAñoAdopcion(_descendiente.getAñoAdopcion());
		descendiente.setDiscapacidad(getAeat15Discapacidad2016(
				_descendiente.getDiscapacidad(),
				_descendiente.getMovilidadReducida()));
		if (_descendiente.getComputadoEntero())
			descendiente.setComputadoEntero(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Descendiente.ComputadoEntero());
		return descendiente;
	}

	private static net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Descendiente getAeat15Descendiente2017(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente _descendiente) {
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Descendiente descendiente = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Descendiente();
		descendiente.setAñoNacimiento(_descendiente.getAñoNacimiento());
		descendiente.setAñoAdopcion(_descendiente.getAñoAdopcion());
		descendiente.setDiscapacidad(getAeat15Discapacidad2017(
				_descendiente.getDiscapacidad(),
				_descendiente.getMovilidadReducida()));
		if (_descendiente.getComputadoEntero())
			descendiente.setComputadoEntero(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Descendiente.ComputadoEntero());
		return descendiente;
	}


	private static net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Ascendiente getAeat15Ascendiente2016(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente _ascendiente) {
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Ascendiente ascendiente = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.Ascendiente();
		ascendiente.setAñoNacimiento(_ascendiente.getAñoNacimiento());
		ascendiente.setDiscapacidad(getAeat15Discapacidad2016(
				_ascendiente.getDiscapacidad(),
				_ascendiente.getMovilidadReducida()));
		Convivencia convivencia = _ascendiente.getConvivecia();
		if (convivencia == null) {
			convivencia = Convivencia.UNO;
		}
		ascendiente.setConvivencia(convivencia.getValue());
		return ascendiente;
	}

	private static net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Ascendiente getAeat15Ascendiente2017(
			com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente _ascendiente) {
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Ascendiente ascendiente = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.Ascendiente();
		ascendiente.setAñoNacimiento(_ascendiente.getAñoNacimiento());
		ascendiente.setDiscapacidad(getAeat15Discapacidad2017(
				_ascendiente.getDiscapacidad(),
				_ascendiente.getMovilidadReducida()));
		Convivencia convivencia = _ascendiente.getConvivecia();
		if (convivencia == null) {
			convivencia = Convivencia.UNO;
		}
		ascendiente.setConvivencia(convivencia.getValue());
		return ascendiente;
	}

	private static net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral getAeat15SituacionLaboral2016(
			SituacionLaboral _situacionLaboral, Contrato contrato,
			boolean movilidadGeografica, boolean prolongacionLaboral) {
		net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral situacionLaboral = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral();
		if (SituacionLaboral.DESEMPLEADO == _situacionLaboral) {
			situacionLaboral.setDesempleado(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral.Desempleado());
		} else if (SituacionLaboral.PENSIONISTA == _situacionLaboral) {
			situacionLaboral.setPensionista(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral.Pensionista());
		} else if (SituacionLaboral.TRABAJADOR_ACTIVO == _situacionLaboral) {
			net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral.TrabajadorActivo trabajadorActivo = new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral.TrabajadorActivo();
			if (contrato == null)
				contrato = Contrato.UNO;
			trabajadorActivo.setContrato(contrato.getValue());
			if (movilidadGeografica)
				trabajadorActivo
						.setMovilidadGeografica(new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral.TrabajadorActivo.MovilidadGeografica());
			
			/* At 2016 NOT Found
			if (prolongacionLaboral)
				trabajadorActivo
						.setProlongacionLaboral(new ProlongacionLaboral());
			*/
			
			situacionLaboral.setTrabajadorActivo(trabajadorActivo);
		} else {
			situacionLaboral.setOtraSituacion( new net.aonsolutions.core.aeat.jaxb.TipoRetenidoEntrada2016.SituacionLaboral.OtraSituacion());
		}
		return situacionLaboral;
	}

	private static net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral getAeat15SituacionLaboral2017(
			SituacionLaboral _situacionLaboral, Contrato contrato,
			boolean movilidadGeografica, boolean prolongacionLaboral) {
		net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral situacionLaboral = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral();
		if (SituacionLaboral.DESEMPLEADO == _situacionLaboral) {
			situacionLaboral.setDesempleado(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral.Desempleado());
		} else if (SituacionLaboral.PENSIONISTA == _situacionLaboral) {
			situacionLaboral.setPensionista(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral.Pensionista());
		} else if (SituacionLaboral.TRABAJADOR_ACTIVO == _situacionLaboral) {
			net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral.TrabajadorActivo trabajadorActivo = new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral.TrabajadorActivo();
			if (contrato == null)
				contrato = Contrato.UNO;
			trabajadorActivo.setContrato(contrato.getValue());
			if (movilidadGeografica)
				trabajadorActivo
						.setMovilidadGeografica(new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral.TrabajadorActivo.MovilidadGeografica());
			
			/* At 2016 NOT Found
			if (prolongacionLaboral)
				trabajadorActivo
						.setProlongacionLaboral(new ProlongacionLaboral());
			*/
			
			situacionLaboral.setTrabajadorActivo(trabajadorActivo);
		} else {
			situacionLaboral.setOtraSituacion( new net.aonsolutions.core.aeat.v2017.jaxb.TipoRetenidoEntrada2017.SituacionLaboral.OtraSituacion());
		}
		return situacionLaboral;
	}

}
