package com.esferalia.aon.ui.payroll.controller.launcher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.aeat.jaxb.TipoError;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.esferalia.aon.payroll.irpf.AbstractIrpfTester;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class IrpfTestLauncher extends AbstractIrpfLauncher {
	
	private IrpfTester irpfTester;
	
	@Override
	protected void execute(IrpfLauncherParams params) throws SalaryException,
			ExpressionException, SQLException {
		Connection connection = getConnection();
		Date date = getParams().getDate();
		irpfTester = new IrpfTester(connection, date);
		
		super.execute(params);
		
		String msg = String.format("Total contratos procesados: %d ", irpfTester.getAttempted());
		onMessage(new InfoMessage( msg));
		msg = String.format("Total I.R.P.F chequeados sin detectar problemas: %d ", irpfTester.getSuccess());
		onMessage(new InfoMessage(msg));
		
	}
	
	
	@Override
	public void onWarn(TipoRetenedorError2011 retenedorError2011,
			TipoRetenidoError2011 retenidoError2011) {
		for (TipoError error : retenidoError2011.getError()) {
			WarnMessage warnMessage = 
				new WarnMessage(error.getDescripcion().substring(0, 80));
			warnMessage.setEmployeeName(retenidoError2011.getNif());
			warnMessage.setEnterpriseName(retenedorError2011.getNif());
			onMessage(warnMessage);
		}
		
	}

	@Override
	public void onError(TipoRetenedorError2011 retenedorError2011,
			TipoRetenidoError2011 retenidoError2011) {
		for (TipoError error : retenidoError2011.getError()) {
			ErrorMessage errorMessage = 
				new ErrorMessage(error.getDescripcion());
			onMessage(errorMessage);
		}
		
	}
	@Override
	public void onSalida(TipoRetenedorSalida2011 retenedorSalida2011,
			TipoRetenidoSalida2011 retenidoSalida2011) {
		irpfTester.onSalida(retenedorSalida2011, retenidoSalida2011);
	}
	
	private class IrpfTester extends AbstractIrpfTester {
	
		private int warnings = 0;
		
		public int getWarnings() {
			return warnings;
		}

		public IrpfTester(Connection connection, Date date) 
		throws SQLException {
			super( connection, date );
		}
		
		@Override
		public void onError(TipoRetenedorError2011 retenedorError2011,
				TipoRetenidoError2011 retenidoError2011) {
		}

		@Override
		public void onSalida(TipoRetenedorSalida2011 retenedorSalida2011,
				TipoRetenidoSalida2011 retenidoSalida2011) {
			try {
				super.onSalida(retenedorSalida2011, retenidoSalida2011);

			} catch ( UnExpectedValue e ) {

				Double priorIrpf = e.getPriorIrpf();
				BigDecimal tipoRetencion = retenidoSalida2011.getTipoRetencion();
				
				if ( priorIrpf != null ) {
					double dbIrpf = e.getIrpf();
					double calcIrpf = tipoRetencion.doubleValue();
					if ( calcIrpf < priorIrpf && priorIrpf == dbIrpf ){
						warnings++;
						WarnMessage warnMessage = new WarnMessage(e.getMessage());
						warnMessage.setContractId(e.getContractId());
						warnMessage.setEmployeeName(retenidoSalida2011.getApellidosNombre());
						warnMessage.setEnterpriseName(retenedorSalida2011.getApellidosNombre());
						onMessage(warnMessage);
						return;
					} // Min prior I.R.P.F ?
				}

				ErrorMessage errorMessage = 
					new ErrorMessage(e.getMessage());
				errorMessage.setContractId(e.getContractId());
				errorMessage.setEmployeeName(retenidoSalida2011.getApellidosNombre());
				errorMessage.setEnterpriseName(retenedorSalida2011.getApellidosNombre());
				onMessage(errorMessage);
				
			} catch ( NotFoundException e ) {
				WarnMessage warnMessage = 
					new WarnMessage(String.format("No existen datos calculados para este trabajador."));
				warnMessage.setContractId(e.getContractId());
				warnMessage.setNewIrpf(retenidoSalida2011.getTipoRetencion().toString());
				warnMessage.setEmployeeName(retenidoSalida2011.getApellidosNombre());
				warnMessage.setEnterpriseName(retenedorSalida2011.getApellidosNombre());
				onMessage(warnMessage);
			}
		}
	}
	
}
