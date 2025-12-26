package sp26.se194638.ojt.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.model.request.LoginRequest;
import sp26.se194638.ojt.model.response.LoginResponse;
import sp26.se194638.ojt.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("assign1/api/auth")
public class Controller {

    @Autowired
    private UserService userService;

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<?> login(
            @RequestParam String username,
            @RequestParam String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        return ResponseEntity.ok(userService.login(request));
    }

}