/**
 * 
 */
package com.code.aon.ui.planner;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;


/**
 * Manages application controller classes.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21/11/2006
 *
 */
public class ControllerUtil {

    /**
	 * Returns <code>CalendarManagerBean</code> Controller.
     *  
     * @return
     */
    public static final CalendarManagerBean getCalendarManagerBean() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + CalendarManagerBean.MANAGER_BEAN_NAME + "}");
		return (CalendarManagerBean) vb.getValue(ctx);
    }

    /**
	 * Returns <code>PlannerController</code>.
     *  
     * @return
     */
    public static final PlannerController getPlannerController() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + PlannerController.MANAGER_BEAN_NAME + "}");
		return (PlannerController) vb.getValue(ctx);
    }

    /**
	 * Returns <code>WorkingTimeController</code>.
     *  
     * @return
     */
    public static final WorkingTimeController getWorkingTime() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + WorkingTimeController.MANAGER_BEAN_NAME + "}");
		return (WorkingTimeController) vb.getValue(ctx);
    }

    /**
	 * Returns <code>IncidencesController</code>.
     *  
     * @return
     */
	public static final IncidencesController getIncidences() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + IncidencesController.MANAGER_BEAN_NAME + "}");
		return (IncidencesController) vb.getValue(ctx);
	}

    /**
	 * Returns <code>EventManager</code> Controller.
	 * 
	 * @return
	 */
	public static final EventManager getEventManager() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + EventManager.MANAGER_BEAN_NAME + "}");
		return (EventManager) vb.getValue(ctx);
	}

	/**
	 * Returns <code>ReassignManager</code> Controller.
	 * 
	 * @return
	 */
	public static final ReassignManager getReassignManager() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + ReassignManager.MANAGER_BEAN_NAME + "}");
		return (ReassignManager) vb.getValue(ctx);
	}

	/**
	 * Returns Planner messages bundle.
	 * 
	 * @return
	 */
	public static final ResourceBundle getPlannerBundle() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return ResourceBundle.getBundle( "com.code.aon.ui.planner.i18n.messages", locale );
	}

}
