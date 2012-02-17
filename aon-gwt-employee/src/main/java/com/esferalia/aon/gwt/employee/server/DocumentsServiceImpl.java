package com.esferalia.aon.gwt.employee.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.CATEGORY;
import static com.esferalia.aon.payroll.sql.SQLConstants.RATTACH;
import static com.esferalia.aon.gwt.employee.server.AonServletUtils.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.employee.client.DocumentsService;
import com.esferalia.aon.gwt.employee.shared.Document;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.CategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

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
	
	@Override
	public String getAsHTML(Document doc, int zoom) {
		IDocument2HtmlConverter converter = 
				getDocument2HtmlConverter(doc);
		try {
			ByteArrayOutputStream os = 
					new ByteArrayOutputStream();
			converter.transform(doc, os, zoom);
			os.flush();
			return os.toString();
		} catch (Exception e) {
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
	
	private IDocument2HtmlConverter getDocument2HtmlConverter(Document document) {
		MimeType mimeType = MimeType.get(document.getMimeType());
		return DOC2HTML_CONVERTERS.get(mimeType);
		
	}
	
	
	private static final Map<MimeType, IDocument2HtmlConverter> DOC2HTML_CONVERTERS = 
			new HashMap<MimeType, DocumentsServiceImpl.IDocument2HtmlConverter>(){
		{
			put(MimeType.MIME_PDF, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_WORD, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_WORD_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_EXCEL, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_EXCEL_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_POWER_POINT, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_MS_POWER_POINT_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MIME_HTML, Noop2HtmlConverter.INSTANCE );
			put(MimeType.MIME_TXT, Noop2HtmlConverter.INSTANCE );
		}
	};
	
	
	private static interface IDocument2HtmlConverter {
		void transform(Document doc, OutputStream os, int zoom) throws Exception;
	}


	private static class OpenDocument2HtmlConverter implements IDocument2HtmlConverter {
		
		private static  IDocument2HtmlConverter INSTANCE = new OpenDocument2HtmlConverter();

		@Override
		public void transform(Document doc, OutputStream os, int zoom) throws Exception {
			
			PrintStream printStream = new PrintStream(os);
			
			PDFFile pdfFile = OpenDocument2ImageServlet.getPDFFile(doc.getId());
			
			for (int page = 1; page <= pdfFile.getNumPages(); page++) {
				
				PDFPage pdfPage = pdfFile.getPage(page);

				// get the width and height for the doc at the default zoom
				double width =  pdfPage.getBBox().getWidth() * zoom / 100 ;
				double height = pdfPage.getBBox().getHeight() * zoom / 100 ;
				
				// TODO : aon_gwt_employee ???
				printStream.printf("<div class='page' style='width:%fpx;height:%fpx;'   ><img src='openDocument2Image/%d.png?%s=%d&%s=%d'></img> </div>",
						width,
						height,
						doc.getId(),
						OpenDocument2ImageServlet.PAGE_PARAM,
						page,
						OpenDocument2ImageServlet.ZOOM_PARAM,
						zoom);
			}		
		}
	}

	private abstract static class Document2HtmlConverter implements IDocument2HtmlConverter{
		@Override
		public void transform(Document doc, OutputStream os, int zoom) throws Exception {
			Connection connection = getConnection();

			ResultSet rs = null;
			PreparedStatement stmt = null;
			try {

				stmt = connection.prepareStatement(
						"SELECT " + RattachColumns.DATA
						+ " FROM " + SQLConstants.RATTACH + " WHERE "
						+ RattachColumns.ID + "= ? ");

				stmt.setInt(1, doc.getId());
				
				rs = stmt.executeQuery();
				
				if (!rs.next()) {
					throw new IllegalArgumentException();
				}

				InputStream is = rs.getBinaryStream(RattachColumns.DATA);
				transform(is, os);
			}
			catch (Exception e ) {
				throw new IllegalArgumentException(e);
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						throw new IllegalArgumentException(e);
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						throw new IllegalArgumentException(e);
					}
				}
			}
		}
		
		
		
		abstract void transform(InputStream is, OutputStream os) throws Exception;
		
	}
	
	
	private static class Noop2HtmlConverter  extends  Document2HtmlConverter  {
		
		private static  IDocument2HtmlConverter INSTANCE = new Noop2HtmlConverter();
		
		
		@Override
		void transform(InputStream is, OutputStream os) throws Exception {
			int read ;
			byte buffer [] = new byte [256];
			while ( ( read = is.read(buffer)) == buffer.length ) {
				os.write(buffer, 0, read);
			}
		}
	}

}

