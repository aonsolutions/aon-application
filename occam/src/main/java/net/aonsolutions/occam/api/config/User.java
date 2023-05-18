package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Objects;

import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class User implements Serializable, HasSelector<User> {

	private static final long serialVersionUID = -1817293946547861739L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String login;
	private boolean active;
	
	private boolean dirty;
	private boolean selected;
	
	public Integer getId() {
		return id;
	}
	public User setId(Integer id) {
		this.setDirty( isDirty() || AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public User setDomain(Integer domain) {
		this.setDirty( isDirty() || AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public User setName(String name) {
		this.setDirty( isDirty() || AonObjectUtils.notEquals(this.name,name) );
		this.name = name;
		return this;
	}
	
	public String getLogin() {
		return login;
	}
	public User setLogin(String login) {
		this.setDirty( isDirty() || AonObjectUtils.notEquals(this.login,login) );
		this.login = login;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	public User setActive(boolean active) {
		this.setDirty( isDirty() || AonObjectUtils.notEquals(this.active,active) );
		this.active = active;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	public User setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	// ---------------------------------------------------------- HasSelector<User>
	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public User setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof User other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
