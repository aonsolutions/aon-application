package com.esferalia.aon.payroll.irpf.sql;

import java.util.List;

import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.aeat.jaxb.TipoRetenedorEntrada2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011;
import com.code.aon.config.enumeration.Administration;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.IrpfCalculator.CallbackHandler;
import com.esferalia.aon.payroll.irpf.IrpfException;
import com.esferalia.aon.payroll.irpf.sql.SQLAEAT2011Factory.Entrada2011Handler;
import com.esferalia.aon.salary.expression.ExpressionException;

public class DefaultEntrada2011Handler implements Entrada2011Handler {
	
	private AEATRetencionesEntrada2011 	entrada2011;
	private TipoRetenedorEntrada2011 	retenedorEntrada2011;
	
	private CallbackHandler 			calculatorHandler;
	
	public DefaultEntrada2011Handler( CallbackHandler calculatorHandler){
		this.calculatorHandler = calculatorHandler;
	}
	
	@Override
	public void onAEATRetencionesEntrada2011(
			AEATRetencionesEntrada2011 entrada2011) {
		this.entrada2011 = entrada2011;
	}

	@Override
	public void onTipoRetenedorEntrada2011(
			TipoRetenedorEntrada2011 retenedorEntrada2011) {
		this.retenedorEntrada2011 = retenedorEntrada2011;
	}

	@Override
	public void onTipoRetenidoEntrada2011(Administration administration,
			TipoRetenidoEntrada2011 retenidoEntrada2011) {

		try {
			IrpfCalculator irpfCalculator  =
				IrpfCalculator.getCalculator(administration);
			
			List<TipoRetenidoEntrada2011> retenido = 
				retenedorEntrada2011.getRetenido();
			retenido.clear();
			retenido.add(retenidoEntrada2011);
			
			List<TipoRetenedorEntrada2011> retenedor =
				entrada2011.getRetenedor();
			retenedor.clear();
			retenedor.add(retenedorEntrada2011);
			
			irpfCalculator.calculate(entrada2011, calculatorHandler);
			
		} catch (IrpfException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (ExpressionException e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
	}

}
