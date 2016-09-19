package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST/DELETE METHOD");
		String serverName = req.getServerName();
		
		//if(AonUrlApi.AON.getUrl().contains(serverName)){
			String[] pathInfo = req.getPathInfo().split("/");
			String userName = pathInfo[1];
			String domainName = pathInfo[2]; 
			
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				String s = req.getReader().readLine();
				if(s == null) s = "{}";
				JSONObject json = new JSONObject(s);
				Object object = new Object();
		
				switch (pathInfo[3]) {
				case "issues":
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							if(pathInfo[5].equalsIgnoreCase("labels")){
								if(pathInfo.length > 6){
									Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
									Integer tagId = DBConsults.getTagId(domain.getName(), domain.getId(), userName, pathInfo[6], TagType.TASK_LABEL);
									AON.deleteTaskTag(domain.getName(), domain.getId(), userName, 
											f -> f.getTagProperty().eq(tagId).and(f.getTaskProperty().eq(taskId)));
									object = new Label().toJSON();
								}
							} else if(pathInfo[5].equalsIgnoreCase("type")){
								Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
								AON.deleteTypeTaskTag(domain.getName(), domain.getId(), userName, taskId);
								object = new Label().toJSON();			
							} else if(pathInfo[5].equalsIgnoreCase("priority")){
								Integer taskId = DBConsults.getTaskId(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
								AON.deletePriorityTaskTag(domain.getName(), domain.getId(), userName, taskId);
								object = new Label().toJSON();
							} else if(pathInfo[5].equalsIgnoreCase("user")){
								Task task = DBConsults.getTaskWithNumber(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
								AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setTaskHolder(null));
								object = new User().toJSON();
							} else if(pathInfo[5].equalsIgnoreCase("workgroup")){
								Task task = DBConsults.getTaskWithNumber(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[4]));
								AON.updateTaskUser(domain.getName(), domain.getId(), userName, task.setWorkgroup(null));
								object = new User().toJSON();
							}
						}
						// DELETE ISSUE / TASK
					}
					break;
				case "labels":
					Tag tag = DBConsults.getTag(domain.getName(), domain.getId(), userName, pathInfo[4], TagType.TASK_LABEL);
					AON.deleteTag(domain.getName(), domain.getId(), userName, tag); 
					object = new JSONObject("{}");
					break;
				case "types":
					Tag tag2 = DBConsults.getTag(domain.getName(), domain.getId(), userName, pathInfo[4], TagType.TASK_TYPE);
					AON.deleteTag(domain.getName(), domain.getId(), userName, tag2); 
					object = new JSONObject("{}");					
					break;
				case "priorities":
					Tag tag3 = DBConsults.getTag(domain.getName(), domain.getId(), userName, pathInfo[4], TagType.TASK_PRIORITY);
					AON.deleteTaskTag(domain.getName(), domain.getId(), userName, f -> f.getTagProperty().eq(tag3.getId()));
					AON.deleteTag(domain.getName(), domain.getId(), userName, tag3); 
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
    

	private String getMd5(String str){
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
