package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import tw.edu.fju.miniclinic.model.*;

@Controller
public class LoginController {

    @Autowired
    private DoctorRepository doctorRepo;

    // GET：顯示登入頁
    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    // POST：處理登入
    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm form,
            BindingResult result,
            HttpSession session,
            Model model) {

        // 步驟 1：檢查表單驗證
        if (result.hasErrors()) {
            return "login";  // 顯示錯誤訊息
        }

        // 步驟 2：查詢醫師
        Doctor doctor = doctorRepo.findById(form.getDoctorId()).orElse(null);

        // 步驟 3：檢查密碼（醫師不存在或密碼錯都給同樣的錯誤訊息，避免洩漏帳號是否存在）
        if (doctor == null || !BCrypt.checkpw(form.getPassword(), doctor.getPasswordHash())) {
            model.addAttribute("errorMessage", "醫師編號或密碼錯誤");
            return "login";
        }

        // 步驟 4：登入成功，存入 Session
        session.setAttribute("loggedInDoctorId", doctor.getDoctorId());
        session.setAttribute("loggedInDoctorName", doctor.getName());

        return "redirect:/dashboard";
    }

    // 登出
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // 清除 Session
        return "redirect:/login";
    }

    // GET：顯示修改密碼頁
    @GetMapping("/password")
    public String passwordForm(HttpSession session, Model model) {
        model.addAttribute("loggedInDoctorName", session.getAttribute("loggedInDoctorName"));
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordForm());
        }
        return "password";
    }

    // POST：處理修改密碼
    @PostMapping("/password")
    public String changePassword(
            @ModelAttribute("passwordForm") PasswordForm form,
            HttpSession session,
            Model model) {

        // 1. 取得當前醫師 ID
        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        if (doctorId == null) return "redirect:/login";

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "redirect:/login";

        // 2. 驗證邏輯
        // 舊密碼檢查
        if (!BCrypt.checkpw(form.getOldPassword(), doctor.getPasswordHash())) {
            model.addAttribute("errorMessage", "舊密碼錯誤");
            return "password";
        }

        // 新密碼一致性檢查
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("errorMessage", "兩次密碼不相符");
            return "password";
        }

        // 長度檢查
        if (form.getNewPassword().length() < 8) {
            model.addAttribute("errorMessage", "密碼至少需要 8 個字元");
            return "password";
        }

        // 3. 全部通過，更新密碼
        doctor.setPasswordHash(BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt()));
        doctorRepo.save(doctor);

        return "redirect:/dashboard"; // 修改成功跳回主面板
    }
}