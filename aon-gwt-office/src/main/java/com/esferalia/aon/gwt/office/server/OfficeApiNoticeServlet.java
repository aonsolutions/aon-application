package com.esferalia.aon.gwt.office.server;

import static com.esferalia.aon.gwt.office.shared.ActionEnum.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author amtzdelagos
 * 
 * Servlet que gestiona todo lo relacionado con las Notice en la Base de Datos. 
 * Create & Get Notices.
 *
 */

// Adictos-alared.blogspot.mx

@MultipartConfig
@WebServlet(name = "Office Api Notice Servlet", urlPatterns = {"/aon_gwt_office/api"})
public class OfficeApiNoticeServlet<T> extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, Consumer<HttpServletRequest>> functions = new HashMap<String, Consumer<HttpServletRequest>>() {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(loaduser.get(), OfficeApiNoticeServlet::getUser);			
			put(createissue.get(), OfficeApiNoticeServlet::createIssue);
			put(loadopenissues.get(), OfficeApiNoticeServlet::getOpenIssues);
			put(loadclosedissues.get(), OfficeApiNoticeServlet::getCloseIssues);
			put(loadallissues.get(), OfficeApiNoticeServlet::getAllIssues);
			put(editissue.get(), OfficeApiNoticeServlet::editIssue);
			put(addissuelabel.get(), OfficeApiNoticeServlet::addLabel2Issue);
			put(deleteissue.get(), OfficeApiNoticeServlet::deleteIssue);
			put(getcomments.get(), OfficeApiNoticeServlet::getIssueComments);
			put(addcomment.get(), OfficeApiNoticeServlet::createIssueComment);
			put(editcomment.get(), OfficeApiNoticeServlet::editIssueComment);
			put(deletecomment.get(), OfficeApiNoticeServlet::deleteIssueComment);
			put(loadlabels.get(), OfficeApiNoticeServlet::getLabels);
			put(createlabel.get(), OfficeApiNoticeServlet::createLabel);
			put(savelabel.get(), OfficeApiNoticeServlet::saveLabel);
			put(deletelabel.get(), OfficeApiNoticeServlet::deleteLabel);
		}
	};

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String parameter = req.getParameter("action");
		if ( parameter == null)
			throw new NullPointerException("Error en mapa de acciones");
		
		functions.get(parameter).accept(req);
	}
	
	private static void getUser(HttpServletRequest req) {
		System.out.println("Obteniendo User ... ");
		
	}
	
	private static void getOpenIssues(HttpServletRequest req) {
		System.out.println("Obteniendo open issues ...");
	}
	
	private static void getCloseIssues(HttpServletRequest req) {
		System.out.println("Obteniendo Closed issues ... ");
	}
	
	private static void getAllIssues(HttpServletRequest req) {
		System.out.println("Obteniendo All Issues ... ");
	}
	
	private static void createIssue(HttpServletRequest req) {
		System.out.println("Creando nueva issue ... ");
	}
	
	private static void editIssue(HttpServletRequest req) {
		System.out.println("Editando issue ... ");
	}
	
	private static void addLabel2Issue(HttpServletRequest req) {
		System.out.println("Agregando etiqueta a issue ... ");
	}
	
	private static void deleteIssue(HttpServletRequest req) {
		System.out.println("Borrando issue ... ");
	}
	
	private static void getIssueComments(HttpServletRequest req) {
		System.out.println("Obteniendo Comentarios de issue ... ");
	}
	
	private static void createIssueComment(HttpServletRequest req) {
		System.out.println("Creando nuevo comentario ... ");
	}
	
	private static void editIssueComment(HttpServletRequest req) {
		System.out.println("Editando comentario de issue ... ");
	}
	
	private static void deleteIssueComment(HttpServletRequest req) {
		System.out.println("Borrando issue comment ... ");
	}
	
	private static void getLabels(HttpServletRequest req) {
		System.out.println("Obteniendo labels ... ");
	}
	
	private static void createLabel(HttpServletRequest req) {		
		System.out.println("Creando nueva label ... ");
	}
	
	private static void saveLabel(HttpServletRequest req) {
		System.out.println("Salvando nueva label ... ");
	}
	
	private static void deleteLabel(HttpServletRequest req) {
		System.out.println("Borrando label ... ");
	}
	
	private String getJsonObject(HttpServletRequest req) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String line = null;
		BufferedReader reader = req.getReader();
		
		while ((line = reader.readLine()) != null)
			buffer.append(line);
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param req
	 * @return param of action
	 * Esto nunca debe fallar. La url siempre contiene el parametro Action.
	 * Metodo sucio hasta que consiga sacar el atributo de la request.
	 */
	
	private String getActionFromUrl(HttpServletRequest req) {
		String uri = req.getRequestURI().substring(req.getServletPath().length());
		int pos1 = uri.indexOf("&action=");
		String subUri = uri.substring(pos1, uri.length());
		String[] cadena = subUri.split("=");
		
		return cadena[1];
	}

}
