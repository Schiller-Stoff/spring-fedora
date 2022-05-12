package org.sebi.springfedora.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Class configures security for local develompent
 * - disables all security for dev profile
 */
@Profile( value = {"dev"})
@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
  

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/**");
  }

}
