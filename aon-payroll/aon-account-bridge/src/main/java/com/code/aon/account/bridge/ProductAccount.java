package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.common.ITransferObject;
import com.code.aon.product.Product;

/**
 * The Class ProductAccount.
 */
@Entity
@Table(name="product_account")
public class ProductAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = 1809456459170682443L;

	/** The id. */
	private Integer id;
	
	/** The product. */
	private Product product;
	
	/** The account. */
	private Account account;
	
	/** The type. */
	private ProductAccountType type;

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

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the product.
	 * 
	 * @return the product
	 */
	@ManyToOne
	@JoinColumn(name="product", nullable = false)
	@ForeignKey(name="FK_PRODUCT_ACCOUNT_PRODUCT")
	@Index(name="IDX_PRODUCT_ACCOUNT_PRODUCT")					
	public Product getProduct() {
		return product;
	}

	/**
	 * Sets the product.
	 * 
	 * @param product the product
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_PRODUCT_ACCOUNT_ACCOUNT")
	@Index(name="IDX_PRODUCT_ACCOUNT_ACCOUNT")				
	public Account getAccount() {
		return account;
	}

	/**
	 * Sets the account.
	 * 
	 * @param account the account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Column(nullable=false)
	public ProductAccountType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(ProductAccountType type) {
		this.type = type;
	}

	@Transient
	public ITransferObject getLinkedTo() {
		return getProduct();
	}
	public void setLinkedTo(ITransferObject to) {
		setProduct((Product) to);
	}	
	@Transient
	public String getAccountDescription() {
		return getProduct()==null?null:getProduct().getName();
	}
	
}