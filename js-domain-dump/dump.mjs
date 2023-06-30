
function dump(connection, domain, stream) {
	stream.write( "var enterprises = enterprises || [];\r\n" );
	stream.write( "var employees  = employees || [];\r\n" );
	stream.write( "var banks  = banks || [];\r\n" );
	stream.write( "var documents  = documents || [];\r\n" );
	stream.write( "var taxModels  = taxModels || [];\r\n" );
	return new Promise(( resolve, reject ) => {
		connection.query("SHOW DATABASES", function(err, results, fields) {
			if ( err ) throw err;
			const databases = results.map( d => d.Database );
			databases.forEach( database => {
				dumpEnterprise(connection, database, domain )
				.then( enterprises => { 
					enterprises.forEach( enterprise => stream.write( "enterprises.push(" + JSON.stringify(toEnterprise(enterprise)) +");\r\n" )); 
					return dumpEmployee(connection, database, domain)
				})
				.then ( employees => {
					employees.forEach ( employee => stream.write( "employees.push(" + JSON.stringify(toEmployee(employee)) +");\r\n" ));
					return dumpBank(connection, database, domain)
				})
				.then( banks => { 
					banks.forEach( bank => stream.write( "banks.push(" + JSON.stringify(toBank(bank)) +");\r\n" )); 
					return dumpTaxModel(connection, database, domain)
				})
				.then( taxModels => { 
					taxModels.forEach( taxModel => stream.write( "taxModels.push(" + JSON.stringify(toTaxModel(taxModel)) +");\r\n" )); 
					return dumpInvoiceDocument(connection, database, domain)
				})
				.then( documents => { 
					documents.forEach( document => stream.write( "documents.push(" + JSON.stringify(toDocument(document)) +");\r\n" )); 
					return dumpContractDocument(connection, database, domain)
				})
				.then( documents => { 
					documents.forEach( document => stream.write( "documents.push(" + JSON.stringify(toDocument(document)) +");\r\n" )); 
					resolve( {} );
				})
				.catch( err =>{
					//reject(err);
				 });
			});
		});
	});
	
}
 
function toEnterprise(enterprise) {
	return { 
		__id: enterprise.id,
		name : enterprise.name,
		document : enterprise.document
	};
}

function toEmployee(employee) {
	return { 
		__id: employee.id,
		name : employee.name,
		document : employee.document,
		email : employee.email,
		phone : employee.phone,
		naf : employee.naf,
		active : employee.active
	};
}

function toBank(bank) {
	return { 
		__id: bank.id,
		name : bank.bank_account
	};
}

function toTaxModel(taxModel) {
	return { 
		__id: taxModel.id,
		name : taxModel.name,
		status : taxModel.status,
		paymentMethod : taxModel.paymentmethod,
		result : taxModel.result,
		trimester : taxModel.trimester,
		year : taxModel.year
	};
}

function toDocument(document) {
	return { 
		__id: document.id,
		file : document.file,
		fileName : document.fileName,
		fileType : document.fileType
	};
}

function dumpEnterprise(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" domain.id AS id"
			+", registry.name AS name"
			+", registry.document AS document"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`company` ON (  `domain`.`id` = `company`.`domain` )"
			+ " INNER JOIN `" + database + "`.`enterprise` ON (  `company`.`registry` = `enterprise`.`registry` )"
			+ " INNER JOIN `" + database + "`.`registry` ON (  `enterprise`.`registry` = `registry`.`id` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

function dumpEmployee(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" contract.id AS id"
			+", registry.name AS name"
			+", registry.document AS document"
			+", person.social_security_num AS naf"
			+", user.active AS active"
			+", contact_data.email AS email"
			+", contact_data.phone AS phone"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`contract` ON (  `domain`.`id` = `contract`.`domain` )"
			+ " INNER JOIN `" + database + "`.`person` ON (  `contract`.`person` = `person`.`registry` )"
			+ " INNER JOIN `" + database + "`.`registry` ON (  `person`.`registry` = `registry`.`id` )"
			+ " LEFT JOIN `" + database + "`.`user` ON (  `user`.`registry` = `registry`.`id` )"
			+ " LEFT JOIN `" + database + "`.`contact` ON (  `contact`.`user` = `user`.`id` )"
			+ " LEFT JOIN `" + database + "`.`contact_data` ON (  `contact`.`contact_data` = `contact_data`.`id` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

function dumpBank(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" rbank.id AS id"
			+", rbank.bank_account as name"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`rbank` ON (  `domain`.`id` = `rbank`.`domain` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

function dumpTaxModel(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" fs_model.id AS id"
			+", fs_model.model AS name"
			+", fs_model.status AS status"
			+", finance.pay_method AS paymentmethod"
			+", fs_model.result AS result"
			+", fs_model.period AS trimester"
			+", fs_model.year AS year"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`fs_model` ON (  `domain`.`id` = `fs_model`.`domain` )"
			+ " INNER JOIN `" + database + "`.`finance` ON (  `fs_model`.`finance` = `finance`.`id` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

function dumpInvoiceDocument(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" invoice_attach.id AS id"
			+", invoice_attach.data AS file"
			+", invoice_attach.description AS fileName"
			+", invoice_attach.mimeType AS fileType"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`invoice_attach` ON (  `domain`.`id` = `invoice_attach`.`domain` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

function dumpContractDocument(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" contract_attach.id AS id"
			+", contract_attach.data AS file"
			+", contract_attach.description AS fileName"
			+", contract_attach.mimeType AS fileType"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`contract_attach` ON (  `domain`.`id` = `contract_attach`.`domain` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

export default dump;
