package carPartsStore;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom")
public class ApplicationTraits {
    static private final Logger LOGGER = LoggerFactory.getLogger(ApplicationTraits.class);

    static private String trimToNull(String str) {
        return str == null ? null : str.trim().isEmpty() ? null : str.trim();
    }

    static private byte[] getKeyFileContents(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return Base64.getDecoder().decode(content);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to get ke file contents: " + filePath, e);
        }
    }

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
        private String rsaPublicKey, rsaPrivateKey;

        public KeyPair getRSAKeyPair() {
            rsaPublicKey = trimToNull(rsaPublicKey);
            rsaPrivateKey = trimToNull(rsaPrivateKey);

            if (rsaPublicKey == null && rsaPrivateKey == null) {
                return generateRSAKeyPair();
            } else if (rsaPublicKey != null && rsaPrivateKey != null) {
                return getRSAKeyPairFromFiles();
            } else {
                throw new IllegalStateException("JWT RSA keys are inconsistently set");
            }
        }

        private KeyPair generateRSAKeyPair() {
            LOGGER.info("JWT RSA keys not set; Will generate new RSA key pair");

            try {
                var generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                return generator.generateKeyPair();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to generate RSA key pair", e);
            }
        }

        private KeyPair getRSAKeyPairFromFiles() {
            LOGGER.info("Generating RSA keypair using provided key files: {}, {}", rsaPublicKey, rsaPrivateKey);

            try {
                KeyFactory f = KeyFactory.getInstance("RSA");

                var src = getKeyFileContents(rsaPrivateKey);
                var privateKey = f.generatePrivate(new PKCS8EncodedKeySpec(src));

                src = getKeyFileContents(rsaPublicKey);
                var publicKey = f.generatePublic(new X509EncodedKeySpec(src));

                return new KeyPair(publicKey, privateKey);

            } catch (Exception e) {
                throw new IllegalStateException("Failed to generate pair from files", e);
            }
        }
    }
}