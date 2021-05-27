package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;

public class MysqlSync extends DefaultMysqlDB {

	private File imagesDir;

	public MysqlSync(Connection mysqlConnection,File imagesDir) throws SQLException {
		super(mysqlConnection);
		this.imagesDir = imagesDir;
	}

	public void sync(CtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visit(new EnterpriseSync());
	}

	private class EnterpriseSync extends DefaultCtsqlDBVisitor {


		@Override
		public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
			ctsqlDB.visitEmprnif(this);
		}

		@Override
		public void visitEmprnif(Emprnif emprnif) throws SQLException {

			emprnif.visitEmpract_emprnif(this);
		}

		@Override
		public void visitEmpract_emprnif(Empract empract, Emprnif emprnif)
				throws SQLException {
			Enterprise enterprise = getEnterprise(emprnif);
			syncImage(enterprise, empract, RegistryAttachmentType.LOGO, "L");
			syncImage(enterprise, empract, RegistryAttachmentType.SIGNATURE, "F");		
		}
		
		private Enterprise getEnterprise(Emprnif emprnif) throws SQLException {
			ResultSet rs = null;
			PreparedStatement stmt = null;
			
			try {
				stmt = mysqlConnection
						.prepareStatement("SELECT * "
								+ " FROM registry, enterprise"
								+ " WHERE registry.id = enterprise.registry " 
								+ " AND document= ? "
								+ " "); // TODO : Add domain here
				stmt.setString(1, emprnif.getNumdoc() );
				rs = stmt.executeQuery();
				if (rs.next()) {
					Enterprise enterprise = new Enterprise();
					enterprise.registry = rs.getInt("registry");
					enterprise.scope = rs.getInt("scope");
					enterprise.domain = rs.getInt("domain");
					return enterprise;
				} else {
					return null;
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		}

		private void deleteRattach(Rattach rattach) throws SQLException {
			ResultSet rs = null;
			PreparedStatement stmt = null;
			
			try {
				stmt = mysqlConnection
						.prepareStatement("DELETE "
								+ " FROM rattach"
								+ " WHERE registry = ? " 
								+ " AND  type = ? "
								+ " AND domain = ? "); 
				stmt.setInt(1, rattach.registry );
				stmt.setShort(2, rattach.type);
				stmt.setInt(3, rattach.domain);
				
				stmt.execute();
				
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		}

		private void syncImage(Enterprise enterprise, Empract empract,
				RegistryAttachmentType type, String preffix)
				throws SQLException {

			if (enterprise == null || imagesDir == null || !imagesDir.exists()) {
				return;
			}

			File file = new File(imagesDir, String.format("%s%s.bmp", preffix,
					empract.getCdg()));
			if (!file.exists()) {
				return;
			}

			String description = null;
			try {
				int length = (int) file.length();
				byte bytes[] = new byte[length];
				InputStream is = new FileInputStream(file);
				is.read(bytes);

				Blob blob = new SerialBlob(bytes);
				Rattach rattach = new Rattach();
				rattach.registry = enterprise.registry;
				rattach.domain = enterprise.domain;
				rattach.mimeType = DefaultMysqlDB.enum2short(MimeType.MIME_BMP);
				rattach.data = blob;
				rattach.description = description;
				rattach.type = DefaultMysqlDB.enum2short(type);
				rattach.scope = enterprise.scope;
				rattach.security_level = 0;
				
				deleteRattach(rattach);
				
				List<Rattach> rattachs = new LinkedList<Rattach>();
				rattachs.add(rattach);
				MysqlSync.this.insertRattach(rattachs);

				is.close();
			} catch (IOException e) {
			}
		}
	}

}
