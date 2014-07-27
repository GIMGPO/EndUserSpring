package trisoftdp.core;

public class DynProductGroup {
	public int prodNum;
	public String prodTitle;
	public String prodFolder;
	public String prodNote;
	public String multiGroupNote;
	public Boolean singleGroup = false;
	public Page[] pages;
	public enum PAGE_TYPE {dynamic, relDocs, link};


	public static class Page {
		public PAGE_TYPE pageType;
		public int pageNum;
		public String pageKey;
		public String pageName;
		public String pageDesc;
	}
}
