package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Schema;
import org.jooq.Table;

public class DuplicateCallBackDump extends AbstractChaimCallbackDump {

	// TODO: ver como hacer un rollback manual para la duplicacion
	//private boolean safeMode;

	public DuplicateCallBackDump(CallbackDump cb, boolean safeMode) {
		super(cb);
		//this.safeMode = safeMode;
	}

	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {

		super.accept("SET @" + DOMAIN.getName().toUpperCase() + "_"+ id + "=(SELECT MAX(id) FROM " + DOMAIN.getName() + ") + 2;");

		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}


	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows, String varTableName) {
		
		if (table.field("id") != null) {
			if (!table.getName().equals("domain"))
				super.accept("SET " + varTableName + "=(SELECT MAX(id) FROM " + table.getName() + ") + "+ (numRows+1) +";");
		}

		if (!ciclica.isEmpty()){
			super.accept("SET FOREIGN_KEY_CHECKS=0;");
			super.accept(inSet, table, ciclica, numRows, varTableName);
			super.accept("SET FOREIGN_KEY_CHECKS=1;");
		} else
			super.accept(inSet, table, ciclica, numRows, varTableName);
		

	}

}
