package br.ueg.desenvolvimento.web.projeto_edivan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.ueg.desenvolvimento.web.projeto_edivan")
@EntityScan(basePackages = "br.ueg.desenvolvimento.web.projeto_edivan.models")
public class ProjetoEdivanApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoEdivanApplication.class, args);
	}

}
