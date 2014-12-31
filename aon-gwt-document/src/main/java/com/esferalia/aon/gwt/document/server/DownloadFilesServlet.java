package com.esferalia.aon.gwt.document.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.Utils;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.gwt.document.jooq.DBConsults;
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
        String domain = AonUtil.getDomainName();
        Integer m = Integer.parseInt(mtype);
        String mimetype = MimeType.values()[m].getName();
        FileInfo fi=null;
        if (driveId != ""){
        	Drive d = null;
        	if(isDrive.equals("true")){
        		d = GoogleDriveController.dconnection;
        	}
        	else{
        		DomainGserviceaccount g;
				try {
					g = DatabaseSync.getServiceAccount(domain);
					d = DriveUtils.serviceInitialize(g);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}
        	}
			com.google.api.services.drive.model.File f = d.files().get(driveId).execute();
			InputStream in = DriveUtils.downloadFile(d, f);
			fi = new FileInfo();
			byte[] b = Utils.InputStreamToByte(in);
		    fi.setData(b);
		    fi.setTitle(f.getTitle());
        	
        }
        else if(fileId!=""){
        	Integer id = Integer.parseInt(fileId);
            try {
				fi = DBConsults.getDataAndName(id,domain);
			} catch (SQLException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}
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
