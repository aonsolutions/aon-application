package solutions.aon.seg.social;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
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
		
		try { return getFullItPartsImpl(certificateInputStream, certificatePassword,	certificateType, regime, ccc, from, to); } 
		catch (FailingHttpStatusCodeException e) { 
			switch (e.getStatusCode()) {
				case 403: throw new ForbiddenException();
				default: throw new SegSocialException(e); 
			}
		}
		catch (MalformedURLException e) { throw new SegSocialException(e); }
		catch (IOException e) { throw new SegSocialException(e); } 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		
	}

	//GET ALL THE ITPARTS
	private static Collection<ITPart> getFullItPartsImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, InvalidCertificateException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			
			
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
	
	public static void main(String[] args) {
		try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			ArrayList<It> its = (ArrayList<It>) getIts(
					certificateInputStream,"jg@FNMT", "pkcs12", "0111", "01105360062", 
					Toolkit.parseDate("1/1/2015", "dd/MM/yyyy"), Toolkit.parseDate("1/1/2020", "dd/MM/yyyy")			
			);
			
			File f = new File("Log_Its.ak");
			FileWriter fw = new FileWriter(f);
			for(It it : its) fw.write(it.toString());
			fw.close();
				
		} 
		catch (SegSocialException e) {e.printStackTrace();} 
		catch (FileNotFoundException e1) {e1.printStackTrace();} 
		catch (IOException e1) {e1.printStackTrace();}
	}
	
	
}
