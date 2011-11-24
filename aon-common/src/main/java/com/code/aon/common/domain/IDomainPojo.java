package com.code.aon.common.domain;

import com.code.aon.common.ITransferObject;

public interface IDomainPojo<D extends IDomainPojo<D>> extends ITransferObject {

	public Integer getId();
	public void setId(Integer id);

	public String getName();
	public void setName(String name);

	public D getParent();
	public void setParent(D parent);

    public boolean isActive();
    public void setActive(boolean active);

}
