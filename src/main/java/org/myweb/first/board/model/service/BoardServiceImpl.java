package org.myweb.first.board.model.service;

import java.util.ArrayList;

import org.myweb.first.board.model.dao.BoardDao;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("boardService")
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardDao boardDao;

	@Override
	public ArrayList<Board> selectTop3() {
		return boardDao.selectTop3();
	}

	@Override
	public int selectListCount() {
		return boardDao.selectListCount();
	}

	@Override
	public ArrayList<Board> selectList(Paging paging) {
		return boardDao.selectList(paging);
	}

	@Override
	public Board selectBoard(int boardNum) {
		return boardDao.selectBoard(boardNum);
	}

	//dml --------------------------------------------
	@Override
	public void updateAddReadCount(int boardNum) {
		boardDao.updateAddReadCount(boardNum);
	}

	@Override
	public int insertBoard(Board board) {
		return boardDao.insertBoard(board);
	}

	@Override
	public int insertReply(Board reply) {
		return boardDao.insertReply(reply);
	}

	@Override
	public int updateReplySeq(Board reply) {
		return boardDao.updateReplySeq(reply);
	}

	@Override
	public int updateBoard(Board board) {
		return boardDao.updateBoard(board);
	}

	@Override
	public int updateReply(Board reply) {
		return boardDao.updateReply(reply);
	}

	@Override
	public int deleteBoard(Board board) {
		return boardDao.deleteBoard(board);
	}
	
	// 검색 관련 -------------------------------------------
	@Override
	public int selectSearchTitleCount(String keyword) {
		return boardDao.selectSearchTitleCount(keyword);
	}

	@Override
	public int selectSearchWriterCount(String keyword) {
		return boardDao.selectSearchWriterCount(keyword);
	}

	@Override
	public int selectSearchDateCount(Search search) {
		return boardDao.selectSearchDateCount(search);
	}

	@Override
	public ArrayList<Board> selectSearchTitle(Search search) {
		return boardDao.selectSearchTitle(search);
	}

	@Override
	public ArrayList<Board> selectSearchWriter(Search search) {
		return boardDao.selectSearchWriter(search);
	}

	@Override
	public ArrayList<Board> selectSearchDate(Search search) {
		return boardDao.selectSearchDate(search);
	}
}












