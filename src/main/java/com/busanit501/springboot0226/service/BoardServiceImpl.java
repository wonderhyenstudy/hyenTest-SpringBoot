package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.Reply;
import com.busanit501.springboot0226.dto.*;
import com.busanit501.springboot0226.repository.BoardRepository;
import com.busanit501.springboot0226.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
// 이 클래스가 비즈니스 로직을 담당하는 서비스 객체임을 스프링 컨테이너에 알림
// BoardService 인터페이스를 구현한 클래스가 BoardServiceImpl 다 라고 알려줌
// 스프링이 실행될때 자동으로 연결
@Service
// 로그 기록(log.info 등)을 위한 기능 활성화
@Log4j2
// final이 붙거나 @NonNull이 설정된 필드만을 파라미터로 받는 생성자를 자동으로 생성해주는 Lombok 어노테이션
// final 필드(의존성)를 매개변수로 하는 생성자를 자동 생성 (의존성 주입)
@RequiredArgsConstructor
// 작업 후 저장 또는 되돌리기 하기 위해서 필요함
// 스프링 프레임워크에서 선언적 트랜잭션 관리를 위해 사용하는 어노테이션입니다.
// 해당 어노테이션을 메서드나 클래스에 붙이면, 스프링이 자동으로 트랜잭션의 시작, 커밋, 롤백을 처리해 줍니다.
// 메서드 실행 중 예외 발생 시 롤백, 성공 시 커밋을 보장하는 트랜잭션 관리
@Transactional
public class BoardServiceImpl implements BoardService{
    // 화면으로부터, 글작성 재료를 DTO에 받아서, 엔티티 객체 타입으로 변환해서, 전달하는 용도

    // 전에는 DTO -> VO, DTO -> Entity 객체 변환
    // DTO <-> Entity 객체 간 변환 도구
    private final ModelMapper modelMapper;
    // 실제 DB에 일을 시키는 기능.
    // DB 접근을 위한 인터페이스
    private final BoardRepository boardRepository;

    // 댓글의 삭제 기능 쓰고 싶어서 댓글의 기능들 다 들고왔다
    // 댓글 기능 추가,
    private final ReplyRepository replyRepository;

    // 글 등록 (Register)
    @Override
    public Long register(BoardDTO boardDTO) {
        // 1. 화면에서 넘어온 DTO를 DB 저장용 Entity로 변환
//        Board board = modelMapper.map(boardDTO, Board.class);
        // 방법 자동으로 변경 20260320
        Board board = dtoToEntity(boardDTO);
        // 2. Repository를 통해 DB에 저장하고 생성된 번호(bno)를 반환
        Long bno = boardRepository.save(board).getBno();
        return bno;
    }

    // 조회 (Read One)
    @Override
    public BoardDTO readOne(Long bno) {
        // 1. bno에 해당하는 데이터를 찾음 (Optional로 감싸서 null 안전성 확보)
//        Optional<Board> result = boardRepository.findById(bno);
        Optional<Board> result = boardRepository.findByIdWithImages(bno);
        // 2. 결과가 없으면 예외 발생, 있으면 객체 꺼내기
        Board board = result.orElseThrow();
        // 3. Entity를 화면 전달용 DTO로 변환하여 반환
//        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        BoardDTO boardDTO = entityToDto(board);
        return boardDTO;
    }

    // 수정 (Modify)
    @Override
    public void modify(BoardDTO boardDTO) {
        // 1)디비에서 데이터를 가져오고, 가져온 내용을 화면에서, 2)변경할 내용으로 교체후, 3) 저장(수정)
        // 1. 수정할 대상 데이터를 DB에서 먼저 조회
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());
        Board board = result.orElseThrow();
        // 2) 변경할 내용으로 교체후
        // 2. Entity 내부의 변경 메서드(change)를 호출하여 데이터 수정 (더티 체킹 활용)
        board.change(boardDTO.getTitle(), boardDTO.getContent());

        // 첨부 이미지를 이용한 수정작업,
        // 기존 이미지를 모두 삭제 후, 새로운 이미지 추가
        board.clearImages();// 첨부 이미지의 부모 게시글 번호를 null 로 변경, 고아객체,

        // 화면으로부터, 첨부된 이미지가 있다면, 그러면, 추가.
        if(boardDTO.getFileNames() != null) {
            for(String fileName : boardDTO.getFileNames()) {
                // 예시
                // fileName : 5b418a60-407e-406e-991e-db88d35ea426_크롬기준-로컬스토리지 저장소 확인 방법.PNG
                // fileName : UUID_원본파일명
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }




        // 3) 저장(수정)
        // 3. 변경된 내용을 DB에 반영
        boardRepository.save(board);
    }

    // 삭제 (Remove)
    @Override
    public void remove(Long bno) {
        // PK(bno)를 기준으로 DB 데이터 삭제
//        boardRepository.deleteById(bno);

        // 게시글을 삭제하면, 댓글은
        // 댓글의 존재 여부를 확인 후, 있으면 삭제하기.
        List<Reply> result = replyRepository.findByBoard_Bno(bno);
        boolean checkReplyList = result.isEmpty() ? false : true;
        if(checkReplyList) {
            replyRepository.deleteByBoard_Bno(bno);
        }
        // 게시글만 삭제, 참고로, 연관관계로, 게시글이 삭제가 되면,
        // 자동으로, 첨부 이미지는 삭제가됨.
        boardRepository.deleteById(bno);

    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        // result 안에 페이징 준비물이 많이 들어 있다.
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);
        // 페이징 처리가 된 데이터 10개 목록 가져오고,
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
        // 전체 갯수등 가져오기.
        int total = (int)result.getTotalElements();
        // PageResponseDTO 타입으로 객체를 생성.
        PageResponseDTO<BoardDTO> pageResponseDTO = PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();

        return pageResponseDTO;
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        // result 안에 페이징 준비물이 많이 들어 있다.
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);
        // 페이징 처리가 된 데이터 10개 목록 가져오고,
        // 자동으로, queryDSL 에서 자동으로 변환을 해주기 때문에, 수동으로 변환 안해도 됩니다.
        // searchWithReplyCount , 내부에서 확인.

//        List<BoardDTO> dtoList = result.getContent().stream()
//                .map(board -> modelMapper.map(board, BoardDTO.class))
//                .collect(Collectors.toList());
        // 전체 갯수등 가져오기.
        int total = (int)result.getTotalElements();
        // PageResponseDTO 타입으로 객체를 생성.
        PageResponseDTO<BoardListReplyCountDTO> pageResponseDTO = PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total(total)
                .build();

        return pageResponseDTO;
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        // 수정1, <========searchWithAll 교체 ========================
        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types,keyword,pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }





}
