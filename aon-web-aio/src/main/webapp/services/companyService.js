import { request, put, get, post, getDefaultSessionData, getParentSessionData } from "./request.js";
import { API, API_URL } from "../environments/environments.js";
import * as LS from './localStorageService.js';

let durum;
let company;
let companies;

export const clearCompanyService = () => {
  clearCompanies();
  clearCompany();
  clearDurum();
}

export const clearCompanies = () => companies = undefined;
export const clearCompany = () => companies = undefined;
export const clearDurum = () => durum = undefined;

export const getDomainCompanies = (filter) => get(API.COMPANY, filter);

export const getParentCompany = (data) => get(API.COMPANY_ONE, data, getParentSessionData(data));

export const getCompanies = (data = { limit: 2147483647 }) => {
	return new Promise((resolve, reject) => {
		if (companies?.limit >= data.limit ) {
			resolve(companies.result.slice(0, Math.min(data.limit,companies.result.length)));
		} else {
			request("GET", API.COMPANY, getDefaultSessionData(), data, (r, error) => {
				if (error) {
					reject(error);
				} else {
					let limit = data?.limit;
					let result = JSON.parse(r);
					if (result) {
						result.sort(sortCompanies);
						companies = { result, limit };
					}
					resolve(result ? result : []);
				}
			}
			);
		}
	});
};

const typePriority = {
  OFFICE: 1,
  CONSULTANCY: 2,
  ENTERPRISE: 3,
  GARAGE: 4,
  ACADEMY: 5,
  HOTEL: 6,
  ADMIN: 7,
  GENERIC: 8,
  COMMERCE: 9,
  KIT_DIGITAL: 10
};

export const sortCompanies = (a, b) => {
  // Primero compara por prioridad de tipo
  if (typePriority[a.type] < typePriority[b.type]) {
    return -1;
  }
  if (typePriority[a.type] > typePriority[b.type]) {
    return 1;
  }
  // Si el tipo es el mismo, compara alfabéticamente por nombre
  return a.name.localeCompare(b.name);
}

export const getCompaniesBySchemas = (filter) => get(API.COMPANY_SCHEMAS, filter);

export const getCompany = () => {
  const domain = LS.getDomainId();
  return new Promise((resolve, reject) => {
    if(!domain) resolve({});
    else if (company && company.domain && domain == company.domain.id)
      resolve(company);
    else 
      get(API.COMPANY_ONE, {}).then(r => {
        company = r;
        resolve(r);
      }).catch(e => reject(e));
  });
}

export const setDomainApp = (data, sessionData) => put(API.COMPANY_BOOKING, data, sessionData);

export const getDomainNotice = (data) => get(API.COMPANY_NOTICE, data);

export const getCompanyOne = (data) => get(API.COMPANY_ONE, data);
export const getCompanyMedia = (data) => get(API.COMPANY_MEDIA, data);
export const getCompanyAddress = (data) => get(API.COMPANY_ADDRESS, data);

export const getCompanyBanks = (data) => get(API.COMPANY_BANKS, data);

export const getCompanyActivities = (data) => get(API.COMPANY_ACTIVITIES, data);

export const saveCompany = (data) => put(API.COMPANY, data)

export const getBookingDomainUserRoles = (data, sessionData) => get(API.COMPANY_APPROLES, data, sessionData);

export const getDomainUserRoles = (data) => {
  const domain = LS.getDomainId();
  return new Promise((resolve, reject) => {
	if (durum && durum.domain === domain && !data.reload)
      resolve(durum);
    else 
      get(API.COMPANY_APPROLES, data).then(r => {
        durum = r;
        resolve(r);
      }).catch(reject);
  });
}

export const updateDurDefinedUsers = (definedUsers) => durum.definedUsers = definedUsers;

export const getDomainApps = (domain) => {
    let d = true;
    if (!localStorage.getItem("aon_domain_name")) {
      localStorage.setItem("aon_domain_name", domain);
      d = false;
    }

    return new Promise((resolve, reject) => {
      request(
        "GET",
        API.COMPANY_APP,
        getDefaultSessionData(),
        undefined,
        (result, error) => {
          if (!d) {
            localStorage.removeItem("aon_domain_name");
          }
          if (error) {
            reject(error);
          } else {
            let apps = JSON.parse(result);
            apps.push('tools');
            resolve(apps);
          }
        }
      );
    });
  };

  export const getCompanyHeaderInfo = (data) => get(API.COMPANY_HEADER, data);

  export const getContratado = (data) => get(`${API_URL}/contracted_plans_servlet/apps`, data);
  export const sendFormData = (data) => post(`${API_URL}/contracted_plans_servlet/callForm`, data);
