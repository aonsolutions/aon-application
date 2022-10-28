<#if ejb3>
<#if pojo.hasIdentifierProperty()>
<#if property.equals(clazz.identifierProperty)>
<#if hasRegistryPrimaryKeyJoinColumn>    @${pojo.importType("javax.persistence.Id")} 
	@javax.persistence.GeneratedValue(generator="registry_id")
	@org.hibernate.annotations.GenericGenerator(name="registry_id", strategy="foreign", parameters = {@org.hibernate.annotations.Parameter(name="property", value="registry")})
<#elseif hasProjectPrimaryKeyJoinColumn>    @${pojo.importType("javax.persistence.Id")} 
	@javax.persistence.GeneratedValue(generator="project_id")
	@org.hibernate.annotations.GenericGenerator(name="project_id", strategy="foreign", parameters = {@org.hibernate.annotations.Parameter(name="property", value="project")})
<#elseif hasAssetPrimaryKeyJoinColumn>    @${pojo.importType("javax.persistence.Id")} 
	@javax.persistence.GeneratedValue(generator="asset_id")
	@org.hibernate.annotations.GenericGenerator(name="asset_id", strategy="foreign", parameters = {@org.hibernate.annotations.Parameter(name="property", value="asset")})
<#else>
${pojo.generateAnnIdGenerator()}
	</#if>
</#if>
</#if>
<#if property.getName()=="system" && pojo.getDeclarationName()=="GeoZoneDB">
    @Column(name="`system`", nullable=false)
<#elseif property.getName()=="product" && pojo.getDeclarationName()=="ItemDB">	@${pojo.importType("javax.persistence.ManyToOne")}
    @javax.persistence.JoinColumn(name="product", nullable=false)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="child" && pojo.getDeclarationName()=="GeoTreeDB">	@${pojo.importType("javax.persistence.ManyToOne")}
	@${pojo.importType("javax.persistence.OneToOne")}(cascade={${pojo.importType("javax.persistence.CascadeType")}.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade(value={org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    @javax.persistence.JoinColumn(name="child", nullable = false)
<#elseif property.getName()=="workPlace" && pojo.getDeclarationName()=="HotelDB">	@${pojo.importType("javax.persistence.ManyToOne")}
    @javax.persistence.JoinColumn(name="workplace", nullable = false)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="payMethodTypeDetail" && pojo.getDeclarationName()=="PayMethodTypeDetailAccountDB">	@${pojo.importType("javax.persistence.ManyToOne")}(fetch=${pojo.importType("javax.persistence.FetchType")}.EAGER)
    @javax.persistence.JoinColumn(name="pm_type_detail", nullable=false)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="finance" && pojo.getDeclarationName()=="PrepaymentDB">	@${pojo.importType("javax.persistence.ManyToOne")}(fetch=${pojo.importType("javax.persistence.FetchType")}.EAGER)
    @javax.persistence.JoinColumn(name="finance", nullable=false)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="content" && pojo.getDeclarationName()=="MessageDB">	@${pojo.importType("javax.persistence.ManyToOne")}(cascade = {${pojo.importType("javax.persistence.CascadeType")}.ALL} )
	@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
	@javax.persistence.JoinColumn(name="message_content")
<#elseif property.getName()=="rattach" && pojo.getDeclarationName()=="WebInfoPageResourceDB">	@${pojo.importType("javax.persistence.OneToOne")}(fetch = ${pojo.importType("javax.persistence.FetchType")}.EAGER)
	@${pojo.importType("javax.persistence.JoinColumn")}( name="rattach" )
<#elseif property.getName()=="webInfoPage" && pojo.getDeclarationName()=="WebInfoPageDetailDB">	@${pojo.importType("javax.persistence.OneToOne")}(fetch = ${pojo.importType("javax.persistence.FetchType")}.EAGER)	
	@${pojo.importType("javax.persistence.JoinColumn")}( name="web_info_page",nullable=false )
<#elseif property.getName()=="address" && pojo.getDeclarationName()=="WorkPlaceDB">	@${pojo.importType("javax.persistence.OneToOne")}(cascade={${pojo.importType("javax.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@javax.persistence.JoinColumn(name="address", nullable = false)
<#elseif property.getName()=="enterprise" && pojo.getDeclarationName()=="EnterpriseDataDB">	@${pojo.importType("javax.persistence.OneToOne")}
	@javax.persistence.JoinColumn(name="enterprise", nullable=false)
<#elseif property.getName()=="enterprise" && pojo.getDeclarationName()=="EnterpriseActivityDB">	@${pojo.importType("javax.persistence.OneToOne")}
    @javax.persistence.JoinColumn(name="enterprise", nullable = false, updatable = false )
<#elseif property.getName()=="activity" && pojo.getDeclarationName()=="EnterpriseCCCDB">	@${pojo.importType("javax.persistence.OneToOne")}
	@javax.persistence.JoinColumn(name="enterprise_activity", nullable = false)
<#elseif property.getName()=="registry" && hasRegistryPrimaryKeyJoinColumn>	@${pojo.importType("javax.persistence.OneToOne")}(cascade={${pojo.importType("javax.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("javax.persistence.PrimaryKeyJoinColumn")}	
<#elseif property.getName()=="project" && hasProjectPrimaryKeyJoinColumn>	@${pojo.importType("javax.persistence.OneToOne")}(cascade={${pojo.importType("javax.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("javax.persistence.PrimaryKeyJoinColumn")}	
<#elseif property.getName()=="asset" && hasAssetPrimaryKeyJoinColumn>	@${pojo.importType("javax.persistence.OneToOne")}(cascade={${pojo.importType("javax.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("javax.persistence.PrimaryKeyJoinColumn")}	
<#elseif c2h.isManyToOne(property)>	${pojo.generateManyToOneAnnotation(property)}
${pojo.generateJoinColumnsAnnotation(property, cfg)}
<#elseif c2h.isCollection(property)>
${pojo.generateCollectionAnnotation(property, cfg)}
<#else>
<#if !pojo.generateBasicAnnotation(property).trim().equals("")>
${pojo.generateBasicAnnotation(property)}
</#if>
${pojo.generateAnnColumnAnnotation(property)}
<#if property.getType().getName()=="text" || property.getType().getName()=="binary">
	@${pojo.importType("javax.persistence.Lob")}
</#if>
<#if property.getType().getName()=="com.code.aon.config.BankAccount">
	@${pojo.importType("org.hibernate.annotations.Type")}(type="com.code.aon.config.hibernate.BankAccountType")
</#if>
<#if property.getType().getName()=="com.code.aon.product.util.DiscountExpression">
	@${pojo.importType("org.hibernate.annotations.Type")}(type="com.code.aon.product.util.DiscountExpressionUserType")
</#if>
<#if aonExporter.isStringEnum(property)>
	@${pojo.importType("org.hibernate.annotations.Type")}(type = "stringEnum", parameters = { @${pojo.importType("org.hibernate.annotations.Parameter")}(name = "enumClassname", value = "${property.getType().getName()}") })
</#if>
</#if>
</#if>
