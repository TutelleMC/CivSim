package io.github.metriximor.civsimbukkit.services;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class UUIDService {
    public static UUID generateUUID(Object obj) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        String str = obj.toString();
        byte[] bytes = md.digest(str.getBytes());

        // Use only the most significant bits for creating the UUID
        byte[] msb = Arrays.copyOfRange(bytes, 0, 8);
        ByteBuffer buffer = ByteBuffer.wrap(msb);
        long mostSigBits = buffer.getLong();

        return new UUID(mostSigBits, 0);
    }

}
