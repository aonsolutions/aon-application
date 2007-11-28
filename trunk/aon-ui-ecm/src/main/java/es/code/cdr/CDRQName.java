/**
 * 
 */
package es.code.cdr;

import java.io.Serializable;

import org.apache.jackrabbit.name.QName;

/**
 * Qualified name. A qualified name is a combination of a namespace URI
 * and a local part. Instances of this class are used to internally represent
 * the names of AON content items and other objects within a content repository.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 06/07/2007
 * @see QName
 * 
 */
public final class CDRQName implements Cloneable, Comparable, Serializable {

    // reserved namespace for built-in primary AON node types
    public static final String NS_AON_PREFIX = "aon";
    public static final String NS_AON_URI = "http://www.code.es/aon";

    //--------------------------------< node type related item name constants >

    /**
     * aon:authDelete
     */
    public static final CDRQName AON_AUTHDELETE = new CDRQName(NS_AON_URI, "authDelete");

    /**
     * aon:authRead
     */
    public static final CDRQName AON_AUTHREAD = new CDRQName(NS_AON_URI, "authRead");

    /**
     * aon:authWrite
     */
    public static final CDRQName AON_AUTHWRITE = new CDRQName(NS_AON_URI, "authWrite");

    /**
     * aon:author
     */
    public static final CDRQName AON_AUTHOR = new CDRQName(NS_AON_URI, "author");

    /**
     * aon:entrydate
     */
    public static final CDRQName AON_ENTRYDATE = new CDRQName(NS_AON_URI, "entrydate");

    /**
     * aon:notification
     */
    public static final CDRQName AON_NOTIFICATION = new CDRQName(NS_AON_URI, "notification");

    /**
     * aon:size
     */
    public static final CDRQName AON_SIZE = new CDRQName(NS_AON_URI, "size");

    /**
     * aon:keywords
     */
    public static final CDRQName AON_KEYWORDS = new CDRQName(NS_AON_URI, "keywords");
    /**
     * aon:nowords
     */
    public static final CDRQName AON_NOWORDS = new CDRQName(NS_AON_URI, "nowords");
    /**
     * aon:category
     */
    public static final CDRQName AON_CATEGORY = new CDRQName(NS_AON_URI, "category");
    /**
     * aon:language
     */
    public static final CDRQName AON_LANGUAGE = new CDRQName(NS_AON_URI, "language");
    /**
     * aon:fileformat
     */
    public static final CDRQName AON_FILEFORMAT = new CDRQName(NS_AON_URI, "fileformat");
    /**
     * aon:content
     */
    public static final CDRQName AON_CONTENT = new CDRQName(NS_AON_URI, "content");

    //---------------------------------------------< node type name constants >

    /**
     * aon:cdr
     */
    public static final CDRQName AON_CDR = new CDRQName( NS_AON_URI, "cdr" );

    /**
     * aon:folder
     */
    public static final CDRQName AON_FOLDER = new CDRQName( NS_AON_URI, "folder" );

    /**
     * aon:document
     */
    public static final CDRQName AON_DOCUMENT = new CDRQName( NS_AON_URI, "document" );

    /**
     * aon:resource
     */
    public static final CDRQName AON_RESOURCE = new CDRQName( NS_AON_URI, "resource" );

    public static final CDRQName[] EMPTY_ARRAY = new CDRQName[0];

    /** The memorized hash code of this qualified name. */
    private transient int hash;

    /** The memorized string representation of this qualified name. */
    private transient String string;

    /** The internalized namespace URI of this qualified name. */
    private final String namespaceURI;

    /** The local part of this qualified name. */
    private final String localName;

    /**
     * Creates a new qualified name with the given namespace URI and
     * local part.
     * <p/>
     * Note that the format of the local part is not validated. The format
     * can be checked by calling {@link NameFormat#checkFormat(String)}.
     *
     * @param namespaceURI namespace uri
     * @param localName local part
     * @throws IllegalArgumentException if <code>localName</code> is invalid.
     */
    public CDRQName(String namespaceURI, String localName) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("invalid namespaceURI specified");
        }
        // an empty localName is valid though (e.g. the root node name)
        if (localName == null) {
            throw new IllegalArgumentException("invalid localName specified");
        }
        // internalize namespaceURI to improve performance of CDRQName comparisons.
        // Please note that we do *not* internalize localName since this could
        // blow perm space for large repositories
        this.namespaceURI = namespaceURI.intern();
        this.localName = localName;
        hash = 0;
    }

    //------------------------------------------------------< factory methods >

    /**
     * Returns a <code>CDRQName</code> holding the value of the specified
     * string. The string must be in the format returned by the
     * <code>CDRQName.toString()</code> method, i.e.
     * <p/>
     * <code><b>{</b>namespaceURI<b>}</b>localName</code>
     *
     * @param s a <code>String</code> containing the <code>CDRQName</code>
     *          representation to be parsed.
     * @return the <code>CDRQName</code> represented by the argument
     * @throws IllegalArgumentException if the specified string can not be parsed
     *                                  as a <code>CDRQName</code>.
     * @see #toString()
     */
    public static CDRQName valueOf(String s) throws IllegalArgumentException {
        if ("".equals(s) || s == null) {
            throw new IllegalArgumentException("invalid CDRQName literal");
        }

        if (s.charAt(0) == '{') {
            int i = s.indexOf('}');

            if (i == -1) {
                throw new IllegalArgumentException("invalid CDRQName literal");
            }

            if (i == s.length() - 1) {
                throw new IllegalArgumentException("invalid CDRQName literal");
            } else {
                return new CDRQName(s.substring(1, i), s.substring(i + 1));
            }
        } else {
            throw new IllegalArgumentException("invalid CDRQName literal");
        }
    }

    //-------------------------------------------------------< public methods >
    /**
     * Returns the local part of the qualified name.
     *
     * @return local name
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Returns the namespace URI of the qualified name.
     *
     * @return namespace URI
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    //---------------------------------------------------------------< Object >
    /**
     * Returns the string representation of this <code>CDRQName</code> in the
     * following format:
     * <p/>
     * <code><b>{</b>namespaceURI<b>}</b>localName</code>
     *
     * @return the string representation of this <code>CDRQName</code>.
     * @see #valueOf(String)
     * @see Object#toString()
     */
    public String toString() {
        // CDRQName is immutable, we can store the string representation
        if (string == null) {
            string = '{' + namespaceURI + '}' + localName;
        }
        return string;
    }

    /**
     * Compares two qualified names for equality. Returns <code>true</code>
     * if the given object is a qualified name and has the same namespace URI
     * and local part as this qualified name.
     *
     * @param obj the object to compare this qualified name with
     * @return <code>true</code> if the object is equal to this qualified name,
     *         <code>false</code> otherwise
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CDRQName) {
        	CDRQName other = (CDRQName) obj;
            // we can use == operator for namespaceURI since it is internalized
            return namespaceURI == other.namespaceURI
                    && localName.equals(other.localName);
        }
        return false;
    }

    /**
     * Returns the hash code of this qualified name. The hash code is
     * computed from the namespace URI and local part of the qualified
     * name and memorized for better performance.
     *
     * @return hash code
     * @see Object#hashCode()
     */
    public int hashCode() {
        // CDRQName is immutable, we can store the computed hash code value
        int h = hash;
        if (h == 0) {
            h = 17;
            h = 37 * h + namespaceURI.hashCode();
            h = 37 * h + localName.hashCode();
            hash = h;
        }
        return h;
    }

    //------------------------------------------------------------< Cloneable >
    /**
     * Creates a clone of this qualified name.
     * Overriden in order to make <code>clone()</code> public.
     *
     * @return a clone of this instance
     * @throws CloneNotSupportedException never thrown
     * @see Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        // CDRQName is immutable, no special handling required
        return super.clone();
    }

    //-----------------------------------------------------------< Comparable >
    /**
     * Compares two qualified names.
     *
     * @param o the object to compare this qualified name with
     * @return comparison result
     * @throws ClassCastException if the given object is not a qualified name
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        }

        CDRQName other = (CDRQName) o;
        // we can use == operator for namespaceURI since it is internalized
        if (namespaceURI == other.namespaceURI) {
            return localName.compareTo(other.localName);
        } else {
            return namespaceURI.compareTo(other.namespaceURI);
        }
    }

    //-------------------------------------------------< Serializable support >
    /**
     * Creates a new <code>CDRQName</code> instance using the proper constructor
     * during deserialization in order to make sure that internalized strings
     * are used where appropriate.
     */
    private Object readResolve() {
        return new CDRQName(namespaceURI, localName);
    }
}
