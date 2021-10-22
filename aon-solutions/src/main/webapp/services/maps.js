import { mobileAction, MOBILE_ACTION } from "./mobileService.js";

let position;

const getCurrent = () =>
  new Promise((resolve, reject) => {
    if (navigator.geolocation)
      navigator.geolocation.getCurrentPosition(resolve, reject, {
        enableHighAccuracy: true,
        maximumAge: 0,
      });
    else reject("browser no sopported");
  });

const successCallback = ({ coords, timestamp }) =>  ({ latitude: coords.latitude, longitude: coords.longitude, timestamp });

const errorCallback = (error) => {
  let msg = null;

  switch (error.code) {
    case error.PERMISSION_DENIED:
      msg = "El usuario denegó la solicitud de geolocalización.";
      break;
    case error.POSITION_UNAVAILABLE:
      msg = "La información de ubicación no está disponible.";
      break;
    case error.TIMEOUT:
      msg =
        "Se agotó el tiempo de espera de la solicitud para obtener la ubicación del usuario.";
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

export const getPosition = async () => {
  let result = null;
  const isApp = await mobileAction({ action: MOBILE_ACTION.SET_POSITION, times:2 });
  if (isApp) 
    result = await sleepPosition();
  else 
    result = await getCurrent().then(successCallback).catch((e) => e);

  if (result && result.code)  errorCallback(result);

  setPosition(undefined);

  console.log("position", position);
  console.log("result", result);

  return result;
};

export const setPosition = (pos) => {
  position = pos;
};

const sleepPosition = () => {
  return new Promise((resolve) => {
    let idInterval = setInterval(() => {
      if (position) {
        resolve(position);
        clearInterval(idInterval);
      }
    }, 300);
  });
};
