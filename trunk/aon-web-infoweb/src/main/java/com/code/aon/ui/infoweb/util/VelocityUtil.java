package com.code.aon.ui.infoweb.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.util.AonUtil;

public class VelocityUtil extends VelocityEngine implements VelocityConstants {
    
	private static final Logger LOGGER = Logger.getLogger(VelocityUtil.class.getName());
	
	public static final int INFO = 0;

	public static final int ERROR = 1;

	public static final int WARN = 2;

	private File templateDirectory;

	private File temporalDirectory;
	
	private String template;

	private VelocityContext context = new VelocityContext();
	
	public void setTemplateDirectory(File template_path) {
		this.templateDirectory = new File( template_path, template );
		File f = new File(templateDirectory, INDEX_TEMPLATE);
		if (!f.exists()) {
			addMessage("No se han encontrado plantillas en '" + template_path + "'", ERROR);
		}
	}

	public void setTemporalDirectory(File temporalDirectory) {
		this.temporalDirectory = temporalDirectory;
	}

	public VelocityContext getContext() {
		return context;
	}

	public void setContext(VelocityContext context) {
		this.context = context;
	}

    public void initialize() {
        this.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templateDirectory.getAbsolutePath());
        this.setProperty(Velocity.INPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.OUTPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.RUNTIME_LOG, templateDirectory + "/" + VELOCITY_LOG_FILE);
        try {
        	this.init();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

        File fi = new File(this.templateDirectory, INDEX_TEMPLATE);
        BufferedReader reader = null;
        FileReader fr = null;

        File fo = new File(this.temporalDirectory, page);
        BufferedWriter writer = null;
        FileWriter fw = null;

        try {
			if (!fi.exists()) {
				error = true;
				LOGGER.warning("Fichero de plantilla 'index.vm' no encontrado.");
			} else {
                fr = new FileReader(fi);
				reader = new BufferedReader(fr);
			}
			if (!error) {
				try {
                    fw = new FileWriter(fo);
                    writer = new BufferedWriter(fw);

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
					LOGGER.info("Página " + page + " generada con exito");
				} catch(Exception e) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero '" + page + "' <BR/>" + e.getMessage(), ERROR);
				}
			} else {
				addMessage("No se pudo generar el fichero '" + page + "'", ERROR);
			}
		} catch(Exception e) {
		    error = true;
			addMessage("Error al generar el fichero '" + page + "' </BR> " + e.getMessage() + "", ERROR);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
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
            	LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
		return error;
	}

    public boolean generate(String template, String page) {
		boolean error = false;

        File fi = new File(this.templateDirectory, template);
        BufferedReader reader = null;
        FileReader fr = null;

        File fo = new File(this.temporalDirectory, page);
        BufferedWriter writer = null;
        FileWriter fw = null;

        try {
			if (!fi.exists()) {
				error = true;
			} else {
                fr = new FileReader(fi);
				reader = new BufferedReader(fr);
			}

			if (!error) {
				try {
                    fw = new FileWriter(fo);
                    writer = new BufferedWriter(fw);

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
				} catch(Exception e) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero '" + template + "' <BR/>" + e.getMessage(), ERROR);
				}
			} else {
				addMessage("No se pudo generar el fichero '" + page + "'", ERROR);
			}
		} catch(Exception e) {
		    error = true;
			addMessage("Error al generar el fichero '" + page + "' </BR> " + e.getMessage() + "", ERROR);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
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
            	LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

		return error;
	}

    public boolean generateCSS() {
		boolean error = false;
		
		File templateCss = new File( this.templateDirectory, CSS_PATH );
        File fi = new File(templateCss, STYLE_TEMPLATE);
        BufferedReader reader = null;
        FileReader fr = null;

        File temporalCss = new File( this.temporalDirectory, CSS_PATH );
        File fo = new File(temporalCss, "style.css");
        BufferedWriter writer = null;
        FileWriter fw = null;

        try {
			if (!fi.exists()) {
				error = true;
			} else {
                fr = new FileReader(fi);
				reader = new BufferedReader(fr);
			}

			if (!error) {
				try {
                    fw = new FileWriter(fo);
                    writer = new BufferedWriter(fw);

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
				} catch(Exception e) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero 'style.css' <BR/>" + e.getMessage(), ERROR);
				}
			} else {
				addMessage("No se pudo generar el fichero 'style.css'", ERROR);
			}
		} catch(Exception e) {
		    error = true;
			addMessage("Error al generar el fichero 'style.css' </BR> " + e.getMessage() + "", ERROR);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
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
            	LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

}
