import { post, get, openFile } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getWorkplaceCCCs = () => get(`${API_URL}/workplace_ccc`);

export const getMovements = (data) =>  get(`${API_URL}/comunica/movements`);

export const getIpfxnaf = (data) => get(`${API_URL}/comunica/ipfxnaf`, data);

export const getNafxipf = (data) => get(`${API_URL}/comunica/nafxipf`, data);

export const getTA = (data) => openFile(`${API_URL}/comunica/pdf/get-ta`, data);

export const getCertCorriente = (data) =>
  openFile(`${API_URL}/comunica/pdf/cert-corriente`, data);

export const getIDC = (data) =>
  openFile(`${API_URL}/comunica/pdf/get-idc`, data);

export const postAltaDirecta = (data) =>
  post(`${API_URL}/comunica/alta-directa`, data); //ALTA DIRECTA
export const postUpdateCto = (data) =>
  post(`${API_URL}/comunica/update-contrato`, data); //ALTA DIRECTA
export const postDeleteMov = (data) =>
  post(`${API_URL}/comunica/delete-mov`, data); //DELETE MOV

export const getEmployee = (data) =>
  get(`${API_URL}/comunica/get-employee`, data);

export const getPersonas = (dni) =>
  new Promise((resolve) => {
    let jsonValues = [
      {
        id: 1,
        nombre: "NOMBRE1",
        last_name1: "APELLIDO1",
        last_name2: "APELLIDO2",
        dni: "Y11111X",
        mov: "Alta",
        nss: "123123131",
        tipo_contrato: "Jornada Parcial",
        fecha: "21-01-2020",
      },
      {
        id: 2,
        nombre: "NOMBRE2",
        last_name1: "APELLIDO1",
        last_name2: "APELLIDO2",
        dni: "Y2222X",
        mov: "Alta",
        nss: "123123131",
        tipo_contrato: "Jornada completa",
        fecha: "22-01-2020",
      },
      {
        id: 3,
        nombre: "NOMBRE3",
        last_name1: "APELLIDO1",
        last_name2: "APELLIDO2",
        dni: "Y3333X",
        mov: "Alta",
        nss: "123123131",
        tipo_contrato: "Jornada Parcial",
        fecha: "23-01-2020",
      },
      {
        id: 4,
        nombre: "NOMBRE4",
        last_name1: "APELLIDO1",
        last_name2: "APELLIDO2",
        dni: "Y5555X",
        mov: "Alta",
        nss: "123123131",
        tipo_contrato: "Jornada completa",
        fecha: "25-01-2020",
      },
      {
        id: 5,
        nombre: "NOMBRE5",
        last_name1: "APELLIDO1",
        last_name2: "APELLIDO2",
        dni: "Y6666X",
        mov: "Alta",
        nss: "123123131",
        tipo_contrato: "Jornada completa",
        fecha: "25-01-2020",
      },
    ];
    let filters = jsonValues
      .filter((f) => f.dni.indexOf(dni) >= 0)
      .map((m) => {
        return { ...m, value: m.dni, name: m.dni };
      });
    resolve(filters);
  });

export const getTipoContrato = () =>
  new Promise((resolve) => {
    resolve([
      {
        id: "1",
        name: "TC/Obra o Servicio determinado",
        value: "401",
        tipo_jornada: "0",
      },
      {
        id: "2",
        name: "TC/Circunstancia de Producción",
        value: "402",
        tipo_jornada: "0",
      },
      {
        id: "3",
        name: "TP/Obra o Servicio determinado",
        value: "501",
        tipo_jornada: "1",
      },
      {
        id: "4",
        name: "TP/Circunstancia de Producción",
        value: "502",
        tipo_jornada: "1",
      },
    ]);
  });

export const getTipoJornada = () =>
  new Promise((resolve) => {
    resolve([
      {
        id: "1",
        name: "Semanal",
        value: "semanal",
      },
      {
        id: "2",
        name: "Diaria",
        value: "diaria",
      },
    ]);
  });

export const getGrupoCotizacion = () =>
  new Promise((resolve) => {
    resolve([
      {
        value: "01",
        name: "Alta Dirección",
      },
      {
        value: "02",
        name: "Ingenieros y peritos",
      },
      {
        value: "03",
        name: "Jefes admon. y taller",
      },
      {
        value: "04",
        name: "Ayudante no titulados",
      },
      {
        value: "05",
        name: "Oficial Administrativo",
      },
      {
        value: "06",
        name: "Subalternos",
      },
      {
        value: "07",
        name: "Aux. Administrativo",
      },
      {
        value: "08",
        name: "Oficial 1ª y 2ª",
      },
      {
        value: "09",
        name: "Oficial 3ª/especialista",
      },
      {
        value: "10",
        name: "Peones",
      },
      {
        value: "11",
        name: "Menores de 18 años",
      },
    ]);
  });

export const getOcupacion = () =>
  new Promise((resolve) => {
    resolve([
      { id: "1", name: "", value: "" },
      { id: "2", name: "Trabajos de oficina", value: "a" },
      { id: "3", name: "Representantes comercio", value: "b" },
      { id: "4", name: "Trabajos construcción", value: "d" },
      { id: "5", name: "Conductores pasajeros y carga", value: "e" },
      { id: "6", name: "Conductores de carga > 3,5 Tm", value: "f" },
      { id: "7", name: "Personal de limpieza", value: "g" },
      { id: "8", name: "Personal de seguridad", value: "h" },
      { id: "9", name: "Personal de vuelo", value: "i" },
      { id: "10", name: "Dependientes. cajeros", value: "z" },
    ]);
  });


export const getConvenios = () =>
  new Promise((resolve) => {
    resolve([
      { id: "1", name: "- Sin convenio definido", value: "60888888888888" },
    ]);
  });


export const getAllTipoCtz = () =>
  new Promise((resolve) => {
    const json = [
      {
        id: 0,
        name: "Principal",
        value: 0,
        regimen: "0111",
      },
      {
        id: 1,
        name: "F y A",
        value: 1,
        regimen: "0111",
      },
      {
        id: 2,
        name: "Aprendizaje",
        value: 2,
        regimen: "0111",
      },
      {
        id: 3,
        name: "Asimilados",
        value: 3,
        regimen: "0111",
      },
      {
        id: 4,
        name: "Becarios",
        value: 4,
        regimen: "0111",
      },
      {
        id: 5,
        name: "Emp. hogar",
        value: 5,
        regimen: "0138",
      },
      {
        id: 6,
        name: "Agrarios",
        value: 6,
        regimen: "0163",
      },
      {
        id: 7,
        name: "Artistas",
        value: 7,
        regimen: "0112",
      },
    ];

    resolve(json);
  });

export const getTipoCtz = (data) =>
  new Promise(async (resolve) => {
    const json = await getAllTipoCtz();
    resolve(json.find((r) => r.value == data));
  });

