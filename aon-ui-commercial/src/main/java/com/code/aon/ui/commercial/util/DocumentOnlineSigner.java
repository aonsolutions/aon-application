package com.code.aon.ui.commercial.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.report.ReportException;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.finance.SddMandateObject;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

public class DocumentOnlineSigner implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentOnlineSigner.class.getName());
	
	static final String REPORT_TEMPLATE_SDD_MANDATE = "sddMandate";

	
	static final String URL_ECERTIA = "https://app.ecertia.com/api/json/reply/EviSignSubmit";
	static final String URL_EVICERTIA = "https://app.evicertia.com/api/json/reply/EviSignSubmit";
	
	private String username;
	private String password;
	private Integer signingType;
	private String url;
	private boolean collapsed;
	private boolean testing;
	private boolean notifyCommercial;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isCollapsed() {
		return collapsed;
	}
	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}
	public boolean isTesting() {
		return testing;
	}
	public void setTesting(boolean testing) {
		this.testing = testing;
	}
	public boolean isNotifyCommercial() {
		return notifyCommercial;
	}
	public void setNotifyCommercial(boolean notifyCommercial) {
		this.notifyCommercial = notifyCommercial;
	}
	public Integer getSigningType() {
		return signingType;
	}
	public void setSigningType(Integer signingType) {
		this.signingType = signingType;
	}
	
	private String signingType() {
		if(getSigningType()==0) {
			return "Challenge";
		} else if(getSigningType()==1) {
			return "EmailPin";
		} else if(getSigningType()==2) {
			return "Handwriting";
		} else if(getSigningType()==3) {
			return "MobilePin";
		} else if(getSigningType()==4) {
			return "Password";
		} else if(getSigningType()==5) {
			return "WebClick";
		}
		return null;
	}
	
	public void onCollapse(ActionEvent event) {
		setCollapsed(false);
	}
	public void onUncollapse(ActionEvent event) {
		setCollapsed(true);
	}
	
	public void onUpdateUrl(ActionEvent event) {
		setUrl(testing?URL_ECERTIA:URL_EVICERTIA);
	}
	
	public void init() {
		// Test Url by default
		setTesting(true);
		setUrl(URL_ECERTIA);
		setNotifyCommercial(true);
		loadParams();
	}
	
	private void loadParams() {
		ApplicationParameter ap_username = AppParamUtil.getParameter("DOCUMENT_ONLINE_SIGN_username");
		ApplicationParameter ap_password = AppParamUtil.getParameter("DOCUMENT_ONLINE_SIGN_password");
		ApplicationParameter ap_signingType = AppParamUtil.getParameter("DOCUMENT_ONLINE_SIGN_signingType");
		
		if(ap_username!=null && ap_username.getValue().trim().length()>0
				&& ap_password!=null && ap_password.getValue().trim().length()>0
				&& ap_signingType!=null && ap_signingType.getValue().trim().length()>0
				&& NumberUtils.isNumber(ap_signingType.getValue().trim())) {
			setUsername(ap_username.getValue().trim());
			setPassword(ap_password.getValue().trim());
			setSigningType(Integer.parseInt(ap_signingType.getValue().trim()));
			setCollapsed(true);
		} else {
			setUsername(null);
			setPassword(null);
			setSigningType(1);
			setCollapsed(false);
		}
	}
	
	private void saveParams() {
		AppParamUtil.insertParameter("DOCUMENT_ONLINE_SIGN_username", getUsername());
		AppParamUtil.insertParameter("DOCUMENT_ONLINE_SIGN_password", getPassword());
		AppParamUtil.insertParameter("DOCUMENT_ONLINE_SIGN_signingType", String.valueOf(getSigningType()));
	}
		
	public void sendData(Offer offer, boolean includeOffer, boolean includeOfferAttach, boolean includeSddMandate) throws Exception {
		if(StringUtils.isBlank(getUsername()) || StringUtils.isBlank(getPassword())) {
			throw new AbortProcessingException("Las credenciales son necesarias.");
		} else {
			saveParams();
			
			List<byte[]> list = new LinkedList<>();
			if(includeOffer){
				list.add(getOfferFile(offer).getData());
			}
			if(includeOfferAttach){
				for( AonFile aonFile : getOfferAttachemnts(offer) ) {
					list.add(aonFile.getData());
				}
			}
			if(includeSddMandate){
				list.add(getSddMandate(offer).getData());
			}
			
			String targetCommercialEmail = getTargetCommercialEmail(offer);
			if(targetCommercialEmail == null || "".equals(targetCommercialEmail)) {
				throw new Exception("El Cliente Potencial no tiene cuenta de correo electrónico comercial.");
			}
			String rDirStaffEmail = getRDirStaffEmail(offer);
			String rDirStaffName = getRDirStaffName(offer);
			String sellerEmail = getSellerEmail(offer);
			
			byte[] data = mergePdf(list);
			byte[] encoded = Base64.getEncoder().encode(data);
			
			JSONObject json = new JSONObject();
			json.put("lookupKey", "Evisign");
			json.put("subject", "FIRMA");
			json.put("document", new String(encoded));
			
			// signingParties
			JSONArray sp = new JSONArray();
			json.put("signingParties", sp);
			
			JSONObject spDirStaff = new JSONObject();
			spDirStaff.put("name", rDirStaffName);
			spDirStaff.put("address", rDirStaffEmail);
			spDirStaff.put("signingMethod", signingType());
			spDirStaff.put("signingOrder", 2);
			spDirStaff.put("role", "Signer");
			sp.put(spDirStaff);
			if(!rDirStaffEmail.equals(targetCommercialEmail)) {
				JSONObject spTargetCommercial = new JSONObject();
				spTargetCommercial.put("name", offer.getTarget().getRegistry().getName());
				spTargetCommercial.put("address", targetCommercialEmail);
				spTargetCommercial.put("signingMethod", "WebClick");
				spDirStaff.put("signingOrder", 1);
				spTargetCommercial.put("role", "Reviewer");
				sp.put(spTargetCommercial);
			}
			// interestedParties
			if(sellerEmail != null && !sellerEmail.equals("")) {
				JSONArray ips = null;
				try {
					ips = json.getJSONArray("interestedParties");
				} catch (JSONException e) {
					ips = new JSONArray();
				}
				if(ips==null) ips = new JSONArray();
				JSONObject ip = new JSONObject();
				ip.put("address", sellerEmail);
				ips.put(ip);
				json.put("interestedParties", ips);
			}
			
			// options
			JSONArray filter = new JSONArray();
			filter.put("EviSignSigned");
			filter.put("EviSignRejected");
			
			JSONObject opt = new JSONObject();
			String pnUrl = AonUtil.getServerName();
			pnUrl = pnUrl + (pnUrl.endsWith("/")?"":"/") + "offer";
			pnUrl = (pnUrl.startsWith("http://")?"":"http://") + pnUrl;
			opt.put("pushNotificationUrl", pnUrl);
			opt.put("pushNotificationFilter", filter);
			json.put("options", opt);
			
			JSONObject responseJson = postObject(json.toString());
			if(responseJson.opt("uniqueId") != null) {
				com.esferalia.aon.occam.api.model.management.Offer of = AON.getOffer(AonUtil.getDomainName(), offer.getDomain(), "", f -> f.getIdProperty().eq(offer.getId()));
				of.setExternalReference( responseJson.getString("uniqueId"));
				AON.updateOffer(AonUtil.getDomainName(), of.getDomain(), "", of);
				AonUtil.addInfoMessage("Response ID: "+responseJson.getString("uniqueId"));
			} else {
				AonUtil.addInfoMessage("No response obtained...");
			}
		}
	}
	
	private String getTargetCommercialEmail(Offer offer) {
		RegistryMedia rmedia = AON.getRMedia(AonUtil.getDomainName(), offer.getDomain(), "", f -> 
		f.getDomainProperty().eq(offer.getDomain())
		.and(f.getRegistryProperty().eq(offer.getTarget().getId()))
		.and(f.getMediaProperty().eq((byte) 4))
		.and(f.getCommercialProperty().eq((byte) 1)));
		return rmedia==null?"":rmedia.getValue();
	}
	private String getRDirStaffEmail(Offer offer) throws ManagerBeanException {
		IManagerBean dirStaff = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dirStaff.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), offer.getTarget().getId());
		List<ITransferObject> list = dirStaff.getList(criteria);
		if (! list.isEmpty() )
			return ((RegistryDirStaff)list.get(0)).getChargeDescription();
		return "";
	}
	private String getRDirStaffName(Offer offer) throws ManagerBeanException {
		IManagerBean dirStaff = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dirStaff.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), offer.getTarget().getId());
		List<ITransferObject> list = dirStaff.getList(criteria);
		if (! list.isEmpty() )
			return ((RegistryDirStaff)list.get(0)).getName();
		return "";
	}
	private String getSellerEmail(Offer offer) {
		RegistryMedia rmedia = AON.getRMedia(AonUtil.getDomainName(), offer.getDomain(), "", f -> 
			f.getDomainProperty().eq(offer.getDomain())
				.and(f.getRegistryProperty().eq(offer.getSeller().getId()))
				.and(f.getMediaProperty().eq((byte) 4)));
		return rmedia==null?"":rmedia.getValue();
	}
	
	public AonFile getOfferFile( Offer offer ) throws IOException, ReportException, ManagerBeanException {
		SignerController signer = (SignerController) AonUtil.getRegisteredBean(ICommercialConstants.OFFER_SIGNER_CONTROLLER_NAME);
		File file = File.createTempFile( signer.getReportKey(), ".pdf" );
		IAttachment attach = null;
		if ( offer.isSigned() ) {
			attach = signer.getSignedAttachment(offer.getId());
		} else {
			attach = signer.getReport(offer);
		}
		FileUtils.writeByteArrayToFile(file, attach.getData());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( attach.getDescription() + ".pdf" );
		aonFile.setMimeType(MimeType.MIME_PDF);
		return aonFile;
	}
	
	public List<AonFile> getOfferAttachemnts( Offer offer ) throws ManagerBeanException, IOException {
		IManagerBean offerAttach = BeanManager.getManagerBean(OfferAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerAttach.getFieldName(IEntityAlias.OFFER_ATTACHMENT_OFFER_ID), offer.getId());
		criteria.addNotEqualExpression(offerAttach.getFieldName(IEntityAlias.OFFER_ATTACHMENT_MIME_TYPE), MimeType.MIME_SIGNED_PDF);
		List<ITransferObject> list = offerAttach.getList(criteria);
		if (! list.isEmpty() ) {
			List<AonFile> files = new LinkedList<AonFile>();
			for( ITransferObject to : list ) {
				OfferAttachment attach = (OfferAttachment) to;
				AonFile aonFile = new AonFile();
				String ext = "." + ( (attach.getMimeType() != null) ? attach.getMimeType().getExtension() : "tmp");
				File file = File.createTempFile( attach.getDescription(), ext );
				FileUtils.writeByteArrayToFile(file, attach.getData());
				aonFile.setFile(file);
				aonFile.setFileName( attach.getDescription() );
				aonFile.setMimeType( attach.getMimeType() );
				files.add( aonFile );
			}
			return files;
		}
		return Collections.emptyList();
	}
	
	public AonFile getSddMandate(Offer offer) throws IOException, ReportException {
		SddMandateObject sddMandateObject = new SddMandateObject();
		sddMandateObject.setSignDate(offer.getDate());
		sddMandateObject.setRegistry(offer.getTarget().getRegistry());
		sddMandateObject.setReference("PPTO. "+offer.getReferenceCode());
		
		
		AonFile aonFile = new AonFile();
		File file = File.createTempFile( "ssdMandate-temp", "." + MimeType.MIME_PDF.getExtension() );
		aonFile.setFile( file );
		aonFile.setFileName( "Domiciliacion-Bancaria-SEPA" + "." + MimeType.MIME_PDF.getExtension() );
		aonFile.setMimeType(MimeType.MIME_PDF);

		ReportManager reportManager = new ReportManager();
		reportManager.setCollectionProvider( sddMandateObject );
		OutputStream out = new BufferedOutputStream( new FileOutputStream(file) );
		reportManager.execute( out, REPORT_TEMPLATE_SDD_MANDATE );
		out.close();
		return aonFile;
	}
	
	public static byte[] mergePdf(List<byte[]> attachList) throws IOException, DocumentException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		List<PdfReader> pdfReaderList = new ArrayList<PdfReader>();
		for(byte[] data: attachList){
			pdfReaderList.add(new PdfReader(data));
		}

		PdfCopyFields copy = new PdfCopyFields(outputStream);
		copy.open();

		if (null != pdfReaderList && !pdfReaderList.isEmpty()) {
			Iterator<PdfReader> iter = pdfReaderList.iterator();
			while (iter.hasNext()) {
				String pageNOs = "";
				PdfReader pdfReader = (PdfReader) iter.next();
				int noOfPages = pdfReader.getNumberOfPages();
				if (noOfPages > 0) {
					pageNOs = getNumderOfPages(noOfPages);
				}
				copy.addDocument(pdfReader, pageNOs);
			}
		}
		copy.close();
		return outputStream.toByteArray();
	}
	
	/**
	 * Function to get page numbers in string with comma separated
	 * 
	 * @param noOfPages
	 * @return
	 */
	private static String getNumderOfPages(int noOfPages) {
		String pageNOs = "";
		boolean flag = false;
		for (int i = 0; i < noOfPages; i++) {

			if (flag == true) {
				Integer c = (Integer) i;
				pageNOs = pageNOs.concat("," + c.toString());
			}
			if (flag == false) {
				Integer c = (Integer) i;
				pageNOs = c.toString();
				flag = true;
			}
		}
		return pageNOs;
	}

	protected JSONObject postObject(String requestData) {
		String response = post(requestData);
		return response != null ? new JSONObject(response) : new JSONObject();
	}
	
	protected String post(String requestData) {
		try {
			URL url = new URL(getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			String encoding = new String(Base64.getEncoder().encode((getUsername() + ":" + getPassword()).getBytes())); 
			conn.setRequestProperty("Authorization", "Basic " + encoding);
			
			OutputStream os = conn.getOutputStream();
			os.write(requestData.getBytes());
			os.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			
			String output;	
			String response = "";
			while ((output = br.readLine()) != null) {
				response = output;	
			}
			conn.disconnect();
			return response;
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
}