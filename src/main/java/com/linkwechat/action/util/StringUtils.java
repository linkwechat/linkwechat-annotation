package com.linkwechat.action.util;

/**
 * 字符串处理工具类
 * 
 * @author linkwechat linkwechat@foxmail.com
 * @version 1.0
 */
public class StringUtils {

    /**
     * 检查字符串是否为null或空字符串""，如果为空, 则返回true。
     * 
     * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * @return boolean
     */
    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 检查字符串是否不为null或空字符串""，如果不为空, 则返回true。
     * 
     * @param str
     *            要检查的字符串
     * @return boolean
     */
    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }

    /**
     * 检查字符串是否是空白：null、空字符串""或只有空白字符，如果为空白，则返回true。
     * 
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * @return boolean
     */
    public static boolean isBlank(final String str) {
        int length;

        if (str == null || (length = str.length()) == 0) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查字符串是否不是空白：null、空字符串""或只有空白字符，如果不为空白，则返回true。
     * 
     * @param str
     *            要检查的字符串
     * @return boolean
     */
    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    /**
     * 空格字符填充
     * 
     * @param length
     *            需要填充的长度
     * @return String
     */
    public static String fillBlankChar(int length) {
        return fillChar(' ', length);
    }

    /**
     * 字符填充
     * 
     * @param c
     *            需要填充的字符
     * @param length
     *            需要填充的长度
     * @return String
     */
    public static String fillChar(char c, int length) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < length; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /***
     * 
     * 十六进制字符解码成数值
     * 
     * @param c
     *            十六进制字符
     * @return 十六进制字符表示的数值
     */
    private static int HexDecode(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    /***
     * 
     * 十六进制数值编码成字符
     * 
     * @param i
     *            十六进制数值
     * @return 十六进制数值表示的字符
     */
    private static char HexEncode(int i) {
        i = i & 0x0f;
        if (i >= 10)
            return (char) (i + 'A' - 10);
        return (char) (i + '0');
    }

    /***
     * 
     * 从十六进制字符串到字节数组转换
     * 
     * @param hexStr
     *            十六进制字符串
     * @return 字节数组
     */
    public static byte[] HexStringToBytes(String hexStr) {
        byte[] b = new byte[hexStr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte) ((HexDecode(c0) << 4) | HexDecode(c1));
        }
        return b;
    }

    /***
     * 
     * 从字节数组到十六进制字符串转换
     * 
     * @param data
     *            字节数组
     * @return 十六进制字符串
     */
    public static String BytesToHexString(byte[] data) {
        char[] a = new char[data.length * 2];
        int j = 0;
        for (int i = 0; i < data.length; i++) {
            a[j++] = HexEncode(data[i] >> 4 & 0x0f);
            a[j++] = HexEncode(data[i] & 0x0f);
        }
        return new String(a);
    }
}
