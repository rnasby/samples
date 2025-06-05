package carPartsStore;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom")
public class ApplicationTraits {
    private Security security;

    @Getter
    @Setter
    static public class Security {
        private JWT jwt;
    }

    @Getter
    @Setter
    static public class JWT {
        private String secretKey;
        private int tokenTimeoutMin;
        private int refreshTokenTimeoutMin;
    }
}