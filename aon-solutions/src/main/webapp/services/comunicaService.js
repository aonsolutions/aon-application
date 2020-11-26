
import { post, get, openPDF } from "./request.js";
import { API_URL } from "../environments/environments.js";

// export const getMovements = () =>  new Promise((resolve) => {
//     resolve([
//         {
//             id: 1,
//             nombre: 'NOMBRE1',
//             last_name: 'APELLIDOS',
//             dni: '0Y777777X',
//             mov: 'Alta',
//             tipo_contrato: 'Jornada Parcial',
//             fecha: '21-01-2020'
//         },
//         {
//             id: 2,
//             nombre: 'NOMBRE2',
//             last_name: 'APELLIDOS',
//             dni: '0Y777777X',
//             mov: 'Alta',
//             tipo_contrato: 'Jornada completa',
//             fecha: '22-01-2020'
//         },
//         {
//             id: 3,
//             nombre: 'NOMBRE3',
//             last_name: 'APELLIDOS',
//             dni: '0Y777777X',
//             mov: 'Alta',
//             tipo_contrato: 'Jornada Parcial',
//             fecha: '23-01-2020'
//         },
//         {
//             id: 4,
//             nombre: 'NOMBRE4',
//             last_name: 'APELLIDOS',
//             dni: '0Y777777X',
//             mov: 'Alta',
//             tipo_contrato: 'Jornada completa',
//             fecha: '25-01-2020'
//         },
//         {
//             id: 5,
//             nombre: 'NOMBRE5',
//             last_name: 'APELLIDOS',
//             dni: '0Y777777X',
//             mov: 'Alta',
//             tipo_contrato: 'Jornada completa',
//             fecha: '25-01-2020'
//         },
//     ]);
// });

export const getWorkplaceCCCs = () => get(`${API_URL}/workplace_ccc`);

export const getMovements = () => get(`${API_URL}/comunica/movements`);

export const getIpfxnaf = (data) => get(`${API_URL}/comunica/ipfxnaf`, data);

export const getNafxipf = (data) => get(`${API_URL}/comunica/nafxipf`, data);

export const getPersonas = (dni) =>  new Promise((resolve) => {
    let jsonValues= [
        {
            id: 1,
            nombre: 'NOMBRE1',
            last_name1: 'APELLIDO1',
            last_name2: 'APELLIDO2',
            dni: '111',
            mov: 'Alta',
            naf: '123123131',
            tipo_contrato: 'Jornada Parcial',
            fecha: '21-01-2020'
        },
        {
            id: 2,
            nombre: 'NOMBRE2',
            last_name1: 'APELLIDO1',
            last_name2: 'APELLIDO2',
            dni: '222',
            mov: 'Alta',
            naf: '123123131',
            tipo_contrato: 'Jornada completa',
            fecha: '22-01-2020'
        },
        {
            id: 3,
            nombre: 'NOMBRE3',
            last_name1: 'APELLIDO1',
            last_name2: 'APELLIDO2',
            dni: '333',
            mov: 'Alta',
            naf: '123123131',
            tipo_contrato: 'Jornada Parcial',
            fecha: '23-01-2020'
        },
        {
            id: 4,
            nombre: 'NOMBRE4',
            last_name1: 'APELLIDO1',
            last_name2: 'APELLIDO2',
            dni: '444',
            mov: 'Alta',
            naf: '123123131',
            tipo_contrato: 'Jornada completa',
            fecha: '25-01-2020'
        },
        {
            id: 5,
            nombre: 'NOMBRE5',
            last_name1: 'APELLIDO1',
            last_name2: 'APELLIDO2',
            dni: '555',
            mov: 'Alta',
            naf: '123123131',
            tipo_contrato: 'Jornada completa',
            fecha: '25-01-2020'
        },
    ];
    let filters =  jsonValues.filter(f=>f.dni.indexOf(dni) >= 0).map(m=> {
        return {...m, value:m.dni, name:m.dni};
    });
    resolve(filters);

});

export const getTipoContrato = () =>  new Promise((resolve) => {
    resolve([
        {
            "id": "1",
            "name": "TC/Obra o Servicio determinado",
            "value": "401",
            "tipo_jornada": "0"
        }, {
            "id": "2",
            "name": "TC/Circunstancia de Producción",
            "value": "402",
            "tipo_jornada": "0"
        }, {
            "id": "3",
            "name": "TP/Obra o Servicio determinado",
            "value": "501",
            "tipo_jornada": "1"
        }, {
            "id": "4",
            "name": "TP/Circunstancia de Producción",
            "value": "502",
            "tipo_jornada": "1"
        }
    ]);
});

export const getTipoJornada = () =>  new Promise((resolve) => {
    resolve([
        {
            "id": "1",
            "name": "Semanal",
            "value": "semanal",
        }, {
            "id": "2",
            "name": "Diaria",
            "value": "diaria",
        }
    ]);
});

export const getCuentaCotizacion = () =>  new Promise((resolve) => {
    resolve([
        {value:1, name: '1111111111'},
        {value:2, name: '222222222'}
    ]);
});

export const getCentroTrabajo = () =>  new Promise((resolve) => {
    resolve([
        {value:1, name: 'PRINCIPAL'},
        {value:2, name: 'SECUNDARIO'}
    ]);
});

export const getGrupoCotizacion = () =>  new Promise((resolve) => {
    resolve([
        {
            value:'01',
            name: 'Alta Dirección'
        },
        {
            value:'02',
            name: 'Ingenieros y peritos'
        },
        {
            value:'03',
            name: 'Jefes admon. y taller'
        },
        {
            value:'04',
            name: 'Ayudante no titulados'
        },
        {
            value:'05',
            name: 'Oficial Administrativo'
        },
        {
            value:'06',
            name: 'Subalternos'
        },
        {
            value:'07',
            name: 'Aux. Administrativo'
        },
        {
            value: '08',
            name: 'Oficial 1ª y 2ª'
        },
        {
            value: '09',
            name: 'Oficial 3ª/especialista'
        },
        {
            value: '10',
            name: 'Peones'
        },
        {
            value: '11',
            name: 'Menores de 18 años'
        },
    ]);
});

export const getOcupacion = () =>  new Promise((resolve) => {
    resolve([
        {id:"1", name:"", value:""},
        {id:"2", name:"Trabajos de oficina", value:"a"},
        {id:"3", name:"Representantes comercio", value:"b"},
        {id:"4", name:"Trabajos construcción", value:"d"},
        {id:"5", name:"Conductores pasajeros y carga", value:"e"},
        {id:"6", name:"Conductores de carga > 3,5 Tm", value:"f"},
        {id:"7", name:"Personal de limpieza", value:"g"},
        {id:"8", name:"Personal de seguridad", value:"h"},
        {id:"9", name:"Personal de vuelo", value:"i"},
        {id:"10", name:"Dependientes. cajeros", value:"z"},
    ] );
});

export const getTA = (data) => openPDF(`${API_URL}/comunica/pdf/get-ta`, data);

export const getIDC = (data) => openPDF(`${API_URL}/comunica/pdf/get-idc`, data);

// export const getHorasConvenio = (tipo_jornada) =>  new Promise((resolve) => {
//     let jsonValues = [
//         {
//             "id": "1",
//             "name": "40",
//             "value": "40",
//             "tipo_jornada": "semanal"
//         },
//         {
//             "id": "2",
//             "name": "39",
//             "value": "39",
//             "tipo_jornada": "semanal"
//         },
//         {
//             "id": "3",
//             "name": "38",
//             "value": "38",
//             "tipo_jornada": "semanal"
//         },
//         {
//             "id": "4",
//             "name": "37",
//             "value": "37",
//             "tipo_jornada": "semanal"
//         },
//         {
//             "id": "5",
//             "name": "36",
//             "value": "36",
//             "tipo_jornada": "semanal"
//         },
//         {
//             "id": "6",
//             "name": "35",
//             "value": "35",
//             "tipo_jornada": "semanal"
//         },
//         {
//             "id": "7",
//             "name": "8.0",
//             "value": "8.0",
//             "tipo_jornada": "diaria"
//         },
//         {
//             "id": "8",
//             "name": "7.9",
//             "value": "7.9",
//             "tipo_jornada": "diaria"
//         },
//         {
//             "id": "9",
//             "name": "7.8",
//             "value": "7.8",
//             "tipo_jornada": "diaria"
//         },
//         {
//             "id": "10",
//             "name": "7.7",
//             "value": "7.7",
//             "tipo_jornada": "diaria"
//         },
//         {
//             "id": "11",
//             "name": "7.6",
//             "value": "7.6",
//             "tipo_jornada": "diaria"
//         },
//         {
//             "id": "12",
//             "name": "7.5",
//             "value": "7.5",
//             "tipo_jornada": "diaria"
//         }
//     ];
//     let filters =  jsonValues.filter(f=>f.tipo_jornada.indexOf(tipo_jornada) >= 0);
//     resolve(filters);
// });

export const postAltaDirecta = (data) => post(`${API_URL}/comunica/alta-directa`, data); //ALTA DIRECTA
export const postDeleteMov = (data) => post(`${API_URL}/comunica/delete-mov`, data);  //DELETE MOV

export const getConvenios = () =>  new Promise((resolve) => {
    resolve([
        {id:"1", name:"- Sin convenio definido", value:"60888888888888"}, 
    ] );
});