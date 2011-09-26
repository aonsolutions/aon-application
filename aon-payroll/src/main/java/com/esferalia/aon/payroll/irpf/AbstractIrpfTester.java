package com.esferalia.aon.payroll.irpf;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester.UnExpectedValue;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfResultColumns;

public abstract class AbstractIrpfTester implements IrpfCalculator.CallbackHandler {
	
	private static String SQL = "SELECT * "
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
	
	
	private ResultSet 			resultSet;
	private PreparedStatement 	statement;
	
	public AbstractIrpfTester(Connection connection, Date date ) 
	throws SQLException{
		statement = connection.prepareStatement(SQL);
		statement.setDate(1, new java.sql.Date(date.getTime())); // irpf_regularization.effective_date = ? 
	}
	
	
	@Override
	public void onSalida(TipoRetenedorSalida2011 retenedorSalida2011,
			TipoRetenidoSalida2011 retenidoSalida2011) {
		resultSet = null;
		try {
			statement.setString(2, retenedorSalida2011.getNif());	// enterprise_registry.document = ?
			statement.setString(3, retenidoSalida2011.getNif());	// person_registry.document = ?
			resultSet = statement.executeQuery();
			if ( resultSet.next() ) {
				double savedIrpf = getDouble(SQLConstants.IRPF_RESULT, IrpfResultColumns.IRPF);
				BigDecimal tipoRetencion = retenidoSalida2011.getTipoRetencion();
				double irpf = tipoRetencion != null ? tipoRetencion.doubleValue() : 0.00;
				testEquals("Tipo Retencion",savedIrpf, irpf, 0.011);
				resultSet.close();
				resultSet = null;
			}
			else {
			}
		} catch (SQLException e) {
		} 
	}
	
	public void testEquals(String message, double expected, double actual, double delta) {
		if (Double.compare(expected, actual) == 0)
			return;
		if ((Math.abs(expected - actual) > delta)) {
			unExpectedValue(message, expected, actual);
		}
	}
	
	public static class UnExpectedValue extends Error{
		
		private static final long serialVersionUID = -3256215858427420045L;

		public UnExpectedValue(String message) {
			super(message);
		}
	}
	
	
	protected <T> T get(String table, String col) throws SQLException {
		return ( T ) resultSet.getObject( table + "." + col );
	}
	
	protected double getDouble(String table, String col) throws SQLException {
		return resultSet.getDouble( table + "." + col );
	}

	protected <T> T getContract(String col) {
		try {
			return ( T ) get( SQLConstants.CONTRACT, col );
		} catch (SQLException e) {
			return null;
		}
	}

	protected <T> T getIrpfResult(String col) {
		try {
			return ( T ) get( SQLConstants.IRPF_RESULT , col );
		} catch (SQLException e) {
			return null;
		}
	}

	private static void unExpectedValue(String message, double expected,double actual) {
		throw new UnExpectedValue(format(message, expected, actual));
	}

	private static String format(String message, double expected, double actual) {
		return String.format("%s diferente. En la base de datos '%.2f', en el calculado '%.2f'", 
				message, CommonUtil.round(expected), CommonUtil.round(actual));
}
	
}
