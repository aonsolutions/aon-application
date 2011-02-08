package com.transtools.jdbc.test;

public class Data{
	protected String driver;
	protected String protocol;
	protected String host;
	protected String dbpath;
	protected String dbname;
	protected String url;
	protected String user;
	protected String password;
	private int port = 20000;

	public Data(){
		driver = "com.transtools.jdbc.CtsqlJdbcDriver";
		protocol = "ctsql";
		host = "mother";
		dbpath = "/disk3/caravel/demos/insecuss/bd";
//			dbname = "com.transtools.jdbc.test";
		dbname = "test";
		user = "dctl";
		password = "simple1";
	}

	public String createUrl()
	{
		return new String("jdbc:"+protocol+"://" + host +":" + port + "/"+ dbname + ";DBPATH="+ dbpath);
	}

  }
