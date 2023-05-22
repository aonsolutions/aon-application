// AON-ENTITY Nov 6, 2012 12:29:06 PM - 3.2.2.GA
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
	@${pojo.importType("jakarta.persistence.Transient")}
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
