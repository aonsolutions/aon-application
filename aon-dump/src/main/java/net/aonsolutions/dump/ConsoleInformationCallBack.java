package net.aonsolutions.dump;

import java.io.PrintStream;
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

public class ConsoleInformationCallBack extends AbstractChaimCallbackDump{

	PrintStream out;
	Integer totalTables;
	Integer numTablesDownloaded;
	double process;
	AonDump aonDump;
	int idDomain;
	
	public ConsoleInformationCallBack(CallbackDump cb, PrintStream out, AonDump aonDump, int id, int idDomain) {
		super(cb);
		this.out = out;
		this.numTablesDownloaded = 1;
		this.aonDump = aonDump;
		this.idDomain = idDomain;
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		
		this.out.println("[  \033[0;32mOK\033[0;0m  ]\tStarting progress... 0%. Rest tables: " + domainTables.size() + ". Dowloaded tables: 0");
		this.totalTables = domainTables.size();
		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}
	
	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap,
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		
		String errorFK = fkToString(fk);
		
		this.out.println("[  \033[0;31mWARNING\033[0;0m  ]\t" + errorFK);
		return super.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}

	private String fkToString(ForeignKey<?, ?> fk) {
		return "Error reference " + fk.getName() + " foreing key (" + fk.getKey().getName() + ") references to " + fk.getKey().getTable().getName();
	}

	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows, String varTableName) {
		
		process = ( (double) this.numTablesDownloaded / totalTables) * 100;
		process = Math.rint(process*1)/1;
		
		if (process >= 100)
			this.out.println("[  \033[0;32mOK\033[0;0m  ]\tTable downloaded: " + table.getName() +  ", se han descargado: " + numRows +" filas. Porcentaje: 100");
		else
			this.out.println("[  \033[0;32mOK\033[0;0m  ]\tTable downloaded: " + table.getName() +  ", se han descargado: " + numRows +" filas. Porcentaje: "+ process);
			
		this.numTablesDownloaded++;
		
		super.accept(inSet, table, ciclica, numRows, varTableName);
	}
	
	@Override
	public void footer() {
		this.out.println("[  \033[0;32mINFO\033[0;0m  ]\tDownload Finished");
		super.footer();
	}
}
