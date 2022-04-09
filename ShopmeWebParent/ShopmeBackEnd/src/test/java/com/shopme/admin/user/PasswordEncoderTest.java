package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testEncoderPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "kyaw2022";
		String encodedPassword = encoder.encode(rawPassword);
		System.out.println(encodedPassword);
		
		boolean matched = encoder.matches(rawPassword, encodedPassword);
		assertThat(matched).isTrue();
		
	}
}
