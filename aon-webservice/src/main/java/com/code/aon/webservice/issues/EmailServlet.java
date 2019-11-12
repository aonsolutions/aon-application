package com.code.aon.webservice.issues;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.webservice.common.MSG;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskStatus;

@SuppressWarnings("serial")
@WebServlet(name = "EmailFunctionServlet", urlPatterns = { "/emailFunction/*",
														   "/aon_gwt_aio/ms/emailFunction/*"})
public class EmailServlet extends HttpServlet{

	private static final DBConsults DB = DBConsults.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[1];
		String domainName = pathInfo[2]; 
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){
			if(pathInfo.length > 4 && MSG.CLOSE.equals(pathInfo[3])){
				closeTask(domain, userName, Integer.parseInt(pathInfo[4]));
				RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/login/popupclose.jsp");
				dispatcher.forward(req, resp);
			}
		}
	}
	
	private void closeTask(Domain domain, String login, Integer taskId) {
		Task task = DB.getTask(domain, login, taskId);
		if(!task.getStatus().equals(TaskStatus.FINISHED.value())){
			task.setStatus(TaskStatus.FINISHED.value())
				.setModificationUser(login).setModificationDate(Calendar.getInstance().getTime())
				.setEndDate(Calendar.getInstance().getTime());
		
			TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
				.setEvent("closed").setTask(task.getId()).setCreationUser(login);
		
			AON.createTaskEvent(domain.getName(), domain.getId(), login, taskEvent, task.getId());
			AON.updateTask(domain.getName(), domain.getId(), login, task);								
		}
	}
}
