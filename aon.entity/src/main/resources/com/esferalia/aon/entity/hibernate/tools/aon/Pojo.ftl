// AON-ENTITY ${date} - ${version}
package ${aonPackage};

import javax.persistence.Entity;
import javax.persistence.Table;
import com.code.aon.AonVersion;
import ${generatedPackage}.${generatedEntity};

@Entity
${table}
public class ${aonEntity} extends ${generatedEntity} {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
