package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDateException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.ITPart;
import solutions.aon.seg.social.objects.It;
import solutions.aon.seg.social.objects.ItPartId;
import solutions.aon.seg.social.objects.ITPart.ITPartBuilder;
import solutions.aon.seg.social.objects.It.ItBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaREDITParts {
		
	
	//GET ITs
	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to)throws SegSocialException{
		
		Toolkit.verifyData(new Object[] {regime,ccc,from,to});
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		ArrayList<It> its = new ArrayList<It>();
		ArrayList<ITPart>  itParts = (ArrayList<ITPart>) getFullItParts(certificateInputStream, certificatePassword, certificateType, regime, ccc, from, to);
		HashMap<ItPartId, Collection<ITPart>> orderedItParts = new HashMap<ItPartId, Collection<ITPart>>();
		
		for (ITPart itp : itParts) {
			ItPartId id = new ItPartId(itp.getWorkLeaveDate(), itp.getNaf());
			if(orderedItParts.containsKey(id)) orderedItParts.get(id).add(itp);
			else {
				ArrayList<ITPart> list = new ArrayList<ITPart>();
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
			ArrayList<ITPart> confirmations = new ArrayList<ITPart>();
			
			for(ITPart itp : itParts) {
				if(itp.getPartType().toLowerCase().equals("alta")) end = itp;
				if(itp.getPartType().toLowerCase().equals("baja")) start = itp;
				if(itp.getPartType().toLowerCase().equals("confirmación") && !confirmations.contains(itp)) confirmations.add(itp);
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
		}
		catch (MalformedURLException e) { throw new SegSocialException(e); }
		catch (IOException e) { throw new SegSocialException(e); } 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		
	}

	//GET ALL THE ITPARTS
	private static Collection<ITPart> getFullItPartsImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, InvalidCertificateException, InvalidDataException {
		
		Toolkit.verifyData(new Object[] {regime,ccc,from,to});
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			
			if(Toolkit.isFuture(to)) throw new InvalidDateException();
			
			ArrayList<ITPart> itParts = new ArrayList<ITPart>();
			Boolean last = false;
			
			while(!last) {
				HtmlPage origen = webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
				HtmlPage htmlPage = HtmlUnitToolkit.wait4(origen, p -> p.getAnchorByHref("https://w2.seg-social.es/isincaA/inicio.do")).orElseThrow().click();
				htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=C").click();
				
				HtmlForm formularioPartes = htmlPage.getFormByName("BuscaPartesForm");
				formularioPartes.getInputByName("regimen").setValueAttribute(regime);
				htmlPage.getElementById("ccc1").setAttribute("value", ccc.substring(0,2));
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
				htmlPage = show.click();
				handleItPartErrors(htmlPage);
	
				ITPartBuilder builder = new ITPartBuilder();
				String format = "dd/MM/yyyy";
					
				DomNodeList<DomNode> rows = htmlPage.querySelectorAll(".resultados>tbody>tr");
				for (DomNode row : rows) {
					ArrayList<String> data = new ArrayList<String>(); 
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
					catch (NumberFormatException e) {}
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
		DomNode errors = htmlPage.querySelector("#errores");
		if(errors != null) throw new InvalidDataException();
		
	}	
	
	public static String[] causes = {"Baja","Confirmación","Alta"};
	
	//CONTINGENCIES 
	public static enum Contingencies{
		ENFERMEDAD_COMUN,
		ACCIDENTE_NO_LABORAL,
		ACCIDENT_LABORAL,
		ENFERMEDAD_PROFESIONAL,
		PERIODOS_OBSERVACIÓN;
	}
	
	//PART TYPE 
	public static enum PartType{
		ALTA,
		CONFIRMACION,
		BAJA;
	}
	
	//CONTRACTS
	public static enum ContractType{
		FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL,
		RESTO_Y_AUTONOMOS
	}
	
	//REGISTER IT START HANDLE EXCEPTIONS
	public static void addItStart(InputStream certificateInputStream, String certificatePassword,String certificateType,
			String regime, String ccc, String naf, Contingencies contingency, String licenseNumber, String cias, Date startdate, ContractType contractType) throws StatusCodeException, InvalidCertificateException, MalformedURLException, IOException, InvalidDataException {
		
		Toolkit.verifyData(new Object[]{regime, ccc, naf, contingency, licenseNumber, cias, startdate, contractType});
		
		try{ addItStartImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency,licenseNumber, cias, startdate, contractType);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}
	
	//REGISTER IT START
	private static void addItStartImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc,
			String naf, Contingencies contingency, String licenseNumber, String cias, Date startdate, ContractType contractType) throws InvalidCertificateException, FailingHttpStatusCodeException, MalformedURLException, IOException, InvalidDataException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/isincaA/inicio.do");
			HtmlOption type = htmlPage.querySelector("#tipoParte option:nth-child(2)");
			htmlPage = fillCommonData(type.click(), regime, ccc, naf, contingency, PartType.BAJA);
			
			Integer[] arr_startDate = Toolkit.getDateArray(startdate);		
			ArrayList<String> n_coleg_arr_ls = Toolkit.splitString_m(licenseNumber, new int[]{2,4});
			
			HtmlInput n_coleg_1_in = htmlPage.querySelector("#ncol_0");
			HtmlInput n_coleg_2_in = htmlPage.querySelector("#ncol_1");
			HtmlInput n_coleg_3_in = htmlPage.querySelector("#ncol_2");
			HtmlInput cias_in = htmlPage.querySelector("#cias");
			HtmlInput startDate_dd_in = htmlPage.querySelector("#fechaBaja_dd");			
			HtmlInput startDate_mm_in = htmlPage.querySelector("#fechaBaja_mm");			
			HtmlInput startDate_aa_in = htmlPage.querySelector("#fechaBaja_aa");	
			
			n_coleg_1_in.setValueAttribute(n_coleg_arr_ls.get(0));
			n_coleg_2_in.setValueAttribute(n_coleg_arr_ls.get(1));
			n_coleg_3_in.setValueAttribute(n_coleg_arr_ls.get(2));
			cias_in.setValueAttribute(cias);
			startDate_dd_in.setValueAttribute(arr_startDate[0] + "");
			startDate_mm_in.setValueAttribute(arr_startDate[1] + "");
			startDate_aa_in.setValueAttribute(arr_startDate[2] + "");
			
			HtmlOption contract_type_opt = null;
			
			switch (contractType) {
				case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL: 	contract_type_opt = htmlPage.querySelector("#tipoContrato option:nth-child(2)");	break;
				case RESTO_Y_AUTONOMOS:						contract_type_opt = htmlPage.querySelector("#tipoContrato option:nth-child(3)");	break;
			}
			
			HtmlUnitToolkit.showAsXML(new HtmlElement[]{contract_type_opt});
		} 
	}
	
	
	//COMMON DATA FILLING
	private static HtmlPage fillCommonData(HtmlPage htmlPage,String regime, String ccc, String naf, Contingencies contingency,PartType type) throws IOException, InvalidDataException {
		HtmlInput regime_in = htmlPage.querySelector("#regimen");
		HtmlInput ccc_1_in = htmlPage.querySelector("#ccc1");
		HtmlInput ccc_2_in = htmlPage.querySelector("#ccc2");
		HtmlInput naf_1_in = htmlPage.querySelector("#naf1");
		HtmlInput naf_2_in = htmlPage.querySelector("#naf2");
		HtmlOption contingency_opt = null;
		HtmlSubmitInput accept = (HtmlSubmitInput) htmlPage.querySelector("#situacionTrabajador").getNextSibling().getNextSibling();
		
		switch(contingency) {
			case ENFERMEDAD_COMUN: 			contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(2)");	break;
			case ACCIDENTE_NO_LABORAL: 		contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(3)");	break;
			case ACCIDENT_LABORAL: 			contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(4)"); 	break;
			case ENFERMEDAD_PROFESIONAL: 	contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(5)"); 	break;
			case PERIODOS_OBSERVACIÓN: 		contingency_opt = htmlPage.querySelector("#contingencia option:nth-child(6)"); 	break;
		}
		
		String[] ccc_arr =  Toolkit.SplitString(ccc, 2);
		String[] naf_arr =  Toolkit.SplitString(naf, 2);
		
		regime_in.setValueAttribute(regime);
		ccc_1_in.setValueAttribute(ccc_arr[0]);
		ccc_2_in.setValueAttribute(ccc_arr[1]);
		
		naf_1_in.setValueAttribute(naf_arr[0]);
		naf_2_in.setValueAttribute(naf_arr[1]);
		
		htmlPage = accept.click();		
		return htmlPage;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
