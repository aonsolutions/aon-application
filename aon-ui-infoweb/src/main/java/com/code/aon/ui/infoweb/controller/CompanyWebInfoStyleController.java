package com.code.aon.ui.infoweb.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoStyle;
import com.code.aon.infoweb.enumeration.WebInfoFontType;
import com.code.aon.infoweb.enumeration.WebInfoVariableType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.infoweb.util.PathUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyWebInfoStyleController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyWebInfoStyleController.class.getName());

	public boolean showPreviewModalPanel = false;

	public String template;
	
	public Integer homepage;
	
	@Override
	public void onSelect(ActionEvent event){
		cancel(event);
		super.onSelect(event);
	}

	private boolean isValidTemplateDirectory( File directory ) {
		if ( directory.exists() && directory.isDirectory() && directory.canRead() ) {
			String template = directory.getName();
			File styleDefaults = PathUtil.getStyleDefaults(template);
			if ( styleDefaults.exists() && styleDefaults.isFile() && styleDefaults.canRead() ) {
				File styleTemplate = PathUtil.getStyleTemplate(template);
				if ( styleTemplate.exists() && styleTemplate.isFile() && styleTemplate.canRead() ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<SelectItem> getTemplates() throws ManagerBeanException {
		List<SelectItem> templates = new LinkedList<SelectItem>();

		File f = PathUtil.getTemplatesPath();
		if (!f.exists()) {
			AonUtil.addErrorMessage(ICommonMessages.NO_TEMPLATE_DIRECTORY); 
		} else {
			SelectItem item = new SelectItem("","");
			templates.add(item);
			File list[] = f.listFiles();
			if (! ArrayUtils.isEmpty(list) ) {
				Arrays.sort(list);
				for (int i=0;i<list.length;i++) {
					File directory = list[i];
					if ( isValidTemplateDirectory(directory) ) {
						String template = directory.getName();
						item = new SelectItem(template, template);
						templates.add(item);
					}
				}
			}
		}
		return templates;
	}

	public List<SelectItem> getPages() throws ManagerBeanException, ExpressionException {
		List<SelectItem> pages = new LinkedList<SelectItem>();
		IManagerBean pageBean = BeanManager.getManagerBean(WebInfoPage.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(pageBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_ACTIVE), Boolean.TRUE);
		criteria.addOrder(pageBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_POSITION));
		List<ITransferObject> list = (List<ITransferObject>)pageBean.getList(criteria);
		int default_id = 0;
		SelectItem item = new SelectItem(default_id, ICommonMessages.DEFAULT_VALUE);
		pages.add(item);
		for (int i = 0; i < list.size(); i++) {
			WebInfoPage page = (WebInfoPage)list.get(i);
			int id = page.getId();
			String name = page.getName();
			item = new SelectItem(id, name);
			pages.add(item);
		}
		return pages;
	}
	
    private void sortVariables( List<WebInfoStyle> list ) {
    	Comparator<WebInfoStyle> comparator = new Comparator<WebInfoStyle>() {

			@Override
			public int compare(WebInfoStyle wis1, WebInfoStyle wis2) {
				return wis1.getName().compareTo(wis2.getName());
			}
    		
		};
    	Collections.sort( list, comparator );
    }

	private void chargeValues() {
		List<WebInfoStyle> vars = new ArrayList<WebInfoStyle>();
		Map<String,String> varMap = parseTemplateStyle();

		try {
			IManagerBean wisBean = BeanManager.getManagerBean(WebInfoStyle.class);
			Iterator<String> iter = varMap.keySet().iterator();
			while (iter.hasNext()) {
				String var = iter.next();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(wisBean.getFieldName(IEntityAlias.WEB_INFO_STYLE_VARIABLE), var);
				List<ITransferObject> list = wisBean.getList(criteria);
				WebInfoStyle wis = new WebInfoStyle();
				if (! list.isEmpty()) {
					wis = (WebInfoStyle)list.get(0);
				} else {
					wis.setVariable(var);
					WebInfoVariableType type = getVariableType(var); 
					String value = getDefaultValue(var, type);
					wis.setValue(value);
					wisBean.insert(wis);
				}
				vars.add(wis);
			}
			sortVariables(vars);
			model = new SerializableListDataModel(vars);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	public Map<String,String> parseTemplateStyle() {
		Map<String,String> styleMap = new HashMap<String,String>();
		File f = PathUtil.getStyleTemplate( getTemplate() );
		if (!f.exists()) {
			AonUtil.addErrorMessage(ICommonMessages.NO_STYLE_FILE_IN_TEMPLATE); 
		} else {
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
			    br.close();
			} catch (FileNotFoundException e) {
				LOGGER.error( e.getMessage(), e );
			} catch (IOException e) {
				LOGGER.error( e.getMessage(), e );
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
				int edx = line.indexOf('}', idx);
				if (edx > 0) {
					String var = line.substring(idx + 2, edx);
					array.add(var);
				}
			}
		}
		return array;
	}

	public void onChangeTemplate(ActionEvent event) {
		AppParamUtil.insertParameter(AppParam.WEBINFO_TEMPLATE_NAME, getTemplate());	
		chargeValues();
    }

	public void onChangeHomepage(ActionEvent event) {
		AppParamUtil.insertParameter(AppParam.WEBINFO_HOMEPAGE_ID, String.valueOf(getHomepage()));	
    }

	public String getTemplate() {
		if (template == null) {
			ApplicationParameter ap = AppParamUtil.getParameter(AppParam.WEBINFO_TEMPLATE_NAME);
			if ( ap != null ) {
				template = ap.getValue();				
				chargeValues();
			}
		}
		this.model.setRowIndex(0);
		return template;
	}

	public Integer getHomepage() {
		if (homepage == null) {
			homepage = 0;
			ApplicationParameter ap = AppParamUtil.getParameter(AppParam.WEBINFO_HOMEPAGE_ID);
			if ( ap != null ) {
				homepage = Integer.parseInt(ap.getValue());				
			}
		}
		return homepage;
	}
	
	public String getToVariableName() {
		WebInfoStyle style = (WebInfoStyle)getTo();
		return style.getName();
	}
	
    public String getRowVariableName() {
		WebInfoStyle style = (WebInfoStyle)this.model.getRowData();
		return style.getName();
    }

    public WebInfoVariableType getRowVariableType() {
    	try {
	    	if ( getModel().isRowAvailable() ) {
	    		WebInfoStyle style = (WebInfoStyle)getModel().getRowData();
	    		return getVariableType(style.getVariable());
	    	}
    	} catch (ManagerBeanException e ) {
    		LOGGER.error( e.getMessage(), e );
    	}
    	LOGGER.warn( "No row available in model: {}", this.model );	
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
    		File f = PathUtil.getStyleDefaults(getTemplate());
    		if (!f.exists()) {
    			return getDefaultValueByType(type);
    		} else {
    			properties.load(new FileInputStream(f));
    		}
    		String defaultValue = properties.getProperty(name);
    		if (defaultValue == null) {
    			return getDefaultValueByType(type);
    		}
    		return defaultValue;
        } catch (IOException e) {
        	LOGGER.error( e.getMessage(), e );
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
    	return type == WebInfoVariableType.SIZE || type == WebInfoVariableType.BORDER;
    }

    public boolean isColor() {
    	WebInfoVariableType type = getRowVariableType();
    	return type == WebInfoVariableType.COLOR || type == WebInfoVariableType.BACKGROUND_COLOR;
    }

    public boolean isImage() {
    	WebInfoVariableType type = getRowVariableType();
    	return type == WebInfoVariableType.IMAGE; 
    }

    public boolean isFont() {
    	WebInfoVariableType type = getRowVariableType();
    	return type == WebInfoVariableType.FONT; 
    }

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setHomepage(Integer homepage) {
		this.homepage = homepage;
	}

	public boolean isTemplateSelected() {
		return template != null;
	}
	
	public List<SelectItem> getImages() throws ManagerBeanException {
		List<SelectItem> images = new LinkedList<SelectItem>();
		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rattachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
		List<ITransferObject> list = rattachBean.getList(criteria);
		for (int i=0; i <list.size(); i++) {
			RegistryAttachment rattach = (RegistryAttachment)list.get(i);
			Integer id = rattach.getId();
			String name = rattach.getDescription();
			LOGGER.debug("{}", rattach);
			SelectItem item = new SelectItem(id, name);
			images.add(item);
		}
		return images;
	}

	public List<SelectItem> getFontTypes() throws ManagerBeanException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for (WebInfoFontType type : WebInfoFontType.values()) {
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type.ordinal(), name);
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
			criteria.addEqualExpression(rattachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ID), new Integer(id));
			List<ITransferObject> list = rattachBean.getList(criteria);
			if (! list.isEmpty()) {
				RegistryAttachment ra = (RegistryAttachment)list.get(0);
				name = ra.getDescription() + "." + ra.getMimeType().getExtension();
			}
		} catch (NumberFormatException n) {
			name = "blank.jpg";
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return name;
	}

	public WebInfoFontType getFontType() {
		WebInfoFontType type = WebInfoFontType.VERDANA;
		WebInfoStyle style = (WebInfoStyle)this.model.getRowData();
		try {
			if ( NumberUtils.isDigits(style.getValue())) {
				int index = Integer.parseInt(style.getValue());
				type = WebInfoFontType.values()[index];
			}
		} catch (NumberFormatException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return type;
	}

	public void onLoad( ActionEvent event ) {
		if (this.model == null) this.model = new SerializableListDataModel();
		getTemplate();
	}

	public void onShowPreview(ActionEvent event) {
		setShowPreviewModalPanel(true);
	}

	public boolean isShowPreviewModalPanel() {
		return showPreviewModalPanel;
	}

	public void setShowPreviewModalPanel(boolean showPreviewModalPanel) {
		this.showPreviewModalPanel = showPreviewModalPanel;
	}

}
