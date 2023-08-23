package net.aonsolutions.aon.tbai.sign;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.tbai.CRC8;
import ticketbai.emision.TicketBai;


public class TbaiSign {

    public static String buildTbaiId(TicketBai tbai, String sign) throws UnsupportedEncodingException {
    	String dateStr = tbai.getFactura().getCabeceraFactura().getFechaExpedicionFactura();
    	Date date = AonDateUtils.parse(dateStr, "dd-MM-yyyy");
    	String tbaiId = "TBAI-" + tbai.getSujetos().getEmisor().getNIF() 
    		+ "-" + AonDateUtils.format(date, "ddMMyy")
    		+ "-" + sign.substring(0, 13) 
    		+ "-";
    	String crc = CRC8.calculate(tbaiId);
    	return tbaiId + crc;
    }
    
    public String buildTbaiId(String dateStr, String emisor, String sign) throws UnsupportedEncodingException {
    	Date date = AonDateUtils.parse(dateStr, "dd-MM-yyyy");
    	String tbaiId = "TBAI-" + emisor
    		+ "-" + AonDateUtils.format(date, "ddMMyy")
    		+ "-" + sign.substring(0, 13) 
    		+ "-";
    	String crc = CRC8.calculate(tbaiId);
    	return tbaiId + crc;
    }
    
    public static String getSign(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		Document doc = getDocument(data);
		return doc.getElementsByTagName("ds:SignatureValue").item(0).getTextContent();
    }
    
    public static Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = new ByteArrayInputStream(data);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}

}
