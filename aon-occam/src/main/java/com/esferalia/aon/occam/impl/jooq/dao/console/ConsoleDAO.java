package com.esferalia.aon.occam.impl.jooq.dao.console;

import static com.esferalia.aon.jooq.AonMaster.AON_MASTER;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.ConsoleDomainMessageJSON;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageFixType;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class ConsoleDAO {
	
	private static final Field<Integer> USER_COUNT = DSL.count().as("userCount");
	private static final Field<Integer> USER_COUNT_DOMAIN = USER.DOMAIN.as("userCountDomain");

	private static final String INFORMATION_SCHEMA = "information_schema";
	private static final String MYSQL = "mysql";
	private static final String PERFORMANCE_SCHEMA = "performance_schema";
	
	protected ConsoleDAO() {
	}
	
	public static Stream<ConsoleDomain> getDomains(CloseableAONContext ctx, DomainParams params) {
		com.esferalia.aon.jooq.tables.Domain domainChild = DOMAIN.as("domainChild");
		Field<Integer> childCountParent = domainChild.PARENT.as("childCountParent");
		Field<Integer> childCountField = DSL.count().as("childCount");
		BigDecimal one = new BigDecimal(1);
		BigDecimal zero = new BigDecimal(0);
		Field<BigDecimal> childActiveCountIf  = DSL.if_(domainChild.ACTIVE.eq((byte)1), one , zero);
		Field<BigDecimal> childActiveCount = DSL.sum(childActiveCountIf).as("childActiveCount");
		
		Table<Record3<Integer,Integer,BigDecimal>> childCount = 
			ctx.getDslContext().select(childCountParent,childCountField,childActiveCount)
				.from(domainChild)
				.where( domainChild.PARENT.isNotNull() )
				.groupBy(domainChild.PARENT)
				.asTable()
				.as("childCount");
		
		Table<Record2<Integer,Integer>> userCount = ctx.getDslContext().select(USER_COUNT_DOMAIN,USER_COUNT)
				.from(USER)
				.where( USER.ACTIVE.eq((byte) 1) )
				.groupBy(USER_COUNT_DOMAIN)
				.asTable()
				.as("userCount");
		
		return ctx.getDslContext().select()
			.from(DOMAIN)
			.leftOuterJoin(userCount).on(DOMAIN.ID.eq(USER_COUNT_DOMAIN))
			.leftOuterJoin(childCount).on(DOMAIN.ID.eq(childCountParent))
			.leftOuterJoin(APP_PARAM).on(DOMAIN.ID.eq(APP_PARAM.DOMAIN).and(APP_PARAM.NAME.eq(AppParam.AON_SUPPORT_ENABLED.toString())))
			.where( getFilter(params) )
			.offset(params.getOffset())
			.limit(params.getLimit())
			.fetch()
			.stream()
			.map(rec -> new Pair<>(rec, new ConsoleDomainFiller().apply(rec) ) )
			.map( pair -> {pair.getRight().setDefinedUsers( AonNumberUtils.zeroIfNull(pair.getLeft().getValue(USER_COUNT)) );
				return pair;
			})
			.map( pair -> {
				pair.getRight().setChildCount( AonNumberUtils.zeroIfNull(pair.getLeft().getValue(childCountField)) );
				BigDecimal activeCount = pair.getLeft().getValue(childActiveCount);
				pair.getRight().setActiveChildCount( activeCount==null?0:activeCount.intValue()  );
				return pair.getRight();
			})
			;
			
			
		
	}
	
	public static Stream<Schema> getSchemas(AONContext ctx) {
		return  ctx.getDslContext()
			.meta()
			.getSchemas()
			.stream();
	}
	
	public static Stream<Schema> getAONSchemas(AONContext ctx) {
		return getSchemas(ctx)
			.filter(schema -> !INFORMATION_SCHEMA.equals(schema.getName()))
			.filter(schema -> !MYSQL.equals(schema.getName()))
			.filter(schema -> !PERFORMANCE_SCHEMA.equals(schema.getName()))
			;			
	}

	public static Domain changeActive(CloseableAONContext ctx, Integer domainId, boolean active) {
		int count = ctx.getDslContext()
			.update(DOMAIN)
			.set(DOMAIN.ACTIVE, AonEnumUtils.getByte( active ))
			.where(DOMAIN.ID.eq(domainId))
			.execute();
		Domain dom = DomainDAO.getDomain(ctx, domainId);
		if (dom != null) {
			ctx.log().info("Domain {0} - {1} - Active changed --> {2} ({3} rows)"
					,dom.getId()
					,dom.getName()
					,dom.isActive()
					,count
					);
		} else {
			ctx.log().info("Domain active not changed");
		}
		return dom;
	}

	public static Domain changeExpirationDate(CloseableAONContext ctx, Integer domainId, Date expireDate) {
		int count = ctx.getDslContext()
			.update(DOMAIN)
			.set(DOMAIN.EXPIRATIONDATE, AonDateUtils.toSql( expireDate ))
			.where(DOMAIN.ID.eq(domainId))
			.execute();
		Domain dom = DomainDAO.getDomain(ctx, domainId);
		if (dom != null) {
			ctx.log().info("Domain {0} - {1} - Expiration Date changed --> {2} ({3} rows)"
					,dom.getId()
					,dom.getName()
					,dom.getExpirationDate()
					,count
					);
		} else {
			ctx.log().info("Domain active not changed");
		}
		return dom;
	}
	private static Condition and(Condition a, Condition b) {
		return (a==null)?b:a.and(b);
	}
	private static Condition getFilter(DomainParams params) {
		Condition c = null;
		if (params.getId() != null ) {
			c = and(c, DOMAIN.ID.eq(params.getId()));
		}
		if (AonStringUtils.isNotBlank(params.getQuery())) {
			String q = AonStringUtils.PERCENT + params.getQuery() + AonStringUtils.PERCENT;
			c = and(c, DOMAIN.NAME.like(q).or(DOMAIN.DESCRIPTION.like(q))); 
		}
		if (params.getParent() != null ) {
			c = and(c, DOMAIN.PARENT.eq(params.getParent())); 
		}
		if (params.getOrphan() != null ) {
			if (params.getOrphan().booleanValue()) {
				c = and(c, DOMAIN.PARENT.isNull());
			} else {
				c = and(c, DOMAIN.PARENT.isNotNull());
			}
		}
		if (params.getType() != null ) {
			c = and(c, DOMAIN.TYPE.eq(params.getType().byteValue())); 
		}
		if (params.getActive() != null ) {
			c = and(c, DOMAIN.ACTIVE.eq(AonEnumUtils.getByte(params.getActive()))); 
		}
		if (params.getEnableHeredity() != null ) {
			c = and(c, DOMAIN.ENABLEHEREDITY.eq(AonEnumUtils.getByte(params.getEnableHeredity()))); 
		}
		if (params.getDomainManagement() != null ) {
			c = and(c, DOMAIN.DOMAINMANAGEMENT.eq(AonEnumUtils.getByte(params.getDomainManagement()))); 
		}
		if (params.getFromLastAccess() != null ) {
			c = and(c, DOMAIN.LASTACCESS_DATE.ge(new Timestamp( params.getFromLastAccess().getTime()))); 
		}
		if (params.getToLastAccess() != null ) {
			c = and(c, DOMAIN.LASTACCESS_DATE.le(new Timestamp( params.getToLastAccess().getTime()))); 
		}
		if (params.getFromExpirationDate() != null ) {
			c = and(c, DOMAIN.EXPIRATIONDATE.ge(AonDateUtils.toSql(params.getFromExpirationDate()))); 
		}
		if (params.getToExpirationDate() != null ) {
			c = and(c, DOMAIN.EXPIRATIONDATE.le(AonDateUtils.toSql(params.getToExpirationDate())));
		}
		c = c==null?DSL.trueCondition():c;
		return c;
	}
	
	public static class ConsoleDomainFiller extends Filler implements Function<Record, ConsoleDomain> {
		@Override
		public ConsoleDomain apply(Record r) {
			return build(r);
		}
		
		public static ConsoleDomain build(Record r) {
			return buildConsoleDomain(r, DOMAIN);
		}
		
		public static ConsoleDomain buildConsoleDomain(Record r, com.esferalia.aon.jooq.tables.Domain domain) {
			ConsoleDomain consoleDomain = new ConsoleDomain();
			DomainFiller.fillDomain(r, consoleDomain, domain);
			return consoleDomain
				.setChildCount(0)
				.setActiveChildCount(0)
				.setRemoteAccessEnabled( getValue(r, APP_PARAM.ID) != null)
			;	
		}
	}

	public static boolean isRemoteAccessEnabled(AONContext ctx, Integer domainId) {
		return AppParamDAO.getApplicationParameterStream(ctx
				, p -> p.getDomainProperty().eq(domainId)
				.and(p.getNameProperty().eq(AppParam.AON_SUPPORT_ENABLED.toString())))
			.findAny()
			.isPresent();
	}

	public static String remoteAccess(CloseableAONContext ctx, Integer domainId) {
		if (isRemoteAccessEnabled(ctx, domainId)) {
			int count = ctx.getDslContext()
				.delete(APP_PARAM)
				.where(APP_PARAM.DOMAIN.eq(domainId))
				.and(APP_PARAM.NAME .eq(AppParam.AON_SUPPORT_ENABLED.toString()))
				.execute();
			ctx.log().info("Remote Access Change: OFF " + domainId + "(" + count + " rows)");
			return null;
		} else {
			int count = ctx.getDslContext()
				.insertInto(APP_PARAM)
				.set(APP_PARAM.DOMAIN, domainId)
				.set(APP_PARAM.NAME, AppParam.AON_SUPPORT_ENABLED.toString())
				.set(APP_PARAM.VALUE, String.valueOf(new Date().getTime()))
				.execute();
			ctx.log().info("Remote Access Change: ON " + domainId + "(" + count + " rows)");
			String users = ctx.getDslContext()
				.select(USER.LOGIN)
				.from(USER)
				.where(USER.DOMAIN.eq(domainId))
				.and(USER.ACTIVE.eq((byte) 1))
				.limit(10)
				.fetch()
				.stream()
				.map(rec -> "(" + rec.getValue(USER.LOGIN)+ ")")
				.collect(Collectors.joining(", "));
			;
			return AonStringUtils.defaultIfBlank(users, "No hay usuarios activos en el dominio");
		}
		
	}

	public static Boolean fix(CloseableAONContext ctx, ConsoleDomainMessage cm) {
		try {
			System.out.println( ConsoleDomainMessageJSON.toJSON(cm) );
			for ( ConsoleDomainMessageVisitor cmt : ConsoleDomainMessageVisitor.values()) {
				if (cmt.fix( ctx, cm)) {
					return true;		
				}; 			
			}
			return false;
		} catch (Exception e) {
			throw new AonCoreException( e );	
		}
	}
	
	private enum ConsoleDomainMessageVisitor {
		INTEGRITY {
			@Override
			boolean fix(CloseableAONContext ctx, ConsoleDomainMessage cm) {
				if (cm.getType() == ConsoleDomainMessageType.INTEGRITY) {
					if (cm.getFixType() == ConsoleDomainMessageFixType.DELETE) {
						return  deleteRow( ctx, cm);
					} else if (cm.getFixType() == ConsoleDomainMessageFixType.SET_NULL) {
						return  setNull( ctx, cm);
					}
					return true;
				}
				return false;
			}
		},
		PRODUCT {
			boolean fix(CloseableAONContext ctx, ConsoleDomainMessage cm) {
				if (cm.getType() == ConsoleDomainMessageType.PRODUCT) {
					return false;
				}
				return false;
			}
		},
		AGREEMENT {
			@Override
			boolean fix(CloseableAONContext ctx, ConsoleDomainMessage cm) {
				if (cm.getType() == ConsoleDomainMessageType.AGREEMENT) {
					return false;
				}
				return false;
			}
		};

		private ConsoleDomainMessageVisitor() {
			
		}
		
		abstract boolean fix(CloseableAONContext ctx, ConsoleDomainMessage cm);
		
		boolean deleteRow(CloseableAONContext ctx, ConsoleDomainMessage cm) {
			Table<?> table = AON_MASTER.getTable(cm.getTable());
			@SuppressWarnings("unchecked")
			TableField<?, Integer> pkField = (TableField<?, Integer>) table.getPrimaryKey().getFields().get(0);
			int count = ctx.getDslContext().delete(table)
				.where(pkField.eq(cm.getPkId()))
				.execute();
			return (count>0);
		}
		
		boolean setNull(CloseableAONContext ctx, ConsoleDomainMessage cm) {
			Table<?> table = AON_MASTER.getTable(cm.getTable());
			@SuppressWarnings("unchecked")
			TableField<?, Integer> pkField = (TableField<?, Integer>) table.getPrimaryKey().getFields().get(0);
			Field<?> fkField = table.field( cm.getFkColumn() );
			int count = ctx.getDslContext()
				.update(table)
				.setNull( fkField )
				.where(pkField.eq(cm.getPkId()))
				.execute();
			return (count>0);
		}
		
	}
	
}
