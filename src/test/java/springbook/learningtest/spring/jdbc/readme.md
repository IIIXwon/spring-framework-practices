## jdbcClient
> * 구버전에서 사용되던 SimpleJdbcTemplate가 삭제되고 최신버전에서는 JdbcTemplate과 NamedParameterJdbcTemplate를 기반으로 하는 `JdbcClient`를 제공한다
> * 메소드 체이닝 방식으로 보기 편해졌다
> * sql parameter를 입력 할때 `:이름` 과 같은 name으로 지정하면 paramSource, `?` 가변인자로 지정하면 param, params를 사용해야한다
> * jdbcClient는 구버전에서 지원하는 한번에 sql을 실행하는 batch기능이 없으니 batch기능이 필요하면 jdbcTemplate, NamedParameterJdbcTemplate를 사용해야한다
## simpleJdbcInsert
> * 모든 칼럼 정보를 적어야하는 귀찮음이 있는 insert문 작성을 간편하게 도와준다
> * ## simpleJdbcInsertWithGeneratedKey
> * db에 의해 자동으로 생성되는 키 컬럼을 지정할 수 있다
> * ## simpleJdbcCall
> * db에서 지원하는 함수나 프로시저를 선언되어있다면, 호출해서 사용할 수 있다