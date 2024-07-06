import java.util.*;

public class SistemaGestionPedidos {

    private static final String ESTADO_REGISTRADO = "Registrado";
    private static final String ESTADO_EN_PREPARACION = "En Preparación";
    private static final String ESTADO_LISTO_PARA_RECOGER = "Listo para Recoger";
    private static final String ESTADO_ENTREGADO = "Entregado";

    private static int idPedidoCounter = 1;
    private static final Map<Integer, Map<String, Object>> pedidos = new HashMap<>();
    private static final Map<String, Map<String, String>> empleados = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarEmpleados();
        boolean sistemaActivo = true;

        while (sistemaActivo) {
            Map<String, String> usuarioActual = login();
            if (usuarioActual != null) {
                boolean continuar = true;
                while (continuar) {
                    mostrarMenu(usuarioActual.get("rol"));
                    int opcion = scanner.nextInt();
                    scanner.nextLine();
                    switch (opcion) {
                        case 1:
                            if ("MESERO".equals(usuarioActual.get("rol"))) {
                                registrarPedido(usuarioActual);
                            } else {
                                System.out.println("Acceso denegado.");
                            }
                            break;
                        case 2:
                            if ("ASISTENTE_COCINA".equals(usuarioActual.get("rol")) || "ADMINISTRADOR".equals(usuarioActual.get("rol")) || "MESERO".equals(usuarioActual.get("rol"))) {
                                actualizarEstadoPedido(usuarioActual);
                            } else {
                                System.out.println("Acceso denegado.");
                            }
                            break;
                        case 3:
                            if ("MESERO".equals(usuarioActual.get("rol")) || "ADMINISTRADOR".equals(usuarioActual.get("rol")) || "ASISTENTE_COCINA".equals(usuarioActual.get("rol"))) {
                                consultarPedidos(usuarioActual);
                            } else {
                                System.out.println("Acceso denegado.");
                            }
                            break;
                        case 4:
                            if ("ADMINISTRADOR".equals(usuarioActual.get("rol"))) {
                                verEstadisticas();
                            } else {
                                System.out.println("Acceso denegado.");
                            }
                            break;
                        case 5:
                            System.out.println("Sesión cerrada.");
                            continuar = false;
                            break;
                        case 6:
                            System.out.println("⚠️ ¿Estás seguro de que quieres salir del sistema? (sí/no): Recuerda que si sales del sistema, se perderán todos los datos");
                            String respuesta = scanner.nextLine();
                            if (respuesta.equalsIgnoreCase("sí")) {
                                System.out.println("\uD83D\uDD1A Saliendo del sistema.... Hasta luego!");
                                continuar = false;
                                sistemaActivo = false;
                            } else if (respuesta.equalsIgnoreCase("no")) {
                                System.out.println("Volviendo al menú principal...");
                            } else {
                                System.out.println("❌ Opción no válida. Por favor, intente de nuevo.");
                            }
                            break;
                        default:
                            System.out.println("❌ Opción no válida. Por favor, intente de nuevo.");
                    }
                }
            } else {
                System.out.println("\uD83D\uDEA8 Error en el inicio de sesión. Intente nuevamente.");
            }
        }
    }

    private static void inicializarEmpleados() {
        empleados.put("mesero1", crearEmpleado("Diego", "1234", "MESERO"));
        empleados.put("mesero2", crearEmpleado("Alexander", "12345", "MESERO"));
        empleados.put("cocina1", crearEmpleado("Omar", "1234", "ASISTENTE_COCINA"));
        empleados.put("admin", crearEmpleado("Rafael", "admin", "ADMINISTRADOR"));
    }

    private static Map<String, String> crearEmpleado(String usuario, String contrasena, String rol) {
        Map<String, String> empleado = new HashMap<>();
        empleado.put("usuario", usuario);
        empleado.put("contrasena", contrasena);
        empleado.put("rol", rol);
        return empleado;
    }

    private static Map<String, String> login() {
        System.out.println("\n--- \uD83D\uDDA5\uFE0F Bienvenido al Sistema de Gestión de Pedidos \uD83E\uDDD1\u200D\uD83C\uDF73 ---");
        System.out.println("\n--- \uD83D\uDC68\u200D\uD83D\uDCBB Inicio de Sesión ---");
        System.out.print("\uD83D\uDE4E\uD83C\uDFFB\u200D♂️  Usuario: ");
        String usuario = scanner.nextLine();
        System.out.print("\uD83D\uDD11 Contraseña: ");
        String contrasena = scanner.nextLine();

        Map<String, String> empleado = empleados.get(usuario);
        if (empleado != null && empleado.get("contrasena").equals(contrasena)) {
            System.out.println("✅Inicio de sesión exitoso. Bienvenido, " + empleado.get("usuario") + ".");
            return empleado;
        }
        return null;
    }

    private static void mostrarMenu(String rol) {
        System.out.println("\n--- 📋 Sistema de Gestión de Pedidos ---");
        if ("MESERO".equals(rol)) {
            System.out.println("1️⃣ 1. Registrar Pedido (Rol: Mesero)");
            System.out.println("2️⃣ 2. Actualizar Estado de Pedido (Rol: Mesero)");
            System.out.println("3️⃣ 3. Consultar Pedidos (Rol: Mesero)");
        } else if ("ASISTENTE_COCINA".equals(rol)) {
            System.out.println("2️⃣ 2. Actualizar Estado de Pedido (Rol: Asistente de Cocina)");
            System.out.println("3️⃣ 3. Consultar Pedidos (Rol: Asistente de Cocina)");
        } else if ("ADMINISTRADOR".equals(rol)) {
            System.out.println("2️⃣ 2. Actualizar Estado de Pedido (Rol: Administrador)");
            System.out.println("3️⃣ 3. Consultar Pedidos (Rol: Administrador)");
            System.out.println("4️⃣ 4. Ver Estadísticas (Rol: Administrador)");
        }
        System.out.println("5️⃣ 5. Cerrar Sesión");
        System.out.println("6️⃣ 6. Salir del Sistema");
        System.out.print("Seleccione una opción: ");
    }

    private static void mostrarPlatosDisponibles() {
        System.out.println("\n--- 🍽️ Platos Disponibles ---");
        System.out.printf("%-3s %-20s %-10s %-40s%n", "#", "Plato", "Precio", "Descripción");
        System.out.println("--------------------------------------------------------------");
        String[][] platos = obtenerPlatos();
        for (int i = 0; i < platos.length; i++) {
            System.out.printf("%d. %s - %.2f - %s%n", i + 1, platos[i][0], Double.parseDouble(platos[i][1]), platos[i][2]);
        }
    }

    private static String[][] obtenerPlatos() {
        return new String[][] {
                {"Lomo Saltado", "25.0", "Delicioso lomo saltado tradicional."},
                {"Ají de Gallina", "20.0", "Sabroso ají de gallina con crema."},
                {"Seco de Cordero", "30.0", "Tradicional seco de cordero con arroz."},
                {"Ceviche", "18.0", "Fresco ceviche de pescado."},
                {"Jalea Mixta", "35.0", "Variedad de mariscos fritos."},
                {"Arroz con Mariscos", "22.0", "Arroz con mariscos frescos."},
                {"Tallarines Verdes", "15.0", "Tallarines con salsa de albahaca."},
                {"Tallarines a la Huancaína", "18.0", "Tallarines con salsa huancaína."},
                {"Chicha Morada", "5.0", "Refrescante bebida de maíz morado."},
                {"Pisco Sour", "12.0", "Tradicional cocktail peruano."}
        };
    }

    private static void registrarPedido(Map<String, String> mesero) {
        mostrarPlatosDisponibles();

        System.out.println("\n--- 📦 Registro de Pedido ---");
        System.out.print("⌨️ Ingrese el nombre del cliente: ");
        String nombreCliente = scanner.nextLine();
        System.out.print("⌨️ Ingrese el ID del cliente: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine();

        System.out.print("\uD83D\uDEF5 ¿El pedido es para llevar? (sí/no): ");
        String paraLlevar = scanner.nextLine();
        String direccion = "";
        int tiempoEntrega = 0;

        if (paraLlevar.equalsIgnoreCase("sí")) {
            System.out.print("\uD83C\uDFE0 \uD83D\uDCCD Ingrese la dirección del cliente: ");
            direccion = scanner.nextLine();
            System.out.print("\uD83D\uDEE3\uFE0F Ingrese la distancia en kilómetros: ");
            int distancia = scanner.nextInt();
            scanner.nextLine();
            tiempoEntrega = distancia * 5; // 5 minutos por kilómetro
        }

        boolean agregarPlatos = true;
        List<String> listaPlatos = new ArrayList<>();
        String[][] platosDisponibles = obtenerPlatos();

        while (agregarPlatos) {
            int opcionPlato;
            String platoSeleccionado;
            while (true) {
                System.out.print("\uD83C\uDF74 Seleccione el número del plato deseado: ");
                opcionPlato = scanner.nextInt();
                scanner.nextLine();
                if (opcionPlato > 0 && opcionPlato <= platosDisponibles.length) {
                    platoSeleccionado = platosDisponibles[opcionPlato - 1][0];
                    listaPlatos.add(platoSeleccionado);
                    break;
                } else {
                    System.out.println("❌ Opción no válida. Intente nuevamente.");
                }
            }

            System.out.print("🍽️❔¿Desea agregar otro plato? (sí/no): ");
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("no")) {
                agregarPlatos = false;
            }
        }

        Map<String, Object> pedido = new HashMap<>();
        pedido.put("idPedido", idPedidoCounter++);
        pedido.put("idCliente", idCliente);
        pedido.put("nombreCliente", nombreCliente);
        pedido.put("platos", listaPlatos);
        pedido.put("estado", ESTADO_REGISTRADO);
        pedido.put("registradoPor", mesero.get("usuario"));
        pedido.put("paraLlevar", paraLlevar.equalsIgnoreCase("sí"));
        pedido.put("direccion", direccion);
        pedido.put("tiempoEntrega", tiempoEntrega);

        pedidos.put((Integer) pedido.get("idPedido"), pedido);
        System.out.println("✅ Pedido registrado exitosamente:");
        mostrarCabeceraTabla();
        mostrarPedido(pedido);
    }

    private static void actualizarEstadoPedido(Map<String, String> usuarioActual) {
        System.out.println("\n--- 🔄 Actualización de Estado de Pedido ---");
        System.out.print("\uD83C\uDD94 Ingrese el ID del pedido: ");
        int idPedido = scanner.nextInt();
        scanner.nextLine();

        Map<String, Object> pedido = pedidos.get(idPedido);
        if (pedido == null) {
            System.out.println("❌ Pedido no encontrado.");
            return;
        }

        String estadoActual = (String) pedido.get("estado");
        System.out.println("⭐ Estado actual: " + estadoActual);
        if (ESTADO_LISTO_PARA_RECOGER.equals(estadoActual) && "ASISTENTE_COCINA".equals(usuarioActual.get("rol"))) {
            System.out.println("❌ Para cambiar el estado a 'Entregado', debe ser realizado por el mesero o administrador.");
            return;
        }

        System.out.println("\uD83C\uDD95 Seleccione el nuevo estado:");
        if (!ESTADO_ENTREGADO.equals(estadoActual)) {
            switch (estadoActual) {
                case ESTADO_REGISTRADO:
                    System.out.println("1. En Preparación");
                    break;
                case ESTADO_EN_PREPARACION:
                    System.out.println("2. Listo para Recoger");
                    break;
                case ESTADO_LISTO_PARA_RECOGER:
                    if ("MESERO".equals(usuarioActual.get("rol")) || "ADMINISTRADOR".equals(usuarioActual.get("rol"))) {
                        System.out.println("3. Entregado");
                    }
                    break;
            }

            System.out.print("Opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    if (ESTADO_REGISTRADO.equals(estadoActual)) {
                        pedido.put("estado", ESTADO_EN_PREPARACION);
                    }
                    break;
                case 2:
                    if (ESTADO_EN_PREPARACION.equals(estadoActual)) {
                        pedido.put("estado", ESTADO_LISTO_PARA_RECOGER);
                    }
                    break;
                case 3:
                    if (("MESERO".equals(usuarioActual.get("rol")) || "ADMINISTRADOR".equals(usuarioActual.get("rol"))) && ESTADO_LISTO_PARA_RECOGER.equals(estadoActual)) {
                        pedido.put("estado", ESTADO_ENTREGADO);
                    }
                    break;
                default:
                    System.out.println("Opción no válida. Estado no actualizado.");
                    return;
            }

            System.out.println("\uD83D\uDCC3 Estado del pedido actualizado:");
            mostrarCabeceraTabla();
            mostrarPedido(pedido);
        } else {
            System.out.println("❌ El pedido ya está entregado. No se puede actualizar más.");
        }
    }

    private static void consultarPedidos(Map<String, String> usuarioActual) {
        System.out.println("\n--- 🔍 Consulta de Pedidos ---");
        System.out.println("⏳1. Pedidos Pendientes");
        System.out.println("⌛2. Pedidos Completados");
        if ("ADMINISTRADOR".equals(usuarioActual.get("rol"))) {
            System.out.println("✅3. Todos los Pedidos");
        }
        System.out.print("\uD83D\uDCDD Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                if ("MESERO".equals(usuarioActual.get("rol"))) {
                    mostrarPedidos(usuarioActual, ESTADO_REGISTRADO);
                } else if ("ADMINISTRADOR".equals(usuarioActual.get("rol")) || "ASISTENTE_COCINA".equals(usuarioActual.get("rol"))) {
                    mostrarPedidos(usuarioActual, ESTADO_REGISTRADO, ESTADO_EN_PREPARACION, ESTADO_LISTO_PARA_RECOGER);
                }
                break;
            case 2:
                mostrarPedidos(usuarioActual, ESTADO_ENTREGADO);
                break;
            case 3:
                if ("ADMINISTRADOR".equals(usuarioActual.get("rol"))) {
                    mostrarPedidos(usuarioActual, ESTADO_REGISTRADO, ESTADO_EN_PREPARACION, ESTADO_LISTO_PARA_RECOGER, ESTADO_ENTREGADO);
                } else {
                    System.out.println("❌ Opción no válida.");
                }
                break;
            default:
                System.out.println("❌ Opción no válida.");
        }
    }

    private static void mostrarPedidos(Map<String, String> usuarioActual, String... estados) {
        mostrarCabeceraTabla();
        for (Map<String, Object> pedido : pedidos.values()) {
            if ("MESERO".equals(usuarioActual.get("rol")) && !pedido.get("registradoPor").equals(usuarioActual.get("usuario"))) {
                continue;
            }
            for (String estado : estados) {
                if (pedido.get("estado").equals(estado)) {
                    mostrarPedido(pedido);
                }
            }
        }
    }

    private static void mostrarCabeceraTabla() {
        System.out.printf("%-10s %-10s %-20s %-40s %-20s %-20s %-20s %-10s%n", "ID Pedido", "ID Cliente", "Nombre Cliente", "Platos", "Estado", "Registrado Por", "Para Llevar", "Tiempo Entrega");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private static void mostrarPedido(Map<String, Object> pedido) {
        StringBuilder platosConcatenado = new StringBuilder();
        List<String> platos = (List<String>) pedido.get("platos");
        for (String plato : platos) {
            platosConcatenado.append(plato).append(", ");
        }
        if (!platosConcatenado.isEmpty()) {
            platosConcatenado.setLength(platosConcatenado.length() - 2);
        }

        System.out.printf("%-10d %-10d %-20s %-40s %-20s %-20s %-20s %-10d%n", pedido.get("idPedido"), pedido.get("idCliente"), pedido.get("nombreCliente"),
                platosConcatenado, pedido.get("estado"), pedido.get("registradoPor"), (boolean) pedido.get("paraLlevar") ? "Sí" : "No", pedido.get("tiempoEntrega"));
    }

    private static void verEstadisticas() {
        System.out.println("\n--- 📊 Estadísticas ---");
        double totalVentas = 0;
        Map<String, Integer> contadorPlatos = new HashMap<>();
        for (Map<String, Object> pedido : pedidos.values()) {
            if (ESTADO_ENTREGADO.equals(pedido.get("estado"))) {
                List<String> platos = (List<String>) pedido.get("platos");
                for (String plato : platos) {
                    totalVentas += obtenerPrecioPlato(plato);
                    contadorPlatos.put(plato, contadorPlatos.getOrDefault(plato, 0) + 1);
                }
            }
        }
        System.out.printf("\uD83E\uDDEE Total de Ventas: %.2f\n", totalVentas);
        System.out.println("\uD83C\uDF7D\uFE0F Platos más vendidos:");
        for (Map.Entry<String, Integer> entry : contadorPlatos.entrySet()) {
            System.out.printf("%-20s %d\n", entry.getKey(), entry.getValue());
        }
    }

    private static double obtenerPrecioPlato(String nombrePlato) {
        return switch (nombrePlato) {
            case "Lomo Saltado" -> 25.0;
            case "Ají de Gallina" -> 20.0;
            case "Seco de Cordero" -> 30.0;
            case "Ceviche" -> 18.0;
            case "Jalea Mixta" -> 35.0;
            case "Arroz con Mariscos" -> 22.0;
            case "Tallarines Verdes" -> 15.0;
            case "Tallarines a la Huancaína" -> 18.0;
            case "Chicha Morada" -> 5.0;
            case "Pisco Sour" -> 12.0;
            default -> 0.0;
        };
    }
}
