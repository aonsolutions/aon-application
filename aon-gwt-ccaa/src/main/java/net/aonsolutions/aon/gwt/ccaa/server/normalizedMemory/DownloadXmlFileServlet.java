package net.aonsolutions.aon.gwt.ccaa.server.normalizedMemory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.Month;
import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@WebServlet(name = "DownloadXml", urlPatterns = { "/aon_gwt_deposit/gwt_download_deposit/*",
												  "/aon_gwt_aio/gwt_download_deposit/*"})
public class DownloadXmlFileServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String D2_FILE_MEMORY = "Memoria";
	private static final String D2_FILE_AUTOCARTERA_MODEL = "Modelo de Autocartera";
	private static final String D2_FILE_GESTION = "Informe de Gesti\u00f3n";
	private static final String D2_FILE_AUDIT = "Informe de Auditor\u00eda";
	private static final String D2_FILE_TITULAR_REAL = "Informe de Titular Real";
	private static final String D2_FILE_NO_FINANCIERA= "Informe sobre Informaci\u00f3n no financiera";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificaci\u00f3n SICAV";
	
	private static final String D2_FILE_AUTOCARTERA_MODEL_2015 = "Acciones";
	private static final String D2_FILE_GESTION_2015 = "Gestion";
	private static final String D2_FILE_AUDIT_2015 = "Auditoria";
	private static final String D2_FILE_TITULAR_REAL_2015 = "TitularReal";
	private static final String D2_FILE_NO_FINANCIERA_2015 = "nofinanciera";
	private static final String D2_FILE_CONVOC_2015 = "Convocatoria";
	private static final String D2_FILE_SICAV_2015 = "SICAV";
	
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
		
		File f = /*DBConsults.*/getXmlFile(domain, domainId, year);
		
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
	  
	  
	  public static File getXmlFile(String domain, Integer domainId, Integer year)
				throws IOException {
			File parent = null;
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, Month.DECEMBER.ordinal());
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			
			Attach xml = AON.getAttach(domain, domainId, "",
					f -> f.getTypeProperty().eq(RegistryAttachmentType.D2_DEPOSIT.value())
						.and(f.getDomainProperty().eq(domainId)
						.and(f.getAttachDateProperty().eq(AonDateUtils.toSql(calendar.getTime())))),
					AttachType.REGISTRY);
			byte[] data;
			if (xml != null && xml.getId() != null) {
				parent = File.createTempFile(xml.getDescription()+"%", "");
				parent.delete();
				parent.mkdir();
				
				File documents = File.createTempFile("Documentos%", "", parent);
				documents.delete();
				documents.mkdir();
				
				//**************************
				getFileDocuments2Zip(domain, domainId, D2_FILE_MEMORY, documents, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUTOCARTERA_MODEL, documents, year);	
				getFileDocuments2Zip(domain, domainId, D2_FILE_GESTION, documents, year);			
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUDIT, documents, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_CONVOC, documents, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_SICAV, documents, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_NO_FINANCIERA, documents, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_TITULAR_REAL, documents, year);
				//**************************
				
				File tmpDocuments = File.createTempFile("Documentos TMP%", "", parent);
				tmpDocuments.delete();
				tmpDocuments.mkdir();
				
				File otherDocuments =File.createTempFile("Otros Documentos%", "", parent);
				otherDocuments.delete();
				otherDocuments.mkdir();
				
				File otherTmpDocuments =File.createTempFile("Otros Documentos TMP%", "", parent);
				otherTmpDocuments.delete();
				otherTmpDocuments.mkdir();

				File f = File.createTempFile("DEPOSITO%", ".xml", parent);
				
				if(xml.getData() != null)
					data = xml.getData();
				else if(xml.getDriveId() != null) {
					DomainGserviceaccount g = AON.getDomainGserviceaccount(domain, domainId, "");
					Drive drive = AonDrive.getInstace().serviceInitialize(g);
					data = AonDrive.getInstace().downloadFileByteArray(drive, xml.getDriveId());
				} else data = null;
				
				if(data != null)
					try {
						AonFileUtils.writeByteArrayToFile(f, data);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			
			return parent;
	  }
	  
		public static File getFileDocuments2Zip(String domain, Integer domainId,final String name2, File parent, Integer year) throws IOException {
			String name = name2;
			Attach attach = AON.getAttach(domain, domainId, "",
					f -> f.getDescriptionProperty().eq(name2+year)
						.and(f.getDomainProperty().eq(domainId))
						.and(f.getTypeProperty().eq((byte)7)),
					AttachType.REGISTRY);

			byte[] data;				
			if(attach != null && attach.getId() != null){
				if(year != null && year > 2014){
					if(name.equals(D2_FILE_AUDIT)) name = D2_FILE_AUDIT_2015;
					if(name.equals(D2_FILE_AUTOCARTERA_MODEL)) name = D2_FILE_AUTOCARTERA_MODEL_2015;
					if(name.equals(D2_FILE_CONVOC)) name = D2_FILE_CONVOC_2015;
					if(name.equals(D2_FILE_GESTION)) name = D2_FILE_GESTION_2015;
					if(name.equals(D2_FILE_SICAV)) name = D2_FILE_SICAV_2015;
					if(name.equals(D2_FILE_NO_FINANCIERA)) name = D2_FILE_NO_FINANCIERA_2015;
					if(name.equals(D2_FILE_TITULAR_REAL)) name = D2_FILE_TITULAR_REAL_2015;
				}					
				String extension = attach.getMimeType().getExtension();
				File tempFile = File.createTempFile(name.toUpperCase() +"%", "."+extension, parent);				
				if(attach.getData() != null )
					data = attach.getData();
				else if(attach.getDriveId() != null){
					DomainGserviceaccount g = AON.getDomainGserviceaccount(domain, domainId, "");
					Drive drive = AonDrive.getInstace().serviceInitialize(g);
					data = AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId());
				} else data = null;
				if(data != null)		
					AonFileUtils.writeByteArrayToFile(tempFile, data);				
				return tempFile;			
			}
			return null;
		}
}
