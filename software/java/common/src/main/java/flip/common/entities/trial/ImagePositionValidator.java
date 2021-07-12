package flip.common.entities.trial;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ImagePositionValidator implements ConstraintValidator<ImagePositionConstraint, Integer> {

    public ImagePositionValidator() {
        super();
    }

    @Override
    public void initialize(ImagePositionConstraint constraint) {
    }

    @Override
    public boolean isValid(Integer imagePosition, ConstraintValidatorContext cxt) {
        return imagePosition == null || (0 <= imagePosition && imagePosition < 6);
    }
}
