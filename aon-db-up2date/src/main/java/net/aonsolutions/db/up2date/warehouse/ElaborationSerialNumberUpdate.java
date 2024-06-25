package net.aonsolutions.db.up2date.warehouse;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ElaborationDetail;
import com.esferalia.aon.jooq.tables.Item;

import net.aonsolutions.db.up2date.Update;

public class ElaborationSerialNumberUpdate implements Update {

	private static final Logger LOGGER  = Logger.getLogger(ElaborationSerialNumberUpdate.class.getName());
	public static final ElaborationSerialNumberUpdate ELABORATION_SERIAL_NUMBER_UPDATE= new ElaborationSerialNumberUpdate();

	private ElaborationSerialNumberUpdate() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		LOGGER.info("ELABORATION");
		if(getDomain(dslContext) != null) {
			List<Elaboration> elaborations = getElaborations(dslContext);		
			elaborations.stream().forEach(elaboration -> updateElaboration(dslContext, elaboration));
		}

		LOGGER.info("[END]");
	}

	private Domain getDomain(DSLContext dslContext) {
		return dslContext.select(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID, com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME)
				.from(com.esferalia.aon.jooq.tables.Domain.DOMAIN)
				.where(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID.eq(8786))
				.and(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME.eq("paturpat.aibanez.net"))
				.fetch().stream().map(r -> new Domain()
						.setId(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID))
						.setName(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME)))
				.findFirst().orElse(null);
	}
	
	private List<Elaboration> getElaborations(DSLContext dslContext) {
		return dslContext.select(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.ID,
				com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.ITEM,
				com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.DESCRIPTION)
			.from(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION)
			.where(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.DOMAIN.eq(8786))
			.and(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.DESCRIPTION.notLike("%#%"))
			.fetch().stream().map(r -> new Elaboration()
					.setId(r.getValue(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.ID))
					.setDescription(r.getValue(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.DESCRIPTION))
					.setItem(r.getValue(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.ITEM))
			).toList();	
	}
	
	private void updateElaboration(DSLContext dslContext, Elaboration elaboration) {
		Integer item = dslContext.select(ElaborationDetail.ELABORATION_DETAIL.ITEM)
		.from(ElaborationDetail.ELABORATION_DETAIL)
		.where(ElaborationDetail.ELABORATION_DETAIL.ELABORATION.eq(elaboration.getId()))
		.and(ElaborationDetail.ELABORATION_DETAIL.TYPE.eq((byte)0))
		.fetch().stream().map(r -> r.getValue(ElaborationDetail.ELABORATION_DETAIL.ITEM))
		.findFirst().orElse(null);
		if(item != null) {
			String serialNumber = dslContext.select(Item.ITEM.SERIAL_NUMBER)
					.from(Item.ITEM)
					.where(Item.ITEM.ID.eq(item))
					.fetch().stream().map(r -> 
						r.getValue(Item.ITEM.SERIAL_NUMBER) != null ? r.getValue(Item.ITEM.SERIAL_NUMBER) : "")
					.findFirst().orElse(null);
					
			if(serialNumber != null && !serialNumber.isBlank()) {
				dslContext.update(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION)
				.set(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.DESCRIPTION, elaboration.getDescription() + " #" + serialNumber)
				.where(com.esferalia.aon.jooq.tables.Elaboration.ELABORATION.ID.eq(elaboration.getId()))
				.execute();
			}	
		}
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
	
	public class Elaboration {
		Integer id;
		Integer item;
		String description;

		public Integer getId() {
			return id;
		}
		
		public Elaboration setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public String getDescription() {
			return description;
		}
		
		public Elaboration setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Integer getItem() {
			return item;
		}
		
		public Elaboration setItem(Integer item) {
			this.item = item;
			return this;
		}
		
	}
}
