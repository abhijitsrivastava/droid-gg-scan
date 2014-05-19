package com.eduglasses.glassscan.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.eduglasses.glassscan.util.GlassScanUtil;
import com.eduglasses.glassscan.util.PropertiesFileReaderUtil;

@Controller
public class QRCodeController1 {
	
	@RequestMapping(value = "/generateQRCode", method = RequestMethod.GET)
	public ModelAndView showQRCode(HttpServletRequest request, ModelMap map) {
		
		String QRCodeStr = (String)map.get("QRCodeString");
		
		String QRCodeFileLoc = PropertiesFileReaderUtil.getApplicationProperty("qr.code.storage.path");
		
		HttpSession httpSession = request.getSession(false);
		String fileName = (String)httpSession.getAttribute("code");
		
		boolean isQRCodeGenerated = GlassScanUtil.generateQRCode(QRCodeStr, fileName, QRCodeFileLoc);
		ModelAndView modelAndView = new ModelAndView("QRCode");
		
		if(!isQRCodeGenerated) {
			
			String serverURL = PropertiesFileReaderUtil.getApplicationProperty("server.url");
			String QRCodeURL = PropertiesFileReaderUtil.getApplicationProperty("qr.code.url");
			String QRCodeImageURL = serverURL+"/"+QRCodeURL+"/"+ fileName.replace(".", "_").replace(",", "_")+".png";
			modelAndView.addObject("QRCodeImageURL",QRCodeImageURL);
		}
		
		return modelAndView;
	}


}
