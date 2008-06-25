package com.code.aon.ui.form.listener;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.ui.form.EnviromentController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * 
 *
 */
public class SQLCollectionsListener extends ControllerAdapter {

	/** Se obtiene el Logger adecuado. */
	private static final Logger LOGGER = Logger
			.getLogger(SQLCollectionsListener.class.getName());

	/** The Constant SQL_ELEMENT. */
	private static final String SQL_ELEMENT = "sqlElement";
	
	/** The velocity helper. */
	private VelocityHelper velocityHelper;

	/** The statements file. */
	private String statementsFile;

	/** The elements. */
	private Map<String, SQLElement> elements;
	
	/** The resolver. */
	private ResolverFakeMap resolver;	
	
	/**
	 * The Constructor.
	 */
	public SQLCollectionsListener() {
		this.elements = new HashMap<String, SQLElement>();
		this.resolver = new ResolverFakeMap();		
		this.velocityHelper = new VelocityHelper();
		try {
			this.velocityHelper.init(null);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error creating velocity engine", e);
		}
	}

	/**
	 * Gets the statements file.
	 * 
	 * @return the statements file
	 */
	public String getStatementsFile() {
		return statementsFile;
	}

	/**
	 * Sets the statements file.
	 * 
	 * @param statementsFile the statements file
	 */
	public void setStatementsFile(String statementsFile) {
		this.statementsFile = statementsFile;
		loadStatements(statementsFile);
	}

	/**
	 * Load statements.
	 * 
	 * @param resource the resource
	 */
	private void loadStatements(String resource) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		InputStream in = ctx.getResourceAsStream(resource);
		try {
			SAXParser parser = factory.newSAXParser();
			DefaultHandler dh = new SQLCollectionsSAXHandler();
			parser.parse( in, dh );
		} catch (ParserConfigurationException e) {
			LOGGER.log(Level.SEVERE, "Error creating SAX parser", e);
		} catch (SAXException e) {
			LOGGER.log(Level.SEVERE, "Error parsing sql collections file: " + resource, e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error reading sql collections file: " + resource, e);
		}
	}

	/**
	 * Adds the SQL element.
	 * 
	 * @param element the element
	 */
	public void addSQLElement(SQLElement element) {
		this.elements.put(element.getName(), element);
	}

	/**
	 * Gets the elements.
	 * 
	 * @return the elements
	 */
	public Map<String, SQLElement> getElements() {
		return elements;
	}

	/**
	 * Process statement.
	 * 
	 * @param statement the statement
	 * 
	 * @return the string
	 * 
	 * @throws AonException the aon exception
	 */
	private String processStatement(String statement) throws AonException {
		TemplateHelper th = this.velocityHelper.getTemplateHelper();
		String sql = statement.replace( "#{", "${env." );
		Properties properties = EnviromentController.getCurrentProperties();
		if ( properties != null ) {
			th.putInContext( "env", properties );
		}
		String result = null;
		try {
			result = th.processTemplate(sql, "SqlStatement");
		} catch (AonException e) {
			throw new AonException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Execute select.
	 * 
	 * @param statement the statement
	 * @param collectionClass the collection class
	 * 
	 * @return the list< select item>
	 * 
	 * @throws SQLException the SQL exception
	 */
	private List<SelectItem> executeSelect(String statement, Class collectionClass)
			throws SQLException {

		Connection connection = AonUtil.getSQLConnection();

		List<SelectItem> result = new ArrayList<SelectItem>();

		LOGGER.info("Executing select sql: " + statement);
		Statement select = connection.createStatement();
		ResultSet rs = select.executeQuery(statement);
		while (rs.next()) {
			Object value = rs.getObject(1);
			if ( value != null ) {
				if (! collectionClass.equals(value)) {
					String valueString = rs.getString(1);
					value = ConvertUtils.convert( valueString, collectionClass);
				}
				String description = rs.getString(2);
				SelectItem item = new SelectItem(value, description);
				result.add(item);
			}
		}
		rs.close();
		select.close();
		connection.close();
		return result;
	}

	/**
	 * Update collection.
	 * 
	 * @param to the to
	 * @param se the se
	 */
	private void updateCollection(SQLElement se, ITransferObject to) {
		List<SelectItem> list = Collections.emptyList();
		String statement = null;
		try {
			statement = processStatement(se.getStatement());
		} catch (AonException e) {
			LOGGER.log(Level.SEVERE, "Error replacing values in sql statement: " + se.getStatement(), e);
		}	
		Class collectionClass = null;
		try {
			collectionClass = PropertyUtils.getPropertyType( to, se.getName() );
		} catch (IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "Error resolving value class for colleciton: " + se.getName(), e);
		} catch (InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, "Error resolving value class for colleciton: " + se.getName(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, "Error resolving value class for colleciton: " + se.getName(), e);
		}
		try {
			list = executeSelect(statement, collectionClass );
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error executing SQL: " + se.getStatement(), e);
		}
		se.setCollection(list);
	}

	/**
	 * Update transfer object.
	 * 
	 * @param to the to
	 * @param se the se
	 */
	private void updateTransferObject(SQLElement se, ITransferObject to) {
			Object value;
			try {
				value = PropertyUtils.getProperty(to, se.getName());
				if ((value == null) || (!se.isValidValue(value))) {
					if (! se.getCollection().isEmpty() ) {
						SelectItem newValue = se.getCollection().get(0);
						PropertyUtils.setProperty(to, se.getName(), newValue.getValue());
					}
				}
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, "Error updating value " + se.getName() + " of Transfer Object " + to, e);
			} catch (InvocationTargetException e) {
				LOGGER.log(Level.SEVERE, "Error updating value " + se.getName() + " of Transfer Object " + to, e);
			} catch (NoSuchMethodException e) {
				LOGGER.log(Level.SEVERE, "Error updating value " + se.getName() + " of Transfer Object " + to, e);
			}
	}
	
	/**
	 * Update collections.
	 * 
	 * @param to the to
	 */
	private void updateCollections(ITransferObject to) {
		for (SQLElement se : this.elements.values()) {
			try {
				updateCollection(se, to);
				updateTransferObject(se, to);
			} catch ( Throwable e ) {
				LOGGER.log(Level.SEVERE, "Error updating collection of SQL Field: " + se.getName(), e);
			}
		}
	}

	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	public Map getResolver() {
		return this.resolver;
	}
	
	/**
	 * Before bean created.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		updateCollections(event.getController().getTo());
	}

	/**
	 * After bean selected.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		updateCollections(event.getController().getTo());
	}

	/**
	 * The Class SQLElement.
	 */
	public class SQLElement {

		/** The name. */
		private String name;

		/** The statement. */
		private String statement;

		/** The collection. */
		private List<SelectItem> collection;

		/**
		 * The Constructor.
		 */
		public SQLElement() {
		}

		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name.
		 * 
		 * @param name the name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the statement.
		 * 
		 * @return the statement
		 */
		public String getStatement() {
			return statement;
		}

		/**
		 * Sets the statement.
		 * 
		 * @param statement the statement
		 */
		public void setStatement(String statement) {
			this.statement = statement;
		}

		/**
		 * Gets the collection.
		 * 
		 * @return the collection
		 */
		public List<SelectItem> getCollection() {
			return collection;
		}

		/**
		 * Sets the collection.
		 * 
		 * @param collection the collection
		 */
		public void setCollection(List<SelectItem> collection) {
			this.collection = collection;
		}

		/**
		 * Checks if is valid value.
		 * 
		 * @param value the value
		 * 
		 * @return true, if is valid value
		 */
		public boolean isValidValue(Object value) {
			for (SelectItem item : this.collection) {
				if (item.getValue().equals(value)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Gets the label.
		 * 
		 * @param value the value
		 * 
		 * @return the label
		 */
		public String getLabel(Object value) {
			for (SelectItem item : this.collection) {
				if (item.getValue().equals(value)) {
					return item.getLabel();
				}
			}
			return null;
		}

	}

	/**
	 * The Class SQLCollectionsSAXHandler.
	 */
	private class SQLCollectionsSAXHandler extends DefaultHandler {

		/** The sql element. */
		private SQLElement sqlElement;
		
		/** The text. */
		private StringBuffer text;
		
		/**
		 * Start element.
		 * 
		 * @param qName the q name
		 * @param attributes the attributes
		 * @param uri the uri
		 * @param localName the local name
		 * 
		 * @throws SAXException the SAX exception
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if ( SQL_ELEMENT.equals(qName) ) {
				this.sqlElement = new SQLElement();
				this.text = new StringBuffer();
				String nameValue = attributes.getValue("name");
				sqlElement.setName(nameValue);
			}
		}

		/**
		 * Characters.
		 * 
		 * @param ch the ch
		 * @param start the start
		 * @param length the length
		 * 
		 * @throws SAXException the SAX exception
		 */
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if ( this.sqlElement != null ) {
				text.append( ch, start, length );				
			}
		}

		/**
		 * End element.
		 * 
		 * @param qName the q name
		 * @param uri the uri
		 * @param localName the local name
		 * 
		 * @throws SAXException the SAX exception
		 */
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ( SQL_ELEMENT.equals(qName) ) {
				sqlElement.setStatement(text.toString().trim());
				addSQLElement(sqlElement);			
				this.sqlElement = null;
			}
		}
		
	}
	
	/**
	 * The Class ResolverFakeMap.
	 */
	private class ResolverFakeMap extends HashMap {

		/**
		 * Get.
		 * 
		 * @param key the key
		 * 
		 * @return the object
		 */
		@Override
		public Object get(Object key) {
			try {
				DataModel model = getController().getModel();
				if ( model.isRowAvailable() ) {
					ITransferObject to = (ITransferObject) model.getRowData();
					if ( model.getRowIndex() == 0 ) {
						updateCollections( to );
					}
					Object value = PropertyUtils.getProperty( to, (String) key );
					SQLElement sqle = elements.get( key );
					return sqle.getLabel(value);
				}
			} catch (ManagerBeanException e) {
				LOGGER.severe(e.getMessage());
			} catch (IllegalAccessException e) {
				LOGGER.severe(e.getMessage());
			} catch (InvocationTargetException e) {
				LOGGER.severe(e.getMessage());
			} catch (NoSuchMethodException e) {
				LOGGER.severe(e.getMessage());
			} catch ( IllegalArgumentException e ) {
				LOGGER.severe(e.getMessage());
			}
			return null;
		}
		
	}	
	
}
