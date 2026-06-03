package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.HashMap; // 新增 import
import java.util.List;
import java.util.Map;
import java.util.Optional;

import tw.edu.fju.miniclinic.model.*;

@RestController
public class AppointmentApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    // 功能二新增：統計需要用到病患 Repository，在這裡注入
    @Autowired
    private PatientRepository patientRepo;

    /**
     * 功能二：統計摘要端點 GET /api/stats
     * 完美對齊你專案的 Map 風格，不需要任何 DTO 檔案，且格式 100% 吻合
     */
    @GetMapping("/api/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> response = new HashMap<>();
        
        // 1. 查詢各大表總筆數
        response.put("totalDoctors", doctorRepo.count());
        response.put("totalPatients", patientRepo.count());
        response.put("totalAppointments", appointmentRepo.count());

        // 2. 建立內層的 Map 統計各狀態數量
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("BOOKED", appointmentRepo.countByStatus("BOOKED"));
        byStatus.put("COMPLETED", appointmentRepo.countByStatus("COMPLETED"));
        byStatus.put("CANCELLED", appointmentRepo.countByStatus("CANCELLED"));

        // 3. 塞入外層 Map 組成巢狀結構
        response.put("byStatus", byStatus);

        return response;
    }

    // 端點 1：回傳總掛號數 JSON {"count": X}
    @GetMapping("/api/appointments/count")
    public Map<String, Long> getAppointmentCount() {
        return Map.of("count", appointmentRepo.count());
    }

    // 端點 2：支援三種情況的掛號篩選 API
    @GetMapping("/api/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {

        // 情況 A：同時傳或只傳 date (優先處理 date)
        if (date != null && !date.isBlank()) {
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok(appointmentRepo.findByApptDate(parsedDate));
        }

        // 情況 B：只傳 doctorId
        if (doctorId != null && !doctorId.isBlank()) {
            Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
            if (doctorOpt.isPresent()) {
                return ResponseEntity.ok(appointmentRepo.findByDoctor(doctorOpt.get()));
            } else {
                return ResponseEntity.notFound().build(); // 找不到該醫生回傳 404
            }
        }

        // 情況 C：什麼都沒傳，回傳全部掛號
        return ResponseEntity.ok(appointmentRepo.findAll());
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");

        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }

        // 只能修改自己的掛號
        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build();
        }

        String newStatus = payload.get("status");
        if (!List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().build();
        }

        appt.setStatus(newStatus);
        return ResponseEntity.ok(appointmentRepo.save(appt));
    }
}