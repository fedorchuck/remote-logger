package com.github.fedorchuck.remote_logger.commont.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 * @see <a href="https://stackoverflow.com/questions/21926337/how-to-configure-gson-to-serialize-a-set-of-jsr-303-constraintviolation-objects/21954823#21954823">https://stackoverflow.com/questions/21926337/how-to-configure-gson-to-serialize-a-set-of-jsr-303-constraintviolation-objects/21954823#21954823</a>
 */
@Getter
@Setter
@ToString
public class ValidationError {
    private String className;
    private String propertyPath;
    private String errorMessage;

    public static Set<ValidationError> fromViolations(Set violations) {
        Set<ValidationError> errors = new HashSet<ValidationError>();

        for (Object o : violations) {
            ConstraintViolation v = (ConstraintViolation) o;

            ValidationError error = new ValidationError();
            error.setClassName(v.getRootBeanClass().getSimpleName());
            error.setErrorMessage(v.getMessage());
            error.setPropertyPath(v.getPropertyPath().toString());
            errors.add(error);
        }

        return errors;
    }

}
