package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.type.TagType;

@SuppressWarnings("serial")
@WebServlet(name = "DeleteReposServlet", urlPatterns = { "/delete/repos/*",
														 "/aon_gwt_aio/delete/repos/*"})
public class DeleteReposServlet extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(DeleteReposServlet.class.getName());
	private static final DBConsults DB = DBConsults.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Delete Repos Servlet - GET METHOD");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Delete Repos Servlet - POST METHOD");
	
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[1];
		String domainName = pathInfo[2]; 
			
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){
			Object object = new Object();
			
			switch (pathInfo[3]) {
			case MSG.ISSUES:
				if(pathInfo.length > 4){
					if(pathInfo.length > 5){
						if(MSG.LABELS.equalsIgnoreCase(pathInfo[5])){
							if(pathInfo.length > 6){
								Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
								Integer tagId = DB.getTagId(domain, userName, pathInfo[6], TagType.TASK_LABEL);
								AON.deleteTaskTag(domain.getName(), domain.getId(), userName, 
										f -> f.getTagProperty().eq(tagId).and(f.getTaskProperty().eq(taskId)));
								object = new Label().toJSON();
							}
						} else if(MSG.TYPE.equalsIgnoreCase(pathInfo[5])){
							Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
							AON.deleteTypeTaskTag(domain.getName(), domain.getId(), userName, taskId);
							object = new Label().toJSON();			
						} else if(MSG.PRIORITY.equalsIgnoreCase(pathInfo[5])){
							Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
							AON.deletePriorityTaskTag(domain.getName(), domain.getId(), userName, taskId);
							object = new Label().toJSON();
						} else if(MSG.USER.equalsIgnoreCase(pathInfo[5])){
							Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]));
							AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setTaskHolder(null));
							object = new User().toJSON();
						} else if(MSG.WORKGROUP.equalsIgnoreCase(pathInfo[5])){
							Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]));
							AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setWorkgroup(null));
							object = new User().toJSON();
						}
					} else {
						// DELETE TASK FOR EVER!!!!
						Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]));
						AON.deleteTaskComment(domain.getName(), domain.getId(), userName, f -> f.getTaskProperty().eq(task.getId()));
						AON.deleteTaskTag(domain.getName(), domain.getId(), userName, f -> f.getTaskProperty().eq(task.getId()));
						AON.deleteTaskEvent(domain.getName(), domain.getId(), userName, f -> f.getTaskProperty().eq(task.getId()));
						AON.deleteTask(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(task.getId()));
						object = new JSONObject();
					}
				}
				break;
			case MSG.TAG:
				Integer tagId = Integer.parseInt(pathInfo[4]);
				DB.deleteTaskTag(domain, userName, f-> f.getTagProperty().eq(tagId));
				DB.deleteTag(domain, userName, tagId);
				object = new JSONObject();
				break;
			default:
				break;
			}
				
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}

}
