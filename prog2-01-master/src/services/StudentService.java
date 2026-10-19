package services;

import entities.Student;
import repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Capa de Servicios (Service Layer) para la gestión de Estudiantes.
 * Aplica el principio de separación de responsabilidades:
 * - Contiene las reglas y validaciones del negocio.
 * - Orquesta las operaciones entre la interfaz de usuario (Main) y el almacenamiento (StudentRepository).
 */
public class StudentService {

    // Referencia al repositorio de datos (inyección o composición)
    private final StudentRepository studentRepository = new StudentRepository();

    /**
     * Constructor por defecto del servicio.
     * Opcionalmente se pueden precargar datos de prueba para facilitar la demostración.
     */
    public StudentService() {
        // Precarga de datos iniciales para pruebas didácticas
        loadSampleData();
    }

    /**
     * Registra un nuevo estudiante en el sistema tras aplicar validaciones de negocio.
     * Reglas aplicadas:
     * 1. El objeto no puede ser nulo.
     * 2. Todos los campos obligatorios deben estar diligenciados.
     * 3. El número de carnet/código no puede estar duplicado.
     * 
     * @param s Objeto Student a registrar.
     * @return El estudiante guardado con su ID asignado, o null si falló alguna validación.
     */
    public Student create(Student s) {
        if (s == null) {
            System.out.println("Error: El estudiante no puede ser nulo.");
            return null;
        }

        // Validación de campos vacíos o nulos
        if (!validateFields(s)) {
            System.out.println("Error: Hay campos obligatorios que no se han llenado.");
            return null;
        }

        // Validación de unicidad del carnet/código estudiantil
        if (studentRepository.existsByNumberCard(s.getNumberCard())) {
            System.out.println("Error: Ya existe un estudiante con el carnet/código: " + s.getNumberCard());
            return null;
        }

        // Asignación de ID autoincremental único
        s.setId(studentRepository.generateId());

        // Guardado en el repositorio
        Student created = studentRepository.createStudent(s);
        System.out.println("Estudiante registrado exitosamente con ID: " + created.getId());
        return created;
    }

    /**
     * Muestra en consola la lista completa de estudiantes en un formato claro y estructurado.
     * Nota pedagógica: Se removió la excepción artificial (0/0) que servía de ejemplo didáctico en clase,
     * reemplazándola con una presentación profesional de los datos.
     */
    public void listAll() {
        List<Student> students = studentRepository.listAll();

        if (students.isEmpty()) {
            System.out.println("\n[!] No hay estudiantes registrados actualmente.");
            return;
        }

        System.out.println("\n=========================================================================================================");
        System.out.println("                                      LISTADO DE ESTUDIANTES                                            ");
        System.out.println("=========================================================================================================");
        System.out.printf("%-4s | %-12s | %-20s | %-8s | %-18s | %-10s | %-8s | %-10s%n",
                "ID", "CARNET", "NOMBRE", "GÉNERO", "MATERIA", "SEMESTRE", "PROMEDIO", "ESTADO");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (Student st : students) {
            System.out.printf("%-4d | %-12s | %-20s | %-8s | %-18s | %-10s | %-8.2f | %-10s%n",
                    st.getId(),
                    st.getNumberCard(),
                    st.getName(),
                    st.getGender(),
                    st.getSubject(),
                    st.getGrade(),
                    st.calculateAverage(),
                    st.getAcademicStatus());
            if (st.getGrades() != null && !st.getGrades().isEmpty()) {
                System.out.println("     -> Notas registradas: " + st.getGrades());
            }
        }
        System.out.println("=========================================================================================================");
        System.out.println("Total de estudiantes registrados: " + students.size());
    }

    /**
     * Retorna todos los estudiantes en memoria.
     * 
     * @return Lista de estudiantes.
     */
    public List<Student> getAll() {
        return studentRepository.listAll();
    }

    /**
     * Busca un estudiante por su ID numérico.
     * 
     * @param id Identificador único a buscar.
     * @return El estudiante correspondiente o null si no se encuentra.
     */
    public Student findOne(Long id) {
        return studentRepository.findOne(id);
    }

    /**
     * Busca un estudiante por su carnet o código institucional.
     * 
     * @param numberCard Código del carnet a buscar.
     * @return El estudiante encontrado, o null si no existe.
     */
    public Student findByNumberCard(String numberCard) {
        Optional<Student> studentOpt = studentRepository.findByNumberCard(numberCard);
        return studentOpt.orElse(null);
    }

    /**
     * Elimina un estudiante a partir de su carnet o código institucional.
     * 
     * @param numberCard Código del estudiante a eliminar.
     * @return true si se eliminó con éxito, false si no se encontró o el dato es inválido.
     */
    public boolean deleteByNumberCard(String numberCard) {
        if (numberCard == null || numberCard.trim().isEmpty()) {
            System.out.println("Error: Debe ingresar un código o carnet válido.");
            return false;
        }

        Student found = findByNumberCard(numberCard);
        if (found == null) {
            System.out.println("No se encontró ningún estudiante con el carnet/código: " + numberCard);
            return false;
        }

        boolean deleted = studentRepository.deleteByNumberCard(numberCard);
        if (deleted) {
            System.out.println("Estudiante '" + found.getName() + "' (Carnet: " + numberCard + ") eliminado exitosamente.");
        }
        return deleted;
    }

    /**
     * Agrega una nota a un estudiante existente identificado por su carnet.
     * 
     * @param numberCard Carnet del estudiante.
     * @param grade Calificación a registrar (escala 0.0 a 5.0).
     * @return true si se agregó con éxito, false en caso de error.
     */
    public boolean addGradeToStudent(String numberCard, double grade) {
        if (grade < 0.0 || grade > 5.0) {
            System.out.println("Error: La nota debe encontrarse en el rango de 0.0 a 5.0.");
            return false;
        }

        Student student = findByNumberCard(numberCard);
        if (student == null) {
            System.out.println("Error: No se encontró ningún estudiante con el carnet: " + numberCard);
            return false;
        }

        student.addGrade(grade);
        System.out.printf("Nota %.2f agregada exitosamente a %s. Nuevo promedio: %.2f%n",
                grade, student.getName(), student.calculateAverage());
        return true;
    }

    /**
     * Calcula el promedio general de las notas de todos los estudiantes registrados.
     * 
     * @return Promedio general del grupo, o 0.0 si no hay estudiantes o notas.
     */
    public double calculateGeneralAverage() {
        List<Student> students = studentRepository.listAll();
        if (students.isEmpty()) {
            return 0.0;
        }

        double totalAverages = 0.0;
        int studentsWithGrades = 0;

        for (Student s : students) {
            if (s.getGrades() != null && !s.getGrades().isEmpty()) {
                totalAverages += s.calculateAverage();
                studentsWithGrades++;
            }
        }

        if (studentsWithGrades == 0) {
            return 0.0;
        }

        return totalAverages / studentsWithGrades;
    }

    /**
     * Muestra en pantalla el resumen de promedios individuales y el promedio general del curso.
     */
    public void printAverageReport() {
        List<Student> students = studentRepository.listAll();

        if (students.isEmpty()) {
            System.out.println("\n[!] No hay estudiantes registrados para calcular promedios.");
            return;
        }

        System.out.println("\n=========================================================================");
        System.out.println("                    REPORTE DE PROMEDIOS DE NOTAS                       ");
        System.out.println("=========================================================================");
        System.out.printf("%-12s | %-20s | %-15s | %-8s | %-10s%n",
                "CARNET", "NOMBRE", "MATERIA", "PROMEDIO", "ESTADO");
        System.out.println("-------------------------------------------------------------------------");

        for (Student s : students) {
            System.out.printf("%-12s | %-20s | %-15s | %-8.2f | %-10s%n",
                    s.getNumberCard(),
                    s.getName(),
                    s.getSubject(),
                    s.calculateAverage(),
                    s.getAcademicStatus());
        }

        System.out.println("-------------------------------------------------------------------------");
        double generalAvg = calculateGeneralAverage();
        System.out.printf("PROMEDIO GENERAL DEL GRUPO: %.2f%n", generalAvg);
        System.out.println("=========================================================================");
    }

    /**
     * Valida que ninguno de los campos obligatorios del estudiante esté vacío o nulo.
     * 
     * @param s Estudiante a validar.
     * @return true si todos los campos requeridos son válidos, false si falta alguno.
     */
    private boolean validateFields(Student s) {
        if (s == null) return false;
        return isNotBlank(s.getName()) &&
               isNotBlank(s.getGender()) &&
               isNotBlank(s.getPhone()) &&
               isNotBlank(s.getEmail()) &&
               isNotBlank(s.getNumberCard()) &&
               isNotBlank(s.getGrade()) &&
               isNotBlank(s.getSubject());
    }

    /**
     * Método auxiliar para validar si una cadena tiene contenido real (no nula ni solo espacios).
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Carga estudiantes iniciales de demostración para permitir pruebas inmediatas
     * del sistema sin necesidad de ingresar todos los datos manualmente.
     */
    private void loadSampleData() {
        // Estudiante de muestra 1
        List<Double> notasJuan = new ArrayList<>();
        notasJuan.add(4.5);
        notasJuan.add(3.8);
        notasJuan.add(4.2);
        Student s1 = new Student(studentRepository.generateId(), "Juan Pérez", "Masculino", "3001234567",
                "juan.perez@universidad.edu", "EST-101", "Semestre 3", "Programación 2", notasJuan);
        studentRepository.createStudent(s1);

        // Estudiante de muestra 2
        List<Double> notasMaria = new ArrayList<>();
        notasMaria.add(2.8);
        notasMaria.add(2.5);
        notasMaria.add(3.0);
        Student s2 = new Student(studentRepository.generateId(), "María Gómez", "Femenino", "3119876543",
                "maria.gomez@universidad.edu", "EST-102", "Semestre 3", "Programación 2", notasMaria);
        studentRepository.createStudent(s2);
    }
}
