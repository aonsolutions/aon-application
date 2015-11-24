package com.esferalia.aon.gwt.document.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
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
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DBDrive;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;

public class DownloadFilesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String driveId = p_request.getParameter("drive_id");
        String fileId = p_request.getParameter("file_id");
        String mtype = p_request.getParameter("mimetype");
        String isDrive = p_request.getParameter("isdrive");
        String multiple = p_request.getParameter("ismultiple");
        String domainId =  p_request.getParameter("domain_id");
        
        String domain = AonUtil.getDomainName();
        String login = AonServletUtils.getLoggedUser();
        Integer idFile = Integer.parseInt(fileId);
        Integer domainID = 0;
        if(domainId.equals("null") || domainId.equals("undefined")){
        	try {
				domainID = DBDrive.getRAttachDomainID(domain, idFile);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        else domainID = Integer.parseInt(domainId);
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
        				Drive d = null;
        				DomainGserviceaccount g = null;
        				if (fi2.getIsDrive()) {
        					d = GoogleDriveController.dconnection;
        				} else {
        					try {
        						g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain, domainID);
        						d = DriveUtils.serviceInitialize(g);
        					} catch (SQLException e) {
        						e.printStackTrace();
        					} catch (KeyStoreException e) {
        						e.printStackTrace();
        					} catch (GeneralSecurityException e) {
        						e.printStackTrace();
        					}
        				}
        				com.google.api.services.drive.model.File f = null;
						try {
							f = DriveUtils.getFile(d, fi2.getDriveId(), fi2.getFileId());
							if(f.getDescription().equals("OLDRIVE"))
								d = DriveUtils.serviceInitializeOld(g);
						} catch (SQLException | GeneralSecurityException e) {
							e.printStackTrace();
						}
        				InputStream in = DriveUtils.downloadFile(d, f);
        				byte[] b = com.code.aon.google.apis.Utils
        						.InputStreamToByte(in);
        				fi2.setData(b);
        			} else if ((Integer) fi2.getFileId() != null) {
        				com.code.aon.google.apis.FileInfo fi3 = DBConsults
        						.getDataAndName(new Domain().setName(domain),new User().setLogin(login) ,fi2.getFileId());
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
        	Drive d = null;
        	DomainGserviceaccount g = null;
        	if(isDrive.equals("true")){
        		d = GoogleDriveController.dconnection;
        	}
        	else{
				try {
					g = com.code.aon.google.apis.jooq.DBConsults.getServiceAccount(domain, domainID);
					d = DriveUtils.serviceInitialize(g);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}
        	}
			com.google.api.services.drive.model.File f = null;
			try {
				f = DriveUtils.getFile(d, driveId, idFile);
				if(f.getDescription().equals("OLDRIVE"))
					d = DriveUtils.serviceInitializeOld(g);
			} catch (SQLException | GeneralSecurityException e) {
				e.printStackTrace();
			}

			InputStream in = DriveUtils.downloadFile(d, f);
			fi = new FileInfo();
			byte[] b = Utils.InputStreamToByte(in);
		    fi.setData(b);
		    fi.setTitle(f.getTitle());
        	
        }
        else if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
			fi = DBConsults.getDataAndName(new Domain().setName(domain), new User().setLogin(login), id);
        }
        else return;
        
        File file=Utils.InputStreamToFile(fi) ; /* however you choose to go about resolvingfilename */

        long length = file.length();
        FileInputStream fis = new FileInputStream(file);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"."+MimeType.values()[m].getExtension()+"\"");
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
    }}
