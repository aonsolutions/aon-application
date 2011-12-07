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
