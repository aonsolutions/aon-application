package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Timestamp;
import java.util.function.Function;

import org.jooq.Record;

import net.aonsolutions.occam.api.config.Audit;

public class Fillers {
	
	private Fillers() {
		
	}
	
	
	static class AuditFiller implements Function<Record,Audit> {
		private static final String CREATION_USER = DOMAIN.CREATION_USER.getName();
		private static final String CREATION_DATE = DOMAIN.CREATION_DATE.getName();
		private static final String MODIFICATION_USER = DOMAIN.MODIFICATION_USER.getName();
		private static final String MODIFICATION_DATE = DOMAIN.MODIFICATION_DATE.getName();
		@Override
		public Audit apply(Record r) {
			return new Audit()
				.setCreationUser(r.getValue( CREATION_USER , String.class))
				.setCreationDate(r.getValue(CREATION_DATE , Timestamp.class))
				.setModificationUser(r.getValue( MODIFICATION_USER , String.class))
				.setModificationDate(r.getValue(MODIFICATION_DATE, Timestamp.class))
			;
		}

	}
	
}
