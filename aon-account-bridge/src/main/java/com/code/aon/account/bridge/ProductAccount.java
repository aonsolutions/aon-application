package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Product;

@Entity
@Table(name="product_account")
public class ProductAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = 1809456459170682443L;

	private Integer id;
	private Product product;
	private Account account;
	private ProductAccountType type;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="product", nullable = false)
	@ForeignKey(name="FK_PRODUCT_ACCOUNT_PRODUCT")
	@Index(name="IDX_PRODUCT_ACCOUNT_PRODUCT")					
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_PRODUCT_ACCOUNT_ACCOUNT")
	@Index(name="IDX_PRODUCT_ACCOUNT_ACCOUNT")				
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Column(nullable=false)
	public ProductAccountType getType() {
		return type;
	}
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
		return (getProduct()==null) ? null : getProduct().getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProductAccount o = (ProductAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.product,o.product)
				.append(this.type,o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.product)
			.append(this.type)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}