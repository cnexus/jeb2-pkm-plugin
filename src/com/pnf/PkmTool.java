package com.pnf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.pnfsoftware.jeb.util.IO;

public class PkmTool {
	private static final String TEMP_DIR = "pkm_temp";
	private static File TEMP = null;
	static {
		try {
			TEMP = IO.createTempFolder(TEMP_DIR);
		} catch (IOException e) {
			PkmPlugin.LOG.catching(e);
		}
	}
	
	private ByteBuffer bytes;
	private String name;
	private File etcTool;
	
	public PkmTool(String name, File etcTool, byte[] data){
		bytes = ByteBuffer.wrap(data);
		this.etcTool = etcTool;
	}
	
	private File dumpPkm(){
		File pkm = TEMP.toPath().resolve(name).toFile();
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(pkm);
			stream.write(bytes.array());
		} catch (IOException e) {
			PkmPlugin.LOG.catching(e);
		} finally {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					PkmPlugin.LOG.catching(e);
				}
			}
		}
		
		return pkm;
	}
	
	private String wrap(String input){
		StringBuffer buff = new StringBuffer();
		buff.append("\"");
		buff.append(input);
		buff.append("\"");
		
		return buff.toString();
	}
	
	public boolean dumpPng(){
		boolean success = false;
		File input = dumpPkm();
		
		Process p = null;
		
		try {
			p = Runtime.getRuntime()
					.exec(new String[]{wrap(etcTool.getAbsolutePath()),
							"--decode",
							wrap(input.getAbsolutePath())});
		} catch (IOException e) {
			PkmPlugin.LOG.catching(e);
		}
		
		if(p != null){
			try {
				success = p.waitFor() == 0;
			} catch (InterruptedException e) {
				PkmPlugin.LOG.catching(e);
			}
		}
		
		return success;
	}
}
