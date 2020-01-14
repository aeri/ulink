package urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.impl.ClickRepositoryImpl;
import urlshortener.repository.impl.ShortURLRepositoryImpl;

@Configuration
public class PersistenceConfiguration {

    private final JdbcTemplate jdbc;

    public PersistenceConfiguration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    
    /** 
     * @return ShortURLRepository
     */
    @Bean
    ShortURLRepository shortURLRepository() {
        return new ShortURLRepositoryImpl(jdbc);
    }

    
    /** 
     * @return ClickRepository
     */
    @Bean
    ClickRepository clickRepository() {
        return new ClickRepositoryImpl(jdbc);
    }

}
