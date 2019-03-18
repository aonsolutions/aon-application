package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod123Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod123 mod123);
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod123 mod123) throws IOException;
	}
	protected interface IMod123Writer {
		public void fillWriter(Mod123 mod123, Writer wr) throws IOException;	
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod123Writer getInstance();
	}

	
	private enum Writers {
		 AEAT_2019		(mod123 -> (mod123.isAEAT() && mod123.getYear() >= 2019) 	, Mod123WriterAEAT2019::new)
		,AEAT_2016		(mod123 -> (mod123.isAEAT() && mod123.getYear() >= 2016 && mod123.getYear() < 2019)
				 																	, Mod123WriterAEAT2016::new)
		,BIZKAIA_2016	(mod123 -> (mod123.isBizkaia() && mod123.getYear() >= 2016) , Mod123WriterBIZKAIA2016::new)
		,ARABA_2016		(mod123 -> (mod123.isAraba() && mod123.getYear() >= 2016 )	, Mod123WriterARABA2016::new)
		,GIPUZKOA_2016	(mod123 -> (mod123.isGipuzkoa() && mod123.getYear() >= 2016), Mod123WriterGIPUZKOA2016::new)
		,NAVARRA_2016	(mod123 -> (mod123.isNavarra() && mod123.getYear() >= 2016), Mod123WriterGIPUZKOA2016::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod123 mod123) {
			return accepter.accept(mod123);
		}
		public IMod123Writer getInstance() {
			return instancer.getInstance();
		}
	}

	
	public static void fillWriter(Mod123 mod123, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod123)) {
				writer.getInstance().fillWriter(mod123, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
