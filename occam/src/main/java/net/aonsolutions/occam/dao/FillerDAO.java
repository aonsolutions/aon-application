package net.aonsolutions.occam.dao;

import java.util.function.BiFunction;

import org.jooq.Record;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.dao.SecurityDAO.ScopeFiller;

class FillerDAO {
	
	
	
	private FillerDAO() {
		
	}
	
	static class DomainFiller implements BiFunction<Record, com.esferalia.aon.jooq.tables.Domain, Domain> {
		@Override
		public Domain apply(Record rec, com.esferalia.aon.jooq.tables.Domain table) {
			return new Domain()
				.setId(FillerUtils.getValue(rec,table.ID))
				.setName(FillerUtils.getValue(rec,table.NAME))
				.setDescription(FillerUtils.getValue(rec,table.DESCRIPTION))
				.setType( DomainType.safeValueOf( FillerUtils.getValue(rec,table.TYPE)).orElse(null))
				.setScope( FillerUtils.getValue(rec,table.SCOPE) == null ? null : new ScopeFiller().apply(rec))
				.setEnableHeredity(FillerUtils.getBoolean(rec, table.ENABLEHEREDITY))
				.setActive(FillerUtils.getBoolean(rec, table.ACTIVE))
				.setDirty(false)
			;
		}
		
	}
	
	static class RegistryFiller implements BiFunction<Record, com.esferalia.aon.jooq.tables.Registry, Registry> {
		@Override
		public Registry apply(Record rec, com.esferalia.aon.jooq.tables.Registry table) {
			return new Registry()
				.setId(FillerUtils.getValue(rec, table.ID))
				.setDomain(FillerUtils.getValue(rec, table.DOMAIN))
				.setDocument(FillerUtils.getValue(rec, table.DOCUMENT))
				.setDocumentType( DocumentType.safeValueOf( FillerUtils.getValue(rec,table.DOCUMENT_TYPE)).orElse(null))
				.setDocumentCountry( Country.safeValueOf( FillerUtils.getValue(rec,table.DOCUMENT_COUNTRY)).orElse(null))
				.setName(FillerUtils.getValue(rec, table.NAME))
				.setAlias(FillerUtils.getValue(rec, table.ALIAS))
				.setNationality( Country.safeValueOf( FillerUtils.getValue(rec,table.NATIONALITY)).orElse(null))
				.setConfidential(FillerUtils.getConfidential(rec, table.SECURITY_LEVEL))
				.setDirty(false)
			;
		}
	}
	
}
