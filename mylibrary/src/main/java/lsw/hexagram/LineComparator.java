package lsw.hexagram;

import java.util.Comparator;

import lsw.model.Line;

/**
 * Created by swli on 8/11/2015.
 */
public class LineComparator implements Comparator<Line> {
    @Override
    public int compare(Line line, Line t1) {
        return line.getPosition() > t1.getPosition() ? -1 : 1;
    }
}
