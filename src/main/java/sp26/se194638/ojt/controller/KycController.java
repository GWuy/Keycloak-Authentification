package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.model.dto.request.KycRegisterRequest;
import sp26.se194638.ojt.service.KycService;

@RestController
@RequestMapping("assign1/api/kyc")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class KycController {

  @Autowired
  private KycService kycService;

  @GetMapping("/me")
  public ResponseEntity<?> getIdCardByUser(@RequestHeader("Authorization") String header) {
    return ResponseEntity.ok(kycService.getIdCardByUser(header));
  }

  @GetMapping
  public ResponseEntity<?> getAllIdCard(@RequestHeader("Authorization") String header) {
    return ResponseEntity.ok(kycService.listAllIdCards(header));
  }

  @PostMapping("/register")
  public ResponseEntity<?> regisNewIdCard(
    @RequestBody KycRegisterRequest request,
    @RequestHeader("Authorization") String header) {
    return ResponseEntity.ok(kycService.registerIdentificationCard(request, header));
  }

  @PostMapping("/approve/{idCard}")
  public ResponseEntity<?> approveIdCard(
    @PathVariable Integer idCard,
    @RequestHeader("Authorization") String header
  ) {
    return ResponseEntity.ok(kycService.updateIdentificationCardStatus(idCard, header, "verified"));
  }

  @PostMapping("/reject/{idCard}")
  public ResponseEntity<?> rejectIdCard(
    @PathVariable Integer idCard,
    @RequestHeader("Authorization") String header
  ) {
    return ResponseEntity.ok(kycService.updateIdentificationCardStatus(idCard, header, "reject"));
  }


}
