package entities;

import java.util.ArrayList;
import java.util.List;

public class Student extends Person {

    private String email;
    private String numberCard;
    private String grade;
    private String subject;
    private List<Double> grades;


    public Student() {
        super();
        this.grades = new ArrayList<>();
    }


    public Student(Long id, String name, String gender, String phone, 
                   String email, String numberCard, String grade, String subject) {
        super(id, name, gender, phone);
        this.email = email;
        this.numberCard = numberCard;
        this.grade = grade;
        this.subject = subject;
        this.grades = new ArrayList<>();
    }

    public Student(Long id, String name, String gender, String phone, 
                   String email, String numberCard, String grade, String subject, List<Double> grades) {
        super(id, name, gender, phone);
        this.email = email;
        this.numberCard = numberCard;
        this.grade = grade;
        this.subject = subject;
        this.grades = (grades != null) ? new ArrayList<>(grades) : new ArrayList<>();
    }

    // ==========================================
    // Métodos accesores y mutadores (Getters y Setters)
    // ==========================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumberCard() {
        return numberCard;
    }

    public void setNumberCard(String numberCard) {
        this.numberCard = numberCard;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Double> getGrades() {
        return grades;
    }

    public void setGrades(List<Double> grades) {
        this.grades = (grades != null) ? new ArrayList<>(grades) : new ArrayList<>();
    }

    // ==========================================
    // Métodos de lógica de negocio del Estudiante
    // ==========================================

    /**
     * Agrega una nota a la lista de calificaciones del estudiante.
     * 
     * @param grade Nota numérica a registrar (entre 0.0 y 5.0)
     */
    public void addGrade(double grade) {
        this.grades.add(grade);
    }

    /**
     * Calcula el promedio de las notas registradas para el estudiante.
     * Utiliza Stream API para calcular el promedio de forma declarativa.
     * 
     * @return Promedio aritmético de las notas, o 0.0 si no tiene notas registradas.
     */
    public double calculateAverage() {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Double g : grades) {
            sum += g;
        }
        return sum / grades.size();
    }

    /**
     * Retorna el estado académico del estudiante en base a su promedio.
     * En el sistema estándar (escala 0.0 a 5.0), se aprueba con 3.0 o más.
     * 
     * @return "Aprobado", "Reprobado" o "Sin notas"
     */
    public String getAcademicStatus() {
        if (grades == null || grades.isEmpty()) {
            return "Sin notas";
        }
        return calculateAverage() >= 3.0 ? "Aprobado" : "Reprobado";
    }

    // ==========================================
    // Sobrescritura de métodos (Polimorfismo)
    // ==========================================

    /**
     * Representación textual detallada del Estudiante.
     * Incluye tanto los datos heredados de Person como los propios de Student.
     * 
     * @return Cadena formateada con todos los datos del estudiante.
     */
    @Override
    public String toString() {
        return "Estudiante [" +
                "ID=" + getId() +
                ", Carnet='" + numberCard + '\'' +
                ", Nombre='" + getName() + '\'' +
                ", Género='" + getGender() + '\'' +
                ", Teléfono='" + getPhone() + '\'' +
                ", Email='" + email + '\'' +
                ", Semestre='" + grade + '\'' +
                ", Materia='" + subject + '\'' +
                ", Notas=" + grades +
                ", Promedio=" + String.format("%.2f", calculateAverage()) +
                ", Estado=" + getAcademicStatus() +
                ']';
    }
}
