package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.List;
import java.util.Optional;

@Controller
public class PatientController {

    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/patients")
    public String listPatients(Model model) {
        List<Patient> patients = patientRepo.findAll();
        model.addAttribute("patients", patients);

        return "patients";
    }

    @GetMapping("/patients/{chartNo}")
    public String patientDetail(@PathVariable String chartNo, Model model) {
        Optional<Patient> patient = patientRepo.findById(chartNo);

        if (patient.isEmpty()) {
            return "redirect:/patients"; // 找不到病患則導回清單頁
        }

        model.addAttribute("patient", patient.get());
        return "patient-detail";
    }

    // === API 部分 ===

    // 取得所有病患清單的 API
    @GetMapping("/api/patients")
    @ResponseBody
    public List<Patient> getApiPatients() {
        return patientRepo.findAll();
    }

    // 根據病歷號取得單一病患資料的 API
    @GetMapping("/api/patients/{chartNo}")
    @ResponseBody
    public ResponseEntity<Patient> getApiPatient(@PathVariable String chartNo) {
        return patientRepo.findById(chartNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 取得所有不重複病歷號的 API
    @GetMapping("/api/patients/chart-numbers") // 請補上你原本被遮住的路由
@ResponseBody
public List<String> getApiChartNumbers() {
    return patientRepo.findAll()             // 1. 撈出所有病人
            .stream()                        // 2. 開啟串流
            .map(Patient::getChartNo)        // 3. 只要病歷號碼 (請確認 Patient 裡的 getter 名稱)
            .distinct()                      // 4. 去除重複
            .toList();                       // 5. 打包成列表回傳
}
}
