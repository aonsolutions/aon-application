package com.code.aon.ui.admin.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Vector;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.richfaces.event.UploadEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class ServiceAccountController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	Boolean console;
	String aux_client_id;
	String client_id;
	String domain;
	String email_address;
	String size;
	String limit;
	String public_key;
	String google_account;
	byte[] data;
	
	public Boolean getConsole(){
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		Domain domain = AON.getDomain(domainName, domainId, AonUtil.getRemoteUser());		
		return domain.getId()!= null && domain.getDomainType().equals(DomainType.ADMIN);
	}	
	
	public void initialize2(ActionEvent event){
		 aux_client_id="";
		 client_id="";
		 domain="";
		 email_address="";
		 size="";
		 limit="";
		 public_key="";
		 google_account="";
		
	}
	
	private ServiceAccount getSAccount(){
		DomainGserviceaccount sa = AON.getDomainGserviceaccount(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "",
				f-> f.getClientIdProperty().eq(client_id));
		Domain domain = AON.getDomain(AonUtil.getDomainName(), sa.getDomainId(), "");
		return new ServiceAccount(sa.getClientId(), domain.getName(), sa.getEmailAddress(), sa.getSize().toString(), sa.getLimit().toString(), sa.getPublicKey(), sa.getPrivateKey(), sa.getGoogleAccount());
	}
	
	public String getAux_client_id() throws SQLException {

		return aux_client_id;
	}

	public void setAux_client_id(String aux_client_id) throws SQLException {
		
		
		
		this.aux_client_id = aux_client_id;
	}

	static Vector<ServiceAccount> vector = null;
	String beanName;
	
	private ServiceAccount upload; 
	
	String[] clients;
	
	public void initialize(){
		vector= null;
	}
	
	public String[] getClients(){
		return clients;
	}
	
	public Vector<ServiceAccount> getAccounts(){
		LinkedList<DomainGserviceaccount> dgsa =  AON.getDomainGserviceaccountList(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		vector = new Vector<ServiceAccount>();
		
		for (DomainGserviceaccount sa : dgsa) {
			Domain domain = AON.getDomain(AonUtil.getDomainName(), sa.getDomainId(), "");
			vector.add(new ServiceAccount(sa.getClientId(), domain.getName(), sa.getEmailAddress(), sa.getSize().toString(), sa.getLimit().toString(), sa.getPublicKey(), sa.getPrivateKey(), sa.getGoogleAccount()));
		}
		return vector;
	}

	
	
	
	public void updateAccount(ActionEvent event) throws SQLException {
		Integer domainId = DBConsults.getDomainId(getDomain(), DomainManager.getCurrentDomain(), "");
		Domain domain = AON.getDomain(getDomain(), domainId, AonUtil.getRemoteUser());
		DomainGserviceaccount dgsa = new DomainGserviceaccount()
				.setDomain(domain)
				.setEmailAddress(getEmail_address())
				.setPrivateKey(getData())
				.setPublicKey(getPublic_key())
				.setLimit(Double.parseDouble(getLimit()))
				.setGoogleAccount(getGoogle_account());
		AON.updateDomainGserviceaccount(getDomain(), domainId, "", dgsa);
	}
	
	public void deleteAccount(ActionEvent event) throws SQLException {
		Integer domainId = DBConsults.getDomainId(getDomain(), DomainManager.getCurrentDomain(), "");
		AON.deleteDomainGserviceaccount(getDomain(), domainId, "");
	}
	
	public void auxiliar() throws SQLException{
		ServiceAccount sa = getSAccount();
		client_id = sa.getClient_id();
		domain = sa.getDomain();
		email_address = sa.getEmail_address();
		size = sa.getSize();
		limit = sa.getLimit();
		public_key =  sa.getPublic_key();
		data = sa.getData();
		google_account = sa.getGoogle_account();
	}
	
	
	public void createAccount(ActionEvent event) throws SQLException {
		Integer domainId = DBConsults.getDomainId(getDomain(), DomainManager.getCurrentDomain(), "");
		Domain domain = AON.getDomain(getDomain(), domainId, AonUtil.getRemoteUser());
		DomainGserviceaccount dgsa = new DomainGserviceaccount()
				.setClientId(getClient_id())
				.setDomain(domain)
				.setEmailAddress(getEmail_address())
				.setPrivateKey(getData())
				.setPublicKey(getPublic_key())
				.setLimit(Double.parseDouble(getLimit()))
				.setGoogleAccount(getGoogle_account());
		AON.insertDomainGserviceaccount(getDomain(), domainId, "", dgsa);
	}

	public static void main(String[] args) throws SQLException {
		
	}
	
	AonFile aonFile;
	
	public void setAonFile(AonFile aonFile) {
		if ( this.aonFile != null ) {
			this.aonFile.clean();	
		}
		this.aonFile = aonFile;
	}
	
	public void fileUploaded(UploadEvent event) throws IOException {
		
		setAonFile(AttachmentUtil.fileUploaded(event));
		
		Utils.InputStreamToByte(aonFile.openStream());
		data =	Utils.InputStreamToByte(aonFile.openStream());
		//setData(f);
		//upload.setData(event.getUploadItem().getData());
		
	}

	public ServiceAccount getUpload() {
		return upload;
	}
	
	public void setUpload(ServiceAccount upload) {
		this.upload = upload;
	}
	
	public void onUpload(ActionEvent event) {
		upload = new ServiceAccount(getClient_id(), getDomain(), getEmail_address(), getSize(), getLimit(), getPublic_key(), getData(),getGoogle_account());
	}
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getPublic_key() {
		return public_key;
	}
	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getClient_id() {

		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getEmail_address() {
		return email_address;
	}
	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}
	public String getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = FileUtils.byteCountToDisplaySize(size);
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(Long limit) {
		this.limit = FileUtils.byteCountToDisplaySize(limit);
	}

	public String getGoogle_account() {
		return google_account;
	}

	public void setGoogle_account(String google_account) {
		this.google_account = google_account;
	}
	
	
}