package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.jaxb.irpf.AEATRetencionesEntrada2011;
import com.esferalia.aon.payroll.jaxb.irpf.TipoRetenidoEntrada2011;
import com.esferalia.aon.payroll.jaxb.irpf.TipoRetenidoEntrada2011.Descendiente;
import com.esferalia.aon.payroll.jaxb.irpf.sql.SQLAEATRetencionesEntrada2011;
import com.esferalia.aon.payroll.jaxb.irpf.sql.SQLTipoRetenidoEntrada2011;
import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;


public class SQLIrpfCalculatorContext {
	
	
	public static final String PERSON_REGISTRY = "person_registry";
	public static final String ENTERPRISE_REGISTRY = "enterprise_registry";
	
	public static final String EMBARGO_PAID = "embargo_paid";
	
	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String OPEN_BRACKET = "(";
	public static final String CLOSE_BRACKET = ")";
	
	private static final String MAIN_SQL = "SELECT * "
		+" FROM contract"
		+" LEFT JOIN irpf_data ON (contract.id = irpf_data.contract )"
		+" LEFT JOIN contract_data ON (contract.id = contract_data.contract )"
		+", person"
		+", registry AS " + PERSON_REGISTRY
		+", enterprise"
		+", registry AS " + ENTERPRISE_REGISTRY
		+", workplace"
		+" WHERE contract.person = person.registry"
		+" AND person.registry = person_registry.id"			
		+" AND contract.workplace = workplace.id"				
		+" AND workplace.enterprise = enterprise.registry"		
		+" AND enterprise.registry = enterprise_registry.id"	
////		+" AND workplace.address = raddress.id"					
		+" AND ( irpf_data.end_date is null " 
//		+" OR (irpf_data.start_date >= ? "  
		+" OR irpf_data.end_date >= ?)"
		+" AND contract.start_date <= ? "					 
		+" AND ( contract.end_date  IS NULL"
		+" OR contract.end_date >= ? )"
		+" AND contract_data.name like '" + ContractVariables.IRPF_PERCENT.getName()+"'"
		+" AND ( contract_data.end_date IS NULL"
		+" OR contract_data.end_date >= ? )"
		;
	
	private static final String DESCENDIENTS_SQL =
		"SELECT *"
		+" FROM irpf_data_descendients"
		+" LEFT JOIN  irpf_data ON ( irpf_data_descendients.irpf_data = irpf_data.id )"	
		+" WHERE irpf_data = ? "
//		+" AND start_date <= ? "
//		+" AND ( end_date IS NULL"
//		+" OR end_date >= ? )"
		;
	
	private static final String ASCENDANT_SQL =
		"SELECT *"
		+" FROM irpf_data_ascendants"
		+" LEFT JOIN  irpf_data ON ( irpf_data_ascendants.irpf_data = irpf_data.id )"	
		+" WHERE irpf_data = ? "
		;
	
	private static final String GEOZONE_IRPF_DESCENDIENTS_SQL =
		"SELECT *"
		+" FROM geozone_irpf_descendant"
		+" LEFT JOIN  geozone_irpf ON geozone_irpf = geozone_irpf.id"	
		+" WHERE geozone_irpf.geozone = ? "
		+" AND geozone_irpf.amount >= ? "
		+" AND geozone_irpf_descendant.descendant like ? "
		+" ORDER BY geozone_irpf.amount "
		;
	
	private static final String GEOZONE_IRPF_HANDICAP_SQL =
		"SELECT *"
		+" FROM geozone_irpf_handicap"
		+" LEFT JOIN  geozone_irpf ON geozone_irpf = geozone_irpf.id"	
		+" WHERE geozone_irpf.geozone = ? "
		+" AND geozone_irpf.amount >= ? "
		+" AND geozone_irpf_handicap.handicap like ? "
		+" ORDER BY geozone_irpf.amount "
		;
	
	
	private Criteria criteria;
	private Connection connection;
	private Date date;
	private ResultSet resultSet;  
	private PreparedStatement irpfDataStmt;
	private PreparedStatement descendientsStmt;
	private PreparedStatement ascendantsStmt;
	
	private PreparedStatement irpfDescendientsStmt;
	private PreparedStatement irpfHandicapStmt;
	
	
	private SQLTipoRetenidoEntrada2011.SQLDescendiente sqlDescendients;
	private SQLTipoRetenidoEntrada2011.AscendienteImpl sqlAscendants;
	
	public SQLIrpfCalculatorContext(Connection connection, Date date, Criteria criteria) 
	throws SQLException, ExpressionException {
		this.connection = connection;
		this.date = date;
		this.criteria = criteria;
		
		initResultSet();
//		initDescendientsStmt();
//		initAscendantsStmt();
		initIrpfDescendientsStmt();
		initIrpfHandicapStmt();

		this.sqlDescendients = new SQLTipoRetenidoEntrada2011.SQLDescendiente();
		this.sqlAscendants = new SQLTipoRetenidoEntrada2011.AscendienteImpl();
	}
	
	public Collection<TipoRetenidoEntrada2011.Descendiente> getDescendientes()
			throws AonException {
		try {
			this.sqlDescendients.close();
			int id = getId();
			descendientsStmt.setInt(1, id);
			ResultSet rs = descendientsStmt.executeQuery();
			this.sqlDescendients.setResultSet(rs);
			return (Collection<Descendiente>) this.sqlDescendients.iterator();
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}
	
	public Double getPercent(){
		Double percent = null;
		try {
			irpfDescendientsStmt.setInt(1, getGeozone());
			irpfDescendientsStmt.setDouble(2, getGrossSalary());
			irpfDescendientsStmt.setInt(3, getDescendantCount());
			ResultSet rs1 = irpfDescendientsStmt.executeQuery();
			if(rs1.first()){
				percent = new Double(rs1.getDouble("percent"));
			}
			Integer handicap = getHandicap();
			if(handicap!=null && handicap>0){
				irpfHandicapStmt.setInt(1, getGeozone());
				irpfHandicapStmt.setDouble(2, getGrossSalary());
				irpfHandicapStmt.setInt(3, handicap);
				ResultSet rs2 = irpfDescendientsStmt.executeQuery();
				if(rs2.first()){
					percent += rs2.getDouble("percent");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return percent;
	}
	
	public int getId() {
		return getInt("irpf_data", "id");
	}
	
	public int getContractId() {
		return getInt("contract", "id");
	}
	
	public Integer getGeozone() {
		Integer i = getInt("workplace", "economicAgreement");
		if(i==null){
			return 0;
		}else if(i.equals(0)){
			return 1;
		}else if(i.equals(1)){
			return 20;
		}else if(i.equals(2)){
			return 31;
		}else if(i.equals(3)){
			return 48;
		}
		return 0;
	}
	
	public Integer getDescendantCount() {
		Integer i = getInt("irpf_data", "descendient_count");
		return i==null?0:i;
	}
	
	public Integer getHandicap() {
		Integer i = getInt("irpf_data", "disability_level");
		return i==null?0:i;
	}
	
	public Double getGrossSalary(){
		Double total = new Double(0);
		
		for(Month m: Month.values()){
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			startCal.set(2011, m.ordinal(), 1);
			endCal.set(2011, m.ordinal(), startCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			Criteria criteria = new Criteria();
//			criteria.addEqualExpression("contract.id", getInt("contract", "id"));
//			criteria.addEqualExpression("customer.status", CustomerStatus.ACTIVE);
			criteria.addEqualExpression("customer.status", "ACTIVE");
			criteria.addEqualExpression("person_registry.id", getInt("person_registry", "id"));
			
			try {
				ContractSalaryCalculator calculator = new ContractSalaryCalculator();
				SQLSalaryBuilderTester salaryBuilder = new SQLSalaryBuilderTester(connection);
				calculator.setSalaryBuilder(salaryBuilder);
				
				SQLContractSalaryCalculatorContext sqlCtx = 
					new SQLContractSalaryCalculatorContext(connection, 
						startCal.getTime(), 
						endCal.getTime(),
						endCal.getTime(),
						criteria );
				
				if(sqlCtx.next()){
					calculator.calculate(sqlCtx);
					AbstractSQL.Salary s = salaryBuilder.salary;
					if(s!=null){
						total += s.getTotalLiquid();
					} 
				}
			} catch (ExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SalaryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return total;
	}
	
	
	private void initResultSet() throws SQLException {
		String sql = MAIN_SQL;
		if ( this.criteria != null ){
			sql = CriteriaUtilities.toSQLString(this.criteria, sql);
		}
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setDate(1, toSqlDate( this.date ) );
		stmt.setDate(2, toSqlDate( this.date ) );
		stmt.setDate(3, toSqlDate( this.date ) );
		stmt.setDate(4, toSqlDate( new Date() ) );
//		stmt.setDate(3, toSqlDate(  new Date() ) );
//		stmt.setDate(4, toSqlDate(  new Date() ) );
		resultSet = stmt.executeQuery();
	}
	
	private void initDescendientsStmt() throws SQLException {
		this.descendientsStmt  = this.connection.prepareStatement(DESCENDIENTS_SQL);
		this.descendientsStmt.setInt(1, getId() );
	}
	
	private void initAscendantsStmt()
	throws SQLException {
		this.ascendantsStmt  = 
			this.connection.prepareStatement(ASCENDANT_SQL);
		this.ascendantsStmt.setInt(1, getId() );
	}
	private void initIrpfDescendientsStmt()
	throws SQLException {
		this.irpfDescendientsStmt  = this.connection.prepareStatement(GEOZONE_IRPF_DESCENDIENTS_SQL);
	}
	private void initIrpfHandicapStmt()
	throws SQLException {
		this.irpfHandicapStmt  = this.connection.prepareStatement(GEOZONE_IRPF_HANDICAP_SQL);
	}
	
	public boolean next() throws SQLException, ExpressionException{
		
		boolean next =  this.resultSet.next();
		if ( next ) {
//			initContractExpressionCtx();
		}
		else {
			close();
		}
		return next;
	}
	
	public void close() throws SQLException {
		if ( this.resultSet != null ) {
			this.resultSet.close();
			this.resultSet = null;
		}
		if ( this.descendientsStmt != null ) {
			this.descendientsStmt.close();
			this.descendientsStmt = null;
		}
		if ( this.ascendantsStmt != null ) {
			this.ascendantsStmt.close();
			this.ascendantsStmt = null;
		}
	}
	
	private AEATRetencionesEntrada2011 entrada2011;
	
	public AEATRetencionesEntrada2011 getEntrada2011() {
		if(entrada2011 == null){
			entrada2011 = new SQLAEATRetencionesEntrada2011(resultSet);
		}
		return entrada2011;
	}

	public void setEntrada2011(AEATRetencionesEntrada2011 entrada2011) {
		this.entrada2011 = entrada2011;
	}
	
	private static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime()); 
	}
	
	private Integer getInt(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getInt(tableLabel +"."+columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	

}
