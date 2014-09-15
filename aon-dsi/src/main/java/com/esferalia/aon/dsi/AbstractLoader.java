package com.esferalia.aon.dsi;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Identity;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Table;

import com.esferalia.aon.dsi.util.DBUtils;
import com.esferalia.aon.jooq.tables.Domain;

public abstract class AbstractLoader {

	protected DSLContext dsiContext;
	protected DSLContext aonContext;

	private ResourceBundle municipalities;

	public AbstractLoader(DSLContext dsiContext, DSLContext aonContext) {
		this.dsiContext = dsiContext;
		this.aonContext = aonContext;

		this.municipalities = ResourceBundle
				.getBundle("com.code.aon.common.i18n.municipalities");
	}

	protected Integer next(Identity<?, Integer> identity) {
		return DBUtils.next(aonContext, identity);
	}

	protected <R extends Record> Integer getId(Identity<R, Integer> identity,
			Condition... conditions) {
		//@formatter:off
		Integer id = aonContext.select(identity.getField())
				.from(identity.getTable())
				.where(conditions)
				.fetchOne(identity.getField());
		//@formatter:on
		return id != null ? id : next(identity);
	}

	protected <R extends Record> InsertSetStep<R> get(
			InsertSetMoreStep<R> insertSetMoreStep, Table<R> table) {
		return insertSetMoreStep != null ? insertSetMoreStep.newRecord()
				: aonContext.insertInto(table);

	}

	public String getMunicipality(String poblaci) {
		for (String key : municipalities.keySet())
			if (StringUtils.equalsIgnoreCase(municipalities.getString(key),
					poblaci))
				return key;
		return null;
	}

	public Integer getGeozone(String provin, int domain) {
		List<Integer> geozones = 
		//@formatter:off
		aonContext.select(GEOZONE.ID)
			.from(GEOZONE)
			.where(GEOZONE.NAME.equalIgnoreCase(provin))
			.and(GEOZONE.DOMAIN.eq(domain))
			.fetch(GEOZONE.ID);
		//@formatter:on
		if (geozones.size() > 0)
			return geozones.get(0);

		
		//@formatter:off
		Integer parent = aonContext
				.select(DOMAIN.PARENT)
				.from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.fetchOne(DOMAIN.PARENT);
		//@formatter:on
		
		return parent == null ? null : getGeozone(provin, parent);
	}

}
