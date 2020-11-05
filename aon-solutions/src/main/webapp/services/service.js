import {request} from   './request.js';

let companies;
let company;

let invoices;
let invoice;

let TOKEN = localStorage.getItem('aon_session_id')

export const closeSession = () => {
	localStorage.removeItem('aon_session_id');
	localStorage.removeItem('aon_domain_id');
	localStorage.removeItem('aon_domain_name');
	document.getElementById("aonLogin").style.display = 'block';
	document.getElementById("aonHome").style.display = 'none';
}

export const login = (data) => {
	console.log(JSON.stringify(data));
	return new Promise((resolve, reject) => {
		request('POST', '/ms/api/login', undefined, data, (token, error) => {
			if(error) {
				reject(error);
			} else {
				localStorage.setItem('aon_session_id', JSON.parse(token).session_id);
				document.getElementById("aonLogin").style.display = 'none';
				document.getElementById("aonHome").style.display = 'block';
				resolve(token);
			}
		});
	});
}

export const rememberPassword = (email) => {
	request('POST', '/ms/api/remember', undefined, {email}, () => {});
}

export const getManifest = () => {
	return new Promise((resolve, reject) => {
		request('GET', '/ms/api/manifest', undefined, undefined, (result, error) => error ? reject(error) : resolve(result));
	});
}

// const handlerError = (result,error) = (resolve) => result;

export const getCompanies = () => {
  return new Promise( (resolve, reject) => {
		if(companies) {
			resolve(companies);
		} else {
      request('GET', '/ms/api/company', localStorage.getItem('aon_session_id'),  undefined, (r, error) => {
			  if(error) {
          reject(error);
        } else {
					let result = JSON.parse(r);
					if(result) {
						companies = result;
          }
					resolve(companies ? companies : []);
        }
   	  });
	 	}
  });
}

export const getDomainApps = (domain) => {
	let d = true;
	if(!localStorage.getItem('aon_domain_name')){
		localStorage.setItem('aon_domain_name', domain)
		d = false;
	}

	return new Promise((resolve, reject) => {
			request('GET', '/ms/api/company/app', localStorage.getItem('aon_session_id'),  undefined, (result, error) => {
				if(!d) {
					localStorage.removeItem('aon_domain_name');
				}
				if(error) {
					reject(error);
				} else {
					resolve(JSON.parse(result));
				}
			});
	 });
}

export const setDomainApp = (domainApp) => {
	return new Promise((resolve, reject) => {
			request('POST', '/ms/api/company/app', localStorage.getItem('aon_session_id'),  domainApp, (result, error) => {
				if(error) {
					reject(error);
				} else {
					resolve(JSON.parse(result));
				}
			});
	 });
}

export const getUserAppRole = () => {
	return new Promise((resolve, reject) => {
			request('GET', '/ms/api/user/app', localStorage.getItem('aon_session_id'),  undefined, (result, error) => {
				if(error) {
					reject(error);
				} else {
					resolve(JSON.parse(result));
				}
			});
	 });
}

export const setUserAppRole = (userAppRole) => {
	return new Promise((resolve, reject) => {
			request('POST', '/ms/api/user/app', localStorage.getItem('aon_session_id'),  userAppRole, (result, error) => {
				if(error) {
					reject(error);
				} else {
					resolve(JSON.parse(result));
				}
			});
	 });
}

export const getAuth = () => {
  return new Promise((resolve, reject) => {
      request('GET', '/ms/api/auth', localStorage.getItem('aon_session_id'),  undefined, (result, error) => {
        if(error) {
          reject(error);
        } else {
          resolve(JSON.parse(result));
        }
   	  });
   });
}

export const getUsers = () => {
  return new Promise((resolve, reject) => {
      request('GET', '/ms/api/user', localStorage.getItem('aon_session_id'),  undefined, (result, error) => {
        if(error) {
          reject(error);
        } else {
          resolve(JSON.parse(result));
        }
   	  });
   });
}

export const setUser = (user) => {
  return new Promise((resolve, reject) => {
      request('POST', '/ms/api/user', localStorage.getItem('aon_session_id'), user, (result, error) => {
        if(error) {
          reject(error);
        } else {
          resolve(JSON.parse(result));
        }
   	  });
   });
}

export const getCompany = () => {
  return new Promise((resolve, reject) => {
    if(!company && localStorage.getItem('aon_domain_id')) {
      getCompanies().then(companies => {
          for(let i = 0; i < companies.length; i++){
           	if(companies[i].id == localStorage.getItem('aon_domain_id')){
            	setCompany(companies[i]);
            }
          }
      });
    }
    resolve(company);
  });
}

export const invoiceSelection = (inv) => {
	invoice = inv;
}

// INVOICE

export const getInvoice = (id) => {
	return new Promise((resolve, reject) => {
		request('GET', '/ms/api/invoice'  + getInvoiceQuery({id}), localStorage.getItem('aon_session_id'), undefined,  (result, error) => {
			if(error) {
				reject(error);
			} else {
				resolve(JSON.parse(result));
			}
		});
	});
}

export const getInvoices = (data) => {
  return new Promise((resolve, reject) => {
    request('GET', '/ms/api/invoice', localStorage.getItem('aon_session_id'), data, (result, error) => {
      if(error) {
        reject(error);
      } else {
			resolve(JSON.parse(result));
    	}
  	});
  });
}

export const insertInvoice = (invoice) => {
	return new Promise((resolve, reject) => {
  	request('POST', '/ms/api/invoice', localStorage.getItem('aon_session_id'), invoice, (result, error) => {
			if(error) {
				reject(error);
			} else {
				resolve(JSON.parse(result));
			}
		});
	});
}

export const deleteInvoices = (invoiceIds) => {
	return new Promise((resolve, reject) => {
  	request('DELETE', '/ms/api/invoice', localStorage.getItem('aon_session_id'), {id: invoiceIds}, (result, error) => {
			if(error) {
				reject(error);
			} else {
				resolve( JSON.parse(result));
			}
		});
	});
}

 export const get = (url, data) => {
	return new Promise((resolve, reject) => {
	  request('GET', url, TOKEN, data, (result, error) => {
		if(error) {
		  reject(error);
		} else {
			resolve(JSON.parse(result));
		}
		});
	});
  }
  
  export const post = (url, data) => {
	  return new Promise((resolve, reject) => {
		request('POST', url, TOKEN, data, (result, error) => {
			  if(error) {
				  reject(error);
			  } else {
				  resolve(JSON.parse(result));
			  }
		  });
	  });
  }
  
  export const remove = (url, data) => {
	  return new Promise((resolve, reject) => {
		request('DELETE', url, TOKEN, data, (result, error) => {
			  if(error) {
				  reject(error);
			  } else {
				  resolve( JSON.parse(result));
			  }
		  });
	  });
  }

const getInvoiceQuery = (params) => {
	let query = '';
	if(params.id) query = query + (query =='' ? '?id=' : '&id=') + params.id;
	if(params.document) query = query + (query =='' ? '?document=' : '&document=') + params.document;
	if(params.reference) query = query + (query =='' ? '?reference=' : '&reference=') + params.reference;
	if(params.toDate) query = query + (query =='' ? '?toDate=' : '&toDate=') + params.toDate;
	if(params.fromDate) query = query + (query =='' ? '?fromDate=' : '&fromDate=') + params.fromDate;
	if(params.type) query = query + (query =='' ? '?type=' : '&type=') + params.type;
	if(params.status) query = query + (query =='' ? '?status=' : '&status=') + params.status;
	if(params.q) query = query + (query =='' ? '?q=' : '&q=') + params.q;
	if(params.category) query = query + (query =='' ? '?category=' : '&category=') + params.category;
	if(params.last) query = query + (query =='' ? '?last=' : '&last=') + params.last;
	if(params.verified != undefined) query = query + (query =='' ? '?verified=' : '&verified=') + params.verified;

	return query
}
