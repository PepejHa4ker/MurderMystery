package com.pepej.murdermystery.perk;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PerkAnn {

    boolean shouldBeLoaded() default true;

}

