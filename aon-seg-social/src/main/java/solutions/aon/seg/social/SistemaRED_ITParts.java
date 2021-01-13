package solutions.aon.seg.social;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDateException;
import solutions.aon.seg.social.exceptions.invalidData.NoQueryData;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.ITPart;
import solutions.aon.seg.social.objects.ITPart.ITPartBuilder;
import solutions.aon.seg.social.objects.It;
import solutions.aon.seg.social.objects.It.ItBuilder;
import solutions.aon.seg.social.objects.ItPartId;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SistemaRED_ITParts {
		
	private static String URL_BASE = "https://w2.seg-social.es/isincaA/inicio.do";

	//GET ITs
	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to)throws SegSocialException{
		
		Toolkit.verifyData(new Object[] {regime,ccc,from,to});
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		ArrayList<It> its = new ArrayList<>();
		ArrayList<ITPart>  itParts = (ArrayList<ITPart>) getFullItParts(certificateInputStream, certificatePassword, certificateType, regime, ccc, from, to);
		HashMap<ItPartId, Collection<ITPart>> orderedItParts = new HashMap<>();
		
		for (ITPart itp : itParts) {
			ItPartId id = new ItPartId(itp.getWorkLeaveDate(), itp.getNaf());
			if(orderedItParts.containsKey(id)) orderedItParts.get(id).add(itp);
			else {
				ArrayList<ITPart> list = new ArrayList<>();
				list.add(itp);
				orderedItParts.put(id, list);
			}
		}
		ItBuilder builder = new ItBuilder();
		Set<ItPartId> partIds = orderedItParts.keySet(); 
		for(ItPartId id :partIds) {
			itParts  = (ArrayList<ITPart>) orderedItParts.get(id);
			
			ITPart end = null;
			ITPart start = null;
			ArrayList<ITPart> confirmations = new ArrayList<>();
			
			for(ITPart itp : itParts) {
				if(itp.getPartType().toLowerCase().equals("alta")) end = itp;
				if(itp.getPartType().toLowerCase().equals("baja")) start = itp;
				if(itp.getPartType().toLowerCase().equals("confirmaci\u00F3n") && !confirmations.contains(itp)) confirmations.add(itp);
			}
			
			its.add(builder.setStart(start)
			.setConfirmations(confirmations)
			.setEnd(end)
			.build());
		}
		
		return its;
	}
	
	//HANDLE GETFULLITPARTS EXCEPTIONS
	public static Collection<ITPart> getFullItParts(final InputStream certificateInputStream, final String certificatePassword,
	final String certificateType, String regime, String ccc, Date from, Date to) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try { return getFullItPartsImpl(certificateInputStream, certificatePassword,	certificateType, regime, ccc, from, to); } 
		catch (FailingHttpStatusCodeException e) { 
			switch (e.getStatusCode()) {
				case 403: throw new ForbiddenException();
				default: throw new StatusCodeException(); 
			}
		} catch (IOException | InterruptedException e) { throw new SegSocialException(e); }

	}

	//GET ALL THE ITPARTS
	private static Collection<ITPart> getFullItPartsImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to) throws FailingHttpStatusCodeException, IOException, InterruptedException, InvalidCertificateException, InvalidDataException {
		
		Toolkit.verifyData(new Object[] {regime,ccc,from,to});
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			
			if(Toolkit.isFuture(to)) throw new InvalidDateException();
			
			ArrayList<ITPart> itParts = new ArrayList<>();
			boolean last = false;
			
			while(!last) {
				HtmlPage origen = webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
				HtmlPage document = HtmlUnitToolkit.wait4(origen, p -> p.getAnchorByHref(URL_BASE)).orElseThrow().click();
				document = document.getAnchorByHref("/isincaA/menu.do?opcion=C").click();
				
				HtmlForm formularioPartes = document.getFormByName("BuscaPartesForm");
				formularioPartes.getInputByName("regimen").setValueAttribute(regime);
				document.getElementById("ccc1").setAttribute("value", ccc.substring(0,2));
				formularioPartes.getInputByName("ccc2").setValueAttribute(ccc.substring(2));
				
				Integer[] fromArray = Toolkit.getDateArray(from);
				Integer[] toArray = Toolkit.getDateArray(to);
				
				formularioPartes.getInputByName("fechaDesde_dd").setValueAttribute(fromArray[0].toString());
				formularioPartes.getInputByName("fechaDesde_mm").setValueAttribute(fromArray[1].toString());
				formularioPartes.getInputByName("fechaDesde_aa").setValueAttribute(fromArray[2].toString());
				
				formularioPartes.getInputByName("fechaHasta_dd").setValueAttribute(toArray[0].toString());
				formularioPartes.getInputByName("fechaHasta_mm").setValueAttribute(toArray[1].toString());
				formularioPartes.getInputByName("fechaHasta_aa").setValueAttribute(toArray[2].toString());
				
				HtmlInput show = (HtmlInput) formularioPartes.querySelectorAll("input[type=submit]").get(0);
				document = show.click();
				handleItPartErrors(document);
	
				ITPartBuilder builder = new ITPartBuilder();
				String format = "dd/MM/yyyy";
					
				DomNodeList<DomNode> rows = document.querySelectorAll(".resultados>tbody>tr");
				for (DomNode row : rows) {
					ArrayList<String> data = new ArrayList<>();
					Iterable<DomNode> cells = row.getChildren();	
						
					for(DomNode cell : cells) 				
						data.add(cell.getVisibleText().trim());
					
					String recDateStr = data.get(3);
					String naf = data.get(5);
					String workLeaveStr = data.get(7);
					String workRestartStr = data.get(9);
					String partDate = data.get(11);
					Integer partNum = null;
					
					try {partNum = Integer.parseInt(data.get(13).trim());}
					catch (NumberFormatException ignored) {}
					String partType = data.get(15);
					Boolean cancelled = Toolkit.toBoolean(data.get(17));
					Boolean wrong = Toolkit.toBoolean(data.get(19));
					
					ITPart part = builder.setReceptionDate(Toolkit.parseDate(recDateStr, format))
					.setNaf(naf)
					.setWorkLeaveDate(Toolkit.parseDate(workLeaveStr, format))
					.setWorkRestartDate(Toolkit.parseDate(workRestartStr, format))
					.setPartDate(Toolkit.parseDate(partDate, format))
					.setPartNum(partNum)
					.setPartType(partType)
					.setCanceled(cancelled)
					.setWrong(wrong)
					.build();
					
					if(itParts.contains(part)) last = true;
					else itParts.add(part);
				}
				to = itParts.get(itParts.size()-1).getReceptionDate();
			}
			return itParts;
			
		}		
	}

	//HANDLE IT PART ERRORS
	private static void handleItPartErrors(HtmlPage htmlPage) throws InvalidDataException {
		DomNode errors = htmlPage.querySelector("#errores > ul");
		if(errors != null) throw new InvalidDataException(errors.getVisibleText());
	}	

	//CONTINGENCIES 
	public enum Contingencies{
		ENFERMEDAD_COMUN,
		ACCIDENTE_NO_LABORAL,
		ACCIDENT_LABORAL,
		ENFERMEDAD_PROFESIONAL,
		PERIODOS_OBSERVACION
	}
	
	public enum TypeAccident {
		LEVE,
		GRAVE,
		MUY_GRAVE
	}
	
	//PART TYPE 
	public enum PartType{
		ALTA,
		CONFIRMACION,
		BAJA
	}
	
	//CONTRACTS
	public enum ContractType{
		FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL,
		RESTO_Y_AUTONOMOS
	}
	
	public enum SituationEmployee {
		ACTIVO,
		PERCEPTOR_DE_DESEMPLEO
	}
	
	//REGISTER IT START HANDLE EXCEPTIONS
	public static void registerItBaja(InputStream certificateInputStream, String certificatePassword,String certificateType,
			String regime, String ccc, String naf, Contingencies contingency, SituationEmployee situation_employee, Optional<String> licenseNumber, Optional<String> cias, Optional<String> occupation, Date startdate,
			ContractType contractType,  float baseCot , int cotDays, Optional<Date> fATEP, Optional<TypeAccident> typeAccident) throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException, InterruptedException {
		
		Toolkit.verifyData(new Object[]{regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, startdate, contractType, baseCot, cotDays});
		
		try{ registerItBajaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, occupation, startdate, contractType, baseCot, cotDays, fATEP, typeAccident);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}
	
	//REGISTER IT START
	private static void registerItBajaImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, String regime, String ccc,
									   String naf, Contingencies contingency, SituationEmployee situation_employee, Optional<String> licenseNumber, Optional<String> cias, Optional<String> occupation, Date startdate, 
									   ContractType contractType, float base_cot , int cotDays, Optional<Date> fATEP, Optional<TypeAccident> typeAccident) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InvalidDataException, InterruptedException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){

			HtmlPage htmlPage = webClient.getPage(URL_BASE);

			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situation_employee, PartType.BAJA);
			
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("BajaPartesForm")).orElseThrow();

			//Data Contract
			HtmlOption contract_type_opt = null;
			HtmlInput cot_base_in_1 = null;
			HtmlInput cot_base_in_2 = null;
			HtmlInput cot_days_in = null;

			String[] arr_startDate = Toolkit.dateString(startdate);
			String[] arr_base_cot = Toolkit.splitDecimal(base_cot,2);

			switch (contractType) {
				case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL: 	
					contract_type_opt = form.querySelector("#tipoContrato option:nth-child(2)");	
					cot_base_in_1 = form.getInputByName("sumaBC1");
					cot_base_in_2 = form.getInputByName("sumaBC2");
					cot_days_in = form.getInputByName("sumaDias");
					break;
				case RESTO_Y_AUTONOMOS:						
					contract_type_opt = form.querySelector("#tipoContrato option:nth-child(3)");	
					cot_base_in_1 = form.getInputByName("baseCotizacion1");
					cot_base_in_2 = form.getInputByName("baseCotizacion2");
					cot_days_in = form.getInputByName("diasCot");
					break;
			}
			contract_type_opt.click();
		
			form.getInputByName("fechaBaja_dd").setValueAttribute(arr_startDate[0]); 
			form.getInputByName("fechaBaja_mm").setValueAttribute(arr_startDate[1]); 
			form.getInputByName("fechaBaja_aa").setValueAttribute(arr_startDate[2]); 
			

			cot_base_in_1.setValueAttribute(arr_base_cot[0]);
			cot_base_in_2.setValueAttribute(arr_base_cot[1]);
			cot_days_in.setValueAttribute(cotDays + "");

			if(occupation.isPresent()){
				HtmlSelect ocup = form.querySelector("#ocupacion");
				ocup.setSelectedAttribute(occupation.get(), true);
			}
		
			if(licenseNumber.isPresent()){
				ArrayList<String> n_coleg_arr_ls = Toolkit.splitString_m(licenseNumber.get(), new int[]{2,4});
				form.getInputByName("ncol_0").setValueAttribute(n_coleg_arr_ls.get(0)); 
				form.getInputByName("ncol_1").setValueAttribute(n_coleg_arr_ls.get(1)); 
				form.getInputByName("ncol_2").setValueAttribute(n_coleg_arr_ls.get(2)); 
				form.getInputByName("cias").setValueAttribute(cias.get()); 
			}
			//Data Contract END
			
			//Data professional contigencies
			if(fATEP.isPresent()){
				String[] fATEP_string = Toolkit.dateString(fATEP.get());
				form.getInputByName("fechaATEP_dd").setValueAttribute(fATEP_string[0]); 
				form.getInputByName("fechaATEP_mm").setValueAttribute(fATEP_string[1]); 
				form.getInputByName("fechaATEP_aa").setValueAttribute(fATEP_string[2]); 
			}
			
			if(typeAccident.isPresent()) {
				HtmlOption type_accident_opt = null;
				switch (typeAccident.get()) {
					case LEVE: 	
						type_accident_opt = form.querySelector("#tipoAccidente option:nth-child(2)");	
					break;
					case GRAVE:		
						type_accident_opt = form.querySelector("#tipoAccidente option:nth-child(3)");	
					break;
					case MUY_GRAVE:				
						type_accident_opt = form.querySelector("#tipoAccidente option:nth-child(4)");	
					break;
				}
				type_accident_opt.click();
			}
			//Data professional contigencies END
			
			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);

			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);
			
			String message = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo").asText();
			System.out.println(message);
		} 
	}
	
	//REGISTER IT CONFIRMATION HANDLE EXCEPTIONS
	public static void registerItConfirmation(InputStream certificateInputStream, String certificatePassword,String certificateType,
								  String regime, String ccc, String naf, Contingencies contingency, SituationEmployee situation_employee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja, Date fconfirmation, Optional<String> npartConfimation) throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException, InterruptedException {

		Toolkit.verifyData(new Object[]{regime, ccc, naf, contingency});
		try{ registerItConfirmationImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, fbaja, fconfirmation, npartConfimation);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}

	//REGISTER IT CONFIRMATION
	private static void registerItConfirmationImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, 
			String naf, Contingencies contingency, SituationEmployee situation_employee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja, Date fconfirmation, Optional<String> npartConfimation) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InvalidDataException, InterruptedException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situation_employee, PartType.CONFIRMACION);
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("ConfirmacionPartesForm")).orElseThrow();
			
			String[] fbajaString = Toolkit.dateString(fbaja);
			String[] fconfirmationString = Toolkit.dateString(fconfirmation);
			
			form.getInputByName("fechaBaja_dd").setValueAttribute(fbajaString[0]); 
			form.getInputByName("fechaBaja_mm").setValueAttribute(fbajaString[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(fbajaString[2]);
			
			form.getInputByName("fechaParte_dd").setValueAttribute(fconfirmationString[0]); 
			form.getInputByName("fechaParte_mm").setValueAttribute(fconfirmationString[1]);
			form.getInputByName("fechaParte_aa").setValueAttribute(fconfirmationString[2]);
			
			if(npartConfimation.isPresent()) {
				form.getInputByName("numParte").setValueAttribute(npartConfimation.get());
			}
			
			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);
//			
//			//-----test------
			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);
			
			String message = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo").asText();
			System.out.println(message);
			
			Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testIt.html");
		}
	}

	//REGISTER IT END HANDLE EXCEPTIONS
	public static void registerItAlta(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, String naf, 
			Contingencies contingency, SituationEmployee situation_employee, Optional<String> licenseNumber, Optional<String> cias, 
			Date fbaja, Date falta, Optional<Date> fATEP, Optional<TypeAccident> typeAccident, String causa) throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException, InterruptedException {

		Toolkit.verifyData(new Object[]{regime, ccc, naf, contingency});
		try{ registerItAltaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, 
				licenseNumber, cias, fbaja, falta, fATEP, typeAccident, causa);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}

	//REGISTER IT
	private static void registerItAltaImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, 
			String regime, String ccc, String naf, Contingencies contingency, SituationEmployee situation_employee, 
			Optional<String> licenseNumber, Optional<String> cias, Date fbaja, Date falta, Optional<Date> fATEP, Optional<TypeAccident> typeAccident, String causa) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InvalidDataException, InterruptedException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situation_employee, PartType.ALTA);
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("AltaPartesForm")).orElseThrow();
			
			String[] faltaString = Toolkit.dateString(falta);
			String[] fbajaString = Toolkit.dateString(fbaja);
			
			form.getInputByName("fechaBaja_dd").setValueAttribute(faltaString[0]); 
			form.getInputByName("fechaBaja_mm").setValueAttribute(faltaString[1]); 
			form.getInputByName("fechaBaja_aa").setValueAttribute(faltaString[2]); 
			
			form.getInputByName("fechaAlta_dd").setValueAttribute(fbajaString[0]); 
			form.getInputByName("fechaAlta_mm").setValueAttribute(fbajaString[1]); 
			form.getInputByName("fechaAlta_aa").setValueAttribute(fbajaString[2]); 
			
			if(fATEP.isPresent()){
				String[] fATEP_string = Toolkit.dateString(fATEP.get());
				form.getInputByName("fechaAtEp_dd").setValueAttribute(fATEP_string[0]); 
				form.getInputByName("fechaAtEp_mm").setValueAttribute(fATEP_string[1]); 
				form.getInputByName("fechaAtEp_aa").setValueAttribute(fATEP_string[2]); 
			}
			
			if(typeAccident.isPresent()) {
				HtmlOption type_accident_opt = null;
				switch (typeAccident.get()) {
					case LEVE: 	
						type_accident_opt = form.querySelector("#tipoAccidente option:nth-child(2)");	
					break;
					case GRAVE:		
						type_accident_opt = form.querySelector("#tipoAccidente option:nth-child(3)");	
					break;
					case MUY_GRAVE:				
						type_accident_opt = form.querySelector("#tipoAccidente option:nth-child(4)");	
					break;
				}
				type_accident_opt.click();
			}
			
			HtmlSelect cause = form.querySelector("#causaAlta");
			cause.setSelectedAttribute("01", true);
			
			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);

			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);
			
			String message = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo").asText();
			System.out.println(message);
			
			//Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testIt.html");
		}
	}

	private static HtmlPage fillGeneralData(HtmlPage htmlPage, String regime, String ccc, String naf, Contingencies contingency, SituationEmployee situation_employee, PartType type) throws IOException, InvalidDataException {
		HtmlInput regime_in = htmlPage.querySelector("#regimen");
		HtmlInput ccc_1_in =  htmlPage.querySelector("#ccc1");
		HtmlInput ccc_2_in =  htmlPage.querySelector("#ccc2");
		HtmlInput naf_1_in =  htmlPage.querySelector("#naf1");
		HtmlInput naf_2_in =  htmlPage.querySelector("#naf2");
		HtmlOption type_opt = null;
		HtmlOption contingency_opt = null;
		HtmlOption situation_opt = null;

		switch(type) {
			case BAJA:                 
				type_opt = htmlPage.querySelector("#tipoParte option:nth-child(2)");
			break;
			case CONFIRMACION: 
				type_opt = htmlPage.querySelector("#tipoParte option:nth-child(3)");
			break;
			case ALTA: 
				type_opt = htmlPage.querySelector("#tipoParte option:nth-child(4)");
			break;
		}
		type_opt.click();
		
		switch(situation_employee) {
			case ACTIVO:                 
				situation_opt = htmlPage.querySelector("#situacionTrabajador option:nth-child(2)"); 
			break;
			case PERCEPTOR_DE_DESEMPLEO: 
				situation_opt = htmlPage.querySelector("#situacionTrabajador option:nth-child(3)"); 
			break;
		}
		situation_opt.click();
		
		switch(contingency) {
			case ENFERMEDAD_COMUN: 			
				contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(2)"); 
			break;
			case ACCIDENTE_NO_LABORAL: 		
				contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(3)");	
			break;
			case ACCIDENT_LABORAL: 			
				contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(4)"); 
			break;
			case ENFERMEDAD_PROFESIONAL: 	
				contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(5)"); 	
			break;
			case PERIODOS_OBSERVACION: 		
				contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(6)"); 	
			break;
		}
		contingency_opt.click();
		
		String[] ccc_arr =  Toolkit.SplitString(ccc, 2);
		String[] naf_arr =  Toolkit.SplitString(naf, 2);
		
		regime_in.setValueAttribute(regime);
		ccc_1_in.setValueAttribute(ccc_arr[0]);
		ccc_2_in.setValueAttribute(ccc_arr[1]);
		
		naf_1_in.setValueAttribute(naf_arr[0]);
		naf_2_in.setValueAttribute(naf_arr[1]);
		
		HtmlSubmitInput accept = (HtmlSubmitInput) htmlPage.querySelector("#datos input[type=submit]");
		htmlPage = accept.click();		
		handleItPartErrors(htmlPage);
		return htmlPage;
	}
	
	//remove IT
	public static void removeIt(InputStream certificateInputStream, String certificatePassword,String certificateType,String regime, String ccc, String naf, 
			PartType partType, Date dateBj, Date dateProcess)
		throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException, InterruptedException {
		Toolkit.verifyData(new Object[]{regime, ccc, naf, dateBj});
		try{ removeItImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType, dateBj, dateProcess);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}

	//remove IT
	private static void removeItImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, String naf, 
			PartType partType, Date dateBj, Date dateProcess)
			throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InterruptedException, InvalidDataException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){

			HtmlPage htmlPage = webClient.getPage(URL_BASE);

			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=A").click(); 
			
			String[] ccc_arr =  Toolkit.SplitString(ccc, 2);
			String[] naf_arr =  Toolkit.SplitString(naf, 2);
			String[] arr_dateMedical = Toolkit.dateString(dateBj);
			
			String[] arr_dateProcess = Toolkit.dateString(dateProcess);
			
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValueAttribute(regime);
			form.getInputByName("ccc1").setValueAttribute(ccc_arr[0]);
			form.getInputByName("ccc2").setValueAttribute(ccc_arr[1]);
			form.getInputByName("naf1").setValueAttribute(naf_arr[0]);
			form.getInputByName("naf2").setValueAttribute(naf_arr[1]);
			form.getInputByName("fechaBaja_dd").setValueAttribute(arr_dateMedical[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(arr_dateMedical[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(arr_dateMedical[2]);

			HtmlSubmitInput  continue_in = form.querySelector("#botonesANULAR input[value=Continuar]");
			htmlPage = continue_in.click();
			handleItPartErrors(htmlPage);
			
			String str_dateProcess = arr_dateProcess[0]+"/"+arr_dateProcess[1]+"/"+arr_dateProcess[2];
	
			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, str_dateProcess);
	
			if(firstColumn == null) {throw new NoQueryData("Sin datos de consulta");}
			
			htmlPage = HtmlUnitToolkit.setUrlParse(htmlPage, firstColumn).click();

			HtmlSubmitInput  anular = htmlPage.querySelector("#botones input[value=Anular]");
			htmlPage = anular.click();
			
			//confirm
			HtmlSubmitInput  confirm = htmlPage.querySelector("#general > form input[value=Confirmar]");
			htmlPage = confirm.click();
	
			//message success
			String message = htmlPage.querySelector("#miForm > div.importante > div.indent > span.TextoMensaje").asText();
			System.out.println(message);
			//Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testItRemove.html");
		}
	}
	
	//report IT
	public static byte[] pdfIt(InputStream certificateInputStream, String certificatePassword,String certificateType,String regime, String ccc, String naf, PartType partType, 
			Date dateBj, Date dateProcess)
		throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException, InterruptedException {
		Toolkit.verifyData(new Object[]{regime, ccc, naf, dateBj});
		try{ return pdfItImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType, dateBj, dateProcess);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
		return null;
	}

	//report IT
	private static byte[] pdfItImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, String naf, 
			PartType partType, Date dateBj, Date dateProcess)
			throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InterruptedException, InvalidDataException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){

			HtmlPage htmlPage = webClient.getPage(URL_BASE);


			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=E").click(); 
			
			String[] ccc_arr =  Toolkit.SplitString(ccc, 2);
			String[] naf_arr =  Toolkit.SplitString(naf, 2);
			String[] arr_dateBj = Toolkit.dateString(dateBj);
			
			String[] arr_dateProcess = Toolkit.dateString(dateBj);
		
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValueAttribute(regime);
			form.getInputByName("ccc1").setValueAttribute(ccc_arr[0]);
			form.getInputByName("ccc2").setValueAttribute(ccc_arr[1]);
			form.getInputByName("naf1").setValueAttribute(naf_arr[0]);
			form.getInputByName("naf2").setValueAttribute(naf_arr[1]);
			form.getInputByName("fechaBaja_dd").setValueAttribute(arr_dateBj[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(arr_dateBj[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(arr_dateBj[2]);

			HtmlSubmitInput  continue_in = form.querySelector("#botonesANULAR input[value=Continuar]");
			htmlPage = continue_in.click();
			handleItPartErrors(htmlPage);
			
			String str_dateProcess =  arr_dateProcess[0]+"/"+arr_dateProcess[1]+"/"+arr_dateProcess[2];

			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, str_dateProcess);
	
			if(firstColumn == null) {throw new NoQueryData("Sin datos de consulta");}
			
			htmlPage =  HtmlUnitToolkit.setUrlParse(htmlPage, firstColumn).click();
			
	        UnexpectedPage document =  HtmlUnitToolkit.setUrlParse(htmlPage, (HtmlAnchor) htmlPage.querySelector("#botones > p > a")).click();
			InputStream inp = document.getWebResponse().getContentAsStream();
			byte[] pdf = inp.readAllBytes();
			inp.close();
			return pdf;
		}
	}
	
	private static HtmlAnchor getOneAnchorPaginate(HtmlPage htmlPage, PartType partType, String fecha) throws IOException {
		String anulado = "No";
		HtmlAnchor next = null;
		boolean last = false;
		HtmlAnchor firstColumn = null;
		HtmlTable table = (HtmlTable) htmlPage.querySelector("#datos2 > fieldset > table");
		int numberCell = 0;
		String partTypeStr = null;
		switch (partType) {
			case BAJA:						
				numberCell  = 2;
				partTypeStr = "Baja";
			break;
			case ALTA: 	
				numberCell  = 3;
				partTypeStr = "Alta";
			break;
			case CONFIRMACION:						
				numberCell  = 4;
				partTypeStr = "Confirmaci\u00F3n";
			break;
		}
		if(table!=null) {
			while(!last) {
				next = (HtmlAnchor) HtmlUnitToolkit.getElConstains(htmlPage, "#datos2 > fieldset > div > a", "Siguiente");
				for (final HtmlTableRow row : table.getRows()) {
					HtmlTableCell fCell = row.getCell(numberCell);
					HtmlTableCell typeCell = row.getCell(6);
					HtmlTableCell anulCell = row.getCell(7);	

					if(fCell.getVisibleText().equalsIgnoreCase(fecha) && typeCell.getVisibleText().equalsIgnoreCase(partTypeStr) && anulCell.getVisibleText().equalsIgnoreCase(anulado)) {
						firstColumn = row.getCell(0).querySelector("a");
						break;
					}
				}
				
				if(firstColumn == null && next!=null) {
					htmlPage = HtmlUnitToolkit.setUrlParse(htmlPage, next).click();
				} else {
					last = true;
				}
			}
		}
		return firstColumn;
	}
	
}
    