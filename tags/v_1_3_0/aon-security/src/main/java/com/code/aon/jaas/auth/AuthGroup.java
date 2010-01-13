package com.code.aon.jaas.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An implementation of Group that manages a
 * collection of Principal objects based on their hashCode() and equals()
 * methods. This class is not thread safe.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-feb-2005
 * @since 1.0
 *  
 */
public class AuthGroup extends AuthPrincipal implements Group {

	private static final long serialVersionUID = 1L;

	/**
     * Members in the group.
     */
    private HashMap<Principal, Principal> members;

    /**
     * Constructor.
     * 
     * @param groupName
     */
    public AuthGroup(String groupName) {
        super(groupName);
        members = new HashMap<Principal, Principal>();
    }

	/**
     * Adds the specified member to the group.
     * 
     * @param user
     *            the principal to add to this group.
     * @return true if the member was successfully added, false if the principal
     *         was already a member.
     * @see java.security.acl.Group#addMember(Principal)
     */
    public boolean addMember(Principal user) {
        boolean isMember = members.containsKey(user);
        if (!isMember) {
            members.put(user, user);
        }
        return !isMember;
    }

    /**
     * Returns true if the passed principal is a
     * member of the group. This method does a recursive search, so if a
     * principal belongs to a group which is a member of this group, true is
     * returned.
     * 
     * A special check is made to see if the member is an instance of
     * org.jboss.security.AnybodyPrincipal or org.jboss.security.NobodyPrincipal
     * since these classes do not hash to meaningful values.
     * 
     * @param member
     *            the principal whose membership is to be checked.
     * @return true if the principal is a member of this group, false otherwise.
     * @see java.security.acl.Group#isMember(Principal)
     */
    @SuppressWarnings("unchecked")
	public boolean isMember(Principal member) {
        // First see if there is a key with the member name
        boolean isMember = members.containsKey(member);
        if (!isMember) { // Check the AnybodyPrincipal & NobodyPrincipal special
            // cases
            isMember = (member instanceof AnybodyPrincipal);
            if (!isMember) {
                if (member instanceof NobodyPrincipal) {
                    return false;
                }
            }
        }
        if (!isMember) { // Check any Groups for membership
            Collection values = members.values();
            Iterator iter = values.iterator();
            while (!isMember && iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof Group) {
                    Group group = (Group) next;
                    isMember = group.isMember(member);
                }
            }
        }
        return isMember;
    }

    /**
     * Returns an enumeration of the members in the
     * group. The returned objects can be instances of either Principal or Group
     * (which is a subinterface of Principal).
     * 
     * @return an enumeration of the group members.
     * @see java.security.acl.Group#members()
     */
    public Enumeration<Principal> members() {
        return Collections.enumeration(members.values());
    }

    /**
     * Removes the specified member from the group.
     * 
     * @param user
     *            the principal to remove from this group.
     * @return true if the principal was removed, or false if the principal was
     *         not a member.
     * @see java.security.acl.Group#removeMember(Principal)
     */
    public boolean removeMember(Principal user) {
        Object prev = members.remove(user);
        return prev != null;
    }

    @SuppressWarnings("unchecked")
    @Override
	public String toString() {
        StringBuffer tmp = new StringBuffer(getName());
        tmp.append("(members:"); //$NON-NLS-1$
        Iterator iter = members.keySet().iterator();
        while (iter.hasNext()) {
            tmp.append(iter.next());
            tmp.append(',');
        }
        tmp.setCharAt(tmp.length() - 1, ')');
        return tmp.toString();
    }
}