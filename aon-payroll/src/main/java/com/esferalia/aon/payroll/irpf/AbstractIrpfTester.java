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
				Integer resultId = getIrpfResult(IrpfResultColumns.ID);
				if ( resultId == null ) {
					throw new NotFoundException(this);
				}
				
				double savedIrpf = getDouble(SQLConstants.IRPF_RESULT, IrpfResultColumns.IRPF);
				BigDecimal tipoRetencion = retenidoSalida2011.getTipoRetencion();
				double calculatedIrpf = tipoRetencion != null ? tipoRetencion.doubleValue() : 0.00;
				if ( !testEquals(savedIrpf, calculatedIrpf, 0.011) ) {
					throw new UnExpectedValue(this, 
							format("Tipo Retencion", 
									savedIrpf, 
									calculatedIrpf));
				}
			}
		} catch (SQLException e) {
		} 
		finally {
			try {
				if ( resultSet != null )
					resultSet.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public boolean testEquals(double expected, double actual, double delta) {
		if (Double.compare(expected, actual) == 0)
			return true;
		return (Math.abs(expected - actual) <= delta);
	}
	
	public static class UnExpectedValue extends Error{
		
		private Date 	contractEnd;
		private Integer contractId;
		private double 	annualRemuneration;

		public UnExpectedValue(AbstractIrpfTester tester, String message) 
		throws SQLException{
			super(message);
			this.contractId = tester.getContract(ContractColumns.ID);
			this.contractEnd = tester.getContract(ContractColumns.END_DATE);
			this.annualRemuneration = tester.getIrpfResult(IrpfResultColumns.ANNUAL_REMUNERATION);
		}
		
		public Integer getContractId() {
			return contractId;
		}
		
		public Date getContractEnd() {
			return contractEnd;
		}
		
		public double getAnnualRemuneration() {
			return annualRemuneration;
		}
	}
	
	public static class NotFoundException extends Error{
		
		private Integer contractId;

		public NotFoundException(AbstractIrpfTester tester) {
			super();
			this.contractId = tester.getContract(ContractColumns.ID);
		}
		
		public Integer getContractId() {
			return contractId;
		}
	}
	
	private <T> T get(String table, String col) throws SQLException {
		return ( T ) resultSet.getObject( table + "." + col );
	}
	
	private double getDouble(String table, String col) throws SQLException {
		return resultSet.getDouble( table + "." + col );
	}

	private <T> T getContract(String col) {
		try {
			return ( T ) get( SQLConstants.CONTRACT, col );
		} catch (SQLException e) {
			return null;
		}
	}

	private <T> T getIrpfResult(String col) {
		try {
			return ( T ) get( SQLConstants.IRPF_RESULT , col );
		} catch (SQLException e) {
			return null;
		}
	}


	private static String format(String message, double expected, double actual) {
		return String.format("%s diferente. En la base de datos '%.2f', en el calculado '%.2f'", 
				message, CommonUtil.round(expected), CommonUtil.round(actual));
}
	
}
