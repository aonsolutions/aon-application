package com.code.aon.common.xml;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * XML file easy reader.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-feb-2005
 * @since 1.0
 *  
 */
public abstract class AbstractXMLReader implements XMLReader {

    /**
     * CDATA content.
     */
    protected static final String CDATA = "CDATA"; //$NON-NLS-1$

    /**
     * Empty String.
     */
    protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /**
     * DTD notifications handler.
     */
    protected DTDHandler dtdHandler;

    /**
     * SAX notifications error handler.
     */
    protected ErrorHandler errorHandler;

    /**
     * Entity notifications handler.
     */
    protected EntityResolver entityResolver;

    /**
     * Document notifications handler.
     */
    protected ContentHandler contentHandler;

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#getContentHandler()
     */
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#getDTDHandler()
     */
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
     */
    public void setDTDHandler(DTDHandler handler) {
        this.dtdHandler = handler;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#getEntityResolver()
     */
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
     */
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#getErrorHandler()
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
     */
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
     */
    public boolean getFeature(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        throw new SAXNotRecognizedException("");
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
     */
    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("");
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
     */
    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        throw new SAXNotRecognizedException("");
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("");
    }

    /**
     * Document elements stack.
     */
    protected Stack<String> elements = new Stack<String>();

    /**
     * Return the first document element of the stack.
     * 
     * @return The first document element of the stack.
     */
    protected String pop() {
        return elements.pop();
    }

    /**
     * Stores element name.
     * 
     * @param name
     */
    protected void push(String name) {
        elements.push(name);
    }

    /**
     * End element notification, return by <code>pop()</code> method.
     * 
     * @throws SAXException
     */
    protected void endElement() throws SAXException {
        endElement(pop());
    }

    /**
     * End element defined name notification.
     * 
     * @param name
     * @throws SAXException
     */
    protected void endElement(String name) throws SAXException {
        contentHandler.endElement(EMPTY_STRING, name, name);
    }

    /**
     * Start element defined name notification.
     * 
     * @param name
     * @throws SAXException
     */
    protected void startElement(String name) throws SAXException {
        Attributes attrs = new AttributesImpl();
        contentHandler.startElement(EMPTY_STRING, name, name, attrs);
        push(name);
    }

    /**
     * Start element notification.
     * 
     * @param namespaceURI
     * @param name
     * @param attr
     * @throws SAXException 
     */
    protected void startElement(String namespaceURI, String name, Attribute attr)
            throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute(namespaceURI, attr.getName(), attr.getName(), CDATA,
                attr.getValue());
        contentHandler.startElement(namespaceURI, name, name, attrs);
        push(name);
    }

    /**
     * Start element notification.
     * 
     * @param name
     * @param attr
     * @throws SAXException
     */
    protected void startElement(String name, Attribute attr)
            throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute(EMPTY_STRING, attr.getName(), attr.getName(), CDATA,
                attr.getValue());
        contentHandler.startElement(EMPTY_STRING, name, name, attrs);
        push(name);
    }

    /**
     * Start element notification.
     * 
     * @param namespaceURI
     * @param localName
     * @param qName
     * @param attrs
     * @throws SAXException
     */

    protected void startElement(String namespaceURI, String localName,
            String qName, Attribute attrs[]) throws SAXException {
        AttributesImpl attrsImpl = new AttributesImpl();
        for (int i = 0; i < attrs.length; i++) {
            attrsImpl.addAttribute(EMPTY_STRING, attrs[i].getName(), attrs[i]
                    .getName(), CDATA, attrs[i].getValue());
        }
        contentHandler.startElement(namespaceURI, localName, qName, attrsImpl);
        push(localName);
    }

    /**
     * Start element notification.
     * 
     * @param name
     * @param attrs
     * @throws SAXException
     */
    protected void startElement(String name, Attribute attrs[]) throws SAXException {
        AttributesImpl attrsImpl = new AttributesImpl();
        for (int i = 0; i < attrs.length; i++) {
            attrsImpl.addAttribute(EMPTY_STRING, attrs[i].getName(), attrs[i]
                    .getName(), CDATA, attrs[i].getValue());
        }
        contentHandler.startElement(EMPTY_STRING, name, name, attrsImpl);
        push(name);
    }

    /**
     * Send start and end element defined name notification.
     * 
     * @param name
     * @throws SAXException
     */
    protected void element(String name) throws SAXException {
        startElement(name);
        endElement();
    }

    /**
     * Send start and end element defined name notification.
     * 
     * @param name
     * @param attr
     * @throws SAXException
     */
    protected void element(String name, Attribute attr) throws SAXException {
        startElement(name, attr);
        endElement();
    }

    /**
     * Send start and end element defined name notification.
     * 
     * @param name
     * @param attrs
     * @throws SAXException
     */
    protected void element(String name, Attribute attrs[]) throws SAXException {
        startElement(name, attrs);
        endElement();
    }

    /**
     * Send start and end element defined name notification.
     * 
     * @param name
     * @param text
     * @throws SAXException
     */
    protected void element(String name, String text) throws SAXException {
        startElement(name);
        characters(text);
        endElement();
    }

    /**
     * Receive start document notification.
     * 
     * @throws SAXException
     */
    protected void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    /**
     * Receives end document notification.
     * 
     * @throws SAXException
     */
    protected void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    /**
     * Receive text character notification.
     * 
     * @param str
     * @throws SAXException
     */
    protected void characters(String str) throws SAXException {
        char[] characters = str.toCharArray();
        contentHandler.characters(characters, 0, characters.length);
    }

}