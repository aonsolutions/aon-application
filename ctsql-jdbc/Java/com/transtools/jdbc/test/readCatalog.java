package com.transtools.jdbc.test;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class readCatalog {
	public static void main(String[] args)
	{
		java.util.Properties props;
		props = new Properties();
		ResultSet resultset;
		ResultSet resultset1;
		ResultSet resultset2;
		ResultSet resultset3;
		String types[]={"TABLE"};
		String tablename;
		try
		{
			Connection con = SqlHelper.getConnection();
			DatabaseMetaData dbmd= con.getMetaData();

			resultset=dbmd.getTables(null,null,"%",types);
			while(resultset.next())
			{
				tablename=resultset.getString("TABLE_NAME");
				System.out.print(tablename);
				System.out.print(" ");
				System.out.print(resultset.getString("TABLE_TYPE"));
				System.out.print(" ");
				System.out.println(resultset.getString("REMARKS"));
/*
				resultset1=dbmd.getColumns(null,null,tablename,"%");
				System.out.println("COLUMNS");

				while(resultset1.next())
				{
					System.out.print(resultset1.getString("TABLE_NAME"));
					System.out.print(" ");
					System.out.println(resultset1.getString("COLUMN_NAME"));
	//                System.out.print(" ");
	//                System.out.print(resultset1.getString("DATA_TYPE"));
	//                System.out.print(" ");
	//                System.out.println(resultset1.getString("COLUMN_DEF"));
				}
				resultset1.close();
*/
				System.out.println("INDEX");

				resultset2=dbmd.getIndexInfo(null,null,tablename,false,false);
				while(resultset2.next())
				{
					System.out.print(resultset2.getString("TABLE_NAME"));
					System.out.print(" ");
					System.out.print(resultset2.getString("INDEX_NAME"));
					System.out.print(" ");
					System.out.print(resultset2.getString("ORDINAL_POSITION"));
					System.out.print(" ");
					System.out.print(resultset2.getString("COLUMN_NAME"));
					System.out.print(" ");
					System.out.print(resultset2.getString("ASC_OR_DESC"));
					System.out.print(" ");
  //                System.out.println(resultset2.getString("CARDINALITY"));
				}
				resultset2.close();
/*
				resultset1=dbmd.getPrimaryKeys(null,null,tablename);

				while(resultset1.next())
				{
					System.out.print(resultset1.getString("PK_NAME"));
					System.out.print(" ");
					System.out.print(resultset1.getString("COLUMN_NAME"));
					System.out.print(" ");
					System.out.println(resultset1.getString("KEY_SEQ"));
				}
				resultset1.close();
				resultset1=dbmd.getCrossReference(
								null,
								null,
								null,
								null,
								null,
								tablename
								);

				while(resultset1.next())
				{
					System.out.print(resultset1.getString("PKTABLE_NAME"));
					System.out.print(" ");
					System.out.print(resultset1.getString("PKCOLUMN_NAME"));
					System.out.print(" ");
					System.out.print(resultset1.getString("KEY_SEQ"));
					System.out.print(" ");
					System.out.print(resultset1.getShort("UPDATE_RULE"));
					System.out.print(" ");
					System.out.print(resultset1.getShort("DELETE_RULE"));
					System.out.print(" ");
					System.out.println(resultset1.getString("FK_NAME"));

				}
				resultset1.close();
*/
			}
			resultset.close();

/*          resultset=dbmd.getColumns(null,null,"albaranes","%");

			resultset.next();
*/
/*
			resultset=dbmd.getIndexInfo(null,null,"lineas",false,false);
			while(resultset.next())
			{
				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("INDEX_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("ORDINAL_POSITION"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("ASC_OR_DESC"));
				System.out.print(" ");
				System.out.println(resultset.getString("CARDINALITY"));
			}
			resultset.close();
*/
/*            resultset=dbmd.getColumns(null,null,"clientes","%");

			while(resultset.next())
			{
				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("COLUMN_NAME"));
//                System.out.print(" ");
//                System.out.print(resultset.getString("DATA_TYPE"));
//                System.out.print(" ");
//                System.out.println(resultset.getString("COLUMN_DEF"));
			}
			resultset.close();
*/
/*
			resultset.next();

				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("DATA_TYPE"));
			resultset.next();

				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("DATA_TYPE"));
			resultset.first();
				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("DATA_TYPE"));
			resultset.next();
				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("DATA_TYPE"));
			resultset.last();
				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("DATA_TYPE"));
			resultset.previous();
				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("DATA_TYPE"));
*/
/*
			while(resultset.next())
			{
				System.out.print(resultset.getString("TABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("DATA_TYPE"));
			}
			resultset.close();

			resultset=dbmd.getPrimaryKeys(null,null,"unidades");

			while(resultset.next())
			{
				System.out.print(resultset.getString("PK_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("COLUMN_NAME"));
				System.out.print(" ");
				System.out.println(resultset.getString("KEY_SEQ"));
			}
			resultset.close();
*/
/*
			resultset=dbmd.getCrossReference(
							null,
							null,
							null,
							null,
							null,
							"albaranes"
							);

			while(resultset.next())
			{
				System.out.print(resultset.getString("PKTABLE_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("PKCOLUMN_NAME"));
				System.out.print(" ");
				System.out.print(resultset.getString("KEY_SEQ"));
				System.out.print(" ");
				System.out.print(resultset.getShort("UPDATE_RULE"));
				System.out.print(" ");
				System.out.print(resultset.getShort("DELETE_RULE"));
				System.out.print(" ");
				System.out.println(resultset.getString("FK_NAME"));

//                System.out.print(" ");
//                System.out.println(resultset.getString("PK_NAME"));
			}
			resultset.close();
*/
			con.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex.toString());
		}
		System.exit(0);
	}
}
