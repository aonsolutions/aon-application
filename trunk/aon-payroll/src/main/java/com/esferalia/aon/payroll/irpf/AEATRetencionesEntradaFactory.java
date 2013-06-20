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
import es.aeat.pret.rw13.util.ValidaNif;

public class AEATRetencionesEntradaFactory {

	private static final MathContext MATH_CONTEXT = new MathContext(3);

	public static AEATRetencionesEntrada2013 create(
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
		retenedor.setApellidosNombre(ctx
				.getRetenedorApellidosNombre());
		retenedores.add(retenedor);

		List<TipoRetenidoEntrada2013> retenidos = retenedor.getRetenido();
		TipoRetenidoEntrada2013 retenido = newTipoRetenidoEntrada2013(ctx);
		retenidos.add(retenido);

		return entrada2013;
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

}
