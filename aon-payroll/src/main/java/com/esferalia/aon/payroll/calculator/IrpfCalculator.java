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
				Integer ordinal = retenido.getComunidadAutonoma()!=null?Integer.parseInt(retenido.getComunidadAutonoma()):null;
				
//				if(ordinal==null || Administration.COMMON_TERRITORY.ordinal() == ordinal){
//					calculateCommonPercent();
//				} else if(Administration.ALAVA.ordinal() == ordinal){
//					calculateAlavaPercent(retenido);
//				} else if(Administration.BIZKAIA.ordinal() == ordinal){
//					calculateBizkaiaPercent(retenido);
//				} else if(Administration.GIPUZKOA.ordinal() == ordinal){
//					calculateGipuzkoaPercent(retenido);
//				} else if(Administration.NAVARRA.ordinal() == ordinal){
//					calculateNafarroaPercent(retenido);
//				}
				
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
				irpfBuilder.setContract(sqlCtx.getContractId());
				irpfBuilder.setStartDate(date);
				irpfBuilder.setEndDate(null);
				irpfBuilder.setName(ContractVariables.IRPF_PERCENT.getName());
				irpfBuilder.setExpression(String.valueOf(CommonUtil.round(sqlCtx.getPercent())));
				
				irpfBuilder.saveIrpf();
//				sqlCtx.getDescendantCount();
//				sqlCtx.getGeozone();
//				sqlCtx.getGrossSalary();
//				sqlCtx.getPercent();
				
			}
	}
	
	private void calculateCommonPercent() {
//		ModuloCalculo.procesarFicheroXml("", "", "", "");
//		ModuloCalculo.procesarFicheroXml(arg0, arg1, arg2, arg3);
//		(“entrada.xml”,“errores.xml”,“”,“salida.xml”);
		
	}

	private void calculateAlavaPercent(SQLTipoRetenidoEntrada2011 retenido) {
		String percent = null;
		
//		retenido.getDescendientsCount();
//		if(retenido.getDiscapacidad().getGrado1()!=null){
//			if(retenido.getDiscapacidad().getGrado1().getMovilidadReducida()!=null){
//				
//			}
//		} else if(retenido.getDiscapacidad().getGrado2()!=null){
//			
//		} else {
//			
//		}
	}

	private void calculateBizkaiaPercent(SQLTipoRetenidoEntrada2011 retenido) {
		// TODO Auto-generated method stub
		
	}

	private void calculateGipuzkoaPercent(SQLTipoRetenidoEntrada2011 retenido) {
		// TODO Auto-generated method stub
		
	}

	private void calculateNafarroaPercent(SQLTipoRetenidoEntrada2011 retenido) {
		// TODO Auto-generated method stub
		
	}

}
