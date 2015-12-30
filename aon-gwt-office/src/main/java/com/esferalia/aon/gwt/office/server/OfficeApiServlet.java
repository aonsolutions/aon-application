package com.esferalia.aon.gwt.office.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;

/**
 * 
 * @author amtzdelagos
 * 
 *         Servlet que gestiona todo lo relacionado con las Notice en la Base de
 *         Datos. Create & Get Notices.
 *
 */

@MultipartConfig
@WebServlet(name = "Office Api Servlet", urlPatterns = {
		"/aon_gwt_office/api" })
public class OfficeApiServlet extends HttpServlet {

	// ------------------------------------------------------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static interface HttpRequestHandler {

		boolean accept(HttpServletRequest req);

		void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException;
	}

	private static abstract class SimpleRequestHandler
			implements HttpRequestHandler {

		private String action;

		public SimpleRequestHandler(String action) {
			this.action = action;
		}

		@Override
		public boolean accept(HttpServletRequest req) {
			String requestAction = getRequestAction(req);
			return action.equalsIgnoreCase(requestAction);
		}
	}

	private static abstract class RegExpRequestHandler
			implements HttpRequestHandler {

		private Pattern pattern;
		private Matcher matcher;

		public RegExpRequestHandler(String regexp) {
			this.pattern = Pattern.compile(regexp);
		}

		@Override
		public boolean accept(HttpServletRequest req) {
			String action = getRequestAction(req);
			this.matcher = pattern.matcher(action);
			return matcher.matches();
		}

		protected String group(int group) {
			return matcher.group(group);
		}
	}

	private static class GetUserRequestHandler implements HttpRequestHandler {
		@Override
		public boolean accept(HttpServletRequest req) {
			return false;
		}

		@Override
		public void handler(HttpServletRequest req,
				HttpServletResponse resp) throws ServletException, IOException {
			System.out.println("Obteniendo User ... ");
		}
	}
	
	private static class GetAvaiableAssignees extends RegExpRequestHandler {
		
		public GetAvaiableAssignees() {
			super("/repos/(.+)/(.+)/assignees");
		}
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("This call lists all the available assignees to "
					+ "Owner: " + group(1)
					+ " Repo " + group(2)				
					+ " which issues may be assigned.");
		}
	}
	
	private static class GetCheckAssignees extends RegExpRequestHandler {
		
		public GetCheckAssignees() {
			super("/repos/(.+)/(.+)/assignees/(.+)");
		}
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("This call lists all the available assignees to "
					+ "Owner: " + group(1)
					+ " Repo " + group(2)
					+ " Assignee: " + group(3)
					+ " which issues may be assigned.");
		}
	}

	private static class GetAllIssuesRequestHandler
			extends RegExpRequestHandler {

		public GetAllIssuesRequestHandler() {
			super("/repos/(.+)/(.+)/issues");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
			// TODO: 
			// EN UN FUTURO PUEDEN SER OTROS STATE.			
			
			System.out.println("Preparado Lanzamiento..");
			switch (req.getParameter("state")) {
			case "open":
				getOpenNotices(req, resp);				
				break;
			case "closed":
				getClosedNotices(req, resp);
			case "all":
				getAllNotices(req, resp);
				break;
			default:
				getAllNotices(req, resp);
				break;
			}			
	
		}
	}

	private static class GetUserIssuesRequestHandler
			extends SimpleRequestHandler {

		public GetUserIssuesRequestHandler() {
			super("/user/issues");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println(
					"List all issues across owned and member repositories for the authenticated user");
		}
	}

	private static class GetOrgIssuesRequestHandler
			extends RegExpRequestHandler {

		public GetOrgIssuesRequestHandler() {
			super("/orgs/(.+)/issues");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("List all issues for a given organization ( "
					+ group(1) + " ) for the authenticated user:");
		}

	}

	private static class ListIssuesCommentsRequestHandler
			extends RegExpRequestHandler {

		public ListIssuesCommentsRequestHandler() {
			super("/repos/(.+)/(.+)/issues/(\\d+)/comments");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("List comments on an issue ( owner:" + group(1)
					+ ", repo:" + group(2) + ", number:" + group(3) + ")");
		}
	}
	
	private static class GetCommentsOnRepository extends RegExpRequestHandler {
		
		public GetCommentsOnRepository() {
			super("/repos/(.+)/(.+)/issues/comments");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("By default, Issue Comments are ordered by ascending ID."
					+ " Owner: " + group(1)
					+ " Repo: " + group(2));
		}
	}
	
	private static class GetSingleComment extends RegExpRequestHandler {
		
		public GetSingleComment() {
			super("/repos/(.+)/(.+)/issues/comments/(\\d+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("  Get a single comment"
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " Id: " + group(3));
		}
	}
	
	private static class CreateComment extends RegExpRequestHandler {
		
		public CreateComment() {
			super("/repos/(.+)/(.+)/issues/(\\d+)/comments");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Create comment " 
				+ " Owner: " + group(1)
				+ " Repo: " + group(2)
				+ " Number: " + group(3));
		}
	}
	
	private static class EditComment extends RegExpRequestHandler {
		
		public EditComment() {
			super("/repos/(.+)/(.+)/issues/comments/(\\d+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Edit a comment " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " Number: " + group(3));
		}
	}
	
	private static class ListAllLabels extends RegExpRequestHandler {
		
		public ListAllLabels() {
			super("/repos/(.+)/(.+)/labels");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("List all labels for this repository " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2));
		}
	}
	
	
	private static class GetSingleLabel extends RegExpRequestHandler {
		
		public GetSingleLabel() {
			super("/repos/(.+)/(.+)/labels/(.+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Get a single label " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " Name: " + group(3));
		}
	}
	
	private static class CreateLabel extends RegExpRequestHandler {
		
		public CreateLabel() {
			super("/repos/(.+)/(.+)/labels");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Create a label " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2));
		}
	}
	
	private static class UpdateLabel extends RegExpRequestHandler {
		
		public UpdateLabel() {
			super("/repos/(.+)/(.+)/labels/(.+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Update a label " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " LabelName: " + group(3));
		}
	}
	
	private static class ListLabelsOnAnIssue extends RegExpRequestHandler {
		
		public ListLabelsOnAnIssue() {
			super("/repos/(.+)/(.+)/issues/(\\d+)/labels");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("List labels on an issue " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " Number: " + group(3));
		}
	}
	
	// AddLabels2Issue es la misma url pero por POST que ListLabelsOnAnIssue()
	
	private static class DeleteComment extends RegExpRequestHandler {
		
		public DeleteComment() {
			super("/repos/(.+)/(.+)/issues/comments/(\\d+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Delete a comment by id " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " Number: " + group(3));
		}
	}
	
	private static class DeleteLabel extends RegExpRequestHandler {
		
		public DeleteLabel() {
			super("/repos/(.+)/(.+)/labels/(.+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Delete a label " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " LabelName: " + group(3));
		}
	}
	
	private static class DeletelabelFromIssue extends RegExpRequestHandler {
		 
		public DeletelabelFromIssue() {
			super("/repos/(.+)/(.+)/issues/(\\d+)/labels/(.+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("Remove a label from an issue " 
					+ " Owner: " + group(1)
					+ " Repo: " + group(2)
					+ " Number: " + group(3)
					+ " LabelName: " + group(4));
		}
	}
	
	// @formatter:off
	private static final HttpRequestHandler HANDLERS[] = {
			new GetUserRequestHandler(), 
			new GetAllIssuesRequestHandler(),
			new GetUserIssuesRequestHandler(), 
			new GetOrgIssuesRequestHandler(),
			new ListIssuesCommentsRequestHandler(),
			new GetAvaiableAssignees(),
			new GetCheckAssignees(),
			new GetCommentsOnRepository(),
			new GetSingleComment(),
			new CreateComment(),
			new EditComment(),
			new ListAllLabels(),
			new GetSingleComment(),
			new CreateLabel(),
			new UpdateLabel(),
			new GetSingleLabel(),
			new ListLabelsOnAnIssue()
	};
	
	private static final HttpRequestHandler DELETES[] =  {
			new DeleteComment(),
			new DeleteLabel(),
			new DeletelabelFromIssue()
	};
			// @formatter:on

	// ------------------------------------------------------------------------
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/javascript;charset=UTF-8");
		for (HttpRequestHandler handler : HANDLERS) {
			if (handler.accept(req)) {
				handler.handler(req, resp);	
				break;
			}			
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/javascript;charset=UTF-8");		
		for (HttpRequestHandler handler : DELETES) {
			if (handler.accept(req)) {
				handler.handler(req, resp);
				break;
			}
		}
	}

	private static String getJsonObject(HttpServletRequest req)
			throws IOException, Exception {
		StringBuffer buffer = new StringBuffer();
		String line = null;
		BufferedReader reader = req.getReader();

		while ((line = reader.readLine()) != null)
			buffer.append(line);

		return buffer.toString();
	}

	private static String getRequestAction(HttpServletRequest req) {
		return req.getRequestURI().substring(req.getServletPath().length());

	}
	
	//--------------------------------------------------------------------
	
	private static final Integer DOMAIN_ID = 553;
	private static final String DOMAIN_NAME = "mac.amtzdelagos.dev";
	private static final String USER_NAME = "mac"; 
	
	private static void getOpenNotices (HttpServletRequest req, HttpServletResponse resp) {	
		
		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
			List<Notice> notices = AON.getOpenNotices(DOMAIN_ID, DOMAIN_NAME, USER_NAME);
			
//			pw.printf("\"data\"[ %s\r\n",
//					buildNotices(notices.listIterator()));			
			pw.append("[");		
			pw.append(buildNotices(notices.listIterator()));
			pw.append("\n]");
			pw.flush();
		} catch ( Exception ex) {
			System.out.println("Exception ex: " + ex.getMessage() + " " + ex.getLocalizedMessage());
		} finally {
			if ( pw != null){
				pw.close();
			}
		}
	}
	
	private static void getClosedNotices (HttpServletRequest req, HttpServletResponse resp) {

		PrintWriter pw = null;
		try {
			
			List<Notice> notices = AON.getClosedNotices(DOMAIN_ID, DOMAIN_NAME, USER_NAME);
			
			pw = resp.getWriter();
			pw.append("[");		
			pw.append(buildNotices(notices.listIterator()));
			pw.append("\n]");
			pw.flush();
		} catch ( Exception ex) {
			System.out.println("Exception ex: " + ex.getMessage() + " " + ex.getLocalizedMessage());
		} finally {
			if ( pw != null)
				pw.close();				
		}
	}
	
	private static void getAllNotices (HttpServletRequest req, HttpServletResponse resp) {
		
		PrintWriter pw = null;
		try {
			
			List<Notice> notices = AON.getAllNotices(DOMAIN_ID, DOMAIN_NAME, USER_NAME);
			
			pw = resp.getWriter();
			pw.append("[");		
			pw.append(buildNotices(notices.listIterator()));
			pw.append("\n]");
			pw.flush();
		} catch ( Exception ex) {
			System.out.println("Exception ex: " + ex.getMessage() + " " + ex.getLocalizedMessage());
		} finally {
			if ( pw != null)
				pw.close();				
		}
	}
	
	private static String buildNotices(ListIterator<Notice> iterator) {
		
		StringBuffer buffer = new StringBuffer();		

		iterator.forEachRemaining(notice -> {
			buffer.append("\n{\n");
			buffer.append(String.format("\"id\":%s,\r\n",
					String.valueOf(notice.getId())));
			buffer.append(String.format("\"user\":%s,\r\n",
					buildUserSender(notice.getSender())));
			buffer.append(String.format("\"title\":\"%s\",\r\n",
					notice.getTitle()));
			buffer.append(String.format("\"body\":\"%s\",\r\n",
					notice.getBody()));
			buffer.append(String.format("\"labels\":%s,\r\n",
					buildLabels(notice.getTags().listIterator())));
			buffer.append(String.format("\"state\":\"%s\"\r\n",
					"open"));
			buffer.append("},");
		});
		String cadena = buffer.substring(0, buffer.length() - 1);
		return cadena;
	}
	
	private static String buildUserSender(User user) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append(String.format("\"id\":%s,\r\n",
				String.valueOf(user.getId())));
		buffer.append(String.format("\"login\":\"%s\"\r\n",
				user.getName()));
		buffer.append("}");
		return buffer.toString();
	}
	
	private static String buildLabels(ListIterator<Tag> tagsIterator) {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("[\n");
		while(tagsIterator.hasNext()) {
			Tag tag = tagsIterator.next();
			buffer.append("{\n");
			buffer.append(String.format("\"id\":%s,\r\n",
					String.valueOf(tag.getId())));
			buffer.append(String.format("\"name\":\"%s\",\r\n",
					tag.getName()));
			buffer.append(String.format("\"color\":\"%s\"\r\n",
					(tag.getColor() != null) ? tag.getColor() : "" ));
			
			if (tagsIterator.hasNext())
				buffer.append("},\n");
			else
				buffer.append("}\n");
		}
		buffer.append("]");
		
		return buffer.toString();
	}
}