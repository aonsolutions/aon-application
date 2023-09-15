/*
package com.code.aon.aio.servlet;

import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.groupware.enumeration.TaskStatus;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;

import net.aonsolutions.dump.AonDump;

public class ProcessServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public ProcessServlet() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setIntHeader("Refresh", 5);
		
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		Connection connection = null;
		
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getServerName(req));
			
		} catch (AonConnectionException e1) {
			e1.printStackTrace();
			
		}
		
		AonDump aonDump = new AonDump(connection);
		String idTask = req.getParameter("idTask");
		String cancel = req.getParameter("cancel");
		
		int id_task = Integer.parseInt(idTask);
		
		try{
		
			//DUMPING CANCELED
			if (cancel.equals("true")){
				
				Date date = new Date();
				Timestamp time = new Timestamp(date.getTime());
				
				aonDump.dslContext.update(TASK)
					.set(TASK.STATUS, (byte) TaskStatus.DELETED.ordinal())
					.set(TASK.COMMENTS, "DUMP CANCELED")
					.set(TASK.END_DATE, time)
					.where(TASK.ID.eq(id_task))
					.execute();
			}
				
			Byte percent = aonDump.dslContext.select(TASK.PERCENT).from(TASK).where(TASK.ID.eq(id_task)).fetchOne().value1();
			
			 List<String> comment = aonDump.dslContext.select(TASK_COMMENT.COMMENT)
					.from(TASK_COMMENT)
					.where((TASK_COMMENT.TASK).eq(id_task))
					.orderBy(TASK_COMMENT.ID.desc())
					.limit(5)
					.fetch(TASK_COMMENT.COMMENT);
			
			out.println("{");
			out.println("task_comment_id : " + idTask + "," );
			out.println("percent : " + percent + "," );
			
			java.util.Iterator<String> it = comment.iterator();
			while (it.hasNext())
				out.println("task_comement_comment: " + it.next());

			
			out.flush();
			out.println("}");
			
		}finally {
			out.close();
			if ( connection != null )
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
}
*/