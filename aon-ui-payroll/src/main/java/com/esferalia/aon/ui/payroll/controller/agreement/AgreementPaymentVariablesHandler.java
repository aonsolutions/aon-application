package com.esferalia.aon.ui.payroll.controller.agreement;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.IVariableData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.ui.payroll.controller.AbstractVariableHandler;

public class AgreementPaymentVariablesHandler extends AbstractVariableHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(AgreementPaymentVariablesHandler.class.getName());

	public AgreementPaymentVariablesHandler(IController controller) {
		super(controller);
	}

	@Override
	public void initializeVariables(ActionEvent event) {
		AgreementPayment payment = (AgreementPayment)getController().getTo();
		Agreement agreement = payment.getAgreement();
		List<IVariableData> dataList;
		try {
			setVariablesModel(null);
			setUndefinedVariablesModel(null);
			if(payment.getExpression()!=null || payment.getPaymentConcept().getExpression()!=null){
				dataList = new LinkedList<IVariableData>();
				Set<String> vl = ExpressionContext.getVariables(payment.getExpression()==null?payment.getPaymentConcept().getExpression():payment.getExpression());
				List<IVariableData> undefined = new LinkedList<IVariableData>();
				if(!vl.isEmpty()){
					for(String s: vl){
						List<ITransferObject> list = existingAgreementData(s, agreement);
						if(!list.isEmpty()){
							for(ITransferObject to: list){
								VariableData data = new VariableData();
								data.setVariableData((IVariableData) to);
								dataList.add(data);
							}
						} else {
							Calendar startCal = Calendar.getInstance();
							Calendar endCal = Calendar.getInstance();
							startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
							endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
							AgreementData data = new AgreementData();
							data.setAgreement(agreement);
							data.setName(s);
							data.setStartDate(startCal.getTime());
							if(!isSystemVariable(data)){
								VariableData d = new VariableData();
								d.setVariableData((IVariableData) data);
								undefined.add(d);
							}
						}
					}
				}
				setVariablesModel(new ListDataModel(dataList));
				if(!undefined.isEmpty()){
					setUndefinedVariablesModel(new ListDataModel(undefined));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private boolean isSystemVariable(AgreementData data) {
		return ContextVariable.getVariableByName(data.getName())!=null;
	}

	@Override
	public List<?> expressionContext(Object suggest) {
		List<String> list = new LinkedList<String>();
		String filter = (String) suggest;
		for (String systemDataVariable :getSystemDataVariables()){
			if (systemDataVariable.startsWith(filter)) {
				list.add(systemDataVariable);
			}
		}
		for (ContextVariable cv :ContextVariable.values() ){
			if (cv.getName().startsWith(filter)) {
				list.add(cv.getName());		
			}
		}
		Collections.sort(list);
		return list;
	}
	
	private List<String> systemDataVariables;
	public void setSystemDataVariables(List<String> systemDataVariables) {
		this.systemDataVariables = systemDataVariables;
	}
	public List<String> getSystemDataVariables() {
		if (systemDataVariables == null) {
			systemDataVariables = new LinkedList<String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
				Criteria criteria = new Criteria();
				
				//TODO ¿Utilizar las fechas del pojo activo?
				Date date = new Date();
				
				String alias = bean.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE);
				Expression ex1 = ExpressionUtilities.getNullExpression(alias);
				Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
				criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to:list) {
					SystemData sd = (SystemData) to;
					systemDataVariables.add(sd.getName());					
				}
			} catch (ManagerBeanException e) {
				// TODO como tratar esto?
			}
		}
		return systemDataVariables;
	}
	
	protected List<ITransferObject> existingAgreementData(String name, Agreement agreement) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AgreementData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_AGREEMENT_ID), agreement.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_NAME), name);
		return bean.getList(criteria);
	}
	@Override
	protected IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return BeanManager.getManagerBean(AgreementData.class);
	}
	@Override
	protected void resetVariable() {
		setData(new VariableData());
		getData().setVariableData(new AgreementData());
		((AgreementData)getData().getVariableData()).setAgreement(((AgreementPayment) getController().getTo()).getAgreement());
	}

}
