package com.esferalia.aon.payroll.irpf;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;

import es.aeat.pret.rd13.ModeloRetencionesXMLJaxb;
import es.aeat.pret.rd13.XMLProgressListener;
import es.aeat.pret.rw13.jaxb.AEATRetencionesEntrada2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesSalida2013;
import es.aeat.pret.rw13.jaxb.TipoError;
import es.aeat.pret.rw13.jaxb.TipoRetenedorError2013;
import es.aeat.pret.rw13.jaxb.TipoRetenedorSalida2013;
import es.aeat.pret.rw13.jaxb.TipoRetenidoError2013;
import es.aeat.pret.rw13.jaxb.TipoRetenidoSalida2013;

public class IrpfCalculator {

	public static AEATRetencionesSalida2013 calculate(
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

	public static double calculate(IIrpfCalculatorContext ctx) {
		try {
			ctx.next();
			AEATRetencionesEntrada2013 aeatRetencionesEntrada2013 = AEATRetencionesEntradaFactory
					.create(ctx);
			AEATRetencionesSalida2013 aeatRetencionesSalida2013 = IrpfCalculator
					.calculate(aeatRetencionesEntrada2013);
			List<TipoRetenedorSalida2013> retenedores = aeatRetencionesSalida2013
					.getRetenedor();
			TipoRetenedorSalida2013 retenedorSalida2013 = retenedores.get(0);
			List<TipoRetenidoSalida2013> retenidos = retenedorSalida2013
					.getRetenido();
			TipoRetenidoSalida2013 retenidoSalida2013 = retenidos.get(0);
			BigDecimal tipoRetencion = retenidoSalida2013.getTipoRetencion();
			return tipoRetencion != null ? tipoRetencion.doubleValue() : 0.00;
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper( new ExpressionException(e) );
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		} catch (IrpfCalculateException e) {
			AEATRetencionesError2013 error = e.getAEATRetencionesError2013();
			List<TipoRetenedorError2013> retenedores = error.getRetenedor();
			String message = null;
			TipoRetenedorError2013 retenedor = retenedores.get(0);
			List<TipoRetenidoError2013> retenidos = retenedor.getRetenido();
			if (retenidos.size() > 0) {
				TipoRetenidoError2013 retenido = retenidos.get(0);
				List<TipoError> tipoErrores = retenido.getError();
				TipoError tipoError = tipoErrores.get(0);
				message = tipoError.getDescripcion();
			}
			ExpressionException expressionException = new CheckException(
					message);
			throw new ExpressionExceptionWrapper(expressionException);
		}
	}

}
