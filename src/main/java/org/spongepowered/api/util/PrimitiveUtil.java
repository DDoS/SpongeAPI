package org.spongepowered.api.util;

import com.google.common.base.Optional;

public final class PrimitiveUtil {

    private PrimitiveUtil() {
    }

    public static Optional<Character> asCharacter(Object object) {
        if (object instanceof Character) {
            return Optional.of((Character) object);
        }
        try {
            return Optional.of(object.toString().charAt(0));
        } catch (Exception e) {
            // do nothing
        }
        return Optional.absent();
    }

    public static Optional<Boolean> asBoolean(Object object) {
        if (object instanceof Boolean) {
            return Optional.of((Boolean) object);
        }
        return Optional.absent();
    }

    public static Optional<Byte> asByte(Object object) {
        if (object instanceof Number) {
            return Optional.of(((Number) object).byteValue());
        }

        try {
            return Optional.of(Byte.valueOf(object.toString()));
        } catch (Exception e) {
            // do nothing
        }
        return Optional.absent();
    }

    public static Optional<Short> asShort(Object object) {
        if (object instanceof Number) {
            return Optional.of(((Number) object).shortValue());
        }

        try {
            return Optional.of(Short.valueOf(object.toString()));
        } catch (NumberFormatException e) {
            // do nothing
        } catch (NullPointerException e) {
            // do nothing
        }
        return Optional.absent();
    }

    public static Optional<Long> asLong(Object object) {
        if (object instanceof Number) {
            return Optional.of(((Number) object).longValue());
        }

        try {
            return Optional.of(Long.valueOf(object.toString()));
        } catch (NumberFormatException e) {
            // do nothing
        } catch (NullPointerException e) {
            // do nothing
        }
        return Optional.absent();
    }

    public static Optional<Float> asFloat(Object object) {
        if (object instanceof Number) {
            return Optional.of(((Number) object).floatValue());
        }

        try {
            return Optional.of(Float.valueOf(object.toString()));
        } catch (NumberFormatException e) {
            // do nothing
        } catch (NullPointerException e) {
            // do nothing
        }
        return Optional.absent();
    }

    public static Optional<Double> asDouble(Object object) {
        if (object instanceof Number) {
            return Optional.of(((Number) object).doubleValue());
        }

        try {
            return Optional.of(Double.valueOf(object.toString()));
        } catch (NumberFormatException e) {
            // do nothing
        } catch (NullPointerException e) {
            // do nothing
        }
        return Optional.absent();
    }

    public static Optional<Integer> asInteger(Object object) {
        if (object instanceof Number) {
            return Optional.of(((Number) object).intValue());
        }

        try {
            return Optional.of(Integer.valueOf(object.toString()));
        } catch (NumberFormatException e) {
            // do nothing
        } catch (NullPointerException e) {
            // do nothing
        }
        return Optional.absent();
    }

    public static Optional<String> asString(Object object) {
        if (object instanceof String) {
            return Optional.of((String) object);
        } else if (object == null) {
            return Optional.absent();
        } else {
            return Optional.of(object.toString());
        }
    }
}
