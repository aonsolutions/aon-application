package com.transtools.ctsql;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public abstract class CtsqlConstants {
	/**
	 *  Description of the Field
	 */
	public final static short FNEMPTY = -1;
	/**
	 *  Description of the Field
	 */
	public final static short FNDBCHANGE = 1;
	/**
	 *  Description of the Field
	 */
	public final static short FNSELECT = 2;
	/**
	 *  Description of the Field
	 */
	public final static short FNINTOTMP = 3;
	/**
	 *  Description of the Field
	 */
	public final static short FNUPD = 4;
	/**
	 *  Description of the Field
	 */
	public final static short FNDEL = 5;
	/**
	 *  Description of the Field
	 */
	public final static short FNINSERTV = 6;
	/**
	 *  Description of the Field
	 */
	public final static short FNWCUPD = 7;
	/**
	 *  Description of the Field
	 */
	public final static short FNWCDEL = 8;
	/**
	 *  Description of the Field
	 */
	public final static short FNINSERT = 9;
	/**
	 *  Description of the Field
	 */
	public final static short FNLOCK = 10;
	/**
	 *  Description of the Field
	 */
	public final static short FNUNLOCK = 11;
	/**
	 *  Description of the Field
	 */
	public final static short FNDBCREATE = 12;
	/**
	 *  Description of the Field
	 */
	public final static short FNDBDROP = 13;
	/**
	 *  Description of the Field
	 */
	public final static short FNCREATAB = 14;
	/**
	 *  Description of the Field
	 */
	public final static short FNDROPTAB = 15;
	/**
	 *  Description of the Field
	 */
	public final static short FNCREAIDX = 16;
	/**
	 *  Description of the Field
	 */
	public final static short FNDROPIDX = 17;
	/**
	 *  Description of the Field
	 */
	public final static short FNDGRANT = 18;
	/**
	 *  Description of the Field
	 */
	public final static short FNREVOKE = 19;
	/**
	 *  Description of the Field
	 */
	public final static short FNRENTAB = 20;
	/**
	 *  Description of the Field
	 */
	public final static short FNRENCOL = 21;
	/**
	 *  Description of the Field
	 */
	public final static short FNALTTAB = 29;
	/**
	 *  Description of the Field
	 */
	public final static short FNUPDSTAT = 30;
	/**
	 *  Description of the Field
	 */
	public final static short FNDBCLOSE = 31;
	/**
	 *  Description of the Field
	 */
	public final static short FNNWDEL = 32;
	/**
	 *  Description of the Field
	 */
	public final static short FNNWUPD = 33;
	/**
	 *  Description of the Field
	 */
	public final static short FNBEGWORK = 34;
	/**
	 *  Description of the Field
	 */
	public final static short FNCOMMIT = 35;
	/**
	 *  Description of the Field
	 */
	public final static short FNROLLBACK = 36;
	/**
	 *  Description of the Field
	 */
	public final static short FNSAVEPOINT = 37;
	/**
	 *  Description of the Field
	 */
	public final static short FNSTARTDB = 38;
	/**
	 *  Description of the Field
	 */
	public final static short FNFORWARD = 39;
	/**
	 *  Description of the Field
	 */
	public final static short FNCREVIEW = 40;
	/**
	 *  Description of the Field
	 */
	public final static short FNDROPVIEW = 41;
	/**
	 *  Description of the Field
	 */
	public final static short FNCREASYN = 43;
	/**
	 *  Description of the Field
	 */
	public final static short FNDROPSYN = 44;
	/**
	 *  Description of the Field
	 */
	public final static short FNCTEMP = 45;

	/**
	 *  Description of the Field
	 */
	public final static short Z_CTSQLVERS = -1100;
	/**
	 *  Description of the Field
	 */
	public final static short W_ROWSIZE = -1527;
	/**
	 *  Description of the Field
	 */
	public final static short LARGEBUF = 1024;
	/**
	 *  Description of the Field
	 */
	public final static short MEDIUMBUF = 512;
	/**
	 *  Description of the Field
	 */
	public final static short SMALLBUF = 256;

	/**
	 *  Description of the Field
	 */
	public final static short SQ_ID = 100;
	//64
	/**
	 *  Description of the Field
	 */
	public final static short SQ_PARSE = 101;
	//65
	/**
	 *  Description of the Field
	 */
	public final static short SQ_EXECUTE = 102;
	//66
	/**
	 *  Description of the Field
	 */
	public final static short SQ_OPEN = 103;
	//67
	/**
	 *  Description of the Field
	 */
	public final static short SQ_NFETCH = 104;
	//68
	/**
	 *  Description of the Field
	 */
	public final static short SQ_CLOSE = 105;
	//69
	/**
	 *  Description of the Field
	 */
	public final static short SQ_CURNAME = 106;
	//6A
	/**
	 *  Description of the Field
	 */
	public final static short SQ_HOST = 107;
	//6B
	/**
	 *  Description of the Field
	 */
	public final static short SQ_DESCRIBE = 108;
	//6C
	/**
	 *  Description of the Field
	 */
	public final static short SQ_RELEASE = 109;
	//6D
	/**
	 *  Description of the Field
	 */
	public final static short SQ_EOT = 110;
	//6E
	/**
	 *  Description of the Field
	 */
	public final static short SQ_FETCH = 111;
	//6F
	/**
	 *  Description of the Field
	 */
	public final static short SQ_TRAVEL = 112;
	//70
	/**
	 *  Description of the Field
	 */
	public final static short SQ_PUSHCTL = 113;
	//71
	/**
	 *  Description of the Field
	 */
	public final static short SQ_POPCTL = 114;
	//72
	/**
	 *  Description of the Field
	 */
	public final static short SQ_PUTENV = 115;
	//73
	/**
	 *  Description of the Field
	 */
	public final static short SQ_PUTSCROLL = 116;
	//74

	// SQL return codes
	/**
	 * The communication with the SQL server was interrupted.
	 */
	public final static int SQ_FAIL = -1;
	/**
	 *  Description of the Field
	 */
	public final static int SQ_DONE = 1;
	/**
	 *  Description of the Field
	 */
	public final static int SQ_TUPLE = 2;
	/**
	 *  Description of the Field
	 */
	public final static int SQ_ERR = 3;
	/**
	 *  Description of the Field
	 */
	public final static int SQ_LCKWARN = 4;

	/**
	 *  Description of the Field
	 */
	public final static short EMPTY = -1;

	// SQL error codes
	/**
	 *  Description of the Field
	 */
	public final static int SQLNOTFOUND = 100;
	/**
	 *  Description of the Field
	 */
	public final static short C_PREV = 64;
	/**
	 *  Description of the Field
	 */
	public final static short C_EOF = 4;
	/**
	 *  Description of the Field
	 */
	public final static short C_BOF = 32;
	/**
	 *  Description of the Field
	 */
	public final static short C_SQEOF = 128;
	/**
	 *  Description of the Field
	 */
	public final static short C_PREPD = 8;
	/**
	 *  Description of the Field
	 */
	public final static short C_OPEN = 2;

}
