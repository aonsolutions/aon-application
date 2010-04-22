package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;

/**
 * Transfer Object that represents a Expense Account Detail.
 * 
 * @author Esferalia. David Uriarte - 20-nov-2009
 * @since 1.0
 */

@Entity
@Table(name="expense_account_detail")
public class ExpenseAccountDetail implements ITransferObject{
	
	
	/** The id. */
	private Integer id;
	
	/** The expense account */
	private ExpenseAccount expenseAccount;
	
	/** The expense. */
	private Expense expense;
	
	/** The quantity. */
	private double quantity;
	
	/** The price. */
    private double price;
	
	/** The price. */
	private double amount;
	
	
	/**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
    @Column(nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="expense_account", nullable=false)
	@ForeignKey(name = "FK_EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT")
	@Index(name = "IDX_EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT")
	public ExpenseAccount getExpenseAccount() {
		return expenseAccount;
	}

	public void setExpenseAccount(ExpenseAccount expenseAccount) {
		this.expenseAccount = expenseAccount;
	}

	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="expense", nullable=false)
	@ForeignKey(name = "FK_EXPENSE_ACCOUNT_DETAIL_EXPENSE")
	@Index(name = "IDX_EXPENSE_ACCOUNT_DETAIL_EXPENSE")
	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Gets the price.
	 * 
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 * 
	 * @param price the price
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ExpenseAccountDetail o = (ExpenseAccountDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.amount, o.amount)
				.append(this.expense, o.expense)
				.append(this.expenseAccount, o.expenseAccount)
				.append(this.quantity, o.quantity)
				.append(this.price, o.price)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(amount)	
			.append(expense)
			.append(expenseAccount)	
			.append(id)
			.append(quantity)
			.append(price)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}