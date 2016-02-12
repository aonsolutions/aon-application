package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;

@WebServlet(name = "DownloadXml", urlPatterns = { "/aon_gwt_deposit/gwt_download_deposit/*" })
public class DownloadXmlFileServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest p_request,
			HttpServletResponse p_response) throws ServletException,
			IOException {
		
		p_response.addHeader("Content-Disposition",
				"inline; filename=\"DEPOSITO.zip\"");
		p_response.setContentType(MimeType.ZIP.getName());

		String domain_id = p_request.getParameter("domain_id");
		Integer domainId = Integer.parseInt(domain_id);
		String domain = AonServletUtils.getRequestDomainName(p_request);
		
		String yearStr = p_request.getParameter("year");
		Integer year = Integer.parseInt(yearStr);
		
		File f = DBConsults.getXmlFile(domain, domainId, year);
		ZipOutputStream zos = null;
		try {
			zos = makeZip(f, p_response.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (zos != null)
				zos.close();
		}
		

		// FileInputStream input = new FileInputStream(zos.toString());
		//
		// long length = f.length();
		// // FileInputStream fis = new FileInputStream(zos);
		//
		// if (length > 0 && length <= Integer.MAX_VALUE)
		// ;
		// p_response.setContentLength((int) length);
		// ServletOutputStream out = p_response.getOutputStream();
		// p_response.setBufferSize(32768);
		// int bufSize = p_response.getBufferSize();
		// byte[] buffer = new byte[bufSize];
		// BufferedInputStream bis = new BufferedInputStream(input, bufSize);
		// int bytes;
		// while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
		// out.write(buffer, 0, bytes);

		// bis.close();
		// fis.close();
		// out.flush();
		//

	}

	// *****************************************************************************

	private static ZipOutputStream makeZip (File directory, OutputStream os) throws IOException {
		
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		Closeable res = os;
		
		ZipOutputStream zos = null;
		
		try {
			zos = new ZipOutputStream(os);
			res = zos;
			while (!queue.isEmpty()) {
				
				directory = queue.pop();
				String dirName = getChangeName(directory.getName()) + "/";
				//String dirName = directory.getName()+"/";

				for (File kid : directory.listFiles()) {
					
					String name = base.relativize(kid.toURI()).getPath();
					String changeName = (name.endsWith("/")) ? getChangeName(name)+"/" : getChangeName(name);
					
					
					if (kid.isDirectory()) {
						
						queue.push(kid);
						for (File file : kid.listFiles()) {							
							String nameAux = getChangeName(file.getName());
							String changeNameAux = dirName + changeName + nameAux;
							zos.putNextEntry(new ZipEntry(changeNameAux));
							copy(file, zos);
							file.delete();
						}
						
						//String changeName = getChangeName(name);
						String fileName = dirName + ((name.endsWith("/")) 
								? changeName+"/" : changeName);
						zos.putNextEntry(new ZipEntry(fileName));
						
					} else {
						String fileName= dirName+getChangeName(name);
						zos.putNextEntry(new ZipEntry(fileName));
						copy(kid, zos);
						zos.closeEntry();
					}
					
					kid.delete();
				}
				zos.putNextEntry(new ZipEntry(dirName));
				directory.delete();
				queue.clear();
				
			}
		} finally {
			res.close();
		}
		
		return zos;
	}
	
	 private static void copy(File file, OutputStream out) throws IOException {
		    InputStream in = new FileInputStream(file);
		    try {
		      copy(in, out);
		    } finally {
		      in.close();
		    }
		  }
	
	  private static void copy(InputStream in, OutputStream out) throws IOException {
		    byte[] buffer = new byte[1024];
		    while (true) {
		      int readCount = in.read(buffer);
		      if (readCount < 0) {
		        break;
		      }
		      out.write(buffer, 0, readCount);
		    }
		  }
	// *****************************************************************************
	  
	  private static String getChangeName (String name) {
		  
		  Integer startPosition = null;
		  Integer extPosition = name.lastIndexOf('.');
		  
		  if (extPosition > 0) {
			  startPosition = name.indexOf('%');
			  String fileName = name.substring(0, startPosition)+"."+name.substring(extPosition+1);
			  return fileName;
		  }
		  
		  else {
			  startPosition = name.indexOf('%');
			  return new String(name.substring(0, startPosition));
		  }
	  }
 }
