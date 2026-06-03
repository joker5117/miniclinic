package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByApptDate(LocalDate apptDate);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    long countByApptDateBetween(LocalDate from, LocalDate to);
    List<Appointment> findByDoctorAndApptDate(Doctor doctor, LocalDate apptDate);

/**
     * 新增：根據掛號狀態計算總筆數
     * 請確保 AppointmentStatus 是你專案中定義狀態的 Enum 類別名稱
     * 如果你的狀態欄位型態是 String，請把參數型態改成 String status
     */
    long countByStatus(String status);
}

