package grace.guides.plugins;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LanguageConfiguration {

    @Bean
    public LanguageManager languageManager() {
        return new DefaultLanguageManager();
    }

}
