package net.aonsolutions.aon.gwt.ccaa.server.normalizedMemory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.gwt.ccaa.server.xbrl.XmlToXbrl;

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
		
		String domain_id = p_request.getParameter("domain_id");
		Integer domainId = Integer.parseInt(domain_id);
		String domain = p_request.getParameter("domain_name");
		
		String yearStr = p_request.getParameter("year");
		Integer year = Integer.parseInt(yearStr);
		
		File f;
		if (year >= 2024) {
			f = getXmlFile2024(domain, domainId, year);
			p_response.addHeader("Content-Disposition", "inline; filename=\"D2_" + AonStringUtils.left(f.getName(), 9) + "_" + year +".zip\""); 
		} else {
			f = getXmlFile(domain, domainId, year);
			p_response.addHeader("Content-Disposition", "inline; filename=\"DEPOSITO.zip\"");
		}
		
		p_response.setContentType(MimeType.ZIP.getName());
		
		ZipOutputStream zos = null;
		try {
			zos = makeZip(f, p_response.getOutputStream(), year);
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

	private static ZipOutputStream makeZip(File directory, OutputStream os, int year) throws IOException {
		
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
						String fileName= (year >= 2024 ? "" : dirName) + getChangeName(name);
						zos.putNextEntry(new ZipEntry(fileName));
						copy(kid, zos);
						zos.closeEntry();
					}
					
					kid.delete();
				}
				if (year < 2024) {
					zos.putNextEntry(new ZipEntry(dirName));
				}
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
		  
		  Integer startPosition = name.indexOf('%');
		  if (startPosition == -1)
			  return name;
		  
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
				
				Esquema schema = DBConsults.getDeposit(xml);
				
				if(!schema.getCabecera().isMemoriaNormalizada()) {
					Vector<Integer> id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_MEMORY + year);
					if(id.get(0) == -1) {
						schema.getCabecera().setMemoriaNormalizada(true);		
					}
				}
						
				Integer count = 0;
				Esquema.Claves claves = new Esquema.Claves();
				for(Integer i = 0; i < schema.getClaves().getClave().size(); i++) {

					BigInteger code = new BigInteger(D2DepositHeaderKey.BA2121300.getCode());
					if(schema.getClaves().getClave().get(i).getCodigo().equals(code)) {
						count++;
					}
					if(!schema.getClaves().getClave().get(i).getCodigo().equals(code) || count <= 1) {
						claves.getClave().add(schema.getClaves().getClave().get(i));
					}
				}
				schema.setClaves(claves);
				
				try {
					data = Utils.writeXml(schema);
				} catch (JAXBException | IOException e) {
					e.printStackTrace();
					if(xml.getData() != null)
						data = xml.getData();
					else if(xml.getDriveId() != null) {
						DomainGserviceaccount g = AON.getDomainGserviceaccount(domain, domainId, "");
						Drive drive = AonDrive.getInstace().serviceInitialize(g);
						data = AonDrive.getInstace().downloadFileByteArray(drive, xml.getDriveId());
					} else data = null;
				}
								
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
		
	    // Generar XML y XBRL a partir del ejercicio 2024	
		public static File getXmlFile2024(String domain, Integer domainId, Integer year) throws IOException {

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

				Esquema schema = DBConsults.getDeposit(xml);

				// Si existe el fichero de la memoria, se asume que no se cumplimenta la memoria				
				Vector<Integer> id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_MEMORY + year);
				schema.getCabecera().setMemoriaNormalizada(id.get(0) == -1);
				
				// Revisar las claves que lleva el esquema por si hay que modificar o quitar alguna
				Esquema.Claves claves = new Esquema.Claves();
				for (Integer i = 0; i < schema.getClaves().getClave().size(); i++) {

					// Servicios a terceros, si clave 8080832 esta a cero la 831001 se deja vacia, pues por defecto se guarda un 1 
					if (schema.getClaves().getClave().get(i).getCodigo().equals(BigInteger.valueOf(831001))) {
						if (buscar(schema, "8080832").equals("0")) {
							schema.getClaves().getClave().get(i).setValor("");
						}
					}
					
					// Hoja presentación claves memoria (8080805, 8080852), en ocasiones se quedan vacias o a cero ambas, se ponen segun el formato
					boolean isPymes = "PYMES".equalsIgnoreCase(schema.getCabecera().getTipoCuestionario()); // Formato PYMES o Abreviado
					if (schema.getClaves().getClave().get(i).getCodigo().equals(BigInteger.valueOf(8080805))) {
						if ("0".equals(schema.getClaves().getClave().get(i).getValor()) && !isPymes) {
							schema.getClaves().getClave().get(i).setValor("1");
						}						
					}
					if (schema.getClaves().getClave().get(i).getCodigo().equals(BigInteger.valueOf(8080852))) {
						if ("0".equals(schema.getClaves().getClave().get(i).getValor()) && isPymes) {
							schema.getClaves().getClave().get(i).setValor("1");
						}						
					}
					
					// Claves 8080854, 8080855 se ignoran, ahora no se usan y se están grabando en el XML
					if (!schema.getClaves().getClave().get(i).getCodigo().equals(BigInteger.valueOf(8080854)) && 
						!schema.getClaves().getClave().get(i).getCodigo().equals(BigInteger.valueOf(8080855))) {
						claves.getClave().add(schema.getClaves().getClave().get(i));
					}
					
				}
				
				// Añadir clave C8081010
				Clave c8081010 = new Clave();
				c8081010.setCodigo(BigInteger.valueOf(8081010));
				c8081010.setValor(getC8081010(schema));
				claves.getClave().add(c8081010);
				System.out.println(c8081010.getValor());
				
				// Asignar lista de claves modificada al schema
				schema.setClaves(claves);

				Path parentPath = Files.createTempDirectory(schema.getCabecera().getCIF());
				parent = parentPath.toFile();
				
				// Crear el archivo XBRL
				XmlToXbrl.getInstance().createXbrl(schema, parent);
				
				// Crear el archivo XML
				File f = File.createTempFile("deposito%", ".xml", parent);

				try {
					data = Utils.writeXml(schema);
				} catch (JAXBException | IOException e) {
					e.printStackTrace();
					if (xml.getData() != null)
						data = xml.getData();
					else if (xml.getDriveId() != null) {
						DomainGserviceaccount g = AON.getDomainGserviceaccount(domain, domainId, "");
						Drive drive = AonDrive.getInstace().serviceInitialize(g);
						data = AonDrive.getInstace().downloadFileByteArray(drive, xml.getDriveId());
					} else
						data = null;
				}

				if (data != null) {
					try {
						AonFileUtils.writeByteArrayToFile(f, data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// Añadir documentos
				getFileDocuments2Zip(domain, domainId, D2_FILE_MEMORY, parent, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUTOCARTERA_MODEL, parent, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_GESTION, parent, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_AUDIT, parent, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_CONVOC, parent, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_SICAV, parent, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_NO_FINANCIERA, parent, year);
				getFileDocuments2Zip(domain, domainId, D2_FILE_TITULAR_REAL, parent, year);
				
			}

			return parent;
		}
		
		private static String getC8081010(Esquema schema) {
			
			// Codificación de documentos (clave 8081010)
			
//			Cadena tipo "abcdefghijklmnnopqstruvw" donde:
//				"a" es el balance (0 no hay, 1 es normal, 2 es abreviado, 3 es PYME, 4 es Mixto)
//				"b" es la cuenta de pérdidas y ganancias (0 no hay, 1 es normal, 2 es abreviada, 3 es PYME)
//				"c" es la memoria (0 no hay, 1 es normal, 2 es abreviada, 3 es PYME) Claves 8080805 abr y 8080852 pymes
//				"d" es el estado de cambios en el patrimonio neto (0 no hay, 1 es normal)
//				"e" es el estado de flujos de efectivo (0 no hay, 1 es normal)
//				"f" es la hoja identificativa de la sociedad (0 no hay, 1 si hay)
//				"g" es el informe de gestión (0 no hay, 1 si hay) Clave 8080807
//				"h" es el informe de auditoría (0 no hay, 1 si hay) Clave 8080817
//				"i" es el modelo de autocartera (0 no hay, 1 si hay) Clave 8080809
//				"j" son los anuncios de convocatoria (0 no hay, 1 si hay) Clave 8080823
//				"k" es el certificado SICAV (0 no hay, 1 si hay) Clave 8080821
//				"l" es la certificación del acuerdo (0 no hay, 1 si hay) 8080811
//				"m" es la moneda utilizada (E euros, M miles de euros, B millones de euros)
//				"nn" es el número de otros documentos (00 - 89)
//				"o" son los otros documentos (0 no hay, 1 si hay)
//				"p" es la declaración medioambiental (0 no hay, 1 si hay)
//				"q" es el estado sobre información no financiera (0 no hay, 1 si hay) Clave 8080825
//				"s" es la declaración de identificación del titular real (0 no hay, 1 si hay)
//				"t" es el documento sobre servicios a terceros (0 no hay, 1 si hay) Clave 8080832
//				"r" es la retención (0 no hay, 1 si hay)
//				"u" es el formato de presentación (0 si no es ESEF / FEUE, 1 si es ESEF/ FEUE)
//				"v" es la Hoja COVID-19 (0 no hay)
//				"w" es el impuesto sobre sociedades (información por países) (0 no hay, 1 si hay) (A PARTIR DEL EJERCICIO 2025, NO SE USA ES SOLO PARA EL MODELO NORMAL)
			
			String formato = "PYMES".equalsIgnoreCase(schema.getCabecera().getTipoCuestionario()) ? "3" : "2"; // Formato PYMES o Abreviado
			String g = buscar(schema, "8080807");
			String h = buscar(schema, "8080817"); 
			String i = buscar(schema, "8080809"); 
			String j = buscar(schema, "8080823"); 
			String k = buscar(schema, "8080821"); 
			String l = buscar(schema, "8080811");
			String m = buscar(schema, "9002").equals("1") ? "M" : buscar(schema, "9003").equals("1") ? "B" : "E";   
			String q = buscar(schema, "8080825"); 
			String t = buscar(schema, "8080832"); 
			
            //        a         b         c       def    g   h   i   j   k   l   m    nnop    q    s    t    ruv                                                               w
            return formato + formato + formato + "001" + g + h + i + j + k + l + m + "0001" + q + "1" + t + "000" + (schema.getCabecera().getEjercicio().intValue() >= 2025 ? "0" : "");
		}
		
	    // Busca la clave que se le pasa en el XML y devuelve su valor o "0" si no se encuentra  
	    private static String buscar(Esquema schema, String key) {
	    	
			for (int i = 0; i < schema.getClaves().getClave().size(); i++) {
				BigInteger code = new BigInteger(key);
				if (schema.getClaves().getClave().get(i).getCodigo().equals(code)) {				
					return schema.getClaves().getClave().get(i).getValor();
				}
			}
			// Si no la encuentra, se devuelve "0"
			return "0";
			
	    }
		
}
