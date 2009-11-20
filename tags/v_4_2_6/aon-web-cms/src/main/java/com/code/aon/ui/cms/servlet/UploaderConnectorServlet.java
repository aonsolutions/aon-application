/* 
* FCKeditor - The text editor for internet 
* Copyright (C) 2003-2005 Frederico Caldeira Knabben 
*  
* Licensed under the terms of the GNU Lesser General Public License: 
*              http://www.opensource.org/licenses/lgpl-license.php 
*  
* For further information visit: 
*              http://www.fckeditor.net/ 
*  
* File Name: SimpleUploaderServlet.java 
*      Java File Uploader class. 
*  
* Version:  2.3 
* Modified: 2005-08-11 16:29:00 
*  
* File Authors: 
*              Simone Chiaretta (simo@users.sourceforge.net) 
*/  

package com.code.aon.ui.cms.servlet; 

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;


/** 
* Servlet to upload files.<br> 
* 
* This servlet accepts just file uploads, eventually with a parameter specifying file type 
* 
* @author Simone Chiaretta (simo@users.sourceforge.net) 
*/ 

public class UploaderConnectorServlet extends HttpServlet { 
         
	private static final Logger LOGGER = Logger.getLogger(UploaderConnectorServlet.class.getName());
	
	private static String baseDir; 
    private static boolean debug; 
    private static boolean enabled; 
    private static Hashtable allowedExtensions; 
    private static Hashtable deniedExtensions; 
         
        /** 
         * Initialize the servlet.<br> 
         * Retrieve from the servlet configuration the "baseDir" which is the root of the file repository:<br> 
         * If not specified the value of "/UserFiles/" will be used.<br> 
         * Also it retrieve all allowed and denied extensions to be handled. 
         * 
         */ 
         public void init() throws ServletException { 
                LOGGER.info("------------------------ UPLOAD SERVLET -------------------------");
                debug=(new Boolean(getInitParameter("debug"))).booleanValue(); 
                 
                if(debug) LOGGER.info("\r\n---- SimpleUploaderServlet initialization started ----"); 
                 
                baseDir=getInitParameter("baseDir"); 
                enabled=(new Boolean(getInitParameter("enabled"))).booleanValue(); 
                if(baseDir==null) 
                        baseDir="/UserFiles/"; 
                String realBaseDir=getServletContext().getRealPath(baseDir); 
                File baseFile=new File(realBaseDir); 
                if(!baseFile.exists()){ 
                        baseFile.mkdir(); 
                } 
                 
                allowedExtensions = new Hashtable(3); 
                deniedExtensions = new Hashtable(3); 
                                 
                allowedExtensions.put("File",stringToArrayList(getInitParameter("AllowedExtensionsFile"))); 
                deniedExtensions.put("File",stringToArrayList(getInitParameter("DeniedExtensionsFile"))); 
 
                allowedExtensions.put("Image",stringToArrayList(getInitParameter("AllowedExtensionsImage"))); 
                deniedExtensions.put("Image",stringToArrayList(getInitParameter("DeniedExtensionsImage"))); 
                 
                allowedExtensions.put("Flash",stringToArrayList(getInitParameter("AllowedExtensionsFlash"))); 
                deniedExtensions.put("Flash",stringToArrayList(getInitParameter("DeniedExtensionsFlash"))); 
                 
                if(debug) LOGGER.info("---- SimpleUploaderServlet initialization completed ----\r\n"); 
                 
        } 
         

         @Override
         protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         	doPost(req, resp);
         }

        /** 
         * Manage the Upload requests.<br> 
         * 
         * The servlet accepts commands sent in the following format:<br> 
         * simpleUploader?Type=ResourceType<br><br> 
         * It store the file (renaming it in case a file with the same name exists) and then return an HTML file 
         * with a javascript command in it. 
         * 
         */      
        public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
            LOGGER.info("------------------------ UPLOAD SERVLET -------------------------");

                if (debug) LOGGER.info("--- BEGIN DOPOST ---"); 
 
                response.setContentType("text/html; charset=UTF-8"); 
                response.setHeader("Cache-Control","no-cache"); 
                PrintWriter out = response.getWriter(); 
                 
 
                String typeStr=request.getParameter("Type"); 
                 
                String currentPath=baseDir+typeStr; 
                String currentDirPath=getServletContext().getRealPath(currentPath); 
                currentPath=request.getContextPath()+currentPath; 
                 
                if (debug) LOGGER.info(currentDirPath); 
                 
                String retVal="0"; 
                String newName=""; 
                String fileUrl=""; 
                String errorMessage=""; 
                 
                if(enabled) {            
                        DiskFileUpload upload = new DiskFileUpload(); 
                        try { 
                                List items = upload.parseRequest(request); 
                                 
                                Map fields=new HashMap(); 
                                 
                                Iterator iter = items.iterator(); 
                                while (iter.hasNext()) { 
                                    FileItem item = (FileItem) iter.next(); 
                                    if (item.isFormField()) 
                                        fields.put(item.getFieldName(),item.getString()); 
                                    else 
                                        fields.put(item.getFieldName(),item); 
                                } 
                                FileItem uplFile=(FileItem)fields.get("NewFile"); 
                                String fileNameLong=uplFile.getName(); 
                                fileNameLong=fileNameLong.replace('\\','/'); 
                                String[] pathParts=fileNameLong.split("/"); 
                                String fileName=pathParts[pathParts.length-1]; 
                                 
                                String nameWithoutExt=getNameWithoutExtension(fileName); 
                                String ext=getExtension(fileName); 
                                File pathToSave=new File(currentDirPath,fileName); 
                                fileUrl=currentPath+"/"+fileName; 
                                if(extIsAllowed(typeStr,ext)) { 
                                        int counter=1; 
                                        while(pathToSave.exists()){ 
                                                newName=nameWithoutExt+"("+counter+")"+"."+ext; 
                                                fileUrl=currentPath+"/"+newName; 
                                                retVal="201"; 
                                                pathToSave=new File(currentDirPath,newName); 
                                                counter++; 
                                                } 
                                        uplFile.write(pathToSave); 
                                } 
                                else { 
                                        retVal="202"; 
                                        errorMessage=""; 
                                        if (debug) LOGGER.info("Invalid file type: " + ext);      
                                } 
                        }catch (Throwable th) { 
                                if (debug) {
                                	LOGGER.log(Level.SEVERE, th.getMessage(), th); 
                                }
                                retVal="203"; 
                        } 
                } 
                else { 
                        retVal="1"; 
                        errorMessage="This file uploader is disabled. Please check the WEB-INF/web.xml file"; 
                } 
                 
                 
                out.println("<script type=\"text/javascript\">"); 
                out.println("window.parent.OnUploadCompleted("+retVal+",'"+fileUrl+"','"+newName+"','"+errorMessage+"');"); 
                out.println("</script>"); 
                out.flush(); 
                out.close(); 
         
                if (debug) LOGGER.info("--- END DOPOST ---");     
                 
        } 
 
 
        /* 
         * This method was fixed after Kris Barnhoorn (kurioskronic) submitted SF bug #991489 
         */ 
        private static String getNameWithoutExtension(String fileName) { 
                return fileName.substring(0, fileName.lastIndexOf(".")); 
        } 
         
        /* 
         * This method was fixed after Kris Barnhoorn (kurioskronic) submitted SF bug #991489 
         */ 
        private String getExtension(String fileName) { 
                return fileName.substring(fileName.lastIndexOf(".")+1); 
        } 
 
 
 
        /** 
         * Helper function to convert the configuration string to an ArrayList. 
         */ 
          
         private ArrayList stringToArrayList(String str) { 
          
         if(debug) LOGGER.info(str); 
         String[] strArr=str.split("\\|"); 
                  
         ArrayList tmp=new ArrayList(); 
         if(str.length()>0) { 
                 for(int i=0;i<strArr.length;++i) { 
                                if(debug) LOGGER.info(i +" - "+strArr[i]); 
                                tmp.add(strArr[i].toLowerCase()); 
                        } 
                } 
                return tmp; 
         } 
 
 
        /** 
         * Helper function to verify if a file extension is allowed or not allowed. 
         */ 
          
         private boolean extIsAllowed(String fileType, String ext) { 
                         
                        ext=ext.toLowerCase(); 
                         
                        ArrayList allowList=(ArrayList)allowedExtensions.get(fileType); 
                        ArrayList denyList=(ArrayList)deniedExtensions.get(fileType); 
                         
                        if(allowList.size()==0) 
                                if(denyList.contains(ext)) 
                                        return false; 
                                else 
                                        return true; 
 
                        if(denyList.size()==0) 
                                if(allowList.contains(ext)) 
                                        return true; 
                                else 
                                        return false; 
          
                        return false; 
         } 
 
} 
