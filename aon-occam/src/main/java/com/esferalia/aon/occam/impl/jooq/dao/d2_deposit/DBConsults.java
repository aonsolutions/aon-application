package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record7;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class DBConsults {

	public static Boolean isDigitalDeposit(String domain, Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
			Record2<Integer, byte[]> record = ctx.getDslContext()
				.select(RATTACH.ID,RATTACH.DATA)
				.from(RATTACH).join(REGISTRY).on(REGISTRY.ID.eq(RATTACH.REGISTRY))
				.where(RATTACH.TYPE.eq((byte)17).and(RATTACH.DOMAIN.eq(domainId)))
				.fetchOne();
			
			return record != null && record.value2() != null;
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static File getXmlFile(String domain, Integer domainId) throws IOException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
				
				
			// DOMAIN + DOMAIN SON
			Record7<Integer, String, Byte, String, byte[], String, String> record = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID, RATTACH.DATA,
								REGISTRY.DOCUMENT, REGISTRY.NAME)
						.from(RATTACH).join(REGISTRY).on(REGISTRY.ID.eq(RATTACH.REGISTRY))
						.where(RATTACH.TYPE.eq((byte)17).and(RATTACH.DOMAIN.eq(domainId)))
						.fetchOne();
			
				
				
			File f = File.createTempFile("","");
			byte[] data;
			if(record != null){
				data = record.value5();
				try {
					FileInputStream fi =  new FileInputStream(f);
					OutputStream out = null;
			        try {
			            out = openOutputStream(f);
			            
			          
			            out.write(data);
			        } finally {
			            AonIOUtils.closeQuietly(out);
			        }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return f;
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	  
    public static FileOutputStream openOutputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && parent.exists() == false) {
                if (parent.mkdirs() == false) {
                    throw new IOException("File '" + file + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file);
    }
	
	public static Esquema getDeposit(String domain , Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
				
				
			// DOMAIN + DOMAIN SON
			Record7<Integer, String, Byte, String, byte[], String, String> record = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID, RATTACH.DATA,
								REGISTRY.DOCUMENT, REGISTRY.NAME)
						.from(RATTACH).join(REGISTRY).on(REGISTRY.ID.eq(RATTACH.REGISTRY))
						.where(RATTACH.TYPE.eq((byte)17).and(RATTACH.DOMAIN.eq(domainId)))
						.fetchOne();
			
				
				
			File f = new File("/tmp/DEPOSITO.xml"); 
			byte[] data;
			Esquema schema = null;
			if(record != null){
				data = record.value5();
				String document = record.value6();
				String name = record.value7();
			}
			else{
				Record3<Integer, String, String> reg = ctx.getDslContext().select(ENTERPRISE.REGISTRY, REGISTRY.DOCUMENT, REGISTRY.NAME)
						.from(ENTERPRISE.join(REGISTRY).on(REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
						.where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne();
				Integer registry = reg.value1();
				String document = reg.value2();
				String name = reg.value3(); 
				data = Utils.CreateXml(document, name);
				insertDeposit(ctx, domain, data, domainId, name, document, registry);
				
			}
			try {
				schema = Utils.readXml(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return schema;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static String getCIF(String domain, Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
			Record1<String> reg = ctx.getDslContext().select(REGISTRY.DOCUMENT)
				.from(ENTERPRISE.join(REGISTRY).on(REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
				.where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne();
			
			return reg.value1();
		}finally {
			if (ctx != null) ctx.close();
		}
		
	}

	
	public static Integer insertDeposit(AONContext ctx , String domain, byte[] b,Integer domainId, String name, String document, Integer registry) {

			
			return ctx.getDslContext().insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID)
						.values(registry,domainId,null,(byte) com.esferalia.aon.occam.api.model.type.MimeType.XML.ordinal(), name ,(byte) 17,null,(byte)0,null,b,null,null).returning(RATTACH.ID).fetchOne().getId();
	
	}
	
	public static Integer insertDeposit(String domain, byte[] b,Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
			Record3<Integer, String, String> reg = ctx.getDslContext().select(ENTERPRISE.REGISTRY, REGISTRY.DOCUMENT, REGISTRY.NAME)
					.from(ENTERPRISE.join(REGISTRY).on(REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
					.where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne();
			Integer registry = reg.value1();
			String document = reg.value2();
			String name = reg.value3(); 
			
			ctx.getDslContext().delete(RATTACH).where(RATTACH.DOMAIN.eq(domainId))
			.and(RATTACH.TYPE.eq((byte)17)).execute();
		return ctx.getDslContext().insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID)
					.values(registry,domainId,null,(byte) com.esferalia.aon.occam.api.model.type.MimeType.XML.ordinal(), name ,(byte) 17,null,(byte)0,null,b,null,null).returning(RATTACH.ID).fetchOne().getId();
		
		}finally {
			if (ctx != null) ctx.close();
		}
		
}

	
	

	
}
