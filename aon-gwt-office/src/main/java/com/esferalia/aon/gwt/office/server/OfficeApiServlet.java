package com.esferalia.aon.gwt.office.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.LinkedList;
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

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.api.model.registry.Registry;

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
		"/aon_gwt_office/api/*" })
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
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
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
			// TODO
			System.out.println("This call lists all the available assignees to "
					+ "Owner: " + group(1) + " Repo " + group(2)
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
			// TODO
			System.out.println("This call lists all the available assignees to "
					+ "Owner: " + group(1) + " Repo " + group(2) + " Assignee: "
					+ group(3) + " which issues may be assigned.");
		}
	}

	private static class CreateIssue extends RegExpRequestHandler {

		public CreateIssue() {
			super("/repos/(\\d+)/(.+)/issues");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			try {
				String object = getJsonObject(req);
				System.out.println("Object: " + object);
				JSONObject json = new JSONObject(object);

				Integer domainId = Integer.parseInt(group(1));
				String domainName = group(2);
				String userName = AonServletUtils.getLoggedUser();

				Notice notice = new Notice();
				notice.setTitle(json.getString("title"));
				notice.setBody(json.getString("body"));
				notice.setStatus(json.getString("state"));

				if (json.isNull("company") == false) {
					notice.setCompany(json.getString("company"));
					notice.setSource(json.getString("source"));
				}

				if (json.isNull("labels") == false) {
					JSONArray tags = json.getJSONArray("labels");

					for (int x = 0; x < tags.length(); x++) {
						String name = tags.getString(x);
						Tag tag = AON.getTag(domainId, domainName, userName,
								name);
						System.out.println("TagId: " + tag.getId() + "\nName: "
								+ tag.getName() + "\nType: " + tag.getType());
						notice.addTag(tag);
					}
				}

				getCreateNotice(resp, domainId, domainName, notice);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private static class EditIssue extends RegExpRequestHandler {

		public EditIssue() {
			super("/repos/(\\d+)/(.+)/issues/(\\d+)");			
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			try {
				String object = getJsonObject(req);
				System.out.println("Object: " + object);
				JSONObject json = new JSONObject(object);
				
				Integer domainId = Integer.parseInt(group(1));
				String domainName = group(2);				

				Notice notice = new Notice();
				notice.setId(Integer.parseInt(group(1)));

				if (json.isNull("state") == false) {
					notice.setStatus(json.getString("state"));
					getChangeStatusNotice(resp, domainId, domainName, notice);
				} else {
					notice.setTitle(json.getString("title"));
					notice.setBody(json.getString("body"));
					getEditNotice(resp, domainId, domainName, notice);
				}

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	private static class GetAllIssuesRequestHandler
			extends RegExpRequestHandler {

		public GetAllIssuesRequestHandler() {
			super("/repos/(\\d+)/(.+)/issues");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			PrintWriter pw = null;
			try {

				Integer domainId = Integer.parseInt(group(1));
				String domainName = group(2);
				String userName = AonServletUtils.getLoggedUser();

				pw = resp.getWriter();
				List<Notice> notices = new LinkedList<Notice>();

				String state = req.getParameter("state");
				switch (state) {
				case "open":
					notices = AON.getOpenNotices(domainId, domainName,
							userName);
					break;
				case "closed":
					notices = AON.getClosedNotices(domainId, domainName,
							userName);
					break;

				default:
					notices = AON.getAllNotices(domainId, domainName, userName);
					break;
				}

				pw.append('{');
				pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
				pw.printf("\"data\":%s", buildNotices(notices.listIterator()));
				pw.append('}');
				pw.flush();

			} catch (Exception ex) {
				System.out.println("Exception ex: " + ex.getMessage() + " "
						+ ex.getLocalizedMessage());
			} finally {
				if (pw != null) {
					pw.close();
				}
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
			// TODO
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
			// TODO
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
			// TODO
			System.out.println("List comments on an issue ( owner:" + group(1)
					+ ", repo:" + group(2) + ", number:" + group(3) + ")");
		}
	}

	private static class GetIssueComments extends RegExpRequestHandler {

		public GetIssueComments() {
			super("/issues/(\\d+)/comments");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO
			System.out.println("  Get a single comment" + " Owner: " + group(1)
					+ " Repo: " + group(2) + " Id: " + group(3));
		}
	}

	private static class CreateIssueComment extends RegExpRequestHandler {

		public CreateIssueComment() {
			super("/issues/(\\d+)/comments");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO
			System.out.println("Create comment " + " Owner: " + group(1)
					+ " Repo: " + group(2) + " Number: " + group(3));
		}
	}

	private static class EditComment extends RegExpRequestHandler {

		public EditComment() {
			super("/repos/(.+)/(.+)/issues/comments/(\\d+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO
			System.out.println("Edit a comment " + " Owner: " + group(1)
					+ " Repo: " + group(2) + " Number: " + group(3));
		}
	}

	private static class ListAllLabels extends RegExpRequestHandler {

		public ListAllLabels() {
			super("/repos/(\\d+)/(.+)/labels");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = Integer.parseInt(group(1));
			String domainName = group(2);
			String user_name = AonServletUtils.getLoggedUser();

			List<Tag> tags = AON.getTags(domainId, domainName, user_name);

			PrintWriter pw = resp.getWriter();
			pw.append('{');
			pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
			pw.printf("\"data\":%s", buildLabels(tags.listIterator()));
			pw.append('}');
			pw.flush();
		}
	}

	private static class GetRegistries extends RegExpRequestHandler {

		public GetRegistries() {
			super("/repos/(.+)/(.+)/registries");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = Integer.parseInt(group(1));
			String domainName = group(2);
			String userName = AonServletUtils.getLoggedUser();

			List<Registry> registries = AON.getRegistries(domainId, domainName,
					userName);
			System.out.println(registries.size());
			PrintWriter pw = resp.getWriter();
			pw.append('{');
			pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
			pw.printf("\"data\":%s",
					buildRegistries(registries.listIterator()));
			pw.append('}');
			pw.flush();
		}
	}

	private static class GetSingleLabel extends RegExpRequestHandler {

		public GetSingleLabel() {
			super("/repos/(.+)/(.+)/labels/(.+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO
			System.out.println("Get a single label " + " Owner: " + group(1)
					+ " Repo: " + group(2) + " Name: " + group(3));
		}
	}

	private static class CreateLabel extends RegExpRequestHandler {

		public CreateLabel() {
			super("/repos/(\\d+)/(.+)/");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			System.out.println("Create a label");

			try {
				String object = getJsonObject(req);

				Integer domainId = Integer.parseInt(group(1));
				String domainName = group(2);

				System.out.println("Objecto: " + object);

				JSONObject json = new JSONObject(object);
				Tag tag = new Tag();
				tag.setName(json.getString("name"));
				tag.setType((byte) json.getInt("type"));

				if (json.isNull("color") == false)
					tag.setColor(json.getString("color"));

				buildLabel(resp, domainId, domainName, tag);

			} catch (Exception ex) {
				System.out.println("");
			}
		}
	}

	private static class UpdateLabel extends RegExpRequestHandler {

		public UpdateLabel() {
			super("/repos/(\\d+)/(.+)/labels/(.+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			try {
				
				Integer domainId = Integer.parseInt(group(1));
				String domainName = group(2);				
				
				String object = getJsonObject(req);
				System.out.println("Object: " + object);
				JSONObject json = new JSONObject(object);
				String labelName = URLDecoder.decode(group(3), "UTF-8");
				System.out.println("Deco2: " + labelName);
				Tag tag = new Tag();

				if (json.isNull("name") == false)
					tag.setName(json.getString("name"));

				if (json.isNull("color") == false)
					tag.setColor(json.getString("color"));

				getEditLabel(resp, domainId, domainName, labelName, tag);

				System.out.println(" ================== ");
			} catch (Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
			}
		}
	}

	private static class ListLabelsOnAnIssue extends RegExpRequestHandler {

		public ListLabelsOnAnIssue() {
			super("/repos/(.+)/(.+)/issues/(\\d+)/labels");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO
			System.out.println("List labels on an issue " + " Owner: "
					+ group(1) + " Repo: " + group(2) + " Number: " + group(3));
		}
	}

	private static class AddLabelToIssue extends RegExpRequestHandler {

		public AddLabelToIssue() {
			super("repos/(.+)/(.+)/issues/(\\d+)/labels");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			System.out.println("AddLabel2Issue");
		}
	}

	private static class DeleteComment extends RegExpRequestHandler {

		public DeleteComment() {
			super("/repos/(.+)/(.+)/issues/comments/(\\d+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO
			System.out.println("Delete a comment by id " + " Owner: " + group(1)
					+ " Repo: " + group(2) + " Number: " + group(3));
		}
	}

	private static class DeleteLabel extends RegExpRequestHandler {

		public DeleteLabel() {
			super("/repos/(\\d+)/(.+)/labels/(.+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = Integer.parseInt(group(1));
			String domainName = group(2);
			String userName = AonServletUtils.getLoggedUser();

			String decoded = URLDecoder.decode(group(0), "UTF-8");

			boolean deleted = AON.deleteTag(domainId, domainName, userName,
					decoded);
			System.out.println("Borrado: " + deleted);

			if (deleted) {
				resp.setStatus(200);
			} else {
				resp.setStatus(404);
			}

			PrintWriter pw = resp.getWriter();
			pw.append("{\n");
			pw.append("}");
			pw.flush();

		}
	}

	private static class DeleteNotice extends RegExpRequestHandler {

		public DeleteNotice() {
			super("/repos/(\\d+)/(.+)/issues/(\\d+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = Integer.parseInt(group(1));
			String domainName = group(2);
			String userName = AonServletUtils.getLoggedUser();

			Integer noticeId = Integer
					.parseInt(URLDecoder.decode(group(3), "UTF-8"));
			System.out.println("Number: " + noticeId);

			boolean deleted = AON.deleteNotice(domainId, domainName, userName,
					noticeId);
			System.out.println("BORRADO: " + deleted);

			if (deleted)
				resp.setStatus(200);
			else
				resp.setStatus(404);

			PrintWriter pw = resp.getWriter();
			pw.append("{\n");
			pw.append("}");
			pw.flush();
		}
	}

	private static class DeletelabelFromIssue extends RegExpRequestHandler {

		public DeletelabelFromIssue() {
			super("/repos/(.+)/(.+)/issues/(\\d+)/labels/(.+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO
			System.out.println("Remove a label from an issue " + " Owner: "
					+ group(1) + " Repo: " + group(2) + " Number: " + group(3)
					+ " LabelName: " + group(4));
		}
	}

	// @formatter:off
	private static final HttpRequestHandler GET_HANDLERS[] = {
			new GetRegistries(),
			new GetUserRequestHandler(),
			new GetAllIssuesRequestHandler(),
			new GetUserIssuesRequestHandler(), 
			new GetOrgIssuesRequestHandler(),
			new GetAvaiableAssignees(),
			new GetCheckAssignees(),		
			new ListIssuesCommentsRequestHandler(),
			new GetIssueComments(),
			new GetSingleLabel(),
			new ListAllLabels(),
			new ListLabelsOnAnIssue(),			
	};
	
	private static final HttpRequestHandler POST_HANDLERS[] = {
			new CreateIssue(),
			new EditIssue(),
			new AddLabelToIssue(),
			new CreateIssueComment(),
			new EditComment(),
			new CreateLabel(),
			new UpdateLabel(),
	};

	private static final HttpRequestHandler DELETE_HANDLERS[] = {
			new DeleteComment(),
			new DeleteLabel(),
			new DeleteNotice(),
			new DeletelabelFromIssue()
	};
	// @formatter:on

	// ------------------------------------------------------------------------

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");

		for (HttpRequestHandler handler : GET_HANDLERS) {
			if (handler.accept(req)) {
				handler.handler(req, resp);
				break;
			}
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");

		for (HttpRequestHandler handler : POST_HANDLERS) {
			if (handler.accept(req)) {
				handler.handler(req, resp);
				break;
			}
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json;charset=UTF-8");

		for (HttpRequestHandler handler : DELETE_HANDLERS) {
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

		String action = req.getRequestURI()
				.substring(req.getRequestURI().indexOf(req.getServletPath()));

		return action.substring(req.getServletPath().length());

		// Esto funcionaba para los Test, pero cuando se mete
		// el Gwt.getModuleBase() deja de ser correcto.

		// return req.getRequestURI().substring(req.getServletPath().length());

	}

	// --------------------------------------------------------------------

	private static String getNotice(Notice notice) throws Exception {

		StringBuffer buffer = new StringBuffer();

		buffer.append("{\n");
		buffer.append(String.format("\"id\":%s,\r\n",
				String.valueOf(notice.getId())));
		buffer.append(String.format("\"number\":%s,\r\n",
				String.valueOf(notice.getId())));
		buffer.append(
				String.format("\"title\":\"%s\",\r\n", notice.getTitle()));
		buffer.append(String.format("\"body\":\"%s\",\r\n", notice.getBody()));
		buffer.append(
				String.format("\"state\":\"%s\",\r\n", notice.getStatus()));
		buffer.append(String.format("\"company\":\"%s\",\r\n",
				notice.getCompany() != null ? notice.getCompany() : ""));
		buffer.append(String.format("\"source\":\"%s\",\r\n",
				notice.getSource() != null ? notice.getSource() : ""));

		buffer.append(String.format("\"user\":%s,\r\n",
				buildUserSender(notice.getSender())));
		buffer.append(String.format("\"type\":\"%s\",\r\n",
				(notice.getType() != null) ? notice.getType() : "Sin asignar"));
		buffer.append(String.format("\"priority\":\"%s\",\r\n",
				(notice.getPriority() != null) ? notice.getPriority()
						: "Sin asignar"));
		buffer.append(String.format("\"created_at\":\"%s\",\r\n",
				notice.getStartDate()));
		buffer.append(String.format("\"labels\":%s\r\n",
				buildLabels(notice.getTags().listIterator())));
		buffer.append('}');

		return buffer.toString();

	}

	private static void getCreateNotice(HttpServletResponse resp,
			Integer domainId, String domainName, Notice notice) {

		PrintWriter pw = null;
		try {

			pw = resp.getWriter();
			Notice newNotice = AON.addNewNotice(domainId, domainName,
					AonServletUtils.getLoggedUser(), notice);
			pw.append(getNotice(newNotice));
			pw.flush();

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			pw.flush();
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	private static void getEditNotice(HttpServletResponse resp,
			Integer domainId, String domainName, Notice notice) {
		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
			Notice editNotice = AON.editNotice(domainId, domainName,
					AonServletUtils.getLoggedUser(), notice);
			pw.append(getNotice(editNotice));
			pw.flush();

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			pw.flush();
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	private static void getChangeStatusNotice(HttpServletResponse resp,
			Integer domainId, String domainName, Notice notice) {
		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
			Notice editNotice = AON.changeNoticeStatus(domainId, domainName,
					AonServletUtils.getLoggedUser(), notice);
			pw.append(getNotice(editNotice));
			pw.flush();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			pw.flush();
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	private static String buildNotices(ListIterator<Notice> iterator)
			throws Exception {

		StringBuffer buffer = new StringBuffer();
		buffer.append('[');

		while (iterator.hasNext()) {
			Notice notice = iterator.next();
			buffer.append(getNotice(notice));
			if (iterator.hasNext())
				buffer.append(',');
		}
		buffer.append(']');

		return buffer.toString();
	}

	private static String buildUserSender(User user) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append(
				String.format("\"id\":%s,\r\n", String.valueOf(user.getId())));
		buffer.append(String.format("\"login\":\"%s\",\r\n", user.getLogin()));
		buffer.append(String.format("\"name\":\"%s\"\r\n", user.getName()));
		buffer.append("}");
		return buffer.toString();
	}

	private static String buildLabels(ListIterator<Tag> tagsIterator) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("[\n");
		while (tagsIterator.hasNext()) {
			buffer.append(getLabel(tagsIterator.next()));

			if (tagsIterator.hasNext())
				buffer.append(",\n");
		}
		buffer.append("]");

		return buffer.toString();
	}

	private static String getLabel(Tag tag) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append(
				String.format("\"id\":%s,\r\n", String.valueOf(tag.getId())));
		buffer.append(String.format("\"type\":%s,\r\n",
				String.valueOf(tag.getType())));
		buffer.append(String.format("\"name\":\"%s\",\r\n", tag.getName()));
		buffer.append(String.format("\"color\":\"%s\"\r\n",
				(tag.getColor() != null) ? tag.getColor() : ""));
		buffer.append("}");

		return buffer.toString();
	}

	private static String buildRegistries(ListIterator<Registry> regsIterator) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[\n");
		while (regsIterator.hasNext()) {
			buffer.append(getRegistry(regsIterator.next()));

			if (regsIterator.hasNext())
				buffer.append(",\n");
		}
		buffer.append("]");

		return buffer.toString();
	}

	private static String getRegistry(Registry registry) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append(String.format("\"id\":%s,\r\n",
				String.valueOf(registry.getId())));
		buffer.append(
				String.format("\"name\":\"%s\",\r\n", registry.getName()));
		buffer.append(String.format("\"document\":\"%s\"\r\n",
				registry.getDocument()));
		buffer.append("}");

		return buffer.toString();
	}

	private static void buildLabel(HttpServletResponse resp, Integer domainId, String domainName, Tag tag) {

		PrintWriter pw = null;
		try {

			pw = resp.getWriter();
			Tag newTag = AON.addNewTag(domainId, domainName, AonServletUtils.getLoggedUser(), tag);
			pw.append(getLabel(newTag));
			pw.flush();

		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
			pw.flush();
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	private static void getEditLabel(HttpServletResponse resp, Integer domainId, String domainName, String labelName,
			Tag tag) {

		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
			Tag editTag = AON.editTag(domainId, domainName, AonServletUtils.getLoggedUser(),
					labelName, tag);
			pw.append(getLabel(editTag));
			pw.flush();
		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
			pw.flush();
		} finally {
			if (pw != null)
				pw.close();
		}
	}

}