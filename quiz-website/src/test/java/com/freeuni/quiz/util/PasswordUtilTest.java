package com.freeuni.quiz.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordUtilTest {

    @Test
    public void testEncodeDecodeSalt() {
        byte[] salt = PasswordUtil.generateSalt();
        String encoded = PasswordUtil.encodeSalt(salt);
        byte[] decoded = PasswordUtil.decodeSalt(encoded);

        assertArrayEquals(salt, decoded);
    }

    @Test
    public void testHashPassword_consistency() throws Exception {
        String password = "strongPassword123";
        byte[] salt = PasswordUtil.generateSalt();

        String hash1 = PasswordUtil.hashPassword(password, salt);
        String hash2 = PasswordUtil.hashPassword(password, salt);

        assertNotNull(hash1);
        assertEquals(hash1, hash2);
    }

    @Test
    public void testHashPassword_differentSaltsProduceDifferentHashes() throws Exception {
        String password = "strongPassword123";

        byte[] salt1 = PasswordUtil.generateSalt();
        byte[] salt2 = PasswordUtil.generateSalt();

        String hash1 = PasswordUtil.hashPassword(password, salt1);
        String hash2 = PasswordUtil.hashPassword(password, salt2);

        assertNotNull(hash1);
        assertNotNull(hash2);

        assertNotEquals(hash1, hash2);
    }
}
