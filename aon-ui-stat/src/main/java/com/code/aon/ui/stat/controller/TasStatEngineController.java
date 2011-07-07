package com.code.aon.ui.stat.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class TasStatEngineController {

	private TasStatParams params;
	
	public TasStatParams getParams() {
		return params;
	}

	public String getBeanName() {
		return IStatConstants.TAS_STAT_CONTROLLER_NAME;
	}
	
	public void setParams(TasStatParams params) {
		this.params = params;
	}

	public void onResetOwnerStat(ActionEvent event) {
		onEditSearch(event);
		getParams().setStatType(TasStatType.OWNER_STAT);
	}
	public void onResetTasItemStat(ActionEvent event) {
		onEditSearch(event);
		getParams().setStatType(TasStatType.TAS_ITEM_STAT);
	}
	
	public void onEditSearch(ActionEvent event) {
		try {
			setParams( new TasStatParams() );
			IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
			getParams().setTarget( (Target) targetBean.createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar los parámetros de búsqueda";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
	}
	
	public void onSearch(ActionEvent event) {
		String msg = "Estadística no Implementada.";
		AonUtil.addErrorMessage(msg);
		throw new AbortProcessingException(msg);
	}
	public String action() {
		System.out.println("action");
		return null;
	}
}
