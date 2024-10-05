package uni.projects.talkmeow.controllers.manager;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.talkmeow.components.avatar.*;
import uni.projects.talkmeow.repositories.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static uni.projects.talkmeow.utility.ImageProcessor.*;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 05.10.2024
 */

@Controller
@RequestMapping("/manager")
public class ManagerController {


    @Autowired
    private AvatarRepository avatarRepository;

    @GetMapping("/home")
    public String home() {
        return "manager/dashboard";
    }

    @GetMapping("/avatars")
    public String avatars(Model model) {
        model.addAttribute("avatars", avatarRepository.findAll());
        return "manager/avatars";
    }

    @GetMapping("/add-avatar")
    public String addAvatar(Model model) {
        model.addAttribute("avatar", new Avatar());
        model.addAttribute("age", Age.values());
        model.addAttribute("breed", Breed.values());
        model.addAttribute("color", Color.values());
        model.addAttribute("pattern", Patters.values());
        return "manager/add-avatar";
    }

    @SneakyThrows
    @PostMapping("/add-avatar")
    public String addAvatar(@RequestParam("imageData") MultipartFile image,
                            @RequestParam("furColor") Color furColor,
                            @RequestParam("eyeColor") Color eyeColor,
                            @RequestParam("pattern") Patters pattern,
                            @RequestParam("breed") Breed breed,
                            @RequestParam("age") Age age,
                            @RequestParam("source") String source,
                            Model model) throws IOException {
        Avatar avatar = new Avatar();
        if (!image.isEmpty()) {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            int angle = bufferedImage.getHeight() > bufferedImage.getWidth() ? 90 : 0;
            bufferedImage = rotateImage(bufferedImage, angle);
            bufferedImage = resizeAndCropImage(bufferedImage, 200, 200);
            byte[] imageBytes = imageToByteArray(bufferedImage, image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1));
            avatar.setImage(imageBytes);
        } else {
            avatar.setImage(new byte[0]);
        }
        avatar.setFurColor(furColor);
        avatar.setEyeColor(eyeColor);
        avatar.setPattern(pattern);
        avatar.setBreed(breed);
        avatar.setAge(age);
        avatar.setSource(source);

        avatarRepository.save(avatar);
        return "redirect:/manager/add-avatar";
    }
}
