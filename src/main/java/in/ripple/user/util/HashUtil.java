package in.ripple.user.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class HashUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HashUtil.class);


    public static String encodeBase64(byte[] message) {
        byte[] msgInBase64Bytes = Base64.encodeBase64(message);
        return new String(msgInBase64Bytes);
    }

    public static byte[] decodeBase64(String base64String){
        return Base64.decodeBase64(base64String);
    }

    public static byte[] generateSha256Hash(String message) {
        String algorithm = "SHA-256";

        byte[] hash = null;

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            hash = digest.digest(message.getBytes());
        } catch (Exception e) {
            LOG.error("Error while generating SHA-256 hash", e);
        }
        return hash;
    }
}
