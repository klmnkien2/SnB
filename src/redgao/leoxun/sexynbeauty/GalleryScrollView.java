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
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("WrongCall")
public class GalleryScrollView extends ViewGroup implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {

    protected int leftTotalHeight, rightTotalHeight, itemWidth, padding, scroll = 0;
    protected float lastDelta = 0, scale_dip = 0;
    protected Handler handler = new Handler();

    protected int lastX = -1, lastY = -1;
    protected boolean touching = false, reachEnd = false, enabled = true;
    
    protected Context context;
    
    private OnItemClickListener onItemClickListener;
    
    public GalleryScrollView (Context context, AttributeSet attrs) {
        super(context, attrs);
        setListeners();
        this.context = context;
        
        handler.removeCallbacks(updateTask);
        handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
        setChildrenDrawingOrderEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        scale_dip = metrics.density;
        padding = 10;
        rightTotalHeight = leftTotalHeight = padding;
        itemWidth = (metrics.widthPixels - 3* padding) / 2;
    }

    protected void setListeners()
    {
        setOnTouchListener(this);
        super.setOnClickListener(this);
        setOnLongClickListener(this);
    }

    protected Runnable updateTask = new Runnable() {
        @SuppressLint("WrongCall")
        public void run()
        {
            if (lastDelta != 0 && !touching)
            {
                scroll += lastDelta;
                lastDelta *= .8;
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
        
        if (child.getTag() instanceof Integer) {
            if (leftTotalHeight <= rightTotalHeight) {                    
                leftTotalHeight += ((Integer)child.getTag() + padding);
            } else {
                rightTotalHeight += ((Integer)child.getTag() + padding); 
            }        
        }
    };
    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
    };
    
    //LAYOUT
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int leftColumnHeight = padding, rightColumnHeight = padding;
        
        for (int i = 1; i < getChildCount(); i++) {
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
        
        if(reachEnd) {
            ProgressBar progressBar = (ProgressBar)getChildAt(0);
            int x = (itemWidth * 2 + padding * 3 - progressBarSize()) / 2;
            int y = Math.max(leftTotalHeight, rightTotalHeight) - scroll;
            progressBar.layout(x, y, x + progressBarSize(), y + progressBarSize());
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {        
        int cellWidthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.AT_MOST);
        int cellHeightSpec;

        int count = getChildCount();
        for (int index = 0; index < count; index ++) {
            final View child = getChildAt(index);
            if (child.getTag() instanceof Integer) {
                cellHeightSpec = MeasureSpec.makeMeasureSpec((Integer)child.getTag(), MeasureSpec.AT_MOST);
                child.measure(cellWidthSpec, cellHeightSpec);
            }             
        }        
        
        ProgressBar progressBar = (ProgressBar)getChildAt(0);
        progressBar.measure(MeasureSpec.makeMeasureSpec(progressBarSize(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(progressBarSize(), MeasureSpec.AT_MOST));
        
        // Use the size our parents gave us, but default to a minimum size to avoid
        int width = itemWidth * 2 + padding * 3;
        int height = Math.max(leftTotalHeight, rightTotalHeight) + progressBarSize();
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
                   enabled = true;
                   lastX = (int) event.getX();
                   lastY = (int) event.getY();
                   touching = true;
                   break;
               case MotionEvent.ACTION_MOVE:
                   int delta = lastY - (int)event.getY();
             
                   scroll += delta;
                   if (Math.abs(delta) > 2)
                       enabled = false;
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
            if(!reachEnd) {
                startLoadingMore();
            }
            if (scroll <= max + stretch)
                scroll = max;
            else if (!touching)
                scroll += (max - scroll) / stretch;
        }
    }
    
    protected int getMaxScroll()
    {
        return Math.max(leftTotalHeight, rightTotalHeight) + progressBarSize() - getHeight();
    }
    
    protected int progressBarSize() {
        float valueDips = 32;
        return (int)(valueDips * scale_dip + 0.5f);
    }
    
    public void showProgressBar() {
        ProgressBar progressBar = (ProgressBar)getChildAt(0);
        progressBar.setVisibility(VISIBLE);
        reachEnd = true;
    }
    
    public void startLoadingMore() {
        ((GalleryActivity)context).getCurrentGallery().loadMoreThumbs();
    }
    
    public void endLoadingMore() {
        ProgressBar progressBar = (ProgressBar)getChildAt(0);
        progressBar.setVisibility(GONE);
        reachEnd = false;
    }

    @Override
    public void onClick(View v) {
        if (enabled)
        {
            if (onItemClickListener != null && getLastIndex() != -1) {
                onItemClickListener.onItemClick(null, getChildAt(getLastIndex()), getLastIndex(), getLastIndex() / 2);
            }
        }
    }
    
    public int getLastIndex() {
        int lastIndex = -1;
        int leftColumnHeight = padding, rightColumnHeight = padding;
        
        for (int i = 1; i < getChildCount(); i++) {
            if (getChildAt(i).getTag() instanceof Integer) {
                
                if (leftColumnHeight <= rightColumnHeight) {  
                    leftColumnHeight += ((Integer)getChildAt(i).getTag() + padding);
                    if(leftColumnHeight - scroll > lastY && lastX >= padding && lastX <= padding + itemWidth) {
                        lastIndex = i - 1;
                        break;
                    }
                } else {
                    rightColumnHeight += ((Integer)getChildAt(i).getTag() + padding);
                    if(rightColumnHeight - scroll > lastY && lastX >= itemWidth + padding * 2 && lastX <= padding * 2 + itemWidth * 2) {
                        lastIndex = i - 1;
                        break;
                    }
                }
            } 
        }
        return lastIndex;
    }
    
    public void setOnItemClickListener(OnItemClickListener l)
    {
        this.onItemClickListener = l;
    }
}
