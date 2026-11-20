import java.util.Locale;
import java.util.Scanner;

class SAT_Machala_Sukito {

    // =====================================================================
    // FUNCIONES (Retornan un valor)
    // =====================================================================

    public static boolean validarLectura(double nivel, double alturaMaxCm) {
        return nivel >= 0.0 && nivel <= alturaMaxCm;
    }

    public static String clasificarRiesgo(double nivel) {
        // Umbrales referenciales (ajustar según medidas reales de la canaleta)
        if (nivel < 10.0) {
            return "NORMAL";
        } else if (nivel < 18.0) {
            return "PRECAUCION";
        } else if (nivel < 25.0) {
            return "ALERTA";
        } else {
            return "CRITICO";
        }
    }

    public static boolean evaluarRiegoDirecto(String estadoRiesgo, boolean sueloSeco) {
        // Pasa agua directo del canal a Sukito si el nivel está en ALERTA o CRITICO, o si el suelo está seco
        return "ALERTA".equals(estadoRiesgo) || "CRITICO".equals(estadoRiesgo) || sueloSeco;
    }

    // =====================================================================
    // SUBPROCESOS / PROCEDIMIENTOS (Ejecutan tareas sin devolver un valor)
    // =====================================================================

    public static void mostrarMenu(String estadoSistema) {
        System.out.println("==========================================================");
        System.out.println("       SISTEMA DE ALERTA TEMPRANA - SAT-MACHALA           ");
        System.out.println("       Estado del sistema: " + estadoSistema);
        System.out.println("==========================================================");
        System.out.println("1. Registrar (Nivel del canal / Riego directo a Sukito)");
        System.out.println("2. Consultar estado detallado de una zona");
        System.out.println("3. Visualizar estado general de las zonas");
        System.out.println("4. Identificar zonas en alerta o condicion critica");
        System.out.println("5. Consultar historial de lecturas");
        System.out.println("6. Mostrar estadisticas y resumen de alertas");
        System.out.println("7. Finalizar el programa");
        System.out.println("----------------------------------------------------------");
        System.out.print("Seleccione una opcion (1-7): ");
    }

    public static void mostrarEstadoGeneral(String[] nombresZonas, String[] estadosZonas, String[] riegoZonas) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("         ESTADO GENERAL DE ZONAS Y RIEGO DIRECTO SUKITO           ");
        System.out.println("==================================================================");
        System.out.println("ID | Zona / Canal          | Estado     | Riego Sukito");
        System.out.println("------------------------------------------------------------------");
        for (int i = 0; i < 4; i++) {
            System.out.println((i + 1) + "  | " + nombresZonas[i] + " | " + estadosZonas[i] + " | " + riegoZonas[i]);
        }
        System.out.println("==================================================================");
        System.out.println();
    }

    // =====================================================================
    // MÉTODO PRINCIPAL
    // =====================================================================

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US); // Permite usar punto decimal como 12.5

        // En Java los arreglos empiezan en índice 0 (zonas 0..3 corresponden a IDs 1..4)
        String[] zonas = {
            "Puerto Bolivar       ",
            "Las Brisas           ",
            "Urseza y Vergeles    ",
            "El Coco              "
        };
        String[] estados = new String[4];
        String[] riegoEstado = new String[4];

        // Matriz [3 rondas][4 zonas] para mantener la misma lógica de historial
        double[][] historial = new double[3][4];

        int opcion = 0;
        int subOpcion;
        int idZona;
        int datoBinario;
        int contadorAlertas = 0;
        int contadorRiegos = 0;

        double lecturaActual;
        double alturaMaxMaqueta = 30.0; // Ajustar según medida real de la canaleta en cm
        double totalNivel;
        double promedioNivel;

        boolean validacionOk;
        boolean sueloSeco;
        boolean riegoOn;
        String estadoActual;
        String txtRiego;

        for (int i = 0; i < 4; i++) {
            estados[i] = "NORMAL";
            riegoEstado[i] = "INACTIVO";
            for (int j = 0; j < 3; j++) {
                historial[j][i] = 5.0;
            }
        }

        // 1. MIENTRAS (while)
        while (opcion != 7) {
            mostrarMenu("OPERATIVO");
            if (!scanner.hasNextInt()) {
                System.out.println("\n[ERROR] Ingrese un numero valido del 1 al 7.\n");
                scanner.next();
                continue;
            }
            opcion = scanner.nextInt();

            // 2. SEGUN (switch)
            switch (opcion) {
                case 1:
                    System.out.println();
                    System.out.println("--- ¿QUE DESEA REGISTRAR? ---");
                    System.out.println("1. Lectura de nivel del agua en el canal");
                    System.out.println("2. Riego directo del canal a Sukito");
                    System.out.print("Seleccione una opcion (1-2): ");
                    subOpcion = scanner.nextInt();

                    switch (subOpcion) {
                        case 1:
                            System.out.println();
                            System.out.println("--- REGISTRO DE NIVEL DEL AGUA ---");
                            for (int i = 0; i < 4; i++) {
                                System.out.println("Zona " + (i + 1) + ": " + zonas[i].trim());
                            }
                            System.out.print("Ingrese el ID de la zona (1 a 4): ");
                            idZona = scanner.nextInt();

                            // 3. SI...ENTONCES...SINO (if / else)
                            if (idZona >= 1 && idZona <= 4) {
                                int idx = idZona - 1;
                                lecturaActual = 0.0;

                                // 4. REPETIR...HASTA QUE (do-while)
                                do {
                                    System.out.print("Ingrese el nivel de agua en cm (0.0 a " + alturaMaxMaqueta + "): ");
                                    lecturaActual = scanner.nextDouble();
                                    validacionOk = validarLectura(lecturaActual, alturaMaxMaqueta);

                                    // 5. SI...ENTONCES (if)
                                    if (!validacionOk) {
                                        System.out.println("[ERROR] Lectura fuera del rango de la maqueta. Intente nuevamente.");
                                    }
                                } while (!validacionOk);

                                estadoActual = clasificarRiesgo(lecturaActual);
                                estados[idx] = estadoActual;

                                // Desplaza rondas en la matriz historial
                                historial[0][idx] = historial[1][idx];
                                historial[1][idx] = historial[2][idx];
                                historial[2][idx] = lecturaActual;

                                System.out.println("[OK] Nivel registrado en " + zonas[idx].trim() + ": " + lecturaActual + " cm | Estado: " + estadoActual);

                                if ("ALERTA".equals(estadoActual) || "CRITICO".equals(estadoActual)) {
                                    contadorAlertas++;
                                    System.out.println("[ALERTA] Condicion de riesgo detectada en " + zonas[idx].trim());
                                }
                            } else {
                                System.out.println("[ERROR] ID de zona invalido. Debe ser entre 1 y 4.");
                            }
                            break;

                        case 2:
                            System.out.println();
                            System.out.println("--- REGISTRO DE RIEGO DIRECTO A SUKITO ---");
                            for (int i = 0; i < 4; i++) {
                                System.out.println("Zona " + (i + 1) + ": " + zonas[i].trim() + " (Estado: " + estados[i] + " | Riego: " + riegoEstado[i].trim() + ")");
                            }
                            System.out.print("Ingrese el ID de la zona (1 a 4): ");
                            idZona = scanner.nextInt();

                            if (idZona >= 1 && idZona <= 4) {
                                int idx = idZona - 1;
                                System.out.print("Esta seco el suelo de Sukito? (1 = SI / 0 = NO): ");
                                datoBinario = scanner.nextInt();
                                sueloSeco = (datoBinario == 1);

                                riegoOn = evaluarRiegoDirecto(estados[idx], sueloSeco);

                                if (riegoOn) {
                                    txtRiego = "REGADO ";
                                    contadorRiegos++;
                                } else {
                                    txtRiego = "INACTIVO";
                                }
                                riegoEstado[idx] = txtRiego;

                                System.out.println("[OK] Riego directo de Sukito en " + zonas[idx].trim() + ": " + txtRiego.trim());
                            } else {
                                System.out.println("[ERROR] ID de zona invalido. Debe ser entre 1 y 4.");
                            }
                            break;

                        default:
                            System.out.println("[ERROR] Subopcion no valida. Ingrese 1 o 2.");
                            break;
                    }
                    System.out.println();
                    break;

                case 2:
                    System.out.println();
                    System.out.println("--- CONSULTAR ESTADO DE UNA ZONA ---");
                    for (int i = 0; i < 4; i++) {
                        System.out.println("Zona " + (i + 1) + ": " + zonas[i].trim());
                    }
                    System.out.print("Ingrese el ID de la zona a consultar (1 a 4): ");
                    idZona = scanner.nextInt();

                    if (idZona >= 1 && idZona <= 4) {
                        int idx = idZona - 1;
                        System.out.println("--------------------------------------------------");
                        System.out.println("Zona: " + zonas[idx].trim());
                        System.out.println("Ultimo nivel registrado: " + historial[2][idx] + " cm");
                        System.out.println("Estado actual: " + estados[idx]);
                        System.out.println("Riego directo a Sukito: " + riegoEstado[idx].trim());
                        System.out.println("--------------------------------------------------");
                    } else {
                        System.out.println("[AVISO] Zona no encontrada.");
                    }
                    System.out.println();
                    break;

                case 3:
                    mostrarEstadoGeneral(zonas, estados, riegoEstado);
                    break;

                case 4:
                    System.out.println();
                    System.out.println("--- ZONAS EN ALERTA O CONDICION CRITICA ---");
                    // 6. PARA (for)
                    for (int i = 0; i < 4; i++) {
                        if ("ALERTA".equals(estados[i]) || "CRITICO".equals(estados[i])) {
                            System.out.println("-> " + zonas[i].trim() + " | Estado: " + estados[i] + " | Riego Sukito: " + riegoEstado[i].trim());
                        }
                    }
                    System.out.println("Total de alertas acumuladas: " + contadorAlertas);
                    System.out.println();
                    break;

                case 5:
                    System.out.println();
                    System.out.println("--- HISTORIAL DE LECTURAS (MATRIZ) ---");
                    System.out.println("Ronda    | Puerto B. | Las Brisas | Urseza   | El Coco");
                    System.out.println("--------------------------------------------------------");
                    for (int j = 0; j < 3; j++) {
                        System.out.println("Ronda " + (j + 1) + "  | " + historial[j][0] + " cm  | " + historial[j][1] + " cm   | " + historial[j][2] + " cm  | " + historial[j][3] + " cm");
                    }
                    System.out.println();
                    break;

                case 6:
                    System.out.println();
                    System.out.println("--- ESTADISTICAS Y RESUMEN GENERAL ---");
                    totalNivel = 0.0;
                    for (int i = 0; i < 4; i++) {
                        totalNivel += historial[2][i];
                    }
                    promedioNivel = totalNivel / 4.0;
                    System.out.println("Promedio actual del nivel en zonas: " + promedioNivel + " cm");
                    System.out.println("Total de alertas emitidas: " + contadorAlertas);
                    System.out.println("Veces que se activo el riego directo a Sukito: " + contadorRiegos);
                    System.out.println();
                    break;

                case 7:
                    System.out.println();
                    System.out.println("Finalizando simulacion SAT-Machala. Hasta pronto.");
                    System.out.println();
                    break;

                default:
                    System.out.println();
                    System.out.println("[ERROR] Opcion no valida. Ingrese un numero del 1 al 7.");
                    System.out.println();
                    break;
            }
        }
        
        scanner.close();
    }
}