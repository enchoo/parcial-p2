package repositories;

import entities.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de datos para la entidad Student.
 * Aplica el patrón Repository (patrón de diseño arquitectónico), el cual
 * encapsula la lógica necesaria para acceder a las fuentes de datos (en este caso, en memoria con ArrayList).
 * 
 * Centraliza las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre la colección de estudiantes.
 */
public class StudentRepository {

    // ==========================================
    // Almacenamiento en memoria
    // ==========================================
    private final List<Student> list = new ArrayList<>();

    // Secuencia para generar identificadores únicos incrementales
    private Long idSequence = 1L;

    /**
     * Agrega un nuevo estudiante a la colección.
     * 
     * @param student Objeto Student a registrar.
     * @return El objeto Student registrado.
     */
    public Student createStudent(Student student) {
        list.add(student);
        return student;
    }

    /**
     * Retorna la lista completa de todos los estudiantes registrados.
     * 
     * @return List con todos los estudiantes.
     */
    public List<Student> listAll() {
        return list;
    }

    /**
     * Muestra en consola la lista de todos los estudiantes.
     * Mantiene retrocompatibilidad con el diseño original del proyecto.
     */
    public void listAllStudent() {
        if (list.isEmpty()) {
            System.out.println("No hay estudiantes registrados en el sistema.");
            return;
        }
        for (Student student : list) {
            System.out.println(student);
        }
    }

    /**
     * Busca un estudiante por su ID utilizando un ciclo tradicional.
     * 
     * @param id Identificador numérico a buscar.
     * @return El estudiante encontrado, o null si no existe.
     */
    public Student findOne(Long id) {
        if (id == null) return null;
        for (Student student : list) {
            if (student.getId() != null && student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }

    /**
     * Busca un estudiante por su ID utilizando la API de Streams de Java (programación funcional).
     * Corrige el error original de casteo donde se intentaba castear un Stream a Student.
     * 
     * @param id Identificador numérico a buscar.
     * @return El estudiante encontrado, o null si no se encuentra.
     */
    public Student findOneList(Long id) {
        if (id == null) return null;
        return list.stream()
                .filter(s -> s.getId() != null && s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca un estudiante por su código de carnet (numberCard).
     * 
     * @param numberCard Código o número de carnet del estudiante.
     * @return Optional con el estudiante si existe, u Optional.empty() si no.
     */
    public Optional<Student> findByNumberCard(String numberCard) {
        if (numberCard == null || numberCard.trim().isEmpty()) {
            return Optional.empty();
        }
        return list.stream()
                .filter(s -> s.getNumberCard() != null && s.getNumberCard().equalsIgnoreCase(numberCard.trim()))
                .findFirst();
    }

    /**
     * Elimina un estudiante de la colección por su número de carnet/código.
     * 
     * @param numberCard Código o carnet del estudiante a eliminar.
     * @return true si se encontró y eliminó exitosamente, false en caso contrario.
     */
    public boolean deleteByNumberCard(String numberCard) {
        if (numberCard == null || numberCard.trim().isEmpty()) {
            return false;
        }
        return list.removeIf(s -> s.getNumberCard() != null && s.getNumberCard().equalsIgnoreCase(numberCard.trim()));
    }

    /**
     * Elimina un estudiante de la colección por su identificador único (ID).
     * 
     * @param id Identificador del estudiante a eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    public boolean deleteById(Long id) {
        if (id == null) return false;
        return list.removeIf(s -> s.getId() != null && s.getId().equals(id));
    }

    /**
     * Genera un nuevo ID autoincremental único.
     * Corrige el problema original donde list.size() + 1 generaba IDs duplicados
     * cuando se eliminaban elementos previos de la lista.
     * 
     * @return Siguiente identificador numérico único.
     */
    public Long generateId() {
        return idSequence++;
    }

    /**
     * Verifica si ya existe un estudiante registrado con el número de carnet indicado.
     * 
     * @param numberCard Carnet o código a verificar.
     * @return true si ya existe, false si está disponible.
     */
    public boolean existsByNumberCard(String numberCard) {
        return findByNumberCard(numberCard).isPresent();
    }
}
