package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.repository.search.BoardSearch;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

// 제네릭 타입 파라미터를 사용 JpaRepository<T, ID>
// T  (`Board`):** 이 저장소가 관리할 엔티티(Entity) 클래스 타입. (어떤 테이블과 연결할 것인가?)
// ID (`Long`):** 해당 엔티티의 기본 키(Primary Key, `@Id`가 붙은 필드)의 데이터 타입.
public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    // JpaRepository 인터페이스 : 다양한 기본 기능을 탑재하고 있다. CRUD 이용 가능
    // 정의하지 않아도 상속 받아온 인터페이스 안에 모든 기능들이 이미 있다

}
