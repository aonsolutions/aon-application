package com.code.aon.ui.company.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * Controller used in the workPlace maintenance.
 */
public class WorkPlaceController extends BasicController {
	
	public static final String MANAGER_BEAN_NAME = "workplace";

    /**
     * Gets the current working place.
     * 
     * @return
     */
    public WorkPlace getWorkPlace() {
    	return (WorkPlace) getTo();
    }

	/**
	 * Gets a list of company working Places.
	 * 
	 * @return a list with all workPlaces.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getCompanyWorkPlaces() throws ManagerBeanException {
		List<SelectItem> workplaces = new ArrayList<SelectItem>();
		workplaces.add( new SelectItem( -1, "" ) );
		workplaces.addAll( getAll() );
		return workplaces;
	}

	/**
	 * Gets a list with all workPlaces.
	 * 
	 * @return a list with all workPlaces.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getAll() throws ManagerBeanException {
		List<SelectItem> workplaces = new ArrayList<SelectItem>();
		Iterator iter = ( (List) super.getModel().getWrappedData() ).iterator();
		while (iter.hasNext()) {
			WorkPlace wp = (WorkPlace) iter.next();
			SelectItem item = new SelectItem( wp.getId(), wp.getDescription() );
			workplaces.add(item);
		}
		return workplaces;
	}

	/**
	 * Execute a search each time the WorkPlace menu option is pressed.
	 * 
	 * @param event
	 */
	public void onSearch(MenuEvent event) {
        this.onSearch((ActionEvent)event);
    }

}
