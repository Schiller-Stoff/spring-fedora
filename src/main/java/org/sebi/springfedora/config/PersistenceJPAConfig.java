package org.sebi.springfedora.config;

import org.sebi.springfedora.repository.utils.FedroaPlatformTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig{

  //  @Bean
  //  public LocalContainerEntityManagerFactoryBean
  //    //entityManagerFactoryBean(){
  //     //...
  //  }

   @Bean
   public PlatformTransactionManager transactionManager(){

    log.error("!!APPLYING transaction manager");

      // JpaTransactionManager transactionManager = new JpaTransactionManager();
      // transactionManager.setEntityManagerFactory(
      //   entityManagerFactoryBean().getObject() );

      PlatformTransactionManager ptm = new FedroaPlatformTransactionManager();
      return ptm;
   }
}