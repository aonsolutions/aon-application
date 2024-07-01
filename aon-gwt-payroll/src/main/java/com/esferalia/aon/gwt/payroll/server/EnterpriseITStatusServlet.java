package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.Parameter;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.security.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Enterprise-IT-Status", urlPatterns = { "/aon_gwt_payroll/seg-social/enterprise_it_status/*" })
public class EnterpriseITStatusServlet extends HttpServlet {

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SistemaREDService.DATE_FORMAT);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		String login  = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		
		Date from = null;
		Date to = null;
		try {
			Integer domainId = AonServletUtils.getDomainID(domainName);

			Domain domain = AON.getDomain(domainName, domainId, login);
			
			User user = AON.getUser(domain.getName(), domainId, login);

			from = simpleDateFormat.parse(req.getParameter(Parameter.START_DATE.name ()));
			to = simpleDateFormat.parse(req.getParameter(Parameter.END_DATE.name()));
			
			Writer out = new OutputStreamWriter(resp.getOutputStream(), "UTF-8");
			
			out.write("[");
			ITStatusUtils.getEnterpriseEmployeesITs(domain, user, from, to, (employee, notInAON, notInTGSS, inBothList, inTgss) -> {
				try {
					JSONObject employeeObject = new JSONObject();
					employeeObject.put("naf", employee.getNaf());
					employeeObject.put("name", employee.getName().orElse(employee.getNaf()));
					
					JSONArray itsNotInAonArray = new JSONArray();
					for (EmployeeIT employeeITNotInAON : notInAON) {
						JSONObject itNotInAonJSONObject = new JSONObject();
						itNotInAonJSONObject.put("startDate", simpleDateFormat.format(employeeITNotInAON.getStartDate()));
						itNotInAonJSONObject.put("type", employeeITNotInAON.getType());
						itNotInAonJSONObject.put("ccc", employeeITNotInAON.getCcc());
						itNotInAonJSONObject.put("nss", employeeITNotInAON.getNss());
						itNotInAonJSONObject.put("regimen", employeeITNotInAON.getRegime());
						
						employeeITNotInAON.getName().ifPresent(name -> itNotInAonJSONObject.put("name", name));
						employeeITNotInAON.getEndDate().ifPresent(endDate -> itNotInAonJSONObject.put("endDate", endDate));
						employeeITNotInAON.getDni().ifPresent(dni -> itNotInAonJSONObject.put("dni", dni));

						itsNotInAonArray.put(itNotInAonJSONObject);
					}
					employeeObject.put("itsNotInAon", itsNotInAonArray);
		
					JSONArray itsNotInTgssArray = new JSONArray();
					for (EmployeeIT employeeITNotInTgss : notInTGSS) {
						JSONObject itNotInTgssJSONObject = new JSONObject();
						itNotInTgssJSONObject.put("startDate", simpleDateFormat.format(employeeITNotInTgss.getStartDate()));
						itNotInTgssJSONObject.put("type", employeeITNotInTgss.getType());
						itNotInTgssJSONObject.put("ccc", employeeITNotInTgss.getCcc());
						itNotInTgssJSONObject.put("nss", employeeITNotInTgss.getNss());
						itNotInTgssJSONObject.put("regimen", employeeITNotInTgss.getRegime());
						itNotInTgssJSONObject.put("id", employeeITNotInTgss.getId());
						
						employeeITNotInTgss.getName().ifPresent(name -> itNotInTgssJSONObject.put("name", name));
						employeeITNotInTgss.getEndDate().ifPresent(endDate -> itNotInTgssJSONObject.put("endDate", endDate));
						employeeITNotInTgss.getDni().ifPresent(dni -> itNotInTgssJSONObject.put("dni", dni));
						
						itsNotInTgssArray.put(itNotInTgssJSONObject);
					}
					employeeObject.put("itsNotInTgss", itsNotInTgssArray);
					
					JSONArray itsInBothArray = new JSONArray();
					for (EmployeeIT employeeITInBoth : inBothList) {
						JSONObject itInBothJSONObject = new JSONObject();
						itInBothJSONObject.put("startDate", simpleDateFormat.format(employeeITInBoth.getStartDate()));
						itInBothJSONObject.put("type", employeeITInBoth.getType());
						itInBothJSONObject.put("ccc", employeeITInBoth.getCcc());
						itInBothJSONObject.put("nss", employeeITInBoth.getNss());
						itInBothJSONObject.put("regimen", employeeITInBoth.getRegime());
						
						employeeITInBoth.getName().ifPresent(name -> itInBothJSONObject.put("name", name));
						employeeITInBoth.getEndDate().ifPresent(endDate -> itInBothJSONObject.put("endDate", endDate));
						employeeITInBoth.getDni().ifPresent(dni -> itInBothJSONObject.put("dni", dni));
						
						itsInBothArray.put(itInBothJSONObject);
					}
					employeeObject.put("itsInBoth", itsInBothArray);

					JSONArray itsInTgssArray = new JSONArray();
					for (EmployeeIT employeeITInTgss : inTgss) {
						JSONObject itInTgssJSONObject = new JSONObject();
						itInTgssJSONObject.put("startDate", simpleDateFormat.format(employeeITInTgss.getStartDate()));
						itInTgssJSONObject.put("type", employeeITInTgss.getType());
						itInTgssJSONObject.put("ccc", employeeITInTgss.getCcc());
						itInTgssJSONObject.put("nss", employeeITInTgss.getNss());
						itInTgssJSONObject.put("regimen", employeeITInTgss.getRegime());
						
						employeeITInTgss.getName().ifPresent(name -> itInTgssJSONObject.put("name", name));
						employeeITInTgss.getEndDate().ifPresent(endDate -> itInTgssJSONObject.put("endDate", endDate));
						employeeITInTgss.getDni().ifPresent(dni -> itInTgssJSONObject.put("dni", dni));
						
						itsInTgssArray.put(itInTgssJSONObject);
					}
					employeeObject.put("itsInTgss", itsInTgssArray);
					
					
					
					
					out.write(employeeObject.toString());
					out.write(",");
					out.flush();
					
				} catch (IOException e) {
				}
			});
			out.write("]");

		} catch (ParseException e) {
			e.printStackTrace();
		} catch ( Exception e ) {
			throw new ServletException(e);
		}
					
	}

}
