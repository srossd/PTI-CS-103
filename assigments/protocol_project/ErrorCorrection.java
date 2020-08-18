import java.io.UnsupportedEncodingException;

public class ErrorCorrection {

    private static final int[] codewords = createCodewords( "100000000000110111000101",
                                                            "010000000000101110001011",
                                                            "001000000000011100010111",
                                                            "000100000000111000101101",
                                                            "000010000000110001011011",
                                                            "000001000000100010110111",
                                                            "000000100000000101101111",
                                                            "000000010000001011011101",
                                                            "000000001000010110111001",
                                                            "000000000100101101110001",
                                                            "000000000010011011100011",
                                                            "000000000001111111111110");

    private static final int[] parity = createCodewords("110111000101100000000000",
                                                        "101110001011010000000000",
                                                        "011100010111001000000000",
                                                        "111000101101000100000000",
                                                        "110001011011000010000000",
                                                        "100010110111000001000000",
                                                        "000101101111000000100000",
                                                        "001011011101000000010000",
                                                        "010110111001000000001000",
                                                        "101101110001000000000100",
                                                        "011011100011000000000010",
                                                        "111111111110000000000001");

    private static int[] createCodewords(String... rows) {
        int[] ans = new int[rows.length];
        for(int i = 0; i < ans.length; i++) {
            int x = 0;
            for(int j = 0; j < rows[i].length(); j++) {
                if(rows[i].charAt(j) == '1')
                    x += 1<<(rows[i].length()-j-1);
            }
            ans[i] = x;
        }
        return ans;
    }

    private static int weight(int twelve) {
        int ans = 0;
        for(int i = 0; i < 24; i++)
            ans += (twelve >> i) % 2;
        return ans;
    }

    private static int[] toTwentyFourBit(byte[] bytes) {
        int[] ans = new int[(bytes.length+2)/3];
        int j = 0;
        for(int i = 0; i < ans.length; i++) {
            ans[i] = (bytes[j] & 0xff) << 16 | ((j+1 < bytes.length ? bytes[j+1] : 0) & 0xff) << 8 | ((j+2 < bytes.length ? bytes[j+2] : 0) & 0xff);
            j += 3;
        }
        return ans;
    }

    private static int[] toTwelveBit(byte[] bytes) {
        int[] ans = new int[2*((bytes.length+2)/3)];
        int j = 0;
        for(int i = 0; i < bytes.length; i+= 3) {
            ans[j] = ((bytes[i] & 0xff) << 4) | (((i+1 < bytes.length ? bytes[i+1] : 0) & 0xff) >> 4);
            ans[j+1] = (((i+1 < bytes.length ? bytes[i+1] : 0) & 0x0f) << 8) | ((i+2 < bytes.length ? bytes[i+2] : 0) & 0xff);
            j += 2;
        }
        return ans;
    }

    private static byte[] twentyfourToEightBit(int[] twentyfours) {
        byte[] ans = new byte[3*twentyfours.length];
        int j = 0;
        for(int i = 0; i < twentyfours.length; i++) {
            ans[j] = (byte)(twentyfours[i] >> 16);
            ans[j+1] = (byte)((twentyfours[i] >> 8) & 0xff);
            ans[j+2] = (byte)(twentyfours[i] & 0xff);
            j += 3;
        }
        return ans;
    }

    private static byte[] twelveToEightBit(int[] twelves) {
        byte[] ans = new byte[3*twelves.length/2];
        int j = 0;
        for(int i = 0; i < twelves.length; i += 2) {
            ans[j] = (byte)(twelves[i] >> 4);
            ans[j+1] = (byte)(((twelves[i] & 0x0f) << 4) | ((i+1 < twelves.length ? twelves[i+1] : 0) >> 8));
            ans[j+2] = (byte)((i+1 < twelves.length ? twelves[i+1] : 0) & 0xff);
            j += 3;
        }
        return ans;
    }

    private static int[] encode(int[] twelves) {
        int[] ans = new int[twelves.length];
        for(int i = 0; i < ans.length; i++) {
            for(int j = 0; j < 12; j++) {
                if((twelves[i] >> (11-j)) % 2 == 1)
                    ans[i] ^= codewords[j];
            }
        }
        return ans;
    }

    public static String encode(String s) {
        byte[] sbytes;
        try {
            sbytes = s.getBytes("ISO-8859-1");
            int[] twelves = toTwelveBit(sbytes);
            int[] coded = encode(twelves);
            byte[] codebytes = twentyfourToEightBit(coded);
            return new String(codebytes, "ISO-8859-1");
        }
        catch(UnsupportedEncodingException e) {
            System.out.println("ISO-8859-1 encoding failed");
        }

        return "";
    }

    private static int[] decode(int[] twentyfours) {
        int[] ans = new int[twentyfours.length];
        main: for(int i = 0; i < ans.length; i++) {
            int r = twentyfours[i] >> 12;
            int syndrome = 0;
            for(int j = 0; j < 12; j++) {
                int bit = weight(parity[j] & twentyfours[i]) % 2;
                syndrome |= bit << (11-j);
            }

            if(weight(syndrome) <= 3) {
                ans[i] = r;
                continue main;
            }

            for(int j = 0; j < 12; j++) {
                int test = syndrome ^ (parity[j] >> 12);
                if(weight(test) <= 2) {
                    ans[i] = r ^ (1 << (11-j));
                    continue main;
                }
            }

            int q = 0;
            for(int j = 0; j < 12; j++) {
                int bit = weight(codewords[j] & syndrome) % 2;
                q |= bit << (11-j);
            }

            if(weight(q) <= 3) {
                ans[i] = r ^ q;
                continue;
            }

            for(int j = 0; j < 12; j++) {
                int test = q ^ (parity[j] >> 12);
                if(weight(test) <= 2) {
                    ans[i] = r ^ test;
                    continue main;
                }
            }
            
            //uncorrectable
            ans[i] = r;
        }
        return ans;
    }

    public static String decode(String s) {
        byte[] sbytes;
        try {
            sbytes = s.getBytes("ISO-8859-1");
            int[] twentyfours = toTwentyFourBit(sbytes);
            int[] decoded = decode(twentyfours);
            byte[] decodedbytes = twelveToEightBit(decoded);
            return new String(decodedbytes, "ISO-8859-1");
        }
        catch(UnsupportedEncodingException e) {
            System.out.println("ISO-8859-1 encoding failed");
        }

        return "";
    }
}