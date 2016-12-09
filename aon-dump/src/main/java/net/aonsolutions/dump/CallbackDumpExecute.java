package net.aonsolutions.dump;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Table;
import org.jooq.UpdateConditionStep;

public class CallbackDumpExecute implements CallbackDump{

	private DSLContext dslContext;
	
	// Consumer that will execute our sql
	public CallbackDumpExecute(DSLContext dslContext) {			
		this.dslContext = dslContext;
	}
	
	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows) {
		if (!ciclica.isEmpty()){
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			inSet.execute();
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		} else
			inSet.execute();
	}

	@Override
	public void onAttachInsert(UpdateConditionStep<?> update) {
		update.execute();
	}
	
	@Override
	public void accept(String sql) {
		dslContext.execute(sql);
	}

}
