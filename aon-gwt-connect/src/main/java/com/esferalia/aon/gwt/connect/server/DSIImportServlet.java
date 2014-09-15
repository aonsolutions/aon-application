package com.esferalia.aon.gwt.connect.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.esferalia.aon.gwt.connect.shared.DSIImportService;

public class DSIImportServlet extends HttpServlet implements DSIImportService{

	
	private static  interface UnzipCallBack {
		void unzipped(ZipEntry zipEntry);
	}
	
	
	private static class DefaultUnzipCallback implements UnzipCallBack{
		
		private PrintWriter writer;
		
		@Override
		public void unzipped(ZipEntry zipEntry) {
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		File dir = craeteTempDir();
		Part part = req.getPart(ZIP_FILE_PART);
		InputStream in = part.getInputStream();
		
		UnzipCallBack cb = new DefaultUnzipCallback();
		
		
		unzip(dir, in , cb );
	}
	
	
	
	private static File craeteTempDir() throws IOException { 
		File temp = File.createTempFile("dsi-", "");
		temp.delete();
		temp.mkdir();
		return temp;
	}

	private static void unzip(File parent, InputStream in, UnzipCallBack cb ) throws IOException {
		ZipInputStream zin = new ZipInputStream(in);
		
		byte buff[] = new byte[1024];

		for (ZipEntry entry = zin.getNextEntry(); entry != null; entry = zin
				.getNextEntry()) {
			String name = entry.getName();
			File file = new File(parent, name);

			if (entry.isDirectory()) {
				file.mkdirs();
				continue;
			}

			FileOutputStream fout = new FileOutputStream(file);
			for (int read = zin.read(buff, 0, 1024); read > 0; read = zin.read(
					buff, 0, 1024))
				fout.write(buff, 0, read);
			fout.close();
			
			cb.unzipped(entry);

		}

		zin.close();
	}


}
