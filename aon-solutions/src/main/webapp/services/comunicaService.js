
import { post } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getMovements = () =>  new Promise((resolve) => {
    resolve([
        {
            id: 1,
            nombre: 'NOMBRE1',
            last_name: 'APELLIDOS', 
            dni: '0Y777777X',
            mov: 'Alta',
            tipo_contrato: 'Jornada Parcial',
            fecha: '21-01-2020'
        },
        {
            id: 2,
            nombre: 'NOMBRE2',
            last_name: 'APELLIDOS', 
            dni: '0Y777777X',
            mov: 'Alta',
            tipo_contrato: 'Jornada completa',
            fecha: '22-01-2020'
        },
        {
            id: 3,
            nombre: 'NOMBRE3',
            last_name: 'APELLIDOS', 
            dni: '0Y777777X',
            mov: 'Alta',
            tipo_contrato: 'Jornada Parcial',
            fecha: '23-01-2020'
        },
        {
            id: 4,
            nombre: 'NOMBRE4',
            last_name: 'APELLIDOS', 
            dni: '0Y777777X',
            mov: 'Alta',
            tipo_contrato: 'Jornada completa',
            fecha: '25-01-2020'
        },
        {
            id: 5,
            nombre: 'NOMBRE5',
            last_name: 'APELLIDOS', 
            dni: '0Y777777X',
            mov: 'Alta',
            tipo_contrato: 'Jornada completa',
            fecha: '25-01-2020'
        },
    ]);
});

export const getPersonas = (dni) =>  new Promise((resolve) => {
    let jsonValues= [
        {
            id: 1,
            nombre: 'NOMBRE1',
            last_name1: 'APELLIDO1', 
            last_name2: 'APELLIDO2', 
            dni: 'Y11111X',
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
            dni: 'Y222X',
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
            dni: '33333X',
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
            dni: '333311X',
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
            dni: '123456789X',
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
    resolve(
        [{
            "id": "1",
            "name": "INDEFINIDO",
            "value": "100",
            "tipo_jornada": "0"
        }, {
            "id": "2",
            "name": "INDEFINIDO",
            "value": "109",
            "tipo_jornada": "0"
        }, {
            "id": "3",
            "name": "INDEFINIDO",
            "value": "130",
            "tipo_jornada": "0"
        }, {
            "id": "4",
            "name": "INDEFINIDO",
            "value": "131",
            "tipo_jornada": "0"
        }, {
            "id": "5",
            "name": "INDEFINIDO",
            "value": "139",
            "tipo_jornada": "0"
        }, {
            "id": "6",
            "name": "INDEFINIDO",
            "value": "141",
            "tipo_jornada": "0"
        }, {
            "id": "7",
            "name": "INDEFINIDO",
            "value": "150",
            "tipo_jornada": "0"
        }, {
            "id": "8",
            "name": "INDEFINIDO",
            "value": "151",
            "tipo_jornada": "0"
        }, {
            "id": "9",
            "name": "INDEFINIDO",
            "value": "189",
            "tipo_jornada": "0"
        }, {
            "id": "10",
            "name": "INDEFINIDO",
            "value": "200",
            "tipo_jornada": "1"
        }, {
            "id": "11",
            "name": "INDEFINIDO",
            "value": "209",
            "tipo_jornada": "1"
        }, {
            "id": "12",
            "name": "INDEFINIDO",
            "value": "230",
            "tipo_jornada": "1"
        }, {
            "id": "13",
            "name": "INDEFINIDO",
            "value": "231",
            "tipo_jornada": "1"
        }, {
            "id": "14",
            "name": "INDEFINIDO",
            "value": "239",
            "tipo_jornada": "1"
        }, {
            "id": "15",
            "name": "INDEFINIDO",
            "value": "241",
            "tipo_jornada": "1"
        }, {
            "id": "16",
            "name": "INDEFINIDO",
            "value": "250",
            "tipo_jornada": "1"
        }, {
            "id": "17",
            "name": "INDEFINIDO",
            "value": "251",
            "tipo_jornada": "1"
        }, {
            "id": "18",
            "name": "INDEFINIDO",
            "value": "289",
            "tipo_jornada": "1"
        }, {
            "id": "19",
            "name": "DURACIÓN DETERMINADA",
            "value": "401",
            "tipo_jornada": "0"
        }, {
            "id": "20",
            "name": "DURACIÓN DETERMINADA",
            "value": "402",
            "tipo_jornada": "0"
        }, {
            "id": "21",
            "name": "DURACIÓN DETERMINADA",
            "value": "403",
            "tipo_jornada": "0"
        }, {
            "id": "22",
            "name": "TEMPORAL",
            "value": "408",
            "tipo_jornada": "0"
        }, {
            "id": "23",
            "name": "DURACIÓN DETERMINADA",
            "value": "410",
            "tipo_jornada": "0"
        }, {
            "id": "24",
            "name": "DURACIÓN DETERMINADA",
            "value": "418",
            "tipo_jornada": "0"
        }, {
            "id": "25",
            "name": "TEMPORAL",
            "value": "420",
            "tipo_jornada": "0"
        }, {
            "id": "26",
            "name": "TEMPORAL",
            "value": "421",
            "tipo_jornada": "0"
        }, {
            "id": "27",
            "name": "TEMPORAL",
            "value": "430",
            "tipo_jornada": "0"
        }, {
            "id": "28",
            "name": "TEMPORAL",
            "value": "431",
            "tipo_jornada": "0"
        }, {
            "id": "29",
            "name": "TEMPORAL",
            "value": "441",
            "tipo_jornada": "0"
        }, {
            "id": "30",
            "name": "TEMPORAL",
            "value": "451",
            "tipo_jornada": "0"
        }, {
            "id": "31",
            "name": "DURACIÓN DETERMINADA",
            "value": "501",
            "tipo_jornada": "1"
        }, {
            "id": "32",
            "name": "DURACIÓN DETERMINADA",
            "value": "502",
            "tipo_jornada": "1"
        }, {
            "id": "33",
            "name": "DURACIÓN DETERMINADA",
            "value": "503",
            "tipo_jornada": "1"
        }, {
            "id": "34",
            "name": "TEMPORAL",
            "value": "508",
            "tipo_jornada": "1"
        }, {
            "id": "35",
            "name": "DURACIÓN DETERMINADA",
            "value": "510",
            "tipo_jornada": "1"
        }, {
            "id": "36",
            "name": "DURACIÓN DETERMINADA",
            "value": "518",
            "tipo_jornada": "1"
        }, {
            "id": "37",
            "name": "TEMPORAL",
            "value": "520",
            "tipo_jornada": "1"
        }, {
            "id": "38",
            "name": "TEMPORAL",
            "value": "530",
            "tipo_jornada": "1"
        }, {
            "id": "39",
            "name": "TEMPORAL",
            "value": "531",
            "tipo_jornada": "1"
        }, {
            "id": "40",
            "name": "TEMPORAL",
            "value": "540",
            "tipo_jornada": "1"
        }, {
            "id": "41",
            "name": "TEMPORAL",
            "value": "541",
            "tipo_jornada": "1"
        }, {
            "id": "42",
            "name": "TEMPORAL",
            "value": "551",
            "tipo_jornada": "1"
        }]
        );
});

export const getCuentaCotizacion = () =>  new Promise((resolve) => {
    resolve([
        {
            value:1,
            name: '1111111111'
        },
        {
            value:2,
            name: '222222222'
        }
    ]);
});

export const getGrupoCotizacion = () =>  new Promise((resolve) => {
    resolve([
        {
            value:'06',
            name: 'Subalternos'
        },
        {
            value: '08',
            name: 'Oficiales de primera y segunda'
        }
    ]);
});

export const getOcupacion = () =>  new Promise((resolve) => {
    resolve([
        {"id":"1","name":"Personal en trabajos exclusivos de oficina.","value":"a"}, 
        {"id":"2","name":"Tipo de cotización para todos los trabajadores que deban desplazarse habitualmente durante su jornada laboral, siempre que por razon de la ocupacion","value":"b"}, 
        {"id":"3","name":"Trabajadores en peridodo de baja por incapacidad temporal y otras situaciones con suspension de la relacion laboral con obligacion de cotizar.","value":"c"}, 
        {"id":"4","name":"Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construccion en general.","value":"d"}, 
        {"id":"5","name":"Conductores de vehiculo automovil de transporte de pasajeros en general (taxis, automoviles, autobuses, etc.) y de transporte de mercancias que tenga una capacidad de carga util no su","value":"e"}, 
        {"id":"6","name":"Conductores de vehiculo automovil de transporte de mercancias que tenga una capacidad de carga util superior a 3,5 Tm.","value":"f"}, 
        {"id":"7","name":"Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles.","value":"g"}, 
        {"id":"8","name":"Vigilantes, guardas, guardas jurados y personal de seguridad.","value":"h"}
    ] );
});

export const postAltaDirecta = (data) => post(`${API_URL}/comunica/alta-directa`, data);
