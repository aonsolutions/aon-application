package com.esferalia.aon.gwt.office.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.office.exceptions.LabelNotDeletedException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeContainer;
import com.esferalia.aon.occam.api.model.office.NoticeFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
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
	
	private static class GetAllUsers extends RegExpRequestHandler {
		
		public GetAllUsers() {
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
				getUsers(req, resp, domainId);
			}
		}
		
		private void getUsers(HttpServletRequest req, HttpServletResponse resp, int domainId) {
			PrintWriter pw = null;
			
			try {
				String domainName = AonServletUtils.getRequestDomainName(req);				
				List<User> users = AON.getUsers(domainId, domainName, AonServletUtils.getLoggedUser());
				
				pw = resp.getWriter();
				pw.append('{');
				pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
				pw.printf("\"data\":%s", buildUsers(users.listIterator()));
				pw.append('}');
				pw.flush();
				
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		
	}

	private static class GetUser extends RegExpRequestHandler {

		public GetUser() {
			super("/users/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))");
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

		private HttpServletRequest req;
		private HttpServletResponse resp;

		private Integer domain;
		private String domainName;
		private static SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		public CreateIssue() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/issues");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			this.req = req;
			this.resp = resp;

			try {
				this.domain = getDomainId();
				this.domainName = getDomainName();

			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				this.domain = Integer.parseInt(auxArr[2]);
				this.domainName = auxArr[3];

			} finally {
				createNotice();
			}
		}

		private void createNotice() {

			try {

				String object = getJsonObject(req);
				JSONObject json = new JSONObject(object);

				String userName = AonServletUtils.getLoggedUser();

				Notice notice = new Notice();
				notice.setTitle(json.getString("title"));
				
				String dateString = json.getString("startDate");
				Date startDate = null;
				try {
					startDate = sdf.parse(dateString);
				} catch (Exception ex) {
					startDate = new Date();
				}
				notice.setStartDate(startDate);
				Integer userId = Integer.parseInt(json.getString("sender"));
				User user = AON.getUser(domain, domainName, userName, userId);
				notice.setSender(user);

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
						tag.setUser(user);
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
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/issues/(\\d+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			Integer domain = null;
			String domainName = null;
			Integer noticeId = null;

			try {

				domain = getDomainId();
				domainName = getDomainName();
				noticeId = Integer.parseInt(group(3));

			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				domain = Integer.parseInt(auxArr[2]);
				domainName = auxArr[3];
				noticeId = Integer.parseInt(auxArr[5]);

			} finally {
				editIssue(req, resp, domain, domainName, noticeId);
			}

		}

		private void editIssue(HttpServletRequest req, HttpServletResponse resp,
				Integer domain, String domainName, Integer noticeId) {
			try {
				String object = getJsonObject(req);
				JSONObject json = new JSONObject(object);

				Notice notice = new Notice();
				notice.setId(noticeId);

				User user = AON.getUser(domain, domainName,
						AonServletUtils.getLoggedUser(),
						AonServletUtils.getRequestUserId(req));
				notice.setSender(user);

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
	
	private static class AddDuplicateNotice extends RegExpRequestHandler {
		
		private HttpServletRequest req; 
		private HttpServletResponse resp; 
		
		public AddDuplicateNotice() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))"
					+ "/issues/duplicated/(\\d+)");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
			this.req = req;
			this.resp = resp;
			
			Integer domainId = null;			
			Integer parentId = null;
			String domainName = "";			
			
			try {
			
				domainId = getDomainId();
				domainName = getDomainName();
				parentId = Integer.parseInt(group(3));
			
			} catch (Exception ex) {
				String aux = getRequestAction(req);
				String[] auxArr = aux.split("/");
				domainId = Integer.parseInt(auxArr[2]);
				domainName = auxArr[3];				
				parentId = Integer.parseInt(auxArr[6]);

			} finally {
				addDuplicateNotice(domainId, domainName, parentId);
			}
		}
		
		private void addDuplicateNotice(int domainId, String domainName, int parentId) {
			
			PrintWriter pw = null;
			
			try {
				String object = getJsonObject(req);
				JSONObject json = new JSONObject(object);
				
				String userName = AonServletUtils.getLoggedUser();
				
				Notice childNotice = new Notice();
				childNotice.setId(Integer.parseInt(json.getString("id")));
				
				User user = AON.getUser(domainId, domainName,
						userName,
						AonServletUtils.getRequestUserId(req));
				childNotice.setSender(user);
				
				pw = resp.getWriter();
				Notice duplicated = AON.addDuplicateNotice(domainId, domainName, userName, childNotice, parentId);
				pw.append(getNotice(duplicated));
				pw.flush();
				
			} catch (Exception ex) {
				System.out.println(ex.getMessage());				
			} finally {
				pw.flush();
			}
		}
	}

	private static class GetAllIssuesRequestHandler
			extends RegExpRequestHandler {

		public GetAllIssuesRequestHandler() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/issues");
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
				
				pw = resp.getWriter();
				
				NoticeFilter filter = new NoticeFilter();
				filter.setState(req.getParameter("state"));
				
				if (req.getParameter("since") != null)					
					filter.setSinceAsString(req.getParameter("since"));

				if (req.getParameter("sender") != null)					
					filter.setCompany(URLDecoder.decode(req.getParameter("sender"), "UTF-8"));
				
				List<String> tagsList = new LinkedList<String>();
				if (req.getParameter("labels") != null) {					
					String aux = URLDecoder.decode(req.getParameter("labels"), "UTF-8");
					String[] auxArr = aux.split(",");
					for (int z = 0; z < auxArr.length; z++) {
						String name = auxArr[z];
						tagsList.add(name);
					}
					filter.setTags(auxArr);
				}

				if (req.getParameter("text") != null)					
					filter.setText(URLDecoder.decode(req.getParameter("text"), "UTF-8"));
				
				if (req.getParameter("user") != null)
					filter.setUser(URLDecoder.decode(req.getParameter("user"), "UTF-8"));					
				
				filter.setOffset(Integer.parseInt(req.getParameter("offset")));
				
				int count = AON.getSelectedCount(domainId, domainName, AonServletUtils.getLoggedUser(), filter);
				List<Notice> notices = AON.getNotices(domainId, domainName, AonServletUtils.getLoggedUser(), filter);

				NoticeContainer container = new NoticeContainer();
				container.setCount(count);
				container.setNotices(notices);
				
				pw.append('{');
				pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
				pw.printf(String.format("\"count\":\"%s\",\r\n", String.valueOf(container.getCount())));
				pw.printf("\"data\":%s", buildNotices(container.getNotices().listIterator()));
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
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/labels");
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

				List<Tag> tags = AON.getTags(domainId, domainName,
						AonServletUtils.getLoggedUser());

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
		private static SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");

		public CreateComment() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/issues/(\\d+)/comments");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			this.req = req;
			this.resp = resp;

			Integer domain = null;
			Integer noticeHeadId = null;
			String domainName = null;

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
				
				String dateString = json.getString("startDate");
				Date startDate = null;
				try {
					startDate = sdf.parse(dateString);
				} catch (Exception ex) {
					startDate = new Date();
				}
				comment.setStartDate(startDate);
				
				User user = AON.getUser(domainId, domainName,
						AonServletUtils.getLoggedUser(),
						AonServletUtils.getRequestUserId(req));
				comment.setSender(user);
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
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/issues/comments/(\\d+)");
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
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/registries");
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
	
	private static class GetRMedias extends RegExpRequestHandler {

		private HttpServletRequest req;
		private HttpServletResponse resp;
		
		private Integer domain;
		private String domainName;
		
		public GetRMedias() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/rmedia");
		}
		
		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			try {
				this.req = req;
				this.resp = resp;
				this.domain = getDomainId();
				this.domainName = getDomainName();

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				this.domain = Integer.parseInt(actionArr[2]);
				this.domainName = actionArr[3];
			}

			finally {
				getRMedias();
			}
		}
		
		private void getRMedias() {

			PrintWriter pw = null;
			try {
				String userName = AonServletUtils.getLoggedUser();
				List<RegistryMedia> rmedias = AON.getRMedias(domain, domainName, userName);				
				pw = resp.getWriter();
				pw.append('{');
				pw.printf(String.format("\"message\":\"%s\",\r\n", "FOUNDED"));
				pw.printf("\"data\":%s",
						buildRMedia(rmedias.listIterator()));
				pw.append('}');
				pw.flush();

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	private static class CreateLabel extends RegExpRequestHandler {

		public CreateLabel() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/");
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

	private static class AddLabelToAnIssue extends RegExpRequestHandler {

		private HttpServletRequest req;
		private HttpServletResponse resp;

		private Integer domain;
		private Integer noticeId;
		private String domainName;

		public AddLabelToAnIssue() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/issues/(\\d+)/labels/");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			try {
				this.req = req;
				this.resp = resp;

				this.domain = getDomainId();
				this.domainName = getDomainName();
				this.noticeId = Integer.parseInt(group(3));

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				this.domain = Integer.parseInt(actionArr[2]);
				this.domainName = actionArr[3];
				this.noticeId = Integer.parseInt(actionArr[5]);

			} finally {
				addLabelToAnIssue();
			}
		}

		private void addLabelToAnIssue() {

			PrintWriter pw = null;

			try {

				List<Tag> tagListAux = new LinkedList<Tag>();

				String object = getJsonObject(this.req);
				JSONObject json = new JSONObject(object);
				JSONArray tags = json.getJSONArray("labels");

				User user = AON.getUser(domain, domainName,
						AonServletUtils.getLoggedUser(),
						AonServletUtils.getRequestUserId(req));

				for (int x = 0; x < tags.length(); x++) {
					String name = tags.getString(x);
					Tag tag = AON.getTag(domain, domainName,
							AonServletUtils.getLoggedUser(), name);
					tag.setUser(user);
					tagListAux.add(tag);
				}

				Notice notice = AON.addLabelsToAnIssue(domain, domainName,
						AonServletUtils.getLoggedUser(), noticeId, tagListAux);
				pw = resp.getWriter();
				pw.append(getNotice(notice));
				pw.flush();

			} catch (Exception ex) {
				System.out.println("Error al conseguir etiquetas guardadas");
			}
		}
	}

	private static class ReplaceLabelsForIssue extends RegExpRequestHandler {

		private HttpServletRequest req;
		private HttpServletResponse resp;

		private Integer domain;
		private Integer noticeId;
		private String domainName;

		public ReplaceLabelsForIssue() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/notice/(\\d+)/labels/");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			this.req = req;
			this.resp = resp;

			try {
				this.domain = getDomainId();
				this.domainName = getDomainName();
				this.noticeId = Integer.parseInt(group(3));

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				this.domain = Integer.parseInt(actionArr[2]);
				this.domainName = actionArr[3];
				this.noticeId = Integer.parseInt(actionArr[5]);

			} finally {
				replaceLabelsFromIssue();
			}
		}

		private void replaceLabelsFromIssue() {

			PrintWriter pw = null;

			try {

				User user = AON.getUser(domain, domainName,
						AonServletUtils.getLoggedUser(),
						AonServletUtils.getRequestUserId(req));

				String object = getJsonObject(this.req);
				JSONObject json = new JSONObject(object);

				JSONArray addLabelsAux = json.getJSONArray("addLabels");
				JSONArray delLabelsAux = json.getJSONArray("deletedLabels");

				List<Tag> addLabelsList = getListFromJsArray(addLabelsAux,
						user);
				List<Tag> delLabelList = getListFromJsArray(delLabelsAux, user);

				Notice notice = AON.replaceLabelFromIssue(domain, domainName,
						AonServletUtils.getLoggedUser(), noticeId,
						addLabelsList, delLabelList);

				pw = resp.getWriter();
				pw.append(getNotice(notice));
				pw.flush();

			} catch (Exception ex) {
				System.out.println("Error al conseguir etiquetas guardadas");
			}
		}

		private List<Tag> getListFromJsArray(JSONArray array, User user) {

			List<Tag> tags = new LinkedList<Tag>();

			try {

				for (int x = 0; x < array.length(); x++) {
					String name = array.getString(x);
					Tag tag = AON.getTag(this.domain, this.domainName,
							AonServletUtils.getLoggedUser(), name);
					tag.setUser(user);
					tags.add(tag);
				}

				return tags;

			} catch (Exception ex) {
				return tags;
			}
		}
	}

	private static class UpdateLabel extends RegExpRequestHandler {

		public UpdateLabel() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/labels/(.+)");
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
				JSONObject json = new JSONObject(object);
				String labelName = URLDecoder.decode(group(3), "UTF-8");
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

		private HttpServletResponse resp;

		private Integer domainId;
		private String domainName;
		private String labelName;

		public DeleteLabel() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/labels/(.+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			this.resp = resp;

			try {

				this.domainId = getDomainId();
				this.domainName = getDomainName();
				this.labelName = URLDecoder.decode(group(3), "UTF-8");

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				this.domainId = Integer.parseInt(actionArr[2]);
				this.domainName = actionArr[3];
				this.labelName = URLDecoder.decode(actionArr[5], "UTF-8");

			} finally {
				deleteLabel();
			}
		}

		private void deleteLabel() {

			PrintWriter pw = null;

			try {
				pw = resp.getWriter();

				boolean deleted = AON.deleteTag(domainId, domainName,
						AonServletUtils.getLoggedUser(), labelName);

				if (deleted) {
					resp.setStatus(200);
				} else {
					throw new LabelNotDeletedException();
				}

				pw.append("{\n");
				pw.append("}");
				pw.flush();

			} catch (LabelNotDeletedException ex) {
				resp.setStatus(404);
				System.out.println("Etiqueta no eliminada: " + ex.getMessage());
				pw.flush();
			} catch (IOException ex) {
				resp.setStatus(404);
				System.out.println(
						"Se ha producido un error: " + ex.getMessage());
				pw.flush();
			}
		}
	}

	private static class RemoveLabelFromIssue extends RegExpRequestHandler {

		private HttpServletRequest req;
		private HttpServletResponse resp;
		private Integer domainId;
		private Integer noticeId;
		private String domainName;
		private String labelName;

		public RemoveLabelFromIssue() {
			super("/repos/(\\d+)/([\\w-]+(\\.[\\w-]+)*\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))/issues/(\\d+)/labels/(.+)");
		}

		@Override
		public void handler(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			this.req = req;
			this.resp = resp;

			try {
				this.domainId = getDomainId();
				this.domainName = getDomainName();
				this.noticeId = Integer.parseInt(group(3));
				this.labelName = URLDecoder.decode(group(4), "UTF-8");

			} catch (Exception ex) {
				String ver = getRequestAction(req);
				String[] actionArr = ver.split("/");
				this.domainId = Integer.parseInt(actionArr[2]);
				this.domainName = actionArr[3];
				this.noticeId = Integer.parseInt(actionArr[5]);
				this.labelName = URLDecoder.decode(actionArr[7], "UTF-8");

			} finally {
				removeLabelFromIssue();
			}
		}

		private void removeLabelFromIssue()
				throws ServletException, IOException {

			try {

				User user = AON.getUser(domainId, domainName,
						AonServletUtils.getLoggedUser(),
						AonServletUtils.getRequestUserId(req));
				Tag tag = AON.getTag(domainId, domainName,
						AonServletUtils.getLoggedUser(), labelName);
				tag.setUser(user);

				boolean deleted = AON.removeLabelFromIssue(domainId, domainName,
						AonServletUtils.getLoggedUser(), noticeId, tag);

				if (deleted)
					resp.setStatus(200);
				else
					throw new LabelNotDeletedException();

				PrintWriter pw = resp.getWriter();
				pw.append('{');
				pw.append('}');
				pw.flush();

			} catch (LabelNotDeletedException ex) {
				System.err.println(
						"La etiqueta no se ha podido cerrar correctamente");
			}
		}
	}

	// @formatter:off
	private static final HttpRequestHandler GET_HANDLERS[] = {
			new GetAllUsers(),
			new GetUser(),
			new GetRegistries(),
			new GetRMedias(),
			new GetAllIssuesRequestHandler(),
			new ListAllLabels(),			
	};
	
	private static final HttpRequestHandler POST_HANDLERS[] = {
			new CreateIssue(),
			new EditIssue(),
			new AddDuplicateNotice(),
			new CreateLabel(),
			new UpdateLabel(),
			new CreateComment(),
			new EditComment(),
			new AddLabelToAnIssue(),
			new ReplaceLabelsForIssue(),
	};

	private static final HttpRequestHandler DELETE_HANDLERS[] = {
			new DeleteLabel(),			
			new RemoveLabelFromIssue(),
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
		resp.setCharacterEncoding("UTF-8");

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
		buffer.append(
				String.format("\"state\":\"%s\",\r\n", notice.getStatus()));
		buffer.append(String.format("\"company\":\"%s\",\r\n",
				notice.getCompany() != null
						? UriUtils.encode(notice.getCompany()) : ""));
		buffer.append(String.format("\"source\":\"%s\",\r\n",
				notice.getSource() != null ? UriUtils.encode(notice.getSource())
						: ""));
		buffer.append(String.format("\"user\":%s,\r\n",
				buildUserSender(notice.getSender())));
		buffer.append(
				String.format("\"type\":\"%s\",\r\n",
						(notice.getType() != null)
								? UriUtils.encode(notice.getType())
								: Constants.NOT_ASSIGNED));
		buffer.append(String.format("\"priority\":\"%s\",\r\n",
				(notice.getPriority() != null)
						? UriUtils.encode(notice.getPriority())
						: Constants.NOT_ASSIGNED));
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
	
	private static String buildUsers(ListIterator<User> iterator) 
			throws Exception {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		
		while (iterator.hasNext()) {
			User user = iterator.next();
			buffer.append(buildUserSender(user));
			if(iterator.hasNext())
				buffer.append(',');
		}
		buffer.append(']');
		return buffer.toString();
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
		buffer.append(String.format("\"login\":\"%s\",\r\n",
				UriUtils.encode(user.getLogin())));
		buffer.append(String.format("\"name\":\"%s\"\r\n",
				UriUtils.encode(user.getName())));
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

		if ( tag.getUser() != null) {
			buffer.append(String.format("\"user\":%s,\r\n",
					buildUserSender(tag.getUser())));
		}

		if ( tag.getStartDate() != null) {
			buffer.append(String.format("\"created_at\":\"%s\",\r\n",
					tag.getStartDate()));
		}

		if ( tag.getEndDate() != null) {
			buffer.append(String.format("\"deleted_at\":\"%s\",\r\n",
					tag.getEndDate()));
		}
		
		buffer.append(String.format("\"name\":\"%s\",\r\n",
				UriUtils.encode(tag.getName())));
		buffer.append(String.format("\"domain\":\"%s\",\r\n",
				String.valueOf(tag.getDomain())));
		buffer.append(String.format("\"color\":\"%s\"\r\n",
				(tag.getColor() != null) ? tag.getColor() : ""));
		buffer.append("}");

		return buffer.toString();
	}
	
	private static String buildRMedia(ListIterator<RegistryMedia> rmediaIterator) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[\n");
		while (rmediaIterator.hasNext()) {
			buffer.append(getRegistry(rmediaIterator.next()));

			if (rmediaIterator.hasNext())
				buffer.append(",\n");
		}
		buffer.append("]");

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
	
	private static String getRegistry(RegistryMedia rmedia) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append(String.format("\"id\":%s,\r\n",
				String.valueOf(rmedia.getId())));
		buffer.append(String.format("\"domain\":%s,\r\n",
				String.valueOf(rmedia.getDomain())));
		buffer.append(String.format("\"registry\":%s,\r\n",
				getRegistry(rmedia.getRegistry())));
		buffer.append(String.format("\"media\":%s,\r\n",
				String.valueOf(rmedia.getMedia())));		
		buffer.append(String.format("\"value\":\"%s\",\r\n",
				(rmedia.getValue() != null)
						? UriUtils.encode(rmedia.getValue()) : UriUtils.encode("")));
		buffer.append(String.format("\"comment\":\"%s\"\r\n",
				(rmedia.getComment() != null)
						? UriUtils.encode(rmedia.getComment()) : UriUtils.encode("")));
		buffer.append("}");

		return buffer.toString();
	}


	private static String getRegistry(Registry registry) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append(String.format("\"id\":%s,\r\n",
				String.valueOf(registry.getId())));
		buffer.append(String.format("\"name\":\"%s\",\r\n",
				(registry.getName() != null)
						? UriUtils.encode(registry.getName()) : ""));
		buffer.append(String.format("\"alias\":\"%s\",\r\n",
				(registry.getAlias() != null)
						? UriUtils.encode(registry.getAlias()) : ""));
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