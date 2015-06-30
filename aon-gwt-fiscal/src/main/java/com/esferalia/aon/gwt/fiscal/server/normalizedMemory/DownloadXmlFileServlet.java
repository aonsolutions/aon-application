package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;

@WebServlet(name = "DownloadXml", urlPatterns = { "/aon_gwt_fiscal/gwt_download_deposit/*" })
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
		// p_response.setContentType("application/octet-stream");
		p_response.setContentType(MimeType.ZIP.getName());

		String domain_id = p_request.getParameter("domain_id");
		Integer domainId = Integer.parseInt(domain_id);
		String domain = AonUtil.getDomainName();

		File f = DBConsults.getXmlFile(domain, domainId);
		
		try {
			
			ZipOutputStream zos = makeZip(f, p_response.getOutputStream());
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
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
		// // TODO probar ---> libro.close();

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
				
				for (File kid : directory.listFiles()) {
					
					String name = base.relativize(kid.toURI()).getPath();
					if (kid.isDirectory()) {
						queue.push(kid);
						name = name.endsWith("/") ? name : name + "/";
						zos.putNextEntry(new ZipEntry(name));
						
					} else {
						zos.putNextEntry(new ZipEntry(name));
						copy(kid, zos);
						zos.closeEntry();
					}
					
					kid.delete();
				}
				directory.delete();
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

}
