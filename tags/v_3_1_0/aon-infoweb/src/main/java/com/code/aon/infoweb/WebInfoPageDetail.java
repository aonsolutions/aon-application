package com.code.aon.infoweb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.infoweb.enumeration.WebInfoLayoutType;

@Entity
@Table(name="web_info_page_detail")
public class WebInfoPageDetail implements ITransferObject {
	
	private static final long serialVersionUID = 1708466275331452164L;

	private Integer id;
	
	private WebInfoPage webInfoPage;
	
	private String title;
		
	private WebInfoLayoutType layout;
	
	private String content;

	private String extra;

	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="web_info_page",nullable=false )
    @ForeignKey(name = "FK_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE")
    @Index(name = "IDX_WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE")	
	public WebInfoPage getWebInfoPage() {
		return webInfoPage;
	}

	public void setWebInfoPage(WebInfoPage webInfoPage) {
		this.webInfoPage = webInfoPage;
	}

	@Column(name="title",length=255)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name="layout")
	public WebInfoLayoutType getLayout() {
		return layout;
	}

	public void setLayout(WebInfoLayoutType layout) {
		this.layout = layout;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name="content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name="extra",length=255)
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WebInfoPageDetail o = (WebInfoPageDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.content, o.content)
				.append(this.extra, o.extra)
				.append(this.layout, o.layout)
				.append(this.title, o.title)
				.append(this.webInfoPage, o.webInfoPage)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(content)
			.append(extra)			
			.append(id)
			.append(layout)
			.append(title)
			.append(webInfoPage)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}