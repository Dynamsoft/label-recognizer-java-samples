import com.dynamsoft.dlr.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PassportMRZReading {

    public static void main(String[] args){
        String strLine = null;
        String imagePath = null;
        boolean bool = false;
        System.out.println("*************************************************");
        System.out.println("Welcome to Dynamsoft Label Recognizer - Passport MRZ Sample");
        System.out.println("*************************************************");
        System.out.println("Hints: Please input 'Q' or 'q' to quit the application.");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            while (true){
                System.out.println();
                System.out.println(">> Step 1: Input your image file's full path:");
                try {
                    strLine = br.readLine().trim();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (strLine != null && strLine.length() > 0){
                    if (strLine.equalsIgnoreCase("q")) {
                        bool = true;
                        break;
                    }
                    if (strLine.length() >= 2 && strLine.charAt(0) == '\"' && strLine.charAt(strLine.length() - 1) == '\"') {
                        imagePath = strLine.substring(1, strLine.length() - 1);
                    } else {
                        imagePath = strLine;
                    }

                    File file = new File(imagePath);
                    if (file.exists() && file.isFile()){
                        break;
                    }
                }
                System.out.println("Please input a valid path.");
            }

            if (bool){
                break;
            }

            System.out.println();
            System.out.println("Recognition Results:");
            System.out.println("----------------------------------------------------------");

            try {
                // 1.Initialize license.
                // The string "DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9" here is a free public trial license. Note that network connection is required for this license to work.
                // If you want to use an offline license, please contact Dynamsoft Support: https://www.dynamsoft.com/company/contact/
                // You can also request a 30-day trial license via the link: https://www.dynamsoft.com/customer/license/trialLicense?product=dlr&utm_source=github&package=java
                LabelRecognizer.initLicense("DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9");
                
                // 2. Create an instance of Label Recognizer.
                LabelRecognizer dlr = new LabelRecognizer();
                
                // 3. Append config by a template json file.
                dlr.appendSettingsFromFile("./wholeImgMRZTemplate.json");
                
                long startTime = System.currentTimeMillis();
                
                // 4. Recognize text from the image file. The second parameter is set to "locr" which is defined in the template json file.
                DLRResult[] results = dlr.recognizeByFile(imagePath,"locr");
                long endTime = System.currentTimeMillis();
                long spentTime = endTime-startTime;
                
                if (results != null && results.length > 0){
                    System.out.format("Recognize %d line(s). Spent %d ms.\n", results[0].lineResults.length, spentTime);
                    if (results[0].lineResults.length == 2){
                        String MRZCode_1 = results[0].lineResults[0].text;
                        String MRZCode_2 = results[0].lineResults[1].text;
                        String regex_1 = "P[A-Z<](?<nationality>[A-Z<]{3})(?<name>[A-Z<]{39})";
                        String regex_2 = "(?<number>[A-Z0-9<]{9})[0-9](?<nationality>[A-Z<]{3})(?<birth>[0-9]{2}[0-1][0-9][0-3][0-9])[0-9](?<sex>[MF>])(?<expiry>[0-9]{2}[0-1][0-9][0-3][0-9])[0-9][A-Z0-9<]{14}[0-9<][0-9]";
                        String nationality = null;
                        String name = null;
                        String number = null;
                        String birth = null;
                        String sex = null;
                        String expiry = null;
                        String passport = "";
                        if (MRZCode_1.matches(regex_1) && MRZCode_2.matches(regex_2)){
                        	
                            // 5. Output the raw text of MRZ.
                            passport += ("\tMRZCode1: "+ results[0].lineResults[0].text + "\n");
                            passport += ("\tMRZCode2: "+ results[0].lineResults[1].text + "\n");
                            
                            // 6. Parse the raw text of MRZ and output passport info.
                            passport += "Passport: \n";
                            Pattern pattern_1 = Pattern.compile(regex_1);
                            Matcher matcher_1 = pattern_1.matcher(MRZCode_1);
                            Pattern pattern_2 = Pattern.compile(regex_2);
                            Matcher matcher_2 = pattern_2.matcher(MRZCode_2);
                            if(matcher_1.find()){
                                nationality = matcher_1.group("nationality");
                                name = matcher_1.group("name");
                                
                                String[] nameArr = name.split("<<");
                                if(nameArr.length == 1)
                                	name = nameArr[0].replaceAll("<"," ");
                                else if(nameArr.length >= 2){
	                                if (nationality.equals("CHN")){
	                                    name = nameArr[0].replaceAll("<"," ")+" "+nameArr[1].replaceAll("<"," ");
	                                }else {
	                                    name = nameArr[1].replaceAll("<"," ")+" "+nameArr[0].replaceAll("<"," ");
	                                }
                                }
                            }
                            if(matcher_2.find()){
                                number = matcher_2.group("number").replaceAll("<","");
                                String nationalityVerify = matcher_2.group("nationality");
                                if (!nationalityVerify.equals(nationality)){
                                    nationality = nationality + " or " + nationalityVerify;
                                }
                                birth = matcher_2.group("birth");
                                birth = birth.substring(0,2)+"-"+birth.substring(2,4)+"-"+birth.substring(4);
                                if (matcher_2.group("sex").equals("F")){
                                    sex = "Female";
                                }else if (matcher_2.group("sex").equals("M")){
                                    sex = "Male";
                                }else {
                                    sex = "Other";
                                }
                                expiry = matcher_2.group("expiry");
                                expiry = expiry.substring(0,2)+"-"+expiry.substring(2,4)+"-"+expiry.substring(4);
                            }
                            passport += "\tFULL NAME: " + name + "\n";
                            passport += "\tSEX: " + sex + "\n";
                            passport += "\tDATE OF BIRTH: " + birth + "\n";
                            passport += "\tNATIONALITY: " + nationality + "\n";
                            passport += "\tPASSPORT NUMBER: " + number + "\n";
                            passport += "\tDATE OF EXPIRY: " + expiry;
                            System.out.println(passport);
                        }else {
                            String content = "No passport result or result is not correct.\n";
                            for (int i = 0; i < results.length; i++) {
                                for (int k = 0; k < results[i].lineResults.length; k++) {
                                    content += ("line "+(k+1) + " : " + "\n");
                                    content += ("\ttext: " + results[i].lineResults[k].text + "\n");
                                }
                            }
                            System.out.println(content);
                        }
                    }else {
                        String content = "No passport result or result is not correct.\n";
                        for (int i = 0; i < results.length; i++) {
                            for (int k = 0; k < results[i].lineResults.length; k++) {
                                content += ("line "+(k+1) + " : " + "\n");
                                content += ("\ttext: " + results[i].lineResults[k].text + "\n");
                            }
                        }
                        System.out.println(content);
                    }
                }else {
                    System.out.format("No result. Spent %d ms.\n", spentTime);
                }
            } catch (LabelRecognizerException e) {
                e.printStackTrace();
            }
        }
    }
}
