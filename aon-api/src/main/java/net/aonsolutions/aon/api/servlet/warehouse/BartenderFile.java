package net.aonsolutions.aon.api.servlet.warehouse;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class BartenderFile {

	private BartenderFile() {
		
	}
	
	public static File generate(JSONObject json) {
		try {
			File file = File.createTempFile("Etiqueta", "." + MimeType.XML.getExtension());
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// Elemento raíz

			Document doc = docBuilder.newDocument();
			Element xmlScript = doc.createElement("XMLScript");
			
			Attr version = doc.createAttribute("Version");
			version.setValue("2.0");
			xmlScript.setAttributeNode(version);
			
			Attr trusted = doc.createAttribute("Trusted");
			trusted.setValue("True");
			xmlScript.setAttributeNode(trusted);
			
			doc.appendChild(xmlScript);
			
			Element command = doc.createElement("Command");
			Attr commandName = doc.createAttribute("Name");
			commandName.setValue("Print Document");
			command.setAttributeNode(commandName);
			xmlScript.appendChild(command);
		    
			Element print = doc.createElement("Print");
			command.appendChild(print);
			// Primer elemento
			Element printerSetup = doc.createElement("PrinterSetup");
			print.appendChild(printerSetup);
			
			Element printer = doc.createElement("Printer");
			printer.setTextContent(JsonUtils.getString(json, IJsonNames.PRINTER));
			printerSetup.appendChild(printer);
			
			Element copies = doc.createElement("IdenticalCopiesOfLabel");
			copies.setTextContent("1");
			printerSetup.appendChild(copies);
			

			Element format = doc.createElement("Format");
			format.setTextContent(JsonUtils.getString(json, IJsonNames.TAG));			
			print.appendChild(format);


			JSONObject object = JsonUtils.getJSONObject(json, IJsonNames.CONTENT);
			if (!object.isEmpty()) {
				for (String key : JSONObject.getNames(object)) {
					Element name = doc.createElement("NamedSubString");
					Attr attr = doc.createAttribute("Name");
					attr.setValue(key);
					name.setAttributeNode(attr);
					print.appendChild(name);

					Element value = doc.createElement("Value");
					value.setTextContent(JsonUtils.getString(object, key));
					name.appendChild(value);

				}
			}

			// Se escribe el contenido del XML en un archivo
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
			return file;
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;
	}

}
