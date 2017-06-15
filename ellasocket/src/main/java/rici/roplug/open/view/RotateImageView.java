package rici.roplug.open.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
  
/** 
 * A @{code ImageView} which can rotate it's content. 
 */  
public class RotateImageView extends ImageView {  
  
    @SuppressWarnings("unused")  
    private static final String TAG = "RotateImageView";  
  
    private static final int ANIMATION_SPEED = 20; // 270 deg/sec   
  
    private int mCurrentDegree = 0; // [0, 359]   
    private int mStartDegree = 0;  
    private int mTargetDegree = 0;  
  
    private boolean mClockwise = false, mEnableAnimation = true,isStop = true;;  
  
    private long mAnimationStartTime = 0;  
    private long mAnimationEndTime = 0;  
  
    public RotateImageView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public RotateImageView(Context context) {  
        super(context);  
    }  
  
    protected int getDegree() {  
        return mTargetDegree;  
    }    
  
    public void startAnimation(){
    	isStop = false;
    	mStartDegree = mCurrentDegree; 
        mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();  
        mClockwise = true;  
        invalidate();  
    	
    }
    public void stopAnimation(){
    	isStop = true;
        invalidate();  
    	//setOrientation(mCurrentDegree, false);
    }
    public void setOrientation(int degree, boolean animation) {  
        mEnableAnimation = animation;  
        // make sure in the range of [0, 359]   
        //degree = degree >= 88 ? degree % 390 : degree % 390 + 390;  
        degree = degree >= 0 ? degree % 359: degree % 359+ 359; 
        if (degree == mTargetDegree) return;  
  
        mTargetDegree = degree;  
        if (mEnableAnimation) {  
            mStartDegree = mCurrentDegree;  
            mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();    
            int diff = mTargetDegree - mCurrentDegree;    
            mClockwise = diff >= 0;  
            mAnimationEndTime = mAnimationStartTime  
                    + Math.abs(diff) * 1000 / ANIMATION_SPEED;  
        } else {  
            mCurrentDegree = mTargetDegree;  
        }  
  
        invalidate();  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        Drawable drawable = getDrawable();  
        if (drawable == null) return;  
  
        Rect bounds = drawable.getBounds();  
        int w = bounds.right - bounds.left;  
        int h = bounds.bottom - bounds.top;  
  
        if (w == 0 || h == 0) return; // nothing to draw   

        /*if (mCurrentDegree != mTargetDegree) {  
            long time = AnimationUtils.currentAnimationTimeMillis();  
            if (time < mAnimationEndTime) {  
                int deltaTime = (int)(time - mAnimationStartTime);  
                int degree = mStartDegree + ANIMATION_SPEED  
                        * (mClockwise ? deltaTime : -deltaTime) / 1000;  
                degree = degree >= 0 ? degree % 360 : degree % 360 + 360;  
                mCurrentDegree = degree;  
                invalidate();  
            } else {  
                mCurrentDegree = mTargetDegree;  
            }  
        }  */
        if(!isStop){
        	long time = AnimationUtils.currentAnimationTimeMillis();  
        	int deltaTime = (int)(time - mAnimationStartTime);  
            int degree = mStartDegree + ANIMATION_SPEED  
                    * (mClockwise ? deltaTime : -deltaTime) / 1000;  
            degree = degree >= 0 ? degree % 360 : degree % 360 + 360;  
            mCurrentDegree = degree;  
            invalidate();  
        }
        int left = getPaddingLeft();  
        int top = getPaddingTop();  
        int right = getPaddingRight();  
        int bottom = getPaddingBottom();  
        int width = getWidth() - left - right;  
        int height = getHeight() - top - bottom;  
  
        int saveCount = canvas.getSaveCount();  
  
        // Scale down the image first if required.   
        if ((getScaleType() == ImageView.ScaleType.FIT_CENTER) &&  
                ((width < w) || (height < h))) {  
            float ratio = Math.min((float) width / w, (float) height / h);  
            canvas.scale(ratio, ratio, width / 2.0f, height / 2.0f);  
        }  
        canvas.translate(left + width / 2, top + height / 2);  
        canvas.rotate(mCurrentDegree);  
        canvas.translate(-w / 2, -h / 2);  
        drawable.draw(canvas);  
        canvas.restoreToCount(saveCount);  
    }  
  
    private Bitmap mThumb;  
    private Drawable[] mThumbs;  
    private TransitionDrawable mThumbTransition;  
  
    public void setBitmap(Bitmap bitmap) {  
        // Make sure uri and original are consistently both null or both   
        // non-null.   
        if (bitmap == null) {  
            mThumb = null;  
            mThumbs = null;  
            setImageDrawable(null);  
            setVisibility(GONE);  
            return;  
        }  
  
        LayoutParams param = getLayoutParams();  
        final int miniThumbWidth = bitmap.getWidth();//getResources().getDimensionPixelSize(R.dimen.air_quality_circle_width);  
        final int miniThumbHeight = bitmap.getHeight();//getResources().getDimensionPixelSize(R.dimen.air_quality_circle_width);            
     
        mThumb = ThumbnailUtils.extractThumbnail(  
                bitmap, miniThumbWidth, miniThumbHeight);  
        Drawable drawable;  
        if (mThumbs == null || !mEnableAnimation) {  
            mThumbs = new Drawable[2];  
            mThumbs[1] = new BitmapDrawable(getContext().getResources(), mThumb);  
            setImageDrawable(mThumbs[1]);  
        } else {  
            mThumbs[0] = mThumbs[1];  
            mThumbs[1] = new BitmapDrawable(getContext().getResources(), mThumb);  
            mThumbTransition = new TransitionDrawable(mThumbs);  
            setImageDrawable(mThumbTransition);  
            mThumbTransition.startTransition(500);  
        }  
        setVisibility(VISIBLE);  
    }  
}
