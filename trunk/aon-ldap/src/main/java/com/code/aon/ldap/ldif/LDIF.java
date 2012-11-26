/* -*- Mode: C++; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1999 Netscape Communications Corporation. All
 * Rights Reserved.
 *
 * Contributor(s): 
 */
package com.code.aon.ldap.ldif;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.Name;

import com.code.aon.ldap.Entry;
import com.code.aon.ldap.NameResolver;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * LDAP Data Interchange Format (LDIF) is a file format used to
 * import and export directory data from an LDAP server and to
 * describe a set of changes to be applied to data in a directory.
 * This format is described in the Internet draft
 * <A HREF="ftp://ftp.ietf.org/internet-drafts/draft-good-ldap-ldif-00.txt"
 * TARGET="_blank">The LDAP Data Interchange Format (LDIF) -
 * Technical Specification</A>.
 * <P>
 *
 * This class implements an LDIF file parser.  You can construct
 * an object of this class to parse data in LDIF format and
 * manipulate the data as individual <CODE>LDIFRecord</CODE> objects.
 * <P>
 *
 * @version 1.0
 * @see org.ietf.ldap.util.LDIFRecord
 */
public class LDIF {

    /**
     * Internal constants
     */
    private final static char COMMENT = '#';

    private int m_version = 1;
    private boolean m_done = false;
    private LineReader m_reader = null;
    private int m_currLineNum;
    private int m_continuationLength;
    
    /**
     * Constructs an <CODE>LDIF</CODE> object to parse the
     * LDIF data read from a specified file.
     * @param file the name of the LDIF file to parse
     * @exception IOException An I/O error has occurred.
     */
    public LDIF(File file) throws IOException {
        FileInputStream fs = new FileInputStream(file);
        DataInputStream ds = new DataInputStream(fs);
        BufferedReader d = new BufferedReader(new InputStreamReader(ds, "UTF8"));
        m_reader = new LineReader(d);
    }

    /**
     * Constructs an <CODE>LDIF</CODE> object to parse the
     * LDIF data read from an input stream.
     * @param dstThe input stream providing the LDIF data
     * @exception IOException An I/O error has occurred.
     */
    public LDIF(InputStream ds) throws IOException {
        BufferedReader d = new BufferedReader(new InputStreamReader(ds, "UTF8"));
        m_reader = new LineReader(d);
    }

    /**
     * Returns the next record in the LDIF data. You can call this
     * method repeatedly to iterate through all records in the LDIF data.
     * <P>
     *
     * @return the next record as an <CODE>LDIFRecord</CODE>
     * object or null if there are no more records.
     * @exception IOException An I/O error has occurred.
     * @see org.ietf.ldap.util.LDIFRecord
     */
    public Entry nextRecord() throws IOException {
        if ( m_done )
            return null;
        else
            return parse_ldif_record( m_reader );
    }

    private String skipBlankLines(LineReader d) throws IOException {
        String line = null;

        // Skip past any blank lines
        while( ((line = d.readLine()) != null) && (line.length() < 1) ) {
        }
        return line;
    }
    
    /**
     * Parses ldif content. The list of attributes is
     * terminated by \r\n or '-'. This function is
     * also used to parse the attributes in modifications.
     * @param ds data input stream
     */
    private Entry parse_ldif_record(LineReader d)
          throws IOException {
        String line = skipBlankLines(d);
        if (line == null) {
            return null;
        }

        if (line.startsWith("version:")) {
            m_version = Integer.parseInt(
                line.substring("version:".length()).trim() );
            if ( m_version != 1 ) {
                throwLDIFException( "Unexpected " + line );
            }
            // Do the next record
            line = skipBlankLines(d);
            if (line == null) {
                return null;
            }
        }

        if (!line.startsWith("dn:")) {
            throwLDIFException("expecting dn:");
        }
        String dn = line.substring(3).trim();
        if (dn.startsWith(":") && (dn.length() > 1)) {
            String substr = dn.substring(1).trim();
            dn = new String(getDecodedBytes(substr), "UTF8");
        }

        Name name = NameResolver.getName(dn);
        Entry entry = new Entry(name);
        parse_ldif_content(d, entry);
        return entry;
    }

    /**
     * Parses ldif content. The list of attributes is
     * terminated by \r\n or '-'. This function is
     * also used to parse the attributes in modifications.
     * @param ds data input stream
     */
    private void parse_ldif_content(LineReader d, Entry entry)
          throws IOException {
        String line = d.readLine();
        if ((line == null) || (line.length() < 1) || (line.equals("-"))) {
            return;
        }

        /* Read lines until we're past the record */
        while( true ) {
            /* An attribute */
            int len = line.length();
            if ( len < 1 ) {
                break;
            }
            int idx = line.indexOf(':');
            /* Must have a colon */
            if (idx == -1)
                throwLDIFException("no ':' found");
            /* attribute type */
            String newtype = line.substring(0,idx);
            Object val = "";
            /* Could be :: for binary */
            idx++;
            if ( len > idx ) {
                if ( line.charAt(idx) == ':' ) {
                    idx++;
                    String substr = line.substring(idx).trim();
                    val = getDecodedBytes(substr);
                } else if (line.charAt(idx) == '<') {
                    try {
                        URL url =
                            new URL(line.substring(idx+1).trim());
                        String filename = url.getFile();
                        val = getFileContent(filename);
                    } catch (MalformedURLException ex) {
                        throwLDIFException(
                            ex +
                            ": cannot construct url "+
                            line.substring(idx+1).trim());
                    }
                } else {
                    val = line.substring(idx).trim();
                }
            }
            entry.put(newtype, val );

            line = d.readLine();
            if (line == null || (line.length() < 1) || (line.equals("-"))) {
                break;
            }
        }
    }

    private byte[] getDecodedBytes(String line) {
        return Base64.decode(line);
    }

    private byte[] getFileContent(String url) throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(url, "|");
        String filename = url;
        int num = tokenizer.countTokens();
        if (num == 2) {
            String token = (String)tokenizer.nextElement();
            int index = token.lastIndexOf("/");
            String drive = token.substring(index+1);
            token = (String)tokenizer.nextElement();
            token = token.replace('/', '\\');
            filename = drive+":"+token;
        }

        File file = new File(filename);
        byte[] b = new byte[(int)file.length()];
        FileInputStream fi = new FileInputStream(filename);
        fi.read(b);
        return b;
    }

    /**
     * Returns true if all the bytes in the given array are valid for output as a
     * String according to the LDIF specification. If not, the array should
     * output base64-encoded.
     * @return <code>true</code> if all the bytes in the given array are valid for 
     * output as a String according to the LDIF specification; otherwise, 
     * <code>false</code>.
     */
    public static boolean isPrintable(byte[] b) {
        for( int i = b.length - 1; i >= 0; i-- ) {
            if ( (b[i] < ' ') || (b[i] > 127) ) {
                if ( b[i] != '\t' )
                    return false;
            }
        }
        return true;
    }

    /**
     * Outputs the String in LDIF line-continuation format. No line will be longer
     * than the given max. A continuation line starts with a single blank space.
     * @param pw the printer writer
     * @param value the given string being printed out
     * @param max the maximum characters allowed in the line
     */
    public static void breakString( PrintWriter pw, String value, int max) {
        int leftToGo = value.length();
        int written = 0;
        int maxChars = max;
        /* Limit to 77 characters per line */
        while( leftToGo > 0 ) {
            int toWrite = Math.min( maxChars, leftToGo );
            String s = value.substring( written, written+toWrite);
            if ( written != 0 ) {
                pw.print( " " + s );
            } else {
                pw.print( s );
                maxChars -= 1;
            }
            written += toWrite;
            leftToGo -= toWrite;
            /* Don't use pw.println, because it outputs an extra CR
               in Win32 */
            pw.print( '\n' );
        }
    }

    /**
     * Gets the version of LDIF used in the data.
     * @return version of LDIF used in the data.
     */
    public int getVersion() {
        return m_version;
    }

    /**
     * Throws a LDIF file exception including the current line number.
     * @param msg Error message
     */
    protected void throwLDIFException(String msg)throws IOException {
        throw new IOException ("line " +
            (m_currLineNum-m_continuationLength) + ": " + msg);
    }      
    
    /* Concatenate continuation lines, if present */
    class LineReader {
        LineReader( BufferedReader d ) {
            _d = d;
        }
        /**
         * Reads a non-comment line.
         * @return a string or null.
         */
        String readLine() throws IOException {
            String line = null;
            String result = null;
            int readCnt = 0, continuationLength = 0;
            do {
                /* Leftover line from last time? */
                if ( _next != null ) {
                    line = _next;
                    _next = null;
                } else {
                    line = _d.readLine();
                }
                if (line != null) {
                    readCnt++;
                    /* Empty line means end of record */
                    if( line.length() < 1 ) {
                        if ( result == null )
                            result = line;
                        else {
                            _next = line;
                            break;
                        }
                    } else if( line.charAt(0) == COMMENT ) {
                        /* Ignore comment lines */
                    } else if( line.charAt(0) != ' ' ) {
                        /* Not a continuation line */
                        if( result == null ) {
                            result = line;
                        } else {
                            _next = line;
                            break;
                        }
                    } else {
                        /* Continuation line */
                        if ( result == null ) {
                            m_currLineNum += readCnt;
                            throwLDIFException("continuation out of nowhere");
                        }
                        result += line.substring(1);
                        continuationLength++;
                    }
                } else {
                    /* End of file */
                    break;
                }
            } while ( true );

            m_done = ( line == null );
            
            m_currLineNum += readCnt;
            if (_next != null) {
                // read one line ahead
                m_currLineNum--;
            }            
            m_continuationLength = continuationLength;
            
            return result;
        }
        private BufferedReader _d;
        String _next = null;
    }

    @SuppressWarnings("unchecked")
	public Entry[] getOrderedList( int depth ) throws IOException {
    	List<Entry> levels[] = new LinkedList[depth];
    	int count = 0;
    	for( Entry entry : getList() ) {
			int size = entry.getDN().size();
			if ( levels[size] == null ) {
				levels[size] = new LinkedList<Entry>();
			}
			levels[size].add(entry);
			count++;    		
    	}
    	Entry[] list = new Entry[count];
    	int i = 0;
    	Comparator<Entry> entryComparator = new Comparator<Entry>() {
			@Override
			public int compare(Entry o1, Entry o2) {
				return o1.getDN().compareTo(o2.getDN());
			}
		};
    	for( List<Entry> entries : levels ) {
    		if ( entries != null ) {
    			Collections.sort(entries, entryComparator);
    			for( Entry value : entries ) {
    				list[i++] = value;
    			}
    		}
    	}
    	return list;
    }
    
	public List<Entry> getList() throws IOException {
    	List<Entry> levels = new LinkedList<Entry>();
		Entry entry = nextRecord();
		while ( entry != null ) {
			levels.add( entry );
			entry = nextRecord();
		}
    	return levels;
    }
    
    /**
     * Test driver - just reads and parses an LDIF file, printing
     * each record as interpreted
     *
     * @param args name of the LDIF file to parse
     */
    public static void main( String[] args ) {
        if ( args.length != 1 ) {
            System.out.println( "Usage: java LDIF <FILENAME>" );
            System.exit( 1 );
        }
        LDIF ldif = null;
        try {
            ldif = new LDIF( new File(args[0]) );
        } catch (Exception e) {
            System.err.println("Failed to read LDIF file " + args[0] +
                               ", " + e.toString());
            System.exit(1);
        }
        try {
            for( Entry rec = ldif.nextRecord();
                 rec != null; rec = ldif.nextRecord() ) {
                System.out.println( rec.toString() + '\n' );
            }
        } catch ( IOException ex ) {
            System.out.println( ex );
            System.exit( 1 );
        }
        System.exit( 0 );
    }
}
