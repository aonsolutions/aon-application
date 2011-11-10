	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ${pojo.getDeclarationName()} o = (${pojo.getDeclarationName()}) obj;
		if (o.${pojo.getGetterSignature(pojo.getIdentifierProperty())}() == null && ${pojo.getGetterSignature(pojo.getIdentifierProperty())}() == null) {
			return new ${pojo.importType("org.apache.commons.lang.builder.EqualsBuilder")}()
<#foreach property in pojo.getAllPropertiesIterator()><#if pojo.getIdentifierProperty() != property>			.append(this.${property.getName()},o.${property.getName()})
</#if></#foreach>
			.isEquals();
		}
		return ${pojo.importType("org.apache.commons.lang.ObjectUtils")}.equals(${pojo.getGetterSignature(pojo.getIdentifierProperty())}(), o.${pojo.getGetterSignature(pojo.getIdentifierProperty())}());		
	}

	@Override
	public int hashCode() {
		return new ${pojo.importType("org.apache.commons.lang.builder.HashCodeBuilder")}()
<#foreach property in pojo.getAllPropertiesIterator()>			.append(${property.getName()})
</#foreach>
			.toHashCode();
   }   
