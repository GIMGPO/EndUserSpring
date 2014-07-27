package trisoftdp.core;

public class DynRelatedDocs {
	public enum FILE_TYPE {pdf,zip};
	public DocGroup[] docGroups;
	public String rdTitle;

	public static class DocGroup {
		public String groupName;
		public int groupNum;
		public RelDoc[] relDocs;
	}
	public static class RelDoc {
		public String docName;
		public int docNum;
		public String docFile;
		public String helpFolder;
		public String helpFile;
		public FILE_TYPE fileType = FILE_TYPE.pdf;
	}
}
