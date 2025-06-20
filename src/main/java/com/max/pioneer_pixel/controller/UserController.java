package com.max.pioneer_pixel.controller;

import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/API/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> userMap) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: createUser | Params: {}", now, userMap);
        throw new UnsupportedOperationException("Use your existing createUser method");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: searchUsers | Params: name={}, email={}, phone={}, dateOfBirth={}, page={}, size={}",
                now, name, email, phone, dateOfBirth, page, size);
        Page<User> resultPage = userService.searchUsers(name, email, phone, dateOfBirth, PageRequest.of(page, size));
        log.info("[{}] Result: found {} users", now, resultPage.getTotalElements());
        return ResponseEntity.ok(resultPage);
    }

    @PostMapping("/addEmail")
    public ResponseEntity<String> addEmail(@RequestParam Long userId, @RequestParam String email) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: addEmail | Params: userId={}, email={}", now, userId, email);
        try {
            userService.addEmail(userId, email);
            log.info("[{}] Result: email added successfully", now);
            return ResponseEntity.ok("Email added successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[{}] Error: {}", now, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteEmail")
    public ResponseEntity<String> deleteEmail(@RequestParam Long userId, @RequestParam String email) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: deleteEmail | Params: userId={}, email={}", now, userId, email);
        userService.deleteEmail(userId, email);
        log.info("[{}] Result: email deleted successfully", now);
        return ResponseEntity.ok("Email deleted successfully");
    }

    @PostMapping("/addPhone")
    public ResponseEntity<String> addPhone(@RequestParam Long userId, @RequestParam String phone) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: addPhone | Params: userId={}, phone={}", now, userId, phone);
        try {
            userService.addPhone(userId, phone);
            log.info("[{}] Result: phone added successfully", now);
            return ResponseEntity.ok("Phone added successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[{}] Error: {}", now, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deletePhone")
    public ResponseEntity<String> deletePhone(@RequestParam Long userId, @RequestParam String phone) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: deletePhone | Params: userId={}, phone={}", now, userId, phone);
        userService.deletePhone(userId, phone);
        log.info("[{}] Result: phone deleted successfully", now);
        return ResponseEntity.ok("Phone deleted successfully");
    }

    @GetMapping("/emails")
    public List<String> getEmails(@RequestParam Long userId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: getEmails | Params: userId={}", now, userId);
        List<String> emails = userService.getEmails(userId);
        log.info("[{}] Result: {} emails found", now, emails.size());
        return emails;
    }

    @GetMapping("/phones")
    public List<String> getPhones(@RequestParam Long userId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("[{}] Operation: getPhones | Params: userId={}", now, userId);
        List<String> phones = userService.getPhones(userId);
        log.info("[{}] Result: {} phones found", now, phones.size());
        return phones;
    }
}
