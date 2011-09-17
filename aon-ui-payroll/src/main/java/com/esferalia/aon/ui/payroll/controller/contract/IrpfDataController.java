package com.esferalia.aon.ui.payroll.controller.contract;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfDataDescendients;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.IrpfCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.DeductHomeLoan;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class IrpfDataController extends LinesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataController.class.getName());
	
	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
		'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	
	private IrpfDataParams params;
	private DisabilityLevel disabilityLevel;
	private boolean deductHomeLoanAfter;
	private boolean deductHomeLoanBefore;
	
	public IrpfDataParams getParams() {
		if(params==null){
			params = new IrpfDataParams();
		}
		return params;
	}
	public void setParams(IrpfDataParams params) {
		this.params = params;
	}
	public DisabilityLevel getDisabilityLevel() {
		return disabilityLevel;
	}
	public void setDisabilityLevel(DisabilityLevel disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
		if(this.disabilityLevel!=DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE){
			((IrpfData)this.getTo()).setDependence(false);
		}
	}
	public boolean isDeductHomeLoanAfter() {
		if(getTo()!=null){
			deductHomeLoanAfter = ((IrpfData)getTo()).getDeductHomeLoan()==DeductHomeLoan.AFTER_01_01_2001;
		}
		return deductHomeLoanAfter;
	}
	public void setDeductHomeLoanAfter(boolean deductHomeLoanAfter) {
		this.deductHomeLoanAfter = deductHomeLoanAfter;
		if(getTo()!=null){
			((IrpfData)getTo()).setDeductHomeLoan(DeductHomeLoan.AFTER_01_01_2001);
		}
	}
	public boolean isDeductHomeLoanBefore() {
		if(getTo()!=null){
			deductHomeLoanBefore = ((IrpfData)getTo()).getDeductHomeLoan()==DeductHomeLoan.BEFORE_01_01_2001;
		}
		return deductHomeLoanBefore;
	}
	public void setDeductHomeLoanBefore(boolean deductHomeLoanBefore) {
		this.deductHomeLoanBefore = deductHomeLoanBefore;
		if(getTo()!=null){
			((IrpfData)getTo()).setDeductHomeLoan(DeductHomeLoan.BEFORE_01_01_2001);
		}
	}
	
	public boolean isIrpfChanged(){
		if(getParams().getCurrentIrpf()==null || getParams().getNewIrpf()==null){
			return false;
		}
		return !getParams().getCurrentIrpf().equals(getParams().getNewIrpf());
	}
	
	public Integer getDescendientLinesCount() {
		try {
			IrpfData data = (IrpfData) getTo();
			IManagerBean bean = BeanManager.getManagerBean(IrpfDataDescendients.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.IRPF_DATA_DESCENDIENTS_IRPF_DATA_ID), data.getId());
			List<ITransferObject> list = bean.getList(criteria);
			return list.size();
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los descendientes";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public boolean isValidNIF() {
		IrpfData m = (IrpfData) getTo();
		if (m.getSpouseDocument() == null || m.getSpouseDocument().length() == 0) {
			return false;
		}
		char[] doc = m.getSpouseDocument().toCharArray();
		if (doc == null || doc.length == 0) {
			return false;
		}
		doc[0] = (doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!StringUtils.isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}
	
	private SQLIrpfBuilder irpfBuilder;
	private static final String PERSON_ALIAS = "person_registry.id";
	private void refreshIrpfData(){
		setParams(null);
		IrpfData data = (IrpfData) this.getTo();
		try {
			irpfBuilder = new SQLIrpfBuilder(getConnection());
			IrpfCalculator calculator = new IrpfCalculator(new Date());
			calculator.setIrpfBuilder(irpfBuilder);
			SQLIrpfCalculatorContext sqlCtx = new SQLIrpfCalculatorContext(getConnection(),new Date(), getCtxCriteria());
			while ( sqlCtx.next() ) {
				getParams().setGrossSalary(CommonUtil.round(sqlCtx.getGrossSalary()));
				getParams().setCurrentIrpf(Double.parseDouble(sqlCtx.getOldPercent()));
				if(!data.isFiscalExclusion()){
					getParams().setNewIrpf(sqlCtx.getPercent());
				} else {
					getParams().setNewIrpf(0.0);
				}
			}
		} catch (ExpressionException e) {
			String msg = "Error al calcular el irpf";
			LOGGER.error(msg);
		} catch (SQLException e) {
			String msg = "Error al calcular el irpf";
			LOGGER.error(msg);
		}
	}
	protected Connection getConnection(){
		String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		return  HibernateUtil.getSQLConnection(sessionFactory);
	}
	public Criteria getCtxCriteria() {
		Contract contract = (Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo();
		Criteria criteria = new Criteria();
		if (contract.getPerson() != null && contract.getPerson().getId() != null) {
			criteria = criteria == null ? new Criteria() : criteria;
			criteria.addEqualExpression(PERSON_ALIAS, contract.getPerson().getId());
		}
		return criteria;
	}
	
	public void onSelectContract(ActionEvent event){
		IController controller = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		Contract contract = (Contract) controller.getTo();
		try {
			this.getCriteria().addEqualExpression(this.getFieldName(IPayrollAlias.IRPF_DATA_CONTRACT_ID), contract.getId());
			this.getCriteria().addOrder(this.getFieldName(IPayrollAlias.IRPF_DATA_START_DATE), false);
			this.onSearch(event);
			if(this.getRowCount()<=0){
				this.onReset(event);
			} else {
				this.onSelectFirst(event);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el modelo 145";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
		refreshIrpfData();
	}
	
	public void onCalculateIrpf(ActionEvent event){
		refreshIrpfData();
	}
	
	public void onIpdateIrpf(ActionEvent event){
		try {
			getParams().setContractId(((IrpfData)this.getTo()).getContract().getId());
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.DAY_OF_MONTH, 1);
			getParams().setDate(cal.getTime());
			updateIrpf();
			refreshIrpfData();
		} catch (ManagerBeanException e) {
			String msg = "Error al actualizar el irpf";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public boolean updateIrpf() throws ManagerBeanException{
		IManagerBean dataBean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), getParams().getContractId());
		criteria.addEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME), ContractVariables.IRPF_PERCENT.getName());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE), getParams().getDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
		criteria.addOrder(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_START_DATE), false);
		List<ITransferObject> list = dataBean.getList(criteria);
		ContractData existingData = (ContractData) list.get(0);
		if(!existingData.getExpression().equals(getParams().getNewIrpf())){
			// se crea el nuevo irpf
			ContractData data = new ContractData();
			data.setContract(existingData.getContract());
			data.setStartDate(getParams().getDate());
			data.setEndDate(null);
			data.setName(ContractVariables.IRPF_PERCENT.getName());
			data.setExpression(getParams().getNewIrpf().toString());
			// se cierra el irpf anterior
			Calendar cal = Calendar.getInstance();
			cal.setTime(getParams().getDate());
			cal.add(Calendar.DAY_OF_MONTH, -1);
			existingData.setEndDate(cal.getTime());
			// inicio de la transaccion
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				try {
					HibernateUtil.setBeginTransaction(false);
					HibernateUtil.setCloseSession(false);
					HibernateUtil.beginTransaction(sessionName);
					// BEGIN operaciones de la transaccion
					dataBean.update(existingData);
					dataBean.insert(data);
					// FIN operaciones de la transaccion
					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);
				} catch (Exception e) {
					String msg = e.getMessage();
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						msg = "Unable to rollback transaction! (" + msg + ")";
					}
					AonUtil.addErrorMessage(msg);
					return false;
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			} finally {
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
			}
		}
		return true;
	}
	
	public class IrpfDataParams{
		private Integer contractId;
		private Date date;
		private Double grossSalary;
		private Double currentIrpf;
		private Double newIrpf;
		
		public Integer getContractId() {
			return contractId;
		}
		public void setContractId(Integer contractId) {
			this.contractId = contractId;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Double getGrossSalary() {
			return grossSalary;
		}
		public void setGrossSalary(Double grossSalary) {
			this.grossSalary = grossSalary;
		}
		public Double getCurrentIrpf() {
			return currentIrpf;
		}
		public void setCurrentIrpf(Double currentIrpf) {
			this.currentIrpf = currentIrpf;
		}
		public Double getNewIrpf() {
			return newIrpf;
		}
		public void setNewIrpf(Double newIrpf) {
			this.newIrpf = newIrpf;
		}
	}
	
	
	//TODO para la impresion del modelo 145, revisar
	public List<ITransferObject> getDescentant(){
		BasicController controller = (BasicController) FormUtil.getController("irpfDataDescendients");
//		controller.getWrappedList();
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list.addAll(controller.getWrappedList());
		list.add(new IrpfDataDescendients());
		list.add(new IrpfDataDescendients());
		list.add(new IrpfDataDescendients());
		list.add(new IrpfDataDescendients());
		return list;
	}
	
}
