package com.code.aon.jaas.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 * An implementation of Group that allows that acts
 * as a stack of Groups with a single Group member active at any time. When one
 * adds a Group to a NestableGroup the Group is pushed onto the active Group
 * stack and any of the Group methods operate as though the NestableGroup
 * contains only the Group. When removing the Group that corresponds to the
 * active Group, the active Group is popped from the stack and the new active
 * Group is set to the new top of the stack.
 * 
 * The typical usage of this class is when doing a JAAS LoginContext login to
 * runAs a new Principal with a new set of roles that should be added without
 * destroying the current identity and roles.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 *  
 */
public class NestableGroup extends AuthPrincipal implements Group {

	private static final long serialVersionUID = 1L;

	/**
     * The stack of the Groups. Elements are
     * pushed/poped by inserting/removing element 0.
     */
    private LinkedList<Principal> rolesStack;

    /**
     * Creates new NestableGroup with the given name
     * 
     * @param name
     *            Name of the group.
     */
    public NestableGroup(String name) {
        super(name);
        rolesStack = new LinkedList<Principal>();
    }

    /**
     * Returns an enumeration that contains the
     * single active Principal.
     * 
     * @return An enumeration of the single active Principal.
     * @see java.security.acl.Group#members()
     */
    public Enumeration<Principal> members() {
        return new IndexEnumeration();
    }

    /**
     * Removes the first occurence of user from the
     * Principal stack.
     * 
     * @param user
     *            The principal to remove from this group.
     * @return True if the principal was removed, or false if the principal was
     *         not a member.
     * @see java.security.acl.Group#removeMember(Principal)
     */
    public boolean removeMember(Principal user) {
        return rolesStack.remove(user);
    }

    /**
     * Pushes the group onto the Group stack and
     * makes it the active Group.
     * 
     * @param group
     *            The instance of Group that contains the roles to set as the
     *            active Group.
     * @return true always.
     * @throws IllegalArgumentException
     *             Thrown if group is not an instance of Group.
     * @see java.security.acl.Group#addMember(Principal)
     */
    public boolean addMember(Principal group) throws IllegalArgumentException {
        if (!(group instanceof Group)) {
            throw new IllegalArgumentException(
                    "The addMember argument must be a Group"); //$NON-NLS-1$
        }
        rolesStack.addFirst(group);
        return true;
    }

    /**
     * Returns true if the passed principal is a
     * member of the active group. This method does a recursive search, so if a
     * principal belongs to a group which is a member of this group, true is
     * returned.
     * 
     * @param member
     *            The principal whose membership is to be checked.
     * 
     * @return True if the principal is a member of this group, false otherwise.
     * @see java.security.acl.Group#isMember(Principal)
     */
    public boolean isMember(Principal member) {
        if (rolesStack.size() == 0) {
            return false;
        }
        Group activeGroup = (Group) rolesStack.getFirst();
        boolean isMember = activeGroup.isMember(member);
        return isMember;
    }

    /**
     * Returns a string representation of the object. In general, the <code>toString</code>
     * method returns a string that "textually represents" this object. The result should 
     * be a concise but informative representation that is easy for a person to read.
     * 
     * @return a string representation of this group.
     * @see java.security.Principal#toString()
     */
    public String toString() {
        StringBuffer tmp = new StringBuffer(getName());
        tmp.append("(members:"); //$NON-NLS-1$
        Enumeration iter = members();
        while (iter.hasMoreElements()) {
            tmp.append(iter.nextElement());
            tmp.append(',');
        }
        tmp.setCharAt(tmp.length() - 1, ')');
        return tmp.toString();
    }

    private class IndexEnumeration implements Enumeration {

        private Enumeration iter;

        IndexEnumeration() {
            if (rolesStack.size() > 0) {
                Group grp = (Group) rolesStack.get(0);
                iter = grp.members();
            }
        }

        public boolean hasMoreElements() {
            boolean hasMore = iter != null && iter.hasMoreElements();
            return hasMore;
        }

        public Object nextElement() {
            Object next = null;
            if (iter != null) {
                next = iter.nextElement();
            }
            return next;
        }
    }
}