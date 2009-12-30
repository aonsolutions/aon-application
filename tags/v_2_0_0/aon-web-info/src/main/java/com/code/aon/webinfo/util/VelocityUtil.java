package com.code.aon.webinfo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.webinfo.velocity.VelocityConstants;

public class VelocityUtil extends VelocityEngine implements VelocityConstants {
    
	public static final int INFO = 0;

	public static final int ERROR = 1;

	public static final int WARN = 2;

	private String template_path;

	private String temporal_path;

	private VelocityContext context = new VelocityContext();
	
	public void setTemplate_path(String template_path) {
		this.template_path = template_path;
		File f = new File(template_path + "/index.vm");
		if (f.exists()) addMessage("Plantilla encontrada", VelocityUtil.INFO);
		else addMessage("No se han encontrado plantillas en '" + template_path + "'", ERROR);
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
		System.out.println(msg);
/*		if (type == INFO) 
			AonUtil.addInfoMessage(msg);
		else if (type == ERROR)
			AonUtil.addErrorMessage(msg);
		else if (type == WARN)
			AonUtil.addWarningMessage(msg);
		else
			AonUtil.addFatalMessage(msg);*/
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
				addMessage("Fichero de plantilla 'index.vm' no encontrado.", ERROR);
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
