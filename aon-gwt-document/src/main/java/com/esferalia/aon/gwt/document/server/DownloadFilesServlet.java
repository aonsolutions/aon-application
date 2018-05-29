package com.esferalia.aon.gwt.document.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.DBDrive;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class DownloadFilesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String driveId = p_request.getParameter("drive_id");
        String fileId = p_request.getParameter("file_id");
        String title = p_request.getParameter("title");
        String mtype = p_request.getParameter("mimetype");
        String isDrive = p_request.getParameter("isdrive");
        String multiple = p_request.getParameter("ismultiple");
        String domainId =  p_request.getParameter("domain_id");
        String domainName = AonUtil.getDomainName();
        String login = AonServletUtils.getLoggedUser();
        Integer idFile = Integer.parseInt(fileId);
        Integer domainID = 0;
        Domain domain = new Domain().setName(domainName).setId(domainID);
        if(domainId.equals("null") || domainId.equals("undefined")){
        	domainID = DBDrive.getRAttachDomainID(domain, idFile);
        }
        else domainID = Integer.parseInt(domainId);
       
       domain = AON.getDomain(domainName, domainID, login);
       
       User user = new User().setLogin(login);

        Integer m = Integer.parseInt(mtype);
        String mimetype = MimeType.values()[m].getName();
        FileInfo fi=null;
        if(multiple.equals("true")){
        	Vector<com.esferalia.aon.gwt.document.shared.FileInfo> fvector = DocumentsServlet.getDown();
        	ZipOutputStream zos;
        	try {
        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        		zos = new ZipOutputStream(baos);
        		for (com.esferalia.aon.gwt.document.shared.FileInfo fi2 : fvector) {
        			if (fi2.getDriveId() != null) {
        				byte[] b;
        				if (fi2.getIsDrive()) {
        					Drive drive = GoogleDriveController.dconnection;
        					com.google.api.services.drive.model.File file =
        							DriveUtils.getFile(drive, fi2.getDriveId());
        					InputStream in = DriveUtils.downloadFile(drive, file);
        	    			b = AonIOUtils.toByteArray(in);
        				} else {
        					b = DriveUtils.getByteFile(domain, user, fi2.getDriveId(), fi2.getFileId());
        				}
        				fi2.setData(b);
        			} else if ((Integer) fi2.getFileId() != null) {
        				com.code.aon.google.apis.FileInfo fi3 = DBConsults
        						.getDataAndName(domain, user,fi2.getFileId());
        				fi2.setData(fi3.getData());
        			}
        			zos.putNextEntry(new ZipEntry(fi2.getTitle()
        					+ "."
        					+ MimeType.values()[fi2.getMimetype()]
        							.getExtension()));
        			zos.write(fi2.getData());
        			zos.closeEntry();
        		}
        		zos.close();
        		fi = new FileInfo();
        		fi.setData(baos.toByteArray());
        		fi.setTitle("descarga");
        		fi.setMimetype((byte) MimeType.MIME_ZIP.ordinal());	
        		m=MimeType.MIME_ZIP.ordinal();
        		mimetype = MimeType.values()[fi.getMimetype()].getName();
        	}catch (FileNotFoundException e1) {
        		e1.printStackTrace();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        	
        }
        else if (driveId != ""){
        	byte[] b;
        	if(isDrive.equals("true")){
        		Drive drive = GoogleDriveController.dconnection;
				com.google.api.services.drive.model.File file =
						DriveUtils.getFile(drive, driveId);
				InputStream in = DriveUtils.downloadFile(drive, file);
    			b = AonIOUtils.toByteArray(in);
        	}
        	else{
        		b = DriveUtils.getByteFile(domain, user, driveId, idFile);
        		if(b == null) {
            		DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
            		Drive drive = AonDrive.getInstace().serviceInitialize(g);
            		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(idFile), AttachType.REGISTRY);
            		DriveUtils.getInstace().syncX(drive, user, attach);
            		b = DriveUtils.getByteFile(domain, user, driveId, idFile);
            	}
        	}
        	
			fi = new FileInfo();
		    fi.setData(b);
		    fi.setTitle(title);	
        }
        else if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
			fi = DBConsults.getDataAndName(domain, user, id);
        }
        else return;
      
        Integer length = fi.getData().length;

        ByteArrayInputStream bais = new ByteArrayInputStream(fi.getData());
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + fi.getTitle() +"."+MimeType.values()[m].getExtension()+"\"");
        //p_response.setContentType("application/octet-stream");
        p_response.setContentType(mimetype);

        if (length > 0 && length <= Integer.MAX_VALUE)
            p_response.setContentLength((int)length);
        ServletOutputStream out = p_response.getOutputStream();
        p_response.setBufferSize(32768);
        int bufSize = p_response.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        
        bis.close();
        bais.close();
        out.flush();
        out.close();
    }}
