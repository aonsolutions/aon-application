package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DataResponse;
import com.esferalia.aon.jooq.tables.DeliveryInfo;

import net.aonsolutions.db.up2date.Update;

public class DeliveryInfoInsert implements Update {
	
	public static final DeliveryInfoInsert DELIVERY_INFO_INSERT = new DeliveryInfoInsert();

	public static final Byte SERES_DELIVERY = 7;
	
	private DeliveryInfoInsert() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.selectDistinct(DataResponse.DATA_RESPONSE.SOURCE_ID, DataResponse.DATA_RESPONSE.DOMAIN)
				.from(DataResponse.DATA_RESPONSE).where(DataResponse.DATA_RESPONSE.SOURCE.eq(SERES_DELIVERY))
		.fetch().stream()
		.map(r-> new DR()
			.setDomain(r.getValue(DataResponse.DATA_RESPONSE.DOMAIN))
			.setSourceId(r.getValue(DataResponse.DATA_RESPONSE.SOURCE_ID))
		).forEach(dr -> {
			try {
				dslContext.insertInto(DeliveryInfo.DELIVERY_INFO)
				.set(DeliveryInfo.DELIVERY_INFO.DOMAIN, dr.getDomain())
				.set(DeliveryInfo.DELIVERY_INFO.DELIVERY, dr.getSourceId())
				.set(DeliveryInfo.DELIVERY_INFO.TYPE, (byte) 0)
				.set(DeliveryInfo.DELIVERY_INFO.STATUS, (byte) 1)
				.execute();
			} catch (Exception e) {
				System.out.println("ERROR: No existe albaran " + dr.getSourceId());
			}
		});	
	}	
	
	class DR {
		Integer domain;
		Integer sourceId;
		
		public Integer getDomain() {
			return domain;
		}
		
		public DR setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
		
		public Integer getSourceId() {
			return sourceId;
		}
		
		public DR setSourceId(Integer sourceId) {
			this.sourceId = sourceId;
			return this;
		}
	}
}