CREATE TABLE Curso 
(
id_curso  int not null,
Nombre nvarchar2 (20) not null,
Codigo NCHAR (10) not null,
CONSTRAINT id_curso
Primary Key (id_curso)
);


CREATE TABLE Estudiante
(
id_Estudiante int not null,
Nombre NVARCHAR2 (20) not null,
Apellido1 NVARCHAR2(20) not null,
Apellido2 NVARCHAR2 (20),
Correo NVARCHAR2 (30) not null,
Carrera NVARCHAR2 (20) not null,
CONSTRAINT pk_estudiante
PRIMARY KEY (id_Estudiante)
);

CREATE TABLE Inscripcion
(
id_Inscripcion int not null,
Fecha_ins DATE  not null,
curso int,
Estudiante int,
CONSTRAINT pk_inscripcion
PRIMARY KEY (id_Inscripcion),
CONSTRAINT fk_Inscripcion_Curso
FOREIGN KEY (curso)
REFERENCES Curso (id_curso),
CONSTRAINT fk_Inscripcion_Estudiante
FOREIGN KEY (Estudiante)
REFERENCES Estudiante (id_Estudiante)
);

INSERT INTO Curso (id_curso,Nombre,Codigo) 
VALUES (1,'Matematicas','ss12v43dcf');

INSERT INTO Curso (id_curso,Nombre,Codigo) 
VALUES (2,'Ciencias','ju64fr09ik');

INSERT INTO Curso (id_curso,Nombre,Codigo) 
VALUES (3,'Historia','nhr67');

INSERT INTO Estudiante (id_Estudiante,Nombre,Apellido1,Apellido2,Correo,Carrera) 
VALUES (1,'Jose Manuel','Martinez','Hernandez','josemanuel@gmail.com','Contabilidad');

INSERT INTO Estudiante (id_Estudiante,Nombre,Apellido1,Apellido2,Correo,Carrera) 
VALUES (2,'Ana sofia','Contreras','Fernandez','anasofia@gmail.com','Negocios');

INSERT INTO Estudiante (id_Estudiante,Nombre,Apellido1,Apellido2,Correo,Carrera) 
VALUES (3,'Martina','Flores','Peña','martina@gmail.com','Gastronimia');

INSERT INTO inscripcion(id_Inscripcion,Fecha_ins, curso, Estudiante)
VALUES (1,'23/06/25',1,2)

INSERT INTO  inscripcion(id_Inscripcion,Fecha_ins, curso, Estudiante)
VALUES (2,'23/06/25',1,1)

INSERT INTO inscripcion(id_Inscripcion,Fecha_ins, curso, Estudiante)
VALUES (3,'23/06/25',2,3)

INSERT INTO inscripcion(id_Inscripcion,Fecha_ins, curso, Estudiante)
VALUES (4,'23/06/25',2,2)

INSERT INTO inscripcion(id_Inscripcion,Fecha_ins, curso, Estudiante)
VALUES (5,'27/06/25',1,2)

INSERT INTO inscripcion(id_Inscripcion,Fecha_ins, curso, Estudiante)

 //Consulta especifica
SELECT id_Estudiante, Nombre, Apellido1, Apellido2, Correo
FROM Estudiante
WHERE Carrera ='Contabilidad';

//Select
SELECT * FROM Inscripcion;
//
SELECT * FROM Estudiante;
//
SELECT * FROM curso;

SELECT * FROM Estudiante
WHERE id_Estudiante = 3;

DELETE FROM Estudiante
WHERE id_Estudiante= 3;

DELETE FROM Inscripcion
WHERE Estudiante=3;

UPDATE Curso
SET Nombre = 'Historia Universal'
WHERE id_curso=3;
