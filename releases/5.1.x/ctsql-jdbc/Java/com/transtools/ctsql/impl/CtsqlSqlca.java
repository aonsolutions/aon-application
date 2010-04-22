/*
 *  Copyright 2001
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.ctsql.impl;

/**
 *  Description of the Class
 *
 * @author     eva
 * @created    May 21, 2001
 * @version    $Revision: 1.2 $
 */
class CtsqlSqlca {

	private int sqlcode;
	private int[] sqlerrd = {0, 0, 0, 0, 0, 0};
	private boolean locked;
	private boolean nullWarn;
	private String sqlerrm;


	/**
	 *  Sets the SqlCode attribute of the CtsqlSqlca object
	 *
	 * @param  code  The new SqlCode value
	 */
	public void setSqlCode(int code) {
		sqlcode = code;
	}


	/**
	 *  Sets the SqlErrd attribute of the CtsqlSqlca object
	 *
	 * @param  idx    The new SqlErrd value
	 * @param  value  The new SqlErrd value
	 */
	public void setSqlErrd(int idx, int value) {
		if (idx >= 0 && idx < 6) {
			sqlerrd[idx] = value;
		}
	}


	/**
	 *  Sets the SqlErrorString attribute of the CtsqlSqlca object
	 *
	 * @param  aString  The new SqlErrorString value
	 */
	public void setSqlErrorString(String aString) {
		sqlerrm = aString;
	}


	/**
	 *  Sets the Locked attribute of the CtsqlSqlca object
	 *
	 * @param  b  The new Locked value
	 */
	public void setLocked(boolean b) {
		locked = b;
	}


	/**
	 *  Sets the NullWarn attribute of the CtsqlSqlca object
	 *
	 * @param  b  The new NullWarn value
	 */
	public void setNullWarn(boolean b) {
		nullWarn = b;
	}


	/**
	 *  Gets the SqlCode attribute of the CtsqlSqlca object
	 *
	 * @return    The SqlCode value
	 */
	public int getSqlCode() {
		return sqlcode;
	}


	/**
	 *  Gets an SqlErrd attribute element of the CtsqlSqlca object.<BR>
	 *  0 - reserved <BR>
	 *  1 - TISAM error <BR>
	 *  2 - number of processed tuples <BR>
	 *  3 - reserved <BR>
	 *  4 - statement offset where the error is ocurred <BR>
	 *  5 - rowid after insert <BR>
	 *
	 *
	 * @param  idx  error index
	 * @return      The SqlErrd value
	 */
	public int getSqlErrd(int idx) {
		if (idx >= 0 && idx < 6) {
			return sqlerrd[idx];
		} else {
			return 0;
		}
	}


	/**
	 *  Gets the SqlErrorString attribute of the CtsqlSqlca object
	 *
	 * @return    The SqlErrorString value
	 */
	public String getSqlErrorString() {
		return sqlerrm;
	}


	/**
	 *  Gets the Locked attribute of the CtsqlSqlca object
	 *
	 * @return    The Locked value
	 */
	public boolean getLocked() {
		return locked;
	}


	/**
	 *  Gets the NullWarn attribute of the CtsqlSqlca object
	 *
	 * @return    The NullWarn value
	 */
	public boolean getNullWarn() {
		return nullWarn;
	}


	/**
	 *  Description of the Method
	 */
	public void resetSqlca() {
		sqlcode = 0;
		locked = false;
		nullWarn = false;
		sqlerrd[0] = 0;
		sqlerrd[1] = 0;
		sqlerrd[2] = 0;
		sqlerrd[3] = 0;
	}
}

