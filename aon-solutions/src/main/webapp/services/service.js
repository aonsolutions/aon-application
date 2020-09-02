import './request.js';
import '../components/aon-parent.js'
import '../components/aon-desktop.js'

let companies;
let company;

let invoices;
let invoice;

let schema = 'first';
let page = 1;
let per_page = 50;
let end = false;


window.closeSession = closeSession;
window.login = login;
window.getCompanies = getCompanies;
window.getCompany = getCompany;
window.companySelection = companySelection;
window.getUsers = getUsers;
window.setUser = setUser;
window.getInvoices = getInvoices;
window.getInvoice = getInvoice;
window.invoiceSelection = invoiceSelection;

window.getDomainApps = getDomainApps;
window.setDomainApp = setDomainApp;
window.getUserAppRole = getUserAppRole;
window.setUserAppRole = setUserAppRole;

window.getManifest = getManifest;

function closeSession() {
	localStorage.removeItem('aon_session_id');
	localStorage.removeItem('aon_domain_id');
	localStorage.removeItem('aon_domain_name');
	document.getElementById("aonLogin").style.display = 'block';
	document.getElementById("aonHome").style.display = 'none';
}

function login() {
	const username = document.getElementById("user").value;
	const password = document.getElementById("password").value;
	const data = {
			username: username,
			password: password
	}

	console.log(JSON.stringify(data));
	request('POST', '/ms/api/login', undefined, data, undefined, function (token, error) {
		localStorage.setItem('aon_session_id', JSON.parse(token).session_id);
		getCompanies().then(companies => {
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
		}).catch(error => {
			alert(error);
		});
	});
}

function getManifest() {
	return new Promise(function(resolve, reject){
		request('GET', '/ms/api/manifest', undefined, undefined, undefined, function(result, error) {
			resolve(result);
		});
	});
}

function companySelection(company) {
	let aonHeaderCompanyList = document.getElementById('aon-header-company-list');
	aonHeaderCompanyList.style.display = 'block';

	let aonHeaderHelp = document.getElementById('aon-header-help');
	aonHeaderHelp.style.display = 'block';

	let aonHeaderApps = document.getElementById('aon-header-apps');
	aonHeaderApps.style.display = 'none';

	let aonHeaderSearch = document.getElementById('aon-header-search');
	aonHeaderSearch.style.display = 'none';

	let aonHeaderHome = document.getElementById('aon-header-home');
	aonHeaderHome.style.display = 'block';

	let aonHeaderShowMenu = document.getElementById('aon-header-show-menu');
	aonHeaderShowMenu.style.display = 'block';

	let aonHeaderCompany = document.getElementById('aon-header-company');
	aonHeaderCompany.style.display = 'block';

	let aonHeaderCompanyName = document.getElementById('aon-header-company-name');
	aonHeaderCompanyName.innerHTML = company.name;

	let aonHeaderCompanyLogo = document.getElementById('aonHeaderCompanyLogo');
	let aonHeaderCompanyLogoImg = document.getElementById('aonHeaderCompanyLogoImg');
	if(company.logo){
		aonHeaderCompanyLogoImg.src = company.logo;
		aonHeaderCompanyLogo.style.display = 'block';
	} else {
		aonHeaderCompanyLogo.style.display = 'none';
	}

	let aonLogo = document.getElementById('aonLogo');
	let aonLogo2 = document.getElementById('aonLogo2');
	let aonLogoParent = document.getElementById('aonLogoParent');
	if(company.parentLogo){
		aonLogoParent.src = company.parentLogo;
		aonLogoParent.style.display = 'block';
		aonLogo2.style.display = 'block';
		aonLogo.style.display = 'none';
	} else {
		aonLogoParent.style.display = 'none';
		aonLogo2.style.display = 'none';
		aonLogo.style.display = 'block';
	}

	localStorage.setItem("aon_domain_id", company.id);
	localStorage.setItem("aon_domain_name", company.domain);

	getUserAppRole().then(user => {
		let aonMenu = document.getElementById('aonMenu');
		aonMenu.setAttribute('company', JSON.stringify(company));
		aonMenu.setAttribute('user', JSON.stringify(user));
		aonMenu.buildMenu();
		aonMenu.toogle();

		let aonHeader = document.getElementById('aonHeader');
		aonHeader.setAttribute('company', JSON.stringify(company));
		aonHeader.setAttribute('user', JSON.stringify(user));

		rootPanel('<aon-desktop id="aonDesktop"></aon-desktop>');
		let aonDesktop = document.getElementById('aonDesktop');
		aonDesktop.setAttribute('company', JSON.stringify(company));
		aonDesktop.setAttribute('user', JSON.stringify(user));
	});
}

function getCompanies() {
  return new Promise(function(resolve, reject){
		if(end) {
			resolve({companies:companies, end:true});
		} else {
			let headers = {
				schema: schema,
				page: page,
				per_page: per_page
			};
      request('GET', '/ms/api/company', localStorage.getItem('aon_session_id'),  undefined, headers, function (r, error) {
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
        		companies.sort(function (a, b) {
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

function getDomainApps(domain) {
	let d = true;
	if(!localStorage.getItem('aon_domain_name')){
		localStorage.setItem('aon_domain_name', domain)
		d = false;
	}

	return new Promise(function(resolve, reject){
			request('GET', '/ms/api/company/app', localStorage.getItem('aon_session_id'),  undefined, undefined, function (result, error) {
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

function setDomainApp(domainApp) {
	return new Promise(function(resolve, reject){
			request('POST', '/ms/api/company/app', localStorage.getItem('aon_session_id'),  domainApp, undefined, function (result, error) {
				if(error) {
					reject(error);
				} else {
					resolve(JSON.parse(result));
				}
			});
	 });
}

function getUserAppRole() {
	return new Promise(function(resolve, reject){
			request('GET', '/ms/api/user/app', localStorage.getItem('aon_session_id'),  undefined, undefined, function (result, error) {
				if(error) {
					reject(error);
				} else {
					resolve(JSON.parse(result));
				}
			});
	 });
}

function setUserAppRole(userAppRole) {
	return new Promise(function(resolve, reject){
			request('POST', '/ms/api/user/app', localStorage.getItem('aon_session_id'),  userAppRole, undefined, function (result, error) {
				if(error) {
					reject(error);
				} else {
					resolve(JSON.parse(result));
				}
			});
	 });
}

function getUsers() {
  return new Promise(function(resolve, reject){
      request('GET', '/ms/api/user', localStorage.getItem('aon_session_id'),  undefined, undefined, function (result, error) {
        if(error) {
          reject(error);
        } else {
          resolve(JSON.parse(result));
        }
   	  });
   });
}

function setUser(user) {
  return new Promise(function(resolve, reject){
      request('POST', '/ms/api/user', localStorage.getItem('aon_session_id'), user, undefined, function (result, error) {
        if(error) {
          reject(error);
        } else {
          resolve(JSON.parse(result));
        }
   	  });
   });
}

function getCompany() {
  return new Promise(function(resolve, reject){
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

function invoiceSelection(inv) {
	invoice = inv;
//	rootPanel('<aon-invoice></aon-invoice>');
}

// INVOICE

function getInvoice(id) {
	return new Promise(function(resolve, reject){
		request('GET', '/ms/api/invoice'  + getInvoiceQuery({id}), localStorage.getItem('aon_session_id'), undefined,  undefined,  function (result, error) {
			if(error) {
				reject(error);
			} else {
				resolve(JSON.parse(result));
			}
		});
	});
}

function getInvoices(data) {
  return new Promise(function(resolve, reject){
    request('GET', '/ms/api/invoice'  + getInvoiceQuery(data), localStorage.getItem('aon_session_id'), undefined,  undefined, function (result, error) {
      if(error) {
        reject(error);
      } else {
				resolve(JSON.parse(result));
    	}
  	});
  });
}

function insertInvoice(invoice) {
	return new Promise(function(resolve, reject){
  	request('POST', '/ms/api/invoice', localStorage.getItem('aon_session_id'), invoice,  undefined, function (result, error) {
			if(error) {
				reject(error);
			} else {
				resolve(JSON.parse(result));
			}
		});
	});
}

function deleteInvoices(invoiceIds) {
	return new Promise(function(resolve, reject){
  	request('DELETE', '/ms/api/invoice', localStorage.getItem('aon_session_id'), {id: invoiceIds}, undefined, function (result, error) {
			if(error) {
				reject(error);
			} else {
				resolve( JSON.parse(result));
			}
		});
	});
}

function getInvoiceQuery(params){
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







/*function getInvoice() {
  return new Promise(function(resolve, reject){
    resolve(invoice);
  });
}*/
