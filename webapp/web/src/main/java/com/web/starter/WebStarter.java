package com.web.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication

@EntityScan(basePackages = {"com.web"})  // Entity anatasyonların beaninin oluşmasını sağlar
@ComponentScan(basePackages= {"com.web"}) // bu restcontroller,service, repository gibi anatasyonların containerda beaninin oluşmasını sağlıyor
@EnableJpaRepositories(basePackages= {"com.web"})  // jpa repository anatasyonunun beani oluşması için

//@PropertySource(value = "classpath:app.properties") // properties dosyasını app.poroperties olarak okuması için
//@EnableConfigurationProperties(value = GlobalProperties.class)  //Bu @ConfigurationProperties anatasyonunu aktif hale getirir
public class WebStarter {

	public static void main(String[] args) {
		SpringApplication.run(WebStarter.class, args);
	}

}
