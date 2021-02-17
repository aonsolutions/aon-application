import { request, post, get, getToken} from "./request.js";
import { API_URL } from "../environments/environments.js";

let companies;
let company;

let domainUserRoles;


export const getCompanies = () => {
  return new Promise((resolve, reject) => {
    if (companies) {
      resolve(companies);
    } else {
      request(
        "GET",
        `${API_URL}/company`,
        localStorage.getItem("aon_session_id"),
        undefined,
        (r, error) => {
          if (error) {
            reject(error);
          } else {
            let result = JSON.parse(r);
            if (result) {
              companies = result;
            }
            resolve(companies ? companies : []);
          }
        }
      );
    }
  });
};

export const clearCompanies = () => {
  companies = undefined;
}

export const getCompany = () => {
  return new Promise((resolve, reject) => {
    if (!company && localStorage.getItem("aon_domain_id")) {
      getCompanies().then((companies) => {
        for (let i = 0; i < companies.length; i++) {
          if (companies[i].id == localStorage.getItem("aon_domain_id")) {
            setCompany(companies[i]);
          }
        }
      });
    }
    resolve(company);
  });
};

export const setDomainApp = (data) => post(`${API_URL}/company/app`, data);

export const getDomainNotice = (data) => get(`${API_URL}/company/notice`, data);

export const getCompanyOne = (data) => get(`${API_URL}/company/one`, data);
export const getCompanyMedia = (data) => get(`${API_URL}/company/media`, data);
export const getCompanyAddress = (data) => get(`${API_URL}/company/address`, data);

// export const getDomainUserRoles = (data) => get(`${API_URL}/company/approles`, data);

export const getDomainUserRoles = (data) => {
  let company = JSON.parse(localStorage.getItem("company"));

  return new Promise((resolve, reject) => {
    if (domainUserRoles && domainUserRoles.domain === company.id) {
      resolve(domainUserRoles);
    } else {
      request("GET", `${API_URL}/company/approles`, getToken(), data, (result, error) => {
        try{
          if (error) reject(error);
          else {
            domainUserRoles = JSON.parse(result);
            resolve(domainUserRoles);
          }
        } catch(e){reject(e);}
      });
    }
  });
}


export const getDomainApps = (domain) => {
    let d = true;
    if (!localStorage.getItem("aon_domain_name")) {
      localStorage.setItem("aon_domain_name", domain);
      d = false;
    }

    return new Promise((resolve, reject) => {
      request(
        "GET",
        `${API_URL}/company/app`,
        localStorage.getItem("aon_session_id"),
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
