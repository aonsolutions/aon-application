package com.esferalia.aon.payroll.calculator;

import java.sql.SQLException;
import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.jaxb.irpf.sql.SQLAEATRetencionesEntrada2011;
import com.esferalia.aon.payroll.jaxb.irpf.sql.SQLTipoRetenedorEntrada2011;
import com.esferalia.aon.payroll.jaxb.irpf.sql.SQLTipoRetenidoEntrada2011;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;



public class IrpfCalculator  {

	private SQLIrpfBuilder irpfBuilder ;
	private Date date;
	
	public SQLIrpfBuilder getIrpfBuilder() {
		return irpfBuilder;
	}

	public void setIrpfBuilder(SQLIrpfBuilder irpfBuilder) {
		this.irpfBuilder = irpfBuilder;
	}

	public IrpfCalculator(Date date) {
		this.date = date;
	}

	public void calculate ( SQLIrpfCalculatorContext sqlCtx ) 
		throws SalaryException, ExpressionException, SQLException {
			
			SQLAEATRetencionesEntrada2011 entrada = (SQLAEATRetencionesEntrada2011) sqlCtx.getEntrada2011(); 
			
			while ( sqlCtx.next() ) {
				SQLTipoRetenidoEntrada2011 retenido = (SQLTipoRetenidoEntrada2011) ((SQLTipoRetenedorEntrada2011)entrada.getRetenedorList()).getRetenidoList();
				
//				System.out.println(retenido.getApellidosNombre() 
//				+ " - hijos: " 
//				+ sqlCtx.getDescendantCount()
//				+ " - comunidad: " 
//				+ sqlCtx.getGeozone()
//				+ " - bruto: " 
//				+ sqlCtx.getGrossSalary()
//				+ " - porcentaje: " 
//				+ sqlCtx.getPercent()
//				);
				
				irpfBuilder.createNewContractData();
				irpfBuilder.setContractId(sqlCtx.getContractId());
				irpfBuilder.setStartDate(date);
				irpfBuilder.setEndDate(null);
				irpfBuilder.setName(ContractVariables.IRPF_PERCENT.getName());
				irpfBuilder.setExpression(String.valueOf(CommonUtil.round(sqlCtx.getPercent())));
				irpfBuilder.setDocument(retenido.getNif());
				irpfBuilder.setFullName(retenido.getApellidosNombre());
				irpfBuilder.setEnterprise(entrada.getRetenedorList().getApellidosNombre());
				irpfBuilder.setGrossSalary(String.valueOf(CommonUtil.round(sqlCtx.getGrossSalary())));
				irpfBuilder.setOldPercent(sqlCtx.getOldPercent());
				
				irpfBuilder.saveIrpf();
				
			}
	}
	
}
