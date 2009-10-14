package com.code.aon.ui.report.context;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.xml.sax.SAXException;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.ObjectWrapper;
import com.code.aon.common.enumeration.IMask;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.listener.SQLCollectionsListener;
import com.code.aon.ui.form.listener.SQLCollectionsListener.SQLElement;
import com.code.aon.ui.form.lookup.SQLLookupBean;

public class VContextController {

	private static final String DIGESTER_CONFIG_XML = "digester_config.xml";

	private static final Logger LOGGER = Logger.getLogger(VContextController.class.getName());

	private String name;

	private String description;

	private String controllerName;

	private String currentController;

	private VelocityContext velocityContext;

	private List<Object> list;

	public VContextController() {
		list = new LinkedList<Object>();
	}

	public Object getElement() {
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public void addElement(Object element) {
		list.add(element);
	}

	public List getElements() {
		return list;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VelocityContext getVelocityContext() {
		return velocityContext;
	}

	public void setVelocityContext(VelocityContext velocityContext) {
		this.velocityContext = velocityContext;
	}

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public void resolveVariables( InputStream xmlFile, ITransferObject to ){
		Digester dig = DigesterLoader.createDigester(VContextController.class
				.getResource(DIGESTER_CONFIG_XML));
		dig.setUseContextClassLoader(true);
		VContextController contextController = null;
		try {
			contextController = (VContextController) dig.parse(xmlFile);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error reading xml file " + xmlFile, e);
		} catch (SAXException e) {
			LOGGER.log(Level.SEVERE, "Error parsing xml file " + xmlFile, e);
		}
		this.currentController = this.controllerName;
		resolve( contextController.getElements(), to );
	}

	protected void addString( String name, String value  ) {
		String newValue = ( value == null ) ? "" : value;
		this.velocityContext.put( name, StringEscapeUtils.escapeHtml(newValue) );
	}

	protected void addObject( String name, Object value  ) {
		this.velocityContext.put( name, value );
	}

	private void resolve(List l, ITransferObject result) {
		Iterator iter = l.iterator();
		while(iter.hasNext()){
			Object obj = iter.next();
			if(obj != null){
				if(obj instanceof ReportField){
					Object value = resolveField((ReportField)obj, result, true);
					addObject(((ReportField)obj).getVelocityName(), value );
				} else if (obj instanceof ReportEnum) {
					String value = resolveEnum((ReportEnum)obj, result);
					addString(((ReportEnum)obj).getName(), value );
				} else if (obj instanceof ReportLookup) {
					String value = resolveLookup((ReportLookup)obj, result);
					addString(((ReportLookup)obj).getName(), value );
				} else if (obj instanceof ReportGroup){
					resolveGroup((ReportGroup)obj,result);
				} else if (obj instanceof ReportLabel) {
					ReportLabel label = (ReportLabel)obj;
					addString(label.getName(),label.getDescription());
				} else if (obj instanceof ReportImage) {
					ReportImage image = (ReportImage)obj;
					addString(image.getName(),image.getUrl());
				} else if (obj instanceof ReportSqlList) {
					String value = resolveSql((ReportSqlList)obj, result);
					addString(((ReportSqlList)obj).getName(), value);
				} else if(obj instanceof VContextController) {
					VContextController fieldArray = (VContextController) obj;
					try {
						resolveFieldArray( fieldArray, result );
					} catch (ManagerBeanException e) {
						LOGGER.log(Level.SEVERE, "Error resolving field array " + fieldArray.getName(), e);
					}
				}
			}
		}
	}

	//Aplicación de las máscaras que "almacenadas" en el xml de estructura
	private Object resolveField(ReportField reportField, ITransferObject to, boolean resolve) {
		Object result = "";
		String name = resolve ? reportField.getVelocityName() : reportField.getName();
		Object value = getPropertyValue(to, name);
		if ( value != null ) {
			if (resolve && reportField.isCompositeField() ) {
				result = value;
			} else if (value instanceof Boolean) {
				result = (((Boolean) value).booleanValue()?"SI":"NO");
			} else {
				IMask imask = reportField.getIMask();
				if ( imask != null ) {
					result = imask.format(value);
				} else {
					result = value.toString();
				}
			}
		}
		return result;
	}

	private String resolveEnum(ReportEnum enumeration, ITransferObject to) {
		Object value = getPropertyValue(to,enumeration.getName());
		if (value != null) {
			Iterator iter = enumeration.getEnumElements().iterator();
			while(iter.hasNext()){
				ReportEnumElement enumEl = (ReportEnumElement)iter.next();
				if(value.toString().equals(enumEl.getValue())){
					return enumEl.getLabel();
				}
			}
		}
		return null;
	}

	private String resolveLookup(ReportLookup lookup, ITransferObject to) {
		String name = this.currentController + StringUtils.capitalize(lookup.getName()) + "Lookup";
		SQLLookupBean lookupBean = (SQLLookupBean) obtainManagedBean(name);
		try {
			Map<String,Object> map = lookupBean.getCurrentMap( to );
			if ( map != null ) {
				StringBuffer result = new StringBuffer();
				String aliasPrefix = lookupBean.getAliasPrefix() + "_";
				StringTokenizer st = new StringTokenizer( lookup.getDisplayColumns(), " ," );
				while ( st.hasMoreTokens() ) {
					String column = aliasPrefix + st.nextToken();
					Object value = map.get( column );
					if ( value != null ) {
						result.append( value.toString() );
					}
					if ( st.hasMoreTokens() ) {
						result.append( " " );
					}
				}
				return result.toString();
			}
		} catch (IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "Error resolving lookup " + lookup.getName(), e);
		} catch (InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, "Error resolving lookup " + lookup.getName(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, "Error resolving lookup " + lookup.getName(), e);
		}
		return null;
	}

	private String resolveSql(ReportSqlList sql, ITransferObject to) {
		Object val = getPropertyValue( to, sql.getName() );
		if (val != null) {
			String name = this.currentController + "SQLCollections";
			SQLCollectionsListener sqlCollections = (SQLCollectionsListener) obtainManagedBean(name);
			Map<String,SQLElement> elementsMap = sqlCollections.getElements();
			SQLElement sqlElement = elementsMap.get(sql.getName());
			Iterator iter = sqlElement.getCollection().iterator();
			while(iter.hasNext()){
				SelectItem selectItem = (SelectItem)iter.next();
				if(selectItem.getValue().equals(val)){
					return selectItem.getLabel();
				}
			}
		}
		return null;
	}

	private void resolveGroup(ReportGroup group, ITransferObject result) {
		List l = group.getElements();
		resolve(l, result);
	}

	private ObjectWrapper getObjectWrapper( Object o ) {
		String value;
		if ( o != null ) {
			value = StringEscapeUtils.escapeHtml( o.toString() );
		} else {
			value = "";
		}
		return new ObjectWrapper( value );
	}

	@SuppressWarnings("unused")
	private void resolveFieldArray(VContextController array, ITransferObject result) throws ManagerBeanException {
		String oldController = this.currentController;
		this.currentController = this.currentController + "_" + array.getName();
		IController arrayController = (IController) obtainManagedBean(this.currentController);
		DataModel model = arrayController.getModel();
		List props = array.getElements();
		List<ObjectWrapper> outList = new LinkedList<ObjectWrapper>();
		List<Object> matrixList = new LinkedList<Object>();
		List list = (List)model.getWrappedData();
		Iterator iter = list.iterator();
		matrixList = new LinkedList<Object>();
		while(iter.hasNext()){
			ITransferObject to = (ITransferObject)iter.next();
			outList = new LinkedList<ObjectWrapper>();
			Iterator propIter = props.iterator();
			while(propIter.hasNext()){
				Object obj = propIter.next();
				if(obj instanceof ReportLookup){
					String value = resolveLookup(((ReportLookup)obj),to);
					outList.add( getObjectWrapper(value) );
				} else if(obj instanceof ReportField){
					Object value = resolveField((ReportField)obj, to, false);
					outList.add( getObjectWrapper(value) );
				} else if (obj instanceof ReportEnum) {
					String value = resolveEnum((ReportEnum)obj, to);
					outList.add( getObjectWrapper(value) );
				} else if (obj instanceof ReportSqlList) {
					String value = resolveSql((ReportSqlList)obj, to);
					outList.add( getObjectWrapper(value) );
				}
			}
			matrixList.add(outList);
		}
		addObject(array.getName(),matrixList);
		this.currentController = oldController;
	}

	private Object getPropertyValue( ITransferObject to, String name ) {
		Object val = null;
		try {
			val = PropertyUtils.getProperty(to, name);
		} catch (IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "Error getting value of property " + name, e);
		} catch (InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, "Error getting value of property " + name, e);
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, "Error getting value of property " + name, e);
		}
		return val;
	}

	private Object obtainManagedBean(String name) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + name + "}");
		return vb.getValue(ctx);
	}

}
