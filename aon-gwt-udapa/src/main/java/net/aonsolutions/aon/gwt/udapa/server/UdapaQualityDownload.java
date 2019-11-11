package net.aonsolutions.aon.gwt.udapa.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "udapaQualistyDownload", urlPatterns = {"/aon_gwt_aio/ms/download_udapa_quality/*"})
public class UdapaQualityDownload extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	       
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		String option = parameters.get("option");
		Boolean isList = "list".equals(option);

		File file = null;
		if(isList){
			HashMap<String, String[]> map = SecurityUtils.getInstance().getParametersMap(req.getPathInfo().substring(1));

			String type = parameters.get("type");
			if("excel".equals(type)){
		//		file = com.code.aon.webservice.udapa.printQualityList.createExcel(array);
			} else {
		//		file = com.code.aon.webservice.udapa.printQualityList.createPdf(array);
			}

		} else {
			String dataResponseIdStr = parameters.get("id");
			Integer dataResponseId = Integer.parseInt(dataResponseIdStr);
		
			UdapaImpl udp = new UdapaImpl();
			HashMap<String, String> map = udp.getValues(domain.getName(), domain.getId(), dataResponseId);
			DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.QUALITY, f -> f.getIdProperty().eq(dataResponseId));
			map.put("number", dr.getCode());
		
		
			// TODO AÑADIR DATOS K FALTAN!
			Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), login);
			RAddress raddress = AON.getRAddres(domain.getName(), domain.getId(), login, company.getId());
			map.put("registry_document", company.getDocument());
			map.put("registry_name", company.getName());
			map.put("registry_full_address", raddress.getFullAddress());
			map.put("registry_end_address", raddress.getZip() + " " + raddress.getCity() + " " + raddress.getGeozoneName());

			LinkedList<RegistryMedia> list = AON.getRMediaList(domain.getName(), domain.getId(), login, f -> f.getRegistryProperty().eq(company.getId()));
			String phone = list.stream().filter(a -> a.getMedia() == MediaType.FIXED_PHONE.value()).map(r -> r.getValue()).findFirst().orElse("-");
			String fax = list.stream().filter(a -> a.getMedia() == MediaType.FAX.value()).map(r -> r.getValue()).findFirst().orElse("-");
			String mail = list.stream().filter(a -> a.getMedia() == MediaType.EMAIL.value()).map(r -> r.getValue()).findFirst().orElse("-");
			String web = list.stream().filter(a -> a.getMedia() == MediaType.WEB.value()).map(r -> r.getValue()).findFirst().orElse("-");
		
			map.put("phone", phone);
			map.put("fax", fax);
			map.put("mail", mail);
			map.put("web", web);
			
			Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, 
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
					.and(f.getDomainProperty().eq(domain.getId())),
				AttachType.REGISTRY);
		
			LinkedList<byte[]> images = new LinkedList<>();
			AON.getAttachStream(domain.getName(), domain.getId(), login, 
					f-> f.getDomainProperty().eq(domain.getId())
					.and(f.getSourceTypeProperty().eq((byte)0))
					.and(f.getSourceBatchProperty().eq(dataResponseId)),
				AttachType.DATA, true).forEach(d -> images.add(d.getData()));
			file = printQuality.createPdf(map, attach.getData(), images);
		}
		resp.addHeader("Access-Control-Allow-Origin", "*");
	    resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
	    resp.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
	    resp.addHeader("Access-Control-Max-Age", "1728000");
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
	}
}
