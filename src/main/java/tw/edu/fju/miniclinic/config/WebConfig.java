package tw.edu.fju.miniclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tw.edu.fju.miniclinic.interceptor.LoginRequiredInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginRequiredInterceptor loginInterceptor;

    // 使用建構子注入取代 @Autowired 欄位注入，這能解決 IDE 的黃色警告
    public WebConfig(LoginRequiredInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns(
                "/dashboard/**",  // /** 通常已包含 /dashboard 本身
                "/password",
                "/password/**",
                "/api/auth/me",
                "/api/appointments/*/status"
            )
            .excludePathPatterns(
                "/login",
                "/logout"
            );
    }
}