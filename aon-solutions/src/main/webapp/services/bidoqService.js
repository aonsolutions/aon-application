import { API_URL_BIDOQ } from '../environments/environments.js';
import {getCompany}  from  './localStorageService.js';

export const getTypeUserBidoq = (data) =>{
  let name = "Cliente";
  switch(data){
    case 1:
      name = "Super";
    break;
    case 2:
      name = "Admin";
      break;
    case 3:
      name = "Asesor";
      break;
    case 4:
      name = "Auxiliar";
      break;
    case 5:
      name = "Comercial";
      break;
    case 6:
      name = "Cliente";
      break;
  }
  return name;
}

let sessionID;
let clienteID;
export const setSessionId = (data) => sessionID = data;
export const setClienteId = (data) => clienteID = data;
export const getSessionId = () => sessionID;
export const getClienteId = () => clienteID;

export const requestBidoq = (method, url, sendData, fn) => {
  let xhr = new XMLHttpRequest();
  xhr.open(method, url);
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.send(sendData);

  xhr.onload = () => {
    let response = formatResponse(xhr);
    if (xhr.status != 200 || !response) {
      // analyze HTTP status of the response
      console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
      fn(undefined, response);
    } else {
      // show the result
      console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
      fn(response);
    }
  };

  xhr.onprogress = (event) => {
    if (event.lengthComputable) {
      console.log(`Received ${event.loaded} of ${event.total} bytes`);
    } else {
      console.log(`Received ${event.loaded} bytes`); // no Content-Length
    }
  };

  xhr.onerror = () => {
    console.log("Request failed");
  };
};

export const getAccessBidoq = () => {
  const BIDOQ_URL_USER = 'https://mispapeles.es/api/aon/v1/index.php';
  const api_key = "K7>})(xQw~px_wgs#0=97..QGkBSxw*=.uatCfw[D.T{,fy.nrt?ok8jB@9}2}I";
  const company = getCompany();
  if(company && company.document){
    const newParams = {
      method: "get_company_login",
      document: company.document,
      api_key,
    }
    const sendData = new URLSearchParams(newParams).toString();
    return new Promise( (resolve, reject) => {
      requestBidoq('POST', BIDOQ_URL_USER, sendData, (result, error) => {
        if(error) {
          reject(error);
        } else {
          resolve(result);
        }
      });
    });
  }
  return null;
}

export const postBidoq = (data) => {
  const newParams = {
    "device_info": "phone",
    "app_code": "1",
    "operating_system_version": "4.2",
    "app_version": "1.0",
    "clienteID": data.clienteID || getClienteId(),
    "sessionID": data.sessionID || getSessionId(),
    ...data,
  };
  // Codificamos el objeto a una query string de URL
  const sendData = new URLSearchParams(newParams).toString();

  return new Promise( (resolve, reject) => {
    requestBidoq('POST', API_URL_BIDOQ, sendData, (result, error) => {
      if(error) {
        reject(error);
      } else {
        resolve(result);
      }
    });
  });
}

const formatResponse = (xhr) => { 
  let response = {message:""};
  try {
    if (xhr.response && typeof xhr.response === 'object') { 
      response =  xhr.response; 
    } else if (xhr.response && typeof xhr.response === 'string') { 
      response =  JSON.parse(xhr.response); 
    } else if (xhr.responseText) { 
      response =  JSON.parse(xhr.responseText); 
    } 
  } catch (error) {}

  return response;
} 