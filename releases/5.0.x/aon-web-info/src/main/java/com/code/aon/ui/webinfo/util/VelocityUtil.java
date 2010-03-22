package com.code.aon.ui.webinfo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webinfo.velocity.VelocityConstants;

public class VelocityUtil extends VelocityEngine implements VelocityConstants {
    
	public static final int INFO = 0;

	public static final int ERROR = 1;

	public static final int WARN = 2;

	private String template_path;

	private String temporal_path;
	
	private String template;

	private VelocityContext context = new VelocityContext();
	
	public void setTemplate_path(String template_path) {
		this.template_path = template_path + "/" + template;
		File f = new File(template_path + "/" + template + "/index.vm");
		if (!f.exists()) addMessage("No se han encontrado plantillas en '" + template_path + "'", ERROR);
	}

	public void setTemporal_path(String temporal_path) {
		this.temporal_path = temporal_path;
	}

	
	public String getTemplate_path() {
		return template_path;
	}

	public String getTemporal_path() {
		return temporal_path;
	}

	public VelocityContext getContext() {
		return context;
	}

	public void setContext(VelocityContext context) {
		this.context = context;
	}

    public void initialize() {
    	System.out.println(">>>>>>>>>>>>>> " + template_path + "/");
        this.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, template_path + "/");
        this.setProperty(Velocity.INPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.OUTPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.RUNTIME_LOG, template_path + "/" + VELOCITY_LOG_FILE);
        try {
        	this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void addMessage(String msg, int type) {
		if (type == INFO) 
			AonUtil.addInfoMessage(msg);
		else if (type == ERROR)
			AonUtil.addErrorMessage(msg);
		else if (type == WARN)
			AonUtil.addWarningMessage(msg);
		else
			AonUtil.addFatalMessage(msg);
	}

	public void put(String key, Object value) {
		this.context.put(key, value);
	}

	public void remove(String key) {
		this.context.remove(key);
	}

    public boolean generate(String page) {
		boolean error = false;

        File fi = new File(this.template_path + "/index.vm");
        BufferedReader reader = null;
        FileReader fr = null;

        String path = this.temporal_path + "/" + page;
        File fo = new File(path);
        BufferedWriter writer = null;
        FileWriter fw = null;

        try {
			if (!fi.exists()) {
				error = true;
				//addMessage("Fichero de plantilla 'index.vm' no encontrado.", ERROR);
			}
			else {
                fr = new FileReader(fi);
				reader = new BufferedReader(fr);
			}

			if (!error) {
				try {
                    fw = new FileWriter(fo);
                    writer = new BufferedWriter(fw);

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
					//addMessage("Página " + page + " generada con exito", INFO);
				}
				catch(Exception e) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero '" + page + "' <BR/>" + e.getMessage(), ERROR);
				}
			}
			else {
				addMessage("No se pudo generar el fichero '" + page + "'", ERROR);
			}
		}
		catch(Exception e) {
		    error = true;
			addMessage("Error al generar el fichero '" + page + "' </BR> " + e.getMessage() + "", ERROR);
			e.printStackTrace();
		}
        finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                    fw.close();
                }
                if (reader != null) {
                    reader.close();
                    fr.close();
                }
                fi = null;
                fo = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

		return error;
	}

    public boolean generate(String template, String page) {
		boolean error = false;

        File fi = new File(this.template_path + "/" + template);
        BufferedReader reader = null;
        FileReader fr = null;

        String path = this.temporal_path + "/" + page;
        File fo = new File(path);
        BufferedWriter writer = null;
        FileWriter fw = null;

        try {
			if (!fi.exists()) {
				error = true;
			}
			else {
                fr = new FileReader(fi);
				reader = new BufferedReader(fr);
			}

			if (!error) {
				try {
                    fw = new FileWriter(fo);
                    writer = new BufferedWriter(fw);

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
				}
				catch(Exception e) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero '" + template + "' <BR/>" + e.getMessage(), ERROR);
				}
			}
			else {
				addMessage("No se pudo generar el fichero '" + page + "'", ERROR);
			}
		}
		catch(Exception e) {
		    error = true;
			addMessage("Error al generar el fichero '" + page + "' </BR> " + e.getMessage() + "", ERROR);
			e.printStackTrace();
		}
        finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                    fw.close();
                }
                if (reader != null) {
                    reader.close();
                    fr.close();
                }
                fi = null;
                fo = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

		return error;
	}

    public boolean generateCSS() {
		boolean error = false;

        File fi = new File(this.template_path + "/css/style.vm");
        BufferedReader reader = null;
        FileReader fr = null;

        String path = this.temporal_path + "/css/style.css";
        File fo = new File(path);
        BufferedWriter writer = null;
        FileWriter fw = null;

        try {
			if (!fi.exists()) {
				error = true;
			}
			else {
                fr = new FileReader(fi);
				reader = new BufferedReader(fr);
			}

			if (!error) {
				try {
                    fw = new FileWriter(fo);
                    writer = new BufferedWriter(fw);

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
				}
				catch(Exception e) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero 'style.css' <BR/>" + e.getMessage(), ERROR);
				}
			}
			else {
				addMessage("No se pudo generar el fichero 'style.css'", ERROR);
			}
		}
		catch(Exception e) {
		    error = true;
			addMessage("Error al generar el fichero 'style.css' </BR> " + e.getMessage() + "", ERROR);
			e.printStackTrace();
		}
        finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                    fw.close();
                }
                if (reader != null) {
                    reader.close();
                    fr.close();
                }
                fi = null;
                fo = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

		return error;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	
	public void copyDir(String sourcePath, String destinationPath) {
		File sf = new File(sourcePath);
		File df = new File(destinationPath + "/" + sf.getName()); 
		if (sf.exists() && sf.canRead()) {
			if (sf.isDirectory()) {
				copyDir(sf, df);
			}
			else copyFile(sf, df);
		}
	}

	private void copyDir(File sf, File df) {
		//Creamos el directorio destino
		if (!df.exists()) df.mkdirs();
		for (File f : sf.listFiles()) {
			if (f.exists() && f.canRead()) {
				File f2 = new File(df.getAbsolutePath() + "/" + f.getName());
				if (f.isDirectory()) {
					copyDir(f, f2);
				}
				else copyFile(f, f2);
			}
		}
	}

	public void copyFile(String sourcePath, String destinationPath) throws IOException {
		File sf = new File(sourcePath);
		File df = new File(destinationPath);
		File dfp = df.getParentFile();
		if (!dfp.exists()) dfp.mkdirs();
		if (sf.exists() && sf.canRead()) {
			if (sf.isDirectory()) {
				throw new IOException("No se puede copiar un directorio como archivo.");
			}
			else copyFile(sf, df);
		}
	}

	private void copyFile(File sf, File df) {
		InputStream is = null;
		BufferedInputStream bis = null;
        OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			is = new FileInputStream(sf);
			bis = new BufferedInputStream(is);
	        os = new FileOutputStream(df);
			bos = new BufferedOutputStream(os);
			byte[] input = new byte[1024];
			boolean eof = false;
			while (!eof) {
				int length = bis.read(input);
				if (length == -1) {
					eof = true;
				}
				else {
					bos.write(input, 0, length);
				}
			}
			bos.flush();
			bis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{bis.close();}catch(Exception e){}
			try{is.close();}catch(Exception e){}
			try{bos.close();}catch(Exception e){}
			try{os.close();}catch(Exception e){}
			is = null;
			bis = null;
	        os = null;
			bos = null;
		}
	}

}
