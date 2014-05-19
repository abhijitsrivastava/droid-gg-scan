package com.eduglasses.glassscan.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.apache.log4j.Logger;

public class GlassScanUtil {

	private static final Logger logger = Logger.getLogger(GlassScanUtil.class);
	/*
	 * Method returns the stack trace of exception in string format. Used for
	 * logging of exception.
	 */
	public static String getExceptionDescriptionString(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		return stringWriter.toString();
	}
	
	public static boolean generateQRCode(String QRCodeStr, String fileName, String QRCodeFileLoc) {
		
		boolean isError = false;
		
		int width = new Integer(PropertiesFileReaderUtil.getPropertyValue("qr.code.image.width"));
		int height = new Integer(PropertiesFileReaderUtil.getPropertyValue("qr.code.image.height"));
		
		ByteArrayOutputStream out = QRCode.from(QRCodeStr).to(ImageType.PNG).withSize(width, height).stream();
		FileOutputStream fout = null;
		
		try {
			
			 File glassResourceFolder = new File(QRCodeFileLoc);
			 
			 if(!glassResourceFolder.exists()) {
				 glassResourceFolder.mkdirs();
			 }
			 
			String  QRCodeFileFullLoc = QRCodeFileLoc + "/" + fileName.replace(".", "_").replace(",", "_").replace("/", "_").replace("\\", "_") + ".png";
			
			File QRCodeFile = new File(QRCodeFileFullLoc);
			if(QRCodeFile.exists()) {
				QRCodeFile.delete();
			}
			
            fout = new FileOutputStream(QRCodeFile);
    
            fout.write(out.toByteArray());

            fout.flush();

       } catch (FileNotFoundException ex) {
       		isError = true;
       		logger.fatal(GlassScanUtil.getExceptionDescriptionString(ex));
       } catch (IOException ex) {
    	   isError = true;
    	   logger.fatal(GlassScanUtil.getExceptionDescriptionString(ex));
       } finally {
       	if(fout != null) {
       		try {
					fout.close();
				} catch (IOException e1) {
					isError = true;
					logger.fatal(GlassScanUtil.getExceptionDescriptionString(e1));
				}
       	}
       }
		
		return isError;
	}
}
