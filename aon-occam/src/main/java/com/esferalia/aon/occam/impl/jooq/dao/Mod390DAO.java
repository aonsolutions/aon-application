package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;


public class Mod390DAO {

	public static LinkedList<Mod390> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL390.fields())
			.from(FS_MODEL390)
			.join(DOMAIN).on(FS_MODEL390.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL390.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL390.YEAR.desc(), FS_MODEL390.NAME.asc(),FS_MODEL390.REPLACEMENT.asc())
			.fetchInto(FsModel390Record.class)
			.stream()
			.map( new Mod390Filler() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	
	private static class Mod390Filler implements Function<Record, Mod390> {
		
		@Override
		public Mod390 apply(Record record) {
			return new Mod390()
				.setId(record.getValue(FS_MODEL390.ID))
				.setAdministration(record.getValue(FS_MODEL390.ADMINISTRATION))
				.setYear(record.getValue(FS_MODEL390.YEAR))
				.setDomain(record.getValue(FS_MODEL390.DOMAIN))
				.setEnterprise(record.getValue(FS_MODEL390.ENTERPRISE))
				.setDocument(record.getValue(FS_MODEL390.DOCUMENT))
				.setEnterpriseName(record.getValue(FS_MODEL390.NAME))
				.setReplacement(record.getValue(FS_MODEL390.REPLACEMENT) == 1);
		}
		
	}
	
}
