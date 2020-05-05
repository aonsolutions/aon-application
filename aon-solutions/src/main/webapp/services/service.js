import './request.js';
import '../components/aon-parent.js'
import '../components/aon-desktop.js'

let companies;
let company;

let invoices;
let invoice;

window.closeSession = closeSession;
window.login = login;
window.getCompanies = getCompanies;
window.getCompany = getCompany;
window.companySelection = companySelection;

window.getInvoices = getInvoices;
window.getInvoice = getInvoice;
window.invoiceSelection = invoiceSelection;

function closeSession() {
	localStorage.removeItem('session_id');
	localStorage.removeItem('domain_id');
	localStorage.removeItem('domain_name');
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
	request('POST', '/ms/apì/login', undefined, data, function (token, error) {

		localStorage.setItem('session_id', JSON.parse(token).session_id);
		getCompanies().then(companies => {
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
			if(JSON.parse(companies).length === 1) {
				localStorage.setItem('domain_id', JSON.parse(companies)[0].id);
				localStorage.setItem('domain_name', JSON.parse(companies)[0].domain);
				rootPanel('<aon-desktop></aon-desktop>');
			} else {
				rootPanel('<aon-parent></aon-parent>');
			}
		}).catch(error => {
			alert(error);
		});
	});
}

function companySelection(domainName, domainId) {
	localStorage.setItem("domain_id", domainId);
	localStorage.setItem("domain_name", domainName);
	rootPanel('<aon-desktop></aon-desktop>');
}

function getCompanies() {
  return new Promise(function(resolve, reject){
    if(companies) {
		resolve(companies);
    } else {
      request('GET', '/ms/api/company', localStorage.getItem('session_id'), undefined, function (result, error) {
        if(error) {
          reject(error);
        } else {
          companies = JSON.parse(result);
          resolve(companies);
        }
   	  });
   	 }
  });
}

function getCompany() {
  return new Promise(function(resolve, reject){
    if(!company && localStorage.getItem('domain_id')) {
      getCompanies().then(companies => {
          for(let i = 0; i < companies.length; i++){
           	if(companies[i].id == localStorage.getItem('domain_id')){
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
		request('GET', '/ms/api/invoice'  + getInvoiceQuery({id}), localStorage.getItem('session_id'), undefined, function (result, error) {
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
    request('GET', '/ms/api/invoice'  + getInvoiceQuery(data), localStorage.getItem('session_id'), undefined, function (result, error) {
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
  	request('POST', '/ms/api/invoice', localStorage.getItem('session_id'), invoice, function (result, error) {
			if(error) {
				reject(error);
			} else {
				resolve(JSON.parse(result));
			}
		});
	});
}

function deleteInvoices(invoiceIds) : Observable<Invoice> {
	return new Promise(function(resolve, reject){
  	request('DELETE', '/ms/api/invoice', localStorage.getItem('session_id'), {id: invoiceIds}, function (result, error) {
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







function getInvoice() {
  return new Promise(function(resolve, reject){
    resolve(invoice);
  });
}
