package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class SiblingCallbackDump2 extends AbstractChaimCallbackDump {
	
	private Integer parentDomain;
	
	public SiblingCallbackDump2(CallbackDump cb) {
		super(cb);
		// TODO Auto-generated constructor stub
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
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap,
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		super.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
		Integer foreignKey = r.getValue((Field<Integer>)fk.getFields().get(0));
		return foreignKey != null ? DSL.field(foreignKey.toString(), Integer.class) : DSL.castNull(Integer.class);
	}

	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows) {
		
		if (!ciclica.isEmpty()){
			super.accept("SET FOREIGN_KEY_CHECKS=0;");
			super.accept(inSet, table, ciclica, numRows);
			super.accept("SET FOREIGN_KEY_CHECKS=1;");
		}else
			super.accept(inSet, table, ciclica, numRows);

	}

}
