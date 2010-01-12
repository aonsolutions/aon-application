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

	private File outputDirectory;
	
	private String template;

	private VelocityContext context = new VelocityContext();
	
	public void setTemplateDirectory(File templateDirectory) {
		this.templateDirectory = templateDirectory;
	}
	
	public void setOutputDirectory(File temporalDirectory) {
		this.outputDirectory = temporalDirectory;
	}
	
	public File getOutputDirectory() {
		return outputDirectory;
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
		boolean error = true;
		LOGGER.fine( "Template: " + template + " -> " + page );
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
			if ( template.exists() ) {
				try {
					reader = new BufferedReader(new FileReader(template));					
					writer = new BufferedWriter(new FileWriter(page));
                    this.evaluate(context, writer, "AON-INFOWEB", reader);
					writer.flush();
					error = false;
				} catch (Throwable th) {
				    LOGGER.log(Level.SEVERE, th.getMessage(), th);
					addMessage("Error al evaluar el contexto en el fichero '" + template + "' <BR/>" + th.getMessage(), ERROR);
				}
			} else {
				addMessage("No se han encontrado la plantilla '" + template + "'", ERROR);
			}
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(reader);
        }
		return error;
	}

    public boolean generate(String page) {
        File template = new File(this.templateDirectory, INDEX_TEMPLATE);
    	File file = new File(this.outputDirectory, page);
    	return generate(template, file);
	}
    
    public boolean generate(String template, String page) {
        File templateFile = new File(this.templateDirectory, template);
    	File file = new File(this.outputDirectory, page);
    	return generate(templateFile, file);
	}
    
    public boolean generateCSS() {
		File templateCss = new File( this.templateDirectory, CSS_PATH );
        File template = new File(templateCss, STYLE_TEMPLATE);

        File temporalCss = new File( this.outputDirectory, CSS_PATH );
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
