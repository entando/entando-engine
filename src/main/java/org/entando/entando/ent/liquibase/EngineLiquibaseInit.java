package org.entando.entando.ent.liquibase;

import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn
public class EngineLiquibaseInit {

    @Value("${db.environment:}")
    private String context;

    @Autowired
    @Qualifier("portDataSource")
    private DataSource portDataSource;

    @Autowired
    @Qualifier("servDataSource")
    private DataSource servDataSource;

    @Bean
    public SpringLiquibase liquibaseEnginePort() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/changeSetPort.xml");
        liquibase.setContexts(this.context);
        liquibase.setDataSource(this.portDataSource);
        return liquibase;
    }

    @Bean
    public SpringLiquibase liquibaseEngineServ() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/changeSetServ.xml");
        liquibase.setDataSource(this.servDataSource);
        liquibase.setContexts(this.context);
        return liquibase;
    }

}
