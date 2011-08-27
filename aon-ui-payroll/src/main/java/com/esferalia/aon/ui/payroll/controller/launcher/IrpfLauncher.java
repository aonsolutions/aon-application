package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.calculator.IrpfCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfBuilder;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.launcher.ListIrpfBuilderListener.LogMessage;



public class IrpfLauncher extends AbstractIrpfLauncher {
	

	private Integer contractId;
	private String irpf;
	private SQLIrpfBuilder irpfBuilder;
	private Object message;
	
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}	
	public String getIrpf() {
		return irpf;
	}
	public void setIrpf(String irpf) {
		this.irpf = irpf;
	}


	
	@Override
	protected void execute(IrpfLauncherParams params)
			throws SalaryException {
		try {

			irpfBuilder = new SQLIrpfBuilder(getConnection());
			listener = new ListIrpfBuilderListener(irpfBuilder);
			listener.setDebugEnabled(isDebugEnabled());
			listener.setSaveLog(isSaveLog());
			irpfBuilder.setListener(listener);
			IrpfCalculator calculator = new IrpfCalculator(params.getDate());
			calculator.setIrpfBuilder(irpfBuilder);
			
			String msg = MessageFormat.format("Cálculo de IRPF {0}:{1}",new Object[] {params.getDate(), params.getDate()});
			listener.onInfo(msg);
			
			calculate(calculator);
			
			if(isSaveEnabled()){
				try {
					irpfBuilder.commit();
				} catch (Throwable e) {
					listener.onError(e.getLocalizedMessage());
					irpfBuilder.rollback();
				}
				msg = MessageFormat.format("Total variables insertadas: {0} ",new Object[]{irpfBuilder.getInsertedContractData()});
			} else {
				irpfBuilder.rollback();
				msg = MessageFormat.format("Total variables calculadas: {0} ",new Object[]{irpfBuilder.getInsertedContractData()});
			}
			listener.onInfo(msg);
		} catch (ExpressionException e) {
			throw new SalaryException(e);
		} catch (SQLException e) {
			throw new SalaryException(e);
		}
	}
	
	public void onIrpfUpdate(ActionEvent event) {
		try {
			if(updateIrpf()){
				ListIrpfBuilderListener.LogMessage msg = (LogMessage) getMessage();
				msg.getLevel();
				msg.setLevel(SalaryBuilderListenerLevel.INFO);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al actualizar el irpf.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private boolean updateIrpf() throws ManagerBeanException{
		IManagerBean dataBean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contractId);
		criteria.addEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME), ContractVariables.IRPF_PERCENT.getName());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE), getParams().getDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
		criteria.addOrder(dataBean.getFieldName(IPayrollAlias.CONTRACT_DATA_START_DATE), false);
		List<ITransferObject> list = dataBean.getList(criteria);
		ContractData existingData = (ContractData) list.get(0);
		if(!existingData.getExpression().equals(irpf)){
			// se crea el nuevo irpf
			ContractData data = new ContractData();
			data.setContract(existingData.getContract());
			data.setStartDate(getParams().getDate());
			data.setEndDate(null);
			data.setName(ContractVariables.IRPF_PERCENT.getName());
			data.setExpression(irpf);
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
	
	public void onShowContract(ActionEvent event) {
		try {
			ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
			controller.onEditSearch(event);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(controller.getManagerBean().getFieldName(IPayrollAlias.CONTRACT_ID),getContractId());
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(event);
			controller.setBackAction("irpfLauncher_form");
		} catch (ManagerBeanException e) {
			String msg = "Error al navegar al contrato.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

}
