package com.code.aon.fiscal.aeat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.FileUtils;

public class TraspasoModulos390_2013 {
	
	private static final String SOURCE_URL = "jdbc:derby:jar:(/home/ecastellano/TRABAJO/AEAT/IVA2013/CloudScape/Modulos.zip)Modulos";
	
	private static final String TARGET_FILE = "/home/ecastellano/trunk/trunk/aon-gwt-fiscal/src/main/java/com/esferalia/aon/gwt/fiscal/server/Activities.java";
	
	public static void main(String[] args) throws SQLException, IOException {
		TraspasoModulos390_2013 traspa = new TraspasoModulos390_2013();
		traspa.start();
	}

	private void start() throws IOException  {
		Connection  sourceConnection = null;
		Connection targetConnection = null;
		try {
			File targetFile = new File(TARGET_FILE);
			if (targetFile.exists()) {
				System.out.println("Directorio de Base de datos de destino existe");
				System.out.println("Se borra ... ");
				FileUtils.deleteQuietly(targetFile);
				System.out.println("OK!");
			}
			PrintWriter writer = new PrintWriter(targetFile);
			sourceConnection = DriverManager.getConnection(SOURCE_URL);
			trasEpigraph( sourceConnection, writer);
			writer.flush();
			writer.close();
			System.out.println("[END]");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(targetConnection);
			DbUtils.closeQuietly(sourceConnection);
		}
		
	}
	
	private void trasEpigraph(Connection source, Writer writer) throws SQLException, IOException {
		String SELECT_EPIGRAFE = "SELECT" 
				+ " COD_ACT,EPIGRAFE,LITERAL,ID_GRUPO" 
				+ " FROM epigrafe ";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuilder builder = null;
		try {
			writer.write("package com.esferalia.aon.gwt.fiscal.server;");
			writer.write('\n');
			writer.write('\n');
			writer.write("public enum Activities {");
			writer.write('\n');
			
			stmt = source.prepareStatement(SELECT_EPIGRAFE);
			rs = stmt.executeQuery();
			int i = 0;
			while (rs.next()) {
				String act = rs.getString(1);
				String epi = rs.getString(2);
				String lit = rs.getString(3);
				double group = rs.getDouble(4);
				String grp = Integer.toString( (int) group); 
				if (rs.wasNull()) {
					grp = null;
				}
				builder = new StringBuilder();
				builder.append('\t');
				builder.append((i>0)?',':' ');	
				builder.append('E');
				builder.append(act);
				builder.append('_');
				builder.append(epi);
				builder.append(" (\"");
				builder.append(act);
				builder.append("\",\"");
				builder.append(epi);
				builder.append("\",\"");
				builder.append(lit);
				builder.append("\",\"");
				builder.append(grp);
				builder.append("\")");
				builder.append('\n');
				i++;
				writer.write(builder.toString());
			}
			writer.write('\t');
			writer.write(";");
			writer.write('\n');
			writer.write('\n');
			writer.write('\t');
			writer.write("private String activity;");
			writer.write('\n');
			writer.write('\t');
			writer.write("private String epigraph;");
			writer.write('\n');
			writer.write('\t');
			writer.write("private String literal;");
			writer.write('\n');
			writer.write('\t');
			writer.write("private String group;");
			writer.write('\n');
			writer.write('\n');
			writer.write('\t');
			writer.write("private Activities(String act,String epi, String lit, String grp) {");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("this.activity = act;");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("this.epigraph = epi;");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("this.literal = lit;");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("this.group = grp;");
			writer.write('\n');
			writer.write('\t');
			writer.write("}");
			writer.write('\n');
			writer.write('\n');
			writer.write('\t');
			writer.write("public String getActivity() {");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("return activity;");
			writer.write('\n');
			writer.write('\t');
			writer.write("}");
			writer.write('\n');
			writer.write('\t');
			writer.write("public String getEpigraph() {");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("return epigraph;");
			writer.write('\n');
			writer.write('\t');
			writer.write("}");
			writer.write('\n');
			writer.write('\t');
			writer.write("public String getLiteral() {");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("return literal;");
			writer.write('\n');
			writer.write('\t');
			writer.write("}");
			writer.write('\n');
			writer.write('\t');
			writer.write("public String getGroup() {");
			writer.write('\n');
			writer.write('\t');
			writer.write('\t');
			writer.write("return group;");
			writer.write('\n');
			writer.write('\t');
			writer.write("}");
			writer.write('\n');
			writer.write("}");
			
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
		}
	}

}



