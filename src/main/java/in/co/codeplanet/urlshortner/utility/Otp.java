package in.co.codeplanet.urlshortner.utility;

import java.util.Random;

public class Otp {
    public static String generateOtp(int noOfDigits)
    {
        StringBuffer sb=new StringBuffer();
        Random r=new Random();
        for(int i=1;i<=noOfDigits;i++) {
            int n=r.nextInt(0,10);
            sb.append(n);
        }
        return sb.toString();
    }

}
