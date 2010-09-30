package com.code.aon.report;

import java.io.Serializable;

/**
 * Enumeration for the diferent types of output format.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public class OutputFormat implements Serializable, Comparable<OutputFormat> {

	private static final long serialVersionUID = -474109916036902842L;

	/**
	 * Array to store all types.
	 */
	private static OutputFormat[] ALL = new OutputFormat[8];

	/**
	 * PDF format.
	 */
	public static final OutputFormat PDF = new OutputFormat(0, "PDF",
			"application/pdf");

	/**
	 * HTML format.
	 */
	public static final OutputFormat HTML = new OutputFormat(1, "HTML",
			"text/html");

	/**
	 * XML format.
	 */
	public static final OutputFormat XML = new OutputFormat(2, "XML",
			"text/xml");

	/**
	 * MS Excel format.
	 */
	public static final OutputFormat XLS = new OutputFormat(3, "MS Excel",
			"application/vnd.ms-excel");

	/**
	 * CSV format.
	 */
	public static final OutputFormat CSV = new OutputFormat(4, "CSV",
			"text/plain");

	/**
	 * RTF format.
	 */
	public static final OutputFormat RTF = new OutputFormat(5, "RTF",
			"application/rtf");

	/**
	 * TEXT format.
	 */
	public static final OutputFormat TXT = new OutputFormat(6, "TXT",
			"text/plain");

	/**
	 * TEXT format.
	 */
	// MIME TYPE obtenido de --> http://support.microsoft.com/kb/936496/es
	public static final OutputFormat DOCX = new OutputFormat(6, "DOCX",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document");

	/**
	 * Index of the element in the output formats array.
	 */
	private int index;

	/**
	 * Description of the output format.
	 */
	private String description;

	/**
	 * MIME type of the generated stream.
	 */
	private String mimeType;

	/**
	 * Constructor for OutputFormat
	 * 
	 * @param index
	 *            Index of the element.
	 * @param description
	 *            Description of the element.
	 * @param mimeType
	 *            MIME type of the element.
	 */
	private OutputFormat(int index, String description, String mimeType) {
		this.index = index;
		this.description = description;
		this.mimeType = mimeType;
		ALL[this.index] = this;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 *            The reference object with which to compare.
	 * @return <code>True</code> if this object is the same as the obj
	 *         argument; <code>False</code> otherwise.
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof OutputFormat) {
			OutputFormat outputFormat = (OutputFormat) obj;
			return outputFormat.index == index;
		}
		return false;
	}

	/**
	 * Returns a string representation of the object. In this case the
	 * description of this OutputFormat.
	 * 
	 * @return A string representation of the object.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return description;
	}

	/**
	 * Compares this object with the argument for order. Returns a negative
	 * integer, zero, or a positive integer as the first argument is less than,
	 * equal to, or greater than this object.
	 * 
	 * @param outputFormat
	 *            The object to be compared.
	 * 
	 * @return A negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second.
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(OutputFormat outputFormat) {
		if (this == outputFormat) {
			return 0;
		}
		return (this.index < outputFormat.index) ? (-1) : (+1);
	}

	/**
	 * Returns the <code>OutputputFormat</code> allocated in this position.
	 * 
	 * @param i
	 *            The index of the element.
	 * 
	 * @return The <code>OutputputFormat</code> allocated in this position.
	 * @throws ArrayIndexOutOfBoundsException
	 *             In the case of parameter <code>i</code> were out of bounds.
	 */
	public static OutputFormat get(int i) {
		if (i < 0 || i >= ALL.length) {
			throw new ArrayIndexOutOfBoundsException(" Looking for item " + i
					+ " on a " + ALL.length + "-length array.");
		}
		return ALL[i];
	}

	/**
	 * Returns the <code>OutputputFormat</code> that correspond to this
	 * description.
	 * 
	 * @param description
	 *            The description of the element.
	 * 
	 * @return The <code>OutputputFormat</code>.
	 */
	public static OutputFormat get(String description) {
		for (int i = 0; i < ALL.length; i++) {
			if (ALL[i].description.equals(description)) {
				return ALL[i];
			}
		}
		return null;
	}

	/**
	 * Returns the index of this element.
	 * 
	 * @return The index of this element.
	 */
	public int getValue() {
		return index;
	}

	/**
	 * Returns a hash code value for the object. This method is supported for
	 * the benefit of hashtables such as those provided by
	 * <code>java.util.Hashtable</code>.
	 * 
	 * @return A hash code value for this object.
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return index;
	}

	/**
	 * Returns the MIME type of this <code>OutputFormat</code>.
	 * 
	 * @return The MIME type.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns the description of this element.
	 * 
	 * @return The description of this element.
	 */
	public String getType() {
		return description;
	}
	
}