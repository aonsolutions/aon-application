package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.tools.Tool;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLAnchorElement;

import solutions.aon.seg.social.ITPart.ITPartBuilder;
import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.SegSocialException;

public class SistemaREDITParts {

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
			String certificateType, String regime, String ccc, Date from, Date to) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			
			
			ArrayList<ITPart> itParts = new ArrayList<ITPart>();
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

			HtmlAnchor next = htmlPage.getAnchorByText("(2) Siguiente >>");
			ITPartBuilder builder = new ITPartBuilder();
			String format = "dd/MM/yyyy";
			
			
			while(next != null) {
				
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
					itParts.add(part);
				}
				
				try {
					htmlPage = next.click();	
					next = htmlPage.getAnchorByText("(2) Siguiente >>");
					
				}
				catch (ElementNotFoundException e) {next = null;}
			}
			
			
			return itParts;
		}		
	}
	
	public static void main(String[] args) {
		try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			ArrayList<ITPart> parts = (ArrayList<ITPart>) getFullItParts(
					certificateInputStream,"jg@FNMT", "pkcs12", "0111", "01105360062", 
					Toolkit.parseDate("1/1/2015", "dd/MM/yyyy"), Toolkit.parseDate("1/1/2020", "dd/MM/yyyy")			
			);
			
			Toolkit.log(parts.toArray());
		} 
		catch (SegSocialException e) {e.printStackTrace();} 
		catch (FileNotFoundException e1) {e1.printStackTrace();} 
		catch (IOException e1) {e1.printStackTrace();}
	}
	
	
}
