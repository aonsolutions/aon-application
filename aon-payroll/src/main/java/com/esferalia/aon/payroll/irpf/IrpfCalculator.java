package com.esferalia.aon.payroll.irpf;

import es.aeat.pret.rd13.ModeloRetencionesXMLJaxb;
import es.aeat.pret.rd13.XMLProgressListener;
import es.aeat.pret.rw13.jaxb.AEATRetencionesEntrada2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesError2013;
import es.aeat.pret.rw13.jaxb.AEATRetencionesSalida2013;

public class IrpfCalculator {

	public static AEATRetencionesSalida2013 calculate(
			AEATRetencionesEntrada2013 entrada2013)
			throws IrpfCalculateException {
		ModeloRetencionesXMLJaxb modeloRetencionesXMLJaxb = new ModeloRetencionesXMLJaxb();

		modeloRetencionesXMLJaxb.setEntradaRetenciones(entrada2013);
		modeloRetencionesXMLJaxb.setXMLProgressListener( new XMLProgressListener() {
			@Override
			public void avanzarBarraProgreso() {
			}
		});
		
		try {
			modeloRetencionesXMLJaxb.calcularXML();
		} catch ( NullPointerException ignore ){
			
		}

		AEATRetencionesError2013 error2013 = modeloRetencionesXMLJaxb
				.getSalidaXMLError();
		if (error2013 != null)
			throw new IrpfCalculateException(error2013);

		AEATRetencionesSalida2013 salida2013 = modeloRetencionesXMLJaxb
				.getSalidaRetenciones();
		
		return salida2013;
	}

}
