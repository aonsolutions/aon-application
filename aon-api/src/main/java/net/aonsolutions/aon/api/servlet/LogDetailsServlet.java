package net.aonsolutions.aon.api.servlet;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.LogData;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet("/viewDetailsLogs")
public class LogDetailsServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		AonApiData api = initialize(request);

		try {

			String logId = request.getParameter("id");
			int idLog = Integer.parseInt(logId);
			LogData logData = AON_SOLUTIONS.getLogData(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), f -> f.getIdProperty().eq(idLog));

			StringBuilder htmlResponse = new StringBuilder();
			htmlResponse.append("<html><body>");
			htmlResponse.append("<h2>Detalles del Log</h2>");
			htmlResponse.append("<p><strong>ID:</strong> " + logData.getId() + "</p>");
			htmlResponse.append("<p><strong>Fecha:</strong> " + logData.getDate() + "</p>");
			htmlResponse.append("<p><strong>Mensaje:</strong> " + logData.getMessage() + "</p>");
			htmlResponse.append("<a href='viewLogs'>Volver a la lista de logs</a>");
			htmlResponse.append("</body></html>");

			response.getWriter().write(htmlResponse.toString());

		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
