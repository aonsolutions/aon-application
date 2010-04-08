package com.transtools.jdbc.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * Title:        CWeb
 * Description:  Web ( n-tier ) bassed applications development environment
 * Copyright:    Copyright (c) 2000
 * Company:      TransTOOLs S.A
 * @author
 * @version 1.0.0.1
 */

public class MyTest extends JFrame implements ActionListener {


  private static final String DRIVER = "com.transtools.jdbc.CtsqlJdbcDriver";
  public static final String URL = "jdbc:ctsql://eva:20010/almafac;DBPATH=c:\\tmp\\almafac5";
  public static final String LOGIN = "ctsql";
  public static final String PASSWD = "ctsql";

// Eva  private static final String[] tableTypes = {"TABLE", "VIEW", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
  private static final String[] tableTypes = {"TABLE"};

  private Connection con;
  private DatabaseMetaData databaseMetaData;
  private ArrayList arr = new ArrayList(0);
  private Iterator it;
  private Imprime im;
  private String url;
  java.util.Properties props;

  public MyTest() {
  try {
	props = new Properties();
	Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");
	url = "jdbc:ctsql://eva:20010/almafac";

	props = new java.util.Properties();
	props.setProperty( "user", "ctsql" );
	props.setProperty( "password", "ctsql" );
	props.setProperty( "DBPATH", "c:\\tmp\\almafac5" );
	this.con = DriverManager.getConnection( url, props );


  //  this.con = DriverManager.getConnection( URL, LOGIN, PASSWD );
	this.databaseMetaData = this.con.getMetaData();
	ResultSet rsT = this.databaseMetaData.getTables(null, null, "%", tableTypes);
/*  eva
	im = new Imprime();
	im.addActionListener(this);
	im.setCon(this.con);
	im.setDBMD(this.databaseMetaData);
*/
	while (rsT.next())
	{
		String s = rsT.getString("TABLE_NAME");
		arr.add(s);
		System.out.println(s);

	}
	rsT.close();
	im = new Imprime();
	im.addActionListener(this);
	im.setCon(this.con);
	im.setDBMD(this.databaseMetaData);
 // eva   it = arr.iterator();
 // eva  im.printColumns(it.next().toString()); // muestra todas las columnas de cada tabla de la BD.

//    im.printColumn("articulos");
	  im.printColumn("clientes");
	im.printColumn("albaranes");
	im.printColumn("unidades");
	im.printColumn("lineas");
//   im.printColumn("articulos");
	im.printColumn("formpagos");
	im.printColumn("provincias");
//    im.printColumn("proveedores");

// eva    im.printColumn("provincias");
/*
	im.printColumn("albaranes");
	im.printColumn("unidades");
	im.printColumn("lineas");
	im.printColumn("articulos");
	im.printColumn("formpagos");
	im.printColumn("proveedores");
*/
	this.con.close();
  } catch(Exception e) {System.out.println("ERROR MyTest()"); e.printStackTrace();}
  System.exit(0);
  }

  public void actionPerformed(ActionEvent evt) {
	try{
	  if (it.hasNext()) {
/*
	  this.con.close();
	  this.con = DriverManager.getConnection( MyTest.URL, MyTest.LOGIN, MyTest.PASSWD );
	  this.databaseMetaData = this.con.getMetaData();
	  im.setDBMD(this.databaseMetaData);
*/
		 im.printColumns(it.next().toString());
	  }
	} catch(Exception e) {System.out.println("ERROR actionPerformed()"); e.printStackTrace();}
 }

 /* Imprime inner class */
  public class Imprime {

	private ActionListener actionListener;
	private DatabaseMetaData databaseMetaData;
	private Connection con;

	public Imprime() {}

	public void setDBMD(DatabaseMetaData d) {
	  this.databaseMetaData = d;
	}

	public void setCon(Connection c) {
	  this.con = c;
	}

	public void addActionListener(ActionListener l) {
	  actionListener = l;
	}

	private void printColumns(String tableName) throws SQLException {
	  ResultSet rs = this.databaseMetaData.getColumns("", "", tableName, "%");
	  while (rs.next()) {
		System.out.println("TABLE NAME: " + rs.getString("TABLE_NAME") + " COLUMN NAME: " + rs.getString("COLUMN_NAME"));
	  }
	  rs.close();
	  this.fireActionListener();
	}

	private void printColumn(String tableName) throws SQLException {
	  ResultSet rs = this.databaseMetaData.getColumns("", "", tableName, "%");
//      ResultSet rs = this.databaseMetaData.getPrimaryKeys("", "", tableName);
//      ResultSet rs = this.databaseMetaData.getIndexInfo("","",tableName,false,true);
// eva      rs.beforeFirst();
	  while (rs.next()) {
		System.out.println("TABLE NAME: " + rs.getString("TABLE_NAME") + " COLUMN NAME: " + rs.getString("COLUMN_NAME"));
	  }
	  rs.close();
	  rs = this.databaseMetaData.getPrimaryKeys("", "", tableName);
	  while (rs.next()) {
				System.out.print(rs.getString("PK_NAME"));
				System.out.print(" ");
				System.out.print(rs.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(rs.getString("KEY_SEQ"));
	  }
	  rs.close();
	}

	public void fireActionListener() {
	  actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"tableCreated"));
	}

  }
 /* End of Imprime inner class */

  public static void main(String[] arg) {
	new MyTest().show();
  }

}
