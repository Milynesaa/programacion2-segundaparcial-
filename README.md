Sistema de Gestión de Clientes - Android
Aplicación Android nativa desarrollada en Java para la gestión integral de clientes, incluyendo registro con captura de fotografías, carga masiva de archivos con compresión, y un sistema robusto de auditoría y sincronización en segundo plano.
📋 Características Principales
1. Formulario de Registro de Cliente (Requerimiento 1)

Captura de información básica del cliente (CI, Nombre, Dirección, Teléfono).
Captura de tres fotografías distintas (fotoCasa1, fotoCasa2, fotoCasa3) utilizando la cámara del dispositivo.
Envío de datos del formulario en formato JSON junto a las imágenes.
Envío de toda la información en una única petición Multipart usando Retrofit.

2. Carga Múltiple de Archivos (Requerimiento 2)

Selección de múltiples archivos (documentos, videos, imágenes) desde el almacenamiento del dispositivo.
Compresión automática de todos los archivos seleccionados en un único fichero .zip.
Envío del archivo .zip junto con el CI del cliente a través de una petición Multipart.

3. Sistema de Auditoría Local (Requerimiento 3)

Base de datos local persistente implementada con Room.
Registro automático de eventos relevantes y errores (try-catch) en una tabla logs_app.
Interfaz para visualizar el historial completo de actividades con campos clave: ID, Fecha/Hora, Descripción del Evento y Clase de Origen.
Funcionalidad para actualizar la lista de logs y limpiar la base de datos local.

4. Sincronización Automática con WorkManager (Requerimiento 4)

Tarea periódica y robusta que se ejecuta en segundo plano, incluso si la app está cerrada.
Sincronización automática de todos los logs locales con el servidor a través de Retrofit.
Eliminación segura de los logs en el dispositivo una vez que se confirma la sincronización exitosa.
La tarea se ejecuta únicamente cuando hay conexión a internet para optimizar el uso de batería y datos.

🔧 Tecnologías Utilizadas

Java - Lenguaje de programación principal.
Retrofit 2 - Cliente HTTP para comunicación con la API REST.
Room Persistence Library - Abstracción sobre SQLite para una base de datos local robusta.
WorkManager - Para la gestión de tareas programadas y garantizadas en segundo plano.
View Binding - Para una interacción segura y eficiente con las vistas.
Material Design Components - Para una interfaz de usuario moderna y consistente.
Zip4j - Librería para la compresión de archivos en formato .zip.

🏗️ Estructura de la Aplicación
app/src/main/java/com/example/gestionclientes/
├── adapters/
│   └── LogsAdapter.java              // Adaptador para el RecyclerView de Logs
├── database/
│   ├── AppDatabase.java              // Clase principal de la base de datos Room
│   ├── dao/
│   │   └── LogAppDao.java            // Interfaz con las consultas a la DB
│   └── entity/
│       └── LogApp.java               // Entidad que representa la tabla logs_app
├── network/
│   ├── ApiService.java               // Interfaz de Retrofit con los endpoints
│   └── RetrofitClient.java           // Configuración del cliente Retrofit
├── utils/
│   ├── FileUtil.java                 // Utilidades para manejo de archivos y compresión
│   └── LogManager.java               // Clase centralizada para registrar logs
├── workers/
│   └── SyncLogsWorker.java           // Lógica de la tarea de sincronización
├── FormularioClienteActivity.java    // Lógica para el formulario de cliente
├── CargaArchivosActivity.java        // Lógica para la carga de archivos
├── LogsActivity.java                 // Lógica para la visualización de logs
└── MainActivity.java                 // Pantalla principal y menú
🚀 Instalación y Configuración
Requisitos Previos

Android Studio Iguana | 2023.2.1 o superior.
JDK 17 o superior.
Dispositivo o emulador con Android 5.0 (API 21) o superior.

Pasos de Instalación

Clonar el repositorio

bashgit clone  https://github.com/Milynesaa/programacion2-segundaparcial-.git

Abrir en Android Studio

File → Open → Seleccionar la carpeta del proyecto clonado.

Configurar el endpoint de prueba

Visita https://webhook.site/ para obtener una URL de prueba única.
Edita network/RetrofitClient.java y configura la URL base:
javaprivate static final String BASE_URL = "https://webhook.site/TU-ID-UNICO/";

Sincronizar dependencias de Gradle

Espera a que Android Studio descargue y sincronice todas las dependencias. Haz clic en "Sync Now" si es necesario.

Ejecutar la aplicación

Conecta un dispositivo físico (con depuración USB habilitada) o inicia un emulador.
Haz clic en Run → Run 'app' (▶).
📦 Dependencias Principales
Agregadas en build.gradle (Module: app):
gradledependencies {
    // UI y Componentes Principales
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // Retrofit (Cliente HTTP)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

    // Room (Base de Datos Local)
    implementation 'androidx.room:room-runtime:2.6.1'
    annotationProcessor 'androidx.room:room-compiler:2.6.1'

    // WorkManager (Tareas en Segundo Plano)
    implementation 'androidx.work:work-runtime:2.9.0'

    // Compresión de archivos
    implementation 'net.lingala.zip4j:zip4j:2.11.5'
}
🔐 Permisos Requeridos
Declarados en AndroidManifest.xml:
xml<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<!-- Para Android < 10, opcional para > 10 con Scoped Storage -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<uses-feature android:name="android.hardware.camera" android:required="true" />
📱 Uso de la Aplicación
Registrar un Cliente

Desde el menú principal, seleccionar "Formulario Cliente".
Completar los datos y usar los botones para capturar las tres fotografías.
Presionar "Enviar". Los datos y las imágenes se enviarán al endpoint configurado.

Cargar Archivos del Cliente

Seleccionar "Carga de Archivos" en el menú principal.
Ingresar el CI del cliente y presionar "Seleccionar Archivos".
Elegir uno o más archivos del dispositivo.
Presionar "Enviar Archivos". La app comprimirá todo en un .zip y lo enviará.

Consultar Logs de Auditoría

Seleccionar "Ver Logs" en el menú principal.
La pantalla mostrará el historial completo de eventos y errores registrados.
Usar "Actualizar" para refrescar la lista en cualquier momento.
Usar "Limpiar Logs" para eliminar todos los registros de la base de datos local.

🔄 Sincronización Automática
El sistema está diseñado para ser resiliente y eficiente:

La tarea de WorkManager se ejecuta cada 15 minutos (mínimo permitido por Android para tareas periódicas).
Si el dispositivo no tiene conexión a internet, la tarea espera a que la red esté disponible.
Si el envío al servidor falla, los logs se conservan localmente y se reintentará la sincronización en el próximo ciclo, garantizando que no se pierda información.

🐛 Manejo de Errores
Toda operación crítica (peticiones de red, acceso a la base de datos, manejo de archivos) está envuelta en bloques try-catch. Cualquier excepción es capturada y registrada automáticamente en la base de datos de Room a través de la clase LogManager.
Ejemplo:
javatry {
    // Operación de red o de archivo
} catch (Exception e) {
    LogManager.registrarEvento(context, "Error al realizar la operación: " + e.getMessage(), "NombreDeLaClase");
}
💾 Base de Datos
Esquema de la Tabla logs_app
sqlCREATE TABLE logs_app (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    fechaHora TEXT,
    descripcionError TEXT,
    claseOrigen TEXT
)
Operaciones DAO
java// Insertar un nuevo log
logAppDao.insertLog(logApp);

// Obtener todos los logs
logAppDao.getAllLogs();

// Eliminar todos los logs
logAppDao.deleteAll();

Versión: 1.0.0
Última actualización: Noviembre 2025ReintentarClaude puede cometer errores. Verifique las respuestas. Sonnet 4.5
