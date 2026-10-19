import entities.Student;
import services.StudentService;

import java.util.Scanner;

/**
 * Clase principal que actúa como punto de entrada de la aplicación (Main).
 * Presenta una interfaz de consola interactiva con un menú de opciones para
 * gestionar estudiantes, consultar calificaciones y calcular promedios.
 * 
 * Arquitectura del proyecto:
 * 1. entities (Entidades): Person (clase padre), Student (clase hija que hereda de Person).
 * 2. repositories (Repositorios): StudentRepository (almacenamiento en memoria y operaciones CRUD).
 * 3. services (Servicios): StudentService (reglas de negocio, validaciones y cálculos).
 * 4. Main (Presentación): Captura de datos por consola y visualización de resultados.
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentService studentService = new StudentService();

        int option;

        System.out.println("=================================================");
        System.out.println("      SISTEMA DE GESTIÓN ACADÉMICA - PROG 2      ");
        System.out.println("=================================================");

        do {
            printMenu();
            option = readInt(sc, "Seleccione una opción: ");

            switch (option) {
                case 1:
                    // Opción 1: Registrar un nuevo estudiante
                    registerStudent(sc, studentService);
                    break;

                case 2:
                    // Opción 2: Listar todos los estudiantes registrados
                    studentService.listAll();
                    break;

                case 3:
                    // Opción 3: Eliminar estudiante por carnet/código
                    deleteStudent(sc, studentService);
                    break;

                case 4:
                    // Opción 4: Calcular promedio de notas (menú especializado)
                    handleAveragesMenu(sc, studentService);
                    break;

                case 5:
                    // Opción 5: Buscar estudiante por código o ID
                    searchStudent(sc, studentService);
                    break;

                case 0:
                    // Opción 0: Salir del sistema
                    System.out.println("\n¡Gracias por utilizar el sistema! ¡Hasta luego!");
                    break;

                default:
                    System.out.println("\n[!] Opción no válida. Por favor, intente de nuevo.");
            }

        } while (option != 0);

        sc.close();
    }

    /**
     * Muestra las opciones disponibles en el menú principal.
     */
    private static void printMenu() {
        System.out.println("\n===== MENÚ DE GESTIÓN DE ESTUDIANTES =====");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Listar todos los estudiantes");
        System.out.println("3. Eliminar estudiante por código/carnet");
        System.out.println("4. Calcular promedio de notas");
        System.out.println("5. Buscar estudiante por código/carnet");
        System.out.println("0. Salir");
    }

    /**
     * Orquesta el registro de un nuevo estudiante solicitando los datos y
     * delegando la persistencia y validación al servicio.
     * 
     * @param sc Scanner para lectura de consola.
     * @param service Servicio de estudiantes.
     */
    private static void registerStudent(Scanner sc, StudentService service) {
        System.out.println("\n--- REGISTRO DE NUEVO ESTUDIANTE ---");
        Student student = captureStudentData(sc);
        service.create(student);
    }

    /**
     * Captura interactivamente los atributos del estudiante desde la consola.
     * Se corrigen los textos de solicitud de datos para corresponder exactamente
     * a los atributos de Person y Student.
     * 
     * @param sc Scanner para lectura de entradas.
     * @return Objeto Student con la información ingresada.
     */
    private static Student captureStudentData(Scanner sc) {
        Student student = new Student();

        student.setName(readNonEmptyString(sc, "Ingrese Nombre completo: "));
        student.setGender(readNonEmptyString(sc, "Ingrese Género (M/F/Otro): "));
        student.setPhone(readNonEmptyString(sc, "Ingrese Teléfono: "));
        student.setEmail(readNonEmptyString(sc, "Ingrese Email: "));
        student.setNumberCard(readNonEmptyString(sc, "Ingrese Número de carnet / Código: "));
        student.setGrade(readNonEmptyString(sc, "Ingrese Semestre / Nivel: "));
        student.setSubject(readNonEmptyString(sc, "Ingrese la Materia: "));

        // Captura opcional de notas iniciales
        System.out.print("¿Desea ingresar calificaciones para este estudiante ahora? (s/n): ");
        String answer = sc.nextLine().trim();

        if (answer.equalsIgnoreCase("s") || answer.equalsIgnoreCase("si") || answer.equalsIgnoreCase("sí")) {
            int numGrades = readInt(sc, "¿Cuántas notas desea ingresar? (1 a 5): ");
            while (numGrades < 1 || numGrades > 10) {
                System.out.println("Por favor ingrese un número entre 1 y 10.");
                numGrades = readInt(sc, "¿Cuántas notas desea ingresar? (1 a 5): ");
            }

            for (int i = 1; i <= numGrades; i++) {
                double grade = readDouble(sc, "Ingrese la nota #" + i + " (0.0 a 5.0): ", 0.0, 5.0);
                student.addGrade(grade);
            }
        }

        return student;
    }

    /**
     * Proceso para eliminar un estudiante solicitando su código o carnet.
     * 
     * @param sc Scanner para lectura de consola.
     * @param service Servicio de estudiantes.
     */
    private static void deleteStudent(Scanner sc, StudentService service) {
        System.out.println("\n--- ELIMINAR ESTUDIANTE POR CÓDIGO ---");
        String code = readNonEmptyString(sc, "Ingrese el número de carnet/código del estudiante a eliminar: ");

        Student student = service.findByNumberCard(code);
        if (student == null) {
            System.out.println("[!] No se encontró ningún estudiante con el código: " + code);
            return;
        }

        System.out.printf("¿Está seguro de que desea eliminar al estudiante '%s' (Carnet: %s)? (s/n): ",
                student.getName(), student.getNumberCard());
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("s") || confirm.equalsIgnoreCase("si") || confirm.equalsIgnoreCase("sí")) {
            service.deleteByNumberCard(code);
        } else {
            System.out.println("Operación cancelada. El estudiante no fue eliminado.");
        }
    }

    /**
     * Submenú especializado para el cálculo y consulta de promedios de notas.
     * Ofrece cálculo del promedio general, consulta individual y adición de nuevas notas.
     * 
     * @param sc Scanner de entrada.
     * @param service Servicio de estudiantes.
     */
    private static void handleAveragesMenu(Scanner sc, StudentService service) {
        int subOption;
        do {
            System.out.println("\n--- GESTIÓN Y CÁLCULO DE PROMEDIOS ---");
            System.out.println("1. Ver reporte completo de promedios (General e Individual)");
            System.out.println("2. Consultar promedio de un estudiante específico por carnet/código");
            System.out.println("3. Agregar nueva calificación a un estudiante");
            System.out.println("0. Regresar al menú principal");

            subOption = readInt(sc, "Seleccione una opción: ");

            switch (subOption) {
                case 1:
                    service.printAverageReport();
                    break;

                case 2:
                    consultStudentAverage(sc, service);
                    break;

                case 3:
                    addNewGrade(sc, service);
                    break;

                case 0:
                    System.out.println("Regresando al menú principal...");
                    break;

                default:
                    System.out.println("[!] Opción no válida.");
            }
        } while (subOption != 0);
    }

    /**
     * Consulta el promedio y estado de un estudiante individual a partir de su carnet.
     */
    private static void consultStudentAverage(Scanner sc, StudentService service) {
        String code = readNonEmptyString(sc, "Ingrese el carnet/código del estudiante: ");
        Student s = service.findByNumberCard(code);

        if (s == null) {
            System.out.println("[!] No se encontró ningún estudiante con el carnet: " + code);
            return;
        }

        System.out.println("\n-------------------------------------------------");
        System.out.println("Estudiante : " + s.getName());
        System.out.println("Carnet     : " + s.getNumberCard());
        System.out.println("Materia    : " + s.getSubject());
        System.out.println("Notas      : " + (s.getGrades().isEmpty() ? "Sin notas registradas" : s.getGrades()));
        System.out.printf("Promedio   : %.2f%n", s.calculateAverage());
        System.out.println("Estado     : " + s.getAcademicStatus());
        System.out.println("-------------------------------------------------");
    }

    /**
     * Permite registrar una nueva nota a un estudiante existente.
     */
    private static void addNewGrade(Scanner sc, StudentService service) {
        String code = readNonEmptyString(sc, "Ingrese el carnet/código del estudiante: ");
        Student s = service.findByNumberCard(code);

        if (s == null) {
            System.out.println("[!] No se encontró ningún estudiante con el carnet: " + code);
            return;
        }

        System.out.println("Estudiante seleccionado: " + s.getName() + " - Materia: " + s.getSubject());
        double grade = readDouble(sc, "Ingrese la nueva nota (0.0 a 5.0): ", 0.0, 5.0);
        service.addGradeToStudent(code, grade);
    }

    /**
     * Busca un estudiante y muestra toda su ficha técnica.
     */
    private static void searchStudent(Scanner sc, StudentService service) {
        System.out.println("\n--- BÚSQUEDA DE ESTUDIANTE ---");
        String code = readNonEmptyString(sc, "Ingrese el carnet/código del estudiante: ");
        Student s = service.findByNumberCard(code);

        if (s == null) {
            System.out.println("[!] No se encontró ningún estudiante con el carnet: " + code);
        } else {
            System.out.println("\nFicha del Estudiante encontrada:");
            System.out.println(s);
        }
    }

    // ==========================================
    // Métodos utilitarios para lectura segura de consola
    // Previenen caídas del sistema por InputMismatchException
    // ==========================================

    /**
     * Lee un número entero de la consola con validación de excepciones.
     */
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Entrada inválida. Ingrese un número entero válido.");
            }
        }
    }

    /**
     * Lee un número decimal (double) de la consola validando un rango mínimo y máximo.
     */
    private static double readDouble(Scanner sc, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().replace(',', '.');
            try {
                double value = Double.parseDouble(input);
                if (value < min || value > max) {
                    System.out.printf("[!] El valor debe estar entre %.1f y %.1f.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[!] Entrada inválida. Ingrese un valor numérico decimal válido (ej. 4.5).");
            }
        }
    }

    /**
     * Lee una cadena de texto no vacía de la consola.
     */
    private static String readNonEmptyString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[!] Este campo es obligatorio. No puede quedar vacío.");
        }
    }
}