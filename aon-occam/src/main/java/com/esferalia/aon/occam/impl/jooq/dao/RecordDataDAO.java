package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.CommercialRegistryCode;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RecordDataType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class RecordDataDAO {

	private RecordDataDAO() {
		
	}

	public static Stream<RecordData> getStream(AONContext ctx, RecordDataType type, Integer registryId) {
		if(type == null) return getStream(ctx, registryId);
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(RECORD_DATA)
				.where(RECORD_DATA.REGISTRY.eq(registryId))
				.and(RECORD_DATA.DOMAIN.eq(ctx.getDomainId()))
				.and(RECORD_DATA.TYPE.eq(type.value()))
				.fetch().stream()
				.map(new RecordDataFiller());
	}
	
	public static Stream<RecordData> getStream(AONContext ctx, Integer registryId) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(RECORD_DATA)
				.where(RECORD_DATA.REGISTRY.eq(registryId))
				.and(RECORD_DATA.DOMAIN.eq(ctx.getDomainId()))
				.fetch().stream()
				.map(new RecordDataFiller());
	}
	
	public static Stream<RecordData> getStream(AONContext ctx, Integer registryId, boolean withData) {
		 List<RecordData> recordDatas = getStream(ctx, registryId).collect(Collectors.toList());
		 
		 if(withData)
			 recordDatas.forEach(recordData -> {
				 if(null != recordData.getAttach())
					 recordData.setFullAttach(AttachmentDAO.getRegistryAttach(ctx, f -> f.getIdProperty().eq(recordData.getAttach()), withData));
			 });
		 
		 return recordDatas.stream();
	}

	public static RecordData get(AONContext ctx, Integer id) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(RECORD_DATA)
				.where(RECORD_DATA.ID.eq(id))
				.and(RECORD_DATA.DOMAIN.eq(ctx.getDomainId()))
				.limit(1)
				.stream()
				.map(new RecordDataFiller())
				.findFirst().orElse(null);
	}

	public static RecordData save(AONContext ctx, RecordData recordData) {
		ctx.checkWrite();
		checkIncorporationExists(ctx, recordData);
		return (recordData.getId() != null && recordData.getId() > 0)
				? update(ctx, recordData)
				: insert(ctx, recordData);
	}
	
	private static void checkIncorporationExists(AONContext ctx, RecordData recordData) {
		if(recordData.getType().equals(RecordDataType.INCORPORATION)) {
			boolean exists = ctx.getDslContext()
					.selectOne()
					.from(RECORD_DATA)
					.where(RECORD_DATA.DOMAIN.eq(recordData.getDomain()))
					.and(RECORD_DATA.REGISTRY.eq(recordData.getRegistry()))
					.and(RECORD_DATA.TYPE.eq(RecordDataType.INCORPORATION.value()))
					.and(recordData.getId() != null && recordData.getId() > 0
							? RECORD_DATA.ID.ne(recordData.getId())
							: org.jooq.impl.DSL.trueCondition())
					.limit(1)
					.fetchOne() != null;
			if (exists) {
				throw new AonCoreException("Ya existe otro registro con el tipo Constituci\u00F3n.");
			}			
		}
	}	
	
	private static RecordData insert(AONContext ctx, RecordData recordData) {
		Integer id = ctx.getDslContext()
				.insertInto(RECORD_DATA)
				.set(RECORD_DATA.DOMAIN, recordData.getDomain())
				.set(RECORD_DATA.REGISTRY, recordData.getRegistry())
				.set(RECORD_DATA.CREATION_DATE, AonDateUtils.toSql(recordData.getCreationDate()))
				.set(RECORD_DATA.DESCRIPTION, recordData.getDescription())
				.set(RECORD_DATA.NOTARY, recordData.getNotary())
				.set(RECORD_DATA.NUMBER, recordData.getNumber())
				.set(RECORD_DATA.RECORD_DATE, AonDateUtils.toSql(recordData.getRecordDate()))
				.set(RECORD_DATA.VOLUME, recordData.getVolume())
				.set(RECORD_DATA.SECTION, recordData.getSection())
				.set(RECORD_DATA.PAGE, recordData.getPage())
				.set(RECORD_DATA.SHEET, recordData.getSheet())
				.set(RECORD_DATA.REGISTRATION, recordData.getRegistration())
				.set(RECORD_DATA.ATTACH, recordData.getAttach())
				.set(RECORD_DATA.TYPE, recordData.getType() != null ? recordData.getType().value() : null)
				.set(RECORD_DATA.IRUS, recordData.getIrus())
				.set(RECORD_DATA.COMMERCIAL_REGISTRY_CODE, recordData.getCommercialRegistryCode() != null ? recordData.getCommercialRegistryCode().value() : null)
				.returning(RECORD_DATA.ID).fetchOne().getId();
		recordData.setId(id);
		ctx.log().debug("INSERT RECORD_DATA id: " + id);
		return recordData;
	}

	private static RecordData update(AONContext ctx, RecordData recordData) {
		ctx.getDslContext()
				.update(RECORD_DATA)
				.set(RECORD_DATA.DESCRIPTION, recordData.getDescription())
				.set(RECORD_DATA.NOTARY, recordData.getNotary())
				.set(RECORD_DATA.NUMBER, recordData.getNumber())
				.set(RECORD_DATA.CREATION_DATE, AonDateUtils.toSql(recordData.getCreationDate()))
				.set(RECORD_DATA.RECORD_DATE, AonDateUtils.toSql(recordData.getRecordDate()))
				.set(RECORD_DATA.VOLUME, recordData.getVolume())
				.set(RECORD_DATA.SECTION, recordData.getSection())
				.set(RECORD_DATA.PAGE, recordData.getPage())
				.set(RECORD_DATA.SHEET, recordData.getSheet())
				.set(RECORD_DATA.REGISTRATION, recordData.getRegistration())
				.set(RECORD_DATA.ATTACH, recordData.getAttach())
				.set(RECORD_DATA.TYPE, recordData.getType() != null ? recordData.getType().value() : null)
				.set(RECORD_DATA.IRUS, recordData.getIrus())
				.set(RECORD_DATA.COMMERCIAL_REGISTRY_CODE, recordData.getCommercialRegistryCode() != null ? recordData.getCommercialRegistryCode().value() : null)
				.where(RECORD_DATA.ID.eq(recordData.getId()))
				.execute();
		ctx.log().debug("UPDATE RECORD_DATA id: " + recordData.getId());
		return recordData;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext()
				.delete(RECORD_DATA)
				.where(RECORD_DATA.ID.eq(id))
				.execute();
		ctx.log().debug("DELETE RECORD_DATA id: " + id);
	}
	
	public static void delete(AONContext ctx, Integer id, boolean deleteData) {
		ctx.checkWrite();
		
		RecordData recordData = get(ctx, id);
		
		if(deleteData && null != recordData.getAttach())
			AttachmentDAO.deleteRegistryAttach(ctx, f -> f.getIdProperty().eq(recordData.getAttach()));
		
		ctx.getDslContext()
				.delete(RECORD_DATA)
				.where(RECORD_DATA.ID.eq(id))
				.execute();
		ctx.log().debug("DELETE RECORD_DATA id: " + id);
	}

	private static class RecordDataFiller extends Filler implements Function<Record, RecordData> {
		@Override
		public RecordData apply(Record r) {
			return new RecordData()
					.setId(getValue(r, RECORD_DATA.ID))
					.setDomain(getValue(r, RECORD_DATA.DOMAIN))
					.setRegistry(getValue(r, RECORD_DATA.REGISTRY))
					.setCreationDate(getValue(r, RECORD_DATA.CREATION_DATE))
					.setDescription(getValue(r, RECORD_DATA.DESCRIPTION))
					.setNotary(getValue(r, RECORD_DATA.NOTARY))
					.setNumber(getValue(r, RECORD_DATA.NUMBER))
					.setRecordDate(getValue(r, RECORD_DATA.RECORD_DATE))
					.setVolume(getValue(r, RECORD_DATA.VOLUME))
					.setSection(getValue(r, RECORD_DATA.SECTION))
					.setPage(getValue(r, RECORD_DATA.PAGE))
					.setSheet(getValue(r, RECORD_DATA.SHEET))
					.setRegistration(getValue(r, RECORD_DATA.REGISTRATION))
					.setAttach(getValue(r, RECORD_DATA.ATTACH))
					.setType(RecordDataType.safeValueOf(getValue(r,RECORD_DATA.TYPE)))
					.setIrus(getValue(r, RECORD_DATA.IRUS))
					.setCommercialRegistryCode(CommercialRegistryCode.safeValueOf(getValue(r, RECORD_DATA.COMMERCIAL_REGISTRY_CODE)));
		}
	}

}
