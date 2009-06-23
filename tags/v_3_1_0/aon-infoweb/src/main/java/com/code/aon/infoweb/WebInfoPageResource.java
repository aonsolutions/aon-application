package com.code.aon.infoweb;

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
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.registry.RegistryAttachment;

@Entity
@Table(name="web_info_page_resource")
public class WebInfoPageResource implements ITransferObject {
	
	private static final long serialVersionUID = 2700609535843713724L;

	private Integer id;

	private WebInfoPage webInfoPage;

	private RegistryAttachment rattach;
	
	private String content;

	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="web_info_page",nullable=false )
    @ForeignKey(name = "FK_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE")
    @Index(name = "IDX_WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE")		
	public WebInfoPage getWebInfoPage() {
		return webInfoPage;
	}

	public void setWebInfoPage(WebInfoPage webInfoPage) {
		this.webInfoPage = webInfoPage;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="rattach" )
    @ForeignKey(name = "FK_WEB_INFO_PAGE_RESOURCE_RATTACH")
    @Index(name = "IDX_WEB_INFO_PAGE_RESOURCE_RATTACH")			
	public RegistryAttachment getRattach() {
		return rattach;
	}

	public void setRattach(RegistryAttachment rattach) {
		this.rattach = rattach;
	}

	@Column(name="content",	length=255)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WebInfoPageResource o = (WebInfoPageResource) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.content, o.content)
				.append(this.rattach, o.rattach)
				.append(this.webInfoPage, o.webInfoPage)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(content)		
			.append(id)
			.append(rattach)
			.append(webInfoPage)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}