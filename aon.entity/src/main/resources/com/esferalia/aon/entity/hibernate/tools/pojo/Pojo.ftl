// AON-ENTITY ${date} - ${version}
${pojo.getPackageDeclaration()}
<#assign classbody>
<#include "PojoTypeDeclaration.ftl"/> {

<#if !pojo.isInterface()>
<#include "PojoFields.ftl"/>

<#if pojo.hasMetaAttribute("aon-constructor")>
<#include "PojoConstructors.ftl"/>

</#if>
<#include "PojoPropertyAccessors.ftl"/>
<#if (isConfidentialable)>
	@${pojo.importType("javax.persistence.Transient")}
	@Override
	public boolean isConfidential() {
		return ${pojo.importType("com.code.aon.common.enumeration.SecurityLevel")}.CONFIDENTIAL == getSecurityLevel();
	}

	@Transient
	@Override
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

</#if>

<#include "PojoEqualsHashcode.ftl"/>

<#include "PojoToString.ftl"/>

<#else>
<#include "PojoInterfacePropertyAccessors.ftl"/>

</#if>
<#include "PojoExtraClassCode.ftl"/>
}
</#assign>

${pojo.generateImports()}
${classbody}
