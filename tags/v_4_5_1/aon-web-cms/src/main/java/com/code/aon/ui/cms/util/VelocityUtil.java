package com.code.aon.ui.cms.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.IGeneratorLogger;
import com.code.aon.ui.cms.controller.ICMSConstants;

public class VelocityUtil extends VelocityEngine implements Constants, ICMSConstants {
    
	private static final Logger LOGGER = Logger.getLogger(VelocityUtil.class.getName());
	
	public static final int INFO = 0;

	public static final int ERROR = 1;

	public static final int WARN = 2;

	private File templatePath;
	
	private IGeneratorLogger logger;
	
	private VelocityContext context;
	
	public void setTemplatePath(File templatePath) {
		this.templatePath = templatePath;
		File f = new File(templatePath, Templates.INDEX.getTemplateName());
		if (!f.exists()) { 
			logger.error("No se han encontrado plantillas en '" + templatePath + "'");
		}
	}
	
    public VelocityContext getContext() {
		return context;
	}

	public void setContext(VelocityContext context) {
		this.context = context;
	}
	
    public void setLogger(IGeneratorLogger logger) {
		this.logger = logger;
	}
    
	public IGeneratorLogger getLogger() {
		return logger;
	}

	public void initialize() {
        this.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templatePath.getAbsolutePath() + "/");
        this.setProperty(Velocity.INPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.OUTPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.RUNTIME_LOG, templatePath.getAbsolutePath() + "/" + VELOCITY_LOG_FILE);
        try {
        	this.init();
        } catch (Throwable th) {
        	LOGGER.log(Level.SEVERE, th.getMessage(), th);
        }
    }
    
	public void put(String key, Object value) {
		this.context.put(key, value);
	}

	public void remove(String key) {
		this.context.remove(key);
	}

	public boolean generate(File template, File page) {
        String pageShortName = page.getName();
        
		boolean error = false;
        FileWriter fw = null;
        BufferedWriter writer = null;
    	try{
	        fw = new FileWriter(page);
	        writer = new BufferedWriter(fw);
	        
	        error = generate(template, writer, pageShortName);
		} catch(Throwable th) {
		    error = true;
		    logger.error("Error al generar el fichero '" + pageShortName + "' </BR> " + th.getMessage() + "");
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		} finally {
	        try {
	            if (writer != null) {
	                writer.flush();
	            }
	        } catch (Throwable th) {
	        	LOGGER.log(Level.SEVERE, th.getMessage(), th);
	        }
	        IOUtils.closeQuietly(writer);
	        IOUtils.closeQuietly(fw);
	    }
	    return error;
    }
	
    public boolean generate(File template, BufferedWriter writer, String pageShortName) {
		boolean error = false;

        BufferedReader reader = null;
        FileReader fr = null;

        try {
			if (!template.exists()) {
				error = true;
				logger.error("Fichero de plantilla '" + template + "' no encontrado.");
			} else {
                fr = new FileReader(template);
				reader = new BufferedReader(fr);
			}

			if (!error) {
				try {

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
					logger.info("Página " + pageShortName + " generada con exito");
				} catch(Throwable th) {
				    error = true;
				    logger.error("Error al evaluar el contexto en el fichero '" + pageShortName + "' <BR/>" + th.getMessage());
				}
			} else {
				logger.error("No se pudo generar el fichero '" + pageShortName + "'");
			}
		} catch(Throwable th) {
		    error = true;
		    logger.error("Error al generar el fichero '" + pageShortName + "' </BR> " + th.getMessage());
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fr);
        }
		return error;
    }
    
}
