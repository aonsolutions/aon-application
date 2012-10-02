package com.code.aon.ui.report.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;

import com.code.aon.report.ReportException;

public class ReportExporter {

	public static final int EXCEL = 0;
	private IExporterCallBack callBack;
	
	public ReportExporter() {
		
	}

	public ReportExporter(IExporterCallBack callBack) {
		this.callBack = callBack;	
	}
	
	public void run2Excel(PreparedStatement ps, OutputStream out) throws ReportException {
		run(ps, out, EXCEL);
	}
	
	public void run2Text(PreparedStatement ps, OutputStream out, String separator) throws ReportException {
		throw new UnsupportedOperationException("Not yet supported");
	}

	private void run(PreparedStatement ps, OutputStream out, int type) throws ReportException {
		
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			ReportMetadata metadata = new ReportMetadata();
			metadata.initializeMetadata(ps);
			export(out,metadata,rs,type);
		} catch (SQLException e) {
			
			throw new ReportException(e.getMessage(),e);
			
		} finally {
			
			DbUtils.closeQuietly(rs);
		}
		
	}

	private void export(OutputStream out,ReportMetadata metadata, ResultSet rs, int type) throws ReportException {
		try {

			// Todo hacer un factory, si alguna vez hay otro formato.
			IReportExporter exporter = null;
			if (type == 0) {
				exporter = new ExcelReportExporter();	
			}
			exporter.setCallBack(callBack);
			exporter.startExport(out);
			exporter.exportHeader(out,metadata);
			while (rs.next()) {
				exporter.startLine(out);
				for (int i = 1; i < (metadata.getCount() + 1); i++) {
					Object data = rs.getObject(i);
					ReportColumnMetadata columnMetadata = metadata.getColumns().get((i-1));
					exporter.exportColumn(out,columnMetadata,data);		
				}
				exporter.endLine(out);
			}
			exporter.endExport(out);
		} catch (SQLException e) {
			
			throw new ReportException(e.getMessage(),e);
			
		}
	}


	public static void main(String[] args) throws SQLException, ClassNotFoundException, ReportException, IOException {
		Class.forName("org.gjt.mm.mysql.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://127.0.1.1/aon_master","dbuser","serubd2000");
		String select = "SELECT" 
		+" r.id Código"
		+",ELT(c.status+1,'ACTIVE','INACTIVE','BLOCKED') Status"
		+",ELT(r.type+1,'NATURAL','LEGAL') Tipo"
		+",CAST( CONCAT_WS('/',r.document_type,r.document_country,r.document) AS CHAR) Documento"
		+",r.name Nombre"
		+",r.alias `Nombre Comercial`"
		+",r.nationality Nacionalidad"
		+",CAST( CONCAT_WS(' ',ra.street_type,ra.address,ra.number,ra.address2,ra.address3) AS CHAR) Dirección"
		+",ra.city `Ciudad`"
		+",ra.zip `C.P.`"
		+",gz.code `C.Prv.`"
		+",gz.name `Provincia`"
		+",ra.recipient `Per. Contacto`"
		+",(SELECT rm1.value FROM rmedia rm1 WHERE r.id = rm1.registry  AND rm1.media = 1 LIMIT 1) `Teléfono`"
		+",(SELECT rm2.value FROM rmedia rm2 WHERE r.id = rm2.registry  AND rm2.media = 2 LIMIT 1) `Movil`"
		+",(SELECT rm3.value FROM rmedia rm3 WHERE r.id = rm3.registry  AND rm3.media = 3 LIMIT 1) `Fax`"
		+",(SELECT rm4.value FROM rmedia rm4 WHERE r.id = rm4.registry  AND rm4.media = 4 LIMIT 1) `eMail`"
		+",(SELECT rm5.value FROM rmedia rm5 WHERE r.id = rm5.registry  AND rm5.media = 5 LIMIT 1) `Web`"
		+",b.name Banco"
		+",rb.bank_account 'Cuenta banco'"
		+",rpm.number_of_pymnts 'Nº Vtos.'"
		+",rpm.days_to_first_pymnt 'Dias al 1ª Vto.'"
		+",rpm.days_between_pymnts 'Dias entre Vtos.'"
		+",rpm.pymnt_days 'Dias Pago'"
		+" FROM registry r"
		+" INNER JOIN customer c ON r.id = c.registry"
		+" LEFT OUTER JOIN raddress ra ON r.id = ra.registry AND ra.type = 0"
		+" LEFT OUTER JOIN geozone gz ON ra.geozone = gz.id"
		+" LEFT OUTER JOIN rpaymethod rpm ON rpm.registry = r.id"
		+" LEFT OUTER JOIN rbank rb ON rpm.rbank = rb.id"
		+" LEFT OUTER JOIN bank b ON rb.bank = b.id";
		PreparedStatement ps = c.prepareStatement(select);
		ReportExporter rm = new ReportExporter();
		File file = new File("/tmp/excel.xls"); 
		FileOutputStream out = new FileOutputStream(file);
		rm.run2Excel(ps, out);
		out.flush();
		out.close();
	}
}

