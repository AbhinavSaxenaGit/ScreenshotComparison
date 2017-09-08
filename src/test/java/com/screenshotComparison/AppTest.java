package com.screenshotComparison;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AppTest {
	
	//Creating object of CommonSeleniumMethods class
	CommonSeleniumMethods CSMObj = new CommonSeleniumMethods();

	//Class variables
	String browserName = ConfigReader.getProperty("browserName");
	String homePageScreen = ConfigReader.getProperty("homePageScreenUrl");
	
	@BeforeMethod
    public void initiateBrowser(){
		CSMObj.startBrowser(browserName);
    }
	
	@Test
    public void homePageScreen() throws Exception{
		Assert.assertTrue(CSMObj.openURL(homePageScreen), "Test failed while opening URL in homePageScreen");
		Assert.assertTrue(CSMObj.takeScreenShot(), "Test failed while taking screenhot in homePageScreen");
		Assert.assertTrue(CSMObj.compareImages(), "Test failed while comparing screenhot in homePageScreen");
    }	
	
	@AfterMethod
    public void quitBrowser(){
		CSMObj.stopBrowser();
    }

}
