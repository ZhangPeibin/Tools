package com.milk.tools.common;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by peibin on 17-6-9.
 */
/**
 * Annotation type used to mark program elements that should no longer be used
 * by programmers. Compilers produce a warning if a deprecated program element
 * is used.
 *
 * @since 1.5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DeprecatedValue {
    String value();
}