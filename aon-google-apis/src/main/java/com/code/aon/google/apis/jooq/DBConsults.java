package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.CommercialTracking.COMMERCIAL_TRACKING;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record1;
import org.jooq.Result;

import com.code.aon.google.apis.FileInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.User;


public class DBConsults {
	
	public static LinkedList<Domain> getDriveDomainList(String domainName, Integer domainId){
		return AON.getDriveDomainList(domainName, domainId, "");
	}
	
	public static Domain getDomain(String domainName, Integer domainId){
		return AON.getDomain(domainName, domainId, "");
	}
	
	public static Domain getDomain(Domain domain, User user){
		return AON.getDomain(domain.getName(), domain.getId(), user.getLogin());
	}
	
	public static Integer getDomainId(String domainName, Integer domainId, String login){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return ctx.getDslContext().select(DOMAIN.ID)
			.from(DOMAIN).where(DOMAIN.NAME.eq(domainName))
			.limit(1).fetchOne().getValue(DOMAIN.ID);
		}finally{
			if(ctx != null) ctx.close();
		}
	}
	
	public static DomainGserviceaccount getServiceAccount(String domainName, Integer domainId){
		return AON.getDomainGserviceaccount(domainName, domainId, "");
	}
	
	public static DomainGserviceaccount getGeneralServiceAccount(String domainName, Integer domainId){
		return AON.getGeneralDomainGserviceaccount(domainName, domainId, "");
	}
	
	public static DomainGserviceaccount getServiceAccount(Domain domain, User user){
		return AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
	}
	
	public static HashMap<Integer, DomainGserviceaccount> getServiceAccountMap(Domain domain, User user){
		return AON.getDomainGserviceaccountMap(domain.getName(), domain.getId(), user.getLogin(), domain.getParentId());
	}
	
	public static void updateGoogleAccount(Domain domain, User user, String googleAccount){
		AON.updateDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin(), googleAccount);
	}
	
	public static Category getCategory(Domain domain, User user, Integer categoryId){
		return AON.getCategory(domain.getName(), domain.getId(), user.getLogin(), categoryId);
	}
	
	public static LinkedList<Category> getCategoryList(Domain domain, User user){
		return AON.getCategoryList(domain.getName(), domain.getId(), user.getLogin());
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
			fileInfo.setType(a.getType());
			fileInfo.setTitle(a.getDescription());
			fileInfo.setMimetype(a.getMimeType().value());
			fileInfo.setFileId(a.getId());
			fileInfo.setDomainId(a.getDomain().getId());
			return fileInfo;
		}
	}
	
	public static void deleteFileAttach(Domain domain, User user, AttachType attachType, Integer attachId){
		AON.delete(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getIdProperty().eq(attachId), attachType);
	}
	
	public static String getAttachDriveId(Domain domain, User user, AttachType attachType, Integer attachId){
		return AON.getAttach(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(attachId),attachType).getDriveId();
	}
	
	public static void updateAttachData(Domain domain, User user, AttachType attachType, Integer attachId, byte[] data){
		Attach attach  = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(attachId),attachType);
		attach.setData(data);
		AON.updateAttachData(domain.getName(), domain.getId(), user.getLogin(), attach);
	}
	
	//REGISTRY ATTACH
	
	public static Vector<FileInfo> getRAttachFilesInDrive(Domain domain,Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.REGISTRY).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}
	
	public static void deleteRegistryAttachTags(Domain domain, User user, Integer rattachId) {
		AON.deleteRegistryAttachTag(domain.getName(), domain.getId(), user.getLogin(), rattachId);
	}
	
	public static void setDriveIdRegistryAttach(Domain domain,FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
			ctx.getDslContext().update(RATTACH)
			.set(RATTACH.DRIVE_ID,fileInfo.getDriveId())
			.where(RATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteBlobRegistryAttach(Domain domain,FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
			
			byte[] aux =null;
			ctx.getDslContext().update(RATTACH)
			.set(RATTACH.DATA,aux)
			.where(RATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteDriveIdRegistryAttach(Domain domain,String id, Integer fileId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
			String sql = "UPDATE rattach SET drive_id = NULL WHERE id = "+fileId+";";
			ctx.getDslContext().fetch(sql);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	//CONTRACT ATTACH
	
	public static Vector<FileInfo> getContractAttachFilesInDrive(Domain domain,Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.CONTRACT).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}

	public static FileInfo getDataContractAttach(Domain domain, User user, FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

			Result<Record1<byte[]>> data = ctx.getDslContext()
					.select(CONTRACT_ATTACH.DATA)
					.from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FileInfo getEmailsContractAttach(Domain domain, User user, FileInfo fileInfo) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

			Result<Record1<String>> email = ctx.getDslContext()
					.select(RMEDIA.VALUE)
					.from(CONTRACT_ATTACH)
					.join(CONTRACT).on(CONTRACT_ATTACH.CONTRACT.eq(CONTRACT.ID))
					.join(RMEDIA).on(RMEDIA.REGISTRY.eq(CONTRACT.PERSON))
					.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId()).and(RMEDIA.MEDIA.eq((byte) 4)))
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
	
	public static void setDriveIdContractAttach(Domain domain,FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
			ctx.getDslContext().update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(CONTRACT_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteDriveIdContractAttach(Domain domain,String id, Integer fileId) {
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
	
	public static void deleteBlobContractAttach(Domain domain,FileInfo fileInfo){
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

	//ITEM ATTACH
	
	public static Vector<FileInfo> getIattachFilesInDrive(Domain domain,Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.ITEM).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}

	public static FileInfo getDataIattach(Domain domain, User user, FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

			Result<Record1<byte[]>> data = ctx.getDslContext()
					.select(IATTACH.DATA)
					.from(IATTACH)
					.where(IATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void setDriveIdIattach(Domain domain,FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(),"");						
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
			if (ctx != null) ctx.close();
		}
	}
	
	//INVOICE ATTACH
	
	public static Vector<FileInfo> getInvoiceAttachFilesInDrive(Domain domain,Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.INVOICE).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}

	public static FileInfo getDataInvoiceAttach(Domain domain, User user,FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
			Result<Record1<byte[]>> data = ctx.getDslContext()
					.select(INVOICE_ATTACH.DATA)
					.from(INVOICE_ATTACH)
					.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static FileInfo getEmailsInvoiceAttach(Domain domain, User user, FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
			
			Result<Record1<String>> email = ctx.getDslContext()
					.select(RMEDIA.VALUE)
					.from(INVOICE_ATTACH)
					.join(INVOICE).on(INVOICE_ATTACH.INVOICE.eq(INVOICE.ID))
					.join(RMEDIA).on(RMEDIA.REGISTRY.eq(INVOICE.REGISTRY))
					.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId()).and(RMEDIA.MEDIA.eq((byte) 4)))
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

	public static void setDriveIdInvoiceAttach(Domain domain,FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
			ctx.getDslContext()
			.update(INVOICE_ATTACH)
			.set(INVOICE_ATTACH.DRIVEID,fileInfo.getDriveId())
			.where(INVOICE_ATTACH.ID.eq(fileInfo.getFileId())).execute();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteDriveIdInvoiceAttach(Domain domain,String id, Integer fileId){
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
	
	//OFFER ATTACH
	
	public static Vector<FileInfo> getOfferAttachFilesInDrive(Domain domain, Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.OFFER).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}

	public static FileInfo getDataOfferAttach(Domain domain,User user, FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record1<byte[]>> data = ctx.getDslContext()
					.select(OFFER_ATTACH.DATA)
					.from(OFFER_ATTACH)
					.where(OFFER_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void setDriveIdOfferAttach(Domain domain,FileInfo fileInfo) {
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
	
	public static void deleteDriveIdOfferAttach(Domain domain,String id, Integer fileId) {
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
	
	//PAYROLL BATCH ATTACH
	
	public static Vector<FileInfo> getPayrollAttachFilesInDrive(Domain domain,Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.PAYROLL).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}

	public static FileInfo getDataPayrollAttach(Domain domain, User user, FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record1<byte[]>> data = ctx.getDslContext()
					.select(PAYROLL_BATCH_ATTACH.DATA)
					.from(PAYROLL_BATCH_ATTACH)
					.where(PAYROLL_BATCH_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
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
	
	//PROJECT ATTACH
	
	public static Vector<FileInfo> getProjectAttachFilesInDrive(Domain domain,Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.PROJECT).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}
	
	public static FileInfo getDataProjectAttach(Domain domain, User user,FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record1<byte[]>> data = ctx.getDslContext()
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
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static FileInfo getEmailsProjectAttach(Domain domain, User user, FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());			
			Result<Record1<String>> email = ctx.getDslContext()
					.select(RMEDIA.VALUE)
					.from(PROJECT_ATTACH)
					.join(PROJECT).on(PROJECT_ATTACH.PROJECT.eq(PROJECT.ID))
					.join(RMEDIA).on(RMEDIA.REGISTRY.eq(PROJECT.REGISTRY))
					.where(PROJECT_ATTACH.ID.eq(fileInfo.getFileId()).and(RMEDIA.MEDIA.eq((byte) 4)))
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
	
	public static void deleteDriveIdProjectAttach(Domain domain,String id, Integer fileId) {
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

	//SEPE BATCH ATTACH
	
	public static Vector<FileInfo> getSepeAttachFilesInDrive(Domain domain,Vector<FileInfo> attachs) {
		attachs.addAll(AON.getAttachStream(domain.getName(), domain.getId(), "",
				f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.and(f.getDataProperty().isNull())
				.and(f.getDriveIdProperty().isNotNull()),
				AttachType.SEPE).map(new AttachToFileInfo())
				.collect(Collectors.toCollection(Vector::new)));
		return attachs;
	}

	public static FileInfo getDataSepeAttach(Domain domain, User user, FileInfo fileInfo){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record1<byte[]>> data = ctx.getDslContext()
					.select(SEPE_BATCH_ATTACH.DATA)
					.from(SEPE_BATCH_ATTACH)
					.where(SEPE_BATCH_ATTACH.ID.eq(fileInfo.getFileId()))
					.fetch();
			
			for (Record1<byte[]> record1 : data) {
				fileInfo.setData(record1.value1());
			}
			return fileInfo;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

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
	
	public static void deleteDriveIdSepeAttach(Domain domain,String id, Integer fileId){
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
	
	public static void deleteBlobSepeAttach(Domain domain,FileInfo fileInfo){
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

	public static Vector<String> getCommercial(Domain domain, User user, Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record1<String>> data = ctx.getDslContext()
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
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Vector<String> getParentName(Domain domain, User user) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record1<String>> data = ctx.getDslContext()
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
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static String getUserName(Domain domain, User user, Integer id){	
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record1<String>> data = ctx.getDslContext()
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
			if (ctx != null)
				ctx.close();
		}
	}	

	public static Vector<FileInfo> getRattach(Domain domain, User user){
		return AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getDomainProperty().eq(domain.getId()), AttachType.REGISTRY)
				.map(new AttachToFileInfo()).collect(Collectors.toCollection(Vector::new));
	}
	
	public static void upsize(Domain domain,User user, Integer id , Integer size){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			ctx.getDslContext().update(RATTACH)
			.set(RATTACH.DPARENT_ID,size.toString())
			.where(RATTACH.ID.eq(id)).execute();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
}
