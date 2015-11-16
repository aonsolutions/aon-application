package com.code.aon.ui.admin.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DomainType;
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
	String keyA;
	String keyB;
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
	
	public String getKeyA() {
		return keyA;
	}

	public void setKeyA(String keyA) {
		this.keyA = keyA;
	}

	public String getKeyB() {
		return keyB;
	}

	public void setKeyB(String keyB) {
		this.keyB = keyB;
	}

	public boolean getConexFlowAux(){
		AONContext ctx = null;
		try {
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			ctx = AONContext.getAONContext(domainName, domainId, AonUtil.getRemoteUser());
			ApplicationParameter aux1 = AppParamDAO.fetchOne(ctx,
					AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			return (aux1 != null && aux1.getValue() != null && !aux1.getValue()
					.equals("Null"));
		} finally {
			if (ctx != null)
				ctx.close();
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

	public boolean isHotel(){
		Integer domainId = DomainManager.getCurrentDomain();
		String domainName = AonUtil.getDomainName();
		String user = AonUtil.getRemoteUser();
		Domain domain = AON.getDomain(domainName, domainId, user);
		return domain.getDomainType().equals(DomainType.HOTEL);
	}
	
	public boolean isPlayasol(){
		String domainName = AonUtil.getDomainName();
		return domainName.contains("playasol");
	}	
	
	public boolean isShowConexflow(){
		return isPlayasol() && isHotel();
	}
	
	public void onConexFlowShow(){
		AONContext ctx = null;
		try {
			Domain domain = new Domain();
			domain.setName(AonUtil.getDomainName());
			domain.setId(AonUtil.getAuthPrincipal().getDomainId());
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), AonUtil.getRemoteUser());
			ApplicationParameter aux1 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			setServer((aux1 != null && !aux1.getValue().equals("Null")) ? aux1.getValue() : "");			
			ApplicationParameter aux2 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
			setServerAck((aux2 != null && !aux2.getValue().equals("Null")) ? aux2.getValue() : "");
			ApplicationParameter aux3 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_USER);
			setUser((aux3 != null && !aux3.getValue().equals("Null")) ? aux3.getValue() : "");
			
			ApplicationParameter aux4 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_KEY_A);
			setKeyA((aux4 != null && !aux4.getValue().equals("Null")) ? aux4.getValue() : "");
			ApplicationParameter aux5 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_KEY_B);
			setKeyB((aux5 != null && !aux5.getValue().equals("Null")) ? aux5.getValue() : "");

		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	
	public void onSaveConexFlow(ActionEvent event){
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_PARAM, getServer());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM, getServerAck());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_USER, getUser());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_A, "");
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_B, "");

	}
	
	public void onChangeConexFlowStatus(ActionEvent event) {

		if(isConexFlow()){
			onConexFlowShow();
			setShowConexFlowWindow(true);
		}
		else{
			String nullString = "Null";
			AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_PARAM, nullString);
			AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM, nullString);
			AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_USER, nullString);
			AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_A, nullString);
			AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_B, nullString);
		}	
	}
	
	public void onClose(ActionEvent event){
		setShowConexFlowWindow(false);
		setConexFlow(getConexFlowAux());
	}
}