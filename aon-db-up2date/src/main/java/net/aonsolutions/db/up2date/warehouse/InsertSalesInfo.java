package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DataResponse;
import com.esferalia.aon.jooq.tables.DataResponseDetail;
import com.esferalia.aon.jooq.tables.SalesInfo;

import net.aonsolutions.db.up2date.Update;

public class InsertSalesInfo implements Update {

	public static final InsertSalesInfo INSERT_SALES_INFO = new InsertSalesInfo();

	private InsertSalesInfo() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");

		dslContext.select().from(DataResponse.DATA_RESPONSE).where(DataResponse.DATA_RESPONSE.SOURCE.eq((byte) 19)).fetch().stream().forEach(r -> {
			Integer dr = r.getValue(DataResponse.DATA_RESPONSE.ID);
			Integer domain = r.getValue(DataResponse.DATA_RESPONSE.DOMAIN);
			Integer sales = r.getValue(DataResponse.DATA_RESPONSE.SOURCE_ID);
			
			System.out.println("*********************");
			System.out.println("Data Response: " + dr);
			System.out.println("Domain: " + domain);
			System.out.println("Sales: " + sales);
			

			
			String status = dslContext.select(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VALUE)
			.from(DataResponseDetail.DATA_RESPONSE_DETAIL)
			.where(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(dr))
			.orderBy(DataResponseDetail.DATA_RESPONSE_DETAIL.ID.desc())
			.limit(1)
			.fetch().stream().map(record -> record.getValue(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VALUE))
			.findFirst().orElse(null);

			Integer st = null;
			if("PENDING".equals(status)) {
				st = 0;
			}

			if("RETRIEVED".equals(status)) {
				st = 1;
			}
			
			if("REOPENED".equals(status)) {
				st = 2;
			}
			
			if("PROCESSED".equals(status)) {
				st = 3;
			}
			
			if(st != null) {
				dslContext.insertInto(SalesInfo.SALES_INFO)
				.set(SalesInfo.SALES_INFO.DOMAIN, domain)
				.set(SalesInfo.SALES_INFO.SALES, sales)
				.set(SalesInfo.SALES_INFO.TYPE, (byte) 0)
				.set(SalesInfo.SALES_INFO.STATUS, st.byteValue())
				.execute();
				
				System.out.println("INSERT INTO SALES_INFO");
			}
		});
		
		System.out.println("[END]");
	}
	
}
