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
	
	private static final String DISCONNECTED = "Desconectado";
	private static final String CONNECTED = "Conectado";
	
	boolean showTediCenterWindow;
	boolean showTediCenterSnapshotWindow;

	public boolean accepted;
	public boolean active;
	public String status = DISCONNECTED;
	private String token;
	public String email;
	public String password;

	public boolean snapshotAccepted;
	public boolean snapshotActive;
	public String snapshotStatus = DISCONNECTED;
	private String snapshotToken;
	public String snapshotEmail;
	public String snapshotPassword;
	

	public boolean isShowTediCenterWindow() {
		return showTediCenterWindow;
	}
	public void setShowTediCenterWindow(boolean showTediCenterWindow) {
		this.showTediCenterWindow = showTediCenterWindow;
	}
	
	public boolean isShowTediCenterSnapshotWindow() {
		return showTediCenterSnapshotWindow;
	}
	public void setShowTediCenterSnapshotWindow(boolean showTediCenterSnapshotWindow) {
		this.showTediCenterSnapshotWindow = showTediCenterSnapshotWindow;
	}
	
	public void onInit( ActionEvent event ) {
		init();
	}	
	
	public void init() {
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		
		if (domain.getId() == 0) {
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
			ApplicationParameter apEmail = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.TEDI_EMAIL.getValue());
			if(hasToken()) {
				setStatus(CONNECTED + " - " + apEmail.getValue());
			}
			if(apEmail.getValue() != null) {
				setEmail(apEmail.getValue());
			}
		
			DataResponse dsr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
					f -> f.getCodeProperty().eq(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
					.and(f.getDomainProperty().eq(domain.getId())));
			
			if(dsr != null && dsr.getId() != null) {
				DataResponseDetail drd = AON.getDataResponseDetail(domain.getName(), domain.getId(), "", 
					f -> f.getDataResponseProperty().eq(dsr.getId())
						.and(f.getDataVariableProperty().eq(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())))
					.orElse(new DataResponseDetail());
			
				setSnapshotToken(drd.getDataValue());
			}
			ApplicationParameter apSnapshotEmail = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.TEDI_SNAPSHOT_EMAIL.getValue());
			if(hasSnapshotToken()) {
				setSnapshotStatus(CONNECTED + " - " + apSnapshotEmail.getValue());
			}
			if(apSnapshotEmail.getValue() != null) {
				setSnapshotEmail(apSnapshotEmail.getValue());
			}
		} else {
			ApplicationParameter apActive = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.TEDI_ACTIVE.getValue());
			setAccepted("1".equals(apActive.getValue()));
			setActive("1".equals(apActive.getValue()));	
		}
	}
	
	public void update(ActionEvent event) {
		
	}
	
	public void connect(ActionEvent event){
		Tedi tedi = Tedi.login(getEmail(), getPassword());
		setToken(tedi.getToken());
		setStatus(CONNECTED + " - " + getEmail());
		if(DomainManager.getCurrentDomain() == 0) {
			save();
		}
	}
	
	public void disconnect(ActionEvent event){
		setStatus(DISCONNECTED);
		setActive(false);
		setToken(null);
		setPassword("");
		if(DomainManager.getCurrentDomain() == 0) {
			save();
		}
	}
	
	public void save() {
		if(hasToken()) {
			Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
			DataResponse dr = new DataResponse()
				.setCode(AppParam.TEDI_TOKEN.getValue())
				.setDomain(domain.getId())
				.setSource(DataResponseSource.TEDI_INVOICE)
				.setSourceId(0);

			dr = AON.insertDataResponse(domain.getName(), domain.getId(), "", dr);
			DataResponseDetail dtd = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataVariable(AppParam.TEDI_TOKEN.getValue())
				.setDataValue(getToken())
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
			setAccepted(true);
		} else {
			Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
			
			DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
					f -> f.getCodeProperty().eq(AppParam.TEDI_TOKEN.getValue())
					.and(f.getDomainProperty().eq(domain.getId())));
			if(dr != null && dr.getId() != null) {
				AON.deleteDataResponseDetail(domain.getName(), domain.getId(), "", f -> f.getDataResponseProperty().eq(dr.getId()));
				AON.deleteDataResponse(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(dr.getId()));
			}
			setAccepted(false);
		}
	}
	
	public void contract() {

		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		ApplicationParameter apActive = new ApplicationParameter()
			.setDomain(domain.getId())
			.setName(AppParam.TEDI_ACTIVE.getValue())
			.setValue(isActive() ? "1" : "0");
		AON.deleteApplicationParameter(domain.getName(), domain.getId(), "", 
			f -> f.getDomainProperty().eq(domain.getId())
			.and(f.getNameProperty().eq(AppParam.TEDI_ACTIVE.getValue())));
		AON.insertApplicationParameter(domain.getName(), domain.getId(), "", apActive);
		setAccepted(isActive());
	}

	public Boolean hasToken(){
		return this.token != null;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public Boolean isActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public Boolean getAccepted() {
		return accepted;
	}
	
	public Boolean isAccepted() {
		return accepted;
	}
	
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
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
		return snapshotActive;
	}
	
	public Boolean isSnapshotActive() {
		return snapshotActive;
	}
	
	public void setSnapshotActive(Boolean snapshotActive) {
		this.snapshotActive = snapshotActive;
	}
	
	public Boolean getSnapshotAccepted() {
		return snapshotAccepted;
	}
	
	public Boolean isSnapshotAccepted() {
		return snapshotAccepted;
	}
	
	public void setSnapshotAccepted(Boolean snapshotAccepted) {
		this.snapshotAccepted = snapshotAccepted;
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
		Tedi tedi = Tedi.login(getSnapshotEmail(), getSnapshotPassword(),true);
		setSnapshotToken(tedi.getToken());
		setSnapshotStatus(CONNECTED + " - " + getSnapshotEmail());
		if(DomainManager.getCurrentDomain() == 0) {
			snapshotSave();;
		}
	}

	public void snapshotDisconnect(ActionEvent event){
		setSnapshotStatus(DISCONNECTED);
		setSnapshotActive(false);
		setSnapshotToken(null);
		setSnapshotPassword("");
		if(DomainManager.getCurrentDomain() == 0) {
			snapshotSave();;
		}
	}
	
	public void snapshotSave() {
		if(hasSnapshotToken()) {
			Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		
			DataResponse dr = new DataResponse()
				.setCode(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
				.setDomain(domain.getId())
				.setSource(DataResponseSource.TEDI_INVOICE)
				.setSourceId(0);

			dr = AON.insertDataResponse(domain.getName(), domain.getId(), "", dr);
			DataResponseDetail dtd = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataVariable(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
				.setDataValue(getSnapshotToken())
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
			setSnapshotAccepted(true);
		} else {
			Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
			
			DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
					f -> f.getCodeProperty().eq(AppParam.TEDI_SNAPSHOT_TOKEN.getValue())
					.and(f.getDomainProperty().eq(domain.getId())));
			if(dr != null && dr.getId() != null) {
				AON.deleteDataResponseDetail(domain.getName(), domain.getId(), "", f -> f.getDataResponseProperty().eq(dr.getId()));
				AON.deleteDataResponse(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(dr.getId()));
			}
			setSnapshotAccepted(false);
		}
	}
	
	public void onChangeTediCenterStatus(ActionEvent event) {
		if (isActive()) {
			setShowTediCenterWindow(true);
		}
		else{
			setActive(!DISCONNECTED.equals(snapshotStatus));
			//Borrar datos - Desconectar!!
		}	
	}
	
	public void onChangeTediCenterSnapshotStatus(ActionEvent event) {
		if (isSnapshotActive()) {
			setShowTediCenterSnapshotWindow(true);
		}
		else{
			setSnapshotActive(!DISCONNECTED.equals(snapshotStatus));
			//Borrar datos - Desconectar!!
		}	
	}
	
	public void onClose(ActionEvent event){
		setActive(!DISCONNECTED.equals(status));
		setShowTediCenterWindow(false);
	}

	public void onSnapshotClose(ActionEvent event){
		setSnapshotActive(!DISCONNECTED.equals(snapshotStatus));
		setShowTediCenterSnapshotWindow(false);
	}
	
}