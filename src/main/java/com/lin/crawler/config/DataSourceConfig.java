package com.lin.crawler.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 *
 * Created by linjinzhi on 2018-12-14.
 *
 * 数据源配置.
 *
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "caimiDataSource")
    @Qualifier("caimiDataSource")
    @ConfigurationProperties(prefix="spring.datasource.caimi")
    @Primary
    public DataSource caimiDataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean(name = "butterflyDataSource")
    @Qualifier("butterflyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.butterfly")
    public DataSource butterflyDataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean(name = "ladybugDataSource")
    @Qualifier("ladybugDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ladybug")
    public DataSource ladybugDataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean(name = "caimiJdbcTemplate")
    public JdbcTemplate caimiJdbcTemplate(@Qualifier("caimiDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean(name = "butterflyJdbcTemplate")
    public JdbcTemplate butterflyJdbcTemplate(@Qualifier("butterflyDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "ladybugJdbcTemplate")
    public JdbcTemplate ladybugJdbcTemplate(@Qualifier("ladybugDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
