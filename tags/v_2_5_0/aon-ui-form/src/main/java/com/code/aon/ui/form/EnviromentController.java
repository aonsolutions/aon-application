package com.code.aon.ui.form;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.commons.lang.ClassUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;

/**
 * Environment Controller.
 * 
 * @author Consulting & Development.
 */
public class EnviromentController {

    /** Name of the controller. */
    public static final String CONTROLLER_NAME = "enviroment";

    /** Prefix of the environment. */
    public static final String ENVIROMENT_PREFFIX = "Env_";

    private static final String INTERACTIVE_MODULE_ATTRIBUTE = "imodule";

    private static final String BUNDLE_KEY_ATTRIBUTE = "bundleKey";

    private static final String HIDE_MENU_ATTRIBUTE = "hideMenu";

    private static final String ID_ATTRIBUTE = "id";

    private static final String MENU_BUNDLE = "menuModel.bundle";

    private Properties properties;

    /**
     * Constructor.
     */
    public EnviromentController() {
        this.properties = new Properties();
    }

    /**
     * Load the environment parameters.
     * 
     * @return boolean
     */
    private boolean loadParameters() {
        boolean loaded = false;
        FacesContext fc = FacesContext.getCurrentInstance();
        Map map = fc.getExternalContext().getRequestParameterMap();
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            if (entry.getKey() != null) {
                String key = entry.getKey().toString();
                if (key.startsWith(ENVIROMENT_PREFFIX)) {
                    key = key.substring(ENVIROMENT_PREFFIX.length());
                    this.properties.put(key, entry.getValue());
                    loaded = true;
                }
            }
        }
        return loaded;
    }

    /**
     * Return map of ids.
     * 
     * @param id
     * @return Map<String, String>
     */
    private static Map<String, String> getIdMap(String id) {
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(id, "|");
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            int pos = value.indexOf('=');
            map.put(value.substring(0, pos), value.substring(pos + 1));
        }
        return map;
    }

    /**
     * Return the criteria.
     * 
     * @param controller
     * @param id
     * @return Criteria
     * @throws ManagerBeanException
     * @throws ExpressionException
     */
    public static Criteria getCriteria(BasicController controller, String id) throws ManagerBeanException, ExpressionException {
        Criteria criteria = new Criteria();
        String preffix = ClassUtils.getShortClassName(controller.getPojo());
        Map<String, String> ids = getIdMap(id);
        // TODO [EUKE] Revisar este código, Se introduce el If para soportar las
        // claves compuestas.
        // por ejemplo Items en Stock.
        if (ids.size() > 1) {
            preffix = preffix + "_" + ID_ATTRIBUTE;
        }
        // end [EUKE]
        for (Map.Entry<String, String> entry : ids.entrySet()) {
            String alias = preffix + "_" + Introspector.decapitalize(entry.getKey());
            String column = controller.getFieldName(alias);
            String value = entry.getValue();
            criteria.addExpression(column, value);
        }
        return criteria;
    }

    /**
     * Prepares the controller
     * 
     * @param imodule
     */
    private void prepareController(String imodule) {
        BasicController controller = (BasicController) getValue(imodule);
        String id = getProperties().getProperty(ID_ATTRIBUTE);
        if (id != null) {
            try {
                Criteria criteria = getCriteria(controller, id);
                controller.setCriteria(criteria);
                controller.onSearch(null);
                controller.getModel().setRowIndex(0);
                controller.onSelect(null);
            } catch (ManagerBeanException e) {
        		AonUtil.addFatalMessage( e.getMessage());
            } catch (ExpressionException e) {
        		AonUtil.addFatalMessage( e.getMessage());
            }
        } else {
            controller.onReset(null);
        }
    }

    /**
     * Return if menu has hidden attribute.
     * 
     * @return boolean
     */
    public boolean isHideMenu() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map map = fc.getExternalContext().getRequestParameterMap();
        if (map.containsKey(ENVIROMENT_PREFFIX + HIDE_MENU_ATTRIBUTE) || getProperties().containsKey(HIDE_MENU_ATTRIBUTE)) {
            return false;
        }
        return true;
    }

    /**
     * Get on form load
     *  
     * @return String
     */
    public String getOnFormLoad() {
        if (loadParameters()) {
            String imodule = getProperties().getProperty(INTERACTIVE_MODULE_ATTRIBUTE);
            if (imodule != null) {
                prepareController(imodule);
            }
        }
        return "";
    }

    /**
     * Return the properties
     * 
     * @return Properties.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Return current properties
     * 
     * @return Properties
     */
    public static Properties getCurrentProperties() {
        EnviromentController ec = (EnviromentController) getValue(CONTROLLER_NAME);
        if (ec != null) {
            return ec.getProperties();
        }
        return null;
    }

    private static Object getValue(String name) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{" + name + "}");
        return vb.getValue(ctx);
    }

    /**
     * Get label of controller
     * 
     * @return String
     */
    public String getControllerLabel() {
        String key = getProperties().getProperty(BUNDLE_KEY_ATTRIBUTE);
        if (key == null) {
            key = getProperties().getProperty(INTERACTIVE_MODULE_ATTRIBUTE);
        }
        ResourceBundle bundle = (ResourceBundle) getValue(MENU_BUNDLE);
        String label = bundle.getString(key);
        return label;
    }
}
