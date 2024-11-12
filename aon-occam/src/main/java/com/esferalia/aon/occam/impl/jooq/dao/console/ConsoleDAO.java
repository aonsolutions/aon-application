package com.esferalia.aon.occam.impl.jooq.dao.console;

import static com.esferalia.aon.jooq.AonMaster.AON_MASTER;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdateConditionStep;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.jooq.AonMaster;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableFieldType;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.ql.JOOQRenderer;
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
	
	public static Stream<ConsoleDomain> getDomains(AONContext ctx, DomainParams params) {
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
	private static Condition getFilter(DomainParams params) {
		Condition c = DSL.trueCondition();
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
				.delete(APP_PARAM)
				.where(APP_PARAM.DOMAIN.eq(domainId))
				.and(APP_PARAM.NAME .eq(AppParam.AON_SUPPORT_ENABLED.toString()))
				.execute();
			ctx.log().info("Remote Access Change: OFF " + domainId + "(" + count + " rows)");
			return false;
		} else {
			int count = ctx.getDslContext()
				.insertInto(APP_PARAM)
				.set(APP_PARAM.DOMAIN, domainId)
				.set(APP_PARAM.NAME, AppParam.AON_SUPPORT_ENABLED.toString())
				.set(APP_PARAM.VALUE, String.valueOf(new Date().getTime()))
				.execute();
			ctx.log().info("Remote Access Change: ON " + domainId + "(" + count + " rows)");
			return true;
		}
		
	}

	@SuppressWarnings("unchecked")
	private static TableField<?, Integer> getPkField(String tableName) {
		return (TableField<?, Integer>) AON_MASTER.getTable(tableName).getPrimaryKey().getFields().get(0);
	}
	@SuppressWarnings("unchecked")
	private static TableField<?, Integer> getDomainField(String tableName) {
		return (TableField<?, Integer>) AON_MASTER.getTable(tableName).field("domain");
	}
	
	public static ConsoleTableRow getTableRow(AONContext ctx, ConsoleTableRow params) {
		try {
			ConsoleTableRow tableRow = getTableRowMetadata( ctx, params );
			Table<?> table = AON_MASTER.getTable(params.getTable());
			List<Field<?>> selectedFields = new LinkedList<>();
			tableRow.getFields()
				.values()
				.stream()
				.filter( f -> f.getType() != ConsoleTableFieldType.BINARY)
				.forEach( f -> selectedFields.add(table.field( f.getColumn() ) ));
			Optional<Record> rec = ctx.getDslContext().select( selectedFields )
				.from(table)
				.where( getConditions(params))
				.limit(1)
				.stream()
				.findFirst();
			if (rec.isPresent()) {
				Field<?> pkField = getPkField(params.getTable());
				tableRow.setId( (Integer) rec.get().getValue(pkField) );
				selectedFields	
					.stream()
					.forEach( field -> tableRow.getField( field.getName() ).setValue( toString(
							tableRow.getField( field.getName() ).getType(),
							rec.get().getValue(field)) ));
				return tableRow; 
			} 
			return null;
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
			.map( table -> table.getName() )
			.toArray(tableName -> new String[tableName]);
	}

	public static ConsoleTableRow getTableRowMetadata(AONContext ctx, ConsoleTableRow params) {
		ConsoleTableRow tableRow = new ConsoleTableRow()
				.setSchema(params.getSchema())
				.setTable( params.getTable() )
				.setId( params.getId() )
				.setDomain( params.getDomain() );
			
		Table<?> table = AON_MASTER.getTable(params.getTable());
		Arrays.stream( table.fields() )
			.map( field -> new ConsoleTableField().setColumn( field.getName() ).setType( getConsoleTableFieldType(field)))
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
			;
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
		validate(row);
		Table<?> table = AON_MASTER.getTable(row.getTable());
		TableField<?, Integer> pkField = getPkField(row.getTable());
		int count = ctx.getDslContext().delete(table)
			.where(pkField.eq(row.getId()))
			.execute();
		return (count>0);
	}
	
	public static ConsoleTableRow update(AONContext ctx, ConsoleTableRow row, ConsoleTableField field) {
		validate(row);
		validate(field);
		Table<?> table = AON_MASTER.getTable(row.getTable());
		TableField<?, Integer> pkField = getPkField(row.getTable());
		Field<?> updatableField = table.field( field.getColumn() );
		UpdateConditionStep<?> sentence = updateField( ctx.getDslContext().update(table) 
			, updatableField
			, fromString( field.getType(), field.getNewValue()))
			.where(pkField.eq(row.getId()));
		
		System.out.println( sentence.getSQL(ParamType.INLINED) ); 
		
		sentence.execute();
		ConsoleTableRow result = getTableRow(ctx,row); 	
		return result;
	}
	
	private static <T> UpdateSetMoreStep<?> updateField(UpdateSetFirstStep<?> update, Field<T> field, Object value) {
		if ( value == null) {
			return update.setNull(field);	
		}
	    return update.set(field, field.getType().cast(value));
	}	
	
}
