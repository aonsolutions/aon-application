package com.code.aon.webservice.payroll;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mvel2.MVEL;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.DisabiltyLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


@SuppressWarnings("serial")
@WebServlet(name = "ContractServlet", urlPatterns = {"/contract/*",
													 "/aon_gwt_aio/contract/*"})
public class ContractServlet extends HttpServlet{
	
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
			.and(
				f.getEndDateProperty().isNull()
				.or(f.getEndDateProperty().ge(AonDateUtils.toSql(ejInitDate))))
			.and(f.getStartDateProperty().le(AonDateUtils.toSql(ejFinalDate)))
		)
		.sorted((a,b) ->  a.getPersonName().compareTo(b.getPersonName()))
		.forEach(contract -> {
			LinkedList<ContractData> list = PAYROLL.getContractDataList(domainName, domainId, login, g -> 
				g.getContractProperty().eq(contract.getId()));
			LinkedList<Double> fixedDoubleList = new LinkedList<>();fixedDoubleList.add(0.0);
			LinkedList<Double> unfixedDoubleList = new LinkedList<>();unfixedDoubleList.add(0.0);
			Double[] endFixed = new Double[]{0.0};
			Double[] endUnfixed = new Double[]{0.0};
			
			Date contractStart = (contract.getStartDate() != null && contract.getStartDate().compareTo(ejInitDate) > 0)
			        ? contract.getStartDate() : ejInitDate;
			Date contractEnd = (contract.getEndDate() != null && contract.getEndDate().compareTo(ejFinalDate) < 0)
			        ? contract.getEndDate() : ejFinalDate;
			
			if(list.stream().filter(e -> e.getName().equals("COEFICIENTE_PARCIALIDAD")).count() > 0){ 
				list.stream().filter(e -> e.getName().equals("COEFICIENTE_PARCIALIDAD")).forEach(c -> {
					// Intersección real del periodo de parcialidad con el ejercicio
					Date pStart = c.getStartDate();
				    Date pEnd   = c.getEndDate() != null ? c.getEndDate() : contractEnd;   // fallback = fin de contrato, no fin de año
				    Date start  = pStart.compareTo(contractStart) > 0 ? pStart : contractStart;
				    Date end    = pEnd.compareTo(contractEnd)     < 0 ? pEnd   : contractEnd;
				    if (end.compareTo(start) < 0) return;   // tramo fuera de la vida del contrato en el año
					
					String expression = c.getExpression();
					
					if(expression.contains(","))
						expression = normalizarExpresion(expression);
					
					// Try get coef, 1.00 by default or wrong format expression
					Number number = 1.00;
					try {
						number = (Number) MVEL.eval(expression);
					} catch (Exception e) {
						System.err.println("MVEL.eval error -> " + c.getExpression());
					}
					Double coef = number.doubleValue();
					
					list.stream().filter(o -> o.getName().equals("TC2") && (o.getEndDate() == null || o.getEndDate().compareTo(start) > 0) && o.getStartDate().compareTo(end) <= 0)
					.filter(distinctByKey(p -> p.getName() + " " + p.getStartDate() + " " + p.getEndDate()))
					.forEach(h -> {
						Date start2 = h.getStartDate().compareTo(start) > 0 ? h.getStartDate() : start;
						Date end2 = (h.getEndDate() != null && h.getEndDate().compareTo(end) < 0) ? AonDateUtils.addDays(h.getEndDate(),1) : end;
						if (end2.compareTo(start2) >= 0) {
							Long a = AonDateUtils.getDaysBetweenDates(start2, end2);
							String b = h.getExpression().substring(1,2);
							if(b.equals("1") || b.equals("2") || b.equals("3")){
								fixedDoubleList.add(coef * a.doubleValue());
							} else {
								unfixedDoubleList.add(coef * a.doubleValue());
							}
							if(end2.compareTo(ejFinalDate) >= 0){
								if(b.equals("1") || b.equals("2") || b.equals("3")){
									endFixed[0] = 1.0;
								} else {
									endUnfixed[0] = 1.0;
								}
							}
						}
					});
				});	
			} else {
				Date start = AonDateUtils.getYear(contract.getStartDate()) == year ? contract.getStartDate() : ejInitDate;
				Date end = contract.getEndDate() != null && AonDateUtils.getYear(contract.getEndDate()) == year ? contract.getEndDate() : ejFinalDate;	
				list.stream().filter(o -> o.getName().equals("TC2") && (o.getEndDate() == null || o.getEndDate().compareTo(start) > 0) && o.getStartDate().compareTo(end) <= 0)
				.filter(distinctByKey(p -> p.getName() + " " + p.getStartDate() + " " + p.getEndDate()))
				.forEach(h -> {
					Date start2 = h.getStartDate().compareTo(start) > 0 ? h.getStartDate() : start;
					Date end2 = (h.getEndDate() != null && h.getEndDate().compareTo(end) < 0) ? AonDateUtils.addDays(h.getEndDate(),1) : end;
					Long a = AonDateUtils.getDaysBetweenDates(start2, end2);
					String b = h.getExpression().substring(1,2);
					if(b.equals("1") || b.equals("2") || b.equals("3")){
						fixedDoubleList.add(a.doubleValue());
					} else {
						unfixedDoubleList.add(a.doubleValue());
					}
					if(end2.compareTo(ejFinalDate) >= 0){
						if(b.equals("1") || b.equals("2") || b.equals("3")){
							endFixed[0] = 1.0;
						} else {
							endUnfixed[0] = 1.0;
						}
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
				json.put("start_date", AonDateUtils.dateTimeFormat(contract.getStartDate()));
				json.put("end_date", contract.getEndDate() != null ? AonDateUtils.dateTimeFormat(contract.getEndDate()) : "-");
				json.put("gender", ToJSON.objectToJSON(person.get().getGender().ordinal(), person.get().getGender().getName()));
				Double fixed = fixedDoubleList.stream().mapToDouble(i -> i).sum() / 365;
				Double unfixed = unfixedDoubleList.stream().mapToDouble(i -> i).sum() / 365;
				json.put("fixed", AonMathUtils.round(fixed));
				json.put("unfixed", AonMathUtils.round(unfixed));
				json.put("end_fixed", AonMathUtils.round(endFixed[0]));
				json.put("end_unfixed", AonMathUtils.round(endUnfixed[0]));
	
				// TODO find by CategoryDescription & agreementLevel the correct agreementLevelCategory otherwise keep like now
				if(null != contract.getAgreementLevel()) {
					Optional<AgreementLevelCategory> agreementLevelCategory = 
						AonStringUtils.isBlank(contract.getCategoryDescription()) 
						? Optional.empty()	
						: PAYROLL.getAgreementLevelCategory(domainName, domainId, login, 
							f -> f.getAgreementLevelProperty().eq(contract.getAgreementLevel()).and(f.getDescriptionProperty().likeIgnoreCase(contract.getCategoryDescription())));
				
					if(agreementLevelCategory.isPresent())
						json.put("category",  ToJSON.objectToJSON(agreementLevelCategory.get().getId(), contract.getCategoryDescription()));
					else {
						agreementLevelCategory = PAYROLL.getAgreementLevelCategory(domainName, domainId, login, 
								f -> f.getAgreementLevelProperty().eq(contract.getAgreementLevel()));
						
						if(agreementLevelCategory.isPresent())
							json.put("category",  ToJSON.objectToJSON(agreementLevelCategory.get().getId(), contract.getCategoryDescription()));
						else
							json.put("category",  ToJSON.objectToJSON(-1, contract.getCategoryDescription()));
					}
				} else
					json.put("category",  ToJSON.objectToJSON(-1, contract.getCategoryDescription()));
				
				
				Integer disabilityId = (irpfData.isPresent() && irpfData.get().getDisability() != null) ? irpfData.get().getDisability().intValue() : -1;
				String disabilityName = (irpfData.isPresent() && irpfData.get().getDisability() != null) ? DisabiltyLevel.values()[disabilityId].getName() : "-"; 
				json.put("disability", ToJSON.objectToJSON(disabilityId, disabilityName));
				
				array.put(json);
			}
		});
		return array;
	}
	
	public static String normalizarExpresion(String expr) {
	    expr = expr.trim();

	    // Eliminar puntos miles
	    expr = expr.replaceAll("(\\d)\\.(\\d{3})", "$1$2");

	    // Convierte comas en puntos
	    expr = expr.replace(',', '.');

	    return expr;
	}

	
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
	    Map<Object, Boolean> map = new ConcurrentHashMap<>();
	    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
