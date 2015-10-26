package com.code.aon.ui.admin.controller;

import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;

import java.util.List;
import java.util.Vector;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jooq.Result;

import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.jooq.tables.records.PayMethodRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;


public class ConexFlowController extends BasicController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	boolean showConexFlowWindow;
	String server;
	String serverAck;
	String user;
	boolean conexFlow;
	Integer payMethod;
	
	public ConexFlowController() {
		conexFlow = getConexFlowAux();
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		if(server == null) server = "";
		this.server = server;
	}

	public String getServerAck() {
		return serverAck;
	}

	public void setServerAck(String serverAck) {
		if(serverAck == null) serverAck = "";
		this.serverAck = serverAck;
	}

	public boolean getConexFlowAux(){
		AONContext ctx = null;
		try {
			String domainName = AonUtil.getDomainName();
			Integer domainId = AonUtil.getAuthPrincipal().getDomainId();
			ctx = AONContext.getAONContext(domainName, domainId);
			ApplicationParameter aux1 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			return (aux1 != null);
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public boolean isConexFlow() {
		return conexFlow;
	}

	public void setConexFlow(boolean conexFlow) {
		this.conexFlow = conexFlow;
	}

	public boolean isShowConexFlowWindow() {
		return showConexFlowWindow;
	}

	public void setShowConexFlowWindow(boolean showConexFlowWindow) {
		this.showConexFlowWindow = showConexFlowWindow;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		if(user == null) user = "";
		this.user = user;
	}
	
	

	public Integer getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}


	public void onConexFlowShow(){
		AONContext ctx = null;
		try {
			Domain domain = new Domain();
			domain.setName(AonUtil.getDomainName());
			domain.setId(AonUtil.getAuthPrincipal().getDomainId());
			ctx = AONContext.getAONContext(domain.getName(), domain.getId());
			ApplicationParameter aux1 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			setServer(aux1 != null ? aux1.getValue() : "");			
			ApplicationParameter aux2 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
			setServerAck(aux2 != null ? aux2.getValue() : "");
			ApplicationParameter aux3 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_USER);
			setUser(aux3 != null ? aux3.getValue() : "");
			//setPayMethods(payMethods(domain));
			ApplicationParameter aux4 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_PAYMETHOD);	
			Integer i;
			Integer j = 0;
			if(aux4 != null){
				i = Integer.parseInt(aux4.getValue());
				while(!getPayMethods().get(j).getValue().equals(i)) j++;
			}
			setPayMethod((Integer)getPayMethods().get(j).getValue());
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public List<SelectItem> getPayMethods() {
	
			Domain domain = new Domain();
			domain.setName(AonUtil.getDomainName());
			domain.setId(AonUtil.getAuthPrincipal().getDomainId());
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId());

				Result<PayMethodRecord> data = ctx.getDslContext().select()
														.from(PAY_METHOD)
														.where(PAY_METHOD.DOMAIN.eq(domain.getId()))
														.and(PAY_METHOD.TYPE.eq((byte) PayMethodType.CREDIT_CARD.ordinal())
																.or(PAY_METHOD.TYPE.eq((byte) PayMethodType.DEBIT_CARD.ordinal())))
														.fetchInto(PAY_METHOD);
		
				List<SelectItem> list = new Vector<SelectItem>();
				for (PayMethodRecord paymethod : data) {
					SelectItem si = new SelectItem(paymethod.getId(), paymethod.getName());
					list.add(si);
				}
				return list;
			}finally {
				if (ctx != null) ctx.close();
			}
		
	}
	
	public void onSaveConexFlow(ActionEvent event){
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_PARAM, getServer());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM, getServerAck());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_USER, getUser());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_PAYMETHOD, getPayMethod().toString());
	}
	
	public void onChangeConexFlowStatus(ActionEvent event) {

		if(isConexFlow()){
			onConexFlowShow();
			setShowConexFlowWindow(true);
		}
		else{
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_USER);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_PAYMETHOD);
		}	
	}
	
	public void onClose(ActionEvent event){
		setShowConexFlowWindow(false);
		setConexFlow(getConexFlowAux());
	}
}