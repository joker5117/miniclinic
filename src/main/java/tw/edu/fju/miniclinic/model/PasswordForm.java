package tw.edu.fju.miniclinic.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密碼表單物件
 * 用於封裝舊密碼、新密碼及確認新密碼的輸入資料
 */
public class PasswordForm {

    // 舊密碼：不可為空
    @NotBlank(message = "請輸入舊密碼")
    private String oldPassword;

    // 新密碼：不可為空且長度需大於等於 8 碼
    @NotBlank(message = "請輸入新密碼")
    @Size(min = 8, message = "密碼至少需要 8 個字元")
    private String newPassword;

    // 確認新密碼：用於二次確認
    @NotBlank(message = "請輸入確認密碼")
    private String confirmPassword;

    // 建構子
    public PasswordForm() {}

    // Getters and Setters
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}