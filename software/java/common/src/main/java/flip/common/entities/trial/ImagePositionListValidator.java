package flip.common.entities.trial;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ImagePositionListValidator implements ConstraintValidator<ImagePositionListConstraint, List<Integer>> {

    public ImagePositionListValidator() {
        super();
    }

    @Override
    public void initialize(ImagePositionListConstraint constraint) {
    }

    @Override
    public boolean isValid(List<Integer> imagePositions, ConstraintValidatorContext cxt) {
        ImagePositionValidator validator = new ImagePositionValidator();
        return imagePositions.stream().allMatch((imagePosition) -> validator.isValid(imagePosition, cxt));
    }
}
