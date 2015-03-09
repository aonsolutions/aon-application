package com.code.aon.ui.admin.controller;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainGserviceaccount.DOMAIN_GSERVICEACCOUNT;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.richfaces.event.UploadEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;

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
		String domain = AonUtil.getDomainName();
		Domain d = new Domain();
		try {
			d = DBConsults.getDomain(domain);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return d.getId()!= null && d.getType()==5;
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
	
	private ServiceAccount getSAccount() throws SQLException{
		String domain = AonUtil.getDomainName();
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record8<String, String, Double, Double, String, String, byte[], String>> data;
			data = dslContext
					.selectDistinct(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS,
							DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.LIMIT, 
							DOMAIN.NAME ,DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY,
							DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
					.from(DOMAIN_GSERVICEACCOUNT).join(DOMAIN)
					.on(DOMAIN.ID.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN))
					.where(DOMAIN_GSERVICEACCOUNT.CLIENT_ID.eq(client_id))
					.fetch();
			ServiceAccount aux = new ServiceAccount();
			for (Record8<String, String, Double, Double, String, String, byte[], String> record : data) {
				ServiceAccount sa = new ServiceAccount("", "", "", "", "",
						"", null, "");
				if (record.value1() != null){
					sa.setClient_id(record.value1());
				}
				if (record.value5() != null)
					sa.setDomain(record.value5());
				if (record.value2() != null)
					sa.setEmail_address(record.value2());
				if (record.value3() != null){
					sa.setSize(record.value3().longValue());
					sa.setSizestr(record.value3().longValue());
				}
				if (record.value4() != null){
					sa.setLimit(record.value4().longValue());
					sa.setLimitstr(record.value4().longValue());
				}
				if (record.value6() != null){
					sa.setPublic_key(record.value6());
				}
				if (record.value7() != null){
					sa.setData(record.value7());
				}
				if(record.value8() != null){
					sa.setGoogle_account(record.value8());
				}
				else sa.setGoogle_account("-");
				aux = sa;
				
			}
			return aux;
		} finally {
			if (connection != null)
				connection.close();
		}
		
		
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
	
	public Vector<ServiceAccount> getAccounts() throws SQLException {
	
			vector = new Vector<ServiceAccount>();

			String domain = AonUtil.getDomainName();
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());

				Result<Record8<String, String, Double, Double, String, String, byte[], String>> data;
				data = dslContext
						.selectDistinct(DOMAIN_GSERVICEACCOUNT.CLIENT_ID,
								DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS,
								DOMAIN_GSERVICEACCOUNT.SIZE,
								DOMAIN_GSERVICEACCOUNT.LIMIT, DOMAIN.NAME,DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY,DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY
								,DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
						.from(DOMAIN_GSERVICEACCOUNT).join(DOMAIN)
						.on(DOMAIN.ID.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN))
						.fetch();
				int i=0;
				for (Record8<String, String, Double, Double, String, String, byte[], String> record : data) {
					ServiceAccount sa = new ServiceAccount("", "", "", "", "",
							"", null, "");
					if (record.value1() != null){
						sa.setClient_id(record.value1());
					}
					if (record.value5() != null)
						sa.setDomain(record.value5());
					if (record.value2() != null)
						sa.setEmail_address(record.value2());
					if (record.value3() != null){
						sa.setSize(record.value3().longValue());
						sa.setSizestr(record.value3().longValue());
					}
					if (record.value4() != null){
						sa.setLimit(record.value4().longValue());
						sa.setLimitstr(record.value4().longValue());
					}
					if (record.value6() != null){
						sa.setPublic_key(record.value6());
					}
					if (record.value7() != null){
						sa.setData(record.value7());
					}
					if(record.value8() != null){
						sa.setGoogle_account(record.value8());
					}
					vector.add(sa);
					
				}
				return vector;
			} finally {
				if (connection != null)
					connection.close();
			}
	}

	public Integer getDomainId(String dominio) throws SQLException {

		String domain = AonUtil.getDomainName();
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<Integer>> data;
			data = dslContext
					.select(DOMAIN.ID).from(DOMAIN).where(DOMAIN.NAME.eq(dominio)).fetch();
			Integer id = null;
			for (Record1<Integer> record : data) {
				if(record.value1()!= null) id = record.value1();
			}
			return id;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
	public void updateAccount(ActionEvent event) throws SQLException {

		String domain = AonUtil.getDomainName();
		Connection connection = null;
		try {
			Integer domain_id = getDomainId(getDomain());
			connection = DatabaseSync.getConnection(domain);

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			
			if(getData() != null)
				dslContext.update(DOMAIN_GSERVICEACCOUNT)
					.set(DOMAIN_GSERVICEACCOUNT.DOMAIN, domain_id)
					.set(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, getEmail_address())
					.set(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY, getPublic_key())
					.set(DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, getData())
					.set(DOMAIN_GSERVICEACCOUNT.LIMIT, (double) Integer.parseInt(getLimit()))
					.set(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT, getGoogle_account())
					.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(domain_id))
					.execute();
			else{
				dslContext.update(DOMAIN_GSERVICEACCOUNT)
					.set(DOMAIN_GSERVICEACCOUNT.DOMAIN, domain_id)
					.set(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, getEmail_address())
					.set(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY, getPublic_key())
					.set(DOMAIN_GSERVICEACCOUNT.LIMIT, (double) Integer.parseInt(getLimit()))
					.set(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT, getGoogle_account())
					.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(domain_id))
					.execute();
			}
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public void deleteAccount(ActionEvent event) throws SQLException {

		String domain = AonUtil.getDomainName();
		Connection connection = null;
		try {
			Integer domain_id = getDomainId(getDomain());
			connection = DatabaseSync.getConnection(domain);

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			dslContext.delete(DOMAIN_GSERVICEACCOUNT).where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(domain_id)).execute();

		} finally {
			if (connection != null)
				connection.close();
		}
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
		
		System.out.println("asdgasd");
		String domain = AonUtil.getDomainName();
		Connection connection = null;
		try {

			Integer domain_id = getDomainId(getDomain());
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			dslContext
					.insertInto(DOMAIN_GSERVICEACCOUNT,
							DOMAIN_GSERVICEACCOUNT.CLIENT_ID,
							DOMAIN_GSERVICEACCOUNT.DOMAIN,
							DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS,
							DOMAIN_GSERVICEACCOUNT.LIMIT,
							DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY,
							DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY,
							DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
					.values(getClient_id(), domain_id, getEmail_address(), (double) Integer.parseInt(getLimit()),
							getData(),getPublic_key(),getGoogle_account()).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void main(String[] args) throws SQLException {
		Vector<ServiceAccount> vector = null;// getAccounts();
		System.out.println("Holaaaaa");
		System.out.println(vector);
		for (ServiceAccount serviceAccount : vector) {
			System.out.println(serviceAccount.domain);
		}
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
		System.out.println("onUpload");
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