package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rawdoc.RAWDOC;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.records.RawdocRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.RawdocProperties;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounterDetail;
import com.esferalia.aon.occam.api.model.RawdocNotice;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.json.TediInvoiceJSON;

public class RawdocDAO {
	
	private static final String LOG_DATE_FORMAT_PATTERN = "dd/MM/yyyy HH:mm:ss";

	private static final RawdocPropertiesDAO RAWDOC_PROPERTIES = new RawdocPropertiesDAO();
	
	private static class RawdocPropertiesDAO implements RawdocProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RawdocFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		private Condition[] getConditions(RawdocFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.DOMAIN);}
		@Override public Property<Byte> getNatureProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.NATURE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.TYPE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.STATUS);}
		@Override public Property<String> getJsonProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.JSON);}		
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.TimestampPropertyDAO(RAWDOC.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.TimestampPropertyDAO(RAWDOC.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.MODIFICATION_USER);}
	}
	
	private static class RawdocFiller extends Filler implements Function<Record,Rawdoc> {
		@Override
		public Rawdoc apply(Record r) {
			return build(r);
		}
		
		public static Rawdoc build(Record r) {
			return new Rawdoc()
			.setId(getValue(r, RAWDOC.ID))
			.setDomain(getValue(r, RAWDOC.DOMAIN))
			.setNature(RawdocNature.safeValueOf(getValue(r, RAWDOC.NATURE)))
			.setType(RawdocType.safeValueOf(getValue(r, RAWDOC.TYPE)))
			.setStatus(RawdocStatus.safeValueOf(getValue(r, RAWDOC.STATUS)))
			.setJson(getValue(r, RAWDOC.JSON))
			.setTediInvoice(AonStringUtils.isBlank(getValue(r, RAWDOC.JSON)) 
					? null 
					: TediInvoiceJSON.fromJSON( new JSONObject(r.getValue(RAWDOC.JSON) ) ) )
			.setInvoice(AonStringUtils.isBlank(getValue(r, RAWDOC.JSON))
					? new Invoice()
					: InvoiceJSON.fromJSON(getValue(r, RAWDOC.JSON)))
			.setLog(getValue(r, RAWDOC.LOG))
			.setMimeType(MimeType.safeValueOf(getValue(r, RAWDOC.MIME_TYPE)))
			.setData(getValue(r,RAWDOC.DATA))
			.setS3Key(getValue(r, RAWDOC.S3_KEY))
			.setCreationUser(getValue(r, RAWDOC.CREATION_USER))
			.setCreationDate(getValue(r, RAWDOC.CREATION_DATE))
			.setModificationUser(getValue(r, RAWDOC.MODIFICATION_USER))
			.setModificationDate(getValue(r, RAWDOC.MODIFICATION_DATE));
		}
	}
	
	private static class RawdocAttachFiller  implements Function<Record,Attach> {
		@Override
		public Attach apply(Record r) {
			return new Attach()
				.setId(r.getValue(RAWDOC.ID))
				.setDomain(new com.esferalia.aon.occam.api.model.Domain().setId(r.getValue(RAWDOC.DOMAIN)))
				.setAttachType(AttachType.RAWDOC)
				.setData(r.getValue(RAWDOC.DATA))
				.setDescription(RawdocNature.safeValueOf( r.getValue(RAWDOC.NATURE)).getDescription())
				.setMimeType(MimeType.safeValueOf( r.getValue(RAWDOC.MIME_TYPE)))
				.setCreationUser(r.getValue(RAWDOC.CREATION_USER))
				.setCreationDate(r.getValue(RAWDOC.CREATION_DATE))
				.setModificationUser(r.getValue(RAWDOC.MODIFICATION_USER))
				.setModificationDate(r.getValue(RAWDOC.MODIFICATION_DATE));
		}
	}

	private static final Field<?>[] SELECT_FIELDS = new Field[]{
		 RAWDOC.ID		,RAWDOC.DOMAIN	,RAWDOC.NATURE	,RAWDOC.TYPE
		,RAWDOC.STATUS	,RAWDOC.JSON	,RAWDOC.LOG		,RAWDOC.MIME_TYPE
		,RAWDOC.S3_KEY
		,RAWDOC.CREATION_USER			,RAWDOC.CREATION_DATE
		,RAWDOC.MODIFICATION_USER		,RAWDOC.MODIFICATION_DATE
	}; 
	
	private RawdocDAO() {
		
	}
	
	public static Stream<Rawdoc> get(AONContext ctx, RawdocFilter filter) {
		return get(ctx, filter, 0, Integer.MAX_VALUE);
	}
	
	public static Stream<Rawdoc> get(AONContext ctx, RawdocFilter filter, int offset, int limit) {
		return ctx.getDslContext()
				.select( SELECT_FIELDS )
				.from(RAWDOC)
				.where(RAWDOC_PROPERTIES.getConditions(filter))
				.orderBy(RAWDOC.ID.desc())
				.limit(offset,limit)
				.fetch()
				.stream()
				.map(new RawdocFiller());
	}

	public static Optional<Rawdoc> get(AONContext ctx, Integer id) {
		return ctx.getDslContext()
			.select( SELECT_FIELDS )
			.from(RAWDOC)
			.where(RAWDOC.ID.eq(id))
			.fetch()
			.stream()
			.map(new RawdocFiller())
			.findFirst();
	}
	
	public static Optional<byte[]> getRawdocData(AONContext ctx, Integer rawdocId){
		return ctx.getDslContext()
			.select( RAWDOC.DATA )
			.from(RAWDOC)
			.where(RAWDOC.ID.eq(rawdocId))
			.fetch()
			.stream()
			.map( r -> r.getValue(RAWDOC.DATA) )
			.filter( b -> b != null)
			.findFirst();
	}
	
	public static Stream<Attach> getRawdocAttachStream(AONContext ctx, RawdocFilter filter){	
		SelectJoinStep<Record> select = ctx.getDslContext().select().from(RAWDOC);
		return RAWDOC_PROPERTIES.build(select, filter).fetchInto(RAWDOC).stream().map(new RawdocAttachFiller());
	}
	
	public static Stream<Rawdoc> getFull(AONContext ctx, RawdocFilter filter, int offset, int limit) {
		return ctx.getDslContext()
				.select( RAWDOC.fields() )
				.from(RAWDOC)
				.where(RAWDOC_PROPERTIES.getConditions(filter))
				.limit(offset,limit)
				.fetch()
				.stream()
				.map(new RawdocFiller());
	}
	
	public static Optional<Rawdoc> getFull(AONContext ctx, Integer id) {
		return ctx.getDslContext()
			.select( RAWDOC.fields() )
			.from(RAWDOC)
			.where(RAWDOC.ID.eq(id))
			.fetch()
			.stream()
			.map(new RawdocFiller())
			.findFirst();
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
			.set(RAWDOC.S3_KEY, rawdoc.getS3Key())
			.set(RAWDOC.CREATION_USER,ctx.getUser())
			.set(RAWDOC.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(RAWDOC.ID)
			.fetchOne()
			.getValue(RAWDOC.ID);
		ctx.log().info("INSERT RAWDOC id: " + id);
		return get(ctx, id)
			.orElseThrow(() -> new AonCoreException( AonError.INVALID_INSERT.getMessage()));
	}
	
	private static Rawdoc update(AONContext ctx, Rawdoc rawdoc) {
		ctx.checkWrite();

		Rawdoc r = get(ctx, rawdoc.getId()).orElse(null);
		if (r == null || r.getId() == null) {
			return insert(ctx, rawdoc);
		}
		
		RawdocValidation.validateRawdoc(ctx, rawdoc);
		UpdateSetMoreStep<RawdocRecord> stmt = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.DOMAIN,rawdoc.getDomain())
			.set(RAWDOC.NATURE,rawdoc.getNature().value())
			.set(RAWDOC.TYPE  ,rawdoc.getType().value())
			.set(RAWDOC.STATUS,rawdoc.getStatus().value())
			.set(RAWDOC.JSON,rawdoc.getJson())
			.set(RAWDOC.LOG, rawdoc.getLog() != null ? rawdoc.getLog() : getLogArray(ctx, r, rawdoc.getStatus(), null))
			.set(RAWDOC.S3_KEY, rawdoc.getS3Key())
			.set(RAWDOC.MODIFICATION_USER, ctx.getUser())
			.set(RAWDOC.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()));
		if (rawdoc.getData() != null && rawdoc.getMimeType() != null) {
			stmt.set(RAWDOC.MIME_TYPE,rawdoc.getMimeType() == null? null : rawdoc.getMimeType().value())
				.set(RAWDOC.DATA,rawdoc.getData());
		}
		stmt.where(RAWDOC.ID.eq(rawdoc.getId())).execute();
		ctx.log().info("UPDATE RAWDOC id: " + rawdoc.getId());
		return get(ctx, rawdoc.getId())
			.orElseThrow(() -> new AonCoreException( AonError.INVALID_UPDATE.getMessage()));
	}
	
	public static Rawdoc save(AONContext ctx, Rawdoc rawdoc) {
		ctx.checkWrite();
		return rawdoc.getId() != null
			? update(ctx, rawdoc)
			: insert(ctx, rawdoc);
	}
	
	public static Rawdoc save(AONContext ctx, Integer rawdocId, String invoiceJson) {
		ctx.checkWrite();
		if (rawdocId == null || AonMathUtils.isZero(rawdocId)) {
			throw new AonCoreException(AonError.EMPTY_DATA.format("ID"));
		}
		if (AonStringUtils.isBlank(invoiceJson)) {
			throw new AonCoreException(AonError.EMPTY_DATA.format("InvoiceJSON"));
		}
		int count = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.JSON,invoiceJson)
			.set(RAWDOC.MODIFICATION_USER, ctx.getUser())
			.set(RAWDOC.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(RAWDOC.ID.eq(rawdocId))
			.execute();
		ctx.log().info("UPDATE invoiceJSON RAWDOC id: {0} ({1} filas)",rawdocId,count);
		return get(ctx, rawdocId )
			.orElseThrow(() -> new AonCoreException( AonError.INVALID_UPDATE.getMessage()));
	}
	
	public static void delete(AONContext ctx, RawdocFilter filter) {
		ctx.checkWrite();
		
		int count = ctx.getDslContext()
			.delete(RAWDOC)
			.where(RAWDOC_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().info("DELETE RAWDOC {0} ({1} filas)",filter.toString(),count);
	}
	
	public static void delete(AONContext ctx, Integer domain, Integer id) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.delete(RAWDOC)
			.where(RAWDOC.ID.equal(id))
			.and(RAWDOC.DOMAIN.equal(domain))
			.execute();
		ctx.log().info("DELETE RAWDOC domain: {0}; id: {1} ({2} filas)",id,count);
	}

	public static RawdocUserData getUserData(AONContext ctx, int domain){
		AggregateFunction<Integer> countField = DSL.count(RAWDOC.ID);
		
		RawdocUserData rawdocUserData = new RawdocUserData();
		
		ctx.getDslContext()
			.select(DOMAIN.NAME, RAWDOC.DOMAIN, RAWDOC.NATURE, RAWDOC.STATUS, countField)
			.from(RAWDOC)
			.join(DOMAIN).on(RAWDOC.DOMAIN.eq(DOMAIN.ID))
			.where(DOMAIN.ID.eq(domain))
			.groupBy(RAWDOC.DOMAIN,RAWDOC.NATURE, RAWDOC.STATUS)
			.fetch()
			.stream()
			.forEach(rec -> {
				RawdocStatus status = RawdocStatus.safeValueOf( rec.getValue(RAWDOC.STATUS));
				if(rawdocUserData.getInvoiceNotice().containsKey(status)) {
					RawdocNotice notice = rawdocUserData.getInvoiceNotice().get(status);
					notice.setCount(notice.getCount() + rec.getValue(countField));
					notice.getDomains().add(rec.getValue(RAWDOC.DOMAIN));
					notice.getDomainCount().put(rec.getValue(DOMAIN.NAME), rec.getValue(countField));
					rawdocUserData.getInvoiceNotice().put(status, notice);
				} else {
					LinkedList<Integer> ds = new LinkedList<>();
					ds.add(rec.getValue(RAWDOC.DOMAIN));
					HashMap<String, Integer> domainCount = new HashMap<>();
					domainCount.put(rec.getValue(DOMAIN.NAME), rec.getValue(countField));
					rawdocUserData.getInvoiceNotice()
						.put(status, new RawdocNotice()
							.setCount(rec.getValue(countField))
							.setDomains(ds)
							.setDomainCount(domainCount));
				}
			});
		return rawdocUserData;
	}
	
	public static RawdocInvoiceCounter getInvoiceCounter(AONContext ctx) {
		RawdocInvoiceCounter counter = new RawdocInvoiceCounter();
		
		AggregateFunction<Integer> countField = DSL.count(RAWDOC.ID);
		Field<Boolean> ticket = DSL.decode()
			.when(DSL.position(RAWDOC.JSON,"\"TICKET\"").greaterThan(0), true)
			.otherwise( false);
				
		ctx.getDslContext().select(RAWDOC.STATUS, RAWDOC.TYPE, ticket, countField)
		.from(RAWDOC)
		.where(RAWDOC.DOMAIN.eq(ctx.getDomainId()))
		.groupBy(RAWDOC.STATUS, RAWDOC.TYPE, ticket)
		.fetch().stream().forEach(r -> {
			boolean isTicket = r.getValue(ticket);
			RawdocStatus status = RawdocStatus.safeValueOf(r.getValue(RAWDOC.STATUS));
			RawdocType type = RawdocType.safeValueOf(r.getValue(RAWDOC.TYPE));
			TediInvoiceType tediType = null;
			if ( type == RawdocType.OUTPUT) {
				tediType = TediInvoiceType.EMITIDA;
			} else {
				tediType = isTicket?TediInvoiceType.TICKET: TediInvoiceType.RECIBIDA;
			}
			Integer count = r.getValue(countField);
			counter.getMap().computeIfAbsent(status, k -> new RawdocInvoiceCounterDetail());
			counter.getMap().get(status).addCount(count);
			if(RawdocStatus.INBOX.equals(status)) {
				counter.getMap().get(status).getMap().put(tediType, count);
			}
		});
		
		return counter;
	}
	
	public static RawdocUserData getUserData(AONContext ctx, byte[] auth){
		Integer[] userScopes = AuthDAO.getAuthScopes(ctx, auth);
		Integer[] domains = AuthDAO.getAuthDomains(ctx, auth);
		
		Domain domain = DOMAIN.as("d");
		Domain parent = DOMAIN.as("p");
		AggregateFunction<Integer> countField = DSL.count(RAWDOC.ID);
		
		RawdocUserData rawdocUserData = new RawdocUserData();
		
		ctx.getDslContext()
			.select(domain.NAME, RAWDOC.DOMAIN, RAWDOC.NATURE, RAWDOC.STATUS, countField)
			.from(RAWDOC)
			.join(domain).on(RAWDOC.DOMAIN.eq(domain.ID))
			.leftOuterJoin(SCOPE).on(domain.SCOPE.eq(SCOPE.ID))
			.leftOuterJoin(parent).on(domain.PARENT.eq(parent.ID))
			.where(domain.ID.in(domains)
					.or(domain.PARENT.in(domains)
						.and(domain.SCOPE.isNull().or(domain.SCOPE.in(userScopes)))))
			.groupBy(RAWDOC.DOMAIN, RAWDOC.NATURE, RAWDOC.STATUS)
			.fetch()
			.stream()
			.forEach(rec -> {
				RawdocStatus status = RawdocStatus.safeValueOf( rec.getValue(RAWDOC.STATUS));
				if(rawdocUserData.getInvoiceNotice().containsKey(status)) {
					RawdocNotice notice = rawdocUserData.getInvoiceNotice().get(status);
					notice.setCount(notice.getCount() + rec.getValue(countField));
					notice.getDomains().add(rec.getValue(RAWDOC.DOMAIN));
					notice.getDomainCount().put(rec.getValue(DOMAIN.NAME), rec.getValue(countField));
					rawdocUserData.getInvoiceNotice().put(status, notice);
				} else {
					LinkedList<Integer> ds = new LinkedList<>();
					ds.add(rec.getValue(RAWDOC.DOMAIN));
					HashMap<String, Integer> domainCount = new HashMap<>();
					domainCount.put(rec.getValue(DOMAIN.NAME), rec.getValue(countField));
					rawdocUserData.getInvoiceNotice()
						.put(status, new RawdocNotice()
							.setCount(rec.getValue(countField))
							.setDomains(ds)
							.setDomainCount(domainCount));
				}
			});
		return rawdocUserData;
	}

	private static Rawdoc updateStatus( AONContext ctx, Rawdoc rawdoc, RawdocStatus status, String reason) {
		int count = ctx.getDslContext()
			.update(RAWDOC)
				.set(RAWDOC.STATUS,status.value())
				.set(RAWDOC.LOG, getLogArray(ctx, rawdoc, status, reason ) )
				.set(RAWDOC.MODIFICATION_USER, ctx.getUser())
				.set(RAWDOC.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
				.set(RAWDOC.JSON, rawdoc.getJson())
				.where(RAWDOC.ID.equal(rawdoc.getId()))
				.execute();
		ctx.log().info("UPDATE RAWDOC ({0}) id: {1} ({2} filas)",status.name(),rawdoc.getId(),count);
		return get(ctx, rawdoc.getId())
			.orElseThrow(() -> new AonCoreException( AonError.INVALID_UPDATE.getMessage()));
	}
	
	public static Rawdoc toDraft(AONContext ctx, Integer rawdocId) {
		ctx.checkWrite();
		return get(ctx, rawdocId)
			.map(r -> { 
				JSONObject json = new JSONObject(r.getJson());
				json.put(IJsonNames.LAST_STATUS, r.getStatus().name());
				r.setJson(json.toString());
				return r;
			})
			.map( r -> updateStatus(ctx,r,RawdocStatus.DRAFT, null))
			.orElseThrow(() -> new AonCoreException( AonError.INVALID_UPDATE.getMessage()));
	}

	public static Rawdoc toRejected(AONContext ctx, Integer rawdocId, String reason) {
		ctx.checkWrite();
		return get(ctx, rawdocId)
			.map(r -> { 
				JSONObject json = new JSONObject(r.getJson());
				json.put(IJsonNames.LAST_STATUS, r.getStatus().name());
				r.setJson(json.toString());
				return r;
			})
			.map( r -> updateStatus(ctx,r,RawdocStatus.REJECTED,reason))

			.orElseThrow(() -> new AonCoreException( AonError.INVALID_UPDATE.getMessage()));
	}

	public static Rawdoc toInbox(AONContext ctx, Integer rawdocId) {
		ctx.checkWrite();
		return get(ctx, rawdocId)
			.map( r -> updateStatus(ctx,r,RawdocStatus.INBOX, null))
			.orElseThrow(() -> new AonCoreException( AonError.INVALID_UPDATE.getMessage()));
	}
	
	private static String getLogArray(AONContext ctx, Rawdoc r, RawdocStatus status, String reason) {
		JSONArray jsonLog = new JSONArray( r.getLog()==null?"[]":r.getLog());
		HashMap<String,String> map = new HashMap<>();
		map.put(IJsonNames.DATE,new SimpleDateFormat(LOG_DATE_FORMAT_PATTERN).format(new Date()));
		map.put(IJsonNames.USER,ctx.getUser() );
		map.put(IJsonNames.STATUS,status.getDescription() );
		if (reason != null) {
			map.put(IJsonNames.REASON,reason );
		}
		JSONObject json = new JSONObject(map);
		jsonLog.put(json);
		return jsonLog.toString();
	}
	
	public static boolean hasData(AONContext ctx, Integer rawdocId ) {
		return ctx.getDslContext()
				.select( RAWDOC.ID )
				.from(RAWDOC)
				.where(RAWDOC.ID.eq(rawdocId))
				.and(RAWDOC.DATA.isNotNull())
				.fetch()
				.stream()
				.findFirst()
				.isPresent();
	}
	
	private static  class RawdocValidation {

		/**
		 * El dominio del apunte no puede estar vacio.
		 */
		private static final Consumer<RawdocContext> EMPTY_DOMAIN = rc -> {
			if (rc.rawdoc.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		/**
		 * La naturaleza del documento no puede estar vacio.
		 */
		private static final Consumer<RawdocContext> EMPTY_NATURE = rc -> {
			if (rc.rawdoc.getNature() == null) 
				throw new AonCoreException(AonError.EMPTY_RAWDOC_NATURE.getMessage());
		};
		
		/**
		 * El tipo del documento no puede estar vacio.
		 */
		private static final Consumer<RawdocContext> EMPTY_TYPE = rc -> {
			if (rc.rawdoc.getType() == null) 
				throw new AonCoreException(AonError.EMPTY_RAWDOC_TYPE.getMessage());
		};

		/**
		 * El status del documento no puede estar vacio.
		 */
		private static final Consumer<RawdocContext> EMPTY_STATUS = rc -> {
			if (rc.rawdoc.getStatus() == null) 
				throw new AonCoreException(AonError.EMPTY_RAWDOC_STATUS.getMessage());
		};
		
		private record RawdocContext(AONContext ctx, Rawdoc rawdoc) {}
		private static void validateRawdoc(AONContext ctx, Rawdoc rawdoc) throws AonCoreException {
			EMPTY_DOMAIN
				.andThen(EMPTY_NATURE)
				.andThen(EMPTY_TYPE)
				.andThen(EMPTY_STATUS)
				.accept(new RawdocContext(ctx, rawdoc));

		}
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Rawdoc getRandom(AONContext ctx, RawdocFilter filter) {
		return ctx.getDslContext()
			.select( RAWDOC.fields() )
			.from(RAWDOC)
			.where(RAWDOC_PROPERTIES.getConditions(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RawdocFiller())
			.findFirst()
			.orElse(null);
	}

}
