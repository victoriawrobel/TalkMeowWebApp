package uni.projects.talkmeow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import uni.projects.talkmeow.components.avatar.Avatar;
import uni.projects.talkmeow.repositories.AvatarRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uni.projects.talkmeow.Utility.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ManagerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvatarRepository avatarRepository;

    @Test
    @DisplayName("Manager panel is accessible")
    void adminPanelIsAccessible() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);
        this.mockMvc.perform(get("/manager/home").session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Manager panel has the necessary components")
    void managerPanelHasNecessaryComponents() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        this.mockMvc.perform(get("/manager/home").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Manager dashboard")))
                .andExpect(content().string(containsString("View Avatars")))
                .andExpect(content().string(containsString("Add Avatar")));
    }

    @Test
    @DisplayName("Manager panel has working navigation links")
    void managerPanelHasWorkingLinks() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        this.mockMvc.perform(get("/manager/avatars").session(session))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/manager/add-avatar").session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Avatar list page is accessible")
    void avatarListPageIsAccessible() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        this.mockMvc.perform(get("/manager/avatars").session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Avatar list page contains avatars")
    void avatarListPageContainsAvatars() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        this.mockMvc.perform(get("/manager/avatars").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Avatar Image")));
    }

    @Test
    @DisplayName("Avatar list page filters work")
    void avatarListPageFiltersWork() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        this.mockMvc.perform(get("/manager/avatars?furColor=WHITE").session(session))
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='avatar-info']/p/span[text()='WHITE']").exists());

        this.mockMvc.perform(get("/manager/avatars?eyeColor=GREEN").session(session))
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='avatar-info']/p/span[text()='GREEN']").exists());

        this.mockMvc.perform(get("/manager/avatars?breed=BRITISH_SHORTHAIR").session(session))
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='avatar-info']/p/span[text()='BRITISH_SHORTHAIR']").exists());
    }

    @Test
    @DisplayName("Avatar list page shows multiple avatars")
    void avatarListPageShowsMultipleAvatars() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        this.mockMvc.perform(get("/manager/avatars").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Avatar Image")))
                .andExpect(content().string(containsString("Fur Color"))); // Ensure multiple avatars' details appear
    }

    @Test
    @DisplayName("Avatar list page filters with multiple criteria")
    void avatarListPageFiltersMultipleCriteria() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        this.mockMvc.perform(get("/manager/avatars?furColor=BLACK&eyeColor=ORANGE&breed=RAGDOLL").session(session))
                .andExpect(status().isOk())

                .andExpect(xpath("//div[@class='avatar-info']/p/span[text()='BLACK']").exists())
                .andExpect(xpath("//div[@class='avatar-info']/p/span[text()='ORANGE']").exists())
                .andExpect(xpath("//div[@class='avatar-info']/p/span[text()='RAGDOLL']").exists());
    }
    @Test
    @DisplayName("Should return error when image is not provided")
    void shouldReturnErrorWhenImageIsNotProvided() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(new MockMultipartFile("imageData", "", MediaType.IMAGE_JPEG_VALUE, new byte[0]))
                        .param("furColor", "BLACK")
                        .param("eyeColor", "ORANGE")
                        .param("pattern", "SOLID")
                        .param("breed", "RAGDOLL")
                        .param("age", "YOUNG")
                        .param("source", "Generated Image").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/add-avatar?error=image_required"));
    }

    @Test
    @DisplayName("Should return error when fur color is not selected")
    void shouldReturnErrorWhenFurColorIsNotProvided() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);


        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(getPlaceholderImage())
                        .param("furColor", "")
                        .param("eyeColor", "ORANGE")
                        .param("pattern", "SOLID")
                        .param("breed", "RAGDOLL")
                        .param("age", "YOUNG")
                        .param("source", "Generated Image").session(session))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return error when eye color is not selected")
    void shouldReturnErrorWhenEyeColorIsNotProvided() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(getPlaceholderImage())
                        .param("furColor", "BLACK")
                        .param("eyeColor", "")
                        .param("pattern", "SOLID")
                        .param("breed", "RAGDOLL")
                        .param("age", "YOUNG")
                        .param("source", "Generated Image").session(session))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return error when pattern is not selected")
    void shouldReturnErrorWhenPatternIsNotProvided() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(getPlaceholderImage())
                        .param("furColor", "BLACK")
                        .param("eyeColor", "ORANGE")
                        .param("pattern", "")
                        .param("breed", "RAGDOLL")
                        .param("age", "YOUNG")
                        .param("source", "Generated Image").session(session))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return error when breed is not selected")
    void shouldReturnErrorWhenBreedIsNotProvided() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(getPlaceholderImage())
                        .param("furColor", "BLACK")
                        .param("eyeColor", "ORANGE")
                        .param("pattern", "SOLID")
                        .param("breed", "")
                        .param("age", "YOUNG")
                        .param("source", "Generated Image").session(session))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return error when age is not selected")
    void shouldReturnErrorWhenAgeIsNotProvided() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(getPlaceholderImage())
                        .param("furColor", "BLACK")
                        .param("eyeColor", "ORANGE")
                        .param("pattern", "SOLID")
                        .param("breed", "RAGDOLL")
                        .param("age", "")
                        .param("source", "Generated Image").session(session))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return error when source is not provided")
    void shouldReturnErrorWhenSourceIsNotProvided() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);

        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(getPlaceholderImage())
                        .param("furColor", "BLACK")
                        .param("eyeColor", "ORANGE")
                        .param("pattern", "SOLID")
                        .param("breed", "RAGDOLL")
                        .param("age", "YOUNG")
                        .param("source", "").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/add-avatar?error=source_required"));
    }

    private MockMultipartFile getPlaceholderImage() throws IOException {
        File file = new File(placeholderImagePath);
        return new MockMultipartFile("imageData", file.getName(), MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(file.toPath()));
    }

    @Test
    @DisplayName("Should add avatar successfully with correct image and details")
    void shouldAddAvatarSuccessfully() throws Exception {
        MockHttpSession session = login(managerUsername, managerPassword, mockMvc);
        MockMultipartFile imageFile = getPlaceholderImage();
        String source = randomStringUsername(32);

        mockMvc.perform(multipart("/manager/add-avatar")
                        .file(imageFile) // Valid image
                        .param("furColor", "BLACK")
                        .param("eyeColor", "ORANGE")
                        .param("pattern", "SOLID")
                        .param("breed", "RAGDOLL")
                        .param("age", "YOUNG")
                        .param("source", source).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/add-avatar"));

        Avatar savedAvatar = avatarRepository.findBySource(source);

        assertNotNull(savedAvatar);
        assertNotNull(savedAvatar.getImage());
        assertEquals("BLACK", savedAvatar.getFurColor().toString());
        assertEquals("ORANGE", savedAvatar.getEyeColor().toString());
        assertEquals("SOLID", savedAvatar.getPattern().toString());
        assertEquals("RAGDOLL", savedAvatar.getBreed().toString());
        assertEquals("YOUNG", savedAvatar.getAge().toString());
        assertEquals(source, savedAvatar.getSource());

        avatarRepository.deleteBySource(source);
    }

}
