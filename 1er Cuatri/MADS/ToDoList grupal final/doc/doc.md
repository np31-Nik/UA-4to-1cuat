# Clases implementadas y modificadas
### Funcionalidades implementadas: 
+ Barra de menú facilita navegación por la página
+ UsuarioService nuevos métodos 
	+ Comprobar si existe un administrador
	+ Obtener lista de todos los usuarios
	+ Bloquear a un usuario
+ ListaUsuarioController
  + Mostrar listado de usuarios y los detalles de cada usuario
  + Control de roles de usuario
	  > solo administrador puede acceder
  + Bloquear y desbloquear usuarios
+ Modelo Usuario
	+ Bloqueado 
	+ Administrador
+ RegistroData
	+ Nuevo campo para registrarse como administrador 
		> solo puede haber un administrador
+ LoginController
	+ Comprobaciones para usuario bloqueado
		> Recibe error y no puede loguearse
	+ Comprobaciones para usuario admin
		> es redireccionado a la página de listado de usuarios

### Excepciones
+ UsuarioNotFound
	+ Se lanza en caso de que intentamos acceder por barra url a detalles de usuario que no existe. Lanza error 404.
+ UsuarioBloqueado
	+ Se lanza en caso de que un usuario está bloqueado e intenta logearse. En este caso recivibira error 403. 

### Plantillas añadidas
+ about.html
	+ contiene información sobre el proyecto
+ fragments.html
	+ Fragmentos communes a todas las páginas, incluyendo la barra de menu
		> Cambia dependiendo de si el usuario está logeado, mostrando su nombre y opciones de navegación o enlaces para registrarse o logearse.
+ listaUsuarios.html
	+ Muestra a los usuarios, el enlace para ver detalle y bloquear.
		> Un usuario (admin) no puede bloquearse a sí mismo
+ detalleUsuario.html
	+ Muestra todos los datos de usuario
	+ Existe opción para bloquear al usuairo

### Tests implementados
Se han añadido tests para comprobar correcto funcionamiento de los métodos  y plantillas implementados. Se testean tanto casos donde todo va bien como cuando se debe recibir un error.
+ AcercaDeWebTest 
+ BarraDeMenuWebTest
	+ Se controla que la barra cambia dependiendo de si el usuario está logueado o no
+ ListaUsuariosWebTest
	+ Contiene métodos para registrar diferentes usuarios
	+ Se prueba que podemos acceder a las páginas de listado y detalle si el usuario logueado es administrador y que en caso contrario se lanza error.
+ UsuarioServiceTest
	> Se comprueba que los 3 métodos añadidos al usuarioService funcionan correctamente
	+ Podemos obtener a todos los usuarios registrados
	+ Podemos comprobar si existen usuarios administradores en la BD
	+ Podemos bloquear y desbloquear usuario
+ UsuarioWebTest
	+ Comprobamos que podemos registrarnos como administrador en caso de que no existan
	+ Controlamos que un usuario bloqueado no puede acceder a la página

## Estructura de test de servicio
1. Ponemos datos iniciales
2. Ejecutamos servicio
3. Comprobamos que obtenemos resultado esperado.
```
@Test  
public void bloquearUsuario(){  
    Long user = addUsuarioBD();  
	assertThat(usuarioService.bloquearUsuario(user)).isTrue();  
    assertThat(usuarioService.bloquearUsuario(user)).isFalse();  
}
```
En este caso añadimos un usuario a la base de datos y comrobamos que lo podemos bloquear y desbloquear.

## Estructura de test de servicio
```
@Test  
public void getDetalleDeUsuario() throws Exception{  
  Long user = addUsuarioBD();  
  Long idAdmin = addUsuarioAdminBD();  
  when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);  
  
 this.mockMvc.perform(get("/registrados/"+ user.toString()))  
            .andExpect(content().string(containsString("Usuario Ejemplo")));  
}
```
1. Ponemos datos iniciales, un usuario normal y otro administrador.
2. Moqueamos un servicio, en este caso managerSeesion para que diga que somos administrados y permita accedernos a la página.
3. Entramos en la página y comprobamos que existe cadena esperada o que se ha lanzado un error correcto.

## Etructura de un controlador
```
@GetMapping("/bloquear/{idBloquear}")  
public String bloquearUsuario(@PathVariable(value="idBloquear") Long idBloquearUsuario, Model model, HttpSession session){  
    boolean admin = usuarioEsAdmin();  
	if (admin){  
		boolean bloqueado = usuarioService.bloquearUsuario(idBloquearUsuario);  
		model.addAttribute("id", this.usuario.getId());  
		model.addAttribute("usuario", this.usuario);  
		listado = usuarioService.listadoUsuarios();  
		model.addAttribute("usuarios", listado);  
		return "redirect:/registrados";  
	}  
    else{  
        throw new UsuarioNoLogeadoException();  
  }  
}
```
1. Indicamos el tipo de petición que se va a ejecutar (Get, Post, Put etc)
2. Comprobamos requerimientos iniciales. En este caso el usuario debe ser administrador. Si no se cumplen, se lanza error
3. Ejecutamos un servicio
4. Cargamos los datos necesarios para poder acceder desde la plantilla.
5. Por último, abrimos o redireccionamos a la página necesaria. 

## If unless en html
```
<span th:unless="${usuarioLista.getAdmin()}">  
	 <a class="btn btn-light btn-xs" th:href="@{/bloquear/{id}(id=${usuarioLista.id})}"/>  
		 <span th:if="${usuarioLista.getBloqueado()}">Desbloquear</span>  
		 <span th:unless="${usuarioLista.getBloqueado()}">Bloquear</span>  
	 </a>
 </span>
```
La función más utilizada y muy útil en thymeleaf es **if unless** (equivalente a if-else). Este es un ejemplo de como primero comprobamos que el usuario no es administrador. A continuacion, dependiendo de si el usuario está bloqueado o no el botón tendrá texto diferente. Este es muy cómodo y fácil de usar, además de que el código es facil de leer.


# Enlaces de interes
[GitHub](https://github.com/mads-ua-22-23/mads-todolist-sv54)
[ToDoList MADS | Trello](https://trello.com/b/xww3NmiV/todolist-mads)


	
