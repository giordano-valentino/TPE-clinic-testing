# TPE - Clínica (Testing de Software)

Guía rápida para configurar el entorno y levantar la base de datos MySQL necesaria para ejecutar las pruebas del **Punto 2: Testing de Integración** (`BaseDeDatos`, `DataAccessObject` y `AsociadoDTO`).

---

## 📋 Requisitos de Conexión del Proyecto

El código de la aplicación ([`BaseDeDatos.java`](src/main/java/persistencia/BaseDeDatos.java)) espera conectarse con los siguientes parámetros:

* **Host:** `localhost`
* **Puerto:** `3306`
* **Usuario:** `progra_c`
* **Contraseña:** `progra_c`
* **Base de datos:** `Grupo_7`

> 💡 **Nota importante:** **NO** es necesario ejecutar scripts SQL para crear las tablas ni insertar datos a mano. La clase `BaseDeDatos` ejecuta automáticamente el método `iniciarBD()` al conectarse, creando la base `Grupo_7`, la tabla `Asociados` y los 20 registros semilla de prueba.

---

## 🚀 Opción 1: Levantar con Docker (Recomendado)

Esta es la forma estándar y más rápida. No ensucia tu sistema operativo ni requiere configurar usuarios manualmente.

### 1. Requisito previo
Tener instalado y abierto **Docker Desktop**.

### 2. Levantar el contenedor
Abrir una terminal en la raíz del proyecto y ejecutar:

```bash
docker compose up -d
```

*(Alternativa en una sola línea sin compose)*:
```bash
docker run -d --name mysql-clinic -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=Grupo_7 -e MYSQL_USER=progra_c -e MYSQL_PASSWORD=progra_c mysql:8.0
```

### 3. Comandos útiles de Docker
* **Ver si está corriendo:** `docker ps`
* **Pausar la base de datos:** `docker compose stop` (o `docker stop mysql-clinic`)
* **Volver a iniciarla:** `docker compose start` (o `docker start mysql-clinic`)
* **Apagar y eliminar el contenedor:** `docker compose down`

---

## 🛠️ Opción 2: Usar XAMPP (Alternativa sin Docker)

Si algún integrante del equipo no tiene Docker instalado, puede usar XAMPP:

### 1. Iniciar MySQL
1. Abrir **XAMPP Control Panel**.
2. En la fila de **MySQL**, hacer clic en **Start** (debe quedar en verde en el puerto `3306`).

### 2. Crear el usuario `progra_c` (solo la primera vez)
Por defecto XAMPP solo trae el usuario `root` sin contraseña. Hay que crear el usuario que espera el código:

1. En XAMPP Control Panel, hacer clic en el botón **Shell** (a la derecha).
2. Entrar a MySQL ejecutando:
   ```bash
   mysql -u root
   ```
3. Ejecutar las siguientes sentencias SQL:
   ```sql
   CREATE USER IF NOT EXISTS 'progra_c'@'localhost' IDENTIFIED BY 'progra_c';
   GRANT ALL PRIVILEGES ON *.* TO 'progra_c'@'localhost' WITH GRANT OPTION;
   FLUSH PRIVILEGES;
   EXIT;
   ```

---

## 🧪 Ejecución de las Pruebas

Una vez que MySQL esté corriendo (por Docker o XAMPP):

1. En tu IDE (Eclipse o IntelliJ), hacer clic derecho sobre el proyecto -> **Maven** -> **Update Project** (para asegurar que descargue la dependencia `mysql-connector-j` de `pom.xml`).
2. Ir a la suite de pruebas en `src/main/java/test/`.
3. Hacer clic derecho sobre la clase de test -> **Run As** -> **JUnit Test**.
