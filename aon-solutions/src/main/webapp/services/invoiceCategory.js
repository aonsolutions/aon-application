
export const getInvoiceCategories = (type) => {
  if('Emitida' === type) {
    return categoriesEmitida;
  } else if('Recibida' === type) {
    return categoriesRecibida;
  } else if('Ticket' === type) {
    return categoriesTicket;
  }
  return [];
}

const categoriesEmitida = [
  {
    value: '700.0',
    name: 'Ventas de mercaderías'
  },
  {
    value: '705.0',
    name: 'Prestación de servicios'
  }
];

const categoriesRecibida = [
  {
    value: '600.0',
    name: 'Compras de mercaderías'
  },
  {
    value: '607.0',
    name: 'Trabajos realizados por otras empresas'
  },
  {
    value: '621.0',
    name: 'Arrendamiento y cánones'
  },
  {
    value: '622.0',
    name: 'Reparaciones y convervación'
  },
  {
    value: '623.0',
    name: 'Servicios de profesionales independientes'
  },
  {
    value: '624.0',
    name: 'Transportes'
  },
  {
    value: '625.0',
    name: 'Primas de seguros'
  },
  {
    value: '626.0',
    name: 'Servicios bancarios y similares'
  },
  {
    value: '627.0',
    name: 'Publicidad, propaganda y relaciones públicas'
  },
  {
    value: '628.0',
    name: 'Suministros'
  },
  {
    value: '629.0',
    name: 'Otros gastos'
  },
  {
    value: '629.1',
    name: 'Alojamiento'
  },
  {
    value: '629.2',
    name: 'Aparcamiento'
  },
  {
    value: '629.3',
    name: 'Combustible'
  },
  {
    value: '629.4',
    name: 'Desplazamientos'
  },
  {
    value: '629.5',
    name: 'Dietas'
  },
  {
    value: '629.6',
    name: 'Peaje'
  }
];

const categoriesTicket = [
  {
    value: '629.0',
    name: 'Otros gastos'
  },
  {
    value: '629.1',
    name: 'Alojamiento'
  },
  {
    value: '629.2',
    name: 'Aparcamiento'
  },
  {
    value: '629.3',
    name: 'Combustible'
  },
  {
    value: '629.4',
    name: 'Desplazamientos'
  },
  {
    value: '629.5',
    name: 'Dietas'
  },
  {
    value: '629.6',
    name: 'Peaje'
  },
  {
    value: '629.7',
    name: 'Kilometraje'},
  {
    value: '629.8',
    name: 'Multas y sanciones'
  },
  {
    value: '629.9',
    name: 'Tasas y tributos'
  }
];
