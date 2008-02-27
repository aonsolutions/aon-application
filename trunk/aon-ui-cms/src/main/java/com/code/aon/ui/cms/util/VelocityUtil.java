package com.code.aon.ui.cms.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.util.AonUtil;

public class VelocityUtil extends VelocityEngine implements Constants {
    
	public static final int INFO = 0;

	public static final int ERROR = 1;

	public static final int WARN = 2;

	private String template_path;
	
	private VelocityContext context = new VelocityContext();
	
	public void setTemplate_path(String template_path) {
		this.template_path = template_path;
		File f = new File(template_path + "/" + Templates.INDEX.getTemplateName());
		if (f.exists()) addMessage("Plantilla encontrada", VelocityUtil.INFO);
		else addMessage("No se han encontrado plantillas en '" + template_path + "'", ERROR);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void finalize() {
    	/*
    	try{
        	Iterator iter = Arrays.asList(this.getContext().getKeys()).iterator();
        	while (iter.hasNext()){
        		this.remove(""+iter.next());
        	}
        	this.finalize();
    	}catch (RuntimeException e) {
		}
		*/
    }

	public void addMessage(String msg, int type) {
		if (type == INFO)
			AonUtil.addInfoMessage(" INFO: " + msg);
		else if (type == ERROR)
			AonUtil.addErrorMessage(" ERROR: " + msg);
		else if (type == WARN)
			AonUtil.addWarningMessage(" WARNING: " + msg);
		else
			AonUtil.addFatalMessage(msg);
	}

	public void put(String key, Object value) {
		this.context.put(key, value);
	}

	public void remove(String key) {
		this.context.remove(key);
	}

    public boolean generate(String template, String page) {
		boolean error = false;

        File fi = new File(template);
        BufferedReader reader = null;
        FileReader fr = null;

        File fo = new File(page);
        BufferedWriter writer = null;
        FileWriter fw = null;

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
                    fw = new FileWriter(fo);
                    writer = new BufferedWriter(fw);

                    this.evaluate(context, writer, "¡AON-CMS!", reader);
					writer.flush();
					addMessage("Página " + page + " generada con exito", INFO);
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

}
