package lsw.liuyao.model;

/**
 * Created by swli on 3/31/2016.
 */
public class HexagramLineNote {

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private String noteType;
    private String originalNote;

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getOriginalNote() {
        return originalNote;
    }

    public void setOriginalNote(String originalNote) {
        this.originalNote = originalNote;
    }

    public String getDecoratedNote() {
        return decoratedNote;
    }

    public void setDecoratedNote(String decoratedNote) {
        this.decoratedNote = decoratedNote;
    }

    private String decoratedNote;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
