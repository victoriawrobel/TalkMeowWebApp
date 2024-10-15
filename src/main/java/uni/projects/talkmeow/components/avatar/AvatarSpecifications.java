package uni.projects.talkmeow.components.avatar;

import org.springframework.data.jpa.domain.Specification;

public class AvatarSpecifications {

    public static Specification<Avatar> hasFurColor(Color furColor) {
        return (root, query, criteriaBuilder) ->
                furColor == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("furColor"), furColor);
    }

    public static Specification<Avatar> hasEyeColor(Color eyeColor) {
        return (root, query, criteriaBuilder) ->
                eyeColor == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("eyeColor"), eyeColor);
    }

    public static Specification<Avatar> hasPattern(Pattern pattern) {
        return (root, query, criteriaBuilder) ->
                pattern == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("pattern"), pattern);
    }

    public static Specification<Avatar> hasBreed(Breed breed) {
        return (root, query, criteriaBuilder) ->
                breed == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("breed"), breed);
    }

    public static Specification<Avatar> hasAge(Age age) {
        return (root, query, criteriaBuilder) ->
                age == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("age"), age);
    }
}
