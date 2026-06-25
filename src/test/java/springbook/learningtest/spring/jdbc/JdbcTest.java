package springbook.learningtest.spring.jdbc;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig
public class JdbcTest {
    @Autowired
    DataSource dataSource;

    SimpleJdbcInsert sji;
    JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        sji = new SimpleJdbcInsert(dataSource).withTableName("member");
        jdbcClient = JdbcClient.create(dataSource);
    }

    @Test
    void jdbcClient() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(SimpleDao.class, Config.class);
        SimpleDao dao = ac.getBean(SimpleDao.class);
        dao.deleteAll();

        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("name", "Spring");
        m.put("point", 3.5);
        dao.insert(m);
        dao.insert(new MapSqlParameterSource().addValue("id", 2).addValue("name", "Book").addValue("point", 10.1));
        dao.insert(new Member(3, "Jdbc", 20.5));

        assertEquals(3, dao.rowCount());
        assertEquals(2, dao.rowCount(5));
        assertEquals(3, dao.rowCount(1));

        assertEquals("Spring", dao.name(1));
        assertEquals(3.5, dao.point(1));

        Member member = dao.get(1);
        assertEquals(1, member.id());
        assertEquals("Spring", member.name());
        assertEquals(3.5, member.point());

        assertEquals(3, dao.find(1).size());
        assertEquals(2, dao.find(5).size());
        assertEquals(0, dao.find(100).size());

        Map<String, Object> mmap = dao.getMap(1);
        assertEquals(1, mmap.get("id"));
        assertEquals("Spring", mmap.get("name"));
        assertEquals(3.5, mmap.get("point"));

        HashMap<String, Object>[] paramMaps = new HashMap[2];
        paramMaps[0] = new HashMap<String, Object>();
        paramMaps[0].put("id", 1);
        paramMaps[0].put("name", "Spring2");
        paramMaps[1] = new HashMap<String, Object>();
        paramMaps[1].put("id", 2);
        paramMaps[1].put("name", "Book2");
        dao.update(paramMaps);

        assertEquals("Spring2", dao.name(1));
        assertEquals("Book2", dao.name(2));

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        template.batchUpdate("update member set name = :name where id = :id", new SqlParameterSource[]{
                new MapSqlParameterSource().addValue("id", 1).addValue("name", "Spring3")
                , new BeanPropertySqlParameterSource(new Member(2, "Book3", 0))
        });

        assertEquals("Spring3", dao.name(1));
        assertEquals("Book3", dao.name(2));
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
    }

    static class SimpleDao {
        JdbcClient jdbcClient;

        @Autowired
        void init(DataSource dataSource) {
            jdbcClient = JdbcClient.create(dataSource);
        }

        void update(Map<String, Object>[] maps) {
            for (Map<String, Object> map : maps)
                jdbcClient.sql("update member set name = :name where id = :id")
                        .params(map)
                        .update();
        }

        public void deleteAll() {
            jdbcClient.sql("delete from member").update();
        }

        public void insert(MapSqlParameterSource param) {
            jdbcClient.sql("insert into member(id, name, point) values(:id, :name, :point)")
                    .paramSource(param)
                    .update();
        }

        public void insert(Member member) {
            jdbcClient.sql("insert into member(id, name, point) values(:id, :name, :point)")
//                    .paramSource(member)
                    .paramSource(new BeanPropertySqlParameterSource(member))
                    .update();
        }

        public void insert(Map<String, Object> param) {
            jdbcClient.sql("insert into member(id, name, point) values(:id, :name, :point)")
                    .params(param)
                    .update();
        }

        public int rowCount() {
            return jdbcClient.sql("select count(*) from member")
                    .query(Integer.class)
                    .single();
        }

        public int rowCount(double min) {
            return jdbcClient.sql("select count(*) from member where point > ?")
                    .param(min)
                    .query(Integer.class)
                    .single();
        }


        public String name(int id) {
            return jdbcClient.sql("select name from member where id = ?")
                    .param(id)
                    .query(String.class)
                    .single();
        }

        public double point(int id) {
            return jdbcClient.sql("select point from member where id = ?")
                    .param(id)
                    .query(Double.class)
                    .single();
        }

        public Member get(int id) {
            return jdbcClient.sql("select * from member where id = ?")
                    .param(id)
                    .query(Member.class)
                    .single();
        }

        public List<Member> find(double min) {
            return jdbcClient.sql("select * from member where point > ?")
                    .param(min)
                    .query(Member.class)
                    .list();
        }

        public Map<String, Object> getMap(int id) {
            return jdbcClient.sql("select * from member where id = ?")
                    .param(id)
//                    .query(new ColumnMapRowMapper())
//                    .single();
                    .query()
                    .singleRow();
        }
    }

    @Test
    void simpleJdbcInsert() {
        jdbcClient.sql("delete from member").update();

        SimpleJdbcInsert memberInsert = new SimpleJdbcInsert(dataSource).withTableName("member");
        Member member = new Member(1, "Spring", 3.5);
        memberInsert.execute(new BeanPropertySqlParameterSource(member));
    }

    @Test
    void simpleJdbcInsertWithGeneratedKey() {
        jdbcClient.sql("delete from register");

        SimpleJdbcInsert registerInsert = new SimpleJdbcInsert(dataSource).withTableName("register").usingGeneratedKeyColumns("id");
        int key = registerInsert.executeAndReturnKey(new MapSqlParameterSource("name", "Spring")).intValue();
        System.out.println(key);
        // id 칼럼에 auto generated 옵션이 있어야 한다
    }

    @Test
    void simpleJdbcInsertWithSqlParamSource() {
        jdbcClient.sql("delete from member").update();

        MapSqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("id", 1)
                .addValue("name", "Spring")
                .addValue("point", 10.5);
        sji.execute(paramSource);

        Member m = new Member(2, "jdbc", 3.3);
        sji.execute(new BeanPropertySqlParameterSource(m));

        JdbcClient client = JdbcClient.create(dataSource);
        List<Map<String, @Nullable Object>> list = client.sql("select * from member order by id")
                .query()
                .listOfRows();

        assertEquals(2, list.size());

        assertEquals(1, list.get(0).get("id"));
        assertEquals("Spring", list.get(0).get("name"));
        assertEquals(10.5, list.get(0).get("point"));

        assertEquals(2, list.get(1).get("id"));
        assertEquals("jdbc", list.get(1).get("name"));
        assertEquals(3.3, list.get(1).get("point"));
    }

    @Test
    void simpleJdbcCall() {
        jdbcClient.sql("delete from member").update();
        jdbcClient.sql("insert into member(id, name, point) values(1, 'Spring', 0)").update();

        SimpleJdbcCall sjc = new SimpleJdbcCall(dataSource).withFunctionName("find_name");
        String ret = sjc.executeFunction(String.class, 1);
        assertEquals("Spring", ret);
    }
}
