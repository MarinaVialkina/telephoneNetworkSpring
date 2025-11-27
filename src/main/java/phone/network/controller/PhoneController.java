package phone.network.controller;

import phone.network.dto.CreatePhoneRequestDTO;
import phone.network.dto.PhoneDTO;
import phone.network.model.CallResult;
import phone.network.model.Phone;
import phone.network.service.PhoneManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/phones")
//@CrossOrigin(origins = "*")
public class PhoneController {
    private final PhoneManagementService phoneManagementService;

    public PhoneController(PhoneManagementService phoneManagementService) {
        this.phoneManagementService = phoneManagementService;
    }

    @GetMapping
    public ResponseEntity<List<PhoneDTO>> getAllPhones() {
        return ResponseEntity.ok(phoneManagementService.getAllPhones());
    }

    @PostMapping
    public ResponseEntity<?> addPhone(@Valid @RequestBody CreatePhoneRequestDTO request) {
        Phone newPhone = phoneManagementService.addPhone(request.getPhoneNumber());
        if (newPhone == null) {
            return ResponseEntity.badRequest().body(new CallResult(false, "Телефон уже существует"));
        }
        PhoneDTO phoneDTO = PhoneDTO.fromEntity(newPhone);
        return ResponseEntity.ok(phoneDTO);
    }

    @DeleteMapping("/{phoneNumber}")
    public ResponseEntity<CallResult> deletePhone(@PathVariable String phoneNumber) {
        boolean deleted = phoneManagementService.deletePhone(phoneNumber);
        if (deleted) {
            return ResponseEntity.ok(new CallResult(true, "Телефон удален"));
        }
        return ResponseEntity.badRequest().body(new CallResult(false, "Телефон не найден"));
    }
}