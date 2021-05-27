package com.esferalia.aon.payroll.tgss.creta;

import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;

public class IndentXMLStreamWriter implements XMLStreamWriter {
	
	private int line;
	private int depth;
	private String indent;
	private Stack<Integer> lines;
	
	private XMLStreamWriter xmlStreamWriter;
	
	

	public IndentXMLStreamWriter(XMLStreamWriter xmlStreamWriter,String indent ) {
		super();
		this.indent = indent;
		this.xmlStreamWriter = xmlStreamWriter;
		this.lines = new Stack<Integer>();
	}

	@Override
	public void close() throws XMLStreamException {
		xmlStreamWriter.close();
	}

	@Override
	public void flush() throws XMLStreamException {
		xmlStreamWriter.flush();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return xmlStreamWriter.getNamespaceContext();
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return xmlStreamWriter.getPrefix(uri);
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return xmlStreamWriter.getProperty(name);
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		xmlStreamWriter.setDefaultNamespace(uri);
	}

	@Override
	public void setNamespaceContext(NamespaceContext context)
			throws XMLStreamException {
		xmlStreamWriter.setNamespaceContext(context);
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		xmlStreamWriter.setPrefix(prefix, uri);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI,
			String localName, String value) throws XMLStreamException {
		xmlStreamWriter.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName,
			String value) throws XMLStreamException {
		xmlStreamWriter.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		xmlStreamWriter.writeAttribute(localName, value);
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		xmlStreamWriter.writeCData(data);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len)
			throws XMLStreamException {
		xmlStreamWriter.writeCharacters(text, start, len);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		xmlStreamWriter.writeCharacters(text);
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
		data = indent(data);
		xmlStreamWriter.writeComment(data);
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		xmlStreamWriter.writeDTD(dtd);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		xmlStreamWriter.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		indent();
		xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		indent();
		xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
		indent();
		xmlStreamWriter.writeEmptyElement(localName);
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		xmlStreamWriter.writeEndDocument();
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		end();
		xmlStreamWriter.writeEndElement();
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		xmlStreamWriter.writeEntityRef(name);
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
		xmlStreamWriter.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeProcessingInstruction(String target, String data)
			throws XMLStreamException {
		xmlStreamWriter.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeProcessingInstruction(String target)
			throws XMLStreamException {
		xmlStreamWriter.writeProcessingInstruction(target);
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		depth = 0;
		xmlStreamWriter.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String encoding, String version)
			throws XMLStreamException {
		set();
		xmlStreamWriter.writeStartDocument(encoding, version);
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
		set();
		xmlStreamWriter.writeStartDocument(version);
	}

	@Override
	public void writeStartElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		start();
		xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName)
			throws XMLStreamException {
		start();
		xmlStreamWriter.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		start();
		xmlStreamWriter.writeStartElement(localName);
	} 
	
	private void set() {
		line = 0;
		depth = 0;
	}
	
	private void start() throws XMLStreamException {
		indent();
		depth++;
		lines.push(line);
	}

	private void end() throws XMLStreamException {
		depth--;
		if ( line > lines.pop())
			indent();
	}

	private void indent() throws XMLStreamException {
		line++;
		xmlStreamWriter.writeCharacters("\n");
		xmlStreamWriter.writeCharacters(StringUtils.repeat(indent, depth));
	}

	private String indent(String str) throws XMLStreamException {
		indent();
		return str.replaceAll("\n", "\n"+StringUtils.repeat(indent, depth));
	}
}
