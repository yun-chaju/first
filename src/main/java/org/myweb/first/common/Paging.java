package org.myweb.first.common;

public class Paging implements java.io.Serializable {
	private static final long serialVersionUID = -8649527407666546438L;

	private int startRow;		//해당 페이지에 출력할 시작행 (쿼리문에 적용)
	private int endRow;		//해당 페이지에 출력할 끝행 (쿼리문에 적용)
	private int listCount;		//총 목록(행) 갯수 (뷰페이지에서 사용)
	private int limit;			//한 페이지에 출력할 갯수 (뷰페이지에서 사용)
	private int currentPage;		//출력할 현재 페이지 (뷰페이지에서 사용)
	private int maxPage;		//총 페이지수 (마지막 페이지) (뷰페이지에서 사용)
	private int startPage;		//현재 페이지가 속한 페이지 그룹의 시작값 (뷰페이지에서 사용)
	private int endPage;		//현재 페이지가 속한 페이지 그룹의 끝값 (뷰페이지에서 사용)
	private String urlMapping;  //페이지 숫자 클릭시 요청할 url 저장용 (여러 서비스에서 함께 사용하기 위함) (뷰페이지에서 사용)
	
	//기본생성자 없음
	
	//매개변수 4개 있는 생성자만 사용함
	public Paging(int listCount, int limit, int currentPage, String urlMapping) {
		super();
		this.listCount = listCount;
		this.limit = limit;
		this.currentPage = currentPage;
		this.urlMapping = urlMapping;
	}
	
	//페이지 계산 메소드
	public void calculate() {
		//총 페이지 수 계산 : 
		//목록의 총 갯수가 21개인 경우, 한 페이지에 출력할 목록이 10개인 경우 총 페이지 수는 3페이지임
		//목록 1개도 한 페이지 차지함
		this.maxPage = (int)((double)listCount / limit + 0.9);
		
		//뷰페이지 출력에 사용할 페이지 그룹의 시작값 지정
		//목록 하단에 출력할 페이지 그룹의 페이지 숫자 갯수를 10개씩 출력한다면,
		//현재 페이지가 3이면, 페이지 그룹은 1페이지부터 10페이지까지 표시됨
		//현재 페이지가 25페이지이면, 페이지 그룹은 21페이지부터 30페이지까지 표시됨
		this.startPage = (int)(((double)currentPage / 10 + 0.9) - 1) * 10 + 1;
		this.endPage = startPage + 10 - 1;
		
		//마지막 그룹의 끝값을 마지막 페이지와 맞춤
		if(maxPage < endPage) {
			endPage = maxPage;
		}
		
		//요청한 현재 페이지에 출력될 목록의 행번호를 계산
		//한 페이지에 출력할 목록 갯수가 10개인 경우, 현재 페이지가 3페이지이면
		//조회할 행은 21행부터 30행이 됨
		this.startRow = (currentPage - 1) * limit + 1;
		this.endRow = startRow + limit - 1;		
	}

	@Override
	public String toString() {
		return "Paging [startRow=" + startRow + ", endRow=" + endRow + ", listCount=" + listCount + ", limit=" + limit
				+ ", currentPage=" + currentPage + ", maxPage=" + maxPage + ", startPage=" + startPage + ", endPage="
				+ endPage + ", urlMapping=" + urlMapping + "]";
	}

	//getters and setters	
	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getListCount() {
		return listCount;
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public String getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(String urlMapping) {
		this.urlMapping = urlMapping;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}











