package com.code.aon.ui.infoweb.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.infoweb.WebInfoStyle;
import com.code.aon.infoweb.dao.IWebInfoAlias;
import com.code.aon.infoweb.enumeration.WebInfoFontType;
import com.code.aon.infoweb.enumeration.WebInfoVariableType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.util.AonUtil;

public class CompanyWebInfoStyleController extends BasicController implements VelocityConstants {
	
	private static final Logger LOGGER = Logger.getLogger(CompanyWebInfoStyleController.class.getName());
	
	public String template;
	
	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
		cancel(event);
		super.onSelect(event);
	}

	public List<SelectItem> getTemplates() throws ManagerBeanException {
		List<SelectItem> templates = new LinkedList<SelectItem>();

		String path = TEMPLATE_PATH;
		File f = new File(path);
		if (!f.exists()) {
			AonUtil.addErrorMessage("ERROR: No existe el directorio de plantillas. Contacte con su administrador."); 
		}
		else {
			File directories[] = f.listFiles();
			SelectItem item = new SelectItem("","");
			templates.add(item);
			for (int i=0;i<directories.length;i++) {
				File temp = directories[i];
				if (temp.isDirectory()) {
					String template = temp.getName();
					item = new SelectItem(template, template);
					templates.add(item);
				}
			}
		}
		return templates;
	}

	public void chargeValues() {
		List<ITransferObject> vars = new ArrayList<ITransferObject>();
		HashMap<String,String> varMap = parseTemplateStyle();

		try {
			IManagerBean wisBean = BeanManager.getManagerBean(WebInfoStyle.class);
			Iterator<String> iter = varMap.keySet().iterator();
			while (iter.hasNext()) {
				String var = iter.next();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(wisBean.getFieldName(IWebInfoAlias.WEB_INFO_STYLE_VARIABLE), var);
				List<ITransferObject> list = wisBean.getList(criteria);
				WebInfoStyle wis = new WebInfoStyle();
				if (list.size() > 0) {
					wis = (WebInfoStyle)list.get(0);
				}
				else {
					wis.setVariable(var);
					WebInfoVariableType type = getVariableType(var); 
					String value = getDefaultValue(var, type);
					wis.setValue(value);
					wisBean.insert(wis);
				}
				vars.add(wis);
			}
			model = new ListDataModel(vars);
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	public HashMap<String,String> parseTemplateStyle() {
		HashMap<String,String> styleMap = new HashMap<String,String>();
		String path = TEMPLATE_PATH + "/" + getTemplate() + "/" + CSS_PATH + "/" + CSS_STYLE_FILE;
		File f = new File(path);
		if (!f.exists()) {
			AonUtil.addErrorMessage("ERROR: No existe el fichero de estilos para esta plantilla. Contacte con su administrador."); 
		}
		else {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
			    String line = br.readLine();
			    while (line != null) {
			    	ArrayList<String> found = parseLine(line);
			    	for (int i = 0; i < found.size(); i++) {
			    		String var = found.get(i);
			    		if (!styleMap.containsKey(var) && !styleMap.containsKey(var.toLowerCase())) {
			    			styleMap.put(var, "");
			    		}
			    	}
			    	line = br.readLine();
			    }
			
			} catch (FileNotFoundException e) {
				LOGGER.log( Level.SEVERE, e.getMessage(), e );
			} catch (IOException e) {
				LOGGER.log( Level.SEVERE, e.getMessage(), e );
			}
		}
		return styleMap;
	}

	public ArrayList<String> parseLine(String line) {
		String types[] = {"${img_", "${bgcolor_", "${color_", "${size_", "${font_", "${border_"};
		ArrayList<String> array = new ArrayList<String>();
		for (int i=0;i<types.length;i++) {
			String cad = types[i];
			int idx = line.indexOf(cad);
			if (idx > 0) {
				int edx = line.indexOf("}", idx);
				if (edx > 0) {
					String var = line.substring(idx + 2, edx);
					array.add(var);
				}
			}
		}
		return array;
	}

	public void onChangeTemplate(ActionEvent event) {
    	//Guardar el template en constantes
		try {
			IManagerBean apBean = BeanManager.getManagerBean(ApplicationParameter.class);
			ApplicationParameter ap = new ApplicationParameter();
			ap.setName(TEMPLATE_NAME_PARAM);
			ap.setValue(getTemplate());
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(apBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), TEMPLATE_NAME_PARAM);
			List<ITransferObject> list = apBean.getList(criteria);
			if (list.size() > 0) apBean.update(ap);
			else apBean.insert(ap);
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		//Cargar valores del template actual
		chargeValues();
    }

	public String getTemplate() {
		if (template == null) {
			try {
				IManagerBean apBean = BeanManager.getManagerBean(ApplicationParameter.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(apBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), TEMPLATE_NAME_PARAM);
				List<ITransferObject> list = apBean.getList(criteria);
				if (list.size() > 0) {
					ApplicationParameter ap = (ApplicationParameter)list.get(0);
					template = ap.getValue();
					chargeValues();
				}
			} catch (ManagerBeanException e) {
				LOGGER.log( Level.SEVERE, e.getMessage(), e );
			}
		}
		this.model.setRowIndex(0);
		return template;
	}

	public String getToVariableName() {
		WebInfoStyle style = (WebInfoStyle)getTo();
		return getVariableName(style.getVariable());
	}
	
    public String getRowVariableName() {
		WebInfoStyle style = (WebInfoStyle)this.model.getRowData();
		return getVariableName(style.getVariable());
    }

    public String getVariableName(String text) {
		text = text.substring(text.indexOf("_")+1);
		text = text.replaceAll("_", " ");
		return text;
    }
    
    public WebInfoVariableType getRowVariableType() {
    	try {
	    	if ( getModel().isRowAvailable() ) {
	    		WebInfoStyle style = (WebInfoStyle)getModel().getRowData();
	    		return getVariableType(style.getVariable());
	    	}
    	} catch (ManagerBeanException e ) {
    		LOGGER.log( Level.SEVERE, e.getMessage(), e );
    	}
    	LOGGER.severe( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ERROR OBTENIENDO getRowData de "+this.model+"" );	
		return WebInfoVariableType.IMAGE;
    }

    public void setRowVariableType(WebInfoVariableType a) {
    }

    public WebInfoVariableType getVariableType(String text) {
	    WebInfoVariableType wvt[] = WebInfoVariableType.values();
		for (int i = 0;i<wvt.length;i++) {
			if (text.startsWith(wvt[i].getPrefix())) {
				return wvt[i];
			}
		}
		return null;
    }

    public String getDefaultValue(String name, WebInfoVariableType type) {
        Properties properties = new Properties();
        try {
    		String path = TEMPLATE_PATH + "/" + getTemplate() + "/" + CSS_STYLE_DEFAULTS;
    		File f = new File(path);
    		if (!f.exists()) return getDefaultValueByType(type);
    		else properties.load(new FileInputStream(f));
    		String defaultValue = properties.getProperty(name);
    		if (defaultValue == null) return getDefaultValueByType(type);
    		return defaultValue;
        } catch (IOException e) {
        	LOGGER.log( Level.SEVERE, e.getMessage(), e );
        	return getDefaultValueByType(type);
        }
    }
    
    public String getDefaultValueByType(WebInfoVariableType type) {
    	return type.getDefaultValue();
    }

    public String getIconByType() {
    	WebInfoVariableType type = getRowVariableType();
    	return type.getPrefix() + ".gif";
    }
    
    public boolean isNumber() {
    	WebInfoVariableType type = getRowVariableType();
    	if (type == WebInfoVariableType.SIZE || type == WebInfoVariableType.BORDER) return true;
    	else return false; 
    }

    public boolean isColor() {
    	WebInfoVariableType type = getRowVariableType();
    	if (type == WebInfoVariableType.COLOR || type == WebInfoVariableType.BACKGROUND_COLOR) return true;
    	else return false; 
    }

    public boolean isImage() {
    	WebInfoVariableType type = getRowVariableType();
    	if (type == WebInfoVariableType.IMAGE) return true;
    	else return false; 
    }

    public boolean isFont() {
    	WebInfoVariableType type = getRowVariableType();
    	if (type == WebInfoVariableType.FONT) return true;
    	else return false; 
    }

	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isTemplateSelected() {
		return template != null;
	}
	
	public List<SelectItem> getImages() throws ManagerBeanException {
		List<SelectItem> images = new LinkedList<SelectItem>();
		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
		List<ITransferObject> list = rattachBean.getList(criteria);
		for (int i=0; i <list.size(); i++) {
			RegistryAttachment rattach = (RegistryAttachment)list.get(i);
			Integer id = rattach.getId();
			String name = rattach.getDescription();
			LOGGER.fine(">>>>>>>>>>>>>> " + id + " --- " + name + " <<<<<<<<<<<<<<<<");
			SelectItem item = new SelectItem(id, name);
			images.add(item);
		}
		return images;
	}

	public List<SelectItem> getFontTypes() throws ManagerBeanException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;;
		for (WebInfoFontType type : WebInfoFontType.values()) {
			String name = type.getName(locale);
			item = new SelectItem(type.ordinal(), name);
			types.add(item);
		}
		return types;
	}

	public String getRattachName() {
		WebInfoStyle style = (WebInfoStyle)this.model.getRowData();
		String id = style.getValue();
		String name = "Sin descripcion";
		try {
			IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_ID), new Integer(id));
			List<ITransferObject> list = rattachBean.getList(criteria);
			if (list.size() > 0) {
				RegistryAttachment ra = (RegistryAttachment)list.get(0);
				name = ra.getDescription() + "." + ra.getMimeType().getExtension();
			}
		} catch (NumberFormatException n) {
			name = "blank.jpg";
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		return name;
	}

	public String getFontName() {
		WebInfoStyle style = (WebInfoStyle)this.model.getRowData();
		try {
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.ARIAL.ordinal()) {
				return WebInfoFontType.ARIAL.getName();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.TIMES.ordinal()) {
				return WebInfoFontType.TIMES.getName();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.TREBUCHET.ordinal()) {
				return WebInfoFontType.TREBUCHET.getName();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.VERDANA.ordinal()) {
				return WebInfoFontType.VERDANA.getName();
			}

		} catch (NumberFormatException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		return "Sin tipo";
	}

	public String getFontType() {
		WebInfoStyle style = (WebInfoStyle)this.model.getRowData();
		try {
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.ARIAL.ordinal()) {
				return WebInfoFontType.ARIAL.getValue();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.TIMES.ordinal()) {
				return WebInfoFontType.TIMES.getValue();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.TREBUCHET.ordinal()) {
				return WebInfoFontType.TREBUCHET.getValue();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.VERDANA.ordinal()) {
				return WebInfoFontType.VERDANA.getValue();
			}

		} catch (NumberFormatException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		return "Verdana";
	}

	public void onLoad( ActionEvent event ) {
		if (this.model == null) this.model = new ListDataModel();
		getTemplate();
	}
	
}
