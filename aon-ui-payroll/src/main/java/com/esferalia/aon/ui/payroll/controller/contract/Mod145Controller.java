package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Mod145;
import com.esferalia.aon.payroll.Mod145Descendients;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class Mod145Controller extends LinesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(Mod145Controller.class.getName());
	private Integer descendientCount;
		
	public Integer getDescendientCount() {
		try {
			Mod145 m = (Mod145) getTo();
			IManagerBean bean = BeanManager.getManagerBean(Mod145Descendients.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.MOD145DESCENDIENTS_MOD145_ID), m.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(list.size()>0){
				descendientCount = list.size();
			} else {
				descendientCount = m.getDescendientCount();
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los descendientes";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
		return descendientCount;
	}

	public void setDescendientCount(Integer descendientCount) {
		this.descendientCount = descendientCount;
		Mod145 m = (Mod145) getTo();
		m.setDescendientCount(descendientCount);
	}

	public void onSelectContract(ActionEvent event){
		IController controller = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		Contract contract = (Contract) controller.getTo();
		try {
			this.getCriteria().addEqualExpression(this.getFieldName(IPayrollAlias.MOD145_CONTRACT_ID), contract.getId());
			this.getCriteria().addOrder(this.getFieldName(IPayrollAlias.MOD145_DATE), false);
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
	}
	
}
