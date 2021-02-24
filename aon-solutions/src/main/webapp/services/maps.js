import { actionMobile } from "./actionMobile.js";

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

const successCallback = ({ coords, timestamp }) => {
  return { latitude: coords.latitude, longitude: coords.longitude, timestamp };
};

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
  }
  return msg;
};

export const getPosition = async () => {
  let result = null;
  let isApp = await actionMobile({ action: "setPosition" });

  if (isApp) {
    result = await sleepPosition();
  } else {
    try {
      result = await getCurrent().then(successCallback);
    } catch (e) {
      console.warn(errorCallback(e));
    }
  }
  return result;
};

export const setPosition = async (pos) => {
  position = pos;
};

const sleepPosition = () => {
  return new Promise((resolve, reject) => {
    let idInterval = setInterval(() => {
      if (position) {
        resolve(position);
        clearInterval(idInterval);
      }
    }, 300);
  });
};
