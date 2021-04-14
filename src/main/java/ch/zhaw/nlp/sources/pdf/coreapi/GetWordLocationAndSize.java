package ch.zhaw.nlp.sources.pdf.coreapi;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;

public class GetWordLocationAndSize extends PDFTextStripper {

    private Map<Double,List<String>> heights = new HashMap<>();

    public Map<Double, List<String>> getHeights() {
        return heights;
    }

    public void setHeights(Map<Double, List<String>> heights) {
        this.heights = heights;
    }

    public GetWordLocationAndSize() throws IOException {

    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        String wordSeparator = getWordSeparator();
        List<TextPosition> word = new ArrayList<>();
        for (TextPosition text : textPositions) {
            String thisChar = text.getUnicode();
            if (thisChar != null) {
                if (thisChar.length() >= 1) {
                    if (!thisChar.equals(wordSeparator)) {
                        word.add(text);
                    } else if (!word.isEmpty()) {
                        saveHeight(word);
                        word.clear();
                    }
                }
            }
        }
        if (!word.isEmpty()) {
            saveHeight(word);
            word.clear();
        }
    }

    void saveHeight(List<TextPosition> word) {
        Rectangle2D boundingBox = null;
        StringBuilder builder = new StringBuilder();
        for (TextPosition text : word) {
            Rectangle2D box = new Rectangle2D.Float(text.getXDirAdj(), text.getYDirAdj(), text.getWidthDirAdj(), text.getHeightDir());
            if (boundingBox == null)
                boundingBox = box;
            else
                boundingBox.add(box);
            builder.append(text.getUnicode());
        }
        if (heights.containsKey(boundingBox.getHeight())){
            List<String> newWords = heights.get(boundingBox.getHeight());
            newWords.add(builder.toString());
        }else {
            List<String> newList = new ArrayList<>();
            newList.add(builder.toString());
            heights.put(boundingBox.getHeight(),newList);

        }


    }
}