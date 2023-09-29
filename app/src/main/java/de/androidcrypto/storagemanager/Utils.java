package de.androidcrypto.storagemanager;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {
    public static void main(String[] args) {

    }


    public static String getShortTimestamp() {
        // gives a 10 characters long string 2023-09-29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ZonedDateTime
                    .now(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        } else {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
    }




/* char[] to byte[]
// https://stackoverflow.com/a/9670279/8166854
    Convert without creating String object:

            import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

    byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }
    Usage:

    char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    byte[] bytes = toBytes(chars);
    // do something with chars/bytes
Arrays.fill(chars, '\u0000'); // clear sensitive data
Arrays.fill(bytes, (byte) 0); // clear sensitive data
 */
}
