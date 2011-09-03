package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.file.payroll.fan.FAN;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.file.payroll.fan.data.RZS;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class FANWriter {
	
	private ETI eti;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createFAN(List<Enterprise> list ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( list );
			File file = File.createTempFile("XXXXXXXX", ".FAN");
			FileFiller fan = new FAN(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(fan.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private ETI createETIRecord( List<Enterprise> list ) throws ManagerBeanException {
		ETI eti = new ETI();
		// TODO Clave proporcionada por la seguridad social
		Integer clave = 12345678;
		eti.setClave(clave);
		for (Enterprise e: list) {
			EMP emp = createEMPrecord(e);
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(Enterprise enterprise) throws  ManagerBeanException {
		EMP emp = new EMP();
		EnterpriseController ent = (EnterpriseController)FormUtil.getController("enterprise");
		ent.getCriteria().addEqualExpression(ent.getFieldName(ICompanyAlias.ENTERPRISE_ID), enterprise.getId());
		ent.onSearch(null);
		ent.onSelectFirst(null);
		// TODO se ha movido el ccc y activity de company a payroll
//		ent.initMainActiviy();
//		String ccc = ent.getCcc().getCcc();
//		emp.setCodigoCuentaCotizacionSeguridadSocial(ccc);
		String tipo = String.valueOf(enterprise.getRegistry().getType().ordinal());
		if (StringUtils.isBlank(tipo)) {
			tipo = "9";
		}
		emp.setTipo(tipo);
		String pais = null;
		try {
			pais = enterprise.getRegistry().getDefaultAddress().getGeozone().getName();
		} catch (NullPointerException e) {
			pais = "   ";
		}			
		if (StringUtils.isBlank(pais)) {
			pais = "   ";
		}
		emp.setPais(pais);
		emp.setNumero(enterprise.getRegistry().getDocument());
		emp.setCalificador("  ");
//		emp.setCodigoCuentaCotizacionPrincipal(ccc);
		RZS rzs = new RZS();
		rzs.setIndicador("0");
		/*
		TipoAlfabeticoEmpresario
		1 Individual
		2 Colectivo
		3 Sin personalidad jurídica
		4 Entidad u Organismo de las Admones.Públicas
		*/
		rzs.setTipoAlfabeticoEmpresario("1");
		
		
		
		emp.setAnio(2011);
		emp.setDesdeMes(1);
		emp.setHastaMes(1);
		emp.setCalificadorLiquidacion("00");
		emp.setClaseLiquidacion(1);
		
		
		emp.setRzs(rzs);
		for(ITransferObject to: getContracts(enterprise)){
			Contract c = (Contract) to;
			TRA tra = createTRARecord(c);
			emp.getTrabajadores().add(tra);
		}
		return emp;
	}
	
	private TRA createTRARecord(Contract contract) throws ManagerBeanException {
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( contract.getPerson().getSocialSecurityNumber() );
		String tipo = String.valueOf(contract.getPerson().getRegistry().getType()); 
		String pais = contract.getPerson().getRegistry().getDefaultAddress().getGeozone().getName();
		String doc = contract.getPerson().getRegistry().getDocument();
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?"   ":pais; 
		ipf += doc;
		tra.setIpf(ipf);
//		FAB fab = createFABRecord(contract);
//		tra.setFab(fab);
		return tra;
	}

	
	
	private List<Enterprise> getEnterprises(List<Contract> contractList){
		List<Enterprise> list = new LinkedList<Enterprise>();
		for(Contract c: contractList){
			if(!list.contains(c.getWorkPlace().getEnterprise())){
				list.add(c.getWorkPlace().getEnterprise());
			}
		}
		return list;
	}
	
	private List<ITransferObject> getContracts(Enterprise enterprise) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), enterprise.getId());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_START_DATE), new Date());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE), new Date());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
		return bean.getList(criteria);
	}

	
	
}
