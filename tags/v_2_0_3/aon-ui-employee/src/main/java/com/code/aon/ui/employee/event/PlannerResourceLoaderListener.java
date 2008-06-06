/**
 * 
 */
package com.code.aon.ui.employee.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.apache.myfaces.shared_tomahawk.util.MessageUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Resource;
import com.code.aon.company.resources.ResourceManager;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.planner.IncidencesController;
import com.code.aon.ui.planner.Utils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 25/09/2007
 *
 */
public class PlannerResourceLoaderListener extends ControllerAdapter {

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IncidencesController ic = (IncidencesController) event.getController();
        try {
    		boolean found = false;
    		ic.setItemResources( new ArrayList<SelectItem>() );
    		ic.setResources( new ArrayList<Resource>() );
    		List l = ResourceManager.getResourceManager().getResources();
    		if ( l.size() > 0 ) {
    			found = true;
    			Iterator iter = l.iterator();
    			while (iter.hasNext()) {
    				Resource r = (Resource) iter.next();
    				ic.getResources().add( r );
    				SelectItem item = new SelectItem( r.getId(), r.getOwner() );
    				ic.getItemResources().add(item);
    			}
    		}
    		if ( found ) {
    			ic.initialize( (Resource) ic.getResources().get(0), true );
    		} else {
    			ic.setEnabled(false);
    			ic.setResourceChangingEnabled( true );
    			String msg = Utils.getMessage( "aon_planner_resource_not_found", null );
    			MessageUtils.addMessage( FacesMessage.SEVERITY_INFO, msg, null );
    		}
		} catch (ManagerBeanException e) {
			ic.setEnabled(false);
		}
	}

//	/* (non-Javadoc)
//	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
//	 */
//	@Override
//	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
//		IncidencesController ic = (IncidencesController) event.getController();
//		IEvent ievent = ic.getEvent();
//		String id = ievent.getId().substring( (ievent.getId().indexOf( '.' ) + 1), ievent.getId().length() );
//		Integer resourceId = Integer.valueOf( id.substring( 0, id.indexOf( '_' ) ) );
//		try {
//			IResource r = ResourceManager.getResourceManager().getResource( resourceId );
//    		if ( r != null ) {
//    			ic.setResourceChangingEnabled( false );
//    			ic.setResource( r );
//    		} else {
//    			ic.setEnabled(false);
//    			String msg = Utils.getMessage( "aon_planner_resource_not_found", null );
//    			MessageUtils.addMessage( FacesMessage.SEVERITY_INFO, msg, null );
//    		}
//		} catch (ManagerBeanException e) {
//			ic.setEnabled(false);
//		}
//	}

}