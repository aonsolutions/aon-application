package net.aonsolutions.aon.api.utils;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.json.JSONObject;

import com.esferalia.aon.in.payroll.tgss.report.CCCLaboralLife;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.ServicioREDEmployee;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;

public class ComunicaUtils {

	private ComunicaUtils() {
	    throw new IllegalStateException("Utility class");
	}

	/**
	 * GET EMPLOYEES REAL DATA  SEG SOCIAL
	 * @return ArrayList<Employee>
	 */
	public static List<Employee> getEmployeesPrev(Certificate certificate, List<CCCInfo> cccs) {
		 ArrayList<Employee> employees = new ArrayList<>();
		 for (CCCInfo ccc : cccs) {
			    String regimen = ccc.getCccRegimeCode();
	            String cti = ccc.getCccAccount();
	            try {
	            	employees.addAll(ServicioREDEmployee.getPrevEmployees(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), regimen, cti) );
				} catch (Exception e) {e.printStackTrace();}
		 }
	     return employees;
	}
	
	/**
	 * GET EMPLOYEES OLD DATA SEG SOCIAL
	 * @param startDateIni startDateIni search
	 * @param certificate
	 * @param cccs
	 * @return ArrayList<Employee>
	 */
	public static List<Employee> getEmployeesOld(Date startDateIni, Certificate certificate, List<CCCInfo> cccs) {
		 ArrayList<Employee> employees = new ArrayList<>();
	     List<Date> startDates = ComunicaUtils.getStartDates(startDateIni);
	     
	     for (CCCInfo ccc : cccs) {
		     String ctaCti = ccc.getCccAccount();
		     String regime = ccc.getCccRegimeCode();
			 for (Date startDate : startDates) {
			 	Date endDate = AonDateUtils.getMonthLastDay(startDate);
			 	if( com.esferalia.aon.watson.util.AonDateUtils.compare(endDate, new Date()) > 0 ) 
			 		endDate = new Date();
			 	
			 	System.out.println("----- START_DATE: "+AonDateUtils.format(startDate, "dd-MM-yyyy")+ " END_DATE: "+AonDateUtils.format(endDate, "dd-MM-yyyy")+" -----");

	            try {
		  			byte[] pdf = ServicioREDEmployee.getCccLaboralLifePOST(
		  					new ByteArrayInputStream(certificate.getData()), 
		  					certificate.getPassword(), 
		  					certificate.getType(), 
		  					regime, 
		  					ctaCti, 
		  					startDate, 
		  					endDate
		  			);
		  		
	  		        CCCLaboralLife.parse(new ByteArrayInputStream(pdf), new com.esferalia.aon.in.payroll.tgss.report.Employee.EmployeeBuilder()).forEach(data->{
		  		    	EmployeeBuilder empl = new EmployeeBuilder()
		  		    	.setName(data.getName())
		  		    	.setNss(data.getNss())
		  		    	.setIpf(data.getIpf())
		  		    	.setCtaCti(data.getCtaCti())
		  		    	.setRegime(data.getRegime())
		  		    	.setFra(data.getFra());
		  		    	
		  		    	data.getGc().ifPresent(empl::setGc);
		  		    	data.getFrb().ifPresent(empl::setFrb);
		  		    	data.getContract().ifPresent(empl::setContract);
		  		    	data.getOccupation().ifPresent(empl::setOcup);
		  		    	data.getCoef().ifPresent(coef-> empl.setFactor(Double.parseDouble(coef)));
		  		    	
		  		    	employees.add(empl.build());
		  		    
	  		      });
	  		  } catch(Exception e)  {e.printStackTrace();}
			 }
	     }	
	     return employees;
	}
	
	/**
	 * DISTINCT STREAM
	 * @param <T>
	 * @param keyExtractor
	 * @return 
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	/**
	 * 
	 * @param Date start date
	 * @return return dates for month
	 */
	public static List<Date> getStartDates(Date date) {
	    Date startDate = AonDateUtils.getMonthFirstDay(date);
        Date endDate = new Date();
        List<Date> dates = new ArrayList<>();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(GregorianCalendar.MONTH, 1);
        }
        return dates;
	}
	
	/**
	 * 
	 * @param validate data required
	 * @throws Exception
	 */
	public static void validateAlta(JSONObject params) throws Exception {
		String error = null;
		if(params.isNull("ctaCti")) {
			error = "Cuenta de cotizaci\u00f3n requerida";
		} else if(params.isNull("regime")) {
			error = "Regimen requerido";
		} else if(params.isNull("nss")) {
			error = "N\u00famero de afiliaci\u00f3n requerido";
		} else if(params.isNull("ipf")) {
	    	error = "DNI/NIE requerido";	
	    } else if(params.isNull("fecha")) {
			error = "Fecha requerida";
		} else if(params.isNull("gc")) {			
			error = "Grupo de cotizaci\u00f3n requerido";
		} else if(params.isNull("contract")) {
			error = "Tipo de contrato requerido";
		} 

		if(error!=null) {
			throw new Exception(error);
		}
	}
}
