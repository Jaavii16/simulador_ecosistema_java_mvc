# Simulador de Ecosistema en Java (Patrón MVC)

Proyecto académico para la asignatura "Tecnología de la Programación II" del Grado en Ingeniería Informática. El proyecto se desarrolló en dos fases principales, construyendo una aplicación completa desde la lógica del modelo hasta la interfaz gráfica de usuario.

* **Práctica 1:** Creación del **Modelo** de la simulación, aplicando principios de Diseño Orientado a Objetos (OOD), genéricos y colecciones.
* **Práctica 2:** Implementación de la **Vista** y el **Controlador**, aplicando el patrón arquitectónico Modelo-Vista-Controlador (MVC) y el patrón Observer para construir una GUI completa con Java Swing.

## Descripción General

La aplicación simula un ecosistema 2D compuesto por Lobos (carnívoros) y Ovejas (herbívoros). Los animales tienen comportamientos autónomos basados en su estado (normal, hambre, peligro, reproducción) y se mueven por un mapa dividido en regiones que proveen alimento.

La simulación se configura y se inicializa mediante archivos JSON. La interfaz gráfica permite al usuario cargar configuraciones, ejecutar la simulación paso a paso o de forma continua, detenerla y modificar las propiedades de las regiones en tiempo real.

## Características Principales

* **Lógica del Modelo:** Animales (Lobos, Ovejas) con estados y comportamientos (caza, huida, reproducción).
* **Gestión del Entorno:** Mapa dividido en regiones, con tipos como `DefaultRegion` y `DynamicSupplyRegion` que gestionan el alimento.
* **Patrón MVC:** Clara separación entre el modelo (lógica de simulación), la vista (GUI en Swing) y el controlador (gestión de eventos).
* **GUI Interactiva:**
    * Panel de control para cargar archivos, ejecutar, detener y modificar el delta-time de la simulación.
    * Tablas de datos actualizadas en tiempo real (estado de especies y regiones) mediante el patrón Observer.
    * Visor de mapa gráfico que dibuja la posición de los animales.
    * Diálogos para modificar las regiones de la simulación dinámicamente.
* **Configuración por JSON:** Uso de patrones Factory y Builder para crear animales y regiones a partir de archivos de configuración JSON.

## Conceptos y Tecnologías Aplicadas

* **Lenguaje:** Java (JDK 17)
* **Patrón Arquitectónico:** Modelo-Vista-Controlador (MVC)
* **Principios de Diseño:** Diseño Orientado a Objetos (OOD)
* **Patrones de Diseño:**
    * **Observer:** Para notificar a la GUI (Vista) los cambios en el simulador (Modelo).
    * **Factory (Builder-based):** Para la creación de entidades (animales, regiones).
    * **Strategy
