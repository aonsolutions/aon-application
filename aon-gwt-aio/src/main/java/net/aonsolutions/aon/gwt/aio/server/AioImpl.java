package net.aonsolutions.aon.gwt.aio.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

import net.aonsolutions.aon.gwt.aio.client.IAio;

@WebServlet(name = "AioGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_aio" })
public class AioImpl extends AonStatelessRemoteServiceServlet implements IAio{

	private static final long serialVersionUID = 1L;
	
	public AonData getAonData(String domainName, Integer domainId, String login){
		Domain domain = AON.getDomain(domainName, domainId, login);
		User user = AON.getUser(domain.getName(), domain.getId(), login);
		Integer operator = AON.getTaskHolder(domain.getName(), domainId, login, 
				f -> f.getDomainProperty().eq(domainId).and(f.getUserIdProperty().eq(user.getId()))).getId();
		return new AonData().setUser(user)
				.setMd5(getMd5(user.getLogin()+domain.getName()))
				.setDomain(domain)
				.setUserOperator(operator);
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