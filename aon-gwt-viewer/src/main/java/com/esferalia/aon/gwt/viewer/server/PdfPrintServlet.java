package com.esferalia.aon.gwt.viewer.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;


public class PdfPrintServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_OFFICE_PORT = 2002;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{

		String driveId = !req.getParameter("drive_id").equals("null") ? req.getParameter("drive_id") : null;
		Integer attachId = Integer.parseInt(req.getParameter("attach_id"));
		AttachType attachType = AttachType.values()[Integer.parseInt(req.getParameter("attach_type"))];
		String attachName = req.getParameter("attach_name");
		MimeType mimeType = MimeType.values()[Integer.parseInt(req.getParameter("mimetype"))];
		String domainName = req.getParameter("domain_name");
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String login = "";
		
		Domain domain = AON.getDomain(domainName, domainId, login);
		Attach attach = new Attach().setDomain(domain)
				.setDescription(attachName)
				.setDriveId(driveId)
				.setId(attachId)
				.setAttachType(attachType)
				.setMimeType(mimeType);
		
		byte[] data = getData(attach, login);
		
		String md5 = AonFileUtils.getMD5Checksum(data);
		
        File file ;
		if (isOffice(mimeType)) {
			file  = getPdfByeBuffer(md5, mimeType, data);
		}
		else file= AonFileUtils.byteToFile( data, attachName); /* however you choose to go about resolvingfilename */

        long length = file.length();
        FileInputStream fis = new FileInputStream(file);
        
        resp.addHeader("Content-Disposition","inline; filename=\"" + file.getName() +"\"");
        //p_response.setContentType("application/octet-stream");
        resp.setContentType(mimeType.getName());

        if (length > 0 && length <= Integer.MAX_VALUE);
            resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(fis,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        
        bis.close();
        fis.close();
        out.flush();
        out.close();
    }
	

	private static File getPdfByeBuffer(String md5, MimeType mimeType,
			byte[] bytes) throws IOException {



		String tmpDir = System.getProperty("java.io.tmpdir");

		File inputFile = new File(tmpDir, md5 + "." + mimeType.getExtension());

		FileOutputStream inputFileOs = new FileOutputStream(inputFile);
		inputFileOs.write(bytes);
		inputFileOs.close();

		File outputFile = new File(tmpDir, md5 + "."
					+ MimeType.PDF.getExtension());

		convert(inputFile, outputFile);

		//RandomAccessFile randomAccessFile = new RandomAccessFile(outputFile,
		//"r");

		//FileChannel fileChannel = randomAccessFile.getChannel();

		return outputFile;//fileChannel.map(MapMode.READ_ONLY, 0, randomAccessFile.length());
	}
	
	protected static void convert(File inputFile, File outputFile) 
			throws IOException {
		
		DocumentFormatRegistry formatRegistry = 
				new DefaultDocumentFormatRegistry();
		
		DefaultOfficeManagerConfiguration configuration = 
				new DefaultOfficeManagerConfiguration();
		// TODO Servlet params ???
		configuration.setPortNumber(DEFAULT_OFFICE_PORT);
		
		OfficeManager officeManager = configuration.buildOfficeManager();
		officeManager.start();
		OfficeDocumentConverter converter = 
				new OfficeDocumentConverter(officeManager, formatRegistry);
		try {
			 converter.convert(inputFile, outputFile);
		}finally {
			officeManager.stop();
		}
	}
	
	private Boolean isOffice(MimeType m) {
		return m.equals(MimeType.MS_EXCEL) || m.equals(MimeType.MS_EXCEL_2007) 
				|| m.equals(MimeType.MS_POWER_POINT) || m.equals(MimeType.MS_POWER_POINT_2007)
				|| m.equals(MimeType.MS_WORD) || m.equals(MimeType.MS_WORD_2007);

	}
	
	
	public byte[] getData(Attach attach, String login){
		byte[] b = "".getBytes();
		if(attach.getDriveId() != null || 
				(attach.getId() != null && attach.getAttachType() != null)){
			Integer attachId = attach.getId();
			if(attach.getDriveId() == null)
				attach = AON.getAttach(attach.getDomain().getName(), attach.getDomain().getId(), login,
						f-> f.getIdProperty().eq(attachId), attach.getAttachType());
			if(attach.getDriveId() != null){
				b = DriveUtils.getByteFile(attach, new User().setLogin(login));
			} else if(attach.getData() != null) b = attach.getData();
		}
		return b;
	}
}
