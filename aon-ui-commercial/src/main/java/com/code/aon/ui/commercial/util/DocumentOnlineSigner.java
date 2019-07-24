package com.code.aon.ui.commercial.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
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
import com.code.aon.ql.Criteria;
import com.code.aon.report.ReportException;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
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
	
	static final String URL_ECERTIA = "https://app.ecertia.com/api/json/reply/EviSignSubmit";
//	static final String URL_EVICERTIA = "https://app.evicertia.com/api/json/reply/EviSignSubmit";
	
	private String username;
	private String password;
	private String url;
	
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
	
	public void loadTestUrl() {
		setUrl(URL_ECERTIA);
//		setUrl(URL_EVICERTIA);
	}
	
	
	public void sendData(Offer offer, boolean includeOffer, boolean includeOfferAttach) throws Exception {
		
		List<byte[]> list = new LinkedList<>();
		if(includeOffer){
			list.add(getOfferFile(offer).getData());
		}
		if(includeOfferAttach){
			for( AonFile aonFile : getOfferAttachemnts(offer) ) {
				list.add(aonFile.getData());
			}
		}
		RegistryMedia rmedia = AON.getRMedia(AonUtil.getDomainName(), offer.getDomain(), "", f -> 
				f.getDomainProperty().eq(offer.getDomain())
				.and(f.getRegistryProperty().eq(offer.getTarget().getId()))
				.and(f.getMediaProperty().eq((byte) 4))
				.and(f.getCommercialProperty().eq((byte) 1)));
		String email = rmedia.getValue();
		if(email == null || "".equals(email)) {
			throw new Exception("El Cliente Potencial no tiene cuenta de correo electrónico comercial.");
		}
	
		byte[] data = mergePdf(list);
		byte[] encoded = Base64.getEncoder().encode(data);
	
		JSONObject json = new JSONObject();
		json.put("lookupKey", "Evisign");
		json.put("subject", "FIRMA");
		json.put("document", new String(encoded));
		JSONObject sp = new JSONObject();
		sp.put("name", offer.getTarget().getRegistry().getName());
		sp.put("address", email);
		sp.put("signingMethod", "EmailPin");
		json.put("signingParties", sp);
		json.put("options", new JSONObject());
		
		JSONObject responseJson = postObject(json.toString());
		if(responseJson.opt("uniqueId") != null) {
			com.esferalia.aon.occam.api.model.management.Offer of = AON.getOffer(AonUtil.getDomainName(), offer.getDomain(), "", f -> f.getIdProperty().eq(offer.getId()));
			of.setExternalReference( responseJson.getString("uniqueId"));
//			of.setExternalReference( responseJson.getString("uniqueId"));
			AON.updateOffer(AonUtil.getDomainName(), of.getDomain(), "", of);
		}
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}