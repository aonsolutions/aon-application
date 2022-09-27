package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DataResponse;
import com.esferalia.aon.jooq.tables.DataResponseDetail;

import net.aonsolutions.db.up2date.Update;

public class UdapaPfondoUpdateFix implements Update {

	public static final UdapaPfondoUpdateFix UDAPA_PFONDO_UPDATE_FIX = new UdapaPfondoUpdateFix();

	private UdapaPfondoUpdateFix() {
		
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		if(getDomain(dslContext) != null) {
			List<Integer> ids = getIds(dslContext);		
			updatePfondo(dslContext, ids);
		}
	}
	
	private List<Integer> getIds(DSLContext dslContext) {
		Date date = new Date((2022-1900), 7, 22);
		return dslContext.select(DataResponseDetail.DATA_RESPONSE_DETAIL.ID)
				.from(DataResponse.DATA_RESPONSE)
				.join(DataResponseDetail.DATA_RESPONSE_DETAIL).on(DataResponse.DATA_RESPONSE.ID.eq(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_RESPONSE))
				.where(
						DataResponse.DATA_RESPONSE.CODE.greaterOrEqual("22082205")
						.and(DataResponse.DATA_RESPONSE.RESPONSE_DATE.greaterOrEqual(date))
						.and(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq("ufqc2")))
				.fetch().stream().map(r -> r.getValue(DataResponseDetail.DATA_RESPONSE_DETAIL.ID)).collect(Collectors.toCollection(LinkedList::new));
	}
	
	private void updatePfondo(DSLContext dslContext, List<Integer> ids) {
		dslContext.update(DataResponseDetail.DATA_RESPONSE_DETAIL)
			.set(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VALUE, "0.32")
			.where(DataResponseDetail.DATA_RESPONSE_DETAIL.ID.in(ids)).execute();
	}
	
	private Domain getDomain(DSLContext dslContext) {
		return dslContext.select(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID, com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME)
				.from(com.esferalia.aon.jooq.tables.Domain.DOMAIN)
				.where(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID.eq(3049))
				.and(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME.eq("udapa.aonsolutions.net"))
				.fetch().stream().map(r -> new Domain()
						.setId(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID))
						.setName(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME)))
				.findFirst().orElse(null);
	}
	
	public class Domain {
		Integer id;
		String name;

		public Integer getId() {
			return id;
		}
		
		public Domain setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Domain setName(String name) {
			this.name = name;
			return this;
		}
	}
}
