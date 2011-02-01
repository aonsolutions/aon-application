package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "diary_i18n")
public class DiaryDetail implements ITransferObject {

	private static final long serialVersionUID = -7278149395315303099L;

	private Integer id;

	private Diary diary;

	private Language language;

	private String content;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "diary", nullable = false)
	public Diary getDiary() {
		return diary;
	}

	public void setDiary(Diary diary) {
		this.diary = diary;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "content")
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
		final DiaryDetail o = (DiaryDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.content, o.content)				
				.append(this.diary, o.diary)
				.append(this.language, o.language)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(content)
			.append(diary)
			.append(id)	
			.append(language)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}		
	
}
