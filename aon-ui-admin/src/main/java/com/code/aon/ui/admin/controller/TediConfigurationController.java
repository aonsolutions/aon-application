package com.code.aon.ui.admin.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;

import es.translogia.tedi.baloo.Tedi;

public class TediConfigurationController implements IAdminConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public boolean active = false;
	public String status = "Desconectado";
	private String token;
	
	public String email;
	public String password;
	
	public TediConfigurationController() {

	}
	
	public void onInit( ActionEvent event ) {
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
				f -> f.getCodeProperty().eq(AppParam.TEDI_TOKEN.getValue())
				.and(f.getDomainProperty().eq(domain.getId())));
		
		if(dr != null && dr.getId() != null) {
			DataResponseDetail drd = AON.getDataResponseDetail(domain.getName(), domain.getId(), "", 
				f -> f.getDataResponseProperty().eq(dr.getId())
					.and(f.getDataVariableProperty().eq(AppParam.TEDI_TOKEN.getValue())))
				.orElse(new DataResponseDetail());
		
			setToken(drd.getDataValue());
		}
		
		ApplicationParameter apActive = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.TEDI_ACTIVE.getValue());
		ApplicationParameter apEmail = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.TEDI_EMAIL.getValue());
		
		if(!domain.isParent()) {
			
		}
		active = apActive.getValue() != null && "1".equals(apActive.getValue());
		if(hasToken()) {
			setStatus("Conectado - " + apEmail.getValue());
		}
		if(apEmail.getValue() != null) {
			setEmail(apEmail.getValue());
		}
	}	
	
	public void update(ActionEvent event) {
	}
	
	public void connect(ActionEvent event){
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
//		TediUtils tedi = TediUtils.getInstance(email, password);
		Tedi tedi = Tedi.login(email, password);
		setToken(tedi.getToken());
		
		DataResponse dr = new DataResponse()
				.setCode(AppParam.TEDI_TOKEN.getValue())
				.setDomain(domain.getId())
				.setSource(DataResponseSource.TEDI_INVOICE)
				.setSourceId(0);

		dr = AON.insertDataResponse(domain.getName(), domain.getId(), "", dr);
		
		DataResponseDetail dtd = new DataResponseDetail()
			.setDomain(domain.getId())
			.setDataVariable(AppParam.TEDI_TOKEN.getValue())
			.setDataValue(tedi.getToken())
			.setDataResponse(dr.getId());

		dtd = AON.insertDataResponseDetail(domain.getName(), domain.getId(), "",dtd);
		
		ApplicationParameter apEmail = new ApplicationParameter()
				.setDomain(domain.getId())
				.setName(AppParam.TEDI_EMAIL.getValue())
				.setValue(getEmail());
		
		AON.deleteApplicationParameter(domain.getName(), domain.getId(), "", 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getNameProperty().eq(AppParam.TEDI_EMAIL.getValue())));

		AON.insertApplicationParameter(domain.getName(), domain.getId(), "", apEmail);		
		setStatus("Conectado - " + apEmail.getValue());
	}

	public void disconnect(ActionEvent event){
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
				f -> f.getCodeProperty().eq(AppParam.TEDI_TOKEN.getValue())
				.and(f.getDomainProperty().eq(domain.getId())));
		if(dr != null && dr.getId() != null) {
			AON.deleteDataResponseDetail(domain.getName(), domain.getId(), "", f -> f.getDataResponseProperty().eq(dr.getId()));
			AON.deleteDataResponse(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(dr.getId()));
		}
		setStatus("Desconectado");
		setToken(null);
		setPassword("");
	}

	public Boolean hasToken(){
		return this.token != null;
	}
	
	public Boolean getActive() {
		return this.active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}