import { request, put, get, getDefaultSessionData} from "./request.js";
import { API } from "../environments/environments.js";
import * as LS from './localStorageService.js';

let companies;
let company;
let durum;

export const clearCompanyService = () => {
  clearCompanies();
  clearCompany();
  clearDurum();
}

export const clearCompanies = () => companies = undefined;
export const clearCompany = () => companies = undefined;
export const clearDurum = () => durum = undefined;

export const getDomainCompanies = (filter) => get(API.COMPANY, filter);

export const getCompanies = () => {
  return new Promise((resolve, reject) => {
    if (companies) {
      resolve(companies);
    } else {
      request("GET", API.COMPANY, getDefaultSessionData(), undefined, (r, error) => {
          if (error) {
            reject(error);
          } else {
            let result = JSON.parse(r);
            if (result) {
              result.sort(sortCompanies);
              companies = result;
            }
            resolve(companies ? companies : []);
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

export const setDomainApp = (data) => put(API.COMPANY_BOOKING, data);

export const getDomainNotice = (data) => get(API.COMPANY_NOTICE, data);

export const getCompanyOne = (data) => get(API.COMPANY_ONE, data);
export const getCompanyMedia = (data) => get(API.COMPANY_MEDIA, data);
export const getCompanyAddress = (data) => get(API.COMPANY_ADDRESS, data);

export const getCompanyBanks = (data) => get(API.COMPANY_BANKS, data);

export const getCompanyActivities = (data) => get(API.COMPANY_ACTIVITIES, data);

export const saveCompany = (data) => put(API.COMPANY, data)

// export const getDomainUserRoles = (data) => get(`${API_URL}/company/approles`, data);

export const getDomainUserRoles = (data) => {
  const domain = LS.getDomainId();
  return new Promise((resolve, reject) => {
	if (durum && durum.domain === domain && !data.reload)
      resolve(durum);
    else 
      get(API.COMPANY_APPROLES, data).then(r => {
        durum = r;
        resolve(r);
      }).catch(e => reject(e));
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
