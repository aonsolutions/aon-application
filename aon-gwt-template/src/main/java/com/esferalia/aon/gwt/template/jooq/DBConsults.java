package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;




public class DBConsults {
	
	public static TemplateList getTemplates(String domain , Integer domainId) throws SQLException{
			Connection connection = null;
			try {
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				
				Result<Record4<Integer, String, Byte, String>> record = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.ID.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(RATTACH.DOMAIN.eq(domainId).or(DOMAIN.PARENT.eq(domainId))))
						.fetch();
				
				Vector<TemplateInfo> v = new Vector<TemplateInfo>();
				record.stream().forEach(r -> {
					TemplateInfo ti = new TemplateInfo();
					ti.setId(r.value1());
					ti.setName(r.value2());
					ti.setMimetype(r.value3().intValue());
					File f ;
					if(r.value4()!=null){
						ti.setDriveId(r.value4());
						//TODO GET FILE TO DRIVE SERVICE ACCOUNT!!!
						f = null;
					}
					else{
						f = new File("/tmp/"+ti.getName()+".xml"); 
						byte[] b = getXml(dslContext,ti.getId());
						try {
							org.apache.commons.io.FileUtils.writeByteArrayToFile(f,b);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					TemplateInfo aux = null;
					try {
						aux = Utils.readxml(f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ti.setColumns(aux.getColumns());
					ti.setType(aux.getType());
					v.add(ti);
				});
				TemplateList tl = new TemplateList();
				tl.setList(v);
				return tl;
			}finally {
				if (connection != null)
					connection.close();
			}
		
	}
	
	public static Integer insertTemplate(String domain, TemplateInfo ti, byte[] b,Integer domainId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			
			Result<Record1<Integer>> reg = dslContext.select(ENTERPRISE.REGISTRY)
				.from(ENTERPRISE.join(DOMAIN).on(ENTERPRISE.DOMAIN.eq(DOMAIN.ID)))
				.where(DOMAIN.NAME.eq(domain)).fetch();
			
						
			return dslContext.insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID)
						.values(reg.get(0).value1(),domainId,null,(byte) MimeType.MIME_XML.ordinal(),ti.getName(),(byte) 15,null,(byte)0,null,b,null,null).returning(RATTACH.ID).fetchOne().getId();
	
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static byte[] getTemplate(String domain , Integer id) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			return getXml(dslContext, id);
			
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static byte[] getXml(DSLContext dslContext, Integer id){
	
			return dslContext
					.select(RATTACH.DATA)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id))
					.fetchOne().value1();

	}
	
	public static void removeTemplate(String domain,Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			dslContext.delete(RATTACH).where(RATTACH.ID.eq(id)).execute();
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void updateTemplate(String domain,TemplateInfo ti, Integer domainId, byte[] b) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(RATTACH).set(RATTACH.DESCRIPTION,ti.getName())
									.set(RATTACH.DATA,b)
							.where(RATTACH.ID.eq(ti.getId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
}
