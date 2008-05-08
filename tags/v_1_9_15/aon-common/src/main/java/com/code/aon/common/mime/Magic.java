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
package com.code.aon.common.mime;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This class is the primary class for jMimeMagic
 * 
 * @author $Author$
 * @version $Revision$
 */
public class Magic {
	private static Logger log = Logger.getLogger(Magic.class.getName());

	private static boolean initialized = false;

	private static MagicParser magicParser = null;

	private static HashMap hintMap = new HashMap();

	/**
	 * constructor
	 */
	public Magic() {
		log.fine("instantiated");
	}

	/**
	 * Add a hint to use the specified matcher for the given extension
	 * 
	 * @param extension
	 *            DOCUMENT ME!
	 * @param matcher
	 *            DOCUMENT ME!
	 */
	private static void addHint(String extension, MagicMatcher matcher) {
		if (hintMap.keySet().contains(extension)) {
			ArrayList a = (ArrayList) hintMap.get(extension);
			a.add(matcher);
		} else {
			ArrayList a = new ArrayList();
			a.add(matcher);
			hintMap.put(extension, a);
		}
	}

	/**
	 * create a parser and initialize it
	 * 
	 * @throws MagicParseException
	 *             DOCUMENT ME!
	 */
	public static synchronized void initialize() throws MagicParseException {
		log.fine("initialize()");

		if (!initialized) {
			log.fine("initializing");
			magicParser = new MagicParser();
			magicParser.initialize();

			// build hint map
			Iterator i = magicParser.getMatchers().iterator();

			while (i.hasNext()) {
				MagicMatcher matcher = (MagicMatcher) i.next();
				String ext = matcher.getMatch().getExtension();

				if ((ext != null) && !ext.trim().equals("")) {
					log.finest("adding hint mapping for extension '" + ext
							+ "'");

					addHint(ext, matcher);
				} else if (matcher.getMatch().getType().equals("detector")) {
					String[] exts = matcher.getDetectorExtensions();

					for (int j = 0; j < exts.length; j++) {
						log.finest("adding hint mapping for extension '"
								+ exts[j] + "'");

						addHint(exts[j], matcher);
					}
				}
			}

			initialized = true;
		}
	}

	/**
	 * return the parsed MagicMatch objects that were created from the magic.xml
	 * definitions
	 * 
	 * @return the parsed MagicMatch objects
	 * 
	 * @throws MagicParseException
	 *             DOCUMENT ME!
	 */
	public static Collection getMatchers() throws MagicParseException {
		log.fine("getMatchers()");

		if (!initialized) {
			initialize();
		}

		Iterator i = magicParser.getMatchers().iterator();
		ArrayList m = new ArrayList();

		while (i.hasNext()) {
			MagicMatcher matcher = (MagicMatcher) i.next();

			try {
				m.add(matcher.clone());
			} catch (CloneNotSupportedException e) {
				log.severe("failed to clone matchers");
				throw new MagicParseException("failed to clone matchers");
			}
		}

		return m;
	}

	/**
	 * get a match from a stream of data
	 * 
	 * @param data
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws MagicParseException
	 *             DOCUMENT ME!
	 * @throws MagicMatchNotFoundException
	 *             DOCUMENT ME!
	 * @throws MagicException
	 *             DOCUMENT ME!
	 */
	public static MagicMatch getMagicMatch(byte[] data)
			throws MagicParseException, MagicMatchNotFoundException,
			MagicException {
		return getMagicMatch(data, false);
	}

	/**
	 * get a match from a stream of data
	 * 
	 * @param data
	 *            DOCUMENT ME!
	 * @param onlyMimeMatch
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws MagicParseException
	 *             DOCUMENT ME!
	 * @throws MagicMatchNotFoundException
	 *             DOCUMENT ME!
	 * @throws MagicException
	 *             DOCUMENT ME!
	 */
	public static MagicMatch getMagicMatch(byte[] data, boolean onlyMimeMatch)
			throws MagicParseException, MagicMatchNotFoundException,
			MagicException {
		log.fine("getMagicMatch(byte[])");

		if (!initialized) {
			initialize();
		}

		Collection matchers = magicParser.getMatchers();
		log
				.fine("getMagicMatch(byte[]): have " + matchers.size()
						+ " matchers");

		MagicMatcher matcher = null;
		MagicMatch match = null;
		Iterator i = matchers.iterator();

		while (i.hasNext()) {
			matcher = (MagicMatcher) i.next();

			log.fine("getMagicMatch(byte[]): trying to match: "
					+ matcher.getMatch().getMimeType());

			try {
				if ((match = matcher.test(data, onlyMimeMatch)) != null) {
					log.fine("getMagicMatch(byte[]): matched "
							+ matcher.getMatch().getMimeType());

					return match;
				}
			} catch (IOException e) {
				log.severe("getMagicMatch(byte[]): " + e);
				throw new MagicException(e);
			} catch (UnsupportedTypeException e) {
				log.severe("getMagicMatch(byte[]): " + e);
				throw new MagicException(e);
			}
		}

		throw new MagicMatchNotFoundException();
	}

	/**
	 * get a match from a file
	 * 
	 * @param file
	 *            the file to match content in
	 * @param extensionHints
	 *            whether or not to use extension to optimize order of content
	 *            tests
	 * 
	 * @return the MagicMatch object representing a match in the file
	 * 
	 * @throws MagicParseException
	 *             DOCUMENT ME!
	 * @throws MagicMatchNotFoundException
	 *             DOCUMENT ME!
	 * @throws MagicException
	 *             DOCUMENT ME!
	 */
	public static MagicMatch getMagicMatch(File file, boolean extensionHints)
			throws MagicParseException, MagicMatchNotFoundException,
			MagicException {
		return getMagicMatch(file, extensionHints, false);
	}

	/**
	 * get a match from a file
	 * 
	 * @param file
	 *            the file to match content in
	 * @param extensionHints
	 *            whether or not to use extension to optimize order of content
	 *            tests
	 * @param onlyMimeMatch
	 *            only try to get mime type, no submatches are processed when
	 *            true
	 * 
	 * @return the MagicMatch object representing a match in the file
	 * 
	 * @throws MagicParseException
	 *             DOCUMENT ME!
	 * @throws MagicMatchNotFoundException
	 *             DOCUMENT ME!
	 * @throws MagicException
	 *             DOCUMENT ME!
	 */
	public static MagicMatch getMagicMatch(File file, boolean extensionHints,
			boolean onlyMimeMatch) throws MagicParseException,
			MagicMatchNotFoundException, MagicException {
		log.fine("getMagicMatch(File)");

		if (!initialized) {
			initialize();
		}

		long start = System.currentTimeMillis();

		MagicMatcher matcher = null;
		MagicMatch match = null;

		// check for extension hints
		ArrayList checked = new ArrayList();

		if (extensionHints) {
			log.fine("trying to use hints first");

			String name = file.getName();
			int pos = name.lastIndexOf('.');

			if (pos > -1) {
				String ext = name.substring(pos + 1, name.length());

				if ((ext != null) && !ext.equals("")) {
					log.finest("using extension '" + ext + "' for hinting");

					Collection c = (Collection) hintMap.get(ext);

					if (c != null) {
						Iterator i = c.iterator();

						while (i.hasNext()) {
							matcher = (MagicMatcher) i.next();

							log.fine("getMagicMatch(File): trying to match: "
									+ matcher.getMatch().getDescription());

							try {
								if ((match = matcher.test(file, onlyMimeMatch)) != null) {
									log.fine("getMagicMatch(File): matched "
											+ matcher.getMatch()
													.getDescription());

									long end = System.currentTimeMillis();
									log.finest("found match in '"
											+ (end - start) + "' milliseconds");

									return match;
								}
							} catch (UnsupportedTypeException e) {
								log.severe("getMagicMatch(File): " + e);
								throw new MagicException(e);
							} catch (IOException e) {
								log.severe("getMagicMatch(File): " + e);
								throw new MagicException(e);
							}

							// add to the already checked list
							checked.add(matcher);
						}
					}
				} else {
					log.fine("no file extension, ignoring hints");
				}
			} else {
				log.fine("no file extension, ignoring hints");
			}
		}

		Collection matchers = magicParser.getMatchers();
		log.fine("getMagicMatch(File): have " + matchers.size() + " matches");

		Iterator i = matchers.iterator();

		while (i.hasNext()) {
			matcher = (MagicMatcher) i.next();

			if (!checked.contains(matcher)) {
				log.fine("getMagicMatch(File): trying to match: "
						+ matcher.getMatch().getDescription());

				try {
					if ((match = matcher.test(file, onlyMimeMatch)) != null) {
						log.fine("getMagicMatch(File): matched "
								+ matcher.getMatch().getDescription());

						long end = System.currentTimeMillis();
						log.finest("found match in '" + (end - start)
								+ "' milliseconds");

						return match;
					}
				} catch (UnsupportedTypeException e) {
					log.severe("getMagicMatch(File): " + e);
					throw new MagicException(e);
				} catch (IOException e) {
					log.severe("getMagicMatch(File): " + e);
					throw new MagicException(e);
				}
			} else {
				log.fine("getMagicMatch(File): already checked, skipping: "
						+ matcher.getMatch().getDescription());
			}
		}

		throw new MagicMatchNotFoundException();
	}

	/**
	 * print the contents of a magic file
	 * 
	 * @param stream
	 *            DOCUMENT ME!
	 * 
	 * @throws MagicParseException
	 *             DOCUMENT ME!
	 */
	public static void printMagicFile(PrintStream stream)
			throws MagicParseException {
		if (!initialized) {
			initialize();
		}

		Collection matchers = Magic.getMatchers();
		log.fine("have " + matchers.size() + " matches");

		MagicMatcher matcher = null;
		Iterator i = matchers.iterator();

		while (i.hasNext()) {
			matcher = (MagicMatcher) i.next();
			log.fine("printing");
			printMagicMatcher(stream, matcher, "");
		}
	}

	/**
	 * print a magic match
	 * 
	 * @param stream
	 *            DOCUMENT ME!
	 * @param matcher
	 *            DOCUMENT ME!
	 * @param spacing
	 *            DOCUMENT ME!
	 */
	private static void printMagicMatcher(PrintStream stream,
			MagicMatcher matcher, String spacing) {
		stream
				.println(spacing + "name: "
						+ matcher.getMatch().getDescription());
		stream.println(spacing + "children: ");

		Collection matchers = matcher.getSubMatchers();
		Iterator i = matchers.iterator();

		while (i.hasNext()) {
			printMagicMatcher(stream, (MagicMatcher) i.next(), spacing + "  ");
		}
	}

	/**
	 * print a magic match
	 * 
	 * @param stream
	 *            DOCUMENT ME!
	 * @param match
	 *            DOCUMENT ME!
	 * @param spacing
	 *            DOCUMENT ME!
	 */
	public static void printMagicMatch(PrintStream stream, MagicMatch match,
			String spacing) {
		stream.println(spacing + "=============================");
		stream.println(spacing + "mime type: " + match.getMimeType());
		stream.println(spacing + "description: " + match.getDescription());
		stream.println(spacing + "extension: " + match.getExtension());
		stream
				.println(spacing + "test: "
						+ new String(match.getTest().array()));
		stream.println(spacing + "bitmask: " + match.getBitmask());
		stream.println(spacing + "offset: " + match.getOffset());
		stream.println(spacing + "length: " + match.getLength());
		stream.println(spacing + "type: " + match.getType());
		stream.println(spacing + "comparator: " + match.getComparator());
		stream.println(spacing + "=============================");

		Collection submatches = match.getSubMatches();
		Iterator i = submatches.iterator();

		while (i.hasNext()) {
			printMagicMatch(stream, (MagicMatch) i.next(), spacing + "    ");
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 *            DOCUMENT ME!
	 */
	public static void main(String[] args) {
		// Magic magic = new Magic();
		try {
			// Magic.initialize();
			File f = new File(args[0]);

			if (f.exists()) {
				MagicMatch match = Magic.getMagicMatch(f, true, false);

				System.out.println("filename: " + args[0]);
				printMagicMatch(System.out, match, "");

				// Collection submatches = match.getSubMatches();
				// if (match == null) {
				// System.out.println(args[0]+": unknown");
				// } else {
				// System.out.println("=============================");
				// System.out.println("filename: "+args[0]);
				// System.out.println("mime type: "+match.getMimeType());
				// System.out.println("description: "+match.getDescription());
				// System.out.println("extension: "+match.getExtension());
				// System.out.println("test: "+new
				// String(match.getTest().array()));
				// System.out.println("bitmask: "+match.getBitmask());
				// System.out.println("offset: "+match.getOffset());
				// System.out.println("length: "+match.getLength());
				// System.out.println("type: "+match.getType());
				// System.out.println("comparator: "+match.getComparator());
				// System.out.println("=============================");
				//
				// Iterator i = submatches.iterator();
				// while (i.hasNext()) {
				// System.out.println("== SUBMATCH =================");
				// MagicMatch m = (MagicMatch)i.next();
				// System.out.println(m.print());
				// System.out.println("=============================");
				// }
				// }

				// FileInputStream fis = new FileInputStream(f);
				// ByteBuffer buffer = ByteBuffer.allocate((int)f.length());
				// byte []buf = new byte[2048];
				// int size = 0;
				// while ((size = fis.read(buf, 0, 2048)) > 0) {
				// buffer.put(buf, 0, size);
				// }
				// byte []tmp = buffer.array();
				// match = parser.getMagicMatch(tmp);
				// if (match == null) {
				// System.out.println(args[0]+": unknown");
				// } else {
				// System.out.println(args[0]+": "+match.getDescription());
				// System.out.println(match.getMimeType());
				// }
			} else {
				System.err.println("file '" + f.getCanonicalPath()
						+ "' not found");
			}
		} catch (MagicMatchNotFoundException e) {
			System.out.println("no match found");
		} catch (Exception e) {
			System.err.println("error: " + e);
			e.printStackTrace(System.err);
		}
	}
}
