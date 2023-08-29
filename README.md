# TFG-Brais

## Índice

- [Introducción](#introduccion)
- [Guía de uso](#guia-de-uso)
  - [Común a todos los usuarios](#comun-a-todos-los-usuarios)
    - [Cambio de contraseña](#cambio-de-contraseña)
    - [Cerrar sesión](#cerrar-sesion)
  - [Funciones del Administrador](#funciones-del-administrador)
    - [Crear asignatura](#crear-asignatura)
    - [Ver información de la asignatura](#ver-informacion-de-la-asignatura)
    - [Editar asignatura](#editar-asignatura)
    - [Borrar asignatura](#borrar-asignatura)
  - [Funciones del Profesor](#funciones-del-profesor)
    - [Creación y edición de exámenes](#creacion-y-edicion-de-examenes)
    - [Importación de exámenes](#importacion-de-examenes)
    - [Exportación de exámenes](#exportacion-de-examenes)
    - [Ver entregas](#ver-entregas)
    - [Ver calificaciones](#ver-calificaciones)
  - [Funciones del alumno](#funciones-del-alumno)
    - [Realizar exámenes](#realizar-examenes)
    - [Ver calificaciones](#ver-calificaciones)
- [Guía de despliegue](#guia-de-despliegue)

## Introducción

Esta aplicación es un aula virtual pensada para realizar exámenes y que sean corregidos automáticamente por una inteligencia artificial.

## Guía de uso

### Comun a todos los usuarios

#### Cambio de contraseña

Todos los usuarios pueden cambiar su contraseña. Para cambiar la contraseña hay que seleccionar la opción para seleccionar la contraseña de la barra de navegación. En la página de cambio de contraseña tienes que rellenar los campos de contraseña y repetir contraseña. Estos no pueden ser nulos, deben tener una longitud superior a 8 caracteres y deben ser iguales.

![cambioPassword](./images/Cambiar_Password.png)

#### Cerrar sesión

Todos los usuarios pueden cerrar sesión. Para cerrar la sesión hay que seleccionar la opción de logout de la barra de navegación.

### Funciones del Administrador

El admin puede hacer lo que pueden hacer los usuarios comunes. Además pueden crear asignaturas, ver información de las asignaturas, editar asignaturas y borrar asignaturas.

#### Crear asignatura

Para crear una nueva asignatura hay que pulsar el botón nueva asignatura de la página principal. La nueva asignatura debe tener un nombre único (distinto a todas las existentes).
Se deben elegir a los profesores y los alumnos. Las asignaturas pueden crearse sin profesores y sin alumnos pero nunca sin nombre. Un usuario seleccionado como profesor no puede ser seleccionado como alumno y viceversa.

![Crear_Asignatura_1](./images/Crear_Asignatura_1.png)
![Crear_Asignatura_2](./images/Crear_Asignatura_2.png)

#### Ver informacion de la asignatura

Para ver la información de la asignatura hay que pulsar el icono del ojo respectivo a la asignatura en la página principal. Se puede ver el nombre de la asignatura y los profesores y estudantes asignados.

#### Editar asignatura[![](./docs/img/pin.svg)](#editar_asignatura)

Para editar la asignatura hay que pulsar el icono del lápiz respectivo a la asignatura en la página principal. Se puede editar el nombre de la asignatura y los profesores y alumnos asignados con las mismas restricciones que para la creación de las asignaturas.

#### Borrar asignatura[![](./docs/img/pin.svg)](#borrar_asignatura)

Para borrar una asignatura hay que pulsar el icono de la papelera respectivo a la asignatura en la página principal.

### Funciones del Profesor[![](./docs/img/pin.svg)](#funciones_profesor)

El profesor puede hacer lo que pueden hacer los usuarios comunes. El profesor puede realizar actividades relacionadas con la creación y edición de exámenes, calificación de exámenes e importación de exámenes.

#### Creación y edición de exámenes[![](./docs/img/pin.svg)](#creacion_examen)

La creación y edición de exámenes son básicamente iguales. Para crear un examen hay que pulsar el botón "Nuevo Examen" y para editar un examen hay que ir a la pantalla correspondiente al examen y pulsar el botón de "Editar".

Existen dos tipos de examen, los que consisten en preguntas cortas y los que consisten en la subida de un fichero. Estos son prácticamente iguales en las opciones de creación y edición que ofrecen pero tienen ciertas diferencias que se explicarán más tarde.

Para crear el examen, primero hay que rellenar el campo del nombre, este no puede ser vacío o igual que el de otro examen de la asignatura. También hay que seleccionar cúal es el porcentaje que contará el examen para la nota final.

El próximo campo a rellenar será el de selección del tipo de examen. Como comentamos antes, hay 2 tipos:

Fichero: se corresponde con un examen en el que el alumno tendrá que subir un archivo.

Preguntas cortas: en este tipo de examen los alumnos tienen que contestar una serie de preguntas cortas.

El apartado de selección de visibilidad del examen se refiere a si el alumno podrá ver el examen.

El apartado de selección de visibilidad de la nota se refiere a si el alumno podrá ver la nota cuando el examen ya está calificado.

El apartado de entregas tardías se refiere a si los alumnos podrán realizar entregas del examen una vez haya pasado el plazo.

Si se ha elegido la opción de examen de tipo subida de fichero habrá otra opción para seleccionar si el alumno puede subir un fichero aunque ya haya subido uno anterior.

Si se ha elegido la opción de examen de tipo de preguntas cortas habrá una opción para seleccionar el tiempo que tendrá el alumno para realizar el examen.

El apartado de selección de fechas y horas se refieren a cuando se abrirá el examen y cuando se cerrará el examen. La fecha de apertura debe ser anterior a la de cierre, y ambas nunca deben ser anteriores a la fecha actuál.

La opción para subir enunciado se usa para que el profesor pueda subir un fichero con cuestiones relacionadas con el examen.

Si se ha seleccionado la opción de un examen de preguntas cortas se abrirá una opción para establecer las preguntas. El número de preguntas debe ser superior a cero. Cada pregunta debe tener un enunciado y una puntuación.

#### Importación de exámenes[![](./docs/img/pin.svg)](#importacion_examen)

Se pueden importar exámenes de otras plataformas para que sean corregidos por la plataforma. Solamente se pueden importar exámenes de preguntas cortas de otras plataformas.

Para importar exámenes hay que seleccionar el botón de "Importar Examen" en la pantalla principal de la asignatura.

En la pantalla de importación de exámenes hay que seleccionar los mismos campos que para la creación de un examen de preguntas cortas, salvando el apartado de preguntas cortas que son importadas directamente. Hay un nuevo apartado para subir el fichero del que se va a importar. Cuando se importa un examen, se registra al alumno en caso de que no esté registrado (la contraseña establecida es el nombre del alumno seguido del apellido del alumno, ej: Alumno1 Apellido1 Apellido2 su contraseña sería Alumno1Apellido1 Apellido2).

#### Exportación de exámenes[![](./docs/img/pin.svg)](#exportacion_examen)

Los exámenes de preguntas cortas se pueden exportar para ser importados en otras plataformas. Para exportarlos hay que pulsar el botón "Exportar Examen" en la página principal del examen correspondiente.

#### Ver entregas[![](./docs/img/pin.svg)](#ver_entregas)

Los profesores pueden ver las entregas que han hecho los alumnos. Para esto tienen que pulsar el botón "Ver Entregas" en la página principal del examen.

En las entregas se puede ver el nombre del alumno, la fecha de entrega si se ha entregado y la calificación si ha sido calificado.

El profesor puede realizar distintas acciones:

Descargar la entrega: se debe pulsar el icono de la flecha para abajo correspondiente a la entrega.

Ver la entrega: esta opción solo está disponible si el examen es de preguntas cortas. En ella se pueden ver las respuestas y la calificación de cada pregunta en caso de haberla.

Borrar la entrega: esta opción sirve para borrar la entrega del alumno.

Calificar o editar la calificación: si no se ha calificado la entrega se podrá pulsar el botón para calificar, en caso de que haya sido calificada el botón servirá para editar la calificación. Si el examen es de preguntas cortas hay que calificar cada pregunta, si es de subida de fichero hay que calificar la entrega. En ambos casos se puede establecer un comentario sobre la tarea.

#### Ver Calificaciones[![](./docs/img/pin.svg)](#ver_calificaciones)

Los profesores pueden ver todas las calificaciones que han obtenidos todos los alumnos de las asignaturas. A esta pantalla se puede acceder pulsando el botón "Calificaciones" de la pantalla principal de la asignatura.

### Funciones del alumno[![](./docs/img/pin.svg)](#funciones_alumno)

Los alumnos pueden hacer lo que pueden hacer los usuarios comunes. Los alumnos pueden realizar exámenes y ver sus calificaciones.

#### Realizar exámenes[![](./docs/img/pin.svg)](#realizar_examenes)

Los alumnos pueden realizar los exámenes de preguntas cortas, para ello tienen que pulsar el botón realizar entrega en la página principal del examen. Se le redirigirá a la pantalla para realizar el examen, en ella se le mostrarán las preguntas, su valor y el tiempo necesario. Una vez realizado si el profesor lo ha elegido así podrá ver las respuestas y si ha sido calificado ver la calificación de cada pregunta.

Para realizar la entrega de un examen de subida de fichero es similar solo que hay que en ese caso se pide que se suba un fichero. En este caso podrá descargar su entrega.

#### Ver calificaciones[![](./docs/img/pin.svg)](#ver_calificaciones)

Los alumnos pueden ver las calificaciones que han obtenido en la asignatura. Para ello debe pulsar en el botón "Mis Calificaciones" de la página principal de la asignatura.

## Guia de despliegue