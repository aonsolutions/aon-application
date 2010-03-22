package com.code.aon.common.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class AonSQLFile {

	private final static String ESP = " ";
	private final static String COMMENT = "//";

	private LineNumberReader reader;
	private int lineNumber;

	private String separator = ";";

	public AonSQLFile(InputStream input, String separator) {
		this(input);
		this.separator = separator;
	}

	public AonSQLFile(InputStream input) {
		InputStreamReader isr = new InputStreamReader(input);
		reader = new LineNumberReader(isr);
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public boolean ready() throws AonSQLException {
		try {
			return reader.ready();
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage(), e);
		}
	}

	public String getStatement() throws AonSQLException {
		try {
			StringBuffer stmt = new StringBuffer();
			while (ready()) {
				String line = reader.readLine();
				setLineNumber( reader.getLineNumber());
				StringUtils.trim(line);
				if (!line.startsWith(COMMENT)) {
					stmt.append(line);
					if (!StringUtils.isEmpty(line) && line.endsWith(separator)) {
						break;
					}
					stmt.append(ESP);
				}
			}
			return stmt.toString();
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
				stmt.append(line);
				stmt.append(ESP);
				if ((!StringUtils.isEmpty(line) && line.endsWith(separator)) ||
					(!StringUtils.isEmpty(line) && line.startsWith(COMMENT))) {
					list.add(stmt.toString());
					stmt = new StringBuffer();
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
