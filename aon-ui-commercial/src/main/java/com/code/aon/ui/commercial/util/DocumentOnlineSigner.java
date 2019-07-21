package com.code.aon.ui.commercial.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

public class DocumentOnlineSigner implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentOnlineSigner.class.getName());
	
	static final String URL_ECERTIA = "https://app.ecertia.com/api/json/reply/";
	
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
	}
	
	
	public void sendData(Offer offer, boolean includeOffer, boolean includeOfferAttach) throws IOException, DocumentException, ReportException, ManagerBeanException {
		
		List<byte[]> list = new LinkedList<>();
		if(includeOffer){
			list.add(getOfferFile(offer).getData());
		}
		if(includeOfferAttach){
			for( AonFile aonFile : getOfferAttachemnts(offer) ) {
				list.add(aonFile.getData());
			}
		}
		
		byte[] data = mergePdf(list);
		
//		try {
//		    File file = new File("/tmp/test.pdf"); 
//            OutputStream os = new FileOutputStream(file); 
//            os.write(data); 
//            System.out.println("Successfully byte inserted in " + file.getAbsolutePath());
//            os.close(); 
//        } catch (Exception e) { 
//            System.out.println("Exception: " + e); 
//        } 
		
//		AonFile aonFile = new AonFile();
//		aonFile.setData(mergePdf(list));
//		aonFile.setFileName( "presupuesto_"+offer.getReferenceCode()+".pdf" );
//		aonFile.setMimeType(MimeType.MIME_PDF);
//		messageController.addAttachment(aonFile);
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
	
	
}