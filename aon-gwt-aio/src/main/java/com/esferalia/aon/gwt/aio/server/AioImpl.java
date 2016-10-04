package com.esferalia.aon.gwt.aio.server;

import javax.servlet.annotation.WebServlet;

import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.aio.client.IAio;
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;

@WebServlet(name = "AioGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_aio" })
public class AioImpl extends AonRemoteServiceServlet implements IAio{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void selectedMenu() {
		try{
			initFacesContext();
			SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
			smc.setLastMenuAction(null);
		}finally{
			releaseFacesContext();
		}
	}
}