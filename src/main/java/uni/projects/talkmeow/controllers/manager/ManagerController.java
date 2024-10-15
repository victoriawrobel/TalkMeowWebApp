package uni.projects.talkmeow.controllers.manager;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.talkmeow.components.avatar.*;
import uni.projects.talkmeow.repositories.AvatarRepository;
import uni.projects.talkmeow.services.AvatarService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static uni.projects.talkmeow.utility.ImageProcessor.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {


    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private AvatarService avatarService;

    @GetMapping("/home")
    public String home() {
        return "manager/dashboard";
    }

    @GetMapping("/avatars")
    public String avatars(@RequestParam(value = "furColor", required = false) Color furColor,
                          @RequestParam(value = "eyeColor", required = false) Color eyeColor,
                          @RequestParam(value = "pattern", required = false) Pattern pattern,
                          @RequestParam(value = "breed", required = false) Breed breed,
                          @RequestParam(value = "age", required = false) Age age,
                          Model model) {
        List<Avatar> filteredAvatars = avatarService.getFilteredAvatars(age, breed, furColor, eyeColor, pattern);
        model.addAttribute("avatars", filteredAvatars);
        model.addAttribute("age", Age.values());
        model.addAttribute("breed", Breed.values());
        model.addAttribute("color", Color.values());
        model.addAttribute("pattern", Pattern.values());
        return "manager/avatars";
    }

    @GetMapping("/add-avatar")
    public String addAvatar(Model model) {
        model.addAttribute("avatar", new Avatar());
        model.addAttribute("age", Age.values());
        model.addAttribute("breed", Breed.values());
        model.addAttribute("color", Color.values());
        model.addAttribute("pattern", Pattern.values());
        return "manager/add-avatar";
    }

    @SneakyThrows
    @PostMapping("/add-avatar")
    public String addAvatar(@RequestParam(value = "imageData") MultipartFile image,
                            @RequestParam(value = "furColor") Color furColor,
                            @RequestParam("eyeColor") Color eyeColor,
                            @RequestParam("pattern") Pattern pattern,
                            @RequestParam("breed") Breed breed,
                            @RequestParam("age") Age age,
                            @RequestParam("source") String source) {

        if (image.isEmpty()) {
            return "redirect:/manager/add-avatar?error=image_required";
        }

        if (furColor == null) {
            return "redirect:/manager/add-avatar?error=fur_color_required";
        }

        if (eyeColor == null) {
            return "redirect:/manager/add-avatar?error=eye_color_required";
        }

        if (pattern == null) {
            return "redirect:/manager/add-avatar?error=pattern_required";
        }

        if (breed == null) {
            return "redirect:/manager/add-avatar?error=breed_required";
        }

        if (age == null) {
            return "redirect:/manager/add-avatar?error=age_required";
        }

        if (source == null || source.trim().isEmpty()) {
            return "redirect:/manager/add-avatar?error=source_required";
        }

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
