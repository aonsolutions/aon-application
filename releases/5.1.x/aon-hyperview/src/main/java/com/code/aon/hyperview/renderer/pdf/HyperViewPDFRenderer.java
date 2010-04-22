package com.code.aon.hyperview.renderer.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.code.aon.common.xml.AbstractXMLReader;
import com.code.aon.common.xml.Attribute;
import com.code.aon.hyperview.HyperViewException;
import com.code.aon.hyperview.model.HyperViewAttribute;
import com.code.aon.hyperview.model.HyperViewCompositeView;
import com.code.aon.hyperview.model.HyperViewContents;
import com.code.aon.hyperview.model.HyperViewFormSQLNode;
import com.code.aon.hyperview.model.HyperViewListSQLNode;
import com.code.aon.hyperview.model.HyperViewNode;
import com.code.aon.hyperview.model.IHyperViewContent;
import com.code.aon.hyperview.player.IHyperViewPlayerNode;
import com.code.aon.hyperview.player.ViewResult;
import com.code.aon.hyperview.renderer.HyperViewAbstractRenderer;
import com.code.aon.hyperview.renderer.HyperViewRendererException;

public class HyperViewPDFRenderer extends HyperViewAbstractRenderer {

	private static String MIME_TYPE = "application/pdf";

	private static String EXTENSION = "pdf";

	private static final String XSL_RESOURCE = "xsl-fo.xsl";

	private TransformerFactory tFactory = TransformerFactory.newInstance();

	public String getContentType() {
		return MIME_TYPE;
	}

	public String getExtension() {
		return EXTENSION;
	}

	public void start(OutputStream out) throws HyperViewRendererException {
		try {
			NodeXMLReader reader = new NodeXMLReader();
			URL template = HyperViewPDFRenderer.class.getResource(XSL_RESOURCE);
			StreamSource xsl = new StreamSource(template.openStream(), template
					.toExternalForm());
			Transformer transformer = tFactory.newTransformer(xsl);
			SAXSource xml = new SAXSource(reader, new InputSource());
			File resultFile = File.createTempFile("treeNode", ".xml");
			StreamResult result = new StreamResult(resultFile);

			Date date = new Date();
			transformer.setParameter("today", getDateFormatter().format(date));

			URL logo = getLogo();
			transformer.setParameter("logo", logo.toExternalForm());
			transformer.transform(xml, result);
			InputSource xsl_fo = new InputSource(
					new FileInputStream(resultFile));

			Driver fop = new Driver();
			fop.setOutputStream(out);
			fop.setInputSource(xsl_fo);
			fop.setRenderer(Driver.RENDER_PDF);
			fop.run();
			out.flush();
			out.close();
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
			throw new HyperViewRendererException(e.getMessage());
		} catch (FOPException e) {
			e.printStackTrace();
			throw new HyperViewRendererException(e.getMessage());
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new HyperViewRendererException(e.getMessage());
		}
	}

	private class NodeXMLReader extends AbstractXMLReader {

		private final static String HYPERVIEW_ELEMENT = "hyperview";

		// private final static String PARAMETERS_ELEMENT = "parameters";

		// private final static String PARAMETER_ELEMENT = "parameter";

		// private final static String VALUE_ATTR = "value";

		private final static String NODE_ELEMENT = "node";

		private final static String ID_ATTR = "id";

		private final static String NAME_ATTR = "name";

		private final static String DEPTH_ATTR = "depth";

		private final static String FORM_ELEMENT = "form";

		private final static String FIELD_ELEMENT = "field";

		private final static String LABEL_ELEMENT = "label";

		private final static String VALUE_ELEMENT = "value";

		private final static String LIST_ELEMENT = "list";

		private final static String HEADER_ELEMENT = "header";

		private final static String HEADER_CELL = "headerCell";

		private final static String ROW_ELEMENT = "row";

		private final static String CELL_ELEMENT = "cell";

		public void parse(InputSource input) throws IOException, SAXException {
			this.parseHyperView();
		}

		public void parse(String systemId) throws IOException, SAXException {
			this.parseHyperView();
		}

		public void parseHyperView() throws IOException, SAXException {
			try {
				startDocument();
				Attribute hyperViewName = new Attribute(NAME_ATTR,
						getHyperViewPlayer().getHyperView().getLabel());
				startElement(HYPERVIEW_ELEMENT, hyperViewName);
				this.parseParameters();
				this.parseNode(getNode());
				endElement(HYPERVIEW_ELEMENT);
				endDocument();
			} catch (HyperViewRendererException e) {
				throw new SAXException(e.getMessage(), e);
			}
		}

		private void parseParameters() throws SAXException {
			// TODO implement parameters.
		}

		@SuppressWarnings("unchecked")
		private void parseNode(IHyperViewPlayerNode node) throws SAXException,
				HyperViewRendererException {
			try {
				if (!node.isDeployed()) {
					getHyperViewPlayer().deployNode(node);
				}
				String text = getIdentifierFormatted(node.getIdentifier());
				Attribute id = new Attribute(ID_ATTR, text);
				Attribute name = new Attribute(NAME_ATTR, node.getDescription());
				int d = StringUtils.countMatches(text, ".");
				Attribute depth = new Attribute(DEPTH_ATTR, Integer.toString(d));
				Attribute[] attributes = new Attribute[] { id, name, depth };
				startElement(NODE_ELEMENT, attributes);
				HyperViewNode wnode = node.getHyperViewNode();
				resolveContents(node, wnode.getContents());
				if (isRecursive()) {
					List<IHyperViewPlayerNode> children = node.getChildren();
					if (children != null && !children.isEmpty()) {
						for (IHyperViewPlayerNode child : children) {
							parseNode(child);
						}
					}
				}
				endElement(NODE_ELEMENT);
			} catch (NullPointerException e) {
				e.printStackTrace();
				throw e;
			} catch (SAXException e) {
				e.printStackTrace();
				throw e;
			} catch (HyperViewException e) {
				e.printStackTrace();
				throw new SAXException(e);
			}
		}

		@SuppressWarnings("unchecked")
		private void resolveContents(IHyperViewPlayerNode node,
				HyperViewContents contents) throws HyperViewException,
				SAXException, HyperViewRendererException {
			if (contents != null) {
				List<IHyperViewContent> list = contents.getContents();
				for (IHyperViewContent content : list) {
					if (content instanceof HyperViewFormSQLNode) {
						resolveForm(node, (HyperViewFormSQLNode) content);
					} else if (content instanceof HyperViewListSQLNode) {
						resolveList(node, (HyperViewListSQLNode) content);
					} else if (content instanceof HyperViewCompositeView) {
						HyperViewCompositeView composite = (HyperViewCompositeView) content;
						if (composite != null) {
							resolveContents(node, composite.getContents());
						}
					}
				}
			}
		}

		private void resolveForm(IHyperViewPlayerNode node,
				HyperViewFormSQLNode content) throws HyperViewException,
				SAXException, HyperViewRendererException {
			if (content != null) {
				getHyperViewPlayer().resolveFormContent(node, content);
				List<HyperViewAttribute> attrs = content.getMetadata();
				Attribute name = new Attribute(NAME_ATTR, node.getDescription());
				startElement(FORM_ELEMENT, name);
				for (int rownum = 0; rownum < attrs.size(); rownum++) {
					HyperViewAttribute attr = attrs.get(rownum);
					startElement(FIELD_ELEMENT);
					element(LABEL_ELEMENT, attr.getLabel());
					ViewResult result = content.getResult();
					String valueAsString = "";
					if (result != null) {
						Object[] values = content.getResult().getValues();
						Object value = null;
						if (values != null) {
							value = values[rownum];
						}
						valueAsString = getValue(attr, value);
					}
					element(VALUE_ELEMENT, valueAsString);
					endElement(FIELD_ELEMENT);
				}
				endElement(FORM_ELEMENT);
			}
		}

		private void resolveList(IHyperViewPlayerNode node,
				HyperViewListSQLNode content) throws HyperViewException,
				SAXException, HyperViewRendererException {
			if (content != null) {
				getHyperViewPlayer().resolveListContent(node, content);
				List<HyperViewAttribute> attrs = content.getMetadata();
				startElement(LIST_ELEMENT);
				startElement(HEADER_ELEMENT);
				for (int colnum = 0; colnum < attrs.size(); colnum++) {
					HyperViewAttribute attr = attrs.get(colnum);
					element(HEADER_CELL, attr.getLabel());
				}
				endElement(HEADER_ELEMENT);
				List<ViewResult> list = content.getQueryResult();
				for (ViewResult qr : list) {
					Object[] objs = qr.getValues();
					startElement(ROW_ELEMENT);
					for (int colnum = 0; colnum < objs.length; colnum++) {
						HyperViewAttribute attr = attrs.get(colnum);
						element(CELL_ELEMENT, getValue(attr, objs[colnum]));
					}
					endElement(ROW_ELEMENT);
				}
				endElement(LIST_ELEMENT);
			}
		}

		private String getIdentifierFormatted(String identifier) {
			String[] arr = identifier.split(":");
			StringBuffer buf = new StringBuffer();
			if (arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {
					if (i > 0) {
						buf.append(".");
					}
					int x = Integer.parseInt(arr[i]) + 1;
					buf.append(x);
				}
				return buf.toString();
			}
			return "1";
		}

	}

}
