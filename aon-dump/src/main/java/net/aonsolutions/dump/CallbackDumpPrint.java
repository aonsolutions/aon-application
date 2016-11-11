package net.aonsolutions.dump;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.UpdateConditionStep;

public class CallbackDumpPrint extends AbstractPrintCallbackDump {
	
	
	// Consumer that will make our sql to an String
	public CallbackDumpPrint(PrintStream out) {
		super(out);
	}

	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, boolean ciclica, Integer numRows) {
		print(inSet.getSQL());
		println(";");
		
	}

	@Override
	public void onAttachInsert(UpdateConditionStep<?> update) {
		print(update.getSQL());
		println(";");
	}
	
	@Override
	public void accept(String sql) {
		println(sql);
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		
		println();
		accept("SET NAMES utf8;");
		accept("SET character_set_client = utf8;");
		println();
		
	}
	
	@Override
	public void onNewRow(Table<?> t, Map<Field<?>, Object> insertMap, Record r) {
		
		insertMap.forEach((f, v) -> {
			
			if (f.getDataType().isString() && v != null) {
				try {
					// Format String so we can make it compatible with the DataBase
					String value = new String(((String) v).getBytes("ISO-8859-1"), "ISO-8859-1");
	
					if (value.indexOf('\u0000') >= 0)
						value = value.replace("\u0000", "");
	
					insertMap.put((Field<String>) f, value);
	
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		
		});
	}
	
}
