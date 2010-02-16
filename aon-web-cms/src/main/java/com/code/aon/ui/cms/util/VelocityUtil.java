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
import com.code.aon.ui.cms.controller.GeneratorStatusController;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.util.AonUtil;

public class VelocityUtil extends VelocityEngine implements Constants, ICMSConstants {
    
	private static final Logger LOGGER = Logger.getLogger(VelocityUtil.class.getName());
	
	public static final int INFO = 0;

	public static final int ERROR = 1;

	public static final int WARN = 2;

	private String template_path;
	
	private VelocityContext context = new VelocityContext();
	
	public void setTemplate_path(String template_path) {
		this.template_path = template_path;
		File f = new File(template_path + "/" + Templates.INDEX.getTemplateName());
		if (!f.exists()) 
			addMessage("No se han encontrado plantillas en '" + template_path + "'", ERROR);
	}
	
	public VelocityContext getContext() {
		return context;
	}

	public void setContext(VelocityContext context) {
		this.context = context;
	}

    public void initialize() {
        this.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, template_path + "/");
        this.setProperty(Velocity.INPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.OUTPUT_ENCODING, VELOCITY_FILE_ENCODING);
        this.setProperty(Velocity.RUNTIME_LOG, template_path + "/" + VELOCITY_LOG_FILE);
        try {
        	this.init();
        } catch (Throwable th) {
        	LOGGER.log(Level.SEVERE, th.getMessage(), th);
        }
    }
    
    public void finalize() {
    }

	public static void addMessage(String msg, int type) {
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
		if (type == INFO){
			//AonUtil.addInfoMessage(" INFO: " + msg);
			status.addMessage(" INFO: " + msg);
		}else if (type == ERROR){
			//AonUtil.addErrorMessage(" ERROR: " + msg);
			//status.addMessage(" ERROR: " + msg);
			status.addErrorMessage(" ******* ERROR: " + msg + "***********");
		}else if (type == WARN){
			//AonUtil.addWarningMessage(" WARNING: " + msg);
			//status.addMessage(" ******* WARNING: " + msg + "***********");
			status.addErrorMessage(" ******* WARNING: " + msg + "***********");
		}else{
			//AonUtil.addFatalMessage(msg);
			status.addMessage(msg);
		}
	}

	public void put(String key, Object value) {
		this.context.put(key, value);
	}

	public void remove(String key) {
		this.context.remove(key);
	}

    public boolean generate(String template, String page) {
        String pageShortName;
        try{
        	pageShortName = page.substring(page.lastIndexOf('/'));
        }catch (Throwable th) {
        	pageShortName = page;
		}
        
		boolean error = false;
		File fo;
        FileWriter fw = null;
        BufferedWriter writer = null;
    	try{
	        fo = new File(page);
	        fw = new FileWriter(fo);
	        writer = new BufferedWriter(fw);
	        
	        error = generate(template, writer, pageShortName);
		} catch(Throwable th) {
		    error = true;
			addMessage("Error al generar el fichero '" + pageShortName + "' </BR> " + th.getMessage() + "", ERROR);
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
	        fo = null;
	    }
	    return error;
    }
	
    public boolean generate(String template, BufferedWriter writer, String pageShortName) {
		boolean error = false;

        File fi = new File(template);
        BufferedReader reader = null;
        FileReader fr = null;

        try {
			if (!fi.exists()) {
				error = true;
				addMessage("Fichero de plantilla '" + template + "' no encontrado.", ERROR);
			}
			else {
                fr = new FileReader(fi);
				reader = new BufferedReader(fr);
			}

			if (!error) {
				try {

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
					addMessage("Página " + pageShortName + " generada con exito", INFO);
				} catch(Throwable th) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero '" + pageShortName + "' <BR/>" + th.getMessage(), ERROR);
				}
			} else {
				addMessage("No se pudo generar el fichero '" + pageShortName + "'", ERROR);
			}
		} catch(Throwable th) {
		    error = true;
			addMessage("Error al generar el fichero '" + pageShortName + "' </BR> " + th.getMessage() + "", ERROR);
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fr);
            fi = null;
        }

		return error;
    }
}
