package com.code.aon.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.RegistryAttachmentDB;

@Entity
@Table(name="rattach")
@Heritable(force=true)
public class RegistryAttachment extends RegistryAttachmentDB implements IAttachment,IScopable {

	private static final long serialVersionUID = 1L;

	private Set<RegistryAttachmentTag> tags = new HashSet<RegistryAttachmentTag>();
	
	private Integer size;

    public RegistryAttachment() {
    	setSecurityLevel( SecurityLevel.OFFICIAL );
    }

	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	@Transient
	public String getSizeToDisplay() {
		return FileUtils.byteCountToDisplaySize(ArrayUtils.getLength(getData()));
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Transient
	public String getTagList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachmentTag.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_TAG_ATTACHMENT_ID);
		criteria.addEqualExpression(alias, getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			Set<String> tags = new TreeSet<String>();
			for( ITransferObject to : list ) {
				RegistryAttachmentTag rat = (RegistryAttachmentTag) to;
				tags.add( rat.getTag().getName() );
			}
			return StringUtils.join(tags, ", ");
		}
		return null;
	}	
	
	@OneToMany(mappedBy = "attachment", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachmentTag> getTags() {
		return this.tags;
	}

	public void setTags( Set<RegistryAttachmentTag> tags ) {
		this.tags = tags;
	}
	
	@Transient
	public String getMD5() {
		if ( getData() != null ) {
			return DigestUtils.md5Hex(getData());	
		}
		return null;
	}

	@Transient
	public String getDownloadURL() {
		return "/aonDocuments/" + getId() + "-" + getMD5();
	}
	
}