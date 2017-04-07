package com.code.aon.ui.admin.controller;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.PayMethod;
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

	private static final long serialVersionUID = 1L;

	boolean conexFlow;
	boolean showConexFlowWindow;
	String server;
	String serverAck;
	String user;
	String keyA;
	String keyB;
	PayMethod payMethod;
	
	public ConexFlowController() {
		setConexFlow(getConexFlowAux());
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
	
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = (server != null) ? server : StringUtils.EMPTY;
	}

	public String getServerAck() {
		return serverAck;
	}
	public void setServerAck(String serverAck) {
		this.serverAck = (serverAck != null) ? serverAck : StringUtils.EMPTY;
	}

	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = (user != null) ? user : StringUtils.EMPTY;
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

	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public boolean getConexFlowAux(){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
			ApplicationParameter aux = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			return (aux != null && aux.getValue() != null);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public boolean isShowConexflow(){
		return isPlayasol() && isHotel();
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

	public void onConexFlowShow(){
		AONContext ctx = null;
		try {
			Domain domain = new Domain();
			domain.setName(AonUtil.getDomainName());
			domain.setId(AonUtil.getAuthPrincipal().getDomainId());
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), AonUtil.getRemoteUser());

			ApplicationParameter aux1 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			setServer((aux1 != null && aux1.getValue() != null) ? aux1.getValue() : "");
			ApplicationParameter aux2 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
			setServerAck((aux2 != null && aux2.getValue() != null) ? aux2.getValue() : "");
			ApplicationParameter aux3 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_USER);
			setUser((aux3 != null && aux3.getValue() != null) ? aux3.getValue() : "");
			ApplicationParameter aux4 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_KEY_A);
			setKeyA((aux4 != null && aux4.getValue() != null) ? aux4.getValue() : "");
			ApplicationParameter aux5 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_KEY_B);
			setKeyB((aux5 != null && aux5.getValue() != null) ? aux5.getValue() : "");
			ApplicationParameter aux6 = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_PAY_METHOD);
			setPayMethod((aux6 != null && aux6.getValue() != null) ? (PayMethod)BeanManager.getManagerBean(PayMethod.class).get(Integer.parseInt(aux6.getValue())) : null);
		} catch (ManagerBeanException ex) {
			setPayMethod(null);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public void onSaveConexFlow(ActionEvent event){
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_PARAM, getServer());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM, getServerAck());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_USER, getUser());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_A, getKeyA());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_B, getKeyB());
		AppParamUtil.insertParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_PAY_METHOD, getPayMethod().getId());
	}

	public void onChangeConexFlowStatus(ActionEvent event) {
		if (isConexFlow()) {
			onConexFlowShow();
			setShowConexFlowWindow(true);
		}
		else{
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_USER);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_A);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_KEY_B);
			AppParamUtil.removeParameter(com.code.aon.common.enumeration.AppParam.PMS_CONEXFLOW_PAY_METHOD);
			setConexFlow(false);
		}	
	}

	public void onClose(ActionEvent event){
		setShowConexFlowWindow(false);
		setConexFlow(getConexFlowAux());
	}

}