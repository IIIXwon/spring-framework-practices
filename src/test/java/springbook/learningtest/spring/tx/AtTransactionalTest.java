package springbook.learningtest.spring.tx;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import springbook.learningtest.spring.jpa.Member;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringJUnitConfig
public class AtTransactionalTest {
    private static final String LONG_STR = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

    @Autowired
    MemberDao dao;

    @Test
    void tx() {
        dao.deleteAll();
        assertEquals(0L, dao.count());

        dao.add(Arrays.asList(new Member[]{
                new Member(1, "Spring", 1.2)
                , new Member(2, "Spring", 1.2)
        }));

        assertEquals(2L, dao.count());

        try {
            dao.add(Arrays.asList(new Member[]{
                    new Member(3, "Spring", 1.2)
                    , new Member(4, LONG_STR, 1.2)
            }));
            fail();
        } catch (DataAccessException e) {}
        assertEquals(2L, dao.count());
    }
    @Configuration
    @EnableTransactionManagement
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
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public MemberDao memberDao(DataSource dataSource) {
            MemberDaoImpl memberDao = new MemberDaoImpl();
            memberDao.setDataSource(dataSource);
            memberDao.initTemplateConfig(dataSource);
            return memberDao;
        }
    }

    interface MemberDao {
        void add(Member member);

        void add(List<Member> members);

        void deleteAll();

        long count();
    }

    @Transactional
    static class MemberDaoImpl extends JdbcTemplate implements MemberDao {
        SimpleJdbcInsert insert;
        void initTemplateConfig(DataSource dataSource) {
            insert = new SimpleJdbcInsert(dataSource).withTableName("member");
        }
        @Override
        public void add(Member member) {
            insert.execute(new BeanPropertySqlParameterSource(member));
        }

        @Override
        public void add(List<Member> members) {
            for (Member member : members) add(member);
        }

        @Override
        public void deleteAll() {
            this.update("delete from member");
        }

        @Override
        @Transactional(readOnly = true)
        public long count() {
            return this.queryForObject("select count(*) from member", Long.class).longValue();
        }

    }
}
