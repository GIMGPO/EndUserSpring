package trisoftdp.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ToolKit {

	static Pattern malicious = Pattern.compile(".*[><'\"\n\r\\(\\)].*");//add more symbols

    public static byte[] fileToByte(String filename){ 
    	//TODO it is worthwhile to zip it first
        byte[] bytes = null;
        BufferedInputStream is = null;
        try { 
            File file = new File(filename); 
            bytes = new byte[(int) file.length()]; 
            is = new BufferedInputStream(new FileInputStream(file)); 
            is.read(bytes); 
        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } finally {
        	if(is != null) try { is.close(); } catch(IOException e) { System.out.println("fileToByte:" + e.getMessage()); }
        }
        return bytes; 
    } 
    
	public static String printRequest(DynamicPublishingPackage pack ) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("pubPackage=<%s> packageName=<%s> productRelease=<%s> createDate=<%s>%n", pack.pubPackage, pack.packageName, pack.productRelease, pack.createDate));
		sb.append("ditaMaps info:\n");
		for(int i = 0; i < pack.ditaMaps.length; i++)
			sb.append(String.format("file=<%s> task=<%s> title=<%s>%n", pack.ditaMaps[i].file, pack.ditaMaps[i].task, pack.ditaMaps[i].title));
		sb.append("profiles info:\n");
		for(int i = 0; i < pack.profiles.length; i++) {
			sb.append(String.format("name=<%s>:%n", pack.profiles[i].name));
			if(pack.profiles[i].values == null) {
				sb.append("pack.profiles[" + i + "].values == null\n");
				continue;
			}
			for(int j = 0; j < pack.profiles[i].values.length; j++)
				sb.append(String.format("\tname=<%s> id=<%s> multiselect=<%s>%n", pack.profiles[i].values[j].name, pack.profiles[i].values[j].id, pack.profiles[i].values[j].multiselect));
		}
		return sb.toString();
	}
	
	public static int runShellCmd(String[] args, File curDir, String[] envPars, boolean redirectError) throws DynException {
		BufferedReader br = null;
		ProcessBuilder pb = null;
		Process p = null;
		String line;
		String cmd = "";
		int exitValue = -100;
		for(String arg: args ) cmd = cmd.concat(arg + " ");
		CoreConstants.logger.info("Running: " + cmd);
		try {
			pb = new ProcessBuilder(args);
			pb.redirectErrorStream(redirectError);
			if(curDir != null)
				pb.directory(curDir);
			if(envPars != null && envPars.length % 2 == 0) {
				Map<String, String> env = pb.environment();
				for(int i = 0; i < envPars.length; i +=2)
					env.put(envPars[i], envPars[i+1]);
			}
			p = pb.start();
			StringBuilder buf = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null)
				buf.append(line +"\n");
			CoreConstants.logger.info(buf.toString());
			br.close();
			buf.setLength(0);
			if(!redirectError) {
				br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = br.readLine()) != null)
					buf.append(line +"\n");
			}	
			exitValue = p.waitFor();
			if(buf.length() > 0)
				CoreConstants.logger.severe(cmd + "\nexit code " + exitValue + "\nstderr: " + buf.toString());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			throw new DynException("InterruptedException: " + e.getMessage());
		} finally {
			CoreConstants.logger.info("Exit Value of the runShellCmd: " + exitValue);
			if( br != null) try { br.close(); } catch(IOException e) {e.printStackTrace();}
			if (p != null) p.destroy();
		}
		return exitValue;
	}

	public static final void zipDirectory(File directory, File zip) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
		zip(directory, directory, zos);
		zos.close();
	}

	public static final void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
		if (!sourceLocation.exists())
			throw new IOException("Source does not exist " + sourceLocation);
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists())
				if(!targetLocation.mkdir())
					throw new IOException("Failed to create dir " + targetLocation);
			String[] children = sourceLocation.list();
			for (int i=0; i<children.length; i++)
				copyDirectory(new File(sourceLocation, children[i]),new File(targetLocation, children[i]));
		} else {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream(sourceLocation);
				out =new FileOutputStream(targetLocation);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
					out.write(buf, 0, len);
			} finally {
				if(in != null) try {in.close();} catch(IOException e) { e.printStackTrace(); }
				if(out != null) try {out.close();} catch(IOException e) { e.printStackTrace(); }
			}
		}
	}

	public static long generateId() { 
		return System.currentTimeMillis();
	}

	private static String md5DigestToString(byte[] digest) {
		String md5 = "";
		for (byte b: digest)
			md5 = md5.concat(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		return md5;
	}


	public static File getResultById(long resultId, String resultDir) {
		File rf = null;
		File[] files = new File(resultDir).listFiles();
		for(File f: files) {
			if(!f.getName().startsWith("" + resultId))
				continue;
			rf = f;
			break;
		}
		return rf;
	}

	public static File getFileById(long id, File dirName) {
		File rf = null;
		File[] files = dirName.listFiles();
		for(File f: files) {
			if(!f.getName().startsWith("" + id))
				continue;
			rf = f;
			break;
		}
		return rf;
	}

	public static String getMD5(Serializable o) throws DynException {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		byte[] obj = null;
		MessageDigest dgst = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			obj = baos.toByteArray();
			dgst = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new DynException("NoSuchAlgorithmException: " + e.getMessage());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		} finally {
			if(baos != null) try { baos.close(); } catch(IOException e) {e.printStackTrace();}
			if(oos != null) try { oos.close(); } catch(IOException e) {e.printStackTrace();}
		}
		dgst.update(obj);
		return md5DigestToString(dgst.digest());
	}

	public static void setUserPackValues(DynamicPublishingPackage pack, int profileNum, DynamicPublishingPackage.ProfileValue values[], String[] idsFromRequest) {
		if (idsFromRequest == null) {
			pack.profiles[profileNum].values = new DynamicPublishingPackage.ProfileValue[values.length];
			for(int i = 0; i < values.length; i++)
				pack.profiles[profileNum].values[i] = values[i];
		} else {
			pack.profiles[profileNum].values = new DynamicPublishingPackage.ProfileValue[idsFromRequest.length];
			pack.profiles[profileNum].values[0] = new DynamicPublishingPackage.ProfileValue();
			int idx = 0;
			for(String idFromRequest: idsFromRequest) {
				for(int i = 0; i < values.length; i++) {
					if (idFromRequest.equals(values[i].id)) {
						pack.profiles[profileNum].values[idx++] = values[i];
						break;
					}
				}
			}
		}
	}

	public static List<DynamicPublishingPackage.DitaMap> getCommonArrayElements (List<DynamicPublishingPackage.DitaMap> list1, List<DynamicPublishingPackage.DitaMap> list2){
		List<DynamicPublishingPackage.DitaMap> commonElements = new ArrayList<DynamicPublishingPackage.DitaMap>();
		for (int i = 0; i < list2.size(); i++) {
			if (list1.contains(list2.get(i))) {
				commonElements.add(list2.get(i));
			}
		}
		for (int i = 0; i < list1.size(); i++) {
			if (list2.contains(list1.get(i))) {
				if(!commonElements.contains(list1.get(i))) commonElements.add(list1.get(i));
			}
		}

		return commonElements;
	}

	public static String arrayToString2(String[] a, String separator) {
		StringBuilder result = new StringBuilder();
		if (a.length > 0) {
			result.append(a[0]);
			for (int i=1; i<a.length; i++) {
				result.append(separator);
				result.append(a[i]);
			}
		}
		return result.toString();
	}
	public static String[] joinArrays(String[] first, String[] second) {
		List<String> both = new ArrayList<String>(first.length + second.length);
		Collections.addAll(both, first);
		Collections.addAll(both, second);
		return both.toArray(new String[] {});
	}

	/*public static String removeSpaces(String s) {
		StringTokenizer st = new StringTokenizer(s," ",false);
		String t = "";
		while (st.hasMoreElements()) 
			t = t.concat((String) st.nextElement());
		return t;
	}*/

	public static String humanByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String[] getDisplayLanguages() {
		String[] languages = new String[CoreConstants.dynPubLocales.length];
		int i =0;
		for(Locale loc: CoreConstants.dynPubLocales)
			languages[i++] = loc.toString() + ':' + loc.getDisplayLanguage();
		Arrays.sort(languages);
		return languages;
	}

	public static boolean isLangSupported(String lang) {
		if(lang == null)
			return false;
		for(Locale loc: CoreConstants.dynPubLocales)
			if(loc.toString().equals(lang))
				return true;
		return false;
	}

	public static void deleteDir(File dir) {
		if(!dir.exists())
			return;
		File[] kids = dir.listFiles();
		for(int i = 0; i < kids.length; i++) {
			if(kids[i].isDirectory())
				deleteDir(kids[i]);
			else
				if(!kids[i].delete()) 
					System.err.println("Failed to delete " + kids[i]);
		}
		if(!dir.delete())
			System.err.println("Failed to delete " + dir);
	}

	private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		FileInputStream in = null;
		ZipEntry entry = null;
		try {
			for (File f : files) {
				if (f.isDirectory())
					zip(f, base, zos);
				else {
					in = new FileInputStream(f);
					entry = new ZipEntry(f.getPath().substring(	base.getPath().length() + 1));
					zos.putNextEntry(entry);
					while (-1 != (read = in.read(buffer)))
						zos.write(buffer, 0, read);
					in.close();
				}
			}
		} finally {
			if(in != null) try { in.close(); } catch(IOException e) { e.printStackTrace(); } 
		}
	}

	public static String getParameterValidated(String reqParam, String reqValue) throws DynException {
		if(reqValue == null)
			throw new DynException(reqParam + " = null");
		Matcher m = malicious.matcher(reqValue);
		if(m.matches())
			throw new DynException(reqParam + " has malicious characters: " + reqValue);
		if ("configId".equals(reqParam)) {

		} else if ("rd".equals(reqParam)) {

		} else if ("file".equals(reqParam)) {

		} else if ("docId".equals(reqParam)) {

		}
		return reqValue;
	}
}
