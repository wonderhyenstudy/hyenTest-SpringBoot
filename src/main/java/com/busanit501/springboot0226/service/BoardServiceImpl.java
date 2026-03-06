package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.dto.BoardDTO;
import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import com.busanit501.springboot0226.repository.BoardRepository;
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

    // 글 등록 (Register)
    @Override
    public Long register(BoardDTO boardDTO) {
        // 1. 화면에서 넘어온 DTO를 DB 저장용 Entity로 변환
        Board board = modelMapper.map(boardDTO, Board.class);
        // 2. Repository를 통해 DB에 저장하고 생성된 번호(bno)를 반환
        Long bno = boardRepository.save(board).getBno();
        return bno;
    }

    // 조회 (Read One)
    @Override
    public BoardDTO readOne(Long bno) {
        // 1. bno에 해당하는 데이터를 찾음 (Optional로 감싸서 null 안전성 확보)
        Optional<Board> result = boardRepository.findById(bno);
        // 2. 결과가 없으면 예외 발생, 있으면 객체 꺼내기
        Board board = result.orElseThrow();
        // 3. Entity를 화면 전달용 DTO로 변환하여 반환
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
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
        // 3) 저장(수정)
        // 3. 변경된 내용을 DB에 반영
        boardRepository.save(board);
    }

    // 삭제 (Remove)
    @Override
    public void remove(Long bno) {
        // PK(bno)를 기준으로 DB 데이터 삭제
        boardRepository.deleteById(bno);
    }

    // 목록 조회 및 페이징 (List)
    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        // 1. 검색 조건 및 페이지 번호/크기 정보를 추출
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        // result 안에 페이징 준비물이 많이 들어 있다.
        // 2. Querydsl 등이 적용된 searchAll 메서드로 페이징 처리된 Entity 목록 조회
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);
        // 페이징 처리가 된 데이터 10개 목록 가져오고,
        // 3. 조회된 Entity 리스트를 DTO 리스트로 일괄 변환 (Stream API 활용)
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
        // 전체 갯수등 가져오기.
        int total = (int)result.getTotalElements();
        // PageResponseDTO 타입으로 객체를 생성.
        // 4. 화면에서 필요한 페이징 정보(시작페이지, 끝페이지, 총 갯수 등)를 가공하여 반환
        PageResponseDTO<BoardDTO> pageResponseDTO = PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();

        return pageResponseDTO;
    }
}
