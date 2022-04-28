package org.sebi.springfedora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {  HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class, JdbcTemplateAutoConfiguration.class })
public class SpringFedoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringFedoraApplication.class, args);
	}

}
