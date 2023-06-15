
function getEnterprises(){
	return new Promise((resolve, reject)=>{
		enterprises ? resolve(enterprises) : reject(enterprises);
	});
}

