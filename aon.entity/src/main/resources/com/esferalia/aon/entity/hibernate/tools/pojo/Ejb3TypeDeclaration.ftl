<#if ejb3?if_exists>
<#if pojo.isComponent()>
@${pojo.importType("jakarta.persistence.Embeddable")}
<#else>
@${pojo.importType("jakarta.persistence.MappedSuperclass")}
@${pojo.importType("jakarta.persistence.Table")}(name="${clazz.table.name}"<#if clazz.table.schema?exists>, schema="${clazz.table.schema}"</#if><#if clazz.table.catalog?exists>, catalog="${clazz.table.catalog}"</#if><#assign uniqueConstraint=pojo.generateAnnTableUniqueConstraint()><#if uniqueConstraint?has_content>, uniqueConstraints = ${uniqueConstraint}</#if>)
<#if pojo.getDeclarationName()=="ItemDB">
@${pojo.importType("jakarta.persistence.Inheritance")}(strategy=${pojo.importType("jakarta.persistence.InheritanceType")}.JOINED)
</#if>
<#if aonExporter.hasRegistryPrimaryKeyJoinColumn(pojo)>@${pojo.importType("jakarta.persistence.PrimaryKeyJoinColumn")}(name="registry")
</#if>
<#if aonExporter.hasProjectPrimaryKeyJoinColumn(pojo)>@${pojo.importType("jakarta.persistence.PrimaryKeyJoinColumn")}(name="project")
</#if>
<#if aonExporter.hasAssetPrimaryKeyJoinColumn(pojo)>@${pojo.importType("jakarta.persistence.PrimaryKeyJoinColumn")}(name="asset")
</#if>
</#if> 
</#if>
