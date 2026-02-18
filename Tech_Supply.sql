 CREATE DATABASE TechSupply;
 USE TechSupply;
CREATE TABLE sucursales (
     id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
     codigo VARCHAR(20) UNIQUE NOT NULL,
     ubicacion VARCHAR(150) NOT NULL
     );

 CREATE TABLE empleados (
     id_empleado INT AUTO_INCREMENT PRIMARY KEY,
     identificacion VARCHAR(50) UNIQUE NOT NULL,
     nombre VARCHAR(100) NOT NULL,
     cargo VARCHAR(100) NOT NULL,
     id_sucursal INT,
     FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal)
     );

 CREATE TABLE proveedores (
     id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
     nombre VARCHAR(100) NOT NULL,
     direccion VARCHAR(150),
     contacto_principal VARCHAR(100)
     );
CREATE TABLE proveedor_producto (
     id_proveedor INT,
     id_producto INT,
     PRIMARY KEY (id_proveedor, id_producto),
     FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
     FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
     );
 CREATE TABLE productos (
     id_producto INT AUTO_INCREMENT PRIMARY KEY,
     codigo VARCHAR(50) UNIQUE NOT NULL,
     nombre VARCHAR(100) NOT NULL,
     descripcion TEXT,
     precio_unitario DECIMAL(10,2) NOT NULL,
     cantidad_stock INT NOT NULL
     );
CREATE TABLE clientes (
     id_cliente INT AUTO_INCREMENT PRIMARY KEY,
     nombre VARCHAR(100) NOT NULL,
     direccion VARCHAR(150),
     tipo_cliente ENUM('individual', 'corporativo') NOT NULL
     );
CREATE TABLE pedidos (
     id_pedido INT AUTO_INCREMENT PRIMARY KEY,
     numero_pedido VARCHAR(50) UNIQUE NOT NULL,
     fecha_compra DATE NOT NULL,
     id_cliente INT NOT NULL,
     id_empleado INT,
     FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
     FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado)
     );
 CREATE TABLE detalle_pedido (
     id_pedido INT,
     id_producto INT,
     cantidad INT NOT NULL,
     precio DECIMAL(10,2) NOT NULL,
     PRIMARY KEY (id_pedido, id_producto),
     FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
     FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
     );
 CREATE TABLE facturas (
     id_factura INT AUTO_INCREMENT PRIMARY KEY,
     numero_factura VARCHAR(50) UNIQUE NOT NULL,
     fecha_emision DATE NOT NULL,
     monto_total DECIMAL(12,2) NOT NULL,
     estado_pago ENUM('pendiente', 'pagado') NOT NULL,
     id_pedido INT UNIQUE,
     id_cliente INT NOT NULL,
     FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
     FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
     );
INSERT INTO sucursales (codigo, ubicacion) value ('SUC-001', 'Zona 1');
INSERT INTO sucursales (codigo, ubicacion) value ("SUC-002", "El Tinajon");
INSERT INTO sucursales (codigo, ubicacion) value ("SUC-003", "Zona 5");
INSERT INTO sucursales (codigo, ubicacion) value ("SUC-004", "Mixco");
INSERT INTO empleados (identificacion, nombre, cargo, id_sucursal) value ("EMP-001", "Carlos Lopez", "Vendedor",1);
INSERT INTO empleados (identificacion, nombre, cargo, id_sucursal) value ("EMP-002", "Austin Santos", "Gerente",1);
INSERT INTO empleados (identificacion, nombre, cargo, id_sucursal) value ("EMP-003", "Luis Sanchez", "Vendedor",3);
INSERT INTO proveedores (nombre, direccion, contacto_principal) value ("Spirit", "Zona 6 diagnal 12", "Fernando Arreola");
INSERT INTO proveedores (nombre, direccion, contacto_principal) value ("CompuFacil", "Zona 1 4ta calle", "Clara Espinoza");
UPDATE proveedores Set direccion = "21 Av 3-13" WHERE nombre = "Spirit";
INSERT INTO productos (codigo, nombre, descripcion, precio_unitario, cantidad_stock) value ("0001", "Laptop Gamer", "Laptop muy poderosa", 1200.0, 15);
INSERT INTO productos (codigo, nombre, descripcion, precio_unitario, cantidad_stock) value ("0020","Mouse Inalambrico", "Mouse Comodo", 25.50, 100);
INSERT INTO productos (codigo, nombre, descripcion, precio_unitario, cantidad_stock) value ("0990", "Teclado Mecanico", "Teclado RGB", 75, 50);
 INSERT INTO clientes (nombre, direccion, tipo_cliente) value ("Juan Mendoza", "4ta calle zona 3", "individual" );
INSERT INTO clientes (nombre, direccion, tipo_cliente) value ("Empresa Digital S.A", "Zona 1", "Corporativo" );
INSERT INTO pedidos (numero_pedido,id_cliente, id_empleado, fecha_compra) value ("PED-189", 1, 1, NOW());
INSERT INTO facturas (numero_factura, fecha_emision, monto_total, estado_pago, id_pedido, id_cliente) value ("FAC-008", NOW(), 1252.00, "Pagado", 1,1);
INSERT INTO facturas (numero_factura, fecha_emision, monto_total, estado_pago, id_pedido, id_cliente) value ("FAC-009", NOW(), 232.00, "Pendiente", 2,2);
