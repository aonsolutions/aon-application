package com.code.aon.ui.fiscal.controller.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.IFiscalConstants;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.util.AonUtil;

public class Mipf implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static String AEAT_PRINT_MODULE_SCRIPT_FOLDER = "/usr/share/java/aon.mipf";
	private static String AEAT_PRINT_MODULE_SCRIPT_PATH_2013 = AEAT_PRINT_MODULE_SCRIPT_FOLDER + "/mipf13pdf.sh";
	private static String AEAT_PRINT_MODULE_SCRIPT_PATH_2014 = AEAT_PRINT_MODULE_SCRIPT_FOLDER + "/mipf14pdf.sh";

	@Deprecated
	public boolean isScriptPresent() {
		File file = new File(AEAT_PRINT_MODULE_SCRIPT_PATH_2013);
		return file.canRead(); 
	}
	
	public boolean isScriptPresent(int year) {
		String script = getScript(year);
		File file = new File(script);
		return file.canRead(); 
	}
	
	private String getScript(int year) {
		if (year < 2014) {
			return AEAT_PRINT_MODULE_SCRIPT_PATH_2013;
		} 
		return AEAT_PRINT_MODULE_SCRIPT_PATH_2014;
	}

	public String getAeatWebPage(FiscalModelType type ) {
		if (type == FiscalModelType.M111) {
			return IFiscalConstants.MOD111_AEAT_WEB;
		} if (type == FiscalModelType.M115) {
			return IFiscalConstants.MOD115_AEAT_WEB;
		} if (type == FiscalModelType.M123) {
			return IFiscalConstants.MOD123_AEAT_WEB;
		} if (type == FiscalModelType.M130) {
			return IFiscalConstants.MOD130_AEAT_WEB;
		} if (type == FiscalModelType.M310) {
			return IFiscalConstants.MOD310_AEAT_WEB;
		} if (type == FiscalModelType.M131) {
			return IFiscalConstants.MOD131_AEAT_WEB;
		}
		return null; 
	}

	public void aeatReport(FiscalModel fiscalModel, FileOutput fileOutput) {
		/*
		 * mipf13pdf.sh 
		 * 	/E:nombrearchivodatos 			indica el fichero que contiene los datos de entrada que se 
		 * 									van a imprimir, de ser correctos. Es OBLIGATORIO y admite 
		 * 									ruta completa. 
		 * [/R:nombrearchivoerrores] 		indica el nombre del fichero que contiene la relación de 
		 * 									errores, si los hubiera. Es OPCIONAL y admite ruta completa. 
		 * 									Si no se especifica o no se puede abrir, el programa utiliza 
		 * 									el archivo ERRORES.TXT que es creado en el directorio de 
		 * 									ejecución del programa.
		 * [/P:nombreFicheroPDF] 			indica el nombre del fichero de salida PDF, que contiene la 
		 * 									declaración en caso que el fichero de entrada no contenga
		 * 									errores Es OPCIONAL y admite ruta completa. 
		 * [/V:{S|N}] 						Parámetro OPCIONAL para indicar que el módulo se comporte exclusivamente 
		 * 									como un módulo de VALIDACIÓN. Si se indica /V:S el programa NO IMPRIMIRÁ,
		 * 									simplemente verificará el contenido del fichero. 
		 * [/C:{S|N}] 						Parámetro OPCIONAL para indicar la generación o no de una copia 
		 * 									adicional (ejemplar para el declarante) de la declaración a 
		 * 									imprimir. Se imprime una copia más cuando se indica 
		 * /C:S [/F:nombrearchivoflag]		indica el nombre del fichero que se utiliza como flag de ejecución
		 * 									del programa. Se crea en el momento en que empieza la ejecución y
		 * 									desaparece cuando ésta finaliza. Es OPCIONAL y admite ruta completa.
		 * 									Si no se especifica o no se puede abrir, el programa utiliza el
		 * 									archivo FLAG.TXT que es creado en el directorio de ejecución del
		 * 									programa.
		 * 
		 * EJEMPLO: 
		 * 		mipf13pdf.sh /E:/tmp/310.TXT /R:/tmp/errores310.txt /P:/tmp/pruebaMIPF.pdf /C:S
		 */
		
		
		File errorFile = null;
		File pdfFile = null;
		File draftFile = null;
		Process process = null;
		File tempDataFile = null;
		FileInputStream fisError = null;
		FileInputStream fisPDF = null;
		try {
			errorFile = File.createTempFile("fs_", ".err");
			pdfFile = File.createTempFile("fs_", ".pdf");
			String pdfPath = pdfFile.getAbsolutePath();	
			String draftPath = StringUtils.substringBefore(pdfPath, ".pdf") + "Borrador.pdf"; 
			System.out.println( pdfPath );
			System.out.println( draftPath );
			
			tempDataFile = saveDiskFile(fileOutput);
			String[] options = { getScript(fiscalModel.getYear())
					, "/E:" + tempDataFile.getAbsolutePath()
					, "/R:" + errorFile.getAbsolutePath()
					, "/P:" + pdfPath
					, "/C:S"
					, "/F:" + "/tmp/fs_flag"};
			System.out.println(Arrays.toString(options));
			process = Runtime.getRuntime().exec(options,null,new File(AEAT_PRINT_MODULE_SCRIPT_FOLDER));
			int timeout = 30000;
			Worker worker = new Worker(process);
			worker.start();
			try {
				worker.join(timeout);
				if (worker.exit == null) {
					throw new TimeoutException();
				}
			} catch (InterruptedException ex) {
				worker.interrupt();
				Thread.currentThread().interrupt();
			} finally {
				process.destroy();
			}
			
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			boolean isDraft = false;
			if (!pdfFile.exists()) {
				pdfFile = new File(draftPath);
				isDraft = true;
			}
			if (pdfFile.exists()) {
				fisPDF = new FileInputStream( pdfFile );
				response.setContentType( MimeType.MIME_PDF.getName() );
				String name = getAutomaticFileName(fiscalModel) + (isDraft?"_Borrador":"");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".pdf" + "\"");
				response.setHeader("Content-Length", String.valueOf(pdfFile.length()));
				IOUtils.copy(fisPDF, response.getOutputStream());
				fisPDF.close();
				context.responseComplete();
			} else {
				if (errorFile.exists()) {
					fisError = new FileInputStream(errorFile);
					List<?> list = IOUtils.readLines(fisError,"ISO-8859-1");
					AonUtil.addErrorMessage("Se han producido errores al generar el fichero:");
					fisError.close();
					for (Object o : list) {
						AonUtil.addErrorMessage(o.toString());
					}
				}
			}
		} catch (TimeoutException e) {
			String msg = "Tiempo de espera agotado.";
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "No se pudo realizar la impresión del módulo." + e.getMessage();
			AonUtil.addErrorMessage(msg);
		} finally {
			FileUtils.deleteQuietly(errorFile);
			FileUtils.deleteQuietly(pdfFile);
			FileUtils.deleteQuietly(draftFile);
			FileUtils.deleteQuietly(tempDataFile);
			IOUtils.closeQuietly(fisError);
			IOUtils.closeQuietly(fisPDF);
		}
	}

	public void validateAeatFile(int year, FileOutput fileOutput) {
		/*
		 * mipf13pdf.sh 
		 * 	/E:nombrearchivodatos 			indica el fichero que contiene los datos de entrada que se 
		 * 									van a imprimir, de ser correctos. Es OBLIGATORIO y admite 
		 * 									ruta completa. 
		 * [/R:nombrearchivoerrores] 		indica el nombre del fichero que contiene la relación de 
		 * 									errores, si los hubiera. Es OPCIONAL y admite ruta completa. 
		 * 									Si no se especifica o no se puede abrir, el programa utiliza 
		 * 									el archivo ERRORES.TXT que es creado en el directorio de 
		 * 									ejecución del programa.
		 * [/P:nombreFicheroPDF] 			indica el nombre del fichero de salida PDF, que contiene la 
		 * 									declaración en caso que el fichero de entrada no contenga
		 * 									errores Es OPCIONAL y admite ruta completa. 
		 * [/V:{S|N}] 						Parámetro OPCIONAL para indicar que el módulo se comporte exclusivamente 
		 * 									como un módulo de VALIDACIÓN. Si se indica /V:S el programa NO IMPRIMIRÁ,
		 * 									simplemente verificará el contenido del fichero. 
		 * [/C:{S|N}] 						Parámetro OPCIONAL para indicar la generación o no de una copia 
		 * 									adicional (ejemplar para el declarante) de la declaración a 
		 * 									imprimir. Se imprime una copia más cuando se indica 
		 * /C:S [/F:nombrearchivoflag]		indica el nombre del fichero que se utiliza como flag de ejecución
		 * 									del programa. Se crea en el momento en que empieza la ejecución y
		 * 									desaparece cuando ésta finaliza. Es OPCIONAL y admite ruta completa.
		 * 									Si no se especifica o no se puede abrir, el programa utiliza el
		 * 									archivo FLAG.TXT que es creado en el directorio de ejecución del
		 * 									programa.
		 * 
		 * EJEMPLO: 
		 * 		mipf13pdf.sh /E:/tmp/310.TXT /R:/tmp/errores310.txt /P:/tmp/pruebaMIPF.pdf /C:S
		 */
		
		
		File errorFile = null;
		File resultFile = null;
		Process process = null;
		File tempDataFile = null;
		FileInputStream fisError = null;
		FileInputStream fisPDF = null;
		try {
			errorFile = File.createTempFile("fs_", ".err");
			resultFile = new File(errorFile.getAbsolutePath() + ".rst"); 
			tempDataFile = saveDiskFile(fileOutput);
			String[] options = { getScript(year)
					, "/E:" + tempDataFile.getAbsolutePath()
					, "/R:" + errorFile.getAbsolutePath()
					, "/F:" + "/tmp/fs_flag"
					, "/V:S"};
			process = Runtime.getRuntime().exec(options,null,new File(AEAT_PRINT_MODULE_SCRIPT_FOLDER));
			int timeout = 30000;
			Worker worker = new Worker(process);
			worker.start();
			try {
				worker.join(timeout);
				if (worker.exit == null) {
					throw new TimeoutException();
				}
			} catch (InterruptedException ex) {
				worker.interrupt();
				Thread.currentThread().interrupt();
			} finally {
				process.destroy();
			}
			if (resultFile.exists()) {
				fisError = new FileInputStream(resultFile);
				List<?> list = IOUtils.readLines(fisError,"ISO-8859-1");
				fisError.close();
				for (Object o : list) {
					AonUtil.addInfoMessage(o.toString());;
				}
			} else {
				if (errorFile.canRead()) {
					fisError = new FileInputStream(errorFile);
					List<?> list = IOUtils.readLines(fisError,"ISO-8859-1");
					fisError.close();
					AonUtil.addErrorMessage("Se han producido errores al generar el fichero:");
					for (Object o : list) {
						AonUtil.addErrorMessage(o.toString());
					}
				}
			}
		} catch (TimeoutException e) {
			String msg = "Tiempo de espera agotado.";
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "No se pudo realizar la impresión del módulo." + e.getMessage();
			AonUtil.addErrorMessage(msg);
		} finally {
//			FileUtils.deleteQuietly(errorFile);
//			FileUtils.deleteQuietly(resultFile);
//			FileUtils.deleteQuietly(tempDataFile);
			IOUtils.closeQuietly(fisError);
			IOUtils.closeQuietly(fisPDF);
		}
	}

	private File saveDiskFile(FileOutput fileOutput) throws IOException {
		OutputStream outputData = null;
		try {
			File tempDataFile = File.createTempFile("fs_", ".txt");
			outputData = new FileOutputStream(tempDataFile);
			IOUtils.write(fileOutput.getContent(), outputData);
			outputData.flush();
			outputData.close();
			return tempDataFile;
		} finally {
			IOUtils.closeQuietly(outputData);
		}
	}
	
	private class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	protected String getAutomaticFileName(FiscalModel fm) {
		String s = fm.getName() + fm.getSurname();
	    StringBuilder sb = new StringBuilder();
	    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
	        sb.append("_");
	    }
	    for (char c : s.toCharArray()) {
	        if(Character.isJavaIdentifierPart(c)) {
	            sb.append(c);
	        }
	    }		
		
		return fm.getModel() 
				+ "_" + fm.getYear() 
				+ "_" + fm.getPeriod()
				+ "_" + sb.toString();
	}
}
