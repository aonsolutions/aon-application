package net.aonsolutions.occam.api.config;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.metadata.DomainMetadata;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Domain extends OccamEntity<DomainMetadata> {
	
	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private String name;
	private String description;
	private DomainType type;
	private boolean active;
	private Scope scope;
	private Domain parent;
	private boolean inheritance;
	
	private Configuration configuration;
	private Registry company;
	private Booking booking;
	private DomainAudit audit;
	
	private LinkedHashSet<User> users;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Domain markAsClean() {
		super.markAsClean();
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public Domain setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(DomainMetadata.ID));
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}
	public Domain setName(String name) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.name,name), () -> markAsDirty(DomainMetadata.NAME));
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Domain setDescription(String description) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.description,description), () -> markAsDirty(DomainMetadata.DESCRIPTION));
		this.description = description;
		return this;
	}

	public Optional<Domain> getParent() {
		return Optional.ofNullable(parent);
	}
	public Domain setParent(Domain parent) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.parent,parent), () -> markAsDirty(DomainMetadata.PARENT));
		this.parent = parent;
		return this;
	}
	
	public DomainType getType() {
		return type;
	}
	public Domain setType(DomainType type) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.type,type), () -> markAsDirty(DomainMetadata.TYPE));
		this.type = type;
		return this;
	}
	
	public boolean hasInheritance() {
		return inheritance;
	}
	public Domain setInheritance(Boolean inheritance) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.inheritance,inheritance), () -> markAsDirty(DomainMetadata.INHERITANCE));
		this.inheritance = inheritance;
		return this;
	}

	public boolean isActive() {
		return active;
	}
	public Domain setActive(boolean active) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.active,active), () -> markAsDirty(DomainMetadata.ACTIVE));
		this.active = active;
		return this;
	}
	
	public Optional<Scope> getScope() {
		return Optional.ofNullable(scope);
	}
	public Domain setScope(Scope scope) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.scope,scope), () -> markAsDirty(DomainMetadata.SCOPE));
		this.scope = scope;
		return this;
	}
	
	public Optional<Configuration> getConfiguration() {
		return Optional.ofNullable(configuration);
	}
	public Domain setConfiguration(Configuration configuration) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.configuration,configuration), () -> markAsDirty(DomainMetadata.CONFIGURATION));
		this.configuration = configuration;
		return this;
	}
	
	public Optional<Registry> getCompany() {
		return Optional.ofNullable(company);
	}
	public Domain setCompany(Registry company) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.company,company), () -> markAsDirty(DomainMetadata.COMPANY));
		this.company = company;
		return this;
	}
	
	public Optional<Booking> getBooking() {
		return Optional.ofNullable(booking);
	}

	public Domain setBooking(Booking booking) {
		AonObjectUtils.ifTrue( mustMarkaAsDirty(this.booking,booking), () -> markAsDirty(DomainMetadata.BOOKING));
		this.booking = booking;
		return this;
	}

	public Optional<DomainAudit> getAudit() {
		return Optional.ofNullable(audit);
	}

	public Domain setAudit(DomainAudit audit) {
		AonObjectUtils.ifTrue( mustMarkaAsDirty(this.audit,audit), () -> markAsDirty(DomainMetadata.AUDIT));
		this.audit = audit;
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
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}
