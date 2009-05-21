package com.code.aon.accounting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.StringTokenizer;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class Generator {

	public static void main(String[] args) throws IOException,
			ManagerBeanException, ClassNotFoundException {

		Class.forName( "com.mysql.jdbc.Driver" );
		
		Connection c = null;
		PreparedStatement ps = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("INSERT INTO balance_detail (balance,code, description, accounts, sortKey) VALUES (2,?,?,?,?)");
			//c = DriverManager.getConnection("jdbc:mysql://192.168.2.4:3306/aon-asesor?autoReconnect=true","dbuser","serubd2000");
			c= HibernateUtil.getSQLConnection();
			ps = c.prepareStatement(stmt.toString());
			int j = 1;
			File file = new File("/tmp/tablapgc08_pyg.txt");
			InputStream in = new FileInputStream(file);
			Reader reader = new InputStreamReader(in);
			LineNumberReader lnr = new LineNumberReader(reader);
			while (lnr.ready()) {
				String line = lnr.readLine();
				System.out.println(line);
				String[] data = new String[3];
				
				int i = 0;
				StringTokenizer tokenizer = new StringTokenizer(line, "|");
				while (tokenizer.hasMoreTokens()) {
					data[i] = tokenizer.nextToken();
					System.out.println("-----" + data[i].trim());
					++i;
				}
					if (data[0] != null) {
						System.out.println(data[0]);
						ps.setString(1, new String(data[0]));
					} else {
						ps.setNull(1, Types.CHAR);
					}

					if (data[1] != null) {
						System.out.println(data[1]);
						ps.setString(2, new String(data[1]));
					} else {
						ps.setNull(2, Types.CHAR);
					}

					if (data[2] != null) {
						System.out.println(data[2]);
						ps.setString(3, new String(data[2]));
					} else {
						ps.setNull(3, Types.CHAR);
					}
										
						ps.setInt(4, j);
					 		
					 		
					System.out.println(ps.executeUpdate());
					j++;
				}
				
			lnr.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}

			}
			try {
				c.close();
			} catch (Exception e) {
			}
		}
	}
}
