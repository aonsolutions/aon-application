package com.code.aon.commercial;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.commercial.enumeration.ExpenseHolderType;
import com.code.aon.commercial.enumeration.ExpenseStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents a Expense Account.
 * 
 * @author Esferalia. David Uriarte - 20-nov-2009
 * @since 1.0
 */
@Entity
@Table(name="expense_account")
public class ExpenseAccount implements ITransferObject,IRegistry{
	
	
	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;
	
	/** The ExpenseHolderType. */
	private ExpenseHolderType type;
	
	/** The ExpenseStatus. */
	private ExpenseStatus status;
	
	/** The description. */
	private String description;
	
	 /** The issue date. */
    private Date issueDate;
    
    /** The comments. */
	private String comments;
	
	/** The detail of this ExpenseAccount. */
	private Set<ExpenseAccountDetail> lines = new HashSet<ExpenseAccountDetail>();
	
	/**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

	/**
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registry", nullable = false)
    @ForeignKey(name="FK_EXPENSE_ACCOUNT_REGISTRY_REGISTRY")
    @Index(name="registry")  
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	
	/**
	 * Gets the expense holder type.
	 * 
	 * @return the expense holder type.
	 */
	@Column(name="expense_holder_type")
	public ExpenseHolderType getType() {
		return type;
	}

	/**
	 * Sets the expense holder type.
	 * 
	 * @param expense holder type the expense holder type
	 */
	public void setType(ExpenseHolderType type) {
		this.type = type;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status.
	 */
	public ExpenseStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(ExpenseStatus status) {
		this.status = status;
	}

	/**
     * Gets the description.
     * 
     * @return the description
     */
	@Column(length=40)
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
	 * Gets the issue date.
	 * 
	 * @return the issue date
	 */
	@Column(name="issue_date")
	public Date getIssueDate() {
		return issueDate;
	}

	/**
	 * Sets the issue date.
	 * 
	 * @param issueDate the issue date
	 */
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Lob
	@Column(name="comments")	
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments the new comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * Gets the lines.
	 * 
	 * @return the lines
	 */
	@OneToMany(mappedBy = "expenseAccount", cascade={CascadeType.REMOVE})
	@OrderBy("id")
	public Set<ExpenseAccountDetail> getLines() {
		return this.lines;
	}

	/**
	 * Sets the lines.
	 * 
	 * @param lines the lines
	 */
	public void setLines( Set<ExpenseAccountDetail> lines ) {
		this.lines = lines;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ExpenseAccount o = (ExpenseAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.comments, o.comments)
				.append(this.description, o.description)
				.append(this.issueDate, o.issueDate)
				.append(this.registry, o.registry)
				.append(this.status, o.status)
				.append(this.type, o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(comments)	
			.append(description)
			.append(id)	
			.append(issueDate)
			.append(registry)
			.append(status)
			.append(type)	
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}