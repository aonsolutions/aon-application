package com.code.aon.calendar;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.code.aon.common.IResourceLoader;

public class CalendarLoader {

    private static Logger LOGGER = Logger.getLogger(CalendarLoader.class
            .getName());
    
    private static final String CALENDAR_ELEMENT = "calendar";

    private static final String ID_ATTR = "id";

    private static final String PATH_ATTR = "path";
    
    private static final String YEAR_ATTR = "year";    

    private static SAXParserFactory spf;    

    protected static CalendarManager getCalendarManager(IResourceLoader resourceLoader, String resource) throws CalendarException {
        try {
            if (spf == null) {
                spf = SAXParserFactory.newInstance();
            }
            SAXParser saxParser = spf.newSAXParser();
            CalendarConfigReader reader = new CalendarConfigReader(resourceLoader);
            InputStream in = resourceLoader.getResourceAsStream(resource);
            saxParser.parse(in, reader);
            in.close();
            return reader.getCalendarMananger();
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Error reading " + resource, ioe);
            throw new CalendarException(ioe.getMessage(), ioe);
        } catch (SAXException saxe) {
            LOGGER.log(Level.SEVERE, "Error parsing " + resource, saxe);
            throw new CalendarException(saxe.getMessage(), saxe);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error parsing " + resource, e);
            throw new CalendarException(e.getMessage(), e);
        }
    }
    
    private static class CalendarConfigReader extends DefaultHandler {

        private CalendarManager calendarManager;
        
        private IResourceLoader resourceLoader;

        private static final Logger LOG = Logger
                .getLogger(CalendarConfigReader.class.getName());
        
		public CalendarConfigReader( IResourceLoader resourceLoader ) {
			this.resourceLoader = resourceLoader;
		}

		public void startDocument() throws SAXException {
            LOG.fine("startDocument");
            this.calendarManager = new CalendarManager(this.resourceLoader);
        }

        public CalendarManager getCalendarMananger() {
            return this.calendarManager;
        }

        public void endDocument() throws SAXException {
            LOG.fine("endDocument");
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            if (CALENDAR_ELEMENT.equals(qName)) {
                String id = attributes.getValue(ID_ATTR);
                String yearValue = attributes.getValue(YEAR_ATTR);
                int year = Integer.valueOf(yearValue).intValue();
                String path = attributes.getValue(PATH_ATTR);
                CalendarEntry entry = new CalendarEntry(id, year, path );
                this.calendarManager.addCalendarEntry( entry );
            }
        }

        public void warning(SAXParseException exception) throws SAXException {
            LOG.log(Level.SEVERE, "warning", exception);
        }

        public void error(SAXParseException exception) throws SAXException {
            LOG.log(Level.SEVERE, "error", exception);
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            LOG.log(Level.SEVERE, "fatalError", exception);
        }

    }

}
