package net.aonsolutions.aon.api.servlet.invoice;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;


@SuppressWarnings("serial")
@WebServlet(name = "AonQrInvoice", urlPatterns = {"/dip/*",
												"/ms/api/dip/*",
												"/aon_gwt_aio/dip/*"
											})
public class AonQrInvoiceServlet extends AonApiHttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String document = req.getParameter("d") != null ? req.getParameter("d") : "";
		String date = req.getParameter("f") != null ? req.getParameter("f") : "";
		String serie = req.getParameter("s") != null ? req.getParameter("s") : "";;
		String number = req.getParameter("n") != null ? req.getParameter("n") : "";
		String total = req.getParameter("t") != null ? req.getParameter("t") : "";
		
		String html = "<div>"
				+ "    <div style=\"margin: 20px;\">"
				+ "        <img src=\"https://aon.solutions/assets/aon-logo.png\" style=\"width: 250px;\">"
				+ "    </div>"
				+ "    <table style=\"border:  2px solid lightgray;/*! margin: 50px; */padding: 20px;margin: 20px;\">"
				+ "        <tr>"
				+ "          <td style=\"width: 300px;\">NIF DEL EMISOR</td>"
				+ "          <td>"+ document +"</td>"
				+ "        </tr>"
				+ "        <tr>"
				+ "            <td>FECHA DE LA FACTURA</td>"
				+ "            <td>"+ date +"</td>"
				+ "          </tr>"
				+ "        <tr>"
				+ "          <td>SERIE</td>"
				+ "          <td>"+ serie +"</td>"
				+ "        </tr>"
				+ ""
				+ "        <tr>"
				+ "            <td>NÚMERO</td>"
				+ "            <td>"+ number +"</td>"
				+ "          </tr>"
				+ ""
				+ "          <tr>"
				+ "            <td>IMPORTE TOTAL</td>"
				+ "            <td>"+ total +"</td>"
				+ "          </tr>"
				+ "      </table>"
				+ "</div>";
		responseHtml(req, resp, html);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
}
