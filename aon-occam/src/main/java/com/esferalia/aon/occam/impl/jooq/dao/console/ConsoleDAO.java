package com.esferalia.aon.occam.impl.jooq.dao.console;

import static com.esferalia.aon.jooq.AonMaster.AON_MASTER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.watson.j2html.TagCreator.div;
import static com.esferalia.aon.watson.j2html.TagCreator.li;
import static com.esferalia.aon.watson.j2html.TagCreator.ul;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Named;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Schema;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdateConditionStep;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.ParserException;
import org.jooq.impl.SQLDataType;

import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.jooq.AonMaster;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleSchema;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableFieldType;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.ql.JOOQRenderer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.j2html.tags.specialized.UlTag;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConsoleDAO {

	protected static final com.esferalia.aon.jooq.tables.Domain PARENT 
		= com.esferalia.aon.jooq.tables.Domain.DOMAIN.as("parent");
	protected static final com.esferalia.aon.jooq.tables.Domain PAYER 
		= com.esferalia.aon.jooq.tables.Domain.DOMAIN.as("payer");
	protected static final com.esferalia.aon.jooq.tables.AppParam SUPPORT_APP_PARAM  
		= com.esferalia.aon.jooq.tables.AppParam.APP_PARAM.as("supportAppParam");
	protected static final com.esferalia.aon.jooq.tables.AppParam PAYER_APP_PARAM  
		= com.esferalia.aon.jooq.tables.AppParam.APP_PARAM.as("payerAppParam");
	

	private static final String QUOTES_REGEX = "\"(.*?)\"";

	private static final Logger LOGGER = Logger.getLogger( ConsoleDAO.class.getName());
	
	private static final Field<Integer> USER_COUNT = DSL.count().as("userCount");
	private static final Field<Integer> USER_COUNT_DOMAIN = USER.DOMAIN.as("userCountDomain");

	private static final String INFORMATION_SCHEMA = "information_schema";
	private static final String MYSQL = "mysql";
	private static final String SYS = "sys";
	private static final String PERFORMANCE_SCHEMA = "performance_schema";
	private static final String DOMAINEXTRACT_SCHEMA = "domainextract-aonsolutions-net";
	
	private static final String DOMAIN_LABEL = "domain";
	private static final String SCOPE_LABEL = "scope";
	
	private ConsoleDAO() {
	}
	
	public static Stream<ConsoleDomain> getDomains(DomainParams params) {
		return ((AonStringUtils.isEmpty( params.getDbSchema() ))
				?AonCollectionUtils.stream( ConsoleSchema.values() ).flatMap( cs -> getDomainStream(cs, params) )
				:getDomains(params.getDbSchema(), params))
			.limit(params.getLimit())
			;	
	}
	
	private static Stream<ConsoleDomain> getDomains(String schema, DomainParams params) {
		return ConsoleSchema.safeValueOf( schema )
			.map( sc -> getDomainStream(sc, params) )	
			.orElse(Stream.empty());
	}
	
	private static Stream<ConsoleDomain> getDomainStream(ConsoleSchema cs, DomainParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(cs.getSchema())) {
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
			
			Table<Record2<Integer,Integer>> userCount = 
				ctx.getDslContext().select(USER_COUNT_DOMAIN,USER_COUNT)
					.from(USER)
					.where( USER.ACTIVE.eq((byte) 1) )
					.groupBy(USER_COUNT_DOMAIN)
					.asTable()
					.as("userCount");
			
			SelectOnConditionStep<Record> sentence = ctx.getDslContext().select()
				.from(DOMAIN)
				.leftOuterJoin(PARENT).on(DOMAIN.PARENT.eq(PARENT.ID))
				.leftOuterJoin(userCount).on(DOMAIN.ID.eq(USER_COUNT_DOMAIN))
				.leftOuterJoin(childCount).on(DOMAIN.ID.eq(childCountParent))
				.leftOuterJoin(SUPPORT_APP_PARAM).on(DOMAIN.ID.eq(SUPPORT_APP_PARAM.DOMAIN).and(SUPPORT_APP_PARAM.NAME.eq(AppParam.AON_SUPPORT_ENABLED.toString())))
				.leftOuterJoin(PAYER_APP_PARAM).on(DOMAIN.ID.eq(PAYER_APP_PARAM.DOMAIN).and(PAYER_APP_PARAM.NAME.eq(AppParam.AON_DOMAIN_PAYER.toString())))
				.leftOuterJoin(PAYER).on(PAYER_APP_PARAM.VALUE.cast(Integer.class).eq(PAYER.ID))
			;	
			
			
			if ( AonStringUtils.isNotEmpty( params.getSelect() )  ) {
				String subTableName = "ConsoleSubTable";
				Field<Integer> subTableDomain = DSL.field( DSL.name( subTableName, DOMAIN_LABEL ), Integer.class );
				Object[] bindings = getBindings( params.getSelect() );
				Table<?> subTable = (bindings == null)
					? ctx.getDslContext()
						.parser()
						.parseSelect( params.getSelect() )
						.asTable(subTableName)
					: ctx.getDslContext()
						.parser()
						.parseSelect( parseSelect(params.getSelect()), bindings )
						.asTable(subTableName)
				;
				sentence = sentence
					.innerJoin(subTable).on(DOMAIN.ID.eq(subTableDomain));
			}
			return sentence
				.where( getFilter( params) )
				.offset(params.getOffset( cs ))
				.limit(params.getLimit())
				.fetch()
				.stream()
				.map( rec -> {
					ConsoleDomain consoleDomain = new ConsoleDomain();
					DomainFiller.fillDomain(rec, consoleDomain,  DOMAIN);
					Domain consoleDomainParent = new Domain(); 
					DomainFiller.fillDomain(rec, consoleDomainParent,  PARENT);
					if (consoleDomainParent != null && consoleDomainParent.getId() != null) {
						consoleDomain.setParent(consoleDomainParent);
					} 
					BigDecimal activeCount = rec.getValue(childActiveCount);
					Number userCo = rec.getValue(USER_COUNT);
					Integer definedUsers = userCo==null?null: userCo.intValue();
					Integer domainPayerId = rec.getValue(PAYER_APP_PARAM.ID);
					Domain payerDomain = new Domain(); 
					DomainFiller.fillDomain(rec, payerDomain,  PAYER);
					if (payerDomain != null && payerDomain.getId() != null) {
						consoleDomain.setPayerDomain(payerDomain);
					} 
					consoleDomain
						.setSchema( cs.getSchema() )
						.setRemoteAccessEnabled( rec.getValue(SUPPORT_APP_PARAM.ID) != null)
						.setChildCount( AonNumberUtils.zeroIfNull(rec.getValue(childCountField)) )
						.setActiveChildCount( activeCount==null?0:activeCount.intValue()  )
						.setDefinedUsers( AonNumberUtils.zeroIfNull(definedUsers) )
					;	
					return consoleDomain; 
				});
		} catch (ParserException e) {
			e.printStackTrace();
			String msg = "PARSE Schema " + cs.getSchema() + " [Exception: "+ e.getMessage() +"]";
			LOGGER.severe( msg );
			return Stream.empty();
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Schema " + cs.getSchema() + " [Exception: "+ e.getMessage() +"]";
			LOGGER.severe( msg );
			return Stream.empty();
		}
	}
	
	private static String parseSelect(String select) {
		return select.replaceAll(QUOTES_REGEX, "?");
	}

	private static Object[] getBindings( String sentence) {
        Pattern pattern = Pattern.compile(QUOTES_REGEX);
        Matcher matcher = pattern.matcher(sentence);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group(1));
        }         
        return AonCollectionUtils.isEmpty( result ) 
        	?null
			:result.toArray(new String[0])
		;
	}

	private static Stream<Schema> getSchemas(AONContext ctx) {
		return  ctx.getDslContext()
			.meta()
			.getSchemas()
			.stream();
	}
	
	public static Stream<Schema> getAONSchemas(AONContext ctx) {
		return getSchemas(ctx)
			.filter(schema -> !INFORMATION_SCHEMA.equals(schema.getName()))
			.filter(schema -> !MYSQL.equals(schema.getName()))
			.filter(schema -> !SYS.equals(schema.getName()))
			.filter(schema -> !PERFORMANCE_SCHEMA.equals(schema.getName()))
			.filter(schema -> !DOMAINEXTRACT_SCHEMA.equals(schema.getName()))
		;			
	}

	public static Domain changeActive(AONContext ctx, Integer domainId, boolean active) {
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

	public static Domain changeExpirationDate(AONContext ctx, Integer domainId, Date expireDate) {
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
	public static Condition getFilter(DomainParams params) {
		Condition c = DSL.noCondition();
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
		return c;
	}
	
	public static boolean isRemoteAccessEnabled(AONContext ctx, Integer domainId) {
		return AppParamDAO.getApplicationParameterStream(ctx
				, p -> p.getDomainProperty().eq(domainId)
				.and(p.getNameProperty().eq(AppParam.AON_SUPPORT_ENABLED.toString())))
			.findAny()
			.isPresent();
	}
	
	public static Stream<User> availableUsers(AONContext ctx, Integer domainId) {
		DomainRecord domainRecord = DomainDAO.getParentDomain(ctx, domainId);
		Integer[] domains = (domainRecord == null || domainRecord.getParent() == null) 
			? new Integer[]{domainId}
			: new Integer[]{domainId,domainRecord.getParent()};
		return SecurityDAO.getDomainUserStream( ctx, f -> f.getDomainProperty().in( domains)
				.and( f.getActiveProperty().eq( (byte) 1)));
	}

	public static boolean switchRemoteAccess(AONContext ctx, Integer domainId) {
		if (isRemoteAccessEnabled(ctx, domainId)) {
			int count = ctx.getDslContext()
				.delete(SUPPORT_APP_PARAM)
				.where(SUPPORT_APP_PARAM.DOMAIN.eq(domainId))
				.and(SUPPORT_APP_PARAM.NAME .eq(AppParam.AON_SUPPORT_ENABLED.toString()))
				.execute();
			ctx.log().info("Remote Access Change: OFF " + domainId + "(" + count + " rows)");
			return false;
		} else {
			int count = ctx.getDslContext()
				.insertInto(SUPPORT_APP_PARAM)
				.set(SUPPORT_APP_PARAM.DOMAIN, domainId)
				.set(SUPPORT_APP_PARAM.NAME, AppParam.AON_SUPPORT_ENABLED.toString())
				.set(SUPPORT_APP_PARAM.VALUE, String.valueOf(new Date().getTime()))
				.execute();
			ctx.log().info("Remote Access Change: ON " + domainId + "(" + count + " rows)");
			return true;
		}
		
	}

	public static boolean enableRemoteAccess(AONContext ctx, Integer domainId) {
		boolean wasEnabled = isRemoteAccessEnabled(ctx, domainId);
		if (!wasEnabled) {
			int count = ctx.getDslContext()
				.insertInto(SUPPORT_APP_PARAM)
				.set(SUPPORT_APP_PARAM.DOMAIN, domainId)
				.set(SUPPORT_APP_PARAM.NAME, AppParam.AON_SUPPORT_ENABLED.toString())
				.set(SUPPORT_APP_PARAM.VALUE, String.valueOf(new Date().getTime()))
				.execute();
			ctx.log().info("Remote Access Change: ON " + domainId + "(" + count + " rows)");
		}
		return wasEnabled;
	}

	@SuppressWarnings("unchecked")
	private static TableField<?, Integer> getPkField(String tableName) {
		return (TableField<?, Integer>) AON_MASTER.getTable(tableName).getPrimaryKey().getFields().get(0);
	}
	@SuppressWarnings("unchecked")
	private static TableField<?, Integer> getDomainField(String tableName) {
		return (TableField<?, Integer>) AON_MASTER.getTable(tableName).field("domain");
	}
	
	public static Stream<ConsoleTableRow> getTableRows(AONContext ctx, ConsoleTableRow params) {
		return getTableRowsStream(ctx, params, 50);
	}

	public static ConsoleTableRow getTableRow(AONContext ctx, ConsoleTableRow params) {
		return getTableRowsStream(ctx, params, 1)
			.findFirst()
			.orElse(null);
	}
	
	private static Stream<ConsoleTableRow> getTableRowsStream(AONContext ctx, ConsoleTableRow params, int limit) {
		try {
			ConsoleTableRow tableRow = getTableRowMetadata( params );
			Table<?> table = AON_MASTER.getTable(params.getTable());
			List<Field<?>> selectedFields = AonCollectionUtils.stream( tableRow.getFields().values())
				.filter( f -> f.getType() != ConsoleTableFieldType.BINARY)
				.map( f -> table.field( f.getColumn() ))
				.collect(Collectors.toCollection(LinkedList::new));
			return ctx.getDslContext().select( selectedFields )
				.from(table)
				.where( getConditions(params))
				.limit(limit)
				.stream()
				.map( r -> {
					ConsoleTableRow tr = getTableRowMetadata( params );
					Field<?> pkField = getPkField(params.getTable());
					tr.setId( (Integer) r.getValue(pkField) );
					selectedFields	
						.stream()
						.forEach( field -> tr.getField( field.getName() ).setValue( toString(
								tr.getField( field.getName() ).getType(),
								r.getValue(field)) ));
					return tr; 
				});
		} catch (Exception e) {
			throw new AonCoreException( "Error:" + e.getMessage() );
		}
	}

	private static ConsoleTableFieldType getConsoleTableFieldType(Field<?> field) {
		if (field.getDataType() == null ) return null;
		if (field.getDataType().getSQLDataType().isString()) return ConsoleTableFieldType.STRING;
		else if (field.getDataType().getSQLDataType() == SQLDataType.DOUBLE) return ConsoleTableFieldType.DOUBLE;
		else if (field.getDataType().getSQLDataType() == SQLDataType.DECIMAL) return ConsoleTableFieldType.DECIMAL;
		else if (field.getDataType().getSQLDataType() == SQLDataType.TINYINT) return ConsoleTableFieldType.BYTE;
		else if (field.getDataType().getSQLDataType() == SQLDataType.SMALLINT) return ConsoleTableFieldType.SHORT;
		else if (field.getDataType().getSQLDataType().isInteger()) return ConsoleTableFieldType.INTEGER;
		else if (field.getDataType().getSQLDataType().isDate()) return ConsoleTableFieldType.DATE;
		else if (field.getDataType().getSQLDataType().isTimestamp()) return ConsoleTableFieldType.TIMESTAMP;
		else if (field.getDataType().getSQLDataType().isBinary()) return ConsoleTableFieldType.BINARY;
		
		throw new AonCoreException("El tipo SQL [" + field.getDataType().getSQLDataType().getName() + "] no está soportado.");
	}
	private static Object fromString(ConsoleTableFieldType type, String value) {
		if (type== null) return null;
		if (value == null) return null;
		return type.visit(new ConsoleTableFieldType.Visitor<Object>() {
			@Override public Object visitString() { return value; }
			@Override public Object visitByte() { return  AonNumberUtils.toByte( value); }
			@Override public Object visitShort() { return  AonNumberUtils.toShort( value); }
			@Override public Object visitInteger() { return  AonNumberUtils.toInteger( value); }
			@Override public Object visitDouble(){ return  AonNumberUtils.toDouble( value); }
			@Override public Object visitDecimal(){ return  BigDecimal.valueOf( AonNumberUtils.toDouble( value) ); }
			@Override public Object visitDate() {return AonDateUtils.toSql( AonDateUtils.simpleParse( value ) ); }
			@Override public Object visitTimestamp() {return AonDateUtils.dateTimeParse( value ); }
			@Override public Object visitBinary() { return null; }
		});
	}
	
	private static String toString(ConsoleTableFieldType type, Object value) {
		if (type== null) return null;
		if (value == null) return null;
		return type.visit(new ConsoleTableFieldType.Visitor<String>() {

			@Override public String visitString() { return Objects.toString(value, null ); }
			@Override public String visitByte() { return visitNumber(); }
			@Override public String visitShort() { return visitNumber(); }
			@Override public String visitInteger() { return visitNumber(); }
			@Override public String visitDouble() { return visitNumber(); }
			@Override public String visitDecimal() { return visitNumber(); }
			@Override public String visitDate() {return AonDateUtils.simpleFormat( (Date) value );}
			@Override public String visitTimestamp() {return AonDateUtils.dateTimeFormat( (Date) value );}
			@Override public String visitBinary() {return "<BLOB>";}
			
			private String visitNumber() { return AonNumberUtils.toString( (Number) value); }

		});
	}

	public static String[] getAonTables() {
		return AonMaster.AON_MASTER.getTables()
			.stream()
			.map( Named::getName ) 
			.toArray(tableName -> new String[tableName]);
	}

	public static ConsoleTableRow getTableRowMetadata(ConsoleTableRow params) {
		ConsoleTableRow tableRow = new ConsoleTableRow()
				.setSchema(params.getSchema())
				.setTable( params.getTable() )
				.setId( params.getId() )
				.setDomain( params.getDomain() );
			
		Table<?> table = AON_MASTER.getTable(params.getTable());
		Arrays.stream( table.fields() )
			.map( field -> new ConsoleTableField()
					.setColumn( field.getName() )
					.setType( getConsoleTableFieldType(field))
					.setLength( field.getDataType().length() )
					.setComment( field.getComment() )
				)
			.forEach( tableRow::add );
		table
			.getPrimaryKey()
			.getFields()
			.stream()
			.map( f -> tableRow.getField( f.getName() ) )
			.forEach( tr -> tr.setPrimaryKey(true) );
		table
			.getReferences()
			.stream()
			.forEach(fk -> {
				for (int i = 0; i < fk.getFields().size(); i++ ) {
					TableField<?, ?> f = fk.getFields().get(i);
					tableRow.getField( f.getName() )
						.setForeignKey(true)
						.setForeignTable( fk.getKey().getTable().getName() )
						.setForeignColumn( fk.getKeyFields().get(i).getName() )
						;		
				}
			});  
		return tableRow;
	}

	private static Condition add(Condition left, Condition right) {
		return left == null ? right : left.and(right);
	}
	
	private static Condition getConditions(ConsoleTableRow params) {
		
		Condition c = null;
		
		if (params.getFields() != null && !params.getFields().isEmpty() ) {
			JOOQRenderer renderer = new JOOQRenderer();
			params.getFields().values()
				.stream()
				.filter( f -> AonStringUtils.isNotEmpty( f.getQueryValue() ))
				.forEach( f -> renderer.put(AON_MASTER.getTable(params.getTable()).field( f.getColumn() ), f.getQueryValue() ) );
			try {
				c = renderer.getCondition();
			} catch (ExpressionException e) {
				throw new AonCoreException("Error en la evaluaación de los parámetros. [" + e.getMessage() + "]");	
			}
		}
		
		if ( params.getId() != null) {
			TableField<?, Integer> pkField = getPkField(params.getTable());
			c = add( c, pkField.eq(params.getId()));
		}
		if ( params.getDomain() != null) {
			TableField<?, Integer> pkField = getDomainField(params.getTable());
			c = add( c, pkField.eq(params.getDomain()));
		}
		if (c == null) {
			throw new AonCoreException("No se ha indicado ninguna condición");
		}
		return c;
	}

	private static void  validate(ConsoleTableRow row) {
		if (row == null) throw new AonCoreException("No se ha indicado fila para la operación.");
		if (AonStringUtils.isBlank( row.getTable() )) throw new AonCoreException("No se ha indicado tabla para la operación.");
		if (row.getId() == null) throw new AonCoreException("No se ha indicado ID para la operación.");
	}
	
	private static void  validate(ConsoleTableField field) {
		if (field == null) throw new AonCoreException("No se ha indicado columna para la operación.");
		if (AonStringUtils.isBlank( field.getColumn() )) throw new AonCoreException("No se ha indicado nombre de columna para la operación.");
		if (field.getType() == null) throw new AonCoreException("No se ha indicado el tipo de la columna para la operación.");
	}
	
	public static Boolean delete(AONContext ctx, ConsoleTableRow row) {
		try {
			validate(row);
			Table<?> table = AON_MASTER.getTable(row.getTable());
			TableField<?, Integer> pkField = getPkField(row.getTable());
			int count = ctx.getDslContext().delete(table)
				.where(pkField.eq(row.getId()))
				.execute();
			return (count>0);
		} catch (Exception e) {
			throw new AonCoreException("No se pudo borrar la fila. Causa: " + e.getMessage(), e);
		}
	}
	
	public static ConsoleTableRow update(AONContext ctx, ConsoleTableRow row, ConsoleTableField field) {
		try {
			validate(row);
			validate(field);
			Table<?> table = AON_MASTER.getTable(row.getTable());
			TableField<?, Integer> pkField = getPkField(row.getTable());
			Field<?> updatableField = table.field( field.getColumn() );
			UpdateConditionStep<?> sentence = updateField( ctx.getDslContext().update(table) 
				, updatableField
				, fromString( field.getType(), field.getNewValue()))
				.where(pkField.eq(row.getId()));
			sentence.execute();
			if ( AonStringUtils.equals(pkField.getName(),updatableField.getName())) {
				row.setId( AonNumberUtils.toInteger( field.getNewValue() ));
			}
			return getTableRow(ctx,row);
		} catch (Exception e) {
			throw new AonCoreException("No se pudo modificar la fila. Causa: " + e.getMessage(), e);
		}
	}
	
	private static <T> UpdateSetMoreStep<?> updateField(UpdateSetFirstStep<?> update, Field<T> field, Object value) {
		if ( value == null) {
			return update.setNull(field);	
		}
	    return update.set(field, field.getType().cast(value));
	}

	public static String updateScopes(AONContext ctx, Integer domainId, Integer wrongScopeId, Integer newScopeId) {
		List<String> messages = new LinkedList<>();  
		ctx.getDslContext()
			.meta()
			.getTables()
			.stream()
			.filter(Objects::nonNull )
			.filter(t -> !DOMAIN.getName().equals(t.getName()))
			.filter(t ->  AonCollectionUtils.stream(t.fields()).anyMatch( f -> SCOPE_LABEL.equals(f.getName()) ) )
			.filter(t -> ctx.getDslContext().selectOne()
							.from( t )
							.where( getDomainField(t).eq(domainId) )
							.and(getScopeField(t).eq(wrongScopeId))
							.limit(1)
							.fetch()
							.stream()
							.findFirst()
							.isPresent()
			)
			.forEach( t -> {
				
				System.out.println( 
						
						ctx.getDslContext()
						.update( t )
						.set( getScopeField(t), newScopeId )
						.where( getDomainField(t).eq(domainId) )
						.and(getScopeField(t).eq(wrongScopeId))
						.getSQL( ParamType.INLINED ) 
						
						);
				int count = ctx.getDslContext()
					.update( t )
					.set( getScopeField(t), newScopeId )
					.where( getDomainField(t).eq(domainId) )
					.and(getScopeField(t).eq(wrongScopeId))
					.execute();
				if (count > 0 ) {
					messages.add( MessageFormat.format( "Modificadas {0} filas de la tabla {1}", count, t.getName())); 
				}
			});
		if ( AonCollectionUtils.isEmpty(messages)) {
			return div( "No se ha modificado ninguna fila" ).render();
		}
		UlTag ul = ul();
		AonCollectionUtils.stream(messages)
			.forEach( m -> ul.with( li(m) ));
		return ul.render();
	}	
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getScopeField(Table<T> table) {
		return (TableField<T, Integer>) table.field(SCOPE_LABEL);
	}
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_LABEL);
	}
}
