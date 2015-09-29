package com.code.aon.google.apis.jooq;


import static com.code.aon.google.apis.DatabaseSync.getDomains;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainGserviceaccount.DOMAIN_GSERVICEACCOUNT;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Property;

public class DBDrive {
	
	public static Vector<FileInfo> getRAttachLimit(String domain, Integer domainId, Vector<RegistryAttachmentType> rats,Integer firstId ) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Condition condition = RATTACH.TYPE.isNull().or(RATTACH.TYPE.isNotNull());
			if(!rats.isEmpty()){
				condition = RATTACH.TYPE.eq((byte)rats.get(0).ordinal());
				for(RegistryAttachmentType rat : rats){
					if(rats.get(0).ordinal()!= rat.ordinal())
						condition = condition.or(RATTACH.TYPE.eq((byte)rat.ordinal()));
				}
			}
			
			Result<Record6<Integer, Byte, String, Byte, Integer, Integer>> attach = dslContext
					.select(RATTACH.ID, RATTACH.MIMETYPE,
							RATTACH.DESCRIPTION, RATTACH.TYPE
							,RATTACH.CATEGORY,RATTACH.DOMAIN)
					.from(RATTACH)
					.where(RATTACH.DOMAIN.eq(domainId))
							.and(RATTACH.DRIVE_ID.isNull()).and(RATTACH.DATA.isNotNull()).and(condition)
							//.and(RATTACH.TYPE.ne((byte)0)).and(RATTACH.TYPE.ne((byte)15)).and(RATTACH.TYPE.ne((byte)17))
							.and(RATTACH.ID.greaterThan(firstId))
							.orderBy(RATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("registry");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				if (r.value4()!=null) fileInfo.setType(r.value4());
				else fileInfo.setType((short) -1);
				fileInfo.setCategory(r.value5());
				fileInfo.setDomainId(r.value6());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value6());
					fileInfo.setDomain(d.getName());
					byte data[] = DatabaseSync.getFileData(fileInfo.getFileId(),
							domain);
					fileInfo.setData(data);
					
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<FileInfo> getContractAttachLimit(String domain, Integer domainId, Integer firstId) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record5<Integer, Byte, String, Byte, Integer>> attach = dslContext
					.select(CONTRACT_ATTACH.ID, CONTRACT_ATTACH.MIMETYPE,
							CONTRACT_ATTACH.DESCRIPTION, CONTRACT_ATTACH.TYPE
							,CONTRACT_ATTACH.DOMAIN)
					.from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.DOMAIN.eq(domainId))
							.and(CONTRACT_ATTACH.DRIVEID.isNull()).and(CONTRACT_ATTACH.DATA.isNotNull())
							.and(CONTRACT_ATTACH.ID.greaterThan(firstId))
							.orderBy(CONTRACT_ATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("contract");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDomainId(r.value5());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value5());
					fileInfo.setDomain(d.getName());
					fileInfo = DBConsults.getDataContractAttach(domain, fileInfo);
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<FileInfo> getIAttachLimit(String domain, Integer domainId, Integer firstId) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record5<Integer, Byte, String, Byte, Integer>> attach = dslContext
					.select(IATTACH.ID, IATTACH.MIMETYPE,
							IATTACH.DESCRIPTION, IATTACH.TYPE
							,IATTACH.DOMAIN)
					.from(IATTACH)
					.where(IATTACH.DOMAIN.eq(domainId))
							.and(IATTACH.DRIVEID.isNull()).and(IATTACH.DATA.isNotNull())
							.and(IATTACH.ID.greaterThan(firstId))
							.orderBy(IATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("item");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDomainId(r.value5());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value5());
					fileInfo.setDomain(d.getName());
					fileInfo = DBConsults.getDataIattach(domain, fileInfo);
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<FileInfo> getInvoiceAttachLimit(String domain, Integer domainId, Integer firstId) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record5<Integer, Byte, String, Byte, Integer>> attach = dslContext
					.select(INVOICE_ATTACH.ID, INVOICE_ATTACH.MIMETYPE,
							INVOICE_ATTACH.DESCRIPTION, INVOICE_ATTACH.TYPE
							,INVOICE_ATTACH.DOMAIN)
					.from(INVOICE_ATTACH)
					.where(INVOICE_ATTACH.DOMAIN.eq(domainId))
							.and(INVOICE_ATTACH.DRIVEID.isNull()).and(INVOICE_ATTACH.DATA.isNotNull())
							.and(INVOICE_ATTACH.ID.greaterThan(firstId))
							.orderBy(INVOICE_ATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("invoice");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				if(r.value4() != null) fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDomainId(r.value5());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value5());
					fileInfo.setDomain(d.getName());
					fileInfo = DBConsults.getDataInvoiceAttach(domain, fileInfo);
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<FileInfo> getOfferAttachLimit(String domain, Integer domainId, Integer firstId) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record4<Integer, Byte, String, Integer>> attach = dslContext
					.select(OFFER_ATTACH.ID, OFFER_ATTACH.MIMETYPE,
							OFFER_ATTACH.DESCRIPTION,OFFER_ATTACH.DOMAIN)
					.from(OFFER_ATTACH)
					.where(OFFER_ATTACH.DOMAIN.eq(domainId))
							.and(OFFER_ATTACH.DRIVEID.isNull()).and(OFFER_ATTACH.DATA.isNotNull())
							.and(OFFER_ATTACH.ID.greaterThan(firstId))
							.orderBy(OFFER_ATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("offer");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setCategory(-2);
				fileInfo.setDomainId(r.value4());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value4());
					fileInfo.setDomain(d.getName());
					fileInfo = DBConsults.getDataOfferAttach(domain, fileInfo);
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<FileInfo> getPayrollAttachLimit(String domain, Integer domainId, Integer firstId) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record5<Integer, Byte, String, Byte, Integer>> attach = dslContext
					.select(PAYROLL_BATCH_ATTACH.ID, PAYROLL_BATCH_ATTACH.MIMETYPE,
							PAYROLL_BATCH_ATTACH.DESCRIPTION, PAYROLL_BATCH_ATTACH.TYPE
							,PAYROLL_BATCH_ATTACH.DOMAIN)
					.from(PAYROLL_BATCH_ATTACH)
					.where(PAYROLL_BATCH_ATTACH.DOMAIN.eq(domainId))
							.and(PAYROLL_BATCH_ATTACH.DRIVEID.isNull()).and(PAYROLL_BATCH_ATTACH.DATA.isNotNull())
							.and(PAYROLL_BATCH_ATTACH.ID.greaterThan(firstId))
							.orderBy(PAYROLL_BATCH_ATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("payroll");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDomainId(r.value5());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value5());
					fileInfo.setDomain(d.getName());
					fileInfo = DBConsults.getDataPayrollAttach(domain, fileInfo);
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<FileInfo> getProjectAttachLimit(String domain, Integer domainId, Integer firstId) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record4<Integer, Byte, String, Integer>> attach = dslContext
					.select(PROJECT_ATTACH.ID, PROJECT_ATTACH.MIMETYPE,
							PROJECT_ATTACH.DESCRIPTION,PROJECT_ATTACH.DOMAIN)
					.from(PROJECT_ATTACH)
					.where(PROJECT_ATTACH.DOMAIN.eq(domainId))
							.and(PROJECT_ATTACH.DRIVEID.isNull()).and(PROJECT_ATTACH.DATA.isNotNull())
							.and(PROJECT_ATTACH.ID.greaterThan(firstId))
							.orderBy(PROJECT_ATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("project");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setCategory(-2);
				fileInfo.setDomainId(r.value4());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value4());
					fileInfo.setDomain(d.getName());
					fileInfo = DBConsults.getDataProjectAttach(domain, fileInfo);
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<FileInfo> getSepeAttachLimit(String domain, Integer domainId, Integer firstId) throws SQLException{
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record5<Integer, Byte, String, Byte, Integer>> attach = dslContext
					.select(SEPE_BATCH_ATTACH.ID, SEPE_BATCH_ATTACH.MIMETYPE,
							SEPE_BATCH_ATTACH.DESCRIPTION,SEPE_BATCH_ATTACH.TYPE,
							SEPE_BATCH_ATTACH.DOMAIN)
					.from(SEPE_BATCH_ATTACH)
					.where(SEPE_BATCH_ATTACH.DOMAIN.eq(domainId))
							.and(SEPE_BATCH_ATTACH.DRIVEID.isNull()).and(SEPE_BATCH_ATTACH.DATA.isNotNull())
							.and(PROJECT_ATTACH.ID.greaterThan(firstId))
							.orderBy(PROJECT_ATTACH.ID)
							.limit(10)
					.fetch();
	
			Vector<FileInfo> attachs = new Vector<FileInfo>();
			
			attach.stream().forEach(r->{
				FileInfo fileInfo = new FileInfo();
				fileInfo.setAonType("sepe");
				fileInfo.setFileId(r.value1());
				fileInfo.setMimetype(r.value2());
				fileInfo.setTitle(r.value3());
				fileInfo.setType(r.value4());
				fileInfo.setCategory(-2);
				fileInfo.setDomainId(r.value5());
				try { 
					Domain d = DBConsults.getDomain(domain, r.value5());
					fileInfo.setDomain(d.getName());
					fileInfo = DBConsults.getDataSepeAttach(domain, fileInfo);
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
	
	/**********************************************/
	
	public static void updateDriveId(File f, Integer id) throws SQLException{

		String domain = "";
		Map<String, String> domains;
		try {
			domains = getDomains();
			Vector<String> d = new Vector<String>(domains.keySet());
			domain = d.get(0);
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		
			
		
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			String aonType = "";
			for (Property p : f.getProperties()) {
				if(p.getKey().equals("aontype"))
					aonType = p.getValue();
			}
			switch (aonType) {
			case "registry":	
				dslContext.update(RATTACH)
					.set(RATTACH.DRIVE_ID, f.getId())
					.where(RATTACH.ID.eq(id)).execute();
				break;
			case "contract":
				dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DRIVEID, f.getId())
				.where(CONTRACT_ATTACH.ID.eq(id)).execute();
				break;
			case "item":
				dslContext.update(IATTACH)
				.set(IATTACH.DRIVEID, f.getId())
				.where(IATTACH.ID.eq(id)).execute();
				break;
			case "invoice":
				dslContext.update(INVOICE_ATTACH)
				.set(INVOICE_ATTACH.DRIVEID, f.getId())
				.where(INVOICE_ATTACH.ID.eq(id)).execute();
				break;
			case "offer":
				dslContext.update(OFFER_ATTACH)
				.set(OFFER_ATTACH.DRIVEID, f.getId())
				.where(OFFER_ATTACH.ID.eq(id)).execute();
				break;
			case "payroll":
				dslContext.update(PAYROLL_BATCH_ATTACH)
				.set(PAYROLL_BATCH_ATTACH.DRIVEID, f.getId())
				.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).execute();
				break;
			case "project":
				dslContext.update(PROJECT_ATTACH)
				.set(PROJECT_ATTACH.DRIVEID, f.getId())
				.where(PROJECT_ATTACH.ID.eq(id)).execute();
				break;
			case "sepe":
				dslContext.update(SEPE_BATCH_ATTACH)
				.set(SEPE_BATCH_ATTACH.DRIVEID, f.getId())
				.where(SEPE_BATCH_ATTACH.ID.eq(id)).execute();
				break;
			default:
				break;
			}
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	//REGISTRY ATTACH
		public static Vector<FileInfo> getDriveRAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record6<Integer, Byte, String, Byte, String, Integer>> attach = dslContext
							.select(RATTACH.ID,RATTACH.MIMETYPE
									,RATTACH.DESCRIPTION,RATTACH.TYPE
									,RATTACH.DRIVE_ID,RATTACH.CATEGORY)
							.from(RATTACH)
							.where(RATTACH.DOMAIN.eq(domainId)).and(RATTACH.DRIVE_ID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record6<Integer, Byte, String, Byte, String, Integer> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("registry");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setType(r.value4());
					fileInfo.setCategory(r.value6());
					fileInfo.setDriveId(r.value5());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
	
		public static Vector<FileInfo> getRAttach(String domain,
				Vector<FileInfo> attachs) throws SQLException{
		
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());

				Result<Record7<Integer, Byte, String, Byte, String, Integer, Integer>> contract_attach = dslContext
						.select(RATTACH.ID, RATTACH.MIMETYPE,
								RATTACH.DESCRIPTION, RATTACH.TYPE,RATTACH.DRIVE_ID
								,RATTACH.CATEGORY,RATTACH.DOMAIN)
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
						.and(RATTACH.DATA.isNotNull())
						.fetch();

				for (Record7<Integer, Byte, String, Byte, String, Integer, Integer> r : contract_attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("registry");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setType(r.value4());
					fileInfo.setCategory(r.value6());
					fileInfo.setDriveId(r.value5());
					fileInfo.setDomainId(r.value7());
					Domain d = DBConsults.getDomain(domain, r.value7());
					fileInfo.setDomain(d.getName());
					attachs.add(fileInfo);
					
				}
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
	
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
		
		public static Integer getRAttachDomainID(String domain, Integer id) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				
				Result<Record1<Integer>> data =dslContext.select(RATTACH.DOMAIN)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return 0;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
	//CONTRACT ATTACH
		public static Vector<FileInfo> getDriveContractAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record5<Integer, Byte, String, Byte, String>> attach = dslContext
							.select(CONTRACT_ATTACH.ID,CONTRACT_ATTACH.MIMETYPE
									,CONTRACT_ATTACH.DESCRIPTION,CONTRACT_ATTACH.TYPE
									,CONTRACT_ATTACH.DRIVEID)
							.from(CONTRACT_ATTACH)
							.where(CONTRACT_ATTACH.DOMAIN.eq(domainId)).and(CONTRACT_ATTACH.DRIVEID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record5<Integer, Byte, String, Byte, String> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("contract");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setType(r.value4());
					fileInfo.setCategory(-2);
					fileInfo.setDriveId(r.value5());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
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
		public static Vector<FileInfo> getDriveIAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record5<Integer, Byte, String, Byte, String>> attach = dslContext
							.select(IATTACH.ID,IATTACH.MIMETYPE
									,IATTACH.DESCRIPTION,IATTACH.TYPE
									,IATTACH.DRIVEID)
							.from(IATTACH)
							.where(IATTACH.DOMAIN.eq(domainId)).and(IATTACH.DRIVEID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record5<Integer, Byte, String, Byte, String> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("item");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setType(r.value4());
					fileInfo.setCategory(-2);
					fileInfo.setDriveId(r.value5());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
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
		public static Vector<FileInfo> getDriveInvoiceAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record5<Integer, Byte, String, Byte, String>> attach = dslContext
							.select(INVOICE_ATTACH.ID,INVOICE_ATTACH.MIMETYPE
									,INVOICE_ATTACH.DESCRIPTION,INVOICE_ATTACH.TYPE
									,INVOICE_ATTACH.DRIVEID)
							.from(INVOICE_ATTACH)
							.where(INVOICE_ATTACH.DOMAIN.eq(domainId)).and(INVOICE_ATTACH.DRIVEID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record5<Integer, Byte, String, Byte, String> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("invoice");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setType(r.value4());
					fileInfo.setCategory(-2);
					fileInfo.setDriveId(r.value5());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
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
		public static Vector<FileInfo> getDriveOfferAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record4<Integer, Byte, String, String>> attach = dslContext
							.select(OFFER_ATTACH.ID,OFFER_ATTACH.MIMETYPE
									,OFFER_ATTACH.DESCRIPTION
									,OFFER_ATTACH.DRIVEID)
							.from(OFFER_ATTACH)
							.where(OFFER_ATTACH.DOMAIN.eq(domainId)).and(OFFER_ATTACH.DRIVEID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record4<Integer, Byte, String, String> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("OFFER");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setCategory(-2);
					fileInfo.setDriveId(r.value4());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
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
		
		public static Vector<FileInfo> getDrivePayrollAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record5<Integer, Byte, String, Byte, String>> attach = dslContext
							.select(PAYROLL_BATCH_ATTACH.ID,PAYROLL_BATCH_ATTACH.MIMETYPE
									,PAYROLL_BATCH_ATTACH.DESCRIPTION,PAYROLL_BATCH_ATTACH.TYPE
									,PAYROLL_BATCH_ATTACH.DRIVEID)
							.from(PAYROLL_BATCH_ATTACH)
							.where(PAYROLL_BATCH_ATTACH.DOMAIN.eq(domainId)).and(PAYROLL_BATCH_ATTACH.DRIVEID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record5<Integer, Byte, String, Byte, String> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("payroll");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setType(r.value4());
					fileInfo.setCategory(-2);
					fileInfo.setDriveId(r.value5());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
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
		public static Vector<FileInfo> getDriveProjectAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record4<Integer, Byte, String, String>> attach = dslContext
							.select(PROJECT_ATTACH.ID,PROJECT_ATTACH.MIMETYPE
									,PROJECT_ATTACH.DESCRIPTION
									,PROJECT_ATTACH.DRIVEID)
							.from(PROJECT_ATTACH)
							.where(PROJECT_ATTACH.DOMAIN.eq(domainId)).and(PROJECT_ATTACH.DRIVEID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record4<Integer, Byte, String, String> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("project");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setCategory(-2);
					fileInfo.setDriveId(r.value4());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
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
		
		public static Vector<FileInfo> getDriveSepeAttach(String domain, Integer domainId) throws SQLException{
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Result<Record5<Integer, Byte, String, Byte, String>> attach = dslContext
							.select(SEPE_BATCH_ATTACH.ID,SEPE_BATCH_ATTACH.MIMETYPE
									,SEPE_BATCH_ATTACH.DESCRIPTION,SEPE_BATCH_ATTACH.TYPE
									,SEPE_BATCH_ATTACH.DRIVEID)
							.from(SEPE_BATCH_ATTACH)
							.where(SEPE_BATCH_ATTACH.DOMAIN.eq(domainId)).and(SEPE_BATCH_ATTACH.DRIVEID.isNotNull())
							.fetch();
				
				Vector<FileInfo> attachs = new Vector<FileInfo>();
				
				for (Record5<Integer, Byte, String, Byte, String> r : attach) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setAonType("sepe");
					fileInfo.setFileId(r.value1());
					fileInfo.setMimetype(r.value2());
					fileInfo.setTitle(r.value3());
					fileInfo.setType(r.value4());
					fileInfo.setCategory(-2);
					fileInfo.setDriveId(r.value5());
					fileInfo.setDomainId(domainId);
					fileInfo.setDomain(domain);
					attachs.add(fileInfo);
				}
				
				return attachs;
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
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
	
		
		public static Vector<FileInfo> getServiConvenios(String domain) throws SQLException{
			Connection connection = null;
			try {
				Vector<FileInfo> vector = new Vector<FileInfo>();
				
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
							JooqSettings.getDefaultSettings());

				Result<Record4<Integer, String, String, Timestamp>> username = dslContext
							.select(RATTACH.ID, RATTACH.DESCRIPTION,
									RATTACH.DRIVE_ID,RATTACH.MODIFICATION_DATE)
							.from(RATTACH)
							.where(RATTACH.DOMAIN.eq(0).and(RATTACH.ID.lessThan(0)))
							.fetch();

				for (Record4<Integer, String, String, Timestamp> record : username) {
					FileInfo fi = new FileInfo();
					fi.setAonType("registry");
					if (record.value1() != null) {
						fi.setFileId(record.value1());
					}
					if (record.value2() != null) {
						fi.setTitle(record.value2());
					}
					if (record.value3() != null) {
						fi.setDriveId(record.value3());
					}
					if (record.value4() != null) {
						fi.setModificationDate(record.value4());
					}
				
					fi.setDomainId(0);
					vector.add(fi);
				}
			
				return vector;
				
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
		public static FileInfo getServiConvenio(String domain,Integer id) throws SQLException{
			Connection connection = null;
			try {
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
							JooqSettings.getDefaultSettings());

				Record4<Integer, String, String, Timestamp> record = dslContext
							.select(RATTACH.ID, RATTACH.DESCRIPTION,
									RATTACH.DRIVE_ID,RATTACH.MODIFICATION_DATE)
							.from(RATTACH)
							.where(RATTACH.ID.equal(id))
							.fetchOne();
				
				Result<Record1<String>> tags = dslContext.select(TAG.NAME)
					.from(RATTACH_TAG).join(TAG).on(RATTACH_TAG.TAG.equal(TAG.ID))
					.where(RATTACH_TAG.RATTACH.equal(id))
					.fetch();
				
				FileInfo fi = null;
				if(record != null){
					fi = new FileInfo();
					fi.setAonType("registry");
					if (record.value1() != null) {
						fi.setFileId(record.value1());
					}
					if (record.value2() != null) {
						fi.setTitle(record.value2());
					}
					if (record.value3() != null) {
						fi.setDriveId(record.value3());
					}
					if (record.value4() != null) {
						fi.setModificationDate(record.value4());
					}
					Vector<String> v = new Vector<String>();
					for(Record1<String> tag : tags){
						v.add(tag.value1());
					}
					fi.setTags(v);
			
					fi.setDomainId(0);
				}
			
				return fi;
				
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
		public static void updateSCModificationDate(String domain,Integer domainId, FileInfo fileInfo, java.util.Date date) throws SQLException{
			
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
							JooqSettings.getDefaultSettings());
				dslContext.update(RATTACH)
					.set(RATTACH.MODIFICATION_DATE, new Timestamp(date.getTime()))
					.where(RATTACH.ID.eq(fileInfo.getFileId())).execute();
				
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
		public static Integer getSCLastId(String domain) throws SQLException{
			
			Connection connection = null;
			try {

				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
							JooqSettings.getDefaultSettings());
				Record1<Integer> lastId = dslContext.select(RATTACH.ID)
						.from(RATTACH)
						.where(RATTACH.ID.lessThan(0))
						.and(RATTACH.DOMAIN.eq(0))
						.orderBy(RATTACH.ID)
						.limit(1)
						.fetchOne();
				
				return lastId.value1();
				
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
		public static void setSCTag(String domain,FileInfo fileInfo, String tag) throws SQLException{
			
			Connection connection = null;
			try {
				
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
							JooqSettings.getDefaultSettings());
				
				Record1<Integer> t = dslContext.select(TAG.ID)
					.from(TAG)
					.where(TAG.ID.lessThan(0))
					.and(TAG.NAME.equal(tag))
					.fetchOne();
				
				Integer tagId = null;
				if(t != null && t.value1() != null) tagId = t.value1();
				else{
					Record1<Integer> lastId = dslContext.select(TAG.ID)
							.from(TAG)
							.where(TAG.ID.lessThan(0))
							.and(TAG.DOMAIN.eq(0))
							.orderBy(TAG.ID)
							.limit(1)
							.fetchOne();
					
					tagId = lastId.value1() - 1;
					
					dslContext.insertInto(TAG, TAG.ID, TAG.DOMAIN, TAG.NAME, TAG.TYPE)
						.values(tagId, 0, tag,(byte) 0).execute();
				}
				dslContext.insertInto(RATTACH_TAG, RATTACH_TAG.DOMAIN, RATTACH_TAG.RATTACH, RATTACH_TAG.TAG)
						.values(0, fileInfo.getFileId(), tagId).execute();
				
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
		
}
