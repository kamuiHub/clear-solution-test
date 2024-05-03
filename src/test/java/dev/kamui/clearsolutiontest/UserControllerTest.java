package dev.kamui.clearsolutiontest;

import com.jayway.jsonpath.JsonPath;
import dev.kamui.clearsolutiontest.controller.UserController;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    private MvcResult defaultUser;

    @BeforeEach
    public void init() throws Exception {
        String createDefaultUserRequest =
                """
                        {
                             "first_name": "Frodo",
                             "last_name": "Baggins",
                             "email": "frodo@gmail.com",
                             "birth_data": "2004-10-31"
                         }
                        """;

        defaultUser = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createDefaultUserRequest))
                .andReturn();
    }


    @Test
    @DirtiesContext
    public void shouldCreateUserWithoutAddressAndPhoneFields() throws Exception {
        String requestBody =
                """
                        {
                            "first_name": "Bilbo",
                            "last_name": "Baggins",
                            "email": "bilbo@gmail.com",
                            "birth_data": "2001-10-31"
                        }
                        """;

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(201)))
                .andExpect(jsonPath("$.data.first_name", is("Bilbo")))
                .andExpect(jsonPath("$.data.last_name", is("Baggins")))
                .andExpect(jsonPath("$.data.email", is("bilbo@gmail.com")))
                .andExpect(jsonPath("$.data.birth_date", is("2001-10-31")))
                .andExpect(jsonPath("$.data.phone_number").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data.address").value(IsNull.nullValue()));
    }

    @Test
    @DirtiesContext
    public void shouldCreateUserWithAllFields() throws Exception {
        String requestBody =
                """
                        {
                             "first_name": "Bilbo",
                             "last_name": "Baggins",
                             "email": "bilbo@gmail.com",
                             "birth_data": "2001-10-31",
                             "address": {
                                 "country": "Ukraine",
                                 "city": "Odesa",
                                 "state": "Odesa",
                                 "zip": "042445",
                                 "street": "Street"
                             },
                             "phone_number": "380991815459"
                         }
                        """;

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(201)))
                .andExpect(jsonPath("$.data.first_name", is("Bilbo")))
                .andExpect(jsonPath("$.data.last_name", is("Baggins")))
                .andExpect(jsonPath("$.data.email", is("bilbo@gmail.com")))
                .andExpect(jsonPath("$.data.birth_date", is("2001-10-31")))
                .andExpect(jsonPath("$.data.address.country", is("Ukraine")))
                .andExpect(jsonPath("$.data.phone_number", is("380991815459")));
    }

    @Test
    @DirtiesContext
    public void shouldFailedByYoungAge() throws Exception {
        String requestBody =
                """
                        {
                             "first_name": "Bilbo",
                             "last_name": "Baggins",
                             "email": "bilbo@gmail.com",
                             "birth_data": "2006-10-31"
                         }
                        """;

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_details.code", is(400)))
                .andExpect(jsonPath("$.error_details.message", is("age is less than 18")));
    }

    @Test
    @DirtiesContext
    public void shouldFailedByInvalidEmail() throws Exception {
        String requestBody =
                """
                        {
                             "first_name": "Bilbo",
                             "last_name": "Baggins",
                             "email": "bilbo.gmail.com",
                             "birth_data": "2004-10-31"
                         }
                        """;

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_details.code", is(400)))
                .andExpect(jsonPath("$.error_details.message", is("must be a valid e-mail address")));
    }

    @Test
    @DirtiesContext
    public void shouldUpdateUserField() throws Exception {
        int userId = JsonPath.read(defaultUser.getResponse().getContentAsString(), "$.data.id");

        String updateRequestBody =
                """
                        {
                             "phone_number": "380991815459"
                         }
                        """;

        mvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.phone_number", is("380991815459")));
    }


    @Test
    @DirtiesContext
    public void shouldUpdateAllUserFields() throws Exception {
        int userId = JsonPath.read(defaultUser.getResponse().getContentAsString(), "$.data.id");

        String updateRequestBody =
                """
                        {
                             "first_name": "Bilbo2",
                             "last_name": "Baggins2",
                             "email": "bilbo2@gmail.com",
                             "birth_data": "2001-10-31",
                             "address": {
                                 "country": "Ukraine",
                                 "city": "Odesa",
                                 "state": "Odesa",
                                 "zip": "042445",
                                 "street": "Street"
                             },
                             "phone_number": "380991815459"
                         }
                        """;

        mvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.first_name", is("Bilbo2")))
                .andExpect(jsonPath("$.data.last_name", is("Baggins2")))
                .andExpect(jsonPath("$.data.email", is("bilbo2@gmail.com")))
                .andExpect(jsonPath("$.data.address.country", is("Ukraine")))
                .andExpect(jsonPath("$.data.phone_number", is("380991815459")));
    }

    @Test
    @DirtiesContext
    public void shouldFailedUpdateIfUserNotExist() throws Exception {
        String updateRequestBody =
                """
                        {
                             "phone_number": "380991815459"
                         }
                        """;

        mvc.perform(put("/api/v1/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_details.code", is(404)))
                .andExpect(jsonPath("$.error_details.message", is("user not found")));
    }


    @Test
    @DirtiesContext
    public void shouldDeleteUser() throws Exception {
        int userId = JsonPath.read(defaultUser.getResponse().getContentAsString(), "$.data.id");

        mvc.perform(delete("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //verify that the user has been deleted
        mvc.perform(delete("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    @DirtiesContext
    public void shouldFailedDeleteIfUserNotExist() throws Exception {
        mvc.perform(delete("/api/v1/users/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_details.code", is(404)))
                .andExpect(jsonPath("$.error_details.message", is("user not found")));
    }

    @Test
    @DirtiesContext
    public void shouldReturnUsersArrayWithValidBirthDate() throws Exception {
        mvc.perform(get("/api/v1/users")
                        .param("From", "2003-01-01")
                        .param("To", "2004-11-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].birth_date", is("2004-10-31")));
    }

    @Test
    @DirtiesContext
    public void shouldReturnEmptyArray() throws Exception {
        mvc.perform(get("/api/v1/users")
                        .param("From", "2001-01-01")
                        .param("To", "2003-11-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext
    public void shouldReturnBadRequestWithInvalidParameters() throws Exception {
        mvc.perform(get("/api/v1/users")
                        .param("From", "2004-01-01")
                        .param("To", "2001-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}