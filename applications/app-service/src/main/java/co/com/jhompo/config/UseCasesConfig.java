package co.com.jhompo.config;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.model.role.gateways.RoleRepository;
import co.com.jhompo.model.security.PasswordEncoderService;
import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
import co.com.jhompo.usecase.role.RoleUseCase;
import co.com.jhompo.usecase.user.LoginUseCase;
import co.com.jhompo.usecase.user.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


@ComponentScan(basePackages = "co.com.jhompo.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
@Configuration
public class UseCasesConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderService passwordEncoder;

    public UseCasesConfig(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoderService passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserUseCase userUseCase() {
        return new UserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public LoginUseCase loginUseCase() {
        return new LoginUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public RoleUseCase roleUseCase() {
        return new RoleUseCase(roleRepository);
    }
}
