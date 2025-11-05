# Simulador de Ecosistema en Java (Patrón MVC)

Proyecto para la asignatura "Tecnología de la Programación II". El proyecto se desarrolló en dos fases principales, construyendo una aplicación completa desde la lógica del modelo hasta la interfaz gráfica de usuario.

* [cite_start]**Práctica 1:** Creación del **Modelo** de la simulación, aplicando principios de Diseño Orientado a Objetos (OOD), genéricos y colecciones[cite: 2].
* [cite_start]**Práctica 2:** Implementación de la **Vista** y el **Controlador**, aplicando el patrón arquitectónico Modelo-Vista-Controlador (MVC) y el patrón Observer para construir una GUI completa con Java Swing[cite: 812].

## Descripción General

[cite_start]La aplicación simula un ecosistema 2D compuesto por Lobos (carnívoros) y Ovejas (herbívoros)[cite: 31, 32, 175, 245]. [cite_start]Los animales tienen comportamientos autónomos basados en su estado (normal, hambre, peligro, reproducción) [cite: 34, 79, 205, 217, 228, 271, 282, 300] [cite_start]y se mueven por un mapa dividido en regiones que proveen alimento[cite: 39, 41].

[cite_start]La simulación se configura y se inicializa mediante archivos JSON[cite: 49]. [cite_start]La interfaz gráfica permite al usuario cargar configuraciones, ejecutar la simulación paso a paso o de forma continua, detenerla y modificar las propiedades de las regiones en tiempo real[cite: 840, 844, 966, 1112].

## Características Principales

* [cite_start]**Lógica del Modelo:** Animales (Lobos, Ovejas) con estados y comportamientos (caza, huida, reproducción)[cite: 176, 246].
* [cite_start]**Gestión del Entorno:** Mapa dividido en regiones, que gestionan el alimento[cite: 346, 354].
* [cite_start]**Patrón MVC:** Clara separación entre el modelo (lógica de simulación), la vista (GUI en Swing) y el controlador (gestión de eventos)[cite: 838].
* **GUI Interactiva:**
    * [cite_start]Panel de control para cargar archivos, ejecutar, detener y modificar el delta-time de la simulación[cite: 966, 968].
    * [cite_start]Tablas de datos actualizadas en tiempo real (estado de especies y regiones) mediante el patrón Observer[cite: 906, 1085, 1099].
    * [cite_start]Visor de mapa gráfico que dibuja la posición de los animales[cite: 1201].
    * [cite_start]Diálogos para modificar las regiones de la simulación dinámicamente[cite: 1112].
* [cite_start]**Configuración por JSON:** Uso de patrones Factory y Builder para crear animales y regiones a partir de archivos de configuración JSON[cite: 422, 501, 703].

## Conceptos y Tecnologías Aplicadas

* [cite_start]**Lenguaje:** Java (JDK 17) 
* [cite_start]**Patrón Arquitectónico:** Modelo-Vista-Controlador (MVC) [cite: 812, 838]
* [cite_start]**Principios de Diseño:** Diseño Orientado a Objetos (OOD) [cite: 2]
* **Patrones de Diseño:**
    * [cite_start]**Observer:** Para notificar a la GUI (Vista) los cambios en el simulador (Modelo)[cite: 906].
    * [cite_start]**Factory (Builder-based):** Para la creación de entidades (animales, regiones)[cite: 501, 626].
    * [cite_start]**Strategy:** Para definir diferentes comportamientos de selección de animales (ej. `SelectClosest`, `SelectFirst`)[cite: 98, 106, 107].
* [cite_start]**Interfaz Gráfica (GUI):** Java Swing [cite: 812]
* [cite_start]**Estructuras de Datos:** Uso de Genéricos y Colecciones de Java[cite: 2].
* [cite_start]**Serialización:** Lectura y procesamiento de datos en formato JSON[cite: 49, 715].
