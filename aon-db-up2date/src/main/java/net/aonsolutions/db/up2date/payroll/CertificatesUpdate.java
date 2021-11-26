package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Connection;
import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.RattachRecord;

import net.aonsolutions.db.up2date.Update;

public class CertificatesUpdate implements Update {

	public static final CertificatesUpdate CERTIFICATESUPDATE = new CertificatesUpdate();
	
	private CertificatesUpdate() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		// Create TAGs on domain 0 if not exist
		
		Record tgssTagRecord = dslContext.select().from(TAG).where(TAG.DOMAIN.eq(0)).and(TAG.NAME.eq("TGSS")).and(TAG.TYPE.eq((byte)13)).fetchOne();
		if(null == tgssTagRecord)
			tgssTagRecord = dslContext.insertInto(TAG)
				.set(TAG.DOMAIN, 0)
				.set(TAG.NAME, "TGSS")
				.set(TAG.TYPE, (byte)13)
				.returning()
				.fetchOne();
		
		Record sepeTagRecord = dslContext.select().from(TAG).where(TAG.DOMAIN.eq(0)).and(TAG.NAME.eq("SEPE")).and(TAG.TYPE.eq((byte)13)).fetchOne();
		if(null == sepeTagRecord)
			sepeTagRecord = dslContext.insertInto(TAG)
				.set(TAG.DOMAIN, 0)
				.set(TAG.NAME, "SEPE")
				.set(TAG.TYPE, (byte)13)
				.returning()
				.fetchOne();
		
		Record aeatTagRecord = dslContext.select().from(TAG).where(TAG.DOMAIN.eq(0)).and(TAG.NAME.eq("AEAT")).and(TAG.TYPE.eq((byte)13)).fetchOne();
		if(null == aeatTagRecord)
			 dslContext.insertInto(TAG)
				.set(TAG.DOMAIN, 0)
				.set(TAG.NAME, "AEAT")
				.set(TAG.TYPE, (byte)13)
				.execute();
		
		// Update TGSS certificates
		
		Result<Record> rattachTgssRecords = dslContext.select().from(RATTACH)
			.where(RATTACH.TYPE.eq((byte)4))
			.and(RATTACH.REGISTRY.in(
					dslContext.select(USER.REGISTRY).from(USER).fetch(USER.REGISTRY)
			)).fetch();
		
		for(Record rattachTgssRecord : rattachTgssRecords) {
			Record newRattachRecord = duplicateRattach(dslContext, rattachTgssRecord);
			
			Integer registryId = newRattachRecord.get(RATTACH.REGISTRY);
			Integer rattachId = newRattachRecord.get(RATTACH.ID);
			Integer domainId = newRattachRecord.get(RATTACH.DOMAIN);
			
			Record1<String> raddinfoRecord = dslContext.select(RADDINFO.VALUE).from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(registryId))
					.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
					.fetchOne();
			
			if(null != raddinfoRecord) {
				String password = raddinfoRecord.get(RADDINFO.VALUE);
				
				dslContext.update(RATTACH)
					.set(RATTACH.DESCRIPTION, "TGSS" + "HIDE(" + password + ")")
					.where(RATTACH.ID.eq(rattachId))
					.execute();
			}
			
			dslContext.insertInto(RATTACH_TAG)
			.set(RATTACH_TAG.DOMAIN, domainId)
			.set(RATTACH_TAG.RATTACH, rattachId)
			.set(RATTACH_TAG.TAG, tgssTagRecord.get(TAG.ID))
			.execute();
		}
		
		// Update SEPE certificates
		
		Result<Record> rattachSepeRecords = dslContext.select().from(RATTACH)
			.where(RATTACH.TYPE.eq((byte)4))
			.and(RATTACH.REGISTRY.in(
					dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).fetch(ENTERPRISE.REGISTRY)
			)).fetch();
		
		for(Record rattachSepeRecord : rattachSepeRecords) {
			Record newRattachRecord = duplicateRattach(dslContext, rattachSepeRecord);
			
			Integer registryId = newRattachRecord.get(RATTACH.REGISTRY);
			Integer rattachId = newRattachRecord.get(RATTACH.ID);
			Integer domainId = newRattachRecord.get(RATTACH.DOMAIN);
			
			Record1<String> raddinfoRecord = dslContext.select(RADDINFO.VALUE).from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(registryId))
					.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
					.fetchOne();
			
			if(null != raddinfoRecord) {
				String password = raddinfoRecord.get(RADDINFO.VALUE);
				
				dslContext.update(RATTACH)
					.set(RATTACH.DESCRIPTION, "SEPE" + "HIDE(" + password + ")")
					.where(RATTACH.ID.eq(rattachId))
					.execute();
			}
			
			dslContext.insertInto(RATTACH_TAG)
			.set(RATTACH_TAG.DOMAIN, domainId)
			.set(RATTACH_TAG.RATTACH, rattachId)
			.set(RATTACH_TAG.TAG, sepeTagRecord.get(TAG.ID))
			.execute();
		}
		
		// Update global certificates
		
		Result<Record> globalCertificateRecords = dslContext.select().from(DATA_ATTACH)
				.where(DATA_ATTACH.SOURCE.eq((byte)16))
				.and(DATA_ATTACH.TYPE.eq((byte)3))
				.fetch();
		
		for(Record globalCertificateRecord : globalCertificateRecords) {
			Integer domainId = globalCertificateRecord.get(DATA_ATTACH.DOMAIN);
			Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
			byte[] data = globalCertificateRecord.get(DATA_ATTACH.DATA);
			String password = globalCertificateRecord.get(DATA_ATTACH.DESCRIPTION);
			
			Integer rattachId = dslContext.insertInto(RATTACH)
					.set(RATTACH.DOMAIN, domainId)
					.set(RATTACH.REGISTRY, registryEntepriseId)
					.set(RATTACH.MIMETYPE, (byte)32)
					.set(RATTACH.TYPE, (byte)4)
					.set(RATTACH.DATA, data)
					.set(RATTACH.SECURITY_LEVEL, (byte)0)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, "Certificado Global TGSSHIDE(" + password + ")")
					.returning(RATTACH.ID)
					.fetchOne()
					.getId();
			
			dslContext.insertInto(RATTACH_TAG)
			.set(RATTACH_TAG.DOMAIN, domainId)
			.set(RATTACH_TAG.RATTACH, rattachId)
			.set(RATTACH_TAG.TAG, tgssTagRecord.get(TAG.ID))
			.execute();
		}
	}

	private Record duplicateRattach(DSLContext dslContext, Record rattachRecord) {
		dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
		RattachRecord newRattachRecord = dslContext.insertInto(RATTACH)
				.set(RATTACH.DOMAIN, rattachRecord.get(RATTACH.DOMAIN))
				.set(RATTACH.REGISTRY, rattachRecord.get(RATTACH.REGISTRY))
				.set(RATTACH.CATEGORY, rattachRecord.get(RATTACH.CATEGORY))
				.set(RATTACH.MIMETYPE, rattachRecord.get(RATTACH.MIMETYPE))
				.set(RATTACH.DESCRIPTION, rattachRecord.get(RATTACH.DESCRIPTION))
				.set(RATTACH.DATA, rattachRecord.get(RATTACH.DATA))
				.set(RATTACH.TYPE, rattachRecord.get(RATTACH.TYPE))
				.set(RATTACH.SCOPE, rattachRecord.get(RATTACH.SCOPE))
				.set(RATTACH.SECURITY_LEVEL, rattachRecord.get(RATTACH.SECURITY_LEVEL))
				.set(RATTACH.ATTACH_DATE, rattachRecord.get(RATTACH.ATTACH_DATE))
				.set(RATTACH.DRIVE_ID, rattachRecord.get(RATTACH.DRIVE_ID))
				.set(RATTACH.DPARENT_ID, rattachRecord.get(RATTACH.DPARENT_ID))
				.set(RATTACH.CREATION_USER, rattachRecord.get(RATTACH.CREATION_USER))
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.MODIFICATION_USER, rattachRecord.get(RATTACH.MODIFICATION_USER))
				.set(RATTACH.MODIFICATION_DATE, rattachRecord.get(RATTACH.MODIFICATION_DATE))
				.returning()
				.fetchOne();
		dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		return newRattachRecord;
	}

}
