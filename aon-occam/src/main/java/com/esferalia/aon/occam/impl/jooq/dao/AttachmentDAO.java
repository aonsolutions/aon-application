package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record7;

import com.esferalia.aon.jooq.tables.records.ContractAttachRecord;
import com.esferalia.aon.jooq.tables.records.IattachRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceAttachRecord;
import com.esferalia.aon.jooq.tables.records.OfferAttachRecord;
import com.esferalia.aon.jooq.tables.records.PayrollBatchAttachRecord;
import com.esferalia.aon.jooq.tables.records.ProjectAttachRecord;
import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.jooq.tables.records.SepeBatchAttachRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AttachFilter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachQueryProperties;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;


public class AttachmentDAO {
	
	private static final AttachPropertiesDAO.RattachPropertiesDAO RATTACH_PROPERTIES = new AttachPropertiesDAO.RattachPropertiesDAO();
	private static final AttachPropertiesDAO.ContractAttachPropertiesDAO CONTRACT_ATTACH_PROPERTIES = new AttachPropertiesDAO.ContractAttachPropertiesDAO();
	private static final AttachPropertiesDAO.IattachPropertiesDAO IATTACH_PROPERTIES = new AttachPropertiesDAO.IattachPropertiesDAO();
	private static final AttachPropertiesDAO.InvoiceAttachPropertiesDAO INVOICE_ATTACH_PROPERTIES = new AttachPropertiesDAO.InvoiceAttachPropertiesDAO();
	private static final AttachPropertiesDAO.OfferAttachPropertiesDAO OFFER_ATTACH_PROPERTIES = new AttachPropertiesDAO.OfferAttachPropertiesDAO();
	private static final AttachPropertiesDAO.PayrollAttachPropertiesDAO PAYROLL_ATTACH_PROPERTIES = new AttachPropertiesDAO.PayrollAttachPropertiesDAO();
	private static final AttachPropertiesDAO.ProjectAttachPropertiesDAO PROJECT_ATTACH_PROPERTIES = new AttachPropertiesDAO.ProjectAttachPropertiesDAO();
	private static final AttachPropertiesDAO.SepeAttachPropertiesDAO SEPE_ATTACH_PROPERTIES = new AttachPropertiesDAO.SepeAttachPropertiesDAO();
	
	//-------------------- GETS 

	public static Attach getRegistryAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(RATTACH).where(RATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(RATTACH).stream().map(new FullRattachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static Attach getContractAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(CONTRACT_ATTACH).where(CONTRACT_ATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(CONTRACT_ATTACH).stream().map(new FullContractAttachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static Attach getInvoiceAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(INVOICE_ATTACH).where(INVOICE_ATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(INVOICE_ATTACH).stream().map(new FullInvoiceAttachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static Attach getItemAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(IATTACH).where(IATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(IATTACH).stream().map(new FullItemAttachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static Attach getOfferAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(OFFER_ATTACH).where(OFFER_ATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(OFFER_ATTACH).stream().map(new FullOfferAttachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static Attach getPayrollAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(PAYROLL_BATCH_ATTACH).where(PAYROLL_ATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(PAYROLL_BATCH_ATTACH).stream().map(new FullPayrollAttachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static Attach getProjectAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(PROJECT_ATTACH).where(PROJECT_ATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(PROJECT_ATTACH).stream().map(new FullProjectAttachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static Attach getSepeAttach(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
				.select().from(SEPE_BATCH_ATTACH).where(SEPE_ATTACH_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(SEPE_BATCH_ATTACH).stream().map(new FullSepeAttachFiller(ctx))
				.findFirst().orElse(null);
	}
	
	public static LinkedList<Attach> getRegistryAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(RATTACH).where(RATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(RATTACH).stream().map(new FullRattachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getContractAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(CONTRACT_ATTACH).where(CONTRACT_ATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(CONTRACT_ATTACH).stream().map(new FullContractAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getInvoiceAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(INVOICE_ATTACH).where(INVOICE_ATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(INVOICE_ATTACH).stream().map(new FullInvoiceAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getItemAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(IATTACH).where(IATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(IATTACH).stream().map(new FullItemAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getOfferAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(OFFER_ATTACH).where(OFFER_ATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(OFFER_ATTACH).stream().map(new FullOfferAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getPayrollAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(PAYROLL_BATCH_ATTACH).where(PAYROLL_ATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(PAYROLL_BATCH_ATTACH).stream().map(new FullPayrollAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getProjectAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(PROJECT_ATTACH).where(PROJECT_ATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(PROJECT_ATTACH).stream().map(new FullProjectAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getSepeAttachList(AONContext ctx, AttachFilter filter){	
		return ctx.getDslContext()
			.select().from(SEPE_BATCH_ATTACH).where(SEPE_ATTACH_PROPERTIES.getConditions(filter))
			.fetchInto(SEPE_BATCH_ATTACH).stream().map(new FullSepeAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	public static LinkedList<Attach> getRegistryAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(RATTACH).where(RATTACH_PROPERTIES.getConditions(filter))
			.orderBy(RATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(RATTACH).stream().map(new FullRattachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getContractAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(CONTRACT_ATTACH).where(CONTRACT_ATTACH_PROPERTIES.getConditions(filter))
			.orderBy(CONTRACT_ATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(CONTRACT_ATTACH).stream().map(new FullContractAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getInvoiceAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(INVOICE_ATTACH).where(INVOICE_ATTACH_PROPERTIES.getConditions(filter))
			.orderBy(INVOICE_ATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(INVOICE_ATTACH).stream().map(new FullInvoiceAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getItemAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(IATTACH).where(IATTACH_PROPERTIES.getConditions(filter))
			.orderBy(IATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(IATTACH).stream().map(new FullItemAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getOfferAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(OFFER_ATTACH).where(OFFER_ATTACH_PROPERTIES.getConditions(filter))
			.orderBy(OFFER_ATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(OFFER_ATTACH).stream().map(new FullOfferAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getPayrollAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(PAYROLL_BATCH_ATTACH).where(PAYROLL_ATTACH_PROPERTIES.getConditions(filter))
			.orderBy(PAYROLL_BATCH_ATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(PAYROLL_BATCH_ATTACH).stream().map(new FullPayrollAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getProjectAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(PROJECT_ATTACH).where(PROJECT_ATTACH_PROPERTIES.getConditions(filter))
			.orderBy(PROJECT_ATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(PROJECT_ATTACH).stream().map(new FullProjectAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Attach> getSepeAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp){	
		return ctx.getDslContext()
			.select().from(SEPE_BATCH_ATTACH).where(SEPE_ATTACH_PROPERTIES.getConditions(filter))
			.orderBy(SEPE_BATCH_ATTACH.ID)
			.limit(aqp.getLimit())
			.fetchInto(SEPE_BATCH_ATTACH).stream().map(new FullSepeAttachFiller(ctx))
			.collect(Collectors.toCollection(LinkedList::new));
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
	
	public static Integer insertContractAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(CONTRACT_ATTACH, CONTRACT_ATTACH.ATTACH_DATE,
				CONTRACT_ATTACH.CONTRACT, CONTRACT_ATTACH.DATA, CONTRACT_ATTACH.DESCRIPTION,
				CONTRACT_ATTACH.DOMAIN, CONTRACT_ATTACH.DRIVEID, CONTRACT_ATTACH.MIMETYPE,
				CONTRACT_ATTACH.SCOPE, CONTRACT_ATTACH.SECURITY_LEVEL, CONTRACT_ATTACH.TYPE)
		.values(new Timestamp(attach.getDate().getTime()), attach.getAttachModule(),
				attach.getData(), attach.getDescription(), attach.getDomain().getId(),
				attach.getDriveId(), (byte) attach.getMimeType().ordinal(),attach.getScope(),
				attach.getConfidential()?(byte)1:(byte)0, (byte) attach.getType())
		.returning(CONTRACT_ATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertItemAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(IATTACH, IATTACH.DATA, IATTACH.DESCRIPTION,
				IATTACH.DOMAIN, IATTACH.DRIVEID, IATTACH.ITEM, IATTACH.MIMETYPE,
				IATTACH.TYPE)
		.values(attach.getData(), attach.getDescription(), attach.getDomain().getId(),
				attach.getDriveId(), attach.getAttachModule(),
				(byte) attach.getMimeType().ordinal(), (byte) attach.getType())
		.returning(IATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertInvoiceAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(INVOICE_ATTACH, INVOICE_ATTACH.ATTACH_DATE,
				INVOICE_ATTACH.DATA, INVOICE_ATTACH.DESCRIPTION, INVOICE_ATTACH.DOMAIN,
				INVOICE_ATTACH.DRIVEID, INVOICE_ATTACH.INVOICE, INVOICE_ATTACH.MIMETYPE,
				INVOICE_ATTACH.TYPE)
		.values(new Date(attach.getDate().getTime()), attach.getData(), attach.getDescription(),
				attach.getDomain().getId(), attach.getDriveId(), attach.getAttachModule(),
				(byte) attach.getMimeType().ordinal(), (byte) attach.getType())
		.returning(INVOICE_ATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertOfferAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(OFFER_ATTACH, OFFER_ATTACH.DATA, OFFER_ATTACH.DESCRIPTION,
				OFFER_ATTACH.DOMAIN, OFFER_ATTACH.DRIVEID, OFFER_ATTACH.MIMETYPE,
				OFFER_ATTACH.OFFER)
		.values(attach.getData(), attach.getDescription(), attach.getDomain().getId(),
				attach.getDriveId(), (byte) attach.getMimeType().ordinal(),
				attach.getAttachModule())
		.returning(OFFER_ATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertPayrollAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(PAYROLL_BATCH_ATTACH, PAYROLL_BATCH_ATTACH.ATTACH_DATE,
				PAYROLL_BATCH_ATTACH.DATA, PAYROLL_BATCH_ATTACH.DESCRIPTION,
				PAYROLL_BATCH_ATTACH.DOMAIN, PAYROLL_BATCH_ATTACH.DRIVEID,
				PAYROLL_BATCH_ATTACH.MIMETYPE, PAYROLL_BATCH_ATTACH.SCOPE,
				PAYROLL_BATCH_ATTACH.SOURCE_BATCH, PAYROLL_BATCH_ATTACH.SOURCE_TYPE,
				PAYROLL_BATCH_ATTACH.TYPE)
		.values(new Date(attach.getDate().getTime()), attach.getData(), attach.getDescription(),
				attach.getDomain().getId(), attach.getDriveId(), (byte) attach.getMimeType().ordinal(),
				attach.getScope(), attach.getSourceBatch(), (byte) attach.getSourceType(),
				(byte) attach.getType())
		.returning(PAYROLL_BATCH_ATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertProjectAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(PROJECT_ATTACH, PROJECT_ATTACH.ATTACH_DATE, 
				PROJECT_ATTACH.DATA, PROJECT_ATTACH.DESCRIPTION, 
				PROJECT_ATTACH.DOMAIN, PROJECT_ATTACH.DRIVEID, 
				PROJECT_ATTACH.MIMETYPE, PROJECT_ATTACH.PROJECT, 
				PROJECT_ATTACH.SECURITY_LEVEL)
		.values(new Date(attach.getDate().getTime()), attach.getData(), 
				attach.getDescription(), attach.getDomain().getId(), 
				attach.getDriveId(), (byte)attach.getMimeType().ordinal(),
				attach.getAttachModule(), attach.getConfidential()?(byte)1:(byte)0)
		.returning(PROJECT_ATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertRegistryAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(RATTACH, RATTACH.ATTACH_DATE,
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
		.returning(RATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertSepeAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(SEPE_BATCH_ATTACH, SEPE_BATCH_ATTACH.ATTACH_DATE,
				SEPE_BATCH_ATTACH.DATA, SEPE_BATCH_ATTACH.DESCRIPTION, SEPE_BATCH_ATTACH.DOMAIN,
				SEPE_BATCH_ATTACH.DRIVEID, SEPE_BATCH_ATTACH.MIMETYPE,SEPE_BATCH_ATTACH.SCOPE,
				SEPE_BATCH_ATTACH.SOURCE_BATCH, SEPE_BATCH_ATTACH.SOURCE_TYPE,
				SEPE_BATCH_ATTACH.TYPE)
		.values(new Date(attach.getDate().getTime()), attach.getData(), attach.getDescription(),
				attach.getDomain().getId(), attach.getDriveId(), (byte) attach.getMimeType().ordinal(),
				attach.getScope(), attach.getSourceBatch(), (byte) attach.getSourceType(),
				(byte) attach.getType())
		.returning(SEPE_BATCH_ATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertRegistryAttachTag(AONContext ctx, Integer rattachId, Integer tagId){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(RATTACH_TAG, RATTACH_TAG.DOMAIN, RATTACH_TAG.RATTACH, RATTACH_TAG.TAG)
				.values(ctx.getDomainId(), rattachId, tagId).returning(RATTACH_TAG.ID).fetchOne().getId();
	}
	
	//-------------------- FULL UPDATE
	
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

	//-------------------- DATA UPDATE
	
		public static void updateContractAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DATA, attach.getData())
			.where(CONTRACT_ATTACH.ID.eq(attach.getId()))
			.execute();
		}
		
		public static void updateItemAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(IATTACH)
				.set(IATTACH.DATA,attach.getData())
			.where(IATTACH.ID.eq(attach.getId()))
			.execute();
		}

		public static void updateInvoiceAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(INVOICE_ATTACH)
				.set(INVOICE_ATTACH.DATA, attach.getData())
			.where(INVOICE_ATTACH.ID.eq(attach.getId()))
			.execute();
		}

		public static void updateOfferAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(OFFER_ATTACH)
				.set(OFFER_ATTACH.DATA, attach.getData())
			.where(OFFER_ATTACH.ID.eq(attach.getId()))
			.execute();
		}

		public static void updatePayrollAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(PAYROLL_BATCH_ATTACH)
				.set(PAYROLL_BATCH_ATTACH.DATA, attach.getData())
			.where(PAYROLL_BATCH_ATTACH.ID.eq(attach.getId()))
			.execute();
		}

		public static void updateProjectAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(PROJECT_ATTACH)
					.set(PROJECT_ATTACH.DATA, attach.getData())
			.where(PROJECT_ATTACH.ID.eq(attach.getId()))
			.execute();
		}

		public static void updateRegistryAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(RATTACH)
				.set(RATTACH.DATA, attach.getData())
			.where(RATTACH.ID.eq(attach.getId()))
			.execute();
		}

		public static void updateSepeAttachData(AONContext ctx, Attach attach){
			ctx.getDslContext().update(SEPE_BATCH_ATTACH)
				.set(SEPE_BATCH_ATTACH.DATA, attach.getData())
			.where(SEPE_BATCH_ATTACH.ID.eq(attach.getId()))
			.execute();
		}
	
	//-------------------- DELETES
	
	public static void deleteContractAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(CONTRACT_ATTACH).where(CONTRACT_ATTACH_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static void deleteItemAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(IATTACH).where(IATTACH_PROPERTIES.getConditions(filter)).execute();
	}

	public static void deleteInvoiceAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(INVOICE_ATTACH).where(INVOICE_ATTACH_PROPERTIES.getConditions(filter)).execute();
	}

	public static void deleteOfferAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(OFFER_ATTACH).where(OFFER_ATTACH_PROPERTIES.getConditions(filter)).execute();
	}

	public static void deletePayrollAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(PAYROLL_BATCH_ATTACH).where(PAYROLL_ATTACH_PROPERTIES.getConditions(filter)).execute();
	}

	public static void deleteProjectAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(PROJECT_ATTACH).where(PROJECT_ATTACH_PROPERTIES.getConditions(filter)).execute();
	}

	public static void deleteRegistryAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(RATTACH).where(RATTACH_PROPERTIES.getConditions(filter)).execute();
	}

	public static void deleteSepeAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(SEPE_BATCH_ATTACH).where(SEPE_ATTACH_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static void deleteRegistryAttachTag(AONContext ctx, Integer rattachId){
		ctx.getDslContext().delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(rattachId)).execute();
	}

	private static class FullRattachFiller implements Function<RattachRecord, Attach> {
		AONContext ctx;
		public FullRattachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Attach apply(RattachRecord r) {
			return new Attach().setAttachModule(r.getRegistry())
							.setAttachType(AttachType.REGISTRY)
							.setCategory(r.getCategory())
							.setConfidential(r.getSecurityLevel().equals(1))
							.setCreationDate(r.getCreationDate())
							.setCreationUser(r.getCreationUser())
							.setData(r.getData())
							.setDate(r.getAttachDate())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain())
									.setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDparentId(r.getDparentId())
							.setDriveId(r.getDriveId())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setModificationDate(r.getModificationDate())
							.setModificationUser(r.getModificationUser())
							.setScope(r.getScope())
							.setType(r.getType());			
		}
	}
	
	private static class FullContractAttachFiller implements Function<ContractAttachRecord, Attach> {
		AONContext ctx;
		public FullContractAttachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Attach apply(ContractAttachRecord r) {
			return new Attach().setAttachModule(r.getContract())
							.setAttachType(AttachType.CONTRACT)
							.setConfidential(r.getSecurityLevel() == 1)
							.setData(r.getData())
							.setDate(r.getAttachDate())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain())
									.setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setScope(r.getScope())
							.setType(r.getType());
		}
	}
	
	private static class FullInvoiceAttachFiller implements Function<InvoiceAttachRecord, Attach> {
		AONContext ctx;
		public FullInvoiceAttachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Attach apply(InvoiceAttachRecord r) {
			return new Attach().setAttachModule(r.getInvoice())
							.setAttachType(AttachType.INVOICE)
							.setData(r.getData())
							.setDate(r.getAttachDate())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain())
									.setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setType(r.getType());		
		}
	}
	
	private static class FullItemAttachFiller implements Function<IattachRecord, Attach> {
		AONContext ctx;
		public FullItemAttachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Attach apply(IattachRecord r) {
			return new Attach().setAttachModule(r.getItem())
							.setAttachType(AttachType.ITEM)
							.setData(r.getData())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain())
									.setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setType(r.getType());		
		}
	}
	
	private static class FullOfferAttachFiller implements Function<OfferAttachRecord, Attach> {
		AONContext ctx;
		public FullOfferAttachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Attach apply(OfferAttachRecord r) {
			return new Attach().setAttachModule(r.getOffer())
							.setAttachType(AttachType.OFFER)
							.setData(r.getData())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain())
									.setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()]);		
		}
	}
	
	private static class FullPayrollAttachFiller implements Function<PayrollBatchAttachRecord, Attach> {
		AONContext ctx;
		public FullPayrollAttachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Attach apply(PayrollBatchAttachRecord r) {
			return new Attach().setAttachType(AttachType.PAYROLL)
							.setData(r.getData())
							.setDate(r.getAttachDate())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain())
									.setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setScope(r.getScope())
							.setSourceBatch(r.getSourceBatch())
							.setSourceType(r.getSourceType())
							.setType(r.getType());		
		}
	}
	
	private static class FullProjectAttachFiller implements Function<ProjectAttachRecord, Attach> {
		AONContext ctx;
		public FullProjectAttachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		@Override
		public Attach apply(ProjectAttachRecord r) {
			return new Attach().setAttachModule(r.getProject())
							.setAttachType(AttachType.PROJECT)
							.setConfidential(r.getSecurityLevel() == 1)
							.setData(r.getData())
							.setDate(r.getAttachDate())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain()).setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()]);	
		}
	}
	
	private static class FullSepeAttachFiller implements Function<SepeBatchAttachRecord, Attach> {
		AONContext ctx;
		public FullSepeAttachFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Attach apply(SepeBatchAttachRecord r) {
			return new Attach().setAttachType(AttachType.SEPE)
							.setData(r.getData())
							.setDate(r.getAttachDate())
							.setDescription(r.getDescription())
							.setDomain(new Domain().setId(r.getDomain())
									.setName(SecurityDAO.getDomain(ctx, r.getDomain()).getName()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setScope(r.getScope())
							.setSourceBatch(r.getSourceBatch())
							.setSourceType(r.getSourceType());		
		}
	}
	
	
	
	
	
}
