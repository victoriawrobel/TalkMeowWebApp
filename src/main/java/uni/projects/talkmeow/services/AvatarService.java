package uni.projects.talkmeow.services;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uni.projects.talkmeow.components.avatar.*;
import uni.projects.talkmeow.repositories.AvatarRepository;

import java.util.List;

@Service
public class AvatarService {

    @Autowired
    private AvatarRepository avatarRepository;

    public List<Avatar> getFilteredAvatars(Age age, Breed breed, Color furColor, Color eyeColor, Pattern pattern) {
        return avatarRepository.findAll(
                Specification.where(AvatarSpecifications.hasFurColor(furColor))
                        .and(AvatarSpecifications.hasEyeColor(eyeColor))
                        .and(AvatarSpecifications.hasPattern(pattern))
                        .and(AvatarSpecifications.hasBreed(breed))
                        .and(AvatarSpecifications.hasAge(age))
        );
    }

    public Avatar getAvatarById(Long id) {
        return avatarRepository.findById(id).orElse(null);
    }
}

