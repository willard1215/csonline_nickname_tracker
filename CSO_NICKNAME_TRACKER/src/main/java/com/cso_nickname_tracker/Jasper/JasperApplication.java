package com.cso_nickname_tracker.Jasper;

import com.cso_nickname_tracker.Jasper.config.DiscordBotConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;

@SpringBootApplication
public class JasperApplication {

	public static void main(String[] args) throws LoginException {
		SpringApplication.run(JasperApplication.class, args);
	}

}
