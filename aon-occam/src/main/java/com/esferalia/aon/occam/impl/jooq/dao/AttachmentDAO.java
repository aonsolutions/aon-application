package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

import org.jooq.Record8;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Rattach;
import com.esferalia.aon.occam.api.model.type.MimeType;



public class AttachmentDAO {
	
	public static Rattach getRattach(AONContext ctx, Integer id){
		
			
		Record8<byte[], Byte, Byte, String, Integer, String, java.sql.Date, Byte> record = ctx.getDslContext()
			.select(RATTACH.DATA, RATTACH.MIMETYPE, RATTACH.TYPE, RATTACH.DRIVE_ID,
					RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.ATTACH_DATE,
					RATTACH.SECURITY_LEVEL)
			.from(RATTACH)
			.where(RATTACH.ID.eq(id))
			.fetchOne();		
	
		
		Rattach rattach = new Rattach();
		if(record.value1() != null) rattach.setData(record.value1());
		if(record.value2() != null) rattach.setMimeType(MimeType.values()[record.value2()]);
		if(record.value3() != null) rattach.setType(record.value3());
		if(record.value4() != null) rattach.setDriveId(record.value4());
		if(record.value5() != null) rattach.setRattachId(record.value5());
		if(record.value6() != null) rattach.setDescription(record.value6());
		if(record.value7() != null) rattach.setDate(record.value7());
		if(record.value8() != null) rattach.setConfidential(record.value1().equals(1)?true:false);

		return rattach;
	}
	
	public static Rattach getRattachWithoutData(AONContext ctx, Integer id){
		return null;
	}
	
	
	
	
	
	
}
