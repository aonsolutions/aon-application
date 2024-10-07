package net.aonsolutions.aon.api.servlet;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.LogData;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet("/viewLogs")
public class LogServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		AonApiData api = initialize(request);

		try {

			Stream<LogData> logStream = AON_SOLUTIONS.getLogsStream(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin());

			StringBuilder htmlResponse = new StringBuilder();
			htmlResponse.append("<html><body>");
			htmlResponse.append("<h2>Lista de Logs</h2>");
			htmlResponse.append("<ul>");

			logStream.forEach(log -> {
				htmlResponse.append("<li>");
				htmlResponse.append("<a href='viewLogDetails?id=" + log.getId() + "'>");
				htmlResponse.append("Log ID: ").append(log.getId()).append(" - Fecha: ").append(log.getDate());
				htmlResponse.append("</a>");
				htmlResponse.append("</li>");
			});

			htmlResponse.append("</ul>");
			htmlResponse.append("</body></html>");

			response.getWriter().write(htmlResponse.toString());

		} catch (Exception e) {
			e.getStackTrace();
//	            response.getWriter().write("Error al recuperar los logs: " + e.getMessage());
		}
	}
}
