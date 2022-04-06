package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CertificatesClean implements Update {

	public static final CertificatesClean CERTIFICATESCLEAN = new CertificatesClean();
	
	private static final String DIGITAL_CERTIFICATE_PASSWORD = "DIGITAL_CERTIFICATE_PASSWORD";
	
	private CertificatesClean() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		// Initialize transaction
		
		dslContext.transaction(t -> {
			
			// Delete old data attach certificates
			int deleteDataAttachs = dslContext.delete(DATA_ATTACH)
				.where(DATA_ATTACH.SOURCE.eq((byte)16)) //Sistema RED
				.and(DATA_ATTACH.TYPE.eq((byte)3)) // Digital Certificate
				.execute();
			
			System.out.println("Delete Data Attachs : " + deleteDataAttachs + " records");
			
			// Delete rattach whith password on raddinfo
			int deleteRAttachs = dslContext.delete(RATTACH)
			.where(RATTACH.TYPE.eq((byte)4)) // Digital Certificate
			.and(RATTACH.REGISTRY.in(
				dslContext.select(RADDINFO.REGISTRY).from(RADDINFO)
					.where(RADDINFO.ATTRIBUTE.eq(DIGITAL_CERTIFICATE_PASSWORD))
					.fetch(RADDINFO.REGISTRY)
			))
			.andNot(RATTACH.ID.in( // Does not have rattach_tag
					dslContext.select(RATTACH_TAG.RATTACH).from(RATTACH_TAG)
						.fetch(RATTACH_TAG.RATTACH)
			)).execute();
			
			System.out.println("Delete RAttachs : " + deleteRAttachs + " records");
			
			// Delete old raddinfo passwords
			int deleteRAddinfo = dslContext.delete(RADDINFO)
				.where(RADDINFO.ATTRIBUTE.eq(DIGITAL_CERTIFICATE_PASSWORD))
				.execute();
			
			System.out.println("Delete RAddinfo : " + deleteRAddinfo + " records");
								
		});
	}

}
