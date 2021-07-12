package flip.common.entities.trial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Random;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.apache.commons.math3.distribution.NormalDistribution;


@Embeddable
@Data
@FixedDelayConstraint
@RandomNormalDelayConstraint
@RandomUniformDelayConstraint
public final class Delay {

    public enum Type {
        NONE,
        FIXED,
        RANDOM_NORMAL,
        RANDOM_UNIFORM,
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    Type type;

    Long fixedValue;

    Long randomNormalMean;
    Long randomNormalStdDev;

    Long randomUniformMin;
    Long randomUniformMax;


    public long perform(Random random) throws InterruptedException {
        long value = getSleepValue(random);
        Thread.sleep(value);
        return value;
    }

    @Override
    public String toString() {
        switch (type) {
        case NONE:           return "None";
        case FIXED:          return String.format("%dms", fixedValue);
        case RANDOM_NORMAL:  return String.format("mean %dms stddev %dms", randomNormalMean, randomNormalStdDev);
        case RANDOM_UNIFORM: return String.format("min %dms max %dms", randomUniformMin, randomUniformMax);
        default: throw new RuntimeException();
        }
    }

    long getSleepValue(Random random) {
        switch (type) {
        case NONE:           return 0;
        case FIXED:          return fixedValue;
        case RANDOM_NORMAL:  return (long)(random.nextGaussian() * randomNormalStdDev) + randomNormalMean;
        case RANDOM_UNIFORM: return (long)(random.nextDouble() * (randomUniformMax - randomUniformMin)) + randomUniformMin;
        default: throw new RuntimeException();
        }
    }

    public static class FixedDelayValidator implements ConstraintValidator<FixedDelayConstraint, Delay> {

        @Override
        public boolean isValid(Delay delay, ConstraintValidatorContext cxt) {
            if (delay != null && delay.type == Delay.Type.FIXED) {
                boolean isValid = delay.fixedValue != null && 0 <= delay.fixedValue && delay.fixedValue <= 10000;

                if (!isValid) {
                    cxt.disableDefaultConstraintViolation();
                    cxt.buildConstraintViolationWithTemplate(cxt.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("fixedValue")
                        .addConstraintViolation();
                }
                
                return isValid;
            }
            return true;
        }
    }

    public static class RandomNormalDelayValidator implements ConstraintValidator<RandomNormalDelayConstraint, Delay> {

        @Override
        public boolean isValid(Delay delay, ConstraintValidatorContext cxt) {
            if (delay != null && delay.type == Delay.Type.RANDOM_NORMAL) {
                boolean isValid =
                    delay.randomNormalMean != null &&
                    delay.randomNormalStdDev != null &&
                    new NormalDistribution(delay.randomNormalMean, delay.randomNormalStdDev)
                    .cumulativeProbability(10000) >= 0.9;

                if (!isValid) {
                    cxt.disableDefaultConstraintViolation();
                    cxt.buildConstraintViolationWithTemplate(cxt.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("randomNormalMean")
                        .addConstraintViolation();
                }

                return isValid;
            }
            return true;
        }
    }

    public static class RandomUniformDelayValidator implements ConstraintValidator<RandomUniformDelayConstraint, Delay> {

        @Override
        public boolean isValid(Delay delay, ConstraintValidatorContext cxt) {
            if (delay != null && delay.type == Delay.Type.RANDOM_UNIFORM) {
                boolean isValid =
                    delay.randomUniformMin != null && delay.randomUniformMin >= 0 &&
                    delay.randomUniformMax != null && delay.randomUniformMax <= 10000 &&
                    delay.randomUniformMin <= delay.randomUniformMax;

                if (!isValid) {
                    cxt.disableDefaultConstraintViolation();
                    cxt.buildConstraintViolationWithTemplate(cxt.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("randomUniformMin")
                        .addConstraintViolation();
                }

                return isValid;
            }
            return true;
        }
    }
}

@Constraint(validatedBy = Delay.FixedDelayValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@interface FixedDelayConstraint {
    String message() default "invalid fixed delay, must be between 0ms and 10000ms";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Constraint(validatedBy = Delay.RandomNormalDelayValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@interface RandomNormalDelayConstraint {
    String message() default "invalid random normal delay, must have p(>=10000ms) <= 0.1";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Constraint(validatedBy = Delay.RandomUniformDelayValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@interface RandomUniformDelayConstraint {
    String message() default "invalid random uniform delay, must be min >= 0ms, max <= 10000ms";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
