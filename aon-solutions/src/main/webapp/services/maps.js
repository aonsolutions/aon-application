import { mobileAction, MOBILE_ACTION } from "./mobileService.js";

let position;

export const setPosition = (p) => position = p;

export const getPosition = async () => {
  let result = null;

  setPosition(undefined);

  const isApp = await mobileAction({ action: MOBILE_ACTION.SET_POSITION, times:2 });
  if (isApp) {
    result = await sleepPosition();
  } else {
    result = await getCurrentPosition().then(successCallback).catch((err) => ({code:(err.code || null), message:err.message}));
  }

  if (result && result.code)  {
    errorCallback(result);
  }

  return result && result.latitude && result.longitude ? result : null;
}

const getCurrentPosition = () => new Promise((resolve, reject) => {
  if (navigator.geolocation){

    let config = {
      enableHighAccuracy: true,
      maximumAge: 0
    }

    navigator.geolocation.getCurrentPosition(resolve, reject, config);

  } else {
    reject({message:"Browser no sopported", code:999});
  }
});

const successCallback = ({ coords, timestamp }) => ({ latitude: coords.latitude, longitude: coords.longitude, timestamp });

const errorCallback = (error) => {
  let msg = null;
  let timeout = false;

  switch (error.code) {
    case error.PERMISSION_DENIED:
      msg = "El usuario denegó la solicitud de geolocalización.";
    break;
    case error.POSITION_UNAVAILABLE:
      msg = "La información de ubicación no está disponible.";
    break;
    case error.TIMEOUT: case "TIMEOUT": case 3: 
      msg = "Se agotó el tiempo de espera de la solicitud para obtener la ubicación del usuario.";
      timeout = true;
    break;
    case error.UNKNOWN_ERROR:
      msg = "Un error desconocido ocurrió.";
    break;
    case 1: case 999:
      msg = error.message || "Ubicación inactiva";
    break;
  }

  if (msg) {
    throw {
      code:error.code,
      message: msg,
      timeout
    };
  }
};

const sleepPosition = () => {
  const max = 50; // 5 seg
  let i = 0;
  return new Promise((resolve) => {
    let idInterval = setInterval(() => {
      i++;
      if (position) {
        clearInterval(idInterval);
        resolve(position);
      } else if(i >= max){
        clearInterval(idInterval);
        resolve({code:"TIMEOUT"});
      }
    }, 100);// check every 100ms
  });
};