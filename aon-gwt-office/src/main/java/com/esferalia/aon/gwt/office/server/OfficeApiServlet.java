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
import com.esferalia.aon.gwt.common.shared.Base64;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.google.gwt.safehtml.shared.UriUtils;

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

		protected Integer getDomainId() {
			return Integer.parseInt(group(1));
		}

		protected String getDomainName() {
			return group(2);
		}
	}

	private static class GetUser extends RegExpRequestHandler {

		public GetUser() {
			super("/users/(\\d+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = null;

			try {
				domainId = getDomainId();

			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				domainId = Integer.parseInt(auxArr[2]);

			} finally {
				getJsonUser(req, resp, domainId);
			}
		}

		private void getJsonUser(HttpServletRequest req,
				HttpServletResponse resp, Integer domainId) {

			PrintWriter pw = null;
			try {

				String domainName = AonServletUtils.getRequestDomainName(req);
				String userName = AonServletUtils.getLoggedUser();
				Integer userId = AonServletUtils.getRequestUserId(req);

				User user = AON.getUser(domainId, domainName, userName, userId);

				pw = resp.getWriter();
				pw.append('{');
				pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
				pw.printf("\"data\":%s", buildUserSender(user));
				pw.append('}');
				pw.flush();

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

		}
	}

	private static class CreateIssue extends RegExpRequestHandler {

		public CreateIssue() {
			super("/repos/(\\d+)/(.+)/issues");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = null;
			String domainName = "";

			try {

				domainId = getDomainId();
				domainName = getDomainName();

			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				domainId = Integer.parseInt(auxArr[2]);
				domainName = auxArr[3];

			} finally {
				createNotice(req, resp, domainId, domainName);
			}
		}

		private void createNotice(HttpServletRequest req,
				HttpServletResponse resp, Integer domain, String domainName) {

			try {

				String object = getJsonObject(req);
				JSONObject json = new JSONObject(object);

				String userName = AonServletUtils.getLoggedUser();

				Notice notice = new Notice();
				notice.setTitle(json.getString("title"));
				notice.setBody(json.getString("body"));
				notice.setStatus(json.getString("state"));
				notice.setUserId(Integer.parseInt(json.getString("sender")));

				if (json.isNull("company") == false) {
					notice.setCompany(json.getString("company"));
					notice.setSource(json.getString("source"));
				}

				if (json.isNull("labels") == false) {
					JSONArray tags = json.getJSONArray("labels");

					for (int x = 0; x < tags.length(); x++) {
						String name = tags.getString(x);
						Tag tag = AON.getTag(domain, domainName, userName,
								name);
						notice.addTag(tag);
					}
				}

				getCreateNotice(resp, domain, domainName, notice);

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
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

			Integer domain = null;
			String domainName = "";

			try {

				domain = getDomainId();
				domainName = getDomainName();

			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				domain = Integer.parseInt(auxArr[2]);
				domainName = auxArr[3];

			} finally {
				editIssue(req, resp, domain, domainName);
			}

		}

		private void editIssue(HttpServletRequest req, HttpServletResponse resp,
				Integer domain, String domainName) {
			try {
				String object = getJsonObject(req);
				JSONObject json = new JSONObject(object);

				Notice notice = new Notice();
				notice.setId(Integer.parseInt(group(3)));

				if (json.isNull("state") == false) {
					notice.setStatus(json.getString("state"));
					getChangeStatusNotice(resp, domain, domainName, notice);
				} else {
					notice.setTitle(json.getString("title"));
					notice.setBody(json.getString("body"));
					getEditNotice(resp, domain, domainName, notice);
				}

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

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

			Integer domainId = null;
			String domainName = "";

			try {

				domainId = getDomainId();
				domainName = getDomainName();

			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				domainId = Integer.parseInt(auxArr[2]);
				domainName = auxArr[3];

			} finally {
				getAllNotices(req, resp, domainId, domainName);
			}
		}

		private void getAllNotices(HttpServletRequest req,
				HttpServletResponse resp, Integer domainId, String domainName) {
			PrintWriter pw = null;
			try {

				String userName = AonServletUtils.getLoggedUser();
				List<Notice> notices = new LinkedList<Notice>();

				pw = resp.getWriter();

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

	private static class ListAllLabels extends RegExpRequestHandler {

		public ListAllLabels() {
			super("/repos/(.+)/(.+)/labels");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = null;
			String domainName = "";

			try {
				domainId = getDomainId();
				domainName = getDomainName();

			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				domainId = Integer.parseInt(auxArr[2]);
				domainName = auxArr[3];

			} finally {
				getLabels(req, resp, domainId, domainName);
			}
		}

		private void getLabels(HttpServletRequest req, HttpServletResponse resp,
				Integer domainId, String domainName) {

			try {
				String user_name = AonServletUtils.getLoggedUser();

				List<Tag> tags = AON.getTags(domainId, domainName, user_name);

				PrintWriter pw = resp.getWriter();
				pw.append('{');
				pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
				pw.printf("\"data\":%s", buildLabels(tags.listIterator()));
				pw.append('}');
				pw.flush();

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	private static class CreateComment extends RegExpRequestHandler {

		private HttpServletRequest req;
		private HttpServletResponse resp;

		public CreateComment() {
			super("/repos/(\\d+)/(.+)/issues/(\\d+)/comments");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			this.req = req;
			this.resp = resp;

			Integer domain = null;
			Integer noticeHeadId = null;
			String domainName = "";

			try {
				domain = getDomainId();
				domainName = getDomainName();
				noticeHeadId = Integer.parseInt(group(3));
			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				domain = Integer.parseInt(actionArr[2]);
				domainName = actionArr[3];
				noticeHeadId = Integer.parseInt(actionArr[5]);

			} finally {
				createIssueComment(domain, domainName, noticeHeadId);
			}
		}

		private void createIssueComment(Integer domainId, String domainName,
				Integer noticeHeadId) {

			try {
				String object = getJsonObject(this.req);
				JSONObject json = new JSONObject(object);
				Notice comment = new Notice();
				comment.setDomain(domainId);
				comment.setUserId(AonServletUtils.getRequestUserId(this.req));
				comment.setBody(json.getString("body"));
				jsonComment(resp, domainId, domainName, noticeHeadId, comment);

			} catch (Exception ex) {
				System.out.println("Error al crear el comentario");
			}
		}
	}

	private static class EditComment extends RegExpRequestHandler {

		HttpServletRequest req;
		HttpServletResponse resp;

		public EditComment() {
			super("/repos/(\\d+)/(.+)/issues/comments/(\\d+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			this.req = req;
			this.resp = resp;

			Integer domain = null;
			Integer commentId = null;
			String domainName = "";

			try {
				domain = getDomainId();
				domainName = getDomainName();
				commentId = Integer.parseInt(group(3));

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				domain = Integer.parseInt(actionArr[2]);
				domainName = actionArr[3];
				commentId = Integer.parseInt(actionArr[6]);

			} finally {
				editComment(domain, domainName, commentId);
			}
		}

		private void editComment(Integer domainId, String domainName,
				Integer commentId) {

			try {
				String object = getJsonObject(req);
				JSONObject json = new JSONObject(object);
				jsonEditComment(resp, domainId, domainName, commentId,
						json.getString("body"));
			} catch (Exception ex) {
				System.out.println("Error al modificar comentario");
			}
		}
	}

	private static class GetRegistries extends RegExpRequestHandler {

		public GetRegistries() {
			super("/repos/(.+)/(.+)/registries");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domain = null;
			String domainName = "";

			try {
				domain = getDomainId();
				domainName = getDomainName();

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				domain = Integer.parseInt(actionArr[2]);
				domainName = actionArr[3];
			}

			finally {
				getRegistries(resp, domain, domainName);
			}
		}

		private void getRegistries(HttpServletResponse resp, Integer domainId,
				String domainName) {
			PrintWriter pw = null;
			try {
				String userName = AonServletUtils.getLoggedUser();
				List<Registry> registries = AON.getRegistries(domainId,
						domainName, userName);
				pw = resp.getWriter();
				pw.append('{');
				pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
				pw.printf("\"data\":%s",
						buildRegistries(registries.listIterator()));
				pw.append('}');
				pw.flush();

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	private static class CreateLabel extends RegExpRequestHandler {

		public CreateLabel() {
			super("/repos/(\\d+)/(.+)/");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domain = null;
			String domainName = "";

			try {
				domain = getDomainId();
				domainName = getDomainName();
			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				domain = Integer.parseInt(actionArr[2]);
				domainName = actionArr[3];
			} finally {
				createLabel(req, resp, domain, domainName);
			}
		}

		private void createLabel(HttpServletRequest req,
				HttpServletResponse resp, Integer domainId, String domainName) {
			try {
				String object = getJsonObject(req);

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

			Integer domain = null;
			String domainName = "";

			try {

				domain = getDomainId();
				domainName = getDomainName();

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				domain = Integer.parseInt(actionArr[2]);
				domainName = actionArr[3];
			} finally {
				updateLabel(req, resp, domain, domainName);
			}
		}

		private void updateLabel(HttpServletRequest req,
				HttpServletResponse resp, Integer domain, String domainName) {

			try {

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

				getEditLabel(resp, domain, domainName, labelName, tag);

				System.out.println(" ================== ");
			} catch (Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
			}

		}
	}

	private static class DeleteLabel extends RegExpRequestHandler {

		public DeleteLabel() {
			super("/repos/(\\d+)/(.+)/labels/(.+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domainId = getDomainId();
			String domainName = getDomainName();
			String userName = AonServletUtils.getLoggedUser();

			String decoded = URLDecoder.decode(group(3), "UTF-8");

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

			Integer domainId = getDomainId();
			String domainName = getDomainName();
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

	// @formatter:off
	private static final HttpRequestHandler GET_HANDLERS[] = {
			new GetUser(),
			new GetRegistries(),
			new GetAllIssuesRequestHandler(),
			new ListAllLabels()
	};
	
	private static final HttpRequestHandler POST_HANDLERS[] = {
			new CreateIssue(),
			new EditIssue(),
			new CreateLabel(),
			new UpdateLabel(),
			new CreateComment(),
			new EditComment()
	};

	private static final HttpRequestHandler DELETE_HANDLERS[] = {
			new DeleteLabel(),
			new DeleteNotice(),
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
		buffer.append(String.format("\"title\":\"%s\",\r\n",
				UriUtils.encode(notice.getTitle())));
		buffer.append(String.format("\"body\":\"%s\",\r\n",
				UriUtils.encode(notice.getBody())));
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
		buffer.append(String.format("\"comments\":%s,\r\n",
				buildComments(notice.getComments().listIterator())));
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
			AON.changeNoticeStatus(domainId, domainName,
					AonServletUtils.getLoggedUser(), notice);
			pw.append('{');
			pw.append('}');
			// pw.append(getNotice(editNotice));
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

	private static String buildComments(ListIterator<Notice> commentsIterator) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("[\n");
		while (commentsIterator.hasNext()) {
			buffer.append(getComment(commentsIterator.next()));

			if (commentsIterator.hasNext())
				buffer.append(",\n");
		}
		buffer.append("]");

		return buffer.toString();
	}

	private static String getComment(Notice comment) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append(String.format("\"id\":%s,\r\n",
				String.valueOf(comment.getId())));
		buffer.append(String.format("\"user\":%s,\r\n",
				buildUserSender(comment.getSender())));
		buffer.append(String.format("\"created_at\":\"%s\",\r\n",
				comment.getStartDate()));
		buffer.append(
				String.format("\"body\":\"%s\"\r\n", (comment.getBody() != null)
						? UriUtils.encode(comment.getBody()) : ""));
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
		buffer.append(String.format("\"domain\":\"%s\",\r\n",
				String.valueOf(tag.getDomain())));
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
		buffer.append(String.format("\"name\":\"%s\",\r\n",
				(registry.getName() != null) ? registry.getName() : ""));
		buffer.append(
				String.format("\"alias\":\"%s\",\r\n", 
						(registry.getAlias() != null) ? registry.getAlias() : ""));
		buffer.append(String.format("\"document\":\"%s\"\r\n",
				registry.getDocument()));
		buffer.append("}");

		return buffer.toString();
	}

	private static void buildLabel(HttpServletResponse resp, Integer domainId,
			String domainName, Tag tag) {

		PrintWriter pw = null;
		try {

			pw = resp.getWriter();
			Tag newTag = AON.addNewTag(domainId, domainName,
					AonServletUtils.getLoggedUser(), tag);
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

	private static void jsonComment(HttpServletResponse resp, Integer domainId,
			String domainName, Integer noticeHeadId, Notice comment) {

		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
			Notice noticeComment = AON.createComment(domainId, domainName,
					AonServletUtils.getLoggedUser(), noticeHeadId, comment);
			pw.append(getComment(noticeComment));
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

	private static void jsonEditComment(HttpServletResponse resp,
			Integer domainId, String domainName, Integer commentId,
			String body) {

		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
			Notice editComment = AON.editComment(domainId, domainName,
					AonServletUtils.getLoggedUser(), commentId, body);
			pw.append(getComment(editComment));
			pw.flush();

		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	private static void getEditLabel(HttpServletResponse resp, Integer domainId,
			String domainName, String labelName, Tag tag) {

		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
			Tag editTag = AON.editTag(domainId, domainName,
					AonServletUtils.getLoggedUser(), labelName, tag);
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