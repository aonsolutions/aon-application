package com.esferalia.aon.ui.payroll.controller.launcher;

import java.util.List;

import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.aeat.jaxb.TipoRetenedorEntrada2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011;



public class IrpfCalculator  {

	private AEATRetencionesEntrada2011 entrada;

	public AEATRetencionesEntrada2011 getEntrada() {
		List<TipoRetenedorEntrada2011> list = entrada.getRetenedor();
		List<TipoRetenidoEntrada2011> retenido = list.get(0).getRetenido();
		retenido.get(0).getDiscapacidad().getGrado1(). getMovilidadReducida();
		
		
		
		return entrada;
	}

	public void setEntrada(AEATRetencionesEntrada2011 entrada) {
		this.entrada = entrada;
	}
	
}
