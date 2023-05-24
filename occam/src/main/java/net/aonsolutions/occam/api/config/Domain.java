package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Domain implements Serializable, HasSelector<Domain>,HasDirtyFlag<Domain> {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private String name;
	private String description;
	private DomainType type;
	private boolean active;
	private Scope scope;
	private Domain parent;
	private boolean enableHeredity;
	
	private Registry company;
	private Booking booking;
	private DomainAudit audit;
	
	private LinkedHashSet<User> users; 
	
	private boolean dirty;
	private boolean selected;

	public Integer getId() {
		return id;
	}
	public Domain setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}
	public Domain setName(String name) {
		this.dirtyMark( AonObjectUtils.notEquals(this.name,name) );
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Domain setDescription(String description) {
		this.dirtyMark( AonObjectUtils.notEquals(this.description,description) );
		this.description = description;
		return this;
	}

	public Optional<Domain> getParent() {
		return Optional.ofNullable(parent);
	}
	public Domain setParent(Domain parent) {
		this.dirtyMark( AonObjectUtils.notEquals(this.parent,parent) );
		this.parent = parent;
		return this;
	}
	
	public DomainType getType() {
		return type;
	}
	public Domain setType(DomainType type) {
		this.dirtyMark( AonObjectUtils.notEquals(this.type,type) );
		this.type = type;
		return this;
	}
	
	public boolean isEnableHeredity() {
		return enableHeredity;
	}
	public Domain setEnableHeredity(Boolean enableHeredity) {
		this.dirtyMark( AonObjectUtils.notEquals(this.enableHeredity,enableHeredity) );
		this.enableHeredity = enableHeredity;
		return this;
	}

	public boolean isActive() {
		return active;
	}
	public Domain setActive(boolean active) {
		this.dirtyMark( AonObjectUtils.notEquals(this.active,active) );
		this.active = active;
		return this;
	}
	
	public Optional<Scope> getScope() {
		return Optional.ofNullable(scope);
	}
	public Domain setScope(Scope scope) {
		this.dirtyMark( AonObjectUtils.notEquals(this.scope,scope) );
		this.scope = scope;
		return this;
	}
	
	public Optional<Registry> getCompany() {
		return Optional.ofNullable(company);
	}
	public Domain setCompany(Registry company) {
		this.dirtyMark( this.company, company);
		this.company = company;
		return this;
	}
	
	public Optional<Booking> getBooking() {
		return Optional.ofNullable(booking);
	}

	public Domain setBooking(Booking booking) {
		this.dirtyMark( this.booking, booking );
		this.booking = booking;
		return this;
	}

	public Optional<DomainAudit> getAudit() {
		return Optional.ofNullable(audit);
	}

	public Domain setAudit(DomainAudit audit) {
		this.dirtyMark( this.audit, audit);
		this.audit = audit;
		return this;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public Domain setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	// ---------------------------------------------------------- HasSelector<Domain>
	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Domain setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	// ---------------------------------------------------------- 
	public Optional<Collection<User>> getUsers() {
		return Optional.ofNullable(users);
	}
	public Domain setUsers(Collection<User> users) {
		this.users = null;
		return addUsers(users);
	}
	public Domain addUsers(Collection<User> users) {
		if (AonCollectionUtils.isNotEmpty(users)) {
			if (this.users == null ) {
				this.users = new LinkedHashSet<>();
			}
			this.users.addAll(users);
		}
		return this;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Domain other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
