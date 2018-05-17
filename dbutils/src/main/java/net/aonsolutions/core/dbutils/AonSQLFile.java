package net.aonsolutions.core.dbutils;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class AonSQLFile {

	public final static String AON_MASTER = "aon_master";

	private static final String DB_SEP = "`";
	private final static String ESP = " ";
	private final static String COMMENT0 = "//";
	private final static String COMMENT1 = "#";

	private final static String DEFAULT_SEPARATOR = ";";
	private final static String DELIMITER = "DELIMITER";
	private final static String USE_STATEMENT = "USE";
	private final static String CREATE_DATABASE_STATEMENT = "CREATE DATABASE";
	
	private LineNumberReader reader;
	private int lineNumber;
	private String separator;
	private String fileName;
	private String dbName;

	private AonSQLFile(Reader reader) {
		this.reader = new LineNumberReader(reader);
		separator = DEFAULT_SEPARATOR;
	}
	
	public AonSQLFile(InputStream input) {
		this( new InputStreamReader(input) );
	}

	public AonSQLFile(InputStream input, String charsetNmae) throws UnsupportedEncodingException {
		this( new InputStreamReader(input, charsetNmae) );
	}
	
	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public boolean ready() throws AonSQLException {
		try {
			return reader.ready();
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage(), e);
		}
	}
	
	private boolean isDataBaseStatement( String statement ) {
		String statementUpper = statement.toUpperCase();
		return (dbName != null) &&
			( statementUpper.startsWith(USE_STATEMENT) ||
			statementUpper.startsWith(CREATE_DATABASE_STATEMENT) );
	}

	public String getStatement() throws AonSQLException {
		try {
			StringBuffer stmt = new StringBuffer();
			while (ready()) {
				String line = reader.readLine();
				
				if (line.startsWith(DELIMITER)) {
					// Si en la línea aparecen los metacaracters DELIMITER $$, a 
					// partir de ese momento se tomaraá el nuevo separador, en esta caso $$.
					//
					// *** Se ignora la linea ****
					//
					separator = StringUtils.trim(StringUtils.substringAfter(line, ESP));		
				} else {
					setLineNumber( reader.getLineNumber());
					line = StringUtils.trim(line);
					if (!StringUtils.isEmpty(line) && !line.startsWith(COMMENT0) && !line.startsWith(COMMENT1)) {
						boolean sentenceEnds = line.endsWith(separator);
						if (sentenceEnds) {
							line = StringUtils.removeEnd(line, separator);
						}
						stmt.append(line);
						if (sentenceEnds) {
							break;
						} 
						stmt.append(ESP);
					}
				}
			}
			String statement = StringUtils.trimToNull(stmt.toString());
			if ( (statement != null) && isDataBaseStatement(statement) ) {
				statement = StringUtils.replace( statement.toString(), DB_SEP + AON_MASTER + DB_SEP, DB_SEP + dbName + DB_SEP );
			}
			return statement;
		} catch (Exception e) {
			throw new AonSQLException("Line " + lineNumber + ": " + e.getMessage(), e);
		}
	}

	public List<String> getStatementList( int limit ) throws AonSQLException {
		try {
			List<String> list = new LinkedList<String>();
			StringBuffer stmt = new StringBuffer();
			while (ready()) {
				String line = reader.readLine();
				setLineNumber( reader.getLineNumber());
				StringUtils.trim(line);
				if (!line.startsWith(COMMENT0) && !line.startsWith(COMMENT1)) {
					stmt.append(line);
					stmt.append(ESP);
					if (!StringUtils.isEmpty(line) && line.endsWith(separator)) {
						list.add(stmt.toString());
						stmt = new StringBuffer();
					}
				}
			}
			return list;
		} catch (IOException e) {
			throw new AonSQLException("Line " + lineNumber + ": " + e.getMessage(), e);
		}
	}

	public void close() throws AonSQLException {
		try {
			reader.close();
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage(), e);
		}
	}

}
