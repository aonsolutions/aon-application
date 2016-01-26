package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Record7;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;

public class DBConsults {
	
	private static final String D2_FILE_MEMORY = "Memoria";
	private static final String D2_FILE_AUTOCARTERA_MODEL = "Modelo de Autocartera";
	private static final String D2_FILE_GESTION = "Informe de Gestion";
	private static final String D2_FILE_AUDIT = "Informe de Auditoria";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificacion SICAV";


	public static Boolean isDigitalDeposit(String domain, Integer domainId,Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record2<Integer, byte[]> record = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.DATA)
					.from(RATTACH)
					.join(REGISTRY)
					.on(REGISTRY.ID.eq(RATTACH.REGISTRY))
					.where(RATTACH.TYPE.eq((byte) 17)
						.and(RATTACH.DOMAIN.eq(domainId)))
						.and(RATTACH.ATTACH_DATE.eq(newAttachDate(year)))
						.limit(1).fetchOne();

			return record != null && record.value2() != null;

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/*public static Boolean isDigitalDeposit(String domain, Integer domainId, Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record2<Integer, byte[]> record = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.DATA)
					.from(RATTACH)
					.join(REGISTRY)
					.on(REGISTRY.ID.eq(RATTACH.REGISTRY))
					.where(RATTACH.TYPE.eq((byte) 17))
						.and(RATTACH.DOMAIN.eq(domainId))
						.and(RATTACH.ATTACH_DATE.eq(newAttachDate(year)))
					.fetchOne();

			return record != null && record.value2() != null;

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}*/

	// Devuelve el xml file d2

	public static File getXmlFile(String domain, Integer domainId)
			throws IOException {
		AONContext ctx = null;
		File parent = null;

		try {
			ctx = AONContext.getAONContext(domain, domainId);

			// DOMAIN + DOMAIN SON
			Record7<Integer, String, Byte, String, byte[], String, String> record = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.MIMETYPE,
							RATTACH.DRIVE_ID, RATTACH.DATA, REGISTRY.DOCUMENT,
							REGISTRY.NAME)
					.from(RATTACH)
					.join(REGISTRY)
					.on(REGISTRY.ID.eq(RATTACH.REGISTRY))
					.where(RATTACH.TYPE.eq((byte) 17).and(
							RATTACH.DOMAIN.eq(domainId))).
					orderBy(RATTACH.ID).limit(1).fetchOne();
			
			byte[] data;
			if (record != null) {
				
				parent = File.createTempFile(record.value2()+"%", "");
				parent.delete();
				parent.mkdir();
				
				File documents = File.createTempFile("Documentos%", "", parent);
				documents.delete();
				documents.mkdir();
				
				//**************************
				getFileDocuments2Zip(domain, domainId, D2_FILE_MEMORY, documents);
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUTOCARTERA_MODEL, documents);	
				getFileDocuments2Zip(domain, domainId, D2_FILE_GESTION, documents);			
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUDIT, documents);
				getFileDocuments2Zip(domain, domainId, D2_FILE_CONVOC, documents);
				getFileDocuments2Zip(domain, domainId, D2_FILE_SICAV, documents);
				//**************************
				
				File tmpDocuments = File.createTempFile("Documentos TMP%", "", parent);
				tmpDocuments.delete();
				tmpDocuments.mkdir();
				
				File otherDocuments =File.createTempFile("Otros Documentos%", "", parent);
				otherDocuments.delete();
				otherDocuments.mkdir();
				
				File otherTmpDocuments =File.createTempFile("Otros Documentos TMP%", "", parent);
				otherTmpDocuments.delete();
				otherTmpDocuments.mkdir();

				File f = File.createTempFile("DEPOSITO%", ".xml", parent);
				
				data = record.value5();

				try {
					AonFileUtils.writeByteArrayToFile(f, data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return parent;

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static File getXmlFile(String domain, Integer domainId, Integer year)
			throws IOException {
		AONContext ctx = null;
		File parent = null;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Month.DECEMBER.ordinal());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		System.out.println(calendar.getTime());
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			// DOMAIN + DOMAIN SON
			Record7<Integer, String, Byte, String, byte[], String, String> record = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.MIMETYPE,
							RATTACH.DRIVE_ID, RATTACH.DATA, REGISTRY.DOCUMENT,
							REGISTRY.NAME)
					.from(RATTACH)
					.join(REGISTRY)
					.on(REGISTRY.ID.eq(RATTACH.REGISTRY))
					.where(RATTACH.TYPE.eq(RegistryAttachmentType.D2_DEPOSIT.value()))
					.and(RATTACH.DOMAIN.eq(domainId))
					.and(RATTACH.ATTACH_DATE.eq(AonDateUtils.toSql(calendar.getTime()))).
					orderBy(RATTACH.ID).limit(1).fetchOne();
			
			byte[] data;
			if (record != null) {
				
				parent = File.createTempFile(record.value2()+"%", "");
				parent.delete();
				parent.mkdir();
				
				File documents = File.createTempFile("Documentos%", "", parent);
				documents.delete();
				documents.mkdir();
				
				//**************************
				getFileDocuments2Zip(domain, domainId, D2_FILE_MEMORY, documents);
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUTOCARTERA_MODEL, documents);	
				getFileDocuments2Zip(domain, domainId, D2_FILE_GESTION, documents);			
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUDIT, documents);
				getFileDocuments2Zip(domain, domainId, D2_FILE_CONVOC, documents);
				getFileDocuments2Zip(domain, domainId, D2_FILE_SICAV, documents);
				//**************************
				
				File tmpDocuments = File.createTempFile("Documentos TMP%", "", parent);
				tmpDocuments.delete();
				tmpDocuments.mkdir();
				
				File otherDocuments =File.createTempFile("Otros Documentos%", "", parent);
				otherDocuments.delete();
				otherDocuments.mkdir();
				
				File otherTmpDocuments =File.createTempFile("Otros Documentos TMP%", "", parent);
				otherTmpDocuments.delete();
				otherTmpDocuments.mkdir();

				File f = File.createTempFile("DEPOSITO%", ".xml", parent);
				
				data = record.value5();

				try {
					AonFileUtils.writeByteArrayToFile(f, data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return parent;

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Esquema readXml(byte[] data){
		Esquema schema = null;
		try {
			schema = Utils.readXml(data);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return schema;
	}
	
	public static Esquema getDeposit(String domain, Integer domainId, Integer year, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			// DOMAIN + DOMAIN SON
			Record7<Integer, String, Byte, String, byte[], String, String> record = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.MIMETYPE,
							RATTACH.DRIVE_ID, RATTACH.DATA, REGISTRY.DOCUMENT,
							REGISTRY.NAME)
					.from(RATTACH)
					.join(REGISTRY)
					.on(REGISTRY.ID.eq(RATTACH.REGISTRY))
					.where(RATTACH.TYPE.eq((byte) 17).and(
							RATTACH.DOMAIN.eq(domainId)))
							.and(RATTACH.ATTACH_DATE.eq(newAttachDate(year)))
							.orderBy(RATTACH.ID).limit(1).fetchOne();

			File f = new File("/tmp/DEPOSITO.xml");
			byte[] data;
			Esquema schema = null;
			if (record != null) {
				data = record.value5();
				String document = record.value6();
				String name = record.value7();
			} else {
				Record3<Integer, String, String> reg = ctx
						.getDslContext()
						.select(ENTERPRISE.REGISTRY, REGISTRY.DOCUMENT,
								REGISTRY.NAME)
						.from(ENTERPRISE.join(REGISTRY).on(
								REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
						.where(ENTERPRISE.DOMAIN.eq(domainId)).limit(1).fetchOne();
				Integer registry = reg.value1();
				String document = reg.value2();
				String name = reg.value3();
				data = Utils.CreateXml(document, name);
				insertDeposit(ctx, domain, data, domainId, name, document,
						registry, year, login);

			}
			try {
				schema = Utils.readXml(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return schema;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Esquema getDeposit(String domain, Integer domainId,
			String idstr) {
		Integer id = Integer.parseInt(idstr);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			// DOMAIN + DOMAIN SON
			Record5<Integer, String, Byte, String, byte[]> record = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.MIMETYPE,
							RATTACH.DRIVE_ID, RATTACH.DATA).from(RATTACH)
					.where(RATTACH.ID.eq(id))
					.orderBy(RATTACH.ID).limit(1).fetchOne();

			File f = new File("/tmp/DEPOSITO.xml");
			byte[] data;
			Esquema schema = null;

			data = record.value5();

			try {
				schema = Utils.readXml(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return schema;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getCIF(String domain, Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record1<String> reg = ctx
					.getDslContext()
					.select(REGISTRY.DOCUMENT)
					.from(ENTERPRISE.join(REGISTRY).on(
							REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
					.where(ENTERPRISE.DOMAIN.eq(domainId)).limit(1).fetchOne();

			return reg.value1();
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	public static Integer insertDeposit(AONContext ctx, String domain,
			byte[] b, Integer domainId, String name, String document,
			Integer registry, Integer year, String login) {

		return ctx
				.getDslContext()
				.insertInto(RATTACH, RATTACH.REGISTRY, RATTACH.DOMAIN,
						RATTACH.CATEGORY, RATTACH.MIMETYPE,
						RATTACH.DESCRIPTION, RATTACH.TYPE, RATTACH.SCOPE,
						RATTACH.SECURITY_LEVEL, RATTACH.ATTACH_DATE,
						RATTACH.DATA, RATTACH.DRIVE_ID, RATTACH.DPARENT_ID
						,RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
				.values(registry,
						domainId,
						null,
						(byte) com.esferalia.aon.occam.api.model.type.MimeType.XML
								.ordinal(), name, (byte) 17, null, (byte) 0,
						newAttachDate(year), b, null, null,login, new Timestamp(new java.util.Date().getTime())).returning(RATTACH.ID).fetchOne()
				.getId();

	}

	public static Integer insertDeposit(String domain, byte[] b,
			Integer domainId, Integer year, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record3<Integer, String, String> reg = ctx
					.getDslContext()
					.select(ENTERPRISE.REGISTRY, REGISTRY.DOCUMENT,
							REGISTRY.NAME)
					.from(ENTERPRISE.join(REGISTRY).on(
							REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
					.where(ENTERPRISE.DOMAIN.eq(domainId)).limit(1).fetchOne();
			Integer registry = reg.value1();
			String document = reg.value2();
			String name = reg.value3();

			ctx.getDslContext().delete(RATTACH)
					.where(RATTACH.DOMAIN.eq(domainId))
					.and(RATTACH.TYPE.eq((byte) 17))
					.and(RATTACH.ATTACH_DATE.eq(newAttachDate(year))).execute();
			return ctx
					.getDslContext()
					.insertInto(RATTACH, RATTACH.REGISTRY, RATTACH.DOMAIN,
							RATTACH.CATEGORY, RATTACH.MIMETYPE,
							RATTACH.DESCRIPTION, RATTACH.TYPE, RATTACH.SCOPE,
							RATTACH.SECURITY_LEVEL, RATTACH.ATTACH_DATE,
							RATTACH.DATA, RATTACH.DRIVE_ID, RATTACH.DPARENT_ID,
							RATTACH.CREATION_USER, RATTACH.CREATION_DATE, 
							RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
					.values(registry,
							domainId,
							null,
							(byte) com.esferalia.aon.occam.api.model.type.MimeType.XML
									.ordinal(), name, (byte) 17, null,
							(byte) 0, newAttachDate(year), b, null, null,
							null, null, login, new Timestamp(new java.util.Date().getTime()))
					.returning(RATTACH.ID).fetchOne().getId();

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}
	
	public static void insertDeposit(String domain, byte[] b, Integer domainId,
			String idstr, String login) {
		Integer id = Integer.parseInt(idstr);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			ctx.getDslContext().update(RATTACH).set(RATTACH.DATA, b)
					.set(RATTACH.MODIFICATION_USER, login)
					.set(RATTACH.MODIFICATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.where(RATTACH.ID.eq(id)).execute();

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}
	
	public static void deleteDeposit(String domain, Integer domainId, Integer year){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
			ctx.getDslContext().delete(RATTACH).where(RATTACH.DOMAIN.eq(domainId))
			.and(RATTACH.TYPE.eq((byte)17))
			.and(RATTACH.ATTACH_DATE.eq(newAttachDate(year))).execute();
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}	

	public static void deleteText(String domain,Integer domainId, Integer rattachId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
			ctx.getDslContext().delete(RATTACH).where(RATTACH.ID.eq(rattachId))
			.and(RATTACH.TYPE.eq((byte)17)).execute();
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}	
	
	public static Integer insertDepositText(String domain, String name,
			byte[] b, Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record1<Integer> reg = ctx
					.getDslContext()
					.select(ENTERPRISE.REGISTRY)
					.from(ENTERPRISE.join(REGISTRY).on(
							REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
					.where(ENTERPRISE.DOMAIN.eq(domainId))
					.limit(1).fetchOne();
			Integer registry = reg.value1();

			return ctx
					.getDslContext()
					.insertInto(RATTACH, RATTACH.REGISTRY, RATTACH.DOMAIN,
							RATTACH.CATEGORY, RATTACH.MIMETYPE,
							RATTACH.DESCRIPTION, RATTACH.TYPE, RATTACH.SCOPE,
							RATTACH.SECURITY_LEVEL, RATTACH.ATTACH_DATE,
							RATTACH.DATA, RATTACH.DRIVE_ID, RATTACH.DPARENT_ID
							,RATTACH.CREATION_USER, RATTACH.CREATION_DATE
							,RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
					.values(registry,
							domainId,
							null,
							(byte) com.esferalia.aon.occam.api.model.type.MimeType.XML
									.ordinal(), name, (byte) 17, null,
							(byte) 0, null, b, null, null,
							login, new Timestamp(new java.util.Date().getTime()),
							login, new Timestamp(new java.util.Date().getTime()))
					.returning(RATTACH.ID).fetchOne().getId();

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}
	
	public static Vector<Integer> getMemoryFile(String domain, Integer domainId, String name){
		AONContext ctx = null;
		try {
			
			ctx = AONContext.getAONContext(domain, domainId);
			Result<Record2<Integer, Byte>> record = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.MIMETYPE)
					.from(RATTACH)
					.where(RATTACH.DESCRIPTION.eq(name))
					.and(RATTACH.DOMAIN.eq(domainId))
					.and(RATTACH.TYPE.eq((byte)7))
					.fetch();		
			Vector<Integer> v = new Vector<Integer>();
			if(!record.isEmpty()){
				v.add(record.get(0).value1());
				v.add(record.get(0).value2().intValue());
				return v;
			}
			v.add(-1);
			return v;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static File getFileDocuments2Zip(String domain, Integer domainId, String name, File parent) {
		AONContext ctx = null;
		try {
			
			ctx = AONContext.getAONContext(domain, domainId);
			
			RattachRecord record = ctx.getDslContext()
					.selectFrom(RATTACH)
					.where(RATTACH.DESCRIPTION.eq(name)
							.and(RATTACH.DOMAIN.eq(domainId))
							.and(RATTACH.TYPE.eq((byte)7)))
					.limit(1).fetchOne();
			
			byte[] data;
			
			if(record != null){
				
				String extension = MimeType.values()[record.getValue(RATTACH.MIMETYPE)].getExtension();
				File tempFile = File.createTempFile(name.toUpperCase() +"%", "."+extension, parent);				
				data = record.getValue(RATTACH.DATA);
				AonFileUtils.writeByteArrayToFile(tempFile, data);				
				return tempFile;			
			}
			return null;
			
		} catch (IOException ex) {
			System.out.println(ex.getMessage() + " " + ex.getLocalizedMessage());
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	
	public static File getMemoryFile(String domain, Integer domainId, Integer id, String name){
		AONContext ctx = null;
		try {
			
			ctx = AONContext.getAONContext(domain, domainId);
			Result<Record2<Byte, byte[]>> record = ctx
					.getDslContext()
					.select(RATTACH.MIMETYPE, RATTACH.DATA)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id))
					.fetch();		
			
		
			byte[] data;
			File file;
			if(!record.isEmpty()){
				if(record.get(0).value2() != null){
					data = record.get(0).value2();
					file = new File("/tmp/" + name );
					if(!file.isDirectory())
						try {
							AonFileUtils.writeByteArrayToFile(file, data);
						} catch (IOException e) {
							e.printStackTrace();
						}
					return file;
				}
			}
			return null;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Byte getMimeType(String domain, Integer domainId, Integer id){
		AONContext ctx = null;
		try {
			
			ctx = AONContext.getAONContext(domain, domainId);
			Result<Record1<Byte>> record = ctx
					.getDslContext()
					.select(RATTACH.MIMETYPE)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id))
					.fetch();		
			
			if(!record.isEmpty()){
				if(record.get(0).value1() != null){
					return record.get(0).value1();
				}
			}
			return -1;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	
	public static void deleteMemoryFile(String domain,Integer domainId, Integer rattachId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
			ctx.getDslContext().delete(RATTACH).where(RATTACH.ID.eq(rattachId))
			.and(RATTACH.TYPE.eq((byte)7)).execute();
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}	
	public static void deleteMemoryFile(String domain,Integer domainId, String name){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
			ctx.getDslContext().delete(RATTACH)
			.where(RATTACH.DOMAIN.eq(domainId))
			.and(RATTACH.DESCRIPTION.eq(name))
			.and(RATTACH.TYPE.eq((byte)7)).execute();
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Integer insertMemoryFile(String domain, Integer domainId, byte mimetype, byte[] data, String name, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record1<Integer> reg = ctx
					.getDslContext()
					.select(ENTERPRISE.REGISTRY)
					.from(ENTERPRISE.join(REGISTRY).on(
							REGISTRY.ID.eq(ENTERPRISE.REGISTRY)))
					.where(ENTERPRISE.DOMAIN.eq(domainId)).limit(1).fetchOne();
			Integer registry = reg.value1();

			return ctx.getDslContext()
					.insertInto(RATTACH, RATTACH.REGISTRY, RATTACH.DOMAIN,
							RATTACH.CATEGORY, RATTACH.MIMETYPE,
							RATTACH.DESCRIPTION, RATTACH.TYPE, RATTACH.SCOPE,
							RATTACH.SECURITY_LEVEL, RATTACH.ATTACH_DATE,
							RATTACH.DATA, RATTACH.DRIVE_ID, RATTACH.DPARENT_ID
							,RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
					.values(registry, domainId,null,
							mimetype, name, (byte) 7, null,
							(byte) 0, null, data, null, null
							, login, new Timestamp(new java.util.Date().getTime()))
					.returning(RATTACH.ID).fetchOne().getId();

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateMemoryFile(String domain, Integer domainId, byte mimetype, byte[] data, Integer id){
	
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			ctx.getDslContext().update(RATTACH).set(RATTACH.DATA, data)
					.set(RATTACH.MIMETYPE, mimetype)
					.where(RATTACH.ID.eq(id)).execute();

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Date newAttachDate(Integer year){
		if(year != null){
			return new Date(year-1900, 11, 31);
		}
		return new Date(2014-1900, 11, 31);
	}
	
	public static String[] getDepositExercises(String domainName, Integer domainId, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			Object[] array =  ctx.getDslContext().select(RATTACH.ATTACH_DATE)
						.from(RATTACH)
						.where(RATTACH.DOMAIN.eq(domainId))
						.and(RATTACH.TYPE.eq(RegistryAttachmentType.D2_DEPOSIT.value()))
						.fetch().stream()
						.map(r -> Integer.toString(AonDateUtils.getYear(r.getValue(RATTACH.ATTACH_DATE))))
						.toArray();
			return Arrays.copyOf(array, array.length, String[].class);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
}
