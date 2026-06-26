package springbook.learningtest.spring.jpa;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig
public class JpaTest {
    @Autowired
    MemberDao dao;
    @Autowired
    MemberRepositoryDao repositoryDao;
    @Autowired
    EntityManager em;


    @Test
    @Transactional
    void sharedEntityManager() {
        this.em.createQuery("delete from Member").executeUpdate();

        Member m = new Member(10, "Spring", 7.8);
        this.em.persist(m);
        Long count = this.em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
        assertEquals(1L, count);
    }

    @Test
    void entityManagerFactory() {
        EntityManager em = dao.emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Member ").executeUpdate();
        Member m = new Member(10, "Spring", 7.8);
        em.persist(m);
        Long count = em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
        assertEquals(1L, count);
        em.getTransaction().commit();
    }

    @Test
    @Transactional
    void JpaApiException() {
        assertThrows(PersistenceException.class, () -> dao.addDuplicatedId());
    }

    @Test
    @Transactional
    void JpaRepositoryException() {
        assertThrows(DataAccessException.class, () -> repositoryDao.addDuplicatedId());
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
        public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
            return new JpaTransactionManager(emf);
        }

        @Bean
        public MemberDao memberDao() {
            return new MemberDao();
        }

        @Bean
        public MemberRepositoryDao memberRepositoryDao() {
            return new MemberRepositoryDao();
        }
    }

    public static class MemberDao {
        @PersistenceContext(type = PersistenceContextType.EXTENDED)
        EntityManager em;
        @PersistenceUnit
        EntityManagerFactory emf;

        public void addDuplicatedId() {
            em.persist(new Member(10, "Spring", 7.8));
            em.persist(new Member(10, "Spring", 7.8));
            em.flush();
        }
    }


    @Repository
    static class MemberRepositoryDao {
        @PersistenceContext
        EntityManager em;

        public void addDuplicatedId() {
            em.persist(new Member(10, "Spring", 7.8));
            em.persist(new Member(10, "Spring", 7.8));
            em.flush();
        }
    }
}
