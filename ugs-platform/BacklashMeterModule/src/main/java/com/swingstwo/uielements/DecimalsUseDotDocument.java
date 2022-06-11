package com.swingstwo.uielements;

import javax.swing.text.PlainDocument;
import javax.swing.text.*;
import java.awt.Toolkit;
import java.text.*;
import java.util.Locale;

public class DecimalsUseDotDocument extends PlainDocument {

    private Format format;

    public DecimalsUseDotDocument(Format f) {
        format = f;
    }

    public Format getFormat() {
        return format;
    }

    public String recodeDecimalPointSymbol(String src) {
        String sRet = src;
        try {
            byte[] bytes = src.getBytes("Cp1251");
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == (byte) 44) // Comma
                    bytes[i] = (byte) 46; // Replace with dot
            }
            ;
            sRet = new String(bytes, "Cp1251");
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
        }
        return sRet;
    }

    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {

        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(offs, currentText.length());
        String proposedResult;

        try {
            String sBaseName = "";
            Class<?> cls = format.getClass().getSuperclass();
            if (cls != null)
                sBaseName = cls.getSimpleName();
            if (sBaseName.compareToIgnoreCase("NumberFormat") == 0) {
                DecimalFormat dec = new DecimalFormat("#####.##", new DecimalFormatSymbols(Locale.US));
                str = recodeDecimalPointSymbol(str);
                proposedResult = beforeOffset + str + afterOffset;
                dec.parse(proposedResult);
                //BigDecimal bd = BigDecimal.
                Double d = Double.valueOf(proposedResult);
            } else {
                proposedResult = beforeOffset + str + afterOffset;
                format.parseObject(proposedResult);
            }
            super.insertString(offs, str, a);
            //} catch (ParseException e) {
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
            //System.err.println("insertString: could not parse: " + proposedResult);
        }
    }

    public void remove(int offs, int len) throws BadLocationException {
        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(len + offs, currentText.length());
        String proposedResult = beforeOffset + afterOffset;

        try {
            if (proposedResult.length() != 0)
                format.parseObject(proposedResult);
            super.remove(offs, len);
            //} catch (ParseException e) {
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
            //System.err.println("remove: could not parse: " + proposedResult);
        }
    }
}
