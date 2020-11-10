
const result = [
    {
        id: 1,
        name: 'NOMBRE',
        last_name: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada Parcial',
        fecha: '21-01-2020'
    },
    {
        id: 2,
        name: 'NOMBRE',
        last_name: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '22-01-2020'
    },
    {
        id: 3,
        name: 'NOMBRE',
        last_name: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada Parcial',
        fecha: '23-01-2020'
    },
    {
        id: 4,
        name: 'NOMBRE',
        last_name: 'APELLIDOS', 
        dni: '0Y777777X',
        mov: 'Alta',
        naf: '123123131',
        tipo_contrato: 'Jornada completa',
        fecha: '25-01-2020'
    },
    {
        id: 5,
        name: 'NOMBRE',
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