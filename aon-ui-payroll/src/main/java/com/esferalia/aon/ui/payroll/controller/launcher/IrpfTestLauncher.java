package com.esferalia.aon.ui.payroll.controller.launcher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aeat.jaxb.TipoError;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.esferalia.aon.payroll.sql.AbstractSQL.IrpfResult;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfResultColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

public class IrpfTestLauncher extends AbstractIrpfLauncher {
	
	private PreparedStatement  preparedStatement;
	
	public IrpfTestLauncher() throws SQLException {
	}
	
	
	@Override
	protected void execute(IrpfLauncherParams params) throws SalaryException,
			ExpressionException, SQLException {
		Connection connection = getConnection();
		try {
			preparedStatement = connection.prepareStatement(TEST_SQL );
			super.execute(params);
		} finally {
			if ( preparedStatement != null )
				preparedStatement .close();
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
		ResultSet rs = null;
		try {
			Date sqlDate = new Date ( getParams().getDate().getTime() );
			preparedStatement.setDate(1, sqlDate); 							// irpf_regularization.effective_date = ? 
			preparedStatement.setString(2, retenedorSalida2011.getNif());	// enterprise_registry.document = ?
			preparedStatement.setString(3, retenidoSalida2011.getNif());	// person_registry.document = ?
			rs = preparedStatement.executeQuery();
			if ( rs.next() ) {
				double savedIrpf = rs.getDouble(SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.IRPF);
				BigDecimal tipoRetencion = retenidoSalida2011.getTipoRetencion();
				double irpf = tipoRetencion != null ? tipoRetencion.doubleValue() : 0.00;
				if ( ! testEquals(savedIrpf, irpf, 0.001) ) {
					ErrorMessage errorMessage = 
						new ErrorMessage(String.format("Tipo de retención diferente. Guardado %f , calculada %f", savedIrpf, irpf ));
					errorMessage.setContractId(rs.getInt(SQLConstants.CONTRACT + "." + ContractColumns.ID ));
					errorMessage.setEmployeeName(retenidoSalida2011.getApellidosNombre());
					errorMessage.setEnterpriseName(retenedorSalida2011.getApellidosNombre());
					onMessage(errorMessage);
				}
				
			}
			else {
				WarnMessage warnMessage = 
					new WarnMessage("No existen resultados de calculo de I.R.P.F para este trabajador.");
				//warnMessage.setContractId(rs.getInt(SQLConstants.CONTRACT + "." + ContractColumns.ID));
				warnMessage.setEmployeeName(retenidoSalida2011.getApellidosNombre());
				warnMessage.setEnterpriseName(retenedorSalida2011.getApellidosNombre());
				onMessage(warnMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: handle exception
		} 
		finally  {
			
			if ( rs != null ) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public boolean testEquals(double expected, double actual, double delta) {
		return ( Math.abs(expected - actual) <= delta);
	}
	
	private static String TEST_SQL = "SELECT * "
		+" FROM contract "
		+" LEFT JOIN irpf_result ON ( contract.id = irpf_result.contract"
		+									" AND irpf_result.effective_date = ? )"
		+", registry AS person_registry" 
		+", workplace"
		+", enterprise"
		+", registry AS enterprise_registry " 
		+" WHERE contract.person = person_registry.id"
		+" AND contract.workplace = workplace.id"				
		+" AND workplace.enterprise = enterprise.registry"		
		+" AND enterprise.registry = enterprise_registry.id"
		+" AND enterprise_registry.document = ? "
		+" AND person_registry.document = ?";

	
}
