package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.AccountEntryMetadata;
import net.aonsolutions.occam.api.model.type.AccountEntryType;

public class AccountEntry extends AonEntity<AccountEntryMetadata> implements HasAudit {

	private static final long serialVersionUID = 369125336534396707L;

	private Integer id;
	private Integer domain;
	private AccountPeriod period;
	private Date entryDate;
	private AccountEntryType entryType;
	private Activity activity;
	private Integer journal;
	private boolean confidential;
	private String comments;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
	private LinkedList<AccountEntryDetail> details;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public AccountEntry markAsClean() {
		super.markAsClean();
		return this; 
	}
	@Override
	public AccountEntry setSelected(boolean selected) {
		super.setSelected(selected);
		return this;
	}
	@Override
	public AccountEntry setDeleted(boolean selected) {
		super.setDeleted(selected);
		return this;
	}

	public Integer getId() {
		return this.id;
	}
	public AccountEntry setId(Integer id) {
		checkIfDirty( this.id,id, AccountEntryMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public AccountEntry setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, AccountEntryMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}

	public Optional<AccountPeriod> getPeriod() {
		return Optional.ofNullable(period);
	}
	public AccountEntry setPeriod(AccountPeriod period) {
		checkIfDirty( this.period,period, AccountEntryMetadata.ACCOUNT_PERIOD);
		this.period = period;
		return this;
	}

	public boolean isPeriodActive() {
		return getPeriod().isEmpty() 
			|| getPeriod().map( p -> p.isActive()).orElse(false);
	}
	
	public Date getEntryDate() {
		return this.entryDate;
	}
	public AccountEntry setEntryDate(Date entryDate) {
		checkIfDirty( this.entryDate,entryDate, AccountEntryMetadata.ENTRY_DATE);
		this.entryDate = entryDate;
		return this;
	}

	public AccountEntryType getEntryType() {
		return this.entryType;
	}
	public AccountEntry setEntryType(AccountEntryType entryType) {
		checkIfDirty( this.entryType,entryType, AccountEntryMetadata.ENTRY_TYPE);
		this.entryType = entryType;
		return this;
	}
	
	public Optional<Activity> getActivity() {
		return Optional.ofNullable(activity);
	}
	public AccountEntry setActivity(Activity activity) {
		checkIfDirty( this.activity,activity, AccountEntryMetadata.ACTIVITY);
		this.activity = activity;
		return this;
	}
	
	public String getActivityDescription() {
		return getActivity().map(a -> a.getDescription()).orElse(null);
	}
	
	public boolean isInvoice() {
		return getEntryType() == null || getEntryType().isInvoice();
	}

	public Integer getJournal() {
		return this.journal;
	}
	public AccountEntry setJournal(Integer journal) {
		checkIfDirty( this.journal,journal, AccountEntryMetadata.JOURNAL);
		this.journal = journal;
		return this;
	}

	public boolean isConfidential() {
		return this.confidential;
	}
	public AccountEntry setConfidential(boolean confidential) {
		checkIfDirty( this.confidential,confidential, AccountEntryMetadata.SECURITY_LEVEL);
		this.confidential = confidential;
		return this;
	}

	public String getComments() {
		return this.comments;
	}
	public AccountEntry setComments(String comments) {
		checkIfDirty( this.comments,comments, AccountEntryMetadata.COMMENTS);
		this.comments = comments;
		return this;
	}

	public Stream<AccountEntryDetail> detailStream() {
		return AonCollectionUtils.stream( details );
	}
	public AccountEntry addDetail( AccountEntryDetail detail) {
		if (this.details == null) this.details = new LinkedList<>();
		markAsDirty(AccountEntryMetadata.DETAILS);
		this.details.add(detail);
		return this;
	}
	
	public int getDetailsSize() {
		return (int) detailStream()
			.filter(aed -> !aed.isDeleted())
			.count();
	}
	
	public Optional<AccountEntryDetail> getLastDetail() {
		return detailStream()
			.filter(aed -> !aed.isDeleted())
			.reduce( (a,b) -> b);
	}
	
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public AccountEntry setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public AccountEntry setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public AccountEntry setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public AccountEntry setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof AccountEntry other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
	static AccountEntry clone(AccountEntry ori) {
		AccountEntry newEntry = new AccountEntry() 
			.setId(ori.id)
			.setPeriod(ori.period)
			.setDomain(ori.domain)
			.setEntryDate(ori.entryDate)
			.setEntryType(ori.entryType)
			.setActivity(ori.activity)
			.setJournal(ori.journal)
			.setConfidential(ori.confidential)
			.setComments(ori.comments)
			.setCreationUser(ori.creationUser)
			.setCreationDate(ori.creationDate)
			.setModificationUser(ori.modificationUser)
			.setModificationDate(ori.modificationDate)
			.setDeleted(ori.isDeleted())
			.setSelected(ori.isSelected())
		;
		ori.detailStream()
			.map(AccountEntryDetail::clone)
			.forEach( newEntry::addDetail);
		newEntry.markAsClean();
		ori.dirtySetStream().forEach(newEntry::markAsDirty); 
		return newEntry;
	}
}
