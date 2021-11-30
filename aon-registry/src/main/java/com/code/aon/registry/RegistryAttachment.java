package com.code.aon.registry;

import static com.code.aon.common.BlobObjectAction.READ;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.BlobObjectAction;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.dao.hibernate.BlobEntityListener;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.RegistryAttachmentDB;

@Entity
@Table(name="rattach")
@Heritable(force=true)
@EntityListeners(BlobEntityListener.class)
public class RegistryAttachment extends RegistryAttachmentDB implements IAttachment, IScopable, IBlobObject, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<RegistryAttachmentTag> tags = new HashSet<RegistryAttachmentTag>();

	private byte[] data;

	private Integer size;

	private String MD5;

    public RegistryAttachment() {
    	setSecurityLevel( SecurityLevel.OFFICIAL );
    }

    @Transient
	@Column(name="data")
	public byte[] getData() {
    	if ( data != null ) {
    		return data;
    	}
		return getManager(READ).getBlob(this, DATA_PROPERTY);
	}

	public void setData(byte[] data) {
		this.data = data;
		setSize(ArrayUtils.getLength(data));
		setMD5(DigestUtils.md5Hex(ArrayUtils.nullToEmpty(data)));
	}

	@Formula("IFNULL(LENGTH(data),IFNULL(dparent_id,0))")
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	@Transient
	public String getSizeToDisplay() {
		return FileUtils.byteCountToDisplaySize(getSize()!=null?getSize():0);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Transient
	public String getTagList(Integer domainId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachmentTag.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_TAG_ATTACHMENT_ID);
		criteria.addEqualExpression(alias, getId());
		if ( domainId != null ) {
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_TAG_DOMAIN), domainId);
		}
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

	@Transient
	public String getTagList() throws ManagerBeanException {
		return getTagList(null);
	}

	@OneToMany(mappedBy = "attachment", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachmentTag> getTags() {
		return this.tags;
	}

	public void setTags( Set<RegistryAttachmentTag> tags ) {
		this.tags = tags;
	}

	@Formula("MD5(data)")
	public String getMD5() {
		return MD5;
	}

	public void setMD5(String mD5) {
		MD5 = mD5;
	}

	@Transient
	public String getDownloadURL() {
		return "/aonDocuments/" + getId() + "-" + getMD5();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.appendSuper(super.hashCode())
			.append(size)
			.append(MD5)
			.toHashCode();
	}

	@Override
	@Transient
	public String[] getBlobProperties() {
		return DATA_BLOB_PROPERTIES;
	}

	@Override
	@Transient
	public Serializable getReference( String property ) {
		return (getDriveId()!=null) ? getDriveId() : getId();
	}

	@Override
	@Transient
	public IBlobManager getManager( BlobObjectAction action ) {
		if ( getDriveId() != null ) {
			return DriveUtils.getInstace();
		}
		return HibernateBlobManager.getInstance();
	}

	@Override
	public void reset() {
		this.data = null;
	}

	@Override
	@Transient
	public String getAonType() {
		return "registry";
	}

	@Override
	@Transient
	public void setAonType(String aonType) {
	}

}
