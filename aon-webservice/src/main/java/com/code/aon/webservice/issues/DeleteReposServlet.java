package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;

@SuppressWarnings("serial")
@WebServlet(name = "DeleteReposServlet", urlPatterns = { "/delete/repos/*" })
public class DeleteReposServlet extends HttpServlet{

	private static final DBConsults DB = DBConsults.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST/DELETE METHOD");
		//String serverName = req.getServerName();
		
		//if(AonUrlApi.AON.getUrl().contains(serverName)){
			String[] pathInfo = req.getPathInfo().split("/");
			String userName = pathInfo[1];
			String domainName = pathInfo[2]; 
			
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){

				Object object = new Object();
		
				switch (pathInfo[3]) {
				case "issues":
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							if(pathInfo[5].equalsIgnoreCase("labels")){
								if(pathInfo.length > 6){
									Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
									Integer tagId = DB.getTagId(domain, userName, pathInfo[6], TagType.TASK_LABEL);
									AON.deleteTaskTag(domain.getName(), domain.getId(), userName, 
											f -> f.getTagProperty().eq(tagId).and(f.getTaskProperty().eq(taskId)));
									object = new Label().toJSON();
								}
							} else if(pathInfo[5].equalsIgnoreCase("type")){
								Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
								AON.deleteTypeTaskTag(domain.getName(), domain.getId(), userName, taskId);
								object = new Label().toJSON();			
							} else if(pathInfo[5].equalsIgnoreCase("priority")){
								Integer taskId = DB.getTaskId(domain, userName, Integer.parseInt(pathInfo[4]));
								AON.deletePriorityTaskTag(domain.getName(), domain.getId(), userName, taskId);
								object = new Label().toJSON();
							} else if(pathInfo[5].equalsIgnoreCase("user")){
								Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]));
								AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setTaskHolder(null));
								object = new User().toJSON();
							} else if(pathInfo[5].equalsIgnoreCase("workgroup")){
								Task task = DB.getTaskWithNumber(domain, userName, Integer.parseInt(pathInfo[4]));
								AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setWorkgroup(null));
								object = new User().toJSON();
							}
						}
						// DELETE ISSUE / TASK
					}
					break;
				case "labels":
					Tag tag = DB.getTag(domain, userName, pathInfo[4], TagType.TASK_LABEL);
					DB.deleteTaskTag(domain, userName, f-> f.getTagProperty().eq(tag.getId()));
					DB.deleteTag(domain, userName, tag);
					object = new JSONObject("{}");
					break;
				case "types":
					Tag tag2 = DB.getTag(domain, userName, pathInfo[4], TagType.TASK_TYPE);
					DB.deleteTaskTag(domain, userName, f-> f.getTagProperty().eq(tag2.getId()));
					DB.deleteTag(domain, userName, tag2);
					object = new JSONObject("{}");					
					break;
				default:
					break;
				}
				
				resp.setContentType("application/json;charset=UTF-8");
				addCorsHeader(resp);
				PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
				os.println(object.toString());
				os.flush();
			}
		//}
	}
	
    private void addCorsHeader(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }
    
}
