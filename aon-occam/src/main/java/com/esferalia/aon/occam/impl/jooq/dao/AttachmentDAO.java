package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

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
	
	public static Attach getRattach(AONContext ctx, Condition condition){	
		Record8<byte[], Byte, Byte, String, Integer, String, java.sql.Date, Byte> record = ctx.getDslContext()
			.select(RATTACH.DATA, RATTACH.MIMETYPE, RATTACH.TYPE, RATTACH.DRIVE_ID,
					RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.ATTACH_DATE,
					RATTACH.SECURITY_LEVEL)
			.from(RATTACH)
			.where(condition)
			.fetchOne();
	
		Attach rattach = new Attach();

		if(record.value1() != null) rattach.setData(record.value1());
		if(record.value2() != null) rattach.setMimeType(MimeType.values()[record.value2()]);
		if(record.value3() != null) rattach.setType(record.value3());
		if(record.value4() != null) rattach.setDriveId(record.value4());
		if(record.value5() != null) rattach.setRattachId(record.value5());
		if(record.value6() != null) rattach.setDescription(record.value6());
		if(record.value7() != null) rattach.setDate(record.value7());
		if(record.value8() != null) rattach.setConfidential(record.value8().equals(1)?true:false);

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
			rattach.setRattachId(record.getId());
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
		if(record.value4() != null) rattach.setRattachId(record.value4());
		if(record.value5() != null) rattach.setDescription(record.value5());
		if(record.value6() != null) rattach.setDate(record.value6());
		if(record.value7() != null) rattach.setConfidential(record.value7().equals(1)?true:false);

		return rattach;
	}	
}
