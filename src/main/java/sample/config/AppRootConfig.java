package sample.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource("classpath:/crowd.properties")
@ComponentScan(basePackages = "sample")
@EnableWebMvc
public class AppRootConfig {
}
