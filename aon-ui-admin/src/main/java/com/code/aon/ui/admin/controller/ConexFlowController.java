package com.code.aon.ui.admin.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
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

	public void onConexFlowShow(){
		AONContext ctx = null;
		try {
			String domainName = AonUtil.getDomainName();
			Integer domainId = AonUtil.getAuthPrincipal().getDomainId();
			ctx = AONContext.getAONContext(domainName, domainId);
			ApplicationParameter aux1 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			setServer(aux1 != null ? aux1.getValue() : "");			
			ApplicationParameter aux2 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
			setServerAck(aux2 != null ? aux2.getValue() : "");
			ApplicationParameter aux3 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_USER);
			setUser(aux3 != null ? aux3.getValue() : "");
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public void onSaveConexFlow(ActionEvent event){
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_PARAM, getServer());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM, getServerAck());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_USER, getUser());
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
		}
		
	}
	
	public void onClose(ActionEvent event){
		setShowConexFlowWindow(false);
		setConexFlow(getConexFlowAux());
	}
}