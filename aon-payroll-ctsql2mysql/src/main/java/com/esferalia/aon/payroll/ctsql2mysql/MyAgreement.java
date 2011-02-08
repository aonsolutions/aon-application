package com.esferalia.aon.payroll.ctsql2mysql;

import static com.code.aon.employee.calculator.ContractSalaryCalculator.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Categoria;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Convenio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nivel;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percniv;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class MyAgreement extends DefaultCtsqlDBVisitor {
	
	private static final Integer SYSTEM_AGREEMENT = 0;
	

	private DefaultMysqlDB mysqlDB;
	
	int level;
	int agreement;

	private Map<String, Integer>	levels;
	private Map<String, Integer>	agreements;
	

	public MyAgreement(DefaultMysqlDB mysqlDB) {
		this.mysqlDB = mysqlDB;
		this.levels = new HashMap<String, Integer>();
		this.agreements = new HashMap<String, Integer>();
	}
	
	
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitConvenio(this);
	}
	

	public Integer getAgreement(String oldCdg) {
		return agreements.get(oldCdg);
	}
	
	@Override
	public void visitConvenio(Convenio convenio) throws SQLException {
		
		String description = convenio.getDescripcion();
		this.agreement = mysqlDB.insertAgreement(null,	//TODO: ¿ Calendar ?  
				description);
		agreements.put(convenio.getCdg(), this.agreement);
		
		convenio.visitRel_niv_con(this);
		convenio.visitRel_cat_con(this);
	}
	
	@Override
	public void visitRel_niv_con(Nivel nivel, Convenio convenio)
			throws SQLException {
		this.level =  
			mysqlDB.insertAgreement_level(this.agreement, nivel.getCdg());
		levels.put(nivel.getCdg(), this.level);
		
		nivel.visitPercniv_nivel(this);
	}
	
	@Override
	public void visitRel_cat_con(Categoria categoria, Convenio convenio)
			throws SQLException {
		Integer level = levels.get(categoria.getNivel() );
		if ( level == null ) {
			mysqlDB.error("categoria[{}]: Not found nivel retributivo {} ", 
					categoria.getCdg(), categoria.getNivel() );
			return;
		}
		String description = categoria.getDescripcion();
		mysqlDB.insertAgreement_level_category(level, description);
	}
	
	@Override
	public void visitPercniv_nivel(Percniv percniv, Nivel nivel)
			throws SQLException {
		
		String description = percniv.getDescom();
		
		PaymentType paymetType = 
			mysqlDB.getPaymentType( description, 
								percniv.getDinesp(), 
								percniv.getTipcot());
		
		String function = mysqlDB.getFunction(percniv.getImporte(), 
				percniv.getImpuni(), 
				percniv.getUnidades());
		
		mysqlDB.insertAgreement_level_payment(
				this.level, 
				DefaultMysqlDB.enum2short(paymetType), 
				function, 
				description);
	}
}
