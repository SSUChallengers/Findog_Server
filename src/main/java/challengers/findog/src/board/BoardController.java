package challengers.findog.src.board;

import challengers.findog.config.BaseException;
import challengers.findog.config.BaseResponse;
import challengers.findog.config.BaseResponseStatus;
import challengers.findog.src.board.model.*;
import challengers.findog.utils.JwtService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static challengers.findog.config.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;
    private final JwtService jwtService;

    /**
     * 게시글 작성 API
     *
     * @return PostBoardRes
     */
    @ApiOperation(value = "게시글 작성", notes = "body를 포함해서 post request를 보내면 postId와 userId를 리턴")
    @PostMapping("/post")
    public BaseResponse<BoardRes> createBoard(@Valid @ModelAttribute PostBoardReq postBoardReq) {
//        if (br.hasErrors()) {
//            String error = br.getAllErrors().get(0).getDefaultMessage();
//            return new BaseResponse<>(BaseResponseStatus.of(error));
//        }

        try {
            BoardRes boardRes = boardService.createBoard(postBoardReq);
            return new BaseResponse<>(boardRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 게시글 수정 API
     *
     * @param :postId
     * @return 수정완료 메세지
     */
    @ApiOperation(value = "게시글 수정", notes = "body값을 모두 포함해서 request 보내야함")
    @ApiImplicitParam(name = "postId", value = "게시글 ID", required = true, dataType = "int")
    @PatchMapping("/update/{postId}")
    public BaseResponse<String> updateBoard(@PathVariable("postId") int postId, @Valid @ModelAttribute PatchBoardReq patchBoardReq) {
        try {
            int userId = jwtService.getUserIdx();
            if (userId != boardService.checkAuth(postId)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            boardService.updateBoard(postId, patchBoardReq);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String result = "게시글이 수정되었습니다.";
        return new BaseResponse<>(result);
    }

    /**
     * 게시글 삭제 API
     *
     * @param :postId
     * @return 삭제완료 메세지
     */
    @ApiOperation(value = "게시글 삭제", notes = "유저 검증 후 존재하는 게시글에 대해서 해당 게시글, 이미지, 댓글을 모두 삭제")
    @ApiImplicitParam(name = "postId", value = "게시글 ID", required = true, dataType = "int")
    @DeleteMapping("/{postId}")
    public BaseResponse<String> deleteBoard(@PathVariable("postId") int postId) {
        try {
            //유저 권한 검증
            int userId = jwtService.getUserIdx();
            if (userId != boardService.checkAuth(postId)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //게시글 삭제
            boardService.deleteBoard(postId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String result = "게시글이 삭제되었습니다.";
        return new BaseResponse<>(result);
    }

    /**
     * 해당 게시글 조회 API
     *
     * @param :postId
     * @return getBoardRes
     */
    @ApiOperation(value = "해당 게시글 조회", notes = "해당 게시글을 포함한 이미지, 댓글을 모두 조회")
    @ApiImplicitParam(name = "postId", value = "게시글 ID", required = true, dataType = "int")
    @GetMapping("/{postId}")
    public BaseResponse<GetBoardRes> getBoard(@PathVariable("postId") int postId) {
        try {
            int userId = jwtService.getUserIdx();
            GetBoardRes getBoardRes = boardService.getBoard(postId, userId);
            return new BaseResponse<>(getBoardRes);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 전체 게시글 조회 API
     *
     * @return boardList
     */
    @ApiOperation(value = "게시물 조회", notes = "페이징 처리")
    @GetMapping("")
    public BaseResponse<List<Board>> getBoardList(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value="size", defaultValue = "5") int size){
        try{
            List<Board> boardList = boardService.getBoardList(page, size);
            return new BaseResponse<>(boardList);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 총 게시글 수 조회 API
     *
     * @return count
     */
    @ApiOperation(value = "게시물 조회", notes = "페이징 처리")
    @GetMapping("/count")
    public BaseResponse<Integer> getBoardCount(){
        try{
            return new BaseResponse<>(boardService.getBoardCount());
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 게시글 좋아요 API
     *
     * @return userId, postId
     */
    @ApiOperation(value = "게시글 좋아요")
    @PostMapping("/like")
    public BaseResponse<BoardRes> likeBoard(@Valid @ModelAttribute LikeBoardReq likeBoardReq) {
        try {
            int userId = jwtService.getUserIdx();
            //게시글 삭제
            BoardRes boardRes = boardService.likeBoard(userId, likeBoardReq.getPostId());
            return new BaseResponse<>(boardRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 게시글 좋아요 취소 API
     *
     * @return userId, postId
     */
    @ApiOperation(value = "게시글 좋아요")
    @DeleteMapping("/like")
    public BaseResponse<BoardRes> likeCancelBoard(@Valid @ModelAttribute LikeBoardReq likeBoardReq) {
        try {
            int userId = jwtService.getUserIdx();
            //게시글 삭제
            BoardRes boardRes =boardService.likeCancelBoard(userId, likeBoardReq.getPostId());
            return new BaseResponse<>(boardRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 게시글 검색 API
     *
     * @return boardList
     */
    @ApiOperation(value = "게시물 검색", notes = "페이징 처리")
    @GetMapping("/search")
    public BaseResponse<List<Board>> searchBoard(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value="size", defaultValue = "5") int size){
        try{
            List<Board> boardList = boardService.searchBoard(keyword, page, size);
            return new BaseResponse<>(boardList);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}