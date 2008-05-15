/**
 * 
 */
package es.code.cdr.beans;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/12/2007
 *
 */
public class Permission implements Serializable {

	private static final long serialVersionUID = 3697630070716768802L;

	/** Role/Profile name. */
	private Object name;
	/** Enabled permissions. */
	private String[] permissions;
	/** Enabled users*/
	private String users;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param permissions
	 */
	public Permission(Object name, String permissions, String users) {
		this.name = name;
		StringTokenizer st = new StringTokenizer( permissions, "," );
		this.permissions = new String[ st.countTokens() ];
		int index = 0;
		while ( st.hasMoreTokens() ) {
			this.permissions[ index++ ] = st.nextToken().trim();
		}
		this.users = users;
	}

	/**
	 * @return the name
	 */
	public Object getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(Object name) {
		this.name = name;
	}

	/**
	 * @return the permissions
	 */
	public String getPermissions2String() {
		return convertToString( permissions );
	}

	/**
	 * @return the permissions
	 */
	public String[] getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the users
	 */
	public String getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(String users) {
		this.users = users;
	}

    /**
     * Converts string arrays for displays.
     *
     * @param stringArray string array to convert
     * @return a string concatenating the elements of the string array
     */
    private static String convertToString(String[] stringArray) {
        if (stringArray == null) {
            return "";
        }
        StringBuffer itemBuffer = new StringBuffer();
        for (int i = 0, max = stringArray.length; i < max; i++) {
            if (i > 0) {
                itemBuffer.append(",");
            }
            itemBuffer.append(stringArray[i]);
        }
        return itemBuffer.toString();
    }

}
