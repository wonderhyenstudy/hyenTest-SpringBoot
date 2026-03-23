package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 아무 기능이 없지만, 우리는 기본 기능을 이용해서. CURD를 테스트 할수 있다.

    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);

    // 부모 게시글 번호를 이용해서, 해당 댓글을 삭제하기.
    // 예시) 게시글 1번 , 댓글 3개 있으면,
    // 게시글 1번을 삭제시, where 조건부 해당 댓글 3개 삭제.
    void deleteByBoard_Bno(Long bno);


    // Board 에서 조회하는 기능 사용하기 위해 추가
    // 게시글의 bno로 댓글을 조회하는 기능
    List<Reply> findByBoard_Bno(Long bno);

}
