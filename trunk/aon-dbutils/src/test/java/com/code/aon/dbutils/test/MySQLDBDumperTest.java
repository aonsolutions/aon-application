package com.code.aon.dbutils.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.dbutils.MySQLDBDumper;
import com.code.aon.dbutils.event.DBUtilsEvent;
import com.code.aon.dbutils.event.DBUtilsListener;
import com.code.aon.dbutils.runner.DBUtilsRunner;

public class MySQLDBDumperTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		System.out.println(new Date());
		File file = File.createTempFile("test_", ".sql");
		System.out.println(file);
		PrintWriter pw = new PrintWriter(file);
		MySQLDBDumper dumper = new MySQLDBDumper(HibernateUtil.getSQLConnection(), pw);
		DBUtilsRunner thread = new DBUtilsRunner(dumper);
		dumper.addDBUtilsListener(new DBUtilsListener() {
			@Override
			public void eventHappen(DBUtilsEvent event) {
				System.out.println(new Date());
			}
		});
		new Thread(thread, "COPIA-SEGURIDAD").start();
	}
}
