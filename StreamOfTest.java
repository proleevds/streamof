import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamOfTest {

    @Test
    void test() {
        HashSet<EnumExt> input = Sets.newHashSet(EnumExt.valueOf(Enum1.ANOTHER), EnumExt.Z);
        HashSet<Enum1> expected = Sets.newHashSet(Enum1.Y, Enum1.X, Enum1.ANOTHER);
        Assertions.assertEquals(expected, processFields1(input));
        Assertions.assertEquals(expected, processFields2(input));
    }

    static Set<Enum1> processFields1(Set<EnumExt> fields) {
        Set<Enum1> result = fields.stream()
            .filter(f -> !EnumExt.Z.equals(f))
            .map(EnumExt::baseValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        if (fields.contains(EnumExt.Z)) {
            result.add(Enum1.X);
            result.add(Enum1.Y);
        }
        return result;
    }

    static Set<Enum1> processFields2(Set<EnumExt> fields) {
        return fields.stream()
            .flatMap(f ->
                EnumExt.Z.equals(f) ?
                    Stream.of(EnumExt.valueOf(Enum1.X), EnumExt.valueOf(Enum1.Y)) :
                    Stream.of(f))
            .map(EnumExt::baseValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
    }

    public static class EnumExt {
        private static final Map<String, EnumExt> values = new HashMap<>();
        public static final EnumExt Z = new EnumExt("Z");

        static {
            // here it was simplified: originally util class goes to Enum1 and extracts possible values via Reflect
            List<Enum1> enum1Values = Arrays.asList(Enum1.X, Enum1.Y, Enum1.ANOTHER);
            for (Enum1 e : enum1Values) {
                values.put(e.name(), new EnumExt(e));
            }
        }

        private final Enum1 baseValue;
        private final String name;

        public EnumExt(Enum1 baseValue) {
            this.name = null;
            this.baseValue = baseValue;
        }

        public EnumExt(String name) {
            this.name = name;
            this.baseValue = null;

            values.put(name, this);
        }

        public Optional<Enum1> baseValue() {
            return Optional.ofNullable(baseValue);
        }

        public static EnumExt valueOf(Enum1 value) {
            return values.get(value.name());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            EnumExt enumExt = (EnumExt) o;
            return Objects.equals(baseValue, enumExt.baseValue) &&
                Objects.equals(name, enumExt.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(baseValue, name);
        }

        @Override
        public String toString() {
            return "EnumExt{" +
                "baseValue=" + baseValue +
                ", name='" + name + '\'' +
                '}';
        }
    }

    public static class Enum1 {
        public static final Enum1 X = new Enum1("X");
        public static final Enum1 Y = new Enum1("Y");
        public static final Enum1 ANOTHER = new Enum1("ANOTHER");

        private final String name;

        private Enum1(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Enum1 enum1 = (Enum1) o;
            return name.equals(enum1.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Enum1{" +
                "name='" + name + '\'' +
                '}';
        }
    }
}
