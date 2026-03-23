package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.BoardImage;
import com.busanit501.springboot0226.domain.Reply;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {
    // 자동 조립. 창고에 있는 객체 중 타입이 맞는 걸 자동으로 꽂아줌.
    // 자동으로 해당 객체를 연결하겠다.
    // DB에 접근하고 싶을 때. DB 작업 할때
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private PageableArgumentResolver pageableArgumentResolver;
    // 게시글 삭제시, 댓글 삭제 기능도 필요해서, 도움을 요청.
    @Autowired
    private ReplyRepository replyRepository;


    // 연습 : 더미 데이터 만들기
    @Test
    public void testInsert(){
        // 더미 데이터 100개를 임의로 작성, 하드코딩.  반복문을 이요해서, 추가 할 예정
        IntStream.rangeClosed(1,100).forEach(i->{
            // 더미 엔티티 객체 생성, 빌드 패턴이용.
            Board board = Board.builder()
                    .title("더미 제목...title" + i)
                    .content("더미 내용...content" + i)
                    .writer("더미 사용자...user" + i % 10)
                    .build();
            // boardRepository 의 기능들 중에서, save 라는 메서드를 이용해서, 실제 디비에 저장
            Board result = boardRepository.save(board);
            log.info("결과 확인 : " + result.getBno());
        });
    }


    //조회 테스트
    @Test
    public void testSelect() {
        Long bno = 100L;
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();
        log.info("board 확인 : " +board );
    }

    //수정 테스트
    @Test
    public void testUpdate() {
        Long bno = 3L;
        // Optional<Board> : "데이터가 담겨있을 수도 있고 비어있을 수도 있는 특별한 상자" 에 담아서 반환해줘
        // findById(bno): JPA 레포지토리가 제공하는 메서드로, PK(bno)를 이용해 데이터를 찾습니다.
        // 데이터를 Optional이라는 안전 상자에 담아준다
        Optional<Board> result = boardRepository.findById(bno);
        // 내용물이 있으면 board 변수에 담고, 데이터가 없다면 예외처리 해줘
        // 에러 메시지 전달
        // 상자를 열어서 데이터가 있으면 꺼내고, 없으면 에러를 발생
        Board board = result.orElseThrow();
        board.change("수정 테스트 제목", "수정 테스트 내용");
        boardRepository.save(board);
        log.info("board 수정확인 : " +board );
    }

    //삭제 테스트
    @Test
    public void testDelete() {
        Long bno = 100L;
        boardRepository.deleteById(bno);
    }

    //페이징 테스트
    @Test
    public void testPaging() {
        // 1 페이지, 정렬 내림차순,
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<Board> result = boardRepository.findAll(pageable);
        // result 결과에는 다양한 페이징 준비물이 들어있다.
        log.info("[Paging] 전체 갯수 result.getTotalElements() : " + result.getTotalElements());
        log.info("[Paging] 전체 페이지 result.getTotalPages() : " + result.getTotalPages());
        log.info("[Paging] 조회 페이지 번호 result.getNumber() : " + result.getNumber());
        log.info("[Paging] 조회 페이지 크기 result.getSize() : " + result.getSize());

        // 페이징 처리가 된 10개의 데이터 목록도 있음.
        List<Board> todoList = result.getContent();
        log.info("[Paging] 페이징 처리가 된 10개 데이터 확인 result.getContent() : " + todoList);
    }

    @Test
    public void testSearch() {
        // page 2번에서(page =1), 크기 : 10, 내림차순
        Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());
        boardRepository.search1(pageable);
    }

    // 실제 페이징 처리된 검색
    @Test
    public void testSearch2() {
        // 검색, 페이징 처리 ,
        // 준비물 1) 검색 타입 2) 검색어 3) 화면에서 전달받은 페이징 처리 준비물(보기 위한 페이지 번호, 크기 10개)
        String[] types = {"t", "c", "w"}; // 명시적으로 값을 만들어서
        String keyword = "오늘";
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());
        // 메서드에, 준비한 준비물을 대입을 해서, 호출해보기.
        // 메서드를 호출할 때 매개변수(배달 물건)로 던져줍니다.
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable );

        // result 결과에는 다양한 페이징 준비물이 들어있다.
        log.info("전체 갯수 result.getTotalElements() : " + result.getTotalElements());
        log.info("전체 페이지 result.getTotalPages() : " + result.getTotalPages());
        log.info("조회 페이지 번호  result.getNumber() :  " + result.getNumber());
        log.info("조회 페이지 크기 result.getSize() :  " + result.getSize());

        log.info("이전 페이지 여부 result.hasPrevious() :  " + result.hasPrevious());
        log.info("다음 페이지 여부 result.hasNext() :  " + result.hasNext());

        // 페이징 처리가 된 10개의 데이터 목록도 있음.
        List<Board> todoList = result.getContent();
        log.info("페이징 처리가 된 10개 데이터 확인 result.getContent() : " + todoList);

    }


    // 영속성 테스트 작업1 , 아직 고아 객체 제거 설정이 없어서,
    // 삭제 확인은 안되지만, 참고 테스트 파일 생성.
    @Test
    public void testInsertWithImage() {

        Board board = Board.builder()
                .title("샘플 게시글....")
                .content("샘플 내용....")
                .writer("샘플 작성자...")
                .build();

        // 첨부 이미지 3개를 작성해서, Board 객체안에, 담기
        for(int i = 0; i < 3 ; i++) {
            board.addImage(UUID.randomUUID().toString(), "file" + i + ".png");

        }
        boardRepository.save(board);

    }

    // @EntityGraph 이용한 호출 , 즉 N+1 문제 해결책.
    // 조인해서, 두 테이블을 붙여서, 한번만 호출할 예정.
    // 이 과정을 보여주기.
    @Test
    // 메서드나 클래스 단위의 작업들을 하나의 논리적 트랜잭션으로 묶어,
    // 모두 성공(Commit)하거나 모두 실패(Rollback)하게 만드는 선언적 트랜잭션 관리 기능
    // 데이터 작업의 '전부 아니면 전무(All or Nothing)'를 보장하는 어노테이션
    // 데이터 변경 작업(DML) 수행 후 성공 시 커밋, 예외 발생 시 자동으로 롤백
    // 데이터 불일치 방지
    @Transactional
    public void testReadWithImage() {
        // 샘플테이블에서, 게시글 번호를 조회.
        // 각자 데이터 베이스 이용해야함.
        // 왜 Optional 로 받지?
//        Optional<Board> result = boardRepository.findById(119L);
        // BoardRepository 만들어둔 메서드 사용
        Optional<Board> result = boardRepository.findByIdWithImages(119L);
        Board board = result.orElseThrow();
        log.info("board 조회 해보기 : " + board);


        log.info("====================================== ");
        log.info("board에 첨부된 이미지들을 조회 해보기 : " + board.getImageSet());
        // 에러가 발생함. no session

        // 첨부 이미지를 확인
        // board.getImageSet() 반복으로 하나씩 꺼내서 boardImage 에 담는다
        for( BoardImage boardImage: board.getImageSet()) {
            log.info("게시글에 첨부된 이미지 조회 : " + boardImage);
        }
    }

    // 수정해보기.
    @Test
    // 해당 메서드안에 여러개의 작업을 하나의 단위로 만들어서, 모두 수행이 되면 진행시키고, 하나라도 진행이 안되면, 진행안해줘
    // 예시) 돈 이체(메서드기능), 행위1:(송금자 계자 -), 행위2:(받는자.계좌 +)
    // 예시) (행위1, 행위2) : 하나의 단위로 묶기, 트랜잭션 무조건 행위1, 행위2가 다같이 실행이 되어야함.
    // 만약, 2개중에 하나라도 안되면, 무조건 롤백.
    // 한 메서드 안의 여러 행위들을 하나로 묶어줘서, 하나가 에러 나면 모두다 실행 안되게 끔 한다.
    @Transactional
    @Commit // 저장히기
    public void testModifyImage() {
        // 기존 게시글에는, 첨부 이미지 샘플 3개있음.
        Optional<Board> result = boardRepository.findByIdWithImages(112L);
        Board board = result.orElseThrow();
        log.info("board 조회 해보기 : " + board);

        // 기존의 첨부파일들은 삭제
        board.clearImages();

        // 새로운 첨부 파일들 추가
        for(int i = 0; i < 3 ; i++) {
            board.addImage(UUID.randomUUID().toString(), "수정4_file_" + i + ".png");
            log.info("게시글에 첨부된 이미지 조회 : " + board.getImageSet());
        }


        // 테스트 실행을 하면, 고아 객체 형태로 남아 있는 거 먼저 확인. 후, 고아 객체 제거하는 설정하기.
        boardRepository.save(board);

    }


    // 이미지 삭제
    @Transactional
    @Commit
    @Test
    //import org.springframework.transaction.annotation.Transactional;
    public void testRemoveAll() {
        Long bno = 112L;
        // 샘플 게시글 번호 : 1L
        replyRepository.deleteByBoard_Bno(bno);
        // 그리고 나서, 게시글 지우기.
        boardRepository.deleteById(bno);
    }

    // 약 100 개정도의 게시글과, 댓글, 첨부 이미지 까지만, 더미데이터 추가 해보기.
    @Transactional
    @Commit
    @Test
    public void testInsertAll() {
        for (int i = 1; i <= 100; i++) {
            Board board = Board.builder()
                    .title("샘플 데이터 " + i)
                    .content("샘플 제목 " + i)
                    .writer("이상용" + i)
                    .build();

            for (int j = 0; j < 3; j++) {
                if (i % 5 == 0) {
                    // 5번째 씩 , 첨부 이미지 추가 안하기.
                    continue;
                }
                // 첨부 이미지 3장씩 더미데이터
                String uuid = UUID.randomUUID().toString();
                String fileName = "샘플 이미지";
                board.addImage(uuid, fileName + j + ".png");


            }
            // 게시글 작성 후 ,
            boardRepository.save(board);
            // 댓글 달기.
            for (int j = 0; j < 3; j++) {
                Reply reply = Reply.builder()
                        .board(board)
                        .replyText("샘플 댓글" + j)
                        .replyer("이상용")
                        .build();
                replyRepository.save(reply);
            }
        }
    }

    // N + 1 , test 문제 상황 보기.
    @Transactional
    @Test
    public void testSearchImageReplyCount() {
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());
        boardRepository.searchWithAll(null,null,pageable);

    }

}

