package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "article_category")
public class ArticleCategory implements IActivableObject, IPositionObject {

	private static final long serialVersionUID = -2266381230541904502L;

	private Integer id;

	private String alias;

	private boolean active = true;

	private Integer position = new Integer(0);
	
	private Section section;
	
	private Section elementSection;
	
	private Set<ArticleCategoryDetail> details;

	private Set<Article> articles;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "alias", nullable = false, length = 32)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "position", nullable = false)
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@OneToMany(mappedBy = "articleCategory", cascade={CascadeType.REMOVE})
	public Set<ArticleCategoryDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<ArticleCategoryDetail> details ) {
		this.details = details;
	}

	@OneToMany(mappedBy = "articleCategory", cascade={CascadeType.REMOVE})
	public Set<Article> getArticles() {
		return this.articles;
	}

	public void setArticles( Set<Article> articles) {
		this.articles = articles;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section")
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "elementSection")
	public Section getElementSection() {
		return elementSection;
	}

	public void setElementSection(Section elementSection) {
		this.elementSection = elementSection;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ArticleCategory o = (ArticleCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.elementSection, o.elementSection)
				.append(this.position, o.position)
				.append(this.section, o.section)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(alias)
			.append(elementSection)
			.append(id)	
			.append(position)
			.append(section)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}
