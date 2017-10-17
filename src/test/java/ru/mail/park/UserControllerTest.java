package ru.mail.park;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.requests.Requests;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate template;

    @Test
    public void testSignupUser() throws Exception {
        //BAD_REQUEST
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signup")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SignupRequest("", "", ""))))
                .andExpect(status().is4xxClientError());
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signup")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SignupRequest("login", "password", "email"))))
                .andExpect(status().isOk());
        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signup")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SignupRequest("login", "password", "email"))))
                .andExpect(status().is4xxClientError());
        assertEquals(1, countRowsInTable(template, "users"));
    }

    @Test
    public void testSigninUser() throws Exception {
        //SignUp
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signup")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SignupRequest("login", "password", "email"))))
                .andExpect(status().isOk());
        //BAD_REQUEST
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("", ""))))
                .andExpect(status().is4xxClientError());
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("login", "password"))))
                .andExpect(status().isOk());
        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .sessionAttr("login", "login")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("login", "password"))))
                .andExpect(status().is4xxClientError());
        //NOT_FOUND
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("another_login", "password"))))
                .andExpect(status().is4xxClientError());
        //UNAUTHORIZED
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("login", "another_password"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testLogout() throws Exception {
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/logout")
                .sessionAttr("login", "login")
                .header("content-type", "application/json"))
                .andExpect(status().isOk());
        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/logout")
                .header("content-type", "application/json"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testCurrent() throws Exception {
        //SignUp
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signup")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SignupRequest("login", "password", "email"))))
                .andExpect(status().isOk());
        //SignIn
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("login", "password"))))
                .andExpect(status().isOk());
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.get("/restapi/current")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.email").value("email"));
        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.get("/restapi/current"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testChange() throws Exception {
        //SignUp
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signup")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SignupRequest("login", "password", "email"))))
                .andExpect(status().isOk());
        //SignIn
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("login", "password"))))
                .andExpect(status().isOk());
        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/settings")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SettingsRequest("new_email", "new_password"))))
                .andExpect(status().is4xxClientError());
        //NOT_FOUND
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/settings")
                .sessionAttr("login", "another_login")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SettingsRequest("new_email", "new_password"))))
                .andExpect(status().is4xxClientError());
        //BAD_REQUEST
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/settings")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SettingsRequest("", ""))))
                .andExpect(status().is4xxClientError());
        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/settings")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SettingsRequest("new_email", "new_password"))))
                .andExpect(status().is4xxClientError());
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/settings")
                .sessionAttr("login", "login")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SettingsRequest("new_email", "new_password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new_email"));
        //Logout
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/logout")
                .sessionAttr("login", "login")
                .header("content-type", "application/json"))
                .andExpect(status().isOk());
        //SignIn
        mockMvc.perform(MockMvcRequestBuilders.post("/restapi/signin")
                .header("content-type", "application/json")
                .content(Requests.makeJson(new Requests.SigninRequest("login", "new_password"))))
                .andExpect(status().isOk());
    }
}
