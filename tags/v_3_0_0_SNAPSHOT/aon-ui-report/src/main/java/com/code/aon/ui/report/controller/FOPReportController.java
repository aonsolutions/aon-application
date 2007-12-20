package com.code.aon.ui.report.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.log.CommonsLogLogSystem;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.EnviromentController;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.report.context.VContextController;
import com.sun.org.apache.xml.internal.utils.XMLReaderManager;

public class FOPReportController {

	private static final Logger LOGGER = Logger.getLogger(FOPReportController.class.getName());

	private static final String XSL2FO_RESOURCE = "/com/code/aon/ui/report/controller/xhtml2fo.xsl";
	
	private static final String DTD_RESOURCE_PATH = "/com/code/aon/ui/report/resources/";
	
	private static final String URL_RESOLVER = "urlResolver";

	private String controllerName;

	private String id;
	
	private String preferencesURL;
	
	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private ITransferObject getTo( BasicController controller ) throws ManagerBeanException, ExpressionException {
		Criteria criteria = EnviromentController.getCriteria( controller, this.id );
		List<ITransferObject> list = controller.getManagerBean().getList(criteria);
		if ( (list!=null) && (list.size() > 0) ) {
			return list.get(0);
		}
		return null;
	}
	
	protected ITransferObject getTo() throws ManagerBeanException, ExpressionException {
		ITransferObject result = null;
		if (controllerName != null) {
			BasicController controller = obtainController(controllerName);
			if (controller != null) {
				if ( this.id != null ) {
					result = getTo( controller );
				}
				if ( result == null ) {
					result = controller.getTo();
				}
			}
		}
		return result;
	}

	public void onReport(@SuppressWarnings("unused")
	ActionEvent event) {
		try {
			LOGGER.info("controllerName=" + this.controllerName + ",id=" + this.id );
			ServletResponse res = (ServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			res.setContentType(OutputFormat.PDF.getMimeType());
			OutputStream out = res.getOutputStream();
			ITransferObject to = getTo();
			createFop(to, out);
			out.close();
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.responseComplete();
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, "Error generation Report", th);
		}
	}

	private BasicController obtainController(String name) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + name + "}");
		return (BasicController) vb.getValue(ctx);
	}
	
	protected VContextController getContextController() {
		VContextController vContextController = new VContextController();
		vContextController.setVelocityContext(new VelocityContext());
		vContextController.setControllerName(this.controllerName);
		return vContextController;
	}

	// TODO Intentar saltarse el transform utilizando el driver de FOP.
	private void createFop(ITransferObject to, OutputStream out) {
		// xmlFile: Fichero con la estructura
		FacesContext fctx = FacesContext.getCurrentInstance();
		ExternalContext ctx = fctx.getExternalContext();
		String reportPath = "/report/" + this.controllerName.replace('_', '/');
		InputStream xmlFile = ctx.getResourceAsStream( reportPath + ".xml");

		VContextController contextController = getContextController();
		contextController.resolveVariables(xmlFile, to);
		try {
			xmlFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error closing file " + xmlFile, e);
		}

		// outFile: fichero xhtml de plantilla
		InputStream htmlFile = ctx.getResourceAsStream( reportPath + ".xhtml");
		VelocityContext velocityContext = contextController.getVelocityContext();
		velocityContext.put( URL_RESOLVER, new URLResolver() );
		html2fo(htmlFile, velocityContext, out);
		try {
			htmlFile.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error closing file " + htmlFile, e);
		}

		System.gc();
	}

	public String getPreferencesURL() {
		return preferencesURL;
	}

	public void setPreferencesURL(String preferencesURL) {
		this.preferencesURL = preferencesURL;
	}

	public String getEncodedPreferencesURL() {
		String url = null;
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			url = ec.getRequestContextPath() + this.preferencesURL;
			url = ec.encodeActionURL(url);
			if (!url.contains("jsessionid")) {
				HttpServletRequest req = (HttpServletRequest) ec.getRequest();
				String id = req.getRequestedSessionId();
				if (id != null) {
					url = url + ";jsessionid=" + id;
				}
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return url;
	}

	private File removeComments(InputStream input) {
		File commentsFile = null;
		try {
			commentsFile = File.createTempFile("comments", ".xhtml");
			// Eliminar los comentarios en las sentencias de Velocity
			InputStreamReader tidyReader = new InputStreamReader(input);
			BufferedReader bReader = new BufferedReader(tidyReader);
			StringWriter sWriter = new StringWriter();
			String line = new String();
			line = bReader.readLine();
			while (line != null) {
				sWriter.append(line);
				sWriter.append("\n");
				line = bReader.readLine();
			}
			sWriter.close();

			Pattern ptt = Pattern.compile("<!--velocity");
			Matcher m = ptt.matcher(sWriter.toString());
			String sOut = m.replaceAll("");
			ptt = Pattern.compile("velocity-->");
			m = ptt.matcher(sOut);
			sOut = m.replaceAll("");
			FileWriter fWriter = new FileWriter(commentsFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(sOut);
			bWriter.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error creating temp files", e);
		}

		return commentsFile;
	}

	private File applyVelocity(File commentsFile, VelocityContext ctx) {
		File xhtmlDataFile = null;
		try {
			xhtmlDataFile = File.createTempFile("report", ".xhtml");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error creating temp files", e);
		}

		Properties p = new Properties();
		p.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, 
				CommonsLogLogSystem.class.getName());
		p.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM + ".commons.logging.name",
				"com.code.aon.util.VelocityHelper");
		try {
			Velocity.init(p);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error initializing Velocity", e);
		}
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(xhtmlDataFile));
			Velocity.evaluate(ctx, writer, "Go!", new InputStreamReader(new FileInputStream(commentsFile)));
			writer.flush();
			writer.close();
		} catch (ParseErrorException e) {
			LOGGER.log(Level.SEVERE, "Error parsing xhtml template", e);
		} catch (MethodInvocationException e) {
			LOGGER.log(Level.SEVERE, "Error evaluating xhtml template", e);
		} catch (ResourceNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Error template not fount", e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error writting xhtml template", e);
		}

		return xhtmlDataFile;
	}

	private File createXslFoFile(File xhtmlDataFile, OutputStream out) {
		File xslfoFile = null;
		try {
			xslfoFile = File.createTempFile("xsl-fo", ".xhtml");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error creating temp files", e);
		}
		FileInputStream xslfoFileInputStream = null;
		try {
			URL template = FOPReportController.class.getResource(XSL2FO_RESOURCE);
			StreamSource xsl = new StreamSource(template.openStream(), template.toExternalForm());

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xsl);

            SAXParserFactory saxParserFactory=SAXParserFactory.newInstance();
            saxParserFactory.setValidating(false);
            XMLReader xmlReader = XMLReaderManager.getInstance().getXMLReader();
            xmlReader.setEntityResolver(new FOPReportController().new MyEntityResolver());
            SAXSource xhtml = new SAXSource(new InputSource(new FileInputStream(xhtmlDataFile)));
            xhtml.setXMLReader(xmlReader);

			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("dd/MM/yyyy");
			transformer.setParameter("today", sdf.format(new Date()));
			LOGGER.info("XSL-FO file path: "+xslfoFile.getAbsolutePath());
			FileOutputStream xslfoFileOutputStream = new FileOutputStream(xslfoFile);
			transformer.transform(xhtml, new StreamResult(xslfoFileOutputStream));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error creating xsl fo file", e);
		} catch (TransformerConfigurationException e) {
			LOGGER.log(Level.SEVERE, "Error creating xsl fo file", e);
		} catch (TransformerException e) {
			LOGGER.log(Level.SEVERE, "Error creating xsl fo file", e);
		} catch (SAXException e) {
            LOGGER.log(Level.SEVERE, "Error creating xsl fo file", e);
        }
		try {
			xslfoFileInputStream = new FileInputStream(xslfoFile);
			InputSource source = new InputSource(xslfoFileInputStream);
			Driver driver = new Driver();
			driver.setRenderer(Driver.RENDER_PDF);
			driver.setOutputStream(out);
			driver.setInputSource(source);
			driver.run();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error creating pdf file", e);
		} catch (FOPException e) {
			LOGGER.log(Level.SEVERE, "Error creating pdf file", e);
		}
		return xslfoFile;
	}

	public void html2fo(InputStream input, VelocityContext ctx, OutputStream out) {
		File commentsFile = removeComments(input);
		File xhtmlDataFile = applyVelocity(commentsFile, ctx);
		File xslfoFile = createXslFoFile(xhtmlDataFile, out);
		commentsFile.delete();
		xhtmlDataFile.delete();
		xslfoFile.delete();
	}

	public class MyEntityResolver implements EntityResolver {
		private final Logger LOGGER2 = Logger.getLogger(MyEntityResolver.class.getName());

		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			File file = new File(systemId);
			String fileName = file.getName();
			String path = DTD_RESOURCE_PATH + fileName;
			InputStream in = MyEntityResolver.class.getResourceAsStream(path);
			if (in != null) {
				LOGGER2.info("** " + systemId + " resource found!");
				return new InputSource(in);
			}
			LOGGER2.warning(" Unable to find " + systemId + " resource!");
			return null;
		}
	}

	public class URLResolver {
		
		private String path;
		
		public URLResolver() {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			this.path = "http://" + request.getLocalAddr() + ":"
					+ request.getLocalPort() + request.getContextPath();
		}
		
		public String resolver( String resource ) {
			return path + resource;
		}
		
	}
}