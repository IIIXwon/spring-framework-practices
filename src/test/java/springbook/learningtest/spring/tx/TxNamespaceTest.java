package springbook.learningtest.spring.tx;

import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import springbook.learningtest.spring.jpa.Member;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@SpringJUnitConfig
public class TxNamespaceTest {
    private static final String LONG_STR = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

    @Autowired
    MemberDao dao;

    @Test
    void tx() {
        dao.deleteAll();
        assertEquals(0, dao.count());

        dao.add(Arrays.asList(new Member[]{
                new Member(1, "Spring", 1.2)
                , new Member(2, "Spring", 1.2)
        }));

        assertEquals(2, dao.count());

        try {
            dao.add(Arrays.asList(new Member[]{
                    new Member(3, "Spring", 1.2)
                    , new Member(4, LONG_STR, 1.2)
            }));
            fail();
        } catch (DataAccessException e) {}
        assertEquals(2, dao.count());
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
        @Bean
        public DefaultAdvisorAutoProxyCreator autoProxyCreator() {
            return new DefaultAdvisorAutoProxyCreator();
        }

        @Bean
        public TransactionInterceptor txAdvice() {
            NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
            RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
            readOnlyTx.setReadOnly(true);
            readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);

            RuleBasedTransactionAttribute defaultTx = new RuleBasedTransactionAttribute();
            Map<String, TransactionAttribute> txMethods = new HashMap<>();
            txMethods.put("get*", readOnlyTx);
            txMethods.put("*", defaultTx);

            source.setNameMap(txMethods);
            return new TransactionInterceptor(transactionManager(), source);
        }

        @Bean
        public Advisor advisor() {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* *..MemberDao.*(..))");
            return new DefaultPointcutAdvisor(pointcut, txAdvice());
        }
    }

    interface MemberDao {
        void add(Member member);

        void add(List<Member> members);

        void deleteAll();

        long count();
    }

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
        public long count() {
            return this.queryForObject("select count(*) from member", Long.class).longValue();
        }

    }
}
