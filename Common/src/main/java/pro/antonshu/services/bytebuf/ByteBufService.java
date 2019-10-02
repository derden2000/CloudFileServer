package pro.antonshu.services.bytebuf;

public class ByteBufService {
    /*
     * This class needs to create structural type of data for ByteBuf transmitting.
     *
     * Each command must have 3 required fields and 1 optional field.
     *
     * Required:
     * 1. Command marker (16 bytes);
     * 2. Type of command (16 bytes);
     * 3. The owner of the command (16 bytes);
     *
     * Optional:
     * 4. Array of data (976 bytes);
     */
    public static byte[] prepareSendData(String comType, String user, byte[] data) {
        byte[] total = new byte[1024];
        System.arraycopy("marker".getBytes(), 0, total, 0, "marker".getBytes().length);
        System.arraycopy(comType.getBytes(), 0, total, 16, comType.getBytes().length);
        System.arraycopy(user.getBytes(), 0, total, 32, user.getBytes().length);
        if (data != null) {
            System.arraycopy(data, 0, total, 48, data.length);
        }
        return total;
    }
}
