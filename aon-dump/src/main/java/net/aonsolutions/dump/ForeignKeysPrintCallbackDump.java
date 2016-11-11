package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class ForeignKeysPrintCallbackDump extends AbstractChaimCallbackDump{

	//private CallbackDump  cb;
	private Integer parentDomain;
	int id_task;
	int idDomain;
	
	
	public ForeignKeysPrintCallbackDump(CallbackDump cb, int id, int idDomain) {
		super(cb);
		this.id_task = id;
		this.idDomain = idDomain;
		
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext, int id, IdsMap idsMap) {
		
		super.accept("BEGIN;");
		
		domainTables.forEach((t, k) -> {
			if (t.field("id") != null){
				super.accept("SET @" + t.getName().toUpperCase() + "=(SELECT MAX(id) FROM " + t.getName() + ");");
			}
		});
		
		super.accept("SET @" + DOMAIN.getName().toUpperCase() + "=(SELECT MAX(id) FROM " + DOMAIN.getName() + ");");
		
		parentDomain = dslContext.select(DOMAIN.PARENT).from(DOMAIN).where((DOMAIN.ID).equal(id)).fetchOne().value1();
		
		Integer idDomain = idsMap.getOrder(DOMAIN.getName(), id);

		idsMap.setEspecificOrder(DOMAIN.getName(), parentDomain, idDomain, false);
		
		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}
	
	@Override
	public void footer(){
	
		super.accept("COMMIT;");
	}
	
	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, boolean ciclica, List<?> references, Condition where) {
		
		if ( fk.getTable().equals(DOMAIN) && fk.getKey().getTable().equals(DOMAIN))
			return DSL.castNull(Integer.class);
		
		insertFKTask(dslContext, fk);
		
		//System.err.println(fk);
		super.onErrFk(dslContext, r,fk, aondump, idsMap, cb, ciclica, references, where);
		
		
		Record1<?> fkField;
		
		// Doesn't exit domain field
		if ((Field<Integer>)fk.getKey().getTable().field("domain") == null)
			return null;
		
		try {
			fkField = dslContext.select(fk.getKey().getFields().get(0))
				.from(fk.getKey().getTable().getName())
				.where((((Field<Integer>)fk.getKey().getTable().field("domain")).equal(parentDomain))
				.and(((Field<Integer>) fk.getKey().getFields().get(0)).eq(r.getValue((Field<Integer>)fk.getFields().get(0))))
				).fetchAny();
			
			Field<Integer> parentField = null;
			 
			if (fkField != null){
				parentField = DSL.field(((Integer)fkField.value1()).toString(), Integer.class);
				cb.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
			}
			return parentField;
			
		} catch ( NullPointerException e){
			e.getMessage();
			throw e;
		} 
	}

	private void insertFKTask(DSLContext dslContext, ForeignKey<?, ?> fk) {
		
		Date date = new Date();
		Timestamp time = new Timestamp(date.getTime());		
		
//		dslContext
//			.insertInto(TASK_COMMENT, TASK_COMMENT.DOMAIN, TASK_COMMENT.TASK, TASK_COMMENT.COMMENT, TASK_COMMENT.CREATION_USER, TASK_COMMENT.CREATION_DATE)
//			.values(this.idDomain, this.id_task, "WARNING: "+fk.toString(), "3203", time)
//			.execute();
		
	}

	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, boolean ciclica, Integer numRows) {
		
		if (ciclica){
			super.accept("SET FOREIGN_KEY_CHECKS=0;");
			super.accept(inSet, table, ciclica, numRows);
			super.accept("SET FOREIGN_KEY_CHECKS=1;");
		}else
			super.accept(inSet, table, ciclica, numRows);

	}

}
