package trisoftdp.core;

import java.io.File;
import java.io.FileFilter;

public class FileFilterByExtension implements FileFilter{
	private String extension;
	public FileFilterByExtension(String extension) {
		this.extension = extension;
	}
	public boolean accept(File file) {
		return file.getName().endsWith(extension);
	}
}
