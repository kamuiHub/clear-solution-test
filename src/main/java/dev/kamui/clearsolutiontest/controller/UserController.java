package dev.kamui.clearsolutiontest.controller;

import dev.kamui.clearsolutiontest.dto.*;
import dev.kamui.clearsolutiontest.exception.InvalidAgeException;
import dev.kamui.clearsolutiontest.exception.UserAlreadyExistException;
import dev.kamui.clearsolutiontest.exception.UserNotFoundException;

import dev.kamui.clearsolutiontest.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@Validated
@RequestMapping("api/v1/users")
public class UserController {

    @Value("${min.age}")
    private int minAge;

    private final Set<User> storage = new HashSet<>();

    @PostMapping
    public ResponseEntity<UserDataResponse> createUser(@Valid @RequestBody UserRequest request) {
        if (!validAge(request.getBirthData())) {
            throw new InvalidAgeException("Age is less than 18");
        }
        User user = UserRequest.convertToNewUser(request, storage.size() + 1);
        if (userExist(user)) {
            throw new UserAlreadyExistException("User with this email is already exist");
        }
        storage.add(user);

        //that since there's no database layer and the User class serves as a simple data class,
        // it's appropriate to return it directly as a JSON response without the need for a separate DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDataResponse(HttpStatus.CREATED.value(), user));
    }

    @GetMapping
    public ResponseEntity<UserDataListResponse> getUsers(
            @RequestParam(name = "From") LocalDate from,
            @RequestParam(name = "To") LocalDate to) {

        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().build();
        }

        List<User> userDataList = storage.stream()
                .filter(user -> user.getBirthDate().isAfter(from) && user.getBirthDate().isBefore(to))
                .toList();
        return ResponseEntity.ok(new UserDataListResponse(HttpStatus.OK.value(), userDataList));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDataResponse> updateUser(@PathVariable(name = "id") long id,
                                                       @Valid @RequestBody UserUpdateRequest request) {
        User user = findById(id);
        updateUser(request, user);
        return ResponseEntity.ok(new UserDataResponse(200, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") long id) {
        storage.remove(findById(id));
        return ResponseEntity.noContent().build();
    }

    private boolean validAge(LocalDate userBirthData) {
        return (int) ChronoUnit.YEARS.between(userBirthData, LocalDate.now()) >= minAge;
    }

    private boolean userExist(User user) {
        return storage.contains(user);
    }

    private User findById(long id) {
        Optional<User> user = storage.stream().filter(u -> u.getId() == id).findFirst();
        return user.orElseThrow(() -> new UserNotFoundException("User with id not found"));
    }

    private void updateUser(UserUpdateRequest updateRequest, User user) {
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getBirthData() != null) {
            user.setBirthDate(updateRequest.getBirthData());
        }
        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
    }
}
