
const result = [
    {
        id: 1,
        nombre: 'NOMBRE',
        last_nombre: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada Parcial',
        fecha: '21-01-2020'
    },
    {
        id: 2,
        nombre: 'NOMBRE',
        last_nombre: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '22-01-2020'
    },
    {
        id: 3,
        nombre: 'NOMBRE',
        last_nombre: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada Parcial',
        fecha: '23-01-2020'
    },
    {
        id: 4,
        nombre: 'NOMBRE',
        last_nombre: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '25-01-2020'
    },
    {
        id: 5,
        nombre: 'NOMBRE',
        last_name: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '25-01-2020'
    },
];

export const getMovements = () =>  new Promise((resolve) => {
    resolve(result);
});



const personas = [
    {
        id: 1,
        nombre: 'NOMBRE',
        last_name1: 'APELLIDO1', 
        last_name2: 'APELLIDO2', 
        dni: 'Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada Parcial',
        fecha: '21-01-2020'
    },
    {
        id: 2,
        nombre: 'RAY',
        last_name1: 'VASQUEZ', 
        last_name2: 'BEAUPERTHUY', 
        dni: 'Y7514970X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '22-01-2020'
    },
    {
        id: 3,
        nombre: 'NOMBRE',
        last_name1: 'APELLIDO1', 
        last_name2: 'APELLIDO2', 
        dni: 'Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada Parcial',
        fecha: '23-01-2020'
    },
    {
        id: 4,
        nombre: 'NOMBRE',
        last_name1: 'APELLIDO1', 
        last_name2: 'APELLIDO2', 
        dni: 'Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '25-01-2020'
    },
    {
        id: 5,
        nombre: 'NOMBRE',
        last_name1: 'APELLIDO1', 
        last_name2: 'APELLIDO2', 
        dni: 'Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '25-01-2020'
    },
];

export const getPersonas = (dni) =>  new Promise((resolve) => {
    resolve(personas);
});