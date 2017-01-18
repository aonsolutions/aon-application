package com.esferalia.aon.gwt.dump.server;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.annotation.WebServlet;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.dump.client.ConnectService;
import com.esferalia.aon.gwt.dump.shared.Domain;
import com.esferalia.aon.gwt.dump.shared.Parameters;
import com.esferalia.aon.gwt.dump.shared.Progress;
import com.esferalia.aon.gwt.dump.shared.Task;
import com.esferalia.aon.jooq.tables.AppParam;
import com.esferalia.aon.jooq.tables.records.TaskRecord;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.dump.AonDump;
import net.aonsolutions.dump.DuplicateCallBackDump;
import net.aonsolutions.dump.CallbackDump;
import net.aonsolutions.dump.CallbackDumpExecute;
import net.aonsolutions.dump.CallbackDumpPrint;
import net.aonsolutions.dump.CancelException;
import net.aonsolutions.dump.CommentsPrintCallbackDump;
import net.aonsolutions.dump.DomainParentCallBackDump;
import net.aonsolutions.dump.DomainSiblingCallBackDump;
import net.aonsolutions.dump.DomainZeroCallbackDump;
import net.aonsolutions.dump.DownloadCallBackDump;
import net.aonsolutions.dump.EraseUser;
import net.aonsolutions.dump.ErrorReferenceCallBackDump;
import net.aonsolutions.dump.IndexUniqueCallBackDump;
import net.aonsolutions.dump.ModifyDataCallBack;
import net.aonsolutions.dump.ParentCallbackDump;
import net.aonsolutions.dump.SiblingCallBackDump;
import net.aonsolutions.dump.TaskProcessCallBack;
import net.aonsolutions.dump.UpdateCallBack;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Dump Servlet", urlPatterns = { "/aon_gwt_dump/dump" })

public class ConnectServiceImpl extends AonRemoteServiceServlet implements ConnectService {

	@Override
	public List<Domain> getAvailableDomains() throws AonCoreException {

		Connection connection = null;
		Settings settings = null;
		DSLContext dslContext = null;
		List<Domain> listDomains = new ArrayList<Domain>();

		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();

			Integer idDomain = getDomainID(); //UserUtils.getInstance().getLoggedUser().getDomain();

			settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType(ParamType.INLINED);

			// Establish context
			dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

			Result<Record4<Integer, String, String, String>> domainsResult = dslContext
					.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION, DOMAIN.SUBDOMAINSUFFIX).from(DOMAIN)
					.where(DOMAIN.PARENT.eq(idDomain).or(DOMAIN.ID.eq(idDomain))).fetch();

			domainsResult.forEach(d -> listDomains.add(new Domain().setId(d.getValue(DOMAIN.ID))
					.setName(d.getValue(DOMAIN.NAME)).setDescription(d.getValue(DOMAIN.DESCRIPTION))
					.setSuffix(d.getValue(DOMAIN.SUBDOMAINSUFFIX))));

			return listDomains;

		} catch (SQLException e1) {
			throw new AonCoreException(e1.getMessage());

		} finally {
			releaseFacesContext();
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Integer getDomainPermission(){
		
		Connection connection = null;
		Settings settings = null;
		DSLContext dslContext = null;

		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();

			Integer idDomain = getDomainID();//UserUtils.getInstance().getLoggedUser().getDomain();

			settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType(ParamType.INLINED);

			// Establish context
			dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

			String domainResultAllow = dslContext.select(APP_PARAM.VALUE)
					 .from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(idDomain)
							 .and(APP_PARAM.NAME.eq("ALLOW_DUPLICATE_DOMAIN")))
					 .fetchOne(APP_PARAM.VALUE);
			
			if (idDomain == 0 || Boolean.valueOf(domainResultAllow))
				return 2; //FULL EQUIPE
			
			Byte domainsResultManagement = dslContext
					.select(DOMAIN.DOMAINMANAGEMENT).from(DOMAIN)
					.where(DOMAIN.ID.eq(idDomain)).fetchOne(DOMAIN.DOMAINMANAGEMENT);

			if (domainsResultManagement == 1)
				return 1; //DOS MODOS BACKUP
			else
				return 0; //UN MODO BACKUP

		} catch (SQLException e1) {
			throw new AonCoreException(e1.getMessage());

		} finally {
			releaseFacesContext();
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Override	
	public Task dumpDomain(String domainName, Parameters parameters) {

		Connection connection = null;

		try {
			initFacesContext();

			connection = AonServletUtils.getConnection();
			AonDump aondump = new AonDump(connection);

			int idDomain = aondump.dslContext.select(DOMAIN.ID).from(DOMAIN).where((DOMAIN.NAME).equal(domainName))
					.fetchOne().value1();

			TaskRecord tr = taskStart(aondump, parameters.getDescripcionEmpresa());
			Integer id = tr.getId();
			Thread hiloDump = new Thread(() -> dump(aondump, id, domainName, idDomain, tr, parameters), "hiloDump");

			hiloDump.start();

			return new Task().setId(id);

		} catch (SQLException e1) {
			throw new AonCoreException(e1.getMessage());

		} finally {
			releaseFacesContext();

		}

	}

	private TaskRecord taskStart(AonDump aonDump, String domainDescription) {

		Date date = new Date();
		Timestamp time = new Timestamp(date.getTime());

		String idUser = UserUtils.getInstance().getLoggedUser().getId().toString();
		Integer idDomain = UserUtils.getInstance().getLoggedUser().getDomain();

		TaskRecord tr = aonDump.dslContext
				.insertInto(TASK, TASK.DOMAIN, TASK.NUMBER, TASK.DESCRIPTION, TASK.START_DATE, TASK.STATUS,
						TASK.PERCENT, TASK.CREATION_USER, TASK.COMMENTS)
				.values(idDomain, 0, domainDescription, time, (byte) TaskStatus.IN_PROGRESS.ordinal(), (byte) 0, idUser,
						"Dump starting")
				.returning(TASK.ID, TASK.DOMAIN, TASK.NUMBER, TASK.DESCRIPTION, TASK.START_DATE, TASK.STATUS,
						TASK.PERCENT, TASK.CREATION_USER, TASK.COMMENTS)
				.fetchOne();// INSERT;

		return tr;
	}

	private static void dump(AonDump aonDump, int id_task, String domain, int idDomain, TaskRecord tr,
			Parameters parameters) {

		try {

			PrintStream outZip = null;
			ZipOutputStream zos = null;
			CallbackDump cb = null;
			File file = null;
			file = File.createTempFile("dumpSQL", ".zip");
			FileOutputStream fos = new FileOutputStream(file);
			zos = new ZipOutputStream(fos);
			zos.putNextEntry(new ZipEntry("dump.sql"));
			outZip = new PrintStream(zos, true, "UTF-8");

			if (parameters.getDownloadType() == 0 || parameters.getDownloadType() == 1) {
				cb = new CallbackDumpPrint(outZip);
				
			} else if (parameters.getDownloadType() == 2 || parameters.getDownloadType() == 3){
				try {
					cb = new CallbackDumpExecute(aonDump.dslContext);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			cb = new UpdateCallBack(cb, System.out, aonDump, parameters.getDescripcionEmpresa(), idDomain);
			cb = new ErrorReferenceCallBackDump(cb);
			
			if (parameters.getDownloadType() == 0 || parameters.getDownloadType() == 2)
				cb = new SiblingCallBackDump(cb);
			
			if (parameters.getDownloadType() == 1 || parameters.getDownloadType() == 3)
				cb = new ParentCallbackDump(cb);
			
			cb = new DomainZeroCallbackDump(cb);
			
			if (parameters.getDownloadType() == 0 || parameters.getDownloadType() == 2)
				cb = new DomainSiblingCallBackDump(cb);
			
			if (parameters.getDownloadType() == 1 || parameters.getDownloadType() == 3)
				cb = new DomainParentCallBackDump(cb, idDomain);
			
			cb = new IndexUniqueCallBackDump(cb);
			cb = new ModifyDataCallBack(cb, parameters.getNewDomain(), "domain", "name");
			cb = new TaskProcessCallBack(cb, System.out, aonDump, tr.getId(), idDomain);
			
			if (parameters.getDownloadType() == 0 || parameters.getDownloadType() == 1 || parameters.getDownloadType() == 4) 
				cb = new DownloadCallBackDump(cb, false);
			else
				cb = new DuplicateCallBackDump(cb, false); 

			if (parameters.getComments())
				cb = new CommentsPrintCallbackDump(outZip, cb);
			if (parameters.getEraseUsers())
				cb = new EraseUser(cb, aonDump.dslContext, parameters.getNewUserPass(), parameters.getNewUserName());

			
			try {
				ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
				String database = ci.getDomainDatabase(domain);
				aonDump.findDomainInTables(aonDump.connection, aonDump.dslContext, cb, "localhost", database, domain);
			} catch (CancelException e) {
				System.out.println("DESCARGA CANCELADA");
				if (file != null)
					file.delete();
				return;
			}

			if (zos != null)
				zos.closeEntry();

			if (outZip != null)
				outZip.close();

			insert(aonDump, idDomain, new FileInputStream(file), id_task, parameters);

			System.out.println("FIN DEL PROGRAMA");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}

	}

	private static void insert(AonDump aonDump, int idDomain, FileInputStream pis, int id_task, Parameters parameters) {

		PreparedStatement preparedStatement = null;

		InputStream is = pis;

		int registryDomain = aonDump.dslContext.select(COMPANY.REGISTRY).from(COMPANY)
				.where((COMPANY.DOMAIN).equal(idDomain)).fetchOne().value1();

		String insertAttach = "insert into rattach" + "(domain, registry, description, data) values" + "(?, ?, ?, ?)";

		Integer idAttach = 0;

		try {

			aonDump.dslContext.execute("LOCK TABLES `rattach` WRITE;");

			preparedStatement = aonDump.connection.prepareStatement(insertAttach);
			preparedStatement.setInt(1, idDomain);
			preparedStatement.setInt(2, registryDomain);
			preparedStatement.setString(3, "DumpDomain.zip");
			preparedStatement.setBlob(4, is);
			preparedStatement.execute();

			idAttach = aonDump.dslContext.select(DSL.max(RATTACH.ID)).from(RATTACH).fetchOne().value1();

			System.out.println(idAttach);

			aonDump.dslContext.execute("UNLOCK TABLES;");

			Date date = new Date();
			Timestamp time = new Timestamp(date.getTime());

			Timestamp startDate = aonDump.dslContext.select(TASK.START_DATE).from(TASK).where(TASK.ID.eq(id_task))
					.fetchOne().value1();

			int totalTime = new Timestamp(time.getTime() - startDate.getTime()).getMinutes();

			aonDump.dslContext
					.insertInto(TASK_COMMENT, TASK_COMMENT.DOMAIN, TASK_COMMENT.TASK, TASK_COMMENT.COMMENT,
							TASK_COMMENT.CREATION_USER, TASK_COMMENT.CREATION_DATE)
					.values(idDomain, id_task, "Tiempo total: " + totalTime + " minutos.", "3203", time).execute();

			if (parameters.getDownloadType() == 0 || parameters.getDownloadType() == 1) {
				aonDump.dslContext
						.insertInto(TASK_COMMENT, TASK_COMMENT.DOMAIN, TASK_COMMENT.TASK, TASK_COMMENT.COMMENT,
								TASK_COMMENT.CREATION_USER, TASK_COMMENT.CREATION_DATE)
						.values(idDomain, id_task, "DUMP_FINISHED: <a href='downloadDataServlet?idTask=" + idAttach
								+ "' style='color: blue;'> Descargar </a>", "3203", time)
						.execute();
			}

			aonDump.dslContext.update(TASK).set(TASK.PERCENT, (byte) 100).set(TASK.NUMBER, idAttach)
					.set(TASK.STATUS, (byte) TaskStatus.FINISHED.ordinal()).set(TASK.COMMENTS, "DUMP FINISHED")
					.set(TASK.END_DATE, time).where(TASK.ID.eq(id_task)).execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Progress process(Integer idTask, int lastID) {
		Connection connection = null;

		try {

			connection = AonServletUtils.getConnection();
			AonDump aondump = new AonDump(connection);

			Byte percent = aondump.dslContext.select(TASK.PERCENT).from(TASK).where(TASK.ID.eq(idTask)).fetchOne()
					.value1();

			Byte status = aondump.dslContext.select(TASK.STATUS).from(TASK).where(TASK.ID.eq(idTask)).fetchOne()
					.value1();

			List<String> comments = aondump.dslContext.select(TASK_COMMENT.COMMENT).from(TASK_COMMENT)
					.where((TASK_COMMENT.TASK).eq(idTask).and((TASK_COMMENT.ID).greaterThan(lastID)))
					.orderBy(TASK_COMMENT.ID.asc()).fetch(TASK_COMMENT.COMMENT);

			List<Integer> lastIDTask = aondump.dslContext.select(TASK_COMMENT.ID).from(TASK_COMMENT)
					.where((TASK_COMMENT.TASK).eq(idTask)).orderBy(TASK_COMMENT.ID.desc()).limit(1)
					.fetch(TASK_COMMENT.ID);

			return new Progress().setComments(comments).setPercent(Byte.toUnsignedInt(percent))
					.setLastId(lastIDTask.get(0)).setStatus(status.intValue());

		} catch (SQLException e1) {
			throw new AonCoreException(e1.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Boolean cancelDownload(Integer id_task) {

		Connection connection = null;

		try {
			connection = AonServletUtils.getConnection();

			AonDump aondump = new AonDump(connection);

			Date date = new Date();
			Timestamp time = new Timestamp(date.getTime());

			aondump.dslContext.update(TASK).set(TASK.STATUS, (byte) TaskStatus.DELETED.ordinal())
					.set(TASK.COMMENTS, "DUMP CANCELED").set(TASK.END_DATE, time).where(TASK.ID.eq(id_task)).execute();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public List<Task> getTaskPending() {

		Connection connection = null;
		Settings settings = null;
		DSLContext dslContext = null;
		List<Task> tasks = new ArrayList<Task>();

		try {
			initFacesContext();

			String idUser = UserUtils.getInstance().getLoggedUser().getId().toString();
			Integer idDomain = UserUtils.getInstance().getLoggedUser().getDomain();
			
			connection = AonServletUtils.getConnection();
			

			settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType(ParamType.INLINED);

			// Establish context
			dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

			Result<Record2<Integer, String>> result = dslContext.select(TASK.ID, TASK.DESCRIPTION).from(TASK)
					.where(TASK.CREATION_USER.equal(idUser)
					.and(TASK.ID.greaterThan(0)))
					.and(TASK.DOMAIN.eq(idDomain))
					.fetch();

			result.forEach(
					r -> tasks.add(new Task().setId(r.getValue(TASK.ID)).setDescription(r.getValue(TASK.DESCRIPTION))));

		} catch (SQLException e1) {
			throw new AonCoreException(e1.getMessage());

		} finally {
			releaseFacesContext();

			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tasks;
	}

	@Override
	public Boolean eraseDownload(Integer idTask) {
		Connection connection = null;
		Settings settings = null;
		DSLContext dslContext = null;

		try {
			initFacesContext();

			connection = AonServletUtils.getConnection();

			settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType(ParamType.INLINED);

			// Establish context
			dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

			Integer idRattach = dslContext.select(TASK.NUMBER).from(TASK).where(TASK.ID.eq(idTask)).fetchOne().value1();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext.update(TASK_COMMENT).set(TASK_COMMENT.ID, TASK_COMMENT.ID.mul(-1))
					.set(TASK_COMMENT.TASK, TASK_COMMENT.TASK.mul(-1)).where(TASK_COMMENT.TASK.eq(idTask)).execute();

			dslContext.update(TASK).set(TASK.ID, TASK.ID.mul(-1)).where(TASK.ID.eq(idTask)).execute();

			if (idRattach != 0) {

				dslContext.update(RATTACH).set(RATTACH.ID, RATTACH.ID.mul(-1)).where(RATTACH.ID.eq(idRattach))
						.execute();
			}

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

			return true;

		} catch (SQLException e1) {
			throw new AonCoreException(e1.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
