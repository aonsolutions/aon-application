package net.aonsolutions.aon.gwt.commercial.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

import net.aonsolutions.aon.gwt.commercial.client.ICommercial;

@WebServlet(name = "CommercialGwtServlet", urlPatterns = { "/aon_gwt_commercial/gwt_commercial" })
public class CommercialImpl extends AonRemoteServiceServlet implements ICommercial{

	private static final long serialVersionUID = 1L;
	
	
	public String getLoggedUser() {
		return AonServletUtils.getLoggedUser();
	}
	
	public AonData getAonData(String domainName, Integer domainId){
		Domain domain = AON.getDomain(domainName, domainId, getLoggedUser());
		User user = AON.getUser(domain.getName(), domain.getId(), getLoggedUser());
		return new AonData().setUser(user)
				.setMd5(getMd5(user.getLogin()+domain.getName()))
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