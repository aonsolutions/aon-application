// AON-MOCK-ENTITY ${date}
package ${aonPackage};

import javax.persistence.Entity;
import javax.persistence.Table;
<#if hasUniqueConstraint>
import javax.persistence.UniqueConstraint;
</#if>
import ${generatedPackage}.${generatedEntity};

@Entity
${table}
public class ${aonEntity} extends ${generatedEntity} {
	
	private static final long serialVersionUID = 1L;
	
} 
