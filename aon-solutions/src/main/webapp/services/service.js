import {request} from   './request.js';

import '../components/aon-desktop.js'

let companies;
let company;

let invoices;
let invoice;

let schema = 'first';
let page = 1;
let per_page = 50;
let end = false;

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
		request('POST', '/ms/api/login', undefined, data, undefined,  (token, error) => {
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
	request('POST', '/ms/api/remember', undefined, {email}, undefined, () => {});
}

export const getManifest = () => {
	return new Promise((resolve, reject) => {
		request('GET', '/ms/api/manifest', undefined, undefined, undefined, (result, error) => error ? reject(error) : resolve(result));
	});
}

// const handlerError = (result,error) = (resolve) => result;

export const getCompanies = () => {
  return new Promise( (resolve, reject) => {
		if(end) {
			resolve({companies:companies, end:true});
		} else {
			let headers = {
				schema: schema,
				page: page,
				per_page: per_page
			};
      request('GET', '/ms/api/company', localStorage.getItem('aon_session_id'),  undefined, headers, (r, error) => {
			  if(error) {
          reject(error);
        } else {
					let result = JSON.parse(r);
					if(result && result.companies) {
            if(companies){
              companies = companies.concat(result.companies);
            } else {
              companies = result.companies;
            }
        		companies.sort((a, b) => {
              if (a.name.toUpperCase() > b.name.toUpperCase()) {
                return 1;
              }
              if (a.name.toUpperCase() < b.name.toUpperCase()) {
                return -1;
              }
              // a must be equal to b
              return 0;
            });
            schema = result.schema;
            page = result.page;
            per_page = result.per_page;
            end = result.end;
						result.companies = companies;
						resolve(result);
          } else {
            resolve({companies:[], end: false});
          }
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
			request('GET', '/ms/api/company/app', localStorage.getItem('aon_session_id'),  undefined, undefined, (result, error) => {
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
			request('POST', '/ms/api/company/app', localStorage.getItem('aon_session_id'),  domainApp, undefined, (result, error) => {
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
			request('GET', '/ms/api/user/app', localStorage.getItem('aon_session_id'),  undefined, undefined, (result, error) => {
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
			request('POST', '/ms/api/user/app', localStorage.getItem('aon_session_id'),  userAppRole, undefined, (result, error) => {
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
      request('GET', '/ms/api/user', localStorage.getItem('aon_session_id'),  undefined, undefined, (result, error) => {
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
      request('POST', '/ms/api/user', localStorage.getItem('aon_session_id'), user, undefined, (result, error) => {
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
		request('GET', '/ms/api/invoice'  + getInvoiceQuery({id}), localStorage.getItem('aon_session_id'), undefined,  undefined, (result, error) => {
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
    request('GET', '/ms/api/invoice'  + getInvoiceQuery(data), localStorage.getItem('aon_session_id'), undefined,  undefined, (result, error) => {
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
  	request('POST', '/ms/api/invoice', localStorage.getItem('aon_session_id'), invoice,  undefined, (result, error) => {
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
  	request('DELETE', '/ms/api/invoice', localStorage.getItem('aon_session_id'), {id: invoiceIds}, undefined, (result, error) => {
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
