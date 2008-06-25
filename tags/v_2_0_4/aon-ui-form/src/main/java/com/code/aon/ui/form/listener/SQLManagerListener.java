package com.code.aon.ui.form.listener;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.ui.form.EnviromentController;
import com.code.aon.ui.form.enumeration.ListenerType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLManagerListener.
 */
public class SQLManagerListener extends ControllerAdapter {

	/** Se obtiene el Logger adecuado. */
	private static final Logger LOGGER = Logger
			.getLogger(SQLManagerListener.class.getName());
	
	/** The Constant SQL_ELEMENT. */
	private static final String SQL_ELEMENT = "sqlElement";
	
	/** The velocity helper. */
	private VelocityHelper velocityHelper;
	
	/** The statements file. */
	private String statementsFile;

	/** The elements. */
	private List<SQLElement> elements;

	/**
	 * The Constructor.
	 */
	public SQLManagerListener() {
		this.elements = new ArrayList<SQLElement>();
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
		loadStatements( statementsFile );
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
			LOGGER.log(Level.SEVERE, "Error parsing sql manager file: " + resource, e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error reading sql manager file: " + resource, e);
		}
	}
	
	/**
	 * Adds the SQL element.
	 * 
	 * @param element the element
	 */
	public void addSQLElement( SQLElement element ) {
		this.elements.add(element);
	}

	/**
	 * Execute.
	 * 
	 * @param type the type
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	private void execute(ListenerType type, ControllerEvent event)
			throws ControllerListenerException {
		ITransferObject to = event.getController().getTo();
		for (SQLElement element : this.elements) {
			if (type.isIncluded(element.getType())) {
				for (String statement : element.getStatements()) {
					try {					
						executeStatement(statement, to);
					} catch (Throwable th) {
						throw new ControllerListenerException(th.getMessage(), th);
					}
				}
			}
		}
	}

	/**
	 * Checks if is select statement.
	 * 
	 * @param statement the statement
	 * 
	 * @return true, if is select statement
	 */
	private boolean isSelectStatement(String statement) {
		if ( statement.length() > 6 ) {
			String select = statement.substring(0, 6);
			return select.equalsIgnoreCase("SELECT");
		}
		return false;
	}

	/**
	 * Process statement.
	 * 
	 * @param to the to
	 * @param statement the statement
	 * 
	 * @return the string
	 * 
	 * @throws AonException the aon exception
	 */
	private String processStatement(String statement, ITransferObject to)
			throws AonException {
		TemplateHelper th = this.velocityHelper.getTemplateHelper();
		th.putInContext("ds", to);
		String sql = statement.replace( "${", "${ds." );
		sql = sql.replace( "#{", "${env." );
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
	 * Execute statement.
	 * 
	 * @param to the to
	 * @param statement the statement
	 * 
	 * @throws SQLException the SQL exception
	 * @throws AonException the aon exception
	 */
	private void executeStatement(String statement, ITransferObject to)
			throws SQLException, AonException {
		String sql = processStatement(statement, to);
		if (isSelectStatement(sql)) {
			executeSelect(sql, to);
		} else {
			executeUpdate(sql, to);
		}
	}

	/**
	 * Gets the property.
	 * 
	 * @param to the to
	 * @param name the name
	 * 
	 * @return the property
	 */
	private PropertyDescriptor getProperty(ITransferObject to, String name) {
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(to);
		for (PropertyDescriptor pd : pds) {
			if (name.equalsIgnoreCase(pd.getName())) {
				return pd;
			}
		}
		return null;
	}

	/**
	 * Sets the property.
	 * 
	 * @param to the to
	 * @param value the value
	 * @param name the name
	 * 
	 * @throws AonException the aon exception
	 */
	private void setProperty(ITransferObject to, String name, Object value)
			throws AonException {
		PropertyDescriptor pd = getProperty(to, name);
		if (pd != null) {
			if (PropertyUtils.isWriteable(to, pd.getName())) {
				try {
					BeanUtils.setProperty(to, pd.getName(), value);
				} catch (IllegalAccessException e) {
					throw new AonException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new AonException(e.getMessage(), e);
				}
			} else {
				LOGGER.warning("No se ha encontrado el setter de la propiedad "
						+ name + " en " + to.getClass().getName());
			}
		} else {
			LOGGER.warning("No se ha encontrado la propiedad " + name + " en "
					+ to.getClass().getName());
		}
	}

	/**
	 * Execute select.
	 * 
	 * @param to the to
	 * @param statement the statement
	 * 
	 * @throws SQLException the SQL exception
	 * @throws AonException the aon exception
	 */
	private void executeSelect(String statement, ITransferObject to)
			throws SQLException, AonException {
		Connection connection = AonUtil.getSQLConnection();

		LOGGER.fine("Executing select sql: " + statement);
		Statement select = connection.createStatement();
		ResultSet rs = select.executeQuery(statement);
		ResultSetMetaData rsmd = rs.getMetaData();
		if (rs.next()) {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnLabel(i);
				Object value = rs.getObject(i);
				setProperty(to, name, value);
			}
		} else {
			LOGGER.warning( "No se ha recuperado ningún registro en la SELECT.");
		}
		rs.close();
		select.close();
		connection.close();
	}
	
	/**
	 * Execute update.
	 * 
	 * @param to the to
	 * @param sql the sql
	 * 
	 * @throws SQLException the SQL exception
	 */
	@SuppressWarnings("unused")
	private void executeUpdate(String sql,ITransferObject to)
			throws SQLException {
		Connection connection = AonUtil.getSQLConnection();

		LOGGER.fine("Executing select sql: " + sql);
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql);
		statement.close();
		connection.commit();
		connection.close();
	}

	/**
	 * Before bean added.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
		execute(ListenerType.BEFORE_ADDED, event);
	}
	
	/**
	 * Before bean canceled.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void beforeBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.BEFORE_CANCELED, event);
	}

	/**
	 * Before bean created.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.BEFORE_CREATED, event);
	}

	/**
	 * Before bean removed.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.BEFORE_REMOVED, event);
	}

	/**
	 * Before bean selected.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.BEFORE_SELECTED, event);
	}

	/**
	 * Before bean updated.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.BEFORE_UPDATED, event);
	}

	/**
	 * After bean added.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.AFTER_ADDED, event);
	}

	/**
	 * After bean canceled.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.AFTER_CANCELED, event);
	}


	/**
	 * After bean created.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.AFTER_CREATED, event);
	}

	/**
	 * After bean removed.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.AFTER_REMOVED, event);
	}

	/**
	 * After bean selected.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.AFTER_SELECTED, event);
	}

	/**
	 * After bean updated.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		execute(ListenerType.AFTER_UPDATED, event);
	}

	/**
	 * The Class SQLElement.
	 */
	public class SQLElement {

		/** The type. */
		private int type;

		/** The statements. */
		private String[] statements;

		/**
		 * The Constructor.
		 */
		public SQLElement() {
		}
		
		/**
		 * Sets the statements.
		 * 
		 * @param sqls the sqls
		 */
		public void setStatements(String sqls) {
			StringTokenizer st = new StringTokenizer(sqls, ";");
			this.statements = new String[st.countTokens()];
			for (int i = 0; i < this.statements.length; i++) {
				this.statements[i] = st.nextToken().trim();
			}
		}

		/**
		 * Sets the type.
		 * 
		 * @param type the type
		 */
		public void setType(int type) {
			this.type = type;
		}

		/**
		 * Gets the statements.
		 * 
		 * @return the statements
		 */
		public String[] getStatements() {
			return statements;
		}

		/**
		 * Gets the type.
		 * 
		 * @return the type
		 */
		public int getType() {
			return type;
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
				String typeValue = attributes.getValue("type");
				sqlElement.setType( Integer.valueOf(typeValue).intValue() );
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
				sqlElement.setStatements(text.toString().trim());
				addSQLElement( sqlElement );				
				this.sqlElement = null;
			}
		}
		
	}
	
}
