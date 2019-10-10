package com.code.aon.ui.admin.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
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
	
	private static final String DISCONNECTED = "Desconectado";
	private static final String CONNECTED = "Conectado";
	
	public boolean active = false;
	public String status = DISCONNECTED;
	private String token;
	public String email;
	public String password;
	
	public boolean snapshotActive = false;
	public String snapshotStatus = DISCONNECTED;
	private String snapshotToken;
	public String snapshotEmail;
	public String snapshotPassword;
	
	public void onInit( ActionEvent event ) {
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		boolean betaUser = ds.isBetaUser();
		
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
		active = apActive.getValue() != null && "1".equals(apActive.getValue());
		if(hasToken()) {
			setStatus(CONNECTED + " - " + apEmail.getValue());
		}
		if(apEmail.getValue() != null) {
			setEmail(apEmail.getValue());
		}
		if (betaUser) {
			DataResponse dsr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
					f -> f.getCodeProperty().eq(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
					.and(f.getDomainProperty().eq(domain.getId())));
			
			if(dsr != null && dsr.getId() != null) {
				DataResponseDetail drd = AON.getDataResponseDetail(domain.getName(), domain.getId(), "", 
					f -> f.getDataResponseProperty().eq(dsr.getId())
						.and(f.getDataVariableProperty().eq(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())))
					.orElse(new DataResponseDetail());
			
				setToken(drd.getDataValue());
			}
			ApplicationParameter apSnapshotActive = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.TEDI_SNAPSHOT_ACTIVE.getValue());
			ApplicationParameter apSnapshotEmail = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.TEDI_SNAPSHOT_EMAIL.getValue());
			snapshotActive = apSnapshotActive.getValue() != null && "1".equals(apSnapshotActive.getValue());
			if(hasToken()) {
				setStatus(CONNECTED + " - " + apSnapshotEmail.getValue());
			}
			if(apSnapshotEmail.getValue() != null) {
				setEmail(apSnapshotEmail.getValue());
			}
		}
	}	
	
	public void update(ActionEvent event) {
	}
	
	public void connect(ActionEvent event){
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		Tedi tedi = Tedi.login(getEmail(), getPassword());
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
		setStatus(CONNECTED + " - " + apEmail.getValue());
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
		setStatus(DISCONNECTED);
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
	
	public Boolean hasSnapshotToken(){
		return this.snapshotToken != null;
	}
	
	public Boolean getSnapshotActive() {
		return this.snapshotActive;
	}
	
	public void setSnapshotActive(Boolean snapshotActive) {
		this.snapshotActive = snapshotActive;
	}
	
	public String getSnapshotToken() {
		return this.snapshotToken;
	}
	
	public void setSnapshotToken(String token) {
		this.snapshotToken = token;
	}
	
	public String getSnapshotStatus() {
		return this.snapshotStatus;
	}
	
	public void setSnapshotStatus(String status) {
		this.snapshotStatus = status;
	}
	
	public String getSnapshotEmail() {
		return this.snapshotEmail;
	}
	public void setSnapshotEmail(String email) {
		this.snapshotEmail = email;
	}
	
	public String getSnapshotPassword() {
		return this.snapshotPassword;
	}
	
	public void setSnapshotPassword(String password) {
		this.snapshotPassword = password;
	}

	public void snapshotConnect(ActionEvent event){
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		Tedi tedi = Tedi.login(getSnapshotEmail(), getSnapshotPassword(),true);
		setSnapshotToken(tedi.getToken());
		DataResponse dr = new DataResponse()
				.setCode(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
				.setDomain(domain.getId())
				.setSource(DataResponseSource.TEDI_INVOICE)
				.setSourceId(0);

		dr = AON.insertDataResponse(domain.getName(), domain.getId(), "", dr);
		DataResponseDetail dtd = new DataResponseDetail()
			.setDomain(domain.getId())
			.setDataVariable(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
			.setDataValue(tedi.getToken())
			.setDataResponse(dr.getId());
		dtd = AON.insertDataResponseDetail(domain.getName(), domain.getId(), "",dtd);
		ApplicationParameter apEmail = new ApplicationParameter()
				.setDomain(domain.getId())
				.setName(AppParam.TEDI_SNAPSHOT_EMAIL.getValue())
				.setValue(getSnapshotEmail());
		AON.deleteApplicationParameter(domain.getName(), domain.getId(), "", 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getNameProperty().eq(AppParam.TEDI_SNAPSHOT_EMAIL.getValue())));
		AON.insertApplicationParameter(domain.getName(), domain.getId(), "", apEmail);		
		setSnapshotStatus(CONNECTED + " - " + apEmail.getValue());
	}

	public void snapshotDisconnect(ActionEvent event){
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
				f -> f.getCodeProperty().eq(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
				.and(f.getDomainProperty().eq(domain.getId())));
		if(dr != null && dr.getId() != null) {
			AON.deleteDataResponseDetail(domain.getName(), domain.getId(), "", f -> f.getDataResponseProperty().eq(dr.getId()));
			AON.deleteDataResponse(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(dr.getId()));
		}
		setSnapshotStatus(DISCONNECTED);
		setSnapshotToken(null);
		setSnapshotPassword("");
	}
	
}