package com.code.aon.aio.servlet.viewer;

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

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.Utils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;

public class PdfPrintServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_OFFICE_PORT = 2002;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String driveId = p_request.getParameter("drive_id");
        String fileId = p_request.getParameter("file_id");
        String fileTitle = p_request.getParameter("title");
        String mtype = p_request.getParameter("mimetype");
        String domainId = p_request.getParameter("domain_id");
        Integer domainID = Integer.parseInt(domainId);
        String domainName = AonUtil.getDomainName();
        Domain domain = new Domain().setName(domainName).setId(domainID);
        String login = ""; //AonUtil.getRemoteUser();
        User user = new User().setLogin(login);
        Integer m = Integer.parseInt(mtype);
        MimeType mt = MimeType.values()[m];
        Integer idFile = Integer.parseInt(fileId);
        String mimetype = MimeType.values()[m].getName();
        Attach fi = null;
        if (driveId != ""){
        	byte[] b = DriveUtils.getByteFile(domain, user, driveId, idFile);
        	fi = new Attach().setData(b).setDescription(fileTitle);
        }
        else if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
			fi = AON.getAttach(domain.getName(), domain.getId(), login, 
					f -> f.getIdProperty().eq(id)
					, AttachType.REGISTRY);
        }
        else return;
        File file ;
		if (isOffice(mt)) {
			file  = getPdfByeBuffer(fi.getId(),mt,fi.getData());
			mimetype = MimeType.MIME_PDF.getName();
		}
		else file=Utils.InputStreamToFile(fi) ; /* however you choose to go about resolvingfilename */

        long length = file.length();
        FileInputStream fis = new FileInputStream(file);
        
        p_response.addHeader("Content-Disposition","inline; filename=\"" + file.getName() +"\"");
        //p_response.setContentType("application/octet-stream");
        p_response.setContentType(mimetype);

        if (length > 0 && length <= Integer.MAX_VALUE);
            p_response.setContentLength((int)length);
        ServletOutputStream out = p_response.getOutputStream();
        p_response.setBufferSize(32768);
        int bufSize = p_response.getBufferSize();
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
	

	private static File getPdfByeBuffer(Integer id, MimeType mimeType,
			byte[] bytes) throws IOException {



		String tmpDir = System.getProperty("java.io.tmpdir");

		File inputFile = new File(tmpDir, id + "." + mimeType.getExtension());

		FileOutputStream inputFileOs = new FileOutputStream(inputFile);
		inputFileOs.write(bytes);
		inputFileOs.close();

		File outputFile = new File(tmpDir, id + "."
					+ MimeType.MIME_PDF.getExtension());

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
		return m.equals(MimeType.MIME_MS_EXCEL) || m.equals(MimeType.MIME_MS_EXCEL_2007) 
				|| m.equals(MimeType.MIME_MS_POWER_POINT) || m.equals(MimeType.MIME_MS_POWER_POINT_2007)
				|| m.equals(MimeType.MIME_MS_WORD) || m.equals(MimeType.MIME_MS_WORD_2007);

	}
}
