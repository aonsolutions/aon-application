package com.code.aon.ui.infoweb.util;

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
	
	public File getTemporalDirectory() {
		return temporalDirectory;
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
        this.setProperty(Velocity.RUNTIME_LOG_REFERENCE_LOG_INVALID, Boolean.TRUE.toString());
        try {
        	this.init();
        } catch (Throwable th) {
            LOGGER.log(Level.SEVERE, th.getMessage(), th);
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
		LOGGER.fine( "Key: " + key + ", Value: " + value );
		this.context.put(key, value);
	}

	public void remove(String key) {
		this.context.remove(key);
	}

    public boolean generate(File template, File page) {
		boolean error = false;
		LOGGER.fine( "Template: " + template + " -> " + page );
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
			if (!template.exists()) {
				error = true;
			} else {
				reader = new BufferedReader(new FileReader(template));
			}
			if (!error) {
				try {
					writer = new BufferedWriter(new FileWriter(page));
                    this.evaluate(context, writer, "AON-INFOWEB", reader);
					writer.flush();
				} catch(Throwable th) {
				    error = true;
					addMessage("Error al evaluar el contexto en el fichero '" + template + "' <BR/>" + th.getMessage(), ERROR);
				}
			} else {
				addMessage("No se pudo generar el fichero '" + page + "'", ERROR);
			}
		}  catch(Throwable th) {
		    error = true;
			addMessage("Error al generar el fichero '" + page + "' </BR> " + th.getMessage() + "", ERROR);
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(reader);
        }
		return error;
	}

    public boolean generate(String page) {
        File template = new File(this.templateDirectory, INDEX_TEMPLATE);
    	File file = new File(this.temporalDirectory, page);
    	return generate(template, file);
	}
    
    public boolean generate(String template, String page) {
        File templateFile = new File(this.templateDirectory, template);
    	File file = new File(this.temporalDirectory, page);
    	return generate(templateFile, file);
	}
    
    public boolean generateCSS() {
		File templateCss = new File( this.templateDirectory, CSS_PATH );
        File template = new File(templateCss, STYLE_TEMPLATE);

        File temporalCss = new File( this.temporalDirectory, CSS_PATH );
        File file = new File(temporalCss, "style.css");

        return generate(template, file);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
