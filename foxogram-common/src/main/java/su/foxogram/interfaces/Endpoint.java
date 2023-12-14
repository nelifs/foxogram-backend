package su.foxogram.interfaces;

import su.foxogram.enums.APIEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
    APIEnum.Endpoints path();
}