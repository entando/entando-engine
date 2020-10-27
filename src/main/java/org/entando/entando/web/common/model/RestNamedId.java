package org.entando.entando.web.common.model;

import org.springframework.data.annotation.Transient;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;

/**
 * Class to support both common and named ids:
 * <pre>
 * A named id is an key-value assignment expression in the form:
 * - {name}={value}
 *
 * Please note that "name" can be a zero-length string, this way you will still
 * be allowed to enter no-name identifiers containing equals signs
 *
 * A common id is instead just an identified that doesn't contain the equals signs
 *
 * Finally, note that you can use this class as <b>@PathVariable</b>
 *
 * </pre>
 */
public class RestNamedId implements Serializable {

    public static final char SEPARATOR = '=';
    public static final String NO_NAME = "";

    private final String rawId;
    @Transient
    public final String name;
    @Transient
    public final String value;

    /**
     * Parse and builds a named id from its raw form
     *
     * @param rawId the raw named id, null is treated like an empty string
     */
    public RestNamedId(@Nullable String rawId) {
        this.rawId = rawId;
        if (rawId == null) rawId="";
        int pos = rawId.indexOf(SEPARATOR);
        if (pos == -1) {
            name = NO_NAME;
            value = rawId;
        } else {
            name = rawId.substring(0, pos);
            value = rawId.substring(pos + 1);
        }
    }

    /**
     * Name validation check.
     * Tells if the object name equals the one provided
     */
    @SuppressWarnings("PointlessNullCheck")
    public boolean hasName(@NotNull String name) {
        return (name != null && this.name.equals(name));
    }

    /**
     * Name validation check.
     * Tells if the object name has a name
     */
    public boolean hasName() {
        return !this.name.isEmpty();
    }

    /**
     * <p>Value extraction validated by name</p>
     * <p>Fallbacks if and only if validation doesn't pass</p>
     *
     * @param mustHaveName the expected mustHaveName
     * @return an optional that may contain the parsed value of the id expression
     */
    public Optional<String> getValidValue(@NotNull String mustHaveName) {
        return this.hasName(mustHaveName) ? Optional.of(this.value) : Optional.empty();
    }

    /**
     * Build an object form a raw id
     *
     * @see #RestNamedId(String)
     */
    public static RestNamedId from(String rawId) {
        return new RestNamedId(rawId);
    }

    /**
     * Build an named object form its components
     *
     * @see #RestNamedId(String)
     */
    public static RestNamedId of(@Nullable String name, @Nullable String value) {
        if (name == null || name.isEmpty()) {
            if (value == null || value.isEmpty()) return new RestNamedId("");
            if (value.indexOf(SEPARATOR) == -1) {
                return new RestNamedId(value);
            } else {
                return new RestNamedId(SEPARATOR + value);
            }
        } else {
            return new RestNamedId(name + SEPARATOR + value);
        }
    }

    @Override
    public String toString() {
        return rawId;
    }
}
