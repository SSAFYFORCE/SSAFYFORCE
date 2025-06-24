package force.ssafy.global.config;

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MetaDBConfig {
    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.meta")
    public DataSource metaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager metaTransactionManager() {
        return new DataSourceTransactionManager(metaDataSource());
    }

}
