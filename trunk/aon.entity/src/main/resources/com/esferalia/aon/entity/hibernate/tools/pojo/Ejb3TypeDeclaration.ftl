<#if ejb3?if_exists>
<#if pojo.isComponent()>
@${pojo.importType("javax.persistence.Embeddable")}
<#else>
@${pojo.importType("javax.persistence.MappedSuperclass")}
@${pojo.importType("javax.persistence.Table")}(name="${clazz.table.name}"<#if clazz.table.schema?exists>, schema="${clazz.table.schema}"</#if><#if clazz.table.catalog?exists>, catalog="${clazz.table.catalog}"</#if><#assign uniqueConstraint=pojo.generateAnnTableUniqueConstraint()><#if uniqueConstraint?has_content>, uniqueConstraints = ${uniqueConstraint}</#if>)
<#if pojo.getDeclarationName()=="ItemDB">
@${pojo.importType("javax.persistence.Inheritance")}(strategy=${pojo.importType("javax.persistence.InheritanceType")}.JOINED)
</#if>
<#if aonExporter.hasRegistryPrimaryKeyJoinColumn(pojo)>@${pojo.importType("javax.persistence.PrimaryKeyJoinColumn")}(name="registry")
</#if>
<#if aonExporter.hasProjectPrimaryKeyJoinColumn(pojo)>@${pojo.importType("javax.persistence.PrimaryKeyJoinColumn")}(name="project")
</#if>
<#if aonExporter.hasAssetPrimaryKeyJoinColumn(pojo)>@${pojo.importType("javax.persistence.PrimaryKeyJoinColumn")}(name="asset")
</#if>
</#if> 
</#if>
