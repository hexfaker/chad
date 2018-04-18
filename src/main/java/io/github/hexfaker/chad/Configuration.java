package io.github.hexfaker.chad;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Tanaev Vladislav (equee)
 */
@Data
@ConfigurationProperties(prefix = "auth")
@Component
public class Configuration {
  String token = 
}