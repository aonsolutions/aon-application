/**
 * 
 */
package es.code.ecm;

import java.io.Serializable;

import org.apache.jackrabbit.name.NameFormat;
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
public final class ECMQName implements Cloneable, Comparable, Serializable {

    // reserved namespace for built-in primary AON node types
    public static final String AON_GROUP_SUFFIX = "group";
    public static final String AON_USER_SUFFIX = "user";
    public static final String AON_FOLDER_SUFFIX = "folder";
    public static final String AON_CATEGORY_SUFFIX = "category";
    public static final String NS_AON_PREFIX = "aon";
    public static final String NS_AON_URI = "http://www.code.es/aon";

    //--------------------------------< node type related item name constants >

    /**
     * aon:read
     */
    public static final ECMQName AON_READ = new ECMQName(NS_AON_URI, "read");
    /**
     * aon:write
     */
    public static final ECMQName AON_WRITE = new ECMQName(NS_AON_URI, "write");
    /**
     * aon:delete
     */
    public static final ECMQName AON_DELETE = new ECMQName(NS_AON_URI, "delete");
    /**
     * aon:level
     */
    public static final ECMQName AON_LEVEL = new ECMQName(NS_AON_URI, "level");
    /**
     * aon:group
     */
    public static final ECMQName AON_GROUP = new ECMQName(NS_AON_URI, AON_GROUP_SUFFIX);
    /**
     * aon:user
     */
    public static final ECMQName AON_USER = new ECMQName(NS_AON_URI, AON_USER_SUFFIX);
    /**
     * aon:domain
     */
    public static final ECMQName AON_DOMAIN = new ECMQName(NS_AON_URI, "domain");
    /**
     * aon:login
     */
    public static final ECMQName AON_LOGIN = new ECMQName(NS_AON_URI, "login");
    /**
     * aon:name
     */
    public static final ECMQName AON_NAME = new ECMQName(NS_AON_URI, "name");
    /**
     * aon:member
     */
    public static final ECMQName AON_MEMBER = new ECMQName(NS_AON_URI, "member");
    /**
     * aon:roles
     */
    public static final ECMQName AON_ROLES = new ECMQName(NS_AON_URI, "roles");

    /**
     * aon:category
     */
    public static final ECMQName AON_CATEGORY = new ECMQName(NS_AON_URI, AON_CATEGORY_SUFFIX);

    /**
     * aon:categoryname
     */
    public static final ECMQName AON_CATEGORYNAME = new ECMQName(NS_AON_URI, "categoryname");

    /**
     * aon:title
     */
    public static final ECMQName AON_TITLE = new ECMQName(NS_AON_URI, "title");

    /**
     * aon:notification
     */
    public static final ECMQName AON_NOTIFICATION = new ECMQName(NS_AON_URI, "notification");
    /**
     * aon:size
     */
    public static final ECMQName AON_SIZE = new ECMQName(NS_AON_URI, "size");
    /**
     * aon:keywords
     */
    public static final ECMQName AON_KEYWORDS = new ECMQName(NS_AON_URI, "keywords");
    /**
     * aon:nowords
     */
    public static final ECMQName AON_NOWORDS = new ECMQName(NS_AON_URI, "nowords");
    /**
     * aon:language
     */
    public static final ECMQName AON_LANGUAGE = new ECMQName(NS_AON_URI, "language");
    /**
     * aon:fileformat
     */
    public static final ECMQName AON_FILEFORMAT = new ECMQName(NS_AON_URI, "fileformat");
    /**
     * aon:content
     */
    public static final ECMQName AON_CONTENT = new ECMQName(NS_AON_URI, "content");

    //---------------------------------------------< node type name constants >

    /**
     * aon:cdr
     */
    public static final ECMQName AON_CDR = new ECMQName( NS_AON_URI, "cdr" );

    /**
     * aon:folder
     */
    public static final ECMQName AON_FOLDER = new ECMQName( NS_AON_URI, AON_FOLDER_SUFFIX );

    /**
     * aon:document
     */
    public static final ECMQName AON_DOCUMENT = new ECMQName( NS_AON_URI, "document" );

    /**
     * aon:resource
     */
    public static final ECMQName AON_RESOURCE = new ECMQName( NS_AON_URI, "resource" );

    public static final ECMQName[] EMPTY_ARRAY = new ECMQName[0];

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
    public ECMQName(String namespaceURI, String localName) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("invalid namespaceURI specified");
        }
        // an empty localName is valid though (e.g. the root node name)
        if (localName == null) {
            throw new IllegalArgumentException("invalid localName specified");
        }
        // internalize namespaceURI to improve performance of ECMQName comparisons.
        // Please note that we do *not* internalize localName since this could
        // blow perm space for large repositories
        this.namespaceURI = namespaceURI.intern();
        this.localName = localName;
        hash = 0;
    }

    //------------------------------------------------------< factory methods >

    /**
     * Returns a <code>ECMQName</code> holding the value of the specified
     * string. The string must be in the format returned by the
     * <code>ECMQName.toString()</code> method, i.e.
     * <p/>
     * <code><b>{</b>namespaceURI<b>}</b>localName</code>
     *
     * @param s a <code>String</code> containing the <code>ECMQName</code>
     *          representation to be parsed.
     * @return the <code>ECMQName</code> represented by the argument
     * @throws IllegalArgumentException if the specified string can not be parsed
     *                                  as a <code>ECMQName</code>.
     * @see #toString()
     */
    public static ECMQName valueOf(String s) throws IllegalArgumentException {
        if ("".equals(s) || s == null) {
            throw new IllegalArgumentException("invalid ECMQName literal");
        }

        if (s.charAt(0) == '{') {
            int i = s.indexOf('}');

            if (i == -1) {
                throw new IllegalArgumentException("invalid ECMQName literal");
            }

            if (i == s.length() - 1) {
                throw new IllegalArgumentException("invalid ECMQName literal");
            } else {
                return new ECMQName(s.substring(1, i), s.substring(i + 1));
            }
        } else {
            throw new IllegalArgumentException("invalid ECMQName literal");
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
     * Returns the string representation of this <code>ECMQName</code> in the
     * following format:
     * <p/>
     * <code><b>{</b>namespaceURI<b>}</b>localName</code>
     *
     * @return the string representation of this <code>ECMQName</code>.
     * @see #valueOf(String)
     * @see Object#toString()
     */
    public String toString() {
        // ECMQName is immutable, we can store the string representation
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
        if (obj instanceof ECMQName) {
        	ECMQName other = (ECMQName) obj;
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
        // ECMQName is immutable, we can store the computed hash code value
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
        // ECMQName is immutable, no special handling required
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

        ECMQName other = (ECMQName) o;
        // we can use == operator for namespaceURI since it is internalized
        if (namespaceURI == other.namespaceURI) {
            return localName.compareTo(other.localName);
        } else {
            return namespaceURI.compareTo(other.namespaceURI);
        }
    }

    //-------------------------------------------------< Serializable support >
    /**
     * Creates a new <code>ECMQName</code> instance using the proper constructor
     * during deserialization in order to make sure that internalized strings
     * are used where appropriate.
     */
    private Object readResolve() {
        return new ECMQName(namespaceURI, localName);
    }
}
