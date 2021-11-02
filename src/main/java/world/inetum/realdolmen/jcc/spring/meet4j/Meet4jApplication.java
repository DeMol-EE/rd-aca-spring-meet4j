package world.inetum.realdolmen.jcc.spring.meet4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import world.inetum.realdolmen.jcc.spring.meet4j.people.Person;
import world.inetum.realdolmen.jcc.spring.meet4j.people.PersonRepository;

import java.util.Collections;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Meet4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(Meet4jApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence raw) {
                return String.valueOf(raw.hashCode());
            }

            @Override
            public boolean matches(CharSequence raw, String encoded) {
                return encoded.equals(encode(raw));
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService(PersonRepository personRepository) {
        return principalName -> {
            Person p = personRepository.findByEmail(principalName);
            if (p == null) {
                return null;
            }
            return new User(
                    String.valueOf(p.getId()),
                    p.getPassword(),
                    Collections.emptySet()
            );
        };
    }

    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.httpBasic();
//                http.authorizeRequests()
//                        .anyRequest()
//                        .permitAll();
                http.csrf().disable();
            }
        };
    }
}
