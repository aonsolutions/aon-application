package com.esferalia.aon.payroll.ctsql2mysql;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.salary.enumeration.SalaryType.SETTLE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.code.aon.dbutils.AonSQLException;
import com.den_4.inotify_java.EventQueueFull;
import com.den_4.inotify_java.Inotify;
import com.den_4.inotify_java.InotifyEvent;
import com.den_4.inotify_java.InotifyEventListener;
import com.den_4.inotify_java.NativeInotify;
import com.den_4.inotify_java.enums.Event;
import com.den_4.inotify_java.exceptions.InotifyException;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;

public class Ctsql2MysqlD {

	static class ReschedulableTimer {
		private Timer timer;
		private Runnable task;
		private TimerTask timerTask;

		public ReschedulableTimer(Runnable runnable) {
			task = runnable;
			timer = new Timer();
		}

		public void reschedule(long delay) {
			if (timerTask != null) {
				timerTask.cancel();
			}
			timerTask = new TimerTask() {
				public void run() {
					task.run();
				}
			};
			timer.schedule(timerTask, delay);
		}
	}

	static class InputStream2Output implements Runnable {

		private InputStream in;
		private OutputStream out;

		public InputStream2Output(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void run() {
			try {
				int read = -1;
				byte b[] = new byte[256];
				while ((read = in.read(b)) != -1)
					out.write(b, 0, read);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private class Ctsql2MysqlDump implements Runnable, InotifyEventListener {

		private ReschedulableTimer timer;

		public Ctsql2MysqlDump() {
			this.timer = new ReschedulableTimer(this);
		}

		// ------------------------------------------
		// InotifyEventListener
		// ------------------------------------------
		@Override
		public void queueFull(EventQueueFull e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void filesystemEventOccurred(InotifyEvent event) {
			try {
				MysqlDB.info("ctsql2mysqld: notify event {}",
						event.toString());
				timer.reschedule(Ctsql2MysqlD.this.delay * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ------------------------------------------
		// Runnable
		// ------------------------------------------
		@Override
		public void run() {
			try {
				ctsql2Mysql.dropDatabase();
			} catch (SQLException e) {
			}

			try {
				ctsql2Mysql.transfer();
				MysqlDB.info("ctsql2mysqld : exec ");
				exec();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AonSQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private Inotify inotify;
	private Ctsql2Mysql ctsql2Mysql;
	private long delay = 15;

	public Ctsql2MysqlD(String[] args) throws InotifyException {
		this.ctsql2Mysql = new Ctsql2Mysql(args);
	}

	private String getDBPath() {
		String ctsqlUrl = ctsql2Mysql.getCtsqlURL();
		Pattern pattern = Pattern
				.compile("jdbc:ctsql://[^/]+/([^;]+)(;[^;]*)*;DBPATH=([^;]+)");
		Matcher matcher = pattern.matcher(ctsqlUrl);
		return matcher.find() ? matcher.group(3) + File.separator
				+ matcher.group(1) + ".dbs" : null;
	}

	private String getDirPath(String tableName) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		Connection ctsqlConnection = null;
		try {
			ctsqlConnection = Ctsql2MysqlD.this.ctsql2Mysql
					.getCtsqlConnection();
			stmt = ctsqlConnection.prepareStatement("SELECT * "
					+ " FROM systables" + " WHERE tabname = ?");
			stmt.setString(1, tableName);

			rs = stmt.executeQuery();
			return rs.next() ? rs.getString("dirpath") : null;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (ctsqlConnection != null)
				ctsqlConnection.close();
		}

	}

	private void startListen() throws InotifyException {

		delay = ctsql2Mysql.getDelay();

		NativeInotify.loadLibrary(null);
		inotify = new Inotify();

		String dbPath = getDBPath();

		Ctsql2MysqlDump dump = new Ctsql2MysqlDump();
		for (String table : ctsql2Mysql.getTables()) {
			try {
				String dirPath = getDirPath(table);
				String path = dbPath + File.separator + dirPath + ".dat";
				int watch = inotify.addWatch(path, Event.Modify);
				inotify.addListener(watch, dump);

			} catch (SQLException e) {
				// TODO: /var/log/messages
			}
		}

		while (inotify.isActive()) {
			Thread.yield();
		}
	}

	private void exec() throws IOException {
		String commands[] = ctsql2Mysql.getCommands();
		if (commands == null)
			return;
		for (String cmd : commands)
			exec(cmd);
	}

	private void exec(String command) throws IOException {

		MysqlDB.info("ctsql2mysqld : command {}", command);

		Runtime runtime = Runtime.getRuntime();
		String cmd [] = {"/bin/sh" , "-c", command };
		Process process = runtime.exec(cmd);

		InputStream out = process.getInputStream();
		new InputStream2Output(out, System.out).run();

		InputStream err = process.getErrorStream();
		new InputStream2Output(err, System.err).run();
	}

	/**
	 * @param args
	 * @throws InotifyException
	 */
	public static void main(String[] args) throws InotifyException {
		new Ctsql2MysqlD(args).startListen();
	}

}
