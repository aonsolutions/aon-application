/**
 * 
 */
package com.code.aon.ui.planner;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.planner.core.IncidenceType;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 26/09/2007
 *
 */
public class Utils {

	/**
	 * Returns a list of a <code>SelectItem</code> categories.
	 * 
     * @param ec
	 * @return
	 */
	public static final List<SelectItem> getCategoryTypes(EventCategory[] ec) {
        List<SelectItem> categories = new LinkedList<SelectItem>();
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for (int i = 0; i < ec.length; i++) {
			if ( ec[i] != null )
				categories.add( new SelectItem( ec[i], ec[i].getName(locale) ) );
		}
		return categories;
	}

	/**
	 * Return incidence types.
	 * 
	 * @return
	 * @throws ManagerBeanException
	 */
	public static final List<SelectItem> getIncidenceTypes() {
		List<SelectItem> incidenceTypes = new ArrayList<SelectItem>();
		incidenceTypes.add( new SelectItem( -1, "" ) );
		try { 
			List<ITransferObject> c = BeanManager.getManagerBean(IncidenceType.class).getList(null);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				IncidenceType incidenceType = (IncidenceType) iter.next();
				SelectItem item = new SelectItem(incidenceType.getId(), incidenceType.getDescription());
				incidenceTypes.add(item);
			}
		} catch (ManagerBeanException e) {
            e.printStackTrace();
		}
		return incidenceTypes;
	}

	/**
	 * Return incidence compute types.
	 * 
	 * @return
	 * @throws ManagerBeanException
	 */
	public static final List<SelectItem> getIncidenceComputeTypes() {
		List<SelectItem> incidenceCompute = new ArrayList<SelectItem>();
		String baseName = 
			AonUtil.getConfigurationController().getApplicationBundles().get( "plannerBundle" );
		ResourceBundle bundle = ResourceBundle.getBundle( baseName );
		incidenceCompute.add( new SelectItem( 99, "" ) );
		incidenceCompute.add( new SelectItem( -1, bundle.getString( "aon_planner_incidence_less" ) ) );
		incidenceCompute.add( new SelectItem( 0, bundle.getString( "aon_planner_incidence_equal" ) ) );
		incidenceCompute.add( new SelectItem( 1, bundle.getString( "aon_planner_incidence_plus" ) ) );
		return incidenceCompute;
	}

	/**
	 * Returns the formatted message.
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static final String getMessage(String key, Object[] values) {
		String baseName = 
			AonUtil.getConfigurationController().getApplicationBundles().get( "plannerBundle" );
		ResourceBundle bundle = ResourceBundle.getBundle( baseName );
		MessageFormat mf = new MessageFormat( bundle.getString( key ) );
		return mf.format( values );
	}
}
