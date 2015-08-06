package common;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by swli on 8/6/2015.
 */
public class YaoDragListener implements View.OnDragListener {

    public interface OnDropInteraction
    {
        void OnDrop(View containerView, int positon);
    }

    private OnDropInteraction onDropInteraction;

    public void setOnDropInteraction(OnDropInteraction onDropInteraction)
    {
        this.onDropInteraction = onDropInteraction;
    }

    private int position;

    public YaoDragListener(int position)
    {
        this.position = position;
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        //int action = dragEvent.getAction();
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                //view.setBackground(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                //view.setBackgroundColor(Color.WHITE);
                //v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View viewTemp = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                //owner.removeView(view);

                if(onDropInteraction !=null)
                    onDropInteraction.OnDrop(view, position);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                //v.setBackgroundDrawable(normalShape);
            default:
                break;
        }
        return true;
    }
}
