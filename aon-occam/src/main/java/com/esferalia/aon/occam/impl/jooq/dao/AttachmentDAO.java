package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;

import java.sql.Date;
import java.sql.Timestamp;

import org.jooq.Condition;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;



public class AttachmentDAO {

	//-------------------- GETS 
	
	public static Attach getRattach(AONContext ctx, Condition condition){	
		Record8<byte[], Byte, Byte, String, Integer, String, java.sql.Date, Byte> record = ctx.getDslContext()
			.select(RATTACH.DATA, RATTACH.MIMETYPE, RATTACH.TYPE, RATTACH.DRIVE_ID,
					RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.ATTACH_DATE,
					RATTACH.SECURITY_LEVEL)
			.from(RATTACH)
			.where(condition)
			.fetchOne();
	
		Attach rattach = new Attach();
		if(record != null){
			if(record.value1() != null) rattach.setData(record.value1());
			if(record.value2() != null) rattach.setMimeType(MimeType.values()[record.value2()]);
			if(record.value3() != null) rattach.setType(record.value3());
			if(record.value4() != null) rattach.setDriveId(record.value4());
			if(record.value5() != null) rattach.setId(record.value5());
			if(record.value6() != null) rattach.setDescription(record.value6());
			if(record.value7() != null) rattach.setDate(record.value7());
			if(record.value8() != null) rattach.setConfidential(record.value8().equals(1)?true:false);
		}
		return rattach;
	}
	
	
	public static Attach getRattachXXX(AONContext ctx, Condition condition){	
		Result<RattachRecord> rattachRecord = ctx.getDslContext().select()
					.from(RATTACH)
					.where(condition)
					.fetchInto(RATTACH);
	
		Attach rattach = new Attach();
		
		if(rattachRecord.size() == 1){
			RattachRecord record = rattachRecord.get(1);
			rattach.setAttachType(AttachType.REGISTRY);
			rattach.setData(record.getData());
			rattach.setMimeType(MimeType.values()[record.getMimetype()]);
			rattach.setType(record.getType());
			rattach.setDriveId(record.getDriveId());
			rattach.setId(record.getId());
			rattach.setDescription(record.getDescription());
			rattach.setDate(record.getAttachDate());
			rattach.setConfidential(record.getSecurityLevel().equals(1)?true:false);
		}
		return rattach;
	}
	
	public static Attach getRattachWithoutData(AONContext ctx, Condition condition){
		Record7< Byte, Byte, String, Integer, String, java.sql.Date, Byte> record = ctx.getDslContext()
			.select(RATTACH.MIMETYPE, RATTACH.TYPE, RATTACH.DRIVE_ID,
					RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.ATTACH_DATE,
					RATTACH.SECURITY_LEVEL)
			.from(RATTACH)
			.where(condition)
			.fetchOne();		
			
		Attach rattach = new Attach();
		rattach.setAttachType(AttachType.REGISTRY);
		if(record.value1() != null) rattach.setMimeType(MimeType.values()[record.value1()]);
		if(record.value2() != null) rattach.setType(record.value2());
		if(record.value3() != null) rattach.setDriveId(record.value3());
		if(record.value4() != null) rattach.setId(record.value4());
		if(record.value5() != null) rattach.setDescription(record.value5());
		if(record.value6() != null) rattach.setDate(record.value6());
		if(record.value7() != null) rattach.setConfidential(record.value7().equals(1)?true:false);

		return rattach;
	}	
	
	//-------------------- INSERTS 
	
	public static void insertContractAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(CONTRACT_ATTACH, CONTRACT_ATTACH.ATTACH_DATE,
				CONTRACT_ATTACH.CONTRACT, CONTRACT_ATTACH.DATA, CONTRACT_ATTACH.DESCRIPTION,
				CONTRACT_ATTACH.DOMAIN, CONTRACT_ATTACH.DRIVEID, CONTRACT_ATTACH.MIMETYPE,
				CONTRACT_ATTACH.SCOPE, CONTRACT_ATTACH.SECURITY_LEVEL, CONTRACT_ATTACH.TYPE)
		.values(new Timestamp(attach.getDate().getTime()), attach.getAttachModule(),
				attach.getData(), attach.getDescription(), attach.getDomain().getId(),
				attach.getDriveId(), (byte) attach.getMimeType().ordinal(),attach.getScope(),
				attach.getConfidential()?(byte)1:(byte)0, (byte) attach.getType())
		.execute();
	}
	
	public static void insertItemAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(IATTACH, IATTACH.DATA, IATTACH.DESCRIPTION,
				IATTACH.DOMAIN, IATTACH.DRIVEID, IATTACH.ITEM, IATTACH.MIMETYPE,
				IATTACH.TYPE)
		.values(attach.getData(), attach.getDescription(), attach.getDomain().getId(),
				attach.getDriveId(), attach.getAttachModule(),
				(byte) attach.getMimeType().ordinal(), (byte) attach.getType())
		.execute();
	}
	
	public static void insertInvoiceAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(INVOICE_ATTACH, INVOICE_ATTACH.ATTACH_DATE,
				INVOICE_ATTACH.DATA, INVOICE_ATTACH.DESCRIPTION, INVOICE_ATTACH.DOMAIN,
				INVOICE_ATTACH.DRIVEID, INVOICE_ATTACH.INVOICE, INVOICE_ATTACH.MIMETYPE,
				INVOICE_ATTACH.TYPE)
		.values(new Date(attach.getDate().getTime()), attach.getData(), attach.getDescription(),
				attach.getDomain().getId(), attach.getDriveId(), attach.getAttachModule(),
				(byte) attach.getMimeType().ordinal(), (byte) attach.getType())
		.execute();
	}
	
	public static void insertOfferAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(OFFER_ATTACH, OFFER_ATTACH.DATA, OFFER_ATTACH.DESCRIPTION,
				OFFER_ATTACH.DOMAIN, OFFER_ATTACH.DRIVEID, OFFER_ATTACH.MIMETYPE,
				OFFER_ATTACH.OFFER)
		.values(attach.getData(), attach.getDescription(), attach.getDomain().getId(),
				attach.getDriveId(), (byte) attach.getMimeType().ordinal(),
				attach.getAttachModule())
		.execute();
	}
	
	public static void insertPayrollAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(PAYROLL_BATCH_ATTACH, PAYROLL_BATCH_ATTACH.ATTACH_DATE,
				PAYROLL_BATCH_ATTACH.DATA, PAYROLL_BATCH_ATTACH.DESCRIPTION,
				PAYROLL_BATCH_ATTACH.DOMAIN, PAYROLL_BATCH_ATTACH.DRIVEID,
				PAYROLL_BATCH_ATTACH.MIMETYPE, PAYROLL_BATCH_ATTACH.SCOPE,
				PAYROLL_BATCH_ATTACH.SOURCE_BATCH, PAYROLL_BATCH_ATTACH.SOURCE_TYPE,
				PAYROLL_BATCH_ATTACH.TYPE)
		.values(new Date(attach.getDate().getTime()), attach.getData(), attach.getDescription(),
				attach.getDomain().getId(), attach.getDriveId(), (byte) attach.getMimeType().ordinal(),
				attach.getScope(), attach.getSourceBatch(), (byte) attach.getSourceType(),
				(byte) attach.getType())
		.execute();
	}
	
	public static void insertProjectAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(PROJECT_ATTACH, PROJECT_ATTACH.ATTACH_DATE, 
				PROJECT_ATTACH.DATA, PROJECT_ATTACH.DESCRIPTION, 
				PROJECT_ATTACH.DOMAIN, PROJECT_ATTACH.DRIVEID, 
				PROJECT_ATTACH.MIMETYPE, PROJECT_ATTACH.PROJECT, 
				PROJECT_ATTACH.SECURITY_LEVEL)
		.values(new Date(attach.getDate().getTime()), attach.getData(), 
				attach.getDescription(), attach.getDomain().getId(), 
				attach.getDriveId(), (byte)attach.getMimeType().ordinal(),
				attach.getAttachModule(), attach.getConfidential()?(byte)1:(byte)0)
		.execute();
	}
	
	public static void insertRegistryAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(RATTACH, RATTACH.ATTACH_DATE,
				RATTACH.CATEGORY, RATTACH.CREATION_DATE, RATTACH.CREATION_USER,
				RATTACH.DATA, RATTACH.DESCRIPTION, RATTACH.DOMAIN,
				RATTACH.DPARENT_ID, RATTACH.DRIVE_ID, RATTACH.MIMETYPE,
				RATTACH.MODIFICATION_DATE, RATTACH.MODIFICATION_USER,
				RATTACH.REGISTRY, RATTACH.SCOPE, RATTACH.SECURITY_LEVEL,
				RATTACH.TYPE)
		.values(new Date(attach.getDate().getTime()), attach.getCategory(), 
				new Timestamp(attach.getCreationDate().getTime()), attach.getCreationUser(), 
				attach.getData(), attach.getDescription(), attach.getDomain().getId(), 
				attach.getDparentId(), attach.getDriveId(), (byte)attach.getMimeType().ordinal(),
				new Timestamp(attach.getModificationDate().getTime()),attach.getModificationUser(),
				attach.getAttachModule(), attach.getScope(),
				attach.getConfidential()?(byte)1:(byte)0, (byte) attach.getType())
		.execute();
	}
	
	public static void insertSepeAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		ctx.getDslContext().insertInto(SEPE_BATCH_ATTACH, SEPE_BATCH_ATTACH.ATTACH_DATE,
				SEPE_BATCH_ATTACH.DATA, SEPE_BATCH_ATTACH.DESCRIPTION, SEPE_BATCH_ATTACH.DOMAIN,
				SEPE_BATCH_ATTACH.DRIVEID, SEPE_BATCH_ATTACH.MIMETYPE,SEPE_BATCH_ATTACH.SCOPE,
				SEPE_BATCH_ATTACH.SOURCE_BATCH, SEPE_BATCH_ATTACH.SOURCE_TYPE,
				SEPE_BATCH_ATTACH.TYPE)
		.values(new Date(attach.getDate().getTime()), attach.getData(), attach.getDescription(),
				attach.getDomain().getId(), attach.getDriveId(), (byte) attach.getMimeType().ordinal(),
				attach.getScope(), attach.getSourceBatch(), (byte) attach.getSourceType(),
				(byte) attach.getType())
		.execute();
	}
	
	//-------------------- UPDATES
	
	public static void updateContractAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(attach.getDate().getTime()))
			.set(CONTRACT_ATTACH.CONTRACT, attach.getAttachModule())
			.set(CONTRACT_ATTACH.DATA, attach.getData())
			.set(CONTRACT_ATTACH.DESCRIPTION, attach.getDescription())
			.set(CONTRACT_ATTACH.DOMAIN, attach.getDomain().getId())
			.set(CONTRACT_ATTACH.DRIVEID, attach.getDriveId())
			.set(CONTRACT_ATTACH.MIMETYPE, (byte) attach.getMimeType().ordinal())
			.set(CONTRACT_ATTACH.SCOPE, attach.getScope())
			.set(CONTRACT_ATTACH.SECURITY_LEVEL, attach.getConfidential()?(byte)1:(byte)0)
			.set(CONTRACT_ATTACH.TYPE, (byte) attach.getType())
		.where(CONTRACT_ATTACH.ID.eq(attach.getId()))
		.execute();
	}
	
	public static void updateItemAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(IATTACH)
			.set(IATTACH.DATA,attach.getData())
			.set(IATTACH.DESCRIPTION, attach.getDescription())
			.set(IATTACH.DOMAIN, attach.getDomain().getId())
			.set(IATTACH.DRIVEID, attach.getDriveId())
			.set(IATTACH.ITEM, attach.getAttachModule())
			.set(IATTACH.MIMETYPE, (byte) attach.getMimeType().ordinal())
			.set(IATTACH.TYPE, (byte) attach.getType())
		.where(IATTACH.ID.eq(attach.getId()))
		.execute();
	}

	public static void updateInvoiceAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(INVOICE_ATTACH)
			.set(INVOICE_ATTACH.ATTACH_DATE, new Date(attach.getDate().getTime()))
			.set(INVOICE_ATTACH.DATA, attach.getData())
			.set(INVOICE_ATTACH.DESCRIPTION, attach.getDescription())
			.set(INVOICE_ATTACH.DOMAIN, attach.getDomain().getId())
			.set(INVOICE_ATTACH.DRIVEID, attach.getDriveId())
			.set(INVOICE_ATTACH.INVOICE, attach.getAttachModule())
			.set(INVOICE_ATTACH.MIMETYPE, (byte) attach.getMimeType().ordinal())
			.set(INVOICE_ATTACH.TYPE, (byte) attach.getType())
		.where(INVOICE_ATTACH.ID.eq(attach.getId()))
		.execute();
	}

	public static void updateOfferAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(OFFER_ATTACH)
			.set(OFFER_ATTACH.DATA, attach.getData())
			.set(OFFER_ATTACH.DESCRIPTION, attach.getDescription())
			.set(OFFER_ATTACH.DOMAIN, attach.getDomain().getId())
			.set(OFFER_ATTACH.DRIVEID, attach.getDriveId())
			.set(OFFER_ATTACH.MIMETYPE, (byte) attach.getMimeType().ordinal())
			.set(OFFER_ATTACH.OFFER, attach.getAttachModule())
		.where(OFFER_ATTACH.ID.eq(attach.getId()))
		.execute();
	}

	public static void updatePayrollAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(PAYROLL_BATCH_ATTACH)
			.set(PAYROLL_BATCH_ATTACH.ATTACH_DATE,  new Date(attach.getDate().getTime()))
			.set(PAYROLL_BATCH_ATTACH.DATA, attach.getData())
			.set(PAYROLL_BATCH_ATTACH.DESCRIPTION, attach.getDescription())
			.set(PAYROLL_BATCH_ATTACH.DOMAIN, attach.getDomain().getId())
			.set(PAYROLL_BATCH_ATTACH.DRIVEID, attach.getDriveId())
			.set(PAYROLL_BATCH_ATTACH.MIMETYPE, (byte) attach.getMimeType().ordinal())
			.set(PAYROLL_BATCH_ATTACH.SCOPE, attach.getScope())
			.set(PAYROLL_BATCH_ATTACH.SOURCE_BATCH, attach.getSourceBatch())
			.set(PAYROLL_BATCH_ATTACH.SOURCE_TYPE, (byte) attach.getSourceType())
			.set(PAYROLL_BATCH_ATTACH.TYPE, (byte) attach.getType())
		.where(PAYROLL_BATCH_ATTACH.ID.eq(attach.getId()))
		.execute();
	}

	public static void updateProjectAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(PROJECT_ATTACH)
				.set(PROJECT_ATTACH.ATTACH_DATE, new Date(attach.getDate().getTime()))
				.set(PROJECT_ATTACH.DATA, attach.getData())
				.set(PROJECT_ATTACH.DESCRIPTION, attach.getDescription())
				.set(PROJECT_ATTACH.DOMAIN, attach.getDomain().getId())
				.set(PROJECT_ATTACH.DRIVEID, attach.getDriveId()) 	
				.set(PROJECT_ATTACH.MIMETYPE, (byte)attach.getMimeType().ordinal())
				.set(PROJECT_ATTACH.PROJECT, attach.getAttachModule())
				.set(PROJECT_ATTACH.SECURITY_LEVEL, attach.getConfidential()?(byte)1:(byte)0)
		.where(PROJECT_ATTACH.ID.eq(attach.getId()))
		.execute();
	}

	public static void updateRegistryAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(RATTACH)
			.set(RATTACH.ATTACH_DATE, new Date(attach.getDate().getTime()))
			.set(RATTACH.CATEGORY,attach.getCategory())
			.set(RATTACH.CREATION_DATE, new Timestamp(attach.getCreationDate().getTime()))
			.set(RATTACH.CREATION_USER, attach.getCreationUser())
			.set(RATTACH.DATA, attach.getData())
			.set(RATTACH.DESCRIPTION, attach.getDescription())
			.set(RATTACH.DOMAIN, attach.getDomain().getId())
			.set(RATTACH.DPARENT_ID, attach.getDparentId())
			.set(RATTACH.DRIVE_ID, attach.getDriveId())
			.set(RATTACH.MIMETYPE, (byte) attach.getMimeType().ordinal())
			.set(RATTACH.MODIFICATION_DATE, new Timestamp(attach.getModificationDate().getTime()))
			.set(RATTACH.MODIFICATION_USER, attach.getModificationUser())
			.set(RATTACH.REGISTRY, attach.getAttachModule())
			.set(RATTACH.SCOPE, attach.getScope())
			.set(RATTACH.SECURITY_LEVEL,attach.getConfidential()?(byte)1:(byte)0)
			.set(RATTACH.TYPE, (byte) attach.getType())
		.where(RATTACH.ID.eq(attach.getId()))
		.execute();
	}

	public static void updateSepeAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(SEPE_BATCH_ATTACH)
			.set(SEPE_BATCH_ATTACH.ATTACH_DATE, new Date(attach.getDate().getTime()))
			.set(SEPE_BATCH_ATTACH.DATA, attach.getData())
			.set(SEPE_BATCH_ATTACH.DESCRIPTION, attach.getDescription())
			.set(SEPE_BATCH_ATTACH.DOMAIN, attach.getDomain().getId())
			.set(SEPE_BATCH_ATTACH.DRIVEID, attach.getDriveId()) 	
			.set(SEPE_BATCH_ATTACH.MIMETYPE, (byte)attach.getMimeType().ordinal())
			.set(SEPE_BATCH_ATTACH.SCOPE, attach.getScope())
			.set(SEPE_BATCH_ATTACH.SOURCE_BATCH, attach.getSourceBatch())
			.set(SEPE_BATCH_ATTACH.SOURCE_TYPE, (byte) attach.getSourceType())
			.set(SEPE_BATCH_ATTACH.TYPE, (byte) attach.getType())
		.where(SEPE_BATCH_ATTACH.ID.eq(attach.getId()))
		.execute();
	}
	
	//-------------------- DELETES
	
	public static void deleteContractAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(CONTRACT_ATTACH).where(condition).execute();
	}
	
	public static void deleteItemAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(IATTACH).where(condition).execute();
	}

	public static void deleteInvoiceAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(INVOICE_ATTACH).where(condition).execute();
	}

	public static void deleteOfferAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(OFFER_ATTACH).where(condition).execute();
	}

	public static void deletePayrollAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(PAYROLL_BATCH_ATTACH).where(condition).execute();
	}

	public static void deleteProjectAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(PROJECT_ATTACH).where(condition).execute();
	}

	public static void deleteRegistryAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(RATTACH).where(condition).execute();
	}

	public static void deleteSepeAttach(AONContext ctx, Condition condition){
		ctx.getDslContext().delete(SEPE_BATCH_ATTACH).where(condition).execute();
	}
	
}
