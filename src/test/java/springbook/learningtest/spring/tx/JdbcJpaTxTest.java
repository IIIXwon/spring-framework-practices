package springbook.learningtest.spring.tx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import springbook.learningtest.spring.jpa.Member;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringJUnitConfig
public class JdbcJpaTxTest {
    private static final String LONG_STR = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

    @Autowired
    MemberJdbcDao jdbcDao;
    @Autowired
    MemberJpaDao jpaDao;
    @Autowired
    PlatformTransactionManager platformTransactionManager;
    @Autowired
    EntityManagerFactory emf;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void jdbcDaoWithoutTx() {
        jdbcDao.deleteAll();
        assertEquals(0L, jdbcDao.count());

        try {
            rollbackJob();
            fail("DataAccessException expected");
        } catch (DataAccessException e) {
        }
        assertEquals(1L, jdbcDao.count());
    }

    @Test
    void jdbcDaoWithTx() {
        jdbcDao.deleteAll();
        assertEquals(0L, jdbcDao.count());

        try {
            new TransactionTemplate(transactionManager).executeWithoutResult((status) -> rollbackJob());
            fail("DataAccessException expected");
        } catch (DataAccessException e) {
        }
        assertEquals(0L, jdbcDao.count());
    }

    @Test
    void jdbcAndJpaTx() {
        jdbcDao.deleteAll();
        assertEquals(0L, jdbcDao.count());
        try {
            new TransactionTemplate(transactionManager).executeWithoutResult((status) -> {
                jdbcDao.add(new Member(1, "Spring", 1.2));
                jpaDao.add(new Member(2, "Jpa", 1.2));
                assertEquals(2L, jdbcDao.count());
                jpaDao.add(new Member(3, LONG_STR, 1.2));
            });
            fail("DataAccessException expected");
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(0L, jdbcDao.count());
    }

    void rollbackJob() {
        jdbcDao.add(new Member(1, "Spring", 1.2));
        jdbcDao.add(new Member(2, LONG_STR, 1.2));
    }

    @Configuration
    static class Config {
        @Bean
        public DataSource dataSource() {
            SimpleDriverDataSource ds = new SimpleDriverDataSource();
            ds.setDriverClass(org.postgresql.Driver.class);
            ds.setUrl("jdbc:postgresql://3.36.244.202:5432/springbook_db");
            ds.setUsername("myapp_user");
            ds.setPassword("1234");
            return ds;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
            emf.setDataSource(dataSource());
            emf.setPackagesToScan("springbook.learningtest.spring.jpa");

            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            vendorAdapter.setGenerateDdl(true);
            vendorAdapter.setShowSql(true);
            emf.setJpaVendorAdapter(vendorAdapter);

            return emf;
        }

        @Bean
        public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
            return new PersistenceExceptionTranslationPostProcessor();
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager ma = new JpaTransactionManager();
            ma.setEntityManagerFactory(entityManagerFactory().getObject());
            return ma;
        }

        @Bean
        public MemberJdbcDao jdbcDao() {
            MemberJdbcDao dao = new MemberJdbcDao();
            dao.setDataSource(dataSource());
            dao.initTemplateConfig(dataSource());
            return dao;
        }

        @Bean
        public MemberJpaDao jpaDao() {
            return new MemberJpaDao();
        }
    }

    static class MemberJdbcDao extends JdbcTemplate {
        SimpleJdbcInsert insert;

        void initTemplateConfig(DataSource dataSource) {
            insert = new SimpleJdbcInsert(dataSource).withTableName("member");
        }

        public void add(Member member) {
            insert.execute(new BeanPropertySqlParameterSource(member));
        }


        public void deleteAll() {
            this.update("delete from member");
        }

        @Transactional(readOnly = true)
        public long count() {
            return this.queryForObject("select count(*) from member", Long.class).longValue();
        }
    }

    @Repository
    static class MemberJpaDao {
        @PersistenceContext
        EntityManager entityManager;

        void add(Member m) {
            entityManager.persist(m);
            entityManager.flush();
        }
    }
}
