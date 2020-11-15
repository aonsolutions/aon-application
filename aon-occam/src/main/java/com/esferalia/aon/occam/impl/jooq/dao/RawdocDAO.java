package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rawdoc.RAWDOC;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Properties.RawdocProperties;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.impl.jooq.validation.RawdocValidation;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.json.TediInvoiceJSON;

public class RawdocDAO {
	private static final RawdocPropertiesDAO RAWDOC_PROPERTIES = new RawdocPropertiesDAO();
	
	private static class RawdocPropertiesDAO implements RawdocProperties {
		private Condition[] getConditions(RawdocFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RAWDOC.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RAWDOC.DOMAIN);}
		@Override public Property<Byte> getNatureProperty() {return new FilterDAO.PropertyDAO<Byte>(RAWDOC.NATURE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(RAWDOC.TYPE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(RAWDOC.STATUS);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.TimestampPropertyDAO(RAWDOC.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(RAWDOC.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.TimestampPropertyDAO(RAWDOC.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(RAWDOC.MODIFICATION_USER);}
	}
	
	private static class RawdocFiller  implements Function<Record,Rawdoc> {
		@Override
		public Rawdoc apply(Record record) {
			return new Rawdoc()
				.setId(record.getValue(RAWDOC.ID))
				.setDomain(record.getValue(RAWDOC.DOMAIN))
				.setNature(RawdocNature.safeValueOf( record.getValue(RAWDOC.NATURE)))
				.setType(RawdocType.safeValueOf( record.getValue(RAWDOC.TYPE)))
				.setStatus(RawdocStatus.safeValueOf( record.getValue(RAWDOC.STATUS)))
				.setJson(record.getValue(RAWDOC.JSON))
				.setTediInvoice( AonStringUtils.isBlank(record.getValue(RAWDOC.JSON)) 
						? null 
						: TediInvoiceJSON.fromJSON( new JSONObject(record.getValue(RAWDOC.JSON) ) ) )
				.setLog(record.getValue(RAWDOC.LOG))
				.setMimeType(MimeType.safeValueOf( record.getValue(RAWDOC.MIME_TYPE)))
				.setCreationUser(record.getValue(RAWDOC.CREATION_USER))
				.setCreationDate(record.getValue(RAWDOC.CREATION_DATE))
				.setModificationUser(record.getValue(RAWDOC.MODIFICATION_USER))
				.setModificationDate(record.getValue(RAWDOC.MODIFICATION_DATE))
				;
		}
	}
	
	private static class FullRawdocFiller  extends RawdocFiller {
		@Override
		public Rawdoc apply(Record record) {
			return super.apply(record)
				.setData(record.getValue(RAWDOC.DATA));
		}
	}

	private static Field<?>[] SELECT_FIELDS = new Field[]{
		 RAWDOC.ID		,RAWDOC.DOMAIN	,RAWDOC.NATURE	,RAWDOC.TYPE
		,RAWDOC.STATUS	,RAWDOC.JSON	,RAWDOC.LOG		,RAWDOC.MIME_TYPE
		,RAWDOC.CREATION_USER			,RAWDOC.CREATION_DATE
		,RAWDOC.MODIFICATION_USER		,RAWDOC.MODIFICATION_DATE
	}; 

	
	public static Stream<Rawdoc> get(AONContext ctx, RawdocFilter filter) {
		return get(ctx, filter, 0, Integer.MAX_VALUE);
	}
	
	public static Stream<Rawdoc> get(AONContext ctx, RawdocFilter filter, int offset, int limit) {
		return ctx.getDslContext()
				.select( SELECT_FIELDS )
				.from(RAWDOC)
				.where(RAWDOC_PROPERTIES.getConditions(filter))
				.limit(offset,limit)
				.fetch()
				.stream()
				.map(new RawdocFiller());
	}

	public static Rawdoc get(AONContext ctx, Integer id) {
		return ctx.getDslContext()
				.select( SELECT_FIELDS )
				.from(RAWDOC)
				.where(RAWDOC.ID.eq(id))
				.fetch()
				.stream()
				.map(new RawdocFiller())
				.findFirst()
				.orElse(null);
	}
	
	public static Rawdoc getFull(AONContext ctx, Integer id) {
		return ctx.getDslContext()
				.select( RAWDOC.fields() )
				.from(RAWDOC)
				.where(RAWDOC.ID.eq(id))
				.fetch()
				.stream()
				.map(new FullRawdocFiller())
				.findFirst()
				.orElse(null);
	}
	
	public static Rawdoc insert(AONContext ctx, Rawdoc rawdoc) {
		ctx.checkWrite();
		RawdocValidation.validateRawdoc(ctx, rawdoc);
		Integer id = ctx.getDslContext()
			.insertInto(RAWDOC)
			.set(RAWDOC.DOMAIN,rawdoc.getDomain())
			.set(RAWDOC.NATURE,rawdoc.getNature().value())
			.set(RAWDOC.TYPE  ,rawdoc.getType().value())
			.set(RAWDOC.STATUS,rawdoc.getStatus().value())
			.set(RAWDOC.JSON,rawdoc.getJson())
			.set(RAWDOC.LOG,rawdoc.getLog())
			.set(RAWDOC.MIME_TYPE,rawdoc.getMimeType() == null? null : rawdoc.getMimeType().value())
			.set(RAWDOC.DATA,rawdoc.getData())
			.set(RAWDOC.CREATION_USER,ctx.getUser())
			.set(RAWDOC.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(RAWDOC.ID)
			.fetchOne()
			.getValue(RAWDOC.ID);
		ctx.log().info("INSERT RAWDOC id: " + id);
		return get(ctx, id);
	}
	
	public static void delete(AONContext ctx, Integer domain, Integer id) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.delete(RAWDOC)
			.where(RAWDOC.ID.equal(id))
			.execute();
		ctx.log().info("DELETE RAWDOC domain: " + domain + "; id: " + id + " ("+count+" filas)");
	}

	public static LinkedList<RawdocDomainData> getDomainData(AONContext ctx, int domain) {
		TreeMap<String,RawdocDomainData> map = new TreeMap<String,RawdocDomainData>();
		AggregateFunction<Integer> COUNT = DSL.count(RAWDOC.ID);
		ctx.getDslContext()
			.select( DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,RAWDOC.NATURE,RAWDOC.TYPE,RAWDOC.STATUS, COUNT)
			.from(RAWDOC)
			.innerJoin(DOMAIN).on(DOMAIN.ID.eq(RAWDOC.DOMAIN))
			.where(RAWDOC.DOMAIN.eq(domain))
			.groupBy(DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,RAWDOC.NATURE,RAWDOC.TYPE,RAWDOC.STATUS)
			.fetch()
			.stream()
			.forEach(record -> {
				String desc = record.getValue(DOMAIN.DESCRIPTION);
				RawdocDomainData data = map.get(desc);
				if (data == null) {
					data = new RawdocDomainData()
							.setDomain(record.getValue(DOMAIN.ID))
							.setDomainName(record.getValue(DOMAIN.NAME))
							.setDomainDescription(record.getValue(DOMAIN.DESCRIPTION));
					map.put(desc, data);
				}
				data.getInvoiceBreakdown().putBreakdown(
					 RawdocType.safeValueOf( record.getValue(RAWDOC.TYPE))
					,RawdocStatus.safeValueOf( record.getValue(RAWDOC.STATUS))
					,record.getValue(COUNT));
			});
		return new LinkedList<RawdocDomainData>( map.values() );
	}

	public static void toDraft(AONContext ctx, Integer rawdocId) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.STATUS,RawdocStatus.DRAFT.value())
			.where(RAWDOC.ID.equal(rawdocId))
			.execute();
		ctx.log().info("UPDATE RAWDOC (DRAFT) id: " + rawdocId + " ("+count+" filas)");
	}

	public static void toRejected(AONContext ctx, Integer rawdocId, String reason) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.STATUS,RawdocStatus.REJECTED.value())
			.where(RAWDOC.ID.equal(rawdocId))
			.execute();
		ctx.log().info("UPDATE RAWDOC (REJECTED) id: " + rawdocId + " ("+count+" filas)");
	}

	public static void toInbox(AONContext ctx, Integer rawdocId) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.STATUS,RawdocStatus.INBOX.value())
			.where(RAWDOC.ID.equal(rawdocId))
			.execute();
		ctx.log().info("UPDATE RAWDOC (INBOX) id: " + rawdocId + " ("+count+" filas)");
	}
}
