package com.agentdid127.resourcepack.library.utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesEx extends Properties {
    private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) 
            bufLen = 2147483647;

        StringBuilder outBuffer = new StringBuilder(bufLen);

        for (int x = 0; x < len; ++x) {
            char aChar = theString.charAt(x);
            if (aChar > '=' && aChar < 127) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                } else 
                    outBuffer.append(aChar);
            } else {
                switch (aChar) {
                    case '\t':
                        outBuffer.append('\\');
                        outBuffer.append('t');
                        continue;
                    case '\n':
                        outBuffer.append('\\');
                        outBuffer.append('n');
                        continue;
                    case '\f':
                        outBuffer.append('\\');
                        outBuffer.append('f');
                        continue;
                    case '\r':
                        outBuffer.append('\\');
                        outBuffer.append('r');
                        continue;
                    case ' ':
                        if (x == 0 || escapeSpace) 
                            outBuffer.append('\\');
                        outBuffer.append(' ');
                        continue;
                    case '!':
                    case '#':
                    case '=':
                        outBuffer.append('\\');
                        outBuffer.append(aChar);
                        continue;
                }

                if ((aChar < ' ' || aChar > '~') & escapeUnicode) {
                    outBuffer.append('\\');
                    outBuffer.append('u');
                    outBuffer.append(PropertiesEx.toHex(aChar >> 12 & 15));
                    outBuffer.append(PropertiesEx.toHex(aChar >> 8 & 15));
                    outBuffer.append(PropertiesEx.toHex(aChar >> 4 & 15));
                    outBuffer.append(PropertiesEx.toHex(aChar & 15));
                } else 
                    outBuffer.append(aChar);
            }
        }

        return outBuffer.toString();
    }

    private static void writeComments(BufferedWriter bw, String comments) throws IOException {
        bw.write("#");
        int len = comments.length();
        int current = 0;
        int last = 0;

        for (char[] uu = {'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'}; current < len; ++current) {
            char c = comments.charAt(current);
            if (c > 255 || c == '\n' || c == '\r') {
                if (last != current) 
                    bw.write(comments.substring(last, current));

                if (c > 255) {
                    uu[2] = PropertiesEx.toHex(c >> 12 & 15);
                    uu[3] = PropertiesEx.toHex(c >> 8 & 15);
                    uu[4] = PropertiesEx.toHex(c >> 4 & 15);
                    uu[5] = PropertiesEx.toHex(c & 15);
                    bw.write(new String(uu));
                } else {
                    bw.newLine();
                    if (c == '\r' && current != len - 1 && comments.charAt(current + 1) == '\n') 
                        ++current;
                    if (current == len - 1 || comments.charAt(current + 1) != '#' && comments.charAt(current + 1) != '!') 
                        bw.write("#"); 
                }

                last = current + 1;
            }
        }

        if (last != current) 
            bw.write(comments.substring(last, current));

        bw.newLine();
    }

    public void store(OutputStream out, String comments) throws IOException {
        store1(new BufferedWriter(new OutputStreamWriter(out, "8859_1")), comments, true);
    }

    private void store1(BufferedWriter bw, String comments, boolean escUnicode) throws IOException {
        if (comments != null)
          PropertiesEx.writeComments(bw, comments);

        bw.write("#" + (new Date()));
        bw.newLine();
        synchronized (this) {
            Iterator var5 = entrySet().iterator();

            while (true) {
                if (!var5.hasNext()) 
                    break;

                Map.Entry<Object, Object> e = (Map.Entry) var5.next();
                String key = (String) e.getKey();
                String val = (String) e.getValue();
                //key = this.saveConvert(key, true, escUnicode);
                //val = this.saveConvert(val, false, escUnicode);
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }

        bw.flush();
		bw.close();
    }

    private static char toHex(int nibble) {
        return PropertiesEx.hexDigit[nibble & 15];
    }
}