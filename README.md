# PROYECTO: MaMeti (APP)
***
El proyecto MaMeti es un ecosistema de distintas partes (un chaleco de rehabilitación, un dispositivo IoT para la recolección de información externa y una aplicación para dispositivos Android), la aplicación tiene como propósito visualizar la información que vaya recolectando el dispositivo IoT del ambiente y de la mascota, asi mismo se mantiene un contacto mas directo con veterinarios de cabecera o diferentes contactos de utilidad para salvaguardar el bienestar de la mascota.
***

## Tabla de Contenido
1. [Información General](#general-info)
2. [Tecnologias](#technologies)
3. [Instalación](#installation)

***

## 1. Información General

Status del proyecto: Finalizada la versión 1.0.0
Esta aplicación que es parte del ecosistema del proyecto MaMeti está enfocada principalmente en mostrar la información recopilada por el dispositivo IoT que está en el chaleco de rehabilitación para el constante monitoreo de la mascota, con la información recolectada por el dispositivo IoT la aplicación puede mostrar varios consejos en base a distintos escenarios que pueden ser de ayuda al usuario para poder prevenir escenarios de riesgo más catastróficos o peligrosos de los que la aplicación le está advirtiendo en determinado momento, ya sea por temperaturas muy altas o bajas, un ritmo cardiaco acelerado o presencia muy alta de humedad en el ambiente.

Otras funciones que contiene es el registro de contactos de emergencia, a los cuales se les podrá llamar por teléfono o enviar un correo electrónico, se cuenta con una búsqueda rápida con el uso de Google Maps para poder encontrar clínicas veterinarias cerca de nuestra ubicación actual. De una manera similar al guardado de contactos de emergencia para atender a la mascota se puede registrar información de la mascota y del responsable de la misma. Esta información es guardada en la nube, ya que se incluye un registro mediante correo electrónico o cuenta de Google para salvaguardar la seguridad de los datos.


Con un total de 8 pantallas diferentes:
1.	Login
2.	Crear perfil (En esta área se crea un perfil mediante un correo electrónico)
3.	Menú principal
4.	Monitoreo (Donde se muestran los datos recolectados con el dispositivo IoT)
5.	Perfil usuario (Donde se guardan todos los datos del responsable de la mascota, información como nombre completo, dirección, datos de contacto)
6.	Perfil mascota (Donde se guardan los datos de la mascota, como: nombre, raza, talla, edad, peso)
7.	Emergencia (Donde se guardan todos los contactos de emergencia que el usuario considere necesarios)
8.	Editar contactos de emergencia (En esta actividad, el usuario puede agregar, editar o eliminar los contactos que ya ha registrado en la aplicación)

[Galería de imágenes de la aplicación](https://1drv.ms/f/s!AiFt84JeRlx8gYFC6mJbC2uVUhaDdw?e=kPovA0)

***

## 2. Tecnologias

-	**La aplicación esta desarrollada en lenguaje de programación Kotlin**, elegido con la finalidad de compilar la aplicación de forma nativa en dispositivos Android.
-	**Base de datos NoSQL para el guardado de los datos de contacto, usuarios y mascota**: dentro del desarrollo es usado el servicio de Firestore (proporcionado por Google, dentro de su plataforma Firebase)
-	**Base de datos SQL para la comunicación con el dispositivo IoT**: Se configuro un servidor de base de datos SQL **(actualmente off-line)** para que el dispositivo IoT pudiera enviar los datos recolectados mediante APIs hechas con PHP y posteriormente pudieran ser mostrados dentro de la aplicación.
-	Dentro de la aplicación se integra el uso de otras aplicaciones del teléfono, tales como teléfono, Gmail y Google Maps, por lo cual estas deben estar instaladas en el dispositivo en el cual se instale la aplicación.

***

## 3. Instalación

El presente desarrollo de software tiene como objetivo la compilación de una aplicación en lenguaje Kotlin para uso de dispositivos con un **SO Android en su versión 7.0 y superiores**; [link de la aplicación compilada](https://1drv.ms/u/s!AiFt84JeRlx8gYFMC6o178EMbr6LeA?e=OkKvu6).
