/*
jMimeMagic(TM) is a Java library for determining the content type of files or
streams.

Copyright (C) 2004 David Castro

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information, please email arimus@users.sourceforge.net
*/
package com.code.aon.common.mime.detectors;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.code.aon.common.mime.MagicDetector;


/**
 * DOCUMENT ME!
 *
 * @author $Author$
 * @version $Revision$
  */
public class TextFileDetector implements MagicDetector
{
    private static Logger log = Logger.getLogger(TextFileDetector.class.getName());

    /**
     * Creates a new TextFileDetector object.
     */
    public TextFileDetector()
    {
        super();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDisplayName()
    {
        return "Text File Detector";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion()
    {
        return "0.1";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledExtensions()
    {
        return new String[] { "txt", "text" };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledTypes()
    {
        return new String[] { "text/plain" };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return "textfiledetector";
    }

    /**
     * DOCUMENT ME!
     *
     * @param data DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     * @param bitmask DOCUMENT ME!
     * @param comparator DOCUMENT ME!
     * @param mimeType DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] process(byte[] data, int offset, int length, long bitmask, char comparator,
        String mimeType, Map params)
    {
        log.fine("processing stream data");

        Pattern pattern = Pattern.compile( "[^\\p{ASCII}\\p{Space}]" );
        
        try {
            String s = new String(data, "UTF-8");

            if (!pattern.matcher(s).matches()) {
                return new String[] { "text/plain" };
            }

            return null;
        } catch (UnsupportedEncodingException e) {
            log.severe("TextFileDetector: failed to process data");

            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     * @param bitmask DOCUMENT ME!
     * @param comparator DOCUMENT ME!
     * @param mimeType DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] process(File file, int offset, int length, long bitmask, char comparator,
        String mimeType, Map params)
    {
        log.fine("processing file data");

        return new String[] {  };
    }
}
