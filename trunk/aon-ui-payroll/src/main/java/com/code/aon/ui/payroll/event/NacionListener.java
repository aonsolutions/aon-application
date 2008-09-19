package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.geograficas.Nacion;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;




public class NacionListener extends ControllerAdapter {
	
	
	
@Override
public void beforeBeanAdded(ControllerEvent event)
throws ControllerListenerException {
						
		IController c = event.getController();
		Nacion n = (Nacion) c.getTo();
		String cdg = n.getCdg();		
		if (cdg.length()== 2 ){
			n.setCdg("0"+ cdg);
		}		
        if (cdg.length()== 1 ){
        	n.setCdg("00"+ cdg);
		}
		
		


}

@Override
public void beforeBeanUpdated(ControllerEvent event)
	throws ControllerListenerException {

	IController c = event.getController();
	Nacion n = (Nacion) c.getTo();
	String cdg = n.getCdg();		
	if (cdg.length()== 2 ){
		n.setCdg("0"+ cdg);
	}		
    if (cdg.length()== 1 ){
    	n.setCdg("00"+ cdg);
	}
	
	
	
}

}
