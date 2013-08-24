package teksperanto.javafx;

import javafx.scene.control.TextArea;

/**
 *
 * @author Nicholas Quirk
 */
class EsperantoSubstitutor extends Thread {

    TextArea textArea;
    Long lastKeyPressTime;

    public EsperantoSubstitutor(TextArea textArea, Long lastKeyPressTime) {
        this.textArea = textArea;
        this.lastKeyPressTime = lastKeyPressTime;
    }

    public void run() {

        while (true) {

            if ((System.currentTimeMillis() - lastKeyPressTime) > 2000) {

                int lastCaretPosition = textArea.getCaretPosition();

                textArea.setText(translateCharacters(textArea.getText()));

                if (textArea.getText().length() < lastCaretPosition) {
                    textArea.positionCaret(textArea.getText().length());
                } else {
                    textArea.positionCaret(lastCaretPosition);
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String translateCharacters(String str) {
        return str.replaceAll("Cx", "\u0108")
                .replaceAll("cx", "\u0109").replaceAll("Gx", "\u011C")
                .replaceAll("gx", "\u011D").replaceAll("Hx", "\u0124")
                .replaceAll("hx", "\u0125").replaceAll("Jx", "\u0134")
                .replaceAll("jx", "\u0135").replaceAll("Sx", "\u015C")
                .replaceAll("sx", "\u015D").replaceAll("Ux", "\u016C")
                .replaceAll("ux", "\u016D");
    }
}
