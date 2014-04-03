package redgao.leoxun.sexynbeauty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

@SuppressLint("WrongCall")
public class GalleryScrollView extends ViewGroup implements View.OnTouchListener, View.OnLongClickListener {
    //layout vars
    public static float childRatio = .9f;
    protected int viewGroupHeight = 0, leftColumnHeight, rightColumnHeight, itemWidth, padding, dpi, scroll = 0;
    protected float lastDelta = 0;
    protected Handler handler = new Handler();
    //dragging vars
    protected int lastX = -1, lastY = -1;
    protected boolean touching = false;
    
    //CONSTRUCTOR AND HELPERS
    public GalleryScrollView (Context context, AttributeSet attrs) {
        super(context, attrs);
        setListeners();
        handler.removeCallbacks(updateTask);
        handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
        setChildrenDrawingOrderEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        dpi = metrics.densityDpi;
        padding = 10;
        itemWidth = (metrics.widthPixels - 3* padding) / 2;
    }

    protected void setListeners()
    {
        setOnTouchListener(this);
        setOnLongClickListener(this);
    }

    protected Runnable updateTask = new Runnable() {
        @SuppressLint("WrongCall")
        public void run()
        {
            if(touching) {
                if (lastY < padding * 2 && scroll > 0)
                    scroll -= 20;
                else if (lastY > getBottom() - getTop() - (padding * 2) && scroll < getMaxScroll())
                    scroll += 20;
            }
            else if (lastDelta != 0 && !touching)
            {
                scroll += lastDelta;
                lastDelta *= .9;
                if (Math.abs(lastDelta) < .25)
                    lastDelta = 0;
            }
            clampScroll();
            onLayout(true, getLeft(), getTop(), getRight(), getBottom());
        
            handler.postDelayed(this, 25);
        }
    };
    
    //OVERRIDES
    @Override
    public void addView(View child) {
        super.addView(child);
        if(isExtraView(child)) return;
    };
    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        if(isExtraView(getChildAt(index))) return;
    };
    
    //LAYOUT
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        leftColumnHeight = rightColumnHeight = padding;
        
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getTag() instanceof String) {
                
            } else if (getChildAt(i).getTag() instanceof Integer) {
                
                if (leftColumnHeight <= rightColumnHeight) {                    
                    int x = padding;
                    int y = leftColumnHeight - scroll;
                    RelativeLayout item = (RelativeLayout)getChildAt(i);
                    item.layout(x, y, x + itemWidth, y + (Integer)getChildAt(i).getTag());
                    
                    leftColumnHeight += ((Integer)getChildAt(i).getTag() + padding);
                } else {
                    int x = padding * 2 + itemWidth;
                    int y = rightColumnHeight - scroll;
                    RelativeLayout item = (RelativeLayout)getChildAt(i);
                    item.layout(x, y, x + itemWidth, y + (Integer)getChildAt(i).getTag());
                    
                    rightColumnHeight += ((Integer)getChildAt(i).getTag() + padding); 
                }
            } 
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {        
        int cellWidthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.AT_MOST);
        int cellHeightSpec, leftTotal = padding, rightTotal = padding;

        int count = getChildCount();
        for (int index = 0; index < count; index ++) {
            final View child = getChildAt(index);
            if (child.getTag() instanceof Integer) {
                cellHeightSpec = MeasureSpec.makeMeasureSpec((Integer)child.getTag(), MeasureSpec.AT_MOST);
                child.measure(cellWidthSpec, cellHeightSpec);
                
                if (leftTotal <= rightTotal) {                    
                    leftTotal += ((Integer)child.getTag() + padding);
                } else {
                    rightTotal += ((Integer)child.getTag() + padding); 
                }
            } 
            
        }

        // Use the size our parents gave us, but default to a minimum size to avoid
        int width = itemWidth * 2 + padding * 3;
        int height = Math.max(leftTotal, rightTotal);
        if(viewGroupHeight==0) viewGroupHeight = height;
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),  resolveSize(height, heightMeasureSpec));
    }
 
    public boolean onLongClick(View view)
    {
        return false;
    }
    
    public boolean onTouch(View view, MotionEvent event)
    {
        int action = event.getAction();
           switch (action & MotionEvent.ACTION_MASK) {
               case MotionEvent.ACTION_DOWN:
                   lastX = (int) event.getX();
                   lastY = (int) event.getY();
                   touching = true;
                   break;
               case MotionEvent.ACTION_MOVE:
                   int delta = lastY - (int)event.getY();
             
                   scroll += delta;
                   clampScroll();
                   onLayout(true, getLeft(), getTop(), getRight(), getBottom());
                 
                   lastX = (int) event.getX();
                   lastY = (int) event.getY();
                   lastDelta = delta;
                   break;
               case MotionEvent.ACTION_UP:                  
                   touching = false;
                   break;
           }
        
        return true;
    }
    
    private boolean isExtraView(View view) {
        if (view.getTag() instanceof String) {
            if (view.getTag().equals("add_button") || view.getTag().equals("remove_button")) {
                return true;
            }
        }
        return false;
    }    
    
    public void scrollToTop()
    {
        scroll = 0;
    }
    public void scrollToBottom()
    {
        scroll = Integer.MAX_VALUE;
        clampScroll();
    }
    protected void clampScroll()
    {
        int stretch = 3, overreach = getHeight() / 2;
        int max = getMaxScroll();
        max = Math.max(max, 0);
        
        if (scroll < -overreach)
        {
            scroll = -overreach;
            lastDelta = 0;
        }
        else if (scroll > max + overreach)
        {
            scroll = max + overreach;
            lastDelta = 0;
        }
        else if (scroll < 0)
        {
            if (scroll >= -stretch)
                scroll = 0;
            else if (!touching)
                scroll -= scroll / stretch;
        }
        else if (scroll > max)
        {
            if (scroll <= max + stretch)
                scroll = max;
            else if (!touching)
                scroll += (max - scroll) / stretch;
        }
    }
    protected int getMaxScroll()
    {
        return viewGroupHeight;
    }
}
