import { API_URL_BIDOQ } from '../environments/environments.js';
import {getToken} from './request.js';

const BIDOQ_API_KEY = 'K7>})(xQw~px_wgs#0=97..QGkBSxw*=.uatCfw[D.T{,fy.nrt?ok8jB@9}2}I';

export const typeUserBidoq = {
  2 : "Admin",
  3 : "Asesor",
  6 : "Cliente",
}

let sessionID;
let clienteID;
export const setSessionId = (data) => sessionID = data;
export const setClienteId = (data) =>  clienteID = data;
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
  const token =  getToken();
  const BIDOQ_METHOD = 'get_user_login';
  // const BIDOQ_URL_USER = 'https://mispapeles.es/api/aon/v1/index.php';
  const sendData = `api_key=${BIDOQ_API_KEY}&method=${BIDOQ_METHOD}&session_id=${token}`;
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