import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getDocument = (id) => get(`${API_URL}/documental`, { id });

export const getDocuments = (data) => get(`${API_URL}/documental/files`, data);

export const getCategories = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return get(`${API_URL}/attachment/${domainName}/${user}/category`, data);
};

export const createCategory = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/category/create`, data);
};

export const editCategory = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/category/edit/${data.id}`, data);
};

export const deleteCategory = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/category/delete/${data.id}`, data);
};

export const getScopes = (data) => get(`${API_URL}/scopes`, data);

export const getAeatCertificates = () => {
  const data = {type: 'AEAT'}
  return get(`${API_URL}/cert`, data);
}

export const getCertificates = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  data = data || {};
  data.domain = localStorage.getItem('aon_domain_id');
  return get(`${API_URL}/attachment/${domainName}/${user}/certificates`, data);
};

export const getTags = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return get(`${API_URL}/attachment/${domainName}/${user}/tag`, data);
};

export const createTag = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/tag/create`, data);
};

export const editTag = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/tag/edit/${data.id}`, data);
};

export const deleteTag= (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/tag/delete/${data.id}`, data);
};

export const uploadFileDocumental = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment_upload/${domainName}/${user}`, data);
}

export const updateFile = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/file/${data.id}`, data);
}

export const updateFiles = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/files`, data);
}

export const deleteFile = (data) => {
  let domainName = localStorage.getItem("aon_domain_name");
  let user = localStorage.getItem('aon_domain_login');
  return post(`${API_URL}/attachment/${domainName}/${user}/remove`, data);
}

export const insertDocument = (data) => post(`${API_URL}/documental`, data);

export const downloadDocuments = (data) => open(`${API_URL}/multiple_download/document?json=${data}`);

export const sendDocumentMail = (data) => post(`${API_URL}/send_mail/document`, data);