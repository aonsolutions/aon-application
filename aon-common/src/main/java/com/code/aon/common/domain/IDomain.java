package com.code.aon.common.domain;

import com.code.aon.common.ITransferObject;

public interface IDomain<D extends IDomainPojo<D>> extends ITransferObject {

	public D getDomain();
	public void setDomain(D domain);

}
