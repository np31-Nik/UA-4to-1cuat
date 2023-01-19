#### Miembros del equipo 14:

- Nikita Polyanskiy
- Serhii Vidernikov
- Madani El Mrabet Martínez

# Práctica 4

## Cambios con respecto a la versión anterior

### Descripcion de equipo

- En el listado de equipos aparece un nuevo cambio para introducir la descripcion del equipo a crear.
- En los detalles de equipo aparecerá al descripcion del equipo.
- Actualizado EquipoData y Equipo, añadiendo el campo desc y metodos getDesc y setDesc.
- Añadido metodo cambiarDesc() para EquipoService.
- Actualizado ListadoEquiposController para guardar y obtener la descripcion del equipo.
- Actualizadas plantillas detalleEquipo (Mostrar descripcion) y equiposListado (Campo formulario para descripcion del
  euqipo).

### Navbar link listaEquipos quitado

Ahora al acceder a la aplicación sin estar logeado, no aparece la referencia al apartado de equipos. Antes aparecía el
link y saltaba excepción al intentar acceder sin logearse

### Navbar link a Registaros

Al entrar como administrador, en la barra de menu aparece enlace que lleva a la pagina con todos los registrados

## Despliegue de la aplicacion

La unica diferencia entre los esquemas de datos es que ahora la table Equipos disponde de una columna descripcion. Para migrar bases de datos se ha agregado script de migracion que añade la nueva columna. Puesto que es solo un cambio tenemos una unica instruccion:
```
ALTER TABLE public.equipos
    ADD COLUMN descripcion character varying(255)
```
Antes (schema-1.2.0.sql):
```
CREATE TABLE public.equipos (
id bigint NOT NULL,
name character varying(255)
);
```
Despues (schema-1.3.0.sql):
```
CREATE TABLE public.equipos (
    id bigint NOT NULL,
    name character varying(255),
    descripcion character varying(255)
);
```

Para aplicar los cambios, primero debemos montar un directorio compartido con la maquina docker. En nuestro caso el directorio se ha creado con nombre ``\\Program\ Files\\Git\\mi-host``, pero eso no importa mientras se pueda acceder.


Para ejecutar el script debemos ejecutar el comando `docker exec -it [maquina docker] bash`. Hay que mencionar que este comando da error si lo usamos en windows con GitBash, por lo que se ha realizado en windows powershell. Ahora que estamos dentro debemos ir al directorio que contiene el script y ejecutarlo con ``psql -U mads mads < /schema-1.2.0-1.3.0.sql``. Con esto tendermos la tabla equipos cambiada y podemos aplicar los cambios de nuestro compañero y despliegar la aplicacion con  ``./mvnw spring-boot:run -D profiles=postgres``. 

## Repositorios

- [Github](https://github.com/mads-ua-22-23/todolist-equipo-14)
- [Docker](https://hub.docker.com/repository/docker/sergswa7/mads-todolist-equipo14)
