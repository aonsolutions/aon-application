package com.code.aon.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProfileDB;

@Entity
@Table(name="profile")
public class Profile extends ProfileDB {
	
	private static final long serialVersionUID = 1L;

	private Set<ProfileRole> roles = new HashSet<ProfileRole>();

	@OneToMany(mappedBy = "profile", cascade={CascadeType.REMOVE})
	public Set<ProfileRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<ProfileRole> roles) {
		this.roles = roles;
	}

	@Transient
	public String getRoleList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfileRole.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.PROFILE_ROLE_PROFILE_ID);
		criteria.addEqualExpression(alias, getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			Set<String> roles = new TreeSet<String>();
			for( ITransferObject to : list ) {
				ProfileRole pr = (ProfileRole) to;
				roles.add( pr.getApplicationRole().getRole().getName() );
			}
			return StringUtils.join(roles, ", ");
		}
		return null;
	}	

}