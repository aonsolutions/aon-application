import { mobileAction, MOBILE_ACTION } from "./mobileService.js";

let position;

const getCurrent = () => new Promise((resolve, reject) => {
  if (navigator.geolocation)
    navigator.geolocation.getCurrentPosition(resolve, reject, {
      enableHighAccuracy: true,
      maximumAge: 0,
    });
  else reject("browser no sopported");
});

const successCallback = ({ coords, timestamp }) => ({ latitude: coords.latitude, longitude: coords.longitude, timestamp });

const errorCallback = (error) => {
  let msg = null;

  switch (error.code) {
    case error.PERMISSION_DENIED:
      msg = "El usuario denegó la solicitud de geolocalización.";
      break;
    case error.POSITION_UNAVAILABLE:
      msg = "La información de ubicación no está disponible.";
      break;
    case error.TIMEOUT: case "TIMEOUT":
      // msg ="Se agotó el tiempo de espera de la solicitud para obtener la ubicación del usuario.";
      break;
    case error.UNKNOWN_ERROR:
      msg = "Un error desconocido ocurrió.";
      break;
    case 1: case 999:
      msg = error.message || "Inactive location";
    break;
  }
  if (msg) 
    alert(msg);
};

export const getPosition = async () => getPositionResult();

export const setPosition = (p) => position = p;

const getPositionResult = async()=>{
  let result = null;
  const isApp = await mobileAction({ action: MOBILE_ACTION.SET_POSITION, times:2 });
  if (isApp) 
    result = await sleepPosition();
  else 
    result = await getCurrent().then(successCallback).catch((e) => e);

  if (result && result.code)  
    errorCallback(result);

  setPosition(undefined);

  console.log("result", result);

  return result;
}

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