/* Populate tables */
INSERT INTO usuarios (id, email, nombre, password, fecha_nacimiento, admin, bloqueado) VALUES('1', 'user@ua', 'Usuario Ejemplo', '123', '2001-02-10', 'false', 'false');
INSERT INTO usuarios (id, email, nombre, password, fecha_nacimiento, admin, bloqueado) VALUES('2', 'user1@ua', 'Usuario Ejemplo', '123', '2001-02-10', 'false', 'false');
INSERT INTO usuarios (id, email, nombre, password, fecha_nacimiento, admin, bloqueado) VALUES('3', 'user2@ua', 'Usuario Ejemplo', '123', '2001-02-10', 'true', 'false');
INSERT INTO tareas (id, titulo, usuario_id, terminada, prioridad) VALUES('1', 'Lavar coche', '1', 'false', 0);
INSERT INTO tareas (id, titulo, usuario_id, terminada, prioridad) VALUES('2', 'Renovar DNI', '1', 'false', 0);

INSERT INTO equipos(id, descripcion, name) VALUES ('1', 'Equipos Ejemplo', 'Equipo1')