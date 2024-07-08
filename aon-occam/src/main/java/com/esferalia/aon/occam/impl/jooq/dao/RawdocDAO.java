package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rawdoc.RAWDOC;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Order.RawdocOrder;
import com.esferalia.aon.occam.api.model.Properties.RawdocProperties;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
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
import com.esferalia.aon.occam.impl.jooq.dao.PropertyOrdersDAO.RawdocPropertyOrdersDAO;
import com.esferalia.aon.occam.impl.jooq.validation.RawdocValidation;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.json.TediInvoiceJSON;

public class RawdocDAO {
	private static final String ACTION_DATE = "date";
	private static final String ACTION_USER = "user";
	private static final String ACTION_STATUS = "status";
	private static final String ACTION_REASON = "reason";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private static final RawdocPropertiesDAO RAWDOC_PROPERTIES = new RawdocPropertiesDAO();
	private static final RawdocPropertyOrdersDAO RAWDOC_PROPERTY_ORDERS = new RawdocPropertyOrdersDAO();	
	
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
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.TimestampPropertyDAO(RAWDOC.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.TimestampPropertyDAO(RAWDOC.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(RAWDOC.MODIFICATION_USER);}
		@Override public Property<String> getReferenceCodeProperty(){return new FilterDAO.PropertyDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.reference").cast(SQLDataType.VARCHAR));}
		@Override public Property<String> getJsonNameProperty(){return new FilterDAO.PropertyDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.name").cast(SQLDataType.VARCHAR));}
		@Override public Property<String> getJsonTotalProperty(){return new FilterDAO.PropertyDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.total").cast(SQLDataType.VARCHAR));}
		@Override public Property<String> getJsonDateProperty(){return new FilterDAO.PropertyDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.date").cast(SQLDataType.VARCHAR));}
		@Override public Property<java.util.Date> getDateProperty(){return new FilterDAO.DatePropertyDAO(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.date").cast(SQLDataType.DATE));}
	}
	
	private static class RawdocFiller  implements Function<Record,Rawdoc> {
		@Override
		public Rawdoc apply(Record r) {
			return build(r);
		}
		
		public static Rawdoc build(Record r) {
			return new Rawdoc()
					.setId(r.getValue(RAWDOC.ID))
					.setDomain(r.getValue(RAWDOC.DOMAIN))
					.setNature(RawdocNature.safeValueOf( r.getValue(RAWDOC.NATURE)))
					.setType(RawdocType.safeValueOf( r.getValue(RAWDOC.TYPE)))
					.setStatus(RawdocStatus.safeValueOf( r.getValue(RAWDOC.STATUS)))
					.setJson(r.getValue(RAWDOC.JSON))
					.setTediInvoice( AonStringUtils.isBlank(r.getValue(RAWDOC.JSON)) 
							? null 
							: TediInvoiceJSON.fromJSON( new JSONObject(r.getValue(RAWDOC.JSON) ) ) )
					.setInvoice(AonStringUtils.isBlank(r.getValue(RAWDOC.JSON))
							? new Invoice()
							: InvoiceJSON.fromJSON(r.getValue(RAWDOC.JSON)))
					.setLog(r.getValue(RAWDOC.LOG))
					.setMimeType(MimeType.safeValueOf( r.getValue(RAWDOC.MIME_TYPE)))
					.setCreationUser(r.getValue(RAWDOC.CREATION_USER))
					.setCreationDate(r.getValue(RAWDOC.CREATION_DATE))
					.setModificationUser(r.getValue(RAWDOC.MODIFICATION_USER))
					.setModificationDate(r.getValue(RAWDOC.MODIFICATION_DATE));
		}
	}
	
	private static class FullRawdocFiller  extends RawdocFiller {
		@Override
		public Rawdoc apply(Record r) {
			return super.apply(r)
				.setData(r.getValue(RAWDOC.DATA));
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
				.orderBy(RAWDOC.ID.desc())
				.limit(offset,limit)
				.fetch()
				.stream()
				.map(new RawdocFiller());
	}
	
	
	public static SelectConditionStep<Record> prepareQuery(AONContext ctx , RawdocFilter filter, boolean ticket) {
		SelectConditionStep<Record> query = ctx.getDslContext()
				.select().
				from(RAWDOC).
				where(RAWDOC_PROPERTIES.getConditions(filter));
		return query;
	}
	
	public static Stream<Rawdoc> getRawdocNewPortal(AONContext ctx , RawdocFilter filter , Integer page, Integer perPage, boolean ticket, RawdocOrder order){
		return prepareQuery(ctx, filter, ticket)
				.orderBy(RAWDOC_PROPERTY_ORDERS.getOrders(order))
				.limit(perPage)
				.offset(perPage * (page -1)).
				fetch().
				stream().
				map(new RawdocFiller());
	}
	
	public static Rawdoc getRawdocById(AONContext ctx, Integer id) {
		return ctx.getDslContext()
				.select()
				.from(RAWDOC)
				.where(RAWDOC.DOMAIN.eq(ctx.getDomainId()))
				.and(RAWDOC.ID.eq(id))
				.fetch()
				.stream()
				.map(new RawdocFiller())
				.findFirst()
				.orElse(null);
	}
	
	public static long getRawdocCount(AONContext ctx , RawdocFilter filter , boolean ticket) {
		return prepareQuery(ctx, filter, ticket).fetch().stream().count();
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
				.map(new FullRawdocFiller());
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
			.set(RAWDOC.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(RAWDOC.ID)
			.fetchOne()
			.getValue(RAWDOC.ID);
		ctx.log().info("INSERT RAWDOC id: " + id);
		return get(ctx, id);
	}
	
	private static Rawdoc update(AONContext ctx, Rawdoc rawdoc) {
		ctx.checkWrite();

		Rawdoc r = get(ctx, rawdoc.getId());

		RawdocValidation.validateRawdoc(ctx, rawdoc);
		ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.DOMAIN,rawdoc.getDomain())
			.set(RAWDOC.NATURE,rawdoc.getNature().value())
			.set(RAWDOC.TYPE  ,rawdoc.getType().value())
			.set(RAWDOC.STATUS,rawdoc.getStatus().value())
			.set(RAWDOC.JSON,rawdoc.getJson())
			.set(RAWDOC.LOG, rawdoc.getLog() != null ? rawdoc.getLog() : getLogArray(ctx, r, rawdoc.getStatus(), null))
			.set(RAWDOC.MODIFICATION_USER, ctx.getUser())
			.set(RAWDOC.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(RAWDOC.ID.eq(rawdoc.getId()))
			.execute();
		if(rawdoc != null && rawdoc.getData() != null && rawdoc.getMimeType() != null) {
			updateFile(ctx, rawdoc);
		}
		ctx.log().info("UPDATE RAWDOC id: " + rawdoc.getId());
		return get(ctx, rawdoc.getId());
	}
	
	private static void updateFile(AONContext ctx, Rawdoc rawdoc) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.MIME_TYPE,rawdoc.getMimeType() == null? null : rawdoc.getMimeType().value())
			.set(RAWDOC.DATA,rawdoc.getData())
			.where(RAWDOC.ID.eq(rawdoc.getId()))
			.execute();
	}
	
	public static Rawdoc save(AONContext ctx, Rawdoc rawdoc) {
		ctx.checkWrite();
		return rawdoc.getId() != null
			? update(ctx, rawdoc)
			: insert(ctx, rawdoc);
	}
	
	public static void delete(AONContext ctx, RawdocFilter filter) {
		ctx.checkWrite();
		
		int count = ctx.getDslContext()
			.delete(RAWDOC)
			.where(RAWDOC_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().info("DELETE RAWDOC " + filter.toString() + " ("+count+" filas)");
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
			.select( DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,RAWDOC.NATURE,RAWDOC.TYPE, RAWDOC.STATUS, COUNT)
			.from(RAWDOC)
			.innerJoin(DOMAIN).on(DOMAIN.ID.eq(RAWDOC.DOMAIN))
			.where(RAWDOC.DOMAIN.eq(domain))
			.groupBy(DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,RAWDOC.NATURE, RAWDOC.STATUS)
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
	
	public static RawdocUserData getUserData(AONContext ctx, int domain){
		AggregateFunction<Integer> COUNT = DSL.count(RAWDOC.ID);
		
		RawdocUserData rawdocUserData = new RawdocUserData();
		
		ctx.getDslContext()
			.select(RAWDOC.DOMAIN, RAWDOC.NATURE, RAWDOC.STATUS, COUNT)
			.from(RAWDOC)
			.join(DOMAIN).on(RAWDOC.DOMAIN.eq(DOMAIN.ID))
			.where(DOMAIN.ID.eq(domain))
			.groupBy(RAWDOC.DOMAIN,RAWDOC.NATURE, RAWDOC.STATUS)
			.fetch()
			.stream()
			.forEach(record -> {
				RawdocStatus status = RawdocStatus.safeValueOf( record.getValue(RAWDOC.STATUS));
				if(rawdocUserData.getInvoiceNotice().containsKey(status)) {
					RawdocNotice notice = rawdocUserData.getInvoiceNotice().get(status);
					notice.setCount(notice.getCount() + record.getValue(COUNT));
					notice.getDomains().add(record.getValue(RAWDOC.DOMAIN));
					rawdocUserData.getInvoiceNotice().put(status, notice);
				} else {
					LinkedList<Integer> ds = new LinkedList<Integer>();
					ds.add(record.getValue(RAWDOC.DOMAIN));
					rawdocUserData.getInvoiceNotice()
						.put(status, new RawdocNotice()
							.setCount(record.getValue(COUNT))
							.setDomains(ds));
				}
			});
		return rawdocUserData;
	}
	
	public static RawdocInvoiceCounter getInvoiceCounter(AONContext ctx) {
		RawdocInvoiceCounter counter = new RawdocInvoiceCounter();
		
		AggregateFunction<Integer> COUNT = DSL.count(RAWDOC.ID);
		Field<Boolean> ticket = DSL.decode()
			.when(DSL.position(RAWDOC.JSON,"\"TICKET\"").greaterThan(0), true)
			.otherwise( false);
				
		ctx.getDslContext().select(RAWDOC.STATUS, RAWDOC.TYPE, ticket, COUNT)
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
			Integer count = r.getValue(COUNT);
			counter.getMap().computeIfAbsent(status, k -> new RawdocInvoiceCounterDetail());
			counter.getMap().get(status).addCount(count);
			if(RawdocStatus.INBOX.equals(status)) {
				counter.getMap().get(status).getMap().put(tediType, count);
			}
		});
		
		return counter;
	}
	
	public static RawdocUserData getUserData(AONContext ctx, byte[] auth){
		Integer[] userScopes = SecurityDAO.getAuthScopes(ctx, auth);
		Integer[] domains = SecurityDAO.getAuthDomains(ctx, auth);
		
		Domain domain = DOMAIN.as("d");
		Domain parent = DOMAIN.as("p");
		AggregateFunction<Integer> COUNT = DSL.count(RAWDOC.ID);
		
		RawdocUserData rawdocUserData = new RawdocUserData();
		
		ctx.getDslContext()
			.select(RAWDOC.DOMAIN, RAWDOC.NATURE, RAWDOC.STATUS, COUNT)
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
			.forEach(record -> {
				RawdocStatus status = RawdocStatus.safeValueOf( record.getValue(RAWDOC.STATUS));
				if(rawdocUserData.getInvoiceNotice().containsKey(status)) {
					RawdocNotice notice = rawdocUserData.getInvoiceNotice().get(status);
					notice.setCount(notice.getCount() + record.getValue(COUNT));
					notice.getDomains().add(record.getValue(RAWDOC.DOMAIN));
					rawdocUserData.getInvoiceNotice().put(status, notice);
				} else {
					LinkedList<Integer> ds = new LinkedList<Integer>();
					ds.add(record.getValue(RAWDOC.DOMAIN));
					rawdocUserData.getInvoiceNotice()
						.put(status, new RawdocNotice()
							.setCount(record.getValue(COUNT))
							.setDomains(ds));
				}
			});
		return rawdocUserData;
	}

	public static void toDraft(AONContext ctx, Integer rawdocId) {
		ctx.checkWrite();
		Rawdoc r = get(ctx, rawdocId);
		int count = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.STATUS,RawdocStatus.DRAFT.value())
			.set(RAWDOC.LOG, getLogArray(ctx, r, RawdocStatus.DRAFT, null ) )
			.set(RAWDOC.MODIFICATION_USER, ctx.getUser())
			.set(RAWDOC.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(RAWDOC.ID.equal(rawdocId))
			.execute();
		ctx.log().info("UPDATE RAWDOC (DRAFT) id: " + rawdocId + " ("+count+" filas)");
	}

	public static void toRejected(AONContext ctx, Integer rawdocId, String reason) {
		ctx.checkWrite();
		Rawdoc r = get(ctx, rawdocId);
		int count = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.STATUS,RawdocStatus.REJECTED.value())
			.set(RAWDOC.LOG, getLogArray(ctx, r, RawdocStatus.REJECTED, reason ))
			.set(RAWDOC.MODIFICATION_USER, ctx.getUser())
			.set(RAWDOC.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(RAWDOC.ID.equal(rawdocId))
			.execute();
		ctx.log().info("UPDATE RAWDOC (REJECTED) id: " + rawdocId + " ("+count+" filas)");
	}

	public static void toInbox(AONContext ctx, Integer rawdocId) {
		ctx.checkWrite();
		Rawdoc r = get(ctx, rawdocId);
		int count = ctx.getDslContext()
			.update(RAWDOC)
			.set(RAWDOC.STATUS,RawdocStatus.INBOX.value())
			.set(RAWDOC.LOG, getLogArray(ctx, r, RawdocStatus.INBOX, null ) )
			.set(RAWDOC.MODIFICATION_USER, ctx.getUser())
			.set(RAWDOC.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(RAWDOC.ID.equal(rawdocId))
			.execute();
		ctx.log().info("UPDATE RAWDOC (INBOX) id: " + rawdocId + " ("+count+" filas)");
	}
	
	private static String getLogArray(AONContext ctx, Rawdoc r, RawdocStatus status, String reason) {
		JSONArray jsonLog = new JSONArray( r.getLog()==null?"[]":r.getLog());
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(ACTION_DATE  ,DATE_FORMAT.format(new Date()));
		map.put(ACTION_USER  ,ctx.getUser() );
		map.put(ACTION_STATUS,status.getDescription() );
		if (reason != null) {
			map.put(ACTION_REASON,reason );
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
	
}
