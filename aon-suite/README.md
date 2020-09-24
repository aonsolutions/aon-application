# aon Suite

## Dependencias
- **Bootstrap 4.5.2**
- **bootstrap.bundle.min.js**: Librería JavaScript de Bootstrap
- **jQuery 3.5.1**
- **Chart.js 2.9.3**: Librería JavaScript para la visualización de gráficos con animaciones e interactivos, responsive y con soporte hasta IE11+

## Dependencias de desarrollo
- **gulp 4.0.2**: Gestor de tareas de desarrollo
- **gulp-concat**: Necesario para compilar el JS
- **browser-sync**: Necesario para recargar automáticamente el navegador cuando se guarda algún archivo

    - Al ejecutar la tarea `gulp watch` se nos abrirá una nueva pestaña en el navegador con la dirección `localhost:9000`, cualquier cambio que hagamos en ficheros .scss, .js o .html que se encuentren en los directorios configurados para ello, hará que se actualice el navegador automáticamente

    - Si tras ejecutar `gulp watch` accedemos a la dirección `localhost:9001`, podremos ver una interfaz que proporciona el paquete Browsersync con algunas herramientas de desarrollo, por ejemplo si desde el menú de esta interfaz pulsamos en la opción "Remote Debug" del menú de la izquierda, podremos activar "CSS Outlining", lo que añadirá bordes a todos los elementos de la página

    - Browsersync ofrece también la posibilidad de acceder a la app desde cualquier dispositivo que se encuentre conectado a nuestra red. Esta opción está desactivada por defecto, para activarla debemos comentar una línea del archivo `gulpfile.js`, al hacerlo nos aparecerá en la terminal una IP junto a "External URL:" y podremos utilizar dicha IP para acceder a la app desde otros dispositivos, la línea a comentar es la siguiente (se encuentra dentro de la función `sync`):

        ```js
        listen: 'localhost'
        ```

        Nota: para que este cambio surta efecto debemos terminar el proceso de gulp en la terminal, y volver a ejecutar `gulp watch`