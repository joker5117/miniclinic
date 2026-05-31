package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;
import java.util.stream.Collectors;

import tw.edu.fju.miniclinic.model.*;

@Controller
public class HomeController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // 你原本的首頁路由（對應 home.html）
    @GetMapping("/")
    public String index() {
        return "home";
    }

    // 新增：處理 /stats 統計頁面渲染的路由
    @GetMapping("/stats")
    public String getStatsPage(Model model) {
        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("appointmentCount", appointmentRepo.count());

        // 依科別分組統計掛號
        Map<String, Long> deptStats = appointmentRepo.findAll().stream()
                .filter(appt -> appt.getDoctor() != null && appt.getDoctor().getDepartment() != null)
                .collect(Collectors.groupingBy(
                        appt -> appt.getDoctor().getDepartment(),
                        Collectors.counting()
                ));

        model.addAttribute("deptStats", deptStats);
        return "stats"; // 對應 templates/stats.html
    }
}