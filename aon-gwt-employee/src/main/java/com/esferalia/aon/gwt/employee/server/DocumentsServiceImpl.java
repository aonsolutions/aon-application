package com.esferalia.aon.gwt.employee.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.CATEGORY;
import static com.esferalia.aon.payroll.sql.SQLConstants.RATTACH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.employee.client.DocumentsService;
import com.esferalia.aon.gwt.employee.shared.Document;
import com.esferalia.aon.payroll.sql.SQLConstants.CategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;

@SuppressWarnings("serial")
public class DocumentsServiceImpl extends AonRemoteServiceServlet implements DocumentsService {

	@Override
	public List<Document> getEnterpriseDocuments() {
		try {
			Integer registryID = getEnterpriseID();
			Connection connection = getConnection();
			return getRegistryDocuments(registryID, connection);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static List<Document> getRegistryDocuments(Integer registryID, Connection connection)
			throws SQLException {
		
		ResultSet rs = null;
		PreparedStatement stmt = null;

		
		try {

			String sql = "SELECT * " + 
						 " FROM " + RATTACH +
						 " , " + CATEGORY + 
						 " WHERE " + RATTACH + "." + RattachColumns.REGISTRY + " = ? " +
						 " AND " +  RATTACH + "." + RattachColumns.CATEGORY + " = " + CATEGORY + "." + CategoryColumns.ID ;

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, registryID);
			rs = stmt.executeQuery();

			List<Document> documents = 
					new LinkedList<Document>();
			
			while (rs.next()) {
				Document document = new Document();
				
				document.setId(rs.getInt(RATTACH + "." + RattachColumns.ID));
				document.setDate(rs.getDate(RATTACH + "." + RattachColumns.ATTACH_DATE));
				document.setCategory(rs.getString(CATEGORY+ "." + CategoryColumns.NAME));
				
				Integer mimeTypeValue = 
						( Integer ) rs.getObject(RATTACH + "." + RattachColumns.MIMETYPE);
				
				if ( mimeTypeValue != null ) {
					document.setMimeType(getMimeTypeOf(mimeTypeValue));
				}
				
				document.setDescription(rs.getString(RATTACH + "." + RattachColumns.DESCRIPTION));
				
				documents.add(document);
			}

			return documents;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}

	}
	
	
	private static String getMimeTypeOf(int value) {
		MimeType mimeTypes [] = MimeType.values();
		if ( value < 0 || value >= mimeTypes.length ){
			return null;
		}
		return mimeTypes[value].getName();
	}
}
