package com.esferalia.aon.gwt.aio.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.annotation.WebServlet;

import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.aio.client.IAio;
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

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
	
	public String getLoggedUser() {
		return AonServletUtils.getLoggedUser();
	}
	
	public AonData getAonData(String domainName, Integer domainId){
		Domain domain = AON.getDomain(domainName, domainId, getLoggedUser());
		User user = AON.getUser(domain.getName(), domain.getId(), getLoggedUser());
		return new AonData().setUser(user)
				.setMd5(getMd5(getLoggedUser()+domain.getName()))
				.setDomain(domain);
	}
	
	public String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update(str.getBytes());
	    byte byteData[] = md.digest();
	    //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	     	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }       
        return sb.toString();
	}
}