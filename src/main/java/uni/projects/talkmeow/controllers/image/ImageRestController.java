package uni.projects.talkmeow.controllers.image;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.talkmeow.components.avatar.Avatar;
import uni.projects.talkmeow.repositories.AvatarRepository;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/image")
public class ImageRestController {

    @Autowired
    private AvatarRepository avatarRepository;

    @SneakyThrows
    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Avatar not found"));
        return getResponseEntity(avatar.getImage());
    }

    private ResponseEntity<byte[]> getResponseEntity(byte[] image) {
        ByteBuffer imageBuffer = ByteBuffer.wrap(image);
        byte[] imageData = new byte[imageBuffer.remaining()];
        imageBuffer.get(imageData);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }
}
