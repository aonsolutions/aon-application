package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
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
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.SelectConditionStep;
import org.jooq.SelectField;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.records.ContractAttachRecord;
import com.esferalia.aon.jooq.tables.records.IattachRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceAttachRecord;
import com.esferalia.aon.jooq.tables.records.OfferAttachRecord;
import com.esferalia.aon.jooq.tables.records.PayrollBatchAttachRecord;
import com.esferalia.aon.jooq.tables.records.ProjectAttachRecord;
import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.jooq.tables.records.SepeBatchAttachRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;


public class AttachmentDAO {
	
	private static final AttachPropertiesDAO.RattachPropertiesDAO RATTACH_PROPERTIES = new AttachPropertiesDAO.RattachPropertiesDAO();
	private static final AttachPropertiesDAO.ContractAttachPropertiesDAO CONTRACT_ATTACH_PROPERTIES = new AttachPropertiesDAO.ContractAttachPropertiesDAO();
	private static final AttachPropertiesDAO.IattachPropertiesDAO IATTACH_PROPERTIES = new AttachPropertiesDAO.IattachPropertiesDAO();
	private static final AttachPropertiesDAO.InvoiceAttachPropertiesDAO INVOICE_ATTACH_PROPERTIES = new AttachPropertiesDAO.InvoiceAttachPropertiesDAO();
	private static final AttachPropertiesDAO.OfferAttachPropertiesDAO OFFER_ATTACH_PROPERTIES = new AttachPropertiesDAO.OfferAttachPropertiesDAO();
	private static final AttachPropertiesDAO.PayrollAttachPropertiesDAO PAYROLL_ATTACH_PROPERTIES = new AttachPropertiesDAO.PayrollAttachPropertiesDAO();
	private static final AttachPropertiesDAO.ProjectAttachPropertiesDAO PROJECT_ATTACH_PROPERTIES = new AttachPropertiesDAO.ProjectAttachPropertiesDAO();
	private static final AttachPropertiesDAO.SepeAttachPropertiesDAO SEPE_ATTACH_PROPERTIES = new AttachPropertiesDAO.SepeAttachPropertiesDAO();
	private static final AttachPropertiesDAO.DataAttachPropertiesDAO DATA_ATTACH_PROPERTIES = new AttachPropertiesDAO.DataAttachPropertiesDAO();

	//-------------------- GETS 
	
	// WD -> Without Data
	@SuppressWarnings("rawtypes")
	private static SelectField[] rattachWD = {RATTACH.ID, RATTACH.DOMAIN, RATTACH.REGISTRY, RATTACH.MIMETYPE, RATTACH.DESCRIPTION,
		RATTACH.TYPE, RATTACH.SCOPE, RATTACH.SECURITY_LEVEL, RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID, RATTACH.DPARENT_ID, RATTACH.CATEGORY,
		RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE};

	@SuppressWarnings("rawtypes")
	private static SelectField[] contractAttachWD = {CONTRACT_ATTACH.ID, CONTRACT_ATTACH.DOMAIN, CONTRACT_ATTACH.CONTRACT,
		CONTRACT_ATTACH.MIMETYPE, CONTRACT_ATTACH.DESCRIPTION, CONTRACT_ATTACH.TYPE, CONTRACT_ATTACH.SCOPE,
		CONTRACT_ATTACH.SECURITY_LEVEL, CONTRACT_ATTACH.ATTACH_DATE, CONTRACT_ATTACH.DRIVEID};
	
	@SuppressWarnings("rawtypes")
	private static SelectField[] invoiceAttachWD = {INVOICE_ATTACH.ID, INVOICE_ATTACH.DOMAIN, INVOICE_ATTACH.INVOICE,
			INVOICE_ATTACH.MIMETYPE, INVOICE_ATTACH.DESCRIPTION, INVOICE_ATTACH.TYPE, INVOICE_ATTACH.ATTACH_DATE, INVOICE_ATTACH.DRIVEID};
	
	@SuppressWarnings("rawtypes")
	private static SelectField[] iAttachWD = {IATTACH.ID, IATTACH.DOMAIN, IATTACH.ITEM,
			IATTACH.MIMETYPE, IATTACH.DESCRIPTION, IATTACH.TYPE, IATTACH.DRIVEID};
	
	@SuppressWarnings("rawtypes")
	private static SelectField[] offerAttachWD = {OFFER_ATTACH.ID, OFFER_ATTACH.DOMAIN, OFFER_ATTACH.OFFER,
			OFFER_ATTACH.MIMETYPE, OFFER_ATTACH.DESCRIPTION, OFFER_ATTACH.DRIVEID};
	
	@SuppressWarnings("rawtypes")
	private static SelectField[] payrollAttachWD = {PAYROLL_BATCH_ATTACH.ID, PAYROLL_BATCH_ATTACH.DOMAIN, PAYROLL_BATCH_ATTACH.SOURCE_BATCH,
			PAYROLL_BATCH_ATTACH.SOURCE_TYPE,PAYROLL_BATCH_ATTACH.MIMETYPE, PAYROLL_BATCH_ATTACH.DESCRIPTION, PAYROLL_BATCH_ATTACH.TYPE,
			PAYROLL_BATCH_ATTACH.SCOPE, PAYROLL_BATCH_ATTACH.ATTACH_DATE, PAYROLL_BATCH_ATTACH.DRIVEID};

	@SuppressWarnings("rawtypes")
	private static SelectField[] projectAttachWD = {PROJECT_ATTACH.ID, PROJECT_ATTACH.DOMAIN, PROJECT_ATTACH.PROJECT,
			PROJECT_ATTACH.MIMETYPE, PROJECT_ATTACH.DESCRIPTION, PROJECT_ATTACH.SECURITY_LEVEL, PROJECT_ATTACH.ATTACH_DATE,
			PROJECT_ATTACH.ATTACH_TYPE, PROJECT_ATTACH.DRIVEID, PROJECT_ATTACH.CREATION_DATE, PROJECT_ATTACH.CREATION_USER,
			PROJECT_ATTACH.MODIFICATION_DATE, PROJECT_ATTACH.MODIFICATION_USER};
	
	@SuppressWarnings("rawtypes")
	private static SelectField[] sepeAttachWD = {SEPE_BATCH_ATTACH.ID, SEPE_BATCH_ATTACH.DOMAIN, SEPE_BATCH_ATTACH.SOURCE_BATCH,
			SEPE_BATCH_ATTACH.SOURCE_TYPE,SEPE_BATCH_ATTACH.MIMETYPE, SEPE_BATCH_ATTACH.DESCRIPTION, SEPE_BATCH_ATTACH.TYPE,
			SEPE_BATCH_ATTACH.SCOPE, SEPE_BATCH_ATTACH.ATTACH_DATE, SEPE_BATCH_ATTACH.DRIVEID};
	
	@SuppressWarnings("rawtypes")
	private static SelectField[] dataAttachWD = {DATA_ATTACH.ID, DATA_ATTACH.DOMAIN, DATA_ATTACH.SOURCE,
			DATA_ATTACH.SOURCE_ID,DATA_ATTACH.MIMETYPE,/* DATA_ATTACH.DESCRIPTION,*/ DATA_ATTACH.TYPE,
			DATA_ATTACH.DRIVE_ID, DATA_ATTACH.CREATION_DATE, DATA_ATTACH.CREATION_USER,
			DATA_ATTACH.MODIFICATION_DATE, DATA_ATTACH.MODIFICATION_USER};

	public static Stream<Attach> getRegistryAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){		
		SelectJoinStep<Record> select = ctx.getDslContext().select(rattachWD).from(RATTACH);
		if(withData) select = ctx.getDslContext().select().from(RATTACH);
		return RATTACH_PROPERTIES.build(select, filter).fetchInto(RATTACH).stream().map(new FullRattachFiller(ctx));		
	}
	
	public static Stream<Attach> getContractAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){
		SelectJoinStep<Record> select = ctx.getDslContext().select(contractAttachWD).from(CONTRACT_ATTACH);
		if(withData) select = ctx.getDslContext().select().from(CONTRACT_ATTACH);
		return CONTRACT_ATTACH_PROPERTIES.build(select, filter).fetchInto(CONTRACT_ATTACH).stream().map(new FullContractAttachFiller(ctx));		
	}
	
	public static Stream<Attach> getInvoiceAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){	
		SelectJoinStep<Record> select = ctx.getDslContext().select(invoiceAttachWD).from(INVOICE_ATTACH);
		if(withData) select = ctx.getDslContext().select().from(INVOICE_ATTACH);
		return INVOICE_ATTACH_PROPERTIES.build(select, filter).fetchInto(INVOICE_ATTACH).stream().map(new FullInvoiceAttachFiller(ctx));
	}
	
	public static Stream<Attach> getItemAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){	
		SelectJoinStep<Record> select = ctx.getDslContext().select(iAttachWD).from(IATTACH);
		if(withData) select = ctx.getDslContext().select().from(IATTACH);
		return IATTACH_PROPERTIES.build(select, filter).fetchInto(IATTACH).stream().map(new FullItemAttachFiller(ctx));
	}
	
	public static Stream<Attach> getOfferAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){	
		SelectJoinStep<Record> select = ctx.getDslContext().select(offerAttachWD).from(OFFER_ATTACH);
		if(withData) select = ctx.getDslContext().select().from(OFFER_ATTACH);
		return OFFER_ATTACH_PROPERTIES.build(select, filter).fetchInto(OFFER_ATTACH).stream().map(new FullOfferAttachFiller(ctx));
	}
	
	public static Stream<Attach> getPayrollAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){	
		SelectJoinStep<Record> select = ctx.getDslContext().select(payrollAttachWD).from(PAYROLL_BATCH_ATTACH);
		if(withData) select = ctx.getDslContext().select().from(PAYROLL_BATCH_ATTACH);
		return PAYROLL_ATTACH_PROPERTIES.build(select, filter).fetchInto(PAYROLL_BATCH_ATTACH).stream().map(new FullPayrollAttachFiller(ctx));
	}
	
	public static Stream<Attach> getProjectAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){	
		SelectJoinStep<Record> select = ctx.getDslContext().select(projectAttachWD).from(PROJECT_ATTACH);
		if(withData) select = ctx.getDslContext().select().from(PROJECT_ATTACH);
		return PROJECT_ATTACH_PROPERTIES.build(select, filter).fetchInto(PROJECT_ATTACH).stream().map(new FullProjectAttachFiller(ctx));
	}
	
	public static Stream<Attach> getSepeAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){	
		SelectJoinStep<Record> select = ctx.getDslContext().select(sepeAttachWD).from(SEPE_BATCH_ATTACH);
		if(withData) select = ctx.getDslContext().select().from(SEPE_BATCH_ATTACH);
		return SEPE_ATTACH_PROPERTIES.build(select, filter).fetchInto(SEPE_BATCH_ATTACH).stream().map(new FullSepeAttachFiller(ctx));
	}
	
	public static Stream<Attach> getDataAttachStream(AONContext ctx, AttachFilter filter, Boolean withData){	
		SelectJoinStep<Record> select = ctx.getDslContext().select(dataAttachWD).from(DATA_ATTACH);
		if(withData) select = ctx.getDslContext().select().from(DATA_ATTACH);
		return DATA_ATTACH_PROPERTIES.build(select, filter).fetchInto(DATA_ATTACH).stream().map(new FullDataAttachFiller());
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
				PROJECT_ATTACH.SECURITY_LEVEL, PROJECT_ATTACH.ATTACH_TYPE,
				PROJECT_ATTACH.CREATION_DATE, PROJECT_ATTACH.CREATION_USER,
				PROJECT_ATTACH.MODIFICATION_DATE, PROJECT_ATTACH.MODIFICATION_USER)
		.values(new Date(attach.getDate().getTime()), attach.getData(), 
				attach.getDescription(), attach.getDomain().getId(), 
				attach.getDriveId(), (byte)attach.getMimeType().ordinal(),
				attach.getAttachModule(), attach.getConfidential()?(byte)1:(byte)0,
				attach.getType() != null ? attach.getType() : 0,
				AonDateUtils.toTimestamp(new java.util.Date()), ctx.getUser(),
				AonDateUtils.toTimestamp(new java.util.Date()), ctx.getUser())
		.returning(PROJECT_ATTACH.ID).fetchOne().getId();
	}
	
	public static Integer insertRegistryAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(RATTACH, RATTACH.ATTACH_DATE,
				RATTACH.CATEGORY, RATTACH.DATA, RATTACH.DESCRIPTION,
				RATTACH.DOMAIN,	RATTACH.DPARENT_ID, RATTACH.DRIVE_ID,
				RATTACH.MIMETYPE,RATTACH.REGISTRY, RATTACH.SCOPE,
				RATTACH.SECURITY_LEVEL,	RATTACH.TYPE,
				RATTACH.CREATION_DATE, RATTACH.CREATION_USER,
				RATTACH.MODIFICATION_DATE, RATTACH.MODIFICATION_USER
				)
		.values(new Date(attach.getDate().getTime()), attach.getCategory(), 
				attach.getData(), attach.getDescription(), attach.getDomain().getId(), 
				attach.getDparentId(), attach.getDriveId(), (byte)attach.getMimeType().ordinal(),
				attach.getAttachModule(), attach.getScope(),
				attach.getConfidential()?(byte)1:(byte)0, (byte) attach.getType(),
				AonDateUtils.toTimestamp(new java.util.Date()), ctx.getUser(),
				AonDateUtils.toTimestamp(new java.util.Date()), ctx.getUser())
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
	
	public static Integer insertDataAttach(AONContext ctx, Attach attach){
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(DATA_ATTACH, DATA_ATTACH.DOMAIN,
				DATA_ATTACH.DATA, DATA_ATTACH.DRIVE_ID, DATA_ATTACH.MIMETYPE,
				DATA_ATTACH.SOURCE_ID, DATA_ATTACH.SOURCE, DATA_ATTACH.TYPE,
				DATA_ATTACH.CREATION_DATE, DATA_ATTACH.CREATION_USER,
				DATA_ATTACH.MODIFICATION_DATE, DATA_ATTACH.MODIFICATION_USER)
			.values(attach.getDomain().getId(), attach.getData(), attach.getDriveId(),
				(byte) attach.getMimeType().ordinal(), attach.getSourceBatch(), 
				(byte) attach.getSourceType(), (byte) attach.getType(),
				AonDateUtils.toTimestamp(new java.util.Date()), ctx.getUser(),
				AonDateUtils.toTimestamp(new java.util.Date()), ctx.getUser())
		.returning(DATA_ATTACH.ID).fetchOne().getId();
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
				.set(PROJECT_ATTACH.MODIFICATION_DATE, AonDateUtils.toTimestamp(new java.util.Date()))
				.set(PROJECT_ATTACH.MODIFICATION_USER, ctx.getUser())
		.where(PROJECT_ATTACH.ID.eq(attach.getId()))
		.execute();
	}

	public static void updateRegistryAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(RATTACH)
			.set(RATTACH.ATTACH_DATE, new Date(attach.getDate().getTime()))
			.set(RATTACH.CATEGORY,attach.getCategory())
			.set(RATTACH.DATA, attach.getData())
			.set(RATTACH.DESCRIPTION, attach.getDescription())
			.set(RATTACH.DOMAIN, attach.getDomain().getId())
			.set(RATTACH.DPARENT_ID, attach.getDparentId())
			.set(RATTACH.DRIVE_ID, attach.getDriveId())
			.set(RATTACH.MIMETYPE, (byte) attach.getMimeType().ordinal())
			.set(RATTACH.REGISTRY, attach.getAttachModule())
			.set(RATTACH.SCOPE, attach.getScope())
			.set(RATTACH.SECURITY_LEVEL,attach.getConfidential()?(byte)1:(byte)0)
			.set(RATTACH.TYPE, (byte) attach.getType())
			.set(RATTACH.MODIFICATION_DATE, AonDateUtils.toTimestamp(new java.util.Date()))
			.set(RATTACH.MODIFICATION_USER, ctx.getUser())
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
	
	public static void updateDataAttach(AONContext ctx, Attach attach){
		ctx.getDslContext().update(DATA_ATTACH)
			.set(DATA_ATTACH.DATA, attach.getData())
			.set(DATA_ATTACH.DOMAIN, attach.getDomain().getId())
			.set(DATA_ATTACH.DRIVE_ID, attach.getDriveId()) 	
			.set(DATA_ATTACH.MIMETYPE, (byte)attach.getMimeType().ordinal())
			.set(DATA_ATTACH.SOURCE_ID, attach.getSourceBatch())
			.set(DATA_ATTACH.SOURCE, (byte) attach.getSourceType())
			.set(DATA_ATTACH.TYPE, (byte) attach.getType())
		.where(DATA_ATTACH.ID.eq(attach.getId()))
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
					.set(PROJECT_ATTACH.MODIFICATION_DATE, AonDateUtils.toTimestamp(new java.util.Date()))
					.set(PROJECT_ATTACH.MODIFICATION_USER, ctx.getUser())
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
		
	//-------------------- DRIVE ID UPDATE
		
			public static void updateContractAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DRIVEID, driveId)
				.where(CONTRACT_ATTACH.ID.eq(attachId))
				.execute();
			}
			
			public static void updateItemAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(IATTACH)
					.set(IATTACH.DRIVEID,driveId)
				.where(IATTACH.ID.eq(attachId))
				.execute();
			}

			public static void updateInvoiceAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(INVOICE_ATTACH)
					.set(INVOICE_ATTACH.DRIVEID, driveId)
				.where(INVOICE_ATTACH.ID.eq(attachId))
				.execute();
			}

			public static void updateOfferAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(OFFER_ATTACH)
					.set(OFFER_ATTACH.DRIVEID, driveId)
				.where(OFFER_ATTACH.ID.eq(attachId))
				.execute();
			}

			public static void updatePayrollAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(PAYROLL_BATCH_ATTACH)
					.set(PAYROLL_BATCH_ATTACH.DRIVEID, driveId)
				.where(PAYROLL_BATCH_ATTACH.ID.eq(attachId))
				.execute();
			}

			public static void updateProjectAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(PROJECT_ATTACH)
						.set(PROJECT_ATTACH.DRIVEID, driveId)
						.set(PROJECT_ATTACH.MODIFICATION_DATE, AonDateUtils.toTimestamp(new java.util.Date()))
						.set(PROJECT_ATTACH.MODIFICATION_USER, ctx.getUser())
				.where(PROJECT_ATTACH.ID.eq(attachId))
				.execute();
			}

			public static void updateRegistryAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(RATTACH)
					.set(RATTACH.DRIVE_ID, driveId)
				.where(RATTACH.ID.eq(attachId))
				.execute();
			}

			public static void updateSepeAttachDriveId(AONContext ctx, Integer attachId, String driveId){
				ctx.getDslContext().update(SEPE_BATCH_ATTACH)
					.set(SEPE_BATCH_ATTACH.DRIVEID, driveId)
				.where(SEPE_BATCH_ATTACH.ID.eq(attachId))
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
	
	public static void deleteDataAttach(AONContext ctx, AttachFilter filter){
		ctx.getDslContext().delete(DATA_ATTACH).where(DATA_ATTACH_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static void deleteRegistryAttachTag(AONContext ctx, Integer rattachId){
		ctx.getDslContext().delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(rattachId)).execute();
	}
	
	//------------------ DRIVE DOMAIN ID LIST
	
	public static SelectConditionStep<Record1<Integer>> rattachDomainList(AONContext ctx){
		return ctx.getDslContext().select(RATTACH.DOMAIN)
			.from(RATTACH)
			.where(RATTACH.DATA.isNotNull())
				.and(RATTACH.TYPE.notIn(
					RegistryAttachmentType.LOGO.value(),
					RegistryAttachmentType.AON_TEMPLATES.value(),
					RegistryAttachmentType.D2_DEPOSIT.value(),
					RegistryAttachmentType.DOMAIN_BOOK_HISTORY.value(),
					RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value(),
					RegistryAttachmentType.CRETA_RESPUESTA.value(),
					RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS.value()
				));
	}
	
	public static SelectConditionStep<Record1<Integer>> projectAttachDomainList(AONContext ctx){
		return ctx.getDslContext().select(PROJECT_ATTACH.DOMAIN)
			.from(PROJECT_ATTACH)
			.where(PROJECT_ATTACH.DATA.isNotNull());
	}
	
	public static SelectConditionStep<Record1<Integer>> invoiceAttachDomainList(AONContext ctx){
		return ctx.getDslContext().select(INVOICE_ATTACH.DOMAIN)
			.from(INVOICE_ATTACH)
			.where(INVOICE_ATTACH.DATA.isNotNull());
	}
	
	public static SelectConditionStep<Record1<Integer>> contractAttachDomainList(AONContext ctx){
		return ctx.getDslContext().select(CONTRACT_ATTACH.DOMAIN)
			.from(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.DATA.isNotNull());
	}
	
	public static SelectConditionStep<Record1<Integer>> iattachDomainList(AONContext ctx){
		return ctx.getDslContext().select(IATTACH.DOMAIN)
			.from(IATTACH)
			.where(IATTACH.DATA.isNotNull());
	}
	
	public static SelectConditionStep<Record1<Integer>> offerAttachDomainList(AONContext ctx){
		return ctx.getDslContext().select(OFFER_ATTACH.DOMAIN)
			.from(OFFER_ATTACH)
			.where(OFFER_ATTACH.DATA.isNotNull());
	}
	
	public static SelectConditionStep<Record1<Integer>> payrollAttachDomainList(AONContext ctx){
		return ctx.getDslContext().select(PAYROLL_BATCH_ATTACH.DOMAIN)
			.from(PAYROLL_BATCH_ATTACH)
			.where(PAYROLL_BATCH_ATTACH.DATA.isNotNull());
	}
	
	public static SelectConditionStep<Record1<Integer>> sepeAttachDomainList(AONContext ctx){
		return ctx.getDslContext().select(SEPE_BATCH_ATTACH.DOMAIN)
			.from(SEPE_BATCH_ATTACH)
			.where(SEPE_BATCH_ATTACH.DATA.isNotNull());
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
							.setConfidential(r.getSecurityLevel() == 1)
							.setCreationDate(r.getCreationDate())
							.setCreationUser(r.getCreationUser())
							.setData(r.getData())
							.setDate(r.getAttachDate())
							.setDescription(r.getDescription())
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
							.setDparentId(r.getDparentId())
							.setDriveId(r.getDriveId())
							.setId(r.getId())
							.setMimeType(r.getMimetype()!= null ? MimeType.values()[r.getMimetype()] : null)
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
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
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
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
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
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
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
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
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
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
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
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setType(r.getAttachType())
							.setCreationDate(r.getCreationDate())
							.setCreationUser(r.getCreationUser())
							.setModificationDate(r.getModificationDate())
							.setModificationUser(r.getModificationUser())
							;	
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
							.setDomain(AON.getDomain(ctx.getDomainName(), r.getDomain(), ctx.getUser()))
							.setDriveId(r.getDriveid())
							.setId(r.getId())
							.setMimeType(MimeType.values()[r.getMimetype()])
							.setScope(r.getScope())
							.setSourceBatch(r.getSourceBatch())
							.setSourceType(r.getSourceType());		
		}
	}
	
	private static class FullDataAttachFiller implements Function<Record, Attach> {
		
		@Override
		public Attach apply(Record r) {
			return new Attach().setAttachType(AttachType.DATA)
							.setData(r.getValue(DATA_ATTACH.DATA))
							.setDomain(new Domain().setId(r.getValue(DATA_ATTACH.DOMAIN)))
							.setDriveId(r.getValue(DATA_ATTACH.DRIVE_ID))
							.setId(r.getValue(DATA_ATTACH.ID))
							.setMimeType(MimeType.values()[r.getValue(DATA_ATTACH.MIMETYPE)])
							.setSourceBatch(r.getValue(DATA_ATTACH.SOURCE_ID))
							.setSourceType(r.getValue(DATA_ATTACH.SOURCE))
							.setType(r.getValue(DATA_ATTACH.TYPE));		
		}
	}
	
	
	
	
	
}
