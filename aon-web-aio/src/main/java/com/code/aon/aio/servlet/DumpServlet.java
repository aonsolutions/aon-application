package com.code.aon.aio.servlet;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Task.TASK;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.jooq.tables.records.TaskRecord;

import net.aonsolutions.dump.AonDump;
import net.aonsolutions.dump.CallbackDump;
import net.aonsolutions.dump.CallbackDumpPrint;
import net.aonsolutions.dump.CancelException;
import net.aonsolutions.dump.CommentsPrintCallbackDump;
import net.aonsolutions.dump.ForeignKeysPrintCallbackDump;
import net.aonsolutions.dump.IndexUniqueCallBackDump;
import net.aonsolutions.dump.ModifyDataCallBack;
import net.aonsolutions.dump.ParentCallbackDump;
import net.aonsolutions.parserMain.BackgroundCallBack;

public class DumpServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		String domain = req.getParameter("domain");
		Connection connection = null;
		

		
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getServerName(req));
			
		} catch (AonConnectionException e1) {
			e1.printStackTrace();
			
		}
		
		AonDump aonDump = new AonDump(connection);
		
		try{
			
			int idDomain = aonDump.dslContext.select(DOMAIN.ID).from(DOMAIN).where((DOMAIN.NAME).equal(domain)).fetchOne().value1();
			
			 TaskRecord tr = taskStart(aonDump, idDomain);

			//DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			
			out.println("{");
			out.println("id : " + tr.getId() + "," );
			out.println("domain : " + tr.getDomain() + "," );
			out.println("number : " + tr.getNumber() + "," );
			out.println("description : " + tr.getDescription() + "," );
			out.println("start_date : " + tr.getStartDate() + "," );
			out.println("status : " + tr.getStatus() + "," );
			out.println("percent : " + tr.getPercent() + "," );
			out.println("creation_user : " + tr.getCreationUser() + "," );
			out.println("comments : " + tr.getComments());
			
			out.flush();
			
			
			Thread hiloDump = new Thread( ()-> dump(aonDump, tr.getId() , domain, idDomain), "hiloDump");
			
			hiloDump.start();
			
			out.println("}");
			
		}finally {
			out.close();
		
		}
	}


	private TaskRecord taskStart(AonDump aonDump, int idDomain) {
		
		Date date = new Date();
		Timestamp time = new Timestamp(date.getTime());
		
		//int idDomain = ds.getDomainId();
		
		//String idUser = UserUtils.getInstance().getLoggedUser().getId().toString();
		
		 TaskRecord tr = aonDump.dslContext
				.insertInto(TASK, TASK.DOMAIN, TASK.NUMBER, TASK.DESCRIPTION, TASK.START_DATE, TASK.STATUS, TASK.PERCENT, TASK.CREATION_USER, TASK.COMMENTS)
				.values(idDomain, 1, "Dump Domain", time, (byte) TaskStatus.IN_PROGRESS.ordinal(), (byte) 0, "3203", "Dump starting")
				.returning(TASK.ID, TASK.DOMAIN, TASK.NUMBER, TASK.DESCRIPTION, TASK.START_DATE, TASK.STATUS, TASK.PERCENT, TASK.CREATION_USER, TASK.COMMENTS).fetchOne();// INSERT;
			
		return tr;
	}


	private static void dump(AonDump aonDump, int id_task, String domain, int idDomain) {
		
		try {

			PrintStream outZip = null;
			ZipOutputStream zos = null;
//			PipedInputStream pis = new PipedInputStream();
//			PipedOutputStream pos = new PipedOutputStream(pis);
			
			//File creation where result will be in
			//File file = new File("/tmp/dumpSQL.zip");
			File file = File.createTempFile("dumpSQL", ".zip");
			FileOutputStream fos = new FileOutputStream(file);
			zos = new ZipOutputStream(fos);
			zos.putNextEntry(new ZipEntry("sql_html"));
			outZip  = new PrintStream(zos, true, "UTF-8");
			
			//final Thread hiloInsert = new Thread( ()-> insert(aonDump, domain, pis), "hiloInsert");
			
			CallbackDump cb;
			//cb = new CallbackDumpExecute(aonDump.dslContext);
			cb = new CallbackDumpPrint(outZip);
			cb = new ParentCallbackDump(cb);
			cb = new IndexUniqueCallBackDump(cb);
			cb = new BackgroundCallBack(cb, System.out, aonDump, id_task, idDomain);
			cb = new ModifyDataCallBack(cb, "{name}_2", "domain", "name");
			cb = new ForeignKeysPrintCallbackDump(cb, id_task, idDomain);
			cb = new CommentsPrintCallbackDump(outZip, cb);

//			cb = new AbstractChaimCallbackDump(cb) {
//				public void header(org.jooq.Schema schema, String hostName, java.util.Map<org.jooq.Table<?>,Integer> domainTables, org.jooq.DSLContext dslContext, int id, net.aonsolutions.dump.IdsMap idsMap) {
//					super.header(schema, hostName, domainTables, dslContext, id, idsMap);
//					hiloInsert.start();
//				};
//			};
			
			try{
				aonDump.findDomainInTables(aonDump.connection, aonDump.dslContext, cb, "localhost", "sig-grupo-esferalia", domain);
			} catch (CancelException e){
				System.out.println("DESCARGA CANCELADA");
				file.delete();
				return;
			}
			
			if (zos != null)
				zos.closeEntry();
			
			outZip.close();
			
			insert(aonDump, idDomain, new FileInputStream(file));
			
			Date date = new Date();
			Timestamp time = new Timestamp(date.getTime());
			
			aonDump.dslContext.update(TASK)
				.set(TASK.PERCENT, (byte) 100)
				.set(TASK.STATUS, (byte) TaskStatus.FINISHED.ordinal())
				.set(TASK.COMMENTS, "DUMP FINISHED")
				.set(TASK.END_DATE, time)
				.where(TASK.ID.eq(id_task))
				.execute();
			
			System.out.println("FIN DEL PROGRAMA");
			
		} catch (IOException e) {
			e.printStackTrace();
	
		}

	}


	private static void insert(AonDump aonDump, int idDomain, FileInputStream pis) {
		
//		
//		InputStreamReader is = new InputStreamReader(pis);
//		char cbuf [] = new char [256] ;
//		try {
//			while ( is.read(cbuf) >= 0) {
//				System.out.print(cbuf);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		PreparedStatement preparedStatement = null;
		
		InputStream is = pis;
		
		
		int registryDomain = aonDump.dslContext.select(COMPANY.REGISTRY).from(COMPANY).where((COMPANY.DOMAIN).equal(idDomain)).fetchOne().value1();
		
		String insertAttach = "insert into rattach"
				+ "(domain, registry, description, data) values"
				+ "(?, ?, ?, ?)";
		
		
		try {
			preparedStatement = aonDump.connection.prepareStatement(insertAttach);
			preparedStatement.setInt(1, idDomain);
			preparedStatement.setInt(2, registryDomain);
			preparedStatement.setString(3, "Dump Domain");
			preparedStatement.setBlob(4, is);
			preparedStatement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
		
	
}