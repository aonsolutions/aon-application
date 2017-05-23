package com.code.aon.webservice.payroll;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.DisabiltyLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;


@SuppressWarnings("serial")
@WebServlet(name = "ContractServlet", urlPatterns = {"/contract/*",
													 "/aon_gwt_aio/contract/*"})
public class ContractServlet extends HttpServlet{
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static final Logger LOGGER  = Logger.getLogger(ContractServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Contract Servlet - GET METHOD");
		String[] pathInfo = req.getPathInfo().split("/");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(pathInfo[pathInfo.length-1]);
		String domainName = parameters.get("domain");
		String login = parameters.get("login");	
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String md5 = Utils.getMd5(login+domainName);

		if(accessToken.equals(md5)){
			if(pathInfo.length > 1){
				Object object = new Object();
				Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
				if("media_list".equals(pathInfo[1])){
					object = getContractMediaList(domain, login, Integer.parseInt(parameters.get("year")));
				}
				Utils.giveBack(req, resp, object, new JSONObject());
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Contract Servlet - POST METHOD");
	}
	
	public static JSONArray getContractMediaList(Domain domain, String login, Integer year){
		String domainName = domain.getName();
		Integer domainId = domain.getId();
		JSONArray array = new JSONArray();
		Date ejInitDate = AonDateUtils.getDate(year, 0, 1);
		Date ejFinalDate = AonDateUtils.getDate(year, 11, 31);
		PAYROLL.getContractStream(domainName, domainId, login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(AonDateUtils.toSql(ejInitDate))))
		)
		.sorted((a,b) ->  AON.getPerson(domainName, domainId, login, f-> f.getIdProperty().eq(a.getPerson())).get().getName().compareTo(
				 AON.getPerson(domainName, domainId, login, f-> f.getIdProperty().eq(b.getPerson())).get().getName()))
		.forEach(contract -> {
			LinkedList<ContractData> list = PAYROLL.getContractDataList(domainName, domainId, login, g -> 
				g.getContractProperty().eq(contract.getId()));
			LinkedList<Double> fixedDoubleList = new LinkedList<>();fixedDoubleList.add(0.0);
			LinkedList<Double> unfixedDoubleList = new LinkedList<>();unfixedDoubleList.add(0.0);
				
			if(list.stream().filter(e -> e.getName().equals("COEFICIENTE_PARCIALIDAD")).count() > 0){ 
				list.stream().filter(e -> e.getName().equals("COEFICIENTE_PARCIALIDAD")).forEach(c -> {
					Date start = AonDateUtils.getYear(c.getStartDate()) == year ? c.getStartDate() : ejInitDate;
					Date end = c.getEndDate() != null && AonDateUtils.getYear(c.getEndDate()) == year ? c.getStartDate() : ejFinalDate;
					Double coef = Double.parseDouble(c.getExpression());
					list.stream().filter(o -> o.getName().equals("TC2") && o.getEndDate().compareTo(start) > 0 && o.getStartDate().compareTo(end) <= 0).forEach(h -> {
						Date start2 = h.getStartDate().compareTo(start) > 0 ? h.getStartDate() : start;
						Date end2 = (h.getEndDate() != null && h.getEndDate().compareTo(end) < 0) ? AonDateUtils.addDays(h.getEndDate(),1) : end;
						Long a = AonDateUtils.getDaysBetweenDates(start2, end2);
						String b = h.getExpression().substring(1,2);
						if(b.equals("1") || b.equals("2") || b.equals("3")){
							fixedDoubleList.add(coef * a.doubleValue());
						} else {
							unfixedDoubleList.add(coef * a.doubleValue());
						}
					});
				});	
			} else {
				Date start = AonDateUtils.getYear(contract.getStartDate()) == year ? contract.getStartDate() : ejInitDate;
				Date end = contract.getEndDate() != null && AonDateUtils.getYear(contract.getEndDate()) == year ? contract.getEndDate() : ejFinalDate;	
				list.stream().filter(o -> o.getName().equals("TC2") && (o.getEndDate() == null || o.getEndDate().compareTo(start) > 0) && o.getStartDate().compareTo(end) <= 0).forEach(h -> {
					Date start2 = h.getStartDate().compareTo(start) > 0 ? h.getStartDate() : start;
					Date end2 = (h.getEndDate() != null && h.getEndDate().compareTo(end) < 0) ? AonDateUtils.addDays(h.getEndDate(),1) : end;
					Long a = AonDateUtils.getDaysBetweenDates(start2, end2);
					String b = h.getExpression().substring(1,2);
					if(b.equals("1") || b.equals("2") || b.equals("3")){
						fixedDoubleList.add(a.doubleValue());
					} else {
						unfixedDoubleList.add(a.doubleValue());
					}
				});
			}
			
			Optional<ContractData> qg=  list.stream().filter(e -> e.getName().equals("GRUPO_COTIZACION")).findFirst();
			Optional<IrpfData> irpfData = PAYROLL.getIrpfData(domainName, domainId, login, w -> w.getContractProperty().eq(contract.getId()));
			Optional<Person> person = AON.getPerson(domainName, domainId, login, f-> f.getIdProperty().eq(contract.getPerson()));
			
		//	Date start = AonDateUtils.getYear(contract.getStartDate()) == year ? contract.getStartDate() : ejInitDate;
		//	Date end = contract.getEndDate() != null && AonDateUtils.getYear(contract.getEndDate()) == year ? contract.getEndDate() : ejFinalDate;
			
			if(qg.isPresent()){
				JSONObject json = new JSONObject();
				String q = qg.get().getExpression();
				json.put("quotation_group",q.substring(1, q.length()-1)); 
				json.put("name", person.isPresent() ? person.get().getName() : "-");
				json.put("document", person.isPresent() ? person.get().getDocument() : "-");
				json.put("start_date", dateTimeFormat.format(contract.getStartDate()));
				json.put("end_date", contract.getEndDate() != null ? dateTimeFormat.format(contract.getEndDate()) : "-");
				json.put("gender", ToJSON.objectToJSON(person.get().getGender().ordinal(), person.get().getGender().getName()));
				Double fixed = fixedDoubleList.stream().mapToDouble(i -> i).sum() / 365;
				Double unfixed = unfixedDoubleList.stream().mapToDouble(i -> i).sum() / 365;
				json.put("fixed", AonMathUtils.round(fixed));
				json.put("unfixed", AonMathUtils.round(unfixed));
			
				Integer disabilityId = (irpfData.isPresent() && irpfData.get().getDisability() != null) ? irpfData.get().getDisability().intValue() : -1;
				String disabilityName = (irpfData.isPresent() && irpfData.get().getDisability() != null) ? DisabiltyLevel.values()[disabilityId].getName() : "-"; 
				json.put("disability", ToJSON.objectToJSON(disabilityId, disabilityName));
				
				array.put(json);
			}
		});
		return array;
	}
	
}
