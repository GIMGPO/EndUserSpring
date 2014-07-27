/**
 * The class representing the main object for the LCA DP application
 * Has all elements and attributes of the trisoft_dynpub_package.dtd
 * 
 * Updated Feb 2014: 
 * 1. New attribute added to the class ProfileValue(), multiselect. 
 *    See corresponding changes in PackageData.java.getPackage().
 * 2. All the classes made Cloneable to address Next/Previous navigation
 * 
 * @author shadrn1
 *
 */

package trisoftdp.core;

import java.io.Serializable;
import java.util.Date;

public class DynamicPublishingPackage implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;

	public enum PROFILE_STATUS {hidden,normal};
	public enum OUTPUT_TYPE {pdf2,xhtml};
	public enum PROFILE_SELECT {user,auto};
	public enum REVERSE {yes,no};
	public boolean PROFILE_MULTISELECT;

	public String pubPackage;
	public String packageName; 
	public String productRelease;
	public Date createDate;
	public DitaMap[] ditaMaps = new DitaMap[0];
	public String[] languages = new String[0];
	public Profile[] profiles = new Profile[0];
	public OUTPUT_TYPE outputType = OUTPUT_TYPE.pdf2;
	public REVERSE quesFirst = REVERSE.no;

	
	@Override
	protected DynamicPublishingPackage clone() throws CloneNotSupportedException {
		DynamicPublishingPackage p = (DynamicPublishingPackage) super.clone();
		if(ditaMaps != null) {
			p.ditaMaps = new DitaMap[ditaMaps.length];
			for(int i = 0; i < ditaMaps.length; i++) {
				p.ditaMaps[i] = ditaMaps[i].clone();
			}
		}		
		p.languages = (languages == null)? null:languages.clone();
		p.profiles = (profiles == null)? null:profiles.clone();
		return p;
	}

	public static class DitaMap implements Serializable, Cloneable {
		private static final long serialVersionUID = 1L;
		public String title;
		public String task;
		public String file;
		public MapProfile[] mapProfiles;

		//@Override
		protected DitaMap clone() throws CloneNotSupportedException {
			DitaMap dm = (DitaMap) super.clone();
			if(mapProfiles != null) {
				dm.mapProfiles = new MapProfile[mapProfiles.length];
				for(int i =0; i < mapProfiles.length; i++)
					dm.mapProfiles[i] = mapProfiles[i].clone();
			}
			return dm;
		}
	}

	public static class Profile implements Serializable, Cloneable{
		private static final long serialVersionUID = 1L;
		public String id;
		public PROFILE_STATUS status;
		public PROFILE_SELECT selectType;
		public String name;
		public int quesNum; 
		public String quesString;
		public String quesInfo;
		public ProfileValue[] values;

		@Override
		protected Profile clone() throws CloneNotSupportedException {
			Profile p = (Profile) super.clone();
			if(values != null) {
				p.values = new ProfileValue[values.length];
				for(int i = 0; i < values.length; i++)
					p.values[i] = values[i].clone();
			}
			return p;
		}
	}

	public static class MapProfile implements Serializable, Cloneable{
		private static final long serialVersionUID = 1L;
		public String name;
		public String[] values;

		@Override
		protected MapProfile clone() throws CloneNotSupportedException {
			MapProfile p = (MapProfile) super.clone();
			p.values = (values == null)? null: values.clone();
			return p;
		}
	}

	public static class ProfileValue implements Serializable, Cloneable{
		private static final long serialVersionUID = 1L;
		public String id;
		public String name; 
		public boolean multiselect = false;
		public String[] dependentIds;

		@Override
		protected ProfileValue clone() throws CloneNotSupportedException {
			ProfileValue p = (ProfileValue) super.clone();
			p.dependentIds = (dependentIds == null)? null: dependentIds.clone();
			return p;
		}
	}

}
