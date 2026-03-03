package com.busanit501.springboot0226.repository.search;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

// 파일설명 : BoardSearch 에서 약속한 기능을 실제로 Querydsl 코드로 작성하는 곳. QBoard 사용. 구현체
// 주의 사항 : Q-Class 생성 여부 : 프로젝트를 빌드(Build -> Build Project 혹은 gradle compileJava)했을 때,
// build/generated 폴더 안에 QBoard.java가 생성되었는지 꼭 확인해 보세요.
// 이게 있어야 BoardSearchImpl에서 에러 없이 코딩할 수 있습니다.
// Impl 이름 규칙: BoardSearch 인터페이스의 구현체 이름은 반드시 뒤에
// Impl이 붙어야 스프링이 자동으로 인식해서 BoardRepository 에 연결해 줍니다.

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    // 생성자
    public BoardSearchImpl() {
        super(Board.class);
    }

    // 메서드 구현
    // search like
    @Override
    public Page<Board> search1(Pageable pageable) {
        // 자바 문법으로, 검색 및 필터에 필요한 문장을 작성.(SQL 대신에, 자바 코드로 작성.), 빌더 패던을 이용하세요.
        // 동적인 쿼리 작성하기 위해 Qboard 사용
        QBoard board = QBoard.board; // Q 도메인 객체를 이용
        JPQLQuery<Board> query = from(board); // select .. from board

        query.where(board.title.contains("t")); // where title like ..

        // 추가1. 제목, 작성자 검색 조건 추가
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.or(board.title.contains("t"));
        booleanBuilder.or(board.content.contains("t"));

        // query , 조건을 적용하기.
        query.where(booleanBuilder);
        // 유효성 체크, bno 0보다 초과하는 조건,
        query.where(board.bno.gt(0L));

        // 추가1. 페이징 처리
        this.getQuerydsl().applyPagination(pageable, query);

        // fetch : 가져오기
        // DB 서버로 호출해서, 데이터를 받아오기
        List<Board> list =  query.fetch();
        // 조건에 맞는 전체 데이터 개수
        long count = query.fetchCount(); // 조건에 맞는 전체 데이터 개수

        return null;
    }

    // String[] types, "t", "c", "tc"
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        // 자바 문법으로, 검색 및 필터에 필요한 문장을 작성.(SQL 대신에, 자바 코드로 작성.), 빌더 패던을 이용하세요.
        // 동적인 쿼리 작성하기 위해 Qboard 사용
        QBoard board = QBoard.board; // Q 도메인 객체를 이용
        JPQLQuery<Board> query = from(board); // select .. from board

        // 검색 조건 사용해보기.
        if(types != null && types.length > 0 && keyword != null){ // 유효성 체크
            // 여러 조건을 BooleanBuilder 를 이용해서, 조건의 묶음 만들기
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for(String type : types){
                switch (type){
                    case "t" :
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c" :
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w" :
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                } // end switch
            } // end for

            // 조건부 설정을 적용하기
            query.where(booleanBuilder);

        } // end if

        // 간단한 유효성 체크, bno > 0
        query.where(board.bno.gt(0L));

        // 페이징 조건, 적용하기
        this.getQuerydsl().applyPagination(pageable,query);

        // 위의 준비물을 이용해서, 검색, 필터, 페이징의 결과를 반환해보기
        List<Board> list = query.fetch(); // 페이징 처리가 된 10개의 데이터 목록
        // 전체 갯수,
        long total = query.fetchCount();
        // Page 타입으로 전달하기
        Page<Board> result = new PageImpl<Board>(list, pageable, total);

        return result;
    }
}
