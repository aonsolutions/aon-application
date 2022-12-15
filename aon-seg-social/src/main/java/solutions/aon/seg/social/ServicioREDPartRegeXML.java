package solutions.aon.seg.social;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.toolkit.Toolkit;

public abstract class ServicioREDPartRegeXML extends ServicioREDRegeXML {
	
	private static void newInstanceXml(String xml, DefaultHandler handler) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
	}
	
	public static Map<Integer, ITPart> getInfoPartsByXml(String xml) throws ParserConfigurationException, SAXException, IOException {
		Map<Integer, ITPart> map = new HashMap<>();
		
		DefaultHandler handler = new DefaultHandler() {
			private StringBuilder data;
			ITPart part = new ITPart();
			boolean bajaLogicaDesc;
			int position = 0;
			boolean bNss;
			boolean bTypePart;
			boolean bWorkLeaveDate;
			boolean bReceptionDate;	
			boolean bWorkRestartDate;
			boolean bConfirmationDate;
			boolean bPartNum;
			boolean bCcc;
			
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("DatosParte")) {
					part = new ITPart();
				}  else if (qName.equalsIgnoreCase("naf")) {
					bNss = true;
				}  else if (qName.equalsIgnoreCase("cccEmpresa")) {
					bCcc = true;
				} else if (qName.equalsIgnoreCase("fechaBaja")) {
					bWorkLeaveDate = true;
				} else if (qName.equalsIgnoreCase("fechaRecepcion")) {
					bReceptionDate = true;
				}  else if (qName.equalsIgnoreCase("fechaAlta")) {
					bWorkRestartDate = true;
				} else if (qName.equalsIgnoreCase("tipoParteDesc")) {
					bTypePart = true;
				} else if (qName.equalsIgnoreCase("bajaLogicaDesc")) {
					bajaLogicaDesc = true;
				} else if (qName.equalsIgnoreCase("fechaParteConf")) { // descripcionCausaAlta
					bConfirmationDate = true;
				} else if (qName.equalsIgnoreCase("numParteConf")) { // descripcionCausaAlta
					bPartNum = true;
				}
		
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (bajaLogicaDesc) {
					boolean isAnulado = data.indexOf("S")>-1;
					if(!isAnulado) {
						map.put(position, part);
					}
					bajaLogicaDesc = false;
					position++;
				} else if (bNss) {
					part.setNaf(data.toString());
					bNss = false;
				} else if (bCcc) {
					part.setCcc(data.toString());
					bCcc = false;
				}else if (bTypePart) {
					part.setPartType(data.toString());
					bTypePart = false;
				}  else if (bWorkLeaveDate) {
					part.setWorkLeaveDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkLeaveDate = false;
				} else if (bReceptionDate) {
					part.setReceptionDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bReceptionDate = false;
				}  else if (bWorkRestartDate) {
					part.setWorkRestartDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkRestartDate = false;
				}  else if (bConfirmationDate) {
					part.setConfirmationDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bConfirmationDate = false;
				} else if (bPartNum) {
					part.setPartNum(Integer.parseInt(data.toString()));
					bPartNum = false;
				} 
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		
		newInstanceXml(xml, handler);
        	
        return map;
	}
	
	public static void setInfoPart(String xml, ITPart part) throws ParserConfigurationException, SAXException, IOException {
		DefaultHandler handler = new DefaultHandler() {
			StringBuilder data;
			boolean dataEmployee;
			boolean bIpf;
			boolean bNss;
			boolean bEmployeeName;
			boolean bEmployeeDirection;
			boolean bTypePart;
			boolean bContingency;
			boolean bWorkLeaveDate;
			boolean bReceptionDate;
			boolean bWorkRestartDate;
			boolean bCauseRestart;
			boolean bCcc;
			
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equalsIgnoreCase("DatosTrabajador")) {
					dataEmployee = true;
				} else if (qName.equalsIgnoreCase("certificado_entidad")) {
					dataEmployee = false;
				} else if (dataEmployee) {
					if(qName.equalsIgnoreCase("ipf")) {						
						bIpf = true;
					} else if(qName.equalsIgnoreCase("naf")) {						
						bNss = true;
					} else if(qName.equalsIgnoreCase("nombre")) {						
						bEmployeeName = true;
					} else if(qName.equalsIgnoreCase("direccion")) {						
						bEmployeeDirection = true;
					} else if (qName.equalsIgnoreCase("cccEmpresa")) {
						bCcc = true;
					} 
				} else if (qName.equalsIgnoreCase("tipoParteDesc")) {
					bTypePart = true;
				} else if (qName.equalsIgnoreCase("contingencia")) { // contingenciaDesc
					bContingency = true;
				} else if (qName.equalsIgnoreCase("fechaBaja")) {
					bWorkLeaveDate = true;
				} else if (qName.equalsIgnoreCase("fechaRecepcion")) {
					bReceptionDate = true;
				}  else if (qName.equalsIgnoreCase("fechaAlta")) {
					bWorkRestartDate = true;
				} else if (qName.equalsIgnoreCase("causaAlta")) { // descripcionCausaAlta
					bCauseRestart = true;
				} 
				data = new StringBuilder();
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (bIpf) {
					part.setIpf(data.toString());
					bIpf = false;
				} else if (bNss) {
					part.setNaf(data.toString());
					bNss = false;
				} else if (bCcc) {
					part.setCcc(data.toString());
					bCcc = false;
				} else if (bEmployeeName) {
					part.setNameEmployee(data.toString());
					bEmployeeName = false;
				} else if (bEmployeeDirection) {
					part.setDirectionEmployee(data.toString());
					bEmployeeDirection = false;
				} else if (bTypePart) {
					part.setPartType(data.toString());
					bTypePart = false;
				}  else if (bContingency) {
					part.setContingency(data.toString());
					bContingency = false;
				} else if (bWorkLeaveDate) {
					part.setWorkLeaveDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkLeaveDate = false;
				} else if (bReceptionDate) {
					part.setReceptionDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bReceptionDate = false;
				}  else if (bWorkRestartDate) {
					part.setWorkRestartDate(Toolkit.parseDate(data.toString(), DATE_FORMAT));
					bWorkRestartDate = false;
				} else if (bCauseRestart) {
					part.setCauseRestart(data.toString());
					bCauseRestart = false;
				}
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				data.append(new String(ch, start, length));
			}
		};
		
		newInstanceXml(xml, handler);
	}
	
}
