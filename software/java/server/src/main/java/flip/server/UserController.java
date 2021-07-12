package flip.server;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.stereotype.Controller;


@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired JdbcUserDetailsManager jdbcUserDetailsManager;
    @Autowired PasswordEncoder        passwordEncoder;

    @PostMapping
    public String createUser(
                             @RequestParam(name="username", required=true) String username,
                             @RequestParam(name="password", required=true) String password) {

        UserDetails user = User
            .withUsername(username)
            .password(passwordEncoder.encode(password))
            .roles("USER")
            .build();

        jdbcUserDetailsManager.createUser(user);

        return "redirect:/";
    }
}
