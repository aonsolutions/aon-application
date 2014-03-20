package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.cra.CRA;
import com.esferalia.aon.file.payroll.cra.data.CRE;
import com.esferalia.aon.file.payroll.cra.data.DDE;
import com.esferalia.aon.file.payroll.cra.data.ETI;
import com.esferalia.aon.file.payroll.cra.data.TRB;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.enumeration.ss.T84;

public class CRAWriter {
	
	/* Clave proporcionada por la seguridad social */
	private final Integer SS_KEY = 12345678;
	
	private final String WHITESPACE_1  = " ";
	
	private ETI eti;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

	private Integer year;
	private Month month;
	
	private Date getStartDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month.ordinal(), 1, 0, 0, 0);
		return cal.getTime(); 
	}
	private Date getEndDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month.ordinal(), 1, 23, 59, 59);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createCRA(List<EnterpriseCCC> list, Integer year, Month month) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( list, year, month );
			File file = File.createTempFile("temp", ".CRA");
			FileFiller cra = new CRA(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(cra.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	public ETI createETIRecord(List<EnterpriseCCC> list, Integer year, Month month) throws ManagerBeanException {
		ETI eti = new ETI();
		this.year = year;
		this.month = month;
		
		eti.setClave(SS_KEY);
		eti.setPrueba(WHITESPACE_1);
		for (EnterpriseCCC ccc: list) {
			DDE dde = createDDERecord(ccc);
			if(dde!=null){
				eti.getDdeList().add(dde);
			}
		}
		setEti( eti );
		return getEti();
	}
	
	/**
	 * DDE - Datos De Empresa
	 * 
	 * @param ccc
	 * @return
	 * @throws ManagerBeanException
	 */
	private DDE createDDERecord(EnterpriseCCC ccc) throws  ManagerBeanException {
		DDE dde = new DDE();
		dde.setAnio(year.toString());
		dde.setMes(String.valueOf(month.ordinal()+1));
		dde.setCodigoCuentaCotizacionSeguridadSocial(ccc.getFullCcc());
    	
		List<ITransferObject> list = obtainContracts(ccc);
		if(!list.isEmpty()){
			for(ITransferObject to: list){
				Contract contract = (Contract) to;
				TRB trb = createTRBRecord(contract, dde);
				dde.getTrbList().add(trb);
			}
			return dde;
		}
		return null;
	}
	
	/**
	 * TRB - Datos de TRabajador
	 * 
	 * @param contract
	 * @param dde
	 * @return
	 * @throws ManagerBeanException
	 */
	private TRB createTRBRecord(Contract contract, DDE dde) throws ManagerBeanException {
		TRB trb = new TRB();
		trb.setNaf(contract.getPerson().getSocialSecurityNumber());
		List<ITransferObject> list = obtainContractPayments(contract);
		if(!list.isEmpty()){
			for(ITransferObject to: list){
				CRE cre = createCRERecord((SalaryPayment)to);
				if(cre!=null){
					trb.getCreList().add(cre);
				}
			}
		}
		return trb;
	}
	
	/**
	 * CRE - Conceptos REtributivos
	 * 
	 * @param to
	 * @return
	 */
	private CRE createCRERecord(SalaryPayment payment) {
		// TODO
		T84 paymentType = T84.getEnumByValue(payment.getType().toString().replace("CRA_", ""));
		if(paymentType!=null){
			CRE cre = new CRE();
			cre.setConcepto(paymentType.getCode());
//			Valores posibles: (IndicativoConcepto)
//			E=concepto excluido de la base; 
//			I=concepto incluido de la base.
			cre.setIndicativoConcepto("I");
			cre.setImporte(String.valueOf((int)(CommonUtil.round(payment.getAmount(), 2)*100)));
			cre.setIndicativoTipoActuacion("");
			return cre;
		}
		return null;
	}
	
	
	//////////////////////////
	// AUX
	//////////////////////////
	
	private List<ITransferObject> obtainContracts(EnterpriseCCC ccc) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ENTERPRISE_CCC_ID), ccc.getId());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), getEndDate());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), getStartDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		criteria.addOrder("Contract.person.socialSecurityNumber");
		return bean.getList(criteria);
	}

	private List<ITransferObject> obtainContractPayments(Contract contract) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(SalaryPayment.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression("SalaryPayment.salary.contract.id", contract.getId());
		criteria.addGreaterThanOrEqualExpression("SalaryPayment.salary.startDate", getStartDate());
		criteria.addLessThanOrEqualExpression("SalaryPayment.salary.endDate", getEndDate());
		criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_PAYMENT_TYPE));
		return bean.getList(criteria);
	}
}
