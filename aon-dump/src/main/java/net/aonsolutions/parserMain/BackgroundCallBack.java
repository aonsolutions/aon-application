package net.aonsolutions.parserMain;

import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Schema;
import org.jooq.Table;

import com.code.aon.groupware.enumeration.TaskStatus;

import net.aonsolutions.dump.AbstractChaimCallbackDump;
import net.aonsolutions.dump.AonDump;
import net.aonsolutions.dump.CallbackDump;
import net.aonsolutions.dump.CancelException;
import net.aonsolutions.dump.IdsMap;

public class BackgroundCallBack extends AbstractChaimCallbackDump{

	PrintStream out;
	Integer totalTables;
	Integer numTablesDownloaded;
	double process;
	int id_task;
	AonDump aonDump;
	int idDomain;
	
	public BackgroundCallBack(CallbackDump cb, PrintStream out, AonDump aonDump, int id, int idDomain) {
		super(cb);
		this.out = out;
		this.numTablesDownloaded = 1;
		this.aonDump = aonDump;
		this.id_task = id;
		this.idDomain = idDomain;
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		
		String out = "<a style='color: green;'> Starting progress... 0%. Rest tables: " + domainTables.size() + ". Dowloaded tables: 0 </a>";
		
		insertTaskComment (out);
		
		this.totalTables = domainTables.size();
		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}

	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows) {
		
		
		process = ( (double) this.numTablesDownloaded / totalTables) * 100;
		process = Math.rint(process*1)/1;
		
		String out = "";
		
		this.aonDump.dslContext.execute("LOCK TABLES " + TASK.getName() + " READ;");
		Byte status = this.aonDump.dslContext.select(TASK.STATUS).from(TASK).where((TASK.ID).eq(id_task)).fetchOne().value1();
		this.aonDump.dslContext.execute("UNLOCK TABLES;");
		
		if (status == (byte) TaskStatus.DELETED.ordinal()){
			out = "<a style='color: red;'> WARNING: Download canceled </a>";
			insertTaskComment(out);
			
			process = 100;
			this.aonDump.dslContext.update(TASK).set(TASK.PERCENT, (byte) process).where(TASK.ID.eq(this.id_task)).execute();
			
			throw new CancelException();
		
		}else if (status == (byte) TaskStatus.FINISHED.ordinal()){			
			System.out.println("Descarga Finalizada BD.");
			
		}else if (process < 100){

			out = "<a  style='color: green;'> INFO: table downloaded: " + table.getName() +  ", se han descargado: " + numRows +" filas. </a>";
			
			insertTaskComment(out);
			
			this.aonDump.dslContext.update(TASK).set(TASK.PERCENT, (byte) process).where(TASK.ID.eq(this.id_task)).execute();
			
		}
		
		this.numTablesDownloaded++;

		super.accept(inSet, table, ciclica, numRows);
	}
	
	private void insertTaskComment(String out) {
		
		Date date = new Date();
		Timestamp time = new Timestamp(date.getTime());
		
		this.aonDump.dslContext
			.insertInto(TASK_COMMENT, TASK_COMMENT.DOMAIN, TASK_COMMENT.TASK, TASK_COMMENT.COMMENT, TASK_COMMENT.CREATION_USER, TASK_COMMENT.CREATION_DATE)
			.values(this.idDomain, this.id_task, out, "3203", time)
			.execute();
		
	}

}
