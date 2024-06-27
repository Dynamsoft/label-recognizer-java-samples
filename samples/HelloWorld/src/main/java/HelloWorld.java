import com.dynamsoft.dlr.*;

public class HelloWorld {
	public static void main(String[] args) {
		try {
			
			// 1.Initialize license.
			// The string "DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9" here is a free public trial license. Note that network connection is required for this license to work.
			// If you want to use an offline license, please contact Dynamsoft Support: https://www.dynamsoft.com/company/contact/
			// You can also request a 30-day trial license via the link: https://www.dynamsoft.com/customer/license/trialLicense?product=dlr&utm_source=github&package=java
			LabelRecognizer.initLicense("DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9");
			
		    // 2.Create an instance of Label Recognizer.
			LabelRecognizer dlr = new LabelRecognizer();
			
	        // 3.Recognize text from an image file.
			DLRResult[] results = dlr.recognizeByFile("../../images/dlr-sample.png", "");
			
			if (results != null && results.length > 0) {
				for (int i = 0; i < results.length; i++) {
					
					// Get result of each text area (also called label).
					DLRResult result = results[i];
					System.out.println("Result " + i + ":");
					for (int j = 0; j < result.lineResults.length; j++) {
						
						// Get the result of each text line in the label.
						DLRLineResult lineResult = result.lineResults[j];
						System.out.println(">>Line Result " + j + ": " + lineResult.text);
					}
				}
			} else {
				System.out.println("No data detected.");
			}
		} catch (LabelRecognizerException ex) {
			ex.printStackTrace();
		}
	}
}
