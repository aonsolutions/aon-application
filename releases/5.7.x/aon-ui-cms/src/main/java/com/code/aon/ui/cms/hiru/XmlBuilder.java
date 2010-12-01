package com.code.aon.ui.cms.hiru;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.code.aon.cms.HiruCourse;
import com.code.aon.cms.HiruCourseDetail;
import com.code.aon.cms.HiruOrganizerCentre;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Languages;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class XmlBuilder {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(XmlBuilder.class);
	
	private File destDir;

	private String url;

	public XmlBuilder(File destDir, String url){
		this.destDir = destDir;
		this.url = url;
	}

	public void generate() throws FileNotFoundException, XmlBuilderException{
		this.fireMessage("Start generation.");
		if (!destDir.exists()){
			this.fireMessage("Creating directory....");
			destDir.mkdir();
			this.fireMessage("Created.");
		}
		
		File file = null;
		PrintWriter out = null;
		try {
			file = new File(destDir+"/centros.xml");
			out = new PrintWriter(file);
			this.fireMessage("Start centres xml.");
			buildOrganizerCentre(out);
			this.fireMessage("Centres xml finished.");
		} catch (Throwable th) {
			this.fireMessage(" ** ERROR ** Centres xml error: "+th.getMessage());
			throw new XmlBuilderException(th);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	@SuppressWarnings("unused")
	private void buildOrganizerCentre(Writer out) 
		throws FileNotFoundException, 
				TransformerConfigurationException, 
				SAXException, 
				ManagerBeanException,
				XmlBuilderException{
			StreamResult streamResult = new StreamResult(out);
			SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler hd = tf.newTransformerHandler();
			Transformer serializer = hd.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			hd.setResult(streamResult);
			hd.startDocument();
			AttributesImpl atts = new AttributesImpl();
			hd.startElement("","","zentroak",atts);
			
			IManagerBean beanHOC = BeanManager.getManagerBean(HiruOrganizerCentre.class);
			Criteria criteriaHOC = new Criteria();
			criteriaHOC.addEqualExpression(beanHOC.getFieldName(ICMSAlias.HIRU_ORGANIZER_CENTRE_ACTIVE), true);
			List<ITransferObject> listHOC = beanHOC.getList(criteriaHOC);
			for (Iterator<ITransferObject> iteratorHOC = listHOC.iterator(); iteratorHOC.hasNext();) {
				atts.clear();
				hd.startElement("","","zentroa",atts);
				
				HiruOrganizerCentre object = (HiruOrganizerCentre) iteratorHOC.next();
				this.fireMessage("--> Centre " + object.getName());
			
				String id = object.getId().toString();
				generateElement(hd,atts,"","","unique_id",id);
				String value = object.getName()==null?"":object.getName();
				generateElement(hd,atts,"","","izena",value);
				value = object.getTelephone()==null?"":object.getTelephone();
				generateElement(hd,atts,"","","telefonoa",value);
				value = object.getFax()==null?"":object.getFax();
				generateElement(hd,atts,"","","faxa",value);
				value = object.getPostal_code()==null?"":object.getPostal_code().toString();
				generateElement(hd,atts,"","","postae",value);
				value = object.getWeb()==null?"":object.getWeb();
				generateElement(hd,atts,"","","web_orria",value);
				value = object.getAddress()==null?"":object.getAddress();
				generateElement(hd,atts,"","","helbidea",value);
				value = object.getPostal_code()==null?"":""+object.getPostal_code();
				generateElement(hd,atts,"","","pk",value);
				value = object.getLocality()==null?"":object.getLocality();
				generateElement(hd,atts,"","","herria",value);
				value = this.url+"/curso_id_"+id+".xml";
				generateElement(hd,atts,"","","feed",value);

				hd.endElement("","","zentroa");
				
				File coursefile = null;
				PrintWriter courseout = null;
				try{
					coursefile= new File(destDir+"/curso_id_"+id+".xml");
					courseout = new PrintWriter(coursefile);
					this.fireMessage("Start courses xml.");
					buildCourse(object,courseout);
					this.fireMessage("Courses xml finished.");
				} catch (Throwable th) {
					this.fireMessage(" ** ERROR ** Courses xml error: "+th.getMessage());
					throw new XmlBuilderException(th);
				} finally {
					courseout.close();
				}

			}
			hd.endElement("","","zentroak");
			hd.endDocument();
			listHOC = null;
	}

	@SuppressWarnings("unused")
	private void buildCourse(HiruOrganizerCentre hiruOrganizerCentre,Writer out) 
		throws FileNotFoundException, 
				TransformerConfigurationException, 
				SAXException, 
				ManagerBeanException{
		StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		TransformerHandler hd = tf.newTransformerHandler();
		Transformer serializer = hd.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		serializer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS,"izenburua_es izenburua_eu url_es url_eu hasi bukatu info_es info_eu");

		hd.setResult(streamResult);
		hd.startDocument();
		AttributesImpl atts = new AttributesImpl();
		hd.startElement("","","kurtsoak",atts);
		
		IManagerBean beanHCI18n = BeanManager.getManagerBean(HiruCourseDetail.class);
		Criteria criteriaHCI18n;
		List<ITransferObject> listHCI18n;
		HiruCourse object;
		HiruCourseDetail objectDetailEu = null;
		HiruCourseDetail objectDetailEs = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		IManagerBean beanHC = BeanManager.getManagerBean(HiruCourse.class);
		Criteria criteriaHC = new Criteria();
		criteriaHC.addEqualExpression(beanHC.getFieldName(ICMSAlias.HIRU_COURSE_ACTIVE), true);
		criteriaHC.addEqualExpression(beanHC.getFieldName(ICMSAlias.HIRU_COURSE_HIRU_ORGANIZER_CENTRE_ID), hiruOrganizerCentre.getId());
		List<ITransferObject> listHC = beanHC.getList(criteriaHC);
		for (Iterator<ITransferObject> iteratorHC = listHC.iterator(); iteratorHC.hasNext();) {
			object = (HiruCourse) iteratorHC.next();

			this.fireMessage("--> Course " + object.getAlias());

			criteriaHCI18n = new Criteria();
			criteriaHCI18n.addEqualExpression(beanHCI18n.getFieldName(ICMSAlias.HIRU_COURSE_DETAIL_HIRU_COURSE_ID), object.getId());
			criteriaHCI18n.addEqualExpression(beanHCI18n.getFieldName(ICMSAlias.HIRU_COURSE_DETAIL_LANGUAGE_ID),Languages.BASQUE.ordinal());
			listHCI18n = beanHCI18n.getList(criteriaHCI18n);

			if (!listHCI18n.isEmpty()){
				objectDetailEu = (HiruCourseDetail) listHCI18n.get(0);
			}else{
				this.fireMessage("--> ** ERROR ** Course not EU language");
			}

			criteriaHCI18n = new Criteria();
			criteriaHCI18n.addEqualExpression(beanHCI18n.getFieldName(ICMSAlias.HIRU_COURSE_DETAIL_HIRU_COURSE_ID), object.getId());
			criteriaHCI18n.addEqualExpression(beanHCI18n.getFieldName(ICMSAlias.HIRU_COURSE_DETAIL_LANGUAGE_ID),Languages.SPANISH.ordinal());
			listHCI18n = beanHCI18n.getList(criteriaHCI18n);

			if (!listHCI18n.isEmpty()){
				objectDetailEs = (HiruCourseDetail) listHCI18n.get(0);
			}else{
				this.fireMessage("--> ** ERROR ** Course not ES language");
			}

			if (objectDetailEu!=null && objectDetailEs!=null){
				atts.clear();
				hd.startElement("","","kurtsoa",atts);
				String value = object.getId().toString();
				generateElement(hd,atts,"","","unique_id",value);
				value = objectDetailEs.getName()==null?"":objectDetailEs.getName();
				generateElement(hd,atts,"","","izenburua_es",value);
				value = objectDetailEu.getName()==null?"":objectDetailEu.getName();
				generateElement(hd,atts,"","","izenburua_eu",value);
				value = objectDetailEs.getUrl()==null?"":objectDetailEs.getUrl();
				generateElement(hd,atts,"","","url_es",value);
				value = objectDetailEu.getUrl()==null?"":objectDetailEu.getUrl();
				generateElement(hd,atts,"","","url_eu",value);
				value = object.getInitDate()==null?"":sdf.format(object.getInitDate());
				generateElement(hd,atts,"","","hasi",value);
				value = object.getEndDate()==null?"":sdf.format(object.getEndDate());
				generateElement(hd,atts,"","","bukatu",value);
				value = objectDetailEs.getInfo()==null?"":objectDetailEs.getInfo();
				generateElement(hd,atts,"","","info_es",value);
				value = objectDetailEu.getInfo()==null?"":objectDetailEu.getInfo();
				generateElement(hd,atts,"","","info_eu",value);
				hd.startElement("","","gaiak",atts);
				hd.startElement("","","gaia",atts);
				value = String.valueOf(object.getSubject().ordinal());
				generateElement(hd,atts,"","","gaia_id",value);
				value = String.valueOf(object.getSubject().getName(Languages.BASQUE.getLocale()));
				generateElement(hd,atts,"","","gaia_izena",value);
				hd.endElement("","","gaia");
				hd.endElement("","","gaiak");
				hd.endElement("","","kurtsoa");
			}
		}
		hd.endElement("","","kurtsoak");
		hd.endDocument();
		listHC = null;
	}

	private void generateElement(TransformerHandler hd,
			AttributesImpl atts,
			String uri,
			String localName,
			String qName,
			String value) throws SAXException{
		hd.startElement(uri,localName,qName,atts);
		//hd.startCDATA();
		hd.characters(value.toCharArray(),0,value.length());
		//hd.endCDATA();
		hd.endElement(uri,localName,qName);
	}
	
	private List<XmlBuilderListener> listeners = new ArrayList<XmlBuilderListener>();
	
	public void addXmlBuilderListener(XmlBuilderListener listener) {
		listeners.add(listener);
	}
	
	public void removeXmlBuilderListener(XmlBuilderListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireMessage(String message) {
		for (Iterator<XmlBuilderListener> iterator = listeners.iterator(); iterator.hasNext();) {
			XmlBuilderListener listener = iterator.next();
			listener.addMessage(message);
		}
	}

	public static void main(String[] args) {
		try {
			// ControllerUtil.getDocumentsPath()
			XmlBuilder b = new XmlBuilder( new File("c:/tmp"+"/"+"hiru"),"http://hiru.com/xml");
			b.generate();			
		}catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
		}
	}
	
}
