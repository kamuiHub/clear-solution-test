package dev.kamui.clearsolutiontest.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import dev.kamui.clearsolutiontest.model.Address;
import dev.kamui.clearsolutiontest.model.User;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRequest {
    @NotBlank(message = "first_name must not be empty")
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank(message = "last_name must not be empty")
    @Size(min = 2, max = 20)
    private String lastName;

    @NotNull(message = "e-mail address must not be empty")
    @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "must be a valid e-mail address")
    private String email;

    @NotNull(message = "birth_data must not be empty")
    @Past(message = "birth_data must be earlier than current date")
    private LocalDate birthData;

    private Address address;

    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$", message = "must be a valid phone_number")
    private String phoneNumber;

    public static User convertToNewUser(UserRequest userRequest, long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setBirthDate(userRequest.getBirthData());
        user.setAddress(userRequest.getAddress());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        return user;
    }
}