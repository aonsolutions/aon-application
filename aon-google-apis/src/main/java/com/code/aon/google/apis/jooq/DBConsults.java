package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.CommercialTracking.COMMERCIAL_TRACKING;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainGserviceaccount.DOMAIN_GSERVICEACCOUNT;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.SQLConstants;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;


public class DBConsults {
	
	public static Domain getDomain(String domainCon, Integer domainId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domainCon);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record2<String, String>> data = dslContext.select(DOMAIN.NAME, DOMAIN.DESCRIPTION)
				.from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId))
				.fetch();

			Domain domain = new Domain();
			domain.setId(domainId);
			if(data.get(0).value1()!= null)
				domain.setName(data.get(0).value1());
			if(data.get(0).value2()!= null)
				domain.setDescription(data.get(0).value2());
						
			return domain;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Domain getDomain(String domainCon) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domainCon);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record4<Integer, String, String, Byte>> data = dslContext.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION, DOMAIN.TYPE)
				.from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainCon))
				.fetch();

			Domain domain = new Domain();
			if(data.get(0).value1()!= null)
			domain.setId(data.get(0).value1());
			if(data.get(0).value2()!= null)
				domain.setName(data.get(0).value2());
			if(data.get(0).value3()!= null)
				domain.setDescription(data.get(0).value3());
			if(data.get(0).value4() != null){
				domain.setType(data.get(0).value4().shortValue());
			}
			return domain;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static DomainGserviceaccount getServiceAccount(String domain, Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String>> data = dslContext
					.select(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET
							, DOMAIN.NAME , DOMAIN_GSERVICEACCOUNT.DOMAIN
							, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, DOMAIN_GSERVICEACCOUNT.LIMIT
							, DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY
							, DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
					.from(DOMAIN_GSERVICEACCOUNT)
					.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(domainId))
					.fetch();
			
			Result<Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String>> dataParent = dslContext
					.select(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET
							, DOMAIN.NAME , DOMAIN_GSERVICEACCOUNT.DOMAIN
							, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, DOMAIN_GSERVICEACCOUNT.LIMIT
							, DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY
							, DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
					.from(DOMAIN_GSERVICEACCOUNT).join(DOMAIN).on(DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN))
					.where(DOMAIN.ID.eq(domainId))
					.fetch();
			
			Result<Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String>> dataDefault = dslContext
					.select(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET
							, DOMAIN.NAME , DOMAIN_GSERVICEACCOUNT.DOMAIN
							, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, DOMAIN_GSERVICEACCOUNT.LIMIT
							, DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY
							, DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
					.from(DOMAIN_GSERVICEACCOUNT)
					.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(0))
					.fetch();
			
			/*Result<Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String>> data = dslContext
					.select(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET
							, DOMAIN.NAME , DOMAIN_GSERVICEACCOUNT.DOMAIN
							, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, DOMAIN_GSERVICEACCOUNT.LIMIT
							, DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY
							, DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
					.from(DOMAIN_GSERVICEACCOUNT)
						.join(DOMAIN).on(DOMAIN.ID.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN).or(DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.ID.eq(domainId).or(DOMAIN.ID.eq(0)))
					.fetch();
			*/
			Integer size = data.size();
			Boolean b = false;
			DomainGserviceaccount dgserviceaccount = new DomainGserviceaccount();
			for(Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String> r : data){
				dgserviceaccount = new DomainGserviceaccount(r);
				b= true;
				/*if(size == 1 && r.value4() == 0) 
					dgserviceaccount = new DomainGserviceaccount(r);
				if(size == 2 && r.value4() != 0)
					dgserviceaccount = new DomainGserviceaccount(r);
				if(size == 3 && r.value4() == domainId)
					dgserviceaccount = new DomainGserviceaccount(r);	*/
			};
			for(Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String> r : data){
				if(!b){
					dgserviceaccount = new DomainGserviceaccount(r);
					b= true;
				}

			};
			for(Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String> r : data){
				if(!b){
					dgserviceaccount = new DomainGserviceaccount(r);
					b= true;
				}
			};
			
			return dgserviceaccount;
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	private String getUserName(String email, String domain)
			throws AonConnectionException, SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> username = dslContext
					.select(USER.LOGIN)
					.from(USER)
					.join(MAIL_ACCOUNT)
					.on(USER.ID.eq(MAIL_ACCOUNT.USER_ID))
					.join(DOMAIN)
					.on(DOMAIN.ID.eq(USER.DOMAIN))
					.where(MAIL_ACCOUNT.EMAIL.eq(email).and(
							DOMAIN.NAME.eq(domain))).fetch();

			String a = username.format();
			return a;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static Result<Record3<Integer, String, String>> getCategory(
			String domain) throws AonConnectionException, SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record3<Integer, String, String>> category = dslContext
					.select(CATEGORY.ID, CATEGORY.NAME, CATEGORY.DESCRIPTION)
					.from(CATEGORY)
					.join(DOMAIN)
					.on(CATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.fetch();

			return category;
		} finally {
			if (connection != null)
				connection.close();
		}

	}
	
	public static Result<Record3<Integer, String, String>> getCategoryAux(
			String domain) throws AonConnectionException, SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record3<Integer, String, String>> category = dslContext
					.select(CATEGORY.ID, CATEGORY.NAME, CATEGORY.DESCRIPTION)
					.from(CATEGORY)
					.join(DOMAIN)
					.on(CATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.ID.in(dslContext.select(DOMAIN.PARENT)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.fetch();

			return category;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static Result<Record1<String>> getCategoryName(int id, String domain)
			throws AonConnectionException, SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> category = dslContext.select(CATEGORY.NAME)
					.from(CATEGORY).where(CATEGORY.ID.eq(id)).fetch();

			return category;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	/**************************** ATTACHS 
	 * @throws SQLException *****************************/
	//REGISTRY ATTACH
	public static Vector<FileInfo> getRAttachFilesInDrive(String domain,Vector<FileInfo> attachs) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record5<Integer, Byte, String, Byte, String>> RAttach = dslContext
					.select(RATTACH.ID,
							RATTACH.MIMETYPE,
							RATTACH.DESCRIPTION,
							RATTACH.TYPE,RATTACH.DRIVE_ID)
					.from(RATTACH)
					.join(DOMAIN)
					.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(RATTACH.DATA.isNull())
					.fetch();
			
			for (Record5<Integer, Byte, String, Byte, String> record5 : RAttach) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("registry");
				fileInfo.setFileId(record5.value1());
				fileInfo.setMimetype(record5.value2());
				fileInfo.setTitle(record5.value3());
				fileInfo.setType(record5.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(record5.value5());
				attachs.add(fileInfo);
			}
			return attachs;
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobRAttach(byte[] bs,String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(RATTACH)
			.set(RATTACH.DATA,bs)
			.where(RATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFileRAttach(String domain, String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(RATTACH)
				.where(RATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteRAttachTags(String domain,String drive_id, Integer id) throws SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			/*Result<Record1<Integer>> data =dslContext.select(RATTACH.ID)
				.from(RATTACH)
				.where(RATTACH.DRIVE_ID.eq(drive_id)).fetch();
			*/
			dslContext.delete(RATTACH_TAG)
				.where(RATTACH_TAG.RATTACH.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getRAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(RATTACH.DRIVE_ID)
				.from(RATTACH)
				.where(RATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	//CONTRACT ATTACH
	public static Vector<FileInfo> getContractAttach(String domain,
			Vector<FileInfo> attachs) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record6<Integer, Byte, String, Byte, String, Integer>> contract_attach = dslContext
					.select(CONTRACT_ATTACH.ID, CONTRACT_ATTACH.MIMETYPE,
							CONTRACT_ATTACH.DESCRIPTION, CONTRACT_ATTACH.TYPE
							,CONTRACT_ATTACH.DRIVEID, CONTRACT_ATTACH.DOMAIN)
					.from(CONTRACT_ATTACH)
					.join(DOMAIN)
					.on(CONTRACT_ATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(CONTRACT_ATTACH.DATA.isNotNull())
					.fetch();

			for (Record6<Integer, Byte, String, Byte, String, Integer> r : contract_attach) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("contract");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(r.value5());
				fileInfo.setDomainId(r.value6());
				Domain d = DBConsults.getDomain(domain, r.value6());
				fileInfo.setDomain(d.getName());
				attachs.add(fileInfo);
			}
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static FileInfo getDataContractAttach(String domain,
			FileInfo fileInfo) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<byte[]>> data = dslContext
					.select(CONTRACT_ATTACH.DATA)
					.from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static FileInfo getEmailsContractAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			int num= 4;
			
			
			Result<Record1<String>> email = dslContext
					.select(RMEDIA.VALUE)
					.from(CONTRACT_ATTACH)
					.join(CONTRACT).on(CONTRACT_ATTACH.CONTRACT.eq(CONTRACT.ID))
					.join(RMEDIA).on(RMEDIA.REGISTRY.eq(CONTRACT.PERSON))
					.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId()).and(RMEDIA.MEDIA.eq((byte) num)))
					.fetch();
			
			Vector<String> mails = new Vector<String>();
			for (Record1<String> record1 : email) {
				mails.add(record1.value1());
			}
			fileInfo.setEmails(mails);
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static void setDriveIdContractAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
						
			dslContext.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveIdContractAttach(String domain,String id, Integer fileId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			String sql = "UPDATE contract_attach SET driveId = NULL WHERE id = "+fileId+";";
			dslContext.fetch(sql);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteBlobContractAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			byte[] aux =null;
			dslContext.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.DATA,aux)
			.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobContractAttach(byte[] bs,String domain,String driveId, Integer  id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.DATA,bs)
			.where(CONTRACT_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFileContractAttach(String domain, String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getContractAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(CONTRACT_ATTACH.DRIVEID)
				.from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	//ITEM ATTACH
	public static Vector<FileInfo> getIattach(String domain,
			Vector<FileInfo> attachs) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record6<Integer, Byte, String, Byte, String, Integer>> attach = dslContext
					.select(IATTACH.ID, IATTACH.MIMETYPE, IATTACH.DESCRIPTION,
							IATTACH.TYPE,IATTACH.DRIVEID, IATTACH.DOMAIN)
					.from(IATTACH)
					.join(DOMAIN)
					.on(IATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(IATTACH.DATA.isNotNull())
					.fetch();

			for (Record6<Integer, Byte, String, Byte, String, Integer> r : attach) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("item");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(r.value5());
				fileInfo.setDomainId(r.value6());
				Domain d = DBConsults.getDomain(domain, r.value6());
				fileInfo.setDomain(d.getName());
				attachs.add(fileInfo);
			}
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static FileInfo getDataIattach(String domain,
			FileInfo fileInfo) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<byte[]>> data = dslContext
					.select(IATTACH.DATA)
					.from(IATTACH)
					.where(IATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void setDriveIdIattach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
						
			dslContext.update(IATTACH)
			.set(IATTACH.DRIVEID,fileInfo.getDriveId())
			.where(IATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveIdIattach(String domain,String id, Integer fileId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			String sql = "UPDATE iattach SET driveId = NULL WHERE id = "+fileId+";";
			dslContext.fetch(sql);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteBlobIattach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			byte[] aux =null;
			dslContext.update(IATTACH)
			.set(IATTACH.DATA,aux)
			.where(IATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobIAttach(byte[] bs,String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(IATTACH)
			.set(IATTACH.DATA,bs)
			.where(IATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFileIAttach(String domain, String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(IATTACH)
				.where(IATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getIAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(IATTACH.DRIVEID)
				.from(IATTACH)
				.where(IATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	//INVOICE ATTACH
	public static Vector<FileInfo> getInvoiceAttach(String domain,
			Vector<FileInfo> attachs) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record6<Integer, Byte, String, Byte, String, Integer>> attach = dslContext
					.select(INVOICE_ATTACH.ID, INVOICE_ATTACH.MIMETYPE,
							INVOICE_ATTACH.DESCRIPTION, INVOICE_ATTACH.TYPE
							,INVOICE_ATTACH.DRIVEID, INVOICE_ATTACH.DOMAIN)
					.from(INVOICE_ATTACH)
					.join(DOMAIN)
					.on(INVOICE_ATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(INVOICE_ATTACH.DATA.isNotNull())
					.fetch();

			for (Record6<Integer, Byte, String, Byte, String, Integer> r : attach) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("invoice");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				if ( r.value4() != null )
					fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(r.value5());
				fileInfo.setDomainId(r.value6());
				Domain d = DBConsults.getDomain(domain, r.value6());
				fileInfo.setDomain(d.getName());
				attachs.add(fileInfo);
			}
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static FileInfo getDataInvoiceAttach(String domain,
			FileInfo fileInfo) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<byte[]>> data = dslContext
					.select(INVOICE_ATTACH.DATA)
					.from(INVOICE_ATTACH)
					.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static FileInfo getEmailsInvoiceAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			int num= 4;
			
			
			Result<Record1<String>> email = dslContext
					.select(RMEDIA.VALUE)
					.from(INVOICE_ATTACH)
					.join(INVOICE).on(INVOICE_ATTACH.INVOICE.eq(INVOICE.ID))
					.join(RMEDIA).on(RMEDIA.REGISTRY.eq(INVOICE.REGISTRY))
					.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId()).and(RMEDIA.MEDIA.eq((byte) num)))
					.fetch();
			
			Vector<String> mails = new Vector<String>();
			for (Record1<String> record1 : email) {
				mails.add(record1.value1());
			}
			fileInfo.setEmails(mails);
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}	

	public static void setDriveIdInvoiceAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
						
			dslContext.update(INVOICE_ATTACH)
			.set(INVOICE_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveIdInvoiceAttach(String domain,String id, Integer fileId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			String sql = "UPDATE invoice_attach SET driveId = NULL WHERE id = "+fileId+";";
			dslContext.fetch(sql);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteBlobInvoiceAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			byte[] aux =null;
			dslContext.update(INVOICE_ATTACH)
			.set(INVOICE_ATTACH.DATA,aux)
			.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobInvoiceAttach(byte[] bs,String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(INVOICE_ATTACH)
			.set(INVOICE_ATTACH.DATA,bs)
			.where(INVOICE_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFileInvoiceAttach(String domain, String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(INVOICE_ATTACH)
				.where(INVOICE_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getInvoiceAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(INVOICE_ATTACH.DRIVEID)
				.from(INVOICE_ATTACH)
				.where(INVOICE_ATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	//OFFER ATTACH
	public static Vector<FileInfo> getOfferAttach(String domain,
			Vector<FileInfo> attachs) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record5<Integer, Byte, String, String, Integer>> attach = dslContext
					.select(OFFER_ATTACH.ID, OFFER_ATTACH.MIMETYPE,
							OFFER_ATTACH.DESCRIPTION,OFFER_ATTACH.DRIVEID
							, OFFER_ATTACH.DOMAIN)
					.from(OFFER_ATTACH)
					.join(DOMAIN)
					.on(OFFER_ATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(OFFER_ATTACH.DATA.isNotNull())
					.fetch();

			for (Record5<Integer, Byte, String, String, Integer> r : attach) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("offer");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(r.value4());
				fileInfo.setDomainId(r.value5());
				Domain d = DBConsults.getDomain(domain, r.value5());
				fileInfo.setDomain(d.getName());
				attachs.add(fileInfo);
			}
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static FileInfo getDataOfferAttach(String domain,
			FileInfo fileInfo) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<byte[]>> data = dslContext
					.select(OFFER_ATTACH.DATA)
					.from(OFFER_ATTACH)
					.where(OFFER_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void setDriveIdOfferAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
						
			dslContext.update(OFFER_ATTACH)
			.set(OFFER_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(OFFER_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveIdOfferAttach(String domain,String id, Integer fileId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			String sql = "UPDATE offer_attach SET driveId = NULL WHERE id = "+fileId+";";
			dslContext.fetch(sql);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteBlobOfferAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			byte[] aux =null;
			dslContext.update(OFFER_ATTACH)
			.set(OFFER_ATTACH.DATA,aux)
			.where(OFFER_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobOfferAttach(byte[] bs,String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(OFFER_ATTACH)
			.set(OFFER_ATTACH.DATA,bs)
			.where(OFFER_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFileOfferAttach(String domain, String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(OFFER_ATTACH)
				.where(OFFER_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getOfferAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(OFFER_ATTACH.DRIVEID)
				.from(OFFER_ATTACH)
				.where(OFFER_ATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	//PAYROLL BATCH ATTACH
	public static Vector<FileInfo> getPayrollAttach(String domain,
			Vector<FileInfo> attachs) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record6<Integer, Byte, String, Byte, String, Integer>> attach = dslContext
					.select(PAYROLL_BATCH_ATTACH.ID,
							PAYROLL_BATCH_ATTACH.MIMETYPE,
							PAYROLL_BATCH_ATTACH.DESCRIPTION,
							PAYROLL_BATCH_ATTACH.TYPE,PAYROLL_BATCH_ATTACH.DRIVEID
							,PAYROLL_BATCH_ATTACH.DOMAIN)
					.from(PAYROLL_BATCH_ATTACH)
					.join(DOMAIN)
					.on(PAYROLL_BATCH_ATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(PAYROLL_BATCH_ATTACH.DATA.isNotNull())
					.fetch();

			for (Record6<Integer, Byte, String, Byte, String, Integer> r : attach) {
				if (r.value2()!=null){
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("payroll");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(r.value5());
				fileInfo.setDomainId(r.value6());
				Domain d = DBConsults.getDomain(domain, r.value6());
				fileInfo.setDomain(d.getName());
				attachs.add(fileInfo);
				}
			}
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static FileInfo getDataPayrollAttach(String domain,
			FileInfo fileInfo) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<byte[]>> data = dslContext
					.select(PAYROLL_BATCH_ATTACH.DATA)
					.from(PAYROLL_BATCH_ATTACH)
					.where(PAYROLL_BATCH_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void setDriveIdPayrollAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
						
			dslContext.update(PAYROLL_BATCH_ATTACH)
			.set(PAYROLL_BATCH_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(PAYROLL_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveIdPayrollAttach(String domain,String id, Integer fileId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			String sql = "UPDATE payroll_batch_attach SET driveId = NULL WHERE id = "+fileId+";";
			dslContext.fetch(sql);
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteBlobPayrollAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			byte[] aux =null;
			dslContext.update(PAYROLL_BATCH_ATTACH)
			.set(PAYROLL_BATCH_ATTACH.DATA,aux)
			.where(PAYROLL_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobPayrollAttach(byte[] bs,String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(PAYROLL_BATCH_ATTACH)
			.set(PAYROLL_BATCH_ATTACH.DATA,bs)
			.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFilePayrollAttach(String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(PAYROLL_BATCH_ATTACH)
				.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getPayrollAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(PAYROLL_BATCH_ATTACH.DRIVEID)
				.from(PAYROLL_BATCH_ATTACH)
				.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	//PROJECT ATTACH
	public static Vector<FileInfo> getProjectAttach(String domain,
			Vector<FileInfo> attachs) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record5<Integer, Byte, String, String, Integer>> attach = dslContext
					.select(PROJECT_ATTACH.ID, PROJECT_ATTACH.MIMETYPE,
							PROJECT_ATTACH.DESCRIPTION,PROJECT_ATTACH.DRIVEID, PROJECT_ATTACH.DOMAIN)
					.from(PROJECT_ATTACH)
					.join(DOMAIN)
					.on(PROJECT_ATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(PROJECT_ATTACH.DATA.isNotNull())
					.fetch();

			for (Record5<Integer, Byte, String, String, Integer> r : attach) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("project");
				fileInfo.setFileId(r.value1());
				Byte mimeType = r.value2();
				if ( mimeType != null )
					fileInfo.setMimetype(mimeType);
				fileInfo.setTitle(r.value3());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(r.value4());
				fileInfo.setDomainId(r.value5());
				Domain d = DBConsults.getDomain(domain, r.value5());
				fileInfo.setDomain(d.getName());
				attachs.add(fileInfo);
			}
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static FileInfo getDataProjectAttach(String domain,
			FileInfo fileInfo) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<byte[]>> data = dslContext
					.select(PROJECT_ATTACH.DATA)
					.from(PROJECT_ATTACH)
					.where(PROJECT_ATTACH.ID.eq(fileInfo.getFileId()))
					.and(PROJECT_ATTACH.DATA.isNotNull())
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static FileInfo getEmailsProjectAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			int num= 4;
			
			
			Result<Record1<String>> email = dslContext
					.select(RMEDIA.VALUE)
					.from(PROJECT_ATTACH)
					.join(PROJECT).on(PROJECT_ATTACH.PROJECT.eq(PROJECT.ID))
					.join(RMEDIA).on(RMEDIA.REGISTRY.eq(PROJECT.REGISTRY))
					.where(PROJECT_ATTACH.ID.eq(fileInfo.getFileId()).and(RMEDIA.MEDIA.eq((byte) num)))
					.fetch();
			
			Vector<String> mails = new Vector<String>();
			for (Record1<String> record1 : email) {
				mails.add(record1.value1());
			}
			fileInfo.setEmails(mails);
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}	
	
	public static void setDriveIdProjectAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
						
			dslContext.update(PROJECT_ATTACH)
			.set(PROJECT_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(PROJECT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveIdProjectAttach(String domain,String id, Integer fileId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			String sql = "UPDATE project_attach SET driveId = NULL WHERE id = "+fileId+";";
			dslContext.fetch(sql);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteBlobProjectAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			byte[] aux =null;
			dslContext.update(PROJECT_ATTACH)
			.set(PROJECT_ATTACH.DATA,aux)
			.where(PROJECT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobProjectAttach(byte[] bs,String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(PROJECT_ATTACH)
			.set(PROJECT_ATTACH.DATA,bs)
			.where(PROJECT_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFileProjectAttach(String domain, String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getProjectAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(PROJECT_ATTACH.DRIVEID)
				.from(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	//SEPE BATCH ATTACH
	public static Vector<FileInfo> getSepeAttach(String domain,
			Vector<FileInfo> attachs) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record6<Integer, Byte, String, Byte, String, Integer>> attach = dslContext
					.select(SEPE_BATCH_ATTACH.ID, SEPE_BATCH_ATTACH.MIMETYPE,
							SEPE_BATCH_ATTACH.DESCRIPTION,
							SEPE_BATCH_ATTACH.TYPE ,SEPE_BATCH_ATTACH.DRIVEID 
							, SEPE_BATCH_ATTACH.DOMAIN)
					.from(SEPE_BATCH_ATTACH)
					.join(DOMAIN)
					.on(SEPE_BATCH_ATTACH.DOMAIN.eq(DOMAIN.ID))
					.join(DOMAIN_GSERVICEACCOUNT)
					.on(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(DOMAIN.ID).or(
							DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN)))
					.where(DOMAIN.NAME
							.eq(domain)
							.or(DOMAIN.PARENT.in(dslContext.select(DOMAIN.ID)
									.from(DOMAIN).where(DOMAIN.NAME.eq(domain)))))
					.and(SEPE_BATCH_ATTACH.DATA.isNotNull())
					.fetch();

			for (Record6<Integer, Byte, String, Byte, String, Integer> r : attach) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("sepe");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDriveId(r.value5());
				fileInfo.setDomainId(r.value6());
				Domain d = DBConsults.getDomain(domain, r.value6());
				fileInfo.setDomain(d.getName());
				attachs.add(fileInfo);
			}
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public static FileInfo getDataSepeAttach(String domain,
			FileInfo fileInfo) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<byte[]>> data = dslContext
					.select(SEPE_BATCH_ATTACH.DATA)
					.from(SEPE_BATCH_ATTACH)
					.where(SEPE_BATCH_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void setDriveIdSepeAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
						
			dslContext.update(SEPE_BATCH_ATTACH)
			.set(SEPE_BATCH_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(SEPE_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveIdSepeAttach(String domain,String id, Integer fileId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			String sql = "UPDATE sepe_batch_attach SET driveId = NULL WHERE id = "+fileId+";";
			dslContext.fetch(sql);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteBlobSepeAttach(String domain,FileInfo fileInfo) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			byte[] aux =null;
			dslContext.update(SEPE_BATCH_ATTACH)
			.set(SEPE_BATCH_ATTACH.DATA,aux)
			.where(SEPE_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertBlobSepeAttach(byte[] bs,String domain,String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(SEPE_BATCH_ATTACH)
			.set(SEPE_BATCH_ATTACH.DATA,bs)
			.where(SEPE_BATCH_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteFileSepeAttach(String domain, String driveId, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(SEPE_BATCH_ATTACH)
				.where(SEPE_BATCH_ATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getSepeAttachDriveID(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<String>> data =dslContext.select(SEPE_BATCH_ATTACH.DRIVEID)
				.from(SEPE_BATCH_ATTACH)
				.where(SEPE_BATCH_ATTACH.ID.eq(id)).fetch();
		
			if(data.get(0).value1()!=null) return data.get(0).value1();
			else return "";
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
	
	public static Vector<String> getCommercial(String domain,
			Integer id) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> data = dslContext
					.select(REGISTRY.NAME)
					.from(COMMERCIAL_TRACKING).join(REGISTRY).on(COMMERCIAL_TRACKING.SELLER.eq(REGISTRY.ID))
					.where(COMMERCIAL_TRACKING.ID.eq(id))
					.fetch();
			Vector<String> vector = new Vector<String>();
			for (Record1<String> record1 : data) {
				vector.add(record1.value1());
			}
			return vector;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<String> getParentName(String domain) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> data = dslContext
					.select(DOMAIN.NAME)
					.from(DOMAIN)
					.where(DOMAIN.PARENT.isNull())
					.fetch();
			
			Vector<String> vector = new Vector<String>();
			for (Record1<String> record1 : data) {
				vector.add(record1.value1());
			}
			return vector;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static String getUserName(String domain, Integer id) throws AonConnectionException,
	SQLException {
Connection connection = null;
try {

	connection = DatabaseSync.getConnection(domain);

	DSLContext dslContext = DSL.using(connection,
			JooqSettings.getDefaultSettings());

	Result<Record1<String>> data = dslContext
			.select(REGISTRY.NAME)
			.from(REGISTRY)
			.where(REGISTRY.ID.eq(id))
			.fetch();
	
	String name = new String();
	for (Record1<String> record1 : data) {
		if(record1.value1() != null) name = record1.value1();
	}
	return name;
} finally {
	if (connection != null)
		connection.close();
}
}
	//getParentName(schemas.get(sch)))
	

	public static Vector<FileInfo> getRattach(String domain) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3<Integer, String, Integer>> data = dslContext
					.select(RATTACH.ID,RATTACH.DRIVE_ID,RATTACH.DATA.length())
					.from(RATTACH).join(DOMAIN)
						.on(DOMAIN.ID.eq(RATTACH.DOMAIN))
					.where(DOMAIN.NAME.eq(domain))
					.fetch();
			Vector<FileInfo> v = new Vector<FileInfo>();
 			data.stream().forEach(d->{
				FileInfo fi = new FileInfo();
				if(d.value1() != null) fi.setFileId(d.value1());
				if(d.value2() != null) fi.setDriveId(d.value2());
				if(d.value3() != null) fi.setSize(d.value3());
				v.add(fi);
			});
			
			return v;

		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void upsize(String domain, Integer id , Integer size) throws SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());			
			dslContext.update(RATTACH)
			.set(RATTACH.DPARENT_ID,size.toString())
			.where(RATTACH.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
}
