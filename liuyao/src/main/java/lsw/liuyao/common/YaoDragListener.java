package lsw.liuyao.common;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import lsw.liuyao.R;

/**
 * Created by swli on 8/6/2015.
 */
public class YaoDragListener implements View.OnDragListener {

    public interface OnDropInteraction
    {
        void OnDrop(View containerView, int position);
        void OnEntered(View containerView, int position);
    }

    private OnDropInteraction onDropInteraction;

    public void setOnDropInteraction(OnDropInteraction onDropInteraction)
    {
        this.onDropInteraction = onDropInteraction;
    }

    private int position;
    private int childCount;
    private boolean hasYao;
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
                LinearLayout container = (LinearLayout) view;
                childCount = container.getChildCount();
                if(childCount > 0 && (container.getChildAt(0) instanceof ImageView))
                    hasYao = true;
                else
                    hasYao = false;
                view.setBackgroundResource(R.drawable.yao_container_hover);

                break;
            case DragEvent.ACTION_DRAG_EXITED:
                //view.setBackgroundColor(Color.WHITE);
                if(hasYao)
                    view.setBackgroundResource(0);
                else
                    view.setBackgroundResource(R.drawable.yao_container);
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                //View viewTemp = (View) dragEvent.getLocalState();
                //ViewGroup owner = (ViewGroup) view.getParent();
                //owner.removeView(view);
                view.setBackgroundResource(0);
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
