package trisoftdp.core;


public class DynPage {
	public String pageTitle;
	public String pageGroupsTitle;
	public String pageIntro;
	public DynPackageGroup[] packGroups;
	
	public static class DynPackageGroup {
		public String groupName;
		public int groupNum;
		public Pack[] packs;
	
		public static class Pack {
			public String packFileName;
			public int packNum;
		}
	}
}
