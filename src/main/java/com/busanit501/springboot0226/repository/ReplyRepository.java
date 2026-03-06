package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 아무 기능이 없지만, 우리는 기본 기능을 이용해서. CURD를 테스트 할수 있다.

    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);

}
