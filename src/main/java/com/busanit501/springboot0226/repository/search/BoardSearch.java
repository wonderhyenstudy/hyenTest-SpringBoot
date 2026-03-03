package com.busanit501.springboot0226.repository.search;

import com.busanit501.springboot0226.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// 파일설명 : 나는 이런 검색 기능을 만들 거야"라고 이름만 선언하는 설계도. 인터페이스

public interface BoardSearch {
    // 검색의 결과도, 페이징 처리를 할 예정.
    Page<Board> search1(Pageable pageable);

    // 검색어(제목, 내용), 페이징 처리 적용하는 메소드
    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

}
