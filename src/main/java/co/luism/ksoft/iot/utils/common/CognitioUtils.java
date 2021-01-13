package co.luism.ksoft.iot.utils.common;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by luis on 15.10.14.
 */
public class CognitioUtils {

    public static String getMD5Hash(String data){

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        //byte[] bytesOfMessage = tempStr.getBytes("UTF-8"); // Maybe you're not using a charset here
        //MessageDigest md5 = MessageDigest.getInstance("MD5");
        //byte[] theDigest = md5.digest(bytesOfMessage);
        byte[] bytesOfMessage;
        try {
            bytesOfMessage = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        //md5.update(data.getBytes(), 0, data.length());
        if (md5 != null) {
            md5.update(bytesOfMessage);
            byte[] digest = md5.digest();
            return byteToHexString(digest);
        }

        return null;
    }

    public static String byteToHexString(byte [] hash){

        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0"
                        + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }

        return hexString.toString().toUpperCase();
    }





    public static String byteToBase64(byte[] b){

        byte[] c = Base64.encodeBase64(b);

        return new String(c);

    }

    public static byte[] base64ToByte(String b){

        return  Base64.decodeBase64(b.getBytes());

    }

    public static boolean compareFiles(File file1, File file2) throws IOException, ParserConfigurationException, SAXException {

        long diff = 0;
         if(file1.length() > file2.length()){
             diff = file1.length() - file2.length();
         } else {
             diff = file2.length() - file1.length();
         }

        if(diff > 5000){
            return false;
        }

        return true;
    }

    /**
     * Byte Array ab einem gegebenen Offset in einen long wandeln.
     * (4 Byte)
     *
     * @param b The byte array
     * @param offset Der array offset
     * @param iByteOrder Anordnung der Bytes (BIG oder LITTLE Endian)
     * @return
     *      Long mit dem entsprechenden Wert
     */
    public static long byteArrayToInt4B(byte[] b, int offset, int iByteOrder)
    {
        long value = 0;
        for(int i = 0; i < 4; i++){
            int shift;
            if(iByteOrder == 1){
                shift = (4 - 1 - i) * 8;
            }else{
                shift = i * 8;

            }
            value += (b[i + offset] & 0x000000FF) << shift;
        }

        return value;
    }

    /**
     * Byte Array ab einem gegebenen Offset in einen long wandeln.
     * (2 Byte)
     *
     * @param b The byte array
     * @param offset Der array offset
     * @param iByteOrder Anordnung der Bytes (BIG oder LITTLE Endian)
     * @return
     *      Integer mit dem entsprechenden Wert
     */
    public static int byteArrayToInt2B(byte[] b, int offset, int iByteOrder)
    {
        int value = 0;
        for(int i = 0; i < 2; i++){
            int shift;
            if (iByteOrder == 1) {
                shift = (2 - 1 - i) * 8;
            } else {
                shift = i * 8;
            }
            value += (b[i + offset] & 0x000000FF) << shift;
        }

        return value;
    }
}
