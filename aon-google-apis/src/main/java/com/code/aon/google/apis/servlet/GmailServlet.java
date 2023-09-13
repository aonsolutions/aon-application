package com.code.aon.google.apis.servlet;

import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.io.IOException;
import java.sql.Date;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.Record1;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

public class GmailServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String registry = req.getParameter("registry");
        String email = req.getParameter("email");
        String domainName = req.getParameter("domain");
        // String domain_id = req.getParameter("domain_id");
        String type = req.getParameter("type");
        Integer reg = Integer.parseInt(registry);
        Integer domainId = Integer.parseInt("1");
        Domain domain = new Domain().setName(domainName).setId(domainId);
        User user = new User().setLogin("");
        if(type.equals("baja")){
			updateRmedia(domain, user, reg, email);
        	RequestDispatcher dispatcher = getServletContext()
    				.getRequestDispatcher("/login/baja.jsp");
    			req.setAttribute("email", req.getParameter("email"));
    			req.setAttribute("type", req.getParameter("type"));
    			req.setAttribute("logo","http://"+domain+"/aonDocuments/company.logo");
    			req.setAttribute("css", "http://"+domain + "/aonResource/com/code/aon/ui/resources/facelet/login/css/login-aon.css");
    			req.setAttribute("favicon","http://"+domain + "/aonResource/8.18-SNAPSHOT/images/favicon.ico");
    			dispatcher.forward(req, resp);
        }
        else{
			updateRAddInfo(domain, user, reg, type);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/login/emailchange.jsp");
			req.setAttribute("email", req.getParameter("email"));
			req.setAttribute("type", req.getParameter("type"));
			req.setAttribute("logo","http://"+domain+"/aon-aio/aonDocuments/company.logo");
			req.setAttribute("css", "http://"+domain + "/aon-aio/aonResource/com/code/aon/ui/resources/facelet/login/css/login-aon.css");
			req.setAttribute("favicon","http://"+domain + "/aon-aio/aonResource/8.18-SNAPSHOT/images/favicon.ico");

			dispatcher.forward(req, resp);
        }    
	}
	
	public static void updateRAddInfo(Domain domain, User user, Integer registry, String email){
		CloseableAONContext ctx =null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record1<Integer>> data = ctx.getDslContext().select(RADDINFO.DOMAIN).from(RADDINFO).where(RADDINFO.REGISTRY.eq(registry)).and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL")).fetch();
			if(data.isEmpty()){
				ctx.getDslContext().insertInto(RADDINFO, RADDINFO.DOMAIN, RADDINFO.REGISTRY, RADDINFO.ATTRIBUTE, RADDINFO.VALUE, RADDINFO.VALUE_DATE)
						.values(domain.getId(),registry,"GOOGLEMAIL",email, new Date(new java.util.Date().getTime())).execute();
			}
			else ctx.getDslContext().update(RADDINFO)
						.set(RADDINFO.VALUE,email)
						.where(RADDINFO.REGISTRY.eq(registry))
							.and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL"))
						.execute();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateRmedia(Domain domain, User user, Integer registry, String email){
		CloseableAONContext ctx =null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			ctx.getDslContext().update(RMEDIA)
			.set(RMEDIA.COMMERCIAL,(byte)0)
			.where(RMEDIA.REGISTRY.eq(registry))
				.and(RMEDIA.MEDIA.eq((byte) 4))
				.and(RMEDIA.VALUE.eq(email))
			.execute();
			
			ctx.getDslContext().delete(RADDINFO).where(RADDINFO.REGISTRY.eq(registry)).and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL")).execute();

			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
}
