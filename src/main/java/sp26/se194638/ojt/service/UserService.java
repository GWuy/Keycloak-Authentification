package sp26.se194638.ojt.service;

import org.keycloak.models.KeycloakContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.request.LoginRequest;
import sp26.se194638.ojt.model.request.RegisterRequest;
import sp26.se194638.ojt.model.response.LoginResponse;
import sp26.se194638.ojt.model.response.RegisterResponse;
import sp26.se194638.ojt.repository.BlacklistRepository;
import sp26.se194638.ojt.repository.UserRepository;

import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BlacklistRepository blacklistRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TOKEN_URL =
            "http://localhost:8080/realms/assignment1/protocol/openid-connect/token";

    private static final String INTROSPECTIVE_URL =
            "http://localhost:8080/realms/assignment1/protocol/openid-connect/token/introspect";

    private static final String CLIENT_ID = "gwuy-api-client";
    private static final String CLIENT_SECRET = "FkSHjwFx7htplZhmaKRWiQ2q2gjArthC";

    public LoginResponse login(LoginRequest loginRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);
        body.add("username", loginRequest.getUsername());
        body.add("password", loginRequest.getPassword());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Username or Password incorrect!!!");
        }

        User userLogin = userRepository.findByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword());

        //check xem cos trong black list khong
        boolean isInBlackList = blacklistRepository.existsByUser(userLogin);

        if (isInBlackList || "INACTIVE".equalsIgnoreCase(userLogin.getStatus())) {
            throw new RuntimeException("Login fail. You account has been banned");
        }

        String accessToken = jwtService.generateToken(userLogin);
        String refreshToken = jwtService.generateRefreshToken(userLogin);
        LocalDateTime expiresIn;
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            expiresIn = LocalDateTime.now().plusMinutes(jsonNode.get("expires_in").asLong());
        } catch (Exception e) {
            throw new RuntimeException("Error in parsing");
        }

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .build();


        return loginResponse;
    }

    public RegisterResponse register(RegisterRequest request) {

        return null;
    }
}