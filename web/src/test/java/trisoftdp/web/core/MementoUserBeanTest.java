package trisoftdp.web.core;

import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.DynamicPublishingPackage.DitaMap;

public class MementoUserBeanTest {
	
	@Test
	public void fakeTest() {}
	
//	@Test
	public void pushPopTest() {
		MementoUserBean mub = new MementoUserBean();
		DynamicPublishingPackage userPack = new DynamicPublishingPackage();
		mub.setPubLegend(new HashMap<String,String>());
		userPack.packageName = "package1";
		userPack.languages = new String[]{"Russian", "English"};
		DitaMap ditaMap1 = new DitaMap();
		ditaMap1.file = "gopa1";
		userPack.ditaMaps = new DitaMap[] {ditaMap1};
		mub.setUserPack(userPack);
//		System.out.println(mub.getUserPack().packageName);
//		for(String lang: mub.getUserPack().languages)
//			System.out.println(lang);
		try {
			mub.push(null);
			userPack.packageName = "package2";
			userPack.languages = new String[]{"French", "Spanish"};
			DitaMap ditaMap2 = new DitaMap();
			ditaMap2.file = "gopa2";
			userPack.ditaMaps = new DitaMap[] {ditaMap2};
			mub.push(null);
			ditaMap1.file = "gopa3";
//			System.out.println(mub.getUserPack().packageName);
//			for(String lang: mub.getUserPack().languages)
//				System.out.println(lang);
			
			while (!mub.getIsStackEmpty() ) {
				mub.pop();
				System.out.println(mub.getUserPack().packageName);
				for(String lang: mub.getUserPack().languages)
					System.out.println(lang);
				for(DitaMap dm: mub.getUserPack().ditaMaps)
					System.out.println("file=" + dm.file);
			}
			
		} catch (DynException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
