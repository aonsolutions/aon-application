package com.code.aon.google.apis.jooq;


import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Result;

import com.code.aon.google.apis.FileInfo;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.model.File;

public class DBDrive {
	
	
	public static LinkedList<Attach> getAttachLimit(Domain domain, User user, AttachType attachType,
			Byte[] types, Integer page, Integer perPage){
		
		return AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getDriveIdProperty().isNull()
				.and(f.getDataProperty().isNotNull())
				.and(f.getMimeTypeProperty().isNotNull())
				.and(f.getTypeProperty().in(types))
				.page(page).perPage(perPage),
				attachType, true)
				.collect(Collectors.toCollection(LinkedList::new));
	}	
	
	public static Stream<Attach> getAttachStreamLimit(Domain domain, User user, AttachType attachType, Byte[] types, Integer page, Integer perPage){
		return AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getDriveIdProperty().isNull()
				.and(f.getDataProperty().isNotNull())
				.and(f.getMimeTypeProperty().isNotNull())
				.and(f.getTypeProperty().in(types))
				.page(page).perPage(perPage),
				attachType, true);
	}
	
	public static Vector<FileInfo> getRegistryAttachLimit(Domain domain, User user, Vector<RegistryAttachmentType> rats,Integer firstId ){
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Condition condition = RATTACH.TYPE.isNull().or(RATTACH.TYPE.isNotNull());
			if(!rats.isEmpty()){
				condition = RATTACH.TYPE.eq((byte)rats.get(0).ordinal());
				for(RegistryAttachmentType rat : rats){
					if(rats.get(0).ordinal()!= rat.ordinal())
						condition = condition.or(RATTACH.TYPE.eq((byte)rat.ordinal()));
				}
			}
			
			Result<Record6<Integer, Byte, String, Byte, Integer, Integer>> attach = ctx.getDslContext()
					.select(RATTACH.ID, RATTACH.MIMETYPE,
							RATTACH.DESCRIPTION, RATTACH.TYPE
							,RATTACH.CATEGORY,RATTACH.DOMAIN)
					.from(RATTACH)
					.where(RATTACH.DOMAIN.eq(domain.getId()))
							.and(RATTACH.DRIVE_ID.isNull()).and(RATTACH.DATA.isNotNull()).and(condition)
							.and(RATTACH.MIMETYPE.isNotNull())//.and(RATTACH.TYPE.isNotNull())
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
					Domain d = new Domain().setName(domain.getName()).setId( r.getValue(RATTACH.DOMAIN));
					fileInfo.setDomain(d.getName());
					byte data[] = getFileData(domain, user, fileInfo.getFileId());
					fileInfo.setData(data);
					
				} catch (Exception e) {e.printStackTrace();}
				attachs.add(fileInfo);
			});
		
			return attachs;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	
	public static byte[] getFileData(Domain domain, User user, Integer attachId){
		return AON.getAttach(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(attachId), AttachType.REGISTRY).getData();
	}
	
	/**********************************************/
	
	public static void updateDriveId(Domain domain, User user, File file, Integer attachId){
		String aonType = "";
		for (String key : file.getProperties().keySet()) {
			if(key.equals("aontype"))
				aonType = file.getProperties().get(key);
		}
		AON.updateAttachDriveId(domain.getName(), domain.getId(), user.getLogin(), attachId, file.getId(), AttachType.getAttachType(aonType));
	}
	
	public static void updateDriveId(File f, Integer id){

		String domainName = "";
		Integer domainId = 0;
		Map<String, Integer> domains = DBSync.getDomainMap();
		Vector<String> d = new Vector<String>(domains.keySet());
		domainName = d.get(0);
		domainId = domains.get(domainName);
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, "");
			
			String aonType = "";
			for (String key : f.getProperties().keySet()) {
				if(key.equals("aontype"))
					aonType = f.getProperties().get(key);
			}
			switch (aonType) {
			case "registry":	
				ctx.getDslContext().update(RATTACH)
					.set(RATTACH.DRIVE_ID, f.getId())
					.where(RATTACH.ID.eq(id)).execute();
				break;
			case "contract":
				ctx.getDslContext().update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DRIVEID, f.getId())
				.where(CONTRACT_ATTACH.ID.eq(id)).execute();
				break;
			case "item":
				ctx.getDslContext().update(IATTACH)
				.set(IATTACH.DRIVEID, f.getId())
				.where(IATTACH.ID.eq(id)).execute();
				break;
			case "invoice":
				ctx.getDslContext().update(INVOICE_ATTACH)
				.set(INVOICE_ATTACH.DRIVEID, f.getId())
				.where(INVOICE_ATTACH.ID.eq(id)).execute();
				break;
			case "offer":
				ctx.getDslContext().update(OFFER_ATTACH)
				.set(OFFER_ATTACH.DRIVEID, f.getId())
				.where(OFFER_ATTACH.ID.eq(id)).execute();
				break;
			case "payroll":
				ctx.getDslContext().update(PAYROLL_BATCH_ATTACH)
				.set(PAYROLL_BATCH_ATTACH.DRIVEID, f.getId())
				.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).execute();
				break;
			case "project":
				ctx.getDslContext().update(PROJECT_ATTACH)
				.set(PROJECT_ATTACH.DRIVEID, f.getId())
				.where(PROJECT_ATTACH.ID.eq(id)).execute();
				break;
			case "sepe":
				ctx.getDslContext().update(SEPE_BATCH_ATTACH)
				.set(SEPE_BATCH_ATTACH.DRIVEID, f.getId())
				.where(SEPE_BATCH_ATTACH.ID.eq(id)).execute();
				break;
			default:
				break;
			}
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/**************************** ATTACHS *****************************/
	
	private static class AttachToFileInfo implements Function<Attach, FileInfo> {
		
		@Override
		public FileInfo apply(Attach a) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setAonType(a.getAttachType().getName());
			fileInfo.setCategory(a.getCategory() != null ? a.getCategory() : -2);
			fileInfo.setData(a.getData());
			fileInfo.setDate(a.getDate());
			fileInfo.setDriveId(a.getDriveId());
			fileInfo.setType(a.getType() != null ? a.getType() : 0);
			fileInfo.setTitle(a.getDescription());
			fileInfo.setMimetype(a.getMimeType().value());
			fileInfo.setFileId(a.getId());
			fileInfo.setDomainId(a.getDomain() != null && a.getDomain().getId() != null ? a.getDomain().getId() : null);
			fileInfo.setDomain(a.getDomain() != null && a.getDomain().getName() != null ? a.getDomain().getName() : null);
			return fileInfo;
		}
	}
	
	public static LinkedList<FileInfo> getDriveAttach(Domain domain, User user, AttachType attachType){
		return AON.getAttachList(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getDriveIdProperty().isNotNull()),
				attachType).stream().map(new AttachToFileInfo())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static FileInfo getDataAttach(Domain domain, User user, AttachType attachType,
			FileInfo fileInfo) {
		byte[] data = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(fileInfo.getFileId()), attachType).getData();
		return fileInfo.setData(data);
	}
	
	//REGISTRY ATTACH
		
		public static Vector<FileInfo> getRegistryAttach(Domain domain,Vector<FileInfo> attachs) {
			attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
					f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
					.and(f.getDataProperty().isNotNull()),
					AttachType.REGISTRY, true).map(new  AttachToFileInfo())
					.collect(Collectors.toCollection(Vector::new)));
			return attachs;
		}
	
		public static void insertBlobRAttach(byte[] bs,Domain domain,String driveId, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().update(RATTACH)
				.set(RATTACH.DATA,bs)
				.where(RATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFileRAttach(Domain domain, String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(RATTACH)
					.where(RATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteRAttachTags(Domain domain,String drive_id, Integer id)  {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(RATTACH_TAG)
					.where(RATTACH_TAG.RATTACH.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getRAttachDriveID(Domain domain, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<String>> data =ctx.getDslContext().select(RATTACH.DRIVE_ID)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static Integer getRAttachDomainID(Domain domain, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<Integer>> data =ctx.getDslContext().select(RATTACH.DOMAIN)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return 0;
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static FileInfo getEmailsRegistryAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");

				Result<Record1<String>> email = ctx.getDslContext()
						.select(RMEDIA.VALUE)
						.from(RATTACH)
						.join(REGISTRY).on(RATTACH.REGISTRY.eq(REGISTRY.ID))
						.join(RMEDIA).on(RMEDIA.REGISTRY.eq(REGISTRY.ID))
						.where(RATTACH.ID.eq(fileInfo.getFileId()).and(RMEDIA.MEDIA.eq((byte) 4)))
						.fetch();
				
				Vector<String> mails = new Vector<String>();
				for (Record1<String> record1 : email) {
					mails.add(record1.value1());
				}
				fileInfo.setEmails(mails);
				return fileInfo;
			} finally {
				if (ctx != null)
					ctx.close();
			}
			
		}
	//CONTRACT ATTACH

		public static FileInfo getEmailsContractAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				int num= 4;
				
				
				Result<Record1<String>> email = ctx.getDslContext()
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
				if (ctx != null)
					ctx.close();
			}
			
		}
		
		public static void setDriveIdContractAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
							
				ctx.getDslContext().update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DRIVEID,fileInfo.getDriveId())
				.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteDriveIdContractAttach(Domain domain,String id, Integer fileId){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				String sql = "UPDATE contract_attach SET driveId = NULL WHERE id = "+fileId+";";
				ctx.getDslContext().fetch(sql);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteBlobContractAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				byte[] aux =null;
				ctx.getDslContext().update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DATA,aux)
				.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void insertBlobContractAttach(byte[] bs,Domain domain,String driveId, Integer  id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DATA,bs)
				.where(CONTRACT_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFileContractAttach(Domain domain, String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getContractAttachDriveID(Domain domain, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<String>> data =ctx.getDslContext().select(CONTRACT_ATTACH.DRIVEID)
					.from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
	
	//ITEM ATTACH
		
		public static void setDriveIdIattach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
							
				ctx.getDslContext().update(IATTACH)
				.set(IATTACH.DRIVEID,fileInfo.getDriveId())
				.where(IATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteDriveIdIattach(Domain domain,String id, Integer fileId) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				String sql = "UPDATE iattach SET driveId = NULL WHERE id = "+fileId+";";
				ctx.getDslContext().fetch(sql);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteBlobIattach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				byte[] aux =null;
				ctx.getDslContext().update(IATTACH)
				.set(IATTACH.DATA,aux)
				.where(IATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void insertBlobIAttach(byte[] bs,Domain domain,String driveId, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().update(IATTACH)
				.set(IATTACH.DATA,bs)
				.where(IATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFileIAttach(Domain domain, String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");	
	
				ctx.getDslContext().delete(IATTACH)
					.where(IATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getIAttachDriveID(Domain domain, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				Result<Record1<String>> data =ctx.getDslContext().select(IATTACH.DRIVEID)
					.from(IATTACH)
					.where(IATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
	//INVOICE ATTACH
		
		public static FileInfo getEmailsInvoiceAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				int num= 4;
				
				
				Result<Record1<String>> email = ctx.getDslContext()
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
				if (ctx != null)
					ctx.close();
			}
			
		}	

		public static void setDriveIdInvoiceAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
							
				ctx.getDslContext().update(INVOICE_ATTACH)
				.set(INVOICE_ATTACH.DRIVEID,fileInfo.getDriveId())
				.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteDriveIdInvoiceAttach(Domain domain,String id, Integer fileId) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				String sql = "UPDATE invoice_attach SET driveId = NULL WHERE id = "+fileId+";";
				ctx.getDslContext().fetch(sql);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteBlobInvoiceAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				byte[] aux =null;
				ctx.getDslContext().update(INVOICE_ATTACH)
				.set(INVOICE_ATTACH.DATA,aux)
				.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void insertBlobInvoiceAttach(byte[] bs,Domain domain,String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().update(INVOICE_ATTACH)
				.set(INVOICE_ATTACH.DATA,bs)
				.where(INVOICE_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFileInvoiceAttach(Domain domain, String driveId, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(INVOICE_ATTACH)
					.where(INVOICE_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getInvoiceAttachDriveID(Domain domain, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<String>> data =ctx.getDslContext().select(INVOICE_ATTACH.DRIVEID)
					.from(INVOICE_ATTACH)
					.where(INVOICE_ATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
	//OFFER ATTACH
		
		public static void setDriveIdOfferAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
							
				ctx.getDslContext().update(OFFER_ATTACH)
				.set(OFFER_ATTACH.DRIVEID,fileInfo.getDriveId())
				.where(OFFER_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteDriveIdOfferAttach(Domain domain,String id, Integer fileId){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				String sql = "UPDATE offer_attach SET driveId = NULL WHERE id = "+fileId+";";
				ctx.getDslContext().fetch(sql);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteBlobOfferAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				byte[] aux =null;
				ctx.getDslContext().update(OFFER_ATTACH)
				.set(OFFER_ATTACH.DATA,aux)
				.where(OFFER_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void insertBlobOfferAttach(byte[] bs,Domain domain,String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().update(OFFER_ATTACH)
				.set(OFFER_ATTACH.DATA,bs)
				.where(OFFER_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFileOfferAttach(Domain domain, String driveId, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(OFFER_ATTACH)
					.where(OFFER_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getOfferAttachDriveID(Domain domain, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<String>> data =ctx.getDslContext().select(OFFER_ATTACH.DRIVEID)
					.from(OFFER_ATTACH)
					.where(OFFER_ATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
	//PAYROLL BATCH ATTACH
		
		public static void setDriveIdPayrollAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
							
				ctx.getDslContext().update(PAYROLL_BATCH_ATTACH)
				.set(PAYROLL_BATCH_ATTACH.DRIVEID,fileInfo.getDriveId())
				.where(PAYROLL_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteDriveIdPayrollAttach(Domain domain,String id, Integer fileId){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				String sql = "UPDATE payroll_batch_attach SET driveId = NULL WHERE id = "+fileId+";";
				ctx.getDslContext().fetch(sql);
				
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteBlobPayrollAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				byte[] aux =null;
				ctx.getDslContext().update(PAYROLL_BATCH_ATTACH)
				.set(PAYROLL_BATCH_ATTACH.DATA,aux)
				.where(PAYROLL_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void insertBlobPayrollAttach(byte[] bs,Domain domain,String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().update(PAYROLL_BATCH_ATTACH)
				.set(PAYROLL_BATCH_ATTACH.DATA,bs)
				.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFilePayrollAttach(Domain domain,String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(PAYROLL_BATCH_ATTACH)
					.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getPayrollAttachDriveID(Domain domain, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<String>> data =ctx.getDslContext().select(PAYROLL_BATCH_ATTACH.DRIVEID)
					.from(PAYROLL_BATCH_ATTACH)
					.where(PAYROLL_BATCH_ATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
	//PROJECT ATTACH
		
		public static FileInfo getEmailsProjectAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				int num= 4;
				
				
				Result<Record1<String>> email = ctx.getDslContext()
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
				if (ctx != null)
					ctx.close();
			}
			
		}	
		
		public static void setDriveIdProjectAttach(Domain domain,FileInfo fileInfo){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				ctx.getDslContext().update(PROJECT_ATTACH)
				.set(PROJECT_ATTACH.DRIVEID,fileInfo.getDriveId())
				.where(PROJECT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteDriveIdProjectAttach(Domain domain,String id, Integer fileId){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				String sql = "UPDATE project_attach SET driveId = NULL WHERE id = "+fileId+";";
				ctx.getDslContext().fetch(sql);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteBlobProjectAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				byte[] aux =null;
				ctx.getDslContext().update(PROJECT_ATTACH)
				.set(PROJECT_ATTACH.DATA,aux)
				.where(PROJECT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void insertBlobProjectAttach(byte[] bs,Domain domain,String driveId, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().update(PROJECT_ATTACH)
				.set(PROJECT_ATTACH.DATA,bs)
				.where(PROJECT_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFileProjectAttach(Domain domain, String driveId, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(PROJECT_ATTACH)
					.where(PROJECT_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getProjectAttachDriveID(Domain domain, Integer id){
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<String>> data =ctx.getDslContext().select(PROJECT_ATTACH.DRIVEID)
					.from(PROJECT_ATTACH)
					.where(PROJECT_ATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		//SEPE BATCH ATTACH

		public static void setDriveIdSepeAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
							
				ctx.getDslContext().update(SEPE_BATCH_ATTACH)
				.set(SEPE_BATCH_ATTACH.DRIVEID,fileInfo.getDriveId())
				.where(SEPE_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteDriveIdSepeAttach(Domain domain,String id, Integer fileId) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				String sql = "UPDATE sepe_batch_attach SET driveId = NULL WHERE id = "+fileId+";";
				ctx.getDslContext().fetch(sql);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteBlobSepeAttach(Domain domain,FileInfo fileInfo) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				byte[] aux =null;
				ctx.getDslContext().update(SEPE_BATCH_ATTACH)
				.set(SEPE_BATCH_ATTACH.DATA,aux)
				.where(SEPE_BATCH_ATTACH.ID.eq(fileInfo.getFileId())).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void insertBlobSepeAttach(byte[] bs,Domain domain,String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				ctx.getDslContext().update(SEPE_BATCH_ATTACH)
				.set(SEPE_BATCH_ATTACH.DATA,bs)
				.where(SEPE_BATCH_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void deleteFileSepeAttach(Domain domain, String driveId, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				ctx.getDslContext().delete(SEPE_BATCH_ATTACH)
					.where(SEPE_BATCH_ATTACH.ID.eq(id)).execute();
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getSepeAttachDriveID(Domain domain, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				
				Result<Record1<String>> data =ctx.getDslContext().select(SEPE_BATCH_ATTACH.DRIVEID)
					.from(SEPE_BATCH_ATTACH)
					.where(SEPE_BATCH_ATTACH.ID.eq(id)).fetch();
			
				if(data.get(0).value1()!=null) return data.get(0).value1();
				else return "";
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
	
		
		public static Vector<FileInfo> getServiConvenios(Domain domain) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				Vector<FileInfo> vector = new Vector<FileInfo>();
				
				

				Result<Record4<Integer, String, String, Timestamp>> username = ctx.getDslContext()
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
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static FileInfo getServiConvenio(Domain domain, User user, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

				Record4<Integer, String, String, Timestamp> record = ctx.getDslContext()
							.select(RATTACH.ID, RATTACH.DESCRIPTION,
									RATTACH.DRIVE_ID,RATTACH.MODIFICATION_DATE)
							.from(RATTACH)
							.where(RATTACH.ID.equal(id))
							.fetchOne();
				
				Result<Record1<String>> tags = ctx.getDslContext().select(TAG.NAME)
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
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void updateSCModificationDate(Domain domain, User user, FileInfo fileInfo, java.util.Date date){
			
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
				ctx.getDslContext().update(RATTACH)
					.set(RATTACH.MODIFICATION_DATE, new Timestamp(date.getTime()))
					.where(RATTACH.ID.eq(fileInfo.getFileId())).execute();
				
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static Integer getSCLastId(Domain domain) {
			
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
				Record1<Integer> lastId = ctx.getDslContext().select(RATTACH.ID)
						.from(RATTACH)
						.where(RATTACH.ID.lessThan(0))
						.and(RATTACH.DOMAIN.eq(0))
						.orderBy(RATTACH.ID)
						.limit(1)
						.fetchOne();
				
				return lastId.value1();
				
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static void setSCTag(Domain domain, User user, FileInfo fileInfo, String tag){
			
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
				
				Record1<Integer> t = ctx.getDslContext().select(TAG.ID)
					.from(TAG)
					.where(TAG.ID.lessThan(0))
					.and(TAG.NAME.equal(tag))
					.fetchOne();
				
				Integer tagId = null;
				if(t != null && t.value1() != null) tagId = t.value1();
				else{
					Record1<Integer> lastId = ctx.getDslContext().select(TAG.ID)
							.from(TAG)
							.where(TAG.ID.lessThan(0))
							.and(TAG.DOMAIN.eq(0))
							.orderBy(TAG.ID)
							.limit(1)
							.fetchOne();
					
					tagId = lastId.value1() - 1;
					
					ctx.getDslContext().insertInto(TAG, TAG.ID, TAG.DOMAIN, TAG.NAME, TAG.TYPE)
						.values(tagId, 0, tag,(byte) 0).execute();
				}
				ctx.getDslContext().insertInto(RATTACH_TAG, RATTACH_TAG.DOMAIN, RATTACH_TAG.RATTACH, RATTACH_TAG.TAG)
						.values(0, fileInfo.getFileId(), tagId).execute();
				
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

}
