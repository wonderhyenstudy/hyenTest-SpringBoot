package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.repository.search.BoardSearch;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

// Repository : "이런 데이터를 찾아줘"라고 메서드 이름만 선언하면, JPA가 SQL을 알아서 생성해서 DB에 넣어주고 가져dha
// 자바의 Board 객체를 DB의 BOARD 테이블 데이터로 번역해서 넣어줌

// 제네릭 타입 파라미터를 사용 JpaRepository<T, ID>
// T  (`Board`):** 이 저장소가 관리할 엔티티(Entity) 클래스 타입. (어떤 테이블과 연결할 것인가?)
// ID (`Long`):** 해당 엔티티의 기본 키(Primary Key, `@Id`가 붙은 필드)의 데이터 타입.
// 구현체(실제 동작하는 코드)를 자동으로 만들어서 주입
// 쿼리 메서드: findByTitle(String title)이라고 이름만 지어줘도,
// JPA가 SELECT * FROM board WHERE title = ? 쿼리를 알아서 짜줍니다.
// save(), findById(), findAll(), delete() 같은 건 내가 코드를 짤 필요도 없이 그냥 불러다 쓰면됨
public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    // JpaRepository 인터페이스 : 다양한 기본 기능을 탑재하고 있다. CRUD 이용 가능
    // 정의하지 않아도 상속 받아온 인터페이스 안에 모든 기능들이 이미 있다

    // @EntityGraph 를이용해서, 조인을 이용한, 쿼리 1번 호출, 조인 할 대상, imageSet
    // 연관된 데이터를 한 번의 쿼리로 효율적으로 가져오기 위한 설정
    // 1. @EntityGraph: 연관된 엔티티를 "즉시 로딩(Eager Load)" 하도록 지시합니다.
    // attributePaths: 한꺼번에 조회할 연관관계 필드 이름을 지정합니다.
    // 여기서는 Board 엔티티 안에 있는 'imageSet' 필드를 같이 가져오겠다는 뜻입니다.
    @EntityGraph(attributePaths = {"imageSet"})
    // 2. @Query: 직접 실행할 JPQL 쿼리를 작성합니다.
    // Board(b)를 조회하는데, 조건은 파라미터로 받은 bno와 일치하는 데이터입니다.
    @Query("select b from Board b where b.bno = :bno")
    // 3. 반환 타입 및 메서드명:
    // bno(게시글 번호)를 받아서 Board와 그에 속한 이미지들(imageSet)을 함께 담아 반환합니다.
    // 결과가 없을 수도 있으니 Optional로 감싸서 안전하게 처리합니다.
    Optional<Board> findByIdWithImages(Long bno);

}
